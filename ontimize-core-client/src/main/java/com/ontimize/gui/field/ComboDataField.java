package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.TipScroll;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.util.FormatPattern;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a scrollable list. It is an abstract class to use for all data components
 * showed in a scrollable list.
 * <p>
 *
 * @author Imatia Innovation
 */
public abstract class ComboDataField extends DataField implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ComboDataField.class);

	public static final String TRANSLATION = "translation";

	/**
	 * Format pattern string key to convert the data field content value.
	 *
	 * @see ReferenceExtDataField#DATEFORMAT
	 * @since Ontimize 5.2059EN
	 */
	public static final String FORMAT = "format";

	/**
	 * Date format pattern string key to convert the data field dates values.
	 *
	 * @see ReferenceExtDataField#FORMAT
	 * @since Ontimize 5.2059EN
	 */
	public static final String DATEFORMAT = "dateformat";

	/**
	 * The reference to locale. By default, null.
	 */
	protected Locale localeComponente = null;

	/**
	 * Reference to a vector. By default, null.
	 */
	protected List values = null;

	/**
	 * Pattern to format the field contents. Null if the <code>format</code> parameter is missing. Also
	 * wrappers the content of the <code>dateformat <code> parameter.
	 *
	 * @since Ontimize 5.2059EN
	 */
	protected FormatPattern formatPattern = null;

	/**
	 * The interface that defines a <code>KeySelectionManager</code> to selection in the combo.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public interface ExtKeySelectionManager extends JComboBox.KeySelectionManager {

		/**
		 * Gets the combo index.
		 * <p>
		 * @param text the text to search
		 * @param model the model of combo
		 * @return the index of combo
		 */
		public int getComboIndex(String text, ComboBoxModel model);

	}

	/**
	 * This class extends the editor component used for JComboBox components.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class CustomComboBoxEditor implements ComboBoxEditor {

		/**
		 * The reference for a combo box editor. By default, null.
		 */
		protected ComboBoxEditor editor = null;

		/**
		 * The class constructor. Fix the <code>editor</code> value.
		 * <p>
		 * @param ed the editor value
		 */
		public CustomComboBoxEditor(final ComboBoxEditor ed) {
			this.editor = ed;
		}

		@Override
		public Component getEditorComponent() {
			return this.editor.getEditorComponent();
		}

		@Override
		public void setItem(final Object anObject) {
			if (CustomComboBoxModel.NULL_SELECTION.equals(anObject)) {
				this.editor.setItem(null);
			} else {
				this.editor.setItem(anObject);
			}
		}

		@Override
		public Object getItem() {
			return this.editor.getItem();
		}

		@Override
		public void selectAll() {
			this.editor.selectAll();
		}

		@Override
		public void addActionListener(final ActionListener l) {
			this.editor.addActionListener(l);
		}

		@Override
		public void removeActionListener(final ActionListener l) {
			this.editor.removeActionListener(l);
		}

	}

	/**
	 * This class implements a custom combo box.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class CustomComboBox extends JComboBox implements Freeable {

		/**
		 * The text tip instance.
		 */
		protected TipScroll textTip = null;// new TipScroll(true);

		/**
		 * The query text instance.
		 */
		protected StringBuilder queryText = new StringBuilder();

		/**
		 * The size of combo. By default, 10.
		 */
		protected int size = 10;

		/**
		 * Indicates whether size is fit to contain. By default, false.
		 */
		boolean fitSizeToContained = false;

		/**
		 * Class constructor.
		 */
		public CustomComboBox() {
			this(10);
			// this.editor= (new CustomComboBoxEditor(this.getEditor()));
		}

		@Override
		public void setBackground(final Color c) {
			super.setBackground(c);
			if (this.getEditor() != null) {
				if (this.getEditor().getEditorComponent() != null) {
					this.getEditor().getEditorComponent().setBackground(c);
				}
			}
		}

		@Override
		public void configureEditor(final ComboBoxEditor anEditor, final Object anItem) {
			if (CustomComboBoxModel.NULL_SELECTION.equals(anItem)) {
				anEditor.setItem(null);
			} else {
				anEditor.setItem(anItem);
			}
		}

		@Override
		public void free() {
			this.getTipScroll().dispose();
			this.textTip = null;
		}

		/**
		 * Class constructor when is possible specifies the size.
		 * <p>
		 * @param size the size
		 */
		public CustomComboBox(final int size) {
			ToolTipManager.sharedInstance().registerComponent(this);
			this.size = size;
			this.setRequestFocusEnabled(true);
			this.addFocusListener(new FocusAdapter() {

				@Override
				public void focusLost(final FocusEvent e) {
					CustomComboBox.this.resetTipScroll();
				}
			});
			this.addKeyListener(new KeyAdapter() {

				@Override
				public void keyPressed(final KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						CustomComboBox.this.resetTipScroll();
					} else if (e.getKeyCode() == 8) {
						if (CustomComboBox.this.queryText.length() > 0) {
							CustomComboBox.this.queryText.delete(CustomComboBox.this.queryText.length() - 1,
									CustomComboBox.this.queryText.length());
							CustomComboBox.this.getTipScroll()
							.show(CustomComboBox.this, 0,
									(CustomComboBox.this.getTipScroll()
											.getFontMetrics(CustomComboBox.this.getTipScroll().getFont())
											.getHeight() / 2)
									+ CustomComboBox.this.getHeight() + 2,
									CustomComboBox.this.queryText.toString());
						}
					}
				}
			});
		}

		protected TipScroll getTipScroll() {
			if (this.textTip == null) {
				final Window w = SwingUtilities.getWindowAncestor(this);
				this.textTip = new TipScroll(w, true);
			}
			return this.textTip;
		}

		protected void resetTipScroll() {
			this.getTipScroll().setVisible(false);
			if ((this.queryText != null) && (this.queryText.length() > 0)) {
				this.queryText.delete(0, this.queryText.length());
			}
		}

		/**
		 * Sets the size of component.
		 * <p>
		 * @param size the size
		 */
		public void setSizeComponent(final int size) {
			this.size = size;
		}

		/**
		 * Fits the size to content.
		 * <p>
		 * @param a the condition to fit.
		 */
		public void setFitSizeToContents(final boolean a) {
			this.fitSizeToContained = a;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();

			if (this.fitSizeToContained) {
				return d;
			} else {
				d = new Dimension(this.size * this.getFontMetrics(this.getFont()).charWidth('A'), d.height);
			}
			return d;
		}

		@Override
		public String getToolTipText(final MouseEvent e) {
			if (this.getWidth() < CustomComboBox.calculatePreferredTextWidth(this)) {
				final Component c = this.getRendererComponent();
				if (c instanceof JLabel) {
					return ((JLabel) c).getText();
				} else {
					return this.getToolTipText();
				}
			} else {
				return this.getToolTipText();
			}
		}

		private static JList auxList = new JList();

		private Component getRendererComponent() {
			final Component comp = this.getRenderer()
					.getListCellRendererComponent(CustomComboBox.auxList, this.getSelectedItem(), this.getSelectedIndex(),
							true, false);
			return comp;
		}

		/**
		 * Calculates the preferred text width.
		 * <p>
		 * @param c the combo box to calculate
		 * @return the preferred text width
		 */
		protected static int calculatePreferredTextWidth(final JComboBox c) {
			try {
				final Component comp = c.getRenderer()
						.getListCellRendererComponent(CustomComboBox.auxList, c.getSelectedItem(), c.getSelectedIndex(),
								true, false);
				final int auxWidth = comp.getPreferredSize().width;
				return auxWidth + 4;
			} catch (final Exception ex) {
				ComboDataField.logger.trace(null, ex);
				return -1;
			}
		}

		@Override
		public void updateUI() {
			super.updateUI();
		}

		@Override
		public void setSelectedIndex(final int i) {
			if ((this.getModel() instanceof CustomComboBoxModel)
					&& ((CustomComboBoxModel) this.getModel()).isNullSelection() && (i < 0)) {
				super.setSelectedIndex(0);
			} else {
				super.setSelectedIndex(i);
			}
		}

		@Override
		public boolean selectWithKeyChar(final char keyChar) {
			// Text:
			int index;
			if (this.keySelectionManager == null) {
				this.keySelectionManager = this.createDefaultKeySelectionManager();
			}
			if (this.keySelectionManager instanceof ExtKeySelectionManager) {
				return this.checkKeySelectionInstanceExtKeySelectionManager(keyChar);
			} else {
				index = this.keySelectionManager.selectionForKey(keyChar, this.getModel());
				if (index != -1) {
					this.setSelectedIndex(index);
					return true;
				} else {
					return false;
				}
			}
		}

		/**
		 * Method used to reduce the complexity of {@link #selectWithKeyChar(char)}
		 * @param keyChar
		 * @return
		 */
		protected boolean checkKeySelectionInstanceExtKeySelectionManager(final char keyChar) {
			int index;
			if (Character.isLetterOrDigit(keyChar) || (Character.isSpaceChar(keyChar) && (this.queryText.length() > 0))
					|| this.isValidChar(keyChar)) {
				this.queryText.append(keyChar);
				this.getTipScroll()
				.show(this, 0,
						(this.getTipScroll().getFontMetrics(this.getTipScroll().getFont()).getHeight() / 2)
						+ this.getHeight() + 2,
						this.queryText.toString());
				index = ((ExtKeySelectionManager) this.keySelectionManager).getComboIndex(this.queryText.toString(),
						this.getModel());
				if (index != -1) {
					final long t = System.currentTimeMillis();
					this.setSelectedIndex(index);
					ComboDataField.logger.debug(this.getClass().toString() + " -> " + (System.currentTimeMillis() - t));
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		protected boolean isValidChar(final char keyChar) {
			if ((keyChar == '.') || (keyChar == '-') || (keyChar == '+') || (keyChar == '*') || (keyChar == '_')
					|| (keyChar == '<') || (keyChar == '>') || (keyChar == '¡')
					|| (keyChar == '!') || (keyChar == '¿') || (keyChar == '?') || (keyChar == '\'') || (keyChar == '$')
					|| (keyChar == '%') || (keyChar == '&') || (keyChar == '/')
					|| (keyChar == '(') || (keyChar == ')') || (keyChar == '=') || (keyChar == '#')
					|| (keyChar == '@')) {
				return true;
			}
			return false;
		}

	};

	/**
	 * This class implements the default custom combo box renderer.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class DefaultCustomComboBoxRenderer extends DefaultListCellRenderer {

		/**
		 * The class constructor. It is empty.
		 */
		public DefaultCustomComboBoxRenderer() {
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean hasFocus) {
			if ((value == null) || value.equals(CustomComboBoxModel.NULL_SELECTION)) {
				if (hasFocus) {
					return super.getListCellRendererComponent(list, " ", index, false, hasFocus);
				} else {
					return super.getListCellRendererComponent(list, " ", index, isSelected, hasFocus);
				}
			} else {
				return super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
			}
		}

	};

	/**
	 * This class implements a inner listener for field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerListener implements ActionListener {

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

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (this.innerListenerEnabled) {
				ComboDataField.this.fireValueChanged(ComboDataField.this.getValue(), this.storeInnerValue,
						ValueEvent.USER_CHANGE);
				this.storeInnerValue = ComboDataField.this.getValue();
			}
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

	};

	/**
	 * The inner listener instance.
	 */
	protected InnerListener innerListener = new InnerListener();

	/**
	 * The key for NULL selection.
	 */
	public static final String NULL_SELECTION = "nullselection";

	protected boolean nullSelection = true;

	/**
	 * Class constructor. Creates the component, sets the selected index and adds a renderer.
	 */
	public ComboDataField() {
		this.dataField = new JComboBox() {

			@Override
			public void setSelectedIndex(final int i) {
				if ((this.getModel() instanceof CustomComboBoxModel)
						&& ((CustomComboBoxModel) this.getModel()).isNullSelection() && (i < 0)) {
					super.setSelectedIndex(0);
				} else {
					super.setSelectedIndex(i);
				}
			}

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				return new Dimension(ComboDataField.this.fieldSize * this.getFontMetrics(this.getFont()).charWidth('A'),
						d.height);
			}
		};
		((JComboBox) this.dataField).setRenderer(new DefaultCustomComboBoxRenderer());
	}

	/**
	 * Class constructor with combo parameter. Only sets the renderer and fix the <code>dataField</code>
	 * variable.
	 * <p>
	 * @param combo the combo
	 */
	public ComboDataField(final JComboBox combo) {
		this.dataField = combo;
		((JComboBox) this.dataField).setRenderer(new DefaultCustomComboBoxRenderer());
	}

	/**
	 * Installs the inner listener and calls to super for initialize parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 *
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *
	 *        <tr>
	 *        <td>dateformat</td>
	 *        <td><i>A
	 *        <a href= "http://java.sun.com/docs/books/tutorial/i18n/format/simpleDateFormat.html" >Java
	 *        date pattern<a></i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Java Date pattern to use in <code>format</code> parameter.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>format</td>
	 *        <td>message;column_1;...;column_N</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The field shows the translation with the given message and columns.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>translation</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The format message is translated by bundle</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>nullselection</td>
	 *        <td><i></td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td><i>Since 5.2057EN-0.3</i>. By default, combo contains an empty value in first position
	 *        of deployed list. With this parameter is possible to delete this value and show only field
	 *        values.</td>
	 *        </tr>
	 *        </Table>
	 *
	 */

	@Override
	public void init(final Map parameters) {

		this.installInnerListener();
		// Use the method in super class to initialize basic parameters
		super.init(parameters);

		if (parameters.containsKey(ComboDataField.NULL_SELECTION)) {
			final String path = (String) parameters.get(ComboDataField.NULL_SELECTION);
			this.nullSelection = ParseUtils.getBoolean(path, true);
		}

		// Now initialize this object
		if (this.dataField instanceof CustomComboBox) {
			((CustomComboBox) this.dataField).setSizeComponent(this.fieldSize);
		}

		final boolean translation = ParseUtils.getBoolean((String) parameters.get(ComboDataField.TRANSLATION), false);

		final Object oFormat = parameters.get(ComboDataField.FORMAT);
		if ((oFormat != null) && (oFormat instanceof String)) {
			this.formatPattern = new FormatPattern(oFormat.toString(), translation);

			final Object oDateFormat = parameters.get(ComboDataField.DATEFORMAT);
			if ((oDateFormat != null) && (oDateFormat instanceof String)) {
				final String dateFormat = oDateFormat.toString();
				this.formatPattern.setDateFormat(dateFormat);
			}
		}
	}

	@Override
	public void updateUI() {
		final Color c = (Color) UIManager.getDefaults().get("ComboBox.disabledForeground");
		if (c != null) {
			UIManager.getDefaults().put("ComboBox.disabledForeground", c.darker());
		}
		UIManager.getDefaults().put("ComboBox.disabledBackground", DataComponent.VERY_LIGHT_GRAY);
		super.updateUI();
	}

	@Override
	public boolean isEmpty() {
		final ComboBoxModel m = ((JComboBox) this.dataField).getModel();
		if ((m instanceof CustomComboBoxModel) && ((CustomComboBoxModel) m).isNullSelection()) {
			if (((JComboBox) this.dataField).getSelectedIndex() > 0) {
				return false;
			} else {
				return true;
			}
		} else {
			if (((JComboBox) this.dataField).getSelectedIndex() >= 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public void deleteData() {
		// ApplicationManager.printCurrentThreadMethods(10);
		try {
			this.setInnerListenerEnabled(false);
			final Object oPreviousValue = this.getValue();
			((JComboBox) this.dataField).setSelectedIndex(-1);
			this.valueSave = this.getValue();
			this.setInnerValue(this.valueSave);
			// since 5.3.8. Reset pop-up tip showed when user types in combo
			if (this.dataField instanceof CustomComboBox) {
				((CustomComboBox) this.dataField).resetTipScroll();
			}
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} finally {
			this.setInnerListenerEnabled(true);
		}
	}

	/**
	 * Sets a fixed value List to show in scrollable combo.
	 * <p>
	 * @param values the List with values.
	 */
	public void setValues(final List values) {
		this.values = values;
		((JComboBox) this.dataField).removeAllItems();

		if (((JComboBox) this.dataField).getModel() instanceof CustomComboBoxModel) {
			if (values != null) {
				final CustomComboBoxModel model = (CustomComboBoxModel) ((JComboBox) this.dataField).getModel();
				model.setDataVector(values);
			}
		} else {
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					((JComboBox) this.dataField).addItem(values.get(i));
				}
			}
		}
		this.deleteData();
	}

	/**
	 * Gets the value vector.
	 * <p>
	 * @return the value vector
	 */
	public List getValues() {
		final List v = new Vector();
		for (int i = 0; i < ((JComboBox) this.dataField).getItemCount(); i++) {
			v.add(((JComboBox) this.dataField).getItemAt(i));
		}
		return v;
	}

	/**
	 * Establishes the combo values. If value parameter is a string array or a vector, values are added
	 * to combo one by one. If value is null, data are deleted.
	 * <p>
	 * @param value the object to set the value.
	 */
	@Override
	public void setValue(Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		if (value instanceof List) {
			((JComboBox) this.dataField).removeAllItems();
			for (int i = 0; i < ((List) value).size(); i++) {
				((JComboBox) this.dataField).addItem(((List) value).get(i));
			}
			((JComboBox) this.dataField).setSelectedIndex(0);
		} else {
			if (value instanceof String[]) {
				((JComboBox) this.dataField).removeAllItems();
				for (int i = 0; i < ((String[]) value).length; i++) {
					((JComboBox) this.dataField).addItem(((String[]) value)[i]);
				}
				((JComboBox) this.dataField).setSelectedIndex(0);
			} else {
				// If there is a value list
				boolean bAdd = true;
				int index = -1;
				for (int i = 0; i < ((JComboBox) this.dataField).getItemCount(); i++) {
					if (value instanceof String) {
						if (((JComboBox) this.dataField).getItemAt(i).equals(((String) value).trim())) {
							index = i;
							bAdd = false;
							break;
						}
					} else {
						if (((JComboBox) this.dataField).getItemAt(i).equals(value)) {
							index = i;
							bAdd = false;
							break;
						}
					}
				}
				if (bAdd && (index < 0)) {
					if (value instanceof String) {
						value = ((String) value).trim();
					}
					((JComboBox) this.dataField).addItem(value);
					((JComboBox) this.dataField).setSelectedItem(value);
				} else {
					((JComboBox) this.dataField).setSelectedIndex(index);
				}
			}
		}
		this.valueSave = this.getValue();
		this.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Gets the value for combo.
	 * <p>
	 *
	 * @see JComboBox#getSelectedItem()
	 * @return the value
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		return ((JComboBox) this.dataField).getSelectedItem();
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);

		if (this.formatPattern != null) {
			this.formatPattern.setResourceBundle(this.resources);
		}

		final JComboBox comboBox = (JComboBox) this.dataField;

		if ((comboBox != null) && (comboBox.getRenderer() instanceof Internationalization)) {
			((Internationalization) comboBox.getRenderer()).setResourceBundle(resource);
		}

	}

	/**
	 * Sets selected an index.
	 * <p>
	 * @param index the index to select
	 */
	public void setSelected(final int index) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		try {
			((JComboBox) this.dataField).setSelectedIndex(index);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				ComboDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
		}
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Installs the inner listener.
	 */
	protected void installInnerListener() {
		if (this.dataField != null) {
			((JComboBox) this.dataField).addActionListener(this.innerListener);
		}
	}

	/**
	 * Gets the inner value.
	 * <p>
	 * @return the inner value
	 */
	protected Object getInnerValue() {
		return this.innerListener.getInnerValue();
	}

	/**
	 * Sets the inner value
	 * <p>
	 * @param o the object to set the inner value
	 */
	protected void setInnerValue(final Object o) {
		this.innerListener.setInnerValue(o);
	}

	/**
	 * Compares the inner value with parameter value.
	 * <p>
	 * @param value the value
	 * @return the true or false comparation
	 */
	protected boolean isInnerValueEqual(final Object value) {
		return this.innerListener.isEqualInnerValue(value);
	}

	/**
	 * This method allows to enable/disable the ValueEvent events notifier. So, inner events will be not
	 * triggered when inner listener is disabled. It is advisable disabling the listener only when
	 * content is inserted by program.
	 * <p>
	 * @param enabled the condition to enable/disable the inner listener.
	 */
	protected void setInnerListenerEnabled(final boolean enabled) {
		// ApplicationManager.printCurrentThreadMethods(10);
		this.innerListener.setInnerListenerEnabled(enabled);
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				ComboDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				ComboDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				ComboDataField.logger.debug("Component: " + this.attribute + " modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void free() {
		super.free();
		if (this.dataField instanceof CustomComboBox) {
			((CustomComboBox) this.dataField).free();
		}
	}

}
