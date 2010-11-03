package com.anjlab.sat3;

import java.io.IOException;
import java.io.InputStream;

public interface IFormulaReader
{

    public abstract ITabularFormula readFormula(InputStream input)
            throws IOException;

}