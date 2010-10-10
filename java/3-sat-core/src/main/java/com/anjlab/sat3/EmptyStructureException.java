package com.anjlab.sat3;

public class EmptyStructureException extends Exception
{
    private static final long serialVersionUID = -1L;
    
    private ICompactTripletsStructure cts;

    public EmptyStructureException(ICompactTripletsStructure cts)
    {
        this.cts = cts;
    }

    public ICompactTripletsStructure getCTS()
    {
        return cts;
    }
}
