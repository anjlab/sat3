package com.anjlab.sat3;

public interface IVertex extends ICompactTripletsStructureHolder
{
    ITripletValue getTripletValue();

    ITripletPermutation getPermutation();

    int getTierIndex();
    
    IVertex getBottomVertex1();
    IVertex getBottomVertex2();

    void foundEmptyEdge(EdgeKind edge);
    boolean hasEmptyBottomEdge();

    void markDirty();
    boolean isDirty();

    boolean bothEdgesAreEmpty();
}
