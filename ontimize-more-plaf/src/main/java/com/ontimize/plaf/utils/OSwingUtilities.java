/*
 * Copyright (c) 2009 Kathryn Huxtable and Kenneth Orr.
 *
 * This file is part of the Ontimize Pluggable Look and Feel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * $Id: OSwingUtilities.java,v 1.1 2026/04/19 12:00:00 daniel.grana Exp $
 */
package com.ontimize.plaf.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 * Swing utilities that replace sun.swing.SwingUtilities2 internal API with public API.
 * This class provides functionality to work with fonts and strings without depending on
 * Sun's internal classes.
 *
 * @author Imatia Innovation
 */
public class OSwingUtilities {
    /**
	 * Returns the FontMetrics for the current Font of the passed in Graphics. This method is used when a Graphics is
	 * available, typically when painting. If a Graphics is not available the JComponent method of the same name should
	 * be used.
	 * <p>
	 * Callers should pass in a non-null JComponent, the exception to this is if a JComponent is not readily available
	 * at the time of painting.
	 * <p>
	 * This does not necessarily return the FontMetrics from the Graphics.
	 *
	 * @param c
	 *            JComponent requesting FontMetrics, may be null
	 * @param g
	 *            Graphics Graphics
	 */
	public static FontMetrics getFontMetrics(final JComponent c, final Graphics g) {
		return getFontMetrics(c, g, g.getFont());
	}

	/**
	 * Returns the FontMetrics for the specified Font. This method is used when a Graphics is available, typically when
	 * painting. If a Graphics is not available the JComponent method of the same name should be used.
	 * <p>
	 * Callers should pass in a non-null JComonent, the exception to this is if a JComponent is not readily available at
	 * the time of painting.
	 * <p>
	 * This does not necessarily return the FontMetrics from the Graphics.
	 *
	 * @param c
	 *            JComponent requesting FontMetrics, may be null
	 * @param c
	 *            Graphics Graphics
	 * @param font
	 *            Font to get FontMetrics for
	 */
	public static FontMetrics getFontMetrics(final JComponent c, final Graphics g,
			final Font font) {
		if (c != null) {
			// Note: We assume that we're using the FontMetrics
			// from the widget to layout out text, otherwise we can get
			// mismatches when printing.
			return c.getFontMetrics(font);
		}
		return Toolkit.getDefaultToolkit().getFontMetrics(font);
	}

	/**
	 * Returns the FontMetrics for the current Font of the Graphics.
	 * Simple wrapper for Graphics.getFontMetrics().
	 *
	 * @param g the Graphics context
	 * @return the FontMetrics for the current font in the Graphics
	 */
	public static FontMetrics getFontMetrics(final Graphics g) {
		return g.getFontMetrics();
	}

    /**
     * Clips a string if it exceeds the available width.
     * This is a replacement for sun.swing.SwingUtilities2.clipStringIfNecessary()
     *
     * @param fm the FontMetrics to use for measuring
     * @param string the string to clip
     * @param availableWidth the available width
     * @return the clipped string, or the original if it fits
     */
    public static String clipStringIfNecessary(final FontMetrics fm, final String string, final int availableWidth) {
        if (string == null || string.isEmpty()) {
            return string;
        }

        final int width = fm.stringWidth(string);
        if (width <= availableWidth) {
            return string;
        }

        // String is too wide, we need to clip it
        // Use "..." as the ellipsis
        final String ellipsis = "...";
        final int ellipsisWidth = fm.stringWidth(ellipsis);

        if (ellipsisWidth > availableWidth) {
            // Not enough space even for ellipsis
            return ellipsis.substring(0, 1);
        }

        // Binary search for the right number of characters
        final int availableWidthForString = availableWidth - ellipsisWidth;
        final int length = string.length();

        // Start from the end and work backwards
        for (int i = length - 1; i >= 0; i--) {
            final String substring = string.substring(0, i);
            if (fm.stringWidth(substring) <= availableWidthForString) {
                return substring + ellipsis;
            }
        }

        return ellipsis;
    }

	/**
	 * Change focus to the visible component in {@code JTabbedPane}. This is not a general-purpose method and is here
	 * only to permit sharing code.
	 */
	public static boolean tabbedPaneChangeFocusTo(final Component comp) {
		if (comp != null) {
			if (comp.isFocusTraversable()) {
				compositeRequestFocus(comp);
				return true;
			} else if (comp instanceof JComponent
					&& ((JComponent) comp).requestDefaultFocus()) {

				return true;
			}
		}

		return false;
	}

	// At this point we need this method here. But we assume that there
	// will be a common method for this purpose in the future releases.
	public static Component compositeRequestFocus(final Component component) {
		if (component instanceof Container) {
			final Container container = (Container) component;
			if (container.isFocusCycleRoot()) {
				final FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
				final Component comp = policy.getDefaultComponent(container);
				if (comp != null) {
					comp.requestFocus();
					return comp;
				}
			}
			final Container rootAncestor = container.getFocusCycleRootAncestor();
			if (rootAncestor != null) {
				final FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
				final Component comp = policy.getComponentAfter(rootAncestor, container);

				if (comp != null && SwingUtilities.isDescendingFrom(comp, container)) {
					comp.requestFocus();
					return comp;
				}
			}
		}
		if (component.isFocusable()) {
			component.requestFocus();
			return component;
		}
		return null;
	}

    /**
     * Private constructor to prevent instantiation.
     */
    private OSwingUtilities() {
    }

}