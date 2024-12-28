package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellAttributesPanel extends HTMLAttributeEditorPanel {

    private static final Logger logger = LoggerFactory.getLogger(CellAttributesPanel.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected AlignmentAttributesPanel alignPanel = null;

    protected SizeAttributesPanel sizePanel = null;

    protected JCheckBox dontWrapCB = null;

    protected BGColorPanel bgColorPanel = null;

    protected JPanel spanPanel = null;

    protected JPanel propertiesPanel = null;

    protected JCheckBox colSpanCB = null;

    protected JCheckBox rowSpanCB = null;

    protected JSpinner colSpanField = null;

    protected JSpinner rowSpanField = null;

    protected JPanel expansionPanel = null;

    protected TitledBorder spanBorder;

    protected TitledBorder propertiesBorder;

    /**
     * This is the default constructor
     */
    public CellAttributesPanel() {
        this(new Hashtable());
    }

    public CellAttributesPanel(Map attr) {
        super(attr);
        this.initialize();
        this.alignPanel.setAttributes(this.getAttributes());
        this.sizePanel.setAttributes(this.getAttributes());
        this.updateComponentsFromAttribs();
    }

    @Override
    public void updateComponentsFromAttribs() {
        this.alignPanel.updateComponentsFromAttribs();
        this.sizePanel.updateComponentsFromAttribs();

        if (this.attribs.containsKey("colspan")) {
            this.colSpanCB.setSelected(true);
            this.colSpanField.setEnabled(true);
            try {
                this.colSpanField.getModel().setValue(new Integer(this.attribs.get("colspan").toString()));
            } catch (Exception ex) {
                CellAttributesPanel.logger.error(null, ex);
            }
        } else {
            this.colSpanCB.setSelected(false);
            this.colSpanField.setEnabled(false);
        }

        if (this.attribs.containsKey("rowspan")) {
            this.rowSpanCB.setSelected(true);
            this.rowSpanField.setEnabled(true);
            try {
                this.rowSpanField.getModel().setValue(new Integer(this.attribs.get("rowspan").toString()));
            } catch (Exception ex) {
                CellAttributesPanel.logger.error(null, ex);
            }
        } else {
            this.rowSpanCB.setSelected(false);
            this.rowSpanField.setEnabled(false);
        }

        if (this.attribs.containsKey("bgcolor")) {
            this.bgColorPanel.setSelected(true);
            this.bgColorPanel.setColor(this.attribs.get("bgcolor").toString());
        } else {
            this.bgColorPanel.setSelected(false);
        }

        this.dontWrapCB.setSelected(this.attribs.containsKey("nowrap"));
    }

    @Override
    public void updateAttribsFromComponents() {
        this.alignPanel.updateAttribsFromComponents();
        this.sizePanel.updateAttribsFromComponents();
        if (this.dontWrapCB.isSelected()) {
            this.attribs.put("nowrap", "nowrap"); //$NON-NLS-2$
        } else {
            this.attribs.remove("nowrap");
        }

        if (this.bgColorPanel.isSelected()) {
            this.attribs.put("bgcolor", this.bgColorPanel.getColor());
        } else {
            this.attribs.remove("bgcolor");
        }

        if (this.colSpanCB.isSelected()) {
            this.attribs.put("colspan", this.colSpanField.getModel().getValue().toString());
        } else {
            this.attribs.remove("colspan");
        }

        if (this.rowSpanCB.isSelected()) {
            this.attribs.put("rowspan", this.rowSpanField.getModel().getValue().toString());
        } else {
            this.attribs.remove("rowspan");
        }
    }

    @Override
    public void setAttributes(Map attr) {
        this.alignPanel.setAttributes(attr);
        this.sizePanel.setAttributes(attr);
        super.setAttributes(attr);
    }

    /**
     * This method initializes this
     * @return void
     */
    protected void initialize() {
        GridBagConstraints gridBagConstraints31 = new GridBagConstraints();
        gridBagConstraints31.gridx = 0;
        gridBagConstraints31.gridwidth = 3;
        gridBagConstraints31.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints31.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints31.weighty = 1.0;
        gridBagConstraints31.gridy = 3;
        GridBagConstraints gridBagConstraints21 = new GridBagConstraints();
        gridBagConstraints21.gridx = 0;
        gridBagConstraints21.gridheight = 2;
        gridBagConstraints21.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints21.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints21.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints21.gridy = 1;
        GridBagConstraints gridBagConstraints22 = new GridBagConstraints();
        gridBagConstraints22.gridx = 1;
        gridBagConstraints22.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints22.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints22.gridwidth = 2;
        gridBagConstraints22.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 5);
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(430, 200);
        this.setPreferredSize(new java.awt.Dimension(430, 200));
        this.add(this.getAlignPanel(), gridBagConstraints);
        this.add(this.getSizePanel(), gridBagConstraints1);
        this.add(this.getPropertiesPanel(), gridBagConstraints22);
        this.add(this.getSpanPanel(), gridBagConstraints21);
        this.add(this.getExpansionPanel(), gridBagConstraints31);

    }

    /**
     * This method initializes alignPanel
     * @return javax.swing.JPanel
     */
    protected AlignmentAttributesPanel getAlignPanel() {
        if (this.alignPanel == null) {
            this.alignPanel = new AlignmentAttributesPanel();
            this.alignPanel.setPreferredSize(new java.awt.Dimension(180, 95));

        }
        return this.alignPanel;
    }

    /**
     * This method initializes sizePanel
     * @return javax.swing.JPanel
     */
    protected JPanel getSizePanel() {
        if (this.sizePanel == null) {
            this.sizePanel = new SizeAttributesPanel();
        }
        return this.sizePanel;
    }

    /**
     * This method initializes propertiesPanel
     * @return javax.swing.JPanel
     */
    protected JPanel getPropertiesPanel() {
        if (this.propertiesPanel == null) {
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridheight = 1;
            gridBagConstraints5.insets = new java.awt.Insets(0, 0, 0, 0);
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
            gridBagConstraints4.gridy = 0;
            this.propertiesPanel = new JPanel();
            this.propertiesPanel.setLayout(new GridBagLayout());
            this.propertiesPanel.setBorder(this.getPropertiesPanelBorder());
            this.propertiesPanel.add(this.getDontWrapCB(), gridBagConstraints4);
            this.propertiesPanel.add(this.getBgColorPanel(), gridBagConstraints5);
        }
        return this.propertiesPanel;
    }

    /**
     * This method initializes dontWrapCB
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getDontWrapCB() {
        if (this.dontWrapCB == null) {
            this.dontWrapCB = new JCheckBox();
            this.dontWrapCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.dont_wrap_text"));
        }
        return this.dontWrapCB;
    }

    /**
     * This method initializes bgColorPanel
     * @return javax.swing.JPanel
     */
    protected BGColorPanel getBgColorPanel() {
        if (this.bgColorPanel == null) {
            this.bgColorPanel = new BGColorPanel();
        }
        return this.bgColorPanel;
    }

    protected Border getPropertiesPanelBorder() {
        this.propertiesBorder = javax.swing.BorderFactory.createTitledBorder(null,
                HTMLAttributeEditorPanel.i18n.str("HTMLShef.properties"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                null, null);

        return javax.swing.BorderFactory.createCompoundBorder(this.propertiesBorder,
                javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    /**
     * This method initializes spanPanel
     * @return javax.swing.JPanel
     */
    protected JPanel getSpanPanel() {
        if (this.spanPanel == null) {
            GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
            gridBagConstraints7.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints7.gridy = 1;
            gridBagConstraints7.weightx = 1.0;
            gridBagConstraints7.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints7.gridx = 1;
            GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
            gridBagConstraints6.fill = java.awt.GridBagConstraints.NONE;
            gridBagConstraints6.gridy = 0;
            gridBagConstraints6.weightx = 0.0;
            gridBagConstraints6.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints6.gridx = 1;
            GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
            gridBagConstraints5.gridx = 0;
            gridBagConstraints5.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints5.gridheight = 1;
            gridBagConstraints5.insets = new java.awt.Insets(0, 0, 0, 0);
            gridBagConstraints5.gridy = 1;
            GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
            gridBagConstraints4.gridx = 0;
            gridBagConstraints4.anchor = java.awt.GridBagConstraints.WEST;
            gridBagConstraints4.insets = new java.awt.Insets(0, 0, 5, 0);
            gridBagConstraints4.gridy = 0;
            this.spanPanel = new JPanel();
            this.spanPanel.setLayout(new GridBagLayout());
            this.spanPanel.setBorder(this.getSpanPanelBorder());
            this.spanPanel.add(this.getColSpanCB(), gridBagConstraints4);
            this.spanPanel.add(this.getRowSpanCB(), gridBagConstraints5);
            this.spanPanel.add(this.getColSpanField(), gridBagConstraints6);
            this.spanPanel.add(this.getRowSpanField(), gridBagConstraints7);
        }
        return this.spanPanel;
    }

    protected Border getSpanPanelBorder() {
        this.spanBorder = javax.swing.BorderFactory.createTitledBorder(null,
                HTMLAttributeEditorPanel.i18n.str("HTMLShef.span"),
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION,
                null, null);

        return javax.swing.BorderFactory.createCompoundBorder(this.spanBorder,
                javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
    }

    /**
     * This method initializes colSpanCB
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getColSpanCB() {
        if (this.colSpanCB == null) {
            this.colSpanCB = new JCheckBox();
            this.colSpanCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.colspan"));
            this.colSpanCB.setPreferredSize(new java.awt.Dimension(85, 25));
            this.colSpanCB.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    CellAttributesPanel.this.colSpanField.setEnabled(CellAttributesPanel.this.colSpanCB.isSelected());
                }
            });
        }
        return this.colSpanCB;
    }

    /**
     * This method initializes rowSpanCB
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getRowSpanCB() {
        if (this.rowSpanCB == null) {
            this.rowSpanCB = new JCheckBox();
            this.rowSpanCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.rowspan"));
            this.rowSpanCB.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    CellAttributesPanel.this.rowSpanField.setEnabled(CellAttributesPanel.this.rowSpanCB.isSelected());
                }
            });
        }
        return this.rowSpanCB;
    }

    /**
     * This method initializes colSpanField
     * @return javax.swing.JSpinner
     */
    protected JSpinner getColSpanField() {
        if (this.colSpanField == null) {
            this.colSpanField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        }
        return this.colSpanField;
    }

    /**
     * This method initializes rowSpanField
     * @return javax.swing.JSpinner
     */
    protected JSpinner getRowSpanField() {
        if (this.rowSpanField == null) {
            this.rowSpanField = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));

        }
        return this.rowSpanField;
    }

    /**
     * This method initializes expansionPanel
     * @return javax.swing.JPanel
     */
    protected JPanel getExpansionPanel() {
        if (this.expansionPanel == null) {
            this.expansionPanel = new JPanel();
        }
        return this.expansionPanel;
    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        super.setResourceBundle(resourceBundle);

        if (this.dontWrapCB != null) {
            this.dontWrapCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.dont_wrap_text"));
        }
        if (this.spanBorder != null) {
            this.spanBorder.setTitle(HTMLAttributeEditorPanel.i18n.str("HTMLShef.span"));
        }
        if (this.propertiesBorder != null) {
            this.propertiesBorder.setTitle(HTMLAttributeEditorPanel.i18n.str("HTMLShef.properties"));
        }
        if (this.colSpanCB != null) {
            this.colSpanCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.colspan"));
        }
        if (this.rowSpanCB != null) {
            this.rowSpanCB.setText(HTMLAttributeEditorPanel.i18n.str("HTMLShef.rowspan"));
        }

        if (this.bgColorPanel != null) {
            this.bgColorPanel.setResourceBundle(resourceBundle);
        }
        if (this.alignPanel != null) {
            this.alignPanel.setResourceBundle(resourceBundle);
        }
        if (this.sizePanel != null) {
            this.sizePanel.setResourceBundle(resourceBundle);
        }
    }

}
