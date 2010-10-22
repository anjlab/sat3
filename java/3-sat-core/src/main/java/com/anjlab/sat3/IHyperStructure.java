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
    
    void addFirstTierVertex(int tierSize, IVertex vertex);
    void addNextVertex(IVertex prevVertex, int tierSize, IVertex vertex);
}
