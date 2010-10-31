package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

public interface IHyperStructure
{
    ICompactTripletsStructure getBasicCTS();
    ICompactTripletsStructure getOtherCTS();
    
    /**
     * 
     * @return List of {@link OpenIntObjectHashMap}
     */
    ObjectArrayList getTiers();
    
    void addVertex(int tierSize, IVertex vertex);
}
