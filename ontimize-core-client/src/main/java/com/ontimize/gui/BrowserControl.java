package com.ontimize.gui;

import java.awt.Desktop;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BrowserControl {

    private static final Logger logger = LoggerFactory.getLogger(BrowserControl.class);

    public static void displayURL(final String url) {

		if (url == null || url.isBlank()) {
			return;
		}

        try {
			if (!Desktop.isDesktopSupported()) {
				logger.warn("Desktop API not supported");
				return;
            }

			final Desktop desktop = Desktop.getDesktop();
			final URI uri = URI.create(url);
			if (url.toLowerCase().startsWith("mailto:")) {
				if (desktop.isSupported(Desktop.Action.MAIL)) {
					desktop.mail(uri);
				}
			} else {
				if (desktop.isSupported(Desktop.Action.BROWSE)) {
					desktop.browse(uri);
                }
            }
		} catch (final Exception e) {
			logger.error("Error opening URL: {}", url, e);
        }
    }

    /**
     * Try to determine whether this application is running under Windows or some other platform by
     * examing the "os.name" property.
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform() {
        final String os = System.getProperty("os.name");

        if ((os != null) && os.startsWith(BrowserControl.WIN_ID)) {
            return true;
        } else {
            return false;
        }
    }

    // Used to identify the windows platform.
    private static final String WIN_ID = "Windows";

    // The default system browser under windows.
    private static final String WIN_PATH = "rundll32";

    // The flag to display a url.
    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";

    private static final String WIN_NEW_WINDOW_START = "javascript:location.href='";

    private static final String WIN_NEW_WINDOW_END = "'";

    // The default browser under unix.
    private static final String UNIX_PATH = "netscape";

    // The flag to display a url.
    private static final String UNIX_FLAG = "-remote openURL";

}
