package com.ontimize.report;

import com.ontimize.jee.common.report.store.ReportStore;

public class ReportName {

    private String name = null;

    private final String description;

    private ReportStore reportStore = null;

    public ReportName(String name, String dsrc, ReportStore store) {
        this.name = name;
        this.description = dsrc;
        this.reportStore = store;
    }

    public String getName() {
        return this.name;
    }

    public ReportStore getStore() {
        return this.reportStore;
    }

    public String getDescription() {
        return this.description;
    }

}
