/*
 * Copyright (c) 2010 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
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
    public void addVertex(int tierSize, IVertex vertex)
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
}