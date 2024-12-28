package com.ontimize.util.swing;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.ontimize.gui.ApToolBarButton;
import com.ontimize.gui.ApToolBarNavigator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.ButtonSelectionInternationalization;
import com.ontimize.gui.images.ImageManager;

public class ButtonSelection extends JComponent {

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String BUTTON_SELECTION = "ButtonSelection";

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2062EN
	 */
	public static final String MENU_BUTTON_SELECTION = "MenuButtonSelection";

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2072EN
	 */
	public static final String TOOLBAR_NAVIGATOR_MENU_BUTTON_SELECTION = "ToolbarNavigatorMenuButtonSelection";

	public static Boolean defaultMenuButtonSelectionPaintFocus;

	public static Boolean defaultMenuButtonSelectionContentAreaFilled;

	public static Boolean defaultMenuButtonSelectionCapable;

	public static Boolean defaultButtonSelectionPaintFocus;

	public static Boolean defaultButtonSelectionContentAreaFilled;

	public static Boolean defaultButtonSelectionCapable;

	public static int defaultArrowButtonWidth = 8;

	protected JButton button = null;

	protected JButton menuButton = null;

	protected JList menuList = null;

	protected boolean highlight = false;

	protected Action actionMenu = null;

	protected class RolloverHandler extends MouseAdapter {

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (ButtonSelection.this.button.isEnabled()) {
				ButtonSelection.this.button.setBorderPainted(true);
				ButtonSelection.this.menuButton.setBorderPainted(true);
				ButtonSelection.this.repaint();
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			if (ButtonSelection.this.button.isEnabled()) {
				ButtonSelection.this.button.setBorderPainted(false);
				ButtonSelection.this.menuButton.setBorderPainted(false);
				ButtonSelection.this.repaint();
			}
		}

	}

	public ButtonSelection(final boolean highlight) {
		this(highlight, false);
	}

	public ButtonSelection(final boolean highlight, final boolean rollover) {
		this.button = new EButtonSelection();
		this.init(highlight);
	}

	public ButtonSelection(final String text, final boolean highlight) {
		this.button = new EButtonSelection(text);
		this.init(highlight);
	}

	public ButtonSelection(final String text, final Icon icon, final boolean highlight) {
		this.button = new EButtonSelection(text, icon);
		this.init(highlight);
	}

	public ButtonSelection(final Action a, final boolean highlight) {
		this.button = new EButtonSelection(a);
		this.init(highlight);
	}

	public ButtonSelection(final Icon icon, final boolean highlight) {
		this.button = new EButtonSelection(icon);
		this.init(highlight);
	}

	public ButtonSelection() {
		this(false);
	}

	public ButtonSelection(final Map h) {
		this.button = new EButtonSelection();
	}

	@Override
	public void repaint() {
		super.repaint();

		final Container c = this.getParent();
		if (c != null) {
			c.repaint();
		}
	}

