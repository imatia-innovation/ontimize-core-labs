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

import com.ontimize.gui.field.RealDataField;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Use {@link RealCellEditor}
 *
 * @deprecated
 */
@Deprecated
public class RealTableCellEditor extends DefaultDataTableCellEditor {

    private static final Logger logger = LoggerFactory.getLogger(RealTableCellEditor.class);

    protected RealDataField realDataField = new RealDataField(new Hashtable());

    public RealTableCellEditor(EntityReferenceLocator buscador, Table t) {
        super(buscador, t);
        KeyAdapter kl = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    RealTableCellEditor.this.stopCellEditing();
                }
            }
        };
        this.realDataField.getDataField().addKeyListener(kl);
        FocusAdapter fl = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                RealTableCellEditor.this.cancelCellEditing();
            }
        };
        this.realDataField.getDataField().addFocusListener(fl);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (com.ontimize.gui.ApplicationManager.DEBUG) {
            if (value != null) {
                RealTableCellEditor.logger.debug("Requested editor component for cell: " + value.toString());
            } else {
                RealTableCellEditor.logger.debug("Requested editor component for cell: ");
            }
        }
        this.editedTable = table;
        this.editedRow = row;
        this.editedColumn = column;
        if (table != null) {
            this.realDataField.deleteData();
            this.realDataField.setValue(value);
            this.editor = this.realDataField.getDataField();
            this.editor.setBorder(new LineBorder(Color.red));
            this.currentEditor = this.realDataField;
            return this.editor;
        } else {
            this.currentEditor = null;
            return null;
        }
    }

}
