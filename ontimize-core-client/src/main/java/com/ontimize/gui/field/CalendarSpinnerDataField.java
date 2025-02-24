package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.calendar.VisualCalendarComponent;
import com.ontimize.gui.calendar.event.CalendarEvent;
import com.ontimize.gui.calendar.event.CalendarListener;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.spinner.CustomSpinnerDateModel.SpinnerDateDocument;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.util.ParseUtils;

public class CalendarSpinnerDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(CalendarSpinnerDataField.class);

	protected static class CalendarSpinnerGroup extends JPanel implements Internationalization {

		protected static String DAYS = "days";

		protected static String MONTHS = "months";

		protected static String YEARS = "years";

		protected static String LAYOUT = "layout";

		protected static String HORIZONTAL_LAYOUT = "horizontal";

		protected static String VERTICAL_LAYOUT = "vertical";

		protected String layoutType = CalendarSpinnerGroup.HORIZONTAL_LAYOUT;

		protected GridBagLayout defaultLayout = null;

		protected JSpinner spinnerDays = null;

		protected JSpinner spinnerMonths = null;

		protected JSpinner spinnerYears = null;

		protected SpinnerDateModel spinnerDateModel = null;

		public JSpinner getSpinnerDays() {
			return this.spinnerDays;
		}

		public JSpinner getSpinnerMonths() {
			return this.spinnerMonths;
		}

		public JSpinner getSpinnerYears() {
			return this.spinnerYears;
		}

		public CalendarSpinnerGroup(final Map parameters) {
			super();
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);

			final Object oLayout = parameters.get(CalendarSpinnerGroup.LAYOUT);
			if (oLayout != null) {
				if (oLayout.equals(CalendarSpinnerGroup.VERTICAL_LAYOUT)) {
					this.layoutType = CalendarSpinnerGroup.VERTICAL_LAYOUT;
				}
			}

			this.spinnerDays = new JSpinner();
			this.spinnerDays.setName(CalendarSpinnerGroup.DAYS);
			this.spinnerMonths = new JSpinner();
			this.spinnerMonths.setName(CalendarSpinnerGroup.MONTHS);
			this.spinnerYears = new JSpinner();
			this.spinnerYears.setName(CalendarSpinnerGroup.YEARS);

			this.setDataFieldPreferredSize();

			// spinnerDays.setPreferredSize(d);
			// spinnerMonths.setPreferredSize(d);
			// spinnerYears.setPreferredSize(d);

			this.spinnerDateModel = new CalendarSpinnerDataFieldDateModel();
			this.spinnerDays.setModel(this.spinnerDateModel);
			this.spinnerMonths.setModel(this.spinnerDateModel);
			this.spinnerYears.setModel(this.spinnerDateModel);

			// DateEditor spDaysEditor = new DateEditor(spinnerDays,"d");
			// spinnerDays.setEditor(spDaysEditor);
			// DateEditor spMonthsEditor = new DateEditor(spinnerMonths,"MMM");
			// spinnerMonths.setEditor(spMonthsEditor);
			// DateEditor spYearsEditor = new DateEditor(spinnerYears,"yyyy");
			// spinnerYears.setEditor(spYearsEditor);

			final CalendarSpinnerDataFieldDateEditor spDaysEditor = new CalendarSpinnerDataFieldDateEditor(this.spinnerDays,
					"d");
			this.spinnerDays.setEditor(spDaysEditor);
			final CalendarSpinnerDataFieldDateEditor spMonthsEditor = new CalendarSpinnerDataFieldDateEditor(
					this.spinnerMonths, "MMMMM");
			this.spinnerMonths.setEditor(spMonthsEditor);
			final CalendarSpinnerDataFieldDateEditor spYearsEditor = new CalendarSpinnerDataFieldDateEditor(this.spinnerYears,
					"yyyy");
			this.spinnerYears.setEditor(spYearsEditor);

			((CalendarSpinnerDataFieldDateEditor) this.spinnerDays.getEditor()).getTextField()
			.setDocument(new SpinnerDateDocument("d"));
			((CalendarSpinnerDataFieldDateEditor) this.spinnerMonths.getEditor()).getTextField()
			.setDocument(new SpinnerDateDocument("MMMMM"));
			((CalendarSpinnerDataFieldDateEditor) this.spinnerYears.getEditor()).getTextField()
			.setDocument(new SpinnerDateDocument("yyyy"));

			//
			// spDaysEditor.addMouseListener(new MouseAdapter(){
			// public void mouseClicked(logger e) {
			// ((SpinnerDateModel)spinnerDays.getModel()).setCalendarField(Calendar.DAY_OF_MONTH);
			// super.mouseClicked(e);
			// }
			// });
			// spMonthsEditor.addMouseListener(new MouseAdapter(){
			// public void mouseClicked(logger e) {
			// ((SpinnerDateModel)spinnerMonths.getModel()).setCalendarField(Calendar.MONTH);
			// super.mouseClicked(e);
			// }
			// });
			// spYearsEditor.addMouseListener(new MouseAdapter(){
			// public void mouseClicked(logger e) {
			// ((SpinnerDateModel)spinnerYears.getModel()).setCalendarField(Calendar.YEAR);
			// super.mouseClicked(e);
			// }
			// });

			this.setDefaultLayout();

		}

		public void setDefaultLayout() {
			if (this.layoutType.equals(CalendarSpinnerGroup.HORIZONTAL_LAYOUT)) {
				this.add(this.spinnerDays, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				this.add(this.spinnerMonths, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				this.add(this.spinnerYears, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
			} else if (this.layoutType.equals(CalendarSpinnerGroup.VERTICAL_LAYOUT)) {
				this.add(this.spinnerDays, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				this.add(this.spinnerMonths, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
				this.add(this.spinnerYears, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST,
						GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
			}
		}

		public void setValue(final Object value) {
			if (this.spinnerDateModel != null) {
				this.spinnerDateModel.setValue(value);
			}
		}

		public Object getValue() {
			if (this.spinnerDateModel != null) {
				return this.spinnerDateModel.getValue();
			}
			return null;
		}

		@Override
		public void setEnabled(final boolean enabled) {
			this.spinnerDays.setEnabled(enabled);
			this.spinnerMonths.setEnabled(enabled);
			this.spinnerYears.setEnabled(enabled);
			super.setEnabled(enabled);
		}

		public void addChangeListener(final ChangeListener l) {
			this.spinnerDateModel.addChangeListener(l);
		}

		@Override
		public void setFont(final Font font) {
			if (this.spinnerDays != null) {
				this.spinnerDays.setFont(font);
				this.spinnerDays.getEditor().setFont(font);
			}
			if (this.spinnerMonths != null) {
				this.spinnerMonths.setFont(font);
				this.spinnerMonths.getEditor().setFont(font);
			}
			if (this.spinnerYears != null) {
				this.spinnerYears.setFont(font);
			}
			super.setFont(font);
		}

		public void setDataFieldPreferredSize() {
			if (this.layoutType.equals(CalendarSpinnerGroup.VERTICAL_LAYOUT)) {
				// The maximum size is set by the largest month (September).
				// Trick: Uses a JTextField to calculate the preferred height
				final JTextField aux = new JTextField();
				aux.setFont(this.spinnerMonths.getFont());
				aux.setText("september sep");
				final Dimension parentDimension = aux.getPreferredSize();
				final int height = (int) parentDimension.getHeight();
				final int width = parentDimension.width;// + parentDimension.width/8;
				final Dimension d = new Dimension(width, height);
				this.spinnerDays.setPreferredSize(d);
				this.spinnerMonths.setPreferredSize(d);
				this.spinnerYears.setPreferredSize(d);
			}
			if (this.layoutType.equals(CalendarSpinnerGroup.HORIZONTAL_LAYOUT)) {
				final JTextField aux = new JTextField();
				aux.setFont(this.spinnerMonths.getFont());

				aux.setText("30 30");
				Dimension parentDimension = aux.getPreferredSize();
				int height = (int) parentDimension.getHeight();
				int width = parentDimension.width;
				Dimension d = new Dimension(width, height);
				this.spinnerDays.setPreferredSize(d);

				aux.setText("september sep");
				parentDimension = aux.getPreferredSize();
				height = (int) parentDimension.getHeight();
				width = parentDimension.width;
				d = new Dimension(width, height);
				this.spinnerMonths.setPreferredSize(d);

				aux.setText("2000 20");
				parentDimension = aux.getPreferredSize();
				height = (int) parentDimension.getHeight();
				width = parentDimension.width;
				d = new Dimension(width, height);
				this.spinnerYears.setPreferredSize(d);
			}
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale l) {
			final Document docDays = ((DefaultEditor) this.spinnerDays.getEditor()).getTextField().getDocument();
			if ((docDays != null) && (docDays instanceof Internationalization)) {
				((Internationalization) docDays).setComponentLocale(l);
			}
			final Document docMonths = ((DefaultEditor) this.spinnerMonths.getEditor()).getTextField().getDocument();
			if ((docMonths != null) && (docMonths instanceof Internationalization)) {
				((Internationalization) docMonths).setComponentLocale(l);
			}
			final Document docYears = ((DefaultEditor) this.spinnerYears.getEditor()).getTextField().getDocument();
			if ((docYears != null) && (docYears instanceof Internationalization)) {
				((Internationalization) docYears).setComponentLocale(l);
			}

		}

		@Override
		public void setResourceBundle(final ResourceBundle resourceBundle) {

		}

	}

	public static class CalendarSpinnerDataFieldDateModel extends SpinnerDateModel {

		protected boolean nullSelection = false;

		public CalendarSpinnerDataFieldDateModel() {
			super();
		}

		public CalendarSpinnerDataFieldDateModel(final Date value, final Comparable start, final Comparable end, final int calendarField) {
			super(value, start, end, calendarField);
		}

		@Override
		public void setValue(final Object value) {

			if (value == null) {
				this.nullSelection = true;
				this.fireStateChanged();
				return;
			} else {
				this.nullSelection = false;
				super.setValue(value);
				// fireStateChanged();
			}
		}

		@Override
		public Object getValue() {
			if (this.nullSelection) {
				return null;
			}
			return super.getValue();
		}

		@Override
		public Object getPreviousValue() {
			return super.getPreviousValue();
		}

		@Override
		public Object getNextValue() {
			return super.getNextValue();
		}

		@Override
		public void setCalendarField(final int calendarField) {
			super.setCalendarField(calendarField);
		}

	}

	public static class CalendarSpinnerDataFieldDateEditor extends DateEditor {

		public CalendarSpinnerDataFieldDateEditor(final JSpinner arg0, final String arg1) {
			super(arg0, arg1);
		}

		public CalendarSpinnerDataFieldDateEditor(final JSpinner spinner) {
			super(spinner);
		}

		@Override
		public void commitEdit() throws ParseException {
			try {
				super.commitEdit();
			} catch (final Exception e) {
				CalendarSpinnerDataField.logger.trace(null, e);
			}
		}

		@Override
		public String getToolTipText() {
			return this.getTextField().getValue() == null ? null : this.getTextField().getValue().toString();
		}

		@Override
		public void stateChanged(final ChangeEvent e) {

			final JSpinner spinner = (JSpinner) e.getSource();
			final Object value = spinner.getValue();
			if (value == null) {
				try {
					this.getTextField().getDocument().remove(0, this.getTextField().getDocument().getLength());
				} catch (final BadLocationException ex) {
					CalendarSpinnerDataField.logger.error(null, ex);
				}
			} else {
				final Document doc = this.getTextField().getDocument();
				final SpinnerDateDocument dateDoc = (SpinnerDateDocument) doc;
				dateDoc.setValue((Date) value);
				// super.stateChanged(e);
			}
		}

		// public void stateChanged(ChangeEvent e) {
		// JSpinner spinner = (JSpinner)(e.getSource());
		// Object value = spinner.getValue();
		// if (value==null){
		// try {
		// this.getTextField().getDocument().remove(0,
		// getTextField().getDocument().getLength());
		// } catch (BadLocationException e1) {
		// e1.printStackTrace();
		// }
		// } else {
		// Document dateDoc = getTextField().getDocument();
		// if(currentDate == null){
		// currentDate = new SimpleDateFormat();
		// }
		// currentDate.getCalendar().setTime((Date)value);
		// Object dateValue = null;
		// if(spinner.getName().equals(CalendarSpinnerGroup.DAYS)){
		// dateValue = new
		// Integer(currentDate.getCalendar().get(Calendar.DAY_OF_MONTH));
		// } else if(spinner.getName().equals(CalendarSpinnerGroup.MONTHS)){
		// dateValue = new
		// Integer(currentDate.getCalendar().get(Calendar.MONTH));
		// } else if(spinner.getName().equals(CalendarSpinnerGroup.YEARS)){
		// dateValue = new
		// Integer(currentDate.getCalendar().get(Calendar.YEAR));
		// } else{
		// dateValue = value;
		// }
		//
		// try {
		// this.getTextField().getDocument().remove(0,
		// getTextField().getDocument().getLength());
		// dateDoc.insertString(0, dateValue.toString(), null);
		// } catch (BadLocationException e1) {
		// e1.printStackTrace();
		// }
		// }
		// }

	}

	/**
	 * This class implements a inner listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerListener implements ChangeListener {

		/**
		 * The condition about inner listener activation. By default, true
		 */
		protected boolean innerListenerEnabled = true;

		/**
		 * Stores the inner value. By default, null.
		 */
		protected Object storeInnerValue = null;

		/**
		 * Sets enable the inner listener in function of condition.
		 * <p>
		 * @param eanbled the condition of activation
		 */
		public void setInnerListenerEnabled(final boolean enabled) {
			this.innerListenerEnabled = enabled;
		}

		/**
		 * Gets the inner value.
		 * <p>
		 * @return the stored inner value
		 */
		public Object getInnerValue() {
			return this.storeInnerValue;
		}

		/**
		 * Sets the inner value.
		 * <p>
		 * @param o the object to set
		 */
		public void setInnerValue(final Object o) {
			this.storeInnerValue = o;
		}

		/**
		 * Compares the inner value with parameter.
		 * <p>
		 * @param newValue the value to compare
		 * @return the condition
		 */
		protected boolean isEqualInnerValue(final Object newValue) {
			if ((newValue == null) && (this.storeInnerValue == null)) {
				return true;
			}
			if ((newValue == null) || (this.storeInnerValue == null)) {
				return false;
			}
			return this.storeInnerValue.equals(newValue);
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (this.innerListenerEnabled) {
				CalendarSpinnerDataField.this.fireValueChanged(CalendarSpinnerDataField.this.getValue(),
						this.storeInnerValue, ValueEvent.USER_CHANGE);
				this.storeInnerValue = CalendarSpinnerDataField.this.getValue();
			}

		}

	};

	protected static final String DELETE_BUTTON = "deletebutton";

	protected static final String CALENDAR_BUTTON = "calendarbutton";

	protected InnerListener innerListener = new InnerListener();

	protected boolean deletebutton = false;

	protected boolean calendarbutton = false;

	protected FieldButton deleteButton = null;

	protected FieldButton calendarButton = null;

	protected EJDialog vCalendar = null;

	protected GregorianCalendar mainCalendar = null;

	protected boolean calendarChange = false;

	protected String calendarTitleKey = "calendar";

	protected VisualCalendarComponent calendarComp = null;

	public CalendarSpinnerDataField(final Map parameters) {
		this.init(parameters);
	}

	@Override
	public void init(final Map parameters) {

		this.createDataField(parameters);
		super.init(parameters);

		final Object oDeleteButton = parameters.get(CalendarSpinnerDataField.DELETE_BUTTON);
		if (oDeleteButton != null) {
			if (oDeleteButton.equals("yes")) {
				this.deletebutton = true;
			}
		}

		final Object oCalendarButton = parameters.get(CalendarSpinnerDataField.CALENDAR_BUTTON);
		if (oCalendarButton != null) {
			if (oCalendarButton.equals("yes")) {
				this.calendarbutton = true;
			}
		}

		if (this.deletebutton || this.calendarbutton) {
			this.installButtons();
		}

		this.registerKey();
		this.installInnerListener();

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

		if (this.deletebutton) {
			this.changeButton(this.deleteButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
		}
		if (this.calendarbutton) {
			this.changeButton(this.calendarButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
		}
	}

	protected void createDataField(final Map parameters) {
		final CalendarSpinnerGroup spinner = new CalendarSpinnerGroup(parameters) {

			@Override
			public boolean isVisible() {
				return super.isVisible();
			}
		};

		this.dataField = spinner;
	}

	public void installButtons() {

		if (this.calendarbutton) {
			this.calendarButton = new FieldButton();
			this.calendarButton.setIcon(ImageManager.getIcon(ImageManager.CALENDAR));
			this.calendarButton.setMargin(new Insets(0, 0, 0, 0));
			this.calendarButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent event) {
					CalendarSpinnerDataField.this.showCalendar((Component) event.getSource());
				}
			});
			super.add(this.calendarButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
							GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		if (this.deletebutton) {
			this.deleteButton = new FieldButton();
			this.deleteButton.setIcon(ImageManager.getIcon(ImageManager.DELETE));
			this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
			this.deleteButton
			.setToolTipText(ApplicationManager.getTranslation("datafield.reset_field", this.resources));
			this.deleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					CalendarSpinnerDataField.this.deleteUserData();
				}
			});
			super.add(this.deleteButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
							GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

	}

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
			final Object oNewValue = this.getValue();
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
					CalendarSpinnerDataField.logger.debug(null, e);
				} else {
					CalendarSpinnerDataField.logger.trace(null, e);
				}
			}

			this.calendarComp = new VisualCalendarComponent(this.getLocale());
			d.getContentPane().add(this.calendarComp);
			d.pack();
			this.calendarComp.addCalendarListener(new CalendarListener() {

				@Override
				public void dateChanged(final CalendarEvent e) {
					CalendarSpinnerDataField.this
					.setValueFromComponent(CalendarSpinnerDataField.this.calendarComp.getCurrentDate());
				}
			});
			this.calendarComp.addMouseListenerToDaysTable(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 2) {
						CalendarSpinnerDataField.this.vCalendar.setVisible(false);
					}
				}
			});
		}
	}

	public void setValueFromComponent(final Object auxValue) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.innerListener.getInnerValue();
		if (auxValue instanceof Timestamp) {
			final Object oValue = new Date(((Timestamp) auxValue).getTime());
			((CalendarSpinnerGroup) this.dataField).setValue(auxValue);
			this.innerListener.setInnerValue(this.getValue());
			if (this.calendarChange) {
				this.fireValueChanged(this.innerListener.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
			}

		} else if (auxValue instanceof Date) {
			((CalendarSpinnerGroup) this.dataField).setValue(auxValue);
			this.innerListener.setInnerValue(this.getValue());
			if (this.calendarChange) {
				this.fireValueChanged(this.innerListener.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
			}
		}
		this.setInnerListenerEnabled(true);
	}

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

	@Override
	public void deleteData() {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((CalendarSpinnerGroup) this.dataField).setValue(null);
		this.valueSave = this.getValue();
		this.innerListener.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	protected void deleteUserData() {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((CalendarSpinnerGroup) this.dataField).setValue(null);
		this.innerListener.setInnerValue(this.getValue());
		this.fireValueChanged(this.innerListener.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public boolean isEmpty() {
		final Object obj = ((CalendarSpinnerGroup) this.dataField).getValue();
		if (obj == null) {
			return true;
		} else {
			if (obj instanceof String) {
				if (((String) obj).length() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return ((CalendarSpinnerGroup) this.dataField).getValue();
	}

	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((CalendarSpinnerGroup) this.dataField).setValue(value);
		this.valueSave = this.getValue();
		this.innerListener.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		((CalendarSpinnerGroup) this.dataField).setEnabled(enabled);
		if (this.deleteButton != null) {
			this.deleteButton.setEnabled(enabled);
		}
		if (this.calendarButton != null) {
			this.calendarButton.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	@Override
	public int getSQLDataType() {
		return 0;
	}

	protected void setInnerListenerEnabled(final boolean enabled) {
		this.innerListener.setInnerListenerEnabled(enabled);
	}

	/**
	 * Installs the inner listener.
	 */
	protected void installInnerListener() {
		if (this.dataField != null) {
			((CalendarSpinnerGroup) this.dataField).addChangeListener(this.innerListener);
		}
	}

	public FieldButton getDeleteButton() {
		return this.deleteButton;
	}

	public FieldButton getCalendarButton() {
		return this.calendarButton;
	}

	@Override
	public void setFont(final Font font) {

		if (this.dataField != null) {
			((CalendarSpinnerGroup) this.dataField).setFont(font);
		}
		super.setFont(font);
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// Object value = getValue();
		// // ((CalendarSpinnerGroup)this.dataField).setComponentLocale(l);
		// super.setComponentLocale(l);
		//
		// boolean events = this.fireValueEvents;
		// this.fireValueEvents = false;
		// if(value!=null){
		// this.setValue(value);
		// }
		// this.fireValueEvents = events;

		// Locale l = getSameCountryLocale(loc);
		final Object value = this.getValue();
		this.setLocale(l);
		this.locale = l;

		this.setInnerListenerEnabled(false);
		try {
			// ((DateDocument) ((JTextField)
			// dataField).getDocument()).setComponentLocale(l);
			super.setComponentLocale(l);
			this.mainCalendar = new GregorianCalendar(l);
			if (this.vCalendar != null) {
				this.vCalendar.setLocale(l);
			}
			if (this.calendarComp != null) {
				this.calendarComp.setCalendarLocale(l);
			}
		} catch (final Exception ex) {
			CalendarSpinnerDataField.logger.error(null, ex);
		}
		if (value != null) {
			this.setValue(value);
		}
		this.setInnerListenerEnabled(true);

	}

	protected void registerKey() {
		final InputMap inMap = this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actMap = this.getActionMap();
		final KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.CTRL_MASK, true);
		final AbstractAction act = new AbstractAction() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final CalendarSpinnerGroup group = (CalendarSpinnerGroup) CalendarSpinnerDataField.this.dataField;
				CalendarSpinnerDataField.logger
				.debug(((DefaultEditor) group.getSpinnerDays().getEditor()).getTextField().getValue().toString());
				CalendarSpinnerDataField.logger
				.debug(((DefaultEditor) group.getSpinnerMonths().getEditor()).getTextField().getValue().toString());
				CalendarSpinnerDataField.logger
				.debug(((DefaultEditor) group.getSpinnerYears().getEditor()).getTextField().getValue().toString());
			}

		};
		final String key = "values";
		inMap.put(ks, key);
		actMap.put(key, act);
	}

}
