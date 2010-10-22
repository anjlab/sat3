package com.anjlab.sat3;

public interface IVertex extends ICompactTripletsStructureHolder
{
    ITripletValue getTripletValue();

    ITripletPermutation getPermutation();

    int getTierIndex();
    
    IVertex getBottomVertex1();
    IVertex getBottomVertex2();
    IVertex getTopVertex1();
    IVertex getTopVertex2();
}
