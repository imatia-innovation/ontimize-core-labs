package com.ontimize.gui.login;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
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
import com.ontimize.gui.field.PasswordDataField;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;
import com.ontimize.xml.DefaultXMLParametersManager;

public class DefaultChangePasswordDialog extends AbstractChangePasswordDialog implements IChangePasswordDialog {

	private static final Logger logger = LoggerFactory.getLogger(DefaultChangePasswordDialog.class);

	protected JLabel icon = null;

	protected String changePasswordText = null;

	protected JLabel textLabel = new JLabel();

	protected String titleKey = IChangePasswordDialog.WINDOW_TITLE;

	protected JPanel securityPanel = null;

	public DefaultChangePasswordDialog(final Application mainApplication, final Map parameters,
			final EntityReferenceLocator locator, final String user, final String password) {
		super(mainApplication, parameters, locator, user, password);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		this.getContentPane().setLayout(new GridBagLayout());
		((JComponent) this.getContentPane()).registerKeyboardAction(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				DefaultChangePasswordDialog.this.acceptButton.doClick();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), JComponent.WHEN_IN_FOCUSED_WINDOW);

		this.currentPassword = this.createCurrentPassword(parameters);
		this.newPassword = this.createPassword(parameters);
		this.repeatNewPassword = this.createRepeatPassword(parameters);
		this.acceptButton = this.createAcceptButton(parameters);
		this.securityLabel = this.createSecurityLabel(parameters);
		this.securityPanel = this.createSecurityPanel(parameters);
		final ImageIcon currentIcon = this.createImage(parameters);

		if (currentIcon != null) {
			this.icon = new JLabel();
			this.icon.setIcon(currentIcon);
		}

		if (parameters.containsKey(IChangePasswordDialog.CHANGE_PASSWORD_TEXT)) {
			this.changePasswordText = (String) parameters.get(IChangePasswordDialog.CHANGE_PASSWORD_TEXT);
			if (this.changePasswordText != null) {
				this.textLabel
				.setFont(this.textLabel.getFont().deriveFont((float) (this.textLabel.getFont().getSize() + 2)));
				this.textLabel.setText(ParseUtils.getString(
						ApplicationManager.getApplicationBundle().getString(this.changePasswordText),
						this.changePasswordText));
			}
		}

