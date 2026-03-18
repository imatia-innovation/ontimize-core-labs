package com.ontimize.jee.desktopclient.dms.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

/**
 * The Class DataWrapperTransferable.
 *
 * @param <T>
 *            the generic type
 */
public class DataWrapperTransferable<T> implements Transferable {

	/** The flavor. */
	private final DataFlavor		flavor;

	/** The flavors. */
	private final DataFlavor		flavors[];

	/** The data wrapper. */
	private final DataWrapper<T>	dataWrapper;

	/**
	 * Instantiates a new data wrapper transferable.
	 *
	 * @param data
	 *            the data
	 * @param source
	 *            the source
	 * @param humanReadable
	 *            the human readable
	 */
	public DataWrapperTransferable(T data, Object source, String humanReadable) {
		this.dataWrapper = new DataWrapper<>(data, source);
		this.flavor = new DataFlavor(DataWrapper.class, humanReadable);
		this.flavors = new DataFlavor[] { this.flavor };
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	@Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		if (flavor.equals(flavor)) {
			return this.dataWrapper;
		} else {
			throw new UnsupportedFlavorException(flavor);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return this.flavors;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return flavor.equals(flavor) || flavor.equals(DataFlavor.stringFlavor);
	}
}
