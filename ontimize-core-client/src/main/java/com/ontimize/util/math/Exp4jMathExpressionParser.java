package com.ontimize.util.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

public class Exp4jMathExpressionParser implements MathExpressionParser {

	private static final Logger		logger	= LoggerFactory.getLogger(Exp4jMathExpressionParser.class);

	protected ExpressionBuilder		expressionBuilder;
	protected String				errorInfo;
	protected boolean				error;
	protected Map<String, Double>	variables;
	protected Map<String, Function>	functions;

	public Exp4jMathExpressionParser() {
		super();
		errorInfo = null;
		error = false;
		variables = new HashMap<>();
		functions = new HashMap<>();
	}

	@Override
	public void parseExpression(final String expression) {
		try {
			errorInfo = null;
			error = false;
			this.variables.clear();
			this.expressionBuilder = new ExpressionBuilder(expression);
		} catch (final Exception exc) {
			logger.trace(null, exc);
			this.error = true;
			this.errorInfo = exc.toString();
		}
	}

	@Override
	public void addVariable(final String var, final double value) {
		this.variables.put(var, value);
	}

	@Override
	public void addVariableAsObject(final String var, final Object o) {
		try {
			if ((o == null) || !(o instanceof Number)) {
				return;
			}
			final Number current = (Number) o;
			this.addVariable(var, current.doubleValue());
		} catch (final Exception err) {
			logger.error(null, err);
			this.error = true;
			this.errorInfo = err.toString();
		}
	}

	@Override
	public boolean hasError() {
		return this.error;
	}

	@Override
	public String getErrorInfo() {
		return this.errorInfo;
	}

	@Override
	public double getValue() {
		try {
			return this.expressionBuilder
					.variables(this.variables.keySet())
					.functions(new ArrayList<>(functions.values()))
					.build()
					.setVariables(variables)
					.evaluate();
		} catch (final Exception exc) {
			logger.trace(null, exc);
			this.error = true;
			this.errorInfo = exc.toString();
			return 0;
		}
	}

	@Override
	public Object getValueAsObject() {
		try {
			final double d = this.getValue();
			if (this.error) {
				return null;
			} else {
				return Double.valueOf(d);
			}
		} catch (final Exception ex) {
			logger.trace(null, ex);
			this.error = true;
			this.errorInfo = ex.toString();
		}
		return null;
	}

	@Override
	public void addFunction(final String key, final Object function) {
		functions.put(key, (Function) function);
	}

	@Override
	public void addStandardFunctions() {
	}

	@Override
	public void addStandardConstants() {

	}

	@Override
	public void removeVariable(final String var) {
		this.variables.remove(var);
	}

	@Override
	public void setTraverse(final boolean value) {

	}

}
