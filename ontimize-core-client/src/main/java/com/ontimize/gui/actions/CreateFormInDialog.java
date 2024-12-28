package com.ontimize.gui.actions;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormExt;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.dto.EntityResult;

public class CreateFormInDialog extends AbstractButtonAction {

	private static final Logger logger = LoggerFactory.getLogger(CreateFormInDialog.class);

	protected String formManagerName = null;

	protected String formName = null;

	protected String titleKey = null;

	protected JDialog dialog = null;

	protected Form form = null;

	protected Form sourceForm = null;

	protected Map equivalentsFields = null;

	public CreateFormInDialog(final String formManagerName, final String formName, final String titleKey) {
		this.formManagerName = formManagerName;
		this.formName = formName;
		this.titleKey = titleKey;
	}

	public CreateFormInDialog(final String formManagerName, final String formName, final String titleKey, final Map equivalentFields) {
		this.formManagerName = formManagerName;
		this.formName = formName;
		this.titleKey = titleKey;
		this.equivalentsFields = equivalentFields;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		// Form creation if not exists.
		final Object source = e.getSource();
		this.sourceForm = ApplicationManager.getFormAncestor((Component) source);
		if ((this.sourceForm == null) && (this.formManagerName == null)) {
			CreateFormInDialog.logger.debug(this.getClass().toString() + ". The source of the event must be in a form");
			return;
		}
		if (this.formManagerName == null) {
			this.formManagerName = this.sourceForm.getFormManager().getId();
		}

		if (this.dialog == null) {
			Window w = null;

			if (source instanceof Component) {
				w = SwingUtilities.getWindowAncestor((Component) source);
			}
			this.createDialog(w);
		}

		Locale locale = null;
		ResourceBundle res = null;
		if (this.formManagerName != null) {
			final IFormManager fManager = ApplicationManager.getApplication().getFormManager(this.formManagerName);
			if (fManager != null) {
				locale = fManager.getLocale();
				res = fManager.getResourceBundle();
			}
		} else if (this.sourceForm != null) {
			locale = this.sourceForm.getFormManager().getLocale();
			res = this.sourceForm.getFormManager().getResourceBundle();
		}

		if (this.form != null) {
			this.form.setLocale(locale);
			this.form.setResourceBundle(res);
		}

		final String sTitle = ApplicationManager.getTranslation(this.titleKey, this.form.getResourceBundle());
		this.dialog.setTitle(sTitle);
		// Now show
		this.windowWillShow();
		this.dialog.setVisible(true);

	}

	public void windowWillShow() {

	}

	public boolean windowWillClose() {
		return true;
	}

	protected void createDialog(final Window w) {
		if (this.dialog == null) {
			if (w instanceof Dialog) {
				this.dialog = new EJDialog((Dialog) w, this.titleKey, true) {

					@Override
					protected void processWindowEvent(final WindowEvent e) {
						if (e.getID() == WindowEvent.WINDOW_CLOSING) {
							if (CreateFormInDialog.this.windowWillClose()) {
								super.processWindowEvent(e);
							}
						} else {
							super.processWindowEvent(e);
						}
					}
				};
			} else {
				this.dialog = new EJDialog((Frame) w, this.titleKey, true) {

					@Override
					protected void processWindowEvent(final WindowEvent e) {
						if (e.getID() == WindowEvent.WINDOW_CLOSING) {
							if (CreateFormInDialog.this.windowWillClose()) {
								super.processWindowEvent(e);
							}
						} else {
							super.processWindowEvent(e);
						}
					}
				};
			}
			this.form = ApplicationManager.getApplication()
					.getFormManager(this.formManagerName)
					.getFormCopy(this.formName, this.dialog.getContentPane());
			((EJDialog) this.dialog).setSizePositionPreference(this.form.getSizeDialogPreferenceKey());
			this.dialog.pack();
			ApplicationManager.center(this.dialog);
		}
	}

	public void goToSourceRecord() throws Exception {
		// Get the keys of the form that contains the source component
		if (this.sourceForm == null) {
			throw new Exception("The form which is the event source is null");
		}
		final Map hKeysValues = new Hashtable();
		final Map kv = new Hashtable();
		final List vKeys = this.form.getKeys();
		if (vKeys.isEmpty()) {
			throw new Exception("'keys' parameter is mandatory in the form to open");
		} else {
			for (int i = 0; i < vKeys.size(); i++) {
				final String sField = vKeys.get(i).toString();
				String eqField = sField;
				if ((this.equivalentsFields != null) && this.equivalentsFields.containsKey(sField)) {
					eqField = (String) this.equivalentsFields.get(sField);
				}
				final Object oValue = this.sourceForm.getDataFieldValue(eqField);
				final List v = new Vector();
				v.add(0, oValue);
				if (oValue != null) {
					hKeysValues.put(sField, v);
					kv.put(sField, oValue);
				}
			}
			final List parentkeys = this.form.getParentKeys();
			for (int i = 0; i < parentkeys.size(); i++) {
				final String sField = parentkeys.get(i).toString();
				String eqField = sField;
				if ((this.equivalentsFields != null) && this.equivalentsFields.containsKey(sField)) {
					eqField = (String) this.equivalentsFields.get(sField);
				}

				final Object oValue = this.sourceForm.getDataFieldValue(eqField);
				final List v = new Vector();
				v.add(0, oValue);
				if (oValue != null) {
					hKeysValues.put(sField, v);
					kv.put(sField, oValue);
				}
			}
		}
		if (this.form.getInteractionManager() != null) {
			this.form.getInteractionManager().setQueryInsertMode();
		}
		if (this.form instanceof FormExt) {
			this.form.updateDataFields(hKeysValues);
		} else {
			final EntityResult res = this.form.query(kv, this.form.getDataFieldAttributeList());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.sourceForm.message(res.getMessage(), Form.ERROR_MESSAGE);
			} else {
				this.form.updateDataFields(EntityResultUtils.toMap(res));
			}
		}
		if (this.form.getInteractionManager() != null) {
			this.form.getInteractionManager().setUpdateMode();
		}
	}

	public Object getCurrentRecordValueField(final Object key) {
		if ((this.form == null) || (key == null)) {
			return null;
		}
		return this.form.getDataFieldValue(key.toString());
	}

	public JDialog getDialog() {
		return this.dialog;
	}

	public Form getForm() {
		return this.form;
	}

}
