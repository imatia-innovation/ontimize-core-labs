package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.FontUIResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;

/**
 * Class used to select a new font to set
 */

public class FontSelector implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(FontSelector.class);

	public static final String checkBox = "CheckBox.font";

	public static final String tree = "Tree.font";

	public static final String toggleButton = "ToggleButton.font";

	public static final String menuItemRadio = "RadioButtonMenuItem.font";

	public static final String menuItem = "MenuItem.font";

	public static final String menu = "Menu.font";

	public static final String panel = "Panel.font";

	public static final String textArea = "TextArea.font";

	public static final String viewport = "Viewport.font";

	public static final String tableHeader = "TableHeader.font";

	public static final String textField = "TextField.font";

	public static final String optionPane = "OptionPane.font";

	public static final String menuBar = "MenuBar.font";

	public static final String button = "Button.font";

	public static final String label = "Label.font";

	public static final String list = "List.font";

	public static final String table = "Table.font";

	public static final String panelTabs = "TabbedPane.font";

	public static final String radioButton = "RadioButton.font";

	public static final String menuItemCheck = "CheckBoxMenuItem.font";

	public static final String menuPopup = "PopupMenu.font";

	public static final String border = "TitledBorder.font";

	public static final String comboBox = "ComboBox.font";

	public static final String spinner = "Spinner.font";

	Frame parentFrame = null;

	FontDialog dialog = null;

	static String[] uIKeys = { FontSelector.checkBox, FontSelector.tree, FontSelector.toggleButton,
			FontSelector.menuItemRadio, FontSelector.menuItem, FontSelector.menu, FontSelector.panel,
			FontSelector.textArea, FontSelector.tableHeader, FontSelector.textField, FontSelector.optionPane,
			FontSelector.menuBar, FontSelector.button, FontSelector.label, FontSelector.list, FontSelector.table,
			FontSelector.panelTabs, FontSelector.radioButton, FontSelector.menuItemCheck, FontSelector.menuPopup,
			FontSelector.border, FontSelector.comboBox, FontSelector.spinner };

	protected static SimpleFontsDialog dF = null;

	protected static class SimpleFontsDialog extends EJDialog implements Internationalization {

		protected static String acceptKey = "application.accept";

		protected static String cancelKey = "application.cancel";

		protected static String sourceKey = "font";

		protected String title = null;

		protected JComboBox fontCombo = null;

		protected JComboBox sizeCombo = null;

		protected JTextArea exampleJText = new JTextArea();

		protected static String example = "selected_font_sample";

		protected JButton acceptButton = new JButton("application.accept");

		protected JButton cancelButton = new JButton("application.cancel");

		protected Font currentFont = null;

		protected JPanel contentPanel = null;

		public SimpleFontsDialog(final Frame f, final String title) {
			super(f, title, true);
			this.title = title;
			this.acceptButton.setMargin(new Insets(2, 2, 2, 2));
			this.cancelButton.setMargin(new Insets(2, 2, 2, 2));
			this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.exampleJText.setText(SimpleFontsDialog.example);
			this.exampleJText.setOpaque(false);
			this.exampleJText.setEditable(false);
			this.exampleJText.setWrapStyleWord(true);
			this.exampleJText.setLineWrap(true);
			this.exampleJText.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			this.exampleJText.setRows(6);
			final Vector vSizes = new Vector();
			vSizes.add(new Integer(6));
			vSizes.add(new Integer(7));
			vSizes.add(new Integer(8));
			vSizes.add(new Integer(9));
			vSizes.add(new Integer(10));
			vSizes.add(new Integer(11));
			vSizes.add(new Integer(12));
			vSizes.add(new Integer(14));
			vSizes.add(new Integer(16));
			vSizes.add(new Integer(18));
			vSizes.add(new Integer(20));
			vSizes.add(new Integer(24));
			vSizes.add(new Integer(28));
			vSizes.add(new Integer(36));
			vSizes.add(new Integer(46));
			vSizes.add(new Integer(60));
			this.sizeCombo = new JComboBox(vSizes);
			final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final String envfonts[] = gEnv.getAvailableFontFamilyNames();
			final Vector vector = new Vector();
			for (int i = 1; i < envfonts.length; i++) {
				try {
					final Font fAux = new Font(envfonts[i], Font.PLAIN, 12);
					vector.add(envfonts[i]);
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						FontSelector.logger.error(null, e);
					} else {
						FontSelector.logger.trace(null, e);
					}
				}
			}
			this.fontCombo = new JComboBox(vector);
			this.fontCombo.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						final Object sel = SimpleFontsDialog.this.fontCombo.getSelectedItem();
						SimpleFontsDialog.this.currentFont = new FontUIResource(new Font(sel.toString(), Font.PLAIN,
								SimpleFontsDialog.this.exampleJText.getFont().getSize()));
						SimpleFontsDialog.this.exampleJText.setFont(SimpleFontsDialog.this.currentFont);
					}
				}
			});
			this.sizeCombo.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						int size = 12;
						try {
							size = ((Number) SimpleFontsDialog.this.sizeCombo.getSelectedItem()).intValue();
						} catch (final Exception ex) {
							FontSelector.logger.error(null, ex);
						}
						SimpleFontsDialog.this.currentFont = new FontUIResource(
								SimpleFontsDialog.this.exampleJText.getFont().deriveFont((float) size));
						SimpleFontsDialog.this.exampleJText.setFont(SimpleFontsDialog.this.currentFont);
					}
				}
			});
			final JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelNorte.add(this.fontCombo);
			panelNorte.add(this.sizeCombo);
			final JPanel buttonsPanel = new JPanel(new GridLayout(0, 1));
			buttonsPanel.add(this.acceptButton);
			buttonsPanel.add(this.cancelButton);
			this.acceptButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SimpleFontsDialog.this.currentFont = SimpleFontsDialog.this.exampleJText.getFont();
					SimpleFontsDialog.this.setVisible(false);
				}
			});
			this.cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SimpleFontsDialog.this.currentFont = null;
					SimpleFontsDialog.this.setVisible(false);
				}
			});
			this.contentPanel = new JPanel(new GridBagLayout());
			this.getContentPane().add(this.contentPanel);
			this.contentPanel.setBorder(new TitledBorder("font"));
			this.contentPanel.add(panelNorte, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 2), 0, 0));
			this.contentPanel.add(buttonsPanel, new GridBagConstraints(1, 0, 1, 2, 0, 0, GridBagConstraints.NORTH,
					GridBagConstraints.NONE, new Insets(5, 2, 2, 2), 0, 0));
			this.contentPanel.add(new JScrollPane(this.exampleJText),
					new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));
			this.fontCombo.setSelectedIndex(0);
			this.sizeCombo.setSelectedIndex(6);
			this.pack();
			ApplicationManager.center(this);
		}

		public Font showDialog(final Font f) {
			if (f != null) {
				this.fontCombo.setSelectedItem(f.getName());
				this.sizeCombo.setSelectedItem(new Integer(f.getSize()));
			}
			this.setVisible(true);
			return this.currentFont;
		}

		@Override
		public void setResourceBundle(final ResourceBundle res) {
			this.setTitle(ApplicationManager.getTranslation(this.title, res));
			this.acceptButton.setText(ApplicationManager.getTranslation(SimpleFontsDialog.acceptKey, res));
			this.cancelButton.setText(ApplicationManager.getTranslation(SimpleFontsDialog.cancelKey, res));
			this.contentPanel
			.setBorder(new TitledBorder(ApplicationManager.getTranslation(SimpleFontsDialog.sourceKey, res)));
			this.exampleJText.setText(ApplicationManager.getTranslation(SimpleFontsDialog.example, res));
		}

		@Override
		public void setComponentLocale(final Locale l) {

		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		protected void processKeyEvent(final KeyEvent e) {
			if ((e.getKeyCode() == KeyEvent.VK_ESCAPE) && (e.getID() == KeyEvent.KEY_PRESSED)) {
				this.currentFont = null;
				this.setVisible(false);
				return;
			}
			super.processKeyEvent(e);
		}

	}

	class FontDialog extends JDialog {

		ButtonGroup buttonGroup = new ButtonGroup();

		JButton apply = new JButton("Apply");

		JButton close = new JButton("close");

		JButton reset = new JButton("Reset");

		JList list = new JList();

		Font font = null;

		JTextField text = new JTextField("Show the selected font");

		JRadioButton treeButton = new JRadioButton();

		JRadioButton tableButton = new JRadioButton();

		JRadioButton menuButton = new JRadioButton();

		JRadioButton textFieldButton = new JRadioButton();

		JRadioButton bButtons = new JRadioButton();

		JRadioButton allButtons = new JRadioButton();

		String[] fontSizes = { "6", "8", "10", "12", "14", "16", "20", "24", "28", "32", "38", "44", "50" };

		JComboBox fontSize = new JComboBox(this.fontSizes);

		public FontDialog(final Frame parentFrame, final boolean supportingEuro) {
			super(parentFrame, true);
			// Add a JList with the fonts.
			final JPanel buttonsPanel = new JPanel(new GridBagLayout());
			final JPanel radioButtonsPanel = new JPanel(new GridBagLayout());
			this.getContentPane().add(radioButtonsPanel, BorderLayout.NORTH);
			radioButtonsPanel.add(this.treeButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.menuButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.tableButton, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.textFieldButton, new GridBagConstraints(3, 0, 1, 1, 1, 0,
					GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.bButtons, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.allButtons, new GridBagConstraints(5, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			radioButtonsPanel.add(this.fontSize);
			this.fontSize.setSelectedIndex(4);
			radioButtonsPanel.add(this.text, new GridBagConstraints(0, 1, 6, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			buttonsPanel.add(this.apply, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			buttonsPanel.add(this.close, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			buttonsPanel.add(this.reset, new GridBagConstraints(2, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.buttonGroup.add(this.treeButton);
			this.buttonGroup.add(this.menuButton);
			this.buttonGroup.add(this.tableButton);
			this.buttonGroup.add(this.textFieldButton);
			this.buttonGroup.add(this.bButtons);
			this.buttonGroup.add(this.allButtons);

			this.treeButton.setText(ApplicationManager.getTranslation("Trees"));

			this.tableButton.setText(ApplicationManager.getTranslation("Tables"));
			this.textFieldButton.setText(ApplicationManager.getTranslation("Fields"));
			this.menuButton.setText(ApplicationManager.getTranslation("Menu"));
			this.bButtons.setText(ApplicationManager.getTranslation("Buttons"));
			this.allButtons.setText(ApplicationManager.getTranslation("All"));
			this.treeButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.treeButton.isSelected()) {
						final Object o = UIManager.get(FontSelector.tree);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));
							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object oFontName = model.getElementAt(i);
								if ((oFontName != null)
										&& oFontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.tableButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.tableButton.isSelected()) {
						final Object o = UIManager.get(FontSelector.table);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));

							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object fontName = model.getElementAt(i);
								if ((fontName != null)
										&& fontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.textFieldButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.textFieldButton.isSelected()) {
						final Object o = UIManager.get(FontSelector.textField);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));

							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object fontName = model.getElementAt(i);
								if ((fontName != null)
										&& fontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.menuButton.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.menuButton.isSelected()) {
						final Object o = UIManager.get(FontSelector.menu);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));

							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object fontName = model.getElementAt(i);
								if ((fontName != null)
										&& fontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.allButtons.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.allButtons.isSelected()) {
						final Object o = UIManager.get(FontSelector.label);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));
							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object fontName = model.getElementAt(i);
								if ((fontName != null)
										&& fontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.bButtons.addChangeListener(new ChangeListener() {

				@Override
				public void stateChanged(final ChangeEvent e) {
					if (FontDialog.this.bButtons.isSelected()) {
						final Object o = UIManager.get(FontSelector.button);
						if (o instanceof Font) {
							final int t = ((Font) o).getSize();
							FontDialog.this.fontSize.setSelectedItem(Integer.toString(t));
							final ListModel model = FontDialog.this.list.getModel();
							for (int i = 0; i < model.getSize(); i++) {
								final Object fontName = model.getElementAt(i);
								if ((fontName != null)
										&& fontName.toString().equalsIgnoreCase(((Font) o).getFamily())) {
									FontDialog.this.list.setSelectedIndex(i);
									FontDialog.this.list.ensureIndexIsVisible(i);
									break;
								}
							}
						}
					}
				}
			});
			this.text.setEditable(false);
			this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
			this.getContentPane().add(new JScrollPane(this.list));
			final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
			final String envfonts[] = gEnv.getAvailableFontFamilyNames();
			final Vector vector = new Vector();
			for (int i = 1; i < envfonts.length; i++) {
				try {
					final Font f = new Font(envfonts[i], Font.PLAIN, 12);
					if (supportingEuro) {
						if (FontSelector.supportsEuroSymbol(f)) {
							vector.add(envfonts[i]);
						}
					} else {
						vector.add(envfonts[i]);
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						FontSelector.logger.error(null, e);
					} else {
						FontSelector.logger.trace(null, e);
					}
				}
			}
			this.list.setListData(vector);
			this.list.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					final Object oSelectedValue = FontDialog.this.list.getSelectedValue();
					if (oSelectedValue != null) {
						FontDialog.this.text.setFont(new Font(oSelectedValue.toString(), Font.PLAIN, 12));
					}
				}
			});
			this.apply.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Object oSelectedValue = FontDialog.this.list.getSelectedValue();
					if (oSelectedValue == null) {
						FontDialog.this.font = null;
						return;
					} else {
						try {
							int size = 12;
							try {
								size = Integer.parseInt(FontDialog.this.fontSize.getSelectedItem().toString());
							} catch (final Exception ex) {
								FontSelector.logger.trace(null, ex);
							}
							FontDialog.this.font = new FontUIResource(
									new Font(oSelectedValue.toString(), Font.PLAIN, size));
							if (FontDialog.this.treeButton.isSelected()) {
								UIManager.put(FontSelector.tree, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							} else if (FontDialog.this.tableButton.isSelected()) {
								UIManager.put(FontSelector.table, FontDialog.this.font);
								UIManager.put(FontSelector.tableHeader, FontDialog.this.font);
								UIManager.put(FontSelector.viewport, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							} else if (FontDialog.this.menuButton.isSelected()) {
								UIManager.put(FontSelector.menu, FontDialog.this.font);
								UIManager.put(FontSelector.menuItem, FontDialog.this.font);
								UIManager.put(FontSelector.menuItemCheck, FontDialog.this.font);
								UIManager.put(FontSelector.menuItemRadio, FontDialog.this.font);
								UIManager.put(FontSelector.menuPopup, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							} else if (FontDialog.this.textFieldButton.isSelected()) {
								UIManager.put(FontSelector.textField, FontDialog.this.font);
								UIManager.put(FontSelector.label, FontDialog.this.font);
								UIManager.put(FontSelector.textArea, FontDialog.this.font);
								UIManager.put(FontSelector.border, FontDialog.this.font);
								UIManager.put(FontSelector.comboBox, FontDialog.this.font);
								UIManager.put(FontSelector.list, FontDialog.this.font);
								UIManager.put(FontSelector.optionPane, FontDialog.this.font);
								UIManager.put(FontSelector.panel, FontDialog.this.font);
								UIManager.put(FontSelector.panelTabs, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							} else if (FontDialog.this.bButtons.isSelected()) {
								UIManager.put(FontSelector.checkBox, FontDialog.this.font);
								UIManager.put(FontSelector.radioButton, FontDialog.this.font);
								UIManager.put(FontSelector.button, FontDialog.this.font);
								UIManager.put(FontSelector.toggleButton, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							} else if (FontDialog.this.allButtons.isSelected()) {
								UIManager.put(FontSelector.tree, FontDialog.this.font);
								UIManager.put(FontSelector.table, FontDialog.this.font);
								UIManager.put(FontSelector.tableHeader, FontDialog.this.font);
								UIManager.put(FontSelector.viewport, FontDialog.this.font);
								UIManager.put(FontSelector.menu, FontDialog.this.font);
								UIManager.put(FontSelector.menuItem, FontDialog.this.font);
								UIManager.put(FontSelector.menuItemCheck, FontDialog.this.font);
								UIManager.put(FontSelector.menuItemRadio, FontDialog.this.font);
								UIManager.put(FontSelector.menuPopup, FontDialog.this.font);
								UIManager.put(FontSelector.textField, FontDialog.this.font);
								UIManager.put(FontSelector.label, FontDialog.this.font);
								UIManager.put(FontSelector.textArea, FontDialog.this.font);
								UIManager.put(FontSelector.border, FontDialog.this.font);
								UIManager.put(FontSelector.comboBox, FontDialog.this.font);
								UIManager.put(FontSelector.list, FontDialog.this.font);
								UIManager.put(FontSelector.optionPane, FontDialog.this.font);
								UIManager.put(FontSelector.panel, FontDialog.this.font);
								UIManager.put(FontSelector.panelTabs, FontDialog.this.font);
								UIManager.put(FontSelector.checkBox, FontDialog.this.font);
								UIManager.put(FontSelector.radioButton, FontDialog.this.font);
								UIManager.put(FontSelector.button, FontDialog.this.font);
								UIManager.put(FontSelector.toggleButton, FontDialog.this.font);
								SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
							}
						} catch (final Exception ex) {
							FontSelector.logger.trace(null, ex);
							FontDialog.this.font = null;
						}
						// setVisible(false);
					}
				}
			});

			this.close.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					FontDialog.this.font = null;
					FontDialog.this.setVisible(false);
				}
			});

			this.reset.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final UIDefaults uiDef = UIManager.getLookAndFeelDefaults();
					for (int i = 0; i < FontSelector.uIKeys.length; i++) {
						UIManager.put(FontSelector.uIKeys[i], uiDef.get(FontSelector.uIKeys[i]));
					}
					SwingUtilities.updateComponentTreeUI(FontSelector.this.parentFrame);
				}
			});

			this.fontSize.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(final ItemEvent e) {
					if (e.getStateChange() == ItemEvent.SELECTED) {
						final Object oSelectedValue = FontDialog.this.list.getSelectedValue();
						if (oSelectedValue != null) {
							int size = 12;
							try {
								size = Integer.parseInt(FontDialog.this.fontSize.getSelectedItem().toString());
							} catch (final Exception ex) {
								FontSelector.logger.error(null, ex);
							}
							FontDialog.this.font = new FontUIResource(
									new Font(oSelectedValue.toString(), Font.PLAIN, size));
							FontDialog.this.text.setFont(FontDialog.this.font);
							FontDialog.this.pack();
						}
					}
				}
			});

			this.treeButton.setSelected(true);
			this.list.setFont(this.list.getFont().deriveFont((float) 12));
			this.treeButton.setFont(this.treeButton.getFont().deriveFont((float) 12));
			this.bButtons.setFont(this.bButtons.getFont().deriveFont((float) 12));
			this.menuButton.setFont(this.menuButton.getFont().deriveFont((float) 12));
			this.tableButton.setFont(this.tableButton.getFont().deriveFont((float) 12));
			this.textFieldButton.setFont(this.textFieldButton.getFont().deriveFont((float) 12));
			this.allButtons.setFont(this.allButtons.getFont().deriveFont((float) 12));
			this.fontSize.setFont(this.fontSize.getFont().deriveFont((float) 12));
			this.apply.setFont(this.apply.getFont().deriveFont((float) 12));
			this.close.setFont(this.close.getFont().deriveFont((float) 12));
			this.reset.setFont(this.reset.getFont().deriveFont((float) 12));
			this.pack();

			// Center in the window
			final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		}

	};

	public FontSelector(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

	public Font showAvaliableFonts(final boolean supportingEuro) {
		if (this.dialog == null) {
			this.dialog = new FontDialog(this.parentFrame, supportingEuro);
		}
		this.dialog.setVisible(true);
		return this.dialog.font;
	}

	/**
	 * Show the font selector and returns the selected font or null if no font has been selected
	 * @param parentFrame
	 * @param title
	 * @return
	 */
	public static Font showFontSelector(final Frame parentFrame, final String title) {
		return FontSelector.showFontSelector(parentFrame, title, null, null);
	}

	public static Font showFontSelector(final Frame parentFrame, final String title, final ResourceBundle res) {
		return FontSelector.showFontSelector(parentFrame, title, res, null);
	}

	public static Font showFontSelector(final Frame parentFrame, final String title, final ResourceBundle res, final Font font) {
		if (FontSelector.dF == null) {
			FontSelector.dF = new SimpleFontsDialog(parentFrame, title);
		}
		FontSelector.dF.setResourceBundle(res);
		return FontSelector.dF.showDialog(font);
	}

	@Override
	public void free() {
		if (this.dialog != null) {
			this.dialog.dispose();
		}
		if (FontSelector.logger.isDebugEnabled()) {
			FontSelector.logger.debug(this.getClass().toString() + " Free.");
		}
	}


	public static boolean supportsEuroSymbol(final Font f) {
		return (f == null) || f.canDisplay('\u20AC');
	}

	public static void setFrameFont(final Frame applicationFrame, final Font font) {
		for (int i = 0; i < FontSelector.uIKeys.length; i++) {
			UIManager.put(FontSelector.uIKeys[i], new FontUIResource(font));
		}
		SwingUtilities.updateComponentTreeUI(applicationFrame);
	}

	public static void setDialogFont(final Dialog dialog, final Font font) {
		for (int i = 0; i < FontSelector.uIKeys.length; i++) {
			UIManager.put(FontSelector.uIKeys[i], new FontUIResource(font));
		}
		SwingUtilities.updateComponentTreeUI(dialog);
	}

	public static void setApplicationFont(final Application application, final Font font) {
		for (int i = 0; i < FontSelector.uIKeys.length; i++) {
			UIManager.put(FontSelector.uIKeys[i], new FontUIResource(font));
		}
		SwingUtilities.updateComponentTreeUI(application.getFrame());
	}

	public static Font getCurrentFont() {
		final Font defaultAppFont = PlafPreferences.getDefaultAppFont();
		if (defaultAppFont!=null){
			return defaultAppFont;
		}

		final Object o = UIManager.get(FontSelector.label);
		if (o instanceof FontUIResource) {
			return (FontUIResource) o;
		} else if (o instanceof Font) {
			return (Font) o;
		} else {
			return null;
		}
	}

	public static void setApplicationFontSize(final Application application, final int size) {


		if (application == null) {
			return;
		}

		final Font defaultAppFont = PlafPreferences.getDefaultAppFont();
		if (defaultAppFont!=null){
			final Font newFont = defaultAppFont.deriveFont((float) size);
			PlafPreferences.getInstance().setFontPreference(newFont);
		}

		for (int i = 0; i < FontSelector.uIKeys.length; i++) {
			try {
				final Font currentFont = (Font) UIManager.get(FontSelector.uIKeys[i]);
				final FontUIResource f = new FontUIResource(currentFont.deriveFont((float) size));
				UIManager.put(FontSelector.uIKeys[i], f);
				UIManager.getDefaults().put(FontSelector.uIKeys[i], f);
			} catch (final Exception e) {
				FontSelector.logger.error(null, e);
			}
		}
		if (SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.updateComponentTreeUI(application.getFrame());
			application.getFrame().setFont(application.getFrame().getFont().deriveFont((float) 10));
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					SwingUtilities.updateComponentTreeUI(application.getFrame());
					if (application.getFrame().getFont() != null) {
						application.getFrame().setFont(application.getFrame().getFont().deriveFont((float) 10));
					}
				}
			});
		}
	}

	public static void setUIManagerFontSize(final int size) {
		for (int i = 0; i < FontSelector.uIKeys.length; i++) {
			try {
				final Font currentFont = (Font) UIManager.get(FontSelector.uIKeys[i]);
				final FontUIResource f = new FontUIResource(currentFont.deriveFont((float) size));
				UIManager.put(FontSelector.uIKeys[i], f);
				UIManager.getDefaults().put(FontSelector.uIKeys[i], f);
			} catch (final Exception e) {
				FontSelector.logger.error(null, e);
			}
		}
	}

}
