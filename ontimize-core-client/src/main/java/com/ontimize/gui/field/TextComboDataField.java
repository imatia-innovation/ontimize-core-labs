package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a combo where elements are rendered like strings.
 * <p>
 *
 * @author Imatia Innovation
 */
public class TextComboDataField extends ComboDataField {

	private static final Logger logger = LoggerFactory.getLogger(TextComboDataField.class);

	/**
	 * The condition to translate texts. By default, false.
	 */
	protected boolean translateTexts = false;

	/**
	 * The equivalences properties. By default, null.
	 */
	protected Properties equivalents = null;

	/**
	 * The code number class. By default, it is null in this field for supporting only varchar codes.
	 *
	 * @since 5.3.15
	 */
	protected Integer codeNumberClass;

	protected class TranslateRenderer extends DefaultCustomComboBoxRenderer {

		public static final String COMBO_RENDERER_LABEL_NAME = "ComboBox.listRenderer";

		@Override
		public String getName() {
			return COMBO_RENDERER_LABEL_NAME;
		}

		/**
		 * Gets the text from object.
		 * <p>
		 * @param value the object
		 * @return the text
		 */
		public String getText(final Object value) {
			String sText = null;
			if (value != null) {
				sText = value.toString();
				if (TextComboDataField.this.translateTexts && !CustomComboBoxModel.NULL_SELECTION.equals(sText)) {
					if (TextComboDataField.this.resources != null) {
						sText = ApplicationManager.getTranslation(sText, TextComboDataField.this.resources);
						return sText;
					}
				}
				if (TextComboDataField.this.equivalents != null) {
					sText = TextComboDataField.this.getEquivalent(value);
				}
			}
			return sText;
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			String sText = null;
			if (value != null) {
				sText = value.toString();
				if (!CustomComboBoxModel.NULL_SELECTION.equals(sText)) {
					if ((TextComboDataField.this.formatPattern != null)
							&& !TextComboDataField.this.formatPattern.isEmpty()) {
						sText = TextComboDataField.this.formatPattern.parse(value);
					} else if (TextComboDataField.this.translateTexts && (TextComboDataField.this.resources != null)) {
						if (TextComboDataField.this.equivalents != null) {
							sText = ApplicationManager.getTranslation(TextComboDataField.this.getEquivalent(value),
									TextComboDataField.this.resources);
						} else {
							sText = ApplicationManager.getTranslation(sText, TextComboDataField.this.resources);
						}
					} else if (TextComboDataField.this.equivalents != null) {
						sText = TextComboDataField.this.getEquivalent(value);
					}
				}
			}

			final Component component =  super.getListCellRendererComponent(list, sText, index, isSelected, cellHasFocus);
			if (index==-1) {
				component.setEnabled(TextComboDataField.this.isEnabled());
			}
			return component;
		}

		// Used because the super.isOpaque() return false if the parent
		// component
		// is not opaque
		// Without this variable the required background color does not work
		protected boolean opaque;

		@Override
		public boolean isOpaque() {
			return super.isOpaque() || this.opaque;
		}

		@Override
		public void setOpaque(final boolean isOpaque) {
			super.setOpaque(isOpaque);
			this.opaque = isOpaque;
		}

		@Override
		public void paint(final Graphics g) {
			super.paint(g);
		}

	}

	/**
	 * The class constructor. Calls to super() and initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public TextComboDataField(final Map parameters) {
		super(new CustomComboBox() {

			@Override
			public void configureEditor(final ComboBoxEditor anEditor, final Object anItem) {
				if (CustomComboBoxModel.NULL_SELECTION.equals(anItem)) {
					anEditor.setItem(null);
				} else {
					anEditor.setItem(anItem);
				}
			}
		});
		this.init(parameters);
		((JComboBox) this.dataField).setModel(new CustomComboBoxModel(this.nullSelection));
		this.deleteData();

		final Object equivalences = parameters.get("equivalences");
		if (equivalences != null) {
			try {
				this.equivalents = new Properties();
				final URL url = this.getClass().getClassLoader().getResource(equivalences.toString());
				this.equivalents.load(url.openStream());
			} catch (final Exception e) {
				TextComboDataField.logger.error(null, e);
			}
		}
		this.processEquivalents();

		final Object values = parameters.get("values");
		if (values != null) {
			final StringTokenizer st = new StringTokenizer(values.toString(), ";");
			final List v = new Vector();
			while (st.hasMoreTokens()) {
				v.add(st.nextToken());
			}
			if (ApplicationManager.DEBUG) {
				TextComboDataField.logger
				.debug(this.getClass().toString() + " : " + this.attribute + " Set values: " + v);
			}
			this.setValues(v);
		}

		this.translateTexts = ParseUtils.getBoolean((String) parameters.get("translate"), false);

		((JComboBox) this.dataField).setRenderer(new TranslateRenderer());

		boolean ed = false;
		final Object editable = parameters.get("editable");
		if (editable != null) {
			if (editable.equals("yes")) {
				ed = true;
			} else {
				ed = false;
			}
		} else {
			ed = false;
		}
		if (ed) {
			this.setEditable(ed);
		}
	}

	protected void processEquivalents() {
		if ((this.codeNumberClass == null) || (this.equivalents == null)) {
			// valid values
			return;
		}
		if (this.equivalents != null) {
			// Search in the equivalences
			final List<String> lKeys = new ArrayList(this.equivalents.keySet());
			for (int i = 0; i < lKeys.size(); i++) {
				final Object oKey = lKeys.get(i);
				final Object oValue = this.equivalents.remove(oKey);
				final Object oNewKey = ParseUtils.getValueForClassType(oKey, this.codeNumberClass);
				this.equivalents.put(oNewKey, oValue);
			}
		}
	}

	public List processValues(final List values) {
		if ((values == null) || (this.codeNumberClass == null)) {
			return values;
		}
		for (int i = 0; i < values.size(); i++) {
			final Object value = values.get(i);
			final Object newValue = ParseUtils.getValueForClassType(value, this.codeNumberClass);
			if (newValue != null) {
				values.set(i, newValue);
			}
		}
		return values;

	}

	/**
	 * Sets the data field editable.
	 * <p>
	 * @param b the condition to set editable.
	 */
	public void setEditable(final boolean b) {
		((JComboBox) this.dataField).setEditable(b);
	}

