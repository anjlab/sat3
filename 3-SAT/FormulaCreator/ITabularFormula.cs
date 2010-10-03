using System.Collections.Generic;

namespace FormulaCreator
{
    public interface ITabularFormula
    {
        int VarCount { get; }
        int TermCount { get; }
        IList<IList<ITriplet>> Tiers { get; }
        bool TiersSorted { get; }
        void SortTiers();
        void Add(ITriplet triplet);
        void Add(IEnumerable<ITriplet> triplet);
        IList<int> Premutation { get; }
        void ApplyJoin(JoinInfo joinInfo, IList<ITriplet> triplets);
        bool IsEmpty { get; }
    }
}