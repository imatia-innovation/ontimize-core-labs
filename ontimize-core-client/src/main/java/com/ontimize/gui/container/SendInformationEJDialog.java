package com.ontimize.gui.container;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.field.HTMLDataField;
import com.ontimize.util.ParseUtils;

public class SendInformationEJDialog extends EJDialog {

	protected static Map sendHTMLDataFieldParams = null;

	protected HTMLDataField messageToSend = null;

	protected JLabel sendInformationLabel = new JLabel();

	protected JButton acceptButton = new JButton();

	protected JButton cancelButton = new JButton();

	protected ActionListener cancelButtonListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			SendInformationEJDialog.this.dispose();
		}
	};

	protected ActionListener acceptButtonListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			SendInformationEJDialog.this.setVisible(false);
		}
	};

	public SendInformationEJDialog(final Dialog parentComponent, final String title, final boolean modal, final ResourceBundle bundle)
			throws Exception {
		super(parentComponent, title, modal);
		this.createSendInformationDialog(parentComponent, bundle);
	}

	public SendInformationEJDialog(final Frame parentComponent, final String title, final boolean modal, final ResourceBundle bundle)
			throws Exception {
		super(parentComponent, title, modal);
		this.createSendInformationDialog(parentComponent, bundle);
	}

	public SendInformationEJDialog(final Window parentComponent, final String title, final boolean modal, final ResourceBundle bundle)
			throws Exception {
		super(parentComponent, title, modal);
		this.createSendInformationDialog(parentComponent, bundle);
	}

	public void createSendInformationDialog(final Component parentComponent, final ResourceBundle bundle) throws Exception {
		this.initParams();

		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		final GridBagConstraints gbc_sendInformationLabel = new GridBagConstraints();
		gbc_sendInformationLabel.gridx = 0;
		gbc_sendInformationLabel.gridy = 0;
		gbc_sendInformationLabel.gridwidth = 3;
		gbc_sendInformationLabel.insets = new Insets(5, 5, 5, 5);
		this.getContentPane().add(this.sendInformationLabel, gbc_sendInformationLabel);

		final GridBagConstraints gbc_messageToSend = new GridBagConstraints();
		gbc_messageToSend.gridx = 0;
		gbc_messageToSend.gridy = 1;
		gbc_messageToSend.weightx = 1.0;
		gbc_messageToSend.weighty = 1.0;
		gbc_messageToSend.fill = GridBagConstraints.BOTH;
		gbc_messageToSend.gridwidth = 3;
		gbc_messageToSend.insets = new Insets(0, 5, 5, 5);
		this.getContentPane().add(this.messageToSend, gbc_messageToSend);

		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.gridx = 1;
		gbc_acceptButton.gridy = 2;
		gbc_acceptButton.insets = new Insets(0, 5, 5, 5);
		this.getContentPane().add(this.acceptButton, gbc_acceptButton);

		final GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.gridx = 2;
		gbc_cancelButton.gridy = 2;
		gbc_cancelButton.insets = new Insets(0, 0, 5, 5);
		this.getContentPane().add(this.cancelButton, gbc_cancelButton);

		this.sendInformationLabel
		.setText(ParseUtils.getString(bundle.getString("servermonitor.input_massive_message_label"),
				"servermonitor.input_massive_message_label"));
		this.acceptButton.setText(ParseUtils
				.getString(ApplicationManager.getApplicationBundle().getString("application.accept"), "Accept"));
		this.cancelButton.setText(ParseUtils
				.getString(ApplicationManager.getApplicationBundle().getString("application.cancel"), "Cancel"));
		this.acceptButton.addActionListener(this.acceptButtonListener);
		this.cancelButton.addActionListener(this.cancelButtonListener);

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.pack();
		this.centerLocationOnParent(parentComponent);
	}

	protected void initParams() throws Exception {
		if (SendInformationEJDialog.sendHTMLDataFieldParams == null) {
			SendInformationEJDialog.sendHTMLDataFieldParams = new Hashtable();
			SendInformationEJDialog.sendHTMLDataFieldParams.put("attr", "htmlmessage");
			SendInformationEJDialog.sendHTMLDataFieldParams.put("labelvisible", "no");
			SendInformationEJDialog.sendHTMLDataFieldParams.put("expand", "yes");
			SendInformationEJDialog.sendHTMLDataFieldParams.put("rows", "10");
			SendInformationEJDialog.sendHTMLDataFieldParams.put("dim", "text");
		} else if (!(SendInformationEJDialog.sendHTMLDataFieldParams instanceof Map)) {
			throw new Exception("sendHTMLDataFieldParams is not a HashTable");
		}
		this.messageToSend = new HTMLDataField(SendInformationEJDialog.sendHTMLDataFieldParams);
	}

	public void centerLocationOnParent(final Component parentComponent) {
		this.setLocationRelativeTo(parentComponent);
		final Point p = this.getLocation();
		p.x = p.x - (this.getWidth() / 2);
		p.y = p.y - (this.getHeight() / 2);
	}

	public String getMessage() {
		return this.messageToSend.getValue().toString();
	}

	@Override
	public void dispose() {
		super.dispose();
		this.messageToSend.setValue(null);
	}

}
