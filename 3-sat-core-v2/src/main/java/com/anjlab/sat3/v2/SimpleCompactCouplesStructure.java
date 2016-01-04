package com.anjlab.sat3.v2;

import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._00_instance;
import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._01_instance;
import static com.anjlab.sat3.v2.SimpleCoupleValueFactory._10_instance;

import com.anjlab.sat3.ICompactTripletsStructure;
import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.ITier;
import com.anjlab.sat3.ITripletValue;
import com.anjlab.sat3.SimpleTripletValueFactory;
import com.anjlab.sat3.Value;

import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenLongObjectHashMap;

public class SimpleCompactCouplesStructure implements ICompactCouplesStructure
{
    private final IPermutation permutation;
    private final ObjectArrayList tiers;
    
    private final OpenLongObjectHashMap twins;
    private int twinLabelCounter;
    
    public SimpleCompactCouplesStructure(ICompactTripletsStructure cts)
    {
        if (!cts.tiersSorted())
        {
            throw new IllegalArgumentException("CTS Tiers should be sorted");
        }
        
        this.permutation = cts.getPermutation();
        
        this.tiers = new ObjectArrayList(cts.getTiers().size() + 1);
        
        createCouplesFromTriplets(cts);
        
        this.twins = new OpenLongObjectHashMap();
        
        markAbsentTriplets(cts);
    }

    private SimpleCompactCouplesStructure(SimpleCompactCouplesStructure copyFrom)
    {
        //  Share instance of permutation
        this.permutation = copyFrom.permutation;
        this.twinLabelCounter = copyFrom.twinLabelCounter;
        
        this.tiers = new ObjectArrayList(copyFrom.tiers.size());
        for (int i = 0; i < copyFrom.tiers.size(); i++)
        {
            ITier2 tier = (ITier2) copyFrom.tiers.get(i);
            this.tiers.add(tier.clone());
        }
        
        this.twins = new OpenLongObjectHashMap(copyFrom.twins.size());
        LongArrayList keys = copyFrom.twins.keys();
        for (int i = 0; i < keys.size(); i++)
        {
            long key = keys.getQuick(i);
            ObjectArrayList copyFromTuples =
                    (ObjectArrayList) copyFrom.twins.get(key);
            ObjectArrayList tuples = new ObjectArrayList(copyFromTuples.size());
            for (int j = 0; j < copyFromTuples.size(); j++)
            {
                Tuple tuple = (Tuple) copyFromTuples.get(j);
                tuples.add(tuple.clone());
            }
            this.twins.put(key, tuples);
        }
    }

    private void markAbsentTriplets(ICompactTripletsStructure cts)
    {
        for (int i = 0; i < tiers.size() - 1; i++)
        {
            ITier2 tier = (ITier2) tiers.get(i);
            for (ICoupleValue couple : tier)
            {
                checkTwinCandidate(cts, i, couple, couple.getAdjoinRightTarget1());
                checkTwinCandidate(cts, i, couple, couple.getAdjoinRightTarget2());
            }
        }
    }

    private void checkTwinCandidate(ICompactTripletsStructure cts,
            int tierIndex, ICoupleValue couple, ICoupleValue adjoinCouple)
    {
        ITier2 rightTier = (ITier2) tiers.get(tierIndex + 1);
        if (rightTier.contains(adjoinCouple))
        {
            //  Check if resulting triplet is in initial CTS,
            //  if not -- mark them with a twins label
            
            ITripletValue tripletValue = SimpleTripletValueFactory
                    .getTripletValue(
                            couple.isNotA() ? -1 : 1,
                            couple.isNotB() ? -1 : 1,
                            adjoinCouple.isNotB() ? -1 : 1);
            
            ITier leftTier = cts.getTier(tierIndex);
            if (!leftTier.contains(tripletValue))
            {
                long twinTupleKey = getTwinTupleKey(tierIndex);
                
                ObjectArrayList tuples = (ObjectArrayList) twins.get(twinTupleKey);
                
                if (tuples == null)
                {
                    tuples = new ObjectArrayList();
                    twins.put(twinTupleKey, tuples);
                }
                
                tuples.add(new Tuple(couple, adjoinCouple, twinLabelCounter++));
            }
        }
    }

