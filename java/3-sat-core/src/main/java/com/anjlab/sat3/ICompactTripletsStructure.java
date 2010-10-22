package com.anjlab.sat3;

public interface ICompactTripletsStructure extends ITabularFormula
{
    boolean tiersSorted();
    void union(ICompactTripletsStructure cts);
    void intersect(ICompactTripletsStructure cts);
    /**
     * 
     * @param varName
     * @param value
     * @return True if some clauses were removed during concretization.
     */
    boolean concretize(int varName, Value value);
    boolean concretize(ITripletPermutation tripletPermutation, ITripletValue tripletValue);
    /**
     * Runs clearing procedure on this formula.
     * @return True if some clauses were removed during cleanup.
     */
    boolean cleanup();
    Value valueOf(int varName);
}