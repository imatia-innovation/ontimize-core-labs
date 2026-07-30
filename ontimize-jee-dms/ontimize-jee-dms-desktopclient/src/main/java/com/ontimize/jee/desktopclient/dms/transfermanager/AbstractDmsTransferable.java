package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.util.Observable;

/**
 * The Class AbstractDmsTransferable.
 */
public abstract class AbstractDmsTransferable extends Observable {

	/**
	 * The Enum Status.
	 */
	public enum Status {

		/** The on prepare. */
		ON_PREPARE("Preparing", false),
		/** The downloading. */
		DOWNLOADING("Downloading", false),
		/** The paused. */
		PAUSED("Paused", false),
		/** The completed. */
		COMPLETED("Complete", true),
		/** The cancelled. */
		CANCELLED("Cancelled", true),
		/** The error. */
		ERROR("Error", true),
		/** The uploading. */
		UPLOADING("Uploading", false);

		/** The name. */
		String	name;

		/** The finish state. */
		boolean	finishState;

		/**
		 * Instantiates a new status.
		 *
		 * @param name
		 *            the name
		 * @param finishState
		 *            the finish state
		 */
		Status(String name, boolean finishState) {
			this.name = name;
			this.finishState = finishState;
		}

		/**
		 * Checks if is finish state.
		 *
		 * @return true, if is finish state
		 */
		public boolean isFinishState() {
			return this.finishState;
		}

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName() {
			return this.name;
		}
	}

	/** The status. */
	private Status	status;

	/** The progress. */
	private Number	progress;

	/** The name. */
	private String	name;

	/** The size. */
	private Long	size;

	/**
	 * Instantiates a new abstract dms transferable.
	 *
	 * @param name
	 *            the name
	 * @param size
	 *            the size
	 */
	public AbstractDmsTransferable(String name, Long size) {
		super();
		this.name = name;
		this.size = size;
		this.status = Status.ON_PREPARE;
	}

	/**
	 * Checks if is finished.
	 *
	 * @return true, if is finished
	 */
	public boolean isFinished() {
		return this.getStatus().isFinishState();
	}

	/**
	 * Checks if is initiated.
	 *
	 * @return true, if is initiated
	 */
	public boolean isInitiated() {
		return !Status.ON_PREPARE.equals(this.getStatus());
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public Status getStatus() {
		return this.status;
	}

	/**
	 * Gets the progress.
	 *
	 * @return the progress
	 */
	public Number getProgress() {
		return this.progress;
	}

	/**
	 * Sets the status.
	 *
	 * @param status
	 *            the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Sets the progress.
	 *
	 * @param progress
	 *            the new progress
	 */
	public void setProgress(Number progress) {
		this.progress = progress;
		this.setChanged();
		this.notifyObservers();
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name
	 *            the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the size.
	 *
	 * @return the size
	 */
	public Long getSize() {
		return this.size;
	}

	/**
	 * Sets the size.
	 *
	 * @param size
	 *            the new size
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * Pause.
	 */
	public void pause() {
		this.setStatus(Status.PAUSED);

	}

	/**
	 * Resume.
	 */
	public void resume() {
		this.setStatus(Status.DOWNLOADING);
	}

	/**
	 * Cancel.
	 */
	public void cancel() {
		this.setStatus(Status.CANCELLED);
	}
}
