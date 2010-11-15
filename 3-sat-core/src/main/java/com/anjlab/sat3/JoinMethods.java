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

import static com.anjlab.sat3.Join2BetweenTiers.canTranspose;
import static com.anjlab.sat3.JoinMethods.endsWith;
import static com.anjlab.sat3.JoinMethods.ensureTheOnlyTierWithVariableExistsAndGetTheTier;
import static com.anjlab.sat3.JoinMethods.ensureTheOnlyTierWithVariablesExistsAndGetTheTier;
import static com.anjlab.sat3.JoinMethods.startsWith;
import cern.colt.list.ObjectArrayList;

public class JoinMethods
{
    private static final IJoinMethod[] joinMethods = 
                        new IJoinMethod[]
                        {
                            //  The order matters 
                            new Join3AsIs(),
                            new Join3BetweenTiers(),
                            new Join2BetweenTiers(),
                            new Join2Right(),
                            new Join2Left(),
                            new Join1Right(),
                            new Join1Left(),
                            new Join0(),
                        };  
    
    public static IJoinMethod[] getMethods()
    {
        return joinMethods;
    }

    public static boolean missingAll(ITabularFormula formula, int varName1, int varName2)
    {
        IPermutation permutation = formula.getPermutation();
        
        return !permutation.contains(varName1)
            && !permutation.contains(varName2);
    }

    public static boolean startsWith(ITabularFormula formula, int varName)
    {
        return formula.getPermutation().get(0) == varName;
    }

    public static boolean startsWith(ITabularFormula formula, int varName1, int varName2)
    {
        IPermutation permutation = formula.getPermutation();

        return permutation.get(0) == varName1
            && permutation.get(1) == varName2;
    }

    public static boolean contains(ITabularFormula formula, int varName)
    {
        return formula.getPermutation().contains(varName);
    }

    public static boolean endsWith(ITabularFormula formula, int varName1, int varName2)
    {
        IPermutation permutation = formula.getPermutation();
        
        int permutationSize = permutation.size();
        
        return permutation.get(permutationSize - 2) == varName1
            && permutation.get(permutationSize - 1) == varName2;
    }

    public static boolean endsWith(ITabularFormula formula, int varName)
    {
        return formula.getPermutation().get(formula.getPermutation().size() - 1) == varName;
    }

    public static ITier ensureTheOnlyTierWithVariableExistsAndGetTheTier(ITabularFormula formula, int varName)
    {
        ObjectArrayList tiers = formula.findTiersFor(varName);
        if (tiers == null)
        {
            return null;
        }
        if (tiers.size() == 1)
        {
            return (ITier) tiers.get(0);
        }
        return null;
    }

    public static ITier ensureTheOnlyTierWithVariablesExistsAndGetTheTier(ITabularFormula formula, int varName1, int varName2)
    {
        ObjectArrayList tiers = formula.findTiersFor(varName1, varName2);
        if (tiers == null)
        {
            return null;
        }
        if (tiers.size() == 1)
        {
            return (ITier) tiers.get(0);
        }
        return null;
    }
    
    public static boolean singleTierExistsForTheVariablesLeft(ITabularFormula formula, int varName1, int varName2)
    {
        ITier tierWithBoth = ensureTheOnlyTierWithVariablesExistsAndGetTheTier(formula, varName1, varName2);
        if (tierWithBoth == null)
        {
            return false;
        }
        ITier tierWithVarName1 = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, varName1);
        if (tierWithVarName1 == null)
        {
            return false;
        }
        ITier tierWithVarName2 = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, varName2); 
        if (tierWithVarName2 == null)
        {
            return false;
        }
        return tierWithVarName1 == tierWithVarName2 && startsWith(tierWithVarName1, varName1, varName2);
    }
    
    static boolean startsWith(ITier tier, int varName1, int varName2)
    {
        return tier.getAName() == varName1 && tier.getBName() == varName2;
    }

    public static boolean startsWith(ITier tier, int varName1)
    {
        return tier.getAName() == varName1;
    }

    public static boolean endsWith(ITier tier, int varName1, int varName2)
    {
        return tier.getBName() == varName1 && tier.getCName() == varName2;
    }

    public static boolean endsWith(ITier tier, int varName1)
    {
        return tier.getCName() == varName1;
    }
}

