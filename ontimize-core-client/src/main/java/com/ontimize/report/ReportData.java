package com.ontimize.report;

import com.ontimize.jee.common.report.store.ReportStore;

/**
 * <p>
 * Internal model for {@linkplain ReportSelection} list.
 *
 * @author Imatia Innovation S.L.
 * @since 03/12/2008
 */
public interface ReportData extends java.io.Serializable {

    public Object getKey();

    public String getName();

    public String getDescription();

    public ReportStore getStore();

    public String getInternalID();

    // Operations

    public void edit(ReportConfig config) throws Exception;

    public void view(ReportConfig config) throws Exception;

    public void print(ReportConfig config) throws Exception;

    public boolean delete(ReportConfig config) throws Exception;

    public void add(ReportConfig config) throws Exception;

}
