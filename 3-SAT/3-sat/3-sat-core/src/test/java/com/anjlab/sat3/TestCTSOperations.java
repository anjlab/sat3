package com.anjlab.sat3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestCTSOperations
{
    @Test
    public void testConcretize()
    {
    	ICompactTripletsStructure s1 = (ICompactTripletsStructure)
    		Helper.createFormula(
                     new int[]
                         {
                             1, 2, 3,
                             -1, 2, -3,
                             2, -3, 4,
                             2, 3, -4
                         });

//             x1 x2 x3 x4
//              0  0  0   
//              1  0  1   
//                 0  1  0
//                 0  0  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

    	ICompactTripletsStructure s2 = s1.concretize(3, true);

//             x4 x1 x2 x3
//              0  1  1   
//                 1  1  1
//            VarCount: 4; TermCount: 2; TiersCount: 2

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);

        assertEquals(2, s2.getTermCount());
        assertEquals(SimpleTripletValueFactory._101_instance, s2.getTiers().get(0).iterator().next());
        assertEquals(SimpleTripletValueFactory._010_instance, s2.getTiers().get(1).iterator().next());
    }

    @Test
    public void TestConcretizeWithCleanup()
    {
    	ICompactTripletsStructure s1 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {
                             1, 2, 3, 
                             -1, 2, -3,
                             2, -3, 4,
                             2, 3, -4,
                             -4, 3, 5
                         });

//             x1 x2 x3 x4 x5
//              0  0  0      
//              1  0  1      
//                 0  1  0   
//                 0  0  1   
//                    0  1  0
//            VarCount: 5; TermCount: 5; TiersCount: 3

    	ICompactTripletsStructure s2 = s1.concretize(2, true);

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);

        assertTrue(s2.isEmpty());
    }

    @Test
    public void TestUnion()
    {
    	ICompactTripletsStructure s1 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {
                             1, 2, 3, 
                             -1, 2, -3,
                             2, -3, 4,
                             2, 3, -4
                         });

//             x1 x2 x3 x4
//              0  0  0   
//              1  0  1   
//                 0  1  0
//                 0  0  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

    	ICompactTripletsStructure s2 = (ICompactTripletsStructure)
                 Helper.createFormula(
                     new int[]
                         {
                             4, 1, 2,
                             4, -1, -2, 
                             1, 2, 3,
                             -1, -2, -3
                         });

//             x4 x1 x2 x3
//              0  0  0   
//              0  1  1   
//                 0  0  0
//                 1  1  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);
    }
}
