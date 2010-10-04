package com.anjlab.sat3;

public interface IMutableTripletValue extends ITripletValue
{
	IMutableTripletValue swapAB();
	IMutableTripletValue swapAC();
	IMutableTripletValue swapBC();
}