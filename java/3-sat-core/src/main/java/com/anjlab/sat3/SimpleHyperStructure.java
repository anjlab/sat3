package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.function.IntObjectProcedure;
import cern.colt.list.IntArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

import com.anjlab.sat3.ICompactTripletsStructure.CleanupStatus;

public class SimpleHyperStructure implements IHyperStructure
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHyperStructure.class);
    
    private ICompactTripletsStructure basicCTS;
    private ICompactTripletsStructure otherCTS;
    //  List of OpenIntObjectHashMap where Object is IVertex
    private ObjectArrayList tiers; 

    private boolean basicGraphChanged;
    
    public SimpleHyperStructure(ICompactTripletsStructure basicCTS, ICompactTripletsStructure otherCTS)
    {
        this.basicCTS = basicCTS;
        this.otherCTS = otherCTS;
        
        this.tiers = new ObjectArrayList();
    }
    public ICompactTripletsStructure getBasicCTS()
    {
        return basicCTS;
    }
    public ICompactTripletsStructure getOtherCTS()
    {
        return otherCTS;
    }
    public ObjectArrayList getTiers()
    {
        return tiers;
    }
    public void addVertex(int tierSize, IVertex vertex)
    {
        int tierIndex = vertex.getTierIndex();
        OpenIntObjectHashMap edges;
        if (tierIndex == tiers.size())
        {
            edges = new OpenIntObjectHashMap(tierSize);
            tiers.add(edges);
        }
        else
        {
            edges = (OpenIntObjectHashMap) tiers.get(tierIndex);
        }
        edges.put(vertex.getTripletValue().getTierKey(), vertex);
        
        ((SimpleVertex)vertex).setHyperStructure(this);
    }
    public void build()
    {
        createFirstTier();
        
        //  List of ITier
        ObjectArrayList basicTiers = basicCTS.getTiers();
        
        for (int j = 1; j < basicTiers.size(); j++)
        {
            LOGGER.info("Building HSS tier #{} of {}", j+1, basicTiers.size());
            
            final int nextTierIndex = j;
            final ITier basicPrevTier = (ITier) basicTiers.get(nextTierIndex - 1);
            final ITier basicNextTier = (ITier) basicTiers.get(nextTierIndex);
            
            OpenIntObjectHashMap basicPrevTierVertices = (OpenIntObjectHashMap) getTiers().get(nextTierIndex - 1);
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("HSS   tier #{} is: {}", nextTierIndex, verticesTripletsToString(basicPrevTierVertices));
                LOGGER.debug("Basic tier #{} is: {}", nextTierIndex, tripletsToString(basicPrevTier));
                LOGGER.debug("Basic tier #{} is: {}", nextTierIndex + 1, tripletsToString(basicNextTier));
            }
            
            if (basicPrevTier.size() != basicPrevTierVertices.size())
            {
                throw new AssertionError("BG and HSS should be isomorphic");
            }
            
            //  Shift each vertex of the tier along associated edges to the next tier
            
            IntArrayList keys = basicPrevTierVertices.keys();
            for (int k = 0; k < keys.size(); k++)
            {
                int vertexTierKey = keys.get(k);

                IVertex prevTierVertex = (IVertex) ((OpenIntObjectHashMap) getTiers().get(nextTierIndex - 1)).get(vertexTierKey);

                ITripletValue tripletValue = prevTierVertex.getTripletValue();

                ITripletValue adjoinTarget = tripletValue.getAdjoinRightTarget1();
                if (basicNextTier.contains(adjoinTarget))
                {
                    //  calculate substructure-edge for target edge 1
                    createOrUpdateNextTierVertexInHSS(nextTierIndex, basicNextTier, vertexTierKey, adjoinTarget);
                }
                adjoinTarget = tripletValue.getAdjoinRightTarget2();
                if (basicNextTier.contains(adjoinTarget))
                {
                    //  calculate substructure-edge for target edge 2
                    createOrUpdateNextTierVertexInHSS(nextTierIndex, basicNextTier, vertexTierKey, adjoinTarget);
                }
            }
            
            unifyCoincidentSubstructuresOfATier(nextTierIndex);

            //  Check for dirty vertices
            int dirtyTiersCount = countDirtyTiers();
            if (dirtyTiersCount > 0)
            {
                LOGGER.debug("Remove last {} tier(s) of the HSS and rebuild them", dirtyTiersCount);
                int indexOfLastTier = getTiers().size() - 1;
                getTiers().removeFromTo(indexOfLastTier - (dirtyTiersCount - 1), indexOfLastTier);

                j -= dirtyTiersCount;
                
                if (j < 0)
                {
                    j = 0;
                    createFirstTier();
                }
            }
            else
            {
                if (Helper.EnableAssertions)
                {
                    assertIntersectionOfTierSubstructuresIsEmpty(nextTierIndex);
                }
            }
        }
    }
    

    private void createFirstTier()
    {
        ObjectArrayList basicTiers = basicCTS.getTiers();
        ITier firstBasicTier = (ITier) basicTiers.get(0);
        
        LOGGER.info("Building HSS tier #1 of {}", basicTiers.size());
        
        tryAddFirstTierVertex(firstBasicTier, _000_instance);
        tryAddFirstTierVertex(firstBasicTier, _001_instance);
        tryAddFirstTierVertex(firstBasicTier, _010_instance);
        tryAddFirstTierVertex(firstBasicTier, _011_instance);
        tryAddFirstTierVertex(firstBasicTier, _100_instance);
        tryAddFirstTierVertex(firstBasicTier, _101_instance);
        tryAddFirstTierVertex(firstBasicTier, _110_instance);
        tryAddFirstTierVertex(firstBasicTier, _111_instance);
        
        unifyCoincidentSubstructuresOfATier(0);
        
        if (Helper.EnableAssertions)
        {
            assertIntersectionOfTierSubstructuresIsEmpty(0);
        }
    }

    private void tryAddFirstTierVertex(ITier firstBasicTier, ITripletValue tripletValue) 
        throws EmptyStructureException
    {
        if (firstBasicTier.contains(tripletValue))
        {
            ICompactTripletsStructure clone = (ICompactTripletsStructure) otherCTS.clone();
            
            clone.concretize(firstBasicTier, tripletValue);
            
            addVertex(firstBasicTier.size(), new SimpleVertex(firstBasicTier, 0, tripletValue, clone));
        }
    }

    private void assertIntersectionOfTierSubstructuresIsEmpty(
            final int tierIndex)
            throws AssertionError
    {
        OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) getTiers().get(tierIndex);
        if (tierVertices.size() == 1)
        {
            return;
        }
        ICompactTripletsStructure intersection = Helper.intersectAll(tierVertices.values());
        if (!intersection.isEmpty())
        {
            Helper.prettyPrint(intersection);
            throw new AssertionError("By the construction rules, intersection of substructure-vertices of a single tier should be an empty substructure");
        }
    }

    private void createOrUpdateNextTierVertexInHSS(final int nextTierIndex,
            final ITier basicNextTier,
            int vertexTierKey, ITripletValue adjoinTarget)
    {
        ICompactTripletsStructure substructureEdge = concordantShift(
                nextTierIndex, vertexTierKey, basicNextTier.getCName(),
                adjoinTarget.isNotC() ? Value.AllNegative : Value.AllPlain);

        OpenIntObjectHashMap prevTierVertices = (OpenIntObjectHashMap) getTiers().get(nextTierIndex - 1);
        IVertex prevTierVertex = (IVertex) prevTierVertices.get(vertexTierKey);
        
        if (substructureEdge.isEmpty())
        {
            //  Mark all vertices of HSS tierIndex with empty edges
            prevTierVertex.foundEmptyEdge();
        }
        
        OpenIntObjectHashMap tierVertices = null;
        IVertex existingVertex = null;
        
        if (nextTierIndex < getTiers().size()) 
        {
            tierVertices = (OpenIntObjectHashMap) getTiers().get(nextTierIndex);
            existingVertex = (IVertex) tierVertices.get(adjoinTarget.getTierKey());
        }
        
        //  If the vertex is already on the next tier...
        if (existingVertex != null)
        {
            //  ... unite substructure-edge width substructure-vertex 
            //  and replace target substructure-vertex with resulting substructure
            
            existingVertex.getCTS().union(substructureEdge);
        }
        else
        {
            //  put substructure-edge to substructure-vertex as is
            addVertex(basicNextTier.size(), new SimpleVertex(basicNextTier, nextTierIndex, adjoinTarget, substructureEdge));
        }
    }

    /**
     * 
     * @param hss
     * @param vertexTierKey
     * @param cName
     * @param cValue
     * @return ICompactTripletsStructure
     */
    private ICompactTripletsStructure concordantShift(
            int nextTierIndex, int vertexTierKey, int cName, Value cValue)
    {
        //  Concretization
        
        IVertex vertexToShift = ((IVertex) ((OpenIntObjectHashMap) getTiers().get(nextTierIndex - 1)).get(vertexTierKey));
        //  Work with a copy of substructure-vertex to keep original substructure the same
        ICompactTripletsStructure substructureEdge = (ICompactTripletsStructure) vertexToShift.getCTS().clone();
        substructureEdge.concretize(cName, cValue);
        if (substructureEdge.isEmpty())
        {
            return substructureEdge;
        }
        
        //  Filtration
        for (int s = 0; s < nextTierIndex - 1; s++)
        {
            //  Intersection
            int sTierSize = ((OpenIntObjectHashMap) getTiers().get(s)).size();
            
            OpenIntObjectHashMap sTierVertices = (OpenIntObjectHashMap) getTiers().get(s);
            ObjectArrayList intersections = new ObjectArrayList();
            
            for (int sv = 0; sv < sTierSize; sv++)
            {
                IVertex sTierVertex = (IVertex) sTierVertices.values().get(sv); 
                ICompactTripletsStructure clone = (ICompactTripletsStructure) substructureEdge.clone();
                clone.intersect(sTierVertex.getCTS());
                intersections.add(clone);
            };
            
            //  Union
            substructureEdge = (ICompactTripletsStructure) intersections.get(0);

            for (int ks = 1; ks < sTierSize; ks++)
            {
                    substructureEdge.union((ICompactTripletsStructure) intersections.get(ks));
            }
        }

        return substructureEdge;
    }

    private int countDirtyTiers()
    {
        int count = 0;
        for (int i = getTiers().size() - 1; i >= 0; i--)
        {
            OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) getTiers().get(i);
            IVertex tierVertex = (IVertex) tierVertices.get(tierVertices.keys().get(0));
            if (tierVertex.isDirty())
            {
                count++;
            }
            else
            {
                break;
            }
        }
        return count;
    }

    /**
     * @param hss List of IHyperStructure
     * @param tierIndex
     */
    private void unifyCoincidentSubstructuresOfATier(final int tierIndex)
    {
        final OpenIntObjectHashMap basicTierVertices = (OpenIntObjectHashMap) getTiers().get(tierIndex);

        IntArrayList keys = basicTierVertices.keys();
        
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Tier #{} of HSS contained {} vertices before unification: {}", 
                    new Object[] { tierIndex + 1, basicTierVertices.size(), verticesTripletsToString(basicTierVertices) });
        }

        for (int j = 0; j < keys.size(); j++)
        {
            int vertexTierKey = keys.get(j);
            
            IVertex vertex = (IVertex) ((OpenIntObjectHashMap) getTiers().get(tierIndex)).get(vertexTierKey);
            
            try
            {
                //  If we have any empty substructure-vertex in vertices
                //  we should remove this vertex from the basic graph
                if (vertex.getCTS().isEmpty())
                {
                    throw new EmptyStructureException(vertex.getCTS());
                }
            }
            catch (EmptyStructureException e)
            {
                LOGGER.info("Found empty substructure-vertex assigned to vertex {} of tier #{}", vertex.getTripletValue(), tierIndex + 1);
                
                //  Remove vertex with empty substructure from HSS and BG
                OpenIntObjectHashMap hsTierVertices = (OpenIntObjectHashMap) getTiers().get(tierIndex);

                hsTierVertices.removeKey(vertexTierKey);
                LOGGER.debug("Vertex {} removed from tier #{} of HS: {}", 
                        new Object[] { vertex.getTripletValue(), tierIndex + 1, verticesTripletsToString(hsTierVertices) });

                ITier basicTier = basicCTS.getTier(tierIndex);
                basicTier.remove(vertex.getTripletValue());
                LOGGER.debug("Coincident vertex {} removed from tier #{} of the BG: {}", 
                        new Object[] { vertex.getTripletValue(), tierIndex + 1, tripletsToString(basicTier) });
                
                LOGGER.debug("Executing cleaup procedure on the basic structure...");
                LOGGER.debug("Basic structure before cleanup:");
                Helper.prettyPrint(basicCTS);
                CleanupStatus status = basicCTS.cleanup(tierIndex, tierIndex);
                LOGGER.debug("Basic structure after cleanup:");
                Helper.prettyPrint(basicCTS);
                
                if (basicCTS.isEmpty())
                {
                    throw new EmptyStructureException(basicCTS);
                }
                
                if (status.someClausesRemoved)
                {
                    basicGraphChanged = true;
                }
                
                int deep = status.someClausesRemoved ? tierIndex - status.from : 0;
                
                LOGGER.debug("{} vertices were removed from BG incuding some vertices in {} tier(s) above", status.numberOfClausesRemoved, deep);
                if (deep > 0)
                {
                    markDirty(tierIndex, deep);
                }
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Tier #{} of HSS contained {} vertices after unification: {}", 
                    new Object[] { tierIndex + 1, basicTierVertices.size(), verticesTripletsToString(basicTierVertices) });
        }
    }

    private void markDirty(int tierIndex, int deep)
    {
        //  Mark substructure-vertices of the above (deep + 2) tiers dirty
        for (int d = 0; d < deep + 2; d++)
        {
            int dirtyTierIndex = tierIndex - d;
            if (dirtyTierIndex < 0)
            {
                break;
            }
            OpenIntObjectHashMap dirtyTierVertices = (OpenIntObjectHashMap) getTiers().get(dirtyTierIndex);
            dirtyTierVertices.forEachPair(new IntObjectProcedure()
            {
                public boolean apply(int key, Object value)
                {
                    ((IVertex) value).markDirty();
                    return true;
                }
            });
        }
    }

    private static StringBuilder tripletsToString(ITier tier)
    {
        final StringBuilder builder = new StringBuilder();
        appendToBuilder(tier, builder, _000_instance);
        appendToBuilder(tier, builder, _001_instance);
        appendToBuilder(tier, builder, _010_instance);
        appendToBuilder(tier, builder, _011_instance);
        appendToBuilder(tier, builder, _100_instance);
        appendToBuilder(tier, builder, _101_instance);
        appendToBuilder(tier, builder, _110_instance);
        appendToBuilder(tier, builder, _111_instance);
        return builder;
    }

    private static void appendToBuilder(final ITier tier, final StringBuilder builder, ITripletValue tripletValue)
    {
        if (tier.contains(tripletValue)) 
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }
            builder.append(tripletValue);
        }
    }

    private static StringBuilder verticesTripletsToString(final OpenIntObjectHashMap tierVertices)
    {
        final StringBuilder builder = new StringBuilder();
        appendToBuilder(tierVertices, builder, _000_instance);
        appendToBuilder(tierVertices, builder, _001_instance);
        appendToBuilder(tierVertices, builder, _010_instance);
        appendToBuilder(tierVertices, builder, _011_instance);
        appendToBuilder(tierVertices, builder, _100_instance);
        appendToBuilder(tierVertices, builder, _101_instance);
        appendToBuilder(tierVertices, builder, _110_instance);
        appendToBuilder(tierVertices, builder, _111_instance);
        return builder;
    }

    private static void appendToBuilder(final OpenIntObjectHashMap tierVertices, final StringBuilder builder, ITripletValue tripletValue)
    {
        if (tierVertices.containsKey(tripletValue.getTierKey())) 
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }
            builder.append(tripletValue);
        }
    }

    public boolean isBasicGraphChangedDuringBuild()
    {
        return basicGraphChanged;
    }
}
