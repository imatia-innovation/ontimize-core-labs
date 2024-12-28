package com.ontimize.gui.field;

import java.util.Map;

import com.ontimize.jee.common.db.NullValue;

/**
 * This class implements a data label.
 * <p>
 *
 * @author Imatia Innovation
 */
public class DataLabel extends Label implements DataComponent {

	/**
	 * The condition of required. By default, false.
	 */
	protected boolean required = false;

	/**
	 * The condition to show. By default, true.
	 */
	protected boolean show = true;

	/**
	 * The condition to modify. By default, true.
	 */
	protected boolean modificable = true;

	/**
	 * The text without data reference. By default, null.
	 */
	protected String textWithoutData = null;

	/**
	 * The class constructor. Calls to <code>super()</code> and checks required attribute parameter.
	 * <p>
	 * @param parameters the Map with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute to manage the label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>nodatatext</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text showed in field when field has no data.</td>
	 *        </tr>
	 *
	 */
	public DataLabel(final Map parameters) {
		super(parameters);
		if (this.attribute == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'attr' parameter is required ");
		}
		final Object oWithoutData = parameters.get("nodatatext");
		if (oWithoutData != null) {
			this.textWithoutData = oWithoutData.toString();
		}
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.VARCHAR;
	}

	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.setText(value.toString());
	}

	@Override
	public String getLabelComponentText() {
		return "";
	}

	@Override
	public Object getValue() {
		return this.textLabel;
	}

	@Override
	public boolean isHidden() {
		return !this.show;
	}

	@Override
	public void setModifiable(final boolean m) {
		this.modificable = m;
	}

	@Override
	public boolean isModifiable() {
		return this.modificable;
	}

	@Override
	public boolean isEmpty() {
		final boolean e = "".equals(this.getText()) ? true : false;
		return e;
	}

	@Override
	public void deleteData() {
		if (this.textWithoutData == null) {
			this.setText("");
		} else {
			this.setText(this.textWithoutData);
		}
	}

}
