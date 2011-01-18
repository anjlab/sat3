package com.anjlab.sat3;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import cern.colt.function.LongObjectProcedure;
import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;

import com.anjlab.sat3.VarPairsIndexFactory.VarPairsIndex;

public class TestVarPairsIndex
{
    @BeforeClass
    public static void setup()
    {
        Helper.UsePrettyPrint = true;
        Helper.EnableAssertions = true;
        System.out.println(TestVarPairsIndex.class.getName());
    }
    
    @Test
    public void testBuildPartialIndex()
    {
        final ObjectArrayList cts = new ObjectArrayList();
        
        cts.add(Helper.createFormula(
                1, 2, 3,
                   2, 3, 4,
                      3, 4, 5,
                         4, 5, 6,
                            5, 6, 7));
        cts.add(Helper.createFormula(
                2, 5, 1,
                   5, 1, 3,
                      1, 3, 4, 
                         3, 4, 6,
                            4, 6, 7));
        
        //  Full index should be built prior to partial
        VarPairsIndexFactory.getInstance().buildIndex(cts);
        
        VarPairsIndex index = VarPairsIndexFactory.getInstance().
            buildPartialIndex(cts, (ICompactTripletsStructureHolder) cts.get(0), 0, 0);
        
        index.forEachPair(new LongObjectProcedure()
        {
            public boolean apply(long key, Object value)
            {
                int varName1_ = (int) (key >> 21);
                int varName2_ = (int) (key & 0x1FFFFF);

                System.out.println(varName1_+ ", " + varName2_);
                
                LongArrayList tiers = (LongArrayList) value;
                for (int i = 0; i < tiers.size(); i++)
                {
                    long formulaAndTierIndex = tiers.get(i);
                    int formulaIndex = (int)(formulaAndTierIndex >> 32);
                    int tierIndex = (int)(formulaAndTierIndex & 0x00000000FFFFFFFFL);

                    ICompactTripletsStructure formula = (ICompactTripletsStructure)cts.get(formulaIndex);
                    ITier tier = formula.getTier(tierIndex);
                    System.out.println(tier);
                    Helper.prettyPrint(formula);
                }
                
                return true;
            }
        });
        
        assertEquals(4, index.pairs().size());
    }
}
