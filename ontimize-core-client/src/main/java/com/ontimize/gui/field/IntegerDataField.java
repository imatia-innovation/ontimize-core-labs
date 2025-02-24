package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.AdvancedIntegerDocument;
import com.ontimize.gui.field.document.IntegerDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.calc.Calculator;

/**
 * This class implements a field to insert integer values.
 * <p>
 *
 * @author Imatia Innovation
 */
public class IntegerDataField extends TextFieldDataField implements OpenDialog, Freeable, AdvancedDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(IntegerDataField.class);

	/**
	 * Reference for the parent frame used in date selection dialog. By default, null.
	 */
	protected Frame frame = null;

	/**
	 * The reference for calculator button. This button shows a calculator. By default, null.
	 */
	protected JButton calcButton = null;

	/**
	 * The reference for calculator.
	 */
	protected Calculator calc = null;

	/**
	 * The return string. By default, false.
	 */
	protected boolean returnString = false;

	/**
	 * The advanced help menu reference. By default, null.
	 */
	protected JMenuItem advancedHelpBMenu = null;

	/**
	 * The class constructor. Initializes parameters and adds focus listener.
	 * <p>
	 * @param params
	 */
	public IntegerDataField(final Map params) {
		this.init(params);
		if (this.textAlignment == -1) {
			((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.RIGHT);
		}

		((JTextField) this.dataField).getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(final DocumentEvent ev) {
				IntegerDataField.this.colorSelection(ev);
			}

			@Override
			public void removeUpdate(final DocumentEvent ev) {
				IntegerDataField.this.colorSelection(ev);
			}

			@Override
			public void changedUpdate(final DocumentEvent ev) {
				IntegerDataField.this.colorSelection(ev);
			}
		});

		this.dataField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				if ((!IntegerDataField.this.isEmpty()) && !e.isTemporary()) {
					IntegerDataField.this.unFormat();
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if ((!IntegerDataField.this.isEmpty()) && (!e.isTemporary())) {
					IntegerDataField.this.format();
				}
			}
		});

		/*
		 * ((JTextField)campoDatos).addActionListener(new ActionListener() { public void
		 * actionPerformed(ActionEvent even) { transferFocus(); } });
		 */

	}

	/**
	 * The color selection for document.
	 * <p>
	 * @param e the document event
	 */
	protected void colorSelection(final DocumentEvent e) {
		final IntegerDocument doc = (IntegerDocument) ((JTextField) this.dataField).getDocument();
		if (doc.isRight()) {
			if (this.isRequired()) {
				this.dataField.setForeground(DataField.requiredFieldForegroundColor);
			} else {
				this.dataField.setForeground(this.fontColor);
			}
		} else {
			final Color foreground = this.dataField.getForeground();
			if (!Color.red.equals(foreground)){
				if (fontColor instanceof UIResource){
					this.fontColor = foreground;
				}
				this.dataField.setForeground(Color.red);
			}
		}
	}

	@Override
	public void setAdvancedQueryMode(final boolean advancedQueryMode) {
		if (!(((JTextField) this.dataField).getDocument() instanceof AdvancedIntegerDocument)) {
			return;
		}
		this.setInnerValue(this.getValue());
		this.advancedQueryMode = advancedQueryMode;

		((AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument()).setAdvancedQueryMode(advancedQueryMode);

		final Object oNewValue = this.getValue();
		if ((oNewValue == null) && (this.getInnerValue() != null)) {
			this.fireValueChanged(this.getValue(), this.getInnerValue(), ValueEvent.PROGRAMMATIC_CHANGE);
		} else if ((this.getInnerValue() == null) && (oNewValue != null)) {
			this.fireValueChanged(this.getValue(), this.getInnerValue(), ValueEvent.PROGRAMMATIC_CHANGE);
		} else if ((this.getInnerValue() != null) && (oNewValue != null) && (!this.getInnerValue().equals(oNewValue))) {
			this.fireValueChanged(this.getValue(), this.getInnerValue(), ValueEvent.PROGRAMMATIC_CHANGE);
		}

		this.setInnerValue(oNewValue);
	}

	/**
	 * Initializes parameters.
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
	 *
	 *        <tr>
	 *        <td>returnstring</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates when field value is returned like a string. It could be used to fill with
	 *        zeros.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>calc</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether a calculator is showed in the field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>minintegerdigits</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The minimum number of digits.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>maxintegerdigits</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The maximum number of digits.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>grouping</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether the field is showed without point separator. For example,
	 *        <code>1000</code> or <code>1.000</code> . It is not valid with
	 *        <code>advancedquerymode</code></td>
	 *        </tr>
	 *
	 *        *
	 *        <tr>
	 *        <td>numbertype</td>
	 *        <td>integer/long/bigdecimal/biginteger/short</td>
	 *        <td>integer</td>
	 *        <td>no</td>
	 *        <td>Indicates the number type of the field</td>
	 *        </tr>
	 *        </Table>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		((JTextField) this.dataField).setDocument(new AdvancedIntegerDocument());

		final Object numberType = parameters.get("numbertype");
		if (numberType != null) {
			final AdvancedIntegerDocument integerDocument = (AdvancedIntegerDocument) ((JTextField) this.dataField)
					.getDocument();
			if (numberType.toString().equalsIgnoreCase("long")) {
				integerDocument.setNumberTypeUsed(IntegerDocument.LONG);
			} else if (numberType.toString().equalsIgnoreCase("short")) {
				integerDocument.setNumberTypeUsed(IntegerDocument.SHORT);
			} else if (numberType.toString().equalsIgnoreCase("bigdecimal")) {
				integerDocument.setNumberTypeUsed(IntegerDocument.BIGDECIMAL);
			} else if (numberType.toString().equalsIgnoreCase("biginteger")) {
				integerDocument.setNumberTypeUsed(IntegerDocument.BIGINTEGER);
			} else if (numberType.toString().equalsIgnoreCase("integer")) {
				integerDocument.setNumberTypeUsed(IntegerDocument.INTEGER);
			}
		}

		final Object returnstring = parameters.get("returnstring");
		if (returnstring != null) {
			if (returnstring.equals("yes")) {
				final AdvancedIntegerDocument doc = (AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument();
				doc.setGroupingUsed(false);
				this.returnString = true;
			} else {
				this.returnString = false;
			}
		} else {
			this.returnString = false;
		}

		final Object minintegerdigits = parameters.get("minintegerdigits");
		if (minintegerdigits != null) {
			try {
				final int minimum = Integer.parseInt(minintegerdigits.toString());
				final AdvancedIntegerDocument doc = (AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument();
				doc.setMinimumIntegerDigits(minimum);
			} catch (final Exception e) {
				IntegerDataField.logger.error("Error in parameter 'minintegerdigits' ", e);
			}
		}

		final Object maxintegerdigits = parameters.get("maxintegerdigits");
		if (maxintegerdigits != null) {
			try {
				final int maximum = Integer.parseInt(maxintegerdigits.toString());
				final AdvancedIntegerDocument doc = (AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument();
				doc.setMaximumIntegerDigits(maximum);

			} catch (final Exception e) {
				IntegerDataField.logger.error("Error in parameter 'maxintegerdigits' ", e);
			}
		}

		final Object grouping = parameters.get("grouping");
		if (grouping != null) {
			if (grouping.toString().equals("no")) {
				final IntegerDocument doc = (IntegerDocument) ((JTextField) this.dataField).getDocument();
				doc.setGroupingUsed(false);
			}
		}

		// By default the calculator button is visible
		final Object calcul = parameters.get("calc");

		if ((calcul != null) && calcul.toString().equalsIgnoreCase("yes")) {
			// Add the button to show the calculator
			final ImageIcon calcIcon = ImageManager.getIcon(ImageManager.CALC);
			if (calcIcon == null) {
				if (ApplicationManager.DEBUG) {
					IntegerDataField.logger.debug("calc.png icon hasn't been found");
				}
				this.calcButton = new FieldButton("...");
			} else {
				this.calcButton = new FieldButton();
				this.calcButton.setIcon(calcIcon);
				this.calcButton.setMargin(new Insets(0, 0, 0, 0));
			}
			super.add(this.calcButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			if (this.labelPosition != SwingConstants.LEFT) {
				this.validateComponentPositions();
			}
			// Process the button click
			this.calcButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent evento) {
					if (IntegerDataField.this.calc == null) {
						IntegerDataField.this.calc = new Calculator(IntegerDataField.this.frame);
					}
					final Object oValue = IntegerDataField.this.getValue();
					double initialValue = 0.0;
					if (oValue != null) {
						initialValue = ((Integer) oValue).intValue();
					}
					final int result = (int) IntegerDataField.this.calc.showCalculator(initialValue,
							IntegerDataField.this.getLocationOnScreen().x,
							IntegerDataField.this.getLocationOnScreen().y + IntegerDataField.this.getHeight());
					IntegerDataField.this.setValue(new Integer(result));
				}
			});
		}

	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		if (this.calcButton != null) {
			this.calcButton.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.frame = parentFrame;
	}

	/**
	 * Applies format to field.
	 * <p>
	 *
	 * @see IntegerDocument#format()
	 */
	protected void format() {

		final boolean selectAll = this.isSelectedAll();
		try {
			final Object oNewValue = this.getValue();
			this.setInnerListenerEnabled(false);
			final IntegerDocument document = (IntegerDocument) ((JTextField) this.dataField).getDocument();
			document.format();
			this.setInnerListenerEnabled(true);

			if (!this.isInnerValueEqual(oNewValue)) {
				this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		} catch (final Exception ex) {
			IntegerDataField.logger.trace(null, ex);
		} finally {

			if (selectAll) {
				((JTextField) this.dataField).selectAll();
			}
			this.setInnerListenerEnabled(true);
		}
	}

	/**
	 * Removes format from field.
	 * <p>
	 *
	 * @see IntegerDocument#unFormat()
	 */
	protected void unFormat() {

		final boolean selectAll = this.isSelectedAll();
		try {
			final Object oNewValue = this.getInnerValue();
			this.setInnerListenerEnabled(false);
			final IntegerDocument document = (IntegerDocument) ((JTextField) this.dataField).getDocument();
			document.remove(0, document.getLength());
			document.insertString(0, oNewValue.toString(), null);
			this.setInnerListenerEnabled(true);

			this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerValue(oNewValue);
		} catch (final Exception ex) {
			IntegerDataField.logger.trace(null, ex);
		} finally {

			if (selectAll) {
				((JTextField) this.dataField).selectAll();
			}
			this.setInnerListenerEnabled(true);
		}
	}

	/**
	 * Gets the numerical field value .
	 * <p>
	 * @return the numerical value
	 */
	public Number getNumericalValue() {
		if (this.isEmpty()) {
			return null;
		}
		final IntegerDocument document = (IntegerDocument) ((JTextField) this.dataField).getDocument();
		return document.getValue();
	}

	/**
	 * Sets the field value when entry parameter is an integer, also deletes the field.
	 * <p>
	 * @param value the object to set.
	 */
	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getNumericalValue();

		if (value instanceof Number) {

			final IntegerDocument document = (IntegerDocument) ((JTextField) this.dataField).getDocument();
			document.setValue((Number) value);
			this.valueSave = this.getNumericalValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);

		} else if (value instanceof String) {

			try {
				final IntegerDocument document = (IntegerDocument) ((JTextField) this.dataField).getDocument();
				document.setValue(NumberFormat.getInstance().parse(value.toString()));
			} catch (final ParseException e) {
				((JTextField) this.dataField).setText((String) value);
			}

			this.valueSave = this.getNumericalValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);

		} else if ((value instanceof SearchValue)
				&& (((JTextField) this.dataField).getDocument() instanceof AdvancedIntegerDocument)) {

			final AdvancedIntegerDocument document = (AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument();
			document.setQueryValue((SearchValue) value);
			this.valueSave = this.getNumericalValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);

		} else {
			this.deleteData();
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final IntegerDocument integerDocument = (IntegerDocument) ((JTextField) this.dataField).getDocument();
		if (this.advancedQueryMode) {
			return ((AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument()).getQueryValue();
		} else {
			if (this.returnString) {
				return ((JTextField) this.dataField).getText();
			}
			return integerDocument.getValue();
		}
	}

	@Override
	public boolean isEmpty() {
		if (this.advancedQueryMode) {
			if (((AdvancedIntegerDocument) ((JTextField) this.dataField).getDocument()).getQueryValue() != null) {
				return false;
			} else {
				return true;
			}
		} else {
			return super.isEmpty();
		}
	}

	@Override
	public void free() {
		super.free();
		this.frame = null;
		if (ApplicationManager.DEBUG) {
			IntegerDataField.logger.debug(this.getClass().toString() + " Free.");
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.INTEGER;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getNumericalValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				IntegerDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				IntegerDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				IntegerDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			super.createPopupMenu();
			this.popupMenu.addSeparator();
			this.advancedHelpBMenu = new JMenuItem();
			String sMenuText = HelpUtilities.ADVANCED_SEARCH_HELP;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(HelpUtilities.ADVANCED_SEARCH_HELP);
				}
			} catch (final Exception e) {
				IntegerDataField.logger.trace(null, e);
			}
			final ImageIcon helpIcon = ImageManager.getIcon(ImageManager.HELPBOOK);
			if (helpIcon != null) {
				this.advancedHelpBMenu.setIcon(helpIcon);
			}
			this.advancedHelpBMenu.setText(sMenuText);
			this.popupMenu.add(this.advancedHelpBMenu);
			this.advancedHelpBMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						HelpUtilities.showDefaultAdvancedHelpDialog(
								SwingUtilities.getWindowAncestor(IntegerDataField.this),
								IntegerDataField.this.advancedHelpBMenu.getText(),
								IntegerDataField.this.locale);
					} catch (final Exception ex) {
						IntegerDataField.logger.error(null, ex);
						IntegerDataField.this.parentForm.message("datafield.help_files_cannot be displayed",
								Form.ERROR_MESSAGE, ex);
					}
				}
			});
		}
	}

	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		if (this.popupMenu == null) {
			this.createPopupMenu();
		}
		if (this.popupMenu != null) {
			this.configurePopupMenuHelp();
			this.advancedHelpBMenu.setVisible(this.advancedQueryMode);
			this.popupMenu.show(c, x, y);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
		if (this.advancedHelpBMenu != null) {
			this.advancedHelpBMenu
			.setText(ApplicationManager.getTranslation(HelpUtilities.ADVANCED_SEARCH_HELP, resource));
		}
	}

	@Override
	protected void installInnerListener() {
		if (this.dataField != null) {
			final Document d = ((JTextField) this.dataField).getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new InnerDocumentListener() {

						@Override
						protected Object getValueField() {
							if (IntegerDataField.this.isAdvancedQueryMode()) {
								try {
									// Since 5.2067EN-0.1
									// in advanced query mode, you need all
									// characters e.g 2:5, not numerical value
									return ((JTextField) IntegerDataField.this.dataField).getDocument()
											.getText(0,
													((JTextField) IntegerDataField.this.dataField).getDocument()
													.getLength());
								} catch (final BadLocationException e) {
									IntegerDataField.logger.error(null, e);
									return null;
								}
							} else {
								return IntegerDataField.this.getNumericalValue();
							}

						}
					};
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

}
