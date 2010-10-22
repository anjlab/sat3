package com.anjlab.sat3;

public interface ITripletValue
{
    boolean isNotA();
    boolean isNotB();
    boolean isNotC();
    
    /**
     * There are exist 8 different triplet values: 000, 001, 010, 011, 100, 101, 110 and 111. 
     * It is possible to associate these values with 8 different numbers (byte should be enough for this).
     * 
     * @return Unique number associated with this triplet value.
     */
    byte getTierKey();
    
    ITripletValue getAdjoinRightTarget1();
    ITripletValue getAdjoinRightTarget2();
}