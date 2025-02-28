/*
 * The Apache Software License, Version 1.1 Copyright (c) 2001-2004 Caucho Technology, Inc. All
 * rights reserved. Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met: 1. Redistributions of source code
 * must retain the above copyright notice, this list of conditions and the following disclaimer. 2.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution. 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software developed by the Caucho
 * Technology (http://www.caucho.com/)." Alternately, this acknowlegement may appear in the software
 * itself, if and wherever such third-party acknowlegements normally appear. 4. The names "Hessian",
 * "Resin", and "Caucho" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact info@caucho.com. 5.
 * Products derived from this software may not be called "Resin" nor may "Resin" appear in their
 * names without prior written permission of Caucho Technology. THIS SOFTWARE IS PROVIDED ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL CAUCHO
 * TECHNOLOGY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 * @author Scott Ferguson
 */

package com.caucho.hessian.client;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import com.caucho.hessian.io.AbstractHessianInput;
import com.caucho.hessian.io.AbstractHessianOutput;
import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.HessianRemoteObject;
import com.caucho.hessian.io.HessianRemoteResolver;
import com.caucho.hessian.io.SerializerFactory;
import com.caucho.services.client.ServiceProxyFactory;

// @formatter:off
/**
 * Factory for creating Hessian client stubs. The returned stub will call the remote object for all methods.
 *
 * <pre>
 *
 * String url = "http://localhost:8080/ejb/hello";
 * HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
 * </pre>
 *
 * After creation, the stub can be like a regular Java class. Because it makes remote calls, it can throw more exceptions than a Java class. In particular, it may throw protocol
 * exceptions.
 *
 * The factory can also be configured as a JNDI resource. The factory expects to parameters: "type" and "url", corresponding to the two arguments to <code>create</code>
 *
 * In Resin 3.0, the above example would be configured as:
 *
 * <pre>
 *  &lt;reference> &lt;jndi-name>hessian/hello&lt;/jndi-name>
 * &lt;factory>com.caucho.hessian.client.HessianProxyFactory&lt;/factory> &lt;init-param url="http://localhost:8080/ejb/hello"/> &lt;init-param
 * type="test.HelloHome"/> &lt;/reference>
 * </pre>
 *
 * To get the above resource, use JNDI as follows:
 *
 * <pre>
 * Context ic = new InitialContext();
 * HelloHome hello = (HelloHome) ic.lookup("java:comp/env/hessian/hello");
 *
 * System.out.println("Hello: " + hello.helloWorld());
 * </pre>
 *
 * <h3>Authentication</h3>
 *
 * <p>The proxy can use HTTP basic authentication if the user and the password are set.
 */
// @formatter:on
public class HessianProxyFactory implements ServiceProxyFactory, ObjectFactory {

	private final ClassLoader			loader;

	private SerializerFactory			serializerFactory;

	private HessianConnectionFactory	connFactory;

	private final HessianRemoteResolver	resolver;

	private String						user;

	private String						password;

	private String						basicAuth;

	private boolean						isOverloadEnabled	= false;

	private boolean						isChunkedPost		= true;

	private boolean						isDebug				= false;

	private long						readTimeout			= -1;

	private long						connectTimeout		= -1;

	/**
	 * Creates the new proxy factory.
	 */
	public HessianProxyFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates the new proxy factory.
	 */
	public HessianProxyFactory(final ClassLoader loader) {
		this.loader = loader;
		this.resolver = new HessianProxyResolver(this);
	}

	/**
	 * Sets the user.
	 */
	public void setUser(final String user) {
		this.user = user;
		this.basicAuth = null;
	}

	/**
	 * Sets the password.
	 */
	public void setPassword(final String password) {
		this.password = password;
		this.basicAuth = null;
	}

	public String getBasicAuth() {
		if (this.basicAuth != null) {
			return this.basicAuth;
		} else if ((this.user != null) && (this.password != null)) {
			return "Basic " + this.base64(this.user + ":" + this.password);
		} else {
			return null;
		}
	}

	/**
	 * Sets the connection factory to use when connecting to the Hessian service.
	 */
	public void setConnectionFactory(final HessianConnectionFactory factory) {
		this.connFactory = factory;
	}

	/**
	 * Returns the connection factory to be used for the HTTP request.
	 */
	public HessianConnectionFactory getConnectionFactory() {
		if (this.connFactory == null) {
			this.connFactory = this.createHessianConnectionFactory();
			this.connFactory.setHessianProxyFactory(this);
		}

		return this.connFactory;
	}

