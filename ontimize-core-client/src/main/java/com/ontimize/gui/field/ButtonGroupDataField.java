package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueChangeDataComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.util.ParseUtils;

public class ButtonGroupDataField extends IdentifiedAbstractFormComponent
implements DataComponent, ValueChangeDataComponent {

	private static final Logger logger = LoggerFactory.getLogger(ButtonGroupDataField.class);

	public static final String TYPE = "buttontype";

	public static final String TOGGLE = "toggle";

	public static final String RADIO = "radio";

	public static final String CHECK = "check";

	public static final String HORIZONTAL_TEXT_POSITION = "horizontaltextposition";

	protected String valueType = ButtonGroupDataField.RADIO;

	public static final String VALUES = "values";

	public static final String TRUE_VALUE = "truevalue";

	public static final String FALSE_VALUE = "falsevalue";

	public static final String VERTICAL_ORIENTATION_VALUE = "vertical";

	public static final String HORIZONTAL_ORIENTATION_VALUE = "horizontal";

	public static final String ORIENTATION = "orientation";

	public static final int HORIZONTAL_ORIENTATION = 0;

	public static final int VERTICAL_ORIENTATION = 1;

	protected int orientation = ButtonGroupDataField.HORIZONTAL_ORIENTATION;

	public static final String BOOLEAN_TYPE = "booleantype";

	protected boolean booleanType = false;

	protected boolean modifiable = true;

	protected List changedListeners = new Vector(2);

	protected int horizontalTextPosition = SwingConstants.TRAILING;

	/**
	 * The default font color. By default, black.
	 */
	protected Color fontColor = Color.black;

	/**
	 * The variable to store the field value when <code>setValue()</code> is called.
	 */
	protected Object valueSave = null;

	/**
	 * The condition to indicate when field is required. By default, false.
	 */
	protected boolean required = false;

	/**
	 * The condition to indicate whether must be shown. By default, true.
	 */
	protected boolean show = true;

	/**
	 * The returned boolean. By default, false.
	 */
	protected boolean returnBoolean = false;

	/**
	 * The returned string. By default, false.
	 */
	protected boolean returnString = false;

	protected boolean opaque = false;

	protected int alignment = GridBagConstraints.CENTER;

	protected List valueList = null;

	protected String trueValue = null;

	protected String falseValue = null;

	public static String defaultTrueValue = "yes";

	public static String defaultFalseValue = "no";

	protected ButtonGroup buttonGroup = null;

	protected Map<String, AbstractButton>	buttons							= new Hashtable<>();

	public ButtonGroupDataField(final Map parameters) {
		this.setOpaque(false);
		this.init(parameters);
		this.createDataField();
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
	 *        <p>
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>center/left/right</td>
	 *        <td>center</td>
	 *        <td>no</td>
	 *        <td>The alignment for field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>buttontype</td>
	 *        <td>radio/toggle/check</td>
	 *        <td>radio</td>
	 *        <td>no</td>
	 *        <td>Indicates the type of button will be showed. The possibilities are ToggleButton,
	 *        RadioButtons or CheckBoxs.</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>booleantype</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates that this field supports two values. One value is represented by the true
	 *        and another by the false.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>returnboolean</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates when field value is returned like a Boolean.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>returnstring</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates when field value is returned like a string. It could be used to fill with
	 *        zeros.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>orientation</td>
	 *        <td>vertical/horizontal</td>
	 *        <td>horizontal</td>
	 *        <td>no</td>
	 *        <td>Indicates the alignment of the components.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>values</td>
	 *        <td><i>value1;value2;...</i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the values can set on the element</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>truevalue</td>
	 *        <td><i>value</i></td>
	 *        <td></td>
	 *        <td>no (only with booleantype="yes")</td>
	 *        <td>Indicates the value chosen when the field is "true"</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>falsevalue</td>
	 *        <td><i>value</i></td>
	 *        <td></td>
	 *        <td>no (only with booleantype="yes")</td>
	 *        <td>Indicates the value chosen when the field is "false"</td>
	 *        </tr>
	 *        </Table>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		try {
			super.init(parameters);
		} catch (final Exception e) {
			ButtonGroupDataField.logger.error(null, e);
			throw new IllegalArgumentException(e.getMessage());
		}

		if (parameters.containsKey(DataField.ATTR)) {
			this.attribute = parameters.get(DataField.ATTR);
		} else {
			throw new IllegalArgumentException(ButtonGroupDataField.class + ": " + DataField.ATTR + " is mandatory ");
		}

		if (parameters.containsKey(ButtonGroupDataField.TYPE)) {
			this.valueType = ParseUtils.getString(parameters.get(ButtonGroupDataField.TYPE).toString(),
					ButtonGroupDataField.RADIO);
		}

		if (parameters.containsKey(ButtonGroupDataField.BOOLEAN_TYPE)) {
			this.booleanType = ParseUtils.getBoolean(parameters.get(ButtonGroupDataField.BOOLEAN_TYPE).toString(),
					false);
		}

		if (parameters.containsKey(ButtonGroupDataField.VALUES)) {
			this.valueList = ApplicationManager.getTokensAt(parameters.get(ButtonGroupDataField.VALUES).toString(),
					";");
		}

		if (this.booleanType) {
			if ((this.valueList == null) || this.valueList.isEmpty()) {
				if (this.valueList == null) {
					this.valueList = new ArrayList();
				}
				this.valueList.add(ButtonGroupDataField.defaultTrueValue);
				this.valueList.add(ButtonGroupDataField.defaultFalseValue);
			}

			this.returnBoolean = ParseUtils.getBoolean((String) parameters.get(CheckDataField.RETURNBOOLEAN), false);
			this.returnString = ParseUtils.getBoolean((String) parameters.get(CheckDataField.RETURNSTRING), false);

			if (parameters.containsKey(ButtonGroupDataField.TRUE_VALUE)) {
				this.trueValue = ParseUtils.getString(parameters.get(ButtonGroupDataField.TRUE_VALUE).toString(),
						ButtonGroupDataField.defaultTrueValue);
				if (!this.valueList.contains(this.trueValue)) {
					if (ApplicationManager.DEBUG) {
						ButtonGroupDataField.logger
						.debug("WARNING: '" + ButtonGroupDataField.TRUE_VALUE + "' isn't in value list.");
					}
				}
			} else {
				this.trueValue = ButtonGroupDataField.defaultTrueValue;
				if (ApplicationManager.DEBUG) {
					ButtonGroupDataField.logger
					.debug("WARNING: '" + ButtonGroupDataField.TRUE_VALUE
							+ "' hasn't been setted. The default value is '" + ButtonGroupDataField.defaultTrueValue
							+ "'");
				}
			}

			if (parameters.containsKey(ButtonGroupDataField.FALSE_VALUE)) {
				this.falseValue = ParseUtils.getString(parameters.get(ButtonGroupDataField.FALSE_VALUE).toString(),
						ButtonGroupDataField.defaultFalseValue);
				if (!this.valueList.contains(this.falseValue)) {
					if (ApplicationManager.DEBUG) {
						ButtonGroupDataField.logger
						.debug("WARNING: '" + ButtonGroupDataField.FALSE_VALUE + "' isn't in value list.");
					}
				}
			} else {
				this.falseValue = ButtonGroupDataField.defaultFalseValue;
				if (ApplicationManager.DEBUG) {
					ButtonGroupDataField.logger
					.debug("WARNING: '" + ButtonGroupDataField.FALSE_VALUE
							+ "' hasn't been setted. The default value is '"
							+ ButtonGroupDataField.defaultFalseValue + "'");
				}
			}
		}

		if (parameters.containsKey(ButtonGroupDataField.ORIENTATION)) {
			if (ButtonGroupDataField.VERTICAL_ORIENTATION_VALUE
					.equalsIgnoreCase(ParseUtils.getString(parameters.get(ButtonGroupDataField.ORIENTATION).toString(),
							ButtonGroupDataField.HORIZONTAL_ORIENTATION_VALUE))) {
				this.orientation = ButtonGroupDataField.VERTICAL_ORIENTATION;
			}
		}

		if (parameters.containsKey(DataField.VISIBLE)) {
			this.show = ParseUtils.getBoolean((String) parameters.get(DataField.VISIBLE), true);
		}

		if (parameters.containsKey(DataField.REQUIRED)) {
			this.required = ParseUtils.getBoolean((String) parameters.get(DataField.REQUIRED), true);
		}

		if (parameters.containsKey(DataField.OPAQUE)) {
			this.opaque = ParseUtils.getBoolean((String) parameters.get(DataField.OPAQUE), false);
		}

		if (parameters.containsKey(ButtonGroupDataField.HORIZONTAL_TEXT_POSITION)) {
			final String value = ParseUtils.getString((String) parameters.get(ButtonGroupDataField.HORIZONTAL_TEXT_POSITION),
					"");
			if ("left".equalsIgnoreCase(value)) {
				this.horizontalTextPosition = SwingConstants.LEFT;
			}
		}

		if (parameters.containsKey(DataField.ALIGN)) {
			// Next parameter: align
			final Object oAlign = parameters.get(DataField.ALIGN);
			if (oAlign != null) {
				if (oAlign.equals(DataField.RIGHT)) {
					this.alignment = GridBagConstraints.NORTHEAST;
				} else {
					if (oAlign.equals(DataField.LEFT)) {
						this.alignment = GridBagConstraints.NORTHWEST;
					} else {
						this.alignment = GridBagConstraints.NORTH;
					}
				}
			}
		}

		if (parameters.containsKey(DataField.LABELFONTCOLOR)) {
			this.fontColor = ParseUtils.getColor((String) parameters.get(DataField.LABELFONTCOLOR), this.fontColor);
		}
	}

	public ButtonGroup getButtonGroup() {
		return this.buttonGroup;
	}

	protected void createDataField() {
		if ((this.valueList == null) || this.valueList.isEmpty()) {
			throw new IllegalArgumentException(
					ButtonGroupDataField.class + ": " + ButtonGroupDataField.VALUES + " must be established ");
		}
		this.buttonGroup = new CustomButtonGroup();

		for (int i = 0; i < this.valueList.size(); i++) {
			final String currentValue = this.valueList.get(i).toString();
			final AbstractButton currentButton = this.createButton(currentValue);
			currentButton.setForeground(this.fontColor);
			this.buttonGroup.add(currentButton);
			this.buttons.put(currentValue, currentButton);
		}

		this.setLayout(new GridBagLayout());
		this.layoutButtons();
		DataField.changeOpacity(this, this.opaque);
	}

	protected SelectedButtonListener selectedListener = new SelectedButtonListener();

	protected AbstractButton createButton(final String text) {
		AbstractButton button = null;
		if (ButtonGroupDataField.RADIO.equalsIgnoreCase(this.valueType)) {
			button = new JRadioButton(text);
		} else if (ButtonGroupDataField.CHECK.equalsIgnoreCase(this.valueType)) {
			button = new JCheckBox(text);
		} else {
			button = new JToggleButton(text);
		}

		button.setHorizontalTextPosition(this.horizontalTextPosition);
		button.addActionListener(this.selectedListener);
		return button;
	}

	protected void layoutButtons() {
		final Enumeration buttonEnumeration = this.buttonGroup.getElements();
		while (buttonEnumeration.hasMoreElements()) {
			final AbstractButton currentButton = (AbstractButton) buttonEnumeration.nextElement();
			if (ButtonGroupDataField.HORIZONTAL_ORIENTATION == this.orientation) {
				this.add(currentButton,
						new GridBagConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 1, 0, 0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			} else {
				this.add(currentButton,
						new GridBagConstraints(GridBagConstraints.REMAINDER, GridBagConstraints.RELATIVE, 1, 1, 0, 0,
								GridBagConstraints.CENTER,
								GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			}
		}
	}

	@Override
	public void deleteData() {
		final Object oPreviousValue = this.getValue();
		((CustomButtonGroup) this.buttonGroup).clearSelection();
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public int getSQLDataType() {
		if (this.booleanType) {
			if (this.returnBoolean) {
				return java.sql.Types.BIT;
			} else if (this.returnString) {
				return java.sql.Types.VARCHAR;
			}
			return java.sql.Types.INTEGER;
		} else {
			return java.sql.Types.VARCHAR;
		}
	}

	protected String getValueFromButtonModel(final ButtonModel model) {
		if (model == null) {
			return null;
		}
		for (final Entry<String, AbstractButton> entry : buttons.entrySet()) {
			final String id = entry.getKey();
			final AbstractButton currentButton = entry.getValue();
			if (model.equals(currentButton.getModel())) {
				return id;
			}
		}
		return null;
	}

	@Override
	public Object getValue() {
		final ButtonModel selectedModel = this.buttonGroup.getSelection();
		if (selectedModel == null) {
			return null;
		}
		final String currentValue = this.getValueFromButtonModel(selectedModel);
		if (currentValue == null) {
			return null;
		}

		if (this.booleanType) {
			if (this.trueValue.equalsIgnoreCase(currentValue)) {
				if (this.returnString) {
					return CheckDataField.YES;
				}
				if (this.returnBoolean) {
					return Boolean.TRUE;
				}
				return CheckDataField.UNO;
			} else {
				if (this.returnString) {
					return CheckDataField.NO;
				}
				if (this.returnBoolean) {
					return Boolean.FALSE;
				}
				return CheckDataField.ZERO;
			}
		} else {
			return currentValue;
		}
	}

	@Override
	public boolean isEmpty() {
		final ButtonModel selectedModel = this.buttonGroup.getSelection();
		if (selectedModel == null) {
			return true;
		}
		final String currentValue = this.getValueFromButtonModel(selectedModel);
		if (currentValue == null) {
			return true;
		}
		return false;
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
				ButtonGroupDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				ButtonGroupDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				ButtonGroupDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isRequired() {
		return this.required;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	@Override
	public void setRequired(final boolean required) {
		this.required = required;
	}

	@Override
	public void setValue(final Object value) {
		final Object previousValue = this.getValue();

		if ((value != null) && !(value instanceof NullValue)) {
			if (this.booleanType) {
				boolean booleanValue = false;
				if (value instanceof Number) {
					if (((Number) value).intValue() != 0) {
						booleanValue = true;
					}
				} else if (value instanceof Boolean) {
					booleanValue = ((Boolean) value).booleanValue();
				} else if (value instanceof String) {
					if (value.equals(CheckDataField.YES)) {
						booleanValue = true;
					}
				}
				AbstractButton button = null;
				if (booleanValue) {
					button = this.buttons.get(this.trueValue);
				} else {
					button = this.buttons.get(this.falseValue);
				}

				this.buttonGroup.setSelected(button.getModel(), true);
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else {
				if (this.buttons.containsKey(value)) {
					final AbstractButton button = this.buttons.get(value);
					this.buttonGroup.setSelected(button.getModel(), true);
					this.valueSave = this.getValue();
					this.fireValueChanged(this.valueSave, previousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				} else {
					this.deleteData();
				}
			}
		} else {
			this.deleteData();
		}
	}

	@Override
	public void addValueChangeListener(final ValueChangeListener arg0) {
		this.changedListeners.add(arg0);
	}

	public void fireValueChanged(final Object newValue, final Object oldValue, final int type) {
		for (int i = 0; i < this.changedListeners.size(); i++) {
			((ValueChangeListener) this.changedListeners.get(i))
			.valueChanged(new ValueEvent(this, newValue, oldValue, type));
		}
	}

	@Override
	public void removeValueChangeListener(final ValueChangeListener arg0) {
		this.changedListeners.remove(arg0);
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		for (final Entry<String, AbstractButton> entry : buttons.entrySet()) {
			final String key = entry.getKey();
			final AbstractButton currentButton = entry.getValue();
			currentButton.setText(ApplicationManager.getTranslation(key.toString(), res));
		}
	}

	public class SelectedButtonListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final AbstractButton button = (AbstractButton) e.getSource();
			final String value = ButtonGroupDataField.this.getValueFromButtonModel(button.getModel());
			String oldvalue = null;
			if (ButtonGroupDataField.this.buttonGroup instanceof CustomButtonGroup) {
				oldvalue = ButtonGroupDataField.this.getValueFromButtonModel(
						((CustomButtonGroup) ButtonGroupDataField.this.buttonGroup).getOldSelection());
			} else {
				oldvalue = value;
			}
			ButtonGroupDataField.this.fireValueChanged(value, oldvalue, ValueEvent.USER_CHANGE);
		}

	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				final Enumeration enumeration = this.buttonGroup.getElements();
				while (enumeration.hasMoreElements()) {
					final AbstractButton button = (AbstractButton) enumeration.nextElement();
					button.setEnabled(enabled);
				}
			} else {
				this.setEnabled(false);
			}
		} else {
			if (!enabled) {
				final Enumeration enumeration = this.buttonGroup.getElements();
				while (enumeration.hasMoreElements()) {
					final AbstractButton button = (AbstractButton) enumeration.nextElement();
					button.setEnabled(enabled);
				}
			}
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, this.alignment,
					GridBagConstraints.NONE, new Insets(AbstractFormComponent.defaultTopMargin,
							AbstractFormComponent.defaultLeftMargin, AbstractFormComponent.defaultBottomMargin,
							AbstractFormComponent.defaultRightMargin),
					0, 0);
		} else {
			return null;
		}
	}

	public static class CustomButtonGroup extends ButtonGroup {

		protected ButtonModel oldSelection = null;

		public ButtonModel getOldSelection() {
			return this.oldSelection;
		}

		@Override
		public void setSelected(final ButtonModel m, final boolean b) {
			if (b && (m != null) && (m != this.getSelection())) {
				this.oldSelection = this.getSelection();
			}
			super.setSelected(m, b);
		}

		@Override
		public void clearSelection() {
			if (this.getSelection() != null) {
				this.oldSelection = this.getSelection();
				if (this.oldSelection == null) {
					return;
				}
				// TODO In java 1.4 doesn't support deselected.
				// clearSelection method is supported since 1.6
				final Enumeration buttons = this.getElements();
				while (buttons.hasMoreElements()) {
					final AbstractButton button = (AbstractButton) buttons.nextElement();
					if (this.oldSelection.equals(button.getModel())) {
						this.remove(button);
						this.add(button);
					}
				}
			}
		}

		@Override
		public void add(final AbstractButton b) {
			if (b == null) {
				return;
			}

			if (b.isSelected()) {
				this.oldSelection = this.getSelection();
			}
			super.add(b);
		}

		@Override
		public void remove(final AbstractButton b) {
			if (b == null) {
				return;
			}
			if (b.getModel().equals(this.getSelection())) {
				this.oldSelection = this.getSelection();
			}
			super.remove(b);
			b.setSelected(false);
		}

	}

}
