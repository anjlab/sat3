package com.anjlab.sat3;


public interface ITabularFormula
{
    int getVarCount();
    int getTermCount();
    GenericArrayList<ITier> getTiers();
    boolean tiersSorted();
    void sortTiers();
    void add(ITriplet triplet);
    IPermutation getPermutation();
    void applyJoin(JoinInfo joinInfo, ITier tier);
    boolean isEmpty();
}
