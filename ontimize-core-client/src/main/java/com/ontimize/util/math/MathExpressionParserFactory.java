package com.ontimize.util.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

public class MathExpressionParserFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MathExpressionParserFactory.class);

    public static final String MATH_EXPRESSION_PARSER_PROPERTY = "com.ontimize.util.math.MathExpressionParser";

    public static MathExpressionParser getInstance() {
        try {
            final String type = System.getProperty(MathExpressionParserFactory.MATH_EXPRESSION_PARSER_PROPERTY);
			if (MathExpressionParser.EXP4J.equalsIgnoreCase(type)) {
				return new Exp4jMathExpressionParser();
            } else {
				return checkExistingMathParsersInClasspath();
            }
        } catch (final Exception e) {
            MathExpressionParserFactory.LOGGER
                .error(MathExpressionParserFactory.class.getName() + ": No math parser library found");
            if (ApplicationManager.DEBUG) {
                MathExpressionParserFactory.LOGGER.error(null, e);
            }
        } catch (final Error e) {
            MathExpressionParserFactory.LOGGER
                .error(MathExpressionParserFactory.class.getName() + ": No math parser library found");
            if (ApplicationManager.DEBUG) {
                MathExpressionParserFactory.LOGGER.error(null, e);
            }
        }
        return null;
    }

	private static MathExpressionParser checkExistingMathParsersInClasspath() {
		if (isClassAvailable("net.objecthunter.exp4j.ExpressionBuilder")) {
			return new Exp4jMathExpressionParser();
		}
		throw new IllegalArgumentException("No suitable math parser found");
	}

	private static boolean isClassAvailable(final String string) {
		try {
			Class.forName(string);
			return true;
		} catch (final ClassNotFoundException err) {
			return false;
		}
	}

}
