package com.ontimize.plaf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.LookAndFeel;
import javax.swing.Painter;
import javax.swing.PopupFactory;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthConstants;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;
import javax.swing.plaf.synth.SynthUI;

import com.ontimize.gui.ApToolBarNavigator;
import com.ontimize.gui.ApToolBarPopupButton;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationToolBar;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Form.FormButton;
import com.ontimize.gui.attachment.AttachmentComponent;
import com.ontimize.gui.attachment.AttachmentListPopup;
import com.ontimize.gui.button.FormHeaderButton;
import com.ontimize.gui.button.FormHeaderPopupButton;
import com.ontimize.gui.calendar.VisualCalendarComponent;
import com.ontimize.gui.container.CollapsiblePanel;
import com.ontimize.gui.container.CurveMattedDeployableBorder;
import com.ontimize.gui.container.Grid;
import com.ontimize.gui.container.Row;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DataField.FieldButton;
import com.ontimize.gui.field.HTMLDataField;
import com.ontimize.gui.field.ListDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.CellEditor;
import com.ontimize.gui.table.CellRenderer;
import com.ontimize.gui.table.RowHeadCellRenderer;
import com.ontimize.gui.table.SortTableCellRenderer;
import com.ontimize.gui.table.SumCellRenderer;
import com.ontimize.gui.table.SumRowSetupDialog;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.Table.QuickFieldText;
import com.ontimize.gui.table.TableButton;
import com.ontimize.gui.tree.BasicTreeCellRenderer;
import com.ontimize.gui.tree.Tree;
import com.ontimize.plaf.OntimizeDefaults.LazyPainter;
import com.ontimize.plaf.border.OLoweredBorder;
import com.ontimize.plaf.component.OntimizePopupFactory;
import com.ontimize.plaf.component.OntimizeSynthIcon;
import com.ontimize.plaf.painter.AbstractOButtonPainter;
import com.ontimize.plaf.painter.AbstractOTextFieldPainter;
import com.ontimize.plaf.painter.AbstractOToolBarPainter;
import com.ontimize.plaf.painter.AbstractRegionPainter;
import com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext;
import com.ontimize.plaf.painter.ButtonPainter;
import com.ontimize.plaf.painter.OCardPanelPainter;
import com.ontimize.plaf.painter.OCheckBoxMenuItemPainter;
import com.ontimize.plaf.painter.OCheckBoxPainter;
import com.ontimize.plaf.painter.OCollapsibleButtonPanelPainter;
import com.ontimize.plaf.painter.OColumnPanelPainter;
import com.ontimize.plaf.painter.OComboBoxArrowButtonPainter;
import com.ontimize.plaf.painter.OComboBoxPainter;
import com.ontimize.plaf.painter.OComboBoxTextFieldPainter;
import com.ontimize.plaf.painter.OEditorPanePainter;
import com.ontimize.plaf.painter.OFormBodyPanelPainter;
import com.ontimize.plaf.painter.OFormButtonPanelPainter;
import com.ontimize.plaf.painter.OFormTabbedPaneTabAreaPainter;
import com.ontimize.plaf.painter.OFormTabbedPaneTabPainter;
import com.ontimize.plaf.painter.OFormTitlePainter;
import com.ontimize.plaf.painter.OGridPanelPainter;
import com.ontimize.plaf.painter.OMenuBarMenuPainter;
import com.ontimize.plaf.painter.OMenuBarPainter;
import com.ontimize.plaf.painter.OMenuItemPainter;
import com.ontimize.plaf.painter.OMenuPainter;
import com.ontimize.plaf.painter.OPopupItemPainter;
import com.ontimize.plaf.painter.OPopupMenuPainter;
import com.ontimize.plaf.painter.OPopupMenuSeparatorPainter;
import com.ontimize.plaf.painter.ORadioButtonMenuItemPainter;
import com.ontimize.plaf.painter.ORadioButtonPainter;
import com.ontimize.plaf.painter.ORootPainter;
import com.ontimize.plaf.painter.ORowPanelPainter;
import com.ontimize.plaf.painter.OScrollBarButtonPainter;
import com.ontimize.plaf.painter.OScrollBarPainter;
import com.ontimize.plaf.painter.OScrollBarThumbPainter;
import com.ontimize.plaf.painter.OScrollBarTrackPainter;
import com.ontimize.plaf.painter.OScrollPanePainter;
import com.ontimize.plaf.painter.OSeparatorPainter;
import com.ontimize.plaf.painter.OSplitPaneDividerPainter;
import com.ontimize.plaf.painter.OTabbedPaneArrowButtonPainter;
import com.ontimize.plaf.painter.OTabbedPaneContentPainter;
import com.ontimize.plaf.painter.OTabbedPanePainter;
import com.ontimize.plaf.painter.OTabbedPaneTabAreaPainter;
import com.ontimize.plaf.painter.OTabbedPaneTabPainter;
import com.ontimize.plaf.painter.OTableButtonFooterPanelPainter;
import com.ontimize.plaf.painter.OTableButtonPanelPainter;
import com.ontimize.plaf.painter.OTableHeaderPainter;
import com.ontimize.plaf.painter.OTableHeaderRendererPainter;
import com.ontimize.plaf.painter.OTextAreaPainter;
import com.ontimize.plaf.painter.OToggleButtonPainter;
import com.ontimize.plaf.painter.OToolBarPainter;
import com.ontimize.plaf.painter.OToolBarSeparatorPainter;
import com.ontimize.plaf.painter.OToolBarToggleButtonPainter;
import com.ontimize.plaf.painter.OToolTipPainter;
import com.ontimize.plaf.painter.OTreeCellEditorPainter;
import com.ontimize.plaf.painter.OTreeCellRendererPainter;
import com.ontimize.plaf.painter.util.ShapeFactory;
import com.ontimize.plaf.state.OComboBoxArrowButtonEditableState;
import com.ontimize.plaf.state.OComboBoxEditableState;
import com.ontimize.plaf.state.OProgressBarFinishedState;
import com.ontimize.plaf.state.OProgressBarIndeterminateState;
import com.ontimize.plaf.state.ORootPaneNoFrameState;
import com.ontimize.plaf.state.ORootPaneWindowFocusedState;
import com.ontimize.plaf.state.OSliderThumbArrowShapeState;
import com.ontimize.plaf.state.OSplitPaneDividerVerticalState;
import com.ontimize.plaf.state.OSplitPaneVerticalState;
import com.ontimize.plaf.state.OTableHeaderTableHeaderRendererSortedState;
import com.ontimize.plaf.state.OTextAreaNotInScrollPaneState;
import com.ontimize.plaf.state.OToolBarEastState;
import com.ontimize.plaf.state.OToolBarNorthState;
import com.ontimize.plaf.state.OToolBarSouthState;
import com.ontimize.plaf.state.OToolBarWestState;
import com.ontimize.plaf.state.RequiredState;
import com.ontimize.plaf.utils.OAppContext;
import com.ontimize.plaf.utils.OntimizeLAFColorUtils;
import com.ontimize.plaf.utils.OntimizeLAFParseUtils;
import com.ontimize.plaf.utils.ReflectionUtils;
import com.ontimize.plaf.utils.StyleUtil;
import com.ontimize.util.swing.ButtonSelection;
import com.ontimize.util.swing.CollapsibleButtonPanel;

/**
 * Ontimize Look And Feel development.
 *
 * @author Imatia Innovation
 */
@SuppressWarnings("serial")
public class OntimizeLookAndFeel extends javax.swing.plaf.nimbus.NimbusLookAndFeel {

    /**
     * Used in a handful of places where we need an empty Insets.
     */
    public static final Insets EMPTY_UIRESOURCE_INSETS = new InsetsUIResource(0, 0, 0, 0);

    public static final String UI_PACKAGE_PREFIX = "com.ontimize.plaf.ui.O";

    protected boolean decorated = false;

    protected boolean initialized = false;

    /**
     * The number of pixels that compounds the border width of the component.
     */
    public static int defaultNumBorders = 4;

    /**
     * The radius of component corners.
     */
    public static double defaultRadius = Double.valueOf(7);// Double.MAX_VALUE;

    /**
     * The default font that will be used. I store this value so that it can be set in the UIDefaults
     * when requested.
     */
    protected Font defaultFont;

    public static final String[] NIMBUS_COLORS_KEYS = new String[] { "nimbusSelectionBackground", "text",
            "nimbusSelectedText", "nimbusDisabledText", "nimbusLightBackground",
            "control", "info", "nimbusInfoBlue", "nimbusAlertYellow", "nimbusBase", "nimbusFocus", "nimbusGreen",
            "nimbusRed", "nimbusOrange", "activeCaption", "background",
            "controlDkShadow", "controlHighlight", "controlLHighlight", "controlShadow", "controlText", "desktop",
            "inactiveCaption", "infoText", "menu", "menuText",
            "nimbusBlueGrey", "nimbusBorder", "nimbusSelection", "scrollbar", "textBackground", "textForeground",
            "textHighlight", "textHighlightText", "textInactiveText" };

    /**
     * Constructor method. Here it is indicated: - initialize the map of styles (for Ontimize components
     * that must be configured regardless of other JAVA components. For instance, to allow users to
     * configurate the Rows and Columns in other way that the other Panels in the look and feel, but
     * using the same keys)
     */
    public OntimizeLookAndFeel() {
        super();
        this.defaultFont = this.getDefaultFont();

        /*
         * Register all of the regions and their states that this class will use for later lookup.
         * Additional regions can be registered later by 3rd party components. These are simply the default
         * registrations.
         */
        this.registerStyles();

    }

    public OntimizeLookAndFeel(final Font defaultFont) {
        super();

        FontUIResource fUI = null;
        if (defaultFont instanceof FontUIResource) {
            fUI = (FontUIResource) defaultFont;
        } else {
            fUI = new FontUIResource(defaultFont);
        }
        this.defaultFont = fUI;

        /*
         * Register all of the regions and their states that this class will use for later lookup.
         * Additional regions can be registered later by 3rd party components. These are simply the default
         * registrations.
         */
        this.registerStyles();

    }

    @Override
    public String getName() {
        return "Ontimize L&F";
    }

    @Override
    public String getID() {
        return "Ontimize L&F";
    }

    @Override
    public String getDescription() {
        return "Ontimize Look and Feel";
    }

    @Override
    public boolean getSupportsWindowDecorations() {
        return this.decorated;
    }

    public static Object lookup(final String s) {
        if (UIManager.getDefaults() != null) {
            return UIManager.getDefaults().get(s);
        } else {
            return UIManager.get(s);
        }
    }

    public static boolean isWindowOpacityEnabled(final Window window) {
        // boolean flag = !getBoolean("Synthetica.window.opaque", window, true);
        final boolean flag = false;
        // return !isJava6u10OrAbove() && OS.getCurrentOS() != OS.Mac || !flag;
        return !OntimizeLookAndFeel.isJava6u10OrAbove() && !flag;
    }

    public static boolean isWindowShapeEnabled(final Window window) {
        // String s = getString("Synthetica.window.shape", window);
        // return (isJava6u10OrAbove() || OS.getCurrentOS() == OS.Mac) &&
        // "ROUND_RECT".equals(s);
        return OntimizeLookAndFeel.isJava6u10OrAbove();
    }

    public static void updateWindowShape(final Window window) {
    }

    protected static boolean isJava6u10OrAbove() {
        final String s = System.getProperty("java.version");
        if (s.startsWith("1.5.")) {
            return false;
        }
        if (s.startsWith("1.6.0_09")) {
            return false;
        }
        if (s.startsWith("1.6.0_08")) {
            return false;
        }
        if (s.startsWith("1.6.0_07")) {
            return false;
        }
        if (s.startsWith("1.6.0_06")) {
            return false;
        }
        if (s.startsWith("1.6.0_05")) {
            return false;
        }
        if (s.startsWith("1.6.0_04")) {
            return false;
        }
        if (s.startsWith("1.6.0_03")) {
            return false;
        }
        if (s.startsWith("1.6.0_02")) {
            return false;
        }
        if (s.startsWith("1.6.0_01")) {
            return false;
        }
        return !s.equals("1.6.0");
    }

    public static SynthContext createContext(final JComponent component, final Region region, final int state) {
        final SynthStyle synthstyle = NimbusLookAndFeel.getStyle(component, region);
        return new SynthContext(component, region, synthstyle, state);
    }

    protected Icon frameCloseIcon = null;

    protected Icon frameIconifyIcon = null;

    protected Icon frameMaximizeIcon = null;

    protected Icon createFrameCloseIcon() {
        if (this.frameCloseIcon == null) {
            final URL url = this.getClass().getClassLoader().getResource("com/ontimize/plaf/images/closeIcon.png");
            this.frameCloseIcon = new ImageIcon(url);
        }
        return this.frameCloseIcon;
    }

    protected Icon createFrameIconifyIcon() {
        if (this.frameIconifyIcon == null) {
            final URL url = this.getClass().getClassLoader().getResource("com/ontimize/plaf/images/iconifyIcon.png");
            this.frameIconifyIcon = new ImageIcon(url);
        }
        return this.frameIconifyIcon;
    }

    protected Icon createFrameMaximizeIcon() {
        if (this.frameMaximizeIcon == null) {
            final URL url = this.getClass().getClassLoader().getResource("com/ontimize/plaf/images/maximizeIcon.png");
            this.frameMaximizeIcon = new ImageIcon(url);
        }
        return this.frameMaximizeIcon;
    }

    // public static Icon createFrameMinimizeIcon() {
    // if (frame_minIcon == null) {
    // frame_minIcon = new FrameButtonIcon(Part.WP_RESTOREBUTTON);
    // }
    // return frame_minIcon;
    // }

    /**
     * Convenience method for setting a component's foreground and background color properties with
     * values from the defaults. The properties are only set if the current value is either {@code null}
     * or a {@code UIResource}.
     * @param c component to set the colors on
     * @param defaultBgName key for the background
     * @param defaultFgName key for the foreground
     *
     * @see #installColorsAndFont
     * @see UIManager#getColor
     * @throws NullPointerException as described in <a href="#exceptions">exceptions</a>
     */
    public static void installColors(final JComponent c, final String defaultBgName, final String defaultFgName) {
        if (UIManager.getColor(defaultBgName) != null) { // bg == null || bg
            // instanceof
            // UIResource
            c.setBackground(UIManager.getColor(defaultBgName));
        }

        if (UIManager.getColor(defaultFgName) != null) {// fg == null || fg
            // instanceof UIResource
            c.setForeground(UIManager.getColor(defaultFgName));
        }
    }

    /**
     * Convenience method for setting a component's foreground, background and font properties with
     * values from the defaults. The properties are only set if the current value is either {@code null}
     * or a {@code UIResource}.
     * @param c component set to the colors and font on
     * @param defaultBgName key for the background
     * @param defaultFgName key for the foreground
     * @param defaultFontName key for the font
     * @throws NullPointerException as described in <a href="#exceptions">exceptions</a>
     *
     * @see #installColors
     * @see UIManager#getColor
     * @see UIManager#getFont
     */
    public static void installColorsAndFont(final JComponent c, final String defaultBgName, final String defaultFgName,
            final String defaultFontName) {
        if (UIManager.getFont(defaultFontName) != null) { // original -> f ==
            // null || f
            // instanceof
            // UIResource
            c.setFont(UIManager.getFont(defaultFontName));
        }
        OntimizeLookAndFeel.installColors(c, defaultBgName, defaultFgName);
    }

    public static Font defaultAppFont = null;

    public static Font getDefaultAppFont(){
        return defaultAppFont;
    }

    public static void setDefaultAppFont(final Font font){
        defaultAppFont = font;
    }

    protected Font getDefaultFont() {

        if (this.defaultFont == null) {
            this.defaultFont = StyleUtil.getFont("Application", "font", null);

            if ((OntimizeLookAndFeel.defaultAppFont instanceof Font) && (this.defaultFont == null)) {
                this.defaultFont = OntimizeLookAndFeel.defaultAppFont;
            }

            if (this.defaultFont == null) {
                try {
                    // defaultFont = FontManager.getFontConfigFUIR("Arial",
                    // Font.PLAIN, 11);
                    this.defaultFont = new FontUIResource(new Font("Arial", Font.PLAIN, 11));
                } catch (final Throwable e) {
                }
            }

            if (this.defaultFont == null) {
                this.defaultFont = new Font("Arial", Font.PLAIN, 11);
            }
        }

        return this.defaultFont;
    }

    public void reinstallDefaultFont(final Font font) {
        if (font == null) {
            return;
        }
        FontUIResource fUI = null;
        if (font instanceof FontUIResource) {
            fUI = (FontUIResource) font;
        } else {
            fUI = new FontUIResource(font);
        }
        this.defaultFont = fUI;
    }

    protected void defineDefaultFont(final UIDefaults d) {
        if (d != null) {
            d.put("defaultFont", this.getDefaultFont());
        }
    }

    protected LazyPainter createLazyPainter(final String className, final int which) {
        return new LazyPainter(className, which);
    }

    protected LazyPainter createLazyPainter(final String className, final int which, final String path) {
        return new LazyPainter(className, which, path);
    }

    protected LazyPainter createLazyPainter(final String className, final int which, final PaintContext ctx) {
        return new LazyPainter(className, which, ctx);
    }

    protected LazyPainter createLazyPainter(final String className, final int which, final PaintContext ctx, final String path) {
        return new LazyPainter(className, which, ctx, path);
    }

    protected void defineTextPane(final UIDefaults d) {
        // Initialize TextPane
        final String compName = "TextPane";
        this.defineTextPane(compName, d);
    }

    protected void defineTextPane(String compName, final UIDefaults d) {
        // Initialize TextPane
        if (compName == null) {
            compName = "TextPane";
        }

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 6 4 6");

        OntimizeLookAndFeel.setColor(d, compName, "selectionForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColor(d, compName, "selectionBackground", "#39698a");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#243b4aCC");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");

        d.put(compName + "[Enabled].background", StyleUtil.getColorUI(compName, "[Enabled].background", "#FFFFFF"));
        d.put(compName + "[Disabled].background", StyleUtil.getColorUI(compName, "[Disabled].background", "#FFFFFF7D"));
        d.put(compName + "[Selected].background", StyleUtil.getColorUI(compName, "[Selected].background", "#FFFFFF"));

    }

    protected void defineTextFields(final UIDefaults d) {
        final String compName = "TextField";
        this.defineTextFields(compName, d);
    }

    protected void defineTextFields(String compName, final UIDefaults d) {
        // TextField:
        if (compName == null) {
            compName = "TextField";
        }

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 14 4 14");
        OntimizeLookAndFeel.setDouble(d, "Application", "radius",
                ((Double) OntimizeLookAndFeel.defaultRadius).toString());
        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");

        // TextForeground:
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#616d75");// #8F9CA4
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground", "#d4d5d7");
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Required+Selected].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);
        DataField.requiredFieldForegroundColor = requiredFgColor;

        // Background:
        final ColorUIResource enabledColor = StyleUtil.getColorUI(compName, "[Enabled].background", "#FFFFFF");
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        ColorUIResource disabledRequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRequiredColor = disabledRequiredColor != null ? disabledRequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        ColorUIResource focusedRequiredColor = StyleUtil.getColorUI(compName, "[Focused+Required].background", null);
        focusedRequiredColor = focusedRequiredColor != null ? focusedRequiredColor : requiredColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        d.put(compName + "[Enabled].background", enabledColor);
        d.put(compName + "[Disabled].background", disabledColor);
        d.put(compName + "[Focused].background", focusedColor);
        d.put(compName + "[Required].background", requiredColor);
        d.put(compName + "[Focused+Required].background", focusedRequiredColor);
        d.put(compName + "[Disabled+Required].background", disabledRequiredColor);
        DataField.BACKGROUNDCOLOR = enabledColor;
        DataField.defaultDisableBackgroundColor = disabledColor;
        DataField.FOCUS_BACKGROUNDCOLOR = focusedColor;
        DataField.requiredFieldBackgroundColor = requiredColor;

