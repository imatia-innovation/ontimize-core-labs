package com.ontimize.gui.field.html.dialogs;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.ontimize.gui.field.html.dialogs.panels.TableAttributesPanel;
import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.images.ImageManager;

public class NewTableDialog extends OptionDialog {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance();

    protected LayoutPanel layoutPanel = new LayoutPanel();

    protected TableAttributesPanel propsPanel;

    public static Icon icon = ImageManager.getIcon(ImageManager.TABLE_VIEW);

    public NewTableDialog(Frame parent) {
        super(parent, "HTMLShef.new_table", "HTMLShef.new_table_desc", NewTableDialog.icon);
        this.init();
    }

    public NewTableDialog(Dialog parent) {
        super(parent, "HTMLShef.new_table", "HTMLShef.new_table_desc", NewTableDialog.icon);
        this.init();
    }

    protected void init() {
        // default attribs
        Map ht = new Hashtable();
        ht.put("border", "1");
        ht.put("width", "100%");
        this.propsPanel = new TableAttributesPanel();
        this.propsPanel.setAttributes(ht);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(this.layoutPanel, BorderLayout.NORTH);
        mainPanel.add(this.propsPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setContentPane(mainPanel);
        this.setSize(new Dimension(420, 420));
        this.setResizable(false);
    }

    public String getHTML() {
        String html = "<table";
        Map attribs = this.propsPanel.getAttributes();

        for (Iterator e = attribs.keySet().iterator(); e.hasNext();) {
            String key = e.next().toString();
            String val = attribs.get(key).toString();
            html += ' ' + key + "=\"" + val + "\"";
        }

        html += ">\n";

        int numRows = this.layoutPanel.getRows();
        int numCols = this.layoutPanel.getColumns();
        for (int row = 1; row <= numRows; row++) {
            html += "<tr>\n";
            for (int col = 1; col <= numCols; col++) {
                html += "\t<td>\n</td>\n";
            }
            html += "</tr>\n";
        }

        return html + "</table>";
    }

    protected class LayoutPanel extends JPanel {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        protected JLabel rowsLabel = null;

        protected JLabel colsLabel = null;

        protected int iRows, iCols;

        protected JSpinner rowsField = null;

        protected JSpinner colsField = null;

        /**
         * This is the default constructor
         */
        public LayoutPanel() {
            this(1, 1);
        }

        public LayoutPanel(int r, int c) {
            super();
            this.iRows = r > 0 ? r : 1;
            this.iCols = c > 0 ? c : 1;
            this.initialize();
        }

        public int getRows() {
            return Integer.parseInt(this.rowsField.getModel().getValue().toString());
        }

        public int getColumns() {
            return Integer.parseInt(this.colsField.getModel().getValue().toString());
        }

        /**
         * This method initializes this
         * @return void
         */
        protected void initialize() {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints7.gridy = 0;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.gridx = 3;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 0.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.insets = new java.awt.Insets(0, 0, 0, 15);
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
            gridBagConstraints1.gridx = 2;
            gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints1.insets = new java.awt.Insets(0, 0, 0, 5);
            gridBagConstraints1.gridy = 0;
            this.colsLabel = new JLabel();
            this.colsLabel.setText(NewTableDialog.i18n.str("HTMLShef.columns"));
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.gridx = 0;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
            gridBagConstraints.weighty = 0.0;
            gridBagConstraints.gridy = 0;
            this.rowsLabel = new JLabel();
            this.rowsLabel.setText(NewTableDialog.i18n.str("HTMLShef.rows"));
            this.setLayout(new GridBagLayout());
            this.setSize(330, 70);
            this.setPreferredSize(new java.awt.Dimension(330, 70));
            this.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                    javax.swing.BorderFactory.createTitledBorder(null, NewTableDialog.i18n.str("HTMLShef.layout"),
                            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION, null, null),
                    javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            this.add(this.rowsLabel, gridBagConstraints);
            this.add(this.colsLabel, gridBagConstraints1);
            this.add(this.getRowsField(), gridBagConstraints6);
            this.add(this.getColsField(), gridBagConstraints7);
        }

        /**
         * This method initializes rowsField
         * @return javax.swing.JSpinner
         */
        protected JSpinner getRowsField() {
            if (this.rowsField == null) {
                this.rowsField = new JSpinner(new SpinnerNumberModel(this.iRows, 1, 999, 1));
            }
            return this.rowsField;
        }

        /**
         * This method initializes colsField
         * @return javax.swing.JSpinner
         */
        protected JSpinner getColsField() {
            if (this.colsField == null) {
                this.colsField = new JSpinner(new SpinnerNumberModel(this.iCols, 1, 999, 1));
            }
            return this.colsField;
        }

    }

}
