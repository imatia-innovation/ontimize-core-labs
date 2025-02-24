package com.ontimize.gui;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;

public class MultipleReferenceDataFieldValue implements Serializable {

	private final Map data = new Hashtable();

	private Map cache = null;

	private long time = 0;

	public MultipleReferenceDataFieldValue(final Map data) {
		if (data != null) {
			this.data.putAll(data);
		}
	}

	public void setDataCache(final Map c, final long time) {
		this.cache = c;
		this.time = time;
	}

	public Map getCache() {
		return this.cache;
	}

	public long getCachedTime() {
		return this.time;
	}

}
