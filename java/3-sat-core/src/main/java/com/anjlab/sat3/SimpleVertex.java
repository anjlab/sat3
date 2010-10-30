package com.anjlab.sat3;

import cern.colt.map.OpenIntObjectHashMap;

public class SimpleVertex implements IVertex
{
    private ITripletPermutation permutation;
    private ITripletValue tripletValue;
    private ICompactTripletsStructure structure;
    private int tierIndex;
    private IHyperStructure hyperStructure;
    private boolean bottom1Empty;
    private boolean bottom2Empty;
    private boolean dirty;
    
    /**
     * 
     * @param tripletPermutation
     * @param tierIndex
     * @param tripletValue
     * @param structure Assigned substructure-vertex
     */
    public SimpleVertex(ITripletPermutation tripletPermutation, 
                        int tierIndex, ITripletValue tripletValue,
                        ICompactTripletsStructure structure)
    {
        this.permutation = tripletPermutation;
        this.tripletValue = tripletValue;
        this.structure = structure;
        this.tierIndex = tierIndex;
    }
    public void markDirty()
    {
        dirty = true;
    }
    public boolean isDirty()
    {
        return dirty;
    }
    public boolean hasEmptyBottomEdge()
    {
        return bottom1Empty || bottom2Empty;
    }
    public void foundEmptyEdge(EdgeKind edgeKind)
    {
        if (edgeKind == EdgeKind.Bottom1)
        {
            bottom1Empty = true;
        }
        else
        {
            bottom2Empty = true;
        }
    }
    void setHyperStructure(IHyperStructure hyperStructure)
    {
        this.hyperStructure = hyperStructure;
    }
    public ICompactTripletsStructure getCTS()
    {
        return structure;
    }
    
    public ITripletPermutation getPermutation()
    {
        return permutation;
    }
    public ITripletValue getTripletValue()
    {
        return tripletValue;
    }
    public int getTierIndex()
    {
        return tierIndex;
    }
    public String toString()
    {
        return (isDirty() ? "dirty " : "")
             + permutation.getAName()
             + ","
             + permutation.getBName()
             + ","
             + permutation.getCName()
             + " -> "
             + tripletValue.toString()
             + "\n"
             + structure.toString();
    }
    public boolean bothEdgesAreEmpty()
    {
        return getBottomVertex1() == null && getBottomVertex2() == null;
    }
    public IVertex getBottomVertex1()
    {
        return (!bottom1Empty) && (tierIndex < hyperStructure.getTiers().size() - 1) 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget1().getTierKey())
             : null; 
    }
    public IVertex getBottomVertex2()
    {
        return (!bottom2Empty) && (tierIndex < hyperStructure.getTiers().size() - 1) 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget2().getTierKey())
             : null; 
    }
}
