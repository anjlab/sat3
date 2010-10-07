package com.anjlab.sat3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cern.colt.map.OpenIntObjectHashMap;

public class Helper
{
    public static boolean UsePrettyPrint = false;
    
    private static class InternalJoinInfo extends JoinInfo
    {
        public ITabularFormula formula;

        public InternalJoinInfo(JoinInfo joinInfo, ITabularFormula formula)
        {
            super(joinInfo.joinMethod);
            concatenationPower = joinInfo.concatenationPower;
            targetPermutation = joinInfo.targetPermutation;
            rule = joinInfo.rule;
            this.formula = formula;
        }
        
        public String toString()
        {
            return joinMethod.getClass().getSimpleName() 
                 + " rule " + rule 
                 + " (pow=" + concatenationPower 
                 + ", perm=" + targetPermutation
                 + ", f=" + formula + ")";
        }
    }

    public static GenericArrayList<ITabularFormula> createCTF(ITabularFormula formula)
    {
        GenericArrayList<ITabularFormula> ctf = new GenericArrayList<ITabularFormula>();

        GenericArrayList<ITier> tiers = formula.getTiers();
        for (int i = 0; i < tiers.size(); i++)
        {
            ITier tier = tiers.get(i);
            //  Search possible CTFs to which the tier may join
            OpenIntObjectHashMap joinCandidates = getJoinCandidates(ctf, tier);

            if (joinCandidates.size() != 0)
            {
                InternalJoinInfo join = pickAJoin(joinCandidates);
                join.formula.applyJoin(join, tier);
            }
            else
            {
                ITabularFormula f = new SimpleFormula();
                JoinInfo joinInfo = new JoinInfo(null);
                joinInfo.targetPermutation = tier;
                f.applyJoin(joinInfo, tier);
                ctf.add(f);
            }
        }

        return ctf;
    }

    public static ICompactTripletsStructure createCompleteCTS(IPermutation permutation)
    {
        SimpleFormula formula = new SimpleFormula(permutation);

        for (int i = 0; i < permutation.size() - 2; i++)
        {
            int a = permutation.get(i);
            int b = permutation.get(i + 1);
            int c = permutation.get(i + 2);

            ITier tier = SimpleTier.createCompleteTier(a, b, c);

            formula.addTier(tier);
        }

        return formula;
    }

    public static IPermutation completePermutation(IPermutation permutationHead, IPermutation variables)
    {
        IPermutation result = new SimplePermutation(permutationHead, variables.size());
        if (permutationHead.size() == variables.size())
        {
            //    Nothing to complete (permutationHead is already completed permutation)
            return result;
        }
        for (int i = 0; i < variables.size(); i++)
        {
            int varName = variables.get(i);
            if (!permutationHead.contains(varName))
            {
                result.add(varName);
            }
        }
        return result;
    }

    private static OpenIntObjectHashMap getJoinCandidates(GenericArrayList<ITabularFormula> ctf, ITier tier)
    {
        IJoinMethod[] methods = JoinMethods.getMethods();

        OpenIntObjectHashMap joinCandidates = new OpenIntObjectHashMap(methods.length);

        for (int j = 0; j < ctf.size(); j++)
        {
            ITabularFormula f = ctf.get(j);
            for (int i = 0; i < methods.length; i++)
            {
                IJoinMethod method = methods[i];
                
                JoinInfo joinInfo = method.getJoinInfo(f, tier);

                if (joinInfo.concatenationPower >= 0)
                {
                    InternalJoinInfo internalJoinInfo = new InternalJoinInfo(joinInfo, f);

                    if (!joinCandidates.containsKey(joinInfo.concatenationPower))
                    {
                        List<InternalJoinInfo> list = new ArrayList<InternalJoinInfo>();
                        list.add(internalJoinInfo);
                        joinCandidates.put(joinInfo.concatenationPower, list);
                        
                        //    According to current implementation of
                        //    #pickAJoin() we always pick join with greatest
                        //    concatenationPower.
                        //    Now JoinMethods.getMethods() returns all
                        //    joinMethods ordered by concatenation power (descending) 
                        //    of possible joins they will find.
                        //    Since in #pickAJoin() we always pick a join that was 
                        //    founded by first joinMethod we may simply return 
                        //    first join candidate.
                        
                        //    Note: Comment the following line if you want to change implementation of #pickAJoin()
                        
                        return joinCandidates;
                    }
                    else
                    {
                        @SuppressWarnings("unchecked")
                        List<InternalJoinInfo> candidates = 
                            (List<InternalJoinInfo>) joinCandidates.get(joinInfo.concatenationPower);
                        candidates.add(internalJoinInfo);
                    }
                }
            }
        }
        return joinCandidates;
    }

    private static InternalJoinInfo pickAJoin(OpenIntObjectHashMap dictionary)
    {
        //  We don't know what is the best join to pick
        //  This implementation is a greedy algorithm 
        //    which returns first founded join with maximum concatenation power

        //    If one want to change this algorithm, make sure you also changed
        //    #getJoinCandidates() (see comments in method body)
        
        for (int i = 3; i >= 0; i--)
        {
            if (dictionary.containsKey(i))
            {
                @SuppressWarnings("unchecked")
                List<InternalJoinInfo> candidates = (List<InternalJoinInfo>) dictionary.get(i);
                return candidates.get(0);
            }
        }
        throw new RuntimeException();
    }

