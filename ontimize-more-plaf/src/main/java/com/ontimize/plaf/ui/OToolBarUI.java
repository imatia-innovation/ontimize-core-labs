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
 * $Id: OToolBarUI.java,v 1.6 2013/06/25 06:28:12 daniel.grana Exp $
 */
package com.ontimize.plaf.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicToolBarUI;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthGraphicsUtils;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;

import com.ontimize.plaf.OntimizeLookAndFeel;
import com.ontimize.plaf.utils.ContextUtils;

/**
 * OntimizeToolBarUI implementation.
 *
 * Based on SynthToolBarUI.
 *
 * @see javax.swing.plaf.synth.SynthToolBarUI
 */
public class OToolBarUI extends BasicToolBarUI implements PropertyChangeListener, SynthUI {

    public static int defaultToolBarHeight = 50;

    protected Icon handleIcon = null;

    protected Rectangle contentRect = new Rectangle();

    protected SynthStyle style;

    protected SynthStyle contentStyle;

    protected SynthStyle dragWindowStyle;

    public static ComponentUI createUI(final JComponent c) {
        return new OToolBarUI();
    }

    @Override
    protected void installDefaults() {
        toolBar.setLayout(createLayout());
        updateStyle(toolBar);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        toolBar.addPropertyChangeListener(this);

        // WindowUtils.installJComponentRepainterOnWindowFocusChanged(toolBar);
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
        toolBar.removePropertyChangeListener(this);
    }

    protected void updateStyle(final JToolBar c) {
        SynthContext context = getContext(c, Region.TOOL_BAR_CONTENT, ENABLED);
        contentStyle = OntimizeLookAndFeel.updateStyle(context, this);
        context.getComponent().setOpaque(false);


        context = getContext(c, Region.TOOL_BAR_DRAG_WINDOW, ENABLED);
        context.getComponent().setOpaque(false);
        dragWindowStyle = OntimizeLookAndFeel.updateStyle(context, this);


        context = getContext(c, ENABLED);
        context.getComponent().setOpaque(false);
        final SynthStyle oldStyle = style;

        style = OntimizeLookAndFeel.updateStyle(context, this);
        if (oldStyle != style) {
            handleIcon = style.getIcon(context, "ToolBar.handleIcon");
            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }

    }

    @Override
    protected void uninstallDefaults() {
        SynthContext context = getContext(toolBar, ENABLED);

        style.uninstallDefaults(context);

        style = null;

        handleIcon = null;

        context = getContext(toolBar, Region.TOOL_BAR_CONTENT, contentStyle, ENABLED);
        contentStyle.uninstallDefaults(context);

        contentStyle = null;

        context = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, dragWindowStyle, ENABLED);
        dragWindowStyle.uninstallDefaults(context);

        dragWindowStyle = null;

