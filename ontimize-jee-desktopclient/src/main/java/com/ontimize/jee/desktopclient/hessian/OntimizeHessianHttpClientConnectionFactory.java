/**
 * CustomHessianURLConnectionFactory.java 18-abr-2013
 *
 *
 *
 */
package com.ontimize.jee.desktopclient.hessian;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.AbstractHessianConnectionFactory;
import com.caucho.hessian.client.HessianConnection;
import com.ontimize.jee.common.session.HeaderAttribute;

/**
 * URLConnectionFactory personalizada para conectar con hessian. Tamién envía el locale del
 * usuario en las peticiones.
 *
 * @author <a href="user@email.com">Author</a>
 */
public class OntimizeHessianHttpClientConnectionFactory extends AbstractHessianConnectionFactory {

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory
			.getLogger(OntimizeHessianHttpClientConnectionFactory.class.getName());

	protected CloseableHttpClient	httpClient;

	/**
	 * Instantiates a new ontimize hessian http client connection factory.
	 */
	public OntimizeHessianHttpClientConnectionFactory() {
		super();
	}

	/**
	 * Opens a new or recycled connection to the HTTP server.
	 * @param url the url
	 * @return the hessian connection
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public HessianConnection open(final URI url) throws IOException {
		// Prepare authentication

		OntimizeHessianHttpClientConnectionFactory.logger.trace(this + " open(" + url + ")");

		final CloseableHttpClient httpClient = getClient();

		final HttpPost request = new HttpPost(url.toString());
		this.addCustomHeaders(request);

		return new OntimizeHessianHttpClientConnection(request, httpClient);
	}

	protected CloseableHttpClient getClient() {
		if (httpClient == null) {
			httpClient = createClient();
		}
		return httpClient;
	}

	protected CloseableHttpClient createClient() {
		final long connectTimeout = this.getHessianProxyFactory().getConnectTimeout();
		return OntimizeHessianHttpClientSessionProcessorFactory.createClient(connectTimeout);
	}

	protected OntimizeHessianProxyFactory getOntimizeHessianProxyFactory() {
		return (OntimizeHessianProxyFactory) this.getHessianProxyFactory();
	}

	/**
	 * Adds custom headers to the request.
	 * @param request the hessian connection
	 */
	protected void addCustomHeaders(final HttpPost request) {
		final Locale locale = Locale.getDefault();
		final HeaderAttribute ha = new HeaderAttribute();
		ha.setName("locale-country");
		ha.setValue(locale.getCountry());
		final HeaderAttribute hb = new HeaderAttribute();
		hb.setName("locale-language");
		hb.setValue(locale.getLanguage());
		final List<HeaderAttribute> has = new ArrayList<>();
		has.add(ha);
		has.add(hb);
		Collection<HeaderAttribute> headerAttributes;
		headerAttributes = this.getOntimizeHessianProxyFactory().getHeaderAttributesProvider().getHeaderAttributes(has);
		for (final HeaderAttribute headerAtt : headerAttributes) {
			if (headerAtt.getName() != null) {
				request.addHeader(headerAtt.getName(), headerAtt.getValue());
			}
		}
	}

}
