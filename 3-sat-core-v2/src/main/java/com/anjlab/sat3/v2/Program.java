package com.anjlab.sat3.v2;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjlab.sat3.EmptyStructureException;
import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ITabularFormula;

import cern.colt.list.ObjectArrayList;

public class Program
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Program.class);

    public static final int SAT = 0;
    public static final int UNSAT = -1;

    public static int main(String[] args) throws IOException
    {
        String formulaFile = args[0];
        ITabularFormula formula = Helper.loadFromFile(formulaFile);

        if (formula.getVarCount() > 26)
        {
            LOGGER.info("Variables count > 26 => force using universal names for variables.");
            Helper2.UseUniversalVarNames = true;
        }

        ObjectArrayList ct;
        try
        {
            ct = Helper.createCTF(formula);
            Helper.completeToCTS(ct, formula.getPermutation());
            Helper.unify(ct);
        }
        catch (EmptyStructureException e)
        {
            LOGGER.info("Empty structure", e);
            return UNSAT;
        }
        
        ISystem system = new SimpleSystem(Helper2.createCCS(ct));
        AtomicReference<ISystem> reference = new AtomicReference<ISystem>(system);
        boolean sat = new Solver().solve(reference);
        system = reference.get();
        System.out.println("SAT: " + sat);
        if (sat)
        {
            if (!Helper2.evaluate(formula, system.getCIC()))
            {
                throw new AssertionError("Formula was classified as SAT,"
                        + " but founded satisfying set failed evaluation");
            }
            return SAT;
        }
        return UNSAT;
    }
}
