package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.createFormula;
import static com.anjlab.sat3.SimplePermutation.createPermutation;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

public class TestJoinMethods
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        Helper.UseUniversalVarNames = true;
        System.out.println(TestJoinMethods.class.getName());
    }

    @Test
    public void testJoin2BetweenTiers() throws Throwable
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 3; a2 <= 5; a2++)
            for (int b2 = 3; b2 <= 5; b2++)
            for (int c2 = 3; c2 <= 5; c2++)
                for (int a3 = 5; a3 <= 7; a3++)
                for (int b3 = 5; b3 <= 7; b3++)
                for (int c3 = 5; c3 <= 7; c3++)
                {
                    if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                    if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                    if (a3 == b3 || a3 == c3 || b3 == c3) continue;
                    
                    Join2BetweenTiers method = new Join2BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    SimpleTier tier = new SimpleTier(a2, b2, c2);
                    tier.add(_101_instance);
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertEquals(3, formula.getPermutation().get(2));
                        assertEquals(4, formula.getPermutation().get(3));
                        assertEquals(5, formula.getPermutation().get(4));
                        
                        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7));
                        
                        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                    }
                    catch (Throwable t)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw t;
                    }
                }
    }

    @Test
    public void testJoin2BetweenTiers_BitsAreOnTheSamePlaces() throws Throwable
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -5; a2 <= 5; a2 = a2 == -3 ? 3 : a2+1)
            for (int b2 = -5; b2 <= 5; b2 = b2 == -3 ? 3 : b2+1)
            for (int c2 = -5; c2 <= 5; c2 = c2 == -3 ? 3 : c2+1)
                for (int a3 = -7; a3 <= 7;  a3 = a3 == -5 ? 5 : a3+1)
                for (int b3 = -7; b3 <= 7;  b3 = b3 == -5 ? 5 : b3+1)
                for (int c3 = -7; c3 <= 7;  c3 = c3 == -5 ? 5 : c3+1)
                {
                    if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                    if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                    if (Math.abs(a3) == Math.abs(b3) || Math.abs(a3) == Math.abs(c3) || Math.abs(b3) == Math.abs(c3)) continue;
                    
                    Join2BetweenTiers method = new Join2BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    
                    ITier tier0 = formula.getTiers().get(0);
                    ITier tier1 = formula.getTiers().get(1);
                    
                    SimpleTier tier = new SimpleTier(Math.abs(a2), Math.abs(b2), Math.abs(c2));
                    tier.add(new SimpleTriplet(a2, b2, c2));
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertTrue("Debugger", true);
                        
                        assertValueEquals(tier0, a1);
                        assertValueEquals(tier0, b1);
                        assertValueEquals(tier0, c1);
                        
                        assertValueEquals(tier, a2);
                        assertValueEquals(tier, b2);
                        assertValueEquals(tier, c2);
                        
                        assertValueEquals(tier1, a3);
                        assertValueEquals(tier1, b3);
                        assertValueEquals(tier1, c3);
                    }
                    catch (Throwable t)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw t;
                    }
                }
    }

    private void assertValueEquals(ITier tier, int varName)
    {
        if (tier.getAName() == Math.abs(varName))
        {
            if (varName < 0) assertEquals(Value.AllNegative, tier.valueOfA()); else
                        assertEquals(Value.AllPlain, tier.valueOfA());
        }
        else if (tier.getBName() == Math.abs(varName))
        {
            if (varName < 0) assertEquals(Value.AllNegative, tier.valueOfB()); else
                        assertEquals(Value.AllPlain, tier.valueOfB());
        }
        else if (tier.getCName() == Math.abs(varName))
        {
            if (varName < 0) assertEquals(Value.AllNegative, tier.valueOfC()); else
                        assertEquals(Value.AllPlain, tier.valueOfC());
        }
    }
    
    @Test
    public void testJoin2BetweenTiers_ThreeTiers() throws Throwable
    {
        Join2BetweenTiers method = new Join2BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                   2, 3, 4, 
                                                            6, 7, 8);
        SimpleTier tier = new SimpleTier(4, 5, 6);
        tier.add(_101_instance);
        try
        {
            assertTrue("Should join", method.tryJoin(formula, tier));
            assertTrue(formula.getPermutation().sameAs(
                    createPermutation(1, 2, 3, 4, 5, 6, 7, 8)));
            
            formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8));
            
            assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
        }
        catch (AssertionError e)
        {
            System.out.println("Formula: " + formula);
            System.out.println("Tier: " + tier);
            
            throw e;
        }
    }

    @Test
    public void testJoin2BetweenTiers_ThreeTiers2() throws Throwable
    {
        Join2BetweenTiers method = new Join2BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                      3, 5, 4, 
                                                               8, 7, 9);
        SimpleTier tier = new SimpleTier(5, 6, 7);
        tier.add(_101_instance);
        try
        {
            assertTrue("Should join", method.tryJoin(formula, tier));
            assertTrue(formula.getPermutation().sameAs(
                    createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9)));
            
            formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9));
            
            assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
        }
        catch (AssertionError e)
        {
            System.out.println("Formula: " + formula);
            System.out.println("Tier: " + tier);
            
            throw e;
        }
    }

    public void testJoin2BetweenTiers_ThreeTiers3() throws Throwable
    {
        Join2BetweenTiers method = new Join2BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                      3, 5, 4, 
                                                               7, 8, 9,
                                                                  8, 9, 10);
        SimpleTier tier = new SimpleTier(5, 6, 7);
        tier.add(_101_instance);
        try
        {
            assertTrue("Should join", method.tryJoin(formula, tier));
            assertTrue(formula.getPermutation().sameAs(
                    createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)));
            
            formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            
            assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
        }
        catch (AssertionError e)
        {
            System.out.println("Formula: " + formula);
            System.out.println("Tier: " + tier);
            
            throw e;
        }
    }

    @Test
    public void testJoin2BetweenTiers_ThreeTiers4() throws EmptyStructureException
    {
        Join2BetweenTiers method = new Join2BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                      3, 5, 4, 
                                                               11, 12, 13,
                                                                           7, 8, 9,
                                                                              8, 9, 10);
        SimpleTier tier = new SimpleTier(5, 6, 7);
        tier.add(_101_instance);
        try
        {
            assertTrue("Should join", method.tryJoin(formula, tier));
            
            assertTrue("Permutation should match", formula.getPermutation().sameAs(
                    createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13)));
            
            formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13));
            
            assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
        }
        catch (AssertionError e)
        {
            System.out.println("Formula: " + formula);
            System.out.println("Tier: " + tier);
            
            throw e;
        }
    }
    
    @Test
    public void tryJoin3BetweenTiers_2Left1Right() throws Throwable
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 1; a2 <= 3; a2++)
            for (int b2 = 1; b2 <= 3; b2++)
            for (int c2 = 4; c2 <= 6; c2++)
                for (int a3 = 4; a3 <= 6; a3++)
                for (int b3 = 4; b3 <= 6; b3++)
                for (int c3 = 4; c3 <= 6; c3++)
                {
                    if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                    if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                    if (a3 == b3 || a3 == c3 || b3 == c3) continue;
                    
                    Join3BetweenTiers method = new Join3BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    SimpleTier tier = new SimpleTier(a2, b2, c2);
                    tier.add(_101_instance);
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertEquals(1, formula.getPermutation().indexOf(tier.getAName()));
                        assertEquals(2, formula.getPermutation().indexOf(tier.getBName()));
                        assertEquals(3, formula.getPermutation().indexOf(tier.getCName()));
                        
                        formula.complete(createPermutation(1, 2, 3, 4, 5, 6));
                        
                        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                    }
                    catch (Throwable t)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw t;
                    }
                }
    }

    @Test
    public void tryJoin3BetweenTiers_2Left1Right_BitsAreOnTheSamePlaces() throws Throwable
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -3; a2 <= 3; a2 = a2 == -1 ? 1 : a2+1)
            for (int b2 = -3; b2 <= 3; b2 = b2 == -1 ? 1 : b2+1)
            for (int c2 = -6; c2 <= 6; c2 = c2 == -4 ? 4 : c2+1)
                for (int a3 = -6; a3 <= 6; a3 = a3 == -4 ? 4 : a3+1)
                for (int b3 = -6; b3 <= 6; b3 = b3 == -4 ? 4 : b3+1)
                for (int c3 = -6; c3 <= 6; c3 = c3 == -4 ? 4 : c3+1)
                {
                    if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                    if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                    if (Math.abs(a3) == Math.abs(b3) || Math.abs(a3) == Math.abs(c3) || Math.abs(b3) == Math.abs(c3)) continue;
                    
                    Join3BetweenTiers method = new Join3BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    ITier tier0 = formula.getTiers().get(0);
                    ITier tier1 = formula.getTiers().get(1);
                    
                    SimpleTier tier = new SimpleTier(Math.abs(a2), Math.abs(b2), Math.abs(c2));
                    tier.add(new SimpleTriplet(a2, b2, c2));
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertTrue("Debugger", true);
                        
                        assertValueEquals(tier0, a1);
                        assertValueEquals(tier0, b1);
                        assertValueEquals(tier0, c1);
                        
                        assertValueEquals(tier, a2);
                        assertValueEquals(tier, b2);
                        assertValueEquals(tier, c2);
                        
                        assertValueEquals(tier1, a3);
                        assertValueEquals(tier1, b3);
                        assertValueEquals(tier1, c3);
                    }
                    catch (Throwable t)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw t;
                    }
                }
    }

    @Test
    public void testJoin3BetweenTiers_2Left1Right_ThreeTiers() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                         4, 5, 6,
                                                                  7, 8, 9);
        SimpleTier tier = new SimpleTier(2, 3, 7);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(1, 2, 3, 7, 8, 9, 4, 5, 6)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }

    @Test
    public void testJoin3BetweenTiers_2Left1Right_ThreeTiers2() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                   2, 3, 4,
                                                         4, 6, 5,
                                                                  7, 8, 9);
        SimpleTier tier = new SimpleTier(5, 6, 7);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(1, 2, 3, 4, 6, 5, 7, 8, 9)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }
    
    @Test
    public void testJoin3BetweenTiers_2Left1Right_ThreeTiers3() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                         4, 5, 6,
                                                            5, 6, 7, 
                                                                     8, 9, 10);
        SimpleTier tier = new SimpleTier(2, 3, 8);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(1, 2, 3, 8, 9, 10, 4, 5, 6, 7)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }

    @Test
    public void tryJoin3BetweenTiers_1Left2Right() throws EmptyStructureException
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 1; a2 <= 3; a2++)
            for (int b2 = 4; b2 <= 6; b2++)
            for (int c2 = 4; c2 <= 6; c2++)
                for (int a3 = 4; a3 <= 6; a3++)
                for (int b3 = 4; b3 <= 6; b3++)
                for (int c3 = 4; c3 <= 6; c3++)
                {
                    if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                    if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                    if (a3 == b3 || a3 == c3 || b3 == c3) continue;
                    
                    Join3BetweenTiers method = new Join3BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    SimpleTier tier = new SimpleTier(a2, b2, c2);
                    tier.add(_101_instance);
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertEquals(2, formula.getPermutation().indexOf(tier.getAName()));
                        assertEquals(3, formula.getPermutation().indexOf(tier.getBName()));
                        assertEquals(4, formula.getPermutation().indexOf(tier.getCName()));
                        
                        formula.complete(createPermutation(1, 2, 3, 4, 5, 6));
                        
                        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                    }
                    catch (AssertionError e)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw e;
                    }
                }
    }

    @Test
    public void tryJoin3BetweenTiers_1Left2Right_BitsAreOnTheSamePlaces() throws EmptyStructureException
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -3; a2 <= 3; a2 = a2 == -1 ? 1 : a2+1)
            for (int b2 = -6; b2 <= 6; b2 = b2 == -4 ? 4 : b2+1)
            for (int c2 = -6; c2 <= 6; c2 = c2 == -4 ? 4 : c2+1)
                for (int a3 = -6; a3 <= 6; a3 = a3 == -4 ? 4 : a3+1)
                for (int b3 = -6; b3 <= 6; b3 = b3 == -4 ? 4 : b3+1)
                for (int c3 = -6; c3 <= 6; c3 = c3 == -4 ? 4 : c3+1)
                {
                    if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                    if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                    if (Math.abs(a3) == Math.abs(b3) || Math.abs(a3) == Math.abs(c3) || Math.abs(b3) == Math.abs(c3)) continue;
                    
                    Join3BetweenTiers method = new Join3BetweenTiers();
                    ITabularFormula formula = createFormula(a1, b1, c1, a3, b3, c3);
                    ITier tier0 = formula.getTiers().get(0);
                    ITier tier1 = formula.getTiers().get(1);
                    
                    SimpleTier tier = new SimpleTier(Math.abs(a2), Math.abs(b2), Math.abs(c2));
                    tier.add(new SimpleTriplet(a2, b2, c2));
                    try
                    {
                        assertTrue("Should join", method.tryJoin(formula, tier));
                        assertTrue("Debugger", true);
                        
                        assertValueEquals(tier0, a1);
                        assertValueEquals(tier0, b1);
                        assertValueEquals(tier0, c1);
                        
                        assertValueEquals(tier, a2);
                        assertValueEquals(tier, b2);
                        assertValueEquals(tier, c2);
                        
                        assertValueEquals(tier1, a3);
                        assertValueEquals(tier1, b3);
                        assertValueEquals(tier1, c3);
                    }
                    catch (AssertionError e)
                    {
                        System.out.println("Formula: " + formula);
                        System.out.println("Tier: " + tier);
                        
                        throw e;
                    }
                }
    }

    @Test
    public void testJoin3BetweenTiers_1Left2Right_ThreeTiers() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                         4, 5, 6,
                                                                  7, 8, 9);
        SimpleTier tier = new SimpleTier(3, 7, 8);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(4, 5, 6, 1, 2, 3, 7, 8, 9)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }

    @Test
    public void testJoin3BetweenTiers_1Left2Right_ThreeTiers2() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                   2, 3, 4,
                                                         4, 6, 5,
                                                                  7, 8, 9);
        SimpleTier tier = new SimpleTier(6, 7, 8);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }
    
    @Test
    public void testJoin3BetweenTiers_1Left2Right_ThreeTiers3() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 2, 3, 
                                                         4, 5, 6,
                                                            5, 6, 7, 
                                                                     8, 9, 10);
        SimpleTier tier = new SimpleTier(3, 8, 9);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(4, 5, 6, 7, 1, 2, 3, 8, 9, 10)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }
    
    @Test
    public void testJoin3BetweenTiers_1Left2Right_ThreeTiers4() throws EmptyStructureException
    {
        Join3BetweenTiers method = new Join3BetweenTiers();
        ITabularFormula formula = createFormula(1, 3, 8, 
                                                         5, 6, 7);
        SimpleTier tier = new SimpleTier(6, 8, 7);
        tier.add(_101_instance);
        
        assertTrue("Should join", method.tryJoin(formula, tier));
        
        assertTrue("Permutation should match", formula.getPermutation().sameAs(
                createPermutation(1, 3, 8, 6, 7, 5)));
        
        formula.complete(createPermutation(1, 2, 3, 4, 5, 6, 7, 8));
        
        assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
    }

    @Test
    public void tryJoin1Left() throws EmptyStructureException
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 3; a2 <= 5; a2++)
            for (int b2 = 3; b2 <= 5; b2++)
            for (int c2 = 3; c2 <= 5; c2++)
            {
                if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                
                Join1Left method = new Join1Left();
                ITabularFormula formula = createFormula(a2, b2, c2);
                SimpleTier tier = new SimpleTier(a1, b1, c1);
                tier.add(_101_instance);
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertEquals(0, formula.getPermutation().indexOf(tier.getAName()));
                    assertEquals(1, formula.getPermutation().indexOf(tier.getBName()));
                    assertEquals(2, formula.getPermutation().indexOf(tier.getCName()));
                    
                    formula.complete(createPermutation(1, 2, 3, 4, 5));
                    
                    assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin1Left_BitsAreOnTheSamePlaces() throws EmptyStructureException
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -5; a2 <= 5; a2 = a2 == -3 ? 3 : a2+1)
            for (int b2 = -5; b2 <= 5; b2 = b2 == -3 ? 3 : b2+1)
            for (int c2 = -5; c2 <= 5; c2 = c2 == -3 ? 3 : c2+1)
            {
                if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                
                Join1Left method = new Join1Left();
                ITabularFormula formula = createFormula(a2, b2, c2);
                ITier tier0 = formula.getTiers().get(0);
                
                SimpleTier tier = new SimpleTier(Math.abs(a1), Math.abs(b1), Math.abs(c1));
                tier.add(new SimpleTriplet(a1, b1, c1));
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertTrue("Debugger", true);
                    
                    assertValueEquals(tier0, a2);
                    assertValueEquals(tier0, b2);
                    assertValueEquals(tier0, c2);
                    
                    assertValueEquals(tier, a1);
                    assertValueEquals(tier, b1);
                    assertValueEquals(tier, c1);
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin1Right() throws EmptyStructureException
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 3; a2 <= 5; a2++)
            for (int b2 = 3; b2 <= 5; b2++)
            for (int c2 = 3; c2 <= 5; c2++)
            {
                if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                
                Join1Right method = new Join1Right();
                ITabularFormula formula = createFormula(a2, b2, c2);
                SimpleTier tier = new SimpleTier(a1, b1, c1);
                tier.add(_101_instance);
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertEquals(2, formula.getPermutation().indexOf(tier.getAName()));
                    assertEquals(3, formula.getPermutation().indexOf(tier.getBName()));
                    assertEquals(4, formula.getPermutation().indexOf(tier.getCName()));
                    
                    formula.complete(createPermutation(1, 2, 3, 4, 5));
                    
                    assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin1Right_BitsAreOnTheSamePlaces() throws EmptyStructureException
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -5; a2 <= 5; a2 = a2 == -3 ? 3 : a2+1)
            for (int b2 = -5; b2 <= 5; b2 = b2 == -3 ? 3 : b2+1)
            for (int c2 = -5; c2 <= 5; c2 = c2 == -3 ? 3 : c2+1)
            {
                if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                
                Join1Right method = new Join1Right();
                ITabularFormula formula = createFormula(a2, b2, c2);
                ITier tier0 = formula.getTiers().get(0);
                
                SimpleTier tier = new SimpleTier(Math.abs(a1), Math.abs(b1), Math.abs(c1));
                tier.add(new SimpleTriplet(a1, b1, c1));
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertTrue("Debugger", true);
                    
                    assertValueEquals(tier0, a2);
                    assertValueEquals(tier0, b2);
                    assertValueEquals(tier0, c2);
                    
                    assertValueEquals(tier, a1);
                    assertValueEquals(tier, b1);
                    assertValueEquals(tier, c1);
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin2Left() throws EmptyStructureException
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 2; a2 <= 4; a2++)
            for (int b2 = 2; b2 <= 4; b2++)
            for (int c2 = 2; c2 <= 4; c2++)
            {
                if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                
                Join2Left method = new Join2Left();
                ITabularFormula formula = createFormula(a2, b2, c2);
                SimpleTier tier = new SimpleTier(a1, b1, c1);
                tier.add(_000_instance);
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertEquals(0, formula.getPermutation().indexOf(tier.getAName()));
                    assertEquals(1, formula.getPermutation().indexOf(tier.getBName()));
                    assertEquals(2, formula.getPermutation().indexOf(tier.getCName()));
                    
                    formula.complete(createPermutation(1, 2, 3, 4));
                    
                    assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin2Left_BitsOnTheirPlaces() throws EmptyStructureException
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -4; a2 <= 4; a2 = a2 == -2 ? 2 : a2+1)
            for (int b2 = -4; b2 <= 4; b2 = b2 == -2 ? 2 : b2+1)
            for (int c2 = -4; c2 <= 4; c2 = c2 == -2 ? 2 : c2+1)
            {
                if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                
                Join2Left method = new Join2Left();
                ITabularFormula formula = createFormula(a2, b2, c2);
                ITier tier0 = formula.getTiers().get(0);
                
                SimpleTier tier = new SimpleTier(Math.abs(a1), Math.abs(b1), Math.abs(c1));
                tier.add(new SimpleTriplet(a1, b1, c1));
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertTrue("Debugger", true);
                    
                    assertValueEquals(tier0, a2);
                    assertValueEquals(tier0, b2);
                    assertValueEquals(tier0, c2);
                    
                    assertValueEquals(tier, a1);
                    assertValueEquals(tier, b1);
                    assertValueEquals(tier, c1);
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }
    
    @Test
    public void tryJoin2Right() throws EmptyStructureException
    {
        for (int a1 = 1; a1 <= 3; a1++)
        for (int b1 = 1; b1 <= 3; b1++)
        for (int c1 = 1; c1 <= 3; c1++)
            for (int a2 = 2; a2 <= 4; a2++)
            for (int b2 = 2; b2 <= 4; b2++)
            for (int c2 = 2; c2 <= 4; c2++)
            {
                if (a1 == b1 || a1 == c1 || b1 == c1) continue;
                if (a2 == b2 || a2 == c2 || b2 == c2) continue;
                
                Join2Right method = new Join2Right();
                ITabularFormula formula = createFormula(a2, b2, c2);
                SimpleTier tier = new SimpleTier(a1, b1, c1);
                tier.add(_000_instance);
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertEquals(1, formula.getPermutation().indexOf(tier.getAName()));
                    assertEquals(2, formula.getPermutation().indexOf(tier.getBName()));
                    assertEquals(3, formula.getPermutation().indexOf(tier.getCName()));
                    
                    formula.complete(createPermutation(1, 2, 3, 4));
                    
                    assertTrue(((ICompactTripletsStructure)formula).tiersSorted());
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }

    @Test
    public void tryJoin2Right_BitsOnTheirPlaces() throws EmptyStructureException
    {
        for (int a1 = -3; a1 <= 3; a1 = a1 == -1 ? 1 : a1+1)
        for (int b1 = -3; b1 <= 3; b1 = b1 == -1 ? 1 : b1+1)
        for (int c1 = -3; c1 <= 3; c1 = c1 == -1 ? 1 : c1+1)
            for (int a2 = -4; a2 <= 4; a2 = a2 == -2 ? 2 : a2+1)
            for (int b2 = -4; b2 <= 4; b2 = b2 == -2 ? 2 : b2+1)
            for (int c2 = -4; c2 <= 4; c2 = c2 == -2 ? 2 : c2+1)
            {
                if (Math.abs(a1) == Math.abs(b1) || Math.abs(a1) == Math.abs(c1) || Math.abs(b1) == Math.abs(c1)) continue;
                if (Math.abs(a2) == Math.abs(b2) || Math.abs(a2) == Math.abs(c2) || Math.abs(b2) == Math.abs(c2)) continue;
                
                Join2Right method = new Join2Right();
                ITabularFormula formula = createFormula(a2, b2, c2);
                ITier tier0 = formula.getTiers().get(0);
                
                SimpleTier tier = new SimpleTier(Math.abs(a1), Math.abs(b1), Math.abs(c1));
                tier.add(new SimpleTriplet(a1, b1, c1));
                try
                {
                    assertTrue("Should join", method.tryJoin(formula, tier));
                    assertTrue("Debugger", true);
                    
                    assertValueEquals(tier0, a2);
                    assertValueEquals(tier0, b2);
                    assertValueEquals(tier0, c2);
                    
                    assertValueEquals(tier, a1);
                    assertValueEquals(tier, b1);
                    assertValueEquals(tier, c1);
                }
                catch (AssertionError e)
                {
                    System.out.println("Formula: " + formula);
                    System.out.println("Tier: " + tier);
                    
                    throw e;
                }
            }
    }
}

