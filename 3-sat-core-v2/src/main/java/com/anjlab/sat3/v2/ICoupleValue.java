package com.anjlab.sat3.v2;

public interface ICoupleValue
{
    boolean isNotA();
    boolean isNotB();
    
    byte getTierKey();
    
    ICoupleValue getAdjoinRightTarget1();
    ICoupleValue getAdjoinRightTarget2();
    ICoupleValue getAdjoinLeftSource1();
    ICoupleValue getAdjoinLeftSource2();
    
    ICoupleValue cloneWithInversedA();
    ICoupleValue cloneWithInversedB();
}
