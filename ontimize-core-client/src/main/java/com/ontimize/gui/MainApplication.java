package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.FocusManager;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JToolBar;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.cache.CacheManager;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.login.DefaultChangePasswordDialog;
import com.ontimize.gui.login.DefaultLoginDialog;
import com.ontimize.gui.login.IChangePasswordDialog;
import com.ontimize.gui.login.ILoginDialog;
import com.ontimize.gui.manager.BaseFormManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.ControlApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.preferences.ShortcutDialogConfiguration;
import com.ontimize.gui.preferences.ShortcutDialogConfiguration.ComponentKeyStroke;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.gui.RemotelyManageable;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferenceReferencer;
import com.ontimize.jee.common.gui.preferences.RemoteApplicationPreferences;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.InitialContext;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.ApplicationPermission;
import com.ontimize.jee.common.security.ClientPermissionManager;
import com.ontimize.jee.common.xml.XMLClientProvider;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.incidences.IncidenceHelper;
import com.ontimize.util.jar.JarUtil.InformationDialog;
import com.ontimize.util.remote.IRemoteAdministrationWindow;
import com.ontimize.util.swing.EJFrame;
import com.ontimize.xml.DefaultXMLParametersManager;

/**
 * Basic implementation of the Application interface. This application will hold, and provide
 * references for, the following elements:
 * <ul>
 * <li>Application menu, when exist.</li>
 * <li>Buttons bar, when exist.</li>
 * <li>FormManagers, with its associated Forms. Only one FormManager can be shown in a determined
 * moment.</li>
 * <li>Status bar, which show information messages related to the application operative.</li>
 * </ul>
 * <BR>
 * <B> Application parameters are displayed in the
 * {@link MainApplication#MainApplication(Map)} contructor </B>
 */
public class MainApplication extends JFrame implements Application {

	private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

	public static final String DATABASE_BUNDLE_MANAGER = "databasebundlemanager";

	public static final String APP_LOCALE = "app_locale";

	public static final String APP_CONNECT_TO = "app_connect_to";

	public static final String APP_WINDOW_POSITION = "app_window_position";

	public static final String APP_WINDOW_STATE = "app_window_state";

	public static final String APP_WINDOW_SIZE = "app_window_size";

	public static final String APP_STATUS_BAR_VISIBLE = "app_status_bar_visible";

	public static final String APP_TOOL_BAR_VISIBLE = "app_tool_bar_visible";

	public static final String APP_FONTSIZE = "app_font_size";

	public static final String APP_REMEMBER_LAST_LOGIN = "app_remember_last_login";

	public static final String APP_LAST_LOGIN = "app_last_login";

	public static final String APP_REMEMBER_LAST_PASSWORD = "app_remember_last_password";

	public static final String APP_LAST_PASSWORD = "app_last_password";

	public static final String APP_USER_DIR = "user_dir";

	public static final String LOOK_AND_FEEL_CLASS_NAME = "look_and_feel_classname";

	public static final String THEME_CLASS_NAME = "jgoodies_theme_classname";

	public static final String M_SYSTEM_HAS_NOT_CONNECTION_NETWORK = "M_SISTEMA_NO_DISPONE_DE_CONEXION_DE_RED";

	public static final String M_SYSTEM_HAS_NOT_CONNECTION_NETWORK_es_ES = "Se ha determinado que el sistema no dispone de una conexión de red activada. Compruebe su conexión.";

	public static final String APP_TOOLBAR_LOCATION = "app_toolbar_location";

	private boolean active = false;

	public static final String APP_FONTNAME = "app_font_name";

	public static final String LOGIN_DIALOG_CLASS = "logindialogclass";

	protected String loginDialogClass = null;

	protected String changePasswordDialogClass = null;

	protected String changePasswordSecurityLevel = null;

	protected boolean changePasswordSecurityButton = true;

	/**
	 * Return true if the application frame is active
	 * @return
	 */
	public boolean isActiveApplication() {
		return this.active;
	}

	protected WindowAdapter windowListener = new WindowAdapter() {

		@Override
		public void windowActivated(final WindowEvent e) {
			super.windowActivated(e);
			MainApplication.this.active = true;
		}

		@Override
		public void windowDeactivated(final WindowEvent e) {
			super.windowDeactivated(e);
			MainApplication.this.active = false;
		}
	};

	/**
	 * A default mouse listener that displays in the status bar the message of all the StatusComponents
	 * registered in the application, when the mouse passes over those components.
	 */
	protected class StatusListener extends MouseAdapter {

		protected String text = null;

		public StatusListener() {
		}

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (e.getSource() instanceof StatusComponent) {
				final Application ap = ApplicationManager.getApplication();
				if (ap != null) {
					ap.setStatusBarText(((StatusComponent) e.getSource()).getStatusText());
				}
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			if (e.getSource() instanceof StatusComponent) {
				final Application ap = ApplicationManager.getApplication();
				if (ap != null) {
					ap.setStatusBarText("");
				}
			}
		}

	};

	public static String BEGIN_SESSION_CORRECTLY = "mainapplication.session_successfully_initiated";

	public static String FORMS_MANAGER_CREATED = "mainapplication.form_manager_created";

	public static String REGISTER_FORMS_MANAGER = "mainapplication.form_interaction_manager_registered";

	public static String APPLICATION_MENU_SET = "mainapplication.application_menu_established";

	public static String APPLICATION_MENU_CONFIGURED = "mainapplication.application_menu_configured";

	public static String BEGIN_SESSION_CORRECTLY_es_ES = "Sesión Iniciada";

	public static String FORMS_MANAGER_CREATED_es_ES = "Creado Gestor de Formularios";

	public static String REGISTER_FORMS_MANAGER_es_ES = "Registrado Gestor de Formularios";

	public static String APPLICATION_MENU_SET_es_ES = "Establecido Menú de Aplicación";

	public static String APPLICATION_MENU_CONFIGURED_es_ES = "Configurado Menú de Aplicación";

	public static final String SSO_LOGIN_PROPERTY = "com.ontimize.sso.ssologin";

	public static final String SSO_DEBUG_PROPERTY = "com.ontimize.sso.debug";

	/**
	 * Form managers layout
	 */
	protected CardLayout cardLayout = new CardLayout();

	protected List<String> panelIds = new ArrayList<String>();

	protected Hashtable<String, IFormManager> formsManagers = new Hashtable<String, IFormManager>();

	protected Hashtable<String, JFrame> formManagerFrames = new Hashtable<String, JFrame>();

	protected EntityReferenceLocator locator = null;

	protected ResourceBundle resources = null;

	protected boolean loggedIn = false;

	protected String keyTitle = "";

	protected JMenuBar menu = null;

	protected ImageIcon splashImage = null;

	protected ImageIcon splashImage2 = null;

	protected int timeSplash = -1;

	protected int frameNumber = -1;

	private long initialTime = -1;

	private ISplash window = null;

	protected String currentPassword = null;

	private ILoginDialog dLogin = null;

	private LockDialog dLock = null;

	private final boolean sessionInit = false;

	protected Locale locale = Locale.getDefault();

	protected MenuListener menuListener = null;

	protected String iconString = null;

	protected ToolBarListener toolBarListener = null;

	protected JToolBar toolBar = null;

	protected String helpSet = null;

	protected boolean rememberLastLogin = false;

	protected boolean rememberLastPassword = false;

	protected boolean allowRememberPassword = false;

	protected String databaseBundleManagerName = null;

	protected String lastLogin = null;

	protected String lastPassword = null;

	protected String loginIcon = null;

	protected String changePasswordIcon = null;

	protected String loginText = null;

	protected String changePasswordText = null;

	protected JPanel panelCardLayout = new JPanel();

	protected JPanel panelAuxCardLayout = new JPanel(new BorderLayout());

	protected String connectTo = null;

	protected boolean informWhenOffline = true;

	protected boolean showDNSOptions = true;

	protected StatusListener statusListener = null;

	protected String preferenceFile = null;

	protected ApplicationPreferences preferences = null;

	protected boolean loadRemotePreferences = false;

	protected String formsManagerActive = null;

	private boolean encrypt = false;

	protected long tLogin = System.currentTimeMillis();

	protected String resourcesFileName = null;

	protected List exitListeners = new Vector();

	protected StatusBar statusBar = null;

	public StatusBar getStatusBar() {
		return this.statusBar;
	}

	protected String labelFileURI = null;

	protected String packageName = null;

	protected Object toolBarIcon = null;

	// Used to minimize the application in the tray when the java version is 1.6
	protected Object trayIcon;

	protected String name = null;

	@Override
	public String getDatabaseBundleName() {
		return this.databaseBundleManagerName;
	}

	/**
	 * Implementation of the application status bar
	 *
	 * @version 1.0
	 */
	public static class StatusBar extends JPanel {

		protected int lastPaintPosition = 0;

