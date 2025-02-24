package com.ontimize.gui;

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
import javax.swing.JButton;
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
 * This class implements an application toolbar button.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ApToolBarButton extends JButton implements FormComponent, IdentifiedElement, SecureElement, MouseListener,
IParameterItem, IDynamicItem, Freeable, Transferable, DragGestureListener, DragSourceListener {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarButton.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String TOOLBARBUTTON_NAME = "ToolBar:Button";

	/**
	 * The attribute for component. By default, null.
	 */
	protected String attribute = null;

	/**
	 * The reference to the tooltip. By default, null.
	 */
	protected String tooltip = null;

	protected String originalText = null;

	private MenuPermission visiblePermission = null;

	private MenuPermission enabledPermission = null;

	/**
	 * The form name to show
	 */
	protected String formName = null;

	/**
	 * True when the form is opened in a dialog
	 */
	protected boolean dialog = false;

	/**
	 * The name of the FormManager
	 */
	protected String formManagerName = null;

	/**
	 * The instance of the application toolbar default dimension.
	 */
	protected Dimension dimension;

	protected Insets margin;

	protected boolean dynamic;

	protected DragSource source;

	protected boolean dragEnabled = false;

	/**
	 * The class constructor. Initializes parameters, sets tooltip, margins, listeners and initializes
	 * permissions.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	public ApToolBarButton(final Map parameters) {
		this.init(parameters);
		this.setBorderPainted(false);
		this.addMouseListener(this);
		this.setMargin(new Insets(0, 0, 0, 0));
		this.setRequestFocusEnabled(false);
		this.setToolTipText(this.tooltip);
		if (this.originalText != null) {
			this.setText(this.originalText);
		}
		if (this.margin != null) {
			this.setMargin(this.margin);
		}

		this.dynamic = ParseUtils.getBoolean((String) parameters.get("dynamic"), false);

		this.initPermissions();

		this.source = new DragSource();
		this.source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
	}

	@Override
	public String getName() {
		return ApToolBarButton.TOOLBARBUTTON_NAME;
	}

	@Override
	public void updateUI() {
		super.updateUI();
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
	 * Gets the preferred size.
	 * @return the preferred size
	 */
	protected Dimension getSwingPreferredSize() {
		return super.getPreferredSize();
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		if (this.isEnabled()) {
			this.setBorderPainted(true);
		}
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		this.setBorderPainted(false);
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

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(this.attribute);
		return v;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the Map with parameters
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
	 *        <td>The attribute to manage the separator.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>tip</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The key for button tip.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>icon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The URL for icon button.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>paintfocus</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td></td>
	 *        <td>Sets the focus paint property. See AbstractButton - setFocusPainted(boolean).</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opacity condition for buttons.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>highlight</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the highlight property when mouse is entered. See
	 *        {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires
	 *        opaque='no'.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>form</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The form name to be opened</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>showdialog</td>
	 *        <td>yes/no</td>
	 *        <td>If the 'form' parameter is established the default values is 'yes'.Otherwise the
	 *        default value is 'no'</td>
	 *        <td>no</td>
	 *        <td>Established whether the form is opened in a dialog</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		// Attribute
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
			this.setActionCommand(this.attribute);
		}

		final Object action = parameters.get("action");
		if (action != null) {
			this.setActionCommand(action.toString());
		}


		// Tip
		final Object tip = parameters.get("tip");
		if (tip == null) {
			this.tooltip = this.attribute;
		} else {
			this.tooltip = tip.toString();
		}

		// Text
		final Object text = parameters.get("text");
		if (text != null) {
			this.originalText = text.toString();
		}

		// Icon
		Object icon = parameters.get("icon");
		if (icon != null) {
			this.setHorizontalAlignment(SwingConstants.CENTER);
			final String sIconFile = icon.toString();
			final ImageIcon imageIcon = ImageManager.getIcon(sIconFile);
			if (imageIcon != null) {
				this.setIcon(imageIcon);
			}
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

		icon = parameters.get("pressedicon");
		if (icon != null) {
			final ImageIcon pressedIcon = ImageManager.getIcon(icon.toString());
			if (pressedIcon != null) {
				this.setPressedIcon(pressedIcon);
			}
		} else if (this.getIcon() != null) {
			final ImageIcon pressedIcon = ImageManager.darker((ImageIcon) this.getIcon());
			if (pressedIcon != null) {
				this.setPressedIcon(pressedIcon);
			}
		}

		icon = parameters.get("rolloverselectedicon");
		if (icon != null) {
			final ImageIcon imageIcon = ImageManager.getIcon(icon.toString());
			if (imageIcon != null) {
				this.setRolloverSelectedIcon(imageIcon);
			}
		}

		final boolean rollOverEnabled = ParseUtils.getBoolean((String) parameters.get("rollover"), this.isRolloverEnabled());
		this.setRolloverEnabled(rollOverEnabled);

		final Object margin = parameters.get("margin");
		if (margin != null) {
			this.margin = ApplicationManager.parseInsets(margin.toString());
		}

		if (!ParseUtils.getBoolean((String) parameters.get("opaque"), true)) {
			this.setOpaque(false);
			this.setContentAreaFilled(false);
		}
		this.setFocusPainted(ParseUtils.getBoolean((String) parameters.get("paintfocus"), false));
		this.installHighlight(parameters);

		this.setBackground(ParseUtils.getColor((String) parameters.get("bgcolor"), this.getBackground()));

		boolean defaultShowDialog = false;
		final Object formName = parameters.get("form");

		if ((formName != null) && (formName instanceof String)) {
			this.setFormName((String) formName);
			defaultShowDialog = true;
		}

		this.formManagerName = ParseUtils.getString((String) parameters.get("formmanager"), (String) attr);
		this.dialog = ParseUtils.getBoolean((String) parameters.get("showdialog"), defaultShowDialog);

	}

	@Override
	public String getFormName() {
		return this.formName;
	}

	public void setFormName(final String formName) {
		this.formName = formName;
	}

	@Override
	public boolean isDialog() {
		return this.dialog;
	}

	public void setDialog(final boolean dialog) {
		this.dialog = dialog;
	}

	@Override
	public String getFormManagerName() {
		return this.formManagerName;
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
					// Check to show
					if (visible) {
						manager.checkPermission(this.visiblePermission);
					}
					super.setVisible(visible);
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG_SECURITY) {
						ApToolBarButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
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
				// Check to enable
				if (enabled && (manager != null)) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				super.setEnabled(enabled);

			} catch (final Exception e) {
				this.restricted = true;
				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarButton.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		} else {
			super.setEnabled(enabled);
		}
	}

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
					ApToolBarButton.logger.debug(null, e);
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
						ApToolBarButton.logger.debug(null, e);
					}
				}
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

	@Override
	public void setComponentLocale(final Locale l) {
	}

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	public String getOriginalText() {
		return this.originalText;
	}

	protected void installHighlight(final Map params) {
		if (!ParseUtils.getBoolean((String) params.get("opaque"), true)
				&& ParseUtils.getBoolean((String) params.get("highlight"), false)) {
			this.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					if (ApToolBarButton.this.isEnabled()) {
						ApToolBarButton.this.setOpaque(true);
						ApToolBarButton.this.setContentAreaFilled(true);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					ApToolBarButton.this.setOpaque(false);
					ApToolBarButton.this.setContentAreaFilled(false);
				}
			});
		}
	}

	@Override
	public boolean isDynamic() {
		return this.dynamic;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		try {
			return new DataFlavor[] { new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ApToolBarButton.class.getName() + "\"") };
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
