package com.ontimize.plaf.ui;

import java.awt.Adjustable;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthUI;

import com.ontimize.plaf.OntimizeLookAndFeel;
import com.ontimize.plaf.component.OntimizeArrowButton;
import com.ontimize.plaf.utils.ContextUtils;

public class OScrollBarUI extends BasicScrollBarUI implements PropertyChangeListener, SynthUI {

    protected SynthStyle style;

    protected SynthStyle thumbStyle;

    protected SynthStyle trackStyle;

    protected boolean validMinimumThumbSize;

    protected int scrollBarWidth;

    // These two variables should be removed when the corrosponding ones in BasicScrollBarUI are made
    // protected
    protected int incrGap;

    protected int decrGap;

    public static ComponentUI createUI(final JComponent c) {
        return new OScrollBarUI();
    }

    @Override
	protected void installDefaults() {
        // NOTE: This next line of code was added because, since incrGap and decrGap in
        // BasicScrollBarUI are protected, I need to have some way of updating them.
        // This is an incomplete solution (since it implies that the incrGap and decrGap
        // are set once, and not reset per state. Probably ok, but not always ok).
        // This line of code should be removed at the same time that incrGap and
        // decrGap are removed and made protected in the super class.
        super.installDefaults();

        trackHighlight = NO_HIGHLIGHT;
        if (scrollbar.getLayout() == null ||
                (scrollbar.getLayout() instanceof UIResource)) {
            scrollbar.setLayout(this);
        }
        updateStyle(scrollbar);
    }

    @Override
	protected void configureScrollBarColors() {
    }

    protected void updateStyle(final JScrollBar c) {
        final SynthStyle oldStyle = style;
        SynthContext context = getContext(c, ENABLED);
        style = OntimizeLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            scrollBarWidth = style.getInt(context, "ScrollBar.thumbHeight", 14);

            minimumThumbSize = (Dimension) style.get(context,
                    "ScrollBar.minimumThumbSize");
            if (minimumThumbSize == null) {
                minimumThumbSize = new Dimension();
                validMinimumThumbSize = false;
            } else {
                validMinimumThumbSize = true;
            }
            maximumThumbSize = (Dimension) style.get(context,
                    "ScrollBar.maximumThumbSize");
            if (maximumThumbSize == null) {
                maximumThumbSize = new Dimension(4096, 4097);
            }

            incrGap = style.getInt(context, "ScrollBar.incrementButtonGap", 0);
            decrGap = style.getInt(context, "ScrollBar.decrementButtonGap", 0);

            // handle scaling for sizeVarients for special case components. The
            // key "JComponent.sizeVariant" scales for large/small/mini
            // components are based on Apples LAF
            final String scaleKey = (String) scrollbar.getClientProperty(
                    "JComponent.sizeVariant");
            if (scaleKey != null) {
                if ("large".equals(scaleKey)) {
                    scrollBarWidth *= 1.15;
                    incrGap *= 1.15;
                    decrGap *= 1.15;
                } else if ("small".equals(scaleKey)) {
                    scrollBarWidth *= 0.857;
                    incrGap *= 0.857;
                    decrGap *= 0.857;
                } else if ("mini".equals(scaleKey)) {
                    scrollBarWidth *= 0.714;
                    incrGap *= 0.714;
                    decrGap *= 0.714;
                }
            }

            if (oldStyle != null) {
                uninstallKeyboardActions();
                installKeyboardActions();
            }
        }


        context = getContext(c, Region.SCROLL_BAR_TRACK, ENABLED);
        trackStyle = OntimizeLookAndFeel.updateStyle(context, this);


