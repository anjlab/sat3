package com.anjlab.sat3;

public interface ITripletPermutation
{

    /**
     * One-based index of first variable in the permutation.
     */
    int getAName();
    int getBName();
    int getCName();
    
    int[] getABC();
    
    int[] getCanonicalName();

    long canonicalHashCode();

    boolean hasSameVariablesAs(ITripletPermutation permutation);
    boolean hasVariable(int varName);
    
    void transposeTo(ITripletPermutation targetPermutation);
    void transposeTo(int targetA, int targetB, int targetC);
    void transposeTo(int[] abc);

    void swapAB();
    void swapAC();
    void swapBC();

}
