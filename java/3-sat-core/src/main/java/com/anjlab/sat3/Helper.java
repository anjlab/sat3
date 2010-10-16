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
import java.util.Arrays;
import java.util.Random;

import cern.colt.function.LongObjectProcedure;
import cern.colt.map.OpenLongObjectHashMap;

public class Helper
{
    public static boolean UsePrettyPrint = false;

    public static boolean EnableAssertions = false;
    
    public static boolean UseUniversalVarNames = false;

    public static GenericArrayList<ITabularFormula> createCTF(ITabularFormula formula)
    {
        GenericArrayList<ITabularFormula> ctf = new GenericArrayList<ITabularFormula>();

        GenericArrayList<ITier> tiers = formula.getTiers();

        ITabularFormula f = new SimpleFormula();
        f.unionOrAdd(tiers.get(0));
        ctf.add(f);

        for (int i = 1; i < tiers.size(); i++)
        {
            ITier tier = tiers.get(i);
            //  Search possible CTFs to which the tier may join

            if (!joinTier(ctf, tier))
            {
                f = new SimpleFormula();
                f.unionOrAdd(tier);
                ctf.add(f);
            }
        }

        return ctf;
    }

    /**
     * @return True if tier was joined to some <code>ctf</code>
     */
    private static boolean joinTier(GenericArrayList<ITabularFormula> ctf, ITier tier)
    {
        IJoinMethod[] methods = JoinMethods.getMethods();

        int ctfCount = ctf.size();
        Object[] ctfElements = ctf.elements();
        
        for (int j = 0; j < ctfCount; j++)
        {
            ITabularFormula f = (ITabularFormula)ctfElements[j];
            for (int i = 0; i < methods.length; i++)
            {
                IJoinMethod method = methods[i];
                
                if (method.tryJoin(f, tier))
                {
                    return true;
                }
            }
        }
        return false;
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
        for (int i = 0; i < clausesCount && formula.getPermutation().size() < varCount; i++)
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

        boolean smallFormula = false; //    formula.getVarCount() < 100;
        if (UsePrettyPrint || smallFormula)
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
    
    public static ITabularFormula loadFromGenericDIMACSFileFormat(String filename) throws IOException
    {
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filename)), "ascii"));
            
            return new GenericFormulaReader().readFormula(reader);
        }
        finally
        {
            if (reader != null)
            {
                reader.close();
            }
        }
    }

    public static ITabularFormula createFormula(int... values)
    {
        if (values.length%3 != 0)
        {
            throw new IllegalArgumentException("Number of values must be a multiple of 3");
        }
        ITabularFormula formula = new SimpleFormula();
        for (int i = 0; i < values.length; i +=3)
        {
            SimpleTriplet triplet = new SimpleTriplet(values[i], values[i + 1], values[i + 2]);
            formula.unionOrAdd(triplet);
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

    public static void unify(GenericArrayList<ITabularFormula> cts) throws EmptyStructureException
    {
        if (cts.size() < 2)
        {
            throw new IllegalArgumentException("Unification is a q-ary operation where q should be > 1");
        }

        OpenLongObjectHashMap index = buildVarPairsIndex(cts);
        
        unify(index, cts);
    }
    
    private static void unify(OpenLongObjectHashMap index, GenericArrayList<ITabularFormula> cts) throws EmptyStructureException
    {
        boolean someClausesRemoved = false;

        System.out.println("Running unify routine...");
        
        int varCount = cts.get(0).getPermutation().size();
        int ctsCount = cts.size();
        
        Object[] ctsElements = cts.elements();
        
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

        for (int varName = 1; varName <= varCount; varName++)
        {
            for (int i = 0; i < ctsCount; i++)
            {
                ICompactTripletsStructure s = (ICompactTripletsStructure) ctsElements[i];
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
    
    private static OpenLongObjectHashMap buildVarPairsIndex(GenericArrayList<ITabularFormula> cts) throws EmptyStructureException
    {
        System.out.println("Building pairs index...");
        
        int varCount = cts.get(0).getPermutation().size();
        int tierCount = varCount - 2;
        int ctsCount = cts.size();
        
        final OpenLongObjectHashMap result = new OpenLongObjectHashMap();
        
        for(int i = 0; i < ctsCount; i++)
        {
            ITabularFormula s = cts.get(i);
            Object[] tierElements = s.getTiers().elements();
            
            for (int j = 0; j < tierCount; j++)
            {
                ITier tier = (ITier) tierElements[j];
                
                ((SimpleTier)tier).setFormula(s);
                
                addTier(result, tier.getAName(), tier.getBName(), tier);
                addTier(result, tier.getAName(), tier.getCName(), tier);
                addTier(result, tier.getBName(), tier.getCName(), tier);
            }
        }
        
        result.forEachPair(new LongObjectProcedure()
        {
            public boolean apply(long key, Object value)
            {
                if (((GenericArrayList<?>)value).size() < 2)
                {
                    result.removeKey(key);
                }
                return true;
            }
        });
        
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void addTier(OpenLongObjectHashMap hash, int varName1, int varName2, ITier tier)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;

        if (EnableAssertions)
        {
            int varName1_ = (int) (key >> 21);
            int varName2_ = (int) (key & 0x1FFFFF);
    
            if ((varName1 != varName1_) && (varName1 != varName2_)
                    || (varName2 != varName1_) && (varName2 != varName2_))
            {
                throw new RuntimeException("Bad hash");
            }
        }

        GenericArrayList<ITier> tiers = (GenericArrayList<ITier>) hash.get(key);
        
        if (tiers == null)
        {
            hash.put(key, new GenericArrayList<ITier>(new ITier[] {tier}));
        }
        else
        {
            if (!tier.hasVariable(varName1) || !tier.hasVariable(varName2))
            {
                throw new IllegalStateException();
            }
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

    public static void printBits(byte keys)
    {
        int mask = 0x80;
        while (mask > 0)
        {
            if ((keys & mask) == mask)
            {
                System.out.print("1");
            }
            else
            {
                System.out.print("0");
            }
            mask >>= 1;
        }
        System.out.println();
    }

    public static void debugPrettyPrintToFile(ITabularFormula formula)
    {
        try
        {
            UsePrettyPrint = true;
            StringBuilder builder = buildPrettyOutput(formula);
            FileOutputStream fos = new FileOutputStream(new File("debug.txt"));
            fos.write(builder.toString().getBytes());
            fos.close();
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void createCTS(ITabularFormula formula, GenericArrayList<ITabularFormula> ctf)
            throws EmptyStructureException
    {
        for (int i = 0; i < ctf.size(); i++)
        {
            ctf.get(i).complete(formula.getPermutation());
        }
    }
}
