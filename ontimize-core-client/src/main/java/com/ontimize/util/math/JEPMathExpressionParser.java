package com.ontimize.util.math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

public class JEPMathExpressionParser implements MathExpressionParser {

	private static final Logger logger = LoggerFactory.getLogger(JEPMathExpressionParser.class);

	private Object parser;

	private final String parserClass = "org.nfunk.jep.JEP";

	public JEPMathExpressionParser() {
		try {
			this.parser = new org.nfunk.jep.JEP();
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				JEPMathExpressionParser.logger
				.debug("Error creating JEP parser. Check whether jep library is included in build path.", e);
			}
		}
	}

	@Override
	public void addVariable(final String arg0, final double arg1) {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).addVariable(arg0, arg1);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void parseExpression(final String expression) {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).parseExpression(expression);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void addVariableAsObject(final String var, final Object o) {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).addVariable(var, o);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public boolean hasError() {
		try {
			if (this.parser != null) {
				return ((org.nfunk.jep.JEP) this.parser).hasError();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
		return true;
	}

	@Override
	public Object getValueAsObject() {
		try {
			if (this.parser != null) {
				return ((org.nfunk.jep.JEP) this.parser).getValueAsObject();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
		return null;
	}

	@Override
	public String getErrorInfo() {
		try {
			if (this.parser != null) {
				return ((org.nfunk.jep.JEP) this.parser).getErrorInfo();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
		return null;
	}

	@Override
	public void addFunction(final String key, final Object function) {
		try {
			if ((this.parser != null) && (function != null)
					&& (function instanceof org.nfunk.jep.function.PostfixMathCommandI)) {
				((org.nfunk.jep.JEP) this.parser).addFunction(key,
						(org.nfunk.jep.function.PostfixMathCommandI) function);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void addStandardFunctions() {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).addStandardFunctions();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void addStandardConstants() {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).addStandardConstants();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void removeVariable(final String var) {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).removeVariable(var);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public void setTraverse(final boolean value) {
		try {
			if (this.parser != null) {
				((org.nfunk.jep.JEP) this.parser).setTraverse(value);
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
	}

	@Override
	public double getValue() {
		try {
			if (this.parser != null) {
				return ((org.nfunk.jep.JEP) this.parser).getValue();
			}
		} catch (final Exception e) {
			JEPMathExpressionParser.logger.error(null, e);
		}
		return 0D;
	}

	public Object getParser() {
		return this.parser;
	}

	public String getParserClass() {
		return this.parserClass;
	}

}
