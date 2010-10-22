package com.anjlab.sat3;

public interface IVertex extends ICompactTripletsStructureHolder
{
    ITripletValue getTripletValue();

    ITripletPermutation getPermutation();

    int getTierIndex();
}
