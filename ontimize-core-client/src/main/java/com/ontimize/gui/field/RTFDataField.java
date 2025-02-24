package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.FindDialog;
import com.ontimize.gui.FormatText;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.rtf.RTFEditorKit;
import com.ontimize.util.templates.ITemplateField;

/**
 * Main class to implement a specific HTML data field.
 * <p>
 *
 * @author Imatia Innovation
 */

public class RTFDataField extends MemoDataField implements AdvancedDataComponent, ITemplateField {

	private static final Logger logger = LoggerFactory.getLogger(RTFDataField.class);

	protected static final String BOLD = "bold";

	protected static final String ITALIC = "italic";

	protected static final String UNDERLINE = "underline";

	protected static final String RTF_BASE = "";

	protected int minFontSize;

	protected int maxFontSize;

	protected int fontSizeStep;

	protected boolean opaquebuttons;

	protected boolean borderbuttons;

	protected JMenuItem menuFormattedCut;

	protected JMenuItem menuFormattedCopy;

	protected MouseListener listenerHighlightButtons;

	protected StyledEditorKit editor;

	protected String lastSelectedFontName;

	protected int lastSelectedFontSize;

	protected ButtonGroup alignButtonGroup;

	public static int defaultTemplateDataType = ITemplateField.DATA_TYPE_FIELD;

	protected int templateDataType = RTFDataField.defaultTemplateDataType;

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

		protected StyledEditorKit.StyledTextAction act;

