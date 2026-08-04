package com.ontimize.util;

import java.awt.Color;
import java.awt.Shape;
import java.awt.Window;
import java.lang.reflect.Method;

import javax.swing.RootPaneContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWTUtilities {

    private static final Logger logger = LoggerFactory.getLogger(AWTUtilities.class);

    // com.sun.awt.AWTUtilities.setWindowOpaque(this, false);
    public static void setWindowOpaque(final Window w, final boolean opaque) {
		if (w == null) {
			return;
		}

        try {

			if (opaque) {
				w.setBackground(new Color(0, 0, 0, 255));
			} else {
				w.setBackground(new Color(0, 0, 0, 0));
            }

		} catch (final Exception ex) {
			logger.trace(null, ex);
        }
    }


    // com.sun.awt.AWTUtilities.setWindowOpacity(this, 0.8f);
    public static void setWindowOpacity(final Window w, final float opacity) {
        try {
            try {
                // java 7
                final Method method = w.getClass().getMethod("setOpacity", new Class[] { float.class });
                method.invoke(w, new Object[] { Float.valueOf(opacity) });
                return;
            } catch (final Exception thr) {
                AWTUtilities.logger.trace(null, thr);
            }

            final Class awtUtilitites = Class.forName("com.sun.awt.AWTUtilities");
            final Method method = awtUtilitites.getMethod("setWindowOpacity", new Class[] { Window.class, float.class });
            method.invoke(null, new Object[] { w, Float.valueOf(opacity) });
        } catch (final Exception thr) {
            AWTUtilities.logger.trace(null, thr);
            try {
                if (w instanceof RootPaneContainer) {
                    ((RootPaneContainer) w).getRootPane().putClientProperty("Window.alpha", Float.valueOf(opacity));
                }
            } catch (final Exception e) {
                AWTUtilities.logger.trace(null, e);
            }
        }
    }

    // setWindowShape
    // AWTUtilities.setWindowShape(this, new RoundRectangle2D.Float(0, 0,
    // this.getWidth(), this.getHeight(), 20, 20));
    public static void setWindowShape(final Window w, final Shape shape) {
        try {
            final Class awtUtilitites = Class.forName("com.sun.awt.AWTUtilities");
            final Method method = awtUtilitites.getMethod("setWindowShape", new Class[] { Window.class, Shape.class });
            method.invoke(null, new Object[] { w, shape });
        } catch (final Exception thr) {
            AWTUtilities.logger.trace(null, thr);
        }
    }

    // this.setAlwaysOnTop(true);
    public static void setAlwaysOnTop(final Window w, final boolean always) {
        try {
            final Method method = w.getClass().getMethod("setAlwaysOnTop", new Class[] { boolean.class });
            method.invoke(null, new Object[] { Boolean.valueOf(always) });
        } catch (final Exception thr) {
            AWTUtilities.logger.trace(null, thr);
        }
    }

}
