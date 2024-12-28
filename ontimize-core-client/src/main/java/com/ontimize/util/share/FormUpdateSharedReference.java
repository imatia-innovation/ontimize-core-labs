package com.ontimize.util.share;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.MemoDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.share.IShareRemoteReference;
import com.ontimize.jee.common.util.share.SharedElement;

public class FormUpdateSharedReference extends EJDialog implements Internationalization {

	protected EntityReferenceLocator locator;

	protected ResourceBundle resourceBundle;

	protected TextDataField nameDataField;

	protected TextDataField contentShareDataField;

	protected MemoDataField memoDataField;

	protected IShareRemoteReference remoteReference;

	protected Window owner;

	protected String defaultMessage;

	protected Object contentShare;

	protected String name;

	protected boolean updateShare = false;

	/**
	 * Create the window to update a shared element
	 * @param owner -> Parent window element of the form
	 * @param modal -> To specify if the dialog will be opened in a model form, blocking the other parts
	 *        of application
	 * @param locator -> Locator, needed to obtain the remote references to the sharing systems
	 * @param p -> Point where the dialog opens (top-left corner)
	 * @param share -> Share element to update
	 * @throws Exception
	 */
	public FormUpdateSharedReference(final Window owner, final boolean modal, final EntityReferenceLocator locator, final Point p,
			final SharedElement share) throws Exception {
		super(owner, ApplicationManager.getTranslation("M_ADD_CONTENT_SHARE_TITLE"), modal);
		try {
			this.remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, locator.getSessionId());
			this.setResourceBundle(ApplicationManager.getApplicationBundle());
			this.locator = locator;
			this.contentShare = share.getContentShare();
			this.owner = owner;
			this.defaultMessage = share.getMessage();
			this.name = share.getName();
			this.createAndConfigurePanelComponents();
			this.pack();
			this.setLocation(p);
			this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			this.setVisible(true);
		} catch (final Exception ex) {
			MessageDialog.showErrorMessage(owner,
					ApplicationManager.getTranslation("M_SHARE_REMOTE_REFERENCE_NOT_EXIST"));
			throw ex;
		}
	}

	/**
	 * Create and configure the panel
	 * @throws Exception
	 */
	protected void createAndConfigurePanelComponents() throws Exception {
		this.memoDataField = this.createAndConfigureMessage();
		this.nameDataField = this.createAndConfigureName();
		this.contentShareDataField = this.createAndConfigureContentShare();
		final Button acceptButton = this.createAndConfigureButtonAccept();
		final Button cancelButton = this.createAndConfigureButtonCancel();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		// Name
		final GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(0, 0, 5, 0);
		gbc_name.gridwidth = 2;
		gbc_name.fill = GridBagConstraints.BOTH;
		gbc_name.gridx = 0;
		gbc_name.gridy = 0;
		this.getContentPane().add(this.nameDataField, gbc_name);

		// Content
		final GridBagConstraints gbc_content = new GridBagConstraints();
		gbc_content.insets = new Insets(0, 0, 5, 0);
		gbc_content.gridwidth = 2;
		gbc_content.fill = GridBagConstraints.BOTH;
		gbc_content.gridx = 0;
		gbc_content.gridy = 1;
		this.getContentPane().add(this.contentShareDataField, gbc_content);

		// Message
		final GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(0, 0, 5, 0);
		gbc_message.gridwidth = 2;
		gbc_message.fill = GridBagConstraints.BOTH;
		gbc_message.gridx = 0;
		gbc_message.gridy = 2;
		this.getContentPane().add(this.memoDataField, gbc_message);

		// Accept button
		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.anchor = GridBagConstraints.EAST;
		gbc_acceptButton.insets = new Insets(0, 0, 0, 5);
		gbc_acceptButton.gridx = 0;
		gbc_acceptButton.gridy = 3;
		acceptButton.setHorizontalAlignment(SwingConstants.LEFT);
		this.getContentPane().add(acceptButton, gbc_acceptButton);

		// Cancel button
		final GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.WEST;
		gbc_cancelButton.gridx = 1;
		gbc_cancelButton.gridy = 3;
		cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
		this.getContentPane().add(cancelButton, gbc_cancelButton);
	}

	/**
	 * Create and return the TextDataField that has the content to share. This element is not visible
	 * @return Return the TextDataField that has the content
	 */
	protected TextDataField createAndConfigureContentShare() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, "contentShare");
		h.put(DataField.REQUIRED, "yes");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation("shareRemote.contentShare"));
		h.put(DataField.DIM, "text");
		h.put(DataField.SIZE, 25);
		final TextDataField textDataField = new TextDataField(h);
		textDataField.setValue(this.contentShare);
		textDataField.setVisible(false);
		return textDataField;
	}

	/**
	 * Create and return the TextDataField which has the shared element name.
	 * @return The text field which has the shared element name
	 */
	protected TextDataField createAndConfigureName() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, "name");
		h.put(DataField.REQUIRED, "yes");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation("shareRemote.name"));
		h.put(DataField.DIM, "text");
		h.put(DataField.SIZE, 25);
		final TextDataField textDataField = new TextDataField(h);
		textDataField.setValue(this.name);
		return textDataField;
	}

	/**
	 * Create and return the cancel button. This button is used to ensure that the variable
	 * <code>updateShare</code> to false.
	 * @return The cancel button
	 */
	protected Button createAndConfigureButtonCancel() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "cancelButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.cancel"));
		final Button cancelButton = new Button(h);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormUpdateSharedReference.this.updateShare = false;
				FormUpdateSharedReference.this.dispose();
			}
		});

		return cancelButton;

	}

	/**
	 * Create and return the accept button. This button must to ensure that the variable
	 * <code>updateShare</code> is set to <code>true</code>, and save the name, content and message of
	 * the form into the form variables <code>name</code>, <code>defaultMessage</code> and
	 * <code>contentShare</code>
	 * @return The accept button
	 */
	protected Button createAndConfigureButtonAccept() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "acceptButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.accept"));
		final Button acceptButton = new Button(h);

		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!FormUpdateSharedReference.this.nameDataField.isEmpty()
						&& !FormUpdateSharedReference.this.contentShareDataField.isEmpty()
						&& !FormUpdateSharedReference.this.nameDataField.getText().trim().isEmpty()) {
					FormUpdateSharedReference.this.updateShare = true;
					FormUpdateSharedReference.this.name = FormUpdateSharedReference.this.nameDataField.getText().trim();
					FormUpdateSharedReference.this.contentShare = FormUpdateSharedReference.this.contentShareDataField
							.getValue();
					FormUpdateSharedReference.this.defaultMessage = FormUpdateSharedReference.this.memoDataField
							.getText();
					FormUpdateSharedReference.this.dispose();
				} else {
					MessageDialog.showErrorMessage(FormUpdateSharedReference.this, "shareRemote.name_cannot_empty");
				}
			}
		});

		return acceptButton;
	}

	/**
	 * Create and configure the message component
	 * @return The default message component
	 */
	protected MemoDataField createAndConfigureMessage() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IShareRemoteReference.SHARE_MESSAGE_STRING);
		h.put(DataField.REQUIRED, "no");
		h.put(DataField.EXPAND, "yes");
		h.put(DataField.DIM, "text");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation("OptionPane.messageDialogTitle"));
		h.put("rows", "8");

		final MemoDataField messageMemo = new MemoDataField(h);
		messageMemo.setValue(this.defaultMessage);
		return messageMemo;

	}

	@Override
	public void setComponentLocale(final Locale l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;

	}

	@Override
	public List getTextsToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/**
	 * Return the name of the shared element
	 * @return The name of the shared element
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the content of the shared element
	 * @return The content of the shared element
	 */
	public Object getContentShare() {
		return this.contentShare;
	}

	/**
	 * Return the message of the shared element
	 * @return The message of the shared element
	 */
	public Object getMessage() {
		return this.defaultMessage;
	}

	/**
	 * @return The status of the update, <code>true</code> if the component is ready to update (accept
	 *         button), or <code>false</code> otherwise
	 */
	public boolean getUpdateStatus() {
		return this.updateShare;
	}

}
