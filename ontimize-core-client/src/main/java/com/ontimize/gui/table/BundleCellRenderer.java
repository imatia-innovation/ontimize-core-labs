package com.ontimize.gui.table;

import java.awt.Component;
import java.awt.TextComponent;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;

/**
 * Renderer used to show the translation of a text in a table.
 *
 */
public class BundleCellRenderer extends CellRenderer implements Internationalization {

	protected ResourceBundle bundle;

	public BundleCellRenderer() {
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean selected, final boolean hasFocus,
			final int row, final int column) {
		final Component c = super.getTableCellRendererComponent(table, value, selected, hasFocus, row, column);
		String translation = null;
		if (value != null) {
			translation = ApplicationManager.getTranslation(value.toString(), this.bundle);
		}

		if (c instanceof TextComponent) {
			((TextComponent) c).setText(translation);
		} else if (c instanceof JLabel) {
			((JLabel) c).setText(translation);
		}
		return c;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.bundle = res;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector(0);
	}

}
