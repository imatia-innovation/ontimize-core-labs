package com.ontimize.jee.desktopclient.test;

import java.net.URI;

import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.exceptions.InvalidCredentialsException;
import com.ontimize.jee.common.hessian.CustomSerializerFactory;
import com.ontimize.jee.common.security.ILoginProvider;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianHttpClientConnectionFactory;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianHttpClientSessionProcessorFactory;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianProxyFactory;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianProxyFactoryBean;

/**
 * The Class AbstractOntimizeTest.
 */
public abstract class AbstractOntimizeTest {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractOntimizeTest.class);

	/** The pf. */
	OntimizeHessianProxyFactory pf;

	/** The hcf. */
	OntimizeHessianHttpClientConnectionFactory hcf;

	/** The serializer. */
	CustomSerializerFactory serializer;

	/**
	 * Instantiates a new abstract ontimize test.
	 */
	public AbstractOntimizeTest() {
		super();

		// SLF4JBridgeHandler.removeHandlersForRootLogger();
		// SLF4JBridgeHandler.install();
	}

	/**
	 * Creates the service.
	 * @param <T> the generic type
	 * @param serviceInterface the service interface
	 * @param relativeUrl the relative url
	 * @return the t
	 */
	protected <T> T createService(final Class<T> serviceInterface, final String relativeUrl) {
		final OntimizeHessianProxyFactoryBean bean = new OntimizeHessianProxyFactoryBean();
		bean.setProxyFactory(this.pf);
		bean.setServiceRelativeUrl(relativeUrl);
		bean.setServiceInterface(serviceInterface);
		bean.afterPropertiesSet();
		return (T) bean.getObject();
	}

	/**
	 * Prepare test.
	 * @param args the args
	 * @throws Exception the exception
	 */
	protected void prepareTest(final String[] args) throws Exception {
		final long startTime = System.currentTimeMillis();
		System.setProperty("com.ontimize.services.baseUrl", this.getServiceBaseUrl());
		this.serializer = new CustomSerializerFactory();
		this.pf = new OntimizeHessianProxyFactory();
		this.pf.setSerializerFactory(this.serializer);
		final TestLoginProvider loginProvider = new TestLoginProvider();
		this.pf.setLoginProvider(loginProvider);
		this.hcf = (OntimizeHessianHttpClientConnectionFactory) this.pf.getConnectionFactory();
		loginProvider.doLogin(new URI(this.getServiceBaseUrl()), this.getUser(), this.getPass());
		this.doTest();
		AbstractOntimizeTest.logger.error("Test finalizado en {}", System.currentTimeMillis() - startTime);
	}

	/**
	 * Gets the url connection factory.
	 * @return the url connection factory
	 */
	public OntimizeHessianHttpClientConnectionFactory getUrlConnectionFactory() {
		return this.hcf;
	}

	/**
	 * Gets the proxy factory.
	 * @return the proxy factory
	 */
	public OntimizeHessianProxyFactory getProxyFactory() {
		return this.pf;
	}

	/**
	 * Gets the service base url.
	 * @return the service base url
	 */
	protected abstract String getServiceBaseUrl();

	/**
	 * Do test.
	 * @throws Exception the exception
	 */
	protected abstract void doTest() throws Exception;

	/**
	 * Gets the user.
	 * @return the user
	 */
	protected abstract String getUser();

	/**
	 * Gets the pass.
	 * @return the pass
	 */
	protected abstract String getPass();

	/**
	 * The Class TestLoginProvider.
	 */
	public static class TestLoginProvider implements ILoginProvider {

		/** The last used user. */
		private String lastUsedUser;

		/** The last used password. */
		private String lastUsedPassword;

		/**
		 * Instantiates a new test login provider.
		 */
		public TestLoginProvider() {
			super();
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.ontimize.jee.common.security.ILoginProvider#doLogin(java.net.URI, java.lang.String,
		 * java.lang.String)
		 */
		@Override
		public void doLogin(final URI serviceUrl, final String user, final String password) throws InvalidCredentialsException {
			if ((user != null) && (password != null)) {
				OntimizeHessianHttpClientSessionProcessorFactory.addCredentials(serviceUrl, user, password);
			}
			this.lastUsedUser = user;
			this.lastUsedPassword = password;
			try {
				// no pedemos autenticar con un servicio hessian porque la entity (inputstream) no es "repeatable" y
				// no puede llevar a cabo la
				// negociacion

				final SocketConfig.Builder socketConfigBuilder = SocketConfig.custom().setSoKeepAlive(true);
				final SocketConfig socketConfig = socketConfigBuilder.build();
				final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

				final PoolingHttpClientConnectionManagerBuilder connectionManangerBuilder = PoolingHttpClientConnectionManagerBuilder
						.create()
						.setDefaultSocketConfig(socketConfig)
						.setMaxConnPerRoute(20)
						.setMaxConnTotal(50)
						.setTlsSocketStrategy(null);
				OntimizeHessianHttpClientSessionProcessorFactory.checkIgnoreSSLCerts(connectionManangerBuilder);
				final PoolingHttpClientConnectionManager connectionMananger = connectionManangerBuilder.build();
				final HttpClientBuilder clientBuilder = HttpClients.custom()
						.disableAutomaticRetries()
						.disableAuthCaching()
						.setDefaultCredentialsProvider(credentialsProvider)
						.setConnectionManager(connectionMananger)
						.addRequestInterceptorLast(OntimizeHessianHttpClientSessionProcessorFactory.getHttpProcessor())
						.setDefaultCookieStore(OntimizeHessianHttpClientSessionProcessorFactory.getCookieStore())
						.setConnectionManagerShared(true)
						.disableRedirectHandling();
				final CloseableHttpClient httpClient = clientBuilder.build();

				final HttpGet request = new HttpGet(serviceUrl);
				final CloseableHttpResponse response = httpClient.execute(request);
				if (response.getCode() == 401) {
					throw new InvalidCredentialsException(response.getReasonPhrase());
				}

			} catch (final Exception ex) {
				throw new InvalidCredentialsException(ex);
			} finally {
				if ((user != null) && (password != null)) {
					OntimizeHessianHttpClientSessionProcessorFactory.removeCredentials(serviceUrl);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see com.ontimize.jee.common.security.ILoginProvider#doLogin(java.net.URI)
		 */
		@Override
		public void doLogin(final URI uri) throws InvalidCredentialsException {
			this.doLogin(uri, this.lastUsedUser, this.lastUsedPassword);
		}

	}

}
