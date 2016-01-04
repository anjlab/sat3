package com.anjlab.sat3.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public class SimpleSystem implements ISystem
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSystem.class);
    
    private final ObjectArrayList ccss;
    private final ObjectArrayList cics;
    
    public SimpleSystem(ObjectArrayList ccss)
    {
        this.ccss = ccss;
        this.cics = new ObjectArrayList(ccss.size());
        
        for (int i = 0; i < ccss.size(); i++)
        {
            ICompactCouplesStructure ccs = (ICompactCouplesStructure) ccss.get(i);
            cics.add(new SimpleColumnsInversionControl(ccs.getPermutation()));
        }
    }

    private SimpleSystem(SimpleSystem copyFrom)
    {
        this.ccss = new ObjectArrayList(copyFrom.ccss.size());
        for (int i = 0; i < copyFrom.ccss.size(); i++)
        {
            ICompactCouplesStructure ccs =
                    (ICompactCouplesStructure) copyFrom.ccss.get(i);
            
            this.ccss.add(ccs.clone());
        }
        
        this.cics = new ObjectArrayList(copyFrom.cics.size());
        for (int i = 0; i < copyFrom.cics.size(); i++)
        {
            IColumnsInversionControl cic =
                    (IColumnsInversionControl) copyFrom.cics.get(i);
            
            this.cics.add(cic.clone());
        }
    }

    @Override
    public IColumnsInversionControl getCIC()
    {
        return (IColumnsInversionControl) cics.get(0);
    }

    @Override
    public ObjectArrayList getCCSs()
    {
        return ccss;
    }

    @Override
    public ObjectArrayList getCICs()
    {
        return cics;
    }
    
    @Override
    public Value valueOf(int varName)
    {
        Value result = Value.Mixed;
        for (int i = 0; i < ccss.size(); i++)
        {
            ICompactCouplesStructure ccs = (ICompactCouplesStructure) ccss.get(0);
            
            Value value = ccs.valueOf(varName);
            
            if (value == Value.Mixed)
            {
                return Value.Mixed;
            }
            
            if (i == 0)
            {
                result = value;
            }
            else if (result != value)
            {
                return Value.Mixed;
            }
        }
        return result;
    }

    private void inverse(int varName)
    {
        for (int i = 0; i < ccss.size(); i++)
        {
            ICompactCouplesStructure ccs = (ICompactCouplesStructure) ccss.get(i);
            
            ccs.inverse(varName);
        }
    }

    private boolean setNegative(int varName)
    {
        for (int i = 0; i < cics.size(); i++)
        {
            IColumnsInversionControl cic = (IColumnsInversionControl) cics.get(i);
            if (!cic.setNegative(varName))
            {
                return false;
            }
        }
        return true;
    }

    private boolean setPlain(int varName)
    {
        for (int i = 0; i < cics.size(); i++)
        {
            IColumnsInversionControl cic = (IColumnsInversionControl) cics.get(i);
            if (!cic.setPlain(varName))
            {
                return false;
            }
        }
        return true;
    }
    
    public void fixConstantValues()
    {
        IPermutation permutation = getCIC().getPermutation();
        int size = permutation.size();
        int[] elements = permutation.elements();
        
        for (int i = 0; i < size; i++)
        {
            int varName = elements[i];
            
            Value value = valueOf(varName);
            switch (value)
            {
            case AllNegative:
                inverse(varName);
                setNegative(varName);
                break;
            case AllPlain:
                setPlain(varName);
                break;

            default:
                //  No nothing
                break;
            }
        }
    }
    
    @Override
    public int getStartingVarName()
    {
        IColumnsInversionControl cic = getCIC();
        for (int i = 0; i < cic.getPermutation().size(); i++)
        {
            int varName = cic.getPermutation().get(i);
            if (cic.isNotFixed(varName))
            {
                return varName;
            }
        }
        return -1;
    }
    
    @Override
    public ISystem clone()
    {
        return new SimpleSystem(this);
    }
    
    @Override
    public boolean trySet(int varName, Value value)
    {
        //  Because each CCS has its own copy of a CIC vector
        //  we may implement constraints distribution algorithm
        //  separately for each CCS,
        //  and then synchronize differences/detect conflicts if any
        
        //  1) Distribute constraints in each CCS separately
        for (int i = 0; i < cics.size(); i++)
        {
            IColumnsInversionControl cic = (IColumnsInversionControl) cics.get(i);
            ICompactCouplesStructure ccs = (ICompactCouplesStructure) ccss.get(i);
            boolean succeeded = ccs.trySet(varName, value, cic);
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("CCS #{} after setting varName {} to {} => {}",
                        new Object[] { i, varName, value, succeeded });
                
                Helper2.prettyPrint(ccs, cic);
            }
            
            if (!succeeded)
            {
                //  Exit early
                return false;
            }
        }
        
        //  2) Compare all CICs, make sure there are no conflicts.
        //  In case of conflict return false.
        
        
        //  FIXME Right now it's O(n^2), could be O(n)
        boolean dirty;
        do
        {
            dirty = false;
            for (int i = 0; i < cics.size(); i++)
            {
                IColumnsInversionControl cicI = (IColumnsInversionControl) cics.get(i);
                for (int j = 0; j < cics.size(); j++)
                {
                    if (i == j)
                    {
                        continue;
                    }
                    
                    IColumnsInversionControl cicJ = (IColumnsInversionControl) cics.get(j);
                    
                    for (int k = 0; k < cicI.getPermutation().size(); k++)
                    {
                        int currVarName = cicI.getPermutation().get(k);
                        
                        if (cicI.isConflicted(currVarName) || cicJ.isConflicted(currVarName))
                        {
                            LOGGER.info("Var {} is in conflict state (1)", currVarName);
                            //  Set conflicted marker for debug purposes
                            cicI.setConflicted(currVarName);
                            cicJ.setConflicted(currVarName);
                            return false;
                        }
                        
                        if ((cicI.isFixedPlain(currVarName) && cicJ.isFixedNegative(currVarName))
                                || (cicI.isFixedNegative(currVarName) && cicJ.isFixedPlain(currVarName)))
                        {
                            LOGGER.info("Var {} is in conflict state (2)", currVarName);
                            //  Set conflicted marker for debug purposes
                            cicI.setConflicted(currVarName);
                            cicJ.setConflicted(currVarName);
                            return false;
                        }
                        
                        if (cicI.isFixed(currVarName) && cicJ.isNotFixed(currVarName))
                        {
                            Value fixedValue = cicI.getFixedValue(currVarName);
                            
                            if (LOGGER.isDebugEnabled())
                            {
                                LOGGER.info("Var {} is fixed in CCS#{}, but is free in CCS#{}, trying to set it to {}",
                                        new Object[] { currVarName, i, j, fixedValue });
                            }
                            
                            ICompactCouplesStructure ccsJ = (ICompactCouplesStructure) ccss.get(j);
                            ccsJ.trySet(currVarName, fixedValue, cicJ);
                            
                            if (LOGGER.isDebugEnabled())
                            {
                                Helper2.prettyPrint(ccsJ, cicJ);
                            }
                            
                            dirty = true;
                        }
                        
                        if (cicI.isNotFixed(currVarName) && cicJ.isFixed(currVarName))
                        {
                            Value fixedValue = cicJ.getFixedValue(currVarName);
                            
                            if (LOGGER.isDebugEnabled())
                            {
                                LOGGER.info("Var {} is fixed in CCS#{}, but is free in CCS#{}, trying to set it to {}",
                                        new Object[] { currVarName, j, i, fixedValue });
                            }
                            
                            ICompactCouplesStructure ccsI = (ICompactCouplesStructure) ccss.get(i);
                            ccsI.trySet(currVarName, fixedValue, cicI);
                            
                            if (LOGGER.isDebugEnabled())
                            {
                                Helper2.prettyPrint(ccsI, cicI);
                            }
                            
                            dirty = true;
                        }
                    }
                }
            }
        } while (dirty);
        
        //  If some value was fixed in one CIC and is free in another,
        //  then resume distribution in that other CIC/CCS
        //  and repeat from step 1.
        
        //  TODO Find all CICs with paused state and see if some values were fixed for them in other CICs.
        
        return true;
    }
}
