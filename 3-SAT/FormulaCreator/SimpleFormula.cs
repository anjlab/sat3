using System.Collections.Generic;

namespace FormulaCreator
{
    public class SimpleFormula : ITabularFormula
    {
        public int VarCount { get; private set;}

        public int TermCount { get; private set; }

        private readonly IList<IList<ITriplet>> _tiers;

        private readonly IList<int> _premutation;

        public SimpleFormula()
        {
            _premutation = new List<int>();
            _tiers = new List<IList<ITriplet>>();

            VarCount = 0;
        }

        public IList<IList<ITriplet>> Tiers
        {
            get { return _tiers; }
        }

        public void SortTiers()
        {
            ((List<IList<ITriplet>>) _tiers).Sort(
                (t1, t2) => _premutation.IndexOf(t1[0].AName) - _premutation.IndexOf(t2[0].AName));
        }

        public void Add(ITriplet triplet)
        {
            IList<ITriplet> targetTier = null;
            foreach (var tier in Tiers)
            {
                if (tier[0].HasSameVariablesAs(triplet))
                {
                    targetTier = tier;
                    break;
                }
            }

            ITriplet tierPremutation = null;

            if (targetTier == null)
            {
                targetTier = new List<ITriplet>(8);
                _tiers.Add(targetTier);

                if (!_premutation.Contains(triplet.AName)) { _premutation.Add(triplet.AName); VarCount++; }
                if (!_premutation.Contains(triplet.BName)) { _premutation.Add(triplet.BName); VarCount++; }
                if (!_premutation.Contains(triplet.CName)) { _premutation.Add(triplet.CName); VarCount++; }

                int aIndex = _premutation.IndexOf(triplet.AName);
                int bIndex = _premutation.IndexOf(triplet.BName);
                int cIndex = _premutation.IndexOf(triplet.CName);

                tierPremutation = OrderIs(aIndex, bIndex, cIndex) ? new SimpleTriplet(triplet.AName, triplet.BName, triplet.CName) :
                                  OrderIs(bIndex, aIndex, cIndex) ? new SimpleTriplet(triplet.BName, triplet.AName, triplet.CName) :
                                  OrderIs(bIndex, cIndex, aIndex) ? new SimpleTriplet(triplet.BName, triplet.CName, triplet.AName) :
                                  OrderIs(cIndex, bIndex, aIndex) ? new SimpleTriplet(triplet.CName, triplet.BName, triplet.AName) :
                                  OrderIs(aIndex, cIndex, bIndex) ? new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName) :
                                                                    new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
                                  
            }

            if (targetTier.Contains(triplet))
            {
                //  Do not add same triplets since they won't affect formula satisfiability
                return;
            }

            targetTier.Add(triplet.TransformAs(targetTier.Count == 0 ? tierPremutation : targetTier[0]));

            TermCount++;
        }

        private static bool OrderIs(int aIndex, int bIndex, int cIndex)
        {
            return (aIndex < bIndex && bIndex < cIndex);
        }

        public IList<int> Premutation
        {
            get { return _premutation; }
        }

        public void Add(IEnumerable<ITriplet> triplets)
        {
            foreach (var triplet in triplets)
            {
                Add(triplet);
            }
        }

        public void ApplyJoin(JoinInfo joinInfo, IList<ITriplet> triplets)
        {
            int varIndex;
            if ((varIndex = Premutation.IndexOf(joinInfo.TargetPremutation.AName)) >= 0)
            {
                if (varIndex + 1 > Premutation.Count - 1 || Premutation[varIndex + 1] != joinInfo.TargetPremutation.BName)
                {
                    if (varIndex + 1 > Premutation.Count - 1)
                    {
                        Premutation.Add(joinInfo.TargetPremutation.BName);
                    }
                    else
                    {
                        Premutation.Insert(varIndex + 1, joinInfo.TargetPremutation.BName);
                    }
                    VarCount++;
                }
                if (varIndex + 2 > Premutation.Count - 1 || Premutation[varIndex + 2] != joinInfo.TargetPremutation.CName)
                {
                    if (varIndex + 2 > Premutation.Count - 1)
                    {
                        Premutation.Add(joinInfo.TargetPremutation.CName);
                    }
                    else
                    {
                        Premutation.Insert(varIndex + 2, joinInfo.TargetPremutation.CName);
                    }
                    VarCount++;
                }
            }
            else if ((varIndex = Premutation.IndexOf(joinInfo.TargetPremutation.BName)) >= 0)
            {
                int offset = 0;
                if (varIndex - 1 < 0 || Premutation[varIndex - 1] != joinInfo.TargetPremutation.AName)
                {
                    if (varIndex - 1 < 0) offset++;
                    Premutation.Insert(varIndex - 1 + offset, joinInfo.TargetPremutation.AName);
                    VarCount++;
                }
                if (varIndex + 1 + offset > Premutation.Count - 1 || Premutation[varIndex + 1 + offset] != joinInfo.TargetPremutation.CName)
                {
                    if (varIndex + 1 + offset > Premutation.Count - 1)
                    {
                        Premutation.Add(joinInfo.TargetPremutation.CName);
                    }
                    else
                    {
                        Premutation.Insert(varIndex + 1 + offset, joinInfo.TargetPremutation.CName);
                    }
                    VarCount++;
                }
            }
            else if ((varIndex = Premutation.IndexOf(joinInfo.TargetPremutation.CName)) >= 0)
            {
                int offset = 0;
                if (varIndex - 1 < 0 || Premutation[varIndex - 1] != joinInfo.TargetPremutation.BName)
                {
                    if (varIndex - 1 < 0) offset++;

                    Premutation.Insert(varIndex - 1 + offset, joinInfo.TargetPremutation.BName);
                    VarCount++;
                }
                if (varIndex - 2 + offset < 0 || Premutation[varIndex - 2 + offset] != joinInfo.TargetPremutation.AName)
                {
                    if (varIndex - 2 + offset < 0) offset++;

                    Premutation.Insert(varIndex - 2 + offset, joinInfo.TargetPremutation.AName);
                    VarCount++;
                }
            }
            else
            {
                Premutation.Add(joinInfo.TargetPremutation.AName);
                Premutation.Add(joinInfo.TargetPremutation.BName);
                Premutation.Add(joinInfo.TargetPremutation.CName);
                VarCount += 3;
            }
            Add(triplets);
        }
    }
}