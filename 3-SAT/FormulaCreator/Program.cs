using System;
using System.Collections.Generic;

namespace FormulaCreator
{
    public class Program
    {
        public static void Main(string[] args)
        {
            const int seed = 42;
            var rand = new Random(seed);

            const int maxN = 26;
            var n = rand.Next(3, maxN + 1);

            var mMax = 8*8*(n - 2);
            var m = rand.Next(1, mMax + 1);

            var formula = Helper.LoadFromFile("article-example.csv");

//            var formula = Helper.CreateRandomFormula(rand, n, m);

//            var filename = string.Format("{0}-{1}-{2}.csv", n, m, seed);
//            Helper.SaveToFile(formula, filename);
//            formula = Helper.LoadFromFile(filename);

            Console.WriteLine("Initial formula:");
            Console.WriteLine("---------------");

            Helper.PrettyPrint(formula);

            List<ITabularFormula> ctf = Helper.CreateCTF(formula);

            foreach (var f in ctf)
            {
                Console.WriteLine("------------------------");

                f.SortTiers();

                Helper.PrettyPrint(f);
            }

            Console.WriteLine("CTF: " + ctf.Count);

            Console.ReadLine();
        }
    }
}
