package com.anjlab.sat3;

import java.text.MessageFormat;

public class SimpleTriplet extends SimpleTripletPermutation implements ITriplet
{
	private IMutableTripletValue value;

	public SimpleTriplet(int a, int b, int c)
    {
    	super(Math.abs(a), Math.abs(b), Math.abs(c));
    	
    	value = SimpleTripletValueFactory.getTripletValue(a, b, c);
    }

    public boolean isNotA() { return value.isNotA(); }
    public boolean isNotB() { return value.isNotB(); }
    public boolean isNotC() { return value.isNotC(); }

    public String toString()
    {
        return MessageFormat.format(
        		"{0}={1},{2}={3},{4}={5}", 
        		getAName(), value.isNotA(),
				getBName(), value.isNotB(), 
				getCName(), value.isNotC());
    }

    public boolean equals(Object obj)
    {
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }

        SimpleTriplet other = (SimpleTriplet) obj;
        
        return value == other.value
            && getAName() == other.getAName()
            && getBName() == other.getBName()
            && getCName() == other.getCName();
    }

	public int getTierKey()
	{
		return value.getTierKey();
	}

	public void swapAB()
	{
		super.swapAB();
		value = value.swapAB();
	}
	
	public void swapAC()
	{
		super.swapAC();
		value = value.swapAC();
	}
	
	public void swapBC()
	{
		super.swapBC();
		value = value.swapBC();
	}
}