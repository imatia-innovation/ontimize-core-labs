package com.ontimize.gui.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;

/**
 * Class that implements the LoginDialog. It allows the insertion of a login and a password and the
 * selection of some client preferences just like to remember the password and the login. The class
 * starts the login procedure by calling the {@link EntityReferenceLocator#startSession} method.
 */
public class DefaultLoginDialog extends AbstractLoginDialog implements ILoginDialog {

	private static final Logger logger = LoggerFactory.getLogger(DefaultLoginDialog.class);

	protected String loginText = null;

	String titleKey = "mainapplication.access_control";

	String ConnectingKey = "mainapplication.connecting";

	String hideOptionsKey = "Opciones<<<";

	String showOptionsKey = "Opciones>>>";

	protected JLabel icon = new JLabel();

	protected JLabel text = new JLabel();

	protected JPanel statusPanel = new JPanel();

	protected Button avancedButton = null;

	protected JPanel comboPanel = null;

	/**
	 * Creates the login dialog, creating the Swing elements that are displayed and the listeners to
	 * those objects.
	 */
	public DefaultLoginDialog(final Application main, final Map parameters, final EntityReferenceLocator locator) {
		super(main, parameters, locator);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.getContentPane().setLayout(new GridBagLayout());
		((JComponent) this.getContentPane()).registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DefaultLoginDialog.this.acceptButton.doClick();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.user = this.createLogin(parameters);

		this.password = this.createPassword(parameters);

		this.acceptButton = this.createAcceptButton(parameters);

		this.cancelButton = this.createCancelButton(parameters);

		this.certificateButton = this.createCertificatesButton(parameters);

		if (parameters.containsKey(ILoginDialog.LOGIN_ICON)) {
			final ImageIcon currentIcon = this.createImage(parameters);
			if (currentIcon != null) {
				this.icon.setIcon(currentIcon);
			}
			this.getContentPane()
			.add(this.icon, new GridBagConstraints(0, 0, 1, 8, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}

		if (parameters.containsKey(ILoginDialog.LOGIN_TEXT)) {
			this.loginText = (String) parameters.get(ILoginDialog.LOGIN_TEXT);
			if (this.loginText != null) {
				this.text.setFont(this.text.getFont().deriveFont((float) (this.text.getFont().getSize() + 2)));
				this.text.setText(this.loginText);
				this.getContentPane()
				.add(this.text,
						new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
								GridBagConstraints.HORIZONTAL, new Insets(5, 5, 3, 5), 0, 0));
			}
		}

		this.rememberLogin = this.createRememberLogin(parameters);
		this.rememberPassword = this.createRememberPassword(parameters);

		this.rememberLogin.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(final ValueEvent e) {
				if (DefaultLoginDialog.this.rememberLogin.isSelected()) {
					DefaultLoginDialog.this.rememberPassword.setEnabled(true);
				} else {
					DefaultLoginDialog.this.rememberPassword.setValue(Boolean.FALSE);
					DefaultLoginDialog.this.rememberPassword.setEnabled(false);
				}
			}
		});

		final ApplicationPreferences preferences = this.getApplication().getPreferences();

		if (preferences == null) {
			this.rememberLogin.setVisible(false);
			this.rememberPassword.setVisible(false);
		}

		this.init(parameters);

