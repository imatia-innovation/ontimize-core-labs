package com.ontimize.jee.desktopclient.dms.transfermanager;

import java.util.Observable;

import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.desktopclient.components.taskmanager.ITask;
import com.ontimize.jee.desktopclient.components.taskmanager.TaskStatus;

/**
 * The Class DmsTransferTask.
 */
public class DmsTransferTask implements ITask {

	/** The transferable. */
	private final AbstractDmsTransferable transferable;

	/**
	 * Instantiates a new dms transfer task.
	 *
	 * @param transferable
	 *            the transferable
	 */
	public DmsTransferTask(AbstractDmsTransferable transferable) {
		super();
		this.transferable = transferable;
	}

	/**
	 * Gets the transferable.
	 *
	 * @return the transferable
	 */
	public AbstractDmsTransferable getTransferable() {
		return this.transferable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getName()
	 */
	@Override
	public String getName() {
		return this.transferable.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getSize()
	 */
	@Override
	public Number getSize() {
		return this.transferable.getSize();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getProgress()
	 */
	@Override
	public Number getProgress() {
		return this.transferable.getProgress();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getStatus()
	 */
	@Override
	public TaskStatus getStatus() {
		switch (this.transferable.getStatus()) {
			case CANCELLED:
				return TaskStatus.CANCELLED;
			case COMPLETED:
				return TaskStatus.COMPLETED;
			case ERROR:
				return TaskStatus.ERROR;
			case ON_PREPARE:
				return TaskStatus.ON_PREPARE;
			case PAUSED:
				return TaskStatus.PAUSED;
			case DOWNLOADING:
			case UPLOADING:
				return TaskStatus.RUNNING;
			default:
				return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#pause()
	 */
	@Override
	public void pause() {
		this.transferable.pause();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#resume()
	 */
	@Override
	public void resume() {
		this.transferable.resume();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#cancel()
	 */
	@Override
	public void cancel() {
		this.transferable.cancel();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getObservable()
	 */
	@Override
	public Observable getObservable() {
		return this.transferable;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#onTaskClicked()
	 */
	@Override
	public void onTaskClicked() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#isFinished()
	 */
	@Override
	public boolean isFinished() {
		return ObjectTools.isIn(this.getStatus(), TaskStatus.COMPLETED, TaskStatus.ERROR);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#isPausable()
	 */
	@Override
	public boolean isPausable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#isCancellable()
	 */
	@Override
	public boolean isCancellable() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ontimize.jee.desktopclient.components.taskmanager.ITask#hasResultDetails()
	 */
	@Override
	public boolean hasResultDetails() {
		return false;
	}

}
