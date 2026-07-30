package com.ontimize.jee.desktopclient.dms.util;

import java.io.IOException;
import java.io.InputStream;

import com.ontimize.jee.desktopclient.dms.transfermanager.AbstractDmsTransferable;

/**
 * The Class PercentageInputStream.
 */
public class ProgressInputStream extends InputStream {

	/** The current length. */
	protected long						currentLength	= 0;

	/** The total length. */
	protected long						totalLength;

	/** The wrap. */
	protected InputStream				wrap;

	/** The transferable. */
	protected AbstractDmsTransferable	transferable;

	/**
	 * Instantiates a new percentage input stream.
	 *
	 * @param is
	 *            the is
	 * @param transferable
	 *            the transferable
	 * @param totalLength
	 *            the total length
	 */
	public ProgressInputStream(InputStream is, AbstractDmsTransferable transferable, long totalLength) {
		super();
		this.currentLength = 0;
		this.wrap = is;
		this.totalLength = totalLength;
		this.transferable = transferable;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read()
	 */
	@Override
	public int read() throws IOException {
		int read = this.wrap.read();
		if ((read >= 0) && (this.totalLength > 0)) {
			this.currentLength++;
			this.transferable.setProgress(this.currentLength / this.totalLength);
		}
		return read;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int read = this.wrap.read(b, off, len);
		if (read > 0) {
			this.currentLength += read;
			this.transferable.setProgress((double) this.currentLength / this.totalLength);
		}
		return read;
	}

	/*
	 * (non-Javadoc)
	 * @see java.io.InputStream#read(byte[], int, int)
	 */
	@Override
	public int read(byte[] b) throws IOException {
		int read = this.wrap.read(b);
		if (read > 0) {
			this.currentLength += read;
			this.transferable.setProgress((double) this.currentLength / this.totalLength);
		}
		return read;
	}

	@Override
	public long skip(long n) throws IOException {
		long skip = super.skip(n);
		if (skip > 0) {
			this.currentLength += skip;
			this.transferable.setProgress((double) this.currentLength / this.totalLength);
		}
		return skip;
	}
}