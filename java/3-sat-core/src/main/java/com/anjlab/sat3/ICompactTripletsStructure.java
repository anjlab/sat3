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
    public static class CleanupStatus
    {
        public final boolean someClausesRemoved;
        public final int from;
        public final int to;
        public final int numberOfClausesRemoved;
        
        public CleanupStatus(boolean someClausesRemoved, int from, int to, int numberOfClausesRemoved)
        {
            this.someClausesRemoved = someClausesRemoved;
            this.from = from;
            this.to = to;
            this.numberOfClausesRemoved = numberOfClausesRemoved;
        }
    }
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
}