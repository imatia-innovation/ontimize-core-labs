package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JPanel;

public class RowAttributesPanel extends HTMLAttributeEditorPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected AlignmentAttributesPanel alignPanel = null;

    protected BGColorPanel bgColorPanel = null;

    protected JPanel expansionPanel = null;

    /**
     * This is the default constructor
     */
    public RowAttributesPanel() {
        this(new Hashtable());
    }

    public RowAttributesPanel(Map attr) {
        super(attr);
        this.initialize();
        this.alignPanel.setAttributes(this.getAttributes());
        this.updateComponentsFromAttribs();
    }

    @Override
    public void updateComponentsFromAttribs() {
        if (this.attribs.containsKey("bgcolor")) {
            this.bgColorPanel.setSelected(true);
            this.bgColorPanel.setColor(this.attribs.get("bgcolor").toString());
        }

        this.alignPanel.updateComponentsFromAttribs();
    }

    @Override
    public void updateAttribsFromComponents() {
        if (this.bgColorPanel.isSelected()) {
            this.attribs.put("bgcolor", this.bgColorPanel.getColor());
        } else {
            this.attribs.remove("bgcolor");
        }
        this.alignPanel.updateAttribsFromComponents();
    }

    public void setComponentStates(Map attribs) {
        if (attribs.containsKey("bgcolor")) {
            this.bgColorPanel.setSelected(true);
            this.bgColorPanel.setColor(attribs.get("bgcolor").toString());
        }

        this.alignPanel.setComponentStates(attribs);

    }

    @Override
    public void setAttributes(Map attr) {
        this.alignPanel.setAttributes(attr);
        super.setAttributes(attr);
    }

    /**
     * This method initializes this
     * @return void
     */
    protected void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.gridy = 2;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.gridy = 1;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(279, 140);
        this.setPreferredSize(new java.awt.Dimension(215, 140));
        this.add(this.getAlignPanel(), gridBagConstraints);
        this.add(this.getBgColorPanel(), gridBagConstraints1);
        this.add(this.getExpansionPanel(), gridBagConstraints2);
    }

    /**
     * This method initializes alignPanel
     * @return javax.swing.JPanel
     */
    protected AlignmentAttributesPanel getAlignPanel() {
        if (this.alignPanel == null) {
            this.alignPanel = new AlignmentAttributesPanel();
        }
        return this.alignPanel;
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

        if (this.bgColorPanel != null) {
            this.bgColorPanel.setResourceBundle(resourceBundle);
        }
        if (this.alignPanel != null) {
            this.alignPanel.setResourceBundle(resourceBundle);
        }
    }

}
