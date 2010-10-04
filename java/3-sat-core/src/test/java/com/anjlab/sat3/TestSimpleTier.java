package com.anjlab.sat3;

import org.junit.Assert;
import org.junit.Test;

public class TestSimpleTier {

	@Test
	public void testAddSameTripletSeveralTimes()
	{
		ITier tier = new SimpleTier(1, 2, 3);
		
		tier.add(new SimpleTriplet(1, 2, 3));
		tier.add(new SimpleTriplet(1, 2, 3));
		tier.add(new SimpleTriplet(1, 2, 3));
		tier.add(new SimpleTriplet(1, 2, -3));
		
		//	Adding same triplet several times should result in adding only one triplet
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
		
		tier.add(new SimpleTriplet(1, 2, 3));
		tier.add(new SimpleTriplet(1, -2, 3));
		
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
}
