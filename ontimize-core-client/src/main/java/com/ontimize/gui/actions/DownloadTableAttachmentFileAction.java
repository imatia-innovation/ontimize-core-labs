package com.ontimize.gui.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class DownloadTableAttachmentFileAction extends DownloadAttachmentFileAction {

	protected Table table;

	public DownloadTableAttachmentFileAction(final String entity, final boolean openFile, final String sName, final boolean bWait,
			final String uriSound, final boolean askOpen, final boolean temporal, final Table table) {
		super(entity, openFile, sName, bWait, uriSound, askOpen, temporal);
		this.table = table;
	}

	@Override
	protected String getProposedFileName(final ActionEvent e) {
		final Form f = this.getForm(e);
		if ((this.fileFieldName != null) && (this.table != null) && (this.table.getSelectedRowsNumber() > 0)) {
			final EntityResult erSelected = new EntityResultMapImpl(new HashMap(this.table.getSelectedRowData()));
			final Map recordValues = erSelected.getRecordValues(0);
			return recordValues.get(this.fileFieldName).toString().trim();
		} else {
			return null;
		}
	}

	@Override
	protected Map getAttachmentValuesKeys(final Form f) throws Exception {
		// TODO Auto-generated method stub
		final Map keys = super.getAttachmentValuesKeys(f);
		if ((this.table != null) && (this.table.getSelectedRowsNumber() > 0)) {

			final EntityResult erSelected = new EntityResultMapImpl(new HashMap(this.table.getSelectedRowData()));
			for (int i = 0; i < erSelected.calculateRecordNumber(); i++) {
				final Map currentDoc = erSelected.getRecordValues(i);

				for (final Object key : this.table.getKeys()) {
					if (currentDoc.containsKey(key)) {
						keys.put(key, currentDoc.get(key));
					}
				}
			}
		}
		return keys;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		super.actionPerformed(e);
	}

}
