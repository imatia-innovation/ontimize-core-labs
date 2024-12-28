package com.ontimize.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.FileManagementEntity;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class DeleteAttachmentFileAction extends AbstractButtonAction {

	private static final Logger logger = LoggerFactory.getLogger(DeleteAttachmentFileAction.class);

	protected String entityName = null;

	protected boolean refresh = false;

	public DeleteAttachmentFileAction(final String entity, final boolean refresh) {
		this.entityName = entity;
		this.refresh = refresh;
	}

	public void setEntity(final String entity) {
		this.entityName = entity;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Form f = this.getForm(e);
		final EntityReferenceLocator referenceLocator = f.getFormManager().getReferenceLocator();
		try {
			final Entity entity = referenceLocator.getEntityReference(this.entityName);
			if (entity instanceof FileManagementEntity) {
				final FileManagementEntity eGA = (FileManagementEntity) entity;
				final Map kv = this.getAttachmentValuesKeys(f);
				final boolean delete = eGA.deleteAttachmentFile(kv, referenceLocator.getSessionId());
				if (this.refresh) {
					f.refreshCurrentDataRecord();
				}

				if (delete) {
					f.message("The attach file has been deleted successfully", JOptionPane.INFORMATION_MESSAGE);
				} else {
					f.message("Error when the attach file was being deleted", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (final Exception ex) {
			DeleteAttachmentFileAction.logger.error(null, ex);
			f.message("Error when the attach file was being deleted: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					ex);
		}
	}

	protected Map getAttachmentValuesKeys(final Form f) throws Exception {
		final Map kv = new Hashtable();
		final List vKeys = f.getKeys();
		if (vKeys.isEmpty()) {
			throw new Exception("The 'keys' parameter is necessary  in the parent form");
		}
		for (int i = 0; i < vKeys.size(); i++) {
			final Object oKeyValue = f.getDataFieldValueFromFormCache(vKeys.get(i).toString());
			if (oKeyValue == null) {
				throw new Exception("Value of the key " + vKeys.get(i) + " not found in the parent form");
			}
			kv.put(vKeys.get(i), oKeyValue);
		}
		return kv;
	}

}
