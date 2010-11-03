package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.printFormulas;

import java.io.File;
import java.io.FileInputStream;
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
        Options options = getCommandLineOptions();
        
        CommandLineParser parser = new PosixParser();
        CommandLine commandLine = parser.parse(options, args);

        if (commandLine.getArgs().length != 1 || commandLine.hasOption(HELP_OPTION))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Program.class.getName() + " [OPTIONS] <PATH>" +
                    "\nWhere <PATH> is a path to file containing N-SAT formula instance in DIMACS CNF or Romanov SKT file format.", options);
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
            stopWatch.start("Load formula");
            ITabularFormula formula = Helper.loadFromFile(formulaFile);
            long timeElapsed = stopWatch.stop();
            
            statistics.put("InitialFormulaLoadTime", String.valueOf(timeElapsed));
            
            if (formula.getVarCount() > 26)
            {
                LOGGER.info("Variables count > 26 => force using universal names for variables.");
                Helper.UseUniversalVarNames = true;
            }
            
            statistics.put("InitialFormulaVarCount", String.valueOf(formula.getVarCount()));
            statistics.put("InitialFormulaClausesCount", String.valueOf(formula.getClausesCount()));
            
            Helper.prettyPrint(formula);
            stopWatch.printElapsed();
            
            if (commandLine.hasOption(EVALUATE_OPTION))
            {
                stopWatch.start("Evaluate formula");
                Properties properties = new Properties();
                FileInputStream is = null;
                try
                {
                    is = new FileInputStream(new File(commandLine.getOptionValue(EVALUATE_OPTION)));
                    properties.load(is);
                    boolean satisfiable = formula.evaluate(properties);
                    stopWatch.stop();
                    if (satisfiable)
                    {
                        System.out.println("Formula evaluated as SAT");
                    }
                    else
                    {
                        System.out.println("Formula evaluated as UNSAT");
                    }
                    stopWatch.printElapsed();
                }
                finally
                {
                    if (is != null)
                    {
                        is.close();
                    }
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
            
            stopWatch.start("CreateCTF");
            ObjectArrayList ct = Helper.createCTF(formula);
            timeElapsed = stopWatch.stop();
            printFormulas(ct);
            stopWatch.printElapsed();
            
            statistics.put("CTFCreationTime", String.valueOf(timeElapsed));
            statistics.put("CTFCount", String.valueOf(ct.size()));
            
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
            
            statistics.put("CTSCreationTime", String.valueOf(timeElapsed));
            
            stopWatch.start("Unify all CTS");
            Helper.unify(ct);
            timeElapsed = stopWatch.stop();
            printFormulas(ct);
            stopWatch.printElapsed();

            statistics.put("CTSUnificationTime", String.valueOf(timeElapsed));
            
            LOGGER.info("CTF: {}", ct.size());
            
            stopWatch.start("Create HSS");
            ObjectArrayList hss = Helper.createHyperStructuresSystem(ct, statistics);
            timeElapsed = stopWatch.stop();
            stopWatch.printElapsed();

            statistics.put("HSSCreationTime", String.valueOf(timeElapsed));
            statistics.put("BasicCTSFinalClausesCount", String.valueOf(((IHyperStructure) hss.get(0)).getBasicCTS().getClausesCount()));
            
            stopWatch.start("Find HSS(0) route");
            ObjectArrayList route = Helper.findHSSRoute(hss);
            timeElapsed = stopWatch.stop();
            stopWatch.printElapsed();

            statistics.put("SearchHSSRouteTime", String.valueOf(timeElapsed));
            
            if (Helper.EnableAssertions)
            {
                if (!formula.equals(formulaClone))
                {
                    LOGGER.warn("Initial formula differs from its cloned version");
                }
            }
            
            stopWatch.start("Verify formula is satisfiable using variable values from HSS route");
            verifySatisfiable(formula, route);
            if (Helper.EnableAssertions)
            {
                verifySatisfiable(ctfClone, route);
            }
            stopWatch.stop();
            stopWatch.printElapsed();

            //  Draw non-empty intersections of HSS(0) with last vertex in HSS route 
            //  to illustrate possible options of HSS route search
            ObjectArrayList markers = Helper.findNonEmptyIntersections((IHyperStructure) hss.get(0), (IVertex) route.get(route.size() - 1));
            
            String hssImageFile = formulaFile + "-hss-0.png";
            
            if (commandLine.hasOption(HSS_IMAGE_OUTPUT_FILENAME_OPTION))
            {
                hssImageFile = commandLine.getOptionValue(HSS_IMAGE_OUTPUT_FILENAME_OPTION);
            }
            
            stopWatch.start("Write HSS as image to " + hssImageFile);
            Helper.writeToImage((IHyperStructure) hss.get(0), route, markers, hssImageFile);
            stopWatch.stop();
            stopWatch.printElapsed();
            
            String resultsFilename = getResultsFilename(commandLine, formulaFile);
            stopWatch.start("Write HSS route to " + resultsFilename);
            writeSatToFile(resultsFilename, statistics, route);
            stopWatch.stop();
            stopWatch.printElapsed();
        }
        catch (EmptyStructureException e)
        {
            stopWatch.stop();
            stopWatch.printElapsed();
            
            writeUnsatToFile(getResultsFilename(commandLine, formulaFile), statistics);
            
            LOGGER.info("One of the structures was built empty", e);
            
            System.out.println("Formula not satisfiable");
        }
        finally
        {
            System.out.println("Program completed");
        }
    }

    private static void writeUnsatToFile(String resultsFile, Properties statistics) throws IOException
    {
        OutputStream out = null;
        try
        {
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

    private static void writeSatToFile(String resultsFile, Properties statistics, ObjectArrayList route) throws IOException
    {
        OutputStream out = null;
        try
        {
            out = new FileOutputStream(new File(resultsFile));
            
            IVertex vertex = null;
            for (int i = 0; i < route.size(); i++)
            {
                vertex = (IVertex) route.get(i);
                statistics.put(String.valueOf(vertex.getPermutation().getAName()), String.valueOf(vertex.getTripletValue().isNotA()));
            }
            if (vertex != null)
            {
                statistics.put(String.valueOf(vertex.getPermutation().getBName()), String.valueOf(vertex.getTripletValue().isNotB()));
                statistics.put(String.valueOf(vertex.getPermutation().getCName()), String.valueOf(vertex.getTripletValue().isNotC()));
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
                                       .withDescription("Disables program self-check during execution.")
                                       .create(DISABLE_ASSERTIONS_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("use-abc-var-names")
                                       .withDescription("If specified, program will use ABC names for variables " +
                                                        "(like 'a', 'b', ..., 'z' instead of 'x1', 'x2', etc.) during formula output." +
                                                        "\nDisabled by default. Forced disabled if variables count more than 26.")
                                       .create(USE_ABC_VAR_NAMES_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("hss-image-output")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("File name where firsth resulting hyperstructure image will be written (if built any). Defaults to <PATH>-hss-0.png")
                                       .create(HSS_IMAGE_OUTPUT_FILENAME_OPTION));

        options.addOption(OptionBuilder.withLongOpt("output")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("File name where resulting HSS route will be written (if found any). Defaults to <PATH>-results.txt")
                                       .create(RESULTS_OUTPUT_FILE_OPTION));
        
        options.addOption(OptionBuilder.withLongOpt("evaluate-formula")
                                       .hasArg()
                                       .withArgName("filename")
                                       .withDescription("Evaluate formula using variable values from this file.")
                                       .create(EVALUATE_OPTION));

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
