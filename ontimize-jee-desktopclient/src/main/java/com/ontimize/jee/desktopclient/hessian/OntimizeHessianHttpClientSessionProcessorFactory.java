package com.ontimize.jee.desktopclient.hessian;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.Credentials;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.SafeCasting;
import com.ontimize.jee.common.util.Base64Utils;

/**
 * A factory for creating OntimizeHessianHttpClientSessionProcessor objects.
 */
public final class OntimizeHessianHttpClientSessionProcessorFactory {

	private static final Logger logger = LoggerFactory
			.getLogger(OntimizeHessianHttpClientSessionProcessorFactory.class);

	public final static String JWT_HEADER = "X-Auth-Token";

	/** The sessionid. */
	// private static String SESSIONID;
	private static final CookieStore httpCookieStore = new BasicCookieStore();

	/** The request interceptor. */
	private static HttpRequestInterceptor requestInterceptor = new SessionIdHttpRequestInterceptor();

	/** The response interceptor. */
	private static HttpResponseInterceptor responseInterceptor = new SessionIdHttpResponseInterceptor();

	public static boolean ENCRYPT = true;

	public static String JWT_TOKEN = null;

	private static Map<AuthScope, Credentials> credentials = new HashMap<>();

	/**
	 * Gets the http processor.
	 * @return the http processor
	 */
	public static void addCredentials(final URI uri, final String userName, final String password) {
		final String host = uri.getHost();
		final Integer port = uri.getPort();
		if (OntimizeHessianHttpClientSessionProcessorFactory.ENCRYPT) {
			OntimizeHessianHttpClientSessionProcessorFactory.credentials.put(
					new AuthScope(null, host, port, null, StandardAuthScheme.BASIC),
					new UsernamePasswordCredentials(userName, password.toCharArray()));
			OntimizeHessianHttpClientSessionProcessorFactory.credentials.put(
					new AuthScope(null, host, port, null, StandardAuthScheme.DIGEST),
					new UsernamePasswordCredentials(userName,
							OntimizeHessianHttpClientSessionProcessorFactory.encrypt(password).toCharArray()));
		} else {
			OntimizeHessianHttpClientSessionProcessorFactory.credentials.put(new AuthScope(host, port),
					new UsernamePasswordCredentials(userName, password.toCharArray()));
		}
	}

	public static boolean removeCredentials(final URI uri) {
		final String host = uri.getHost();
		final Integer port = uri.getPort();
		final Credentials remove = OntimizeHessianHttpClientSessionProcessorFactory.credentials
				.remove(new AuthScope(host, port));
		return remove != null;
	}

