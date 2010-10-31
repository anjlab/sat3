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
    /**
     * <p>The formula should be 'clean' in (0 ... from) range and (to ... tiers.size()-1) range, 
     * as well as inside (from ... to) range.</p>
     * 
     * <p>Such formulas usually appear as a result of concretization of 'clean' formula.</p>
     * 
     * @param from
     * @param to
     * @return
     */
    CleanupStatus cleanup(int from, int to);
    Value valueOf(int varName);
    void clear();
}