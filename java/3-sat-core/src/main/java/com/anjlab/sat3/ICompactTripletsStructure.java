package com.anjlab.sat3;

public interface ICompactTripletsStructure extends ITabularFormula
{
    ICompactTripletsStructure union(ICompactTripletsStructure cts);
    ICompactTripletsStructure intersect(ICompactTripletsStructure cts);
    /**
     * 
     * @param varName
     * @param value
     * @return True if some clauses were removed during concretization.
     */
    boolean concretize(int varName, Value value);
    /**
     * Runs clearing procedure on this formula.
     * @return True if some clauses were removed during cleanup.
     */
    boolean cleanup();
    void subtract(ITabularFormula formula);
    Value valueOf(int varName);
}