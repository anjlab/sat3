package com.anjlab.sat3;

public class StopWatch
{
	private long start;
	private long end;
	public StopWatch(boolean startImediately)
	{
		if (startImediately)
		{
			start();
		}
	}
	public void start()
	{
		start = System.currentTimeMillis();
	}
	private void stop()
	{
		end = System.currentTimeMillis();
	}
	public void nextLap(String comment)
	{
		stop();
		
		System.out.println(comment + ": " + (end - start) + "ms");
		
		start();
	}
}
