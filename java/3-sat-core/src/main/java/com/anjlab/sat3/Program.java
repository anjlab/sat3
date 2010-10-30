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
    
            ITabularFormula formulaClone = formula.clone();
            
            stopWatch.start("Create CTF");
            ObjectArrayList ct = Helper.createCTF(formula);
            stopWatch.stop();
            printFormulas(ct);
            LOGGER.info("CTF: {}", ct.size());
            stopWatch.printElapsed();

            assertNoTripletsLost(formula, ct);

            ObjectArrayList ctfClone = new ObjectArrayList();
            for (int i = 0; i < ct.size(); i++)
            {
                ctfClone.add(((ITabularFormula) ct.get(i)).clone());
            }
            
            stopWatch.start("Create CTS");
            Helper.completeToCTS(ct, formula.getPermutation());
            stopWatch.stop();
//            formula = null;
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

            stopWatch.start("Find HSS(0) route");
            ObjectArrayList route = Helper.findHSSRoute(hss);
            stopWatch.stop();
            stopWatch.printElapsed();

            if (!formula.equals(formulaClone))
            {
                LOGGER.warn("Initial formula differs from its cloned version");
            }

            verifySatisfiable(formulaClone, route);
            verifySatisfiable(ctfClone, route);
            
//          stopWatch.start("Calculate CTS from the route");
//          ICompactTripletsStructure s = Helper.intersectAll(route);
//          stopWatch.stop();
//          Helper.prettyPrint(s);
//          stopWatch.printElapsed();

            ObjectArrayList markers = Helper.findNonEmptyIntersections((IHyperStructure) hss.get(0), (IVertex) route.get(route.size() - 1));
            
            String filename = "target/hss.png";
            stopWatch.start("Write HSS to image filename " + filename);
            Helper.writeToImage((IHyperStructure) hss.get(0), route, markers, filename);
            stopWatch.stop();
            stopWatch.printElapsed();
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
//                throw new AssertionError("HSS was built but initial formula is not satisfiable with values from HS route");
            System.err.println("HSS was built but initial formula is not satisfiable with values from HS route");
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
//                throw new AssertionError("HSS was built but CTF is not satisfiable with values from HS route");
            System.err.println("HSS was built but CTF is not satisfiable with values from HS route");
        }
        else
        {
            LOGGER.info("CTF verified as satisfiable with variables from HSS route");
        }
    }
}
