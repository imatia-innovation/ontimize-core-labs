package com.ontimize.report;

import java.awt.Component;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.swing.table.TableModel;

import com.ontimize.jee.common.report.store.BasicReportStoreDefinition;
import com.ontimize.report.engine.dynamicjasper.DynamicJasperEngine;
import com.ontimize.report.utils.PreviewDialog;
import com.ontimize.report.utils.ReportProcessor;

/**
 * This interface provides definition of necessary methods to add a new engine for reports.
 * Actually, Ontimize provides two default implementations of this one: {@link FreeReportEngine}
 * (for JFreeReport 0.8.4_10) and {@link DynamicJasperEngine} (for JasperReports 3.5.1).
 *
 * @author Imatia Innovation
 */
public interface ReportEngine {

	/**
	 * Gets the viewer for this engine. It is placed at right in report dialog.
	 * @return the viewer
	 */
	public Object getBaseTemplate();

	/**
	 * Invalidates the viewer. Implementation of this method could be not required.
	 */
	public void close();

	/**
	 * Method to dispose all resources. It is called at the end of report.
	 */
	public void dispose();

	/**
	 * Gets the title of report.
	 * @return the title
	 */
	public String getTitle();

	/**
	 * Sets the title of report.
	 * @param text the text to set in title
	 */
	public void setTitleReport(String text);

	/**
	 * Sets the title of report.
	 * @param text the text to set in title
	 */
	public void setReportDescription(String text);

	/**
	 * Gets the description of report (subtitle).
	 * @return the subtitle of report
	 */
	public String getReportDescription();

	/**
	 * Generates the report. It is the method where all logic of report should be implemented.
	 * Parameters passed can be null whether programmer controls them inside of this method.
	 * @param pageTitle the title of report
	 * @param description subtitle of report
	 * @return the generated report
	 * @throws Exception A new Exception
	 */
	public Object generateReport(String pageTitle, String description) throws Exception;

	/**
	 * Updates the report. When this method is called, report should be generated again.
	 * @param force The boolean to allow/deny dynamically updates when user presses the update
	 *        dynamically button in report dialog.
	 */
	public void updateReport(boolean force);

	/**
	 * Gets the title of report (Maintains backward compatibility).
	 * @return the title The title of page
	 */
	public String getPageTitle();

	/**
	 * Report dialog is passed to engine to get references to dialog elements from engine (i.e. check
	 * buttons marked, columns selected,...).
	 * @param reportDialog the report dialog to be used in engine.
	 */
	public void setDefaultReportDialog(Object reportDialog);

	/**
	 * This method should be only implemented to define a custom report store for reports in our
	 * application.
	 * @param pageTitle the title of page
	 * @return The reportStoreDefinition
	 */
	public BasicReportStoreDefinition generaReportStoreDefinition(String pageTitle);

	/**
	 * Returns the list of templates for reports.
	 * @return The <code>List</code> of templates
	 */
	public List getDefaultTemplates();

	/**
	 * Convenience method. TO-DO
	 */
	public void buildOptions();

	/**
	 * Returns the name of report engine. It will be used only for log and debug purposes.
	 * @return the name of report engine configured
	 */
	public String getReportEngineName();

	public String getXMLTemplate() throws IOException;

	public Map getColumnWidth();

	/**
	 * Implementation of this method for each engine must check libraries in classpath and return
	 * true/false whether all required for reports are present/missed. This method is automatically
	 * checked when report engine is registered.
	 * @return the condition of availability of libraries.
	 */
	public boolean checkLibraries();

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, String template, URL base,
			ReportProcessor rp) throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, String xMLTemplate, URL base)
			throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			ReportProcessor rp) throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base)
			throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc) throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc, ReportProcessor rp) throws Exception;

	/**
	 * Implementation of this method for each engine must show a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog showPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc, ReportProcessor rp, PageFormat pf)
					throws Exception;

	/**
	 * Implementation of this method for each engine must return a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog getPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			ReportProcessor r) throws Exception;

	/**
	 * Implementation of this method for each engine must return a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog getPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base)
			throws Exception;

	/**
	 * Implementation of this method for each engine must return a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog getPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc) throws Exception;

	/**
	 * Implementation of this method for each engine must return a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog getPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc, ReportProcessor r) throws Exception;

	/**
	 * Implementation of this method for each engine must return a dialog with printed report. Not all
	 * parameters are required
	 */
	public PreviewDialog getPreviewDialog(Component c, String title, TableModel m, URL xMLTemplate, URL base,
			String[] order, boolean[] asc, ReportProcessor r, PageFormat pf)
					throws Exception;

}
