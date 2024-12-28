package com.ontimize.gui;

import java.util.EventObject;
import java.util.Map;

public class DataNavigationEvent extends EventObject {

	public static final int BUTTON_NAVIGATION = 0;

	public static final int PROGRAMMATIC_NAVIGATION = 1;

	protected Form form = null;

	protected Map data = null;

	protected int type = 0;

	protected int index = -1;

	protected int oldIndex = -1;

	public DataNavigationEvent(final Form source, final Map data, int type, final int index, final int oldIndex) {
		super(source);
		this.form = source;
		this.data = data;
		if ((type != DataNavigationEvent.BUTTON_NAVIGATION) && (type != DataNavigationEvent.PROGRAMMATIC_NAVIGATION)) {
			type = DataNavigationEvent.BUTTON_NAVIGATION;
		} else {
			this.type = type;
		}
		this.index = index;
		this.oldIndex = oldIndex;
	}

	public Form getForm() {
		return this.form;
	}

	public Map getData() {
		return this.data;
	}

	public int getType() {
		return this.type;
	}

	public int getIndex() {
		return this.index;
	}

	public int getOldIndex() {
		return this.oldIndex;
	}

	@Override
	public String toString() {
		return "DataChangedEvent: " + this.form.getArchiveName() + ", type : " + this.type + ", new index : "
				+ this.index + ", old index: " + this.oldIndex;
	}

}
