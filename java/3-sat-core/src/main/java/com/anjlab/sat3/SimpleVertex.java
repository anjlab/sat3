package com.anjlab.sat3;

public class SimpleVertex implements IVertex
{
    private ITripletPermutation permutation;
    private ITripletValue tripletValue;
    private ICompactTripletsStructure structure;
    private int tierIndex;
    
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

    @Override
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
}
