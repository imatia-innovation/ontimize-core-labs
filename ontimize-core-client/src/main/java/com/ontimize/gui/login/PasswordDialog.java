package com.ontimize.gui.login;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.button.Button;

public class PasswordDialog extends JDialog implements ActionListener {

	public static final String HELP_TEXT = "passworddialog.help";

	public static final String HELP_TITLE = "passworddialog.title";

	private static PasswordDialog pf = null;

	Button okButton = null;

	Button cancelButton = null;

	JPasswordField passwordField = new JPasswordField(10);

	JLabel help = new JLabel("");

	public PasswordDialog(final Frame owner) {
		super(owner, true);

		this.okButton = this.createOKButton();
		this.cancelButton = this.createCancelButton();
		this.okButton.setIcon(ApplicationManager.getDefaultOKIcon());
		this.okButton.setResourceBundle(ApplicationManager.getApplicationBundle());
		this.cancelButton.setIcon(ApplicationManager.getDefaultCancelIcon());
		this.cancelButton.setResourceBundle(ApplicationManager.getApplicationBundle());

		this.okButton.addActionListener(this);
		this.okButton.setActionCommand("OK");
		this.passwordField.addActionListener(this);
		this.passwordField.setActionCommand("OK");
		this.passwordField.setEchoChar('*');
		this.cancelButton.addActionListener(this);
		this.cancelButton.setActionCommand("CANCEL");

		this.setLayout(new GridBagLayout());
		this.add(this.help, new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));

		this.add(this.passwordField, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 2, 2));

		this.add(this.okButton, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.EAST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));

		this.add(this.cancelButton, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST,
				GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 2, 2));
	}

	private Button createOKButton() {
		final Map p = new Hashtable();
		p.put("key", "application.accept");
		p.put("text", "application.accept");
		return new Button(p);
	}

	private Button createCancelButton() {
		final Map p = new Hashtable();
		p.put("key", "application.cancel");
		p.put("text", "application.cancel");
		return new Button(p);
	}

	protected String getPassword() {
		return new String(this.passwordField.getPassword());
	}

	@Override
	public void setVisible(final boolean b) {
		if (b) {
			this.passwordField.setText("");
		}
		super.setVisible(b);
	}

	public void setBundle(final ResourceBundle bundle) {
		this.help.setText(ApplicationManager.getTranslation(PasswordDialog.HELP_TEXT, bundle));
		this.setTitle(ApplicationManager.getTranslation(PasswordDialog.HELP_TITLE, bundle));
		this.okButton.setResourceBundle(bundle);
		this.cancelButton.setResourceBundle(bundle);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(221, 123);
	}

	public static String showPasswordDialog(final Frame owner, final ResourceBundle bundle) {
		if (PasswordDialog.pf == null) {
			PasswordDialog.pf = new PasswordDialog(owner);
		}
		PasswordDialog.pf.pack();
		ApplicationManager.center(PasswordDialog.pf);
		PasswordDialog.pf.setBundle(bundle);
		PasswordDialog.pf.setVisible(true);
		return PasswordDialog.pf.getPassword();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if ("CANCEL".equals(e.getActionCommand())) {
			this.passwordField.setText("");
		}
		this.setVisible(false);
	}

}
