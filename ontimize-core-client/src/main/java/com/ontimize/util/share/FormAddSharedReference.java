package com.ontimize.util.share;

import java.awt.Component;
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
import java.util.Vector;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.ListDataField;
import com.ontimize.gui.field.MemoDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.share.IShareRemoteReference;
import com.ontimize.jee.common.util.share.SharedElement;

/**
 * This form allows to convert any content into a shared element, allowing to add receiver users,
 * add a custom message or custom name to the element
 *
 * @author Imatia Innovation
 *
 */
public class FormAddSharedReference extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(FormAddSharedReference.class);

	protected EntityReferenceLocator locator;

	protected Object contentShare;

	protected String componentShareKey;

	protected ResourceBundle resourceBundle;

	protected ListDataField listDataField;

	protected MemoDataField memoDataField;

	protected TextDataField nameDataField;

	protected IShareRemoteReference remoteReference;

	protected String sourceUser;

	protected Window owner;

	protected String defaultMessage;

	protected String name;

	protected boolean modifyName = true;

	private boolean buttonOpt = false;

	/**
	 * Action listener prepared to open a pop-up to select the receiver users of the shared element
	 *
	 * @author Imatia Innovation
	 *
	 */
	public class AddUserTargetActionListener implements ActionListener {

		public AddUserTargetActionListener() {
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Point p = ((Component) e.getSource()).getLocationOnScreen();
			final FormAddUserSharedReference sharedReference = new FormAddUserSharedReference(
					SwingUtilities.getWindowAncestor((Button) e.getSource()), true,
					FormAddSharedReference.this.locator,
					FormAddSharedReference.this.listDataField);
			sharedReference.setLocation(p);
			sharedReference.setVisible(true);
		}

	}

	/**
	 * Constructor of form in a dialog with default name and allow edition name
	 * @param owner -> Parent window element of the form
	 * @param modal -> To specify if the dialog will be opened in a model form, blocking the other parts
	 *        of application
	 * @param locator -> Locator, needed to obtain the remote references to the sharing systems
	 * @param content -> Content to share using the sharing system
	 * @param componentKey -> Key to identify type of element
	 * @param sourceUser -> Name of the user who shares the element
	 * @param defaultMessage -> A default message to add at form
	 * @param name -> Default name of the shared element
	 * @param modifyname -> Allow or deny modification of name of the element
	 * @param p -> Point where the dialog opens (top-left corner)
	 * @throws Exception
	 */
	public FormAddSharedReference(final Window owner, final boolean modal, final EntityReferenceLocator locator, final Object content,
			final String componentKey, final String sourceUser, final String defaultMessage,
			final String name, final boolean modifyname, final Point p) throws Exception {
		super(owner, ApplicationManager.getTranslation("M_ADD_CONTENT_SHARE_TITLE"), modal);
		try {
			this.remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, locator.getSessionId());
			this.setResourceBundle(ApplicationManager.getApplicationBundle());
			this.locator = locator;
			this.contentShare = content;
			this.componentShareKey = componentKey;
			this.sourceUser = sourceUser;
			this.owner = owner;
			this.defaultMessage = defaultMessage;
			this.name = name;
			this.modifyName = modifyname;
			this.createAndConfigurePanelComponents();
			this.pack();
			this.setLocation(p);
			this.setVisible(true);
		} catch (final Exception ex) {
			MessageDialog.showErrorMessage(owner,
					ApplicationManager.getTranslation("M_SHARE_REMOTE_REFERENCE_NOT_EXIST"));
			throw ex;
		}
	}

	/**
	 * Constructor of form in a window
	 * @param owner -> Parent window element of the form
	 * @param modal -> To specify if the dialog will be opened in a model form, blocking the other parts
	 *        of application
	 * @param locator -> Locator, needer to obtain the remote references to the sharing system
	 * @param content -> Content to share using the sharing system
	 * @param componentKey -> Key to identify type of the element
	 * @param sourceUser -> Name of the user who shares the element
	 * @param defaultMessage -> Default message of the shared element
	 * @param p -> Point where the dialog opens (top-left corner)
	 * @throws Exception
	 */
	public FormAddSharedReference(final Window owner, final boolean modal, final EntityReferenceLocator locator, final Object content,
			final String componentKey, final String sourceUser, final String defaultMessage,
			final Point p) throws Exception {
		super(owner, ApplicationManager.getTranslation("M_ADD_CONTENT_SHARE_TITLE"), modal);
		try {
			this.remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, locator.getSessionId());
			this.setResourceBundle(ApplicationManager.getApplicationBundle());
			this.locator = locator;
			this.contentShare = content;
			this.componentShareKey = componentKey;
			this.sourceUser = sourceUser;
			this.owner = owner;
			this.defaultMessage = defaultMessage;
			this.createAndConfigurePanelComponents();
			this.pack();
			this.setLocation(p);
			this.setVisible(true);
		} catch (final Exception ex) {
			MessageDialog.showErrorMessage(owner,
					ApplicationManager.getTranslation("M_SHARE_REMOTE_REFERENCE_NOT_EXIST"));
			throw ex;
		}
	}

	/**
	 * Create and configure the content panel
	 * @throws Exception
	 */
	protected void createAndConfigurePanelComponents() throws Exception {
		this.listDataField = this.createAndConfigureTargetUser();
		this.memoDataField = this.createAndConfigureMessage();
		this.nameDataField = this.createAndConfigureName();
		final Button acceptButton = this.createAndConfigureButtonAccept();
		final Button cancelButton = this.createAndConfigureButtonCancel();
		final Button addUserTargetButton = this.createAndConfigureButtonAddUserTarget();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.5, 0, 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		// User add button
		final GridBagConstraints gbc_userAddButton = new GridBagConstraints();
		gbc_userAddButton.anchor = GridBagConstraints.EAST;
		gbc_userAddButton.insets = new Insets(0, 0, 5, 0);
		gbc_userAddButton.gridx = 1;
		gbc_userAddButton.gridy = 0;
		addUserTargetButton.setHorizontalAlignment(SwingConstants.RIGHT);
		this.getContentPane().add(addUserTargetButton, gbc_userAddButton);

		// User list
		final GridBagConstraints gbc_userList = new GridBagConstraints();
		gbc_userList.insets = new Insets(0, 0, 5, 0);
		gbc_userList.gridwidth = 2;
		gbc_userList.fill = GridBagConstraints.BOTH;
		gbc_userList.gridx = 0;
		gbc_userList.gridy = 1;
		this.getContentPane().add(this.listDataField, gbc_userList);

		// Name
		final GridBagConstraints gbc_name = new GridBagConstraints();
		gbc_name.insets = new Insets(0, 0, 5, 0);
		gbc_name.gridwidth = 2;
		gbc_name.fill = GridBagConstraints.BOTH;
		gbc_name.gridx = 0;
		gbc_name.gridy = 2;
		this.getContentPane().add(this.nameDataField, gbc_name);

		// Message
		final GridBagConstraints gbc_message = new GridBagConstraints();
		gbc_message.insets = new Insets(0, 0, 5, 0);
		gbc_message.gridwidth = 2;
		gbc_message.fill = GridBagConstraints.BOTH;
		gbc_message.gridx = 0;
		gbc_message.gridy = 3;
		this.getContentPane().add(this.memoDataField, gbc_message);

		// Accept button
		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.anchor = GridBagConstraints.EAST;
		gbc_acceptButton.insets = new Insets(0, 0, 0, 5);
		gbc_acceptButton.gridx = 0;
		gbc_acceptButton.gridy = 4;
		acceptButton.setHorizontalAlignment(SwingConstants.LEFT);
		this.getContentPane().add(acceptButton, gbc_acceptButton);

		// Cancel button
		final GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.WEST;
		gbc_cancelButton.gridx = 1;
		gbc_cancelButton.gridy = 4;
		cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
		this.getContentPane().add(cancelButton, gbc_cancelButton);
	}

	/**
	 * Create and configure a button, associated with the action listener
	 * {@link AddUserTargetActionListener}, to add receivers of shared elements
	 * @return Add receiver button
	 */
	protected Button createAndConfigureButtonAddUserTarget() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "addUserTarget");
		h.put(Button.TEXT, "+");
		final Button addUserTarget = new Button(h);
		addUserTarget.addActionListener(new AddUserTargetActionListener());
		return addUserTarget;
	}

	/**
	 * Create and configure cancel button
	 * @return Cancel button
	 */
	protected Button createAndConfigureButtonCancel() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "cancelButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.cancel"));
		final Button cancelButton = new Button(h);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormAddSharedReference.this.dispose();
			}
		});

		return cancelButton;

	}

	/**
	 * Create and configure accept button
	 * @return
	 */
	protected Button createAndConfigureButtonAccept() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "acceptButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.accept"));
		final Button acceptButton = new Button(h);
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (FormAddSharedReference.this.checkFormShareData()) {
					// ADD SHARE DATA
					final Vector<String> v = new Vector<String>();
					for (final Object oActual : (List) FormAddSharedReference.this.listDataField.getValue()) {
						v.add(oActual.toString());
					}

					try {
						final EntityResult eR = FormAddSharedReference.this.remoteReference.addSharedItem(
								new SharedElement(FormAddSharedReference.this.memoDataField.getText(),
										FormAddSharedReference.this.componentShareKey,
										FormAddSharedReference.this.contentShare,
										FormAddSharedReference.this.sourceUser,
										FormAddSharedReference.this.nameDataField.getText()),
								v, FormAddSharedReference.this.locator.getSessionId());
						if (eR.getCode() == EntityResult.OPERATION_WRONG) {
							MessageDialog.showErrorMessage(
									SwingUtilities.getWindowAncestor(FormAddSharedReference.this.getContentPane()),
									ApplicationManager.getTranslation("M_SHARED_ITEMS_NOT_ADDED"));
						} else {
							FormAddSharedReference.this.buttonOpt = true;
							FormAddSharedReference.this.dispose();
						}
					} catch (final Exception e1) {
						FormAddSharedReference.logger.error(null, e1);
					}
				} else {
					if (!FormAddSharedReference.this.nameDataField.isEmpty()) {
						MessageDialog.showErrorMessage(
								SwingUtilities.getWindowAncestor(FormAddSharedReference.this.getContentPane()),
								ApplicationManager.getTranslation("shareRemote.M_NECESSARY_TARGET_USER"));
					} else {
						MessageDialog.showErrorMessage(
								SwingUtilities.getWindowAncestor(FormAddSharedReference.this.getContentPane()),
								ApplicationManager.getTranslation("shareRemote.M_NECESSARY_NAME"));
					}
				}

			}
		});
		return acceptButton;
	}

	/**
	 * Create and configure the message component
	 * @return The message component
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

	/**
	 * Create and configure the receiver list
	 * @return List of receivers users
	 * @throws Exception
	 */
	protected ListDataField createAndConfigureTargetUser() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IShareRemoteReference.SHARE_USER_TARGET_STRING);
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation(IShareRemoteReference.SHARE_USER_TARGET_STRING));
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.DIM, "text");
		h.put(DataField.EXPAND, "yes");
		h.put("rows", "5");
		final ListDataField listDataField = new ListDataField(h);
		return listDataField;
	}

	/**
	 * Create and configure the name component
	 * @return Name element
	 */
	protected TextDataField createAndConfigureName() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, "name");
		h.put(DataField.REQUIRED, "yes");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation("shareRemote.name"));
		h.put(DataField.DIM, "text");
		final TextDataField textDataField = new TextDataField(h);
		textDataField.setValue(this.name);
		textDataField.setEnabled(this.getModifyName());
		return textDataField;
	}

	/**
	 * Return a boolean if the form has data on all required components, like the receivers, content and
	 * key
	 * @return <code>true</code>, <code>false</code> otherwise.
	 */
	protected boolean checkFormShareData() {
		if (!this.listDataField.isEmpty() && (this.contentShare != null) && (this.componentShareKey != null)
				&& !this.nameDataField.isEmpty()) {
			return true;
		}
		return false;

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

	/**
	 * Return a boolean if the name of the shared element will modified
	 * @return <code>true</code>, <code>false</code> otherwise.
	 */
	public boolean getModifyName() {
		return this.modifyName;
	}

	/**
	 * Return a boolean if the user press the accept button or the cancel button
	 * @return <code>true</code>, <code>false</code> otherwise.
	 */
	public boolean getButtonOptionResult() {
		return this.buttonOpt;
	}

}
