package com.ontimize.util.twain;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TwainUtilities {

    private static final Logger logger = LoggerFactory.getLogger(TwainUtilities.class);

    private static boolean twainEnabled = false;

    private static void check() {
        try {
            Class.forName("SK.gnome.twain.TwainManager");
            TwainUtilities.logger.trace("TwainUtilities: JavaTwain classes found");
            TwainUtilities.twainEnabled = true;
        } catch (UnsatisfiedLinkError err) {
            TwainUtilities.logger.trace("TwainUtilities: No JavaTwain classes found " + err.getMessage(), err);
            TwainUtilities.twainEnabled = false;
        } catch (ClassNotFoundException e) {
            TwainUtilities.logger.trace("TwainUtilities: No JavaTwain classes found " + e.getMessage(), e);
            TwainUtilities.twainEnabled = false;
        }catch (NoClassDefFoundError error) {
        	TwainUtilities.logger.trace("TwainUtilities: No JavaTwain classes found " + error.getMessage(), error);
            TwainUtilities.twainEnabled = false;
        }
    }

    static {
        TwainUtilities.check();
    }

    public static boolean isTwainEnabled() {
        return TwainUtilities.twainEnabled;
    }

    public static Rectangle2D.Double convertPixelsToMM(double resX, double resY, Rectangle2D.Double r) {
        // An inch have resX pixels in the x axis and resY in Y axis.
        double xInches = r.x / resX;
        double yInches = r.y / resY;

        double inchesWidth = r.width / resX;
        double inchesHeight = r.height / resY;

        Rectangle2D.Double res = new Rectangle.Double(25.4 * xInches, 25.4 * yInches, 25.4 * inchesWidth,
                25.4 * inchesHeight);
        TwainUtilities.logger.debug("Rectangle converted from pixels to mm: " + r + " -> " + res);
        return res;
    }

    public static Rectangle2D.Double convertMMToPixels(double resX, double resY, Rectangle2D.Double r) {
        // An inch have resX pixels in the x axis and resY in Y axis.
        double xPixels = (r.x * resX) / 25.4;
        double yPixels = (r.y * resY) / 25.4;

        double dPixelsWidth = (r.width * resX) / 25.4;
        double dPixelsHeight = (r.height * resY) / 25.4;

        Rectangle2D.Double res = new Rectangle2D.Double(xPixels, yPixels, dPixelsWidth, dPixelsHeight);
        TwainUtilities.logger.debug("Rectangle converted from mm to pixels: " + r + " -> " + res);
        return res;
    }

    public static Rectangle2D.Double convertPixelsToInches(double resX, double resY, Rectangle2D.Double r) {
        if (r == null) {
            return null;
        }
        // An inch have resX pixels in the x axis and resY in Y axis.
        double inchesX = r.x / resX;
        double inchesY = r.y / resY;

        double widthInches = r.width / resX;
        double heightInches = r.height / resY;

        return new Rectangle2D.Double(inchesX, inchesY, widthInches, heightInches);
    }

    public static Rectangle2D.Double convertMMtoInches(Rectangle2D.Double r) {
        if (r == null) {
            return null;
        }
        // An inch have resX pixels in the x axis and resY in Y axis.
        double xPixels = r.x / 25.4;
        double yPixels = r.y / 25.4;

        double pixelWidth = r.width / 25.4;
        double pixelsHeight = r.height / 25.4;

        return new Rectangle2D.Double(xPixels, yPixels, pixelWidth, pixelsHeight);
    }

    public static Rectangle2D.Double convertInchesToPixels(double resX, double resY, Rectangle2D.Double r) {
        if (r == null) {
            return null;
        }
        // / An inch have resX pixels in the x axis and resY in Y axis.
        double pixelesX = r.x * resX;
        double pixelesY = r.y * resY;

        double pixelsWidth = r.width * resX;
        double pixelsHeight = r.height * resY;

        return new Rectangle2D.Double(pixelesX, pixelesY, pixelsWidth, pixelsHeight);
    }

    public static BufferedImage toBufferedImage(Image im, int type) {
        BufferedImage image = new BufferedImage(im.getWidth(null), im.getHeight(null), type);
        image.getGraphics().drawImage(im, 0, 0, null);
        return image;
    }

    public static BufferedImage toBufferedImage(Image im) {
        BufferedImage image = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);// BufferedImage.TYPE_INT_RGB);
        image.getGraphics().drawImage(im, 0, 0, null);
        return image;
    }

}
