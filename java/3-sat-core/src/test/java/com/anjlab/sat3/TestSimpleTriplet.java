package com.anjlab.sat3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSimpleTriplet
{
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
