package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static java.text.MessageFormat.format;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.function.IntObjectProcedure;
import cern.colt.function.LongObjectProcedure;
import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;
import cern.colt.map.OpenLongObjectHashMap;

public class Helper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);
    
    public static boolean UsePrettyPrint = false;

    public static boolean EnableAssertions = false;
    
    public static boolean UseUniversalVarNames = false;

    /**
     * 
     * @param formula
     * @return List of ITabularFormula
     */
    public static ObjectArrayList createCTF(ITabularFormula formula)
    {
        ObjectArrayList ctf = new ObjectArrayList();

        ObjectArrayList tiers = formula.getTiers();

        ITabularFormula f = new SimpleFormula();
        f.unionOrAdd(formula.getTier(0));
        ctf.add(f);

        for (int i = 1; i < tiers.size(); i++)
        {
            ITier tier = (ITier) tiers.get(i);
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
     * @param ctf List of ITabularFormula
     * @return True if tier was joined to some <code>ctf</code>
     */
    private static boolean joinTier(ObjectArrayList ctf, ITier tier)
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

        LOGGER.info(builder.toString());
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
                ObjectArrayList tiers = formula.getTiers();
                for (int j = 0; j < tiers.size(); j++)
                {
                    ITier tier = (ITier) tiers.get(j);
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
            ObjectArrayList tiers = formula.getTiers();
            for (int i = 0; i < tiers.size(); i++)
            {
                ITier tier = (ITier) tiers.get(i);
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
        return 8*varCount*(varCount - 1)*(varCount - 2)*(varCount - 3 + 1)/6;
    }

    public static void printLine(char c, int length) {
        String string = getString(c, length);
        LOGGER.info(string);
    }

    private static String getString(char c, int length)
    {
        char[] line = new char[length];
        Arrays.fill(line, c);
        String string = new String(line);
        return string;
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
                        throw new AssertionError("Bad DIMACS CNF file format");
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

    /**
     * 
     * @param cts List of ICompactTripletsStructureHolder
     * @throws EmptyStructureException
     */
    public static void unify(ObjectArrayList cts) throws EmptyStructureException
    {
        if (cts.size() < 2)
        {
            throw new IllegalArgumentException("Unification is a q-ary operation where q should be > 1");
        }

        OpenLongObjectHashMap index = buildVarPairsIndex(cts);
        
        unify(index, cts);
    }

    /**
     * 
     * @param index
     * @param cts List of ICompactTripletsStructureHolder
     * @throws EmptyStructureException
     */
    private static void unify(OpenLongObjectHashMap index, ObjectArrayList cts) throws EmptyStructureException
    {
        boolean someClausesRemoved = false;

        LOGGER.debug("Running unify routine...");
        
        int varCount = ((ICompactTripletsStructureHolder) cts.get(0)).getCTS().getPermutation().size();
        int ctsCount = cts.size();
        
        Object[] ctsElements = cts.elements();
        
        index.forEachPair(new LongObjectProcedure()
        {
            public boolean apply(long key, Object value)
            {
                //  See Helper#addTier() for details of key construction
                int varName1 = (int) (key >> 21);
                int varName2 = (int) (key & 0x1FFFFF);
                
                //  List of ITier
                ObjectArrayList tiers = (ObjectArrayList) value;
                Object[] tiersElements = tiers.elements();
                int tierCount = tiers.size();
                
                int[] abci = new int[3];
                int[] abcj = new int[3];
                
                for (int i = 0; i < tierCount - 1; i++)
                {
                    ITier ti = (ITier) tiersElements[i];
                    
                    //  Remember tier permutation
                    System.arraycopy(ti.getABC(), 0, abci, 0, 3);
                    
                    for (int j = i + 1; j < tierCount; j++)
                    {
                        ITier tj = (ITier) tiersElements[j];
                        
                        if (ti.getFormula() == tj.getFormula())
                        {
                            continue;
                        }
                        
                        //  Remember tier permutation
                        System.arraycopy(tj.getABC(), 0, abcj, 0, 3);
                        
                        //  Transpose tiers for adjoin
                        int a = getCanonicalVarName3(varName1, varName2, ti.getCanonicalName());
                        int c = getCanonicalVarName3(varName1, varName2, tj.getCanonicalName());
                        
                        ti.transposeTo(a, varName1, varName2);
                        tj.transposeTo(varName1, varName2, c);
                        
                        //  Ensure values of varName1 and varName2 are the same in both tiers
                        ti.adjoinRight(tj);
                        tj.adjoinLeft(ti);
                        
                        //  Return tier permutation back
                        tj.transposeTo(abcj);
                    }
                    //  Return tier permutation back
                    ti.transposeTo(abci);
                }
                return true;
            }
        });

        for (int i = 0; i < ctsCount; i++)
        {
            ICompactTripletsStructure s = ((ICompactTripletsStructureHolder) ctsElements[i]).getCTS();
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
                ICompactTripletsStructure s = ((ICompactTripletsStructureHolder) ctsElements[i]).getCTS();
                Value value = s.valueOf(varName);
                if (value != Value.Mixed)
                {
                    //  Concretize all other CTS with (varName -> value)
                    for (int j = 0; j < ctsCount; j++)
                    {
                        if (i == j) continue;
                        
                        ICompactTripletsStructure sj = ((ICompactTripletsStructureHolder) ctsElements[j]).getCTS();
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
            LOGGER.debug("Some clauses removed during unification");
            unify(index, cts);
        }
        else
        {
            LOGGER.debug("No clauses removed during unification");
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
    
    /**
     * 
     * @param cts List of ICompactTripletsStructureHolder
     * @return
     * @throws EmptyStructureException
     */
    private static OpenLongObjectHashMap buildVarPairsIndex(ObjectArrayList cts) throws EmptyStructureException
    {
        LOGGER.debug("Building pairs index...");
        
        int varCount = ((ICompactTripletsStructureHolder) cts.get(0)).getCTS().getPermutation().size();
        int tierCount = varCount - 2;
        int ctsCount = cts.size();
        
        final OpenLongObjectHashMap result = new OpenLongObjectHashMap();
        
        for(int i = 0; i < ctsCount; i++)
        {
            ITabularFormula s = ((ICompactTripletsStructureHolder) cts.get(i)).getCTS();
            if(s.isEmpty())
            {
                throw new EmptyStructureException(s);
            }
            
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
        
        final LongArrayList toBeRemoved = new LongArrayList();
        
        result.forEachPair(new LongObjectProcedure()
        {
            public boolean apply(long key, Object value)
            {
                //  List of ITier
                ObjectArrayList tiers = (ObjectArrayList) value;
                if (tiers.size() < 2)
                {
                    toBeRemoved.add(key);
                }
                else
                {
                    ITabularFormula formula = ((ITier)tiers.get(0)).getFormula();
                    for (int i = 1; i < tiers.size(); i++)
                    {
                        if (formula != ((ITier)tiers.get(i)).getFormula())
                        {
                            //  Found distinct formulas
                            return true;
                        }
                    }
                    //  All triplets are from the same formula
                    toBeRemoved.add(key);
                }
                return true;
            }
        });
        
        int size = toBeRemoved.size();
        for (int i = 0; i < size; i++)
        {
            result.removeKey(toBeRemoved.getQuick(i));
        }
        LOGGER.debug("Removed {} triplet permutations from index", size);
        
        return result;
    }

    private static void addTier(OpenLongObjectHashMap hash, int varName1, int varName2, ITier tier)
    {
        long key = varName1 < varName2 ? (long)varName1 << 21 | varName2 : (long)varName2 << 21 | varName1;

        if (EnableAssertions)
        {
            int varName1_ = (int) (key >> 21);
            int varName2_ = (int) (key & 0x1FFFFF);
    
            if (((varName1 != varName1_) && (varName1 != varName2_))
                    || ((varName2 != varName1_) && (varName2 != varName2_)))
            {
                throw new AssertionError("Bad hash");
            }
        }

        //  List of ITier
        ObjectArrayList tiers = (ObjectArrayList) hash.get(key);
        
        if (tiers == null)
        {
            hash.put(key, new ObjectArrayList(new ITier[] {tier}));
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

    /**
     * 
     * @param filenamePrefix
     * @param cts List of ITabularFormula
     * @throws IOException
     */
    public static void saveCTS(String filenamePrefix, ObjectArrayList cts) throws IOException
    {
        System.out.println("Saving CTS to file system...");
        
        for (int i = 0; i < cts.size(); i++)
        {
            ITabularFormula f = (ITabularFormula) cts.get(i);
            String filename = filenamePrefix + "-cts-" + i + ".cnf";
            System.out.print("Saving " + filename + "...");
            Helper.saveToDIMACSFileFormat(f, filename);
            System.out.println(" done");
        }
    }

    /**
     * @param formulas List of ITabularFormula
     */
    public static void printFormulas(ObjectArrayList formulas)
    {
        for (int i = 0; i < formulas.size(); i++)
        {
            Helper.prettyPrint((ITabularFormula) formulas.get(i));
        }
    }

    public static void printBits(byte keys)
    {
        StringBuilder builder = new StringBuilder(8);
        int mask = 0x80;
        while (mask > 0)
        {
            if ((keys & mask) == mask)
            {
                builder.append('1');
            }
            else
            {
                builder.append('0');
            }
            mask >>= 1;
        }
        LOGGER.info(builder.toString());
    }

    public static void debugPrettyPrintToFile(ITabularFormula formula)
    {
        boolean oldPrettyPrint = UsePrettyPrint;
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
        finally
        {
            UsePrettyPrint = oldPrettyPrint;
        }
    }

    /**
     * 
     * @param formula
     * @param ctf List of ITabularFormula
     * @throws EmptyStructureException
     */
    public static void createCTS(ITabularFormula formula, ObjectArrayList ctf)
            throws EmptyStructureException
    {
        for (int i = 0; i < ctf.size(); i++)
        {
            ((ITabularFormula) ctf.get(i)).complete(formula.getPermutation());
        }
    }
    
    /**
     * 
     * @param cts
     * @return List of IHyperStructure
     * @throws EmptyStructureException
     */
    public static ObjectArrayList createHyperStructuresSystem(ObjectArrayList cts) throws EmptyStructureException
    {
        final ObjectArrayList hss = new ObjectArrayList();
        
        ICompactTripletsStructure sBasic = chooseBasicStructure(cts);

        for (int i = 1; i < cts.size(); i++)
        {
            ICompactTripletsStructure otherCTS = (ICompactTripletsStructure) cts.get(i);
            if (otherCTS == sBasic)
            {
                continue;
            }
            IHyperStructure hs = new SimpleHyperStructure(sBasic, otherCTS);
            hs.build();
            
            hss.add(hs);
            
            if (i > 1 && hs.isBasicGraphChangedDuringBuild())
            {
                //  All previously created HS need to be recalculated
                return createHyperStructuresSystem(cts);
            }
        }

        boolean dirty = false;
        
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        for (int j = 0; j < basicGraph.getTiers().size(); j++)
        {
            OpenIntObjectHashMap tier = (OpenIntObjectHashMap) basicGraph.getTiers().get(j);
            for (int v = 0; v < tier.size(); v++)
            {
                ObjectArrayList vertices = new ObjectArrayList();
                for (int h = 0; h < hss.size(); h++)
                {
                    IHyperStructure hs = (IHyperStructure) hss.get(h);
                    OpenIntObjectHashMap hsTier = (OpenIntObjectHashMap) hs.getTiers().get(j);
                    vertices.add(hsTier.values().get(v));
                }
                try
                {
                    unify(vertices);
                } 
                catch (EmptyStructureException e)
                {
                    ICompactTripletsStructure basicCTS = basicGraph.getBasicCTS();
                    
                    ITier basicTier = ((ITier) basicCTS.getTiers().get(j));
                    
                    basicTier.remove(((IVertex) vertices.get(0)).getTripletValue());
                    
                    basicCTS.cleanup(j, j);
                    
                    dirty = true;
                    
                    break;
                }
            }
            if (dirty)
            {
                break;
            }
        }

        if (dirty)
        {
            return createHyperStructuresSystem(cts);
        }

        return hss;
    }

    /**
     * Choose CTS with minimum number of clauses as a basic structure
     * @param cts List of ICompactTripletsStructure 
     * @return
     */
    private static ICompactTripletsStructure chooseBasicStructure(ObjectArrayList cts)
    {
        ICompactTripletsStructure sBasic = (ICompactTripletsStructure) cts.get(0);
        for (int i = 1; i < cts.size(); i++)
        {
            ICompactTripletsStructure s = (ICompactTripletsStructure) cts.get(i);
            if (sBasic.getClausesCount() > s.getClausesCount())
            {
                sBasic = s;
            }
        }
        return sBasic;
    }
    
    /**
     * 
     * @param hs
     * @param route List of {@link IVertex}. Optional route to highlight on the image.
     * @param filename
     * @throws IOException
     */
    public static void writeToImage(IHyperStructure hs, final ObjectArrayList route, String filename) throws IOException
    {
        int fontSize = 12;
        
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D metrics = (Graphics2D) image.getGraphics();

        Font font = new Font("Courier New", Font.PLAIN, fontSize);
        
        metrics.setFont(font);

        final int widthOfZeroChar = metrics.getFontMetrics().stringWidth("0");
        final int heightOfZeroChar = metrics.getFontMetrics().getHeight();
        final int widthBetweenChars = 2;
        final int widthBetweenTriplets = 5;
        final int heightBetweenTriplets = 30;
        final int offsetTop = 10;
        final int offsetBottom = 10;
        final int offsetLeft = 10;
        final int offsetRight = 10;
        final int varCount = hs.getBasicCTS().getVarCount();
        final int widthOfTierHeader = ("(,,) j=".length() 
                                       + 3 * getLegendName(varCount).length() 
                                       + String.valueOf(varCount - 2).length()) * widthOfZeroChar;
        
        //  List of ITier
        ObjectArrayList ctsTiers = hs.getBasicCTS().getTiers();
        final int tiersCount = ctsTiers.size();
        
        final int widthOfValuesArea = 8 * (widthOfZeroChar * 3 + widthBetweenChars * 2) + 8 * widthBetweenTriplets + offsetLeft;
        
        image = new BufferedImage(widthOfValuesArea + widthOfTierHeader + offsetRight, 
                                  heightOfZeroChar * tiersCount + heightBetweenTriplets * (tiersCount - 1) + offsetTop + offsetBottom, 
                                  BufferedImage.TYPE_3BYTE_BGR);
        
        final Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
        
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        
        //  List of OpenIntObjectHashMap
        ObjectArrayList tiers = hs.getTiers();
        for (int i = 0; i < tiers.size(); i++)
        {
            final OpenIntObjectHashMap tier = (OpenIntObjectHashMap) tiers.get(i);
            ITier ctsTier =  (ITier) ctsTiers.get(i);

            final int tierIndex = i;
            
            final int y = offsetTop + heightOfZeroChar + tierIndex * heightOfZeroChar + tierIndex * heightBetweenTriplets;

            graphics.drawString((format("({0},{1},{2}) j={3}", 
                                    getLegendName(ctsTier.getAName()), 
                                    getLegendName(ctsTier.getBName()),
                                    getLegendName(ctsTier.getCName()),
                                    tierIndex + 1)), 
                                widthOfValuesArea, y);

            tier.forEachPair(new IntObjectProcedure()
            {
                public boolean apply(int tierKey, Object value)
                {
                    IVertex vertex = (IVertex) value;
                    
                    int tripletOffset = getTripletOffset(vertex, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);
                    
                    Color color = graphics.getColor();
                    
                    if (vertex.hasEmptyEdge())
                    {
                        graphics.setColor(Color.GREEN);
                    }

                    if (route != null)
                    {
                        IVertex routeVertex = (IVertex) route.get(tierIndex);
                        if (routeVertex != null
                                && routeVertex.getTripletValue() == vertex.getTripletValue()
                                && routeVertex.getPermutation().hasSameVariablesAs(vertex.getPermutation()))
                        {
                            //  Highlight triplet value
                            graphics.setColor(Color.RED);
                            
                            if (vertex.hasEmptyEdge())
                            {
                                graphics.setColor(Color.BLUE);
                            }
                        }
                    }
                    
                    graphics.drawString(vertex.getTripletValue().toString(), tripletOffset, y);
                    
                    graphics.setColor(color);
                    
                    if (vertex.getBottomVertex1() != null) drawLine(route, vertex, vertex.getBottomVertex1(), graphics, offsetTop, heightOfZeroChar, heightBetweenTriplets, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);
                    if (vertex.getBottomVertex2() != null) drawLine(route, vertex, vertex.getBottomVertex2(), graphics, offsetTop, heightOfZeroChar, heightBetweenTriplets, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);

                    return true;
                }
            });
        }
        
        ImageIO.write(image, "png", new File(filename));
    }

    private static void drawLine(ObjectArrayList route, IVertex source, IVertex target, Graphics2D graphics, int offsetTop, int heightOfZeroChar, int heightBetweenTriplets, int offsetLeft, int widthBetweenTriplets, int widthBetweenChars, int widthOfZeroChar)
    {
        int sourceTierIndex = source.getTierIndex();
        int x1 = getTripletOffset(source, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);
        int y1 = offsetTop + heightOfZeroChar + sourceTierIndex * heightOfZeroChar + sourceTierIndex * heightBetweenTriplets;

        int targetTierIndex = target.getTierIndex();
        int x2 = getTripletOffset(target, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);
        int y2 = offsetTop + heightOfZeroChar + targetTierIndex * heightOfZeroChar + targetTierIndex * heightBetweenTriplets;

        int offsetFromTier = 2;
        
        Color color = graphics.getColor();
        if (route != null)
        {
            IVertex routeVertex = (IVertex) route.get(source.getTierIndex());
            IVertex routeVertex2 = (IVertex) route.get(target.getTierIndex());
            if (routeVertex != null && routeVertex2 != null
                    && routeVertex.getTripletValue() == source.getTripletValue()
                    && routeVertex.getPermutation().hasSameVariablesAs(source.getPermutation())
                    && routeVertex2.getTripletValue() == target.getTripletValue()
                    && routeVertex2.getPermutation().hasSameVariablesAs(target.getPermutation()))
            {
                //  Highlight edge
                graphics.setColor(Color.RED);
            }
        }
        
        graphics.drawLine(x1 + 3 * widthOfZeroChar / 2, y1 + offsetFromTier, x2 + 3 * widthOfZeroChar / 2, y2 - heightOfZeroChar - offsetFromTier);
        
        graphics.setColor(color);
    }

    private static int getTripletOffset(IVertex vertex, int offsetLeft,
            int widthBetweenTriplets, int widthBetweenChars, int widthOfZeroChar)
    {
        int tripletOffset = offsetLeft;
        int tripletDeltaWidth = widthBetweenChars * 2 + widthOfZeroChar * 3 + widthBetweenTriplets;
        
        if (vertex.getTripletValue() == _000_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _001_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _010_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _011_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _100_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _101_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
        if (vertex.getTripletValue() == _110_instance) return tripletOffset;
        tripletOffset += tripletDeltaWidth;
//        if (vertex.getTripletValue() == _111_instance) 
            return tripletOffset;
    }
    
    /**
     * 
     * @param hs
     * @return List of {@link IVertex} that form non-empty (n-2)-intersection
     */
    public static ObjectArrayList findHyperStructureRoute(IHyperStructure hs)
    {
        int tiersCount = hs.getTiers().size();
        
        ObjectArrayList result = new ObjectArrayList(tiersCount);
        
        OpenIntObjectHashMap lastTier = (OpenIntObjectHashMap) hs.getTiers().get(tiersCount - 1);
        IVertex vertexFromTheLastTier = (IVertex) lastTier.values().get(0);
        
        ICompactTripletsStructure substructure = vertexFromTheLastTier.getCTS();
        
        for (int j = 0; j < tiersCount - 1; j++)
        {
            OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) hs.getTiers().get(j);
            boolean added = false;
            for (int i = 0; i < tierVertices.size(); i++)
            {
                ICompactTripletsStructure clone = (ICompactTripletsStructure) substructure.clone();
                
                IVertex tierVertex = (IVertex) tierVertices.values().get(i);
                
                clone.intersect(tierVertex.getCTS());
                
                if (!clone.isEmpty())
                {
                    result.add(tierVertex);
                    
                    substructure = clone;
                    
                    added = true;
                    
                    break;
                }
            }
            if (!added)
            {
                //  Note: For debug purposes only. 
                result.add(null);
                
                System.err.println("Implementation error: " + (j+1) + " tier was built with errors. Please provide CNF file to developers.");
            }
        }

        result.add(vertexFromTheLastTier);

        if (result.size() != hs.getTiers().size())
        {
            //  This shouldn't happen if the implementation is correct
            throw new AssertionError("Valid hyperstructure must contain at least one non-empty (n-2)-intersection");
        }
        
        return result;
    }

    /**
     * 
     * @param route List of {@link IVertex}
     * @return
     */
    public static ICompactTripletsStructure intersectAll(ObjectArrayList route)
    {
        ICompactTripletsStructure result = (ICompactTripletsStructure) ((IVertex)route.get(0)).getCTS().clone();
        for (int i = 1; i < route.size(); i++)
        {
            IVertex vertex = (IVertex) route.get(i);
            result.intersect(vertex.getCTS());
        }
        return result;
    }
}
