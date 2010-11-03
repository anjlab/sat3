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
            int a = readInt(input);
            
            if (a == Integer.MAX_VALUE)
            {
                break;
            }
            
            int b = readInt(input);
            int c = readInt(input);
            
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

    private int readInt(InputStream input) throws IOException
    {
        int value = Integer.MAX_VALUE;
        int buf;
        if ((buf = input.read()) != -1)
        {
            value = buf;
            value = value | (input.read() << 8);
            value = value | (input.read() << 16);
            value = value | (input.read() << 24);
        }
        return value;
    }

}
