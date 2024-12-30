package com.ontimize.jee.desktopclient.dms.upload.cloud.gdrive;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class LoginIntoGoogleDriveComponent extends Row {

	private static final Logger	logger			= LoggerFactory.getLogger(LoginIntoGoogleDriveComponent.class);

	private static final String	SUCCESS_CODE	= "Success code=";

	private WebView				webView;
	private JFXPanel			fxPanel;

	/**
	 * Instantiates a new ExpressionDataField.
	 *
	 * @param parameters
	 *            the parameters
	 */
	public LoginIntoGoogleDriveComponent(Hashtable parameters) {
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
					LoginIntoGoogleDriveComponent.this.fxPanel.setScene(LoginIntoGoogleDriveComponent.this.createBasicScene());
					synchronized (LoginIntoGoogleDriveComponent.this.fxPanel) {
						LoginIntoGoogleDriveComponent.this.fxPanel.notify();
					}
				}
			});
			try {
				this.fxPanel.wait();
			} catch (InterruptedException e) {
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
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (newState == State.SUCCEEDED) {
					String title = LoginIntoGoogleDriveComponent.this.webView.getEngine().getTitle();
					if ((title != null) && title.contains(LoginIntoGoogleDriveComponent.SUCCESS_CODE)) {
						String code = title.substring(LoginIntoGoogleDriveComponent.SUCCESS_CODE.length());
						try {
							GoogleDriveManager.getInstance().setAuthorizationCode(code);
							((IMGoogleDrive) LoginIntoGoogleDriveComponent.this.parentForm.getInteractionManager()).showRemoteDir();
						} catch (Exception ex) {
							MessageManager.getMessageManager().showExceptionMessage(ex, LoginIntoGoogleDriveComponent.logger);
						}
					}

				} else if (newState == State.FAILED) {
					MessageManager.getMessageManager().showExceptionMessage(LoginIntoGoogleDriveComponent.this.webView.getEngine().getLoadWorker().getException(),
					        LoginIntoGoogleDriveComponent.logger);
				}
			}
		});

		BorderPane borderPane = new BorderPane();
		borderPane.setCenter(this.webView);
		return new Scene(borderPane, 10, 10);
	}

	@Override
	public Object getConstraints(LayoutManager parentLayout) {
		Object constraints = super.getConstraints(parentLayout);
		if (constraints instanceof GridBagConstraints) {
			return new GridBagConstraints(-1, -1, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		}
		return constraints;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (this.webView != null) {
			this.webView.setDisable(!enabled);
		}
	}

	public void showLoginScreen() {
		try {
			final String url = GoogleDriveManager.getInstance().getAuthorizationUrl();
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					LoginIntoGoogleDriveComponent.this.webView.getEngine().load(url);
				}
			});
		} catch (Exception ex) {
			LoginIntoGoogleDriveComponent.logger.error("Error loading login screen", ex);
		}

	}

}
