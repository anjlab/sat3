package com.anjlab.sat3.v2;

public class SimpleCoupleValueFactory
{
    public static final ICoupleValue _00_instance = new _00();
    public static final ICoupleValue _01_instance = new _01();
    public static final ICoupleValue _10_instance = new _10();
    public static final ICoupleValue _11_instance = new _11();

    private static final ICoupleValue[] values = new ICoupleValue[128];

    static
    { 
        values[(byte)(_00_instance.getTierKey() - 1)] = _00_instance;    //    1
        values[(byte)(_01_instance.getTierKey() - 1)] = _01_instance;    //    4
        values[(byte)(_10_instance.getTierKey() - 1)] = _10_instance;    //    16
        values[(byte)(_11_instance.getTierKey() - 1)] = _11_instance;    //    64
    }
    
    public static ICoupleValue getCoupleValue(int a, int b)
    {
        return values[(byte)(getTierKey(a, b) - 1)];
    }

    public static int getTierKey(int a, int b)
    {
//        int key = ((a > 0 ? 0 : 1))         //    Computing key this way will give numbering 0 4 2 6 1 5 3 7
//                + ((b > 0 ? 0 : 1) << 1)    //    which coincide with the tier numbering 1 2 4 8 16 32 64 128
//                + ((c > 0 ? 0 : 1) << 2);
        
        //  Note: This should be the same as in other places where similar keys used
        
        int key = 1;
        
        if (a < 0) key <<= 4;
        if (b < 0) key <<= 2;
//        if (c < 0) key <<= 1;
        return key;
    }

    public static ICoupleValue getCoupleValue(int tierKey) {
        ICoupleValue coupleValue = (ICoupleValue) values[(byte)(tierKey - 1)];
        return coupleValue;
    }
    
    public static ICoupleValue getCoupleValue(String coupleString)
    {
        if (coupleString.equals("00")) return _00_instance; else
        if (coupleString.equals("01")) return _01_instance; else
        if (coupleString.equals("10")) return _10_instance; else
        if (coupleString.equals("11")) return _11_instance; else
            throw new IllegalArgumentException("coupleString = " + coupleString);
    }
    
    private static class _00 implements ICoupleValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return false; }
        public byte getTierKey() { return 1; }
        public String toString() { return "00"; }
        public ICoupleValue getAdjoinRightTarget1() { return _00_instance; }
        public ICoupleValue getAdjoinRightTarget2() { return _01_instance; }
        public ICoupleValue getAdjoinLeftSource1() { return _00_instance; }
        public ICoupleValue getAdjoinLeftSource2() { return _10_instance; }
        public ICoupleValue cloneWithInversedA() {  return _10_instance; }
        public ICoupleValue cloneWithInversedB() {  return _01_instance; }
    }

    private static class _01 implements ICoupleValue
    {
        public boolean isNotA() { return false; }
        public boolean isNotB() { return true; }
        public byte getTierKey() { return 4; }
        public String toString() { return "01"; }
        public ICoupleValue getAdjoinRightTarget1() { return _10_instance; }
        public ICoupleValue getAdjoinRightTarget2() { return _11_instance; }
        public ICoupleValue getAdjoinLeftSource1() { return _00_instance; }
        public ICoupleValue getAdjoinLeftSource2() { return _10_instance; }
        public ICoupleValue cloneWithInversedA() {  return _11_instance; }
        public ICoupleValue cloneWithInversedB() {  return _00_instance; }
    }

    private static class _10 implements ICoupleValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return false; }
        public byte getTierKey() { return 16; }
        public String toString() { return "10"; }
        public ICoupleValue getAdjoinRightTarget1() { return _00_instance; }
        public ICoupleValue getAdjoinRightTarget2() { return _01_instance; }
        public ICoupleValue getAdjoinLeftSource1() { return _01_instance; }
        public ICoupleValue getAdjoinLeftSource2() { return _11_instance; }
        public ICoupleValue cloneWithInversedA() {  return _00_instance; }
        public ICoupleValue cloneWithInversedB() {  return _11_instance; }
    }

    private static class _11 implements ICoupleValue
    {
        public boolean isNotA() { return true; }
        public boolean isNotB() { return true; }
        public byte getTierKey() { return 64; }
        public String toString() { return "11"; }
        public ICoupleValue getAdjoinRightTarget1() { return _10_instance; }
        public ICoupleValue getAdjoinRightTarget2() { return _11_instance; }
        public ICoupleValue getAdjoinLeftSource1() { return _01_instance; }
        public ICoupleValue getAdjoinLeftSource2() { return _11_instance; }
        public ICoupleValue cloneWithInversedA() {  return _01_instance; }
        public ICoupleValue cloneWithInversedB() {  return _10_instance; }
    }

}
