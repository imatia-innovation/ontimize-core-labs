package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.AdvancedDateDocument;
import com.ontimize.gui.field.document.DateDocument;
import com.ontimize.gui.field.document.HourDocument;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a data field with date and hours. Moreover, it is possible to show an
 * associated calendar with this field.
 * <p>
 *
 * @author Imatia Innovation
 */
public class HourDateDataField extends DateDataField {

	private static final Logger logger = LoggerFactory.getLogger(HourDateDataField.class);

	protected JTextField hourField;

	protected boolean dateVisible = true;

	protected String dateAttribute = null;

	private boolean register = false;

	/**
	 * The class constructor. Calls to <code>super</code> with parameters, fixes the constrains, sets
	 * listeners and initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	public HourDateDataField(final Map parameters) {
		super(parameters);
		if (!this.isEnabled) {
			this.setEnabled(false);
		}
		// if (!this.isEnabled) {
		// this.hourField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		// }
		final HourDocument doc = new HourDocument();
		this.hourField.setDocument(doc);
		doc.addDocumentListener(this.innerListener);
		this.hourField.setToolTipText(((HourDocument) this.hourField.getDocument()).getSampleHour());
		final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);
		this.remove(this.dataField);
		this.add(this.hourField, constraints);
		constraints.gridx = GridBagConstraints.RELATIVE;
		this.add(this.dataField, constraints);
		if (this.calendarButton != null) {
			final GridBagConstraints buttonConstraints = ((GridBagLayout) this.getLayout())
					.getConstraints(this.calendarButton);
			this.remove(this.calendarButton);
			this.add(this.calendarButton, buttonConstraints);
		}
		this.hourField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (!e.isTemporary()) {
					HourDateDataField.this.formatHour();
					final DateDocument dF = (DateDocument) ((JTextField) HourDateDataField.this.dataField).getDocument();
					if ((dF.getLength() == 0)
							&& (((HourDocument) HourDateDataField.this.hourField.getDocument()).getLength() > 0)) {
						if (HourDateDataField.this.dateAttribute != null) {
							final Object oDate = HourDateDataField.this.parentForm
									.getDataFieldValue(HourDateDataField.this.dateAttribute);
							if ((oDate == null) || (oDate instanceof Date)) {
								dF.setValue((Date) oDate);
							}
						} else {
							dF.setValue(new Date());
						}
						((JTextField) HourDateDataField.this.dataField).selectAll();
					}
				}
			}
		});

		this.dataField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (!e.isTemporary()) {
					HourDateDataField.this.format();
					final HourDocument dH = (HourDocument) HourDateDataField.this.hourField.getDocument();
					final DateDocument dF = (DateDocument) ((JTextField) HourDateDataField.this.dataField).getDocument();
					if ((dH.getLength() == 0) && (dF.getLength() > 0) && dF.isValid()) {
						try {
							final String stringHour = dH.getFormat().format(dF.getDate());
							dH.insertString(0, stringHour, null);
						} catch (final BadLocationException ex) {
							HourDateDataField.logger.error(null, ex);
						}
					}
				}
			}
		});
		final Object dv = parameters.get("datevisible");
		if (dv != null) {
			if (dv.equals("no")) {
				this.dateVisible = false;
				this.dataField.setVisible(false);
				if (this.calendarButton != null) {
					this.calendarButton.setVisible(false);
				}
			}
		}
		final Object date = parameters.get("date");
		if (date != null) {
			this.dateAttribute = date.toString();
		}

		this.hourField.addKeyListener(new KeyListenerSetDate());
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Adds the next parameters:
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
	 *        <td>datevisible</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates the visibility condition of date field and calendar button.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>date</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The date for the field will be fixed by this attribute. Creates a dependence with
	 *        other date field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>hourfieldsize</td>
	 *        <td>integer value</td>
	 *        <td>6</td>
	 *        <td>no</td>
	 *        <td>The number of columns in the hour JTextField.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		this.hourField = new JTextField(8);
		super.init(parameters);

		try {
			this.getHourField().setColumns(ParseUtils.getInteger((String) parameters.get("hourfieldsize"), 6));
		} catch (final Exception e) {
			HourDateDataField.logger.trace(null, e);
			this.getHourField().setColumns(8);
		}

		Object border = parameters.get(DataField.BORDER);
		if (border == null) {
			border = DataField.DEFAULT_BORDER;
		}
		if (border != null) {
			if (border.equals(DataField.NONE)) {
				this.hourField.setBorder(new EmptyBorder(0, 0, 0, 0));
				this.hourField.setOpaque(false);
			} else if (border.equals(DataField.RAISED)) {
				this.hourField.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals(DataField.LOWERED)) {
				this.hourField.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				try {
					final Color c = ColorConstants.colorNameToColor(border.toString());
					this.hourField.setBorder(new LineBorder(c));
				} catch (final Exception e) {
					HourDateDataField.logger.trace(null, e);
					this.hourField.setBorder(ParseUtils.getBorder((String) border, this.dataField.getBorder()));
				}
			}
		} else {
			if (!this.isEnabled) {
				// this.hourField.setBorder(new BorderUIResource(new
				// EtchedBorder(EtchedBorder.LOWERED)));
			}
		}

		if (this.textAlignment != -1) {
			switch (this.textAlignment) {
			case ALIGN_LEFT:
				this.hourField.setHorizontalAlignment(SwingConstants.LEFT);
				break;
			case ALIGN_CENTER:
				this.hourField.setHorizontalAlignment(SwingConstants.CENTER);
				break;
			case ALIGN_RIGHT:
				this.hourField.setHorizontalAlignment(SwingConstants.RIGHT);
				break;
			default:
				this.hourField.setHorizontalAlignment(SwingConstants.LEFT);
				break;
			}
		}

		this.hourField.setFont(ParseUtils.getFont((String) parameters.get("font"), this.hourField.getFont()));
	}

	@Override
	public void setFontSize(final int fontSize) {
		super.setFontSize(fontSize);
		int iSize = fontSize;
		if (this.incrementalFont) {
			if (this.originalSize == -1) {
				this.originalSize = this.labelComponent.getFont().getSize();
			}
			iSize = this.originalSize + iSize;
		}
		if (this.hourField != null) {
			this.hourField.setFont(this.hourField.getFont().deriveFont((float) iSize));
		}
	}

	@Override
	public void setParentForm(final Form form) {
		super.setParentForm(form);
		if ((form != null) && (!this.register) && (this.dateAttribute != null)) {
			final DataComponent c = form.getDataFieldReference(this.dateAttribute);
			if (c != null) {
				if (c instanceof ValueChangeDataComponent) {
					((ValueChangeDataComponent) c).addValueChangeListener(new ValueChangeListener() {

						@Override
						public void valueChanged(final ValueEvent e) {
							if (HourDateDataField.this.dataField != null) {
								final DateDocument dF = (DateDocument) ((JTextField) HourDateDataField.this.dataField)
										.getDocument();
								if ((e.getNewValue() == null) || (e.getNewValue() instanceof Date)) {
									if (e.getType() == ValueEvent.PROGRAMMATIC_CHANGE) {
										HourDateDataField.this.setInnerListenerEnabled(false);
									}
									dF.setValue((Date) e.getNewValue());
									HourDateDataField.this.setInnerListenerEnabled(true);
								}
							}
						}
					});
				}
			} else {
				HourDateDataField.logger.debug(this.getClass().toString() + ": " + this.attribute
						+ " -> Field not found with the attribute: " + this.dateAttribute);
			}
			this.register = true;
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (!this.dateVisible) {
			if (this.dataField != null) {
				this.dataField.setVisible(false);
				if (this.calendarButton != null) {
					this.calendarButton.setVisible(false);
				}
			}
		}
	}

	@Override
	public Object getDateValue() {
		if (this.isEmpty()) {
			return null;
		}
		return this.getValue();
	}

	/**
	 * Formats the hour.
	 * <p>
	 *
	 * @see HourDocument#format()
	 */
	protected void formatHour() {
		try {
			final Object oNewValue = this.getValue();
			this.setInnerListenerEnabled(false);
			final HourDocument document = (HourDocument) this.hourField.getDocument();
			document.format();
			this.setInnerListenerEnabled(true);
			if (!this.isInnerValueEqual(oNewValue)) {
				this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		} catch (final Exception ex) {
			HourDateDataField.logger.trace(null, ex);
		} finally {
			this.setInnerListenerEnabled(true);
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
		super.setEnabled(enabled);
		if (this.hourField != null) {
			this.hourField.setEnabled(enabled);
		}
	}

	@Override
	public void setComponentLocale(final Locale loc) {
		super.setComponentLocale(loc);
		final Locale l = this.getLocale();
		this.setInnerListenerEnabled(false);
		((HourDocument) this.hourField.getDocument()).setComponentLocale(l);
		this.hourField.setToolTipText(((HourDocument) this.hourField.getDocument()).getSampleHour());
		this.setInnerListenerEnabled(true);
	}

	@Override
	public boolean isEmpty() {
		if (!((DateDocument) ((JTextField) this.dataField).getDocument()).isValid()) {
			return true;
		}
		if (((JTextField) this.dataField).getText().equals("") || this.hourField.getText().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void deleteData() {
		final Object oPreviousValue = this.getDateValue();
		try {
			this.setInnerListenerEnabled(false);
			final Document dateDocument = ((JTextField) this.dataField).getDocument();
			final Document hourDocument = this.hourField.getDocument();
			dateDocument.remove(0, dateDocument.getLength());
			hourDocument.remove(0, hourDocument.getLength());
			this.valueSave = this.getDateValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				HourDateDataField.logger.debug("Error deleting field.", e);
			} else {
				HourDateDataField.logger.trace(null, e);
			}
		} finally {
			this.setInnerListenerEnabled(true);
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		if (this.advancedQueryMode) {
			final SearchValue vb = ((AdvancedDateDocument) ((JTextField) this.dataField).getDocument()).getQueryValue();
			if ((vb != null) && (vb.getValue() instanceof java.util.Date)) {
				final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
				final HourDocument hourDocument = (HourDocument) this.hourField.getDocument();
				final Date dDate = dateDocument.getDate();
				final Date dHour = hourDocument.getHour();
				final GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance(this.locale);
				calendar.setTime(dDate);
				final GregorianCalendar calendar2 = (GregorianCalendar) Calendar.getInstance(this.locale);
				calendar2.setTime(dHour);
				calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
				calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
				calendar.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
				return new SearchValue(vb.getCondition(), new java.sql.Timestamp(calendar.getTime().getTime()));
			} else {
				return vb;
			}
		} else {
			final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
			final HourDocument hourDocument = (HourDocument) this.hourField.getDocument();
			final Date dDate = dateDocument.getDate();
			final Date dHour = hourDocument.getHour();
			final GregorianCalendar calendar = (GregorianCalendar) Calendar.getInstance(this.locale);
			calendar.setTime(dDate);
			final GregorianCalendar calendar2 = (GregorianCalendar) Calendar.getInstance(this.locale);
			calendar2.setTime(dHour);
			calendar.set(Calendar.HOUR_OF_DAY, calendar2.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, calendar2.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, calendar2.get(Calendar.SECOND));
			return new java.sql.Timestamp(calendar.getTime().getTime());
		}
	}

	@Override
	public void setValueFromComponent(final Object componentValue) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getInnerValue();
		if ((componentValue instanceof Timestamp) || (componentValue instanceof Date)) {
			Object oValue = null;
			DateDocument document = null;
			HourDocument hourDocument = null;

			if (componentValue instanceof Timestamp) {
				oValue = new Date(((Timestamp) componentValue).getTime());
				document = (DateDocument) ((JTextField) this.dataField).getDocument();
				hourDocument = (HourDocument) this.hourField.getDocument();
			} else {
				oValue = componentValue;
				document = (DateDocument) ((JTextField) this.dataField).getDocument();
				hourDocument = (HourDocument) this.hourField.getDocument();
			}

			try {
				final String stringDate = document.getFormat().format((Date) oValue);
				final String stringHour = hourDocument.getFormat().format((Date) oValue);
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					HourDateDataField.logger
					.debug(this.getClass() + "-> setValueFromComponent: " + stringHour + " " + stringDate);
				}

				document.remove(0, document.getLength());
				document.insertString(0, stringDate, null);
				hourDocument.remove(0, hourDocument.getLength());
				hourDocument.insertString(0, stringHour, null);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					HourDateDataField.logger.debug(null, e);
				} else {
					HourDateDataField.logger.trace(null, e);
				}
			}
			this.setInnerValue(this.getDateValue());
			if (this.calendarChange) {
				this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
			}
		}
		this.setInnerListenerEnabled(true);
	}

	@Override
	public void setValue(final Object value) {

		final Object oPreviousValue = this.getDateValue();
		if (com.ontimize.gui.ApplicationManager.DEBUG) {
			HourDateDataField.logger.debug(this.getClass() + "-> setValue :" + value);
		}
		if ((value instanceof java.sql.Timestamp) || (value instanceof Date)) {
			this.setInnerListenerEnabled(false);
			final Object oValue = new Date(((Date) value).getTime());
			final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
			final HourDocument hourDocument = (HourDocument) this.hourField.getDocument();
			try {
				final String stringDate = document.getFormat().format((Date) oValue);
				final String stringHour = hourDocument.getFormat().format((Date) oValue);
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					HourDateDataField.logger.debug(this.getClass() + "-> setValue: " + stringHour + " " + stringDate);
				}

				document.remove(0, document.getLength());
				document.insertString(0, stringDate, null);
				hourDocument.remove(0, hourDocument.getLength());
				hourDocument.insertString(0, stringHour, null);
				this.hourField.setCaretPosition(0);
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					HourDateDataField.logger.debug(null, e);
				} else {
					HourDateDataField.logger.trace(null, e);
				}
			}
			this.valueSave = this.getDateValue();
			this.setInnerValue(this.valueSave);
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		} else {
			this.deleteData();
		}
	}

	/**
	 * Gets the hour field.
	 * <p>
	 * @return the hour field value.
	 */
	public JTextField getHourField() {
		return this.hourField;
	}

	@Override
	protected void updateBackgroundColor() {
		super.updateBackgroundColor();
		try {
			if (this.requiredBorder != null) {
				if (!this.enabled) {
					this.hourField.setForeground(this.fontColor);
					this.hourField.setBackground(DataComponent.VERY_LIGHT_GRAY);
				} else {
					this.hourField.setBorder(
							this.required ? BorderManager.getBorder(this.requiredBorder) : this.noRequiredBorder);
					this.hourField.setBackground(this.backgroundColor);
					this.hourField.setForeground(this.fontColor);
				}
			} else if (DataField.ASTERISK_REQUIRED_STYLE) {
				if (this.enabled) {
					((AuxPanel) this.panel).setAsteriskVisible(this.isRequired());
					this.hourField.setForeground(this.fontColor);
					this.hourField.setBackground(this.backgroundColor);
				} else {
					((AuxPanel) this.panel).setAsteriskVisible(false);
					this.hourField.setForeground(this.fontColor);
					this.hourField.setBackground(DataComponent.VERY_LIGHT_GRAY);
				}
			} else {
				if (!this.enabled) {
					this.hourField.setForeground(this.fontColor);
					this.hourField.setBackground(DataComponent.VERY_LIGHT_GRAY);
				} else {
					if (this.required) {
						this.hourField.setBackground(DataField.requiredFieldBackgroundColor);
						this.hourField.setForeground(DataField.requiredFieldForegroundColor);
					} else {
						this.hourField.setBackground(this.backgroundColor);
						this.hourField.setForeground(this.fontColor);
					}
				}
			}
		} catch (final Exception e) {
			HourDateDataField.logger.error(null, e);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
	}

	@Override
	protected void setCurrentDate() {
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final DateDocument document = (DateDocument) ((JTextField) this.dataField).getDocument();
		final Date currentDate = new Date();
		document.setValue(currentDate);
		final HourDocument hDocument = (HourDocument) this.hourField.getDocument();
		final String stringHour = hDocument.getFormat().format(currentDate);
		try {
			hDocument.remove(0, hDocument.getLength());
			hDocument.insertString(0, stringHour, null);
		} catch (final BadLocationException e) {
			HourDateDataField.logger.error(null, e);
		}
		this.setInnerValue(this.getDateValue());
		this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	protected void setDate(final int day) {
		String hour = null;
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final Calendar calendar = Calendar.getInstance();
		hour = this.hourField.getText();
		final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
		final HourDocument hDocument = (HourDocument) this.hourField.getDocument();
		try {
			if (day > calendar.getMaximum(Calendar.DAY_OF_MONTH)) {
				dateDocument.remove(0, dateDocument.getLength());
				hDocument.remove(0, hDocument.getLength());
			} else {
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), day);
				dateDocument.setValue(new Timestamp(calendar.getTimeInMillis()));

				if ((hour != null) && (hour.compareTo("") != 0)) {
					hDocument.remove(0, hDocument.getLength());
					hDocument.insertString(0, hour, null);
				} else {
					final String stringHour = hDocument.getFormat().format(new Timestamp(calendar.getTimeInMillis()));
					hDocument.remove(0, hDocument.getLength());
					hDocument.insertString(0, stringHour, null);
				}
			}
			this.setInnerValue(this.getDateValue());
			this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		} catch (final BadLocationException e) {
			HourDateDataField.logger.error(null, e);
		}
		this.setInnerListenerEnabled(true);
	}

	@Override
	protected void setDate(final int day, final int month) {
		String hour = null;
		this.setInnerListenerEnabled(false);
		final Object oPrevValue = this.getInnerValue();
		final Calendar calendar = Calendar.getInstance();
		hour = this.hourField.getText();
		final DateDocument dateDocument = (DateDocument) ((JTextField) this.dataField).getDocument();
		final HourDocument hDocument = (HourDocument) this.hourField.getDocument();
		try {
			if ((day > calendar.getMaximum(Calendar.DAY_OF_MONTH)) || (month > 12)) {
				dateDocument.remove(0, dateDocument.getLength());
				hDocument.remove(0, hDocument.getLength());
			} else {
				calendar.set(calendar.get(Calendar.YEAR), month - 1, day);
				dateDocument.setValue(new Timestamp(calendar.getTimeInMillis()));

				if ((hour != null) && (hour.compareTo("") != 0)) {
					hDocument.remove(0, hDocument.getLength());
					hDocument.insertString(0, hour, null);
				} else {
					final String stringHour = hDocument.getFormat().format(new Timestamp(calendar.getTimeInMillis()));
					hDocument.remove(0, hDocument.getLength());
					hDocument.insertString(0, stringHour, null);
				}
			}
			this.setInnerValue(this.getDateValue());
			this.fireValueChanged(this.getInnerValue(), oPrevValue, ValueEvent.USER_CHANGE);
		} catch (final BadLocationException e) {
			HourDateDataField.logger.error(null, e);
		}
		this.setInnerListenerEnabled(true);
	}

	protected class KeyListenerSetDate implements KeyListener {

		@Override
		public void keyPressed(final KeyEvent e) {
			// Do nothing
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
				HourDateDataField.this.setCurrentDate();
			}
		}

	}

}
