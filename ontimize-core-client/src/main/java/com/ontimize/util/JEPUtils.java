package com.ontimize.util;

import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;

public abstract class JEPUtils {

	private static final Logger logger = LoggerFactory.getLogger(JEPUtils.class);

	private static Map customFunctions = new Hashtable();

	public static class Round extends org.nfunk.jep.function.PostfixMathCommand {

		NumberFormat nf = NumberFormat.getInstance();

		public Round() {
			this.numberOfParameters = 2;
		}

		@Override
		public void run(final Stack stack) throws org.nfunk.jep.ParseException {
			this.checkStack(stack);

			final Object oDecimals = stack.pop();
			final Object oValue = stack.pop();
			final Object oNewValue = this.round(oValue, oDecimals);
			stack.push(oNewValue);

			return;
		}

		public Object round(final Object param1, final Object param2) throws org.nfunk.jep.ParseException {
			if (param1 instanceof Number) {
				int nDec = 0;
				if (param2 instanceof Number) {
					nDec = ((Number) param2).intValue();
				} else if (param2 instanceof String) {
					nDec = Integer.parseInt((String) param2);
				} else {
					throw new org.nfunk.jep.ParseException("Invalid parameter type " + param2);
				}
				this.nf.setMinimumFractionDigits(nDec);
				this.nf.setMaximumFractionDigits(nDec);
				try {
					final String s = this.nf.format(((Number) param1).doubleValue());
					final Number n = this.nf.parse(s);
					return n;
				} catch (final Exception e) {
					JEPUtils.logger.trace(null, e);
					throw new org.nfunk.jep.ParseException("round Error " + e.getMessage());
				}
			}

			throw new org.nfunk.jep.ParseException("Invalid parameter type");
		}

	}

	public static void registerCustomFunction(final String name, final Object function) {
		final String property = System.getProperty(MathExpressionParserFactory.MATH_EXPRESSION_PARSER_PROPERTY);
		if (MathExpressionParser.JEP.equalsIgnoreCase(property)) {
			try {
				if ((function != null) && (function instanceof org.nfunk.jep.function.PostfixMathCommandI)) {
					JEPUtils.customFunctions.put(name, function);
				}
			} catch (final Exception e) {
				JEPUtils.logger.error(null, e);
			}
		} else {
			try {
				if (function != null) {
					JEPUtils.customFunctions.put(name, function);
				}
			} catch (final Exception e) {
				JEPUtils.logger.error(null, e);
			}
		}
	}

	public static Map getCustomFunctions() {
		return ObjectTools.clone(JEPUtils.customFunctions);
	}

	static {
		final String property = System.getProperty(MathExpressionParserFactory.MATH_EXPRESSION_PARSER_PROPERTY);
		if (!MathExpressionParser.JEP3x.equalsIgnoreCase(property)) {
			try {
				JEPUtils.registerCustomFunction("round", new Round());
			} catch (final Throwable ex) {
				JEPUtils.logger.error(null, ex);
			}
		}
	}

}
