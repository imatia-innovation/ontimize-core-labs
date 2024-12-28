package com.ontimize.gui;

import java.awt.Window;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ObjectTools;

public abstract class BaseDetailForm extends JPanel implements IDetailForm {

	private static final Logger logger = LoggerFactory.getLogger(BaseDetailForm.class);

	protected Map tableKeys = null;

	protected List fieldsKey = null;

	protected int vectorIndex = 0;

	protected Form form = null;

	protected Map parentkeys = null;

	protected Table table = null;

	protected String title = null;

	protected Map data = new Hashtable();

	protected Map codValues = null;

	protected Map reverseCodValues = null;

	@Override
	public void setComponentLocale(final Locale l) {
		this.form.setComponentLocale(l);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.form.setResourceBundle(resourceBundle);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = this.form.getTextsToTranslate();
		return v;
	}

	@Override
	public Form getForm() {
		return this.form;
	}

	@Override
	public Table getTable() {
		return this.table;
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

	public String getTableFieldName(final String name) {
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
	public void setQueryInsertMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setQueryInsertMode();
	}

	@Override
	public void setUpdateMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setUpdateMode();
	}

	@Override
	public void setInsertMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setInsertMode();
	}

	@Override
	public void setQueryMode() {
		this.updateFieldsParentkeys();
		this.form.getInteractionManager().setQueryMode();
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

	@Override
	public void setParentKeyValues(final Map parentKeyValues) {
		this.parentkeys = this.valuesToForm(parentKeyValues);
		this.updateFieldsParentkeys();
	}

	/**
	 * This method sets the keys in the table records.<br>
	 * This keys are used to query the record values
	 * @param tableKeys
	 * @param index
	 */
	@Override
	public void setKeys(final Map tableKeys, final int index) {
		this.tableKeys = this.valuesToForm(tableKeys);
		// Reset the index of the selected element
		this.vectorIndex = 0;

		// If there are more than one record
		int recordNumber = 0;
		if (tableKeys.isEmpty()) {
			this.form.disableButtons();
			this.form.disableDataFields();
		} else {
			final Enumeration enumTableKeys = Collections.enumeration(this.tableKeys.keySet());
			final List vKeys = (List) this.tableKeys.get(enumTableKeys.nextElement());
			recordNumber = vKeys.size();
		}

		if (index < recordNumber) {
			this.vectorIndex = index;
		}
		if (!tableKeys.isEmpty()) {
			if (!(this.form instanceof FormExt)) {
				if (this.vectorIndex >= 0) {
					this.data = EntityResultUtils.toMap(this.query(this.vectorIndex));
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

							if (BaseDetailForm.logger.isDebugEnabled()) {
								final Window w = SwingUtilities.getWindowAncestor(this);
								MessageDialog.showErrorMessage(w,
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
						if (BaseDetailForm.logger.isDebugEnabled()) {
							final Window window = SwingUtilities.getWindowAncestor(this);
							MessageDialog.showErrorMessage(window,
									"DEBUG: DetailForm: Map with the detail form keys contains less elements for the key "
											+ oKeyField + " than the selected index " + index);
						}
						return new EntityResultMapImpl();
					}

					if (vKeyValues.get(index) == null) {
						if (BaseDetailForm.logger.isDebugEnabled()) {
							final Window window = SwingUtilities.getWindowAncestor(this);
							MessageDialog.showErrorMessage(window,
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
			BaseDetailForm.logger
			.error("DetailForm: Error in query. Check the parameters, the xml and the entity configuration", e);
			if (ApplicationManager.DEBUG) {
				BaseDetailForm.logger.error(null, e);
			}
			return new EntityResultMapImpl();
		}
	}

	@Override
	public void free() {
		this.tableKeys = null;
		this.fieldsKey = null;
		this.parentkeys = null;
		this.table = null;
		this.data = null;
		this.codValues = null;
		this.reverseCodValues = null;
		FreeableUtils.freeComponent(form);
		this.form = null;
	}

}
