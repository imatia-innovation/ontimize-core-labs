package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.util.math.MathExpressionParser;
import com.ontimize.util.math.MathExpressionParserFactory;

/**
 * This class is a dialog that displays all the column calculator functionalities.
 */
class ColumnCalculatorWindow extends EJDialog {

	private static final Logger logger = LoggerFactory.getLogger(ColumnCalculatorWindow.class);

	private static ColumnCalculatorWindow wCalculator = null;

	protected JList columnList = null;

	protected JList calculedColList = null;

	protected JTextArea textArea = null;

	protected JButton bAccept = null;

	protected JButton bCancel = null;

	protected JButton bResult = null;

	protected JTable resultTable = null;

	protected JButton bAdd = null;

	protected JButton bDelete = null;

	protected JButton bEdit = null;

	protected JSplitPane spColumn = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

	protected JSplitPane spWest = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

	protected Table table = null;

	public ColumnCalculatorWindow(final Dialog owner, final Table t) {
		super(owner);
		this.table = t;
		this.init();
	}

	public ColumnCalculatorWindow(final Frame owner, final Table t) {
		super(owner);
		this.table = t;
		this.init();
	}

	protected static class Expression {

		private String col = null;

		private String expression = null;

		public Expression(final String col, final String expression) {
			this.col = col;
			this.expression = expression;
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

		@Override
		public String toString() {
			return this.col;
		}

	}

