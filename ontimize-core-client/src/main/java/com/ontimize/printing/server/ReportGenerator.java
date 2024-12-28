package com.ontimize.printing.server;

import java.util.List;

public interface ReportGenerator {

    public static final String REPORT_NOT_FOUND = "INFORME_NO_EXISTE";

    public static final String ERROR = "ERROR";

    public List getReportList();

    public List getReportDescription();

    public String getDescription();

    public String createReport(String name, Object params, String archiveName);

}
