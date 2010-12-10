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

import java.util.Arrays;
import java.util.Iterator;

public class SimpleTier extends SimpleTripletPermutation implements ITier
{
    protected byte keys_73516240;
    protected int size;
    private ITabularFormula formula;

    public static SimpleTier createCompleteTier(int a, int b, int c)
    {
        SimpleTier result = new SimpleTier(a, b, c);
        result.keys_73516240 = -1;  //  Complete tier
        result.size = 8;
        return result;
    }
    
    public SimpleTier(int a, int b, int c)
    {
        super(a, b, c);
    }
    
    private SimpleTier(SimpleTier tier)
    {
        super(tier.getABC().clone(), tier.getCanonicalName(), tier.canonicalHashCode());
    }
    
    public ITier clone()
    {
        SimpleTier tier = new SimpleTier(this);
        tier.keys_73516240 = keys_73516240;
        tier.size = size;
        return tier;
    }
    
    public void add(ITripletValue triplet)
    {
        if (!contains(triplet)) //    We need this check to keep value of size correct 
        {
            keys_73516240 = (byte)(keys_73516240 | triplet.getTierKey());
            size++;
        }
    }

    public void intersect(ITripletValue tripletValue)
    {
        keys_73516240 = (byte)(keys_73516240 & tripletValue.getTierKey());
        size = keys_73516240 == 0 ? 0 : 1;
    }

    public int size()
    {
        return size;
    }

    public boolean contains(ITripletValue triplet)
    {
        int key = triplet.getTierKey();
        return (keys_73516240 & key) == key;
    }

    public void remove(ITripletValue triplet)
    {
        if (contains(triplet))    //    We need this check to keep value of size correct
        {
            removeKey(triplet.getTierKey());
        }
    }

    private void removeKey(int key) {
        keys_73516240 = (byte)(keys_73516240 & (255 ^ key));
        size--;
    }

    /**
     * Use only if performance is not a goal.
     */
    public Iterator<ITripletValue> iterator()
    {
        return new Iterator<ITripletValue>()
        {
            private byte key = 0;
            private byte counter = 0;
            public final boolean hasNext()
            {
                return counter < size;
            }
            public final ITripletValue next()
            {
                key = (byte) (key == 0 ? 1 : key << 1);
                boolean hasValue = (keys_73516240 & key) == key;
                while (!hasValue)
                {
                    key <<= 1;
                    hasValue = (keys_73516240 & key) == key;
                }
                
                counter++;
                return SimpleTripletValueFactory.getTripletValue(key);
            }
            public final void remove()
            {
                removeKey(key);
            }
        };
    }

    public void swapAB()
    {
        super.swapAB();
        
        if (size == 0)
        {
            return;
        }
        
        int keys_oo51oooo = keys_73516240 & 0x30;
        int keys_oooo62oo = keys_73516240 & 0x0C;
        
        keys_73516240 = (byte)
                       ((keys_73516240 & 0xC3)
                      | ((keys_oo51oooo >> 2) & 0x3F)
                      | (keys_oooo62oo << 2));
    }
    
    public void swapAC()
    {
        super.swapAC();
        
        if (size == 0)
        {
            return;
        }
        
        int keys_o3o1oooo = keys_73516240 & 0x50;
        int keys_oooo6o4o = keys_73516240 & 0x0A;
        
        keys_73516240 = (byte)
                       ((keys_73516240 & 0xA5)
                      | ((keys_o3o1oooo >> 3) & 0x1F)
                      | (keys_oooo6o4o << 3));
    }

    public void swapBC()
    {
        super.swapBC();
        
        if (size == 0)
        {
            return;
        }
        
        int keys_o3ooo2oo = keys_73516240 & 0x44;
        int keys_oo5ooo4o = keys_73516240 & 0x22;
        
        keys_73516240 = (byte)
                       ((keys_73516240 & 0x99)
                      | ((keys_o3ooo2oo >> 1) & 0x7F)
                      | (keys_oo5ooo4o << 1));
    }
    
