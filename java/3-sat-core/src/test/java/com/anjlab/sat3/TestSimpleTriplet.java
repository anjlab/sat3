package com.anjlab.sat3;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimpleTriplet
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
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
    
}
