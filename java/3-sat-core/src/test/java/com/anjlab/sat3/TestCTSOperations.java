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
//            VarCount: 4; ClausesCount: 4; TiersCount: 2

    	ICompactTripletsStructure s2 = s1.concretize(3, true);

//             x4 x1 x2 x3
//              0  1  1   
//                 1  1  1
//            VarCount: 4; ClausesCount: 2; TiersCount: 2

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);

        assertEquals(2, s2.getClausesCount());
        assertEquals(SimpleTripletValueFactory._101_instance, s2.getTiers().get(0).iterator().next());
        assertEquals(SimpleTripletValueFactory._010_instance, s2.getTiers().get(1).iterator().next());
    }

    @Test
    public void testConcretizeWithCleanup()
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
//            VarCount: 5; ClausesCount: 5; TiersCount: 3

    	ICompactTripletsStructure s2 = s1.concretize(2, true);

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);

        assertTrue(s2.isEmpty());
    }

    @Test
    public void testUnion()
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
//            VarCount: 4; ClausesCount: 4; TiersCount: 2

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
//            VarCount: 4; ClausesCount: 4; TiersCount: 2

        Helper.prettyPrint(s1);
        Helper.prettyPrint(s2);
    }
    
    @Test
    public void testSubtract()
    {
    	ITier t1 = SimpleTier.createCompleteTier(1, 2, 3);
    	
    	ITier t2 = new SimpleTier(1, 2, 3);
    	t2.add(SimpleTripletValueFactory._001_instance);
    	t2.add(SimpleTripletValueFactory._101_instance);
    	
    	t1.subtract(t2);
    	
    	assertEquals(6, t1.size());
    	
    	assertTrue(t1.contains(SimpleTripletValueFactory._000_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._010_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._011_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._100_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._110_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._111_instance));
    	
    	assertTrue(!t1.contains(SimpleTripletValueFactory._001_instance));
    	assertTrue(!t1.contains(SimpleTripletValueFactory._101_instance));
    }
    
    @Test
    public void testAdjoinRight()
    {
    	ITier t1 = new SimpleTier(1, 2, 3);
    	t1.add(SimpleTripletValueFactory._000_instance);
    	t1.add(SimpleTripletValueFactory._001_instance);
    	t1.add(SimpleTripletValueFactory._011_instance);
    	
    	ITier t2 = new SimpleTier(2, 3, 4);
    	t2.add(SimpleTripletValueFactory._001_instance);
    	t2.add(SimpleTripletValueFactory._001_instance);
    	t2.add(SimpleTripletValueFactory._111_instance);
    	
    	t1.adjoinRight(t2);
    	
    	assertEquals(2, t1.size());
    	assertTrue(t1.contains(SimpleTripletValueFactory._000_instance));
    	assertTrue(t1.contains(SimpleTripletValueFactory._011_instance));
    	
    	assertTrue(!t1.contains(SimpleTripletValueFactory._001_instance));
    }
    
    @Test
    public void testAdjoinLeft()
    {
    	ITier t1 = new SimpleTier(1, 2, 3);
    	t1.add(SimpleTripletValueFactory._001_instance);
    	t1.add(SimpleTripletValueFactory._011_instance);
    	t1.add(SimpleTripletValueFactory._111_instance);

    	ITier t2 = new SimpleTier(2, 3, 4);
    	t2.add(SimpleTripletValueFactory._000_instance);
    	t2.add(SimpleTripletValueFactory._001_instance);
    	t2.add(SimpleTripletValueFactory._011_instance);

    	t2.adjoinLeft(t1);
    	
    	assertEquals(1, t2.size());
    	assertTrue(t2.contains(SimpleTripletValueFactory._011_instance));
    	
    	assertTrue(!t2.contains(SimpleTripletValueFactory._000_instance));
    	assertTrue(!t2.contains(SimpleTripletValueFactory._001_instance));
    }

}
