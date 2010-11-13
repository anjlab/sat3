/*
 * Copyright (C) 2010 AnjLab
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
