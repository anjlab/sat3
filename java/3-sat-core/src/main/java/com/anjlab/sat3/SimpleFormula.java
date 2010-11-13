/*
 * Copyright (C) 2010 AnjLab
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

import static com.anjlab.sat3.SimpleTier.createCompleteTier;
import static java.lang.Boolean.parseBoolean;

import java.util.Comparator;
import java.util.Properties;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenLongObjectHashMap;

public final class SimpleFormula implements ICompactTripletsStructure, ICompactTripletsStructureHolder
{
    //  List of ITier
    private final ObjectArrayList tiers;
    //  tiersHash1 and tiersHash2 only needed during CTF creation
    private OpenIntObjectHashMap tiersHash1;
    private OpenLongObjectHashMap tiersHash2;
    private OpenLongObjectHashMap tiersHash3;
    private final IPermutation permutation;

    public SimpleFormula()
    {
        permutation = new SimplePermutation();
        tiers = new ObjectArrayList();
        tiersHash1 = new OpenIntObjectHashMap();
        tiersHash2 = new OpenLongObjectHashMap();
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
    private SimpleFormula(SimpleFormula formula, final boolean fillTiersHash3)
    {
        this.permutation = formula.permutation;
        int tiersCount = formula.tiers.size();
        
        this.tiers = new ObjectArrayList(tiersCount);
        this.tiersHash3 = fillTiersHash3 
                        ? new OpenLongObjectHashMap(tiersCount)
                        : null;
                        
        Object[] tiersElements = formula.tiers.elements();
        for (int i = 0; i < tiersCount; i++)
        {
            ITier clone = ((ITier) tiersElements[i]).clone();
            tiers.add(clone);
            if (fillTiersHash3)
            {
                tiersHash3.put(clone.canonicalHashCode(), clone);
            }
        }
    }

    public SimpleFormula(IPermutation permutation)
    {
        this.permutation = permutation;
        tiers = new ObjectArrayList();
        tiersHash3 = new OpenLongObjectHashMap();
    }

    /**
     * Creates copy of this formula. 
     * 
     * New formula uses the same instance of <code>permutation</code> and cloned instances of tiers.
     * Be careful about modifying permutation of new formula, because these changes will also affect initial formula.
     * TiersHashX will not be initialized.
     */
    public SimpleFormula clone()
    {
        return new SimpleFormula(this, false);
    }
    
    public int getClausesCount() {
        int clausesCount = 0;
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = (ITier) tiers.get(i);
            clausesCount += tier.size();
        }
        return clausesCount;
    }
    
    public int getVarCount() {
        return permutation.size();
    }
    
    public ObjectArrayList getTiers()
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
    
    public void sortTiers()
    {
        tiers.quickSortFromTo(0, tiers.size() - 1, tierComparator);
    }

    public void complete(IPermutation variables) throws EmptyStructureException
    {
        try
        {
            tiersHash1 = null;
            tiersHash2 = null;
            
            int varCount = variables.size();
            int tiersCount = varCount - 2;
            
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
                ((ITier) tiers.get(i)).inverse();
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
    
    public ObjectArrayList findTiersFor(int varName)
    {
        return (ObjectArrayList) tiersHash1.get(varName);
    }
    
    public ObjectArrayList findTiersFor(int varName1, int varName2)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;
        
        return (ObjectArrayList) tiersHash2.get(key);
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
        
        //  List of ITier
        ObjectArrayList tiers = (ObjectArrayList) tiersHash1.get(key);
        
        if (tiers == null)
        {
            tiersHash1.put(key, new ObjectArrayList(new ITier[] {tier}));
        }
        else
        {
            tiers.add(tier);
        }
    }
    
    private void addTiersHash2(ITier tier, int varName1, int varName2)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;
        
        ObjectArrayList tiers = (ObjectArrayList) tiersHash2.get(key);
        
        if (tiers == null)
        {
            tiersHash2.put(key, new ObjectArrayList(new ITier[] {tier}));
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
            
            targetTier = new SimpleTier(tierPermutation.getAName(), 
                                        tierPermutation.getBName(), 
                                        tierPermutation.getCName());
            
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

    /**
     * {@inheritDoc}
     * 
     * <p><b>IMPORTANT!</b> If formula doesn't not meet the above conditions, the
     * result will be incorrect. In this case if {@link Helper#EnableAssertions} 
     * set to <code>true</code> an exception will be thrown.</p>
     */
    public CleanupStatus cleanup(int from, int to)
    {
        if (tiers.size() == 1)
        {
            return new CleanupStatus(false, from, to, 0);
        }

        if (tiers.size() != getVarCount() - 2)
        {
            clear();
            return new CleanupStatus(true, 0, 0, 0);
        }

        if ((from > to) || (from < 0) || (to > tiers.size() - 1))
        {
            throw new IllegalArgumentException("(from > to) || (from < 0) || (to > tiers.size() - 1), from: " + from + ", to: " + to);
        }
        
        if (Helper.EnableAssertions)
        {
            assertTiersSorted();
        }
        
        int numberOfClausesRemoved = 0;
        
        int index;

        index = from - 1;
        while (index >= 0)
        {
            ITier tier = (ITier) tiers.get(index);
            ITier bottom = (ITier) tiers.get(index + 1);
            int size = tier.size();
            tier.adjoinRight(bottom);
            int removed = size - tier.size();
            numberOfClausesRemoved += removed;
            if (removed == 0)
            {
                //  Nothing removed
                break;
            }
            else
            {
                if (tier.isEmpty())
                {
                    clear();
                    return new CleanupStatus(true, 0, 0, 0);
                }
            }
            index--;
        }
        
        int actualFrom = index + 1;
        
        index = to + 1;
        while (index < tiers.size())
        {
            ITier top = (ITier) tiers.get(index - 1);
            ITier tier = (ITier) tiers.get(index);
            int size = tier.size();
            tier.adjoinLeft(top);
            int removed = size - tier.size();
            numberOfClausesRemoved += removed;
            if (removed == 0)
            {
                //  Nothing removed
                break;
            }
            else
            {
                if (tier.isEmpty())
                {
                    clear();
                    return new CleanupStatus(true, 0, 0, 0);
                }
            }
            index++;
        }
        
        int actualTo = index - 1;
        
        if (Helper.EnableAssertions)
        {
            SimpleFormula clone = this.clone();
            clone.cleanup();
            if (this.getClausesCount() != clone.getClausesCount())
            {
                throw new AssertionError("Error in cleanup(from,to) implementation");
            }
        }
        
        return new CleanupStatus(numberOfClausesRemoved > 0, actualFrom, actualTo, numberOfClausesRemoved);
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
        
        Object[] tiersElements = tiers.elements();
        
        for (int i = 0; i < tiersSize; i++)
        {
            ITier tier = (ITier) tiersElements[i];
            int size = tier.size();
            if (i < tiersSizeMinusOne)
            {
                ITier nextTier = (ITier) tiersElements[i + 1];
                tier.adjoinRight(nextTier);
            }
            if (i > 0)
            {
                ITier prevTier = (ITier) tiersElements[i - 1];
                tier.adjoinLeft(prevTier);
            }
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

    public void union(ICompactTripletsStructure cts)
    {
        SimpleFormula other = (SimpleFormula) cts;

        if (Helper.EnableAssertions)
        {
            assertSamePermutation(other);
        }
        
        if (other.isEmpty())
        {
            //  Union with empty structure will not change the structure
            return;
        }
        
        if (isEmpty())
        {
            ObjectArrayList otherTiers = cts.getTiers();
            for (int i = 0; i < otherTiers.size(); i++)
            {
                ITier otherTier = (ITier) otherTiers.get(i);
                tiers.add(otherTier.clone());
            }
        }
        else
        {
            //  Both left and right operands are not empty
            for (int i = 0; i < tiers.size(); i++)
            {
                ITier tier = (ITier) tiers.get(i);
                
                ITier otherTier = (ITier) other.tiers.get(i);
                
                tier.union(otherTier);
            }
        }

        //  No need in running clearing procedure
    }

    private void assertSamePermutation(SimpleFormula operand)
    {
        if (!permutation.sameAs(operand.permutation))
        {
            throw new IllegalArgumentException("Operand permutation should be the same as the formula permutation");
        }
    }

    public void intersect(ICompactTripletsStructure cts)
    {
        SimpleFormula other = (SimpleFormula) cts;
        
        if (Helper.EnableAssertions)
        {
            assertSamePermutation(other);
        }
        
        if (cts.isEmpty() || isEmpty())
        {
            //  Intersection with empty structure will result in empty structure
            clear();
            return;
        }
        
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = (ITier) tiers.get(i);
            
            ITier otherTier = (ITier) other.tiers.get(i);
            
            tier.intersect(otherTier);
        }

        cleanup();
    }

    public boolean concretize(int varName, Value value)
    {
        boolean someClausesRemoved = internalConcretize(varName, value);
        
//        if (someClausesRemoved)
//        {
//            return cleanup();
//        }
        
        return someClausesRemoved;
    }
    
    public boolean concretize(ITripletPermutation tripletPermutation, ITripletValue tripletValue)
    {
        boolean someClausesRemoved = internalConcretize(tripletPermutation.getAName(), tripletValue.isNotA() ? Value.AllNegative : Value.AllPlain)
                                   | internalConcretize(tripletPermutation.getBName(), tripletValue.isNotB() ? Value.AllNegative : Value.AllPlain)
                                   | internalConcretize(tripletPermutation.getCName(), tripletValue.isNotC() ? Value.AllNegative : Value.AllPlain);
        
//        if (someClausesRemoved)
//        {
//            return cleanup();
//        }
        
        return someClausesRemoved;
    }
    
    private boolean internalConcretize(int varName, Value value)
    {
        if (isEmpty())
        {
            return false;
        }
        
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
        
        int from = indexOf;
        int to = indexOf;
        
        for (int i = 0, tierIndex = indexOf; i < 3 && tierIndex >= 0; i++, tierIndex--)
        {
            if (tierIndex < tiers.size())
            {
                from = tierIndex;
                
                ITier tier = (ITier) tiers.get(tierIndex);
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
                if (tier.isEmpty())
                {
                    clear();
                    return true;
                }
            }
        }
        
        if (someClausesRemoved)
        {
            if (to > tiers.size() - 1)
            {
                to = tiers.size() - 1;
            }
            return cleanup(from, to).someClausesRemoved;
        }
        
        return someClausesRemoved;
    }

    public boolean isEmpty()
    {
        return tiers.size() == 0;
    }

    public void clear()
    {
        tiers.clear();
        
        if (tiersHash1 != null) tiersHash1.clear();
        if (tiersHash2 != null) tiersHash2.clear();
        if (tiersHash3 != null) tiersHash3.clear();
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
                    value = ((ITier) tiers.get(tierIndex)).valueOfA();
                    if (value == Value.Mixed)
                    {
                        break;
                    }
                }
                else if (i == 1)
                {
                    Value value2 = ((ITier) tiers.get(tierIndex)).valueOfB();
                    if (value2 != value)
                    {
                        value = Value.Mixed;
                        break;
                    }
                }
                else
                {
                    Value value3 = ((ITier) tiers.get(tierIndex)).valueOfC();
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
    
    public ICompactTripletsStructure getCTS()
    {
        return this;
    }
    
    public ITier getTier(int tierIndex)
    {
        return (ITier) tiers.get(tierIndex);
    }
    
    public boolean evaluate(Properties properties)
    {
        boolean result = true;
        for (int j = 0; j < getTiers().size(); j++)
        {
            for (ITripletValue tiplet : getTier(j))
            {
                ITripletPermutation permutation = getTier(j);
                
                boolean aValue = parseBoolean(String.valueOf(properties.get(String.valueOf(permutation.getAName()))));
                boolean bValue = parseBoolean(String.valueOf(properties.get(String.valueOf(permutation.getBName()))));
                boolean cValue = parseBoolean(String.valueOf(properties.get(String.valueOf(permutation.getCName()))));

                if (tiplet.isNotA()) aValue = !aValue;
                if (tiplet.isNotB()) bValue = !bValue;
                if (tiplet.isNotC()) cValue = !cValue;

                result = result && (aValue || bValue || cValue);
                
                if (!result)
                {
                    return result;
                }
            }
        }
        return result;
    }
    
    public boolean evaluate(ObjectArrayList route)
    {
        boolean result = true;
        for (int j = 0; j < getTiers().size(); j++)
        {
            for (ITripletValue tiplet : getTier(j))
            {
                ITripletPermutation permutation = getTier(j);
                
                boolean aValue = getValueFromRoute(route, permutation.getAName());
                boolean bValue = getValueFromRoute(route, permutation.getBName());
                boolean cValue = getValueFromRoute(route, permutation.getCName());

                if (tiplet.isNotA()) aValue = !aValue;
                if (tiplet.isNotB()) bValue = !bValue;
                if (tiplet.isNotC()) cValue = !cValue;

                result = result && (aValue || bValue || cValue);
                
                if (!result)
                {
                    return result;
                }
            }
        }
        return result;
    }

    private boolean getValueFromRoute(ObjectArrayList route, int varName)
    {
        for (int i = 0; i < route.size(); i++)
        {
            IVertex vertex = (IVertex) route.get(i);
            if (vertex.getPermutation().hasVariable(varName))
            {
                if (varName == vertex.getPermutation().getAName())
                {
                    return vertex.getTripletValue().isNotA();
                }
                if (varName == vertex.getPermutation().getBName())
                {
                    return vertex.getTripletValue().isNotB();
                }
                if (varName == vertex.getPermutation().getCName())
                {
                    return vertex.getTripletValue().isNotC();
                }
            }
        }
        throw new IllegalArgumentException("Variable " + varName + " was not found in route");
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SimpleFormula))
        {
            return false;
        }
        SimpleFormula other = (SimpleFormula) obj;
        if (!this.permutation.sameAs(other.permutation))
        {
            //  Permutations differs
            return false;
        }
        for (int j = 0; j < tiers.size(); j++)
        {
            ITier tier = (ITier) tiers.get(j);
            ITier otherTier = (ITier) other.tiers.get(j);
            if (!tier.equals(otherTier))
            {
                //  Tiers differs
                return false;
            }
        }
        return true;
    }
    
    public boolean containsAllValuesOf(ITier anotherTier)
    {
        for (int i = 0; i < getTiers().size(); i++)
        {
            ITier tier = getTier(i);
            if (tier.hasSameVariablesAs(anotherTier))
            {
                int[] abc = new int[3];
                System.arraycopy(tier.getABC(), 0, abc, 0, 3);
                try
                {
                    tier.transposeTo(anotherTier);
                    if (tier.equals(anotherTier))
                    {
                        return true;
                    }
                } finally
                {
                    tier.transposeTo(abc);
                }
            }
        }
        return false;
    }
    
    public boolean isElementary()
    {
        if (Helper.EnableAssertions)
        {
            assertTiersSorted();
        }
        if (tiers.size() != permutation.size() - 2)
        {
            //  Not all tiers present in CTS
            return false;
        }
        for (int j = 0; j < tiers.size(); j++)
        {
            ITier tier = (ITier) tiers.get(j);
            if (tier.size() != 1)
            {
                return false;
            }
        }
        return true;
    }
}
