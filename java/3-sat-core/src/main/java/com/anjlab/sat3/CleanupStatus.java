package com.anjlab.sat3;

public class CleanupStatus
{
    public final boolean someClausesRemoved;
    public final int from;
    public final int to;
    public final int numberOfClausesRemoved;
    
    public CleanupStatus(boolean someClausesRemoved, int from, int to, int numberOfClausesRemoved)
    {
        this.someClausesRemoved = someClausesRemoved;
        this.from = from;
        this.to = to;
        this.numberOfClausesRemoved = numberOfClausesRemoved;
    }
}