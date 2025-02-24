package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JComboBox;
import javax.swing.JList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.db.NullValue;

/**
 * This class implements a specific combo box indicated for components with only 'true' and 'false'
 * values.
 * <p>
 *
 * @author Imatia Innovation
 */
public class BooleanComboDataField extends ComboDataField {

	private static final Logger logger = LoggerFactory.getLogger(BooleanComboDataField.class);

	/**
	 * The key for '1' value.
	 */
	public static final Integer UNO = new Integer(1);

	/**
	 * The key for '0' value.
	 */
	public static final Integer ZERO = new Integer(0);

	/**
	 * The key for default true value. By default, yes.
	 */
	public static String DEFAULT_TRUEVALUE = "Yes";

	/**
	 * The key for default false value. By default, no.
	 */
	public static String DEFAULT_FALSEVALUE = "No";

	/**
	 * The return boolean. By default, false.
	 */
	protected boolean returnBoolean = false;

	/**
	 * The return string. By default, false.
	 */
	protected boolean returnString = false;

	/**
	 * The translate texts. By default, false.
	 */
	protected boolean translateTexts = false;

	/**
	 * The true value adaptation.
	 */
	protected String trueValue = BooleanComboDataField.DEFAULT_TRUEVALUE;

	/**
	 * The false value adaptation.
	 */
	protected String falseValue = BooleanComboDataField.DEFAULT_FALSEVALUE;

	private String translatedTrueValue = this.trueValue;

	private String translatedFalseValue = this.falseValue;

	private Color colorFalse = null;

	private Color colorTrue = null;

