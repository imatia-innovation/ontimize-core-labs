package com.ontimize.gui.preferences;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Form;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.util.Base64Utils;

/**
 * Basic implementation of the application preferences
 *
 * @version 1.0
 */

public class BasicApplicationPreferences extends AbstractApplicationPreferences {

	private static final Logger logger = LoggerFactory.getLogger(BasicApplicationPreferences.class);

	public static boolean remoteUserPreferences = false;

	public static boolean checkOldPreferences = true;

	public boolean dirtyMode = false;

	public static boolean DEBUG = false;

	public static final String APP_LOCALE = "app_locale";

	public static final String APP_CONNECT_TO = "app_connect_to";

	/**
	 * Name of the preference to set the even table rows background color
	 */
	public static final String TABLE_EVEN_ROWS_COLOR = "table_even_rows_color";

	public static final String TABLE_VISIBLE_COLS = "table_visible_cols";

	public static final String TABLE_CONTROL_PANEL = "table_control_panel";

	public static final String TABLE_CONF_SORT_FILTER = "table_conf_sort_filter";

	public static final String TABLE_CONF_SORT_FILTER_DYNAMIC_PIVOT_TABLE = "table_conf_sort_filter_dynamic_pivot_table";

	public static final String TABLE_D_PIVOT_SORT_FILTER = "table_d_pivot_conf_sort_filter";

	public static final String TABLE_CONF_SORT_FILTER_CONFIGURATIONS = "table_conf_sort_filter_configurations";

	public static final String TABLE_CONF_SORT_FILTER_CONFIGURATIONS_DYNAMIC_PIVOT_TABLE = "table_conf_sort_filter_configurations_dynamic_pivot_table";

	public static final String TABLE_CONF_REPORT_CONFIGURATIONS = "table_conf_report_configurations";

	public static final String TABLE_CONF_REPORT_CONFIGURATIONS_DYNAMIC_PIVOT_TABLE = "table_conf_report_configurations_dynamic_pivot_table";

	public static final String TABLE_CONF_CHART_CONFIGURATIONS = "table_conf_chart_configurations";

	public static final String TABLE_COLS_POSITION_SIZE = "table_cols_position_size";

	public static final String TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS = "table_conf_pivot_table_configurations";

	public static final String TABLE_CALCULATED_COLUMNS_CONFIGURATION = "table_conf_calculated_columns";


	/**
	 * Name of the preference to set the required fields background color
	 */
	public static final String REQUIRED_FIELDS_BG_COLOR = "required_fields_bg_color";

	public static final String FOCUSED_FIELD_BG_COLOR = "focused_field_bg_color";

	public static final String SHOW_TIPS = "show_tips";

	/**
	 * Name of the preference to show/hide the table controls
	 */
	public static final String SHOW_TABLE_CONTROLS = "show_table_controls";

	/**
	 * Name of the preference to show/hide the column with the rows number in a table
	 */
	public static final String SHOW_TABLE_NUM_ROW = "show_table_number_row";

	/**
	 * Name of the preference to set the application window position
	 */
	public static final String APP_WINDOW_POSITION = "app_window_position";

	/**
	 * Name of the preference to set the application window state
	 */
	public static final String APP_WINDOW_STATE = "app_window_state";

	/**
	 * Name of the preference to set the application window size
	 */
	public static final String APP_WINDOW_SIZE = "app_window_size";

	/**
	 * Name of the preference to show/hide the application status bar
	 */
	public static final String APP_STATUS_BAR_VISIBLE = "app_status_bar_visible";

	/**
	 * Name of the preference to show/hide the application buttons bar
	 */
	public static final String APP_TOOL_BAR_VISIBLE = "app_tool_bar_visible";

	/**
	 * Name of the preference to configure button toolbar layout
	 */
	public static final String APP_TOOL_BAR_CONFIG = "app_tool_bar_config";

	/**
	 * Name of the preference to set the application font size
	 */
	public static final String APP_FONTSIZE = "app_font_size";

	/**
	 * Name of the preference to adjust automatically the available space for the trees
	 */
	public static final String ADJUST_TREE_SPACE = "adjust_tree_space";

	public static boolean ADJUST_TREE_SPACE_VALUE = true;

	/**
	 * Name of the preference to remember or not the last login used
	 */
	public static final String APP_REMEMBER_LAST_LOGIN = "app_remember_last_login";