	private void init() {
		this.columnList = new JList();
		this.textArea = new JTextArea();
		this.textArea.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.bAccept = new JButton("OK");
		this.bCancel = new JButton("application.cancel");
		this.bResult = new JButton("Result");

		this.columnList = new JList(new Vector(this.table.getRealColumns()));// The original
		// visible
		// columns
		this.columnList.setFocusable(false);
		this.columnList.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 2) {
					final int index = ColumnCalculatorWindow.this.columnList.locationToIndex(e.getPoint());
					if (index >= 0) {
						final Object value = ColumnCalculatorWindow.this.columnList.getModel().getElementAt(index);
						// String selected = textArea.getSelectedText();
						ColumnCalculatorWindow.this.textArea.replaceSelection(value.toString());
					}
				}
			}
		});

		final DefaultListModel dlM = new DefaultListModel();
		final Map h = ((TableSorter) this.table.getJTable().getModel()).getCalculatedColumns();
		final Enumeration enuK = Collections.enumeration(h.keySet());

		while (enuK.hasMoreElements()) {
			final Object k = enuK.nextElement();
			final ColumnCalculatorWindow.Expression exp = new Expression(k.toString(), h.get(k).toString());
			dlM.addElement(exp);
		}

		this.calculedColList = new JList(dlM);

		this.calculedColList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.calculedColList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				final int index = ColumnCalculatorWindow.this.calculedColList.getSelectedIndex();
				if (index >= 0) {
					final Object value = ColumnCalculatorWindow.this.calculedColList.getModel().getElementAt(index);
					ColumnCalculatorWindow.this.textArea
					.setText(((ColumnCalculatorWindow.Expression) value).getExpression());
				}

			}
		});

		final JPanel jbButtonsPanel = new JPanel(new GridBagLayout());
		jbButtonsPanel.add(this.bAccept, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jbButtonsPanel.add(this.bCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		jbButtonsPanel.add(this.bResult, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.resultTable = new ResultTable(this.table);

		this.bResult.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final MathExpressionParser parser = MathExpressionParserFactory.getInstance();
				final List source = new ArrayList();
				final List realCols = ColumnCalculatorWindow.this.table.getRealColumns();
				final String expression = ColumnCalculatorWindow.this.textArea.getText();
				if ((expression != null) && (expression.length() > 0)) {
					for (final Iterator i = realCols.iterator(); i.hasNext();) {
						final String col = i.next().toString();
						if (expression.indexOf(col) >= 0) {
							parser.addVariable(col, 0.0);
							source.add(col);
						}
					}
					parser.parseExpression(expression);
				}
				ColumnCalculatorWindow.this.resultTable
				.setModel(new ResultModel(ColumnCalculatorWindow.this.table.getJTable().getModel(), parser));
			}
		});

		this.bCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

			}
		});

		this.bAccept.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				ColumnCalculatorWindow.wCalculator.setVisible(false);
			}
		});

		this.bAdd = new JButton("A+");
		this.bAdd.setMargin(new Insets(0, 0, 0, 0));
		this.bAdd.setToolTipText("table.insert");
		this.bAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String name = JOptionPane.showInputDialog(ColumnCalculatorWindow.this.bAdd,
						"Name of the column to insert", null);
				final ColumnCalculatorWindow.Expression exp = new Expression(name,
						ColumnCalculatorWindow.this.textArea.getText());
				((DefaultListModel) ColumnCalculatorWindow.this.calculedColList.getModel()).addElement(exp);
			}
		});

		this.bDelete = new JButton("D-");
		this.bDelete.setToolTipText("delete");
		this.bDelete.setMargin(new Insets(0, 0, 0, 0));
		this.bDelete.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int index = ColumnCalculatorWindow.this.calculedColList.getSelectedIndex();
				if (index >= 0) {
					((DefaultListModel) ColumnCalculatorWindow.this.calculedColList.getModel()).remove(index);
				}
			}
		});

		this.bEdit = new JButton("E+");
		this.bEdit.setToolTipText("Editar");
		this.bEdit.setMargin(new Insets(0, 0, 0, 0));
		this.bEdit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int index = ColumnCalculatorWindow.this.calculedColList.getSelectedIndex();
				if (index >= 0) {
					final ColumnCalculatorWindow.Expression exp = (ColumnCalculatorWindow.Expression) ((DefaultListModel) ColumnCalculatorWindow.this.calculedColList
							.getModel())
							.getElementAt(index);
					exp.setExpression(ColumnCalculatorWindow.this.textArea.getText());
				}
			}
		});

		final JPanel buttonsPanel = new JPanel(new GridBagLayout());
		buttonsPanel.add(this.bAdd, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonsPanel.add(this.bDelete, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		buttonsPanel.add(this.bEdit, new GridBagConstraints(0, 2, 1, 1, 0, 1, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

		this.getContentPane().setLayout(new BorderLayout());
		this.spColumn.add(new JScrollPane(this.columnList));
		this.spColumn.add(new JScrollPane(this.calculedColList));
		this.spWest.add(this.spColumn);
		final JScrollPane pC = new JScrollPane(this.textArea);
		final JPanel p = new JPanel(new BorderLayout());
		p.add(pC);
		p.add(buttonsPanel, BorderLayout.WEST);
		this.spWest.add(p);
		this.spWest.setPreferredSize(new Dimension(375, 500));
		final JPanel pCenter = new JPanel(new GridBagLayout());
		pCenter.add(this.spWest, new GridBagConstraints(0, 0, 2, 1, 1, 1, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		final JScrollPane sP = new JScrollPane(this.resultTable);
		sP.setPreferredSize(new Dimension(125, 500));
		pCenter.add(sP, new GridBagConstraints(2, 0, 1, 1, 0, 1, GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0), 0, 0));
		this.getContentPane().add(pCenter, BorderLayout.CENTER);
		this.getContentPane().add(jbButtonsPanel, BorderLayout.SOUTH);
		this.pack();

	}

	protected static class ResultTable extends JTable {

		protected Table table = null;

		public ResultTable(final Table t) {
			super(new ResultModel(t.getJTable().getModel(), null));
			this.table = t;
			this.setColumHeader();
		}

		@Override
		public TableCellRenderer getCellRenderer(final int row, final int column) {
			final int cM = this.convertColumnIndexToModel(column);
			if (cM == 0) {
				return this.table.getJTable().getCellRenderer(row, column);
			}
			return super.getCellRenderer(row, column);
		}

		protected void setColumHeader() {
			if (this.table != null) {
				final TableColumn tC = this.table.getJTable().getColumnModel().getColumn(0);
				final TableColumn tCO = this.getColumnModel().getColumn(0);
				tCO.setWidth(tC.getWidth());
				tCO.setMaxWidth(tC.getMaxWidth());
				tCO.setMinWidth(tC.getMinWidth());
				tCO.setPreferredWidth(tC.getPreferredWidth());
			}
		}

		@Override
		public void tableChanged(final TableModelEvent e) {
			super.tableChanged(e);
			this.setColumHeader();
		}

	}

	protected static class ResultModel extends AbstractTableModel {

		protected TableModel tModel = null;

		protected MathExpressionParser parser = null;

		public ResultModel(final TableModel tModel, final MathExpressionParser parser) {
			this.tModel = tModel;
			this.parser = parser;
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(final int columnIndex) {
			if (columnIndex == 0) {
				return "Nº";
			} else {
				return "";
			}
		}

		@Override
		public int getRowCount() {
			return this.tModel.getRowCount();
		}

		@Override
		public Object getValueAt(final int rowIndex, final int columnIndex) {
			if ((this.parser == null) && (columnIndex == 0)) {
				return new Integer(rowIndex);
			}
			if (this.parser == null) {
				return null;
			}

			if (columnIndex == 0) {
				return this.tModel.getValueAt(rowIndex, 0);
			}
			final int to = this.tModel.getColumnCount();
			for (int i = 1; i < to; i++) {
				final Object col = this.tModel.getColumnName(i);
				final Object oValue = this.tModel.getValueAt(rowIndex, i);
				if ((oValue != null) && (oValue instanceof Number)) {
					this.parser.addVariableAsObject(col.toString(), new Double(((Number) oValue).doubleValue()));
				} else {
					if (oValue != null) {
						this.parser.addVariableAsObject(col.toString(), oValue);
					} else {
						this.parser.addVariable(col.toString(), 0.0);
					}
				}
			}
			if (this.parser.hasError()) {
				if (ApplicationManager.DEBUG) {
					ColumnCalculatorWindow.logger.debug(this.getClass().toString() + ". Calculated column Error: "
							+ ". Expression: " + ". Error: " + this.parser.getErrorInfo());
				}
			}
			return this.parser.getValueAsObject();
		}

	}

	public static void showCalculatorWindow(final Component c, final Table t) {
		final Window w = SwingUtilities.getWindowAncestor(c);
		if (w instanceof Frame) {
			ColumnCalculatorWindow.wCalculator = new ColumnCalculatorWindow((Frame) w, t);
		} else if (w instanceof Dialog) {
			ColumnCalculatorWindow.wCalculator = new ColumnCalculatorWindow((Dialog) w, t);
		}
		ApplicationManager.center(ColumnCalculatorWindow.wCalculator);
		ColumnCalculatorWindow.wCalculator.setVisible(true);
	}

}
