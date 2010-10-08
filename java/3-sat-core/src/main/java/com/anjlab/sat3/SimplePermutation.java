package com.anjlab.sat3;

import java.util.Arrays;

import cern.colt.list.IntArrayList;
import cern.colt.map.OpenIntIntHashMap;

public class SimplePermutation implements IPermutation
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
    
    public SimplePermutation(IPermutation permutationHead, int size)
    {
        permutation = new IntArrayList(size);
        permutation.addAllOf(((SimplePermutation) permutationHead).permutation);
        
        permutationHash = new OpenIntIntHashMap(size);
        positionHash = new OpenIntIntHashMap(size);
        
        for (int i = 0; i < permutation.size(); i++)
        {
            permutationHash.put(permutation.get(i), i);
            positionHash.put(i, permutation.get(i));
        }
    }

    public boolean contains(int varName)
    {
        //    O(1)
        return permutationHash.containsKey(varName);
    }

    public int indexOf(int varName)
    {
        //    O(1)
        return permutationHash.containsKey(varName)
             ? permutationHash.get(varName)
             : -1;
    }

    public void add(int varName)
    {
        assertNotContains(varName);
        
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
    public void add(int index, int varName)
    {
        assertNotContains(varName);
        
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

    private void assertNotContains(int varName)
    {
        if (contains(varName))
        {
            throw new IllegalStateException("Permutation " + this + " already contains variable " + varName);
        }
    }

    public int size()
    {
        return permutation.size();
    }

    public int get(int index)
    {
        return permutation.get(index);
    }

    public String toString()
    {
        return permutation.toString();
    }
    
    public boolean same(IPermutation permutation)
    {
        return Arrays.equals(
                this.permutation.elements(),
                ((SimplePermutation) permutation).permutation.elements());
    }
}
