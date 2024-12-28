package com.ontimize.cache;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateFormatCache {

	private DateFormatCache() {

	}

	protected static DateFormatCache staticCache = new DateFormatCache();

	protected Map						cache		= new HashMap();

	public void put(final Locale l, final DateFormat df) {
		this.cache.put(l, df);
	}

	public boolean exists(final Locale l) {
		return this.cache.containsKey(l);
	}

	public DateFormat get(final Locale l) {
		return (DateFormat) this.cache.get(l);
	}

	public static void addDateFormat(final Locale l, final DateFormat df) {
		DateFormatCache.staticCache.put(l, df);
	}

	public static boolean containsDateFormat(final Locale l) {
		if (l == null) {
			return false;
		}
		return DateFormatCache.staticCache.exists(l);
	}

	public static DateFormat getDateFormat(final Locale l) {
		return DateFormatCache.staticCache.get(l);
	}

}
