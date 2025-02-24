package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * The Class RemoteDriveTableModel.
 */
public class CloudDriveTableModel<T extends Serializable> extends AbstractTableModel {

	private static final long	serialVersionUID		= 1L;
	public static final String	COLUMN_DESCRIPTION		= "dms.gfile_description";
	public static final String	COLUMN_USERMODIFICATION	= "dms.gfile_usermodification";
	public static final String	COLUMN_DATEMODIFICATION	= "dms.gfile_datemodification";
	public static final String	COLUMN_NAME				= "dms.gfile_name";
	public static final String	COLUMN_SIZE				= "dms.gfile_size";

	/** The data list. */
	private List<T>				dataList				= new ArrayList<>();

	/** The columns. */
	private static String[]		columns					= new String[] { CloudDriveTableModel.COLUMN_NAME, CloudDriveTableModel.COLUMN_SIZE, CloudDriveTableModel.COLUMN_DATEMODIFICATION, CloudDriveTableModel.COLUMN_USERMODIFICATION, CloudDriveTableModel.COLUMN_DESCRIPTION };

	/**
	 * Instantiates a new remote drive table model.
	 */
	public CloudDriveTableModel() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return this.dataList.size();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int column) {
		if ((column < 0) || (column >= CloudDriveTableModel.columns.length)) {
			return null;
		}
		return CloudDriveTableModel.columns[column];
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return CloudDriveTableModel.columns.length;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.getFileAtRow(rowIndex);
	}

	/**
	 * Sets the data list.
	 *
	 * @param dataList
	 *            the new data list
	 */
	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
		if (this.dataList == null) {
			this.dataList = new ArrayList<>();
		}
		this.fireTableDataChanged();
	}

	/**
	 * Gets the file at row.
	 *
	 * @param row
	 *            the row
	 * @return the file at row
	 */
	public T getFileAtRow(int row) {
		if ((row < 0) || (row >= this.dataList.size())) {
			return null;
		}
		return this.dataList.get(row);
	}

}
