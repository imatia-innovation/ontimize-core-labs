package com.ontimize.gui.login;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.ShortcutDialogConfiguration.ComponentKeyStroke;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.RemotelyManageable;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.EncryptHelper;
import com.ontimize.security.CertificateUtils;
import com.ontimize.security.TokenLogin;
import com.ontimize.security.provider.SunPKCS11Wrapper;
import com.ontimize.util.remote.IRemoteAdministrationWindow;
import com.ontimize.xml.DefaultXMLParametersManager;

public abstract class AbstractLoginDialog extends JDialog implements ILoginDialog {

	private static final Logger logger = LoggerFactory.getLogger(AbstractLoginDialog.class);

	public static final String LOGIN_PASSWORD_DATA_FIELD = "LoginPasswordDataField";

	public static final String LOGIN_TEXT_DATA_FIELD = "LoginTextDataField";

	public static final String LOGIN_CHECK_DATA_FIELD = "LoginCheckDataField";

	public static final String LOGIN_BUTTON = "LoginButton";

	protected boolean loggedIn = false;

	protected EntityReferenceLocator locator;

	protected Application application = null;

	protected TextDataField user = null;

	protected PasswordDataField password = null;

	protected CheckDataField rememberLogin = null;

	protected CheckDataField rememberPassword = null;

	protected JComboBox serverCombo = null;

	protected Button acceptButton = null;

	protected Button cancelButton = null;

	protected JLabel status = null;

	protected Label connectToLabel = null;

	protected ResourceBundle bundle = null;

	protected AcceptListener acceptListener = null;

	protected CancelListener cancelListener = null;

	protected Color statusBarForeground = Color.red;

	protected Button certificateButton = null;

	protected CertificateListener certificateListener = null;

	private AliasCertPair aliasCertPair = null;

	// When this variable is set, automatically userDNIeCNColumn is set in user
	// field.
	public static boolean useDNIeCN = false;

	// since 5.2068EN-0.5
	// Indicates the DNIe field whose value will be fixed in user field.
	public static String userDNIeCNColumn = CertificateUtils.DNIe_CN;

	protected Map params = new Hashtable();

	public AbstractLoginDialog(final Application mainApplication, final Map parameters, final EntityReferenceLocator locator) {
		super(mainApplication.getFrame(), ILoginDialog.WINDOW_TITLE, true);
		this.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		this.locator = locator;
		this.application = mainApplication;
		this.registerKeyBindings();
		this.params = parameters;
		this.setModalityType(ModalityType.DOCUMENT_MODAL);
		if (((ClientReferenceLocator) locator).isAllowCertificateLogin()) {
			this.checkDNIeInstalled();
		}
	}

	/**
	 * Register key bindings for login dialog.
	 *
	 * @since 5.2061EN - Registered keystroke for remote administration window.
	 */
	protected void registerKeyBindings() {
		final InputMap inMap = ((JComponent) this.getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		final ActionMap actMap = ((JComponent) this.getContentPane()).getActionMap();
		final String key1 = "RemoteAdministration";
		final KeyStroke ks1 = KeyStroke.getKeyStroke(KeyEvent.VK_A,
				InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK, false);
		final Act act1 = new Act() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final IRemoteAdministrationWindow window = ApplicationManager
						.getRemoteAdminWindow((RemotelyManageable) AbstractLoginDialog.this.locator);
				window.setIconImage(ImageManager.getIcon(ImageManager.ONTIMIZE).getImage());
				window.showWindow();
			}
		};

