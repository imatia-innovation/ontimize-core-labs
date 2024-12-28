package com.ontimize.gui.field;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.ActionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.FindDialog;
import com.ontimize.gui.FormatText;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.actions.CopyAction;
import com.ontimize.gui.field.html.actions.CutAction;
import com.ontimize.gui.field.html.actions.DefaultAction;
import com.ontimize.gui.field.html.actions.HTMLAlignAction;
import com.ontimize.gui.field.html.actions.HTMLEditorActionFactory;
import com.ontimize.gui.field.html.actions.HTMLElementPropertiesAction;
import com.ontimize.gui.field.html.actions.HTMLFontColorAction;
import com.ontimize.gui.field.html.actions.HTMLImageAction;
import com.ontimize.gui.field.html.actions.HTMLInlineAction;
import com.ontimize.gui.field.html.actions.HTMLTableAction;
import com.ontimize.gui.field.html.actions.HTMLTextEditAction;
import com.ontimize.gui.field.html.actions.HTMLViewerAction;
import com.ontimize.gui.field.html.actions.PasteAction;
import com.ontimize.gui.field.html.utils.ActionPerformedListener;
import com.ontimize.gui.field.html.utils.CompoundUndoManager;
import com.ontimize.gui.field.html.utils.OHTMLEditorKit;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.templates.ITemplateField;

/**
 * Main class to implement a specific HTML data field.
 * <p>
 *
 * @author Imatia Innovation
 */

