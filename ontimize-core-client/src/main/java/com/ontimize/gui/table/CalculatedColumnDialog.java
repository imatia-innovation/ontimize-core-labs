package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.actions.RedoAction;
import com.ontimize.gui.actions.UndoAction;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.CollapsiblePanel;
import com.ontimize.gui.container.Column;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.container.MattedDeployableBorder;
import com.ontimize.gui.container.Row;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.HtmlHelpField;
import com.ontimize.gui.field.Label;
import com.ontimize.gui.field.SelectionListDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table.SelectableRenderCell;
import com.ontimize.util.JEPUtils;
import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;
import com.ontimize.util.swing.text.ComponentTextPane;
import com.ontimize.xml.DefaultXMLParametersManager;

public class CalculatedColumnDialog extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(CalculatedColumnDialog.class);

	public static String HTML_HELP_TEXT = "com/ontimize/gui/i18n/calculatedcols_help.html";// "calculatedcols.htmlhelptemplate";//

	private static final String CALCULATED_COLUMNS_TIP = "calculatedcols.calculatedcolumnstip";

	protected static final String AVAILABLE_COLUMMNS_TIP = "calculatedcols.availablecolummnstip";

	protected static final String RENDER_ROW_LIST_TIP = "calculatedcols.renderrowlisttip";

	protected static final String RENDER_TITLE = "calculatedcols.rendererlist";

	public static final String CalculatedColsDialogTitle = "calculatedcols.CalculatedColsDialogTitle";

	public static String labelsBgImage = ImageManager.TABLE_HEADER_33;

	public static Border listBorder = BorderFactory.createLineBorder(new Color(188, 188, 188), 3);

	protected Table table = null;

	protected Column calculatedColumnsPanel;

	protected Button createNewCalculatedColumnButton;

	protected Button deleteCalculatedColumnButton;

	protected JList calculatedColumnList;

	protected Column expressionEditorPanel;

	protected Button addButton;

	protected Button substractButton;

	protected Button multiplyButton;

	protected Button divideButton;

	protected Button openParenthesisButton;

	protected Button closeParenthesisButton;

	protected Column availableColumnPanel;

	protected JList availableColumnsList;

	protected SelectionListDataField availableRenderList;

	protected ComponentTextPane expressionPane;

	protected Button acceptButton;

	protected Button cancelButton;

	protected List internationalizationComponentList = new ArrayList();

	protected List deletedExpressions;

	protected List newExpressions;

	protected boolean updateSelectedCalculatedColumnExpression = true;

	protected UndoManager undoManager = new UndoManager();

	protected HtmlHelpField htmlHelpField;

	/**
	 * List with all the components that must be enabled or disabled when a calculated columns is
	 * selected or not
	 */
	protected List componentsGroup = new ArrayList();

	public CalculatedColumnDialog(final Frame frame, final Table table) {
		super(frame, CalculatedColumnDialog.CalculatedColsDialogTitle, true);
		this.init(table);
	}

	public CalculatedColumnDialog(final Dialog d, final Table table) {
		super(d, CalculatedColumnDialog.CalculatedColsDialogTitle, true);
		this.init(table);
	}

	protected void enableGroup(final boolean enabled) {
		for (int i = 0; i < this.componentsGroup.size(); i++) {
			((Component) this.componentsGroup.get(i)).setEnabled(enabled);
		}
	}

	protected void init(final Table table) {
		this.table = table;

		this.expressionPane = this.createExpressionEditor();

		final Row middleContainer = this.createRow(true, null, 0, "5;5;5;5");

		this.calculatedColumnsPanel = this.createColumn(true, null, 150, true, "1;1;1;1", false, null);

		final Column borderColumn = this.createColumn(true, "calculatedcols.calculatedcols", 150, true, "1;1;1;1", false,
				CalculatedColumnDialog.listBorder);

		this.calculatedColumnList = this.createList("calulatedcols.calculatedcolslist",
				CalculatedColumnDialog.CALCULATED_COLUMNS_TIP);
		// this.configureCalculatedColsListRenderer(this.calculatedColumnList);

		this.updateCalculatedColsList();

		this.calculatedColumnList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				CalculatedColumnDialog.this.changeCalculatedColumnSelection();
			}
		});

		final JScrollPane calculatedScroll = new JScrollPane(this.calculatedColumnList);
		calculatedScroll.setBorder(BorderFactory.createEmptyBorder());

		borderColumn.add(calculatedScroll,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		this.calculatedColumnsPanel.add(borderColumn,
				borderColumn.getConstraints(this.calculatedColumnsPanel.getLayout()));

		this.createNewCalculatedColumnButton = this.createButton("calculatedcols.createkey", "calculatedcols.create",
				ImageManager.ADD, false, null);
		this.createNewCalculatedColumnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalculatedColumnDialog.this.createNewCalculatedColumn(null);
			}
		});

		this.deleteCalculatedColumnButton = this.createButton("calculatedcols.deletekey", "calculatedcols.delete",
				ImageManager.DELETE, true, null);
		this.deleteCalculatedColumnButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalculatedColumnDialog.this.deleteSeletedCalculatedColumn();
			}
		});

		final Row calculatedColumnsRow = this.createRow(false, null, 0, "1;1;1;1");
		calculatedColumnsRow.add(this.createNewCalculatedColumnButton,
				this.createNewCalculatedColumnButton.getConstraints(calculatedColumnsRow.getLayout()));
		calculatedColumnsRow.add(this.deleteCalculatedColumnButton,
				this.deleteCalculatedColumnButton.getConstraints(calculatedColumnsRow.getLayout()));

		this.calculatedColumnsPanel.add(calculatedColumnsRow,
				calculatedColumnsRow.getConstraints(this.calculatedColumnsPanel.getLayout()));

		middleContainer.add(this.calculatedColumnsPanel,
				this.calculatedColumnsPanel.getConstraints(middleContainer.getLayout()));

		// //////////////////////////////////////////////////////////
		// ////////// SECOND ////////////////////////////////////////
		// //////////////////////////////////////////////////////////

		this.expressionEditorPanel = this.createColumn(true, null, 0, false, "1;5;1;5", true, null);

		/* CREATE RENDER CELL LIST (TOP MIDDLE COLUMN) */

		final Column rendenderRow = this.createColumn(true, null, 0, true, "1;5;1;5", false, null);
		this.availableRenderList = this.createRendererList("calculatedcols.rendererlist",
				CalculatedColumnDialog.RENDER_ROW_LIST_TIP);
		((JList) this.availableRenderList.getDataField()).setModel(this.table.getCalculatedColumnsRender());

		this.availableRenderList.addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(final ValueEvent e) {
				if (e.getNewValue() != e.getOldValue()) {

					final List v = (List) CalculatedColumnDialog.this.availableRenderList.getValue();

					String renderKey = null;
					if (v != null) {
						renderKey = ((SelectableRenderCell) v.get(0)).getValue().toString();
					} else {
						renderKey = Table.DEFAULT_CELL_RENDERER;
					}

					CalculatedColumnDialog.this.updateCalculatedColumnRenderExpression(renderKey);
				}

			}
		});

		this.componentsGroup.add(this.availableRenderList);
		rendenderRow.add(this.availableRenderList);

		/* END */

		this.expressionEditorPanel.add(rendenderRow,
				rendenderRow.getConstraints(this.expressionEditorPanel.getLayout()));

		this.addButton = this.createButton("calculatedcols.addbutton", null, ImageManager.CALC_ADD, true, null);
		this.addButton.addActionListener(new InsertSymbolListener(" + ", this.expressionPane));

		this.substractButton = this.createButton("calculatedcols.substractbutton", null, ImageManager.CALC_SUBSTRACT,
				true, null);
		this.substractButton.addActionListener(new InsertSymbolListener(" - ", this.expressionPane));

		this.multiplyButton = this.createButton("calculatedcols.multiplybutton", null, ImageManager.CALC_MULTIPLY, true,
				null);
		this.multiplyButton.addActionListener(new InsertSymbolListener(" * ", this.expressionPane));

		this.divideButton = this.createButton("calculatedcols.dividebutton", null, ImageManager.CALC_DIVIDE, true,
				null);
		this.divideButton.addActionListener(new InsertSymbolListener(" / ", this.expressionPane));

		this.openParenthesisButton = this.createButton("calculatedcols.openparenthesisbutton", null,
				ImageManager.CALC_OPEN_PARENTHESIS, true, null);
		this.openParenthesisButton.addActionListener(new InsertSymbolListener(" ( ", this.expressionPane));

		this.closeParenthesisButton = this.createButton("calculatedcols.closeparenthesisbutton", null,
				ImageManager.CALC_CLOSE_PARENTHESIS, true, null);
		this.closeParenthesisButton.addActionListener(new InsertSymbolListener(" ) ", this.expressionPane));

		final Row operationButtonsRow = this.createRow(true, null, 0, "1;1;1;1");
		operationButtonsRow.add(this.addButton, this.addButton.getConstraints(operationButtonsRow.getLayout()));
		operationButtonsRow.add(this.substractButton,
				this.substractButton.getConstraints(operationButtonsRow.getLayout()));
		operationButtonsRow.add(this.multiplyButton,
				this.multiplyButton.getConstraints(operationButtonsRow.getLayout()));
		operationButtonsRow.add(this.divideButton, this.divideButton.getConstraints(operationButtonsRow.getLayout()));
		operationButtonsRow.add(this.openParenthesisButton,
				this.openParenthesisButton.getConstraints(operationButtonsRow.getLayout()));
		operationButtonsRow.add(this.closeParenthesisButton,
				this.closeParenthesisButton.getConstraints(operationButtonsRow.getLayout()));

		this.expressionEditorPanel.add(operationButtonsRow,
				operationButtonsRow.getConstraints(this.expressionEditorPanel.getLayout()));

		this.componentsGroup.add(this.expressionPane);

		final JScrollPane expressionScroll = new JScrollPane(this.expressionPane);
		expressionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.expressionEditorPanel.add(expressionScroll,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		middleContainer.add(this.expressionEditorPanel,
				this.calculatedColumnsPanel.getConstraints(middleContainer.getLayout()));

		// //////////////////////////////////////////////////////////
		// ////////// THIRD /////////////////////////////////////////
		// //////////////////////////////////////////////////////////

		this.availableColumnPanel = this.createColumn(true, "calculatedcols.availablecols", 150, true, "1;1;1;1", true,
				CalculatedColumnDialog.listBorder);
		this.availableColumnsList = this.createList("calculatedcols.availablecolslist",
				CalculatedColumnDialog.AVAILABLE_COLUMMNS_TIP);
		this.configureAvailableColsListRenderer(this.availableColumnsList);
		this.componentsGroup.add(this.availableColumnsList);
		this.updateAvailableColsList();

		this.availableColumnsList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (CalculatedColumnDialog.this.availableColumnsList.isEnabled()
						&& CalculatedColumnDialog.this.expressionPane.isEnabled() && (e.getClickCount() == 2)) {
					final int index = CalculatedColumnDialog.this.availableColumnsList.locationToIndex(e.getPoint());
					if (index >= 0) {
						CalculatedColumnDialog.this.addColumnToExpression(index);
					}
				}
			}
		});

		final JScrollPane availableColsScroll = new JScrollPane(this.availableColumnsList);
		availableColsScroll.setBorder(BorderFactory.createEmptyBorder());
		this.availableColumnPanel.add(availableColsScroll,
				new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		middleContainer.add(this.availableColumnPanel,
				this.availableColumnPanel.getConstraints(middleContainer.getLayout()));

		final Row bottomContainer = this.createRow(false, null, 0, "0;5;5;5");

		this.acceptButton = this.createButton("calculatedcols.accept", "application.accept", ImageManager.OK, false,
				"right");
		this.acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalculatedColumnDialog.this.acceptDialog();
			}
		});

		this.cancelButton = this.createButton("calculatedcols.cancel", "application.cancel", ImageManager.CANCEL, false,
				"left");
		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CalculatedColumnDialog.this.cancelDialog();
			}
		});

		bottomContainer.add(this.acceptButton, this.acceptButton.getConstraints(bottomContainer.getLayout()));
		bottomContainer.add(this.cancelButton, this.cancelButton.getConstraints(bottomContainer.getLayout()));

		final Column mainPanel = this.createColumn(true, null, 0, false, null, false, null);

		final FormComponent helpPanel = this.createHelpPanel();
		mainPanel.add((Component) helpPanel, helpPanel.getConstraints(mainPanel.getLayout()));
		mainPanel.add(middleContainer, middleContainer.getConstraints(mainPanel.getLayout()));
		mainPanel.add(bottomContainer, bottomContainer.getConstraints(mainPanel.getLayout()));

		this.add(mainPanel);

		if ((this.table != null) && (this.table.getResourceBundle() != null)) {
			this.setResourceBundle(table.getResourceBundle());
		}

		// To close the dialog in the top right close button must be exactly the
		// same as click in the cancel button
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				super.windowClosing(e);
				CalculatedColumnDialog.this.cancelDialog();
			}
		});

		this.enableGroup(false);
		this.pack();
	}

	protected void cancelDialog() {

		// Check changes and show message if there are changes without save
		final List modifiedElements = this.getModifiedElements(true, true);

		if (modifiedElements.size() > 0) {
			final StringBuilder sb = new StringBuilder(
					"<html> " + ApplicationManager.getTranslation("calculatedcols.columnsmodifiedwithoutsave"));
			sb.append(": " + modifiedElements.get(0).toString());
			for (int i = 1; i < modifiedElements.size(); i++) {
				sb.append(", " + modifiedElements.get(i));
			}

			sb.append("<br>");
			sb.append(ApplicationManager.getTranslation("calculatecols.continuewithoutsave"));
			sb.append("</html>");

			final int result = JOptionPane.showConfirmDialog(this, sb.toString(), "", JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) {
				return;
			}
		} else if ((this.deletedExpressions != null) && (this.deletedExpressions.size() > 0)) {
			final int result = JOptionPane.showConfirmDialog(this,
					ApplicationManager.getTranslation("calculatedcols.columnsdeletedwithoutsave"), "",
					JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.NO_OPTION) {
				return;
			}
		}
		this.setVisible(false);
	}

	/**
	 * Get a list with the modified columns
	 * @param onlyNames If this is then the list contains only the names (String) of the modified
	 *        columns. If it is false then the list contains the Expression object
	 * @param translate If this is true and onlyNames is true then the names are translated. If
	 * @return
	 */
	protected List getModifiedElements(final boolean onlyNames, final boolean translate) {
		final int size = this.calculatedColumnList.getModel().getSize();
		final List modifiedElements = new ArrayList();
		for (int i = 0; i < size; i++) {
			final Object elementAt = this.calculatedColumnList.getModel().getElementAt(i);
			if ((elementAt != null) && (elementAt instanceof Expression) && ((Expression) elementAt).isModified()) {
				modifiedElements
				.add(ApplicationManager.getTranslation(elementAt.toString(), this.table.getResourceBundle()));
			}
		}
		return modifiedElements;
	}

	protected void acceptDialog() {
		final int size = this.calculatedColumnList.getModel().getSize();
		final List names = new ArrayList();
		final List expressions = new ArrayList();
		final List renderKeys = new ArrayList();
		for (int i = 0; i < size; i++) {
			final Object elementAt = this.calculatedColumnList.getModel().getElementAt(i);
			if ((elementAt != null) && (elementAt instanceof Expression)) {
				names.add(((Expression) elementAt).getColumn());

				final String expression = ((Expression) elementAt).getExpression();
				final String renderKey = ((Expression) elementAt).getRenderKey();
				if ((expression != null) && (expression.trim().length() > 0)) {
					expressions.add(expression);
					renderKeys.add(renderKey);
				} else {
					MessageDialog.showMessage(this, "calculatedcols.expressionempty", JOptionPane.ERROR_MESSAGE,
							this.table.getResourceBundle(),
							new Object[] { ((Expression) elementAt).getColumn() });
					return;
				}
			}
		}

		// Validate the expressions --> Check for loops in the calculated
		// columns
		try {
			this.checkLoops(null);
		} catch (final Exception e) {
			CalculatedColumnDialog.logger.trace(null, e);
			MessageDialog.showMessage(this, "calculatedcols.loopsfound", JOptionPane.ERROR_MESSAGE,
					this.table.getResourceBundle());
			return;
		}

		this.table.configureCalculatedCols(names, expressions, renderKeys, true);
		// If some of the new visible columns has a different height or some of
		// the hidden one it is needed reevaluate the row height
		for (int i = 0; i < this.table.getVisibleColumns().size(); i++) {
			final Object col = this.table.getVisibleColumns().get(i);
			this.table.fitColumnSize(this.table.getColumnIndex((String) col));
		}
		this.setVisible(false);
	}

	protected void deleteSeletedCalculatedColumn() {
		if (this.deletedExpressions == null) {
			this.deletedExpressions = new ArrayList();
		}
		final Object selectedElement = this.calculatedColumnList.getSelectedValue();
		if ((this.newExpressions != null) && this.newExpressions.contains(selectedElement)) {
			this.newExpressions.remove(selectedElement);
		} else {
			this.deletedExpressions.add(selectedElement);
		}

		// calculatedColumnList.remove(calculatedColumnList.getSelectedIndex());
		if (this.calculatedColumnList.getModel() instanceof CustomListModel) {
			if (this.availableColumnsList.getModel() instanceof CustomListModel) {
				((CustomListModel) this.availableColumnsList.getModel())
				.removeElement(((Expression) this.calculatedColumnList.getSelectedValue()).getColumn());
			}
			((CustomListModel) this.calculatedColumnList.getModel())
			.removeElement(this.calculatedColumnList.getSelectedIndex());
		}

	}

	protected ComponentTextPane createExpressionEditor() {
		final ComponentTextPane editorPane = new ComponentTextPane();

		this.registerUndoableListener(editorPane);
		this.registerUndoRedoActions(editorPane);

		// Register a document listener to update the appearance of the text
		// when
		// the value changes
		editorPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				CalculatedColumnDialog.this.updateSelectedCalculatedColumnExpression();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				CalculatedColumnDialog.this.updateSelectedCalculatedColumnExpression();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});

		editorPane.addContainerListener(new ContainerAdapter() {

			@Override
			public void componentRemoved(final ContainerEvent e) {
				super.componentRemoved(e);
				CalculatedColumnDialog.this.updateSelectedCalculatedColumnExpression();
			}

		});

		return editorPane;
	}

	protected static String[] invalidChars = new String[] { ";", ":", " " };

	protected void createNewCalculatedColumn(final String defaultName) {
		String name = JOptionPane.showInputDialog(this,
				ApplicationManager.getTranslation("calculatedcols.insertnewcolumnname"), defaultName);
		if (name != null) {
			name = name.trim();
			if (name.length() == 0) {
				// The name can not be empty
				MessageDialog.showMessage(this, "calculatedcols.newcolumnnamecannotbeempty",
						JOptionPane.WARNING_MESSAGE, this.table.getResourceBundle());
				return;
			} else {
				for (int i = 0; i < CalculatedColumnDialog.invalidChars.length; i++) {
					if (name.indexOf(CalculatedColumnDialog.invalidChars[i]) >= 0) {
						final String message = ApplicationManager.getTranslation("calculatedcols.newcolumnnameinvalidchars",
								this.table.getResourceBundle(),
								CalculatedColumnDialog.invalidChars);
						MessageDialog.showMessage(this, message, JOptionPane.WARNING_MESSAGE,
								this.table.getResourceBundle());
						this.createNewCalculatedColumn(name);
						return;
					}
				}
			}

			// Now we have to check if the name does not already exist
			int size = this.calculatedColumnList.getModel().getSize();
			for (int i = 0; i < size; i++) {
				final Object elementAt = this.calculatedColumnList.getModel().getElementAt(i);
				if ((elementAt != null) && (elementAt instanceof Expression)
						&& ((Expression) elementAt).getColumn().equals(name)) {
					MessageDialog.showMessage(this, "calculatedcols.columnnamealreadyexist",
							JOptionPane.WARNING_MESSAGE, this.table.getResourceBundle());
					this.createNewCalculatedColumn(name);
					return;
				}
			}

			size = this.availableColumnsList.getModel().getSize();
			for (int i = 0; i < size; i++) {
				final Object elementAt = this.availableColumnsList.getModel().getElementAt(i);
				if ((elementAt != null) && elementAt.equals(name)) {
					MessageDialog.showMessage(this, "calculatedcols.columnnamealreadyexist",
							JOptionPane.WARNING_MESSAGE, this.table.getResourceBundle());
					this.createNewCalculatedColumn(name);
					return;
				}
			}

			// Object code = ((JList)
			// this.availableRenderList.getDataField()).getSelectedValue();

			final Expression exp = new Expression(name, null, true, Table.DEFAULT_CELL_RENDERER);

			((CustomListModel) this.calculatedColumnList.getModel()).addElement(exp);
			((CustomListModel) this.availableColumnsList.getModel()).addElement(name);

			this.calculatedColumnList.setSelectedValue(exp, false);

			if (this.newExpressions == null) {
				this.newExpressions = new ArrayList();
			}
			this.newExpressions.add(exp);
		}
	}

	protected int[] getExpressionIndexColName(final String colName, final String expression) {
		if ((colName != null) && (expression != null)) {
			final Pattern pattern = Pattern.compile("(^|[^0-9 ^A-Z ^a-z ^_]|\\s)" + colName + "([^0-9 ^A-Z ^a-z ^_]|$|\\s)");
			final Matcher matcher = pattern.matcher(expression);
			final List indices = new ArrayList();
			int start = 0;
			while (matcher.find(start)) {
				start = matcher.start();
				final int idx = matcher.group().indexOf(colName);
				indices.add(new Integer(start + idx));
				start += 1;
				if (start > (expression.length() - 1)) {
					break;
				}
			}
			final int[] result = new int[indices.size()];
			for (int i = 0; i < indices.size(); i++) {
				result[i] = ((Number) indices.get(i)).intValue();
			}
			return result;
		}
		return new int[0];
	}

	protected JList createList(final String attr, final String tip) {
		final JList list = new JList(new CustomListModel());
		if (tip != null) {
			list.setToolTipText(ApplicationManager.getTranslation(tip));
		}
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setName(attr);
		list.setBorder(BorderFactory.createEmptyBorder());
		return list;
	}

	protected SelectionListDataField createRendererList(final String attr, final String tip) {
		final Map parameters = new Hashtable();
		parameters.put("rows", 5);
		parameters.put("selection", "simple");
		parameters.put("attr", CalculatedColumnDialog.RENDER_TITLE);
		parameters.put("text", ApplicationManager.getTranslation(CalculatedColumnDialog.RENDER_TITLE));
		parameters.put("labelposition", "top");
		parameters.put("dim", "text");
		final SelectionListDataField list = new SelectionListDataField(parameters);
		if (tip != null) {
			list.setToolTipText(ApplicationManager.getTranslation(tip));
		}
		list.setName(attr);
		list.setBorder(BorderFactory.createEmptyBorder());
		list.setEnabled(true);
		return list;
	}

	protected void addColumnToExpression(final int index) {
		final Object value = this.availableColumnsList.getModel().getElementAt(index);
		if (!value.equals(((Expression) this.calculatedColumnList.getSelectedValue()).getColumn())) {
			// expressionPane.replaceSelection(value.toString());
			this.expressionPane.insertTextComponent(value.toString(),
					ApplicationManager.getTranslation(value.toString(), this.table.getResourceBundle()));
			this.expressionPane.requestFocus();
			this.updateSelectedCalculatedColumnExpression();
		}
	}

	/**
	 * Method used to update the text in the JTextPane using the selected calculated column
	 */
	protected void changeCalculatedColumnSelection() {

		// Disables the method to update the expression of the selected column
		this.updateSelectedCalculatedColumnExpression = false;

		try {

			final int index = this.calculatedColumnList.getSelectedIndex();
			final boolean bEnabled = index >= 0;
			this.enableGroup(bEnabled);

			if (bEnabled) {
				final Object value = this.calculatedColumnList.getModel().getElementAt(index);

				final String expression = ((Expression) value).getExpression();
				final String renderKey = ((Expression) value).getRenderKey();

				// TODO CHANGE RENDER LIST
				final List v = new Vector();
				v.add(renderKey);
				// this.availableRenderList.setItems(v);
				// this.availableRenderList.getValue();
				//

				this.availableRenderList.setValue(v);

				this.availableRenderList.repaint();

				this.setExpressionText(expression);

				this.expressionPane.setCaretPosition(this.expressionPane.getText().length());
				this.expressionPane.requestFocus();
				this.undoManager.discardAllEdits();

				if (!((Expression) value).isModifiable()) {
					this.deleteCalculatedColumnButton.setEnabled(false);
					this.expressionPane.setEnabled(false);
				}
			} else {
				this.expressionPane.setText("");
				this.availableRenderList.setValue(new Vector());
			}

			this.availableColumnsList.repaint();
		} finally {
			// Enables the method to update the expression of the selected
			// column
			this.updateSelectedCalculatedColumnExpression = true;
		}
	}

	protected void setExpressionText(final String expression) {
		this.expressionPane.setText(expression);

		// Change the column names if exist for a component with the
		// original and translated text
		final List listData = ((CustomListModel) this.availableColumnsList.getModel()).getListData();
		final Map hColumnPositions = new Hashtable();
		for (int i = 0; i < listData.size(); i++) {
			final String columnName = (String) listData.get(i);
			final int[] expressionIndexColName = this.getExpressionIndexColName(columnName, expression);
			if (expressionIndexColName.length > 0) {
				hColumnPositions.put(columnName, expressionIndexColName);
			}
		}

		if (!hColumnPositions.isEmpty()) {
			final Enumeration columnNames = Collections.enumeration(hColumnPositions.keySet());
			final List indices = new ArrayList();
			while (columnNames.hasMoreElements()) {
				final String colName = (String) columnNames.nextElement();
				final int[] positions = (int[]) hColumnPositions.get(colName);
				for (int i = 0; i < positions.length; i++) {
					indices.add(new ColumnPosition(colName, positions[i]));
				}
			}

			// this indices must be sort
			Collections.sort(indices);
			for (int k = indices.size() - 1; k >= 0; k--) {
				final int pos = ((ColumnPosition) indices.get(k)).position;
				final String col = ((ColumnPosition) indices.get(k)).columnName;
				this.expressionPane.setSelectionStart(pos);
				this.expressionPane.setSelectionEnd(pos + col.length());
				this.expressionPane.insertTextComponent(col,
						ApplicationManager.getTranslation(col, this.table.getResourceBundle()));
			}
		}
	}

	public static class ColumnPosition implements Comparable {

		public String columnName;

		public int position;

		public ColumnPosition(final String columnName, final int position) {
			this.columnName = columnName;
			this.position = position;
		}

		@Override
		public int compareTo(final Object o) {
			if (o instanceof ColumnPosition) {
				return this.position - ((ColumnPosition) o).position;
			}
			return 0;
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	protected void updateSelectedCalculatedColumnExpression() {
		if (this.updateSelectedCalculatedColumnExpression) {
			final Object selectedValue = this.calculatedColumnList.getSelectedValue();
			if ((selectedValue != null) && (selectedValue instanceof Expression)) {
				try {
					final String expression = this.expressionPane.getExpression();
					this.checkExpression(((Expression) selectedValue).getColumn(), expression);
					if (((Expression) selectedValue).isModifiable()) {
						((Expression) selectedValue).setExpression(expression);
					}
					// Call to expressionPane.setforeground blocks app when is
					// called (5.2068EN-0.1)
					// expressionPane.setForeground(Color.black);
				} catch (final Exception e) {
					CalculatedColumnDialog.logger.debug(null, e);
				}
			}
		}
	}

	protected void updateCalculatedColumnRenderExpression(final String renderKey) {
		if (this.updateSelectedCalculatedColumnExpression) {
			final Object selectedValue = this.calculatedColumnList.getSelectedValue();
			if ((selectedValue != null) && (selectedValue instanceof Expression)) {
				try {
					((Expression) selectedValue).setRenderKey(renderKey);
				} catch (final Exception e) {
					CalculatedColumnDialog.logger.debug(null, e);
				}
			}
		}

	}

	protected void configureCalculatedColsListRenderer(final SelectionListDataField list) {
		// list.setCellRenderer(new TranslationListCellRenderer() {
		//
		// @Override
		// public Component getListCellRendererComponent(JList list, Object
		// value, int index, boolean isSelected, boolean cellHasFocus) {
		// Component c = super.getListCellRendererComponent(list, value, index,
		// isSelected, cellHasFocus);
		//
		// // Only useful for the calculated column list to write in red if
		// // is
		// // not modifiable
		// if ((value instanceof Expression) && !((Expression)
		// value).isModifiable()) {
		// c.setForeground(Color.red);
		// }
		// return c;
		// }
		// });
	}

	protected void configureAvailableColsListRenderer(final JList list) {
		list.setCellRenderer(new TranslationListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				final Object selectedValue = CalculatedColumnDialog.this.calculatedColumnList.getSelectedValue();
				if ((selectedValue != null) && (selectedValue instanceof Expression)) {
					if ((value != null) && (value instanceof String)
							&& value.equals(((Expression) selectedValue).getColumn())) {
						c.setEnabled(false);
					}
				}
				return c;
			}

		});
	}

	protected Column createColumn(final boolean expand, final String title, final int preferredWidth, final boolean titleAsLabel, final String margin,
			final boolean expandlast, final Border border) {
		final Map params = this.getContainerParameters(expand, title, preferredWidth, 0, margin, titleAsLabel);

		params.put("expandlast", expandlast ? "yes" : "no");

		Row rowTitle = null;
		if ((title != null) && titleAsLabel) {
			rowTitle = this.buildLabelTitle(title);
		}
		final Column column = new Column(params);
		if (border != null) {
			column.setBorder(border);
		}

		if (rowTitle != null) {
			column.add(rowTitle, rowTitle.getConstraints(column.getLayout()));
		}
		this.internationalizationComponentList.add(column);
		return column;
	}

	protected Row buildLabelTitle(final String labelText) {
		Map hParams = DefaultXMLParametersManager.getParameters(Row.class.getName());

		hParams.put("margin", "2;2;2;2");
		hParams.put("bgimage", CalculatedColumnDialog.labelsBgImage);
		hParams.put("opaque", "yes");
		hParams.put("expand", "no");
		final Row row = new Row(hParams);

		hParams.clear();
		hParams = DefaultXMLParametersManager.getParameters(Label.class.getName());
		hParams.put(Label.ATTR, labelText);
		hParams.put(Label.TEXT, labelText);
		hParams.put(Label.ALIGN, Label.CENTER);
		final Label label = new Label(hParams);

		row.add(label);
		this.internationalizationComponentList.add(row);
		this.internationalizationComponentList.add(label);
		return row;
	}

	protected Row createRow(final boolean expand, final String title, final int preferredHeight, final String margin) {
		final Map params = this.getContainerParameters(expand, title, 0, preferredHeight, margin, false);
		final Row row = new Row(params);
		this.internationalizationComponentList.add(row);
		return row;
	}

	protected Map getContainerParameters(final boolean expand, final String title, final int preferredWidth, final int preferredHeight,
			final String margin, final boolean titleAsLabel) {
		final Map params = new Hashtable();
		params.put("expand", expand ? "yes" : "no");

		if (preferredWidth > 0) {
			params.put("width", "" + preferredWidth);
		}
		if (preferredHeight > 0) {
			params.put("height", "" + preferredHeight);
		}
		if (margin != null) {
			params.put("margin", margin);
		}

		if ((title != null) && !titleAsLabel) {
			params.put("title", title);
			params.put("titleposition", "belowtop");
		}

		return params;
	}

	protected Button createButton(final String key, final String text, final String iconPath, final boolean addToGroup, final String align) {
		final Map params = DefaultXMLParametersManager.getParameters(Button.class.getName());
		params.put(Button.KEY, key);
		params.put(Button.TIP, key);
		if (align != null) {
			params.put(Button.ALIGN, align);
		}
		if (text != null) {
			params.put(Button.TEXT, text);
		}
		if (iconPath != null) {
			params.put("icon", iconPath);
		}

		final Button button = new Button(params);
		this.internationalizationComponentList.add(button);
		if (addToGroup) {
			this.componentsGroup.add(button);
		}
		return button;
	}

	protected void createHtmlHelpField() {
		final Map param = DefaultXMLParametersManager.getParameters(HtmlHelpField.class.getName());
		param.put("attr", "calculatedcols.helpfield");
		param.put("template", CalculatedColumnDialog.HTML_HELP_TEXT);
		param.put("scroll", "yes");

		try {
			this.htmlHelpField = new HtmlHelpField(param);
			this.htmlHelpField.setBorder(new EmptyBorder(0, 5, 0, 5));
			this.internationalizationComponentList.add(this.htmlHelpField);
		} catch (final Exception e) {
			CalculatedColumnDialog.logger.error(null, e);
		}
	}

	/**
	 * Creates a CollapsiblePanel and add the help field to it
	 * @param field
	 */
	protected FormComponent createHelpPanel() {
		// <CollapsiblePanel title="Help"
		// anim="yes" expandvertical="no" startshowed="no"
		// borderclass="com.ontimize.gui.container.MattedDeployableBorder"
		// expand="yes">
		// <Row height="100" expand="yes">
		// <HtmlHelpField attr="branchHelpField"
		// scrollh="no" scroll="yes" scrollv="yes"
		// paint="white"
		// template="com/ontimize/quickstart/client/html/branch_diagramhelp.html"
		// />
		// </Row>
		// </CollapsiblePanel>

		this.createHtmlHelpField();
		// Column container = createColumn(true, null, 5, false, null,true);
		final Row container = this.createRow(true, null, 150, null);
		container.add(this.htmlHelpField, this.htmlHelpField.getConstraints(container.getLayout()));
		final Map param = DefaultXMLParametersManager.getParameters(CollapsiblePanel.class.getName());
		param.put("title", "calculatedcols.helptitle");
		param.put("expand", "yes");
		param.put("expandvertical", "yes");
		param.put("downicon", ImageManager.HELP_2);
		param.put("upicon", ImageManager.HELP);
		param.put("color", "147;197;231");
		param.put("recttitlecolor", "147;197;231");
		param.put("startshowed", "no");
		if (!param.containsKey("anim")) {
			param.put("anim", "no");
		}
		if (!param.containsKey("borderclass")) {
			param.put("borderclass", MattedDeployableBorder.class.getName());
		}

		final CollapsiblePanel collapsiblePanel = new CollapsiblePanel(param);
		this.internationalizationComponentList.add(collapsiblePanel);
		collapsiblePanel.add(container, container.getConstraints(collapsiblePanel.getLayout()));
		return collapsiblePanel;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		// Translate the title
		this.setTitle(ApplicationManager.getTranslation(CalculatedColumnDialog.CalculatedColsDialogTitle, res));

		// Translate the tip of the JList
		this.calculatedColumnList
		.setToolTipText(ApplicationManager.getTranslation(CalculatedColumnDialog.CALCULATED_COLUMNS_TIP, res));
		this.availableColumnsList
		.setToolTipText(ApplicationManager.getTranslation(CalculatedColumnDialog.AVAILABLE_COLUMMNS_TIP, res));

		// All the internationalizated components
		for (int i = 0; i < this.internationalizationComponentList.size(); i++) {
			final Object object = this.internationalizationComponentList.get(i);
			if (object instanceof Internationalization) {
				((Internationalization) object).setResourceBundle(res);
			}
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		for (int i = 0; i < this.internationalizationComponentList.size(); i++) {
			final Object object = this.internationalizationComponentList.get(i);
			if (object instanceof Internationalization) {
				((Internationalization) object).setComponentLocale(l);
			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		for (int i = 0; i < this.internationalizationComponentList.size(); i++) {
			final List temp = ((Internationalization) this.internationalizationComponentList.get(i)).getTextsToTranslate();
			if (temp != null) {
				v.addAll(temp);
			}
		}
		return v;
	}

	protected void updateCalculatedColsList() {
		if (this.calculatedColumnList.getModel() instanceof CustomListModel) {
			((CustomListModel) this.calculatedColumnList.getModel()).clear();
		} else {
			this.calculatedColumnList.setListData(new Object[0]);
		}

		if (this.table != null) {
			final Map h = ((TableSorter) this.table.getJTable().getModel()).getCalculatedColumns();
			final Enumeration enuK = Collections.enumeration(h.keySet());
			final Vector listValues = new Vector();
			final List originalCols = this.table.getOriginalCalculatedColumns();
			while (enuK.hasMoreElements()) {
				final Object k = enuK.nextElement();
				final boolean isModifiable = (this.table.isModifiableCalculatedColumns() && (originalCols != null)
						&& originalCols.contains(k)) || (originalCols == null)
						|| !originalCols
						.contains(k);

				final CellRenderer columnCellRenderer = (CellRenderer) this.table.getJTable()
						.getColumn(k.toString())
						.getCellRenderer();
				String renderKey = Table.DEFAULT_CELL_RENDERER;
				if (columnCellRenderer != null) {
					for (final Entry<String, CellRenderer> entry : Table.getRendererMap().entrySet()) {
						if (Table.DEFAULT_CELL_RENDERER.equalsIgnoreCase(entry.getKey().toString())) {
							continue;
						}
						final CellRenderer cellRend = entry.getValue();
						if (columnCellRenderer.getClass().equals(cellRend.getClass())) {
							renderKey = entry.getKey();
							break;
						}
					}
				}

				// Object value = ((JList)
				// this.availableRenderList.getDataField()).getSelectedValue();

				final Expression exp = new Expression(k.toString(), h.get(k).toString(), isModifiable, renderKey);
				listValues.add(exp);
			}
			if (this.calculatedColumnList.getModel() instanceof CustomListModel) {
				((CustomListModel) this.calculatedColumnList.getModel()).setListData(listValues);
			} else {
				this.calculatedColumnList.setListData(listValues);
			}
		}
	}

	protected void updateAvailableColsList() {
		if (this.availableColumnsList.getModel() instanceof CustomListModel) {
			((CustomListModel) this.availableColumnsList.getModel()).clear();
		} else {
			this.availableColumnsList.setListData(new Object[0]);
		}

		if (this.table != null) {
			final List availableColumns = this.table.getOperationColumns();
			if (availableColumns != null) {
				if (this.availableColumnsList.getModel() instanceof CustomListModel) {
					((CustomListModel) this.availableColumnsList.getModel()).setListData(availableColumns);
				} else {
					this.availableColumnsList.setListData(new Vector(availableColumns));
				}

			}
		}
	}

	public static void showCalculatorWindow(final Component c, final Table t) {
		final Window w = SwingUtilities.getWindowAncestor(t);
		CalculatedColumnDialog calculatedColumnsDialog = null;
		if (w instanceof Frame) {
			calculatedColumnsDialog = new CalculatedColumnDialog((Frame) w, t);
		} else if (w instanceof Dialog) {
			calculatedColumnsDialog = new CalculatedColumnDialog((Dialog) w, t);
		}
		final Dimension size = calculatedColumnsDialog.getSize();
		size.setSize(size.getWidth() < 400 ? 400 : size.getWidth(), size.getHeight() < 450 ? 450 : size.getHeight());
		ApplicationManager.center(calculatedColumnsDialog);
		calculatedColumnsDialog.setSize(size);
		calculatedColumnsDialog.setAutoPackOnOpen(false);
		calculatedColumnsDialog.setVisible(true);
		calculatedColumnsDialog.dispose();
	}

	protected static class InsertSymbolListener implements ActionListener {

		protected String value;

		protected JEditorPane expressionPane;

		public InsertSymbolListener(final String value, final JEditorPane pane) {
			this.value = value;
			this.expressionPane = pane;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (this.expressionPane.isEnabled()) {
				this.expressionPane.replaceSelection(this.value.toString());
				this.expressionPane.requestFocus();
			}
		}

	}

	protected class TranslationListCellRenderer extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if ((CalculatedColumnDialog.this.table != null) && (value != null)) {
				final String translation = ApplicationManager.getTranslation(value.toString(),
						CalculatedColumnDialog.this.table.getResourceBundle());

				if (c instanceof JTextComponent) {
					((JTextComponent) c).setText(translation);
				} else if (c instanceof JLabel) {
					((JLabel) c).setText(translation);
				}
			}
			return c;
		}

	}

	public static class Expression {

		protected String col = null;

		protected String renderKey = null;

		protected String originalExpression;

		protected String expression = null;

		private final boolean canChange;

		public Expression(final String col, final String expression, final boolean canChange, final String renderKey) {
			this.col = col;
			this.originalExpression = expression;
			this.expression = expression;
			this.canChange = canChange;
			this.renderKey = renderKey;
		}

		public String getColumn() {
			return this.col;
		}

		public String getExpression() {
			return this.expression;
		}

		public String getRenderKey() {
			return this.renderKey;
		}

		public void setColumn(final String col) {
			if (col != null) {
				this.col = col;
			}
		}

		public void setExpression(final String expression) {
			if (expression != null) {
				this.expression = expression;
			} else {
				this.expression = "";
			}
		}

		public void setRenderKey(final String renderKey) {
			this.renderKey = renderKey;
		}

		public boolean isModifiable() {
			return this.canChange;
		}

		public boolean isModified() {
			if (this.canChange) {
				if (this.originalExpression != null) {
					return !this.originalExpression.equals(this.expression);
				}
				return true;
			}
			return false;
		}

		@Override
		public String toString() {
			return this.col;
		}

	}

	protected static class CustomListModel extends AbstractListModel {

		protected List values;

		public CustomListModel() {
			this.values = new ArrayList();
		}

		public CustomListModel(final List list) {
			this.values = list;
		}

		public void setListData(final List values) {
			this.values = values;
		}

		public List getListData() {
			return this.values;
		}

		public void addElement(final Object element) {
			if (this.values == null) {
				this.values = new ArrayList();
			}
			this.values.add(element);
			this.fireIntervalAdded(this, this.values.size() - 1, this.values.size() - 1);
		}

		public void removeElement(final int index) {
			if ((this.values != null) && (this.values.size() > index)) {
				this.values.remove(index);
				this.fireIntervalRemoved(this, index, index);
			}
		}

		public void removeElement(final Object element) {
			if ((this.values != null) && this.values.contains(element)) {
				final int index = this.values.indexOf(element);
				this.values.remove(element);
				this.fireIntervalRemoved(this, index, index);
			}
		}

		public void clear() {
			if (this.values != null) {
				this.values.clear();
			}
		}

		@Override
		public Object getElementAt(final int index) {
			if ((this.values != null) && (this.values.size() > index)) {
				return this.values.get(index);
			}
			return null;
		}

		@Override
		public int getSize() {
			if (this.values != null) {
				return this.values.size();
			}
			return 0;
		}

	}

	protected void registerUndoableListener(final JTextComponent component) {
		component.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(final UndoableEditEvent e) {
				CalculatedColumnDialog.this.undoManager.addEdit(e.getEdit());
			}
		});
	}

	protected void registerUndoRedoActions(final JTextComponent component) {
		final InputMap inMap = component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		final ActionMap actMap = component.getActionMap();
		final KeyStroke ksUndo = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK, true);
		inMap.put(ksUndo, "Undo");
		actMap.put("Undo", new UndoAction(this.undoManager));

		final KeyStroke ksRedo = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK, true);
		inMap.put(ksRedo, "Redo");
		actMap.put("Redo", new RedoAction(this.undoManager));
	}

	protected void checkExpression(final String col, final String expr) throws Exception {
		CalculatedColumnDialog.logger.debug("Checking expression: {}", expr);
		final MathExpressionParser parser = CalculatedColumnDialog.createParser(col, expr,
				((CustomListModel) this.availableColumnsList.getModel()).getListData());
		parser.parseExpression(expr.toString());
		if (parser.hasError()) {
			CalculatedColumnDialog.logger.debug("Error in ( {} ) -->", expr, parser.getErrorInfo());
			// Call to expressionPane.setforeground blocks app when using l&f
			// and parser detects an expression error (5.2068EN-0.1)
			// expressionPane.setForeground(Color.red);
			throw new Exception(parser.getErrorInfo());
		}
	}

	protected void checkLoops(final List sourceExpressions) throws Exception {
		List calculatedColumns = null;
		if (sourceExpressions != null) {
			calculatedColumns = sourceExpressions;
		} else {
			calculatedColumns = ((CustomListModel) this.calculatedColumnList.getModel()).getListData();
		}
		final List dependentCalculatedColumns = new ArrayList();
		for (int i = 0; i < calculatedColumns.size(); i++) {
			final List calculatedColumnsDependence = this.getCalculatedColumnsDependence(
					((Expression) calculatedColumns.get(i)).getExpression(),
					((Expression) calculatedColumns.get(i)).getExpression(), calculatedColumns);
			if ((calculatedColumnsDependence != null) && (calculatedColumnsDependence.size() > 0)) {
				dependentCalculatedColumns.add(calculatedColumns.get(i));
			}
		}

		if (sourceExpressions != null) {
			if (sourceExpressions.size() == dependentCalculatedColumns.size()) {
				throw new Exception("Loops found");
			}
		}

		if (dependentCalculatedColumns.size() > 0) {
			// if the size is 1 then it means that the column depends on itself
			this.checkLoops(dependentCalculatedColumns);
		}
	}

	protected List getCalculatedColumnsDependence(final String expression, final String columnName, final List columns) {
		if (expression != null) {
			final List result = new ArrayList();
			final int count = columns.size();
			for (int i = 0; i < count; i++) {
				final String colDep = columns.get(i).toString();
				if (ExtendedTableModel.expressionContainsColName(colDep, expression,
						ExtendedTableModel.availableCalculatedColumnNameCharacterPattern)) {
					result.add(colDep);
				}
			}
			return result;
		}
		return null;
	}

	public static MathExpressionParser createParser(final String col, final String expr, final List columns) {

		final MathExpressionParser parser = MathExpressionParserFactory.getInstance();
		parser.setTraverse(ApplicationManager.DEBUG);
		final Map custom = JEPUtils.getCustomFunctions();
		final Enumeration keys = Collections.enumeration(custom.keySet());
		while (keys.hasMoreElements()) {
			final String key = (String) keys.nextElement();
			CalculatedColumnDialog.logger.debug("Add expression parser function: {} -> {}", key, custom.get(key));
			try {
				parser.addFunction(key, custom.get(key));
			} catch (final java.lang.NoSuchMethodError e) {
				CalculatedColumnDialog.logger.error(null, e);
			}
		}

		for (int i = 0; i < columns.size(); i++) {
			parser.addVariable(columns.get(i).toString(), 0.0);
		}

		return parser;

	}

	protected void configureRendererList(final JList list) {
		list.setCellRenderer(new TranslationListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				return c;
			}

		});
	}

}
