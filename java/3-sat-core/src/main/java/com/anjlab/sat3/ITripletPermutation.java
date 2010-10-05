package com.anjlab.sat3;

public interface ITripletPermutation
{

    /**
     * One-based index of first variable in the permutation.
     */
    int getAName();
    int getBName();
    int getCName();
    
    int[] getCanonicalName();

    long canonicalHashCode();

    boolean hasSameVariablesAs(ITripletPermutation permutation);
    boolean hasVariable(int varName);
    
    void transposeTo(ITripletPermutation targetPermutation);

    void swapAB();
    void swapAC();
    void swapBC();

}
