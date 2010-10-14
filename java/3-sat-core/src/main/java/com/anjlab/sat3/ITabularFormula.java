package com.anjlab.sat3;

public interface ITabularFormula
{
    int getVarCount();
    /**
     * Use this method only if performance is not a goal.
     */
    int getClausesCount();
    GenericArrayList<ITier> getTiers();
    ITier findTierFor(ITripletPermutation tripletPermutation);
    void add(ITriplet triplet);
    void unionOrAdd(ITier tier);
    IPermutation getPermutation();
    boolean isEmpty();
    void complete(IPermutation variables) throws EmptyStructureException;
    GenericArrayList<ITier> findTiersFor(int varName);
    GenericArrayList<ITier> findTiersFor(int varName1, int varName2);
    void sortTiers();
}
