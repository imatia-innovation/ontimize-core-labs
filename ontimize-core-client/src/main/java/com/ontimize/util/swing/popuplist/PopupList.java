package com.ontimize.util.swing.popuplist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;

public abstract class PopupList extends JList {

    public static final String POPUPLIST_DELETE_TEMPLATE = "popuplist.delete_template";

    public static final String POPUPLIST_SAVE_TEMPLATE = "popuplist.save_template";

    public static final String POPUPLIST_CHECK = "popuplist.check";

    public static final String POPUPLIST_PRIVATE = "popuplist.private";

    private JPopupMenu popup = null;

    protected JLabel emptyLabel = null;

    private JLabel infoLabel = null;

    protected JPanel southPanel = new JPanel(new GridLayout(0, 1));

    private JScrollPane scroll = null;

    protected ResourceBundle bundle = null;

    protected Form form;

    protected boolean enabledPrivateTemplates;

    public static Border defaultPopupBorder = new LineBorder(Color.black);

    protected static class MouseHandler extends MouseAdapter implements MouseMotionListener {

        @Override
        public void mouseMoved(MouseEvent e) {
            Object o = e.getSource();
            if (o instanceof PopupList) {
                PopupList list = (PopupList) o;
                int i = list.locationToIndex(e.getPoint());
                if (i >= 0) {
                    list.setSelectedIndex(i);
                    list.repaint();
                }
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            Object o = e.getSource();
            if (o instanceof PopupList) {
                PopupList list = (PopupList) o;
                int i = list.getSelectedIndex();
                if (i >= 0) {
                    // list.removeSelectionInterval(i,i);
                    list.repaint();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

    }

    public PopupList(ResourceBundle res, boolean bPrivateTemplates) {
        this.enabledPrivateTemplates = bPrivateTemplates;
        this.bundle = res;
        this.installListModel();
        this.installCellRenderer();
        this.setEnabled(true);
        this.setBorder(null);
        this.installMouseHandler();
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MouseHandler handler = new MouseHandler();
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
    }

    public PopupList(ResourceBundle res) {
        this.bundle = res;
        this.installListModel();
        this.installCellRenderer();
        this.setEnabled(true);
        this.setBorder(null);
        this.installMouseHandler();
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MouseHandler handler = new MouseHandler();
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
    }

    public PopupList(ResourceBundle res, Form form, boolean bPrivateTemplates) {
        this.enabledPrivateTemplates = bPrivateTemplates;
        this.form = form;
        this.bundle = res;
        this.installListModel();
        this.installCellRenderer();
        this.setEnabled(true);
        this.setBorder(null);
        this.installMouseHandler();
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        MouseHandler handler = new MouseHandler();
        this.addMouseListener(handler);
        this.addMouseMotionListener(handler);
    }

    protected abstract void installListModel();

    protected abstract void installCellRenderer();

    protected abstract void installMouseHandler();

    protected void addPopupItem(PopupItem item) {
        this.southPanel.add(item);
    }

    @Override
    public int getVisibleRowCount() {
        if (this.getModel().getSize() > 10) {
            return 20;
        } else {
            return this.getModel().getSize();
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.popup != null) {
            this.popup.setVisible(visible);
        }
    }

    public void createPopup() {
        if (this.popup == null) {
            this.popup = new JPopupMenu();
            this.popup.setLayout(new BorderLayout());
            this.popup.add(this.getInfoMessage(), BorderLayout.NORTH);
            JPanel temp = new JPanel(new GridLayout(0, 1));
            this.getEmptyMessage();
            temp.add(this.southPanel);
            this.popup.add(temp, BorderLayout.SOUTH);
            this.scroll = new JScrollPane(this);
            this.popup.add(this.scroll);
            if (!ApplicationManager.useOntimizePlaf) {
                this.popup.setBorder(this.getPopupBorder());
            } else {
                temp.setOpaque(false);
                this.southPanel.setOpaque(false);
                this.scroll.setOpaque(false);
            }
            this.popup.pack();
        }
    }

    public void show(Component c, int x, int y) {
        if (this.popup == null) {
            this.createPopup();
        }

        if (this.getModel().getSize() > 0) {
            this.emptyLabel.setVisible(false);
            this.infoLabel.setVisible(true);
            this.popup.add(this.infoLabel, BorderLayout.NORTH);
            this.scroll.setVisible(true);
            this.setTextLabelInfo(this.bundle, this.getModel());
        } else {
            this.infoLabel.setVisible(false);
            this.emptyLabel.setVisible(true);
            this.popup.add(this.emptyLabel, BorderLayout.NORTH);
            this.scroll.setVisible(false);
        }

        this.popup.show(c, x, y);
    }

    protected JLabel getEmptyMessage() {
        if (this.emptyLabel == null) {
            this.emptyLabel = new JLabel(ApplicationManager.getTranslation("M_NO_HAY_DATOS", this.bundle, null));
        }
        return this.emptyLabel;
    }

    protected abstract void setTextLabelInfo(ResourceBundle res, ListModel model);

    protected JLabel getInfoMessage() {
        if (this.infoLabel == null) {
            this.infoLabel = new JLabel();
        }
        return this.infoLabel;
    }

    public void setDataModel(EntityResult values) {
        ListModel model = this.getModel();
        if (model instanceof PopupListModel) {
            ((PopupListModel) this.getModel()).setDataModel(values);
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int x = e.getX();
        if ((x > 1) && (x < 18)) {
            return ApplicationManager.getTranslation(PopupList.POPUPLIST_CHECK,
                    ApplicationManager.getApplicationBundle());
        } else if ((x >= 18) && (x <= 35)) {
            if (this.enabledPrivateTemplates) {
                return ApplicationManager.getTranslation(PopupList.POPUPLIST_PRIVATE,
                        ApplicationManager.getApplicationBundle());
            } else {
                return ApplicationManager.getTranslation(PopupList.POPUPLIST_SAVE_TEMPLATE,
                        ApplicationManager.getApplicationBundle());
            }
        } else if ((x > 35) && (x <= 52)) {
            if (this.enabledPrivateTemplates) {
                return ApplicationManager.getTranslation(PopupList.POPUPLIST_SAVE_TEMPLATE,
                        ApplicationManager.getApplicationBundle());
            } else {
                return ApplicationManager.getTranslation(PopupList.POPUPLIST_DELETE_TEMPLATE,
                        ApplicationManager.getApplicationBundle());
            }
        } else if ((x > 52) && (x <= 69)) {
            return ApplicationManager.getTranslation(PopupList.POPUPLIST_DELETE_TEMPLATE,
                    ApplicationManager.getApplicationBundle());
        }
        return "";
    }

    public JPanel getPanelElements() {
        return this.southPanel;
    }

    protected Border getPopupBorder() {
        return PopupList.defaultPopupBorder;
    }

}
