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

public interface ICompactTripletsStructure extends ITabularFormula
{
    boolean tiersSorted();
    void union(ICompactTripletsStructure cts);
    void intersect(ICompactTripletsStructure cts);
    /**
     * 
     * @param varName
     * @param value
     * @return True if some clauses were removed during concretization.
     */
    boolean concretize(int varName, Value value);
    boolean concretize(ITripletPermutation tripletPermutation, ITripletValue tripletValue);
    /**
     * Runs clearing procedure on this formula.
     * @return True if some clauses were removed during cleanup.
     */
    boolean cleanup();
    /**
     * <p>The formula should be 'clean' in (0 ... from) range and (to ... tiers.size()-1) range, 
     * as well as inside (from ... to) range.</p>
     * 
     * <p>Such formulas usually appear as a result of concretization of 'clean' formula.</p>
     * 
     * @param from
     * @param to
     * @return
     */
    CleanupStatus cleanup(int from, int to);
    Value valueOf(int varName);
    void clear();
    boolean isElementary();
}