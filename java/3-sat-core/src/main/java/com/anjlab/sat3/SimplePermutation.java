package com.anjlab.sat3;

import java.util.Arrays;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

public final class SimplePermutation implements IPermutation
{
    private final IntArrayList permutation;
    private final OpenIntIntHashMap permutationHash;
    private final OpenIntIntHashMap positionHash;
    
    public SimplePermutation()
    {
        permutation = new IntArrayList();
        permutationHash = new OpenIntIntHashMap();
        positionHash = new OpenIntIntHashMap();
    }

    public final boolean contains(int varName)
    {
        //    O(1)
        return permutationHash.containsKey(varName);
    }

    public final int indexOf(int varName)
    {
        //  TODO Replacing zero-based indexing with the 1-based
        //  allow remove invocation of containsKey method here
        
        //    O(1)
        return permutationHash.containsKey(varName)
             ? permutationHash.get(varName)
             : -1;
    }

    public final void add(int varName)
    {
        if (Helper.EnableAssertions)
        {
            assertNotContains(varName);
        }
        
        permutation.add(varName);
        
        int index = permutation.size() - 1;
        
        permutationHash.put(varName, index);
        positionHash.put(index, varName);
    }

    /**
     * Insert <code>varName</code> before position <code>index</code>.
     * 
     * Note: This implementation can be slow. To avoid usage of this method remove 
     * the <code>JoinBetweenTiers2</code> implementation of {@link IJoinMethod} from {@link JoinMethods#getMethods()}.
     */
    public final void add(int index, int varName)
    {
        if (Helper.EnableAssertions)
        {
            assertNotContains(varName);
        }
        
        permutation.beforeInsert(index, varName);
        permutationHash.put(varName, index);
        
        for (int i = permutation.size() - 2; i >= index; i--)
        {
            int var = positionHash.get(i);
            int idx = permutationHash.get(var);
            permutationHash.put(var, idx + 1);
            positionHash.put(i + 1, var);
        }
        
        positionHash.put(index, varName);
    }

    private final void assertNotContains(int varName)
    {
        if (contains(varName))
        {
            throw new IllegalStateException("Permutation " + this + " already contains variable " + varName);
        }
    }

    public final int size()
    {
        return permutation.size();
    }

    public final int[] elements()
    {
        return permutation.elements();
    }
    
    public final int get(int index)
    {
        return permutation.get(index);
    }

    public final String toString()
    {
        return permutation.toString();
    }
    
    public final boolean same(IPermutation permutation)
    {
        return Arrays.equals(
                this.permutation.elements(),
                ((SimplePermutation) permutation).permutation.elements());
    }
    
    public void ensureCapacity(int varCount)
    {
        permutation.ensureCapacity(varCount);
    }

    public static IPermutation create(int[] variables)
    {
        IPermutation permutation = new SimplePermutation();
        
        for (int varName : variables)
        {
            permutation.add(varName);
        }
        
        return permutation;
    }
}