	/**
	 * The Class SessionIdHttpRequestInterceptor.
	 */
	public static class SessionIdHttpRequestInterceptor implements HttpRequestInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpRequestInterceptor#process(org.apache.http.HttpRequest,
		 * org.apache.http.protocol.HttpContext)
		 */
		@Override
		public void process(final HttpRequest request, final EntityDetails entity, final HttpContext context)
				throws HttpException, IOException {
			if (!OntimizeHessianHttpClientSessionProcessorFactory.credentials.isEmpty()) {
				// CredentialsProvider credentialsProvider = (CredentialsProvider)
				// context.getAttribute(ClientContext.CREDS_PROVIDER);
				final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				for (final Entry<AuthScope, Credentials> entry : OntimizeHessianHttpClientSessionProcessorFactory.credentials
						.entrySet()) {
					credentialsProvider.setCredentials(entry.getKey(), entry.getValue());
				}
				((HttpClientContext) context).setCredentialsProvider(credentialsProvider);
				// context.setAttribute(HttpClientContext.CREDS_PROVIDER, credentialsProvider);
			}
			if ((OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN != null)
					&& (OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN.length() > 0)) {
				request.addHeader("Authorization",
						"Bearer " + OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN);
			}
		}

	}

	/**
	 * The Class SessionIdHttpResponseInterceptor.
	 */
	public static class SessionIdHttpResponseInterceptor implements HttpResponseInterceptor {

		/*
		 * (non-Javadoc)
		 *
		 * @see org.apache.http.HttpResponseInterceptor#process(org.apache.http.HttpResponse,
		 * org.apache.http.protocol.HttpContext)
		 */
		@Override
		public void process(final HttpResponse response, final EntityDetails entity, final HttpContext context)
				throws HttpException, IOException {
			final Header jwtHeader = response.getFirstHeader(OntimizeHessianHttpClientSessionProcessorFactory.JWT_HEADER);
			if ((jwtHeader != null) && (jwtHeader.getValue() != null) && (jwtHeader.getValue().length() > 0)) {
				OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN = jwtHeader.getValue();
			} else if ((response.getCode() == 401)
					|| (response.getCode() == 302)) {
				OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN = null;
			}
		}

	}

	// public static String getSESSIONID() {
	// return OntimizeHessianHttpClientSessionProcessorFactory.SESSIONID;
	// }

	static class SessionProcessorKey {

		private final String key;

		private final Integer port;

		public SessionProcessorKey(final String key, final Integer port) {
			super();
			this.key = key;
			this.port = port;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.key == null) ? 0 : this.key.hashCode());
			result = (prime * result) + ((this.port == null) ? 0 : this.port.hashCode());
			return result;
		}

		/*
		 * (non-Javadoc)
		 *
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final SessionProcessorKey other = (SessionProcessorKey) obj;
			if (this.key == null) {
				if (other.key != null) {
					return false;
				}
			} else if (!this.key.equals(other.key)) {
				return false;
			}
			if (this.port == null) {
				if (other.port != null) {
					return false;
				}
			} else if (!this.port.equals(other.port)) {
				return false;
			}
			return true;
		}

	}

	private static String encrypt(final String password) {
		try {
			final MessageDigest md = java.security.MessageDigest.getInstance("SHA");
			// Get the password byes
			final byte[] bytes = password.getBytes();
			md.update(bytes);
			final byte[] ecriptedBytes = md.digest();

			final char[] characters = Base64Utils.encode(ecriptedBytes);
			final String result = new String(characters);
			return result;
		} catch (final Exception e) {
			OntimizeHessianHttpClientSessionProcessorFactory.logger.error(null, e);
			return null;
		}
	}

	public static CookieStore getCookieStore() {
		return OntimizeHessianHttpClientSessionProcessorFactory.httpCookieStore;
	}

	protected static CloseableHttpClient httpClient;

	public static CloseableHttpClient getClient() {
		if (httpClient == null) {
			httpClient = createClient(-1);
		}
		return httpClient;
	}

	protected static CloseableHttpClient createClient(final long connectTimeout) {
		final SocketConfig.Builder socketConfigBuilder = SocketConfig.custom().setSoKeepAlive(true);
		if (connectTimeout >= 0) {
			socketConfigBuilder.setSoTimeout(SafeCasting.longToInt(connectTimeout), TimeUnit.MILLISECONDS);
		}
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
				.addRequestInterceptorLast(requestInterceptor)
				.addResponseInterceptorFirst(responseInterceptor)
				.setDefaultCookieStore(OntimizeHessianHttpClientSessionProcessorFactory.getCookieStore())
				.setConnectionManagerShared(true)
				.disableRedirectHandling();
		final CloseableHttpClient client = clientBuilder.build();

		return client;
	}

	public static void checkIgnoreSSLCerts(final PoolingHttpClientConnectionManagerBuilder builder) {
		final boolean ignoreSSLCerts = ParseUtilsExtended.getBoolean(System.getProperty("ignoreSSLCerts"), false);
		if (ignoreSSLCerts) {
			try {

				// Crear un SSLContext que confíe en todos los certificados
				final SSLContext sslContext = SSLContexts.custom()
						.loadTrustMaterial(
								(TrustStrategy) (final X509Certificate[] chain, final String authType) -> true)
						.build();
				final TlsSocketStrategy tlsStrategy = new DefaultClientTlsStrategy(sslContext,
						NoopHostnameVerifier.INSTANCE);
				builder.setTlsSocketStrategy(tlsStrategy);

			} catch (final Exception error) {
				OntimizeHessianHttpClientSessionProcessorFactory.logger.error(null, error);
			}
		}
	}

	public static Object getSESSIONID() {
		for (final Cookie cookie : OntimizeHessianHttpClientSessionProcessorFactory.httpCookieStore.getCookies()) {
			if ("session".equals(cookie.getName().toLowerCase())) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public static String getJwtToken() {
		return OntimizeHessianHttpClientSessionProcessorFactory.JWT_TOKEN;
	}

}
