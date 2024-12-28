package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.FindDialog;
import com.ontimize.gui.MessageDialog;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.text.XMLEditorKit;

/**
 * This class implements a {@link MemoDataField} extension indicated to XML syntax.
 * <p>
 *
 * @author Imatia Innovation
 */
public class XMLDataField extends MemoDataField {

	private static final Logger logger = LoggerFactory.getLogger(XMLDataField.class);

	/**
	 * For debugging mode. By default, false.
	 */
	public static boolean DEBUG = false;

	/**
	 * A reference to document factory. By default, null.
	 */
	protected DocumentBuilderFactory docBFactory = null;

	/**
	 * A reference to document builder. By default, null.
	 */
	protected DocumentBuilder docB = null;

	/**
	 * An instance for error text attribute.
	 */
	protected SimpleAttributeSet errorTextAttribute = new SimpleAttributeSet();

	/**
	 * An instance for OK text attribute.
	 */
	protected SimpleAttributeSet oKTextAttribute = new SimpleAttributeSet();

	/**
	 * The key for XML analyse.
	 */
	public static String analyzeKey = "xmldatafield.analyse_xml";

	/**
	 * An instance of analyze menu item.
	 */
	protected JMenuItem analyzeMenu = new JMenuItem(XMLDataField.analyzeKey);

	protected JMenuItem indentItem;

	public static String indentKey = "xmldatafield.indent_xml";

	protected boolean format;

	protected int indentSize;

	/**
	 * The class constructor. Calls to <code>super()</code> with parameters. This field has not
	 * additional XML parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 * @throws Exception when an Exception occurs
	 */
	public XMLDataField(final Map parameters) throws Exception {
		super(parameters);
		// GridBagLayout l = (GridBagLayout)this.getLayout();
		// Object constraints = l.getConstraints(scroll);
		this.scroll.setPreferredSize(new Dimension(this.dataField.getPreferredSize().width,
				this.dataField.getFontMetrics(this.dataField.getFont()).getHeight() * this.rows));
		this.docBFactory = DocumentBuilderFactory.newInstance();
		this.docB = this.docBFactory.newDocumentBuilder();
		StyleConstants.setBackground(this.errorTextAttribute, Color.red.brighter());
		StyleConstants.setForeground(this.errorTextAttribute, Color.black);
		if (this.fontColor != null) {
			StyleConstants.setForeground(this.oKTextAttribute, this.fontColor);
		}
		((JTextPane) this.dataField).getDocument().putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
	}

