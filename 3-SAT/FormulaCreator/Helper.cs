using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

namespace FormulaCreator
{
    public static class Helper
    {
        private class InternalJoinInfo : JoinInfo
        {
            public ITabularFormula Formula { get; private set; }

            public InternalJoinInfo(JoinInfo joinInfo, ITabularFormula formula)
                : base(joinInfo.JoinMethod)
            {
                ConcatenationPower = joinInfo.ConcatenationPower;
                TargetPremutation = joinInfo.TargetPremutation;
                Rule = joinInfo.Rule;
                Formula = formula;
            }
        }

        public static List<ITabularFormula> CreateCTF(ITabularFormula formula)
        {
            var ctf = new List<ITabularFormula>();

            foreach (var tier in formula.Tiers)
            {
                //  Search possible CTFs to which the tier may join
                var joinCandidates = GetJoinCandidates(ctf, tier);

                if (joinCandidates.Count != 0)
                {
                    var join = PickAJoin(joinCandidates);
                    join.Formula.ApplyJoin(join, tier);
                }
                else
                {
                    var f = new SimpleFormula();
                    f.ApplyJoin(new JoinInfo(null) { TargetPremutation = tier[0] }, tier);
                    ctf.Add(f);
                }
            }

            return ctf;
        }

        public static ICompactTripletsStructure CreateCompleteCTS(IList<int> premutation)
        {
            var formula = new SimpleFormula();

            for (var i = 0; i < premutation.Count - 2; i++)
            {
                var a = premutation[i];
                var b = premutation[i + 1];
                var c = premutation[i + 2];

                var tier = CreateCompleteTier(a, b, c);

                formula.Add(tier);
            }

            return formula;
        }

        private static IEnumerable<ITriplet> CreateCompleteTier(int a, int b, int c)
        {
            var tier = new List<ITriplet>(8);
            for (var i = 1; i >= -1; i -= 2)
                for (var j = 1; j >= -1; j -= 2)
                    for (var k = 1; k >= -1; k -= 2)
                        tier.Add(new SimpleTriplet(a*i, b*j, c*k));
            return tier;
        }

        public static List<int> CompletePremutation(ICollection<int> premutation, IEnumerable<int> variables)
        {
            var result = new List<int>();
            result.AddRange(premutation);
            foreach (var variable in variables)
            {
                if (!premutation.Contains(variable))
                {
                    result.Add(variable);
                }
            }
            return result;
        }

        private static Dictionary<int, IList<InternalJoinInfo>> GetJoinCandidates(IEnumerable<ITabularFormula> ctf, IList<ITriplet> tier)
        {
            var joinCandidates = new Dictionary<int, IList<InternalJoinInfo>>();

            foreach (var f in ctf)
            {
                foreach (var method in JoinMethods.GetMethods())
                {
                    var joinInfo = method.GetJoinInfo(f, tier[0]);

                    if (joinInfo.ConcatenationPower >= 0)
                    {
                        var internalJoinInfo = new InternalJoinInfo(joinInfo, f);

                        if (!joinCandidates.ContainsKey(joinInfo.ConcatenationPower))
                        {
                            var list = new List<InternalJoinInfo> {internalJoinInfo};
                            joinCandidates.Add(joinInfo.ConcatenationPower, list);
                        }
                        else
                        {
                            joinCandidates[joinInfo.ConcatenationPower].Add(internalJoinInfo);
                        }
                    }
                }
            }
            return joinCandidates;
        }

        private static InternalJoinInfo PickAJoin(IDictionary<int, IList<InternalJoinInfo>> dictionary)
        {
            //  We don't know what is the best join to pick
            //  This implementation is a greedy algorithm

            for (int i = 3; i >= 0; i--)
            {
                if (dictionary.ContainsKey(i))
                {
                    return dictionary[i][0];
                }
            }
            throw new InvalidOperationException();
        }

        public static ITabularFormula CreateRandomFormula(Random random, int varCount, int termCount)
        {
            var formula = new SimpleFormula();
            while (formula.TermCount < termCount)
            {
                formula.Add(CreateRandomTriplet(random, varCount));
            }
            return formula;
        }

        public static ITriplet CreateRandomTriplet(Random random, int varCount)
        {
            var a = random.Next(-varCount, varCount + 1);
            while (a == 0)
                a = random.Next(-varCount, varCount + 1);

            var b = random.Next(-varCount, varCount + 1);
            while (b == 0 || Math.Abs(b) == Math.Abs(a))
                b = random.Next(-varCount, varCount + 1);

            var c = random.Next(-varCount, varCount + 1);
            while (c == 0 || Math.Abs(c) == Math.Abs(b) || Math.Abs(c) == Math.Abs(a))
                c = random.Next(-varCount, varCount + 1);

            return new SimpleTriplet(a, b, c);
        }

        public static void Swap(ref int a, ref int b)
        {
            a = a + b;
            b = a - b;
            a = a - b;
        }

