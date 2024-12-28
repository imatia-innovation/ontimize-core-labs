package com.ontimize.db.query;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.query.store.FileQueryStore;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.AdvancedQueryEntity;
import com.ontimize.jee.common.db.ContainsExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.ContainsOperator;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.SQLStatementBuilder.Field;
import com.ontimize.jee.common.db.SQLStatementBuilder.Operator;
import com.ontimize.jee.common.db.query.ParameterField;
import com.ontimize.jee.common.db.query.QueryExpression;
import com.ontimize.jee.common.db.query.store.QueryStore;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.CollectionTools;

public class QueryBuilder extends JPanel {

	private static final Logger logger = LoggerFactory.getLogger(QueryBuilder.class);


	public static boolean DEBUG = false;

	/**
	 * Variable to indicate when must be shown the save/load expressions buttons without the Report
	 */
	private boolean basicSave = false;

	public boolean getBasicSave() {
		return this.basicSave;
	}

	public boolean setBasicSave(final boolean basicSave) {
		return this.basicSave = basicSave;
	}

	protected static class ExpressionRenderer extends DefaultTableCellRenderer {

		public ExpressionRenderer() {
			super();
		}

		@Override
		public Component getTableCellRendererComponent(final JTable t, Object v, final boolean s, final boolean f, final int r, final int c) {

			if (v instanceof Expression) {
				v = ExtendedSQLConditionValuesProcessor.createQueryConditionsExpress((Expression) v);
			}
			final Component comp = super.getTableCellRendererComponent(t, v, s, false, r, c);

			if (c == 1) {
				if (s) {
					comp.setForeground(new Color(255, 255, 255));
				} else {
					comp.setForeground(new Color(0, 0, 0));
				}
			}
			return comp;
		}

	}

	protected static class ColumnsComparator implements Comparator {

		private ResourceBundle bundle = null;

		public ColumnsComparator(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public int compare(final Object o1, final Object o2) {
			if (((String) o1).equals(ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN)) {
				return -1;
			}
			if (((String) o2).equals(ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN)) {
				return 1;
			}

			final String s1 = ApplicationManager.getTranslation(o1.toString(), this.bundle);
			final String s2 = ApplicationManager.getTranslation(o2.toString(), this.bundle);
			return s1.compareToIgnoreCase(s2);
		}

	}

	public static int getVarchar() {
		return ConditionsTableModel.VARCHAR;
	}

	public static int getDate() {
		return ConditionsTableModel.DATE;
	}

	public static int getNumber() {
		return ConditionsTableModel.NUMBER;
	}

	public static int getAny() {
		return ConditionsTableModel.ANY;
	}

	public static boolean needsExpressionParameters(final Expression e) {
		if (e == null) {
			return false;
		}
		if (e.getLeftOperand() instanceof Expression) {
			boolean aux = QueryBuilder.needsExpressionParameters((Expression) e.getLeftOperand());
			if (aux) {
				return true;
			}
			if (e.getRightOperand() instanceof Expression) {
				aux = QueryBuilder.needsExpressionParameters((Expression) e.getRightOperand());
			}
			return aux;
		} else {
			return (e.getRightOperand() != null) && (e.getRightOperand() instanceof ParameterField)
					&& (((ParameterField) e.getRightOperand()).getValue() == null);
		}
	}

	public static boolean hasExpressionParameters(final Expression e) {
		if (e == null) {
			return false;
		}
		if (e.getLeftOperand() instanceof Expression) {
			boolean aux = QueryBuilder.needsExpressionParameters((Expression) e.getLeftOperand());
			if (aux) {
				return true;
			}
			if (e.getRightOperand() instanceof Expression) {
				aux = QueryBuilder.hasExpressionParameters((Expression) e.getRightOperand());
			}
			return aux;
		} else {
			return (e.getRightOperand() != null) && (e.getRightOperand() instanceof ParameterField);
		}
	}

	protected static class ConditionsTableModel extends AbstractTableModel {

		public static final int NUMBER = 0;

		public static final int DATE = 1;

		public static final int VARCHAR = 2;

		public static final int ANY = 3;

		public static final int BOOLEAN = 4;

		protected Expression[] expressions = new Expression[0];

		protected String[] columns = null;

		protected ArrayList expressionList = new ArrayList();

		protected java.util.List auxcols = new ArrayList();

		protected java.util.List auxtypes = new ArrayList();

		protected ResourceBundle bundle = null;

		protected java.util.List getColumnTypes() {
			return this.auxtypes;
		}

		public ConditionsTableModel(final ResourceBundle bundle) {
			this.bundle = bundle;
			this.columns = new String[] { ApplicationManager.getTranslation("QueryBuilderField", bundle),
					ApplicationManager.getTranslation("QueryBuilderOperator",
							bundle),
					ApplicationManager.getTranslation("QueryBuilderValue", bundle),
					ApplicationManager.getTranslation("QueryBuilderExpression", bundle) };
		}

		protected void setExpressions(final java.util.List list) {
			final Expression[] e = new Expression[list.size()];

			for (int i = 0, j = list.size(); i < j; i++) {
				e[i] = (Expression) list.get(i);
			}
			this.expressions = e;
		}

		protected void setColumnTypes(final Object[] auxcols, final int[] auxtypes) {
			this.auxcols = java.util.Arrays.asList(auxcols);
			for (int i = 0, a = auxtypes.length; i < a; i++) {
				this.auxtypes.add(new Integer(auxtypes[i]));
			}
		}

		public Operator[] getTypeOperators(final int type) {
			switch (type) {
			case NUMBER:
				return new Operator[] { BasicOperator.EQUAL_OP, BasicOperator.NOT_EQUAL_OP, BasicOperator.LESS_OP,
						BasicOperator.LESS_EQUAL_OP, BasicOperator.MORE_OP, BasicOperator.MORE_EQUAL_OP,
						BasicOperator.NULL_OP, BasicOperator.NOT_NULL_OP, BasicOperator.LIKE_OP,
						BasicOperator.NOT_LIKE_OP };

			case DATE:
				return new Operator[] { BasicOperator.EQUAL_OP, BasicOperator.NOT_EQUAL_OP, BasicOperator.LESS_OP,
						BasicOperator.LESS_EQUAL_OP, BasicOperator.MORE_OP, BasicOperator.MORE_EQUAL_OP,
						BasicOperator.NULL_OP, BasicOperator.NOT_NULL_OP, BasicOperator.LIKE_OP,
						BasicOperator.NOT_LIKE_OP };

			case VARCHAR:
				return new Operator[] { BasicOperator.EQUAL_OP, BasicOperator.NOT_EQUAL_OP, BasicOperator.LIKE_OP,
						BasicOperator.NOT_LIKE_OP, BasicOperator.NULL_OP, BasicOperator.NOT_NULL_OP };

			case ANY:
				return new Operator[] { ContainsOperator.CONTAINS_OP, ContainsOperator.NOT_CONTAINS_OP };

			case BOOLEAN:
				return new Operator[] { BasicOperator.EQUAL_OP, BasicOperator.NOT_EQUAL_OP, BasicOperator.NULL_OP,
						BasicOperator.NOT_NULL_OP };

			default:
				return new Operator[] { BasicOperator.NULL_OP, BasicOperator.NOT_NULL_OP, BasicOperator.LIKE_OP,
						BasicOperator.NOT_LIKE_OP };
			}
		}

		protected void addExpression() {
			this.addExpression(new BasicExpression(null, null, null));
		}

		protected void addExpression(final Expression e) {
			final Expression[] ne = new Expression[this.expressions.length + 1];
			final BasicExpression be = (BasicExpression) e;
			System.arraycopy(this.expressions, 0, ne, 0, this.expressions.length);
			ne[this.expressions.length] = be;
			this.expressions = ne;
			this.expressionList.add(e);

			this.fireTableChanged(new TableModelEvent(this));

		}

		private void initListExpression(final Expression e) {
			if (e.getRightOperand() instanceof Expression) {
				this.initListExpression((Expression) e.getLeftOperand());
				this.initListExpression((Expression) e.getRightOperand());
			} else {
				this.expressionList.add(e);
			}
		}

		public void addInitExpression(final Expression e) {
			// Insert all in the list
			this.initListExpression(e);
			final Expression[] ne = new Expression[this.expressions.length + 1];
			final BasicExpression be = (BasicExpression) e;
			System.arraycopy(this.expressions, 0, ne, 0, this.expressions.length);
			ne[this.expressions.length] = be;
			this.expressions = ne;
			this.fireTableChanged(new TableModelEvent(this));
		}

		public java.util.List getExpressionsList() {
			return this.expressionList;
		}

		public void clearExpressionValues() {
			if (this.expressionList == null) {
				return;
			}

			for (int i = 0, a = this.expressionList.size(); i < a; i++) {
				if (((Expression) this.expressionList.get(i)).getRightOperand() instanceof ParameterField) {
					((ParameterField) ((Expression) this.expressionList.get(i)).getRightOperand()).setValue(null);
				}
			}
			this.fireTableChanged(new TableModelEvent(this));
		}

		@Override
		public int getRowCount() {
			return this.expressions != null ? this.expressions.length : 0;
		}

		@Override
		public int getColumnCount() {
			return this.columns.length;
		}

		@Override
		public String getColumnName(final int c) {
			return this.columns[c];
		}

		@Override
		public Object getValueAt(final int r, final int c) {
			if (r >= this.expressions.length) {
				return null;
			}
			if (c == 0) {
				return this.expressions[r].getLeftOperand();
			} else if (c == 1) {
				return this.expressions[r].getOperator();
			} else if (c == 2) {
				return this.expressions[r].getRightOperand();
			} else if (c == 3) {
				return ContainsExtendedSQLConditionValuesProcessor.createQueryConditionsExpress(this.expressions[r]);
			} else {
				return null;
			}
		}

		public void setValueAtCustom(final Object v, final int r, final int c) throws Exception {

			if (!(r < this.expressions.length)) {
				return;
			}
			if (c == 0) {

				if ((v instanceof Field) || (v instanceof Expression) || (v == null)) {
					super.setValueAt(v, r, c);
					this.expressions[r].setLeftOperand(v);
				} else if (v instanceof String) {
					final BasicField bf = new BasicField((String) v);
					super.setValueAt(bf, r, c);
					super.setValueAt(null, r, 1);
					super.setValueAt(null, r, 2);

					this.expressions[r].setLeftOperand(bf);
					this.expressions[r].setOperator(null);
					this.expressions[r].setRightOperand(null);
				} else {
					throw new IllegalArgumentException("must be field or expression");
				}
			} else if (c == 1) {
				if ((v == null) || (v instanceof Operator)) {
					super.setValueAt(v, r, c);
					this.expressions[r].setOperator((Operator) v);
					super.setValueAt(null, r, 2);
					this.expressions[r].setRightOperand(null);
				} else {
					throw new IllegalArgumentException("must be operator");
				}
			} else if (c == 2) {
				if (v instanceof String) {

					final String s = ((BasicField) this.expressions[r].getLeftOperand()).toString().trim();
					final int i = this.auxcols.indexOf(s);
					if (ConditionsTableModel.NUMBER == ((Integer) this.auxtypes.get(i)).intValue()) {
						String sValue = (String) v;

						if (v.equals("")) {
							return;
						}

						if (this.expressions[r].getOperator().toString().equals(BasicOperator.LIKE)
								|| this.expressions[r].getOperator()
								.toString()
								.equals(BasicOperator.NOT_LIKE)) {
							sValue = sValue.replace('?', '0');
							sValue = sValue.replace('*', '0');
						}
						try {
							final double current = Double.parseDouble(sValue);
							QueryBuilder.logger.trace("Value is right : {}", current);
						} catch (final Exception e) {
							throw new Exception("M_QueryBuilderErrorInsercionEsperaInt", e);
						}
					} else if (ConditionsTableModel.BOOLEAN == ((Integer) this.auxtypes.get(i)).intValue()) {
						final String sValue = (String) v;
						if (!sValue.equals("0") && !sValue.equals("1")) {
							throw new Exception("M_QueryBuilderErrorInsercionEsperaBoo");
						}
					}
				}
				super.setValueAt(v, r, c);
				if (v instanceof String) {
					this.expressions[r].setRightOperand(v);
				} else {
					this.expressions[r].setRightOperand(v);
				}
			}
			this.fireTableChanged(new TableModelEvent(this));
		}

		@Override
		public boolean isCellEditable(final int r, final int c) {
			if (c == 0) {
				final Object v = this.getValueAt(r, 0);
				if (v instanceof Expression) {
					return false;
				}
				return true;
			} else if (c == 1) {
				final Object v = this.getValueAt(r, 0);
				if (v == null) {
					return false;
				} else {
					return true;
				}
			} else if (c == 2) {
				final Object v = this.getValueAt(r, 0);
				final Object v2 = this.getValueAt(r, 1);
				if (v instanceof Expression) {
					return false;
				}

				if ((v == null) || (v2 == null) || (v2 == BasicOperator.NULL_OP) || (v2 == BasicOperator.NOT_NULL_OP)) {
					this.setValueAt(null, r, 2);
					return false;
				} else {
					return true;
				}
			} else {
				return false;
			}
		}

		public Expression getExpression(final int r) {
			if (r < this.expressions.length) {
				return this.expressions[r];
			}
			return null;
		}

		public void split(final int r) {
			if ((r < 0) || (r >= this.getRowCount())) {
				return;
			}
			// Combine the expressions
			if (!this.isExpressionOK(r)) {
				return;
			}
			// Split only if it is a composed expressions
			if (!(this.expressions[r].getLeftOperand() instanceof Expression)) {
				return;
			}
			if (!(this.expressions[r].getRightOperand() instanceof Expression)) {
				return;
			}
			// Insert left expression in r
			// and right in r+1
			final Expression[] newE = new Expression[this.expressions.length + 1];
			for (int i = 0; i < r; i++) {
				newE[i] = this.expressions[i];
			}
			newE[r] = (Expression) this.expressions[r].getRightOperand();
			newE[r + 1] = (Expression) this.expressions[r].getLeftOperand();
			for (int i = r + 2; i < newE.length; i++) {
				newE[i] = this.expressions[i - 1];
			}
			this.expressions = newE;
			this.fireTableDataChanged();
		}