	/**
	 * Name of the preference to store the last login used in the application
	 */
	public static final String APP_LAST_LOGIN = "app_last_login";

	public static final String APP_REMEMBER_LAST_PASSWORD = "app_remember_last_password";

	public static final String APP_LAST_PASSWORD = "app_last_password";

	public static final String APP_USER_DIR = "user_dir";

	/**
	 * Name of the preference to store the application look and feel
	 */
	public static final String LOOK_AND_FEEL_CLASS_NAME = "look_and_feel_classname";

	public static final String SPLIT_POSITION = "split_position";

	public static final String COLLAPSIBLE_PANEL_VISIBLE = "collapsible_visible";

	public static final String DETAIL_DIALOG_SIZE_POSITION = "detail_dialog_size_position";

	public static final String DIALOG_SIZE_POSITION = "dialog_size_position";

	public static final String APP_TOOLBAR_LOCATION = "app_toolbar_location";

	public static final String MENU_ACCELERATOR = "menu_accelerator";

	public static final String FORM_TABLEVIEW_COLUMNS = "form_tableview_columns";

	protected JDialog dPreferences = null;

	protected String file = null;

	protected String basePath = null;

	protected JDialog colorDialog = null;

	protected JPanel panelTableColor = null;

	protected JPanel panelRequiredBackgroundColor = null;

	protected JPanel panelFieldBackgroundColor = null;

	protected static String T_COLORS = "T_COLORS";

	protected static String L_EVEN_COLOR_TABLE_ROW = "applicationpreferences.background_colour_of_event_table_rows";

	protected static String L_REQUIRED_BACKGROUND_COLOR_FIELD = "applicationpreferences.background_color_of_required_fields";

	protected static String L_FOCUS_COLOR_FIELD = "applicationpreferences.background_colour_of_field_with_focus";

	protected JLabel labelTableColor = null;

	protected JLabel labelRequiredBackgroungColor = null;

	protected JLabel backgroundColorFieldLabel = null;

	protected JButton okButton = null;

	protected JButton cancelButton = null;

	protected JButton restoreButton = null;

	protected RemoteApplicationPreferences rprefs = null;

	protected boolean loadDeaults = true;

	/**
	 * Creates new BasicApplicationPreferences object with the preferences stored in the file
	 * <code>file</code>.
	 * @param file Name of the file (without path) that contains the application preferences. This file
	 *        must be in the user directory
	 */
	public BasicApplicationPreferences(final String file) {
		this(file, (RemoteApplicationPreferences) null);
	}

	/**
	 * Creates a BasicApplicationPreferences object with the preferences stored in the file
	 * <code>file</code>.
	 * @param file Name of the preferences file
	 * @param basePath Path where the file is
	 */
	public BasicApplicationPreferences(final String file, final String basePath) {
		this(file, basePath, null);
	}

	/**
	 * Creates a BasicApplicationPreferences object with the preferences stored in the file
	 * <code>file</code>.
	 * @param file Name of the file (without path). This file must be in the user directory
	 * @param rp Remote preferences
	 */
	public BasicApplicationPreferences(final String file, final RemoteApplicationPreferences rp) {
		super();
		this.file = file;
		this.basePath = System.getProperty("user.home");
		if (this.basePath != null) {
			if (this.basePath.charAt(this.basePath.length() - 1) != '/') {
				this.basePath = this.basePath + "/";
			}
		} else {
			this.basePath = "";
		}
		this.rprefs = rp;
	}

	/**
	 * Creates a BasicApplicationPreferences object with the preferences stored in the file
	 * <code>file</code>.
	 * @param file Name of the file
	 * @param basePath Path where the file is
	 * @param rp Remote preferences
	 */
	public BasicApplicationPreferences(final String file, String basePath, final RemoteApplicationPreferences rp) {
		super();
		this.file = file;
		if (basePath != null) {
			if (basePath.charAt(basePath.length() - 1) != '/') {
				basePath = basePath + "/";
			}
		} else {
			basePath = "";
		}
		this.basePath = basePath;
		this.rprefs = rp;
	}

	public String getFile() {
		return this.file;
	}

	public void setFile(final String file) {
		this.file = file;
	}

	public String getBasePath() {
		return this.basePath;
	}

	public void setBasePath(final String basePath) {
		this.basePath = basePath;
	}