	protected void init(final boolean highlight) {
		this.highlight = highlight;
		this.menuButton = new EArrowButtonSelection();

		// menuButton.setFocusPainted(false);
		// button.setFocusPainted(false);
		// button.setDefaultCapable(false);
		// menuButton.setDefaultCapable(false);
		this.changeButtons();
		if (highlight) {
			this.menuButton.setBorderPainted(false);
			this.button.setBorderPainted(false);
			final RolloverHandler handler = new RolloverHandler();
			this.addMouseListener(handler);
			this.button.addMouseListener(handler);
			this.menuButton.addMouseListener(handler);
		}

		if (this.button.getIcon() != null) {
			this.button.setRolloverIcon(ImageManager.brighter((ImageIcon) this.button.getIcon()));
		}

		final Insets inset = this.menuButton.getMargin();
		inset.right = 1;
		inset.left = 1;
		this.menuButton.setMargin(inset);

		this.setLayout(new GridBagLayout());
		this.add(this.button, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.menuButton, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
		this.actionMenu = new Action();
		this.menuButton.addActionListener(this.actionMenu);
		this.setEnabled(true);

		this.button.setName(ButtonSelection.BUTTON_SELECTION);
		this.menuButton.setName(ButtonSelection.MENU_BUTTON_SELECTION);
	}

	protected void changeButtons() {
		// Selection Button configuration...
		if (ButtonSelection.defaultButtonSelectionCapable != null) {
			this.button.setDefaultCapable(ButtonSelection.defaultButtonSelectionCapable.booleanValue());
			this.menuButton.setDefaultCapable(ButtonSelection.defaultButtonSelectionCapable.booleanValue());
		} else {
			this.button.setDefaultCapable(false);
			this.menuButton.setDefaultCapable(false);
		}

		if (ButtonSelection.defaultButtonSelectionPaintFocus != null) {
			this.button.setFocusPainted(ButtonSelection.defaultButtonSelectionPaintFocus.booleanValue());
			this.menuButton.setFocusPainted(ButtonSelection.defaultButtonSelectionPaintFocus.booleanValue());
		} else {
			this.button.setFocusPainted(false);
			this.menuButton.setFocusPainted(false);
		}
	}

	public void setMenu(final JPopupMenu menu) {
		((EArrowButtonSelection) this.menuButton).setMenu(menu);
	}

	public JPopupMenu getMenu() {
		return ((EArrowButtonSelection) this.menuButton).getMenu();
	}

	public void setMenuList(final JList menuList) {
		this.menuList = menuList;
	}

	public JList getMenuList() {
		return this.menuList;
	}

	@Override
	public void setToolTipText(final String text) {
		this.button.setToolTipText(text);
	}

	protected class Action extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ButtonSelection.this.getMenu() != null) {
				if (ButtonSelection.this.highlight) {
					ButtonSelection.this.menuButton.setBorderPainted(false);
					ButtonSelection.this.button.setBorderPainted(false);
				}
				ButtonSelection.this.getMenu()
				.show(ButtonSelection.this.button, 0, ButtonSelection.this.button.getHeight());
			} else if (ButtonSelection.this.menuList != null) {
				if (ButtonSelection.this.highlight) {
					ButtonSelection.this.menuButton.setBorderPainted(false);
					ButtonSelection.this.button.setBorderPainted(false);
				}

			}
		}

	}

	public void setRolloverEnabled(final boolean roll) {
		this.button.setRolloverEnabled(roll);
		this.menuButton.setRolloverEnabled(roll);
	}

	public void setIcon(final Icon icon) {
		this.button.setIcon(icon);
	}

	public void setText(final String text) {
		this.button.setText(text);
	}

	public void addActionListener(final ActionListener a) {
		this.button.addActionListener(a);
	}

	public void addActionMenuListener(final ActionListener action) {
		this.menuButton.removeActionListener(this.actionMenu);
		this.menuButton.addActionListener(action);
	}

	public void setMargin(final Insets insets) {
		this.button.setMargin(insets);
		insets.right = 1;
		insets.left = 1;
		this.menuButton.setMargin(insets);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		this.menuButton.setEnabled(enabled);
		this.button.setEnabled(enabled);
	}

	public JButton getButton() {
		return this.button;
	}

	public JButton getMenuButton() {
		return this.menuButton;
	}

	public Action getActionMenu() {
		return this.actionMenu;
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension d = super.getPreferredSize();
		if (ApplicationManager.useOntimizePlaf) {
			if (this.getButton() != null) {
				d.height = this.getButton().getPreferredSize().height;
			}
		}
		return d;
	}

	protected class EButtonSelection extends JButton {

		public EButtonSelection() {
		}

		public EButtonSelection(final javax.swing.Action a) {
			super(a);
		}

		public EButtonSelection(final Icon icon) {
			super(icon);
		}

		public EButtonSelection(final String text, final Icon icon) {
			super(text, icon);
		}

		public EButtonSelection(final String text) {
			super(text);
		}

		@Override
		public String getName() {
			if (SwingUtilities.getAncestorOfClass(ApToolBarNavigator.class, this) != null) {
				return ApToolBarButton.TOOLBARBUTTON_NAME;
			}
			return ButtonSelection.BUTTON_SELECTION;
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (ButtonSelection.defaultButtonSelectionContentAreaFilled != null) {
				super.setContentAreaFilled(ButtonSelection.defaultButtonSelectionContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

		@Override
		public void setDefaultCapable(final boolean defaultCapable) {
			if (ButtonSelection.defaultButtonSelectionCapable != null) {
				super.setDefaultCapable(ButtonSelection.defaultButtonSelectionCapable.booleanValue());
				return;
			}
			super.setDefaultCapable(defaultCapable);
		}

		@Override
		public void setFocusPainted(final boolean b) {
			if (ButtonSelection.defaultButtonSelectionPaintFocus != null) {
				if (SwingUtilities.getAncestorOfClass(ApToolBarNavigator.class, this) != null) {
					super.setFocusPainted(b);
					return;
				}
				super.setFocusPainted(ButtonSelection.defaultButtonSelectionPaintFocus.booleanValue());
				return;
			}
			super.setFocusPainted(b);
		}

		@Override
		public Dimension getPreferredSize() {
			if (this.getParent() instanceof ButtonSelectionInternationalization) {
				return super.getPreferredSize();
			}
			return super.getPreferredSize();
		}

	}

	protected class EArrowButtonSelection extends EButtonSelection implements IMenuButton {

		protected JPopupMenu menu;

		protected Dimension dimension;

		public EArrowButtonSelection() {
			if (!ApplicationManager.useOntimizePlaf) {
				final ImageIcon icon = ImageManager.getIcon(ImageManager.POPUP_ARROW);
				this.setIcon(icon);
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
			final Dimension d = super.getPreferredSize();
			if (ApplicationManager.useOntimizePlaf) {
				if (this.dimension == null) {
					// Create the dimension
					this.dimension = new Dimension(ButtonSelection.defaultArrowButtonWidth,
							super.getPreferredSize().height);
				}
				return this.dimension;
			}
			return d;
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (ButtonSelection.defaultMenuButtonSelectionContentAreaFilled != null) {
				super.setContentAreaFilled(ButtonSelection.defaultMenuButtonSelectionContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

		@Override
		public void setDefaultCapable(final boolean defaultCapable) {
			if (ButtonSelection.defaultMenuButtonSelectionCapable != null) {
				super.setDefaultCapable(ButtonSelection.defaultMenuButtonSelectionCapable.booleanValue());
				return;
			}
			super.setDefaultCapable(defaultCapable);
		}

		@Override
		public void setFocusPainted(final boolean b) {
			if (ButtonSelection.defaultMenuButtonSelectionPaintFocus != null) {
				if (SwingUtilities.getAncestorOfClass(ApToolBarNavigator.class, this) != null) {
					super.setFocusPainted(b);
					return;
				}
				super.setFocusPainted(ButtonSelection.defaultMenuButtonSelectionPaintFocus.booleanValue());
				return;
			}
			super.setFocusPainted(b);
		}

		@Override
		public String getName() {
			if (SwingUtilities.getAncestorOfClass(ApToolBarNavigator.class, this) != null) {
				return ButtonSelection.TOOLBAR_NAVIGATOR_MENU_BUTTON_SELECTION;
			}
			return ButtonSelection.MENU_BUTTON_SELECTION;
		}

		public void setMenu(final JPopupMenu menu) {
			this.menu = menu;
		}

		@Override
		public JPopupMenu getMenu() {
			return this.menu;
		}

	}

}
