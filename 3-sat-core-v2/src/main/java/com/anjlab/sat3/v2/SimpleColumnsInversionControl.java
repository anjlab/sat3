package com.anjlab.sat3.v2;

import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.Value;

public class SimpleColumnsInversionControl implements IColumnsInversionControl
{
    private final IPermutation permutation;
    private final int[] components;
    
    //  Possible state of components
    private static final int NOT_USED        = 0;
    private static final int FIXED_NEGATIVE  = 1;
    private static final int FIXED_PLAIN     = 2;
    private static final int FREE            = 4;
    private static final int CONFLICTED      = 8;
    
    public SimpleColumnsInversionControl(IPermutation permutation)
    {
        this.permutation = permutation;
        this.components = new int[permutation.size()];
    }

    @Override
    public IPermutation getPermutation()
    {
        return permutation;
    }

    @Override
    public boolean setNegative(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        components[varIndex] |= FIXED_NEGATIVE;
        
        if (isComponentFixedPlain(components[varIndex]))
        {
            //  Conflict
            return false;
        }
        
        return true;
    }

    @Override
    public boolean setPlain(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        components[varIndex] |= FIXED_PLAIN;
        
        if (isComponentFixedNegative(components[varIndex]))
        {
            //  Conflict
            return false;
        }
        
        return true;
    }

    @Override
    public void setFree(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        components[varIndex] |= FREE;
    }

    @Override
    public boolean isFree(int varName)
    {
        int component = getComponent(varName);
        
        return (component & FREE) == FREE;
    }
    
    @Override
    public Value getFixedValue(int varName)
    {
        int component = getComponent(varName);
        
        //  Note this may give wrong result if varName is not fixed
        return isComponentFixedPlain(component)
                ? Value.AllPlain
                : Value.AllNegative;
    }
    
    @Override
    public boolean isFixed(int varName)
    {
        int component = getComponent(varName);
        
        return isComponentFixedPlain(component)
            || isComponentFixedNegative(component);
    }
    
    @Override
    public boolean isNotFixed(int varName)
    {
        return !isFixed(varName);
    }

    @Override
    public void setConflicted(int varName)
    {
        int varIndex = permutation.indexOf(varName);
        
        components[varIndex] |= CONFLICTED;
    }
    
    @Override
    public boolean isConflicted(int varName)
    {
        int component = getComponent(varName);
        
        return isComponentConflicted(component);
    }
    
    private boolean isComponentConflicted(int component)
    {
        return ((component & CONFLICTED) == CONFLICTED)
                || (isComponentFixedPlain(component)
                        && isComponentFixedNegative(component));
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < permutation.size(); i++)
        {
            appendLegendChar(builder, permutation.get(i));
        }
        return builder.toString();
    }

    private int getComponent(int varName)
    {
        return components[permutation.indexOf(varName)];
    }

    public void appendLegendChar(StringBuilder builder, int varName)
    {
        int component = getComponent(varName);
        
        if (component == NOT_USED)
        {
            builder.append('_');
        }
        else if (isComponentConflicted(component))
        {
            builder.append('!');
        }
        else if (isComponentFixedPlain(component))
        {
            builder.append('0');
        }
        else if (isComponentFixedNegative(component))
        {
            builder.append('1');
        }
        else
        {
            builder.append('?');
        }
    }

    @Override
    public boolean isFixedNegative(int varName)
    {
        int component = getComponent(varName);
        return isComponentFixedNegative(component);
    }

    private boolean isComponentFixedNegative(int component)
    {
        return (component & FIXED_NEGATIVE) == FIXED_NEGATIVE;
    }

    @Override
    public boolean isFixedPlain(int varName)
    {
        int component = getComponent(varName);
        return isComponentFixedPlain(component);
    }

    private boolean isComponentFixedPlain(int component)
    {
        return (component & FIXED_PLAIN) == FIXED_PLAIN;
    }
    
    @Override
    public IColumnsInversionControl clone()
    {
        SimpleColumnsInversionControl copy =
                new SimpleColumnsInversionControl(permutation);
        System.arraycopy(this.components, 0, copy.components, 0, components.length);
        return copy;
    }
}
