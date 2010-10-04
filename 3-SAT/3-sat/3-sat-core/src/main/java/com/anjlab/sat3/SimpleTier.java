package com.anjlab.sat3;

import java.util.Iterator;

public class SimpleTier extends SimpleTripletPermutation implements ITier
{
	private int keys;
	private int size;
	private SimpleTier swapBuffer;
	
	public static SimpleTier createCompleteTier(int a, int b, int c)
	{
		SimpleTier result = new SimpleTier(a, b, c);
		result.keys = 255;
		result.size = 8;
		return result;
	}
	
	public SimpleTier(int a, int b, int c)
	{
		super(a, b, c);
		swapBuffer = new SimpleTier();
	}

	private SimpleTier()
	{
		//	Variable names not used in swapBuffer 
		super(1, 2, 3);
	}
	
	public SimpleTier(ITripletPermutation tripletPermutation)
	{
		this(tripletPermutation.getAName(), 
			 tripletPermutation.getBName(),
			 tripletPermutation.getCName());
	}

	public boolean add(ITripletValue triplet)
	{
		if (!contains(triplet)) 
		{
			size++;
			keys = keys | triplet.getTierKey();
			return true;
		}
		else
		{
			return false;
		}
	}

	public int size()
	{
		return size;
	}

	public boolean contains(ITripletValue triplet)
	{
		int key = triplet.getTierKey();
		return (keys & key) == key;
	}

	public void remove(ITripletValue triplet)
	{
		if (contains(triplet))
		{
			removeKey(triplet.getTierKey());
		}
	}

	private void removeKey(int key) {
		keys = keys & (255 ^ key);
		size--;
	}

	public Iterator<ITripletValue> iterator()
	{
		return new Iterator<ITripletValue>()
		{
			private int key = 0;
			private byte counter = 0;
			private byte shiftCount = 0;
			public boolean hasNext()
			{
				return counter < size;
			}
			public ITripletValue next()
			{
				key = key == 0 ? 1 : key << 1;
				shiftCount++;
				boolean hasValue = (keys & key) == key;
				while (!hasValue && shiftCount < 8)
				{
					key = key << 1;
					shiftCount++;
					hasValue = (keys & key) == key;
				}
				
				counter++;
				return SimpleTripletValueFactory.getTripletValue(key);
			}
			public void remove()
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
		
		Iterator<ITripletValue> iterator = iterator();
		
		while (iterator.hasNext())
		{
			swapBuffer.add(iterator.next());
			iterator.remove();
		}
		
		for (ITripletValue tripletValue : swapBuffer)
		{
			add(((IMutableTripletValue)tripletValue).swapAB());
		}
	}
	
	public void swapAC()
	{
		super.swapAC();
		
		if (size == 0)
		{
			return;
		}
		
		Iterator<ITripletValue> iterator = iterator();
		
		while (iterator.hasNext())
		{
			swapBuffer.add(iterator.next());
			iterator.remove();
		}
		
		for (ITripletValue tripletValue : swapBuffer)
		{
			add(((IMutableTripletValue)tripletValue).swapAC());
		}
	}

	public void swapBC()
	{
		super.swapBC();
		
		if (size == 0)
		{
			return;
		}
		
		Iterator<ITripletValue> iterator = iterator();
		
		while (iterator.hasNext())
		{
			swapBuffer.add(iterator.next());
			iterator.remove();
		}
		
		for (ITripletValue tripletValue : swapBuffer)
		{
			add(((IMutableTripletValue)tripletValue).swapBC());
		}
	}
	
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(getAName());
		builder.append(",");
		builder.append(getBName());
		builder.append(",");
		builder.append(getCName());
		return builder.toString();
	}

	public void subtract(ITier tier)
	{
		if (size != 8)
			throw new UnsupportedOperationException("Operation implemented only when subtracting from complete tier");
		
		if (canonicalHashCode() != tier.canonicalHashCode())
			throw new IllegalArgumentException("Cannot subtract tiers with different set of variables: " + this + " and " + tier);
		
		size -= tier.size();
		keys = keys ^ ((SimpleTier) tier).keys;
	}
}
