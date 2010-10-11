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

import cern.colt.function.LongObjectProcedure;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenLongObjectHashMap;

public class Helper
{
    public static boolean UsePrettyPrint = false;

    public static boolean EnableAssertions = false;
    
    public static boolean UseUniversalVarNames = false;
    
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

        int tierCount = permutation.size() - 2;
        
        int[] permutationElements = ((SimplePermutation)permutation).elements();
        
        for (int i = 0; i < tierCount; i++)
        {
            int a = permutationElements[i];
            int b = permutationElements[i + 1];
            int c = permutationElements[i + 2];

            ITier tier = SimpleTier.createCompleteTier(a, b, c);

            formula.addTier(tier);
        }

        return formula;
    }

    public static IPermutation completePermutation(IPermutation permutationHead, IPermutation variables)
    {
        int varCount = variables.size();
        
        IPermutation result = new SimplePermutation(permutationHead, varCount);
        if (permutationHead.size() == varCount)
        {
            //    Nothing to complete (permutationHead is already completed permutation)
            return result;
        }
        int[] variablesElements = ((SimplePermutation)variables).elements();
        for (int i = 0; i < varCount; i++)
        {
            int varName = variablesElements[i];
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

        int ctfCount = ctf.size();
        Object[] ctfElements = ctf.elements();
        
        for (int j = 0; j < ctfCount; j++)
        {
            ITabularFormula f = (ITabularFormula)ctfElements[j];
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
        if (UseUniversalVarNames)
        {
            return "x" + varName;
        }
        else
        {
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

    public static GenericArrayList<ICompactTripletsStructure> createCTS(ITabularFormula formula, GenericArrayList<ITabularFormula> ctf)
    {
        GenericArrayList<ICompactTripletsStructure> cts = new GenericArrayList<ICompactTripletsStructure>(ctf.size());

        for (int i = 0; i < ctf.size(); i++)
        {
            ITabularFormula f = ctf.get(i);
            
            if (Helper.EnableAssertions)
            {
                if (!f.tiersSorted())
                {
                    throw new IllegalArgumentException("Tiers should be sorted");
                }
            }

            IPermutation targetPermutation = completePermutation(f.getPermutation(), formula.getPermutation());

            ICompactTripletsStructure completeCTS = createCompleteCTS(targetPermutation);

            completeCTS.subtract(f);

            cts.add(completeCTS);
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

        public String toString()
        {
            return "n=" + n + ", sign=" + sign + ", r=" + r + ", a=" + a + ", b=" + b + ", c=" + c;
        }
        
        private ITabularFormula formula = new SimpleFormula();

        public ITabularFormula readFormula(BufferedReader reader) throws IOException
        {
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

    public static void unify(GenericArrayList<ICompactTripletsStructure> cts) throws EmptyStructureException
    {
        if (cts.size() < 2)
        {
            throw new IllegalArgumentException("Unification is a q-ary operation where q should be > 1");
        }

        OpenLongObjectHashMap index = buildVarNamePairsIndex(cts);
        unify(index, cts);
    }
    
    private static void unify(OpenLongObjectHashMap index, GenericArrayList<ICompactTripletsStructure> cts) throws EmptyStructureException
    {
        boolean someClausesRemoved = false;

        int varCount = cts.get(0).getPermutation().size();
        int ctsCount = cts.size();
        
        Object[] ctsElements = cts.elements();
        
        for (int varName = 1; varName <= varCount; varName++)
        {
            for (int i = 0; i < ctsCount; i++)
            {
                ICompactTripletsStructure s = (ICompactTripletsStructure) ctsElements[i];
                if (s.isEmpty())
                {
                    throw new EmptyStructureException(s);
                }
                Value value = s.valueOf(varName);
                if (value != Value.Mixed)
                {
                    //  Concretize all other CTS with (varName -> value)
                    for (int j = 0; j < ctsCount; j++)
                    {
                        if (i == j) continue;
                        
                        ICompactTripletsStructure sj = (ICompactTripletsStructure) ctsElements[j];
                        someClausesRemoved |= sj.concretize(varName, value);
                        
                        if (sj.isEmpty())
                        {
                            throw new EmptyStructureException(sj);
                        }
                    }
                }
            }
        }
        
        index.forEachPair(new LongObjectProcedure()
        {
            @SuppressWarnings("unchecked")
            public boolean apply(long key, Object value)
            {
                //  See Helper#addTier() for details of key construction
                int varName1 = (int) (key >> 21);
                int varName2 = (int) (key & 0x1FFFFF);
                
                GenericArrayList<ITier> tiers = (GenericArrayList<ITier>) value;
                Object[] tiersElements = tiers.elements();
                int tierCount = tiers.size();
                
                int[] abci = new int[3];
                int[] abcj = new int[3];
                
                for (int i = 0; i < tierCount - 1; i++)
                {
                    ITier ti = (ITier) tiersElements[i];
                    
                    //  Remember tiers permutations
                    System.arraycopy(ti.getABC(), 0, abci, 0, 3);
                    
                    for (int j = i + 1; j < tierCount; j++)
                    {
                        ITier tj = (ITier) tiersElements[j];
                        
                        if (ti.getFormula() == tj.getFormula())
                        {
                            continue;
                        }
                        
                        //  Remember tiers permutations
                        System.arraycopy(tj.getABC(), 0, abcj, 0, 3);

                        //  Transpose tiers for adjoin
                        int a = getCanonicalVarName3(varName1, varName2, ti.getCanonicalName());
                        int c = getCanonicalVarName3(varName1, varName2, tj.getCanonicalName());

                        ti.transposeTo(a, varName1, varName2);
                        tj.transposeTo(varName1, varName2, c);
                        
                        //  Ensure values of varName1 and varName2 are the same in both tiers
                        ti.adjoinRight(tj);
                        tj.adjoinLeft(ti);
                        
                        //  Return tiers permutations back
                        tj.transposeTo(abcj);
                    }
                    //  Return tiers permutations back
                    ti.transposeTo(abci);
                }
                return true;
            }
        });
        
        for (int i = 0; i < ctsCount; i++)
        {
            ICompactTripletsStructure s = (ICompactTripletsStructure) ctsElements[i];
            someClausesRemoved |= s.cleanup();

            if (s.isEmpty())
            {
                throw new EmptyStructureException(s);
            }
        }
        
        if (someClausesRemoved)
        {
            unify(index, cts);
        }
    }

    
    /**
     * @param varName1
     * @param varName2
     * @param canonicalName array of {a, b, c}, where a < b < c
     * @return
     * 
     * @author Viacheslav Rudyuk <viacheslav.rudyuk@gmail.com>
     */
    public static int getCanonicalVarName3(int varName1, int varName2, int[] canonicalName)
    {
        int varName3;
        if (varName1 == canonicalName[1])
        {
            if (varName2 == canonicalName[2])
            {
                varName3 = canonicalName[0];
            } else
            {
                varName3 = canonicalName[2];
            }
        }
        else
        {
            if (varName2 == canonicalName[1])
            {
                if (varName1 > varName2)
                {
                    varName3 = canonicalName[0];
                }
                else
                {
                    varName3 = canonicalName[2];
                }
            }
            else
            {
                varName3 = canonicalName[1];
            }
        }
        return varName3;
    }
    
    private static OpenLongObjectHashMap buildVarNamePairsIndex(GenericArrayList<ICompactTripletsStructure> cts)
    {
        int varCount = cts.get(0).getPermutation().size();
        int tierCount = varCount - 2;
        int ctsCount = cts.size();
        
        int initialCapacity = (2 * varCount + 1) * ctsCount * 2;
        
        OpenLongObjectHashMap result = new OpenLongObjectHashMap(initialCapacity);
        
        for(int i = 0; i < ctsCount; i++)
        {
            ICompactTripletsStructure s = cts.get(i);
            for (int j = 0; j < tierCount; j++)
            {
                ITier tier = s.getTiers().get(j);
                
                ((SimpleTier)tier).setFormula(s);
                
                addTier(result, tier.getAName(), tier.getBName(), tier);
                addTier(result, tier.getAName(), tier.getCName(), tier);
                addTier(result, tier.getBName(), tier.getCName(), tier);
            }
        }
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void addTier(OpenLongObjectHashMap hash, int varName1, int varName2, ITier tier)
    {
        long key = varName1 < varName2 ? varName1 << 21 | varName2 : varName2 << 21 | varName1;

        GenericArrayList<ITier> tiers = (GenericArrayList<ITier>) hash.get(key);
        
        if (tiers == null)
        {
            hash.put(key, new GenericArrayList<ITier>(new ITier[] {tier}));
        }
        else
        {
            tiers.add(tier);
        }
    }

    public static void saveCTS(String filenamePrefix, GenericArrayList<ITabularFormula> cts) throws IOException
    {
        System.out.println("Saving CTS to file system...");
        
        for (int i = 0; i < cts.size(); i++)
        {
            ITabularFormula f = cts.get(i);
            String filename = filenamePrefix + "-cts-" + i + ".cnf";
            System.out.print("Saving " + filename + "...");
            Helper.saveToDIMACSFileFormat(f, filename);
            System.out.println(" done");
        }
    }

    public static void printFormulas(GenericArrayList<?> formulas)
    {
        for (int i = 0; i < formulas.size(); i++)
        {
            Helper.prettyPrint((ITabularFormula) formulas.get(i));
        }
    }
}
