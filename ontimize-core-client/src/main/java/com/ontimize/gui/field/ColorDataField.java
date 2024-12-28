package com.ontimize.gui.field;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Types;
import java.util.Date;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.document.ColorDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

/**
 * The main class for creating a color data field.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ColorDataField extends TextFieldDataField {

	private static final Logger logger = LoggerFactory.getLogger(ColorDataField.class);

	/**
	 * Only for DEBUG mode. By default, false.
	 */
	public static boolean DEBUG = false;

	/** Return type property. */
	public static final String RETURN_TYPE_PROPERTY = "returnedtype";

	/** Color type value property. */
	public static final String COLOR_TYPE_VALUE = "color";

	/** String RGB type value property. */
	public static final String STRING_RGB_TYPE_VALUE = "stringrgb";

	/** String Hex type value property. */
	public static final String STRING_HEX_TYPE_VALUE = "stringhex";

	/** Number type value property. */
	public static final String NUMBER_TYPE_VALUE = "number";

	/** Integer type value property. */
	public static final String INTEGER_TYPE_VALUE = "integer";

	/** Show text field property. */
	public static final String SHOW_TEXT_FIELD_PROPERTY = "showtextfield";

	/** Yes property. */
	public static final String YES = "yes";

	/** No property. */
	public static final String NO = "no";

	/** Enabled text field property. */
	public static final String ENABLED_TEXT_FIELD_PROPERTY = "enabledtextfield";

	/** Show delete button property. */
	public static final String SHOW_DELETE_BUTTON_PROPERTY = "showdeletebutton";

	/** The color button. */
	protected JButton colorButton = null;

	/** The delete button. */
	protected JButton deleteButton = null;

	/** The panel Color. */
	protected JPanelColor panelColor = null;

	/** The static Color chooser. */
	protected static JColorChooser colorChooser = null;

	/**
	 * The return color.
	 * <p>
	 *
	 * @see ColorDocument#RETURN_COLOR
	 */
	protected int returnType = ColorDocument.RETURN_COLOR;

	/** The condition to enable text field. */
	protected boolean textFieldEnabled = true;

	/** The Color document. */
	protected ColorDocument colorDocument = null;

	/**
	 * The class constructor. It initializes the parameters, sets the color and sets the document.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters from XML definition.
	 */
	public ColorDataField(final Map parameters) {
		this.init(parameters);
		this.colorDocument = new ColorDocument(this.returnType);
		((JTextField) super.dataField).setDocument(this.colorDocument);
	}

	/**
	 * Calls to super() to init parameters.
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
	 *        <td>returnedtype</td>
	 *        <td><i>stringrgb;stringhex;number;integer</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The returned type:<br>
	 *        <ul>
	 *        <li>&quot;stringrgb&quot;: Returns a string in decimal format rrr;ggg;bbb (no necessary
	 *        always 10 characters)</li>
	 *        <li>&quot;stringhex&quot;: Returns a string in hexadecimal format #RRGGBB</li>
	 *        <li>&quot;number&quot;: Returns a Number object. Its value will be R*Math.exp(256, 2) +
	 *        G*256 + B</li>
	 *        <li>&quot;integer&quot;: Returns an integer object. Its value will be RBG</li>
	 *        </ul>
	 *        </td>
	 *        </tr>
	 *        <tr>
	 *        <td>showtextfield</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>Condition to show the text field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>enabledtextfield</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>Condition to activate the text field</td>
	 *        </tr>
	 *        <tr>
	 *        <td>showdeletebutton</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>Condition to show the delete button</td>
	 *        </tr>
	 *        <tr>
	 *        <td>colorwidth</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>Width in pixels of color bar showed in this field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>colorheight</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>Height in pixels of color bar showed in this field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>borderbuttons</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Condition about border of buttons for this field. by default, borders are
	 *        enabled.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaquebuttons</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Condition about opacity of buttons for this field. by default, buttons are
	 *        opaques.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>highlightbuttons</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Condition about highlight of buttons for this field. by default, hightlight is
	 *        disabled.</td>
	 *        </tr>
	 *        </TABLE>
	 */

	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		if (this.panelColor == null) {
			this.panelColor = new JPanelColor();
			final Dimension d = this.panelColor.getPreferredSize();
			final int colorWidth = ParseUtils.getInteger((String) parameters.get("colorwidth"), 50);
			d.width = colorWidth;
			final int colorHeight = ParseUtils.getInteger((String) parameters.get("colorheight"), d.height);
			d.height = colorHeight;
			this.panelColor.setPreferredSize(d);
			this.panelColor.setBorder(BorderFactory.createLineBorder(Color.black));
			super.add(this.panelColor,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 2, 1, 2, 2, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		if (this.colorButton == null) {
			final ImageIcon chooseColorIcon = ImageManager.getIcon(ImageManager.CHOOSE_COLOR);
			if (chooseColorIcon != null) {
				this.colorButton = new DataField.FieldButton(chooseColorIcon);
			} else {
				if (ApplicationManager.DEBUG) {
					ColorDataField.logger.debug("choosecolor.png icon hasn't been found");
				}
				this.colorButton = new DataField.FieldButton("...");
			}
			this.colorButton.setMargin(new Insets(0, 0, 0, 0));
			super.add(this.colorButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0D, 0.0D, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			this.colorButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (ColorDataField.colorChooser == null) {
						ColorDataField.colorChooser = new JColorChooser();
					}
					final String sTitle = ApplicationManager.getTranslation("ColorDataField.chooseColor",
							ColorDataField.this.parentForm.getResourceBundle());
					final Color color = JColorChooser.showDialog(ColorDataField.this, sTitle,
							ColorDataField.this.getColorValue());
					if (color != null) {
						ColorDataField.this.setInnerListenerEnabled(false);
						ColorDataField.this.colorDocument.setColorValue(color);
						ColorDataField.this.setInnerListenerEnabled(true);
						ColorDataField.this.fireValueChanged(ColorDataField.this.getValue(),
								ColorDataField.this.valueSave, ValueEvent.USER_CHANGE);
						if (ColorDataField.this.panelColor != null) {
							ColorDataField.this.panelColor.setColor(ColorDataField.this.getColorValue());
						}
					}
				}
			});
		}

		if (this.deleteButton == null) {
			final ImageIcon cancelIcon = ImageManager.getIcon(ImageManager.CANCEL);
			if (cancelIcon != null) {
				this.deleteButton = new DataField.FieldButton(cancelIcon);
			} else {
				if (ApplicationManager.DEBUG) {
					ColorDataField.logger.debug("cancel.png icon not found");
				}
				this.deleteButton = new DataField.FieldButton("...");
			}
			this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
			super.add(this.deleteButton,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0D, 0.0D, GridBagConstraints.CENTER,
							GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			this.deleteButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ColorDataField.this.setInnerListenerEnabled(false);
					ColorDataField.this.colorDocument.setColorValue(null);
					ColorDataField.this.setInnerListenerEnabled(true);
					ColorDataField.this.fireValueChanged(ColorDataField.this.getValue(), ColorDataField.this.valueSave,
							ValueEvent.USER_CHANGE);
					if (ColorDataField.this.panelColor != null) {
						ColorDataField.this.panelColor.setColor(null);
					}
				}
			});

		}

		if (parameters.containsKey(ColorDataField.RETURN_TYPE_PROPERTY)) {
			final String sType = parameters.get(ColorDataField.RETURN_TYPE_PROPERTY).toString();
			if (sType.equals(ColorDataField.STRING_RGB_TYPE_VALUE)) {
				this.returnType = ColorDocument.RETURN_RGB_STRING;
			} else if (sType.equals(ColorDataField.STRING_HEX_TYPE_VALUE)) {
				this.returnType = ColorDocument.RETURN_HEX_STRING;
			} else if (sType.equals(ColorDataField.NUMBER_TYPE_VALUE)) {
				this.returnType = ColorDocument.RETURN_NUMBER;
			} else if (sType.equals(ColorDataField.INTEGER_TYPE_VALUE)) {
				this.returnType = ColorDocument.RETURN_INTEGER;
			}
		}

		final boolean showTextField = ParseUtils.getBoolean((String) parameters.get(ColorDataField.SHOW_TEXT_FIELD_PROPERTY),
				true);
		if (!showTextField && (this.dataField != null)) {
			this.dataField.setVisible(false);
		}

		final boolean enabledTextField = ParseUtils
				.getBoolean((String) parameters.get(ColorDataField.ENABLED_TEXT_FIELD_PROPERTY), true);
		if (!enabledTextField) {
			this.textFieldEnabled = false;
			if (this.dataField != null) {
				super.dataField.setEnabled(false);
			}
		}

		final boolean showDeleteButton = ParseUtils
				.getBoolean((String) parameters.get(ColorDataField.SHOW_DELETE_BUTTON_PROPERTY), true);
		if (!showDeleteButton && (this.deleteButton != null)) {
			this.deleteButton.setVisible(false);
		}

		final boolean borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"), true);
		final boolean opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false);
		MouseListener listenerHighlightButtons = null;
		if (highlightButtons) {
			listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButton(this.deleteButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
		this.changeButton(this.colorButton, borderbuttons, opaquebuttons, listenerHighlightButtons);

		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}
	}

	/**
	 * Gets the JPanel Color.
	 * <p>
	 * @return the JPanel Color
	 */
	public JPanelColor getColorPanel() {
		return this.panelColor;
	}

	/**
	 * Gets the JButton color.
	 * <p>
	 * @return the color button.
	 */
	public JButton getColorButton() {
		return this.colorButton;
	}

	/**
	 * Gets the delete button.
	 * <p>
	 * @return the delete button.
	 */
	public JButton getDeleteButton() {
		return this.deleteButton;
	}

	/**
	 * The main class for creating a color panel.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class JPanelColor extends JPanel {

		protected Color color = null;

		/**
		 * The constructor calls to parent constructor and setColor.
		 */
		public JPanelColor() {
			super();
			this.setColor(null);
		}

		/**
		 * Sets the color for panel.
		 * <p>
		 * @param color the color parameter
		 */
		public void setColor(final Color color) {
			this.color = color;
			if (color == null) {
				this.setBackground(Color.white);
			} else {
				this.setBackground(color);
			}
			this.repaint();
		}

		/**
		 * Gets the panel color.
		 * <p>
		 * @return the panel color
		 */
		public Color getColor() {
			return this.color;
		}

		/**
		 * The method paint.
		 * <p>
		 * @param g the graphics object
		 */
		@Override
		public void paint(final Graphics g) {
			super.paint(g);
			if (this.color == null) {
				final Graphics2D g2 = (Graphics2D) g;
				g2.setPaint(Color.red);
				g2.setStroke(new BasicStroke(1));
				g2.drawLine(0, 0, this.getWidth(), this.getHeight());
				this.paintBorder(g);
			}
		}

	}

	/**
	 * The main class to listen the document inner color.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ColorInnerDocumentListener extends InnerDocumentListener {

		/** The condition to enable or disable the inner listener. */
		protected boolean innerListenerActive = false;

		/**
		 * Empty method.
		 */
		@Override
		public void changedUpdate(final DocumentEvent documentevent) {
		}

		/**
		 * Sets the color when {@link #innerListenerActive} is enabled.
		 * <p>
		 * @ param e the document change notification
		 */
		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerActive) {
				ColorDataField.this.fireValueChanged(ColorDataField.this.getValue(), ColorDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
			if (ColorDataField.this.panelColor != null) {
				ColorDataField.this.panelColor.setColor(ColorDataField.this.getColorValue());
			}
		}

		/**
		 * Removes the update color when {@link #innerListenerActive} is enabled.
		 * <p>
		 * @param e the document change notification
		 */
		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerActive) {
				ColorDataField.this.fireValueChanged(ColorDataField.this.getValue(), ColorDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
			if (ColorDataField.this.panelColor != null) {
				ColorDataField.this.panelColor.setColor(ColorDataField.this.getColorValue());
			}
		}

		/**
		 * Changes the inner listener according to parameter
		 * <p>
		 * @param enabled the condition to enable the inner listener
		 */
		@Override
		public void setInnerListenerEnabled(final boolean enabled) {
			this.innerListenerActive = enabled;
		}

		/**
		 * The constructor to set true the inner document listener.
		 */
		protected ColorInnerDocumentListener() {
			this.innerListenerActive = true;
		}

	}

	/**
	 * The method to add a new inner listener.
	 */
	@Override
	protected void installInnerListener() {
		if (super.dataField != null) {
			final Document d = ((JTextField) super.dataField).getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new ColorInnerDocumentListener();
				}
				d.addDocumentListener(this.innerListener);
			}
		}
	}

	/**
	 * The method to get the integer SQL types for color, rgb string, hex string, number or integer.
	 * <p>
	 * @return the return type according to SQL types
	 */
	@Override
	public int getSQLDataType() {
		switch (this.returnType) {
		case ColorDocument.RETURN_COLOR:
			return Types.OTHER;
		case ColorDocument.RETURN_RGB_STRING:
		case ColorDocument.RETURN_HEX_STRING:
			return Types.VARCHAR;
		case ColorDocument.RETURN_NUMBER:
			return Types.INTEGER;
		case ColorDocument.RETURN_INTEGER:
			return Types.INTEGER;
		}
		return Types.OTHER;
	}

	/**
	 * The empty or no empty condition according to method {@link #getValue()}.
	 * <p>
	 *
	 * @see #getValue()
	 * @return the condition
	 */
	@Override
	public boolean isEmpty() {
		return this.getValue() == null;
	}

	/**
	 * The method to get the color value from a document reference.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return the document color value.
	 */
	public Color getColorValue() {
		final ColorDocument document = (ColorDocument) ((JTextField) this.dataField).getDocument();
		return document.getColorValue();
	}

	/**
	 * The method to get the RGB color from a document reference.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return the string with RGB value
	 */
	public String getRGBValue() {
		final ColorDocument document = (ColorDocument) ((JTextField) this.dataField).getDocument();
		return document.getRGBStringValue();
	}

	/**
	 * The method to get the HEX RGB color from a document reference.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return the string with Hex RGB value
	 */
	public String getHEXValue() {
		final ColorDocument document = (ColorDocument) ((JTextField) this.dataField).getDocument();
		return document.getHEXStringValue();
	}

	/**
	 * The method to get the number color.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return a numerical value
	 */
	public Number getNumericalValue() {
		final ColorDocument document = (ColorDocument) ((JTextField) this.dataField).getDocument();
		return document.getNumericalValue();
	}

	/**
	 * The method to get the number color in a object.
	 * <p>
	 *
	 * @see JTextComponent#getDocument()
	 * @return a numerical value
	 */
	@Override
	public Object getValue() {
		final ColorDocument document = (ColorDocument) ((JTextField) this.dataField).getDocument();
		return document.getValue();
	}

	/**
	 * The main method to set the color values.
	 * <p>
	 * @param value the object where the values(color,number,...) are placed
	 */
	@Override
	public void setValue(final Object value) {
		if (ColorDataField.DEBUG) {
			ColorDataField.logger.debug(new Date() + " : " + this.getClass().getName() + ".setValue: " + value);
		}
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		if ((value instanceof Color) || (value instanceof String) || (value instanceof Number)) {
			((ColorDocument) ((JTextField) super.dataField).getDocument()).setValue(value);
			super.valueSave = this.getValue();
			this.fireValueChanged(super.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} else {
			this.deleteData();
		}
		this.setInnerListenerEnabled(true);
		if (this.panelColor != null) {
			this.panelColor.setColor(this.getColorValue());
		}
		if (ColorDataField.DEBUG) {
			if (this.getValue() != null) {
				ColorDataField.logger
				.debug("Color " + ((ColorDocument) ((JTextField) super.dataField).getDocument()).getColorValue());
				ColorDataField.logger.debug("StringRGB "
						+ ((ColorDocument) ((JTextField) super.dataField).getDocument()).getRGBStringValue());
				ColorDataField.logger.debug("StringHEX "
						+ ((ColorDocument) ((JTextField) super.dataField).getDocument()).getHEXStringValue());
				ColorDataField.logger.debug(
						"Number " + ((ColorDocument) ((JTextField) super.dataField).getDocument()).getNumericalValue()
						.intValue());
			}
		}
	}

	/**
	 * Deletes data.
	 */
	@Override
	public void deleteData() {
		if (ColorDataField.DEBUG) {
			ColorDataField.logger.debug(new Date() + ": " + this.getClass().getName() + " .deletedata()");
		}
		super.deleteData();
	}

	/**
	 * Checks permissions and enables the buttons according to @param.
	 * <p>
	 * @param enabled the condition to enable components
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permissioin = this.checkEnabledPermission();
			if (!permissioin) {
				return;
			}
		}
		super.setEnabled(enabled);
		if (this.colorButton != null) {
			this.colorButton.setEnabled(enabled);
		}

		if (this.deleteButton != null) {
			this.deleteButton.setEnabled(enabled);
		}

		if (this.dataField != null) {
			if (this.textFieldEnabled) {
				this.dataField.setEnabled(enabled);
			} else {
				this.dataField.setEnabled(false);
			}
		}
	}

}
