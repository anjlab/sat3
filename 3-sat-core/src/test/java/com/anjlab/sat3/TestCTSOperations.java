/*
 * Copyright (c) 2010 AnjLab
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

import static com.anjlab.sat3.Helper.prettyPrint;
import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestCTSOperations
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestCTSOperations.class.getName());
    }
    
    @Test
    public void testConcretize()
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure)
            Helper.createFormula(
                     new int[]
                         {               //             x1 x2 x3 x4                               
                             1, 2, 3,    //              0  0  0                                  
                             -1, 2, -3,  //              1  0  1                                  
                             2, -3, 4,   //                 0  1  0                               
                             2, 3, -4    //                 0  0  1                               
                         });             //            VarCount: 4; ClausesCount: 4; TiersCount: 2

        Helper.prettyPrint(s1);

        s1.concretize(3, Value.AllNegative);

                                         //             x4 x1 x2 x3
                                         //              0  1  1   
                                         //                 1  1  1
                                         //            VarCount: 4; ClausesCount: 2; TiersCount: 2

        Helper.prettyPrint(s1);

        assertEquals(2, s1.getClausesCount());
        assertEquals(SimpleTripletValueFactory._101_instance, s1.getTier(0).iterator().next());
        assertEquals(SimpleTripletValueFactory._010_instance, s1.getTier(1).iterator().next());
    }

    @Test
    public void testConcretizeWithCleanup()
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {               //             x1 x2 x3 x4 x5                            
                             1, 2, 3,    //              0  0  0                                  
                             -1, 2, -3,  //              1  0  1                                  
                             2, -3, 4,   //                 0  1  0                               
                             2, 3, -4,   //                 0  0  1                               
                             3, -4, 5    //                    0  1  0                            
                         });             //            VarCount: 5; ClausesCount: 5; TiersCount: 3

        Helper.prettyPrint(s1);

        s1.concretize(2, Value.AllNegative);

        Helper.prettyPrint(s1);

        assertTrue(s1.isEmpty());
    }

    @Test
    public void testUnion()
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {              //        a b c d 
                             1, 2, 3,   //        0 0 0   
                             -1, 2, -3, //        1 0 1   
                             2, 3, -4,  //          0 0 1 
                             2, -3, 4   //          0 1 0 
                         });            //       VarCount: 4; ClausesCount: 4; TiersCount: 2 
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {               //        a b c d
                             1, 2, 3,    //        0 0 0  
                             -1, -2, -3, //        1 1 1  
                             2, 3, 4,    //          0 0 0
                             -2, -3, 4   //          1 1 0
                         });             //       VarCount: 4; ClausesCount: 4; TiersCount: 2

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);

        s1.union(s2);
        
                                         //      a b c d
                                         //      0 0 0  
                                         //      1 0 1  
                                         //      1 1 1  
                                         //        0 0 0
                                         //        0 0 1
                                         //        0 1 0
                                         //        1 1 0
                                         //     VarCount: 4; ClausesCount: 7; TiersCount: 2

        Helper.prettyPrint(s1);

        assertEquals(7, s1.getClausesCount());
    }
    
    @Test
    public void testUnionLeftOperandIsEmpty()
    {
        ICompactTripletsStructure s = (ICompactTripletsStructure)
                                      Helper.createFormula(1, 2, 3,
                                                             -2, 3, 4);
        
        s.cleanup();
        
        assertTrue(s.isEmpty());
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                                2, 3, 4);
        
        s2.cleanup();
        
        assertTrue(!s2.isEmpty());
        
        s.union(s2);
        
        assertTrue("Should not be empty", !s.isEmpty());
        assertEquals(s2.getClausesCount(), s.getClausesCount());
    }
    

    @Test
    public void testUnionRightOperandIsEmpty()
    {
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                                2, 3, 4);
        
        s2.cleanup();
        
        assertTrue(!s2.isEmpty());

        ICompactTripletsStructure s = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                               -2, 3, 4);

        s.cleanup();
        
        assertTrue(s.isEmpty());

        s2.union(s);
        
        assertEquals(2, s2.getClausesCount());
    }
    
    @Test
    public void testCleanupFormulaThatHasEmptyTier()
    {
        ICompactTripletsStructure s = (ICompactTripletsStructure)
            Helper.createFormula(
                new int[]
                    {             //        a b c d e                                 
                        1, 2, 3,  //        0 0 0                                     
                        2, 3, 4,  //            0 0 0                                 
                        3, 4, 5   //       VarCount: 5; ClausesCount: 2; TiersCount: 3
                    });
        
        s.getTier(1).remove(SimpleTripletValueFactory._000_instance);
        
        prettyPrint(s);
        
        s.cleanup();
        
        assertTrue(s.isEmpty());
    }

    @Test
    public void testCleanupToEmpty()
    {
        ICompactTripletsStructure s = (ICompactTripletsStructure)
            Helper.createFormula(
                new int[]
                    {               //        a b c d e                                 
                        1, 2, 3,    //        0 0 0                                     
                        2, 3, 4,    //          0 0 0                                   
                        -3, -4, -5  //            1 1 1                                 
                    });             //       VarCount: 5; ClausesCount: 3; TiersCount: 3
        
        prettyPrint(s);
        
        s.cleanup();
        
        assertTrue(s.isEmpty());
    }
    
    @Test
    public void testIntersect()
    {
        ICompactTripletsStructure s1 = (ICompactTripletsStructure)
            Helper.createFormula(
                new int[]
                    {               //        a b c d e                                 
                        1, 2, -3,   //        0 0 1                                     
                        -1, 2, 3,   //        1 0 0                                     
                        2, 3, 4,    //          0 0 0                                   
                        2, -3, -4,  //          0 1 1                                   
                        3, 4, 5,    //            0 0 0                                 
                        -3, -4, -5  //            1 1 1                                 
                    });             //       VarCount: 5; ClausesCount: 6; TiersCount: 3
       
        prettyPrint(s1);
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
            Helper.createFormula(
                new int[]
                    {                //        a b c d e                                 
                        1, 2, 3,     //        0 0 0                                     
                        -1, 2, 3,    //        1 0 0                                     
                        -1, 2, -3,   //        1 0 1                                     
                        2, 3, 4,     //          0 0 0                                   
                        2, -3, 4,    //          0 1 0                                   
                        2, -3, -4,   //          0 1 1                                   
                        3, 4, 5,     //            0 0 0                                 
                        -3, 4, 5,    //            1 0 0                                 
                        -3, -4, -5,  //            1 1 1                                 
                    });              //       VarCount: 5; ClausesCount: 9; TiersCount: 3
       
        prettyPrint(s2);
        
        s1.intersect(s2);
        
                                     //        a b c d e
                                     //        1 0 0    
                                     //          0 0 0  
                                     //            0 0 0
                                     //       VarCount: 5; ClausesCount: 3; TiersCount: 3
       
        prettyPrint(s1);
        
        assertEquals(3, s1.getClausesCount());
        
        ITier _123 = s1.getTier(0);
        assertEquals(1, _123.size());
        assertTrue(_123.contains(_100_instance));

        ITier _234 = s1.getTier(1);
        assertEquals(1, _234.size());
        assertTrue(_234.contains(_000_instance));
        
        ITier _345 = s1.getTier(2);
        assertEquals(1, _345.size());
        assertTrue(_345.contains(_000_instance));
    }
    
    @Test
    public void testIntersectLeftOperandIsEmpty()
    {
        ICompactTripletsStructure s = (ICompactTripletsStructure)
                                      Helper.createFormula(1, 2, 3,
                                                             -2, 3, 4);
        
        s.cleanup();
        
        assertTrue(s.isEmpty());
        
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                                2, 3, 4);
        
        s2.cleanup();
        
        assertTrue(!s2.isEmpty());
        
        s.intersect(s2);
        
        assertTrue(s.isEmpty());
    }
    

    @Test
    public void testIntersectRightOperandIsEmpty()
    {
        ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                                2, 3, 4);
        
        s2.cleanup();
        
        assertTrue(!s2.isEmpty());

        ICompactTripletsStructure s = (ICompactTripletsStructure)
                                        Helper.createFormula(1, 2, 3,
                                                               -2, 3, 4);

        s.cleanup();
        
        assertTrue(s.isEmpty());

        s2.intersect(s);
        
        assertTrue(s2.isEmpty());
    }
    
    @Test
    public void testCompleteCTF2CTS() throws Exception
    {
        ITabularFormula formula = Helper.createFormula(
                new int[] {
                        1, 2, 3,
                        1, -2, 3,
                        -1, 2, 3,
                        2, 3, 4,
                        2, -3, 4,
                        -2, -3, -4
                });
        
        Helper.prettyPrint(formula);
        
        formula.complete(SimplePermutation.createPermutation(
                new int[] {1, 2, 3, 4, 5, 6}));
        
        assertEquals(6, formula.getVarCount());

        Helper.prettyPrint(formula);

        assertTrue(!formula.getTier(0).contains(_000_instance));
        assertTrue(!formula.getTier(0).contains(_010_instance));
        assertTrue(!formula.getTier(0).contains(_100_instance));
        assertEquals(5, formula.getTier(0).size());
        assertTrue(!formula.getTier(1).contains(_000_instance));
        assertTrue(!formula.getTier(1).contains(_010_instance));
        assertTrue(!formula.getTier(1).contains(_111_instance));
        assertEquals(4, formula.getTier(1).size());  //  Cleanup
        assertEquals(8, formula.getTier(2).size());
        assertEquals(8, formula.getTier(2).size());
    }
    
    @Test
    public void testCleanupFromTo()
    {
        SimpleFormula formula = (SimpleFormula)
                                Helper.createFormula(1,  2,  3, 
                                                     1,  2, -3,
                                                     1, -2, -3,
                                                         2, -3, -4,
                                                        -2, -3,  4,
                                                         2,  3, -4,
                                                             3, -4,  5, 
                                                            -3, -4, -5,
                                                            -3,  4,  5);
        
        Helper.prettyPrint(formula);
        
        formula.getTier(1).remove(_001_instance);
        
        formula.cleanup(1, 1);
        
        Helper.prettyPrint(formula);
        
        Assert.assertEquals(6, formula.getClausesCount());
    }
    
    @Test
    public void testElementary()
    {
        ICompactTripletsStructure cts = (ICompactTripletsStructure) 
                        Helper.createFormula(1, 2, -3,
                                                2, -3, 4, 
                                                   -3, 4, 5, 
                                                       4, 5, -6);
        assertTrue(cts.isElementary());
        
        ICompactTripletsStructure ctsNotAllTiersPresent = (ICompactTripletsStructure) 
                        Helper.createFormula(1, 2, -3,
                                                2, -3, 4, 
                                                       4, 5, -6);
        
        assertFalse(ctsNotAllTiersPresent.isElementary());
    }
}