public class HTMLShefDataField extends MemoDataField
implements AdvancedDataComponent, ITemplateField, ActionPerformedListener {

	private static final Logger logger = LoggerFactory.getLogger(HTMLShefDataField.class);

	/**
	 * Variable used to expand the toolbar or not. It it is false, it means that toolbar is full
	 * expanded. By default it is false.
	 */
	public static boolean toolBarFiller = false;

	protected static final String HTML_BASE = "<html><head></head><body></body></html>";

	protected static final String TAB = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

	protected int minFontSize;

	protected int maxFontSize;

	protected int fontSizeStep;

	protected boolean opaquebuttons;

	protected boolean borderbuttons;

	protected MouseListener listenerHighlightButtons;

	protected int lastSelectedFontSize;

	@Override
	public void previousActionPerformed(final ActionEvent e) {
		this.valueSave = this.getValue();
	}

	@Override
	public void postActionPerformed(final ActionEvent e) {
		this.fireValueChanged(this.getValue(), this.valueSave, ValueEvent.USER_CHANGE);

	}

	protected JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1)) {

		protected static final String name = "TableButtonPanel";

		@Override
		public String getName() {
			return name;
		};
	};

	protected JComboBox fontSizeCombo = null;

	protected JComboBox paragraphCombo = null;

	protected JComboBox fontFamilyCombo = null;

	protected StyleContext context = new StyleContext();

	protected HTMLEditorKit editor;

	protected String plainTextColumn = null;

	protected boolean advancedQueryMode = false;

	protected HTMLViewerAction htmlViewerAction;

	protected JMenu stylesMenu;

	protected JMenu tableMenu;

	protected ButtonGroup alignButtonGroup = new ButtonGroup();

	protected ActionList actionList;

	protected CaretListener caretHandler;

	protected FocusListener focusHandler;

	protected DocumentListener textChangedHandler;

	protected ActionListener paragraphComboHandler;

	protected ActionListener fontChangeHandler;

	protected ActionListener fontSizeChangeHandler;

	protected boolean isWysTextChanged;

	/**
	 * Initializes parameters. Configure buttons and adds key events. XML definition could be contains
	 * an optional parameter: 'plaintextcolumn' specifying the identifier to save the text in plain
	 * format.
	 * <p>
	 * @param parameters the Map with parameters
	 */

	public HTMLShefDataField(final Map parameters) {
		super(parameters);
		this.configureButtonsPanelAndEditor();
		this.htmlViewerAction = new HTMLViewerAction((JEditorPane) this.dataField);
		this.dataField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {

				if ((e.getKeyCode() == KeyEvent.VK_H) && e.isControlDown() && e.isAltDown()) {
					HTMLShefDataField.this.htmlViewerAction
					.actionPerformed(new ActionEvent(e.getSource(), 0, "text_mode"));
				}
			}
		});
		this.plainTextColumn = (String) parameters.get("plaintextcolumn");

		this.registerKeystroke();
	}

	@Override
	protected void registerUndoableListener() {
		// Do not do nothing! Keystroke is now registered into method
		// registerKeystroke
	}

	@Override
	protected void registerUndoRedoActions() {
		// Do not do nothing! Action is now registered into method
		// registerKeystroke
	}

	protected void registerKeystroke() {
		if (this.actionList != null) {
			final InputMap iMap = this.dataField.getInputMap(JComponent.WHEN_FOCUSED);
			final ActionMap aMap = this.dataField.getActionMap();

			for (int i = 0; i < this.actionList.size(); i++) {
				final AbstractAction act = (AbstractAction) this.actionList.get(i);
				if (act != null) {
					final Object oKs = act.getValue(Action.ACCELERATOR_KEY);
					if (oKs instanceof KeyStroke) {
						iMap.put((KeyStroke) oKs, act.getValue(Action.NAME));
						aMap.put(act.getValue(Action.NAME), act);
					}

				}
			}
		}
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

	@Override
	protected void createPopupMenu() {

		if (this.popupMenu == null) {
			this.popupMenu = new ExtendedJPopupMenu();
			final ActionList actList = HTMLEditorActionFactory.createEditActionList();
			for (int i = 0; i < actList.size(); i++) {
				final Action act = (Action) actList.get(i);
				if (act == null) {
					this.popupMenu.addSeparator();
				} else {
					final JMenuItem item = HTMLEditorActionFactory.createMenuItem(act);// ActionUIFactory.getInstance().createMenuItem(act);
					if (act instanceof CutAction) {
						this.menuCut = item;
					} else if (act instanceof CopyAction) {
						this.menuCopy = item;
					} else if (act instanceof PasteAction) {
						this.menuPaste = item;
					}
					this.popupMenu.add(item);
				}
			}

			this.addHelpMenuPopup(this.popupMenu);
			this.popupMenu.addSeparator();
			this.searchMenu = new JMenuItem(MemoDataField.queryKey);
			try {
				if (this.resources != null) {
					this.searchMenu.setText(this.resources.getString(MemoDataField.queryKey));
				}
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					HTMLShefDataField.logger.debug(null, e);
				} else {
					HTMLShefDataField.logger.trace(null, e);
				}
			}
			this.popupMenu.add(this.searchMenu);
			this.searchMenu.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (HTMLShefDataField.this.dQuery == null) {
						HTMLShefDataField.this.dQuery = new FindDialog(HTMLShefDataField.this.parentFrame,
								(JTextComponent) HTMLShefDataField.this.dataField);
						HTMLShefDataField.this.dQuery.setResourceBundle(HTMLShefDataField.this.resources);
						HTMLShefDataField.this.dQuery.setComponentLocale(HTMLShefDataField.this.locale);
					}
					HTMLShefDataField.this.dQuery
					.show(((JTextComponent) HTMLShefDataField.this.dataField).getCaretPosition());
				}
			});

			// Styles...
			this.popupMenu.addSeparator();
			ActionList lst = HTMLEditorActionFactory.createInlineActionList();
			if (this.actionList != null) {
				this.actionList.addAll(lst);
			}
			this.stylesMenu = HTMLEditorActionFactory.createMenu(lst,
					ApplicationManager.getTranslation("HTMLShef.styles", this.resources));
			this.popupMenu.add(this.stylesMenu);

			// Element properties
			this.popupMenu.addSeparator();
			final Action objectPropertiesAction = new HTMLElementPropertiesAction();
			if (this.actionList != null) {
				this.actionList.add(objectPropertiesAction);
			}
			final JMenuItem item = HTMLEditorActionFactory.createMenuItem(objectPropertiesAction);// ActionUIFactory.getInstance().createMenuItem(objectPropertiesAction);
			this.popupMenu.add(item);

			// Special Table properties menu...
			this.tableMenu = new JMenu(ApplicationManager.getTranslation("HTMLShef.table", this.resources));
			lst = HTMLEditorActionFactory.createInsertTableElementActionList();
			if (this.actionList != null) {
				this.actionList.addAll(lst);
			}
			this.tableMenu.add(HTMLEditorActionFactory.createMenu(lst,
					ApplicationManager.getTranslation("HTMLShef.insert", this.resources)));

			lst = HTMLEditorActionFactory.createDeleteTableElementActionList();
			if (this.actionList != null) {
				this.actionList.addAll(lst);
			}
			this.tableMenu.add(HTMLEditorActionFactory.createMenu(lst,
					ApplicationManager.getTranslation("HTMLShef.delete", this.resources)));
			this.popupMenu.add(this.tableMenu);
			this.updateState();
		}
	}

	@Override
	protected void showPopupMenu(final Component source, final int x, final int y) {
		super.showPopupMenu(source, x, y);
		if (this.tableMenu != null) {
			this.tableMenu.setVisible(HTMLElementPropertiesAction.isTableElement((JEditorPane) this.dataField));
		}

	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (this.tableMenu != null) {
			this.tableMenu.setText(ApplicationManager.getTranslation("HTMLShef.table", this.resources));
		}
		if (this.stylesMenu != null) {
			this.stylesMenu.setText(ApplicationManager.getTranslation("HTMLShef.styles", this.resources));
		}
		if (this.popupMenu != null) {
			final int count = this.popupMenu.getComponentCount();
			for (int i = 0; i < count; i++) {
				final Component c = this.popupMenu.getComponent(i);
				if (c instanceof AbstractButton) {
					final Object act = ((AbstractButton) c).getAction();
					if (act instanceof AbstractAction) {
						String text = (String) ((AbstractAction) act).getValue("ID");
						if (text != null) {
							text = ApplicationManager.getTranslation(text, this.resources);
							((AbstractButton) c).setText(text);
						}
					}
					if (act instanceof Internationalization) {
						((Internationalization) act).setResourceBundle(res);
					}
				}
				if (c instanceof JMenu) {
					this.translateJMenu((JMenu) c);
				}
			}
		}
		if ((this.buttonsPanel != null) && (this.buttonsPanel.getComponentCount() > 0)) {
			for (int i = 0; i < this.buttonsPanel.getComponentCount(); i++) {
				final Component c = this.buttonsPanel.getComponent(i);
				if (c instanceof AbstractButton) {
					final Action act = ((AbstractButton) c).getAction();
					if (act != null) {
						String text = (String) ((AbstractAction) act).getValue("ID");
						if (text != null) {
							text = ApplicationManager.getTranslation(text, this.resources);
							((AbstractButton) c).setToolTipText(text);
						} else {
							((AbstractButton) c).setToolTipText(act.getValue(Action.NAME).toString());
						}
					}

				}
			}
		}
	}

	protected void translateJMenu(final JMenu menu) {
		if (menu != null) {
			final int count = menu.getItemCount();
			for (int i = 0; i < count; i++) {
				final JMenuItem menuItem = menu.getItem(i);
				if (menuItem != null) {
					final Object act = menuItem.getAction();
					if (act instanceof AbstractAction) {
						String text = (String) ((AbstractAction) act).getValue("ID");
						if (text != null) {
							text = ApplicationManager.getTranslation(text, this.resources);
							menuItem.setText(text);
						}
					} else if (menuItem.getText() != null) {
						final String text = ApplicationManager.getTranslation(menuItem.getText(), this.resources);
						menuItem.setText(text);
					}
				}

				if (menuItem instanceof JMenu) {
					this.translateJMenu((JMenu) menuItem);
				}

			}
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

		this.fontSizeCombo = new JComboBox(vData) {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.width = 70;
				return d;
			};
		};
		this.fontSizeCombo.addActionListener(this.fontSizeChangeHandler);
		this.fontSizeCombo.setRenderer(new DefaultListCellRenderer());

	}

	/**
	 * Creates a combo with paragraph styles.
	 */
	protected void createParagraphCombo() {

		// Paragraph style combo selector.
		final ActionList paraActions = new ActionList("paraActions");
		final ActionList lst = HTMLEditorActionFactory.createBlockElementActionList();
		paraActions.addAll(lst);
		final ActionList alst = HTMLEditorActionFactory.createListElementActionList();
		paraActions.addAll(alst);
		this.actionList.addAll(paraActions);

		this.paragraphCombo = new JComboBox(this.toArray(paraActions));
		this.paragraphCombo.addActionListener(this.paragraphComboHandler);
		this.paragraphCombo.setRenderer(new ParagraphComboRenderer());

		final PropertyChangeListener propLst = new PropertyChangeListener() {

			@Override
			public void propertyChange(final PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("selected")) {
					if (evt.getNewValue().equals(Boolean.TRUE)) {
						HTMLShefDataField.this.paragraphCombo
						.removeActionListener(HTMLShefDataField.this.paragraphComboHandler);
						HTMLShefDataField.this.paragraphCombo.setSelectedItem(evt.getSource());
						HTMLShefDataField.this.paragraphCombo
						.addActionListener(HTMLShefDataField.this.paragraphComboHandler);
					}
				}
			}
		};
		for (final Iterator it = paraActions.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (o instanceof DefaultAction) {
				((DefaultAction) o).addPropertyChangeListener(propLst);
			}
		}
	}

	/**
	 * Creates a combo with font families.
	 */
	protected void createFontFamilyCombo() {
		final Vector fonts = new Vector();
		fonts.add("Default");
		fonts.add("serif");
		fonts.add("sans-serif");
		fonts.add("monospaced");
		final GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts.addAll(Arrays.asList(gEnv.getAvailableFontFamilyNames()));

		this.fontFamilyCombo = new JComboBox(fonts);
		this.fontFamilyCombo.addActionListener(this.fontChangeHandler);
		this.fontFamilyCombo.setRenderer(new DefaultListCellRenderer());
	}

	/**
	 * Converts an action list to an array. Any of the null "separators" or sub ActionLists are ommited
	 * from the array.
	 * @param lst
	 * @return
	 */
	protected Action[] toArray(final ActionList lst) {
		final List acts = new ArrayList();
		for (final Iterator it = lst.iterator(); it.hasNext();) {
			final Object v = it.next();
			if ((v != null) && (v instanceof Action)) {
				acts.add(v);
			}
		}

		return (Action[]) acts.toArray(new Action[acts.size()]);
	}

	@Override
	public void setEnabled(final boolean en) {
		super.setEnabled(en);
		if (en) {
			final boolean permission = this.checkEnabledPermission();
			if (permission) {
				this.enableButtonsPanelComponents(en);
			} else {
				this.enableButtonsPanelComponents(false);
			}
		} else {
			this.enableButtonsPanelComponents(false);
		}
	}

	protected void enableButtonsPanelComponents(final boolean enabled) {
		if ((this.buttonsPanel != null) && (this.buttonsPanel.getComponentCount() > 0)) {
			for (int i = 0; i < this.buttonsPanel.getComponentCount(); i++) {
				final Component c = this.buttonsPanel.getComponent(i);
				if (c != null) {
					c.setEnabled(enabled);
				}
			}
		}
	}

	/**
	 *
	 */
	protected void configureButtonsPanelAndEditor() {
		this.actionList = new ActionList("editor-actions");
		this.actionList.add(CompoundUndoManager.UNDO);
		this.actionList.add(CompoundUndoManager.REDO);

		this.createParagraphCombo();
		this.buttonsPanel.add(this.paragraphCombo);

		this.createFontFamilyCombo();
		this.buttonsPanel.add(this.fontFamilyCombo);

		this.createFontSizeCombo();
		this.buttonsPanel.add(this.fontSizeCombo);

		Action act = new HTMLFontColorAction();
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		this.addToolBarSeparator(this.buttonsPanel);

		act = new HTMLInlineAction(HTMLInlineAction.BOLD);
		act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		act = new HTMLInlineAction(HTMLInlineAction.ITALIC);
		act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		act = new HTMLInlineAction(HTMLInlineAction.UNDERLINE);
		act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		this.addToolBarSeparator(this.buttonsPanel);

		// Buttons alignment
		List alst = HTMLEditorActionFactory.createAlignActionList();
		for (final Iterator it = alst.iterator(); it.hasNext();) {
			act = (Action) it.next();
			act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
			this.actionList.add(act);
			this.addToToolBar(this.buttonsPanel, act);
		}

		this.addToolBarSeparator(this.buttonsPanel);

		// Buttons list (ordered and unordered)
		alst = HTMLEditorActionFactory.createListElementActionList();
		for (final Iterator it = alst.iterator(); it.hasNext();) {
			act = (Action) it.next();
			act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
			this.actionList.add(act);
			this.addToToolBar(this.buttonsPanel, act);
		}

		this.addToolBarSeparator(this.buttonsPanel);

		act = new HTMLTableAction();
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		act = new HTMLImageAction();
		this.actionList.add(act);
		this.addToToolBar(this.buttonsPanel, act);

		this.createPopupMenu();

		this.changePanelButtons();

		final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.scroll);
		constraints.gridx = 2;
		constraints.gridy = constraints.gridy + 1;

		final GridBagConstraints buttonsPanelConstraints = new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0);
		if (this.labelPosition == SwingConstants.TOP) {
			buttonsPanelConstraints.gridy = 1;
		}
		if (HTMLShefDataField.toolBarFiller) {
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

	protected void addToolBarSeparator(final JPanel buttonsPanel) {
		buttonsPanel.add(new JSeparator(SwingConstants.VERTICAL));
	}

	protected void addToToolBar(final JPanel buttonsPanel, final Action act) {

		if (act instanceof HTMLTextEditAction) {
			((HTMLTextEditAction) act).addActionPerformedListener(this);
		}

		final AbstractButton button = HTMLEditorActionFactory.createButton(act);// ActionUIFactory.getInstance().createButton(act);
		this.configToolbarButton(button);
		buttonsPanel.add(button);

		if (act instanceof HTMLAlignAction) {
			this.alignButtonGroup.add(button);
		}
	}

	public static int defaultHTMLToolbarButtonHeight = 22;

	protected int toolbarButtonHeight = HTMLShefDataField.defaultHTMLToolbarButtonHeight;

	protected void configToolbarButton(final AbstractButton button) {
		button.setText(null);
		button.setMnemonic(0);
		button.setMaximumSize(new Dimension(this.toolbarButtonHeight, this.toolbarButtonHeight));
		button.setMinimumSize(new Dimension(this.toolbarButtonHeight, this.toolbarButtonHeight));
		button.setPreferredSize(new Dimension(this.toolbarButtonHeight, this.toolbarButtonHeight));
		final Action a = button.getAction();
		if (a != null) {
			button.setToolTipText(a.getValue(Action.NAME).toString());
		}
	}

	protected void changePanelButtons() {
		if ((this.buttonsPanel != null) && (this.buttonsPanel.getComponentCount() > 0)) {
			for (int i = 0; i < this.buttonsPanel.getComponentCount(); i++) {
				final Component c = this.buttonsPanel.getComponent(i);
				if (c instanceof AbstractButton) {
					this.changeButton((AbstractButton) c, this.borderbuttons, this.opaquebuttons,
							this.listenerHighlightButtons);
				}
			}
		}
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
				if (!v.equals(HTMLShefDataField.HTML_BASE)) {
					this.editor.read(sr, ((JTextComponent) this.dataField).getDocument(), 0);
				}
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					HTMLShefDataField.logger.error(null, e);
				} else {
					HTMLShefDataField.logger.trace(null, e);
				}
			} finally {
				this.enableInnerListener(true);
				try {
					sr.close();
				} catch (final Exception e) {
					HTMLShefDataField.logger.trace(null, e);
				}
			}
		}
	}

	@Override
	public void deleteData() {
		this.setValue(HTMLShefDataField.HTML_BASE);
		try {
			this.enableInnerListener(false);
			((JTextComponent) this.dataField).getDocument()
			.remove(0, ((JTextComponent) this.dataField).getDocument().getLength());
		} catch (final Exception e) {
			HTMLShefDataField.logger.error(null, e);
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
			String res = s.toString();
			res = res.replaceAll("<html>\r", "<html>");
			res = res.replaceAll("</head>\r", "</head>");
			res = res.replaceAll("<body>\r", "<body>");
			res = res.replaceAll("</body>\r", "</body>");
			res = res.replaceAll("</html>\r", "</html>");
			res = res.replaceAll("\t", HTMLShefDataField.TAB);
			res = res.replaceAll("\r", "<BR>");

			// Now multiple value if plaintextcolumn is not null
			if (this.plainTextColumn != null) {
				final FormatText vm = new FormatText(this.plainTextColumn, (String) this.attribute,
						doc.getText(0, doc.getLength()), res);
				return vm;
			}
			return res;
		} catch (final Exception e) {
			HTMLShefDataField.logger.error(null, e);
			return null;
		} finally {
			try {
				if (sw != null) {
					sw.close();
				}
			} catch (final Exception e) {
				HTMLShefDataField.logger.trace(null, e);
			}
		}
	}

	@Override
	protected void createDataField() {
		this.dataField = new JEditorPane() {

			@Override
			protected void processKeyEvent(final KeyEvent e) {
				if ((e.getKeyCode() == KeyEvent.VK_F3) && (e.getID() == KeyEvent.KEY_RELEASED)) {
					if (HTMLShefDataField.this.dQuery == null) {
						HTMLShefDataField.this.dQuery = new FindDialog(HTMLShefDataField.this.parentFrame,
								(JTextComponent) HTMLShefDataField.this.dataField);
						HTMLShefDataField.this.dQuery.setResourceBundle(HTMLShefDataField.this.resources);
						HTMLShefDataField.this.dQuery.setComponentLocale(HTMLShefDataField.this.locale);
						HTMLShefDataField.this.dQuery
						.show(((JTextComponent) HTMLShefDataField.this.dataField).getCaretPosition());
					} else {
						if (HTMLShefDataField.this.dQuery != null) {
							HTMLShefDataField.this.dQuery
							.find(((JTextComponent) HTMLShefDataField.this.dataField).getCaretPosition());
						}
					}
					e.consume();
					return;
				} else {
					super.processKeyEvent(e);
				}
			}
		};

		final JEditorPane ed = (JEditorPane) this.dataField;
		this.editor = new OHTMLEditorKit();
		ed.setEditorKitForContentType("text/html", this.editor);
		ed.setContentType("text/html");

		this.initializeHandlers();

		this.insertHTML("<p></p>", 0);

		ed.addCaretListener(this.caretHandler);
		ed.addFocusListener(this.focusHandler);

		final HTMLDocument document = (HTMLDocument) ed.getDocument();
		final CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
		document.addUndoableEditListener(cuh);
		document.addDocumentListener(this.textChangedHandler);
	}

	protected void insertHTML(final String html, final int location) {
		try {
			final HTMLEditorKit kit = (HTMLEditorKit) ((JEditorPane) this.dataField).getEditorKit();
			final Document doc = ((JEditorPane) this.dataField).getDocument();
			final StringReader reader = new StringReader(HTMLUtils.jEditorPaneizeHTML(html));
			kit.read(reader, doc, location);
		} catch (final Exception ex) {
			HTMLShefDataField.logger.error(null, ex);
		}
	}

	protected void initializeHandlers() {
		this.caretHandler = new CaretHandler();
		this.focusHandler = new FocusHandler();
		this.textChangedHandler = new TextChangedHandler();
		this.paragraphComboHandler = new ParagraphComboHandler();
		this.fontChangeHandler = new FontChangeHandler();
		this.fontSizeChangeHandler = new FontSizeChangeHandler();
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
			HTMLShefDataField.logger.error(null, e);
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
			HTMLShefDataField.logger.trace(null, ex);
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

	protected class TextChangedHandler implements DocumentListener {

		@Override
		public void insertUpdate(final DocumentEvent e) {
			this.textChanged();
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			this.textChanged();
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			this.textChanged();
		}

		protected void textChanged() {
			HTMLShefDataField.this.isWysTextChanged = true;
		}

	}

	protected class ParagraphComboHandler implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == HTMLShefDataField.this.paragraphCombo) {
				final Action a = (Action) HTMLShefDataField.this.paragraphCombo.getSelectedItem();
				a.actionPerformed(e);
			}
		}

	}

	protected class ParagraphComboRenderer extends DefaultListCellRenderer {

		protected static final long serialVersionUID = 1L;

		@Override
		public Component getListCellRendererComponent(final JList list, Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			if (value instanceof Action) {
				String text = (String) ((Action) value).getValue("ID");
				if (text != null) {
					text = ApplicationManager.getTranslation(text, HTMLShefDataField.this.resources);
					value = text;
				} else {
					value = ((Action) value).getValue(Action.NAME);
				}
			}

			return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}

	}

	protected class FontChangeHandler implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == HTMLShefDataField.this.fontFamilyCombo) {
				final HTMLDocument document = (HTMLDocument) ((JTextComponent) HTMLShefDataField.this.dataField)
						.getDocument();
				CompoundUndoManager.beginCompoundEdit(document);

				if (HTMLShefDataField.this.fontFamilyCombo.getSelectedIndex() != 0) {
					HTMLUtils.setFontFamily((JEditorPane) HTMLShefDataField.this.dataField,
							HTMLShefDataField.this.fontFamilyCombo.getSelectedItem().toString());
				} else {
					HTMLUtils.setFontFamily((JEditorPane) HTMLShefDataField.this.dataField, null);
				}
				CompoundUndoManager.endCompoundEdit(document);
			}
		}

		public void itemStateChanged(final ItemEvent e) {

		}

	}

	protected class FontSizeChangeHandler implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() == HTMLShefDataField.this.fontSizeCombo) {

				final HTMLDocument document = (HTMLDocument) ((JTextComponent) HTMLShefDataField.this.dataField)
						.getDocument();
				CompoundUndoManager.beginCompoundEdit(document);

				final Integer iFontSize = (Integer) HTMLShefDataField.this.fontSizeCombo.getSelectedItem();
				final JTextComponent tc = (JTextComponent) HTMLShefDataField.this.dataField;
				final int start = tc.getSelectionStart();
				int end = tc.getSelectionEnd();
				if (start == end) {
					end = start + 1;
				}

				HTMLShefDataField.this.setTextFontSize(start, end - start, iFontSize.intValue());

				CompoundUndoManager.endCompoundEdit(document);
			}
		}

		public void itemStateChanged(final ItemEvent e) {

		}

	}

	protected class FocusHandler implements FocusListener {

		@Override
		public void focusGained(final FocusEvent e) {
			if (e.getComponent() instanceof JEditorPane) {
				final JEditorPane ed = (JEditorPane) e.getComponent();
				CompoundUndoManager.updateUndo(ed.getDocument());

				HTMLShefDataField.this.updateState();
			}
		}

		@Override
		public void focusLost(final FocusEvent e) {

		}

	}

	protected class CaretHandler implements CaretListener {

		/*
		 * (non-Javadoc)
		 *
		 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent )
		 */
		@Override
		public void caretUpdate(final CaretEvent e) {
			HTMLShefDataField.this.updateState(e);
		}

	}

	protected void updateState() {
		this.updateState(null);
	}

	protected void updateState(final CaretEvent e) {
		// Updating font family combo...
		this.fontFamilyCombo.removeActionListener(this.fontChangeHandler);
		final String fontName = HTMLUtils.getFontFamily((JEditorPane) this.dataField);
		if (fontName == null) {
			this.fontFamilyCombo.setSelectedIndex(0);
		} else {
			this.fontFamilyCombo.setSelectedItem(fontName);
		}
		this.fontFamilyCombo.addActionListener(this.fontChangeHandler);

		// Updating font size combo...
		this.fontSizeCombo.removeActionListener(this.fontSizeChangeHandler);

		if (e != null) {
			final Font fontTextSelected = this.getSelectedTextFont(e.getMark(), e.getDot());
			if (e.getMark() == e.getDot()) {
				if (fontTextSelected.getSize() != this.lastSelectedFontSize) {
					this.fontSizeCombo.setSelectedItem(new Integer(fontTextSelected.getSize()));
					this.lastSelectedFontSize = fontTextSelected.getSize();
				}
			}
		}
		this.fontSizeCombo.addActionListener(this.fontSizeChangeHandler);

		this.actionList.putContextValueForAll(HTMLTextEditAction.EDITOR, this.dataField);
		this.actionList.updateEnabledForAll();
	}

	protected Font getSelectedTextFont(final int offset, final int length) {
		final StyledDocument doc = (StyledDocument) ((JTextComponent) this.dataField).getDocument();
		return doc.getFont(doc.getCharacterElement(offset).getAttributes());
	}

}
