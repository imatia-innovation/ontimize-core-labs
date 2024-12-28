package com.ontimize.gui.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.HasHelpIdComponent;
import com.ontimize.gui.SecureElement;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

public class ToggleButton extends JToggleButton
implements com.ontimize.gui.field.FormComponent, AccessForm, Freeable, MouseListener, SecureElement,
IdentifiedElement, HasHelpIdComponent {

	private static final Logger logger = LoggerFactory.getLogger(ToggleButton.class);

	public static final String TIP = "tip";

	public static final String TEXT = "text";

	public static final String KEY = "key";

	public static final String VALIGN = "valign";

	public static final String ALIGN = "align";

	/**
	 * The text to show in button. By default, null.
	 */
	protected String text = null;

	/**
	 * The alignment. By default, centered.
	 */
	protected int alignment = GridBagConstraints.NORTH;

	/**
	 * The vertical alignment. By default, at top.
	 */
	protected int alignmentV = GridBagConstraints.NORTH;

	/**
	 * The key to manages the button. By default, null.
	 */
	protected String buttonKey = null;

	/**
	 * Condition about focusable. By default, true.
	 */
	protected boolean focusable = true;

	/**
	 * The tooltip key.
	 */
	protected String tooltip = null;

	/**
	 * A condition to check if tooltip is specified. By default, false.
	 */
	protected boolean specifiedTooltip = false;

	/**
	 * The roll over condition. By default, false.
	 */
	protected boolean rollover = false;

	/**
	 * A reference to resource bundle file. By default, null.
	 */
	protected ResourceBundle resourcesFileName = null;

	/**
	 * A reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * A visible permission reference. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * A enable permission reference. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * The label size. By default, -1.
	 */
	protected int labelSize = -1;

	/**
	 * The preferred height. By default, -1.
	 */
	protected int preferredHeight = -1;

	/**
	 * The font size. By default, 12 pt.
	 */
	protected int fontSize = 12;

	/**
	 * The font color. By default, black.
	 */
	protected Color fontColor = Color.black;

	/**
	 * The bold font condition. By default, false.
	 */
	protected boolean bold = false;

	/**
	 * The text to show when key pressed from keyboard. By default, null.
	 */
	protected String keyStrokeText = null;

	/**
	 * The help identifier. By default, null.
	 */
	protected String helpId = null;

	/**
	 * The icon reference. By default, null.
	 */
	protected String icon = null;

	/**
	 * The reference to icon with alt key pressed. By default, null.
	 */
	protected String altIcon = null;

	/**
	 * The reference to text with alt key pressed. By default, null.
	 */
	protected String alttext = null;

	/**
	 * The reference to tip with alt key pressed. By default, null.
	 */

	protected String altTip = null;

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	protected boolean expand = false;

	protected boolean dimtext = false;

	protected double weighty = 0;

	protected double weightx = 0.01;

	protected Insets insets = new Insets(1, 1, 1, 1);

	public ToggleButton(final Map parameters) throws Exception {
		super("");
		this.init(parameters);
		super.setText(this.text);
		this.updateTip();
		this.addMouseListener(this);
		this.installHelpId();
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			// Specifies the component alignment.
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

			int fill = GridBagConstraints.NONE;

			if (this.expand && this.dimtext) {
				fill = GridBagConstraints.BOTH;
				this.weighty = 1.0;
				this.weightx = 1.0;
			} else if (this.expand) {
				this.weighty = 1.0;
				fill = GridBagConstraints.VERTICAL;
			} else if (this.dimtext) {
				this.weightx = 1.0;
				fill = GridBagConstraints.HORIZONTAL;
			}

			return new GridBagConstraints(0, 0, 1, 1, this.weightx, this.weighty, totalAlignment, fill, this.insets, 0,
					0);
		} else {
			return null;
		}
	}

	@Override
	public void init(final Map parameters) throws Exception {
		this.setRolloverParameter(parameters);
		this.setFocusableParameter(parameters);
		this.setHelpIdParameter(parameters);
		this.setTextParameter(parameters);
		this.setAlignmentParameter(parameters);
		this.setVAlignmentParameter(parameters);
		this.setTipParameter(parameters);
		this.setKeyParameter(parameters);
		this.setIconAlignParameter(parameters);
		this.setTextAlignParameter(parameters);
		this.setIconParameter(parameters);
		this.setMarginParameter(parameters);
		this.setLabelSizeParameter(parameters);
		this.setHeightParameter(parameters);
		this.setMnemonicParameter(parameters);
		this.setBoldParameter(parameters);
		this.setBorderParameter(parameters);
		this.setFontSizeParameter(parameters);
		this.setFontColorParameter(parameters);
		this.setAltIconParameter(parameters);
		this.setAltTextParameter(parameters);
		this.setAltTipParameter(parameters);
		this.setOpaqueParameter(parameters);
		this.setBorderVisibleParameter(parameters);
		// Parameter expand
		this.expand = ParseUtils.getBoolean((String) parameters.get(Button.EXPAND), false);
		this.setDimParameter(parameters);
		this.setFont(ParseUtils.getFont((String) parameters.get("font"), this.getFont()));
		this.setFocusPainted(ParseUtils.getBoolean((String) parameters.get("paintfocus"), true));
		// pressedicon can be an icon path or 'yes' (to say not delete this
		// parameter).
		// If it is 'yes' then an icon with the same name that the icon
		// parameter + _pressed must be exist
		final Icon pressedIcon = ParseUtils.getPressedImageIcon((String) parameters.get("pressedicon"),
				(String) parameters.get("icon"), null);
		this.setPressedIconParameter(pressedIcon);
		this.setSelectedIconParameter(parameters);
		this.setDisableIconParameter(parameters);
		this.installHighlight(parameters);
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDisableIconParameter(final Map parameters) {
		final Icon disabledIcon = ParseUtils.getDisabledImageIcon((String) parameters.get("disabledicon"),
				(String) parameters.get("icon"), null);
		if (disabledIcon != null) {
			this.setDisabledIcon(disabledIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setSelectedIconParameter(final Map parameters) {
		final Icon selectedIcon = ParseUtils.getImageIcon((String) parameters.get("selectedicon"), null);
		if (selectedIcon != null) {
			this.setSelectedIcon(selectedIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param pressedIcon
	 */
	protected void setPressedIconParameter(final Icon pressedIcon) {
		if (pressedIcon != null) {
			this.setPressedIcon(pressedIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setDimParameter(final Map parameters) {
		final String dim = ParseUtils.getString((String) parameters.get(Button.DIM), "no");
		if ("text".equalsIgnoreCase(dim)) {
			this.dimtext = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderVisibleParameter(final Map parameters) {
		if (!ParseUtils.getBoolean((String) parameters.get("bordervisible"), true)) {
			this.setBorderPainted(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setOpaqueParameter(final Map parameters) {
		if (!ParseUtils.getBoolean((String) parameters.get("opaque"), true)) {
			this.setOpaque(false);
			this.setContentAreaFilled(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltTipParameter(final Map parameters) {
		final Object alttip = parameters.get("alttip");
		if (alttip != null) {
			this.altTip = alttip.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltTextParameter(final Map parameters) {
		final Object alttext = parameters.get("alttext");
		if (alttext != null) {
			this.alttext = alttext.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAltIconParameter(final Map parameters) {
		final Object alticon = parameters.get("alticon");
		if (alticon != null) {
			this.altIcon = alticon.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontColorParameter(final Map parameters) {
		final Object fontcolor = parameters.get("fontcolor");
		if (fontcolor != null) {
			try {
				this.fontColor = ColorConstants.parseColor(fontcolor.toString());
				this.setFontColor(this.fontColor);
			} catch (final Exception e) {
				ToggleButton.logger.error(this.getClass().toString() + " Error 'fontcolor' parameter:" + e.getMessage(),
						e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFontSizeParameter(final Map parameters) {
		final Object fontsize = parameters.get("fontsize");
		if (fontsize != null) {
			try {
				this.fontSize = Integer.parseInt(fontsize.toString());
				this.setFontSize(this.fontSize);
			} catch (final Exception e) {
				ToggleButton.logger.error(this.getClass().toString() + " : Error 'fontsize' parameter", e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBorderParameter(final Map parameters) {
		final Object border = parameters.get("border");
		if (border != null) {
			if (border.equals("raised")) {
				this.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals("lowered")) {
				this.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setBoldParameter(final Map parameters) {
		final Object bold = parameters.get("bold");
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
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMnemonicParameter(final Map parameters) {
		final Object mnemonic = parameters.get("mnemonic");
		if ((mnemonic != null) && !mnemonic.equals("")) {
			try {
				final int mCode = Integer.parseInt(mnemonic.toString());
				this.setMnemonic(mCode);
			} catch (final Exception e) {
				ToggleButton.logger
				.error(this.getClass().toString() + ". Error: 'mnemonic' parameter: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHeightParameter(final Map parameters) {
		final Object height = parameters.get("height");
		if (height == null) {
		} else {
			try {
				this.preferredHeight = Integer.parseInt(height.toString());
			} catch (final Exception e) {
				ToggleButton.logger.error("Error: 'height' parameter: " + height.toString(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setLabelSizeParameter(final Map parameters) {
		final Object labelSize = parameters.get("labelsize");
		if (labelSize == null) {
		} else {
			try {
				final Integer tamInteger = new Integer(labelSize.toString());
				this.labelSize = tamInteger.intValue();
			} catch (final Exception e) {
				ToggleButton.logger.error("Error:'labelsize' parameter: " + labelSize.toString(), e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setMarginParameter(final Map parameters) {
		final Object margin = parameters.get("margin");
		if (margin != null) {
			try {
				this.setMargin(ApplicationManager.parseInsets((String) margin));
			} catch (final Exception e) {
				ToggleButton.logger.error(this.getClass().toString() + " : " + this.buttonKey
						+ ": Error: 'margin' parameter " + e.getMessage(), e);
				if (ApplicationManager.DEBUG) {
					ToggleButton.logger.error(null, e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setIconParameter(final Map parameters) {
		// Parameter 'icon'
		final Object icon = parameters.get("icon");
		if (icon == null) {
			this.setMargin(new Insets(2, 5, 2, 5));
		} else {
			final String sIconFile = icon.toString();
			this.icon = sIconFile;
			if (sIconFile.equals("images/search.png") || sIconFile.equals("images/insert.png")
					|| sIconFile.equals("images/update.png")
					|| sIconFile.equals("images/delete_document.png")) {
				final ImageIcon imIcon = ImageManager.getIcon(sIconFile);
				if (imIcon != null) {
					this.setIcon(imIcon);
					this.setMargin(new Insets(2, 4, 2, 4));
				}
			} else {
				final ImageIcon buttonIcon = ImageManager.getIcon(sIconFile);
				if (buttonIcon != null) {
					this.setIcon(buttonIcon);
					this.setMargin(new Insets(2, 4, 2, 4));
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextAlignParameter(final Map parameters) {
		// Parameter 'textalign'
		final Object textalign = parameters.get("textalign");
		if (textalign == null) {
			this.setHorizontalAlignment(SwingConstants.CENTER);
		} else {
			if (textalign.toString().equalsIgnoreCase("right")) {
				this.setHorizontalAlignment(SwingConstants.RIGHT);
			} else if (textalign.toString().equalsIgnoreCase("left")) {
				this.setHorizontalAlignment(SwingConstants.LEFT);
			} else {
				this.setHorizontalAlignment(SwingConstants.CENTER);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setIconAlignParameter(final Map parameters) {
		// Parameter 'iconalign'
		final Object iconalign = parameters.get("iconalign");
		if (iconalign == null) {
			this.setHorizontalTextPosition(SwingConstants.RIGHT);
		} else {
			if (iconalign.toString().equalsIgnoreCase("right")) {
				this.setHorizontalTextPosition(SwingConstants.LEFT);
			} else if (iconalign.toString().equalsIgnoreCase("top")) {
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setVerticalTextPosition(SwingConstants.BOTTOM);
			} else if (iconalign.toString().equalsIgnoreCase("bottom")) {
				this.setHorizontalTextPosition(SwingConstants.CENTER);
				this.setVerticalTextPosition(SwingConstants.TOP);
			} else {
				this.setHorizontalTextPosition(SwingConstants.RIGHT);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setKeyParameter(final Map parameters) {
		// Parameter 'key'
		final Object oKey = parameters.get(ToggleButton.KEY);
		if (oKey == null) {
			ToggleButton.logger.debug(this.getClass().toString() + " 'key' parameter is required");
		} else {
			this.buttonKey = oKey.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTipParameter(final Map parameters) {
		// Parameter 'tip'
		final Object tip = parameters.get(ToggleButton.TIP);
		if (tip == null) {
			this.tooltip = this.text;
		} else {
			this.tooltip = tip.toString();
			this.specifiedTooltip = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setVAlignmentParameter(final Map parameters) {
		final Object vAlign = parameters.get(ToggleButton.VALIGN);
		if (vAlign == null) {
		} else {
			if (vAlign.equals("center")) {
				this.alignmentV = GridBagConstraints.CENTER;
			} else {
				if (vAlign.equals("bottom")) {
					this.alignmentV = GridBagConstraints.SOUTH;
				} else {
					this.alignmentV = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setAlignmentParameter(final Map parameters) {
		// Parameter 'alignment'
		final Object oAlignment = parameters.get(ToggleButton.ALIGN);
		if (oAlignment == null) {
			this.alignment = GridBagConstraints.NORTH;
		} else {
			if (oAlignment.equals("left")) {
				this.alignment = GridBagConstraints.NORTHWEST;
			} else {
				if (oAlignment.equals("right")) {
					this.alignment = GridBagConstraints.NORTHEAST;
				} else {
					this.alignment = GridBagConstraints.NORTH;
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setTextParameter(final Map parameters) {
		// Parameter 'text'
		final Object oButtonText = parameters.get(ToggleButton.TEXT);
		if (oButtonText == null) {
		} else {
			this.text = oButtonText.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setHelpIdParameter(final Map parameters) {
		final Object helpid = parameters.get("helpid");
		if (helpid != null) {
			this.helpId = helpid.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setFocusableParameter(final Map parameters) {
		final Object focusable = parameters.get("focusable");
		if (focusable == null) {
		} else {
			if (focusable.toString().equalsIgnoreCase("no")) {
				this.focusable = false;
				if (ApplicationManager.DEBUG) {
					ToggleButton.logger.debug(this.getClass().toString() + " " + this.getKey() + "Focusable: no");
				}
			} else {
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param parameters
	 */
	protected void setRolloverParameter(final Map parameters) {
		final Object rollover = parameters.get("rollover");
		if (rollover == null) {
			this.setRollover(false);
		} else {
			if (rollover.toString().equalsIgnoreCase("yes")) {
				this.setRollover(true);
			} else {
				this.setRollover(false);
			}
		}
	}

	/**
	 * Gets the button attribute.
	 * <p>
	 * @return the button key
	 */
	@Override
	public Object getAttribute() {
		return this.buttonKey;
	}

	protected void installHighlight(final Map params) {
		if (!ParseUtils.getBoolean((String) params.get("opaque"), true)
				&& ParseUtils.getBoolean((String) params.get("highlight"), false)) {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					if (ToggleButton.this.isEnabled()) {
						ToggleButton.this.setOpaque(true);
						ToggleButton.this.setContentAreaFilled(true);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					ToggleButton.this.setOpaque(false);
					ToggleButton.this.setContentAreaFilled(false);
				}
			});
		}
	}

	/**
	 * Gets the button key.
	 * <p>
	 * @return the buttonkey parameter
	 */
	public String getKey() {
		return this.buttonKey;
	}

	/**
	 * Sets the bold condition.
	 * <p>
	 * @param bold the bold condition.
	 */
	public void setBold(final boolean bold) {
		try {
			if (bold) {
				this.setFont(this.getFont().deriveFont(Font.BOLD));
			} else {
				this.setFont(this.getFont().deriveFont(Font.PLAIN));
			}
			this.bold = bold;
		} catch (final Exception e) {
			ToggleButton.logger.error(this.getClass().toString() + " : Error establishing bold", e);
			if (ApplicationManager.DEBUG) {
				ToggleButton.logger.error(null, e);
			}
		}
	}

	/**
	 * Sets the font size.
	 * <p>
	 * @param fontSize the font size
	 */
	public void setFontSize(final int fontSize) {
		try {
			this.setFont(this.getFont().deriveFont((float) fontSize));
		} catch (final Exception e) {
			ToggleButton.logger.error(this.getClass().toString() + " : Error establishing font size", e);
			if (ApplicationManager.DEBUG) {
				ToggleButton.logger.error(null, e);
			}
		}
	}

	/**
	 * Sets the font color.
	 * <p>
	 * @param fontColor the font color
	 */
	public void setFontColor(final Color fontColor) {
		try {
			this.setForeground(fontColor);
		} catch (final Exception e) {
			ToggleButton.logger.error(this.getClass().toString() + " : Error establishing font color", e);
			if (ApplicationManager.DEBUG) {
				ToggleButton.logger.error(null, e);
			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.text);
		v.add(this.tooltip);
		return v;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resourcesFileName = resources;
		String sLocaleText = null;
		try {
			final String sTextKey = this.altMode ? this.alttext : this.text;
			if (resources != null) {
				sLocaleText = resources.getString(sTextKey);
			}
			if (sLocaleText != null) {
				super.setText(sLocaleText);
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				ToggleButton.logger.debug(null, e);
			}
		}
		this.updateTip();

	}

	/**
	 * Updates the tip.
	 * <p>
	 *
	 * @see #setToolTipText(String)
	 */
	protected void updateTip() {
		final String tipKey = this.altMode ? this.altTip : this.tooltip;
		if (tipKey == null) {
			return;
		}
		try {
			if (this.resourcesFileName != null) {
				this.setToolTipText(this.getTextWithKeyStroke(this.resourcesFileName.getString(tipKey)));
			} else {
				this.setToolTipText(this.getTextWithKeyStroke(tipKey));
			}
		} catch (final Exception e) {
			this.setToolTipText(this.getTextWithKeyStroke(tipKey));
			if (ApplicationManager.DEBUG) {
				ToggleButton.logger.debug(this.getClass().toString() + " : " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Gets the keystroke text.
	 * <p>
	 * @param text the basic button text
	 * @return the text to show
	 */
	protected String getTextWithKeyStroke(final String text) {
		if (this.keyStrokeText == null) {
			return text;
		} else {
			return text + " (" + this.keyStrokeText + ")";
		}
	}

	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
	}

	@Override
	public void free() {
		this.resourcesFileName = null;
		this.tooltip = null;
		if (ApplicationManager.DEBUG) {
			ToggleButton.logger.debug(this.getClass().toString() + ": free");
		}
	}

	/**
	 * Sets rollover.
	 * <p>
	 * @param rollover the roll-over condition
	 */
	public void setRollover(final boolean rollover) {
		this.rollover = rollover;
		if (rollover) {
			this.setBorderPainted(false);
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		if ((this.rollover) && this.isEnabled()) {
			this.setBorderPainted(true);
		}
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (this.rollover) {
			this.setBorderPainted(false);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if (this.preferredHeight >= 0) {
			d.height = this.preferredHeight;
		}
		if (this.labelSize <= 0) {
			return d;
		}

		final FontMetrics fm = this.getFontMetrics(this.getFont().deriveFont(Font.PLAIN));
		int newWidth = this.labelSize * fm.charWidth('A');
		newWidth = newWidth + 20;
		d.width = newWidth;
		return d;
	}

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
	 * Checks the visible permission condition.
	 * <p>
	 * @return the condition about visibility permissions
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.buttonKey != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.buttonKey, true);
				}
			}
			try {
				// Checks to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ToggleButton.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					ToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the enabled permission condition.
	 * <p>
	 * @return the enable permission condition.
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.buttonKey != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.buttonKey, true);
				}
			}
			try {
				// Checks to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ToggleButton.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					ToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
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
	public String getHelpIdString() {
		if (this.helpId != null) {
			return this.helpId;
		}
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf(".") + 1);
		return className + "HelpId";
	}

	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			ToggleButton.logger.debug(e.getMessage(), e);
			return;
		}

	}

	/**
	 * The alt mode state condition. By default, false.
	 */
	protected boolean altMode = false;

	/**
	 * Sets the alt mode state.
	 * <p>
	 * @param mode the mode condition
	 */
	public void setAltMode(final boolean mode) {
		this.altMode = mode;

		final String iconKey = mode ? this.altIcon : this.icon;
		final String textKey = mode ? this.alttext : this.text;
		if (iconKey != null) {
			this.setIcon(ApplicationManager.getIcon(iconKey));
		}
		if (textKey != null) {
			this.setText(textKey);
		}
		this.updateTip();
	}

	/**
	 * Checks the alt mode state.
	 * <p>
	 * @return the condition
	 */
	public boolean isAltMode() {
		return this.altMode;
	}

}