        public static void PrettyPrint(ITabularFormula formula)
        {
            Console.WriteLine(new string('-', 50));
            int longestVarName = 0;
            foreach (var varName in formula.Premutation)
            {
                var varNameLength = varName.ToString().Length;

                if (varNameLength > longestVarName)
                {
                    longestVarName = varNameLength;
                }
            }

            var builder = new StringBuilder();
            var spaces = new string(' ', longestVarName + 2);
            for (int i = 0; i < formula.VarCount; i++)
            {
                var varName = formula.Premutation[i];

                builder.Append(' ');
                builder.Append(GetLegendName(varName));
            }
            builder.Append('\n');
            if (formula.IsEmpty)
            {
                builder.Append("<empty>\n");
            }
            else
            {
                foreach (var tier in formula.Tiers)
                {
                    foreach (var triplet in tier)
                    {
                        for (var i = 0; i < formula.VarCount; i++)
                        {
                            var varName = formula.Premutation[i];

                            if (varName == triplet.AName)
                            {
                                builder.Append(spaces.Substring(0, GetLegendName(varName).Length));
                                builder.Append(triplet.NotA ? 1 : 0);
                            }
                            else if (varName == triplet.BName)
                            {
                                builder.Append(spaces.Substring(0, GetLegendName(varName).Length));
                                builder.Append(triplet.NotB ? 1 : 0);
                            }
                            else if (varName == triplet.CName)
                            {
                                builder.Append(spaces.Substring(0, GetLegendName(varName).Length));
                                builder.Append(triplet.NotC ? 1 : 0);
                            }
                            else
                            {
                                builder.Append(spaces.Substring(0, GetLegendName(varName).Length + 1));
                            }
                        }
                        builder.Append('\n');
                    }
                }
            }
            builder.Append("VarCount: "
                           + formula.VarCount
                           + "; TermCount: "
                           + formula.TermCount
                           + "; TiersCount: "
                           + formula.Tiers.Count);

            Console.WriteLine(builder);
        }

        static readonly char[] LegendBuffer = new char[100];
        const int ABC = 'z' - 'a' + 1;

        private static string GetLegendName(int varName)
        {
            return "x" + varName;

//            int count = 0;
//
//            while (varName > ABC)
//            {
//                var mod = varName%ABC;
//                LegendBuffer[count] = (char) ('a' + mod - 1);
//                varName = varName - ABC;
//                count++;
//            }
//
//            if (varName > 0)
//            {
//                LegendBuffer[count] = (char) ('a' + varName - 1);
//                count++;
//            }
//
//            return new string(LegendBuffer, 0, count);
        }

        public static void SaveToFile(ITabularFormula formula, string filename)
        {
            using (var output = new FileStream(filename, FileMode.Create))
            using (var writer = new StreamWriter(output))
            {
                var builder = new StringBuilder();
                foreach (var tier in formula.Tiers)
                {
                    foreach (var triplet in tier)
                    {
                        if (triplet.NotA) builder.Append('-');
                        builder.Append(triplet.AName);
                        builder.Append(',');
                        if (triplet.NotB) builder.Append('-');
                        builder.Append(triplet.BName);
                        builder.Append(',');
                        if (triplet.NotC) builder.Append('-');
                        builder.Append(triplet.CName);
                        builder.Append('\n');
                    }
                }
                writer.Write(builder.ToString());
            }
        }

        public static ITabularFormula LoadFromFile(string filename)
        {
            using (var input = new FileStream(filename, FileMode.Open))
            using (var reader = new StreamReader(input))
            {
                var formula = new SimpleFormula();

                string line;
                while ((line = reader.ReadLine()) != null)
                {
                    var values = line.Split(',');
                    var triplet = new SimpleTriplet(Int32.Parse(values[0]),
                                                    Int32.Parse(values[1]),
                                                    Int32.Parse(values[2]));

                    formula.Add(triplet);
                }

                return formula;
            }
        }

        public static List<ICompactTripletsStructure> CreateCTS(ITabularFormula formula, List<ITabularFormula> ctf)
        {
            var cts = new List<ICompactTripletsStructure>();

            foreach (var f in ctf)
            {
                if (!f.TiersSorted)
                {
                    throw new InvalidOperationException("Tiers should be sorted");
                }

                var targetPremutation = CompletePremutation(f.Premutation, formula.Premutation);

                var template = CreateCompleteCTS(targetPremutation);

                var draftCTS = template.Subtract(f);

                cts.Add(draftCTS);
            }
            return cts;
        }

        public static ITabularFormula CreateFormula(int[] values)
        {
            if (values.Length%3 != 0)
            {
                throw new ArgumentException("Number of values must be a multiple of 3");
            }
            var formula = new SimpleFormula();
            for (var i = 0; i < values.Length; i +=3)
            {
                var triplet = new SimpleTriplet(values[i], values[i + 1], values[i + 2]);
                formula.Add(triplet);
            }
            return formula;
        }
    }
}
