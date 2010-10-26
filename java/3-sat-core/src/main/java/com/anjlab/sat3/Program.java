package com.anjlab.sat3;

import static com.anjlab.sat3.Helper.printFormulas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.list.ObjectArrayList;

public class Program
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);
    
    public static void main(String[] args) throws Exception
    {
        if (args.length != 1)
        {
            System.out.println("Usage:\n\tjava [-Dverbose=true] " + Program.class.getName() + " formula.cnf");
            System.exit(0);
        }

        String verbose = System.getProperty("verbose");
        
        Helper.UsePrettyPrint = verbose != null && verbose.equalsIgnoreCase("true");
        Helper.EnableAssertions = true;
        Helper.UseUniversalVarNames = true;

        StopWatch stopWatch = new StopWatch();
        
        try
        {
            stopWatch.start("Load formula");
            ITabularFormula formula = Helper.loadFromGenericDIMACSFileFormat(args[0]);
            stopWatch.stop();
            Helper.prettyPrint(formula);
            stopWatch.printElapsed();
    
            stopWatch.start("Create CTF");
            ObjectArrayList ct = Helper.createCTF(formula);
            stopWatch.stop();
            printFormulas(ct);
            LOGGER.info("CTF: {}", ct.size());
            stopWatch.printElapsed();
    
            stopWatch.start("Create CTS");
            Helper.createCTS(formula, ct);
            stopWatch.stop();
            //  formula may be garbage collected now
            formula = null;
            printFormulas(ct);
            stopWatch.printElapsed();
    
    //        saveCTS(args[0], ct);
            
            stopWatch.start("Unify all CTS");
            Helper.unify(ct);
            stopWatch.stop();
            printFormulas(ct);
            stopWatch.printElapsed();

            LOGGER.info("CTF: {}", ct.size());
            
            stopWatch.start("Create HSS");
            ObjectArrayList hss = Helper.createHyperStructuresSystem(ct);
            stopWatch.stop();
            stopWatch.printElapsed();

            for (int h = 0; h < hss.size(); h++)
            {
                stopWatch.start("Find HSS(" + h + ") route");
                ObjectArrayList route = Helper.findHyperStructureRoute((IHyperStructure) hss.get(h));
                stopWatch.stop();
                stopWatch.printElapsed();

//              stopWatch.start("Calculate CTS from the route");
//              ICompactTripletsStructure s = Helper.intersectAll(route);
//              stopWatch.stop();
//              Helper.prettyPrint(s);
//              stopWatch.printElapsed();

                String filename = "target/hss-0" + h + ".png";
                stopWatch.start("Write HSS(" + h + ") to image filename " + filename);
                Helper.writeToImage((IHyperStructure) hss.get(h), route, filename);
                stopWatch.stop();
                stopWatch.printElapsed();
            }
        }
        catch (EmptyStructureException e)
        {
            stopWatch.stop();
            stopWatch.printElapsed();
            
            System.out.println("Formula not satisfiable, because one of the structures was built empty");
            e.printStackTrace();
        }
        finally
        {
            System.out.println("Program completed");
        }
    }
}
