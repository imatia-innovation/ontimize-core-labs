package com.ontimize.jee.desktopclient.dms.upload;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DataNavigationListener;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.CardPanel;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DocumentIdentifier;
import com.ontimize.jee.common.tools.CheckingTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable.Status;
import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsUploadable;
import com.ontimize.jee.desktopclient.dms.transfermanager.DmsTransfererManagerFactory;
import com.ontimize.jee.desktopclient.dms.viewer.DocumentationTable;
import com.ontimize.util.ParseUtils;

public class OpenUploadableChooserActionListener implements ActionListener {

	private static final Logger logger = LoggerFactory.getLogger(OpenUploadableChooserActionListener.class);

	public static final String TRANSFERABLE = "TRANSFERABLE";
	protected static Dimension PREFERRED_SIZE = new Dimension(350, 350);
	protected Form formDialog = null;
	protected String documentationTableAttr;
	DocumentationTable table = null;	

	/**
	 * Parameter to limit button enable when interaction manager is in INSERT_MODE.
	 * By default false.
	 */
	public static final String PARAM_ENABLE_FIM_INSERT = "enable.insert";
	/**
	 * Parameter to limit button enable when interaction manager is in QUERY_MODE.
	 * By default false.
	 */
	public static final String PARAM_ENABLE_FIM_QUERY = "enable.query";
	/**
	 * Parameter to limit button enable when interaction manager is in
	 * QUERY_INSERT_MODE. By default false.
	 */
	public static final String PARAM_ENABLE_FIM_QUERY_INSERT = "enable.queryinsert";
	/**
	 * Parameter to limit button enable when interaction manager is in UPDATE_MODE.
	 * By default false.
	 */
	public static final String PARAM_ENABLE_FIM_UPDATE = "enable.update";

	/** Limit button enable when interaction manager is in INSERT_MODE */
	protected boolean isInsertModeValidToEnable;
	/** Limit button enable when interaction manager is in QUERY_MODE */
	protected boolean isQueryModeValidToEnable;
	/** Limit button enable when interaction manager is in QUERY_INSERT_MODE */
	protected boolean isQueryInsertModeValidToEnable;
	/** Limit button enable when interaction manager is in UPDATE_MODE */
	protected boolean isUpdateModeValidToEnable;

	
	
	public OpenUploadableChooserActionListener(DocumentationTable table) {
		super();
		this.table = table;
	}

	protected void init(Map<?, ?> params) throws Exception {
		this.isInsertModeValidToEnable = ParseUtilsExtended
				.getBoolean((String) params.get(OpenUploadableChooserActionListener.PARAM_ENABLE_FIM_INSERT), true);
		this.isQueryModeValidToEnable = ParseUtilsExtended
				.getBoolean((String) params.get(OpenUploadableChooserActionListener.PARAM_ENABLE_FIM_QUERY), true);
		this.isQueryInsertModeValidToEnable = ParseUtilsExtended.getBoolean(
				(String) params.get(OpenUploadableChooserActionListener.PARAM_ENABLE_FIM_QUERY_INSERT), true);
		this.isUpdateModeValidToEnable = ParseUtilsExtended
				.getBoolean((String) params.get(OpenUploadableChooserActionListener.PARAM_ENABLE_FIM_UPDATE), true);

		this.documentationTableAttr = ParseUtils.getString((String) params.get("documentationtable"), null);
	}

