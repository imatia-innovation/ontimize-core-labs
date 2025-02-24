package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.sql.Types;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.gui.LongString;

/**
 * Text field associated with a dictionary
 *
 * @version 1.0 31-01-2001
 */

public class DictionaryDataField extends DataField implements MouseListener, ActionListener, OpenDialog {

	private static final Logger logger = LoggerFactory.getLogger(DictionaryDataField.class);

	public static boolean DEBUG_DICTIONARY = false;

	protected int maxLenght = -1;

	protected boolean uppercase = false;

	protected SimpleAttributeSet textErrorAttributes = new SimpleAttributeSet();

	protected SimpleAttributeSet textOkAttributes = new SimpleAttributeSet();

	protected static DictionaryChangeListener globalListener = null;

	protected String initialLanguage = null;

	protected String currentDictionayFile = null;

	protected String currentLanguage = null;

	protected String dictionaryListFile = null;

	protected Locale locale = Locale.getDefault();

	// Keys are different locales and values vectors with the words
	protected static Map wordList = new Hashtable();

	protected List currentWordList = new Vector();

	protected String menuKey = "Orthography";

	protected String saveKey = "AddToDictionary";

	protected String deleteKey = "DeleteFromDictionary";

	protected String changeDictionaryKey = "ChangeDictionary";

	protected String suggestionKey = "Suggestions";

	protected ExtendedJPopupMenu menu = new ExtendedJPopupMenu(this.menuKey);

	protected JMenuItem saveMenu = null;

	protected JMenuItem deleteMenu = null;

	protected JMenuItem changeDictionaryMenu = null;

	protected JMenu suggestionMenu = null;

	protected JMenuItem noSuggestionMenu = new JMenuItem("There are not suggestions");

	protected Frame parentFrame = null;

	protected int delay = 1000;

	protected int rowNumber = 3;

	protected boolean sQLTypeText = false;

	protected java.text.Collator collator = null;

	protected class Analyzer extends Thread {

		protected boolean checkNext = true;

		protected DictionayDocument doc = null;

		protected int delay = 1000;

		public Analyzer(final DictionayDocument doc, final int delay) {
			this.doc = doc;
			this.delay = delay;
		}

		@Override
		public void run() {
			this.setPriority(Thread.MIN_PRIORITY);
			try {
				Thread.sleep(this.delay);
				final long t = System.currentTimeMillis();
				this.analyzeDocument();
				if (DictionaryDataField.DEBUG_DICTIONARY) {
					DictionaryDataField.logger.debug("Time to analyze document: " + (System.currentTimeMillis() - t));
				}
			} catch (final Exception e) {
				if (DictionaryDataField.DEBUG_DICTIONARY) {
					DictionaryDataField.logger.debug(null, e);
				} else {
					DictionaryDataField.logger.trace(null, e);
				}
			}
		}

