package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;

/**
 * This class implements a <code>html</code> label.
 * <p>
 *
 * @author Imatia Innovation
 */
public class HTMLLabel extends IdentifiedAbstractFormComponent {

	private static final Logger logger = LoggerFactory.getLogger(HTMLLabel.class);

	/**
	 * The key for URLHTML. By default, "html".
	 */
	public static String URLHTML = "html";

	/**
	 * The key for default listener. By default, "listener".
	 */
	public static String DEFAULT_LISTENER = "listener";

	/**
	 * A reference for a scroll. By default, null.
	 */
	protected JScrollPane scroll = null;

	/**
	 * A hyperlink listener. By default, null.
	 */
	protected HTMLHyperlinkListener htmlHyperlinkListener = null;

	/**
	 * A reference for a editor pane. By default, null.
	 */
	protected JEditorPane editorPane = null;

	/**
	 * A reference for an urlHTML. By default, null.
	 */
	protected String urlHTML = null;

	/**
	 * The class constructor. Sets layout, initializes parameters and adds a scroll.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public HTMLLabel(final Map parameters) {
		this.setLayout(new BorderLayout());
		this.init(parameters);
		this.scroll = new JScrollPane(this.editorPane);
		this.add(this.scroll);
		this.editorPane.setEditable(false);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(100, 300);
	}

	/**
	 * Sets the text for editor pane.
	 * <p>
	 * @param text the text
	 */
	public void setText(final String text) {
		if (this.editorPane != null) {
			this.editorPane.setText(text);
		}
	}

	/**
	 * This class implements a hyperlink listener.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class HTMLHyperlinkListener implements HyperlinkListener {

		@Override
		public void hyperlinkUpdate(final HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				final String desc = e.getDescription().trim();
				if (desc != null) {
					if (desc.indexOf("#") == 0) {
						// Internal link
						if (e.getSource() instanceof JEditorPane) {
							((JEditorPane) e.getSource()).scrollToReference(e.getURL().getRef());
						}
					} else if (desc.indexOf("mailto:") == 0) {
						// Mail
						WWWDataField.processURL(desc);
						// BrowserControl.displayURL(desc);
					} else if (desc.indexOf("http") == 0) {
						// Web page
						WWWDataField.processURL(desc);
						// try {
						// String htmlUrl = java.net.URLEncoder.encode(desc,
						// "UTF-8");
						// if
						// (com.ontimize.util.webstart.WebStartUtilities.isWebStartApplication())
						// {
						// try {
						// // In a web start application use the open browser
						// utility
						// com.ontimize.util.webstart.WebStartUtilities.openBrowser(htmlUrl);
						// } catch (Exception ex) {
						// logger.error(null,ex);
						// BrowserControl.displayURL(htmlUrl);
						// }
						// }
						// } catch (UnsupportedEncodingException e1) {
						// e1.printStackTrace();
						// }
					} else {
						// Web page resource
						final URL resource = e.getURL();
						final JFileChooser chooser = new JFileChooser();
						chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), e.getDescription()));
						final int op = chooser.showSaveDialog((Component) e.getSource());
						if (op == JFileChooser.APPROVE_OPTION) {
							final File f = chooser.getSelectedFile();
							if (f != null) {
								try {

									final InputStream iS = resource.openStream();
									final FileOutputStream fOut = new FileOutputStream(f);
									final byte[] byteAux = new byte[1024];
									int bReader = 0;
									while ((bReader = iS.read(byteAux)) > 0) {
										fOut.write(byteAux, 0, bReader);
									}
									fOut.close();
								} catch (final IOException ex) {
									HTMLLabel.logger.error(null, ex);
								}

							}
						}
					}
				}
			}
		}

	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>html</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The url for open in the label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>listener</td>
	 *        <td><i>no/yes</i> or <i>false/true</i> (both valid)</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The listener for label.</td>
	 *        </tr>
	 *        </Table>
	 */

	@Override
	public void init(final Map parameters) {
		Object oParameter = parameters.get(Label.ATTR);
		if (oParameter == null) {
			if (ApplicationManager.DEBUG) {
				HTMLLabel.logger.debug(this.getClass().toString() + ": Error. Parameter 'attr' not found");
			}
		} else {
			this.attribute = oParameter.toString();
		}

		oParameter = parameters.get(HTMLLabel.URLHTML);
		if (oParameter != null) {
			try {
				final URL url = this.getClass().getClassLoader().getResource(oParameter.toString());
				this.editorPane = new JEditorPane(url);
			} catch (final Exception ex) {
				HTMLLabel.logger.error(null, ex);
			}
		} else {
			throw new IllegalArgumentException("It's neccesary attribute 'html'");
		}

		oParameter = parameters.get(HTMLLabel.DEFAULT_LISTENER);

		if (oParameter != null) {
			if (("no".equals(oParameter) || !"false".equals(oParameter))) {
				this.htmlHyperlinkListener = new HTMLHyperlinkListener();
			}
		} else {
			this.htmlHyperlinkListener = new HTMLHyperlinkListener();
		}

		if ((this.editorPane != null) && (this.htmlHyperlinkListener != null)) {
			this.editorPane.addHyperlinkListener(this.htmlHyperlinkListener);
		}

	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.editorPane.setEnabled(enabled);
	}

}
