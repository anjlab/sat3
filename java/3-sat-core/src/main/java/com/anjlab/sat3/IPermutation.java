package com.anjlab.sat3;

public interface IPermutation
{
    /**
     * 
     * @param varName
     * @return Zero-based index
     */
    int indexOf(int varName);
    boolean contains(int varName);
    void add(int varName);
    /**
     * 
     * @param index Zero-based index
     * @param varName
     */
    void add(int index, int varName);
    int size();
    /**
     * 
     * @param index Zero-based index
     * @return
     */
    int get(int index);
    boolean same(IPermutation permutation);
}
