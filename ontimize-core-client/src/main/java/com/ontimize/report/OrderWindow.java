package com.ontimize.report;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

public class OrderWindow extends EJDialog implements Internationalization {

    private ResourceBundle res = null;

    private static String key = "ReportDesigner.Order";

    private final JList l = new JList();

    private final JToolBar toolBar = new JToolBar(JToolBar.VERTICAL);

    private final JButton acceptButton = new JButton(ImageManager.getIcon(ImageManager.OK));

    private final JButton cancelButton = new JButton(ImageManager.getIcon(ImageManager.CANCEL));

    private java.util.List resultList = null;

    protected static class OrderListRenderer extends TranslateListRenderer {

        private ImageIcon upIcon = null;

        private ImageIcon downIcon = null;

        public OrderListRenderer(ResourceBundle bundle) {
            super(bundle);
            this.upIcon = ImageManager.getIcon(ImageManager.ARROW_UP_BLUE);
            this.downIcon = ImageManager.getIcon(ImageManager.ARROW_DOWN_BLUE);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            Component com = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (com instanceof JLabel) {
                if (value instanceof SelectableItemOrder) {
                    if (((SelectableItemOrder) value).getOrder()) {
                        ((JLabel) com).setIcon(this.upIcon);
                    } else {
                        ((JLabel) com).setIcon(this.downIcon);
                    }
                }
            }
            return com;
        }

    }

    public OrderWindow(Dialog d, String title, ResourceBundle bundle) {
        super(d, true);
        this.res = bundle;
        this.init();
    }

    public OrderWindow(Frame f, String title, ResourceBundle bundle) {
        super(f, true);
        this.res = bundle;
        this.init();
    }

    public java.util.List showOrderWindow(java.util.List selectedColumnList, java.util.List groupList,
            java.util.List sortColumnList) {
        this.setOrderColumns(sortColumnList, selectedColumnList, groupList);
        this.pack();
        ApplicationManager.center(this);
        this.setVisible(true);
        return this.resultList;
    }

    protected java.util.List getColumnOrderName() {

        java.util.List li = new ArrayList();
        Object oName = null;
        DefaultListModel model = (DefaultListModel) this.l.getModel();

        for (int i = 0, a = model.getSize(); i < a; i++) {
            oName = model.getElementAt(i);
            li.add(oName);
        }
        return li;
    }

    private void setOrderColumns(java.util.List sortColumnList, java.util.List selectedColumnList,
            java.util.List groupList) {
        DefaultListModel model = (DefaultListModel) this.l.getModel();
        if (sortColumnList != null) {
            for (int i = 0, a = sortColumnList.size(); i < a; i++) {
                boolean insert = false;

                Object o = sortColumnList.get(i);
                SelectableItemOrder item = (SelectableItemOrder) sortColumnList.get(i);
                String name = item.getText();
                for (int j = 0; j < selectedColumnList.size(); j++) {
                    if (name.equals(selectedColumnList.get(j))) {
                        selectedColumnList.remove(j);
                        insert = true;
                        break;
                    }
                }
                if (insert) {
                    for (int j = 0, b = groupList.size(); j < b; j++) {
                        if (name.equals(groupList.get(j))) {
                            insert = false;
                            break;
                        }
                    }
                }

                int index = model.indexOf(item);
                if (insert) {
                    if (index == -1) {
                        model.addElement(item.clone());
                    }
                } else if (index != -1) {
                    model.remove(index);
                }
            }
        }

        for (int i = 0, a = selectedColumnList.size(); i < a; i++) {
            String si = (String) selectedColumnList.get(i);
            boolean alguno = false;
            for (int j = 0, b = groupList.size(); j < b; j++) {
                if (si.equals(groupList.get(j))) {
                    alguno = true;
                    break;
                }
            }
            if (!alguno) {
                model.addElement(new SelectableItemOrder(si));
            }
        }
    }

    JButton upButton = new JButton();

    JButton downButton = new JButton();

    JButton allUpButton = new JButton();

    JButton allDownButton = new JButton();

