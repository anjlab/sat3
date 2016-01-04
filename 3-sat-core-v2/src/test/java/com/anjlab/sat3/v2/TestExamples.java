package com.anjlab.sat3.v2;

import java.io.IOException;

import org.junit.Ignore;
import org.junit.Test;

import junit.framework.Assert;

public class TestExamples
{
    @Test
    public void testArticleExample() throws IOException
    {
        Helper2.UsePrettyPrint = true;
        Helper2.UseUniversalVarNames = true;
        
        Assert.assertEquals(Program.SAT, Program.main(new String[] {
                "../3-sat-core/target/test-classes/article-example.cnf"
        }));
    }
        
    @Test
    public void testRTI_k3_n100_m429_42_SAT() throws IOException
    {
        Helper2.UsePrettyPrint = false;
        Helper2.UseUniversalVarNames = false;
        
        Assert.assertEquals(Program.SAT, Program.main(new String[] {
                "../3-sat-core/target/test-classes/RTI_k3_n100_m429_42_SAT.cnf"
        }));
    }
    
    @Test
    public void testUnsat() throws IOException
    {
        Assert.assertEquals(Program.UNSAT, Program.main(new String[] {
                "../3-sat-core/target/test-classes/test-unsat.cnf"
        }));
    }
    
    @Ignore // Too big to run every time
    @Test
    public void testRpoc_xits_08_UNSAT() throws IOException
    {
        Assert.assertEquals(Program.UNSAT, Program.main(new String[] {
                "../3-sat-core/target/test-classes/rpoc_xits_08_UNSAT.cnf"
        }));
    }
    
    @Test
    public void testUf20_0532() throws IOException
    {
        Helper2.UsePrettyPrint = true;
        Assert.assertEquals(Program.SAT, Program.main(new String[] {
                "../3-sat-core/target/test-classes/uf20-0532.cnf"
        }));
    }
    
    @Ignore // http://www.cs.ubc.ca/~hoos/SATLIB/benchm.html
    @Test
    public void testUf20All() throws IOException
    {
        int satCounter = 0;
        int unsatCounter = 0;
        
        Helper2.UsePrettyPrint = false;
        for (int i = 1; i <= 1000; i++)
        {
            int result = Program.main(new String[] {
                    "/Users/dmitrygusev/downloads/uf20-91/uf20-0" + i + ".cnf"
            });
            
            if (result == Program.SAT)
            {
                satCounter++;
            }
            else
            {
                unsatCounter++;
            }
        }
        
        System.out.println("sat: " + satCounter + ", unsat: " + unsatCounter);
    }
    
    @Ignore // http://www.cs.ubc.ca/~hoos/SATLIB/benchm.html
    @Test
    public void testUf50All() throws IOException
    {
        int satCounter = 0;
        int unsatCounter = 0;
        
        Helper2.UsePrettyPrint = false;
        for (int i = 1; i <= 1000; i++)
        {
            int result = Program.main(new String[] {
                    "/Users/dmitrygusev/downloads/uf50-218/uf50-0" + i + ".cnf"
            });
            
            if (result == Program.SAT)
            {
                satCounter++;
            }
            else
            {
                unsatCounter++;
            }
        }
        
        System.out.println("sat: " + satCounter + ", unsat: " + unsatCounter);
    }
    
    @Ignore // http://www.cs.ubc.ca/~hoos/SATLIB/benchm.html
    @Test
    public void testUuf50All() throws IOException
    {
        int satCounter = 0;
        int unsatCounter = 0;
        
        Helper2.UsePrettyPrint = false;
        for (int i = 1; i <= 1000; i++)
        {
            int result = Program.main(new String[] {
                    "/Users/dmitrygusev/downloads/UU50.218.1000/uuf50-0" + i + ".cnf"
            });
            
            if (result == Program.SAT)
            {
                satCounter++;
            }
            else
            {
                unsatCounter++;
            }
        }
        
        System.out.println("sat: " + satCounter + ", unsat: " + unsatCounter);
    }
}
