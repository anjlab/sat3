package com.anjlab.sat3.v2;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.anjlab.sat3.Helper;
import com.anjlab.sat3.ICompactTripletsStructure;
import com.anjlab.sat3.ITabularFormula;
import com.anjlab.sat3.Value;

import cern.colt.list.ObjectArrayList;

public class TestSimpleCompactCouplesStructure
{
    @BeforeClass
    public static void beforeClass()
    {
        Helper.UsePrettyPrint = true;
        Helper2.UsePrettyPrint = true;
        Helper.UseUniversalVarNames = false;
        Helper2.UseUniversalVarNames = false;
    }

    @Test
    public void testCreateCCSFromCTSWithTwinLabels()
    {
        //  Examples taken from
        //  http://arxiv.org/pdf/1309.6078v1.pdf
        
        int a = 1, b = 2, c = 3, d = 4, e = 5, f = 6, g = 7, h = 8;
        
        ITabularFormula s1_table4 = Helper.createFormula(
                a, b, -c,
                -a, b, -c,
                b, -c, d,
                b, -c, -d,
                -c, d, -e,
                -c, -d, -e,
                d, -e, -f,
                -d, -e, f,
                -d, -e, -f,
                -e, f, -g,
                -e, -f, g,
                f, -g, -h,
                -f, g, h);
        
        Helper.prettyPrint(s1_table4);
        
        ICompactCouplesStructure g1_table7 =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) s1_table4);
        
        Helper2.prettyPrint(g1_table7);
        
        ITabularFormula s2_table4 = Helper.createFormula(
                h, g, b,
                -h, -g, b,
                g, b, -e,
                -g, b, -e,
                b, -e, a,
                b, -e, -a,
                -e, a, f,
                -e, a, -f,
                -e, -a, -f,
                a, f, -c,
                a, -f, -c,
                -a, -f, -c,
                f, -c, -d,
                -f, -c, d,
                -f, -c, -d);
        
        Helper.prettyPrint(s2_table4);
        
        ICompactCouplesStructure g2_table7 =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) s2_table4);
        
        Helper2.prettyPrint(g2_table7);
    }
    
    @Test
    public void testInverse()
    {
        ITabularFormula simpleCTF = Helper.createFormula(
                1, 2, 3,
                -1, 2, 3,
                -1, -2, -3,
                2, -3, 4);
        
        Helper.prettyPrint(simpleCTF);
        
        ICompactCouplesStructure ccs =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) simpleCTF);
        
        Helper2.prettyPrint(ccs);
        
        ObjectArrayList twins01 = ccs.getTwinTuples(0, 1);
        Assert.assertEquals(2, twins01.size());
        
        Tuple tuple0 = (Tuple) twins01.get(0);
        Assert.assertEquals(0, tuple0.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._00_instance, tuple0.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple0.fromRightTier);
        
        Tuple tuple1 = (Tuple) twins01.get(1);
        Assert.assertEquals(1, tuple1.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple1.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple1.fromRightTier);
        
        ObjectArrayList twins12 = ccs.getTwinTuples(1, 2);
        Assert.assertEquals(1, twins12.size());
        
        Tuple tuple2 = (Tuple) twins12.get(0);
        Assert.assertEquals(2, tuple2.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple2.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple2.fromRightTier);
        
        ccs.inverse(1);
        
        Helper2.prettyPrint(ccs);
        
        //  Inverse should update twins index
        
        //  Instances of Tuple are mutable, values should be updated in place
        Assert.assertEquals(0, tuple0.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple0.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple0.fromRightTier);
        
        Assert.assertEquals(1, tuple1.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._00_instance, tuple1.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple1.fromRightTier);
        
        Assert.assertEquals(2, tuple2.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple2.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple2.fromRightTier);
        
        ccs.inverse(2);
        
        Helper2.prettyPrint(ccs);
        
        Assert.assertEquals(0, tuple0.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple0.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple0.fromRightTier);
        
        Assert.assertEquals(1, tuple1.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple1.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple1.fromRightTier);
        
        Assert.assertEquals(2, tuple2.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple2.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple2.fromRightTier);
        
        ccs.inverse(3);
        
        Helper2.prettyPrint(ccs);
        
        Assert.assertEquals(0, tuple0.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple0.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple0.fromRightTier);
        
        Assert.assertEquals(1, tuple1.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple1.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple1.fromRightTier);
        
        Assert.assertEquals(2, tuple2.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._00_instance, tuple2.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._00_instance, tuple2.fromRightTier);
        
        ccs.inverse(4);
        
        Helper2.prettyPrint(ccs);
        
        Assert.assertEquals(0, tuple0.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._11_instance, tuple0.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple0.fromRightTier);
        
        Assert.assertEquals(1, tuple1.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple1.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._10_instance, tuple1.fromRightTier);
        
        Assert.assertEquals(2, tuple2.labelIndex);
        Assert.assertEquals(SimpleCoupleValueFactory._00_instance, tuple2.fromLeftTier);
        Assert.assertEquals(SimpleCoupleValueFactory._01_instance, tuple2.fromRightTier);
    }
    
    @Test
    public void testValueOf()
    {
        ITabularFormula simpleCTF = Helper.createFormula(
                1, 2, 3,
                -1, 2, -3,
                2, 3, -4,
                3, -4, 5);
        
        Helper.prettyPrint(simpleCTF);
        
        ICompactCouplesStructure ccs =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) simpleCTF);
        
        Helper2.prettyPrint(ccs);
        
        Assert.assertEquals(Value.Mixed, ccs.valueOf(1));
        Assert.assertEquals(Value.AllPlain, ccs.valueOf(2));
        Assert.assertEquals(Value.Mixed, ccs.valueOf(3));
        Assert.assertEquals(Value.AllNegative, ccs.valueOf(4));
        Assert.assertEquals(Value.AllPlain, ccs.valueOf(5));
    }
    
    @Test
    public void testTrySet()
    {
        //  Examples taken from
        //  http://arxiv.org/pdf/1309.6078v1.pdf
        
        int a = 1, b = 2, c = 3, d = 4, e = 5, f = 6, g = 7, h = 8;
        
        ITabularFormula s1_table4 = Helper.createFormula(
                a, b, -c,
                -a, b, -c,
                b, -c, d,
                b, -c, -d,
                -c, d, -e,
                -c, -d, -e,
                d, -e, -f,
                -d, -e, f,
                -d, -e, -f,
                -e, f, -g,
                -e, -f, g,
                f, -g, -h,
                -f, g, h);
        
        Helper.prettyPrint(s1_table4);
        
        ICompactCouplesStructure g1_table7 =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) s1_table4);
        
        Helper2.prettyPrint(g1_table7);
        
        IColumnsInversionControl cic =
                new SimpleColumnsInversionControl(g1_table7.getPermutation());
        
        g1_table7.trySet(1, Value.AllPlain, cic);
        
        Helper2.prettyPrint(g1_table7, cic);
        
        g1_table7.trySet(8, Value.AllPlain, cic);
        
        Helper2.prettyPrint(g1_table7, cic);
    }
    
    @Test
    public void testTrySet_case1()
    {
        SimpleCompactCouplesStructure ccs =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) Helper.createFormula(
                                1, 2, 3,
                                -1, 2, 3,
                                2, -3, 4,
                                3, 4, -5));
        
        IColumnsInversionControl cic = new SimpleColumnsInversionControl(
                ccs.getPermutation());
        
        cic.setPlain(2);
        cic.setPlain(4);
        
        Helper2.prettyPrint(ccs, cic);
        
        ccs.trySet(3, Value.AllPlain, cic);
        
        Helper2.prettyPrint(ccs, cic);
        
        Assert.assertTrue(cic.isConflicted(3));
    }
    
    @Test
    public void testTrySet_case2()
    {
        SimpleCompactCouplesStructure ccs =
                new SimpleCompactCouplesStructure(
                        (ICompactTripletsStructure) Helper.createFormula(
                                1, 2, 3,
                                1, -2, 3,
                                2, -3, 4,
                                3, 4, -5,
                                -3, 4, 5));
        
        IColumnsInversionControl cic = new SimpleColumnsInversionControl(
                ccs.getPermutation());
        
        cic.setPlain(2);
        cic.setPlain(4);
        
        Helper2.prettyPrint(ccs, cic);
        
        ccs.trySet(3, Value.AllPlain, cic);
        
        Helper2.prettyPrint(ccs, cic);
        
        Assert.assertTrue(cic.isConflicted(3));
    }
}
