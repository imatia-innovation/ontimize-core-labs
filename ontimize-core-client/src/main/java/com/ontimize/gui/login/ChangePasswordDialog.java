package com.ontimize.gui.login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.util.Locale;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.xml.DefaultXMLParametersManager;

public class ChangePasswordDialog extends AbstractChangePasswordDialog {

	private static final Logger logger = LoggerFactory.getLogger(ChangePasswordDialog.class);

	public static int topMargin = 108;

	public static int bottomMargin = 40;

	public static int leftMargin = 20;

	public static int rightMargin = 20;

	public static int labelTopMargin = 0;

	public static int labelBottomMargin = 20;

	public static int labelLeftMargin = 0;

	public static int labelRightMargin = 0;

	protected BackgroundPanel background = null;

	public static final Integer BACKGROUND_LAYER = new Integer(-60000);

	protected static class BackgroundPanel extends JPanel implements ComponentListener {

		protected JLabel label = null;

		public BackgroundPanel(final ImageIcon icon) {
			this.setOpaque(false);
			this.label = new JLabel(icon);
			this.setLayout(new BorderLayout());
			this.add(this.label, BorderLayout.CENTER);
		}

		@Override
		public void componentHidden(final ComponentEvent e) {
			// Do nothing!
		}

		@Override
		public void componentMoved(final ComponentEvent e) {
			// DO nothing!
		}

		@Override
		public void componentResized(final ComponentEvent e) {
			this.setBounds(((JComponent) e.getSource()).getBounds());
			ChangePasswordDialog.logger.debug(e.getSource() + ":" + this.getPreferredSize());
		}

		@Override
		public void componentShown(final ComponentEvent e) {
			// Do nothing!
		}

	}

	public ChangePasswordDialog(final Application mainApplication, final Map parameters, final EntityReferenceLocator locator,
			final String user, final String password) {
		super(mainApplication, parameters, locator, user, password);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		((JComponent) this.getContentPane()).registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ChangePasswordDialog.this.acceptButton.doClick();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);
		this.setUndecorated(true);
		this.setResizable(false);

