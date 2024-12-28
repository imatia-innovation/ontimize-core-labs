package com.ontimize.jee.desktopclient.locator.remoteoperation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.util.operation.RemoteOperationManager;

/**
 * The Class RemoteOperationManagerProxyHandler.
 */
public class WebsocketRemoteOperationManager implements RemoteOperationManager {

	private static final AtomicLong ID_GENERATOR = new AtomicLong();

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(WebsocketRemoteOperationManager.class);

	/**
	 * Instantiates a new remote operation manager proxy handler.
	 */
	public WebsocketRemoteOperationManager() {
		super();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#run(java.lang.String, java.util.HashMap,
	 * int)
	 */
	@Override
	public String run(final String clase, final HashMap parameters, final int sessionId) {
		throw new OntimizeJEERuntimeException("Cast to WebsocketRemoteOperationManager and use listener");
	}

	/**
	 * Run.
	 * @param clase the clase
	 * @param parameters the parameters
	 * @param sessionId the session id
	 * @param listener the listener
	 * @return the remote operation delegate
	 * @throws Exception the exception
	 */
	public RemoteOperationDelegate run(final String clase, final Map<String, Object> parameters, final int sessionId,
			final IRemoteOperationListener<?> listener) {
		final RemoteOperationDelegate delegate = new RemoteOperationDelegate(
				String.valueOf(WebsocketRemoteOperationManager.ID_GENERATOR.incrementAndGet()), clase, parameters,
				listener);
		delegate.run();
		return delegate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#hasRequired(java.lang .String, int)
	 */
	@Deprecated
	@Override
	public boolean hasRequired(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use delegate");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#getRequired(java.lang .String, int)
	 */
	@Deprecated
	@Override
	public HashMap getRequired(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use delegate");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#setRequired(java.lang .String,
	 * java.util.HashMap, int)
	 */
	@Deprecated
	@Override
	public void setRequired(final String token, final HashMap required, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use delegate");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#cancel(java.lang.String , int)
	 */
	@Deprecated
	@Override
	public void cancel(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use delegate");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#isFinished(java.lang .String, int)
	 */
	@Deprecated
	@Override
	public boolean isFinished(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use listener");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#getResult(java.lang .String, int)
	 */
	@Deprecated
	@Override
	public Map getResult(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use listener");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#getStatus(java.lang .String, int)
	 */
	@Deprecated
	@Override
	public int getStatus(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use listener");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.util.operation.RemoteOperationManager#
	 * getCurrentExecutionInformation(java.lang.String, int)
	 */
	@Deprecated
	@Override
	public Map getCurrentExecutionInformation(final String token, final int sessionId) throws Exception {
		throw new OntimizeJEERuntimeException("Use listener");
	}

}
