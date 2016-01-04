package com.anjlab.sat3.v2;

import org.junit.Assert;
import org.junit.Test;

public class TestSimpleCoupleValueFactory
{
    @Test
    public void testTierKey()
    {
        Assert.assertEquals(
                SimpleCoupleValueFactory._00_instance.getTierKey(),
                SimpleCoupleValueFactory.getTierKey(0, 0));
        
        Assert.assertEquals(
                SimpleCoupleValueFactory._01_instance.getTierKey(),
                SimpleCoupleValueFactory.getTierKey(0, -1));
        
        Assert.assertEquals(
                SimpleCoupleValueFactory._10_instance.getTierKey(),
                SimpleCoupleValueFactory.getTierKey(-1, 0));
        
        Assert.assertEquals(
                SimpleCoupleValueFactory._11_instance.getTierKey(),
                SimpleCoupleValueFactory.getTierKey(-1, -1));
    }
}
