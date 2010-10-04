package com.anjlab.sat3;

import cern.colt.map.OpenIntObjectHashMap;

public class SimpleTripletValueFactory {

	public static final IMutableTripletValue _000_instance = new _000();
	public static final IMutableTripletValue _001_instance = new _001();
	public static final IMutableTripletValue _010_instance = new _010();
	public static final IMutableTripletValue _011_instance = new _011();
	public static final IMutableTripletValue _100_instance = new _100();
	public static final IMutableTripletValue _101_instance = new _101();
	public static final IMutableTripletValue _110_instance = new _110();
	public static final IMutableTripletValue _111_instance = new _111();

	private static final OpenIntObjectHashMap values = new OpenIntObjectHashMap(8 * 2 + 1);

	static
	{ 
		values.put(_000_instance.hashCode(), _000_instance);	//	0 + 17 = 17		Add 17 to hash code to make
		values.put(_001_instance.hashCode(), _001_instance);	//	4 + 17 = 21		keys unique across the hash map
		values.put(_010_instance.hashCode(), _010_instance);	//	2 + 17 = 19
		values.put(_011_instance.hashCode(), _011_instance);	//	6 + 17 = 23
		values.put(_100_instance.hashCode(), _100_instance);	//	1 + 17 = 18
		values.put(_101_instance.hashCode(), _101_instance);	//	5 + 17 = 22
		values.put(_110_instance.hashCode(), _110_instance);	//	3 + 17 = 20
		values.put(_111_instance.hashCode(), _111_instance);	//	7 + 17 = 24
		
		values.put(_000_instance.getTierKey(), _000_instance);	//	1
		values.put(_001_instance.getTierKey(), _001_instance);	//	2
		values.put(_010_instance.getTierKey(), _010_instance);	//	4
		values.put(_011_instance.getTierKey(), _011_instance);	//	8
		values.put(_100_instance.getTierKey(), _100_instance);	//	16
		values.put(_101_instance.getTierKey(), _101_instance);	//	32
		values.put(_110_instance.getTierKey(), _110_instance);	//	64
		values.put(_111_instance.getTierKey(), _111_instance);	//	128
	}
	
	public static IMutableTripletValue getTripletValue(int a, int b, int c)
	{
		int key = ((a > 0 ? 0 : 1))
		        + ((b > 0 ? 0 : 1) << 1)
		        + ((c > 0 ? 0 : 1) << 2)
		        + 17;
		
		return (IMutableTripletValue) values.get(key);
	}

	public static ITripletValue getTripletValue(int tierKey) {
		ITripletValue tripletValue = (ITripletValue) values.get(tierKey);
		return tripletValue;
	}
	
	private static class _000 implements IMutableTripletValue
	{
		public boolean isNotA() { return false;	}
		public boolean isNotB() { return false;	}
		public boolean isNotC() { return false; }
		public IMutableTripletValue swapAB() { return this; }
		public IMutableTripletValue swapAC() { return this; }
		public IMutableTripletValue swapBC() { return this; }
		public int hashCode() { return 0 + 17; }
		public int getTierKey() { return 1; }
	}

	private static class _001 implements IMutableTripletValue
	{
		public boolean isNotA() { return false;	}
		public boolean isNotB() { return false;	}
		public boolean isNotC() { return true; }
		public IMutableTripletValue swapAB() { return this; }
		public IMutableTripletValue swapAC() { return _100_instance; }
		public IMutableTripletValue swapBC() { return _010_instance; }
		public int hashCode() { return 4 + 17; }
		public int getTierKey() { return 2; }
	}

	private static class _010 implements IMutableTripletValue
	{
		public boolean isNotA() { return false;	}
		public boolean isNotB() { return true;	}
		public boolean isNotC() { return false; }
		public IMutableTripletValue swapAB() { return _100_instance; }
		public IMutableTripletValue swapAC() { return this; }
		public IMutableTripletValue swapBC() { return _001_instance; }
		public int hashCode() { return 2 + 17; }
		public int getTierKey() { return 4; }
	}

	private static class _011 implements IMutableTripletValue
	{
		public boolean isNotA() { return false;	}
		public boolean isNotB() { return true;	}
		public boolean isNotC() { return true; }
		public IMutableTripletValue swapAB() { return _101_instance; }
		public IMutableTripletValue swapAC() { return _110_instance; }
		public IMutableTripletValue swapBC() { return this; }
		public int hashCode() { return 6 + 17; }
		public int getTierKey() { return 8; }
	}

	private static class _100 implements IMutableTripletValue
	{
		public boolean isNotA() { return true;	}
		public boolean isNotB() { return false;	}
		public boolean isNotC() { return false; }
		public IMutableTripletValue swapAB() { return _010_instance; }
		public IMutableTripletValue swapAC() { return _001_instance; }
		public IMutableTripletValue swapBC() { return this; }
		public int hashCode() { return 1 + 17; }
		public int getTierKey() { return 16; }
	}

	private static class _101 implements IMutableTripletValue
	{
		public boolean isNotA() { return true;	}
		public boolean isNotB() { return false;	}
		public boolean isNotC() { return true; }
		public IMutableTripletValue swapAB() { return _011_instance; }
		public IMutableTripletValue swapAC() { return this; }
		public IMutableTripletValue swapBC() { return _110_instance; }
		public int hashCode() { return 5 + 17; }
		public int getTierKey() { return 32; }
	}

	private static class _110 implements IMutableTripletValue
	{
		public boolean isNotA() { return true;	}
		public boolean isNotB() { return true;	}
		public boolean isNotC() { return false; }
		public IMutableTripletValue swapAB() { return this; }
		public IMutableTripletValue swapAC() { return _011_instance; }
		public IMutableTripletValue swapBC() { return _101_instance; }
		public int hashCode() { return 3 + 17; }
		public int getTierKey() { return 64; }
	}

	private static class _111 implements IMutableTripletValue
	{
		public boolean isNotA() { return true;	}
		public boolean isNotB() { return true;	}
		public boolean isNotC() { return true; }
		public IMutableTripletValue swapAB() { return this; }
		public IMutableTripletValue swapAC() { return this; }
		public IMutableTripletValue swapBC() { return this; }
		public int hashCode() { return 7 + 17; }
		public int getTierKey() { return 128; }
	}

}