	public void parentFormSetted() {

		// FIMUtils.injectAnnotatedFields(this, this.button.getParentForm());
		this.table.getParentForm().addDataNavigationListener((DataNavigationListener) this);

		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			table.addPropertyChangeListener("enable", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if (evt.getSource() instanceof Button) {
						
						Button b;
						b = (Button) evt.getSource();
						
						if ((b.getParentForm() == null) || (b == null) || !true) {
							return;
						}
						b.setEnabled(OpenUploadableChooserActionListener.this.getEnableValueToSet());
					}
				}
			});
		}
	}

	@Override
	public void actionPerformed(final ActionEvent ev) {
		try {
			AbstractDmsUploadable uploadable = this.showSelectionDialog(this.table.getParentForm());
			if (uploadable != null) {
				this.upload(uploadable);
			}
		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, OpenUploadableChooserActionListener.logger);
		}
	}

	protected void upload(AbstractDmsUploadable uploadable) throws DmsException {

		Serializable idDocument = (Serializable) this.table.getParentForm()
				.getDataFieldValue(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		Serializable idFile = (Serializable) this.table.getParentForm()
				.getDataFieldValue(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		Serializable idVersion = (Serializable) this.table.getParentForm()
				.getDataFieldValue(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		Serializable idCategory = (Serializable) this.table.getParentForm()
				.getDataFieldValue(DMSNaming.CATEGORY_ID_CATEGORY);

		final DocumentationTable table = this.getDocumentationTable();
		if (table != null) {
			idDocument = table.getCurrentIdDocument();
			idCategory = table.getCurrentIdCategory();
		}
		if (idDocument == null) {
			throw new DmsException(DMSNaming.ERROR_DOCUMENT_ID_MANDATORY);
		}
		DocumentIdentifier docIdf = new DocumentIdentifier(idDocument, idFile, idVersion);
		uploadable.setDocumentIdentifier(docIdf);
		uploadable.setCategoryId(idCategory);
		if (table != null) {
			uploadable.addObserver(new Observer() {

				@Override
				public void update(Observable observable, Object arg) {
					CheckingTools.failIf(!(observable instanceof AbstractDmsUploadable),
							"Observable not instanceof AbstractDmsUploadable");
					AbstractDmsUploadable uploadable = (AbstractDmsUploadable) observable;
					if (uploadable.getStatus().equals(Status.COMPLETED) && (table.getCurrentIdDocument() != null)
							&& table.getCurrentIdDocument().equals(uploadable.getDocumentIdentifier().getDocumentId())
							&& ((table.getCurrentIdCategory() == null)
									|| table.getCurrentIdCategory().equals(uploadable.getCategoryId()))) {
						table.refreshInThread(0);
					}
				}
			});
		}
		DmsTransfererManagerFactory.getInstance().transfer(uploadable);
	}

	protected DocumentationTable getDocumentationTable() {
		if (this.table == null) {
			this.table = this.findDocumentationTable(this.documentationTableAttr);
		}
		return this.table;
	}

	public DocumentationTable findDocumentationTable(String attr) {
		if (attr != null) {
			for (Object component : this.table.getParentForm().getComponentList()) {
				if ((component instanceof DocumentationTable)
						&& ((DocumentationTable) component).getAttribute().toString().equals(attr)) {
					return (DocumentationTable) component;
				}
			}
		}
		return null;
	}

	protected AbstractDmsUploadable showSelectionDialog(Form parentForm) {
		if (this.formDialog == null) {
			this.formDialog = OpenUploadableChooserActionListener.getCurrentActiveForm().getFormManager()
					.getFormCopy("ontimize-dms-forms/formMultipleFiles.form", IMMultipleFiles.class.getName());
			this.formDialog.putInModalDialog(this.formDialog.getFormTitle(), parentForm);
			this.formDialog.getJDialog().setModal(true);
			this.formDialog.getJDialog().setResizable(false);
			this.formDialog.getJDialog().setSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setPreferredSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
			this.formDialog.getJDialog().setMaximumSize(OpenUploadableChooserActionListener.PREFERRED_SIZE);
		}
		this.formDialog.deleteDataFields();
		((CardPanel) this.formDialog.getElementReference("cardpanel")).show("mainpanel");
		this.formDialog.setDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE, null);
		this.formDialog.getJDialog().setVisible(true);
		return (AbstractDmsUploadable) this.formDialog
				.getDataFieldValue(OpenUploadableChooserActionListener.TRANSFERABLE);
	}

	protected boolean getEnableValueToSet() {
		boolean enable = true;
		int fimMode = this.table.getParentForm().getInteractionManager().getCurrentMode();
		switch (fimMode) {
		case InteractionManager.INSERT:
			enable &= this.isInsertModeValidToEnable;
			break;
		case InteractionManager.QUERY:
			enable &= this.isQueryModeValidToEnable;
			break;
		case InteractionManager.QUERYINSERT:
			enable &= this.isQueryInsertModeValidToEnable;
			break;
		case InteractionManager.UPDATE:
			enable &= this.isUpdateModeValidToEnable;
			break;
		}
		return enable && this.getDocumentationTable().isEnabled();
	}

	public static Form getCurrentActiveForm() {
		String gfAct = ((MainApplication) ApplicationManager.getApplication()).getActiveFMName();
		if (gfAct != null) {
			return ApplicationManager.getApplication().getFormManager(gfAct).getActiveForm();
		}
		return null;
	}
}