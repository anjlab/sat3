package com.anjlab.sat3.v2;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.anjlab.sat3.ICompactTripletsStructure;
import com.anjlab.sat3.IPermutation;
import com.anjlab.sat3.ITabularFormula;
import com.anjlab.sat3.ITripletPermutation;
import com.anjlab.sat3.ITripletValue;
import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public class Helper2
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Helper2.class);
    
    public static boolean UsePrettyPrint = false;
    
    public static boolean UseUniversalVarNames = true;

    public static void printLine(char c, int length) {
        String string = getString(c, length);
        LOGGER.info(string);
    }

    public static String getString(char c, int length)
    {
        char[] line = new char[length];
        Arrays.fill(line, c);
        String string = new String(line);
        return string;
    }

    public static void prettyPrint(ICompactCouplesStructure ccs)
    {
        prettyPrint(ccs, null);
    }

    public static void prettyPrint(ICompactCouplesStructure ccs, IColumnsInversionControl cic)
    {
        printLine('-', 50);

        StringBuilder builder = buildPrettyOutput(ccs, cic);
        builder.insert(0, '\n');

        LOGGER.info(builder.toString());
    }

    public static StringBuilder buildPrettyOutput(
            ICompactCouplesStructure ccs, IColumnsInversionControl cic,
            int varName)
    {
        int varIndex = ccs.getPermutation().indexOf(varName);
        return buildPrettyOutput(ccs, cic,
                Math.max(0, varIndex - 3),
                Math.min(ccs.getPermutation().size() - 1, varIndex + 3));
    }
    
    public static StringBuilder buildPrettyOutput(ICompactCouplesStructure ccs, IColumnsInversionControl cic)
    {
        return buildPrettyOutput(ccs, cic, 0, ccs.getPermutation().size() - 1);
    }
    
    private static StringBuilder buildPrettyOutput(
            ICompactCouplesStructure ccs, IColumnsInversionControl cic,
            int fromIndex, int toIndex)
    {
        StringBuilder builder = new StringBuilder();

        boolean smallFormula = false; //    formula.getVarCount() < 100;
        if (UsePrettyPrint || smallFormula)
        {
            int longestVarName = 0;
            IPermutation permutation = ccs.getPermutation();
            for (int i = fromIndex; i <= toIndex; i++)
            {
                int varName = permutation.get(i);
                int varNameLength = String.valueOf(varName).length();
    
                if (varNameLength > longestVarName)
                {
                    longestVarName = varNameLength;
                }
            }

            char[] spacesArray = new char[longestVarName + 2 + 4];
            Arrays.fill(spacesArray, ' ');
            String spaces = new String(spacesArray);
            
            String headerOffset = spaces.substring(0, longestVarName + 1 + " | ".length());
            
            if (cic != null)
            {
                builder.append(headerOffset);
                for (int i = fromIndex; i <= toIndex; i++)
                {
                    int varName = ccs.getPermutation().get(i);
                    
                    builder.append(spaces.substring(0, getLegendName(varName).length()));
                    ((SimpleColumnsInversionControl) cic)
                        .appendLegendChar(builder, varName);
                }
                builder.append('\n');
            }
            
            builder.append(headerOffset);
            for (int i = fromIndex; i <= toIndex; i++)
            {
                int varName = ccs.getPermutation().get(i);
                
                builder.append(' ');
                builder.append(getLegendName(varName));
            }
            builder.append('\n');
            if (ccs.isEmpty())
            {
                builder.append("<empty>\n");
            }
            else
            {
                ObjectArrayList tiers = ccs.getTiers();
                for (int j = fromIndex; j < toIndex; j++)
                {
                    ITier2 tier = (ITier2) tiers.get(j);
                    for (ICoupleValue coupleValue : tier)
                    {
                        builder
                            .append(spaces.substring(0, longestVarName - String.valueOf(j).length() + 1))
                            .append(j)
                            .append(" | ");
                        
                        boolean valueOfLastVarPrinted = false;
                        for (int i = fromIndex; i <= toIndex; i++)
                        {
                            int varName = ccs.getPermutation().get(i);
                            
                            if (varName == getAName(j, ccs.getPermutation()))
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length()));
                                builder.append(coupleValue.isNotA() ? 1 : 0);
                            }
                            else if (varName == getBName(j, ccs.getPermutation()))
                            {
                                builder.append(spaces.substring(0, getLegendName(varName).length()));
                                builder.append(coupleValue.isNotB() ? 1 : 0);
                                valueOfLastVarPrinted = true;
                                
                                //  Print twin labels
                                boolean twinLabelPrinted = false;
                                
                                if (j > 0)
                                {
                                    //  Should be previous tier
                                    ObjectArrayList twinLabels = ccs.getTwinTuples(j - 1, j);
                                    if (twinLabels != null)
                                    {
                                        for (int t = 0; t < twinLabels.size(); t++)
                                        {
                                            Tuple tuple = (Tuple) twinLabels.get(t);
                                            
                                            //  Because current tier is right
                                            if (coupleValue.equals(tuple.fromRightTier))
                                            {
                                                if (twinLabelPrinted)
                                                {
                                                    builder.append(", ");
                                                }
                                                else
                                                {
                                                    builder.append(" ... ");
                                                }
                                                builder.append(tuple.labelIndex);
                                                
                                                twinLabelPrinted = true;
                                            }
                                        }
                                    }
                                }
                                
                                if (j < tiers.size() - 1)
                                {
                                    //  Should be next tier
                                    ObjectArrayList twinLabels = ccs.getTwinTuples(j, j + 1);
                                    if (twinLabels != null)
                                    {
                                        for (int t = 0; t < twinLabels.size(); t++)
                                        {
                                            Tuple tuple = (Tuple) twinLabels.get(t);
                                            
                                            //  Because current tier is left
                                            if (coupleValue.equals(tuple.fromLeftTier))
                                            {
                                                if (twinLabelPrinted)
                                                {
                                                    builder.append(", ");
                                                }
                                                else
                                                {
                                                    builder.append(" ... ");
                                                }
                                                builder.append(tuple.labelIndex);
                                                
                                                twinLabelPrinted = true;
                                            }
                                        }
                                    }
                                }
                            }
                            else
                            {
                                if (!valueOfLastVarPrinted)
                                {
                                    builder.append(spaces.substring(0, getLegendName(varName).length() + 1));
                                }
                            }
                        }
                        builder.append('\n');
                    }
                }
            }
        }
        
        builder.append("VarCount: "
                       + ccs.getPermutation().size()
                       + "; ClausesCount: "
                       + ccs.getClausesCount()
                       + "; TiersCount: "
                       + ccs.getTiers().size());
        return builder;
    }

   private static int getAName(int tierIndex, IPermutation permutation)
   {
       return permutation.get(tierIndex);
   }

   private static int getBName(int tierIndex, IPermutation permutation)
   {
       return permutation.get(tierIndex + 1);
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

    public static void prettyPrint(ISystem system)
    {
        for (int i = 0; i < system.getCCSs().size(); i++)
        {
            ICompactCouplesStructure ccs = (ICompactCouplesStructure) system.getCCSs().get(i);

            prettyPrint(ccs, system.getCIC());
        }
    }

    public static ObjectArrayList createCCS(ObjectArrayList cts)
    {
        ObjectArrayList ccss = new ObjectArrayList(cts.size());
        for (int i = 0; i < cts.size(); i++)
        {
            ccss.add(new SimpleCompactCouplesStructure(
                    (ICompactTripletsStructure) cts.get(i)));
        }
        return ccss;
    }

    public static boolean evaluate(ITabularFormula formula, IColumnsInversionControl cic)
    {
        boolean result = true;
        for (int j = 0; j < formula.getTiers().size(); j++)
        {
            for (ITripletValue tiplet : formula.getTier(j))
            {
                ITripletPermutation permutation = formula.getTier(j);
                
                boolean aValue = getValueFromCIC(cic, permutation.getAName());
                boolean bValue = getValueFromCIC(cic, permutation.getBName());
                boolean cValue = getValueFromCIC(cic, permutation.getCName());

                if (tiplet.isNotA()) aValue = !aValue;
                if (tiplet.isNotB()) bValue = !bValue;
                if (tiplet.isNotC()) cValue = !cValue;

                result = result && (aValue || bValue || cValue);
                
                if (!result)
                {
                    return result;
                }
            }
        }
        return result;
    }

    private static boolean getValueFromCIC(IColumnsInversionControl cic, int varName)
    {
        Value value = cic.getFixedValue(varName);
        return value == Value.AllNegative ? true : false;
    }

    public static void printVariablesSideBySide(ISystem system, int indexA, int indexB)
    {
        IPermutation permutation = system.getCIC().getPermutation();
        for (int i = 0; i < permutation.size(); i++)
        {
            int varName = permutation.get(i);
            String[] lines1 = buildPrettyOutput(
                    (ICompactCouplesStructure) system.getCCSs().get(indexA),
                    (IColumnsInversionControl) system.getCICs().get(indexA),
                    varName)
                    .toString()
                    .split("\n");
            
            String[] lines2 = buildPrettyOutput(
                    (ICompactCouplesStructure) system.getCCSs().get(indexB),
                    (IColumnsInversionControl) system.getCICs().get(indexB),
                    varName)
                    .toString()
                    .split("\n");
            
            StringBuilder builder = new StringBuilder();
            
            int maxLineLength = lines1[0].length();
            for (int j = 1; j < lines1.length; j++)
            {
                if (maxLineLength < lines1[j].length())
                {
                    maxLineLength = lines1[j].length();
                }
            }
            
            String spaces = getString(' ', maxLineLength);
            
            for (int j = 0; j < Math.max(lines1.length, lines2.length); j++)
            {
                builder
                    .append(j < lines1.length ? lines1[j] : "")
                    .append(spaces.substring(0, maxLineLength - (j < lines1.length ? lines1[j].length() : 0)))
                    .append("     ")
                    .append(j < lines2.length ? lines2[j] : "")
                    .append("\n");
            }
            
            LOGGER.info("-------- varName: {}\n\n{}", varName, builder);
        }
    }
}
