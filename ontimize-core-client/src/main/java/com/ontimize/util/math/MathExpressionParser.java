package com.ontimize.util.math;

public interface MathExpressionParser {

	public static final String	EXP4J	= "exp4j";

    public void addVariable(String var, double value);

    public void parseExpression(String expression);

    public void addVariableAsObject(String var, Object o);

    public boolean hasError();

    public Object getValueAsObject();

    public String getErrorInfo();

    public void addFunction(String key, Object function);

    public void addStandardFunctions();

    public void addStandardConstants();

    public void removeVariable(String var);

    public void setTraverse(boolean value);

    public double getValue();

}
