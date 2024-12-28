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

import com.ontimize.gui.field.CheckDataField;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Use {@link BooleanCellEditor}
 *
 * @deprecated
 */
@Deprecated
public class BooleanTableCellEditor extends DefaultDataTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(BooleanTableCellEditor.class);

    protected CheckDataField checkDataField = new CheckDataField(new Hashtable());

    public BooleanTableCellEditor(EntityReferenceLocator referenceLocator, Table t) {
        super(referenceLocator, t);
        KeyAdapter kl = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    BooleanTableCellEditor.this.stopCellEditing();
                }
            }
        };
        this.checkDataField.getDataField().addKeyListener(kl);
        FocusAdapter fl = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                BooleanTableCellEditor.this.cancelCellEditing();
            }
        };
        this.checkDataField.getDataField().addFocusListener(fl);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (com.ontimize.gui.ApplicationManager.DEBUG) {
            if (value != null) {
                BooleanTableCellEditor.logger.debug("Requested editor component for cell: " + value.toString());
            } else {
                BooleanTableCellEditor.logger.debug("Requested editor component for cell: NULL ");
            }
        }
        this.editedTable = table;
        this.editedRow = row;
        this.editedColumn = column;
        if (table != null) {
            this.checkDataField.deleteData();
            this.checkDataField.setValue(value);
            this.editor = this.checkDataField.getDataField();
            this.editor.setBorder(new LineBorder(Color.red));
            this.currentEditor = this.checkDataField;
            return this.editor;
        } else {
            this.currentEditor = null;
            return null;
        }
    }

}
