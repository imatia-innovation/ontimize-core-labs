package com.ontimize.util.logging;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.util.logging.IRemoteLogManager;
import com.ontimize.jee.common.util.logging.Level;
import com.ontimize.jee.common.util.logging.LogManagerFactory;

public class LoggerModel extends AbstractTableModel {

	private static final Logger logger = LoggerFactory.getLogger(LoggerModel.class);

	protected List<Logger> list;

	protected IRemoteLogManager manager;

	public LoggerModel(final IRemoteLogManager manager) {
		this.manager = manager;
	}

	@Override
	public String getColumnName(final int column) {
		if (column == 0) {
			return "Logger";
		}
		if (column == 1) {
			return "Level";
		}

		return super.getColumnName(column);
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		if (columnIndex == 1) {
			return Level.class;
		}
		return super.getColumnClass(columnIndex);
	}

	@Override
	public int getRowCount() {
		if (this.list == null) {
			return 0;
		}
		return this.list.size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public boolean isCellEditable(final int rowIndex, final int columnIndex) {
		if (columnIndex == 1) {
			return true;
		}
		return false;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		final Logger logger = this.list.get(rowIndex);
		if (columnIndex == 0) {
			return logger.getName();
		}
		if (columnIndex == 1) {
			return this.getLevel(logger);
		}
		return null;
	}

	@Override
	public void setValueAt(final Object aValue, final int rowIndex, final int columnIndex) {
		if (columnIndex == 1) {
			final Logger logger = this.list.get(rowIndex);
			try {
				this.setLevel(logger, (Level) aValue);
			} catch (final Exception e) {
				logger.error("setValueAt", e);
			}
		}
		super.setValueAt(aValue, rowIndex, columnIndex);

	}

	public List<Logger> getList() {
		return this.list;
	}

	public void setList(final List<Logger> list) {
		this.list = list;
		this.fireTableDataChanged();
	}

	public Level getLevel(final Logger logger) {
		if (this.manager != null) {
			try {
				return this.manager.getLevel(logger, "");
			} catch (final Exception e) {
				logger.error("remote getLevel exception", e);
				return null;
			}
		}
		return LogManagerFactory.getLogManager().getLevel(logger);
	}

	public void setLevel(final Logger logger, final Level level) throws Exception {
		if (this.manager != null) {
			this.manager.setLevel(logger, level, "");
		}
		LogManagerFactory.getLogManager().setLevel(logger, level);
	}

}
