package com.ontimize.util.swing.popuplist;

import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class PopupListModel extends AbstractListModel {

	EntityResult record = null;

	Object key = null;

	public PopupListModel() {
		this(null);
	}

	public PopupListModel(final Object key) {
		this.key = key;
	}

	@Override
	public int getSize() {
		if (this.record == null) {
			return 0;
		} else {
			return this.record.calculateRecordNumber();
		}
	}

	@Override
	public Object getElementAt(final int index) {
		final Map h = this.record.getRecordValues(index);
		// return h.get(key);
		return h;
	}

	public void setKey(final Object key) {
		this.key = key;
	}

	public void setDataModel(final EntityResult res) {
		final int end = this.getSize();
		this.record = res;
		if (this.record == null) {
			this.record = new EntityResultMapImpl();
		}
		this.fireContentsChanged(this, 0, end - 1);
	}

	public Map getRegistry(final Object o) {
		if (this.record.containsKey(this.key)) {
			final List v = (List) this.record.get(this.key);
			final int index = v.indexOf(o);
			if (index >= 0) {
				return this.record.getRecordValues(index);
			}
		}
		return null;
	}

}
