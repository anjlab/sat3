/*
 * Copyright (c) 2010, 2011 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

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
    
    public void clear()
    {
        indexCache.clear();
    }
    
    public class VarPairsIndex
    {
        private OpenLongObjectHashMap pairsToTiersIndex = new OpenLongObjectHashMap();
        private OpenIntObjectHashMap varNameToPairsIndex = new OpenIntObjectHashMap();
        private VarPairsIndex fullIndex;
        
        public VarPairsIndex(VarPairsIndex fullIndex)
        {
            this.fullIndex = fullIndex;
        }
        
        public VarPairsIndex()
        {
        }
        
        /**
         * 
         * @param varName1
         * @param varName2
         * @param formulaIndex Index of formula which tier is adding.
         * @param tierIndex Index of tier, containing varName1 and varName2 in the formula.
         * @param cts List of CTS. Used when {@link Helper#EnableAssertions} is <code>true</code>.
         */
        public void addTier(int varName1, int varName2, int formulaIndex, int tierIndex, ObjectArrayList cts)
        {
            long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;
            long formulaAndTierIndices = (long)formulaIndex << 32 | (long)tierIndex;
            
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

            //  List of formulaAndTierIndices
            LongArrayList tiers = (LongArrayList) pairsToTiersIndex.get(key);
            
            if (tiers == null)
            {
                tiers = new LongArrayList(new long[] {formulaAndTierIndices});
                pairsToTiersIndex.put(key, tiers);
            }
            else
            {
                if (Helper.EnableAssertions)
                {
                    ITier tier = ((ICompactTripletsStructureHolder) cts.get(formulaIndex)).getCTS().getTier(tierIndex);
                    if (!tier.hasVariable(varName1) || !tier.hasVariable(varName2))
                    {
                        throw new IllegalStateException();
                    }
                }
                
                //  Ensure tier will be added only once
                if (tiers.indexOf(formulaAndTierIndices) < 0)
                {
                    tiers.add(formulaAndTierIndices);
                }
            }
        }

        /**
         * Remove pairs from index that only belong to single CTS
         */
        public void purge()
        {
            final LongArrayList toBeRemoved = new LongArrayList();
            
            pairsToTiersIndex.forEachPair(new LongObjectProcedure()
            {
                public boolean apply(long key, Object value)
                {
                    //  List of formulaAndTierIndices
                    LongArrayList tiers = (LongArrayList) value;
                    int tiersCount = tiers.size();
                    if (tiersCount < 2)
                    {
                        toBeRemoved.add(key);
                    }
                    else
                    {
                        long formulaAndTierIndex = tiers.getQuick(0);
                        long formulaIndexPart = formulaAndTierIndex & 0xFFFFFFFF00000000L;
                        for (int i = 1; i < tiersCount; i++)
                        {
                            long formulaAndTierIndex2 = tiers.getQuick(i);
                            long formulaIndexPart2 = formulaAndTierIndex2 & 0xFFFFFFFF00000000L;
                            if (formulaIndexPart != formulaIndexPart2)
                            {
                                //  Found distinct formulas
                                
                                //  See VarPairsIndex#addTier() for details of key construction
                                int varName1 = (int) (key >> 21);
                                int varName2 = (int) (key & 0x1FFFFF);
                                
                                addVarNamePair(varName1, key);
                                addVarNamePair(varName2, key);
                                
                                return true;
                            }
                        }
                        //  All triplets are from the same formula
                        toBeRemoved.add(key);
                    }
                    return true;
                }

                private void addVarNamePair(int varName1, long key)
                {
                    LongArrayList pairs = (LongArrayList) varNameToPairsIndex.get(varName1);
                    if (pairs == null)
                    {
                        pairs = new LongArrayList(new long[]{ key });
                        varNameToPairsIndex.put(varName1, pairs);
                    }
                    else
                    {
                        pairs.add(key);
                    }
                }
            });
            
            int size = toBeRemoved.size();
            for (int i = 0; i < size; i++)
            {
                long key = toBeRemoved.getQuick(i);
                pairsToTiersIndex.removeKey(key);
            }
            LOGGER.debug("Removed {} triplet permutations from index", size);
        }

        public void forEachPair(LongObjectProcedure procedure)
        {
            pairsToTiersIndex.forEachPair(procedure);
        }

        public LongArrayList pairs()
        {
            return pairsToTiersIndex.keys();
        }

        public LongArrayList getPairs(int varName)
        {
            return (LongArrayList) varNameToPairsIndex.get(varName);
        }

        public void rebuildIndex(ObjectArrayList cts, ICompactTripletsStructureHolder formula, int fromTier, int toTier)
        {
            for (int j = fromTier; j <= toTier; j++)
            {
                ITier tier = formula.getCTS().getTier(j);
                
                addVarName(tier.getAName());
                addVarName(tier.getBName());
                addVarName(tier.getCName());
            }
        }

        private void addVarName(int varName)
        {
            LongArrayList pairs = fullIndex.getPairs(varName);
            
            if (pairs == null)
            {
                //  There is no any pairs that contain this varName.
                //  No need to add it to the index.
                return;
            }
            
            for (int i = 0; i < pairs.size(); i++)
            {
                long key = pairs.getQuick(i);
                if (!pairsToTiersIndex.containsKey(key))
                {
                    pairsToTiersIndex.put(key, fullIndex.pairsToTiersIndex.get(key));
                }
            }
        }
    }
    
    /**
     *  
     * 
     * @param cts
     * @param formula
     * @param fromTier Index of start tier in the formula
     * @param toTier Index of end tier in the formula
     * @return
     */
    public VarPairsIndex buildPartialIndex(final ObjectArrayList cts, ICompactTripletsStructureHolder formula, int fromTier, int toTier)
    {
        int cacheKey = getCacheKey(cts);
        
        VarPairsIndex fullIndex = (VarPairsIndex) indexCache.get(cacheKey);
        
        if (fullIndex == null)
        {
            throw new IllegalStateException("Full index should be built first");
        }
        
        VarPairsIndex index = new VarPairsIndex(fullIndex);
        
        index.rebuildIndex(cts, formula, fromTier, toTier);
        
        return index;
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
            
            LOGGER.debug("Building pairs index...");
            
            int tierCount = ((ICompactTripletsStructureHolder) cts.get(0)).getCTS().getPermutation().size() - 2;
            int ctsCount = cts.size();
            
            for(int i = 0; i < ctsCount; i++)
            {
                ITabularFormula s = ((ICompactTripletsStructureHolder) cts.getQuick(i)).getCTS();
                if(s.isEmpty())
                {
                    throw new EmptyStructureException(s);
                }
                
                Object[] tierElements = s.getTiers().elements();
                
                for (int j = 0; j < tierCount; j++)
                {
                    ITier tier = (ITier) tierElements[j];
                    
                    index.addTier(tier.getAName(), tier.getBName(), i, j, cts);
                    index.addTier(tier.getAName(), tier.getCName(), i, j, cts);
                    index.addTier(tier.getBName(), tier.getCName(), i, j, cts);
                }
            }
            
            indexCache.put(cacheKey, index);
            
            index.purge();
        }
        else
        {
            int ctsCount = cts.size();
            
            for(int i = 0; i < ctsCount; i++)
            {
                ITabularFormula s = ((ICompactTripletsStructureHolder) cts.getQuick(i)).getCTS();
                if(s.isEmpty())
                {
                    throw new EmptyStructureException(s);
                }
            }
            
            index = (VarPairsIndex) indexCache.get(cacheKey);
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