final class Join3AsIs implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        ITier existingTier = formula.findTierFor(tier);
        
        if (existingTier != null)
        {
            formula.unionOrAdd(existingTier);
            return true;
        }

        return false;
    }
}

final class Join3BetweenTiers implements IJoinMethod
{
    private boolean tryJoin2Left1Right(ITabularFormula formula, ITier tier,
            int left1, int left2, int right)
    {
        int left1Index = formula.getPermutation().indexOf(left1);
        int left2Index = formula.getPermutation().indexOf(left2);
        int rightIndex = formula.getPermutation().indexOf(right);
        
        if (left1Index > rightIndex || left2Index > rightIndex)
        {
            return false;
        }
        ITier leftTier = ensureTheOnlyTierWithVariablesExistsAndGetTheTier(formula, left1, left2);
        if (leftTier == null)
        {
            return false;
        }
        ITier rightTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right);
        if (rightTier == null)
        {
            return false;
        }
        if (leftTier == rightTier)
        {
            return false;
        }
        if (endsWith(leftTier, left1, left2))
        {
            return transposeRightTierAndJoin(formula, tier, right, left2Index, rightIndex, rightTier, left1, left2);
        }
        else if (startsWith(leftTier, left1) && endsWith(leftTier, left2)
                    && canTranspose(formula, leftTier, left1, leftTier.getBName()))
        {
            formula.getPermutation().swap(left1, leftTier.getBName());
            leftTier.swapAB();
            return transposeRightTierAndJoin(formula, tier, right, left2Index, rightIndex, rightTier, left1, left2);
        }
        else if (startsWith(leftTier, left1, left2)
                    && canTranspose(formula, leftTier, left2, leftTier.getCName())
                    && canTranspose(formula, leftTier, left1, left2))
        {
            formula.getPermutation().swap(left1, leftTier.getCName());
            formula.getPermutation().swap(left1, left2);
            leftTier.swapAC();
            leftTier.swapBC();
            return transposeRightTierAndJoin(formula, tier, right, left2Index + 1, rightIndex, rightTier, left1, left2);
        }
        
