package com.anjlab.sat3;

public interface ITriplet extends ITripletPermutation, ITripletValue
{
    /// <summary>
    /// Value <code>true</code> means the variable is with logical NOT.
    /// </summary>
    boolean isNotA();
    boolean isNotB();
    boolean isNotC();

    void transposeTo(ITripletPermutation targetPermutation);
}