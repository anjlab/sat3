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

public interface ITripletValue
{
    boolean isNotA();
    boolean isNotB();
    boolean isNotC();
    
    /**
     * There are exist 8 different triplet values: 000, 001, 010, 011, 100, 101, 110 and 111. 
     * It is possible to associate these values with 8 different numbers (byte should be enough for this).
     * 
     * @return Unique number associated with this triplet value.
     */
    byte getTierKey();
    
    ITripletValue getAdjoinRightTarget1();
    ITripletValue getAdjoinRightTarget2();
    ITripletValue getAdjoinLeftSource1();
    ITripletValue getAdjoinLeftSource2();
}