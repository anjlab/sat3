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
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.printFormulas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.ObjectArrayList;

public class Program
{
    private static final String GENERATE_3SAT_OPTION = "g";
    private static final String FIND_HSS_ROUTE_OPTION = "r";
    private static final String CREATE_SKT_OPTION = "c";
    private static final String EVALUATE_OPTION = "e";
    private static final String RESULTS_OUTPUT_FILE_OPTION = "o";
    private static final String HELP_OPTION = "h";
    private static final String HSS_IMAGE_OUTPUT_FILENAME_OPTION = "i";
    private static final String USE_ABC_VAR_NAMES_OPTION = "u";
    private static final String DISABLE_ASSERTIONS_OPTION = "a";
    private static final String USE_PRETTY_PRINT_OPTION = "p";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    
    public static void main(String[] args) throws Exception
    {
        System.out.println("Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem"
                           + "\nCopyright (c) 2010 AnjLab"
                           + "\nThis program comes with ABSOLUTELY NO WARRANTY."
                           + "\nThis is free software, and you are welcome to redistribute it under certain conditions." 
                           + "\nSee LICENSE.txt file or visit <http://www.gnu.org/copyleft/lesser.html> for details.");
        
        LOGGER.debug("Reading version number from manifest");
        String implementationVersion = Helper.getImplementationVersionFromManifest("3-SAT Core RI");
        System.out.println("Version: " + implementationVersion + "\n");
        
        Options options = getCommandLineOptions();
        
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.getArgs().length != 1 || commandLine.hasOption(HELP_OPTION))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Program.class.getName() + " [OPTIONS] <input-file-name>" +
                    "\nWhere <input-file-name> is a path to file containing k-SAT formula instance in DIMACS CNF or Romanov SKT file format.", options);
            System.exit(0);
        }

        String formulaFile = commandLine.getArgs()[0];

        Helper.UsePrettyPrint = commandLine.hasOption(USE_PRETTY_PRINT_OPTION);
        Helper.EnableAssertions = !commandLine.hasOption(DISABLE_ASSERTIONS_OPTION);
        Helper.UseUniversalVarNames = !commandLine.hasOption(USE_ABC_VAR_NAMES_OPTION);
        
        Properties statistics = new Properties();
        StopWatch stopWatch = new StopWatch();
        
        try
        {
            statistics.put(Helper.IMPLEMENTATION_VERSION, implementationVersion);
            
            stopWatch.start("Load formula");
            ITabularFormula formula = Helper.loadFromFile(formulaFile);
            long timeElapsed = stopWatch.stop();
            
            statistics.put(Helper.INITIAL_FORMULA_LOAD_TIME, String.valueOf(timeElapsed));
            
            if (commandLine.hasOption(GENERATE_3SAT_OPTION))
            {
                String generated3SatFilename = formulaFile + "-3sat.cnf";
                
                LOGGER.info("Saving 3-SAT formula to {}...", generated3SatFilename);
                Helper.saveToDIMACSFileFormat(formula, generated3SatFilename);
            }
            
            if (formula.getVarCount() > 26)
            {
                LOGGER.info("Variables count > 26 => force using universal names for variables.");
                Helper.UseUniversalVarNames = true;
            }
            
            statistics.put(Helper.INITIAL_FORMULA_VAR_COUNT, String.valueOf(formula.getVarCount()));
            statistics.put(Helper.INITIAL_FORMULA_CLAUSES_COUNT, String.valueOf(formula.getClausesCount()));
            
            Helper.prettyPrint(formula);
            stopWatch.printElapsed();
            
            if (commandLine.hasOption(FIND_HSS_ROUTE_OPTION))
            {
                String hssPath = commandLine.getOptionValue(FIND_HSS_ROUTE_OPTION);
                
                stopWatch.start("Load HSS from " + hssPath);
                ObjectArrayList hss = Helper.loadHSS(hssPath);
                stopWatch.stop();
                stopWatch.printElapsed();
                
                findHSSRoute(commandLine, formulaFile, statistics, stopWatch, formula, null, null, hss, hssPath);
                
                return;
            }
            
            if (commandLine.hasOption(EVALUATE_OPTION))
            {
                String resultsFilename = commandLine.getOptionValue(EVALUATE_OPTION);
                boolean satisfiable = evaluateFormula(stopWatch, formula, resultsFilename);
                if (satisfiable)
                {
                    System.out.println("Formula evaluated as SAT");
                }
                else
                {
                    System.out.println("Formula evaluated as UNSAT");
                }
                //  Only evaluate formula value
                return;
            }
            
            //  Find if formula is SAT
            
            //  Clone initial formula to verify formula satisfiability later
            ITabularFormula formulaClone = null;
            if (Helper.EnableAssertions)
            {
                stopWatch.start("Clone initial formula");
                formulaClone = formula.clone();
                stopWatch.stop();
                stopWatch.printElapsed();
            }
            
            stopWatch.start("Create CTF");
            ObjectArrayList ct = Helper.createCTF(formula);
            timeElapsed = stopWatch.stop();
            printFormulas(ct);
            stopWatch.printElapsed();
            
            statistics.put(Helper.CTF_CREATION_TIME, String.valueOf(timeElapsed));
            statistics.put(Helper.CTF_COUNT, String.valueOf(ct.size()));
            
            LOGGER.info("CTF count: {}", ct.size());

            if (Helper.EnableAssertions)
            {
                assertNoTripletsLost(formula, ct);
            }

            //  Clone CTF to verify formula satisfiability against it later
            ObjectArrayList ctfClone = null;
            
            if (Helper.EnableAssertions)
            {
                ctfClone = Helper.cloneStructures(ct);
            }
            
            stopWatch.start("Create CTS");
            Helper.completeToCTS(ct, formula.getPermutation());
            timeElapsed = stopWatch.stop();
            printFormulas(ct);
            stopWatch.printElapsed();
            
            statistics.put(Helper.CTS_CREATION_TIME, String.valueOf(timeElapsed));
            
            if (commandLine.hasOption(CREATE_SKT_OPTION))
            {
                String sktFilename = formulaFile + ".skt";
                stopWatch.start("Convert CTS to " + sktFilename);
                Helper.convertCTStructuresToRomanovSKTFileFormat(ct, sktFilename);
                stopWatch.stop();
                stopWatch.printElapsed();
                
                return;
            }
            
            ObjectArrayList hss = unifyAndCreateHSS(statistics, stopWatch, ct);
            
            String hssPath = formulaFile + "-hss";
            stopWatch.start("Save HSS to " + hssPath + "...");
            Helper.saveHSS(hssPath, hss);
            stopWatch.stop();
            stopWatch.printElapsed();
            
            findHSSRoute(commandLine, formulaFile, statistics, stopWatch, formula, formulaClone, ctfClone, hss, hssPath);
        }
        catch (EmptyStructureException e)
        {
            stopWatch.stop();
            stopWatch.printElapsed();
            
            LOGGER.info("One of the structures was built empty", e);
            
            String resultsFilename = getResultsFilename(commandLine, formulaFile);
            stopWatch.start("Saving current statictics of calculations to " + resultsFilename);
            writeUnsatToFile(resultsFilename, statistics);
            stopWatch.stop();
            stopWatch.printElapsed();
            
            System.out.println("Formula not satisfiable");
        }
        finally
        {
            System.out.println("Program completed");
        }
    }

    private static ObjectArrayList unifyAndCreateHSS(Properties statistics, StopWatch stopWatch, ObjectArrayList cts)
    {
        long timeElapsed;
        stopWatch.start("Unify all CTS");
        Helper.unify(cts);
        timeElapsed = stopWatch.stop();
        printFormulas(cts);
        stopWatch.printElapsed();

        statistics.put(Helper.CTS_UNIFICATION_TIME, String.valueOf(timeElapsed));
        
        LOGGER.info("CTF: {}", cts.size());
        
        ObjectArrayList hss = null;
        try
        {
            stopWatch.start("Create HSS");
            hss = Helper.createHyperStructuresSystem(cts, statistics);
        }
        finally
        {
            timeElapsed = stopWatch.stop();
            stopWatch.printElapsed();
            if (hss != null)
            {
                statistics.put(Helper.BASIC_CTS_FINAL_CLAUSES_COUNT, String.valueOf(((IHyperStructure) hss.get(0)).getBasicCTS().getClausesCount()));
            }
            statistics.put(Helper.HSS_CREATION_TIME, String.valueOf(timeElapsed));
        }
        return hss;
    }

    private static void findHSSRoute(CommandLine commandLine, String formulaFile,
            Properties statistics, StopWatch stopWatch,
            ITabularFormula formula, ITabularFormula formulaClone,
            ObjectArrayList ctfClone, ObjectArrayList hss, String hssPath)
            throws IOException
    {
        long timeElapsed;
        //  TODO Configure hssTempPath using CL options
        String hssTempPath = hssPath + "-temp"; 
        stopWatch.start("Find HSS route");
        ObjectArrayList route = Helper.findHSSRouteByReduce(hss, hssTempPath);
        timeElapsed = stopWatch.stop();
        stopWatch.printElapsed();
        
        statistics.put(Helper.SEARCH_HSS_ROUTE_TIME, String.valueOf(timeElapsed));
        
        if (Helper.EnableAssertions)
        {
            if (formulaClone != null)
            {
                if (!formula.equals(formulaClone))
                {
                    LOGGER.warn("Initial formula differs from its cloned version");
                }
            }
        }
        
        String hssImageFile = formulaFile + "-hss-0.png";
        
        if (commandLine.hasOption(HSS_IMAGE_OUTPUT_FILENAME_OPTION))
        {
            hssImageFile = commandLine.getOptionValue(HSS_IMAGE_OUTPUT_FILENAME_OPTION);
        }
        
        stopWatch.start("Write HSS as image to " + hssImageFile);
        Helper.writeToImage(((SimpleVertex) route.get(route.size() - 1)).getHyperStructure(), route, null, hssImageFile);
        stopWatch.stop();
        stopWatch.printElapsed();
        
        stopWatch.start("Verify formula is satisfiable using variable values from HSS route");
        verifySatisfiable(formula, route);
        if (Helper.EnableAssertions)
        {
            if (ctfClone != null)
            {
                verifySatisfiable(ctfClone, route);
            }
        }
        stopWatch.stop();
        stopWatch.printElapsed();
        
        String resultsFilename = getResultsFilename(commandLine, formulaFile);
        stopWatch.start("Write HSS route to " + resultsFilename);
        writeSatToFile(formula, resultsFilename, statistics, route);
        stopWatch.stop();
        stopWatch.printElapsed();
    }

    private static boolean evaluateFormula(StopWatch stopWatch,
            ITabularFormula formula, String resultsFilename)
            throws FileNotFoundException, IOException
    {
        stopWatch.start("Evaluate formula");
        boolean satisfiable;
        Properties properties = new Properties();
        FileInputStream is = null;
        try
        {
            is = new FileInputStream(new File(resultsFilename));
            properties.load(is);
            satisfiable = formula.evaluate(properties);
            stopWatch.stop();
            stopWatch.printElapsed();
        }
        finally
        {
            if (is != null)
            {
                is.close();
            }
        }
        return satisfiable;
    }

    private static void writeUnsatToFile(String resultsFile, Properties statistics) throws IOException
    {
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(new File(resultsFile));
            
            statistics.store(out, "Unsatisfiable");
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    private static void writeSatToFile(ITabularFormula formula, String resultsFile, Properties statistics, ObjectArrayList route) throws IOException
    {
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(new File(resultsFile));
            
            IVertex vertex = null;
            for (int i = 0; i < route.size(); i++)
            {
                vertex = (IVertex) route.get(i);
                
                writeToStatistics(formula, statistics, vertex.getPermutation().getAName(), vertex.getTripletValue().isNotA());
            }
            if (vertex != null)
            {
                writeToStatistics(formula, statistics, vertex.getPermutation().getBName(), vertex.getTripletValue().isNotB());
                writeToStatistics(formula, statistics, vertex.getPermutation().getCName(), vertex.getTripletValue().isNotC());
            }
            
            statistics.store(out, "Satisfiable. Variable values from HSS route");
        }
        finally
        {
            if (out != null)
            {
                out.close();
            }
        }
    }

    private static void writeToStatistics(ITabularFormula formula, Properties statistics, int varName, boolean varValue)
    {
        String stringValue = String.valueOf(varValue);
        statistics.put("_" + varName, stringValue);
        
        int originalVarName = formula.getOriginalVarName(varName);
        if (originalVarName > 0)
        {
            statistics.put(String.valueOf(originalVarName), stringValue);
        }
    }

    private static String getResultsFilename(CommandLine commandLine, String formulaFile)
    {
        String resultsFile = formulaFile + "-results.txt";
        if (commandLine.hasOption(RESULTS_OUTPUT_FILE_OPTION))
        {
            resultsFile = commandLine.getOptionValue(RESULTS_OUTPUT_FILE_OPTION);
        }
        return resultsFile;
    }

    @SuppressWarnings("static-access")
    private static Options getCommandLineOptions()
    {
        Options options = new Options();
        
        options.addOption(OptionBuilder.withLongOpt("help")
                                       .withDescription("Prints this help message.")
                                       .create(HELP_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("use-pretty-print")
                                       .withDescription("If specified, program will print detailed information about " +
                                                        "formulas including triplet values." +
                                                        "\nUseful when studying how algorithm works (especially if variables count less than 20)." +
                                                        "\nDisabled by default.")
                                       .create(USE_PRETTY_PRINT_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("disable-assertions")
                                       .withDescription("Disables internal program self-check during execution. This may improve performance.")
                                       .create(DISABLE_ASSERTIONS_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("use-abc-var-names")
                                       .withDescription("If specified, program will use ABC names for variables " +
                                                        "(like 'a', 'b', ..., 'z' instead of 'x1', 'x2', etc.) during formula output." +
                                                        "\nDisabled by default. Forced disabled if variables count more than 26.")
                                       .create(USE_ABC_VAR_NAMES_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("hss-image-output")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("File name where visual representation of resulting basic graph will be written (only for SAT instances). Defaults to <input-file-name>-hss-0.png")
                                       .create(HSS_IMAGE_OUTPUT_FILENAME_OPTION));

        options.addOption(OptionBuilder.withLongOpt("output")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("File name where results of calculation will be written (time measurements and satisfying set for SAT instances). Defaults to <input-file-name>-results.txt")
                                       .create(RESULTS_OUTPUT_FILE_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("evaluate-formula")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("Evaluate formula using variable values from this file.")
                                       .create(EVALUATE_OPTION));

        options.addOption(OptionBuilder.withLongOpt("create-skt")
                                       .withDescription("Convert input formula to Romanov SKT file format.")
                                       .create(CREATE_SKT_OPTION));

        options.addOption(OptionBuilder.withLongOpt("find-hss-route")
                                       .hasArg()
                                       .withArgName("dirname")
                                       .withDescription("Find route in HSS from folder <dirname>")
                                       .create(FIND_HSS_ROUTE_OPTION));

        options.addOption(OptionBuilder.withLongOpt("generate-3sat-formula")
                                       .withDescription("Generate 3-SAT formula from <input-file-name> and save it to <input-file-name>-3sat.cnf.")
                                       .create(GENERATE_3SAT_OPTION));

        return options;
    }

    private static void assertNoTripletsLost(ITabularFormula formula, ObjectArrayList ctf)
    {
        int tripletCount = 0;
        for (int i = 0; i < ctf.size(); i++)
        {
            ITabularFormula f = (ITabularFormula) ctf.get(i);
            for (int j = 0; j < f.getTiers().size(); j++)
            {
                ITier tier = f.getTier(j);
                if (!formula.containsAllValuesOf(tier))
                {
                    throw new AssertionError("CTF triplet not found in initial formula");
                }
                else
                {
                    tripletCount += tier.size();
                }
            }
        }
        if (tripletCount != formula.getClausesCount())
        {
            throw new AssertionError("Bad CTF: tripletCount != formula.getClausesCount()");
        }
    }

    private static void verifySatisfiable(ITabularFormula formula, ObjectArrayList route)
    {
        boolean satisfiable = false;
        try
        {
            satisfiable = formula.evaluate(route);
        }
        catch (NullPointerException e)
        {
            //  Bad route
            e.printStackTrace();
        }
        
        if (!satisfiable)
        {
            throw new AssertionError("HSS was built but initial formula is not satisfiable with values from HS route");
        }
        else
        {
            LOGGER.info("Initial formula verified as satisfiable with variables from HSS route");
        }
    }

    private static void verifySatisfiable(ObjectArrayList ctf, ObjectArrayList route)
    {
        boolean satisfiable = false;
        try
        {
            satisfiable = Helper.evaluate(ctf, route);
        }
        catch (NullPointerException e)
        {
            //  Bad route
            e.printStackTrace();
        }
        
        if (!satisfiable)
        {
            throw new AssertionError("HSS was built but CTF is not satisfiable with values from HS route");
        }
        else
        {
            LOGGER.info("CTF verified as satisfiable with variables from HSS route");
        }
    }
}
