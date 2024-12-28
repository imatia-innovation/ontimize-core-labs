package com.ontimize.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.border.SoftButtonBorder;

/**
 * The class to define a button with two possible states for toolbar. Basic parameters are
 * specificated in method {@link #init}
 *
 * @author IMATIA innovation
 */
public class ApToolBarToggleButton extends JToggleButton
implements FormComponent, IdentifiedElement, SecureElement, MouseListener, ItemListener, Freeable, Transferable,
DragGestureListener, DragSourceListener {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarToggleButton.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String TOOLBARTOGGLEBUTTON_NAME = "ToolBar:ToggleButton";

	@Override
	public String getName() {
		return ApToolBarToggleButton.TOOLBARTOGGLEBUTTON_NAME;
	}

	/**
	 * The attribute parameter. By default, null.
	 */
	protected String attribute = null;

	/**
	 * The tooltip parameter. By default, null.
	 */
	protected String tooltip = null;

	protected String originalText = null;

	private MenuPermission visiblePermission = null;

	private MenuPermission enabledPermission = null;

	/**
	 * The Dimension parameter. By default, 24 x 24.
	 */
	protected Dimension dimension;

	/**
	 * The default color. By default, null.
	 */
	protected Color defaultBG = null;

	/**
	 * The condition to paint border when mouse is over button. By default, false.
	 */
	protected boolean mouseIn = false;

	protected DragSource source;

	protected boolean dragEnabled = false;

	/**
	 * Changes background and border color in function of ItemEvent.
	 * <p>
	 * param e event which indicates that an item was selected or deselected
	 */
	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			this.setBorderPainted(true);
			if (this.defaultBG == null) {
				this.defaultBG = this.getBackground();
			}
			this.setBackground(this.defaultBG.brighter());
			this.repaint();
		} else {
			if (this.mouseIn) {
				this.setBorderPainted(true);
			} else {
				this.setBorderPainted(false);
			}
			this.setBackground(this.defaultBG);
			this.repaint();
		}
	}

	/**
	 * The class constructor. It inits parameters, adds listeners and sets borders and margins.
	 * <p>
	 * @param parameters The <code>Hashtable<code> with parameters
	 */
	public ApToolBarToggleButton(final Map parameters) {
		this.init(parameters);
		this.setBorderPainted(false);
		this.addMouseListener(this);
		this.addItemListener(this);
		this.setMargin(new Insets(0, 0, 0, 0));
		this.setRequestFocusEnabled(false);
		this.setToolTipText(this.tooltip);
		this.initPermissions();
		this.source = new DragSource();
		this.source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
	}

	@Override
	public Dimension getMinimumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return this.getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		if (this.dimension == null) {
			// Create the dimension
			this.dimension = new Dimension(super.getPreferredSize().width,
					ApplicationToolBar.DEFAULT_BUTTON_SIZE != -1 ? ApplicationToolBar.DEFAULT_BUTTON_SIZE
							: super.getPreferredSize().height);
		}
		return this.dimension;
	}

	/**
	 * Gets the the preferred size.
	 * <p>
	 *
	 * @see JComponent#getPreferredSize()
	 * @return the swing preferred size
	 */
	protected Dimension getSwingPreferredSize() {
		return super.getPreferredSize();
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		if (this.isEnabled() && !this.isSelected()) {
			this.setBorderPainted(true);
		}
		this.mouseIn = true;
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		if (!this.isSelected()) {
			this.setBorderPainted(false);
		}
		this.mouseIn = false;
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
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Adds the attribute parameter to the List to translate.
	 * <p>
	 * @return the List with attribute parameter.
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		return v;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
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
	 *        <td>The attribute. It is required and it is established like actionCommand of button.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The tip key of button.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>URI for button.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The text for toggle button.</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		// Attribute parameter
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
			this.setActionCommand(this.attribute);
		}

		// Tip parameter
		final Object tip = parameters.get("tip");
		if (tip == null) {
			this.tooltip = this.attribute;
		} else {
			this.tooltip = tip.toString();
		}

		// Icon parameter
		Object icon = parameters.get("icon");
		if (icon != null) {
			this.setHorizontalAlignment(SwingConstants.CENTER);
			final String archivoIcono = icon.toString();
			final ImageIcon imageIcon = ImageManager.getIcon(archivoIcono);
			if (imageIcon != null) {
				this.setIcon(imageIcon);
			}
		}

		final String selectedIcon = ParseUtils.getString((String) parameters.get("selectedicon"), null);
		if (selectedIcon != null) {
			final ImageIcon imageIcon = ImageManager.getIcon(selectedIcon);
			this.setSelectedIcon(imageIcon);
		} else if (this.getIcon() != null) {
			this.setSelectedIcon(ImageManager.darker((ImageIcon) this.getIcon()));
		}

		icon = parameters.get("rollovericon");
		if (icon != null) {
			final ImageIcon imageIcon = ImageManager.getIcon(icon.toString());
			if (imageIcon != null) {
				this.setRolloverIcon(imageIcon);
			}
		} else if (this.getIcon() != null) {
			final ImageIcon rollOverIcon = ImageManager.brighter((ImageIcon) this.getIcon());
			if (rollOverIcon != null) {
				this.setRolloverIcon(rollOverIcon);
			}
		}

		icon = parameters.get("rolloverselectedicon");
		if (icon != null) {
			final ImageIcon imageIcon = ImageManager.getIcon(icon.toString());
			if (imageIcon != null) {
				this.setRolloverSelectedIcon(imageIcon);
			}
		} else if (this.getIcon() != null) {
			final ImageIcon rollOverIcon = (ImageIcon) this.getIcon();
			if (rollOverIcon != null) {
				this.setRolloverSelectedIcon(rollOverIcon);
			}
		}

		if (!ParseUtils.getBoolean((String) parameters.get("opaque"), true)) {
			this.setOpaque(false);
			this.setContentAreaFilled(false);
		}

		final boolean rollOverEnabled = ParseUtils.getBoolean((String) parameters.get("rollover"), this.isRolloverEnabled());
		this.setRolloverEnabled(rollOverEnabled);

		this.setFocusPainted(ParseUtils.getBoolean((String) parameters.get("paintfocus"), false));
		this.installHighlight(parameters);

		this.originalText = ParseUtils.getString((String) parameters.get("text"), null);
		if (this.originalText != null) {
			this.setText(this.originalText);
		}
	}

	/**
	 * Initializes the permissions.
	 *
	 * @see ApplicationManager#CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS
	 */
	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				this.enabledPermission = new MenuPermission("enabled", this.attribute, true);
			}
			try {
				manager.checkPermission(this.enabledPermission);
				this.restricted = false;
			} catch (final Exception e) {
				this.restricted = true;
				super.setEnabled(false);

				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
			if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new MenuPermission("visible", this.attribute, true);
				}
				try {
					manager.checkPermission(this.visiblePermission);
				} catch (final Exception e) {
					super.setVisible(false);
					if (ApplicationManager.DEBUG_SECURITY) {
						ApToolBarToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
					}
				}
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		this.defaultBG = null;
		if (this.getBorder() instanceof CompoundBorder) {
			final Border b = ((CompoundBorder) this.getBorder()).getOutsideBorder();
			if (b instanceof javax.swing.plaf.basic.BasicBorders.ButtonBorder) {
				final Border be = new SoftButtonBorder();
				final CompoundBorder bn = new CompoundBorder(be, ((CompoundBorder) this.getBorder()).getInsideBorder());
				this.setBorder(bn);
			}
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		if (this.tooltip != null) {
			if (resources != null) {
				this.setToolTipText(ApplicationManager.getTranslation(this.tooltip, resources));
			} else {
				this.setToolTipText(this.tooltip);
			}
		}
		if (this.originalText != null) {
			if (resources != null) {
				this.setText(ApplicationManager.getTranslation(this.originalText, resources));
			} else {
				this.setText(this.originalText);
			}
		}
		this.dimension = new Dimension(super.getPreferredSize().width,
				ApplicationToolBar.DEFAULT_BUTTON_SIZE != -1 ? ApplicationToolBar.DEFAULT_BUTTON_SIZE
						: super.getPreferredSize().height);
	}

	/**
	 * Sets the component locale.
	 * <p>
	 * @param l the locale to set
	 */
	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setVisible(final boolean visible) {
		if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new MenuPermission("visible", this.attribute, true);
				}
				try {
					// Checks to show
					if (visible) {
						manager.checkPermission(this.visiblePermission);
					}
					super.setVisible(visible);
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG_SECURITY) {
						ApToolBarToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
					}
				}
			} else {
				super.setVisible(visible);
			}
		} else {
			super.setVisible(visible);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				this.enabledPermission = new MenuPermission("enabled", this.attribute, true);
			}
			try {
				// checks to enable
				if ((enabled) && (manager != null)) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				super.setEnabled(enabled);
			} catch (final Exception e) {
				this.restricted = true;
				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarToggleButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		} else {
			super.setEnabled(enabled);
		}
	}

	/**
	 * The condition of restricted. By default, false.
	 */
	protected boolean restricted = false;

	/**
	 * Gets the restricted condition.
	 * <p>
	 * @return the {@link #restricted} variable value.
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	protected void installHighlight(final Map params) {
		if (!ParseUtils.getBoolean((String) params.get("opaque"), true)
				&& ParseUtils.getBoolean((String) params.get("highlight"), false)) {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					if (ApToolBarToggleButton.this.isEnabled()) {
						ApToolBarToggleButton.this.setOpaque(true);
						ApToolBarToggleButton.this.setContentAreaFilled(true);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					ApToolBarToggleButton.this.setOpaque(false);
					ApToolBarToggleButton.this.setContentAreaFilled(false);
				}
			});
		}
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		try {
			return new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
					+ ApToolBarToggleButton.class.getName() + "\"") };
		} catch (final ClassNotFoundException e) {
			logger.error(null, e);
		}
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(final DataFlavor flavor) {
		return true;
	}

	@Override
	public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return this;
	}

	@Override
	public void dragGestureRecognized(final DragGestureEvent dge) {
		this.dragEnabled = this.isEnabled();
		this.setEnabled(false);
		this.source.startDrag(dge, DragSource.DefaultMoveDrop, this, this);
	}

	@Override
	public void dragEnter(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragOver(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dropActionChanged(final DragSourceDragEvent dsde) {
	}

	@Override
	public void dragExit(final DragSourceEvent dse) {
	}

	@Override
	public void dragDropEnd(final DragSourceDropEvent dsde) {
		this.setEnabled(this.dragEnabled);
	}

}
