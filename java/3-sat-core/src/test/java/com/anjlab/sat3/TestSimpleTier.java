package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimpleTier
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestSimpleTier.class.getName());
    }
    
    @Test
    public void testAddSameTripletSeveralTimes()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.add(_000_instance);
        tier.add(_000_instance);
        tier.add(_001_instance);
        
        //    Adding same triplet several times should result in adding only one triplet
        assertEquals(2, tier.size());
    }

    @Test
    public void testRemoveTriplet()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.add(_010_instance);
        tier.add(_100_instance);
        tier.add(_011_instance);
        
        assertEquals(4, tier.size());
        
        tier.remove(_100_instance);
        
        assertEquals(3, tier.size());
    }

    @Test
    public void testTripletsIterator()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.add(_010_instance);
        
        for (ITripletValue tripletValue : tier) {
            System.out.println(tripletValue);
        }
    }
    
    @Test
    public void testFullTierIterator()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.add(_001_instance);
        tier.add(_010_instance);
        tier.add(_011_instance);
        tier.add(_100_instance);
        tier.add(_101_instance);
        tier.add(_110_instance);
        tier.add(_111_instance);
        
        assertEquals(8, tier.size());
        
        int counter = 0;
        for (ITripletValue tripletValue : tier) {
            System.out.println(tripletValue);
            assertNotNull(tripletValue);
            counter++;
        }
        
        assertEquals(8, counter);
    }
    
    @Test
    public void testSwapAB()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.swapAB();
        assertTrue(tier.contains(_000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_001_instance);
        tier.swapAB();
        assertTrue(tier.contains(_001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_010_instance);
        tier.swapAB();
        assertTrue(tier.contains(_100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_011_instance);
        tier.swapAB();
        assertTrue(tier.contains(_101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_100_instance);
        tier.swapAB();
        assertTrue(tier.contains(_010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_101_instance);
        tier.swapAB();
        assertTrue(tier.contains(_011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_110_instance);
        tier.swapAB();
        assertTrue(tier.contains(_110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_111_instance);
        tier.swapAB();
        assertTrue(tier.contains(_111_instance));
    }
    
    @Test
    public void testSwapAC()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.swapAC();
        assertTrue(tier.contains(_000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_001_instance);
        tier.swapAC();
        assertTrue(tier.contains(_100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_010_instance);
        tier.swapAC();
        assertTrue(tier.contains(_010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_011_instance);
        tier.swapAC();
        assertTrue(tier.contains(_110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_100_instance);
        tier.swapAC();
        assertTrue(tier.contains(_001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_101_instance);
        tier.swapAC();
        assertTrue(tier.contains(_101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_110_instance);
        tier.swapAC();
        assertTrue(tier.contains(_011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_111_instance);
        tier.swapAC();
        assertTrue(tier.contains(_111_instance));
    }
    
    @Test
    public void testSwapBC()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_000_instance);
        tier.swapBC();
        assertTrue(tier.contains(_000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_001_instance);
        tier.swapBC();
        assertTrue(tier.contains(_010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_010_instance);
        tier.swapBC();
        assertTrue(tier.contains(_001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_011_instance);
        tier.swapBC();
        assertTrue(tier.contains(_011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_100_instance);
        tier.swapBC();
        assertTrue(tier.contains(_100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_101_instance);
        tier.swapBC();
        assertTrue(tier.contains(_110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_110_instance);
        tier.swapBC();
        assertTrue(tier.contains(_101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_111_instance);
        tier.swapBC();
        assertTrue(tier.contains(_111_instance));
    }

    @Test
    public void testAdjoinRight()
    {
        ITier t1 = new SimpleTier(1, 2, 3);
        t1.add(_000_instance);
        t1.add(_001_instance);
        t1.add(_011_instance);
        
        ITier t2 = new SimpleTier(2, 3, 4);
        t2.add(_001_instance);
        t2.add(_001_instance);
        t2.add(_111_instance);
        
        t1.adjoinRight(t2);
        
        assertEquals(2, t1.size());
        assertTrue(t1.contains(_000_instance));
        assertTrue(t1.contains(_011_instance));
        
        assertTrue(!t1.contains(_001_instance));
    }
    
    @Test
    public void testAdjoinLeft()
    {
        ITier t1 = new SimpleTier(1, 2, 3);
        t1.add(_001_instance);
        t1.add(_011_instance);
        t1.add(_111_instance);

        ITier t2 = new SimpleTier(2, 3, 4);
        t2.add(_000_instance);
        t2.add(_001_instance);
        t2.add(_011_instance);

        t2.adjoinLeft(t1);
        
        assertEquals(1, t2.size());
        assertTrue(t2.contains(_011_instance));
        
        assertTrue(!t2.contains(_000_instance));
        assertTrue(!t2.contains(_001_instance));
    }

    @Test
    public void testSubtract()
    {
        ITier t1 = SimpleTier.createCompleteTier(1, 2, 3);
        
        ITier t2 = new SimpleTier(1, 2, 3);
        t2.add(_001_instance);
        t2.add(_101_instance);
        
        t1.subtract(t2);
        
        assertEquals(6, t1.size());
        
        assertTrue(t1.contains(_000_instance));
        assertTrue(t1.contains(_010_instance));
        assertTrue(t1.contains(_011_instance));
        assertTrue(t1.contains(_100_instance));
        assertTrue(t1.contains(_110_instance));
        assertTrue(t1.contains(_111_instance));
        
        assertTrue(!t1.contains(_001_instance));
        assertTrue(!t1.contains(_101_instance));
    }

    @Test
    public void testConcretize()
    {
        SimpleTier tier;
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(1, Value.AllPlain);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(_000_instance));
        assertTrue(tier.contains(_001_instance));
        assertTrue(tier.contains(_010_instance));
        assertTrue(tier.contains(_011_instance));
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(2, Value.AllPlain);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(_000_instance));
        assertTrue(tier.contains(_001_instance));
        assertTrue(tier.contains(_100_instance));
        assertTrue(tier.contains(_101_instance));
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(3, Value.AllPlain);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(_000_instance));
        assertTrue(tier.contains(_010_instance));
        assertTrue(tier.contains(_100_instance));
        assertTrue(tier.contains(_110_instance));
    }
    
    @Test
    public void testValueOfA()
    {
        SimpleTier tier = new SimpleTier(1, 2, 3);
        Value value = tier.valueOfA();
        assertEquals("Value of empty tier", Value.Mixed, value);
        
        tier.add(_000_instance);
        tier.add(_001_instance);
        tier.add(_010_instance);
        tier.add(_011_instance);

        value = tier.valueOfA();
        assertEquals("Value of full a=0 tier", Value.AllPlain, value);
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_100_instance);
        tier.add(_101_instance);
        tier.add(_110_instance);
        tier.add(_111_instance);

        value = tier.valueOfA();
        assertEquals("Value of full a=1 tier", Value.AllNegative, value);
        
        tier.add(_001_instance);
        value = tier.valueOfA();
        assertEquals("Value of a=0 and a=1 tier", Value.Mixed, value);
    }
    
    @Test
    public void testValueOfB()
    {
        SimpleTier tier = new SimpleTier(1, 2, 3);
        Value value = tier.valueOfB();
        assertEquals("Value of empty tier", Value.Mixed, value);
        
        tier.add(_000_instance);
        tier.add(_001_instance);
        tier.add(_100_instance);
        tier.add(_101_instance);

        value = tier.valueOfB();
        assertEquals("Value of full b=0 tier", Value.AllPlain, value);
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_010_instance);
        tier.add(_011_instance);
        tier.add(_110_instance);
        tier.add(_111_instance);

        value = tier.valueOfB();
        assertEquals("Value of full b=1 tier", Value.AllNegative, value);
        
        tier.add(_001_instance);
        value = tier.valueOfB();
        assertEquals("Value of b=0 and b=1 tier", Value.Mixed, value);
    }
    
    @Test
    public void testValueOfC()
    {
        SimpleTier tier = new SimpleTier(1, 2, 3);
        Value value = tier.valueOfC();
        assertEquals("Value of empty tier", Value.Mixed, value);
        
        tier.add(_000_instance);
        tier.add(_010_instance);
        tier.add(_100_instance);
        tier.add(_110_instance);

        value = tier.valueOfC();
        assertEquals("Value of full c=0 tier", Value.AllPlain, value);
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(_001_instance);
        tier.add(_011_instance);
        tier.add(_101_instance);
        tier.add(_111_instance);

        value = tier.valueOfC();
        assertEquals("Value of full c=1 tier", Value.AllNegative, value);
        
        tier.add(_000_instance);
        value = tier.valueOfC();
        assertEquals("Value of c=0 and c=1 tier", Value.Mixed, value);
    }
}