		protected JPanel iconPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.height = 20;
				if (d.width < 50) {
					d.width = 50;
				}
				return d;
			}
		};

		protected JLabel statusText = new JLabel() {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.height = 20;
				return d;
			}
		};

		protected JProgressBar progressBar = new JProgressBar() {

			Dimension d = null;

			@Override
			public Dimension getPreferredSize() {
				if (this.d == null) {
					this.d = new Dimension(120, 20);
				}
				return this.d;
			}
		};

		/**
		 * Creates the status bar.
		 */
		public StatusBar() {
			((FlowLayout) this.iconPanel.getLayout()).setHgap(1);
			((FlowLayout) this.iconPanel.getLayout()).setVgap(0);
			this.statusText.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
			this.iconPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

			this.progressBar.setFont(this.progressBar.getFont().deriveFont((float) 10));
			this.setLayout(new GridBagLayout());
			this.progressBar.setStringPainted(true);
			this.add(this.progressBar, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.statusText, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.iconPanel, new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

		/**
		 * Sets the tip text for the icon identified by 'iconId'
		 * @param iconId icon identifier
		 * @param toolTip tooltip text to show
		 */
		public void setStatusIconToolTip(final String iconId, final String toolTip) {
			final JLabel labelExist = this.getIconLabel(iconId);
			if (labelExist != null) {
				labelExist.setToolTipText(toolTip);
			}
		}

		/**
		 * Registers a mouse listener to the specified icon
		 * @param iconId the icon identifier
		 * @param mouseListener the mouse listener
		 */
		public void addMouseListenerToStatusIcon(final String iconId, final MouseListener mouseListener) {
			final JLabel label = this.getIconLabel(iconId);
			if (label != null) {
				label.addMouseListener(mouseListener);
			}
		}

		/**
		 * Removes a mouse listener from an status icon
		 * @param iconId the icon identifier
		 * @param mouseListener
		 */
		public void removeMouseListenerFromStatusIcon(final String iconId, final MouseListener mouseListener) {
			final JLabel label = this.getIconLabel(iconId);
			if (label != null) {
				label.removeMouseListener(mouseListener);
			}
		}

		/**
		 * Returns the JLabel used to show the icon referenced by IconId
		 * @param iconId the icon identifier
		 * @return the JLabel related to the identified icon or null in case the icon can-t be found
		 */
		public JLabel getIconLabel(final String iconId) {
			for (int i = 0; i < this.iconPanel.getComponentCount(); i++) {
				final Component c = this.iconPanel.getComponent(i);
				if ((c instanceof JLabel) && c.getName().equals(iconId)) {
					return (JLabel) c;
				}
			}
			return null;
		}

		/**
		 * Adds a icon to the right in the status bar. The parameter <code>iconId</code> is the icon
		 * identifier. If an icon with this identifier already exists then this icon is replaced with the
		 * new one
		 * @param iconId
		 * @param icon
		 */
		public void addStatusIcon(final String iconId, final ImageIcon icon) {
			if (SwingUtilities.isEventDispatchThread()) {
				final JLabel labelExist = this.getIconLabel(iconId);
				if (labelExist == null) {
					final JLabel iconLabel = new JLabel(icon);
					iconLabel.setName(iconId);
					this.iconPanel.add(iconLabel);
					this.iconPanel.doLayout();
					iconLabel.paintImmediately(0, 0, iconLabel.getWidth(), iconLabel.getHeight());
				} else {
					labelExist.setIcon(icon);
					labelExist.paintImmediately(0, 0, labelExist.getWidth(), labelExist.getHeight());
				}
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						final JLabel labelExist = StatusBar.this.getIconLabel(iconId);
						if (labelExist == null) {
							final JLabel labelIcono = new JLabel(icon);
							labelIcono.setName(iconId);
							StatusBar.this.iconPanel.add(labelIcono);
							StatusBar.this.iconPanel.doLayout();
							labelIcono.paintImmediately(0, 0, labelIcono.getWidth(), labelIcono.getHeight());
						} else {
							labelExist.setIcon(icon);
							labelExist.paintImmediately(0, 0, labelExist.getWidth(), labelExist.getHeight());
						}
					}
				});
			}
		}

		/**
		 * Remove the icon with the specified identifier
		 * @param iconId Icon identifier
		 */
		public void removeStatusIcon(final String iconId) {
			if (SwingUtilities.isEventDispatchThread()) {
				final JLabel labelExist = this.getIconLabel(iconId);
				if (labelExist != null) {
					EventListener[] listeners = labelExist.getListeners(MouseListener.class);
					for (int i = 0; i < listeners.length; i++) {
						labelExist.removeMouseListener((MouseListener) listeners[i]);
						listeners = labelExist.getListeners(MouseListener.class);
					}
					this.iconPanel.remove(labelExist);
					this.iconPanel.paintImmediately(0, 0, this.iconPanel.getWidth(), this.iconPanel.getHeight());
				}
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						final JLabel labelExist = StatusBar.this.getIconLabel(iconId);
						if (labelExist != null) {
							EventListener[] listeners = labelExist.getListeners(MouseListener.class);
							for (int i = 0; i < listeners.length; i++) {
								labelExist.removeMouseListener((MouseListener) listeners[i]);
								listeners = labelExist.getListeners(MouseListener.class);
							}
							StatusBar.this.iconPanel.remove(labelExist);
							StatusBar.this.iconPanel.paintImmediately(0, 0, StatusBar.this.iconPanel.getWidth(),
									StatusBar.this.iconPanel.getHeight());
						}
					}
				});
			}
		}

		/**
		 * Sets the text to show in the status bar.
		 * @param text the text to show or null in case the current text must be removed
		 */
		public void setStatusText(final String text) {
			if (SwingUtilities.isEventDispatchThread()) {
				this.statusText.setText(text);
				this.statusText.paintImmediately(0, 0, this.statusText.getWidth(), this.statusText.getHeight());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						StatusBar.this.statusText.setText(text);
						StatusBar.this.statusText.paintImmediately(0, 0, StatusBar.this.statusText.getWidth(),
								StatusBar.this.statusText.getHeight());
					}
				});
			}
		}

		/**
		 * Sets the progress text, usually to show the operations that are being performed.
		 * @param text
		 */
		public void setProgressText(final String text) {
			if (SwingUtilities.isEventDispatchThread()) {
				this.progressBar.setString(text);
				this.progressBar.paintImmediately(0, 0, this.statusText.getWidth(), this.statusText.getHeight());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						StatusBar.this.progressBar.setString(text);
						StatusBar.this.progressBar.paintImmediately(0, 0, StatusBar.this.statusText.getWidth(),
								StatusBar.this.statusText.getHeight());
					}
				});
			}
		}

		/**
		 * Returns the complete percentage shown in the progress bar.
		 * @return the progress bar percentage complete
		 */
		public double getPercentComplete() {
			return this.progressBar.getPercentComplete();
		}

		/**
		 * Sets the maximum progress bar value.
		 * @param maxProgress the maximum progress bar value
		 */
		public void setProgressMaximum(final int maxProgress) {
			if (SwingUtilities.isEventDispatchThread()) {
				this.progressBar.setMaximum(maxProgress);
				this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
				this.lastPaintPosition = maxProgress;
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						StatusBar.this.progressBar.setMaximum(maxProgress);
						StatusBar.this.progressBar.paintImmediately(0, 0, StatusBar.this.progressBar.getWidth(),
								StatusBar.this.progressBar.getHeight());
						StatusBar.this.lastPaintPosition = maxProgress;
					}
				});
			}
		}

		/**
		 * Sets the current progress bar position and paints it immediately.
		 * @param position
		 */
		public void setProgressPosition(final int position) {
			if (SwingUtilities.isEventDispatchThread()) {
				if ((Math
						.abs((float) this.lastPaintPosition - (float) position) > (0.05 * this.progressBar.getMaximum()))
						|| (position > (0.99 * this.progressBar.getMaximum()))) {
					this.progressBar.setValue(position);
					this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
					this.lastPaintPosition = position;
				}
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {

						if ((Math.abs((float) StatusBar.this.lastPaintPosition - (float) position) > (0.05
								* StatusBar.this.progressBar
								.getMaximum()))
								|| (position > (0.99 * StatusBar.this.progressBar.getMaximum()))) {
							StatusBar.this.progressBar.setValue(position);
							StatusBar.this.progressBar.paintImmediately(0, 0, StatusBar.this.progressBar.getWidth(),
									StatusBar.this.progressBar.getHeight());
							StatusBar.this.lastPaintPosition = position;
						}
					}
				});
			}
		}

		/**
		 * Sets the current progress bar position.
		 * @param position
		 * @param paintImmediately if true the progress bar will be repainted immediately
		 */
		public void setProgressPosition(final int position, final boolean paintImmediately) {
			if (paintImmediately) {
				this.setProgressPosition(position);
				return;
			}
			if (SwingUtilities.isEventDispatchThread()) {
				this.progressBar.setValue(position);
				this.progressBar.paintImmediately(0, 0, this.progressBar.getWidth(), this.progressBar.getHeight());
			} else {
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						StatusBar.this.progressBar.setValue(position);
					}
				});
			}
		}

	};

	/**
	 * The lock dialog.
	 */
	private class LockDialog extends JDialog implements Internationalization {

		String titleKey = "application.application_locked";

		String blockKey = "application.application_has_been_locked";

		Button unBlockButton = null;

		Button exit = null;

		JLabel blockText = new JLabel(this.blockKey);

		TextDataField user = null;

		PasswordDataField password = null;

		boolean insertData = false;

		JLabel northImage = new JLabel();

		public LockDialog() {
			super(MainApplication.this, "APLICACION_BLOQUEADA", true);
			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			final JPanel panelCentral = new JPanel(new GridBagLayout());
			Map p = new Hashtable();
			p.put("attr", "User_");
			p.put("dim", "text");
			p.put("labelsize", "10");
			this.user = new TextDataField(p);
			final Map p2 = DefaultXMLParametersManager.getParameters(PasswordDataField.class.getName());
			p2.put("attr", "Password");
			p2.put("dim", "text");
			p2.put("labelsize", "10");
			if (MainApplication.this.encrypt) {
				p2.put("encrypt", "yes");
			}
			this.password = new PasswordDataField(p2);
			p = new Hashtable();
			p.put("key", "mainapplication.unlock");
			p.put("text", "mainapplication.unlock");
			this.unBlockButton = new Button(p);
			p = new Hashtable();
			p.put("key", "exit");
			p.put("text", "exit");
			this.exit = new Button(p);

			this.northImage.setIcon(ImageManager.getIcon(ImageManager.LOCK_APPLICATION_HEADER));
			this.getContentPane().add(this.northImage, BorderLayout.NORTH);
			final JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.CENTER));
			panelSur.add(this.unBlockButton);
			panelSur.add(this.exit);
			this.getContentPane().add(panelSur, BorderLayout.SOUTH);
			panelCentral.add(this.blockText, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 0), 0, 0));
			panelCentral.add(this.password, new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			panelCentral.add(this.user, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			panelCentral.add(this.password, new GridBagConstraints(0, 2, 2, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.getContentPane().add(panelCentral);
			final ImageIcon lockIcon = ImageManager.getIcon(ImageManager.LOCK_APPLICATION);
			if (lockIcon != null) {
				this.blockText.setIcon(lockIcon);
			}
			this.user.setVisible(false);
			this.password.setVisible(false);
			this.pack();
			this.unBlockButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					LockDialog.this.insertData = !LockDialog.this.insertData;
					if (LockDialog.this.insertData) {
						LockDialog.this.user
						.setValue(((ClientReferenceLocator) MainApplication.this.locator).getUser());
						LockDialog.this.user.setVisible(true);
						LockDialog.this.password.setVisible(true);
						LockDialog.this.password.requestFocus();
						LockDialog.this.pack();
					} else {
						// Checks the values
						final Object oUser = LockDialog.this.user.getValue();
						final Object oPassword = LockDialog.this.password.getValue();
						if ((oUser != null) && (oPassword != null)) {
							if (oUser.equals(((ClientReferenceLocator) MainApplication.this.locator).getUser())
									&& oPassword.equals(MainApplication.this.currentPassword)) {
								// Unlock
								LockDialog.this.setVisible(false);
								LockDialog.this.insertData = false;
								MainApplication.this.removeFromTray();
							} else {
								LockDialog.this.user.deleteData();
								LockDialog.this.password.deleteData();
								LockDialog.this.insertData = true;
							}
						} else {
							LockDialog.this.insertData = true;
						}
					}
				}
			});
			this.exit.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					MainApplication.this.exit();
				}
			});

			this.getRootPane().setDefaultButton(this.unBlockButton);
		}

		@Override
		public void setVisible(final boolean vis) {
			this.user.deleteData();
			this.password.deleteData();
			this.user.setVisible(false);
			this.password.setVisible(false);
			this.insertData = false;
			this.pack();
			if (vis) {
				MainApplication.this.hideApplication();
				this.pack();
				final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
			} else {
				MainApplication.this.showApplication();
			}
			super.setVisible(vis);
		}

		@Override
		public List getTextsToTranslate() {
			final List v = new Vector();
			v.addAll(this.user.getTextsToTranslate());
			v.addAll(this.password.getTextsToTranslate());
			v.addAll(this.unBlockButton.getTextsToTranslate());
			v.addAll(this.exit.getTextsToTranslate());
			v.add(this.titleKey);
			v.add(this.blockKey);
			return v;
		}

		@Override
		public void setResourceBundle(final ResourceBundle resources) {
			this.user.setResourceBundle(resources);
			this.password.setResourceBundle(resources);
			this.unBlockButton.setResourceBundle(resources);
			this.exit.setResourceBundle(resources);
			this.setTitle(ApplicationManager.getTranslation(this.titleKey, resources));
			this.blockText.setText(ApplicationManager.getTranslation(this.blockKey, resources));
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

	}

	private final char[] c = new char[6];

	private final char[] c2 = new char[] {};

	private final int i = 0;

	@Override
	protected void processKeyEvent(final KeyEvent e) {
		super.processKeyEvent(e);
	}

	protected static final String NORMAL_VIEW = "NORMAL_VIEW";

	protected static final String MAXIMIZE_VIEW = "MAXIMIZE_VIEW";

	protected JPanel centerPanel = new JPanel(new CardLayout());

	protected int stateFrame = 0;

	protected Form formMaximize = null;

	protected boolean maximize = false;

	/**
	 * MainApplication configuration parameters:
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>parameter</b></td>
	 * <td><b>value</b></td>
	 * </tr>
	 * <tr>
	 * <td>title</td>
	 * <td>Main application window title. The value will be the key of the text, which will be
	 * translated using the application bundle.</td>
	 * </tr>
	 * <tr>
	 * <td>databasebundlemanager</td>
	 * <td>The name of registered remote object that manages functionality of database bundle.</td>
	 * </tr>
	 * <tr>
	 * </td>
	 * <td>locale</td>
	 * <td>initial application locale</td>
	 * </tr>
	 * <tr>
	 * <td>resources</td>
	 * <td>resource bundle. Package pointing to the file which contains the application bundle
	 * resources. It is the name of the file with no locale information, that is, with no locale sufix.
	 * For example, com.business.project.client.i18n.bundle</td>
	 * </tr>
	 * <tr>
	 * <td>splash</td>
	 * <td>Image or images to show during the login process. The image path must be passed, for example,
	 * com/business/project/client/images/login.png. Up to two images can be shown, separated t
	 * milliseconds, configuring the splash value as follows: 'pathimage1;pathimage2;t'</td>
	 * </tr>
	 * <tr>
	 * <td>icon</td>
	 * <td>Application windows icon</td>
	 * </tr>
	 * <tr>
	 * <td>loginicon</td>
	 * <td>login window image</td>
	 * </tr>
	 * <tr>
	 * <tr>
	 * <td>changepasswordicon</td>
	 * <td>Change password window image</td>
	 * </tr>
	 * <tr>
	 * <td>logindialogclass</td>
	 * <td>Class that will be used for creating the login dialog. Not required.</td>
	 * </tr>
	 * <tr>
	 * <td>changePasswordDialogClass</td>
	 * <td>Class that will be used for creating the change password dialog. Not required.</td>
	 * </tr>
	 * <tr>
	 * <td>logintext</td>
	 * <td>login window header text</td>
	 * </tr>
	 * <tr>
	 * <td>changepasswordtext</td>
	 * <td>Change password window header text</td>
	 * </tr>
	 * <tr>
	 * <td>encrypt</td>
	 * <td>determines whether the password is encrypted in the login window or not. Possible values
	 * 'yes'/'no? The default value is 'no'. The password is used afterwards in the login process.</td>
	 * </tr>
	 * <tr>
	 * <td>preferences</td>
	 * <td>Application preferences file name, only the name without complete path. The file will be
	 * created in the user home path. Example, 'application:name.conf'</td>
	 * </tr>
	 * <tr>
	 * <td>helpset</td>
	 * <td>Helpset file package path, for example, com.business.product.client.gui.help. This file will
	 * be merged with the one that contains the basic components help.</td>
	 * </tr>
	 * <tr>
	 * <td>showdnsoptions</td>
	 * <td>Indicates whether the connection selection combo must be shown, when a name conversion is
	 * defined (FQDN) and the client is in the private network. The default value is 'yes'</td>
	 * </tr>
	 * <tr>
	 * <td>allowrememberpassword</td>
	 * <td>Show the option of remembering the password between session starts.</td>
	 * </tr>
	 * <tr>
	 * <td>status</td>
	 * <td>Indicates whether the application has status bar or not. By default, 'yes'</td>
	 * </tr>
	 * <tr>
	 * <td>name</td>
	 * <td>Application name</td>
	 * </tr>
	 * </Table>
	 * @param params the configuration parameters
	 */
	public MainApplication(final Map params) {

		this.addWindowListener(this.windowListener);
		this.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(final ComponentEvent e) {
				if (ApplicationManager.DEBUG) {
					final Component c = e.getComponent();
					MainApplication.this.setStatusBarText(c.getWidth() + " x " + c.getHeight());
				}
			}
		});

		if ((!(FocusManager.getCurrentManager() instanceof FixedFocusManager))
				&& !ApplicationManager.jvmVersionHigherThan_1_4_0()) {
			MainApplication.logger.debug("Established FixedFocusManager");
			FocusManager.setCurrentManager(new FixedFocusManager());
		}

		this.init(params);
		ApplicationManager.setApplication(this);
		HelpUtilities.setMainHelpSet(this.helpSet);
	}

	/**
	 * Configure the parameters of the xml file
	 * @param params
	 */
	protected void init(final Map params) {
		if (params.containsKey(MainApplication.DATABASE_BUNDLE_MANAGER)) {
			this.databaseBundleManagerName = (String) params.get(MainApplication.DATABASE_BUNDLE_MANAGER);
		}

		final Object allowrememberpassword = params.get("allowrememberpassword");
		if ((allowrememberpassword != null) && allowrememberpassword.equals("yes")) {
			this.allowRememberPassword = true;
		} else {
			this.allowRememberPassword = false;
		}

		this.createAndSetStatusBar(params);

		this.panelAuxCardLayout.add(this.panelCardLayout);

		this.centerPanel.add(MainApplication.NORMAL_VIEW, this.panelAuxCardLayout);

		this.getContentPane().add(this.centerPanel);

		this.panelCardLayout.setLayout(this.cardLayout);

		Locale locale0 = Locale.getDefault();
		this.configureApplicationProperties(params);
		locale0 = setupLocaleParameter(params, locale0);
		setupResourceParameter(params, locale0);


		if (params.containsKey(MainApplication.LOGIN_DIALOG_CLASS)) {
			this.loginDialogClass = params.get(MainApplication.LOGIN_DIALOG_CLASS).toString();
		}

		if (params.containsKey(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS)) {
			this.changePasswordDialogClass = params.get(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS).toString();
		}

		if (params.containsKey(IChangePasswordDialog.PASSWORD_LEVEL)) {
			this.changePasswordSecurityLevel = params.get(IChangePasswordDialog.PASSWORD_LEVEL).toString();
		}

		if (params.containsKey(IChangePasswordDialog.SECURITY_BUTTON)) {
			this.changePasswordSecurityButton = ParseUtils
					.getBoolean(params.get(IChangePasswordDialog.SECURITY_BUTTON).toString(), true);
		}

		setupPreferencesParameter(params);

		setupHelpSetParameter(params);

		setupNameParameter(params);
	}

	protected void setupNameParameter(final Map params) {
		final Object oName = params.get("name");
		if (oName != null) {
			this.name = oName.toString();
		}
	}

	protected void setupHelpSetParameter(final Map params) {
		final Object helpset = params.get("helpset");
		if (helpset != null) {
			this.helpSet = helpset.toString();
		}
	}

	protected void setupPreferencesParameter(final Map params) {
		final Object oPreferences = params.get("preferences");
		if (oPreferences != null) {
			this.preferenceFile = oPreferences.toString();
		}
	}

	protected void setupResourceParameter(final Map params, final Locale locale) {
		final Object oResource = params.get("resources");
		if (oResource == null) {
			MainApplication.logger.debug(this.getClass().toString() + " :'resources' is null. Using default file");
			this.resourcesFileName = "com.ontimize.gui.i18n.bundleAPI";
			try {
				this.resources = ExtendedPropertiesBundle.getExtendedBundle(this.resourcesFileName, locale);
				this.setResourceBundle(this.resources);
			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			}
		} else {
			this.resourcesFileName = oResource.toString();
			try {
				this.resources = ExtendedPropertiesBundle.getExtendedBundle(oResource.toString(), locale);
				this.setResourceBundle(this.resources);
			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			}
		}
	}

	protected Locale setupLocaleParameter(final Map params, Locale locale) {
		final Object loc = params.get("locale");
		if (loc == null) {
			MainApplication.logger.debug(this.getClass().toString() + " :'locale' not set. Using default locale.");
		} else {
			final StringTokenizer st = new StringTokenizer(loc.toString(), "_");
			String sCountry = null;
			String language = null;
			String variant = "";// Always initialized

			if (st.hasMoreTokens()) {
				language = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				sCountry = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				variant = st.nextToken();
			}
			if ((language != null) && (sCountry != null)) {
				locale = new Locale(language, sCountry, variant);
				this.setComponentLocale(locale);
			} else {
				MainApplication.logger.debug(
						this.getClass().toString() + ". locale' parameter wrong: Use example: locale='es_ES_VARIANT'");
			}
		}
		return locale;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 * @param params
	 */
	protected void createAndSetStatusBar(final Map params) {
		boolean setStatusBar = true;
		final Object status = params.get("status");
		if ((status != null) && status.equals("no")) {
			setStatusBar = false;
		}

		if (setStatusBar) {
			try {
				final Class statusClass = Class.forName(status.toString());
				this.statusBar = (StatusBar) statusClass.newInstance();
			} catch (final Exception ex) {
				MainApplication.logger.trace(null, ex);
				this.statusBar = new StatusBar();
			}

			this.panelAuxCardLayout.add(this.statusBar, BorderLayout.SOUTH);
		}
	}

	/**
	 * @param params
	 */
	protected void configureApplicationProperties(final Map params) {
		final Object tit = params.get("title");
		if (tit != null) {
			this.keyTitle = tit.toString();
			this.setTitle(this.keyTitle);
		}
		final Object lico = params.get("loginicon");
		if (lico != null) {
			this.loginIcon = lico.toString();
		}
		final Object cpico = params.get("changepasswordicon");
		if (cpico != null) {
			this.changePasswordIcon = cpico.toString();
		}
		final Object ltext = params.get("logintext");
		if (ltext != null) {
			this.loginText = ltext.toString();
		}
		final Object cptext = params.get("changepasswordtext");
		if (cptext != null) {
			this.changePasswordText = cptext.toString();
		}
		final Object encript = params.get("encrypt");
		if (encript != null) {
			if (encript.toString().equals("yes")) {
				this.encrypt = true;
			} else {
				this.encrypt = false;
			}
		}

		final Object warnifoffline = params.get("warnifoffline");
		if (warnifoffline != null) {
			if (warnifoffline.toString().equals("no")) {
				this.informWhenOffline = false;
			} else {
				this.informWhenOffline = true;
			}
		}

		final Object showdnsoptions = params.get("showdnsoptions");
		if (showdnsoptions != null) {
			if (showdnsoptions.toString().equals("no")) {
				this.showDNSOptions = false;
			} else {
				this.showDNSOptions = true;
			}
		}

		configureIconApplicationProperty(params);

		configureSplashApplicationProperty(params);
	}

	protected void configureSplashApplicationProperty(final Map params) {
		final Object splash = params.get("splash");
		if (splash != null) {
			final StringTokenizer st = new StringTokenizer(splash.toString(), ";");
			final int tokens = st.countTokens();
			if (((tokens != 1) && (tokens != 2) && (tokens != 3)) || splash.equals("")) {
				MainApplication.logger.debug(this.getClass().toString() + " : Error in parameter 'splash'");
			}
			String sImageName = splash.toString();
			String imageName2 = null;
			if (tokens >= 2) {
				sImageName = st.nextToken();
				imageName2 = st.nextToken();
				if (tokens == 3) {
					try {
						this.timeSplash = Integer.parseInt(st.nextToken());
					} catch (final Exception e) {
						MainApplication.logger.error("Error in parameter 'splash': ", e);
					}
				}
			}
			this.splashImage = ImageManager.getIcon(sImageName);
			if (this.splashImage == null) {
				MainApplication.logger.warn("{} not found", sImageName.toString());
			}
			// imageName2 o number of frames.
			if (imageName2 != null) {
				try {
					this.frameNumber = Integer.parseInt(imageName2);
				} catch (final Exception e) {
					MainApplication.logger.trace(null, e);
				}
				if (this.frameNumber < 0) {
					this.splashImage2 = ImageManager.getIcon(imageName2);
					if (this.splashImage2 == null) {
						MainApplication.logger.warn("{} not found", imageName2.toString());
					}
				}
			}

		} else {
			this.splashImage = ImageManager.getIcon(ImageManager.ONTIMIZE_SPLASH);
		}
	}

	protected void configureIconApplicationProperty(final Map params) {
		final Object icon = params.get("icon");
		if (icon != null) {
			this.iconString = icon.toString();

			URL url = ImageManager.getIconURL(this.iconString);
			if (url == null) {
				url = this.getClass().getClassLoader().getResource(icon.toString());
			}
			if (url != null) {
				this.setIconImage(new ImageIcon(url).getImage());
			} else {
				MainApplication.logger.error("{}: {} not found", this.getClass().toString(), icon.toString());
			}
		}
	}

	/**
	 * Sets the application references locator. This method calls {@link #initPreloginPreferences()} and
	 * after that the {@link #login()} method. If case the login is successful, the
	 * {@link #initStaticPreferences()} and the {@link #setPermission()} are also called and in the
	 * specified order. Finally, the loading application window is shown. This method must be called
	 * only once.
	 * @param locator the {@link EntityReferenceLocator} for the application.
	 */

	@Override
	public void setReferencesLocator(final EntityReferenceLocator locator) {
		MainApplication.logger.debug("Setting reference locator : {}", locator.getClass());
		this.locator = locator;
		this.initPreloginPreferences();
		if ((this.rememberLastLogin) && (this.rememberLastPassword) && (this.lastPassword != null)) {
			if (!this.loggedIn) {
				try {
					final String password = this.lastPassword;
					final int sessionId = locator.startSession(this.lastLogin, password, null);
					if (sessionId < 0) {
						throw new Exception("unauthorized_user");
					}
					this.currentPassword = password;
					this.loggedIn = true;
					try {
						if (((UtilReferenceLocator) this.getReferenceLocator()).supportChangePassword(this.lastLogin,
								sessionId)) {

							final Map params = new Hashtable();
							if (this.loginIcon != null) {
								params.put(ILoginDialog.LOGIN_ICON, this.loginIcon);
							}
							params.put(ILoginDialog.ENCRYPT, this.encrypt ? "yes" : "no");
							if (this.changePasswordText != null) {
								params.put(IChangePasswordDialog.CHANGE_PASSWORD_TEXT, this.changePasswordText);
							}
							if (this.changePasswordIcon != null) {
								params.put(IChangePasswordDialog.CHANGE_PASSWORD_ICON, this.changePasswordIcon);
							}

							if (this.changePasswordSecurityLevel != null) {
								params.put(IChangePasswordDialog.PASSWORD_LEVEL, this.changePasswordSecurityLevel);
								params.put(IChangePasswordDialog.SECURITY_BUTTON, this.changePasswordSecurityButton);
							}

							IChangePasswordDialog cpdialog = null;

							if (this.changePasswordDialogClass != null) {
								try {
									final Class cpClass = Class.forName(this.changePasswordDialogClass.toString());
									final Constructor constructor = cpClass
											.getConstructor(new Class[] { Application.class, Hashtable.class,
													EntityReferenceLocator.class, String.class, String.class });
									cpdialog = (IChangePasswordDialog) constructor
											.newInstance(new Object[] { this, params, locator, this.lastLogin, password });
								} catch (final Exception e) {
									MainApplication.logger.error(
											"Cannot instanciate the class: {}. Using default class. Error: {}",
											this.changePasswordDialogClass.toString(),
											e.getMessage(), e);
									cpdialog = new DefaultChangePasswordDialog(this, params, locator, this.lastLogin,
											password);
								}

							} else {
								cpdialog = new DefaultChangePasswordDialog(this, params, locator, this.lastLogin,
										password);
							}

							cpdialog.setResourceBundle(this.getResourceBundle());
							cpdialog.showChangePassword();
						}
					} catch (final Exception e) {
						MainApplication.logger.error("Error to obtain the reference locator.", e);
					}
				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
					this.login();
				}
			}
		} else {
			this.login();
		}

		// When the user is registered it is possible to set the remote
		// preferences
		if (BasicApplicationPreferences.remoteUserPreferences) {
			try {
				if ((locator instanceof ClientReferenceLocator) && ((ClientReferenceLocator) locator).isLocalMode()) {
					BasicApplicationPreferences.remoteUserPreferences = false;
					MainApplication.logger.debug("Remote preferences has no sense in local mode");
				} else {
					final InitialContext context = ((ClientReferenceLocator) locator).getInitialContext();
					RemoteApplicationPreferences remotePreferences = null;
					if ((context != null) && context.containsKey(InitialContext.REMOTE_APPLICATION_PREFERENCES)) {
						remotePreferences = (RemoteApplicationPreferences) context
								.get(InitialContext.REMOTE_APPLICATION_PREFERENCES);
					}

					if (remotePreferences == null) {
						remotePreferences = ((RemoteApplicationPreferenceReferencer) locator)
								.getRemoteApplicationPreferences(locator.getSessionId());
					}

					this.preferences.setRemoteApplicationPreferences(remotePreferences);
				}
			} catch (final Exception e) {
				MainApplication.logger.trace(null, e);
			}
		}

		this.initStaticPreferences();
		// When the login process finish then set the permissions
		try {
			this.setPermission();
		} catch (final Exception e) {
			MainApplication.logger.trace(null, e);
			MessageDialog.showMessage(MainApplication.this, "Client permissions can't be loaded." + e.getMessage(),
					JOptionPane.ERROR_MESSAGE, this.resources);
		}

		if (this.databaseBundleManagerName != null) {
			ExtendedPropertiesBundle.useDatabaseBundle((UtilReferenceLocator) locator, this.databaseBundleManagerName);
		}

		this.showSplash();

		try {
			if (((UtilReferenceLocator) this.locator).supportIncidenceService()) {
				IncidenceHelper.addIncidenceServiceButton(this);
			}
		} catch (final Exception e) {
			MainApplication.logger.error(null, e);
		}
	}

	@Override
	public void setMenu(final JMenuBar jMenuBar) {
		this.menu = jMenuBar;
		this.setJMenuBar(jMenuBar);
		this.invalidate();
		this.validate();
		String sText = MainApplication.APPLICATION_MENU_SET_es_ES;
		try {
			if (this.resources != null) {
				sText = this.resources.getString(MainApplication.APPLICATION_MENU_SET);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MainApplication.logger.debug(null, e);
			} else {
				MainApplication.logger.trace(null, e);
			}
		}
		if ((this.menu != null) && (this.menu instanceof Internationalization)) {
			((Internationalization) this.menu).setResourceBundle(this.resources);
		}

		if ((this.menu != null) && (this.menu instanceof ApplicationMenuBar)) {
			// Add the shortcuts to the shortcuts dialog
			final String name = this.getKeyStrokeGroupName();
			((ApplicationMenuBar) this.menu).addConfigurableKeyStrokeGroup(name, this.defaultApplicationKeyBindings);
		}

		sText = MainApplication.APPLICATION_MENU_CONFIGURED_es_ES;
		try {
			if (this.resources != null) {
				sText = this.resources.getString(MainApplication.APPLICATION_MENU_CONFIGURED);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MainApplication.logger.debug(null, e);
			} else {
				MainApplication.logger.trace(null, e);
			}
		}

		this.firePropertyChange("menubar", null, this.menu);
	}

	private String getKeyStrokeGroupName() {
		return MainApplication.class.getName().substring(MainApplication.class.getName().lastIndexOf(".") + 1);
	}

	protected EJFrame createFrame(final String idPanel, final ResourceBundle resourceBundle) {
		final EJFrame frame = new EJFrame(ApplicationManager.getTranslation(idPanel, resourceBundle));
		return frame;
	}

	@Override
	public void showFormManagerContainer(final String idPanel) {

		if (!this.panelIds.contains(idPanel)) {
			if (this.locator instanceof XMLClientProvider) {
				try {
					final Map parameters = ((XMLClientProvider) this.locator).getFormManagerParameters(idPanel,
							this.locator.getSessionId());
					if (parameters != null) {
						final IFormManager currentFormManager = XMLApplicationBuilder.getXMLApplicationBuilder()
								.createFormManager("FormManager", parameters);
						if (currentFormManager != null) {
							this.registerFormManager(idPanel, currentFormManager);
						}
					}
				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
				}
			}
		}

		final IFormManager formManager = this.formsManagers.get(idPanel);
		if (formManager.showFrame()) {
			EJFrame frame = null;
			if (this.formManagerFrames.containsKey(idPanel)) {
				frame = (EJFrame) this.formManagerFrames.get(idPanel);
				frame.setSizePositionPreference(this.getSizePreferenceKey(idPanel));
			} else {
				frame = this.createFrame(idPanel, formManager.getResourceBundle());
				frame.setSizePositionPreference(this.getSizePreferenceKey(idPanel));
				final String path = formManager.getIcon();
				Image currentImage = null;
				if (path != null) {
					final ImageIcon icon = ImageManager.getIcon(path);
					currentImage = icon.getImage();
				}
				if (currentImage == null) {
					currentImage = this.getIconImage();
				}
				frame.setIconImage(currentImage);
				final Container cont = formManager.getContainer();
				if (cont != null) {
					frame.setContentPane(cont);
				} else {
					MainApplication.logger.warn("The container of the form manager {} is null", idPanel);
					frame.setContentPane((Container) formManager);
				}
				frame.pack();
				this.formManagerFrames.put(idPanel, frame);
			}
			frame.setTitle(ApplicationManager.getTranslation(idPanel, formManager.getResourceBundle()));
			if (frame.isVisible()) {
				if (frame.getState() == Frame.ICONIFIED) {
					final int state = frame.getExtendedState();
					final int newState = state & 6;
					frame.setExtendedState(newState);
				}
				frame.toFront();
				// frame.setExtendedState(Frame.NORMAL);
			} else {
				frame.setVisible(true);
			}

		} else if (this.panelIds.contains(idPanel)) {
			this.formsManagerActive = idPanel;
			this.cardLayout.show(this.panelCardLayout, idPanel);
			NavigationHandler.getInstance()
			.updateLastVisitedFormManagers(
					((MainApplication) ApplicationManager.getApplication()).getActiveFMName());
		} else {
			MainApplication.logger
			.debug(this.getClass().toString() + ": No container found referenced by id='" + idPanel + "'");
		}
	}

	protected String getSizePreferenceKey(final String idPanel) {
		final StringBuilder builder = new StringBuilder();
		builder.append("frameFormManager");
		builder.append("_");
		builder.append(idPanel);
		return builder.toString();
	}

	@Override
	public IFormManager getFormManager(final String idPanel) {
		return this.formsManagers.get(idPanel);
	}

	/**
	 * Removes the specified FormManager from the window hierarchy, but maintains the FormManager
	 * reference. In that way, the form manager can be added to other container.
	 * @param formManager
	 * @return the removed form manager or null in case no form manager found
	 */
	public IFormManager removeFormManagerComponent(final String formManager) {
		final IFormManager fmFormManager = this.getFormManager(formManager);
		if (fmFormManager != null) {
			if (fmFormManager.showFrame()) {
				final JFrame currentFrame = this.formManagerFrames.remove(formManager);
				currentFrame.setContentPane(new JPanel());
				currentFrame.dispose();
			} else {
				this.cardLayout.removeLayoutComponent((Component) fmFormManager);
			}

			this.panelIds.remove(formManager);
			this.formsManagers.remove(formManager);
			return fmFormManager;
		} else {
			return null;
		}
	}

	@Override
	public void registerFormManager(final String formManagerName, final IFormManager formManager) {
		String sText = MainApplication.FORMS_MANAGER_CREATED_es_ES;
		try {
			if (this.resources != null) {
				sText = this.resources.getString(MainApplication.FORMS_MANAGER_CREATED);
			}
		} catch (final Exception e) {
			MainApplication.logger.debug(null, e);
		}
		if (this.formsManagers.containsKey(formManagerName)) {
			MainApplication.logger.warn("Form manager already registered: {}", formManagerName);
			return;
		}
		formManager.setApplication(this);
		this.formsManagers.put(formManagerName, formManager);

		final Container cont = formManager.getContainer();
		if (!formManager.showFrame()) {
			if (cont != null) {
				this.panelCardLayout.add(cont, formManagerName);
			} else {
				MainApplication.logger.warn("The container of the form manager {} is null", formManagerName);
				this.panelCardLayout.add((Container) formManager, formManagerName);
			}
		}

		this.panelIds.add(this.panelIds.size(), formManagerName);
		if (formManager.getReferenceLocator() == null) {
			formManager.setReferenceLocator(this.locator);
		}
		formManager.setResourceBundle(this.resources);
		sText = MainApplication.REGISTER_FORMS_MANAGER_es_ES;
		try {
			if (this.resources != null) {
				sText = this.resources.getString(MainApplication.REGISTER_FORMS_MANAGER);
			}
		} catch (final Exception e) {
			MainApplication.logger.error(null, e);
		}
	}

	/**
	 * Centers the frame in the screen
	 */
	public void centerOnScreen() {
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
	}

	@Override
	public Frame getFrame() {
		return this;
	}

	public String getIcon() {
		return this.iconString;
	}

	/**
	 * Sets the application window visible, only once the login has been done successfully. In any other
	 * case, this method will do nothing. The following calls are also done:<BR>
	 * - {@link #setInitialSize()} <br>
	 * - {@link #centerOnScreen()} <br>
	 * - {@link #setInitialState()} <br>
	 * - {@link #hideSplash()} <br>
	 */
	@Override
	public void show() {
		if (this.loggedIn) {
			try {
				this.setInitialSize();
				this.centerOnScreen();
				this.setInitialState();

			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			} finally {
				this.hideSplash();

				super.setState(Frame.NORMAL);

				super.show();

				this.toFront();

				this.repaint();
			}
			MainApplication.logger.info("Elapsed time since logging...: {} millisecs. {} {} -> {}",
					System.currentTimeMillis() - this.tLogin, this.getLocationOnScreen(),
					this.getLocation(), this.getSize());
		} else {
			super.show();
			MainApplication.logger.warn("Application cannnot be started without login");
		}
	}

	/**
	 * Sets the initial application window size to its preferred size.
	 */
	public void setInitialSize() {
		this.getContentPane().invalidate();
		this.getContentPane().validate();
		this.pack();
	}

	/**
	 * Sets the application window visible, only once the login has been done successfully. In any other
	 * case, this method will do nothing. The following calls are also done:<BR>
	 * - {@link #setInitialSize()} <br>
	 * - {@link #centerOnScreen()} <br>
	 * - {@link #setInitialState()} <br>
	 * - {@link #hideSplash()} <br>
	 * @param show if true the application is shown; hidden if false.
	 */
	@Override
	public void show(final boolean show) {
		if (show) {
			if (this.loggedIn) {
				try {
					this.setInitialSize();
					this.centerOnScreen();
					this.setInitialState();

				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
				} finally {
					this.hideSplash();

					super.setState(Frame.NORMAL);

					super.show(show);

					this.toFront();
					this.repaint();
				}
			} else {
				super.show(show);
			}
		} else {
			super.show(show);
		}
	}

	/**
	 * Sets the application window visible, only once the login has been done successfully. In any other
	 * case, this method will do nothing. The following calls are also done:<BR>
	 * - {@link #setInitialSize()} <br>
	 * - {@link #centerOnScreen()} <br>
	 * - {@link #setInitialState()} <br>
	 * - {@link #hideSplash()} <br>
	 */
	@Override
	public void setVisible(final boolean vis) {
		if ((vis) && (this.loggedIn)) {
			try {

				this.setInitialSize();
				this.centerOnScreen();
				this.setInitialState();

			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			} finally {
				this.hideSplash();

				super.setState(Frame.NORMAL);

				super.setVisible(true);

				this.toFront();

				this.repaint();
			}
		} else {
			if (!vis) {
				super.setVisible(false);
			} else {
				MainApplication.logger
				.debug(this.getClass().getName() + ".setVisible() : Application cannot be started without login");
			}
		}
	}

	private void checkOffline() {
		try {
			if (com.ontimize.util.webstart.WebStartUtilities.isWebStartApplication()) {
				if (com.ontimize.util.webstart.WebStartUtilities.isOffline()) {
					String sMessage = MainApplication.M_SYSTEM_HAS_NOT_CONNECTION_NETWORK_es_ES;
					try {
						if (this.resources != null) {
							sMessage = this.resources.getString(MainApplication.M_SYSTEM_HAS_NOT_CONNECTION_NETWORK);
						}
					} catch (final Exception e) {
						if (ApplicationManager.DEBUG) {
							MainApplication.logger.debug(null, e);
						} else {
							MainApplication.logger.trace(null, e);
						}
					}
					MessageDialog.showErrorMessage(this, sMessage);
				}
			}
		} catch (final Exception e) {
			MainApplication.logger.trace(null, e);
			// Nothing
		}
	}

	/**
	 * Creates the login dialog
	 * @return the login dialog
	 */
	protected ILoginDialog createLoginDialog() {

		final Map parameters = new Hashtable();

		parameters.put(ILoginDialog.ENCRYPT, "" + this.encrypt);
		if (this.loginIcon != null) {
			parameters.put(ILoginDialog.LOGIN_ICON, this.loginIcon);
		}
		if (this.loginText != null) {
			parameters.put(ILoginDialog.LOGIN_TEXT, this.loginText);
		}
		parameters.put(ILoginDialog.REMEMBER_PASSWORD, "" + this.allowRememberPassword);
		parameters.put(ILoginDialog.REMEMBER_LAST_LOGIN, "" + this.rememberLastLogin);
		parameters.put(ILoginDialog.LAST_LOGIN, this.lastLogin);
		parameters.put(ILoginDialog.DNS_OPTIONS, "" + this.showDNSOptions);
		if (this.changePasswordText != null) {
			parameters.put(IChangePasswordDialog.CHANGE_PASSWORD_TEXT, this.changePasswordText);
		}
		if (this.changePasswordIcon != null) {
			parameters.put(IChangePasswordDialog.CHANGE_PASSWORD_ICON, this.changePasswordIcon);
		}
		if (this.changePasswordDialogClass != null) {
			parameters.put(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS, this.changePasswordDialogClass);
		}

		if (this.changePasswordSecurityLevel != null) {
			parameters.put(IChangePasswordDialog.PASSWORD_LEVEL, this.changePasswordSecurityLevel);
			parameters.put(IChangePasswordDialog.SECURITY_BUTTON, this.changePasswordSecurityButton);
		}

		if (this.connectTo != null) {
			parameters.put(ILoginDialog.CONNECT_TO, this.connectTo);
		}

		if (this.loginDialogClass != null) {
			try {

				final Class loginDialog = Class.forName(this.loginDialogClass);
				final Constructor constructor = loginDialog
						.getConstructor(new Class[] { Application.class, Hashtable.class, EntityReferenceLocator.class });
				final Object currentObject = constructor.newInstance(new Object[] { this, parameters, this.locator });
				return (ILoginDialog) currentObject;

			} catch (final Exception ex) {
				MainApplication.logger.error(null, ex);
			}
		}

		return new DefaultLoginDialog(this, parameters, this.locator);
		// return new LoginDialog(this, parameters, this.locator);
	}

	/**
	 * Shows the login dialog. If user is already log in then this method does nothing
	 */
	@Override
	public boolean login() {
		/*
		 * First of all, try Single Sign-On login if it's enabled
		 */
		if (!this.loggedIn) {
			try {
				int sid = -1;
				if (this.locator != null) {
					final String trySSOLogin = System.getProperty(MainApplication.SSO_LOGIN_PROPERTY);
					final String ssoDebug = System.getProperty(MainApplication.SSO_DEBUG_PROPERTY);
					if ((trySSOLogin != null) && ParseUtils.getBoolean(trySSOLogin, false)) {
						Boolean debug = Boolean.FALSE;
						if (ssoDebug != null) {
							debug = Boolean.valueOf(ParseUtils.getBoolean(ssoDebug, false));
						}

						final Class locatorClass = this.locator.getClass();
						final Class[] methodParams = { Boolean.class };
						final Method ssoMethod = locatorClass.getMethod("ssoLogin", methodParams);
						final Object ssoResult = ssoMethod.invoke(this.locator, new Object[] { debug });
						if ((ssoResult != null) && (ssoResult instanceof Integer)) {
							sid = ((Integer) ssoResult).intValue();
						}
					}
				}
				if (sid > 0) {
					this.loggedIn = true;
				}
				if (this.loggedIn) {
					this.tLogin = System.currentTimeMillis();
					this.currentPassword = "pass";
				}
			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			}
		}

		if (!this.loggedIn) {
			if (this.dLogin == null) {
				this.dLogin = this.createLoginDialog();
			}
			this.dLogin.setResourceBundle(this.resources);
			try {
				this.setSize(1, 1);
				ApplicationManager.center(MainApplication.this);
				super.setState(Frame.ICONIFIED);
				super.setVisible(true);
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						MainApplication.this.checkOffline();
						MainApplication.this.loggedIn = MainApplication.this.dLogin.login();
						if (MainApplication.this.loggedIn) {
							MainApplication.this.currentPassword = MainApplication.this.dLogin.getPasswordValue();
						}
					}
				});
				super.setVisible(false);

			} catch (final Exception e) {
				MainApplication.logger.trace(null, e);
				return false;
			}
		} else {
			return this.loggedIn;
		}

		if (this.loggedIn) {
			this.tLogin = System.currentTimeMillis();
		}
		return this.loggedIn;
	}

	/**
	 * Closes the client application. Notifies all the listeners and then close it.<br>
	 * If some listener does not validate the exit action then application does not finish.<br>
	 * Saves the user preferences and close the remote session
	 */
	@Override
	public void exit() {
		if (this.fireApplicationExiting() == false) {
			return;
		}
		if (this.preferences != null) {
			final Point p = this.getLocation();
			final String pos = ApplicationManager.parsePointValue(p);

			final Dimension d = this.getSize();
			final String tam = ApplicationManager.parseDimensionValue(d);

			String user = null;
			if (this.locator instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.locator).getUser();
			}

			final int state = this.getExtendedState();
			this.preferences.setPreference(user, BasicApplicationPreferences.APP_WINDOW_STATE, "" + state);
			this.preferences.setPreference(user, BasicApplicationPreferences.APP_WINDOW_POSITION, pos);
			this.preferences.setPreference(user, BasicApplicationPreferences.APP_WINDOW_SIZE, tam);

			// Buttons bar
			if (this.toolBar != null) {
				final Container c = this.getContentPane();
				final Point point = this.toolBar.getLocation();
				if ((point.x == c.getInsets().left) && (point.y == c.getInsets().top)) {
					if (this.toolBar.getOrientation() == SwingConstants.HORIZONTAL) {
						// north
						this.preferences.setPreference(user, BasicApplicationPreferences.APP_TOOLBAR_LOCATION,
								BorderLayout.NORTH);
					} else {
						// west
						this.preferences.setPreference(user, BasicApplicationPreferences.APP_TOOLBAR_LOCATION,
								BorderLayout.WEST);
					}
				} else {
					if (this.toolBar.getOrientation() == SwingConstants.HORIZONTAL) {
						// south
						this.preferences.setPreference(user, BasicApplicationPreferences.APP_TOOLBAR_LOCATION,
								BorderLayout.SOUTH);
					} else {
						// east
						this.preferences.setPreference(user, BasicApplicationPreferences.APP_TOOLBAR_LOCATION,
								BorderLayout.EAST);
					}
				}
			}
			if (((BasicApplicationPreferences) this.preferences).isDirtyMode()) {
				((BasicApplicationPreferences) this.preferences).setDirtyMode(false);
			} else {
				this.preferences.savePreferences();
			}
		}

		final String exitText = "mainapplication.quit_the_application";
		final String printProcessText = "mainapplication_there_is_an_ongoing_printing_process";
		final String processText = "mainapplication.there_are_?_active_process";

		// If some form manager exists then show a message
		if (this.resources != null) {
			int n = 0;
			if ((n = ApplicationManager.getExtOpThreadsMonitor(this).getAliveThreadsCount()) > 0) {
				final boolean close = MessageDialog.showQuestionMessage(this, ApplicationManager.getTranslation(processText,
						this.resources, new Object[] { new Integer(n) }));
				if (!close) {
					return;
				}
			}
		}
		this.showExitMessage();
	}

	protected void showExitMessage() {
		final String exitText = "mainapplication.quit_the_application";
		final String printProcessText = "mainapplication_there_is_an_ongoing_printing_process";

		int option = MessageDialog.showMessage(this, exitText, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
				this.resources, this.getTitle());
		if (option == JOptionPane.YES_OPTION) {
			if (ApplicationManager.printingJobInProgress()) {
				option = MessageDialog.showMessage(this, printProcessText, JOptionPane.QUESTION_MESSAGE,
						JOptionPane.YES_NO_OPTION, this.resources);
				if (option == JOptionPane.NO_OPTION) {
					return;
				}
			}
			try {
				if ((this.locator != null) && (this.locator.getSessionId() >= 0)) {
					this.locator.endSession(this.locator.getSessionId());
				}
			} catch (final Exception e) {
				MainApplication.logger.error("Error closing session ", e);
			}
			MainApplication.systemExit();
		}
	}

	public static void systemExit() {
		try {
			final Method exitMethod = System.class.getMethod("exit", new Class[] { int.class });
			exitMethod.invoke(null, new Object[] { 0 });
		} catch (final Exception e) {
			MainApplication.logger.error("", e);
		}
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.exit();
		} else {
			super.processWindowEvent(e);

		}
	}

	@Override
	public List getTextsToTranslate() {
		List v = null;
		if (this.dLogin != null) {
			v = this.dLogin.getTextsToTranslate();
		} else {
			v = new Vector();
		}
		if (this.dLock != null) {
			v.addAll(this.dLock.getTextsToTranslate());
		}
		v.add(this.keyTitle);
		v.add("mainapplication_there_is_an_ongoing_printing_process");
		v.add("mainapplication.quit_the_application");
		// Now the menu:
		if ((this.menu != null) && (this.menu instanceof Internationalization)) {
			v.addAll(((Internationalization) this.menu).getTextsToTranslate());
		}
		final Enumeration enumKeys = this.formsManagers.keys();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oFormManager = this.formsManagers.get(oKey);
			if (oFormManager instanceof IFormManager) {
				v.addAll(((IFormManager) oFormManager).getTextsToTranslate());
			}
		}
		return v;
	}

	/**
	 * Creates a file with all the text in the application that must be translated
	 * @param file the destiny file
	 * @param importProp
	 */
	public void saveTextsToTranslate(final String file, final Properties importProp) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file, false);
			bw = new BufferedWriter(fw);
			bw.write("#####################     Application  ######################");
			this.saveTextToTranslateApplications(importProp, bw);
			bw.write("#####################     Login dialog  ######################");
			bw.newLine();
			List v = new Vector();
			if (this.dLogin != null) {
				v = this.dLogin.getTextsToTranslate();
				try {
					Collections.sort(v);
				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
				}
				for (int i = 0; i < v.size(); i++) {
					bw.write(v.get(i) + "=");
					if ((importProp != null) && importProp.containsKey(v.get(i))) {
						bw.write(importProp.getProperty(v.get(i).toString()));
					}
					bw.newLine();
				}
			}
			v.add(this.keyTitle);
			v.add("mainapplication_there_is_an_ongoing_printing_process");
			v.add(MainApplication.APPLICATION_MENU_CONFIGURED);
			v.add(MainApplication.FORMS_MANAGER_CREATED);
			v.add(MainApplication.REGISTER_FORMS_MANAGER);
			v.add(MainApplication.BEGIN_SESSION_CORRECTLY);
			v.add(MainApplication.APPLICATION_MENU_SET);

			if ((this.menu != null) && (this.menu instanceof Internationalization)) {
				final List vMenu = ((Internationalization) this.menu).getTextsToTranslate();
				try {
					Collections.sort(vMenu);
				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
				}
				bw.write("####################### Application menu ##########################");
				bw.newLine();
				for (int i = 0; i < vMenu.size(); i++) {
					if (!v.contains(vMenu.get(i))) {
						v.add(vMenu.get(i));
						bw.write(vMenu.get(i) + "=");
						if ((importProp != null) && importProp.containsKey(vMenu.get(i))) {
							bw.write(importProp.getProperty(vMenu.get(i).toString()));
						}
						bw.newLine();
					}
				}
			}

			bw.flush();
			bw.close();
			fw.close();

			// Now all the form managers
			final Enumeration enumKeys = this.formsManagers.keys();
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				final Object formManagers = this.formsManagers.get(oKey);
				if (formManagers instanceof BaseFormManager) {
					((BaseFormManager) formManagers).saveTextsToTranslate(file, true, v, importProp);
				}
			}

			// Now elements in source but not in the destination
			if (importProp != null) {
				FileWriter fw2 = null;
				BufferedWriter bw2 = null;
				try {
					fw2 = new FileWriter(file, true);
					bw2 = new BufferedWriter(fw2);
					bw2.write(
							"#####################     In the imported file but not in the application ######################");
					bw2.newLine();
					final Enumeration enumImportedKeys = importProp.keys();
					while (enumImportedKeys.hasMoreElements()) {
						final Object oKey = enumImportedKeys.nextElement();
						if (!v.contains(oKey)) {
							bw2.write(oKey.toString() + "=" + importProp.getProperty(oKey.toString(), ""));
							bw2.newLine();
						}
					}
				} catch (final Exception e) {
					MainApplication.logger.error(null, e);
				} finally {
					try {
						if (bw2 != null) {
							bw2.close();
						}
						if (fw2 != null) {
							fw2.close();
						}
					} catch (final Exception e) {
						MainApplication.logger.trace(null, e);
					}
				}
			}
		} catch (final Exception e) {
			MainApplication.logger.error(null, e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (final Exception e) {
				MainApplication.logger.trace(null, e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #saveTextsToTranslate(String, Properties)}
	 * @param importProp
	 * @param bw
	 * @throws IOException
	 */
	protected void saveTextToTranslateApplications(final Properties importProp, final BufferedWriter bw) throws IOException {
		bw.newLine();
		bw.write(this.keyTitle + "=");
		if ((importProp != null) && importProp.containsKey(this.keyTitle)) {
			bw.write(importProp.getProperty(this.keyTitle));
		}
		bw.newLine();
		bw.write("mainapplication_there_is_an_ongoing_printing_process" + "=");
		if ((importProp != null) && importProp.containsKey("mainapplication_there_is_an_ongoing_printing_process")) {
			bw.write(importProp.getProperty("mainapplication_there_is_an_ongoing_printing_process"));
		}
		bw.newLine();
		bw.write(MainApplication.APPLICATION_MENU_CONFIGURED + "=");
		if ((importProp != null) && importProp.containsKey(MainApplication.APPLICATION_MENU_CONFIGURED)) {
			bw.write(importProp.getProperty(MainApplication.APPLICATION_MENU_CONFIGURED));
		}
		bw.newLine();
		bw.write(MainApplication.FORMS_MANAGER_CREATED + "=");
		if ((importProp != null) && importProp.containsKey(MainApplication.FORMS_MANAGER_CREATED)) {
			bw.write(importProp.getProperty(MainApplication.FORMS_MANAGER_CREATED));
		}
		bw.newLine();
		bw.write(MainApplication.APPLICATION_MENU_SET + "=");
		if ((importProp != null) && importProp.containsKey(MainApplication.APPLICATION_MENU_SET)) {
			bw.write(importProp.getProperty(MainApplication.APPLICATION_MENU_SET));
		}
		bw.newLine();
		bw.write(MainApplication.REGISTER_FORMS_MANAGER + "=");
		if ((importProp != null) && importProp.containsKey(MainApplication.REGISTER_FORMS_MANAGER)) {
			bw.write(importProp.getProperty(MainApplication.REGISTER_FORMS_MANAGER));
		}
		bw.newLine();
		bw.write(MainApplication.BEGIN_SESSION_CORRECTLY + "=");
		if ((importProp != null) && importProp.containsKey(MainApplication.BEGIN_SESSION_CORRECTLY)) {
			bw.write(importProp.getProperty(MainApplication.BEGIN_SESSION_CORRECTLY));
		}
		bw.newLine();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resources = resources;
		ApplicationManager.setResourceBundle(resources);
		if (this.dLogin != null) {
			this.dLogin.setResourceBundle(resources);
		}
		if ((this.menu != null) && (this.menu instanceof Internationalization)) {
			((Internationalization) this.menu).setResourceBundle(this.resources);
		}

		if ((this.toolBar != null) && (this.toolBar instanceof Internationalization)) {
			((Internationalization) this.toolBar).setResourceBundle(this.resources);
		}
		try {
			if (resources != null) {
				this.setTitle(resources.getString(this.keyTitle));
			}
		} catch (final Exception e) {
			this.setTitle(this.keyTitle);
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MainApplication.logger.debug(null, e);
			} else {
				MainApplication.logger.trace(null, e);
			}
		}
		final Enumeration enumKeys = this.formsManagers.keys();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oFormManagers = this.formsManagers.get(oKey);
			if (oFormManagers instanceof IFormManager) {
				((IFormManager) oFormManagers).setResourceBundle(resources);
			}
			if (this.formManagerFrames.containsKey(oKey)) {
				this.formManagerFrames.get(oKey).setTitle(ApplicationManager.getTranslation((String) oKey, resources));
			}
		}
		if (this.resources != null) {
			if (!this.resources.getLocale().equals(this.locale)) {
				if (this.preferences != null) {
					this.preferences.setPreference(this.getUser(), MainApplication.APP_LOCALE,
							this.resources.getLocale().toString());
				}
			}
		} else {
			if (this.preferences != null) {
				this.preferences.setPreference(this.getUser(), MainApplication.APP_LOCALE, null);
			}
		}

		// Swing components translation.
		UIManager.put("OptionPane.okButtonText",
				ApplicationManager.getTranslation("OptionPane.okButtonText", resources));
		UIManager.put("OptionPane.cancelButtonText",
				ApplicationManager.getTranslation("OptionPane.cancelButtonText", resources));
		UIManager.put("OptionPane.inputDialogTitle",
				ApplicationManager.getTranslation("OptionPane.inputDialogTitle", resources));
		UIManager.put("OptionPane.messageDialogTitle",
				ApplicationManager.getTranslation("OptionPane.messageDialogTitle", resources));
		UIManager.put("OptionPane.titleText", ApplicationManager.getTranslation("OptionPane.titleText", resources));

		UIManager.put("OptionPane.noButtonText",
				ApplicationManager.getTranslation("OptionPane.noButtonText", resources));
		UIManager.put("OptionPane.yesButtonText",
				ApplicationManager.getTranslation("OptionPane.yesButtonText", resources));

		UIManager.put("ColorChooser.sampleText",
				ApplicationManager.getTranslation("ColorChooser.sampleText", resources));
		UIManager.put("ColorChooser.swatchesNameText",
				ApplicationManager.getTranslation("ColorChooser.swatchesNameText", resources));
		UIManager.put("ColorChooser.swatchesRecentText",
				ApplicationManager.getTranslation("ColorChooser.swatchesRecentText", resources));
		UIManager.put("ColorChooser.hsbNameText",
				ApplicationManager.getTranslation("ColorChooser.hsbNameText", resources));
		UIManager.put("ColorChooser.hsbRedText",
				ApplicationManager.getTranslation("ColorChooser.hsbRedText", resources));
		UIManager.put("ColorChooser.hsbGreenText",
				ApplicationManager.getTranslation("ColorChooser.hsbGreenText", resources));
		UIManager.put("ColorChooser.hsbBlueText",
				ApplicationManager.getTranslation("ColorChooser.hsbBlueText", resources));
		UIManager.put("ColorChooser.rgbNameText",
				ApplicationManager.getTranslation("ColorChooser.rgbNameText", resources));
		UIManager.put("ColorChooser.rgbRedText",
				ApplicationManager.getTranslation("ColorChooser.rgbRedText", resources));
		UIManager.put("ColorChooser.rgbGreenText",
				ApplicationManager.getTranslation("ColorChooser.rgbGreenText", resources));
		UIManager.put("ColorChooser.rgbBlueText",
				ApplicationManager.getTranslation("ColorChooser.rgbBlueText", resources));
		UIManager.put("ColorChooser.previewText",
				ApplicationManager.getTranslation("ColorChooser.previewText", resources));
		UIManager.put("ColorChooser.okText", ApplicationManager.getTranslation("ColorChooser.okText", resources));
		UIManager.put("ColorChooser.cancelText",
				ApplicationManager.getTranslation("ColorChooser.cancelText", resources));
		UIManager.put("ColorChooser.resetText", ApplicationManager.getTranslation("ColorChooser.resetText", resources));
	}

	/**
	 * Returns the login name of the application user.
	 * @return the user name
	 */
	protected String getUser() {
		String user = null;
		if (this.locator instanceof ClientReferenceLocator) {
			user = ((ClientReferenceLocator) this.locator).getUser();
		}
		return user;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		this.setLocale(l);
		if (this.dLogin != null) {
			this.dLogin.setComponentLocale(l);
		}
		if ((this.menu != null) && (this.menu instanceof Internationalization)) {
			((Internationalization) this.menu).setComponentLocale(l);
		}
		final Enumeration enumKeys = this.formsManagers.keys();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final IFormManager formManager = this.formsManagers.get(oKey);
			formManager.setComponentLocale(l);
		}

		if (this.locale != null) {
			if ((this.resources == null) || !this.locale.equals(this.resources.getLocale())) {
				if (this.preferences != null) {
					this.preferences.setPreference(this.getUser(), MainApplication.APP_LOCALE, l.toString());
				}
			}
		} else {
			if (this.preferences != null) {
				this.preferences.setPreference(this.getUser(), MainApplication.APP_LOCALE, null);
			}
		}
	}

	/**
	 * Shows the loading application window (splash).
	 */
	protected void showSplash() {
		if (this.splashImage != null) {
			if (this.frameNumber < 0) {
				this.window = new TopWindow(this, null, this.resources, this.splashImage2, this.splashImage, null);
			} else {
				this.window = new Splash(this, this.resources, this.splashImage, this.frameNumber,
						this.timeSplash < 0 ? 100 : this.timeSplash);
			}
			this.window.setRepaintTime(600);
			this.window.show(true);
			this.initialTime = System.currentTimeMillis();
			String sessionText = MainApplication.BEGIN_SESSION_CORRECTLY_es_ES;
			try {
				if (this.resources != null) {
					sessionText = this.resources.getString(MainApplication.BEGIN_SESSION_CORRECTLY);
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					MainApplication.logger.debug(null, e);
				} else {
					MainApplication.logger.trace(null, e);
				}
			}
		}
	}

	private void setSplashText(final String text) {
		if (this.window != null) {
			this.window.updateText(text);
		}
	}

	/**
	 * Hides the splash (loading application window).
	 */
	protected void hideSplash() {
		if (this.window != null) {
			if (this.timeSplash <= 0) {
				if (!SwingUtilities.isEventDispatchThread()) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							MainApplication.this.window.hide();
							MainApplication.this.window.dispose();
						}
					});
				} else {
					this.window.hide();
					this.window.dispose();
				}
			} else {
				// Hides it after the specified time.
				final long timeToEnd = this.timeSplash - (System.currentTimeMillis() - this.initialTime);
				if (timeToEnd <= 0) {
					this.window.hide();
				} else {
					final Thread t = new Thread() {

						@Override
						public void run() {
							try {
								Thread.sleep(timeToEnd);
							} catch (final Exception e) {
								MainApplication.logger.trace(null, e);
							}
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									MainApplication.this.window.hide();
									MainApplication.this.window.dispose();
								}
							});
						}

					};
					t.start();
				}
			}
		}
	}

	/**
	 * Sets the application window visible, only once the login has been done successfully. In any other
	 * case, this method will do nothing. The following calls are also done:<BR>
	 * - {@link #setInitialSize()} <br>
	 * - {@link #centerOnScreen()} <br>
	 * - {@link #setInitialState()} <br>
	 * - {@link #hideSplash()} <br>
	 */
	public void showApplication() {
		super.show();
		this.hideSplash();
		final Window[] windows = this.getOwnedWindows();
		if (windows != null) {
			for (int i = 0; i < windows.length; i++) {
				if (windows[i] instanceof JWindow) {
					windows[i].hide();
				}
			}
		}
		this.removeFromTray();
	}

	private void hideApplication() {
		super.setVisible(false);
	}

	@Override
	public void lock() {
		if (this.dLock == null) {
			this.dLock = new LockDialog();
		}
		this.dLock.setResourceBundle(this.resources);
		if (SwingUtilities.isEventDispatchThread()) {
			// Lock the entry using a dialog
			this.sendToTray();
			this.dLock.setVisible(true);

		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					MainApplication.this.sendToTray();
					MainApplication.this.dLock.setVisible(true);
				}
			});
		}
	}

	public boolean isLocked() {
		if (this.dLock == null) {
			return false;
		} else {
			return this.dLock.isVisible();
		}
	}

	/**
	 * Get the client application reference locator
	 * @return
	 */
	@Override
	public EntityReferenceLocator getReferenceLocator() {
		return this.locator;
	}

	@Override
	public void endSession() {
		try {
			this.locator.endSession(this.locator.getSessionId());
			super.setVisible(false);
			this.loggedIn = false;
			this.login();
		} catch (final Exception e) {
			MainApplication.logger.error(null, e);
			MessageDialog.showMessage(MainApplication.this, e.getMessage(), JOptionPane.ERROR_MESSAGE, this.resources);
		}
	}

	/**
	 * Sets the client permissions. To do that calls the
	 * {@link ClientPermissionManager#installClientPermissions(Hashtable, int)} with the current user
	 * and the current password.
	 * @throws Exception
	 */
	protected void setPermission() throws Exception {
		if (this.locator instanceof ClientPermissionManager) {
			try {
				if (ApplicationManager.DEBUG) {
					MainApplication.logger.debug(this.getClass().toString() + " Setting client permissions");
				}
				final Map kv = new Hashtable();
				kv.put("User_", ((ClientReferenceLocator) this.locator).getUser());
				kv.put("Password", this.currentPassword);
				((ClientPermissionManager) this.locator).installClientPermissions(kv, this.locator.getSessionId());
				if (!MainApplication.checkApplicationPermission("StatusbarPermission")) {
					if (this.statusBar != null) {
						this.panelAuxCardLayout.remove(this.statusBar);
						this.statusBar = null;
					}
				}
			} catch (final Exception e) {
				MainApplication.logger.error(null, e);
			}
		} else {
			if (ApplicationManager.DEBUG) {
				MainApplication.logger.debug(
						"CLIENT PERMISSIONS HAVEN'T BEEN ESTABLISHED BECAUSE LOCATOR ISN'T A CLIENTPERMISSIONMANAGER INSTANCE");
			}
		}
	}

	@Override
	public Locale getLocale() {
		return this.locale;
	}

	@Override
	public ResourceBundle getResourceBundle() {
		return this.resources;
	}

	public void message(final String message, final int type) {
		MessageDialog.showMessage(this.getFrame(), message, type, this.resources);
	}

	/**
	 * Adds an ApplicationListener to the application.
	 * @param applicationListener a new application listener
	 */
	public void addApplicationListener(final ApplicationListener applicationListener) {
		if (!this.exitListeners.contains(applicationListener)) {
			this.exitListeners.add(applicationListener);
		}
	}

	/**
	 * Removes the specified ApplicationListener
	 * @param applicationListener
	 */
	public void removeApplicationListener(final ApplicationListener applicationListener) {
		this.exitListeners.remove(applicationListener);
	}

	/**
	 * Notifies all the {@link ApplicationListener} that the application will be closed.
	 * @return
	 */
	protected boolean fireApplicationExiting() {
		if (this.exitListeners.isEmpty()) {
			return true;
		}
		final ApplicationEvent e = new ApplicationEvent(this);
		for (int i = 0; i < this.exitListeners.size(); i++) {
			final boolean bAllowed = ((ApplicationListener) this.exitListeners.get(i)).applicationClosing(e);
			if (!bAllowed) {
				return bAllowed;
			}
		}
		return true;
	}

	/**
	 * Returns the form manager name that is active(shown)
	 * @return
	 */
	public String getActiveFMName() {
		if ((this.formsManagerActive == null) && (this.panelIds.size() > 0)) {
			return this.panelIds.get(0);
		}
		return this.formsManagerActive;
	}

	public Form getActiveForm() {
		final String sActiveFormManagerName = this.getActiveFMName();
		if (sActiveFormManagerName != null) {
			return this.getFormManager(sActiveFormManagerName).getActiveForm();
		}
		return null;
	}

	@Override
	public JMenuBar getMenu() {
		return this.menu;
	}

	@Override
	public void setMenuListener(final MenuListener l) {
		this.menuListener = l;
	}

	@Override
	public MenuListener getMenuListener() {
		return this.menuListener;
	}

	public String getResourcesFileName() {
		return this.resourcesFileName;
	}

	@Override
	public ApplicationPreferences getPreferences() {
		return this.preferences;
	}

	/**
	 * Loads the application preferences.
	 * @return Application preferences or null if 'preferences' parameter is not specified
	 */
	public ApplicationPreferences loadApplicationPreferences() {
		if (this.preferenceFile != null) {
			File f = null;
			String basePath = System.getProperty("user.home");
			if (basePath != null) {
				if (basePath.charAt(basePath.length() - 1) != '/') {
					basePath = basePath + "/";
				}
			}

			ApplicationPreferences prefs = null;

			if (BasicApplicationPreferences.remoteUserPreferences) {
				f = new File(basePath, this.preferenceFile);
				if ((f != null) && f.exists()) {
					final File dest = new File(basePath, this.preferenceFile + ".lbk");
					if (!dest.exists()) {
						ApplicationManager.copyFile(f, dest);
					}
					f.delete();
				}
				RemoteApplicationPreferences remoteAppPref = null;
				try {
					if (this.locator.getSessionId() > 0) {
						remoteAppPref = ((RemoteApplicationPreferenceReferencer) this.locator)
								.getRemoteApplicationPreferences(this.locator.getSessionId());
					}
				} catch (final Exception e) {
					MainApplication.logger.trace(null, e);
				}
				prefs = new BasicApplicationPreferences(this.preferenceFile + ".init", remoteAppPref);
			} else {
				f = new File(basePath, this.preferenceFile);
				if (!f.exists()) {
					this.loadRemotePreferences = true;
				} else {
					final File dest = new File(basePath, this.preferenceFile + ".bak");
					if (!dest.exists()) {
						ApplicationManager.copyFile(f, dest);
					}
				}
				prefs = new BasicApplicationPreferences(this.preferenceFile);
			}

			prefs.loadPreferences();
			return prefs;
		} else {
			return null;
		}
	}

	/**
	 * Start the preferences before showing the login window. These preferences are:<BR>
	 * -Remember last login<BR>
	 * -Remember password
	 */
	protected void initPreloginPreferences() {
		MainApplication.logger.debug("Init pre-login preferences");
		try {
			if (this.preferenceFile != null) {
				if (this.preferences == null) {
					this.preferences = this.loadApplicationPreferences();
					this.preferences.loadPreferences();
				}

				final String user = null;

				final String sRemLastLog = this.preferences.getPreference(user, MainApplication.APP_REMEMBER_LAST_LOGIN);
				if (sRemLastLog != null) {
					try {
						final boolean bRemember = ApplicationManager.parseStringValue(sRemLastLog, false);
						this.rememberLastLogin = bRemember;
					} catch (final Exception e) {
						MainApplication.logger.error(null, e);
					}
				} else {
					this.rememberLastLogin = false;
				}

				final String sLastLogin = this.preferences.getPreference(user, MainApplication.APP_LAST_LOGIN);
				if (sLastLogin != null) {
					try {
						this.lastLogin = sLastLogin;
					} catch (final Exception e) {
						MainApplication.logger.error(null, e);
					}
				} else {
					this.lastLogin = null;
				}

				if (this.allowRememberPassword) {
					final String sRememberLastLog = this.preferences.getPreference(user,
							MainApplication.APP_REMEMBER_LAST_PASSWORD);
					if (sRememberLastLog != null) {
						try {
							final boolean bRemember = ApplicationManager.parseStringValue(sRememberLastLog, false);
							this.rememberLastPassword = bRemember;
						} catch (final Exception e) {
							MainApplication.logger.error(null, e);
						}
					} else {
						this.rememberLastPassword = false;
					}

					final String sLastPass = this.preferences.getPreference(user, MainApplication.APP_LAST_PASSWORD);
					if (sLastPass != null) {
						try {
							final String sUserDirectory = System.getProperty("user.home");
							if ((sUserDirectory != null) && (sUserDirectory.length() > 0)) {
								final String sUserDirInPrefs = this.preferences.getPreference(null,
										MainApplication.APP_USER_DIR);
								if ((sUserDirInPrefs != null) && (sUserDirInPrefs.length() > 0)) {
									final String sUserDirInPrefEncrypt = encrypt(sUserDirInPrefs);
									if (sUserDirectory.equalsIgnoreCase(sUserDirInPrefEncrypt)) {
										this.lastPassword = encrypt(sLastPass);
									} else {
										this.lastPassword = null;
									}
								} else {
									this.lastPassword = null;
								}
							} else {
								// There is not user directory. Then last
								// password is not stored
								this.lastPassword = null;
							}
						} catch (final Exception e) {
							MainApplication.logger.error(null, e);
						}
					}
				}

				String connectTo = this.preferences.getPreference(user, MainApplication.APP_CONNECT_TO);
				if (connectTo != null) {
					try {
						this.connectTo = connectTo;
					} catch (final Exception e) {
						MainApplication.logger.error(null, e);
					}
				} else {
					connectTo = null;
				}

				// Now the locale
				final String loc = this.preferences.getPreference(null, MainApplication.APP_LOCALE);
				if (loc != null) {
					final StringTokenizer st = new StringTokenizer(loc.toString(), "_");
					String sCountry = null;
					String language = null;
					String variant = "";
					if (st.hasMoreTokens()) {
						language = st.nextToken();
					}
					if (st.hasMoreTokens()) {
						sCountry = st.nextToken();
					}
					if (st.hasMoreTokens()) {
						variant = st.nextToken();
					}
					final Locale lAux = new Locale(language, sCountry, variant);
					MainApplication.logger.debug("Setting locale according to the user preferences : {}", lAux);
					this.setComponentLocale(lAux);
					this.setResourceBundle(ExtendedPropertiesBundle.getExtendedBundle(this.resourcesFileName, lAux));
				}

			}
		} catch (final Exception e) {
			MainApplication.logger.debug(null, e);
		}
	}

	private static String encrypt(final String s) throws IllegalArgumentException {
		if ((s == null) || (s.length() == 0)) {
			throw new IllegalArgumentException(
					"Error: invalid string. If can not be null and the lenght must be greater than 0");
		}
		final byte[] bytes = s.getBytes();
		final byte[] res = new byte[bytes.length];
		final byte[] llave = { 12, 67, 89, 124 };
		for (int i = 0; i < bytes.length; i++) {
			final byte b = bytes[i];
			byte bRes = b;
			for (int j = 0; j < llave.length; j++) {
				bRes = (byte) (bRes ^ llave[j]);
			}
			res[i] = bRes;
		}
		return new String(res);
	}

	/**
	 * Starts the static preferences, just like the font size, the application look and feel and the
	 * language.
	 */
	protected void initStaticPreferences() {
		if (ApplicationManager.DEBUG) {
			MainApplication.logger.debug(this.getClass().toString() + " : Initializing static preferences");
		}
		try {
			if (this.preferenceFile != null) {
				if (this.preferences == null) {
					this.preferences = this.loadApplicationPreferences();
					this.preferences.loadPreferences();
				}
				String user = null;
				if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}

				final String look = this.preferences.getPreference(user, MainApplication.LOOK_AND_FEEL_CLASS_NAME);

				if (!ApplicationManager.useOntimizePlaf) {
					final String sFontSize = this.preferences.getPreference(user, MainApplication.APP_FONTSIZE);
					final String sFont = this.preferences.getPreference(user, MainApplication.APP_FONTNAME);
					if (sFont != null) {
						try {
							// Font size
							int tFont = -1;
							if (sFontSize != null) {
								tFont = Integer.parseInt(sFontSize);
							} else {
								tFont = UIManager.getDefaults().getFont("Label.font").getSize();
							}
							final Font f = new Font(sFont, Font.PLAIN, tFont);
							this.setApplicationFont(f, false);
						} catch (final Exception ex) {
							MainApplication.logger.error(null, ex);
						}
					} else {
						if (sFontSize != null) {
							try {
								final int tFont = Integer.parseInt(sFontSize);
								if (tFont != UIManager.getDefaults().getFont("Label.font").getSize()) {
									FontSelector.setApplicationFontSize(this, tFont);
								}
							} catch (final Exception e) {
								MainApplication.logger.error(null, e);
							}
						} else {
							try {
								FontSelector.setApplicationFontSize(this, 10);
							} catch (final Exception e) {
								MainApplication.logger.trace(null, e);
							}
						}
					}
				}

				final String loc = this.preferences.getPreference(this.getUser(), MainApplication.APP_LOCALE);
				if (loc != null) {
					final StringTokenizer st = new StringTokenizer(loc.toString(), "_");
					String sCountry = null;
					String language = null;
					String variant = "";
					if (st.hasMoreTokens()) {
						language = st.nextToken();
					}
					if (st.hasMoreTokens()) {
						sCountry = st.nextToken();
					}
					if (st.hasMoreTokens()) {
						variant = st.nextToken();
					}
					final Locale lAux = new Locale(language, sCountry, variant);
					if (ApplicationManager.DEBUG) {
						MainApplication.logger.debug("Setting locale according to the user preferences " + lAux);
					}
					this.setComponentLocale(lAux);
					this.setResourceBundle(ExtendedPropertiesBundle.getExtendedBundle(this.resourcesFileName, lAux));
				}

			}
		} catch (final Exception e) {
			MainApplication.logger.trace(null, e);
		}

		this.registerKeyBindings();
	}

	/**
	 * Sets an application font and saves that configuration in the preferences.
	 * @param font
	 * @param savePrefs if true, the new font configuration will be saved to preferences
	 */
	protected void setApplicationFont(final Font font, final boolean savePrefs) {
		FontSelector.setApplicationFont(this, font);
		if ((this.preferences != null) && savePrefs) {
			String user = null;
			if (this.locator instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.locator).getUser();
			}
			this.preferences.setPreference(user, MainApplication.APP_FONTNAME, font.getName());
			this.preferences.setPreference(user, MainApplication.APP_FONTSIZE, "" + font.getSize());
			this.preferences.savePreferences();
		}
	}

	/**
	 * Changes the application font.
	 * @param font
	 */
	public void setApplicationFont(final Font font) {
		this.setApplicationFont(font, true);
	}

	/**
	 * Sets the initial application state. In this case, the main application preferences will be also
	 * loaded.
	 */
	public void setInitialState() {
		if (ApplicationManager.DEBUG) {
			MainApplication.logger.debug(this.getClass().toString() + " : setInitialState");
		}
		try {
			if (this.preferenceFile != null) {
				if (this.preferences == null) {
					this.preferences = this.loadApplicationPreferences();
					this.preferences.loadPreferences();
				}

				if (!BasicApplicationPreferences.remoteUserPreferences) {
					if (this.preferences instanceof BasicApplicationPreferences) {
						if (this.locator instanceof RemoteApplicationPreferenceReferencer) {
							try {
								if (this.loadRemotePreferences) {
									this.preferences = ControlApplicationPreferences.setRemoteProperties(
											(RemoteApplicationPreferenceReferencer) this.locator, this.preferences);
								}
								((BasicApplicationPreferences) this.preferences).setRemoteApplicationPreferences(
										((RemoteApplicationPreferenceReferencer) this.locator)
										.getRemoteApplicationPreferences(this.locator.getSessionId()));
							} catch (final Exception ex) {
								MainApplication.logger.error(null, ex);
							}
						}
					}
				}
				String user = null;
				if (this.locator instanceof ClientReferenceLocator) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}
				if (this.menu instanceof HasPreferenceComponent) {
					((HasPreferenceComponent) this.menu).initPreferences(this.preferences, user);
				}

				if (this.toolBar instanceof HasPreferenceComponent) {
					((HasPreferenceComponent) this.toolBar).initPreferences(this.preferences, user);
				}

				final Enumeration enumKeys = this.formsManagers.keys();
				while (enumKeys.hasMoreElements()) {
					final Object c = enumKeys.nextElement();
					final IFormManager formManager = this.formsManagers.get(c);
					if (formManager != null) {
						formManager.setApplicationPreferences(this.preferences);
					}
				}

				final String statusBarVisible = this.preferences.getPreference(user, MainApplication.APP_STATUS_BAR_VISIBLE);
				if (statusBarVisible != null) {
					final boolean vis = ApplicationManager.parseStringValue(statusBarVisible, true);
					this.setStatusBarVisible(vis);
				}


				if (this.getJMenuBar() != null) {
					this.getJMenuBar().invalidate();
				}
				this.getContentPane().invalidate();
				this.getContentPane().validate();

				int state = 0;

				final String stateS = this.preferences.getPreference(user, BasicApplicationPreferences.APP_WINDOW_STATE);
				if (stateS != null) {
					state = ParseUtils.getInteger(stateS, state);
				}

				final String sSize = this.preferences.getPreference(user, MainApplication.APP_WINDOW_SIZE);
				if (sSize != null) {
					final Dimension d = ApplicationManager.parseStringValue(sSize, this.getSize());
					this.setSize(d);
				}

				final String pos = this.preferences.getPreference(user, MainApplication.APP_WINDOW_POSITION);
				if (pos != null) {
					final Point p = ApplicationManager.parseStringValue(pos, this.getLocation());
					final Point findPoint = new Point(p.x + (this.getWidth() / 2), p.y + (this.getHeight() / 2));

					final GraphicsDevice[] graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getScreenDevices();
					Rectangle bounds = null;
					for (int i = 0; i < graphicsDevice.length; i++) {
						if (graphicsDevice[i].getDefaultConfiguration().getBounds().contains(findPoint)) {
							bounds = graphicsDevice[i].getDefaultConfiguration().getBounds();
							break;
						}
					}

					if (bounds == null) {
						bounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
								.getDefaultScreenDevice()
								.getDefaultConfiguration()
								.getBounds();
					}

					if ((p.x < bounds.x) || (p.x > (bounds.x + bounds.width))) {
						p.x = bounds.x + 50;
					}

					if ((p.y < bounds.y) || (p.y > (bounds.y + bounds.height))) {
						p.y = bounds.y + 50;
					}

					this.setLocation(p);
				}

				this.setExtendedState(state);

			}
		} catch (final Exception e) {
			MainApplication.logger.error("Error creating application preferences", e);
		}
	}

	/**
	 * Sets the status bar text, translated.
	 * @param statusBarText
	 */
	@Override
	public void setStatusBarText(final String statusBarText) {
		if (this.statusBar != null) {
			String sText = statusBarText;
			if (sText == null) {
				sText = "";
			}
			if (sText.length() > 0) {
				try {
					if (this.resources != null) {
						sText = this.resources.getString(statusBarText);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						MainApplication.logger.debug(null, e);
					} else {
						MainApplication.logger.trace(null, e);
					}
				}
			}
			this.statusBar.setStatusText(sText);
		}
	}

	/**
	 * Adds a status bar.
	 * @param iconId the name that the icon will have for the application
	 * @param icon the image to be displayed
	 */
	public void addStatusBarIcon(final String iconId, final ImageIcon icon) {
		if (this.statusBar != null) {
			this.statusBar.addStatusIcon(iconId, icon);
		}
	}

	/**
	 * Removes an icon from the status bar
	 * @param iconId the icon identifier
	 */
	public void removeStatusBarIcon(final String iconId) {
		if (this.statusBar != null) {
			this.statusBar.removeStatusIcon(iconId);
		}
	}

	/**
	 * Sets the tool tip for the status bar icon identified by 'iconId'. The text is not translated.
	 * @param iconId
	 * @param tip
	 */
	public void setStatusBarIconToolTip(final String iconId, final String tip) {
		this.setStatusBarIconToolTip(iconId, tip, false);
	}

	/**
	 * Sets the tool tip for the status bar icon identified by 'iconId'.
	 * @param iconId
	 * @param tip
	 * @param translate if true, the text is translated
	 */
	public void setStatusBarIconToolTip(final String iconId, final String tip, final boolean translate) {
		if (this.statusBar != null) {
			if (translate) {
				String sText = tip;
				if (sText == null) {
					sText = "";
				}
				if (sText.length() > 0) {
					try {
						if (this.resources != null) {
							sText = this.resources.getString(tip);
						}
					} catch (final Exception e) {
						if (ApplicationManager.DEBUG) {
							MainApplication.logger.debug(null, e);
						} else {
							MainApplication.logger.trace(null, e);
						}
					}
				}
				this.statusBar.setStatusIconToolTip(iconId, sText);
			} else {
				this.statusBar.setStatusIconToolTip(iconId, tip);
			}
		}
	}

	/**
	 * Get the current value in the component that shows the complete action percent value in the status
	 * bar
	 * @return
	 */
	public int getStatusBarPercentComplete() {
		if (this.statusBar != null) {
			return (int) (this.statusBar.getPercentComplete() * 100);
		} else {
			return 0;
		}
	}

	/**
	 * Sets the maximum value for the progress bar.
	 * @param maximum
	 */
	public void setStatusBarProgressMaximum(final int maximum) {
		if (this.statusBar != null) {
			this.statusBar.setProgressMaximum(maximum);
		}
	}

	/**
	 * Sets the progress bar current value
	 * @param currentPosition
	 */
	public void setStatusBarProgressPosition(final int currentPosition) {
		if (this.statusBar != null) {
			this.statusBar.setProgressPosition(currentPosition);
		}
	}

	/**
	 * Sets the progress bar current value
	 * @param p
	 * @param paintImmediately .
	 * @deprecated
	 */
	@Deprecated
	public void setStatusBarProgressPosition(final int p, final boolean paintImmediately) {
		if (this.statusBar != null) {
			this.statusBar.setProgressPosition(p, false);
		}
	}

	/**
	 * Sets the status bar progress text
	 * @param text
	 */
	public void setStatusBarProgressText(final String text) {
		this.setStatusBarProgressText(text, true);
	}

	/**
	 * Sets the status bar progress text, optionally translated.
	 * @param text
	 * @param translate if true, the text will be translated
	 */
	public void setStatusBarProgressText(final String text, final boolean translate) {
		if (this.statusBar != null) {
			if (translate) {
				String sText = text;
				if (sText == null) {
					sText = "";
				}
				if (sText.length() > 0) {
					try {
						if (this.resources != null) {
							sText = this.resources.getString(text);
						}
					} catch (final Exception e) {
						if (ApplicationManager.DEBUG) {
							MainApplication.logger.debug(null, e);
						} else {
							MainApplication.logger.trace(null, e);
						}
					}
				}
				this.statusBar.setProgressText(sText);
			} else {
				this.statusBar.setProgressText(text);
			}
		}
	}

	/**
	 * Registers the {@link StatusComponent}. A mouse listener is added to the component so the
	 * component help text can be shown in the status bar when the mouse passes over it.
	 * @param statusComponent the component
	 */
	@Override
	public void registerStatusComponent(final StatusComponent statusComponent) {
		if (this.statusListener == null) {
			this.statusListener = new StatusListener();
		}
		if (statusComponent instanceof Component) {
			((Component) statusComponent).addMouseListener(this.statusListener);
		}
	}

	/**
	 * Unregister the status component. The mouse listener added when the registration process is
	 * removed as well.
	 * @param statusComponent the component
	 */
	@Override
	public void unregisterStatusComponent(final StatusComponent statusComponent) {
		if (this.statusListener == null) {
			return;
		}
		if (statusComponent instanceof Component) {
			((Component) statusComponent).removeMouseListener(this.statusListener);
		}
	}

	/**
	 * Adds a mouse listener to the component with the specified icon.
	 * @param iconId the identification of the component to be added an icon
	 * @param mouseListener
	 */
	public void addMouseListenerToStatusIcon(final String iconId, final MouseListener mouseListener) {
		if (this.statusBar != null) {
			this.statusBar.addMouseListenerToStatusIcon(iconId, mouseListener);
		}
	}

	/**
	 * Removes the mouse listener from the specified icon
	 * @param iconId the identification of the component of which the listener will be removed
	 * @param mouseListener
	 */
	public void removeMouseListenerFromStatusIcon(final String iconId, final MouseListener mouseListener) {
		if (this.statusBar != null) {
			this.statusBar.removeMouseListenerFromStatusIcon(iconId, mouseListener);
		}
	}

	/**
	 * Sets the visibility of the status bar, if any. In case a preferences file has been established to
	 * the application, the status bar preference will be set.
	 * @param visible
	 */
	public void setStatusBarVisible(final boolean visible) {
		if (this.statusBar != null) {
			this.statusBar.setVisible(visible);
		}
		if (this.preferences != null) {
			String user = null;
			if (this.locator instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.locator).getUser();
			}
			this.preferences.setPreference(user, MainApplication.APP_STATUS_BAR_VISIBLE,
					ApplicationManager.parseBooleanValue(visible));
		}
	}

	/**
	 * Returns the status bar visibility.
	 * @return true when the status bar exists and is visible
	 */
	public boolean isStatusBarVisible() {
		if (this.statusBar != null) {
			return this.statusBar.isVisible();
		}
		return false;
	}

	/**
	 * Determines the visibility status of the application buttons bar.
	 * @param visible
	 */
	public void setToolBarVisible(final boolean visible) {
		if (this.toolBar != null) {
			this.toolBar.setVisible(visible);
		}
		if (this.preferences != null) {
			String user = null;
			if (this.locator instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.locator).getUser();
			}
			this.preferences.setPreference(user, MainApplication.APP_TOOL_BAR_VISIBLE,
					ApplicationManager.parseBooleanValue(visible));
		}
	}

	/**
	 * Returns the buttons bar visibility status
	 * @return true when the buttons bar exists and is visible
	 */
	public boolean isToolBarVisible() {
		if (this.toolBar != null) {
			return this.toolBar.isVisible();
		}
		return false;
	}

	/**
	 * Sets the buttons bar listener.
	 * @param toolBarListener
	 */
	@Override
	public void setToolBarListener(final ToolBarListener toolBarListener) {
		this.toolBarListener = toolBarListener;
	}

	/**
	 * Provides a reference to the buttons bar listener.
	 * @return the buttons bar listener or null if it does not exist
	 */
	@Override
	public ToolBarListener getToolBarListener() {
		return this.toolBarListener;
	}

	/**
	 * Provides a reference to the tool bar
	 * @return the toll bar or null if it does not exist
	 */
	@Override
	public JToolBar getToolBar() {
		return this.toolBar;
	}

	/**
	 * Check an ApplicationPermission using the ClientSecurityManager.<br>
	 * Permissions can be specified like the following examples:<br>
	 * &lt;APPLICATION&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;ToolbarPermission restricted="yes" /&gt;<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;StatusbarPermission restricted="yes" /&gt;<br>
	 * &lt;/APPLICATION&gt;<br>
	 * @param permissionName The permission name like ToolbarPermission or StatusbarPermission
	 * @return
	 */
	public static boolean checkApplicationPermission(final String permissionName) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			final ApplicationPermission applicationPermission = new ApplicationPermission(permissionName, true);
			try {
				manager.checkPermission(applicationPermission);
				return true;
			} catch (final Exception e) {
				if (e instanceof NullPointerException) {
					MainApplication.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					MainApplication.logger.debug(null, e);
				} else {
					MainApplication.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Sets the application tool bar. The tool bar is placed by default in the north part of the window,
	 * or under the preferences values for the tool bar.
	 * @param applicationToolBar the application tool bar
	 */
	@Override
	public void setToolBar(final JToolBar applicationToolBar) {

		this.toolBar = applicationToolBar;
		Object constraints = BorderLayout.NORTH;
		if (this.preferences != null) {
			String user = null;
			if (this.locator instanceof ClientReferenceLocator) {
				user = ((ClientReferenceLocator) this.locator).getUser();
			}
			final String pref = this.preferences.getPreference(user, BasicApplicationPreferences.APP_TOOLBAR_LOCATION);
			if (pref != null) {
				constraints = pref;
			}
		}
		if (constraints.equals(BorderLayout.NORTH) || constraints.equals(BorderLayout.SOUTH)) {
			this.toolBar.setOrientation(SwingConstants.HORIZONTAL);
		} else {
			this.toolBar.setOrientation(SwingConstants.VERTICAL);
		}

		this.panelAuxCardLayout.add(this.toolBar, constraints);

		if ((this.toolBar != null) && (this.toolBar instanceof Internationalization)) {
			((Internationalization) this.toolBar).setResourceBundle(this.resources);
		}
		this.getContentPane().invalidate();
		this.getContentPane().validate();
		this.getContentPane().doLayout();
	}

	/**
	 * Sets the title key for this frame. The value in the client application will be the translation of
	 * the key according to the application locale.
	 * @param titleKey
	 */
	public void setTitleKey(final String titleKey) {
		this.keyTitle = titleKey;
		try {
			if (this.resources != null) {
				this.setTitle(this.resources.getString(this.keyTitle));
			}
		} catch (final Exception e) {
			this.setTitle(this.keyTitle);
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MainApplication.logger.debug(null, e);
			} else {
				MainApplication.logger.trace(null, e);
			}
		}
	}

	/**
	 * Shows or hides the client application debug windows. The windows are shown if they are hidden and
	 * vice versa.
	 */
	private class Act extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (ApplicationManager.isApplicationManagerWindowVisible()) {
				ApplicationManager.setApplicationManagerWindowVisible(false);
				ApplicationManager.setMemoryMonitorWindowVisible(false);
				try {
					final Class clazz = Class.forName("com.ontimize.util.rmitunneling.RMIHTTPTunnelingSocketFactory");
					final Method method = clazz.getMethod("setStreamInfoWindowVisible", new Class[] { boolean.class });
					method.invoke(null, new Object[] { false });

					// RMIHTTPTunnelingSocketFactory.setStreamInfoWindowVisible(false);
				} catch (final Exception nfe) {
					logger.trace("{}", nfe.getMessage(), nfe);
				}
			} else {
				ApplicationManager.setApplicationManagerWindowVisible(true);
				ApplicationManager.setMemoryMonitorWindowVisible(true);
			}
		}

	}

	private InformationDialog informationDialog = null;

	protected List defaultApplicationKeyBindings;

	/**
	 * Registers the key strokes for the application.
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>keystroke</b></td>
	 * <td><b>function</b></td>
	 * </tr>
	 * <tr>
	 * <td>space bar + meta + control + shift</td>
	 * <td>Show the client help windows</td>
	 * </tr>
	 * <tr>
	 * <td>F12 + meta + control + shift</td>
	 * <td>saves the language file</td>
	 * </tr>
	 * <tr>
	 * <td>return + meta + control + shift</td>
	 * <td>shows system information</td>
	 * </tr>
	 * <tr>
	 * <td>p + control + shift</td>
	 * <td>application preferences</td>
	 * </tr>
	 * <tr>
	 * <td>l + control + shift</td>
	 * <td>license server</td>
	 * </tr>
	 * <tr>
	 * <td>v + control + shift</td>
	 * <td>ontimize version</td>
	 * </tr>
	 * <tr>
	 * <td>t + control + shift</td>
	 * <td>license text</td>
	 * </tr>
	 * <tr>
	 * <td>'+' + control</td>
	 * <td>maximize / minimize when maximized</td>
	 * </tr>
	 * <tr>
	 * <td>'c' + control + shift</td>
	 * <td>shows the form cache viewer</td>
	 * </tr>
	 * </Table>
	 */

	protected void registerKeyBindings() {
		try {
			final InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			final ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();

			final ApplicationPreferences prefs = this.getPreferences();
			String user = null;
			if (prefs != null) {
				if (this.getReferenceLocator() instanceof ClientReferenceLocator) {
					user = ((ClientReferenceLocator) this.getReferenceLocator()).getUser();
				}
			}

			// Application manager window
			final String key = "ApplicationManagerWindow";
			final KeyStroke ks = this.getPreferredKeyStroke(prefs, user, key,
					KeyStroke.getKeyStroke(KeyEvent.VK_SPACE,
							InputEvent.ALT_MASK | InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, true));
			final Act act = new Act();
			this.setKeyBinding(key, ks, act, inMap, actMap, true);

			// Bundle file
			final String key2 = "BundleFile";
			KeyStroke ks2 = KeyStroke.getKeyStroke(KeyEvent.VK_F12,
					InputEvent.ALT_MASK | InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, true);
			ks2 = this.getPreferredKeyStroke(prefs, user, key2, ks2);
			final Act act2 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Save default language file");
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
					final int option = fc.showSaveDialog(MainApplication.this);
					if (option == JFileChooser.APPROVE_OPTION) {
						final File f = fc.getSelectedFile();
						String file = f.getPath();
						if (!file.endsWith(".properties")) {
							file = file + ".properties";
						}
						MainApplication.this.saveTextsToTranslate(file, null);
					}
				}
			};
			this.setKeyBinding(key2, ks2, act2, inMap, actMap, false);

			// System information
			final String key3 = "SystemInformation";
			KeyStroke ks3 = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
					InputEvent.ALT_MASK | InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, true);
			ks3 = this.getPreferredKeyStroke(prefs, user, key3, ks3);
			final Act act3 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ApplicationManager.showSystemInformation();
				}
			};
			this.setKeyBinding(key3, ks3, act3, inMap, actMap, true);

			// Preferences
			final String key4 = "Preferences";
			KeyStroke ks4 = KeyStroke.getKeyStroke(KeyEvent.VK_P,
					InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, false);
			ks4 = this.getPreferredKeyStroke(prefs, user, key4, ks4);
			final Act act4 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ControlApplicationPreferences.showControlApplicationPreferences(MainApplication.this,
							(ClientReferenceLocator) MainApplication.this.getReferenceLocator(),
							MainApplication.this.getResourceBundle());
				}
			};
			this.setKeyBinding(key4, ks4, act4, inMap, actMap, true);

			// Maximize
			final String key6 = "Maximize";
			KeyStroke ks6 = KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK, false);
			ks6 = this.getPreferredKeyStroke(prefs, user, key6, ks6);
			final Act act6 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					MainApplication.this.maximize();
				}
			};

			this.setKeyBinding(key6, ks6, act6, inMap, actMap, true);

			// Ontimize version
			final String key7 = "OntimizeVersion";
			KeyStroke ks7 = KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, false);
			ks7 = this.getPreferredKeyStroke(prefs, user, key7, ks7);
			final Act act7 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (MainApplication.this.informationDialog == null) {
						MainApplication.this.informationDialog = new InformationDialog(true);
						ApplicationManager.center(MainApplication.this.informationDialog);
					}
					MainApplication.this.informationDialog.setVisible(true);
				}
			};
			this.setKeyBinding(key7, ks7, act7, inMap, actMap, true);

			// Cache viewer
			final String key9 = "CacheViewer";
			KeyStroke ks9 = KeyStroke.getKeyStroke(KeyEvent.VK_C,
					InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK, false);
			ks9 = this.getPreferredKeyStroke(prefs, user, key9, ks9);
			final Act act9 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					CacheManager.CacheManagerViewer.showViewer(null);
				}
			};
			this.setKeyBinding(key9, ks9, act9, inMap, actMap, true);

			final String key10 = "RemoteAdministration";
			KeyStroke ks10 = KeyStroke.getKeyStroke(KeyEvent.VK_A,
					InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, false);
			ks10 = this.getPreferredKeyStroke(prefs, user, key10, ks10);
			final Act act10 = new Act() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final IRemoteAdministrationWindow window = ApplicationManager
							.getRemoteAdminWindow((RemotelyManageable) MainApplication.this.locator);
					window.setIconImage(MainApplication.this.getIconImage());
					window.showWindow();
				}
			};

			this.setKeyBinding(key10, ks10, act10, inMap, actMap, true);
			if (ApplicationManager.DEBUG) {
				MainApplication.logger.debug("MainApplication: Keybindings registered");
			}
		} catch (final Exception e) {
			MainApplication.logger.error("Error registering keybindings", e);
		}
	}

	// TODO Implementar para los nuevos FormManagers
	public void maximize() {
		/*
		 * if (!this.maximize) { String sActiveFormManagerName = this.getActiveFMName(); if
		 * (sActiveFormManagerName != null) { FormManager formManager =
		 * this.getFormManager(sActiveFormManagerName); this.formMaximize = formManager.getActiveForm();
		 * this.maximize = true; this.menu.setVisible(false);
		 * formManager.formPanel.remove(this.formMaximize); this.stateFrame =
		 * MainApplication.this.getExtendedState();
		 * MainApplication.this.setExtendedState(Frame.MAXIMIZED_BOTH);
		 * this.centerPanel.add(this.formMaximize, MainApplication.MAXIMIZE_VIEW); ((CardLayout)
		 * this.centerPanel.getLayout()).show(this.centerPanel, MainApplication.MAXIMIZE_VIEW); } } else {
		 * String sActiveFormManagerName = this.getActiveFMName(); if (sActiveFormManagerName != null) {
		 * this.maximize = false; this.centerPanel.remove(this.formMaximize); this.menu.setVisible(true);
		 * MainApplication.this.setExtendedState(this.stateFrame); FormManager formManager =
		 * this.getFormManager(sActiveFormManagerName);
		 * formManager.formPanel.add(this.formMaximize.getArchiveName(), this.formMaximize); ((CardLayout)
		 * formManager.formPanel.getLayout()).show(formManager.formPanel,
		 * this.formMaximize.getArchiveName()); ((CardLayout)
		 * this.centerPanel.getLayout()).show(this.centerPanel, MainApplication.NORMAL_VIEW); } }
		 */
	}

	private String getKeyStrokePreferencesKey(final String keyName) {
		return ShortcutDialogConfiguration.getAcceleratorPreferencesKey(this.getKeyStrokeGroupName(), keyName);
	}

	protected KeyStroke getPreferredKeyStroke(final ApplicationPreferences preferences, final String user, final String keyName,
			final KeyStroke defaultKeyStroke) {
		KeyStroke ks = defaultKeyStroke;
		if (preferences != null) {
			final String pref = preferences.getPreference(user, this.getKeyStrokePreferencesKey(keyName));
			if (pref != null) {
				final String prefs[] = pref.split(" ");
				try {
					ks = KeyStroke.getKeyStroke(Integer.parseInt(prefs[1]), Integer.parseInt(prefs[0]));
				} catch (final Exception e) {
					MainApplication.logger.trace(null, e);
				}
			}
		}
		return ks;
	}

	public void setKeyBinding(final String key, final KeyStroke keyStroke, final Action action, final InputMap inMap, final ActionMap actMap,
			final boolean isConfigurable) {

		inMap.put(keyStroke, key);
		actMap.put(key, action);

		if (isConfigurable) {
			final ComponentKeyStroke compKeyStroke = new ComponentKeyStroke((JComponent) this.getContentPane(),
					this.getKeyStrokeGroupName());
			compKeyStroke.setKeyName(key);
			compKeyStroke.setKeyStroke(keyStroke);
			compKeyStroke.setAction(action);
			compKeyStroke.setInputMapCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);

			if (this.defaultApplicationKeyBindings == null) {
				this.defaultApplicationKeyBindings = new Vector();
			}

			this.defaultApplicationKeyBindings.add(compKeyStroke);
		}

	}

	private static String ExtOpThreadsMonitor = "ExtOpThreadsMonitor";

	/**
	 * Sets a ExtOpThreadsMonitor to the application. Users can see this monitor using the status bar
	 * icon on the right.
	 * @param op the operation thread monitor
	 */
	public void registerExtOpThreadsMonitor(final ApplicationManager.ExtOpThreadsMonitor op) {
		if (op != null) {
			if (this.statusBar != null) {
				if (this.statusBar.getIconLabel(MainApplication.ExtOpThreadsMonitor) == null) {
					this.addStatusBarIcon(MainApplication.ExtOpThreadsMonitor,
							ApplicationManager.getDefaultExtOpThreadsMonitorIcon());
					this.addMouseListenerToStatusIcon(MainApplication.ExtOpThreadsMonitor, new MouseAdapter() {

						@Override
						public void mouseClicked(final MouseEvent e) {
							if (e.getClickCount() == 2) {
								ApplicationManager.getExtOpThreadsMonitor(MainApplication.this).setVisible(true);
							}
						}
					});
				}
			}
		}
	}

	/**
	 * Turns off the feature of remembering the last password, so the next time the application is
	 * launched, it will ask for a new password.
	 */
	public void deactivatedRememberLastPassword() {
		if (this.rememberLastPassword && (this.preferences != null)) {
			this.preferences.setPreference(null, MainApplication.APP_REMEMBER_LAST_PASSWORD,
					ApplicationManager.parseBooleanValue(false));
			this.preferences.savePreferences();
		}
	}

	/**
	 * Determines weather the feature of remembering the password between sessions is enabled in the
	 * local machine.
	 * @return true if the password is being remembered
	 */
	public boolean isRememberLastPasswordEnabled() {
		return this.rememberLastPassword;
	}

	/**
	 * Minimizes the application and sends it to the tray bar. In windows machines and with a version of
	 * java previous to 1.6 in order to work properly, the systray java library and also the dll that
	 * perform the minimization must be in the application's path.
	 * @return true if the application was minimized successfully
	 */
	public boolean sendToTray() {
		try {
			final Class systemTrayClass = Class.forName("java.awt.SystemTray");
			if (systemTrayClass != null) {
				final Method isSupportedMethod = systemTrayClass.getMethod("isSupported", new Class[0]);
				final Object isSupportedResult = isSupportedMethod.invoke(systemTrayClass, new Object[0]);
				if ((isSupportedResult != null) && (isSupportedResult instanceof Boolean)) {
					if (this.trayIcon == null) {
						this.buildTray();
					}

					final Method getSystemTrayMethod = systemTrayClass.getMethod("getSystemTray", new Class[0]);
					final Object systemTrayObject = getSystemTrayMethod.invoke(systemTrayClass, new Object[0]);
					final Class trayIconClass = this.trayIcon.getClass();
					final Method addMethod = systemTrayObject.getClass().getMethod("add", new Class[] { trayIconClass });
					addMethod.invoke(systemTrayObject, new Object[] { this.trayIcon });
					this.getFrame().setVisible(false);
					return true;
				}
			}
		} catch (final Exception ex) {
			if (ApplicationManager.DEBUG) {
				MainApplication.logger.debug(null, ex);
			} else {
				MainApplication.logger.trace(null, ex);
			}
			return this.sendToTrayUsingSysTrayLibrary();
		}

		return false;
	}

	protected void buildTray() {
		String sTitle = "";
		sTitle = this.getTitle();
		String icon = "iconimatia.png";
		final String iconStr = this.getIcon();
		if (iconStr != null) {
			icon = iconStr;
		}

		try {
			final Class trayIconClass = Class.forName("java.awt.TrayIcon");
			final Constructor trayIconConstructor = trayIconClass
					.getConstructor(new Class[] { Image.class, String.class, PopupMenu.class });
			this.trayIcon = trayIconConstructor.newInstance(new Object[] { ImageManager.getIcon(icon).getImage(),
					sTitle, this.createTrayContextualMenu(sTitle) });
			final Method setImageAutoSizeMethod = trayIconClass.getMethod("setImageAutoSize", new Class[] { boolean.class });
			setImageAutoSizeMethod.invoke(this.trayIcon, new Object[] { Boolean.TRUE });

			final Object systemTrayListener = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						MainApplication.this.removeIconFromSystemTray();
						MainApplication.this.showApplication();
						MainApplication.this.toFront();
					} catch (final Exception error) {
						MainApplication.logger.trace(null, error);
					}
				}
			};

			final Method addActionListenerMethod = trayIconClass.getMethod("addActionListener",
					new Class[] { ActionListener.class });
			addActionListenerMethod.invoke(this.trayIcon, new Object[] { systemTrayListener });
		} catch (final Exception e) {
			MainApplication.logger.trace(null, e);
		}
	}

	protected PopupMenu createTrayContextualMenu(final String sTitle) {
		final PopupMenu menu = new PopupMenu(sTitle);
		final MenuItem m = new MenuItem(ApplicationManager.getTranslation("Exit", this.getResourceBundle()));
		m.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				MainApplication.this.exit();
			}
		});
		menu.add(m);
		menu.addSeparator();
		final MenuItem m1 = new MenuItem(
				ApplicationManager.getTranslation("mainapplication.show_application", this.getResourceBundle()));
		m1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					MainApplication.this.removeIconFromSystemTray();
				} catch (final Exception ex) {
					MainApplication.logger.error(null, ex);
				}
				MainApplication.this.showApplication();
				MainApplication.this.toFront();
			}
		});
		menu.add(m1);
		return menu;
	}

	protected void removeIconFromSystemTray() throws Exception {
		final Class systemTrayClass = Class.forName("java.awt.SystemTray");
		final Method getSystemTrayMethod = systemTrayClass.getMethod("getSystemTray", new Class[0]);
		final Object systemTrayObject = getSystemTrayMethod.invoke(systemTrayClass, new Object[0]);
		final Class trayIconClass = this.trayIcon.getClass();
		final Method addMethod = systemTrayObject.getClass().getMethod("remove", new Class[] { trayIconClass });
		addMethod.invoke(systemTrayObject, new Object[] { this.trayIcon });

	}

	protected boolean sendToTrayUsingSysTrayLibrary() {
		try {
			if (this.toolBarIcon != null) {
				com.ontimize.windows.systray.SystrayUtils.show(this.toolBarIcon);
				this.getFrame().setVisible(false);
			} else {
				String sTitle = "";
				sTitle = this.getTitle();

				final JMenu menu = new JMenu(sTitle);
				final JMenuItem m = new JMenuItem(ApplicationManager.getTranslation("Exit", this.getResourceBundle()));
				m.setActionCommand("Exit");
				menu.add(m);
				menu.addSeparator();
				final JMenuItem m1 = new JMenuItem(ApplicationManager.getTranslation("mainapplication.show_application",
						this.getResourceBundle()));
				m1.setActionCommand("mainapplication.show_application");
				menu.add(m1);
				String icon = "iconimatia";

				String iconStr = this.getIcon();
				if (iconStr != null) {
					final int pointIndex = iconStr.lastIndexOf(".");
					if (pointIndex > 0) {
						iconStr = iconStr.substring(0, pointIndex);
					}
					icon = iconStr;
				}

				final com.ontimize.windows.systray.SystrayUtils.SystrayListener l = new com.ontimize.windows.systray.SystrayUtils.SystrayListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (MainApplication.this.isLocked()) {
							if (e.getActionCommand().equals("Exit")) {
								MainApplication.this.dLock.exit.doClick();
							} else if (e.getActionCommand().equals("mainapplication.show_application")) {
								MainApplication.this.dLock.toFront();
							}
						} else {
							if (e.getActionCommand().equals("Exit")) {
								MainApplication.this.exit();
							} else if (e.getActionCommand().equals("mainapplication.show_application")) {
								MainApplication.this.showApplication();
								MainApplication.this.toFront();
							}
						}
					}

					@Override
					public void iconLeftDoubleClicked() {
						if (MainApplication.this.isLocked()) {
							MainApplication.this.dLock.toFront();
						} else {
							MainApplication.this.showApplication();
							MainApplication.this.toFront();
						}
					}

					@Override
					public void iconLeftClicked() {
					}
				};
				try {
					this.toolBarIcon = com.ontimize.windows.systray.SystrayUtils.addSystemTrayIcon(icon, sTitle, menu,
							l);
				} catch (final Exception e) {
					// If this happen it is probably because the application
					// logo.ico does not exist.
					icon = "iconimatia";
					this.toolBarIcon = com.ontimize.windows.systray.SystrayUtils.addSystemTrayIcon(icon, sTitle, menu,
							l);
					MainApplication.logger.error(
							"WARNING: " + icon + ".ico does not exist to minimize the application in the systray", e);
				}
				this.setVisible(false);
			}
			return true;
		} catch (final Exception ex) {
			if (ApplicationManager.DEBUG) {
				MainApplication.logger.error(null, ex);
			} else {
				MainApplication.logger.trace(null, ex);
			}
			return false;
		}
	}

	/**
	 * Removes the application short cut from the system tray (windows).
	 */
	protected void removeFromTray() {
		if (this.toolBarIcon != null) {
			com.ontimize.windows.systray.SystrayUtils.hide(this.toolBarIcon);
		} else if (this.trayIcon != null) {
			try {
				this.removeIconFromSystemTray();
			} catch (final Exception ex) {
				if (ApplicationManager.DEBUG) {
					MainApplication.logger.debug(null, ex);
				} else {
					MainApplication.logger.trace(null, ex);
				}
			}
		}
	}

	/**
	 * Installs the JGoodies theme.
	 * @param theme
	 * @deprecated
	 */
	@Deprecated
	public void installJGoodiesTheme(final String theme) {
	}

	/**
	 * Return the application name.
	 */
	@Override
	public String getName() {
		return this.name;
	}

}
