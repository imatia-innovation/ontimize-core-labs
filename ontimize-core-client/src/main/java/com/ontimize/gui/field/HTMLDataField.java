package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLWriter;
import javax.swing.text.html.MinimalHTMLWriter;
import javax.swing.text.html.StyleSheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.FindDialog;
import com.ontimize.gui.FormatText;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.border.SoftButtonBorder;
import com.ontimize.util.templates.ITemplateField;

/**
 * Main class to implement a specific HTML data field.
 * <p>
 *
 * @author Imatia Innovation
 */

public class HTMLDataField extends MemoDataField implements AdvancedDataComponent, ITemplateField {

	/**
	 * Variable used to expand the toolbar or not. It it is false, it means that toolbar is full
	 * expanded. By default it is false.
	 */
	private static final Logger logger = LoggerFactory.getLogger(HTMLDataField.class);

	public static boolean toolBarFiller = false;

	protected static final String BOLD = "bold";

	protected static final String ITALIC = "italic";

	protected static final String UNDERLINE = "underline";

	protected static final String HTML_BASE = "<html><head></head><body></body></html>";

	protected static final String TAB = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

	protected int minFontSize;

	protected int maxFontSize;

	protected int fontSizeStep;

	protected boolean opaquebuttons;

	protected boolean borderbuttons;

	protected MouseListener listenerHighlightButtons;

	protected int lastSelectedFontSize;

	/**
	 * Main class to manage correctly the field color.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class ExtForegroundAction extends StyledEditorKit.StyledTextAction {

		public ExtForegroundAction() {
			super("color");
		}

		@Override
		public void actionPerformed(final ActionEvent e) {

			final JEditorPane editor = this.getEditor(e);
			if (editor != null) {
				Color cIni = Color.black;
				final Element element = this.getStyledDocument(editor).getCharacterElement(editor.getSelectionStart());
				if (element != null) {
					cIni = StyleConstants.getForeground(element.getAttributes());
				}
				final Color c = JColorChooser.showDialog(editor,
						ApplicationManager.getTranslation("ColorDataField.chooseColor"), cIni);
				if (c != null) {
					final MutableAttributeSet attr = new SimpleAttributeSet();
					StyleConstants.setForeground(attr, c);
					this.setCharacterAttributes(editor, attr, false);
				} else {
				}
			}
		}

	}

	/**
	 * Class to adapt the style field.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class ActionWrapper implements ActionListener {

		protected StyledEditorKit.StyledTextAction act = null;

		public ActionWrapper(final StyledEditorKit.StyledTextAction a) {
			this.act = a;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			HTMLDataField.this.valueSave = HTMLDataField.this.getValue();
			this.act.actionPerformed(e);
			HTMLDataField.this.fireValueChanged(HTMLDataField.this.getValue(), HTMLDataField.this.valueSave,
					ValueEvent.USER_CHANGE);
		}

	}

	protected static class ToggleButton extends JToggleButton {

		protected int iHigh = 22;

		public ToggleButton() {
			this.setBorder(new SoftButtonBorder());
		}

		@Override
		public void updateUI() {
			super.updateUI();
			this.setBorder(new SoftButtonBorder());
		}

		@Override
		public boolean isRequestFocusEnabled() {
			return false;
		}

		@Override
		public boolean isFocusTraversable() {
			return false;
		}

		public boolean isDefaultCapable() {
			return false;
		}

		@Override
		public Dimension getMinimumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getMaximumSize() {
			return this.getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			d.height = this.iHigh;
			return d;
		}

	}

	protected JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)) {

		protected static final String name = "TableButtonPanel";

		@Override
		public String getName() {
			return name;
		};
	};

	protected JToggleButton boldBt = new ToggleButton();

	protected JToggleButton italicBt = new ToggleButton();

	protected JToggleButton underlineBt = new ToggleButton();

	protected JToggleButton leftAlignBt = new ToggleButton();

	protected JToggleButton centerAlignBt = new ToggleButton();

	protected JToggleButton rightAlignBt = new ToggleButton();

	private JComboBox fontsizeCombo = null;

	protected JButton colorButton = new DataField.FieldButton() {

		@Override
		public boolean isRequestFocusEnabled() {
			return false;
		}

		@Override
		public boolean isFocusTraversable() {
			return false;
		}
	};

	protected StyleContext context = new StyleContext();

	protected Map styles = new Hashtable();

	/**
	 * Overwrite to avoid break line in the Writer
	 */