		this.layoutComponents(parameters);
		this.installListener();
		this.pack();
		this.background.setBounds(this.getContentPane().getBounds());
	}

	@Override
	public Dimension getPreferredSize() {
		final Dimension backgroundDimension = this.background.getPreferredSize();
		return backgroundDimension;
	}

	protected void layoutComponents(final Map parameters) {
		final ImageIcon icon = this.createImage(parameters);
		if (icon != null) {
			this.background = new BackgroundPanel(icon);
		}

		this.currentPassword = this.createCurrentPassword(parameters);
		this.newPassword = this.createPassword(parameters);
		this.repeatNewPassword = this.createRepeatPassword(parameters);
		this.acceptButton = this.createAcceptButton(parameters);
		this.securityLabel = this.createSecurityLabel(parameters);

		this.getLayeredPane().add(this.background, ChangePasswordDialog.BACKGROUND_LAYER);
		((JComponent) this.getContentPane()).setOpaque(false);

		this.getContentPane().addComponentListener(this.background);
		this.getContentPane().setLayout(new GridBagLayout());
		final JPanel centralPanel = new JPanel();
		this.populateCentralPane(centralPanel);

		final GridBagConstraints gbc_centralPanel = new GridBagConstraints();
		gbc_centralPanel.gridx = 0;
		gbc_centralPanel.gridy = 0;
		gbc_centralPanel.gridwidth = 1;
		gbc_centralPanel.gridheight = 1;
		gbc_centralPanel.weightx = 1;
		gbc_centralPanel.weighty = 1;
		gbc_centralPanel.anchor = GridBagConstraints.CENTER;
		gbc_centralPanel.fill = GridBagConstraints.BOTH;
		gbc_centralPanel.insets = new Insets(ChangePasswordDialog.topMargin, ChangePasswordDialog.leftMargin,
				ChangePasswordDialog.bottomMargin, ChangePasswordDialog.rightMargin);
		gbc_centralPanel.ipadx = 0;
		gbc_centralPanel.ipady = 0;
		this.getContentPane().add(centralPanel, gbc_centralPanel);
	}

	protected void populateCentralPane(final JPanel centralPanel) {

		centralPanel.setOpaque(false);
		centralPanel.setLayout(new GridBagLayout());

		final GridBagConstraints gbc_securityLabel = new GridBagConstraints();
		gbc_securityLabel.gridx = 0;
		gbc_securityLabel.gridy = 0;
		gbc_securityLabel.gridwidth = 1;
		gbc_securityLabel.gridheight = 1;
		gbc_securityLabel.weightx = 1;
		gbc_securityLabel.weighty = 1;
		gbc_securityLabel.anchor = GridBagConstraints.CENTER;
		gbc_securityLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityLabel.insets = new Insets(ChangePasswordDialog.labelTopMargin, ChangePasswordDialog.labelLeftMargin,
				ChangePasswordDialog.labelBottomMargin,
				ChangePasswordDialog.labelRightMargin);
		this.securityLabel.setHorizontalAlignment(SwingConstants.CENTER);
		centralPanel.add(this.securityLabel, gbc_securityLabel);

		final GridBagConstraints gbc_currentPassword = new GridBagConstraints();
		gbc_currentPassword.gridx = 0;
		gbc_currentPassword.gridy = 1;
		gbc_currentPassword.gridwidth = 1;
		gbc_currentPassword.gridheight = 1;
		gbc_currentPassword.weightx = 1;
		gbc_currentPassword.weighty = 1;
		gbc_currentPassword.anchor = GridBagConstraints.WEST;
		gbc_currentPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentPassword.insets = new Insets(0, 0, 5, 0);
		centralPanel.add(this.currentPassword, gbc_currentPassword);

		final GridBagConstraints gbc_newPassword = new GridBagConstraints();
		gbc_newPassword.gridx = 0;
		gbc_newPassword.gridy = 2;
		gbc_newPassword.gridwidth = 1;
		gbc_newPassword.gridheight = 1;
		gbc_newPassword.weightx = 1;
		gbc_newPassword.weighty = 1;
		gbc_newPassword.anchor = GridBagConstraints.WEST;
		gbc_newPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_newPassword.insets = new Insets(0, 0, 5, 0);
		centralPanel.add(this.newPassword, gbc_newPassword);

		final GridBagConstraints gbc_repeatNewPassword = new GridBagConstraints();
		gbc_repeatNewPassword.gridx = 0;
		gbc_repeatNewPassword.gridy = 3;
		gbc_repeatNewPassword.gridwidth = 1;
		gbc_repeatNewPassword.gridheight = 1;
		gbc_repeatNewPassword.weightx = 1;
		gbc_repeatNewPassword.weighty = 1;
		gbc_repeatNewPassword.anchor = GridBagConstraints.WEST;
		gbc_repeatNewPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_repeatNewPassword.insets = new Insets(0, 0, 5, 0);
		centralPanel.add(this.repeatNewPassword, gbc_repeatNewPassword);

		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.gridx = 0;
		gbc_acceptButton.gridy = 4;
		gbc_acceptButton.gridwidth = 1;
		gbc_acceptButton.gridheight = 1;
		gbc_acceptButton.weightx = 1;
		gbc_acceptButton.weighty = 1;
		gbc_acceptButton.anchor = GridBagConstraints.CENTER;
		gbc_repeatNewPassword.fill = GridBagConstraints.NONE;
		gbc_acceptButton.insets = new Insets(0, 0, 5, 0);
		this.acceptButton.setIcon(ApplicationManager.getDefaultOKIcon());
		centralPanel.add(this.acceptButton, gbc_acceptButton);

	}

	@Override
	protected PasswordDataField createCurrentPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		h.putAll(DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CURRENT_PASSWORD));
		h.putAll(DefaultXMLParametersManager.getParameters(IChangePasswordDialog.SECURITY_PASSWORD_PARAMETERS));
		h.put("attr", "currentPassword");

		if (!h.containsKey("size")) {
			h.put("size", "12");
		}

		if (!h.containsKey("labelposition")) {
			h.put("labelposition", "top");
		}

		if (parameters.containsKey(ILoginDialog.ENCRYPT)) {
			final boolean encrypt = ApplicationManager
					.parseStringValue((String) parameters.get(IChangePasswordDialog.ENCRYPT));
			if (encrypt) {
				h.put("encrypt", "yes");
			}
		}

		if (parameters.containsKey(IChangePasswordDialog.SECURITY_BUTTON)) {
			if (parameters.get(IChangePasswordDialog.SECURITY_BUTTON).equals(true)
					&& (Integer.parseInt(parameters.get(IChangePasswordDialog.PASSWORD_LEVEL).toString()) > 0)) {
				h.put("size", Integer.parseInt((String) h.get("size")) + 2);
			}
		}

		h.remove(IChangePasswordDialog.PASSWORD_LEVEL); // Not necessary on
		// current password
		h.remove(IChangePasswordDialog.SECURITY_BUTTON); // Not necessary on
		// current password

		return new PasswordDataField(h);
	}

	@Override
	protected PasswordDataField createPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		h.putAll(DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_NEW_PASSWORD));
		h.putAll(DefaultXMLParametersManager.getParameters(IChangePasswordDialog.SECURITY_PASSWORD_PARAMETERS));
		h.put("attr", "newPassword");

		if (!h.containsKey("size")) {
			h.put("size", "12");
		}

		if (!h.containsKey("labelposition")) {
			h.put("labelposition", "top");
		}

		if (parameters.containsKey(ILoginDialog.ENCRYPT)) {
			final boolean encrypt = ApplicationManager
					.parseStringValue((String) parameters.get(IChangePasswordDialog.ENCRYPT));
			if (encrypt) {
				h.put("encrypt", "yes");
			}
		}

		if (parameters.containsKey("securitylevel")) {
			h.put(IChangePasswordDialog.PASSWORD_LEVEL, parameters.get(IChangePasswordDialog.PASSWORD_LEVEL));
			h.put(IChangePasswordDialog.SECURITY_BUTTON, parameters.get(IChangePasswordDialog.SECURITY_BUTTON));
		}

		return new PasswordDataField(h);
	}

	@Override
	protected PasswordDataField createRepeatPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		h.putAll(DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_REPEAT_NEW_PASSWORD));
		h.putAll(DefaultXMLParametersManager.getParameters(IChangePasswordDialog.SECURITY_PASSWORD_PARAMETERS));
		h.put("attr", "repeatNewPassword");

		if (!h.containsKey("size")) {
			h.put("size", "12");
		}

		if (!h.containsKey("labelposition")) {
			h.put("labelposition", "top");
		}

		if (parameters.containsKey(ILoginDialog.ENCRYPT)) {
			final boolean encrypt = ApplicationManager
					.parseStringValue((String) parameters.get(IChangePasswordDialog.ENCRYPT));
			if (encrypt) {
				h.put("encrypt", "yes");
			}
		}

		if (parameters.containsKey("securitylevel")) {
			h.put(IChangePasswordDialog.PASSWORD_LEVEL, parameters.get(IChangePasswordDialog.PASSWORD_LEVEL));
			h.put(IChangePasswordDialog.SECURITY_BUTTON, parameters.get(IChangePasswordDialog.SECURITY_BUTTON));
		}

		return new PasswordDataField(h);
	}

	@Override
	protected JLabel createSecurityLabel(final Map parameters) {
		final JLabel securityLabel = super.createSecurityLabel(parameters);
		securityLabel.setForeground(Color.BLUE.darker());
		securityLabel.setPreferredSize(new Dimension(200, 14));
		return securityLabel;
	}

	protected void installListener() {
		this.acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String password = ChangePasswordDialog.this.getPasswordValue();
				if (password != null) {
					if (ChangePasswordDialog.this.oldPassword
							.equals(ChangePasswordDialog.this.currentPassword.getValue())) {
						final EntityResult eResult = ChangePasswordDialog.this.setPassword(password);
						if (eResult.getCode() != EntityResult.OPERATION_WRONG) {
							ChangePasswordDialog.this.dispose();
						} else {
							ChangePasswordDialog.logger.debug("The password cannot be modified.");
						}
					} else {
						ChangePasswordDialog.this.securityLabel.setForeground(Color.RED.darker());
						ChangePasswordDialog.this.securityLabel.setText(ApplicationManager
								.getTranslation("securityLabel.current_not_match", ChangePasswordDialog.this.bundle));
					}
				} else {
					ChangePasswordDialog.this.securityLabel.setForeground(Color.RED.darker());
					ChangePasswordDialog.this.securityLabel.setText(ApplicationManager
							.getTranslation("securityLabel.not_match", ChangePasswordDialog.this.bundle));
				}
			}
		});
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// Do nothing!
	}

}
