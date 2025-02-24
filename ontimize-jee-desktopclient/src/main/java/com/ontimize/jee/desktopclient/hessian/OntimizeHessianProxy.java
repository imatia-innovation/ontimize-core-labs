package com.ontimize.jee.desktopclient.hessian;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.caucho.hessian.client.HessianRuntimeException;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.ontimize.jee.common.exceptions.InvalidCredentialsException;
import com.ontimize.jee.common.security.ILoginProvider;

public class OntimizeHessianProxy extends HessianProxy {

	private static final Logger logger = LoggerFactory.getLogger(OntimizeHessianProxy.class);

	public OntimizeHessianProxy(final URI url, final HessianProxyFactory factory) {
		super(url, factory);
	}

	public OntimizeHessianProxy(final URI url, final HessianProxyFactory factory, final Class<?> type) {
		super(url, factory, type);
	}

	@Override
	protected OntimizeHessianProxyFactory getFactory() {
		return (OntimizeHessianProxyFactory) this.factory;
	}

	/**
	 * Sends the HTTP request to the Hessian connection.
	 */
	@Override
	protected HessianConnection sendRequest(final String methodName, final Object[] args) throws IOException {
		try {
			return this.internalSendRequest(methodName, args);
		} catch (final IOException exception) {
			Throwable cause = exception;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			// if (cause instanceof NonRepeatableRequestException) {
			// // significa que intento autenticar
			// if (this.relogin()) {
			// return this.internalSendRequest(methodName, args);
			// }
			// }
			throw exception;
		} catch (final InvalidCredentialsException ex) {
			if (this.relogin()) {
				return this.internalSendRequest(methodName, args);
			}
			throw ex;
		}
	}

	protected boolean relogin() {
		final ILoginProvider loginProvider = this.getFactory().getLoginProvider();
		if (loginProvider != null) {
			try {
				loginProvider.doLogin(this.getURL());
				return true;
			} catch (final Exception error) {
				OntimizeHessianProxy.logger.error(null, error);
			}
		}
		return false;
	}

	private HessianConnection internalSendRequest(final String methodName, final Object[] args) throws IOException {
		HessianConnection conn = null;

		conn = this.getFactory().getConnectionFactory().open(this.getURL());

		boolean isValid = false;
		OutputStream os = null;

		try {
			this.addRequestHeaders(conn);

			try {
				os = conn.getOutputStream();
			} catch (final Exception e) {
				throw new HessianRuntimeException(e);
			}

			final AbstractHessianOutput out = this.getFactory().getHessianOutput(os);
			out.call(methodName, args);
			out.flush();
			if (conn instanceof OntimizeHessianHttpClientConnection) { // TODO repensar alternativa
				os.close();
			}
			conn.sendRequest();
			isValid = true;
			return conn;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
			} catch (final Exception e) {
				OntimizeHessianProxy.logger.info(e.toString(), e);
			}

			try {
				if (!isValid && (conn != null)) {
					conn.close();
				}
			} catch (final Exception e) {
				OntimizeHessianProxy.logger.info(e.toString(), e);
			}
		}
	}

}
