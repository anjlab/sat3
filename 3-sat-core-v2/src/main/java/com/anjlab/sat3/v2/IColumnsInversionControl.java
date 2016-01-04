package com.anjlab.sat3.v2;

import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.Value;

public interface IColumnsInversionControl
{
    IPermutation getPermutation();

    boolean setNegative(int varName);

    boolean setPlain(int varName);

    boolean isFixed(int varName);
    
    boolean isNotFixed(int varName);

    IColumnsInversionControl clone();

    void setFree(int varName);

    boolean isConflicted(int varName);

    boolean isFixedNegative(int varName);

    boolean isFixedPlain(int varName);

    boolean isFree(int varName);

    Value getFixedValue(int varName);

    void setConflicted(int varName);
}
