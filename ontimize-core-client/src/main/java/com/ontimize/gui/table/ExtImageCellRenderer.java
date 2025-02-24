package com.ontimize.gui.table;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import com.ontimize.jee.common.util.remote.BytesBlock;

public class ExtImageCellRenderer extends CellRenderer {

    int width = -1;

    int height = -1;

    boolean keepAspectRatio = true;

    public ExtImageCellRenderer() {
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

    public ExtImageCellRenderer(int width, int height, boolean keepAspectRatio) {
        if ((width <= 0) && (height <= 0)) {
            throw new IllegalArgumentException("One of the width or height parameters must be greater than  0");
        }
        this.width = width;
        this.height = height;
        this.keepAspectRatio = keepAspectRatio;
        this.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean hasFocus,
            int row, int column) {

        byte[] data = null;
        if (value instanceof BytesBlock) {
            data = ((BytesBlock) value).getBytes();
        } else if (value instanceof byte[]) {
            data = (byte[]) value;
        }
        Component component = super.getTableCellRendererComponent(table, null, selected, hasFocus, row, column);
        if (data != null) {
            ImageIcon im = new ImageIcon(data);
            if ((this.width != -1) && (this.height != -1)) {
                if (this.keepAspectRatio) {
                    // Minimum scale sets the limit
                    double scaleX = this.width / (double) im.getIconWidth();
                    double scaleY = this.height / (double) im.getIconHeight();
                    double scale = Math.min(scaleX, scaleY);
                    int widthE = (int) (im.getIconWidth() * scale);
                    int heightE = (int) (im.getIconHeight() * scale);
                    java.awt.Image i = im.getImage().getScaledInstance(widthE, heightE, java.awt.Image.SCALE_FAST);
                    im = new ImageIcon(i);
                } else {
                    // Minimum scale sets the limit
                    double scaleX = this.width / (double) im.getIconWidth();
                    double scaleY = this.height / (double) im.getIconHeight();
                    int widthE = (int) (im.getIconWidth() * scaleX);
                    int heightE = (int) (im.getIconHeight() * scaleY);
                    java.awt.Image i = im.getImage().getScaledInstance(widthE, heightE, java.awt.Image.SCALE_FAST);
                    im = new ImageIcon(i);
                }
            } else if (this.width != -1) {
                int eqHeight = (int) ((this.width / (double) im.getIconWidth()) * im.getIconHeight());
            } else if (this.height != -1) {
                int eqWidth = (int) ((this.height / (double) im.getIconHeight()) * im.getIconWidth());
            }

            if (table != null) {
                int height = Math.max(20, im != null ? im.getIconHeight() : 0);
                if (Math.abs(table.getRowHeight(row) - height) > 5) {
                    table.setRowHeight(row, height);
                }
            }
            ((JLabel) component).setIcon(im);
        } else {
            ((JLabel) component).setIcon(null);
        }
        return component;
    }

}
