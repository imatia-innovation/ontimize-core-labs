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

import com.ontimize.gui.field.DateDataField;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Use {@link DateCellEditor}
 *
 * @deprecated
 */
@Deprecated
public class DateTableCellEditor extends DefaultDataTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(DateTableCellEditor.class);

    protected DateDataField dateDataField = new DateDataField(new Hashtable());

    public DateTableCellEditor(EntityReferenceLocator locator, Table table) {
        super(locator, table);
        KeyAdapter kl = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    DateTableCellEditor.this.stopCellEditing();
                }
            }
        };
        this.dateDataField.getDataField().addKeyListener(kl);
        FocusAdapter fl = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                DateTableCellEditor.this.cancelCellEditing();
            }
        };
        this.dateDataField.getDataField().addFocusListener(fl);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (com.ontimize.gui.ApplicationManager.DEBUG) {
            if (value != null) {
                DateTableCellEditor.logger.debug("Requested editor component for cell: " + value.toString());
            } else {
                DateTableCellEditor.logger.debug("Requested editor component for cell: ");
            }
        }
        this.editedTable = table;
        this.editedRow = row;
        this.editedColumn = column;
        if (table != null) {
            this.dateDataField.deleteData();
            this.dateDataField.setValue(value);
            this.editor = this.dateDataField.getDataField();
            this.editor.setBorder(new LineBorder(Color.red));
            this.currentEditor = this.dateDataField;
            return this.editor;
        } else {
            this.currentEditor = null;
            return null;
        }
    }

}
