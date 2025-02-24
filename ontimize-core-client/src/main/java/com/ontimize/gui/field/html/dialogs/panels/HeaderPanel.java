package com.ontimize.gui.field.html.dialogs.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.ontimize.gui.field.html.utils.I18n;
import com.ontimize.gui.i18n.Internationalization;

public class HeaderPanel extends JPanel implements Internationalization {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private static final I18n i18n = I18n.getInstance();

    protected JLabel titleLabel = null;

    protected JLabel msgLabel = null;

    protected JLabel iconLabel = null;

    protected String title;

    protected String desc;

    /**
     * This is the default constructor
     */
    public HeaderPanel() {
        super();
        this.initialize();
    }

    public HeaderPanel(String title, String desc, Icon ico) {
        super();
        this.title = title;
        this.desc = desc;
        this.initialize();
        this.setTitle(title);
        this.setDescription(desc);
        this.setIcon(ico);

    }

    public void setTitle(String title) {
        this.title = title;
        this.titleLabel.setText(title != null ? HeaderPanel.i18n.str(title) : title);
    }

    public void setDescription(String desc) {
        this.desc = desc;
        this.msgLabel.setText(desc != null ? HeaderPanel.i18n.str(desc) : desc);
    }

    public void setIcon(Icon icon) {
        this.iconLabel.setIcon(icon);
    }

    /**
     * This method initializes this
     * @return void
     */
    protected void initialize() {
        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 1;
        gridBagConstraints3.gridheight = 2;
        gridBagConstraints3.insets = new java.awt.Insets(0, 5, 0, 10);
        gridBagConstraints3.gridy = 0;
        this.iconLabel = new JLabel();
        this.iconLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.insets = new java.awt.Insets(2, 25, 0, 0);
        gridBagConstraints2.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;
        gridBagConstraints2.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints2.gridy = 1;
        this.msgLabel = new JLabel();
        this.msgLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        this.msgLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 0, 0);
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridy = 0;
        this.titleLabel = new JLabel();
        this.titleLabel.setFont(new Font("Dialog", Font.BOLD, 16));
        this.titleLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        this.setLayout(new GridBagLayout());
        this.setSize(360, 56);
        this.setPreferredSize(new java.awt.Dimension(360, 56));
        this.add(this.titleLabel, gridBagConstraints);
        this.add(this.msgLabel, gridBagConstraints2);
        this.add(this.iconLabel, gridBagConstraints3);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Rectangle bounds = this.getBounds();

        // Set Paint for filling Shape
        Color init = this.getBackground();
        Color end = UIManager.get("Label.foreground") != null ? (Color) UIManager.get("Label.foreground")
                : new Color(99, 135, 160);

        Paint gradientPaint = new GradientPaint(bounds.width * 0.5f, bounds.y, init, bounds.width, 0f, end);
        g2.setPaint(gradientPaint);
        g2.fillRect(0, 0, bounds.width, bounds.height);

        g2.setPaint(end);
        g2.drawLine(0, bounds.height - 1, bounds.width, bounds.height - 1);
    }

    @Override
    public void setComponentLocale(Locale l) {

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        if (this.title != null) {
            this.titleLabel.setText(HeaderPanel.i18n.str(this.title));
        }
        if (this.desc != null) {
            this.msgLabel.setText(HeaderPanel.i18n.str(this.desc));
        }
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