		this.createLayout();
		this.installListener();
		this.pack();

	}

	protected void createLayout() {

		if (this.icon != null) {
			final GridBagConstraints gbc_icon = new GridBagConstraints();
			gbc_icon.gridx = 0;
			gbc_icon.gridy = 0;
			gbc_icon.gridwidth = 1;
			gbc_icon.gridheight = 6;
			gbc_icon.weightx = 1;
			gbc_icon.weighty = 1;
			gbc_icon.anchor = GridBagConstraints.WEST;
			gbc_icon.fill = GridBagConstraints.HORIZONTAL;
			gbc_icon.insets = new Insets(0, 0, 0, 0);
			this.getContentPane().add(this.icon, gbc_icon);
		}

		if (this.changePasswordText != null) {
			final GridBagConstraints gbc_text = new GridBagConstraints();
			gbc_text.gridx = 1;
			gbc_text.gridy = 0;
			gbc_text.gridwidth = 1;
			gbc_text.gridheight = 1;
			gbc_text.anchor = GridBagConstraints.WEST;
			gbc_text.fill = GridBagConstraints.HORIZONTAL;
			gbc_text.insets = new Insets(5, 10, 5, 5);
			this.getContentPane().add(this.textLabel, gbc_text);
		}

		final GridBagConstraints gbc_currentPassword = new GridBagConstraints();
		gbc_currentPassword.gridx = 1;
		gbc_currentPassword.gridy = 1;
		gbc_currentPassword.gridwidth = 1;
		gbc_currentPassword.gridheight = 1;
		gbc_currentPassword.weightx = 1;
		gbc_currentPassword.weighty = 1;
		gbc_currentPassword.anchor = GridBagConstraints.WEST;
		gbc_currentPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_currentPassword.insets = new Insets(0, 0, 5, 5);
		this.getContentPane().add(this.currentPassword, gbc_currentPassword);

		final GridBagConstraints gbc_newPassword = new GridBagConstraints();
		gbc_newPassword.gridx = 1;
		gbc_newPassword.gridy = 2;
		gbc_newPassword.gridwidth = 1;
		gbc_newPassword.gridheight = 1;
		gbc_newPassword.weightx = 1;
		gbc_newPassword.weighty = 1;
		gbc_newPassword.anchor = GridBagConstraints.WEST;
		gbc_newPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_newPassword.insets = new Insets(0, 0, 5, 5);
		this.getContentPane().add(this.newPassword, gbc_newPassword);

		final GridBagConstraints gbc_repeatNewPassword = new GridBagConstraints();
		gbc_repeatNewPassword.gridx = 1;
		gbc_repeatNewPassword.gridy = 3;
		gbc_repeatNewPassword.gridwidth = 1;
		gbc_repeatNewPassword.gridheight = 1;
		gbc_repeatNewPassword.weightx = 1;
		gbc_repeatNewPassword.weighty = 1;
		gbc_repeatNewPassword.anchor = GridBagConstraints.WEST;
		gbc_repeatNewPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_repeatNewPassword.insets = new Insets(0, 0, 5, 5);
		this.getContentPane().add(this.repeatNewPassword, gbc_repeatNewPassword);

		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.gridx = 1;
		gbc_acceptButton.gridy = 4;
		gbc_acceptButton.gridwidth = 1;
		gbc_acceptButton.gridheight = 1;
		gbc_acceptButton.weightx = 1;
		gbc_acceptButton.weighty = 1;
		gbc_acceptButton.anchor = GridBagConstraints.CENTER;
		gbc_repeatNewPassword.fill = GridBagConstraints.NONE;
		gbc_acceptButton.insets = new Insets(0, 0, 5, 0);
		this.acceptButton.setIcon(ApplicationManager.getDefaultOKIcon());
		this.getContentPane().add(this.acceptButton, gbc_acceptButton);

		final GridBagConstraints gbc_securityPanel = new GridBagConstraints();
		gbc_securityPanel.insets = new Insets(0, 0, 0, 0);
		gbc_securityPanel.gridx = 1;
		gbc_securityPanel.gridy = 5;
		gbc_securityPanel.weightx = 1;
		gbc_securityPanel.weighty = 1;
		gbc_securityPanel.anchor = GridBagConstraints.SOUTH;
		gbc_securityPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_securityPanel.insets = new Insets(0, 0, 0, 0);
		this.getContentPane().add(this.securityPanel, gbc_securityPanel);

		final FlowLayout lay = new FlowLayout(FlowLayout.LEFT, 0, 0);
		this.securityPanel.setLayout(lay);
		this.securityPanel.add(this.securityLabel);

	}

	protected JPanel createSecurityPanel(final Map parameters) {
		final JPanel jPanel = new JPanel();
		jPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, jPanel.getBackground(), Color.white,
				jPanel.getBackground(), Color.darkGray));
		jPanel.add(this.securityLabel);
		return jPanel;
	}

	protected void installListener() {
		this.acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String password = DefaultChangePasswordDialog.this.getPasswordValue();
				if (password != null) {
					if (DefaultChangePasswordDialog.this.oldPassword
							.equals(DefaultChangePasswordDialog.this.currentPassword.getValue())) {
						final EntityResult eResult = DefaultChangePasswordDialog.this.setPassword(password);
						if (eResult.getCode() != EntityResult.OPERATION_WRONG) {
							DefaultChangePasswordDialog.this.dispose();
						} else {
							DefaultChangePasswordDialog.logger.debug("The password cannot be modified.");
						}
					} else {
						DefaultChangePasswordDialog.this.securityLabel.setForeground(Color.RED);
						DefaultChangePasswordDialog.this.securityLabel
						.setText(ApplicationManager.getTranslation("securityLabel.current_not_match",
								DefaultChangePasswordDialog.this.bundle));
					}
				} else {
					DefaultChangePasswordDialog.this.securityLabel.setForeground(Color.RED);
					DefaultChangePasswordDialog.this.securityLabel.setText(ApplicationManager
							.getTranslation("securityLabel.not_match", DefaultChangePasswordDialog.this.bundle));
				}
			}
		});
	}

	@Override
	protected PasswordDataField createCurrentPassword(final Map parameters) {
		final Map h = DefaultXMLParametersManager.getParameters(AbstractLoginDialog.LOGIN_PASSWORD_DATA_FIELD);
		h.putAll(DefaultXMLParametersManager
				.getParameters(AbstractChangePasswordDialog.CHANGE_PASSWORD_DIALOG_CURRENT_PASSWORD));
		h.putAll(DefaultXMLParametersManager.getParameters(IChangePasswordDialog.SECURITY_PASSWORD_PARAMETERS));
		h.put("attr", "currentPassword");
		if (!h.containsKey("labelsize")) {
			h.put("labelsize", "10");
		}
		if (!h.containsKey("size")) {
			h.put("size", "12");
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
		if (!h.containsKey("labelsize")) {
			h.put("labelsize", "10");
		}
		if (!h.containsKey("size")) {
			h.put("size", "12");
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

		if (!h.containsKey("labelsize")) {
			h.put("labelsize", "10");
		}
		if (!h.containsKey("size")) {
			h.put("size", "12");
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
		securityLabel.setForeground(Color.BLUE);
		securityLabel.setPreferredSize(new Dimension(200, 14));
		return securityLabel;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// Do nothing
	}

	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		v.add(this.titleKey);
		if (this.changePasswordText != null) {
			v.add(this.changePasswordText);
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		super.setResourceBundle(resourceBundle);
		try {
			if (resourceBundle != null) {
				this.setTitle(resourceBundle.getString(this.titleKey));
			} else {
				this.setTitle(this.titleKey);
			}
		} catch (final Exception e) {
			DefaultChangePasswordDialog.logger.debug(null, e);
			this.setTitle(this.titleKey);
		}
	}

}
