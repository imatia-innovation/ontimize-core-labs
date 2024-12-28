package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URLEncoder;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.BrowserControl;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

/**
 * This class implements a data field with a button to open a web browser.
 * <p>
 *
 * @author Imatia Innovation
 */
public class WWWDataField extends TextDataField {

	private static final Logger logger = LoggerFactory.getLogger(WWWDataField.class);

	protected static final String INTERNET_BUTTON_TOOLTIP = "wwwdatafield.internet_button";

	protected JButton internetButton = new FieldButton();

	/**
	 * The class constructor. Calls to super and fix the browser icon and its listener.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public WWWDataField(final Map parameters) {
		super(parameters);

		// This is similar to a normal text data field. It is possible to change
		// the font color and we have to add the button events listener to open
		// the browser

		final ImageIcon buttonIcon = ImageManager.getIcon(ImageManager.INTERNET);
		if (buttonIcon == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				WWWDataField.logger.debug("internet.png icon not found");
			}
			this.internetButton.setText("...");
			this.internetButton.setMargin(new Insets(0, 0, 0, 0));
		} else {
			this.internetButton.setIcon(buttonIcon);
			this.internetButton.setMargin(new Insets(0, 0, 0, 0));
		}
		this.internetButton.setToolTipText(WWWDataField.INTERNET_BUTTON_TOOLTIP);

		super.add(this.internetButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}
		this.internetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				final String url = WWWDataField.this.getText();
				if (!url.equals("")) {
					WWWDataField.processURL(url);
				}
			}
		});

		final boolean borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"), true);
		final boolean opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false);
		MouseListener listenerHighlightButtons = null;
		if (highlightButtons) {
			listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButton(this.internetButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
	}

	/**
	 * Process the URL and displays the correct browser depending of a url address or a mail address.
	 * <p>
	 * @param url the address to process
	 */
	public static void processURL(String url) {
		// We consider that if the URL contains '@' then it is an e-mail address
		if (url.indexOf('@') != -1) {
			BrowserControl.displayURL("mailto:" + url);
		} else {
			// If the url is not an e-mail address then open the browser.
			// For a problem with the dll library if the url ends with html or
			// htm
			// then add /
			if (url.substring(url.length() - 3, url.length()).equalsIgnoreCase("htm")
					|| url.substring(url.length() - 4, url.length()).equalsIgnoreCase("html")) {
				url = url + "/";
			}
			// Codify the url to avoid the browser does it,
			// and avoid the problem tha appear if there are two spaces
			// together.
			// We use the class java.net.URLEncoder.
			final StringBuilder urlEncoded = new StringBuilder();
			for (int i = 0; i < url.length(); i++) {
				final char character = url.charAt(i);
				if (character == ' ') {
					urlEncoded.append("%20");
				} else if (character == '"') {
					urlEncoded.append("%22");
				} else if (character == '<') {
					urlEncoded.append("%3C");
				} else if (character == '>') {
					urlEncoded.append("%3E");
				} else if (character == '#') {
					urlEncoded.append("%23");
				} else if (character == '%') {
					urlEncoded.append("%25");
				} else if (character == '{') {
					urlEncoded.append("%7B");
				} else if (character == '}') {
					urlEncoded.append("%7D");
				} else if (character == '|') {
					urlEncoded.append("%7C");
				} else if (character == '\\') {
					urlEncoded.append("%5C");
				} else if (character == '^') {
					urlEncoded.append("%5E");
				} else if (character == '~') {
					urlEncoded.append("%7E");
				} else if (character == '[') {
					urlEncoded.append("%5B");
				} else if (character == ']') {
					urlEncoded.append("%5E");
				} else if (character == '`') {
					urlEncoded.append("%60");
				} else {
					urlEncoded.append(character);
				}
			}
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				WWWDataField.logger.debug(URLEncoder.encode(url));
				WWWDataField.logger.debug(urlEncoded.toString());
			}

			if (urlEncoded.substring(0, 7).equalsIgnoreCase("http://")
					|| urlEncoded.substring(0, 8).equalsIgnoreCase("https://")) {
				if (com.ontimize.util.webstart.WebStartUtilities.isWebStartApplication()) {
					try {
						// This is a webstart application. Uses the open browser
						// utility
						com.ontimize.util.webstart.WebStartUtilities.openBrowser(urlEncoded.toString());
					} catch (final Exception e) {
						WWWDataField.logger.error(null, e);
						BrowserControl.displayURL(urlEncoded.toString());
					}
				} else {
					BrowserControl.displayURL(urlEncoded.toString());
				}
			} else {
				if (com.ontimize.util.webstart.WebStartUtilities.isWebStartApplication()) {
					try {
						// This is a webstart application. Uses the open browser
						// utility
						com.ontimize.util.webstart.WebStartUtilities.openBrowser("http://" + urlEncoded);
					} catch (final Exception e) {
						WWWDataField.logger.error(null, e);
						BrowserControl.displayURL("http://" + urlEncoded);
					}
				} else {
					BrowserControl.displayURL("http://" + urlEncoded);
				}
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		if (this.internetButton != null) {
			this.internetButton.setVisible(enabled);
		}
		this.dataField.setEnabled(enabled);
		this.enabled = enabled;
		if (!enabled) {
			this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
			this.dataField.setForeground(Color.black);
		} else {
			this.dataField.setBackground(this.backgroundColor);
			this.dataField.setForeground(Color.blue);
		}
		this.updateBackgroundColor();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resource) {
		super.setResourceBundle(resource);
		try {
			this.internetButton.setToolTipText(resource.getString(WWWDataField.INTERNET_BUTTON_TOOLTIP));
		} catch (final Exception e) {
			WWWDataField.logger.trace(null, e);
		}
	}

	/**
	 * Validates email address.
	 * @param email Email address
	 * @return condition about email
	 * @since 5.2068EN
	 */
	public static boolean isValidEmail(final String email) {
		Pattern pattern;
		Matcher matcher;
		pattern = Pattern
				.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
		matcher = pattern.matcher(email);
		return matcher.matches();
	}

}
