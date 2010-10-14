package com.anjlab.sat3;

import org.junit.Test;

public class TestBinaryLogic
{
    @Test
    public void testNot()
    {
        byte b = 1;
        for (int i = 0; i < 9; i++)
        {
            Helper.printBits(b);
            //  Shift left
            b = (byte) (b << 1);
        }
        Helper.printLine('*', 10);
        b = 0; //   empty tier
        b = -1; //  complete tier
        for (int i = 0; i < 9; i++)
        {
            Helper.printBits(b);
            //  Shift right
            b = (byte) ((b >> 1) & 0x7F);
        }
    }
}
