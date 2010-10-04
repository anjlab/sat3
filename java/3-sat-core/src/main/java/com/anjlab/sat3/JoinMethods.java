package com.anjlab.sat3;


public class JoinMethods
{
	private static final IJoinMethod[] joinMethods = 
						new IJoinMethod[]
		                {
							//	The order matters (see comments in Helper#getJoinCandidates() and Helper#pickAJoin()
							new Join3AsIs(),
					        new JoinBetweenTiers2(),
					        new JoinLeft2(),
					        new JoinRight2(),
					        new JoinLeft1(),
					        new JoinRight1(),
					        new JoinRight0(),
					    };  
	
    public static IJoinMethod[] getMethods()
    {
        return joinMethods;
    }
}

class Join3AsIs implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        int aIndex = formula.getPermutation().indexOf(tier.getAName());
        int bIndex = formula.getPermutation().indexOf(tier.getBName());
        int cIndex = formula.getPermutation().indexOf(tier.getCName());

        if (aIndex >= 0 && bIndex >= 0 && cIndex >= 0)
        {
            if (follows(aIndex, bIndex, cIndex))
            {
                result.rule = "1";
                result.concatenationPower = 3;
                result.targetPermutation = tier;
            }
            else if (follows(bIndex, aIndex, cIndex))
            {
                result.rule = "2";
                result.concatenationPower = 3;
                result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getAName(), tier.getCName());
            }
            else if (follows(aIndex, cIndex, bIndex))
            {
                result.rule = "3";
                result.concatenationPower = 3;
                result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getCName(), tier.getBName());
            }
            else if (follows(cIndex, aIndex, bIndex))
            {
                result.rule = "4";
                result.concatenationPower = 3;
                result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getAName(), tier.getBName());
            }
            else if (follows(bIndex, cIndex, aIndex))
            {
                result.rule = "5";
                result.concatenationPower = 3;
                result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getCName(), tier.getAName());
            }
            else if (follows(cIndex, bIndex, aIndex))
            {
                result.rule = "6";
                result.concatenationPower = 3;
                result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getBName(), tier.getAName());
            }
        }

        return result;
    }

    private static boolean follows(int aIndex, int bIndex, int cIndex)
    {
        return follows(aIndex, bIndex) && follows(bIndex, cIndex);
    }

    private static boolean follows(int aIndex, int bIndex)
    {
        return aIndex == bIndex - 1;
    }
}

class JoinLeft1 implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        if (formula.getPermutation().get(0) == tier.getCName()
            && !formula.getPermutation().contains(tier.getBName())
            && !formula.getPermutation().contains(tier.getAName()))
        {
            result.rule = "1";
            result.concatenationPower = 1;
            result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getBName(), tier.getCName());
        }
        else if (formula.getPermutation().get(0) == tier.getBName()
            && !formula.getPermutation().contains(tier.getAName())
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "2";
            result.concatenationPower = 1;
            result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getCName(), tier.getBName());
        }
        else if (formula.getPermutation().get(0) == tier.getAName()
            && !formula.getPermutation().contains(tier.getBName())
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "3";
            result.concatenationPower = 1;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getCName(), tier.getAName());
        }

        return result;
    }
}

class JoinLeft2 implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        if (formula.getPermutation().get(0) == tier.getBName()
            && formula.getPermutation().get(1) == tier.getCName()
            && !formula.getPermutation().contains(tier.getAName()))
        {
            result.rule = "1";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getBName(), tier.getCName());
        }
        else if (formula.getPermutation().get(0) == tier.getCName()
            && formula.getPermutation().get(1) == tier.getBName()
            && !formula.getPermutation().contains(tier.getAName()))
        {
            result.rule = "2";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getCName(), tier.getBName());
        }
        else if (formula.getPermutation().get(0) == tier.getAName()
            && formula.getPermutation().get(1) == tier.getBName()
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "3";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getAName(), tier.getBName());
        }
        else if (formula.getPermutation().get(0) == tier.getBName()
            && formula.getPermutation().get(1) == tier.getAName()
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "4";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getBName(), tier.getAName());
        }
        else if (formula.getPermutation().get(0) == tier.getAName()
            && formula.getPermutation().get(1) == tier.getCName()
            && !formula.getPermutation().contains(tier.getBName()))
        {
            result.rule = "5";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getAName(), tier.getCName());
        }
        else if (formula.getPermutation().get(0) == tier.getCName()
            && formula.getPermutation().get(1) == tier.getAName()
            && !formula.getPermutation().contains(tier.getBName()))
        {
            result.rule = "6";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getCName(), tier.getAName());
        }

        return result;
    }
}

