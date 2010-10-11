package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.printFormulas;

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
        Helper.EnableAssertions = false;
        Helper.UseUniversalVarNames = false;

        StopWatch stopWatch = new StopWatch();
        
        stopWatch.start("Load formula");
        ITabularFormula formula = Helper.loadFromDIMACSFileFormat(args[0]);
        stopWatch.stop();
        
        Helper.prettyPrint(formula);

        stopWatch.printElapsed();

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

        stopWatch.start("Create CTS");
        GenericArrayList<ICompactTripletsStructure> cts = Helper.createCTS(formula, ctf);
        stopWatch.stop();

        printFormulas(cts);

        stopWatch.printElapsed();

        System.out.println("CTF: " + ctf.size());

//        saveCTS(args[0], cts);

        stopWatch.printElapsed();
        
        stopWatch.start("Unify all CTS");
        Helper.unify(cts);
        stopWatch.stop();
        
        printFormulas(cts);
        
        stopWatch.printElapsed();
        
        System.out.println("Program completed");
    }

}
