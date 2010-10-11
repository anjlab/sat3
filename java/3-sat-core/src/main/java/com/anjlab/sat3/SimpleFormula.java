package com.anjlab.sat3;

import java.util.Comparator;

import cern.colt.map.OpenLongObjectHashMap;

public final class SimpleFormula implements ICompactTripletsStructure
{
    private final GenericArrayList<ITier> tiers;
    private final OpenLongObjectHashMap tiersHash;
    private final IPermutation permutation;

    public SimpleFormula()
    {
        permutation = new SimplePermutation();
        tiers = new GenericArrayList<ITier>();
        tiersHash = new OpenLongObjectHashMap();
    }

    /**
     * Creates copy of <code>formula</code>. 
     * 
     * New formula uses the same instance of <code>permutation</code> and cloned instances of tiers.
     * Be careful about modifying permutation of new formula, because these changes will also affect initial formula.
     * 
     * @param formula
     */
    private SimpleFormula(SimpleFormula formula)
    {
        this.permutation = formula.permutation;
        this.tiers = new GenericArrayList<ITier>(formula.tiers.size());
        this.tiersHash = new OpenLongObjectHashMap(formula.tiers.size());
        for (int i = 0; i < formula.tiers.size(); i++)
        {
            ITier tier = formula.tiers.get(i);
            addTier(tier.clone());
        }
    }

    public SimpleFormula(IPermutation permutation)
    {
        this.permutation = permutation;
        tiers = new GenericArrayList<ITier>();
        tiersHash = new OpenLongObjectHashMap(); 
    }