		protected void analyzeDocument() {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						synchronized (Analyzer.this.doc) {
							Analyzer.this.analyzeOrthography();
							Analyzer.this.analyzeGrammar();
						}
					}
				});
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					DictionaryDataField.logger.error(null, e);
				} else {
					DictionaryDataField.logger.trace(null, e);
				}
			}
		}

		protected void analyzeOrthography() {
			// Read document content. The words that are not in the dictionary
			// are underline in red color
			try {
				// Reset attributes.
				this.doc.setCharacterAttributes(0, this.doc.getLength(), DictionaryDataField.this.textOkAttributes,
						true);
				Thread.yield();
				final String sText = this.doc.getText(0, this.doc.getLength());
				final StringBuilder st = new StringBuilder(sText);
				// Check words:
				final StringBuilder word = new StringBuilder();
				final List indexes = new Vector();
				final List vWords = new Vector();
				for (int i = 0; i < st.length(); i++) {
					Thread.yield();
					final char character = st.charAt(i);
					if (Character.isLetterOrDigit(character) && (i < (st.length() - 1))) {
						word.append(character);
					} else {
						if ((i == (st.length() - 1)) && Character.isLetterOrDigit(character)) {
							word.append(character);
						}
						if (word.length() == 0) {
							continue;
						}
						// End of the word, then check it
						if (DictionaryDataField.this.currentWordList.contains(word.toString().toLowerCase())) {
							// OK,
						} else {
							// No OK, put the word in red color
							if (DictionaryDataField.DEBUG_DICTIONARY) {
								DictionaryDataField.logger.debug("Word not found: " + word);
							}
							indexes.add(indexes.size(), new Integer(i - word.length()));
							vWords.add(vWords.size(), word.toString());
						}
						word.delete(0, word.length());
					}
				}
				synchronized (this.doc) {
					for (int i = 0; i < indexes.size(); i++) {
						final int index = ((Integer) indexes.get(i)).intValue();
						try {
							this.doc.setCharacterAttributes(index,
									Math.min(this.doc.getLength() - index, ((String) vWords.get(i)).length()),
									DictionaryDataField.this.textErrorAttributes, true);
						} catch (final Exception e) {
							DictionaryDataField.logger.error("Error setting attributes: Index: " + index + " : word: '"
									+ vWords
									.get(i)
									+ "'  . Text: " + sText + ". Text Doc: " + this.doc.getText(0, this.doc.getLength())
									+ " Length: " + this.doc.getLength(), e);
						}
					}
				}
			} catch (final Exception ex) {
				DictionaryDataField.logger.error(null, ex);
			}
		}

		protected void analyzeGrammar() {
			try {
				Thread.yield();
				final String sText = this.doc.getText(0, this.doc.getLength());
				final StringBuilder st = new StringBuilder(sText);
				// Check words:
				for (int i = 0; i < st.length(); i++) {
					Thread.yield();
					// First rule: After a dot, the next character must be a
					// blank
					// space or a number.
					// If it is a space the following must be upper case.
					final char character = st.charAt(i);
					if ((character == '.') || (character == ',')) {
						if (i >= (st.length() - 1)) {
							break;
						}
						final char character2 = st.charAt(i + 1);
						if (Character.isDigit(character2)) {
							continue;
						} else if (character2 == ' ') {
							if (i >= (st.length() - 2)) {
								continue;
							}
							final char character3 = st.charAt(i + 2);
							if (Character.isLetter(character3) && (!Character.isUpperCase(character3))
									&& (character == '.')) {
								// The letter after a dot is not upper case, put
								// it in
								// red color
								this.doc.setCharacterAttributes(i, 3, DictionaryDataField.this.textErrorAttributes,
										true);
							} else {
								continue;
							}
						} else {
							// If the character after a dot is different of
							// space and
							// number then it is wrong
							this.doc.setCharacterAttributes(i, Math.min(st.length() - i - 1, 2),
									DictionaryDataField.this.textErrorAttributes, true);
						}
					}
				}
			} catch (final Exception ex) {
				DictionaryDataField.logger.error(null, ex);
			}
		}

	}

	protected class DictionayDocument extends DefaultStyledDocument {

		protected boolean upper = false;

		protected int maxLength = -1;

		protected boolean innerListenerEnabled = true;

		public void setInnerListenerEnabled(final boolean act) {
			this.innerListenerEnabled = act;
		}

		public DictionayDocument(final boolean upper, final int maxLength) {
			this.upper = upper;
			this.maxLength = maxLength;
		}

		public DictionayDocument() {
		}

		public void setUpper(final boolean upper) {
			this.upper = true;
			try {
				final String content = this.getText(0, this.getLength());
				if (content.length() > 0) {
					this.remove(0, this.getLength());
					this.insertString(0, content, null);
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
		}

		public void setMaxLength(final int maxLength) {
			this.maxLength = maxLength;
			if (this.maxLength <= 0) {
				return;
			}
			try {
				final int length = this.getLength();
				if (length > this.maxLength) {
					this.remove(this.maxLength, length - this.maxLength);
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
		}

		@Override
		public void insertString(final int offset, String str, final AttributeSet a) throws BadLocationException {
			if (this.maxLength > 0) {
				if ((offset + str.length()) > this.maxLength) {
					str = str.substring(0, this.maxLength - offset);
				}
			}
			if (this.upper) {
				super.insertString(offset, str.toUpperCase(), a);
			} else {
				super.insertString(offset, str, a);
			}
			DictionaryDataField.this.runAnalyzer(DictionaryDataField.this.delay);
			if (this.innerListenerEnabled) {
				DictionaryDataField.this.fireValueChanged(DictionaryDataField.this.getValue(),
						DictionaryDataField.this.valueSave, ValueEvent.USER_CHANGE);
			}
		}

		@Override
		public void remove(final int offset, final int len) throws BadLocationException {
			super.remove(offset, len);
			DictionaryDataField.this.runAnalyzer(DictionaryDataField.this.delay);
			if (this.innerListenerEnabled) {
				DictionaryDataField.this.fireValueChanged(DictionaryDataField.this.getValue(),
						DictionaryDataField.this.valueSave, ValueEvent.USER_CHANGE);
			}
		}

	};

	protected void runAnalyzer(final int delay) {
		if ((this.analyzer == null) || !this.analyzer.isAlive()) {
			this.analyzer = new Analyzer(this.doc, delay);
			this.analyzer.start();
		} else {
			if (this.analyzer.isAlive()) {
				if (DictionaryDataField.DEBUG_DICTIONARY) {
					DictionaryDataField.logger.debug(this.getClass().getName() + " : Stopping analyzer Thread");
				}
				final long t = System.currentTimeMillis();
				this.analyzer.interrupt();
				if (DictionaryDataField.DEBUG_DICTIONARY) {
					DictionaryDataField.logger.debug(this.getClass().getName() + ": Analyzer thread stopped: "
							+ (System.currentTimeMillis() - t));
				}
			}
			this.analyzer = new Analyzer(this.doc, delay);
			this.analyzer.start();
		}
	}

	protected class ExtendedTextPane extends JTextPane {

		protected int rows = 3;

		protected int rowHeight = 0;

		public ExtendedTextPane(final StyledDocument doc, final int rows) {
			super(doc);
			this.rows = rows;
			this.setMargin(new Insets(0, 0, 0, 0));
		}

		@Override
		public void updateUI() {
			super.updateUI();
			this.setMargin(new Insets(0, 0, 0, 0));
		}

		public void setRows(final int rows) {
			this.rows = rows;
			this.invalidate();
		}

		@Override
		public void setFont(final Font f) {
			super.setFont(f);
			this.rowHeight = 0;
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			Dimension size = super.getPreferredScrollableViewportSize();
			size = size == null ? new Dimension(400, 400) : size;
			size.height = this.rows == 0 ? size.height : this.rows * this.getRowHeight();
			return size;
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension d = super.getPreferredSize();
			d = d == null ? new Dimension(400, this.rows * this.getRowHeight()) : d;
			if (this.rows == 1) {
				d.height = this.rows * this.getRowHeight();
			} else if (this.rows != 0) {
				d.height = Math.max(d.height, this.rows * this.getRowHeight());
			}

			return d;
		}

		protected int getRowHeight() {
			if (this.rowHeight == 0) {
				final FontMetrics metrics = this.getFontMetrics(this.getFont());
				this.rowHeight = metrics.getHeight();
			}
			return this.rowHeight;
		}

	};

	protected Analyzer analyzer = null;

	protected DictionayDocument doc = null;

	public DictionaryDataField(final Map parameters) throws IllegalArgumentException {
		super();
		this.doc = new DictionayDocument();
		this.dataField = new ExtendedTextPane(this.doc, this.rowNumber);

		this.init(parameters);
		this.doc.setUpper(this.uppercase);
		this.doc.setMaxLength(this.maxLenght);
		this.loadDictionary(this.initialLanguage);
		StyleConstants.setForeground(this.textErrorAttributes, Color.red);
		StyleConstants.setForeground(this.textOkAttributes, this.fontColor);
		this.dataField.addMouseListener(this);
		this.buildMenu();
	}

	protected void buildMenu() {
		final ImageIcon saveIcon = ImageManager.getIcon(ImageManager.ADD_DICTIONARY);
		this.saveMenu = new JMenuItem(this.saveKey);
		if (saveIcon != null) {
			this.saveMenu.setIcon(saveIcon);
		}
		final ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.REMOVE_FROM_DICTIONARY);
		this.deleteMenu = new JMenuItem(this.deleteKey);
		if (deleteIcon != null) {
			this.deleteMenu.setIcon(deleteIcon);
		}

		final ImageIcon suggestionsIcon = ImageManager.getIcon(ImageManager.TIPS);
		this.suggestionMenu = new JMenu(this.suggestionKey);
		if (suggestionsIcon != null) {
			this.changeDictionaryMenu.setIcon(suggestionsIcon);
		}

		final ImageIcon changeDicIcon = ImageManager.getIcon(ImageManager.CHANGE_DICTIONARY);
		this.changeDictionaryMenu = new JMenuItem(this.changeDictionaryKey);
		if (changeDicIcon != null) {
			this.changeDictionaryMenu.setIcon(changeDicIcon);
		}
		this.menu.add(this.saveMenu);
		this.menu.add(this.deleteMenu);
		this.menu.add(this.suggestionMenu);
		this.menu.addSeparator();
		this.menu.add(this.changeDictionaryMenu);
		this.saveMenu.addActionListener(this);
		this.deleteMenu.addActionListener(this);
		this.changeDictionaryMenu.addActionListener(this);
	}

	@Override
	public void init(final Map parameters) {
		final Object border = parameters.get(DataField.BORDER);
		parameters.remove(DataField.BORDER);

		super.init(parameters);

		final Object maxlength = parameters.get("maxlength");
		if (maxlength != null) {
			try {
				this.maxLenght = Integer.parseInt(maxlength.toString());
			} catch (final Exception e) {
				DictionaryDataField.logger.error("Error in parameter 'maxlength'.", e);
			}
		}
		final Object uppercase = parameters.get("uppercase");
		if (uppercase != null) {
			if (uppercase.equals("yes")) {
				this.uppercase = true;
			}
		}

		final Object sqltexttipe = parameters.get("sqltexttipe");
		if (sqltexttipe != null) {
			if (sqltexttipe.equals("yes")) {
				this.sQLTypeText = true;
			} else {
				this.sQLTypeText = false;
			}
		}

		final Object dicclist = parameters.get("dicclist");
		if (dicclist == null) {
			DictionaryDataField.logger.debug("Parameter 'dicclist' not found");
			throw new IllegalArgumentException(
					this.getClass().toString() + ": " + this.attribute + " Paramter 'dicclist' is required");
		} else {
			this.dictionaryListFile = dicclist.toString();
		}

		final Object language = parameters.get("language");
		if (language == null) {
			DictionaryDataField.logger.debug("Parameter 'language' not found");
			throw new IllegalArgumentException(
					this.getClass().toString() + ": " + this.attribute + " Parameter 'language' is required");
		} else {
			this.initialLanguage = language.toString();
		}

		final Object delay = parameters.get("delay");
		if (delay == null) {
		} else {
			try {
				this.delay = Integer.parseInt(delay.toString());
			} catch (final Exception e) {
				DictionaryDataField.logger.error("Error in parameter 'delay': ", e);
			}
		}

		this.remove(this.dataField);
		final JScrollPane scroll = new JScrollPane(this.dataField);
		if (border != null) {
			if (border.equals(DataField.RAISED)) {
				scroll.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals(DataField.LOWERED)) {
				scroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
			}
		} else {
			scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));
		}
		this.add(scroll,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, this.weightDataFieldH, 0.0,
						GridBagConstraints.EAST, this.redimensJTextField,
						new Insets(DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
								DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL, DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
								DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL),
						0, 0));

		final Object rows = parameters.get("rows");
		if (rows == null) {
		} else {
			try {
				this.rowNumber = Integer.parseInt(rows.toString());
				((ExtendedTextPane) this.dataField).setRows(this.rowNumber);
			} catch (final Exception e) {
				DictionaryDataField.logger.error("Error in parameter 'rows': " + e.getMessage(), e);
			}
		}
	}

	protected void loadDictionary(final String language) {
		try {
			final Properties prop = new Properties();
			final URL urlDictionaryListFile = this.getClass().getClassLoader().getResource(this.dictionaryListFile);
			if (urlDictionaryListFile == null) {
				this.parentForm.message("ColorDataField.dictionaryListFileNotFound", Form.ERROR_MESSAGE);
				return;
			}
			prop.load(urlDictionaryListFile.openStream());

			final String sFile = prop.getProperty(language);
			if (sFile != null) {
				final URL url = this.getClass().getClassLoader().getResource(sFile.toString());
				if (url != null) {
					this.currentLanguage = language;
					this.loadDictionary(url);
					if (this.collator == null) {
						this.collator = java.text.Collator.getInstance();
					}
					Collections.sort(this.currentWordList, this.collator);
					return;
				} else {
					DictionaryDataField.logger.debug(this.getClass().toString() + ": File not found " + sFile);
					return;
				}
			} else {
				DictionaryDataField.logger.debug(this.getClass().toString() + ": Language not found in properties");
			}
		} catch (final Exception e) {
			DictionaryDataField.logger.error(null, e);
			if (this.parentForm != null) {
				this.parentForm.message("ColorDataField.ErrorLoadingDictionary", Form.ERROR_MESSAGE);
			}
		}
	}

	protected void loadDictionary(final URL urlFile) {
		DictionaryDataField.logger.debug("Loading dictionary " + urlFile);
		if (urlFile == null) {
			DictionaryDataField.logger.debug(this.getClass().toString() + " : File not found");
		} else {
			if (DictionaryDataField.wordList.containsKey(urlFile)) {
				this.currentWordList = (List) DictionaryDataField.wordList.get(urlFile);
			} else {
				this.currentWordList = this.readFile(urlFile);
				// If file is into a .jar file then save it out of the jar
				if (urlFile.getProtocol().equalsIgnoreCase("jar")) {
					this.saveFileOutJar(urlFile);
				}
				DictionaryDataField.wordList.put(urlFile, this.currentWordList);
			}
		}

	}

	protected List readFile(final URL urlFile) {
		final List v = new Vector();
		InputStream in = null;
		InputStreamReader inR = null;
		BufferedReader br = null;
		try {
			// Load the dictionary. It is a list of words, each word in one line
			in = urlFile.openStream();
			inR = new InputStreamReader(in);
			br = new BufferedReader(inR);
			// First line (word or sentence):
			String sWord = br.readLine();
			while (sWord != null) {
				v.add(sWord.toLowerCase());
				sWord = br.readLine();
			}
			if (br != null) {
				br.close();
			}
			if (inR != null) {
				inR.close();
			}
			if (in != null) {
				in.close();
			}
			return v;
		} catch (final Exception e) {
			DictionaryDataField.logger.error(null, e);
			return null;
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
			try {
				if (inR != null) {
					inR.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		if (this.sQLTypeText) {
			return new LongString(((JTextPane) this.dataField).getText());
		} else {
			return ((JTextPane) this.dataField).getText();
		}
	}

	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.enableListenerInterno(false);
		final Object oPreviousValue = this.getValue();
		((JTextPane) this.dataField).setText(value.toString());
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.enableListenerInterno(true);
	}

	@Override
	public void deleteData() {
		this.enableListenerInterno(false);
		final Object oPreviousValue = this.getValue();
		((JTextPane) this.dataField).setText("");
		this.valueSave = this.getValue();
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.enableListenerInterno(true);
	}

	@Override
	public boolean isEmpty() {
		if (((JTextPane) this.dataField).getText().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isModifiable() {
		return this.modifiable;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.modifiable = modifiable;
	}

	@Override
	public int getSQLDataType() {
		if (!this.sQLTypeText) {
			return Types.VARCHAR;
		}
		return Types.LONGVARCHAR;
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		if ((e.getClickCount() == 1) && (e.getModifiers() == InputEvent.META_MASK)) {
			final JTextPane tp = (JTextPane) this.dataField;
			final DictionayDocument d = (DictionayDocument) tp.getDocument();
			try {
				final String sContent = d.getText(0, d.getLength());
				if (sContent.equals("")) {
					this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
					return;
				}
				final int offset = tp.viewToModel(new Point(e.getX(), e.getY()));
				if (offset >= sContent.length()) {
					this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
					return;
				}
				final char character = sContent.charAt(offset);
				if (Character.isLetterOrDigit(character)) {
					int startOffset = 0;
					int endOffset = sContent.length();
					for (int i = offset - 1; i >= 0; i--) {
						if (!Character.isLetterOrDigit(sContent.substring(i, i + 1).charAt(0))) {
							startOffset = i + 1;
							break;
						}
					}
					for (int i = offset + 1; i < sContent.length(); i++) {
						if (!Character.isLetterOrDigit(sContent.substring(i, i + 1).charAt(0))) {
							endOffset = i;
							break;
						}
					}
					// Between startOffset and endOffset is the word
					// If it is a new one then save it
					final String sWord = sContent.substring(startOffset, endOffset);
					if (DictionaryDataField.DEBUG_DICTIONARY) {
						DictionaryDataField.logger.debug("'" + sWord + "'");
					}
					if (!this.currentWordList.contains(sWord.toLowerCase())) {
						// Show menu:
						this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY(), sWord, false, startOffset);
					} else {
						this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY(), sWord, true, startOffset);
					}
				} else {
					this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
				}
			} catch (final Exception ex) {
				DictionaryDataField.logger.error(null, ex);
			}
		}
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	protected void showPopupMenu(final Component c, final int x, final int y) {
		if (this.isEnabled()) {
			this.deleteMenu.setEnabled(false);
			this.saveMenu.setEnabled(false);
			this.deleteMenu.setActionCommand("");
			this.saveMenu.setActionCommand("");
			this.saveMenu.setText(this.saveKey);
			this.deleteMenu.setText(this.deleteKey);
			if (this.currentLanguage != null) {
				this.changeDictionaryMenu.setText(this.changeDictionaryKey + " " + this.currentLanguage);
			} else {
				this.changeDictionaryMenu.setText(this.changeDictionaryKey);
			}
			this.menu.show(c, x, y);
		}
	}

	protected void showPopupMenu(final Component c, final int x, final int y, final String word, final boolean delete, final int offsetIni) {
		if (this.isEnabled()) {
			if (delete) {
				this.deleteMenu.setEnabled(true);
				this.saveMenu.setEnabled(false);
				this.deleteMenu.setActionCommand(word);
				this.saveMenu.setActionCommand("");
				this.saveMenu.setText(this.saveKey);
				this.deleteMenu.setText(this.deleteKey + " : " + word);
				this.suggestionMenu.setEnabled(false);
				this.uninstallSuggestionListener();
				this.suggestionMenu.removeAll();
			} else {
				this.deleteMenu.setEnabled(false);
				this.saveMenu.setEnabled(true);
				this.saveMenu.setActionCommand(word);
				this.deleteMenu.setActionCommand("");
				this.deleteMenu.setText(this.deleteKey);
				this.saveMenu.setText(this.saveKey + " : " + word);
				this.suggestionMenu.setEnabled(true);
				// Search similar words.
				final List vSuggestions = this.getSuggestions(word);
				this.uninstallSuggestionListener();
				this.suggestionMenu.removeAll();
				if (!vSuggestions.isEmpty()) {
					for (int i = 0; i < vSuggestions.size(); i++) {
						final JMenuItem suggestionMenu = new JMenuItem(vSuggestions.get(i).toString());
						suggestionMenu.setActionCommand(word + " " + offsetIni);
						suggestionMenu.add(suggestionMenu);
					}
					this.installSuggestionListener();
				} else {
					this.noSuggestionMenu.setEnabled(false);
					this.suggestionMenu.add(this.noSuggestionMenu);
				}
			}
			if (this.currentLanguage != null) {
				this.changeDictionaryMenu.setText(this.changeDictionaryKey + " " + this.currentLanguage);
			} else {
				this.changeDictionaryMenu.setText(this.changeDictionaryKey);
			}
			this.menu.show(c, x, y);
		}
	}

	protected void uninstallSuggestionListener() {
		if (this.suggestionMenu != null) {
			for (int i = 0; i < this.suggestionMenu.getItemCount(); i++) {
				this.suggestionMenu.getItem(i).removeActionListener(this);
			}
		}
	}

	protected void installSuggestionListener() {
		if (this.suggestionMenu != null) {
			for (int i = 0; i < this.suggestionMenu.getItemCount(); i++) {
				this.suggestionMenu.getItem(i).addActionListener(this);
			}
		}
	}

	/**
	 * This method must not be overwrite
	 */
	@Override
	public void actionPerformed(final ActionEvent e) {
		// Save
		if (e.getSource().equals(this.saveMenu)) {
			final String word = e.getActionCommand();
			// Save in the current file
			if (this.saveWordInDictionary(word)) {
				this.fireGlobalWordAdded(this.currentLanguage, this.locale, word);
			}
		} else if (e.getSource().equals(this.deleteMenu)) {
			final String word = e.getActionCommand();
			// Save in the current file
			if (this.deleteWordInDictionary(word)) {
				this.fireGlobalWordRemoved(this.currentLanguage, this.locale, word);
			}
		} else if (e.getSource().equals(this.changeDictionaryMenu)) {
			final URL url = this.getEnabledDictionary();
			if (url != null) {
				this.loadDictionary(url);
				this.runAnalyzer(0);
			}
		} else if (e.getSource() instanceof JMenuItem) {
			final JMenuItem el = (JMenuItem) e.getSource();
			DictionaryDataField.logger.debug("Pulsada sugerencia");
			// Change the word
			final String ac = el.getActionCommand();
			final int spaceIndex = ac.lastIndexOf(" ");
			final String orig = ac.substring(0, spaceIndex);
			final String index = ac.substring(spaceIndex + 1, ac.length());
			DictionaryDataField.logger.debug("Word: '" + orig + "' , Index: '" + index + "'");
			int ind = -1;
			try {
				ind = Integer.parseInt(index);
			} catch (final Exception ex) {
				DictionaryDataField.logger.error(null, ex);
			}
			this.replaceWord(ind, orig, el.getText());
		}

	}

	protected URL getEnabledDictionary() {
		// Then search the dictionaries in the file with the list
		try {
			final Properties prop = new Properties();
			final URL urlListFile = this.getClass().getClassLoader().getResource(this.dictionaryListFile);
			if (urlListFile == null) {
				this.parentForm.message("M_FILE_DICTIONARY_LIST_NOT_FOUND", Form.ERROR_MESSAGE);
				return null;
			}
			prop.load(urlListFile.openStream());

			final Vector v = new Vector();
			final List vURLS = new Vector();
			final Enumeration enumLanguages = prop.propertyNames();
			while (enumLanguages.hasMoreElements()) {
				final Object oLanguage = enumLanguages.nextElement();
				final Object oFile = prop.get(oLanguage);

				final URL url = this.getClass().getClassLoader().getResource(oFile.toString());
				if (url != null) {
					v.add(v.size(), oLanguage);
					vURLS.add(vURLS.size(), url);
				} else {
					DictionaryDataField.logger.debug(this.getClass().toString() + ": File not found " + oFile);
				}
			}

			// Now show a window
			final JList list = new JList(v);

			list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			final JDialog d = new JDialog(this.parentFrame, "Diccionarios Disponibles", true);
			d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			final JScrollPane scroll = new JScrollPane(list);
			final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			final JButton closeButton = new JButton("datafield.select");
			buttonPanel.add(closeButton);
			d.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
			d.getContentPane().add(scroll);
			d.pack();
			d.setSize(Math.max(250, d.getWidth()), d.getHeight());
			ApplicationManager.center(d);
			closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (list.getSelectedIndex() >= 0) {
						d.setVisible(false);
						d.dispose();
					}
				}
			});
			d.setVisible(true);
			if (list.getSelectedIndex() >= 0) {
				this.currentLanguage = v.get(list.getSelectedIndex()).toString();
				return (URL) vURLS.get(list.getSelectedIndex());
			} else {
				return null;
			}
		} catch (final Exception e) {
			DictionaryDataField.logger.trace(null, e);
			this.parentForm.message(e.getMessage(), Form.ERROR_MESSAGE);
			return null;
		}
	}

	protected boolean saveWordInDictionary(final String word) {
		try {
			if (this.currentLanguage != null) {
				final Properties prop = new Properties();
				final URL urlListFile = this.getClass().getClassLoader().getResource(this.dictionaryListFile);
				if (urlListFile == null) {
					this.parentForm.message("ColorDataField.dictionaryListFileNotFound", Form.ERROR_MESSAGE);
					return false;
				}
				prop.load(urlListFile.openStream());

				final String sFile = prop.getProperty(this.currentLanguage);
				if (sFile != null) {
					final URL urlFile = this.getClass().getClassLoader().getResource(sFile);
					final boolean correct = this.addFile(urlFile, word.toLowerCase());
					if (correct) {
						this.currentWordList.add(word.toLowerCase());
						this.runAnalyzer(0);
					} else {
						DictionaryDataField.logger.debug("Error adding word to the dictorary");
					}
					return correct;
				} else {
					DictionaryDataField.logger.debug(this.getClass().toString()
							+ ": Word has not been added to the dictionary. Language file not found "
							+ this.currentLanguage);
					return false;
				}
			} else {
				DictionaryDataField.logger.debug("Dictionary not specified");
				return false;
			}
		} catch (final IOException e) {
			DictionaryDataField.logger.error(null, e);
			return false;
		}
	}

	protected boolean addFile(final URL urlFile, final String word) {
		BufferedWriter bw = null;
		Writer writer = null;
		try {
			// Load the dictionary. It is a word list.
			// Each word in a new line
			if (DictionaryDataField.DEBUG_DICTIONARY) {
				DictionaryDataField.logger.debug(urlFile.toString());
				DictionaryDataField.logger.debug(urlFile.getProtocol());
				DictionaryDataField.logger.debug(urlFile.getFile());
				DictionaryDataField.logger.debug(urlFile.getPath());
				DictionaryDataField.logger.debug(urlFile.getHost());
			}
			if (urlFile.getProtocol().equalsIgnoreCase("file")) {
				writer = new FileWriter(urlFile.getFile(), true);
				bw = new BufferedWriter(writer);
				bw.newLine();
				bw.write(word);
				bw.flush();
				bw.close();
				writer.close();
				return true;
			} else {
				DictionaryDataField.logger
				.debug("This component does not support data output with protocols different than 'file'");
				return false;
			}
		} catch (final Exception e) {
			DictionaryDataField.logger.error(null, e);
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
		}
	}

	protected void saveFileOutJar(final URL urlFile) {
		BufferedWriter bw = null;
		Writer writer = null;
		try {
			// Dictionary can no be saved into the jar.
			// First of all because a problem with the signatures. In other hand
			// new updates of the jar file
			// will clean our dictionary.
			// Example:
			// jar:file:/C:/package/es.jar!/com/ontimize/gui/resources/dictionary.txt
			// Then save the word list in a file in the same path that the jar
			// file
			final String sFile = urlFile.getFile();
			final URL urlF = new URL(sFile);
			final String pathComplete = urlF.getFile();
			// Now remove until the '!'
			final String path = pathComplete.substring(0, pathComplete.indexOf("!"));
			// path is the path to the jar file.
			final File f = new File(path);
			final String baseDirectoryPath = f.getParent();
			// Now get the path of the jar file to save the dictionary with the
			// same structure
			final String inputPath = pathComplete.substring(pathComplete.indexOf("!") + 1, pathComplete.length());
			if (DictionaryDataField.DEBUG_DICTIONARY) {
				DictionaryDataField.logger.debug(path);
				DictionaryDataField.logger.debug(baseDirectoryPath);
				DictionaryDataField.logger.debug(inputPath);
			}
			// Save it
			writer = new FileWriter(baseDirectoryPath + inputPath);
			bw = new BufferedWriter(writer);
			for (int i = 0; i < this.currentWordList.size(); i++) {
				bw.newLine();
				bw.write(this.currentWordList.get(i).toString());
				bw.flush();
			}
			bw.close();
			writer.close();
		} catch (final Exception e) {
			DictionaryDataField.logger.error(null, e);
		}
	}

	protected boolean deleteWordInDictionary(final String word) {
		try {
			if (this.currentLanguage != null) {
				final Properties prop = new Properties();
				final URL urlListFile = this.getClass().getClassLoader().getResource(this.dictionaryListFile);
				if (urlListFile == null) {
					this.parentForm.message("M_FILE_DICTIONARY_LIST_NOT_FOUND", Form.ERROR_MESSAGE);
					return false;
				}
				prop.load(urlListFile.openStream());

				final String sFile = prop.getProperty(this.currentLanguage);
				if (sFile != null) {
					final URL urlFile = this.getClass().getClassLoader().getResource(sFile);
					final boolean correct = this.deleteWord(urlFile, word);
					if (correct) {
						this.currentWordList.remove(word);
						this.runAnalyzer(0);
					} else {
						DictionaryDataField.logger.debug("Dictionary word can no be deleted");
					}
					return correct;
				} else {
					DictionaryDataField.logger.debug(this.getClass().toString()
							+ ": Word can no be deleted. Language file not found " + this.currentLanguage);
					return false;
				}
			} else {
				DictionaryDataField.logger.debug("Dictionary not specified");
				return false;
			}
		} catch (final IOException e) {
			DictionaryDataField.logger.error(null, e);
			return false;
		}
	}

	protected boolean deleteWord(final URL urlFile, final String word) {
		BufferedWriter bw = null;
		Writer writer = null;
		try {
			// Load the dictionary. It is a word list.
			// Each word in a new line
			if (DictionaryDataField.DEBUG_DICTIONARY) {
				DictionaryDataField.logger.debug(urlFile.toString());
				DictionaryDataField.logger.debug(urlFile.getProtocol());
				DictionaryDataField.logger.debug(urlFile.getFile());
				DictionaryDataField.logger.debug(urlFile.getPath());
				DictionaryDataField.logger.debug(urlFile.getHost());
			}
			if (urlFile.getProtocol().equalsIgnoreCase("file")) {
				writer = new FileWriter(urlFile.getFile(), false);
				bw = new BufferedWriter(writer);
				for (int i = 0; i < this.currentWordList.size(); i++) {
					final Object p = this.currentWordList.get(i);
					if (!p.toString().equalsIgnoreCase(word)) {
						bw.newLine();
						bw.write(word);
					}
				}
				bw.flush();
				bw.close();
				writer.close();
				return true;
			} else {
				DictionaryDataField.logger
				.debug("This component does not support data output with protocols different than 'file' ");
				return false;
			}
		} catch (final Exception e) {
			DictionaryDataField.logger.error(null, e);
			return false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (final Exception e) {
				DictionaryDataField.logger.trace(null, e);
			}
		}
	}

	protected void fireGlobalWordAdded(final String lang, final Locale l, final String word) {
		if (DictionaryDataField.globalListener != null) {
			DictionaryDataField.globalListener.wordAdded(new DictionaryEvent(this, lang, word));
		}
	}

	protected void fireGlobalWordRemoved(final String lang, final Locale l, final String word) {
		if (DictionaryDataField.globalListener != null) {
			DictionaryDataField.globalListener.wordRemoved(new DictionaryEvent(this, lang, word));
		}
	}

	public static void setDictionaryChangeListener(final DictionaryChangeListener listener) {
		DictionaryDataField.globalListener = listener;
	}

	@Override
	public void setParentFrame(final Frame m) {
		this.parentFrame = m;
	}

	protected void replaceWord(final int i, final String orig, final String newWord) {
		try {
			this.doc.remove(i, orig.length());
			this.doc.insertString(i, newWord, null);
		} catch (final Exception ex) {
			DictionaryDataField.logger.error(null, ex);
		}
	}

	/**
	 * Convenience function. Enables or disables the event notifications provoked from inner listener.
	 * So, it is easy to distinguish between internal(by program) and external (from user interface)
	 * modifications of field. It should be disabled only when application modifies the content
	 * internally.
	 * @param enable
	 */
	protected void enableListenerInterno(final boolean enable) {
		this.doc.setInnerListenerEnabled(enable);
	}

	protected List getSuggestions(final String word) {
		final List sugs = new Vector();
		String wordWithoutAccent = word.replace('á', 'a');
		wordWithoutAccent = wordWithoutAccent.replace('é', 'e');
		wordWithoutAccent = wordWithoutAccent.replace('í', 'i');
		wordWithoutAccent = wordWithoutAccent.replace('ó', 'o');
		wordWithoutAccent = wordWithoutAccent.replace('ú', 'u');
		// Show the suggestions replacing b by v,
		final String wordWithV = wordWithoutAccent.replace('b', 'v');
		for (int i = 0; i < this.currentWordList.size(); i++) {
			final String p = (String) this.currentWordList.get(i);
			String docWordWithoutAccent = p.replace('á', 'a');
			docWordWithoutAccent = docWordWithoutAccent.replace('é', 'e');
			docWordWithoutAccent = docWordWithoutAccent.replace('í', 'i');
			docWordWithoutAccent = docWordWithoutAccent.replace('ó', 'o');
			docWordWithoutAccent = docWordWithoutAccent.replace('ú', 'u');
			final String dictionaryWordWithV = docWordWithoutAccent.replace('b', 'v');
			if (dictionaryWordWithV.equalsIgnoreCase(wordWithV)) {
				sugs.add(sugs.size(), p);
			}
		}
		// Put the previous and the next words
		int previousIndex = 0;
		for (int i = 0; i < this.currentWordList.size(); i++) {
			final String p = (String) this.currentWordList.get(i);
			if (this.collator.compare(p, word) >= 0) {
				previousIndex = i - 1;
				break;
			}
		}
		if (previousIndex < this.currentWordList.size()) {
			if ((previousIndex >= 0) && !sugs.contains(this.currentWordList.get(previousIndex))) {
				sugs.add(this.currentWordList.get(previousIndex));
			}
			if (((previousIndex + 1) < this.currentWordList.size())
					&& !(sugs.contains(this.currentWordList.get(previousIndex + 1)))) {
				sugs.add(this.currentWordList.get(previousIndex + 1));
			}
		}
		return sugs;
	}

}
