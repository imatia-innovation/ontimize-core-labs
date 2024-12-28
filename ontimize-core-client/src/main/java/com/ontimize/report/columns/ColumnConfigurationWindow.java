package com.ontimize.report.columns;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import javax.swing.text.StyleConstants;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.document.IntegerDocument;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.report.DefaultReportDialog;

public class ColumnConfigurationWindow extends EJDialog implements Internationalization {

    public DefaultReportDialog reportDialog;

    protected ResourceBundle res = null;

    protected String title;

    protected String column;

    protected final JButton acceptButton = new JButton(ImageManager.getIcon(ImageManager.OK));

    protected final JButton cancelButton = new JButton(ImageManager.getIcon(ImageManager.CANCEL));

    protected JLabel columnLabel;

    protected JLabel nameColumnLabel;

    protected JCheckBox autoConfigurationCheck;

    protected JCheckBox fixedWidthCheck;

    protected JTextField widthField;

    protected JCheckBox alignmentCheck;

    protected JToggleButton leftAlignBt;

    protected JToggleButton centerAlignBt;

    protected JToggleButton rightAlignBt;

    protected static final String AUTO_CONFIGURATION_TEXT = "reportdesigner.autoconfiguration";

    protected static final String FIXED_WIDTH_TEXT = "reportdesigner.fixedwidth";

    protected static final String COLUMN_NAME_TEXT = "reportdesigner.columnname";

    protected static final String ALIGNMENT_TEXT = "reportdesigner.alignment";

    public ColumnConfigurationWindow(Dialog d, String title, ResourceBundle bundle, DefaultReportDialog reportDialog) {
        super(d, true);
        this.res = bundle;
        this.reportDialog = reportDialog;
        this.title = title;
        this.init();
    }

    public ColumnConfigurationWindow(Frame f, String title, ResourceBundle bundle, DefaultReportDialog reportDialog) {
        super(f, true);
        this.res = bundle;
        this.reportDialog = reportDialog;
        this.title = title;
        this.init();
    }

    protected void selectAutoConfiguration(boolean selected) {
        if (selected) {
            this.fixedWidthCheck.setEnabled(false);
            this.widthField.setEnabled(false);
            this.alignmentCheck.setEnabled(false);
            this.leftAlignBt.setEnabled(false);
            this.centerAlignBt.setEnabled(false);
            this.rightAlignBt.setEnabled(false);
        } else {
            this.fixedWidthCheck.setEnabled(true);
            this.widthField.setEnabled(this.fixedWidthCheck.isSelected());
            if ((this.widthField.getText() == null) && (this.widthField.getText().length() == 0)) {
                this.widthField.setText("70");
            }
            this.alignmentCheck.setEnabled(true);
            this.leftAlignBt.setEnabled(this.alignmentCheck.isSelected());
            this.centerAlignBt.setEnabled(this.alignmentCheck.isSelected());
            this.rightAlignBt.setEnabled(this.alignmentCheck.isSelected());
        }
    }

    public void init() {
        this.setTitle(ApplicationManager.getTranslation(this.title, this.res));
        this.getContentPane().setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());

