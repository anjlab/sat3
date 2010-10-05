package com.anjlab.sat3;

import java.util.Comparator;

import cern.colt.list.ObjectArrayList;

public class GenericArrayList<T>
{
    private ObjectArrayList tiers;
    
    public GenericArrayList()
    {
        tiers = new ObjectArrayList();
    }
    public GenericArrayList(int initialCapacity)
    {
        tiers = new ObjectArrayList(initialCapacity);
    }
    public void add(T element)
    {
        tiers.add(element);
    }
    public int size()
    {
        return tiers.size();
    }
    @SuppressWarnings("unchecked")
    public T get(int index)
    {
        return (T) tiers.get(index);
    }
    public void sort(Comparator<ITier> comparator)
    {
        tiers.quickSortFromTo(0, tiers.size() - 1, comparator);
    }
    public void clear()
    {
        tiers.clear();
    }
}
