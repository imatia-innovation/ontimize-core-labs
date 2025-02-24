/*
 *
 */
package com.ontimize.jee.desktopclient.locator.handlers;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.i18n.IDatabaseBundleManager;
import com.ontimize.jee.common.locator.ErrorAccessControl;
import com.ontimize.jee.common.locator.InitialContext;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.tools.proxy.AbstractInvocationDelegate;
import com.ontimize.jee.common.util.operation.RemoteOperationManager;
import com.ontimize.jee.common.util.share.IShareRemoteReference;
import com.ontimize.jee.desktopclient.locator.remoteoperation.WebsocketRemoteOperationManager;

/**
 * The Class UtilLocatorInvocationDelegate.
 */
public class UtilInvocationDelegate extends AbstractInvocationDelegate implements UtilReferenceLocator {

	/** The remote operation handler. */
	protected WebsocketRemoteOperationManager remoteOperationHandler;

	protected IDatabaseBundleManager databaseBundleManager;

	protected IShareRemoteReference sharePreferencesReference;

	/**
	 * Instantiates a new util locator invocation delegate.
	 */
	public UtilInvocationDelegate() {
		super();
		this.remoteOperationHandler = new WebsocketRemoteOperationManager();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getMessages(int, int)
	 */
	@Override
	public List getMessages(final int sessionIdTo, final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#sendMessage(java.lang.String, java.lang.String,
	 * int)
	 */
	@Override
	public void sendMessage(final String message, final String user, final int sessionId) throws Exception {
		// do nothing right now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.ontimize.locator.UtilReferenceLocator#sendMessage(com.ontimize.locator.UtilReferenceLocator.
	 * Message, java.lang.String, int)
	 */
	@Override
	public void sendMessage(final Message message, final String user, final int sessionId) throws Exception {
		// do nothing right now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#sendMessageToAll(java.lang.String, int)
	 */
	@Override
	public void sendMessageToAll(final String message, final int sessionId) throws Exception {
		// do nothing right now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getAttachmentEntity(int)
	 */
	@Override
	public Entity getAttachmentEntity(final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getPrintingTemplateEntity(int)
	 */
	@Override
	public Entity getPrintingTemplateEntity(final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getConnectedUsers(int)
	 */
	@Override
	public List getConnectedUsers(final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getConnectedSessionIds(int)
	 */
	@Override
	public List getConnectedSessionIds(final int sessionid) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getRemoteOperationManager(int)
	 */
	@Override
	public RemoteOperationManager getRemoteOperationManager(final int sessionId) throws Exception {
		return this.remoteOperationHandler;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getRemoteReference(java.lang.String, int)
	 */
	@Override
	public Object getRemoteReference(final String name, final int sessionId) throws Exception {
		if (name == null) {
			return null;
		}
		if (name.equals(ExtendedPropertiesBundle.getDbBundleManagerName())) {
			return this.getRemoteReferenceDatabaseBundle();
		}
		if (name.equals(IShareRemoteReference.REMOTE_NAME)) {
			return this.getRemoteReferenceSharePreferences();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#removeEntity(java.lang.String, int)
	 */
	@Override
	public void removeEntity(final String entityName, final int sessionId) throws Exception {
		// do nothing right now
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getLoadedEntities(int)
	 */
	@Override
	public List getLoadedEntities(final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getServerTimeZone(int)
	 */
	@Override
	public TimeZone getServerTimeZone(final int sessionId) throws Exception {
		return TimeZone.getDefault();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getLoginEntityName(int)
	 */
	@Override
	public String getLoginEntityName(final int sessionId) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getToken()
	 */
	@Override
	public String getToken() throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getUserFromCert(java.lang.String)
	 */
	@Override
	public String getUserFromCert(final String certificate) throws Exception {
		return null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.locator.UtilReferenceLocator#getPasswordFromCert(java.lang.String)
	 */
	@Override
	public String getPasswordFromCert(final String certificate) throws Exception {
		return null;
	}

	@Override
	public InitialContext retrieveInitialContext(final int sessionId, final Map params) throws Exception {
		return null;
	}

	/**
	 * Gets the remote reference database bundle.
	 * @return the remote reference database bundle
	 */
	protected IDatabaseBundleManager getRemoteReferenceDatabaseBundle() {
		if (this.databaseBundleManager != null) {
			return this.databaseBundleManager;
		}

		this.databaseBundleManager = (IDatabaseBundleManager) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { IDatabaseBundleManager.class }, new DatabaseBundleManagerInvocationDelegate());
		return this.databaseBundleManager;
	}

	protected IShareRemoteReference getRemoteReferenceSharePreferences() {
		if (this.sharePreferencesReference != null) {
			return this.sharePreferencesReference;
		}

		this.sharePreferencesReference = (IShareRemoteReference) Proxy.newProxyInstance(
				Thread.currentThread().getContextClassLoader(),
				new Class<?>[] { IShareRemoteReference.class }, new SharePreferencesInvocationDelegate());
		return this.sharePreferencesReference;
	}

	@Override
	public Locale getLocale(final int arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSuffixString() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLocale(final int arg0, final Locale arg1) throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public String getLocaleEntity() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void blockUserDB(final String arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public EntityResult changePassword(final String arg0, final int arg1, final Map arg2, final Map arg3) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean checkBlockUserDB(final String arg0) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean getAccessControl() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ErrorAccessControl getErrorAccessControl() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean supportChangePassword(final String arg0, final int arg1) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List getRemoteAdministrationMessages(final int arg0, final int arg1) throws Exception {
		// TODO Auto-generated method stub
		return new Vector();
	}

	@Override
	public void sendRemoteAdministrationMessages(final String arg0, final int arg1) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportIncidenceService() throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
