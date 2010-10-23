package com.anjlab.sat3;

import cern.colt.map.OpenIntObjectHashMap;

public class SimpleVertex implements IVertex
{
    private ITripletPermutation permutation;
    private ITripletValue tripletValue;
    private ICompactTripletsStructure structure;
    private int tierIndex;
    private IHyperStructure hyperStructure;
    private boolean hasEmptyEdge;
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
    public boolean hasEmptyEdge()
    {
        return hasEmptyEdge;
    }
    public void foundEmptyEdge()
    {
        hasEmptyEdge = true;
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
        return structure.toString();
    }
    public IVertex getBottomVertex1()
    {
        return tierIndex < hyperStructure.getTiers().size() - 1 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget1().getTierKey())
             : null; 
    }
    public IVertex getBottomVertex2()
    {
        return tierIndex < hyperStructure.getTiers().size() - 1 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget2().getTierKey())
             : null; 
    }
    public IVertex getTopVertex1()
    {
        return tierIndex > 0 
            ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex - 1)).get(tripletValue.getAdjoinLeftSource1().getTierKey())
            : null;
    }
    public IVertex getTopVertex2()
    {
        return tierIndex > 0 
        ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex - 1)).get(tripletValue.getAdjoinLeftSource2().getTierKey())
        : null;
    }
}