        return false;
    }

    private static boolean transposeRightTierAndJoin(ITabularFormula formula,
            ITier tier, int right, int leftMostIndex, int rightIndex, ITier rightTier, int left1, int left2)
    {
        IPermutation permutation = formula.getPermutation();
        if (startsWith(rightTier, right))
        {
            if (rightIndex - leftMostIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(leftMostIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftMostIndex + 1, rightIndex - 1);
                    rightIndex = leftMostIndex + 1;
                }
                else
                {
                    permutation.shiftToStart(leftMostIndex + 1, rightIndex - 1);
                }
            }
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        if (rightTier.getBName() == right
                && canTranspose(formula, rightTier, rightTier.getAName(), rightTier.getBName()))
        {
            rightIndex--;
            if (rightIndex - leftMostIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(leftMostIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftMostIndex + 1, rightIndex - 1);
                }
                else
                {
                    permutation.shiftToStart(leftMostIndex + 1, rightIndex - 1);
                }
            }
            permutation.swap(rightTier.getAName(), rightTier.getBName());
            rightTier.swapAB();
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        if (rightTier.getCName() == right
                && canTranspose(formula, rightTier, rightTier.getAName(), rightTier.getCName()))
        {
            rightIndex -= 2;
            if (rightIndex - leftMostIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(leftMostIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftMostIndex + 1, rightIndex - 1);
                }
                else
                {
                    permutation.shiftToStart(leftMostIndex + 1, rightIndex - 1);
                }
            }
            permutation.swap(rightTier.getAName(), rightTier.getCName());
            rightTier.swapAC();
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
    
    private boolean tryJoin1Left2Right(ITabularFormula formula, ITier tier,
            int left, int right1, int right2)
    {
        int leftIndex = formula.getPermutation().indexOf(left);
        int right1Index = formula.getPermutation().indexOf(right1);
        int right2Index = formula.getPermutation().indexOf(right2);

        if (leftIndex > right1Index || leftIndex > right2Index)
        {
            return false;
        }
        ITier leftTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left);
        if (leftTier == null)
        {
            return false;
        }
        ITier rightTier = ensureTheOnlyTierWithVariablesExistsAndGetTheTier(formula, right1, right2);
        if (rightTier == null)
        {
            return false;
        }
        if (leftTier == rightTier)
        {
            return false;
        }
        if (endsWith(leftTier, left))
        {
            return transposeRightTierAndJoin2(formula, tier, right1, right2, rightTier, 
                    Math.min(right1Index, right2Index), leftIndex, left);
        }
        else if (leftTier.getBName() == left
                && canTranspose(formula, leftTier, left, leftTier.getCName()))
        {
            formula.getPermutation().swap(left, leftTier.getCName());
            leftTier.swapBC();
            return transposeRightTierAndJoin2(formula, tier, right1, right2, rightTier, 
                    Math.min(right1Index, right2Index), leftIndex + 1, left);
        }
        else if (startsWith(leftTier, left) 
                && canTranspose(formula, leftTier, left, leftTier.getCName()))
        {
            formula.getPermutation().swap(left, leftTier.getCName());
            leftTier.swapAC();
            return transposeRightTierAndJoin2(formula, tier, right1, right2, rightTier, 
                    Math.min(right1Index, right2Index), leftIndex + 2, left);
        }
        return false;
    }
    
    private static boolean transposeRightTierAndJoin2(ITabularFormula formula, ITier tier, int right1, int right2, 
            ITier rightTier, int rightLeastIndex, int leftIndex, int left)
    {
        IPermutation permutation = formula.getPermutation();
        if (endsWith(rightTier, right1, right2)
                && canTranspose(formula, rightTier, right2, rightTier.getAName())
                && canTranspose(formula, rightTier, right1, right2))
        {
            rightLeastIndex--;
            if (rightLeastIndex - leftIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(rightLeastIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - leftIndex < leftIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightLeastIndex - 1);
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightLeastIndex - 1);
                }
            }
            formula.getPermutation().swap(right2, rightTier.getAName());
            formula.getPermutation().swap(right1, right2);
            rightTier.swapAC();
            rightTier.swapAB();
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(rightTier, right1) && endsWith(rightTier, right2)
                    && canTranspose(formula, rightTier, right2, rightTier.getBName()))
        {
            if (rightLeastIndex - leftIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(rightLeastIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - leftIndex < leftIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightLeastIndex - 1);
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightLeastIndex - 1);
                }
            }
            formula.getPermutation().swap(right2, rightTier.getBName());
            rightTier.swapBC();
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(rightTier, right1, right2))
        {
            if (rightLeastIndex - leftIndex != 1)
            {
                ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, formula.getPermutation().get(rightLeastIndex));
                if (boundaryTier == null)
                {
                    //  Some tiers exist between left-right indices that adjoined with left or right tier
                    return false;
                }
                int length = permutation.size();
                if (length - leftIndex < leftIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightLeastIndex - 1);
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightLeastIndex - 1);
                }
            }
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
    
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        int aIndex = formula.getPermutation().indexOf(a);
        int bIndex = formula.getPermutation().indexOf(b);
        int cIndex = formula.getPermutation().indexOf(c);
        
        if (aIndex < 0 || bIndex < 0 || cIndex < 0)
        {
            return false;
        }
        
        return tryJoin2Left1Right(formula, tier, a, b, c)
            || tryJoin2Left1Right(formula, tier, a, c, b)
            || tryJoin2Left1Right(formula, tier, b, a, c)
            || tryJoin2Left1Right(formula, tier, b, c, a)
            || tryJoin2Left1Right(formula, tier, c, a, b)
            || tryJoin2Left1Right(formula, tier, c, b, a)
            
            || tryJoin1Left2Right(formula, tier, a, b, c)
            || tryJoin1Left2Right(formula, tier, a, c, b)
            || tryJoin1Left2Right(formula, tier, b, a, c)
            || tryJoin1Left2Right(formula, tier, b, c, a)
            || tryJoin1Left2Right(formula, tier, c, a, b)
            || tryJoin1Left2Right(formula, tier, c, b, a);
    }

}

