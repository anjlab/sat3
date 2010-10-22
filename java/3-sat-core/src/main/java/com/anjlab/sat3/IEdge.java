package com.anjlab.sat3;

public interface IEdge
{

    IVertex getSource();

    void setNext1(IEdge edge);
    IEdge getNext1();
    void setPrevious1(IEdge edge);
    IEdge getPrevious1();
    void setNext2(IEdge edge);
    IEdge getNext2();
    void setPrevious2(IEdge edge);
    IEdge getPrevious2();
}
