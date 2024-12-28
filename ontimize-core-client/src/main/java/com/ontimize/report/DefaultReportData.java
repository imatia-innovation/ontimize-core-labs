package com.ontimize.report;

import java.awt.Window;
import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.report.store.ReportStore;

public abstract class DefaultReportData implements ReportData {

    protected Object key;

    protected String name;

    protected String description;

    protected ReportStore store;

    public DefaultReportData(Object key, String name, String description, ReportStore store) {
        this.key = key;
        this.name = name;
        this.description = description;
        this.store = store;
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public ReportStore getStore() {
        return this.store;
    }

    @Override
    public abstract String getInternalID();

    protected boolean askDelete(ReportConfig config) {
        Window ancestor = config.getAncestor();
        ResourceBundle bundle = config.getResourceBundle();

        String message = ApplicationManager.getTranslation("ReportDesigner.M_BorrarInforme", bundle);
        int option = JOptionPane.showConfirmDialog(ancestor, message);
        return option == JOptionPane.OK_OPTION;
    }

}
