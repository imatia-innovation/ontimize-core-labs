package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;

import com.ontimize.gui.field.html.HTMLUtils;
import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;

public class BGColorPanel extends JPanel implements Internationalization {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance();

    protected JCheckBox bgColorCB = null;

    protected JPanel colorPanel = null;

    protected JButton colorButton = null;

    protected Color selColor = Color.WHITE;

    /**
     * This is the default constructor
     */
    public BGColorPanel() {
        super();
        this.initialize();
    }

    public void setSelected(boolean sel) {
        this.bgColorCB.setSelected(sel);
        this.colorButton.setEnabled(sel);
    }

    public boolean isSelected() {
        return this.bgColorCB.isSelected();
    }

    public String getColor() {
        return HTMLUtils.colorToHex(this.selColor);
    }

    public void setColor(String hexColor) {
        this.selColor = HTMLUtils.stringToColor(hexColor);
        this.colorPanel.setBackground(this.selColor);
    }

    /**
     * This method initializes this
     * @return void
     */
    protected void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 2;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.gridy = 0;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 1;
        gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints1.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints1.gridy = 0;
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        gridBagConstraints.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(175, 30);
        this.setPreferredSize(new java.awt.Dimension(175, 30));
        this.setMinimumSize(this.getPreferredSize());
        this.setMaximumSize(this.getPreferredSize());
        this.add(this.getBgColorCB(), gridBagConstraints);
        this.add(this.getColorPanel(), gridBagConstraints1);
        this.add(this.getColorButton(), gridBagConstraints2);

        this.colorButton.setEnabled(this.bgColorCB.isSelected());
    }

    /**
     * This method initializes bgColorCB
     * @return javax.swing.JCheckBox
     */
    protected JCheckBox getBgColorCB() {
        if (this.bgColorCB == null) {
            this.bgColorCB = new JCheckBox();
            this.bgColorCB.setText(BGColorPanel.i18n.str("HTMLShef.background"));

            this.bgColorCB.addItemListener(new java.awt.event.ItemListener() {

                @Override
                public void itemStateChanged(java.awt.event.ItemEvent e) {
                    BGColorPanel.this.colorButton.setEnabled(BGColorPanel.this.bgColorCB.isSelected());
                    if (BGColorPanel.this.bgColorCB.isSelected()) {
                        BGColorPanel.this.colorPanel.setBackground(BGColorPanel.this.selColor);
                    } else {
                        BGColorPanel.this.colorPanel.setBackground(BGColorPanel.this.getBackground());
                    }
                }
            });
        }
        return this.bgColorCB;
    }

    /**
     * This method initializes colorPanel
     * @return javax.swing.JPanel
     */
    protected JPanel getColorPanel() {
        if (this.colorPanel == null) {
            this.colorPanel = new JPanel();
            this.colorPanel.setPreferredSize(new java.awt.Dimension(50, 20));
            this.colorPanel
                .setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        }
        return this.colorPanel;
    }

    /**
     * This method initializes colorButton
     * @return javax.swing.JButton
     */
    protected JButton getColorButton() {
        if (this.colorButton == null) {
            this.colorButton = new JButton();
            this.colorButton.setIcon(ImageManager.getIcon(ImageManager.CHOOSE_COLOR));
            this.colorButton.setPreferredSize(new java.awt.Dimension(20, 20));
            this.colorButton.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    Color c = JColorChooser.showDialog(BGColorPanel.this, BGColorPanel.i18n.str("HTMLShef.color"),
                            BGColorPanel.this.selColor);
                    if (c != null) {
                        BGColorPanel.this.selColor = c;
                        BGColorPanel.this.colorPanel.setBackground(c);
                        BGColorPanel.this.colorPanel.setToolTipText(HTMLUtils.colorToHex(c));
                    }
                }
            });
        }
        return this.colorButton;
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        if (this.bgColorCB != null) {
            this.bgColorCB.setText(BGColorPanel.i18n.str("HTMLShef.background"));
        }
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
