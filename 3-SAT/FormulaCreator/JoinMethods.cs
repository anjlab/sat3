using System.Collections.Generic;
using System.Linq;

namespace FormulaCreator
{
    public static class JoinMethods
    {
        public static IEnumerable<IJoinMethod> GetMethods()
        {
            return new IJoinMethod[]
                       {
                           new Join3AsIs(),
                           new JoinBetweenTiers2(),
                           new JoinLeft2(),
                           new JoinRight2(),
                           new JoinLeft1(),
                           new JoinRight1(),
                           new JoinRight0(),
                       };
        }
    }

    public class JoinInfo
    {
        public int ConcatenationPower { get; set; }
        public ITriplet TargetPremutation { get; set; }
        public IJoinMethod JoinMethod { get; private set; }
        public string Rule { get; set; }

        public JoinInfo(IJoinMethod method)
        {
            JoinMethod = method;
            ConcatenationPower = -1;
        }
    }

    public interface IJoinMethod
    {
        JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet);
    }

    internal class Join3AsIs : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            var aIndex = formula.Premutation.IndexOf(triplet.AName);
            var bIndex = formula.Premutation.IndexOf(triplet.BName);
            var cIndex = formula.Premutation.IndexOf(triplet.CName);

            if (aIndex >= 0 && bIndex >= 0 && cIndex >= 0)
            {
                if (Follows(aIndex, bIndex, cIndex))
                {
                    result.Rule = "1";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = triplet;
                }
                else if (Follows(bIndex, aIndex, cIndex))
                {
                    result.Rule = "2";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.AName, triplet.CName);
                }
                else if (Follows(aIndex, cIndex, bIndex))
                {
                    result.Rule = "3";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName);
                }
                else if (Follows(cIndex, aIndex, bIndex))
                {
                    result.Rule = "4";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
                }
                else if (Follows(bIndex, cIndex, aIndex))
                {
                    result.Rule = "5";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.CName, triplet.AName);
                }
                else if (Follows(cIndex, bIndex, aIndex))
                {
                    result.Rule = "6";
                    result.ConcatenationPower = 3;
                    result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.BName, triplet.AName);
                }
            }

            return result;
        }

        private static bool Follows(int aIndex, int bIndex, int cIndex)
        {
            return Follows(aIndex, bIndex) && Follows(bIndex, cIndex);
        }

        private static bool Follows(int aIndex, int bIndex)
        {
            return aIndex == bIndex - 1;
        }
    }

    internal class JoinLeft1 : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            if (formula.Premutation[0] == triplet.CName
                && !formula.Premutation.Contains(triplet.BName)
                && !formula.Premutation.Contains(triplet.AName))
            {
                result.Rule = "1";
                result.ConcatenationPower = 1;
                result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.BName, triplet.CName);
            }
            else if (formula.Premutation[0] == triplet.BName
                && !formula.Premutation.Contains(triplet.AName)
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "2";
                result.ConcatenationPower = 1;
                result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName);
            }
            else if (formula.Premutation[0] == triplet.AName
                && !formula.Premutation.Contains(triplet.BName)
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "3";
                result.ConcatenationPower = 1;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.CName, triplet.AName);
            }

            return result;
        }
    }

    internal class JoinLeft2 : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            if (formula.Premutation[0] == triplet.BName
                && formula.Premutation[1] == triplet.CName
                && !formula.Premutation.Contains(triplet.AName))
            {
                result.Rule = "1";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.BName, triplet.CName);
            }
            else if (formula.Premutation[0] == triplet.CName
                && formula.Premutation[1] == triplet.BName
                && !formula.Premutation.Contains(triplet.AName))
            {
                result.Rule = "2";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName);
            }
            else if (formula.Premutation[0] == triplet.AName
                && formula.Premutation[1] == triplet.BName
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "3";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
            }
            else if (formula.Premutation[0] == triplet.BName
                && formula.Premutation[1] == triplet.AName
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "4";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.BName, triplet.AName);
            }
            else if (formula.Premutation[0] == triplet.AName
                && formula.Premutation[1] == triplet.CName
                && !formula.Premutation.Contains(triplet.BName))
            {
                result.Rule = "5";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.AName, triplet.CName);
            }
            else if (formula.Premutation[0] == triplet.CName
                && formula.Premutation[1] == triplet.AName
                && !formula.Premutation.Contains(triplet.BName))
            {
                result.Rule = "6";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.CName, triplet.AName);
            }

            return result;
        }
    }

    internal class JoinRight2 : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            if (formula.Premutation[formula.Premutation.Count - 2] == triplet.AName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.BName
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "1";
                result.ConcatenationPower = 2;
                result.TargetPremutation = triplet;
            }
            else if (formula.Premutation[formula.Premutation.Count - 2] == triplet.BName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.AName
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "2";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.AName, triplet.CName);
            }
            else if (formula.Premutation[formula.Premutation.Count - 2] == triplet.BName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.CName
                && !formula.Premutation.Contains(triplet.AName))
            {
                result.Rule = "3";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.CName, triplet.AName);
            }
            else if (formula.Premutation[formula.Premutation.Count - 2] == triplet.CName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.BName
                && !formula.Premutation.Contains(triplet.AName))
            {
                result.Rule = "4";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.BName, triplet.AName);
            }
            else if (formula.Premutation[formula.Premutation.Count - 2] == triplet.AName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.CName
                && !formula.Premutation.Contains(triplet.BName))
            {
                result.Rule = "5";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName);
            }
            else if (formula.Premutation[formula.Premutation.Count - 2] == triplet.CName
                && formula.Premutation[formula.Premutation.Count - 1] == triplet.AName
                && !formula.Premutation.Contains(triplet.BName))
            {
                result.Rule = "6";
                result.ConcatenationPower = 2;
                result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
            }

            return result;
        }
    }

    internal class JoinRight1 : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            if (formula.Premutation[formula.Premutation.Count - 1] == triplet.AName
                && !formula.Premutation.Contains(triplet.BName)
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "1";
                result.ConcatenationPower = 1;
                result.TargetPremutation = triplet;
            }
            else if (formula.Premutation[formula.Premutation.Count - 1] == triplet.BName
                && !formula.Premutation.Contains(triplet.AName)
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "2";
                result.ConcatenationPower = 1;
                result.TargetPremutation = new SimpleTriplet(triplet.BName, triplet.AName, triplet.CName);
            }
            else if (formula.Premutation[formula.Premutation.Count - 1] == triplet.CName
                && !formula.Premutation.Contains(triplet.AName)
                && !formula.Premutation.Contains(triplet.BName))
            {
                result.Rule = "3";
                result.ConcatenationPower = 1;
                result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
            }

            return result;
        }
    }

    internal class JoinRight0 : IJoinMethod
    {
        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            if (!formula.Premutation.Contains(triplet.AName)
                && !formula.Premutation.Contains(triplet.BName)
                && !formula.Premutation.Contains(triplet.CName))
            {
                result.Rule = "1";
                result.ConcatenationPower = 0;
                result.TargetPremutation = triplet;
            }

            return result;
        }
    }

    internal class JoinBetweenTiers2 : IJoinMethod
    {
        private static bool Follows(int aIndex, int bIndex)
        {
            return aIndex == bIndex - 1;
        }

        public JoinInfo GetJoinInfo(ITabularFormula formula, ITriplet triplet)
        {
            var result = new JoinInfo(this);

            int aIndex = formula.Premutation.IndexOf(triplet.AName);
            int bIndex = formula.Premutation.IndexOf(triplet.BName);
            int cIndex = formula.Premutation.IndexOf(triplet.CName);
            
            if (Follows(aIndex, bIndex) && cIndex < 0)
            {
                var aTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.AName));
                var bTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.BName));

                var aTier = aTiers.FirstOrDefault();
                var bTier = bTiers.FirstOrDefault();

                if (aTiers.Count() == 1 && bTiers.Count() == 1 
                    && aTier != null && bTier != null && aTier != bTier)
                {
                    result.Rule = "1";
                    result.ConcatenationPower = 2;
                    result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.CName, triplet.BName);
                }
            }
            else if (Follows(cIndex, bIndex) && aIndex < 0)
            {
                var cTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.CName));
                var bTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.BName));

                var cTier = cTiers.FirstOrDefault();
                var bTier = bTiers.FirstOrDefault();

                if (cTiers.Count() == 1 && bTiers.Count() == 1
                    && cTier != null && bTier != null && cTier != bTier)
                {
                    result.Rule = "2";
                    result.ConcatenationPower = 2;
                    result.TargetPremutation = new SimpleTriplet(triplet.CName, triplet.AName, triplet.BName);
                }
            }
            else if (Follows(aIndex, cIndex) && bIndex < 0)
            {
                var aTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.AName));
                var cTiers = formula.Tiers.Where(tier => tier[0].HasVariable(triplet.CName));

                var aTier = aTiers.FirstOrDefault();
                var cTier = cTiers.FirstOrDefault();

                if (aTiers.Count() == 1 && cTiers.Count() == 1
                    && aTier != null && cTier != null && aTier != cTier)
                {
                    result.Rule = "3";
                    result.ConcatenationPower = 2;
                    result.TargetPremutation = new SimpleTriplet(triplet.AName, triplet.BName, triplet.CName);
                }
            }

            return result;
        }
    }
}
