package com.anjlab.sat3;

import java.io.BufferedReader;
import java.io.IOException;

import cern.colt.list.IntArrayList;

public class GenericFormulaReader
{
    private int originalVarCount = 0;
    private int n = 0;
    private int m = 0;
    private int b = 0;
    private int c = 0;
   
    private final IntArrayList values = new IntArrayList();
    
    private ITabularFormula formula = new SimpleFormula();
    
    public String toString()
    {
        return "n=" + n + ", m=" + m;
    }

    public ITabularFormula readFormula(BufferedReader reader) throws IOException
    {
        readMetadata(reader);
        
        int sign = 1;
        int r = 0;
        int ch;
        while ((ch = reader.read()) != -1)
        {
            if (Character.isWhitespace(ch))
            {
                if (r != 0) 
                {
                    r = r * sign;
                    values.add(r);
                            
                    r = 0;
                    sign = 1;
                }
                continue;
            }
            if (ch == '0' && r == 0)
            {
                if(!values.isEmpty())
                {
                    addTriplets();
                    values.clear();
                }
                continue;
            }
            if (ch == '-')
            {
                sign = -1;
            }

            if ('0' <= ch && ch < '0' + 10)
            {
                r = r * 10 + ch - '0';
            } 
        }
        
        System.out.println("Original VarCount: " + originalVarCount + ". Final VarCount: " + n + ". Added: " + (n - originalVarCount));
        
        return formula;
    }

    private void readMetadata(BufferedReader reader) throws IOException
    {
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (line.startsWith("c"))
            {
                continue;
            }
            if (line.startsWith("p"))
            {
                String[] pLine = line.split("\\W+");
                if (pLine.length != 4 || !pLine[1].equals("cnf"))
                {
                    throw new AssertionError("Bad DIMACS CNF file format");
                }
                
                n = Integer.parseInt(pLine[2]);
                m = Integer.parseInt(pLine[3]);
                
                originalVarCount = n;
                
                break;
            }
        }
    }

    private boolean createNewVars = true;
    private void addTriplets()
    {
        int count = values.size();
        int[] elements = values.elements();
        
        if(count == 1)
        {
            if(createNewVars)
            {
                b = ++n;
                c = ++n;
            }
            createNewVars = !createNewVars;
            
            formula.add(new SimpleTriplet(elements[0], b, c));
            formula.add(new SimpleTriplet(elements[0], b, -c));
            formula.add(new SimpleTriplet(elements[0], -b, c));
            formula.add(new SimpleTriplet(elements[0], -b, -c));
        } else if(count == 2)
        {
            if(createNewVars)
            {
                b = ++n;
            }
            createNewVars = !createNewVars;
            
            formula.add(new SimpleTriplet(elements[0], elements[1], b));
            formula.add(new SimpleTriplet(elements[0], elements[1], -b));
        } else if(count == 3)
        {
            formula.add(new SimpleTriplet(elements[0], elements[1], elements[2]));
        } else
        {
            int last = ++n;
            formula.add(new SimpleTriplet(elements[0], elements[1], last));
            for(int v=2; v < count - 2; v++)
            {
                formula.add(new SimpleTriplet(-last, elements[v], last = ++n));
            }
            formula.add(new SimpleTriplet(-last, elements[count - 2], elements[count -1]));
            
            createNewVars = true;
        }
        
        //System.out.println(formula.getVarCount() + ";" + x);
    }
}
