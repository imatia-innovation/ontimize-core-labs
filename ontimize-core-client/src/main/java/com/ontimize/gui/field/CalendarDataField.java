package com.ontimize.gui.field;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.calendar.VisualCalendarComponent;
import com.ontimize.gui.calendar.event.CalendarEvent;
import com.ontimize.gui.calendar.event.CalendarListener;

/**
 * This class implements a field with a calendar component. This calendar will be sensible to
 * listeners and value changes.
 * <p>
 *
 * @author Imatia Innovation
 */
public class CalendarDataField extends IdentifiedAbstractFormComponent
implements ValueChangeDataComponent, CalendarListener {

	private static final Logger logger = LoggerFactory.getLogger(CalendarDataField.class);

	private List valueListener = null;

	/**
	 * The condition of value events activation. By default, true.
	 */
	protected boolean fireValueEvents = true;

	protected VisualCalendarComponent calendarComp = null;

	/**
	 * The reference to calendar. By default, null.
	 */
	protected Calendar calendar = null;

	/**
	 * The possibility to modify the field. By default, true.
	 */
	protected boolean modifiable = true;

	/**
	 * The required condition to field. By default, false.
	 */
	protected boolean required = false;

	/**
	 * The stored value when <code>setValue</code> is called. By default, null.
	 */
	protected Object storeValue = null;

	/**
	 * The inner stored value reference. By default, null.
	 */
	protected Object innerStoredValue = null;

	/**
	 * The class constructor. Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public CalendarDataField(final Map parameters) {
		this.init(parameters);
	}

	/**
	 * Inits parameters and adds the calendar component to container.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
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
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		if (parameters.containsKey(DataField.ATTR)) {
			this.attribute = parameters.get(DataField.ATTR);
		} else {
			throw new IllegalArgumentException("Parameter 'attr' is mandatory");
		}

		this.calendar = new GregorianCalendar();
		this.calendarComp = new VisualCalendarComponent();
		this.calendarComp.addCalendarListener(this);

		final CompoundBorder cBorder = new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(2, 2, 2, 2));
		this.calendarComp.setBorder(cBorder);
		super.setLayout(new GridBagLayout());
		super.add(this.calendarComp, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
	}

	/**
	 * Sets a menu for calendar component.
	 * <p>
	 *
	 * @see VisualCalendarComponent#setJMenu(JMenu)
	 * @param menu the reference to menu
	 */
	public void setJMenu(final JMenu menu) {
		if (this.calendarComp != null) {
			this.calendarComp.setJMenu(menu);
		}
	}

	/**
	 * Gets a menu for calendar component.
	 * <p>
	 *
	 * @see VisualCalendarComponent#getJMenu()
	 * @return the reference to menu
	 */
	public JMenu getJMenu() {
		if (this.calendarComp != null) {
			return this.calendarComp.getJMenu();
		}
		return null;
	}

	/**
	 * Adds a mouse listener for menu.
	 * <p>
	 * @param l the mouse listener reference to add
	 */
	public void addMenuMouseListener(final MouseListener l) {
		if (this.calendarComp != null) {
			this.calendarComp.addMenuMouseListener(l);
		}
	}

	/**
	 * Removes the menu mouse listener.
	 * <p>
	 * @param l the mouse listener reference to remove
	 */
	public void removeMenuMouseListener(final MouseListener l) {
		if (this.calendarComp != null) {
			this.calendarComp.removeMenuMouseListener(l);
		}
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	public VisualCalendarComponent getCalendarComponent() {
		return this.calendarComp;
	}

	@Override
	public Object getValue() {
		return this.innerStoredValue;
	}

	@Override
	public void setValue(final Object value) {
		this.fireValueEvents = false;
		final Object oPreviousValue = this.storeValue;
		if (value != null) {
			if (value instanceof Date) {
				this.calendar.setTime((Date) value);
				this.calendarComp.setDate(this.calendar.getTime());
			}
		}
		this.storeValue = value;
		this.innerStoredValue = value;
		this.fireValueEvents = true;
		this.fireValueChanged(this.storeValue, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	public void deleteData() {
		this.fireValueEvents = false;
		final Object oPreviousValue = this.storeValue;
		this.storeValue = null;
		this.innerStoredValue = null;
		this.fireValueEvents = true;
		this.fireValueChanged(this.storeValue, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	public boolean isEmpty() {
		if (this.storeValue != null) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.TIMESTAMP;
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public boolean isModified() {
		if (this.storeValue == null) {
			return false;
		}
		if (this.storeValue instanceof Date) {
			final long g = ((Date) this.storeValue).getTime();
			if (g == this.calendar.getTimeInMillis()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.calendarComp.setEnabled(enabled);
	}

	@Override
	public void addValueChangeListener(final ValueChangeListener l) {
		if (this.valueListener == null) {
			this.valueListener = new ArrayList();
		}
		if (!this.valueListener.contains(l)) {
			this.valueListener.add(l);
		}

	}

	@Override
	public void removeValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && this.valueListener.contains(l)) {
			this.valueListener.remove(l);
		}
	}

	/**
	 * Fires the value changed events for value listeners.
	 * <p>
	 * @param newValue the new value
	 * @param oldValue the previous value
	 * @param type the type of event
	 */
	protected void fireValueChanged(final Object newValue, final Object oldValue, final int type) {
		if (!this.fireValueEvents) {
			return;
		}
		if (this.valueListener == null) {
			return;
		}
		for (int i = this.valueListener.size() - 1; i >= 0; i--) {
			((ValueChangeListener) this.valueListener.get(i))
			.valueChanged(new ValueEvent(this, newValue, oldValue, type));
		}
	}

	@Override
	public void dateChanged(final CalendarEvent e) {
		final Object oldValue = this.innerStoredValue;
		this.calendar.set(Calendar.DAY_OF_MONTH, e.getDay());
		this.calendar.set(Calendar.MONTH, e.getMonth());
		this.calendar.set(Calendar.YEAR, e.getYear());
		this.innerStoredValue = this.calendar.getTime();
		this.fireValueChanged(this.innerStoredValue, oldValue, ValueEvent.USER_CHANGE);
		if (ApplicationManager.DEBUG) {
			CalendarDataField.logger.debug("Use change day: " + e.getDay() + "/" + e.getMonth() + "/" + e.getYear());
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {

		this.setLocale(l);
		this.fireValueEvents = false;
		try {
			this.calendar = new GregorianCalendar(l);
			if (this.calendarComp != null) {
				this.calendarComp.setCalendarLocale(l);
			}
		} catch (final Exception ex) {
			CalendarDataField.logger.error(null, ex);
		}
		this.fireValueEvents = true;
	}

}
