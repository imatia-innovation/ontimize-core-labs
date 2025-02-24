package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.images.ImageManager;

public class TableWaitPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(TableWaitPanel.class);

    public TableWaitPanel() {
        super();
        this.build();
    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(new Color(0, 0, 0, 65));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintComponent(g);
    }

    /**
     * Construye el panel de espera
     */
    protected void build() {
        this.setLayout(new GridBagLayout());
        this.addMouseListener(new MouseListener() {

            @Override
            public void mouseReleased(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                e.consume();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                e.consume();
            }
        });

        // ApplicationManager.getTranslation("table.query_data")
        JLabel label = new JLabel(ImageManager.getIcon(ImageManager.TABLE_WORKING), SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(24f));
        label.setForeground(new Color(0xdddddd));

        // DrawablePanel label = new
        // DrawablePanel("loading_sprites_256.png",
        // 256);
        JPanel filler = new JPanel();
        filler.setOpaque(false);
        JPanel filler2 = new JPanel();
        filler2.setOpaque(false);
        JButton cancelButton = new JButton(ApplicationManager.getTranslation("table.cancel_refresh_button"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Table table = (Table) SwingUtilities.getAncestorOfClass(Table.class, (Component) e.getSource());
                table.checkRefreshThread();
            }
        });

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(label, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTH,
                GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
        centerPanel.add(cancelButton, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTH,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 10, 0));

        // centerPanel.setBorder(new LineBorder(Color.red));
        // label.setBorder(new LineBorder(Color.yellow));

        this.add(filler, new GridBagConstraints(0, 0, 0, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(centerPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.add(filler2, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.SOUTH,
                GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

        this.setOpaque(false);
        label.setOpaque(false);
    }

    protected class DrawablePanel extends JPanel {

        protected List images;

        protected boolean show = true;

        protected JLabel centerLabel = new JLabel();

        protected int refreshTime = 100;

        public DrawablePanel(String imagePath, int width) {
            this.setLayout(new BorderLayout());
            this.add(this.centerLabel);
            ImageIcon imageIcon = ImageManager.getIcon(imagePath);
            this.images = this.getImages(imageIcon, width);
            Thread t = new Thread("Splash") {

                @Override
                public void run() {
                    int index = 0;
                    while (DrawablePanel.this.show) {
                        try {
                            Thread.sleep(DrawablePanel.this.refreshTime);
                            DrawablePanel.this.centerLabel.setIcon((ImageIcon) DrawablePanel.this.images.get(index));
                            index++;
                            index = index % DrawablePanel.this.images.size();
                        } catch (InterruptedException e) {
                            TableWaitPanel.logger.error(null, e);
                        }

                    }
                };
            };
            t.start();
        }

        protected List getImages(ImageIcon panel, int width) {
            List images = new ArrayList();

            int number = panel.getIconWidth() / width;
            int height = panel.getIconHeight();
            for (int i = 0; i < number; i++) {
                BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics g = buffer.createGraphics();
                g.drawImage(panel.getImage(), 0, 0, width, height, width * i, 0, width * (i + 1), height, null);
                ImageIcon current = new ImageIcon(buffer);
                images.add(current);
            }
            return images;
        }

    }

}