		public void operationBetweenRows(final int[] rows, final Operator op) {
			// Go through the objects
			// Get the lower row
			// Order
			Arrays.sort(rows);
			final ArrayList list = new ArrayList();

			for (int i = 0, j = rows.length; i < j; i++) {
				if (!this.isExpressionOKWithoutParameters(rows[i])) {
					return;
				}
			}

			for (int i = rows.length - 1; i >= 0; i--) {
				final int r = rows[i];
				if ((r < 0) || (r >= this.getRowCount())) {
					continue;
				}
				// Combine expressions
				list.add(this.expressions[r]);
				this.expressions[r] = null;
				for (int j = r + 1; j < this.expressions.length; j++) {
					this.expressions[j - 1] = this.expressions[j];
				}
				final Expression[] newE = new Expression[this.expressions.length - 1];
				System.arraycopy(this.expressions, 0, newE, 0, newE.length);
				this.expressions = newE;
			}
			if (list.size() < 2) {
				return;
			}

			BasicExpression be = new BasicExpression(list.get(0), op, list.get(1));
			for (int i = 2; i < list.size(); i++) {
				be = new BasicExpression(be, op, list.get(i));
			}
			// Insert the expression in rows[0]
			final Expression[] newE = new Expression[this.expressions.length + 1];
			for (int i = 0; i < rows[0]; i++) {
				newE[i] = this.expressions[i];
			}
			newE[rows[0]] = be;
			for (int i = rows[0] + 1; i < newE.length; i++) {
				newE[i] = this.expressions[i - 1];
			}
			this.expressions = newE;
			this.fireTableDataChanged();
		}

		public void orBetweenRows(final int[] rows) {
			this.operationBetweenRows(rows, BasicOperator.OR_OP);
		}

		public void doAndBetweenRows(final int[] rows) {
			this.operationBetweenRows(rows, BasicOperator.AND_OP);
		}

		public void doAndNotBetweenRows(final int[] rows) {
			this.operationBetweenRows(rows, BasicOperator.AND_NOT_OP);
		}

		public void orNotBetweenRows(final int[] rows) {
			this.operationBetweenRows(rows, BasicOperator.OR_NOT_OP);
		}

		private void removeListExpression(final Expression e) {

			if (e.getLeftOperand() instanceof Expression) {
				this.removeListExpression((Expression) e.getLeftOperand());
				this.removeListExpression((Expression) e.getRightOperand());
			} else {
				final int a = this.expressionList.size();
				int i = 0;
				while ((i < a) && !this.expressionList.get(i).equals(e)) {
					i++;
				}
				if (i < a) {
					this.expressionList.remove(i);
				}
			}
		}

		public void removeRows(final int[] rows) {
			Arrays.sort(rows);

			for (int i = 0, a = rows.length; i < a; i++) {
				this.removeListExpression(this.expressions[rows[i]]);
			}

			final Expression[] newE = new Expression[this.expressions.length - rows.length];
			int a = 0;
			int b = 0;
			for (int i = 0; i < this.expressions.length; i++) {
				if ((a >= rows.length) || (i != rows[a])) {
					newE[b] = this.expressions[i];
					b++;
				} else {
					a++;
				}
			}

			this.expressions = newE;
			this.fireTableDataChanged();
		}

		public void removeAllExpressions() {
			this.expressions = new Expression[0];
			this.expressionList = new ArrayList();
			this.fireTableDataChanged();
		}

		protected boolean isExpressionOKWithoutParameters(final int r) {
			return this.isExpressionOKWithoutParameters(this.expressions[r]);
		}

		protected boolean isExpressionOKWithoutParameters(final Expression e) {
			return this.analyzeExpression(e, false);
		}

		protected boolean isExpressionOK(final int r) {
			return this.isExpressionOK(this.expressions[r]);
		}

		protected boolean isExpressionOK(final Expression e) {
			return this.analyzeExpression(e, true);
		}

		private boolean analyzeExpression(final Expression e, final boolean parameters) {

			if (e == null) {
				return false;
			}

			if (e.getLeftOperand() instanceof Expression) {
				boolean aux = this.analyzeExpression((Expression) e.getLeftOperand(), parameters);
				if (!aux) {
					return false;
				}
				if (e.getRightOperand() instanceof Expression) {
					aux = this.analyzeExpression((Expression) e.getRightOperand(), parameters);
				}
				return aux;
			} else {
				return (e.getLeftOperand() != null) && (e.getOperator() != null)
						&& ((!parameters && (e.getRightOperand() != null)) || (parameters && (e
								.getRightOperand() != null) && !(e.getRightOperand() instanceof ParameterField))
								|| (parameters && (e
										.getRightOperand() != null) && (e.getRightOperand() instanceof ParameterField)
										&& (((ParameterField) e.getRightOperand()).getValue() != null))
								|| e
								.getOperator()
								.toString()
								.equals(BasicOperator.NULL_OP.toString())
								|| e.getOperator().toString().equals(BasicOperator.NOT_NULL_OP.toString()));
			}
		}

		protected boolean needsExpressionParameters(final int r) {
			if (r >= this.expressions.length) {
				return false;
			}
			return QueryBuilder.needsExpressionParameters(this.expressions[r]);
		}

	}

	static class CustomCellEditor extends DefaultCellEditor {

		CustomCellEditor(final JComboBox combo) {
			super(combo);
			this.setClickCountToStart(2);
		}

		CustomCellEditor(final JTextField tf) {
			super(tf);
			this.setClickCountToStart(2);
		}

		@Override
		public boolean shouldSelectCell(final EventObject anEvent) {
			return true;
		}

		@Override
		public boolean isCellEditable(final EventObject e) {
			if (e instanceof MouseEvent) {
				if (((MouseEvent) e).isControlDown()) {
					return false;
				} else if (((MouseEvent) e).isShiftDown()) {
					return false;
				}
				if (((MouseEvent) e).getClickCount() < this.getClickCountToStart()) {
					return false;
				}
			}

			return true;
		}

	}

	protected static class CustomDefaultListCellRenderer extends DefaultListCellRenderer {

		ResourceBundle bundle = null;

		public CustomDefaultListCellRenderer(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {

			final Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (c instanceof JLabel) {
				((JLabel) c).setText(ApplicationManager.getTranslation(((JLabel) c).getText(), this.bundle));
			}

			return c;
		}

	}

	static class ComboEditor extends CustomCellEditor /*
	 * implements TableCellEditor
	 */ {

		boolean editable = false;

		ResourceBundle bundle;

		public ComboEditor(final ResourceBundle bundle) {
			super(new JComboBox());
			this.bundle = bundle;
		}

		public ComboEditor(final boolean editable, final ResourceBundle bundle) {
			super(new JComboBox());
			this.editable = editable;
			this.bundle = bundle;
		}

		Object[] items = null;

		@Override
		public Component getTableCellEditorComponent(final JTable t, final Object v, final boolean s, final int r, final int c) {
			final Component comp = super.getTableCellEditorComponent(t, v, s, r, c);
			if (comp instanceof JComboBox) {
				if (((DefaultComboBoxModel) ((JComboBox) comp).getModel()).getIndexOf(v) < 0) {
					((JComboBox) comp).setSelectedItem(v);
				}
				((JComboBox) comp).setSelectedItem(v);

				if (this.editable) {
					((JComboBox) comp).setEditable(true);
				}

			}
			return comp;
		}

		public void setItems(final Object[] items) {
			this.items = items;
			((JComboBox) super.getComponent()).setRenderer(new CustomDefaultListCellRenderer(this.bundle));
			((JComboBox) super.getComponent()).setModel(new DefaultComboBoxModel(this.items));
		}

	};

	static class TextEditor extends CustomCellEditor implements TableCellEditor {

		public TextEditor() {
			super(new JTextField());
		}

		@Override
		public Component getTableCellEditorComponent(final JTable t, final Object v, final boolean s, final int r, final int c) {
			final Component comp = super.getTableCellEditorComponent(t, v, s, r, c);
			return comp;
		}

	};

	protected static class ETable extends JTable {

		protected ExpressionRenderer expressionRenderer = new ExpressionRenderer();

		public Expression getExpression(final int i) {
			return null;
		}

		public boolean expressionOK(final Expression e) {
			return false;
		}

		@Override
		public TableCellRenderer getCellRenderer(final int r, final int c) {
			return this.expressionRenderer;
		}

	}

	protected static class CustomTableCellRenderer extends DefaultTableCellRenderer {

		ResourceBundle bundle;

		public CustomTableCellRenderer(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
				final int row, final int column) {
			final Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
			if ((column == 0) || (column == 2)) {

				if (value instanceof ParameterField) {
					if (!isSelected) {
						if (((ParameterField) value).getValue() == null) {
							c.setForeground(new Color(255, 0, 0));
						} else {
							c.setForeground(new Color(0, 0, 255));
						}
					} else {
						c.setForeground(new Color(255, 255, 255));
					}
				} else {
					if (value instanceof Field) {
						if (!isSelected) {
							c.setForeground(new Color(0, 0, 255));
						} else {
							c.setForeground(new Color(255, 255, 255));
						}
					} else {
						if (!isSelected) {
							c.setForeground(new Color(0, 0, 0));
						} else {
							c.setForeground(new Color(255, 255, 255));
						}
					}
				}

				if (c instanceof JLabel) {

					if (column == 0) {
						if (((ConditionsTableModel) table.getModel()).getExpression(row)
								.getLeftOperand() instanceof Expression) {
							((JLabel) c).setText(ContainsSQLConditionValuesProcessorHelper
									.renderQueryConditionsExpressBundle(
											(Expression) ((ConditionsTableModel) table.getModel()).getExpression(row)
											.getLeftOperand(),
											this.bundle));
						} else {

							if (((ConditionsTableModel) table.getModel()).getExpression(row).getLeftOperand() != null) {
								((JLabel) c).setText(ApplicationManager
										.getTranslation(
												((Field) ((ConditionsTableModel) table.getModel()).getExpression(row)
														.getLeftOperand()).toString(),
												this.bundle));
							}
						}
					} else {
						if (((ConditionsTableModel) table.getModel()).getExpression(row)
								.getRightOperand() instanceof Expression) {
							((JLabel) c).setText(ContainsSQLConditionValuesProcessorHelper
									.renderQueryConditionsExpressBundle(
											(Expression) ((ConditionsTableModel) table.getModel()).getExpression(row)
											.getRightOperand(),
											this.bundle));
						} else {
							if (((ConditionsTableModel) table.getModel()).getExpression(row)
									.getRightOperand() != null) {
								if (((ConditionsTableModel) table.getModel()).getExpression(row)
										.getRightOperand() instanceof Field) {
									((JLabel) c).setText(ApplicationManager
											.getTranslation(
													((Field) ((ConditionsTableModel) table.getModel()).getExpression(row)
															.getRightOperand()).toString(),
													this.bundle));
								} else {
									((JLabel) c).setText(ApplicationManager.getTranslation(
											(String) ((ConditionsTableModel) table.getModel()).getExpression(row)
											.getRightOperand(),
											this.bundle));
								}
							}
						}
					}
				}
			}

			return c;

		}

	}

	protected static class CustomTableCellRendererExpression extends DefaultTableCellRenderer {

		ResourceBundle bundle;

		public CustomTableCellRendererExpression(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
				final int row, final int column) {
			final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (c instanceof JLabel) {
				if (column == 3) {

					if (((ConditionsTableModel) table.getModel()).getRowCount() != 0) {
						((JLabel) c).setText(ContainsSQLConditionValuesProcessorHelper
								.renderQueryConditionsExpressBundle(
										((ConditionsTableModel) table.getModel()).getExpression(row), this.bundle));
						final Expression e = ((ConditionsTableModel) table.getModel()).getExpression(row);
						if (!isSelected) {
							if (((ConditionsTableModel) table.getModel()).isExpressionOK(e)) {
								final Color b = new Color(0, 0, 0);
								c.setForeground(b);
							} else {
								final Color b = new Color(255, 0, 0);
								c.setForeground(b);

							}
						}
					}
				}
			}
			return c;
		}

	}

	protected static class pvTableModel extends AbstractTableModel {

		private java.util.List parameterValueList = null;

		private ResourceBundle bundle = null;

		private int[] types;

		private String[] cols;

		public pvTableModel(final ResourceBundle bundle, final java.util.List l, final String[] cols, final int[] types) {
			this.bundle = bundle;
			this.parameterValueList = l;
			this.cols = cols;
			this.types = types;
		}

		public void setColsAndTypes(final String[] cols, final int[] types) {
			this.types = types;
			this.cols = cols;
		}

