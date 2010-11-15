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

import java.util.Properties;

public class ExecutionRecord
{
    private final String filename;
    private final String implementationVersion; 
    private final long initialFormulaLoadTime;
    private final long initialFormulaVarCount;
    private final long initialFormulaClausesCount;
    private final long ctfCreationTime;
    private final long ctfCount;
    private final long ctsCreationTime;
    private final long ctsUnificationTime;
    private final long basicCTSInitialClausesCount;
    private final long hssCreationTime;
    private final long numberOfHssTiersBuilt;
    private final long basicCTSFinalClausesCount;
    private final long searchHSSRouteTime;
    
    public ExecutionRecord(String filename, Properties statistics)
    {
        this.filename = filename; 
        implementationVersion = String.valueOf(statistics.get(Helper.IMPLEMENTATION_VERSION));
        initialFormulaLoadTime = tryParseLong(statistics, Helper.INITIAL_FORMULA_LOAD_TIME);
        initialFormulaVarCount = tryParseLong(statistics, Helper.INITIAL_FORMULA_VAR_COUNT);
        initialFormulaClausesCount = tryParseLong(statistics, Helper.INITIAL_FORMULA_CLAUSES_COUNT);
        ctfCreationTime = tryParseLong(statistics, Helper.CTF_CREATION_TIME);
        ctfCount = tryParseLong(statistics, Helper.CTF_COUNT);
        ctsCreationTime = tryParseLong(statistics, Helper.CTS_CREATION_TIME);
        ctsUnificationTime = tryParseLong(statistics, Helper.CTS_UNIFICATION_TIME);
        basicCTSInitialClausesCount = tryParseLong(statistics, Helper.BASIC_CTS_INITIAL_CLAUSES_COUNT);
        hssCreationTime = tryParseLong(statistics, Helper.HSS_CREATION_TIME);
        numberOfHssTiersBuilt = tryParseLong(statistics, Helper.NUMBER_OF_HSS_TIERS_BUILT);
        basicCTSFinalClausesCount = tryParseLong(statistics, Helper.BASIC_CTS_FINAL_CLAUSES_COUNT);
        searchHSSRouteTime = tryParseLong(statistics, Helper.SEARCH_HSS_ROUTE_TIME);
    }

    private long tryParseLong(Properties statistics, String property)
    {
        String value = (String) statistics.get(property);
        return value == null ? -1 : Long.parseLong(value);
    }

    public String toTABDelimitedLine()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(filename);
        builder.append('\t');
        builder.append(implementationVersion);
        builder.append('\t');
        builder.append(initialFormulaLoadTime);
        builder.append('\t');
        builder.append(initialFormulaVarCount);
        builder.append('\t');
        builder.append(initialFormulaClausesCount);
        builder.append('\t');
        builder.append(ctfCreationTime);
        builder.append('\t');
        builder.append(ctfCount);
        builder.append('\t');
        builder.append(ctsCreationTime);
        builder.append('\t');
        builder.append(ctsUnificationTime);
        builder.append('\t');
        builder.append(basicCTSInitialClausesCount);
        builder.append('\t');
        builder.append(hssCreationTime);
        builder.append('\t');
        builder.append(numberOfHssTiersBuilt);
        builder.append('\t');
        builder.append(basicCTSFinalClausesCount);
        builder.append('\t');
        builder.append(searchHSSRouteTime);
        return builder.toString();
    }

    public static String tabDelimitedHeader()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("FileName");
        builder.append('\t');
        builder.append(Helper.IMPLEMENTATION_VERSION);
        builder.append('\t');
        builder.append(Helper.INITIAL_FORMULA_LOAD_TIME);
        builder.append('\t');
        builder.append(Helper.INITIAL_FORMULA_VAR_COUNT);
        builder.append('\t');
        builder.append(Helper.INITIAL_FORMULA_CLAUSES_COUNT);
        builder.append('\t');
        builder.append(Helper.CTF_CREATION_TIME);
        builder.append('\t');
        builder.append(Helper.CTF_COUNT);
        builder.append('\t');
        builder.append(Helper.CTS_CREATION_TIME);
        builder.append('\t');
        builder.append(Helper.CTS_UNIFICATION_TIME);
        builder.append('\t');
        builder.append(Helper.BASIC_CTS_INITIAL_CLAUSES_COUNT);
        builder.append('\t');
        builder.append(Helper.HSS_CREATION_TIME);
        builder.append('\t');
        builder.append(Helper.NUMBER_OF_HSS_TIERS_BUILT);
        builder.append('\t');
        builder.append(Helper.BASIC_CTS_FINAL_CLAUSES_COUNT);
        builder.append('\t');
        builder.append(Helper.SEARCH_HSS_ROUTE_TIME);
        return builder.toString();
    }

}
