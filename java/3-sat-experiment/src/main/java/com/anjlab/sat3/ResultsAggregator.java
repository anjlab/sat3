package com.anjlab.sat3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

 
public class ResultsAggregator
{
    public static void main(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            System.out.println("Specify path to folder with results");
            System.exit(0);
        }
        File resultsFolder = new File(args[0]);
        String[] resultFiles = resultsFolder.list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith("results.txt");
            }
        });
        List<ExecutionRecord> records = new ArrayList<ExecutionRecord>();
        for (String file : resultFiles)
        {
            Properties statistics = new Properties();
            FileInputStream is = null;
            try
            {
                is = new FileInputStream(resultsFolder.getPath() + File.separator + file);
                statistics.load(is);
                ExecutionRecord record = new ExecutionRecord(file.substring(0, file.indexOf("-results.txt")), statistics);
                records.add(record);
            }
            finally
            {
                if (is != null)
                {
                    is.close();
                }
            }
        }
        exportToTABDelimited(records, resultsFolder.getPath() + File.separator + "aggregated-results.tab");
        System.out.println("Done");
    }

    private static void exportToTABDelimited(List<ExecutionRecord> records, String filename) throws IOException
    {
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter(new FileWriter(filename));
            writer.write(ExecutionRecord.tabDelimitedHeader());
            writer.write('\n');
            for (ExecutionRecord record : records)
            {
                writer.write(record.toTABDelimitedLine());
                writer.write('\n');
            }
        }
        finally
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }
}
