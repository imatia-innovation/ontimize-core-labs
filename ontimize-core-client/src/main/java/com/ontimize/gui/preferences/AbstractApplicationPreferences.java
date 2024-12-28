package com.ontimize.gui.preferences;

import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * <p>
 * Basic implementation of the application preferences. This is an abstract class.
 * </p>
 *
 * @version 1.0
 * @see BasicApplicationPreferences
 */

public abstract class AbstractApplicationPreferences implements ApplicationPreferences {

	protected List listeners = new Vector();

	/**
	 * Preferences store
	 */
	protected Properties props = new Properties();

	public Properties getProperties() {
		return this.props;
	}

	protected Properties userProps = new Properties();

	protected Properties defaultUserProps = new Properties();

	/**
	 * Character used to separate the user name and the preference name in the preference key
	 */
	protected char separator = '_';

	public AbstractApplicationPreferences() {
	}

	/**
	 * Gets the key used to search a preference
	 * @param user Name of the user who searches the preference. This parameter can be null
	 * @param name Preference name
	 * @return Key that must be used to search the preference
	 */
	protected String getKeyPreference(final String user, final String name) {
		if ((user == null) || (user.length() == 0)) {
			return name;
		}
		final StringBuilder sb = new StringBuilder(user);
		sb.append(this.separator);
		sb.append(name);
		return sb.toString();
	}

	@Override
	public void addApplicationPreferencesListener(final ApplicationPreferencesListener l) {
		if (this.listeners.contains(l) == false) {
			this.listeners.add(l);
		}
	}

	@Override
	public void removeApplicationPreferencesListener(final ApplicationPreferencesListener l) {
		this.listeners.remove(l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on changes in preference
	 * values. The fire event include information about the user, the preference key and the new value
	 * for this preference.
	 * @param user Name of the user
	 * @param pref Preference name
	 * @param value New preference value
	 */
	protected void fireApplicationPreferencesChanged(final String user, final String pref, final String value) {
		for (int i = 0; i < this.listeners.size(); i++) {
			((ApplicationPreferencesListener) this.listeners.get(i))
			.preferenceChanged(new PreferenceEvent(this, user, pref, value));
		}
	}

}
