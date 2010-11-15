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

public interface IPermutation
{
    /**
     * 
     * @param varName
     * @return Zero-based index
     */
    int indexOf(int varName);
    boolean contains(int varName);
    void add(int varName);
    /**
     * 
     * @param index Zero-based index
     * @param varName
     */
    void add(int index, int varName);
    int size();
    /**
     * 
     * @param index Zero-based index
     * @return
     */
    int get(int index);
    boolean sameAs(IPermutation permutation);
    void swap(int varName1, int varName2);
    int[] elements();
    void shiftToStart(int from, int to);
    void shiftToEnd(int from, int to);
}
