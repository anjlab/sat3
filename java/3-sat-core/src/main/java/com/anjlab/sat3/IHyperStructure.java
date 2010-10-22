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
    
    IEdge addFirstTierEdge(int tierSize, IVertex source);
    IEdge addNextEdge(IEdge prevEdge, int targetTierSize, IVertex prevEdgeTarget);
}
