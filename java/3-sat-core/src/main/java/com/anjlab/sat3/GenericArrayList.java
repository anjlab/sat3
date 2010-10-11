package com.anjlab.sat3;

import java.util.Comparator;

import cern.colt.list.ObjectArrayList;

public final class GenericArrayList<T>
{
    private final ObjectArrayList list;
    
    public GenericArrayList()
    {
        list = new ObjectArrayList();
    }
    public GenericArrayList(int initialCapacity)
    {
        list = new ObjectArrayList(initialCapacity);
    }
    public GenericArrayList(T[] elements)
    {
        list = new ObjectArrayList(elements);
    }
    public final void add(T element)
    {
        list.add(element);
    }
    public final int size()
    {
        return list.size();
    }
    @SuppressWarnings("unchecked")
    public final T get(int index)
    {
        return (T) list.get(index);
    }
    public final void sort(Comparator<ITier> comparator)
    {
        list.quickSortFromTo(0, list.size() - 1, comparator);
    }
    public final void clear()
    {
        list.clear();
    }
    public Object[] elements()
    {
        return list.elements();
    }
}