		public ActionWrapper(final StyledEditorKit.StyledTextAction a) {
			this.act = a;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			RTFDataField.this.valueSave = RTFDataField.this.getValue();
			this.act.actionPerformed(e);
			RTFDataField.this.fireValueChanged(RTFDataField.this.getValue(), RTFDataField.this.valueSave,
					ValueEvent.USER_CHANGE);
		}

	}

	protected JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));

	protected JToggleButton boldBt;

	protected JToggleButton italicBt;

	protected JToggleButton underlineBt;

	protected JToggleButton leftAlignBt;

	protected JToggleButton centerAlignBt;

	protected JToggleButton rightAlignBt;

	protected JComboBox fontsizeCombo;

	protected JComboBox fontFamilyCombo;

	protected JButton colorButton;

	protected StyleContext context = new StyleContext();

	protected Map styles = new Hashtable();

	protected String plainTextColumn = null;

	protected boolean advancedQueryMode = false;

	protected class AlignListener extends ActionWrapper {

		protected StyledEditorKit.StyledTextAction act;

		public AlignListener(final StyledEditorKit.StyledTextAction a) {
			super(a);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			super.actionPerformed(e);
		}

	}

	/**
	 * Initializes parameters. Configure buttons and adds key events. XML definition could be contains
	 * an optional parameter: 'plaintextcolumn' specifying the identifier to save the text in plain
	 * format.
	 * <p>
	 * @param parameters the Map with parameters
	 */

	public RTFDataField(final Map parameters) {
		super(parameters);
		this.createStyles();
		this.configureButtonsPanelAndEditor();
		this.installButtonsHandler();
		this.dataField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_SPACE) && e.isControlDown() && e.isAltDown()) {
					MessageDialog.showMessage(RTFDataField.this.parentFrame, (String) RTFDataField.this.getValue(),
							JOptionPane.INFORMATION_MESSAGE, null);
				}
			}
		});
		this.plainTextColumn = (String) parameters.get("plaintextcolumn");

		this.templateDataType = ParseUtils.getTemplateDataType(
				(String) parameters.get(ITemplateField.TEMPLATE_DATA_TYPE), RTFDataField.defaultTemplateDataType);

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

	protected void createFontFamilyCombo() {
		// Get the local graphics environment
		final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

		// Get the font names from the graphics environment
		final String[] fontNames = env.getAvailableFontFamilyNames();

		this.fontFamilyCombo = new JComboBox(fontNames) {

			@Override
			public boolean isRequestFocusEnabled() {
				return false;
			}

			@Override
			public boolean isFocusTraversable() {
				return false;
			}
		};

		// Render the combo with the font names
		this.fontFamilyCombo.setRenderer(new FontCellRenderer());

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
				if ((dot < 0) || (dot >= ((JTextComponent) RTFDataField.this.dataField).getDocument().getLength())) {
					return;
				}
				final Element element = ((StyledDocument) ((JTextComponent) RTFDataField.this.dataField).getDocument())
						.getCharacterElement(dot);
				if (element != null) {
					final AttributeSet at = element.getAttributes();
					if (at != null) {
						RTFDataField.this.boldBt.setSelected(StyleConstants.isBold(at));
						RTFDataField.this.italicBt.setSelected(StyleConstants.isItalic(at));
						RTFDataField.this.underlineBt.setSelected(StyleConstants.isUnderline(at));
					}
				}

				final Element pElement = ((StyledDocument) ((JTextComponent) RTFDataField.this.dataField).getDocument())
						.getParagraphElement(dot);
				if (pElement != null) {
					final AttributeSet at = pElement.getAttributes();
					if (at != null) {
						final int al = StyleConstants.getAlignment(at);
						switch (al) {
						case StyleConstants.ALIGN_LEFT:
							RTFDataField.this.leftAlignBt.setSelected(true);
							break;
						case StyleConstants.ALIGN_RIGHT:
							RTFDataField.this.rightAlignBt.setSelected(true);
							break;
						case StyleConstants.ALIGN_CENTER:
							RTFDataField.this.centerAlignBt.setSelected(true);
							break;
						default:
							RTFDataField.this.leftAlignBt.setSelected(true);
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
				this.setEnabledRTF(en);
			} else {
				this.setEnabledRTF(false);
			}
		} else {
			this.setEnabledRTF(false);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #setEnabled(boolean)}
	 * @param en
	 */
	protected void setEnabledRTF(final boolean en) {
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
		if (this.fontFamilyCombo != null) {
			this.fontFamilyCombo.setEnabled(en);
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
	}

	public void configureButtons() {
		// Map hToggleParameters =
		// DefaultXMLParametersManager.getParameters(ToggleButton.class.getName());
		try {
			this.boldBt = new ToggleButton();
			this.italicBt = new ToggleButton();
			this.underlineBt = new ToggleButton();
			this.leftAlignBt = new ToggleButton();
			this.centerAlignBt = new ToggleButton();
			this.rightAlignBt = new ToggleButton();
			this.colorButton = new DataField.FieldButton() {

				@Override
				public boolean isRequestFocusEnabled() {
					return false;
				}

				@Override
				public boolean isFocusTraversable() {
					return false;
				}

			};

			this.alignButtonGroup = new ButtonGroup();
			this.alignButtonGroup.add(this.leftAlignBt);
			this.alignButtonGroup.add(this.centerAlignBt);
			this.alignButtonGroup.add(this.rightAlignBt);

			this.leftAlignBt.setIcon(ImageManager.getIcon(ImageManager.LEFT_ALIGN));
			this.centerAlignBt.setIcon(ImageManager.getIcon(ImageManager.CENTER_ALIGN));
			this.rightAlignBt.setIcon(ImageManager.getIcon(ImageManager.RIGHT_ALIGN));
			this.boldBt.setIcon(ImageManager.getIcon(ImageManager.BOLD_FONT));
			this.italicBt.setIcon(ImageManager.getIcon(ImageManager.ITALIC_FONT));
			this.underlineBt.setIcon(ImageManager.getIcon(ImageManager.UNDERLINE_FONT));
			this.colorButton.setIcon(ImageManager.getIcon(ImageManager.CHOOSE_COLOR));

			// Register actions
			this.boldBt.addActionListener(new ActionWrapper(new StyledEditorKit.BoldAction()));
			this.italicBt.addActionListener(new ActionWrapper(new StyledEditorKit.ItalicAction()));
			this.underlineBt.addActionListener(new ActionWrapper(new StyledEditorKit.UnderlineAction()));
			this.leftAlignBt.addActionListener(
					new ActionWrapper(new StyledEditorKit.AlignmentAction("leftAlign", StyleConstants.ALIGN_LEFT)));
			this.centerAlignBt.addActionListener(
					new ActionWrapper(new StyledEditorKit.AlignmentAction("centerAlign", StyleConstants.ALIGN_CENTER)));
			this.rightAlignBt.addActionListener(
					new ActionWrapper(new StyledEditorKit.AlignmentAction("rightAlign", StyleConstants.ALIGN_RIGHT)));
			this.colorButton.addActionListener(new ExtForegroundAction());

		} catch (final Exception e) {
			RTFDataField.logger.error(null, e);
		}
	}

	protected void configureButtonsPanelAndEditor() {

		this.configureButtons();

		// Add to the panel
		this.buttonsPanel.add(this.boldBt);
		this.buttonsPanel.add(this.italicBt);
		this.buttonsPanel.add(this.underlineBt);

		// Add to the panel
		this.buttonsPanel.add(this.leftAlignBt);
		this.buttonsPanel.add(this.centerAlignBt);
		this.buttonsPanel.add(this.rightAlignBt);

		this.createFontSizeCombo();
		this.buttonsPanel.add(this.fontsizeCombo);
		this.createFontFamilyCombo();
		this.buttonsPanel.add(this.fontFamilyCombo);
		this.buttonsPanel.add(this.colorButton);

		this.changeButton(this.colorButton, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.rightAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.leftAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.centerAlignBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.boldBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.italicBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButton(this.underlineBt, this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);

		this.fontsizeCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Integer iFontSize = (Integer) RTFDataField.this.fontsizeCombo.getSelectedItem();
				final JTextComponent tc = (JTextComponent) RTFDataField.this.dataField;
				final int start = tc.getSelectionStart();
				int end = tc.getSelectionEnd();
				if (start == end) {
					end = start + 1;
				}

				RTFDataField.this.setTextFontSize(start, end - start, iFontSize.intValue());
			}
		});

		this.fontFamilyCombo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String sFontFamilyCombo = (String) RTFDataField.this.fontFamilyCombo.getSelectedItem();
				final JTextComponent tc = (JTextComponent) RTFDataField.this.dataField;
				final int start = tc.getSelectionStart();
				int end = tc.getSelectionEnd();
				if (start == end) {
					end = start + 1;
				}

				RTFDataField.this.setFontFamily(start, end - start, sFontFamilyCombo);
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
				final boolean oldValueEvents = RTFDataField.this.fireValueEvents;
				try {
					RTFDataField.this.fireValueEvents = false;

					final Font fontTextSelected = RTFDataField.this.getSelectedTextFont(e.getMark(), e.getDot());
					if (e.getMark() == e.getDot()) {
						if (!fontTextSelected.getName().equals(RTFDataField.this.lastSelectedFontName)) {
							RTFDataField.this.fontFamilyCombo.setSelectedItem(fontTextSelected.getName());
							RTFDataField.this.lastSelectedFontName = fontTextSelected.getName();
						}
						if (fontTextSelected.getSize() != RTFDataField.this.lastSelectedFontSize) {
							RTFDataField.this.fontsizeCombo.setSelectedItem(new Integer(fontTextSelected.getSize()));
							RTFDataField.this.lastSelectedFontSize = fontTextSelected.getSize();
						}
					}
				} finally {
					RTFDataField.this.fireValueEvents = oldValueEvents;
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
				if (!v.equals(RTFDataField.RTF_BASE)) {
					this.editor.read(sr, ((JTextComponent) this.dataField).getDocument(), 0);
				}
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					RTFDataField.logger.error(null, e);
				} else {
					RTFDataField.logger.trace(null, e);
				}
			} finally {
				this.enableInnerListener(true);
				try {
					sr.close();
				} catch (final Exception e) {
					RTFDataField.logger.trace(null, e);
				}
			}
		}
	}

	@Override
	public void deleteData() {
		this.setValue(RTFDataField.RTF_BASE);
		try {
			this.enableInnerListener(false);
			((JTextComponent) this.dataField).getDocument()
			.remove(0, ((JTextComponent) this.dataField).getDocument().getLength());
		} catch (final Exception e) {
			RTFDataField.logger.error(null, e);
		} finally {
			this.enableInnerListener(true);
		}
	}

	@Override
	public Object getValue() {
		OutputStream baOS = null;
		try {
			final Document doc = ((JEditorPane) this.dataField).getDocument();
			if (this.advancedQueryMode && (this.plainTextColumn != null)) {
				final FormatText vm = new FormatText(this.plainTextColumn, null, doc.getText(0, doc.getLength()), null);
				return vm;
			}
			baOS = new ByteArrayOutputStream();
			final int longitud = doc.getLength();
			if (longitud == 0) {
				return null;
			}

			this.editor.write(baOS, doc, 0, longitud);

			// sw.flush();
			// StringBuilder s = sw.getBuffer();
			// int i1 = s.indexOf("<head>");
			// int i2 = s.indexOf("</head>");
			// int i3 = s.indexOf("\r", i1);
			// while (i3 >= 0 && i2 >= 0 && i3 < i2) {
			// s.replace(i3, i3 + 1, "");
			// i3 = s.indexOf("\r", i1);
			// }
			// String res = s.toString();
			// res = res.replaceAll("<html>\r", "<html>");
			// res = res.replaceAll("</head>\r", "</head>");
			// res = res.replaceAll("<body>\r", "<body>");
			// res = res.replaceAll("</body>\r", "</body>");
			// res = res.replaceAll("</html>\r", "</html>");
			// res = res.replaceAll("\t", TAB);
			// res = res.replaceAll("\r", "<BR>");
			final String res = baOS.toString();
			// Now multiple value if plaintextcolumn is not null
			if (this.plainTextColumn != null) {
				final FormatText vm = new FormatText(this.plainTextColumn, (String) this.attribute,
						doc.getText(0, doc.getLength()), res);
				return vm;
			}
			return res;
		} catch (final Exception e) {
			RTFDataField.logger.error(null, e);
			return null;
		} finally {
			try {
				if (baOS != null) {
					baOS.close();
				}
			} catch (final Exception e) {
				RTFDataField.logger.trace(null, e);
			}
		}
	}

	/**
	 * Creates styles(underlined, bold, ...)
	 */
	protected void createStyles() {
		// Bold
		Style s = this.context.addStyle(null, null);
		boolean b = true;
		StyleConstants.setBold(s, b);
		this.styles.put(RTFDataField.BOLD + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setBold(s, b);
		this.styles.put(RTFDataField.BOLD + b, s);

		// Italic
		s = this.context.addStyle(null, null);
		b = true;
		StyleConstants.setItalic(s, b);
		this.styles.put(RTFDataField.ITALIC + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setItalic(s, b);
		this.styles.put(RTFDataField.ITALIC + b, s);

		// Underline
		s = this.context.addStyle(null, null);
		b = true;
		StyleConstants.setUnderline(s, b);
		this.styles.put(RTFDataField.UNDERLINE + b, s);

		s = this.context.addStyle(null, null);
		b = false;
		StyleConstants.setUnderline(s, b);
		this.styles.put(RTFDataField.UNDERLINE + b, s);

	}

	@Override
	protected void createDataField() {
		this.dataField = new JEditorPane() {

			{
				this.getActionMap().put("copy", new RTFCopyAction());
			}

			@Override
			protected void processKeyEvent(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_F3) && (e.getID() == KeyEvent.KEY_RELEASED)) {
					if (RTFDataField.this.dQuery == null) {
						RTFDataField.this.dQuery = new FindDialog(RTFDataField.this.parentFrame,
								(JTextComponent) RTFDataField.this.dataField);
						RTFDataField.this.dQuery.setResourceBundle(RTFDataField.this.resources);
						RTFDataField.this.dQuery.setComponentLocale(RTFDataField.this.locale);
						RTFDataField.this.dQuery
						.show(((JTextComponent) RTFDataField.this.dataField).getCaretPosition());
					} else {
						if (RTFDataField.this.dQuery != null) {
							RTFDataField.this.dQuery
							.find(((JTextComponent) RTFDataField.this.dataField).getCaretPosition());
						}
					}
					e.consume();
					return;
				} else {
					super.processKeyEvent(e);
				}
			}

		};
		this.editor = new RTFEditorKit();
		this.setRTFEditorKit(this.editor);
	}

	protected void setRTFEditorKit(final StyledEditorKit editor) {
		((JEditorPane) this.dataField).setEditorKit(editor);
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
			RTFDataField.logger.error(null, e);
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
			RTFDataField.logger.error(null, e);
		}
	}

	protected void setFontFamily(final int offset, final int length, final String fontName) {
		try {
			final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
			if (doc.getLength() == 0) {
				return;
			}
			final Style s = this.context.addStyle(null, null);
			StyleConstants.setFontFamily(s, fontName);
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
			RTFDataField.logger.error(null, e);
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
			RTFDataField.logger.error(null, e);
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
			RTFDataField.logger.error(null, e);
		}
	}

	protected class FontCellRenderer extends DefaultListCellRenderer {

		protected int iFontSize = 10;

		public FontCellRenderer() {

		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			// Get the default cell renderer
			final JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

			// Create a font based on the item value
			final Font itemFont = new Font((String) value, Font.PLAIN, 14);

			if (itemFont.canDisplayUpTo((String) value) == -1) {
				// Set the font of the label
				label.setFont(itemFont);
			} else {
				// Create a font based on the item value
				final String fontName = label.getFont().getFontName();
				final Font largerFont = new Font(fontName, Font.PLAIN, 24);

				// Set the font of the label
				label.setFont(largerFont);
			}

			return label;
		}

	}

	protected class RTFCopyAction extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JEditorPane editor = (JEditorPane) RTFDataField.this.dataField;

			String text = null;
			final int p0 = Math.min(editor.getCaret().getDot(), editor.getCaret().getMark());
			final int p1 = Math.max(editor.getCaret().getDot(), editor.getCaret().getMark());
			if (p0 != p1) {
				try {
					// This is to fix a Sun bug of RTFEditorKit in method write
					// The Sun method :
					// public void write(OutputStream out, Document doc, int
					// pos, int len)
					// throws IOException, BadLocationException {
					// //PENDING(prinz) this needs to be fixed to
					// //use the given document range.
					// RTFGenerator.writeDocument(doc, out);
					// }

					final Document doc = editor.getDocument();

					final ByteArrayOutputStream baOS = new ByteArrayOutputStream();
					editor.getEditorKit().write(baOS, doc, p0, p1 - p0);

					final Document auxDoc = editor.getEditorKit().createDefaultDocument();
					final ByteArrayInputStream input = new ByteArrayInputStream(baOS.toByteArray());

					editor.getEditorKit().read(input, auxDoc, p0);
					auxDoc.remove(0, p0);
					auxDoc.remove(p1 - p0, auxDoc.getLength() - (p1 - p0));

					final ByteArrayOutputStream aux = new ByteArrayOutputStream();
					editor.getEditorKit().write(aux, auxDoc, 0, auxDoc.getLength());

					text = aux.toString();
				} catch (final Exception ex) {
					throw new IllegalArgumentException(ex.getMessage(), ex);
				}
			}
			if (text != null) {
				final RTFSelection sel = new RTFSelection(text.toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, sel);
			}

		}

	}

	@Override
	protected void createPopupMenu() {
		super.createPopupMenu();
		final ActionListener[] listeners = this.menuCopy.getActionListeners();
		if (listeners != null) {
			for (int i = 0; i < listeners.length; i++) {
				if (listeners[i] instanceof CopyActionListener) {
					this.menuCopy.removeActionListener(listeners[i]);
				}
			}
		}

		this.menuCopy.addActionListener(new RTFCopyAction());
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
			RTFDataField.logger.trace(null, ex);
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

	protected static class RTFSelection implements Transferable, ClipboardOwner {

		DataFlavor rtfFlavor;

		DataFlavor[] supportedFlavors;

		private final String content;

		public RTFSelection(final String s) {
			this.content = s;
			try {
				this.rtfFlavor = new DataFlavor("text/rtf; class=java.io.InputStream");
				this.supportedFlavors = new DataFlavor[] { this.rtfFlavor, DataFlavor.stringFlavor };
			} catch (final ClassNotFoundException ex) {
				RTFDataField.logger.error(null, ex);
			}
		}

		@Override
		public boolean isDataFlavorSupported(final DataFlavor flavor) {
			return flavor.equals(this.rtfFlavor) || flavor.equals(DataFlavor.stringFlavor);
		}

		@Override
		public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors() {
			RTFDataField.logger.debug("..");
			return this.supportedFlavors;
		}

		@Override
		public Object getTransferData(final DataFlavor flavor) throws UnsupportedFlavorException, IOException {

			if (flavor.equals(DataFlavor.stringFlavor)) {
				return this.content;
			} else if (flavor.equals(this.rtfFlavor)) {
				final byte[] byteArray = this.content.getBytes();
				return new ByteArrayInputStream(byteArray);
			}
			throw new UnsupportedFlavorException(flavor);
		}

		@Override
		public void lostOwnership(final Clipboard clipboard, final Transferable contents) {

		}

	}

	@Override
	public int getTemplateDataType() {
		return this.templateDataType;
	}

	/**
	 * Gets an image with content of rendered field.
	 *
	 * @since 5.2067EN
	 */
	@Override
	public Object getTemplateDataValue() {
		if (ITemplateField.DATA_TYPE_IMAGE == this.templateDataType) {
			final JEditorPane editorPane = (JEditorPane) this.dataField;
			this.width = editorPane.getPreferredSize().width;
			this.height = editorPane.getPreferredSize().height - editorPane.getInsets().top
					- editorPane.getInsets().bottom;
			final BufferedImage bi = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
			editorPane.print(bi.getGraphics());
			return bi;
		}

		return this.getValue();
	}

}
