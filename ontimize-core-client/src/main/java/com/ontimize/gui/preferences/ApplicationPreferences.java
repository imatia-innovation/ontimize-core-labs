package com.ontimize.gui.preferences;

import com.ontimize.gui.Application;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferences;

/**
 * Interface that must be implemented for all classes that represent application preferences
 *
 * @version 1.0
 */
public interface ApplicationPreferences {

	/**
	 * Get the value of the preference with name <code>preferenceName</code> for the user
	 * <code>user</code>
	 * @param user Name of the user or null if the preference is for all users
	 * @param preferenceName Name of the preference
	 * @return Preference value
	 */
	public String getPreference(String user, String preferenceName);

	/**
	 * Sets the value of the preference with name <code>preferenceName</code> for the user
	 * <code>user</code>
	 * @param user Name of the user or null if the preference is for all users
	 * @param preferenceName Name of the preference
	 * @param value The value for the preference
	 */
	public void setPreference(String user, String preferenceName, String value);

	/**
	 * Show a dialog to configure the user preferences
	 * @param user
	 * @param fmName
	 * @param formName
	 * @param a
	 * @deprecated
	 */
	@Deprecated
	public void showPreferencesDialog(String user, String fmName, String formName, Application a);

	/**
	 * Save the current preference values
	 */
	public void savePreferences();

	/**
	 * Load the preference values
	 */
	public void loadPreferences();

	/**
	 * Adds an ApplicationPreferencesListener.
	 * @param l
	 */
	public void addApplicationPreferencesListener(ApplicationPreferencesListener l);

	/**
	 * Removes an ApplicationPreferencesListener.
	 * @param l
	 */
	public void removeApplicationPreferencesListener(ApplicationPreferencesListener l);

	public void setRemoteApplicationPreferences(RemoteApplicationPreferences rp);

	public RemoteApplicationPreferences getRemoteApplicationPreferences();

}
