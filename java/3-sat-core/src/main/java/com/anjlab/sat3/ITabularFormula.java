package com.anjlab.sat3;

public interface ITabularFormula
{
    int getVarCount();
    /**
     * Use this method only if performance is not a goal.
     */
    int getClausesCount();
    GenericArrayList<ITier> getTiers();
    boolean tiersSorted();
    void sortTiers();
    void add(ITriplet triplet);
    IPermutation getPermutation();
    void applyJoin(JoinInfo joinInfo, ITier tier);
    boolean isEmpty();
}