final class Join2BetweenTiers implements IJoinMethod
{
    public static boolean tryJoin2BetweenTiers(ITabularFormula formula, ITier tier, int left, int right, int middle)
    {
        int leftIndex = formula.getPermutation().indexOf(left);
        int middleIndex = formula.getPermutation().indexOf(middle);
        int rightIndex = formula.getPermutation().indexOf(right);
        
        if (leftIndex > rightIndex || leftIndex < 0 || rightIndex < 0 || middleIndex >= 0)
        {
            return false;
        }
        ITier leftTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left);
        if (leftTier == null)
        {
            return false;
        }
        ITier rightTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right);
        if (rightTier == null)
        {
            return false;
        }
        if (leftTier == rightTier)
        {
            return false;
        }
        if (leftTier.getCName() == left)
        {
            return transposeRightTierAndJoin(formula, tier, left, right, middle, leftIndex, rightIndex, leftTier, rightTier);
        }
        else if (leftTier.getBName() == left 
                && canTranspose(formula, leftTier, leftTier.getBName(), leftTier.getCName()))
        {
            formula.getPermutation().swap(leftTier.getBName(), leftTier.getCName());
            leftTier.swapBC();
            return transposeRightTierAndJoin(formula, tier, left, right, middle, leftIndex + 1, rightIndex, leftTier, rightTier);
        }
        else if (leftTier.getAName() == left 
                && canTranspose(formula, leftTier, leftTier.getAName(), leftTier.getCName()))
        {
            formula.getPermutation().swap(leftTier.getAName(), leftTier.getCName());
            leftTier.swapAC();
            return transposeRightTierAndJoin(formula, tier, left, right, middle, leftIndex + 2, rightIndex, leftTier, rightTier);
        }
            
        return false;
    }

    private static boolean transposeRightTierAndJoin(ITabularFormula formula,
            ITier tier, int left, int right, int middle, int leftIndex,
            int rightIndex, ITier leftTier, ITier rightTier)
    {
        IPermutation permutation = formula.getPermutation();
        if (startsWith(rightTier, right))
        {
            if (rightIndex - leftIndex != 1)
            {
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightIndex - 1);
                    rightIndex = leftIndex + 1;
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightIndex - 1);
                }
            }
            permutation.add(rightIndex, middle);
            tier.transposeTo(left, middle, right);
            formula.unionOrAdd(tier);
            return true;
        }
        if (rightTier.getBName() == right
                && canTranspose(formula, rightTier, rightTier.getAName(), rightTier.getBName()))
        {
            rightIndex--;
            if (rightIndex - leftIndex != 1)
            {
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightIndex - 1);
                    rightIndex = leftIndex + 1;
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightIndex - 1);
                }
            }
            permutation.add(rightIndex, middle);
            permutation.swap(rightTier.getAName(), rightTier.getBName());
            rightTier.swapAB();
            tier.transposeTo(left, middle, right);
            formula.unionOrAdd(tier);
            return true;
        }
        if (rightTier.getCName() == right
                && canTranspose(formula, rightTier, rightTier.getAName(), rightTier.getCName()))
        {
            rightIndex -= 2;
            if (rightIndex - leftIndex != 1)
            {
                int length = permutation.size();
                if (length - rightIndex < rightIndex)
                {
                    permutation.shiftToEnd(leftIndex + 1, rightIndex - 1);
                    rightIndex = leftIndex + 1;
                }
                else
                {
                    permutation.shiftToStart(leftIndex + 1, rightIndex - 1);
                }
            }
            permutation.add(rightIndex, middle);
            permutation.swap(rightTier.getAName(), rightTier.getCName());
            rightTier.swapAC();
            tier.transposeTo(left, middle, right);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }

    public static boolean canTranspose(ITabularFormula formula, ITier tier, int varName1, int varName2)
    {
        ITier otherTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, varName1);
        
        if (otherTier != tier)
        {
            return false;
        }
        otherTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, varName2);
        if (otherTier != tier)
        {
            return false;
        }
        return true;
    }

    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        return (tryJoin2BetweenTiers(formula, tier, a, b, c))
            || (tryJoin2BetweenTiers(formula, tier, b, a, c))
            || (tryJoin2BetweenTiers(formula, tier, c, b, a))
            || (tryJoin2BetweenTiers(formula, tier, b, c, a))
            || (tryJoin2BetweenTiers(formula, tier, a, c, b))
            || (tryJoin2BetweenTiers(formula, tier, c, a, b));
    }
}

