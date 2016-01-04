package com.anjlab.sat3.v2;

import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjlab.sat3.Value;

public class Solver
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Solver.class);
    
    public boolean solve(AtomicReference<ISystem> system)
    {
        system.get().fixConstantValues();
        
        for (int counter = 1; ; counter++)
        {
            int startingVarName = system.get().getStartingVarName();
            
            if (startingVarName == -1)
            {
                break;
            }
            
            ISystem safepoint = system.get().clone();
            
            LOGGER.info("Iteration #{} with starting var {}", counter, startingVarName);
            
            Helper2.prettyPrint(system.get());
            
            LOGGER.info("Trying to set " + startingVarName + " to 0");
            
            boolean succeeded = system.get().trySet(startingVarName, Value.AllPlain);
            
            if (!succeeded)
            {
                LOGGER.info("Trying to set " + startingVarName + " to 1");
                
                //  Rollback
                system.set(safepoint);
                
                succeeded = system.get().trySet(startingVarName, Value.AllNegative);
                
                if (!succeeded)
                {
                    LOGGER.info("Unable to set " + startingVarName + " => UNSAT");
                    return false;
                }
            }
        }
        
        return true;
    }
}
