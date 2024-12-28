package com.ontimize.util.share;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.ListDataField;
import com.ontimize.gui.field.SelectionListDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.share.IShareRemoteReference;

public class FormAddUserSharedReference extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(FormAddUserSharedReference.class);

	protected ResourceBundle resourceBundle;

	protected EntityReferenceLocator locator;

	protected ListDataField listDataField;

	protected SelectionListDataField selectionListDataField;

	protected Vector<String> vSelectedUsers = new Vector<String>();

	protected boolean updateTargetShare = false;

	/**
	 * Create a form window to add receiver to a shared element
	 * @param owner -> Parent window element of the form
	 * @param modal -> To specify if the dialog will be opened in a model form, blocking the other parts
	 *        of application
	 * @param locator -> Locator, needed to obtain the remote references to the sharing systems
	 * @param p -> Point where the dialog opens (top-left corner)
	 * @param listDataField -> ListDataField of the receiver list
	 */
	public FormAddUserSharedReference(final Window owner, final boolean modal, final EntityReferenceLocator locator,
			final ListDataField listDataField) {
		super(owner, ApplicationManager.getTranslation("M_ADD_TARGET_USER_TITLE"), modal);
		this.setResourceBundle(ApplicationManager.getApplicationBundle());
		this.locator = locator;
		this.listDataField = listDataField;
		this.createAndConfigurePanelComponents();
		this.pack();
	}

	/**
	 * Create and configure the content panel
	 * @throws Exception
	 */
	protected void createAndConfigurePanelComponents() {
		final SelectionListDataField selectionListDataField = this.createAndConfigureUserList();
		final Button acceptButton = this.createAndConfigureButtonAccept();
		final Button cancelButton = this.createAndConfigureButtonCancel();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		final GridBagConstraints gbc_selectionList = new GridBagConstraints();
		gbc_selectionList.gridwidth = 2;
		gbc_selectionList.insets = new Insets(0, 0, 5, 0);
		gbc_selectionList.fill = GridBagConstraints.BOTH;
		gbc_selectionList.gridx = 0;
		gbc_selectionList.gridy = 0;
		this.getContentPane().add(selectionListDataField, gbc_selectionList);

		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.anchor = GridBagConstraints.EAST;
		gbc_acceptButton.insets = new Insets(0, 0, 0, 5);
		gbc_acceptButton.gridx = 0;
		gbc_acceptButton.gridy = 1;
		this.getContentPane().add(acceptButton, gbc_acceptButton);

		final GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.WEST;
		gbc_cancelButton.gridx = 1;
		gbc_cancelButton.gridy = 1;
		this.getContentPane().add(cancelButton, gbc_cancelButton);

	}

	/**
	 * Create and return a SelectionListDataField with the list of selected users
	 * @return The SelectionListDataField with the list of selected user
	 */
	protected SelectionListDataField createAndConfigureUserList() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IShareRemoteReference.SHARE_TARGET_USERS_LIST);
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation(IShareRemoteReference.SHARE_TARGET_USERS_LIST));
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.DIM, "text");
		h.put(DataField.EXPAND, "yes");

		this.selectionListDataField = new SelectionListDataField(h);

		try {
			final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) this.locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME,
							this.locator.getSessionId());

			final List<String> eRUserList = remoteReference.getUserList(this.locator.getSessionId());
			final Vector<String> erVector = new Vector<String>();
			erVector.addAll(eRUserList);
			this.selectionListDataField.setItems(erVector);

			this.selectionListDataField.addValueChangeListener(new ValueChangeListener() {

				@Override
				public void valueChanged(final ValueEvent e) {
					FormAddUserSharedReference.this.vSelectedUsers = (Vector<String>) FormAddUserSharedReference.this.selectionListDataField
							.getValue();
				}
			});
		} catch (final Exception e1) {
			FormAddUserSharedReference.logger.trace(null, e1);
			MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(FormAddUserSharedReference.this),
					ApplicationManager.getTranslation("M_NOT_RETRIEVE_USER_DATA"));
			return null;
		}

		final List oV = (List) this.listDataField.getValue();
		if (oV != null) {
			final Vector<String> v = new Vector<String>();
			for (final Object actualOV : oV) {
				v.add(actualOV.toString());
			}

			this.selectionListDataField.setValue(v);
		}

		return this.selectionListDataField;
	}

	/**
	 * Create and return the accept button
	 * @return The accept button
	 */
	protected Button createAndConfigureButtonAccept() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "acceptButton");
		h.put(Button.TEXT, this.resourceBundle.getObject("application.accept"));
		final Button acceptButton = new Button(h);
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormAddUserSharedReference.this.updateTargetShare = true;
				FormAddUserSharedReference.this.listDataField.setValue(FormAddUserSharedReference.this.vSelectedUsers);
				FormAddUserSharedReference.this.dispose();

			}
		});
		return acceptButton;
	}

	/**
	 * Create and return the cancel button
	 * @return The cancel button
	 */
	protected Button createAndConfigureButtonCancel() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "cancelButton");
		h.put(Button.TEXT, this.resourceBundle.getObject("application.cancel"));
		final Button cancelButton = new Button(h);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormAddUserSharedReference.this.updateTargetShare = false;
				FormAddUserSharedReference.this.dispose();
			}
		});

		return cancelButton;

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
	 * Return a boolean if the list has been updated
	 * @return Return <code>true</code> if the list of users has changed, <code>false</code> otherwise
	 */
	public boolean getUpdateStatus() {
		return this.updateTargetShare;
	}

}
