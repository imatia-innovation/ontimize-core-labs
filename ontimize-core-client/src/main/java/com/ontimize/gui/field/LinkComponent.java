package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;

/**
 * This class creates a component with linkeable components. Links are created with
 * <code>html</code> code.
 * <p>
 *
 * @author Imatia Innovation
 *
 */
public class LinkComponent extends IdentifiedAbstractFormComponent {

	private static final Logger logger = LoggerFactory.getLogger(LinkComponent.class);

	/**
	 * A reference for a editor pane. By default, null.
	 */
	protected JEditorPane editorPane = null;

	/**
	 * The scroll presence condition. By default, true.
	 */
	protected boolean scroll = true;

	/**
	 * The enabled condition. By default, true.
	 */
	protected boolean enabled = true;

	/**
	 * The opaque condition. By default, true.
	 */
	protected boolean opaque = true;

	/**
	 * A reference for the bundle. By default, null.
	 */
	protected ResourceBundle bundle = null;

	/**
	 * An instance for a label vector.
	 */
	protected List labelVector = new Vector();

	/**
	 * An instance for a link vector.
	 */
	protected List linkVector = new Vector();

	/**
	 * An instance for a table vector.
	 */
	protected List tableVector = new Vector();

	/**
	 * The insert mode vector.
	 */
	protected List insertModeVector = new Vector();

	/**
	 * The class constructor. Calls to <code>super()</code> and init parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 */
	public LinkComponent(final Map parameters) {
		super();

		try {
			this.init(parameters);
		} catch (final Exception e) {
			LinkComponent.logger.error(null, e);
		}
	}

	/**
	 * Initializes parameters, creates the component and processes the label.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. New parameters are added:
	 *
	 *        <p>
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
	 *        <td>The attribute to manage the field.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>enabled</td>
	 *        <td><i><i>yes/no or true/false</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The enabled condition.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>scroll</td>
	 *        <td><i>yes/no or true/false</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The scroll presence.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td><i>yes/no or true/false</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The opaque field condition.</td>
	 *        </tr>
	 *        </TABLE>
	 */

	@Override
	public void init(final Map parameters) {
		if (parameters.containsKey("attr")) {
			this.attribute = parameters.get("attr");
		} else {
			throw new IllegalArgumentException("Error LinkComponent: be necessary 'attr'");
		}

		if (parameters.containsKey("enabled")) {
			final String sValue = (String) parameters.get("enabled");
			if ("no".equals(sValue) || "false".equals(sValue)) {
				this.enabled = false;
			}
		}

		if (parameters.containsKey("scroll")) {
			final Object o = parameters.get("scroll");
			if ("false".equals(o.toString()) || "no".equals(o.toString())) {
				this.scroll = false;
			}
		}

		if (parameters.containsKey("opaque")) {
			final String oValue = (String) parameters.get("opaque");
			if ("no".equals(oValue) || "false".equals(oValue)) {
				this.opaque = false;
			}
		}
		this.createComponent();
		this.processLabel(parameters);

	}

	/**
	 * Creates the component. Creates the {@link JEditorPane},
	 * {@link LinkComponent}{@link #setLayout(LayoutManager)} and adds scroll.
	 */
	public void createComponent() {
		this.editorPane = new JEditorPane("text/html", "");
		this.editorPane.setOpaque(this.opaque);
		this.setLayout(new BorderLayout());
		if (this.scroll) {
			this.add(new JScrollPane(this.editorPane));
		} else {
			this.add(this.editorPane);
		}
	}

