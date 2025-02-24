package com.ontimize.gui.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.JTable;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Use {@link IntegerCellEditor}
 *
 * @deprecated
 */
@Deprecated
public class IntegerDefaultDataTableCellEditor extends DefaultDataTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(IntegerDefaultDataTableCellEditor.class);

    protected IntegerDataField integerField = new IntegerDataField(new Hashtable());

    public IntegerDefaultDataTableCellEditor(EntityReferenceLocator locator, Table table) {
        super(locator, table);
        KeyAdapter kl = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    IntegerDefaultDataTableCellEditor.this.stopCellEditing();
                }
            }
        };
        this.integerField.getDataField().addKeyListener(kl);
        FocusAdapter fl = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                IntegerDefaultDataTableCellEditor.this.cancelCellEditing();
            }
        };
        this.integerField.getDataField().addFocusListener(fl);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (com.ontimize.gui.ApplicationManager.DEBUG) {
            if (value != null) {
                IntegerDefaultDataTableCellEditor.logger
                    .debug("Requested component editor for cell: " + value.toString());
            } else {
                IntegerDefaultDataTableCellEditor.logger.debug("Requested component editor for cell: null");
            }
        }
        this.editedTable = table;
        this.editedRow = row;
        this.editedColumn = column;
        if (table != null) {
            this.integerField.deleteData();
            this.integerField.setValue(value);
            this.editor = this.integerField.getDataField();
            this.editor.setBorder(new LineBorder(Color.red));
            this.currentEditor = this.integerField;
            return this.editor;
        } else {
            this.currentEditor = null;
            return null;
        }
    }

}
