package com.ontimize.gui;

import java.awt.Frame;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JMenuBar;
import javax.swing.JToolBar;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 *
 * Defines the methods that an application must implement. Those methods allows to interchange
 * references among the application elements as well to perform the main operations the application
 * must perform.
 *
 */
public interface Application extends Internationalization {

	/**
	 * Sets a menu bar to the application.
	 * @param menuBar the menu bar
	 * @see ApplicationMenuBar
	 */
	public void setMenu(JMenuBar menuBar);

	/**
	 * Sets a tool bar to the application.
	 * @param toolBar the tool bar
	 * @see ApplicationToolBar
	 */
	public void setToolBar(JToolBar toolBar);

	/**
	 * Provides a reference to the ReferenceLocator configured to this application. The locator is set
	 * by the application constructor, and is mandatory to perform the login process.
	 * @return a reference to the ReferencesLocator, null in case no locator set yet
	 */
	public EntityReferenceLocator getReferenceLocator();

	/**
	 * Sets the EntityReferenceLocator for this application. The references locator is necessary to
	 * provide references to the Entity objects. The locator set will be unique for the whole
	 * application.
	 * @param locator
	 */
	public void setReferencesLocator(EntityReferenceLocator locator);

	/**
	 * Shows a form manager.
	 * @param formManagerName the name of the FormManager to show.
	 */
	public void showFormManagerContainer(String formManagerName);

	/**
	 * Provides a reference to a FormManager.
	 * @param formManagerName the name of the FormManager
	 * @return the FormManager or null in case that FormManager does not exist.
	 */
	public IFormManager getFormManager(String formManagerName);

	/**
	 * Adds a FormManager to the application.
	 * @param formManagerName the FormManager class
	 * @param formManager the name that will be use to register and reference the FormManager
	 */
	public void registerFormManager(String formManagerName, IFormManager formManager);

	/**
	 * Shows the application. This is the last method that is called before showing the application to
	 * the user. If some operations must be done before the application is loaded, this is the method
	 * that must be overwritten/implemented. For example, if the application must be sent to the tool
	 * bar just before to be showed, this is the method in which do that.
	 */
	public void show();

	/**
	 * Returns the frame in which the application is placed.
	 */
	public Frame getFrame();

	/**
	 * Performs the login operation
	 */
	public boolean login();

	/**
	 * Closes the application.
	 */
	public void exit();

	/**
	 * Locks the application without closing it.
	 * @deprecated
	 */
	@Deprecated
	public void lock();

	/**
	 * Ends the user session.
	 */
	public void endSession();

	/**
	 * Returns the current application locale.
	 * @return the current application locale
	 */
	public Locale getLocale();

	/**
	 * Returns the current application resource bundle.
	 * @return the current application resource bundle
	 */
	public ResourceBundle getResourceBundle();

	/**
	 * Provides a reference to the application menu bar.
	 * @return the application menu bar
	 */
	public JMenuBar getMenu();

	/**
	 * Provides a reference to the class that listens the menu events.
	 * @return the application menu listener
	 */
	public MenuListener getMenuListener();

	/**
	 * Sets the application menu listener
	 * @param menuListener the application menu listener
	 */
	public void setMenuListener(MenuListener menuListener);

	/**
	 * Provides a reference to the application tool bar.
	 * @return the application tool bar
	 */
	public JToolBar getToolBar();

	/**
	 * Returns the application tool bar listener
	 * @return the application tool bar listener
	 */
	public ToolBarListener getToolBarListener();

	/**
	 * Sets a toolbarListener
	 * @param toolbarListener the new {@link ToolBarListener}
	 */
	public void setToolBarListener(ToolBarListener toolbarListener);

	/**
	 * Returns the application preferences.
	 * @return the preferences for the application
	 */
	public ApplicationPreferences getPreferences();

	/**
	 * Sets a text in the application bar.
	 * @param text
	 */
	public void setStatusBarText(String text);

	/**
	 * Register a {@link StatusComponent} to be managed by this application.
	 * @param statusComponent the StatusComponent
	 */
	public void registerStatusComponent(StatusComponent statusComponent);

	/**
	 * Unregister a {@link StatusComponent} from the list of the be application managed components.
	 * @param statusComponent the StatusComponent
	 */
	public void unregisterStatusComponent(StatusComponent statusComponent);

	/**
	 * Returns the application name.
	 * @return the application name.
	 */
	public String getName();

	/**
	 * Get the name of remote reference which manage the database bundle
	 * @return remote reference name.
	 */
	public String getDatabaseBundleName();

}
