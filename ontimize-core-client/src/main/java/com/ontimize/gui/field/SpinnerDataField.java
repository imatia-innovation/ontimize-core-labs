package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.IntegerDocument;
import com.ontimize.gui.field.document.RealDocument;
import com.ontimize.gui.field.spinner.CustomDateEditor;
import com.ontimize.gui.field.spinner.CustomListEditor;
import com.ontimize.gui.field.spinner.CustomNumberEditor;
import com.ontimize.gui.field.spinner.CustomSpinnerDateModel;
import com.ontimize.gui.field.spinner.CustomSpinnerDateModel.SpinnerDateDocument;
import com.ontimize.gui.field.spinner.CustomSpinnerListModel;
import com.ontimize.gui.field.spinner.CustomSpinnerNumberModel;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.ParseUtils;

/**
 * Field similar to <code>JSpinner</code> that allows to modify value (increasing/decreasing) with
 * two selectors. <br>
 * <br>
 * XML parameters for configuration of this field can be found here: <br>
 * {@link #init(Map)} <br>
 *
 * @author Imatia Innovation SL
 * @since 5.2059EN
 */

public class SpinnerDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(SpinnerDataField.class);

	public static String TYPE = "type";

	public static String TYPE_NUMBER = "number";

	public static String TYPE_TEXT = "text";

	public static String TYPE_DATE = "date";

	public static String VALUES = "values";

	public static String MIN = "min";

	public static String MAX = "max";

	public static String STEP = "step";

	public static String INITIAL = "initial";

	public static String NUMBER = "number";

	public static String NUMBERCLASS = "numberclass";

	public static String CALENDAR = "calendar";

	public static String FORMAT = "format";

	protected String type = SpinnerDataField.TYPE_TEXT;

	protected int numberclass = ParseTools.INTEGER_;

	protected String format = null;

	protected Object values = null;

	protected static class EJSpinner extends JSpinner implements Internationalization {

		public EJSpinner() {
			super();
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale l) {

			final DefaultEditor editor = (DefaultEditor) this.getEditor();
			if (editor instanceof Internationalization) {
				((Internationalization) editor).setComponentLocale(l);
			}
		}

		@Override
		public void setResourceBundle(final ResourceBundle resourceBundle) {

		}

	}

	protected class InnerDocumentListener implements DocumentListener {

		/**
		 * The inner listener enabled condition. By default, true.
		 */
		protected boolean innerListenerEnabled = true;

		Object innerValue = null;

		/**
		 * Sets enabled the inner listener.
		 * <p>
		 * @param enabled the condition
		 */
		public void setInnerListenerEnabled(final boolean enabled) {
			this.innerListenerEnabled = enabled;
		}

		/**
		 * Gets the value of field.
		 * <p>
		 * @return the value
		 */
		protected Object getValueField() {
			return SpinnerDataField.this.getValue();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getValueField();
				if (this.isEqualInnerValue(oNewValue)) {
					return;
				}
				SpinnerDataField.this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getValueField();
				if (this.isEqualInnerValue(oNewValue)) {
					return;
				}
				SpinnerDataField.this.fireValueChanged(oNewValue, this.getInnerValue(), ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {

		}

		/**
		 * Gets the inner value.
		 * <p>
		 * @return the inner value
		 */
		public Object getInnerValue() {
			return this.innerValue;
		}

		/**
		 * Sets the inner value.
		 * <p>
		 * @param o the inner value
		 */
		public void setInnerValue(final Object o) {
			this.innerValue = o;
		}

		/**
		 * Compares the inner value with parameter.
		 * <p>
		 * @param newValue the value to compare
		 * @return the condition
		 */
		protected boolean isEqualInnerValue(final Object newValue) {
			if ((newValue == null) && (this.innerValue == null)) {
				return true;
			}
			if ((newValue == null) || (this.innerValue == null)) {
				return false;
			}
			return this.innerValue.equals(newValue);
		}

	}

	/**
	 * This class implements a inner listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerListener implements ChangeListener {

		/**
		 * The condition about inner listener activation. By default, true
		 */
		protected boolean innerListenerEnabled = true;

		/**
		 * Stores the inner value. By default, null.
		 */
		protected Object storeInnerValue = null;

		/**
		 * Sets enable the inner listener in function of condition.
		 * <p>
		 * @param eanbled the condition of activation
		 */
		public void setInnerListenerEnabled(final boolean enabled) {
			this.innerListenerEnabled = enabled;
		}

		/**
		 * Gets the inner value.
		 * <p>
		 * @return the stored inner value
		 */
		public Object getInnerValue() {
			return this.storeInnerValue;
		}

		/**
		 * Sets the inner value.
		 * <p>
		 * @param o the object to set
		 */
		public void setInnerValue(final Object o) {
			this.storeInnerValue = o;
		}

		/**
		 * Compares the inner value with parameter.
		 * <p>
		 * @param newValue the value to compare
		 * @return the condition
		 */
		protected boolean isEqualInnerValue(final Object newValue) {
			if ((newValue == null) && (this.storeInnerValue == null)) {
				return true;
			}
			if ((newValue == null) || (this.storeInnerValue == null)) {
				return false;
			}
			return this.storeInnerValue.equals(newValue);
		}

		@Override
		public void stateChanged(final ChangeEvent e) {
			if (this.innerListenerEnabled) {
				SpinnerDataField.this.fireValueChanged(SpinnerDataField.this.getValue(), this.storeInnerValue,
						ValueEvent.USER_CHANGE);
				this.storeInnerValue = SpinnerDataField.this.getValue();
			}

		}

	};

	/**
	 * The inner listener instance.
	 */
	protected InnerListener innerListener = new InnerListener();

	protected JCheckBoxMenuItem includeMenu = new JCheckBoxMenuItem(CheckDataField.INCLUDE_MENU_KEY, true);

	protected InnerDocumentListener innerDocumentListener = null;

	public SpinnerDataField(final Map parameters) {
		this.init(parameters);
	}

	/**
	 * Method that reads the xml parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Allows the next parameters:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>max</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Maximum value for spinner when not specified 'values' and type is number.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>min</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Minimum value for spinner when not specified 'values' and type is number.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>numberclass</td>
	 *        <td>Integer/Float/Long/Double</td>
	 *        <td>Integer</td>
	 *        <td>no</td>
	 *        <td>Number class that uses spinner in data model. It used when type is number</td>
	 *        </tr>
	 *        <tr>
	 *        <td>step</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Step used to increase/decrease value in spinner when type is number.
	 *        </tr>
	 *        <tr>
	 *        <td>type</td>
	 *        <td>text/number</td>
	 *        <td>text</td>
	 *        <td>no</td>
	 *        <td>The type of data allowed for this field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>values</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Used to set a fixed values to spinner. Available for type text and number.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {

		this.createDataField();
		super.init(parameters);
		this.installInnerListener();

		final Object otype = parameters.get(SpinnerDataField.TYPE);
		if (otype != null) {
			if (otype.equals(SpinnerDataField.TYPE_TEXT)) {
				this.type = (String) otype;
			} else if (otype.equals(SpinnerDataField.TYPE_DATE)) {
				this.type = (String) otype;
			} else {
				this.type = SpinnerDataField.TYPE_NUMBER; // by default
			}
		}

		final Object oinitial = parameters.get(SpinnerDataField.INITIAL);
		final Object omin = parameters.get(SpinnerDataField.MIN);
		final Object omax = parameters.get(SpinnerDataField.MAX);
		final Object ostep = parameters.get(SpinnerDataField.STEP);

		final Object onumberclass = parameters.get(SpinnerDataField.NUMBERCLASS);
		if (onumberclass != null) {
			this.numberclass = ParseUtils.getIntTypeForName((String) onumberclass);
		}

		final Object oformat = parameters.get(SpinnerDataField.FORMAT);
		if (oformat != null) {
			this.format = oformat.toString();
		}

		this.values = parameters.get(SpinnerDataField.VALUES);
		final List vecValues = new Vector();
		if (this.values != null) {
			final StringTokenizer st = new StringTokenizer(this.values.toString(), ";");
			while (st.hasMoreTokens()) {
				final Object token = st.nextToken();
				Object elem = null;
				if (this.type.equals(SpinnerDataField.TYPE_NUMBER)) {
					elem = ParseUtils.getValueForClassType(token, this.numberclass);
				} else if (this.type.equals(SpinnerDataField.TYPE_DATE)) {
					// TODO Unimplemented.
				} else if (this.type.equals(SpinnerDataField.TYPE_TEXT)) {
					elem = token;
				}
				vecValues.add(elem);
			}
		}

		if (this.dataField instanceof JSpinner) {
			// SetModel.
			if (this.values != null) {
				// Sets SpinnerListModel.
				final CustomSpinnerListModel spinnerListModel = new CustomSpinnerListModel(vecValues);
				((JSpinner) this.dataField).setModel(spinnerListModel);
				final CustomListEditor editor = new CustomListEditor((JSpinner) this.dataField);
				((JSpinner) this.dataField).setEditor(editor);
				if (this.type.equals(SpinnerDataField.TYPE_NUMBER)) {
					final CustomNumberEditor.NumberFormatterFactory factory = new CustomNumberEditor.NumberFormatterFactory();
					factory.setValueClass(ParseUtils.getClassType(this.numberclass));
					editor.getTextField().setFormatterFactory(factory);
				}
			} else if (this.type.equals(SpinnerDataField.TYPE_NUMBER) && (this.values == null)) {
				// Sets SpinnerNumberModel.
				Object initialValue = null;
				Object minValue = null;
				Object maxValue = null;
				Object stepValue = null;
				if (omin != null) {
					minValue = ParseUtils.getValueForClassType(omin, this.numberclass);
				}
				if (omax != null) {
					maxValue = ParseUtils.getValueForClassType(omax, this.numberclass);
				}
				if (ostep != null) {
					stepValue = ParseUtils.getValueForClassType(ostep, this.numberclass);
				} else {
					stepValue = ParseUtils.getValueForClassType("1", this.numberclass);
				}
				if (oinitial != null) {
					initialValue = ParseUtils.getValueForClassType(oinitial, this.numberclass);
				} else {
					initialValue = ParseUtils.getValueForClassType("0", this.numberclass);
				}

				final CustomSpinnerNumberModel spinnerNumberModel = new CustomSpinnerNumberModel(this.numberclass,
						(Number) initialValue, (Comparable) minValue, (Comparable) maxValue,
						(Number) stepValue);
				((JSpinner) this.dataField).setModel(spinnerNumberModel);
				final CustomNumberEditor customNumberEditor = new CustomNumberEditor((JSpinner) this.dataField);
				((JSpinner) this.dataField).setEditor(customNumberEditor);

				final CustomNumberEditor.NumberFormatterFactory factory = new CustomNumberEditor.NumberFormatterFactory();
				factory.setValueClass(ParseUtils.getClassType(this.numberclass));
				customNumberEditor.getTextField().setFormatterFactory(factory);

				if ((this.numberclass == ParseTools.DOUBLE_) || (this.numberclass == ParseTools.FLOAT_)) {
					customNumberEditor.setDocument(new RealDocument());
				} else {
					customNumberEditor.setDocument(new IntegerDocument());
				}
			} else if (this.type.equals(SpinnerDataField.TYPE_DATE) && (this.values == null)) {
				// Sets SpinnerDateModel.
				CustomSpinnerDateModel spinnerDateModel = null;
				if (ostep != null) {
					final int calendarField = ParseUtils.getCalendarField(ostep.toString());
					spinnerDateModel = new CustomSpinnerDateModel(null, null, calendarField);
				} else {
					spinnerDateModel = new CustomSpinnerDateModel();
				}

				((JSpinner) this.dataField).setModel(spinnerDateModel);
				final CustomDateEditor editor = new CustomDateEditor((JSpinner) this.dataField);
				((JSpinner) this.dataField).setEditor(editor);

				SpinnerDateDocument dataDoc = null;
				if (this.format != null) {
					dataDoc = new SpinnerDateDocument(this.format);
				} else {
					dataDoc = new SpinnerDateDocument();
				}
				((DefaultEditor) ((JSpinner) this.dataField).getEditor()).getTextField().setDocument(dataDoc);
				// TODO Insert the formatter in dateeditor.

			} else {
				// If no model is set, the default model is Integer
				// SpinnerNumberModel.
				final SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel();
				((JSpinner) this.dataField).setModel(spinnerNumberModel);
			}
		}

		this.installInnerDocumentListener();

	}

	protected void setInnerListenerEnabled(final boolean enabled) {
		this.innerListener.setInnerListenerEnabled(enabled);
		if (this.innerDocumentListener != null) {
			this.innerDocumentListener.setInnerListenerEnabled(enabled);
		}
	}

	/**
	 * Installs the inner listener.
	 */
	protected void installInnerListener() {
		if (this.dataField != null) {
			((JSpinner) this.dataField).addChangeListener(this.innerListener);
		}
	}

	protected void installInnerDocumentListener() {
		if (this.dataField != null) {
			final JComponent editor = ((JSpinner) this.dataField).getEditor();
			final Document doc = ((DefaultEditor) editor).getTextField().getDocument();
			if (doc != null) {
				if (this.innerDocumentListener == null) {
					this.innerDocumentListener = new InnerDocumentListener();
				}
				doc.addDocumentListener(this.innerDocumentListener);
			}
		}
	}

	@Override
	public void deleteData() {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((JSpinner) this.dataField).setValue(null);
		this.valueSave = this.getValue();
		this.innerListener.setInnerValue(this.valueSave);
		if (this.innerDocumentListener != null) {
			this.innerDocumentListener.setInnerValue(this.valueSave);
		}
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public int getSQLDataType() {
		return ParseUtils.getSQLType(this.numberclass, ParseTools.INTEGER_);
	}

	@Override
	public boolean isModified() {
		return super.isModified();
	}

	@Override
	public boolean isEmpty() {
		final Object obj = ((JSpinner) this.dataField).getValue();
		if (obj == null) {
			return true;
		} else {
			if (obj instanceof String) {
				if (((String) obj).length() == 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return ((JSpinner) this.dataField).getValue();
	}

	@Override
	public void setValue(final Object value) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((JSpinner) this.dataField).setValue(value);
		this.valueSave = this.getValue();
		this.innerListener.setInnerValue(this.valueSave);
		if (this.innerDocumentListener != null) {
			this.innerDocumentListener.setInnerValue(this.valueSave);
		}
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (!enabled && (this instanceof AdvancedDataComponent)) {
			((AdvancedDataComponent) this).setAdvancedQueryMode(false);
		}
		if (!enabled) {
			if (this.conditions != null) {
				this.conditions.setVisible(false);
			}
			this.advancedQueryMode = false;
		}
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				if (!this.advancedQueryMode) {
					this.dataField.setEnabled(enabled);
				} else {
					if (this.includeMenu.isSelected()) {
						this.dataField.setEnabled(true);
					} else {
						this.dataField.setEnabled(false);
					}
				}
				this.enabled = enabled;
				this.updateBackgroundColor();
			}
		} else {
			this.dataField.setEnabled(enabled);
			this.enabled = enabled;
		}
	}

	protected void createDataField() {
		final EJSpinner spinner = new EJSpinner() {

			@Override
			public Dimension getPreferredSize() {
				// Changed to addapt size to values stored and consider buttons
				// width
				final int buttonsWidth = this.getComponent(0).getWidth();
				final char charMaxWidth = ((JSpinner) SpinnerDataField.this.dataField)
						.getModel() instanceof SpinnerNumberModel ? '0' : 'W';
				return new Dimension(
						(SpinnerDataField.this.fieldSize * this.getFontMetrics(this.getFont()).charWidth(charMaxWidth))
						+ buttonsWidth,
						super.getPreferredSize().height);
			}
		};
		this.dataField = spinner;
	}

	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			// this.popupMenu = new ExtendedJPopupMenu();
			super.createPopupMenu();
			this.popupMenu.addSeparator();
			String sMenuText = CheckDataField.INCLUDE_MENU_KEY;
			try {
				if (this.resources != null) {
					sMenuText = this.resources.getString(CheckDataField.INCLUDE_MENU_KEY);
				}
			} catch (final Exception e) {
				SpinnerDataField.logger.trace(null, e);
			}

			this.includeMenu.setText(sMenuText);
			this.popupMenu.add(this.includeMenu);
			this.includeMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (!SpinnerDataField.this.includeMenu.isSelected()) {
						// Update the checkbox
						((AbstractButton) SpinnerDataField.this.dataField).setEnabled(false);
					} else {
						((AbstractButton) SpinnerDataField.this.dataField).setEnabled(true);
					}
				}
			});
		}
	}

	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		// if(this.isEnabled()==false) return;
		if (this.popupMenu == null) {
			this.createPopupMenu();
		}
		if (this.popupMenu != null) {
			this.configurePopupMenuHelp();
			this.includeMenu.setVisible(this.advancedQueryMode && this.isEnabled());
			this.popupMenu.show(c, x, y);
		}
	}

	public void setAdvancedQueryMode(final boolean enabled) {
		this.advancedQueryMode = enabled;
		if (!this.advancedQueryMode) {
			this.dataField.setEnabled(this.isEnabled());
		} else {
			// By default it is not included in the query
			this.includeMenu.setSelected(false);
			this.dataField.setEnabled(false);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// Object value = getValue();
		super.setComponentLocale(l);
		// Document d = ((DefaultEditor)((JSpinner)
		// this.dataField).getEditor()).getTextField().getDocument();
		// if (d != null && d instanceof Internationalization) {
		// ((Internationalization) d).setComponentLocale(l);
		// }
		// boolean events = this.fireValueEvents;
		// this.fireValueEvents = false;
		// if(value!=null){
		// this.setValue(value);
		// }
		// this.fireValueEvents = events;

	}

}