	protected HTMLEditorKit editor = new HTMLEditorKit() {

		class CustomHTMLWriter extends HTMLWriter {

			public CustomHTMLWriter(final Writer w, final HTMLDocument doc, final int pos, final int len) {
				super(w, doc, pos, len);
				this.setLineLength(Integer.MAX_VALUE);
			}

		}

		@Override
		public void write(final Writer out, final Document doc, final int pos, final int len) throws IOException, BadLocationException {
			if (doc instanceof HTMLDocument) {
				final CustomHTMLWriter w = new CustomHTMLWriter(out, (HTMLDocument) doc, pos, len);
				w.write();
			} else if (doc instanceof StyledDocument) {
				final MinimalHTMLWriter w = new MinimalHTMLWriter(out, (StyledDocument) doc, pos, len);
				w.write();
			} else {
				super.write(out, doc, pos, len);
			}
		}

	};

	protected String plainTextColumn = null;

	protected boolean advancedQueryMode = false;

	protected class AlignListener implements ActionListener {

		public AlignListener() {

		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JTextComponent tc = (JTextComponent) HTMLDataField.this.dataField;
			final int start = tc.getSelectionStart();
			int end = tc.getSelectionEnd();
			if (start == end) {
				end = start + 1;
			}
			// Now check if this is true or false
			final Object source = e.getSource();
			final Style s = HTMLDataField.this.context.addStyle(null, null);
			if (source == HTMLDataField.this.leftAlignBt) {
				StyleConstants.setAlignment(s, StyleConstants.ALIGN_LEFT);
			} else if (source == HTMLDataField.this.centerAlignBt) {
				StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
			} else {
				StyleConstants.setAlignment(s, StyleConstants.ALIGN_RIGHT);
			}
			HTMLDataField.this.setParagraphAttributes(start, end - start, s, false);
		}

	}

	protected AlignListener alignListener = new AlignListener();

	protected ButtonGroup alignButtonGroup = new ButtonGroup();

	/**
	 * Initializes parameters. Configure buttons and adds key events. XML definition could be contains
	 * an optional parameter: 'plaintextcolumn' specifying the identifier to save the text in plain
	 * format.
	 * <p>
	 * @param parameters the Map with parameters
	 */

