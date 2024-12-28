package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.FindDialog;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.TipScroll;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.actions.RedoAction;
import com.ontimize.gui.actions.UndoAction;
import com.ontimize.gui.field.document.LimitedTextDocument;
import com.ontimize.gui.field.document.TextDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.gui.LongString;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.multilanguage.FormMultilanguageTable;

/**
 * This class implements a memo data field. This field could be used like a text box in a
 * description for a user or a product.
 * <p>
 *
 * @author Imatia Innovation
 */
public class MemoDataField extends DataField implements OpenDialog, Freeable, InteractionManagerModeListener {

	private static final Logger logger = LoggerFactory.getLogger(MemoDataField.class);

	/**
	 * The parameter name for trimming trailing white spaces in field. By default, false.
	 *
	 * @since 5.3.15
	 */
	public static final String TRIM = "trim";

	/**
	 * The memo data field rows. By default, 2.
	 */
	protected int rows = 2;

	/**
	 * The condition about the SQL type: False -> VARCHAR ; True -> LONGVARCHAR. By default, false.
	 */
	protected boolean sqlTypeText = false;

	/**
	 * A reference for a search menu. By default, null.
	 */
	protected JMenuItem searchMenu = null;

	/**
	 * A reference for a all selection menu.
	 */
	protected JMenuItem selectAllMenu = null;

	/**
	 * The key for search.
	 */
	public static String queryKey = "memodatafield.search_text";

	/**
	 * The key for select all.
	 */
	public static String selectAllKey = "memodatafield.select_all";

	/**
	 * The reference for parent frame. By default, null.
	 */
	protected Frame parentFrame = null;

	/**
	 * A reference for a find dialog. By default, null.
	 * <p>
	 *
	 * @see FindDialog
	 */
	protected FindDialog dQuery = null;

	/**
	 * A reference for a scroll tip. By default, null.
	 * <p>
	 *
	 * @see TipScroll
	 */
	protected TipScroll scrollTip = null;

	/**
	 * The condition of scroll tip activation. By default, true.
	 */
	protected boolean activationScrollTip = true;

	/**
	 * A reference for a scroll. By default, null.
	 */
	protected JScrollPane scroll = null;

	/**
	 * The key for "OF" text.
	 */
	public static final String DE = "OF";

	/**
	 * The key for a white space.
	 */
	public static final String WHITE_SPACE = " ";

	/**
	 * The name of the parameter that indicates whether multi-language mode is activated for the
	 * selected field
	 */
	private static final String MULTILANGUAGE_STR = "multilanguage";

	/**
	 * The reference to translate the "OF" text. By default, "OF".
	 */
	protected String translateDE = "of";

	/**
	 * The maximum text length. By default, -1.
	 */
	protected int maximumTextLength = -1;

	/**
	 * The reference for upper case condition. By default, false.
	 */
	protected boolean uppercase = false;

	/**
	 * The reference to vertical expansion. By default, false.
	 */
	protected boolean expandVertical = false;

	/**
	 * Condition about whether field value must contain leading and trailing whitespace omitted or not.
	 *
	 * @since 5.2068EN
	 */
	// 5.3.15
	protected boolean trim = true;

	protected UndoManager undoManager = new UndoManager();

	public static boolean tabTransferFocus = true;

	/**
	 * This class implements a inner listener for document.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class InnerDocumentListener implements DocumentListener {

		/**
		 * The inner listener activation condition. By default, true.
		 */
		protected boolean innerListenerEnabled = true;

