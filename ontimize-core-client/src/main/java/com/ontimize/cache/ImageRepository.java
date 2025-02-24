package com.ontimize.cache;

import java.awt.Component;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageRepository {

	private static final Logger logger = LoggerFactory.getLogger(ImageRepository.class);

	public static boolean DEBUG = false;

	protected static final Component component = new Component() {
	};

	protected static Map images = new Hashtable();

	protected static MediaTracker tracker = null;

	protected static Map ids = new Hashtable();

	public static int COMPLETE = MediaTracker.COMPLETE;

	public static int ERRORED = MediaTracker.ERRORED;

	public static int ABORTED = MediaTracker.ABORTED;

	public static int LOADING = MediaTracker.LOADING;

	/**
	 * Gets an image loading it from the specified URL. If the image is loaded yet returns it, other
	 * case start the image load.<br>
	 * This is a useful method in combination with {@link ImageObserver}
	 * @param imageURL
	 * @return
	 */
	public synchronized static java.awt.Image getImage(final URL imageURL) {
		if (imageURL == null) {
			ImageRepository.logger.debug("Image URL is null");
			return null;
		}
		if (ImageRepository.tracker == null) {
			ImageRepository.tracker = new MediaTracker(ImageRepository.component);
		}
		if (ImageRepository.images.containsKey(imageURL)) {
			return (java.awt.Image) ImageRepository.images.get(imageURL);
		} else {
			final java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
			ImageRepository.images.put(imageURL, image);
			final int idImage = ImageRepository.images.size();
			ImageRepository.ids.put(imageURL, new Integer(idImage));
			ImageRepository.tracker.addImage(image, idImage);
			try {
				ImageRepository.tracker.waitForID(idImage, 1);
				ImageRepository.logger.debug("Init loading of: {}", imageURL);
			} catch (final Exception e) {
				ImageRepository.logger.error("Loading process interrupted: {}", imageURL, e.getMessage(), e);
			}
			return image;
		}
	}

	public synchronized static java.awt.Image getImage(final URL imageURL, final ImageObserver observer) {
		if (imageURL == null) {
			ImageRepository.logger.debug("Image URL is null");
			return null;
		}
		if (ImageRepository.tracker == null) {
			ImageRepository.tracker = new MediaTracker(ImageRepository.component);
		}

		if (ImageRepository.images.containsKey(imageURL)) {
			final java.awt.Image image = (java.awt.Image) ImageRepository.images.get(imageURL);
			if (ImageRepository.getLoadingState(imageURL) == MediaTracker.COMPLETE) {
				observer.imageUpdate(image, ImageObserver.ALLBITS, 0, 0, image.getWidth(observer),
						image.getHeight(observer));
			} else {
				try {
					ImageRepository.tracker.waitForID(((Integer) ImageRepository.ids.get(imageURL)).intValue());
					observer.imageUpdate(image, ImageObserver.ALLBITS, 0, 0, image.getWidth(observer),
							image.getHeight(observer));
				} catch (final Exception e) {
					ImageRepository.logger.error("Loading process interrupted: {}", imageURL, e.getMessage(), e);
				}
			}
			return image;
		} else {
			final java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
			// Register the observer.
			image.getWidth(observer);
			ImageRepository.images.put(imageURL, image);
			final int imageId = ImageRepository.images.size();
			ImageRepository.ids.put(imageURL, new Integer(imageId));
			ImageRepository.tracker.addImage(image, imageId);
			try {
				ImageRepository.tracker.waitForID(imageId);
				ImageRepository.logger.debug("Init loading of: {}", imageURL);
			} catch (final Exception e) {
				ImageRepository.logger.error("Loading process interrupted {}", imageURL, e.getMessage(), e);
			}
			return image;
		}
	}

	/**
	 * Gets an image loading it from the specified URL. This method waits until the image is loaded. If
	 * the image is yet loaded returns it.
	 * @param imageURL
	 * @return
	 */
	public synchronized static java.awt.Image getImageAndWait(final URL imageURL) {
		if (imageURL == null) {
			ImageRepository.logger.debug("Image URL is null");
			return null;
		}
		if (ImageRepository.tracker == null) {
			ImageRepository.tracker = new MediaTracker(ImageRepository.component);
		}
		if (ImageRepository.images.containsKey(imageURL)) {
			final Integer id = (Integer) ImageRepository.ids.get(imageURL);
			if (id == null) {
				ImageRepository.logger.debug("Image Id not found: " + imageURL.toString());
			} else {
				if (ImageRepository.tracker.isErrorID(id.intValue())) {
					ImageRepository.logger.error("Error loading image: {}", imageURL);
				} else {
					try {
						final long t = System.currentTimeMillis();
						ImageRepository.tracker.waitForID(id.intValue());
						ImageRepository.logger.trace("Finished image loading: {}", imageURL);
						ImageRepository.logger.trace("Total loading time {} : {} millis", imageURL,
								System.currentTimeMillis() - t);
					} catch (final Exception e) {
						ImageRepository.logger.error("Loading process interrupted: {}", imageURL, e.getMessage(), e);
					}
				}
			}
			return (java.awt.Image) ImageRepository.images.get(imageURL);
		} else {
			final java.awt.Image image = Toolkit.getDefaultToolkit().getImage(imageURL);
			ImageRepository.images.put(imageURL, image);
			final int imageId = ImageRepository.images.size();
			ImageRepository.ids.put(imageURL, new Integer(imageId));
			ImageRepository.tracker.addImage(image, imageId);
			try {
				final long t = System.currentTimeMillis();
				ImageRepository.tracker.waitForID(imageId);
				ImageRepository.logger.info("Finished image loading: {}", imageURL);
				ImageRepository.logger.trace("Total loading time {} : {} millis", imageURL,
						System.currentTimeMillis() - t);
			} catch (final Exception e) {
				ImageRepository.logger.error("Loading process interrupted {}", imageURL, e.getMessage(), e);
			}
			return image;
		}
	}

	public synchronized static int getLoadingState(final URL imageURL) {
		final Integer id = (Integer) ImageRepository.ids.get(imageURL);
		if (id == null) {
			ImageRepository.logger.error("Image Id not found {}", imageURL);
			return MediaTracker.ERRORED;
		} else {
			return ImageRepository.tracker.statusID(id.intValue(), false);
		}
	}

}