	public void setLoadDefaults(final boolean value) {
		this.loadDeaults = value;
	}

	@Override
	public void loadPreferences() {
		// Get the preference file
		try {
			if (this.rprefs != null) {
				final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
				this.rprefs.loadRemotePreferences(sessionId);
				// First of all try to load the user remote preference if the
				// remoteUserPreference option is enabled
				if (BasicApplicationPreferences.remoteUserPreferences) {
					final String user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator())
							.getUser();
					if ((user != null) && (user.length() > 0)) {
						final String pref = this.rprefs.getRemotePreference(sessionId, user,
								RemoteApplicationPreferences.KEY_USER_PREFERENCE);
						if (pref == null) {
							Properties prop = null;
							if (BasicApplicationPreferences.checkOldPreferences) {
								// Get the old preferences for this user
								prop = this.checkOldPreferences(user);
							}

							if (prop != null) {
								this.userProps = prop;
							} else {
								// If the user has not remote preference then it
								// uses
								// the server default preferences
								this.userProps = ControlApplicationPreferences.getDefaultUserPreferences(user,
										(ClientReferenceLocator) ApplicationManager.getApplication()
										.getReferenceLocator());
								if (this.userProps == null) {
									this.userProps = new Properties();
								}
							}
						} else {
							this.userProps = BasicApplicationPreferences.parserStringBase64ToProperties(pref);
						}

						this.defaultUserProps = ControlApplicationPreferences.getDefaultUserPreferences(user,
								(ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator());
						if (this.defaultUserProps == null) {
							this.defaultUserProps = new Properties();
						}
					}
				}
			}
		} catch (final Exception ex) {
			BasicApplicationPreferences.logger.trace(null, ex);
		}

		final File f = new File(this.basePath, this.file);
		if (!f.exists()) {
			this.loadDefaults();
		} else {
			FileInputStream fIn = null;
			try {
				fIn = new FileInputStream(f);
				this.props.load(fIn);
			} catch (final Exception e) {
				BasicApplicationPreferences.logger.trace(null, e);
				this.loadDefaults();
			} finally {
				if (fIn != null) {
					try {
						fIn.close();
					} catch (final Exception e) {
						BasicApplicationPreferences.logger.trace(null, e);
					}
				}
			}
		}
	}

	/**
	 * Initializes the default preference values
	 */
	protected void loadDefaults() {
		if (this.loadDeaults) {
			// Application window size
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setPreference(null, BasicApplicationPreferences.APP_WINDOW_SIZE,
					ApplicationManager.parseDimensionValue(new Dimension(d.width - 50, d.height - 50)));
			this.setPreference(null, BasicApplicationPreferences.APP_STATUS_BAR_VISIBLE,
					ApplicationManager.parseBooleanValue(true));
			this.setPreference(null, BasicApplicationPreferences.APP_TOOL_BAR_VISIBLE,
					ApplicationManager.parseBooleanValue(true));

			// Application window position
			this.setPreference(null, BasicApplicationPreferences.APP_WINDOW_POSITION,
					ApplicationManager.parseDimensionValue(new Dimension(25, 25)));

			// Table preference
			this.setPreference(null, BasicApplicationPreferences.SHOW_TABLE_CONTROLS,
					ApplicationManager.parseBooleanValue(true));
			this.setPreference(null, BasicApplicationPreferences.SHOW_TABLE_NUM_ROW,
					ApplicationManager.parseBooleanValue(false));

			// Show tips
			this.setPreference(null, BasicApplicationPreferences.SHOW_TIPS, ApplicationManager.parseBooleanValue(true));

			this.setPreference(null, BasicApplicationPreferences.ADJUST_TREE_SPACE,
					ApplicationManager.parseBooleanValue(BasicApplicationPreferences.ADJUST_TREE_SPACE_VALUE));

			this.setPreference(null, BasicApplicationPreferences.APP_REMEMBER_LAST_LOGIN,
					ApplicationManager.parseBooleanValue(false));
			this.setPreference(null, BasicApplicationPreferences.APP_LAST_LOGIN, "");

			if (!ApplicationManager.useOntimizePlaf) {
				this.setPreference(null, BasicApplicationPreferences.APP_FONTSIZE, "12");
			}
		}
	}

