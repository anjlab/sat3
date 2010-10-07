package com.anjlab.sat3;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestHashCode
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        System.out.println(TestHashCode.class.getName());
    }
    
    @Test
    public void testHashCodeForArray()
    {
        ITripletPermutation stp =
            new SimpleTripletPermutation(1, 2, 3);
        
        Assert.assertEquals(13194143727617L, stp.canonicalHashCode());
    }
}
