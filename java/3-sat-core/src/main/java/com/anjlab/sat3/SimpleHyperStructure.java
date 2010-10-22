package com.anjlab.sat3;

import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

public class SimpleHyperStructure implements IHyperStructure
{
    private ICompactTripletsStructure basicCTS;
    private ICompactTripletsStructure otherCTS;
    //  List of OpenIntObjectHashMap where Object is IVertex
    private ObjectArrayList tiers; 
    
    public SimpleHyperStructure(ICompactTripletsStructure basicCTS, ICompactTripletsStructure otherCTS)
    {
        this.basicCTS = basicCTS;
        this.otherCTS = otherCTS;
        
        this.tiers = new ObjectArrayList();
    }
    public ICompactTripletsStructure getBasicCTS()
    {
        return basicCTS;
    }
    public ICompactTripletsStructure getOtherCTS()
    {
        return otherCTS;
    }
    public ObjectArrayList getTiers()
    {
        return tiers;
    }
    public void addFirstTierVertex(int tierSize, IVertex vertex)
    {
        int tierIndex = vertex.getTierIndex();
        OpenIntObjectHashMap edges;
        if (tierIndex == tiers.size())
        {
            edges = new OpenIntObjectHashMap(tierSize);
            tiers.add(edges);
        }
        else
        {
            edges = (OpenIntObjectHashMap) tiers.get(tierIndex);
        }
        edges.put(vertex.getTripletValue().getTierKey(), vertex);
        
        ((SimpleVertex)vertex).setHyperStructure(this);
    }
    public void addNextVertex(IVertex prevVertex, int tierSize, IVertex vertex)
    {
        addFirstTierVertex(tierSize, vertex);
    }
}