    public static long getTwinTupleKey(int indexOfFirstTier)
    {
        return ((long) indexOfFirstTier) << 32 | (long) (indexOfFirstTier + 1);
    }

    private void createCouplesFromTriplets(ICompactTripletsStructure cts)
    {
        int tiers3Count = cts.getTiers().size();
        
        for (int i = 0; i < tiers3Count; i++)
        {
            SimpleTier2 firstTier2;
            
            if (i == 0)
            {
                firstTier2 = new SimpleTier2();
                this.tiers.add(firstTier2);
            }
            else
            {
                //  Already created on previous step
                firstTier2 = (SimpleTier2) this.tiers.get(i);
            }
            
            SimpleTier2 secondTier2 = new SimpleTier2();
            this.tiers.add(secondTier2);
            
            ITier tier3 = cts.getTier(i);
            for (ITripletValue triplet : tier3)
            {
                firstTier2.add(SimpleCoupleValueFactory
                        .getCoupleValue(
                                (triplet.isNotA() ? -1 : 1) * tier3.getAName(),
                                (triplet.isNotB() ? -1 : 1) * tier3.getBName()));
                
                secondTier2.add(SimpleCoupleValueFactory
                        .getCoupleValue(
                                (triplet.isNotB() ? -1 : 1) * tier3.getBName(),
                                (triplet.isNotC() ? -1 : 1) * tier3.getCName()));
            }
        }
    }

    @Override
    public IPermutation getPermutation()
    {
        return permutation;
    }
    
    @Override
    public ObjectArrayList getTiers()
    {
        return tiers;
    }

    @Override
    public ITier2 getTier(int index)
    {
        return (ITier2) tiers.get(index);
    }

    @Override
    public boolean isEmpty()
    {
        return tiers.isEmpty();
    }

