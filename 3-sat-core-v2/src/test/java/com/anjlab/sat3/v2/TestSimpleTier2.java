package com.anjlab.sat3.v2;

import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._00_instance;
import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._01_instance;
import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._10_instance;
import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._11_instance;

import org.junit.Assert;
import org.junit.Test;

import com.anjlab.sat3.Value;

public class TestSimpleTier2
{
    @Test(expected=IllegalArgumentException.class)
    public void testKeysOfAForMixedValue()
    {
        ITier2 tier = new SimpleTier2();
        tier.keysOfA(Value.Mixed);
    }
    
    @Test
    public void testKeysOfA()
    {
        ITier2 tier;
        tier = new SimpleTier2();
        Assert.assertEquals(0, tier.keysOfA(Value.AllNegative));
        Assert.assertEquals(0, tier.keysOfA(Value.AllPlain));
        
        tier.add(_10_instance);
        tier.add(_11_instance);
        Assert.assertEquals(0, tier.keysOfA(Value.AllPlain));
        
        tier = new SimpleTier2();
        tier.add(_00_instance);
        tier.add(_01_instance);
        Assert.assertEquals(0, tier.keysOfA(Value.AllNegative));
        
        int keys;
        
        tier = new SimpleTier2();
        tier.add(_01_instance);
        keys = tier.keysOfA(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_01_instance, SimpleCoupleValueFactory.getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_00_instance);
        keys = tier.keysOfA(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_00_instance, SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_01_instance);
        tier.add(_00_instance);
        keys = tier.keysOfA(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertNull(SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_10_instance);
        keys = tier.keysOfA(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_10_instance, SimpleCoupleValueFactory.getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_11_instance);
        keys = tier.keysOfA(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_11_instance, SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_10_instance);
        tier.add(_11_instance);
        keys = tier.keysOfA(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertNull(SimpleCoupleValueFactory .getCoupleValue(keys));
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testKeysOfBForMixedValue()
    {
        ITier2 tier = new SimpleTier2();
        tier.keysOfB(Value.Mixed);
    }
    
    @Test
    public void testKeysOfB()
    {
        ITier2 tier;
        tier = new SimpleTier2();
        Assert.assertEquals(0, tier.keysOfB(Value.AllNegative));
        Assert.assertEquals(0, tier.keysOfB(Value.AllPlain));
        
        tier.add(_01_instance);
        tier.add(_11_instance);
        Assert.assertEquals(0, tier.keysOfB(Value.AllPlain));
        
        tier = new SimpleTier2();
        tier.add(_00_instance);
        tier.add(_10_instance);
        Assert.assertEquals(0, tier.keysOfB(Value.AllNegative));
        
        int keys;
        
        tier = new SimpleTier2();
        tier.add(_00_instance);
        keys = tier.keysOfB(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_00_instance, SimpleCoupleValueFactory.getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_10_instance);
        keys = tier.keysOfB(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_10_instance, SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_10_instance);
        tier.add(_00_instance);
        keys = tier.keysOfB(Value.AllPlain);
        Assert.assertNotSame(0, keys);
        Assert.assertNull(SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_01_instance);
        keys = tier.keysOfB(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_01_instance, SimpleCoupleValueFactory.getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_11_instance);
        keys = tier.keysOfB(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertEquals(_11_instance, SimpleCoupleValueFactory .getCoupleValue(keys));
        
        tier = new SimpleTier2();
        tier.add(_01_instance);
        tier.add(_11_instance);
        keys = tier.keysOfB(Value.AllNegative);
        Assert.assertNotSame(0, keys);
        Assert.assertNull(SimpleCoupleValueFactory .getCoupleValue(keys));
    }
    
    @Test
    public void testValueOfA()
    {
        ITier2 tier = new SimpleTier2();
        
        Assert.assertEquals(Value.Mixed, tier.valueOfA());
        
        tier.add(_00_instance);
        Assert.assertEquals(Value.AllPlain, tier.valueOfA());
        
        tier.add(_01_instance);
        Assert.assertEquals(Value.AllPlain, tier.valueOfA());
        
        tier.add(_10_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfA());
        
        tier.add(_11_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfA());
        
        tier.remove(_01_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfA());
        
        tier.remove(_00_instance);
        Assert.assertEquals(Value.AllNegative, tier.valueOfA());
        
        tier.remove(_10_instance);
        Assert.assertEquals(Value.AllNegative, tier.valueOfA());
    }
    
    @Test
    public void testValueOfB()
    {
        ITier2 tier = new SimpleTier2();
        
        Assert.assertEquals(Value.Mixed, tier.valueOfB());
        
        tier.add(_00_instance);
        Assert.assertEquals(Value.AllPlain, tier.valueOfB());
        
        tier.add(_10_instance);
        Assert.assertEquals(Value.AllPlain, tier.valueOfB());
        
        tier.add(_01_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfB());
        
        tier.add(_11_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfB());
        
        tier.remove(_10_instance);
        Assert.assertEquals(Value.Mixed, tier.valueOfB());
        
        tier.remove(_00_instance);
        Assert.assertEquals(Value.AllNegative, tier.valueOfB());
        
        tier.remove(_01_instance);
        Assert.assertEquals(Value.AllNegative, tier.valueOfB());
    }
    
    @Test
    public void testInverseOnEmptyTier()
    {
        ITier2 tier = new SimpleTier2();
        Assert.assertEquals(0, tier.size());
        
        //  Inverse empty tier
        tier.inverseA();
        Assert.assertEquals(0, tier.size());
        
        //  Inverse empty tier
        tier.inverseB();
        Assert.assertEquals(0, tier.size());
    }
    
    @Test
    public void testInverseA()
    {
        ITier2 tier = new SimpleTier2();
        tier.add(_00_instance);
        
        tier.inverseA();
        
        Assert.assertEquals(1, tier.size());
        Assert.assertTrue(tier.contains(_10_instance));
        
        tier.add(_00_instance);
        tier.inverseA();
        
        Assert.assertEquals(2, tier.size());
        Assert.assertTrue(tier.contains(_00_instance));
        Assert.assertTrue(tier.contains(_10_instance));
        
        tier.add(_01_instance);
        tier.inverseA();
        
        Assert.assertEquals(3, tier.size());
        Assert.assertTrue(tier.contains(_10_instance));
        Assert.assertTrue(tier.contains(_00_instance));
        Assert.assertTrue(tier.contains(_11_instance));
        
        tier.add(_01_instance);
        tier.inverseA();
        
        Assert.assertEquals(4, tier.size());
    }
    
    @Test
    public void testInverseB()
    {
        ITier2 tier = new SimpleTier2();
        tier.add(_00_instance);
        
        tier.inverseB();
        
        Assert.assertEquals(1, tier.size());
        Assert.assertTrue(tier.contains(_01_instance));
        
        tier.add(_00_instance);
        tier.inverseB();
        
        Assert.assertEquals(2, tier.size());
        Assert.assertTrue(tier.contains(_00_instance));
        Assert.assertTrue(tier.contains(_01_instance));
        
        tier.add(_10_instance);
        tier.inverseB();
        
        Assert.assertEquals(3, tier.size());
        Assert.assertTrue(tier.contains(_01_instance));
        Assert.assertTrue(tier.contains(_00_instance));
        Assert.assertTrue(tier.contains(_01_instance));
        
        tier.add(_10_instance);
        tier.inverseB();
        
        Assert.assertEquals(4, tier.size());
    }
}
