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

import static com.anjlab.sat3.SimpleTripletValueFactory.getTripletValue;

public final class SimpleTriplet extends SimpleTier implements ITriplet
{
    public SimpleTriplet(int a, int b, int c)
    {
        super(Math.abs(a), Math.abs(b), Math.abs(c));
        
        size = 1;
        keys_73516240 = 1;
        
        if (a < 0) keys_73516240 <<= 4;
        if (b < 0) keys_73516240 <<= 2;
        if (c < 0) keys_73516240 <<= 1;
    }

    public boolean isNotA() { return (keys_73516240 & 0xF0) != 0; }
    public boolean isNotB() { return (keys_73516240 & 0xCC) != 0; }
    public boolean isNotC() { return (keys_73516240 & 0xAA) != 0; }

    /**
     * {@inheritDoc}
     * 
     * <p>This implementation associates numbers according to bits in a byte:</p>
     * <table>
     * <tr><th>Values of (a,b,c)</th><th>Associated number</th><th></th></tr>
     * <tr><td>000</td><td>0</td>
     * <td rowspan='8'>
     * The numbering is implemented this way:
     * <pre>
     *  keys_73516240 = 1;
     *  
     *  if (a < 0) keys_73516240 <<= 4;
     *  if (b < 0) keys_73516240 <<= 2;
     *  if (c < 0) keys_73516240 <<= 1;
     * </pre> 
     * </td></tr>
     * <tr><td>001</td><td>4</td></tr>
     * <tr><td>010</td><td>2</td></tr>
     * <tr><td>011</td><td>6</td></tr>
     * <tr><td>100</td><td>1</td></tr>
     * <tr><td>101</td><td>5</td></tr>
     * <tr><td>110</td><td>3</td></tr>
     * <tr><td>111</td><td>7</td></tr>
     * </table>
     * <p>So, for example, if the third least meaningful bit in <code>keys_73516240</code>
     * equals to 1 then the triplet value is a value #2, which is 010.
     * </p>
     * Such implementation allows to store all possible combinations 
     * of triplet values of a tier in a single byte. 
     */
    public byte getTierKey()
    {
        return keys_73516240;
    }

    public ITripletValue getAdjoinRightTarget1()
    {
        return getTripletValue(keys_73516240).getAdjoinRightTarget1();
    }

    public ITripletValue getAdjoinRightTarget2()
    {
        return getTripletValue(keys_73516240).getAdjoinRightTarget2();
    }

    public ITripletValue getAdjoinLeftSource1()
    {
        return getTripletValue(keys_73516240).getAdjoinLeftSource1();
    }

    public ITripletValue getAdjoinLeftSource2()
    {
        return getTripletValue(keys_73516240).getAdjoinLeftSource2();
    }

}