    @Override
    public int getClausesCount()
    {
        int clausesCount = 0;
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier2 tier = (ITier2) tiers.get(i);
            clausesCount += tier.size();
        }
        return clausesCount;
    }

    @Override
    public ObjectArrayList getTwinTuples(int tierIndex1, int tierIndex2)
    {
        if (tierIndex1 + 1 == tierIndex2)
        {
            return (ObjectArrayList) twins.get(getTwinTupleKey(tierIndex1));
        }
        
        if (tierIndex1 == tierIndex2 + 1)
        {
            return (ObjectArrayList) twins.get(getTwinTupleKey(tierIndex2));
        }
        
        throw new IllegalArgumentException(
                "Unable to get twin labels for non-adjacent tiers: "
                        + tierIndex1 + ", " + tierIndex2);
    }
    
    @Override
    public void inverse(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        if (varIndex < 0)
        {
            throw new IllegalArgumentException("varName=" + varName + " not found in this structure");
        }
        
        //  If index is first or last then we should only inverse single tier,
        //  with the same index as this varName, otherwise should also inverse previous tier
        
        if (varIndex == 0)
        {
            ITier2 tier = (ITier2) tiers.get(0);
            tier.inverseA();
            
            tierInversedA(0);
        }
        else if (varIndex == tiers.size())
        {
            ITier2 tier = (ITier2) tiers.get(varIndex - 1);
            tier.inverseB();
            
            tierInversedB(varIndex - 1);
        }
        else
        {
            ITier2 tier = (ITier2) tiers.get(varIndex);
            tier.inverseA();
            
            tierInversedA(varIndex);
            
            ITier2 prevTier = (ITier2) tiers.get(varIndex - 1);
            prevTier.inverseB();
            
            tierInversedB(varIndex - 1);
        }
    }

    //  TODO tierInversedA & tierInversedB smell, add more tests

    private void tierInversedA(int tierIndex)
    {
        if (tierIndex < tiers.size() - 1)
        {
            ObjectArrayList tuples = getTwinTuples(tierIndex, tierIndex + 1);
            if (tuples != null)
            {
                for (int i = 0; i < tuples.size(); i++)
                {
                    Tuple tuple = (Tuple) tuples.get(i);
                    tuple.fromLeftTier = tuple.fromLeftTier.cloneWithInversedA();
                }
            }
        }
        
        if (tierIndex > 0)
        {
            ObjectArrayList tuples = getTwinTuples(tierIndex - 1, tierIndex);
            if (tuples != null)
            {
                for (int i = 0; i < tuples.size(); i++)
                {
                    Tuple tuple = (Tuple) tuples.get(i);
                    tuple.fromLeftTier = tuple.fromLeftTier.cloneWithInversedB();
                    tuple.fromRightTier = tuple.fromRightTier.cloneWithInversedA();
                }
            }
        }
    }

    private void tierInversedB(int tierIndex)
    {
        if (tierIndex > 0)
        {
            ObjectArrayList tuples = getTwinTuples(tierIndex - 1, tierIndex);
            if (tuples != null)
            {
                for (int i = 0; i < tuples.size(); i++)
                {
                    Tuple tuple = (Tuple) tuples.get(i);
                    tuple.fromRightTier = tuple.fromRightTier.cloneWithInversedB();
                }
            }
        }
    }

    @Override
    public Value valueOf(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        if (varIndex < 0)
        {
            throw new IllegalArgumentException("varName=" + varName + " not found in this structure");
        }
        
        //  If index is first or last then we should only get value of single tier,
        //  with the same index as this varName, otherwise should also check previous tier
        
        if (varIndex == 0)
        {
            ITier2 tier = (ITier2) tiers.get(0);
            return tier.valueOfA();
        }
        else if (varIndex == tiers.size())
        {
            ITier2 tier = (ITier2) tiers.get(tiers.size() - 1);
            return tier.valueOfB();
        }
        else
        {
            ITier2 tier = (ITier2) tiers.get(varIndex);
            Value valueOfA = tier.valueOfA();
            ITier2 prevTier = (ITier2) tiers.get(varIndex - 1);
            Value valueOfB = prevTier.valueOfB();
            
            return valueOfA == valueOfB
                    ? valueOfA
                    : Value.Mixed;
        }
    }
    
    public String toString()
    {
        return Helper2.buildPrettyOutput(this, null).insert(0, '\n').toString();
    }
    
    @Override
    public ICompactCouplesStructure clone()
    {
        return new SimpleCompactCouplesStructure(this);
    }

    @Override
    public boolean trySet(int varName, Value value, IColumnsInversionControl cic)
    {
        if (value != Value.AllPlain && value != Value.AllNegative)
        {
            throw new IllegalArgumentException(value + " not supported");
        }
        
        //  Try to distribute the value from varName down to right,
        //  then from varName up to left.
        
        //  When moving up or down we may stop if we reached the end,
        //  or when unable to fix the value (Figure 1, case a).
        //  In latter case we may remember where we stop and continue from
        //  this place later if corresponding variable was fixed in other CICs
        //  during this step.
        
        int varIndex = getPermutation().indexOf(varName);
        
        if (varIndex == 0)
        {
            int tierIndex = 0;
            return trySetA(varName, value, tierIndex, cic);
        }
        else if (varIndex == getTiers().size())
        {
            int tierIndex = varIndex - 1;
            return trySetB(varName, value, tierIndex, cic);
        }
        else
        {
            int tierIndexOfA = varIndex;
            return trySetA(varName, value, tierIndexOfA, cic)
                && trySetB(varName, value, tierIndexOfA - 1, cic);
        }
    }

    private boolean trySetA(
            int varNameOfA,
            Value value,
            int tierIndex,
            IColumnsInversionControl cic)
                    throws AssertionError
    {
        if (cic.isNotFixed(varNameOfA))
        {
            if (value == Value.AllNegative)
            {
                inverse(varNameOfA);
            }
            
            //  Note:
            //  There are 1 or 2 couples on this tier where varNameOfA has 0 value in A,
            //  and there are 1 or 2 couples on previous tier where varNameOfA has 0 value in B
            
            if (value == Value.AllPlain)
            {
                cic.setPlain(varNameOfA);
            }
            else
            {
                cic.setNegative(varNameOfA);
            }
        }
        
        //  Try to distribute the value to the bottom tiers
        
        //  TODO Check what's happening with the last tier
        for (int i = tierIndex; i < tiers.size(); i++)
        {
            int terminalVarName = distributeZerosToTheRight(cic, i);
            if (cic.isConflicted(terminalVarName))
            {
                return false;
            }
            if (cic.isFree(terminalVarName))
            {
                break;
            }
        }
        
        return true;
    }

    private int distributeZerosToTheLeft(IColumnsInversionControl cic, int tierIndex)
    {
        ITier2 tier = getTier(tierIndex);
        
        int varIndexOfB = tierIndex + 1;
        int varNameOfB = cic.getPermutation().get(varIndexOfB);
        
        int tierKeysOfB = tier.keysOfB(Value.AllPlain);
        
        if (tierKeysOfB == 0)
        {
            //  There's no couples on this tier with varNameOfB equal 0
            //  Same as conflict for selected initial varName
            
            cic.setConflicted(varNameOfB);
            return varNameOfB;
        }
        
        ICoupleValue coupleValue = SimpleCoupleValueFactory.getCoupleValue(tierKeysOfB);
        
        int varIndexOfA = varIndexOfB - 1;
        int varNameOfA = getPermutation().get(varIndexOfA);
        
        if (coupleValue != null)
        {
            //  We have exactly one couple, let's try to fix the value for varNameOfA
            
            //  1) If value for varNameOfA already fixed, then:
            //  1.1) if coupleValue doesn't start with 0 -> conflict
            //  1.2) if it starts with 0, then we have fixed entire couple 00
            //       and this couple must appear in JSS
            
            if (cic.isFixed(varNameOfA))
            {
                if (coupleValue.isNotA())
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
                else
                {
                    //  1.2.1) check that this couple joins with left & right tiers without twin-conflicts
                    //  1.2.1.1) if it doesn't join with left or right tiers (or both) -> conflict
                    //  1.2.1.2) if it does join with both -> continue to the next tier
                    
                    boolean joinsBothTiers =
                            joinsRight(cic, varIndexOfA, tierIndex)
                            && joinsLeft(cic, varIndexOfA, tierIndex);
                    
                    if (joinsBothTiers)
                    {
                        //  OK, continue to the next tier
                        return varNameOfA;
                    }
                    else
                    {
                        cic.setConflicted(varNameOfA);
                        return varNameOfA;
                    }
                }
            }
            else
            //  2) If value of varNameOfA wasn't previously fixed, then
            //  2.1) If coupleValue starts with 1, fix it as 1 and inverse(varNameOfA)
            //  2.2) If coupleValue starts with 0, fix it as 0
            //  2.3) Repeat branch under step 1.2.1
            {
                if (coupleValue.isNotA())
                {
                    cic.setNegative(varNameOfA);
                    inverse(varNameOfA);
                }
                else
                {
                    cic.setPlain(varNameOfA);
                }
                
                boolean joinsBothTiers =
                        joinsRight(cic, varIndexOfA, tierIndex)
                        && joinsLeft(cic, varIndexOfA, tierIndex);
                
                if (joinsBothTiers)
                {
                    //  OK, Continue to the next tier
                    return varNameOfA;
                }
                else
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
            }
        }
        else
        {
            //  More than one value.
            //  There can't be more than two, and we know exactly what the values are.
            
            //  3) If value for varNameOfA already fixed, then:
            //  3.1) Of two existing couples find the one that starts with 0 and proceed with it from 1.2.1
            
            if (cic.isFixed(varNameOfA))
            {
                boolean joinsBothTiers =
                        joinsRight(cic, varIndexOfA, tierIndex)
                        && joinsLeft(cic, varIndexOfA, tierIndex);
                
                if (joinsBothTiers)
                {
                    //  OK, continue to the next tier
                    return varNameOfA;
                }
                else
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
            }
            else
            //  4) If value for varNameOfA wasn't yet fixed, then:
            //  4.1) varNameOfA won't be fixed during this iteration -> mark it as free, break the loop
            {
                cic.setFree(varNameOfA);
                return varNameOfA;
            }
        }
    }

    private int distributeZerosToTheRight(IColumnsInversionControl cic, int tierIndex)
    {
        ITier2 tier = getTier(tierIndex);
        
        int varIndexOfA = tierIndex;
        int varNameOfA = cic.getPermutation().get(varIndexOfA);
        
        int tierKeysOfA = tier.keysOfA(Value.AllPlain);
        
        if (tierKeysOfA == 0)
        {
            //  There's no couples on this tier with varNameOfA equal 0
            //  Same as conflict for selected initial varName
            
            cic.setConflicted(varNameOfA);
            return varNameOfA;
        }
        
        ICoupleValue coupleValue = SimpleCoupleValueFactory.getCoupleValue(tierKeysOfA);
        
        if (coupleValue != null)
        {
            //  We have exactly one couple, let's try to fix the value for varNameOfB
            
            int varNameOfB = getPermutation().get(varIndexOfA + 1);
            
            //  1) If value for varNameOfB already fixed, then:
            //  1.1) if coupleValue doesn't end with 0 -> conflict
            //  1.2) if it ends with 0, then we have fixed entire couple 00
            //       and this couple must appear in JSS
            
            if (cic.isFixed(varNameOfB))
            {
                if (coupleValue.isNotB())
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
                else
                {
                    //  1.2.1) check that this couple joins with left & right tiers without twin-conflicts
                    //  1.2.1.1) if it doesn't join with left or right tiers (or both) -> conflict
                    //  1.2.1.2) if it does join with both -> continue to the next tier
                    
                    boolean joinsBothTiers =
                            joinsRight(cic, varIndexOfA, tierIndex)
                            && joinsLeft(cic, varIndexOfA, tierIndex);
                    
                    if (joinsBothTiers)
                    {
                        //  OK, continue to the next tier
                        return varNameOfB;
                    }
                    else
                    {
                        cic.setConflicted(varNameOfA);
                        return varNameOfA;
                    }
                }
            }
            else
            //  2) If value of varNameOfB wasn't previously fixed, then
            //  2.1) If coupleValue ends with 1, fix it as 1 and inverse(varNameOfB)
            //  2.2) If coupleValue ends with 0, fix it as 0
            //  2.3) Repeat branch under step 1.2.1
            {
                if (coupleValue.isNotB())
                {
                    cic.setNegative(varNameOfB);
                    inverse(varNameOfB);
                }
                else
                {
                    cic.setPlain(varNameOfB);
                }
                
                boolean joinsBothTiers =
                        joinsRight(cic, varIndexOfA, tierIndex)
                        && joinsLeft(cic, varIndexOfA, tierIndex);
                
                if (joinsBothTiers)
                {
                    //  OK, Continue to the next tier
                    return varNameOfB;
                }
                else
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
            }
        }
        else
        {
            //  More than one value.
            //  There can't be more than two, and we know exactly what the values are.
            
            int varNameOfB = getPermutation().get(varIndexOfA + 1);
            
            //  3) If value for varNameOfB already fixed, then:
            //  3.1) Of two existing couples find the one that ends with 0 and proceed with it from 1.2.1
            
            if (cic.isFixed(varNameOfB))
            {
                boolean joinsBothTiers =
                        joinsRight(cic, varIndexOfA, tierIndex)
                        && joinsLeft(cic, varIndexOfA, tierIndex);
                
                if (joinsBothTiers)
                {
                    //  OK, continue to the next tier
                    return varNameOfB;
                }
                else
                {
                    cic.setConflicted(varNameOfA);
                    return varNameOfA;
                }
            }
            else
            //  4) If value for varNameOfB wasn't yet fixed, then:
            //  4.1) varNameOfB won't be fixed during this iteration -> mark it as free, break the loop
            {
                cic.setFree(varNameOfB);
                return varNameOfB;
            }
        }
    }

    private boolean joinsLeft(IColumnsInversionControl cic, int varIndexOfA, int tierIndex)
    {
        ObjectArrayList tuples = getTwinTuples(tierIndex - 1, tierIndex);
        
        if (tuples == null)
        {
            //  OK
            return true;
        }
        
        ITier2 prevTier = getTier(tierIndex - 1);
        
        int varNameOfZ = getPermutation().get(varIndexOfA - 1);
        if (cic.isFixed(varNameOfZ))
        {
            //  There must be the 00 couple on the nextTier to form non empty JSS
            if (!prevTier.contains(_00_instance))
            {
                //  Doesn't join
                return false;
            }
            else
            {
                if (containsTuple(tuples, _00_instance, _00_instance))
                {
                    //  Doesn't join
                    return false;
                }
                else
                {
                    //  OK
                    return true;
                }
            }
        }
        else
        {
            //  We can't guarantee at this point
            //  that there won't be any conflicts in the future
            //  because at least one couple won't be forbidden by twins
            
            //  But in case if the couple that won't be forbidden
            //  by twins won't present in the nextTier we need the following checks:
            //  a) _00 is in prevTier and 00/00 not in the twins, or
            //  b) _10 is in prevTier and 01/00 not in the twins
            
            if (prevTier.contains(_00_instance)
                    && !containsTuple(tuples, _00_instance, _00_instance))
            {
                //  OK
                return true;
            }
            else if (prevTier.contains(_10_instance)
                    && !containsTuple(tuples, _10_instance, _01_instance))
            {
                //  OK
                return true;
            }
            else
            {
                //  Doesn't join
                return false;
            }
        }
    }

    private boolean joinsRight(IColumnsInversionControl cic, int varIndexOfA, int tierIndex)
    {
        ObjectArrayList tuples = getTwinTuples(tierIndex, tierIndex + 1);
        
        if (tuples == null)
        {
            //  OK
            return true;
        }
        
        ITier2 nextTier = getTier(tierIndex + 1);
        
        int varNameOfC = getPermutation().get(varIndexOfA + 2);
        if (cic.isFixed(varNameOfC))
        {
            //  There must be the 00 couple on the nextTier to form non empty JSS
            if (!nextTier.contains(_00_instance))
            {
                //  Doesn't join
                return false;
            }
            else
            {
                if (containsTuple(tuples, _00_instance, _00_instance))
                {
                    //  Doesn't join
                    return false;
                }
                else
                {
                    //  OK
                    return true;
                }
            }
        }
        else
        {
            //  We can't guarantee at this point
            //  that there won't be any conflicts in the future
            //  because at least one couple won't be forbidden by twins
            
            //  But in case if the couple that won't be forbidden
            //  by twins won't present in the nextTier we need the following checks:
            //  a) _00 is in nextTier and 00/00 not in the twins, or
            //  b) _01 is in nextTier and 00/01 not in the twins
            
            if (nextTier.contains(_00_instance)
                    && !containsTuple(tuples, _00_instance, _00_instance))
            {
                //  OK
                return true;
            }
            else if (nextTier.contains(_01_instance)
                    && !containsTuple(tuples, _00_instance, _01_instance))
            {
                //  OK
                return true;
            }
            else
            {
                //  Doesn't join
                return false;
            }
        }
    }

    private boolean containsTuple(
            ObjectArrayList tuples, ICoupleValue leftCouple, ICoupleValue rightCouple)
    {
        //  TODO Rewrite with O(1)
        for (int i = 0; i < tuples.size(); i++)
        {
            Tuple tuple = (Tuple) tuples.get(i);
            if (tuple.fromLeftTier == leftCouple
                    && tuple.fromRightTier == rightCouple)
            {
                return true;
            }
        }
        return false;
    }

    private boolean trySetB(
            int varNameOfB,
            Value value,
            int tierIndex,
            IColumnsInversionControl cic)
                    throws AssertionError
    {
        if (cic.isNotFixed(varNameOfB))
        {
            if (value == Value.AllNegative)
            {
                inverse(varNameOfB);
            }
            
            //  Note:
            //  There are 1 or 2 couples on this tier where varNameOfB has 0 value in B,
            //  and there are 1 or 2 couples on next tier where varNameOfB has 0 value in A
            
            if (value == Value.AllPlain)
            {
                cic.setPlain(varNameOfB);
            }
            else
            {
                cic.setNegative(varNameOfB);
            }
        }
        
        //  Try to distribute the value to the upper tiers
        
        //  TODO Check what's happening with the first tier
        for (int i = tierIndex; i >= 0; i--)
        {
            int terminalVarName = distributeZerosToTheLeft(cic, i);
            if (cic.isConflicted(terminalVarName))
            {
                return false;
            }
            if (cic.isFree(terminalVarName))
            {
                break;
            }
        }
        
        return true;
    }
}
