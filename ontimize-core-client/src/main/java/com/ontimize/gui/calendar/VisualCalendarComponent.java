package com.ontimize.gui.calendar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.calendar.event.CalendarEvent;
import com.ontimize.gui.calendar.event.CalendarListener;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.images.ImageManager;

/**
 * Implements a calendar with selectable year, month and day. The calendar is sensitive to locale.
 * Year and month can be entered directly or selected. Other functionalities will be added, such
 * changing font, aspect...
 *
 * @version 1.0
 */
public class VisualCalendarComponent extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(VisualCalendarComponent.class);

	protected static Color GENERAL_BORDER_COLOR = new Color(200, 200, 215).darker();

	protected static Color SELECTED_DAY_BORDER_COLOR = new Color(210, 210, 225).darker();

	public static Color defaultDifferentDayColor = new Color(194, 212, 222);

	public static Border fieldsBorder = new LineBorder(VisualCalendarComponent.GENERAL_BORDER_COLOR);

	public static Color defaultBackgroundColor = new Color(0xD5e2e9);

	protected static String[] defaultShortWeekday = new String[] { "", "sunday.shortweekday", "monday.shortweekday",
			"tuesday.shortweekday", "wednesday.shortweekday",
			"thursday.shortweekday", "friday.shortweekday", "saturday.shortweekday" };

	/*
	 * English
	 */
	// public static String[] defaultShortWeekdaysEn = new String[] { "", "Su",
	// "Mo", "Tu", "We", "Th", "Fr", "Sa" };

	/*
	 * Spanish
	 */
	// public static String[] defaultShortWeekdaysEs = new String[] { "", "Do",
	// "Lu", "Ma", "Mi", "Ju", "Vi", "Sá" };

	/*
	 * French
	 */
	// public static String[] defaultShortWeekdaysFr = new String[] { "", "Di",
	// "Lu", "Ma", "Me", "Je", "Ve", "Sa" };

	/*
	 * Dutch
	 */
	// public static String[] defaultShortWeekdaysNl = new String[] { "", "Zo",
	// "Ma", "Di", "Wo", "Do", "Vr", "Za" };

	private JTextField tfYear = null;

	private JComboBox comboMonth = null;

	private JButton nextYearButton = null;

	private JButton prevYearBt = null;

	private JButton nextMonthButton = null;

	private JButton prevMonthBt = null;

	private JTable daysTable = null;

	protected JMenu menu = null;

	private Calendar calendar = null;

	private boolean fireCalendarEvents = true;

	protected boolean fireSelectionEvent = true;

	protected boolean moveBack = false;

	private boolean truncate = false;

	private JScrollPane scrollTable = null;

	private Locale calendarLocale = Locale.getDefault();

	private List listeners = null;

	public VisualCalendarComponent() {
		this(Locale.getDefault());
	}

	protected static class EButtonBorder extends SoftBevelBorder implements javax.swing.plaf.UIResource {

		public EButtonBorder() {
			super(BevelBorder.RAISED);

		}

		@Override
		public Color getHighlightInnerColor(final Component c) {
			final Color highlight = this.getHighlightInnerColor();
			return highlight != null ? highlight : c.getBackground();
		}

		@Override
		public Color getShadowInnerColor(final Component c) {
			final Color shadow = this.getShadowInnerColor();
			return shadow != null ? shadow : c.getBackground();
		}

		@Override
		public Color getShadowOuterColor(final Component c) {
			final Color shadow = this.getShadowInnerColor();
			return shadow != null ? shadow : c.getBackground().darker().darker();
		}

		@Override
		public Color getHighlightOuterColor(final Component c) {
			final Color highlight = this.getHighlightInnerColor();
			return highlight != null ? highlight : c.getBackground().brighter().brighter();
		}

		@Override
		public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int width, final int height) {
			boolean isPressed = false;

			if (c instanceof AbstractButton) {
				final AbstractButton b = (AbstractButton) c;
				final ButtonModel model = b.getModel();

				isPressed = (model.isPressed() && model.isArmed()) || model.isSelected();

				if (isPressed) {
					this.bevelType = BevelBorder.LOWERED;
				} else {
					this.bevelType = BevelBorder.RAISED;
				}
				super.paintBorder(c, g, x, y, width, height);
			}

		}

	}

	protected static class EButton extends JButton implements MouseListener {

		public EButton() {
			this.addMouseListener(this);
			this.setBorderPainted(false);
		}

		@Override
		public void updateUI() {
			super.updateUI();
			if (this.getBorder() instanceof CompoundBorder) {
				final Border b = ((CompoundBorder) this.getBorder()).getOutsideBorder();
				if (b instanceof javax.swing.plaf.basic.BasicBorders.ButtonBorder) {
					final Border be = new EButtonBorder();
					final CompoundBorder bn = new CompoundBorder(be, ((CompoundBorder) this.getBorder()).getInsideBorder());
					this.setBorder(bn);
				}
			}
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (this.isEnabled()) {
				this.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			this.setBorderPainted(false);
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

	}

	public static class HeaderRenderer extends DefaultTableCellRenderer {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		protected static final String HEADCELLRENDERER_NAME = "VisualCalendar:TableHeader.renderer";

		// Table Header Renderer configuration variables...
		public static Border defaultHeaderBorder = new LineBorder(VisualCalendarComponent.GENERAL_BORDER_COLOR);

		public static Color defaultBackgroundHeaderColor = new Color(200, 200, 215);

		public static Font headerFont;

		protected Border headerBorder = HeaderRenderer.defaultHeaderBorder;

		@Override
		public String getName() {
			return HeaderRenderer.HEADCELLRENDERER_NAME;
		}

		@Override
		public void setForeground(final Color c) {
			if (c != null) {
				super.setForeground(c);
			}
		}

		@Override
		public Component getTableCellRendererComponent(final JTable t, final Object v, final boolean sel, final boolean f, final int r, final int c) {
			final Component comp = super.getTableCellRendererComponent(t, v, sel, f, r, c);

			if (HeaderRenderer.headerFont != null) {
				comp.setFont(HeaderRenderer.headerFont);
			}

			if (!ApplicationManager.useOntimizePlaf) {
				comp.setBackground(HeaderRenderer.defaultBackgroundHeaderColor);
				if (comp instanceof JComponent) {
					((JComponent) comp).setBorder(this.headerBorder);
				}
			}

			if (comp instanceof JLabel) {
				((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
			}
			return comp;
		}

	}

	public static class DayRenderer extends DefaultTableCellRenderer {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		protected static final String CELLRENDERER_NAME = "VisualCalendar:Table.cellRenderer";

		public static Color dayOfWeekFgColor;

		public static Color dayOfWeekEndFgColor;

		public static Color daySelectedFgColor;

		public static Font headerFont;

		protected Border selectedBorder = BorderFactory
				.createLineBorder(VisualCalendarComponent.SELECTED_DAY_BORDER_COLOR);

		protected VisualCalendarComponent vc = null;

		protected Calendar calendar = null;

		private boolean paintTodayBorder = false;

		private boolean daySelected = false;

		public DayRenderer(final VisualCalendarComponent vc) {
			this.vc = vc;
			this.calendar = Calendar.getInstance(vc.getCalendarLocale());
			this.calendar.setLenient(false);
		}

		@Override
		public String getName() {
			return DayRenderer.CELLRENDERER_NAME;
		}

		@Override
		public void setForeground(final Color c) {
			if (c != null) {
				super.setForeground(c);
			}
		}

		@Override
		public Component getTableCellRendererComponent(final JTable t, Object v, final boolean sel, final boolean f, final int r, final int c) {
			if (v instanceof Date) {
				this.calendar.setTime((Date) v);
				v = new Integer(this.calendar.get(Calendar.DAY_OF_MONTH));
			}
			final Component comp = super.getTableCellRendererComponent(t, v, false, false, r, c);

			this.paintTodayBorder = false;

			if (v instanceof Integer) {
				final int day = ((Integer) v).intValue();
				this.calendar.setTime(new Date());
				if (day == this.calendar.get(Calendar.DAY_OF_MONTH)) {

					final int currentYear = this.calendar.get(Calendar.YEAR);
					final int currentMonth = this.calendar.get(Calendar.MONTH);
					if ((currentYear == this.vc.getYear()) && (currentMonth == this.vc.getMonth())
							&& !this.isDifferentMonthDay(v, r)) {
						this.paintTodayBorder = true;
					}
				}
			} else

				// Font configuration
				if (DayRenderer.headerFont != null) {
					comp.setFont(DayRenderer.headerFont);
				}
			// if(ApplicationManager.useOntimizePlaf){
			if (sel && ApplicationManager.useOntimizePlaf) {
				comp.setForeground(DayRenderer.daySelectedFgColor);
			} else if (this.isDifferentMonthDay(v, r)) {
				comp.setForeground(VisualCalendarComponent.defaultDifferentDayColor);
			} else if (this.isWeekendDay(v, r) && ApplicationManager.useOntimizePlaf) {
				comp.setForeground(DayRenderer.dayOfWeekEndFgColor);
			} else if (this.paintTodayBorder && ApplicationManager.useOntimizePlaf) {
				comp.setForeground(DayRenderer.daySelectedFgColor);
			} else if (ApplicationManager.useOntimizePlaf) {
				comp.setForeground(DayRenderer.dayOfWeekFgColor);
			} else {
				comp.setForeground(Color.black);
			}
			// }

			final Font font = comp.getFont();
			this.daySelected = false;
			if (sel) {
				comp.setFont(font.deriveFont((float) (font.getSize() + 2)));
				comp.setFont(font.deriveFont(Font.BOLD));
				this.daySelected = true;
			}
			if (comp instanceof JComponent) {
				if (sel && (v != null) && !ApplicationManager.useOntimizePlaf) {
					((JComponent) comp).setBorder(this.selectedBorder);
				} else {
					((JComponent) comp).setBorder(null);
				}
			}
			if (comp instanceof JLabel) {
				((JLabel) comp).setHorizontalAlignment(SwingConstants.CENTER);
			}

			return comp;
		}

		private boolean isWeekendDay(final Object v, final int row) {
			final int rowCount = this.vc.getDaysTable().getRowCount();
			if (v instanceof Integer) {
				final int day = ((Integer) v).intValue();

				int year = this.vc.getYear();
				int month = this.vc.getMonth();
				if ((row == 0) && (day > 7)) {
					if (month == 0) {
						year++;
					}
					month = (month + 11) % 12;
				}
				if (row == rowCount) {
					if (day < this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
						if (month == 11) {
							year--;
						}
						month = (month + 1) % 12;
					}
				}
				this.calendar.set(year, month, day);
				if ((Calendar.SATURDAY == this.calendar.get(Calendar.DAY_OF_WEEK))
						|| (Calendar.SUNDAY == this.calendar.get(Calendar.DAY_OF_WEEK))) {
					return true;
				}

			}
			return false;
		}

		private boolean isDifferentMonthDay(final Object v, final int row) {
			final int rowCount = this.vc.getDaysTable().getRowCount();
			if ((row != 0) && (row < (rowCount - 2))) {
				return false;
			}

			if (v instanceof Integer) {
				final int day = ((Integer) v).intValue();
				final int year = this.vc.getYear();
				final int month = this.vc.getMonth();
				if ((row == 0) && (day > 7)) {
					return true;
				}
				if ((row >= (rowCount - 2)) && (row != 0)) {
					if ((day < this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) && (day < 15)) {
						return true;
					}
				}
			}
			return false;
		}

		public boolean isPaintingToday() {
			return this.paintTodayBorder;
		}

		public boolean isDaySelected() {
			return this.daySelected;
		}

		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (this.paintTodayBorder && !ApplicationManager.useOntimizePlaf) {
				// Draw the circle
				final Graphics2D g2 = (Graphics2D) g;
				final RenderingHints oldRH = g2.getRenderingHints();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Color.red);
				g2.drawOval(1, 1, this.getWidth() - 3, this.getHeight() - 3);

				g2.setRenderingHints(oldRH);
			}
		}

		public void updateLocale() {
			this.calendar = Calendar.getInstance(this.vc.getCalendarLocale());
			this.calendar.setLenient(false);
		}

	}

	protected static class YearDocument extends PlainDocument {

		@Override
		public void insertString(final int offset, final String s, final AttributeSet at) throws BadLocationException {
			// Check that it is a number
			if (s.length() > 1) {
				final StringBuilder sb = new StringBuilder(this.getText(0, this.getLength()));
				sb.insert(offset, s);
				try {
					super.insertString(offset, s, at);
				} catch (final Exception e) {
					VisualCalendarComponent.logger.trace(null, e);
				}
			} else if (s.length() == 1) {
				// If is a character
				if (Character.isDigit(s.charAt(0))) {
					super.insertString(offset, s, at);
				}
			}
		}

	}

	protected class DayTableModel extends AbstractTableModel {

		protected Calendar calendar = null;

		protected DateFormatSymbols dateFormat = null;

		private Locale locale = null;

		private List listeners = new Vector(2);

		private JTable table = null;

		private boolean truncate = false;

		protected void fireCalendarEvent(final int d, final int m, final int y, final Locale l) {
			if (this.listeners == null) {
				return;
			}
			final CalendarEvent e = new CalendarEvent(this, d, m, y, l);
			for (int i = 0; i < this.listeners.size(); i++) {
				((CalendarListener) this.listeners.get(i)).dateChanged(e);
			}
		}

		public void addCalendarListener(final CalendarListener c) {
			if (this.listeners == null) {
				this.listeners = new Vector();
			}
			this.listeners.add(c);
		}

		public void removeCalendarListener(final CalendarListener c) {
			if (this.listeners != null) {
				this.listeners.remove(c);
			}
		}

		protected ListSelectionListener rowSelectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					DayTableModel.this.updateSelection();
				}
			}
		};

		protected ListSelectionListener columnSelectionListener = new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					DayTableModel.this.updateSelection();
				}
			}
		};

		public int getDay() {
			return this.calendar.get(Calendar.DAY_OF_MONTH);
		}

		public int getYear() {
			return this.calendar.get(Calendar.YEAR);
		}

		public int getMonth() {
			return this.calendar.get(Calendar.MONTH);
		}

		public void setDate(final Date d) {
			if (d != null) {
				this.calendar.setTime(d);
				this.calendar.set(Calendar.MILLISECOND, 0);
				if (this.truncate) {
					this.calendar.set(Calendar.SECOND, 0);
					this.calendar.set(Calendar.MINUTE, 0);
					this.calendar.set(Calendar.HOUR, 0);
				}
				this.fireCalendarEvent(this.getDay(), this.getMonth(), this.getYear(), this.locale);
			}
		}

		@Override
		public String getColumnName(final int c) {

			final String[] names = this.dateFormat.getShortWeekdays();
			final int firstWeekDay = this.calendar.getFirstDayOfWeek();
			// The first day in the week is 0
			final int offset = c + firstWeekDay;
			if (offset > this.getColumnCount()) {
				return names[offset - this.getColumnCount()];
			} else {
				return names[offset];
			}
		}

		public DayTableModel(final Locale l, final JTable t, final boolean trunc) {
			this.truncate = trunc;
			this.locale = l;
			this.setLocale(l);
			this.table = t;
			t.getSelectionModel().addListSelectionListener(this.rowSelectionListener);
			t.getColumnModel().getSelectionModel().addListSelectionListener(this.columnSelectionListener);
		}

		private void updateSelection() {
			if ((this.table.getSelectedRow() >= 0) && (this.table.getSelectedColumn() >= 0)
					&& !VisualCalendarComponent.this.fireSelectionEvent) {
				try {
					final Object value = this.getValueAt(this.table.getSelectedRow(), this.table.getSelectedColumn());
					if (value instanceof Date) {
						VisualCalendarComponent.this.setDate((Date) value);
					} else if (value instanceof Integer) {
						final Calendar auxCalendar = Calendar.getInstance(this.locale);
						auxCalendar.setLenient(false);
						auxCalendar.set(Calendar.MILLISECOND, 0);
						auxCalendar.set(Calendar.SECOND, 0);
						auxCalendar.set(Calendar.MINUTE, 0);
						auxCalendar.set(Calendar.HOUR, 0);
						auxCalendar.set(this.getYear(), this.getMonth(), 1);
						auxCalendar.add(Calendar.DAY_OF_MONTH, ((Integer) value).intValue() - 1);
						VisualCalendarComponent.this.setDate(auxCalendar.getTime());
					}
					// Integer i = (Integer)
					// if (i == null) return;
					// int dau = i.intValue();
					// updateMonthYear(dau);
					// VisualCalendarComponent.this.setDay(dau);
				} catch (final Exception e) {
					VisualCalendarComponent.logger.error(null, e);
				}
			}
		}

		public void updateMonthYear(final int dau) {
			final boolean fire = VisualCalendarComponent.this.fireCalendarEvents;
			VisualCalendarComponent.this.fireCalendarEvents = false;
			int yearu = this.getYear();
			int monthu = this.getMonth();
			if ((this.table.getSelectedRow() == 0) && (dau > 7)) {
				if (monthu == 0) {
					yearu--;
				}
				monthu = (this.getMonth() + 11) % 12;
				VisualCalendarComponent.this.moveBack = true;
			}
			if ((this.table.getSelectedRow() >= (this.table.getRowCount() - 2)) && (this.table.getSelectedRow() != 0)) {
				if ((dau < this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) && (dau < 15)) {
					if (monthu == 11) {
						yearu++;
					}
					monthu = (this.getMonth() + 1) % 12;
					VisualCalendarComponent.this.moveBack = false;
					// VisualCalendarComponent.this.setDay(dau);
					// this.calendar.set(Calendar.DAY_OF_MONTH, dau);
					// comboMonth.setSelectedIndex(index);
					// this.setMonth(index);
				}
			}
			final Calendar auxCalendar = Calendar.getInstance(this.locale);
			auxCalendar.setLenient(false);
			auxCalendar.set(Calendar.MILLISECOND, 0);
			auxCalendar.set(Calendar.SECOND, 0);
			auxCalendar.set(Calendar.MINUTE, 0);
			auxCalendar.set(Calendar.HOUR, 0);
			auxCalendar.set(yearu, monthu, dau);
			// setDate(auxCalendar.getTime());
			VisualCalendarComponent.this.setDate(auxCalendar.getTime());
			VisualCalendarComponent.this.fireCalendarEvents = fire;
		}

		@Override
		public int getRowCount() {
			return 6;
		}

		@Override
		public int getColumnCount() {
			return (this.calendar.getActualMaximum(Calendar.DAY_OF_WEEK)
					- this.calendar.getActualMinimum(Calendar.DAY_OF_WEEK)) + 1;
		}

		public Date getCurrentDate() {
			return this.calendar.getTime();
		}

		public Timestamp getCurrentTimestamp() {
			return new Timestamp(this.calendar.getTime().getTime());
		}

		public void setLocale(final Locale l) {
			if (this.calendar != null) {
				final int year = this.getYear();
				final int month = this.getMonth();
				final int day = this.getDay();
				this.calendar = Calendar.getInstance(l);
				this.calendar.setLenient(false);
				this.calendar.set(Calendar.MILLISECOND, 0);
				this.calendar.set(Calendar.SECOND, 0);
				this.calendar.set(Calendar.MINUTE, 0);
				this.calendar.set(Calendar.HOUR, 0);
				this.locale = l;
				this.dateFormat = new DateFormatSymbols(l);
				if (ApplicationManager.useOntimizePlaf) {
					this.dateFormat.setShortWeekdays(VisualCalendarComponent.this.getLocaleShortWeekDays(l));
				}
				this.fireTableChanged(new TableModelEvent(this));
				this.fireTableStructureChanged();

				VisualCalendarComponent.setDaysTablePreferenceSize(this.table);

				this.calendar.set(Calendar.YEAR, year);
				this.calendar.set(Calendar.MONTH, month);
				this.calendar.set(Calendar.DAY_OF_MONTH, day);
				this.fireTableDataChanged();
				this.fireCalendarEvent(this.getDay(), this.getMonth(), this.getYear(), this.locale);
			} else {
				this.calendar = Calendar.getInstance(l);
				this.calendar.setLenient(false);
				this.calendar.set(Calendar.MILLISECOND, 0);
				this.calendar.set(Calendar.SECOND, 0);
				this.calendar.set(Calendar.MINUTE, 0);
				this.calendar.set(Calendar.HOUR, 0);
				this.locale = l;
				this.dateFormat = new DateFormatSymbols(l);
				if (ApplicationManager.useOntimizePlaf) {
					this.dateFormat.setShortWeekdays(VisualCalendarComponent.this.getLocaleShortWeekDays(l));
				}
				this.fireTableChanged(new TableModelEvent(this));
			}
		}

		public void setYear(final int y) {
			this.calendar.set(Calendar.YEAR, y);
			this.fireTableDataChanged();
			this.fireCalendarEvent(this.getDay(), this.getMonth(), this.getYear(), this.locale);
		}

		public void setDay(final int d) {
			this.calendar.set(Calendar.DAY_OF_MONTH, d);
			this.fireCalendarEvent(this.getDay(), this.getMonth(), this.getYear(), this.locale);
		}

		public void setMonth(final int m) {
			int day = this.getDay();
			this.calendar.set(Calendar.DAY_OF_MONTH, 1);
			this.calendar.set(Calendar.MONTH, m);
			this.fireTableDataChanged();
			if (day > this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
				day = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				this.calendar.set(Calendar.DAY_OF_MONTH, day);
			} else if (day < this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH)) {
				day = this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
				this.calendar.set(Calendar.DAY_OF_MONTH, day);
			} else {
				this.calendar.set(Calendar.DAY_OF_MONTH, day);
			}
			this.fireCalendarEvent(day, this.getMonth(), this.getYear(), this.locale);
		}

		@Override
		public Object getValueAt(final int r, final int c) {
			// We have to calculate the number of rows to show the days of the
			// current year and month
			final int originalDay = this.calendar.get(Calendar.DAY_OF_MONTH);
			final int maxMonthDay = this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
			final int minMonthDay = this.calendar.getActualMinimum(Calendar.DAY_OF_MONTH);

			this.calendar.set(Calendar.DAY_OF_MONTH, minMonthDay);
			// Fist is Sunday
			final int weekDayFirstMonthDay = this.calendar.get(Calendar.DAY_OF_WEEK);
			final int firstWeekDay = this.calendar.getFirstDayOfWeek();

			this.calendar.set(Calendar.DAY_OF_MONTH, originalDay);

			// This is the column to start in
			// The number of necessary cells to insert all days is
			// weekDayFirstMonthDay+1+maxMonthDay-minMonthDay+1
			// In the row number 0, and column weekDayFirstMonthDay will be the
			// first day

			// Some problems appear
			// Other possibility. Look for the day of the week (column)
			// for the minimum day of the month (1st)
			int columnToDayOne = weekDayFirstMonthDay - firstWeekDay;
			// If it is less than 0 subtract to the column number
			if (columnToDayOne < 0) {
				columnToDayOne = this.getColumnCount() + columnToDayOne;
			}

			final int offset = ((r * this.getColumnCount()) + c) - columnToDayOne;

			// Sum to the first day of the month and get the result
			if (offset < 0) {
				final int prevMonth = (this.getMonth() + 11) % 12;
				final int prevYear = this.getMonth() == 0 ? this.getYear() - 1 : this.getYear();
				final Calendar auxCalendar = Calendar.getInstance(this.locale);
				auxCalendar.setLenient(false);
				auxCalendar.set(Calendar.MILLISECOND, 0);
				auxCalendar.set(Calendar.SECOND, 0);
				auxCalendar.set(Calendar.MINUTE, 0);
				auxCalendar.set(Calendar.HOUR, 0);
				auxCalendar.set(prevYear, prevMonth, 1);
				final int maxPrevMonthDay = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				// return new Integer(maxPrevMonthDay + offset + 1);
				auxCalendar.add(Calendar.DAY_OF_MONTH, maxPrevMonthDay + offset);
				return auxCalendar.getTime();
			} else if (offset >= maxMonthDay) {
				final int nextMonth = (this.getMonth() + 1) % 12;
				final int nextYear = this.getMonth() == 11 ? this.getYear() + 1 : this.getYear();
				final Calendar auxCalendar = Calendar.getInstance(this.locale);
				auxCalendar.setLenient(false);
				auxCalendar.set(Calendar.MILLISECOND, 0);
				auxCalendar.set(Calendar.SECOND, 0);
				auxCalendar.set(Calendar.MINUTE, 0);
				auxCalendar.set(Calendar.HOUR, 0);
				auxCalendar.set(nextYear, nextMonth, 1);
				final int maxNextMonthDay = auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
				auxCalendar.add(Calendar.DAY_OF_MONTH, offset - maxMonthDay);
				return auxCalendar.getTime();
			}
			final int day = minMonthDay + offset;
			return new Integer(day);
		}

	}

	public Locale getCalendarLocale() {
		return this.calendarLocale;
	}

	public String[] getLocaleShortWeekDays(final Locale l) {
		final String[] shortWeekdays = new String[VisualCalendarComponent.defaultShortWeekday.length];
		shortWeekdays[0] = "";
		for (int i = 1; i < VisualCalendarComponent.defaultShortWeekday.length; i++) {
			shortWeekdays[i] = ApplicationManager.getTranslation(VisualCalendarComponent.defaultShortWeekday[i],
					ApplicationManager.getApplicationBundle());
		}
		return shortWeekdays;
	}

	public VisualCalendarComponent(final Locale l) {
		this(l, false);
	}

	public VisualCalendarComponent(final Locale l, final boolean trunc) {

		if (ApplicationManager.useOntimizePlaf) {
			this.setBackground(VisualCalendarComponent.defaultBackgroundColor);
		}

		// Create the calendar
		// Initialize graphic interface
		this.truncate = trunc;
		this.daysTable = this.createDayTable();
		this.calendarLocale = l;
		this.calendar = Calendar.getInstance(l);
		this.setLocale(l);
		final DayTableModel model = this.createDayTableModel(l);

		model.addCalendarListener(new CalendarListener() {

			@Override
			public void dateChanged(final CalendarEvent e) {
				if (VisualCalendarComponent.this.fireCalendarEvents) {
					VisualCalendarComponent.this.fireCalendarEvent(e.getDay(), e.getMonth(), e.getYear(),
							e.getLocale());
				}
			}
		});

		this.daysTable.setModel(model);

		this.tfYear = this.createYearTextField();

		this.comboMonth = this.createComboMonth(l);

		this.createAndConfigureButtons();

		this.buildGUI();
		final Calendar c = Calendar.getInstance(l);
		c.setLenient(false);
		c.set(Calendar.MILLISECOND, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.HOUR, 0);
		this.setYear(c.get(Calendar.YEAR));
		this.setMonth(c.get(Calendar.MONTH));
		this.setDay(c.get(Calendar.DAY_OF_MONTH));
	}

	public void setJMenu(final JMenu menu) {
		this.menu = menu;
	}

	public JMenu getJMenu() {
		return this.menu;
	}

	public JTable getDaysTable() {
		return this.daysTable;
	}

	public void addMenuMouseListener(final MouseListener l) {
		if (this.daysTable != null) {
			this.daysTable.addMouseListener(l);
		}
	}

	public void removeMenuMouseListener(final MouseListener l) {
		if (this.daysTable != null) {
			this.daysTable.removeMouseListener(l);
		}
	}

	private void buildGUI() {
		this.setLayout(new GridBagLayout());

		this.add(this.prevYearBt, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.tfYear, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.nextYearButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.add(this.prevMonthBt, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.comboMonth, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.nextMonthButton, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		// Size for buttons and icons
		this.prevYearBt.setMargin(new Insets(0, 2, 0, 2));
		this.nextYearButton.setMargin(new Insets(0, 2, 0, 2));
		this.prevMonthBt.setMargin(new Insets(0, 2, 0, 2));
		this.nextMonthButton.setMargin(new Insets(0, 2, 0, 2));

		this.prevYearBt.setFocusPainted(false);
		this.nextYearButton.setFocusPainted(false);
		this.prevMonthBt.setFocusPainted(false);
		this.nextMonthButton.setFocusPainted(false);

		this.prevYearBt.setFocusable(false);
		this.nextYearButton.setFocusable(false);
		this.prevMonthBt.setFocusable(false);
		this.nextMonthButton.setFocusable(false);
		this.comboMonth.setFocusable(false);
		this.tfYear.setFocusable(false);

		final ImageIcon prevIcon = ImageManager.getIcon(ImageManager.CALENDAR_PREV);
		if (prevIcon != null) {
			this.prevYearBt.setIcon(prevIcon);
			this.prevMonthBt.setIcon(prevIcon);
		} else {
			this.prevYearBt.setText("<<");
			this.prevMonthBt.setText("<<");
		}

		final ImageIcon nextIcon = ImageManager.getIcon(ImageManager.CALENDAR_NEXT);
		if (nextIcon != null) {
			this.nextYearButton.setIcon(nextIcon);
			this.nextMonthButton.setIcon(nextIcon);
		} else {
			this.nextYearButton.setText(">>");
			this.nextMonthButton.setText(">>");
		}

		// Fonts
		this.daysTable.setFont(this.daysTable.getFont().deriveFont((float) 10));
		this.prevYearBt.setFont(this.prevYearBt.getFont().deriveFont((float) 10));
		this.prevMonthBt.setFont(this.prevMonthBt.getFont().deriveFont((float) 10));
		this.nextYearButton.setFont(this.nextYearButton.getFont().deriveFont((float) 10));
		this.nextMonthButton.setFont(this.nextMonthButton.getFont().deriveFont((float) 10));
		this.tfYear.setFont(this.tfYear.getFont().deriveFont((float) 10));
		this.comboMonth.setFont(this.comboMonth.getFont().deriveFont((float) 10));

		if (VisualCalendarComponent.fieldsBorder != null) {
			this.tfYear.setBorder(VisualCalendarComponent.fieldsBorder);
			this.comboMonth.setBorder(VisualCalendarComponent.fieldsBorder);
		}

		VisualCalendarComponent.setDaysTablePreferenceSize(this.daysTable);

		this.daysTable.setIntercellSpacing(new Dimension(0, 0));
		this.daysTable.setShowGrid(false);

		this.scrollTable = new JScrollPane(this.daysTable);

		this.scrollTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		this.scrollTable.setBorder(new LineBorder(VisualCalendarComponent.GENERAL_BORDER_COLOR));

		this.add(this.scrollTable, new GridBagConstraints(0, 1, 6, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		this.setSize(this.getPreferredSize());

	}

	protected static void setDaysTablePreferenceSize(final JTable table) {
		final FontMetrics fontMetrics = table.getFontMetrics(table.getFont());
		final int width = fontMetrics.stringWidth("MMM");
		for (int i = 0; i < table.getColumnCount(); i++) {
			final TableColumn tc = table.getColumnModel().getColumn(i);
			tc.setMinWidth(width - 2);
			tc.setMaxWidth(width + 10);
			tc.setPreferredWidth(width);
			tc.setWidth(width);
		}
	}

	public void setShowGrid(final boolean show) {
		this.daysTable.setShowGrid(show);
	}

	protected void createAndConfigureButtons() {
		this.nextMonthButton = new DataField.FieldButton();
		this.nextMonthButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				int sel = VisualCalendarComponent.this.comboMonth.getSelectedIndex();
				if (sel < (VisualCalendarComponent.this.comboMonth.getItemCount() - 1)) {
					sel++;
					VisualCalendarComponent.this.comboMonth.setSelectedIndex(sel);
					VisualCalendarComponent.this.setMonthIntern_(sel);
				} else if (sel == (VisualCalendarComponent.this.comboMonth.getItemCount() - 1)) {
					VisualCalendarComponent.this.nextYearButton.doClick(10);
					sel = 0;
					VisualCalendarComponent.this.comboMonth.setSelectedIndex(sel);
					VisualCalendarComponent.this.setMonthIntern_(sel);
				}
			}
		});
		this.prevMonthBt = new DataField.FieldButton();
		this.prevMonthBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				int sel = VisualCalendarComponent.this.comboMonth.getSelectedIndex();
				if (sel > 0) {
					sel--;
					VisualCalendarComponent.this.comboMonth.setSelectedIndex(sel);
					VisualCalendarComponent.this.setMonthIntern_(sel);
				} else if (sel == 0) {
					VisualCalendarComponent.this.prevYearBt.doClick(10);
					sel = VisualCalendarComponent.this.comboMonth.getModel().getSize() - 1;
					VisualCalendarComponent.this.comboMonth.setSelectedIndex(sel);
					VisualCalendarComponent.this.setMonthIntern_(sel);
				}
			}
		});

		this.nextYearButton = new DataField.FieldButton();
		this.nextYearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String text = VisualCalendarComponent.this.tfYear.getText();
				if ((text != null) && (text.length() > 0)) {
					try {
						int year = Integer.parseInt(text);
						if (year == 0) {
							return;
						}
						year++;
						final String text2 = Integer.toString(year);
						VisualCalendarComponent.this.tfYear.setText(text2);
					} catch (final Exception ex) {
						VisualCalendarComponent.logger.trace(null, ex);
					}
				}
			}
		});
		this.prevYearBt = new DataField.FieldButton();
		this.prevYearBt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String text = VisualCalendarComponent.this.tfYear.getText();
				if ((text != null) && (text.length() > 0)) {
					try {
						int year = Integer.parseInt(text);
						year--;
						final String text2 = Integer.toString(year);
						VisualCalendarComponent.this.tfYear.setText(text2);
					} catch (final Exception ex) {
						VisualCalendarComponent.logger.trace(null, ex);
					}
				}
			}
		});

	}

	protected JComboBox createComboMonth(final Locale l) {
		final ComboBoxModel model = this.createComboModel(l);
		final JComboBox c = new JComboBox(model);
		c.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final JComboBox source = (JComboBox) e.getSource();
				VisualCalendarComponent.this.setMonthIntern_(source.getSelectedIndex());
			}
		});
		return c;
	}

	protected ComboBoxModel createComboModel(final Locale l) {
		final DateFormatSymbols df = new DateFormatSymbols(l);
		final String[] months = df.getMonths();
		final Vector v = new Vector();
		for (int i = 0; i < months.length; i++) {
			if ((months[i] != null) && !months[i].equals("")) {
				v.add(months[i]);
			}
		}
		final DefaultComboBoxModel model = new DefaultComboBoxModel(v);
		return model;
	}

	protected JTable createDayTable() {
		final JTable t = new JTable() {

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return this.getPreferredSize();
			}

			@Override
			public void setEnabled(final boolean enabled) {
				super.setEnabled(enabled);
				if (!enabled) {
					this.getSelectionModel().clearSelection();
				}
			}

			@Override
			protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
				if (ks.getKeyCode() == KeyEvent.VK_ENTER) {
					final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
					if (w instanceof EJDialog) {
						w.setVisible(false);
						return true;
					}
				}
				return super.processKeyBinding(ks, e, condition, pressed);
			}
		};

		t.setTableHeader(new JTableHeader(t.getColumnModel()) {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.height = Math.max(15, d.height);
				return d;
			}
		});

		t.getTableHeader().setReorderingAllowed(false);
		t.getTableHeader().setResizingAllowed(false);
		t.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		final DayRenderer dayRenderer = new DayRenderer(this);
		t.setDefaultRenderer(Object.class, dayRenderer);
		t.setDefaultRenderer(Integer.class, dayRenderer);

		t.setColumnSelectionAllowed(false);
		t.setRowSelectionAllowed(false);
		t.setCellSelectionEnabled(true);
		t.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		t.getColumnModel().getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		if (ApplicationManager.useOntimizePlaf) {
			t.setShowGrid(false);
		}
		return t;
	}

	protected DayTableModel createDayTableModel(final Locale l) {
		return new DayTableModel(l, this.daysTable, this.truncate);
	}

	protected JTextField createYearTextField() {
		final JTextField tf = new JTextField(4);
		final YearDocument doc = new YearDocument();
		tf.setDocument(doc);
		doc.addDocumentListener(this.createYearDocumentListener());
		return tf;
	}

	protected DocumentListener createYearDocumentListener() {
		return new DocumentListener() {

			@Override
			public void insertUpdate(final DocumentEvent e) {
				// Check if it is an integer value
				try {
					if (e.getDocument().getLength() > 0) {
						final int year = Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength()));
						VisualCalendarComponent.this.setYearInner_(year);
					}
				} catch (final Exception ex) {
					VisualCalendarComponent.logger.trace(null, ex);
				}
			}

			@Override
			public void removeUpdate(final DocumentEvent e) {
				// Check if it is an integer value
				try {
					if (e.getDocument().getLength() > 0) {
						final int year = Integer.parseInt(e.getDocument().getText(0, e.getDocument().getLength()));
						VisualCalendarComponent.this.setYearInner_(year);
					}
				} catch (final Exception ex) {
					VisualCalendarComponent.logger.trace(null, ex);
				}
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {

			}
		};
	}

	public void setYear(final int year) {
		// Select the year
		if (year < 0) {
			throw new IllegalArgumentException("Year must be greater than 0");
		}

		this.tfYear.setText(Integer.toString(year));
		this.setYearInner_(year);

	}

	protected void setYearInner_(final int year) {
		final int day = ((DayTableModel) this.daysTable.getModel()).getDay();
		((DayTableModel) this.daysTable.getModel()).setYear(year);
	}

	public void setMonth(final int month) {
		// Select the year
		final Calendar calendar = ((DayTableModel) this.daysTable.getModel()).calendar;
		final int minimumMonth = calendar.getActualMinimum(Calendar.MONTH);
		final int maximumMonth = calendar.getActualMaximum(Calendar.MONTH);

		if (month < minimumMonth) {
			throw new IllegalArgumentException("month in this year must be greater than " + minimumMonth);
		}
		if (month > maximumMonth) {
			throw new IllegalArgumentException("month in this year must be lower than " + maximumMonth);
		}

		this.comboMonth.setSelectedIndex(month);
		this.setMonthIntern_(month);

	}

	protected void setMonthIntern_(final int month) {
		((DayTableModel) this.daysTable.getModel()).setMonth(month);
	}

	public void setDay(final int day) {
		int row = 0;
		int column = 0;
		for (int i = 0; i < this.daysTable.getRowCount(); i++) {
			for (int j = 0; j < this.daysTable.getColumnCount(); j++) {
				final Object currentValue = this.daysTable.getValueAt(i, j);
				if (currentValue instanceof Integer) {
					final Integer d = (Integer) currentValue;
					if ((d != null) && (d.intValue() == day)) {
						row = i;
						column = j;
						if (!this.moveBack) {
							this.fireSelectionEvent = true;
							this.daysTable.setRowSelectionInterval(row, row);
							this.daysTable.setColumnSelectionInterval(column, column);
							this.fireSelectionEvent = false;
							return;
						}
					}
				}
			}
		}
		this.fireSelectionEvent = true;
		this.daysTable.setRowSelectionInterval(row, row);
		this.daysTable.setColumnSelectionInterval(column, column);
		this.fireSelectionEvent = false;
		return;
	}

	public int getYear() {
		return ((DayTableModel) this.daysTable.getModel()).getYear();
	}

	public int getMonth() {
		return ((DayTableModel) this.daysTable.getModel()).getMonth();
	}

	public int getDay() {
		return ((DayTableModel) this.daysTable.getModel()).getDay();
	}

	public Date getCurrentDate() {
		return ((DayTableModel) this.daysTable.getModel()).getCurrentDate();
	}

	public Timestamp getCurrentTimestamp() {
		return ((DayTableModel) this.daysTable.getModel()).getCurrentTimestamp();
	}

	public static JDialog getCalendarDialog(final Frame parent, final Locale l) {
		final JDialog d = new JDialog(parent);
		final VisualCalendarComponent c = new VisualCalendarComponent(l);
		c.addCalendarListener(new CalendarListener() {

			@Override
			public void dateChanged(final CalendarEvent e) {
				VisualCalendarComponent.logger.debug(e.getDay() + "/" + e.getMonth() + "/" + e.getYear());
			}
		});
		d.getContentPane().add(c);
		d.pack();
		return d;
	}

	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final JDialog d = VisualCalendarComponent.getCalendarDialog(null, Locale.getDefault());

		d.setVisible(true);
	}

	protected void fireCalendarEvent(final int d, final int m, final int y, final Locale l) {
		if (this.listeners == null) {
			return;
		}
		final CalendarEvent e = new CalendarEvent(this, d, m, y, l);
		for (int i = 0; i < this.listeners.size(); i++) {
			((CalendarListener) this.listeners.get(i)).dateChanged(e);
		}
	}

	public void addCalendarListener(final CalendarListener c) {
		if (this.listeners == null) {
			this.listeners = new Vector();
		}
		this.listeners.add(c);
	}

	public void removeCalendarListener(final CalendarListener c) {
		if (this.listeners != null) {
			this.listeners.remove(c);
		}
	}

	public void setCalendarLocale(final Locale l) {
		super.setLocale(l);
		this.calendarLocale = l;
		this.reloadAll(l);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.tfYear.setEnabled(enabled);
		this.comboMonth.setEnabled(enabled);
		this.nextYearButton.setEnabled(enabled);
		this.prevYearBt.setEnabled(enabled);
		this.nextMonthButton.setEnabled(enabled);
		this.prevMonthBt.setEnabled(enabled);
		this.daysTable.setEnabled(enabled);
	}

	public synchronized void setDate(final Date d) {
		this.fireCalendarEvents = false;
		try {
			// Month and year
			this.calendar.setTime(d);
			final int month = this.calendar.get(Calendar.MONTH);
			final int year = this.calendar.get(Calendar.YEAR);
			this.tfYear.setText(Integer.toString(year));
			this.comboMonth.setSelectedIndex(month);
			((DayTableModel) this.daysTable.getModel()).setDate(d);
			this.fireCalendarEvent(this.getDay(), this.getMonth(), this.getYear(), this.getCalendarLocale());
		} catch (final Exception e) {
			VisualCalendarComponent.logger.error(null, e);
		} finally {
			this.fireCalendarEvents = true;
		}
	}

	protected void reloadAll(final Locale l) {
		this.fireCalendarEvents = false;
		try {
			this.calendar = Calendar.getInstance(l);
			final int month = this.getMonth();
			// We have to change the table model and combo names
			final ComboBoxModel m = this.createComboModel(l);
			this.comboMonth.setModel(m);
			this.comboMonth.setSelectedIndex(month);
			((DayTableModel) this.daysTable.getModel()).setLocale(l);
			if (this.daysTable.getDefaultRenderer(Object.class) instanceof DayRenderer) {
				((DayRenderer) this.daysTable.getDefaultRenderer(Object.class)).updateLocale();
			}
		} finally {
			this.fireCalendarEvents = true;
		}

	}

	public void addMouseListenerToDaysTable(final MouseListener m) {
		if (this.daysTable != null) {
			this.daysTable.addMouseListener(m);
		}
	}

	public void removeMouseListenerFromDaysTable(final MouseListener m) {
		if (this.daysTable != null) {
			this.daysTable.removeMouseListener(m);
		}
	}

}
