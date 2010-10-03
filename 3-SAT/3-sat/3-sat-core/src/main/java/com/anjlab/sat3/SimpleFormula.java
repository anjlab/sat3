package com.anjlab.sat3;

import java.util.Comparator;
import java.util.Iterator;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import cern.colt.map.OpenLongObjectHashMap;

public class SimpleFormula implements ICompactTripletsStructure
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

    public SimpleFormula(IPermutation permutation, GenericArrayList<ITier> tiers) {
		this.permutation = permutation;
		this.tiers = new GenericArrayList<ITier>(tiers.size());
		this.tiersHash = new OpenLongObjectHashMap(tiers.size());
		for (int i = 0; i < tiers.size(); i++)
		{
			ITier tier = tiers.get(i);
			addTier(new SimpleTier(tier));
		}
	}
    
	public SimpleFormula(IPermutation permutation) {
		this.permutation = permutation;
		tiers = new GenericArrayList<ITier>();
		tiersHash = new OpenLongObjectHashMap(); 
	}

	void addTier(ITier tier)
	{
		tiers.add(tier);
		tiersHash.put(tier.canonicalHashCode(), tier);
	}
	
	public int getTermCount() {
    	int termCount = 0;
    	for (int i = 0; i < tiers.size(); i++)
    	{
    		ITier tier = tiers.get(i);
			termCount += tier.size();
		}
    	return termCount;
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
        for (int i = 0; i < getTiers().size() - 1; i++)
        {
            if (permutation.indexOf(getTiers().get(i).getAName()) > 
                permutation.indexOf(getTiers().get(i + 1).getAName()))
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

    	if (triplet.getAName() != targetTier.getAName() 
    			|| triplet.getBName() != targetTier.getBName()
    			|| triplet.getCName() != targetTier.getCName())
        {
    		triplet.transposeTo(targetTier);
        }
        
        add(targetTier, triplet);
    }

    private void add(ITier tier)
    {
    	ITier targetTier = findOrCreateTargetTierFor(tier);
    	
    	if (tier.getAName() != targetTier.getAName() 
        		|| tier.getBName() != targetTier.getBName()
        		|| tier.getCName() != targetTier.getCName())
        {
    		tier.transposeTo(targetTier);
        }
    	
    	for (ITripletValue tripletValue : tier)
    	{
    		add(targetTier, tripletValue);
		}
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

	private void add(ITier targetTier, ITripletValue tripletValue) {
		if (targetTier.add(tripletValue))
        {
        	//	Value added
        }
        else
        {
            //  Duplicate value wasn't added (same triplets won't affect formula satisfiability)
        }
	}

	private ITier findTierFor(ITripletPermutation tripletPermutation) {
		long key = tripletPermutation.canonicalHashCode();
		//	O(1)
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
        
        //	Find targetTier and transpose tier to targetTier's permutation  
        
        add(tier);
    }

	public ICompactTripletsStructure subtract(ITabularFormula formula)
    {
        SimpleFormula result = new SimpleFormula(permutation, tiers);

        for (int i = 0; i < tiers.size(); i++)
        {
        	ITier tier = tiers.get(i);
            boolean subtracted = false;
    		GenericArrayList<ITier> otherTiers = formula.getTiers();
			for (int j = 0; j < otherTiers.size(); j++)
    		{
    			ITier other = otherTiers.get(j);
                if (tier.hasSameVariablesAs(other))
                {
                    //  Subtract

                    for (ITripletValue tripletValue : tier)
                    {
                        if (!other.contains(tripletValue))
                        {
                        	//	Since we created resulting formula 
                        	//	based on the same tiers as this formula, 
                        	//	tiers indices should be the same
                            result.add(result.tiers.get(i), tripletValue);
                        }
                    }

                    subtracted = true;

                    break;
                }
            }
            if (!subtracted)
            {
            	//	No tiers with same-name variables were in the formula
            	ITier targetTier = result.tiers.get(i);
                for (ITripletValue tripletValue : tier) {
					result.add(targetTier, tripletValue);
				}
            }
        }

        result.cleanup();

        return result;
    }

    /// <summary>
    /// Runs clearing procedure on this formula.
    /// </summary>
    private void cleanup()
    {
        if (!tiersSorted())
        {
            throw new RuntimeException("Tiers should be sorted");
        }

        if (tiers.size() != getVarCount() - 2)
        {
            clear();
            return;
        }

        boolean removed = false;

        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = tiers.get(i);
            Iterator<ITripletValue> triplets = tier.iterator();
            while (triplets.hasNext())
            {
                ITripletValue triplet = triplets.next();
                if (i > 0)
                {
                    ITier prevTier = tiers.get(i - 1);
                    if (!concatenatesLeft(triplet, prevTier))
                    {
                        tier.remove(triplet);
                        removed = true;
                        if (tier.size() == 0)
                        {
                            clear();
                            return;
                        }
                        continue;
                    }
                }
                if (i < tiers.size() - 1)
                {
                    ITier nextTier = tiers.get(i + 1);
                    if (!concatenatesRight(triplet, nextTier))
                    {
                        tier.remove(triplet);
                        removed = true;
                        if (tier.size() == 0)
                        {
                            clear();
                            return;
                        }
                        continue;
                    }
                }
            }
        }
        if (removed)
        {
            cleanup();
        }
    }

    public ICompactTripletsStructure union(ICompactTripletsStructure cts)
    {
        throw new NotImplementedException();
    }

    public ICompactTripletsStructure intersect(ICompactTripletsStructure cts)
    {
        throw new NotImplementedException();
    }

    public ICompactTripletsStructure concretize(int varName, boolean value)
    {
        //  Concretization should return CTS with the same permutation
        SimpleFormula result = new SimpleFormula(permutation, tiers);

        for (int i = 0; i < tiers.size(); i++)
        {
        	ITier tier = tiers.get(i);
            for (ITripletValue triplet : tier)
            {
                if (!tier.hasVariable(varName)
                    || (tier.getAName() == varName && triplet.isNotA() == value)
                    || (tier.getBName() == varName && triplet.isNotB() == value)
                    || (tier.getCName() == varName && triplet.isNotC() == value))
                {
                    result.add(result.tiers.get(i), triplet);
                }
            }
        }

        result.cleanup();

        return result;
    }

    public boolean isEmpty()
    {
        return getTermCount() == 0;
    }

    private void clear()
    {
        tiers.clear();
    }

    private static boolean concatenatesRight(ITripletValue leftTriplet, ITier tier)
    {
        for (ITripletValue rightTriplet : tier)
        {
            if (concatenates(leftTriplet, rightTriplet))
            {
                return true;
            }
        }
        return false;
    }

    private static boolean concatenates(ITripletValue leftTriplet, ITripletValue rightTriplet)
    {
        return leftTriplet.isNotB() == rightTriplet.isNotA() && leftTriplet.isNotC() == rightTriplet.isNotB();
    }

    private static boolean concatenatesLeft(ITripletValue rightTriplet, ITier tier)
    {
        for (ITripletValue leftTriplet : tier)
        {
            if (concatenates(leftTriplet, rightTriplet))
            {
                return true;
            }
        }
        return false;
    }
    
    public String toString()
    {
    	return permutation.toString() 
    	     + " : varCount=" + getVarCount() 
    	     + ", termCount=" + getTermCount() 
    	     + ", tierCount=" + tiers.size();
    }
}
