package com.ontimize.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ItemEvent;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.border.SoftButtonBorder;

/**
 * This class creates a two-state button for toolbar that shows a menu when is pressed.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ApToolBarPopupButton extends ApToolBarToggleButton {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarPopupButton.class);

	/**
	 * An instance of pop-up menu.
	 */
	protected JPopupMenu popup = new JPopupMenu();

	/**
	 * A reference for an arrow image for icon.
	 */
	protected static ImageIcon arrow = null;

	public static String popupArrowIcon = ImageManager.POPUP_ARROW;

	/**
	 * A reference for a deactivated arrow image for icon.
	 */
	protected static ImageIcon deactivateArrow = null;

	/**
	 * The condition to process the change in the state.
	 */
	protected boolean processStateChanged = true;

	protected static int arrowMargin = 6;

	/**
	 * The class constructor. Calls to <code>super</code> with parameters and makes visible the pop-up
	 * menu.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
	 */
	public ApToolBarPopupButton(final Map parameters) {
		super(parameters);
		if (ApToolBarPopupButton.arrow == null) {
			ApToolBarPopupButton.arrow = ImageManager.getIcon(ApToolBarPopupButton.popupArrowIcon);
			if (ApToolBarPopupButton.arrow != null) {
				ApToolBarPopupButton.deactivateArrow = new ImageIcon(
						GrayFilter.createDisabledImage(ApToolBarPopupButton.arrow.getImage()));
			} else {
				ApToolBarPopupButton.logger
				.debug(this.getClass().toString() + ": " + ImageManager.POPUP_ARROW + " hasn't been found");
			}
		}

		this.setHorizontalAlignment(SwingConstants.LEFT);

		this.popup.addPopupMenuListener(new PopupMenuListener() {

			@Override
			public void popupMenuCanceled(final PopupMenuEvent e) {
			}

			@Override
			public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {

			}

			@Override
			public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
				if (ApToolBarPopupButton.this.isSelected()) {
					ApToolBarPopupButton.this.processStateChanged = false;
					ApToolBarPopupButton.this.setSelected(false);
					ApToolBarPopupButton.this.setBorderPainted(false);
					ApToolBarPopupButton.this.processStateChanged = true;
				}
			}
		});
	}

	@Override
	public void add(final Component c, final Object constraints) {
		if ((c instanceof MenuElement) || (c instanceof JSeparator)) {
			this.popup.add(c, constraints);
		} else {
			super.add(c, constraints);
		}
	}

	/**
	 * Adds a pop-up component element in a specified position.
	 * <p>
	 * @param item the item to add
	 * @param index the position
	 */
	public void addPopupElement(final JComponent item, final int index) {
		this.popup.add(item, index);
	}

	/**
	 * Adds a pop-up menu item element in a specified position.
	 * <p>
	 * @param item the item to add
	 * @param index the position
	 */
	public void addPopupElement(final JMenuItem item, final int index) {
		this.popup.add(item, index);
	}

	/**
	 * Gets the pop-up element specified by parameter.
	 * <p>
	 * @param attr the attribute
	 * @return the menu element
	 */
	public MenuElement getPopupElement(final String attr) {
		for (int i = 0; i < this.getPopupComponentsCount(); i++) {
			final Component c = this.getPopupComponentAt(i);
			if ((c instanceof IdentifiedElement) && (c instanceof MenuElement)) {
				if (((IdentifiedElement) c).getAttribute().equals(attr)) {
					return (MenuElement) c;
				}

			}
		}
		return null;
	}

	@Override
	public Dimension getPreferredSize() {
		if (ApplicationManager.useOntimizePlaf) {
			ApToolBarPopupButton.arrowMargin = 2;
			if (this.dimension == null) {
				this.dimension = new Dimension(
						super.getSwingPreferredSize().width + (ApToolBarPopupButton.arrow == null ? 0
								: ApToolBarPopupButton.arrow
								.getIconWidth() + ApToolBarPopupButton.arrowMargin),
						super.getSwingPreferredSize().height);
			}
		}
		if (this.dimension == null) {
			// Create the dimension
			this.dimension = new Dimension(
					super.getPreferredSize().width + (ApToolBarPopupButton.arrow == null ? 0
							: ApToolBarPopupButton.arrow.getIconWidth() + ApToolBarPopupButton.arrowMargin),
					ApplicationToolBar.DEFAULT_BUTTON_SIZE != -1 ? ApplicationToolBar.DEFAULT_BUTTON_SIZE
							: super.getPreferredSize().height);
		}
		return this.dimension;
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

	/**
	 * Removes the pop-up element specified by parameter.
	 * <p>
	 * @param attr the attribute
	 */
	public void removePopupElement(final String attr) {
		for (int i = 0; i < this.getPopupComponentsCount(); i++) {
			final Component c = this.getPopupComponentAt(i);
			if (c instanceof IdentifiedElement) {
				if (((IdentifiedElement) c).getAttribute().equals(attr)) {
					this.popup.remove(c);
				}
				return;
			}
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		// Paint the arrow
		if (this.isEnabled()) {
			ApToolBarPopupButton.arrow.paintIcon(this, g,
					this.getWidth() - ApToolBarPopupButton.arrow.getIconWidth() - 3, (this.getHeight() / 2) - 1);
		} else {
			ApToolBarPopupButton.deactivateArrow.paintIcon(this, g,
					this.getWidth() - ApToolBarPopupButton.arrow.getIconWidth() - 3, (this.getHeight() / 2) - 1);
		}
	}

	/**
	 * Gets the number of components in this panel.
	 * <p>
	 * @return the value
	 */
	public int getPopupComponentsCount() {
		return this.popup.getComponentCount();
	}

	/**
	 * Gets the pop-up component specified by position.
	 * <p>
	 * @param i the index
	 * @return the component
	 */
	public Component getPopupComponentAt(final int i) {
		return this.popup.getComponent(i);
	}

	/**
	 * Overwrite to show the popup
	 */
	@Override
	public void itemStateChanged(final ItemEvent e) {
		if (!this.processStateChanged) {
			return;
		}
		if (e.getStateChange() == ItemEvent.SELECTED) {
			this.setBorderPainted(true);
			if (!this.popup.isVisible()) {
				this.popup.show(this, 0, this.getHeight() + 1);
			}
		} else {
			this.popup.setVisible(false);
			this.setBorderPainted(false);
			this.repaint();
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		for (int i = 0; i < this.getPopupComponentsCount(); i++) {
			final Component c = this.getPopupComponentAt(i);
			if (c instanceof Internationalization) {
				((Internationalization) c).setResourceBundle(resources);
			}
		}
		this.dimension = new Dimension(
				super.getPreferredSize().width + (ApToolBarPopupButton.arrow == null ? 0
						: ApToolBarPopupButton.arrow.getIconWidth() + ApToolBarPopupButton.arrowMargin),
				ApplicationToolBar.DEFAULT_BUTTON_SIZE != -1 ? ApplicationToolBar.DEFAULT_BUTTON_SIZE
						: super.getPreferredSize().height);
	}


	@Override
	public DataFlavor[] getTransferDataFlavors() {
		try {
			return new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
					+ ApToolBarPopupButton.class.getName() + "\"") };
		} catch (final ClassNotFoundException e) {
			logger.error(null, e);
		}
		return null;
	}

}