	@Override
	public String getPreference(final String user, final String preferenceName) {

		final String key = this.getKeyPreference(user, preferenceName);
		if (BasicApplicationPreferences.remoteUserPreferences) {
			try {
				if (user != null) {
					String p = this.userProps.getProperty(key);
					if (p != null) {
						return p;
					}
					p = this.defaultUserProps.getProperty(key);
					if (p != null) {
						return p;
					}
				}
			} catch (final Exception e) {
				BasicApplicationPreferences.logger.trace(null, e);
			}
		}

		final String prop = this.props.getProperty(key);
		if (BasicApplicationPreferences.DEBUG) {
			BasicApplicationPreferences.logger
			.debug("Requeste preference " + preferenceName + " for the user: " + user + ", value: " + prop);
		}
		if (prop == null) {
			if (BasicApplicationPreferences.DEBUG) {
				BasicApplicationPreferences.logger.debug("Preference " + preferenceName + " not found for the user: "
						+ user + ". Returns the default preference value");
			}
			// Get the default value if it exists
			final String sDefaultProperty = this.props.getProperty(preferenceName);
			if (sDefaultProperty == null) {
				if (BasicApplicationPreferences.DEBUG) {
					BasicApplicationPreferences.logger
					.debug("Default preference " + preferenceName + " not found: request the default value");
				}
				final String propDef = this.getDefault(preferenceName);
				if (BasicApplicationPreferences.DEBUG) {
					BasicApplicationPreferences.logger.debug("Default value " + propDef + " for: " + preferenceName);
				}
				return propDef;
			} else {
				return sDefaultProperty;
			}
		}
		return prop;
	}

