package com.anjlab.sat3;

public interface ICompactTripletsStructure extends ITabularFormula
{
    ICompactTripletsStructure union(ICompactTripletsStructure cts);
    ICompactTripletsStructure intersect(ICompactTripletsStructure cts);
    ICompactTripletsStructure concretize(int varName, boolean value);
    /**
     * Runs clearing procedure on this formula.
     */
    void cleanup();
    void subtract(ITabularFormula formula);
}