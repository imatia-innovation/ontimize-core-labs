package com.ontimize.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.report.RemoteReportReferencer;
import com.ontimize.jee.common.report.store.ReportStore;
import com.ontimize.module.IModuleActionMenuListener;
import com.ontimize.report.BaseEntityReportDesigner;
import com.ontimize.report.ReportConfig;

public class DefaultActionMenuListener extends DefaultMenuListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultActionMenuListener.class);

	public static final String SYSTEM_INFORMATION = "SystemInformation";

	public static final String SHORTCUT_CONFIGURATION = "ShortcutConfiguration";

	public static final String REPORT_EDITOR = "ReportEditor";

	public static final String REPORT_DESIGNER = "ReportDesigner";

	public static final String REPORT_LIST = "ReportList";

	public static final String HELP_THEME = "HelpTheme";

	public static final String SEND_TO_TRAY = "SendToTray";

	public static final String CHAT = "Chat";

	public static final String MESSAGES = "Messages";

	public static final String LCMC_S = "LCMC";

	public static final String FONT_SETUP = "FontSetup";

	public static final String LARGE_FONT = "LargeFont";

	public static final String REGULAR_FONT = "RegularFont";

	public static final String SMALL_FONT = "SmallFont";

	public static final String CHANGE_TABLE_FIELD_LOOK = "ChangeTableFieldLook";

	public static final String LOCK = "Lock";

	public static final String EXIT = "Exit";

	public static final String SHOW_HIDE_MONITOR = "ShowHideMonitor";

	public static final String MANAGER = "MANAGER";

	public static final String SHOW_HIDE_TABLE_ROW_NUMBER = "ShowHideTableRowNumber";

	public static final String SHOW_HIDE_TOOLBAR = "ShowHideToolbar";

	public static final String SHOW_HIDE_STATUS_BAR = "ShowHideStatusBar";

	public static final String REMEMBER_PASSWORD = "RememberPassword";

	public static final String WHAT_IS_THIS = "WhatIsThis";

	Form fSessions = null;

	JDialog sessionDialog = null;

	String adminSessionId = null;

	protected ReportConfig dReportSelection = null;

	protected BaseEntityReportDesigner entityReportDesigner = null;

	protected Map createdForms = new Hashtable();

	/** The listener list. */
	protected List<IModuleActionMenuListener> moduleListenerList;

	public DefaultActionMenuListener() {
		this.moduleListenerList = new ArrayList<IModuleActionMenuListener>();
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		if (this.application instanceof MainApplication) {
			final boolean rememberPassword = ((MainApplication) this.application).isRememberLastPasswordEnabled();
			if (rememberPassword) {
				this.menu.setItemMenuEnabled(DefaultActionMenuListener.REMEMBER_PASSWORD, true);
				final JMenuItem item = this.menu.getMenuItem(DefaultActionMenuListener.REMEMBER_PASSWORD);
				if (item != null) {
					item.setSelected(true);
				}
			} else {
				this.menu.setItemMenuEnabled(DefaultActionMenuListener.REMEMBER_PASSWORD, false);
			}
			boolean vis = ((MainApplication) this.application).isStatusBarVisible();
			JMenuItem item = this.menu.getMenuItem(DefaultActionMenuListener.SHOW_HIDE_STATUS_BAR);
			if (item != null) {
				item.setSelected(vis);
			}

			vis = ((MainApplication) this.application).isStatusBarVisible();
			item = this.menu.getMenuItem(DefaultActionMenuListener.SHOW_HIDE_TOOLBAR);
			if (item != null) {
				item.setSelected(vis);
			}
		}

		// Preferences
		final ApplicationPreferences pref = this.application.getPreferences();
		if (pref != null) {
			final String p = pref.getPreference(null, BasicApplicationPreferences.SHOW_TABLE_NUM_ROW);
			if (p != null) {
				final boolean vis = ApplicationManager.parseStringValue(p, true);
				final JMenuItem item = this.menu.getMenuItem(DefaultActionMenuListener.SHOW_HIDE_TABLE_ROW_NUMBER);
				if (item != null) {
					item.setSelected(vis);
				}
			}
		}
	}

	protected void showHelp(final ActionEvent e) {
		final Window parent = this.application.getFrame();
		HelpUtilities.showHelpOnItem(parent, e);
	}

	protected void showHelp() {
		final Window parent = this.application.getFrame();
		HelpUtilities.showHelp(parent);
	}

	protected boolean isDialog(final Object source) {
		if (source instanceof IParameterItem) {
			return ((IParameterItem) source).isDialog();
		}
		return false;
	}

	protected String getFormName(final Object source) {
		if (source instanceof IParameterItem) {
			return ((IParameterItem) source).getFormName();
		}
		return null;
	}

	protected String getFormManagerName(final Object source) {
		if (source instanceof IParameterItem) {
			return ((IParameterItem) source).getFormManagerName();
		}
		return null;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Cursor cursor = null;
		try {
			for (final IModuleActionMenuListener listener : this.moduleListenerList) {
				if (listener.actionModulePerformed(e)) {
					return;
				}
			}
			if (this.application instanceof Component) {
				cursor = ((Component) this.application).getCursor();
				((Component) this.application).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			}
			final String command = e.getActionCommand();
			if (command != null) {

				final boolean checkPrevious = this.actionSimpleCommand(command, e);

				if (command.equals(DefaultActionMenuListener.CHANGE_TABLE_FIELD_LOOK) && !checkPrevious) {
					if ((this.application.getPreferences() == null)
							|| (!(this.application.getPreferences() instanceof BasicApplicationPreferences))) {
						MessageDialog.showMessage(this.application.getFrame(), "preferences_file_has_no_been_defined",
								JOptionPane.ERROR_MESSAGE,
								this.application.getResourceBundle());
						return;
					}
					if (this.application.getPreferences() instanceof BasicApplicationPreferences) {
						((BasicApplicationPreferences) this.application.getPreferences())
						.showColorChooserDialog(this.application.getFrame());
					}

				} else if (command.equals(DefaultActionMenuListener.SEND_TO_TRAY)) {
					if ((this.application == null) || (this.application.getFrame() == null)) {
						return;
					}
					if (this.application instanceof MainApplication) {
						((MainApplication) this.application).sendToTray();
					}

				} else if (command.equals(DefaultActionMenuListener.FONT_SETUP)) {
					if ((this.application == null) || (this.application.getFrame() == null)) {
						return;
					}
					this.actionFontSelector();

				} else if (e.getSource() instanceof IParameterItem) {
					final IParameterItem currentItem = (IParameterItem) e.getSource();
					final String formManagerName = currentItem.getFormManagerName();

					final IFormManager formManager = this.application.getFormManager(formManagerName);

					if (formManager == null) {
						return;
					}
					if (formManager != null) {
						formManager.load();
					}

					if (currentItem.isDialog()) {
						if (currentItem.getFormName() == null) {
							DefaultActionMenuListener.logger.debug(
									"The 'form' parameters must be fixed in the MenuItem to established the DIALOG FORM.");
							return;
						}
						final String key = formManagerName + "/" + currentItem.getFormName();
						Form currentForm = null;
						if (this.createdForms.containsKey(key)) {
							currentForm = (Form) this.createdForms.get(key);
						} else {
							currentForm = formManager.getFormCopy(currentItem.getFormName());
							if (currentForm != null) {
								this.createdForms.put(key, currentForm);
							}
						}

						if (currentForm != null) {
							final JDialog dialog = currentForm.putInModalDialog();
							dialog.setTitle(
									ApplicationManager.getTranslation(
											currentForm.getFormTitle() != null ? currentForm.getFormTitle() : "",
													currentForm.getResourceBundle()));
							dialog.setVisible(true);
						}
						return;
					} else {
						this.application.showFormManagerContainer(formManagerName);
						final String formName = currentItem.getFormName();
						if ((formName != null) && (formManager != null)) {
							formManager.showForm(formName);
						}
					}
				}
			}
		} finally {
			if (this.application instanceof Component) {
				((Component) this.application).setCursor(cursor);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param command
	 * @param e
	 * @return
	 */
	protected boolean actionSimpleCommand(final String command, final ActionEvent e) {
		boolean toret = false;
		if (command.toUpperCase().startsWith(DefaultActionMenuListener.MANAGER) && !this.isDialog(e.getSource())) {
			this.actionManagerCommand(e, command);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.WHAT_IS_THIS)) {
			this.actionHelpCommand(e);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SHOW_HIDE_STATUS_BAR)) {
			this.actionStatusBarCommand(e);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SHOW_HIDE_TABLE_ROW_NUMBER)) {
			this.actionTableRowNumberCommand(e);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SHOW_HIDE_MONITOR)) {
			this.actionMonitorCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.REMEMBER_PASSWORD)) {
			this.actionRememberPasswordCommand(e);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SHOW_HIDE_TOOLBAR)) {
			this.actionToolbarCommand(e);
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.EXIT)) {
			this.application.exit();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.LOCK)) {
			this.actionLockCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SMALL_FONT)) {
			this.actionSmallFontCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.REGULAR_FONT)) {
			this.actionRegularFontCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.LARGE_FONT)) {
			this.actionLargeFontCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.HELP_THEME)) {
			this.actionHelpThemeCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.MESSAGES)) {
			this.actionMessagesCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.CHAT)) {
			this.actionChatCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.REPORT_LIST)) {
			this.actionReportListCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.REPORT_DESIGNER)) {
			this.actionReportDesignerCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.REPORT_EDITOR)) {
			this.actionReportEditorCommand();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SHORTCUT_CONFIGURATION)) {
			this.menu.showMenuShortcutsConfigurationDialog();
			toret = true;
		} else if (command.equals(DefaultActionMenuListener.SYSTEM_INFORMATION)) {
			ApplicationManager.showSystemInformation();
			toret = true;
		}

		return toret;
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 * @param command
	 */
	protected void actionManagerCommand(final ActionEvent e, final String command) {
		final IFormManager formManager = this.application.getFormManager(command);
		if (formManager != null) {
			formManager.load();
		}
		this.application.showFormManagerContainer(command);
		final String formName = this.getFormName(e.getSource());
		if ((formName != null) && (formManager != null)) {
			formManager.showForm(formName);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 */
	protected void actionHelpCommand(final ActionEvent e) {
		if (HelpUtilities.isHelpEnabled()) {
			this.showHelp(e);
		} else {
			DefaultActionMenuListener.logger
			.debug("HelpUtilities.isHelpEnabled() is false -> jh.jar must be in classpath");
			MessageDialog.showMessage(this.application.getFrame(), "applicationmanager.show_help_error",
					JOptionPane.ERROR_MESSAGE, this.application.getResourceBundle());
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 */
	protected void actionStatusBarCommand(final ActionEvent e) {
		final Object source = e.getSource();
		if (source instanceof AbstractButton) {
			if (this.application instanceof MainApplication) {
				((MainApplication) this.application).setStatusBarVisible(((AbstractButton) source).isSelected());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 */
	protected void actionTableRowNumberCommand(final ActionEvent e) {
		final Object source = e.getSource();
		if (source instanceof AbstractButton) {
			if (this.application instanceof MainApplication) {
				final boolean bShow = ((AbstractButton) source).isSelected();
				if (this.application.getPreferences() != null) {
					this.application.getPreferences()
					.setPreference(null, BasicApplicationPreferences.SHOW_TABLE_NUM_ROW,
							ApplicationManager.parseBooleanValue(bShow));
					this.application.getPreferences().savePreferences();
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionMonitorCommand() {
		this.menu.getMenuItem(DefaultActionMenuListener.SHOW_HIDE_MONITOR);
		final com.ontimize.gui.ApplicationManager.OPThreadsMonitor m = ApplicationManager.getOPThreadsMonitor();
		if (m != null) {
			m.setVisible(!m.isVisible());
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 */
	protected void actionRememberPasswordCommand(final ActionEvent e) {
		final Object source = e.getSource();
		if (source instanceof AbstractButton) {
			if (this.application instanceof MainApplication) {
				((MainApplication) this.application).deactivatedRememberLastPassword();
				((AbstractButton) source).setSelected(false);
				((AbstractButton) source).setEnabled(false);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 * @param e
	 */
	protected void actionToolbarCommand(final ActionEvent e) {
		final Object source = e.getSource();
		if (source instanceof AbstractButton) {
			if (this.application instanceof MainApplication) {
				((MainApplication) this.application).setToolBarVisible(((AbstractButton) source).isSelected());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionLockCommand() {
		((MainApplication) this.application).lock();
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionSmallFontCommand() {
		if (MessageDialog.showQuestionMessage(application.getFrame(), "apply_font_configuration",
				application.getResourceBundle())) {
			FontSelector.setApplicationFontSize(this.application, 10);
			final ApplicationPreferences prefs = this.application.getPreferences();
			String user = null;
			if ((this.application.getReferenceLocator() != null)
					&& (this.application.getReferenceLocator() instanceof ClientReferenceLocator)) {
				user = ((ClientReferenceLocator) this.application.getReferenceLocator()).getUser();
			}
			if (prefs != null) {
				prefs.setPreference(user, MainApplication.APP_FONTSIZE, "10");
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionRegularFontCommand() {
		if (MessageDialog.showQuestionMessage(application.getFrame(), "apply_font_configuration",
				application.getResourceBundle())) {
			FontSelector.setApplicationFontSize(this.application, 12);
			final ApplicationPreferences prefs = this.application.getPreferences();
			String user = null;
			if (this.application.getReferenceLocator() instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.application.getReferenceLocator()).getUser();
			}
			if (prefs != null) {
				prefs.setPreference(user, MainApplication.APP_FONTSIZE, "12");
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionLargeFontCommand() {
		if (MessageDialog.showQuestionMessage(application.getFrame(), "apply_font_configuration",
				application.getResourceBundle())) {
			FontSelector.setApplicationFontSize(this.application, 16);
			final ApplicationPreferences prefs = this.application.getPreferences();
			String user = null;
			if (this.application.getReferenceLocator() instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.application.getReferenceLocator()).getUser();
			}
			if (prefs != null) {
				prefs.setPreference(user, MainApplication.APP_FONTSIZE, "16");
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionHelpThemeCommand() {
		try {
			if (HelpUtilities.isHelpEnabled()) {
				this.showHelp();
			} else {
				DefaultActionMenuListener.logger
				.debug("HelpUtilities.isHelpEnabled() is false -> jh.jar must be in classpath");
				MessageDialog.showMessage(this.application.getFrame(), "applicationmanager.show_help_error",
						JOptionPane.ERROR_MESSAGE, this.application.getResourceBundle());
			}
		} catch (final Exception ex) {
			MessageDialog.showMessage(this.application.getFrame(), "applicationmanager.show_help_error",
					ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.application.getResourceBundle());
			if (ApplicationManager.DEBUG) {
				DefaultActionMenuListener.logger.debug(null, ex);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionMessagesCommand() {
		if (this.application.getReferenceLocator() instanceof ClientReferenceLocator) {
			((ClientReferenceLocator) this.application.getReferenceLocator())
			.showMessageDialog(this.application.getFrame());
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionChatCommand() {
		if (this.application.getReferenceLocator() instanceof ClientReferenceLocator) {
			final ClientReferenceLocator locator = (ClientReferenceLocator) this.application.getReferenceLocator();
			if (locator.hasChat()) {
				Chat.showChat(this.application.getReferenceLocator(), null, locator.getMessageCheckTime(),
						locator.getChatCheckTime());
			} else {
				locator.showMessageDialog(this.application.getFrame());
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionReportListCommand() {
		final EntityReferenceLocator referenceLocator = this.application.getReferenceLocator();
		try {
			if (this.dReportSelection == null) {

				try {
					final Class rootClass = Class.forName("com.ontimize.report.ReportSelection");
					final Class[] p = { Frame.class, EntityReferenceLocator.class, ReportStore[].class,
							ResourceBundle.class };
					final Constructor constructorReportSelection = rootClass.getConstructor(p);
					final Object[] parameters = { this.application.getFrame(), referenceLocator, null,
							ApplicationManager.getApplication().getResourceBundle() };
					this.dReportSelection = (ReportConfig) constructorReportSelection.newInstance(parameters);
				} catch (final Exception ex) {
					DefaultActionMenuListener.logger.error(null, ex);
				}
				// built with reflection - legacy
				// dReportSelection = new
				// com.ontimize.report.ReportSelection(this.application.getFrame(),
				// referenceLocator, null, ApplicationManager
				// .getApplication().getResourceBundle());

				if (this.dReportSelection instanceof JFrame) {
					((JFrame) this.dReportSelection).setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
				}

				if (this.dReportSelection instanceof Window) {
					ApplicationManager.center((Window) this.dReportSelection);
				}

				if (this.rs == null) {
					if (referenceLocator instanceof RemoteReportReferencer) {
						final List list = ((RemoteReportReferencer) referenceLocator)
								.getRemoteReportStore(referenceLocator.getSessionId());
						if (list != null) {
							this.rs = (ReportStore[]) list.toArray(new ReportStore[list.size()]);
						}
					}
				}

				this.dReportSelection.setReportStores(this.rs);
			}
			this.dReportSelection.setResourceBundle(ApplicationManager.getApplication().getResourceBundle());
			this.dReportSelection.setVisible(true);
		} catch (final Exception ex) {
			DefaultActionMenuListener.logger.error(null, ex);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionReportDesignerCommand() {
		try {
			final EntityReferenceLocator locator = this.application.getReferenceLocator();
			final java.util.List l = this.getEntitiesListForReportDesigner();
			final EntityReferenceLocator referenceLocator = ApplicationManager.getApplication().getReferenceLocator();

			if (this.entityReportDesigner == null) {
				if (this.rs == null) {
					if (locator instanceof RemoteReportReferencer) {
						final List list = ((RemoteReportReferencer) locator).getRemoteReportStore(locator.getSessionId());
						if (list != null) {
							this.rs = (ReportStore[]) list.toArray(new ReportStore[list.size()]);
						}
					}
				}

				try {
					final Class rootClass = Class.forName("com.ontimize.report.EntityReportDesigner");
					final Class[] p = { Frame.class, EntityReferenceLocator.class, String.class, java.util.List.class,
							ResourceBundle.class, ReportStore[].class };
					final Constructor constructorEntityReportDesigner = rootClass.getConstructor(p);
					final Object[] parameters = { null, referenceLocator, "ReportDesigner", l,
							this.application.getResourceBundle(), this.rs };
					this.entityReportDesigner = (BaseEntityReportDesigner) constructorEntityReportDesigner
							.newInstance(parameters);
				} catch (final Exception ex) {
					DefaultActionMenuListener.logger.error(null, ex);
				}
				// built with reflection - legacy version
				// entityReportDesigner = new
				// com.ontimize.report.EntityReportDesigner(null,
				// referenceLocator, "ReportDesigner", l, (URL)
				// null, application.getResourceBundle(), rs);

				this.entityReportDesigner.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			}

			if (this.entityReportDesigner instanceof Internationalization) {
				((Internationalization) this.entityReportDesigner)
				.setResourceBundle(ApplicationManager.getApplication().getResourceBundle());
			}

			this.entityReportDesigner.setVisible(true);
		} catch (final Exception ex) {
			DefaultActionMenuListener.logger.error(null, ex);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionReportEditorCommand() {
		try {
			final EntityReferenceLocator locator = this.application.getReferenceLocator();

			if (this.rs == null) {
				if (locator instanceof RemoteReportReferencer) {
					final List list = ((RemoteReportReferencer) locator).getRemoteReportStore(locator.getSessionId());
					if (list != null) {
						this.rs = (ReportStore[]) list.toArray(new ReportStore[list.size()]);
					}
				}
			}

			this.getEntitiesListForReportDesigner();
			final EntityReferenceLocator referenceLocator = ApplicationManager.getApplication().getReferenceLocator();

			com.ontimize.report.ReportUtils.showDefaultReportDesigner(this.application.getFrame(), this.rs,
					referenceLocator,
					ApplicationManager.getApplication().getResourceBundle(), null, "", "");
		} catch (final Exception ex) {
			DefaultActionMenuListener.logger.error(null, ex);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #actionPerformed(ActionEvent)}
	 */
	protected void actionFontSelector() {
		final Font f = FontSelector.showFontSelector(this.application.getFrame(), DefaultActionMenuListener.FONT_SETUP,
				this.application.getResourceBundle(),
				FontSelector.getCurrentFont());
		if (f != null) {
			final boolean bApply = MessageDialog.showQuestionMessage(this.application.getFrame(), "apply_font_configuration",
					this.application.getResourceBundle());
			if (bApply) {
				final PlafPreferences plafPreferences = PlafPreferences.getInstance();
				if (plafPreferences!=null){
					plafPreferences.setFontPreference(f);
				}
				if (this.application instanceof MainApplication) {
					((MainApplication) this.application).setApplicationFont(f);
				} else {
					FontSelector.setApplicationFont(this.application, f);
				}
			}
		}
	}

	protected java.util.List getEntitiesListForReportDesigner() {
		if (this.application.getReferenceLocator() instanceof RemoteReportReferencer) {
			try {
				return ((RemoteReportReferencer) this.application.getReferenceLocator())
						.getReportEntityNames(this.application.getReferenceLocator().getSessionId());
			} catch (final Exception ex) {
				DefaultActionMenuListener.logger.error(null, ex);
			}
		}
		return new ArrayList();
	}

	protected ReportStore[] rs = null;

	public com.ontimize.report.BaseEntityReportDesigner getEntityReportDesigner() {
		return this.entityReportDesigner;
	}

	/**
	 * Adds the listener.
	 * @param listener the listener
	 */
	@Override
	public void addListener(final IModuleActionMenuListener listener) {
		this.moduleListenerList.add(listener);
	}

}
