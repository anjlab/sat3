package com.anjlab.sat3.v2;

import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public interface ICompactCouplesStructure
{
    IPermutation getPermutation();
    
    ObjectArrayList getTiers();
    
    ITier2 getTier(int index);
    
    /**
     * 
     * @param tierIndex1
     * @param tierIndex2
     * @return List of all {@link Tuple}s representing
     *         inadmissable concatenations between the two tiers.
     */
    ObjectArrayList getTwinTuples(int tierIndex1, int tierIndex2);
    
    boolean isEmpty();
    
    int getClausesCount();
    
    void inverse(int varName);

    Value valueOf(int varName);

    ICompactCouplesStructure clone();

    boolean trySet(int varName, Value value, IColumnsInversionControl cic);
}
