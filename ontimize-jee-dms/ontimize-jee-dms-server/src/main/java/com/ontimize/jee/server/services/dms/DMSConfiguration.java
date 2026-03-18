package com.ontimize.jee.server.services.dms;

import com.ontimize.jee.common.tools.CheckingTools;

/**
 * The Class DMSConfiguration.
 */
public class DMSConfiguration {

    /** The engine. */
    private IDMSServiceServer engine;

    /**
     * Gets the engine.
     * @return the engine
     */
    public IDMSServiceServer getEngine() {
        CheckingTools.failIfNull(this.engine, "No dms engine defined");
        return this.engine;
    }

    /**
     * Sets the engine.
     * @param engine the engine
     */
    public void setEngine(IDMSServiceServer engine) {
        this.engine = engine;
    }

}