		this.setKeyBinding(key1, ks1, act1, inMap, actMap);
	}

	public void setKeyBinding(final String key, final KeyStroke keyStroke, final Action action, final InputMap inMap, final ActionMap actMap) {

		inMap.put(keyStroke, key);
		actMap.put(key, action);

		final ComponentKeyStroke compKeyStroke = new ComponentKeyStroke((JComponent) this.getContentPane(),
				"Remote Administration");
		compKeyStroke.setKeyName(key);
		compKeyStroke.setKeyStroke(keyStroke);
		compKeyStroke.setAction(action);
		compKeyStroke.setInputMapCondition(JComponent.WHEN_IN_FOCUSED_WINDOW);
	}

	@Override
	public EntityReferenceLocator getEntityReferenceLocator() {
		return this.locator;
	}

	@Override
	public Application getApplication() {
		return this.application;
	}

	@Override
	public boolean login() {
		this.setVisible(true);
		return this.isLoggedIn();
	}

	@Override
	public boolean isLoggedIn() {
		return this.loggedIn;
	}

	protected void init(final Map parameters) {
		if (parameters.containsKey(ILoginDialog.REMEMBER_PASSWORD)) {
			final boolean allowRememberPassword = ApplicationManager
					.parseStringValue((String) parameters.get(ILoginDialog.REMEMBER_PASSWORD));
			if (!allowRememberPassword) {
				this.rememberPassword.setValue(Boolean.FALSE);
				this.rememberPassword.setVisible(false);
			}
		}

		if (parameters.containsKey(ILoginDialog.REMEMBER_LAST_LOGIN)) {
			final boolean rememberLastLogin = ApplicationManager
					.parseStringValue((String) parameters.get(ILoginDialog.REMEMBER_LAST_LOGIN));
			if (rememberLastLogin) {
				this.rememberLogin.setValue(new Boolean(true));
			}
		}

		if (parameters.containsKey(ILoginDialog.LAST_LOGIN)) {
			final String lastLogin = (String) parameters.get(ILoginDialog.LAST_LOGIN);
			if (lastLogin != null) {
				this.user.setValue(lastLogin);
			}
		}
	}

	protected void savePreferences() {

		final ApplicationPreferences preferences = this.getApplication().getPreferences();
		if (preferences != null) {

			if (this.isRememberLogin() && (((ClientReferenceLocator) this.locator).getUser() != null)) {
				preferences.setPreference(null, MainApplication.APP_REMEMBER_LAST_LOGIN,
						ApplicationManager.parseBooleanValue(true));
				preferences.setPreference(null, MainApplication.APP_LAST_LOGIN,
						((ClientReferenceLocator) this.locator).getUser());
				preferences.savePreferences();
				if (this.isRememberPassword() && (this.getPasswordValue() != null)) {
					preferences.setPreference(null, MainApplication.APP_REMEMBER_LAST_PASSWORD,
							ApplicationManager.parseBooleanValue(true));
					preferences.setPreference(null, MainApplication.APP_LAST_PASSWORD,
							EncryptHelper.encrypt(this.getPasswordValue()));

					final String userDir = System.getProperty("user.home");
					if ((userDir != null) && (userDir.length() > 0)) {
						preferences.setPreference(null, MainApplication.APP_USER_DIR, EncryptHelper.encrypt(userDir));
					}

					preferences.savePreferences();
				}
			}
			String connectTo = null;
			if (this.isServerSelection()) {
				connectTo = this.getConnectedServer();
			}
			if (connectTo != null) {
				preferences.setPreference(null, MainApplication.APP_CONNECT_TO, connectTo);
				preferences.savePreferences();
			}
		}
	}

	@Override
	public boolean checkLogin() throws Exception {

		if (this.isServerSelection()) {
			final String sSelected = this.getConnectedServer();
			if (sSelected.equalsIgnoreCase(ConnectionManager.getServerHostname())) {
				ConnectionManager.setAutomaticInternalNetworkDetection(false);
			} else {
				ConnectionManager.setAutomaticInternalNetworkDetection(true);
			}
		}

		if (!SwingUtilities.isEventDispatchThread()) {
			AbstractLoginDialog.logger.debug("Accept button is not eventdispatchthread");
		}

		try {
			final Object us = this.getUserValue();
			final Object pass = this.getPasswordValue();

			if ((us != null) && (pass != null)) {
				try {
					int sessionId = -1;
					if (this.getEntityReferenceLocator() != null) {
						sessionId = this.getEntityReferenceLocator().startSession(us.toString(), pass.toString(), null);
					}
					if (sessionId >= 0) {
						this.loggedIn = true;
						this.savePreferences();
						this.changePassword();
					} else {
						this.loggedIn = false;
					}
				} catch (final Exception e2) {
					this.loggedIn = false;
					throw e2;
				}
			} else {
				this.loggedIn = false;
			}

			if (this.loggedIn) {
				return this.loggedIn;
			}

		} catch (final Exception ex) {
			throw ex;
		}

		return !this.loggedIn;
	}

	protected void changePassword() {
		try {
			if (((UtilReferenceLocator) this.getEntityReferenceLocator()).supportChangePassword(this.getUserValue(),
					this.getEntityReferenceLocator().getSessionId())) {

				IChangePasswordDialog cpdialog = null;

				if (this.params.containsKey(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS)) {

					try {
						final Class cpClass = Class
								.forName(this.params.get(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS).toString());
						final Constructor constructor = cpClass
								.getConstructor(new Class[] { Application.class, Map.class,
										EntityReferenceLocator.class, String.class, String.class });
						cpdialog = (IChangePasswordDialog) constructor.newInstance(new Object[] { this.getApplication(),
								this.params, this.getEntityReferenceLocator(), this.user
								.getValue()
								.toString(),
								this.password.getValue().toString() });
					} catch (final Exception e) {
						AbstractLoginDialog.logger.error(
								"Cannot instanciate the class: {}. Using default class. Error: {}",
								this.params.get(IChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CLASS).toString(),
								e.getMessage(), e);
						cpdialog = new DefaultChangePasswordDialog(this.getApplication(), this.params,
								this.getEntityReferenceLocator(), this.user.getValue().toString(),
								this.password.getValue().toString());
					}

				} else {
					cpdialog = new DefaultChangePasswordDialog(this.getApplication(), this.params,
							this.getEntityReferenceLocator(), this.user.getValue().toString(),
							this.password.getValue().toString());
				}

				cpdialog.setResourceBundle(this.bundle);
				cpdialog.showChangePasswordInParentLocation(this);
			}
		} catch (final Exception e) {
			AbstractLoginDialog.logger.error("Error to obtain the reference to LoginEntity", e);
		}
	}

	protected TextDataField createLogin(final Map parameters) {
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_TEXT_DATA_FIELD);
		p.put("attr", "User_");
		p.put("size", "12");
		return new TextDataField(p);
	}

	protected PasswordDataField createPassword(final Map parameters) {
		final Map p2 = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		p2.put("attr", "Password");
		p2.put("size", "12");
		if (parameters.containsKey(ILoginDialog.ENCRYPT)) {
			final boolean encrypt = ApplicationManager.parseStringValue((String) parameters.get(ILoginDialog.ENCRYPT));
			if (encrypt) {
				p2.put("encrypt", "yes");
			}
		}
		return new PasswordDataField(p2);
	}

	protected Button createAcceptButton(final Map parameters) {
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_BUTTON);
		p.put("key", "application.accept");
		p.put("text", "application.accept");
		return new Button(p);
	}

	protected Button createCancelButton(final Map parameters) {
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_BUTTON);
		p.put("key", "application.cancel");
		p.put("text", "application.cancel");
		return new Button(p);
	}

	protected Button createCertificatesButton(final Map parameters) {
		final Map p = new Hashtable();
		p.put("key", "application.certificate");
		p.put("text", "application.certificate");
		return new Button(p);
	}

	protected CheckDataField createRememberLogin(final Map parameters) {
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_CHECK_DATA_FIELD);

		p.put("attr", "RememberLogin");
		//p.put("fontsize", "10");
		p.put("labelposition", "right");
		return new CheckDataField(p);
	}

	protected CheckDataField createRememberPassword(final Map parameters) {
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_CHECK_DATA_FIELD);
		p.put("attr", "RememberPassword");
		//p.put("fontsize", "10");
		p.put("labelposition", "right");
		return new CheckDataField(p);
	}

	protected Label createServerLabel() {
		final Map pet = new Hashtable();
		pet.put("attr", "et");
		pet.put("text", ILoginDialog.CONNECT_TO_KEY);
		pet.put("fontsize", "10");
		return new Label(pet);
	}

	protected JComboBox createServerCombo(final Map parameters) {
		final boolean showDNSOptions = ApplicationManager.parseStringValue((String) parameters.get(ILoginDialog.DNS_OPTIONS));

		final String ipInternaServidor = ConnectionManager.getServerInternalIP();
		final String hostnameServidor = ConnectionManager.getServerHostname();
		if (ipInternaServidor != null) {
			final boolean internal = ConnectionManager.checkInternalNetwork();
			if (internal && showDNSOptions) {
				final Object[] oValues = { ipInternaServidor, hostnameServidor };
				this.serverCombo = new JComboBox(oValues);
				this.serverCombo.setFont(this.serverCombo.getFont().deriveFont((float) 10));

				String connectTo = null;
				if (parameters.containsKey(ILoginDialog.CONNECT_TO)) {
					connectTo = (String) parameters.get(ILoginDialog.CONNECT_TO);
				}

				if (connectTo != null) {
					this.serverCombo.setSelectedItem(connectTo);
				} else {
					this.serverCombo.setSelectedItem(ipInternaServidor);
				}
				this.serverCombo.setToolTipText(ILoginDialog.CONNECT_TO_TOOLTIP);
				return this.serverCombo;
			}
		}
		return null;
	}

	protected ImageIcon createImage(final Map parameters) {
		ImageIcon icon = null;
		if (parameters.containsKey(ILoginDialog.LOGIN_ICON)) {
			final String loginIcon = (String) parameters.get(ILoginDialog.LOGIN_ICON);
			icon = ImageManager.getIcon(loginIcon);
		}
		if (icon == null) {
			return ImageManager.getIcon(ImageManager.BACK_LOGIN);
		} else {
			return icon;
		}
	}

	protected JLabel createStatusLabel(final Map parameters) {
		return new JLabel();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.bundle = resources;
		if (this.user != null) {
			this.user.setResourceBundle(this.bundle);
		}
		if (this.password != null) {
			this.password.setResourceBundle(this.bundle);
		}
		if (this.acceptButton != null) {
			this.acceptButton.setResourceBundle(this.bundle);
		}
		if (this.cancelButton != null) {
			this.cancelButton.setResourceBundle(this.bundle);
		}
		if (this.rememberLogin != null) {
			this.rememberLogin.setResourceBundle(this.bundle);
		}
		if (this.rememberPassword != null) {
			this.rememberPassword.setResourceBundle(this.bundle);
		}
		if (this.certificateButton != null) {
			this.certificateButton.setResourceBundle(this.bundle);
		}

		if (this.connectToLabel != null) {
			this.connectToLabel.setResourceBundle(this.bundle);
		}

	}

	private boolean installedDNIeProvider = false;

	protected void checkDNIeInstalled() {
		this.installedDNIeProvider = null != CertificateUtils.getProviderInstalled(CertificateUtils.DNIe);
	}

	public boolean isInstalledDNIeProvider() {
		return this.installedDNIeProvider;
	}

	public class CertificateListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {

			final Component comp = SwingUtilities.getWindowAncestor((Component) e.getSource());
			final Cursor c = comp.getCursor();
			try {
				comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				AbstractLoginDialog.this.certificateButton.setEnabled(false);
				AbstractLoginDialog.this.certificateButtonPressed();
			} catch (final Exception ex) {
				AbstractLoginDialog.logger.error(null, ex);
			} finally {
				AbstractLoginDialog.this.certificateButton.setEnabled(true);
				comp.setCursor(c);
			}
		}

	}

	public void certificateButtonPressed() {
		// this.user.setValue(null);
		this.password.setValue(null);

		final Cursor c = this.getCursor();

		// Check provider
		if (!this.isInstalledDNIeProvider()) {
			if ((ApplicationManager.getApplication() != null)
					&& (ApplicationManager.getApplication().getPreferences() != null)
					&& (ApplicationManager.getApplication()
							.getPreferences()
							.getPreference(null, SunPKCS11Wrapper.PREFERENCE_PKCS11_MODULE) != null)) {
				SunPKCS11Wrapper.pkcsConfigFile = ApplicationManager.getApplication()
						.getPreferences()
						.getPreference(null, SunPKCS11Wrapper.PREFERENCE_PKCS11_MODULE);
				CertificateUtils.installCertProviders();
			}
			if (!this.isInstalledDNIeProvider()) {
				// since 5.2068EN-0.4
				final boolean accepted = PKCS11Dialog.showDialog(this.getApplication().getFrame(), this.bundle);
				if (accepted) {
					this.checkDNIeInstalled();
				} else {
					if (!this.isInstalledDNIeProvider()) {
						MessageDialog.showMessage(this.getApplication().getFrame(),
								"abstractlogindialog.noproviderinstalled", JOptionPane.ERROR_MESSAGE, this.bundle);
						return;
					}
				}
			}
		}

		// get Pin
		final String pin = PasswordDialog.showPasswordDialog(this.getApplication().getFrame(), this.bundle);
		if ((pin == null) || pin.equals("")) {
			return;
		}
		// GetCertificates
		List l = null;
		try {
			l = CertificateUtils.getAliasCertPairFromKeyStore(CertificateUtils.getKeystoreInstance(), pin);

			AbstractLoginDialog.logger.debug("Certificates " + l.size());

		} catch (final Exception ex) {
			this.setCursor(Cursor.getDefaultCursor());
			MessageDialog.showMessage(this.getApplication().getFrame(), ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.bundle);
			this.setCursor(c);
			if (ApplicationManager.DEBUG) {
				AbstractLoginDialog.logger.error(null, ex);
			} else {
				AbstractLoginDialog.logger.trace(null, ex);
			}
			return;
		}

		// select Certificate
		this.setCursor(Cursor.getDefaultCursor());
		this.aliasCertPair = CertificateChooserDialog.selectCertificate(l, this.getApplication().getFrame(),
				this.bundle);

		if (this.aliasCertPair == null) {
			this.setCursor(Cursor.getDefaultCursor());
			MessageDialog.showMessage(this.getApplication().getFrame(), "abstractlogindialog.nocertificateselected",
					JOptionPane.ERROR_MESSAGE, this.bundle);
			this.setCursor(c);
			return;
		}

		// get Private Key
		PrivateKey sk = null;
		try {
			sk = (PrivateKey) CertificateUtils.getKeystoreInstance()
					.getKey(this.aliasCertPair.getAlias(), pin.toCharArray());
		} catch (final Exception ex) {
			this.setCursor(Cursor.getDefaultCursor());
			MessageDialog.showMessage(this.getApplication().getFrame(), ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.bundle);
			this.setCursor(c);
			if (ApplicationManager.DEBUG) {
				AbstractLoginDialog.logger.error(null, ex);
			} else {
				AbstractLoginDialog.logger.trace(null, ex);
			}
			return;
		}

		// get Certificate
		final Certificate cert = this.aliasCertPair.getCert();
		if (!(this.aliasCertPair.getCert() instanceof X509Certificate)) {
			this.setCursor(Cursor.getDefaultCursor());
			MessageDialog.showMessage(this.getApplication().getFrame(), "abstractlogindialog.certificateinvalidformat",
					JOptionPane.ERROR_MESSAGE, this.bundle);
			this.setCursor(c);
			return;
		}

		String token = null;
		try {
			token = ((TokenLogin) this.getEntityReferenceLocator()).getToken();
		} catch (final Exception ex) {
			this.setCursor(Cursor.getDefaultCursor());
			MessageDialog.showMessage(this.getApplication().getFrame(), ex.getMessage(), JOptionPane.ERROR_MESSAGE,
					this.bundle);
			if (ApplicationManager.DEBUG) {
				AbstractLoginDialog.logger.error(null, ex);
			} else {
				AbstractLoginDialog.logger.trace(null, ex);
			}
			this.setCursor(c);
			return;
		}

		final String signedToken = CertificateUtils.getSignedToken(token, cert, sk);
		this.fillDialogFields(this.aliasCertPair.getCert(), token, signedToken);
	}

	protected void fillDialogFields(final Certificate cert, final String token, final String signedToken) {
		String userName = null;

		if (AbstractLoginDialog.useDNIeCN) {
			if (cert instanceof X509Certificate) {
				userName = CertificateUtils.getX509CertificateSubjectDNFields((X509Certificate) cert,
						AbstractLoginDialog.userDNIeCNColumn);
			}
			if (userName != null) {
				this.user.setValue(userName);
			} else {
				this.user.setValue("NO NAME");
			}
		}

		final String pass = CertificateUtils.createTokenToSend(cert, token, signedToken);
		this.password.setValue(pass);

		this.acceptButton.doClick();
	}

	public class CancelListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			MainApplication.systemExit();
		}

	}

	public class AcceptListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			try {
				if (AbstractLoginDialog.this.serverCombo != null) {
					final String sSelected = (String) AbstractLoginDialog.this.serverCombo.getSelectedItem();
					if (sSelected.equalsIgnoreCase(ConnectionManager.getServerHostname())) {
						ConnectionManager.setAutomaticInternalNetworkDetection(false);
					} else {
						ConnectionManager.setAutomaticInternalNetworkDetection(true);
					}
				}
				if (!SwingUtilities.isEventDispatchThread()) {
					AbstractLoginDialog.logger.debug("Accept button is not eventdispatchthread");
				}
				AbstractLoginDialog.this.acceptButton.setEnabled(false);
				AbstractLoginDialog.this.status.setForeground(Color.blue.darker());
				try {
					boolean initSession = false;
					if (AbstractLoginDialog.this.getEntityReferenceLocator() != null) {
						String sConnectiongText = ILoginDialog.CONNECTING_KEY;
						try {
							if (AbstractLoginDialog.this.bundle != null) {
								sConnectiongText = AbstractLoginDialog.this.bundle
										.getString(ILoginDialog.CONNECTING_KEY);
							}
						} catch (final Exception ex) {
							if (ApplicationManager.DEBUG) {
								AbstractLoginDialog.logger.debug(null, ex);
							} else {
								AbstractLoginDialog.logger.trace(null, ex);
							}
						}
						AbstractLoginDialog.this.status.setText(sConnectiongText);
						AbstractLoginDialog.this.status.paintImmediately(0, 0,
								AbstractLoginDialog.this.status.getWidth(),
								AbstractLoginDialog.this.status.getHeight());
						initSession = AbstractLoginDialog.this.checkLogin();
					}

					if (AbstractLoginDialog.this.isLoggedIn()) {
						AbstractLoginDialog.this.savePreferences();
						AbstractLoginDialog.this.setVisible(!AbstractLoginDialog.this.isLoggedIn());
						return;
					}
					AbstractLoginDialog.this.status.setText("");
				} catch (final SecurityException e1) {
					AbstractLoginDialog.logger.trace(null, e1);
					AbstractLoginDialog.this.status.setText(ApplicationManager.getTranslation(e1.getMessage()));
				} catch (final Exception e2) {
					AbstractLoginDialog.logger.trace(null, e2);
					AbstractLoginDialog.this.status
					.setText(ApplicationManager.getTranslation(ILoginDialog.ERROR_LOGIN_KEY));

					MessageDialog.showMessage(AbstractLoginDialog.this.getApplication().getFrame(), e2.getMessage(),
							JOptionPane.ERROR_MESSAGE, AbstractLoginDialog.this.bundle);
					if (ApplicationManager.DEBUG) {
						AbstractLoginDialog.logger.error(null, e2);
					} else {
						AbstractLoginDialog.logger.trace(null, e2);
					}
				}

				AbstractLoginDialog.this.password.deleteData();
				AbstractLoginDialog.this.password.requestFocus();
				AbstractLoginDialog.this.status.setForeground(AbstractLoginDialog.this.statusBarForeground);
			} catch (final Exception ex) {
				AbstractLoginDialog.logger.trace(null, ex);
			} finally {
				AbstractLoginDialog.this.acceptButton.setEnabled(true);
			}
		}

	}

	@Override
	public String getUserValue() {
		final Object v = this.user.getValue();
		if (v == null) {
			return null;
		}
		if (this.loggedIn && (this.password.getValue() != null)
				&& this.password.getValue().toString().startsWith(CertificateUtils.USER_CERTIFICATE)) {
			try {
				final String passwordValue = this.password.getValue().toString();
				final Map h = CertificateUtils.parseTokenReceived(passwordValue);
				final Certificate cert = (Certificate) h.get(CertificateUtils.USER_CERTIFICATE);

				try {
					final String stringCertificate = CertificateUtils.encodeCertificate(cert);
					final String userValue = ((TokenLogin) this.getEntityReferenceLocator())
							.getUserFromCert(stringCertificate);
					return userValue;
				} catch (final Exception ex) {
					AbstractLoginDialog.logger.error(null, ex);
					MessageDialog.showMessage(this.getApplication().getFrame(), ex.getMessage(),
							JOptionPane.ERROR_MESSAGE, this.bundle);
				}
			} catch (final Exception ex) {
				AbstractLoginDialog.logger.error(null, ex);
				return null;
			}
		}
		return v.toString();
	}

	@Override
	public String getPasswordValue() {
		final Object obj = this.password.getValue();
		if (obj == null) {
			return null;
		}
		if (this.loggedIn && ((ClientReferenceLocator) this.locator).isAllowCertificateLogin()
				&& obj.toString().startsWith(CertificateUtils.USER_CERTIFICATE)) {
			try {
				final Map h = CertificateUtils.parseTokenReceived(obj.toString());
				final Certificate cert = (Certificate) h.get(CertificateUtils.USER_CERTIFICATE);
				try {
					final String stringCertificate = CertificateUtils.encodeCertificate(cert);
					final String passwordValue = ((TokenLogin) this.getEntityReferenceLocator())
							.getPasswordFromCert(stringCertificate);
					return passwordValue;
				} catch (final Exception ex) {
					AbstractLoginDialog.logger.error(null, ex);
					MessageDialog.showMessage(this.getApplication().getFrame(), ex.getMessage(),
							JOptionPane.ERROR_MESSAGE, this.bundle);
				}
			} catch (final Exception ex) {
				AbstractLoginDialog.logger.error(null, ex);
				return null;
			}
		}
		return obj.toString();
	}

	@Override
	public boolean isRememberLogin() {
		if (this.rememberLogin != null) {
			return this.rememberLogin.isSelected();
		}
		return false;
	}

	@Override
	public boolean isRememberPassword() {
		if (this.rememberPassword != null) {
			return this.rememberPassword.isSelected();
		}
		return false;
	}

	@Override
	public String getConnectedServer() {
		if (this.isServerSelection()) {
			return (String) this.serverCombo.getSelectedItem();
		}
		return null;
	}

	@Override
	public boolean isServerSelection() {
		if (this.serverCombo != null) {
			return true;
		}
		return false;
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

}
