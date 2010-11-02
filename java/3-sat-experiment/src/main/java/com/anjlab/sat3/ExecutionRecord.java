package com.anjlab.sat3;

import java.util.Properties;

public class ExecutionRecord
{

    private final long initialFormulaLoadTime;
    private final long initialFormulaVarCount;
    private final long initialFormulaClausesCount;
    private final long ctfCreationTime;
    private final long ctfCount;
    private final long ctsCreationTime;
    private final long ctsUnificationTime;
    private final long basicCTSInitialClausesCount;
    private final long hssCreationTime;
    private final long basicCTSFinalClausesCount;
    private final long searchHSSRouteTime;
    private final String filename;
    
    public ExecutionRecord(String filename, Properties statistics)
    {
        this.filename = filename; 
        initialFormulaLoadTime = Long.parseLong((String) statistics.get("InitialFormulaLoadTime"));
        initialFormulaVarCount = Long.parseLong((String) statistics.get("InitialFormulaVarCount"));
        initialFormulaClausesCount = Long.parseLong((String) statistics.get("InitialFormulaClausesCount"));
        ctfCreationTime = Long.parseLong((String) statistics.get("CTFCreationTime"));
        ctfCount = Long.parseLong((String) statistics.get("CTFCount"));
        ctsCreationTime = Long.parseLong((String) statistics.get("CTSCreationTime"));
        ctsUnificationTime = Long.parseLong((String) statistics.get("CTSUnificationTime"));
        basicCTSInitialClausesCount = Long.parseLong((String) statistics.get("BasicCTSInitialClausesCount"));
        hssCreationTime = Long.parseLong((String) statistics.get("HSSCreationTime"));
        basicCTSFinalClausesCount = Long.parseLong((String) statistics.get("BasicCTSFinalClausesCount"));
        searchHSSRouteTime = Long.parseLong((String) statistics.get("SearchHSSRouteTime"));
    }

    public String toTABDelimitedLine()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(filename);
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
        builder.append("InitialFormulaLoadTime");
        builder.append('\t');
        builder.append("InitialFormulaVarCount");
        builder.append('\t');
        builder.append("InitialFormulaClausesCount");
        builder.append('\t');
        builder.append("CTFCreationTime");
        builder.append('\t');
        builder.append("CTFCount");
        builder.append('\t');
        builder.append("CTSCreationTime");
        builder.append('\t');
        builder.append("CTSUnificationTime");
        builder.append('\t');
        builder.append("BasicCTSInitialClausesCount");
        builder.append('\t');
        builder.append("HSSCreationTime");
        builder.append('\t');
        builder.append("BasicCTSFinalClausesCount");
        builder.append('\t');
        builder.append("SearchHSSRouteTime");
        return builder.toString();
    }

}
