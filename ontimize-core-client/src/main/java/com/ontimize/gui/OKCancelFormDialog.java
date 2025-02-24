package com.ontimize.gui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.manager.IFormManager;

public class OKCancelFormDialog extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(OKCancelFormDialog.class);

	public static final int OK = 0;

	public static final int CANCEL = 1;

	protected Form form = null;

	protected IFormManager formManager = null;

	protected String formName = null;

	protected String returnValue = null;

	protected String emptyMessage = null;

	protected Object value = null;

	protected String titleKey = null;

	protected int result = OKCancelFormDialog.CANCEL;

	protected String[] returnValues = null;

	protected OKActionValidator validator = null;

	protected static OKCancelFormDialog v = null;

	public static interface OKActionValidator {

		public boolean validateOKAction(OKCancelFormDialog d);

	}

	protected OKCancelFormDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage) {
		this(frame, formManager, title, formName, returnValue, emptyMessage, null);

	}

	@Override
	protected void setInitialFocus() {
		if (this.form != null) {
			this.form.requestDefaultFocus();
		}
	}

	protected OKCancelFormDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage, final OKActionValidator validator) {
		super(frame, title, true);
		this.validator = validator;
		final String[] returnValues = { returnValue };
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected OKCancelFormDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage) {
		super(frame, title, true);
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected OKCancelFormDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final OKActionValidator validator) {
		super(frame, title, true);
		this.validator = validator;
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected OKCancelFormDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage) {
		super(dialog, title, true);
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected OKCancelFormDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final OKActionValidator validator) {
		super(dialog, title, true);
		this.validator = validator;
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected void setFixedValues(final Map fixedValues, final String[] fixedValuesModifiables) {
		this.form.enableDataFields(true);
		final List vAttributes = this.form.getDataFieldAttributeList();
		for (int i = 0; i < vAttributes.size(); i++) {
			this.form.setModifiable(vAttributes.get(i).toString(), true);
		}
		if (fixedValues != null) {
			final Enumeration enumKeys = Collections.enumeration(fixedValues.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				OKCancelFormDialog.v.form.setDataFieldValue(oKey, fixedValues.get(oKey));
				OKCancelFormDialog.v.form.setModifiable(oKey.toString(), false);
				boolean isModifiable = false;
				if (fixedValuesModifiables != null) {
					for (int i = 0; i < fixedValuesModifiables.length; i++) {
						if (oKey.equals(fixedValuesModifiables[i])) {
							isModifiable = true;
							break;
						}
					}
				}
				if (!isModifiable) {
					if (ApplicationManager.DEBUG) {
						OKCancelFormDialog.logger.debug("Disabling fields " + oKey + " of fixed values");
					}
					OKCancelFormDialog.v.form.disableDataField(oKey);
				} else {
					if (ApplicationManager.DEBUG) {
						OKCancelFormDialog.logger.debug("The field " + oKey + " of fixed values is not disabled");
					}
				}
			}
		}
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.result = OKCancelFormDialog.CANCEL;
			this.setVisible(false);
		} else {
			super.processWindowEvent(e);
		}
	}

	protected void init(final IFormManager formManager, final String title, final String formName, final String[] returnValues,
			final String emptyMessage) {
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.formManager = formManager;
		this.titleKey = title;
		this.formName = formName;
		if (returnValues != null) {
			this.returnValue = returnValues[0];
			this.returnValues = returnValues;
		}

		this.emptyMessage = emptyMessage;
		this.createForm();
		this.installButtonListeners();
		this.pack();
		ApplicationManager.center(this);
	}

	public Form getForm() {
		return this.form;
	}

	public int getResult() {
		return this.result;
	}

	public String getFormName() {
		return this.formName;
	}

	@Override
	public String getTitle() {
		return this.titleKey;
	}

	public String getReturnValue() {
		return this.returnValue;
	}

	public Object getFormValue(final String key) {
		return key == null ? null : this.form.getDataFieldValue(key);
	}

	public String getEmptyMessage() {
		return this.emptyMessage;
	}

	protected OKCancelFormDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage) {
		super(dialog, title, true);
		final String[] returnValues = { returnValue };
		this.init(formManager, title, formName, returnValues, emptyMessage);
	}

	protected void createForm() {
		if (this.form == null) {
			this.form = this.formManager.getFormCopy(this.formName, this.getContentPane());
		}
	}

	protected void installButtonListeners() {
		final Button bAccept = this.form.getButton("accept");
		if (bAccept != null) {
			bAccept.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (OKCancelFormDialog.this.returnValues != null) {
						for (int i = 0; i < OKCancelFormDialog.this.returnValues.length; i++) {
							if (OKCancelFormDialog.this.form.isEmpty(OKCancelFormDialog.this.returnValues[i])
									&& (OKCancelFormDialog.this.emptyMessage != null)) {
								OKCancelFormDialog.this.form.message(OKCancelFormDialog.this,
										OKCancelFormDialog.this.emptyMessage, Form.ERROR_MESSAGE);
								OKCancelFormDialog.this.form.requestDefaultFocus();
								return;
							}
						}
					}
					if (OKCancelFormDialog.this.validator != null) {
						final boolean val = OKCancelFormDialog.this.validator.validateOKAction(OKCancelFormDialog.this);
						if (val) {
							OKCancelFormDialog.this.result = OKCancelFormDialog.OK;
							if (OKCancelFormDialog.this.returnValues != null) {
								OKCancelFormDialog.this.value = OKCancelFormDialog.this.form
										.getDataFieldValue(OKCancelFormDialog.this.returnValues[0]);
							}
							OKCancelFormDialog.this.setVisible(false);
						}
					} else {
						OKCancelFormDialog.this.result = OKCancelFormDialog.OK;
						if (OKCancelFormDialog.this.returnValues != null) {
							OKCancelFormDialog.this.value = OKCancelFormDialog.this.form
									.getDataFieldValue(OKCancelFormDialog.this.returnValues[0]);
						}
						OKCancelFormDialog.this.setVisible(false);
					}
				}
			});
		}

		final Button bCancel = this.form.getButton("cancel");
		if (bCancel != null) {
			bCancel.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					OKCancelFormDialog.this.result = OKCancelFormDialog.CANCEL;
					OKCancelFormDialog.this.setVisible(false);
				}
			});
		}
	}

	@Override
	public void setVisible(final boolean vis) {
		if (vis) {
			if (this.form != null) {
				this.form.enableDataFields(false);
				this.form.enableButtons();
				this.form.requestDefaultFocus();
			}
		}
		super.setVisible(vis);
	}

	public static int showDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage) {
		return OKCancelFormDialog.showDialog(frame, formManager, title, formName, returnValue, emptyMessage, null);
	}

	public static int showDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage, final OKActionValidator val) {
		if (OKCancelFormDialog.v == null) {
			OKCancelFormDialog.v = new OKCancelFormDialog(frame, formManager, title, formName, returnValue,
					emptyMessage, val);
		} else if (!(frame == OKCancelFormDialog.v.getOwner())
				|| !(OKCancelFormDialog.v.formName.equals(formName)) || !(OKCancelFormDialog.v.titleKey
						.equals(title))
				|| ((OKCancelFormDialog.v.returnValue != null) && !(OKCancelFormDialog.v.returnValue
						.equals(returnValue)))
				|| ((OKCancelFormDialog.v.emptyMessage != null) && !(OKCancelFormDialog.v.emptyMessage
						.equals(emptyMessage)))
				|| (val != OKCancelFormDialog.v.validator)) {
			try {
				OKCancelFormDialog.v.form.free();
			} catch (final Exception e) {
				OKCancelFormDialog.logger.error(e.getMessage(), e);
				if (ApplicationManager.DEBUG) {
					OKCancelFormDialog.logger.error(null, e);
				}
			}
			OKCancelFormDialog.v.dispose();
			OKCancelFormDialog.v = new OKCancelFormDialog(frame, formManager, title, formName, returnValue,
					emptyMessage, val);
		}
		SwingUtilities.updateComponentTreeUI(OKCancelFormDialog.v);
		OKCancelFormDialog.v.form.deleteDataFields(true);
		OKCancelFormDialog.v.form.setAdvancedQueryModeAll(OKCancelFormDialog.advancedQueryMode);
		OKCancelFormDialog.v.form.requestDefaultFocus();
		OKCancelFormDialog.v.setResourceBundle(formManager.getResourceBundle());
		OKCancelFormDialog.v.setVisible(true);

		return OKCancelFormDialog.v.result;
	}

	public static int showDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage) {
		return OKCancelFormDialog.showDialog(frame, formManager, title, formName, returnValues, emptyMessage, null);

	}

	public static int showDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues) {
		return OKCancelFormDialog.showDialog(frame, formManager, title, formName, returnValues, emptyMessage,
				fixedValues, null);
	}

	public static int showDialog(final Frame frame, final IFormManager formManger, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues,
			final OKActionValidator val) {
		return OKCancelFormDialog.showDialog(frame, formManger, title, formName, returnValues, emptyMessage,
				fixedValues, null, null);
	}

	public static int showDialog(final Frame frame, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues,
			final OKActionValidator val, final String[] fixedValuesModifiables) {
		if (OKCancelFormDialog.v == null) {
			OKCancelFormDialog.v = new OKCancelFormDialog(frame, formManager, title, formName, returnValues,
					emptyMessage, val);
		} else if (!(frame == OKCancelFormDialog.v.getOwner()) || !(OKCancelFormDialog.v.formName.equals(formName))
				|| !(OKCancelFormDialog.v.titleKey.equals(
						title))
				|| ((OKCancelFormDialog.v.emptyMessage != null)
						&& !(OKCancelFormDialog.v.emptyMessage.equals(emptyMessage)))
				|| (val != OKCancelFormDialog.v.validator)) {
			try {
				OKCancelFormDialog.v.form.free();
			} catch (final Exception e) {
				OKCancelFormDialog.logger.error(e.getMessage(), e);
				if (ApplicationManager.DEBUG) {
					OKCancelFormDialog.logger.error(null, e);
				}
			}
			OKCancelFormDialog.v.dispose();
			OKCancelFormDialog.v = new OKCancelFormDialog(frame, formManager, title, formName, returnValues,
					emptyMessage, val);
		} else {
			// Now checks if the return values are the same
			boolean bValuesMatch = true;
			if ((returnValues == null) && (OKCancelFormDialog.v.returnValues == null)) {
			} else {
				if (returnValues == null) {
					bValuesMatch = false;
				} else if (OKCancelFormDialog.v.returnValues == null) {
					bValuesMatch = false;
				} else if (returnValues.length != OKCancelFormDialog.v.returnValues.length) {
					bValuesMatch = false;
				} else {
					for (int i = 0; i < returnValues.length; i++) {
						if (!returnValues[i].equals(OKCancelFormDialog.v.returnValues[i])) {
							bValuesMatch = false;
							break;
						}
					}
				}
			}
			if (!bValuesMatch) {
				try {
					OKCancelFormDialog.v.form.free();
				} catch (final Exception e) {
					OKCancelFormDialog.logger.error(e.getMessage(), e);
					if (ApplicationManager.DEBUG) {
						OKCancelFormDialog.logger.error(null, e);
					}
				}
				OKCancelFormDialog.v.dispose();
				OKCancelFormDialog.v = new OKCancelFormDialog(frame, formManager, title, formName, returnValues,
						emptyMessage, val);
			}
		}
		SwingUtilities.updateComponentTreeUI(OKCancelFormDialog.v);
		OKCancelFormDialog.v.form.deleteDataFields(true);
		OKCancelFormDialog.v.form.setAdvancedQueryModeAll(OKCancelFormDialog.advancedQueryMode);
		OKCancelFormDialog.v.setFixedValues(fixedValues, fixedValuesModifiables);
		OKCancelFormDialog.v.form.requestDefaultFocus();
		OKCancelFormDialog.v.setResourceBundle(formManager.getResourceBundle());
		OKCancelFormDialog.v.setVisible(true);

		return OKCancelFormDialog.v.result;
	}

	public static int showDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage) {
		return OKCancelFormDialog.showDialog(dialog, formManager, title, formName, returnValues, emptyMessage, null);

	}

	public static int showDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues) {
		return OKCancelFormDialog.showDialog(dialog, formManager, title, formName, returnValues, emptyMessage,
				fixedValues, null);
	}

	public static int showDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues,
			final OKActionValidator val) {
		return OKCancelFormDialog.showDialog(dialog, formManager, title, formName, returnValues, emptyMessage,
				fixedValues, null, null);
	}

	public static int showDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String[] returnValues, final String emptyMessage, final Map fixedValues,
			final OKActionValidator val, final String[] fixedValuesModifiables) {
		if (OKCancelFormDialog.v == null) {
			OKCancelFormDialog.v = new OKCancelFormDialog(dialog, formManager, title, formName, returnValues,
					emptyMessage, val);
		} else if (!(dialog == OKCancelFormDialog.v.getOwner()) || !(OKCancelFormDialog.v.formName.equals(formName))
				|| !(OKCancelFormDialog.v.titleKey.equals(
						title))
				|| ((OKCancelFormDialog.v.emptyMessage != null)
						&& !(OKCancelFormDialog.v.emptyMessage.equals(emptyMessage)))
				|| (val != OKCancelFormDialog.v.validator)) {
			try {
				OKCancelFormDialog.v.form.free();
			} catch (final Exception e) {
				OKCancelFormDialog.logger.error(e.getMessage(), e);
				if (ApplicationManager.DEBUG) {
					OKCancelFormDialog.logger.error(null, e);
				}
			}
			OKCancelFormDialog.v.dispose();
			OKCancelFormDialog.v = new OKCancelFormDialog(dialog, formManager, title, formName, returnValues,
					emptyMessage, val);
		} else {
			// Checks if the return values are the same
			boolean bValuesMatch = true;
			if ((returnValues == null) && (OKCancelFormDialog.v.returnValues == null)) {

			} else {
				if (returnValues == null) {
					bValuesMatch = false;
				} else if (OKCancelFormDialog.v.returnValues == null) {
					bValuesMatch = false;
				} else if (returnValues.length != OKCancelFormDialog.v.returnValues.length) {
					bValuesMatch = false;
				} else {
					for (int i = 0; i < returnValues.length; i++) {
						if (!returnValues[i].equals(OKCancelFormDialog.v.returnValues[i])) {
							bValuesMatch = false;
							break;
						}
					}
				}
			}
			if (!bValuesMatch) {
				try {
					OKCancelFormDialog.v.form.free();
				} catch (final Exception e) {
					OKCancelFormDialog.logger.error(e.getMessage(), e);
					if (ApplicationManager.DEBUG) {
						OKCancelFormDialog.logger.error(null, e);
					}
				}
				OKCancelFormDialog.v.dispose();
				OKCancelFormDialog.v = new OKCancelFormDialog(dialog, formManager, title, formName, returnValues,
						emptyMessage, val);
			}
		}
		SwingUtilities.updateComponentTreeUI(OKCancelFormDialog.v);
		OKCancelFormDialog.v.form.deleteDataFields(true);
		OKCancelFormDialog.v.form.setAdvancedQueryModeAll(OKCancelFormDialog.advancedQueryMode);
		OKCancelFormDialog.v.setFixedValues(fixedValues, fixedValuesModifiables);
		OKCancelFormDialog.v.form.requestDefaultFocus();
		OKCancelFormDialog.v.setResourceBundle(formManager.getResourceBundle());
		OKCancelFormDialog.v.setVisible(true);

		return OKCancelFormDialog.v.result;
	}

	public static int showDialog(final Dialog dialog, final IFormManager formManager, final String title, final String formName,
			final String returnValue, final String emptyMessage) {
		if (OKCancelFormDialog.v == null) {
			OKCancelFormDialog.v = new OKCancelFormDialog(dialog, formManager, title, formName, returnValue,
					emptyMessage);
		} else if (!(dialog == OKCancelFormDialog.v.getOwner()) || !OKCancelFormDialog.v.formName.equals(formName)
				|| !OKCancelFormDialog.v.titleKey
				.equals(title)
				|| !OKCancelFormDialog.v.returnValue.equals(returnValue)
				|| !OKCancelFormDialog.v.emptyMessage.equals(emptyMessage)) {
			try {
				OKCancelFormDialog.v.form.free();
			} catch (final Exception e) {
				OKCancelFormDialog.logger.error(e.getMessage(), e);
				if (ApplicationManager.DEBUG) {
					OKCancelFormDialog.logger.error(null, e);
				}
			}
			OKCancelFormDialog.v.dispose();
			OKCancelFormDialog.v = new OKCancelFormDialog(dialog, formManager, title, formName, returnValue,
					emptyMessage);
		}
		SwingUtilities.updateComponentTreeUI(OKCancelFormDialog.v);
		OKCancelFormDialog.v.form.deleteDataFields(true);
		OKCancelFormDialog.v.form.setAdvancedQueryModeAll(OKCancelFormDialog.advancedQueryMode);
		OKCancelFormDialog.v.form.requestDefaultFocus();
		OKCancelFormDialog.v.setResourceBundle(formManager.getResourceBundle());
		OKCancelFormDialog.v.setVisible(true);

		return OKCancelFormDialog.v.result;
	}

	public static Object getValue() {
		return OKCancelFormDialog.v == null ? null : OKCancelFormDialog.v.value;
	}

	public static Object[] getValues() {
		if (OKCancelFormDialog.v == null) {
			return null;
		} else {
			final Object[] values = new Object[OKCancelFormDialog.v.returnValues.length];
			for (int i = 0; i < OKCancelFormDialog.v.returnValues.length; i++) {
				values[i] = OKCancelFormDialog.v.form.getDataFieldValue(OKCancelFormDialog.v.returnValues[i]);
			}
			return values;
		}
	}

	public static void setValue(final String attr, final Object value) {
		if (OKCancelFormDialog.v != null) {
			OKCancelFormDialog.v.form.setDataFieldValue(attr, value);
		}
		return;
	}

	public static Object getValue(final String attr) {
		return OKCancelFormDialog.v == null ? null : OKCancelFormDialog.v.form.getDataFieldValue(attr);
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector();
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		if (this.form != null) {
			this.form.setResourceBundle(res);
		}
		try {
			if (res != null) {
				this.setTitle(res.getString(this.titleKey));
			}
		} catch (final Exception e) {
			OKCancelFormDialog.logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	protected static boolean advancedQueryMode = false;

	public static void setAdvancedQueryModeOn(final boolean on) {
		OKCancelFormDialog.advancedQueryMode = on;
	}

	public static boolean getAdvancedQueryModeOn() {
		return OKCancelFormDialog.advancedQueryMode;
	}

}