        this.columnLabel = new JLabel(
                ApplicationManager.getTranslation(ColumnConfigurationWindow.COLUMN_NAME_TEXT, this.res));
        centerPanel.add(this.columnLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 4, 2, 2), 0, 0));
        this.nameColumnLabel = new JLabel();
        Font font = this.nameColumnLabel.getFont().deriveFont(Font.BOLD);
        this.nameColumnLabel.setFont(font);

        centerPanel.add(this.nameColumnLabel, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        this.autoConfigurationCheck = new JCheckBox(
                ApplicationManager.getTranslation(ColumnConfigurationWindow.AUTO_CONFIGURATION_TEXT, this.res));
        this.autoConfigurationCheck.setSelected(true);

        this.autoConfigurationCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnConfigurationWindow.this
                    .selectAutoConfiguration(ColumnConfigurationWindow.this.autoConfigurationCheck.isSelected());
            }
        });

        this.fixedWidthCheck = new JCheckBox(
                ApplicationManager.getTranslation(ColumnConfigurationWindow.FIXED_WIDTH_TEXT, this.res));
        this.widthField = new JTextField(5);
        this.widthField.setDocument(new IntegerDocument());

        centerPanel.add(this.autoConfigurationCheck, new GridBagConstraints(0, 1, 2, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setBorder(new TitledBorder(""));
        centerPanel.add(configPanel, new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(4, 4, 4, 4), 0, 0));

        configPanel.add(this.fixedWidthCheck, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));
        this.widthField.setEnabled(this.fixedWidthCheck.isSelected());
        this.fixedWidthCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnConfigurationWindow.this.widthField
                    .setEnabled(ColumnConfigurationWindow.this.fixedWidthCheck.isSelected());
            }
        });
        configPanel.add(this.widthField, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        this.alignmentCheck = new JCheckBox(
                ApplicationManager.getTranslation(ColumnConfigurationWindow.ALIGNMENT_TEXT, this.res));
        configPanel.add(this.alignmentCheck, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

        this.leftAlignBt = new JToggleButton(ImageManager.getIcon(ImageManager.LEFT_ALIGN));
        this.centerAlignBt = new JToggleButton(ImageManager.getIcon(ImageManager.CENTER_ALIGN));
        this.rightAlignBt = new JToggleButton(ImageManager.getIcon(ImageManager.RIGHT_ALIGN));

        this.alignmentCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnConfigurationWindow.this.leftAlignBt
                    .setEnabled(ColumnConfigurationWindow.this.alignmentCheck.isSelected());
                ColumnConfigurationWindow.this.centerAlignBt
                    .setEnabled(ColumnConfigurationWindow.this.alignmentCheck.isSelected());
                ColumnConfigurationWindow.this.rightAlignBt
                    .setEnabled(ColumnConfigurationWindow.this.alignmentCheck.isSelected());
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(this.leftAlignBt);
        group.add(this.centerAlignBt);
        group.add(this.rightAlignBt);

        JPanel alignmentPanel = new JPanel(new GridBagLayout());
        configPanel.add(alignmentPanel, new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
        alignmentPanel.add(this.leftAlignBt, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
                GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        alignmentPanel.add(this.centerAlignBt, new GridBagConstraints(1, 0, 1, 1, 0, 1, GridBagConstraints.NORTH,
                GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));
        alignmentPanel.add(this.rightAlignBt, new GridBagConstraints(2, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(2, 0, 2, 0), 0, 0));

        this.getContentPane().add(new JScrollPane(centerPanel));
        JPanel panel = new JPanel();
        panel.add(this.acceptButton);
        panel.add(this.cancelButton);

        this.getContentPane().add(panel, BorderLayout.SOUTH);

        this.acceptButton.setText(ApplicationManager.getTranslation("ReportDesigner.Aceptar", this.res));
        this.acceptButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnConfigurationWindow.this.save();
                ColumnConfigurationWindow.this.setVisible(false);
                ColumnConfigurationWindow.this.reportDialog.updateReport();
            }
        });

        this.cancelButton.setText(ApplicationManager.getTranslation("ReportDesigner.Cancelar", this.res));
        this.cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ColumnConfigurationWindow.this.setVisible(false);
            }
        });

        this.selectAutoConfiguration(true);
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resources) {
        this.res = resources;
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    protected void save() {
        List<String> configuredColumns = this.reportDialog.getConfiguredColumns();
        Map<String, Integer> columnFixedWidth = this.reportDialog.getColumnFixedWidth();
        Map<String, Integer> columnAlignment = this.reportDialog.getColumnAlignment();

        configuredColumns.remove(this.column);
        columnFixedWidth.remove(this.column);
        if (!this.autoConfigurationCheck.isSelected()) {
            configuredColumns.add(this.column);
            if (this.fixedWidthCheck.isSelected()) {
                String value = this.widthField.getText();
                columnFixedWidth.put(this.column, Integer.valueOf(value));
            }
            if (this.alignmentCheck.isSelected()) {
                if (this.leftAlignBt.isSelected()) {
                    columnAlignment.put(this.column, StyleConstants.ALIGN_LEFT);
                } else if (this.centerAlignBt.isSelected()) {
                    columnAlignment.put(this.column, StyleConstants.ALIGN_CENTER);
                } else if (this.rightAlignBt.isSelected()) {
                    columnAlignment.put(this.column, StyleConstants.ALIGN_RIGHT);
                }
            }

        }

    }

    public void showColumnConfigurator(String columnName) {
        this.column = columnName;
        this.nameColumnLabel.setText(ApplicationManager.getTranslation(columnName, this.res));

        List<String> configuredColumns = this.reportDialog.getConfiguredColumns();
        if (configuredColumns.contains(columnName)) {
            Map<String, Integer> columnFixedWidth = this.reportDialog.getColumnFixedWidth();
            if (columnFixedWidth.containsKey(columnName)) {
                this.fixedWidthCheck.setSelected(true);
                Integer width = columnFixedWidth.get(columnName);
                this.widthField.setText(width.toString());
            } else {
                this.fixedWidthCheck.setSelected(false);
            }

            Map<String, Integer> columnAlignment = this.reportDialog.getColumnAlignment();
            if (columnAlignment.containsKey(columnName)) {
                this.alignmentCheck.setSelected(true);
                Integer align = columnAlignment.get(columnName);
                switch (align) {
                    case StyleConstants.ALIGN_LEFT:
                        this.leftAlignBt.setSelected(true);
                        break;
                    case StyleConstants.ALIGN_CENTER:
                        this.centerAlignBt.setSelected(true);
                        break;
                    case StyleConstants.ALIGN_RIGHT:
                        this.rightAlignBt.setSelected(true);
                        break;
                }
            } else {
                this.alignmentCheck.setSelected(false);
            }

            this.selectAutoConfiguration(false);
        } else {
            this.autoConfigurationCheck.setSelected(true);
            this.selectAutoConfiguration(true);
            this.fixedWidthCheck.setSelected(false);
            this.widthField.setText("");
            this.alignmentCheck.setSelected(false);
        }
        this.pack();
        ApplicationManager.center(this);
        this.setVisible(true);
    }

}
