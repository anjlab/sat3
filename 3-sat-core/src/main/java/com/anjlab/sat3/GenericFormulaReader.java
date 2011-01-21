/*
 * Copyright (c) 2010, 2011 AnjLab
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
 * You should have received a copy of the GNU Lesser General Public License along with 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

public class GenericFormulaReader implements IFormulaReader
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericFormulaReader.class);
    
    private int originalVarCount = 0;
    private int originalClausesCount = 0;
   
    private SimpleFormula formula = new SimpleFormula();
    
    public ITabularFormula readFormula(InputStream input) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input, "ascii"));
        
        readMetadata(reader);
        
        // internal var name -> original var name
        OpenIntIntHashMap internalToOriginalMap = new OpenIntIntHashMap();
        // original var name -> internal var name
        OpenIntIntHashMap sourceIndices = new OpenIntIntHashMap();
        IntArrayList sourceValues = readSourceValues(reader, sourceIndices);
        
        for (int originalVarName : sourceIndices.keys().elements())
        {
            internalToOriginalMap.put(sourceIndices.get(originalVarName), originalVarName);
        }
        
        IntArrayList values = new IntArrayList();

        for (int i = 0; i < sourceValues.size(); i++)
        {
            int sourceVar = sourceValues.get(i);
            if (sourceVar != 0)
            {
                int sign = sourceVar > 0 ? 1 : -1;
                int var = sign * sourceIndices.get(Math.abs(sourceVar));
                values.add(var);
            }
            else
            {
                if(!values.isEmpty())
                {
                    addTriplets(internalToOriginalMap, values);
                    values.clear();
                }
            }
        }
        
        formula.setVarMappings(internalToOriginalMap);
        
        LOGGER.debug("Original Var Count: {}; Original Clauses Count: {}; Final Var Count: {}; Final Clauses Count: {}",
                     new Object[] { originalVarCount, originalClausesCount, formula.getVarCount(), formula.getClausesCount() });
        
        return formula;
    }


    private IntArrayList readSourceValues(BufferedReader reader, OpenIntIntHashMap sourceIndices)
            throws IOException
    {
        IntArrayList sourceValues = new IntArrayList();
        OpenIntIntHashMap originalVarNames = new OpenIntIntHashMap();
        
        int sign = 1;
        int r = 0;
        int ch;
        while ((ch = reader.read()) != -1)
        {
            if (Character.isWhitespace(ch))
            {
                if (r != 0)
                {
                    originalVarNames.put(r, r);
                    r = r * sign;
                    sourceValues.add(r);
                    
                    r = 0;
                    sign = 1;
                }
                continue;
            }
            if (ch == '0' && r == 0)
            {
                sourceValues.add(0);
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
        
        IntArrayList sortedVarNames = originalVarNames.keys();
        sortedVarNames.sort();
        for (int i = 0; i < sortedVarNames.size(); i++)
        {
            sourceIndices.put(sortedVarNames.get(i), i + 1);
        }
        
        return sourceValues;
    }

    private void readMetadata(BufferedReader reader) throws IOException
    {
        String line;
        while ((line = reader.readLine()) != null)
        {
            line = line.toLowerCase();
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
                
                originalVarCount = Integer.parseInt(pLine[2]);
                originalClausesCount = Integer.parseInt(pLine[3]);
                
                break;
            }
        }
    }

    private boolean createNewVars = true;
    private int b = 0;
    private int c = 0;
    
    private void addTriplets(OpenIntIntHashMap varMappings, IntArrayList values)
    {
        int n = varMappings.size();

        int count = values.size();
        int[] elements = values.elements();
        
        if(count == 1)
        {
            if(createNewVars)
            {
                b = ++n;
                c = ++n;
                
                varMappings.put(b, -1);
                varMappings.put(c, -1);
            }
//            createNewVars = !createNewVars;
            
            formula.add(new SimpleTriplet(elements[0], b, c));
            formula.add(new SimpleTriplet(elements[0], b, -c));
            formula.add(new SimpleTriplet(elements[0], -b, c));
            formula.add(new SimpleTriplet(elements[0], -b, -c));
        }
        else if(count == 2)
        {
            if(createNewVars)
            {
                b = ++n;
                
                varMappings.put(b, -1);
            }
//            createNewVars = !createNewVars;
            
            formula.add(new SimpleTriplet(elements[0], elements[1], b));
            formula.add(new SimpleTriplet(elements[0], elements[1], -b));
        }
        else if(count == 3)
        {
            formula.add(new SimpleTriplet(elements[0], elements[1], elements[2]));
        }
        else
        {
            int last = ++n;
            
            varMappings.put(last, -1);
            
            formula.add(new SimpleTriplet(elements[0], elements[1], last));
            for(int v=2; v < count - 2; v++)
            {
                formula.add(new SimpleTriplet(-last, elements[v], last + 1));
                
                last = ++n;
                
                varMappings.put(last, -1);
            }
            formula.add(new SimpleTriplet(-last, elements[count - 2], elements[count -1]));
            
            createNewVars = true;
        }
    }
}
