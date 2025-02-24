package com.ontimize.report.listeners;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.report.DefaultReportDialog;
import com.ontimize.report.ReportUtils;
import com.ontimize.report.SelectableItemOrder;
import com.ontimize.report.item.SelectableDateGroupItem;
import com.ontimize.report.item.SelectableItem;
import com.ontimize.report.item.SelectableMultipleItem;

public class GroupColumnListener extends MouseAdapter {

    private static final Logger logger = LoggerFactory.getLogger(GroupColumnListener.class);

    public DefaultReportDialog reportDialog;

    public GroupColumnListener(DefaultReportDialog reportDialog) {
        this.reportDialog = reportDialog;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        int index = this.reportDialog.getGroupList().locationToIndex(e.getPoint());

        if (SwingUtilities.isRightMouseButton(e)) {
            if (e.getSource() instanceof JList) {
                if (index < 0) {
                    return;
                }
                if (((JList) e.getSource()).getModel().getElementAt(index) instanceof SelectableDateGroupItem) {
                    // TODO Change getSuperClass ->
                    // isAssignableFrom(java.util.Date.class) returned false
                    // with java.sql.Timestamp
                    if (this.reportDialog
                        .getModel()
                        .getColumnClass(this.reportDialog
                            .getColumnIndex(
                                    ((SelectableDateGroupItem) ((JList) e.getSource()).getModel().getElementAt(index))
                                        .getText(),
                                    this.reportDialog.getModel()))
                        .getSuperclass() == java.util.Date.class) {
                        this.reportDialog.getGroupByDatePopup().show((Component) e.getSource(), e.getX(), e.getY());
                    }
                }
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getModifiers() == InputEvent.META_MASK) {
            if (this.reportDialog.getGroupList().getSelectedIndices().length < 2) {
                return;
            }
            this.reportDialog.getGroupPopup().show((Component) e.getSource(), e.getX(), e.getY());
            return;
        }

        if (e.getX() > ReportUtils.LIST_MOUSE_X_MAX) {
            return;
        }

        this.reportDialog.getGroupList().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            int index = this.reportDialog.getGroupList().locationToIndex(e.getPoint());
            if (index < 0) {
                return;
            }
            Object o = this.reportDialog.getGroupList().getModel().getElementAt(index);

            if (o instanceof SelectableItem) {

                SelectableItem it = (SelectableItem) this.reportDialog.getGroupList().getModel().getElementAt(index);
                SelectableItemOrder itOrder = new SelectableItemOrder(it.getText());

                boolean bWillBeSelected = !it.isSelected();
                if (this.reportDialog.getOrderCols() != null) {
                    if (!it.isSelected()) {
                        this.reportDialog.getOrderCols().remove(itOrder);
                    } else {
                        String s = it.getText();
                        for (int i = 0, a = this.reportDialog.getPrintingColumnList()
                            .getModel()
                            .getSize(); i < a; i++) {
                            if (((SelectableItem) this.reportDialog.getPrintingColumnList().getModel().getElementAt(i))
                                .getText()
                                .equals(s)) {
                                if (((SelectableItem) this.reportDialog.getPrintingColumnList()
                                    .getModel()
                                    .getElementAt(i)).isSelected()) {
                                    this.reportDialog.getOrderCols().add(itOrder);

                                }
                                break;
                            }
                        }
                    }
                }
                if (!(o instanceof SelectableDateGroupItem)) {
                    it.setSelected(bWillBeSelected);
                }
                Rectangle rect = this.reportDialog.getGroupList().getCellBounds(index, index);
                this.reportDialog.getGroupList().repaint(rect);

            } else if (o instanceof SelectableMultipleItem) {
                SelectableMultipleItem it = (SelectableMultipleItem) this.reportDialog.getGroupList()
                    .getModel()
                    .getElementAt(index);
                List vValues = it.getItemList();
                for (int i = 0; i < vValues.size(); i++) {
                    SelectableItem actual = (SelectableItem) vValues.get(i);
                    String s = actual.getText();
                    ((DefaultListModel) this.reportDialog.getGroupList().getModel()).add(index, actual);
                    if (!actual.isSelected()) {
                        for (int j = 0, a = this.reportDialog.getPrintingColumnList()
                            .getModel()
                            .getSize(); j < a; j++) {
                            if (((SelectableItem) this.reportDialog.getPrintingColumnList().getModel().getElementAt(j))
                                .getText()
                                .equals(s)) {
                                if (((SelectableItem) this.reportDialog.getPrintingColumnList()
                                    .getModel()
                                    .getElementAt(j)).isSelected()) {
                                    this.reportDialog.getOrderCols().add(new SelectableItemOrder(it.getText()));
                                }
                                break;
                            }
                        }
                    }
                    ((DefaultListModel) this.reportDialog.getGroupList().getModel()).removeElement(it);
                }
            }
            this.reportDialog.updateReport();
        } catch (Exception ex) {
            GroupColumnListener.logger.error(ex.getMessage(), ex);
        } finally {
            this.reportDialog.getGroupList().setCursor(Cursor.getDefaultCursor());
            DefaultReportDialog.checkListStatusButtons(this.reportDialog.getGroupList(),
                    this.reportDialog.getAllUpGroupButton(), this.reportDialog.getUpGroupButton(),
                    this.reportDialog.getDownGroupButton(), this.reportDialog.getAllDownGroupButton());
        }
    }

};
