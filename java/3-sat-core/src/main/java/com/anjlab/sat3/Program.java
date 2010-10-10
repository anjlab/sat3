package com.anjlab.sat3;

import java.io.IOException;

public class Program
{
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("Usage:\n\tjava [-Dverbose=true] " + Program.class.getName() + " formula.cnf");
            System.exit(0);
        }

        String verbose = System.getProperty("verbose");
        
        Helper.UsePrettyPrint = verbose != null && verbose.equalsIgnoreCase("true");

        StopWatch stopWatch = new StopWatch();
        
        stopWatch.start("Load formula");
        ITabularFormula formula = Helper.loadFromDIMACSFileFormat(args[0]);
        stopWatch.stop();
        
        Helper.prettyPrint(formula);

        stopWatch.printElapsed();

        Helper.printLine('*', 70);

        stopWatch.start("Create CTF");
        GenericArrayList<ITabularFormula> ctf = Helper.createCTF(formula);
        stopWatch.stop();

        stopWatch.printElapsed();
        
        stopWatch.start("Sort CTF tiers");
        for (int i = 0; i < ctf.size(); i++)
        {
            ctf.get(i).sortTiers();
        }
        stopWatch.stop();

        printFormulas(ctf);
        
        stopWatch.printElapsed();

        Helper.printLine('*', 70);

        stopWatch.start("Create CTS");
        GenericArrayList<ICompactTripletsStructure> cts = Helper.createCTS(formula, ctf);
        stopWatch.stop();

        printFormulas(cts);

        stopWatch.printElapsed();

        System.out.println("CTF: " + ctf.size());

//        saveCTS(args[0], cts);
        
        stopWatch.start("Unify all CTS");
        Helper.unify(cts);
        stopWatch.stop();
        
        stopWatch.printElapsed();
        
        System.out.println("Program completed");
    }

    @SuppressWarnings("unused")
    private static void saveCTS(String filenamePrefix, GenericArrayList<ITabularFormula> cts) throws IOException
    {
        System.out.println("Saving CTS to file system...");
        
        for (int i = 0; i < cts.size(); i++)
        {
            ITabularFormula f = cts.get(i);
            String filename = filenamePrefix + "-cts-" + i + ".cnf";
            System.out.print("Saving " + filename + "...");
            Helper.saveToDIMACSFileFormat(f, filename);
            System.out.println(" done");
        }
    }

    private static void printFormulas(GenericArrayList<?> formulas)
    {
        for (int i = 0; i < formulas.size(); i++)
        {
            Helper.prettyPrint((ITabularFormula) formulas.get(i));
        }
    }
}
