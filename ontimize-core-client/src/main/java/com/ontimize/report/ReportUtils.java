package com.ontimize.report;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.SwingUtilities;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.report.store.ReportStore;
import com.ontimize.report.utils.PreviewDialog;
import com.ontimize.report.utils.ReportProcessor;

public class ReportUtils {

	private static final Logger logger = LoggerFactory.getLogger(ReportUtils.class);

	public static final String REPORT_NAME = "ReportDesigner.ReportName";

	public static final String REPORT_STORE = "ReportDesigner.ReportStore";

	public static final String REPORT_SAVE = "ReportDesigner.ReportSaveDialog";

	public static final String STORED_REPORT_LIST = "ReportDesigner.StoredReportList";

	private static java.util.List templateList = new ArrayList();

	public static final int MIN = 0;

	public static final int MAX = 1;

	public static final int SUM = 2;

	public static final int AVG = 3;

	public static final int GROUP_BY_DATE_TIME = 0;

	public static final int GROUP_BY_DATE = 1;

	public static final int GROUP_BY_MONTH = 2;

	public static final int GROUP_BY_MONTH_AND_YEAR = 3;

	public static final int GROUP_BY_QUARTER = 4;

	public static final int GROUP_BY_QUARTER_AND_YEAR = 5;

	public static final int GROUP_BY_YEAR = 6;

	public static final int LIST_MOUSE_X_MAX = 24;

	private static Object emptyReport = null;

	protected TableModel m = null;

	protected ResourceBundle res = null;

	protected java.util.List templates = null;

	protected String pageTitle = null;

	protected String dscr = "";

	protected String user = null;

	protected String preferenceKey = null;

	protected ApplicationPreferences preferences = null;

	public ReportUtils(final TableModel m, final String titPag, final ResourceBundle res, final java.util.List templateList, final String descr) {
		this(m, titPag, res, templateList, descr, null, null, null);
	}

	public ReportUtils(final TableModel m, final String pageTitle, final ResourceBundle res, final java.util.List templateList, final String descr,
			final String user, final String preferenceKey,
			final ApplicationPreferences prefs) {

		this.m = m;
		this.pageTitle = pageTitle;
		this.res = res;
		if (templateList == null) {
			this.templates = templateList;
		} else {
			this.templates = templateList;
		}

		if (descr != null) {
			this.dscr = descr;
		}
		this.user = user;
		this.preferenceKey = preferenceKey;
		this.preferences = prefs;
	}

	public void setModel(final TableModel m) {
		this.m = m;
	}

	public void setResourceBundle(final ResourceBundle res) {
		this.res = res;
	}

	public DefaultReportDialog createDefaultDialog(final Component c, final String reportDescription) {
		ReportUtils.logger.debug("{} default dialog asked", this.getClass().getName());
		DefaultReportDialog defaultDialog = null;
		if (defaultDialog == null) {
			ReportUtils.logger.debug("{} creating default dialog", this.getClass().getName());

			final Window w = SwingUtilities.getWindowAncestor(c);
			if (c instanceof Table) {
				if (w instanceof Frame) {
					defaultDialog = new DefaultReportDialog((Frame) w, this.m, this.res, this.templates, this.pageTitle,
							this.dscr, this.user, this.preferenceKey, this.preferences,
							(Table) c);
				} else {
					defaultDialog = new DefaultReportDialog((Dialog) w, this.m, this.res, this.templates,
							this.pageTitle, this.dscr, this.user, this.preferenceKey,
							this.preferences, (Table) c);
				}
			} else {
				if (w instanceof Frame) {
					defaultDialog = new DefaultReportDialog((Frame) w, this.m, this.res, this.templates, this.pageTitle,
							this.dscr, this.user, this.preferenceKey,
							this.preferences);
				} else {
					defaultDialog = new DefaultReportDialog((Dialog) w, this.m, this.res, this.templates,
							this.pageTitle, this.dscr, this.user, this.preferenceKey,
							this.preferences);
				}
			}

			defaultDialog.setReportDescription(reportDescription);
			defaultDialog.pack();
			defaultDialog.center();
		}
		return defaultDialog;

	}

