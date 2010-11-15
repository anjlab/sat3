/*
 * Copyright (c) 2010 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
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
