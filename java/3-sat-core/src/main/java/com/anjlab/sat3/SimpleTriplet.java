package com.anjlab.sat3;

import java.text.MessageFormat;

public class SimpleTriplet extends SimpleTier implements ITriplet
{
    public SimpleTriplet(int a, int b, int c)
    {
        super(Math.abs(a), Math.abs(b), Math.abs(c));
        
        keys_73516240 = 1;
        
        if (a < 0) keys_73516240 <<= 4;
        if (b < 0) keys_73516240 <<= 2;
        if (c < 0) keys_73516240 <<= 1;
    }

    public final boolean isNotA() { return (keys_73516240 >= 0x10); }
    public final boolean isNotB() { return (keys_73516240 & 0xCC) != 0; }
    public final boolean isNotC() { return (keys_73516240 & 0xAA) != 0; }

    public final int getTierKey()
    {
        return keys_73516240;
    }

    public String toString()
    {
        return MessageFormat.format(
                "{0}={1},{2}={3},{4}={5}", 
                getAName(), isNotA(),
                getBName(), isNotB(), 
                getCName(), isNotC());
    }

}