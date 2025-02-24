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
import java.util.Locale;
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
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.AdvancedRealDocument;
import com.ontimize.gui.field.document.RealDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.calc.Calculator;

/**
 * The main class for introducing a real numbers in a field.
 * <p>
 *
 * @author Imatia Innovation
 */
public class RealDataField extends TextFieldDataField implements OpenDialog, Freeable, AdvancedDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(RealDataField.class);

	/** A reference for the parent Frame used in the selection of date. */
	protected Frame frame = null;

	/** A reference for a button. */
	protected JButton calcButton = null;

	/** A reference for a calculator. */
	protected Calculator calc = null;

	/** A reference for a help menu. */
	protected JMenuItem advancedHelpBMenu = null;

	/**
	 * Creates the correct representation for the real number in real data field.
	 */
	protected void format() {
		final boolean selectAll = this.isSelectedAll();
		try {
			final Object oNewValue = this.getValue();
			this.setInnerListenerEnabled(false);
			((RealDocument) ((JTextField) this.dataField).getDocument()).format();
			this.setInnerListenerEnabled(true);
			if (!this.isInnerValueEqual(oNewValue)) {
				this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		} catch (final Exception ex) {
			RealDataField.logger.trace(null, ex);
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
	 * @see RealDocument#unFormat()
	 */
	protected void unFormat() {

		final boolean selectAll = this.isSelectedAll();
		try {
			final Object oNewValue = this.getInnerValue();
			this.setInnerListenerEnabled(false);
			final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
			document.remove(0, document.getLength());
			document.insertString(0, oNewValue.toString().replace('.', document.getDecimalSeparator()), null);
			this.setInnerListenerEnabled(true);
			this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerValue(oNewValue);
		} catch (final Exception ex) {
			RealDataField.logger.trace(null, ex);
		} finally {

			if (selectAll) {
				((JTextField) this.dataField).selectAll();
			}
			this.setInnerListenerEnabled(true);
		}
	}

	/**
	 * The class constructor. It initializes the parameters, adds focus listener, sets alignment and
	 * optionally replaces the decimal separator.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public RealDataField(final Map parameters) {

		this.init(parameters);
		this.installValidationDocumentListener();
		this.dataField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusGained(final FocusEvent e) {
				if ((!RealDataField.this.isEmpty()) && !e.isTemporary()) {
					RealDataField.this.unFormat();
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if ((!RealDataField.this.isEmpty()) && !e.isTemporary()) {
					RealDataField.this.format();
				}
			}
		});
		if (this.textAlignment == -1) {
			((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.RIGHT);
		}
		if (this.dataField instanceof EJTextField) {
			((EJTextField) this.dataField).setReplaceDecimalSeparator(true);
		}
	}

	/**
	 * Checks the validation of document.
	 * <p>
	 *
	 * @see #isValid()
	 */
	protected void installValidationDocumentListener() {
		((JTextField) this.dataField).getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(final DocumentEvent ev) {
				RealDataField.this.colorSelection(ev);
			}

			@Override
			public void removeUpdate(final DocumentEvent ev) {
				RealDataField.this.colorSelection(ev);
			}

			@Override
			public void changedUpdate(final DocumentEvent ev) {
				RealDataField.this.colorSelection(ev);
			}
		});
	}

	/**
	 * Creates a pop-up menu.
	 */
	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			super.createPopupMenu();
			this.popupMenu.addSeparator();
			// this.popupMenu = new ExtendedJPopupMenu();
			this.advancedHelpBMenu = new JMenuItem();
			String sMenuText = HelpUtilities.ADVANCED_SEARCH_HELP;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(HelpUtilities.ADVANCED_SEARCH_HELP);
				}
			} catch (final Exception e) {
				RealDataField.logger.trace(null, e);
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
								SwingUtilities.getWindowAncestor(RealDataField.this),
								RealDataField.this.advancedHelpBMenu.getText(),
								RealDataField.this.locale);
					} catch (final Exception ex) {
						RealDataField.logger.error(null, ex);
						RealDataField.this.parentForm.message("datafield.help_files_cannot be displayed",
								Form.ERROR_MESSAGE, ex);
					}
				}
			});
		}
	}

	/**
	 * Shows the pop-up menu.
	 */
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

	/**
	 * Calls to <code>super()</code> to init parameters.
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 * <tr>
	 * <td>calc</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>The presence of the calendar.</td>
	 * </tr>
	 * <tr>
	 * <td>grouping</td>
	 * <td><i>yes/no</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The condition to put the numbers in groups (1 000 000 -> 1000000)</td>
	 * </tr>
	 * <tr>
	 * <td>minintegerdigits</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The minimum number of integer digits.</td>
	 * </tr>
	 * <tr>
	 * <td>maxintegerdigits</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The maximum number of integer digits.</td>
	 * </tr>
	 * <tr>
	 * <td>mindecimaldigits</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The minimum number of decimal digits.</td>
	 * </tr>
	 * <tr>
	 * <td>maxdecimaldigits</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The maximum number of decimal digits.</td>
	 * </tr>
	 * </table>
	 * @param parameters <code>Hashtable</code> for initialization parameters.
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		((JTextField) this.dataField).setDocument(new AdvancedRealDocument());
		final Object minintegerdigits = parameters.get("minintegerdigits");
		if (minintegerdigits != null) {
			try {
				final int minimum = Integer.parseInt(minintegerdigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMinimumIntegerDigits(minimum);
			} catch (final Exception e) {
				RealDataField.logger.error(" Error in parameter 'minintegerdigits' ", e);
			}
		}

		final Object maxintegerdigits = parameters.get("maxintegerdigits");
		if (maxintegerdigits != null) {
			try {
				final int maximum = Integer.parseInt(maxintegerdigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMaximumIntegerDigits(maximum);
			} catch (final Exception e) {
				RealDataField.logger.error(" Error in parameter 'maxintegerdigits' ", e);
			}
		}

		final Object mindecimaldigits = parameters.get("mindecimaldigits");
		if (mindecimaldigits != null) {
			try {
				final int minimum = Integer.parseInt(mindecimaldigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMinimumFractionDigits(minimum);
			} catch (final Exception e) {
				RealDataField.logger.error(" Error in parameter 'mindecimaldigits' ", e);
			}
		}

		final Object maxdecimaldigits = parameters.get("maxdecimaldigits");
		if (maxdecimaldigits != null) {
			try {
				final int maximo = Integer.parseInt(maxdecimaldigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMaximumFractionDigits(maximo);
			} catch (final Exception e) {
				RealDataField.logger.error(" Error in parameter 'maxdecimaldigits' ", e);
			}
		}

		final Object grouping = parameters.get("grouping");
		if (grouping != null) {
			if (grouping.toString().equals("no")) {
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setGrouping(false);
			}
		}

		// Calculator button is shown by default
		final Object calcul = parameters.get("calc");

		if ((calcul != null) && calcul.toString().equalsIgnoreCase("yes")) {
			// Add the button to show
			final ImageIcon calculatorIcon = ImageManager.getIcon(ImageManager.CALC);
			if (calculatorIcon == null) {
				if (ApplicationManager.DEBUG) {
					RealDataField.logger.debug("calc.png icon hasn't been found");
				}
				this.calcButton = new FieldButton("...");
			} else {
				this.calcButton = new FieldButton();
				this.calcButton.setIcon(calculatorIcon);
				this.calcButton.setMargin(new Insets(0, 0, 0, 0));
			}
			super.add(this.calcButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			if (this.labelPosition != SwingConstants.LEFT) {
				this.validateComponentPositions();
			}
			// Process button click
			this.calcButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent evento) {
					if (RealDataField.this.calc == null) {
						RealDataField.this.calc = new Calculator(RealDataField.this.frame);
					}
					final Object oValue = RealDataField.this.getDoubleValue();
					double firstValue = 0.0;
					if (oValue != null) {
						firstValue = ((Double) oValue).doubleValue();
					}
					final double result = RealDataField.this.calc.showCalculator(firstValue,
							RealDataField.this.getLocationOnScreen().x,
							RealDataField.this.getLocationOnScreen().y + RealDataField.this.getHeight());
					RealDataField.this.setValue(new Double(result));
				}
			});
		}

	}

	/**
	 * Checks permissions and enables the calc button according to param.
	 * <p>
	 * @param enabled the condition to enable component
	 */
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

	/**
	 * Sets the parent frame.
	 * <p>
	 * @param parentFrame the parent frame
	 */
	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.frame = parentFrame;
	}

	public Number getNumericalValue() {
		if (this.isEmpty()) {
			return null;
		}
		final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
		return document.getValue();
	}

	/**
	 * The method to get the field value in double format.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return a double value
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
		final Double val = (Double) document.getValue();
		if (val == null) {
			return null;
		} else {
			if (this.advancedQueryMode) {
				return ((AdvancedRealDocument) ((JTextField) this.dataField).getDocument()).getQueryValue();
			} else {
				return val;
			}
		}
	}

	/**
	 * Sets foreground color for document.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @param e the document event
	 */
	protected void colorSelection(final DocumentEvent e) {
		final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
		if (doc.isValid()) {
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

	/**
	 * The empty or no empty field condition.
	 * <p>
	 * @return the condition
	 */
	@Override
	public boolean isEmpty() {
		if (this.advancedQueryMode) {
			if (((AdvancedRealDocument) ((JTextField) this.dataField).getDocument()).getQueryValue() != null) {
				return false;
			} else {
				return true;
			}
		}
		return super.isEmpty();
	}

	/**
	 * The value of data field.
	 * <p>
	 * @return the object with double value
	 */
	public Object getDoubleValue() {
		if (this.isEmpty()) {
			return null;
		}
		final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
		final Double val = (Double) document.getValue();
		if (val == null) {
			return null;
		} else {
			return val;
		}
	}

	/**
	 * Sets the field value. 'value' must be a double or a float. In other case, the field is deleted.
	 * <p>
	 * @param value the object with value
	 */
	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getDoubleValue();
		if ((value instanceof Double) || (value instanceof Float)) {
			final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
			try {
				document.remove(0, document.getLength());
				// Format with the document formatter
				final String stringValue = document.getFormat().format(value);
				document.insertString(0, stringValue, null);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					RealDataField.logger.debug(null, e);
				} else {
					RealDataField.logger.trace(null, e);
				}
			}
			this.valueSave = this.getDoubleValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);

		} else if ((value instanceof SearchValue)
				&& (((JTextField) this.dataField).getDocument() instanceof AdvancedRealDocument)) {
			this.setValue(((SearchValue) value).getValue());
		} else if (value instanceof Number) {
			final Number n = (Number) value;
			this.setValue(new Double(n.doubleValue()));
			// Here no event is fired because this method calls itself
		} else {
			// If it is not a number then clear the field
			this.deleteData();
		}

	}

	@Override
	public void free() {
		super.free();
		this.frame = null;
		if (ApplicationManager.DEBUG) {
			RealDataField.logger.debug(this.getClass().toString() + " Free.");
		}
	}

	/**
	 * The method to get the integer SQL types for double type.
	 * <p>
	 *
	 * @returns the return type according to double SQL type
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.DOUBLE;
	}

	/**
	 * Set the advanced query mode state.
	 * <p>
	 * @param enable the condition to enable the advanced query mode
	 */
	@Override
	public void setAdvancedQueryMode(final boolean enable) {
		if (!(((JTextField) this.dataField).getDocument() instanceof AdvancedRealDocument)) {
			return;
		}
		this.valueSave = this.getValue();
		this.advancedQueryMode = enable;
		((AdvancedRealDocument) ((JTextField) this.dataField).getDocument())
		.setAdvancedQueryMode(this.advancedQueryMode);
		final Object oNewValue = this.getValue();
		if ((oNewValue == null) && (this.valueSave != null)) {
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
		} else if ((this.valueSave == null) && (oNewValue != null)) {
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
		} else if ((this.valueSave != null) && (oNewValue != null) && !this.valueSave.equals(oNewValue)) {
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.PROGRAMMATIC_CHANGE);
		}
		this.valueSave = oNewValue;
	}

	/**
	 * Checks whether field is modified.
	 * <p>
	 * @return the boolean condition
	 */
	@Override
	public boolean isModified() {
		final Object oValue = this.getDoubleValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				RealDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				RealDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				RealDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds a inner listener.
	 */
	@Override
	protected void installInnerListener() {
		if (this.dataField != null) {
			final Document d = ((JTextField) this.dataField).getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new InnerDocumentListener() {

						@Override
						protected Object getValueField() {
							return RealDataField.this.getNumericalValue();
						}
					};
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale loc) {
		final Locale l = DateDataField.getSameCountryLocale(loc);
		final Object value = this.getValue();
		super.setComponentLocale(l);
		final boolean events = this.fireValueEvents;
		this.fireValueEvents = false;
		if (value != null) {
			this.setValue(value);
		}
		this.fireValueEvents = events;

	}

}
