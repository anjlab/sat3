package com.anjlab.sat3;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.ObjectArrayList;

public class TestExamples
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestExamples.class);
    
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestExamples.class.getName());
    }
    
    @Test
    public void testHabr() throws Exception
    {
        ObjectArrayList ct = new ObjectArrayList(
                    new ITabularFormula[] {
                            
                            Helper.createFormula(1, 2, -6,
                                                 1, -2, 6,
                                                 -1, 2, 6,
                                                 -1, -2, -6),
                            Helper.createFormula(3, 4, -7,
                                                 3, -4, 7,
                                                 -3, 4, 7,
                                                 -3, -4, -7),
                            Helper.createFormula(5, 6, -7,
                                                 5, -6, 7,
                                                 -5, 6, 7,
                                                 -5, -6, -7),
                            Helper.createFormula(1, 2, -8,
                                                 1, -2, 8,
                                                 -1, 2, 8,
                                                 -1, -2, -8),
                            Helper.createFormula(3, 4, -9,
                                                 3, -4, 9,
                                                 -3, 4, 9,
                                                 -3, -4, -9),
                            Helper.createFormula(5, 8, 9,
                                                 5, -8, -9,
                                                 -5, 8, -9,
                                                 -5, -8, 9),
                            Helper.createFormula(10, 11, 12),
                    }
                );
        
        LOGGER.info("Initial CTF set");
        Helper.printFormulas(ct);
        
        IPermutation permutation = new SimplePermutation();
        for (int varName = 1; varName <= 12; varName++)
        {
            permutation.add(varName);
        }
        LOGGER.info("CTF -> CTS");
        Helper.completeToCTS(ct, permutation);
        
        LOGGER.info("CTS set");
        Helper.printFormulas(ct);
        
        LOGGER.info("Unify all CTS");
        Helper.unify(ct);
        
        try
        {
            Properties statistics = new Properties();
            Helper.createHyperStructuresSystem(ct, (ICompactTripletsStructure) ct.get(ct.size() - 1), statistics);
        }
        catch (EmptyStructureException e)
        {
            LOGGER.info("Formula not satisfiable", e);
        }
    }
}
