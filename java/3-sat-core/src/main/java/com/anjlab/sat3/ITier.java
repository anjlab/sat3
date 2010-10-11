package com.anjlab.sat3;

public interface ITier extends Iterable<ITripletValue>, ITripletPermutation
{
    void add(ITripletValue triplet);

    int size();

    boolean contains(ITripletValue triplet);

    void remove(ITripletValue triplet);

    void subtract(ITier tier);

    void adjoinLeft(ITier tier);

    void adjoinRight(ITier tier);

    boolean isEmpty();

    void intersect(ITier tier);

    void union(ITier tier);

    void concretize(int varName, Value value);

    ITier clone();

    Value valueOfA();
    Value valueOfB();
    Value valueOfC();

    ITabularFormula getFormula();
}
