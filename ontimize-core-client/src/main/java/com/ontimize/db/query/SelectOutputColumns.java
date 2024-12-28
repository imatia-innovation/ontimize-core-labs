package com.ontimize.db.query;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

public class SelectOutputColumns extends EJDialog implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(SelectOutputColumns.class);

    // -------------------------------
    // private ResourceBundle bundle=null;
    public static boolean DEBUG = true;

    public static final int MIN = 0;

    public static final int MAX = 1;

    public static final int SUM = 2;

    public static final int AVG = 3;

    private static String averageOpKey = "average";

    private static String sumOpKey = "sum";

    private static String maxOpKey = "maximum";

    private static String minOpKey = "minimum";

    private class SelectableFunctionItem extends TranslatedItem implements Internationalization {

        protected boolean selected = false;

        protected int operation = SelectOutputColumns.SUM;

        protected String operationText = SelectOutputColumns.sumOpKey;

        public SelectableFunctionItem(String text, ResourceBundle res) {
            super(text, res);
        }

        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public void setResourceBundle(ResourceBundle res) {
            super.setResourceBundle(res);
            this.setOperationText();
        }

        public void setSelected(boolean sel) {
            this.selected = sel;
        }

        public void setOperation(int op) {
            this.operation = op;
            this.setOperationText();
        }

        protected void setOperationText() {
            if (this.res != null) {
                try {
                    if (this.operation == SelectOutputColumns.SUM) {
                        this.operationText = SelectOutputColumns.sumOpKey;
                    } else if (this.operation == SelectOutputColumns.MAX) {
                        this.operationText = SelectOutputColumns.maxOpKey;
                    } else if (this.operation == SelectOutputColumns.MIN) {
                        this.operationText = SelectOutputColumns.minOpKey;
                    } else if (this.operation == SelectOutputColumns.AVG) {
                        this.operationText = SelectOutputColumns.averageOpKey;
                    }
                    this.operationText = this.res.getString(this.operationText);

                } catch (Exception e) {
                    if (SelectOutputColumns.DEBUG) {
                        SelectOutputColumns.logger.debug(null, e);
                    }
                }
            }
        }

        public int getOperation() {
            return this.operation;
        }

        @Override
        public String toString() {
            if (!this.isSelected()) {
                return this.translatedText;
            }
            return this.translatedText + " - " + this.operationText;
        }

    };

    private class TranslatedItem implements Internationalization {

        protected String text = "";

        protected String translatedText = null;

        protected ResourceBundle res = null;

        public TranslatedItem(String text, ResourceBundle res) {
            this.text = text;
            this.translatedText = text;
            this.setResourceBundle(res);
        }

        @Override
        public void setResourceBundle(ResourceBundle res) {
            this.res = res;
            if (res != null) {
                try {
                    this.translatedText = res.getString(this.text);
                } catch (Exception e) {
                    this.translatedText = this.text;
                    if (SelectOutputColumns.DEBUG) {
                        SelectOutputColumns.logger.debug(null, e);
                    }
                }
            }
        }

        @Override
        public void setComponentLocale(Locale l) {
        }

        @Override
        public List getTextsToTranslate() {
            return null;
        }

        @Override
        public String toString() {
            return this.translatedText;
        }

        public String getText() {
            return this.text;
        }

        @Override
        public int hashCode() {
            return this.text.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof SelectableItem) {
                if (this.text.equals(((SelectableItem) o).getText())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

    private class SelectableItem extends TranslatedItem implements Internationalization {

        protected boolean selected = false;

        public SelectableItem(String text, ResourceBundle res) {
            super(text, res);
        }

        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public void setResourceBundle(ResourceBundle res) {
            super.setResourceBundle(res);
        }

        public void setSelected(boolean sel) {
            this.selected = sel;
        }

        @Override
        public String toString() {
            if (!this.isSelected()) {
                return this.translatedText;
            }
            return this.translatedText;
        }

    };

    private class SelectableItemsListCellRenderer extends JCheckBox implements ListCellRenderer {

        public SelectableItemsListCellRenderer() {
            this.setBorderPaintedFlat(true);
        }

        @Override
        public Component getListCellRendererComponent(JList l, Object v, int r, boolean sel, boolean foc) {

            Color selBgColor = UIManager.getColor("List.selectionBackground");
            Color selFgColor = UIManager.getColor("List.selectionForeground");

            Color noSelFg = UIManager.getColor("List.foreground");
            Color noSelBg = UIManager.getColor("List.background");

            if (sel) {
                this.setForeground(selFgColor);
                this.setBackground(selBgColor);
            } else {
                this.setForeground(noSelFg);
                this.setBackground(noSelBg);
            }
            if (v instanceof SelectableItem) {
                this.setText(((SelectableItem) v).toString());
                boolean bSelected = ((SelectableItem) v).isSelected();
                this.setSelected(bSelected);
            }

            if (v instanceof SelectableFunctionItem) {
                this.setText(((SelectableFunctionItem) v).toString());
                boolean bSelected = ((SelectableFunctionItem) v).isSelected();
                this.setSelected(bSelected);
            }
            return this;
        }

    }

    protected JList jList;

    protected JButton bOk = null;

    protected JButton bCancel = null;

    protected SelectableItem[] si;

    public SelectOutputColumns(String[] columns, boolean[] queryColumns, ResourceBundle bundle) {
        super((Frame) null, ApplicationManager.getTranslation("QueryOutputColumnsTitle", bundle), true);
        this.init(columns, queryColumns, bundle);
    }

    public SelectOutputColumns(Frame f, String[] columns, boolean[] queryColumns, ResourceBundle bundle) {
        super(f, ApplicationManager.getTranslation("QueryOutputColumnsTitle", bundle), true);
        this.init(columns, queryColumns, bundle);
    }

    public SelectOutputColumns(Dialog f, String[] columns, boolean[] queryColumns, ResourceBundle bundle) {
        super(f, ApplicationManager.getTranslation("QueryOutputColumnsTitle", bundle), true);
        this.init(columns, queryColumns, bundle);
    }

    private class ColumnSelectionListener extends MouseAdapter {

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getModifiers() == InputEvent.META_MASK) {
                // Popup menu
                return;
            }
            if (e.getX() > 24) {
                return;
            }
            SelectOutputColumns.this.jList.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            try {
                int index = SelectOutputColumns.this.jList.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }
                SelectableItem it = (SelectableItem) SelectOutputColumns.this.jList.getModel().getElementAt(index);
                boolean willBeSelected = !it.isSelected();

                it.setSelected(willBeSelected);

                Rectangle rect = SelectOutputColumns.this.jList.getCellBounds(index, index);
                SelectOutputColumns.this.jList.repaint(rect);
            } catch (Exception ex) {
                SelectOutputColumns.logger.error(null, ex);
            } finally {
                SelectOutputColumns.this.jList.setCursor(Cursor.getDefaultCursor());

            }
        }

    };

    private final ColumnSelectionListener columnSelectionListener = new ColumnSelectionListener();

    public void init(String[] columns, boolean[] queryColumns, ResourceBundle bundle) {
        JScrollPane sp = new JScrollPane();

        this.si = new SelectableItem[columns.length];

        for (int i = 0, a = columns.length; i < a; i++) {
            this.si[i] = new SelectableItem(columns[i], bundle);
            this.si[i].setSelected(queryColumns[i]);
        }
        this.jList = new JList(this.si);

        JPanel jpButtons = new JPanel();

        this.bOk = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.OK));

        this.bOk.setText(ApplicationManager.getTranslation("QueryBuilderOutputColumsOK", bundle));
        this.bOk.setToolTipText(ApplicationManager.getTranslation("QueryBuilderOutputColumsOK", bundle));
        this.bOk.setActionCommand("application.accept");
        this.bOk.setSize(60, 20);
        this.bOk.addActionListener(this);

        this.bCancel = new com.ontimize.report.ReportDesignerButton(ImageManager.getIcon(ImageManager.DELETE));
        this.bOk.setSize(60, 20);
        this.bCancel.setText(ApplicationManager.getTranslation("QueryBuilderOutputColumsCancel", bundle));
        this.bCancel.setActionCommand("application.cancel");
        this.bCancel.addActionListener(this);

        jpButtons.setLayout(new FlowLayout());
        jpButtons.add(this.bOk);

        this.jList.setCellRenderer(new SelectableItemsListCellRenderer());
        this.jList.addMouseListener(this.columnSelectionListener);

        sp.setViewportView(this.jList);
        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane()
            .add(new JLabel(ApplicationManager.getTranslation("QueryBuilderOuputColumnsElegirColsConsulta", bundle)),
                    new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));
        this.getContentPane()
            .add(sp, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(2, 2, 2, 2), 0, 0));
        this.getContentPane()
            .add(jpButtons, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(2, 2, 2, 2), 0, 0));
        this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("application.cancel".equalsIgnoreCase(e.getActionCommand())) {
            super.setVisible(false);
        }

        if ("application.accept".equalsIgnoreCase(e.getActionCommand())) {
            super.setVisible(false);
        }
    }

    public boolean[] showSelectOutputColumns() {
        super.setVisible(true);
        boolean[] s = new boolean[this.jList.getModel().getSize()];
        for (int i = 0, a = this.jList.getModel().getSize(); i < a; i++) {
            s[i] = ((SelectableItem) this.jList.getModel().getElementAt(i)).isSelected();
        }

        return s;
    }

}
