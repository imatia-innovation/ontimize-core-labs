package com.ontimize.jee.server.spring.namespace;

import com.ontimize.jee.server.services.dms.DMSConfiguration;

public class OntimizeDMSConfiguration {

    private DMSConfiguration dmsConfiguration;

    public DMSConfiguration getDmsConfiguration() {
        return this.dmsConfiguration;
    }

    public void setDmsConfiguration(DMSConfiguration dmsConfiguration) {
        this.dmsConfiguration = dmsConfiguration;
    }

}