	@Override
	public void init(final Map params) {
		super.init(params);
		this.format = ParseUtils.getBoolean((String) params.get("format"), false);
		this.indentSize = ParseUtils.getInteger((String) params.get("identsize"), 4);
		this.indentItem = new JMenuItem(XMLDataField.indentKey);
		this.indentItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				((JTextComponent) XMLDataField.this.dataField)
				.setText(XMLDataField.this.doFormat((String) XMLDataField.this.getValue()));
			}
		});
	}

	@Override
	protected void createPopupMenu() {
		if (this.popupMenu == null) {
			super.createPopupMenu();
			try {
				this.analyzeMenu.setText(this.resources.getString(XMLDataField.analyzeKey));
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					XMLDataField.logger.debug(null, e);
				} else {
					XMLDataField.logger.trace(null, e);
				}
			}
			this.popupMenu.addSeparator();
			this.popupMenu.add(this.analyzeMenu);
			this.analyzeMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					XMLDataField.this.showErrors();
				}
			});

			if (this.indentItem != null) {
				this.popupMenu.addSeparator();
				this.popupMenu.add(this.indentItem);
			}
		}
	}

	@Override
	protected void showPopupMenu(final Component s, final int x, final int y) {
		if (this.isEmpty()) {
			this.analyzeMenu.setEnabled(false);
		} else {
			this.analyzeMenu.setEnabled(true);
		}
		super.showPopupMenu(s, x, y);
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (this.analyzeMenu != null) {
			try {
				this.analyzeMenu.setText(this.resources.getString(XMLDataField.analyzeKey));
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					XMLDataField.logger.debug(null, e);
				} else {
					XMLDataField.logger.trace(null, e);
				}
			}
		}

		if (this.indentItem != null) {
			this.indentItem.setText(ApplicationManager.getTranslation(XMLDataField.indentKey, res));
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		v.add(XMLDataField.analyzeKey);
		v.add(XMLDataField.indentKey);
		return v;
	}

	@Override
	public void setValue(final Object value) {
		super.setValue(this.format ? this.doFormat((String) value) : value);
	}

	/**
	 * Checks the field errors.
	 * <p>
	 * @return the error condition
	 */
	public boolean hasErrors() {

		if (this.isEmpty()) {
			return false;
		} else {
			this.setOkAttributes(0, ((JTextComponent) this.dataField).getText().length());
			ByteArrayInputStream bIn = null;
			try {
				bIn = new ByteArrayInputStream(((JTextComponent) this.dataField).getText().getBytes());
				this.docB.parse(bIn);

				return false;
			} catch (final IOException e) {
				XMLDataField.logger.error(null, e);
				return true;
			} catch (final SAXException e) {
				XMLDataField.logger.error(null, e);
				return true;
			}

			finally {
				if (bIn != null) {
					try {
						bIn.close();
					} catch (final Exception e) {
						XMLDataField.logger.trace(null, e);
					}
				}
			}
		}
	}

	/**
	 * Parses XML and shows incorrect buildings.
	 * <p>
	 * @return the correct or incorrect parsed condition
	 */
	public boolean showErrors() {

		if (this.isEmpty()) {
			return false;
		} else {
			this.setOkAttributes(0, ((JTextComponent) this.dataField).getText().length());
			ByteArrayInputStream bIn = null;
			try {
				bIn = new ByteArrayInputStream(((JTextComponent) this.dataField).getText().getBytes());
				this.docB.parse(bIn);
				return false;
			} catch (final Exception e) {
				XMLDataField.logger.error(null, e);
				int line = -1;
				int column = -1;
				if (e instanceof SAXParseException) {
					column = ((SAXParseException) e).getColumnNumber();
					line = ((SAXParseException) e).getLineNumber();
					if (line < 0) {
						line = 0;
					}
					final int iBeginOffset = this.getStartLineOffset(line);
					int iEndOffset = this.getEndLineOffset(line);
					if (iEndOffset <= iBeginOffset) {
						iEndOffset = iBeginOffset + 1;
					}
					if (column >= 0) {
						this.setErrorAttributes(iBeginOffset + Math.max(0, column), 2);
					} else {
						this.setErrorAttributes(iBeginOffset, iEndOffset - iBeginOffset);
					}
				}
				String message = null;
				if (line >= 0) {
					message = "XML is incorrect. Line: " + line + ". Column: " + column;
				} else {
					message = "XML is incorrect.";
				}
				final Window w = SwingUtilities.getWindowAncestor(this);
				if (w instanceof Dialog) {
					MessageDialog.showMessage((Dialog) w, message, e.getMessage(), JOptionPane.ERROR_MESSAGE,
							this.resources);
				} else {
					MessageDialog.showMessage((Frame) w, message, e.getMessage(), JOptionPane.ERROR_MESSAGE,
							this.resources);
				}
				return true;
			} finally {
				try {
					if (bIn != null) {
						bIn.close();
					}
				} catch (final Exception e) {
					XMLDataField.logger.trace(null, e);
				}
			}

		}
	}

	/**
	 * Gets the start line offset.
	 * <p>
	 * @param line the line to get start line offset
	 * @return the line offset value
	 */
	public int getStartLineOffset(final int line) {
		int offset = 0;
		int currentLine = 1;
		final String sText = ((JTextComponent) this.dataField).getText();
		for (int i = 0; i < sText.length(); i++) {
			if (sText.charAt(i) == '\n') {
				currentLine++;
			}
			if (currentLine >= line) {
				offset = i + 1;
				break;
			}
		}
		if (XMLDataField.DEBUG) {
			// Offset at the beginning of the line
			XMLDataField.logger.debug("Offset at the beginning of the line: " + line + ":" + offset);
		}
		return offset;
	}

	/**
	 * Gets the end line offset.
	 * <p>
	 * @param line the line to get end line offset
	 * @return the line offset value
	 */
	public int getEndLineOffset(final int line) {
		int offset = 0;
		int currentLine = 1;
		final String sText = ((JTextComponent) this.dataField).getText();
		for (int i = 0; i < sText.length(); i++) {
			if (sText.charAt(i) == '\n') {
				currentLine++;
			}
			if (currentLine > line) {
				offset = i;
				break;
			}
		}
		if (XMLDataField.DEBUG) {
			XMLDataField.logger.debug("Offset at the end of the line: " + line + ":" + offset);
		}
		return offset;
	}

	/**
	 * Sets the error attributes.
	 * <p>
	 * @param offsetIni the initial offset position
	 * @param length the length
	 */
	protected void setErrorAttributes(final int offsetIni, final int length) {
		try {
			final StyledDocument doc = ((JTextPane) this.dataField).getStyledDocument();
			if (offsetIni >= (doc.getLength() - 1)) {
				return;
			}
			doc.setCharacterAttributes(offsetIni, Math.min(doc.getLength() - offsetIni, length),
					this.errorTextAttribute, true);
		} catch (final Exception e) {
			XMLDataField.logger.error(null, e);
		}
	}

	/**
	 * Sets the OK attributes.
	 * <p>
	 * @param offsetIni the initial offset position
	 * @param lenght the length
	 */
	protected void setOkAttributes(final int offsetIni, final int lenght) {
		try {
			final StyledDocument doc = ((JTextPane) this.dataField).getStyledDocument();
			doc.setCharacterAttributes(offsetIni, Math.min(doc.getLength() - offsetIni, lenght), this.oKTextAttribute,
					true);
		} catch (final Exception e) {
			XMLDataField.logger.error(null, e);
		}
	}

	@Override
	protected void createDataField() {
		this.dataField = new JTextPane() {

			@Override
			protected void processKeyEvent(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_F3) && (e.getID() == KeyEvent.KEY_RELEASED)) {
					if (XMLDataField.this.dQuery == null) {
						XMLDataField.this.dQuery = new FindDialog(XMLDataField.this.parentFrame,
								(JTextPane) XMLDataField.this.dataField);
						XMLDataField.this.dQuery.setResourceBundle(XMLDataField.this.resources);
						XMLDataField.this.dQuery.setComponentLocale(XMLDataField.this.locale);
						XMLDataField.this.dQuery
						.show(((JTextComponent) XMLDataField.this.dataField).getCaretPosition());
					} else {
						if (XMLDataField.this.dQuery != null) {
							XMLDataField.this.dQuery
							.find(((JTextComponent) XMLDataField.this.dataField).getCaretPosition());
						}
					}
					e.consume();
					return;
				}
				super.processKeyEvent(e);
			}

			@Override
			public void setText(final String t) {
				super.setText(t);
				this.setCaretPosition(0);
			}
		};

		((JTextPane) this.dataField).setEditorKitForContentType(XMLEditorKit.XML_CONTENT_TYPE, new XMLEditorKit());
		((JTextPane) this.dataField).setContentType(XMLEditorKit.XML_CONTENT_TYPE);
	}

	protected String getIndentText() {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.indentSize; i++) {
			sb.append(" ");
		}
		return sb.toString();
	}

	/**
	 * Reflow XML
	 */
	public String doFormat(final String xml) {
		final String indentText = this.getIndentText();
		final List parts = new Vector();
		final char[] chars = xml.toCharArray();
		int index = 0;
		int first = 0;
		String part = null;
		while (index < chars.length) {
			// Check for start of tag
			if (chars[index] == '<') {
				// Did we have data before this tag?
				if (first < index) {
					part = new String(chars, first, index - first);
					part = part.trim();
					// Save non-whitespace data
					if (part.length() > 0) {
						parts.add(part);
					}
				}
				// Save the start of tag
				first = index;
			}
			// Check for end of tag
			if (chars[index] == '>') {
				// Save the tag
				part = new String(chars, first, (index - first) + 1);
				parts.add(part);
				first = index + 1;
			}
			// Check for end of line
			if ((chars[index] == '\n') || (chars[index] == '\r')) {
				// Was there data on this line?
				if (first < index) {
					part = new String(chars, first, index - first);
					part = part.trim();
					// Save non-whitespace data
					if (part.length() > 0) {
						parts.add(part);
					}
				}
				first = index + 1;
			}
			index++;
		}
		// Reflow as XML
		final StringBuilder buf = new StringBuilder();
		final Object[] list = parts.toArray();
		int indent = 0;
		int pad = 0;
		index = 0;
		while (index < list.length) {
			part = (String) list[index];
			if (buf.length() == 0) {
				// Just add first tag (should be XML header)
				buf.append(part);
			} else {
				// All other parts need to start on a new line
				buf.append('\n');
				// If we're at an end tag then decrease indent
				if (part.startsWith("</")) {
					indent--;
				}
				// Add any indent
				for (pad = 0; pad < indent; pad++) {
					buf.append(indentText);
				}
				// Add the tag or data
				buf.append(part);
				// If this is a start tag then increase indent
				if (part.startsWith("<") && !part.startsWith("</") && !part.endsWith("/>")
						&& !part.startsWith("<!--")) {
					indent++;
					// Check for special <tag>data</tag> case
					if ((index + 2) < list.length) {
						part = (String) list[index + 2];
						if (part.startsWith("</")) {
							part = (String) list[index + 1];
							if (!part.startsWith("<")) {
								buf.append(part);
								part = (String) list[index + 2];
								buf.append(part);
								index = index + 2;
								indent--;
							}
						}
					}
				}
			}
			index++;
		}
		return new String(buf);
	}

}
