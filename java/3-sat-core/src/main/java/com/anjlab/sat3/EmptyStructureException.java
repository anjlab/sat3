package com.anjlab.sat3;

public class EmptyStructureException extends RuntimeException
{
    private static final long serialVersionUID = -1L;
    
    private Object structure;

    public EmptyStructureException(ICompactTripletsStructure cts)
    {
        this.structure = cts;
    }

    public EmptyStructureException(IHyperStructure hs)
    {
        this.structure = hs;
    }
    
    public EmptyStructureException(ITabularFormula s)
    {
        this.structure = s;
    }

    public Object getStructure()
    {
        return structure;
    }
}
