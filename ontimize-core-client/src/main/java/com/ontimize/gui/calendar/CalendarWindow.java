package com.ontimize.gui.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.table.ObjectCellRenderer;

/**
 * This method implements the calendar window for the DateDataField component. It works
 * independently of the date component, but it must be associated at one data component.
 */
public class CalendarWindow extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(CalendarWindow.class);

	public static String titleKey = "calendar";

	ListSelectionListener selectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(final ListSelectionEvent event) {
			final int selectedRow = CalendarWindow.this.dayTable.getSelectedRow();
			final int selectedColumn = CalendarWindow.this.dayTable.getSelectedColumn();
			if ((selectedRow < 1) || (selectedColumn < 0)) {
				return;
			}
			CalendarWindow.this.dayTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());
			final Object oValue = CalendarWindow.this.dayTable.getValueAt(selectedRow, selectedColumn);
			if (oValue == null) {
				return;
			}
			final String sDay = oValue.toString();
			int dayNumber = 0;
			int year = 0;
			int month = 0;
			try {
				dayNumber = Integer.parseInt(sDay);
				month = CalendarWindow.this.months.getSelectedIndex();
				year = Integer.parseInt(CalendarWindow.this.years.getText());
				final GregorianCalendar calendarAux = new GregorianCalendar();
				calendarAux.set(Calendar.YEAR, year);
				calendarAux.set(Calendar.MONTH, month);
				calendarAux.set(Calendar.DAY_OF_MONTH, dayNumber);
				if (CalendarWindow.this.dateDataField != null) {
					CalendarWindow.this.dateDataField.setValue(calendarAux.getTime());
				}
			} catch (final Exception e) {
				CalendarWindow.logger.error(null, e);
			}
		}
	};

	class DayModel extends DefaultTableModel {

		protected GregorianCalendar calendarAux = new GregorianCalendar(CalendarWindow.this.locale);

		protected int minDay = 0;

		protected int maxDay = 0;

		protected int weekDay = 0;

		protected int firstWeekDay = 0;

		protected List weekDaysList = new Vector();

		public DayModel(final List weekDayNamesList) {
			super();
			this.weekDaysList = weekDayNamesList;
		}

		public void setMonthYear(final int year, final int month, final Locale l) {
			this.calendarAux = new GregorianCalendar(l);
			this.calendarAux.set(year, month, 1);
			// Maximum and minimum values of month days
			this.minDay = this.calendarAux.getActualMinimum(Calendar.DATE);
			this.maxDay = this.calendarAux.getActualMaximum(Calendar.DATE);
			// Look for the first day of the week;
			this.weekDay = this.calendarAux.get(Calendar.DAY_OF_WEEK);
			// First week day
			this.firstWeekDay = this.calendarAux.getFirstDayOfWeek();
			this.fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			try {
				return this.weekDaysList.size();
			} catch (final Exception e) {
				CalendarWindow.logger.trace(null, e);
				return 0;
			}
		}

		@Override
		public int getRowCount() {
			// From the first day of the week we count until 7,
			// increment 7 more until this number is greater than
			// the days in the month

			final int monthDaysNumber = (this.maxDay - this.minDay) + 1;
			// In the first row:
			int firstRow = 0;
			if ((this.weekDay - this.firstWeekDay) < 0) {
				firstRow = -(this.weekDay - this.firstWeekDay);
			} else {
				firstRow = 7 - (this.weekDay - this.firstWeekDay);
			}
			// Remain
			final int remain = monthDaysNumber - firstRow;
			// More rows are needed
			final double additionalRows = remain / 7.0;
			if (additionalRows > (int) additionalRows) {
				return (int) additionalRows + 1 + 1 + 1;
				// +1 is used by the name of the days
			} else {
				return (int) additionalRows + 1 + 1;
			}
		}

		@Override
		public Object getValueAt(final int row, final int column) {
			if (row == 0) {
				if (column <= CalendarWindow.this.weekDayNamesList.size()) {
					return CalendarWindow.this.weekDayNamesList.get(column);
				} else {
					return null;
				}
			} else {
				// Calculate the day in the cell.
				// The cell(1,weekDay-firstWeekDay) is the origin, there is the
				// 1.
				int column1 = this.weekDay - this.firstWeekDay;
				if (column1 < 0) {
					column1 = 7 + column1;
				}
				// Additional cells count from the previous one in horizontal.
				final int additionalCells = (column - column1) + ((row - 1) * 7);
				if (((additionalCells + 1) < this.minDay) || ((additionalCells + 1) > this.maxDay)) {
					return null;
				} else {
					return new Integer(additionalCells + 1);
				}
			}
		}

	}

	// Internal class for selections renderer
	class CalendarCellRenderer extends ObjectCellRenderer {

		protected JTableHeader tableHeader = new JTableHeader();

		protected BevelBorder border = null;

		public CalendarCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
				final int row, final int column) {
			Component component = null;
			if (row == 0) {
				component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
				component.setBackground(Color.gray.brighter());
				if (component instanceof JComponent) {
					if (this.border == null) {
						this.border = new BevelBorder(BevelBorder.RAISED, component.getBackground(), Color.white,
								component.getBackground(), Color.darkGray);
					}
					((JComponent) component).setBorder(this.border);
				}
			} else {
				component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
				if (hasFocus) {
					component.setBackground(Color.cyan.darker());
					component.setForeground(Color.red);
				}
			}
			return component;
		}

	}

	class CalendarTable extends JTable {

		public CalendarTable() {
			this.setDefaultRenderer(Object.class, new CalendarCellRenderer());
			this.setDefaultRenderer(String.class, new CalendarCellRenderer());
			this.setCellSelectionEnabled(true);
			this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			for (int i = 0; i < this.getColumnCount(); i++) {
				this.setDefaultEditor(this.getColumnClass(i), null);
			}
			this.getColumnModel().getSelectionModel().addListSelectionListener(CalendarWindow.this.selectionListener);
			this.getSelectionModel().addListSelectionListener(CalendarWindow.this.selectionListener);
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					if (event.getClickCount() == 2) {
						CalendarWindow.this.dispose();
					}
				}
			});
		}

		public CalendarTable(final int rows, final int columns) {
			super(rows, columns);
			this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent evento) {
					final ListSelectionModel lsm = (ListSelectionModel) evento.getSource();
					final int selectedRow = lsm.getMinSelectionIndex();
					CalendarWindow.this.dayTable.setDefaultRenderer(Object.class, new CalendarCellRenderer());
					final Object oDayValue = CalendarWindow.this.dayTable.getValueAt(selectedRow,
							CalendarWindow.this.dayTable.getSelectedColumn());
					if (oDayValue != null) {
						final String sDay = oDayValue.toString();
						int dayNumber = 0;
						int year = 0;
						int month = 0;
						try {
							dayNumber = Integer.parseInt(sDay);
							month = CalendarWindow.this.months.getSelectedIndex();
							year = Integer.parseInt(CalendarWindow.this.years.getText());
							final GregorianCalendar calendarAux = new GregorianCalendar();
							calendarAux.set(Calendar.YEAR, year);
							calendarAux.set(Calendar.MONTH, month);
							calendarAux.set(Calendar.DAY_OF_MONTH, dayNumber);
							if (CalendarWindow.this.dateDataField != null) {
								CalendarWindow.this.dateDataField.setValue(calendarAux.getTime());
							}
						} catch (final Exception e) {
							CalendarWindow.logger.trace(null, e);
						}
					}
				}
			});
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					if (event.getClickCount() == 2) {
						CalendarWindow.this.dispose();
					}
				}
			});
		}

	}

	class YearDocument extends PlainDocument {

		public YearDocument() {
			super();
		}

		@Override
		public void insertString(final int offset, final String string, final AttributeSet attributes) throws BadLocationException {
			// It is an integer number.
			// If the inserted value is a valid integer value then accept it, in
			// other case reject it.
			if (string.length() == 1) {
				if (!Character.isDigit(string.charAt(0))) {
					return;
				} else {
					try {
						final StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
						currentText.insert(offset, string);
						// Checks if it is a valid integer number
						try {
							Integer.parseInt(currentText.toString());
							super.insertString(offset, string, attributes);
						} catch (final Exception e) {
							CalendarWindow.logger.trace(null, e);
						}
					} catch (final Exception e) {
						CalendarWindow.logger.trace(null, e);
					}
				}
			} else {
				try {
					final StringBuilder currentText = new StringBuilder(this.getText(0, this.getLength()));
					currentText.insert(offset, string);
					// Checks if it is a valid integer number
					try {
						Integer.parseInt(currentText.toString());
						super.insertString(offset, string, attributes);
					} catch (final Exception e) {
						CalendarWindow.logger.trace(null, e);
					}
				} catch (final Exception e) {
					CalendarWindow.logger.trace(null, e);
				}
			}
		}

	}

	protected GregorianCalendar calendar = null;

	protected GridBagLayout layout = new GridBagLayout();

	protected JTextField years = null;

	;

	protected JComboBox months = new JComboBox();

	protected JTable dayTable = null;

	protected DayModel model = null;

	protected List weekDayNamesList = new Vector();

	protected List monthNameList = new Vector();

	protected Locale locale = null;

	protected DataComponent dateDataField = null;

	public CalendarWindow(final Frame parent, final Locale l, final DataComponent field, final Date date) {
		super(parent, true);
		this.init(l, field, date);
	}

	public CalendarWindow(final Dialog parentDialog, final Locale l, final DataComponent field, final Date date) {
		super(parentDialog, true);
		this.init(l, field, date);
	}

	protected void init(final Locale l, final DataComponent field, final Date date) {
		this.months.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.getContentPane().setLayout(this.layout);

		// Create the name lists depending of the locale
		this.locale = l;

		this.dateDataField = field;
		this.createNameList(l);
		this.model = new DayModel(this.weekDayNamesList);
		this.addYears();
		this.months.setEditable(false);
		this.months.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalendarWindow.this.updateDays();
			}
		});
		this.addMonth();
		this.years.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent event) {
			}

			@Override
			public void insertUpdate(final DocumentEvent event) {
				CalendarWindow.this.updateDays();
			}

			@Override
			public void removeUpdate(final DocumentEvent event) {
				CalendarWindow.this.updateDays();
			}
		});
		this.years.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalendarWindow.this.yearsActionPerformed(e);
			}
		});
		this.updateDays();
		for (int i = 0; i < this.dayTable.getColumnCount(); i++) {
			this.dayTable.setDefaultEditor(this.dayTable.getColumnClass(i), null);
		}
		// Show the current date if the argument date is null
		if (date == null) {
			this.months.setSelectedIndex(this.calendar.get(Calendar.MONTH));
			this.years.setText(Integer.toString(this.calendar.get(Calendar.YEAR)));
			// Look for the values of the days:
			for (int i = 1; i < this.dayTable.getRowCount(); i++) {
				for (int j = 0; j < this.dayTable.getColumnCount(); j++) {
					final Object oValue = this.dayTable.getValueAt(i, j);
					if (oValue != null) {
						final int monthDay = this.calendar.get(Calendar.DAY_OF_MONTH);
						if (oValue instanceof Integer) {
							if (((Integer) oValue).intValue() == monthDay) {
								this.dayTable.setRowSelectionInterval(i, i);
								this.dayTable.setColumnSelectionInterval(j, j);
								break;
							}
						}
					}
				}
			}
		} else {
			final GregorianCalendar calendarAux = new GregorianCalendar();
			calendarAux.setTime(date);
			this.years.setText(new Integer(calendarAux.get(Calendar.YEAR)).toString());
			this.months.setSelectedIndex(calendarAux.get(Calendar.MONTH));
			// Selected the day:
				for (int i = 0; i < this.dayTable.getRowCount(); i++) {
					for (int j = 0; j < this.dayTable.getColumnCount(); j++) {
						final Object oValue = this.dayTable.getValueAt(i, j);
						if (oValue != null) {
							try {
								if (calendarAux.get(Calendar.DAY_OF_MONTH) == Integer.parseInt(oValue.toString())) {
									// Do selection
									this.dayTable.setRowSelectionInterval(i, i);
									this.dayTable.setColumnSelectionInterval(j, j);
									break;
								}
							} catch (final Exception e) {
								CalendarWindow.logger.trace(null, e);
							}
						}
					}
				}
		}

		this.getContentPane()
		.add(this.years, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane()
		.add(this.months, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane()
		.add(this.dayTable, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		this.initColumnWidth();
		this.pack();
		this.setResizable(false);
		this.setLocation(
				((int) ((Container) field).getLocationOnScreen().getX() + ((Container) field).getWidth())
				- this.getWidth(),
				(int) ((Container) field).getLocationOnScreen().getY() + ((Container) field).getHeight());

	}

	protected void initColumnWidth() {
		// Variable to get the width
		final int[] widths = new int[this.dayTable.getColumnCount()];
		int preferredWidth = 0;
		for (int i = 0; i < this.dayTable.getColumnCount(); i++) {
			for (int j = 0; j < this.dayTable.getRowCount(); j++) {
				final TableCellRenderer renderer = this.dayTable.getCellRenderer(i, j);
				try {
					final Object oValue = this.dayTable.getValueAt(i, j);
					if (oValue == null) {
						continue;
					}
					final Component componenteRender = renderer.getTableCellRendererComponent(null, oValue, false, false, i,
							j);
					final int auxWidth = componenteRender.getPreferredSize().width;
					preferredWidth = Math.max(preferredWidth, auxWidth);
				} catch (final Exception e) {
					CalendarWindow.logger.trace(null, e);
				}
			}
			widths[i] = preferredWidth;
			this.dayTable.getColumn(this.dayTable.getModel().getColumnName(i)).setPreferredWidth(widths[i] + 2);
		}
	}

	protected void createNameList(final Locale l) {
		// Create the list with the names
		DateFormatSymbols dateFormatSymbols = null;
		if (l != null) {
			this.calendar = new GregorianCalendar(l);
			dateFormatSymbols = new DateFormatSymbols(l);
		} else {
			this.calendar = new GregorianCalendar();
			dateFormatSymbols = new DateFormatSymbols();
		}

		// Months
		final String[] monthNames = dateFormatSymbols.getMonths();
		for (int i = 0; i < 12; i++) {
			this.monthNameList.add(i, monthNames[i]);
		}
		// Week days
		final String[] weekDaysNames = dateFormatSymbols.getShortWeekdays();
		// First week day
		final int firstWeekDay = this.calendar.getFirstDayOfWeek();
		// Create column names
		for (int i = firstWeekDay; i < weekDaysNames.length; i++) {
			if ((weekDaysNames[i] != null) && !(weekDaysNames[i].equals(""))) {
				this.weekDayNamesList.add(i - firstWeekDay, weekDaysNames[i]);
			}
		}
		for (int i = 0; i < firstWeekDay; i++) {
			if ((weekDaysNames[i] != null) && !(weekDaysNames[i].equals(""))) {
				this.weekDayNamesList.add(this.weekDayNamesList.size(), weekDaysNames[i]);
			}
		}
	}

	protected void updateDays() {
		final GregorianCalendar calendarAux = new GregorianCalendar();
		int year = calendarAux.get(Calendar.YEAR);
		try {
			year = Integer.parseInt(this.years.getText());
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				CalendarWindow.logger.debug(null, e);
			} else {
				CalendarWindow.logger.trace(null, e);
			}
		}
		final int month = this.months.getSelectedIndex();
		// Column zero contains the first day of the week
		// Create the table with the appropriate number of rows and columns
		if (this.dayTable == null) {
			this.dayTable = new CalendarTable();
		}
		this.model.setMonthYear(year, month, this.locale);
		this.dayTable.setModel(this.model);
		this.dayTable.setCellSelectionEnabled(true);
		this.dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dayTable.setDefaultEditor(Object.class, null);
		this.dayTable.repaint();
	}

	protected void addYears() {
		final GregorianCalendar calendar = new GregorianCalendar();
		this.years = new JTextField(new YearDocument(), new Integer(calendar.get(Calendar.YEAR)).toString(), 5);
		this.years.setColumns(4);
		this.years.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
	}

	protected void addMonth() {
		this.months.removeAllItems();

		for (int i = 0; i < this.monthNameList.size(); i++) {
			this.months.addItem(this.monthNameList.get(i));
		}
		try {
			this.months.setSelectedIndex(this.calendar.get(Calendar.MONTH));
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				CalendarWindow.logger.debug(null, e);
			} else {
				CalendarWindow.logger.trace(null, e);
			}
		}
	}

	void yearsActionPerformed(final ActionEvent e) {
		this.updateDays();
	}

	void monthActionPerformed(final ActionEvent e) {
		this.updateDays();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if (resources != null) {
				this.setTitle(resources.getString(CalendarWindow.titleKey));
			} else {
				this.setTitle(CalendarWindow.titleKey);
			}
		} catch (final Exception e) {
			this.setTitle(CalendarWindow.titleKey);
			if (ApplicationManager.DEBUG) {
				CalendarWindow.logger.debug(null, e);
			} else {
				CalendarWindow.logger.trace(null, e);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			final int row = this.dayTable.getSelectedRow();
			final int column = this.dayTable.getSelectedColumn();
			if ((row != -1) && (column != -1)) {
				final Object oDayValue = this.dayTable.getValueAt(row, column);
				if (oDayValue != null) {
					final String sDay = oDayValue.toString();
					int dayNumber = 0;
					int year = 0;
					int month = 0;
					try {
						dayNumber = Integer.parseInt(sDay);
						month = this.months.getSelectedIndex();
						year = Integer.parseInt(this.years.getText());
						final GregorianCalendar calendarAux = new GregorianCalendar();
						calendarAux.set(Calendar.YEAR, year);
						calendarAux.set(Calendar.MONTH, month);
						calendarAux.set(Calendar.DAY_OF_MONTH, dayNumber);
						if (this.dateDataField != null) {
							this.dateDataField.setValue(calendarAux.getTime());
						}
						super.processWindowEvent(e);
					} catch (final Exception ex) {
						CalendarWindow.logger.trace(null, ex);
						super.processWindowEvent(e);
					}
				}
			} else {
				super.processWindowEvent(e);
			}
		} else {
			super.processWindowEvent(e);
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

}
