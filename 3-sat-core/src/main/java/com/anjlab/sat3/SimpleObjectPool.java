package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

public class SimpleObjectPool
{
    private final OpenIntObjectHashMap map = new OpenIntObjectHashMap();
    
    private int count = 0;
    private int maxCount = 0;
    
    public Object acquire(int key)
    {
        ObjectArrayList objects = (ObjectArrayList) map.get(key);
        if (objects == null || objects.size() == 0)
        {
            return null;
        }
        Object object = objects.getQuick(0);
        objects.remove(0);
        count--;
        return object;
    }
    public void release(int key, Object object)
    {
        ObjectArrayList objects = (ObjectArrayList) map.get(key);
        if (objects == null)
        {
            objects = new ObjectArrayList();
            map.put(key, objects);
        }
        objects.add(object);
        count++;
        if (count > maxCount)
        {
            maxCount = count;
        }
    }
}
