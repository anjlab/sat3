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

import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cern.colt.list.ObjectArrayList;

public class TestSimpleFormula
{

    @Test
    public void testEvaluate()
    {
        ITabularFormula formula = Helper.createFormula(1, 2, 3);
        
        ObjectArrayList route = new ObjectArrayList();
        //  Route contains inverse values
        route.add(new SimpleVertex(new SimpleTripletPermutation(1, 2, 3), 0, _111_instance, null));
        
        assertTrue(formula.evaluate(route));
    }
}
