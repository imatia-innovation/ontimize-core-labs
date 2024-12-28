package com.ontimize.util.calendar;

import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

/**
 * Implements an international calendar
 *
 * @deprecated
 * @version 1.0 06/02/2001
 */
@Deprecated
public class CalendarDialog extends JDialog {

	private static final Logger logger = LoggerFactory.getLogger(CalendarDialog.class);

	/**
	 * First: Calendar show the days in a typical representation. The header shows the day names in
	 * abbreviation mode and the tooltip show the complete names. This component uses a table and a
	 * table model
	 */

	public static class CalendarModel extends AbstractTableModel {

		protected GregorianCalendar calendar = null;

		protected int dayCount = 0;

		protected String[] dayNames = null;

		protected Locale l = null;

		public CalendarModel(final Locale l) {
			super();
			this.l = l;
			this.calendar = (GregorianCalendar) Calendar.getInstance(l);
			this.updateModel();
		}

		@Override
		public String getColumnName(final int index) {
			return this.dayNames[index];
		}

		public int getYear() {
			return this.calendar.get(Calendar.YEAR);
		}

		public int getMonth() {
			return this.calendar.get(Calendar.MONTH);
		}

		public void setYear(final int y) {
			this.calendar.set(Calendar.YEAR, y);
			this.updateModel();
		}

		public void setMonth(final int m) {
			this.calendar.set(Calendar.MONTH, m);
			this.updateModel();
		}

		protected void updateModel() {
			final int minDay = this.calendar.getActualMinimum(Calendar.DAY_OF_WEEK);
			final int maxDay = this.calendar.getActualMaximum(Calendar.DAY_OF_WEEK) + 1;
			int daysNumber = 0;
			for (int i = minDay; i <= maxDay; i++) {
				daysNumber++;
			}
			this.dayCount = daysNumber;
			this.dayNames = new String[this.dayCount];
			DateFormatSymbols dateFormatSymbols = null;
			if (this.l != null) {
				dateFormatSymbols = new DateFormatSymbols(this.l);
			} else {
				dateFormatSymbols = new DateFormatSymbols();
			}
			final String[] weekDayNames = dateFormatSymbols.getShortWeekdays();
			// First week day
			final int iFirstWeekDay = this.calendar.getFirstDayOfWeek();
			// Create the column names
			for (int i = iFirstWeekDay; i < weekDayNames.length; i++) {
				if ((weekDayNames[i] != null) && !weekDayNames[i].equals("")) {
					this.dayNames[i - iFirstWeekDay] = weekDayNames[i];
				}
			}
			this.calendar.set(Calendar.DAY_OF_MONTH, this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		}

		public void setLocale(final Locale l) {
			this.calendar = (GregorianCalendar) Calendar.getInstance(l);
			this.updateModel();
		}

		@Override
		public int getRowCount() {
			return 7;
		}

		@Override
		public int getColumnCount() {
			return this.dayCount;
		}

		@Override
		public Object getValueAt(final int f, final int c) {
			// Calculate
			final int firstMonthDay = this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
			final int lastMonthDay = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

			this.calendar.set(Calendar.DAY_OF_MONTH, this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
			final int iWeekDay = this.calendar.get(Calendar.DAY_OF_WEEK);
			final int columnWithFirstDayCell = iWeekDay - firstMonthDay;
			if ((f == 0) && (c < columnWithFirstDayCell)) {
				return null;
			} else {
				final int columsPerRow = this.getColumnCount();
				final int iValue = ((columsPerRow * f) + c + 1) - columnWithFirstDayCell;
				if (iValue > lastMonthDay) {
					return null;
				}
				return new Integer(iValue);
			}
		}

	}

	public static class CalendarCellRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
				final int row, final int column) {
			final Component componente = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
			if ((hasFocus) || (selected)) {
				componente.setBackground(Color.cyan);
				componente.setForeground(Color.red);
			}
			return componente;
		}

	}

	public static String titleKey = "calendar";

	// Internal class to renderer the selected item
	class InnerCalendarCellRenderer extends DefaultTableCellRenderer {

