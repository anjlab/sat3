package com.anjlab.sat3.v2;

import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public interface ISystem
{
    IColumnsInversionControl getCIC();
    
    ObjectArrayList getCCSs();
    
    Value valueOf(int varName);

    int getStartingVarName();

    ISystem clone();

    boolean trySet(int varName, Value value);

    void fixConstantValues();

    ObjectArrayList getCICs();
}
