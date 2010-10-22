package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;

public interface ITabularFormula
{
    int getVarCount();
    /**
     * Use this method only if performance is not a goal.
     */
    int getClausesCount();
    /**
     * @return List of ITier
     */
    ObjectArrayList getTiers();
    ITier getTier(int tierIndex);
    ITier findTierFor(ITripletPermutation tripletPermutation);
    void add(ITriplet triplet);
    void unionOrAdd(ITier tier);
    IPermutation getPermutation();
    boolean isEmpty();
    void complete(IPermutation variables) throws EmptyStructureException;
    /**
     * @return List of ITier
     */
    ObjectArrayList findTiersFor(int varName);
    /**
     * @return List of ITier
     */
    ObjectArrayList findTiersFor(int varName1, int varName2);
    void sortTiers();
    ITabularFormula clone();
}