        // Border:
        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        // Editor inner border...
        d.put(compName + ".editorInnerBorder",
                StyleUtil.getArrayColorUI(compName, "editorInnerBorder", "#BAf1FE #F3FdFF"));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OTextFieldPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "4 14 4 14"), new Dimension(122, 26),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED_REQUIRED, ctx));
    }

    protected void defineTextArea(final UIDefaults d) {
        final String compName = "TextArea";
        this.defineTextArea(compName, d);
    }

    protected void defineTextArea(String compName, final UIDefaults d) {
        // Initialize TextArea
        if (compName == null) {
            compName = "TextArea";
        }

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "6 6 6 6");
        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");

        // TextForeground
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#616d75");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground", "#d4d5d7");
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);

        // Background
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        ColorUIResource disabledRequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRequiredColor = disabledRequiredColor != null ? disabledRequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        ColorUIResource focusedRequiredColor = StyleUtil.getColorUI(compName, "[Focused+Required].background", null);
        focusedRequiredColor = focusedRequiredColor != null ? focusedRequiredColor : requiredColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        d.put(compName + "[Disabled].background", disabledColor);
        d.put(compName + "[Focused].background", focusedColor);
        d.put(compName + "[Selected].background", StyleUtil.getColorUI(compName, "[Selected].background", "#FFFFFF"));
        d.put(compName + "[Required].background", requiredColor);
        d.put(compName + "[Focused+Required].background", focusedRequiredColor);
        d.put(compName + "[Disabled+Required].background", disabledRequiredColor);

        // Border
        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        // TextArea in scroll pane
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OTextAreaPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "6 6 6 6"), new Dimension(122, 24),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,Required,NotInScrollPane");
        d.put(compName + ".NotInScrollPane", new OTextAreaNotInScrollPaneState());
        d.put(compName + ".Required", new RequiredState());
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Focused+Required].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_FOCUSED_REQUIRED, ctx));

        // TextArea not in scroll pane
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Disabled+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_DISABLED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Disabled+Required+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_DISABLED_REQUIRED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Enabled+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_ENABLED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Focused+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_FOCUSED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Required+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_REQUIRED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Focused+Required+NotInScrollPane].backgroundPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BACKGROUND_FOCUSED_REQUIRED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Disabled+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_DISABLED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Disabled+Required+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_DISABLED_REQUIRED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Focused+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_FOCUSED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Enabled+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_ENABLED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Required+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_REQUIRED_NOTINSCROLLPANE, ctx));
        d.put(compName + "[Focused+Required+NotInScrollPane].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_FOCUSED_REQUIRED_NOTINSCROLLPANE, ctx));
    }

    protected void definePassword(final UIDefaults d) {
        final String compName = "PasswordField";
        this.definePassword(compName, d);
    }

    protected void definePassword(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "PasswordField";
        }

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 14 4 14");
        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#8e8f91");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground",
                null);
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        d.put(compName + "[Disabled].background", disabledColor);

        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        d.put(compName + "[Focused].background", focusedColor);

        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        d.put(compName + "[Required].background", requiredColor);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Required].background", "#89A5B9");

        ColorUIResource disabledRrequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRrequiredColor = disabledRrequiredColor != null ? disabledRrequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        d.put(compName + "[Disabled+Required].background", disabledRrequiredColor);

        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        // Editor inner border...
        d.put(compName + ".editorInnerBorder",
                StyleUtil.getArrayColorUI(compName, "editorInnerBorder", "#BAf1FE #F3FdFF"));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OTextFieldPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "4 14 4 14"), new Dimension(122, 26),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED_REQUIRED, ctx));
    }

    protected void defineReferenceExtComponent(final UIDefaults d) {
        final String compName = "TextField:\"TextField.ReferenceExt\"";
        this.defineReferenceExtComponent(compName, d);
    }

    protected void defineReferenceExtComponent(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "TextField:\"TextField.ReferenceExt\"";
        }

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 14 4 14");

        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");
        // TextForeground
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#8e8f91");
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground",
                null);
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);

        // Background
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        ColorUIResource disabledRequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRequiredColor = disabledRequiredColor != null ? disabledRequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        ColorUIResource focusedRequiredColor = StyleUtil.getColorUI(compName, "[Focused+Required].background", null);
        focusedRequiredColor = focusedRequiredColor != null ? focusedRequiredColor : requiredColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        d.put(compName + "[Disabled].background", disabledColor);
        d.put(compName + "[Focused].background", focusedColor);
        d.put(compName + "[Required].background", requiredColor);
        d.put(compName + "[Focused+Required].background", focusedRequiredColor);
        d.put(compName + "[Disabled+Required].background", disabledRequiredColor);

        // Border
        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OReferenceExtFieldPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "4 14 4 14"), new Dimension(122, 26),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED_REQUIRED, ctx));

    }

    protected void defineReferenceExtCodeComponent(final UIDefaults d) {
        final String compName = "TextField:\"TextField.ReferenceExtCode\"";
        this.defineReferenceExtCodeComponent(compName, d);
    }

    protected void defineReferenceExtCodeComponent(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "TextField:\"TextField.ReferenceExtCode\"";
        }

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 14 4 14");

        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");
        // TextForeground
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#8e8f91");
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground",
                null);
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);

        // Background
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        ColorUIResource disabledRequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRequiredColor = disabledRequiredColor != null ? disabledRequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        ColorUIResource focusedRequiredColor = StyleUtil.getColorUI(compName, "[Focused+Required].background", null);
        focusedRequiredColor = focusedRequiredColor != null ? focusedRequiredColor : requiredColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        d.put(compName + "[Disabled].background", disabledColor);
        d.put(compName + "[Focused].background", focusedColor);
        d.put(compName + "[Required].background", requiredColor);
        d.put(compName + "[Focused+Required].background", focusedRequiredColor);
        d.put(compName + "[Disabled+Required].background", disabledRequiredColor);

        // Border
        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OReferenceExtCodeFieldPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "4 14 4 14"), new Dimension(122, 26),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED_REQUIRED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED_REQUIRED, ctx));

    }

    protected void defineQuickFilter(final UIDefaults d) {
        final String compName = "Table:\"Table.QuickFilter\"";
        this.defineQuickFilter(compName, d);
    }

    protected void defineQuickFilter(String compName, final UIDefaults d) {

        // QuickFilter...
        if (compName == null) {
            compName = "Table:\"Table.QuickFilter\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OQuickFilterPainter");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "4 14 4 30");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#a0b5c3");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");

        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#FFFFFF7D");
        d.put(compName + "[Disabled].background", disabledColor);
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        d.put(compName + "[Focused].background", focusedColor);

        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Focused].border",
                StyleUtil.getArrayColorUI(compName, "[Focused].border", "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819"));
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        d.put(compName + ".icon", StyleUtil.getProperty(compName, "icon", "com/ontimize/plaf/images/queryfilter.png"));

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(4, 14, 4, 30),
                new Dimension(122, 26), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));

        QuickFieldText.paintFindText = false;
    }

    protected void defineEditorPane(final UIDefaults d) {
        final String compName = "EditorPane";
        this.defineEditorPane(compName, d);
    }

    protected void defineEditorPane(String compName, final UIDefaults d) {
        // Initialize EditorPane
        if (compName == null) {
            compName = "EditorPane";
        }
        d.put(compName + ".contentMargins", new InsetsUIResource(10, 10, 10, 10));
        d.put(compName + ".opaque", Boolean.TRUE);

        d.put(compName + "[Enabled].textForeground",
                StyleUtil.getColorUI(compName, "[Enabled].textForeground", "#335971"));
        d.put(compName + "[Disabled].textForeground",
                StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#335971"));
        d.put(compName + "[Focused].textForeground",
                StyleUtil.getColorUI(compName, "[Focused].textForeground", "#61BEE8"));
        d.put(compName + "[Selected].textForeground",
                StyleUtil.getColorUI(compName, "[Selected].textForeground", "#FFFFFF"));

        d.put(compName + "[Enabled].background", StyleUtil.getColorUI(compName, "[Enabled].background", "#FFFFFF"));
        d.put(compName + "[Disabled].background", StyleUtil.getColorUI(compName, "[Disabled].background", "#FFFFFF7D"));
        d.put(compName + "[Selected].background", StyleUtil.getColorUI(compName, "[Selected].background", "#FFFFFF"));

        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E5"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Focused].border",
                StyleUtil.getArrayColorUI(compName, "[Focused].border", "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819"));

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OEditorPanePainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(10, 10, 10, 10),
                new Dimension(122, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OEditorPanePainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OEditorPanePainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OEditorPanePainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Focused].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OTextAreaPainter.BORDER_ENABLED, ctx));

    }

    protected void defineFileChooser(final UIDefaults d) {
        final String compName = "FileChooser";
        this.defineFileChooser(compName, d);
    }

    protected void defineFileChooser(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "FileChooser";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "10 10 10 10");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "usesSingleFilePane", "true");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#366581");
        OntimizeLookAndFeel.setColor(d, compName, "background", "#366581");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFileChooserPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(10, 10, 10, 10),
                new Dimension(122, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OEditorPanePainter.BACKGROUND_ENABLED, ctx));

    }

    protected void defineOptionPane(final UIDefaults d) {
        final String compName = "OptionPane";
        this.defineOptionPane(compName, d);
    }

    protected void defineOptionPane(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "OptionPane";
        }

        // Initialize OptionPane
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#366581");
        // MessageForeground must be Color not ColorUIResource, with UI color is
        // not fixed.
        OntimizeLookAndFeel.setColor(d, compName, "messageForeground", "#ffffff");
        OntimizeLookAndFeel.setInteger(d, compName, "buttonMinimumWidth", "60");

    }

    protected void defineScrollPane(final UIDefaults d) {
        final String compName = "ScrollPane";
        this.defineScrollPane(compName, d);
    }

    protected void defineScrollPane(String compName, final UIDefaults d) {

        // Initialize ScrollPane
        if (compName == null) {
            compName = "ScrollPane";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OScrollPanePainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(4, 4, 4, 4),
                new Dimension(122, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + ".contentMargins", new InsetsUIResource(4, 4, 4, 4));
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "useChildTextComponentFocus", "true");

        d.put(compName + ".States", "Enabled,Disabled,Focused,Required");
        d.put(compName + ".Required", new RequiredState());

        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Enabled+Focused].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_ENABLED_FOCUSED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Required].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_REQUIRED, ctx));
        d.put(compName + "[Focused+Required].borderPainter",
                this.createLazyPainter(pClass, OScrollPanePainter.BORDER_FOCUSED_REQUIRED, ctx));

        // Store ScrollPane Corner Component
        d.put(compName + ".cornerPainter", this.createLazyPainter(pClass, OScrollPanePainter.CORNER_ENABLED, ctx));
    }

    protected void defineFormScrollPanel(final UIDefaults d) {
        final String compName = "\"FormScrollPane\"";
        this.defineScrollPane(d);

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
    }

    protected void defineTreeScrollPanel(final UIDefaults d) {
        final String compName = "\"TreeScrollPane\"";
        this.defineScrollPane(d);

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
    }

    protected void defineViewport(final UIDefaults d) {

        // // Initialize Viewport
        d.put("Viewport.contentMargins", new InsetsUIResource(0, 0, 0, 0));
        d.put("Viewport.opaque", Boolean.FALSE);
    }

    protected void defineScrollBar(final UIDefaults d) {
        final String compName = "ScrollBar";
        this.defineScrollBar(compName, d);
    }

    protected void defineScrollBar(String compName, final UIDefaults d) {
        // ScrollBar:
        if (compName == null) {
            compName = "ScrollBar";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "6 6 6 6");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setInteger(d, compName, "decrementButtonGap", "0");// 0
        OntimizeLookAndFeel.setInteger(d, compName, "incrementButtonGap", "0");// 0
        OntimizeLookAndFeel.setInteger(d, compName, "thumbHeight", "12");
        d.put(compName + ".minimumThumbSize", new DimensionUIResource(29, 29));
        d.put(compName + ".maximumThumbSize", new DimensionUIResource(1000, 1000));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].border", "#FFFFFF");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OScrollBarPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "6 6 6 6"), new Dimension(122, 24),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + ".States", "Enabled,Disabled");
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OScrollBarPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, OScrollBarPainter.BORDER_DISABLED, ctx));

    }

    protected void defineScrollBarButton(final UIDefaults d) {
        final String compName = "ScrollBar:\"ScrollBar.button\"";
        this.defineScrollBarButton(compName, d);
    }

    protected void defineScrollBarButton(String compName, final UIDefaults d) {
        // ScrollBar button:
        if (compName == null) {
            compName = "ScrollBar:\"ScrollBar.button\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OScrollBarButtonPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setInteger(d, compName, "size", "25");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#BDBDBD");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#D0DAE2");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#BDBDBD");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].background", "#9D9D9D");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].arrowBackground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].arrowBackground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].arrowBackground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].arrowBackground", "#FFFFFF");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(25, 16),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + "[Enabled].foregroundPainter",
                this.createLazyPainter(pClass, OScrollBarButtonPainter.FOREGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].foregroundPainter",
                this.createLazyPainter(pClass, OScrollBarButtonPainter.FOREGROUND_DISABLED, ctx));
        d.put(compName + "[MouseOver].foregroundPainter",
                this.createLazyPainter(pClass, OScrollBarButtonPainter.FOREGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Pressed].foregroundPainter",
                this.createLazyPainter(pClass, OScrollBarButtonPainter.FOREGROUND_PRESSED, ctx));
    }

    protected void defineScrollBarThumb(final UIDefaults d) {
        final String compName = "ScrollBar:ScrollBarThumb";
        this.defineScrollBarThumb(compName, d);
    }

    protected void defineScrollBarThumb(String compName, final UIDefaults d) {
        // ScrollBar thumb :
        if (compName == null) {
            compName = "ScrollBar:ScrollBarThumb";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OScrollBarThumbPainter");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 2 2 2");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].background", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].border", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].border", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].backgroundShadow", "#FFFFFF25");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].backgroundShadow", "#FFFFFF25");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].backgroundShadow", "#FFFFFF25");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "2 2 2 2"), new Dimension(38, 15),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, 2.0);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OScrollBarThumbPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OScrollBarThumbPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OScrollBarThumbPainter.BACKGROUND_PRESSED, ctx));
    }

    protected void defineScrollBarTrack(final UIDefaults d) {
        final String compName = "ScrollBar:ScrollBarTrack";
        this.defineScrollBarTrack(compName, d);
    }

    protected void defineScrollBarTrack(String compName, final UIDefaults d) {
        // ScrollBar track :
        if (compName == null) {
            compName = "ScrollBar:ScrollBarTrack";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OScrollBarTrackPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 5 5 5");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#E6E6E6");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#EEF1F4");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5"), new Dimension(18, 15),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, 2.0);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OScrollBarTrackPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OScrollBarTrackPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTable(final UIDefaults d) {
        final String compName = "Table";
        this.defineTable(compName, d);
    }

    protected void defineTable(String compName, final UIDefaults d) {
        // Table:
        if (compName == null) {
            compName = "Table";
        }

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.defaultFont));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 2 2 2");
        OntimizeLookAndFeel.setInteger(d, compName, "rowHeight", "22");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "showGrid", "true");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "gridColor", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#3A5F77");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#3A5F77");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "TextBackground", "#9cb2c1");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#c3d5dd");
        OntimizeLookAndFeel.setColor(d, compName, "alternateRowColor", "#Dae7Ed");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "alternateOddRowColor", "#B6CAD6");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "alternateEvenRowColor", "#A6BDCB");
        OntimizeLookAndFeel.setBoolean(d, compName, "useOddEvenAlternateRowColor", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "rendererUseTableColors", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "rendererUseUIBorder", "true");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "selectionBackground", "#F2F8F9");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "selectionForeground", "#2E8ECB");

        d.put(compName + ".cellNoFocusBorder", new BorderUIResource(BorderFactory.createEmptyBorder(2, 5, 2, 5)));
        Color borderColor = StyleUtil.getColor(compName, "[Selected].border", "#2E8ECB");
        final Border outsideBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor);
        final Border insideBorder = BorderFactory.createEmptyBorder(0, 5, 0, 5);
        d.put(compName + ".focusCellHighlightBorder",
                new BorderUIResource(BorderFactory.createCompoundBorder(outsideBorder, insideBorder)));

        // Color used for default TableCellRenderes of swing
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Selected].textForeground", "#2E8ECB");
        // Color used for default TableCellRenderes of swing
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Selected].textBackground", "#F2F8F9");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Selected].textBackground", "#F2F8F9");

        // ************Ontimize Table Configuration***********
        Table.defaultTableOpaque = Boolean.TRUE;
        Table.defaultTableBackgroundColor = StyleUtil.getColor("Table", "background", "#9CB2C1");
        Table.MIN_ROW_HEIGHT = StyleUtil.getInteger(compName, "minRowHeight", "22");
        borderColor = StyleUtil.getColorUI("Table", "border", "#ADC0CE");
        final BorderUIResource tBorder = new BorderUIResource(BorderFactory.createLineBorder(borderColor, 2));
        BorderManager.putBorder(BorderManager.DEFAULT_TABLE_BORDER_KEY, tBorder);
        d.put(compName + ".scrollPaneBorder", tBorder);

        compName = "\"PageFetcher.Label\"";
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8e8f91");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");

        OntimizeLookAndFeel.setColor(d, compName, "foreground", "#335971");
        OntimizeLookAndFeel.setColor(d, compName, "disabled", "#243b4aCC");
        OntimizeLookAndFeel.setColor(d, compName, "disabledText", "#243b4aCC");

        // Buttons configuration...
        ImageManager.TABLE_SUMROWSETUP = "com/ontimize/plaf/images/table/calc.png";
        ImageManager.TABLE_CALCULATEDCOLS = "com/ontimize/plaf/images/table/calculate.png";
        ImageManager.TABLE_CHART = "com/ontimize/plaf/images/table/chart.png";
        ImageManager.TABLE_DEFAULT_CHARTS = "com/ontimize/plaf/images/table/chart.png";
        ImageManager.TABLE_CONF_VISIBLE_COLS = "com/ontimize/plaf/images/table/confvisiblecols.png";
        ImageManager.TABLE_CONF_VISIBLE_COLS_RED = "com/ontimize/plaf/images/table/confvisiblecolsred.png";
        ImageManager.TABLE_COPY = "com/ontimize/plaf/images/table/copy.png";
        ImageManager.TABLE_EXCEL = "com/ontimize/plaf/images/table/excel.png";
        ImageManager.TABLE_INSERT = "com/ontimize/plaf/images/table/insert.png";
        ImageManager.TABLE_HTML = "com/ontimize/plaf/images/table/internet.png";
        ImageManager.TABLE_REPORTS = "com/ontimize/plaf/images/table/page.png";
        ImageManager.TABLE_PIVOT = "com/ontimize/plaf/images/table/pivot.png";
        ImageManager.TABLE_PRINT = "com/ontimize/plaf/images/table/print.png";
        ImageManager.TABLE_REMOVE = "com/ontimize/plaf/images/table/recycler.png";
        ImageManager.TABLE_REFRESH = "com/ontimize/plaf/images/table/refresh.png";
        ImageManager.TABLE_SAVE_TABLE_FILTER = "com/ontimize/plaf/images/table/savetablefilter.png";
        ImageManager.TABLE_GROUP = "com/ontimize/plaf/images/table/groupicons.png";

    }

    protected void defineTableCellRenderer(final UIDefaults d) {
        final String compName = "Table:\"Table.cellRenderer\"";
        this.defineTableCellRenderer(compName, d);
    }

    protected void defineTableCellRenderer(String compName, final UIDefaults d) {
        // Table:"Table.cellRenderer
        if (compName == null) {
            compName = "Table:\"Table.cellRenderer\"";
        }

        // ************Ontimize Table Configuration***********
        CellRenderer.font = StyleUtil.getFont(compName, "font", OntimizeLAFParseUtils.fontToString(this.defaultFont));
        CellRenderer.fontColor = StyleUtil.getColor(compName, "foreground", "#3A5F77");
        CellRenderer.evenRowBackgroundColor = StyleUtil.getColor(compName, "evenRowBackground", "#A6BDCB");
        CellRenderer.oddRowBackgroundColor = StyleUtil.getColor(compName, "oddRowBackground", "#B6CAD6");
        CellRenderer.emptyBorder = BorderFactory.createEmptyBorder(0, 6, 0, 6);

        CellRenderer.editableFontColor = StyleUtil.getColor(compName, "[Editable].foreground", "#3A5F77");
        CellRenderer.evenEditableBackgroundColor = StyleUtil.getColor(compName, "[Editable].evenEditableBackground",
                "#c3d5dd");
        CellRenderer.oddEditableBackgroundColor = StyleUtil.getColor(compName, "[Editable].oddEditableBackground",
                "#dae7ed");

        CellRenderer.requiredInsertColumns = StyleUtil.getColor(compName, "[Insertable].requiredBackground", "#b8bacb");
        CellRenderer.noRequiredInsertColumns = StyleUtil.getColor(compName, "[Insertable].norequiredBackground",
                "#cdced9");

        CellRenderer.selectedFontColor = StyleUtil.getColor(compName, "[Selected].foreground", "#2e8ecb");
        CellRenderer.selectedEditableBackgroundColor = StyleUtil.getColor(compName, "[Selected+Editable].background",
                "#ffffff");
        CellRenderer.selectedBackgroundColor = StyleUtil.getColor(compName, "[Selected].background", "#ffffff");
        final Color borderColor = StyleUtil.getColor(compName, "[Selected].border", "#2E8ECB");
        final Border outsideBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor);
        final Border insideBorder = BorderFactory.createEmptyBorder(0, 6, 0, 6);
        CellRenderer.focusBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
        CellRenderer.focusBorderColor = borderColor;

    }

    protected void defineTableRowHeadCellRenderer(final UIDefaults d) {
        final String compName = "Table:\"Table.rowHeadCellRenderer\"";
        this.defineTableRowHeadCellRenderer(compName, d);
    }

    protected void defineTableRowHeadCellRenderer(String compName, final UIDefaults d) {
        // Row number column...
        if (compName == null) {
            compName = "Table:\"Table.rowHeadCellRenderer\"";
        }
        RowHeadCellRenderer.rowNumberForeground = StyleUtil.getColor(compName, "foreground", "#dae7ed");
        Font rNFont = this.getDefaultFont();
        rNFont = rNFont.deriveFont((float) rNFont.getSize() + 3);
        RowHeadCellRenderer.rowNumberFont = StyleUtil.getFont(compName, "font",
                OntimizeLAFParseUtils.fontToString(rNFont));
        RowHeadCellRenderer.rowNumberBackground = StyleUtil.getColor(compName, "[Enabled].background", "#6387a0");
        RowHeadCellRenderer.rowNumberBorder = BorderFactory.createEmptyBorder();
    }

    protected void defineTableSumCellRenderer(final UIDefaults d) {
        final String compName = "Table:\"Table.sumCellRenderer\"";
        this.defineTableSumCellRenderer(compName, d);
    }

    protected void defineTableSumCellRenderer(String compName, final UIDefaults d) {
        // SumCell Renderer
        if (compName == null) {
            compName = "Table:\"Table.sumCellRenderer\"";
        }
        SumCellRenderer.backgroundColor = StyleUtil.getColor(compName, "[Enabled].background", "#80B721");
        SumCellRenderer.foregroundColor = StyleUtil.getColor(compName, "foreground", "#FFFFFF");
        SumCellRenderer.disabledBackgroundColor = StyleUtil.getColor(compName, "[Disabled].background", "#9CB2C1");
    }

    protected void defineTableVisualCalendarCellRenderer(final UIDefaults d) {
        final String compName = "Table:\"VisualCalendar:Table.cellRenderer\"";
        this.defineTableVisualCalendarCellRenderer(compName, d);
    }

    protected void defineTableVisualCalendarCellRenderer(String compName, final UIDefaults d) {
        // VisualCalendar Component...
        if (compName == null) {
            compName = "Table:\"VisualCalendar:Table.cellRenderer\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableVisualCalendarCellRendererPainter");

        d.put(compName + ".States", "Enabled");
        d.put(compName + ".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        d.put(compName + ".opaque", Boolean.TRUE);
        final Color fgColor = StyleUtil.getColor(compName, "foreground", "#6b7e75");
        d.put(compName + "[Enabled].textForeground", fgColor);
        d.put(compName + ".foreground", fgColor);
        d.put(compName + ".TextForeground", fgColor);

        VisualCalendarComponent.DayRenderer.dayOfWeekFgColor = StyleUtil.getColor(compName, "dayOfWeekFgColor",
                "#6b7e75");
        VisualCalendarComponent.DayRenderer.dayOfWeekEndFgColor = StyleUtil.getColor(compName, "dayOfWeekEndFgColor",
                "#a8b7c0");
        VisualCalendarComponent.DayRenderer.daySelectedFgColor = StyleUtil.getColor(compName, "daySelectedFgColor",
                "#ffffff");

        OntimizeLookAndFeel.setColor(d, compName, "[Selected].background", "#5cb1ea");
        OntimizeLookAndFeel.setColor(d, compName, "[Selected].dayColor", "#bcd3e3");
        OntimizeLookAndFeel.setColor(d, compName, "evenColumnBackground", "#ffffff");
        OntimizeLookAndFeel.setColor(d, compName, "oddColumnBackground", "#F3F3F0");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(5, 5, 5, 5),
                new Dimension(22, 20), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTableCellEditor(final UIDefaults d) {
        final String compName = "\"Table.editor\"";
        this.defineTableCellEditor(compName, d);
    }

    protected void defineTableCellEditor(String compName, final UIDefaults d) {

        if (compName == null) {
            compName = "\"Table.editor\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableCellEditorPainter");

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 6 0 6");
        d.put(compName + ".opaque", Boolean.TRUE);

        // Background of the selected text
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textBackground", "#39698a");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#243b4aCC");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Enabled].background", "#FFFFFF");

        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Focused+Enabled].border",
                StyleUtil.getArrayColorUI(compName, "[Focused].border", "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819"));
        d.put(compName + "[Focused].innerShadow", StyleUtil.getColor(compName, "[Focused].innerShadow", "#CACACA"));

        final PaintContext ctx = new PaintContext(new Insets(1, 1, 1, 1), new Dimension(31, 17), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);

        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused+Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Focused+Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_FOCUSED, ctx));
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, AbstractOTextFieldPainter.BORDER_DISABLED, ctx));

        // ************Ontimize Table Configuration***********
        compName = "Table:\"Table.cellEditor\"";
        final Color borderColor = StyleUtil.getColor(compName, ".border", "#4cb5d7");
        final Border insideBorder = BorderFactory.createEmptyBorder(0, 6, 0, 6);
        final Border outsideBorder = BorderFactory.createLineBorder(borderColor);
        CellEditor.focusBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);
        CellEditor.fontColor = StyleUtil.getColor(compName, "foreground", "#31c7fc");
        CellEditor.backgroundColor = StyleUtil.getColor(compName, "background", "#FFFFFF");
        CellEditor.font = StyleUtil.getFont(compName, "font", OntimizeLAFParseUtils.fontToString(this.defaultFont));

        d.put(compName + ".cellEditorBorder", new TableCellEditorBorder(CellEditor.focusBorder));

    }

    public class TableCellEditorBorder extends BorderUIResource {

        protected Border delegate_;

        public TableCellEditorBorder(final Border delegate) {
            super(delegate);
            this.delegate_ = delegate;
        }

        public Border getDelegateBorder() {
            return this.delegate_;
        }

    }

    protected void defineTableHeader(final UIDefaults d) {
        final String compName = "TableHeader";
        this.defineTableHeader(compName, d);
    }

    protected void defineTableHeader(String compName, final UIDefaults d) {
        // TableHeader:
        if (compName == null) {
            compName = "TableHeader";
        }

        Font tHFont = this.getDefaultFont();
        tHFont = tHFont.deriveFont((float) tHFont.getSize() + 1);
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", OntimizeLAFParseUtils.fontToString(tHFont));
        /*
         * NOTE: //Insets fixed, they can't be changed by the user. If user wants to change insets, he could
         * change them into table header renderer.
         */
        d.put(compName + ".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "rightAlignSortArrow", "false");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#647b8b");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#e5edf0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);

        SortTableCellRenderer.defaultForegroundFilterColor = StyleUtil.getColor(compName, "foregroundFilter",
                "#2E8ECb");
        SortTableCellRenderer.paintSortIcon = true;

        d.put("Table.ascendingSortIcon", new OntimizeSynthIcon("TableHeader", "ascendingSortIconPainter", 31, 17));
        d.put("Table.descendingSortIcon", new OntimizeSynthIcon("TableHeader", "descendingSortIconPainter", 31, 17));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "ascendingSortIconBackground", "#80b721");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "descendingSortIconBackground", "#e64718");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableHeaderPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(0, 0, 0, 0),
                new Dimension(31, 17), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + "[Enabled].ascendingSortIconPainter",
                this.createLazyPainter(pClass, OTableHeaderPainter.ASCENDINGSORTICON_ENABLED, ctx));
        d.put(compName + "[Enabled].descendingSortIconPainter",
                this.createLazyPainter(pClass, OTableHeaderPainter.DESCENDINGSORTICON_ENABLED, ctx));

    }

    protected void defineTableHeaderRenderer(final UIDefaults d) {
        final String compName = "TableHeader:\"TableHeader.renderer\"";
        this.defineTableHeaderRenderer(compName, d);
    }

    protected void defineTableHeaderRenderer(String compName, final UIDefaults d) {

        // TableHeader: renderer :
        if (compName == null) {
            compName = "TableHeader:\"TableHeader.renderer\"";
        }

        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Sorted");
        d.put(compName + ".Sorted", new OTableHeaderTableHeaderRendererSortedState());

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 2 2 2");
        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "2 2 2 2");
        // 4 is the default VerticalHeaderMargin in Ontimize
        SortTableCellRenderer.defaultVerticalHeaderMargin = 4 + insets.top + insets.bottom;

        Font tHRFont = this.getDefaultFont();
        tHRFont = tHRFont.deriveFont((float) tHRFont.getSize() + 1);
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", OntimizeLAFParseUtils.fontToString(tHRFont));
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");

        d.put(compName + ".backgroundDegraded",
                StyleUtil.getArrayColor(compName, "backgroundDegraded", "#aab7bf #e5edf0"));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#647b8b");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "outsideRightBorder", "#858d92");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "insideRightBorder", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "bottomBorder", "#858d92");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableHeaderRendererPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(22, 20), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled+Focused].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Enabled+Sorted].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED_SORTED, ctx));
        d.put(compName + "[Enabled+Focused+Sorted].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED_FOCUSED_SORTED, ctx));
        d.put(compName + "[Disabled+Sorted].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_DISABLED_SORTED, ctx));

    }

    protected void defineVisualCalendarTableHeaderRenderer(final UIDefaults d) {
        final String compName = "TableHeader:\"VisualCalendar:TableHeader.renderer\"";
        this.defineVisualCalendarTableHeaderRenderer(compName, d);
    }

    protected void defineVisualCalendarTableHeaderRenderer(String compName, final UIDefaults d) {

        // VisualCalendar Component...
        if (compName == null) {
            compName = "TableHeader:\"VisualCalendar:TableHeader.renderer\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableHeaderVisualCalendarRendererPainter");

        d.put(compName + ".States", "Enabled");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 5 5 5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foregroundShadow", "00000033");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "topBorder", "#959ea3");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "bottomBorder", "#b6bdbf");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5"), new Dimension(22, 20),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTableHeaderRendererPainter.BACKGROUND_ENABLED, ctx));

    }

    protected void defineLabel(final UIDefaults d) {
        final String compName = "Label";
        this.defineLabel(compName, d);
    }

    protected void defineLabel(String compName, final UIDefaults d) {
        // Label
        if (compName == null) {
            compName = "Label";
        }
        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(this.defaultFont));
        d.put(compName + ".contentMargins", new InsetsUIResource(0, 0, 0, 0));

        d.put(compName + ".States", "Disabled,Enabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        /*
         * Note: Colors with state ([Enables]...) are used on renderers of Combos, Lists, etc. Due to,
         * background of this components is white, the color of the font is #335971
         */
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#243b4aCC");
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground",
                null);
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].textForeground", "#FFFFFF");
        /*
         * Generic colors of the Label when used alone, that is, when label background is not set and then,
         * white color is used.
         */
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");
        OntimizeLookAndFeel.setColor(d, compName, "foreground", "#6387A0");
        OntimizeLookAndFeel.setColor(d, compName, "disabled", "#FFFFFF7D");
        OntimizeLookAndFeel.setColor(d, compName, "disabledText", "#243b4aCC");

    }

    protected void defineELabel(final UIDefaults d) {
        final String compName = "\"ELabel\"";
        this.defineELabel(compName, d);
    }

    protected void defineResultCountLabel(final UIDefaults d) {
        final String compName = "\"ResultCountLabel\"";
        this.defineELabel(compName, d);
    }

    protected void defineELabel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"ELabel\"";
        }
        // Label
        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(this.defaultFont));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", "#FFFFFF");

    }

    protected void defineButton(final UIDefaults d) {
        final String compName = "Button";
        this.defineButton(compName, d);
    }

    protected void defineButton(String compName, final UIDefaults d) {

        if (compName == null) {
            compName = "Button";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OButtonPainter");

        // Button
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "7 7 7 7");
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", OntimizeLAFParseUtils.fontToString(this.defaultFont));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8e8f91");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed].textForeground", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default+Focused].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default+MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default+Focused+MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default+Pressed].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Default+Focused+Pressed].textForeground", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setFloat(d, compName, "[Default].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused+MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Pressed].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused+Pressed].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Enabled].alphaTransparency", "0.3");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.3");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed].alphaTransparency", "0.5");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "7 7 7 7"),
                new Dimension(104, 33), false, PaintContext.CacheMode.NO_CACHING,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));

        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineToggleButton(final UIDefaults d) {
        final String compName = "ToggleButton";
        this.defineToggleButton(compName, d);
    }

    protected void defineToggleButton(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "ToggleButton";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "7 7 7 7");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8e8f91");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed].textForeground", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver+Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed+Selected].textForeground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed+Selected].textForeground", null);

        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Enabled].alphaTransparency", "0.3");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.3");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setFloat(d, compName, "[Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed+Selected].alphaTransparency", "0.5");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OToggleButtonPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "7 7 7 7"),
                new Dimension(104, 33), false, PaintContext.CacheMode.NO_CACHING,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));

        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[Disabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_DISABLED_SELECTED, ctx));
        d.put(compName + "[Focused+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_SELECTED_FOCUSED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED, ctx));
        d.put(compName + "[Focused+MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_PRESSED_SELECTED, ctx));
        d.put(compName + "[Focused+Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_PRESSED_SELECTED_FOCUSED, ctx));

    }

    protected void defineSelectableItem(final UIDefaults d) {
        final String compName = "\"SelectableItem\"";
        this.defineSelectableItem(compName, d);
    }

    protected void defineSelectableItem(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"SelectableItem\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OCheckBoxPainter");

        final String iconBasePath = "com/ontimize/plaf/images/check/";

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
        // setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#243b4aCC");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#335971");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(180, 180), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "0 0 0 0"));
        // // addColor(d, "CheckBox[Disabled].textForeground",
        // "nimbusDisabledText", 0.0f, 0.0f, 0.0f, 0);
        String path = StyleUtil.getIconPath(compName, "[Disabled].icon",
                iconBasePath + "18x18_ico_checkbox_disabled.png");
        d.put(compName + "[Disabled].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_DISABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled].icon", iconBasePath + "18x18_ico_checkbox_enabled.png");
        d.put(compName + "[Enabled].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_ENABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused].icon", iconBasePath + "18x18_ico_checkbox_focused.png");
        d.put(compName + "[Focused].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver].icon", iconBasePath + "18x18_ico_checkbox_mouseover.png");
        d.put(compName + "[MouseOver].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver].icon",
                iconBasePath + "18x18_ico_checkbox_mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed].icon", iconBasePath + "18x18_ico_checkbox_pressed.png");
        d.put(compName + "[Pressed].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed].icon",
                iconBasePath + "18x18_ico_checkbox_pressed-focused.png");
        d.put(compName + "[Focused+Pressed].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Selected].icon", iconBasePath + "18x18_ico_checkbox_selected.png");
        d.put(compName + "[Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-focused.png");
        d.put(compName + "[Focused+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed.png");
        d.put(compName + "[Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed-focused.png");
        d.put(compName + "[Focused+Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover.png");
        d.put(compName + "[MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover-focussed.png");
        d.put(compName + "[Focused+MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Disabled+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_disabled-selected.png");
        d.put(compName + "[Disabled+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_DISABLED_SELECTED, ctx, path));

    }

    protected void defineCheckBox(final UIDefaults d) {
        final String compName = "CheckBox";
        this.defineCheckBox(compName, d);
    }

    protected void defineCheckBox(String compName, final UIDefaults d) {
        // //Initialize CheckBox
        if (compName == null) {
            compName = "CheckBox";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OCheckBoxPainter");
        final String iconBasePath = "com/ontimize/plaf/images/check/";

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(180, 180), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "0 0 0 0"));
        String path = StyleUtil.getIconPath(compName, "[Disabled].icon",
                iconBasePath + "18x18_ico_checkbox_disabled.png");
        d.put(compName + "[Disabled].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_DISABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled].icon", iconBasePath + "18x18_ico_checkbox_enabled.png");
        d.put(compName + "[Enabled].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_ENABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused].icon", iconBasePath + "18x18_ico_checkbox_focused.png");
        d.put(compName + "[Focused].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver].icon", iconBasePath + "18x18_ico_checkbox_mouseover.png");
        d.put(compName + "[MouseOver].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver].icon",
                iconBasePath + "18x18_ico_checkbox_mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed].icon", iconBasePath + "18x18_ico_checkbox_pressed.png");
        d.put(compName + "[Pressed].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed].icon",
                iconBasePath + "18x18_ico_checkbox_pressed-focused.png");
        d.put(compName + "[Focused+Pressed].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Selected].icon", iconBasePath + "18x18_ico_checkbox_selected.png");
        d.put(compName + "[Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-focused.png");
        d.put(compName + "[Focused+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed.png");
        d.put(compName + "[Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed-focused.png");
        d.put(compName + "[Focused+Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_PRESSED_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover.png");
        d.put(compName + "[MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover-focussed.png");
        d.put(compName + "[Focused+MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_MOUSEOVER_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Disabled+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_disabled-selected.png");
        d.put(compName + "[Disabled+Selected].iconPainter",
                this.createLazyPainter(pClass, OCheckBoxPainter.ICON_DISABLED_SELECTED, ctx, path));

    }

    protected void defineComboBox(final UIDefaults d) {

        // Initialize ComboBox
        final String compName = "ComboBox";
        this.defineComboBox(compName, d);
    }

    protected void defineComboBox(String compName, final UIDefaults d) {

        // Initialize ComboBox
        if (compName == null) {
            compName = "ComboBox";
        }
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 13 2 4");

        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Selected,Disabled,Focused,Editable,Required");
        d.put(compName + ".Required", new RequiredState());
        d.put(compName + ".Editable", new OComboBoxEditableState());
        d.put(compName + ".forceOpaque", Boolean.TRUE);
        d.put(compName + ".buttonWhenNotEditable", Boolean.TRUE);
        d.put(compName + ".rendererUseListColors", Boolean.TRUE);
        d.put(compName + ".pressedWhenPopupVisible", Boolean.TRUE);
        d.put(compName + ".squareButton", Boolean.FALSE);
        d.put(compName + ".popupInsets", new InsetsUIResource(-2, 2, 0, 2));
        d.put(compName + ".padding", new InsetsUIResource(0, 0, 0, 0));

        d.put(compName + ".numBorders", Integer.valueOf(4));

        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", "#8e8f91");
        OntimizeLookAndFeel.setColor(d, compName, "selectionForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColor(d, compName, "selectionBackground", "#36627F");

        // TextForeground:
        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#616d75");// #8F9CA4
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground", "#d4d5d7");
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;
        final ColorUIResource requiredFgColor = StyleUtil.getColorUI(compName, "[Required].textForeground", "#FFFFFF");
        ColorUIResource focusedRequiredFgColor = StyleUtil.getColorUI(compName, "[Focused+Required].textForeground",
                null);
        focusedRequiredFgColor = focusedRequiredFgColor != null ? focusedRequiredFgColor : requiredFgColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#61BEE8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        d.put(compName + "[Required].textForeground", requiredFgColor);
        d.put(compName + "[Focused+Required].textForeground", focusedRequiredFgColor);

        // Background:
        final ColorUIResource disabledColor = StyleUtil.getColorUI(compName, "[Disabled].background", "#a0b5c3");
        final ColorUIResource focusedColor = StyleUtil.getColorUI(compName, "[Focused].background", "#FFFFFF");
        final ColorUIResource requiredColor = StyleUtil.getColorUI(compName, "[Required].background", "#89A5B9");
        ColorUIResource disabledRequiredColor = StyleUtil.getColorUI(compName, "[Disabled+Required].background", "#6a8da5");
        disabledRequiredColor = disabledRequiredColor != null ? disabledRequiredColor
                : new ColorUIResource(OntimizeLAFColorUtils.setAlpha(requiredColor, 0.5));
        ColorUIResource focusedRequiredColor = StyleUtil.getColorUI(compName, "[Focused+Required].background", null);
        focusedRequiredColor = focusedRequiredColor != null ? focusedRequiredColor : requiredColor;

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        d.put(compName + "[Disabled].background", disabledColor);
        d.put(compName + "[Focused].background", focusedColor);
        d.put(compName + "[Required].background", requiredColor);
        d.put(compName + "[Focused+Required].background", focusedRequiredColor);
        d.put(compName + "[Disabled+Required].background", disabledRequiredColor);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed].background", "#FFFFFF");

        // Border:
        d.put(compName + "[Enabled].border", StyleUtil.getArrayColorUI(compName, "[Enabled].border", "#E5E5E57D"));
        d.put(compName + "[Required].border", StyleUtil.getArrayColorUI(compName, "[Required].border", "#E5E5E57D"));
        d.put(compName + "[Disabled].border", StyleUtil.getArrayColorUI(compName, "[Disabled].border", "#A5B6C0"));
        d.put(compName + "[Disabled+Required].border",
                StyleUtil.getArrayColorUI(compName, "[Disabled+Required].border", "#E5E5E57D"));
        final ColorUIResource[] focusedBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused].border",
                "#61BEE8FF #61BEE8B3 #61BEE866 #61BEE819");
        d.put(compName + "[Focused].border", focusedBorderColors);
        ColorUIResource[] focusedRequiredBorderColors = StyleUtil.getArrayColorUI(compName, "[Focused+Required].border",
                null);
        focusedRequiredBorderColors = focusedRequiredBorderColors != null ? focusedRequiredBorderColors
                : focusedBorderColors;
        d.put(compName + "[Focused+Required].border", focusedRequiredBorderColors);

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OComboBoxPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "2 13 2 4"),
                new Dimension(83, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY, 2.0);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_DISABLED_PRESSED, ctx));
        d.put(compName + "[Disabled+Required].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Required].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_REQUIRED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
        d.put(compName + "[Focused+Required].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_REQUIRED_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Enabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_ENABLED_SELECTED, ctx));
        d.put(compName + "[Disabled+Editable].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_DISABLED_EDITABLE, ctx));
        d.put(compName + "[Editable+Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_ENABLED_EDITABLE, ctx));
        d.put(compName + "[Editable+Focused].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_FOCUSED_EDITABLE, ctx));
        d.put(compName + "[Editable+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_MOUSEOVER_EDITABLE, ctx));
        d.put(compName + "[Editable+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxPainter.BACKGROUND_PRESSED_EDITABLE, ctx));


        compName = "ComboBox:\"ComboBox.renderer\"";
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FF0000"); //335971
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FF0000"); //8e8f91
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#FF0000"); //61BEE8
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FF0000"); //FFFFFF
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Required].textForeground", "#FF0000");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Required].textForeground", "#FF0000");
    }

    protected void defineComboBoxTextField(final UIDefaults d) {
        final String compName = "ComboBox:\"ComboBox.textField\"";
        this.defineComboBoxTextField(compName, d);
    }

    protected void defineComboBoxTextField(String compName, final UIDefaults d) {
        // ************ComboBox.TextField*******************************
        if (compName == null) {
            compName = "ComboBox:\"ComboBox.textField\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OComboBoxTextFieldPainter");

        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected,Required");
        d.put(compName + ".Required", new RequiredState());
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#616D75");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].textForeground", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].background", "#89A5B9");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(64, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY, 2.0);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxTextFieldPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxTextFieldPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OComboBoxTextFieldPainter.BACKGROUND_SELECTED, ctx));
    }

    protected void defineComboBoxArrow(final UIDefaults d) {
        final String compName = "ComboBox:\"ComboBox.arrowButton\"";
        this.defineComboBoxArrow(compName, d);
    }

    protected void defineComboBoxArrow(String compName, final UIDefaults d) {
        // *************ComboBox.Arrow******************************
        if (compName == null) {
            compName = "ComboBox:\"ComboBox.arrowButton\"";
        }
        d.put(compName + ".contentMargins", new InsetsUIResource(1, 1, 1, 1));
        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Disabled,Editable,Required");
        d.put(compName + ".Required", new RequiredState());
        d.put(compName + ".Editable", new OComboBoxArrowButtonEditableState());
        d.put(compName + ".size", Integer.valueOf(21));

        /*
         * Note: background arrow button is painted into OComboBoxPainter.
         */
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#2639457D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Required].background", "#2639457D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Required].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].background", "#263945");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Editable].background", "#2639457D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Editable+Enabled].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Editable+Focused].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Editable+MouseOver].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Editable+Pressed].background", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Editable+Required].background", "#263945");

        OntimizeLookAndFeel.setColor(d, compName, "[Disabled].foreground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColor(d, compName, "[Disabled+Required].foreground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColor(d, compName, "[Enabled].foreground", "#FFFFFF");
        OntimizeLookAndFeel.setColor(d, compName, "[Selected].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[MouseOver].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[MouseOver+Required].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Pressed].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Required].foreground", "#FFFFFF");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OComboBoxArrowButtonPainter");
        final PaintContext ctx = new PaintContext(new Insets(1, 1, 1, 1), new Dimension(20, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING,
                Double.POSITIVE_INFINITY, 2.0);
        // because it is registered into NimbusDefaults
        d.remove(compName + "[Disabled+Editable].backgroundPainter");
        d.remove(compName + "[Editable+Enabled].backgroundPainter");
        d.remove(compName + "[Editable+MouseOver].backgroundPainter");
        d.remove(compName + "[Editable+Pressed].backgroundPainter");
        d.remove(compName + "[Editable+Selected].backgroundPainter");
        d.remove(compName + "[Editable+Required].backgroundPainter");

        d.put(compName + "[Enabled].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_ENABLED, ctx));
        d.put(compName + "[MouseOver].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_MOUSEOVER, ctx));
        d.put(compName + "[MouseOver+Required].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_MOUSEOVER_REQUIRED, ctx));
        d.put(compName + "[Disabled].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Required].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_DISABLED_REQUIRED, ctx));
        d.put(compName + "[Pressed].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_PRESSED, ctx));
        d.put(compName + "[Selected].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_SELECTED, ctx));
        d.put(compName + "[Required].foregroundPainter",
                this.createLazyPainter(pClass, OComboBoxArrowButtonPainter.FOREGROUND_REQUIRED, ctx));
    }

    protected void defineComboBoxListRenderer(final UIDefaults d) {
        final String compName = "ComboBox:\"ComboBox.listRenderer\"";
        this.defineComboBoxListRenderer(compName, d);
    }

    protected void defineComboBoxListRenderer(String compName, final UIDefaults d) {
        // **************ComboBox.ListRenderer****************
        if (compName == null) {
            compName = "ComboBox:\"ComboBox.listRenderer\"";
        }
        d.put(compName + ".States", "Enabled,Disabled,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 10 2 6");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#000000");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#616d75");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Required].textForeground", "#d4d5d7");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].textForeground", "#ffffff"); //8e8f91
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#ffffff"); //ffffff
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#000000"); //ffffff
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#ffffff");
    }

    protected void defineComboBoxRenderer(final UIDefaults d) {
        final String compName = "ComboBox:\"ComboBox.renderer\"";
        this.defineComboBoxRenderer(compName, d);
    }

    protected void defineComboBoxRenderer(String compName, final UIDefaults d) {
        // *************ComboBox.Renderer*************************
        if (compName == null) {
            compName = "ComboBox:\"ComboBox.renderer\"";
        }

        d.put(compName + ".States", "Disabled,Selected,Required");
        d.put(compName + ".Required", new RequiredState());

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 4 2 4");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff");

        final ColorUIResource disabledFgColor = StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#8e8f91");
        ColorUIResource disabledRequiredFgColor = StyleUtil.getColorUI(compName, "[Disabled+Required].textForeground",
                null);
        disabledRequiredFgColor = disabledRequiredFgColor != null ? disabledRequiredFgColor : disabledFgColor;

        d.put(compName + "[Disabled].textForeground", disabledFgColor);
        d.put(compName + "[Disabled+Required].textForeground", disabledRequiredFgColor);
        OntimizeLookAndFeel.setColor(d, compName, "[Selected].textForeground", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Required].textForeground", "#FFFFFF");
    }

    protected void defineComboBoxScrollPane(final UIDefaults d) {
        final String compName = "\"ComboBox.scrollPane\"";
        this.defineComboBoxScrollPane(compName, d);
    }

    protected void defineComboBoxScrollPane(String compName, final UIDefaults d) {
        // *************ComboBox.scrollPane*************************
        if (compName == null) {
            compName = "\"ComboBox.scrollPane\"";
        }
        d.put(compName + ".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        final ColorUIResource borderColor = StyleUtil.getColorUI(compName, "border", "#8CA0AD");
        final Border cBorder = BorderFactory.createLineBorder(borderColor, 2);
        d.put(compName + ".border", cBorder);
    }

    protected void defineMenu(final UIDefaults d) {
        final String compName = "Menu";
        this.defineMenu(compName, d);
    }

    protected void defineMenu(String compName, final UIDefaults d) {
        // Menu:
        if (compName == null) {
            compName = "Menu";
        }

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));

        d.put(compName + "[Disabled].textForeground",
                StyleUtil.getColorUI(compName, "[Disabled].textForeground", "#FFFFFF7D"));
        d.put(compName + "[Enabled].textForeground",
                StyleUtil.getColorUI(compName, "[Enabled].textForeground", "#FFFFFF"));
        d.put(compName + "[Enabled+Selected].textForeground",
                StyleUtil.getColorUI(compName, "[Enabled+Selected].textForeground", "#426a84"));

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OMenuPainter");
        // Background...
        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "3 10 3 10"));
        d.put(compName + "[Enabled+Selected].background",
                StyleUtil.getColor(compName, "[Enabled+Selected].background", "#86adbf"));
        d.put(compName + "[Enabled+Selected].border",
                StyleUtil.getColor(compName, "[Enabled+Selected].border", "#c6dfe3"));

        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "3 10 3 10"), new Dimension(100, 30),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + "[Enabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OMenuPainter.BACKGROUND_ENABLED_SELECTED, ctx));

        // Arrow...
        d.put(compName + "[Enabled].arrowBackground",
                StyleUtil.getColor(compName, "[Enabled].arrowBackground", "#FFFFFF7D"));
        d.put(compName + "[Disabled].arrowBackground",
                StyleUtil.getColor(compName, "[Disabled].arrowBackground", "#0000007D"));
        d.put(compName + "[Enabled+Selected].arrowBackground",
                StyleUtil.getColor(compName, "[Enabled+Selected].arrowBackground", "#0000004C"));

        d.put(compName + ".arrowBackground_upperShadow",
                StyleUtil.getColor(compName, "arrowBackground_upperShadow", "#00000033"));
        d.put(compName + ".arrowBackground_bottomShadow",
                StyleUtil.getColor(compName, "arrowBackground_bottomShadow", "#ffffff7d"));
        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(1, 1, 1, 1),
                new Dimension(6, 13), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + "[Disabled].arrowIconPainter",
                this.createLazyPainter(pClass, OMenuPainter.ARROWICON_DISABLED, ctx));
        d.put(compName + "[Enabled].arrowIconPainter",
                this.createLazyPainter(pClass, OMenuPainter.ARROWICON_ENABLED, ctx));
        d.put(compName + "[Enabled+Selected].arrowIconPainter",
                this.createLazyPainter(pClass, OMenuPainter.ARROWICON_ENABLED_SELECTED, ctx));
    }

    protected void defineMenuBar(final UIDefaults d) {
        final String compName = "MenuBar";
        this.defineMenuBar(compName, d);
    }

    protected void defineMenuBar(String compName, final UIDefaults d) {

        // MenuBar:
        if (compName == null) {
            compName = "MenuBar";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OMenuBarPainter");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "1 5 0 5");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#1C304B");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#1C304B");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#1C304B");

        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "1 5 0 5"), new Dimension(18, 22),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, 2.0);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OMenuBarPainter.BACKGROUND_ENABLED, ctx));

        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "1 5 0 5"), new Dimension(30, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, 2.0);
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OMenuBarPainter.BORDER_ENABLED, ctx));

    }

    protected void defineMenuBarMenu(final UIDefaults d) {
        final String compName = "MenuBar:Menu";
        this.defineMenuBarMenu(compName, d);
    }

    protected void defineMenuBarMenu(String compName, final UIDefaults d) {
        // MenuBar:Menu
        if (compName == null) {
            compName = "MenuBar:Menu";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OMenuBarMenuPainter");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#B3CAd8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#B3CAd87D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#1C304B");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#86ADBF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].border", "#C6DFE3");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "3 15 0 15");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "3 15 0 15"), new Dimension(100, 30),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OMenuBarMenuPainter.BACKGROUND_SELECTED, ctx));
    }

    protected void defineMenuItem(final UIDefaults d) {
        final String compName = "MenuItem";
        this.defineMenuItem(compName, d);
    }

    protected void defineMenuItem(String compName, final UIDefaults d) {
        // MenuItem:
        if (compName == null) {
            compName = "MenuItem";
        }

        d.put(compName + ".States", "Enabled,MouseOver,Disabled");
        OntimizeLookAndFeel.setInteger(d, compName, "textIconGap", "5");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", "#426A84");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#FFFFFF");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OMenuItemPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "2 10 2 10"), new Dimension(100, 3),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "2 10 2 10"));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OMenuItemPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OMenuItemPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OMenuItemPainter.BACKGROUND_DISABLED, ctx));

        // MenuItemAccelerator
        OntimizeLookAndFeel.setColorUIResource(d, compName, ":MenuItemAccelerator[Disabled].textForeground",
                "#FFFFFF7F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, ":MenuItemAccelerator[Enabled].textForeground",
                "#FFFFFF7F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, ":MenuItemAccelerator[MouseOver].textForeground",
                "#426A84");
    }

    protected void definePopupItem(final UIDefaults d) {
        final String compName = "\"PopupItem\"";
        this.definePopupItem(compName, d);
    }

    protected void definePopupItem(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"PopupItem\"";
        }
        d.put(compName + ".States", "Enabled,MouseOver,Disabled");
        d.put(compName + ".textIconGap", Integer.valueOf(5));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", "#426A84");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#FFFFFF");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OPopupItemPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "1 10 1 10"), new Dimension(100, 3),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "1 10 1 10"));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OPopupItemPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OPopupItemPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OPopupItemPainter.BACKGROUND_DISABLED, ctx));

    }

    protected void defineRadioButtonMenuItem(final UIDefaults d) {
        // RadioButtonMenuItem:
        final String compName = "RadioButtonMenuItem";
        this.defineRadioButtonMenuItem(compName, d);
    }

    protected void defineRadioButtonMenuItem(String compName, final UIDefaults d) {
        // RadioButtonMenuItem:
        if (compName == null) {
            compName = "RadioButtonMenuItem";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.ORadioButtonMenuItemPainter");
        final String iconBasePath = "com/ontimize/plaf/images/radio/";

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 10 2 10");
        OntimizeLookAndFeel.setInteger(d, compName, "textIconGap", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", "#426A84");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].textForeground", "#426A84");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].background", "#FFFFFF");

        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "2 10 2 10"), new Dimension(100, 3),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.BACKGROUND_SELECTED_MOUSEOVER, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.BACKGROUND_DISABLED, ctx));

        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "1 10 1 10"), new Dimension(18, 18), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        String path = StyleUtil.getIconPath(compName, "[Disabled].icon",
                iconBasePath + "18x18_ico_radiobutton_disabled.png");
        d.put(compName + "[Disabled].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_DISABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled].icon", iconBasePath + "18x18_ico_radiobutton_enabled.png");
        d.put(compName + "[Enabled].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_ENABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused].icon", iconBasePath + "18x18_ico_radiobutton_focused.png");
        d.put(compName + "[Focused].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver].icon",
                iconBasePath + "18x18_ico_radiobutton_mouseover.png");
        d.put(compName + "[MouseOver].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_MOUSEOVER, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver].icon",
                iconBasePath + "18x18_ico_radiobutton_mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver].checkIconPainter", this.createLazyPainter(pClass,
                ORadioButtonMenuItemPainter.CHECKICON_ICON_MOUSEOVER_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed].icon", iconBasePath + "18x18_ico_radiobutton_pressed.png");
        d.put(compName + "[Pressed].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_PRESSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed].icon",
                iconBasePath + "18x18_ico_radiobutton_pressed-focused.png");
        d.put(compName + "[Focused+Pressed].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_PRESSED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected.png");
        d.put(compName + "[Enabled+Selected].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-focused.png");
        d.put(compName + "[Focused+Selected].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-pressed.png");
        d.put(compName + "[Pressed+Selected].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_PRESSED_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-pressed-focused.png");
        d.put(compName + "[Focused+Pressed+Selected].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_PRESSED_SELECTED_FOCUSED, ctx,
                        path));

        path = StyleUtil.getIconPath(compName, "[MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-mouseover.png");
        d.put(compName + "[MouseOver+Selected].checkIconPainter", this.createLazyPainter(pClass,
                ORadioButtonMenuItemPainter.CHECKICON_ICON_MOUSEOVER_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver+Selected].checkIconPainter",
                this.createLazyPainter(pClass, ORadioButtonMenuItemPainter.CHECKICON_ICON_MOUSEOVER_SELECTED_FOCUSED,
                        ctx, path));

        path = StyleUtil.getIconPath(compName, "[Disabled+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_disabled-selected.png");
        d.put(compName + "[Disabled+Selected].checkIconPainter", this.createLazyPainter(pClass,
                ORadioButtonMenuItemPainter.CHECKICON_ICON_DISABLED_SELECTED, ctx, path));

    }

    protected void defineCheckBoxMenuItem(final UIDefaults d) {

        // CheckBoxMenuItem :
        final String compName = "CheckBoxMenuItem";
        this.defineCheckBoxMenuItem(compName, d);
    }

    protected void defineCheckBoxMenuItem(String compName, final UIDefaults d) {

        // CheckBoxMenuItem
        if (compName == null) {
            compName = "CheckBoxMenuItem";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OCheckBoxMenuItemPainter");
        final String iconBasePath = "com/ontimize/plaf/images/check/";

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 10 2 10");
        OntimizeLookAndFeel.setInteger(d, compName, "textIconGap", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", "#426A84");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].textForeground", "#426A84");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#FFFFFF01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].background", "#FFFFFF");

        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "2 10 2 10"), new Dimension(100, 3),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.BACKGROUND_SELECTED_MOUSEOVER, ctx));

        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "1 10 1 10"), new Dimension(18, 18), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        String path = StyleUtil.getIconPath(compName, "[Disabled].icon",
                iconBasePath + "18x18_ico_checkbox_disabled.png");
        d.put(compName + "[Disabled].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_DISABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled].icon", iconBasePath + "18x18_ico_checkbox_enabled.png");
        d.put(compName + "[Enabled].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_ENABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused].icon", iconBasePath + "18x18_ico_checkbox_focused.png");
        d.put(compName + "[Focused].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver].icon", iconBasePath + "18x18_ico_checkbox_mouseover.png");
        d.put(compName + "[MouseOver].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_MOUSEOVER, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver].icon",
                iconBasePath + "18x18_ico_checkbox_mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_MOUSEOVER_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed].icon", iconBasePath + "18x18_ico_checkbox_pressed.png");
        d.put(compName + "[Pressed].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_PRESSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed].icon",
                iconBasePath + "18x18_ico_checkbox_pressed-focused.png");
        d.put(compName + "[Focused+Pressed].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_PRESSED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected.png");
        d.put(compName + "[Enabled+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-focused.png");
        d.put(compName + "[Focused+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed.png");
        d.put(compName + "[Pressed+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_PRESSED_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-pressed-focused.png");
        d.put(compName + "[Focused+Pressed+Selected].checkIconPainter", this.createLazyPainter(pClass,
                OCheckBoxMenuItemPainter.CHECKICON_ICON_PRESSED_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover.png");
        d.put(compName + "[MouseOver+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_MOUSEOVER_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_selected-mouseover-focussed.png");
        d.put(compName + "[Focused+MouseOver+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_MOUSEOVER_SELECTED_FOCUSED, ctx,
                        path));

        path = StyleUtil.getIconPath(compName, "[Disabled+Selected].icon",
                iconBasePath + "18x18_ico_checkbox_disabled-selected.png");
        d.put(compName + "[Disabled+Selected].checkIconPainter",
                this.createLazyPainter(pClass, OCheckBoxMenuItemPainter.CHECKICON_ICON_DISABLED_SELECTED, ctx, path));

    }

    protected void defineList(final UIDefaults d) {
        // Initialize List
        final String compName = "List";
        this.defineList(compName, d);
    }

    protected void defineList(String compName, final UIDefaults d) {
        // Initialize List
        if (compName == null) {
            compName = "List";
        }
        // OntimizeLookAndFeel.setInsetsUIResource(d, compName,
        // "contentMargins", "0 0 0 0");
        Insets in = StyleUtil.getInsets(compName, "contentMargins", "2 6 2 6");
        d.put(compName + ".cellNoFocusBorder",
                new BorderUIResource(BorderFactory.createEmptyBorder(in.top, in.left, in.bottom, in.right)));

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "rendererUseListColors", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "rendererUseUIBorder", "true");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");// setColorUIResource
        OntimizeLookAndFeel.setColor(d, compName, "foreground", "#335971");// setColorUIResource
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "dropLineColor", "#73a4d1");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", "#39698a");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", "#8e8f91");

        OntimizeLookAndFeel.setColor(d, compName, "[Selected].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColor(d, compName, "[Selected].textBackground", "#36627F");
        OntimizeLookAndFeel.setColor(d, compName, "[Disabled+Selected].textBackground", "#36627F");
        OntimizeLookAndFeel.setColor(d, compName, "[Disabled].textForeground", "#8e8f91");

        // List.cellRenderer...
        compName = "List:\"List.cellRenderer\"";
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 6 2 6");

        in = StyleUtil.getInsets(compName, "contentMargins", "2 6 2 6");
        d.put(compName + ".cellNoFocusBorder",
                new BorderUIResource(BorderFactory.createEmptyBorder(in.top, in.left, in.bottom, in.right)));
        ListDataField.defaultNoFocusBorder = BorderFactory.createEmptyBorder(in.top, in.left, in.bottom, in.right);
        d.put(compName + ".focusCellHighlightBorder",
                new BorderUIResource(new PainterBorder("Tree:TreeCell[Enabled+Focused].backgroundPainter", in)));

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8e8f91");
        OntimizeLookAndFeel.setColor(d, compName, "[Disabled].background", "#39698a");
    }

    protected void definePanel(final UIDefaults d) {
        final String compName = "Panel";
        this.definePanel(compName, d);
    }

    protected void definePanel(String compName, final UIDefaults d) {
        // Panel
        if (compName == null) {
            compName = "Panel";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#366581");
        OntimizeLookAndFeel.setColor(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColor(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColor(d, compName, "disabledText", null);

    }

    protected void definePopupMenu(final UIDefaults d) {
        final String compName = "PopupMenu";
        this.definePopupMenu(compName, d);
    }

    protected void definePopupMenu(String compName, final UIDefaults d) {
        // Popup Menu:
        if (compName == null) {
            compName = "PopupMenu";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "18 9 16 9");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setBoolean(d, compName, "consumeEventOnClose", "true");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#8cbec6");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#8cbec6");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#86adbf");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].border", "#c6dfe3");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#c6dfe3");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OPopupMenuPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "18 9 16 9"), new Dimension(220, 313),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OPopupMenuPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OPopupMenuPainter.BACKGROUND_ENABLED, ctx));

    }

    protected void definePopupMenuSeparator(final UIDefaults d) {
        final String compName = "PopupMenuSeparator";
        this.definePopupMenuSeparator(compName, d);
    }

    protected void definePopupMenuSeparator(String compName, final UIDefaults d) {
        // Popup Menu Separator:
        if (compName == null) {
            compName = "PopupMenuSeparator";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 0 5 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "bottomshadowcolor", "#FFFFFF33");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "uppershadowcolor", "#00000033");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OPopupMenuSeparatorPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 0 5 0"), new Dimension(100, 2),
                true, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OPopupMenuSeparatorPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineSeparator(final UIDefaults d) {
        final String compName = "Separator";
        this.defineSeparator(compName, d);
    }

    protected void defineSeparator(String compName, final UIDefaults d) {
        // Separator:
        if (compName == null) {
            compName = "Separator";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 5 5 5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "00000033");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].backgroundShadow", "FFFFFF33");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OSeparatorPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5"), new Dimension(100, 2),
                true, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put("Separator[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OSeparatorPainter.BACKGROUND_ENABLED, ctx));

    }

    protected void defineProgressBar(final UIDefaults d) {
        final String compName = "ProgressBar";
        this.defineProgressBar(compName, d);
    }

    protected void defineProgressBar(String compName, final UIDefaults d) {
        // JProgressBar...
        if (compName == null) {
            compName = "ProgressBar";
        }
        d.put(compName + ".Indeterminate", new OProgressBarIndeterminateState());
        d.put(compName + ".Finished", new OProgressBarFinishedState());

    }

    protected void defineSliders(final UIDefaults d) {
        final String compName = "Slider";
        this.defineSliders(compName, d);
    }

    protected void defineSliders(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "Slider";
        }
        d.put(compName + ".ArrowShape", new OSliderThumbArrowShapeState());
        d.put(compName + ":SliderThumb.ArrowShape", new OSliderThumbArrowShapeState());
        d.put(compName + ":SliderTrack.ArrowShape", new OSliderThumbArrowShapeState());
    }

    protected void defineSplitPane(final UIDefaults d) {
        final String compName = "SplitPane";
        this.defineSplitPane(compName, d);
    }

    protected void defineSplitPane(String compName, final UIDefaults d) {
        // SplitPane:
        if (compName == null) {
            compName = "SplitPane";
        }

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setInteger(d, compName, "size", "12");
        OntimizeLookAndFeel.setInteger(d, compName, "dividerSize", "12");

        OntimizeLookAndFeel.setBoolean(d, compName, "centerOneTouchButtons", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "oneTouchExpandable", "false");
        OntimizeLookAndFeel.setBoolean(d, compName, "continuousLayout", "true");
        OntimizeLookAndFeel.setInteger(d, compName, "oneTouchButtonOffset", "30");

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
        d.put(compName + ".Vertical", new OSplitPaneVerticalState());

    }

    protected void defineSplitPaneDivider(final UIDefaults d) {
        final String compName = "SplitPane:SplitPaneDivider";
        this.defineSplitPaneDivider(compName, d);
    }

    protected void defineSplitPaneDivider(String compName, final UIDefaults d) {
        // SplitPane: SplitPaneDivider :
        if (compName == null) {
            compName = "SplitPane:SplitPaneDivider";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OSplitPaneDividerPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "3 0 3 0");
        d.put(compName + ".States", "Enabled,MouseOver,Pressed,Disabled,Focused,Selected,Vertical");
        d.put(compName + ".Vertical", new OSplitPaneDividerVerticalState());

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#E6E6E6");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#E6E6E6");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].foreground", "#AAAAAA7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Vertical].foreground", "#AAAAAA7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].foregroundBorder", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Vertical].foregroundBorder", "#FFFFFF");

        final Insets in = StyleUtil.getInsets(compName, "contentMargins", "3 0 3 0");
        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(in, new Dimension(68, 10),
                false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OSplitPaneDividerPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, OSplitPaneDividerPainter.BACKGROUND_FOCUSED, ctx));

        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(0, 1, 0, 1),
                new Dimension(92, 12), true,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, 2.0);
        d.put(compName + "[Enabled].foregroundPainter",
                this.createLazyPainter(pClass, OSplitPaneDividerPainter.FOREGROUND_ENABLED, ctx));
        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(1, 0, 1, 0),
                new Dimension(12, 90), true,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, 2.0);
        d.put(compName + "[Enabled+Vertical].foregroundPainter",
                this.createLazyPainter(pClass, OSplitPaneDividerPainter.FOREGROUND_ENABLED_VERTICAL, ctx));

    }

    protected void defineTree(final UIDefaults d) {
        final String compName = "Tree";
        this.defineTree(compName, d);
    }

    protected void defineTree(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "Tree";
        }

        OntimizeLookAndFeel.setBoolean(d, compName, "rendererFillBackground", "false");
        OntimizeLookAndFeel.setBoolean(d, compName, "drawHorizontalLines", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "drawVerticalLines", "true");
        d.put(compName + ".linesStyle", "dashed");
        OntimizeLookAndFeel.setBoolean(d, compName, "lineTypeCurved", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "paintLines", "true");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "lineColor", "#C5D1DA");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "lineSelectionColor", "#517286");
        OntimizeLookAndFeel.setInteger(d, compName, "rightChildIndent", "10");
        OntimizeLookAndFeel.setIcon(d, compName, "expandedIcon", "com/ontimize/plaf/images/11x11_tree_open.png");
        OntimizeLookAndFeel.setIcon(d, compName, "collapsedIcon", "com/ontimize/plaf/images/11x11_tree_close.png");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "selectionBackground", "#517286");
        OntimizeLookAndFeel.setColor(d, compName, "selectionForeground", "#E4EDF0");
        OntimizeLookAndFeel.setColor(d, compName, "textForeground", "#94A4B0");

    }

    protected void defineTreeCellRenderer(final UIDefaults d) {
        final String compName = "Tree:\"Tree.cellRenderer\"";
        this.defineTreeCellRenderer(compName, d);
    }

    protected void defineTreeCellRenderer(String compName, final UIDefaults d) {
        // OTreeCellRendererPainter
        if (compName == null) {
            compName = "Tree:\"Tree.cellRenderer\"";
        }

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 2 0 30");
        OntimizeLookAndFeel.setInteger(d, compName, "iconTextGap", "10");
        Font tCRFont = this.getDefaultFont();
        tCRFont = tCRFont.deriveFont((float) tCRFont.getSize() - 1);
        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(tCRFont));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#517286");
        OntimizeLookAndFeel.setColor(d, compName, "rootForeground", "#4AAEE3");
        OntimizeLookAndFeel.setColor(d, compName, "rootSelectionForeground", "#396F8A");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#E4EDF0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "backgroundSelectionParent", "#ABC7D8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "backgroundSelection", "#517286");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "topBackgroundSelection", "#638092");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "bottomBackgroundSelection", "#496678");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "backgroundChildCount", "#36627F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foregroundChildCount", "#E4EDF0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "backgroundSelectionCount", "#E4EDF0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foregroundSelectionChildCount", "#517286");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTreeCellRendererPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 2 0 30"),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OTreeCellRendererPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTreeCellEditor(final UIDefaults d) {
        final String compName = "\"Tree.cellEditor\"";
        this.defineTreeCellEditor(compName, d);
    }

    protected void defineTreeCellEditor(String compName, final UIDefaults d) {
        // Initialize \"Tree.cellEditor\"
        if (compName == null) {
            compName = "\"Tree.cellEditor\"";
        }
        d.put(compName + ".contentMargins", new InsetsUIResource(2, 5, 2, 5));
        d.put(compName + ".opaque", Boolean.TRUE);
        Font tCRFont = this.getDefaultFont();
        tCRFont = tCRFont.deriveFont((float) tCRFont.getSize() - 1);
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", OntimizeLAFParseUtils.fontToString(tCRFont));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#ffffff");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTreeCellEditorPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "2 5 2 5"),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTreeCellEditorPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled+Focused].backgroundPainter",
                this.createLazyPainter(pClass, OTreeCellEditorPainter.BACKGROUND_ENABLED_FOCUSED, ctx));
    }

    protected void defineDiagramToolBar(final UIDefaults d) {
        final String compName = "\"DiagramToolBar\"";
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(1, 1, 1, 1),
                new Dimension(30, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 3.0, 3.0);
        d.put(compName + "[East].backgroundPainter", this.createLazyPainter(
                "com.ontimize.plaf.painter.EastOToolBarPainter", AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[North].backgroundPainter",
                this.createLazyPainter("com.ontimize.plaf.painter.NorthODiagramToolBarPainter",
                        AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[South].backgroundPainter", this.createLazyPainter(
                "com.ontimize.plaf.painter.SouthOToolBarPainter", AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[West].backgroundPainter", this.createLazyPainter(
                "com.ontimize.plaf.painter.WestOToolBarPainter", AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineToolBar(final UIDefaults d) {
        this.defineDiagramToolBar(d);
        final String compName = "ToolBar";
        this.defineToolBar(compName, d);
    }

    protected void defineToolBar(String compName, final UIDefaults d) {
        // Toolbar:
        if (compName == null) {
            compName = "ToolBar";
        }

        d.put(compName + ".States", "North,East,West,South");
        d.put(compName + ".North", new OToolBarNorthState());
        d.put(compName + ".East", new OToolBarEastState());
        d.put(compName + ".West", new OToolBarWestState());
        d.put(compName + ".South", new OToolBarSouthState());

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "useTextureImage", "true");
        d.put(compName + ".backgroundImage",
                StyleUtil.getProperty(compName, "backgroundImage", "com/ontimize/plaf/images/toolbarbackground.png"));
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);

        // WARNING: There is an important BUG in class NimbusDefaults in Nimbus.
        // The TOOLBAR (not other components inside the toolbar such as
        // ToolBar:Button or ToolBar:ToggleButton)
        // has not normal states (enable, disable, mouseover, ...). The toolbar
        // has the states stored in the variable ToolBar.States, which
        // (by default in Nimbus) are: North,East,West,South.
        // For that reason, keys like ToolBar[Enabled].borderPainter or
        // ToolBar[Enabled].handleIconPainter has no sense. Two ways for fixing
        // them:
        // Solution A) override those keys changing the status Enabled for the
        // others (North,East,West,South).
        // Solution B) override the property ToolBar.States in order to add the
        // Enabled value to the existing ones (North,East,West,South).
        // In Ontimize L&F we choose the first one, because the status for the
        // toolbar is always enabled.
        PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(30, 30),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OToolBarPainter");
        d.put(compName + "[East].borderPainter", this.createLazyPainter(pClass, OToolBarPainter.BORDER_EAST, ctx));
        d.put(compName + "[North].borderPainter", this.createLazyPainter(pClass, OToolBarPainter.BORDER_NORTH, ctx));
        d.put(compName + "[South].borderPainter", this.createLazyPainter(pClass, OToolBarPainter.BORDER_SOUTH, ctx));
        d.put(compName + "[West].borderPainter", this.createLazyPainter(pClass, OToolBarPainter.BORDER_WEST, ctx));

        // WARNING: Here, there is also other BUG in class NimbusDefaults in
        // Nimbus. Only the values for keys that contains backgroundPainter,
        // foregroundPainter or borderPainter
        // are processed as a painter (See class NimbusStyle, method
        // init(values, defaults)) => Method createValue() in LazyPainter never
        // was called for the key handleIconPainter.
        // Fixing: as we use the same painter for the whole Toolbar
        // (OToolBarPainter), we change the not-valid key
        // (ToolBar[Enabled].handleIconPainter) for a valid key
        // (ToolBar[East].backgroundPainter).
        ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(1, 1, 1, 1),
                new Dimension(30, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 3.0, 3.0);
        pClass = StyleUtil.getProperty(compName, "[East].painterClass",
                "com.ontimize.plaf.painter.EastOToolBarPainter");
        d.put(compName + "[East].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        pClass = StyleUtil.getProperty(compName, "[North].painterClass",
                "com.ontimize.plaf.painter.NorthOToolBarPainter");
        d.put(compName + "[North].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        pClass = StyleUtil.getProperty(compName, "[South].painterClass",
                "com.ontimize.plaf.painter.SouthOToolBarPainter");
        d.put(compName + "[South].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));
        pClass = StyleUtil.getProperty(compName, "[West].painterClass",
                "com.ontimize.plaf.painter.WestOToolBarPainter");
        d.put(compName + "[West].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOToolBarPainter.BACKGROUND_ENABLED, ctx));

        final Integer height = StyleUtil.getInteger(compName, "height", "50");
        ApplicationToolBar.DEFAULT_TOOLBAR_HEIGHT = height;

    }

    protected void defineToolBarButton(final UIDefaults d) {
        // ToolBar: Button:
        final String compName = "ToolBar:Button";
        this.defineToolBarButton(compName, d);
    }

    protected void defineToolBarButton(String compName, final UIDefaults d) {
        // ToolBar: Button:
        if (compName == null) {
            compName = "ToolBar:Button";
        }

        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 8 0 8");
        OntimizeLookAndFeel.setInteger(d, compName, "size", "32");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#817F7F7F");

        OntimizeLookAndFeel.setFloat(d, compName, "[Default].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused+MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Pressed].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Default+Focused+Pressed].alphaTransparency", "1.0");

        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Enabled].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed].alphaTransparency", "1.0");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OToolBarButtonPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 8 0 8"), new Dimension(32, 32),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, Double.POSITIVE_INFINITY);
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));

        final Integer size = StyleUtil.getInteger(compName, "size", "32");
        ApplicationToolBar.DEFAULT_BUTTON_SIZE = size;
        ApToolBarNavigator.defaultButtonsBorder = BorderFactory.createEmptyBorder();
    }

    protected void defineToolBarToggleButton(final UIDefaults d) {
        // ToolBar: ToggleButton:
        final String compName = "ToolBar:ToggleButton";
        this.defineToolBarToggleButton(compName, d);
    }

    protected void defineToolBarToggleButton(String compName, final UIDefaults d) {
        // ToolBar: ToggleButton:
        if (compName == null) {
            compName = "ToolBar:ToggleButton";
        }
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 8 0 8");
        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#817F7F7F");

        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Enabled].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed].alphaTransparency", "1.0");

        OntimizeLookAndFeel.setFloat(d, compName, "[Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled+Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver+Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver+Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed+Selected].alphaTransparency", "1.0");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed+Selected].alphaTransparency", "1.0");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 8 0 8"), new Dimension(32, 32),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 2.0, Double.POSITIVE_INFINITY);
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OToolBarToggleButtonPainter");

        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));

        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[Focused+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_PRESSED_SELECTED, ctx));
        d.put(compName + "[Focused+Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_PRESSED_SELECTED_FOCUSED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED, ctx));
        d.put(compName + "[Focused+MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Disabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarToggleButtonPainter.BACKGROUND_DISABLED_SELECTED, ctx));

    }

    protected void defineToolBarSeparator(final UIDefaults d) {
        final String compName = "ToolBarSeparator";
        this.defineToolBarSeparator(compName, d);
    }

    protected void defineToolBarSeparator(String compName, final UIDefaults d) {
        // ToolBarSeparator:
        if (compName == null) {
            compName = "ToolBarSeparator";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OToolBarSeparatorPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF4C");

        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(38, 7), true,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OToolBarSeparatorPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineToolTip(final UIDefaults d) {
        final String compName = "ToolTip";
        this.defineToolTip(compName, d);
    }

    protected void defineToolTip(String compName, final UIDefaults d) {
        // ToolTip
        if (compName == null) {
            compName = "ToolTip";
        }

        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 9 6 9");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#86ADBF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#FFFFFF01");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "border", "#C6DFE3");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 9 6 9"), new Dimension(10, 10),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OToolTipPainter");
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OToolTipPainter.BACKGROUND_ENABLED, ctx));

    }

    protected void defineRadioButton(final UIDefaults d) {
        // Initialize RadioButton
        final String compName = "RadioButton";
        this.defineRadioButton(compName, d);
    }

    protected void defineRadioButton(String compName, final UIDefaults d) {
        // Initialize RadioButton
        if (compName == null) {
            compName = "RadioButton";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.ORadioButtonPainter");
        final String iconBasePath = "com/ontimize/plaf/images/radio/";

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#ffffff01");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#FFFFFF");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(18, 18), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".contentMargins", StyleUtil.getInsetsUI(compName, "contentMargins", "0 0 0 0"));

        String path = StyleUtil.getIconPath(compName, "[Disabled].icon",
                iconBasePath + "18x18_ico_radiobutton_disabled.png");
        d.put(compName + "[Disabled].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_DISABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Enabled].icon", iconBasePath + "18x18_ico_radiobutton_enabled.png");
        d.put(compName + "[Enabled].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_ENABLED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused].icon", iconBasePath + "18x18_ico_radiobutton_focused.png");
        d.put(compName + "[Focused].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver].icon",
                iconBasePath + "18x18_ico_radiobutton_mouseover.png");
        d.put(compName + "[MouseOver].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_MOUSEOVER, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver].icon",
                iconBasePath + "18x18_ico_radiobutton_mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_MOUSEOVER_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed].icon", iconBasePath + "18x18_ico_radiobutton_pressed.png");
        d.put(compName + "[Pressed].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_PRESSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed].icon",
                iconBasePath + "18x18_ico_radiobutton_pressed-focused.png");
        d.put(compName + "[Focused+Pressed].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_PRESSED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Selected].icon", iconBasePath + "18x18_ico_radiobutton_selected.png");
        d.put(compName + "[Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-focused.png");
        d.put(compName + "[Focused+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Pressed+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-pressed.png");
        d.put(compName + "[Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_PRESSED_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+Pressed+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-pressed-focused.png");
        d.put(compName + "[Focused+Pressed+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_PRESSED_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-mouseover.png");
        d.put(compName + "[MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_MOUSEOVER_SELECTED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Focused+MouseOver+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_selected-mouseover-focused.png");
        d.put(compName + "[Focused+MouseOver+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_MOUSEOVER_SELECTED_FOCUSED, ctx, path));

        path = StyleUtil.getIconPath(compName, "[Disabled+Selected].icon",
                iconBasePath + "18x18_ico_radiobutton_disabled-selected.png");
        d.put(compName + "[Disabled+Selected].iconPainter",
                this.createLazyPainter(pClass, ORadioButtonPainter.ICON_DISABLED_SELECTED, ctx, path));
    }

    protected void defineRow(final UIDefaults d) {
        final String compName = "\"Row\"";
        this.defineRow(compName, d);
    }

    protected void defineRow(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"Row\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.ORowPanelPainter");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#48718c");
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, ORowPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineColumn(final UIDefaults d) {
        final String compName = "\"Column\"";
        this.defineColumn(compName, d);
    }

    protected void defineColumn(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"Column\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OColumnPanelPainter");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#48718c");
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OColumnPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineCardPanel(final UIDefaults d) {
        final String compName = "\"CardPanel\"";
        this.defineCardPanel(compName, d);
    }

    protected void defineCardPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"CardPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OCardPanelPainter");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#FFFFFF14");
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);

        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OCardPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineGrid(final UIDefaults d) {
        final String compName = "\"Grid\"";
        this.defineGrid(compName, d);
    }

    protected void defineGrid(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"Grid\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OGridPanelPainter");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#FFFFFF14");
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        Grid.defaultOpaque = StyleUtil.getBoolean(compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 5 5 5");

        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(insets,
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);

        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OGridPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTitleBorder(final UIDefaults d) {
        final String compName = "TitledBorder";
        this.defineTitleBorder(compName, d);
    }

    protected void defineTitleBorder(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "TitledBorder";
        }
        d.put(compName + ".position", "BELOW_TOP");
        final Insets insets = StyleUtil.getInsets(compName, "contentMargins", "10 10 10 10");
        final Integer radius = StyleUtil.getInteger(compName, "radius", null);
        final Integer titleSize = StyleUtil.getInteger(compName, "titleSize", null);

        d.put(compName + ".border", new BorderUIResource(new OLoweredBorder(insets, titleSize, radius)));
        Font tBFont = this.getDefaultFont();
        tBFont = tBFont.deriveFont(Font.BOLD, (float) tBFont.getSize() + 2);
        OntimizeLookAndFeel.setFont(d, compName, "font", OntimizeLAFParseUtils.fontToString(tBFont));

        OntimizeLookAndFeel.setColor(d, compName, "titleColor", "#3b3b3b");
    }

    protected void defineTableButton(final UIDefaults d) {
        final String compName = "\"TableButton\"";
        this.defineTableButton(compName, d);
    }

    protected void defineTableButton(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"TableButton\"";
        }

        TableButton.defaultPaintFocus = true;
        TableButton.defaultContentAreaFilled = true;
        TableButton.defaultCapable = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableButtonPainter");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(33, 33),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineTableButtonPanel(final UIDefaults d) {
        final String compName = "\"TableButtonPanel\"";
        this.defineTableButtonPanel(compName, d);
    }

    protected void defineTableButtonPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"TableButtonPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableButtonPanelPainter");

        OntimizeLookAndFeel.setColor(d, compName, "topBackgroundColor", "#DAE7ED");
        OntimizeLookAndFeel.setColor(d, compName, "bottomBackgroundColor", "#A2ABB0");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(0, 0, 0, 0),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OTableButtonPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTableButtonFooterPanel(final UIDefaults d) {
        final String compName = "\"TableButtonFooterPanel\"";
        this.defineTableButtonFooterPanel(compName, d);
    }

    protected void defineTableButtonFooterPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"TableButtonFooterPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTableButtonFooterPanelPainter");

        OntimizeLookAndFeel.setColor(d, compName, "topBackgroundColor", "#A2ABB0");
        OntimizeLookAndFeel.setColor(d, compName, "bottomBackgroundColor", "#DAE7ED");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(0, 0, 0, 0),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OTableButtonFooterPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineFormButtonPanel(final UIDefaults d) {
        final String compName = "\"FormButtonPanel\"";
        this.defineFormButtonPanel(compName, d);
    }

    protected void defineFormButtonPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"FormButtonPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFormButtonPanelPainter");

        OntimizeLookAndFeel.setColor(d, compName, "topBackgroundColor", "#AAB8BF");
        OntimizeLookAndFeel.setColor(d, compName, "bottomBackgroundColor", "#E6EFF2");
        OntimizeLookAndFeel.setColor(d, compName, "background", "#000000");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(5, 5, 5, 5),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OFormButtonPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineFormBodyPanel(final UIDefaults d) {
        final String compName = "\"FormBodyPanel\"";
        this.defineFormBodyPanel(compName, d);
    }

    protected void defineFormBodyPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"FormBodyPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFormBodyPanelPainter");

        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OFormBodyPanelPainter.BG_IMAGE = StyleUtil.getProperty(compName, "backgroundImage",
                "com/ontimize/plaf/images/backgroundDarkBlue.jpg");
        Form.DEFAULT_FORM_MARGIN = StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "5 5 5 5"), new Dimension(100, 30),
                false, AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OFormBodyPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineFieldButton(final UIDefaults d) {
        final String compName = "\"FieldButton\"";
        this.defineFieldButton(compName, d);
    }

    protected void defineFieldButton(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"FieldButton\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFieldButtonPainter");

        FieldButton.defaultContentAreaFilled = true;
        FieldButton.defaultPaintFocus = true;
        FieldButton.defaultCapable = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "3 3 3 3");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.25");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.25");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.25");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "3 3 3 3"), new Dimension(33, 33),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineFormButton(final UIDefaults d) {
        final String compName = "\"FormButton\"";
        this.defineFormButton(compName, d);
    }

    protected void defineFormButton(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"FormButton\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OFormButtonPainter");

        Form.defaultFormButtonSize = 26;
        FormButton.defaultContentAreaFilled = true;
        FormButton.defaultPaintFocus = true;
        FormButton.defaultCapable = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(33, 33),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineFormHeader(final UIDefaults d) {

        ImageManager.INSERT = "com/ontimize/plaf/images/formheader/insert.png";
        ImageManager.CONFIRM_INSERT = "com/ontimize/plaf/images/formheader/confirminsert.png";
        ImageManager.UPDATE = "com/ontimize/plaf/images/formheader/update.png";
        ImageManager.SEARCH = "com/ontimize/plaf/images/formheader/search.png";
        ImageManager.CONFIRM_QUERY = "com/ontimize/plaf/images/formheader/confirmquery.png";
        ImageManager.DELETE_DOCUMENT = "com/ontimize/plaf/images/formheader/delete_document.png";
        ImageManager.ADVANCE_SEARCH = "com/ontimize/plaf/images/formheader/advancesearch.png";
        ImageManager.NEXT_2 = "com/ontimize/plaf/images/formheader/next.png";
        ImageManager.PREVIOUS_2 = "com/ontimize/plaf/images/formheader/previous.png";
        ImageManager.END_2 = "com/ontimize/plaf/images/formheader/end.png";
        ImageManager.START_2 = "com/ontimize/plaf/images/formheader/start.png";
        ImageManager.PRINT = "com/ontimize/plaf/images/formheader/print.png";
        ImageManager.REFRESH_2 = "com/ontimize/plaf/images/formheader/refresh.png";
        ImageManager.ATTACH_FILE = "com/ontimize/plaf/images/formheader/attachfile.png";
        ImageManager.BUNDLE = "com/ontimize/plaf/images/formheader/bundle.png";
        ImageManager.EDIT = "com/ontimize/plaf/images/formheader/edit.png";
        ImageManager.TABLE_VIEW = "com/ontimize/plaf/images/formheader/tableview.png";
        ImageManager.CHECK = "com/ontimize/plaf/images/formheader/tableviewOK.png";
        ImageManager.DELETE_FIELDS = "com/ontimize/plaf/images/formheader/deletefields.png";
        ImageManager.FUNNEL_NEW = "com/ontimize/plaf/images/formheader/filter.png";
    }

    protected void defineFormHeaderButton(final UIDefaults d) {
        final String compName = "\"FormHeaderButton\"";
        this.defineFormHeaderButton(compName, d);
    }

    protected void defineFormHeaderButton(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"FormHeaderButton\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFormHeaderButtonPainter");

        FormHeaderButton.defaultCapable = true;
        FormHeaderButton.defaultContentAreaFilled = true;
        FormHeaderButton.defaultPaintFocus = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#FFFFFF");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#FFFFFF7F");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].textForeground", "#426A84");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(
                StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(33, 33),
                false, AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineFormHeaderPopupButton(final UIDefaults d) {
        final String compName = "\"FormHeaderPopupButton\"";
        this.defineFormHeaderPopupButton(compName, d);
    }

    protected void defineFormHeaderPopupButton(String compName, final UIDefaults d) {

        if (compName == null) {
            compName = "\"FormHeaderPopupButton\"";
        }

        FormHeaderPopupButton.defaultCapable = true;
        FormHeaderPopupButton.defaultContentAreaFilled = true;
        FormHeaderPopupButton.defaultPaintFocus = true;

        // setInsetsUIResource(d, compName, "contentMargins", "16 16 16 16");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Enabled].alphaTransparency", "0.3");
        // setFloat(d, compName, "[Focused].alphaTransparency", "0.3");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setFloat(d, compName, "[Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Disabled+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Pressed+Selected].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+Pressed+Selected].alphaTransparency", "0.5");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFormHeaderPopupButtonPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "7 7 7 7"),
                new Dimension(104, 33), false, PaintContext.CacheMode.NO_CACHING,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));

        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[Disabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_DISABLED_SELECTED, ctx));
        d.put(compName + "[Focused+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_SELECTED_FOCUSED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED, ctx));
        d.put(compName + "[Focused+MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_MOUSEOVER_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_PRESSED_SELECTED, ctx));
        d.put(compName + "[Focused+Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OToggleButtonPainter.BACKGROUND_PRESSED_SELECTED_FOCUSED, ctx));

    }

    protected void defineButtonSelection(final UIDefaults d) {
        final String compName = "\"ButtonSelection\"";
        this.defineButtonSelection(compName, d);
    }

    protected void defineButtonSelection(String compName, final UIDefaults d) {
        // ButtonSelection...
        if (compName == null) {
            compName = "\"ButtonSelection\"";
        }

        ButtonSelection.defaultButtonSelectionCapable = true;
        ButtonSelection.defaultButtonSelectionPaintFocus = true;
        ButtonSelection.defaultButtonSelectionContentAreaFilled = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OButtonSelectionPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(33, 33), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineMenuButtonSelection(final UIDefaults d) {
        final String compName = "\"MenuButtonSelection\"";
        this.defineMenuButtonSelection(compName, d);
    }

    protected void defineMenuButtonSelection(String compName, final UIDefaults d) {
        // MenuButtonSelection...
        if (compName == null) {
            compName = "\"MenuButtonSelection\"";
        }

        ButtonSelection.defaultMenuButtonSelectionCapable = true;
        ButtonSelection.defaultMenuButtonSelectionPaintFocus = true;
        ButtonSelection.defaultMenuButtonSelectionContentAreaFilled = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "10 0 10 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].background", "#366581");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setColor(d, compName, "[Disabled].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Enabled].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Focused].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[MouseOver].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Pressed].foreground", "#000000");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OMenuButtonSelectionPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "10 0 10 0"),
                new Dimension(33, 33), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineToolbarNavigatorMenuButtonSelection(final UIDefaults d) {
        final String compName = "\"ToolbarNavigatorMenuButtonSelection\"";
        this.defineToolbarNavigatorMenuButtonSelection(compName, d);
    }

    protected void defineToolbarNavigatorMenuButtonSelection(String compName, final UIDefaults d) {
        // MenuButtonSelection...
        if (compName == null) {
            compName = "\"ToolbarNavigatorMenuButtonSelection\"";
        }

        ButtonSelection.defaultMenuButtonSelectionCapable = true;
        ButtonSelection.defaultMenuButtonSelectionPaintFocus = true;
        ButtonSelection.defaultMenuButtonSelectionContentAreaFilled = true;

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "8 8 8 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "defaultButtonFollowsFocus", "false");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColor(d, compName, "background", "#FFFFFF");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#335971");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#8F9CA4");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[MouseOver].alphaTransparency", "0.5");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver].background", "#ffffff");
        OntimizeLookAndFeel.setFloat(d, compName, "[Focused+MouseOver].alphaTransparency", "0.5");

        OntimizeLookAndFeel.setColor(d, compName, "[Disabled].foreground", "#929292");
        OntimizeLookAndFeel.setColor(d, compName, "[Enabled].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Focused].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[MouseOver].foreground", "#000000");
        OntimizeLookAndFeel.setColor(d, compName, "[Pressed].foreground", "#000000");

        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OToolbarNavigatorMenuButtonSelectionPainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "8 8 8 0"),
                new Dimension(33, 33), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);

        d.put(compName + "[Default].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT, ctx));
        d.put(compName + "[Default+Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Default+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT, ctx));
        d.put(compName + "[Default+Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_DEFAULT_FOCUSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Focused].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_FOCUSED, ctx));
        d.put(compName + "[MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER, ctx));
        d.put(compName + "[Focused+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED, ctx));
        d.put(compName + "[Focused+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, AbstractOButtonPainter.BACKGROUND_PRESSED_FOCUSED, ctx));
    }

    protected void defineCollapsibleButtonPanel(final UIDefaults d) {
        final String compName = "\"CollapsibleButtonPanel\"";
        this.defineCollapsibleButtonPanel(compName, d);
    }

    protected void defineCollapsibleButtonPanel(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"CollapsibleButtonPanel\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OCollapsibleButtonPanelPainter");

        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 3 2 6");

        OntimizeLookAndFeel.setColor(d, compName, "lineBorderColor", "#90999E");
        OntimizeLookAndFeel.setColor(d, compName, "backgroundColor", "#FFFFFF");
        CollapsibleButtonPanel.backgroundColor = null;
        CollapsibleButtonPanel.lineBorderColor = null;
        CollapsibleButtonPanel.leftIconPath = StyleUtil.getIconPath(compName, "arrowLeftIcon",
                "com/ontimize/plaf/images/allleftarrow.png");
        CollapsibleButtonPanel.rightIconPath = StyleUtil.getIconPath(compName, "arrowRightIcon",
                "com/ontimize/plaf/images/allrightarrow.png");

        final Insets in = StyleUtil.getInsets(compName, "contentMargins", "2 3 1 4");
        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(in, new Dimension(20, 30),
                false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OCollapsibleButtonPanelPainter.BACKGROUND_ENABLED, ctx));

    }

    protected void defineCollapsiblePanel(final UIDefaults d) {
        final String compName = "CollapsiblePanel";
        this.defineCollapsiblePanel(compName, d);
    }

    protected void defineCollapsiblePanel(String compName, final UIDefaults d) {
        // Assigning CurveMattedDeployableBorder to collapsible panel.
        if (compName == null) {
            compName = "CollapsiblePanel";
        }
        Font cPFont = this.getDefaultFont();
        cPFont = cPFont.deriveFont(Font.BOLD, (float) cPFont.getSize() + 1);
        CurveMattedDeployableBorder.defaultFont = StyleUtil.getFont(compName, "font",
                OntimizeLAFParseUtils.fontToString(cPFont));
        CollapsiblePanel.borderClass = "com.ontimize.gui.container.CurveMattedDeployableBorder";
        CurveMattedDeployableBorder.defaultRectTitleColor = StyleUtil.getColor(compName, "titleBackground", "#2a3c48");
        CurveMattedDeployableBorder.defaultFontColor = StyleUtil.getColor(compName, "foreground", "#e1e1e1");
        CurveMattedDeployableBorder.defaultFontShadowColor = StyleUtil.getColor(compName, "foregroundShadow",
                "#22303a");
        CurveMattedDeployableBorder.defaultSolidBandColor = StyleUtil.getColor(compName, "background", "#eef4f6");
        CurveMattedDeployableBorder.defaultGradientBandInitColor = StyleUtil.getColor(compName,
                "backgroundGradientInit", "#dae8ec");
        CurveMattedDeployableBorder.defaultGradientBandEndColor = StyleUtil.getColor(compName, "backgroundGradientEnd",
                "#e3ecf0");

        CurveMattedDeployableBorder.ARROW_DOWN_ICON_PATH = StyleUtil.getIconPath(compName, "arrowDownIcon",
                "com/ontimize/plaf/images/border/curvematted/arrow_down.png");
        CurveMattedDeployableBorder.ARROW_LEFT_ICON_PATH = StyleUtil.getIconPath(compName, "arrowLeftIcon",
                "com/ontimize/plaf/images/border/curvematted/arrow_left.png");
        CurveMattedDeployableBorder.ARROW_RIGHT_ICON_PATH = StyleUtil.getIconPath(compName, "arrowRightIcon",
                "com/ontimize/plaf/images/border/curvematted/arrow_right.png");
        CurveMattedDeployableBorder.ARROW_UP_ICON_PATH = StyleUtil.getIconPath(compName, "arrowUpIcon",
                "com/ontimize/plaf/images/border/curvematted/arrow_up.png");

    }

    protected void defineComponentToolBar(final UIDefaults d) {
        final String compName = "\"ComponentToolBar\"";
        this.defineComponentToolBar(compName, d);
    }

    protected void defineComponentToolBar(String compName, final UIDefaults d) {
        if (compName == null) {
            compName = "\"ComponentToolBar\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OComponentToolBarPainter");

        d.put(compName + ".topBackgroundColor", OntimizeLAFParseUtils.parseColor("#DAE7ED", Color.black));
        d.put(compName + ".downBackgroundColor", OntimizeLAFParseUtils.parseColor("#A2ABB0", Color.black));

        final PaintContext ctx = new com.ontimize.plaf.painter.AbstractRegionPainter.PaintContext(new Insets(5, 5, 5, 5),
                new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OTableButtonPanelPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineFormTitle(final UIDefaults d) {
        final String compName = "\"FormTitle\"";
        this.defineFormTitle(compName, d);
    }

    protected void defineFormTitle(String compName, final UIDefaults d) {

        if (compName == null) {
            compName = "\"FormTitle\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OFormTitlePainter");

        d.put(compName + ".contentMargins", new InsetsUIResource(1, 60, 2, 60));

        Font fTFont = this.getDefaultFont();
        fTFont = fTFont.deriveFont(Font.BOLD, (float) fTFont.getSize() + 5);
        d.put(compName + ".font", StyleUtil.getFont(compName, "font", OntimizeLAFParseUtils.fontToString(fTFont)));
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#E9ECEE");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForegroundShadow", "#616C73");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#91A2AC");
        OntimizeLookAndFeel.setPaint(d, compName, "bgpaint", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "shadow", "#00000033");

        final PaintContext ctx = new PaintContext(new Insets(5, 60, 5, 60), new Dimension(100, 30), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + ".backgroundPainter",
                this.createLazyPainter(pClass, OFormTitlePainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + ".borderPainter", this.createLazyPainter(pClass, OFormTitlePainter.BORDER_ENABLED, ctx));

    }

    protected void defineTabbedPane(final UIDefaults d) {
        final String compName = "TabbedPane";
        this.defineTabbedPane(compName, d);
    }

    protected void defineTabbedPane(String compName, final UIDefaults d) {
        // TabbedPane:
        if (compName == null) {
            compName = "TabbedPane";
        }

        d.put(compName + ".States", "Enabled,Disabled");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "8 12 12 12");
        final FontUIResource font = StyleUtil.getFontUIResource(compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        d.put(compName + ".font", font);
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInteger(d, compName, "textIconGap", "3");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#517286");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "darkShadow", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "highlight", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "shadow", null);
        OntimizeLookAndFeel.setBoolean(d, compName, "isTabRollover", null);
        OntimizeLookAndFeel.setInteger(d, compName, "tabRunOverlay", "2");
        OntimizeLookAndFeel.setInteger(d, compName, "tabOverlap", "-1");
        OntimizeLookAndFeel.setBoolean(d, compName, "extendTabsToBase", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "tabAreaStatesMatchSelectedTab", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "nudgeSelectedLabel", "false");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#000000");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].border", "#000000");

        final String pClass = StyleUtil.getProperty(compName, "painterClass", "com.ontimize.plaf.painter.OTabbedPanePainter");
        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "8 12 12 12"),
                new Dimension(68, 10), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].borderPainter",
                this.createLazyPainter(pClass, OTabbedPanePainter.BORDER_ENABLED, ctx));
        d.put(compName + "[Disabled].borderPainter",
                this.createLazyPainter(pClass, OTabbedPanePainter.BORDER_DISABLED, ctx));

    }

    protected void defineTabbedPaneTab(final UIDefaults d) {
        final String compName = "TabbedPane:TabbedPaneTab";
        this.defineTabbedPaneTab(compName, d);
    }

    protected void defineTabbedPaneTab(String compName, final UIDefaults d) {

        // Tab...
        if (compName == null) {
            compName = "TabbedPane:TabbedPaneTab";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTabbedPaneTabPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "1 1 1 1");
        // It must be the same font that TabbedPane!
        final FontUIResource font = StyleUtil.getFontUIResource("TabbedPane", "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        d.put("TabbedPane:TabbedPaneTab.font", font);

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#263945");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#E4E4E4");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+MouseOver].background", "#CCCCCC");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Pressed].background", "#77ACD0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#969396");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Selected].background", "#969396");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Selected].background", "#628ca9");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed+Selected].background", "#77acd0");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "1 1 1 1"),
                new Dimension(44, 21), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_ENABLED_MOUSEOVER, ctx));
        d.put(compName + "[Enabled+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_ENABLED_PRESSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_DISABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER, ctx));
        d.put(compName + "[Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED, ctx));
        d.put(compName + "[Focused+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Focused+MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Focused+Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED_FOCUSED, ctx));
    }

    protected void defineTabbedPaneTabArea(final UIDefaults d) {
        final String compName = "TabbedPane:TabbedPaneTabArea";
        this.defineTabbedPaneTabArea(compName, d);
    }

    protected void defineTabbedPaneTabArea(String compName, final UIDefaults d) {
        // TabArea...
        if (compName == null) {
            compName = "TabbedPane:TabbedPaneTabArea";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTabbedPaneTabAreaPainter");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"),
                new Dimension(5, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabAreaPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_MOUSEOVER, ctx));
        d.put(compName + "[Enabled+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabAreaPainter.BACKGROUND_ENABLED_PRESSED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneTabAreaPainter.BACKGROUND_ENABLED, ctx));
    }

    protected void defineTabbedPaneContent(final UIDefaults d) {
        final String compName = "TabbedPane:TabbedPaneContent";
        this.defineTabbedPaneContent(compName, d);
    }

    protected void defineTabbedPaneContent(String compName, final UIDefaults d) {
        // Tab Content...
        if (compName == null) {
            compName = "TabbedPane:TabbedPaneContent";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTabbedPaneContentPainter");

        d.put(compName + ".States", "Enabled,Disabled");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "5 2 5 2");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#517286");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#517286");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "5 2 5 2"),
                new Dimension(68, 10), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneContentPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneContentPainter.BACKGROUND_DISABLED, ctx));
    }

    protected void defineTabbedPaneTabAreaButton(final UIDefaults d) {
        final String compName = "TabbedPane:TabbedPaneTabArea:\"TabbedPaneTabArea.button\"";
        this.defineTabbedPaneTabAreaButton(compName, d);
    }

    protected void defineTabbedPaneTabAreaButton(String compName, final UIDefaults d) {

        // Tab Buttons...
        if (compName == null) {
            compName = "TabbedPane:TabbedPaneTabArea:\"TabbedPaneTabArea.button\"";
        }
        final String pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OTabbedPaneArrowButtonPainter");

        d.put(compName + ".States", "Enabled,Pressed,Disabled,MouseOver");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].foreground", "#BDBDBD");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].foreground", "#D0DAE2");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].foreground", "#9D9D9D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].foreground", "#517286");

        final PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "1 1 1 1"),
                new Dimension(68, 10), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_ENABLED, ctx));
        d.put(compName + "[Pressed].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_PRESSED, ctx));
        d.put(compName + "[MouseOver].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_MOUSEOVER, ctx));

    }

    protected void defineFormTabbedPane(final UIDefaults d) {

        // TabbedPane:
        final String name = "FormTabbedPane";
        String compName = name;

        d.put(compName + ".States", "Enabled,Disabled,Selected,Focused");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setFontUIResource(d, compName, "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        OntimizeLookAndFeel.setBoolean(d, compName, "opaque", "false");
        OntimizeLookAndFeel.setInteger(d, compName, "textIconGap", "3");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "background", "#517286");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "darkShadow", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabled", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "disabledText", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "highlight", null);
        OntimizeLookAndFeel.setColorUIResource(d, compName, "shadow", null);

        OntimizeLookAndFeel.setBoolean(d, compName, "isTabRollover", null);
        OntimizeLookAndFeel.setInteger(d, compName, "tabRunOverlay", "2");
        OntimizeLookAndFeel.setInteger(d, compName, "tabOverlap", "-1");
        OntimizeLookAndFeel.setBoolean(d, compName, "extendTabsToBase", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "useBasicArrows", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "tabAreaStatesMatchSelectedTab", "true");
        OntimizeLookAndFeel.setBoolean(d, compName, "nudgeSelectedLabel", "false");

        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].border", "#000000");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].border", "#000000");

        // Tab...
        compName = name + ":TabbedPaneTab";
        String pClass = OFormTabbedPaneTabPainter.class.getName();

        // It must be the same font that TabbedPane!
        final FontUIResource font = StyleUtil.getFontUIResource("FormTabbedPane", "font",
                OntimizeLAFParseUtils.fontToString(this.getDefaultFont()));
        d.put("FormTabbedPane:TabbedPaneTab.font", font);
        d.put(compName + ".States", "Enabled,Disabled,Selected,Focused");

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "2 8 3 8");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].background", "#E4E4E4");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+MouseOver].background", "#CCCCCC");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled+Pressed].background", "#77ACD0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].background", "#969396");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled+Selected].background", "#969396");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+MouseOver+Selected].background", "#77acd0");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused+Pressed+Selected].background", "#77acd0");

        PaintContext ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "7 7 1 7"),
                new Dimension(44, 20), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY);
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled+MouseOver].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_ENABLED_MOUSEOVER, ctx));
        d.put(compName + "[Enabled+Pressed].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_ENABLED_PRESSED, ctx));
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Disabled+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_DISABLED, ctx));
        d.put(compName + "[Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED, ctx));
        d.put(compName + "[MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER, ctx));
        d.put(compName + "[Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED, ctx));
        d.put(compName + "[Focused+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_FOCUSED, ctx));
        d.put(compName + "[Focused+MouseOver+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_MOUSEOVER_FOCUSED, ctx));
        d.put(compName + "[Focused+Pressed+Selected].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabPainter.BACKGROUND_SELECTED_PRESSED_FOCUSED,
                        ctx));

        // Tab Label
        compName = "\"FormTabbedPaneTab.Label\"";
        d.put(compName + ".States", "Enabled,Disabled,Focused,Selected");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "foreground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].textForeground", "#263945");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Selected].textForeground", "#ffffff");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Focused].textForeground", "#ffffff");

        // *********************************************************************
        // TabArea...
        compName = name + ":TabbedPaneTabArea";
        pClass = OFormTabbedPaneTabAreaPainter.class.getName();

        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "0 0 0 0");
        OntimizeLookAndFeel.setPaint(d, compName, "[Enabled].background", "#ffffff22");
        OntimizeLookAndFeel.setPaint(d, compName, "[Disabled].background", "#aaaaaa33");

        ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "0 0 0 0"), new Dimension(5, 24), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabAreaPainter.BACKGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, OFormTabbedPaneTabAreaPainter.BACKGROUND_ENABLED, ctx));

        // *********************************************************************
        // Content...
        compName = name + ":TabbedPaneContent";
        d.put(compName + ".States", "Enabled,Disabled");
        OntimizeLookAndFeel.setInsetsUIResource(d, compName, "contentMargins", "1 1 1 1");

        // Tab Buttons...
        compName = name + ":TabbedPaneTabArea:\"FormTabbedPaneTabArea.button\"";
        pClass = StyleUtil.getProperty(compName, "painterClass",
                "com.ontimize.plaf.painter.OFormTabbedPaneArrowButtonPainter");

        d.put(compName + ".States", "Enabled,Pressed,Disabled,MouseOver");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Enabled].foreground", "#BDBDBD");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Disabled].foreground", "#D0DAE2");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[Pressed].foreground", "#9D9D9D");
        OntimizeLookAndFeel.setColorUIResource(d, compName, "[MouseOver].foreground", "#517286");

        ctx = new PaintContext(StyleUtil.getInsets(compName, "contentMargins", "1 1 1 1"), new Dimension(68, 10), false,
                AbstractRegionPainter.PaintContext.CacheMode.FIXED_SIZES,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        d.put(compName + "[Disabled].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_DISABLED, ctx));
        d.put(compName + "[Enabled].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_ENABLED, ctx));
        d.put(compName + "[Pressed].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_PRESSED, ctx));
        d.put(compName + "[MouseOver].foregroundPainter",
                this.createLazyPainter(pClass, OTabbedPaneArrowButtonPainter.FOREGROUND_MOUSEOVER, ctx));
    }

    protected void defineOntimizeComponents(final UIDefaults d) {
        ApplicationManager.useOntimizePlaf = true;

        DataField.DEFAULT_BOTTOM_MARGIN = 0;
        DataField.DEFAULT_TOP_MARGIN = 0;
        DataField.DEFAULT_PARENT_MARGIN = 0;
        DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL = 0;
        DataField.DEFAULT_FIELD_LEFT_MARGIN = 0;
        DataField.DEFAULT_FIELD_RIGHT_MARGIN = 0;

        Row.defaultFLayoutHGap = 1;
        Row.defaultFLayoutVGap = 0;

        Form.toUppercaseTitle = true;
        Form.defaultBorderButtons = false;

        // Set the height in ReferenceExt table y Form Table View.
        Form.defaultTableViewMinRowHeight = 22;

        Tree.enabledRowCount = true;

        BasicTreeCellRenderer.firstNodeConfiguration = true;
        BasicTreeCellRenderer.organizationalForegroundColor = d.getColor("Tree:\"Tree.cellRenderer\".textForeground");
        BasicTreeCellRenderer.includeChildCount = false;

        BasicTreeCellRenderer.rootNodeForegroundColor = d.getColor("Tree:\"Tree.cellRenderer\".rootForeground");
        BasicTreeCellRenderer.rootNodeSelectionForegroundColor = d
            .getColor("Tree:\"Tree.cellRenderer\".rootSelectionForeground");

        BooleanCellRenderer.USE_CHECKBOX = true;

        FormHeaderButton.createRolloverIcon = true;
        com.ontimize.util.swing.RolloverButton.createRolloverIcon = true;
        FormHeaderPopupButton.createRolloverIcon = true;

        // ImageDataField
        final Color borderColor = StyleUtil.getColorUI("Image", "border", "#ADC0CE");
        final BorderUIResource iBorder = new BorderUIResource(BorderFactory.createLineBorder(borderColor, 2));
        BorderManager.putBorder(BorderManager.DEFAULT_IMAGE_BORDER_KEY, iBorder);

        // VisualCalendar component...
        ImageManager.CALENDAR_PREV = ImageManager.ARROW_LEFT;
        ImageManager.CALENDAR_NEXT = ImageManager.ARROW_RIGHT;
        VisualCalendarComponent.fieldsBorder = null;
        VisualCalendarComponent.HeaderRenderer.headerFont = new Font("Arial", Font.BOLD, 17);
        VisualCalendarComponent.DayRenderer.headerFont = new Font("Arial", Font.PLAIN, 13);

        // PopupList
        final Border border = new EmptyBorder(StyleUtil.getInsets("MenuItem", "contentMargins", "0 10 0 10"));
        AttachmentListPopup.itemsBorder = border;
        AttachmentComponent.componentBorder = border;

        // ApToolbarNavigator
        ApToolBarNavigator.defaultPrevIconPath = "com/ontimize/plaf/images/toolbar/prev.png";
        ApToolBarNavigator.defaultNextIconPath = "com/ontimize/plaf/images/toolbar/next.png";
        ApToolBarNavigator.defaultListIconPath = "com/ontimize/plaf/images/toolbar/home.png";
        ApToolBarNavigator.defaultOpaque = false;
        ApToolBarNavigator.defaultBorderButtons = false;

        try {
			com.ontimize.gui.login.ShapeLoginDialog.opaque = false;
        } catch (final Throwable e) {

        }

        // SumRowSetupDialog
        SumRowSetupDialog.defaultSelectedItemBgColor = new Color(0x36627F);
        SumRowSetupDialog.defaultItemFgColor = new Color(0x335971);

        // Popup_arrow
        ApToolBarPopupButton.popupArrowIcon = "com/ontimize/plaf/images/popuparrow_white.png";
    }

    protected void defineHTMLDataField(final UIDefaults d) {
        HTMLDataField.toolBarFiller = true;
        DataField.DEFAULT_PARENT_MARGIN_FOR_SCROLL = 0;
        DataField.DEFAULT_PARENT_MARGIN = 0;
        ImageManager.BOLD_FONT = "com/ontimize/plaf/images/html/text_bold.png";
        ImageManager.ITALIC_FONT = "com/ontimize/plaf/images/html/text_italic.png";
        ImageManager.UNDERLINE_FONT = "com/ontimize/plaf/images/html/text_underline.png";
        ImageManager.LEFT_ALIGN = "com/ontimize/plaf/images/html/text_align_left.png";
        ImageManager.CENTER_ALIGN = "com/ontimize/plaf/images/html/text_align_center.png";
        ImageManager.RIGHT_ALIGN = "com/ontimize/plaf/images/html/text_align_right.png";
        ImageManager.JUSTIFY_ALIGN = "com/ontimize/plaf/images/html/text_align_justify.png";
        ImageManager.CHOOSE_COLOR = "com/ontimize/plaf/images/html/color.png";
        ImageManager.LIST_ORDERED = "com/ontimize/plaf/images/html/listordered.png";
        ImageManager.LIST_UNORDERED = "com/ontimize/plaf/images/html/listunordered.png";
        ImageManager.IMAGE = "com/ontimize/plaf/images/html/html_image.png";
        ImageManager.HTML_TABLE = "com/ontimize/plaf/images/html/html_table.png";

        try {
            com.ontimize.gui.field.HTMLShefDataField.defaultHTMLToolbarButtonHeight = 32;
        } catch (final Throwable e) {
        }
    }

    protected void defineODMSComponents(final UIDefaults d) {
        try {
            System.setProperty("com.ontimize.dms.client.gui.COLOR_CONFIGURATION_FILE",
                    "com/ontimize/plaf/odms/color.properties");
        } catch (final Throwable e) {
        }
    }

    /**
     * Initialize the root pane settings.
     * @param d the UI defaults map.
     */
    protected void defineRootPanes(final UIDefaults d) {

        this.decorated = true;

        // ***********************
        // FROM Ontimize
        // ***********************
        final String compName = "RootPane";
        final String pClass = "com.ontimize.plaf.painter.ORootPainter";

        d.put(compName + ".States", "Enabled,WindowFocused,NoFrame");
        d.put(compName + ".contentMargins", new InsetsUIResource(0, 0, 0, 0));
        d.put(compName + ".opaque", Boolean.FALSE);
        d.put(compName + ".NoFrame", new ORootPaneNoFrameState());
        d.put(compName + ".WindowFocused", new ORootPaneWindowFocusedState());

        final PaintContext ctx = new PaintContext(new Insets(0, 0, 0, 0), new Dimension(100, 25), false,
                AbstractRegionPainter.PaintContext.CacheMode.NO_CACHING, 1.0, 1.0);
        d.put(compName + "[Enabled+NoFrame].backgroundPainter",
                this.createLazyPainter(pClass, ORootPainter.BACKGROUND_ENABLED_NOFRAME, ctx));
        d.put(compName + "[Enabled].backgroundPainter",
                this.createLazyPainter(pClass, ORootPainter.BACKGROUND_ENABLED, ctx));
        d.put(compName + "[Enabled+WindowFocused].backgroundPainter",
                this.createLazyPainter(pClass, ORootPainter.BACKGROUND_ENABLED_WINDOWFOCUSED, ctx));

        // ***********************
        // FROM ONTIMIZE OntimizeRootPaneUI
        // ***********************
        // if(UIManager.getBoolean("Ontimize.window.decoration")==true)
        // if (decorated)
        // uidefaults
        // .put("RootPaneUI", "com.ontimize.plaf.OntimizeRootPaneUI");
        // else
        // decorated = false;

        // uidefaults.put("RootPane.border.insets", new InsetsUIResource(2, 2,
        // 2, 2));
        // uidefaults.put("RootPane.border.background",
        // OntimizeLAFColorUtils.colorHexToColor("#E7EFF1"));
        // uidefaults.put("RootPane.border.arc", Integer.valueOf(8));
        //
        d.put("RootPane.titlePane.background", OntimizeLAFColorUtils.colorHexToColor("#05A9C4"));
        // uidefaults.put("RootPane.titlePane.height", Integer.valueOf(25));
        // uidefaults.put("RootPane.titlePane.line.background",
        // OntimizeLAFColorUtils.colorHexToColor("#FFFFFF", 102));
        // uidefaults.put("RootPane.titlePane.arc", Integer.valueOf(4));

    }

    /**
     * This method is override in order to populate the uidefaults object with the configuration
     * information provided by the CSS file or the default one. For that fact, information is put in
     * that UIDefaults Object by using the put(key, value) method.
     *
     */
    @Override
    public UIDefaults getDefaults() {
        if (!this.initialized) {
            this.initialized = true;
            final UIDefaults uidefaults = super.getDefaults();

            // Define customized UI's...
            this.defineUI(uidefaults, "ComboBox");

            this.defineUI(uidefaults, "Label");

            this.defineUI(uidefaults, "List");

            // defineUI(uidefaults, "Panel");

            this.defineUI(uidefaults, "PasswordField");

            // defineUI(uidefaults, "RootPane");

            this.defineUI(uidefaults, "ScrollBar");

            this.defineUI(uidefaults, "ScrollPane");

            this.defineUI(uidefaults, "TabbedPane");

            this.defineUI(uidefaults, "FormTabbedPane");

            this.defineUI(uidefaults, "Table");

            this.defineUI(uidefaults, "TableHeader");

            this.defineUI(uidefaults, "TextField");

            this.defineUI(uidefaults, "TextArea");

            this.defineUI(uidefaults, "ToolBar");

            this.defineUI(uidefaults, "ToolTip");

            this.defineUI(uidefaults, "Tree");

            this.defineUI(uidefaults, "Viewport");

            // Set the default font.
            this.defineDefaultFont(uidefaults);

            // Define painter components configuration...
            this.defineButton(uidefaults);

            this.defineCheckBox(uidefaults);

            this.defineCheckBoxMenuItem(uidefaults);

            this.defineComboBox(uidefaults);

            this.defineComboBoxArrow(uidefaults);

            this.defineComboBoxTextField(uidefaults);

            this.defineComboBoxListRenderer(uidefaults);

            this.defineComboBoxRenderer(uidefaults);

            this.defineComboBoxScrollPane(uidefaults);

            this.defineEditorPane(uidefaults);

            this.defineFileChooser(uidefaults);

            this.defineLabel(uidefaults);

            this.defineList(uidefaults);

            this.defineMenu(uidefaults);

            this.defineMenuBar(uidefaults);

            this.defineMenuBarMenu(uidefaults);

            this.defineMenuItem(uidefaults);

            this.defineOptionPane(uidefaults);

            this.definePanel(uidefaults);

            this.definePassword(uidefaults);

            this.definePopupMenu(uidefaults);

            this.definePopupItem(uidefaults);

            this.definePopupMenuSeparator(uidefaults);

            this.defineProgressBar(uidefaults);

            this.defineRadioButtonMenuItem(uidefaults);

            // defineRootPanes(uidefaults);

            this.defineScrollBar(uidefaults);

            this.defineScrollBarButton(uidefaults);

            this.defineScrollBarThumb(uidefaults);

            this.defineScrollBarTrack(uidefaults);

            this.defineScrollPane(uidefaults);

            this.defineSeparator(uidefaults);

            this.defineSliders(uidefaults);

            this.defineSplitPane(uidefaults);

            this.defineSplitPaneDivider(uidefaults);

            this.defineTabbedPane(uidefaults);

            this.defineTabbedPaneTab(uidefaults);

            this.defineTabbedPaneTabArea(uidefaults);

            this.defineTabbedPaneContent(uidefaults);

            this.defineTabbedPaneTabAreaButton(uidefaults);

            this.defineTextFields(uidefaults);

            this.defineTextArea(uidefaults);

            this.defineTextPane(uidefaults);

            this.defineToggleButton(uidefaults);

            this.defineToolBar(uidefaults);

            this.defineToolBarButton(uidefaults);

            this.defineToolBarToggleButton(uidefaults);

            this.defineToolBarSeparator(uidefaults);

            this.defineToolTip(uidefaults);

            this.defineTree(uidefaults);

            this.defineTreeCellRenderer(uidefaults);

            this.defineTreeCellEditor(uidefaults);

            // defineViewport(uidefaults);

            // Ontimize components...
            this.defineCardPanel(uidefaults);
            this.defineCollapsibleButtonPanel(uidefaults);
            this.defineCollapsiblePanel(uidefaults);
            this.defineColumn(uidefaults);
            this.defineELabel(uidefaults);
            this.defineFieldButton(uidefaults);
            this.defineFormTabbedPane(uidefaults);
            this.defineFormTitle(uidefaults);
            this.defineFormButton(uidefaults);
            this.defineFormButtonPanel(uidefaults);
            this.defineFormBodyPanel(uidefaults);
            this.defineFormScrollPanel(uidefaults);
            this.defineTreeScrollPanel(uidefaults);
            this.defineFormHeader(uidefaults);
            this.defineFormHeaderButton(uidefaults);
            this.defineFormHeaderPopupButton(uidefaults);
            this.defineHTMLDataField(uidefaults);
            this.defineButtonSelection(uidefaults);
            this.defineMenuButtonSelection(uidefaults);
            this.defineToolbarNavigatorMenuButtonSelection(uidefaults);
            this.defineRadioButton(uidefaults);
            this.defineQuickFilter(uidefaults);
            this.defineRow(uidefaults);
            this.defineSelectableItem(uidefaults);
            this.defineTable(uidefaults);
            this.defineTableButton(uidefaults);
            this.defineTableButtonPanel(uidefaults);
            this.defineTableButtonFooterPanel(uidefaults);
            this.defineTableCellRenderer(uidefaults);
            this.defineTableRowHeadCellRenderer(uidefaults);
            this.defineTableSumCellRenderer(uidefaults);
            this.defineTableVisualCalendarCellRenderer(uidefaults);
            this.defineTableCellEditor(uidefaults);
            this.defineTableHeader(uidefaults);
            this.defineTableHeaderRenderer(uidefaults);
            this.defineVisualCalendarTableHeaderRenderer(uidefaults);
            this.defineTitleBorder(uidefaults);
            this.defineOntimizeComponents(uidefaults);
            this.defineODMSComponents(uidefaults);
            this.defineGrid(uidefaults);
            this.defineComponentToolBar(uidefaults);
            this.defineReferenceExtComponent(uidefaults);
            this.defineReferenceExtCodeComponent(uidefaults);
            this.defineResultCountLabel(uidefaults);

            uidefaults.put("\"FrameButton\".backgroundPainter",
                    this.createLazyPainter("com.ontimize.plaf.painter.ButtonPainter", ButtonPainter.BACKGROUND_DEFAULT,
                            "com/ontimize/plaf/images/closeIcon.png"));

            uidefaults.put("InternalFrame.maximizeIcon", this.createFrameMaximizeIcon());
            uidefaults.put("InternalFrame.minimizeIcon", this.createFrameIconifyIcon());
            uidefaults.put("InternalFrame.iconifyIcon", this.createFrameIconifyIcon());
            uidefaults.put("InternalFrame.closeIcon", this.createFrameCloseIcon());

            JFrame.setDefaultLookAndFeelDecorated(this.decorated);
            JDialog.setDefaultLookAndFeelDecorated(this.decorated);

        }
        return super.getDefaults();
    }

    protected void defineUI(final UIDefaults d, String uiName) {
        uiName = uiName + "UI";
        d.put(uiName, OntimizeLookAndFeel.UI_PACKAGE_PREFIX + uiName);
    }

    /**
     * Registers the given region and prefix. The prefix, if it contains quoted sections, refers to
     * certain named components. If there are not quoted sections, then the prefix refers to a generic
     * component type.
     *
     * <p>
     * If the given region/prefix combo has already been registered, then it will not be registered
     * twice. The second registration attempt will fail silently.
     * </p>register
     * @param region The Synth Region that is being registered. Such as Button, or ScrollBarThumb.
     * @param prefix The UIDefault prefix. For example, could be ComboBox, or if a named components,
     *        "MyComboBox", or even something like ToolBar:"MyComboBox":"ComboBox.arrowButton"
     */
    @Override
    public void register(final Region region, final String prefix) {
        super.register(region, prefix);
    }

    /**
     * Locate the style associated with the given region and component. This is called from
     * OntimizeLookAndFeel in the SynthStyleFactory implementation.
     *
     * <p>
     * Lookup occurs as follows:<br/>
     * Check the map of styles <code>styleMap</code>. If the map contains no styles at all, then simply
     * return the defaultStyle. If the map contains styles, then iterate over all of the styles for the
     * Region <code>r</code> looking for the best match, based on prefix. If a match was made, then
     * return that SynthStyle. Otherwise, return the defaultStyle.
     * </p>
     * @param c The component associated with this region. For example, if the Region is Region.Button
     *        then the component will be a JButton. If the Region is a subregion, such as
     *        ScrollBarThumb, then the associated component will be the component that subregion belongs
     *        to, such as JScrollBar. The JComponent may be named. It may not be null.
     * @param r The region we are looking for a style for. May not be null.
     * @return the style associated with the given region and component.
     */
    public static SynthStyle getOntimizeStyle(final JComponent c, final Region r) {
        return SynthLookAndFeel.getStyle(c, r);
    }

    /**
     * A convience method that will reset the Style of StyleContext if necessary.
     * @param context the SynthContext corresponding to the current state.
     * @param ui the UI delegate.
     * @return the new, updated style.
     */
    public static SynthStyle updateStyle(final SynthContext context, final SynthUI ui) {
        final SynthStyle newStyle = SynthLookAndFeel.getStyle(context.getComponent(), context.getRegion());
        final SynthStyle oldStyle = context.getStyle();

        if (newStyle != oldStyle) {

            if (oldStyle != null) {
                oldStyle.uninstallDefaults(context);
            }

            ReflectionUtils.invoke(context, "setStyle", newStyle);
            if (newStyle instanceof OntimizeStyle) {
                ((OntimizeStyle<?>) newStyle).installDefaults(context, ui);
            } else {
                newStyle.installDefaults(context);
            }
        }

        return newStyle;
    }

    /**
     * Returns the component state for the specified component. This should only be used for Components
     * that don't have any special state beyond that of ENABLED, DISABLED or FOCUSED. For example,
     * buttons shouldn't call into this method.
     */
    public static int getComponentState(final Component c) {
        if (c.isEnabled()) {
            if (c.isFocusOwner()) {
                return SynthConstants.ENABLED | SynthConstants.FOCUSED;
            }
            return SynthConstants.ENABLED;
        }
        return SynthConstants.DISABLED;
    }

    /**
     * Returns the Region for the JComponent <code>c</code>.
     * @param c JComponent to fetch the Region for
     * @return Region corresponding to <code>c</code>
     */
    public static Region getRegion(final JComponent c) {
        return OntimizeRegion.getRegion(c);
    }

    /**
     * A convenience method that handles painting of the background. All SynthUI implementations should
     * override update and invoke this method.
     * @param state the SynthContext describing the current component and state.
     * @param g the Graphics context to use to paint the component.
     */
    public static void update(final SynthContext state, final Graphics g) {
        OntimizeLookAndFeel.paintRegion(state, g, null);
    }

    /**
     * A convenience method that handles painting of the background for subregions. All SynthUI's that
     * have subregions should invoke this method, than paint the foreground.
     * @param state the SynthContext describing the component, region, and state.
     * @param g the Graphics context used to paint the subregion.
     * @param bounds the bounds to paint in.
     */
    public static void updateSubregion(final SynthContext state, final Graphics g, final Rectangle bounds) {
        OntimizeLookAndFeel.paintRegion(state, g, bounds);
    }

    /**
     * Paint a region.
     * @param state the SynthContext describing the current component, region, and state.
     * @param g the Graphics context used to paint the subregion.
     * @param bounds the bounds to paint in.
     */
    protected static void paintRegion(final SynthContext state, final Graphics g, final Rectangle bounds) {
        final JComponent c = state.getComponent();
        final SynthStyle style = state.getStyle();
        int x;
        int y;
        int width;
        int height;

        if (bounds == null) {
            x = 0;
            y = 0;
            width = c.getWidth();
            height = c.getHeight();
        } else {
            x = bounds.x;
            y = bounds.y;
            width = bounds.width;
            height = bounds.height;
        }

        // Fill in the background, if necessary.
        final boolean subregion = state.getRegion().isSubregion();

        // TODO Review if fillRect is necessary.
        if ((subregion && style.isOpaque(state)) || (!subregion && c.isOpaque())) {

            if (!(c instanceof JTextField)) {
                g.setColor(style.getColor(state, ColorType.BACKGROUND));
                g.fillRect(x, y, width, height);
            }
            if ((c instanceof JTextField) && "Tree.cellEditor".equals(c.getName())) {
                g.setColor(style.getColor(state, ColorType.BACKGROUND));
                g.fillRect(x, y, width, height);
            }
        }
    }

    /**
     * Returns true if the Style should be updated in response to the specified PropertyChangeEvent.
     * This forwards to <code>
     * shouldUpdateStyleOnAncestorChanged</code> as necessary.
     * @param event the property change event.
     * @return {@code true} if the style should be updated as a result of this property change,
     *         {@code false} otherwise.
     */
    public static boolean shouldUpdateStyle(final PropertyChangeEvent event) {
        final String eName = event.getPropertyName();

        if ("name" == eName) {

            // Always update on a name change
            return true;
        } else if ("componentOrientation" == eName) {

            // Always update on a component orientation change
            return true;
        } else if (("ancestor" == eName) && (event.getNewValue() != null)) {

            // Only update on an ancestor change when getting a valid
            // parent and the LookAndFeel wants this.
            final LookAndFeel laf = UIManager.getLookAndFeel();

            return ((laf instanceof SynthLookAndFeel) && ((SynthLookAndFeel) laf).shouldUpdateStyleOnAncestorChanged());
        }
        /*
         * Note: The following two Ontimize based overrides should be refactored to be in the Ontimize LAF.
         * Due to constraints in an update release, we couldn't actually provide the public API necessary to
         * allow OntimizeLookAndFeel (a subclass of SynthLookAndFeel) to provide its own rules for
         * shouldUpdateStyle.
         */
        else if ("Ontimize.Overrides" == eName) {

            // Always update when the Ontimize.Overrides client property has
            // been changed
            return true;
        } else if ("Ontimize.Overrides.InheritDefaults" == eName) {

            // Always update when the Ontimize.Overrides.InheritDefaults
            // client property has changed
            return true;
        } else if ("JComponent.sizeVariant" == eName) {

            // Always update when the JComponent.sizeVariant
            // client property has changed
            return true;
        } else if ((eName != null) && (eName.startsWith("JButton.") || eName.startsWith("JTextField."))) {

            // Always update when an Apple-style variant client property has
            // changed.
            return true;
        }

        return false;
    }

    /**
     * Called by UIManager when this look and feel is installed.
     */
    @Override
    public void initialize() {
        super.initialize();
        final SynthStyleFactory styleFactory = SynthLookAndFeel.getStyleFactory();
        // create synth style factory
        SynthLookAndFeel.setStyleFactory(new SynthStyleFactory() {
            @Override
            public SynthStyle getStyle(final JComponent c, final Region r) {
                SynthStyle style = styleFactory.getStyle(c, r);

                if (!(style instanceof OntimizeStyle)) {
                    style = new OntimizeStyleWrapper(style);
                }

                return style;
            }
        });

        // Create popup Factory.
        PopupFactory.setSharedInstance(new OntimizePopupFactory());
    }

    /**
     * Called by UIManager when this look and feel is uninstalled.
     */
    @Override
    public void uninitialize() {
        super.uninitialize();
        this.initialized = false;
        com.ontimize.plaf.utils.ImageCache.getInstance().flush();
        SynthLookAndFeel.setStyleFactory(null);
    }

    /**
     * Initialize the map of styles.
     */
    protected void registerStyles() {
        this.register(Region.ARROW_BUTTON, "ArrowButton");
        this.register(Region.BUTTON, "Button");
        this.register(Region.TOGGLE_BUTTON, "ToggleButton");
        this.register(Region.RADIO_BUTTON, "RadioButton");
        this.register(Region.CHECK_BOX, "CheckBox");
        this.register(Region.COLOR_CHOOSER, "ColorChooser");
        this.register(Region.PANEL, "ColorChooser:\"ColorChooser.previewPanelHolder\"");
        this.register(Region.LABEL, "ColorChooser:\"ColorChooser.previewPanelHolder\":\"OptionPane.label\"");
        this.register(Region.COMBO_BOX, "ComboBox");
        this.register(Region.TEXT_FIELD, "ComboBox:\"ComboBox.textField\"");
        this.register(Region.ARROW_BUTTON, "ComboBox:\"ComboBox.arrowButton\"");
        this.register(Region.LABEL, "ComboBox:\"ComboBox.listRenderer\"");
        this.register(Region.LABEL, "ComboBox:\"ComboBox.renderer\"");
        this.register(Region.SCROLL_PANE, "\"ComboBox.scrollPane\"");
        this.register(Region.FILE_CHOOSER, "FileChooser");
        this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrameTitlePane");
        this.register(Region.INTERNAL_FRAME, "InternalFrame");
        this.register(Region.INTERNAL_FRAME_TITLE_PANE, "InternalFrame:InternalFrameTitlePane");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.menuButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.iconifyButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.maximizeButton\"");
        this.register(Region.BUTTON, "InternalFrame:InternalFrameTitlePane:\"InternalFrameTitlePane.closeButton\"");
        this.register(Region.DESKTOP_ICON, "DesktopIcon");
        this.register(Region.DESKTOP_PANE, "DesktopPane");
        this.register(Region.LABEL, "Label");
        this.register(Region.LIST, "List");
        this.register(Region.LABEL, "List:\"List.cellRenderer\"");
        this.register(Region.MENU_BAR, "MenuBar");
        this.register(Region.MENU, "MenuBar:Menu");
        this.register(Region.MENU_ITEM_ACCELERATOR, "MenuBar:Menu:MenuItemAccelerator");
        this.register(Region.MENU_ITEM, "MenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "MenuItem:MenuItemAccelerator");
        this.register(Region.RADIO_BUTTON_MENU_ITEM, "RadioButtonMenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "RadioButtonMenuItem:MenuItemAccelerator");
        this.register(Region.CHECK_BOX_MENU_ITEM, "CheckBoxMenuItem");
        this.register(Region.MENU_ITEM_ACCELERATOR, "CheckBoxMenuItem:MenuItemAccelerator");
        this.register(Region.MENU, "Menu");
        this.register(Region.MENU_ITEM_ACCELERATOR, "Menu:MenuItemAccelerator");
        this.register(Region.POPUP_MENU, "PopupMenu");
        this.register(Region.POPUP_MENU_SEPARATOR, "PopupMenuSeparator");
        this.register(Region.OPTION_PANE, "OptionPane");
        this.register(Region.SEPARATOR, "OptionPane:\"OptionPane.separator\"");
        this.register(Region.PANEL, "OptionPane:\"OptionPane.messageArea\"");
        this.register(Region.LABEL, "OptionPane:\"OptionPane.messageArea\":\"OptionPane.label\"");
        this.register(Region.PANEL, "Panel");
        this.register(Region.PROGRESS_BAR, "ProgressBar");
        this.register(Region.SEPARATOR, "Separator");
        this.register(Region.SCROLL_BAR, "ScrollBar");
        this.register(Region.ARROW_BUTTON, "ScrollBar:\"ScrollBar.button\"");
        this.register(Region.SCROLL_BAR_THUMB, "ScrollBar:ScrollBarThumb");
        this.register(Region.SCROLL_BAR_TRACK, "ScrollBar:ScrollBarTrack");
        this.register(Region.SCROLL_PANE, "ScrollPane");
        this.register(Region.VIEWPORT, "Viewport");
        this.register(Region.SLIDER, "Slider");
        this.register(Region.SLIDER_THUMB, "Slider:SliderThumb");
        this.register(Region.SLIDER_TRACK, "Slider:SliderTrack");
        this.register(Region.SPINNER, "Spinner");
        this.register(Region.PANEL, "Spinner:\"Spinner.editor\"");
        this.register(Region.FORMATTED_TEXT_FIELD, "Spinner:Panel:\"Spinner.formattedTextField\"");
        this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.previousButton\"");
        this.register(Region.ARROW_BUTTON, "Spinner:\"Spinner.nextButton\"");
        this.register(Region.SPLIT_PANE, "SplitPane");
        this.register(Region.SPLIT_PANE_DIVIDER, "SplitPane:SplitPaneDivider");
        this.register(Region.TABBED_PANE, "TabbedPane");
        this.register(Region.TABBED_PANE_TAB, "TabbedPane:TabbedPaneTab");
        this.register(Region.TABBED_PANE_TAB_AREA, "TabbedPane:TabbedPaneTabArea");
        this.register(Region.TABBED_PANE_CONTENT, "TabbedPane:TabbedPaneContent");

        this.register(Region.ARROW_BUTTON, "TabbedPane:TabbedPaneTabArea:\"TabbedPaneTabArea.button\"");

        this.register(Region.TABLE, "Table");
        this.register(Region.LABEL, "Table:\"Table.cellRenderer\"");
        this.register(Region.LABEL, "Table:\"VisualCalendar:Table.cellRenderer\"");
        this.register(Region.TABLE_HEADER, "TableHeader");
        this.register(Region.LABEL, "TableHeader:\"TableHeader.renderer\"");
        this.register(Region.LABEL, "TableHeader:\"VisualCalendar:TableHeader.renderer\"");
        this.register(Region.TEXT_FIELD, "\"Table.editor\"");
        this.register(Region.TEXT_FIELD, "\"Tree.cellEditor\"");
        this.register(Region.TEXT_FIELD, "TextField");
        this.register(Region.FORMATTED_TEXT_FIELD, "FormattedTextField");
        this.register(Region.PASSWORD_FIELD, "PasswordField");
        this.register(Region.TEXT_AREA, "TextArea");
        this.register(Region.TEXT_PANE, "TextPane");
        this.register(Region.EDITOR_PANE, "EditorPane");
        this.register(Region.TOOL_BAR, "ToolBar");
        this.register(Region.BUTTON, "ToolBar:Button");
        this.register(Region.TOGGLE_BUTTON, "ToolBar:ToggleButton");
        this.register(Region.TOOL_BAR_SEPARATOR, "ToolBarSeparator");
        this.register(Region.TOOL_TIP, "ToolTip");
        this.register(Region.TREE, "Tree");
        this.register(Region.TREE_CELL, "Tree:TreeCell");
        this.register(Region.ROOT_PANE, "RootPane");

        // ********************************************************************
        // Ontimize Components.
        // ***************************************************************
        this.register(Region.BUTTON, "\"FormButton\"");
        this.register(Region.BUTTON, "\"FrameButton\"");
        this.register(Region.BUTTON, "\"FormHeaderButton\"");
        this.register(Region.BUTTON, "\"TableButton\"");
        this.register(Region.BUTTON, "\"ButtonSelection\"");
        this.register(Region.BUTTON, "\"MenuButtonSelection\"");
        this.register(Region.BUTTON, "\"ToolbarNavigatorMenuButtonSelection\"");
        this.register(Region.BUTTON, "\"QueryFilterButton\"");
        this.register(Region.BUTTON, "\"FieldButton\"");

        this.register(Region.CHECK_BOX, "\"SelectableItem\"");

        this.register(Region.TOGGLE_BUTTON, "\"FormHeaderPopupButton\"");
        this.register(Region.TOGGLE_BUTTON, "\"TableButton\"");

        this.register(OntimizeRegion.FORM_TABBED_PANE, "FormTabbedPane");
        this.register(OntimizeRegion.FORM_TABBED_PANE_TAB, "FormTabbedPane:TabbedPaneTab");
        this.register(OntimizeRegion.FORM_TABBED_PANE_TAB_AREA, "FormTabbedPane:TabbedPaneTabArea");
        this.register(OntimizeRegion.FORM_TABBED_PANE_CONTENT, "FormTabbedPane:TabbedPaneContent");
        this.register(Region.LABEL, "\"FormTabbedPaneTab.Label\"");
        this.register(Region.ARROW_BUTTON, "FormTabbedPane:TabbedPaneTabArea:\"FormTabbedPaneTabArea.button\"");

        this.register(Region.PANEL, "\"Form\"");
        this.register(Region.PANEL, "\"FormExt\"");
        this.register(Region.PANEL, "\"Row\"");
        this.register(Region.PANEL, "\"Column\"");
        this.register(Region.PANEL, "\"CardPanel\"");
        this.register(Region.PANEL, "\"Row\"");
        this.register(Region.PANEL, "\"Grid\"");
        this.register(Region.PANEL, "\"TableButtonPanel\"");
        this.register(Region.PANEL, "\"TableButtonFooterPanel\"");
        this.register(Region.PANEL, "\"FormButtonPanel\"");
        this.register(Region.PANEL, "\"FormBodyPanel\"");

        this.register(Region.LABEL, "\"CollapsibleButtonPanel\"");
        this.register(Region.LABEL, "\"ELabel\"");
        this.register(Region.LABEL, "Tree:\"Tree.cellRenderer\"");
        this.register(Region.LABEL, "\"PopupItem\"");
        this.register(Region.LABEL, "\"PageFetcher.Label\"");
        this.register(Region.LABEL, "\"ResultCountLabel\"");

        this.register(Region.SCROLL_PANE, "\"FormScrollPane\"");
        this.register(Region.SCROLL_PANE, "\"TreeScrollPane\"");

        this.register(Region.TEXT_FIELD, "\"FormTitle\"");
        this.register(Region.TEXT_FIELD, "Table:\"Table.QuickFilter\"");
        this.register(Region.TEXT_FIELD, "TextField:\"TextField.ReferenceExt\"");
        this.register(Region.TEXT_FIELD, "TextField:\"TextField.ReferenceExtCode\"");

        this.register(Region.TOOL_BAR, "\"ComponentToolBar\"");
    }

    /**
     * Returns the ui that is of type <code>klass</code>, or null if one can not be found.
     * @param ui the UI delegate to be tested.
     * @param klass the class to test against.
     * @return {@code ui} if {@code klass} is an instance of {@code ui}, {@code null} otherwise.
     */
    public static Object getUIOfType(final ComponentUI ui, final Class<?> klass) {
        if (klass.isInstance(ui)) {
            return ui;
        }

        return null;
    }

    /**
     * Package protected method which returns either BorderLayout.NORTH, BorderLayout.SOUTH,
     * BorderLayout.EAST, or BorderLayout.WEST depending on the location of the toolbar in its parent.
     * The toolbar might be in PAGE_START, PAGE_END, CENTER, or some other position, but will be
     * resolved to either NORTH,SOUTH,EAST, or WEST based on where the toolbar actually IS, with CENTER
     * being NORTH.
     *
     * <p/>
     * This code is used to determine where the border line should be drawn by the custom toolbar
     * states, and also used by OntimizeIcon to determine whether the handle icon needs to be shifted to
     * look correct.
     * </p>
     *
     * <p>
     * Toollbars are unfortunately odd in the way these things are handled, and so this code exists to
     * unify the logic related to toolbars so it can be shared among the static files such as
     * OntimizeIcon and generated files such as the ToolBar state classes.
     * </p>
     * @param toolbar a toolbar in the Swing hierarchy.
     * @return the {@code BorderLayout} orientation of the toolbar, or {@code BorderLayout.NORTH} if
     *         none can be determined.
     */
    public static Object resolveToolbarConstraint(final JToolBar toolbar) {
        /*
         * NOTE: we don't worry about component orientation or PAGE_END etc because the BasicToolBarUI
         * always uses an absolute position of NORTH/SOUTH/EAST/WEST.
         */
        if (toolbar != null) {
            final Container parent = toolbar.getParent();

            if (parent != null) {
                final LayoutManager m = parent.getLayout();

                if (m instanceof BorderLayout) {
                    final BorderLayout b = (BorderLayout) m;
                    final Object con = b.getConstraints(toolbar);

                    if ((con == BorderLayout.SOUTH) || (con == BorderLayout.EAST) || (con == BorderLayout.WEST)) {
                        return con;
                    }

                    return BorderLayout.NORTH;
                }
            }
        }

        return BorderLayout.NORTH;
    }

    /**
     * Context key to get selectedUI.
     */
    private static final Object SELECTED_UI_KEY = new StringBuilder("selectedUI");

    /**
     * Context key to get selectedUIState.
     */
    private static final Object SELECTED_UI_STATE_KEY = new StringBuilder("selectedUIState");

    public static ComponentUI getSelectedUI() {
        return (ComponentUI) OAppContext.get(OntimizeLookAndFeel.SELECTED_UI_KEY);
    }

    /**
     * Used by the renderers. For the most part the renderers are implemented as Labels, which is
     * problematic in so far as they are never selected. To accomodate this OLabelUI checks if the
     * current UI matches that of <code>selectedUI</code> (which this methods sets), if it does, then a
     * state as set by this method is set in the field {@code selectedUIState}. This provides a way for
     * labels to have a state other than selected.
     * @param uix a UI delegate.
     * @param selected is the component selected?
     * @param focused is the component focused?
     * @param enabled is the component enabled?
     * @param rollover is the component's rollover state enabled?
     */
    public static void setSelectedUI(final ComponentUI uix, final boolean selected, final boolean focused, final boolean enabled,
            final boolean rollover) {
        int selectedUIState = 0;

        if (selected) {
            selectedUIState = SynthConstants.SELECTED;
            if (focused) {
                selectedUIState |= SynthConstants.FOCUSED;
            }
        } else if (rollover && enabled) {
            selectedUIState |= SynthConstants.MOUSE_OVER | SynthConstants.ENABLED;
            if (focused) {
                selectedUIState |= SynthConstants.FOCUSED;
            }
        } else {
            if (enabled) {
                selectedUIState |= SynthConstants.ENABLED;
                if (focused) {
                    selectedUIState |= SynthConstants.FOCUSED;
                }
            } else {
                selectedUIState |= SynthConstants.DISABLED;
            }
        }

        OAppContext.put(OntimizeLookAndFeel.SELECTED_UI_KEY, uix);
        OAppContext.put(OntimizeLookAndFeel.SELECTED_UI_STATE_KEY, Integer.valueOf(selectedUIState));

    }

    public static int getSelectedUIState() {
        final Integer result = (Integer) OAppContext.get(OntimizeLookAndFeel.SELECTED_UI_STATE_KEY);

        return result == null ? 0 : result.intValue();
    }

    /**
     * Clears out the selected UI that was last set in setSelectedUI.
     */
    public static void resetSelectedUI() {
        OAppContext.remove(OntimizeLookAndFeel.SELECTED_UI_KEY);
        OAppContext.remove(OntimizeLookAndFeel.SELECTED_UI_STATE_KEY);
    }

    public Image getImage(final String key) {
        final String path = (String) super.getDefaults().get(key);

        if (path != null) {
            final URL url = OntimizeLookAndFeel.class.getClassLoader().getResource(path);
            return new ImageIcon(url).getImage();
        }
        return null;
    }

    public static void clearReferences() {
        // SynthContext.clearReferences();
        OntimizeRegion.clearReferences();
        OLoweredBorder.clearReferences();
        ShapeFactory.clearReferences();
        try {
            final Field map = javax.swing.plaf.synth.SynthContext.class.getDeclaredField("contextMap");
            map.setAccessible(true);
            final Object value = map.get(null);
			final Method m = Map.class.getDeclaredMethod("clear");
			m.invoke(value);
        } catch (final Throwable t) {
        }
    }

    public static class PainterBorder implements Border, UIResource {

        protected Insets insets;

        protected Painter painter;

        protected String painterKey;

        PainterBorder(final String painterKey, final Insets insets) {
            this.insets = insets;
            this.painterKey = painterKey;
        }

        @Override
        public void paintBorder(final Component c, final Graphics g, final int x, final int y, final int w, final int h) {
            if (this.painter == null) {
                this.painter = (Painter) UIManager.get(this.painterKey);
                if (this.painter == null) {
                    return;
                }
            }

            g.translate(x, y);
            if (g instanceof Graphics2D) {
                this.painter.paint((Graphics2D) g, c, w, h);
            } else {
                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                final Graphics2D gfx = img.createGraphics();
                this.painter.paint(gfx, c, w, h);
                gfx.dispose();
                g.drawImage(img, x, y, null);
                img = null;
            }
            g.translate(-x, -y);
        }

        @Override
        public Insets getBorderInsets(final Component c) {
            return (Insets) this.insets.clone();
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

    }

    public static void setColorUIResource(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final ColorUIResource color = StyleUtil.getColorUI(name, key, defaultValue);
        if (color != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, color);
            } else {
                defaults.put(name + "." + key, color);
            }
        }
    }

    public static void setColor(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Color color = StyleUtil.getColor(name, key, defaultValue);
        if (color != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, color);
            } else {
                defaults.put(name + "." + key, color);
            }
        }
    }

    public static void setPaint(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Paint paint = StyleUtil.getPaint(name, key, defaultValue);
        if (paint != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, paint);
            } else {
                defaults.put(name + "." + key, paint);
            }
        }
    }

    public static void setBoolean(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Boolean boolValue = StyleUtil.getBoolean(name, key, defaultValue);
        if (boolValue != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, boolValue);
            } else {
                defaults.put(name + "." + key, boolValue);
            }
        }
    }

    public static void setInteger(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Integer intValue = StyleUtil.getInteger(name, key, defaultValue);
        if (intValue != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, intValue);
            } else {
                defaults.put(name + "." + key, intValue);
            }
        }
    }

    public static void setFloat(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Float floatValue = StyleUtil.getFloat(name, key, defaultValue);
        if (floatValue != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, floatValue);
            } else {
                defaults.put(name + "." + key, floatValue);
            }
        }
    }

    public static void setDouble(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Double doubleValue = StyleUtil.getDouble(name, key, defaultValue);
        if (doubleValue != null) {
            if (key.startsWith("[") || key.startsWith(":")) {
                defaults.put(name + key, doubleValue);
            } else {
                defaults.put(name + "." + key, doubleValue);
            }
        }
    }

    public static void setInsetsUIResource(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final InsetsUIResource insets = StyleUtil.getInsetsUI(name, key, defaultValue);
        if (insets != null) {
            defaults.put(name + "." + key, insets);
        }
    }

    public static void setFontUIResource(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final FontUIResource font = StyleUtil.getFontUIResource(name, key, defaultValue);
        if (font != null) {
            defaults.put(name + "." + key, font);
        }
    }

    public static void setFont(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Font font = StyleUtil.getFont(name, key, defaultValue);
        if (font != null) {
            defaults.put(name + "." + key, font);
        }
    }

    public static void setIcon(final UIDefaults defaults, final String name, final String key, final String defaultValue) {
        final Icon icon = StyleUtil.getIcon(name, key, defaultValue);
        if (icon != null) {
            defaults.put(name + "." + key, icon);
        }
    }

}
