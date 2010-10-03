package com.anjlab.sat3;

public class JoinInfo
{
    public int concatenationPower;
    public ITripletPermutation targetPermutation;
    public IJoinMethod joinMethod;
    public String rule;

    public JoinInfo(IJoinMethod method)
    {
        joinMethod = method;
        concatenationPower = -1;
    }
}