	/**
	 * Gets the default value for the specified preference
	 * @param preferenceName
	 * @return
	 */
	protected String getDefault(final String preferenceName) {
		if (preferenceName != null) {
			if (preferenceName.equals(BasicApplicationPreferences.APP_WINDOW_SIZE)) {
				// Application window size
				final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				return ApplicationManager.parseDimensionValue(new Dimension(d.width - 50, d.height - 50));
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_STATUS_BAR_VISIBLE)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_TOOL_BAR_VISIBLE)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_WINDOW_POSITION)) {
				return ApplicationManager.parseDimensionValue(new Dimension(25, 25));
			} else if (preferenceName.equals(BasicApplicationPreferences.SHOW_TABLE_CONTROLS)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.SHOW_TABLE_NUM_ROW)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.SHOW_TIPS)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.ADJUST_TREE_SPACE)) {
				return ApplicationManager.parseBooleanValue(true);
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_REMEMBER_LAST_LOGIN)) {
				return ApplicationManager.parseBooleanValue(false);
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_FONTSIZE)) {
				return "12";
			} else if (preferenceName.equals(BasicApplicationPreferences.APP_LAST_LOGIN)) {
				return "";
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setPreference(final String user, final String preferenceName, final String value) {
		if (!this.dirtyMode) {
			if (BasicApplicationPreferences.remoteUserPreferences) {
				if (user == null) {
					if (value != null) {
						this.props.setProperty(preferenceName, value);
					} else {
						this.props.remove(preferenceName);
					}
				} else {
					final String key = this.getKeyPreference(user, preferenceName);
					if (value != null) {
						this.userProps.setProperty(key, value);
					} else {
						this.userProps.remove(key);
					}
				}
			} else {
				final String key = this.getKeyPreference(user, preferenceName);
				if (value != null) {
					this.props.setProperty(key, value);
				} else {
					this.props.remove(key);
				}
			}

			if (value != null) {
				if (BasicApplicationPreferences.APP_LOCALE.equals(preferenceName)) {
					this.props.setProperty(BasicApplicationPreferences.APP_LOCALE, value);
				}
			}

			this.fireApplicationPreferencesChanged(user, preferenceName, value);
		}
	}

	@Override
	public synchronized void savePreferences() {

		if (BasicApplicationPreferences.remoteUserPreferences) {
			try {
				if (this.rprefs != null) {
					// First of all stores the user application in the server
					final String user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator())
							.getUser();
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					if ((user != null) && (user.length() > 0)) {
						if (this.userProps == null) {
							this.rprefs.setRemotePreference(sessionId, user,
									RemoteApplicationPreferences.KEY_USER_PREFERENCE, null);
						} else {
							final String pref = BasicApplicationPreferences.parserPropertiesToStringBase64(this.userProps);
							this.rprefs.setRemotePreference(sessionId, user,
									RemoteApplicationPreferences.KEY_USER_PREFERENCE, pref);
						}
						this.rprefs.saveRemotePreferences(sessionId);
					}
				}
			} catch (final Exception ex) {
				BasicApplicationPreferences.logger.error(null, ex);
			}
		}

		final File f = new File(this.basePath, this.file);

		FileOutputStream fOut = null;
		try {
			fOut = new FileOutputStream(f);
			this.props.store(fOut, "Application Preferences " + new java.util.Date().toString());
		} catch (final Exception e) {
			BasicApplicationPreferences.logger.error("Error saving preferences", e);
		} finally {
			if (fOut != null) {
				try {
					fOut.close();
				} catch (final Exception e) {
					BasicApplicationPreferences.logger.trace(null, e);
				}
			}
		}
	}

	@Override
	public void showPreferencesDialog(final String user, final String formManager, final String form, final Application a) {
		if (this.dPreferences == null) {
			final IFormManager gForms = a.getFormManager(formManager);
			final Form f = gForms.getFormCopy(form);
			this.dPreferences = f.putInModalDialog();
		}
		this.dPreferences.setVisible(true);
	}

	public void showColorChooserDialog(final Window parent) {
		this.createColorDialog(parent);
		this.setResourceBundleColorChooser();
		this.colorDialog.pack();
		ApplicationManager.center(this.colorDialog);
		this.colorDialog.setVisible(true);
	}

	protected void setResourceBundleColorChooser() {
		if (this.colorDialog != null) {
			this.colorDialog.setTitle(ApplicationManager.getTranslation(BasicApplicationPreferences.T_COLORS));
			this.labelTableColor
			.setText(ApplicationManager.getTranslation(BasicApplicationPreferences.L_EVEN_COLOR_TABLE_ROW));
			this.labelRequiredBackgroungColor.setText(
					ApplicationManager.getTranslation(BasicApplicationPreferences.L_REQUIRED_BACKGROUND_COLOR_FIELD));
			this.backgroundColorFieldLabel
			.setText(ApplicationManager.getTranslation(BasicApplicationPreferences.L_FOCUS_COLOR_FIELD));
			this.okButton.setText(ApplicationManager.getTranslation("application.accept"));
			this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel"));
			this.restoreButton.setText(ApplicationManager.getTranslation("applicationpreferences.restore"));
		}
	}

	protected void createColorDialog(final Window parent) {
		if (this.colorDialog == null) {
			if (parent instanceof Dialog) {
				this.colorDialog = new EJDialog((Dialog) parent, BasicApplicationPreferences.T_COLORS, true);
			} else {
				this.colorDialog = new EJDialog((Frame) parent, BasicApplicationPreferences.T_COLORS, true);
			}

			this.colorDialog.setContentPane(new JPanel(new GridBagLayout()));
			((JPanel) this.colorDialog.getContentPane()).setBorder(BorderFactory.createEtchedBorder());
			this.panelTableColor = new JPanel();
			this.panelTableColor.setPreferredSize(new Dimension(60, 18));
			this.panelTableColor.setMinimumSize(new Dimension(60, 18));
			this.panelTableColor.setMaximumSize(new Dimension(60, 18));

			this.panelRequiredBackgroundColor = new JPanel();
			this.panelRequiredBackgroundColor.setPreferredSize(new Dimension(60, 18));
			this.panelRequiredBackgroundColor.setMinimumSize(new Dimension(60, 18));
			this.panelRequiredBackgroundColor.setMaximumSize(new Dimension(60, 18));

			this.panelFieldBackgroundColor = new JPanel();
			this.panelFieldBackgroundColor.setPreferredSize(new Dimension(60, 18));
			this.panelFieldBackgroundColor.setMinimumSize(new Dimension(60, 18));
			this.panelFieldBackgroundColor.setMaximumSize(new Dimension(60, 18));

			this.panelRequiredBackgroundColor.setBackground(DataField.requiredFieldBackgroundColor);
			this.panelTableColor.setBackground(CellRenderer.getEvenRowBackgroundColor());
			this.panelFieldBackgroundColor.setBackground(DataComponent.COMP_FOCUS_YELLOW);

			this.panelRequiredBackgroundColor.setBorder(BorderFactory.createEtchedBorder());
			this.panelTableColor.setBorder(BorderFactory.createEtchedBorder());
			this.panelFieldBackgroundColor.setBorder(BorderFactory.createEtchedBorder());

			this.labelTableColor = new JLabel("applicationpreferences.background_colour_of_event_table_rows");
			this.labelRequiredBackgroungColor = new JLabel(
					"applicationpreferences.background_color_of_required_fields");
			this.backgroundColorFieldLabel = new JLabel("applicationpreferences.background_colour_of_field_with_focus");

			this.colorDialog.getContentPane()
			.add(this.labelTableColor,
					new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 5, 5, 10), 0, 0));
			this.colorDialog.getContentPane()
			.add(this.labelRequiredBackgroungColor,
					new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 5, 5, 10), 0, 0));
			this.colorDialog.getContentPane()
			.add(this.backgroundColorFieldLabel,
					new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 5, 5, 10), 0, 0));

			this.colorDialog.getContentPane()
			.add(this.panelTableColor,
					new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.EAST,
							new Insets(5, 5, 5, 5), 0, 0));
			this.colorDialog.getContentPane()
			.add(this.panelRequiredBackgroundColor,
					new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.EAST,
							new Insets(5, 5, 5, 5), 0, 0));

			this.colorDialog.getContentPane()
			.add(this.panelFieldBackgroundColor,
					new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.EAST,
							new Insets(5, 5, 5, 5), 0, 0));

			final JButton jbChooseTableColorButton = new JButton("..");
			final JButton jbChooseRequiredFieldsColorButton = new JButton("..");
			final JButton jbChooseFocusColorButton = new JButton("..");

			this.colorDialog.getContentPane()
			.add(jbChooseTableColorButton,
					new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.WEST,
							new Insets(0, 0, 0, 0), 0, 0));

			this.colorDialog.getContentPane()
			.add(jbChooseRequiredFieldsColorButton,
					new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.WEST,
							new Insets(0, 0, 0, 0), 0, 0));

			this.colorDialog.getContentPane()
			.add(jbChooseFocusColorButton,
					new GridBagConstraints(2, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.WEST,
							new Insets(0, 0, 0, 0), 0, 0));

			jbChooseTableColorButton.setMargin(new Insets(0, 2, 0, 2));
			jbChooseRequiredFieldsColorButton.setMargin(new Insets(0, 2, 0, 2));
			jbChooseFocusColorButton.setMargin(new Insets(0, 2, 0, 2));

			jbChooseRequiredFieldsColorButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Color c = JColorChooser.showDialog(BasicApplicationPreferences.this.colorDialog, "Color",
							BasicApplicationPreferences.this.panelRequiredBackgroundColor.getBackground());
					if (c != null) {
						BasicApplicationPreferences.this.panelRequiredBackgroundColor.setBackground(c);
					}
				}
			});

			jbChooseTableColorButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Color c = JColorChooser.showDialog(BasicApplicationPreferences.this.colorDialog, "Color",
							BasicApplicationPreferences.this.panelTableColor.getBackground());
					if (c != null) {
						BasicApplicationPreferences.this.panelTableColor.setBackground(c);
					}
				}
			});

			jbChooseFocusColorButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Color c = JColorChooser.showDialog(BasicApplicationPreferences.this.colorDialog, "Color",
							BasicApplicationPreferences.this.panelFieldBackgroundColor.getBackground());
					if (c != null) {
						BasicApplicationPreferences.this.panelFieldBackgroundColor.setBackground(c);
					}
				}
			});

			this.okButton = new JButton("application.accept");
			this.cancelButton = new JButton("application.cancel");
			this.restoreButton = new JButton("applicationpreferences.restore");

			final JPanel jbButtonsPanel = new JPanel(new GridLayout(1, 0));
			jbButtonsPanel.add(this.okButton);
			jbButtonsPanel.add(this.cancelButton);
			jbButtonsPanel.add(this.restoreButton);

			this.okButton.setIcon(ImageManager.getIcon(ImageManager.OK));
			this.cancelButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
			this.restoreButton.setIcon(ImageManager.getIcon(ImageManager.UNDO));

			this.colorDialog.getContentPane()
			.add(jbButtonsPanel,
					new GridBagConstraints(0, 3, 3, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
							new Insets(10, 5, 5, 5), 0, 0));

			this.okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					BasicApplicationPreferences.this.colorDialog.setVisible(false);
					BasicApplicationPreferences.this.setPreference(null,
							BasicApplicationPreferences.TABLE_EVEN_ROWS_COLOR,
							ColorConstants
							.colorToRGB(BasicApplicationPreferences.this.panelTableColor.getBackground()));
					BasicApplicationPreferences.this.setPreference(null,
							BasicApplicationPreferences.REQUIRED_FIELDS_BG_COLOR,
							ColorConstants.colorToRGB(
									BasicApplicationPreferences.this.panelRequiredBackgroundColor.getBackground()));
					BasicApplicationPreferences.this.setPreference(null,
							BasicApplicationPreferences.FOCUSED_FIELD_BG_COLOR,
							ColorConstants.colorToRGB(
									BasicApplicationPreferences.this.panelFieldBackgroundColor.getBackground()));

					BasicApplicationPreferences.this.savePreferences();
				}
			});
			this.cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					BasicApplicationPreferences.this.colorDialog.setVisible(false);
				}
			});
			this.restoreButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					BasicApplicationPreferences.this.panelTableColor.setBackground(DataComponent.VERY_LIGHT_SKYBLUE);
					BasicApplicationPreferences.this.panelRequiredBackgroundColor
					.setBackground(DataComponent.VERY_LIGHT_SKYBLUE);
					BasicApplicationPreferences.this.panelFieldBackgroundColor
					.setBackground(DataComponent.COMP_FOCUS_YELLOW);
				}
			});

			this.colorDialog.pack();
		}
	}

	@Override
	public void setRemoteApplicationPreferences(final RemoteApplicationPreferences rp) {
		this.rprefs = rp;
		if (BasicApplicationPreferences.remoteUserPreferences) {
			try {
				if (this.rprefs != null) {
					final int sessionId = ApplicationManager.getApplication().getReferenceLocator().getSessionId();
					this.rprefs.loadRemotePreferences(sessionId);
					// First of all try to load the user remote preferences
					final String user = ((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator())
							.getUser();
					if ((user != null) && (user.length() > 0)) {
						final String pref = this.rprefs.getRemotePreference(sessionId, user,
								RemoteApplicationPreferences.KEY_USER_PREFERENCE);
						if (pref == null) {
							Properties prop = null;
							if (BasicApplicationPreferences.checkOldPreferences) {
								prop = this.checkOldPreferences(user);
							}
							if (prop != null) {
								this.userProps = prop;
							} else {
								// Use the default server preferences
								this.userProps = ControlApplicationPreferences.getDefaultUserPreferences(user,
										(ClientReferenceLocator) ApplicationManager.getApplication()
										.getReferenceLocator());
								if (this.userProps == null) {
									this.userProps = new Properties();
								}
							}
						} else {
							this.userProps = BasicApplicationPreferences.parserStringBase64ToProperties(pref);
						}

						this.defaultUserProps = ControlApplicationPreferences.getDefaultUserPreferences(user,
								(ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator());
						if (this.defaultUserProps == null) {
							this.defaultUserProps = new Properties();
						}
					}
				}
			} catch (final Exception e) {
				BasicApplicationPreferences.logger.trace(null, e);
			}
		}
	}

	@Override
	public RemoteApplicationPreferences getRemoteApplicationPreferences() {
		return this.rprefs;
	}

	public static String parserPropertiesToStringBase64(final Properties properties) {
		try {
			final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			// commented in 5.2078EN-0.1 due to corrupted \u0000\u0000 appeared
			// in preference files
			// ObjectOutputStream objectOut = new ObjectOutputStream(bOut);
			// objectOut.writeObject(properties);
			// objectOut.flush();
			properties.store(bOut, new String());
			final byte[] bPreference = bOut.toByteArray();
			final String preferencias = new String(Base64Utils.encode(bPreference));
			return preferencias;
		} catch (final Exception e) {
			BasicApplicationPreferences.logger.error(null, e);
		}
		return null;
	}

	public static Properties parserStringBase64ToProperties(final String properties) {
		try {
			final Properties p = new Properties();
			if (properties.length() == 0) {
				return new Properties();
			}

			try {
				final Properties oldProperties = BasicApplicationPreferences.retrieveProperties(properties);
				if (oldProperties != null) {
					return oldProperties;
				}
			} catch (final Exception e) {
				if (BasicApplicationPreferences.DEBUG) {
					BasicApplicationPreferences.logger.error(null, e);
				} else {
					BasicApplicationPreferences.logger.trace(null, e);
				}
			}

			final byte[] array = Base64Utils.decode(properties.toCharArray());
			p.load(new ByteArrayInputStream(array));
			return p;
			// commented in 5.2078EN-0.1 due to corrupted \u0000\u0000 appeared
			// in preference files
			// ByteArrayInputStream bIn = new ByteArrayInputStream(array);
			// ObjectInputStream objectInput = new ObjectInputStream(bIn);
			// Object o = objectInput.readObject();
			// if (o instanceof Properties) {
			// return (Properties) o;
			// }
		} catch (final Exception e) {
			BasicApplicationPreferences.logger.error(null, e);
		}
		return null;
	}

	// retrieve preferences serializing property object.
	protected static Properties retrieveProperties(final String properties) throws Exception {
		// commented in 5.2078EN-0.1 due to corrupted \u0000\u0000 appeared in
		// preference files
		final byte[] array = Base64Utils.decode(properties.toCharArray());
		final ByteArrayInputStream bIn = new ByteArrayInputStream(array);
		final ObjectInputStream objectInput = new ObjectInputStream(bIn);
		final Object o = objectInput.readObject();
		if (o instanceof Properties) {
			return (Properties) o;
		}
		throw new Exception("error retrieving serialized property object.");
	}

	public static Properties getUserLocalPreferences(final String user, final Properties properties) {
		final String userTemplate = user + "_";
		if (properties != null) {
			final Properties prop = new Properties();
			final Enumeration enu = properties.keys();
			while (enu.hasMoreElements()) {
				final String sKey = enu.nextElement().toString();
				if (sKey.indexOf(userTemplate) == 0) {
					prop.put(sKey, properties.get(sKey));
				}
			}
			if (prop.isEmpty()) {
				return null;
			}
			return prop;
		}
		return null;
	}

	public Properties getUserPreferences() {
		if (BasicApplicationPreferences.remoteUserPreferences) {
			return this.userProps;
		} else {
			return this.props;
		}
	}

	protected void setUserPreferences(final Properties current) {
		if (BasicApplicationPreferences.remoteUserPreferences) {
			this.userProps = current;
		} else {
			this.props = current;
		}
	}

	/**
	 * This method extract the user preferences of old preferences file.
	 * @param user
	 * @return
	 */
	protected Properties checkOldPreferences(final String user) {
		String fileOld = this.file;
		final int index = fileOld.indexOf(".init");
		if (index == -1) {
			return null;
		}
		fileOld = fileOld.substring(0, index) + ".old";

		final File f = new File(this.basePath, fileOld);

		FileInputStream fIn = null;
		try {
			fIn = new FileInputStream(f);
			final Properties properties = new Properties();
			properties.load(fIn);
			return BasicApplicationPreferences.getUserLocalPreferences(user, properties);
		} catch (final Exception e) {
			BasicApplicationPreferences.logger.error("Old preferences can't be loaded", e);
		} finally {
			if (fIn != null) {
				try {
					fIn.close();
				} catch (final Exception e) {
					BasicApplicationPreferences.logger.trace(null, e);
				}
			}
		}
		return null;
	}

	public Properties getAllPreferences() {
		final Properties props = this.getProperties();

		if (!props.isEmpty()) {

			final String basePath = this.getBasePath();
			final String file = this.getFile();
			if ((basePath == null) || (file == null)) {
				return null;
			}

			final File f = new File(basePath, file);
			if (!f.exists()) {
				BasicApplicationPreferences.logger.debug("Server Preferences file does not exist!");
				return null;
			} else {
				FileInputStream fIn = null;
				try {
					fIn = new FileInputStream(f);
					props.load(fIn);
				} catch (final Exception e) {
					BasicApplicationPreferences.logger.error(null, e);
				} finally {
					if (fIn != null) {
						try {
							fIn.close();
						} catch (final Exception e) {
							BasicApplicationPreferences.logger.trace(null, e);
						}
					}
				}
			}
		}
		return props;
	}

	public boolean isDirtyMode() {
		return this.dirtyMode;
	}

	public void setDirtyMode(final boolean dirtyMode) {
		this.dirtyMode = dirtyMode;
	}

}
