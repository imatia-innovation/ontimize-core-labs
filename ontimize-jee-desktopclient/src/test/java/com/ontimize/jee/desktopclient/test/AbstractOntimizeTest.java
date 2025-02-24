package com.ontimize.jee.desktopclient.test;

import java.net.URI;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;

import com.ontimize.jee.common.security.ILoginProvider;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianProxyFactory;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianProxyFactoryBean;
import com.ontimize.jee.desktopclient.locator.security.OntimizeLoginProvider;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

/**
 * The Class AbstractOntimizeTest.
 */
public abstract class AbstractOntimizeTest {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(AbstractOntimizeTest.class);


	/**
	 * Instantiates a new abstract ontimize test.
	 */
	public AbstractOntimizeTest() {
		super();
		BeansFactory.init(new String[] {});

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
		((GenericApplicationContext) BeansFactory.getApplicationContext())
		.registerBean(serviceInterface, (Supplier)
				() -> {
					final OntimizeHessianProxyFactoryBean bean = new OntimizeHessianProxyFactoryBean();
					bean.setProxyFactory(BeansFactory.getBean(OntimizeHessianProxyFactory.class));
					bean.setServiceRelativeUrl(relativeUrl);
					bean.setServiceInterface(serviceInterface);
					bean.afterPropertiesSet();
					return bean;
				});
		return BeansFactory.getBean(serviceInterface);
	}

	protected OntimizeHessianProxyFactory createHessianProxyFactory() {
		((GenericApplicationContext) BeansFactory.getApplicationContext())
		.registerBean(OntimizeHessianProxyFactory.class, (Supplier) () -> {
			final OntimizeHessianProxyFactory bean = new OntimizeHessianProxyFactory();
			bean.setSerializerFactory(new com.ontimize.jee.common.hessian.CustomSerializerFactory());
			bean.setLoginProvider(BeansFactory.getBean(ILoginProvider.class));
			return bean;
		});
		return BeansFactory.getBean(OntimizeHessianProxyFactory.class);
	}

	protected OntimizeLoginProvider createLoginProvider() {
		((GenericApplicationContext) BeansFactory.getApplicationContext())
		.registerBean(OntimizeLoginProvider.class, (Supplier) () -> {
			return new OntimizeLoginProvider();
		});
		return BeansFactory.getBean(OntimizeLoginProvider.class);
	}

	/**
	 * Prepare test.
	 * @param args the args
	 * @throws Exception the exception
	 */
	protected void prepareTest(final String[] args) throws Exception {
		System.setProperty(OntimizeHessianProxyFactoryBean.SERVICES_BASE_URL, getServiceBaseUrl());
		createLoginProvider();
		createHessianProxyFactory();

		final IUserInformationService service = this.createService(IUserInformationService.class,
				"/private/services/hessian/userinformationservice");
		BeansFactory.getBean(ILoginProvider.class).doLogin(new URI(this.getServiceBaseUrl()), this.getUser(),
				this.getPass());

		final long startTime = System.currentTimeMillis();
		this.doTest();
		AbstractOntimizeTest.logger.error("Test finalizado en {}", System.currentTimeMillis() - startTime);
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

	// /**
	// * The Class TestLoginProvider.
	// */
	// public static class TestLoginProvider implements ILoginProvider {
	//
	// /** The last used user. */
	// private String lastUsedUser;
	//
	// /** The last used password. */
	// private String lastUsedPassword;
	//
	// /**
	// * Instantiates a new test login provider.
	// */
	// public TestLoginProvider() {
	// super();
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see com.ontimize.jee.common.security.ILoginProvider#doLogin(java.net.URI, java.lang.String,
	// * java.lang.String)
	// */
	// @Override
	// public void doLogin(final URI serviceUrl, final String user, final String password) throws
	// InvalidCredentialsException {
	// if ((user != null) && (password != null)) {
	// OntimizeHessianHttpClientSessionProcessorFactory.addCredentials(serviceUrl, user, password);
	// }
	// this.lastUsedUser = user;
	// this.lastUsedPassword = password;
	// try {
	// // no pedemos autenticar con un servicio hessian porque la entity (inputstream) no es "repeatable" y
	// // no puede llevar a cabo la
	// // negociacion
	//
	// final SocketConfig.Builder socketConfigBuilder = SocketConfig.custom().setSoKeepAlive(true);
	// final SocketConfig socketConfig = socketConfigBuilder.build();
	// final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
	//
	// final PoolingHttpClientConnectionManagerBuilder connectionManangerBuilder =
	// PoolingHttpClientConnectionManagerBuilder
	// .create()
	// .setDefaultSocketConfig(socketConfig)
	// .setMaxConnPerRoute(20)
	// .setMaxConnTotal(50)
	// .setTlsSocketStrategy(null);
	// OntimizeHessianHttpClientSessionProcessorFactory.checkIgnoreSSLCerts(connectionManangerBuilder);
	// final PoolingHttpClientConnectionManager connectionMananger = connectionManangerBuilder.build();
	// final HttpClientBuilder clientBuilder = HttpClients.custom()
	// .disableAutomaticRetries()
	// .disableAuthCaching()
	// .setDefaultCredentialsProvider(credentialsProvider)
	// .setConnectionManager(connectionMananger)
	// .addRequestInterceptorLast(OntimizeHessianHttpClientSessionProcessorFactory.getHttpProcessor())
	// // .setDefaultCookieStore(OntimizeHessianHttpClientSessionProcessorFactory.getCookieStore())
	// .setConnectionManagerShared(true)
	// .disableRedirectHandling();
	// final CloseableHttpClient httpClient = clientBuilder.build();
	//
	// final HttpGet request = new HttpGet(serviceUrl);
	// final CloseableHttpResponse response = httpClient.execute(request);
	// if (response.getCode() == 401) {
	// throw new InvalidCredentialsException(response.getReasonPhrase());
	// }
	//
	// } catch (final Exception ex) {
	// throw new InvalidCredentialsException(ex);
	// } finally {
	// if ((user != null) && (password != null)) {
	// OntimizeHessianHttpClientSessionProcessorFactory.removeCredentials(serviceUrl);
	// }
	// }
	// }
	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see com.ontimize.jee.common.security.ILoginProvider#doLogin(java.net.URI)
	// */
	// @Override
	// public void doLogin(final URI uri) throws InvalidCredentialsException {
	// this.doLogin(uri, this.lastUsedUser, this.lastUsedPassword);
	// }
	//
	// }

}
