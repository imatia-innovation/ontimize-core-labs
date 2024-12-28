package com.ontimize.util.logging;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;

import com.ontimize.jee.common.util.logging.ILogManager;
import com.ontimize.jee.common.util.logging.Level;
import com.ontimize.jee.common.util.logging.LogManagerFactory;

public class LevelCellEditor extends DefaultCellEditor {

	protected static ILogManager manager = LogManagerFactory.getLogManager();

	protected static JComboBox comboBox = new JComboBox();

	protected static Level[] levels = new Level[] { null, Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR,
			Level.OFF };

	private final boolean editable = false;

	Object defaullO = null;

	public LevelCellEditor() {
		super(LevelCellEditor.comboBox);
		LevelCellEditor.comboBox.setRenderer(new CellRendererList());
	}

	protected class CellRendererList extends DefaultListCellRenderer {

		@Override
		public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
				final boolean cellHasFocus) {
			final Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (comp instanceof JLabel) {
				if (value == null) {
					((JLabel) comp).setText("    ");
				}
				// if ((getDefaultItem()!=null)&&(value!=null)){
					// if (getDefaultItem().equals(value)){
				// comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
				// }
				// }
			}
			return comp;
		}

	}

	@Override
	public Component getTableCellEditorComponent(final JTable t, final Object v, final boolean s, final int r, final int c) {
		final Component comp = super.getTableCellEditorComponent(t, v, s, r, c);
		if (comp instanceof JComboBox) {
			((JComboBox) comp).setModel(new DefaultComboBoxModel(LevelCellEditor.levels));
			((JComboBox) comp).setSelectedItem(v);
		}
		return comp;
	}

}
