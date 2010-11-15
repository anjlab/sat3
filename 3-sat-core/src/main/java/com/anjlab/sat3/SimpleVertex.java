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

import cern.colt.map.OpenIntObjectHashMap;

public final class SimpleVertex implements IVertex
{
    private final ITripletPermutation permutation;
    private final ITripletValue tripletValue;
    private final ICompactTripletsStructure structure;
    private final int tierIndex;
    private IHyperStructure hyperStructure;
    private boolean bottom1Empty;
    private boolean bottom2Empty;
    private boolean dirty;
    
    /**
     * 
     * @param tripletPermutation
     * @param tierIndex
     * @param tripletValue
     * @param structure Assigned substructure-vertex
     */
    public SimpleVertex(ITripletPermutation tripletPermutation, 
                        int tierIndex, ITripletValue tripletValue,
                        ICompactTripletsStructure structure)
    {
        this.permutation = tripletPermutation;
        this.tripletValue = tripletValue;
        this.structure = structure;
        this.tierIndex = tierIndex;
    }
    public void markDirty()
    {
        dirty = true;
    }
    public boolean isDirty()
    {
        return dirty;
    }
    public boolean hasEmptyBottomEdge()
    {
        return bottom1Empty || bottom2Empty;
    }
    public void foundEmptyEdge(EdgeKind edgeKind)
    {
        if (edgeKind == EdgeKind.Bottom1)
        {
            bottom1Empty = true;
        }
        else
        {
            bottom2Empty = true;
        }
    }
    void setHyperStructure(IHyperStructure hyperStructure)
    {
        this.hyperStructure = hyperStructure;
    }
    public ICompactTripletsStructure getCTS()
    {
        return structure;
    }
    
    public ITripletPermutation getPermutation()
    {
        return permutation;
    }
    public ITripletValue getTripletValue()
    {
        return tripletValue;
    }
    public int getTierIndex()
    {
        return tierIndex;
    }
    public String toString()
    {
        return (isDirty() ? "dirty " : "")
             + permutation.getAName()
             + ","
             + permutation.getBName()
             + ","
             + permutation.getCName()
             + " -> "
             + tripletValue.toString()
             + "\n"
             + structure.toString();
    }
    public boolean bothEdgesAreEmpty()
    {
        return getBottomVertex1() == null && getBottomVertex2() == null;
    }
    public IVertex getBottomVertex1()
    {
        return (!bottom1Empty) && (tierIndex < hyperStructure.getTiers().size() - 1) 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget1().getTierKey())
             : null; 
    }
    public IVertex getBottomVertex2()
    {
        return (!bottom2Empty) && (tierIndex < hyperStructure.getTiers().size() - 1) 
             ? (IVertex) ((OpenIntObjectHashMap) hyperStructure.getTiers().get(tierIndex + 1)).get(tripletValue.getAdjoinRightTarget2().getTierKey())
             : null; 
    }
}
