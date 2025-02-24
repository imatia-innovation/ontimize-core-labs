package com.ontimize.gui.field;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;

/**
 * This class supports a concatenation of data fields obtained with
 * {@link DataField}{@link #getText()}.
 * <p>
 *
 * @author Imatia Innovation
 */
public class CalculatedTextDataField extends TextDataField implements ValueChangeListener {

	private static final Logger logger = LoggerFactory.getLogger(CalculatedTextDataField.class);

	/**
	 * The separator string. By default, " ".
	 */
	protected String separator = " ";

	/**
	 * The five attribute fields List instance.
	 */
	protected List attributeFields = new Vector(5);

	/**
	 * The class constructor.
	 * <p>
	 * @param parameters the Map with parameters
	 * @throws Exception when Exception occurs
	 *
	 *         /** Inits parameters.
	 *         <p>
	 * @param parameters the Map with parameters. The next parameters are added:
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
	 *
	 *        <tr>
	 *        <td>values</td>
	 *        <td><i>value1;value2;...;valuen</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute values.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>separator</td>
	 *        <td></td>
	 *        <td>" "</td>
	 *        <td>no</td>
	 *        <td>Indicates the type of separator.</td>
	 *        </tr>
	 *
	 *        </Table>
	 */
	public CalculatedTextDataField(final Map parameters) throws Exception {
		super(parameters);
		final Object separator = parameters.get("separator");
		if (separator != null) {
			this.separator = separator.toString();
		}
		final Object values = parameters.get("values");
		if ((values == null) || values.equals("")) {
			throw new Exception(this.getClass().toString() + ": Parameter 'values' is mandatory");
		} else {
			final StringTokenizer st = new StringTokenizer(values.toString(), ";");
			while (st.hasMoreTokens()) {
				this.attributeFields.add(st.nextToken());
			}
		}
	}

	@Override
	public boolean isModifiable() {
		return false;
	}

	@Override
	public void setValue(final Object value) {
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
		if (this.parentForm != null) {
			for (int i = 0; i < this.attributeFields.size(); i++) {
				final Object atr = this.attributeFields.get(i);
				if (atr != null) {
					final DataComponent c = f.getDataFieldReference(atr.toString());
					if ((c != null) && (c instanceof DataField)) {
						((DataField) c).addValueChangeListener(this);
					}
				}
			}
		} else {
			CalculatedTextDataField.logger.debug(this.getClass().toString() + " Parent form is NULL");
		}
	}

	@Override
	public void valueChanged(final ValueEvent e) {
		final StringBuilder sb = new StringBuilder("");
		if (this.parentForm != null) {
			for (int i = 0; i < this.attributeFields.size(); i++) {
				final Object oAttr = this.attributeFields.get(i);
				if (oAttr != null) {
					final DataComponent c = this.parentForm.getDataFieldReference(oAttr.toString());
					if (c instanceof DataField) {
						if (c != null) {
							if (c.isEmpty()) {
								if (ApplicationManager.DEBUG) {
									CalculatedTextDataField.logger.debug(
											this.getClass().toString() + " : Field " + c.getAttribute() + " is empty");
								}
								continue;
							} else {
								final String text = ((DataField) c).getText();
								if (i < (this.attributeFields.size() - 1)) {
									if (text != null) {
										sb.append(text);
										sb.append(this.separator);
									}
								} else {
									if (text != null) {
										sb.append(text);
									}
								}
							}
						}
					}
				}
			}
		}
		if (ApplicationManager.DEBUG) {
			CalculatedTextDataField.logger.debug(
					this.getClass().toString() + " : ValueChanged: " + e.getNewValue() + " before: " + e.getOldValue());
		}
		((JTextField) this.dataField).setText(sb.toString());
	}

}
