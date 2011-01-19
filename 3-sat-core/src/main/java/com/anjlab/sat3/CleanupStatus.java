/*
 * Copyright (c) 2010, 2011 AnjLab
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

public class CleanupStatus
{
    public static final CleanupStatus NOTHING_REMOVED = new CleanupStatus(false, 0, 0, 0);
    public static final CleanupStatus ALL_REMOVED = new CleanupStatus(true, 0, 0, 0);

    public final boolean someClausesRemoved;
    public final int from;
    public final int to;
    public final int numberOfClausesRemoved;
    
    public CleanupStatus(boolean someClausesRemoved, int from, int to, int numberOfClausesRemoved)
    {
        this.someClausesRemoved = someClausesRemoved;
        this.from = from;
        this.to = to;
        this.numberOfClausesRemoved = numberOfClausesRemoved;
    }
}