	/**
	 * Sets the debug
	 */
	public void setDebug(final boolean isDebug) {
		this.isDebug = isDebug;
	}

	/**
	 * Gets the debug
	 */
	public boolean isDebug() {
		return this.isDebug;
	}

	/**
	 * Returns true if overloaded methods are allowed (using mangling)
	 */
	public boolean isOverloadEnabled() {
		return this.isOverloadEnabled;
	}

	/**
	 * set true if overloaded methods are allowed (using mangling)
	 */
	public void setOverloadEnabled(final boolean isOverloadEnabled) {
		this.isOverloadEnabled = isOverloadEnabled;
	}

	/**
	 * Set true if should use chunked encoding on the request.
	 */
	public void setChunkedPost(final boolean isChunked) {
		this.isChunkedPost = isChunked;
	}

	/**
	 * Set true if should use chunked encoding on the request.
	 */
	public boolean isChunkedPost() {
		return this.isChunkedPost;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 */
	public long getReadTimeout() {
		return this.readTimeout;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 */
	public void setReadTimeout(final long timeout) {
		this.readTimeout = timeout;
	}

	/**
	 * The socket connection timeout in milliseconds.
	 */
	public long getConnectTimeout() {
		return this.connectTimeout;
	}

	/**
	 * The socket connect timeout in milliseconds.
	 */
	public void setConnectTimeout(final long timeout) {
		this.connectTimeout = timeout;
	}

	/**
	 * True if the proxy can read Hessian 2 responses.
	 */
	public void setHessian2Reply(final boolean isHessian2) {
	}

	/**
	 * True if the proxy should send Hessian 2 requests.
	 */
	public void setHessian2Request(final boolean isHessian2) {
	}

	/**
	 * Returns the remote resolver.
	 */
	public HessianRemoteResolver getRemoteResolver() {
		return this.resolver;
	}

	/**
	 * Sets the serializer factory.
	 */
	public void setSerializerFactory(final SerializerFactory factory) {
		this.serializerFactory = factory;
	}

	/**
	 * Gets the serializer factory.
	 */
	public SerializerFactory getSerializerFactory() {
		if (this.serializerFactory == null) {
			this.serializerFactory = new SerializerFactory(this.loader);
		}

		return this.serializerFactory;
	}

	protected HessianConnectionFactory createHessianConnectionFactory() {
		final String className = System.getProperty(HessianConnectionFactory.class.getName());

		HessianConnectionFactory factory = null;

		try {
			if (className != null) {
				final ClassLoader loader = Thread.currentThread().getContextClassLoader();

				final Class<?> cl = Class.forName(className, false, loader);

				factory = (HessianConnectionFactory) cl.newInstance();

				return factory;
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}

		return new HessianURLConnectionFactory();
	}

	/**
	 * Creates a new proxy with the specified URL. The API class uses the java.api.class value from _hessian_
	 *
	 * @param url
	 *            the URL where the client object is located.
	 * @return a proxy to the object with the specified interface.
	 */
	public Object create(final String url) throws URISyntaxException, ClassNotFoundException {
		HessianMetaInfoAPI metaInfo;

		metaInfo = (HessianMetaInfoAPI) this.create(HessianMetaInfoAPI.class, url);

		final String apiClassName = (String) metaInfo._hessian_getAttribute("java.api.class");

		if (apiClassName == null) {
			throw new HessianRuntimeException(url + " has an unknown api.");
		}

		final Class<?> apiClass = Class.forName(apiClassName, false, this.loader);

		return this.create(apiClass, url);
	}

	/**
	 * Creates a new proxy with the specified URL. The returned object is a proxy with the interface specified by api.
	 *
	 * <pre>
	 *  String url = "http://localhost:8080/ejb/hello"); HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
	 * </pre>
	 *
	 * @param api
	 *            the interface the proxy class needs to implement
	 * @param url
	 *            the URL where the client object is located.
	 * @return a proxy to the object with the specified interface.
	 */
	@Override
	public Object create(final Class<?> api, final String urlName) throws URISyntaxException {
		return this.create(api, urlName, this.loader);
	}

	/**
	 * Creates a new proxy with the specified URL. The returned object is a proxy with the interface specified by api.
	 *
	 * <pre>
	 *  String url = "http://localhost:8080/ejb/hello"); HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
	 * </pre>
	 *
	 * @param api
	 *            the interface the proxy class needs to implement
	 * @param url
	 *            the URL where the client object is located.
	 * @return a proxy to the object with the specified interface.
	 * @throws URISyntaxException
	 */
	public Object create(final Class<?> api, final String urlName, final ClassLoader loader) throws URISyntaxException {
		final URI url = new URI(urlName);

		return this.create(api, url, loader);
	}

	/**
	 * Creates a new proxy with the specified URL. The returned object is a proxy with the interface specified by api.
	 *
	 * <pre>
	 *  String url = "http://localhost:8080/ejb/hello"); HelloHome hello = (HelloHome) factory.create(HelloHome.class, url);
	 * </pre>
	 *
	 * @param api
	 *            the interface the proxy class needs to implement
	 * @param url
	 *            the URL where the client object is located.
	 * @return a proxy to the object with the specified interface.
	 */
	public Object create(final Class<?> api, final URI url, final ClassLoader loader) {
		if (api == null) {
			throw new NullPointerException("api must not be null for HessianProxyFactory.create()");
		}
		InvocationHandler handler = null;

		handler = new HessianProxy(url, this, api);

		return Proxy.newProxyInstance(loader, new Class[] { api, HessianRemoteObject.class }, handler);
	}

	public AbstractHessianInput getHessian2Input(final InputStream is) {
		AbstractHessianInput in;

		in = new Hessian2Input(is);

		in.setRemoteResolver(this.getRemoteResolver());

		in.setSerializerFactory(this.getSerializerFactory());

		return in;
	}

	public AbstractHessianOutput getHessianOutput(final OutputStream os) {
		final AbstractHessianOutput out = new Hessian2Output(os);
		out.setSerializerFactory(this.getSerializerFactory());
		return out;
	}

	/**
	 * JNDI object factory so the proxy can be used as a resource.
	 */
	@Override
	public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx,
			final Hashtable<?, ?> environment)
			throws Exception {
		final Reference ref = (Reference) obj;

		String api = null;
		String url = null;

		for (int i = 0; i < ref.size(); i++) {
			final RefAddr addr = ref.get(i);

			final String type = addr.getType();
			final String value = (String) addr.getContent();

			if ("type".equals(type)) {
				api = value;
			} else if ("url".equals(type)) {
				url = value;
			} else if ("user".equals(type)) {
				this.setUser(value);
			} else if ("password".equals(type)) {
				this.setPassword(value);
			}
		}

		if (url == null) {
			throw new NamingException("`url' must be configured for HessianProxyFactory.");
		}
		// XXX: could use meta protocol to grab this
		if (api == null) {
			throw new NamingException("`type' must be configured for HessianProxyFactory.");
		}

		final Class<?> apiClass = Class.forName(api, false, this.loader);

		return this.create(apiClass, url);
	}

	/**
	 * Creates the Base64 value.
	 */
	private String base64(final String value) {
		final StringBuilder cb = new StringBuilder();

		int i = 0;
		for (i = 0; (i + 2) < value.length(); i += 3) {
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + value.charAt(i + 1);
			chunk = (chunk << 8) + value.charAt(i + 2);

			cb.append(HessianProxyFactory.encode(chunk >> 18));
			cb.append(HessianProxyFactory.encode(chunk >> 12));
			cb.append(HessianProxyFactory.encode(chunk >> 6));
			cb.append(HessianProxyFactory.encode(chunk));
		}

		if ((i + 1) < value.length()) {
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + value.charAt(i + 1);
			chunk <<= 8;

			cb.append(HessianProxyFactory.encode(chunk >> 18));
			cb.append(HessianProxyFactory.encode(chunk >> 12));
			cb.append(HessianProxyFactory.encode(chunk >> 6));
			cb.append('=');
		} else if (i < value.length()) {
			long chunk = value.charAt(i);
			chunk <<= 16;

			cb.append(HessianProxyFactory.encode(chunk >> 18));
			cb.append(HessianProxyFactory.encode(chunk >> 12));
			cb.append('=');
			cb.append('=');
		}

		return cb.toString();
	}

	public static char encode(long d) {
		d &= 0x3f;
		if (d < 26) {
			return (char) (d + 'A');
		} else if (d < 52) {
			return (char) ((d + 'a') - 26);
		} else if (d < 62) {
			return (char) ((d + '0') - 52);
		} else if (d == 62) {
			return '+';
		} else {
			return '/';
		}
	}

}
