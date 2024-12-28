package com.ontimize.gui.images;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RescaleOp;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.jee.common.xml.XMLClientProvider;

public class DefaultImageManager implements IImageManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultImageManager.class);

    public static final String DEFAULT_IMAGE_PATH = "com/ontimize/gui/images/";

    protected List<String> baseImages;

    protected Map<String, ImageIcon> imageCache;

    public DefaultImageManager() {
        super();
        this.imageCache = new HashMap<String, ImageIcon>();
        this.baseImages = new ArrayList<String>();
        this.baseImages.add(DefaultImageManager.DEFAULT_IMAGE_PATH);
    }

    /**
     * Loads the image corresponding to the image path. Have been rewritten the next paths, in order to
     * use the new system of bundle paths:
     *
     * - com/ontimize/gui/images/
     *
     * All images that include the above routes have been rewritten, eliminating the above paths, and
     * leaving the rest of the path intact.
     * @param icon Relative URI to the resource. If the path contains the above paths or any path
     *        defined by the user in baseImages, must be deleted from URI string. Example, "ok.png"
     *        return the image with complete path "com/ontimize/gui/images/ok.png" If the image is in
     *        various paths, will be return the first occurrence
     * @return an ImageIcon corresponding to the resource, or null if the resource is missing
     */
    @Override
    public ImageIcon getIcon(String icon) {

        if (icon == null) {
            DefaultImageManager.logger.debug("Icon is null");
            return null;
        }

        if (icon.startsWith("/")) {
            icon = icon.substring(1);
        }

        ImageIcon imageIcon = this.imageCache.get(icon);
        if (imageIcon == null) {
            URL url = this.getIconURL(icon);
            if (url == null) {
                if (ImageManager.databaseStorageAllowed) {
                    EntityReferenceLocator locator = ApplicationManager.getApplication().getReferenceLocator();
                    if (locator instanceof XMLClientProvider) {
                        try {
                            int sessionId = locator.getSessionId();
                            BytesBlock oBytesBlock = ((XMLClientProvider) locator).getImage(icon, sessionId);
                            if (oBytesBlock != null) {
                                imageIcon = new ImageIcon(oBytesBlock.getBytes());
                                if (ImageManager.cache) {
                                    this.imageCache.put(icon, imageIcon);
                                }
                                return imageIcon;
                            }
                        } catch (Exception e) {
                            DefaultImageManager.logger.debug("Error loading form from server", e);
                        }
                    }
                }
                DefaultImageManager.logger.debug("Icon {} not found", icon);
            } else {
                imageIcon = new ImageIcon(url);
                if (ImageManager.cache) {
                    this.imageCache.put(icon, imageIcon);
                }
            }

        }
        return imageIcon;
    }

    @Override
    public URL getIconURL(String icon) {
        URL url = null;
        for (int i = 0; (url == null) && (i < this.baseImages.size()); i++) {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.baseImages.get(i));
            buffer.append(icon);
            url = this.getClass().getClassLoader().getResource(buffer.toString());
        }

        if (url == null) {
            url = this.getClass().getClassLoader().getResource(icon);
        }
        return url;
    }

    @Override
    public void addBaseImagePath(String path) {
        StringBuilder buffer = new StringBuilder();
        if (path.startsWith("/")) {
            buffer.append(path.substring(1));
        } else {
            buffer.append(path);
        }
        if (!path.endsWith("/")) {
            buffer.append("/");
        }

        this.baseImages.add(0, buffer.toString());

    }

    @Override
    public List<String> getBaseImagePaths() {
        return this.baseImages;
    }

    @Override
    public void removeBaseImagePath(String path) {
        for (int i = 0; i < this.baseImages.size(); i++) {
            if (path.equals(this.baseImages.get(i))) {
                this.baseImages.remove(i);
            }
        }
    }

    @Override
    public ImageIcon transparent(ImageIcon icon, float trans) {
        BufferedImage buffImage = new BufferedImage(icon.getImage().getWidth(null), icon.getImage().getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw Image into BufferedImage
        Graphics g = buffImage.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);

        // we use an image with an alpha channel
        // therefore, we need 4 components (RGBA)
        float[] factors = new float[] { 1.0f, 1.0f, 1.0f, trans };
        float[] offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };

        RescaleOp op = new RescaleOp(factors, offsets, null);
        BufferedImage brighter = op.filter(buffImage, null);

        // Create empty BufferedImage, sized to Image
        ImageIcon result = new ImageIcon();
        result.setImage(brighter);
        return result;
    }

    @Override
    public ImageIcon brighter(ImageIcon icon) {
        BufferedImage buffImage = new BufferedImage(icon.getImage().getWidth(null), icon.getImage().getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw Image into BufferedImage
        Graphics g = buffImage.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);

        // we use an image with an alpha channel
        // therefore, we need 4 components (RGBA)
        float[] factors = new float[] { 1.15f, 1.15f, 1.15f, 1f };
        float[] offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };

        RescaleOp op = new RescaleOp(factors, offsets, null);
        // RescaleOp op = new RescaleOp(1.1f, 0.0f, null);
        BufferedImage brighter = op.filter(buffImage, null);

        // Create empty BufferedImage, sized to Image
        ImageIcon result = new ImageIcon();
        result.setImage(brighter);
        return result;
    }

    @Override
    public ImageIcon darker(ImageIcon icon) {
        BufferedImage buffImage = new BufferedImage(icon.getImage().getWidth(null), icon.getImage().getHeight(null),
                BufferedImage.TYPE_INT_ARGB);

        // Draw Image into BufferedImage
        Graphics g = buffImage.getGraphics();
        g.drawImage(icon.getImage(), 0, 0, null);

        // we use an image with an alpha channel
        // therefore, we need 4 components (RGBA)
        float[] factors = new float[] { 0.75f, 0.75f, 0.75f, 1f };
        float[] offsets = new float[] { 0.0f, 0.0f, 0.0f, 0.0f };

        RescaleOp op = new RescaleOp(factors, offsets, null);
        // RescaleOp op = new RescaleOp(1.1f, 0.0f, null);
        BufferedImage brighter = op.filter(buffImage, null);

        // Create empty BufferedImage, sized to Image
        ImageIcon result = new ImageIcon();
        result.setImage(brighter);
        return result;
    }

    @Override
    public BufferedImage getBlurImage(BufferedImage image, int radius) {
        image = this.getGaussianBlurFilter(radius, true).filter(image, null);
        image = this.getGaussianBlurFilter(radius, false).filter(image, null);
        return image;
    }

    protected ConvolveOp getGaussianBlurFilter(int radius, boolean horizontal) {
        if (radius < 1) {
            throw new IllegalArgumentException("Radius must be >= 1");
        }
        int size = (radius * 2) + 1;
        float[] data = new float[size];
        float sigma = radius / 3.0f;
        float twoSigmaSquare = 2.0f * sigma * sigma;
        float sigmaRoot = (float) Math.sqrt(twoSigmaSquare * Math.PI);
        float total = 0.0f;
        for (int i = -radius; i <= radius; i++) {
            float distance = i * i;
            int index = i + radius;
            data[index] = (float) Math.exp(-distance / twoSigmaSquare) / sigmaRoot;
            total += data[index];
        }
        for (int i = 0; i < data.length; i++) {
            data[i] /= total;
        }
        Kernel kernel = null;
        if (horizontal) {
            kernel = new Kernel(size, 1, data);
        } else {
            kernel = new Kernel(1, size, data);
        }
        return new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
    }

    @Override
    public void resetImageCache() {
        this.imageCache.clear();
    }

}
