package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * The main class to set graphic configuration to labels.
 * <p>
 *
 * @author Imatia Innovation
 */

public class Label extends JPanel implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Label.class);

	/**
	 * The top margin. By default, 2.
	 * <p>
	 *
	 * @see DataField#DEFAULT_TOP_MARGIN
	 */
	public static int DEFAULT_TOP_MARGIN = DataField.DEFAULT_TOP_MARGIN;

	/**
	 * The bottom margin. By default, 2.
	 * <p>
	 *
	 * @see DataField#DEFAULT_BOTTOM_MARGIN
	 */

	public static int DEFAULT_BOTTOM_MARGIN = DataField.DEFAULT_BOTTOM_MARGIN;

	/**
	 * The label left margin. By default, 10.
	 * <p>
	 *
	 * @see DataField#DEFAULT_LABEL_LEFT_MARGIN
	 */
	public static int DEFAULT_LABEL_LEFT_MARGIN = DataField.DEFAULT_LABEL_LEFT_MARGIN;

	/**
	 * The label right margin. By default, 10.
	 * <p>
	 *
	 * @see DataField#DEFAULT_LABEL_RIGHT_MARGIN
	 */
	public static int DEFAULT_LABEL_RIGHT_MARGIN = DataField.DEFAULT_LABEL_RIGHT_MARGIN;

	/**
	 * The parent margin. By default, 1.
	 * <p>
	 *
	 * @see DataField#DEFAULT_PARENT_MARGIN
	 */
	public static int DEFAULT_PARENT_MARGIN = DataField.DEFAULT_PARENT_MARGIN;

	/**
	 * The attr property.
	 */
	public static final String ATTR = "attr";

	/**
	 * The text property.
	 */
	public static final String TEXT = "text";

	/**
	 * The align property.
	 */
	public static final String ALIGN = "align";

	/**
	 * The valign property.
	 */
	public static final String VALIGN = "valign";

	/**
	 * The left property.
	 */
	public static final String LEFT = "left";

	/**
	 * The right property.
	 */
	public static final String RIGHT = "right";

	/**
	 * The center property.
	 */
	public static final String CENTER = "center";

	/**
	 * The bottom property.
	 */
	public static final String BOTTOM = "bottom";

	/**
	 * The size property.
	 */
	public static final String SIZE = "size";

	/**
	 * The fontsize property.
	 */
	public static final String FONTSIZE = "fontsize";

	public static final String FONT = "font";

	/**
	 * The fontcolor property.
	 */
	public static final String FONTCOLOR = "fontcolor";

	/**
	 * The bold property.
	 */
	public static final String BOLD = "bold";

	public static final String MARGIN = "margin";

	/**
	 * The bolt writing condition.
	 */
	protected boolean bold = false;

	/**
	 * The param attribute. By default, null.
	 * <p>
	 *
	 * @see #getAttribute()
	 */
	protected Object attribute = null;

	/**
	 * The font color. By default, black.
	 * <p>
	 *
	 * @see Color#black
	 */
	protected Color fontColor = Color.black;

	/**
	 * The font size. By default, -1.
	 */
	protected int fontSize = -1;

	/**
	 * The original size. By default, -1.
	 */
	protected int originalSize = -1;

	/**
	 * The incremental font. By default, false.
	 */
	protected boolean incrementalFont = false;

	/**
	 * The label size. By default, -1.
	 */
	protected int labelSize = -1;

	/**
	 * The text label. By default, -1.
	 */
	protected String textLabel = null;

	/**
	 * The alignment. By default, NORTHWEST.
	 * <p>
	 *
	 * @see GridBagConstraints#NORTHWEST
	 */
	protected int alignment = GridBagConstraints.NORTHWEST;

	/**
	 * The alignment. By default, NORTH.
	 * <p>
	 *
	 * @see GridBagConstraints#NORTH
	 */
	protected int alignmentV = GridBagConstraints.NORTH;

	/**
	 * The parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * The visibility permission. By default, null.
	 */
	protected FormPermission permissionVisible = null;

	/**
	 * The activation permission. By default, null.
	 */
	protected FormPermission permissionsActived = null;

	/**
	 * The resource file. By default, null.
	 */
	protected ResourceBundle resourceFile = null;

	/**
	 * The label.
	 */
	protected JLabel label = new EJLabel();

	/**
	 * Variable to indicate the field resize.
	 */
	protected double dim;

	/**
	 * The class to create the label.
	 * <p>
	 *
	 * @see JLabel
	 */
	protected class EJLabel extends JLabel {

		public EJLabel() {
			// this.setBorder(new LineBorder(Color.red));
		}

		@Override
		public String getName() {
			return DataField.ELabel.ELABEL;
		}

		/**
		 * Checks the component focusable condition.
		 * <p>
		 * @return in this case always returns false
		 */
		@Override
		public boolean isFocusable() {
			return false;
		}

		/**
		 * Sets the cursor position.
		 * <p>
		 * @param the cursor position
		 */
		@Override
		public void setCursor(final Cursor c) {
			super.setCursor(c);
		}

		/**
		 * The underlined text possibility. By default, false.
		 */
		protected boolean underlined = false;

		Insets paintViewInsets = new Insets(0, 0, 0, 0);

		/**
		 * Sets underlined text in label.
		 * <p>
		 * @param u the condition to set underlined.
		 */
		public void setUnderlined(final boolean u) {
			this.underlined = u;
		}

		/**
		 * The method to paint the label component.
		 * <p>
		 * @param g the graphics component
		 */
		@Override
		protected void paintComponent(final Graphics g) {
			super.paintComponent(g);
			if (this.underlined) {
				// FontMetrics fm = g.getFontMetrics();
				this.paintViewInsets = this.getInsets(this.paintViewInsets);
				final Rectangle paintViewR = new Rectangle();
				paintViewR.x = this.paintViewInsets.left;
				paintViewR.y = this.paintViewInsets.top;
				paintViewR.width = this.getWidth() - (this.paintViewInsets.left + this.paintViewInsets.right);
				paintViewR.height = this.getHeight() - (this.paintViewInsets.top + this.paintViewInsets.bottom);
				g.drawLine(paintViewR.x, paintViewR.height - 1, paintViewR.width, paintViewR.height - 1);
			}
		}

		/**
		 * Gets the preferred size for label component.
		 * <p>
		 * @return the preferred size for label
		 */
		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			try {
				if ((Label.this.labelSize != -1) && (Label.this.labelSize >= 0)) {
					final FontMetrics fontMetrics = this.getFontMetrics(this.getFont().deriveFont(Font.PLAIN));
					final int iWidth = Label.this.labelSize * fontMetrics.charWidth('A');
					return new Dimension(iWidth, d.height);
				}
			} catch (final Exception e) {
				Label.logger.error(null, e);
			}
			return d;
		}

	};

	/**
	 * The class constructor. Sets layout, borders and inits parameters
	 * <p>
	 * @param parameters a <code>Hashtable</code> with label parameters
	 */
	public Label(final Map parameters) {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.init(parameters);
		this.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.add(this.label);
		this.setOpaque(false);
	}

	/**
	 * This method gets the <code>Hashtable</code> and initializes the label with parameters specified,
	 * also aligns the label.
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
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The label text</td>
	 *        </tr>
	 *        <tr>
	 *        <td>align</td>
	 *        <td><i>right/left</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The alignment</td>
	 *        </tr>
	 *        <tr>
	 *        <td>valign</td>
	 *        <td><i>center/bottom</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The vertical alignment</td>
	 *        </tr>
	 *        <tr>
	 *        <td>size</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The label size</td>
	 *        </tr>
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The icon for label</td>
	 *        </tr>
	 *        <tr>
	 *        <td>fontsize</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The font size</td>
	 *        </tr>
	 *        <tr>
	 *        <td>fontcolor</td>
	 *        <td></td>
	 *        <td>black</td>
	 *        <td>no</td>
	 *        <td>The font color</td>
	 *        </tr>
	 *        <tr>
	 *        <td>font</td>
	 *        <td>A string like : 'Arial-BOLD-18' (See #Font.decode())</td>
	 *        <td>The default font for system</td>
	 *        <td>no</td>
	 *        <td>Font for data field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bold</td>
	 *        <td><i>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The bold capitalization condition</td>
	 *        </tr>
	 *        <tr>
	 *        <td>margin</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The empty space around the label in its panel</td>
	 *        </tr>
	 *        <tr>
	 *        <td>dim</td>
	 *        <td><i>no/text/yes</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The resize possibilities (no resize or resize, values yes and text are the same but
	 *        both of them exist to maintain the same possibilities that other fields accept).</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		final Object align = parameters.get(Label.ALIGN);
		if (align != null) {
			if (align.equals(Label.RIGHT)) {
				this.alignment = GridBagConstraints.NORTHEAST;
			} else {
				if (align.equals(Label.LEFT)) {
					this.alignment = GridBagConstraints.NORTHWEST;
				} else {
					this.alignment = GridBagConstraints.NORTH;
				}
			}
		}

		final Object valign = parameters.get(Label.VALIGN);
		if (valign != null) {
			if (valign.equals(Label.CENTER)) {
				this.alignmentV = GridBagConstraints.CENTER;
			} else {
				if (valign.equals(Label.BOTTOM)) {
					this.alignmentV = GridBagConstraints.SOUTH;
				} else {
					this.alignmentV = GridBagConstraints.NORTH;
				}
			}
		}

		final Object atrib = parameters.get(Label.ATTR);
		if (atrib == null) {
			Label.logger.debug("Parameter '{}' not found", Label.ATTR);
		} else {
			this.attribute = atrib.toString();
		}

		Object text = parameters.get(Label.TEXT);
		if (text == null) {
			Label.logger.debug("Parameter '{}' not found", Label.TEXT);
			text = "";
		} else {
			this.textLabel = text.toString();
			this.label.setText(this.textLabel);
		}

		final Object oSize = parameters.get(Label.SIZE);
		if (oSize != null) {
			try {
				this.labelSize = Integer.parseInt(oSize.toString());
			} catch (final Exception e) {
				Label.logger.debug("Error in paramater 'size': " + oSize.toString(), e);
			}
		}

		final Object icon = parameters.get("icon");
		if (icon != null) {
			final String iconFile = icon.toString();
			final ImageIcon iconObject = ImageManager.getIcon(iconFile);
			if (iconObject != null) {
				this.label.setIcon(iconObject);
			} else {
				Label.logger.debug("Icon not found");
			}
		}

		final Object fontsize = parameters.get(Label.FONTSIZE);
		if (fontsize != null) {
			if (fontsize.toString().charAt(0) == '+') {
				this.incrementalFont = true;
				try {
					this.fontSize = Integer.parseInt(fontsize.toString().substring(1));
					this.setFontSize(this.fontSize);
				} catch (final Exception e) {
					Label.logger.error("Error in parameter 'fontsize': ", e);
				}
			} else {
				this.incrementalFont = false;
				try {
					this.fontSize = Integer.parseInt(fontsize.toString());
					this.setFontSize(this.fontSize);
				} catch (final Exception e) {
					Label.logger.error(" Error in parameter 'fontsize' : ", e);
				}
			}
		}

		final Object fontcolor = parameters.get(Label.FONTCOLOR);
		if (fontcolor != null) {
			try {
				this.fontColor = ColorConstants.parseColor(fontcolor.toString());
				this.setFontColor(this.fontColor);
			} catch (final Exception e) {
				Label.logger.error(" Error in parameter 'fontcolor' : ", e);
			}
		}

		final Object bold = parameters.get(Label.BOLD);
		if (bold != null) {
			if (bold.equals("yes")) {
				this.bold = true;
			} else {
				this.bold = false;
			}
		}
		if (this.bold) {
			this.setBold(this.bold);
		}

		final Object margin = parameters.get(Label.MARGIN);
		if (margin != null) {
			try {
				this.label.setBorder(new EmptyBorder(ApplicationManager.parseInsets((String) margin)));
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					Label.logger.error(null, e);
				} else {
					Label.logger.trace(null, e);
				}
				this.label.setBorder(new EmptyBorder(Label.DEFAULT_TOP_MARGIN, Label.DEFAULT_LABEL_LEFT_MARGIN,
						Label.DEFAULT_BOTTOM_MARGIN, Label.DEFAULT_LABEL_RIGHT_MARGIN));
			}
		} else {
			this.label.setBorder(new EmptyBorder(Label.DEFAULT_TOP_MARGIN, Label.DEFAULT_LABEL_LEFT_MARGIN,
					Label.DEFAULT_BOTTOM_MARGIN, Label.DEFAULT_LABEL_RIGHT_MARGIN));
		}

		this.label.setFont(ParseUtils.getFont((String) parameters.get(Label.FONT), this.label.getFont()));

		final String dimValue = (String) parameters.get(DataField.DIM);
		if (dimValue != null) {
			if (dimValue.equalsIgnoreCase("yes") || dimValue.equalsIgnoreCase("text")) {
				this.dim = 1;
			} else if (dimValue.equalsIgnoreCase("no")) {
				this.dim = 0;
			} else {
				try {
					this.dim = Double.parseDouble(dimValue);
				} catch (final Exception e) {
					Label.logger.trace(null, e);
				}
			}
		} else {
			this.dim = 0.00001;
		}
	}

	/**
	 * Updates the font size.
	 */
	public void updateFont() {
		if (this.fontSize != -1) {
			this.setFontSize(this.fontSize);
		}
	}

	/**
	 * Changes the font size.
	 * <p>
	 * @param fontSize the font size
	 */
	public void setFontSize(final int fontSize) {
		if (this.label == null) {
			return;
		}
		int iSize = fontSize;
		if (this.incrementalFont) {
			if (this.originalSize == -1) {
				this.originalSize = this.label.getFont().getSize();
			}
			iSize = this.originalSize + iSize;
		}

		Label.logger.debug("Setting font size: {}", iSize);

		try {
			if (this.label != null) {
				this.label.setFont(this.label.getFont().deriveFont((float) iSize));
			}
		} catch (final Exception e) {
			Label.logger.error("Error setting the font size : ", e);
		}
	}

	/**
	 * Changes the font color.
	 * <p>
	 * @param fontColor the font color
	 */
	public void setFontColor(final Color fontColor) {
		try {
			this.label.setForeground(fontColor);
		} catch (final Exception e) {
			Label.logger.error("Error setting font color :", e);
		}
	}

	/**
	 * Get the alignment layout constraints.
	 * <p>
	 * @param parentLayout the layout to check
	 * @return the constraints
	 */
	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			// It is necessary to know the component alignment
			int totalAlignment = this.alignment;
			switch (this.alignmentV) {
			case GridBagConstraints.NORTH:
				totalAlignment = this.alignment;
				break;
			case GridBagConstraints.CENTER:
				switch (this.alignment) {
				case GridBagConstraints.NORTH:
					totalAlignment = GridBagConstraints.CENTER;
					break;
				case GridBagConstraints.NORTHEAST:
					totalAlignment = GridBagConstraints.EAST;
					break;
				case GridBagConstraints.NORTHWEST:
					totalAlignment = GridBagConstraints.WEST;
					break;
				default:
					break;
				}
				break;
			case GridBagConstraints.SOUTH:
				switch (this.alignment) {
				case GridBagConstraints.NORTH:
					totalAlignment = GridBagConstraints.SOUTH;
					break;
				case GridBagConstraints.NORTHEAST:
					totalAlignment = GridBagConstraints.SOUTHEAST;
					break;
				case GridBagConstraints.NORTHWEST:
					totalAlignment = GridBagConstraints.SOUTHWEST;
					break;
				default:
					break;
				}
				break;
			default:
				totalAlignment = this.alignment;
				break;
			}

			return new GridBagConstraints(0, 0, 1, 1, this.dim, 0, totalAlignment, GridBagConstraints.NONE,
					new Insets(Label.DEFAULT_PARENT_MARGIN, Label.DEFAULT_PARENT_MARGIN, Label.DEFAULT_PARENT_MARGIN,
							Label.DEFAULT_PARENT_MARGIN),
					0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Changes the component locale.
	 * <p>
	 * @param locale the locale
	 */
	@Override
	public void setComponentLocale(final Locale locale) {
		this.label.setLocale(locale);
	}

	/**
	 * Changes the resource bundle.
	 * <p>
	 * @param resources the resource
	 */
	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resourceFile = resources;
		try {
			if (resources != null) {
				this.label.setText(resources.getString(this.textLabel));
			} else {
				this.label.setText(this.textLabel);
			}
		} catch (final Exception e) {
			this.label.setText(this.textLabel);
			Label.logger.debug(null, e);
		}
	}

	/**
	 * Introduces in a List the textLabel.
	 * <p>
	 * @return a List where textLabel is introduced.
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.textLabel);
		return v;
	}

	/**
	 * Gets the attribute paramameter.
	 * <p>
	 * @return the value specified in 'attr'
	 */
	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Enables o disables the visualization permissions.
	 * <p>
	 * @param visible the condition of visualization
	 */
	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(visible);
	}

	/**
	 * Activates the label component.
	 * <p>
	 * @param enabled the boolean to activate
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		super.setEnabled(enabled);
	}

	/**
	 * Initializes the permissions.
	 */
	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
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
	 * Checks whether visible permissions are active.
	 * <p>
	 * @return the visibility condition
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
					Label.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					Label.logger.error(null, e);
				} else {
					Label.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks whether the component has permission to stay enabled.
	 * <p>
	 * @return the boolean result of checking permissions
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.permissionsActived == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.permissionsActived = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.permissionsActived != null) {
					manager.checkPermission(this.permissionsActived);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Label.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					Label.logger.debug(null, e);
				} else {
					Label.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Sets the parent form.
	 * <p>
	 * @param f the parent form
	 */
	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	/**
	 * Updates the UI property. If {@link #BOLD} is true, also it calls to {@link #updateFont()}
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		if (this.bold) {
			this.setBold(this.bold);
		}
		this.updateFont();
	}

	/**
	 * Changes the capitalization to bold.
	 * <p>
	 * @param bold sets bold capitalization.
	 */
	public void setBold(final boolean bold) {
		try {
			if (bold) {
				this.label.setFont(this.label.getFont().deriveFont(Font.BOLD));
			} else {
				this.label.setFont(this.label.getFont().deriveFont(Font.PLAIN));
			}
			this.bold = bold;
		} catch (final Exception e) {
			Label.logger.error("Error set BOLD : ", e);
		}
	}

	/**
	 * Sets the text to a label.
	 * <p>
	 * @param text the text to put in the label
	 */
	public void setText(final String text) {
		this.textLabel = text;
		this.setResourceBundle(this.resourceFile);
	}

	/**
	 * Gets the text from a label.
	 * <p>
	 * @return the reference for a display area
	 */
	public String getText() {
		return this.label.getText();
	}

	/**
	 * Gets the reference of a label.
	 * <p>
	 * @return the reference for a display area
	 */
	public JLabel getLabel() {
		return this.label;
	}

	/**
	 * Checks whether component is restricted.
	 * <p>
	 * @return in this case, it always returns false
	 */
	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public boolean isFocusable() {
		return false;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
