package com.anjlab.sat3;

import cern.colt.map.OpenIntObjectHashMap;

/**
 * This class and constants declared here are for convenience only, they used in tests.
 * 
 * One place this factory used is {@link ITier#iterator()}.
 * 
 * The iterator() method is also there only for convenience, 
 * it is used in {@link Helper#prettyPrint(ITabularFormula)} and 
 * {@link Helper#saveToDIMACSFileFormat(ITabularFormula, String)} only.
 * 
 * All other usages of {@link ITier#iterator()} is a subject to remove 
 * in ongoing improvements.
 * 
 * @author dmitrygusev
 *
 */
public class SimpleTripletValueFactory {

    public static final ITripletValue _000_instance = new _000();
    public static final ITripletValue _001_instance = new _001();
    public static final ITripletValue _010_instance = new _010();
    public static final ITripletValue _011_instance = new _011();
    public static final ITripletValue _100_instance = new _100();
    public static final ITripletValue _101_instance = new _101();
    public static final ITripletValue _110_instance = new _110();
    public static final ITripletValue _111_instance = new _111();

    private static final OpenIntObjectHashMap values = new OpenIntObjectHashMap(8 * 2 + 1);

    static
    { 
        values.put(_000_instance.getTierKey(), _000_instance);    //    1
        values.put(_001_instance.getTierKey(), _001_instance);    //    2
        values.put(_010_instance.getTierKey(), _010_instance);    //    4
        values.put(_011_instance.getTierKey(), _011_instance);    //    8
        values.put(_100_instance.getTierKey(), _100_instance);    //    16
        values.put(_101_instance.getTierKey(), _101_instance);    //    32
        values.put(_110_instance.getTierKey(), _110_instance);    //    64
        values.put(_111_instance.getTierKey(), _111_instance);    //    128
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
        
        return (ITripletValue) values.get(key);
    }

    public static ITripletValue getTripletValue(int tierKey) {
        ITripletValue tripletValue = (ITripletValue) values.get(tierKey);
        return tripletValue;
    }
    
    private static class _000 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 1; }
    }

    private static class _001 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 2; }
    }

    private static class _010 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 4; }
    }

    private static class _011 implements ITripletValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 8; }
    }

    private static class _100 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 16; }
    }

    private static class _101 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return false; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return 32; }
    }

    private static class _110 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return false; }
        public byte getTierKey() { return 64; }
    }

    private static class _111 implements ITripletValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return true; }
        public boolean isNotC() { return true; }
        public byte getTierKey() { return -128; }
    }

}
