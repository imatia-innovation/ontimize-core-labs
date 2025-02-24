package com.ontimize.util.swing.selectablelist;

import java.awt.Color;
import java.awt.Component;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

public class SelectableItemRenderer extends JCheckBox implements ListCellRenderer {

    private static final Logger logger = LoggerFactory.getLogger(SelectableItemRenderer.class);

    public static class TranslatedItem implements Internationalization {

        protected String text = "";

        protected String translatedText = null;

        protected ResourceBundle res = null;

        public TranslatedItem(String text, ResourceBundle res) {
            this.text = text;
            this.translatedText = text;
            this.setResourceBundle(res);
        }

        public String getText() {
            return this.text;
        }

        @Override
        public void setResourceBundle(ResourceBundle res) {
            this.res = res;
            if (res != null) {
                try {
                    this.translatedText = res.getString(this.text);
                } catch (Exception e) {
                    this.translatedText = this.text;
                    if (ApplicationManager.DEBUG) {
                        SelectableItemRenderer.logger.debug(null, e);
                    }
                }
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
        public String toString() {
            return this.translatedText;
        }

        @Override
        public int hashCode() {
            return this.text.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            } else if (o instanceof SelectableItem) {
                if (this.text.equals(((SelectableItem) o).getText())) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

    }

    public static class SelectableItem extends TranslatedItem implements Internationalization {

        protected boolean selected = false;

        public SelectableItem(String text, ResourceBundle res) {
            super(text, res);
        }

        public boolean isSelected() {
            return this.selected;
        }

        @Override
        public void setResourceBundle(ResourceBundle res) {
            super.setResourceBundle(res);
        }

        public void setSelected(boolean sel) {
            this.selected = sel;
        }

        @Override
        public String toString() {
            return this.translatedText;
        }

    };

    public SelectableItemRenderer() {
        this.setBorderPaintedFlat(true);
    }

    @Override
    public String getName() {
        return "SelectableItem";
    }

    @Override
    public Component getListCellRendererComponent(JList l, Object v, int r, boolean sel, boolean foc) {

        Color selectedBackground = UIManager.getColor("List[Selected].textBackground");
        Color selectedForeground = UIManager.getColor("List[Selected].textForeground");

        if (selectedBackground == null) {
            selectedBackground = UIManager.getColor("List.selectionBackground");
        }
        if (selectedForeground == null) {
            selectedForeground = UIManager.getColor("List.selectionForeground");
        }

        Color notSelectedBackground = UIManager.getColor("\"SelectableItem\".background");
        Color notSelectedForeground = UIManager.getColor("\"SelectableItem\".foreground");

        if (notSelectedBackground == null) {
            notSelectedBackground = UIManager.getColor("List.background");
        }
        if (notSelectedForeground == null) {
            notSelectedForeground = UIManager.getColor("List.foreground");
        }

        this.setOpaque(true);
        if (sel) {
            this.setForeground(selectedForeground);
            this.setBackground(selectedBackground);
        } else {
            this.setForeground(notSelectedForeground);
            this.setBackground(notSelectedBackground);
        }
        if (v instanceof SelectableItem) {
            this.setText(((SelectableItem) v).toString());
            boolean bSelected = ((SelectableItem) v).isSelected();
            this.setSelected(bSelected);
        }
        return this;
    }

    @Override
    public void setForeground(Color fg) {
        super.setForeground(fg);
    }

}
