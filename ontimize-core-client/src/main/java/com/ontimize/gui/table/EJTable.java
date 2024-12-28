package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.Collator;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.TipScroll;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.table.TableSorter.ValueByGroup;
import com.ontimize.jee.common.dto.EntityResult;

/**
 * This class is the JTable inside the Table.
 */
public class EJTable extends JTable implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(EJTable.class);

	private List visibleColumns = null;

	private TipScroll textTip;

	private final StringBuilder textToFind = new StringBuilder();

	protected GroupCellRenderer groupCellRenderer = null;

	protected ValueByGroupRenderer valueByGroupRenderer = null;

	protected Table ontimizeTable;

	protected boolean rightDirection = true;

	public static boolean defaultFitRowHeight = false;

	protected boolean fitRowsHeight = EJTable.defaultFitRowHeight;

	public boolean isRightDirection() {
		return this.rightDirection;
	}

	public void setRightDirection(final boolean right) {
		this.rightDirection = right;
	}

	@Override
	public void free() {
		FreeableUtils.freeComponent(this.textTip);
		this.ontimizeTable = null;
	}

	/**
	 * Creates a EJTable with the default visible columns. The table, to hide the non visible columns,
	 * reduces the size of those to 0, so they remain in the model but with no visibility.
	 * @param tableModel the table model inside the JTable
	 * @param visibleColumns the columns that will be visible by default
	 */
	public EJTable(final Table oTable, final TableModel tableModel, final List visibleColumns) {
		super(tableModel);
		this.ontimizeTable = oTable;
		this.visibleColumns = visibleColumns;
		this.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				if (EJTable.this.getTextTip() != null) {
					EJTable.this.getTextTip().setVisible(false);
					EJTable.this.textToFind.delete(0, EJTable.this.textToFind.length());
				}
			}
		});

		this.setSurrendersFocusOnKeystroke(true);
		// autoCreateColumnsFromModel = false;
	}

	@Override
	public void setValueAt(final Object aValue, final int row, final int column) {
		super.setValueAt(aValue, row, column);
	}

	@Override
	public void repaint(final Rectangle r) {
		super.repaint();
	}

	@Override
	public void setRowHeight(final int row, final int height) {
		super.setRowHeight(row, height);
	}

	@Override
	public void setRowHeight(final int rowHeight) {
		if (!fitRowsHeight) {
			super.setRowHeight(rowHeight);
		}
	}

	@Override
	protected void resizeAndRepaint() {
		if (this.rowHeightSet) {
			return;
		}
		this.revalidate();
		this.repaint();
	}

	protected boolean rowHeightSet = false;

	/**
	 * Sets a new TableModel and liberates the memory occupied by the previous one.
	 */
	@Override
	public void setModel(final TableModel tableModel) {
		final TableModel mAct = this.getModel();
		if (mAct != null) {
			if (mAct instanceof TableSorter) {
				try {
					((TableSorter) mAct).free();
				} catch (final Exception ex) {
					EJTable.logger.trace(null, ex);
				}
			}
		}
		super.setModel(tableModel);
	}

	public void selectFirstVisibleColumn(final int rowIndex) {
		int columnIndex = 0;
		while (!this.ontimizeTable.isVisibleColumn(this.getColumnName(columnIndex))) {
			columnIndex++;
			if (columnIndex == (this.getColumnCount() - 1)) {
				break;
			}
		}
		super.changeSelection(rowIndex, columnIndex, false, false);
	}

	@Override
	public void changeSelection(int rowIndex, int columnIndex, final boolean toggle, final boolean extend) {
		final int previousSelected = this.getSelectedColumn();
		final int prevRow = this.getSelectedRow();
		boolean right = true;
		if (previousSelected >= 0) {
			if (columnIndex < previousSelected) {
				right = false;
			}
			if (!right && (prevRow < rowIndex)) {
				right = true;
			}
		}
		if (columnIndex >= 0) {
			// Check the visibility...
			String sName = this.getColumnName(columnIndex);
			while (!this.ontimizeTable.isVisibleColumn(sName)) {
				if (right) {
					if (columnIndex >= (this.getColumnCount() - 1)) {
						if (rowIndex < (this.getRowCount() - 1)) {
							rowIndex++;
							columnIndex = 0;
							while (!this.ontimizeTable.isVisibleColumn(this.getColumnName(columnIndex))) {
								columnIndex++;
								if (columnIndex == (this.getColumnCount() - 1)) {
									break;
								}
							}
						}
						break;
					}
					columnIndex++;
				}
				if (!right) {
					if (columnIndex == 0) {
						if (rowIndex > 0) {
							rowIndex--;
							columnIndex = this.getColumnCount() - 1;
							while (!this.ontimizeTable.isVisibleColumn(this.getColumnName(columnIndex))) {
								columnIndex--;
								if (columnIndex == 0) {
									break;
								}
							}
						}
						break;
					}
					columnIndex--;
				}
				sName = this.getColumnName(columnIndex);
			}
		}
		super.changeSelection(rowIndex, columnIndex, toggle, extend);
	}

	/**
	 * Returns the editor to be used when no editor has been set in a <code>TableColumn</code>. In this
	 * case, the classes that will be the default editors are
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>class type</b></td>
	 * <td><b>default renderer class</b></td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Double</td>
	 * <td>com.ontimize.gui.table.RealCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Float</td>
	 * <td>com.ontimize.gui.table.RealCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Integer</td>
	 * <td>com.ontimize.gui.table.IntegerCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Long</td>
	 * <td>com.ontimize.gui.table.IntegerCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Number</td>
	 * <td>com.ontimize.gui.table.IntegerCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.util.Date</td>
	 * <td>com.ontimize.gui.table.DateCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.sql.Date</td>
	 * <td>com.ontimize.gui.table.DateCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.sql.Timestamp</td>
	 * <td>com.ontimize.gui.table.DateCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>java.lang.Boolean</td>
	 * <td>com.ontimize.gui.table.BooleanCellEditor</td>
	 * </tr>
	 * <tr>
	 * <td>default</td>
	 * <td>com.ontimize.gui.table.StringCellEditor</td>
	 * </tr>
	 * </Table>
	 */
	@Override
	public TableCellEditor getDefaultEditor(final Class type) {
		// Setting the default editor
		if (this.defaultEditorsByColumnClass.containsKey(type)) {
			return (TableCellEditor) this.defaultEditorsByColumnClass.get(type);
		} else {
			com.ontimize.gui.table.CellEditor editor = null;
			final Map params = new Hashtable();
			params.put(com.ontimize.gui.table.CellEditor.COLUMN_PARAMETER, "DEFAULT");
			if ((type == java.lang.Double.class) || (type == java.lang.Float.class)) {
				editor = new com.ontimize.gui.table.RealCellEditor(params);
			} else if ((type == java.lang.Integer.class) || (type == java.lang.Long.class)) {
				editor = new com.ontimize.gui.table.IntegerCellEditor(params);
			} else if (type.getSuperclass() == java.lang.Number.class) {
				editor = new com.ontimize.gui.table.IntegerCellEditor(params);
			} else if ((type == java.util.Date.class) || (type == java.sql.Date.class)
					|| (type == java.sql.Timestamp.class)) {
				editor = new com.ontimize.gui.table.DateCellEditor(params);
			} else if (type == java.lang.Boolean.class) {
				params.put(CheckDataField.RETURNBOOLEAN, "yes");
				editor = new com.ontimize.gui.table.BooleanCellEditor(params);
			} else {
				editor = new com.ontimize.gui.table.StringCellEditor(params);
			}
			this.defaultEditorsByColumnClass.put(type, editor);
			return editor;
		}
	}

	/**
	 * Returns the column view index
	 * @param columnName the column name
	 * @return the column index in the view; -1 in case the column is not found
	 */
	public int getColumnIndex(final String columnName) {
		try {
			return this.convertColumnIndexToView(this.getColumn(columnName).getModelIndex());
		} catch (final Exception ex) {
			EJTable.logger.trace(null, ex);
			return -1;
		}
	}

	protected boolean isInsertingRow(final int editedRow) {
		if ((this.getModel() instanceof TableSorter) && ((TableSorter) this.getModel()).isInsertingEnabled()) {
			return ((TableSorter) this.getModel()).isInsertingRow(editedRow);
		}
		return false;
	}

	protected Map retrieveOtherData(final TableCellEditor ed, final boolean isInsertingRow, int editedRow,
			final Map keysValues) {
		Map otherData = null;
		if (ed instanceof ISetReferenceValues) {
			final List columnsToSet = ((ISetReferenceValues) ed).getSetColumns();
			if ((columnsToSet != null) && (columnsToSet.size() > 0)) {
				otherData = ((ISetReferenceValues) ed).getSetData(true);
				if (otherData == null) {
					otherData = new Hashtable();
				}
				if (!isInsertingRow) {
					// Update the values in other columns if the editor contains
					// the
					// parameter 'onsetvalueset'
					this.ontimizeTable.updateRowData(otherData, columnsToSet, keysValues);
					editedRow = this.ontimizeTable.getRowForKeys(keysValues);
				} else {
					for (int k = 0; k < columnsToSet.size(); k++) {
						final String columnName = (String) columnsToSet.get(k);
						final int colIndex = this.getColumnIndex(columnName);
						this.setValueAt(otherData.get(columnName), editedRow, colIndex);
					}
				}
			}
		}
		return otherData;
	}

	/**
	 * Method called when the table edition stops. Notifies the table by calling the table method
	 * {@link Table#fireEditingWillStop}
	 */
	@Override
	public void editingStopped(final ChangeEvent changeEvent) {
		if (!this.isEditing()) {
			return;
		}

		int editedRow = this.editingRow;
		final int editedColumn = this.editingColumn;

		// Only update and notify if the new value is different that the old one
		if ((editedRow < 0) || (editedColumn < 0)) {
			return;
		}

		if (editedRow >= this.getRowCount()) {
			super.editingStopped(changeEvent);
			return;
		}

		final Object idCol = ((TableSorter) this.getModel())
				.getColumnIdentifier(this.convertColumnIndexToModel(editedColumn));
		final Object oPreviousValue = this.getValueAt(editedRow, editedColumn);
		final Object oValue = this.getCellEditor().getCellEditorValue();
		final TableCellEditor ed = this.getCellEditor();
		try {
			this.ontimizeTable.fireEditingWillStop(oValue, oPreviousValue, editedRow, editedColumn);
		} catch (final com.ontimize.gui.table.EditingVetoException ex) {
			EJTable.logger.trace(null, ex);
			if (ex.isShowMessage()) {
				final Window w = SwingUtilities.getWindowAncestor(this);
				MessageDialog.showErrorMessage(w, ex.getMessage(), this.ontimizeTable.resourcesFile);
			}
			this.editingCanceled(changeEvent);
			this.requestFocus();
			this.changeSelection(editedRow, editedColumn, false, false);
			return;
		}

		// We have to get the keysValues before call super because in the super
		// method the keys for each row changes (the sorter, filter and grouped
		// model changed)
		final Map keysValues = this.ontimizeTable.getRowKeys(editedRow);

		final boolean isInsertingRow = this.isInsertingRow(editedRow);

		super.editingStopped(changeEvent);

		final Map otherData = this.retrieveOtherData(ed, isInsertingRow, editedRow, keysValues);

		// TEST -------------------------------------
		// Update
		boolean bLastEditableColumn = false;

		if ((this.getModel() instanceof TableSorter) && isInsertingRow) {
			bLastEditableColumn = this.isLastEditableColumn(editedColumn, true);
		} else {
			editedRow = this.ontimizeTable.getRowForKeys(keysValues);
			super.changeSelection(editedRow, editedColumn, false, false);
		}

		if ((bLastEditableColumn) && this.isRightDirection() && isInsertingRow) {
			// we should check errors here and don't stop editing in such case
			this.ontimizeTable.executeInsertRow();
			// select first cell new row
			this.selectFirstVisibleColumn(this.getRowCount() - 1);
		}
		// END TEST

		// Repaint the cell
		final Rectangle rect = this.getCellRect(editedRow, editedColumn, true);
		rect.x = 0;
		rect.width = this.getWidth();
		this.repaint(rect);

		if (this.ontimizeTable.editableColumnsUpdateEntity.contains(idCol) && (!isInsertingRow)) {
			this.updateEntityInEditingStopped(editedRow, editedColumn, idCol, oPreviousValue, oValue, ed, keysValues,
					otherData);
			return;
		} else {
			this.ontimizeTable.fireEditingStopped(oValue, oPreviousValue, editedRow, editedColumn);
		}

	}

	protected void updateEntityInEditingStopped(final int editedRow, final int editedColumn, final Object idCol, final Object oPreviousValue,
			final Object oValue, final TableCellEditor ed, final Map keysValues,
			final Map otherData) {
		try {
			if ((oValue == null) && (oPreviousValue == null)) {
				EJTable.logger
				.debug(this.getClass().toString() + ": Cell edition" + editedRow + "," + editedColumn
						+ ". The value was NULL and it is still NULL. Entity is not called");
				return;
			} else {
				if ((oValue != null) && (oPreviousValue != null)) {
					if (oValue.equals(oPreviousValue)) {
						EJTable.logger.debug(
								this.getClass().toString() + ": Cell edition: " + editedRow + "," + editedColumn
								+ ". No value changes " + oValue + ". Entity is not called");
						this.ontimizeTable.fireEditingStopped(oValue, oPreviousValue, editedRow, editedColumn);
						return;
					}
				}
			}

			final EntityResult res = this.ontimizeTable.updateTable(keysValues, editedColumn, ed, otherData, oPreviousValue);

			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.ontimizeTable.getParentForm().message(res.getMessage(), Form.ERROR_MESSAGE);
				final Map oldcv = new Hashtable();
				if (oPreviousValue != null) {
					oldcv.put(idCol, oPreviousValue);
				}
				this.ontimizeTable.refreshRow(editedRow, oldcv);
				this.ontimizeTable.fireEditingCancelled(editedRow, editedColumn);
			} else if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				this.ontimizeTable.getParentForm().message(res.getMessage(), Form.INFORMATION_MESSAGE);
			} else {
				this.ontimizeTable.fireEditingStopped(oValue, oPreviousValue, editedRow, editedColumn);
			}
		} catch (final Exception ex) {
			EJTable.logger.trace(null, ex);
			this.ontimizeTable.getParentForm().message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
			final Map oldkv = new Hashtable();
			if (oPreviousValue != null) {
				oldkv.put(idCol, oPreviousValue);
			}
			this.ontimizeTable.refreshRow(editedRow, oldkv);
			this.ontimizeTable.fireEditingCancelled(editedRow, editedColumn);
		}
	}

	protected boolean isLastEditableColumn(final int editedColumn, boolean bLastEditableColumn) {
		for (int i = editedColumn + 1; i < this.getColumnCount(); i++) {
			if (((TableSorter) this.getModel()).insertableCols.contains(this.getColumnName(i))
					&& this.ontimizeTable.isVisibleColumn(this.getColumnName(i))) {
				bLastEditableColumn = false;
				break;
			}
		}
		return bLastEditableColumn;
	}

	/**
	 * Method called when the table edition is cancel. Notifies the table by calling the table method
	 * {@link Table#fireEditingCancelled}.
	 */
	@Override
	public void editingCanceled(final ChangeEvent changeEvent) {
		if (!this.isEditing()) {
			return;
		}
		final int editedRow = this.editingRow;
		final int editedColumn = this.editingColumn;
		if ((editedRow < 0) || (editedColumn < 0)) {
			return;
		}
		super.editingCanceled(changeEvent);
		this.ontimizeTable.fireEditingCancelled(editedRow, editedColumn);
	}

	@Override
	public TableCellEditor getCellEditor(final int row, final int column) {
		if (this.ontimizeTable.cellEditorManager != null) {
			final TableCellEditor editor = this.ontimizeTable.cellEditorManager.getCellEditor(this, row, column);
			if (editor != null) {
				return editor;
			}
		}
		return super.getCellEditor(row, column);
	}

	@Override
	public boolean editCellAt(final int row, final int column, final EventObject eventObject) {
		final Cursor c = this.getCursor();
		try {
			this.requestFocus();
			// this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final boolean res = super.editCellAt(row, column, eventObject);
			if (res && (this.editorComp != null)) {
				// Scroll
				this.scrollRectToVisible(this.getCellRect(row, column, true));
				this.editorComp.requestFocus();

				if (this.editorComp instanceof JTextComponent) {
					// if (!(eventObject instanceof KeyEvent)) {
					((JTextComponent) this.editorComp).selectAll();
					// 5}
				}
				// else {
				// this.editorComp.requestFocus();
				// }
			}
			return res;
		} catch (final Exception ex) {
			EJTable.logger.error(null, ex);
			return false;
		} finally {
			// this.setCursor(c);
		}
	}

	/**
	 * Provides a reference to the GroupCellRenderer for this table. If no cell renderer set, creates a
	 * new instance for the table.
	 * @return a reference to the groupCellRenderer
	 */
	protected TableCellRenderer getGroupCellRenderer() {
		if (this.groupCellRenderer == null) {
			this.groupCellRenderer = new GroupCellRenderer();
		}
		return this.groupCellRenderer;
	}

	/**
	 * Provides a reference to the ValueByGroupRenderer for this table. If no cell renderer set, creates
	 * a new instance for the table.
	 * @return a reference to the valueByGroupRenderer
	 */
	protected TableCellRenderer getValueByGroupRenderer() {
		if (this.valueByGroupRenderer == null) {
			this.valueByGroupRenderer = new ValueByGroupRenderer(this);
		}
		return this.valueByGroupRenderer;
	}

	@Override
	public TableCellRenderer getCellRenderer(final int row, final int column) {
		final TableSorter m = (TableSorter) this.getModel();

		final int indexModel = this.convertColumnIndexToModel(column);

		final String sColumnName = m.getColumnName(indexModel);

		if (!ExtendedTableModel.ROW_NUMBERS_COLUMN.equalsIgnoreCase(sColumnName)) {
			if ((!m.isSumCell(row, column)) && m.isGrouped()) {
				final Object o = m.getValueAt(row, indexModel);
				if (o instanceof ArrayList) {
					return this.getGroupCellRenderer();
				} else if (o instanceof ValueByGroup) {
					return this.getValueByGroupRenderer();
				}
			}
		}
		return this.getNoGroupedCellRenderer(row, column);
	}

	/**
	 * Returns the CellRenderer for the specified cell, when the cell is not grouped.
	 * @param rowIndex
	 * @param columnIndex
	 * @return the CellRenderer set to the specified cell
	 */
	public TableCellRenderer getNoGroupedCellRenderer(final int rowIndex, final int columnIndex) {

		final TableSorter m = (TableSorter) this.getModel();

		if (this.ontimizeTable.cellRendererManager != null) {
			final TableCellRenderer renderer = this.ontimizeTable.cellRendererManager.getCellRenderer(this, rowIndex,
					columnIndex);
			if (renderer != null) {
				if (renderer instanceof CellRenderer) {
					((CellRenderer) renderer).setCellRendererColorManager(this.ontimizeTable.cellRendererColorManager);
					((CellRenderer) renderer).setCellRendererFontManager(this.ontimizeTable.cellRendererFontManager);
				}
				return renderer;
			}
		}

		if (m != null) {
			if (m.isSumCell(rowIndex, columnIndex)) {
				if (this.getColumnModel().getColumn(columnIndex).getCellRenderer() instanceof CurrencyCellRenderer) {
					// Configure
					final CurrencyCellRenderer cm = (CurrencyCellRenderer) this.getColumnModel()
							.getColumn(columnIndex)
							.getCellRenderer();
					final TableCellRenderer sumCellRenderer = m.getSumCellRenderer(true);
					((CurrencyCellRenderer) sumCellRenderer).setMaximumFractionDigits(cm.getMaximumFractionDigits());
					((CurrencyCellRenderer) sumCellRenderer).setMinimumFractionDigits(cm.getMinimumFractionDigits());
					((CurrencyCellRenderer) sumCellRenderer).setMaximumIntegerDigits(cm.getMaximumIntegerDigits());
					((CurrencyCellRenderer) sumCellRenderer).setMinimumIntegerDigits(cm.getMinimumIntegerDigits());
					((CurrencyCellRenderer) sumCellRenderer).setFont(this.ontimizeTable.getFont());

					return sumCellRenderer;
				} else {

					final TableCellRenderer sumCellRenderer = m.getSumCellRenderer(false);

					if (this.getColumnModel().getColumn(columnIndex).getCellRenderer() instanceof RealCellRenderer) {
						final RealCellRenderer cm = (RealCellRenderer) this.getColumnModel()
								.getColumn(columnIndex)
								.getCellRenderer();

						((RealCellRenderer) sumCellRenderer).setMaximumFractionDigits(cm.getMaximumFractionDigits());
						((RealCellRenderer) sumCellRenderer).setMinimumFractionDigits(cm.getMinimumFractionDigits());
						((RealCellRenderer) sumCellRenderer).setMaximumIntegerDigits(cm.getMaximumIntegerDigits());
						((RealCellRenderer) sumCellRenderer).setMinimumIntegerDigits(cm.getMinimumIntegerDigits());
					}
					((RealCellRenderer) sumCellRenderer).setFont(this.ontimizeTable.getFont());

					return sumCellRenderer;
				}
			} else {
				return super.getCellRenderer(rowIndex, columnIndex);
			}
		} else {
			return super.getCellRenderer(rowIndex, columnIndex);
		}
	}

	/**
	 * Returns the index of the next visible column.
	 * @param columnIndexView
	 * @param right if true, the result will be the column placed at the right place in the table; if it
	 *        is false, the columns placed at the left side will be chosen
	 * @return
	 */
	protected int getNextVisibleColumn(final int columnIndexView, final boolean right) {
		int columnIndex = columnIndexView;
		if (columnIndex >= 0) {
			if (right) {
				columnIndex++;
			} else {
				columnIndex--;
			}
			if ((columnIndex < 0) || (columnIndex >= this.getColumnCount())) {
				return -1;
			}
			String sName = this.getColumnName(columnIndex);
			while (!this.ontimizeTable.isVisibleColumn(sName)) {
				if (right) {
					if (columnIndex >= (this.getColumnCount() - 1)) {
						break;
					}
					columnIndex++;
				}
				if (!right) {
					if (columnIndex == 0) {
						break;
					}
					columnIndex--;
				}
				if ((columnIndex < 0) || (columnIndex >= this.getColumnCount())) {
					return -1;
				}
				sName = this.getColumnName(columnIndex);
			}
		}
		return columnIndex;
	}

	@Override
	protected void processMouseEvent(final MouseEvent e) {
		if (MouseEvent.MOUSE_PRESSED == e.getID()) {
			this.setRightDirection(false);
		}
		super.processMouseEvent(e);
	}

	/**
	 * Additional keybindings registered for component. </br>
	 * <TABLE BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *
	 * <tr>
	 * <td>Key binding</td>
	 * <td>Action</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_LEFT while cell is editing</td>
	 * <td><i>Move back in cell content</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_DOWN while cell is editing</td>
	 * <td><i>Stops cell edition</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_RIGHT while cell is editing</td>
	 * <td><i>Move forward in cell content</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_RIGHT while cell is editing</td>
	 * <td><i>Stops cell edition</td>
	 * </tr>
	 *
	 *
	 * <tr>
	 * <td>VK_TAB while cell is editing</td>
	 * <td><i>Stops cell edition and moves focus to the next right cell</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_TAB + SHIFT while cell is editing</td>
	 * <td><i>Stops cell edition and moves focus to the next left cell</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_ESCAPE while cell is editing</td>
	 * <td><i>Stops cell edition and moves focus to the next cell</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>VK_DELETE while cell is editing</td>
	 * <td><i>Deletes contents in cell</td>
	 * <tr>
	 *
	 * <tr>
	 * <td>VK_ENTER while cell is editing</td>
	 * <td><i>Stops cell edition</td>
	 * <tr>
	 *
	 * <td>VK_ADD with cell selected</td>
	 * <td><i>Open detail form whether detail form is enabled</td>
	 * </tr>
	 * </TABLE>
	 */
	@Override
	protected boolean processKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition, final boolean pressed) {
		if (keyEvent.isConsumed()) {
			return true;
		}
		this.rightDirection = true;

		if (this.processDirectionalKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processTabKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processEscapeKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processEnterKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (((keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				|| ((keyEvent.getID() == KeyEvent.KEY_TYPED) && (keyEvent.getKeyChar() == KeyEvent.VK_ENTER)))
				&& !this.isEditing()) {
			if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
				if (this.ontimizeTable.isInsertingEnabled()
						&& ((TableSorter) this.getModel()).isInsertingRow(this.getSelectedRow())) {
					this.ontimizeTable.executeInsertRow();
					this.changeSelection(this.getRowCount() - 1, 0, false, false);
					return true;
				}
			}
		} else if (this.processControlCKeyBinding(keyStroke, keyEvent, condition, pressed)) {
			// Ctr+C to make the same action that copy to clipboard button
			return true;
		}

		if (this.processKeyBindingWithFocus(keyStroke, keyEvent, condition, pressed)) {
			return true;
		}

		// Enter code not processed
		if (this.processEnterKeyBinding(keyStroke, keyEvent, condition, pressed)) {
			return true;
		}

		return super.processKeyBinding(keyStroke, keyEvent, condition, pressed);
	}

	protected boolean processDirectionalKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if (this.processDownKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processUpKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processLeftKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		} else if (this.processRightKeyBindingInEdition(keyStroke, keyEvent, condition, pressed)) {
			return true;
		}
		return false;
	}

	protected boolean processDownKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_DOWN) && this.isEditing()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}

			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			if (this.cellEditor != null) {
				final boolean stopCellEditing = this.cellEditor.stopCellEditing();
				if (stopCellEditing) {
					if (this.getRowCount() > (editedRow + 1)) {
						this.selectionModel.setSelectionInterval(editedRow + 1, editedRow + 1);
						this.columnModel.getSelectionModel().setSelectionInterval(editedColumn, editedColumn);
						this.selectionModel.setAnchorSelectionIndex(editedRow + 1);
						this.columnModel.getSelectionModel().setAnchorSelectionIndex(editedColumn);
						if (this.isCellEditable(editedRow + 1, editedColumn)) {
							this.editCellAt(editedRow + 1, editedColumn, keyEvent);
						}
					}
				}
			}
			return true;
		}

		return false;
	}

	protected boolean processUpKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_UP) && this.isEditing()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}
			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			if (this.cellEditor != null) {
				final boolean stopCellEditing = this.cellEditor.stopCellEditing();
				if (stopCellEditing) {
					if (editedRow > 0) {
						this.selectionModel.setSelectionInterval(editedRow - 1, editedRow - 1);
						this.columnModel.getSelectionModel().setSelectionInterval(editedColumn, editedColumn);
						this.selectionModel.setAnchorSelectionIndex(editedRow - 1);
						this.columnModel.getSelectionModel().setAnchorSelectionIndex(editedColumn);
						if (this.isCellEditable(editedRow - 1, editedColumn)) {
							this.editCellAt(editedRow - 1, editedColumn, keyEvent);
						}
					}
				}
			}
			return true;
		}

		return false;
	}

	protected boolean processLeftKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_LEFT) && this.isEditing()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}
			this.setRightDirection(false);
			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			final int col = this.getNextVisibleColumn(editedColumn, this.isRightDirection());
			if (this.cellEditor != null) {
				final boolean stopCellEditing = this.cellEditor.stopCellEditing();
				if (stopCellEditing) {
					if (editedColumn > 0) {
						this.selectionModel.setSelectionInterval(editedRow, editedRow);
						this.columnModel.getSelectionModel().setSelectionInterval(col, col);
						this.selectionModel.setAnchorSelectionIndex(editedRow);
						this.columnModel.getSelectionModel().setAnchorSelectionIndex(col);
						if (this.isCellEditable(editedRow, col)) {
							this.editCellAt(editedRow, col, keyEvent);
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	protected boolean processRightKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) && this.isEditing()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}
			this.setRightDirection(true);
			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			final int col = this.getNextVisibleColumn(editedColumn, this.isRightDirection());
			if (this.cellEditor != null) {
				final boolean stopEditing = this.cellEditor.stopCellEditing();
				if (stopEditing) {
					if (editedColumn < (this.getColumnCount() - 1)) {
						this.selectionModel.setSelectionInterval(editedRow, editedRow);
						this.columnModel.getSelectionModel().setSelectionInterval(col, col);
						this.selectionModel.setAnchorSelectionIndex(editedRow);
						this.columnModel.getSelectionModel().setAnchorSelectionIndex(col);
						if (this.isCellEditable(editedRow, col)) {
							this.editCellAt(editedRow, col, keyEvent);
						}
					}
				}
			}
			return true;
		}

		return false;
	}

	protected boolean processTabKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_TAB) && this.isEditing() && !keyEvent.isShiftDown()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}
			this.setRightDirection(true);
			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			final int col = this.getNextVisibleColumn(editedColumn, this.isRightDirection());
			if (this.cellEditor != null) {
				final boolean stopEditing = this.cellEditor.stopCellEditing();
				if (stopEditing) {
					if (editedColumn < (this.getColumnCount() - 1)) {
						// this.selectionModel.setSelectionInterval(editedRow,
						// editedRow);
						// this.columnModel.getSelectionModel().setSelectionInterval(col,
						// col);
						// this.selectionModel.setAnchorSelectionIndex(editedRow);
						// this.columnModel.getSelectionModel().setAnchorSelectionIndex(col);
						this.changeSelection(editedRow, col, false, false);
						if (this.isCellEditable(editedRow, col)) {
							this.editCellAt(editedRow, col, keyEvent);
						}
					}
				}
			}
			return true;
		} else if ((keyEvent.getKeyCode() == KeyEvent.VK_TAB) && this.isEditing() && keyEvent.isShiftDown()) {
			if (keyEvent.getID() != KeyEvent.KEY_PRESSED) {
				return true;
			}
			this.setRightDirection(false);
			final int editedRow = this.getEditingRow();
			final int editedColumn = this.getEditingColumn();
			final int col = this.getNextVisibleColumn(editedColumn, this.isRightDirection());
			if (this.cellEditor != null) {
				final boolean stopEditing = this.cellEditor.stopCellEditing();
				if (stopEditing) {
					if (editedColumn > 0) {
						this.changeSelection(editedRow, col, false, false);
						if (this.isCellEditable(editedRow, col)) {
							this.editCellAt(editedRow, col, keyEvent);
						}
					}
				}
			}
			return true;
		}

		return false;
	}

	protected boolean processEscapeKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) && this.isEditing()) {
			if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
				return true;
			} else if (keyEvent.getID() == KeyEvent.KEY_RELEASED) {
				if (this.cellEditor != null) {
					// Cancel edition
					this.cellEditor.cancelCellEditing();
					keyEvent.consume();
					return true;
				}
			}
		}
		return false;
	}

	protected boolean processControlCKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_C)
				&& ((keyEvent.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK)) != 0)) {
			// Ctr+C to make the same action that copy to clipboard button
			this.ontimizeTable.copySelection();
			return true;
		}
		return false;
	}

	protected boolean processEnterKeyBindingInEdition(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_ENTER) && this.isEditing()) {
			if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
				if (this.cellEditor != null) {
					this.cellEditor.stopCellEditing();
					return true;
				}
			} else {
				return true;
			}
		}
		return false;
	}

	protected boolean processEnterKeyBinding(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition, final boolean pressed) {
		final int code = keyEvent.getKeyCode();
		if ((code == KeyEvent.VK_ENTER)
				|| ((keyEvent.getID() == KeyEvent.KEY_TYPED) && (keyEvent.getKeyChar() == KeyEvent.VK_ENTER))) {
			return true;
		}

		return false;
	}


	protected boolean processKeyBindingWithFocus(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) && this.hasFocus()) {

			if ((keyEvent != null) && (keyEvent.getID() == KeyEvent.KEY_RELEASED)) {
				final int code = keyEvent.getKeyCode();
				if (code == KeyEvent.VK_ENTER) {
					keyEvent.consume();
					if (this.getSelectedRowCount() != 1) {
						return true;
					} else {
						this.ontimizeTable.detail(keyEvent);
					}
					return true;
				} else if (code == KeyEvent.VK_ADD) {
					keyEvent.consume();
					if (this.ontimizeTable.buttonPlus.isEnabled()) {
						this.ontimizeTable.openInsertDetailForm();
					}
					return true;
				}
			}

			if ((keyEvent.getID() == KeyEvent.KEY_TYPED) && !this.isEditing()) {
				// Select the row if it is not empty and the table is sorted
				if (Character.isLetterOrDigit(keyEvent.getKeyChar()) || Character.isSpaceChar(keyEvent.getKeyChar())) {

					this.textToFind.append(keyEvent.getKeyChar());

					if (!this.ontimizeTable.isEmpty()) {
						if (this.ontimizeTable.isSorted()) {
							// Get the sort column values and select the first
							// match
							final int sortColumn = this.ontimizeTable.getViewOrderColumnIndex();
							final String sText = this.textToFind.toString();
							final Collator c = Collator.getInstance();
							final int index = this.getNextIndex(sortColumn, sText, c);
							if (index >= 0) {
								this.setRowSelectionInterval(index, index);
								this.scrollRectToVisible(this.getCellRect(index, sortColumn, true));
							}

							this.getTextTip()
							.show(this.ontimizeTable.scrollPane, 2, 10 + this.getTableHeader().getHeight(),
									this.textToFind.toString());
							keyEvent.consume();
							return true;
						}
					}
				}
			} else if ((this.textToFind.length() > 0)
					&& ((keyEvent.getKeyCode() == KeyEvent.VK_DELETE)
							|| (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE))
					&& (keyEvent
							.getID() == KeyEvent.KEY_RELEASED)) {
				this.textToFind.delete(this.textToFind.length() - 1, this.textToFind.length());
				this.getTextTip()
				.show(this.ontimizeTable.scrollPane, 2, 10 + this.getTableHeader().getHeight(),
						this.textToFind.toString());
				keyEvent.consume();
				return true;
			} else if (this.processEscapeKeyBindingWithFocus(keyStroke, keyEvent, condition, pressed)) {
				return true;
			}
		}
		return false;
	}

	protected int getNextIndex(final int sortColumn, final String sText, final Collator c) {
		int index = -1;
		if (this.ontimizeTable.isAscending()) {
			for (int i = 0; i < this.getRowCount(); i++) {
				final Object oValue = this.getValueAt(i, sortColumn);
				if (oValue == null) {
					continue;
				}

				if (c.compare(sText, oValue.toString()) <= 0) {
					index = i;
					break;
				} else {
					index = i;
				}
			}
		} else {
			for (int i = 0; i < this.getRowCount(); i++) {
				final Object oValue = this.getValueAt(i, sortColumn);
				if (oValue == null) {
					continue;
				}
				if (c.compare(sText, oValue.toString()) >= 0) {
					index = i;
					break;
				} else {
					index = i;
				}
			}
		}
		return index;
	}

	protected boolean processEscapeKeyBindingWithFocus(final KeyStroke keyStroke, final KeyEvent keyEvent, final int condition,
			final boolean pressed) {
		if ((keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) && (keyEvent.getID() == KeyEvent.KEY_RELEASED)) {
			if (this.getTextTip().isVisible()) {
				this.getTextTip().setVisible(false);
				this.textToFind.delete(0, this.textToFind.length());
				keyEvent.consume();
				return true;
			}
		}
		return false;
	}

	/**
	 * Sets the table in 'autofit' mode, which implies that the rows will adapt their height to the
	 * contents inside them.
	 * @param autofit
	 */
	public void setFitRowsHeight(final boolean autofit) {
		this.fitRowsHeight = autofit;
	}

	public boolean isFitRowsHeight() {
		return this.fitRowsHeight;
	}

	@Override
	public void setColumnModel(final TableColumnModel columnModel) {
		super.setColumnModel(columnModel);
	}

	public List getVisibleColumns() {
		return this.visibleColumns;
	}

	@Override
	public void tableChanged(final TableModelEvent e) {
		if ((e != null) && (e.getFirstRow() == ExtendedTableModelEvent.HIDDEN_ROW)
				&& (e.getLastRow() == ExtendedTableModelEvent.HIDDEN_ROW)) {
			this.clearSelection();
		} else {
			super.tableChanged(e);
		}
		// Set the column identifiers. Column names and identifiers are the same
		for (int i = 0; i < this.getColumnCount(); i++) {
			final TableColumn tableColumn = this.getColumnModel().getColumn(i);
			final int modelIndex = this.convertColumnIndexToModel(i);
			tableColumn.setIdentifier(((TableSorter) this.getModel()).getColumnIdentifier(modelIndex));
		}
		// Set the sum rows renderer if it is necessary
		if ((e == null) || (e.getFirstRow() == TableModelEvent.HEADER_ROW)) {
			if ((this.visibleColumns != null) && !this.visibleColumns.isEmpty()) {
				if (this.visibleColumns != null) {
					for (int i = 1; i < this.getColumnCount(); i++) {
						final String col = this.getColumnName(i);
						if (!this.visibleColumns.contains(col)) {
							final TableColumn tc = this.getColumn(col);
							tc.setMinWidth(0);
							tc.setWidth(0);
							tc.setMaxWidth(0);
						}
					}
				}
			}

			final TableColumn tableColumn = this.getColumnModel().getColumn(0);
			if (ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(tableColumn.getHeaderValue())) {
				tableColumn.setWidth(0);
				tableColumn.setPreferredWidth(0);
				tableColumn.setMaxWidth(0);
				tableColumn.setMinWidth(0);
				tableColumn.setResizable(false);
			}
		}
		if ((e == null) || (e.getFirstRow() != e.getLastRow()) || (e.getFirstRow() == TableModelEvent.HEADER_ROW)
				|| (e.getType() == TableModelEvent.INSERT)) {
			if (this.isFitRowsHeight()) {
				this.fitRowHeight();
			}
		}
	}

	/**
	 * Adjusts the height of all rows to the contents inside them.
	 */
	protected void fitRowHeight() {
		try {
			this.rowHeightSet = true;
			for (int i = 0; i < this.getRowCount(); i++) {
				int hMax = this.ontimizeTable.getMinRowHeight();

				List visibleColumns = this.ontimizeTable.getVisibleColumns();
				if ((visibleColumns == null) || visibleColumns.isEmpty()) {
					visibleColumns = this.ontimizeTable.getAttributeList();
				}

				for (int j = 0; j < this.getColumnCount(); j++) {
					final String columnName = this.getColumnName(j);
					if (visibleColumns.contains(columnName)) {
						if (ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(this.getColumnName(j))) {
							continue;
						}
						final Object v = this.getValueAt(i, j);
						final int iWidth = this.getColumnModel().getColumn(j).getWidth();
						final TableCellRenderer r = this.getCellRenderer(i, j);
						if (!((r != null) && (r instanceof MemoCellRenderer) && (iWidth < 20))) {
							if ((r instanceof MemoCellRenderer) || (!(r instanceof CellRenderer))) {
								final Component c = r.getTableCellRendererComponent(this, v, false, false, i, j);
								c.setSize(iWidth, 50);
								final int h = c.getPreferredSize().height;
								hMax = Math.max(hMax, h);
							} else {
								hMax = Math.max(hMax, ((CellRenderer) r).getPreferredSize().height);
							}
						}
					}
				}
				this.setRowHeight(i, hMax);
			}
		} catch (final Exception ex) {
			EJTable.logger.trace(null, ex);
		} finally {
			this.rowHeightSet = false;
			this.resizeAndRepaint();
		}
	}

	protected TipScroll getTextTip() {
		if (this.textTip == null) {
			this.textTip = new TipScroll(true);
		}

		return this.textTip;
	}

	@Override
	protected TableColumnModel createDefaultColumnModel() {
		return new DefaultTableColumnModel() {
			@Override
			public void moveColumn(final int columnIndex, final int newIndex) {
				// Column 0 es la columna del numero de filas.
				if (newIndex == 0) {
					return;
				}

				final int blockedColumnIndex = ontimizeTable.getBlockedColumnIndex();
				// Check newIndex is in blocked model and columnIndex is in main model
				if (blockedColumnIndex >= newIndex && columnIndex > blockedColumnIndex) {
					return;
				}
				super.moveColumn(columnIndex, newIndex);
			}
		};
	}

}
