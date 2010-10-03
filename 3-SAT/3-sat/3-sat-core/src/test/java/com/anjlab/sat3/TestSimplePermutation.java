package com.anjlab.sat3;

import org.junit.Assert;
import org.junit.Test;

public class TestSimplePermutation
{

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
}
