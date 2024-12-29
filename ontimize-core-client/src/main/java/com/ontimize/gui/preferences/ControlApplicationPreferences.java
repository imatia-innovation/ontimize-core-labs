package com.ontimize.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferenceReferencer;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.security.ApplicationPermission;
import com.ontimize.security.ClientSecurityManager;

public class ControlApplicationPreferences extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(ControlApplicationPreferences.class);

	protected static final String KEY_INIT_PREFERENCE = "INIT_CONFIGURACION";

	protected static final String DEFAULT_INIT_CONFIGURATION = "default_init_configuration";

	public static final String CONTROL_REMOTE_PREFERENCES_PERMISSIONS = "controlremotepreferences";

	public static final String CONTROL_DEFAULT_REMOTE_PREFERENCES_PERMISSIONS = "controldefaultremotepreferences";

	public static final String DIALOG_TITLE = "applicationpreferences.preferences";

	/** <i>User has not rights to perfom action</i> message key. */
	public static String M_YOU_DO_NOT_HAVE_PERMISSION_TO_EXECUTE_ACTION = "entity.no_has_permissions_to_perform_this_action";

	protected static List basicPreferences = null;
	static {
		ControlApplicationPreferences.basicPreferences = new ArrayList();
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_WINDOW_SIZE);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_STATUS_BAR_VISIBLE);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_TOOL_BAR_VISIBLE);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_WINDOW_POSITION);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.SHOW_TABLE_CONTROLS);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.SHOW_TABLE_NUM_ROW);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.SHOW_TIPS);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.ADJUST_TREE_SPACE);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_FONTSIZE);
		ControlApplicationPreferences.basicPreferences.add(BasicApplicationPreferences.APP_REMEMBER_LAST_LOGIN);
	}

	protected static final String T_SET_PREFERENCES = "preferences.user_preferences_by_default";

	protected static final String T_EXPORT_TO_PREFERENCES_FILE = "preferences.export_preferences_to_file";

	protected static final String T_EXPORT_TO_USER_PREFERENCES_FILE = "preferences.export_user_preferences_to_file";

	protected static final String T_IMPORT_FROM_PREFERENCES_FILE = "preferences.import_preferences_by_default";

	protected static final String T_IMPORT_FROM_USER_PREFERENCES_FILE = "preferences.import_user_preferences_file";

	protected static final String T_DELETE_USER_PREFERENCES = "preferences.delete_user_preferences";

	protected static final String M_IT_HAS_BEEN_SET_BY_DEFAULT = "applicationpreferences.set_correctly_preferences_by_default";

	protected static final String M_IT_HAS_BEEN_EXPORTED_TO_FILE = "applicationpreferences.export_correctly_the_file";

	protected static final String M_NO_PREFERENCES = "preferences.no_preferences";

	protected static final String M_IT_HAS_BEEN_IMPORTED_FROM_FILE = "applicationpreferences.import_correctly_the_file";

	protected static final String M_PREFERENCES_FOR_USER_HAVE_BEEN_DELETED = "applicationpreferences.deleted_correctly_preferences_for_user";

	protected JButton buttonSet = null;

	protected JButton buttonSaveFile = null;

	protected JButton buttonLoad = null;

	protected JButton buttonDelete = null;

	protected JButton bCurrentUserSaveFile = null;

	protected JButton bCurrentUserLoadFile = null;

	protected JOptionPane errorMessage = null;

	protected ResourceBundle resource = null;

	protected JFileChooser fChooser = null;

	protected ClientReferenceLocator locator = null;

	protected static boolean controlRemotePreferences = true;

	protected static boolean controlDefaultRemotePreferences = true;

	public ControlApplicationPreferences(final Frame owner, final ClientReferenceLocator clientLocator, final ResourceBundle bundle) {
		super(owner, true);
		this.setTitle("applicationpreferences.preferences");
		this.locator = clientLocator;
		this.init(bundle);
	}

	public static boolean checkRemotePreferences(final Frame owner, final ClientReferenceLocator locator, final ResourceBundle bundle) {
		try {
			final RemoteApplicationPreferenceReferencer locatorPreferences = (RemoteApplicationPreferenceReferencer) locator;
			final RemoteApplicationPreferences rAP = locatorPreferences
					.getRemoteApplicationPreferences(((EntityReferenceLocator) locator).getSessionId());
			if (rAP == null) {
				MessageDialog.showMessage(owner, "REMOTE_APPLICATION_PREFERENCES_DOES_NOT_EXIST",
						JOptionPane.INFORMATION_MESSAGE, bundle);
				return false;
			}
			return true;
		} catch (final Exception e) {
			ControlApplicationPreferences.logger.trace(null, e);
			MessageDialog.showMessage(owner, "ERROR_REMOTE_PREFERENCES", JOptionPane.ERROR_MESSAGE, bundle);
			return false;
		}
	}

	protected void checkApplicationPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		ControlApplicationPreferences.controlRemotePreferences = true;
		ControlApplicationPreferences.controlDefaultRemotePreferences = true;
		if (manager != null) {
			ApplicationPermission perm = new ApplicationPermission("controlremotepreferences", false);
			try {
				manager.checkPermission(perm);
				ControlApplicationPreferences.controlRemotePreferences = true;
			} catch (final Exception e) {
				if (e instanceof NullPointerException) {
					ControlApplicationPreferences.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					ControlApplicationPreferences.logger.debug(this.getClass().toString() + ": " + e.getMessage());
				}
				ControlApplicationPreferences.controlRemotePreferences = false;
			}

			perm = new ApplicationPermission("controldefaultremotepreferences", false);
			try {
				manager.checkPermission(perm);
				ControlApplicationPreferences.controlDefaultRemotePreferences = true;
			} catch (final Exception e) {
				if (e instanceof NullPointerException) {
					ControlApplicationPreferences.logger.error(null, e);
				}
				if (ApplicationManager.DEBUG_SECURITY) {
					ControlApplicationPreferences.logger.debug(this.getClass().toString() + ": " + e.getMessage());
				}
				ControlApplicationPreferences.controlDefaultRemotePreferences = false;
			}
		}

	}

	protected void init(final ResourceBundle bundle) {
		this.checkApplicationPermission();
		if (ControlApplicationPreferences.controlRemotePreferences) {
			ImageIcon icon = ImageManager.getIcon(ImageManager.SAVE_FILE);

			if (icon != null) {
				this.buttonSet = new JButton(ControlApplicationPreferences.T_SET_PREFERENCES, icon);
			} else {
				this.buttonSet = new JButton(ControlApplicationPreferences.T_SET_PREFERENCES);
			}
			if (this.buttonSet != null) {
				this.buttonSet.setHorizontalAlignment(SwingConstants.LEFT);
				this.buttonSet.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ControlApplicationPreferences.this.setDefaultPreferences();
					}
				});
			}

			icon = ImageManager.getIcon(ImageManager.DOCUMENT_DOWN);
			if (icon != null) {
				this.buttonSaveFile = new JButton(ControlApplicationPreferences.T_EXPORT_TO_PREFERENCES_FILE, icon);
			} else {
				this.buttonSaveFile = new JButton(ControlApplicationPreferences.T_EXPORT_TO_PREFERENCES_FILE);
			}

			if (this.buttonSaveFile != null) {
				this.buttonSaveFile.setHorizontalAlignment(SwingConstants.LEFT);
				this.buttonSaveFile.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ControlApplicationPreferences.this.savePreferencesToFile();
					}
				});
			}

			icon = ImageManager.getIcon(ImageManager.DOCUMENT_UP);
			if (icon != null) {
				this.buttonLoad = new JButton(ControlApplicationPreferences.T_IMPORT_FROM_PREFERENCES_FILE, icon);
			} else {
				this.buttonLoad = new JButton(ControlApplicationPreferences.T_IMPORT_FROM_PREFERENCES_FILE);
			}

			if (this.buttonLoad != null) {
				this.buttonLoad.setHorizontalAlignment(SwingConstants.LEFT);
				this.buttonLoad.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ControlApplicationPreferences.this.loadFileToPreferences();
					}
				});
			}

			icon = ImageManager.getIcon(ImageManager.DELETE_DOCUMENT);
			if (icon != null) {
				this.buttonDelete = new JButton(ControlApplicationPreferences.T_DELETE_USER_PREFERENCES, icon);
			} else {
				this.buttonDelete = new JButton(ControlApplicationPreferences.T_DELETE_USER_PREFERENCES);
			}

			if (this.buttonDelete != null) {
				this.buttonDelete.setHorizontalAlignment(SwingConstants.LEFT);
				this.buttonDelete.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ControlApplicationPreferences.this.deleteUserPreferences();
					}

				});
			}

			// This buttons are available if the remote user preferences are
			// enabled
			if (BasicApplicationPreferences.remoteUserPreferences) {
				icon = ImageManager.getIcon(ImageManager.DOCUMENT_DOWN);
				if (icon != null) {
					this.bCurrentUserSaveFile = new JButton(
							ControlApplicationPreferences.T_EXPORT_TO_USER_PREFERENCES_FILE, icon);
				} else {
					this.bCurrentUserSaveFile = new JButton(
							ControlApplicationPreferences.T_EXPORT_TO_USER_PREFERENCES_FILE);
				}
				this.bCurrentUserSaveFile.setHorizontalAlignment(SwingConstants.LEFT);
				this.bCurrentUserSaveFile.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ControlApplicationPreferences.this.saveCurrentUserPreferencesToFile();
					}
				});

				icon = ImageManager.getIcon(ImageManager.DOCUMENT_UP);
				if (icon != null) {
					this.bCurrentUserLoadFile = new JButton(
							ControlApplicationPreferences.T_IMPORT_FROM_USER_PREFERENCES_FILE, icon);
				} else {
					this.bCurrentUserLoadFile = new JButton(
							ControlApplicationPreferences.T_IMPORT_FROM_USER_PREFERENCES_FILE);
				}

				if (this.bCurrentUserLoadFile != null) {
					this.bCurrentUserLoadFile.setHorizontalAlignment(SwingConstants.LEFT);
					this.bCurrentUserLoadFile.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							ControlApplicationPreferences.this.loadFileToCurrentUserPreferences();
						}
					});
				}
			}

			this.setResourceBundle(bundle);
			this.getContentPane().setLayout(new GridBagLayout());

			if (ControlApplicationPreferences.controlDefaultRemotePreferences) {
				this.getContentPane()
				.add(this.buttonSet, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
						GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
				this.getContentPane()
				.add(this.buttonSaveFile,
						new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
								new Insets(2, 4, 2, 4), 0, 0));
				this.getContentPane()
				.add(this.buttonLoad,
						new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
								new Insets(2, 4, 2, 4), 0, 0));
				this.getContentPane()
				.add(this.buttonDelete,
						new GridBagConstraints(0, 3, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
								new Insets(2, 4, 2, 4), 0, 0));
			}
			if (BasicApplicationPreferences.remoteUserPreferences) {
				this.getContentPane()
				.add(this.bCurrentUserSaveFile,
						new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST,
								GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
				this.getContentPane()
				.add(this.bCurrentUserLoadFile,
						new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST,
								GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));
			}
			if (!ControlApplicationPreferences.controlDefaultRemotePreferences
					&& !BasicApplicationPreferences.remoteUserPreferences) {
				this.insertErrorMessage(bundle);
			}
		} else {
			this.insertErrorMessage(bundle);
		}
		this.pack();
	}

	protected void insertErrorMessage(final ResourceBundle bundle) {
		this.errorMessage = new JOptionPane(
				ApplicationManager.getTranslation(M_YOU_DO_NOT_HAVE_PERMISSION_TO_EXECUTE_ACTION, bundle),
				JOptionPane.WARNING_MESSAGE,
				JOptionPane.DEFAULT_OPTION);
		this.errorMessage.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent event) {
				if (ControlApplicationPreferences.this.isVisible()
						&& (event.getSource() == ControlApplicationPreferences.this.errorMessage)
						&& event.getPropertyName()
						.equals(JOptionPane.VALUE_PROPERTY)
						&& (event.getNewValue() != null) && (event.getNewValue() != JOptionPane.UNINITIALIZED_VALUE)) {
					ControlApplicationPreferences.this.errorMessage.setValue(null);
					ControlApplicationPreferences.this.setVisible(false);
				}
			}
		});

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.errorMessage);
	}

	private static ControlApplicationPreferences sWindow = null;

	protected JFileChooser getJFileChooser() {
		if (this.fChooser == null) {
			this.fChooser = new JFileChooser();
			this.fChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			this.fChooser.setMultiSelectionEnabled(false);
		}
		return this.fChooser;
	}

	protected void savePreferencesToFile() {
		if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
			RemoteApplicationPreferences rAP;
			try {
				rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferencesB64 = rAP.getRemotePreference(sessionId, null,
							ControlApplicationPreferences.DEFAULT_INIT_CONFIGURATION);
					if (preferencesB64 == null) {
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_NO_PREFERENCES, JOptionPane.INFORMATION_MESSAGE,
								this.resource);
						return;
					}

					final Properties oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(preferencesB64);
					if (oProperties != null) {
						final JFileChooser fc = this.getJFileChooser();
						final int op = fc.showSaveDialog(ControlApplicationPreferences.this);
						if (op != JFileChooser.APPROVE_OPTION) {
							return;
						}
						final FileOutputStream fOutput = new FileOutputStream(fc.getSelectedFile());
						oProperties.store(fOutput, "");
						fOutput.flush();
						fOutput.close();
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_IT_HAS_BEEN_EXPORTED_TO_FILE,
								JOptionPane.INFORMATION_MESSAGE,
								this.resource);
					}
				}
			} catch (final Exception ex) {
				ControlApplicationPreferences.logger.error(null, ex);
			}
		}
	}

	protected void saveCurrentUserPreferencesToFile() {
		if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
			RemoteApplicationPreferences rAP;
			try {
				rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());
				final String user = this.locator.getUser();
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferencesB64 = rAP.getRemotePreference(sessionId, user,
							RemoteApplicationPreferences.KEY_USER_PREFERENCE);
					if (preferencesB64 == null) {
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_NO_PREFERENCES, JOptionPane.INFORMATION_MESSAGE,
								this.resource);
						return;
					}

					final Properties oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(preferencesB64);
					if (oProperties != null) {
						final Properties exit = new Properties();

						final Enumeration enu = oProperties.keys();
						while (enu.hasMoreElements()) {
							final String sKeyName = enu.nextElement().toString();
							if (sKeyName.indexOf(user + "_") == 0) {
								final String sKey = sKeyName.replaceFirst(user + "_", "");
								exit.put(sKey, oProperties.get(sKeyName));
							}
						}

						final JFileChooser fc = this.getJFileChooser();
						final int op = fc.showSaveDialog(ControlApplicationPreferences.this);
						if (op != JFileChooser.APPROVE_OPTION) {
							return;
						}
						final FileOutputStream fOutput = new FileOutputStream(fc.getSelectedFile());
						exit.store(fOutput, "");
						fOutput.flush();
						fOutput.close();
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_IT_HAS_BEEN_EXPORTED_TO_FILE,
								JOptionPane.INFORMATION_MESSAGE,
								this.resource);
					}
				}
			} catch (final Exception ex) {
				ControlApplicationPreferences.logger.error(null, ex);
				MessageDialog.showMessage(ControlApplicationPreferences.this, ex.getMessage(),
						JOptionPane.ERROR_MESSAGE, this.resource);
			}
		}
	}

	protected void deleteUserPreferences() {
		final String user = this.locator.getUser();
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
			RemoteApplicationPreferences rAP;
			try {
				rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferencesB64 = rAP.getRemotePreference(sessionId, user,
							RemoteApplicationPreferences.KEY_USER_PREFERENCE);
					if (preferencesB64 != null) {
						rAP.setRemotePreference(sessionId, user, RemoteApplicationPreferences.KEY_USER_PREFERENCE,
								new String());
						rAP.saveRemotePreferences(sessionId);
						((BasicApplicationPreferences) prefs).setDirtyMode(true);
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_PREFERENCES_FOR_USER_HAVE_BEEN_DELETED,
								JOptionPane.INFORMATION_MESSAGE, this.resource);
					} else {
						((BasicApplicationPreferences) ApplicationManager.getApplication().getPreferences())
						.setUserPreferences(new Properties());
						((BasicApplicationPreferences) ApplicationManager.getApplication().getPreferences())
						.savePreferences();
						((BasicApplicationPreferences) ApplicationManager.getApplication().getPreferences())
						.setDirtyMode(true);
						MessageDialog.showMessage(ControlApplicationPreferences.this,
								ControlApplicationPreferences.M_PREFERENCES_FOR_USER_HAVE_BEEN_DELETED,
								JOptionPane.INFORMATION_MESSAGE, this.resource);
					}
				}
			} catch (final Exception ex) {
				ControlApplicationPreferences.logger.error(null, ex);
				MessageDialog.showMessage(ControlApplicationPreferences.this, ex.getMessage(),
						JOptionPane.ERROR_MESSAGE, this.resource);
			}
		}
	}

	public static Properties getDefaultUserPreferences(final String user, final ClientReferenceLocator locator) {
		if (locator != null) {
			RemoteApplicationPreferences rAP;
			try {
				rAP = ((RemoteApplicationPreferenceReferencer) locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) locator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferencesB64 = rAP.getRemotePreference(sessionId, null,
							ControlApplicationPreferences.DEFAULT_INIT_CONFIGURATION);

					Properties oProperties = null;
					if (preferencesB64 != null) {
						oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(preferencesB64);
					} else {
						final String oldPreferences = rAP.getRemotePreference(sessionId, null,
								ControlApplicationPreferences.KEY_INIT_PREFERENCE);
						if (oldPreferences == null) {
							return null;
						}
						oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(oldPreferences);
					}

					if (oProperties != null) {
						final Properties pref = oProperties;
						final Properties exitProperties = new Properties();
						final Enumeration enumer = pref.keys();
						while (enumer.hasMoreElements()) {
							final Object k = enumer.nextElement();
							final Object value = pref.get(k);
							exitProperties.put(user + "_" + k.toString(), value);
						}
						return exitProperties;
					}
				}
			} catch (final Exception ex) {
				ControlApplicationPreferences.logger.error(null, ex);
			}
		}
		return null;
	}

	protected void loadFileToPreferences() {

		final JFileChooser fc = this.getJFileChooser();
		final int op = fc.showOpenDialog(ControlApplicationPreferences.this);
		if (op == JFileChooser.CANCEL_OPTION) {
			return;
		}
		final File fSelected = fc.getSelectedFile();

		final Properties prop = new Properties();
		FileInputStream input;
		try {
			input = new FileInputStream(fSelected);
			prop.load(input);
			if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
				final RemoteApplicationPreferences rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());

				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();

					final String preferenciasB64 = BasicApplicationPreferences.parserPropertiesToStringBase64(prop);
					rAP.setRemotePreference(sessionId, null, ControlApplicationPreferences.DEFAULT_INIT_CONFIGURATION,
							preferenciasB64);
					rAP.saveRemotePreferences(sessionId);
					MessageDialog.showMessage(ControlApplicationPreferences.this,
							ControlApplicationPreferences.M_IT_HAS_BEEN_IMPORTED_FROM_FILE,
							JOptionPane.INFORMATION_MESSAGE,
							this.resource);
				}
			}
		} catch (final Exception e) {
			ControlApplicationPreferences.logger.error(null, e);
			MessageDialog.showMessage(ControlApplicationPreferences.this, e.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.resource);
		}
	}

	protected void loadFileToCurrentUserPreferences() {

		final String user = this.locator.getUser();

		final JFileChooser fc = this.getJFileChooser();
		final int op = fc.showOpenDialog(ControlApplicationPreferences.this);
		if (op == JFileChooser.CANCEL_OPTION) {
			return;
		}
		final File fSelected = fc.getSelectedFile();

		final Properties prop = new Properties();
		FileInputStream input;
		try {
			input = new FileInputStream(fSelected);
			prop.load(input);
			final Properties exit = new Properties();
			final Enumeration enumer = prop.keys();
			while (enumer.hasMoreElements()) {
				final Object k = enumer.nextElement();
				exit.put(user + "_" + k.toString(), prop.get(k));
			}

			final ApplicationPreferences currentPreferences = ApplicationManager.getApplication().getPreferences();
			if (currentPreferences instanceof BasicApplicationPreferences) {
				((BasicApplicationPreferences) currentPreferences).setUserPreferences(exit);
			}

			if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
				final RemoteApplicationPreferences rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferenciasB64 = BasicApplicationPreferences.parserPropertiesToStringBase64(exit);

					rAP.setRemotePreference(sessionId, user, RemoteApplicationPreferences.KEY_USER_PREFERENCE,
							preferenciasB64);
					rAP.saveRemotePreferences(sessionId);
					MessageDialog.showMessage(ControlApplicationPreferences.this,
							ControlApplicationPreferences.M_IT_HAS_BEEN_IMPORTED_FROM_FILE,
							JOptionPane.INFORMATION_MESSAGE,
							this.resource);
				}
			}
		} catch (final Exception e) {
			ControlApplicationPreferences.logger.error(null, e);
			MessageDialog.showMessage(ControlApplicationPreferences.this, e.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.resource);
		}
	}

	protected void setDefaultPreferences() {
		final String user = this.locator.getUser() + "_";
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();

		if (prefs instanceof BasicApplicationPreferences) {
			final Properties prop = new Properties();
			final Properties p = ((BasicApplicationPreferences) prefs).getUserPreferences();

			if (p != null) {
				final Enumeration enu = p.keys();
				while (enu.hasMoreElements()) {
					final String sKey = enu.nextElement().toString();
					if (sKey.indexOf(user) == 0) {
						final String s_Key = sKey.replaceFirst(user, "");
						prop.put(s_Key, p.get(sKey));
					}
				}
			}

			try {
				final String preferenciasB64 = BasicApplicationPreferences.parserPropertiesToStringBase64(prop);
				final RemoteApplicationPreferences rAP = ((RemoteApplicationPreferenceReferencer) this.locator)
						.getRemoteApplicationPreferences(((EntityReferenceLocator) this.locator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					rAP.setRemotePreference(sessionId, null, ControlApplicationPreferences.DEFAULT_INIT_CONFIGURATION,
							preferenciasB64);
					rAP.saveRemotePreferences(sessionId);
					MessageDialog.showMessage(ControlApplicationPreferences.this,
							ControlApplicationPreferences.M_IT_HAS_BEEN_SET_BY_DEFAULT, JOptionPane.INFORMATION_MESSAGE,
							this.resource);
				}
			} catch (final Exception e) {
				ControlApplicationPreferences.logger.error(null, e);
			}
		}
	}

	public static void showControlApplicationPreferences(final Frame frame, final ClientReferenceLocator clientLocator,
			final ResourceBundle bundle) {
		if (!ControlApplicationPreferences.checkRemotePreferences(frame, clientLocator, bundle)) {
			return;
		}
		if (ControlApplicationPreferences.sWindow == null) {
			ControlApplicationPreferences.sWindow = new ControlApplicationPreferences(frame, clientLocator, bundle);
			ApplicationManager.center(ControlApplicationPreferences.sWindow);
		} else {
			ControlApplicationPreferences.sWindow.setResourceBundle(bundle);
		}
		ControlApplicationPreferences.sWindow.setVisible(true);
	}

	public static ApplicationPreferences setRemoteProperties(
			final RemoteApplicationPreferenceReferencer remoteApplicationPreferenceLocator, final ApplicationPreferences pref) {
		if (remoteApplicationPreferenceLocator instanceof ClientReferenceLocator) {
			final String user = ((ClientReferenceLocator) remoteApplicationPreferenceLocator).getUser() + "_";
			try {
				final RemoteApplicationPreferences rAP = remoteApplicationPreferenceLocator
						.getRemoteApplicationPreferences(
								((EntityReferenceLocator) remoteApplicationPreferenceLocator).getSessionId());
				if (rAP != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					final String preferencesB64 = rAP.getRemotePreference(sessionId, null,
							ControlApplicationPreferences.DEFAULT_INIT_CONFIGURATION);
					Properties oProperties = null;
					if (preferencesB64 != null) {
						oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(preferencesB64);
					} else {
						final String oldPreferences = rAP.getRemotePreference(sessionId, null,
								ControlApplicationPreferences.KEY_INIT_PREFERENCE);
						if (oldPreferences == null) {
							return pref;
						}
						oProperties = BasicApplicationPreferences.parserStringBase64ToProperties(oldPreferences);
					}
					if (oProperties != null) {
						final Enumeration enu = oProperties.keys();
						while (enu.hasMoreElements()) {
							final String sKey = enu.nextElement().toString();
							pref.setPreference(user, sKey, oProperties.getProperty(sKey));
						}
					}
				}
			} catch (final Exception e) {
				ControlApplicationPreferences.logger.error(null, e);
			}
		}
		return pref;
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resource = resources;
		this.setTitle(ApplicationManager.getTranslation(ControlApplicationPreferences.DIALOG_TITLE, resources));
		if (this.buttonSet != null) {
			this.buttonSet.setText(
					ApplicationManager.getTranslation(ControlApplicationPreferences.T_SET_PREFERENCES, this.resource));
		}
		if (this.buttonLoad != null) {
			this.buttonLoad.setText(ApplicationManager
					.getTranslation(ControlApplicationPreferences.T_IMPORT_FROM_PREFERENCES_FILE, this.resource));
		}
		if (this.buttonSaveFile != null) {
			this.buttonSaveFile.setText(ApplicationManager
					.getTranslation(ControlApplicationPreferences.T_EXPORT_TO_PREFERENCES_FILE, this.resource));
		}
		if (this.bCurrentUserLoadFile != null) {
			this.bCurrentUserLoadFile.setText(ApplicationManager
					.getTranslation(ControlApplicationPreferences.T_IMPORT_FROM_USER_PREFERENCES_FILE, this.resource));
		}
		if (this.bCurrentUserSaveFile != null) {
			this.bCurrentUserSaveFile.setText(ApplicationManager
					.getTranslation(ControlApplicationPreferences.T_EXPORT_TO_USER_PREFERENCES_FILE, this.resource));
		}
		if (this.buttonDelete != null) {
			this.buttonDelete.setText(ApplicationManager
					.getTranslation(ControlApplicationPreferences.T_DELETE_USER_PREFERENCES, this.resource));
		}
		if (this.errorMessage != null) {
			this.errorMessage.setMessage(
					ApplicationManager.getTranslation(M_YOU_DO_NOT_HAVE_PERMISSION_TO_EXECUTE_ACTION, resources));
		}
	}

}
