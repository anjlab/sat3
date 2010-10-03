package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.createRandomFormula;
import static com.anjlab.sat3.Helper.loadFromDIMACSFileFormat;
import static com.anjlab.sat3.Helper.prettyPrint;
import static com.anjlab.sat3.Helper.saveToDIMACSFileFormat;
import static junit.framework.Assert.assertEquals;

import java.io.IOException;
import java.util.Random;

import org.junit.Test;

public class TestLoadSave {

	@Test
	public void testLoadFromDIMACS() throws IOException
	{
        ITabularFormula formula = loadFromDIMACSFileFormat("target/test-classes/article-example.cnf");

        prettyPrint(formula);
        
        assertEquals(8, formula.getVarCount());
        assertEquals(44, formula.getTermCount());
	}
	
	@Test
	public void testSaveToDIMACS() throws IOException
	{
		ITabularFormula formula = createRandomFormula(new Random(12), 8, 10);
		
		prettyPrint(formula);
		
        saveToDIMACSFileFormat(formula, "target/test-classes/test.cnf");
        
        ITabularFormula formula2 = loadFromDIMACSFileFormat("target/test-classes/test.cnf");
        
        assertEquals(formula.getVarCount(), formula2.getVarCount());
        assertEquals(formula.getTermCount(), formula2.getTermCount());
	}

	@Test
	public void testLoadSpeed() throws IOException
	{
		long start = System.currentTimeMillis();
        ITabularFormula formula = loadFromDIMACSFileFormat("target/test-classes/unif-k3-r4.2-v18000-c75600-S420719158-080.cnf");
        long end = System.currentTimeMillis();
        
        System.out.println("varCount=" + formula.getVarCount() 
        		+ ", termCount=" + formula.getTermCount()
        		+ ", tierCount=" + formula.getTiers().size()
        		+ ", loadTime=" + (end - start));
    }
}