    public String toString()
    {
        IPermutation permutation = new SimplePermutation();
        permutation.add(getAName());
        permutation.add(getBName());
        permutation.add(getCName());
        SimpleFormula formula = new SimpleFormula(permutation);
        formula.addTier(this);
        return Helper.buildPrettyOutput(formula).insert(0, '\n').toString();
    }

//    public void adjoinRight(ITier tier)
//    {
//        int key = 1;
//        boolean hasValue = (keys_73516240 & key) == key;
//        if (hasValue)
//        {
//            //  adjoinRight
//            ITripletValue triplet = SimpleTripletValueFactory.getTripletValue(key);
//            if (!tier.contains(triplet.getAdjoinRightTarget1())
//                    && !tier.contains(triplet.getAdjoinRightTarget2()))
//            {
//                removeKey(key);
//            }
//        }
//        while (key < 0x80)
//        {
//            key <<= 1;
//            hasValue = (keys_73516240 & key) == key;
//            if (hasValue)
//            {
//                //  adjoinRight
//                ITripletValue triplet = SimpleTripletValueFactory.getTripletValue(key);
//                if (!tier.contains(triplet.getAdjoinRightTarget1())
//                        && !tier.contains(triplet.getAdjoinRightTarget2()))
//                {
//                    removeKey(key);
//                }
//            }
//        }
//    }
    
    public void adjoinRight(ITier tier)
    {
        int tier_keys_73516240 = ((SimpleTier) tier).keys_73516240;
        
        int this_keys_o6o2o4o0 = (((tier_keys_73516240 >> 1) & 0x7F) | tier_keys_73516240) & 0x55;
        int this_keys_7o3o5o1o = ((this_keys_o6o2o4o0 << 1));
        
        int this_keys_76325410 = (this_keys_o6o2o4o0 | this_keys_7o3o5o1o) & get_keys_76325410();
        
        keys_73516240 = get_keys_73516240_from(this_keys_76325410);
        
        updateSize();
    }

//    public void adjoinLeft(ITier tier)
//    {
//        int key = 1;
//        boolean hasValue = (keys_73516240 & key) == key;
//        if (hasValue)
//        {
//            //  adjoinLeft
//            ITripletValue triplet = SimpleTripletValueFactory.getTripletValue(key);
//            if (!tier.contains(triplet.getAdjoinLeftSource1())
//                    && !tier.contains(triplet.getAdjoinLeftSource2()))
//            {
//                removeKey(key);
//            }
//        }
//        while (key < 0x80)
//        {
//            key <<= 1;
//            hasValue = (keys_73516240 & key) == key;
//            if (hasValue)
//            {
//                //  adjoinLeft
//                ITripletValue triplet = SimpleTripletValueFactory.getTripletValue(key);
//                if (!tier.contains(triplet.getAdjoinLeftSource1())
//                        && !tier.contains(triplet.getAdjoinLeftSource2()))
//                {
//                    removeKey(key);
//                }
//            }
//        }
//    }
    
    public void adjoinLeft(ITier tier)
    {
        int tier_keys_76325410 = ((SimpleTier) tier).get_keys_76325410();
        
//        printBits(((SimpleTier) tier).keys_73516240);
//        printBits(tier_keys_76325410);
        
        int this_keys_o3o1o2o0 = (((tier_keys_76325410 >> 1) & 0x7F) | tier_keys_76325410) & 0x55;
        int this_keys_7o5o6o4o = ((this_keys_o3o1o2o0 << 1));
        
//        printBits(this_keys_o3o1o2o0);
//        printBits(this_keys_7o5o6o4o);
        
//        printBits(keys_73516240);
        
        keys_73516240 = (byte)((this_keys_7o5o6o4o | this_keys_o3o1o2o0) & keys_73516240);
        
//        printBits(keys_73516240);
        
        updateSize();
    }
    
    private void updateSize()
    {
        size = 0;
        
        int mask = 1;
        for (int i = 0; i < 8; i++)
        {
            if ((keys_73516240 & mask) == mask)
            {
                size++;
            }
            mask <<= 1;
        }
    }

    private byte get_keys_73516240_from(int keys_76325410)
    {
        int keys_7o3o5o1o = keys_76325410;
        int keys_6o2o4o0o = keys_76325410 << 1;
        
        int keys_7351oooo = 0;
        int keys_6240oooo = 0;
        
        int mask          = 0x80;
        
        for (int i = 0; i < 4; i++)
        {
            keys_7351oooo = (keys_7351oooo)
                          | (keys_7o3o5o1o & mask);
            
            keys_6240oooo = (keys_6240oooo)
                          | (keys_6o2o4o0o & mask);
                          
            keys_7o3o5o1o <<= 1;
            keys_6o2o4o0o <<= 1;
            mask          >>= 1;
        }
        
        return (byte)(keys_7351oooo | ((keys_6240oooo >> 4) & 0x0F));
    }

