package com.ontimize.gui.field;

import java.util.Hashtable;
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
 * Class for calculated currency fields with a specific operator parameter.
 * <p>
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
public class CalculatedCurrencyDataField extends CurrencyDataField implements ValueChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(CalculatedCurrencyDataField.class);

	/**
	 * A reference for calculated expression. By default, null.
	 */
	protected String expression = null;

	/**
	 * An instance of attribute fields.
	 */
	protected List attributeFields = new Vector();

	/**
	 * An instance of Java package for parsing and evaluating mathematical expressions.
	 */
	protected MathExpressionParser parser = MathExpressionParserFactory.getInstance();

	/**
	 * An instance of 2 x 2 List for required fields.
	 */
	protected List requiredFields = new Vector(2, 2);

	/**
	 * Class constructor.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td>Fields used to calculate this field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>expression</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The mathematical expression to use for field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>required</td>
	 *        <td><i>required1;required2;...;requiredn</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The required fields to obtain the calculated field. When a required field from list is
	 *        not present, the calculated field will be empty.</td>
	 *        </tr>
	 *        </Table>
	 */
	public CalculatedCurrencyDataField(final Map parameters) {
		super(parameters);
		final Map custom = JEPUtils.getCustomFunctions();
		for (final String key : ((Map<String, ?>) custom).keySet()) {
			try {
				this.parser.addFunction(key, custom.get(key));
			} catch (final java.lang.NoSuchMethodError e) {
				CalculatedCurrencyDataField.logger.error(e.getMessage(), e);
			}
		}
		this.dataField.setEnabled(false);
		this.updateBackgroundColor(false);
		this.parser.addStandardFunctions();
		this.parser.addStandardConstants();
		final Object attributes = parameters.get("source");
		if (attributes != null) {
			final StringTokenizer st = new StringTokenizer(attributes.toString(), ";");
			while (st.hasMoreTokens()) {
				final String s = st.nextToken();
				this.attributeFields.add(this.attributeFields.size(), s);
				this.parser.addVariable(s, 0.0);
			}
		} else {
			CalculatedCurrencyDataField.logger
			.debug(this.getClass().toString() + " : 'source' parameter hasn't been found");
		}
		final Object oExpression = parameters.get("expression");
		if (oExpression != null) {
			this.expression = oExpression.toString();
			this.parser.parseExpression(this.expression);
		} else {
			CalculatedCurrencyDataField.logger
			.debug(this.getClass().toString() + " : 'expression' parameter hasn't been found ");
		}

		final Object required = parameters.get("required");
		if (required != null) {
			final StringTokenizer st = new StringTokenizer(required.toString(), ";");
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				if (this.attributeFields.contains(token)) {
					this.requiredFields.add(token);
				}
			}
		}
	}

	@Override
	public boolean isModifiable() {
		return false;
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
			// Register this component as listener of the others
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
				final Object atr = this.attributeFields.get(i);
				if (atr != null) {
					final DataComponent c = this.parentForm.getDataFieldReference(atr.toString());
					if (c instanceof DataField) {
						if (c != null) {
							if (c.isEmpty()) {
								if (this.requiredFields.contains(atr)) {
									this.deleteData();
									return;
								}
								if (ApplicationManager.DEBUG) {
									CalculatedCurrencyDataField.logger.debug(this.getClass().toString()
											+ " : Data field " + c.getAttribute() + " is empty");
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
			CalculatedCurrencyDataField.logger.debug(
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
	 * Recalculate the values.
	 * <p>
	 * @param values the Map to recalculate the values
	 */
	protected void recalculate(final Map<?, ?> values) {
		for (final Entry<?, ?> entry : values.entrySet()) {
			final Object oAttribute = entry.getKey();
			final String sAttr = oAttribute.toString();
			final Object oValue = entry.getValue();
			if ((oValue != null) && (oValue instanceof Number)) {
				this.parser.addVariableAsObject(sAttr, new Double(((Number) oValue).doubleValue()));
				if (ApplicationManager.DEBUG) {
					CalculatedCurrencyDataField.logger.debug(this.getClass().toString() + " : " + this.getAttribute()
					+ " AddVariableAsObject: " + sAttr + " -> " + oValue);
				}
			} else {
				if ((oValue != null) && (oValue instanceof String)) {
					try {
						this.parser.addVariableAsObject(sAttr,
								new Double(Integer.valueOf((String) oValue).doubleValue()));
					} catch (final Exception e) {
						this.parser.addVariableAsObject(sAttr, oValue);
						if (ApplicationManager.DEBUG) {
							CalculatedCurrencyDataField.logger.error(null, e);
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
				if (ApplicationManager.DEBUG) {
					CalculatedCurrencyDataField.logger.debug(this.getClass().toString() + " : " + this.getAttribute()
					+ " AddVariableAsObject: " + sAttr + " -> 0 " + oValue);
				}
			}
		}
		if (this.parser.hasError()) {
			CalculatedCurrencyDataField.logger.debug(this.getClass().toString() + " " + this.attribute + " "
					+ this.expression + " -> " + this.parser.getErrorInfo());
		}
		final Object oValue = this.parser.getValueAsObject();
		if (ApplicationManager.DEBUG) {
			CalculatedCurrencyDataField.logger.debug(this.getClass().toString() + ": recalculated value: " + oValue);
		}
		super.setValue(oValue);
		if (this.parser.hasError()) {
			CalculatedCurrencyDataField.logger.debug(this.getClass().toString() + " " + this.attribute + " "
					+ this.expression + " -> " + this.parser.getErrorInfo());
		}
	}

}
