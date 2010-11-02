package com.anjlab.sat3;

import java.util.Properties;

import cern.colt.list.ObjectArrayList;

public interface ITabularFormula
{
    int getVarCount();
    /**
     * Use this method only if performance is not a goal.
     */
    int getClausesCount();
    /**
     * @return List of {@link ITier}
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
     * @return List of {@link ITier}
     */
    ObjectArrayList findTiersFor(int varName);
    /**
     * @return List of {@link ITier}
     */
    ObjectArrayList findTiersFor(int varName1, int varName2);
    void sortTiers();
    ITabularFormula clone();
    
    /**
     * 
     * @param route List of {@link IVertex} forming HSS route. 
     * Contains inverse triplet values of variables. 
     * @return
     */
    boolean evaluate(ObjectArrayList route);
    boolean evaluate(Properties properties);
    boolean containsAllValuesOf(ITier tier);
}
