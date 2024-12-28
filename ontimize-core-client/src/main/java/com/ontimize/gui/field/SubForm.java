package com.ontimize.gui.field;

import java.awt.Frame;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.FormBuilder;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.CreateForms;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.IFormComponentManager;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * This class allows to put a form into another form like a typical component. This subform is
 * wrapped in a TableAttribute structure.
 * <p>
 *
 * @author Imatia Innovation
 */

public class SubForm extends IdentifiedAbstractFormComponent
implements CreateForms, OpenDialog, AccessForm, Freeable, ReferenceComponent, DataComponent, SubFormComponent,
IFormComponentManager {

	private static final Logger logger = LoggerFactory.getLogger(SubForm.class);

	protected String formName = null;

	protected Form form = null;

	protected Frame parentFrame = null;

	protected EntityReferenceLocator locator = null;

	protected FormBuilder formBuilder = null;

	protected List parentKeys = new Vector();

	protected IFormManager formManager = null;

	public SubForm(final Map params) throws Exception {
		super();
		this.init(params);
	}

	@Override
	public void free() {
		super.free();
		FreeableUtils.freeComponent(form);
		form = null;
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

	@Override
	public Object getAttribute() {
		final TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setEntityAndAttributes(this.form.getEntityName(), this.form.getDataFieldAttributeList());
		tableAttribute.setRecordNumberToInitiallyDownload(-1);
		tableAttribute.setKeysParentkeysOtherkeys(this.form.getKeys(), this.parentKeys);
		return tableAttribute;
	}

	@Override
	public void setFormBuilder(final FormBuilder constructor) {
		this.formBuilder = constructor;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param params the <code>Hashtable</code> with parameters. Adds the next parameters:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>form</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The complete path to the subform. For example,
	 *        <i>"com/ontimize/form/form.xml"</i></td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map params) {
		final String formName = (String) params.get("form");
		if (formName == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " : form parameter needs to be specified");
		}
		this.formName = formName;

		// Parameter : parentkey or 'parentkeys'
		final Object parentkeys = params.get("parentkeys");
		if (parentkeys != null) {
			final StringTokenizer st = new StringTokenizer(parentkeys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.parentKeys.add(st.nextToken());
			}
		}
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator referenceLocator) {
		this.locator = referenceLocator;
	}

	@Override
	public String getLabelComponentText() {
		return "";
	}

	@Override
	public Object getValue() {
		return this.form.getDataList();
	}

	@Override
	public void setValue(final Object value) {
		final boolean checkModifiedDataChangeEvent = this.form.getInteractionManager().getCheckModifiedDataChangeEvent();
		final boolean processDataChangeEvent = this.form.getInteractionManager().getDataChangedEventProcessing();
		this.form.getInteractionManager().setCheckModifiedDataChangeEvent(false);
		this.form.getInteractionManager().setDataChangedEventProcessing(false);

		if (value instanceof Map) {
			this.form.updateDataFields((Map) value);
			this.setUpdateMode();
		} else {
			this.deleteData();
		}

		this.form.getInteractionManager().setDataChangedEventProcessing(processDataChangeEvent);
		this.form.getInteractionManager().setCheckModifiedDataChangeEvent(checkModifiedDataChangeEvent);
	}

	@Override
	public void deleteData() {
		this.form.updateDataFields(null);
		this.setQueryInsertMode();
		if ((this.parentKeys != null) && (this.parentKeys.size() > 0)) {
			for (int i = 0, j = this.parentKeys.size(); i < j; i++) {
				final String parentk = (String) this.parentKeys.get(i);
				this.form.deleteDataField(parentk);
			}
		}
	}

	/**
	 * Method to set the value in subform parentkeys fields
	 */
	public void setParentKeys() {
		if ((this.parentKeys != null) && (this.parentKeys.size() > 0)) {
			for (int i = 0, j = this.parentKeys.size(); i < j; i++) {
				final String parentk = (String) this.parentKeys.get(i);
				this.form.setDataFieldValue(parentk, this.parentForm.getDataFieldValue(parentk));
			}
		}
	}

	@Override
	public boolean isEmpty() {
		return (this.form.getDataList() == null) || this.form.getDataList().isEmpty();
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void setModifiable(final boolean modificable) {
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public boolean isModified() {
		if (this.form != null) {
			return this.form.checkModifiedData();
		}
		return false;
	}

	@Override
	public void setRequired(final boolean required) {
	}

	@Override
	public void setFormManager(final IFormManager formManager) {
		this.formManager = formManager;
		if (this.formManager != null) {
			this.createForm();
		}
	}

	protected void createForm() {
		if ((this.formName != null) && (this.formName.length() > 0)) {
			this.form = this.formManager.getFormCopy(this.formName, this);

			if (this.parentKeys.size() == 0) {
				this.parentKeys = this.form.getParentKeys();
			} else {
				this.form.setParentKeys(this.parentKeys);
			}
			if ((this.parentKeys != null) && (this.parentKeys.size() > 0)) {
				for (int i = 0, j = this.parentKeys.size(); i < j; i++) {
					final String parentk = (String) this.parentKeys.get(i);
					final DataComponent parentkeyDataComponent = this.form.getDataFieldReference(parentk);
					if (parentkeyDataComponent != null) {
						parentkeyDataComponent.setModifiable(false);
					} else {
						if (ApplicationManager.DEBUG) {
							SubForm.logger.debug(this.getClass().getName() + ": component with attr: " + parentk
									+ " used like parentkey not included in parent form");
						}
					}
				}
			}

			if (this.form.clearDataFieldButton != null) {
				this.form.clearDataFieldButton.setMnemonic(-1);
			}
		}
	}

	@Override
	public Form getForm() {
		return this.form;
	}

	/**
	 * Disable all form fields and navigation buttons
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.form.setEnabled(enabled);
		if (!enabled) {
			this.form.disableDataFields();
			this.form.disableButtons();
			this.form.clearDataFieldButton.setEnabled(false);
			this.form.startButton.setEnabled(false);
			this.form.endButton.setEnabled(false);
			this.form.previousButton.setEnabled(false);
			this.form.nextButton.setEnabled(false);
			this.form.tableButton.setEnabled(false);
		}
	}

	public void setUpdateMode() {
		final boolean b = this.form.getInteractionManager().getDefaultActiveFocus();
		try {
			// Without disabling the default focus it is impossible to move
			// across the tree records because the subform get the focus
			this.form.getInteractionManager().setDefaultFocusEnabled(false);
			this.setEnabled(true);
			this.form.getInteractionManager().setUpdateMode();
		} finally {
			this.form.getInteractionManager().setDefaultFocusEnabled(b);
		}
	}

	public void setQueryInsertMode() {
		this.setEnabled(false);
		this.form.getInteractionManager().setQueryInsertMode();
	}

	public void setInsertMode() {
		this.form.getInteractionManager().setInsertMode();
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (this.form != null) {
			this.form.setResourceBundle(res);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		super.setComponentLocale(l);
		if (this.form != null) {
			this.form.setComponentLocale(l);
		}
	}

}
