package com.anjlab.sat3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StopWatch
{
    private static final Logger LOGGER = LoggerFactory.getLogger(StopWatch.class);
    
    private long overall;
    private long start;
    private long end;
    private String comment;

    public void start(String comment)
    {
        Helper.printLine('*', 70);
        LOGGER.info(comment + "...");
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
        LOGGER.info("{}: {}ms; overall: {}ms", new Object[] { comment, (end - start), overall });
    }
}
