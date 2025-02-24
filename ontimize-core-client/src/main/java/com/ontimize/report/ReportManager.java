package com.ontimize.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.report.engine.dynamicjasper.DynamicJasperEngine;

public class ReportManager {

	private static final Logger logger = LoggerFactory.getLogger(ReportManager.class);

	private static ReportEngine reportEngine;

	public static boolean createReportEngine() {
		try {
			Class.forName("ar.com.fdvs.dj.domain.DynamicReport");
		} catch (final Exception err) {
			ReportManager.logger.trace(null, err);
			ReportManager.logger.error("ReportManager: No DynamicJasper engines registered");
			return false;
		}

		// use new dynamic jasper 5.0.0
		ReportManager.reportEngine = new DynamicJasperEngine();

		if (ReportManager.reportEngine.checkLibraries()) {
			ReportManager.logger.info("Report engine: {} succesfully registered",
					ReportManager.reportEngine.getReportEngineName());
			return true;
		} else {
			ReportManager.logger.warn("Report engine found: {} but missing some required libraries.",
					ReportManager.reportEngine.getReportEngineName());
			return false;
		}
	}

	public static void registerNewReportEngine(final ReportEngine reportEngine) {
		ReportManager.reportEngine = reportEngine;
		if (reportEngine.checkLibraries()) {
			ReportManager.logger.info("Report engine: {} succesfully registered", reportEngine.getReportEngineName());
		} else {
			ReportManager.logger.warn("Report engine found: {} but missing some required libraries.",
					reportEngine.getReportEngineName());
		}
	}

	public static synchronized boolean isReportsEnabled() {
		try {
			if (ReportManager.reportEngine == null) {
				return ReportManager.createReportEngine();
			}
			return true;
		} catch (final Exception e) {
			ReportManager.logger.error("Report libraries are not available:", e);
			return false;
		}
	}

	public static ReportEngine getReportEngine() throws Exception {
		if (!ReportManager.isReportsEnabled()) {
			throw new Exception("ReportManager: No report engine configured. You must check libraries.");
		}
		return ReportManager.reportEngine;
	}

}
