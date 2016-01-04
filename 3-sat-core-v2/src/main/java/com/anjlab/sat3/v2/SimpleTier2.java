package com.anjlab.sat3.v2;

import java.util.Iterator;

import com.anjlab.sat3.Value;

public class SimpleTier2 implements ITier2
{
    //  XXX Key's places for C are not used in this implementation
    //  see also SimpleCoupleValueFactory#getCoupleValue(a, b)
    protected byte keys_73516240;
    protected int size;

    public SimpleTier2()
    {
    }
    
    private SimpleTier2(SimpleTier2 copyFrom)
    {
        this.keys_73516240 = copyFrom.keys_73516240;
        this.size = copyFrom.size;
    }

    /**
     * Use only if performance is not a goal.
     */
    public Iterator<ICoupleValue> iterator()
    {
        return new Iterator<ICoupleValue>()
        {
            private byte key = 0;
            private byte counter = 0;
            public final boolean hasNext()
            {
                return counter < size;
            }
            public final ICoupleValue next()
            {
                key = (byte) (key == 0 ? 1 : key << 1);
                boolean hasValue = (keys_73516240 & key) == key;
                while (!hasValue)
                {
                    key <<= 1;
                    hasValue = (keys_73516240 & key) == key;
                }
                
                counter++;
                return SimpleCoupleValueFactory.getCoupleValue(key);
            }
            public final void remove()
            {
                removeKey(key);
            }
        };
    }
    
    @Override
    public void remove(ICoupleValue couple)
    {
        if (contains(couple))    //    We need this check to keep value of size correct
        {
            removeKey(couple.getTierKey());
        }
    }

    private void removeKey(int key) {
        keys_73516240 = (byte)(keys_73516240 & (255 ^ key));
        size--;
    }

    @Override
    public void add(ICoupleValue couple)
    {
        if (!contains(couple)) //    We need this check to keep value of size correct 
        {
            keys_73516240 = (byte)(keys_73516240 | couple.getTierKey());
            size++;
        }
    }

    @Override
    public boolean contains(ICoupleValue couple)
    {
        int key = couple.getTierKey();
        return (keys_73516240 & key) == key;
    }
    
    @Override
    public int size()
    {
        return size;
    }
    
    @Override
    public String toString()
    {
        if (size == 0)
        {
            return "<empty>";
        }
        StringBuilder builder = new StringBuilder();
        for (ICoupleValue couple : this)
        {
            builder.append(couple.toString()).append("\n");
        }
        return builder.toString();
    }

    @Override
    public void inverseA()
    {
        if (size == 4 || size == 0)
        {
            return;
        }
        
        //  XXX Optimize this
        
        ICoupleValue[] inversedValues = new ICoupleValue[4];
        
        if (contains(SimpleCoupleValueFactory._00_instance))
        {
            inversedValues[0] = SimpleCoupleValueFactory._10_instance;
        }
        if (contains(SimpleCoupleValueFactory._10_instance))
        {
            inversedValues[1] = SimpleCoupleValueFactory._00_instance;
        }
        if (contains(SimpleCoupleValueFactory._01_instance))
        {
            inversedValues[2] = SimpleCoupleValueFactory._11_instance;
        }
        if (contains(SimpleCoupleValueFactory._11_instance))
        {
            inversedValues[3] = SimpleCoupleValueFactory._01_instance;
        }
        
        keys_73516240 = 0;
        for (int i = 0; i < inversedValues.length; i++)
        {
            if (inversedValues[i] != null)
            {
                keys_73516240 = (byte)(keys_73516240 | inversedValues[i].getTierKey());
            }
        }
    }

    @Override
    public void inverseB()
    {
        if (size == 4 || size == 0)
        {
            return;
        }
        
        //  XXX Optimize this
        
        ICoupleValue[] inversedValues = new ICoupleValue[4];
        
        if (contains(SimpleCoupleValueFactory._00_instance))
        {
            inversedValues[0] = SimpleCoupleValueFactory._01_instance;
        }
        if (contains(SimpleCoupleValueFactory._10_instance))
        {
            inversedValues[1] = SimpleCoupleValueFactory._11_instance;
        }
        if (contains(SimpleCoupleValueFactory._01_instance))
        {
            inversedValues[2] = SimpleCoupleValueFactory._00_instance;
        }
        if (contains(SimpleCoupleValueFactory._11_instance))
        {
            inversedValues[3] = SimpleCoupleValueFactory._10_instance;
        }
        
        keys_73516240 = 0;
        for (int i = 0; i < inversedValues.length; i++)
        {
            if (inversedValues[i] != null)
            {
                keys_73516240 = (byte)(keys_73516240 | inversedValues[i].getTierKey());
            }
        }
    }

    @Override
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
    
    @Override
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
    
    @Override
    public int keysOfA(Value value)
    {
        switch (value)
        {
        case AllPlain:
            //  0000x1x1 -- x bits not used in couples
            return keys_73516240 & 0x05;
            
        case AllNegative:
            //  x1x10000 -- x bits not used in couples
            return keys_73516240 & 0x50;

        default:
            throw new IllegalArgumentException(value + " not supported");
        }
    }
    
    @Override
    public int keysOfB(Value value)
    {
        switch (value)
        {
        case AllPlain:
            //  00x100x1 -- x bits not used in couples
            return keys_73516240 & 0x11;
            
        case AllNegative:
            //  x100x100 -- x bits not used in couples
            return keys_73516240 & 0x44;

        default:
            throw new IllegalArgumentException(value + " not supported");
        }
    }
    
    @Override
    public ITier2 clone()
    {
        return new SimpleTier2(this);
    }
}
