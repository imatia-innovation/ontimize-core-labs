package com.ontimize.jee.desktopclient.locator.handlers;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.security.ClientPermissionManager;
import com.ontimize.jee.common.tools.proxy.AbstractInvocationDelegate;
import com.ontimize.jee.desktopclient.locator.OJeeClientPermissionLocator;
import com.ontimize.security.ClientSecurityManager;

/**
 * The Class ClientPermissionInvocationDelegate.
 */
public class ClientPermissionInvocationDelegate extends AbstractInvocationDelegate implements ClientPermissionManager {

	private static final Logger				logger	= LoggerFactory.getLogger(ClientPermissionInvocationDelegate.class);

	private boolean							clientPermissionsInstalled;

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.security.ClientPermissionManager#getClientPermissions(java.util.Hashtable, int)
	 */
	@Override
    public EntityResult getClientPermissions(final Map userKeys, final int sessionId) throws Exception {
		return null;
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.security.ClientPermissionManager#installClientPermissions(java.util.Hashtable, int)
	 */
	@Override
	public void installClientPermissions(final Map userKeys, final int sessionId) throws Exception {
		if (this.clientPermissionsInstalled) {
			ClientPermissionInvocationDelegate.logger
					.warn("Client permissions are already installed. No new permission can be installed.");
			return;
		}

		final Map<String, ?> clientPermissions = getLocator().getUserInformation().getClientPermissions();
		if (clientPermissions != null) {
			ApplicationManager
					.setClientSecurityManager(new ClientSecurityManager(HashMap.class.cast(clientPermissions)));
			this.clientPermissionsInstalled = true;
			// this.updateHourServerThread = ReflectionTools.newInstance(TimeThread.class, this);
			// this.updateHourServerThread.start();
		} else {
			MessageDialog.showMessage((JDialog) null,
					"Error retrieving client permissions client permissions: Returned NULL value",
					JOptionPane.ERROR_MESSAGE, null);
			ClientPermissionInvocationDelegate.logger.error(
					"Error retrieving client permissions client permissions: Returned NULL value");
		}
	}

	private OJeeClientPermissionLocator getLocator() {
		return (OJeeClientPermissionLocator) ApplicationManager.getApplication().getReferenceLocator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.security.ClientPermissionManager#getTime()
	 */
	@Override
	public long getTime() throws Exception {
		return System.currentTimeMillis();
	}

}