	/**
	 *
	 */
	public static void showDefaultReportDesigner(final Frame f, final ReportStore[] rs, final EntityReferenceLocator referenceLocator,
			final ResourceBundle res, final java.util.List template, final String tit,
			final String descr) {
		final DefaultReportDialog defaultReport = new DefaultReportDialog(f, rs, referenceLocator, res, template, tit, descr);
		defaultReport.pack();
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		defaultReport.setSize(screenSize.width - 80, screenSize.height - 100);
		defaultReport.center();
		defaultReport.setVisible(true);
	}

	/**
	 * Shows a dialog to configure the chart of reports for a table. It is possible to choose the
	 * series, the x axis column, the y axis column and the type of chart
	 * @param c
	 * @param reportDescription
	 */
	public void showDefaultReportDialog(final Component c, final String reportDescription) {
		this.showDefaultReportDialog(c, reportDescription, null);
	}

	public void showDefaultReportDialog(final DefaultReportDialog reportDialog, final String configuration) {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		reportDialog.setSize(screenSize.width - 80, screenSize.height - 100);

		reportDialog.setResourceBundle(this.res);
		reportDialog.getReportEngine().setTitleReport("");
		reportDialog.getReportEngine().setReportDescription("");
		reportDialog.updateReport();

		if (configuration != null) {
			reportDialog.loadConfiguration(configuration);
		}
		reportDialog.center();
		reportDialog.setVisible(true);
	}

	public void showDefaultReportDialog(final Component c, final String reportDescription, final String configuration) {
		final DefaultReportDialog reportDialog = this.createDefaultDialog(c, reportDescription);
		this.showDefaultReportDialog(reportDialog, configuration);
	}

	public void showDefaultReportDialog(final Component c) {
		this.showDefaultReportDialog(c, null);
	}

	public static URL getDefaultBase() {
		try {
			final String tmpDir = System.getProperty("java.io.tmpdir", ".");
			final String out = tmpDir.endsWith(System.getProperty("file.separator", "/")) ? tmpDir + "out.xml"
					: tmpDir + System.getProperty("file.separator", "/") + "out.xml";
			return new File(out).toURL();
		} catch (final Exception e) {
			ReportUtils.logger.trace(null, e);
			return null;
		}
	}

	/**
	 * Sets the report template list for the custom reports.
	 * @param list a <code>List</code> with the list of template paths.
	 */
	public static void setCustomReportTemplates(final java.util.List list) {
		ReportUtils.templateList = list;
	}

	public static String getTranslation(final String sText, final ResourceBundle res, final Object[] args) {
		if (res == null) {
			return new String(sText);
		} else {
			try {
				final String trad = res.getString(sText);

				// Args
				if (args != null) {
					final String tradArgs = MessageFormat.format(trad, args);
					return tradArgs;
				} else {
					return trad;
				}
			} catch (final Exception e) {
				ReportUtils.logger.trace(null, e);
				final StringBuilder sb = new StringBuilder();
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						sb.append(args[i] + " ");
					}
				}
				ReportUtils.logger.debug("{} --> argumentos: {}", e.getMessage(), sb);
				return new String(sText);
			}
		}
	}

	public static List getCustomReportTemplates() {
		return ReportUtils.templateList;
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final String xMLTemplate, final URL base,
			final ReportProcessor rp) throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, xMLTemplate, base, rp);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final String template, final URL base)
			throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, template, base);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final ReportProcessor rp) throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, template, base, rp);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base)
			throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, template, base);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc) throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base, order, asc);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor rp)
					throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, template, base, order, asc, rp);
	}

	public static PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor rp, final PageFormat pf)
					throws Exception {
		return ReportManager.getReportEngine().showPreviewDialog(c, title, m, template, base, order, asc, rp, pf);
	}

	public static PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final ReportProcessor r) throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base, r);
	}

	public static PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base)
			throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base);
	}

	public static PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc) throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base, order, asc);
	}

	public static PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor r)
					throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base, order, asc, r);
	}

	public static PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor r, final PageFormat pf)
					throws Exception {
		return ReportManager.getReportEngine().getPreviewDialog(c, title, m, template, base, order, asc, r, pf);
	}

}
