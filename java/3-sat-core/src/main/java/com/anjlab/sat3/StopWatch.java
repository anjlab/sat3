package com.anjlab.sat3;

import static java.text.MessageFormat.format;

public class StopWatch
{
    private long overall;
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
        overall += (end - start);
    }
    public void printElapsed()
    {
        System.out.println(format("{0}: {1}ms; overall: {2}ms", comment, (end - start), overall));
    }
}
