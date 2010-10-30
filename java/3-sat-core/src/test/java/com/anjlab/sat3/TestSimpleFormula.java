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
