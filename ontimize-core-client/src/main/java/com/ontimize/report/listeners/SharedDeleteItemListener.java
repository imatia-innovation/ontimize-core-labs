package com.ontimize.report.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.share.IShareRemoteReference;
import com.ontimize.jee.common.util.share.SharedElement;
import com.ontimize.report.DefaultReportDialog;

public class SharedDeleteItemListener implements ActionListener {

	protected String deleteKey = "REPORT_DELETE_SHARE_KEY";

	protected String preferenceKey;

	protected DefaultReportDialog defaultReportDialog;

	protected EntityReferenceLocator locator;

	private static final Logger logger = LoggerFactory.getLogger(SharedDeleteItemListener.class);

	public SharedDeleteItemListener(final String preferenceKey, final DefaultReportDialog defaultReportDialog) {
		this.preferenceKey = preferenceKey;
		this.defaultReportDialog = defaultReportDialog;
		this.locator = ApplicationManager.getApplication().getReferenceLocator();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			this.defaultReportDialog.getConfMenu().setVisible(false);
			if (MessageDialog.showQuestionMessage(this.defaultReportDialog.getContainer(),
					ApplicationManager.getTranslation(this.deleteKey))) {
				final int shareId = Integer.parseInt(e.getActionCommand());
				final int sessionID = this.locator.getSessionId();
				final String user = ((ClientReferenceLocator) this.locator).getUser();
				final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) this.locator)
						.getRemoteReference(IShareRemoteReference.REMOTE_NAME,
								sessionID);
				final SharedElement sharedItem = remoteReference.getSharedItem(shareId, sessionID);

				this.defaultReportDialog.saveReportConfiguration(sharedItem);

				remoteReference.deleteSharedItem(shareId, sessionID);
			}
		} catch (final Exception ex) {
			SharedDeleteItemListener.logger.error("{}",
					ApplicationManager.getTranslation("shareRemote.not_retrive_message"), ex.getMessage(), ex);
			MessageDialog.showErrorMessage(this.defaultReportDialog.getContainer(),
					"shareRemote.error_deleting_shared_element");
		}
	}

}