        context = getContext(c, Region.SCROLL_BAR_THUMB, ENABLED);
        thumbStyle = OntimizeLookAndFeel.updateStyle(context, this);

    }

    @Override
	protected void installListeners() {
        super.installListeners();
        scrollbar.addPropertyChangeListener(this);
    }

    @Override
	protected void uninstallListeners() {
        super.uninstallListeners();
        scrollbar.removePropertyChangeListener(this);
    }

    @Override
	protected void uninstallDefaults() {
        SynthContext context = getContext(scrollbar, ENABLED);
        style.uninstallDefaults(context);

        style = null;

        context = getContext(scrollbar, Region.SCROLL_BAR_TRACK, ENABLED);
        trackStyle.uninstallDefaults(context);

        trackStyle = null;

        context = getContext(scrollbar, Region.SCROLL_BAR_THUMB, ENABLED);
        thumbStyle.uninstallDefaults(context);

        thumbStyle = null;

        super.uninstallDefaults();
    }


    @Override
	public SynthContext getContext(final JComponent c) {
        return getContext(c, getComponentState(c));
    }

    protected SynthContext getContext(final JComponent c, final int state) {
        if (this.style == null) {
            this.style = OntimizeLookAndFeel.getOntimizeStyle(c, OntimizeLookAndFeel.getRegion(c));
        }
        return new SynthContext(c,
                OntimizeLookAndFeel.getRegion(c), this.style, state);
    }

    protected Region getRegion(final JComponent c) {
        return OntimizeLookAndFeel.getRegion(c);
    }

    protected int getComponentState(final JComponent c) {
        return OntimizeLookAndFeel.getComponentState(c);
    }

    protected SynthContext getContext(final JComponent c, final Region region) {
        return getContext(c, region, getComponentState(c, region));
    }

    protected SynthContext getContext(final JComponent c, final Region region, final int state) {
        SynthStyle style = this.style;

        if (region == Region.SCROLL_BAR_THUMB) {
            if (this.thumbStyle == null) {
				this.thumbStyle = OntimizeLookAndFeel.getOntimizeStyle(c, region);
            }
            style = thumbStyle;
        } else if (region == Region.SCROLL_BAR_TRACK) {
            if (this.trackStyle == null) {
				this.trackStyle = OntimizeLookAndFeel.getOntimizeStyle(c, region);
            }
            style = trackStyle;
        }
        return new SynthContext(c, region, style, state);
    }

    protected int getComponentState(final JComponent c, final Region region) {
        if (region == Region.SCROLL_BAR_THUMB && isThumbRollover() &&
                c.isEnabled()) {
            return MOUSE_OVER;
        }
        return OntimizeLookAndFeel.getComponentState(c);
    }

    @Override
	public boolean getSupportsAbsolutePositioning() {
        final SynthContext context = getContext(scrollbar);
        final boolean value = style.getBoolean(context,
                "ScrollBar.allowsAbsolutePositioning", false);

        return value;
    }

    @Override
	public void update(final Graphics g, final JComponent c) {
        final SynthContext context = getContext(c);

        OntimizeLookAndFeel.update(context, g);
        ContextUtils.getPainter(context)
            .paintScrollBarBackground(context,
                    g, 0, 0, c.getWidth(), c.getHeight(),
                    scrollbar.getOrientation());
        paint(context, g);

    }

    @Override
	public void paint(final Graphics g, final JComponent c) {
        final SynthContext context = getContext(c);

        paint(context, g);

    }

    protected void paint(final SynthContext context, final Graphics g) {
        SynthContext subcontext = getContext(scrollbar,
                Region.SCROLL_BAR_TRACK);
        paintTrack(subcontext, g, getTrackBounds());

        subcontext = getContext(scrollbar, Region.SCROLL_BAR_THUMB);
        paintThumb(subcontext, g, getThumbBounds());
    }

    @Override
	public void paintBorder(final SynthContext context, final Graphics g, final int x,
            final int y, final int w, final int h) {
        ContextUtils.getPainter(context)
            .paintScrollBarBorder(context, g, x, y, w, h,
                    scrollbar.getOrientation());
    }

    protected void paintTrack(final SynthContext ss, final Graphics g,
            final Rectangle trackBounds) {
        OntimizeLookAndFeel.updateSubregion(ss, g, trackBounds);
        ContextUtils.getPainter(ss)
            .paintScrollBarTrackBackground(ss, g, trackBounds.x,
                    trackBounds.y, trackBounds.width, trackBounds.height,
                    scrollbar.getOrientation());
        ContextUtils.getPainter(ss)
            .paintScrollBarTrackBorder(ss, g, trackBounds.x,
                    trackBounds.y, trackBounds.width, trackBounds.height,
                    scrollbar.getOrientation());
    }

    protected void paintThumb(final SynthContext ss, final Graphics g,
            final Rectangle thumbBounds) {
        OntimizeLookAndFeel.updateSubregion(ss, g, thumbBounds);
        final int orientation = scrollbar.getOrientation();
        ContextUtils.getPainter(ss)
            .paintScrollBarThumbBackground(ss, g, thumbBounds.x,
                    thumbBounds.y, thumbBounds.width, thumbBounds.height,
                    orientation);
        ContextUtils.getPainter(ss)
            .paintScrollBarThumbBorder(ss, g, thumbBounds.x,
                    thumbBounds.y, thumbBounds.width, thumbBounds.height,
                    orientation);
    }

    /**
     * A vertical scrollbar's preferred width is the maximum of preferred widths of the (non
     * <code>null</code>) increment/decrement buttons, and the minimum width of the thumb. The preferred
     * height is the sum of the preferred heights of the same parts. The basis for the preferred size of
     * a horizontal scrollbar is similar.
     * <p>
     * The <code>preferredSize</code> is only computed once, subsequent calls to this method just return
     * a cached size.
     * @param c the <code>JScrollBar</code> that's delegating this method to us
     * @return the preferred size of a Basic JScrollBar
     * @see #getMaximumSize
     * @see #getMinimumSize
     */
    @Override
	public Dimension getPreferredSize(final JComponent c) {
        final Insets insets = c.getInsets();
        return (scrollbar.getOrientation() == Adjustable.VERTICAL)
                ? new Dimension(scrollBarWidth + insets.left + insets.right, 48)
                : new Dimension(48, scrollBarWidth + insets.top + insets.bottom);
    }

    @Override
	protected Dimension getMinimumThumbSize() {
        if (!validMinimumThumbSize) {
            if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
                minimumThumbSize.width = scrollBarWidth;
                minimumThumbSize.height = 7;
            } else {
                minimumThumbSize.width = 7;
                minimumThumbSize.height = scrollBarWidth;
            }
        }
        return minimumThumbSize;
    }

    @Override
	protected JButton createDecreaseButton(final int orientation) {
        final OntimizeArrowButton synthArrowButton = new OntimizeArrowButton(orientation) {
            @Override
            public boolean contains(final int x, final int y) {
                if (decrGap < 0) { // there is an overlap between the track and button
                    int width = getWidth();
                    int height = getHeight();
                    if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
                        // adjust the height by decrGap
                        // Note: decrGap is negative!
                        height += decrGap;
                    } else {
                        // adjust the width by decrGap
                        // Note: decrGap is negative!
                        width += decrGap;
                    }
                    return (x >= 0) && (x < width) && (y >= 0) && (y < height);
                }
                return super.contains(x, y);
            }
        };
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }

    @Override
	protected JButton createIncreaseButton(final int orientation) {
        final OntimizeArrowButton synthArrowButton = new OntimizeArrowButton(orientation) {
            @Override
            public boolean contains(int x, int y) {
                if (incrGap < 0) { // there is an overlap between the track and button
                    int width = getWidth();
                    int height = getHeight();
                    if (scrollbar.getOrientation() == Adjustable.VERTICAL) {
                        // adjust the height and y by incrGap
                        // Note: incrGap is negative!
                        height += incrGap;
                        y += incrGap;
                    } else {
                        // adjust the width and x by incrGap
                        // Note: incrGap is negative!
                        width += incrGap;
                        x += incrGap;
                    }
                    return (x >= 0) && (x < width) && (y >= 0) && (y < height);
                }
                return super.contains(x, y);
            }
        };
        synthArrowButton.setName("ScrollBar.button");
        return synthArrowButton;
    }

    @Override
	protected void setThumbRollover(final boolean active) {
        if (isThumbRollover() != active) {
            scrollbar.repaint(getThumbBounds());
            super.setThumbRollover(active);
        }
    }

    protected void updateButtonDirections() {
        final int orient = scrollbar.getOrientation();
        if (scrollbar.getComponentOrientation().isLeftToRight()) {
            ((OntimizeArrowButton) incrButton).setDirection(
                    orient == HORIZONTAL ? EAST : SOUTH);
            ((OntimizeArrowButton) decrButton).setDirection(
                    orient == HORIZONTAL ? WEST : NORTH);
        } else {
            ((OntimizeArrowButton) incrButton).setDirection(
                    orient == HORIZONTAL ? WEST : SOUTH);
            ((OntimizeArrowButton) decrButton).setDirection(
                    orient == HORIZONTAL ? EAST : NORTH);
        }
    }

    //
    // PropertyChangeListener
    //
    @Override
	public void propertyChange(final PropertyChangeEvent e) {
        final String propertyName = e.getPropertyName();

        if (OntimizeLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JScrollBar) e.getSource());
        }

        if ("orientation" == propertyName) {
            updateButtonDirections();
        } else if ("componentOrientation" == propertyName) {
            updateButtonDirections();
        }
    }

}