	/**
	 * Returns the data field editable condition.
	 * <p>
	 * @return the editable condition
	 */
	public boolean isEditable() {
		return ((JComboBox) this.dataField).isEditable();
	}

	/**
	 * Sets the value to the field.
	 * <p>
	 * @param value the value to set
	 */
	@Override
	public void setValue(final Object value) {
		// if (value != null) {
		// if (this.equivalents != null) {
		// Object eq = this.equivalents.get(value);
		// if (eq != null) {
		// super.setValue(eq);
		// } else {
		// super.setValue(value);
		// }
		// } else {
		// super.setValue(value);
		// }
		// } else
		super.setValue(value);
	}

	@Override
	public void setValues(List values) {
		values = this.processValues(values);
		super.setValues(values);
	}

	@Override
	public boolean isEmpty() {
		if (this.isEditable()) {
			return (((JComboBox) this.dataField).getEditor().getItem() == null)
					|| "".equals(((JComboBox) this.dataField).getEditor().getItem());
		} else {
			return super.isEmpty();
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		Object v = super.getValue();
		if (this.isEditable()) {
			v = ((JComboBox) this.dataField).getEditor().getItem();
		}
		// if (v != null) {
		// if (this.equivalents != null) {
		// // Search in the equivalences
		// Object eq = null;
		// Enumeration enumKeys = this.equivalents.keys();
		// while (enumKeys.hasMoreElements()) {
		// Object oKey = enumKeys.nextElement();
		// Object oValue = this.equivalents.get(oKey);
		// if (oValue.equals(v)) {
		// eq = oKey;
		// break;
		// }
		// }
		// return eq == null ? v : eq;
		// }
		// }
		return v;
	}

	/**
	 * Gets an element equivalent from equivalence set.
	 * <p>
	 * @param s the text to obtain its equivalent
	 * @return the equivalent
	 */
	protected String getEquivalent(final String s) {
		if (this.equivalents != null) {
			return s != null ? (String) this.equivalents.get(s) : null;
		} else {
			return null;
		}
	}

	protected String getEquivalent(final Object s) {
		if (this.equivalents != null) {
			return s != null ? (String) this.equivalents.get(s) : null;
		} else {
			return null;
		}
	}

	/**
	 * Sets the locale component.
	 * <p>
	 * @param l the locale to establish
	 */
	@Override
	public void setComponentLocale(final Locale l) {
		this.localeComponente = l;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
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
	 *        <td>codnumberclass</td>
	 *        <td>BigDecimal/Integer/Long...</td>
	 *        <td>Integer</td>
	 *        <td>no</td>
	 *        <td>since 5.3.15. String value defining data type that field stores in db (All values are
	 *        mapped in ParseUtils class: ParseUtils.INTEGER/ParseUtils.BIG_INTEGER/...)</td>
	 *        </tr>
	 *        <tr>
	 *        <td>values</td>
	 *        <td><i>value1;value2;...;valuen</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Indicates values for combo.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>translate</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether translations are looked for the resource file.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>equivalences</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The URI where there is an equivalence file for combo values. For example, in database
	 *        student qualifications are stored like A,B,C,D<br>
	 *        but it is wanted a most representative values, so it could be defined a equivalence
	 *        properties like:<br>
	 *        A = worst<br>
	 *        B = regular<br>
	 *        C = good<br>
	 *        D = great<br>
	 *        </td>
	 *        </tr>
	 *        <tr>
	 *        <td>editable</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether field support the edition in combo values.</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		// since 5.3.15
		final Object codnumberclass = parameters.get("codnumberclass");
		if (codnumberclass != null) {
			this.codeNumberClass = ParseUtils.getTypeForName(codnumberclass.toString(), ParseTools.INTEGER_);
		}
	}

	@Override
	public int getSQLDataType() {
		if (this.codeNumberClass != null) {
			return this.codeNumberClass;
		}
		return Types.VARCHAR;
	}

	@Override
	public String getText() {
		if (this.isEmpty()) {
			return "";
		}
		if (this.isEditable()) {
			return ((JComboBox) this.dataField).getEditor().getItem().toString();
		}
		if (!(((JComboBox) this.dataField).getRenderer() instanceof TranslateRenderer)) {
			final Object oValue = this.getValue();
			final Component c = ((JComboBox) this.dataField).getRenderer()
					.getListCellRendererComponent(null, this.getEquivalent(oValue != null ? oValue.toString() : null), 0,
							false,
							false);
			if (c instanceof JLabel) {
				return ((JLabel) c).getText();
			} else if (c instanceof JTextComponent) {
				return ((JTextComponent) c).getText();
			} else {
				return "";
			}
		} else {
			final Object oValue = this.getValue();
			String textToTranslate = this.getEquivalent(oValue != null ? oValue.toString() : null);
			if (textToTranslate == null) {
				textToTranslate = oValue != null ? oValue.toString() : null;
			}
			return ((TranslateRenderer) ((JComboBox) this.dataField).getRenderer()).getText(textToTranslate);
		}
	}

}
