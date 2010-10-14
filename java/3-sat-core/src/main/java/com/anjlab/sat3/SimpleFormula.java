package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTier.createCompleteTier;

import java.util.Comparator;

import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenLongObjectHashMap;

public final class SimpleFormula implements ICompactTripletsStructure
{
    private final GenericArrayList<ITier> tiers;
    private final OpenLongObjectHashMap tiersHash3;
    private final IPermutation permutation;

    public SimpleFormula()
    {
        permutation = new SimplePermutation();
        tiers = new GenericArrayList<ITier>();
        tiersHash3 = new OpenLongObjectHashMap();
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
        this.tiersHash3 = new OpenLongObjectHashMap(formula.tiers.size());
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
        tiersHash3 = new OpenLongObjectHashMap(); 
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
        int[] permutationElements = permutation.elements();
        Object[] tiersElements = tiers.elements(); 
        for (int i = 0; i < tiers.size() - 1; i++)
        {
            ITier tier = (ITier) tiersElements[i];
            if (permutation.indexOf(tier.getAName()) > 
                permutation.indexOf(((ITier)tiersElements[i + 1]).getAName()))
            {
                sorted = false;
                break;
            }
            else
            {
                if (!(permutationElements[i] == tier.getAName() 
                        && permutationElements[i + 1] == tier.getBName()
                        && permutationElements[i + 2] == tier.getCName()))
                {
                    sorted = false;
                    break;
                }
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
    
    public final void sortTiers()
    {
        tiers.sort(tierComparator);
    }

    public final void complete(IPermutation variables) throws EmptyStructureException
    {
        try
        {
            tiersHash1.clear();
            tiersHash2.clear();
            tiersHash1 = null;
            tiersHash2 = null;
            
            int varCount = variables.size();
            int tiersCount = varCount - 2;
            if (permutation.size() == varCount && tiers.size() == tiersCount)
            {
                //  Nothing to complete (the formula is already have completed permutation)
                return;
            }
            
            int[] variablesElements = ((SimplePermutation)variables).elements();
            for (int i = 0; i < varCount; i++)
            {
                int varName = variablesElements[i];
                if (!permutation.contains(varName))
                {
                    permutation.add(varName);
                }
            }
            for (int i = 0; i < tiers.size(); i++)
            {
                tiers.get(i).inverse();
            }
            int[] permutationElements = ((SimplePermutation)permutation).elements();
            SimpleTripletPermutation buffer = new SimpleTripletPermutation(1, 2, 3);
            for (int i = 0; i < tiersCount; i++)
            {
                int a = permutationElements[i];
                int b = permutationElements[i + 1];
                int c = permutationElements[i + 2];
                
                buffer.setCanonicalAttributes(a, b, c);
                
                if (!tiersHash3.containsKey(buffer.canonicalHashCode()))
                {
                    addTier(createCompleteTier(a, b, c));
                }
            }
        }
        finally
        {
            sortTiers();
            if (Helper.EnableAssertions)
            {
                assertTiersSorted();
            }
            cleanup();
            if (isEmpty())
            {
                throw new EmptyStructureException(this);
            }
        }
    }

    public void add(ITriplet triplet)
    {
        ITier targetTier = findOrCreateTargetTierFor(triplet);

        triplet.transposeTo(targetTier);
        
        targetTier.add(triplet);
    }

    //  tiersHash1 and tiersHash2 only needed during CTF creation
    private OpenIntObjectHashMap tiersHash1 = new OpenIntObjectHashMap();
    private OpenLongObjectHashMap tiersHash2 = new OpenLongObjectHashMap();
    
    @SuppressWarnings("unchecked")
    public GenericArrayList<ITier> findTiersFor(int varName)
    {
        return (GenericArrayList<ITier>) tiersHash1.get(varName);
    }
    
    @SuppressWarnings("unchecked")
    public GenericArrayList<ITier> findTiersFor(int varName1, int varName2)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;
        
        return (GenericArrayList<ITier>) tiersHash2.get(key);
    }

    public void unionOrAdd(ITier tier)
    {
        ITier targetTier = findTierFor(tier);
        
        if (targetTier == null)
        {
            ensurePermutationContains(tier);
            addTier(tier);
            
            addTiersHash1(tier, tier.getAName());
            addTiersHash1(tier, tier.getBName());
            addTiersHash1(tier, tier.getCName());
            
            addTiersHash2(tier, tier.getAName(), tier.getBName());
            addTiersHash2(tier, tier.getAName(), tier.getCName());
            addTiersHash2(tier, tier.getBName(), tier.getCName());
        }
        else
        {
            tier.transposeTo(targetTier);
            targetTier.union(tier);
        }
    }

    private void addTiersHash1(ITier tier, int varName)
    {
        int key = varName;
        
        @SuppressWarnings("unchecked")
        GenericArrayList<ITier> tiers = (GenericArrayList<ITier>) tiersHash1.get(key);
        
        if (tiers == null)
        {
            tiersHash1.put(key, new GenericArrayList<ITier>(new ITier[] {tier}));
        }
        else
        {
            tiers.add(tier);
        }
    }
    
    private void addTiersHash2(ITier tier, int varName1, int varName2)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;
        
        @SuppressWarnings("unchecked")
        GenericArrayList<ITier> tiers = (GenericArrayList<ITier>) tiersHash2.get(key);
        
        if (tiers == null)
        {
            tiersHash2.put(key, new GenericArrayList<ITier>(new ITier[] {tier}));
        }
        else
        {
            tiers.add(tier);
        }
    }
    
    void addTier(ITier tier)
    {
        tiers.add(tier);
        tiersHash3.put(tier.canonicalHashCode(), tier);
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
        
        ensurePermutationContains(variables);

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

    private void ensurePermutationContains(ITripletPermutation variables)
    {
        if (!permutation.contains(variables.getAName())) { permutation.add(variables.getAName()); }
        if (!permutation.contains(variables.getBName())) { permutation.add(variables.getBName()); }
        if (!permutation.contains(variables.getCName())) { permutation.add(variables.getCName()); }
    }

    public ITier findTierFor(ITripletPermutation tripletPermutation) {
        long key = tripletPermutation.canonicalHashCode();
        //    O(1)
        ITier targetTier = (ITier) tiersHash3.get(key);
        
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
        if (!permutation.sameAs(operand.permutation))
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
        tiersHash3.clear();
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
