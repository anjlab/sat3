package com.anjlab.sat3.v2;

import com.anjlab.sat3.Value;

public interface ITier2 extends Iterable<ICoupleValue>, Cloneable
{
    int size();

    boolean contains(ICoupleValue couple);

    void inverseA();

    void inverseB();

    void add(ICoupleValue couple);

    Value valueOfA();

    Value valueOfB();

    void remove(ICoupleValue couple);

    ITier2 clone();

    int keysOfA(Value value);

    int keysOfB(Value value);
}
