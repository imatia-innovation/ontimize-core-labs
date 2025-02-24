package com.ontimize.util.swing;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.JTextComponent;
import javax.swing.undo.UndoManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.actions.RedoAction;
import com.ontimize.gui.actions.UndoAction;
import com.ontimize.gui.field.SelectionListDataField.SelectableItem;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.JEPUtils;
import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;
import com.ontimize.util.swing.text.ComponentTextPane;

public class JCalculatedExpression extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(JCalculatedExpression.class);

	/**
	 * List of all components that conform the JCalculatedExpression
	 */
	protected List componentList = new Vector();

	/**
	 * The reference to the <code>UndoManager</code>.
	 */
	protected UndoManager undoManager = new UndoManager();

	/**
	 * The reference to the editor of the mathematical expression.
	 */
	protected ComponentTextPane expressionPane;

	/**
	 * Panel that contains all operation buttons.
	 */
	protected JPanel buttonPanel;

	/**
	 * Add button. Adds 'add' Math symbol to the editor.
	 */
	protected JButton addButton;

	/**
	 * Subtract button. Adds 'subtract' Math symbol to the editor.
	 */
	protected JButton substractButton;

	/**
	 * Multiply button. Adds 'multiply' Math symbol to the editor.
	 */
	protected JButton multiplyButton;

	/**
	 * Divide button. Adds 'divide' Math symbol to the editor.
	 */
	protected JButton divideButton;

	/**
	 * Open Parenthesis button. Adds 'open parenthesis' symbol to the editor.
	 */
	protected JButton openParenthesisButton;

	/**
	 * Close Parenthesis button. Adds 'close parenthesis' symbol to the editor.
	 */
	protected JButton closeParenthesisButton;

	/**
	 * JList component that contains a list with available fields to use into the editor of expressions.
	 * The components contained on it are {@link SelectableItem} that allows to visualize the name of
	 * the available field with a check to select if this field is required or not.
	 */
	protected JList availableFieldsList;

	/**
	 * SplitPane that contains the mathematical editor and the list of available fields.
	 */
	protected JSplitPane splitPane;

	public JCalculatedExpression() {
		super();
		this.init();
	}

	/**
	 * Initializes all components and distribute into the component. The panel has configured a
	 * GridBagLayout ant the distribution is the following: - First row: ButtonPanel with all operation
	 * buttons. - Second row: SplitPane that contains the editor(on left side) and the list of available
	 * fields(on the right side).
	 */
	protected void init() {

		this.setLayout(new GridBagLayout());

		// Expression Editor.
		this.expressionPane = this.createExpressionEditor(true);

		// //////////////////////////////////////////////////////////
		// ////////// Button Toolbar /////////////////
		// //////////////////////////////////////////////////////////
		this.buttonPanel = new JPanel();
		this.buttonPanel.setLayout(new GridBagLayout());
		this.buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		this.addButton = this.createButton(null, ImageManager.CALC_ADD, true);
		this.addButton.addActionListener(new InsertSymbolListener(" + ", this.expressionPane));
		this.componentList.add(this.addButton);

		this.substractButton = this.createButton(null, ImageManager.CALC_SUBSTRACT, true);
		this.substractButton.addActionListener(new InsertSymbolListener(" - ", this.expressionPane));
		this.componentList.add(this.substractButton);

		this.multiplyButton = this.createButton(null, ImageManager.CALC_MULTIPLY, true);
		this.multiplyButton.addActionListener(new InsertSymbolListener(" * ", this.expressionPane));
		this.componentList.add(this.multiplyButton);

		this.divideButton = this.createButton(null, ImageManager.CALC_DIVIDE, true);
		this.divideButton.addActionListener(new InsertSymbolListener(" / ", this.expressionPane));
		this.componentList.add(this.divideButton);

		this.openParenthesisButton = this.createButton(null, ImageManager.CALC_OPEN_PARENTHESIS, true);
		this.openParenthesisButton.addActionListener(new InsertSymbolListener(" ( ", this.expressionPane));
		this.componentList.add(this.openParenthesisButton);

		this.closeParenthesisButton = this.createButton(null, ImageManager.CALC_CLOSE_PARENTHESIS, true);
		this.closeParenthesisButton.addActionListener(new InsertSymbolListener(" ) ", this.expressionPane));
		this.componentList.add(this.closeParenthesisButton);

		this.buttonPanel.add(this.addButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.substractButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.multiplyButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.divideButton, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.openParenthesisButton,
				new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(this.closeParenthesisButton,
				new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
						new Insets(0, 0, 0, 0), 0, 0));
		this.buttonPanel.add(new JPanel(), new GridBagConstraints(6, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		// //////////////////////////////////////////////////////////
		// ////////// List of Available Expression Fields ////////////
		// //////////////////////////////////////////////////////////

		this.availableFieldsList = this.createList(null, true);

		this.availableFieldsList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (JCalculatedExpression.this.availableFieldsList.isEnabled()
						&& JCalculatedExpression.this.expressionPane.isEnabled() && (e.getClickCount() == 2)) {
					final int index = JCalculatedExpression.this.availableFieldsList.locationToIndex(e.getPoint());
					if (index >= 0) {
						JCalculatedExpression.this.addColumnToExpression(index);
					}
				}
			}
		});

		final JScrollPane expressionScroll = new JScrollPane(this.expressionPane);
		expressionScroll.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		final JScrollPane availableFieldsScroll = new JScrollPane(this.availableFieldsList);
		availableFieldsScroll.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.splitPane.add(expressionScroll);
		this.splitPane.add(availableFieldsScroll);
		this.splitPane.setDividerSize(10);
		this.splitPane.setOneTouchExpandable(true);
		this.splitPane.setResizeWeight(0.75);
		this.componentList.add(this.splitPane);

		this.setLayout(new GridBagLayout());
		this.add(this.buttonPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.add(this.splitPane, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

	}

	@Override
	public void setBorder(final Border border) {
		if ((border != null) && (this.splitPane != null)) {
			this.splitPane.setBorder(border);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if ((this.componentList != null) && !this.componentList.isEmpty()) {
			for (int i = 0; i < this.componentList.size(); i++) {
				((JComponent) this.componentList.get(i)).setEnabled(enabled);
			}
		}
	}

	/**
	 * Recovers the list of available fields to use into the mathematical expression.
	 * @return a <code>List</code> of elements.
	 */
	public List getAvailableFields() {
		if (this.availableFieldsList != null) {
			return ((CustomListModel) this.availableFieldsList.getModel()).getListData();
		}
		return null;
	}

	/**
	 * This method returns from stored available fields the one indicated by the specified index.
	 * @param index The index of the element to recover.
	 * @return an <code>Object</code> with the name of the available field.
	 */
	public Object getAvailableFields(final int index) {
		if (this.availableFieldsList != null) {
			return ((CustomListModel) this.availableFieldsList.getModel()).getElementAt(index);
		}
		return null;
	}

	public void removeAvailableFields(final int index) {
		if (this.availableFieldsList != null) {
			((CustomListModel) this.availableFieldsList.getModel()).removeElement(index);
		}
	}

	public void removeAvailableFields(final Object element) {
		if (this.availableFieldsList != null) {
			((CustomListModel) this.availableFieldsList.getModel()).removeElement(element);
		}
	}

	public void removeAllAvailableFields() {
		if (this.availableFieldsList != null) {
			((CustomListModel) this.availableFieldsList.getModel()).clear();
		}
	}

	/**
	 * Sets the list of fields that are going to be allowed to introduce into the mathematical
	 * expression of the editor.
	 * @param values A list with the values.
	 */
	public void setAvailableFields(final List values) {
		if (this.availableFieldsList != null) {
			((CustomListModel) this.availableFieldsList.getModel()).setListData(values);
		}
	}

	public void addAvailableFields(final Object field) {
		if (this.availableFieldsList != null) {
			((CustomListModel) this.availableFieldsList.getModel()).addElement(field);
		}
	}

	/**
	 * This method returns the mathematical expression contained into the editor.
	 * @return a <code>String</code> with the mathematical expression.
	 */
	public Object getExpression() {
		if (this.expressionPane != null) {
			return this.expressionPane.getExpression();
		}
		return null;
	}

	/**
	 * This method sets the mathematical expression into the editor.
	 * @param value The mathematical expression.
	 */
	public void setExpression(final Object value) {
		if (value == null) {
			this.expressionPane.setText(null);
			return;
		} else {
			this.setExpressionText(value.toString());
		}
	}

	/**
	 * This method sets the expression into the editor replacing the name of the available fields by
	 * <code>CustomLabel</code> objects. It allows to manage the name of the fields as a unique entity
	 * instead of a set of characters.
	 * @param expression
	 */
	protected void setExpressionText(final String expression) {
		this.expressionPane.setText(expression);

		// Change the column names if exist for a component with the
		// original and translated text
		final List listData = ((CustomListModel) this.availableFieldsList.getModel()).getListData();
		final Map hColumnPositions = new Hashtable();
		for (int i = 0; i < listData.size(); i++) {
			String columnName = null;
			final Object obj = listData.get(i);
			if (obj instanceof String) {
				columnName = (String) obj;
			} else {
				continue;
			}
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
				this.expressionPane.insertTextComponent(col, this.getFieldTranslation(col));
			}
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

	/**
	 * This method creates and configures 'editor' component.
	 * @param addToGroup Boolean that indicates if it is desired to add the current button to the list
	 *        of all components of the JCalculatedExpression.
	 * @return a <code>ComponentTextPane</code> component.
	 */
	protected ComponentTextPane createExpressionEditor(final boolean addToGroup) {
		final ComponentTextPane editorPane = new ComponentTextPane();
		editorPane.setBorder(BorderFactory.createEmptyBorder());
		this.expressionPane = editorPane;

		this.registerUndoableListener(editorPane);
		this.registerUndoRedoActions(editorPane);

		if (addToGroup) {
			this.componentList.add(this.expressionPane);
		}

		// Register a document listener to update the appearance of the text
		// when
		// the value changes
		editorPane.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(final DocumentEvent e) {
				JCalculatedExpression.this.updateSelectedCalculatedColumnExpression();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				JCalculatedExpression.this.updateSelectedCalculatedColumnExpression();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
			}
		});

		return editorPane;
	}

	/**
	 * This method updates the color of the text of the expression depending on if the expression is
	 * well defined (black foreground color) or not (red foreground color).
	 */
	protected void updateSelectedCalculatedColumnExpression() {
		if (this.expressionPane != null) {
			try {
				final String expression = this.expressionPane.getExpression();
				this.checkExpression(expression);
				this.expressionPane.setForeground(Color.black);
			} catch (final Exception e) {
				JCalculatedExpression.logger.trace(null, e);
				this.expressionPane.setForeground(Color.RED);
			}
		}
	}

	/**
	 * This method checks if the mathematical expression of the editor is well defined.
	 * @param expr The mathematical expression.
	 * @throws Exception if the expression is not well defined.
	 */
	protected void checkExpression(final String expr) throws Exception {
		final MathExpressionParser parser = JCalculatedExpression.createParser(expr,
				((CustomListModel) this.availableFieldsList.getModel()).getListData());
		parser.parseExpression(expr.toString());
		if (parser.hasError()) {
			if (ApplicationManager.DEBUG) {
				JCalculatedExpression.logger
				.debug(this.getClass().getName() + "  Error in (" + expr + ") --> " + parser.getErrorInfo());
			}
			throw new Exception(parser.getErrorInfo());
		}
	}

	/**
	 * This method creates the parser that is used to check the mathematical expression of the editor.
	 * @param expr The mathematical expression
	 * @param columns List of available fields that can be used into the expression.
	 * @return
	 */
	public static MathExpressionParser createParser(final String expr, final List columns) {

		final MathExpressionParser parser = MathExpressionParserFactory.getInstance();
		parser.setTraverse(ApplicationManager.DEBUG);
		final Map custom = JEPUtils.getCustomFunctions();
		final Enumeration keys = Collections.enumeration(custom.keySet());
		while (keys.hasMoreElements()) {
			final String key = (String) keys.nextElement();
			try {
				parser.addFunction(key, custom.get(key));
			} catch (final java.lang.NoSuchMethodError e) {
				JCalculatedExpression.logger.error(e.getMessage(), e);
			}
		}

		for (int i = 0; i < columns.size(); i++) {
			parser.addVariable(columns.get(i).toString(), 0.0);
		}

		return parser;
	}

	protected void registerUndoableListener(final JTextComponent component) {
		component.getDocument().addUndoableEditListener(new UndoableEditListener() {

			@Override
			public void undoableEditHappened(final UndoableEditEvent e) {
				JCalculatedExpression.this.undoManager.addEdit(e.getEdit());
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

	/**
	 * This method creates a new JButton
	 * @param text Text of the button.
	 * @param iconPath Path to the icon of the button.
	 * @param addToGroup Boolean that indicates if it is desired to add the current button to the list
	 *        of all components of the JCalculatedExpression.
	 * @return a <code>JButton</code> component.
	 */
	protected JButton createButton(final String text, final String iconPath, final boolean addToGroup) {

		final JButton button = new JButton();
		if (text != null) {
			button.setText(text);
		}
		if (iconPath != null) {
			final ImageIcon icon = ImageManager.getIcon(iconPath);
			button.setIcon(icon);
		}
		button.setOpaque(false);
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setMargin(new Insets(2, 2, 2, 2));
		this.installHighlight(button);

		if (addToGroup) {
			this.componentList.add(button);
		}
		return button;
	}

	protected void installHighlight(final JButton button) {
		if (button != null) {
			button.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					if (JCalculatedExpression.this.isEnabled()) {
						JCalculatedExpression.this.setOpaque(true);
						button.setContentAreaFilled(true);
					}
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					JCalculatedExpression.this.setOpaque(false);
					button.setContentAreaFilled(false);
				}
			});
		}
	}

	/**
	 * This method creates the JList component where are stored available field to use into the
	 * mathematical expression.
	 * @param values The list of elements to insert into the JList.
	 * @param addToGroup Boolean that indicates if it is desired to add the current button to the list
	 *        of all components of the JCalculatedExpression.
	 * @return a <code>JList</code> component.
	 */
	protected JList createList(final List values, final boolean addToGroup) {
		JList listSwing = null;
		if ((values != null) && !values.isEmpty()) {
			listSwing = new JList(new CustomListModel(values));
		} else {
			listSwing = new JList(new CustomListModel());
		}
		listSwing.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listSwing.setOpaque(true);
		listSwing.setBorder(BorderFactory.createEmptyBorder());

		if (addToGroup) {
			this.componentList.add(listSwing);
		}

		return listSwing;
	}

	/**
	 * This method adds selected available field from the list to the editor of expressions.
	 * @param index
	 */
	protected void addColumnToExpression(final int index) {
		final Object value = this.availableFieldsList.getModel().getElementAt(index);
		this.expressionPane.insertTextComponent(value.toString(), this.getFieldTranslation(value.toString()));
		this.expressionPane.requestFocus();
		this.expressionPane.getExpression();
	}

	protected String getFieldTranslation(final String field) {
		return field;
	}

	// ****************************************************************************************
	// ************************** Static Classes
	// ****************************************************************************************

	protected static class CustomListModel extends AbstractListModel {

		protected List values;

		public CustomListModel() {
			this.values = new Vector();
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
				this.values = new Vector();
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

	public static class Expression {

		protected String col = null;

		protected String originalExpression;

		protected String expression = null;

		private final boolean canChange;

		public Expression(final String col, final String expression, final boolean canChange) {
			this.col = col;
			this.originalExpression = expression;
			this.expression = expression;
			this.canChange = canChange;
		}

		public String getColumn() {
			return this.col;
		}

		public String getExpression() {
			return this.expression;
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

}
