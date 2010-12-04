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

import java.util.Properties;

import cern.colt.list.ObjectArrayList;

public interface ITabularFormula
{
    int getVarCount();
    /**
     * Use this method only if performance is not a goal.
     */
    int getClausesCount();
    /**
     * @return List of {@link ITier}
     */
    ObjectArrayList getTiers();
    ITier getTier(int tierIndex);
    ITier findTierFor(ITripletPermutation tripletPermutation);
    void add(ITriplet triplet);
    void unionOrAdd(ITier tier);
    IPermutation getPermutation();
    boolean isEmpty();
    void complete(IPermutation variables) throws EmptyStructureException;
    /**
     * @return List of {@link ITier}
     */
    ObjectArrayList findTiersFor(int varName);
    /**
     * @return List of {@link ITier}
     */
    ObjectArrayList findTiersFor(int varName1, int varName2);
    void sortTiers();
    ITabularFormula clone();
    /**
     * Return this instance to the {@link TabularFormulaFactory}
     */
    void releaseClone();
    
    /**
     * 
     * @param route List of {@link IVertex} forming HSS route. 
     * Contains inverse triplet values of variables. 
     * @return
     */
    boolean evaluate(ObjectArrayList route);
    boolean evaluate(Properties properties);
    boolean containsAllValuesOf(ITier tier);
}
