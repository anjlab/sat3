/*
 * Copyright (c) 2010 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

public final class SimpleTripletValueFactory {

    public static final ITripletValue _000_instance = new _000();
    public static final ITripletValue _001_instance = new _001();
    public static final ITripletValue _010_instance = new _010();
    public static final ITripletValue _011_instance = new _011();
    public static final ITripletValue _100_instance = new _100();
    public static final ITripletValue _101_instance = new _101();
    public static final ITripletValue _110_instance = new _110();
    public static final ITripletValue _111_instance = new _111();

    private static final ITripletValue[] values = new ITripletValue[128];

    static
    { 
        values[(byte)(_000_instance.getTierKey() - 1)] = _000_instance;    //    1
        values[(byte)(_001_instance.getTierKey() - 1)] = _001_instance;    //    2
        values[(byte)(_010_instance.getTierKey() - 1)] = _010_instance;    //    4
        values[(byte)(_011_instance.getTierKey() - 1)] = _011_instance;    //    8
        values[(byte)(_100_instance.getTierKey() - 1)] = _100_instance;    //    16
        values[(byte)(_101_instance.getTierKey() - 1)] = _101_instance;    //    32
        values[(byte)(_110_instance.getTierKey() - 1)] = _110_instance;    //    64
        values[(byte)(_111_instance.getTierKey() - 1)] = _111_instance;    //    128
    }
    
    public static ITripletValue getTripletValue(int a, int b, int c)
    {
//        int key = ((a > 0 ? 0 : 1))         //    Computing key this way will give numbering 0 4 2 6 1 5 3 7
//                + ((b > 0 ? 0 : 1) << 1)    //    which coincide with the tier numbering 1 2 4 8 16 32 64 128
//                + ((c > 0 ? 0 : 1) << 2);
        
        //  Note: This should be the same as in constructor of SimpleTriplet
        
        int key = 1;
        
        if (a < 0) key <<= 4;
        if (b < 0) key <<= 2;
        if (c < 0) key <<= 1;
        
        return values[(byte)(key - 1)];
    }

    public static ITripletValue getTripletValue(int tierKey) {
        ITripletValue tripletValue = (ITripletValue) values[(byte)(tierKey - 1)];
        return tripletValue;
    }
    
    private static class _000 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 1; }
        public String toString() { return "000"; }
        public ITripletValue getAdjoinRightTarget1() { return _000_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _001_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _000_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _100_instance; }
    }

    private static class _001 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 2; }
        public String toString() { return "001"; }
        public ITripletValue getAdjoinRightTarget1() { return _010_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _011_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _000_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _100_instance; }
    }

    private static class _010 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 4; }
        public String toString() { return "010"; }
        public ITripletValue getAdjoinRightTarget1() { return _100_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _101_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _001_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _101_instance; }
    }

    private static class _011 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 8; }
        public String toString() { return "011"; }
        public ITripletValue getAdjoinRightTarget1() { return _110_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _111_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _001_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _101_instance; }
    }

    private static class _100 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 16; }
        public String toString() { return "100"; }
        public ITripletValue getAdjoinRightTarget1() { return _000_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _001_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _010_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _110_instance; }
    }

    private static class _101 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 32; }
        public String toString() { return "101"; }
        public ITripletValue getAdjoinRightTarget1() { return _010_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _011_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _010_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _110_instance; }
    }

    private static class _110 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 64; }
        public String toString() { return "110"; }
        public ITripletValue getAdjoinRightTarget1() { return _100_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _101_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _011_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _111_instance; }
    }

    private static class _111 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return -128; }
        public String toString() { return "111"; }
        public ITripletValue getAdjoinRightTarget1() { return _110_instance; }
        public ITripletValue getAdjoinRightTarget2() { return _111_instance; }
        public ITripletValue getAdjoinLeftSource1() { return _011_instance; }
        public ITripletValue getAdjoinLeftSource2() { return _111_instance; }
    }

}
