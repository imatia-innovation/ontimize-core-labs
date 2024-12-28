package com.ontimize.gui.field;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.JEPUtils;
import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;

/**
 * This class implements a field whose value is defined from a mathematical operation between other
 * associated field values.
 * <p>
 * <b>Important NOTE:</b> <br>
 * Three math parsers are allowed according to three different math libraries: JEP, JEP3x, MESP. By
 * default, JEP are used. To change this performance, user must set the property:<br>
 *
 * For example, to set "mesp" library as parser:
 * <b>System.setProperty("com.ontimize.util.math.MathExpressionParser", "mesp");</b> Possible values
 * are:
 * <ul>
 * <li>"jep"
 * <li>"jep3x"
 * <li>"mesp"
 * </ul>
 *
 * @author Imatia Innovation
 */
public class CalculatedDataField extends RealDataField implements ValueChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(CalculatedDataField.class);

	/**
	 * A reference for the mathematical expression. By default, null.
	 */
	protected String expresion = null;

	/**
	 * An instance of attribute fields.
	 */
	protected List attributeFields = new Vector();

	/**
	 * An instance of parser for mathematical operation.
	 *
	 */
	protected MathExpressionParser parser = MathExpressionParserFactory.getInstance();

	/**
	 * A 2x2 List for required fields.
	 */
	protected List requiredFields = new Vector(2, 2);

	/**
	 * A reference for associated fields.
	 */
	protected String source = null;

	/**
	 * The class constructor. Calls to <code>super()</code> with <code>Hashtable</code> parameters and
	 * parses operation.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. New parameters are added:
	 *
	 *
	 *        <p>
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>source</td>
	 *        <td><i>field1;field2;...;fieldn</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The fields to calculate the field value</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>expression</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The mathematical expression to define the field value</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>requiredfields</td>
	 *        <td><i>required1;required2;...;requiredn</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The required fields. When a required field is not present, the final value for
	 *        calculated data field is not showed.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	public CalculatedDataField(final Map parameters) {
		super(parameters);
		final Map<?, ?> custom = JEPUtils.getCustomFunctions();
		for (final Entry<?, ?> entry : custom.entrySet()) {
			final String key = (String) entry.getKey();
			try {
				this.parser.addFunction(key, entry.getValue());
			} catch (final java.lang.NoSuchMethodError e) {
				CalculatedDataField.logger.error(e.getMessage(), e);
			}
		}
		this.dataField.setEnabled(false);
		this.updateBackgroundColor(false);
		this.parser.addStandardFunctions();
		this.parser.addStandardConstants();
		final Object oAttributes = parameters.get("source");
		if (oAttributes != null) {
			this.source = oAttributes.toString();
			final StringTokenizer st = new StringTokenizer(oAttributes.toString(), ";");
			while (st.hasMoreTokens()) {
				final String s = st.nextToken();
				this.attributeFields.add(this.attributeFields.size(), s);
				this.parser.addVariable(s, 0.0);
			}
		} else {
			CalculatedDataField.logger.debug(this.getClass().toString() + " : 'source' parameter hasn't been found");
		}

		final Object expression = parameters.get("expression");
		if (expression != null) {
			this.expresion = expression.toString();
			this.parser.parseExpression(this.expresion);
		} else {
			CalculatedDataField.logger
			.debug(this.getClass().toString() + " : 'expression' parameter hasn't been found");
		}

		Object required = parameters.get("required");
		if (parameters.containsKey("requiredfields")) {
			required = parameters.get("requiredfields");
		}
		if (required != null) {
			final StringTokenizer st = new StringTokenizer(required.toString(), ";");
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				if (this.attributeFields.contains(token)) {
					this.requiredFields.add(token);
				}
				this.required = false;
			}
		} else {
			this.required = false;
		}
	}

	@Override
	public boolean isModifiable() {
		return false;
	}

	/**
	 * Gets the source parameter.
	 * <p>
	 * @return the source parameter
	 */
	public String getSource() {
		return this.source;
	}

	/**
	 * Gets the expression parameter.
	 * <p>
	 * @return the expression
	 */
	public String getExpression() {
		return this.expresion;
	}

	/**
	 * Prepares the mathematical expression to parse.
	 * <p>
	 * @param source the fields of expression
	 * @param expression the mathematical expression
	 */
	public void setSourceExpression(final String source, final String expression) {
		try {
			if (source != null) {
				if (this.attributeFields != null) {
					for (final Iterator iter = this.attributeFields.iterator(); iter.hasNext();) {
						final String attr = (String) iter.next();
						final DataField cmp = (DataField) this.parentForm.getDataFieldReference(attr);
						if (cmp != null) {
							cmp.removeValueChangeListener(this);
						}
						this.parser.removeVariable(attr);
					}
					this.attributeFields = ApplicationManager.getTokensAt(source, ";");
					if (this.attributeFields != null) {
						for (final Iterator iter = this.attributeFields.iterator(); iter.hasNext();) {
							final String attr = (String) iter.next();
							final DataField cmp = (DataField) this.parentForm.getDataFieldReference(attr);
							if (cmp != null) {
								cmp.addValueChangeListener(this);
							}
							this.parser.addVariable(attr, 0.0);
						}
					}
				}
			}
			if (expression != null) {
				this.expresion = expression;
				this.parser.parseExpression(this.expresion);
			}
			this.recalculate(this.parentForm.getDataFieldValues(false));
		} catch (final Exception ex) {
			CalculatedDataField.logger.error(null, ex);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			this.updateBackgroundColor(false);
		}
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
		if (f != null) {
			for (int i = 0; i < this.attributeFields.size(); i++) {
				final Object atr = this.attributeFields.get(i);
				if (atr != null) {
					final DataComponent c = f.getDataFieldReference(atr.toString());
					if ((c != null) && (c instanceof DataField)) {
						((DataField) c).addValueChangeListener(this);
					}
				}
			}
		}
	}

	@Override
	public void valueChanged(final ValueEvent e) {
		final Map hValues = new Hashtable();
		if (this.parentForm != null) {
			for (int i = 0; i < this.attributeFields.size(); i++) {
				final Object oAttr = this.attributeFields.get(i);
				if (oAttr != null) {
					final DataComponent c = this.parentForm.getDataFieldReference(oAttr.toString());
					if (c instanceof DataField) {
						if (c != null) {
							if (c.isEmpty()) {
								if (this.requiredFields.contains(oAttr)) {
									this.deleteData();
									return;
								}
								if (ApplicationManager.DEBUG) {
									CalculatedDataField.logger
									.debug(this.getClass().toString() + " attribute: " + this.getAttribute()
									+ " : Field " + c.getAttribute() + " is empty");
								}
								hValues.put(c.getAttribute(), new Double(0.0));
								continue;
							} else {
								hValues.put(c.getAttribute(), c.getValue());
							}
						}
					}
				}
			}
		}
		if (ApplicationManager.DEBUG) {
			CalculatedDataField.logger.debug(
					this.getClass().toString() + " : ValueChanged: " + e.getNewValue() + " before: " + e.getOldValue());
		}
		this.recalculate(hValues);
	}

	@Override
	public void setValue(final Object value) {
	}

	@Override
	public boolean isModified() {
		return false;
	}

	/**
	 * Recalculates the value rebuilding the expression to parse it.
	 * <p>
	 * @param values the <code>Hashtable</code> with values to recalculate
	 */
	protected void recalculate(final Map<?, ?> values) {

		for (final Entry<?, ?> entry : values.entrySet()) {
			final Object oAttribute = entry.getKey();
			final String sAttr = oAttribute.toString();
			final Object oValue = entry.getValue();
			if ((oValue != null) && (oValue instanceof Number)) {
				this.parser.addVariableAsObject(sAttr, new Double(((Number) oValue).doubleValue()));
			} else {
				if ((oValue != null) && (oValue instanceof String)) {
					try {
						this.parser.addVariableAsObject(sAttr,
								new Double(Integer.valueOf((String) oValue).doubleValue()));
					} catch (final Exception e) {
						this.parser.addVariableAsObject(sAttr, oValue);
						if (ApplicationManager.DEBUG) {
							CalculatedDataField.logger.error(null, e);
						}
					}
				} else {
					if (oValue == null) {
						this.parser.addVariableAsObject(sAttr, new Double(0.0));
					} else if (oValue instanceof SearchValue) {
						this.parser.addVariableAsObject(sAttr, new Double(0.0));
					} else {
						this.parser.addVariableAsObject(sAttr, oValue);
					}
				}
			}
		}
		if (this.parser.hasError()) {
			CalculatedDataField.logger.debug(this.getClass().toString() + " " + this.attribute + " " + this.expresion
					+ " -> " + this.parser.getErrorInfo());
		}
		final Object oValue = this.parser.getValueAsObject();
		if (ApplicationManager.DEBUG) {
			CalculatedDataField.logger.debug(
					this.getClass().toString() + "attribute: " + this.getAttribute()
					+ ": Field values on which it depends: " + values + " \n    Expression: " + this.expresion);
		}
		super.setValue(oValue);
		if (this.parser.hasError()) {
			CalculatedDataField.logger.debug(this.getClass().toString() + " " + this.attribute + " " + this.expresion
					+ " -> " + this.parser.getErrorInfo());
		}
	}

}
