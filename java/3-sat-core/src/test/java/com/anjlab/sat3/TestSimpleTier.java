package com.anjlab.sat3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimpleTier
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        System.out.println(TestSimpleTier.class.getName());
    }
    
    @Test
    public void testAddSameTripletSeveralTimes()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(new SimpleTriplet(1, 2, 3));
        tier.add(new SimpleTriplet(1, 2, 3));
        tier.add(new SimpleTriplet(1, 2, 3));
        tier.add(new SimpleTriplet(1, 2, -3));
        
        //    Adding same triplet several times should result in adding only one triplet
        Assert.assertEquals(2, tier.size());
    }

    @Test
    public void testRemoveTriplet()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(new SimpleTriplet(1, 2, 3));
        tier.add(new SimpleTriplet(1, -2, 3));
        tier.add(new SimpleTriplet(-1, 2, 3));
        tier.add(new SimpleTriplet(1, -2, -3));
        
        Assert.assertEquals(4, tier.size());
        
        tier.remove(SimpleTripletValueFactory._100_instance);
        
        Assert.assertEquals(3, tier.size());
    }

    @Test
    public void testTripletsIterator()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.union(new SimpleTriplet(1, 2, 3));
        tier.union(new SimpleTriplet(1, -2, 3));
        
        for (ITripletValue tripletValue : tier) {
            System.out.println(tripletValue);
        }
    }
    
    @Test
    public void testFullTierIterator()
    {
        ITier tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._000_instance);
        tier.add(SimpleTripletValueFactory._001_instance);
        tier.add(SimpleTripletValueFactory._010_instance);
        tier.add(SimpleTripletValueFactory._011_instance);
        tier.add(SimpleTripletValueFactory._100_instance);
        tier.add(SimpleTripletValueFactory._101_instance);
        tier.add(SimpleTripletValueFactory._110_instance);
        tier.add(SimpleTripletValueFactory._111_instance);
        
        Assert.assertEquals(8, tier.size());
        
        int counter = 0;
        for (ITripletValue tripletValue : tier) {
            System.out.println(tripletValue);
            Assert.assertNotNull(tripletValue);
            counter++;
        }
        
        Assert.assertEquals(8, counter);
    }
    
    @Test
    public void testSwapAB()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._000_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._001_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._010_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._011_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._100_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._101_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._110_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._111_instance);
        tier.swapAB();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._111_instance));
    }
    
    @Test
    public void testSwapAC()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._000_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._001_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._010_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._011_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._100_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._101_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._110_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._111_instance);
        tier.swapAC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._111_instance));
    }
    
    @Test
    public void testSwapBC()
    {
        ITier tier;
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._000_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._001_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._010_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._010_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._001_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._011_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._011_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._100_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._100_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._101_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._110_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._110_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._101_instance));
        
        tier = new SimpleTier(1, 2, 3);
        
        tier.add(SimpleTripletValueFactory._111_instance);
        tier.swapBC();
        Assert.assertTrue(tier.contains(SimpleTripletValueFactory._111_instance));
    }

    @Test
    public void testAdjoinRight()
    {
        ITier t1 = new SimpleTier(1, 2, 3);
        t1.add(SimpleTripletValueFactory._000_instance);
        t1.add(SimpleTripletValueFactory._001_instance);
        t1.add(SimpleTripletValueFactory._011_instance);
        
        ITier t2 = new SimpleTier(2, 3, 4);
        t2.add(SimpleTripletValueFactory._001_instance);
        t2.add(SimpleTripletValueFactory._001_instance);
        t2.add(SimpleTripletValueFactory._111_instance);
        
        t1.adjoinRight(t2);
        
        assertEquals(2, t1.size());
        assertTrue(t1.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._011_instance));
        
        assertTrue(!t1.contains(SimpleTripletValueFactory._001_instance));
    }
    
    @Test
    public void testAdjoinLeft()
    {
        ITier t1 = new SimpleTier(1, 2, 3);
        t1.add(SimpleTripletValueFactory._001_instance);
        t1.add(SimpleTripletValueFactory._011_instance);
        t1.add(SimpleTripletValueFactory._111_instance);

        ITier t2 = new SimpleTier(2, 3, 4);
        t2.add(SimpleTripletValueFactory._000_instance);
        t2.add(SimpleTripletValueFactory._001_instance);
        t2.add(SimpleTripletValueFactory._011_instance);

        t2.adjoinLeft(t1);
        
        assertEquals(1, t2.size());
        assertTrue(t2.contains(SimpleTripletValueFactory._011_instance));
        
        assertTrue(!t2.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(!t2.contains(SimpleTripletValueFactory._001_instance));
    }

    @Test
    public void testSubtract()
    {
        ITier t1 = SimpleTier.createCompleteTier(1, 2, 3);
        
        ITier t2 = new SimpleTier(1, 2, 3);
        t2.add(SimpleTripletValueFactory._001_instance);
        t2.add(SimpleTripletValueFactory._101_instance);
        
        t1.subtract(t2);
        
        assertEquals(6, t1.size());
        
        assertTrue(t1.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._010_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._011_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._100_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._110_instance));
        assertTrue(t1.contains(SimpleTripletValueFactory._111_instance));
        
        assertTrue(!t1.contains(SimpleTripletValueFactory._001_instance));
        assertTrue(!t1.contains(SimpleTripletValueFactory._101_instance));
    }

    @Test
    public void testConcretize()
    {
        SimpleTier tier;
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(1, true);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._001_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._010_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._011_instance));
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(2, true);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._001_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._100_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._101_instance));
        
        tier = SimpleTier.createCompleteTier(1, 2, 3);
        tier.concretize(3, true);
        assertEquals(4, tier.size());
        assertTrue(tier.contains(SimpleTripletValueFactory._000_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._010_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._100_instance));
        assertTrue(tier.contains(SimpleTripletValueFactory._110_instance));
    }
}
