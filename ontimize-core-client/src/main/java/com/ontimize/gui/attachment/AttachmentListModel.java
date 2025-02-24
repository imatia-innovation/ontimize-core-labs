package com.ontimize.gui.attachment;

import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

/**
 * Model used in the attachment list
 */
public class AttachmentListModel extends AbstractListModel {

	/**
	 * {@link EntityResult} containing attachment information
	 */
	private EntityResult record = null;

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
		return h.get(Form.ATTACHMENT_ID);
	}


	public void setAttachment(final EntityResult res) {
		final int end = this.getSize();
		this.record = res;
		if (this.record == null) {
			this.record = new EntityResultMapImpl();
		}
		this.fireContentsChanged(this, 0, end - 1);
	}


	public Map getRecord(final Object o) {
		if (this.record.containsKey(Form.ATTACHMENT_ID)) {
			final List v = (List) this.record.get(Form.ATTACHMENT_ID);
			final int index = v.indexOf(o);
			if (index >= 0) {
				return this.record.getRecordValues(index);
			}
		}
		return null;
	}

	public Map getRecord(final int i) {
		if (i >= 0) {
			return this.record.getRecordValues(i);
		} else {
			return null;
		}
	}

	public int getRecordIndex(final Map kv) {
		return this.record.getRecordIndex(kv);
	}

	public void updateRecord(final int i, final Map av) {
		EntityResultUtils.updateRecordValues(this.record, av, i);
	}

	public void deleteRecord(final int i) {
		this.record.deleteRecord(i);
	}

	public boolean isEmpty() {
		if (this.getSize() == 0) {
			return true;
		}
		return false;
	}

}
