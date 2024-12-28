package com.ontimize.report.listeners;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;

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
import com.ontimize.util.share.FormUpdateSharedReference;

public class SharedEditItemListener implements ActionListener {

	protected String preferenceKey;

	protected DefaultReportDialog defaultReportDialog;

	protected EntityReferenceLocator locator;

	private static final Logger logger = LoggerFactory.getLogger(SharedEditItemListener.class);

	public SharedEditItemListener(final String preferenceKey, final DefaultReportDialog defaultReportDialog) {
		this.preferenceKey = preferenceKey;
		this.defaultReportDialog = defaultReportDialog;
		this.locator = ApplicationManager.getApplication().getReferenceLocator();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		try {
			final int shareId = Integer.parseInt(e.getActionCommand());
			final int sessionID = this.locator.getSessionId();
			final Point p = ((Component) e.getSource()).getLocationOnScreen();
			final String user = ((ClientReferenceLocator) this.locator).getUser();

			final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) this.locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
			final SharedElement sharedItem = remoteReference.getSharedItem(shareId, sessionID);

			final String filterContent = this.defaultReportDialog.getCurrentConfiguration();

			final FormUpdateSharedReference f = new FormUpdateSharedReference(
					SwingUtilities
					.getWindowAncestor(SwingUtilities.getAncestorOfClass(Window.class, (Component) e.getSource())),
					true, this.locator, p, sharedItem);
			if (f.getUpdateStatus()) {
				final String nameUpdate = f.getName();
				final String contentShareUpdate = filterContent;
				final String messageUpdate = (String) f.getMessage();

				remoteReference.updateSharedItem(shareId, contentShareUpdate, messageUpdate, nameUpdate, sessionID);
			}

		} catch (final Exception ex) {
			SharedEditItemListener.logger.error("{}",
					ApplicationManager.getTranslation("shareRemote.not_retrive_message"), ex.getMessage(), ex);
			MessageDialog.showErrorMessage(this.defaultReportDialog.getContainer(), "shareRemote.not_retrive_message");
		}

	}

}