		public InnerCalendarCellRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
				final int row, final int column) {
			final Component component = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
			if ((hasFocus) || (selected)) {
				component.setBackground(Color.cyan);
				component.setForeground(Color.red);
			}
			return component;
		}

	}

	class CalendarTable extends JTable {

		public CalendarTable() {
			this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent evento) {
					final ListSelectionModel lsm = (ListSelectionModel) evento.getSource();
					final int selectedRow = lsm.getMinSelectionIndex();
					CalendarDialog.this.dayTable.setDefaultRenderer(Object.class, new InnerCalendarCellRenderer());
					final String sDay = CalendarDialog.this.dayTable
							.getValueAt(selectedRow, CalendarDialog.this.dayTable.getSelectedColumn())
							.toString();
					int dayNumber = 0;
					int iYear = 0;
					int iMonth = 0;
					try {
						dayNumber = Integer.parseInt(sDay);
						iMonth = CalendarDialog.this.months.getSelectedIndex();
						iYear = Integer.parseInt(CalendarDialog.this.years.getText());
						final GregorianCalendar calendarAux = new GregorianCalendar();
						calendarAux.set(Calendar.YEAR, iYear);
						calendarAux.set(Calendar.MONTH, iMonth);
						calendarAux.set(Calendar.DAY_OF_MONTH, dayNumber);
					} catch (final Exception e) {
						CalendarDialog.logger.trace(null, e);
					}
				}
			});
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					if (event.getClickCount() == 2) {
						CalendarDialog.this.dispose();
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
					CalendarDialog.this.dayTable.setDefaultRenderer(Object.class, new InnerCalendarCellRenderer());
					final String sDay = CalendarDialog.this.dayTable
							.getValueAt(selectedRow, CalendarDialog.this.dayTable.getSelectedColumn())
							.toString();
					int dayNumber = 0;
					int iYear = 0;
					int iMonth = 0;
					try {
						dayNumber = Integer.parseInt(sDay);
						iMonth = CalendarDialog.this.months.getSelectedIndex();
						iYear = Integer.parseInt(CalendarDialog.this.years.getText());
						final GregorianCalendar calendarAux = new GregorianCalendar();
						calendarAux.set(Calendar.YEAR, iYear);
						calendarAux.set(Calendar.MONTH, iMonth);
						calendarAux.set(Calendar.DAY_OF_MONTH, dayNumber);

					} catch (final Exception e) {
						CalendarDialog.logger.trace(null, e);
					}
				}
			});
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent event) {
					if (event.getClickCount() == 2) {
						CalendarDialog.this.dispose();
					}
				}
			});
		}

		@Override
		public TableCellRenderer getCellRenderer(final int row, final int column) {
			return new InnerCalendarCellRenderer();
		}

	}

	class YearDocument extends PlainDocument {

		public YearDocument() {
			super();
		}

		@Override
		public void insertString(final int offset, final String insertValue, final AttributeSet attributes) throws BadLocationException {
			if (insertValue.length() == 1) {
				if (!Character.isDigit(insertValue.charAt(0))) {
					return;
				} else {
					try {
						final StringBuilder sCurrentText = new StringBuilder(this.getText(0, this.getLength()));
						sCurrentText.insert(offset, insertValue);
						// Try to parse
						try {
							Integer.parseInt(sCurrentText.toString());
							super.insertString(offset, insertValue, attributes);
						} catch (final Exception e) {
							CalendarDialog.logger.trace(null, e);
						}
					} catch (final Exception e) {
						CalendarDialog.logger.trace(null, e);
					}
				}
			} else {
				try {
					final StringBuilder sCurrentText = new StringBuilder(this.getText(0, this.getLength()));
					sCurrentText.insert(offset, insertValue);
					// Try to parse
					try {
						Integer.parseInt(sCurrentText.toString());
						super.insertString(offset, insertValue, attributes);
					} catch (final Exception e) {
						CalendarDialog.logger.trace(null, e);
					}
				} catch (final Exception e) {
					CalendarDialog.logger.trace(null, e);
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

	protected List weekDayNameList = new Vector();

	protected List monthNameList = new Vector();

	protected Locale locale = null;

	public CalendarDialog(final Frame parentFrame, final Locale l, final Date date) {
		super(parentFrame, true);
		this.init(l, date);
	}

	public CalendarDialog(final Dialog parentDialog, final Locale l, final Date date) {
		super(parentDialog, true);
		this.init(l, date);
	}

	protected void init(final Locale l, final Date date) {
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.getContentPane().setLayout(this.layout);
		// Create the name list using the current locale
		this.locale = l;

		this.createNameList(l);

		this.addYears();
		this.months.setEditable(false);
		this.months.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalendarDialog.this.months_actionPerformed(e);
			}
		});
		this.addMonth();
		this.years.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent event) {
			}

			@Override
			public void insertUpdate(final DocumentEvent event) {
				CalendarDialog.this.updateDays();
			}

			@Override
			public void removeUpdate(final DocumentEvent event) {
				CalendarDialog.this.updateDays();
			}
		});
		this.years.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalendarDialog.this.years_actionPerformed(e);
			}
		});
		this.updateDays();
		// If the date argument is null then show the current date
		if (date == null) {
			this.months.setSelectedIndex(this.calendar.get(Calendar.MONTH));
			this.years.setText(Integer.toString(this.calendar.get(Calendar.YEAR)));
		} else {
			final GregorianCalendar calendarAux = new GregorianCalendar();
			calendarAux.setTime(date);
			this.years.setText(new Integer(calendarAux.get(Calendar.YEAR)).toString());
			this.months.setSelectedIndex(calendarAux.get(Calendar.MONTH));
		}

		this.dayTable.setCellSelectionEnabled(true);
		this.dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dayTable.setRowSelectionAllowed(false);
		this.dayTable.setDefaultEditor(Object.class, null);
		this.dayTable.setColumnSelectionAllowed(false);
		for (int i = 0; i < this.dayTable.getColumnCount(); i++) {
			this.dayTable.setDefaultEditor(this.dayTable.getColumnClass(i), null);
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

	}

	protected void initColumnWidth() {
		final int[] iWidth = new int[this.dayTable.getColumnCount()];
		int iDefaultWidth = 0;
		for (int i = 0; i < this.dayTable.getColumnCount(); i++) {
			for (int j = 0; j < this.dayTable.getRowCount(); j++) {
				final TableCellRenderer renderer = this.dayTable.getCellRenderer(i, j);
				try {
					final Object oValue = this.dayTable.getValueAt(i, j);
					if (oValue == null) {
						continue;
					}
					final Component rendererComponent = renderer.getTableCellRendererComponent(null, oValue, false, false, i,
							j);
					final int iAuxWidth = rendererComponent.getPreferredSize().width;
					iDefaultWidth = Math.max(iDefaultWidth, iAuxWidth);
				} catch (final Exception e) {
					CalendarDialog.logger.trace(null, e);
				}
			}
			iWidth[i] = iDefaultWidth;
			this.dayTable.getColumn(this.dayTable.getModel().getColumnName(i)).setPreferredWidth(iWidth[i]);
		}
	}

	protected void createNameList(final Locale l) {
		// Create the list with the names using the locale
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
		final String[] sWeekDays = dateFormatSymbols.getShortWeekdays();
		// First week day
		final int firstWeekDay = this.calendar.getFirstDayOfWeek();
		// Create the column names
		for (int i = firstWeekDay; i < sWeekDays.length; i++) {
			if ((sWeekDays[i] != null) && !sWeekDays[i].equals("")) {
				this.weekDayNameList.add(i - firstWeekDay, sWeekDays[i]);
			}
		}
		for (int i = 0; i < firstWeekDay; i++) {
			if ((sWeekDays[i] != null) && !sWeekDays[i].equals("")) {
				this.weekDayNameList.add(this.weekDayNameList.size(), sWeekDays[i]);
			}
		}
	}

	protected void updateDays() {
		final GregorianCalendar calendarAux = new GregorianCalendar();
		int iYear = calendarAux.get(Calendar.YEAR);
		try {
			iYear = Integer.parseInt(this.years.getText());
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalendarDialog.logger.debug(null, e);
			} else {
				CalendarDialog.logger.trace(null, e);
			}
		}
		final int iMonth = this.months.getSelectedIndex();
		this.calendar.set(iYear, iMonth, 1);
		final int minDay = this.calendar.getActualMinimum(Calendar.DATE);
		final int maxDay = this.calendar.getActualMaximum(Calendar.DATE);
		final int dayOfWeek = this.calendar.get(Calendar.DAY_OF_WEEK);
		final int firstWeekDay = this.calendar.getFirstDayOfWeek();

		if (this.dayTable != null) {
			this.getContentPane().remove(this.dayTable);
		}
		this.dayTable = new CalendarTable(((maxDay - minDay) / this.weekDayNameList.size()) + 1 + 1 + 2,
				this.weekDayNameList.size());
		this.dayTable.setCellSelectionEnabled(true);
		this.dayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.dayTable.setRowSelectionAllowed(false);
		this.dayTable.setColumnSelectionAllowed(false);
		this.dayTable.setDefaultEditor(Object.class, null);
		for (int i = 0; i < this.weekDayNameList.size(); i++) {
			this.dayTable.setValueAt(this.weekDayNameList.get(i), 0, i);
		}

		int iRow = 1;
		int iColumn = dayOfWeek - firstWeekDay;
		if (iColumn < 0) {
			iColumn = this.weekDayNameList.size() + iColumn;
		}
		for (int i = minDay; i < (maxDay + 1); i++) {
			if (iColumn >= (this.weekDayNameList.size() - 1)) {
				this.dayTable.setValueAt(new Integer(i), iRow, iColumn);
				iColumn = 0;
				iRow++;
			} else {
				this.dayTable.setValueAt(new Integer(i), iRow, iColumn);
				iColumn++;
			}

		}
		this.getContentPane()
		.add(this.dayTable, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.dayTable.tableChanged(new TableModelEvent(this.dayTable.getModel()));
		this.update(this.getGraphics());
		this.validate();
	}

	protected void addYears() {
		final GregorianCalendar calendar = new GregorianCalendar();
		this.years = new JTextField(new YearDocument(), new Integer(calendar.get(Calendar.YEAR)).toString(), 5);
		this.years.setColumns(5);
	}

	protected void addMonth() {
		this.months.removeAllItems();
		for (int i = 0; i < this.monthNameList.size(); i++) {
			this.months.addItem(this.monthNameList.get(i));
		}
		try {
			this.months.setSelectedIndex(this.calendar.get(Calendar.MONTH));
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalendarDialog.logger.debug(null, e);
			} else {
				CalendarDialog.logger.trace(null, e);
			}
		}
	}

	void years_actionPerformed(final ActionEvent e) {
		this.updateDays();
	}

	void months_actionPerformed(final ActionEvent e) {
		this.updateDays();
	}

	public void setResourceBundle(final ResourceBundle resources) {
		try {
			if (resources != null) {
				this.setTitle(resources.getString(CalendarDialog.titleKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalendarDialog.logger.debug(null, e);
			} else {
				CalendarDialog.logger.trace(null, e);
			}
		}
	}

	public void setLocaleComponente(final Locale l) {
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			final int iRow = this.dayTable.getSelectedRow();
			final int iColumn = this.dayTable.getSelectedColumn();
			if ((iRow != -1) && (iColumn != -1)) {
				final String sDay = this.dayTable.getValueAt(iRow, iColumn).toString();
				int iDayNumber = 0;
				int iYear = 0;
				int iMonth = 0;
				try {
					iDayNumber = Integer.parseInt(sDay);
					iMonth = this.months.getSelectedIndex();
					iYear = Integer.parseInt(this.years.getText());
					final GregorianCalendar calendarAux = new GregorianCalendar();
					calendarAux.set(Calendar.YEAR, iYear);
					calendarAux.set(Calendar.MONTH, iMonth);
					calendarAux.set(Calendar.DAY_OF_MONTH, iDayNumber);

					super.processWindowEvent(e);
				} catch (final Exception ex) {
					CalendarDialog.logger.trace(null, ex);
					super.processWindowEvent(e);
				}
			} else {
				super.processWindowEvent(e);
			}
		} else {
			super.processWindowEvent(e);
		}
	}

}
