package com.anjlab.sat3;

import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.list.ObjectArrayList;

import com.anjlab.sat3.EmptyStructureException;
import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ICompactTripletsStructure;
import com.anjlab.sat3.IHyperStructure;
import com.anjlab.sat3.ITabularFormula;

public class TestXorChain
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestXorChain.class.getName());
    }
    
    @Test
    public void testStructuresFromReducedHSS() throws Exception
    {
        ObjectArrayList cts = new ObjectArrayList(
                new ITabularFormula[] {
                        Helper.loadFromFile("target/test-classes/xor-chain/basic-cts.cnf"),
                        Helper.loadFromFile("target/test-classes/xor-chain/hss-0-other-cts.cnf"),
                }
            );
        
        Helper.prettyPrint((ITabularFormula) cts.get(0));
        Helper.prettyPrint((ITabularFormula) cts.get(1));
        
        try
        {
            ObjectArrayList hss = Helper.createHyperStructuresSystem(
                    cts, (ICompactTripletsStructure) cts.get(0), new Properties());
            
            System.out.println("Writing HSS(0) image to filesystem...");
            Helper.writeToImage((IHyperStructure) hss.get(0), null, null, "target/test-classes/xor-chain/hs.png");
            System.out.println("Done");
            
            fail("Non-empty HSS for UNSAT instance can't be built according to the Theorem 1");
        }
        catch (EmptyStructureException e)
        {
            //  Okay
        }
        
        //  Save CTS set into a single CNF file
//        SimpleFormula formula = new SimpleFormula();
//        for (int i = 0; i < cts.size(); i++)
//        {
//            ICompactTripletsStructure s = (ICompactTripletsStructure) cts.get(i);
//            
//            for (int j = 0; j < s.getTiers().size(); j++)
//            {
//                ITier tier = s.getTier(j);
//                tier.inverse();
//                
////                if (tier.isEmpty()) continue;
//                
//                Iterator<ITripletValue> iterator = tier.iterator();
//                while (iterator.hasNext())
//                {
//                    ITripletValue tripletValue = iterator.next();
//                    
//                    formula.add(new SimpleTriplet(
//                            tier.getAName() * (tripletValue.isNotA() ? -1 : 1), 
//                            tier.getBName() * (tripletValue.isNotB() ? -1 : 1), 
//                            tier.getCName() * (tripletValue.isNotC() ? -1 : 1)));
//                }
//                
//            }
//        }
//        Helper.saveToDIMACSFileFormat(formula, "target/test-classes/xor-chain/formula.cnf");
    }
}
