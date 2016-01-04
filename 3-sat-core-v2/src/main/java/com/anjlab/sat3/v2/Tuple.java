package com.anjlab.sat3.v2;

//  TODO Find a better name
public class Tuple implements Cloneable
{
    public Tuple(ICoupleValue fromLeftTier, ICoupleValue fromRightTier, int labelIndex)
    {
        this.fromLeftTier = fromLeftTier;
        this.fromRightTier = fromRightTier;
        this.labelIndex = labelIndex;
    }
    
    public ICoupleValue fromLeftTier;
    public ICoupleValue fromRightTier;
    public final int labelIndex;
    
    @Override
    public String toString()
    {
        return new StringBuilder()
                .append(fromLeftTier)
                .append("/")
                .append(fromRightTier)
                .append("=")
                .append(labelIndex)
                .toString();
    }
    
    @Override
    public Tuple clone()
    {
        return new Tuple(fromLeftTier, fromRightTier, labelIndex);
    }
}
