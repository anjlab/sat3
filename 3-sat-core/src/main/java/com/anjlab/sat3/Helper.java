/*
 * Copyright (c) 2010, 2011 AnjLab
 * 
 * This file is part of 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem 
 * is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem
 * is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with
 * Reference Implementation of Romanov's Polynomial Algorithm for 3-SAT Problem.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.anjlab.sat3;

import static com.anjlab.sat3.SimpleTripletValueFactory._000_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._001_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._010_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._011_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._100_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._101_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._110_instance;
import static com.anjlab.sat3.SimpleTripletValueFactory._111_instance;
import static java.text.MessageFormat.format;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.function.IntObjectProcedure;
import cern.colt.function.LongObjectProcedure;
import cern.colt.list.IntArrayList;
import cern.colt.list.LongArrayList;
import cern.colt.list.ObjectArrayList;
import cern.colt.map.OpenIntObjectHashMap;

import com.anjlab.sat3.VarPairsIndexFactory.VarPairsIndex;

public class Helper
{
    public static final String IMPLEMENTATION_VERSION = "ImplementationVersion";
    public static final String INITIAL_FORMULA_LOAD_TIME = "InitialFormulaLoadTime";
    public static final String INITIAL_FORMULA_VAR_COUNT = "InitialFormulaVarCount";
    public static final String INITIAL_FORMULA_CLAUSES_COUNT = "InitialFormulaClausesCount";
    public static final String CTF_CREATION_TIME = "CTFCreationTime";
    public static final String CTF_COUNT = "CTFCount";
    public static final String CTS_CREATION_TIME = "CTSCreationTime";
    public static final String CTS_UNIFICATION_TIME = "CTSUnificationTime";
    public static final String BASIC_CTS_INITIAL_CLAUSES_COUNT = "BasicCTSInitialClausesCount";
    public static final String HSS_CREATION_TIME = "HSSCreationTime";
    public static final String NUMBER_OF_HSS_TIERS_BUILT = "NumberOfHSSTiersBuilt";
    public static final String BASIC_CTS_FINAL_CLAUSES_COUNT = "BasicCTSFinalClausesCount";
    public static final String SEARCH_HSS_ROUTE_TIME = "SearchHSSRouteTime";

    private static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);
    
    public static boolean UsePrettyPrint = false;

    public static boolean EnableAssertions = true;
    
    public static boolean UseUniversalVarNames = true;

    /**
     * 
     * @param formula
     * @return List of {@link ITabularFormula}
     */
    public static ObjectArrayList createCTF(ITabularFormula formula)
    {
        ObjectArrayList ctf = new ObjectArrayList();

        ObjectArrayList tiers = formula.getTiers();

        ITabularFormula f = new SimpleFormula();
        f.unionOrAdd(formula.getTier(0).clone());
        ctf.add(f);

        for (int i = 1; i < tiers.size(); i++)
        {
            ITier tier = ((ITier) tiers.get(i)).clone();
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
        builder.insert(0, '\n');

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
    
    public static ITabularFormula loadFromFile(String filename) throws IOException
    {
        String fileExt = filename.substring(filename.lastIndexOf('.'), filename.length());
        
        IFormulaReader formulaReader;
        if (".skt".equals(fileExt))
        {
            formulaReader = new RomanovSKTFormulaReader();
        }
        else
        {
            formulaReader = new GenericFormulaReader();
        }
        
        FileInputStream is = null;
        try
        {
            is = new FileInputStream(new File(filename));
            
            ITabularFormula formula = formulaReader.readFormula(is);
            
            //  Clear SimpleFormula's tierHash3 after formula loaded
            
            ((SimpleFormula) formula).clearTierHash3();
            
            return formula;
        }
        finally
        {
            if (is != null)
            {
                is.close();
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

        VarPairsIndex index = VarPairsIndexFactory.getInstance().buildIndex(cts);
        
        unify(index, ((ICompactTripletsStructureHolder) cts.get(0)).getCTS().getPermutation(), cts, 1);
    }

    private static class UnificationContext
    {
        public VarPairsIndex partialIndex;
        //  Debug information
        public int numberOfClausesRemoved = 0;
    }
    
    /**
     * 
     * @param index
     * @param varNames
     * @param cts List of {@link ICompactTripletsStructureHolder}
     * @throws EmptyStructureException
     */
    private static void unify(VarPairsIndex index, IPermutation varNames, final ObjectArrayList cts, int recursionLevel) throws EmptyStructureException
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Running unify routine ({} level)...", recursionLevel);
            LOGGER.debug("# of pairs in index: {}; # of varNames in index: {}", index.pairs().size(), varNames.size());
        }
        
        final UnificationContext unificationContext = new UnificationContext();
        final IPermutation partialPermutation = new SimplePermutation();
        
        final int[] abci = new int[3];
        final int[] abcj = new int[3];
        
        index.forEachPair(new LongObjectProcedure()
        {
            public boolean apply(long key, Object value)
            {
                //  See VarPairsIndex#addTier() for details of key construction
                int varName1 = (int) (key >> 21);
                int varName2 = (int) (key & 0x1FFFFF);
                
                //  List of formulaAndTierIndices
                LongArrayList tiers = (LongArrayList) value;
                long[] tiersElements = tiers.elements();
                int tierCount = tiers.size();
                
                for (int i = 0; i < tierCount - 1; i++)
                {
                    long formulaAndTierIndex = tiersElements[i];
                    int formulaIndex = (int)(formulaAndTierIndex >> 32);
                    int tierIndex = (int)(formulaAndTierIndex & 0x00000000FFFFFFFFL);
                    
                    ICompactTripletsStructure si = ((ICompactTripletsStructureHolder) cts.getQuick(formulaIndex)).getCTS();
                    ITier ti = si.getTier(tierIndex);
                    
                    //  Remember tier permutation
                    System.arraycopy(ti.getABC(), 0, abci, 0, 3);
                    
                    int tiSize = ti.size();
                    
                    for (int j = i + 1; j < tierCount; j++)
                    {
                        long formulaAndTierIndex2 = tiersElements[j];
                        int formulaIndex2 = (int)(formulaAndTierIndex2 >> 32);
                        int tierIndex2 = (int)(formulaAndTierIndex2 & 0x00000000FFFFFFFFL);
                        
                        if (formulaIndex == formulaIndex2)
                        {
                            continue;
                        }
                        
                        ICompactTripletsStructure sj = ((ICompactTripletsStructureHolder) cts.getQuick(formulaIndex2)).getCTS();
                        ITier tj = sj.getTier(tierIndex2);
                        
                        //  Remember tier permutation
                        System.arraycopy(tj.getABC(), 0, abcj, 0, 3);
                        
                        //  Transpose tiers for adjoin
                        int a = getCanonicalVarName3(varName1, varName2, ti.getCanonicalName());
                        int c = getCanonicalVarName3(varName1, varName2, tj.getCanonicalName());
                        
                        ti.transposeTo(a, varName1, varName2);
                        tj.transposeTo(varName1, varName2, c);
                        
                        int tjSize = tj.size();
                        
                        //  Ensure values of varName1 and varName2 are the same in both tiers
                        ti.adjoinRight(tj);
                        tj.adjoinLeft(ti);
                        
                        int tjSizeNew = tj.size();
                        
                        //  Return tier permutation back
                        tj.transposeTo(abcj);
                        
                        //  Cleanup sj
                        if (tjSize != tjSizeNew)
                        {
                            CleanupStatus status = sj.cleanup(tierIndex2, tierIndex2);
                            if (status.someClausesRemoved)
                            {
                                updateIndicesAfterCleanupDuringUnification(status, sj, cts, unificationContext, partialPermutation);
                            }
                        }
                    }
                    //  Return tier permutation back
                    ti.transposeTo(abci);
                    
                    int tiSizeNew = ti.size();
                    
                    //  Cleanup si
                    if (tiSize != tiSizeNew)
                    {
                        CleanupStatus status = si.cleanup(tierIndex, tierIndex);
                        if (status.someClausesRemoved)
                        {
                            updateIndicesAfterCleanupDuringUnification(status, si, cts, unificationContext, partialPermutation);
                        }
                    }
                }
                return true;
            }
        });

//        for (int i = 0; i < ctsCount; i++)
//        {
//            ICompactTripletsStructure s = ((ICompactTripletsStructureHolder) ctsElements[i]).getCTS();
//            someClausesRemoved |= s.cleanup();
//
//            if (s.isEmpty())
//            {
//                throw new EmptyStructureException(s);
//            }
//        }

        int varCount = varNames.size();
        int[] varNamesElements = varNames.elements();
        int ctsCount = cts.size();
        Object[] ctsElements = cts.elements();
        
        for (int v = 0; v < varCount; v++)
        {
            int varName = varNamesElements[v];
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
                        
                        CleanupStatus status = sj.concretize(varName, value);
                        
                        if (status.someClausesRemoved)
                        {
                            updateIndicesAfterCleanupDuringUnification(status, sj, cts, unificationContext, partialPermutation);
                        }
                    }
                }
            }
        }
        
        LOGGER.debug("{} clauses removed during this recursion level", unificationContext.numberOfClausesRemoved);
        if (unificationContext.partialIndex != null || partialPermutation.size() > 0)
        {
            unify(unificationContext.partialIndex, partialPermutation, cts, recursionLevel + 1);
        }
        else
        {
            LOGGER.debug("Unification completed on recursion level {}", recursionLevel);
        }
    }
    
    private static void updateIndicesAfterCleanupDuringUnification(
            CleanupStatus status,
            ICompactTripletsStructure formula, 
            final ObjectArrayList cts,
            final UnificationContext unificationContext,
            final IPermutation partialPermutation)
    {
        if (formula.isEmpty())
        {
            throw new EmptyStructureException(formula);
        }
        unificationContext.numberOfClausesRemoved += status.numberOfClausesRemoved;
        if (unificationContext.partialIndex == null)
        {
            unificationContext.partialIndex = VarPairsIndexFactory.getInstance().
                                    buildPartialIndex(cts, (ICompactTripletsStructureHolder) formula, status.from, status.to);
        }
        else
        {
            unificationContext.partialIndex.rebuildIndex(cts, (ICompactTripletsStructureHolder) formula, status.from, status.to);
        }
        for (int t = status.from; t <= status.to; t++)
        {
            ITier tier = formula.getTier(t);
            partialPermutation.put(tier.getABC());
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
     * @param filenamePrefix
     * @param cts List of {@link ITabularFormula}
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
     * @param formulas List of {@link ITabularFormula}
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

    /**
     * 
     * @param ctf List of ITabularFormula
     * @param variables Complete permutation of initial formula.
     * @throws EmptyStructureException
     */
    public static void completeToCTS(ObjectArrayList ctf, IPermutation variables)
            throws EmptyStructureException
    {
        for (int i = 0; i < ctf.size(); i++)
        {
            ((ITabularFormula) ctf.get(i)).complete(variables);
        }
    }
    
    /**
     * 
     * @param cts
     * @param statistics 
     * @return List of {@link IHyperStructure}
     * @throws EmptyStructureException
     */
    public static ObjectArrayList createHyperStructuresSystem(ObjectArrayList cts, Properties statistics) throws EmptyStructureException
    {
        VarPairsIndexFactory.getInstance().clear();
        
        final ObjectArrayList hss = new ObjectArrayList();
        
        try
        {
            ICompactTripletsStructure sBasic = chooseBasicStructure(cts);
            
            statistics.put(BASIC_CTS_INITIAL_CLAUSES_COUNT, String.valueOf(sBasic.getClausesCount()));
            
            //  List of ITier
            ObjectArrayList basicTiers = sBasic.getTiers();
            
            IHyperStructure basicGraph = createFirstHSSTier(cts, hss, sBasic, basicTiers);
            
            StructuresForConcordantShift structuresForConcordantShift = new StructuresForConcordantShift(hss.size());
            
            for (int j = 1; j < basicTiers.size(); j++)
            {
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("Building HSS tier #{} of {}", j+1, basicTiers.size());
                }
             
                final int nextTierIndex = j;
                final int prevTierIndex = nextTierIndex - 1;
                final ITier basicPrevTier = (ITier) basicTiers.get(prevTierIndex);
                final ITier basicNextTier = (ITier) basicTiers.get(nextTierIndex);
    
                OpenIntObjectHashMap basicPrevTierVertices = (OpenIntObjectHashMap) basicGraph.getTiers().get(prevTierIndex);
                
                if (LOGGER.isDebugEnabled())
                {
                    LOGGER.debug("HSS   tier #{} is: {}", nextTierIndex, verticesTripletsToString(basicPrevTierVertices));
                    LOGGER.debug("Basic tier #{} is: {}", nextTierIndex, tripletsToString(basicPrevTier));
                    LOGGER.debug("Basic tier #{} is: {}", nextTierIndex + 1, tripletsToString(basicNextTier));
                }
    
                if (basicPrevTier.size() != basicPrevTierVertices.size())
                {
                    throw new AssertionError("BG and HSS should be isomorphic");
                }
                
                //  Shift each vertex of the tier along associated edges to the next tier
                
                IntArrayList keys = basicPrevTierVertices.keys();
                int keysSize = keys.size();
                for (int k = 0; k < keysSize; k++)
                {
                    int tierKeyOfTheVertexToShift = keys.get(k);
    
                    IVertex prevTierVertex = (IVertex) ((OpenIntObjectHashMap) basicGraph.getTiers().get(prevTierIndex)).get(tierKeyOfTheVertexToShift);
    
                    ITripletValue tripletValue = prevTierVertex.getTripletValue();
    
                    ITripletValue adjoinTarget = tripletValue.getAdjoinRightTarget1();
                    if (basicNextTier.contains(adjoinTarget))
                    {
                        //  calculate substructure-edge for target edge 1
                        createOrUpdateNextTierVertexInHSS(nextTierIndex, basicNextTier, hss, tierKeyOfTheVertexToShift, adjoinTarget, EdgeKind.Bottom1, 
                                                          k, keysSize, k == 0, structuresForConcordantShift);
                    }
                    adjoinTarget = tripletValue.getAdjoinRightTarget2();
                    if (basicNextTier.contains(adjoinTarget))
                    {
                        //  calculate substructure-edge for target edge 2
                        createOrUpdateNextTierVertexInHSS(nextTierIndex, basicNextTier, hss, tierKeyOfTheVertexToShift, adjoinTarget, EdgeKind.Bottom2, 
                                                          k, keysSize, k == 0, structuresForConcordantShift);
                    }
                }
                
                if (!clearLeafVertices(hss, nextTierIndex - 1))
                {
                    unifyCoincidentSubstructuresOfATier(hss, nextTierIndex);
                }
    
                //  Check for dirty vertices
                int dirtyTiersCount = countDirtyTiers(basicGraph);
                if (dirtyTiersCount > 0)
                {
                    LOGGER.debug("Remove last {} tier(s) of the HSS and rebuild them", dirtyTiersCount);
                    for (int i = 0; i < hss.size(); i++)
                    {
                        IHyperStructure hs = (IHyperStructure) hss.get(i);
                        int indexOfLastTier = hs.getTiers().size() - 1;
                        hs.getTiers().removeFromTo(indexOfLastTier - (dirtyTiersCount - 1), indexOfLastTier);
                    }
                    j -= dirtyTiersCount;
                    
                    if (j < 0)
                    {
                        j = 0;
                        basicGraph = createFirstHSSTier(cts, hss, sBasic, basicTiers);
                    }
                }
                else
                {
                    if (Helper.EnableAssertions)
                    {
                        assertHSSTierContainsSameNameVertices(hss, nextTierIndex);
                    }
    
                    if (Helper.EnableAssertions)
                    {
                        assertIntersectionOfTierSubstructuresIsEmpty(basicGraph, nextTierIndex);
                    }
                }
            }
        }
        finally
        {
            int hssTiersCount = 0;
            if (hss.size() > 0)
            {
                hssTiersCount = ((IHyperStructure) hss.get(0)).getTiers().size();
            }
            statistics.put(NUMBER_OF_HSS_TIERS_BUILT, String.valueOf(hssTiersCount));
        }
        
        return hss;
    }

    private static boolean clearLeafVertices(ObjectArrayList hss, int tierIndex)
    {
        int count = 0;
        for (int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            OpenIntObjectHashMap tierVertices = ((OpenIntObjectHashMap) hs.getTiers().get(tierIndex));
            for (int i = 0; i < tierVertices.size(); i++)
            {
                IVertex vertex = (IVertex) tierVertices.values().get(i);
                if (vertex.bothEdgesAreEmpty())
                {
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Clearing leaf-vertex {} on tier #{} of HSS({})", 
                                new Object[] { vertex.getTripletValue(), tierIndex + 1, h });
                    }
                    vertex.getCTS().clear();
                    count++;
                }
            }
        }
        LOGGER.debug("{} leaf-vertices (vertices that were not shifted) were removed from tier #{}", count, tierIndex + 1);
        if (count > 0)
        {
            LOGGER.debug("Running unification of coincident substructures for tier #{}", tierIndex + 1);
            
            unifyCoincidentSubstructuresOfATier(hss, tierIndex);
        }
        return count > 0;
    }

    private static void assertHSSTierContainsSameNameVertices(ObjectArrayList hss, int tierIndex)
    {
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        ICompactTripletsStructure basicCTS = basicGraph.getBasicCTS();
        
        for (int j = 0; j < basicGraph.getTiers().size(); j++)
        {
            ITier basicTier = basicCTS.getTier(j);
            for (int h = 0; h < hss.size(); h++)
            {
                IHyperStructure hs = (IHyperStructure) hss.get(h);
                OpenIntObjectHashMap hsTier = (OpenIntObjectHashMap) hs.getTiers().get(j);
                if (hsTier.size() != basicTier.size())
                {
                    throw new AssertionError("hsTier.size() != basicTier.size(), tierIndex=" + tierIndex);
                }
                assertBothContainsTripletValue(basicTier, hsTier, _000_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _001_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _010_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _011_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _100_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _101_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _110_instance);
                assertBothContainsTripletValue(basicTier, hsTier, _111_instance);
            }
        }
    }

    private static void assertBothContainsTripletValue(ITier basicTier, OpenIntObjectHashMap hsTier, ITripletValue tripletValue) 
        throws AssertionError
    {
        if (basicTier.contains(tripletValue) != hsTier.containsKey(tripletValue.getTierKey()))
        {
            throw new AssertionError("Tier content differs");
        }
    }

    private static IHyperStructure createFirstHSSTier(ObjectArrayList cts, final ObjectArrayList hss, ICompactTripletsStructure sBasic, ObjectArrayList basicTiers)
    {
        hss.clear();
        
        ITier firstBasicTier = (ITier) basicTiers.get(0);

        LOGGER.debug("Building HSS tier #1 of {}", basicTiers.size());

        for (int i = 0; i < cts.size(); i++)
        {
            final ICompactTripletsStructure sOther = (ICompactTripletsStructure) cts.get(i);
            
            if (sOther == sBasic)
            {
                continue;
            }
            
            IHyperStructure hs = new SimpleHyperStructure(sBasic, sOther);
            
            hss.add(hs);
            
            tryAddFirstTierVertex(hs, firstBasicTier, _000_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _001_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _010_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _011_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _100_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _101_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _110_instance, sOther);
            tryAddFirstTierVertex(hs, firstBasicTier, _111_instance, sOther);
        }
        
        unifyCoincidentSubstructuresOfATier(hss, 0);
        
        if (Helper.EnableAssertions)
        {
            assertHSSTierContainsSameNameVertices(hss, 0);
        }

        if (Helper.EnableAssertions)
        {
            assertIntersectionOfTierSubstructuresIsEmpty(((IHyperStructure) hss.get(0)), 0);
        }
        
        return (IHyperStructure) hss.get(0);
    }

    private static void tryAddFirstTierVertex(IHyperStructure hs, ITier firstBasicTier, ITripletValue tripletValue, ICompactTripletsStructure sOther) 
        throws EmptyStructureException
    {
        if (firstBasicTier.contains(tripletValue))
        {
            ICompactTripletsStructure clone = (ICompactTripletsStructure) sOther.clone();
            
            clone.concretize(firstBasicTier, tripletValue);
            
            hs.addVertex(firstBasicTier.size(), new SimpleVertex(firstBasicTier, 0, tripletValue, clone));
        }
    }

    private static void assertIntersectionOfTierSubstructuresIsEmpty(IHyperStructure basicGraph, final int tierIndex)
            throws AssertionError
    {
        OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) basicGraph.getTiers().get(tierIndex);
        if (tierVertices.size() == 1)
        {
            return;
        }
        ICompactTripletsStructure intersection = intersectAll(tierVertices.values());
        if (!intersection.isEmpty())
        {
            Helper.prettyPrint(intersection);
            throw new AssertionError("By the construction rules, intersection of substructure-vertices of a single tier should be an empty substructure");
        }
    }

    private static void createOrUpdateNextTierVertexInHSS(final int nextTierIndex,
            final ITier basicNextTier, final ObjectArrayList hss,
            int tierKeyOfTheVertexToShift, ITripletValue adjoinTarget, EdgeKind edgeKind,
            int vertexIndex, int verticesCount, boolean showProgressToLog, StructuresForConcordantShift structuresForConcordantShift)
    {
        ObjectArrayList substructureEdges = concordantShift(
                hss, nextTierIndex, tierKeyOfTheVertexToShift, basicNextTier.getCName(),
                adjoinTarget.isNotC() ? Value.AllNegative : Value.AllPlain,
                vertexIndex, verticesCount, showProgressToLog, structuresForConcordantShift);

        for (int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            OpenIntObjectHashMap prevTierVertices = (OpenIntObjectHashMap) hs.getTiers().get(nextTierIndex - 1);
            IVertex prevTierVertex = (IVertex) prevTierVertices.get(tierKeyOfTheVertexToShift);
            ICompactTripletsStructure substructureEdge = (ICompactTripletsStructure) substructureEdges.get(h);
            
            if (substructureEdge.isEmpty())
            {
                //  Mark all vertices of HSS tierIndex with empty edges
                prevTierVertex.foundEmptyEdge(edgeKind);
            }
            
            OpenIntObjectHashMap tierVertices = null;
            IVertex existingVertex = null;
            
            if (nextTierIndex < hs.getTiers().size()) 
            {
                tierVertices = (OpenIntObjectHashMap) hs.getTiers().get(nextTierIndex);
                existingVertex = (IVertex) tierVertices.get(adjoinTarget.getTierKey());
            }
            
            //  If the vertex is already on the next tier...
            if (existingVertex != null)
            {
                //  ... unite substructure-edge width substructure-vertex 
                //  and replace target substructure-vertex with resulting substructure
                
                existingVertex.getCTS().union(substructureEdge);
            }
            else
            {
                //  put substructure-edge to substructure-vertex as is
                hs.addVertex(basicNextTier.size(), new SimpleVertex(basicNextTier, nextTierIndex, adjoinTarget, substructureEdge));
            }
        }
    }

    private static class StructuresForConcordantShift
    {
        public ObjectArrayList substructureEdges;
        public OpenIntObjectHashMap hsIntersections;
        public ObjectArrayList coincidentIntersections;
        public StructuresForConcordantShift(int hssSize)
        {
            substructureEdges = new ObjectArrayList(hssSize);
            hsIntersections = new OpenIntObjectHashMap(hssSize);
            coincidentIntersections = new ObjectArrayList(hssSize);
            for (int h = 0; h < hssSize; h++)
            {
                hsIntersections.put(h, new ObjectArrayList(8));
            }
        }
        public void clear()
        {
            substructureEdges.clear();
            coincidentIntersections.clear();
            int hssSize = hsIntersections.size();
            for (int h = 0; h < hssSize; h++)
            {
                hsIntersections.forEachPair(new IntObjectProcedure()
                {
                    public boolean apply(int key, Object value)
                    {
                        ((ObjectArrayList) value).clear();
                        return true;
                    }
                });
            }
        }
    }
    
    /**
     * 
     * @param hss
     * @param tierKeyOfTheVertexToShift
     * @param cName
     * @param cValue
     * @return List of ICompactTripletsStructure
     */
    private static ObjectArrayList concordantShift(final ObjectArrayList hss, int nextTierIndex, int tierKeyOfTheVertexToShift, int cName, Value cValue,
            int vertexIndex, int verticesCount, boolean showProgressToLog, StructuresForConcordantShift structuresForConcordantShift)
    {
        //  Performance improvement: Reuse structures
        structuresForConcordantShift.clear();
        
        //  Parallel concretization
        int prevTierIndex = nextTierIndex - 1;
        for (int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) hs.getTiers().get(prevTierIndex);
            IVertex vertexToShift = (IVertex) prevTier.get(tierKeyOfTheVertexToShift);
            //  Work with a copy of substructure-vertex to keep original substructure the same
            ICompactTripletsStructure substructureEdge = (ICompactTripletsStructure) vertexToShift.getCTS().clone();
            substructureEdge.concretize(cName, cValue);
            structuresForConcordantShift.substructureEdges.add(substructureEdge);
        }
        
        try
        {
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Unify intermediate structures after concretization during concordant shift of {} tier #{} of {} total", 
                        new Object[] { SimpleTripletValueFactory.getTripletValue(tierKeyOfTheVertexToShift), prevTierIndex + 1, 
                        ((IHyperStructure) hss.get(0)).getBasicCTS().getTiers().size() });
            }
            unifyIntermediateSubstructures(structuresForConcordantShift.substructureEdges);
        }
        catch (EmptyStructureException e)
        {
            //  If substructure-edge become empty after concretization step, then resulting substructure-edge will also be empty.
            //  And if substructure-edge is empty at least in one HS, it will be empty in the entire HSS
            clear(structuresForConcordantShift.substructureEdges);
            return structuresForConcordantShift.substructureEdges;
        }
        
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        
        //  Parallel filtration
        for (int s = 0; s < prevTierIndex; s++)
        {
            //  Parallel intersection
            int sTierSize = ((OpenIntObjectHashMap) basicGraph.getTiers().get(s)).size();
            
            for (int h = 0; h < hss.size(); h++)
            {
                IHyperStructure hs = (IHyperStructure) hss.get(h);
                OpenIntObjectHashMap sTierVertices = (OpenIntObjectHashMap) hs.getTiers().get(s);
                ObjectArrayList intersections = (ObjectArrayList) structuresForConcordantShift.hsIntersections.get(h);
                intersections.clear();
                
                for (int sv = 0; sv < sTierSize; sv++)
                {
                    IVertex sTierVertex = (IVertex) sTierVertices.values().get(sv);
                    
                    ICompactTripletsStructure clone = (ICompactTripletsStructure) ((ICompactTripletsStructure) structuresForConcordantShift.substructureEdges.get(h)).clone();
                    clone.intersect(sTierVertex.getCTS());
                    intersections.add(clone);
                }
            }
            
            //  Unify intersections
            int intersectionsSize = ((ObjectArrayList) structuresForConcordantShift.hsIntersections.get(0)).size();
            
            if (intersectionsSize == 0)
            {
                clear(structuresForConcordantShift.substructureEdges);
                return structuresForConcordantShift.substructureEdges;
            }
            
            for (int v = 0; v < intersectionsSize; v++)
            {
                structuresForConcordantShift.coincidentIntersections.clear();
                for (int h = 0; h < hss.size(); h++)
                {
                    ObjectArrayList intersections = (ObjectArrayList) structuresForConcordantShift.hsIntersections.get(h);
                    ICompactTripletsStructure intersection = (ICompactTripletsStructure) intersections.get(v);
                    structuresForConcordantShift.coincidentIntersections.add(intersection);
                }
                
                try
                {
                    unifyIntermediateSubstructures(structuresForConcordantShift.coincidentIntersections);
                }
                catch(EmptyStructureException e)
                {
                    //  If some intersection is empty => all intersections should be empty
                    clear(structuresForConcordantShift.coincidentIntersections);
                }
            }
            
            //  Parallel union
            
            for (int h = 0; h < hss.size(); h++)
            {
                ObjectArrayList intersections = (ObjectArrayList) structuresForConcordantShift.hsIntersections.get(h);
                ICompactTripletsStructure substructureEdge = (ICompactTripletsStructure) intersections.get(0);
                structuresForConcordantShift.substructureEdges.set(h, substructureEdge);
            }
            
            for (int ks = 1; ks < intersectionsSize; ks++)
            {
                for (int h = 0; h < hss.size(); h++)
                {
                    ObjectArrayList intersections = (ObjectArrayList) structuresForConcordantShift.hsIntersections.get(h);
                    ICompactTripletsStructure substructureEdge = (ICompactTripletsStructure) structuresForConcordantShift.substructureEdges.get(h);
                    ICompactTripletsStructure intersection = (ICompactTripletsStructure) intersections.get(ks);
                    substructureEdge.union(intersection);
                }
            }
            
            try
            {
                if (showProgressToLog && s == 0)
                {
                    LOGGER.info("Unify unions after parallel filtration: vertexIndex = {} of {}, sTierIndex = {} of {}, nextTierIndex = {} of {}",
                                new Object[] { vertexIndex, verticesCount - 1, s, prevTierIndex - 1, nextTierIndex, 
                                ((IHyperStructure) hss.get(0)).getBasicCTS().getTiers().size() - 1 });
                }
                else
                {
                    //  Write the same at debug level
                    if (LOGGER.isDebugEnabled())
                    {
                        LOGGER.debug("Unify unions after parallel filtration: vertexIndex = {} of {}, sTierIndex = {} of {}, nextTierIndex = {} of {}",
                                     new Object[] { vertexIndex, verticesCount - 1, s, prevTierIndex - 1, nextTierIndex, 
                                     ((IHyperStructure) hss.get(0)).getBasicCTS().getTiers().size() - 1 });
                    }
                }
                unifyIntermediateSubstructures(structuresForConcordantShift.substructureEdges);
            }
            catch (EmptyStructureException e)
            {
                clear(structuresForConcordantShift.substructureEdges);
                return structuresForConcordantShift.substructureEdges;
            }
        }
        
        return structuresForConcordantShift.substructureEdges;
    }

    private static void unifyIntermediateSubstructures(ObjectArrayList cts) throws EmptyStructureException
    {
        if (cts.size() > 1)
        {
            unify(cts);
        }
        else if (cts.size() == 1)
        {
            ICompactTripletsStructure structure = (ICompactTripletsStructure) cts.get(0);
            if (structure.isEmpty())
            {
                throw new EmptyStructureException(structure);
            }
        }
    }

    private static void clear(ObjectArrayList cts)
    {
        for (int i = 0; i < cts.size(); i++)
        {
            ((ICompactTripletsStructure) cts.get(i)).clear();
        }
    }

    private static int countDirtyTiers(IHyperStructure basicGraph)
    {
        int count = 0;
        for (int i = basicGraph.getTiers().size() - 1; i >= 0; i--)
        {
            OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) basicGraph.getTiers().get(i);
            IVertex tierVertex = (IVertex) tierVertices.get(tierVertices.keys().get(0));
            if (tierVertex.isDirty())
            {
                count++;
            }
            else
            {
                break;
            }
        }
        return count;
    }

    /**
     * @param hss List of {@link IHyperStructure}
     * @param tierIndex
     */
    private static void unifyCoincidentSubstructuresOfATier(final ObjectArrayList hss, final int tierIndex)
    {
        IHyperStructure firstHS = (IHyperStructure) hss.get(0);

        final OpenIntObjectHashMap basicTierVertices = (OpenIntObjectHashMap) firstHS.getTiers().get(tierIndex);

        IntArrayList keys = basicTierVertices.keys();
        
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Tier #{} of HSS contained {} vertices before unification: {}", 
                    new Object[] { tierIndex + 1, basicTierVertices.size(), verticesTripletsToString(basicTierVertices) });
        }

        for (int j = 0; j < keys.size(); j++)
        {
            int vertexTierKey = keys.get(j);
            
            //  List of ICompactTripletsStructureHolder
            ObjectArrayList vertices = new ObjectArrayList(hss.size());
            
            for (int i = 0; i < hss.size(); i++)
            {
                IHyperStructure hs = (IHyperStructure) hss.get(i);
                        
                IVertex vertex = (IVertex) ((OpenIntObjectHashMap) hs.getTiers().get(tierIndex)).get(vertexTierKey);
                
                vertices.add(vertex);
            }
            
            try
            {
                if (hss.size() > 1)
                {
                    unify(vertices);
                }
                else
                {
                    //  Cross-hyperstructure unification is not applicable for 
                    //  HSS if there's less than 2 structures in it.
                    //  But if we have any empty substructure-vertex in vertices
                    //  we should remove this vertex from the basic graph
                    for (int i = 0; i < vertices.size(); i++)
                    {
                        IVertex vertex = (IVertex) vertices.get(i);
                        if (vertex.getCTS().isEmpty())
                        {
                            throw new EmptyStructureException(vertex.getCTS());
                        }
                    }
                }
            }
            catch (EmptyStructureException e)
            {
                removeVertexWithEmptySubstructureFromHSSAndBG(hss, tierIndex, vertexTierKey, vertices);
            }
        }

        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Tier #{} of HSS contained {} vertices after unification: {}", 
                    new Object[] { tierIndex + 1, basicTierVertices.size(), verticesTripletsToString(basicTierVertices) });
        }
    }

    private static void removeVertexWithEmptySubstructureFromHSSAndBG(final ObjectArrayList hss, int tierIndex, int vertexTierKey, ObjectArrayList vertices)
    {
        if (LOGGER.isDebugEnabled())
        {
            LOGGER.debug("Found empty substructure-vertex assigned to vertex {} of tier #{}",
                    ((IVertex) vertices.get(0)).getTripletValue(), tierIndex + 1);
        }
        
        //  Remove vertex with empty substructure from HSS and BG
        for (int i = 0; i < hss.size(); i++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(i);
            
            OpenIntObjectHashMap hsTierVertices = (OpenIntObjectHashMap) hs.getTiers().get(tierIndex);

            IVertex vertex = (IVertex) hsTierVertices.get(vertexTierKey);

            hsTierVertices.removeKey(vertexTierKey);
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Vertex {} removed from tier #{} of HSS({}): {}", 
                        new Object[] { vertex.getTripletValue(), tierIndex + 1, i, verticesTripletsToString(hsTierVertices) });
            }

            ICompactTripletsStructure basicCTS = hs.getBasicCTS();
            ITier basicTier = basicCTS.getTier(tierIndex);
            basicTier.remove(vertex.getTripletValue());
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Coincident vertex {} removed from tier #{} of the BG: {}", 
                        new Object[] { vertex.getTripletValue(), tierIndex + 1, tripletsToString(basicTier) });
                
                LOGGER.debug("Executing cleaup procedure on the basic structure...");
                LOGGER.debug("Basic structure before cleanup:");
                Helper.prettyPrint(basicCTS);
            }
            CleanupStatus status = basicCTS.cleanup(tierIndex, tierIndex);
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("Basic structure after cleanup:");
                Helper.prettyPrint(basicCTS);
            }
            
            if (basicCTS.isEmpty())
            {
                throw new EmptyStructureException(basicCTS);
            }

            int tiersRemovedAbove = status.someClausesRemoved ? tierIndex - status.from : 0;
            int tiersRemovedBelow = status.someClausesRemoved ? status.to - tierIndex : 0;
            
            if (LOGGER.isDebugEnabled())
            {
                LOGGER.debug("{} vertices were removed from BG incuding some vertices " +
                        "in {} tier(s) above and in {} tier(s) below (relatively to tier #{}) ", 
                        new Object[] { status.numberOfClausesRemoved, tiersRemovedAbove, tiersRemovedBelow, tierIndex + 1 });
            }
            
            if (tiersRemovedAbove > 0 || tiersRemovedBelow > 0)
            {
                markDirty(tierIndex, hs, tiersRemovedAbove);
            }
        }
    }

    /**
     * Mark vertices of the above <code>(deep + 2)</code> and all below 
     * tiers (relatively to <code>tierIndex</code>) as dirty.
     * 
     * @param tierIndex
     * @param hs
     * @param deep
     */
    private static void markDirty(int tierIndex, IHyperStructure hs, int deep)
    {
        for (int d = 0; d < deep + 2; d++)
        {
            int dirtyTierIndex = tierIndex - d;
            if (dirtyTierIndex < 0 || dirtyTierIndex >= hs.getTiers().size())
            {
                break;
            }
            OpenIntObjectHashMap dirtyTierVertices = (OpenIntObjectHashMap) hs.getTiers().get(dirtyTierIndex);
            dirtyTierVertices.forEachPair(new IntObjectProcedure()
            {
                public boolean apply(int key, Object value)
                {
                    ((IVertex) value).markDirty();
                    return true;
                }
            });
        }
        tierIndex++;
        while (tierIndex < hs.getTiers().size())
        {
            OpenIntObjectHashMap dirtyTierVertices = (OpenIntObjectHashMap) hs.getTiers().get(tierIndex);
            dirtyTierVertices.forEachPair(new IntObjectProcedure()
            {
                public boolean apply(int key, Object value)
                {
                    ((IVertex) value).markDirty();
                    return true;
                }
            });
            tierIndex++;
        }
    }

    private static StringBuilder tripletsToString(ITier tier)
    {
        StringBuilder builder = new StringBuilder();
        appendToBuilder(tier, builder, _000_instance);
        appendToBuilder(tier, builder, _001_instance);
        appendToBuilder(tier, builder, _010_instance);
        appendToBuilder(tier, builder, _011_instance);
        appendToBuilder(tier, builder, _100_instance);
        appendToBuilder(tier, builder, _101_instance);
        appendToBuilder(tier, builder, _110_instance);
        appendToBuilder(tier, builder, _111_instance);
        return builder;
    }

    private static void appendToBuilder(final ITier tier, final StringBuilder builder, ITripletValue tripletValue)
    {
        if (tier.contains(tripletValue)) 
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }
            builder.append(tripletValue);
        }
    }

    private static StringBuilder verticesTripletsToString(final OpenIntObjectHashMap tierVertices)
    {
        final StringBuilder builder = new StringBuilder();
        appendToBuilder(tierVertices, builder, _000_instance);
        appendToBuilder(tierVertices, builder, _001_instance);
        appendToBuilder(tierVertices, builder, _010_instance);
        appendToBuilder(tierVertices, builder, _011_instance);
        appendToBuilder(tierVertices, builder, _100_instance);
        appendToBuilder(tierVertices, builder, _101_instance);
        appendToBuilder(tierVertices, builder, _110_instance);
        appendToBuilder(tierVertices, builder, _111_instance);
        return builder;
    }

    private static void appendToBuilder(final OpenIntObjectHashMap tierVertices, final StringBuilder builder, ITripletValue tripletValue)
    {
        if (tierVertices.containsKey(tripletValue.getTierKey())) 
        {
            if (builder.length() > 0)
            {
                builder.append(", ");
            }
            builder.append(tripletValue);
        }
    }

    /**
     * Choose CTS with minimum number of clauses as a basic structure.
     * @param cts List of {@link ICompactTripletsStructure} 
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
     * @param route List of {@link IVertex} (optional). Route to highlight on the image.
     * @param markers List of {@link IVertex} (optional). Set of vertices that will be marked with rounded rectangles on resulting image.
     * @param filename
     * @throws IOException
     */
    public static void writeToImage(IHyperStructure hs, final ObjectArrayList route, final ObjectArrayList markers, String filename) throws IOException
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
                    
                    if (vertex.hasEmptyBottomEdge())
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
                            
                            if (vertex.hasEmptyBottomEdge())
                            {
                                graphics.setColor(Color.BLUE);
                            }
                        }
                    }
                    
                    graphics.drawString(vertex.getTripletValue().toString(), tripletOffset, y);
                    
                    graphics.setColor(color);

                    if (vertex.getBottomVertex1() != null) drawLine(route, vertex, vertex.getBottomVertex1(), graphics, offsetTop, heightOfZeroChar, heightBetweenTriplets, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);
                    if (vertex.getBottomVertex2() != null) drawLine(route, vertex, vertex.getBottomVertex2(), graphics, offsetTop, heightOfZeroChar, heightBetweenTriplets, offsetLeft, widthBetweenTriplets, widthBetweenChars, widthOfZeroChar);

                    if (markers != null)
                    {
                        for (int i = 0; i < markers.size(); i++)
                        {
                            if (markers.get(i) == vertex)
                            {
                                graphics.drawRoundRect(tripletOffset - 2, y - heightOfZeroChar + 2, 
                                        3 * widthOfZeroChar + 3, heightOfZeroChar, 5, 5);
                            }
                        }
                    }

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
     * @param hss
     * @return List of {@link IVertex} that form non-empty (n-2)-intersection, i.e. hyperstructure route.
     */
    private static ObjectArrayList quickFindHSSRoute(ObjectArrayList hss)
    {
        LOGGER.info("Running quickFindHSSRoute");
        
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        int tiersCount = basicGraph.getTiers().size();
        
        //  For test purposes only 
//        if (!basicGraph.getBasicCTS().isElementary())
//        {
//            LOGGER.info("Skipping quickFindHSSRoute to test full HSS reduction to elementary HSS");
//            return new ObjectArrayList(new Object[] { null });
//        }

        ObjectArrayList result = new ObjectArrayList(tiersCount);
        ObjectArrayList structures = new ObjectArrayList();

        OpenIntObjectHashMap verticesFromLastTiers = new OpenIntObjectHashMap();
        fillVerticesFromLastTier(hss, structures, verticesFromLastTiers);

        byte prevVertexTierKey = findIntersectionWithFirstTier(hss, result, structures, verticesFromLastTiers);

        if (prevVertexTierKey == -1)
        {
            throw new AssertionError("There must be at least one non-empty same-name intersection of last-tier vertex with first-tier vertex of HSS");
        }
        
        for (int j = 1; j < tiersCount - 1; j++)
        {
            ObjectArrayList intersections = new ObjectArrayList();
            ObjectArrayList targetVertices = new ObjectArrayList();
            
            boolean allNotEmpty = true;
            //  Check bottomVertex1
            for (int h = 0; h < hss.size(); h++)
            {
                IHyperStructure hs = (IHyperStructure) hss.get(h);
                
                OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) hs.getTiers().get(j - 1);
                
                IVertex prevTierVertex = (IVertex) prevTier.get(prevVertexTierKey);
                
                OpenIntObjectHashMap nextTier = (OpenIntObjectHashMap) hs.getTiers().get(j);

                IVertex bottomVertex1 = prevTierVertex.getBottomVertex1();

                if (bottomVertex1 == null)
                {
                    allNotEmpty = false;
                    break;
                }
                
                IVertex nextTierVertex = (IVertex) nextTier.get(bottomVertex1.getTripletValue().getTierKey());
                
                if (nextTierVertex == null)
                {
                    allNotEmpty = false;
                    break;
                }
                
                ICompactTripletsStructure clone = (ICompactTripletsStructure) ((ICompactTripletsStructure) structures.get(h)).clone();
                
                clone.intersect(nextTierVertex.getCTS());
                
                if (clone.isEmpty())
                {
                    allNotEmpty = false;
                    break;
                }
                
                targetVertices.add(nextTierVertex);
                intersections.add(clone);
            }
            
            allNotEmpty = allNotEmpty && forwardFilterPassed(hss, targetVertices, j, tiersCount, intersections, verticesFromLastTiers);
            
            if (allNotEmpty)
            {
                //  Intersection with bottomVertex1
                OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) basicGraph.getTiers().get(j - 1);
                
                IVertex prevTierVertex = (IVertex) prevTier.get(prevVertexTierKey);
                
                IVertex vertex = prevTierVertex.getBottomVertex1();
                
                result.add(vertex);
                
                prevVertexTierKey = vertex.getTripletValue().getTierKey();
                
                updateStructures(hss, structures, j, prevVertexTierKey);
                
                continue;
            }
            intersections = new ObjectArrayList();
            targetVertices = new ObjectArrayList();
            allNotEmpty = true;
            //  Check bottomVertex2
            for (int h = 0; h < hss.size(); h++)
            {
                IHyperStructure hs = (IHyperStructure) hss.get(h);
                
                OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) hs.getTiers().get(j - 1);
                
                IVertex prevTierVertex = (IVertex) prevTier.get(prevVertexTierKey);
                
                OpenIntObjectHashMap nextTier = (OpenIntObjectHashMap) hs.getTiers().get(j);

                IVertex bottomVertex2 = prevTierVertex.getBottomVertex2();

                if (bottomVertex2 == null)
                {
                    allNotEmpty = false;
                    break;
                }

                IVertex nextTierVertex = (IVertex) nextTier.get(bottomVertex2.getTripletValue().getTierKey());

                if (nextTierVertex == null)
                {
                    allNotEmpty = false;
                    break;
                }

                ICompactTripletsStructure clone = (ICompactTripletsStructure) ((ICompactTripletsStructure) structures.get(h)).clone();

                clone.intersect(nextTierVertex.getCTS());
                
                if (clone.isEmpty())
                {
                    allNotEmpty = false;
                    break;
                }
                
                targetVertices.add(nextTierVertex);
                intersections.add(clone);
            }
            
            allNotEmpty = allNotEmpty && forwardFilterPassed(hss, targetVertices, j, tiersCount, intersections, verticesFromLastTiers);
            
            if (allNotEmpty)
            {
                //  Intersection with bottomVertex2
                OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) basicGraph.getTiers().get(j - 1);
                
                IVertex prevTierVertex = (IVertex) prevTier.get(prevVertexTierKey);
                
                IVertex vertex = prevTierVertex.getBottomVertex2();
                
                result.add(vertex);
                
                prevVertexTierKey = vertex.getTripletValue().getTierKey();
                
                updateStructures(hss, structures, j, prevVertexTierKey);
                
                continue;
            }
            
            //  Note: For debug purposes, also isValidHSSRoute 
            //  will check the route for absence of null vertices 
            //  to ensure the route is valid 
            result.add(null);
            
            prevVertexTierKey = ((IVertex) ((OpenIntObjectHashMap) basicGraph.getTiers().get(j)).values().get(0)).getTripletValue().getTierKey();
            
            LOGGER.info("quickFindHSSRoute failed: " + (j+1) + " tier has been built empty. Terminating quickFindHSSRoute");
            
            break;
        }
        
        result.add(verticesFromLastTiers.get(0));
        
        return result;
    }

    private static byte findIntersectionWithFirstTier(ObjectArrayList hss,
            ObjectArrayList result,
            ObjectArrayList structures,
            OpenIntObjectHashMap verticesFromLastTiers)
    {
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        int tiersCount = basicGraph.getTiers().size();
        
        byte prevVertexTierKey = -1;
        
        //  Find non-empty intersection of last-tier vertex with the first tier vertex in each HS 
        //  (index of first tier vertex should be the same for each HS). 
        for (int j = 0; j < 1; j++)
        {
            boolean added = false;
            int tierSize = ((OpenIntObjectHashMap) basicGraph.getTiers().get(j)).size();
            
            for (int s = 0; s < tierSize; s++)
            {
                boolean allSameNameIntersectionsNotEmpty = true;
                ObjectArrayList intersections = new ObjectArrayList();
                ObjectArrayList targetVertices = new ObjectArrayList();
                
                for (int h = 0; h < hss.size(); h++)
                {
                    IHyperStructure hs = (IHyperStructure) hss.get(h);
                    
                    OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) hs.getTiers().get(j);
                    
                    IVertex tierVertex = (IVertex) tierVertices.values().get(s);
                    
                    ICompactTripletsStructure clone = (ICompactTripletsStructure) ((ICompactTripletsStructure) structures.get(h)).clone();
                    
                    clone.intersect(tierVertex.getCTS());
                    
                    if (clone.isEmpty())
                    {
                        allSameNameIntersectionsNotEmpty = false;
                        break;
                    }
                    
                    targetVertices.add(tierVertex);
                    intersections.add(clone);
                }
                
                allSameNameIntersectionsNotEmpty = allSameNameIntersectionsNotEmpty 
                                                && forwardFilterPassed(hss, targetVertices, j, tiersCount, intersections, verticesFromLastTiers);
                
                if (allSameNameIntersectionsNotEmpty)
                {
                    IVertex vertex = (IVertex) ((OpenIntObjectHashMap) basicGraph.getTiers().get(j)).values().get(s);
                    
                    result.add(vertex);
                    
                    added = true;
                    
                    prevVertexTierKey = vertex.getTripletValue().getTierKey();
                    
                    updateStructures(hss, structures, j, prevVertexTierKey);
                    
                    break;
                }
            }
            if (!added)
            {
                throw new AssertionError("No vertices from first tier have non-empty intersection with vertex from last tier.");
            }
        }
        return prevVertexTierKey;
    }

    private static void fillVerticesFromLastTier(ObjectArrayList hss,
            ObjectArrayList structures,
            OpenIntObjectHashMap verticesFromLastTiers)
    {
        IHyperStructure basicGraph = (IHyperStructure) hss.get(0);
        int tiersCount = basicGraph.getTiers().size();
        
        //  Pick any vertex from the last tier of each HS (with the same index for each HS)
        for(int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            
            IVertex vertexFromTheLastTier = (IVertex) ((OpenIntObjectHashMap) hs.getTiers().get(tiersCount - 1)).values().get(0);
            verticesFromLastTiers.put(h, vertexFromTheLastTier);
            
            ICompactTripletsStructure structure = ((IVertex) verticesFromLastTiers.get(h)).getCTS();
            structures.add(structure);
        }
    }

    private static boolean forwardFilterPassed(ObjectArrayList hss, ObjectArrayList sourceVertices, int j, int tiersCount, ObjectArrayList structures, OpenIntObjectHashMap verticesFromLastTiers)
        throws EmptyStructureException
    {
        ObjectArrayList substructures = new ObjectArrayList();
        for (int i = 0; i < structures.size(); i++)
        {
            substructures.add(((ICompactTripletsStructure) structures.get(i)).clone());
        }
        
        try
        {
            //  Forward filter
            
            unifyIntermediateSubstructures(substructures);
            
            for (int j2 = j + 1; j2 < tiersCount; j2++)
            {
                OpenIntObjectHashMap hsIntersections = new OpenIntObjectHashMap();
                for (int h = 0; h < hss.size(); h++)
                {
                    IHyperStructure hs = (IHyperStructure) hss.get(h);
                    OpenIntObjectHashMap nextTier = (OpenIntObjectHashMap) hs.getTiers().get(j2);
                    ObjectArrayList intersections = new ObjectArrayList();
                    hsIntersections.put(h, intersections);
                    for (int s = 0; s < nextTier.size(); s++)
                    {
                        ICompactTripletsStructure lastTierStructure = ((IVertex) verticesFromLastTiers.get(h)).getCTS();
                        IVertex nextTierVertex = (IVertex) nextTier.values().get(s);
                        ICompactTripletsStructure nextTierStructure = (ICompactTripletsStructure) nextTierVertex.getCTS().clone();
                        nextTierStructure.intersect(lastTierStructure);
                        
                        nextTierStructure.intersect((ICompactTripletsStructure) substructures.get(h));
                        intersections.add(nextTierStructure);
                    }
                    
                    if (intersections.size() == 0)
                    {
                        //  Cannot use this path
                        return false;
                    }
                }
                
                //  Unify intersections
                int intersectionsSize = ((ObjectArrayList) hsIntersections.get(0)).size();
                
                if (intersectionsSize == 0)
                {
                    return false;
                }
                
                boolean allEmpty = true;
                
                for (int v = 0; v < intersectionsSize; v++)
                {
                    ObjectArrayList coincidentIntersections = new ObjectArrayList(hss.size());
                    for (int h = 0; h < hss.size(); h++)
                    {
                        ObjectArrayList intersections = (ObjectArrayList) hsIntersections.get(h);
                        ICompactTripletsStructure intersection = (ICompactTripletsStructure) intersections.get(v);
                        coincidentIntersections.add(intersection);
                    }
                    
                    try
                    {
                        unifyIntermediateSubstructures(coincidentIntersections);
                    }
                    catch(EmptyStructureException e)
                    {
                        //  If some intersection is empty => all intersections should be empty
                        clear(coincidentIntersections);
                    }
                    
                    if (!((ICompactTripletsStructure)coincidentIntersections.get(0)).isEmpty())
                    {
                        allEmpty = false;
                    }
                }
                
                if (allEmpty)
                {
                    return false;
                }
//                //  Parallel union
//                
//                for (int h = 0; h < hss.size(); h++)
//                {
//                    ObjectArrayList intersections = (ObjectArrayList) hsIntersections.get(h);
//                    ICompactTripletsStructure substructure = (ICompactTripletsStructure) intersections.get(0);
//                    substructures.set(h, substructure);
//                }
//                
//                for (int ks = 1; ks < intersectionsSize; ks++)
//                {
//                    for (int h = 0; h < hss.size(); h++)
//                    {
//                        ObjectArrayList intersections = (ObjectArrayList) hsIntersections.get(h);
//                        ICompactTripletsStructure substructure = (ICompactTripletsStructure) substructures.get(h);
//                        substructure.union((ICompactTripletsStructure) intersections.get(ks));
//                        substructures.set(h, substructure);
//                    }
//                }
//                
//                try
//                {
////                    LOGGER.info("Unify unions: j2 = {} of {}",
////                            new Object[] { j2, ((IHyperStructure) hss.get(0)).getBasicCTS().getTiers().size() - 1 });
//                    unifyIntermediateSubstructures(substructures);
//                }
//                catch (EmptyStructureException e)
//                {
//                    return false;
//                }
            }
            
            return true;
        }
        catch (EmptyStructureException e)
        {
            return false;
        }
    }
    
    private static void updateStructures(ObjectArrayList hss, ObjectArrayList structures, int tierIndex, byte vertexTierKey)
    {
        for (int h = 0; h < hss.size(); h++)
        {
            IHyperStructure hs = (IHyperStructure) hss.get(h);
            
            OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) hs.getTiers().get(tierIndex);
            
            IVertex tierVertex = (IVertex) tierVertices.get(vertexTierKey);
            
            ICompactTripletsStructure clone = (ICompactTripletsStructure) 
                    ((ICompactTripletsStructure) structures.get(h)).clone();
            
            clone.intersect(tierVertex.getCTS());
            
            structures.set(h, clone);
        }
        unifyIntermediateSubstructures(structures);
    }
    
    /**
     * 
     * @param vertices List of {@link IVertex}
     * @return
     */
    private static ICompactTripletsStructure intersectAll(ObjectArrayList vertices)
    {
        ICompactTripletsStructure result = (ICompactTripletsStructure) ((IVertex)vertices.get(0)).getCTS().clone();
        for (int i = 1; i < vertices.size(); i++)
        {
            IVertex vertex = (IVertex) vertices.get(i);
            result.intersect(vertex.getCTS());
        }
        return result;
    }
    
    public static void convertCTStructuresToRomanovSKTFileFormat(ObjectArrayList cts, String filename)
        throws FileNotFoundException, IOException
    {
        OutputStream os = null;
        try
        {
            os = new FileOutputStream(new File(filename));
            for (int i = 0; i < cts.size(); i++)
            {
                ITabularFormula f = (ITabularFormula) cts.get(i);
                for (int j = 0; j < f.getTiers().size(); j++)
                {
                    ITier tier = f.getTier(j);
                    for (ITripletValue tripletValue : tier)
                    {
                        int a = (tripletValue.isNotA() ? -1 : 1) * tier.getAName();
                        int b = (tripletValue.isNotB() ? -1 : 1) * tier.getBName();
                        int c = (tripletValue.isNotC() ? -1 : 1) * tier.getCName();
                        
                        writeJavaIntAsDelphiLongInt(os, a);
                        writeJavaIntAsDelphiLongInt(os, b);
                        writeJavaIntAsDelphiLongInt(os, c);
                    }
                }
                writeJavaIntAsDelphiLongInt(os, 0);
                writeJavaIntAsDelphiLongInt(os, 0);
                writeJavaIntAsDelphiLongInt(os, 0);
            }
        }
        finally
        {
            if (os != null)
            {
                os.close();
            }
        }
    }
    
    private static void writeJavaIntAsDelphiLongInt(OutputStream os, int intValue) throws IOException
    {
        os.write(intValue);
        intValue >>= 8;
        os.write(intValue);
        intValue >>= 8;
        os.write(intValue);
        intValue >>= 8;
        os.write(intValue);
        intValue >>= 8;
    }
    
    /**
     * 
     * @param hs
     * @param vertex
     * @return List of vertices from <code>hs</code> whose substructures has non-empty intersection 
     * with substructure of the <code>vertex</code> argument.   
     */
    public static ObjectArrayList findNonEmptyIntersections(IHyperStructure hs, IVertex vertex)
    {
        ObjectArrayList result = new ObjectArrayList();
        for (int j = 0; j < hs.getTiers().size(); j++)
        {
            OpenIntObjectHashMap tierVertices = (OpenIntObjectHashMap) hs.getTiers().get(j);
            for (int i = 0; i < tierVertices.size(); i++)
            {
                IVertex tierVertex = (IVertex) tierVertices.values().get(i);
                ICompactTripletsStructure clone = ((ICompactTripletsStructure) tierVertex.getCTS().clone());
                clone.intersect(vertex.getCTS());
                if (!clone.isEmpty())
                {
                    result.add(tierVertex);
                }
            }
        }
        return result;
    }

    /**
     * 
     * @param ctf
     * @param route List of {@link IVertex} forming HSS route.
     * @return
     */
    public static boolean evaluate(ObjectArrayList ctf, ObjectArrayList route)
    {
        boolean result = true;
        
        for (int i = 0; i < ctf.size(); i++)
        {
            result = result && (((ITabularFormula) ctf.get(i)).evaluate(route));
        }
        
        return result;
    }

    /**
     * 
     * @param ct List of {@link ITabularFormula}.
     * @return
     */
    public static ObjectArrayList cloneStructures(ObjectArrayList ct)
    {
        ObjectArrayList ctf = new ObjectArrayList();
        for (int i = 0; i < ct.size(); i++)
        {
            ctf.add(((ITabularFormula) ct.get(i)).clone());
        }
        return ctf;
    }

    /**
     * There are should be at least 4 bytes in input stream. Otherwise result may be not defined.
     * If input stream contains no more data, method return {@link Integer#MAX_VALUE}.
     * 
     * @param input
     * @return Next Integer from input stream.
     * @throws IOException
     */
    public static int readInt(InputStream input) throws IOException
    {
        int value = Integer.MAX_VALUE;
        int buf;
        if ((buf = input.read()) != -1)
        {
            value = buf;
            value = value | (input.read() << 8);
            value = value | (input.read() << 16);
            value = value | (input.read() << 24);
        }
        return value;
    }

    public static String getImplementationVersionFromManifest(String implementationTitle)
    {
        String version = "Unknown";
        ClassLoader cl = Helper.class.getClassLoader();
        if (!(cl instanceof URLClassLoader))
        {
            return version;
        }
        URLClassLoader ucl = (URLClassLoader) cl;
        for (URL url : ucl.getURLs())
        {
            JarFile jar = null;
            try
            {
                String protocol = url.getProtocol();
                if (!"file".equalsIgnoreCase(protocol))
                {
                    continue;
                }
                String filename = URLDecoder.decode(url.getFile(), "utf-8");
                jar = new JarFile(filename);
                Manifest manifest = jar.getManifest();
                Attributes attributes = manifest.getMainAttributes();
                String title = String.valueOf(attributes.get(Attributes.Name.IMPLEMENTATION_TITLE));
                if (title.equalsIgnoreCase(implementationTitle))
                {
                    Attributes buildInfo = manifest.getEntries().get("Build-Info");
                    return String.valueOf(attributes.get(Attributes.Name.IMPLEMENTATION_VERSION))
                         + " "
                         + buildInfo.getValue("Build-Timestamp");
                }
            }
            catch (IOException e)
            {
                LOGGER.warn("Error reading manifest from " + url, e);
            }
            finally
            {
                if (jar != null)
                {
                    try
                    {
                        jar.close();
                    }
                    catch (IOException e)
                    {
                        LOGGER.warn("Error closing " + url, e);
                    }
                }
            }
        }
        return version;
    }
    
    public static ObjectArrayList loadHSS(String hssPath) throws IOException
    {
        LOGGER.info("Load HSS from {}...", hssPath);
        
        ObjectArrayList hss = new ObjectArrayList();
        
        String basicCTSPath = hssPath + File.separator + "basic-cts.cnf";
        LOGGER.info("Load Basic CTS from {}...", basicCTSPath);
        ITabularFormula basicCTS = loadFromFile(basicCTSPath);
        LOGGER.info("Basic CTS loaded");
        
        File[] allCTS = new File(hssPath).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".cnf");
            }
        });
        LOGGER.info("{} more instances to load", allCTS.length - 1);
        
        String[] otherCTSPaths = new File(hssPath).list(new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                return name.endsWith("-other-cts.cnf");
            }
        });
        
        Arrays.sort(otherCTSPaths);
        
        for (String otherCTSPath : otherCTSPaths)
        {
            Matcher matcher = Pattern.compile("hss-(\\d+)-").matcher(otherCTSPath);
            matcher.find();
            final String hsIndex = matcher.group(1);
            
            LOGGER.debug("Load CTS from {}...", otherCTSPath);
            ITabularFormula otherCTS = loadFromFile(hssPath + File.separator + otherCTSPath);
            LOGGER.debug("CTS loaded");
            IHyperStructure hs = new SimpleHyperStructure((ICompactTripletsStructure) basicCTS, (ICompactTripletsStructure) otherCTS);
            hss.add(hs);
            
            String[] substructuresPaths = new File(hssPath).list(new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.startsWith("hss-" + hsIndex + "-tier");
                }
            });
            
            Arrays.sort(substructuresPaths);
            
            String verticesInfoPath = hssPath + File.separator + "hss-" + hsIndex + "-vertices.properties";
            Properties verticesInfo = new Properties();
            InputStream is = new FileInputStream(new File(verticesInfoPath));
            try
            {
                verticesInfo.load(is);
            }
            finally
            {
                is.close();
            }
            
            for (String substructurePath : substructuresPaths)
            {
                Matcher matcher2 = Pattern.compile("tier-(\\d+)-(\\d+)").matcher(substructurePath);
                matcher2.find();
                int j = Integer.parseInt(matcher2.group(1));
                String tripletString = matcher2.group(2);
                
                ITripletValue tripletValue = SimpleTripletValueFactory.getTripletValue(tripletString);
                
                if (hs.getTiers().size() == j)
                {
                    hs.getTiers().add(new OpenIntObjectHashMap(8));
                }
                OpenIntObjectHashMap tier = (OpenIntObjectHashMap) hs.getTiers().get(j);
                
                LOGGER.debug("Load CTS from {}...", substructurePath);
                ITabularFormula substructure = loadFromFile(hssPath + File.separator + substructurePath);
                LOGGER.debug("CTS loaded");
                
                SimpleVertex vertex = new SimpleVertex(basicCTS.getTier(j), j, tripletValue, (ICompactTripletsStructure) substructure);
                vertex.setHyperStructure(hs);
                
                boolean bottom1empty = Boolean.parseBoolean((String) verticesInfo.get(substructurePath + "-bottom1empty"));
                boolean bottom2empty = Boolean.parseBoolean((String) verticesInfo.get(substructurePath + "-bottom2empty"));
                
                if (bottom1empty) vertex.foundEmptyEdge(EdgeKind.Bottom1);
                if (bottom2empty) vertex.foundEmptyEdge(EdgeKind.Bottom2);
                
                tier.put(tripletValue.getTierKey(), vertex);
            }
        }
        
        LOGGER.info("HSS loaded");
        
        return hss;
    }
    
    public static void saveHSS(String hssPath, ObjectArrayList hss) throws IOException
    {
        LOGGER.info("Saving HSS to filesystem {}...", hssPath);
        
        File hssPathFile = new File(hssPath);
        
        if (!hssPathFile.exists() && !hssPathFile.mkdir())
        {
            throw new IOException("Cannot create " + hssPath);
        }
        
        File[] files = hssPathFile.listFiles();
        for (File file : files)
        {
            if (!file.delete())
            {
                throw new IOException("Cannot delete " + file.getAbsolutePath());
            }
        }
        
        IHyperStructure hs = (IHyperStructure) hss.get(0);
        
        String basicCTSPath = hssPath + File.separator + "basic-cts.cnf";
        LOGGER.info("Saving Basic CTS {}...", basicCTSPath);
        saveToDIMACSFileFormat(hs.getBasicCTS(), basicCTSPath);
        LOGGER.info("Basic CTS saved");
        
        int hssLength = String.valueOf(hss.size()).length();
        int tiersLength = String.valueOf(hs.getTiers().size()).length();
        
        for (int h = 0; h < hss.size(); h++)
        {
            hs = (IHyperStructure) hss.get(h);
            
            String hssImageFilename = hssPath + File.separator + "hss-" + leadingZeros(h, hssLength) + ".png";
            LOGGER.debug("Saving HSS({}) image {}...", h, hssImageFilename);
            writeToImage(hs, null, null, hssImageFilename);
            LOGGER.debug("Image saved");
            
            String otherCTSPath = hssPath + File.separator + "hss-" + leadingZeros(h, hssLength) + "-other-cts.cnf";
            LOGGER.debug("Saving CTS {}...", otherCTSPath);
            saveToDIMACSFileFormat(hs.getOtherCTS(), otherCTSPath);
            LOGGER.debug("CTS saved");
            
            Properties verticesInfo = new Properties();
            
            for (int j = 0; j < hs.getTiers().size(); j++)
            {
                OpenIntObjectHashMap tier = (OpenIntObjectHashMap) hs.getTiers().get(j);
                for (int i = 0; i < tier.size(); i++)
                {
                    int key = tier.keys().get(i);
                    IVertex vertex = (IVertex) tier.get(key);
                    ITripletValue triplet = SimpleTripletValueFactory.getTripletValue(key);
                    
                    String vertexFilename = "hss-" + leadingZeros(h, hssLength)
                                            + "-tier-" + leadingZeros(j, tiersLength)
                                            + "-" + triplet + ".cnf";
                    
                    String substructureVertexPath = hssPath + File.separator + vertexFilename; 
                    
                    LOGGER.debug("Saving substructure-vertex {}...", substructureVertexPath);
                    saveToDIMACSFileFormat(vertex.getCTS(), substructureVertexPath);
                    LOGGER.debug("Substructure-vertex saved");
                    
                    verticesInfo.put(vertexFilename + "-bottom1empty", String.valueOf(vertex.isBottom1Empty()));
                    verticesInfo.put(vertexFilename + "-bottom2empty", String.valueOf(vertex.isBottom2Empty()));
                }
            }
            
            String verticesInfoPath = hssPath + File.separator + "hss-" + leadingZeros(h, hssLength) + "-vertices.properties";
            LOGGER.debug("Saving vertices info {}...", verticesInfoPath);
            OutputStream os = new FileOutputStream(new File(verticesInfoPath));
            try
            {
               verticesInfo.store(os, null);
            }
            finally
            {
                os.close();
            }
            LOGGER.debug("Vertices info saved");
            LOGGER.info("HSS({}) saved", h);
        }
        LOGGER.info("HSS saved");
    }

    private static String leadingZeros(int value, int length)
    {
        String result = String.valueOf(value);
        if (result.length() < length)
        {
            result = getString('0', length - result.length()) + result;
        }
        return result;
    }

    public static ObjectArrayList findHSSRouteByReduce(ObjectArrayList hss, String hssTempPath) throws IOException
    {
        ObjectArrayList route = quickFindHSSRoute(hss);
        if (isValidHSSRoute(route))
        {
            return route;
        }
        
        IHyperStructure hs = (IHyperStructure) hss.get(0);
        ICompactTripletsStructure basicCTS = hs.getBasicCTS();
        
        //  Find next tier with one vertex
        for (int j = 0; j < basicCTS.getTiers().size(); j++)
        {
            //  TODO Configure hssTempPathReduced using CL options
            final String hssTempPathReduced = hssTempPath + "-reduced";
            if (j == 0)
            {
                if (basicCTS.getTier(0).size() > 1)
                {
                    //  First reduction
                    ObjectArrayList structures = new ObjectArrayList();
                    OpenIntObjectHashMap verticesFromLastTiers = new OpenIntObjectHashMap();
                    
                    fillVerticesFromLastTier(hss, structures, verticesFromLastTiers);
                    
                    ObjectArrayList result = new ObjectArrayList();
                    findIntersectionWithFirstTier(hss, result, structures, verticesFromLastTiers);
                    
                    ITier firstTier = basicCTS.getTier(0);
                    IVertex firstTierVertex = (IVertex) result.get(0);
                    firstTier.intersect(firstTierVertex.getTripletValue());
                    basicCTS.cleanup(0, 0);
                    
                    //  Unify CTS
                    ObjectArrayList cts = new ObjectArrayList();
                    cts.add(((IHyperStructure) hss.get(0)).getBasicCTS());
                    for (int h = 0; h < hss.size(); h++)
                    {
                        hs = (IHyperStructure) hss.get(h);
                        cts.add(hs.getOtherCTS());
                    }
                    unify(cts);
                    //  Update HSS by creating new one (it must be created non-empty)
                    Properties statistics = new Properties();
                    hss = createHyperStructuresSystem(cts, statistics);
                    saveHSS(hssTempPathReduced, hss);
                    //  Check if its possible to find HSS route now
                    route = quickFindHSSRoute(hss);
                    if (isValidHSSRoute(route))
                    {
                        return route;
                    }
                    //  Route not found. Reduce next tier
                    
                    //  Basic CTS may have been changed in newly created HSS
                    if (!basicCTS.getPermutation().sameAs(((IHyperStructure) hss.get(0)).getBasicCTS().getPermutation()))
                    {
                        //  Basic CTS changed
                        j = -1;
                        basicCTS = ((IHyperStructure) hss.get(0)).getBasicCTS();
                    }
                }
                continue;
            }
            
            ITier tier = basicCTS.getTier(j);
            if (tier.size() > 1)
            {
                //  Previous tier contains one vertex
                OpenIntObjectHashMap prevTier = (OpenIntObjectHashMap) ((IHyperStructure)hss.get(0)).getTiers().get(j - 1);
                IVertex prevVertex = (IVertex) prevTier.values().get(0);
                
                if (prevVertex.hasEmptyBottomEdge())
                {
                    //  If previous vertex contains only one bottom vertex 
                    //  then we should keep that vertex (and move forward to reduce next HSS tier?)
                    if (prevVertex.getBottomVertex1() != null)
                    {
                        tier.intersect(prevVertex.getBottomVertex1().getTripletValue());
                    }
                    else if (prevVertex.getBottomVertex2() != null)
                    {
                        tier.intersect(prevVertex.getBottomVertex2().getTripletValue());
                    }
                    
                    basicCTS.cleanup(j, j);
                    
                    //  Unify CTS
                    ObjectArrayList cts = new ObjectArrayList();
                    cts.add(((IHyperStructure) hss.get(0)).getBasicCTS());
                    for (int h = 0; h < hss.size(); h++)
                    {
                        hs = (IHyperStructure) hss.get(h);
                        cts.add(hs.getOtherCTS());
                    }
                    unify(cts);
                    //  Update HSS by creating new one (it must be created non-empty)
                    Properties statistics = new Properties();
                    hss = createHyperStructuresSystem(cts, statistics);
                    saveHSS(hssTempPathReduced, hss);
                    //  Check if its possible to find HSS route now
                    route = quickFindHSSRoute(hss);
                    if (isValidHSSRoute(route))
                    {
                        return route;
                    }
                    //  Route not found. Reduce next tier
                    
                    //  Basic CTS may have been changed in newly created HSS
                    if (!basicCTS.getPermutation().sameAs(((IHyperStructure) hss.get(0)).getBasicCTS().getPermutation()))
                    {
                        //  Basic CTS changed
                        j = -1;
                        basicCTS = ((IHyperStructure) hss.get(0)).getBasicCTS();
                    }
                }
                else
                {
                    LOGGER.info("Backup HSS to {}...", hssTempPath);
                    saveHSS(hssTempPath, hss);
                    
                    ITripletValue bottomVertex1TripletValue = prevVertex.getBottomVertex1().getTripletValue();
                    ITripletValue bottomVertex2TripletValue = prevVertex.getBottomVertex2().getTripletValue();
                    
                    tier.intersect(bottomVertex1TripletValue);
                    
                    try
                    {
                        basicCTS.cleanup(j, j);
                        
                        //  Unify CTS
                        ObjectArrayList cts = new ObjectArrayList();
                        cts.add(((IHyperStructure) hss.get(0)).getBasicCTS());
                        for (int h = 0; h < hss.size(); h++)
                        {
                            hs = (IHyperStructure) hss.get(h);
                            cts.add(hs.getOtherCTS());
                        }
                        unify(cts);
                        //  Update HSS by creating new one (it must be created non-empty)
                        Properties statistics = new Properties();
                        hss = createHyperStructuresSystem(cts, statistics);
                    }
                    catch (EmptyStructureException e)
                    {
                        //  If HSS was built empty then  
                        //  restore HSS and keep vertex from bottomVertex2
                        
                        LOGGER.info("Restore HSS from backup {}", hssTempPath);
                        hss = loadHSS(hssTempPath);
                        LOGGER.info("done");
                        
                        basicCTS = ((IHyperStructure) hss.get(0)).getBasicCTS();
                        tier = basicCTS.getTier(j);
                        tier.intersect(bottomVertex2TripletValue);
                        
                        basicCTS.cleanup(j, j);
                        
                        //  Unify CTS
                        ObjectArrayList cts = new ObjectArrayList();
                        cts.add(((IHyperStructure) hss.get(0)).getBasicCTS());
                        for (int h = 0; h < hss.size(); h++)
                        {
                            hs = (IHyperStructure) hss.get(h);
                            cts.add(hs.getOtherCTS());
                        }
                        unify(cts);
                        //  Update HSS by creating new one (it must be created non-empty)
                        Properties statistics = new Properties();
                        hss = createHyperStructuresSystem(cts, statistics);
                    }
                    saveHSS(hssTempPathReduced, hss);
                    //  Check if its possible to find HSS route now
                    route = quickFindHSSRoute(hss);
                    if (isValidHSSRoute(route))
                    {
                        return route;
                    }
                    //  Route not found. Reduce next tier
                    
                    //  Basic CTS may have been changed in newly created HSS
                    if (!basicCTS.getPermutation().sameAs(((IHyperStructure) hss.get(0)).getBasicCTS().getPermutation()))
                    {
                        //  Basic CTS changed
                        j = -1;
                        basicCTS = ((IHyperStructure) hss.get(0)).getBasicCTS();
                    }
                }
            }
        }
        
        throw new AssertionError("HSS was reduced to elementary CTS in basic graph but HSS route not found");
    }

    private static boolean isValidHSSRoute(ObjectArrayList route)
    {
        for (int i = 0; i < route.size(); i++)
        {
            if (route.get(i) == null)
            {
                return false;
            }
        }
        return true;
    }
    
}
