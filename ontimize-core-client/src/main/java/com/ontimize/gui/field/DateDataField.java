package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.calendar.VisualCalendarComponent;
import com.ontimize.gui.calendar.event.CalendarEvent;
import com.ontimize.gui.calendar.event.CalendarListener;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.document.AdvancedDateDocument;
import com.ontimize.gui.field.document.DateDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.ParseUtils;

/**
 * The main class for creating a field to introduce a date. It adds the parameter
 * <code>calendar</code>. that indicates if a dialog box with the calendar to set the date.
 * <p>
 *
 * @author Imatia Innovation
 */

public class DateDataField extends TextFieldDataField implements OpenDialog, Freeable, AdvancedDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(DateDataField.class);

	/**
	 * Indicates to set the title to the calendar, by default "calendar".
	 */
	protected String calendarTitleKey = "calendar";

	/**
	 * A reference to the parent frame used to select a date.
	 */
	protected Frame frame = null;

	/**
	 * A reference to the Gregorian Calendar.
	 */
	protected GregorianCalendar mainCalendar = null;

	/**
	 * Indicates the form to notify the calendar change event. By default, "false".
	 */
	protected boolean calendarChange = false;

	/**
	 * A reference to calendar button
	 */
	protected JButton calendarButton = null;

	/**
	 * A reference to the default locale.
	 */
	protected Locale locale = Locale.getDefault();

	/**
	 * Indicates if the date must be emphasized with a different color when it is invalid. By default
	 * "true".
	 */
	protected boolean emphasizeInvalidDate = true;

	/**
	 * A reference to a possible advance help menu.
	 */
	protected JMenuItem advancedHelpBMenu = null;

	/**
	 * Uses only in case of debug mode.
	 */
	public static boolean DEBUG_DATE = false;

	/**
	 * Especial dialog with the functionality that Esc key throws the event WINDOW_CLOSING.
	 */
	protected EJDialog vCalendar = null;

	/**
	 * Implements a calendar with selectable year, month and day. The calendar is sensitive to locale.
	 */
	protected VisualCalendarComponent calendarComp = null;

	/**
	 * The class constructor. Initializes parameters and adds a <code>Document Listener</code>.
	 * <p>
	 * @param parameters a <code>Hashtable</code> with <CODE>calendar</CODE> and
	 *        <CODE>calendarchange</CODE> parameters
	 */
	public DateDataField(final Map parameters) {

		this.init(parameters);

		if (parameters.get(DataField.SIZE) == null) {
			((JTextField) this.dataField).setColumns(8);
		}

		((JTextField) this.dataField).setDocument(new AdvancedDateDocument());
		((JTextField) this.dataField).getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(final DocumentEvent ev) {
				DateDataField.this.colorSelection(ev);
			}

			@Override
			public void removeUpdate(final DocumentEvent ev) {
				DateDataField.this.colorSelection(ev);
			}

			@Override
			public void changedUpdate(final DocumentEvent ev) {
				DateDataField.this.colorSelection(ev);
			}
		});

		((JTextField) this.dataField).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DateDataField.this.transferFocus();
			}
		});

		this.dataField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent event) {
				if (!event.isTemporary()) {
					DateDataField.this.setDate();
					DateDataField.this.format();
				}
			}
		});

		this.dataField.setEnabled(true);
		this.dataField.validate();

		this.getDataField().addKeyListener(new KeyListenerSetDate());
	}

	public void setDocument(final Document dateDocument) {
		if (this.dataField != null) {
			((JTextField) this.dataField).setDocument(dateDocument);
		}
	}

	/**
	 * This method gets the <code>Hashtable</code> and creates the calendar, the access button and
	 * implements the listeners.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>calendar</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates whether a dialog box with a calendar will be showed.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>calendarchange</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates the form to notify the change event in calendar values. By default, its
	 *        value is 'no' and so, the changes are only showed when you abandon the calendar.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>calendaricon</td>
	 *        <td><i></td>
	 *        <td>ImageManager.CALENDAR</td>
	 *        <td>no</td>
	 *        <td>Indicates the path to icon that shows the calendar.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>borderbuttons</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover,
	 *        it is also allowed a border defined in #BorderManager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border for buttons in Form</td>
	 *        </tr>
	 *        <tr>
	 *        <td>highlightbuttons</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the highlight in button property when mouse is entered. See
	 *        {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires
	 *        opaque='no'.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaquebuttons</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Data field opacity condition for Form buttons</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		final Object calendar = parameters.get("calendar");
		if (calendar != null) {
			if (calendar.toString().equalsIgnoreCase("yes")) {
				this.mainCalendar = new GregorianCalendar();
			} else {
				if (calendar.toString().equalsIgnoreCase("no")) {
					this.mainCalendar = null;
				} else {
					this.mainCalendar = new GregorianCalendar();
				}
			}
		} else {
			this.mainCalendar = new GregorianCalendar();
		}

		if (this.mainCalendar != null) {
			final ImageIcon calendarIcon = ParseUtils.getImageIcon((String) parameters.get("calendaricon"),
					ImageManager.getIcon(ImageManager.CALENDAR));
			if (calendarIcon == null) {
				DateDataField.logger.debug("calendar.png icon not found");
				this.calendarButton = new FieldButton("...");
				this.calendarButton.setMargin(new Insets(0, 0, 0, 0));
			} else {
				this.calendarButton = new FieldButton();
				if (calendarIcon.getIconHeight() > this.calendarButton.getHeight()) {
					((FieldButton)this.calendarButton).setHigh(calendarIcon.getIconHeight());
				}
				this.calendarButton.setIcon(calendarIcon);
				this.calendarButton.setMargin(new Insets(0, 0, 0, 0));
			}

			super.add(this.calendarButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.calendarButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					DateDataField.this.showCalendar((Component) event.getSource());
				}
			});

			if (this.labelPosition != SwingConstants.LEFT) {
				this.validateComponentPositions();
			}

			final Object change = parameters.get("calendarchange");
			if (change != null) {
				if ("yes".equalsIgnoreCase(change.toString()) || "true".equalsIgnoreCase(change.toString())) {
					this.calendarChange = true;
				}
			}
		}

		final boolean borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"), true);
		final boolean opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false);
		MouseListener listenerHighlightButtons = null;
		if (highlightButtons) {
			listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButton(this.calendarButton, borderbuttons, opaquebuttons, listenerHighlightButtons);

	}

	/**
	 * Hidden the calendar.
	 */
	public void hiddenCalendar() {
		if (this.vCalendar != null) {
			this.vCalendar.setVisible(false);
		}
	}

	/**
	 * Shows the calendar.
	 * <p>
	 * @param c Component to create graphical representation and user interaction
	 */
	public void showCalendar(final Component c) {
		final Object oValue = this.getValue();
		Object oDateValue = null;

		boolean bPreviousValueNull = false;
		if ((oValue == null) || (!(oValue instanceof Date))) {
			if (this.advancedQueryMode && (oValue != null) && (oValue instanceof SearchValue)) {
				final Object o = ((SearchValue) oValue).getValue();
				if ((o != null) && (o instanceof Date)) {
					oDateValue = o;
				} else {
					oDateValue = null;
				}
			} else {
				oDateValue = new Date();
				bPreviousValueNull = true;
			}
		} else {
			oDateValue = oValue;
		}

		if (this.vCalendar == null) {
			final Window w = SwingUtilities.getWindowAncestor(c);
			if (w instanceof Dialog) {
				this.vCalendar = new EJDialog((Dialog) w, true);
				this.createCalendarComponent(this.vCalendar);
			} else {
				this.vCalendar = new EJDialog((Frame) w, true);
				this.createCalendarComponent(this.vCalendar);
			}
		}

		this.showCalendar((Date) oDateValue, c);

		if (!this.calendarChange) {
			final Object oNewValue = this.getDateValue();
			if ((oNewValue == null) && (oValue != null)) {
				this.fireValueChanged(oNewValue, oValue, ValueEvent.USER_CHANGE);
				return;
			}
			if ((oNewValue != null) && (oValue == null)) {
				this.fireValueChanged(oNewValue, oValue, ValueEvent.USER_CHANGE);
				return;
			}

			if (this.advancedQueryMode && ((oNewValue instanceof SearchValue) || (oValue instanceof SearchValue))) {
				if ((!(oNewValue instanceof SearchValue)) || (!(oValue instanceof SearchValue))) {
					this.fireValueChanged(oNewValue, oValue, ValueEvent.USER_CHANGE);
				} else {
					if (!((SearchValue) oNewValue).getValue().equals(((SearchValue) oValue).getValue())) {
						this.fireValueChanged(oNewValue, oValue, ValueEvent.USER_CHANGE);
					}
				}
				return;
			}

			if (this.mainCalendar == null) {
				this.mainCalendar = new GregorianCalendar(this.locale);
			}
			this.mainCalendar.setTime((Date) oValue);
			final int previousDay = this.mainCalendar.get(Calendar.DAY_OF_MONTH);
			final int previousMonth = this.mainCalendar.get(Calendar.MONTH);
			final int previousYear = this.mainCalendar.get(Calendar.YEAR);
			this.mainCalendar.setTime((Date) oNewValue);
			final int day = this.mainCalendar.get(Calendar.DAY_OF_MONTH);
			final int month = this.mainCalendar.get(Calendar.MONTH);
			final int year = this.mainCalendar.get(Calendar.YEAR);
			if ((day != previousDay) || (month != previousMonth) || (year != previousYear)
					|| (bPreviousValueNull && (oNewValue != null))) {
				this.fireValueChanged(oNewValue, oValue, ValueEvent.USER_CHANGE);
			}
		}
	}

	/**
	 * Checks the variable that indicates if user has permission to introduce values into the datafield.
	 * <p>
	 * @param enabled boolean to change permissions
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		if (this.calendarButton != null) {
			this.calendarButton.setEnabled(enabled);
			if (this.vCalendar != null) {
				this.vCalendar.setVisible(false);
			}
		}
		super.setEnabled(enabled);
	}

	/**
	 * Selects the text color in function of field content. Valid date->black color and Invalid
	 * date->red color.
	 * @param e
	 */
	protected void colorSelection(final DocumentEvent e) {
		if (this.emphasizeInvalidDate) {
			final DateDocument doc = (DateDocument) ((JTextField) this.dataField).getDocument();
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
	}

	/**
	 * Creates the correct representation for the date in data field.
	 */
	public void format() {
		try {
			final Object oNewValue = this.getValue();
			this.setInnerListenerEnabled(false);
			final DateDocument doc = (DateDocument) ((JTextField) this.dataField).getDocument();
			doc.format();
			this.setInnerListenerEnabled(true);
			if (!this.isInnerValueEqual(oNewValue)) {
				this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}

		} catch (final Exception ex) {
			DateDataField.logger.trace(null, ex);
		} finally {
			this.setInnerListenerEnabled(true);
		}
	}

	/**
	 * Sets the field value by user when <CODE>auxValue</CODE> is a instance of java.sql.timestamp or
	 * java.util. During the method execution the inner Listener is disabled.
	 * <p>
	 *
	 * @see ValueEvent#USER_CHANGE
	 * @param auxValue Date or Timestamp Object
	 */
	public void setValueFromComponent(final Object auxValue) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getInnerValue();
		if (auxValue instanceof Timestamp) {
			final Object oValue = new Date(((Timestamp) auxValue).getTime());
			final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
			document.setValue((Date) oValue);
			this.setInnerValue(this.getDateValue());
			if (this.calendarChange) {
				this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
			}

		} else if (auxValue instanceof Date) {
			final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
			document.setValue((Date) auxValue);
			this.setInnerValue(this.getDateValue());
			if (this.calendarChange) {
				this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
			}
		}
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Sets the field value by program when <CODE>auxValue</CODE> is a instance of java.sql.timestamp or
	 * java.util. During the method execution the inner Listener is disabled.
	 * <p>
	 *
	 * @see ValueEvent#PROGRAMMATIC_CHANGE
	 * @param auxValue Date or Timestamp Object
	 */
	@Override
	public void setValue(final Object auxValue) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getDateValue();
		if (auxValue instanceof Timestamp) {
			final Object oValue = new Date(((Timestamp) auxValue).getTime());
			final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
			document.setValue((Date) oValue);
			this.valueSave = this.getDateValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		} else {
			// If type is not Timestamp then check if it is Date
			if (auxValue instanceof Date) {
				final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
				document.setValue((Date) auxValue);
				this.valueSave = this.getDateValue();
				this.setInnerValue(this.valueSave);
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				this.setInnerListenerEnabled(true);
			} else {
				if (auxValue instanceof SearchValue) {
					if (((JTextField) this.dataField).getDocument() instanceof AdvancedDateDocument) {
						final AdvancedDateDocument document = (AdvancedDateDocument) ((JTextField) this.dataField)
								.getDocument();
						document.setValue((SearchValue) auxValue);

						this.valueSave = this.getDateValue();
						this.setInnerValue(this.valueSave);
						this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
						this.setInnerListenerEnabled(true);
					} else {
						this.deleteData();
					}
				} else {
					this.deleteData();
				}
			}
		}
		// repaint();
	}

	/**
	 * Gets the Timestamp value.
	 * <p>
	 * @return an Object with the Timestamp value
	 * @throws RuntimeException if any exception occurs
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
		if (this.advancedQueryMode) {
			return ((AdvancedDateDocument) ((JTextField) this.dataField).getDocument()).getQueryValue();
		} else {
			try {
				// this.enableInnerListener(false);
				final Object oValue = document.getTimestampValue();
				return oValue;
			} catch (final Exception ex) {
				DateDataField.logger.error(null, ex);
				throw new RuntimeException(ex.getMessage(), ex);
				// } finally {
				// this.enableInnerListener(true);
			}
		}
	}

	/**
	 * Gets the Date value.
	 * <p>
	 * @return an Object with the Date value
	 * @throws RuntimeException if any exception occurs
	 */
	public Object getDateValue() {
		if (this.isEmpty()) {
			return null;
		}
		final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
		try {
			// this.setInnerListenerEnabled(false);
			final Object oValue = document.getTimestampValue();
			return oValue;
		} catch (final Exception ex) {
			DateDataField.logger.error(null, ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}
		// finally {
		// this.setInnerListenerEnabled(true);
		// }
	}

	/**
	 * This method returns the preferred size for data field to contain a date in correct format. Size
	 * will depend on font size.
	 * <p>
	 * @return The width and height dimension
	 */
	public Dimension getDataFieldPreferredSize() {
		// The minimum size have to permit to see 10 characters.
		// Trick: Uses a JTextField to calculate the preferred height
		final JTextField aux = new JTextField();
		aux.setFont(this.dataField.getFont());
		aux.setText("01/23/456789");
		final Dimension parentDimension = aux.getPreferredSize();
		final int height = (int) parentDimension.getHeight();
		// the width depends on font size.
		final FontMetrics fontMetrics = this.dataField.getFontMetrics(this.getFont());
		// Is necessary to find the maximum advance of digits.
		char character = '1';
		int maximunAdvance = fontMetrics.charWidth(character);
		for (int i = 0; i < 10; i++) {
			character++;
			if (fontMetrics.charWidth(character) > maximunAdvance) {
				maximunAdvance = fontMetrics.charWidth(character);
			}
		}
		if (fontMetrics.charWidth('/') > maximunAdvance) {
			maximunAdvance = fontMetrics.charWidth('/');
		}
		final int width = 10 * maximunAdvance;
		return new Dimension(width, height);
	}

	/**
	 * Checks the presence and validity of data.
	 * <p>
	 * @return boolean true with valid data. False with invalid or empty data.
	 */
	@Override
	public boolean isEmpty() {
		if (this.advancedQueryMode) {
			if (((AdvancedDateDocument) ((JTextField) this.dataField).getDocument()).getQueryValue() != null) {
				return false;
			} else {
				return true;
			}
		}
		if (!((DateDocument) ((JTextField) this.dataField).getDocument()).isValid()) {
			return true;
		} else {
			if (((JTextField) this.dataField).getText().equals("")) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Sets the parent Frame.
	 */
	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.frame = parentFrame;
	}

	public static Locale getSameCountryLocale(final Locale loc) {
		if (loc != null) {
			// If the locale is a "es_ES_gl" that this method doesn't check.
			if ("es_ES_gl".equals(loc.toString())) {
				return loc;
			}
			boolean found = false;
			Locale sameCountryLocale = null;
			final Locale list[] = DateFormat.getAvailableLocales();
			for (int i = 0; i < list.length; i++) {
				if (list[i].equals(loc)) {
					found = true;
					break;
				} else {
					if (list[i].getCountry().equals(loc.getCountry())) {
						// When the specified locale does not exist look for
						// another
						// locale in the same country.
						// If the country has more than one locale look for one
						// with the
						// same language and country, for example es_ES or en_EN
						if ((sameCountryLocale == null) || ((list[i].getLanguage() != null)
								&& list[i].getCountry().toUpperCase().equals(list[i].getLanguage().toUpperCase()))) {
							sameCountryLocale = list[i];
						}
					}
				}
			}

			Locale locale = loc;
			if ((!found) && (sameCountryLocale != null)) {
				locale = sameCountryLocale;
				if (ApplicationManager.DEBUG) {
					DateDataField.logger.debug("Locale " + loc.toString() + " not found. Using " + locale);
				}
			} else {
				locale = loc;
			}
			return locale;
		}
		return null;
	}

	/**
	 * Sets the Component Locale, to format date, specified in parameter.
	 */
	@Override
	public void setComponentLocale(final Locale loc) {
		final Locale l = DateDataField.getSameCountryLocale(loc);
		this.setLocale(l);
		this.locale = l;
		if (ApplicationManager.DEBUG) {
			DateDataField.logger.debug(this.getClass().toString() + " : Set Locale :" + l.toString());
		}
		this.setInnerListenerEnabled(false);
		try {
			((DateDocument) ((JTextField) this.dataField).getDocument()).setComponentLocale(l);
			this.mainCalendar = new GregorianCalendar(l);
			if (this.vCalendar != null) {
				this.vCalendar.setLocale(l);
			}
			if (this.calendarComp != null) {
				this.calendarComp.setCalendarLocale(l);
			}
		} catch (final Exception ex) {
			DateDataField.logger.error(null, ex);
		}
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Releases the frame.
	 */
	@Override
	public void free() {
		super.free();
		this.frame = null;
		if (ApplicationManager.DEBUG) {
			DateDataField.logger.debug(this.getClass().toString() + " Free.");
		}
	}

	/**
	 * Gets the SQL data type for specified data types.
	 * <p>
	 * @return the integer type of TIMESTAMP.
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.TIMESTAMP;
	}

	/**
	 * Sets the Component to support the advanced query mode state.
	 */
	@Override
	public void setAdvancedQueryMode(final boolean enable) {
		if (!(((JTextField) this.dataField).getDocument() instanceof AdvancedDateDocument)) {
			return;
		}

		this.valueSave = this.getValue();
		this.advancedQueryMode = enable;

		((AdvancedDateDocument) ((JTextField) this.dataField).getDocument()).setAdvancedQueryMode(enable);
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
	 * Returns true when data component has been modified.
	 */
	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				DateDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				DateDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				DateDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Creates a Pop-up Menu to advanced search help.
	 */
	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			super.createPopupMenu();
			// this.popupMenu = new ExtendedJPopupMenu();
			this.popupMenu.addSeparator();
			this.advancedHelpBMenu = new JMenuItem();
			String sMenuText = HelpUtilities.ADVANCED_SEARCH_HELP;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(HelpUtilities.ADVANCED_SEARCH_HELP);
				}
			} catch (final Exception e) {
				DateDataField.logger.trace(null, e);
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
								SwingUtilities.getWindowAncestor(DateDataField.this),
								DateDataField.this.advancedHelpBMenu.getText(),
								DateDataField.this.locale);
					} catch (final Exception ex) {
						DateDataField.logger.trace(null, ex);
						DateDataField.this.parentForm.message("datafield.help_files_cannot be displayed",
								Form.ERROR_MESSAGE, ex);
					}
				}
			});
		}
	}

	/**
	 * Shows the Pop-up Menu.
	 * <p>
	 * @param c the Component to show
	 * @param x The x coordinate
	 * @param y The y coordinate
	 */
	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		// if(this.campoDatos.isEnabled()==false) return;
		if (this.popupMenu == null) {
			this.createPopupMenu();
		}
		if (this.popupMenu != null) {
			this.configurePopupMenuHelp();
			this.advancedHelpBMenu.setVisible(this.advancedQueryMode);
			this.popupMenu.show(c, x, y);

		}
	}

	/**
	 * Sets the resource bundle. Also changes the title calendar in case this is specified.
	 * @param res the resource bundle to change
	 */
	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (this.vCalendar != null) {
			this.vCalendar.setTitle(ApplicationManager.getTranslation(this.calendarTitleKey, res));
		}
		if (this.advancedHelpBMenu != null) {
			this.advancedHelpBMenu.setText(ApplicationManager.getTranslation(HelpUtilities.ADVANCED_SEARCH_HELP, res));
		}
	}

	/**
	 * Deletes the date of the data field. During the method execution the inner Listener is disabled.
	 */
	@Override
	public void deleteData() {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getDateValue();
		try {
			final Document dateDocument = ((JTextField) this.dataField).getDocument();
			dateDocument.remove(0, dateDocument.getLength());
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				DateDataField.logger.debug("Error deleting date data field.", e);
			} else {
				DateDataField.logger.trace(null, e);
			}
		}
		this.valueSave = this.getDateValue();
		this.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * @param highlight whether or not the emphasizeInvalidDate is enabled
	 */
	public void setEmphasizeInvalidDate(final boolean highlight) {
		this.emphasizeInvalidDate = highlight;
	}

	/**
	 * Creates a calendar dialog window with the specified listeners.
	 * <p>
	 * @param d a non-modal dialog
	 * @see javax.swing.JDialog
	 */
	private void createCalendarComponent(final JDialog d) {
		if (this.calendarComp == null) {
			try {
				if (this.resources != null) {
					d.setTitle(this.resources.getString(this.calendarTitleKey));
				} else {
					d.setTitle(this.calendarTitleKey);
				}
			} catch (final Exception e) {
				d.setTitle(this.calendarTitleKey);
				if (ApplicationManager.DEBUG) {
					DateDataField.logger.debug(null, e);
				} else {
					DateDataField.logger.trace(null, e);
				}
			}

			this.calendarComp = new VisualCalendarComponent(this.getLocale());
			d.getContentPane().add(this.calendarComp);
			d.pack();
			this.calendarComp.addCalendarListener(new CalendarListener() {

				@Override
				public void dateChanged(final CalendarEvent e) {
					DateDataField.this.setValueFromComponent(DateDataField.this.calendarComp.getCurrentDate());
					DateDataField.this.calendarComp.setDay(DateDataField.this.calendarComp.getDay());
				}
			});
			this.calendarComp.addMouseListenerToDaysTable(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 2) {
						DateDataField.this.vCalendar.setVisible(false);
					}
				}
			});
		}
	}

	/**
	 * Class to define the action performed to close the window.
	 */
	public class CloseAction extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof Component) {
				final Window w = SwingUtilities.getWindowAncestor((Component) o);
				if (w != null) {
					w.setVisible(false);
				}
			}
		}

	}

	/**
	 * Shows the Date in Component
	 * <p>
	 * @param d the date
	 * @param c the component
	 */

	private void showCalendar(final Date d, final Component c) {
		this.calendarComp.setDate(d);
		this.vCalendar.pack();
		final Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();

		if (c != null) {
			final Point pButton = c.getLocationOnScreen();
			int xPoint = 0;
			int yPoint = 0;

			if (((pButton.x + c.getWidth()) - this.vCalendar.getWidth()) < 0) {
				xPoint = (int) c.getLocationOnScreen().getX();
			} else {
				xPoint = ((int) c.getLocationOnScreen().getX() + c.getWidth()) - this.vCalendar.getWidth();
			}

			if ((pButton.y + c.getHeight() + this.vCalendar.getHeight()) > screenDimension.height) {
				yPoint = (int) c.getLocationOnScreen().getY() - this.vCalendar.getHeight();
			} else {
				yPoint = (int) c.getLocationOnScreen().getY() + c.getHeight();
			}
			this.vCalendar.setLocation(xPoint, yPoint);
		} else {
			final Point pButton = this.getLocationOnScreen();
			int xPoint = 0;
			int yPoint = 0;

			if (((pButton.x + this.getWidth()) - this.vCalendar.getWidth()) < 0) {
				xPoint = (int) this.getLocationOnScreen().getX();
			} else {
				xPoint = ((int) this.getLocationOnScreen().getX() + this.getWidth()) - this.vCalendar.getWidth();
			}

			if ((pButton.y + this.getHeight() + this.vCalendar.getHeight()) > screenDimension.height) {
				yPoint = (int) this.getLocationOnScreen().getY() - this.vCalendar.getHeight();
			} else {
				yPoint = (int) this.getLocationOnScreen().getY() + this.getHeight();
			}

			this.vCalendar.setLocation(xPoint, yPoint);
		}

		this.vCalendar.setVisible(true);

	}

	/**
	 * Installs a inner listener to get events by program.
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
							return DateDataField.this.getDateValue();
						}
					};
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

	/**
	 * Fill the DateDataField with the actual date of the system
	 */
	protected void setCurrentDate() {
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
		document.setValue(new Date());
		this.setInnerValue(this.getDateValue());
		this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Check if it has entered a part of the date, and fill the missing data (month or year)
	 */
	protected void setDate() {
		String day = null;
		String month = null;
		final String actualValue = ((JTextField) this.getDataField()).getText();

		// Parse the date
		final StringTokenizer tok = new StringTokenizer(actualValue, "/");
		if (tok.hasMoreElements()) {
			day = tok.nextToken();
		}
		if (tok.hasMoreElements()) {
			month = tok.nextToken();
		}
		if (!tok.hasMoreElements()) {
			if ((day != null) && (month == null)) {
				this.setDate(Integer.valueOf(day));
			}
			if ((day != null) && (month != null)) {
				this.setDate(Integer.valueOf(day), Integer.valueOf(month));
			}
		}
	}

	/**
	 * Fill the field with the the specified day and the actual month and year. If the given day is
	 * higher than the last day of actual month, the field will be cleared.
	 * @param day The specific day.
	 */
	protected void setDate(final int day) {
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final Calendar calendar = Calendar.getInstance();
		final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
		try {
			if (day > calendar.getMaximum(Calendar.DAY_OF_MONTH)) {
				dateDocument.remove(0, dateDocument.getLength());
			} else {
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day);
				dateDocument.setValue(new Timestamp(calendar.getTimeInMillis()));
			}
			this.setInnerValue(this.getDateValue());
			this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		} catch (final BadLocationException e) {
			DateDataField.logger.error(null, e);
		}
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Fill the field with the the specified day and month and the actual year. If the given month is
	 * higher than 12, the field will be cleared.
	 * @param day The specific day.
	 * @param month The specific month.
	 */
	protected void setDate(final int day, final int month) {
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final Calendar calendar = Calendar.getInstance();
		final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
		try {
			if ((day > calendar.getMaximum(Calendar.DAY_OF_MONTH)) || (month > 12)) {
				dateDocument.remove(0, dateDocument.getLength());
			} else {
				calendar.set(calendar.get(Calendar.YEAR), month - 1, day);
				dateDocument.setValue(new Timestamp(calendar.getTimeInMillis()));
			}
			this.setInnerValue(this.getDateValue());
			this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		} catch (final BadLocationException e) {
			DateDataField.logger.error(null, e);
		}
		this.setInnerListenerEnabled(true);
	}

	protected class KeyListenerSetDate implements KeyListener {

		@Override
		public void keyPressed(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				DateDataField.this.setDate();
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			// Do nothing
		}

		@Override
		public void keyTyped(final KeyEvent e) {
			// insertar fecha actual si pulsa h o t
			if ((e.getKeyChar() == 'h') || (e.getKeyChar() == 'H') || (e.getKeyChar() == 't')
					|| (e.getKeyChar() == 'T')) {
				DateDataField.this.setCurrentDate();
			}

		}

	}

	@Override
	public void setDefaultValue() {
		if (this.defaultValue != null) {
			if ("now".equalsIgnoreCase(this.defaultValue)) {
				final Date now = Calendar.getInstance().getTime();
				this.setValue(now);
			}
		}
	}

}
