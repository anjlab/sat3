package com.anjlab.sat3;

import junit.framework.Assert;

import org.junit.Test;

public class TestHashCode
{
    @Test
    public void testHashCodeForArray()
    {
        ITripletPermutation stp =
            new SimpleTripletPermutation(1, 2, 3);
        
        Assert.assertEquals(13194143727617L, stp.canonicalHashCode());
    }
}
