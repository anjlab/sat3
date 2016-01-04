package com.anjlab.sat3.v2;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ICompactTripletsStructure;
import com.anjlab.sat3.ITabularFormula;
import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public class TestSystem
{
    @BeforeClass
    public static void beforeClass()
    {
        Helper.UsePrettyPrint = true;
        Helper2.UsePrettyPrint = true;
        Helper2.UseUniversalVarNames = false;
    }

    @Test
    public void testValueOf()
    {
        ISystem system = createTestSystem();
        
        Assert.assertEquals(Value.Mixed, system.valueOf(1));
        Assert.assertEquals(Value.AllPlain, system.valueOf(2));
        Assert.assertEquals(Value.AllNegative, system.valueOf(3));
        Assert.assertEquals(Value.Mixed, system.valueOf(4));
        Assert.assertEquals(Value.AllNegative, system.valueOf(5));
        Assert.assertEquals(Value.Mixed, system.valueOf(6));
        Assert.assertEquals(Value.Mixed, system.valueOf(7));
        Assert.assertEquals(Value.Mixed, system.valueOf(8));
    }

    @Test
    public void testFixConstantValues()
    {
        SimpleSystem system = createTestSystem();
        
        system.fixConstantValues();
        
        Assert.assertEquals("_01_1___", system.getCIC().toString());
        
        Helper2.prettyPrint(system);
    }
    
    @Test
    public void smokeTestSolve()
    {
        SimpleSystem system = createTestSystem();
        
        Helper.UseUniversalVarNames = true;
        Helper2.UseUniversalVarNames = true;
        
        AtomicReference<ISystem> reference = new AtomicReference<ISystem>(system);
        boolean sat = new Solver().solve(reference);
        
        Helper2.printVariablesSideBySide(reference.get(), 0, 1);
        Helper.UseUniversalVarNames = false;
        Helper2.UseUniversalVarNames = false;
        
        Assert.assertTrue(sat);
    }
    
    private SimpleSystem createTestSystem()
    {
        //  Examples taken from
        //  http://arxiv.org/pdf/1309.6078v1.pdf
        
        int a = 1, b = 2, c = 3, d = 4, e = 5, f = 6, g = 7, h = 8;
        
        ITabularFormula s1_table4 = Helper.createFormula(
                a, b, -c,
                -a, b, -c,
                b, -c, d,
                b, -c, -d,
                -c, d, -e,
                -c, -d, -e,
                d, -e, -f,
                -d, -e, f,
                -d, -e, -f,
                -e, f, -g,
                -e, -f, g,
                f, -g, -h,
                -f, g, h);
        
        ICompactCouplesStructure g1_table7 =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) s1_table4);
        
        Helper2.prettyPrint(g1_table7);
        
        ITabularFormula s2_table4 = Helper.createFormula(
                h, g, b,
                -h, -g, b,
                g, b, -e,
                -g, b, -e,
                b, -e, a,
                b, -e, -a,
                -e, a, f,
                -e, a, -f,
                -e, -a, -f,
                a, f, -c,
                a, -f, -c,
                -a, -f, -c,
                f, -c, -d,
                -f, -c, d,
                -f, -c, -d);
        
        ICompactCouplesStructure g2_table7 =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) s2_table4);
        
        Helper2.prettyPrint(g2_table7);
        
        ObjectArrayList ccss = new ObjectArrayList(
                new Object[] {
                        g1_table7,
                        g2_table7
                });
        
        return new SimpleSystem(ccss);
    }
}
