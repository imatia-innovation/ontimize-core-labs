package com.ontimize.gui;

import java.awt.Container;
import java.awt.Dimension;
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
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a button bar for application.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ApToolBarSeparator extends JToolBar.Separator implements FormComponent, IdentifiedElement, SecureElement,
Freeable, Transferable, DragGestureListener, DragSourceListener {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarSeparator.class);

	protected int width = 6;

	protected int high = ApplicationToolBar.DEFAULT_BUTTON_SIZE;

	protected boolean toolbarFix = false;

	/**
	 * The reference to attribute. By default, null.
	 */
	protected String attribute = null;

	protected MenuPermission visiblePermission = null;

	protected DragSource source;

	/**
	 * The class constructor. Inits parameters and permissions.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public ApToolBarSeparator(final Map parameters) {
		this.init(parameters);
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
		final Dimension d = super.getPreferredSize();
		final Container c = this.getParent();
		if ((c != null) && (c instanceof ApplicationToolBar)) {
			final ApplicationToolBar toolBar = (ApplicationToolBar) c;
			if (((JToolBar) c).getOrientation() == SwingConstants.HORIZONTAL) {
				if (this.toolbarFix && toolBar.isToolbarHeightFix()) {
					d.height = toolBar.getHeight();
				} else {
					d.height = this.high;
				}
				d.width = this.width;
			} else {
				d.height = this.width;
				if (this.toolbarFix) {
					d.width = toolBar.getHeight();
				} else {
					d.width = this.high != -1 ? this.high : d.height;
				}
			}
		}
		return d;
	}

	// protected void paintComponent(Graphics g) {
	// super.paintComponent(g);
	// Container c = this.getParent();
	// if (c != null && c instanceof JToolBar) {
	// if (((JToolBar) c).getOrientation() == JToolBar.HORIZONTAL) {
	// g.setColor(Color.darkGray);
	// int x = (int) ((this.getWidth() / 2.0) - 1.0);
	// g.drawLine(x, 2, x, this.getHeight() - 2);
	// g.setColor(Color.white);
	// g.drawLine(x + 1, 2, x + 1, this.getHeight() - 2);
	// } else {
	// g.setColor(Color.darkGray);
	// int y = (int) ((this.getHeight() / 2.0) - 1.0);
	// g.drawLine(2, y, this.getWidth() - 2, y);
	// g.setColor(Color.white);
	// g.drawLine(2, y + 1, this.getWidth() - 2, y + 1);
	// }
	// }
	// }

	@Override
	public Object getConstraints(final LayoutManager layout) {
		return null;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {

	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector(0);
		return v;
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Initializes parameters and selects the bundle equals to class {@link ResourceBundle}, adding a
	 * suffix to basic file name.
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
	 *        <td>no</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>width</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Separator width in pixels.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>The opacity condition for the separator.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>toolbarsize</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The separator is fixed to toolbar size.</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {

		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
		} else {
			this.attribute = "aptoolbarseparator";
		}

		final Object width = parameters.get("width");
		if (width != null) {
			try {
				this.width = Integer.parseInt(width.toString());
			} catch (final Exception e) {
				ApToolBarSeparator.logger.error("Error 'width' parameter", e);
			}
		}

		if (!ParseUtils.getBoolean((String) parameters.get("opaque"), true)) {
			this.setOpaque(false);
		}

		this.toolbarFix = ParseUtils.getBoolean((String) parameters.get("toolbarsize"), false);

		if (ApplicationToolBar.DEFAULT_TOOLBAR_HEIGHT != -1) {
			this.high = ApplicationToolBar.DEFAULT_TOOLBAR_HEIGHT;
		}

	}

	@Override
	public void setVisible(final boolean vis) {
		if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new MenuPermission("visible", this.attribute, true);
				}
				try {
					// Check to show
					if (vis) {
						manager.checkPermission(this.visiblePermission);
					}
					super.setVisible(vis);
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG_SECURITY) {
						ApToolBarSeparator.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
					}
				}
			} else {
				super.setVisible(vis);
			}
		} else {
			super.setVisible(vis);
		}
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		if (ApplicationManager.CHECK_VISIBLE_PERMISSION_ON_TOOLBAR_COMPONENTS) {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (this.visiblePermission == null) {
				this.visiblePermission = new MenuPermission("visible", this.attribute, true);
			}
			try {
				manager.checkPermission(this.visiblePermission);
			} catch (final Exception e) {
				super.setVisible(false);
				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarSeparator.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * The restricted condition. By default, false.
	 */
	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		try {
			return new DataFlavor[] { new DataFlavor(
					DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + ApToolBarSeparator.class.getName() + "\"") };
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
	}

}
