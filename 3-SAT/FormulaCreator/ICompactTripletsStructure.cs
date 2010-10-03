using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace FormulaCreator
{
    public interface ICompactTripletsStructure : ITabularFormula
    {
        ICompactTripletsStructure Subtract(ITabularFormula formula);
        ICompactTripletsStructure Union(ICompactTripletsStructure cts);
        ICompactTripletsStructure Intersect(ICompactTripletsStructure cts);
        ICompactTripletsStructure Concretize(int varName, bool value);
    }
}
