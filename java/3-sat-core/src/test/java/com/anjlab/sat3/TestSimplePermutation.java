package com.anjlab.sat3;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSimplePermutation
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        System.out.println(TestSimplePermutation.class.getName());
    }
    
    @Test
    public void testIndexOf()
    {
        SimplePermutation sp = new SimplePermutation();
        
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
        SimpleTripletPermutation stp;
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(1, 2, 3));
        
        Assert.assertEquals(1, stp.getAName());
        Assert.assertEquals(2, stp.getBName());
        Assert.assertEquals(3, stp.getCName());
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(1, 3, 2));
        
        Assert.assertEquals(1, stp.getAName());
        Assert.assertEquals(3, stp.getBName());
        Assert.assertEquals(2, stp.getCName());
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(2, 1, 3));
        
        Assert.assertEquals(2, stp.getAName());
        Assert.assertEquals(1, stp.getBName());
        Assert.assertEquals(3, stp.getCName());
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(2, 3, 1));
        
        Assert.assertEquals(2, stp.getAName());
        Assert.assertEquals(3, stp.getBName());
        Assert.assertEquals(1, stp.getCName());
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(3, 1, 2));
        
        Assert.assertEquals(3, stp.getAName());
        Assert.assertEquals(1, stp.getBName());
        Assert.assertEquals(2, stp.getCName());
        
        stp =           new SimpleTripletPermutation(1, 2, 3);
        stp.transposeTo(new SimpleTripletPermutation(3, 2, 1));
        
        Assert.assertEquals(3, stp.getAName());
        Assert.assertEquals(2, stp.getBName());
        Assert.assertEquals(1, stp.getCName());
    }
    
    @Test
    public void testCanonicalName()
    {
        SimpleTripletPermutation stp;
        
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
}
