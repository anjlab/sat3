/*
 * Copyright (C) 2010 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.createRandomFormula;
import static com.anjlab.sat3.Helper.loadFromFile;
import static com.anjlab.sat3.Helper.prettyPrint;
import static com.anjlab.sat3.Helper.saveToDIMACSFileFormat;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.util.Random;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.list.ObjectArrayList;

public class TestLoadSave
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestLoadSave.class.getName());
    }
    
    @Test
    public void testLoadFromDIMACS() throws IOException
    {
        ITabularFormula formula = loadFromFile("target/test-classes/article-example.cnf");

        prettyPrint(formula);
        
        assertEquals(8, formula.getVarCount());
        assertEquals(44, formula.getClausesCount());
    }
    
    @Test
    public void testSaveToDIMACS() throws IOException
    {
        ITabularFormula formula = createRandomFormula(new Random(12), 8, 10);
        
        prettyPrint(formula);
        
        saveToDIMACSFileFormat(formula, "target/test-classes/test.cnf");
        
        ITabularFormula formula2 = loadFromFile("target/test-classes/test.cnf");
        
        assertEquals(formula.getVarCount(), formula2.getVarCount());
        assertEquals(formula.getClausesCount(), formula2.getClausesCount());
    }

    @Test
    public void testLoadSpeed() throws IOException
    {
        long start = System.currentTimeMillis();
        ITabularFormula formula = loadFromFile("target/test-classes/unif-k3-r4.2-v18000-c75600-S420719158-080.cnf");
        long end = System.currentTimeMillis();
        
        System.out.println("varCount=" + formula.getVarCount() 
                + ", clausesCount=" + formula.getClausesCount()
                + ", tiersCount=" + formula.getTiers().size()
                + ", loadTime=" + (end - start));
    }
    
    @Test
    public void testGenericLoad() throws IOException
    {
        ITabularFormula formula = loadFromFile("target/test-classes/sat-example.cnf");
     
        prettyPrint(formula); 
        assertEquals(8, formula.getVarCount());
        assertEquals(9, formula.getClausesCount());
    }
    
    @Test
    public void testGenericLoadSpeed() throws IOException
    {
        long start = System.currentTimeMillis();
        ITabularFormula formula = loadFromFile("target/test-classes/unif-k3-r4.2-v18000-c75600-S420719158-080.cnf");
        long end = System.currentTimeMillis();
        
        System.out.println("varCount=" + formula.getVarCount() 
                + ", clausesCount=" + formula.getClausesCount()
                + ", tiersCount=" + formula.getTiers().size()
                + ", loadTime=" + (end - start));
    }
    
    @Test
    public void testGenericLoadSpeed2() throws IOException
    {
        long start = System.currentTimeMillis();
        ITabularFormula formula = loadFromFile("target/test-classes/gss-31-s100.cnf");
        long end = System.currentTimeMillis();
        
        System.out.println("varCount=" + formula.getVarCount() 
                + ", clausesCount=" + formula.getClausesCount()
                + ", tiersCount=" + formula.getTiers().size()
                + ", loadTime=" + (end - start));
    }
    
    @Test
    public void testLoadFromSKT() throws Exception
    {
        ITabularFormula formula = 
            Helper.createFormula( 1, 2, 3,
                                 -1, 2,-3,
                                  2, 3, 4,
                                  2,-3, 4,
                                  5, 6, 7,
                                  6, 7, 3,
                                  1, 5, 7);
        
        ObjectArrayList ct = Helper.createCTF(formula);
        Helper.completeToCTS(ct, formula.getPermutation());
        
        assertEquals(2, ct.size());
        
        String filename = "target/test-classes/file.skt";
        
        Helper.convertCTStructuresToRomanovSKTFileFormat(ct, filename);

        ITabularFormula restoredFormula = loadFromFile(filename);
        
        assertNotNull(restoredFormula);
        
        for (int j = 0; j < restoredFormula.getTiers().size(); j++)
        {
            Assert.assertTrue(formula.containsAllValuesOf(restoredFormula.getTier(j)));
        }
    }
}
