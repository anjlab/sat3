package com.anjlab.sat3;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ITabularFormula;

public class TestHelper {

	@Test
	public void testCreateRandomFormulaWithRespectToMaxM()
	{
		ITabularFormula formula = Helper.createRandomFormula(new Random(), 3, 6);
		
		assertEquals(3, formula.getVarCount());
		assertEquals(6, formula.getTermCount());
		
		try
		{
			formula = Helper.createRandomFormula(new Random(), 4, 700);
						
			Assert.fail("This implementation assumes duplicated triplets are not allowed");
		}
		catch (IllegalArgumentException e)
		{
			assertEquals("3-SAT formula of 4 variables may have at most 64 valuable terms, but requested to create formula with 700 terms", e.getMessage());
		}
	}
}