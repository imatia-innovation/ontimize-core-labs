package com.ontimize.gui;

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ObjectTools;

/**
 * Form that is shown when user makes double click in a table component
 *
 * @version 1.2 15/06/2001
 */
public class DetailForm extends EJDialog
implements Internationalization, DataNavigationListener, IDetailForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(DetailForm.class);

	public static final String M_MODIFIED_DATA_CLOSE_AND_LOST_CHANGES = "detailform.modified_data_discard_changes_and_close";

	protected Map tableKeys = null;

	protected List fieldsKey = null;

	protected int vectorIndex = 0;

	protected Form form = null;

	protected Map data = new Hashtable();

	/**
	 * @deprecated
	 */
	@Deprecated
	protected Object parentKeyValue = null;

	/**
	 * @deprecated
	 */
	@Deprecated
	protected String parentKeyName = null;

	/**
	 * @deprecated
	 */
	@Deprecated
	protected Map otherParentKeys = null;

	protected Map parentkeys = null;

	protected Table table = null;

	protected String title = null;

	protected CardLayout cardLayout = new CardLayout();

	protected Map cacheForms = new Hashtable(1, 5);

	protected boolean dataChangeEventProcessing = true;

	public boolean checkDataBeforeClose = true;

	protected class ActivationFormListener extends WindowAdapter {

		Form currentForm = null;

		public ActivationFormListener(final Form f) {
			this.currentForm = f;
		}

		@Override
		public void windowActivated(final WindowEvent e) {
			if (this.currentForm.getFormManager() != null) {
				this.currentForm.getFormManager().setActiveForm(this.currentForm);
			}
		}

		@Override
		public void windowDeactivated(final WindowEvent e) {
			if (this.currentForm.getFormManager() != null) {
				this.currentForm.getFormManager().setActiveForm(null);
			}
		}

	};

	protected ActivationFormListener activedFormListener = null;

	protected boolean packed = false;

	protected Map codValues = null;

	protected Map reverseCodValues = null;

	public DetailForm(final Frame f, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Table sourceTable, final Map parentkeys) {
		this(f, title, modal, form, tableKeys, keyFields, sourceTable, parentkeys, null);
	}

	public DetailForm(final Frame f, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Table sourceTable, final Map parentkeys, final Map codValues) {
		super(f, title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, sourceTable, parentkeys);
	}

	public DetailForm(final Dialog d, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Table sourceTable, final Map parentkeys) {
		this(d, title, modal, form, tableKeys, keyFields, sourceTable, parentkeys, null);
	}

	public DetailForm(final Dialog d, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Table sourceTable, final Map parentkeys, final Map codValues) {
		super(d, title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, sourceTable, parentkeys);
	}

	public DetailForm(final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields, final Table sourceTable,
			final Map parentKeys) {
		this(title, modal, form, tableKeys, keyFields, sourceTable, parentKeys, null);
	}

	public DetailForm(final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields, final Table sourceTable,
			final Map parentkeys, final Map codValues) {
		super(form.getParentFrame(), title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, sourceTable, parentkeys);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final Frame f, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentkeys) {
		this(f, title, modal, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentkeys,
				null);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final Frame f, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentkeys, final Map codValues) {
		super(f, title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentkeys);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final Dialog d, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentkeys) {
		this(d, title, modal, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentkeys,
				null);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final Dialog d, final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentkeys, final Map codValues) {
		super(d, title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentkeys);

	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentKeys) {
		this(title, modal, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentKeys,
				null);
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public DetailForm(final String title, final boolean modal, final Form form, final Map tableKeys, final List keyFields,
			final Object parentkeyValue, final String parentkeyName, final Table sourceTable,
			final Map otherParentkeys, final Map codValues) {
		super(form.getParentFrame(), title, modal);
		this.initCodValues(codValues);
		this.init(title, form, tableKeys, keyFields, parentkeyValue, parentkeyName, sourceTable, otherParentkeys);
	}

	protected void initCodValues(final Map codValues) {
		if (codValues == null) {
			return;
		}
		this.codValues = codValues;
		this.reverseCodValues = new Hashtable();
		final Enumeration enumeration = Collections.enumeration(this.codValues.keySet());
		while (enumeration.hasMoreElements()) {
			final Object current = enumeration.nextElement();
			this.reverseCodValues.put(this.codValues.get(current), current);
		}
	}

	@Override
	public String getFormFieldName(final Object name) {
		if (name == null) {
			return null;
		}
		return this.getFormFieldName(name.toString());
	}

	protected String getFormFieldName(final String name) {
		if (this.codValues == null) {
			return name;
		}
		if (this.codValues.containsKey(name)) {
			return (String) this.codValues.get(name);
		}
		return name;
	}

	protected String getTableFieldName(final String name) {
		if (this.reverseCodValues == null) {
			return name;
		}
		if (this.reverseCodValues.containsKey(name)) {
			return (String) this.reverseCodValues.get(name);
		}
		return name;
	}

	@Override
	public String getTableFieldName(final Object name) {
		if (name == null) {
			return null;
		}
		return this.getTableFieldName(name.toString());
	}

	@Override
	public Map valuesToForm(final Map values) {
		if (values != null) {
			final Map clone = new Hashtable();
			final Enumeration enumeration = Collections.enumeration(values.keySet());
			while (enumeration.hasMoreElements()) {
				final Object current = enumeration.nextElement();
				clone.put(this.getFormFieldName(current.toString()), values.get(current));
			}
			return clone;
		}
		return null;
	}

	@Override
	public Map valuesToTable(final Map values) {
		final Map clone = new Hashtable();
		final Enumeration enumeration = Collections.enumeration(values.keySet());
		while (enumeration.hasMoreElements()) {
			final Object current = enumeration.nextElement();
			clone.put(this.getTableFieldName(current.toString()), values.get(current));
		}
		return clone;
	}

	protected List listToForm(final List list) {
		final List current = new Vector();
		for (int i = 0; i < list.size(); i++) {
			current.add(this.getFormFieldName(list.get(i).toString()));
		}
		return current;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected void init(final String title, final Form form, final Map tableKeys, final List keyFields, final Object parentkeyValue,
			final String parentkeyName, final Table sourceTable, final Map otherParentkeys) {
		final Map parentkeysvalues = new Hashtable();
		if ((parentkeyName != null) && (parentkeyValue != null)) {
			parentkeysvalues.put(parentkeyName, parentkeyValue);
		}
		if (otherParentkeys != null) {
			parentkeysvalues.putAll(otherParentkeys);
		}
		this.init(title, form, tableKeys, keyFields, sourceTable, parentkeysvalues);
	}

	/**
	 * This method is used only to preserver compatibility with previous versions
	 * @param parentkeys
	 */
	private void assignParentkeyOtherParentkeys(final Map parentkeys) {
		if ((parentkeys != null) && (parentkeys.size() > 0)) {
			final Map pClone = new Hashtable(parentkeys);
			final Enumeration pkeyNames = Collections.enumeration(pClone.keySet());
			final Object pName = pkeyNames.nextElement();
			this.parentKeyName = this.getFormFieldName(pName);
			this.parentKeyValue = pClone.remove(pName);
			if (pClone.size() > 0) {
				this.otherParentKeys = this.valuesToForm(pClone);
			}
		}
	}

	protected void init(final String title, final Form form, final Map tableKeys, final List keyFields, final Table sourceTable,
			final Map parentkeyValues) {
		this.autoPackOnOpen = false;
		this.title = title;
		this.tableKeys = this.valuesToForm(tableKeys);
		this.form = form;
		this.activedFormListener = new ActivationFormListener(form);
		this.addWindowListener(this.activedFormListener);
		this.table = sourceTable;

		this.parentkeys = this.valuesToForm(parentkeyValues);
		// Call this method only to preserver compatibility
		this.assignParentkeyOtherParentkeys(parentkeyValues);

		this.form.setDetailForm(this);
		this.form.disableDataFields();
		this.fieldsKey = this.listToForm(keyFields);

		if (form.getInteractionManager() instanceof BasicInteractionManager) {
			((BasicInteractionManager) form.getInteractionManager()).setDetailForm(true);
		}

		final Enumeration c = Collections.enumeration(this.parentkeys.keySet());
		while (c.hasMoreElements()) {
			form.setModifiable(c.nextElement().toString(), false);
		}
		this.vectorIndex = 0;

		this.installButtonsListeners();

		this.getContentPane().setLayout(this.cardLayout);
		this.getContentPane().add(form, this.form.getArchiveName());
		if (this.form.getArchiveName() != null) {
			this.cacheForms.put(form.getArchiveName(), form);
		}
		this.pack();
		// Center in the screen
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		// If tableKeys is empty then no query is executed
		if (tableKeys.isEmpty()) {
			this.form.disableButtons();
			this.form.disableDataFields();
			this.form.deleteDataFields();
			this.form.addDataNavigationListener(this);
			return;
		} else {
			// Save data in a Map with the List index
			this.vectorIndex = this.table.getSelectedRow();
			if (this.form instanceof FormExt) {
				((FormExt) this.form).updateDataFields(tableKeys, -1);
			} else {
				this.data = com.ontimize.jee.common.util.EntityResultUtils.toMap(this.query(this.vectorIndex));
				this.form.updateDataFields(this.data);
			}

			// If there are more than one record
			int recordNumber = 0;

			final Enumeration enumTableKeys = Collections.enumeration(this.tableKeys.keySet());
			final List vKeys = (List) this.tableKeys.get(enumTableKeys.nextElement());
			recordNumber = vKeys.size();
			if (recordNumber > 1) {
				// If all buttons are not null and keys is greater than 1
				this.form.startButton.setEnabled(true);
				this.form.previousButton.setEnabled(true);
				this.form.nextButton.setEnabled(true);
				this.form.endButton.setEnabled(true);
			}
			this.form.addDataNavigationListener(this);
		}
	}

	@Override
	public void showDetailForm() {
		if (!this.packed) {
			this.pack();
			this.packed = true;
		}
		this.setVisible(true);
	}

	@Override
	public void hideDetailForm() {
		this.setVisible(false);
	}

	@Override
	public Table getTable() {
		return this.table;
	}

	/**
	 * This method sets the keys in the table records.<br>
	 * This keys are used to query the record values
	 * @param tableValues
	 * @param index
	 */
	@Override
	public void setKeys(final Map tableValues, int index) {
		this.tableKeys = new Hashtable<Object, Object>();
		final Map tempKeys = this.valuesToForm(tableValues);
		final List formKeys = this.form.getKeys();
		final Enumeration keys = Collections.enumeration(tempKeys.keySet());
		while (keys.hasMoreElements()) {
			final Object key = keys.nextElement();
			if (formKeys.contains(key)) {
				this.tableKeys.put(key, tempKeys.get(key));
			}
		}

		// Reset the index of the selected element
		this.vectorIndex = 0;

		// If there are more than one record
		int recordNumber = 0;
		if (this.tableKeys.isEmpty()) {
			this.form.disableButtons();
			this.form.disableDataFields();
		} else {
			final Enumeration enumTableKeys = Collections.enumeration(this.tableKeys.keySet());
			final List vKeys = (List) this.tableKeys.get(enumTableKeys.nextElement());
			recordNumber = vKeys.size();
		}

		if (index < 0) {
			index = 0;
		}

		if (index < recordNumber) {
			this.vectorIndex = index;
		}
		if (!this.tableKeys.isEmpty()) {
			if (!(this.form instanceof FormExt)) {
				if (this.vectorIndex >= 0) {
					this.data = com.ontimize.jee.common.util.EntityResultUtils.toMap(this.query(this.vectorIndex));
					this.form.updateDataFields(this.data);
					if (recordNumber > 1) {
						this.form.startButton.setEnabled(true);
						this.form.previousButton.setEnabled(true);
						this.form.nextButton.setEnabled(true);
						this.form.endButton.setEnabled(true);
						if (this.vectorIndex == 0) {
							this.form.startButton.setEnabled(false);
							this.form.previousButton.setEnabled(false);
						} else if (this.vectorIndex >= (recordNumber - 1)) {
							this.form.nextButton.setEnabled(false);
							this.form.endButton.setEnabled(false);
						}
					}
				} else {
					this.form.updateDataFields(new Hashtable());
				}
			} else {
				((FormExt) this.form).updateDataFields(this.tableKeys, this.vectorIndex);
			}
		} else {
			this.form.updateDataFields(new Hashtable());
		}
		if (recordNumber == 0) {
			this.setQueryInsertMode();
		}
	}

	protected EntityResult query(final int index) {
		EntityResult res = null;
		try {
			final Map hKeysValues = new Hashtable();
			if (index >= 0) {
				// parent key values are used in the query too
				// Parentkey;
				if (this.parentkeys != null) {
					// Other parent keys
					final Enumeration enumOtherKeys = Collections.enumeration(this.parentkeys.keySet());
					while (enumOtherKeys.hasMoreElements()) {
						final Object oParentkeyElement = enumOtherKeys.nextElement();
						if (this.parentkeys.get(oParentkeyElement) == null) {
							if (ApplicationManager.DEBUG) {
								MessageDialog.showErrorMessage(this,
										"DEBUG: DetailForm: parentkey " + oParentkeyElement
										+ " is NULL. It won't be included in the query. Check the xml that contains the table configuration and ensure that the parentkey has value there.");
							}
						} else {
							hKeysValues.put(oParentkeyElement, this.parentkeys.get(oParentkeyElement));
						}
					}
				}
				final List vTableKeys = this.table.getKeys();
				for (int i = 0; i < vTableKeys.size(); i++) {
					final Object oKeyField = vTableKeys.get(i);
					final List vKeyValues = (List) this.tableKeys.get(oKeyField);
					if (vKeyValues.size() <= index) {
						if (ApplicationManager.DEBUG) {
							MessageDialog.showErrorMessage(this,
									"DEBUG: DetailForm: Map with the detail form keys contains less elements for the key "
											+ oKeyField + " than the selected index " + index);
						}
						return new EntityResultMapImpl();
					}
					if (vKeyValues.get(index) == null) {
						if (ApplicationManager.DEBUG) {
							MessageDialog.showErrorMessage(this,
									"DEBUG: DetailForm:  Map with the detail form keys contains a NULL value for the key: "
											+ oKeyField + " in the selected index: " + index);
						}
					}
					hKeysValues.put(oKeyField, vKeyValues.get(index));
				}

			} else {
				return new EntityResultMapImpl();
			}
			final EntityReferenceLocator referenceLocator = this.form.getFormManager().getReferenceLocator();
			final Entity entity = referenceLocator.getEntityReference(this.form.getEntityName());
			final List vAttributeList = ObjectTools.clone(this.form.getDataFieldAttributeList());
			// If key is not include then add it to the query fields, but it can
			// be
			// an ReferenceFieldAttribute
			for (int i = 0; i < this.fieldsKey.size(); i++) {
				boolean containsKey = false;
				for (int j = 0; j < vAttributeList.size(); j++) {
					final Object oAttribute = vAttributeList.get(j);
					if (oAttribute.equals(this.fieldsKey.get(i))) {
						containsKey = true;
						break;
					} else if (oAttribute instanceof ReferenceFieldAttribute) {
						if (((ReferenceFieldAttribute) oAttribute).getAttr() != null) {
							if (((ReferenceFieldAttribute) oAttribute).getAttr().equals(this.fieldsKey.get(i))) {
								containsKey = true;
								break;
							}
						}
					}
				}
				if (!containsKey) {
					vAttributeList.add(this.fieldsKey.get(i));
				}
			}
			res = entity.query(hKeysValues, vAttributeList, referenceLocator.getSessionId());
			// For each key get the value and add it to the data
			return res;
		} catch (final Exception e) {
			DetailForm.logger
			.error("DetailForm: Error in query. Check the parameters, the xml and the entity configuration", e);
			if (ApplicationManager.DEBUG) {
				DetailForm.logger.error(null, e);
			}
			return new EntityResultMapImpl();
		}
	}

	@Override
	public void setInsertMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setInsertMode();
	}

	@Override
	public void setUpdateMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setUpdateMode();
	}

	public void forceUpdateMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setUpdateMode();

	}

	@Override
	public void setQueryInsertMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setQueryInsertMode();
	}

	@Override
	public void setQueryMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setQueryMode();

	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getParentKeyName() {
		return this.parentKeyName;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public Object getParentKeyValue() {
		return this.form.getParentKeyValue();
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	protected void updateFieldsOthersParentKeys() {
		// Fill form fields used as parent keys
		if ((this.otherParentKeys != null) && !this.otherParentKeys.isEmpty()) {
			final Enumeration enumOtherParentKeys = Collections.enumeration(this.otherParentKeys.keySet());
			while (enumOtherParentKeys.hasMoreElements()) {
				final Object oParentkey = enumOtherParentKeys.nextElement();
				this.form.setDataFieldValue(oParentkey, this.otherParentKeys.get(oParentkey));
				final DataComponent comp = this.form.getDataFieldReference(oParentkey.toString());
				if (comp != null) {
					comp.setModifiable(false);
				}
			}
		}
	}

	/**
	 * @deprecated
	 * @param otherParentKeys
	 */
	@Deprecated
	public void resetOthersParentKeys(final List otherParentKeys) {
		if (otherParentKeys != null) {
			for (int i = 0; i < otherParentKeys.size(); i++) {
				final String formAttr = this.getFormFieldName(otherParentKeys.get(i));
				final DataComponent comp = this.form.getDataFieldReference(formAttr);
				if (comp != null) {
					comp.setModifiable(true);
					comp.deleteData();
				}
			}
		}
	}

	@Override
	public void resetParentkeys(final List parentKeys) {
		if (parentKeys != null) {
			for (int i = 0; i < parentKeys.size(); i++) {
				final String formAttr = this.getFormFieldName(parentKeys.get(i));
				final DataComponent comp = this.form.getDataFieldReference(formAttr);
				if (comp != null) {
					comp.setModifiable(true);
					comp.deleteData();
				}
			}
		}
	}

	protected void updateFieldsParentkeys() {
		// Fill form fields used as parent keys
		if ((this.parentkeys != null) && !this.parentkeys.isEmpty()) {
			final Enumeration enumOtherParentKeys = Collections.enumeration(this.parentkeys.keySet());
			while (enumOtherParentKeys.hasMoreElements()) {
				final Object oParentkey = enumOtherParentKeys.nextElement();
				this.form.setDataFieldValue(oParentkey, this.parentkeys.get(oParentkey));
				final DataComponent comp = this.form.getDataFieldReference(oParentkey.toString());
				if (comp != null) {
					comp.setModifiable(false);
				}
			}
		}
	}

	@Override
	public void setAttributeToFix(final Object attribute, final Object value) {
		if (attribute == null) {
			return;
		}
		final String formAttr = this.getFormFieldName(attribute.toString());
		this.form.setDataFieldValue(formAttr, value);
		final DataComponent comp = this.form.getDataFieldReference(formAttr);
		if (comp != null) {
			comp.setModifiable(false);
		}
	}

	/**
	 * @deprecated
	 * @param otherKeys
	 */
	@Deprecated
	public void setOthersParentKeys(final Map otherKeys) {
		// For each key set the form data fields associated as no modifiable
		this.otherParentKeys = this.valuesToForm(otherKeys);
		this.updateFieldsOthersParentKeys();
	}

	/**
	 * @deprecated
	 * @param value
	 */
	@Deprecated
	public void setParentKeyValue(final Object value) {
		this.form.setParentKeyValue(this.parentKeyName, value);
		this.parentKeyValue = value;
	}

	@Override
	public void setParentKeyValues(final Map parentKeyValues) {
		this.parentkeys = this.valuesToForm(parentKeyValues);
		this.updateFieldsParentkeys();
	}

	/**
	 * Clear the detail form data cache and set the primary keys of the table again.<br>
	 * In this way all queries are executed when user uses the form navigation buttons.
	 */
	public void synchronize() {
		final Object keyField = this.fieldsKey.get(0);
		int index = 0;
		if (keyField != null) {
			final List vKeys = (List) this.table.getAllPrimaryKeys().get(keyField);
			if (vKeys != null) {
				index = vKeys.indexOf(this.form.getDataFieldValue(keyField.toString()));
			}
		}
		if (index < 0) {
			index = 0;
		}
		this.setKeys(this.table.getAllPrimaryKeys(), index);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = this.form.getTextsToTranslate();
		if (this.title != null) {
			v.add(this.title);
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		final ResourceBundle formBundle = this.form.getResourceBundle();
		if (!res.equals(formBundle)) {
			this.form.setResourceBundle(res);
		}

		try {
			if (res != null) {
				this.setTitle(res.getString(this.title));
			} else {
				this.setTitle(this.title);
			}
		} catch (final Exception e) {
			this.setTitle(this.title);
			DetailForm.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.form.setComponentLocale(l);
	}

	public void updateUI() {
		this.getRootPane().updateUI();
	}

	@Override
	public Form getForm() {
		return this.form;
	}

	protected int getRecordCount() {
		if (this.tableKeys.isEmpty()) {
			return 0;
		} else {
			final Enumeration enumTableKeys = Collections.enumeration(this.tableKeys.keySet());
			final List vKeys = (List) this.tableKeys.get(enumTableKeys.nextElement());
			return vKeys.size();
		}
	}

	protected void updateNavigationButtonState() {
		final int recordNumber = this.getRecordCount();
		if (recordNumber <= 1) {
			this.form.previousButton.setEnabled(false);
			this.form.startButton.setEnabled(false);
			this.form.endButton.setEnabled(false);
			this.form.nextButton.setEnabled(false);
			return;
		} else {
			this.form.previousButton.setEnabled(true);
			this.form.startButton.setEnabled(true);
			this.form.endButton.setEnabled(true);
			this.form.nextButton.setEnabled(true);
		}
		if (this.vectorIndex == 0) {
			this.form.previousButton.setEnabled(false);
			this.form.startButton.setEnabled(false);
			this.form.endButton.setEnabled(true);
			this.form.nextButton.setEnabled(true);
		} else if (this.vectorIndex >= (recordNumber - 1)) {
			this.form.nextButton.setEnabled(false);
			this.form.endButton.setEnabled(false);
			this.form.startButton.setEnabled(true);
			this.form.previousButton.setEnabled(true);
		}
	}

	protected void installButtonsListeners() {
		// If form is a FormExt, then it is not necessary to install the buttons
		// listeners. The FormExt uses the keys to query the other data.
		if (this.form instanceof FormExt) {
			return;
		}
		// remove the buttons event managers
		if (this.form.previousButton != null) {
			this.form.resultCountLabel.setVisible(false);
			this.form.previousButton.removeActionListener(this.form.previousButtonListener);
			this.form.nextButton.removeActionListener(this.form.nextButtonListener);
			this.form.endButton.removeActionListener(this.form.endButtonListener);
			this.form.startButton.removeActionListener(this.form.startButtonListener);
		}
		if ((this.form.previousButton == null) || (this.form.endButton == null) || (this.form.startButton == null)
				|| (this.form.nextButton == null)) {
			if (this.form.clearDataFieldButton == null) {
				this.form.clearDataFieldButton = new JButton(this.form.deleteButtonText);
			}
			try {
				this.form.nextIcon = ImageManager.getIcon(ImageManager.NEXT_2);
				this.form.previousIcon = ImageManager.getIcon(ImageManager.PREVIOUS_2);
				this.form.endIcon = ImageManager.getIcon(ImageManager.END_2);
				this.form.startIcon = ImageManager.getIcon(ImageManager.START_2);
				this.form.nextButton = new JButton(this.form.nextIcon);
				this.form.previousButton = new JButton(this.form.previousIcon);
				this.form.endButton = new JButton(this.form.endIcon);
				this.form.startButton = new JButton(this.form.startIcon);
				this.form.previousButton.setMnemonic(KeyEvent.VK_LEFT);
				this.form.nextButton.setMnemonic(KeyEvent.VK_RIGHT);
				this.form.nextButton.setMargin(new Insets(0, 0, 0, 0));
				this.form.previousButton.setMargin(new Insets(0, 0, 0, 0));
				this.form.endButton.setMargin(new Insets(0, 0, 0, 0));
				this.form.startButton.setMargin(new Insets(0, 0, 0, 0));
			} catch (final Exception e) {
				DetailForm.logger
				.debug(this.getClass().toString() + ": " + "Detail form. Icons error. " + e.getMessage(), e);
				this.form.nextButton = new JButton("->");
				this.form.previousButton = new JButton("<-");
				this.form.endButton = new JButton(">>");
				this.form.startButton = new JButton("<<");
				this.form.previousButton.setMnemonic(KeyEvent.VK_LEFT);
				this.form.nextButton.setMnemonic(KeyEvent.VK_RIGHT);
			}
			// Add buttons to the north
			this.form.buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			this.form.buttonPanel.add(this.form.startButton);
			this.form.buttonPanel.add(this.form.previousButton);
			this.form.buttonPanel.add(this.form.nextButton);
			this.form.buttonPanel.add(this.form.endButton);
			this.form.buttonPanel.add(this.form.clearDataFieldButton);
		}

		// Event listeners
		// Get the form buttons
		this.form.previousButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				if (DetailForm.this.vectorIndex > 0) {
					DetailForm.this.vectorIndex--;
				}
				DetailForm.this.form.endButton.setEnabled(true);
				DetailForm.this.form.nextButton.setEnabled(true);
				// Checks if the record has yet been queried. If the record has
				// been queried then the key exist in the data.
				// If record has more than one key all keys have to been
				// checked.
				boolean dataExist = false;
				int index = -1;
				// Get each key and check if it exist in key vector
				final Enumeration enumKeys = Collections.enumeration(DetailForm.this.data.keySet());
				final List vAux = (List) DetailForm.this.data.get(enumKeys.nextElement());
				final int numberOfRecordsInData = vAux.size();
				final int keysNumber = DetailForm.this.fieldsKey.size();
				for (int i = 0; i < numberOfRecordsInData; i++) {
					boolean bAllMatch = true;
					// For each key field check if the value matchs with the
					// record -i.
					for (int j = 0; j < keysNumber; j++) {
						final Object oKeyField = DetailForm.this.fieldsKey.get(j);
						final List vDataKeyValues = (List) DetailForm.this.data.get(oKeyField);
						final List vKeyValues = (List) DetailForm.this.tableKeys.get(oKeyField);
						final Object oKey = vKeyValues.get(DetailForm.this.vectorIndex);
						if (!vDataKeyValues.get(i).equals(oKey)) {
							bAllMatch = false;
							break;
						}
					}
					// If all keys match then stop
					if (bAllMatch) {
						dataExist = true;
						index = i;
						break;
					}
				}
				if (dataExist) {
					DetailForm.this.form.updateDataFields(DetailForm.this.data);
					DetailForm.this.form.updateDataFieldNavegationButton(index);
					DetailForm.this.updateNavigationButtonState();
					return;
				}
				final EntityResult result = DetailForm.this.query(DetailForm.this.vectorIndex);
				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					DetailForm.this.form.message(result.getMessage(), Form.ERROR_MESSAGE);
					return;
				}
				int newDataIndex = 0;
				final Enumeration keys = result.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object oValue = result.get(key);
					if (oValue instanceof List) {
						final List vValues = (List) DetailForm.this.data.get(key);
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						vValues.add(newDataIndex, ((List) oValue).get(0));
					} else {
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						((List) DetailForm.this.data.get(key)).add(newDataIndex, oValue);
					}
				}
				DetailForm.this.form.updateDataFields(DetailForm.this.data);
				// Set the current index in the form. Search in the data
				DetailForm.this.form.updateDataFieldNavegationButton(newDataIndex);
				DetailForm.this.updateNavigationButtonState();
			}
		});

		this.form.nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				if (DetailForm.this.vectorIndex < (DetailForm.this.getRecordCount() - 1)) {
					DetailForm.this.vectorIndex++;
				}
				boolean dateAlreadyExist = false;
				int existentDataIndex = -1;
				// Get each key and check in the key List if match with the
				// record keys
				final Enumeration enumKeys = Collections.enumeration(DetailForm.this.data.keySet());
				final List vAux = (List) DetailForm.this.data.get(enumKeys.nextElement());
				final int numberOfRecordsInData = vAux.size();
				final int keysNumber = DetailForm.this.fieldsKey.size();
				for (int i = 0; i < numberOfRecordsInData; i++) {
					boolean bAllMatch = true;
					// Checks for each key field if the data in index i match
					// with the record key
					// If record does not exist then query
					for (int j = 0; j < keysNumber; j++) {
						final Object oKeyField = DetailForm.this.fieldsKey.get(j);
						final List vDataKeysValues = (List) DetailForm.this.data.get(oKeyField);
						final List vKeyValues = (List) DetailForm.this.tableKeys.get(oKeyField);
						final Object oKey = vKeyValues.get(DetailForm.this.vectorIndex);
						if (!vDataKeysValues.get(i).equals(oKey)) {
							bAllMatch = false;
							break;
						}
					}
					// If all match then stop
					if (bAllMatch) {
						dateAlreadyExist = true;
						existentDataIndex = i;
						break;
					}
				}
				if (dateAlreadyExist) {
					DetailForm.this.form.updateDataFields(DetailForm.this.data);
					DetailForm.this.form.updateDataFieldNavegationButton(existentDataIndex);
					DetailForm.this.updateNavigationButtonState();
					return;
				}
				final EntityResult result = DetailForm.this.query(DetailForm.this.vectorIndex);
				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					DetailForm.this.form.message(result.getMessage(), Form.ERROR_MESSAGE);
					DetailForm.this.updateNavigationButtonState();
					return;
				}
				int newDataIndex = 0;
				final Enumeration keys = result.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object oValue = result.get(key);
					if (oValue instanceof List) {
						final List vValues = (List) DetailForm.this.data.get(key);
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						vValues.add(newDataIndex, ((List) oValue).get(0));
					} else {
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						((List) DetailForm.this.data.get(key)).add(newDataIndex, oValue);
					}
				}
				DetailForm.this.form.updateDataFields(DetailForm.this.data);
				// Set the current index in the form. Search in the data
				DetailForm.this.form.updateDataFieldNavegationButton(newDataIndex);
				DetailForm.this.updateNavigationButtonState();
			}
		});

		this.form.startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				DetailForm.this.vectorIndex = 0;
				boolean dataAlreadyExist = false;
				int existentDataIndex = -1;
				final Enumeration enumKeys = Collections.enumeration(DetailForm.this.data.keySet());
				final List vAux = (List) DetailForm.this.data.get(enumKeys.nextElement());
				final int dataRecordNumber = vAux.size();
				final int keysNumber = DetailForm.this.fieldsKey.size();
				for (int i = 0; i < dataRecordNumber; i++) {
					boolean allMatch = true;
					// Check for each key field if the data matches with the
					// record key. If keys does not exist then query
					for (int j = 0; j < keysNumber; j++) {
						final Object oKeyField = DetailForm.this.fieldsKey.get(j);
						final List oDataKeysValues = (List) DetailForm.this.data.get(oKeyField);
						final List vParentKeysValues = (List) DetailForm.this.tableKeys.get(oKeyField);
						final Object oKey = vParentKeysValues.get(DetailForm.this.vectorIndex);
						if (!oDataKeysValues.get(i).equals(oKey)) {
							allMatch = false;
							break;
						}
					}
					// If all coincide then stop
					if (allMatch) {
						dataAlreadyExist = true;
						existentDataIndex = i;
						break;
					}
				}
				if (dataAlreadyExist) {
					DetailForm.this.form.updateDataFields(DetailForm.this.data);
					DetailForm.this.form.updateDataFieldNavegationButton(existentDataIndex);
					DetailForm.this.updateNavigationButtonState();
					return;
				}
				final EntityResult result = DetailForm.this.query(DetailForm.this.vectorIndex);
				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					DetailForm.this.form.message(result.getMessage(), Form.ERROR_MESSAGE);
					return;
				}
				int newDataIndex = 0;
				final Enumeration keys = result.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object oValue = result.get(key);
					if (oValue instanceof List) {
						final List vValues = (List) DetailForm.this.data.get(key);
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						vValues.add(newDataIndex, ((List) oValue).get(0));
					} else {
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						((List) DetailForm.this.data.get(key)).add(newDataIndex, oValue);
					}
				}
				DetailForm.this.form.updateDataFields(DetailForm.this.data);
				// Set the current index in the form. Search in the data
				DetailForm.this.form.updateDataFieldNavegationButton(newDataIndex);
				DetailForm.this.updateNavigationButtonState();
			}
		});

		this.form.endButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				DetailForm.this.vectorIndex = DetailForm.this.getRecordCount() - 1;
				DetailForm.this.vectorIndex = Math.max(DetailForm.this.vectorIndex, 0);
				// Checks if the record has already been queried.
				// This is when all the record keys exist in the data
				boolean alreadyExist = false;
				int existDataIndex = -1;
				// Get each key and check it
				final Enumeration enumKeys = Collections.enumeration(DetailForm.this.data.keySet());
				final List vAux = (List) DetailForm.this.data.get(enumKeys.nextElement());
				final int recordNumberInData = vAux.size();
				final int keysNumber = DetailForm.this.fieldsKey.size();
				for (int i = 0; i < recordNumberInData; i++) {
					boolean bAllMatch = true;
					// Checks for each key field if the data in index i matchs
					// with the record keys.
					// When record does not exist then query it
					for (int j = 0; j < keysNumber; j++) {
						final Object oKeyField = DetailForm.this.fieldsKey.get(j);
						final List vDataKeyValues = (List) DetailForm.this.data.get(oKeyField);
						final List vKeyvalues = (List) DetailForm.this.tableKeys.get(oKeyField);
						final Object oKey = vKeyvalues.get(DetailForm.this.vectorIndex);
						if (!vDataKeyValues.get(i).equals(oKey)) {
							bAllMatch = false;
							break;
						}
					}
					// If all match then stop
					if (bAllMatch) {
						alreadyExist = true;
						existDataIndex = i;
						break;
					}
				}
				if (alreadyExist) {
					DetailForm.this.form.updateDataFields(DetailForm.this.data);
					DetailForm.this.form.updateDataFieldNavegationButton(existDataIndex);
					DetailForm.this.updateNavigationButtonState();
					return;
				}
				final EntityResult result = DetailForm.this.query(DetailForm.this.vectorIndex);
				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					DetailForm.this.form.message(result.getMessage(), Form.ERROR_MESSAGE);
					return;
				}
				int newDataIndex = 0;
				final Enumeration keys = result.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object oValue = result.get(key);
					if (oValue instanceof List) {
						final List vValues = (List) DetailForm.this.data.get(key);
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						vValues.add(newDataIndex, ((List) oValue).get(0));
					} else {
						newDataIndex = ((List) DetailForm.this.data.get(key)).size();
						((List) DetailForm.this.data.get(key)).add(newDataIndex, oValue);
					}
				}
				DetailForm.this.form.updateDataFields(DetailForm.this.data);
				// Set the current index in the form
				DetailForm.this.form.updateDataFieldNavegationButton(newDataIndex);
				DetailForm.this.updateNavigationButtonState();
			}
		});
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (this.getForm().getInteractionManager() != null) {
				if (this.getCheckDataBeforeClose()) {
					if ((!this.getForm().getInteractionManager().getModifiedFieldAttributes().isEmpty())
							&& this.getForm()
							.getInteractionManager()
							.getCheckModifiedDataChangeEvent()) {
						final boolean bClose = this.getForm().question(DetailForm.M_MODIFIED_DATA_CLOSE_AND_LOST_CHANGES);
						if (!bClose) {
							return;
						} else {
							// To avoid two questions to close the window
							final boolean previousValueAskOnClose = this.isAskOnClose();
							final boolean previousValueAskOnEsc = this.isAskOnEsc();
							this.setAskOnClose(false);
							this.setAskOnEsc(false);
							super.processWindowEvent(e);
							this.setAskOnClose(previousValueAskOnClose);
							this.setAskOnEsc(previousValueAskOnEsc);
							return;
						}
					}
				}
			}
		}
		super.processWindowEvent(e);
	}

	@Override
	protected void processKeyEvent(final KeyEvent e) {
		super.processKeyEvent(e);
	}

	public void changeDynamicForm(final Map dynamicFormData) {
		final Form previousForm = this.form;
		final DynamicFormManager dynamicFormManager = previousForm.getDynamicFormManager();
		if (dynamicFormManager != null) {
			final String formToShow = dynamicFormManager.getForm(dynamicFormData);
			if (!this.cacheForms.containsKey(formToShow)) {
				final IFormManager formManager = previousForm.getFormManager();
				if (formManager.getInteractionManager(formToShow) == null) {
					formManager.setInteractionManager(dynamicFormManager.getFormInteractionManagerClass(formToShow),
							formToShow);
				}
				final Form f = formManager.getFormCopy(formToShow);
				this.form = f;
				f.setDynamicFormManager(dynamicFormManager);
				f.setDetailForm(this);
				this.cacheForms.put(formToShow, f);
				this.getContentPane().add(f, formToShow);
				// Registers as form listener
				f.addDataNavigationListener(this);
				// Install the navigation buttons listeners
				// Uninstall the old form listeners and install a new ones for
				// the new form
				this.installButtonsListeners();

				if (ApplicationManager.DEBUG) {
					DetailForm.logger.debug(
							"Registered DetailForm as DataChanged listener to manage the dynamic form: " + formToShow);
				}
			}
			this.showForm(formToShow);
			if (ApplicationManager.DEBUG) {
				DetailForm.logger.debug("DetailForm: current form is : " + this.form.getArchiveName());
			}
			if ((this.form != null) && (this.form != previousForm)) {
				// WARNING: Configure the no modifiable fields and the values
				// for the fields that are not in the data list
				final Map hFieldValues = previousForm.getDataFieldValues(false);
				this.form.setDataFieldValues(hFieldValues);
				final List v = previousForm.getDataFieldAttributeList();
				for (int i = 0; i < v.size(); i++) {
					final DataComponent component = previousForm.getDataFieldReference(v.get(i).toString());
					if (component != null) {
						if (!component.isModifiable()) {
							this.form.setModifiable(v.get(i).toString(), false);
						}
					}
				}

				// Data list. Shared

				this.dataChangeEventProcessing = false;
				try {
					final Map hDataList = previousForm.totalDataList;
					if (this.form.getInteractionManager() != null) {
						this.form.getInteractionManager().setDataChangedEventProcessing(false);
					}
					if (this.form instanceof FormExt) {
						((FormExt) this.form).updateDataFields(hDataList, -1);
					} else {
						this.form.updateDataFields(hDataList);
					}
					if ((previousForm instanceof FormExt) && (this.form instanceof FormExt)) {
						((FormExt) this.form).queryRecordIndex = ObjectTools
								.clone(((FormExt) previousForm).queryRecordIndex);
					}

				} catch (final Exception ex) {
					DetailForm.logger.error(null, ex);
				} finally {
					if (this.form.getInteractionManager() != null) {
						this.form.getInteractionManager().setDataChangedEventProcessing(true);
					}
				}
				this.dataChangeEventProcessing = true;

				// Now the interaction manager status
				final InteractionManager formInteractionManager = this.form.getInteractionManager();
				if (formInteractionManager != null) {
					final int currentMode = previousForm.getInteractionManager().currentMode;
					switch (currentMode) {
					case InteractionManager.INSERT:
						formInteractionManager.setInsertMode();
						break;
					case InteractionManager.QUERY:
						formInteractionManager.setQueryMode();
						break;
					case InteractionManager.QUERYINSERT:
						formInteractionManager.setQueryInsertMode();
						break;
					case InteractionManager.UPDATE:
						formInteractionManager.setUpdateMode();
						break;
					default:
						formInteractionManager.setUpdateMode();
						break;
					}
				}

			} else {
				// If the form is the same then nothing is done
				if (ApplicationManager.DEBUG) {
					DetailForm.logger.debug("New form to show is the same that the previous one");
				}
			}
		} else if (ApplicationManager.DEBUG) {
			DetailForm.logger.debug("changeDynamicForm: Dynamico form manager has not been established");
		}
	}

	@Override
	public boolean dataWillChange(final DataNavigationEvent e) {
		return true;
	}

	@Override
	public void dataChanged(final DataNavigationEvent e) {
		if (!this.dataChangeEventProcessing) {
			return;
		}
		final Map hFormData = e.getData();
		final int viewIndex = this.table.getRowForKeys(this.valuesToTable(hFormData));
		if ((!hFormData.isEmpty()) && (viewIndex >= 0)) {
			this.table.setSelectedRow(viewIndex);
		}
		// Show the appropriate form if dynamic form manager exists
		final Form previousForm = this.form;

		if (ApplicationManager.DEBUG) {
			DetailForm.logger.debug("Previous form: " + previousForm.getArchiveName());
		}
		final DynamicFormManager dynamicFormManager = previousForm.getDynamicFormManager();
		if (dynamicFormManager != null) {
			final String formToShow = dynamicFormManager.getForm(hFormData);
			if (!this.cacheForms.containsKey(formToShow)) {
				final IFormManager formManager = previousForm.getFormManager();
				if (formManager.getInteractionManager(formToShow) == null) {
					formManager.setInteractionManager(dynamicFormManager.getFormInteractionManagerClass(formToShow),
							formToShow);
				}
				final Form f = formManager.getFormCopy(formToShow);
				this.form = f;
				f.setDynamicFormManager(dynamicFormManager);
				f.setDetailForm(this);
				this.cacheForms.put(formToShow, f);
				this.getContentPane().add(f, formToShow);
				// Register as listeners of the new form
				f.addDataNavigationListener(this);
				// Install the navigation buttons listeners
				// Uninstall the form listeners and install a new ones for the
				// new form
				this.installButtonsListeners();
				if (ApplicationManager.DEBUG) {
					DetailForm.logger
					.debug("Registering DeatailForm as DataChanged listener to manage dynamic form: " + formToShow);
				}
			}
			this.showForm(formToShow);
			if (ApplicationManager.DEBUG) {
				DetailForm.logger.debug("Current form: " + this.form.getArchiveName());
			}
			if ((this.form != null) && (this.form != previousForm)) {
				// WARNING: Configure the no modifiable fields and values for
				// fields that are not in the data list
				final Map hFieldsValues = previousForm.getDataFieldValues(false);
				this.form.setDataFieldValues(hFieldsValues);
				final List v = previousForm.getDataFieldAttributeList();
				for (int i = 0; i < v.size(); i++) {
					final DataComponent component = previousForm.getDataFieldReference(v.get(i).toString());
					if (component != null) {
						if (!component.isModifiable()) {
							this.form.setModifiable(v.get(i).toString(), false);
						}
					}
				}
				// Data list. Shared

				this.dataChangeEventProcessing = false;
				try {
					final Map hDataList = previousForm.totalDataList;
					if (this.form.getInteractionManager() != null) {
						this.form.getInteractionManager().setDataChangedEventProcessing(false);
					}

					if (this.form instanceof FormExt) {
						((FormExt) this.form).updateDataFields(hDataList, -1);
					} else {
						this.form.updateDataFields(hDataList);
					}
					if ((previousForm instanceof FormExt) && (this.form instanceof FormExt)) {
						((FormExt) this.form).queryRecordIndex = ObjectTools
								.clone(((FormExt) previousForm).queryRecordIndex);
					}

					// Select the record
					if (ApplicationManager.DEBUG) {
						DetailForm.logger.debug(previousForm.totalDataList + " : Index: " + e.getIndex());
					}
					this.form.updateDataFieldNavegationButton(e.getIndex());
				} catch (final Exception ex) {
					DetailForm.logger.error(null, ex);
				} finally {
					if (this.form.getInteractionManager() != null) {
						this.form.getInteractionManager().setDataChangedEventProcessing(true);
					}
				}
				this.dataChangeEventProcessing = true;

				// Now the interaction manager status
				final InteractionManager interactionManager = this.form.getInteractionManager();
				if (interactionManager != null) {
					final int currentMode = previousForm.getInteractionManager().currentMode;
					switch (currentMode) {
					case InteractionManager.INSERT:
						interactionManager.setInsertMode();
						break;
					case InteractionManager.QUERY:
						interactionManager.setQueryMode();
						break;
					case InteractionManager.QUERYINSERT:
						interactionManager.setQueryInsertMode();
						break;
					case InteractionManager.UPDATE:
						interactionManager.setUpdateMode();
						break;
					default:
						interactionManager.setUpdateMode();
						break;
					}
				}

			} else {
				// If the new form is the save as the previous one then nothing
				// is done
				if (ApplicationManager.DEBUG) {
					DetailForm.logger.debug("New form to show is the same as the previous one");
				}
			}

		}
	}

	public void updateDataCacheActualRecord() {
		// Query the current data record values
		final EntityResult result = this.query(this.vectorIndex);
		if (result.getCode() == EntityResult.OPERATION_WRONG) {
			this.form.message(result.getMessage(), Form.ERROR_MESSAGE);
			return;
		}
		final Enumeration keys = result.keys();
		while (keys.hasMoreElements()) {
			final Object key = keys.nextElement();
			final Object oValue = result.get(key);
			if (oValue instanceof List) {
				final List vValues = (List) this.data.get(key);
				if (this.vectorIndex < vValues.size()) {
					vValues.remove(this.vectorIndex);
					vValues.add(this.vectorIndex, ((List) oValue).get(0));
				}
			} else {
				final List vValues = (List) this.data.get(key);
				if (this.vectorIndex < vValues.size()) {
					vValues.remove(this.vectorIndex);
					vValues.add(this.vectorIndex, oValue);
				}
			}
		}
		this.form.updateDataFields(this.vectorIndex);
	}

	public void centerOnScreen() {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width / 2) - (this.getWidth() / 2), (d.height / 2) - (this.getHeight() / 2));
	}

	public void showForm(final String formName) {
		// If the form is in cache then show it, in other case load it first
		if (this.cacheForms.containsKey(formName)) {
			this.cardLayout.show(this.getContentPane(), formName);
			this.form = (Form) this.cacheForms.get(formName);
		}

	}

	protected void reload(final Form f) {
		if (f == null) {
			return;
		}
		try {
			if (this.cacheForms.containsValue(f)) {
				final IFormManager formManager = f.getFormManager();
				final String fileName = f.getArchiveName();
				this.cacheForms.remove(fileName);
				if (f.getInteractionManager() != null) {
					f.getInteractionManager().free();
					f.free();
				}
				final Container container = f.getParent();
				if (container != null) {
					Object constraints = null;
					final LayoutManager l = container.getLayout();
					if (l instanceof CardLayout) {
						constraints = f.getArchiveName();
					}
					container.remove(f);
					final Form newForm = formManager.getFormCopy(fileName);
					container.add(newForm, constraints);
					if (this.form == f) {
						this.form = newForm;
					}
					container.validate();
					container.repaint();
				}
			}
		} catch (final Exception e) {
			DetailForm.logger.error(null, e);
		}
	}

	public void setEntityName(final String name) {
		this.form.setEntityName(name);
	}

	public void resetEntityName() {
		this.form.resetEntityName();
	}

	public void setCheckDataWhenClose(final boolean v) {
		this.checkDataBeforeClose = v;
	}

	public boolean getCheckDataBeforeClose() {
		return this.checkDataBeforeClose;
	}

	@Override
	public void free() {
		FreeableUtils.clearMap(this.tableKeys);
		FreeableUtils.clearMap(this.data);
		FreeableUtils.clearMap(this.otherParentKeys);
		FreeableUtils.clearMap(this.parentkeys);
		FreeableUtils.clearMap(this.cacheForms);
		FreeableUtils.clearMap(this.codValues);
		FreeableUtils.clearMap(this.reverseCodValues);
		FreeableUtils.clearCollection(this.fieldsKey);
		if (this.form != null) {
			this.form.free();
		}
		this.parentKeyValue = null;
		this.cardLayout = null;
		this.activedFormListener = null;
		FreeableUtils.freeComponent(this.table);
		FreeableUtils.freeComponent(this.getComponents());
		this.table = null;
	}

}