		/**
		 * Enables the inner listener.
		 * <p>
		 * @param act the condition to activation
		 */
		public void setInnerListenerEnabled(final boolean act) {
			this.innerListenerEnabled = act;
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				MemoDataField.this.fireValueChanged(MemoDataField.this.getValue(), MemoDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				MemoDataField.this.fireValueChanged(MemoDataField.this.getValue(), MemoDataField.this.valueSave,
						ValueEvent.USER_CHANGE);
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

	}

	/**
	 * The reference to inner listener. By default, null.
	 */
	protected InnerDocumentListener innerListener = null;

	/**
	 * The button that shows the translation table
	 */
	protected JButton multilanguageButton = null;

	/**
	 * Indicates whether the field is configured as multi-language or not
	 */
	protected boolean multilanguage = false;

	/**
	 * Variable where the local reference is stored.
	 */
	protected transient EntityReferenceLocator locator = null;

	/**
	 * The class constructor. Calls to super constructor, initializes parameters and installs listeners.
	 * <p>
	 * @param params the component parameters
	 */
	public MemoDataField(final Map params) {
		super();

		this.createDataField();
		// this.dataField.setOpaque(true);
		this.init(params);

		if (this.multilanguage) {
			this.createMultilanguageButton();
		}

		if (this.dataField instanceof JTextArea) {
			if (this.maximumTextLength > 0) {
				((JTextArea) this.dataField).setDocument(new LimitedTextDocument(this.maximumTextLength));
			} else if (this.uppercase) {
				((JTextArea) this.dataField).setDocument(new TextDocument(this.uppercase));
			}
			((JTextArea) this.dataField).setRows(this.rows);
			((JTextArea) this.dataField).setColumns(this.fieldSize);
			((JTextArea) this.dataField).setLineWrap(true);
			((JTextArea) this.dataField).setWrapStyleWord(true);

		}
		this.installInnerListener();
		this.installScrollListener();
		// DEBUG
		// this.setOpaque(true);
		// this.setBackground(Color.red);
		this.registerUndoableListener();
		this.registerUndoRedoActions();
	}

	protected void registerUndoableListener() {
		((JTextComponent) this.getDataField()).getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(final UndoableEditEvent e) {
				MemoDataField.this.undoManager.addEdit(e.getEdit());
			}
		});
	}

	protected void registerUndoRedoActions() {
		final InputMap inMap = this.getDataField().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap actMap = this.getDataField().getActionMap();
		final KeyStroke ksUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, true);
		inMap.put(ksUndo, "Undo");
		actMap.put("Undo", new UndoAction(this.undoManager));

		final KeyStroke ksRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK, true);
		inMap.put(ksRedo, "Redo");
		actMap.put("Redo", new RedoAction(this.undoManager));
	}

	@Override
	public String getText() {
		return ((JTextComponent) this.dataField).getText();
	}

	/**
	 * Creates the field and overrides the {@link #processKeyEvent(KeyEvent)} method for managing focus.
	 */
	protected void createDataField() {
		this.dataField = new JTextArea() {

			// Overwrite this method to change the focus
			@Override
			protected void processKeyEvent(final KeyEvent e) {
				if (e.getKeyCode() != KeyEvent.VK_TAB) {
					if ((e.getKeyCode() == KeyEvent.VK_F3) && (e.getID() == KeyEvent.KEY_RELEASED)) {
						if (MemoDataField.this.dQuery == null) {
							MemoDataField.this.dQuery = new FindDialog(MemoDataField.this.parentFrame,
									(JTextArea) MemoDataField.this.dataField);
							MemoDataField.this.dQuery.setResourceBundle(MemoDataField.this.resources);
							MemoDataField.this.dQuery.setComponentLocale(MemoDataField.this.locale);
							MemoDataField.this.dQuery
							.show(((JTextComponent) MemoDataField.this.dataField).getCaretPosition());
						} else {
							if (MemoDataField.this.dQuery != null) {
								MemoDataField.this.dQuery
								.find(((JTextComponent) MemoDataField.this.dataField).getCaretPosition());
							}
						}
						e.consume();
						return;
					}
					super.processKeyEvent(e);
				} else if (e.getID() == KeyEvent.KEY_PRESSED) {
					// Is VK_TAB
					if (MemoDataField.tabTransferFocus) {
						if (e.isShiftDown()) {
							this.transferFocusBackward();
						} else {
							this.transferFocus();
						}
					} else {
						// Insert a TAB in the field
						super.processKeyEvent(e);
					}
				}
			}

			@Override
			public void setText(final String t) {
				super.setText(t);
				this.setCaretPosition(0);
			}
		};
	}

	/**
	 * Installs the scroll listener.
	 */
	protected void installScrollListener() {
		if (this.scroll != null) {
			final JScrollBar scrollBarVertical = this.scroll.getVerticalScrollBar();
			scrollBarVertical.addMouseMotionListener(new MouseMotionAdapter() {

				@Override
				public void mouseDragged(final MouseEvent e) {
					if (MemoDataField.this.activationScrollTip) {
						if (scrollBarVertical.getVisibleAmount() < scrollBarVertical.getMaximum()) {
							// Show the window
							final Point p = MemoDataField.this.scroll.getViewport().getViewPosition();
							final int topRow = (p.y / MemoDataField.this.dataField
									.getFontMetrics(MemoDataField.this.dataField.getFont())
									.getHeight()) + 1;
							MemoDataField.this.hideScrollTip();
							MemoDataField.this.showScrollTip(scrollBarVertical, 0, 0, topRow);
						}
					}
				}
			});
			scrollBarVertical.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseReleased(final MouseEvent e) {
					MemoDataField.this.hideScrollTip();
				}
			});
		}
	}

	/**
	 * Shows the scroll tip.
	 * <p>
	 * @param scrollBar the reference to scroll bar.
	 * @param x the x position
	 * @param y the y position
	 * @param row the row number
	 */
	protected void showScrollTip(final JScrollBar scrollBar, final int x, final int y, final int row) {
		if (this.scrollTip == null) {
			final Window w = SwingUtilities.getWindowAncestor(this);
			this.scrollTip = new TipScroll(w);
		}
		final StringBuilder StringBuilder = new StringBuilder(MemoDataField.WHITE_SPACE);
		StringBuilder.append(row);
		StringBuilder.append(MemoDataField.WHITE_SPACE);

		StringBuilder.append(this.translateDE);
		StringBuilder.append(MemoDataField.WHITE_SPACE);
		StringBuilder.append(this.getRowNumber());
		StringBuilder.append(MemoDataField.WHITE_SPACE);
		this.scrollTip.show(scrollBar, x, y, StringBuilder.toString());
	}

	/**
	 * Gets the row number.
	 * <p>
	 * @return the row number
	 */
	protected int getRowNumber() {
		return this.dataField.getHeight() / this.dataField.getFontMetrics(this.dataField.getFont()).getHeight();
	}

	/**
	 * Hides the scroll tip.
	 */
	protected void hideScrollTip() {
		if (this.scrollTip != null) {
			this.scrollTip.setVisible(false);
		}
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param params the Map with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sqltexttype</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>no -> Varchar ; yes -> LongVarchar.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>uppercase</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>The uppercase condition.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>rows</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The number of rows.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>maxlength</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The maxlength for component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expand</td>
	 *        <td>yes/no</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The vertical expansion.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>multilanguage</td>
	 *        <td><i>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether the field should display the multi-language configuration button
	 *        when this option is enabled in the form. (Only allowed when the parameter 'multilanguage'
	 *        in the {@link Form} is set to true)</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map params) {
		final Object border = params.get(DataField.BORDER);
		params.remove(DataField.BORDER);
		super.init(params);

		final Object sqltexttype = params.get("sqltexttype");
		if (sqltexttype != null) {
			if (sqltexttype.equals("yes")) {
				this.sqlTypeText = true;
			} else {
				this.sqlTypeText = false;
			}
		}

		final Object uppercase = params.get("uppercase");
		if (uppercase != null) {
			if (uppercase.equals("yes")) {
				this.uppercase = true;
			} else {
				this.uppercase = false;
			}
		}
		final Object rows = params.get("rows");
		if (rows != null) {
			try {
				this.rows = Integer.parseInt(rows.toString());
			} catch (final Exception e) {
				MemoDataField.logger.error(this.getClass().toString() + " Error in parameter 'rows': " + e.getMessage(),
						e);
			}
		}

		final Object maxlength = params.get("maxlength");
		if (maxlength != null) {
			try {
				this.maximumTextLength = Integer.parseInt(maxlength.toString());
			} catch (final Exception e) {
				MemoDataField.logger
				.error(this.getClass().toString() + "Error in parameter 'maxlength': " + e.getMessage(), e);
			}
		}

		final Object expand = params.get("expand");
		if (expand != null) {
			if (expand.equals("yes")) {
				this.expandVertical = true;
			} else {
				this.expandVertical = false;
			}
		}
		this.remove(this.dataField);

		this.scroll = new JScrollPane(this.dataField);
		this.scroll.setOpaque(false);

		if (border != null) {
			if (border.equals(DataField.NONE)) {
				this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));
			} else if (border.equals(DataField.RAISED)) {
				this.scroll.setBorder(new EtchedBorder(EtchedBorder.RAISED));
			} else if (border.equals(DataField.LOWERED)) {
				this.scroll.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			} else {
				try {
					final Color c = ColorConstants.colorNameToColor(border.toString());
					this.scroll.setBorder(new LineBorder(c));
				} catch (final Exception e) {
					MemoDataField.logger.trace(null, e);
					this.scroll.setBorder(ParseUtils.getBorder((String) border, this.scroll.getBorder()));
				}
			}
		}

		double verticalCoeficient = 0.0;
		if (this.expandVertical) {
			verticalCoeficient = 0.01;
		}
		int fill = this.redimensJTextField;
		if (this.expandVertical) {
			switch (this.redimensJTextField) {
			case GridBagConstraints.HORIZONTAL:
				fill = GridBagConstraints.BOTH;
				break;
			case GridBagConstraints.NONE:
				fill = GridBagConstraints.VERTICAL;
				break;
			}
		}

		if (this.labelPosition == SwingConstants.TOP) {
			this.add(this.scroll,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 2, 1, 1, this.weightDataFieldH,
							verticalCoeficient, GridBagConstraints.EAST, fill,
							new Insets(DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL),
							0, 0));
		} else {
			this.add(this.scroll,
					new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, this.weightDataFieldH,
							verticalCoeficient, GridBagConstraints.EAST, fill,
							new Insets(DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL,
									DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL),
							0, 0));
		}

		if (params.containsKey("opaque") && !ApplicationManager.parseStringValue(params.get("opaque").toString())) {
			DataField.changeOpacity(this, false);
		}
		this.trim = ParseUtils.getBoolean((String) params.get(MemoDataField.TRIM), true);

		this.multilanguage = ParseUtils.getBoolean((String) params.get(MemoDataField.MULTILANGUAGE_STR), false);
		if (this.multilanguage) {
			try {
				this.locator = ApplicationManager.getApplication().getReferenceLocator();
			} catch (final Exception e) {
				MemoDataField.logger.error("Error when obtaining the reference locator", e);
			}
		}
	}

	@Override
	public int getSQLDataType() {
		if (!this.sqlTypeText) {
			return Types.VARCHAR;
		}
		return Types.LONGVARCHAR;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		if (this.sqlTypeText) {
			return new LongString(((JTextComponent) this.dataField).getText());
		} else {
			return ((JTextComponent) this.dataField).getText();
		}
	}

	@Override
	public boolean isEmpty() {
		if (((JTextComponent) this.dataField).getText().length() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public void setValue(final Object value) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.enableInnerListener(false);
		try {
			final Object oPreviousValue = this.getValue();
			if (value != null) {
				if (this.trim) {
					((JTextComponent) this.dataField).setText(value.toString().trim());
				} else {
					((JTextComponent) this.dataField).setText(value.toString());
				}
				((JTextComponent) this.dataField).getDocument()
				.putProperty(DefaultEditorKit.EndOfLineStringProperty, "\n");
				this.valueSave = this.getValue();
				this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			} else {
				this.deleteData();
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				MemoDataField.logger.error(null, e);
			}
		} finally {
			this.enableInnerListener(true);
		}
	}

	@Override
	public void deleteData() {
		try {
			this.enableInnerListener(false);
			final Object oPreviousValue = this.getValue();
			((JTextComponent) this.dataField).setText("");
			this.valueSave = this.getValue();
			this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				MemoDataField.logger.error(null, e);
			}
		} finally {
			this.enableInnerListener(true);
		}
	}

	/**
	 * Adds a inner listener for document.
	 */
	protected void installInnerListener() {
		final Document d = ((JTextComponent) this.dataField).getDocument();
		if (d != null) {
			if (this.innerListener == null) {
				this.innerListener = new InnerDocumentListener();
			}
			d.addDocumentListener(this.innerListener);
		}
	}

	/**
	 * This method allows to enable/disable the ValueEvent events notifier. So, inner events will be not
	 * triggered when inner listener is disabled. It is advisable disabling the listener only when
	 * content is inserted by program.
	 * <p>
	 * @param enable the condition to enable/disable the inner listener.
	 */
	protected void enableInnerListener(final boolean enable) {
		if (this.innerListener != null) {
			this.innerListener.setInnerListenerEnabled(enable);
		}
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		if (res != null) {
			if (this.dQuery != null) {
				this.dQuery.setResourceBundle(res);
			}
			if (this.searchMenu != null) {
				try {
					this.searchMenu.setText(this.resources.getString(MemoDataField.queryKey));
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						MemoDataField.logger.debug(e.getMessage(), e);
					}
				}
			}

			try {
				this.translateDE = this.resources.getString(MemoDataField.DE);
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					MemoDataField.logger.debug(e.getMessage(), e);
				}
			}

			if (this.selectAllMenu != null) {
				this.selectAllMenu.setText(ApplicationManager.getTranslation(MemoDataField.selectAllKey, res));
			}

		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		super.setComponentLocale(l);
		if (this.dQuery != null) {
			this.dQuery.setComponentLocale(l);
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		if (this.dQuery != null) {
			v.addAll(this.dQuery.getTextsToTranslate());
		}
		v.add(MemoDataField.DE);
		v.add(MemoDataField.queryKey);
		return v;
	}

	@Override
	protected void createPopupMenu() {
		super.createPopupMenu();
		this.searchMenu = new JMenuItem(MemoDataField.queryKey);
		try {
			if (this.resources != null) {
				this.searchMenu.setText(this.resources.getString(MemoDataField.queryKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				MemoDataField.logger.debug(e.getMessage(), e);
			}
		}
		this.popupMenu.addSeparator();
		this.popupMenu.add(this.searchMenu);
		this.searchMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (MemoDataField.this.dQuery == null) {
					MemoDataField.this.dQuery = new FindDialog(MemoDataField.this.parentFrame,
							(JTextComponent) MemoDataField.this.dataField);
					MemoDataField.this.dQuery.setResourceBundle(MemoDataField.this.resources);
					MemoDataField.this.dQuery.setComponentLocale(MemoDataField.this.locale);
				}
				MemoDataField.this.dQuery.show(((JTextComponent) MemoDataField.this.dataField).getCaretPosition());
			}
		});

		this.popupMenu.addSeparator();
		this.selectAllMenu = new JMenuItem(MemoDataField.selectAllKey);
		this.popupMenu.add(this.selectAllMenu);
		this.selectAllMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (MemoDataField.this.dataField instanceof JTextComponent) {
					final JTextComponent textComponent = (JTextComponent) MemoDataField.this.dataField;
					textComponent.requestFocus();
					textComponent.selectAll();
				}
			}
		});

	}

	@Override
	protected void showPopupMenu(final Component source, final int x, final int y) {
		super.showPopupMenu(source, x, y);
		if (this.searchMenu != null) {
			this.searchMenu.setVisible(this.isEnabled());
		}
	}

	/**
	 * Sets the max length of field.
	 * <p>
	 * @param l the length
	 */
	public void setMaxLength(final int l) {
		final Document doc = ((JTextField) this.dataField).getDocument();
		if (doc instanceof LimitedTextDocument) {
			((LimitedTextDocument) doc).setMaxLength(l);
		} else {
			final String text = this.getText();
			((JTextComponent) this.dataField).setDocument(new LimitedTextDocument(l));
			((JTextComponent) this.dataField).setText(text);
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		final Object c = super.getConstraints(parentLayout);
		if (this.expandVertical && (c instanceof GridBagConstraints)) {
			((GridBagConstraints) c).weighty = 0.01;
			final int fill = ((GridBagConstraints) c).fill;
			switch (fill) {
			case GridBagConstraints.HORIZONTAL:
				((GridBagConstraints) c).fill = GridBagConstraints.BOTH;
				break;
			case GridBagConstraints.NONE:
				((GridBagConstraints) c).fill = GridBagConstraints.VERTICAL;
				break;
			}
		}
		return c;
	}

	@Override
	public void free() {
		super.free();
		if (this.scrollTip != null) {
			this.scrollTip.dispose();
			this.scrollTip = null;
		}
	}

	@Override
	protected void updateBackgroundColor() {
		if (this.requiredBorder != null) {
			if (this.noRequiredBorder == null) {
				this.noRequiredBorder = this.scroll.getBorder();
			}
			if (!this.enabled) {
				this.dataField.setForeground(this.fontColor);
				this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
			} else {
				this.scroll
				.setBorder(this.required ? BorderManager.getBorder(this.requiredBorder) : this.noRequiredBorder);
				this.dataField.setBackground(this.backgroundColor);
				this.dataField.setForeground(this.fontColor);
			}
		} else {
			super.updateBackgroundColor();
		}
	}

	/**
	 * Creates the button to display the multi-language translation button
	 */
	protected void createMultilanguageButton() {
		this.multilanguageButton = new FieldButton();
		this.multilanguageButton.setMargin(new Insets(0, 0, 0, 0));
		this.multilanguageButton.setIcon(ImageManager.getIcon(ImageManager.BUNDLE));
		this.multilanguageButton.setToolTipText(ApplicationManager.getTranslation("textdatafield.multilanguage",
				ApplicationManager.getApplicationBundle()));
		this.multilanguageButton.setFocusable(false);

		this.multilanguageButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				MemoDataField.this.createAndShowDialogTranslation();
				MemoDataField.this.getParentForm().refreshCurrentDataRecord();

			}
		});

		if (this.labelPosition != SwingConstants.TOP) {
			super.add(this.multilanguageButton, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0,
					GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		} else {
			super.add(this.multilanguageButton, new GridBagConstraints(2, 2, 1, 1, 0, 0,
					GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}

	}

	/**
	 * Creates and displays the translation dialog that is activated by clicking on the corresponding
	 * button
	 */
	protected void createAndShowDialogTranslation() {

		final Map formKeys = new Hashtable();
		for (final Object o : this.getParentForm().getKeys()) {
			formKeys.put(o, this.getParentForm().getDataFieldValue((String) o));
		}

		final Dialog owner = new FormMultilanguageTable(SwingUtilities.getWindowAncestor(this.getMultilanguageButton()), true,
				this.locator, this.getParentForm().getEntityName(),
				(String) this.getAttribute(), formKeys, true);
		if (((FormMultilanguageTable) owner).isCreated()) {
			ApplicationManager.center(owner);
			owner.setVisible(true);
		} else {
			owner.dispose();
		}
	}

	/**
	 * Checks if the field is multi-language or not
	 * @return <code>true</code> if the field is multi-language, <code>false</code> otherwise.
	 */
	public boolean isMultilanguage() {
		return this.multilanguage;
	}

	/**
	 * Sets if the field is multi-language or not
	 * @param multilanguage <code>true</code> to set the field as multi-language, <code>false</code>
	 *        otherwise
	 */
	public void setMultilanguage(final boolean multilanguage) {
		this.multilanguage = multilanguage;
	}

	/**
	 * Returns the button showing the multi-language translation dialog
	 * @return The multi-language button
	 */
	public JButton getMultilanguageButton() {
		return this.multilanguageButton;
	}

	/**
	 * Sets the button that displays the multi-language translation dialog
	 * @param multilanguageButton the multi-language button
	 */
	public void setMultilanguageButton(final JButton multilanguageButton) {
		this.multilanguageButton = multilanguageButton;
	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (this.getMultilanguageButton() != null) {
			if (e.getInteractionManagerMode() == InteractionManager.UPDATE) {
				this.getMultilanguageButton().setVisible(true);
				this.getMultilanguageButton().setEnabled(true);
			} else {
				this.getMultilanguageButton().setVisible(false);
				this.getMultilanguageButton().setEnabled(false);
			}
		}

	}

}
