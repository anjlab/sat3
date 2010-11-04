package com.anjlab.sat3;

import java.io.IOException;
import java.io.InputStream;

import cern.colt.list.ObjectArrayList;

public class RomanovSKTFormulaReader implements IFormulaReader
{

    public ITabularFormula readFormula(InputStream input) throws IOException
    {
        ObjectArrayList cts = new ObjectArrayList();
        
        ITabularFormula formula = new SimpleFormula();
        
        while (true)
        {
            int a = Helper.readInt(input);
            
            if (a == Integer.MAX_VALUE)
            {
                break;
            }
            
            int b = Helper.readInt(input);
            int c = Helper.readInt(input);
            
            if (a == 0 && b == 0 && c == 0)
            {
                cts.add(formula);
                formula = new SimpleFormula();
            }
            else
            {
                formula.add(new SimpleTriplet(a, b, c));
            }
        }
        
        formula = new SimpleFormula();
        
        for (int k = 0; k < cts.size(); k++)
        {
            ITabularFormula f = (ITabularFormula) cts.get(k);
            for (int j = 0; j < f.getTiers().size(); j++)
            {
                ITier tier = f.getTier(j);
                tier.inverse();
                if (!tier.isEmpty())
                {
                    formula.unionOrAdd(tier);
                }
            }
        }
        
        return formula;
    }

}
