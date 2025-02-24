package com.ontimize.db;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.jee.common.dto.EntityResult;

public class ApplicationStatusBarInputStream extends BufferedInputStream {

	private static final long ONE_GB = 1024 * 1024 * 1024;

	private static final long ONE_MB = 1024 * 1024;

	private static final long ONE_KB = 1024;

	private static final int MIN_BYTE_PROGRESS = 1024 * 50;

	protected int bytesNumberToUncompressor = 0;

	protected int currentReadingPosition = 0;

	protected int percentLastAct = 0;

	protected String timeLeftLabel;

	protected Application application = null;


	protected int readProgress = 0;

	protected long t0 = 0;


	public ApplicationStatusBarInputStream(final InputStream in, final int size) {
		super(in);
		this.bytesNumberToUncompressor = size;
		this.application = ApplicationManager.getApplication();
		if ((this.application != null) && (this.application instanceof MainApplication)) {
			((MainApplication) this.application).setStatusBarProgressMaximum(this.bytesNumberToUncompressor);
		}
		this.timeLeftLabel = ApplicationManager.getTranslation("time_left") + ": ";


	}

	@Override
	public void close() throws IOException {
		// ATTENTION: Stream is not closed
		this.in = null;
		this.buf = null;
	}

	protected void updateStateBar(final int pos) {
		this.updateStateBar(pos, 0);
	}


	protected void updateStateBar(final int pos, final double timeElapsed) {
		if (pos == 0) {
			if (this.application instanceof MainApplication) {
				final MainApplication ap2 = (MainApplication) this.application;
				ap2.setStatusBarProgressPosition(0);
				ap2.setStatusBarProgressText(null, false);
				ap2.setStatusBarText(null);
			}
			this.percentLastAct = 0;
		} else {
			final int iCurrentPercent = (int) ((this.currentReadingPosition * 100.0) / this.bytesNumberToUncompressor);
			if ((iCurrentPercent - this.percentLastAct) > EntityResult.MIN_PERCENT_PROGRESS) {
				this.percentLastAct = iCurrentPercent;
				if (this.application instanceof MainApplication) {
					final MainApplication ap2 = (MainApplication) this.application;
					ap2.setStatusBarProgressPosition(this.currentReadingPosition, false);
					ap2.setStatusBarProgressText(
							iCurrentPercent + " %  " + (this.currentReadingPosition / 1024) + " KB", false);
					if (timeElapsed > 0) {
						String timeLeft = "";
						final int secondsLeft = (int) (((this.bytesNumberToUncompressor - this.currentReadingPosition)
								* timeElapsed) / this.readProgress);
						if (secondsLeft > 3600) {
							timeLeft = (secondsLeft / 3600) + " h";
						} else if (secondsLeft > 60) {
							timeLeft = (secondsLeft / 60) + " m";
						} else {
							timeLeft = secondsLeft + " s";
						}
						String rate = "";
						final double bytesPerSecond = this.readProgress / timeElapsed;
						if (bytesPerSecond >= ONE_GB) {
							rate = String.valueOf((int) (bytesPerSecond / ONE_GB)) + " GB/s";
						} else if (bytesPerSecond >= ONE_MB) {
							rate = String.valueOf((int) (bytesPerSecond / ONE_MB)) + " MB/s";
						} else if (bytesPerSecond >= ONE_KB) {
							rate = String.valueOf((int) (bytesPerSecond / ONE_KB)) + " KB/s";
						} else {
							rate = String.valueOf((int) bytesPerSecond) + " B/s";
						}
						ap2.setStatusBarText(this.timeLeftLabel + timeLeft + " (" + rate + ")");
					}
				}
			}
		}
	}


	@Override
	public int read() throws IOException {
		if (this.currentReadingPosition >= this.bytesNumberToUncompressor) {
			if ((this.bytesNumberToUncompressor > MIN_BYTE_PROGRESS) && (this.application != null)
					&& (this.application instanceof MainApplication)) {
				this.updateStateBar(0);
			}
			return -1;
		}
		final int res = super.read();
		if (res != -1) {
			this.currentReadingPosition += 1;
			if ((this.bytesNumberToUncompressor > MIN_BYTE_PROGRESS) && (this.application != null)
					&& (this.application instanceof MainApplication)) {
				this.updateStateBar(this.currentReadingPosition);
			}
		} else {
			if ((this.bytesNumberToUncompressor > MIN_BYTE_PROGRESS) && (this.application != null)
					&& (this.application instanceof MainApplication)) {
				this.updateStateBar(this.bytesNumberToUncompressor);
			}
		}
		return res;
	}


	@Override
	public int read(final byte[] b) throws IOException {
		return this.read(b, 0, b.length);
	}


	@Override
	public int read(final byte[] b, final int offset, final int len) throws IOException {
		if (this.currentReadingPosition >= this.bytesNumberToUncompressor) {
			if ((this.bytesNumberToUncompressor > MIN_BYTE_PROGRESS) && (this.application != null)
					&& (this.application instanceof MainApplication)) {
				final MainApplication ap2 = (MainApplication) this.application;
				ap2.setStatusBarProgressPosition(0);
				ap2.setStatusBarProgressText(null, false);
				ap2.setStatusBarText(null);
			}
			return -1;
		}

		final int res = super.read(b, offset, Math.min(this.bytesNumberToUncompressor - this.currentReadingPosition, len));
		if (res != -1) {
			this.currentReadingPosition += res;
			this.readProgress += res;
		}

		if ((this.bytesNumberToUncompressor > MIN_BYTE_PROGRESS) && (this.application != null)
				&& (this.application instanceof MainApplication)) {
			final double tElapsed = (System.nanoTime() - this.t0) / 1e9;
			this.updateStateBar(res != -1 ? this.currentReadingPosition : this.bytesNumberToUncompressor, tElapsed);
			this.t0 = System.nanoTime();
			this.readProgress = 0;
		}

		return res;
	}

	@Override
	public long skip(final long n) throws IOException {
		final long l = super.skip(n);
		this.currentReadingPosition += l;
		return l;
	}

}