    protected void init() {

        this.setTitle(ApplicationManager.getTranslation(OrderWindow.key, this.res));
        DefaultListModel m = new DefaultListModel();
        this.l.setModel(m);
        this.l.setCellRenderer(new OrderListRenderer(this.res));
        this.l.setToolTipText(ApplicationManager.getTranslation(DefaultReportDialog.ORDER_TIP_KEY, this.res));
        this.l.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof JList) {
                    JList list = (JList) e.getSource();
                    int index = list.locationToIndex(e.getPoint());
                    if (index != -1) {
                        if (e.getX() < 24) {
                            SelectableItemOrder item = (SelectableItemOrder) list.getModel().getElementAt(index);
                            item.setOrder(!item.getOrder());
                            list.repaint();
                        }
                    }
                }
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }
        });

        this.l.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        JPanel aux = new JPanel(new BorderLayout());
        aux.setBorder(new EmptyBorder(3, 3, 3, 3));
        this.getContentPane().add(aux);

        JPanel panelInterno = new JPanel(new BorderLayout());
        panelInterno.add(new JScrollPane(this.l));

        JPanel jpButtonsPanel = new JPanel(new GridLayout(0, 1));
        this.toolBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.toolBar.add(this.allUpButton);
        this.toolBar.add(this.upButton);
        this.toolBar.add(this.downButton);
        this.toolBar.add(this.allDownButton);
        RolloverHandler.getInstance().add(this.allUpButton);
        RolloverHandler.getInstance().add(this.upButton);
        RolloverHandler.getInstance().add(this.downButton);
        RolloverHandler.getInstance().add(this.allDownButton);

        this.upButton.setToolTipText(this.res.getString(DefaultReportDialog.UP_BUTTON_KEY));
        this.downButton.setToolTipText(this.res.getString(DefaultReportDialog.DOWN_BUTTON_KEY));
        this.allUpButton.setToolTipText(this.res.getString(DefaultReportDialog.ALL_UP_BUTTON_KEY));
        this.allDownButton.setToolTipText(this.res.getString(DefaultReportDialog.ALL_DOWN_BUTTON_KEY));

        this.allDownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!OrderWindow.this.l.isSelectionEmpty()) {
                    int i = OrderWindow.this.l.getSelectedIndex();
                    if (i != (OrderWindow.this.l.getModel().getSize() - 1)) {
                        DefaultListModel m = (DefaultListModel) OrderWindow.this.l.getModel();
                        Object o = m.getElementAt(i);
                        m.remove(i);
                        m.addElement(o);
                        OrderWindow.this.l.setSelectedIndex(m.getSize() - 1);
                    }
                }
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }
        });

        this.allUpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!OrderWindow.this.l.isSelectionEmpty()) {
                    int i = OrderWindow.this.l.getSelectedIndex();
                    if (i != 0) {
                        DefaultListModel m = (DefaultListModel) OrderWindow.this.l.getModel();
                        Object o = m.getElementAt(i);
                        m.remove(i);
                        m.add(0, o);
                        OrderWindow.this.l.setSelectedIndex(0);
                    }
                }
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }
        });

        this.downButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!OrderWindow.this.l.isSelectionEmpty()) {
                    int i = OrderWindow.this.l.getSelectedIndex();
                    if (i != (OrderWindow.this.l.getModel().getSize() - 1)) {
                        DefaultListModel m = (DefaultListModel) OrderWindow.this.l.getModel();
                        Object o = m.getElementAt(i);
                        m.remove(i);
                        m.add(i + 1, o);
                        OrderWindow.this.l.setSelectedIndex(i + 1);
                    }
                }
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }
        });

        this.upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!OrderWindow.this.l.isSelectionEmpty()) {
                    int i = OrderWindow.this.l.getSelectedIndex();
                    if (i != 0) {
                        DefaultListModel m = (DefaultListModel) OrderWindow.this.l.getModel();
                        Object o = m.getElementAt(i);
                        m.remove(i);
                        m.add(i - 1, o);
                        OrderWindow.this.l.setSelectedIndex(i - 1);
                    }
                }
                DefaultReportDialog.checkListStatusButtons(OrderWindow.this.l, OrderWindow.this.allUpButton,
                        OrderWindow.this.upButton, OrderWindow.this.downButton,
                        OrderWindow.this.allDownButton);
            }
        });

        ImageIcon startIcon = ImageManager.getIcon(ImageManager.START_2_VERTICAL);
        if (startIcon != null) {
            this.allUpButton.setIcon(startIcon);
        } else {
            this.allUpButton.setText("Up+");
        }

        ImageIcon endIcon = ImageManager.getIcon(ImageManager.PREVIOUS_2_VERTICAL);
        if (endIcon != null) {
            this.upButton.setIcon(endIcon);
        } else {
            this.upButton.setText("Up");
        }

        ImageIcon nextIcon = ImageManager.getIcon(ImageManager.NEXT_2_VERTICAL);
        if (nextIcon != null) {
            this.downButton.setIcon(nextIcon);
        } else {
            this.downButton.setText("Down");
        }

        ImageIcon end2Icon = ImageManager.getIcon(ImageManager.END_2_VERTICAL);
        if (end2Icon != null) {
            this.allDownButton.setIcon(end2Icon);
        } else {
            this.allDownButton.setText("Down+");
        }

        this.upButton.setMargin(new Insets(0, 0, 0, 0));
        this.downButton.setMargin(new Insets(0, 0, 0, 0));
        this.allUpButton.setMargin(new Insets(0, 0, 0, 0));
        this.allDownButton.setMargin(new Insets(0, 0, 0, 0));

        jpButtonsPanel.add(this.toolBar);
        aux.add(jpButtonsPanel, BorderLayout.EAST);

        JPanel jpButtonsPanel2 = new JPanel(new GridBagLayout());

        this.acceptButton.setText(ApplicationManager.getTranslation("ReportDesigner.Aceptar", this.res));
        this.acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderWindow.this.resultList = OrderWindow.this.getColumnOrderName();
                OrderWindow.this.setVisible(false);
            }

        });
        this.cancelButton.setText(ApplicationManager.getTranslation("ReportDesigner.Cancelar", this.res));
        this.cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                OrderWindow.this.resultList = null;
                OrderWindow.this.setVisible(false);
            }
        });
        jpButtonsPanel2.add(this.acceptButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jpButtonsPanel2.add(this.cancelButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        jpButtonsPanel2.setBorder(new EmptyBorder(3, 3, 3, 3));
        panelInterno.add(jpButtonsPanel2, BorderLayout.SOUTH);
        aux.add(panelInterno);
        this.pack();
        DefaultReportDialog.checkListStatusButtons(this.l, this.allUpButton, this.upButton, this.downButton,
                this.allDownButton);
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setComponentLocale(Locale locale) {

    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        this.res = bundle;
        this.l.setCellRenderer(new OrderListRenderer(this.res));
        this.cancelButton.setText(ApplicationManager.getTranslation("application.cancel", this.res));
        this.acceptButton.setText(ApplicationManager.getTranslation("application.accept", this.res));
    }

}
