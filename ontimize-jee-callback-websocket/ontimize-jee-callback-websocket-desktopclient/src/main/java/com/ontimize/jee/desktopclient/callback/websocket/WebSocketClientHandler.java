package com.ontimize.jee.desktopclient.callback.websocket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeoutException;

import javax.websocket.EncodeException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpStatus;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.callback.CallbackWrapperMessage;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.jackson.OntimizeMapper;
import com.ontimize.jee.common.services.user.IUserInformationService;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.ontimize.jee.common.util.Base64Utils;
import com.ontimize.jee.desktopclient.callback.ICallbackClientHandler;
import com.ontimize.jee.desktopclient.callback.ICallbackEventListener;
import com.ontimize.jee.desktopclient.hessian.OntimizeHessianProxyFactoryBean;
import com.ontimize.jee.desktopclient.spring.BeansFactory;

public class WebSocketClientHandler extends TextWebSocketHandler implements ICallbackClientHandler, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);

	private WebSocketSession webSocketSession;

	private String webSocketRelativeUrl;

	private String webSocketUrl;

	private boolean connected;

	private final List<ICallbackEventListener> listeners;

	private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

	public WebSocketClientHandler() {
		super();
		this.listeners = new ArrayList<>();
		this.connected = false;
		this.reconnectInThread();
	}

	public void setWebSocketRelativeUrl(final String url) {
		this.webSocketRelativeUrl = url;
	}

	public String getWebSocketRelativeUrl() {
		return this.webSocketRelativeUrl;
	}

	public String getWebSocketUrl() {
		return this.webSocketUrl;
	}

	public void setWebSocketUrl(final String webSocketUrl) {
		this.webSocketUrl = webSocketUrl;
	}

	@Override
	public void afterPropertiesSet() {
		if ((this.getWebSocketRelativeUrl() != null) && (this.getWebSocketRelativeUrl().length() > 0)) {
			String base = System.getProperty(OntimizeHessianProxyFactoryBean.SERVICES_BASE_URL);
			if (base != null) {
				if ((base.charAt(base.length() - 1) != '/') && (this.getWebSocketRelativeUrl().charAt(0) != '/')) {
					base = base + '/';
				}
				this.setWebSocketUrl(base + this.getWebSocketRelativeUrl());
			}
		}
	}

	@Override
	public void addCallbackEventListener(final ICallbackEventListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeCallbackEventListener(final ICallbackEventListener listener) {
		this.listeners.remove(listener);
	}

	/**
	 * Connect to WebSocket
	 * @param sServer
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws URISyntaxException
	 * @throws TimeoutException
	 */
	protected void connect() throws InterruptedException, ExecutionException, URISyntaxException, TimeoutException {
		// final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
		// OntimizeHessianHttpClientSessionProcessorFactory.createClient(-1));
		// final RestTemplate restTemplate = new RestTemplate(requestFactory);
		final List<Transport> transports = new ArrayList<>(2);
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		// transports.add(new RestTemplateXhrTransport(restTemplate));

		final SockJsClient sockJsClient = new SockJsClient(transports);
		final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

		final ListenableFuture<WebSocketSession> handshake = sockJsClient.doHandshake(this, headers,
				new URI(this.getWebSocketUrl()));
		sockJsClient.start();
		this.webSocketSession = handshake.get();
		WebSocketClientHandler.logger.info("websocket connecting");
	}

	/**
	 * Send message to server by WebSocket
	 * @param message
	 * @throws OntimizeJEEException
	 * @throws IOException
	 * @throws EncodeException
	 */
	@Override
	public void sendMessage(final Integer messageType, final String messageSubtype, final Object ob) throws OntimizeJEEException {
		try {
			final TextMessage textMessage = new TextMessage(
					new CallbackWrapperMessage(messageType, messageSubtype, ob).serialize());
			this.checkConnection();
			this.webSocketSession.sendMessage(textMessage);
		} catch (final OntimizeJEEException ex) {
			throw ex;
		} catch (final Exception error) {
			WebSocketClientHandler.logger.error(null, error);
			throw new OntimizeJEEException(error);
		}
	}

	@Override
	public void sendMessage(final Integer messageType, final Object ob) throws OntimizeJEEException {
		this.sendMessage(messageType, null, ob);
	}

	private void checkConnection() throws OntimizeJEEException {
		if (!this.connected) {
			throw new OntimizeJEEException("websocket not connected");
		}
	}

	@Override
	public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
		super.afterConnectionEstablished(session);
		WebSocketClientHandler.logger.info("websocket connection established");
		this.connected = true;
	}

	@Override
	public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
		super.afterConnectionClosed(session, status);
		WebSocketClientHandler.logger.info("websocket connection closed");
		this.connected = false;
		this.webSocketSession = null;
		this.reconnectInThread();
	}

	@Override
	public void handleTransportError(final WebSocketSession session, final Throwable exception) throws Exception {
		super.handleTransportError(session, exception);
	}

	@Override
	protected void handleTextMessage(final WebSocketSession session, final TextMessage textMessage) throws Exception {
		super.handleTextMessage(session, textMessage);
		CallbackWrapperMessage wrappedMessage = null;
		try {
			final String newMessage = Base64Utils
					.decode(new String(textMessage.getPayload().getBytes(StandardCharsets.ISO_8859_1)));
			wrappedMessage = new OntimizeMapper().readValue(newMessage, CallbackWrapperMessage.class);
		} catch (final Exception error) {
			throw new OntimizeJEERuntimeException(error);
		}
		this.fireMessageEvent(wrappedMessage);
	}

	private void fireMessageEvent(final CallbackWrapperMessage wrappedMessage) {
		for (final ICallbackEventListener listener : this.listeners.toArray(new ICallbackEventListener[0])) {
			listener.onCallbackMessageReceived(wrappedMessage);
		}
	}

	private void reconnectInThread() {
		this.executor.execute(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
					} catch (final InterruptedException e) {
						// do nothing
					}
					try {
						// Avoid trying reconnect whilst the user in not logged
						if ((ApplicationManager.getApplication() == null) || !ParseUtilsExtended
								.getBoolean(ReflectionTools.getFieldValue(ApplicationManager.getApplication(), "loggedIn"),
										true)) {
							continue;
						}
						WebSocketClientHandler.this.connect();
						WebSocketClientHandler.logger.info("conectado");
						return;
					} catch (final HttpClientErrorException ex) {
						if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
							// Necessary to reconnect, the sessionId is not valid anymore -> Do some operation
							WebSocketClientHandler.logger.error("INVALID_SESSION__MUST_BE_RE-LOGGED");
							BeansFactory.getBean(IUserInformationService.class).getUserInformation();
							WebSocketClientHandler.logger.error("RE-LOGGED_SUCCESSFULLY");
						} else {
							WebSocketClientHandler.logger.error(null, ex);
						}
					} catch (final Exception error) {
						WebSocketClientHandler.logger.error(null, error);
					}
				}
			}
		});

	}

}
