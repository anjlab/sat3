package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;

public interface IHyperStructure
{
    ICompactTripletsStructure getBasicCTS();
    ICompactTripletsStructure getOtherCTS();
    
    /**
     * 
     * @return List of OpenIntObjectHashMap
     */
    ObjectArrayList getTiers();
    
    void addVertex(int tierSize, IVertex vertex);
    void build();
    boolean isBasicGraphChangedDuringBuild();
}
