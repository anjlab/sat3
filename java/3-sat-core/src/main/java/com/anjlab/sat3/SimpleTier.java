package com.anjlab.sat3;

import java.util.Iterator;

public class SimpleTier extends SimpleTripletPermutation implements ITier
{
	private int keys_73516240;
	private int size;
	private ITier swapBuffer;
	
	public static SimpleTier createCompleteTier(int a, int b, int c)
	{
		SimpleTier result = new SimpleTier(a, b, c);
		result.keys_73516240 = 255;
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
			keys_73516240 = keys_73516240 | triplet.getTierKey();
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
		return (keys_73516240 & key) == key;
	}

	public void remove(ITripletValue triplet)
	{
		if (contains(triplet))
		{
			removeKey(triplet.getTierKey());
		}
	}

	private void removeKey(int key) {
		keys_73516240 = keys_73516240 & (255 ^ key);
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
				boolean hasValue = (keys_73516240 & key) == key;
				while (!hasValue && shiftCount < 8)
				{
					key = key << 1;
					shiftCount++;
					hasValue = (keys_73516240 & key) == key;
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
		keys_73516240 = keys_73516240 ^ ((SimpleTier) tier).keys_73516240;
	}

	public void adjoinRight(ITier tier)
	{
		int tier_keys_73516240 = ((SimpleTier) tier).keys_73516240;
		
		int this_keys_o6o2o4o0 = ((tier_keys_73516240 >> 1) | tier_keys_73516240) & 0x55;
		int this_keys_7o3o5o1o = ((this_keys_o6o2o4o0 << 1));
		
		int this_keys_76325410 = (this_keys_o6o2o4o0 | this_keys_7o3o5o1o) & get_keys_76325410();
		
		keys_73516240 = get_keys_73516240_from(this_keys_76325410);
		
		updateSize();
	}

	public void adjoinLeft(ITier tier)
	{
		int tier_keys_76325410 = ((SimpleTier) tier).get_keys_76325410();
		
//		printBits(((SimpleTier) tier).keys_73516240);
//		printBits(tier_keys_76325410);
		
		int this_keys_o3o1o2o0 = ((tier_keys_76325410 >> 1) | tier_keys_76325410) & 0x55;
		int this_keys_7o5o6o4o = ((this_keys_o3o1o2o0 << 1));
		
//		printBits(this_keys_o3o1o2o0);
//		printBits(this_keys_7o5o6o4o);
		
//		printBits(keys_73516240);
		
		keys_73516240 = (this_keys_7o5o6o4o | this_keys_o3o1o2o0) & keys_73516240;
		
//		printBits(keys_73516240);
		
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

	private int get_keys_73516240_from(int keys_76325410)
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
		
		return keys_7351oooo | (keys_6240oooo >> 4);
	}

	private int get_keys_76325410()
	{
		int keys_7351oooo = (keys_73516240 & 0xF0);
		int keys_o6420ooo = (keys_73516240 & 0x0F) << 3;
		int mask          = 0x80;

//		printBits(keys_73516240);

		int keys_76325410 = 0;
		
		for (int i = 0; i < 8; i++)
		{
//			printBits(keys_7351oooo);
//			printBits(keys_o6420ooo);

			keys_76325410 = (keys_76325410)
			              | (keys_7351oooo & (mask))
			              | (keys_o6420ooo & (mask >>= 1));

//			printBits(keys_76325410);

			keys_7351oooo >>= 1;
			keys_o6420ooo >>= 1;
			mask          >>= 1;
		}
		
		return keys_76325410;
	}

//	private void printBits(int keys)
//	{
//		int mask = 0x80;
//		while (mask > 0)
//		{
//			if ((keys & mask) == mask)
//			{
//				System.out.print("1");
//			}
//			else
//			{
//				System.out.print("0");
//			}
//			mask >>= 1;
//		}
//		System.out.println();
//	}
}
