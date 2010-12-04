package com.anjlab.sat3;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.function.LongObjectProcedure;
import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenLongObjectHashMap;

public class VarPairsIndexFactory
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VarPairsIndexFactory.class);
    
    private static VarPairsIndexFactory instance = new VarPairsIndexFactory();
    
    private OpenIntObjectHashMap indexCache = new OpenIntObjectHashMap();
    
    public static VarPairsIndexFactory getInstance()
    {
        return instance;
    }
    
    public class VarPairsIndex
    {
        private OpenLongObjectHashMap index = new OpenLongObjectHashMap();
        
        public void updateTier(int varName1, int varName2, ITier tier)
        {
            long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;

            //  List of ITier
            ObjectArrayList tiers = (ObjectArrayList) index.get(key);

            if (tiers == null)
            {
                //  Nothing to update. varName1 and varName2 does not meet in two different formulas
                return;
            }
            
            if (Helper.EnableAssertions)
            {
                int varName1_ = (int) (key >> 21);
                int varName2_ = (int) (key & 0x1FFFFF);
        
                if (((varName1 != varName1_) && (varName1 != varName2_))
                        || ((varName2 != varName1_) && (varName2 != varName2_)))
                {
                    throw new AssertionError("Bad hash");
                }
            }
            
            for (int i = 0; i < tiers.size(); i++)
            {
                ITier t = (ITier) tiers.getQuick(i);
                if (Arrays.equals(t.getABC(), tier.getABC()) 
                        && t.getFormula().getPermutation().sameAs(tier.getFormula().getPermutation()))
                {
                    tiers.setQuick(i, tier);
                    return;
                }
            }
            throw new AssertionError("Update tier mismatch");
        }
        
        public void addTier(int varName1, int varName2, ITier tier)
        {
            long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;

            if (Helper.EnableAssertions)
            {
                int varName1_ = (int) (key >> 21);
                int varName2_ = (int) (key & 0x1FFFFF);
        
                if (((varName1 != varName1_) && (varName1 != varName2_))
                        || ((varName2 != varName1_) && (varName2 != varName2_)))
                {
                    throw new AssertionError("Bad hash");
                }
            }

            //  List of ITier
            ObjectArrayList tiers = (ObjectArrayList) index.get(key);
            
            if (tiers == null)
            {
                index.put(key, new ObjectArrayList(new ITier[] {tier}));
            }
            else
            {
                if (!tier.hasVariable(varName1) || !tier.hasVariable(varName2))
                {
                    throw new IllegalStateException();
                }
                tiers.add(tier);
            }
        }

        public void purge()
        {
            final LongArrayList toBeRemoved = new LongArrayList();
            
            index.forEachPair(new LongObjectProcedure()
            {
                public boolean apply(long key, Object value)
                {
                    //  List of ITier
                    ObjectArrayList tiers = (ObjectArrayList) value;
                    if (tiers.size() < 2)
                    {
                        toBeRemoved.add(key);
                    }
                    else
                    {
                        ITabularFormula formula = ((ITier)tiers.get(0)).getFormula();
                        for (int i = 1; i < tiers.size(); i++)
                        {
                            if (formula != ((ITier)tiers.get(i)).getFormula())
                            {
                                //  Found distinct formulas
                                return true;
                            }
                        }
                        //  All triplets are from the same formula
                        toBeRemoved.add(key);
                    }
                    return true;
                }
            });
            
            int size = toBeRemoved.size();
            for (int i = 0; i < size; i++)
            {
                long key = toBeRemoved.getQuick(i);
                index.removeKey(key);
            }
            LOGGER.debug("Removed {} triplet permutations from index", size);
        }

        public void forEachPair(LongObjectProcedure procedure)
        {
            index.forEachPair(procedure);
        }
    }
    
    /**
     * 
     * @param cts List of {@link ICompactTripletsStructureHolder}
     * @return
     * @throws EmptyStructureException
     */
    public VarPairsIndex buildIndex(final ObjectArrayList cts) throws EmptyStructureException
    {
        VarPairsIndex index;
        
        int cacheKey = getCacheKey(cts);
        
        boolean creatingNewIndex = !indexCache.containsKey(cacheKey);
        
        if (creatingNewIndex)
        {
            index = new VarPairsIndex();
            indexCache.put(cacheKey, index);
        }
        else
        {
            index = (VarPairsIndex) indexCache.get(cacheKey);
        }
            
        LOGGER.debug("Building pairs index...");
        
        int varCount = ((ICompactTripletsStructureHolder) cts.get(0)).getCTS().getPermutation().size();
        int tierCount = varCount - 2;
        int ctsCount = cts.size();
        
        for(int i = 0; i < ctsCount; i++)
        {
            ITabularFormula s = ((ICompactTripletsStructureHolder) cts.get(i)).getCTS();
            if(s.isEmpty())
            {
                throw new EmptyStructureException(s);
            }
            
            Object[] tierElements = s.getTiers().elements();
            
            for (int j = 0; j < tierCount; j++)
            {
                ITier tier = (ITier) tierElements[j];
                
                ((SimpleTier)tier).setFormula(s);
                
                if (creatingNewIndex)
                {
                    index.addTier(tier.getAName(), tier.getBName(), tier);
                    index.addTier(tier.getAName(), tier.getCName(), tier);
                    index.addTier(tier.getBName(), tier.getCName(), tier);
                }
                else
                {
                    index.updateTier(tier.getAName(), tier.getBName(), tier);
                    index.updateTier(tier.getAName(), tier.getCName(), tier);
                    index.updateTier(tier.getBName(), tier.getCName(), tier);
                }
            }
        }
        
        if (creatingNewIndex)
        {
            index.purge();
        }
        
        return index;
    }

    private int getCacheKey(ObjectArrayList cts)
    {
        int result = 1;
        for (int i = 0; i < cts.size(); i++)
        {
            IPermutation permutation = ((ICompactTripletsStructureHolder) cts.get(i)).getCTS().getPermutation();
            //  See Arrays.hashCode(int[])
            result = 31 * result + permutation.elementsHash();
        }
        return result;
    }

}
