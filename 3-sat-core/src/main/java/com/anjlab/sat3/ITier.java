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

public interface ITier extends Iterable<ITripletValue>, ITripletPermutation
{
    void add(ITripletValue triplet);
    int size();
    boolean contains(ITripletValue triplet);
    void remove(ITripletValue triplet);
    void adjoinLeft(ITier tier);
    void adjoinRight(ITier tier);
    boolean isEmpty();
    void intersect(ITier tier);
    void union(ITier tier);
    void concretize(int varName, Value value);
    ITier clone();
    Value valueOfA();
    Value valueOfB();
    Value valueOfC();
    void inverse();
    void intersect(ITripletValue tripletValue);
}
