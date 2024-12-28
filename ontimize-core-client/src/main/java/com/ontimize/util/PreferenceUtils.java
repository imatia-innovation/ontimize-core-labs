package com.ontimize.util;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class PreferenceUtils {

	public static void deletePreference(final String keyPreference, final String user) {
		if (keyPreference == null) {
			return;
		}
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		if (prefs != null) {
			final String pref = prefs.getPreference(user, keyPreference);
			if (pref != null) {
				prefs.setPreference(user, keyPreference, null);
				prefs.savePreferences();
			}
		}
	}

	public static String loadPreference(final String keyPreference) {
		final EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
		String user = null;
		if (locator instanceof ClientReferenceLocator) {
			user = ((ClientReferenceLocator) locator).getUser();
		}
		return PreferenceUtils.loadPreference(keyPreference, user);
	}

	public static String loadPreference(final String keyPreference, final String user) {
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		return PreferenceUtils.loadPreference(keyPreference, user, prefs);
	}

	public static String loadPreference(final String keyPreference, final String user, final ApplicationPreferences prefs) {
		if (keyPreference == null) {
			return null;
		}
		if (prefs != null) {
			return prefs.getPreference(user, keyPreference);
		}
		return null;
	}

	public static String loadPreference(final String keyPreference, final ApplicationPreferences prefs) {
		final EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
		String user = null;
		if ((locator != null) && (locator instanceof ClientReferenceLocator)) {
			user = ((ClientReferenceLocator) locator).getUser();
		}
		return PreferenceUtils.loadPreference(keyPreference, user, prefs);
	}

	public static void savePreference(final String keyPreference, final String preference, final ApplicationPreferences prefs) {
		final EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
		String user = null;
		if ((locator != null) && (locator instanceof ClientReferenceLocator)) {
			user = ((ClientReferenceLocator) locator).getUser();
		}
		PreferenceUtils.savePreference(keyPreference, preference, user);
	}

	public static void savePreference(final String keyPreference, final String preference, final String user,
			final ApplicationPreferences prefs) {
		if (keyPreference == null) {
			return;
		}
		if (prefs != null) {
			prefs.setPreference(user, keyPreference, preference);
			prefs.savePreferences();
		}
	}

	public static void savePreference(final String keyPreference, final String preference) {
		final EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
		String user = null;
		if ((locator != null) && (locator instanceof ClientReferenceLocator)) {
			user = ((ClientReferenceLocator) locator).getUser();
		}
		PreferenceUtils.savePreference(keyPreference, preference, user);
	}

	public static void savePreference(final String keyPreference, final String preference, final String user) {
		if (keyPreference == null) {
			return;
		}
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		PreferenceUtils.savePreference(keyPreference, preference, user, prefs);
	}

}
