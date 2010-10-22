package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.list.ObjectArrayList;

public class TestHelper
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        Helper.UseUniversalVarNames = true;
        System.out.println(TestHelper.class.getName());
    }
    
    @Test
    public void testCreateRandomFormulaWithRespectToMaxM()
    {
        ITabularFormula formula = Helper.createRandomFormula(new Random(), 3, 6);
        
        assertEquals(3, formula.getVarCount());
        //  Clauses count not really plays any role in this implementation
//        assertEquals(6, formula.getClausesCount());
        
        try
        {
            formula = Helper.createRandomFormula(new Random(), 4, 700);
                        
            Assert.fail("This implementation assumes duplicated triplets are not allowed");
        }
        catch (IllegalArgumentException e)
        {
            assertEquals("3-SAT formula of 4 variables may have at most 64 valuable clauses, but requested to create formula with 700 clauses", e.getMessage());
        }
    }
    
    @Test
    public void testUnify() throws Exception
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure) Helper.createFormula(
                new int[] 
                        {                //        a b c d e f g h
                            1, 2, -3,    //        0 0 1          
                            -1, 2, -3,   //        1 0 1          
                            -1, -2, 3,   //        1 1 0          
                            2, -3, 4,    //          0 1 0        
                            2, -3, -4,   //          0 1 1        
                            -2, 3, 4,    //          1 0 0        
                            -2, 3, -4,   //          1 0 1        
                            3, 4, -5,    //            0 0 1      
                            3, -4, 5,    //            0 1 0      
                            3, -4, -5,   //            0 1 1      
                            -3, 4, -5,   //            1 0 1      
                            -3, -4, -5,  //            1 1 1      
                            4, -5, -6,   //              0 1 1    
                            -4, 5, 6,    //              1 0 0    
                            -4, -5, 6,   //              1 1 0    
                            -4, -5, -6,  //              1 1 1    
                            5, 6, -7,    //                0 0 1  
                            -5, 6, -7,   //                1 0 1  
                            -5, -6, 7,   //                1 1 0  
                            6, -7, -8,   //                  0 1 1
                            -6, 7, 8     //                  1 0 0
                        });              //       VarCount: 8; ClausesCount: 21; TiersCount: 6
        
        
        assertEquals(21, s1.getClausesCount());
        
        Helper.prettyPrint(s1);
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure) Helper.createFormula(
                new int[] 
                        {                //        h g b e a f c d
                            8, 7, 2,     //        0 0 0          
                            8, -7, -2,   //        0 1 1          
                            -8, -7, 2,   //        1 1 0          
                            7, 2, -5,    //          0 0 1        
                            -7, 2, -5,   //          1 0 1        
                            -7, -2, 5,   //          1 1 0        
                            2, -5, 1,    //            0 1 0      
                            2, -5, -1,   //            0 1 1      
                            -2, 5, -1,   //            1 0 1      
                            -5, 1, 6,    //              0 1 0    
                            -5, 1, -6,   //              1 0 0    
                            5, -1, 6,    //              1 0 1    
                            -5, -1, -6,  //              1 1 1    
                            1, 6, -3,    //                0 0 1  
                            1, -6, -3,   //                0 1 1  
                            -1, 6, 3,    //                1 0 0  
                            -1, -6, -3,  //                1 1 1  
                            6, -3, -4,   //                  0 0 1
                            6, 3, -4,    //                  0 1 1
                            -6, -3, 4,   //                  1 1 0
                            -6, -3, -4   //                  1 1 1
                        });              //       VarCount: 8; ClausesCount: 21; TiersCount: 6
        
        assertEquals(21, s2.getClausesCount());
        
        Helper.prettyPrint(s2);
        
        //  List of ITabularFormula
        ObjectArrayList cts = new ObjectArrayList(new ITabularFormula[] {s1, s2});
        
        Helper.unify(cts);
        
        assertEquals(13, s1.getClausesCount());
        assertEquals(15, s2.getClausesCount());
        
        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);
        
        assertTrue(s1.getTier(0).contains(_001_instance));
        assertTrue(s1.getTier(0).contains(_101_instance));
        assertTrue(s1.getTier(1).contains(_010_instance));
        assertTrue(s1.getTier(1).contains(_011_instance));
        assertTrue(s1.getTier(2).contains(_101_instance));
        assertTrue(s1.getTier(2).contains(_111_instance));
        assertTrue(s1.getTier(3).contains(_011_instance));
        assertTrue(s1.getTier(3).contains(_110_instance));
        assertTrue(s1.getTier(3).contains(_111_instance));
        assertTrue(s1.getTier(4).contains(_101_instance));
        assertTrue(s1.getTier(4).contains(_110_instance));
        assertTrue(s1.getTier(5).contains(_011_instance));
        assertTrue(s1.getTier(5).contains(_100_instance));
        
        assertTrue(s2.getTier(0).contains(_000_instance));
        assertTrue(s2.getTier(0).contains(_110_instance));
        assertTrue(s2.getTier(1).contains(_001_instance));
        assertTrue(s2.getTier(1).contains(_101_instance));
        assertTrue(s2.getTier(2).contains(_010_instance));
        assertTrue(s2.getTier(2).contains(_011_instance));
        assertTrue(s2.getTier(3).contains(_100_instance));
        assertTrue(s2.getTier(3).contains(_101_instance));
        assertTrue(s2.getTier(3).contains(_111_instance));
        assertTrue(s2.getTier(4).contains(_001_instance));
        assertTrue(s2.getTier(4).contains(_011_instance));
        assertTrue(s2.getTier(4).contains(_111_instance));
        assertTrue(s2.getTier(5).contains(_011_instance));
        assertTrue(s2.getTier(5).contains(_110_instance));
        assertTrue(s2.getTier(5).contains(_111_instance));
    }
    
    @Test
    public void testGetCanonicalVarName3()
    {
        assertEquals(3, Helper.getCanonicalVarName3(1, 2, new int[] {1, 2, 3}));
        assertEquals(3, Helper.getCanonicalVarName3(2, 1, new int[] {1, 2, 3}));
        assertEquals(1, Helper.getCanonicalVarName3(2, 3, new int[] {1, 2, 3}));
        assertEquals(1, Helper.getCanonicalVarName3(3, 2, new int[] {1, 2, 3}));
        assertEquals(2, Helper.getCanonicalVarName3(1, 3, new int[] {1, 2, 3}));
        assertEquals(2, Helper.getCanonicalVarName3(3, 1, new int[] {1, 2, 3}));
    }

    @Test
    public void testCreateCTF()
    {
        for (int n = 7; n < 100; n++)
        {
            try
            {
                System.out.println(n);
                ITabularFormula formula = Helper.createRandomFormula(21, n);
                ObjectArrayList ctf = Helper.createCTF(formula);
                Helper.createCTS(formula, ctf);
            }
            catch (EmptyStructureException e)
            {
            }
        }
    }
}