		@Override
		public int getRowCount() {
			if (this.parameterValueList == null) {
				return 0;
			}
			return this.parameterValueList.size();
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(final int c) {
			if (c == 0) {
				return ApplicationManager.getTranslation("QueryBuilderField", this.bundle);
			}
			if (c == 1) {
				return ApplicationManager.getTranslation("QueryBuilderOperator", this.bundle);
			}
			if (c == 2) {
				return ApplicationManager.getTranslation("QueryBuilderValue", this.bundle);
			}
			return null;
		}

		@Override
		public Object getValueAt(final int r, final int c) {
			if (this.parameterValueList == null) {
				return null;
			}

			if (r < this.parameterValueList.size()) {
				final Expression e = (Expression) this.parameterValueList.get(r);
				if (c == 0) {
					return e.getLeftOperand();
				} else if (c == 1) {
					return e.getOperator();
				} else if (c == 2) {
					if (((ParameterField) ((Expression) this.parameterValueList.get(r)).getRightOperand())
							.getValue() == null) {
						return null;
					}
					return ((ParameterField) e.getRightOperand()).getValue();
				} else {
					return null;
				}
			}
			return null;
		}

		public void setListParameter(final List list) {
			this.parameterValueList = list;
			this.fireTableDataChanged();
		}

		@Override
		public boolean isCellEditable(final int r, final int c) {
			return c == 2;
		}

		public void setValueAtCustom(final Object aValue, final int r, final int c) throws Exception {
			if (r >= this.parameterValueList.size()) {
				return;
			}
			if (c != 2) {
				return;
			}

			final String s = this.getValueAt(r, 0).toString().trim();

			int ind = -1;
			for (int i = 0, a = this.cols.length; i < a; i++) {
				if (this.cols[i].equals(s)) {
					ind = i;
					break;
				}
			}

			if (ind != -1) {
				if (ConditionsTableModel.NUMBER == this.types[ind]) {
					String sValue = (String) aValue;
					if (sValue.equals("")) {
						return;
					}

					final Operator op = (Operator) this.getValueAt(r, 1);

					if (op.toString().equals(BasicOperator.LIKE) || op.toString().equals(BasicOperator.NOT_LIKE)) {
						sValue = sValue.replace('?', '0');
						sValue = sValue.replace('*', '0');
					}

					try {
						final double current = Double.parseDouble(sValue);
						QueryBuilder.logger.trace("Value is right : " + current);
					} catch (final Exception e) {
						throw new Exception("M_QueryBuilderErrorInsercionEsperaInt", e);
					}
					((ParameterField) ((Expression) this.parameterValueList.get(r)).getRightOperand()).setValue(aValue);
				} else {
					((ParameterField) ((Expression) this.parameterValueList.get(r)).getRightOperand()).setValue(aValue);
				}
			} else {
				((ParameterField) ((Expression) this.parameterValueList.get(r)).getRightOperand()).setValue(aValue);
			}
		}

	}

	protected static class pvTable extends JTable {

		private final TextEditor textEditor = new TextEditor();

		private ResourceBundle bundle = null;

		private String[] cols = null;

		private int[] types = null;

		private CustomCellEditor customCellEditor = null;

		public pvTable(final ResourceBundle bundle, final java.util.List l, final String[] cols, final int[] types) {
			this.bundle = bundle;
			this.cols = cols;
			this.types = types;
			this.setModel(new pvTableModel(bundle, l, cols, types));
		}

		public void setColsAndTypes(final String[] cols, final int[] types) {
			final TableModel model = this.getModel();
			if (model instanceof pvTableModel) {
				((pvTableModel) model).setColsAndTypes(cols, types);
			}
		}

		@Override
		public TableCellEditor getCellEditor(final int row, final int c) {
			if ((c == 0) || (c == 1) || (c == 2)) {
				final String sName = ((BasicField) this.getModel().getValueAt(row, 0)).toString().trim();
				int ind = -1;
				for (int i = 0, a = this.cols.length; i < a; i++) {
					if (this.cols[i].equals(sName)) {
						ind = i;
						break;
					}
				}
				if (ind != -1) {
					final int tipo = this.types[ind];
					if (tipo == ConditionsTableModel.BOOLEAN) {
						final Vector v = new Vector();
						v.add("0");
						v.add("1");
						final JComboBox jc = new JComboBox(v);
						this.customCellEditor = new CustomCellEditor(jc);
						return this.customCellEditor;
					}
				}
				return this.textEditor;
			} else {
				return null;
			}
		}

		@Override
		public void setValueAt(final Object aValue, final int row, final int column) {
			try {
				((pvTableModel) this.getModel()).setValueAtCustom(aValue, row, this.convertColumnIndexToModel(column));
			} catch (final Exception e) {
				QueryBuilder.logger.error(null, e);
				super.setValueAt(null, row, column);
				if (SwingUtilities.getWindowAncestor(this) instanceof Frame) {
					MessageDialog.showMessage((Frame) SwingUtilities.getWindowAncestor(this), e.getMessage(),
							JOptionPane.OK_OPTION, this.bundle);
				} else {
					MessageDialog.showMessage((Dialog) SwingUtilities.getWindowAncestor(this), e.getMessage(),
							JOptionPane.OK_OPTION, this.bundle);
				}
			}

		}

		public void setListParameter(final List l) {
			final TableModel model = this.getModel();
			if (model instanceof pvTableModel) {
				final pvTableModel tModel = (pvTableModel) model;
				tModel.setListParameter(l);
			}
		}

	}

	protected static Map getColumnTypes(final String e, final ResourceBundle bu) {

		final Map h = new Hashtable();
		try {
			final EntityReferenceLocator b = ApplicationManager.getApplication().getReferenceLocator();
			final Object entity = b.getEntityReference(e);

			if ((e != null) && (entity instanceof AdvancedQueryEntity)) {

				final AdvancedQueryEntity eAv = (AdvancedQueryEntity) entity;
				final Map m = eAv.getColumnListForAvancedQuery(b.getSessionId());
				final ArrayList colum = new ArrayList();
				final ArrayList tips = new ArrayList();
				final ArrayList columaux = new ArrayList();
				final Set setKeys = m.keySet();
				final Iterator it = setKeys.iterator();

				while (it.hasNext()) {
					final Object c = it.next();
					columaux.add(c);
					colum.add(c);
					tips.add(m.get(c));
				}

				Collections.sort(columaux, new ColumnsComparator(bu));

				// Values to change
				final String[] c = new String[colum.size()];
				final int[] t = new int[tips.size()];

				for (int i = 0, j = tips.size(); i < j; i++) {
					c[i] = (String) columaux.get(i);
					final int a = colum.indexOf(columaux.get(i));
					t[i] = QueryBuilder.getTypeCol((String) tips.get(a));
				}

				h.put("cols", c);
				h.put("types", t);
			}

		} catch (final Exception ex) {
			QueryBuilder.logger.error(null, ex);
		}

		return h;
	}

	protected static class ParameterValuesTable extends EJDialog implements ActionListener {

		private ResourceBundle bundle = null;

		private java.util.List parameterList = null;

		private JButton bAccept = null;

		private JButton bCancel = null;

		private JTable table = null;

		private final String[] cols;

		private final int[] types;

		private JTable t;

		boolean allOk = true;

		public boolean isEmptyParameterList() {
			return this.parameterList.isEmpty();
		}

		protected boolean getAllOk() {
			return this.allOk;
		}

		public ParameterValuesTable(final Frame o, final JTable table, final ResourceBundle bundle, final String[] cols, final int[] types) {
			super(o, ApplicationManager.getTranslation("QueryBuilderParameterValuesTable", bundle), true);
			this.bundle = bundle;
			// this.parameterList = parameterList;
			this.table = table;
			this.cols = cols;
			this.types = types;
			this.init();
		}

		public ParameterValuesTable(final Frame o, final Expression e, final ResourceBundle bundle, final String[] cols, final int[] types) {
			super(o, ApplicationManager.getTranslation("QueryBuilderParameterValuesTable", bundle), true);
			this.bundle = bundle;
			// this.parameterList = parameterList;
			this.table = new EJTable(bundle);
			this.cols = cols;
			this.types = types;
			((EJTable) this.table).addInitExpression(e);
			this.init();
		}

		public ParameterValuesTable(final Dialog o, final JTable table, final ResourceBundle bundle, final String[] cols, final int[] types) {
			super(o, ApplicationManager.getTranslation("QueryBuilderParameterValuesTable", bundle), true);
			this.bundle = bundle;
			this.table = table;
			// this.parameterList = parameterList;
			this.cols = cols;
			this.types = types;
			this.init();
		}

		public ParameterValuesTable(final Dialog o, final Expression e, final ResourceBundle bundle, final String[] cols, final int[] types) {
			super(o, ApplicationManager.getTranslation("QueryBuilderParameterValuesTable", bundle), true);
			this.bundle = bundle;
			this.table = new EJTable(bundle);
			((EJTable) this.table).addInitExpression(e);
			// this.parameterList = parameterList;
			this.cols = cols;
			this.types = types;
			this.init();
		}

		@Override
		public void actionPerformed(final ActionEvent e) {

			boolean ok = true;
			this.allOk = true;

			if (e.getActionCommand().equals("accept")) {
				for (int i = 0, a = this.parameterList.size(); i < a; i++) {
					if (((ParameterField) ((Expression) this.parameterList.get(i)).getRightOperand())
							.getValue() == null) {
						ok = false;
						this.allOk = false;
						break;
					}
				}

				if (!ok) {
					if (MessageDialog.showQuestionMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
							"M_QueryBuilderParameterValuesCamposNulos", this.bundle)) {
						this.setVisible(false);
					}
				} else {
					this.setVisible(false);
				}
			} else {
				this.allOk = false;
				this.setVisible(false);
			}
		}

		public void init() {
			final java.util.List le = ((ConditionsTableModel) this.table.getModel()).getExpressionsList();
			this.parameterList = new ArrayList();

			for (int i = 0, a = le.size(); i < a; i++) {
				if (((Expression) le.get(i)).getRightOperand() instanceof ParameterField) {
					this.parameterList.add(le.get(i));
				}
			}

			this.t = new pvTable(this.bundle, this.parameterList, this.cols, this.types) {

				@Override
				public Dimension getPreferredScrollableViewportSize() {
					final Dimension d = super.getPreferredScrollableViewportSize();
					d.height = this.getRowHeight() * 12;
					return d;
				}
			};

			this.buildView();
		}

		private void buildView() {
			this.bAccept = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OK));
			this.bCancel = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.CANCEL));
			this.bAccept.setToolTipText(ApplicationManager.getTranslation("QueryBuilderAceptar", this.bundle));
			this.bCancel.setToolTipText(ApplicationManager.getTranslation("QueryBuilderCancelCons", this.bundle));
			this.bAccept.setText(ApplicationManager.getTranslation("QueryBuilderAceptar", this.bundle));
			this.bCancel.setText(ApplicationManager.getTranslation("QueryBuilderCancelCons", this.bundle));

			this.getContentPane().setLayout(new GridBagLayout());
			final JPanel jbButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			jbButtonsPanel.add(this.bAccept);
			jbButtonsPanel.add(this.bCancel);
			this.bAccept.setActionCommand("application.accept");
			this.bAccept.addActionListener(this);

			this.bCancel.setActionCommand("application.cancel");
			this.bCancel.addActionListener(this);