	/**
	 * This class implements a translate renderer for component.
	 *
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class TranslateRenderer extends DefaultCustomComboBoxRenderer {

		public static final String COMBO_RENDERER_LABEL_NAME = "ComboBox.listRenderer";

		@Override
		public String getName() {
			return COMBO_RENDERER_LABEL_NAME;
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			String text = null;
			if (value != null) {
				text = value.toString();
				if (BooleanComboDataField.this.translateTexts) {
					if (BooleanComboDataField.this.resources != null) {
						if (value.equals(BooleanComboDataField.this.trueValue)) {
							text = BooleanComboDataField.this.translatedTrueValue;
						} else if (value.equals(BooleanComboDataField.this.falseValue)) {
							text = BooleanComboDataField.this.translatedFalseValue;
						}
					}
				}
			}
			final Component c = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
			if ((c != null) && !isSelected) {
				if ((BooleanComboDataField.this.colorFalse != null) && (value != null)
						&& value.equals(BooleanComboDataField.this.falseValue)) {
					c.setForeground(BooleanComboDataField.this.colorFalse);
				} else if ((BooleanComboDataField.this.colorTrue != null) && (value != null)
						&& value.equals(BooleanComboDataField.this.trueValue)) {
					c.setForeground(BooleanComboDataField.this.colorTrue);
				}
			}
			if (index == -1) {
				c.setEnabled(BooleanComboDataField.this.isEnabled());
			}
			return c;
		}

	}

	/**
	 * The class constructor. Calls to <code>super</code> and inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	public BooleanComboDataField(final Map parameters) {
		super();
		this.init(parameters);
	}

	/**
	 * Initilizes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 *
	 *        <p>
	 *
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
	 *        <td>returnboolean</td>
	 *        <td><i>yes/no</td>
	 *        <td><code>INTEGER</code></td>
	 *        <td>no</td>
	 *        <td>Indicates whether returned value by <code>getValue()</code> is a boolean.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>returnstring</td>
	 *        <td><i>yes/no</td>
	 *        <td><code></code></td>
	 *        <td>no</td>
	 *        <td>Indicates whether returned value by <code>getValue()</code> is a combo selectable
	 *        option. This parameter has more priority than <code>returnboolean</code>.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>truevalue</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Text will be showed with true value.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>falsevalue</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Text will be showed with false value.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>translate</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether texts for true and false value will be translated. For a correct
	 *        translation, texts should not have spaces.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>falsecolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Color for false value.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>truecolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Color for true value.</td>
	 *        </tr>
	 *
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		final Object falsecolor = parameters.get("falsecolor");
		if (falsecolor != null) {
			try {
				this.colorFalse = ColorConstants.parseColor(falsecolor.toString());
			} catch (final Exception e) {
				BooleanComboDataField.logger.error(null, e);
			}
		}

		final Object truecolor = parameters.get("truecolor");
		if (truecolor != null) {
			try {
				this.colorTrue = ColorConstants.parseColor(truecolor.toString());
			} catch (final Exception e) {
				BooleanComboDataField.logger.error(null, e);
			}
		}

		final Object returnboolean = parameters.get("returnboolean");
		if (returnboolean != null) {
			if (returnboolean.equals("yes")) {
				this.returnBoolean = true;
			} else {
				this.returnBoolean = false;
			}
		} else {
			this.returnBoolean = false;
		}

		final Object returnstring = parameters.get("returnstring");
		if (returnstring != null) {
			if (returnstring.equals("yes")) {
				this.returnString = true;
			} else {
				this.returnString = false;
			}
		} else {
			this.returnString = false;
		}

		// Now values
		final Object truevalue = parameters.get("truevalue");
		if (truevalue != null) {
			this.trueValue = truevalue.toString();
			this.translatedTrueValue = this.trueValue;
		}

		final Object falsevalue = parameters.get("falsevalue");
		if (falsevalue != null) {
			this.falseValue = falsevalue.toString();
			this.translatedFalseValue = this.falseValue;
		}

		final Object translate = parameters.get("translate");
		if (translate != null) {
			if (translate.equals("yes")) {
				this.translateTexts = true;
			} else {
				this.translateTexts = false;
			}
		} else {
			this.translateTexts = false;
		}

		((JComboBox) this.dataField).setRenderer(new TranslateRenderer());
		((JComboBox) this.dataField).setModel(new CustomComboBoxModel(this.nullSelection));
		((JComboBox) this.dataField).addItem(this.trueValue);
		((JComboBox) this.dataField).addItem(this.falseValue);
		((JComboBox) this.dataField).setSelectedItem(this.falseValue);
		if ((this.colorFalse != null) || (this.colorTrue != null)) {
			((JComboBox) this.dataField).addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if ((((JComboBox) BooleanComboDataField.this.dataField).getSelectedItem() != null)
							&& ((JComboBox) BooleanComboDataField.this.dataField).getSelectedItem()
							.equals(BooleanComboDataField.this.trueValue)) {
						BooleanComboDataField.this.dataField
						.setForeground(BooleanComboDataField.this.colorTrue);
						BooleanComboDataField.this.dataField.repaint();
					} else if ((((JComboBox) BooleanComboDataField.this.dataField).getSelectedItem() != null)
							&& ((JComboBox) BooleanComboDataField.this.dataField)
							.getSelectedItem()
							.equals(BooleanComboDataField.this.falseValue)) {
						BooleanComboDataField.this.dataField
						.setForeground(BooleanComboDataField.this.colorFalse);
						BooleanComboDataField.this.dataField.repaint();
					}
				}
			});
		}
	}

	@Override
	protected void updateBackgroundColor() {
		super.updateBackgroundColor();

		if (!DataField.ASTERISK_REQUIRED_STYLE && this.enabled) {
			if ((((JComboBox) this.dataField).getSelectedItem() != null)
					&& ((JComboBox) this.dataField).getSelectedItem().equals(this.trueValue)) {
				this.dataField.setForeground(this.colorTrue);
				this.dataField.repaint();
			} else if ((((JComboBox) this.dataField).getSelectedItem() != null)
					&& ((JComboBox) this.dataField).getSelectedItem().equals(this.falseValue)) {
				this.dataField.setForeground(this.colorFalse);
				this.dataField.repaint();
			}
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final Object o = ((JComboBox) this.dataField).getSelectedItem();

		if ((o != null) && o.equals(this.trueValue)) {
			if (this.returnString) {
				return this.trueValue;
			}
			if (this.returnBoolean) {
				return Boolean.TRUE;
			} else {
				return BooleanComboDataField.UNO;
			}
		} else {
			if (this.returnString) {
				return this.falseValue;
			}
			if (this.returnBoolean) {
				return Boolean.FALSE;
			} else {
				return BooleanComboDataField.ZERO;
			}
		}
	}

	@Override
	public String getText() {
		if (this.isEmpty()) {
			return "";
		}
		return ((TranslateRenderer) ((JComboBox) this.dataField).getRenderer()).getText();
	}

	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		try {
			final Object oPreviousValue = this.getValue();
			if ((value != null) && !(value instanceof NullValue)) {
				if (value instanceof Number) {
					if (((Number) value).intValue() != 0) {
						((JComboBox) this.dataField).setSelectedItem(this.trueValue);
					} else {
						((JComboBox) this.dataField).setSelectedItem(this.falseValue);
					}
					this.valueSave = this.getValue();
					this.setInnerValue(this.valueSave);
					this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				} else {
					// Accept boolean values
					if (value instanceof Boolean) {
						if (((Boolean) value).booleanValue()) {
							((JComboBox) this.dataField).setSelectedItem(this.trueValue);
						} else {
							((JComboBox) this.dataField).setSelectedItem(this.falseValue);
						}
						this.valueSave = this.getValue();
						this.setInnerValue(this.valueSave);
						this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
					} else if (value instanceof String) {
						((JComboBox) this.dataField).setSelectedItem(value);
						this.valueSave = this.getValue();
						this.setInnerValue(this.valueSave);
						this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
					} else {
						this.deleteData();
					}
				}
			} else {
				this.deleteData();
			}
		} catch (final Exception e) {
			BooleanComboDataField.logger.error(null, e);
		} finally {
			this.setInnerListenerEnabled(true);
		}
	}

	@Override
	public int getSQLDataType() {
		if (this.returnBoolean) {
			return java.sql.Types.BIT;
		}
		return java.sql.Types.INTEGER;
	}

	@Override
	public void setValues(final List values) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (res != null) {
			try {
				this.translatedTrueValue = res.getString(this.trueValue);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BooleanComboDataField.logger.debug(e.getMessage(), e);
				}
			}
			try {
				this.translatedFalseValue = res.getString(this.falseValue);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BooleanComboDataField.logger.debug(e.getMessage(), e);
				}
			}
		}
	}

}
