package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.io.Serializable;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.table.blocked.BlockedTable;

public class SumRowTable extends JTable {

    private static final Logger logger = LoggerFactory.getLogger(SumRowTable.class);

    protected JTable dataTable;

    public SumRowTable(JTable table) {
        this.dataTable = table;
        this.dataTable.getColumnModel().addColumnModelListener(new TableColumnModelListener() {

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                TableColumn removeColumn = SumRowTable.this.getColumnModel().getColumn(e.getFromIndex());
                SumRowTable.this.getColumnModel().removeColumn(removeColumn);
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
                int toIndex = e.getToIndex();
                int fromIndex = e.getFromIndex();
                SumRowTable.this.getColumnModel().moveColumn(fromIndex, toIndex);
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
                SumRowTable.this.adjustColumnWidths();
            }

            @Override
            public void columnAdded(TableColumnModelEvent e) {
                int toIndex = e.getToIndex();
                TableColumn newSourceColumn = ((TableColumnModel) e.getSource()).getColumn(toIndex);

                TableColumn newColumn = new TableColumn(newSourceColumn.getModelIndex());
                newColumn.setHeaderValue(newSourceColumn.getHeaderValue());
                newColumn.setIdentifier(newSourceColumn.getIdentifier());
                newColumn.setCellRenderer(newSourceColumn.getCellRenderer());
                SumRowTable.this.getColumnModel().addColumn(newColumn);
                adjustColumnWidths();
            }
        });

        FontMetrics fontMetrics = this.getFontMetrics(this.getFont());