			this.getContentPane()
			.add(new JLabel(ApplicationManager.getTranslation("QueryBuilderParameterValuesTable", this.bundle)),
					new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));

			this.getContentPane()
			.add(new JScrollPane(this.t),
					new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));

			this.getContentPane()
			.add(jbButtonsPanel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		}

	}

	protected static class EJTable extends ETable {

		protected ComboEditor colEditor = new ComboEditor(null);

		protected ComboEditor valueEditor = new ComboEditor(true, null);

		protected ComboEditor operationsEditor = new ComboEditor(null);

		protected TextEditor textEditor = new TextEditor();

		protected java.util.List columns = null;

		protected int[] types = new int[0];

		// protected ExpressionRenderer eR = new ExpressionRenderer();

		protected ResourceBundle bundle = null;

		public EJTable() {

		}

		public EJTable(final ResourceBundle bundle) {
			this.setModel(new ConditionsTableModel(bundle));
			this.colEditor = new ComboEditor(bundle);
			this.valueEditor = new ComboEditor(true, bundle);
			this.operationsEditor = new ComboEditor(bundle);
			this.bundle = bundle;
		}

		public void setColumns(final Object[] cols, final int[] types) {
			boolean alguno = false;
			for (int i = 0, a = types.length; i < a; i++) {
				if (QueryBuilder.isTextType(types[i])) {
					alguno = true;
					break;
				}
			}

			Object[] colsaux = null;
			if (alguno) {
				colsaux = cols;
			} else {
				colsaux = new Object[cols.length - 1];
				for (int i = 0, a = cols.length - 1; i < a; i++) {
					colsaux[i] = cols[i];
				}
			}
			this.colEditor.setItems(colsaux);
			this.columns = java.util.Arrays.asList(cols);
			this.setTypes(types);
			((ConditionsTableModel) this.getModel()).setColumnTypes(cols, types);
		}

		public void addExpression() {
			((ConditionsTableModel) this.getModel()).addExpression();
		}

		public void addInitExpression(final Expression e) {
			this.removeAllExpressions();
			((ConditionsTableModel) this.getModel()).addInitExpression(e);
		}

		public void addExpression(final Expression e) {
			((ConditionsTableModel) this.getModel()).addExpression(e);
		}

		protected void setTypes(final int[] types) {
			this.types = types;
		}

		public void setOperators(final Object[] ops) {
			this.operationsEditor.setItems(ops);
		}

		public void clearExpressionValues() {
			((ConditionsTableModel) this.getModel()).clearExpressionValues();
		}

		@Override
		public TableCellEditor getCellEditor(final int row, final int c) {
			if (c == 0) {
				return this.colEditor;
			} else if (c == 1) {
				final Object v = this.getValueAt(row, 0);
				if ((v == null) || (!(v instanceof BasicField))) {
					return null;
				}
				final int i = this.columns.indexOf(((BasicField) v).toString());
				if ((i >= 0) && (i < this.types.length)) {
					this.operationsEditor
					.setItems(((ConditionsTableModel) this.getModel()).getTypeOperators(this.types[i]));
				}
				return this.operationsEditor;
			} else if (c == 2) {
				if (this.getValueAt(row, 1).toString().equals(ContainsOperator.CONTAINS_OP.toString())
						|| this.getValueAt(row, 1)
						.toString()
						.equals(ContainsOperator.NOT_CONTAINS_OP.toString())) {
					// return textEditor;
					this.valueEditor = new ComboEditor(true, this.bundle);
					final Object[] t = new Object[1];
					t[0] = new ParameterField();
					this.valueEditor.setItems(t);
					return this.valueEditor;
				}

				this.valueEditor = new ComboEditor(true, this.bundle);
				final String s = ((BasicField) this.getValueAt(row, 0)).toString();
				final int t = this.types[this.columns.indexOf(((BasicField) this.getValueAt(row, 0)).toString())];

				final ArrayList l = new ArrayList();
				if (t == ConditionsTableModel.BOOLEAN) {
					this.valueEditor = new ComboEditor(false, this.bundle);
					l.add("0");
					l.add("1");
				}

				for (int i = 0, a = this.columns.size(); i < a; i++) {
					if (((String) this.columns.get(i)).equals(s)) {
						continue;
					}
					if (!(BasicOperator.LIKE.equals(this.getValueAt(row, 1).toString()))
							|| (!BasicOperator.NOT_LIKE.equals(this.getValueAt(row, 1).toString()))) {
						if (t != this.types[i]) {
							continue;
						}
					}
					l.add(this.columns.get(i));
				}

				final Object[] bt = new Object[l.size() + 1];
				for (int i = 0, a = l.size(); i < a; i++) {
					bt[i] = new BasicField((String) l.get(i));
				}
				bt[l.size()] = new ParameterField();
				this.valueEditor.setItems(bt);
				return this.valueEditor;
			} else {
				return null;
			}
		}

		@Override
		public TableCellRenderer getCellRenderer(final int r, final int c) {
			if (c == 0) {
				return new CustomTableCellRenderer(this.bundle);
			}
			if (c == 2) {
				return new CustomTableCellRenderer(this.bundle);
			}
			if (c == 3) {
				return new CustomTableCellRendererExpression(this.bundle);
			}

			return super.getCellRenderer(r, c);
		}

		public void orBetweenRows(final int[] r) {
			((ConditionsTableModel) this.getModel()).orBetweenRows(r);
		}

		public void orNotBetweenRows(final int[] r) {
			((ConditionsTableModel) this.getModel()).orNotBetweenRows(r);
		}

		public void doAndBetweenRows(final int[] r) {
			((ConditionsTableModel) this.getModel()).doAndBetweenRows(r);
		}

		public void doNotBetweenRows(final int[] r) {
			((ConditionsTableModel) this.getModel()).doAndNotBetweenRows(r);
		}

		public void split(final int r) {
			((ConditionsTableModel) this.getModel()).split(r);
		}

		public void removeRows(final int[] r) {
			((ConditionsTableModel) this.getModel()).removeRows(r);
		}

		public void removeAllExpressions() {
			((ConditionsTableModel) this.getModel()).removeAllExpressions();
		}

		@Override
		public Expression getExpression(final int r) {
			return ((ConditionsTableModel) this.getModel()).getExpression(r);
		}

		public java.util.List getExpressionsList() {
			return ((ConditionsTableModel) this.getModel()).getExpressionsList();
		}

		public boolean expressionOK(final int r) {
			return ((ConditionsTableModel) this.getModel()).isExpressionOK(r);
		}

		public boolean expressionOKWithoutParameters(final int r) {
			return ((ConditionsTableModel) this.getModel()).isExpressionOKWithoutParameters(r);
		}

		public boolean expressionOKWithoutParameters(final Expression e) {
			return ((ConditionsTableModel) this.getModel()).isExpressionOKWithoutParameters(e);
		}

		public boolean needsExpressionParameters(final int r) {
			return ((ConditionsTableModel) this.getModel()).needsExpressionParameters(r);
		}

		// public boolean expressionNeedParameters(Expression e){
		// return
		// ((ConditionsTableModel)this.getModel()).expressionNeedParameters(e);
		// }

		@Override
		public boolean expressionOK(final Expression e) {
			return ((ConditionsTableModel) this.getModel()).isExpressionOK(e);
		}

		@Override
		public void setValueAt(final Object aValue, final int row, final int column) {
			try {
				((ConditionsTableModel) this.getModel()).setValueAtCustom(aValue, row, column);
			} catch (final Exception e) {
				QueryBuilder.logger.error(null, e);
				super.setValueAt(null, row, column);
				if (SwingUtilities.getWindowAncestor(this) instanceof Frame) {
					MessageDialog.showMessage((Frame) SwingUtilities.getWindowAncestor(this), e.getMessage(),
							JOptionPane.OK_OPTION, this.bundle);
				} else {
					MessageDialog.showMessage((Dialog) SwingUtilities.getWindowAncestor(this), e.getMessage(),
							JOptionPane.OK_OPTION, this.bundle);
				}
			}
		}

	};

	protected JButton bOKCons = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OK));

	protected JButton bCancelCons = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.CANCEL));

	protected JButton bStore = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.SAVE_FILE));

	protected JButton bPreview = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.PREVIEW));

	// Ponemos otro icono para distingir
	protected JButton bSave = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.SAVE_TABLE_FILTER));

	protected JButton bLoad = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OPEN));

	protected JButton bCols = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.CONF_VISIBLE_COLS));

	protected JButton bClear = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.RECYCLER));

	protected JButton bNew = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.NEW_GIF));

	protected JButton bDelete = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.DELETE_GIF));

	protected JButton bOR = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OR));

	protected JButton bAnd = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.AND));

	protected JButton bModif = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.MODIF));

	protected JButton bValues = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.OPTIONS));

	protected JButton bClearValues = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.DOCUMENT_DELETE));

	protected JButton bSplit = new com.ontimize.report.ReportDesignerButton(
			ImageManager.getIcon(ImageManager.LINK_DELETE));

	protected JButton bAndN = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.AND_NOT));

	protected JButton bORN = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OR_NOT));

	protected JButton bHelp = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.HELP));

	protected JButton bHelp2 = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.GEAR));

	protected JTextArea text = new JTextArea();

	protected JPanel pButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));

	protected JPanel pOKCancel = new JPanel(new FlowLayout(FlowLayout.CENTER));

	protected boolean cancelPressed = false;

	// protected JPanel pHelp = new JPanel();
	protected JPanel panelQuery = new JPanel();

	protected EJTable table = null;

	protected JScrollPane js = null;

	protected JComboBox comboEntities;

	protected String[] cols;

	protected boolean[] queryColumns;

	protected int[] types;

	protected ResourceBundle bundle = null;

	protected SimpleExpressionsDialog expressionDialog = null;

	protected EntityReferenceLocator locator = null;

	protected String entity = null;

	protected Vector					entityList			= null;

	protected boolean okCancel = false;

	protected boolean showColumns = false;

	// private static final String DEFAULT_DIRECTORY =
	// System.getProperty("user.home");

	// private static final String SUBDIR_NAME = ".reports" + File.separator +
	// "entities";

	// private static final String EXTENSION = ".conf";

	public String getEntity() {
		return this.entity;
	}

	public String[] getCols() {
		return this.cols;
	}

	public boolean[] getQueryColumns() {
		return this.queryColumns;
	}

	public QueryBuilder(final Vector entityList, final ResourceBundle bundle, final EntityReferenceLocator locator,
			final boolean okCancel,
			final QueryExpression initQuery, final boolean mostrarCols,
			final String[] tCols) {
		this.initWithQueryExpression(entityList, bundle, locator, okCancel, initQuery, mostrarCols, tCols);
		this.initMainWindow();
		if (this.table.getRowCount() == 0) {
			this.table.addExpression();
		}
	}

	private void initWithQueryExpression(final Vector entityList, final ResourceBundle bundle,
			final EntityReferenceLocator locator,
			final boolean okCancel, final QueryExpression initQuery, final boolean mostrarCols,
			final String[] tCols) {
		if (initQuery == null) {
			this.entityList = entityList;
		}
		this.locator = locator;
		this.bundle = bundle;
		this.okCancel = okCancel;
		this.showColumns = mostrarCols;

		if (this.table == null) {
			this.table = new EJTable(bundle) {

				@Override
				public Dimension getPreferredScrollableViewportSize() {
					final Dimension d = super.getPreferredScrollableViewportSize();
					d.height = this.getRowHeight() * 10;
					return d;
				}
			};
		}

		if (initQuery != null) {
			if (tCols == null) {
				this.initVariables(initQuery);
			} else {
				this.initVariables(initQuery, tCols);
			}
		} else if ((entityList != null) && !entityList.isEmpty()) {
			this.initVariables((String) CollectionTools.firstElement(entityList));
		}

		this.installTableListener();
		this.js = new JScrollPane();

		if (this.table.getRowCount() == 0) {
			this.table.addExpression();
		}
	}

	public QueryBuilder(final ResourceBundle bundle, final String[] lCols, final String[] tCols, final Expression initExpression) {
		this.entityList = null;
		this.entity = null;
		this.bundle = bundle;
		this.okCancel = true;
		this.showColumns = true;

		this.table = new EJTable(bundle) {

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				final Dimension d = super.getPreferredScrollableViewportSize();
				d.height = this.getRowHeight() * 10;
				return d;
			}
		};

		final String[] auxCols = new String[lCols.length + 1];
		this.queryColumns = new boolean[lCols.length + 1];

		for (int i = 0, a = lCols.length; i < a; i++) {
			auxCols[i] = new String(lCols[i]);
			this.queryColumns[i] = true;
		}

		auxCols[lCols.length] = ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN;
		this.queryColumns[lCols.length] = false;
		this.cols = auxCols;
		final java.util.List l = new ArrayList();
		for (int i = 0, a = tCols.length; i < a; i++) {
			l.add(tCols[i]);
		}
		this.types = QueryBuilder.convertType(l);
		this.setColumns(this.cols, this.types);

		if (initExpression != null) {
			this.table.addInitExpression(initExpression);
			this.text.setText(ContainsSQLConditionValuesProcessorHelper
					.renderQueryConditionsExpressBundle(initExpression, bundle));
		}

		this.bPreview.setEnabled(false);
		// this.bStore.setEnabled(false);

		this.initMainWindow();

		if (this.table.getRowCount() == 0) {
			this.table.addExpression();
		}
	}

	private void setColumnTypes(final String[] cols, final String entityName, final boolean option) {

		try {
			final Object entity = this.locator.getEntityReference(entityName);

			if (entity instanceof AdvancedQueryEntity) {
				if (option) {
					this.table.removeAllExpressions();
				}

				final AdvancedQueryEntity eAv = (AdvancedQueryEntity) entity;
				final Map m = eAv.getColumnListForAvancedQuery(this.locator.getSessionId());

				final ArrayList tips = new ArrayList();
				final ArrayList colsTips = new ArrayList();
				final ArrayList ordenTips = new ArrayList();

				final Set setKeys = m.keySet();
				final Iterator it = setKeys.iterator();
				while (it.hasNext()) {
					final Object c = it.next();
					colsTips.add(c);
					tips.add(m.get(c));

				}

				for (int i = 0, a = cols.length; i < a; i++) {
					if (!cols[i].equals(ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN)) {
						final int k = colsTips.indexOf(cols[i]);
						// logger.debug(cols[i]+" k "+k);
						if (k >= 0) {
							ordenTips.add(tips.get(k));
						} else {
							ordenTips.add("String");
						}
					}
				}

				// Values to change
				this.types = new int[tips.size() + 1];
				this.types = QueryBuilder.convertType(ordenTips);
				this.setColumns(cols, this.types);
			}
		} catch (final Exception ex) {
			QueryBuilder.logger.error(null, ex);
		}
	}

	private void initVariables(final QueryExpression initExpression, final String[] tCols) {

		final Vector v = new Vector();
		v.add(initExpression.getEntity());
		this.entityList = v;
		this.entity = initExpression.getEntity();

		final java.util.List l = initExpression.getCols();
		final java.util.List l2 = initExpression.getColumnToQuery();

		if (l != null) {
			this.cols = new String[l.size() + 1];
			this.queryColumns = new boolean[l.size() + 1];

			for (int i = 0, a = l.size(); i < a; i++) {
				this.cols[i] = new String((String) l.get(i));
				this.queryColumns[i] = ((Boolean) l2.get(i)).booleanValue();
			}
			this.cols[l.size()] = ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN;
			this.queryColumns[l.size()] = false;
		}

		this.types = new int[tCols.length + 1];
		for (int i = 0, a = tCols.length; i < a; i++) {
			this.types[i] = QueryBuilder.getTypeCol(tCols[i]);
		}
		this.types[tCols.length] = ConditionsTableModel.ANY;

		if (QueryBuilder.DEBUG) {
			QueryBuilder.logger.debug("Init Expression  y tCols");
			QueryBuilder.logger.debug("Column  Types");
			for (int i = 0, a = this.cols.length; i < a; i++) {
				QueryBuilder.logger.debug(this.cols[i] + " = " + QueryBuilder.getStringType(this.types[i]));
			}
		}

		this.setColumns(this.cols, this.types);
	}

	private void initVariables(final QueryExpression initExpression) {
		final Vector v = new Vector();
		v.add(initExpression.getEntity());
		this.entityList = v;
		this.entity = initExpression.getEntity();

		final java.util.List l = initExpression.getCols();
		final java.util.List lb = initExpression.getCols();
		final java.util.List l2 = initExpression.getColumnToQuery();

		if (l != null) {
			this.cols = new String[l.size() + 1];
			this.queryColumns = new boolean[l.size() + 1];

			Collections.sort(lb, new ColumnsComparator(this.bundle));

			for (int i = 0, a = l.size(); i < a; i++) {
				this.cols[i] = new String((String) lb.get(i));
				final int b = l.indexOf(lb.get(i));
				this.queryColumns[i] = ((Boolean) l2.get(b)).booleanValue();
			}
			this.cols[l.size()] = ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN;
			this.queryColumns[l.size()] = false;
		}

		this.setColumnTypes(this.cols, this.entity, true);

		if (QueryBuilder.DEBUG) {
			QueryBuilder.logger.debug("Init Expression ");
			QueryBuilder.logger.debug("Column Types");
			for (int i = 0, a = this.cols.length; i < a; i++) {
				QueryBuilder.logger.debug(this.cols[i] + " = " + QueryBuilder.getStringType(this.types[i]));
			}
		}

		if (initExpression.getExpression() != null) {
			this.table.addInitExpression(initExpression.getExpression());
			this.text.setText(ContainsSQLConditionValuesProcessorHelper
					.renderQueryConditionsExpressBundle(initExpression.getExpression(), this.bundle));
		}
	}

	protected int[] getTypes() {
		return this.types;
	}

	private void initVariables(final String entityName) {

		try {
			final Entity entity = this.locator.getEntityReference(entityName);

			if ((this.entity == null) || ((this.entity != null) && !this.entity.equals(entityName))) {
				if (entity instanceof AdvancedQueryEntity) {
					this.table.removeAllExpressions();

					this.bPreview.setEnabled(true);
					this.entity = entityName;

					final AdvancedQueryEntity eAv = (AdvancedQueryEntity) entity;
					final Map m = eAv.getColumnListForAvancedQuery(this.locator.getSessionId());
					final ArrayList colum = new ArrayList();
					final ArrayList tips = new ArrayList();
					final ArrayList auxCOlumn = new ArrayList();
					final Set setKeys = m.keySet();
					final Iterator it = setKeys.iterator();

					while (it.hasNext()) {
						final Object c = it.next();
						auxCOlumn.add(c);
						colum.add(c);
						tips.add(m.get(c));
					}

					Collections.sort(auxCOlumn, new ColumnsComparator(this.bundle));

					// Values to change
					this.cols = new String[colum.size() + 1];
					this.types = new int[tips.size() + 1];
					this.queryColumns = new boolean[tips.size() + 1];

					for (int i = 0, j = tips.size(); i < j; i++) {
						this.cols[i] = (String) auxCOlumn.get(i);
						final int a = colum.indexOf(auxCOlumn.get(i));
						this.types[i] = QueryBuilder.getTypeCol((String) tips.get(a));
						this.queryColumns[i] = true;
					}

					this.cols[colum.size()] = ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN;
					this.types[colum.size()] = ConditionsTableModel.ANY;
					this.queryColumns[colum.size()] = false;

					if (QueryBuilder.DEBUG) {
						QueryBuilder.logger.debug("Entity " + entityName);
						QueryBuilder.logger.debug("Columns    Types");
						for (int i = 0, a = this.cols.length; i < a; i++) {
							QueryBuilder.logger.debug(this.cols[i] + " = " + QueryBuilder.getStringType(this.types[i]));
						}
					}

					this.setColumns(this.cols, this.types);
				}
			}
		} catch (final Exception e) {
			QueryBuilder.logger.error(null, e);
		}
	}

	private void initMainWindow() {

		final JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		this.text.setLineWrap(true);
		this.text.setWrapStyleWord(true);
		this.text.setEditable(false);
		this.text.setRows(4);
		this.text.setColumns(50);

		this.setLayout(new GridBagLayout());

		this.bOKCons.setToolTipText(ApplicationManager.getTranslation("QueryBuilderOKCons", this.bundle));
		this.bCancelCons.setToolTipText(ApplicationManager.getTranslation("QueryBuilderCancelCons", this.bundle));

		this.bSave.setToolTipText(ApplicationManager.getTranslation("QueryBuilderSalvar", this.bundle));
		this.bCols.setToolTipText(ApplicationManager.getTranslation("QueryBuilderColumns", this.bundle));
		this.bClear.setToolTipText(ApplicationManager.getTranslation("QueryBuilderBorrarTodo", this.bundle));
		this.bLoad.setToolTipText(ApplicationManager.getTranslation("QueryBuilderCargar", this.bundle));
		this.bNew.setToolTipText(ApplicationManager.getTranslation("QueryBuilderNuevaRestric", this.bundle));
		this.bDelete.setToolTipText(ApplicationManager.getTranslation("QueryBuilderBorrarRestric", this.bundle));
		this.bOR.setToolTipText(ApplicationManager.getTranslation("QueryBuilderOR", this.bundle));
		this.bAnd.setToolTipText(ApplicationManager.getTranslation("QueryBuilderAND", this.bundle));
		this.bSplit.setToolTipText(ApplicationManager.getTranslation("QueryBuilderSPLIT", this.bundle));
		this.bModif.setToolTipText(ApplicationManager.getTranslation("QueryBuilderModifRestric", this.bundle));
		this.bValues.setToolTipText(ApplicationManager.getTranslation("QueryBuilderValues", this.bundle));
		this.bClearValues.setToolTipText(ApplicationManager.getTranslation("QueryBuilderClearValues", this.bundle));
		this.bStore.setToolTipText(ApplicationManager.getTranslation("QueryBuilderSalvarStore", this.bundle));
		this.bPreview.setToolTipText(ApplicationManager.getTranslation("QueryBuilderPreview", this.bundle));
		this.bAndN.setToolTipText(ApplicationManager.getTranslation("QueryBuilderANDNOT", this.bundle));
		this.bORN.setToolTipText(ApplicationManager.getTranslation("QueryBuilderORNOT", this.bundle));
		this.bHelp.setToolTipText(ApplicationManager.getTranslation("QueryBuilderHELP", this.bundle));
		this.bHelp2.setToolTipText(ApplicationManager.getTranslation("QueryBuilderConfig", this.bundle));

		toolbar.add(this.bStore);
		toolbar.add(this.bClear);
		toolbar.addSeparator();

		if (this.basicSave) {
			this.pButtons.add(this.bSave);// Si se ponen cambiar estos
			this.pButtons.add(this.bLoad);
		}

		if (!this.okCancel || (this.okCancel && (this.entityList != null) && (this.entityList.size() > 1))) {
			this.comboEntities = new JComboBox(this.entityList);
			toolbar.add(this.comboEntities);

			this.comboEntities.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (((JComboBox) e.getSource()).getSelectedItem() != null) {
						QueryBuilder.this.initVariables((String) ((JComboBox) e.getSource()).getSelectedItem());
						if (QueryBuilder.this.table.getRowCount() == 0) {
							QueryBuilder.this.table.addExpression();
						}
					}
				}
			});
		}

		toolbar.add(this.bPreview);

		if (!this.okCancel) {
			toolbar.add(this.bCols);
		} else if (this.showColumns) {
			toolbar.add(this.bCols);
		}
		toolbar.add(this.bValues);
		toolbar.add(this.bClearValues);
		toolbar.addSeparator();

		toolbar.add(this.bNew);
		toolbar.add(this.bDelete);
		toolbar.add(this.bOR);
		toolbar.add(this.bAnd);
		toolbar.add(this.bORN);
		toolbar.add(this.bAndN);
		toolbar.add(this.bSplit);
		toolbar.add(this.bModif);

		toolbar.addSeparator();
		toolbar.add(this.bHelp);
		toolbar.add(this.bHelp2);

		if (this.okCancel) {
			this.bOKCons.setText(ApplicationManager.getTranslation("QueryBuilderRealizarConsA", this.bundle));
			this.pOKCancel.add(this.bOKCons);
			this.bCancelCons.setText(ApplicationManager.getTranslation("QueryBuilderCancelConsA", this.bundle));
			this.pOKCancel.add(this.bCancelCons);
		}

		this.bHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				int t = -1;
				if (QueryBuilder.this.entityList != null) {
					t = QueryBuilder.this.entityList.size();
				}
				QueryBuilderHelp.show((Component) e.getSource(), QueryBuilder.this.bundle, QueryBuilder.this.okCancel,
						QueryBuilder.this.showColumns, t);
			}
		});

		this.bHelp2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilderShowColsAndTypes.show((Component) e.getSource(), QueryBuilder.this.bundle,
						QueryBuilder.this.cols, QueryBuilder.this.types, QueryBuilder.this.entity);
			}
		});

		this.panelQuery.setLayout(new BorderLayout());
		this.panelQuery.add(new JLabel(ApplicationManager.getTranslation("QueryBuilderConsultaTotal", this.bundle)),
				BorderLayout.NORTH);
		this.panelQuery.add(new JScrollPane(this.text));

		this.add(toolbar, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE,
				new Insets(10, 2, 2, 2), 0, 0));

		this.add(this.panelQuery, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		this.js = new JScrollPane(this.table);
		this.add(this.js, new GridBagConstraints(0, 2, 1, 1, 0, 0.01, GridBagConstraints.CENTER,
				GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		if (this.okCancel) {
			this.add(this.pOKCancel, new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
		}

		this.installTableListener();
		this.installButtonListeners();
	}

	protected static class ExpTable extends EJTable {

		private String[] cols;

		protected ComboEditor colEditor = new ComboEditor(null);

		protected ComboEditor operationsEditor = new ComboEditor(null);

		protected TextEditor textEditor = new TextEditor();

		protected java.util.List columns = null;

		protected int[] types = new int[0];

		protected ResourceBundle bundle = null;

		public ExpTable(final java.util.List expresiones, final String[] cols, final int[] types, final ResourceBundle bundle) {
			this.bundle = bundle;
			this.setModel(new ConditionsTableModel(bundle));
			this.setExpressions(expresiones);
			this.setCols(cols);
			this.setTypes(types);
			this.colEditor = new ComboEditor(bundle);
			this.operationsEditor = new ComboEditor(bundle);

			boolean alguno = false;
			for (int i = 0, a = types.length; i < a; i++) {
				if (QueryBuilder.isTextType(types[i])) {
					alguno = true;
					break;
				}
			}

			Object[] colsaux = null;
			if (alguno) {
				colsaux = cols;
			} else {
				colsaux = new Object[cols.length - 1];

				for (int i = 0, a = cols.length - 1; i < a; i++) {
					colsaux[i] = cols[i];
				}
			}
			this.colEditor.setItems(colsaux);

			// colEditor.setItems(cols);
			this.columns = java.util.Arrays.asList(cols);

			((ConditionsTableModel) this.getModel()).setColumnTypes(cols, types);
		}

		public void setExpressions(final java.util.List expresiones) {
			((ConditionsTableModel) this.getModel()).setExpressions(expresiones);
		}

		@Override
		public void setTypes(final int[] types) {
			this.types = types;
		}

		public void setCols(final String[] cols) {
			this.cols = cols;
		}

		@Override
		public TableCellEditor getCellEditor(final int row, final int c) {
			if (c == 0) {
				return this.colEditor;
			} else if (c == 1) {
				final Object v = this.getValueAt(row, 0);
				if ((v == null) || (!(v instanceof BasicField))) {
					return null;
				}
				final int i = this.columns.indexOf(((BasicField) v).toString());
				if ((i >= 0) && (i < this.types.length)) {
					this.operationsEditor
					.setItems(((ConditionsTableModel) this.getModel()).getTypeOperators(this.types[i]));
				}
				return this.operationsEditor;
			} else if (c == 2) {
				if (this.getValueAt(row, 1).toString().equals(ContainsOperator.CONTAINS_OP.toString())
						|| this.getValueAt(row, 1)
						.toString()
						.equals(ContainsOperator.NOT_CONTAINS_OP.toString())) {
					return this.textEditor;
				}

				this.valueEditor = new ComboEditor(true, this.bundle);
				final String s = ((BasicField) this.getValueAt(row, 0)).toString();
				final int t = this.types[this.columns.indexOf(((BasicField) this.getValueAt(row, 0)).toString())];

				final ArrayList l = new ArrayList();
				if (t == ConditionsTableModel.BOOLEAN) {
					this.valueEditor = new ComboEditor(false, this.bundle);
					l.add("0");
					l.add("1");
				}

				for (int i = 0, a = this.columns.size(); i < a; i++) {
					if (((String) this.columns.get(i)).equals(s)) {
						continue;
					}
					if (t != this.types[i]) {
						continue;
					}
					l.add(this.columns.get(i));
				}

				final BasicField[] bt = new BasicField[l.size()];
				for (int i = 0, a = l.size(); i < a; i++) {
					bt[i] = new BasicField((String) l.get(i));
				}

				this.valueEditor.setItems(bt);
				return this.valueEditor;
			} else {
				return null;
			}
		}

		@Override
		public TableCellRenderer getCellRenderer(final int r, final int c) {
			if (c == 0) {
				return new CustomTableCellRenderer(this.bundle);
			}
			if (c == 2) {
				return new CustomTableCellRenderer(this.bundle);
			}
			if (c == 3) {
				return new CustomTableCellRendererExpression(this.bundle);
			} else {
				return super.getCellRenderer(r, c);
			}
		}

		@Override
		public void setValueAt(final Object aValue, final int row, final int column) {
			try {
				((ConditionsTableModel) this.getModel()).setValueAtCustom(aValue, row, column);
			} catch (final Exception e) {
				QueryBuilder.logger.error(null, e);
				super.setValueAt(null, row, column);
				if (SwingUtilities.getWindowAncestor(this) instanceof Frame) {
					MessageDialog.showMessage((Frame) SwingUtilities.getWindowAncestor(this),
							"M_QueryBuilderErrorInsercionEsperaInt", JOptionPane.OK_OPTION, this.bundle);
				} else {
					MessageDialog.showMessage((Dialog) SwingUtilities.getWindowAncestor(this),
							"M_QueryBuilderErrorInsercionEsperaInt", JOptionPane.OK_OPTION, this.bundle);
				}
			}
		}

	}

	protected static class SimpleExpressionsDialog extends EJDialog implements ActionListener {

		JButton bAccept = null;

		ExpTable expressionTable = null;

		ResourceBundle bundle = null;

		java.util.List expressionList = null;

		public SimpleExpressionsDialog(final Frame o, final java.util.List l, final String[] cols, final int[] types, final JTable table,
				final ResourceBundle bundle) {
			super(o, ApplicationManager.getTranslation("QueryBuilderExpresionesSimplesTitle", bundle), true);
			this.init(l, cols, types, table, bundle);
		}

		public SimpleExpressionsDialog(final Dialog o, final java.util.List l, final String[] cols, final int[] types, final JTable table,
				final ResourceBundle bundle) {
			super(o, ApplicationManager.getTranslation("QueryBuilderExpresionesSimplesTitle", bundle), true);
			this.init(l, cols, types, table, bundle);
		}

		public boolean checkAllExpressions() {

			boolean ok = true;
			for (int i = 0, j = this.expressionList.size(); i < j; i++) {
				if (this.expressionTable != null) {
					if (!this.expressionTable.expressionOK((Expression) this.expressionList.get(i))) {
						ok = false;
						break;
					}
				}
			}
			return ok;
		}

		public void showQuestionDialog(final Object ex) {

			if (MessageDialog.showQuestionMessage(SwingUtilities.getWindowAncestor(this),
					"M_QueryBuilder_AlgunaResInvalida", this.bundle)) {
				this.setVisible(false);
			}
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (!this.checkAllExpressions()) {
				this.showQuestionDialog(e);
			} else {
				this.setVisible(false);
			}

		}

		public void init(final java.util.List l, final String[] cols, final int[] types, final JTable table, final ResourceBundle bundle) {
			this.expressionList = l;
			this.bundle = bundle;
			this.bAccept = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OK));
			this.bAccept.setToolTipText(ApplicationManager.getTranslation("QueryBuilderAccept", bundle));
			this.bAccept.setText(ApplicationManager.getTranslation("QueryBuilderAccept", bundle));

			this.expressionTable = new ExpTable(l, cols, types, bundle);

			this.getContentPane().setLayout(new GridBagLayout());

			final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			buttonsPanel.add(this.bAccept);

			this.bAccept.addActionListener(this);

			this.getContentPane()
			.add(new JLabel(ApplicationManager.getTranslation("QueryBuilderExpSimples", bundle)),
					new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));

			this.getContentPane()
			.add(new JScrollPane(this.expressionTable),
					new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH,
							new Insets(2, 2, 2, 2), 0, 0));

			this.getContentPane()
			.add(buttonsPanel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		}

	}

	protected static ParameterValuesTable pvt = null;

	protected static boolean showParameterValuesTable(final Component c, final ResourceBundle b, final JTable t, final String[] cols,
			final int[] types) {
		final ResourceBundle bundle = b;
		final JTable table = t;

		final Window w = SwingUtilities.getWindowAncestor(c);
		if ((QueryBuilder.pvt == null) || (QueryBuilder.pvt.getOwner() != w)) {
			if (QueryBuilder.pvt != null) {
				QueryBuilder.pvt.dispose();
			}
			if (w instanceof Frame) {
				QueryBuilder.pvt = new ParameterValuesTable((Frame) w, table, bundle, cols, types);
			} else {
				QueryBuilder.pvt = new ParameterValuesTable((Dialog) w, table, bundle, cols, types);
			}
			QueryBuilder.pvt.pack();
			ApplicationManager.center(QueryBuilder.pvt);
		}
		QueryBuilder.pvt.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (!QueryBuilder.pvt.isEmptyParameterList()) {
			QueryBuilder.pvt.setVisible(true);
			((ConditionsTableModel) table.getModel()).fireTableDataChanged();
		}
		return QueryBuilder.pvt.getAllOk();
	}

	protected static boolean showParameterValuesTable(final Component c, final ResourceBundle b, final Expression e, final String[] cols,
			final int[] types) {
		final ResourceBundle bundle = b;
		final JTable table = new EJTable(b);
		((ConditionsTableModel) table.getModel()).addExpression(e);

		Window w = SwingUtilities.getWindowAncestor(c);
		if (c instanceof Frame) {
			w = (Frame) c;
		}
		if ((QueryBuilder.pvt == null) || (QueryBuilder.pvt.getOwner() != w)) {
			if (QueryBuilder.pvt != null) {
				QueryBuilder.pvt.dispose();
			}
			if (w instanceof Frame) {
				QueryBuilder.pvt = new ParameterValuesTable((Frame) w, table, bundle, cols, types);
			} else {
				QueryBuilder.pvt = new ParameterValuesTable((Dialog) w, table, bundle, cols, types);
			}
			QueryBuilder.pvt.pack();
			ApplicationManager.center(QueryBuilder.pvt);
		}

		QueryBuilder.pvt.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (!QueryBuilder.pvt.isEmptyParameterList()) {
			QueryBuilder.pvt.setVisible(true);
			((ConditionsTableModel) table.getModel()).fireTableDataChanged();
		}
		return QueryBuilder.pvt.getAllOk();
	}

	public static boolean showParameterValues(final Component c, final ResourceBundle b, final Expression e, final String[] co, final int[] ty) {
		final ResourceBundle bundle = b;
		final JTable table = new EJTable(b);

		Window w = SwingUtilities.getWindowAncestor(c);
		if (c instanceof Frame) {
			w = (Frame) c;
		}
		if ((QueryBuilder.pvt == null) || (QueryBuilder.pvt.getOwner() != w)) {
			if (QueryBuilder.pvt != null) {
				QueryBuilder.pvt.dispose();
			}
			if (w instanceof Frame) {
				QueryBuilder.pvt = new ParameterValuesTable((Frame) w, table, bundle, co, ty);
			} else {
				QueryBuilder.pvt = new ParameterValuesTable((Dialog) w, table, bundle, co, ty);
			}
			QueryBuilder.pvt.pack();
			ApplicationManager.center(QueryBuilder.pvt);
		}

		QueryBuilder.pvt.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		if (!QueryBuilder.pvt.isEmptyParameterList()) {
			QueryBuilder.pvt.setVisible(true);
			((ConditionsTableModel) table.getModel()).fireTableDataChanged();
		}

		return QueryBuilder.pvt.getAllOk();
	}

	public static boolean showParameterValuesTable(final Component c, final ResourceBundle b, final Expression e, final String entity) {
		final Map ht = QueryBuilder.getColumnTypes(entity, b);

		final String[] co = (String[]) ht.get("cols");
		final int[] ty = (int[]) ht.get("types");
		if ((co == null) || (ty == null)) {
			return true;
		}
		return QueryBuilder.showParameterValuesTable(c, b, e, co, ty);
	}

	public static boolean showParameterValuesTable(final Component c, final ResourceBundle b, final QueryExpression qe) {

		final java.util.List l = qe.getCols();

		final String[] co = new String[l.size()];
		final int[] ty = new int[l.size()];

		for (int i = 0, a = l.size(); i < a; i++) {
			co[i] = new String((String) l.get(i));
			ty[i] = ConditionsTableModel.VARCHAR;
		}
		return QueryBuilder.showParameterValuesTable(c, b, qe.getExpression(), co, ty);
	}

	protected void simpleAll() {
		final Window w = SwingUtilities.getWindowAncestor(this);
		if (w instanceof Frame) {
			this.expressionDialog = new SimpleExpressionsDialog((Frame) w, this.table.getExpressionsList(), this.cols,
					this.types, this.table, this.bundle);
		} else {
			this.expressionDialog = new SimpleExpressionsDialog((Dialog) w, this.table.getExpressionsList(), this.cols,
					this.types, this.table, this.bundle);
		}

		this.expressionDialog.pack();

		ApplicationManager.center(this.expressionDialog);
		this.expressionDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.expressionDialog.setVisible(true);
		((ConditionsTableModel) this.table.getModel()).fireTableDataChanged();

	}

	protected void installButtonListeners() {

		installSaveButtonListener();

		installLoadButtonListener();

		installClearButtonListener();

		installColumnsButtonListener();

		installNewButtonListener();

		installDeleteButtonListener();

		installOrButtonListener();

		installOrNotButtonListener();

		installAndButtonListener();

		installAndNotButtonListener();

		installSplitButtonListener();

		installModifButtonListener();

		installValuesButtonListener();

		installClearValuesButtonListener();

		installStoreButtonListener();

		installPreviewButtonListener();

		installOKConsButtonListener();

		installCancelConsButtonListener();
	}

	protected void installCancelConsButtonListener() {
		this.bCancelCons.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
				QueryBuilder.this.table.removeAllExpressions();
				QueryBuilder.this.cancelPressed = true;
				w.setVisible(false);
			}
		});
	}

	protected void installOKConsButtonListener() {
		this.bOKCons.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());

				if (QueryBuilder.this.table.getRowCount() > 0) {
					final boolean ok = QueryBuilder.this.table.expressionOKWithoutParameters(0);

					if (!ok) {
						MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
								ApplicationManager.getTranslation("M_QueryBuilder_FiltroIncorrecto",
										QueryBuilder.this.bundle));
						return;
					} else {
						w.setVisible(false);
					}
				} else {
					w.setVisible(false);
				}
			}
		});
	}

	protected void installPreviewButtonListener() {
		this.bPreview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Expression query = QueryBuilder.this.table.getExpression(0);

				try {
					final Entity en = QueryBuilder.this.locator.getEntityReference(QueryBuilder.this.entity);
					final List attributes = new Vector();
					for (int i = 0; i < QueryBuilder.this.cols.length; i++) {
						if (QueryBuilder.this.queryColumns[i]) {
							attributes.add(QueryBuilder.this.cols[i]);
						}
					}

					final Map keysValues = new Hashtable();

					if (query != null) {

						final boolean needParameters = QueryBuilder.this.table.needsExpressionParameters(0);
						if (needParameters) {
							final Component c = (Component) e.getSource();
							if (c instanceof Frame) {
								MessageDialog.showMessage(
										(Frame) SwingUtilities.getWindowAncestor((Component) e.getSource()),
										"M_QueryBuilderQueryWithoutParameter",
										JOptionPane.WARNING_MESSAGE, QueryBuilder.this.bundle);
							} else {
								MessageDialog.showMessage(
										(Dialog) SwingUtilities.getWindowAncestor((Component) e.getSource()),
										"M_QueryBuilderQueryWithoutParameter",
										JOptionPane.WARNING_MESSAGE, QueryBuilder.this.bundle);
							}
						}

						if (needParameters && !QueryBuilder.showParameterValuesTable((Component) e.getSource(),
								QueryBuilder.this.bundle, QueryBuilder.this.table,
								QueryBuilder.this.cols, QueryBuilder.this.types)) {
							final Component c = (Component) e.getSource();
							if (SwingUtilities.getWindowAncestor(c) instanceof Frame) {
								MessageDialog.showMessage((Frame) SwingUtilities.getWindowAncestor(c),
										"M_QueryBuilderQueryWithoutParameter", JOptionPane.OK_OPTION,
										QueryBuilder.this.bundle);
							} else {
								MessageDialog.showMessage((Dialog) SwingUtilities.getWindowAncestor(c),
										"M_QueryBuilderQueryWithoutParameter", JOptionPane.OK_OPTION,
										QueryBuilder.this.bundle);
							}
							return;
						}

						final boolean okversion = QueryBuilder.this.table.expressionOK(0);
						boolean yes = false;

						if (!okversion) {
							if (MessageDialog.showQuestionMessage(
									SwingUtilities.getWindowAncestor((Component) e.getSource()),
									"M_QueryBuilder_invalidFilterInRow",
									QueryBuilder.this.bundle)) {
								yes = true;
							}
						}

						if (okversion) {
							query = ContainsExtendedSQLConditionValuesProcessor.queryToStandard(query,
									QueryBuilder.this.cols, QueryBuilder.this.types);
							keysValues.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, query);
						}

						if (okversion || (!okversion && yes)) {
							final EntityResult rs = en.query(keysValues, attributes,
									QueryBuilder.this.locator.getSessionId());
							PreviewQuery.show((Component) e.getSource(), rs, QueryBuilder.this.bundle);

						}
					} else {
						if (MessageDialog.showQuestionMessage(
								SwingUtilities.getWindowAncestor((Component) e.getSource()),
								"M_QueryBuilder_sinFiltroEnTabla",
								QueryBuilder.this.bundle)) {

							final EntityResult rs = en.query(keysValues, attributes,
									QueryBuilder.this.locator.getSessionId());

							if (rs.isEmpty() || (rs.calculateRecordNumber() == 0)) {

								MessageDialog.showInputMessage(
										SwingUtilities.getWindowAncestor((Component) e.getSource()),
										"M_QueryBuilder_consSinResult",
										QueryBuilder.this.bundle);
							} else {
								PreviewQuery.show((Component) e.getSource(), rs, QueryBuilder.this.bundle);
							}
						}
					}
				} catch (final Exception ex) {
					QueryBuilder.logger.error(null, ex);
				}

			}
		});
	}

	protected void installStoreButtonListener() {
		this.bStore.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final String[] auxCols = new String[QueryBuilder.this.cols.length - 1];
				final boolean[] auxColsCons = new boolean[QueryBuilder.this.cols.length - 1];

				for (int i = 0, a = QueryBuilder.this.cols.length - 1; i < a; i++) {
					auxCols[i] = new String(QueryBuilder.this.cols[i]);
					auxColsCons[i] = QueryBuilder.this.queryColumns[i];
				}

				final Expression ex = QueryBuilder.this.table.getExpression(0);

				final Map h = com.ontimize.db.query.QueryExpressionSelection.showQueryExpressionSelection(
						(Component) e.getSource(),
						new QueryExpression(QueryBuilder.clearExpression(ex), QueryBuilder.this.entity, auxCols,
								auxColsCons),
						QueryBuilder.this.entity, QueryBuilder.this.bundle,
						false, QueryBuilder.this.entity == null);

				final QueryExpression queryExpress = (QueryExpression) h.get(QueryExpressionSelection.EXPRESSION);

				if ((queryExpress != null) && (queryExpress.getExpression() != null)) {

					QueryBuilder.this.setExpression(queryExpress.getExpression());
					QueryBuilder.this.cols = new String[queryExpress.getCols().size()];
					for (int i = 0; i< queryExpress.getCols().size(); i++) {
						QueryBuilder.this.cols[i] = (String) queryExpress.getCols().get(i);
					}
					QueryBuilder.this.queryColumns = new boolean[ queryExpress.getQueryColumns().size()];
					for (int i = 0; i < queryExpress.getQueryColumns().size(); i++) {
						QueryBuilder.this.queryColumns[i] = (boolean) queryExpress.getQueryColumns().get(i);
					}

					QueryBuilder.this.cols = new String[queryExpress.getCols().size() + 1];
					QueryBuilder.this.queryColumns = new boolean[queryExpress.getCols().size() + 1];
					if (queryExpress.getEntity() != null) {
						QueryBuilder.this.types = new int[queryExpress.getCols().size() + 1];
					}

					for (int i = 0, a = queryExpress.getCols().size(); i < a; i++) {
						QueryBuilder.this.cols[i] = (String) queryExpress.getCols().get(i);
						QueryBuilder.this.queryColumns[i] = (boolean) queryExpress.getQueryColumns().get(i);
						if (queryExpress.getEntity() != null) {
							QueryBuilder.this.types[i] = ConditionsTableModel.VARCHAR;
						}
					}
					QueryBuilder.this.cols[queryExpress.getCols()
					                       .size()] = ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN;
					QueryBuilder.this.queryColumns[queryExpress.getCols().size()] = false;
					if (queryExpress.getEntity() != null) {
						QueryBuilder.this.types[queryExpress.getCols().size()] = ConditionsTableModel.ANY;
					}

					if (queryExpress.getEntity() != null) {
						QueryBuilder.this.setColumnTypes(QueryBuilder.this.cols, queryExpress.getEntity(), false);
					} else {
						QueryBuilder.this.setColumns(QueryBuilder.this.cols, QueryBuilder.this.types);
					}
				}
			}
		});
	}

	protected void installClearValuesButtonListener() {
		this.bClearValues.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilder.this.table.clearExpressionValues();
			}
		});
	}

	protected void installValuesButtonListener() {
		this.bValues.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilder.showParameterValuesTable((Component) e.getSource(), QueryBuilder.this.bundle,
						QueryBuilder.this.table, QueryBuilder.this.cols,
						QueryBuilder.this.types);
			}
		});
	}

	protected void installModifButtonListener() {
		this.bModif.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				QueryBuilder.this.simpleAll();
			}
		});
	}

	protected void installSplitButtonListener() {
		this.bSplit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length != 1) {
					return;
				}
				QueryBuilder.this.table.split(rows[0]);
			}
		});
	}

	protected void installAndNotButtonListener() {
		this.bAndN.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length < 2) {
					return;
				}
				QueryBuilder.this.table.doNotBetweenRows(rows);
			}
		});
	}

	protected void installAndButtonListener() {
		this.bAnd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length < 2) {
					return;
				}
				QueryBuilder.this.table.doAndBetweenRows(rows);
			}
		});
	}

	protected void installOrNotButtonListener() {
		this.bORN.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length < 2) {
					return;
				}
				QueryBuilder.this.table.orNotBetweenRows(rows);
			}
		});
	}

	protected void installOrButtonListener() {
		this.bOR.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected rows
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length < 2) {
					return;
				}
				QueryBuilder.this.table.orBetweenRows(rows);
			}
		});
	}

	protected void installDeleteButtonListener() {
		this.bDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int[] rows = QueryBuilder.this.table.getSelectedRows();
				if (rows.length < 1) {
					return;
				}
				QueryBuilder.this.table.removeRows(rows);
			}
		});
	}

	protected void installNewButtonListener() {
		this.bNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilder.this.table.addExpression();
			}
		});
	}

	protected void installColumnsButtonListener() {
		this.bCols.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				SelectOutputColumns sCols;

				String[] colsAux = null;
				boolean[] queryColsAux = null;

				final java.util.List l = new ArrayList();
				final java.util.List l2 = new ArrayList();

				for (int i = 0, a = QueryBuilder.this.cols.length; i < a; i++) {
					if (!QueryBuilder.this.cols[i].equals(ContainsExtendedSQLConditionValuesProcessor.ANY_COLUMN)) {
						l.add(QueryBuilder.this.cols[i]);
						l2.add(new Boolean(QueryBuilder.this.queryColumns[i]));
					}
				}

				colsAux = new String[l.size()];
				queryColsAux = new boolean[l.size()];

				for (int i = 0, a = l.size(); i < a; i++) {
					colsAux[i] = new String((String) l.get(i));
					queryColsAux[i] = ((Boolean) l2.get(i)).booleanValue();
				}

				final Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
				if ((w instanceof Frame) || (w instanceof Dialog)) {
					if (w instanceof Frame) {
						sCols = new SelectOutputColumns((Frame) w, colsAux, queryColsAux, QueryBuilder.this.bundle);
					} else {
						sCols = new SelectOutputColumns((Dialog) w, colsAux, queryColsAux, QueryBuilder.this.bundle);
					}
				} else {
					if (e.getSource() instanceof Frame) {
						sCols = new SelectOutputColumns((Frame) e.getSource(), colsAux, queryColsAux,
								QueryBuilder.this.bundle);
					} else {
						if (e.getSource() instanceof Dialog) {
							sCols = new SelectOutputColumns((Dialog) e.getSource(), colsAux, queryColsAux,
									QueryBuilder.this.bundle);
						} else {
							sCols = new SelectOutputColumns(colsAux, queryColsAux, QueryBuilder.this.bundle);
						}
					}
				}

				sCols.pack();
				ApplicationManager.center(sCols);
				final boolean[] aux = sCols.showSelectOutputColumns();

				QueryBuilder.this.queryColumns = new boolean[aux.length + 1];

				for (int i = 0, a = aux.length; i < a; i++) {
					QueryBuilder.this.queryColumns[i] = aux[i];
				}

				QueryBuilder.this.queryColumns[aux.length] = false;
			}
		});
	}

	protected void installClearButtonListener() {
		this.bClear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilder.this.table.removeAllExpressions();
			}
		});
	}

	protected void installLoadButtonListener() {
		this.bLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryBuilder.this.loadQuery();
			}
		});
	}

	protected void installSaveButtonListener() {
		this.bSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int iRows = QueryBuilder.this.table.getRowCount();
				if (iRows < 1) {
					return;
				}
				QueryBuilder.this.saveQuery();
			}
		});
	}

	protected boolean isCancelPressed() {
		return this.cancelPressed;
	}

	public void setColumns(final String[] cols, final int[] types) {
		this.table.setColumns(cols, types);
	}

	protected Operator[] getColumnTypeOperators(final int type) {
		return null;
	}

	protected void installTableListener() {
		this.table.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(final TableModelEvent e) {
				QueryBuilder.this.text.setText(ContainsSQLConditionValuesProcessorHelper
						.renderQueryConditionsExpressBundle(
								((ConditionsTableModel) QueryBuilder.this.table.getModel()).getExpression(0),
								QueryBuilder.this.bundle));
			}
		});
	}

	private File getFile() {

		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);

		final int option = fc.showSaveDialog(this);
		if (option == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile();
		}
		return null;

	}

	protected void saveQuery() {

		final File f = this.getFile();
		if (f != null) {
			try {
				final ObjectOutputStream bu = new ObjectOutputStream(new FileOutputStream(f));
				bu.writeObject(this.table.getExpression(0));
			} catch (final Exception e) {
				QueryBuilder.logger.error(null, e);
			}
		}
	}

	protected void loadQuery() {
		final File f = this.getFile();
		if (f != null) {
			try {
				final ObjectInputStream bu = new ObjectInputStream(new FileInputStream(f));
				final Object o = bu.readObject();
				this.table.addExpression((Expression) o);
			} catch (final Exception e) {
				QueryBuilder.logger.debug("abriendo el fichero", e);
			}
		}
	}

	public Expression getExpression() {

		if (this.table.expressionOKWithoutParameters(this.table.getExpression(0))) {
			return this.table.getExpression(0);
		}
		return null;
	}

	public void setExpression(final Expression ex) {
		if (ex == null) {
			return;
		}
		this.table.addInitExpression(ex);
	}

	protected void removeExpression() {
		final int rows = this.table.getRowCount();
		final int[] f = new int[rows];
		for (int i = 0, a = rows; i < a; i++) {
			f[i] = i;
		}
		this.table.removeRows(f);
	}

	public static int[] convertType(final java.util.List type) {
		final int[] typeColumns = new int[type.size() + 1];
		for (int i = 0; i < type.size(); i++) {
			typeColumns[i] = QueryBuilder.getTypeCol((String) type.get(i));
		}
		typeColumns[type.size()] = ConditionsTableModel.ANY;
		return typeColumns;
	}

	protected static String getStringType(final int i) {
		switch (i) {
		case ConditionsTableModel.VARCHAR:
			return "String";
		case ConditionsTableModel.DATE:
			return "Date";
		case ConditionsTableModel.NUMBER:
			return "Integer";
		case ConditionsTableModel.BOOLEAN:
			return "Boolean";
		default:
			return "unknown";
		}
	}

	public static int getTypeCol(final String type) {
		if ("String".equalsIgnoreCase(type)) {
			return ConditionsTableModel.VARCHAR;
		}
		if ("Date".equalsIgnoreCase(type)) {
			return ConditionsTableModel.DATE;
		}
		if ("Number".equalsIgnoreCase(type)) {
			return ConditionsTableModel.NUMBER;
		}
		if ("Integer".equalsIgnoreCase(type)) {
			return ConditionsTableModel.NUMBER;
		}
		if ("Double".equalsIgnoreCase(type)) {
			return ConditionsTableModel.NUMBER;
		}
		if ("Boolean".equalsIgnoreCase(type)) {
			return ConditionsTableModel.BOOLEAN;
		}
		return -1;
	}

	public static boolean isTextType(final int i) {
		if (i == ConditionsTableModel.VARCHAR) {
			return true;
		}
		return false;
	}

	public static int[] getAllColsType(final String[] col) {
		final int[] types = new int[col.length];
		for (int i = 0, a = col.length; i < a; i++) {
			types[i] = QueryBuilder.getTypeCol(col[i]);
		}
		return types;
	}

	public static String[] convertColumns(final Object[] cols) {
		final String[] columns = new String[cols.length];
		for (int i = 0; i < cols.length; i++) {
			columns[i] = (String) cols[i];
		}
		return columns;
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator) {
		return QueryBuilder.showQueryBuilder(c, entity, bundle, locator, false);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final boolean okCancel) {
		return QueryBuilder.showQueryBuilder(c, entity, bundle, locator, null, okCancel);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery) {
		return QueryBuilder.showQueryBuilder(c, entity, bundle, locator, initQuery, false);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery,
			final boolean okCancel) {

		final Vector v = new Vector();
		v.add(entity);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, false, false);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery,
			final boolean okCancel, final boolean openList) {

		final Vector v = new Vector();
		v.add(entity);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openList, false);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery,
			final boolean okCancel, final boolean openList, final boolean showCols) {

		final Vector v = new Vector();
		v.add(entity);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openList, showCols);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery,
			final boolean okCancel, final boolean openList, final boolean showCols, final String[] tCols) {

		final Vector v = new Vector();
		v.add(entity);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openList, showCols, tCols);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery,
			final boolean okCancel, final boolean openList, final boolean mostrarCols, final String[] tCols, final List lastName) {

		final Vector v = new Vector();
		v.add(entity);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openList, mostrarCols, tCols,
				lastName);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final String entity, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final Expression initQuery,
			final boolean okCancel, final boolean openList) {

		final Vector v = new Vector();
		v.add(entity);
		final QueryExpression q = new QueryExpression(initQuery, (String) null, (java.util.List) null, (java.util.List) null);
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, q, okCancel, openList, false);
	}

	public static QueryExpression showQueryBuilder(final java.awt.Component c, final Vector entities,
			final ResourceBundle bundle, final EntityReferenceLocator locator) {
		return QueryBuilder.showQueryBuilder(c, entities, bundle, locator, null, false, false, false);
	}

	private static EJDialog buildDialog(final Component c, final ResourceBundle bundle) {
		EJDialog dialog = null;

		if (!(c instanceof Frame) && !(c instanceof Dialog)) {
			final Window w = SwingUtilities.getWindowAncestor(c);
			if (w instanceof Frame) {
				dialog = new EJDialog((Frame) w, ApplicationManager.getTranslation("QueryBuilderTitle", bundle), true);
			} else if (w instanceof Dialog) {
				dialog = new EJDialog((Dialog) w, ApplicationManager.getTranslation("QueryBuilderTitle", bundle), true);
			}
		} else {
			if (c instanceof Frame) {
				dialog = new EJDialog((Frame) c, ApplicationManager.getTranslation("QueryBuilderTitle", bundle), true);
			}
			if (c instanceof Dialog) {
				dialog = new EJDialog((Dialog) c, ApplicationManager.getTranslation("QueryBuilderTitle", bundle), true);
			}
		}
		return dialog;
	}

	public QueryBuilder() {
	}

	public static QueryExpression showQueryBuilder(final Component c, final Vector v, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery, final boolean okCancel,
			final boolean openCols, final boolean showCols) {
		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openCols, showCols, null);
	}

	private static Expression clearExpression(final Expression ex) {
		if (ex == null) {
			return ex;
		}
		Expression e = new BasicExpression(ex.getLeftOperand(), ex.getOperator(), ex.getRightOperand());

		if (e.getLeftOperand() instanceof Expression) {
			final Expression aux = (Expression) e.getLeftOperand();
			final Expression aux2 = (Expression) e.getRightOperand();

			e = new BasicExpression(aux, e.getOperator(), aux2);
			return e;
		} else {
			if (e.getRightOperand() instanceof ParameterField) {
				e.setRightOperand(new ParameterField());
			}
		}

		return e;
	}

	public static QueryExpression showQueryBuilder(final Component c, final Vector v, final ResourceBundle bundle,
			final EntityReferenceLocator locator, final QueryExpression initQuery, final boolean okCancel,
			final boolean openList, final boolean showCols, final String[] tCols) {

		return QueryBuilder.showQueryBuilder(c, v, bundle, locator, initQuery, okCancel, openList, showCols, tCols,
				new Vector());
	}

	protected static JDialog queryBuilderDialog = null;

	protected static QueryBuilder qb = null;

	public static QueryExpression showQueryBuilder(final Component c, final Vector v, final ResourceBundle bundle,
			final EntityReferenceLocator locator, QueryExpression initQuery, final boolean okCancel,
			final boolean openList, final boolean showCols, final String[] tCols, final List lastName) {

		Map h = null;

		if (openList) {
			// If returns null => cancel query
			// If returns some not null value but
			// queryExpression.getExpression()
			// is null => Define
			// If returns some not null value but
			// queryExpression.getExpression()
			// != null => returns queryExpression

			if ((initQuery != null) && (initQuery.getEntity() != null)) {
				h = QueryExpressionSelection.showQueryExpressionSelection(c, null, initQuery.getEntity(), bundle, true,
						false);
			} else {
				if ((v != null) && !v.isEmpty()) {
					h = QueryExpressionSelection.showQueryExpressionSelection(c, null,
							(String) CollectionTools.firstElement(v),
							bundle, true, false);
				} else {
					return null;
				}
			}
			if (h == null) {
				return null;
			}
			if (((Boolean) h.get(QueryExpressionSelection.DEFINE)).booleanValue()) {
				if (h.get(QueryExpressionSelection.EXPRESSION) != null) {
					initQuery = (QueryExpression) h.get(QueryExpressionSelection.EXPRESSION);
				}
			} else {
				final QueryExpression qexp = (QueryExpression) h.get(QueryExpressionSelection.EXPRESSION);
				if (qexp == null) {
					return null;
				}

				final Map ht = QueryBuilder.getColumnTypes(qexp.getEntity(), bundle);
				final String[] co = (String[]) ht.get("cols");
				final int[] ty = (int[]) ht.get("types");
				if ((co == null) || (ty == null)) {
					return null;
				}

				final boolean op = QueryBuilder.showParameterValuesTable(c, bundle, qexp.getExpression(), co, ty);
				if (op) {
					if ((lastName != null) && (h.get(QueryExpressionSelection.NAME) != null)) {
						lastName.add(h.get(QueryExpressionSelection.NAME));
					}
					return qexp;
				} else {
					return null;
				}
			}
		}

		QueryBuilder.brute = false;
		if ((QueryBuilder.queryBuilderDialog == null)
				|| (QueryBuilder.queryBuilderDialog.getOwner() != SwingUtilities.getWindowAncestor(c))) {
			if (QueryBuilder.queryBuilderDialog != null) {
				QueryBuilder.queryBuilderDialog.dispose();
			}
			QueryBuilder.queryBuilderDialog = QueryBuilder.buildDialog(c, bundle);

			if (QueryBuilder.queryBuilderDialog != null) {
				QueryBuilder.qb = new QueryBuilder(v, bundle, locator, okCancel, initQuery, showCols, tCols);
				QueryBuilder.queryBuilderDialog.addWindowListener(new WindowListener() {

					@Override
					public void windowClosing(final WindowEvent evt) {
						QueryBuilder.brute = true;
					}

					@Override
					public void windowActivated(final WindowEvent evt) {
					}

					@Override
					public void windowClosed(final WindowEvent evt) {
					}

					@Override
					public void windowDeactivated(final WindowEvent evt) {
					}

					@Override
					public void windowDeiconified(final WindowEvent evt) {
					}

					@Override
					public void windowIconified(final WindowEvent evt) {
					}

					@Override
					public void windowOpened(final WindowEvent evt) {
					}
				});

				QueryBuilder.queryBuilderDialog.getContentPane().add(new JScrollPane(QueryBuilder.qb));
				QueryBuilder.queryBuilderDialog.pack();
				ApplicationManager.center(QueryBuilder.queryBuilderDialog);
			}
		} else {
			QueryBuilder.qb.initWithQueryExpression(v, bundle, locator, okCancel, initQuery, showCols, tCols);

		}
		QueryBuilder.queryBuilderDialog.setVisible(true);

		if (QueryBuilder.qb.isCancelPressed()) {
			return null;
		}

		final String[] auxCols = new String[QueryBuilder.qb.getCols().length - 1];
		final boolean[] auxColsCons = new boolean[QueryBuilder.qb.getCols().length - 1];

		for (int i = 0, a = QueryBuilder.qb.getCols().length - 1; i < a; i++) {
			auxCols[i] = new String(QueryBuilder.qb.getCols()[i]);
			auxColsCons[i] = QueryBuilder.qb.getQueryColumns()[i];
		}

		if (QueryBuilder.brute) {
			return null;
		}

		final QueryExpression qexp = new QueryExpression(QueryBuilder.qb.getExpression(), QueryBuilder.qb.getEntity(),
				auxCols, auxColsCons);

		if (QueryBuilder.qb.getExpression() == null) {
			return qexp;
		}

		final Expression e = new BasicExpression(qexp.getExpression().getLeftOperand(), qexp.getExpression().getOperator(),
				qexp.getExpression().getRightOperand());

		if (h != null) {
			if (h.get(QueryExpressionSelection.NAME) != null) {
				final QueryStore store = new FileQueryStore();
				final Expression ex = QueryBuilder.clearExpression(e);
				store.addQuery((String) h.get(QueryExpressionSelection.NAME),
						new QueryExpression(ex, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery()));
				lastName.add(h.get(QueryExpressionSelection.NAME));
			} else {
				if (MessageDialog.showQuestionMessage(SwingUtilities.getWindowAncestor(c), "M_QueryBuilderSave",
						bundle)) {
					final Map hi = QueryExpressionSelection.showQueryExpressionSelection(c, qexp, qexp.getEntity(),
							bundle, false, true);

					if (hi.get(QueryExpressionSelection.NAME) != null) {
						final QueryStore store = new FileQueryStore();
						final Expression ex = QueryBuilder.clearExpression(e);
						store.addQuery((String) hi.get(QueryExpressionSelection.NAME),
								new QueryExpression(ex, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery()));
						lastName.add(hi.get(QueryExpressionSelection.NAME));
					}
				}
			}
		}

		final boolean needParameters = QueryBuilder.needsExpressionParameters(e);
		if (needParameters) {

			final Window w = SwingUtilities.getWindowAncestor(c);
			if (w instanceof Frame) {
				MessageDialog.showMessage((Frame) w, "M_QueryBuilderQueryWithoutParameter", JOptionPane.WARNING_MESSAGE,
						bundle);
			} else {
				MessageDialog.showMessage((Dialog) w, "M_QueryBuilderQueryWithoutParameter",
						JOptionPane.WARNING_MESSAGE, bundle);
			}
			return new QueryExpression(e, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery());
		}

		return qexp;
	}

	static boolean brute = false;

	public static QueryExpression showQueryBuilder(final Component c, final ResourceBundle bundle, final String[] columns, final String[] types,
			final Expression initExpression, final List lastName) {

		// Have not entity
		QueryBuilder.brute = false;

		QueryExpression initQuery = null;
		final Map h = QueryExpressionSelection.showQueryExpressionSelection(c, null, null, bundle, true, false);

		if (h == null) {
			return null;
		}

		if (((Boolean) h.get(QueryExpressionSelection.DEFINE)).booleanValue()) {
			if (h.get(QueryExpressionSelection.EXPRESSION) != null) {
				initQuery = (QueryExpression) h.get(QueryExpressionSelection.EXPRESSION);
			}
		} else {
			final QueryExpression qexp = (QueryExpression) h.get(QueryExpressionSelection.EXPRESSION);
			if (qexp == null) {
				return null;
			}

			final java.util.List l = qexp.getCols();

			final String[] co = new String[l.size()];
			final int[] ty = new int[l.size()];

			for (int i = 0, a = l.size(); i < a; i++) {
				co[i] = ApplicationManager.getTranslation((String) l.get(i), bundle);
				ty[i] = ConditionsTableModel.VARCHAR;
			}

			if ((co == null) || (ty == null)) {
				return null;
			}
			final boolean op = QueryBuilder.showParameterValuesTable(c, bundle, qexp.getExpression(), co, ty);
			if (op) {
				if ((lastName != null) && (h.get(QueryExpressionSelection.NAME) != null)) {
					lastName.add(h.get(QueryExpressionSelection.NAME));
				}
				return qexp;
			} else {
				return null;
			}
		}

		if ((QueryBuilder.queryBuilderDialog == null)
				|| (QueryBuilder.queryBuilderDialog.getOwner() != SwingUtilities.getWindowAncestor(c))) {
			if (QueryBuilder.queryBuilderDialog != null) {
				QueryBuilder.queryBuilderDialog.dispose();
			}
			QueryBuilder.qb = new QueryBuilder(bundle, columns, types, initExpression);

			QueryBuilder.queryBuilderDialog.addWindowListener(new WindowListener() {

				@Override
				public void windowClosing(final WindowEvent evt) {
					QueryBuilder.brute = true;
				}

				@Override
				public void windowActivated(final WindowEvent evt) {
				}

				@Override
				public void windowClosed(final WindowEvent evt) {
				}

				@Override
				public void windowDeactivated(final WindowEvent evt) {
				}

				@Override
				public void windowDeiconified(final WindowEvent evt) {
				}

				@Override
				public void windowIconified(final WindowEvent evt) {
				}

				@Override
				public void windowOpened(final WindowEvent evt) {
				}
			});

			QueryBuilder.queryBuilderDialog.getContentPane().add(new JScrollPane(QueryBuilder.qb));
			QueryBuilder.queryBuilderDialog.pack();
			if (QueryBuilder.queryBuilderDialog != null) {
				ApplicationManager.center(QueryBuilder.queryBuilderDialog);
			}
		}

		QueryBuilder.queryBuilderDialog.setVisible(true);

		if (QueryBuilder.qb.isCancelPressed()) {
			return null;
		}

		final String[] auxCols = new String[QueryBuilder.qb.getCols().length - 1];
		final boolean[] auxColsCons = new boolean[QueryBuilder.qb.getCols().length - 1];

		for (int i = 0, a = QueryBuilder.qb.getCols().length - 1; i < a; i++) {
			auxCols[i] = new String(QueryBuilder.qb.getCols()[i]);
			auxColsCons[i] = QueryBuilder.qb.getQueryColumns()[i];
		}

		if (QueryBuilder.brute) {
			return null;
		}

		final QueryExpression qexp = new QueryExpression(QueryBuilder.qb.getExpression(), QueryBuilder.qb.getEntity(),
				auxCols, auxColsCons);
		if (QueryBuilder.qb.getExpression() == null) {
			return qexp;
		}

		final Expression e = new BasicExpression(qexp.getExpression().getLeftOperand(), qexp.getExpression().getOperator(),
				qexp.getExpression().getRightOperand());

		if (h != null) {
			if (h.get(QueryExpressionSelection.NAME) != null) {
				final QueryStore store = new FileQueryStore();
				final Expression ex = QueryBuilder.clearExpression(e);
				store.addQuery((String) h.get(QueryExpressionSelection.NAME),
						new QueryExpression(ex, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery()));
				lastName.add(h.get(QueryExpressionSelection.NAME));
			} else {
				if (MessageDialog.showQuestionMessage(SwingUtilities.getWindowAncestor(c), "M_QueryBuilderSave",
						bundle)) {
					final Map hi = QueryExpressionSelection.showQueryExpressionSelection(c, qexp, qexp.getEntity(),
							bundle, false, true);

					if (hi.get(QueryExpressionSelection.NAME) != null) {
						final QueryStore store = new FileQueryStore();
						final Expression ex = QueryBuilder.clearExpression(e);
						store.addQuery((String) hi.get(QueryExpressionSelection.NAME),
								new QueryExpression(ex, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery()));
						lastName.add(hi.get(QueryExpressionSelection.NAME));
					}
				}
			}
		}

		final boolean needParameters = QueryBuilder.needsExpressionParameters(e);
		if (needParameters) {

			final Window w = SwingUtilities.getWindowAncestor(c);
			if (w instanceof Frame) {
				MessageDialog.showMessage((Frame) w, "M_QueryBuilderQueryWithoutParameter", JOptionPane.WARNING_MESSAGE,
						bundle);
			} else {
				MessageDialog.showMessage((Dialog) w, "M_QueryBuilderQueryWithoutParameter",
						JOptionPane.WARNING_MESSAGE, bundle);
			}

			if (!QueryBuilder.showParameterValuesTable(c, bundle, e, columns, QueryBuilder.getAllColsType(types))) {
				MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(c),
						ApplicationManager.getTranslation("M_QueryBuilderQueryWithoutParameterError", bundle));
				return null;
			}
			return new QueryExpression(e, qexp.getEntity(), qexp.getCols(), qexp.getColumnToQuery());
		}
		return qexp;

	}

}