		this.getContentPane()
		.add(this.user, new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));
		this.getContentPane()
		.add(this.password, new GridBagConstraints(1, 2, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0, 5), 0, 0));

		final JPanel jpButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JPanel panelGrid = new JPanel(new GridLayout(1, 0, 5, 1));
		jpButtonsPanel.add(panelGrid);
		panelGrid.add(this.acceptButton);
		if (((ClientReferenceLocator) locator).isAllowCertificateLogin() && this.isInstalledDNIeProvider()) {
			panelGrid.add(this.certificateButton);
		}
		panelGrid.add(this.cancelButton);
		this.status = this.createStatusLabel(parameters);
		this.status.setPreferredSize(new Dimension(150, 14));
		final FlowLayout lay = new FlowLayout(FlowLayout.LEFT, 2, 0);
		this.statusPanel.setLayout(lay);
		this.status.setForeground(Color.blue.darker());
		this.statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, this.statusPanel.getBackground(), Color.white,
				this.statusPanel.getBackground(), Color.darkGray));
		this.statusPanel.add(this.status);

		this.acceptButton.setIcon(ApplicationManager.getDefaultOKIcon());
		this.cancelButton.setIcon(ApplicationManager.getDefaultCancelIcon());
		this.certificateButton.setIcon(ImageManager.getIcon(ImageManager.CERTIFICATE_ICON));

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
				this.comboPanel = new JPanel(new GridBagLayout());
				final Map pet = new Hashtable();
				pet.put("attr", "et");
				pet.put("text", ILoginDialog.CONNECT_TO_KEY);
				pet.put("fontsize", "10");
				this.connectToLabel = new Label(pet);
				this.comboPanel.add(this.connectToLabel,
						new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
								new Insets(0, 0, 0, 10), 0, 0));
				this.comboPanel.add(this.serverCombo,
						new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
								new Insets(0, 0, 0, 0), 0, 0));

				this.getContentPane()
				.add(this.comboPanel,
						new GridBagConstraints(1, 6, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST,
								GridBagConstraints.HORIZONTAL, new Insets(3, 5, 0, 5), 0, 0));
			}
		}

		this.getContentPane()
		.add(jpButtonsPanel, new GridBagConstraints(1, 3, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane()
		.add(this.rememberLogin,
				new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
						new Insets(3, 5, 0, 5), 0, 0));
		this.getContentPane()
		.add(this.rememberPassword,
				new GridBagConstraints(1, 5, 1, 1, 1, 0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE,
						new Insets(3, 5, 0, 5), 0, 0));
		this.getContentPane()
		.add(this.statusPanel,
				new GridBagConstraints(1, 7, 1, 1, 1, 0, GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));

		this.acceptListener = new AcceptListener();
		this.acceptButton.addActionListener(this.acceptListener);
		this.cancelListener = new CancelListener();
		this.cancelButton.addActionListener(this.cancelListener);
		this.certificateListener = new CertificateListener();
		this.certificateButton.addActionListener(this.certificateListener);
	}

	@Override
	protected TextDataField createLogin(final Map parameters) {
		if (!((ClientReferenceLocator) locator).isAllowCertificateLogin() || !this.isInstalledDNIeProvider()) {
			return super.createLogin(parameters);
		}
		final Map p = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_TEXT_DATA_FIELD);
		p.put("attr", "User_");
		p.put("size", "24");
		p.put("labelsize", "8");
		return new TextDataField(p);
	}

	@Override
	protected PasswordDataField createPassword(final Map parameters) {
		if (!((ClientReferenceLocator) locator).isAllowCertificateLogin() || !this.isInstalledDNIeProvider()) {
			return super.createPassword(parameters);
		}
		final Map p2 = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		p2.put("attr", "Password");
		p2.put("size", "24");
		p2.put("labelsize", "8");
		if (parameters.containsKey(ILoginDialog.ENCRYPT)) {
			final boolean encrypt = ApplicationManager.parseStringValue((String) parameters.get(ILoginDialog.ENCRYPT));
			if (encrypt) {
				p2.put("encrypt", "yes");
			}
		}
		return new PasswordDataField(p2);
	}

	/**
	 * Hides the combo used to select the network to connect to.
	 */
	protected void hideCombo() {
		if (this.comboPanel != null) {
			this.avancedButton.setText(this.showOptionsKey);
			this.comboPanel.setVisible(false);
		}
	}

	/**
	 * Shows the combo used to select the network to connect to.
	 */
	protected void showCombo() {
		if (this.comboPanel != null) {
			this.avancedButton.setText(this.hideOptionsKey);
			this.comboPanel.setVisible(true);
		}
	}

	/**
	 * Hides or shows the login net selection combo.
	 */
	public void changeComboVisibility() {
		if (this.comboPanel != null) {
			final boolean vis = this.comboPanel.isVisible();
			if (vis) {
				this.hideCombo();
			} else {
				this.showCombo();
			}
			this.pack();
		}
	}

	/**
	 * Shows or hides the login window.
	 */
	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			this.pack();
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		}
		super.setVisible(visible);
	}

	/**
	 * Sets the focus when the window is open.
	 */
	@Override
	protected void processWindowEvent(final WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_OPENED) {
			if (!this.user.isEmpty()) {
				this.password.requestFocus();
			} else {
				this.user.requestFocus();
			}
		}
	}

	/**
	 * Returns the login texts to be translated to the user language.
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.addAll(this.user.getTextsToTranslate());
		v.addAll(this.password.getTextsToTranslate());
		v.addAll(this.acceptButton.getTextsToTranslate());
		v.addAll(this.cancelButton.getTextsToTranslate());
		v.add(this.titleKey);
		if (this.loginText != null) {
			v.add(this.loginText);
		}
		return v;
	}

	/**
	 * Sets the login window language.
	 */
	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		if (this.avancedButton != null) {
			this.avancedButton.setResourceBundle(this.bundle);
		}

		try {
			if (resources != null) {
				this.setTitle(resources.getString(this.titleKey));
			} else {
				this.setTitle(this.titleKey);
			}
		} catch (final Exception e) {
			this.setTitle(this.titleKey);
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				DefaultLoginDialog.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
			}
		}
		if (this.serverCombo != null) {
			try {
				if (resources != null) {
					this.serverCombo.setToolTipText(resources.getString(ILoginDialog.CONNECT_TO_TOOLTIP));
				} else {
					this.serverCombo.setToolTipText(ILoginDialog.CONNECT_TO_TOOLTIP);
				}
			} catch (final Exception e) {
				this.serverCombo.setToolTipText("Select a server");
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					DefaultLoginDialog.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		}

		if (this.loginText != null) {
			try {
				if (resources != null) {
					this.text.setText(resources.getString(this.loginText));
				} else {
					this.text.setText(this.loginText);
				}
			} catch (final Exception e) {
				this.text.setText(this.loginText);
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					DefaultLoginDialog.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Unused.
	 */
	@Override
	public void setComponentLocale(final Locale l) {
	}

}
