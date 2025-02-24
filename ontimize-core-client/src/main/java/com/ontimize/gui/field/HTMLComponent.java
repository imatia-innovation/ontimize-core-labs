package com.ontimize.gui.field;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * This class implements a HTML editor component.
 * <p>
 *
 * @see HTMLEditorKit
 * @author Imatia Innovation
 */
public class HTMLComponent extends JTextPane implements FormComponent, IdentifiedElement, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(HTMLComponent.class);

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The reference to the .html file.
	 */
	protected String file = null;

	/**
	 * The default locale reference.
	 */
	protected Locale locale = Locale.getDefault();

	/**
	 * The reference to parent form. By default, false.
	 */
	protected Form parentForm = null;

	/**
	 * The reference for visible permission in form. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The reference for enabled permission in form. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * This class implements an extension html editor where the document creation priority is set to
	 * minimum.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ExtendedHTMLEditorKit extends HTMLEditorKit {

		/**
		 * This method creates a document and sets the load priority in thread to minimun.
		 * <p>
		 * @return the document
		 */
		@Override
		public Document createDefaultDocument() {
			final Document doc = super.createDefaultDocument();
			((AbstractDocument) doc).setAsynchronousLoadPriority(Thread.MIN_PRIORITY);
			return doc;
		}

	}

	/**
	 * The class constructor. Inits parameters, sets transparent and disables the edition in the
	 * component.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public HTMLComponent(final Map parameters) {
		this.setEditorKit(new ExtendedHTMLEditorKit());
		this.init(parameters);
		this.setOpaque(false);
		this.setEditable(false);
		// Load the file. Absolute Path !!!!!
		this.loadPage(this.locale);
	}

	/**
	 * Initializes parameters and selects the bundle equals to class {@link ResourceBundle}, adding a
	 * suffix to basic file name.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *
	 *        <p>
	 *
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>archive</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Associated file. The extension file must be <code>.htm</code> or
	 *        <code>.html</code>.</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		final Object attr = parameters.get("attr");
		if (attr == null) {
			HTMLComponent.logger.debug(this.getClass().toString() + " : Attribute is null");
		} else {
			this.attribute = attr;
		}
		final Object archive = parameters.get("archive");
		if (archive == null) {
			HTMLComponent.logger.debug(this.getClass().toString() + " : Parameter 'archive' is null");
		} else {
			this.file = archive.toString();
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {

	}

	@Override
	public void setComponentLocale(final Locale l) {
		final Locale previousLocale = this.locale;
		this.locale = l;
		if ((l == null) && (previousLocale == null)) {
			return;
		}
		if (l != null) {
			if (!l.equals(previousLocale)) {
				this.loadPage(this.locale);
			}
		}
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	protected void loadPage(final Locale l) {
		// Load the page with the specified locale. If it does not exist then
		// load
		// the page without the local suffix
		if (this.file != null) {
			final long t = System.currentTimeMillis();
			String sLocaleSuffix = "_" + l.getLanguage() + "_" + l.getCountry();
			final String sVariant = l.getVariant();
			if ((sVariant != null) && !sVariant.equals("")) {
				sLocaleSuffix = sLocaleSuffix + "_ " + sVariant;
			}
			// File name:
			final int dotIndex = this.file.lastIndexOf(".");
			final String sFileName = this.file.substring(0, dotIndex);
			final String extenssion = this.file.substring(dotIndex, this.file.length());
			final String sCompleteName = sFileName + sLocaleSuffix + extenssion;
			URL urlPage = this.getClass().getClassLoader().getResource(sCompleteName);
			if (urlPage == null) {
				if (ApplicationManager.DEBUG) {
					HTMLComponent.logger.debug(this.getClass().toString() + " : Not found : " + sCompleteName);
				}
				// Try without the locale
				urlPage = this.getClass().getClassLoader().getResource(this.file);
				if (urlPage == null) {
					if (ApplicationManager.DEBUG) {
						HTMLComponent.logger.debug(this.getClass().toString() + " : Not found either : " + this.file);
					}
				} else {
					try {
						this.setPage(urlPage);
					} catch (final Exception e) {
						HTMLComponent.logger.error(null, e);
					}
				}
			} else {
				try {
					this.setPage(urlPage);
				} catch (final Exception e) {
					HTMLComponent.logger.error(null, e);
				}
			}
			if (ApplicationManager.DEBUG_TIMES) {
				HTMLComponent.logger.debug("HTMLComponent: Time loading page: " + (System.currentTimeMillis() - t));
			}
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		this.setOpaque(false);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}
		super.setEnabled(enabled);
	}

	@Override
	public void setVisible(final boolean visible) {
		if (visible) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(visible);
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}
	}

	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					HTMLComponent.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					HTMLComponent.logger.debug(null, e);
				} else {
					HTMLComponent.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					HTMLComponent.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					HTMLComponent.logger.debug(null, e);
				} else {
					HTMLComponent.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	public void setHTML(final String html) {
		final String v = html;
		final StringReader sr = new StringReader(v);
		try {
			this.setText("");
			this.getEditorKit().read(sr, this.getDocument(), 0);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				HTMLComponent.logger.error(null, e);
			} else {
				HTMLComponent.logger.trace(null, e);
			}
		} finally {
			try {
				sr.close();
			} catch (final Exception e) {
				HTMLComponent.logger.trace(null, e);
			}
		}
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