	public HTMLDataField(final Map parameters) {
		super(parameters);
		this.createStyles();
		this.configureButtonsPanelAndEditor();
		this.installButtonsHandler();
		this.dataField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_SPACE) && e.isControlDown() && e.isAltDown()) {
					MessageDialog.showMessage(HTMLDataField.this.parentFrame, (String) HTMLDataField.this.getValue(),
							JOptionPane.INFORMATION_MESSAGE, null);
				}
			}
		});
		this.plainTextColumn = (String) parameters.get("plaintextcolumn");
	}

	@Override
	public void init(final Map params) {
		super.init(params);
		this.minFontSize = ParseUtils.getInteger((String) params.get("minfontsize"), 10);
		this.maxFontSize = ParseUtils.getInteger((String) params.get("maxfontsize"), 44);
		this.fontSizeStep = ParseUtils.getInteger((String) params.get("fontsizestep"), 2);

		this.borderbuttons = ParseUtils.getBoolean((String) params.get("borderbuttons"), true);
		this.opaquebuttons = ParseUtils.getBoolean((String) params.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) params.get("highlightbuttons"), false);

		if (highlightButtons) {
			this.listenerHighlightButtons = new MouseAdapter() {

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
	}

	/**
	 * Creates a combo with height fonts <code>minFontSize</code> to <code>maxFontSize</code>.
	 */
	protected void createFontSizeCombo() {
		final Vector vData = new Vector();
		for (int i = this.minFontSize; i <= this.maxFontSize; i = i + this.fontSizeStep) {
			vData.add(new Integer(i));
		}

		this.fontsizeCombo = new JComboBox(vData) {

			@Override
			public boolean isRequestFocusEnabled() {
				return false;
			}

			@Override
			public boolean isFocusTraversable() {
				return false;
			}
		};

	}

	/**
	 * Installs the button handler to manage buttons for document.
	 */
	protected void installButtonsHandler() {
		// Install the document listener to manage the buttons

		((JTextComponent) this.dataField).addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(final CaretEvent e) {
				final int dot = e.getDot();
				if ((dot < 0) || (dot >= ((JTextComponent) HTMLDataField.this.dataField).getDocument().getLength())) {
					return;
				}
				final Element element = ((StyledDocument) ((JTextComponent) HTMLDataField.this.dataField).getDocument())
						.getCharacterElement(dot);
				if (element != null) {
					final AttributeSet at = element.getAttributes();
					if (at != null) {
						HTMLDataField.this.boldBt.setSelected(StyleConstants.isBold(at));
						HTMLDataField.this.italicBt.setSelected(StyleConstants.isItalic(at));
						HTMLDataField.this.underlineBt.setSelected(StyleConstants.isUnderline(at));
					}
				}

				final Element pElement = ((StyledDocument) ((JTextComponent) HTMLDataField.this.dataField).getDocument())
						.getParagraphElement(dot);
				if (pElement != null) {
					final AttributeSet at = pElement.getAttributes();
					if (at != null) {
						final int al = StyleConstants.getAlignment(at);
						switch (al) {
						case StyleConstants.ALIGN_LEFT:
							HTMLDataField.this.leftAlignBt.setSelected(true);
							break;
						case StyleConstants.ALIGN_RIGHT:
							HTMLDataField.this.rightAlignBt.setSelected(true);
							break;
						case StyleConstants.ALIGN_CENTER:
							HTMLDataField.this.centerAlignBt.setSelected(true);
							break;
						default:
							HTMLDataField.this.leftAlignBt.setSelected(true);
							break;
						}
					}
				}
			}
		});

	}

	@Override
	public void setEnabled(final boolean en) {
		super.setEnabled(en);
		if (en) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				if (this.boldBt != null) {
					this.boldBt.setEnabled(en);
				}
				if (this.italicBt != null) {
					this.italicBt.setEnabled(en);
				}
				if (this.underlineBt != null) {
					this.underlineBt.setEnabled(en);
				}
				if (this.fontsizeCombo != null) {
					this.fontsizeCombo.setEnabled(en);
				}
				if (this.colorButton != null) {
					this.colorButton.setEnabled(en);
				}
				if (this.leftAlignBt != null) {
					this.leftAlignBt.setEnabled(en);
				}
				if (this.centerAlignBt != null) {
					this.centerAlignBt.setEnabled(en);
				}
				if (this.rightAlignBt != null) {
					this.rightAlignBt.setEnabled(en);
				}
			} else {
				if (this.boldBt != null) {
					this.boldBt.setEnabled(false);
				}
				if (this.italicBt != null) {
					this.italicBt.setEnabled(false);
				}
				if (this.underlineBt != null) {
					this.underlineBt.setEnabled(false);
				}
				if (this.fontsizeCombo != null) {
					this.fontsizeCombo.setEnabled(false);
				}
				if (this.colorButton != null) {
					this.colorButton.setEnabled(false);
				}
				if (this.leftAlignBt != null) {
					this.leftAlignBt.setEnabled(false);
				}
				if (this.centerAlignBt != null) {
					this.centerAlignBt.setEnabled(false);
				}
				if (this.rightAlignBt != null) {
					this.rightAlignBt.setEnabled(false);
				}
			}
		} else {
			if (this.boldBt != null) {
				this.boldBt.setEnabled(false);
			}
			if (this.italicBt != null) {
				this.italicBt.setEnabled(false);
			}
			if (this.underlineBt != null) {
				this.underlineBt.setEnabled(false);
			}
			if (this.fontsizeCombo != null) {
				this.fontsizeCombo.setEnabled(false);
			}
			if (this.colorButton != null) {
				this.colorButton.setEnabled(false);
			}
			if (this.leftAlignBt != null) {
				this.leftAlignBt.setEnabled(false);
			}
			if (this.centerAlignBt != null) {
				this.centerAlignBt.setEnabled(false);
			}
			if (this.rightAlignBt != null) {
				this.rightAlignBt.setEnabled(false);
			}
		}
	}

	/**
	 *
	 */
	protected void configureButtonsPanelAndEditor() {
		// this.boldBt.addActionListener(new StyleListener(BOLD));
		this.boldBt.addActionListener(new ActionWrapper(new StyledEditorKit.BoldAction()));
		this.italicBt.addActionListener(new ActionWrapper(new StyledEditorKit.ItalicAction()));
		this.underlineBt.addActionListener(new ActionWrapper(new StyledEditorKit.UnderlineAction()));

		this.boldBt.setIcon(ImageManager.getIcon(ImageManager.BOLD_FONT));
		this.italicBt.setIcon(ImageManager.getIcon(ImageManager.ITALIC_FONT));
		this.underlineBt.setIcon(ImageManager.getIcon(ImageManager.UNDERLINE_FONT));

		// Add to the panel
		this.buttonsPanel.add(this.boldBt);
		this.buttonsPanel.add(this.italicBt);
		this.buttonsPanel.add(this.underlineBt);

		// Buttons alignment
		this.leftAlignBt.addActionListener(this.alignListener);
		this.centerAlignBt.addActionListener(this.alignListener);
		this.rightAlignBt.addActionListener(this.alignListener);

		this.alignButtonGroup.add(this.leftAlignBt);
		this.alignButtonGroup.add(this.centerAlignBt);
		this.alignButtonGroup.add(this.rightAlignBt);

		this.leftAlignBt.setIcon(ImageManager.getIcon(ImageManager.LEFT_ALIGN));
		this.centerAlignBt.setIcon(ImageManager.getIcon(ImageManager.CENTER_ALIGN));
		this.rightAlignBt.setIcon(ImageManager.getIcon(ImageManager.RIGHT_ALIGN));

		// Add to the panel
		this.buttonsPanel.add(this.leftAlignBt);
		this.buttonsPanel.add(this.centerAlignBt);
		this.buttonsPanel.add(this.rightAlignBt);

		this.createFontSizeCombo();
		this.buttonsPanel.add(this.fontsizeCombo);

		this.colorButton.setIcon(ImageManager.getIcon(ImageManager.CHOOSE_COLOR));

		this.buttonsPanel.add(this.colorButton);

		this.changeButton(this.colorButton, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.rightAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.leftAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.centerAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.boldBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.italicBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.underlineBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);

		this.colorButton.addActionListener(new ExtForegroundAction());

		this.fontsizeCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Integer iFontSize = (Integer) HTMLDataField.this.fontsizeCombo.getSelectedItem();
				final JTextComponent tc = (JTextComponent) HTMLDataField.this.dataField;
				final int start = tc.getSelectionStart();
				int end = tc.getSelectionEnd();
				if (start == end) {
					end = start + 1;
				}

				HTMLDataField.this.setTextFontSize(start, end - start, iFontSize.intValue());
			}
		});

		if (this.dataField != null) {
			this.installSelectionTextListener();
		}

		// Add the panel to the field. The main problem is put it in the correct
		// position
		final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.scroll);
		constraints.gridx = 2;
		constraints.gridy = constraints.gridy + 1;

		final GridBagConstraints buttonsPanelConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
		if (this.labelPosition == SwingConstants.TOP) {
			buttonsPanelConstraints.gridy = 1;
		}
		if (HTMLDataField.toolBarFiller) {
			buttonsPanelConstraints.fill = 0;
		}

		final GridBagConstraints labelConstraint = ((GridBagLayout) this.getLayout()).getConstraints(this.labelComponent);
		if ((this.labelPosition == SwingConstants.LEFT) || (this.labelPosition == SwingConstants.RIGHT)) {
			labelConstraint.gridheight = 3;
		} else if (this.labelPosition == SwingConstants.BOTTOM) {
			labelConstraint.gridy = 3;
		}

		this.remove(this.scroll);
		this.remove(this.labelComponent);

		this.add(this.buttonsPanel, buttonsPanelConstraints);
		this.add(this.labelComponent, labelConstraint);
		this.add(this.scroll, constraints);
		this.scroll.setPreferredSize(new Dimension(this.dataField.getPreferredSize().width,
				this.dataField.getFontMetrics(this.dataField.getFont()).getHeight() * this.rows));

	}

	protected Font getSelectedTextFont(final int offset, final int length) {
		final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
		return doc.getFont(doc.getCharacterElement(offset).getAttributes());
	}

	protected void installSelectionTextListener() {
		((JTextComponent) this.dataField).addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(final CaretEvent e) {
				final Font fontTextSelected = HTMLDataField.this.getSelectedTextFont(e.getMark(), e.getDot());
				if (e.getMark() == e.getDot()) {
					if (fontTextSelected.getSize() != HTMLDataField.this.lastSelectedFontSize) {
						HTMLDataField.this.fontsizeCombo.setSelectedItem(new Integer(fontTextSelected.getSize()));
						HTMLDataField.this.lastSelectedFontSize = fontTextSelected.getSize();
					}
				}
			}
		});
	}

	@Override
	public void setValue(final Object value) {
		if (value == null) {
			this.deleteData();
			return;
		} else {
			final Object oPreviousValue = this.getValue();
			final String v = value.toString();
			final StringReader sr = new StringReader(v);
			try {
				this.enableInnerListener(false);
				((JTextComponent) this.dataField).setText("");
				if (!v.equals(HTMLDataField.HTML_BASE)) {
					this.editor.read(sr, ((JTextComponent) this.dataField).getDocument(), 0);
				}
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} catch (final Exception e) {
				HTMLDataField.logger.debug(null, e);
			} finally {
				this.enableInnerListener(true);
				try {
					sr.close();
				} catch (final Exception e) {
					HTMLDataField.logger.trace(null, e);
				}
			}
		}
	}

	@Override
	public void deleteData() {
		this.setValue(HTMLDataField.HTML_BASE);
		try {
			this.enableInnerListener(false);
			((JTextComponent) this.dataField).getDocument()
			.remove(0, ((JTextComponent) this.dataField).getDocument().getLength());
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
		} finally {
			this.enableInnerListener(true);
		}
	}

	@Override
	public Object getValue() {
		StringWriter sw = null;
		try {
			final Document doc = ((JEditorPane) this.dataField).getDocument();
			if (this.advancedQueryMode && (this.plainTextColumn != null)) {
				final FormatText vm = new FormatText(this.plainTextColumn, null, doc.getText(0, doc.getLength()), null);
				return vm;
			}
			sw = new StringWriter();
			final int longitud = doc.getLength();
			if (longitud == 0) {
				return null;
			}

			this.editor.write(sw, doc, 0, longitud);
			sw.flush();
			final StringBuffer s = sw.getBuffer();
			final int i1 = s.indexOf("<head>");
			final int i2 = s.indexOf("</head>");
			int i3 = s.indexOf("\r", i1);
			while ((i3 >= 0) && (i2 >= 0) && (i3 < i2)) {
				s.replace(i3, i3 + 1, "");
				i3 = s.indexOf("\r", i1);
			}

			final String res = this.parseHTML(s.toString());

			// Now multiple value if plaintextcolumn is not null
			if (this.plainTextColumn != null) {
				final FormatText vm = new FormatText(this.plainTextColumn, (String) this.attribute,
						doc.getText(0, doc.getLength()), res);
				return vm;
			}
			return res;
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
			return null;
		} finally {
			try {
				if (sw != null) {
					sw.close();
				}
			} catch (final Exception e) {
				HTMLDataField.logger.trace(null, e);
			}
		}
	}

	public String parseHTML(final String s) {

		final String headerString = "<html><head></head><body>";
		final String footerString = "</body></html>";

		final StringBuilder builder = new StringBuilder();
		builder.append(headerString);

		final int headerIndex = s.indexOf("<body>") + 6;
		final int footerIndex = s.indexOf("</body>");
		if ((headerIndex > -1) && (footerIndex > -1)) {
			String content = s.substring(headerIndex, footerIndex);
			content = content.replace("\r", "");
			content = content.replaceAll("\t", HTMLDataField.TAB);
			final int firstNewLine = content.indexOf("\n") > -1 ? content.indexOf("\n") + 1 : 0;
			final int lastNewLine = content.lastIndexOf("\n") > -1 ? content.lastIndexOf("\n") : content.length();
			content = content.substring(firstNewLine);
			content = content.substring(0, lastNewLine);
			content = content.replaceAll("^\\s+", "");
			content = content.replaceAll("$\\s+", "");
			content = content.replaceAll("\n", "<br/>");
			builder.append(content);
		}
		builder.append(footerString);
		return builder.toString();
	}

	/**
	 * Creates styles(underlined, bold, ...)
	 */
	protected void createStyles() {
		// Bold
		Style s = this.context.addStyle(null, null);
		boolean b = true;
		StyleConstants.setBold(s, b);
		this.styles.put(HTMLDataField.BOLD + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setBold(s, b);
		this.styles.put(HTMLDataField.BOLD + b, s);
		// Italic
		s = this.context.addStyle(null, null);
		b = true;
		StyleConstants.setItalic(s, b);
		this.styles.put(HTMLDataField.ITALIC + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setItalic(s, b);
		this.styles.put(HTMLDataField.ITALIC + b, s);

		// Underline
		s = this.context.addStyle(null, null);
		b = true;
		StyleConstants.setUnderline(s, b);
		this.styles.put(HTMLDataField.UNDERLINE + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setUnderline(s, b);
		this.styles.put(HTMLDataField.UNDERLINE + b, s);

	}

	@Override
	protected void createDataField() {
		this.dataField = new JEditorPane() {

			@Override
			protected void processKeyEvent(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_F3) && (e.getID() == KeyEvent.KEY_RELEASED)) {
					if (HTMLDataField.this.dQuery == null) {
						HTMLDataField.this.dQuery = new FindDialog(HTMLDataField.this.parentFrame,
								(JTextComponent) HTMLDataField.this.dataField);
						HTMLDataField.this.dQuery.setResourceBundle(HTMLDataField.this.resources);
						HTMLDataField.this.dQuery.setComponentLocale(HTMLDataField.this.locale);
						HTMLDataField.this.dQuery
						.show(((JTextComponent) HTMLDataField.this.dataField).getCaretPosition());
					} else {
						if (HTMLDataField.this.dQuery != null) {
							HTMLDataField.this.dQuery
							.find(((JTextComponent) HTMLDataField.this.dataField).getCaretPosition());
						}
					}
					e.consume();
					return;
				} else {
					super.processKeyEvent(e);
				}
			}
		};

		final HTMLEditorKit ed = new HTMLEditorKit() {

			@Override
			public Document createDefaultDocument() {
				final StyleSheet styles = this.getStyleSheet();
				final StyleSheet ss = new StyleSheet();

				ss.addStyleSheet(styles);

				final HTMLDocument doc = new HTMLDocument(ss) {

					@Override
					public void insertString(final int offs, String str, final AttributeSet a) throws BadLocationException {
						if ("\n".equalsIgnoreCase(str)) {
							str = "\n";
						}
						super.insertString(offs, str, a);
					}
				};
				doc.setParser(this.getParser());
				doc.setAsynchronousLoadPriority(4);
				doc.setTokenThreshold(100);
				return doc;
			}

		};

		((JEditorPane) this.dataField).setEditorKit(ed);

	}

	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * Sets text attributes.
	 * <p>
	 * @param offset the start of changes
	 * @param length the lenght of changes
	 * @param at the attribute set
	 * @param replace the condition replacement
	 */
	protected void setTextAttributes(final int offset, final int length, final AttributeSet at, final boolean replace) {
		try {

			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			if (doc.getLength() == 0) {
				return;
			}
			if (length > 0) {
				this.valueSave = this.getValue();
				doc.setCharacterAttributes(offset, Math.min(doc.getLength() - offset, length), at, replace);
				this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);
			} else {
				final StyledEditorKit k = this.editor;
				final MutableAttributeSet inputAttributes = k.getInputAttributes();
				inputAttributes.addAttributes(at);
			}
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
		}
	}

	/**
	 * Changes the character attributes.
	 * <p>
	 * @param attr the attribute set
	 * @param replace the condition of replacement
	 */
	protected void setCharacterAttributes(final AttributeSet attr, final boolean replace) {
		final int p0 = ((JTextComponent) this.dataField).getSelectionStart();
		final int p1 = ((JTextComponent) this.dataField).getSelectionEnd();
		if (p0 != p1) {
			this.valueSave = this.getValue();
			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);
		}
		final StyledEditorKit k = this.editor;
		final MutableAttributeSet inputAttributes = k.getInputAttributes();
		if (replace) {
			inputAttributes.removeAttributes(inputAttributes);
		}
		inputAttributes.addAttributes(attr);
	}

	/**
	 * Sets paragraph attributes.
	 * <p>
	 * @param offset the start of change
	 * @param length the length of change
	 * @param at the attribute set
	 * @param replace the condition of replacement
	 */
	protected void setParagraphAttributes(final int offset, final int length, final AttributeSet at, final boolean replace) {
		try {

			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			if (doc.getLength() == 0) {
				return;
			}
			this.valueSave = this.getValue();
			// Object oPreviousValue = this.getValue();
			doc.setParagraphAttributes(offset, Math.min(doc.getLength() - offset, length), at, replace);
			this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
		}
	}

	protected void setTextFontSize(final int offset, final int length, final int size) {
		try {
			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			if (doc.getLength() == 0) {
				return;
			}
			final Style s = this.context.addStyle(null, null);
			StyleConstants.setFontSize(s, size);
			if (length > 0) {
				this.valueSave = this.getValue();
				doc.setCharacterAttributes(offset, Math.min(doc.getLength() - offset, length), s, false);
				this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);
			} else {
				final StyledEditorKit k = this.editor;
				final MutableAttributeSet inputAttributes = k.getInputAttributes();
				inputAttributes.addAttributes(s);
			}
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
		}
	}

	protected void setTextFontColor(final int offset, final int length, final Color c) {
		try {

			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			if (doc.getLength() == 0) {
				return;
			}
			final Style s = this.context.addStyle(null, null);
			StyleConstants.setForeground(s, c);
			if (length > 0) {
				this.valueSave = this.getValue();
				doc.setCharacterAttributes(offset, Math.min(doc.getLength() - offset, length), s, false);
				this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);
			} else {
				final StyledEditorKit k = this.editor;
				final MutableAttributeSet inputAttributes = k.getInputAttributes();
				inputAttributes.addAttributes(s);
			}
		} catch (final Exception e) {
			HTMLDataField.logger.error(null, e);
		}
	}

	@Override
	public boolean isEmpty() {
		try {
			if ((((JTextComponent) this.dataField).getDocument().getLength() == 0)
					|| ((JTextComponent) this.dataField).getDocument()
					.getText(0, ((JTextComponent) this.dataField).getDocument().getLength())
					.equalsIgnoreCase("\n")
					|| (((JTextComponent) this.dataField).getDocument()
							.getText(0, ((JTextComponent) this.dataField).getDocument().getLength())
							.trim()
							.length() == 0)) {
				return true;
			}
			return false;
		} catch (final BadLocationException ex) {
			HTMLDataField.logger.trace(null, ex);
			return false;
		}
	}

	/**
	 * Sets visible the buttons panel.
	 * <p>
	 * @param v the condition about visibility panel
	 */
	public void setButtonPanelVisible(final boolean v) {
		this.buttonsPanel.setVisible(v);
	}

	@Override
	public void setAdvancedQueryMode(final boolean mode) {
		this.advancedQueryMode = mode;
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_IMAGE;
	}

	/**
	 * Gets an image with content of rendered field.
	 *
	 * @since 5.2067EN
	 */
	@Override
	public Object getTemplateDataValue() {
		final BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final JEditorPane editorPane = (JEditorPane) this.dataField;
		editorPane.paint(bi.getGraphics());
		return bi;
	}

}
