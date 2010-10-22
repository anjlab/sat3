package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

public class SimpleHyperStructure implements IHyperStructure
{
    private ICompactTripletsStructure basicCTS;
    private ICompactTripletsStructure otherCTS;
    //  List of OpenIntObjectHashMap
    private ObjectArrayList tiers; 
    
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
    public IEdge addFirstTierEdge(int tierSize, IVertex source)
    {
        int tierIndex = source.getTierIndex();
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
        SimpleEdge edge = new SimpleEdge(source);
        edges.put(source.getTripletValue().getTierKey(), edge);
        return edge;
    }
    public IEdge addNextEdge(IEdge prevEdge, int targetTierSize, IVertex prevEdgeTarget)
    {
        IEdge nextEdge = addFirstTierEdge(targetTierSize, prevEdgeTarget);
        
        if (prevEdge.getSource().getTripletValue().getAdjoinRightTarget1() == prevEdgeTarget.getTripletValue())
        {
            prevEdge.setNext1(nextEdge);
        }
        else
        {
            prevEdge.setNext2(nextEdge);
        }
        //  TODO Set previous2
        nextEdge.setPrevious1(prevEdge);
        return nextEdge;
    }
}
