package com.anjlab.sat3;

public class StopWatch
{
	private long start;
	private long end;
	private String comment;

	public void start(String comment)
	{
		this.comment = comment;
		start = System.currentTimeMillis();
	}
	public void stop()
	{
		end = System.currentTimeMillis();
	}
	public void printElapsed()
	{
		System.out.println(comment + ": " + (end - start) + "ms");
	}
}
