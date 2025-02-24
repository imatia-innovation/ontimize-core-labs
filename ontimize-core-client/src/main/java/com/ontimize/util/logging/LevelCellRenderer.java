package com.ontimize.util.logging;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.ontimize.jee.common.util.logging.Level;

public class LevelCellRenderer extends JLabel implements TableCellRenderer {

	public LevelCellRenderer() {
		this.setOpaque(true);
	}

	@Override
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus,
			final int row, final int column) {
		if (value instanceof Level) {
			final Level current = (Level) value;
			this.setText(current.toString());
			this.setForeground(Color.BLACK);

			switch (current) {
			case TRACE:
				this.setBackground(Color.BLUE);
				this.setForeground(Color.WHITE);
				break;
			case DEBUG:
				this.setBackground(Color.GREEN);
				break;
			case INFO:
				this.setBackground(Color.WHITE);
				break;
			case WARN:
				this.setBackground(Color.YELLOW);
				break;
			case ERROR:
				this.setBackground(Color.ORANGE);
				break;
			case OFF:
				this.setBackground(Color.RED);
				break;
			default:
				this.setBackground(Color.WHITE);
				break;
			}
		} else {
			this.setBackground(Color.WHITE);
			this.setText("");
		}

		return this;
	}

}