        toolBar.setLayout(null);
    }

    @Override
    protected void installComponents() {
    }

    @Override
    protected void uninstallComponents() {
    }

    protected LayoutManager createLayout() {
        return new SynthToolBarLayoutManager();
    }

    @Override
	public SynthContext getContext(final JComponent c) {
        return getContext(c, getComponentState(c));
    }

    protected SynthContext getContext(final JComponent c, final int state) {
        if (this.style == null) {
            this.style = OntimizeLookAndFeel.getOntimizeStyle(c, OntimizeLookAndFeel.getRegion(c));
        }
        return new SynthContext(c, OntimizeLookAndFeel.getRegion(c), this.style, state);
    }

    protected SynthContext getContext(final JComponent c, final Region region, final SynthStyle style) {
        return new SynthContext(c, region, style, this.getComponentState(c, region));
    }

    protected SynthContext getContext(final JComponent c, final Region region, final SynthStyle style, final int state) {
        return new SynthContext(c, region, style, state);
    }

    protected SynthContext getContext(final JComponent c, final Region region, final int state) {
        SynthStyle style = this.style;

        if (region == Region.TOOL_BAR_CONTENT) {
            if (this.contentStyle == null) {
				this.contentStyle = OntimizeLookAndFeel.getOntimizeStyle(c, region);
            }
            style = this.contentStyle;
        } else if (region == Region.TOOL_BAR_DRAG_WINDOW) {
            if (this.dragWindowStyle == null) {
				this.dragWindowStyle = OntimizeLookAndFeel.getOntimizeStyle(c, region);
            }
            style = dragWindowStyle;
        }
        return new SynthContext(c, region, style, state);
    }

    protected int getComponentState(final JComponent c) {
        return OntimizeLookAndFeel.getComponentState(c);
    }

    protected int getComponentState(final JComponent c, final Region region) {
        return OntimizeLookAndFeel.getComponentState(c);
    }

    @Override
    public void update(final Graphics g, final JComponent c) {
        final SynthContext context = getContext(c);

        OntimizeLookAndFeel.update(context, g);
        ContextUtils.getPainter(context)
            .paintToolBarBackground(context, g, 0, 0, c.getWidth(), c.getHeight(), toolBar.getOrientation());
        paint(context, g);

    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        final Dimension d = super.getPreferredSize(c);
        return d;
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        final Dimension d = super.getMinimumSize(c);
        return d;
    }

    @Override
    public void paint(final Graphics g, final JComponent c) {
        final SynthContext context = getContext(c);

        paint(context, g);

    }

    @Override
	public void paintBorder(final SynthContext context, final Graphics g, final int x, final int y, final int w, final int h) {
        // We don't want that the Toolbar border was painted.
        // ContextUtils.getPainter(context).paintToolBarBorder(context, g, x, y, w, h,
        // toolBar.getOrientation());
    }

    // Overloaded to do nothing so we can share listeners.
    @Override
    protected void setBorderToNonRollover(final Component c) {
    }

    // Overloaded to do nothing so we can share listeners.
    @Override
    protected void setBorderToRollover(final Component c) {
    }

    // Overloaded to do nothing so we can share listeners.
    @Override
    protected void setBorderToNormal(final Component c) {
    }

    protected void paint(final SynthContext context, final Graphics g) {
        if (handleIcon != null && toolBar.isFloatable()) {
            final int startX = toolBar.getComponentOrientation().isLeftToRight() ? 0 : toolBar.getWidth()
                    - SynthGraphicsUtils.getIconWidth(handleIcon, context);
            SynthGraphicsUtils.paintIcon(handleIcon, context, g, startX, 0,
                    SynthGraphicsUtils.getIconWidth(handleIcon, context), SynthGraphicsUtils.getIconHeight(
                            handleIcon, context));
        }

        final SynthContext subcontext = getContext(toolBar, Region.TOOL_BAR_CONTENT, contentStyle);
        // paintContent(subcontext, g, contentRect);
    }

    public void paintContent(final SynthContext context, final Graphics g, final Rectangle bounds) {
        OntimizeLookAndFeel.updateSubregion(context, g, bounds);
        ContextUtils.getPainter(context)
            .paintToolBarContentBackground(context, g, bounds.x, bounds.y, bounds.width, bounds.height,
                    toolBar.getOrientation());
        ContextUtils.getPainter(context)
            .paintToolBarContentBorder(context, g, bounds.x, bounds.y, bounds.width, bounds.height,
                    toolBar.getOrientation());
    }

    @Override
    protected void paintDragWindow(final Graphics g) {
        final int w = dragWindow.getWidth();
        final int h = dragWindow.getHeight();
        final SynthContext context = getContext(toolBar, Region.TOOL_BAR_DRAG_WINDOW, dragWindowStyle);
        OntimizeLookAndFeel.updateSubregion(context, g, new Rectangle(0, 0, w, h));
        ContextUtils.getPainter(context)
            .paintToolBarDragWindowBackground(context, g, 0, 0, w, h, dragWindow.getOrientation());
        ContextUtils.getPainter(context)
            .paintToolBarDragWindowBorder(context, g, 0, 0, w, h, dragWindow.getOrientation());

    }

    //
    // PropertyChangeListener
    //

    @Override
	public void propertyChange(final PropertyChangeEvent e) {
        if (OntimizeLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JToolBar) e.getSource());
        }
    }

    class SynthToolBarLayoutManager implements LayoutManager {

        @Override
		public void addLayoutComponent(final String name, final Component comp) {
        }

        @Override
		public void removeLayoutComponent(final Component comp) {
        }

        @Override
		public Dimension minimumLayoutSize(final Container parent) {
            final JToolBar tb = (JToolBar) parent;
            final Insets insets = tb.getInsets();
            final Dimension dim = new Dimension();
            final SynthContext context = getContext(tb);

            if (tb.getOrientation() == SwingConstants.HORIZONTAL) {
                dim.width = tb.isFloatable() ? SynthGraphicsUtils.getIconWidth(handleIcon, context) : 0;
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    final Component component = tb.getComponent(i);
                    if (component.isVisible()) {
                        compDim = component.getMinimumSize();
                        dim.width += compDim.width;
                        dim.height = Math.max(dim.height, compDim.height);
                    }
                }
            } else {
                dim.height = tb.isFloatable() ? SynthGraphicsUtils.getIconHeight(handleIcon, context) : 0;
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    final Component component = tb.getComponent(i);
                    if (component.isVisible()) {
                        compDim = component.getMinimumSize();
                        dim.width = Math.max(dim.width, compDim.width);
                        dim.height += compDim.height;
                    }
                }
            }
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;


            return dim;
        }

        @Override
		public Dimension preferredLayoutSize(final Container parent) {
            final JToolBar tb = (JToolBar) parent;
            final Insets insets = tb.getInsets();
            final Dimension dim = new Dimension();
            final SynthContext context = getContext(tb);

            if (tb.getOrientation() == SwingConstants.HORIZONTAL) {
                dim.width = tb.isFloatable() ? SynthGraphicsUtils.getIconWidth(handleIcon, context) : 0;
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    final Component component = tb.getComponent(i);
                    if (component.isVisible()) {
                        compDim = component.getPreferredSize();
                        dim.width += compDim.width;
                        dim.height = Math.max(dim.height, compDim.height);
                    }
                }
            } else {
                dim.height = tb.isFloatable() ? SynthGraphicsUtils.getIconHeight(handleIcon, context) : 0;
                Dimension compDim;
                for (int i = 0; i < tb.getComponentCount(); i++) {
                    final Component component = tb.getComponent(i);
                    if (component.isVisible()) {
                        compDim = component.getPreferredSize();
                        dim.width = Math.max(dim.width, compDim.width);
                        dim.height += compDim.height;
                    }
                }
            }
            dim.width += insets.left + insets.right;
            dim.height += insets.top + insets.bottom;


            return dim;
        }

        @Override
		public void layoutContainer(final Container parent) {
            final JToolBar tb = (JToolBar) parent;
            final Insets insets = tb.getInsets();
            final boolean ltr = tb.getComponentOrientation().isLeftToRight();
            final SynthContext context = getContext(tb);

            Component c;
            Dimension d;

            // JToolBar by default uses a somewhat modified BoxLayout as
            // its layout manager. For compatibility reasons, we want to
            // support Box "glue" as a way to move things around on the
            // toolbar. "glue" is represented in BoxLayout as a Box.Filler
            // with a minimum and preferred size of (0,0).
            // So what we do here is find the number of such glue fillers
            // and figure out how much space should be allocated to them.
            int glueCount = 0;
            for (int i = 0; i < tb.getComponentCount(); i++) {
                if (isGlue(tb.getComponent(i))) {
					glueCount++;
				}
            }

            if (tb.getOrientation() == SwingConstants.HORIZONTAL) {
                final int handleWidth = tb.isFloatable() ? SynthGraphicsUtils.getIconWidth(handleIcon, context) : 0;

                // Note: contentRect does not take insets into account
                // since it is used for determining the bounds that are
                // passed to paintToolBarContentBackground().
                contentRect.x = ltr ? handleWidth : 0;
                contentRect.y = 0;
                contentRect.width = tb.getWidth() - handleWidth;
                contentRect.height = tb.getHeight();

                // However, we do take the insets into account here for
                // the purposes of laying out the toolbar child components.
                int x = ltr ? handleWidth + insets.left : tb.getWidth() - handleWidth - insets.right;
                final int baseY = insets.top;
                final int baseH = tb.getHeight() - insets.top - insets.bottom;

                // we need to get the minimum width for laying things out
                // so that we can calculate how much empty space needs to
                // be distributed among the "glue", if any
                int extraSpacePerGlue = 0;
                if (glueCount > 0) {
                    final int minWidth = minimumLayoutSize(parent).width;
                    extraSpacePerGlue = (tb.getWidth() - minWidth) / glueCount;
                    if (extraSpacePerGlue < 0) {
						extraSpacePerGlue = 0;
					}
                }

                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    if (c.isVisible()) {
                        d = c.getPreferredSize();
                        int y, h;
                        if (d.height >= baseH || c instanceof JSeparator) {
                            // Fill available height
                            y = baseY;
                            h = baseH;
                        } else {
                            // Center component vertically in the available
                            // space
                            y = baseY + (baseH / 2) - (d.height / 2);
                            h = d.height;
                        }
                        // if the component is a "glue" component then add to
                        // its
                        // width the extraSpacePerGlue it is due
                        if (isGlue(c)) {
							d.width += extraSpacePerGlue;
						}
                        c.setBounds(ltr ? x : x - d.width, y, d.width, h);
                        x = ltr ? x + d.width : x - d.width;
                    }
                }
            } else {
                final int handleHeight = tb.isFloatable() ? SynthGraphicsUtils.getIconHeight(handleIcon, context) : 0;

                // See notes above regarding the use of insets
                contentRect.x = 0;
                contentRect.y = handleHeight;
                contentRect.width = tb.getWidth();
                contentRect.height = tb.getHeight() - handleHeight;

                final int baseX = insets.left;
                final int baseW = tb.getWidth() - insets.left - insets.right;
                int y = handleHeight + insets.top;

                // we need to get the minimum height for laying things out
                // so that we can calculate how much empty space needs to
                // be distributed among the "glue", if any
                int extraSpacePerGlue = 0;
                if (glueCount > 0) {
                    final int minHeight = minimumLayoutSize(parent).height;
                    extraSpacePerGlue = (tb.getHeight() - minHeight) / glueCount;
                    if (extraSpacePerGlue < 0) {
						extraSpacePerGlue = 0;
					}
                }

                for (int i = 0; i < tb.getComponentCount(); i++) {
                    c = tb.getComponent(i);
                    if (c.isVisible()) {
                        d = c.getPreferredSize();
                        int x, w;
                        if (d.width >= baseW || c instanceof JSeparator) {
                            // Fill available width
                            x = baseX;
                            w = baseW;
                        } else {
                            // Center component horizontally in the available
                            // space
                            x = baseX + (baseW / 2) - (d.width / 2);
                            w = d.width;
                        }
                        // if the component is a "glue" component then add to
                        // its
                        // height the extraSpacePerGlue it is due
                        if (isGlue(c)) {
							d.height += extraSpacePerGlue;
						}
                        c.setBounds(x, y, w, d.height);
                        y += d.height;
                    }
                }
            }

        }

        protected boolean isGlue(final Component c) {
            if (c.isVisible() && c instanceof Box.Filler) {
                final Box.Filler f = (Box.Filler) c;
                final Dimension min = f.getMinimumSize();
                final Dimension pref = f.getPreferredSize();
                return min.width == 0 && min.height == 0 && pref.width == 0 && pref.height == 0;
            }
            return false;
        }

    }

}
