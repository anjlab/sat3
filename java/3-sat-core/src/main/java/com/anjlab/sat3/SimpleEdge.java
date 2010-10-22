package com.anjlab.sat3;

public class SimpleEdge implements IEdge
{
    private IVertex source;
    private IVertex target;
    private IEdge next1;
    private IEdge previous1;
    private IEdge next2;
    private IEdge previous2;
    
    public SimpleEdge(IVertex source, IVertex target)
    {
        this.source = source;
        this.target = target;
    }

    public SimpleEdge(IVertex source)
    {
        this.source = source;
    }

    public IVertex getSource()
    {
        return source;
    }
    public IVertex getTarget()
    {
        return target;
    }
    public void setNext1(IEdge edge)
    {
        this.next1 = edge;
    }
    public void setPrevious1(IEdge edge)
    {
        this.previous1 = edge;
    }
    public IEdge getNext1()
    {
        return next1;
    }
    public IEdge getPrevious1()
    {
        return previous1;
    }
    public IEdge getNext2()
    {
        return next2;
    }
    public IEdge getPrevious2()
    {
        return previous2;
    }
    public void setNext2(IEdge next2)
    {
        this.next2 = next2;
    }
    public void setPrevious2(IEdge previous2)
    {
        this.previous2 = previous2;
    }
    public String toString()
    {
        return source.toString();
    }
}
