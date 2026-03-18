package com.ontimize.jee.desktopclient.dms.upload.cloud.dropbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ontimize.gui.container.Row;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;

public class LoginIntoDropboxComponent extends Row {

	private static final Logger	logger	= LoggerFactory.getLogger(LoginIntoDropboxComponent.class);
	private WebView				webView;
	private JFXPanel			fxPanel;

	/**
	 * Instantiates a new ExpressionDataField.
	 *
	 * @param parameters
	 *            the parameters
	 */
	public LoginIntoDropboxComponent(final Map parameters) {
		super(parameters);
		this.createFxPanel();
		this.setOpaque(true);
		this.setBackground(Color.yellow);
	}

	private void createFxPanel() {
		this.fxPanel = new JFXPanel();
		this.fxPanel.setOpaque(true);
		this.fxPanel.setBackground(Color.red);
		this.setLayout(new BorderLayout());
		this.add(this.fxPanel, BorderLayout.CENTER);
		synchronized (this.fxPanel) {

			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					LoginIntoDropboxComponent.this.fxPanel.setScene(LoginIntoDropboxComponent.this.createBasicScene());
					synchronized (LoginIntoDropboxComponent.this.fxPanel) {
						LoginIntoDropboxComponent.this.fxPanel.notify();
					}
				}
			});
			try {
				this.fxPanel.wait();
			} catch (final InterruptedException e) {
				// do nothing
			}
		}
	}

	private Scene createBasicScene() {
		this.webView = new WebView();
		this.webView.setId("webViewPanel");

		// process page loading
		this.webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {

			@Override
			public void changed(final ObservableValue<? extends State> ov, final State oldState, final State newState) {
				if (newState == State.SUCCEEDED) {
					final Element documentElement = LoginIntoDropboxComponent.this.webView.getEngine().getDocument().getDocumentElement();
					final String authCode = this.findAuthCode(documentElement);
					if (authCode != null) {
						try {
							DropboxManager.getInstance().setAuthorizationCode(authCode);
							((IMDropbox) LoginIntoDropboxComponent.this.parentForm.getInteractionManager()).showRemoteDir();
						} catch (final Exception ex) {
							MessageManager.getMessageManager().showExceptionMessage(ex, LoginIntoDropboxComponent.logger);
						}
					}
				} else if (newState == State.FAILED) {
					MessageManager.getMessageManager().showExceptionMessage(LoginIntoDropboxComponent.this.webView.getEngine().getLoadWorker().getException(),
							LoginIntoDropboxComponent.logger);
					LoginIntoDropboxComponent.this.showLoginScreen();
				}
			}

			private String findAuthCode(final Element documentElement) {
				if ((documentElement.getAttributes() != null) && (documentElement.getAttributes().getLength() > 0) && (documentElement.getAttributes()
						.getNamedItem("id") != null) && "auth-code".equals(documentElement.getAttributes().getNamedItem("id").getNodeValue())) {
					return documentElement.getChildNodes().item(0).getNodeValue();
				}
				for (int i = 0; i < documentElement.getChildNodes().getLength(); i++) {
					final Node el = documentElement.getChildNodes().item(i);
					if (el instanceof Element) {
						final String res = this.findAuthCode((Element) el);
						if ((res != null) && (res.length() > 0)) {
							return res;
						}
					}
				}
				return null;
			}
		});

		final BorderPane borderPane = new BorderPane();
		borderPane.setCenter(this.webView);
		return new Scene(borderPane, 10, 10);
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		final Object constraints = super.getConstraints(parentLayout);
		if (constraints instanceof GridBagConstraints) {
			return new GridBagConstraints(-1, -1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		}
		return constraints;
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (this.webView != null) {
			this.webView.setDisable(!enabled);
		}
	}

	public void showLoginScreen() {
		try {
			final String url = DropboxManager.getInstance().getAuthorizationUrl();
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					LoginIntoDropboxComponent.this.webView.getEngine().load(url);
				}
			});
		} catch (final Exception ex) {
			LoginIntoDropboxComponent.logger.error("Error loading login screen", ex);
		}

	}

}
