package com.ontimize.gui.imaging;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.RenderedImage;

import javax.media.jai.PlanarImage;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JImageDisplayJAI extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(JImageDisplayJAI.class);

    protected RenderedImage source = null;

    protected int adjustSize = -1;

    protected double scale = 1.0;

    protected double zoom = 1;

    protected Rectangle selectionRectangle = null;

    protected Dimension pSize = new Dimension(0, 0);

    protected Stroke selStroke = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1.0f,
            new float[] { 5f, 5.0f }, 0.0f);

    public JImageDisplayJAI() {
        this.setLayout(null);
        this.setOpaque(false);
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    public void setZoom(final double zoom) {
        this.zoom = zoom;
        this.revalidate();
        this.repaint();
    }

    public double getZoom() {
        return this.zoom;
    }

    public void setSelectionRectangle(final Rectangle r) {
        if (r == null) {
            this.selectionRectangle = r;
            this.repaint();
            return;
        }

        final Rectangle old = this.selectionRectangle;
        this.selectionRectangle = r;
        int xMin = r.x - 1;
        int yMin = r.y - 1;
        int xMax = (r.x - 1) + r.width + 2;
        int yMax = (r.y - 1) + r.height + 2;

        if (old != null) {
            xMin = Math.min(xMin, old.x - 1);
            yMin = Math.min(yMin, old.y - 1);
            xMax = Math.max(xMax, (old.x - 1) + old.width + 2);
            yMax = Math.max(yMax, (old.y - 1) + old.height + 2);
        }
        this.repaint(new Rectangle(xMin, yMin, xMax - xMin, yMax - yMin));
    }

    public Rectangle getSelectionRectangle() {
        return this.selectionRectangle;
    }

    @Override
    public Dimension getMaximumSize() {
        if (this.adjustSize > 0) {
            return this.getPreferredSize();
        } else {
            return super.getMaximumSize();
        }
    }

    public int getAdjustToSize() {
        return this.adjustSize;
    }

    public void setAdjustToSize(final int s) {
        this.adjustSize = s;
        this.revalidate();
        this.repaint();
    }

    public JImageDisplayJAI(final RenderedImage renderedimage) {
        this.setLayout(null);
        if (renderedimage == null) {
            throw new IllegalArgumentException("image can´t be null");
        }
        this.source = renderedimage;
        final Rectangle rect = this.source.getData().getBounds();

        final int iWidth = rect.width;
        final int iHeight = rect.height;
        final Insets insets = this.getInsets();
        final Dimension dimension = new Dimension(iWidth + insets.left + insets.right, iHeight + insets.top + insets.bottom);
    }

    @Override
    public Dimension getPreferredSize() {
        final Dimension d = this.calculatePreferredSize();
        return d;
    }

    protected Dimension calculatePreferredSize() {
        if (this.source == null) {
            if (this.adjustSize > 0) {
                this.pSize.width = this.adjustSize;
                this.pSize.height = this.adjustSize;
                return this.pSize;
            }
            final Insets insets = this.getInsets();
            this.pSize.width = 200 + insets.left + insets.right;
            this.pSize.height = 200 + insets.top + insets.bottom;
            return this.pSize;
        }
        try {
            if (this.adjustSize > 0) {
                final Rectangle rect = this.source.getData().getBounds();

                final int iWidth = rect.width;
                final int iHeight = rect.height;

                final int maximum = Math.max(iWidth, iHeight);
                double scale = 1.0;
                final Insets insets = this.getInsets();
                if (maximum == iWidth) {
                    // Adjust
                    double currentZoomValue = 1.0;
                    if (this.zoom > 0) {
                        currentZoomValue = this.zoom;
                    }
                    scale = this.adjustSize / (double) iWidth;
                    this.pSize.width = (int) (this.adjustSize * currentZoomValue) + insets.left + insets.right;
                    this.pSize.height = (int) (iHeight * scale * currentZoomValue) + insets.top + insets.bottom;
                    return this.pSize;
                } else {
                    double currentZoomValue = 1.0;
                    if (this.zoom > 0) {
                        currentZoomValue = this.zoom;
                    }
                    scale = this.adjustSize / (double) iHeight;
                    this.pSize.width = (int) (iWidth * scale * currentZoomValue) + insets.left + insets.right;
                    this.pSize.height = (int) (this.adjustSize * currentZoomValue) + insets.top + insets.bottom;
                    return this.pSize;
                }
            } else if (this.zoom > 0) {
                final Rectangle rect = this.source.getData().getBounds();
                final int iWidth = rect.width;
                final int iHeight = rect.height;
                final Insets insets = this.getInsets();
                this.pSize.width = (int) (iWidth * this.zoom) + insets.left + insets.right;
                this.pSize.height = (int) (iHeight * this.zoom) + insets.top + insets.bottom;
                return this.pSize;
            } else {
                final Rectangle rect = this.source.getData().getBounds();
                final int iWidth = rect.width;
                final int iHeight = rect.height;
                final Insets insets = this.getInsets();
                this.pSize.width = iWidth + insets.left + insets.right;
                this.pSize.height = iHeight + insets.top + insets.bottom;
                return this.pSize;
            }
        } catch (final Exception e) {
            JImageDisplayJAI.logger.trace(null, e);
            if (this.adjustSize > 0) {
                return new Dimension(this.adjustSize, this.adjustSize);
            }
            final Insets insets = this.getInsets();
            this.pSize.width = 200 + insets.left + insets.right;
            this.pSize.height = 200 + insets.top + insets.bottom;
            return this.pSize;
        }
    }

    public void set(final RenderedImage renderedimage) {
        this.disposeImage();
        if (renderedimage != null) {
            this.source = renderedimage;
        }
        this.revalidate();
        this.repaint();
    }

    public void set(final RenderedImage renderedimage, final Rectangle r) {
        this.disposeImage();
        if (renderedimage != null) {
            this.source = renderedimage;
        }
        this.revalidate();
        this.repaint(r);
    }

    protected void removeSource() {
        this.source = null;
        this.revalidate();
        this.repaint();
    }

    public RenderedImage getSource() {
        return this.source;
    }

    public double getCurrentScale() {
        return this.scale;
    }

    @Override
    protected synchronized void paintComponent(final Graphics graphics) {
        try {
            final Graphics2D graphics2d = (Graphics2D) graphics;
            if (this.source == null) {
                if (this.isOpaque()) {
                    graphics2d.setColor(this.getBackground());
                    graphics2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                }
            } else {
                final Rectangle rectangle = graphics2d.getClipBounds();
                if ((rectangle.width == 0) || (rectangle.height == 0)) {
                    return;
                }
                if (this.isOpaque()) {
                    graphics2d.setColor(this.getBackground());
                    graphics2d.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                }
                final Insets insets = this.getInsets();
                final int offsetX = insets.left;
                final int offsetY = insets.top;
                final Rectangle rect = this.source.getData().getBounds();

                final int iWidth = rect.width;
                final int iHeight = rect.height;
                final Dimension prefSize = this.getPreferredSize();
                final double scaleX = (prefSize.width - (insets.left + insets.right)) / (double) iWidth;
                final double scaleY = (prefSize.height - (insets.top + insets.bottom)) / (double) iHeight;
                // Get the minimun scale
                this.scale = Math.min(scaleX, scaleY);

                try {
                    final AffineTransform t = new AffineTransform(AffineTransform.getTranslateInstance(offsetX, offsetY));
                    t.concatenate(AffineTransform.getTranslateInstance(-rect.x * this.scale, -rect.y * this.scale));
                    t.concatenate(AffineTransform.getScaleInstance(this.scale, this.scale));
                    graphics2d.drawRenderedImage(this.source, t);
                } catch (final Exception e) {
                    JImageDisplayJAI.logger.error(null, e);
                    try {
                        graphics2d.drawImage(((PlanarImage) this.source).getAsBufferedImage(), offsetX - rect.x,
                                offsetY - rect.y, null);
                    } catch (final Exception e2) {
                        JImageDisplayJAI.logger.trace(null, e2);
                    }
                } catch (final OutOfMemoryError err) {
                    JImageDisplayJAI.logger.error("OutOfMemoryError: ", err);
                }
                if (this.selectionRectangle != null) {
                    graphics2d.setColor(Color.red);
                    final Stroke s = graphics2d.getStroke();
                    if (this.selStroke != null) {
                        graphics2d.setStroke(this.selStroke);
                    }
                    graphics2d.drawRect(this.selectionRectangle.x, this.selectionRectangle.y,
                            this.selectionRectangle.width, this.selectionRectangle.height);
                    graphics2d.setStroke(s);
                }
            }
        } catch (final Exception e) {
            JImageDisplayJAI.logger.trace(null, e);
        }
    }

    public void disposeImage() {
        if (this.source != null) {
            if (this.source instanceof PlanarImage) {
                ((PlanarImage) this.source).dispose();
                this.source = null;
                this.revalidate();
                this.repaint();
            } else {
                this.removeSource();
            }
        }
    }

    public void setSelectionStroke(final Stroke s) {
        this.selStroke = s;
    }

}