class JoinRight2 implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getAName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getBName()
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "1";
            result.concatenationPower = 2;
            result.targetPermutation = tier;
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getBName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getAName()
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "2";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getAName(), tier.getCName());
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getBName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getCName()
            && !formula.getPermutation().contains(tier.getAName()))
        {
            result.rule = "3";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getCName(), tier.getAName());
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getCName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getBName()
            && !formula.getPermutation().contains(tier.getAName()))
        {
            result.rule = "4";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getBName(), tier.getAName());
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getAName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getCName()
            && !formula.getPermutation().contains(tier.getBName()))
        {
            result.rule = "5";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getCName(), tier.getBName());
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 2) == tier.getCName()
            && formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getAName()
            && !formula.getPermutation().contains(tier.getBName()))
        {
            result.rule = "6";
            result.concatenationPower = 2;
            result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getAName(), tier.getBName());
        }

        return result;
    }
}

class JoinRight1 implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        if (formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getAName()
            && !formula.getPermutation().contains(tier.getBName())
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "1";
            result.concatenationPower = 1;
            result.targetPermutation = tier;
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getBName()
            && !formula.getPermutation().contains(tier.getAName())
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "2";
            result.concatenationPower = 1;
            result.targetPermutation = new SimpleTripletPermutation(tier.getBName(), tier.getAName(), tier.getCName());
        }
        else if (formula.getPermutation().get(formula.getPermutation().size() - 1) == tier.getCName()
            && !formula.getPermutation().contains(tier.getAName())
            && !formula.getPermutation().contains(tier.getBName()))
        {
            result.rule = "3";
            result.concatenationPower = 1;
            result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getAName(), tier.getBName());
        }

        return result;
    }
}

class JoinRight0 implements IJoinMethod
{
    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        if (!formula.getPermutation().contains(tier.getAName())
            && !formula.getPermutation().contains(tier.getBName())
            && !formula.getPermutation().contains(tier.getCName()))
        {
            result.rule = "1";
            result.concatenationPower = 0;
            result.targetPermutation = tier;
        }

        return result;
    }
}

class JoinBetweenTiers2 implements IJoinMethod
{
    private static boolean follows(int aIndex, int bIndex)
    {
        return aIndex == bIndex - 1;
    }

    public JoinInfo getJoinInfo(ITabularFormula formula, ITier tier)
    {
        JoinInfo result = new JoinInfo(this);

        int aIndex = formula.getPermutation().indexOf(tier.getAName());
        int bIndex = formula.getPermutation().indexOf(tier.getBName());
        int cIndex = formula.getPermutation().indexOf(tier.getCName());
        
        if (follows(aIndex, bIndex) && cIndex < 0)
        {
        	GenericArrayList<ITier> aTiers = findTiersWithVariable(formula.getTiers(), tier.getAName());
        	GenericArrayList<ITier> bTiers = findTiersWithVariable(formula.getTiers(), tier.getBName());

            ITier aTier = aTiers.size() > 0 ? aTiers.get(0) : null;
            ITier bTier = bTiers.size() > 0 ? bTiers.get(0) : null;

            if (aTiers.size() == 1 && bTiers.size() == 1 
                && aTier != null && bTier != null && aTier != bTier)
            {
                result.rule = "1";
                result.concatenationPower = 2;
                result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getCName(), tier.getBName());
            }
        }
        else if (follows(cIndex, bIndex) && aIndex < 0)
        {
        	GenericArrayList<ITier> cTiers = findTiersWithVariable(formula.getTiers(), tier.getCName());
        	GenericArrayList<ITier> bTiers = findTiersWithVariable(formula.getTiers(), tier.getBName());

            ITier cTier = cTiers.size() > 0 ? cTiers.get(0) : null;
            ITier bTier = bTiers.size() > 0 ? bTiers.get(0) : null;

            if (cTiers.size() == 1 && bTiers.size() == 1
                && cTier != null && bTier != null && cTier != bTier)
            {
                result.rule = "2";
                result.concatenationPower = 2;
                result.targetPermutation = new SimpleTripletPermutation(tier.getCName(), tier.getAName(), tier.getBName());
            }
        }
        else if (follows(aIndex, cIndex) && bIndex < 0)
        {
            GenericArrayList<ITier> aTiers = findTiersWithVariable(formula.getTiers(), tier.getAName());
            GenericArrayList<ITier> cTiers = findTiersWithVariable(formula.getTiers(), tier.getCName());

            ITier aTier = aTiers.size() > 0 ? aTiers.get(0) : null;
            ITier cTier = cTiers.size() > 0 ? cTiers.get(0) : null;

            if (aTiers.size() == 1 && cTiers.size() == 1
                && aTier != null && cTier != null && aTier != cTier)
            {
                result.rule = "3";
                result.concatenationPower = 2;
                result.targetPermutation = new SimpleTripletPermutation(tier.getAName(), tier.getBName(), tier.getCName());
            }
        }

        return result;
    }

	private GenericArrayList<ITier> findTiersWithVariable(GenericArrayList<ITier> tiers, int varName) {
		GenericArrayList<ITier> result = new GenericArrayList<ITier>();
		for (int i = 0; i < tiers.size(); i++)
		{
			ITier tier = tiers.get(i);
			if (tier.hasVariable(varName))
			{
				result.add(tier);
			}
		}
		return result;
	}
}