	/**
	 * Processes the label and supports a table and insert mode in detail form.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters. Possible values for
	 *        <code>Hashtable</code> are:
	 *        <p>
	 *
	 *        <TABLE BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *
	 *        <tr>
	 *        <td>Key</td>
	 *        <td>Value</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>label</td>
	 *        <td><i>label1;label2;...;labeln</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>hlabel</td>
	 *        <td><i>hlabel1;hlabel2;...;hlabeln</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>table</td>
	 *        <td><i>table1;table2;...;tablen</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>insertmode</td>
	 *        <td><i>insertmode1;insertmode2;...;insertmoden</td>
	 *        </tr>
	 *        </TABLE>
	 *
	 */
	protected void processLabel(final Map parameters) {
		StringTokenizer stLabels = null;
		StringTokenizer stLinks = null;
		StringTokenizer stTables = null;
		StringTokenizer stInsertMode = null;
		if (parameters.containsKey("label")) {
			stLabels = new StringTokenizer((String) parameters.get("label"), ";");
		}
		if (parameters.containsKey("hlabel")) {
			stLinks = new StringTokenizer((String) parameters.get("hlabel"), ";");
		}
		if (parameters.containsKey("table")) {
			stTables = new StringTokenizer((String) parameters.get("table"), ";");
		}
		if (parameters.containsKey("insertmode")) {
			stInsertMode = new StringTokenizer((String) parameters.get("insertmode"), ";");
		}

		String sContent = new String();
		sContent = sContent.concat("<HTML><BODY>");
		if (stLabels != null) {
			while (stLabels.hasMoreTokens()) {
				final String sText = stLabels.nextToken();
				sContent = sContent.concat(" " + ApplicationManager.getTranslation(sText, this.bundle) + " ");
				this.labelVector.add(sText);
				if ((stLinks != null) && stLinks.hasMoreTokens()) {
					final String sLinkText = stLinks.nextToken();
					this.linkVector.add(sLinkText);
					sContent = sContent.concat("<A HREF=\"" + sLinkText + "\">"
							+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
					if ((stTables != null) && stTables.hasMoreTokens()) {
						final String k = stTables.nextToken();
						this.tableVector.add(k.toString());
					}
					if ((stInsertMode != null) && stInsertMode.hasMoreTokens()) {
						final String k = stInsertMode.nextToken();
						this.insertModeVector.add(k);
					}
				}
			}
		}
		if (stLinks != null) {
			while (stLinks.hasMoreTokens()) {
				final String sLinkText = stLinks.nextToken();
				this.linkVector.add(ApplicationManager.getTranslation(sLinkText, this.bundle));
				sContent = sContent.concat(
						"<A HREF=\"" + ApplicationManager.getTranslation(sLinkText, this.bundle) + "\">"
								+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
				if ((stTables != null) && stTables.hasMoreTokens()) {
					final String k = stTables.nextToken();
					this.tableVector.add(k.toString());
				}
				if ((stInsertMode != null) && stInsertMode.hasMoreTokens()) {
					final String k = stInsertMode.nextToken();
					this.insertModeVector.add(k);
				}
			}
		}
		sContent = sContent.concat("</BODY></HTML>");
		try {
			this.editorPane.setText(sContent);
			this.editorPane.setEditable(false);

			this.editorPane.addHyperlinkListener(new HyperlinkListener() {

				@Override
				public void hyperlinkUpdate(final HyperlinkEvent hEvent) {
					if (LinkComponent.this.enabled) {
						if (hEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							final String sEnableLink = hEvent.getDescription();
							final int pos = LinkComponent.this.linkVector.indexOf(sEnableLink);
							if ((pos < LinkComponent.this.tableVector.size())
									&& (pos < LinkComponent.this.insertModeVector.size())) {
								final Table tSource = (Table) LinkComponent.this.parentForm
										.getElementReference((String) LinkComponent.this.tableVector.get(pos));
								if (tSource != null) {

									if (((String) LinkComponent.this.insertModeVector.get(pos))
											.equalsIgnoreCase("yes")) {
										tSource.openInsertDetailForm();
									} else {
										if (tSource.getSelectedRows().length == 1) {
											final int rowNumber = tSource.getSelectedRow();
											tSource.openDetailForm(rowNumber);
										} else {
											LinkComponent.this.parentForm.message("LinkComponent.SelectTableRecord",
													Form.INFORMATION_MESSAGE);
										}
									}
								} else {
									LinkComponent.this.parentForm.message("LinkComponent.TableNotFound",
											Form.INFORMATION_MESSAGE);
								}
							}
						}
					}
				}
			});
		} catch (final Exception e) {
			LinkComponent.logger.trace(null, e);
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		this.bundle = res;
		String sContent = new String();
		sContent = sContent.concat("<HTML><BODY>");
		int i = 0;
		int j = 0;
		while (i < this.labelVector.size()) {
			final String sText = (String) this.labelVector.get(i);
			sContent = sContent.concat(" " + ApplicationManager.getTranslation(sText, this.bundle) + " ");
			if (j < this.linkVector.size()) {
				final String sLinkText = (String) this.linkVector.get(j);
				if (this.enabled) {
					sContent = sContent.concat("<A HREF=\"" + sLinkText + "\">"
							+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
				} else {
					sContent = sContent.concat("<A COLOR=\"000000\" HREF=\"" + sLinkText + "\">"
							+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
				}
				j += 1;
			}
			i += 1;
		}
		while (j < this.linkVector.size()) {
			final String sLinkText = (String) this.linkVector.get(j);
			if (this.enabled) {
				sContent = sContent.concat("<A HREF=\"" + sLinkText + "\">"
						+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
			} else {
				sContent = sContent.concat("<A COLOR=\"000000\" HREF=\"" + sLinkText + "\">"
						+ ApplicationManager.getTranslation(sLinkText, this.bundle) + "</A>");
			}
			j += 1;
		}
		sContent = sContent.concat("</BODY></HTML>");
		this.editorPane.setText(sContent);
	}

	/**
	 * Sets the text for field.
	 * <p>
	 * @param text the text to set
	 */
	public void setText(final String text) {
		if (text.equals("")) {
			this.editorPane.setText("");
			this.labelVector.clear();
		} else {
			String sProcessedText = "<HTML><BODY>";
			sProcessedText = sProcessedText.concat(ApplicationManager.getTranslation(text, this.bundle));
			sProcessedText = sProcessedText.concat("</BODY></HTML>");
			this.editorPane.setText(sProcessedText);
			this.labelVector.clear();
			this.labelVector.add(text);
		}
	}

	/**
	 * Adds label to label vector.
	 * <p>
	 * @param text the text to add to the label
	 */
	public void addLabel(final String text) {
		if (text.equals("")) {
			return;
		}
		this.labelVector.add(text);
		this.setResourceBundle(this.bundle);
	}

	/**
	 * @param data
	 */
	public void addHyperText(final Map data) {
		String sText = "";
		String sTable = "";
		String insertmode = "";
		if (data.containsKey("text")) {
			sText = (String) data.get("text");
		}
		if (sText.equals("")) {
			return;
		}
		if (data.containsKey("table")) {
			sTable = (String) data.get("table");
		}
		if (data.containsKey("insertmode")) {
			insertmode = (String) data.get("insertmode");
		}
		if (!sTable.equals("")) {
			this.tableVector.add(sTable);
		}
		if (!insertmode.equals("")) {
			this.insertModeVector.add(insertmode);
		}
		this.linkVector.add(sText);
		this.setResourceBundle(this.bundle);
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		this.enabled = enabled;
		if (enabled) {
			String sText = this.editorPane.getText();
			sText = sText.replaceAll(" color=\"000000\"", "");
			this.editorPane.setText(sText);
			this.editorPane.setEnabled(true);
		} else {
			String sText = this.editorPane.getText();
			sText = sText.replaceAll("<a ", "<a color=\"000000\" ");
			this.editorPane.setText(sText);
			this.editorPane.setEnabled(false);
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * This class creates a editor pane with <code>text/html</code> mime type and sets opaque.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class LinkEditorPane extends JEditorPane {

		/**
		 * The class constructor.
		 */
		public LinkEditorPane() {
			super("text/html", "");
			this.setOpaque(true);
		}

	}

}