        this.setFillsViewportHeight(true);
        this.setRowSelectionAllowed(false);
    }

    protected Table getTable() {
        if (this.dataTable instanceof EJTable) {
            return ((EJTable) this.dataTable).ontimizeTable;
        } else if (this.dataTable instanceof BlockedTable) {
            return ((BlockedTable) this.dataTable).getJTable().ontimizeTable;
        }
        Table table = (Table) SwingUtilities.getAncestorOfClass(Table.class, this.dataTable);
        return table;
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return super.getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }

    public void adjustColumnWidths() {
        if (this.dataTable != null) {
            TableColumnModel dataTableModelColumn = this.dataTable.getColumnModel();
            int columnCount = this.dataTable.getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                if (this.getColumnModel().getColumnCount() <= i) {
                    SumRowTable.logger.debug(String.valueOf(i));
                }
                TableColumn tC = this.getColumnModel().getColumn(i);
                int width = dataTableModelColumn.getColumn(i).getWidth();
                tC.setMaxWidth(width);
                tC.setMinWidth(0);
                tC.setWidth(width);
                tC.setPreferredWidth(width);

            }
        }
    }

    @Override
    public void createDefaultColumnsFromModel() {
        if (this.dataTable != null) {
            TableModel m = this.getModel();
            if (m != null) {
                // Remove any current columns
                TableColumnModel cm = this.getColumnModel();
                while (cm.getColumnCount() > 0) {
                    cm.removeColumn(cm.getColumn(0));
                }

                TableColumnModel datacm = this.dataTable.getColumnModel();
                // Create new columns from the data model info
                for (int i = 0; i < datacm.getColumnCount(); i++) {
                    TableColumn newColumn = new TableColumn(datacm.getColumn(i).getModelIndex());
                    newColumn.setHeaderValue(datacm.getColumn(i).getHeaderValue());
                    newColumn.setIdentifier(datacm.getColumn(i).getIdentifier());
                    this.addColumn(newColumn);
                }
            }
        } else {
            super.createDefaultColumnsFromModel();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e!=null && (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.DELETE)){
            resizeAndRepaint();
            return;
        }
        super.tableChanged(e);
        if (this.dataTable != null && this.getTable() != null) {
            if ((e == null) || (e.getFirstRow() == TableModelEvent.HEADER_ROW)) {
                List visibleColumns = getTable().getVisibleColumns();
                if ((visibleColumns != null) && !visibleColumns.isEmpty()) {
                    for (int i = 1; i < this.getColumnCount(); i++) {
                        TableColumn tC = this.getColumnModel().getColumn(i);
                        String col = (String) tC.getHeaderValue();
                        if (!visibleColumns.contains(col)) {
                            tC.setMinWidth(0);
                            tC.setWidth(0);
                            tC.setMaxWidth(0);
                        }
                    }
                }
            }
        }
    }

    public String getCellValueAsString(int row, int column) {
        String sText = null;
        TableColumn tc = this.getColumnModel().getColumn(column);
        Object oValue = this.getValueAt(row, column);
        TableCellRenderer r = this.getCellRenderer(row, column);
        Component c = r.getTableCellRendererComponent(this, oValue, false, false, row, column);
        if (c instanceof JLabel) {
            sText = ((JLabel) c).getText();
        } else if (c instanceof JTextComponent) {
            sText = ((JTextComponent) c).getText();
        } else if (c instanceof JCheckBox) {
            if (((JCheckBox) c).isSelected()) {
                sText = ApplicationManager.getTranslation("Yes");
            } else {
                sText = ApplicationManager.getTranslation("No");
            }
        } else {
            sText = "";
            if (oValue != null) {
                sText = oValue.toString();
            }
        }
        return sText;
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int columnIndex) {
        SumRowTableModel m = (SumRowTableModel) this.getModel();
        int indexModel = this.convertColumnIndexToModel(columnIndex);
        String sColumnName = m.getColumnName(indexModel);

        if (!ExtendedTableModel.ROW_NUMBERS_COLUMN.equalsIgnoreCase(sColumnName)) {

            if (this.getColumnModel().getColumn(columnIndex).getCellRenderer() instanceof CurrencyCellRenderer) {
                // Configure
                CurrencyCellRenderer cm = (CurrencyCellRenderer) this.getColumnModel()
                    .getColumn(columnIndex)
                    .getCellRenderer();
                TableCellRenderer sumCellRenderer = m.getSumCellRenderer(true);
                ((CurrencyCellRenderer) sumCellRenderer).setMaximumFractionDigits(cm.getMaximumFractionDigits());
                ((CurrencyCellRenderer) sumCellRenderer).setMinimumFractionDigits(cm.getMinimumFractionDigits());
                ((CurrencyCellRenderer) sumCellRenderer).setMaximumIntegerDigits(cm.getMaximumIntegerDigits());
                ((CurrencyCellRenderer) sumCellRenderer).setMinimumIntegerDigits(cm.getMinimumIntegerDigits());
                ((CurrencyCellRenderer) sumCellRenderer).setFont(this.getTable().getFont());

                return sumCellRenderer;
            } else {

                TableCellRenderer sumCellRenderer = m.getSumCellRenderer(false);
                if (this.getColumnModel().getColumn(columnIndex).getCellRenderer() instanceof RealCellRenderer) {
                    RealCellRenderer cm = (RealCellRenderer) this.getColumnModel()
                        .getColumn(columnIndex)
                        .getCellRenderer();

                    ((RealCellRenderer) sumCellRenderer).setMaximumFractionDigits(cm.getMaximumFractionDigits());
                    ((RealCellRenderer) sumCellRenderer).setMinimumFractionDigits(cm.getMinimumFractionDigits());
                    ((RealCellRenderer) sumCellRenderer).setMaximumIntegerDigits(cm.getMaximumIntegerDigits());
                    ((RealCellRenderer) sumCellRenderer).setMinimumIntegerDigits(cm.getMinimumIntegerDigits());
                }
                ((RealCellRenderer) sumCellRenderer).setFont(this.getTable().getFont());

                return sumCellRenderer;
            }
        } else {
            return super.getCellRenderer(row, columnIndex);
        }
    }

    public static class SumRowBorder implements Border, Serializable {

        protected Border parentBorder;

        protected Insets newInsets;

        public SumRowBorder(Border parentBorder, Insets newInsets) {
            this.parentBorder = parentBorder;
            this.newInsets = newInsets;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            if (this.parentBorder != null) {
                this.parentBorder.paintBorder(c, g, x, y, width, height);
            }
        }

        @Override
        public Insets getBorderInsets(Component c) {
            if (this.parentBorder != null) {
                Insets result = this.parentBorder.getBorderInsets(c);
                if (this.newInsets != null) {
                    if (this.newInsets.top == 0) {
                        result.top = 0;
                    }
                    if (this.newInsets.bottom == 0) {
                        result.bottom = 0;
                    }
                    if (this.newInsets.left == 0) {
                        result.left = 0;
                    }
                    if (this.newInsets.right == 0) {
                        result.right = 0;
                    }
                }

                return result;
            }
            return new Insets(0, 0, 0, 0);
        }

        @Override
        public boolean isBorderOpaque() {
            if (this.parentBorder != null) {
                return this.parentBorder.isBorderOpaque();
            }
            return false;
        }

    }

}
