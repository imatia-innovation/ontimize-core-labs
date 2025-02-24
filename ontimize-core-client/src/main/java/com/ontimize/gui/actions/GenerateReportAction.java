package com.ontimize.gui.actions;

import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.report.ReportUtils;

public class GenerateReportAction extends AbstractButtonAction {

	public static final String M_INSERT_VALUE_FIELD = "value_must_be_entered_message";

	protected String entity = null;

	protected String uriXMLReportDefinition = null;

	protected List keys = null;

	protected List requiredKeys = null;

	protected boolean preview = true;

	protected boolean printDialog = true;

	public GenerateReportAction(final String entity, final String uriXMLReportDefinition, final boolean preview, final List keys,
			final List requiredKeys, final boolean printDialog) {
		this.entity = entity;
		this.uriXMLReportDefinition = uriXMLReportDefinition;
		this.keys = keys;
		this.requiredKeys = requiredKeys;
		this.preview = preview;
		this.printDialog = printDialog;
	}

	protected Map getReportValuesKeys(final Form f) throws Exception {
		if ((this.keys == null) || this.keys.isEmpty()) {
			if (f == null) {
				throw new Exception("parent form is null");
			}
			final Map kv = new Hashtable();
			final List vKeys = f.getKeys();
			if (vKeys.isEmpty()) {
				throw new Exception("The 'keys' parameter is necessary  in the parent form");
			}
			for (int i = 0; i < vKeys.size(); i++) {
				final Object oKeyValue = f.getDataFieldValueFromFormCache(vKeys.get(i).toString());
				if (oKeyValue == null) {
					throw new Exception("Value for the key " + vKeys.get(i) + " not found in parent form");
				}
				kv.put(vKeys.get(i), oKeyValue);
			}
			return kv;
		} else {
			final List vKeys = this.keys;
			final Map kv = new Hashtable();
			for (int i = 0; i < vKeys.size(); i++) {
				final Object oKeyValue = f.getDataFieldValue(vKeys.get(i).toString());
				if (oKeyValue == null) {
					if ((this.requiredKeys != null) && this.requiredKeys.contains(vKeys.get(i))) {
						throw new RuntimeException("");
					}
				} else {
					kv.put(vKeys.get(i), oKeyValue);
				}
			}
			return kv;
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final Form f = this.getForm(e);
		try {
			final Entity ent = f.getFormManager().getReferenceLocator().getEntityReference(this.entity);
			if (ent instanceof com.ontimize.jee.common.db.PrintDataEntity) {
				final EntityResult res = ((com.ontimize.jee.common.db.PrintDataEntity) ent).getPrintingData(
						this.getReportValuesKeys(f),
						f.getFormManager().getReferenceLocator().getSessionId());
				if (res.getCode() == EntityResult.OPERATION_WRONG) {
					f.message(res.getMessage(), Form.ERROR_MESSAGE);
				} else if (res.isEmpty()) {
					f.message("M_NOT_RESULTS_FOUND", Form.WARNING_MESSAGE);
				} else {
					final java.net.URL url = this.getClass().getClassLoader().getResource(this.uriXMLReportDefinition);
					final com.ontimize.report.utils.PreviewDialog d = ReportUtils.getPreviewDialog(f,
							ApplicationManager.getTranslation("TituloImpresionAlbaran", f.getResourceBundle()),
							EntityResultUtils.createTableModel(res), url, null);
					if (!this.preview) {
						d.print(this.printDialog);
					} else {
						d.setVisible(true);
					}
				}
			} else {
				f.message("M_INVALID_REPORT_ENTITY_NO_REPORT_DATA_FOUND", Form.ERROR_MESSAGE);
			}
		} catch (final Exception ex) {
			if (!(ex instanceof RuntimeException)) {
				f.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
			}
		}

	}

}
