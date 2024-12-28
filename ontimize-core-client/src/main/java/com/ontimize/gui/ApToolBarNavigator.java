package com.ontimize.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.Border;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.security.MenuPermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.ButtonSelection;

/**
 * This class implements a navigation bar in application toolbar like a browser, to move between
 * last visited forms.
 *
 * It is composed by three buttons, to see the previous, the next and the list of recent forms.
 *
 * @author Imatia Innovation SL
 * @since 5.2057EN-1.0
 *
 */

public class ApToolBarNavigator extends JPanel implements FormComponent, IdentifiedElement, SecureElement,
Internationalization, Freeable /*
 * , HasPreferenceComponent
 */ {

	private static final Logger logger = LoggerFactory.getLogger(ApToolBarNavigator.class);

	public static boolean defaultEnabledShorcuts = true;

	public static final String PREV_BUTTON_BUNDLE_KEY = "aptoolbarnavigation.prev_button";

	public static final String NEXT_BUTTON_BUNDLE_KEY = "aptoolbarnavigation.next_button";

	public static final String LIST_BUTTON_BUNDLE_KEY = "aptoolbarnavigation.list_button";

	public static Border defaultButtonsBorder = BorderFactory.createEmptyBorder(3, 3, 3, 3);

	public static String defaultPrevIconPath = ImageManager.ARROW_LEFT_24;

	public static String defaultNextIconPath = ImageManager.ARROW_RIGHT_24;

	public static String defaultListIconPath = ImageManager.HOME_24;

	public static boolean defaultOpaque = true;

	public static boolean defaultBorderButtons = true;

	public static boolean defaultOpaqueButtons = true;

	public static boolean defaultHighlightButtons = false;

	protected String attribute;

	protected String prevButtonPath = ApToolBarNavigator.defaultPrevIconPath;

	protected Icon prevIcon;

	protected AbstractButton prevButton;

	protected String prevButtonKey = "prevButton";

	protected String nextButtonPath = ApToolBarNavigator.defaultNextIconPath;

	protected Icon nextIcon;

	protected AbstractButton nextButton;

	protected String nextButtonKey = "nextButton";

	protected Icon listIcon;

	protected ButtonSelection listButton;

	protected String listButtonKey = "listButton";

	protected String listButtonPath = ApToolBarNavigator.defaultListIconPath;

	public boolean enabledShorcuts = ApToolBarNavigator.defaultEnabledShorcuts;

	protected boolean borderbuttons;

	protected boolean opaquebuttons;

	protected boolean highlightButtons;

	protected MouseAdapter listenerHighlightButtons;

	private MenuPermission visiblePermission = null;

	protected ActionListener actionNextButton;

	protected ActionListener actionPrevButton;

	protected ActionListener actionListButton;

	protected String formManager;

	protected JPopupMenu popupMenu = new JPopupMenu();

	protected int currentSelectedItem = 0;

	/**
	 * When navigation process has not started, we must set the navigation position to the end of last
	 * visited formmanager vector.
	 */
	public boolean navigationHasStarted = false;

	public List keyBindings = new Vector();

	public ApToolBarNavigator(final Map parameters) {
		this.init(parameters);
		this.createComponent();
		this.setActionListeners();
		this.registerNavigationEvents();
	}

	public void registerNavigationEvents() {
		NavigationHandler.getInstance().addNavigationListener(new INavigationEvent() {

			@Override
			public void formManagerChanged(final NavigationEvent e) {
				if (e.getType() == NavigationEvent.FORM_MANAGER_CHANGED) {
					ApToolBarNavigator.this.checkbuttons();
					ApToolBarNavigator.this.formManager = e.getFormManager();
					ApToolBarNavigator.this.updateComboList(e.getFormManager());
					ApToolBarNavigator.this.unselectall();
					((RadioMenuItem) ApToolBarNavigator.this.listButton.getMenu().getComponent(0)).setSelected(true);
					ApToolBarNavigator.this.prevButton.setEnabled(true);
					ApToolBarNavigator.this.nextButton.setEnabled(false);
					ApToolBarNavigator.this.listButton.setEnabled(true);
				}
			}

		});
	}

	public void setActionListeners() {
		this.actionNextButton = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ApToolBarNavigator.this.moveForward();
			}
		};

		this.nextButton.addActionListener(this.actionNextButton);

		this.actionPrevButton = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ApToolBarNavigator.this.moveBack();
			}
		};

		this.prevButton.addActionListener(this.actionPrevButton);

		this.actionListButton = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!ApToolBarNavigator.this.navigationHasStarted) {
					ApToolBarNavigator.this.navigationHasStarted = true;
					NavigationHandler.getInstance()
					.setNavigationPosition(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
				}
				ApplicationManager.getApplication()
				.showFormManagerContainer(
						((MainApplication) ApplicationManager.getApplication()).panelIds.get(0).toString());
				NavigationHandler.getInstance()
				.setNavigationPosition(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
				ApToolBarNavigator.this.checkbuttons();
				ApToolBarNavigator.this.currentSelectedItem = 0;
			}
		};

		this.listButton.getButton().addActionListener(this.actionListButton);

	}

	public void moveBack() {
		if (!this.navigationHasStarted) {
			this.navigationHasStarted = true;
			NavigationHandler.getInstance()
			.setNavigationPosition(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
		}
		if (((RadioMenuItem) this.listButton.getMenu().getComponent(0)).isSelected()) {
			NavigationHandler.getInstance()
			.setNavigationPosition(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
		}
		NavigationHandler.getInstance()
		.setNavigationPosition(NavigationHandler.getInstance().getNavigationPosition() - 1);
		ApplicationManager.getApplication()
		.showFormManagerContainer((String) NavigationHandler.getInstance()
				.getLastVisitedFormManagers()
				.get(NavigationHandler.getInstance().getNavigationPosition()));
		NavigationHandler.getInstance()
		.getLastVisitedFormManagers()
		.remove(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
		this.listButton.getMenu().remove(0);
		this.checkbuttons();
		this.unselectall();
		this.currentSelectedItem++;
		// checkSelection();
		if (this.currentSelectedItem < this.listButton.getMenu().getComponentCount()) {
			((RadioMenuItem) this.listButton.getMenu().getComponent(this.currentSelectedItem)).setSelected(true);
		} else {
			this.currentSelectedItem = this.listButton.getMenu().getComponentCount() - 1;
			((RadioMenuItem) this.listButton.getMenu().getComponent(this.listButton.getMenu().getComponentCount() - 1))
			.setSelected(true);
		}

		// printNavigationList();
	}

	public void moveForward() {
		if (((RadioMenuItem) this.listButton.getMenu().getComponent(0)).isSelected()) {
			NavigationHandler.getInstance()
			.setNavigationPosition(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
		}
		NavigationHandler.getInstance()
		.setNavigationPosition(NavigationHandler.getInstance().getNavigationPosition() + 1);
		ApplicationManager.getApplication()
		.showFormManagerContainer((String) NavigationHandler.getInstance()
				.getLastVisitedFormManagers()
				.get(NavigationHandler.getInstance().getNavigationPosition()));
		NavigationHandler.getInstance()
		.getLastVisitedFormManagers()
		.remove(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
		this.listButton.getMenu().remove(0);
		this.checkbuttons();
		this.unselectall();
		this.currentSelectedItem--;
		// checkSelection();
		((RadioMenuItem) this.listButton.getMenu().getComponent(this.currentSelectedItem)).setSelected(true);
	}

	public void moveInList(final ActionEvent e) {
		final int previousFormManagerSize = NavigationHandler.getInstance().getLastVisitedFormManagers().size();
		ApplicationManager.getApplication().showFormManagerContainer(e.getActionCommand());
		if (previousFormManagerSize != NavigationHandler.getInstance().getLastVisitedFormManagers().size()) {
			NavigationHandler.getInstance()
			.getLastVisitedFormManagers()
			.remove(NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1);
			this.listButton.getMenu().remove(0);
		}
		NavigationHandler.getInstance()
		.setNavigationPosition(
				NavigationHandler.getInstance().getLastVisitedFormManagers().size()
				- this.listButton.getMenu().getComponentIndex((JRadioButtonMenuItem) e.getSource()) - 1);
		this.currentSelectedItem = this.listButton.getMenu().getComponentIndex((JRadioButtonMenuItem) e.getSource());
		this.checkbuttons();
		this.checkSelection();
	}

	/**
	 * This method gets the <code>Hashtable</code> and fixes parameters for navigation toolbar
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>prevbutton</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path for previous button icon.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>nextbutton</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path for next button icon.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>listbutton</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path for button that shows the first form</td>
	 *        </tr>
	 *        <tr>
	 *        <td>size</td>
	 *        <td></td>
	 *        <td>120;40</td>
	 *        <td>no</td>
	 *        <td>Size for component</td>
	 *        </tr>
	 *        <tr>
	 *        <td>borderbuttons</td>
	 *        <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover,
	 *        it is also allowed a border defined in #BorderManager</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The border for buttons in Form</td>
	 *        </tr>
	 *        <tr>
	 *        <td>highlightbuttons</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Sets the highlight in button property when mouse is entered. See
	 *        {@link AbstractButton#setContentAreaFilled(boolean))}. This parameter requires
	 *        opaque='no'.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaquebuttons</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Data field opacity condition for Form buttons</td>
	 *        </tr>
	 *        <tr>
	 *        <td>maxpopupelements</td>
	 *        <td></td>
	 *        <td>15</td>
	 *        <td>no</td>
	 *        <td>Maximum number of elements of popup that is displayed</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Opacity condition for toolbar</td>
	 *        </tr>
	 *
	 *        </table>
	 */

	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr != null) {
			this.attribute = attr.toString();
		}

		final Object prevButton = parameters.get("prevbutton");
		if (prevButton != null) {
			this.prevButtonPath = prevButton.toString();
		}

		final Object nextButton = parameters.get("nextbutton");
		if (nextButton != null) {
			this.nextButtonPath = nextButton.toString();
		}

		final Object listButton = parameters.get("listbutton");
		if (listButton != null) {
			this.listButtonPath = listButton.toString();
		}

		// Object size = parameters.get("size");
		// if (size != null) {
		// this.size = ApplicationManager.parseSize((String)size);
		// }

		final Object maxpopupelements = parameters.get("maxpopupelements");
		if (maxpopupelements != null) {
			NavigationHandler.getInstance().defaultNumberFormManagersShowed = Integer
					.parseInt((String) maxpopupelements);
		}

		final boolean opaque = ParseUtils.getBoolean((String) parameters.get("opaque"), ApToolBarNavigator.defaultOpaque);
		this.setOpaque(opaque);
		this.borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"),
				ApToolBarNavigator.defaultBorderButtons);
		this.opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"),
				ApToolBarNavigator.defaultOpaqueButtons);
		this.highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"),
				ApToolBarNavigator.defaultHighlightButtons);

		if (!opaque && this.highlightButtons) {
			this.listenerHighlightButtons = new MouseAdapter() {

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

	}

	public void checkSelection() {
		this.unselectall();
		if (this.listButton.getMenu().getComponentCount() == 0) {
			return;
		}
		if (this.listButton.getMenu().getComponentCount() == 1) {
			((RadioMenuItem) this.listButton.getMenu().getComponent(0)).setSelected(true);
		} else {
			((RadioMenuItem) this.listButton.getMenu().getComponent(this.currentSelectedItem)).setSelected(true);
		}
	}

	protected void unselectall() {
		for (int i = 0; i < this.listButton.getMenu().getComponentCount(); i++) {
			((RadioMenuItem) this.listButton.getMenu().getComponent(i)).setSelected(false);
		}
	}

	public void checkbuttons() {
		if (NavigationHandler.getInstance().getNavigationPosition() == 0 /*
		 * && this. navigationHasStarted
		 */) {
			this.prevButton.setEnabled(false);
			this.nextButton.setEnabled(true);
			this.listButton.setEnabled(true);
			return;
		}

		// if (NavigationHandler.getInstance().getNavigationPosition() == 0 &&
		// !this.navigationHasStarted ) {
		// prevButton.setEnabled(true);
		// nextButton.setEnabled(false);
		// listButton.setEnabled(true);
		// return;
		// }

		if (NavigationHandler.getInstance().getLastVisitedFormManagers().size() == 0) {
			this.prevButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			this.listButton.setEnabled(false);
			return;
		}

		if (NavigationHandler.getInstance()
				.getNavigationPosition() == (NavigationHandler.getInstance().getLastVisitedFormManagers().size() - 1)) {
			this.prevButton.setEnabled(true);
			this.nextButton.setEnabled(false);
			this.listButton.setEnabled(true);
			return;
		}
		this.prevButton.setEnabled(true);
		this.nextButton.setEnabled(true);
		this.listButton.setEnabled(true);
	}

	public void createComponent() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		final Map hButtonNext = new Hashtable();
		hButtonNext.put(Button.KEY, this.nextButtonKey);
		hButtonNext.put("icon", this.nextButtonPath);

		this.nextButton = new ApToolBarButton(hButtonNext);
		this.nextButton.setEnabled(false);
		this.nextButton.setMargin(new Insets(0, 0, 0, 0));
		this.nextButton.setName("ToolBar:Button");

		final Map hButtonPrev = new Hashtable();
		hButtonPrev.put(Button.KEY, this.prevButtonKey);
		hButtonPrev.put("icon", this.prevButtonPath);

		this.prevButton = new ApToolBarButton(hButtonPrev);
		this.prevButton.setEnabled(false);
		this.prevButton.setMargin(new Insets(0, 0, 0, 0));
		this.prevButton.setName("ToolBar:Button");

		final Map hButtonList = new Hashtable();
		hButtonList.put(Button.KEY, this.listButtonKey);
		this.listButton = new ButtonSelection(ImageManager.getIcon(this.listButtonPath), true);
		this.listButton.setMargin(new Insets(0, 0, 0, 0));
		this.listButton.setEnabled(false);
		this.listButton.setMenu(this.popupMenu);

		this.changeButtons(this.addButtons(), this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
	}

	public List addButtons() {
		final List buttons = new Vector();
		this.add(this.prevButton);
		buttons.add(this.prevButton);
		this.add(this.nextButton);
		buttons.add(this.nextButton);
		this.add(this.listButton);
		buttons.add(this.listButton.getButton());
		buttons.add(this.listButton.getMenuButton());
		return buttons;
	}

	public void updateComboList(final String formManager) {
		this.formManager = formManager;

		if (this.listButton.getMenu()
				.getComponentCount() == NavigationHandler.getInstance().defaultNumberFormManagersShowed) {
			this.listButton.getMenu().remove(this.listButton.getMenu().getComponentCount() - 1);
			NavigationHandler.getInstance().getLastVisitedFormManagers().remove(0);
			NavigationHandler.getInstance()
			.setNavigationPosition(NavigationHandler.getInstance().getNavigationPosition() - 1);
		}
		final Map radiomenuparameters = new Hashtable();
		radiomenuparameters.put("attr", formManager);
		radiomenuparameters.put("checked", "no");
		final RadioMenuItem menuItem = new RadioMenuItem(radiomenuparameters);
		menuItem.setResourceBundle(ApplicationManager.getApplicationBundle());

		this.listButton.getMenu().add(menuItem, 0);
		((JMenuItem) this.listButton.getMenu().getComponent(0)).addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ApToolBarNavigator.this.moveInList(e);
			}

		});

	}

	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public void setEnabled(final boolean enabled) {

	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		for (int i = 0; i < this.listButton.getMenu().getComponentCount(); i++) {
			((RadioMenuItem) this.listButton.getMenu().getComponent(i))
			.setText(ApplicationManager.getTranslation(
					((RadioMenuItem) this.listButton.getMenu().getComponent(i)).attribute, resourceBundle));
		}
		if (this.prevButton != null) {
			this.prevButton.setToolTipText(
					ApplicationManager.getTranslation(ApToolBarNavigator.PREV_BUTTON_BUNDLE_KEY, resourceBundle));
		}
		if (this.nextButton != null) {
			this.nextButton.setToolTipText(
					ApplicationManager.getTranslation(ApToolBarNavigator.NEXT_BUTTON_BUNDLE_KEY, resourceBundle));
		}
		if (this.listButton != null) {
			this.listButton.setToolTipText(
					ApplicationManager.getTranslation(ApToolBarNavigator.LIST_BUTTON_BUNDLE_KEY, resourceBundle));
		}
	}

	protected void changeButtons(final List buttons, final boolean borderbuttons, final boolean opaquebuttons,
			final MouseListener listenerHighlightButtons) {
		for (int i = 0; i < buttons.size(); i++) {
			if (buttons.get(i) != null) {
				((AbstractButton) buttons.get(i)).setFocusPainted(false);
				if (!borderbuttons) {
					((AbstractButton) buttons.get(i)).setBorder(ApToolBarNavigator.defaultButtonsBorder);
				}
				if (!opaquebuttons) {
					((AbstractButton) buttons.get(i)).setOpaque(false);
					((AbstractButton) buttons.get(i)).setContentAreaFilled(false);
				}
				if (listenerHighlightButtons != null) {
					((AbstractButton) buttons.get(i)).addMouseListener(listenerHighlightButtons);
				}
			}
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
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
				if (ApplicationManager.DEBUG_SECURITY) {
					ApToolBarNavigator.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		return null;
	}

	// public Dimension getPreferredSize() {
	// if (this.getParent() instanceof JToolBar) {
	// return new Dimension(this.size);
	// }
	// return super.getPreferredSize();
	// }

	@Override
	public Dimension getMinimumSize() {
		return super.getPreferredSize();
	}

	@Override
	public Dimension getMaximumSize() {
		return super.getPreferredSize();
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

	// public void setKeyBinding(String key, KeyStroke keyStroke, Action action,
	// InputMap inMap, ActionMap actMap) {
	//
	// inMap.put(keyStroke, key);
	// actMap.put(key, action);
	//
	// ComponentKeyStroke compKeyStroke = new ComponentKeyStroke(this,
	// ApToolBarNavigator.class.getName());
	// compKeyStroke.setKeyName(key);
	// compKeyStroke.setKeyStroke(keyStroke);
	// compKeyStroke.setAction(action);
	// compKeyStroke.setInputMapCondition(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	// keyBindings.add(compKeyStroke);
	// }
	//
	// private String getKeyStrokePreferencesKey(String keyName) {
	// return
	// ShortcutDialogConfiguration.getAcceleratorPreferencesKey(ApToolBarNavigator.class.getName(),
	// keyName);
	// }
	//
	// public void initPreferences(ApplicationPreferences aPrefs, String user) {
	// // KeyStroke
	// if (aPrefs != null) {
	// String pref = aPrefs.getPreference(user, getKeyStrokePreferencesKey());
	// if (pref != null) {
	// String prefs[] = pref.split(" ");
	// KeyStroke ks = KeyStroke.getKeyStroke(Integer.parseInt(prefs[1]),
	// Integer.parseInt(prefs[0]));
	// //KeyStroke ks = KeyStroke.getKeyStroke(pref);
	// if (ks != null) {
	// super.setAccelerator(ks);
	// }
	// }
	// }
	// }

}
