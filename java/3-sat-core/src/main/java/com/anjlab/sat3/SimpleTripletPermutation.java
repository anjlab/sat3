package com.anjlab.sat3;


public class SimpleTripletPermutation implements ITripletPermutation
{
    private final static int _2_power_21 = 2097152;
    
    private int a;
    private int b;
    private int c;

    private int[] canonicalName;
    private long canonicalHashCode;
    
    public SimpleTripletPermutation(int a, int b, int c)
    {
        this.a = a;
        this.b = b;
        this.c = c;

        //    See comments in #setCanonicalName()
        if (a > _2_power_21 || b > _2_power_21 || c > _2_power_21)
            throw new IndexOutOfBoundsException("a > _2_power_21 || b > _2_power_21 || c > _2_power_21 (" + this + ")");
            
        if (a <= 0 || b <= 0 || c <= 0)
            throw new IllegalArgumentException("a <= 0 || b <= 0 || c <= 0 (" + this + ")");
        
        if (a == b || b == c || a == c)
            throw new IllegalArgumentException("a == b || b == c || a == c (" + this + ")");

        setCanonicalName(a, b, c);
    }

    private void setCanonicalName(int a, int b, int c) {
        if (a < b)
        {
            if (b < c) canonicalName = new int[] {a, b, c}; else
            if (c < a) canonicalName = new int[] {c, a, b}; else
                       canonicalName = new int[] {a, c, b};
        }
        else
        {
            if (c < b) canonicalName = new int[] {c, b, a}; else
            if (a < c) canonicalName = new int[] {b, a, c}; else
                       canonicalName = new int[] {b, c, a};
        }
        
        //    Fitting three integer to 64-bit long
        //    requires each integer be <= 2^21
        canonicalHashCode = (long)((long) canonicalName[2] << (21 * 2))
                          | (long)((long) canonicalName[1] << 21)
                          | (canonicalName[0]);
    }

    public final int getAName()
    {
        return a;
    }

    public final int getBName()
    {
        return b;
    }

    public final int getCName()
    {
        return c;
    }

    public final int[] getCanonicalName()
    {
        return canonicalName;
    }

    public long canonicalHashCode()
    {
        return canonicalHashCode;
    }

    public final boolean hasSameVariablesAs(ITripletPermutation permutation)
    {
        return canonicalHashCode == permutation.canonicalHashCode();
    }

    public final boolean hasVariable(int varName)
    {
        return a == varName || b == varName || c == varName;
    }

    public void transposeTo(ITripletPermutation targetPermutation)
    {
        if (!hasSameVariablesAs(targetPermutation))
        {
            throw new IllegalArgumentException(targetPermutation + " should have same variables as " + this);
        }
        
        if (targetPermutation.getAName() != getAName())
        {
            if (targetPermutation.getAName() == getBName()) swapAB(); else swapAC();
        }
        if (targetPermutation.getBName() != getBName())
        {
            if (targetPermutation.getBName() == getAName()) swapAB(); else swapBC();
        }
        if (targetPermutation.getAName() != getAName())
        {
            if (targetPermutation.getAName() == getBName()) swapAB(); else swapAC();
        }
    }

    public void swapAB()
    {
        a = a + b;
        b = a - b;
        a = a - b;
    }

    public void swapAC()
    {
        a = a + c;
        c = a - c;
        a = a - c;
    }

    public void swapBC()
    {
        c = c + b;
        b = c - b;
        c = c - b;
    }

    public String toString()
    {
        return a + "," + b + "," + c;
    }
}
