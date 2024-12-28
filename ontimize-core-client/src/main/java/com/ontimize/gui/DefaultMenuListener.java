package com.ontimize.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.i18n.LocaleEvent;
import com.ontimize.gui.i18n.LocaleListener;
import com.ontimize.gui.i18n.LocaleMenuItem;
import com.ontimize.gui.i18n.MenuLocale;
import com.ontimize.jee.common.locator.UtilReferenceLocator;

/**
 * This class implements the default menu listener for processing events, overrides
 * actionPerformed(ActionEvent e).
 * <p>
 * This object is automatically registered to change the application locale.
 * <p>
 *
 * @author Imatia Innovation
 */
public abstract class DefaultMenuListener implements MenuListener, ActionListener, LocaleListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultMenuListener.class);

	/**
	 * The application parameter. By default, null.
	 */
	protected Application application = null;

	/**
	 * The application menu bar parameter. By default, null.
	 */
	protected ApplicationMenuBar menu = null;

	private static boolean checkSupportedLocales = false;

	private static List supportedLocales = new Vector();

	private static boolean queryLocales = false;

	/**
	 * The condition to check the supported application locales.
	 */
	public static boolean CHECK_SUPPORTED_LOCALES = false;

	/**
	 * The constructor is empty.
	 */
	public DefaultMenuListener() {
	}

	/**
	 * Sets the param application.
	 */
	@Override
	public void setApplication(final Application apl) {
		this.application = apl;
	}

	/**
	 * Sets the initial state for application. By default, only application locale is set in this
	 * method. For increasing the functionalities, it is recommended to override this method.
	 */
	@Override
	public void setInitialState() {
		// Establishes the initial locale
		if (this.application != null) {
			final Locale l = this.application.getLocale();
			final ApplicationMenuBar m = this.menu;
			final List items = m.getAllItems();
			for (int i = 0; i < items.size(); i++) {
				final Object item = items.get(i);
				if (item instanceof LocaleMenuItem) {
					if (((LocaleMenuItem) item).getLocale().equals(l)) {
						((LocaleMenuItem) item).setSelected(true);
						break;
					}
				}
			}
		}
	}

	/**
	 * Indicates the menu whose events will be listened by this object. It is specially implemented for
	 * JMenuBar classes.
	 */
	@Override
	public void addMenuToListenFor(final JMenuBar menuBar) {
		if (menuBar instanceof ApplicationMenuBar) {
			this.menu = (ApplicationMenuBar) menuBar;
			final ApplicationMenuBar applicationMenuBar = (ApplicationMenuBar) menuBar;
			final List items = applicationMenuBar.getAllItems();
			for (int i = 0; i < items.size(); i++) {
				final Object item = items.get(i);
				if ((item instanceof JMenuItem) && !(item instanceof MenuLocale)) {
					((JMenuItem) item).addActionListener(this);
				} else if ((item instanceof JMenu) && !(item instanceof MenuLocale)) {
					((JMenu) item).addActionListener(this);
				} else if (item instanceof MenuLocale) {
					((MenuLocale) item).addLocaleListener(this);
				}
			}
		} else {
			DefaultMenuListener.logger.debug("menuBar parameter is not an instance of ApplicationMenuBar ");
		}
	}

	/**
	 * Gets the available locales.
	 * <p>
	 *
	 * @see Locale
	 */

	protected static void getAvaliableLocales() {

		if (DefaultMenuListener.supportedLocales.isEmpty()) {
			final Locale[] locales = Locale.getAvailableLocales();
			for (int j = 0; j < locales.length; j++) {
				DefaultMenuListener.supportedLocales.add(locales[j]);
			}
		}
		DefaultMenuListener.queryLocales = true;
	}

	/**
	 * Checks the LocaleEvent and changes the locale.
	 */
	@Override
	public void localeChange(final LocaleEvent e) {
		// Changes the application locale
		try {
			final UtilReferenceLocator reference = (UtilReferenceLocator) this.application.getReferenceLocator();
			reference.setLocale(this.application.getReferenceLocator().getSessionId(), e.getLocale());
			this.application.setComponentLocale(e.getLocale());
			this.application
			.setResourceBundle(ExtendedPropertiesBundle.getExtendedBundle(e.getResourceBundle(), e.getLocale()));

		} catch (final Exception ex) {
			DefaultMenuListener.logger.error("Error changing application locale and language: ", ex);
		}
		if (DefaultMenuListener.CHECK_SUPPORTED_LOCALES) {
			if (!DefaultMenuListener.queryLocales) {
				DefaultMenuListener.getAvaliableLocales();
			}
			if (!DefaultMenuListener.checkSupportedLocales) {
				// Checks the available locales
				final List notSupportedLocales = new Vector();
				for (int i = 0; i < ((MenuLocale) e.getSource()).getMenuComponentCount(); i++) {
					final Component c = ((MenuLocale) e.getSource()).getMenuComponent(i);
					if (c instanceof LocaleMenuItem) {
						final Locale l = ((LocaleMenuItem) c).getLocale();
						final Locale l2 = new Locale(l.getLanguage(), l.getCountry());
						if (!DefaultMenuListener.supportedLocales.contains(l2)) {
							notSupportedLocales.add(l2.getDisplayName(e.getLocale()));
						}
					}
				}
				if (notSupportedLocales.size() > 0) {
					final StringBuilder sbMessage = new StringBuilder(
							"Following Locales are not supported by current JVM: \n");
					for (int i = 0; i < notSupportedLocales.size(); i++) {
						sbMessage.append(notSupportedLocales.get(i));
						sbMessage.append("\n");
					}
					MessageDialog.showMessage(this.application.getFrame(), sbMessage.toString(),
							JOptionPane.WARNING_MESSAGE, JOptionPane.OK_OPTION, null);
				}
				DefaultMenuListener.checkSupportedLocales = true;
			}
		}
	}

	/**
	 * This method implementation is empty.
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
	}

}
