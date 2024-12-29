package com.ontimize.report.listeners;

import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.report.DefaultReportDialog;
import com.ontimize.report.ReportUtils;
import com.ontimize.report.SelectableItemOrder;
import com.ontimize.report.columns.ColumnConfigurationWindow;
import com.ontimize.report.item.SelectableConfigItemsListCellRenderer;
import com.ontimize.report.item.SelectableItem;
import com.ontimize.report.item.SelectableMultipleItem;

public class ColumnSelectionListener extends MouseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ColumnSelectionListener.class);

    DefaultReportDialog reportDialog;

    protected ColumnConfigurationWindow columnConfiguration;

    public ColumnSelectionListener(DefaultReportDialog reportDialog) {
        this.reportDialog = reportDialog;
    }

    private int[] removeValue(int value) {
        List l = new ArrayList();

        int[] out = new int[l.size()];
        for (int i = 0, a = l.size(); i < a; i++) {
            out[i] = ((Integer) l.get(i)).intValue();
        }
        return out;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getModifiers() == InputEvent.META_MASK) {
            // Contextual menu
            return;
        }

        if ((e.getSource() instanceof JList)
                && (((JList) e.getSource()).getCellRenderer() instanceof SelectableConfigItemsListCellRenderer)) {
            JList list = (JList) e.getSource();
            SelectableConfigItemsListCellRenderer renderer = (SelectableConfigItemsListCellRenderer) list
                .getCellRenderer();

            int width = list.getWidth();
            if (e.getX() > (width - renderer.getButtonWidth())) {
                int index = this.reportDialog.getPrintingColumnList().locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                SelectableItem it = (SelectableItem) this.reportDialog.getPrintingColumnList()
                    .getModel()
                    .getElementAt(index);
                if (this.columnConfiguration == null) {
                    Window window = SwingUtilities.getWindowAncestor(list);
                    if (window instanceof Frame) {
                        this.columnConfiguration = new ColumnConfigurationWindow((Frame) window,
                                "reportdialog.columnconfiguration", ApplicationManager.getApplicationBundle(),
                                this.reportDialog);
                    } else if (window instanceof Dialog) {
                        this.columnConfiguration = new ColumnConfigurationWindow((Dialog) window,
                                "reportdialog.columnconfiguration", ApplicationManager.getApplicationBundle(),
                                this.reportDialog);
                    }
                }
                this.columnConfiguration.showColumnConfigurator(it.getText());
                return;
            }
        }

        if (e.getX() > ReportUtils.LIST_MOUSE_X_MAX) {
            return;
        }
        this.reportDialog.getPrintingColumnList().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            int index = this.reportDialog.getPrintingColumnList().locationToIndex(e.getPoint());
            if (index < 0) {
                return;
            }
            SelectableItem it = (SelectableItem) this.reportDialog.getPrintingColumnList()
                .getModel()
                .getElementAt(index);
            SelectableItemOrder itOrder = new SelectableItemOrder(it.getText());

            if (this.reportDialog.getOrderCols() != null) {
                if (it.isSelected()) {
                    this.reportDialog.getOrderCols().remove(itOrder);
                } else {
                    String sName = it.getText();
                    for (int i = 0, a = this.reportDialog.getGroupList().getModel().getSize(); i < a; i++) {

                        Object obj = this.reportDialog.getGroupList().getModel().getElementAt(i);

                        if (obj instanceof SelectableItem) {
                            if (((SelectableItem) this.reportDialog.getGroupList().getModel().getElementAt(i)).getText()
                                .equals(sName)
                                    && !((SelectableItem) this.reportDialog.getGroupList().getModel().getElementAt(i))
                                        .isSelected()) {
                                this.reportDialog.getOrderCols().add(itOrder);
                                break;
                            }
                        }
                        if (obj instanceof SelectableMultipleItem) {

                            List v = ((SelectableMultipleItem) this.reportDialog.getGroupList()
                                .getModel()
                                .getElementAt(i)).getItemList();
                            boolean bSome = false;
                            for (int j = 0, b = v.size(); j < b; j++) {
                                if (sName.equals(((SelectableItem) v.get(j)).getText())) {
                                    bSome = true;
                                    break;
                                }
                            }
                            if (bSome) {
                                break;
                            }
                        }

                    }
                }
            }

            boolean bWillBeSelected = !it.isSelected();

            it.setSelected(bWillBeSelected);

            Rectangle rect = this.reportDialog.getPrintingColumnList().getCellBounds(index, index);
            this.reportDialog.getPrintingColumnList().repaint(rect);

            this.reportDialog.updateReport();
        } catch (Exception ex) {
            ColumnSelectionListener.logger.error(ex.getMessage(), ex);
        } finally {
            this.reportDialog.getPrintingColumnList().setCursor(Cursor.getDefaultCursor());

        }
        DefaultReportDialog.checkListStatusButtons(this.reportDialog.getPrintingColumnList(),
                this.reportDialog.getAllUpButton(), this.reportDialog.getUpButton(),
                this.reportDialog.getDownButton(), this.reportDialog.getAllDownButton());
    }

};
