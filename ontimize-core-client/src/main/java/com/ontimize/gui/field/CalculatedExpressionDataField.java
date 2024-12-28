package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Types;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.JCalculatedExpression;
import com.ontimize.util.swing.selectablelist.SelectableItem;
import com.ontimize.util.swing.selectablelist.SelectableItemListCellRenderer;
import com.ontimize.util.swing.selectablelist.SelectableItemMouseListener;
import com.ontimize.util.swing.text.ComponentTextPane.CustomLabel;

/**
 *
 * <p>
 *
 * @author Imatia Innovation
 */

public class CalculatedExpressionDataField extends JCalculatedExpression
implements FormComponent, AccessForm, IdentifiedElement, DataComponent, ValueChangeDataComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(CalculatedExpressionDataField.class);

	/**
	 * Attribute key to assign the available fields of the component.
	 */
	public static String AVAILABLEFIELDS_ATTR = "availablefields";

	/**
	 * Attribute key to assign the required fields of the component.
	 */
	public static String REQUIREDFIELDS_ATTR = "requiredfields";

	/**
	 * The condition to indicate whether must be shown. By default, true.
	 */
	protected boolean show = true;

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The variable to store the field value when <code>setValue()</code> is called.
	 */
	protected Object valueSave = null;

	/**
	 * The condition to indicate when field is active. By default, true.
	 */
	protected boolean enabled = true;

	protected boolean isEnabled = true;

	/**
	 * The condition to indicate when field is modifiable. By default, true.
	 */
	protected boolean modifiable = true;

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	/**
	 * The reference for border text. By default, null.
	 */
	protected String borderText = null;

	/**
	 * The default background color. By default, white.
	 */
	protected Color backgroundColor = Color.white;

	/**
	 * List that contains the name of the available fields to use into the editor of expressions.
	 */
	protected List availableFields;

	/**
	 * The List instance for a value listener.
	 */
	protected List valueListener = new Vector();

	/**
	 * The condition to activate field events. By default, yes.
	 */
	protected boolean fireValueEvents = true;

	/**
	 * The reference to inner listener. By default, null.
	 */
	protected InnerDocumentListener innerListener = null;

	/**
	 * The reference for field focus listener. By default, null.
	 */
	protected FieldFocusListener fieldlistenerFocus = null;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The reference to visible permission in form. By default, null.
	 */
	protected FormPermission permissionVisible = null;

	/**
	 * The reference to activate permission. By default, null.
	 */
	protected FormPermission permissionActivate = null;

	/**
	 * The reference for resources file. By default, null.
	 */
	protected ResourceBundle resources = null;

	/**
	 * The renderer for elements of the list available fields.
	 */
	protected SelectableItemListCellRenderer selItemRenderer;

	public CalculatedExpressionDataField(final Map parameters) {
		super();
		try {
			this.init(parameters);
		} catch (final Exception e) {
			CalculatedExpressionDataField.logger.error(null, e);
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Inits parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>visible</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The visibility condition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>required</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The required condition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgcolor</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The background color.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>border</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover,
	 *        it is also allowed a border defined in #BorderManager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border for datafield</td>
	 *        </tr>
	 *        <tr>
	 *        <td>enabled</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The field activation.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>availablefields</td>
	 *        <td><i>field1;field2;...;fieldn</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The list of available fields to the mathematical expression.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>requiredfields</td>
	 *        <td><i>field1;field2;...;fieldn</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The list of required fields to the mathematical expression.</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) throws Exception {

		// parameter: attribute 'attr'
		final Object oAttribute = parameters.get(DataField.ATTR);
		if (oAttribute != null) {
			this.attribute = oAttribute.toString();
		}

		final Object visible = parameters.get(DataField.VISIBLE);
		if (visible != null) {
			if (visible.equals(DataField.NO_STR)) {
				this.show = false;
			} else {
				this.show = true;
			}
		}

		final Object enabled = parameters.get(DataField.ENABLED);
		if ((enabled != null) && (enabled instanceof String)) {
			final String sEnabled = enabled.toString();
			if (sEnabled.equalsIgnoreCase("no")) {
				this.isEnabled = false;
				this.setEnabled(false);
			}
		}

		Object border = parameters.get(DataField.BORDER);
		if (border == null) {
			border = DataField.DEFAULT_BORDER;
		}
		if (border != null) {
			if (border.equals(DataField.NONE)) {
				this.borderText = border.toString();
				this.setBorder(new EmptyBorder(0, 0, 0, 0));
				this.setOpaque(false);
			} else if (border.equals(DataField.RAISED)) {
				this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals(DataField.LOWERED)) {
				this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				try {
					final Color c = ColorConstants.colorNameToColor(border.toString());
					this.setBorder(new LineBorder(c));
				} catch (final Exception e) {
					CalculatedExpressionDataField.logger.trace(null, e);
					this.setBorder(ParseUtils.getBorder((String) border, this.getBorder()));
				}
			}
		}

		final Object bgcolor = parameters.get(DataField.BGCOLOR);
		if (bgcolor != null) {
			try {
				this.backgroundColor = ColorConstants.parseColor(bgcolor.toString());
				this.setBackground(this.backgroundColor);
			} catch (final Exception e) {
				CalculatedExpressionDataField.logger
				.error(this.getClass().toString() + " Error 'bgcolor' parameter:" + e.getMessage(), e);
			}
		}

		final Object oAvailableFields = parameters.get(CalculatedExpressionDataField.AVAILABLEFIELDS_ATTR);
		if (oAvailableFields != null) {
			this.availableFields = ApplicationManager.getTokensAt((String) oAvailableFields, ";");
			this.setAvailableFields(this.availableFields);
		}

		final Object oRequiredFields = parameters.get(CalculatedExpressionDataField.REQUIREDFIELDS_ATTR);
		if (oRequiredFields != null) {
			this.setRequiredFields(ApplicationManager.getTokensAt((String) oRequiredFields, ";"));
		}

		try {
			this.valueSave = this.getValue();
		} catch (final Exception e) {
			CalculatedExpressionDataField.logger.trace(null, e);
			this.valueSave = null;
		}

		this.installInnerListener();
		this.fieldlistenerFocus = this.createFocusListener();
		this.installFocusListener();

	}

	/**
	 * In addition to super method, this method adds renderers and listeners to JList component.
	 */
	protected JList createList(final List values) {
		final JList list = super.createList(values, true);
		this.selItemRenderer = new SelectableItemListCellRenderer(this.resources);
		list.setCellRenderer(this.selItemRenderer);
		list.addMouseListener(new SelectableItemMouseListener());
		return list;
	}

	/**
	 * This method obtains the translation of the element passed as parameter if it exists, if not, it
	 * returns the same value.
	 */
	@Override
	protected String getFieldTranslation(final String field) {
		if (this.resources != null) {
			return ApplicationManager.getTranslation(field, this.resources);
		}
		return super.getFieldTranslation(field);
	}

	/**
	 * Adds a inner listener for document.
	 */
	protected void installInnerListener() {
		if (this.expressionPane != null) {
			final Document d = this.expressionPane.getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new InnerDocumentListener();
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

	/**
	 * Creates a field focus listener.
	 * <p>
	 * @return the field focus listener
	 */
	protected FieldFocusListener createFocusListener() {
		return new FieldFocusListener();
	}

	/**
	 * Adds the focus listener to the data field.
	 */
	protected void installFocusListener() {
		if (this.expressionPane != null) {
			this.expressionPane.addFocusListener(this.fieldlistenerFocus);
		}
	}

	/**
	 * Enables the field to insert data.
	 * <p>
	 * @param enabled the condition to set enable
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				super.setEnabled(enabled);
				this.enabled = enabled;
			} else {
				this.setEnabled(false);
			}
		} else {
			if (!enabled && this.hasFocus()) {
				this.transferFocus();
			}
			super.setEnabled(enabled);
			this.enabled = enabled;
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (permission) {
				super.setVisible(visible);
			}
		} else {
			super.setVisible(visible);
		}
	}

	/**
	 * The method to delete data for component.
	 */
	@Override
	public void deleteData() {
		try {
			this.enableInnerListener(false);
			final Object oPreviousValue = this.getValue();
			super.setExpression(null);
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.error(null, e);
			}
		} finally {
			this.enableInnerListener(true);
		}
	}

	/**
	 * Gets the value of the component
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return super.getExpression();
	}

	/**
	 * Sets the value of the component.
	 */
	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.enableInnerListener(false);
		try {
			final Object oPreviousValue = this.getValue();
			if (value != null) {
				super.setExpression(value);
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else {
				this.deleteData();
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.error(null, e);
			}
		} finally {
			this.enableInnerListener(true);
		}
	}

	@Override
	public List getAvailableFields() {
		if (this.availableFields != null) {
			return this.availableFields;
		}
		return null;
	}

	/**
	 * Sets the list of fields that are going to be allowed to introduce into the mathematical
	 * expression of the editor from a String separated by semicolon.
	 * @param value
	 */
	public void setAvailableFields(final String value) {
		if (value != null) {
			this.availableFields = ApplicationManager.getTokensAt(value, ";");
			this.setAvailableFields(this.availableFields);
		}
	}

	@Override
	public void setAvailableFields(final List values) {
		if (values != null) {
			if (this.availableFields == null) {
				this.availableFields = new Vector();
			}

			this.availableFields = this.getSelectableItemList(values);
			super.setAvailableFields(this.availableFields);
		}
	}

	/**
	 * This method returns a list of <code>{@link SelectableItem}</code> objects from a specified list
	 * of field names. Each {@link SelectableItem} includes the name of the available field and a check
	 * field used to configure the component.
	 * @param fields List of available fields names.
	 * @return a list of <code>SelectableItem</code> objects.
	 */
	protected List getSelectableItemList(final List fields) {
		final List list = new Vector();
		if ((fields != null) && !fields.isEmpty()) {
			for (int i = 0; i < fields.size(); i++) {
				final Object currentObj = fields.get(i);
				final SelectableItem item = new SelectableItem(currentObj.toString());
				list.add(item);
			}
		}
		return list;
	}

	/**
	 * This method returns the fields(source fields) that are used into the 'mathematical expression' of
	 * the editor.
	 * @return a <code>List</code> with the source fields.
	 */
	public List getSourceFields() {
		final List sourceFields = new Vector();
		final List labelComp = this.expressionPane.getLabelComponents();
		if ((labelComp != null) && !labelComp.isEmpty()) {
			for (int i = 0; i < labelComp.size(); i++) {
				final Object currentObj = labelComp.get(i);
				if (currentObj instanceof CustomLabel) {
					final Object text = ((CustomLabel) currentObj).getOriginalText();
					if (text != null) {
						sourceFields.add(text);
					}
				}
			}
		}

		return sourceFields;
	}

	/**
	 * This method returns a list of fields that are required into the configuration of the
	 * 'expression'.
	 * @return a <code>List</code> with the required values.
	 */
	public List getRequiredFields() {
		final List requiredFields = new Vector();
		if (this.availableFieldsList != null) {
			final List listData = ((CustomListModel) this.availableFieldsList.getModel()).getListData();
			if ((listData != null) && !listData.isEmpty()) {
				for (int i = 0; i < listData.size(); i++) {
					final Object currentObj = listData.get(i);
					if (currentObj instanceof SelectableItem) {
						if (((SelectableItem) currentObj).isSelected()) {
							requiredFields.add(((SelectableItem) currentObj).getText());
						}
					}
				}
			}

		}
		return requiredFields;
	}

	/**
	 * This method sets which fields are required into the available fields list. The list of required
	 * fields is used into the configuration of the 'expression'.
	 * @param requiredFields List with the fields to set required.
	 */
	public void setRequiredFields(final List requiredFields) {
		if ((this.availableFieldsList != null) && (requiredFields != null)) {
			final List listData = ((CustomListModel) this.availableFieldsList.getModel()).getListData();
			if ((listData != null) && !listData.isEmpty()) {
				for (int i = 0; i < listData.size(); i++) {
					final Object currentObj = listData.get(i);
					if (currentObj instanceof SelectableItem) {
						final String currentTextItem = ((SelectableItem) currentObj).getText();
						if (requiredFields.contains(currentTextItem)) {
							((SelectableItem) currentObj).setSelected(true);
						} else {
							((SelectableItem) currentObj).setSelected(true);
						}
					}
				}
			}
		}
	}

	/**
	 * This method check if the current expression of the editor is well defined. If it is well defined
	 * the method returns <i>true</i>, if not, the method returns <i>false</i>.
	 * @return
	 */
	public boolean isCorrectExpression() {
		try {
			final String expr = (String) this.getExpression();
			super.checkExpression(expr);
			return true;
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.debug("Expression Error: " + e.getMessage(), e);
			}
			return false;
		}
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public int getSQLDataType() {
		return Types.LONGVARCHAR;
	}

	@Override
	public boolean isEmpty() {
		if (this.expressionPane != null) {
			if (this.expressionPane.getText().length() > 0) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isHidden() {
		return !this.show;
	}

	@Override
	public boolean isModifiable() {
		return this.modifiable;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.debug("Component: " + this.attribute
						+ " Modified: Previous value = " + this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.debug("Component: " + this.attribute
						+ " Modified: Previous value = " + this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				CalculatedExpressionDataField.logger.debug("Component: " + this.attribute
						+ " Modified: Previous value = " + this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns true when field is enabled. False in other case.
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	@Override
	public void setRequired(final boolean required) {
		// Don`t do nothing
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resources = resourceBundle;
		if (this.selItemRenderer != null) {
			this.selItemRenderer.setResourceBundle(resourceBundle);
		}
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	/**
	 * Gets the parent form.
	 * <p>
	 * @return the parent form
	 */
	public Form getParentForm() {
		return this.parentForm;
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			final Component[] cs = new Component[1];
			cs[0] = this;
			ClientSecurityManager.registerSecuredElement(this, cs);
		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}
	}

	/**
	 * Return the visible permission condition.
	 * <p>
	 * @return the visible condition
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionVisible == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionVisible = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.permissionVisible != null) {
					manager.checkPermission(this.permissionVisible);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					CalculatedExpressionDataField.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					CalculatedExpressionDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Check enabled permission.
	 * <p>
	 * @return the enabled permission
	 */
	protected boolean checkEnabledPermission() {
		if (!this.isEnabled) {
			return false;
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionActivate == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionActivate = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.permissionActivate != null) {
					manager.checkPermission(this.permissionActivate);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					CalculatedExpressionDataField.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					CalculatedExpressionDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void addValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && !this.valueListener.contains(l)) {
			this.valueListener.add(l);
		}
	}

	@Override
	public void removeValueChangeListener(final ValueChangeListener l) {
		if ((l != null) && this.valueListener.contains(l)) {
			this.valueListener.remove(l);
		}
	}

	/**
	 * Detects fire value events.
	 * <p>
	 * @param newValue the new value
	 * @param oldValue the previous value
	 * @param type the type of value event
	 */
	protected void fireValueChanged(final Object newValue, final Object oldValue, final int type) {
		if (!this.fireValueEvents) {
			return;
		}
		for (int i = 0; i < this.valueListener.size(); i++) {
			((ValueChangeListener) this.valueListener.get(i))
			.valueChanged(new ValueEvent(this, newValue, oldValue, type));
		}
	}

	/**
	 * This method allows to enable/disable the ValueEvent events notifier. So, inner events will be not
	 * triggered when inner listener is disabled. It is advisable disabling the listener only when
	 * content is inserted by program.
	 * <p>
	 * @param enable the condition to enable/disable the inner listener.
	 */
	protected void enableInnerListener(final boolean enable) {
		if (this.innerListener != null) {
			this.innerListener.setInnerListenerEnabled(enable);
		}
	}

	/**
	 * This class implements a inner listener for document.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerDocumentListener implements DocumentListener {

		/**
		 * The inner listener activation condition. By default, true.
		 */
		protected boolean innerListenerEnabled = true;

		/**
		 * Enables the inner listener.
		 * <p>
		 * @param act the condition to activation
		 */
		public void setInnerListenerEnabled(final boolean act) {
			this.innerListenerEnabled = act;
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				CalculatedExpressionDataField.this.fireValueChanged(CalculatedExpressionDataField.this.getValue(),
						CalculatedExpressionDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				CalculatedExpressionDataField.this.fireValueChanged(CalculatedExpressionDataField.this.getValue(),
						CalculatedExpressionDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

	}

	/**
	 * This class implements a field focus listener.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class FieldFocusListener extends FocusAdapter {

		Color sourceBackgroundColor = null;

		/**
		 * Sets the background color.
		 * <p>
		 * @param color the color for background
		 */
		public void setSourceBackgroundColor(final Color color) {
			this.sourceBackgroundColor = color;
		}

		@Override
		public void focusLost(final FocusEvent e) {

			final Object source = e.getSource();
			if (source instanceof Component) {
				final Component c = (Component) source;
				if (c.isEnabled() && (this.sourceBackgroundColor != null)) {
					c.setBackground(this.sourceBackgroundColor);
				}
			}
		}

		@Override
		public void focusGained(final FocusEvent e) {
			final Object source = e.getSource();
			if (source instanceof Component) {
				final Component c = (Component) source;
				if (c.isEnabled()) {
					if (this.sourceBackgroundColor == null) {
						this.sourceBackgroundColor = c.getBackground();
					}
					c.setBackground(DataField.FOCUS_BACKGROUNDCOLOR);
				}
			}
		}

	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
