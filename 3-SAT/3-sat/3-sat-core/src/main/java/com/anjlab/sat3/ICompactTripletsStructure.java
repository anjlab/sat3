package com.anjlab.sat3;

public interface ICompactTripletsStructure extends ITabularFormula
{
    ICompactTripletsStructure subtract(ITabularFormula formula);
    ICompactTripletsStructure union(ICompactTripletsStructure cts);
    ICompactTripletsStructure intersect(ICompactTripletsStructure cts);
    ICompactTripletsStructure concretize(int varName, boolean value);
}