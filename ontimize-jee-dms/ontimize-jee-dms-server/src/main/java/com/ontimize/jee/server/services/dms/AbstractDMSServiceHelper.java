package com.ontimize.jee.server.services.dms;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract class implemented bu DMS helpers (docFelper, fileHelper, versionHelper). This ensure to
 * set a default configuration to columnHelper, that manages all relative about columns naming.
 */

public abstract class AbstractDMSServiceHelper implements InitializingBean {

    @Autowired(required = false)
    private DMSColumnHelper columnHelper;

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    public DMSColumnHelper getColumnHelper() {
        return this.columnHelper;
    }

    public void setColumnHelper(DMSColumnHelper columnHelper) {
        this.columnHelper = columnHelper;
    }

}
