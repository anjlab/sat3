package com.anjlab.sat3.v2;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ITabularFormula;

import cern.colt.list.ObjectArrayList;

public class TestXorChain
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        Helper2.UsePrettyPrint = true;
        Helper2.UseUniversalVarNames = true;
        System.out.println(TestXorChain.class.getName());
    }
    
    @Test
    public void testStructuresFromReducedHSS() throws Exception
    {
        ObjectArrayList cts = new ObjectArrayList(
                new ITabularFormula[] {
                        Helper.loadFromFile("../3-sat-core/target/test-classes/xor-chain/basic-cts.cnf"),
                        Helper.loadFromFile("../3-sat-core/target/test-classes/xor-chain/hss-0-other-cts.cnf"),
                }
            );
        
        Helper.prettyPrint((ITabularFormula) cts.get(0));
        Helper.prettyPrint((ITabularFormula) cts.get(1));
        
        ISystem system = new SimpleSystem(Helper2.createCCS(cts));
        AtomicReference<ISystem> reference = new AtomicReference<ISystem>(system);
        boolean sat = new Solver().solve(reference);
        
        system = reference.get();
        
        Helper2.prettyPrint(system);
        
        Helper2.printVariablesSideBySide(system, 0, 1);
        
        ITabularFormula formula = Helper.loadFromFile(
                "../3-sat-core/target/test-classes/xor-chain/x1_16.shuffled.cnf");
        
        Assert.assertFalse("Formula is UNSAT by construction",
                Helper2.evaluate(formula, system.getCIC()));
        Assert.assertFalse("Formula is UNSAT", sat);
    }
}