    public int getClausesCount() {
        int clausesCount = 0;
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = tiers.get(i);
            clausesCount += tier.size();
        }
        return clausesCount;
    }
    
    public int getVarCount() {
        return permutation.size();
    }
    
    public GenericArrayList<ITier> getTiers()
    {
        return tiers;
    }

    public boolean tiersSorted()
    {
        boolean sorted = true;
        for (int i = 0; i < tiers.size() - 1; i++)
        {
            if (permutation.indexOf(tiers.get(i).getAName()) > 
                permutation.indexOf(tiers.get(i + 1).getAName()))
            {
                sorted = false;
                break;
            }
        }
        return sorted;
    }

    private final Comparator<ITier> tierComparator = new Comparator<ITier>()
    {
        public int compare(ITier t1, ITier t2)
        {
            return permutation.indexOf(t1.getAName()) - permutation.indexOf(t2.getAName());
        }
    };
    
    public void sortTiers()
    {
        tiers.sort(tierComparator);
    }

    public void add(ITriplet triplet)
    {
        ITier targetTier = findOrCreateTargetTierFor(triplet);

        triplet.transposeTo(targetTier);
        
        targetTier.add(triplet);
    }

    private void add(ITier tier)
    {
        ITier targetTier = findOrCreateTargetTierFor(tier);
        
        tier.transposeTo(targetTier);

        targetTier.union(tier);
    }

    void addTier(ITier tier)
    {
        tiers.add(tier);
        tiersHash.put(tier.canonicalHashCode(), tier);
    }

    private ITier findOrCreateTargetTierFor(ITripletPermutation triplet) {
        ITier targetTier = findTierFor(triplet);

        if (targetTier == null)
        {
            ITripletPermutation tierPermutation = createTierFor(triplet);
            
            targetTier = new SimpleTier(tierPermutation);
            
            addTier(targetTier);
        }
        
        return targetTier;
    }

    private ITripletPermutation createTierFor(ITripletPermutation variables) {
        ITripletPermutation tierPermutation;
        
        if (!permutation.contains(variables.getAName())) { permutation.add(variables.getAName()); }
        if (!permutation.contains(variables.getBName())) { permutation.add(variables.getBName()); }
        if (!permutation.contains(variables.getCName())) { permutation.add(variables.getCName()); }

        int aIndex = permutation.indexOf(variables.getAName());
        int bIndex = permutation.indexOf(variables.getBName());
        int cIndex = permutation.indexOf(variables.getCName());

        tierPermutation = orderIs(aIndex, bIndex, cIndex) ? new SimpleTripletPermutation(variables.getAName(), variables.getBName(), variables.getCName()) :
                          orderIs(bIndex, aIndex, cIndex) ? new SimpleTripletPermutation(variables.getBName(), variables.getAName(), variables.getCName()) :
                          orderIs(bIndex, cIndex, aIndex) ? new SimpleTripletPermutation(variables.getBName(), variables.getCName(), variables.getAName()) :
                          orderIs(cIndex, bIndex, aIndex) ? new SimpleTripletPermutation(variables.getCName(), variables.getBName(), variables.getAName()) :
                          orderIs(aIndex, cIndex, bIndex) ? new SimpleTripletPermutation(variables.getAName(), variables.getCName(), variables.getBName()) :
                                                            new SimpleTripletPermutation(variables.getCName(), variables.getAName(), variables.getBName());
        return tierPermutation;
    }

    private ITier findTierFor(ITripletPermutation tripletPermutation) {
        long key = tripletPermutation.canonicalHashCode();
        //    O(1)
        ITier targetTier = tiersHash.containsKey(key)
                         ? (ITier) tiersHash.get(key)
                         : null;
        return targetTier;
    }

    private static boolean orderIs(int aIndex, int bIndex, int cIndex)
    {
        return (aIndex < bIndex && bIndex < cIndex);
    }

    public IPermutation getPermutation()
    {
        return permutation;
    }

    public void applyJoin(JoinInfo joinInfo, ITier tier)
    {
        int varIndex;
        if ((varIndex = permutation.indexOf(joinInfo.targetPermutation.getAName())) >= 0)
        {
            if (varIndex + 1 > permutation.size() - 1 || permutation.get(varIndex + 1) != joinInfo.targetPermutation.getBName())
            {
                if (varIndex + 1 > permutation.size() - 1)
                {
                    permutation.add(joinInfo.targetPermutation.getBName());
                }
                else
                {
                    permutation.add(varIndex + 1, joinInfo.targetPermutation.getBName());
                }
            }
            if (varIndex + 2 > permutation.size() - 1 || permutation.get(varIndex + 2) != joinInfo.targetPermutation.getCName())
            {
                if (varIndex + 2 > permutation.size() - 1)
                {
                    permutation.add(joinInfo.targetPermutation.getCName());
                }
                else
                {
                    permutation.add(varIndex + 2, joinInfo.targetPermutation.getCName());
                }
            }
        }
        else if ((varIndex = permutation.indexOf(joinInfo.targetPermutation.getBName())) >= 0)
        {
            int offset = 0;
            if (varIndex - 1 < 0 || permutation.get(varIndex - 1) != joinInfo.targetPermutation.getAName())
            {
                if (varIndex - 1 < 0) offset++;
                permutation.add(varIndex - 1 + offset, joinInfo.targetPermutation.getAName());
            }
            if (varIndex + 1 + offset > permutation.size() - 1 || permutation.get(varIndex + 1 + offset) != joinInfo.targetPermutation.getCName())
            {
                if (varIndex + 1 + offset > permutation.size() - 1)
                {
                    permutation.add(joinInfo.targetPermutation.getCName());
                }
                else
                {
                    permutation.add(varIndex + 1 + offset, joinInfo.targetPermutation.getCName());
                }
            }
        }
        else if ((varIndex = permutation.indexOf(joinInfo.targetPermutation.getCName())) >= 0)
        {
            int offset = 0;
            if (varIndex - 1 < 0 || permutation.get(varIndex - 1) != joinInfo.targetPermutation.getBName())
            {
                if (varIndex - 1 < 0) offset++;

                permutation.add(varIndex - 1 + offset, joinInfo.targetPermutation.getBName());
            }
            if (varIndex - 2 + offset < 0 || permutation.get(varIndex - 2 + offset) != joinInfo.targetPermutation.getAName())
            {
                if (varIndex - 2 + offset < 0) offset++;

                permutation.add(varIndex - 2 + offset, joinInfo.targetPermutation.getAName());
            }
        }
        else
        {
            permutation.add(joinInfo.targetPermutation.getAName());
            permutation.add(joinInfo.targetPermutation.getBName());
            permutation.add(joinInfo.targetPermutation.getCName());
        }
        
        add(tier);
    }

    public void subtract(ITabularFormula formula)
    {
        SimpleFormula f = (SimpleFormula) formula;
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = tiers.get(i);
            long key = tier.canonicalHashCode();
            if (!f.tiersHash.containsKey(key))
            {
                continue;
            }
            ITier t = (ITier) f.tiersHash.get(key);
            tier.subtract(t);
        }

        cleanup();
    }

    public boolean cleanup()
    {
        if (tiers.size() == 1)
        {
            return false;
        }

        if (tiers.size() != getVarCount() - 2)
        {
            clear();
            return true;
        }

        if (Helper.EnableAssertions)
        {
            assertTiersSorted();
        }

        return internalCleanup();
    }

    private void assertTiersSorted()
    {
        if (!tiersSorted())
        {
            throw new IllegalStateException("Tiers should be sorted");
        }
    }

    private boolean internalCleanup()
    {        
        boolean someClausesRemoved = false;

        int tiersSize = tiers.size();
        int tiersSizeMinusOne = tiersSize - 1;
        
        for (int i = 0; i < tiersSize; i++)
        {
            ITier tier = tiers.get(i);
            int size = tier.size();
            if (i < tiersSizeMinusOne) tier.adjoinRight(tiers.get(i + 1));
            if (i > 0) tier.adjoinLeft(tiers.get(i - 1));
            if (tier.isEmpty())
            {
                clear();
                return true;
            }
            if (size != tier.size())
            {
                someClausesRemoved = true;
            }
        }
        
        if (someClausesRemoved)
        {
            internalCleanup();
        }
        
        return someClausesRemoved;
    }

    public ICompactTripletsStructure union(ICompactTripletsStructure cts)
    {
        SimpleFormula other = (SimpleFormula) cts;

        if (Helper.EnableAssertions)
        {
            assertSamePermutation(other);
        }
        
        //  Union will return CTS with the same permutation
        SimpleFormula result = new SimpleFormula(this);

        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = result.tiers.get(i);
            
            ITier otherTier = other.tiers.get(i);
            
            tier.union(otherTier);
        }

        //  No need in running clearing procedure on result
        
        return result;
    }

    private void assertSamePermutation(SimpleFormula operand)
    {
        if (!permutation.same(operand.permutation))
        {
            throw new IllegalArgumentException("Operand permutation should be the same as the formula permutation");
        }
    }

    public ICompactTripletsStructure intersect(ICompactTripletsStructure cts)
    {
        SimpleFormula other = (SimpleFormula) cts;
        
        if (Helper.EnableAssertions)
        {
            assertSamePermutation(other);
        }
        
        //  Intersect will return CTS with the same permutation
        SimpleFormula result = new SimpleFormula(this);

        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = result.tiers.get(i);
            
            ITier otherTier = other.tiers.get(i);
            
            tier.intersect(otherTier);
        }

        result.cleanup();
        
        return result;
    }

    public boolean concretize(int varName, Value value)
    {
        int indexOf = permutation.indexOf(varName);

        if (Helper.EnableAssertions)
        {
            if (value != Value.AllPlain && value != Value.AllNegative)
            {
                throw new IllegalArgumentException(
                        "Value should be one of (" + Value.AllPlain + ", " + Value.AllNegative 
                        + ") but was " + value);
            }
            
            if (indexOf < 0)
            {
                throw new IllegalArgumentException("Can't concretize on varName=" 
                        + varName + " because varName is not from the formula's permutation");
            }
        }
        
        //  Find tiers containing varName (there are maximum 3 of them), 
        //  and concretize only them

        boolean someClausesRemoved = false;
        
        for (int i = 0, tierIndex = indexOf; i < 3 && tierIndex >= 0; i++, tierIndex--)
        {
            if (tierIndex < tiers.size())
            {
                ITier tier = tiers.get(tierIndex);
                if (!someClausesRemoved)
                {
                    int size = tier.size(); 
                    tier.concretize(varName, value);
                    if (size != tier.size())
                    {
                        someClausesRemoved = true;
                    }
                }
                else
                {
                    tier.concretize(varName, value);
                }
            }
        }
        
        if (someClausesRemoved)
        {
            return cleanup();
        }
        
        return false;
    }

    public boolean isEmpty()
    {
        return tiers.size() == 0;
    }

    private void clear()
    {
        tiers.clear();
        tiersHash.clear();
    }
    
    public String toString()
    {
        return Helper.buildPrettyOutput(this).insert(0, '\n').toString();
    }

    public Value valueOf(int varName)
    {
        
        int indexOf = permutation.indexOf(varName);
        
        if (Helper.EnableAssertions)
        {
            if (indexOf < 0)
            {
                throw new IllegalArgumentException("Can't get value of varName=" 
                        + varName + " because varName is not from the formula's permutation");
            }
        }
        
        Value value = Value.Mixed;
        
        for (int i = 0, tierIndex = indexOf; i < 3 && tierIndex >= 0; i++, tierIndex--)
        {
            if (tierIndex < tiers.size())
            {
                if (i == 0)
                {
                    value = tiers.get(tierIndex).valueOfA();
                    if (value == Value.Mixed)
                    {
                        break;
                    }
                }
                else if (i == 1)
                {
                    Value value2 = tiers.get(tierIndex).valueOfB();
                    if (value2 != value)
                    {
                        value = Value.Mixed;
                        break;
                    }
                }
                else
                {
                    Value value3 = tiers.get(tierIndex).valueOfC();
                    if (value3 != value)
                    {
                        value = Value.Mixed;
                        break;
                    }
                }
            }
        }
        
        return value;
    }
}
