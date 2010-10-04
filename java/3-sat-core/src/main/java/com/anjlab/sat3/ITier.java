package com.anjlab.sat3;

public interface ITier extends Iterable<ITripletValue>, ITripletPermutation
{
	boolean add(ITripletValue triplet);

	int size();

	boolean contains(ITripletValue triplet);

	void remove(ITripletValue triplet);

	void subtract(ITier tier);

	public abstract void adjoinLeft(ITier tier);

	public abstract void adjoinRight(ITier tier);

}