final class Join2Left implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        return tryJoinLeft(formula, tier, a, b, c)
            || tryJoinLeft(formula, tier, a, c, b)
            || tryJoinLeft(formula, tier, b, a, c)
            || tryJoinLeft(formula, tier, b, c, a)
            || tryJoinLeft(formula, tier, c, a, b)
            || tryJoinLeft(formula, tier, c, b, a);
    }

    private boolean tryJoinLeft(ITabularFormula formula, ITier tier, int right1, int right2, int left)
    {
        int right1Index = formula.getPermutation().indexOf(right1);
        int leftIndex = formula.getPermutation().indexOf(left);
        
        if (leftIndex >= 0)
        {
            return false;
        }
        ITier rightTier = ensureTheOnlyTierWithVariablesExistsAndGetTheTier(formula, right1, right2);
        if (rightTier == null)
        {
            return false;
        }
        IPermutation permutation = formula.getPermutation();
        if (startsWith(rightTier, right1, right2))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right1);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.add(right1Index, left);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(rightTier, right1)
                && endsWith(rightTier, right2)
                && canTranspose(formula, rightTier, right2, rightTier.getBName()))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right1);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.swap(right2, rightTier.getBName());
            rightTier.swapBC();
            permutation.add(right1Index, left);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (endsWith(rightTier, right1, right2)
                    && canTranspose(formula, rightTier, right1, rightTier.getAName())
                    && canTranspose(formula, rightTier, right1, right2))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right1);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.swap(right2, rightTier.getAName());
            permutation.swap(right2, right1);
            rightTier.swapAC();
            rightTier.swapAB();
            right1Index--;
            permutation.add(right1Index, left);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
}

final class Join2Right implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        return tryJoinRight(formula, tier, a, b, c)
            || tryJoinRight(formula, tier, a, c, b)
            || tryJoinRight(formula, tier, b, a, c)
            || tryJoinRight(formula, tier, b, c, a)
            || tryJoinRight(formula, tier, c, a, b)
            || tryJoinRight(formula, tier, c, b, a);
    }

    private boolean tryJoinRight(ITabularFormula formula, ITier tier, int left1, int left2, int right)
    {
        int left2Index = formula.getPermutation().indexOf(left2);
        int rightIndex = formula.getPermutation().indexOf(right);
        
        if (rightIndex >= 0)
        {
            return false;
        }
        ITier leftTier = ensureTheOnlyTierWithVariablesExistsAndGetTheTier(formula, left1, left2);
        if (leftTier == null)
        {
            return false;
        }
        IPermutation permutation = formula.getPermutation();
        if (endsWith(leftTier, left1, left2))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left2);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.add(left2Index + 1, right);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(leftTier, left1)
                && endsWith(leftTier, left2)
                && canTranspose(formula, leftTier, left1, leftTier.getBName()))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left2);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.swap(left1, leftTier.getBName());
            leftTier.swapAB();
            permutation.add(left2Index + 1, right);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(leftTier, left1, left2)
                    && canTranspose(formula, leftTier, left1, leftTier.getCName())
                    && canTranspose(formula, leftTier, left1, left2))
        {
            ITier boundaryTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left2);
            if (boundaryTier == null)
            {
                return false;
            }
            permutation.swap(left1, leftTier.getCName());
            permutation.swap(left2, left1);
            leftTier.swapAC();
            leftTier.swapBC();
            left2Index++;
            permutation.add(left2Index + 1, right);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
}

