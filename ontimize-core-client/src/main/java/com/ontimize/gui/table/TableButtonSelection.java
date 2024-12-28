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
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.swing.ButtonSelection;
import com.ontimize.util.swing.RolloverButton;

/**
 * Class that defines table buttons with a deployable panel
 */
public class TableButtonSelection extends ButtonSelection
        implements Internationalization, TableComponent, Transferable, DragGestureListener, DragSourceListener {

    private static final Logger logger = LoggerFactory.getLogger(TableButtonSelection.class);

    protected Dimension dimension = new Dimension(32, 22);

    protected String originalTooltip = null;

    protected String defaultTooptip = null;

    protected Object key = null;

    protected DragSource source;

    public TableButtonSelection() {
        super(true);
        this.source = new DragSource();
        this.source.createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_MOVE, this);
        this.source.createDefaultDragGestureRecognizer(this.getButton(), DnDConstants.ACTION_MOVE, this);
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

    @Override
    public void setComponentLocale(Locale l) {
    }

    @Override
    public void setResourceBundle(ResourceBundle res) {
        if (res != null) {
            super.setToolTipText(ApplicationManager.getTranslation(this.originalTooltip, res));
        } else {
            super.setToolTipText(this.defaultTooptip);
        }
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public void setKey(Object o) {
        this.key = o;
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
        if (ApplicationManager.useOntimizePlaf) {
            Dimension d = super.getPreferredSize();
            if ((this.getButton() != null) && (this.getButton().getPreferredSize() != null)) {
                d.height = this.getButton().getPreferredSize().height;
            }
            return d;
        }
        return this.dimension;
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
    public void setIcon(Icon icon) {
        super.setIcon(icon);
        if (RolloverButton.createRolloverIcon && (icon instanceof ImageIcon)) {
            ImageIcon rollOverIcon = ImageManager.transparent((ImageIcon) icon, 0.5f);
            this.button.setRolloverIcon(rollOverIcon);

            if (this.menuButton.getIcon() instanceof ImageIcon) {
                rollOverIcon = ImageManager.transparent((ImageIcon) this.menuButton.getIcon(), 0.5f);
                this.menuButton.setRolloverIcon(rollOverIcon);
            }
        }
    }

    protected boolean dragEnabled = false;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        try {
            return new DataFlavor[] { new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=\""
                    + TableButtonSelection.class.getName() + "\"") };
        } catch (ClassNotFoundException e) {
            TableButtonSelection.logger.error(null, e);
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

}