    public boolean isEmpty()
    {
        return size == 0;
    }
    
    private int get_keys_76325410()
    {
        int keys_7351oooo = (keys_73516240 & 0xF0);
        int keys_o6420ooo = (keys_73516240 & 0x0F) << 3;
        int mask          = 0x80;

//        printBits(keys_73516240);

        int keys_76325410 = 0;
        
        for (int i = 0; i < 8; i++)
        {
//            printBits(keys_7351oooo);
//            printBits(keys_o6420ooo);

            keys_76325410 = (keys_76325410)
                          | (keys_7351oooo & (mask))
                          | (keys_o6420ooo & (mask >>= 1));

//            printBits(keys_76325410);

            keys_7351oooo = (keys_7351oooo >> 1) & 0x7F;
            keys_o6420ooo = (keys_o6420ooo >> 1) & 0x7F;
            mask          >>= 1;
        }
        
        return keys_76325410;
    }

    public void intersect(ITier tier)
    {
        keys_73516240 = (byte)(keys_73516240 & ((SimpleTier)tier).keys_73516240);
        updateSize();
    }
    
    public void union(ITier tier)
    {
        keys_73516240 = (byte)(keys_73516240 | ((SimpleTier)tier).keys_73516240);
        updateSize();
    }
    
    public void concretize(int varName, Value value)
    {
        if (Helper.EnableAssertions)
        {
            if (value != Value.AllPlain && value != Value.AllNegative)
            {
                throw new IllegalArgumentException(
                        "Value should be one of (" + Value.AllPlain + ", " + Value.AllNegative 
                        + ") but was " + value);
            }
        }
        
        if (getAName() == varName)
        {
            keys_73516240 = (byte)(value == Value.AllPlain ? keys_73516240 & 0x0F : keys_73516240 & 0xF0); 
            updateSize();
        }
        else if (getBName() == varName)
        {
            keys_73516240 = (byte)(value == Value.AllPlain ? keys_73516240 & 0x33 : keys_73516240 & 0xCC); 
            updateSize();
        }
        else if (getCName() == varName)
        {
            keys_73516240 = (byte)(value == Value.AllPlain ? keys_73516240 & 0x55 : keys_73516240 & 0xAA); 
            updateSize();
        }
        else
        {
            throw new IllegalArgumentException("Can't concretize tier on varName="
                    + varName + " because varName is not from the tier's permutation");
        }
    }
    
    public Value valueOfA()
    {
        return size == 0 
             ? Value.Mixed  //  empty tier 
             : (byte)(keys_73516240 & 0x0F) == keys_73516240 
                 ? Value.AllPlain 
                 : (byte)(keys_73516240 & 0xF0) == keys_73516240 
                     ? Value.AllNegative
                     : Value.Mixed;
    }
    
    public Value valueOfB()
    {
        return size == 0 
             ? Value.Mixed  //  empty tier 
             : (byte)(keys_73516240 & 0x33) == keys_73516240 
                 ? Value.AllPlain 
                 : (byte)(keys_73516240 & 0xCC) == keys_73516240 
                     ? Value.AllNegative
                     : Value.Mixed;
    }
    
    public Value valueOfC()
    {
        return size == 0 
             ? Value.Mixed  //  empty tier 
             : (byte)(keys_73516240 & 0x55) == keys_73516240 
                 ? Value.AllPlain 
                 : (byte)(keys_73516240 & 0xAA) == keys_73516240 
                     ? Value.AllNegative
                     : Value.Mixed;
    }

    public ITabularFormula getFormula()
    {
        if (Helper.EnableAssertions)
        {
            if (formula == null)
            {
                throw new IllegalStateException("Formula instance was not set to any value");
            }
        }
        return formula;
    }

    public void setFormula(ITabularFormula formula)
    {
        if (this.formula != null && !this.formula.getPermutation().sameAs(formula.getPermutation()))
        {
            throw new AssertionError("Cannot reuse tier in formula with different permutation");
        }
        this.formula = formula;
    }
    
    public void inverse()
    {
        keys_73516240 = (byte)(~keys_73516240);
        size = 8 - size;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof SimpleTier))
        {
            return false;
        }
        SimpleTier otherTier = (SimpleTier) obj;
        if (!Arrays.equals(getABC(), otherTier.getABC()))
        {
            //  Permutation differs
            return false;
        }
        return keys_73516240 == otherTier.keys_73516240;
    }
}
