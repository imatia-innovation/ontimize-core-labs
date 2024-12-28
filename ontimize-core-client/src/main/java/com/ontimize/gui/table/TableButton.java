package com.ontimize.gui.table;

import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.Icon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.util.swing.RolloverButton;

/**
 * A button that can be placed in a Table
 */
public class TableButton extends RolloverButton
        implements Internationalization, TableComponent, Transferable, DragGestureListener, DragSourceListener {

    private static final Logger logger = LoggerFactory.getLogger(TableButton.class);

    /**
     * The name of class. Used by L&F to put UI properties.
     *
     * @since 5.2062EN
     */
    public static final String TABLEBUTTON_NAME = "TableButton";

    public static Boolean defaultPaintFocus;

    public static Boolean defaultContentAreaFilled;

    public static Boolean defaultCapable;

    protected DragSource source;

    Dimension dimension = new Dimension(22, 22);

    String originalTooltip = null;

    String defaultTooptip = null;

    protected Object key = null;

    public TableButton(String text) {
        super(text);
        this.key = text;
        this.init();
    }

    public TableButton(Object key, String text) {
        super(text);
        this.key = key;
        this.init();
    }

    public TableButton() {
        super();
        this.init();
    }

    public TableButton(Icon icon) {
        super(icon);
        this.init();
    }

    protected void init() {
        this.source = new DragSource();
        this.source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
    }

    @Override
    public boolean isDefaultCapable() {
        if (TableButton.defaultCapable != null) {
            return TableButton.defaultCapable.booleanValue();
        }
        return false;
    }

    @Override
    public void setContentAreaFilled(boolean b) {
        if (TableButton.defaultContentAreaFilled != null) {
            super.setContentAreaFilled(TableButton.defaultContentAreaFilled.booleanValue());
            return;
        }
        super.setContentAreaFilled(b);
    }

    @Override
    public void setFocusPainted(boolean b) {
        if (TableButton.defaultPaintFocus != null) {
            super.setFocusPainted(TableButton.defaultPaintFocus.booleanValue());
            return;
        }
        super.setFocusPainted(b);
    }

    @Override
    public Dimension getMinimumSize() {
        return this.getPreferredSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return this.getPreferredSize();
    }

    @Override
    public void setToolTipText(String t) {
        this.originalTooltip = t;
        super.setToolTipText(t);
    }

    public void setDefaultToolTipText(String t) {
        this.defaultTooptip = t;
    }

    @Override
    public Dimension getPreferredSize() {
        return this.dimension;
    }

    @Override
    public void setPreferredSize(Dimension preferredSize) {
        super.setPreferredSize(preferredSize);
        this.dimension = preferredSize;
    }

    @Override
    public String getName() {
        return TableButton.TABLEBUTTON_NAME;
    }

    @Override
    public void setResourceBundle(ResourceBundle res) {
        if ((res != null) && (this.originalTooltip != null)) {
            super.setToolTipText(ApplicationManager.getTranslation(this.originalTooltip, res));
        } else {
            super.setToolTipText(this.defaultTooptip);
        }
    }

    @Override
    public void setComponentLocale(Locale l) {
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public void setKey(Object k) {
        this.key = k;
    }

    protected boolean dragEnabled = false;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        try {
            return new DataFlavor[] { new DataFlavor(
                    DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" + TableButton.class.getName() + "\"") };
        } catch (ClassNotFoundException e) {
            TableButton.logger.error(null, e);
        }
        return null;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        return this;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        this.dragEnabled = this.isEnabled();
        this.setEnabled(false);
        this.source.startDrag(dge, DragSource.DefaultMoveDrop, this, this);
    }

    @Override
    public void dragEnter(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragOver(DragSourceDragEvent dsde) {
    }

    @Override
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    @Override
    public void dragExit(DragSourceEvent dse) {
    }

    @Override
    public void dragDropEnd(DragSourceDropEvent dsde) {
        this.setEnabled(this.dragEnabled);
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
    }

}