    public static ITabularFormula createRandomFormula(Random random, int varCount, int clausesCount)
    {
        int mMax = getMaxNumberOfUniqueTriplets(varCount);
        
        if (clausesCount > mMax) { 
            throw new IllegalArgumentException(MessageFormat
                .format("3-SAT formula of {0} variables may have at most {1} valuable clauses, but requested to create formula with " + clausesCount + " clauses",
                        varCount, mMax));
        }
        
        ITabularFormula formula = new SimpleFormula();
        while (formula.getClausesCount() < clausesCount)
        {
            formula.add(createRandomTriplet(random, varCount));
        }
        return formula;
    }

    public static ITriplet createRandomTriplet(Random random, int varCount)
    {
        int a = random.nextInt(2 * varCount + 1) - varCount;
        while (a == 0)
            a = random.nextInt(2 * varCount + 1) - varCount;

        int b = random.nextInt(2 * varCount + 1) - varCount;
        while (b == 0 || Math.abs(b) == Math.abs(a))
            b = random.nextInt(2 * varCount + 1) - varCount;

        int c = random.nextInt(2 * varCount + 1) - varCount;
        while (c == 0 || Math.abs(c) == Math.abs(b) || Math.abs(c) == Math.abs(a))
            c = random.nextInt(2 * varCount + 1) - varCount;

        return new SimpleTriplet(a, b, c);
    }

    public static void prettyPrint(ITabularFormula formula)
    {
        printLine('-', 50);

        StringBuilder builder = buildPrettyOutput(formula);

        System.out.println(builder);
    }

