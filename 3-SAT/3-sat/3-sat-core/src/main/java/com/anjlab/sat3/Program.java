package com.anjlab.sat3;



public class Program
{
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
        	System.out.println("Usage:\n\tjava " + Program.class.getName() + " formula.cnf");
        	System.exit(0);
        }

        StopWatch stopWatch = new StopWatch(true);
        
        ITabularFormula formula = Helper.loadFromDIMACSFileFormat(args[0]);
        
        stopWatch.nextLap("Formula loaded");
        
        Helper.prettyPrint(formula);

        Helper.printLine('*', 70);

        GenericArrayList<ITabularFormula> ctf = Helper.createCTF(formula);

        stopWatch.nextLap("CTF created");
        
        for (int i = 0; i < ctf.size(); i++)
        {
        	ITabularFormula f = ctf.get(i);
        	
            f.sortTiers();

            Helper.prettyPrint(f);
        }

        stopWatch.nextLap("CTF tiers sorted");
        
        Helper.printLine('*', 70);

        GenericArrayList<ICompactTripletsStructure> cts = Helper.createCTS(formula, ctf);

        stopWatch.nextLap("CTS created");
        
        for (int i = 0; i < cts.size(); i++)
        {
        	ICompactTripletsStructure f = cts.get(i);
            Helper.prettyPrint(f);
        }

        System.out.println("CTF: " + ctf.size());

    }
}
