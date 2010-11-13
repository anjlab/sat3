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

import static com.anjlab.sat3.SimplePermutation.createPermutation;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimplePermutation
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestSimplePermutation.class.getName());
    }
    
    @Test
    public void testIndexOf()
    {
        IPermutation sp = new SimplePermutation();
        
        sp.add(4);
        sp.add(6);
        sp.add(7);
        
        Assert.assertEquals(0, sp.indexOf(4));
        Assert.assertEquals(1, sp.indexOf(6));
        Assert.assertEquals(2, sp.indexOf(7));
        
        sp.add(1, 9);
        
        Assert.assertEquals(0, sp.indexOf(4));
        Assert.assertEquals(1, sp.indexOf(9));
        Assert.assertEquals(2, sp.indexOf(6));        
        Assert.assertEquals(3, sp.indexOf(7));
        
        sp.add(3);
        
        Assert.assertEquals(0, sp.indexOf(4));
        Assert.assertEquals(1, sp.indexOf(9));
        Assert.assertEquals(2, sp.indexOf(6));        
        Assert.assertEquals(3, sp.indexOf(7));
        Assert.assertEquals(4, sp.indexOf(3));
        
        sp.add(0, 5);
        
        Assert.assertEquals(0, sp.indexOf(5));
        Assert.assertEquals(1, sp.indexOf(4));
        Assert.assertEquals(2, sp.indexOf(9));
        Assert.assertEquals(3, sp.indexOf(6));        
        Assert.assertEquals(4, sp.indexOf(7));
        Assert.assertEquals(5, sp.indexOf(3));
    }
    
    @Test
    public void testTransposition()
    {
        ITripletPermutation stp;
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(1, 2, 3));
        
        Assert.assertEquals(1, stp.getAName());
        Assert.assertEquals(2, stp.getBName());
        Assert.assertEquals(3, stp.getCName());
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(1, 3, 2));
        
        Assert.assertEquals(1, stp.getAName());
        Assert.assertEquals(3, stp.getBName());
        Assert.assertEquals(2, stp.getCName());
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(2, 1, 3));
        
        Assert.assertEquals(2, stp.getAName());
        Assert.assertEquals(1, stp.getBName());
        Assert.assertEquals(3, stp.getCName());
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(2, 3, 1));
        
        Assert.assertEquals(2, stp.getAName());
        Assert.assertEquals(3, stp.getBName());
        Assert.assertEquals(1, stp.getCName());
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(3, 1, 2));
        
        Assert.assertEquals(3, stp.getAName());
        Assert.assertEquals(1, stp.getBName());
        Assert.assertEquals(2, stp.getCName());
        
        stp =           new SimpleTier(1, 2, 3);
        stp.transposeTo(new SimpleTier(3, 2, 1));
        
        Assert.assertEquals(3, stp.getAName());
        Assert.assertEquals(2, stp.getBName());
        Assert.assertEquals(1, stp.getCName());
    }
    
    @Test
    public void testCanonicalName()
    {
        ITripletPermutation stp;
        
        stp = new SimpleTripletPermutation(1, 2, 3);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
        
        stp = new SimpleTripletPermutation(1, 3, 2);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
        
        stp = new SimpleTripletPermutation(2, 1, 3);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
        
        stp = new SimpleTripletPermutation(2, 3, 1);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
        
        stp = new SimpleTripletPermutation(3, 1, 2);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
        
        stp = new SimpleTripletPermutation(3, 2, 1);
        Assert.assertArrayEquals(new int[] {1, 2, 3}, stp.getCanonicalName());
    }
    
    @Test
    public void testShiftToStart()
    {
        IPermutation permutation = createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9);
        permutation.shiftToStart(4, 6);
        Assert.assertTrue(permutation.sameAs(createPermutation(5, 6, 7, 1, 2, 3, 4, 8, 9)));
        
        assertEquals(0, permutation.indexOf(5));
        assertEquals(1, permutation.indexOf(6));
        assertEquals(2, permutation.indexOf(7));
        assertEquals(3, permutation.indexOf(1));
        assertEquals(4, permutation.indexOf(2));
        assertEquals(5, permutation.indexOf(3));
        assertEquals(6, permutation.indexOf(4));
        assertEquals(7, permutation.indexOf(8));
        assertEquals(8, permutation.indexOf(9));
    }

    @Test
    public void testShiftToEnd()
    {
        IPermutation permutation = createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9);
        permutation.shiftToEnd(4, 6);
        Assert.assertTrue(permutation.sameAs(createPermutation(1, 2, 3, 4, 8, 9, 5, 6, 7)));
        
        assertEquals(0, permutation.indexOf(1));
        assertEquals(1, permutation.indexOf(2));
        assertEquals(2, permutation.indexOf(3));
        assertEquals(3, permutation.indexOf(4));
        assertEquals(4, permutation.indexOf(8));
        assertEquals(5, permutation.indexOf(9));
        assertEquals(6, permutation.indexOf(5));
        assertEquals(7, permutation.indexOf(6));
        assertEquals(8, permutation.indexOf(7));
    }
}