   public static StringBuilder buildPrettyOutput(ITabularFormula formula)
    {
        StringBuilder builder = new StringBuilder();

        if (UsePrettyPrint)
        {
            int longestVarName = 0;
            IPermutation permutation = formula.getPermutation();
            for (int i = 0; i < permutation.size(); i++)
            {
                int varName = permutation.get(i);
                int varNameLength = String.valueOf(varName).length();
    
                if (varNameLength > longestVarName)
                {
                    longestVarName = varNameLength;
                }
            }
    
            char[] spacesArray = new char[longestVarName + 2];
            Arrays.fill(spacesArray, ' ');
            String spaces = new String(spacesArray);
            
            for (int i = 0; i < formula.getVarCount(); i++)
            {
                int varName = formula.getPermutation().get(i);
    
                builder.append(' ');
                builder.append(getLegendName(varName));
            }
            builder.append('\n');
            if (formula.isEmpty())
            {
                builder.append("<empty>\n");
            }
            else
            {
                GenericArrayList<ITier> tiers = formula.getTiers();
                for (int j = 0; j < tiers.size(); j++)
                {
                    ITier tier = tiers.get(j);
                    for (ITripletValue tripletValue : tier)
                    {
                        for (int i = 0; i < formula.getVarCount(); i++)
                        {
                            int varName = formula.getPermutation().get(i);
    
                            if (varName == tier.getAName())
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length()));
                                builder.append(tripletValue.isNotA() ? 1 : 0);
                            }
                            else if (varName == tier.getBName())
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length()));
                                builder.append(tripletValue.isNotB() ? 1 : 0);
                            }
                            else if (varName == tier.getCName())
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length()
                                        ));
                                builder.append(tripletValue.isNotC() ? 1 : 0);
                            }
                            else
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length() + 1));
                            }
                        }
                        builder.append('\n');
                    }
                }
            }
        }
        
        builder.append("VarCount: "
                       + formula.getVarCount()
                       + "; ClausesCount: "
                       + formula.getClausesCount()
                       + "; TiersCount: "
                       + formula.getTiers().size());
        return builder;
    }

    static final char[] LEGEND_BUFFER = new char[100];
    static final int ABC = 'z' - 'a' + 1;

    private static String getLegendName(int varName)
    {
//        return "x" + varName;

            int count = 0;

            while (varName > ABC)
            {
                int mod = varName%ABC;
                LEGEND_BUFFER[count] = (char) ('a' + mod - 1);
                varName = varName - ABC;
                count++;
            }

            if (varName > 0)
            {
                LEGEND_BUFFER[count] = (char) ('a' + varName - 1);
                count++;
            }

            return new String(LEGEND_BUFFER, 0, count);
    }

    public static void saveToDIMACSFileFormat(ITabularFormula formula, String filename) throws IOException
    {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filename)), "ascii"));
    
            StringBuilder builder = new StringBuilder();
            builder.append("p cnf ");
            builder.append(formula.getVarCount());
            builder.append(" ");
            builder.append(formula.getClausesCount());
            builder.append('\n');
            GenericArrayList<ITier> tiers = formula.getTiers();
            for (int i = 0; i < tiers.size(); i++)
            {
                ITier tier = tiers.get(i);
                for (ITripletValue tripletValue : tier)
                {
                    if (tripletValue.isNotA()) builder.append('-');
                    builder.append(tier.getAName());
                    builder.append(' ');
                    if (tripletValue.isNotB()) builder.append('-');
                    builder.append(tier.getBName());
                    builder.append(' ');
                    if (tripletValue.isNotC()) builder.append('-');
                    builder.append(tier.getCName());
                    builder.append(" 0\n");
                }
            }
            
            writer.write(builder.toString());
        } 
        finally 
        {
            if (writer != null)
            {
                writer.close();
            }
        }
    }

    public static ITabularFormula loadFromDIMACSFileFormat(String filename) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "ascii"));
            
            return new FormulaReader().readFormula(reader);
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public static GenericArrayList<? extends ITabularFormula> createCTS(ITabularFormula formula, GenericArrayList<ITabularFormula> ctf)
    {
        GenericArrayList<ICompactTripletsStructure> cts = new GenericArrayList<ICompactTripletsStructure>(ctf.size());

        for (int i = 0; i < ctf.size(); i++)
        {
            ITabularFormula f = ctf.get(i);
            
            if (!f.tiersSorted())
            {
                throw new IllegalArgumentException("Tiers should be sorted");
            }

            IPermutation targetPermutation = completePermutation(f.getPermutation(), formula.getPermutation());

            ICompactTripletsStructure template = createCompleteCTS(targetPermutation);

            template.subtract(f);

            cts.add(template);
        }
        return cts;
    }
    
    public static ITabularFormula createFormula(int[] values)
    {
        if (values.length%3 != 0)
        {
            throw new IllegalArgumentException("Number of values must be a multiple of 3");
        }
        SimpleFormula formula = new SimpleFormula();
        for (int i = 0; i < values.length; i +=3)
        {
            ITriplet triplet = new SimpleTriplet(values[i], values[i + 1], values[i + 2]);
            formula.add(triplet);
        }
        return formula;
    }

    public static ITabularFormula createRandomFormula(int seed, int nMax) {
        Random rand = new Random(seed);
        
        int n = rand.nextInt(nMax + 1) + 3;
        
        int mMax = getMaxNumberOfUniqueTriplets(n);
        int m = rand.nextInt(mMax + 1) + 1;
        
        ITabularFormula formula = createRandomFormula(rand, n, m);
        
        return formula;
    }

    private static int getMaxNumberOfUniqueTriplets(int varCount) {
        return varCount*(varCount - 1)*(varCount - 2)*(varCount - 3 + 1)/6*8;
    }

    public static void printLine(char c, int length) {
        char[] line = new char[length];
        Arrays.fill(line, c);
        
        System.out.println(new String(line));
    }

    private static class FormulaReader
    {
        private int n = 0;
        private int sign = 1;
        private int r = 0;
        private int a, b, c;

//        private int lineNumber;
        
        public String toString()
        {
            return "n=" + n + ", sign=" + sign + ", r=" + r + ", a=" + a + ", b=" + b + ", c=" + c;
        }
        
        private ITabularFormula formula = new SimpleFormula();

//        private long prevTime;
        
        public ITabularFormula readFormula(BufferedReader reader) throws IOException
        {
//            prevTime = System.currentTimeMillis();
            readMetadata(reader);
            
            int ch;
            while ((ch = reader.read()) != -1)
            {
                if (Character.isWhitespace(ch))
                {
                    if (r != 0) newNumber();
                    continue;
                }
                if (ch == '0' && r == 0)
                {
//                    lineNumber++;
//                    
//                    if (lineNumber % 1000 == 0)
//                    {
//                        System.out.println((System.currentTimeMillis() - prevTime) + " " + lineNumber + " vc=" + formula.getVarCount() + " tc=" + formula.getClausesCount() + " t=" + formula.getTiers().size());
//                        prevTime = System.currentTimeMillis();
//                    }
                    
                    continue;
                }
                if (ch == '-')
                {
                    sign = -1;
                }

                if ('0' <= ch && ch < '0' + 10)
                {
                    r = r * 10 + ch - '0';
                } 
                else
                {
                    newNumber();
                }
            }
            return formula;
        }

        private void readMetadata(BufferedReader reader) throws IOException
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
//                lineNumber++;
                if (line.startsWith("c"))
                {
                    continue;
                }
                if (line.startsWith("p"))
                {
                    if (!line.contains("cnf"))
                    {
                        throw new RuntimeException("Bad DIMACS CNF file format");
                    }
                    break;
                }
            }
        }

        private void newNumber()
        {
            if (r == 0) return;

            r = r * sign;
            if (n == 0)
            {
                a = r;
                n++;
            } 
            else if (n == 1)
            {
                b = r;
                n++;
            } 
            else if (n == 2)
            {
                c = r;
                addTriplet();
                n = 0;
            }
            
            r = 0;
            sign = 1;
        }

        private void addTriplet()
        {
            ITriplet triplet = new SimpleTriplet(a, b, c);

            formula.add(triplet);
        }
    }
}
