using FormulaCreator;
using NUnit.Framework;

namespace Tests
{
    [TestFixture]
    public class TestCTSOperations
    {
        [Test]
        public void TestConcretize()
        {
            var s1 = (ICompactTripletsStructure)
                     Helper.CreateFormula(
                         new[]
                             {
                                 1, 2, 3,
                                 -1, 2, -3,
                                 2, -3, 4,
                                 2, 3, -4
                             });

//             x1 x2 x3 x4
//              0  0  0   
//              1  0  1   
//                 0  1  0
//                 0  0  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

            var s2 = s1.Concretize(3, true);

//             x4 x1 x2 x3
//              0  0  0   
//              0  1  1   
//                 0  0  0
//                 1  1  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

            Helper.PrettyPrint(s1);
            Helper.PrettyPrint(s2);

            Assert.AreEqual(2, s2.TermCount);
            Assert.AreEqual(new SimpleTriplet(-1, 2, -3), s2.Tiers[0][0]);
            Assert.AreEqual(new SimpleTriplet(2, -3, 4), s2.Tiers[1][0]);
        }

        [Test]
        public void TestConcretizeWithCleanup()
        {
            var s1 = (ICompactTripletsStructure)
                     Helper.CreateFormula(
                         new[]
                             {
                                 1, 2, 3, 
                                 -1, 2, -3,
                                 2, -3, 4,
                                 2, 3, -4,
                                 -4, 3, 5
                             });

//             x1 x2 x3 x4 x5
//              0  0  0      
//              1  0  1      
//                 0  1  0   
//                 0  0  1   
//                    0  1  0
//            VarCount: 5; TermCount: 5; TiersCount: 3

            var s2 = s1.Concretize(2, true);

            Helper.PrettyPrint(s1);
            Helper.PrettyPrint(s2);

            Assert.That(s2.IsEmpty);
        }

        [Test]
        public void TestUnion()
        {
            var s1 = (ICompactTripletsStructure)
                     Helper.CreateFormula(
                         new[]
                             {
                                 1, 2, 3, 
                                 -1, 2, -3,
                                 2, -3, 4,
                                 2, 3, -4
                             });

//             x1 x2 x3 x4
//              0  0  0   
//              1  0  1   
//                 0  1  0
//                 0  0  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

            var s2 = (ICompactTripletsStructure)
                     Helper.CreateFormula(
                         new[]
                             {
                                 4, 1, 2,
                                 4, -1, -2, 
                                 1, 2, 3,
                                 -1, -2, -3
                             });

//             x4 x1 x2 x3
//              0  0  0   
//              0  1  1   
//                 0  0  0
//                 1  1  1
//            VarCount: 4; TermCount: 4; TiersCount: 2

            Helper.PrettyPrint(s1);
            Helper.PrettyPrint(s2);
        }
    }
}
