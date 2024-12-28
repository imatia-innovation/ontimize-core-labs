package com.ontimize.gui.button;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.i18n.ConfigureFormBundleDialog;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.i18n.IConfigureFormBundleGUI;
import com.ontimize.util.ParseUtils;

public class FormBundleButton extends FormHeaderButton implements InteractionManagerModeListener {

	private static final Logger logger = LoggerFactory.getLogger(FormBundleButton.class);

	public static String defaultFormBundleTip = "bundle.configure_form_bundle";

	public static final String FORM_BUNDLE_BUTTON_DEFAULT_KEY = "FormBundleButton";

	public static final String REMOTE_OBJECT_NAME = "remoteobjectname";

	public static final String CONFIGURATION_DIALOG_CLASS = "configurationdialogclass";

	public static final String CONFIGURATION_DIALOG_TITLE = "dialogtitle";

	public static final String ENABLED_MODES = "enabledmodes";

	public static String defaultConfigutationDialogTitle = "bundleConfigurationDialog";

	protected String dialogTitle;

	protected String remoteObjectName;

	protected String configurationDialogClassName;

	protected List enabledModes;

	protected IConfigureFormBundleGUI configurationDialog;

	public FormBundleButton(final Map parameters) {
		super(parameters);
		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FormBundleButton.this.configureTranslations();
				} catch (final Exception e1) {
					FormBundleButton.this.getParentForm().message(e1.getMessage(), Form.ERROR_MESSAGE, e1);
				}
			}
		});
	}

	/**
	 * This method gets the <code>Hashtable</code> and configure the button. This class use all the
	 * parameters of {@link Button}
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
	 *        <td>dialogtitle</td>
	 *        <td><i></td>
	 *        <td>bundleConfigurationDialog</td>
	 *        <td>no</td>
	 *        <td>Title to use in the configuration dialog.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>remoteobjectname</td>
	 *        <td><i></td>
	 *        <td>DatabaseBundle</td>
	 *        <td>no</td>
	 *        <td>Name of the remote object configured in the remote references file.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>configurationdialogclass</td>
	 *        <td><i></td>
	 *        <td>com.ontimize.gui.i18n.ConfigureFormBundleDialog</td>
	 *        <td>no</td>
	 *        <td>Name of the class to show to configure the resource bundle values. This class must
	 *        implements IConfigureFormBundleGUI.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>enabledmodes</td>
	 *        <td>query;insert;queryinsert;update<i></td>
	 *        <td>queryinsert;update</td>
	 *        <td>no</td>
	 *        <td>Modes of the interaction manager when this button must be enabled</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		this.dialogTitle = ParseUtils.getString((String) parameters.get(FormBundleButton.CONFIGURATION_DIALOG_TITLE),
				FormBundleButton.defaultConfigutationDialogTitle);
		this.remoteObjectName = ParseUtils.getString((String) parameters.get(FormBundleButton.REMOTE_OBJECT_NAME),
				ExtendedPropertiesBundle.getDbBundleManagerName());
		this.configurationDialogClassName = ParseUtils
				.getString((String) parameters.get(FormBundleButton.CONFIGURATION_DIALOG_CLASS), null);
		final String modes = ParseUtils.getString((String) parameters.get(FormBundleButton.ENABLED_MODES), null);
		if (modes == null) {
			this.enabledModes = new Vector(2);
			this.enabledModes.add(new Integer(InteractionManager.QUERYINSERT));
			this.enabledModes.add(new Integer(InteractionManager.UPDATE));
		} else {
			this.enabledModes = new Vector(2);
			final List v = ApplicationManager.getTokensAt(modes.toLowerCase(), ";");
			if (v.contains("query")) {
				this.enabledModes.add(new Integer(InteractionManager.QUERY));
			}
			if (v.contains("queryinsert")) {
				this.enabledModes.add(new Integer(InteractionManager.QUERYINSERT));
			}
			if (v.contains("insert")) {
				this.enabledModes.add(new Integer(InteractionManager.INSERT));
			}
			if (v.contains("update")) {
				this.enabledModes.add(new Integer(InteractionManager.UPDATE));
			}
		}

	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (this.enabledModes.contains(new Integer(e.getInteractionManagerMode()))) {
			this.setEnabled(true);
		} else {
			this.setEnabled(false);
		}
	}

	protected void configureTranslations() throws Exception {
		boolean firstTime = true;
		if (this.configurationDialog != null) {
			firstTime = false;
		}

		IConfigureFormBundleGUI confDialog = null;
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			confDialog = this.getConfigurationDialog();
			if (firstTime && (confDialog instanceof JDialog)) {
				((JDialog) confDialog).pack();
				ApplicationManager.center((JDialog) confDialog);
			}
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
		if (confDialog != null) {
			confDialog.setVisible(true);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		if (this.configurationDialog != null) {
			this.configurationDialog.setResourceBundle(resources);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		super.setLocale(l);
		if (this.configurationDialog != null) {
			this.configurationDialog.setComponentLocale(l);
		}
	}

	protected IConfigureFormBundleGUI getConfigurationDialog() throws Exception {
		// configurationDialog = null;
		if (this.configurationDialog == null) {
			final Window w = SwingUtilities.getWindowAncestor(this);
			if (this.configurationDialogClassName != null) {
				// Use reflection to create the dialog
				try {
					final Class dialogClassObject = Class.forName(this.configurationDialogClassName);
					Constructor constructor = null;
					try {
						constructor = dialogClassObject
								.getConstructor(new Class[] { Frame.class, String.class, boolean.class, String.class });
						this.configurationDialog = (IConfigureFormBundleGUI) constructor
								.newInstance(
										new Object[] { (Frame) w, this.dialogTitle, Boolean.TRUE, this.remoteObjectName });
					} catch (final Exception e) {
						FormBundleButton.logger.trace(null, e);
						try {
							constructor = dialogClassObject.getConstructor(
									new Class[] { Dialog.class, String.class, boolean.class, String.class });
							this.configurationDialog = (IConfigureFormBundleGUI) constructor
									.newInstance(new Object[] { (Dialog) w, this.dialogTitle, Boolean.TRUE,
											this.remoteObjectName });
						} catch (final Exception e2) {
							FormBundleButton.logger.trace(null, e2);
							constructor = dialogClassObject.getConstructor(new Class[0]);
							this.configurationDialog = (IConfigureFormBundleGUI) constructor.newInstance(new Object[0]);
						}
					}
				} catch (final Exception e) {
					FormBundleButton.logger.error(null, e);
				}
			}

			if (this.configurationDialog == null) {
				if (w instanceof Frame) {
					this.configurationDialog = new ConfigureFormBundleDialog((Frame) w, this.dialogTitle, true,
							this.remoteObjectName);
				} else if (w instanceof Dialog) {
					this.configurationDialog = new ConfigureFormBundleDialog((Dialog) w, this.dialogTitle, true,
							this.remoteObjectName);
				}
			}
			this.configurationDialog.setForm(this.getParentForm());
			this.configurationDialog.setResourceBundle(this.getParentForm().getResourceBundle());
		}

		return this.configurationDialog;
	}

	public String getDialogTitle() {
		return this.dialogTitle;
	}

	public String getRemoteObjectName() {
		return this.remoteObjectName;
	}

	public String getConfigurationDialogClassName() {
		return this.configurationDialogClassName;
	}

	public List getEnabledModes() {
		return this.enabledModes;
	}

}
