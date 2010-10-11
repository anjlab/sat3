package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.prettyPrint;
import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(SimpleTripletValueFactory._101_instance, s1.getTiers().get(0).iterator().next());
        assertEquals(SimpleTripletValueFactory._010_instance, s1.getTiers().get(1).iterator().next());
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
        
        ICompactTripletsStructure s3 = s1.union(s2);
        
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
        Helper.prettyPrint(s2);
        Helper.prettyPrint(s3);

        assertEquals(7, s3.getClausesCount());
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
        
        s.getTiers().get(1).remove(SimpleTripletValueFactory._000_instance);
        
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
        
        ICompactTripletsStructure s3 = s1.intersect(s2);
        
                                     //        a b c d e
                                     //        1 0 0    
                                     //          0 0 0  
                                     //            0 0 0
                                     //       VarCount: 5; ClausesCount: 3; TiersCount: 3
       
        prettyPrint(s3);
        
        assertEquals(3, s3.getClausesCount());
        
        ITier _123 = s3.getTiers().get(0);
        assertEquals(1, _123.size());
        assertTrue(_123.contains(_100_instance));

        ITier _234 = s3.getTiers().get(1);
        assertEquals(1, _234.size());
        assertTrue(_234.contains(_000_instance));
        
        ITier _345 = s3.getTiers().get(2);
        assertEquals(1, _345.size());
        assertTrue(_345.contains(_000_instance));
    }
}