final class Join1Left implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        return tryJoinLeft(formula, tier, a, b, c)
            || tryJoinLeft(formula, tier, b, a, c)
            || tryJoinLeft(formula, tier, c, a, b);
    }

    private boolean tryJoinLeft(ITabularFormula formula, ITier tier, int right, int left1, int left2)
    {
        int rightIndex = formula.getPermutation().indexOf(right);
        int left1Index = formula.getPermutation().indexOf(left1);
        int left2Index = formula.getPermutation().indexOf(left2);
        
        if (left1Index >= 0 || left2Index >= 0)
        {
            return false;
        }
        ITier rightTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, right);
        if (rightTier == null)
        {
            return false;
        }
        IPermutation permutation = formula.getPermutation();
        if (startsWith(rightTier, right))
        {
            //  TODO Union successive calls to permutation.add(int, int) to a single permutation.add(int, int...) 
            permutation.add(rightIndex, left2);
            permutation.add(rightIndex, left1);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (rightTier.getBName() == right
                && canTranspose(formula, rightTier, right, rightTier.getAName()))
        {
            permutation.swap(right, rightTier.getAName());
            rightTier.swapAB();
            rightIndex--;
            permutation.add(rightIndex, left2);
            permutation.add(rightIndex, left1);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (endsWith(rightTier, right)
                    && canTranspose(formula, rightTier, right, rightTier.getAName()))
        {
            permutation.swap(right, rightTier.getAName());
            rightTier.swapAC();
            rightIndex -= 2;
            permutation.add(rightIndex, left2);
            permutation.add(rightIndex, left1);
            tier.transposeTo(left1, left2, right);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
}

final class Join1Right implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        int a = tier.getAName();
        int b = tier.getBName();
        int c = tier.getCName();

        return tryJoinRight(formula, tier, a, b, c)
            || tryJoinRight(formula, tier, b, a, c)
            || tryJoinRight(formula, tier, c, b, a);
    }

    private boolean tryJoinRight(ITabularFormula formula, ITier tier, int left, int right1, int right2)
    {
        int right1Index = formula.getPermutation().indexOf(right1);
        int right2Index = formula.getPermutation().indexOf(right2);
        int leftIndex = formula.getPermutation().indexOf(left);
        
        if (right1Index >= 0 || right2Index >= 0)
        {
            return false;
        }
        ITier leftTier = ensureTheOnlyTierWithVariableExistsAndGetTheTier(formula, left);
        if (leftTier == null)
        {
            return false;
        }
        IPermutation permutation = formula.getPermutation();
        if (endsWith(leftTier, left))
        {
            permutation.add(leftIndex + 1, right2);
            permutation.add(leftIndex + 1, right1);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (leftTier.getBName() == left
                && canTranspose(formula, leftTier, left, leftTier.getCName()))
        {
            permutation.swap(left, leftTier.getCName());
            leftTier.swapBC();
            leftIndex++;
            permutation.add(leftIndex + 1, right2);
            permutation.add(leftIndex + 1, right1);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        else if (startsWith(leftTier, left)
                    && canTranspose(formula, leftTier, left, leftTier.getCName()))
        {
            permutation.swap(left, leftTier.getCName());
            leftTier.swapAC();
            leftIndex += 2;
            permutation.add(leftIndex + 1, right2);
            permutation.add(leftIndex + 1, right1);
            tier.transposeTo(left, right1, right2);
            formula.unionOrAdd(tier);
            return true;
        }
        return false;
    }
}

final class Join0 implements IJoinMethod
{
    public boolean tryJoin(ITabularFormula formula, ITier tier)
    {
        IPermutation permutation = formula.getPermutation();
        
        if (!permutation.contains(tier.getAName())
            && !permutation.contains(tier.getBName())
            && !permutation.contains(tier.getCName()))
        {
            formula.unionOrAdd(tier);
            return true;
        }

        return false;
    }
}
