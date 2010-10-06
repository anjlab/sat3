package com.anjlab.sat3;

public class Program
{
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("Usage:\n\tjava [-Dverbose=true] " + Program.class.getName() + " formula.cnf");
            System.exit(0);
        }

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
        @SuppressWarnings("unchecked")
        GenericArrayList<ITabularFormula> cts = (GenericArrayList<ITabularFormula>)Helper.createCTS(formula, ctf);
        stopWatch.stop();

        printFormulas(cts);

        stopWatch.printElapsed();

        System.out.println("CTF: " + ctf.size());

//        System.out.println("Saving CTS to file system...");
//        
//        for (int i = 0; i < cts.size(); i++)
//        {
//            ITabularFormula f = cts.get(i);
//            String filename = args[0] + "-cts-" + i + ".cnf";
//            System.out.print("Saving " + filename + "...");
//            Helper.saveToDIMACSFileFormat(f, filename);
//            System.out.println(" done");
//        }
        
        System.out.println("Program completed");
    }

    private static void printFormulas(GenericArrayList<ITabularFormula> formulas)
    {
        for (int i = 0; i < formulas.size(); i++)
        {
            Helper.prettyPrint(formulas.get(i));
        }
    }
}
