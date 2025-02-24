package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.i18n.Internationalization;

/**
 * Abstract class to add the functionalities for data components whose data field is a
 * <code>JTextField</code>.
 * <p>
 *
 * @author Imatia Innovation
 */
public abstract class TextFieldDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(TextFieldDataField.class);

	public static Color disabledTextColor;

	/**
	 * This class provides extended functionalities for text fields.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class EJTextField extends JTextField implements Internationalization {

		/**
		 * The column width. By default, 0.
		 */
		protected int columnWidth = 0;

		/**
		 * The default locale.
		 */
		protected Locale locale = Locale.getDefault();

		/**
		 * The decimal separator. By default, '.'
		 */
		protected char decimalSeparator = '.';

		/**
		 * The condition to replace decimal separator. By default, false.
		 */
		protected boolean replaceDecimalSeparator = false;

		/**
		 * If true the caret position will be set to 0 on focus lost
		 */
		protected boolean setCaretPositionOnFocusLost = true;

		/**
		 * Class constructor. Calls to <code>super()</code> and manages focus events.
		 */
		public EJTextField() {
			super();
			this.decimalSeparator = new DecimalFormatSymbols(this.locale).getDecimalSeparator();
			ToolTipManager.sharedInstance().registerComponent(this);
			this.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(final FocusEvent e) {
				}

				@Override
				public void focusLost(final FocusEvent e) {
					if ((!e.isTemporary()) && EJTextField.this.setCaretPositionOnFocusLost) {
						EJTextField.this.setCaretPosition(0);
					}
				}
			});
		}

		/**
		 * Class constructor where is supported to specify the number of columns. Calls to
		 * <code>super()</code> and manages focus events.
		 * <p>
		 * @param cols number of columns
		 */
		public EJTextField(final int cols) {
			super(cols);
			this.decimalSeparator = new DecimalFormatSymbols(this.locale).getDecimalSeparator();
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		@Override
		public void setFont(final Font f) {
			super.setFont(f);
			this.columnWidth = 0;
		}

		@Override
		public void setText(final String text) {
			super.setText(text);
			if ((!this.isEditable()) || !this.hasFocus()) {
				this.setCaretPosition(0);
			}
		}

		/**
		 * Sets the condition to replace the decimal separator.
		 * <p>
		 * @param replace the condition to replace
		 */
		public void setReplaceDecimalSeparator(final boolean replace) {
			this.replaceDecimalSeparator = replace;
		}

		public void setCaretPositionOnFocusLost(final boolean set) {
			this.setCaretPositionOnFocusLost = set;
		}

		@Override
		protected void processKeyEvent(final KeyEvent e) {
			if (((e.getKeyChar() == '.') || (e.getKeyChar() == ',')) && (e.getID() == KeyEvent.KEY_TYPED)
					&& this.replaceDecimalSeparator) {
				if (e.getKeyChar() == this.decimalSeparator) {
					super.processKeyEvent(e);
				} else {
					e.setKeyChar(this.decimalSeparator);
					super.processKeyEvent(e);
				}
			} else {
				super.processKeyEvent(e);
			}
		}

		@Override
		public void setComponentLocale(final Locale l) {
			this.locale = l;
			this.updateDecimalSeparatorCharacter();
		}

		private void updateDecimalSeparatorCharacter() {
			// If the locale is not a valid one like gl_ES then we need to use a
			// valid locale for the same country
			final Locale loc = DateDataField.getSameCountryLocale(this.locale);
			this.decimalSeparator = new DecimalFormatSymbols(loc).getDecimalSeparator();
		}

		@Override
		public void setResourceBundle(final ResourceBundle resource) {

		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public int getColumnWidth() {
			if (this.columnWidth == 0) {
				final FontMetrics metrics = this.getFontMetrics(this.getFont());
				this.columnWidth = metrics.charWidth('M') + 1;
			}
			return this.columnWidth;
		}

		@Override
		public String getToolTipText(final MouseEvent e) {
			if (this.getWidth() < EJTextField.calculatePreferredWidthText(this)) {
				return this.getText();
			} else {
				return this.getToolTipText();
			}
		}

		/**
		 * Calculates the preferred width text.
		 * <p>
		 * @param tf the text field reference
		 * @return the preferred width for text
		 */
		protected static int calculatePreferredWidthText(final JTextField tf) {
			final FontMetrics metrics = tf.getFontMetrics(tf.getFont());
			final int iAuxWidth = metrics.stringWidth(tf.getText());
			return iAuxWidth + 4;
		}

	}

	/**
	 * This class implements a inner document listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerDocumentListener implements DocumentListener {

		/**
		 * The inner listener enabled condition. By default, true.
		 */
		protected boolean innerListenerEnabled = true;

		Object innerValue = null;

		/**
		 * Sets enabled the inner listener.
		 * <p>
		 * @param enabled the condition
		 */
		public void setInnerListenerEnabled(final boolean enabled) {
			this.innerListenerEnabled = enabled;
		}

		/**
		 * Gets the value of field.
		 * <p>
		 * @return the value
		 */
		protected Object getValueField() {
			return TextFieldDataField.this.getValue();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getValueField();
				if (this.isEqualInnerValue(oNewValue)) {
					return;
				}
				TextFieldDataField.this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getValueField();
				if (this.isEqualInnerValue(oNewValue)) {
					return;
				}
				TextFieldDataField.this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

		/**
		 * Gets the inner value.
		 * <p>
		 * @return the inner value
		 */
		public Object getInnerValue() {
			return this.innerValue;
		}

		/**
		 * Sets the inner value.
		 * <p>
		 * @param o the inner value
		 */
		public void setInnerValue(final Object o) {
			this.innerValue = o;
		}

		/**
		 * Compares the inner value with parameter.
		 * <p>
		 * @param newValue the value to compare
		 * @return the condition
		 */
		protected boolean isEqualInnerValue(final Object newValue) {
			if ((newValue == null) && (this.innerValue == null)) {
				return true;
			}
			if ((newValue == null) || (this.innerValue == null)) {
				return false;
			}
			return this.innerValue.equals(newValue);
		}

	}

	/**
	 * A reference to the inner listener. By default, null.
	 */
	protected InnerDocumentListener innerListener = null;

	/**
	 * Checks the inner listener enabled condition.
	 * <p>
	 * @return the enabled or disabled condition
	 */
	public boolean getInnerListenerEnabled() {
		if (this.innerListener != null) {
			return this.innerListener.innerListenerEnabled;
		} else {
			return false;
		}
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>focuslistener</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The complete class name that implements <CODE>FocusListener</CODE> and will be
	 *        registered like listener for this field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>defaultvalue</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Default value than the component is setted when the defaultValue method is called</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.createDataField();

		if ((this.dataField instanceof JTextField) && (TextFieldDataField.disabledTextColor != null)) {
			((JTextField) this.dataField).setDisabledTextColor(TextFieldDataField.disabledTextColor);
		}

		super.init(parameters);

		if (this.textAlignment != -1) {
			switch (this.textAlignment) {
			case ALIGN_LEFT:
				((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.LEFT);
				break;
			case ALIGN_CENTER:
				((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.CENTER);
				break;
			case ALIGN_RIGHT:
				((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.RIGHT);
				break;
			default:
				((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.LEFT);
				break;
			}
		}
		if (this.dataField instanceof JTextField) {
			((JTextField) this.dataField).setColumns(this.fieldSize);
		}
		this.installInnerListener();

		// Parameters.
		final Object focuslistener = parameters.get("focuslistener");
		if (focuslistener != null) {
			try {
				final Class focusListenerClass = Class.forName(focuslistener.toString());
				final Object o = focusListenerClass.newInstance();
				if (o instanceof FocusListener) {
					this.dataField.addFocusListener((FocusListener) o);
				} else {
					TextFieldDataField.logger.debug(this.getClass().toString() + " : Class " + focuslistener
							+ " does not implement FocusListener");
				}
			} catch (final Exception ex) {
				if (ApplicationManager.DEBUG) {
					TextFieldDataField.logger.error(null, ex);
				} else {
					TextFieldDataField.logger.trace(null, ex);
				}
			}
		}
	}

	/**
	 * Creates the data field.
	 */
	protected void createDataField() {
		this.dataField = new EJTextField() {

			@Override
			public boolean isVisible() {
				return super.isVisible();
			}

			@Override
			public void setDocument(final Document doc) {
				try {
					final Document previousDocument = this.getDocument();
					if ((TextFieldDataField.this.innerListener != null) && (previousDocument != null)) {
						previousDocument.removeDocumentListener(TextFieldDataField.this.innerListener);
					}
				} catch (final Exception e) {
					TextFieldDataField.logger.trace(null, e);
				}
				super.setDocument(doc);
				try {
					TextFieldDataField.this.installInnerListener();
				} catch (final Exception e) {
					TextFieldDataField.logger.trace(null, e);
				}
			}
		};
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public String getLabelComponentText() {
		return this.labelComponent.getText();
	}

	@Override
	public void deleteData() {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		try {
			final Document document = ((JTextField) this.dataField).getDocument();
			document.remove(0, document.getLength());
		} catch (final Exception e) {
			TextFieldDataField.logger.debug("Error deleting field.", e);
		}
		this.valueSave = this.getValue();
		this.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public boolean isModifiable() {
		return this.modifiable;
	}

	@Override
	public String getText() {
		return ((JTextField) this.dataField).getText();
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public boolean isEmpty() {
		if (((JTextField) this.dataField).getText() == null) {
			this.empty = true;
		} else {
			if (((JTextField) this.dataField).getText().length() == 0) {
				this.empty = true;
			} else {
				this.empty = false;
			}
		}
		return this.empty;
	}

	/**
	 * Installs an inner listener.
	 */
	protected void installInnerListener() {
		if (this.dataField != null) {
			final Document d = ((JTextField) this.dataField).getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new InnerDocumentListener();
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

	/**
	 * Enable or disable the intern value events notification.
	 * <p>
	 * @param enable the enabled condition
	 */
	protected void setInnerListenerEnabled(final boolean enable) {
		this.innerListener.setInnerListenerEnabled(enable);
	}

	/**
	 * Gets the inner listener value.
	 * <p>
	 * @return the value
	 */
	protected Object getInnerValue() {
		return this.innerListener.getInnerValue();
	}

	/**
	 * Sets the inner listener value.
	 * <p>
	 * @param o the value
	 */
	protected void setInnerValue(final Object o) {
		this.innerListener.setInnerValue(o);
	}

	/**
	 * Compares the inner value with parameter value.
	 * <p>
	 * @param value the value
	 * @return the true or false comparation
	 */
	protected boolean isInnerValueEqual(final Object value) {
		return this.innerListener.isEqualInnerValue(value);
	}

	protected boolean isSelectedAll() {
		try {
			final int selectStart = ((JTextField) this.dataField).getSelectionStart();
			final int selectEnd = ((JTextField) this.dataField).getSelectionEnd();
			boolean selectAll = false;
			if (((JTextField) this.dataField).getText().length() == (selectEnd - selectStart)) {
				selectAll = true;
			}
			return selectAll;
		} catch (final Exception e) {
			TextFieldDataField.logger.debug("Checking TextFieldDataField", e);
		}
		return false;
	}

}
