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

import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimpleTriplet
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestSimpleTriplet.class.getName());
    }
    
    @Test
    public void testNotX()
    {
        ITriplet t;
        
        t = new SimpleTriplet(1, 2, 3);
        assertTrue(!t.isNotA());
        assertTrue(!t.isNotB());
        assertTrue(!t.isNotC());
        
        t = new SimpleTriplet(1, 2, -3);
        assertTrue(!t.isNotA());
        assertTrue(!t.isNotB());
        assertTrue(t.isNotC());
        
        t = new SimpleTriplet(1, -2, 3);
        assertTrue(!t.isNotA());
        assertTrue(t.isNotB());
        assertTrue(!t.isNotC());
        
        t = new SimpleTriplet(1, -2, -3);
        assertTrue(!t.isNotA());
        assertTrue(t.isNotB());
        assertTrue(t.isNotC());
        
        t = new SimpleTriplet(-1, 2, 3);
        assertTrue(t.isNotA());
        assertTrue(!t.isNotB());
        assertTrue(!t.isNotC());
        
        t = new SimpleTriplet(-1, 2, -3);
        assertTrue(t.isNotA());
        assertTrue(!t.isNotB());
        assertTrue(t.isNotC());
        
        t = new SimpleTriplet(-1, -2, 3);
        assertTrue(t.isNotA());
        assertTrue(t.isNotB());
        assertTrue(!t.isNotC());
        
        t = new SimpleTriplet(-1, -2, -3);
        assertTrue(t.isNotA());
        assertTrue(t.isNotB());
        assertTrue(t.isNotC());
        
    }
    
    @Test
    public void testTransposeTo()
    {
        SimpleTriplet triplet = new SimpleTriplet(1, 2, -3);
//        triplet.add(SimpleTripletValueFactory._001_instance);
        triplet.add(_101_instance);
        triplet.transposeTo(new int[]{ 2, 1, 3 });
        
        Assert.assertEquals(2, triplet.size());
        assertTrue("Should contain", triplet.contains(_001_instance));
        assertTrue("Should contain", triplet.contains(_011_instance));
    }
}
