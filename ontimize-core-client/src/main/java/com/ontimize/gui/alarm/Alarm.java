package com.ontimize.gui.alarm;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

/**
 * @version 1.0
 */
public class Alarm implements Comparable, Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(Alarm.class);

	public static int INFORMATION = 0;

	public static int WARNING = 1;

	protected int type = Alarm.INFORMATION;

	protected String text = "";

	protected String code = "";

	protected Object key = null;

	protected Date hour = null;

	protected boolean recognized = false;

	protected Locale locale = Locale.getDefault();

	protected DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

	protected ResourceBundle resources = null;

	public Alarm(final Object key, final String code, final int type) {
		this(key, code, type, new Date(), code, false);
	}

	public Alarm(final Object key, final String text, final int type, final String code) {
		this(key, text, type, new Date(), code, false);
	}

	public Alarm(final Object key, final String code, final int type, final Date hour) {
		this(key, code, type, hour, code, false);
	}

	public Alarm(final Object key, final String text, final int type, final Date hour, final String code) {
		this(key, text, type, hour, code, false);
	}

	public Alarm(final Object key, final String code, final int type, final boolean recognized) {
		this(key, code, type, new Date(), code, recognized);
	}

	public Alarm(final Object key, final String text, final int tipo, final String code, final boolean recognized) {
		this(key, text, tipo, new Date(), code, recognized);
	}

	public Alarm(final Object key, final String code, final int type, final Date hour, final boolean recognized) {
		this(key, code, type, hour, code, recognized);
	}

	public Alarm(final Object key, final String text, final int type, final Date hour, final String code, final boolean recognized) {
		this.key = key;
		this.text = text;
		this.code = code;
		this.type = type;
		this.hour = hour;
		this.setRecognize(recognized);
	}

	public Object getKey() {
		return this.key;
	}

	public int getType() {
		return this.type;
	}

	public Date getHour() {
		return this.hour;
	}

	/**
	 * Gets the alarm hour with the format specified by the object locale
	 * @return
	 */
	public String getHourAsString() {
		return this.df.format(this.hour);
	}

	public String getText() {
		return this.text;
	}

	public String getCode() {
		return this.code;
	}

	/**
	 * Creates a string with format 'code : text'. The text can be in the resources file
	 */
	@Override
	public String toString() {
		if (this.resources != null) {
			try {
				return this.code + " : " + this.resources.getString(this.text);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					Alarm.logger.debug(this.getClass().toString() + " : " + e.getMessage(), e);
				}
				return this.code + " : " + this.text;
			}
		}
		return this.code + " : " + this.text;
	}

	public boolean isRecognized() {
		return this.recognized;
	}

	public boolean isWarning() {
		return this.type == 0 ? false : true;
	}

	public void setRecognize(final boolean rec) {
		this.recognized = rec;
	}

	@Override
	public int compareTo(final Object o) {
		if (o instanceof Alarm) {
			return this.hour.compareTo(((Alarm) o).getHour());
		} else {
			return 0;
		}
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resources = res;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		this.df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, l);
	}

}
