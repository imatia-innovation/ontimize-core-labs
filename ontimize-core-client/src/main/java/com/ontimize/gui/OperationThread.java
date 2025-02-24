package com.ontimize.gui;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

/**
 * Basic Thread implementation that performs an operation and stores the result into an
 * {@link EntityResult} class.<br>
 * The operation must be performed in the {@link #run()} method, and the result must be stored in an
 * OperationThread variable called {@link #res}.<br>
 * The run method must set the right operation status, such as {@link #hasFinished},
 * {@link #hasStarted}, {@link #cancelled} , etc. An implementation example could be <BR>
 * <code>public void run() {<BR> hasStarted=true;<BR> // Perform operation. The finish check
 * is done with isAlive() hasFinished=true;<BR> }<BR> </code>
 */
public class OperationThread extends Thread {

    protected Object res = null;

    protected boolean cancelled = false;

    protected Thread t = null;

    protected boolean hasFinished = false;

    protected boolean hasStarted = false;

    protected String status = "";

    public static String OPERATION_CANCELLED = "entity.operation_cancelled";

    public String description = null;

    /**
     * Constructor.
     */
    public OperationThread() {
    }

    /**
     * Sets a description to the thread
     * @param description the thread description
     */
    public OperationThread(String description) {
        super(description);
        this.description = description;
    }

    /**
     * Returns the thread description
     * @return the thread description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Checks whether the thread has been canceled or not
     * @return true if the thread was canceled, false otherwise
     */
    public synchronized boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Sets the thread status as canceled. If stopping safely the thread can be done, it must be done in
     * this method. The {@link EntityResult} configured for this thread is set as a new
     * {@link EntityResult} with operation wrong and the message OPERATION_CANCELLED.
     */
    public synchronized void cancel() {
        this.cancelled = true;
        // TODO the Cancel status must be changed
        this.status = "Cancelled";
        this.res = new EntityResultMapImpl();
        ((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
        ((EntityResult) this.res).setMessage(OperationThread.OPERATION_CANCELLED);
    }

    /**
     * Checks whether the thread has finished.
     * @return true if the thread is finished, false if the thread is still running
     */
    public synchronized boolean hasFinished() {
        if (!this.isAlive()) {
            return true;
        }
        return this.hasFinished;
    }

    /**
     * Checks whether the thread has started.
     * @return rue if the thread has started, false otherwise
     */
    public synchronized boolean hasStarted() {
        return this.hasStarted;
    }

    /**
     * Returns the thread result. The result by default is stored in a EntityResult object.
     * @return the thread result
     */
    public synchronized Object getResult() {
        return this.res;
    }

    /**
     * Returns the thread status, which is a message referent to the thread situation.
     * @return
     */
    public String getStatus() {
        return this.status;
    }

}
