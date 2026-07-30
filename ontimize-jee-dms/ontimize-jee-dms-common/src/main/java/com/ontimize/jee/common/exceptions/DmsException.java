package com.ontimize.jee.common.exceptions;

import com.ontimize.jee.common.tools.MessageType;

/**
 * The Class DmsException.
 */
public class DmsException extends OntimizeJEEException {

    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new dms exception.
     */
    public DmsException() {
        super();
    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     */
    public DmsException(String message) {
        super(message);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param cause the cause
     */
    public DmsException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param msgParameters the msg parameters
     */
    public DmsException(String message, Object... msgParameters) {
        super(message, msgParameters);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param msgParameters the msg parameters
     * @param messageType the message type
     */
    public DmsException(String message, Object[] msgParameters, MessageType messageType) {
        super(message, msgParameters, messageType);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param msgParameters the msg parameters
     * @param cause the cause
     */
    public DmsException(String message, Object[] msgParameters, Throwable cause) {
        super(message, msgParameters, cause);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param msgParameters the msg parameters
     * @param msgType the msg type
     * @param msgBlocking the msg blocking
     */
    public DmsException(String message, Object[] msgParameters, MessageType msgType, boolean msgBlocking) {
        super(message, msgParameters, msgType, msgBlocking);

    }

    /**
     * Instantiates a new dms exception.
     * @param cause the cause
     */
    public DmsException(Throwable cause) {
        super(cause);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param msgParameters the msg parameters
     * @param cause the cause
     * @param msgType the msg type
     * @param msgBlocking the msg blocking
     * @param silent the silent
     */
    public DmsException(String message, Object[] msgParameters, Throwable cause, MessageType msgType,
            boolean msgBlocking, boolean silent) {
        super(message, msgParameters, cause, msgType, msgBlocking, silent);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param cause the cause
     * @param msgParameters the msg parameters
     * @param msgType the msg type
     * @param msgBlocking the msg blocking
     * @param silent the silent
     * @param enableSuppression the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public DmsException(String message, Throwable cause, Object[] msgParameters, MessageType msgType,
            boolean msgBlocking, boolean silent, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, msgParameters, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

    }

    /**
     * Instantiates a new dms exception.
     * @param message the message
     * @param cause the cause
     * @param msgType the msg type
     * @param msgBlocking the msg blocking
     * @param silent the silent
     * @param enableSuppression the enable suppression
     * @param writableStackTrace the writable stack trace
     */
    public DmsException(String message, Throwable cause, MessageType msgType, boolean msgBlocking, boolean silent,
            boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, msgType, msgBlocking, silent, enableSuppression, writableStackTrace);

    }

}
