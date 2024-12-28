package com.ontimize.windows.office;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.printing.server.ReportGenerator;

/**
 * Uses a configuration properties file. It must be a key 'db' and the value must be the complete
 * name with the file and path of the database file. Keys in the properties file must be DB'#number'
 * (db1, db2, etc) and values are the names of the database to use including the path..
 */

public class MSAccessRG implements ReportGenerator {

	private static final Logger logger = LoggerFactory.getLogger(MSAccessRG.class);

	private static String description = "Microsoft Access 2000 Report Generator";

	private final Map fileReportDB = new Hashtable();

	private final ScriptUtilities su = new ScriptUtilities();

	private final List reportList = new Vector();

	private String basePath = null;

	private final List synchronizedReport = new Vector();

	private final List synchronizedReportsUse = new Vector();

	public MSAccessRG(final String propertiesFile) throws Exception {
		final URL uRLProp = this.getClass().getClassLoader().getResource(propertiesFile);
		if (uRLProp == null) {
			MSAccessRG.logger.debug(this.getClass().toString() + ". Properties file not found: " + propertiesFile);
			throw new Exception(this.getClass().toString() + ". Properties file not found " + propertiesFile);
		}
		final int index = propertiesFile.lastIndexOf("/");
		if (index >= 0) {
			this.basePath = propertiesFile.substring(0, index);
		} else {
			this.basePath = "";
		}
		MSAccessRG.logger.debug("Report properties files must be in: " + this.basePath);
		final Properties prop = new Properties();
		prop.load(uRLProp.openStream());
		final Object sincr = prop.get("sincronizar");
		if (sincr != null) {
			final StringTokenizer st = new StringTokenizer(sincr.toString(), ";");
			while (st.hasMoreElements()) {
				this.synchronizedReport.add(st.nextToken());
			}
		}
		// Creates the list with the databases and their reports
		final Enumeration e = prop.propertyNames();
		while (e.hasMoreElements()) {
			final String sDB = e.nextElement().toString();
			if (sDB.indexOf("BD") >= 0) {
				// Gets the database file
				final String sFile = prop.getProperty(sDB);
				// Asks for the report list
				final List vReportList = this.getReportList(sFile);
				if (vReportList == null) {
					MSAccessRG.logger.debug("Report list has not been created for: " + sFile);
				} else {
					for (int i = 0; i < vReportList.size(); i++) {
						final Object oReport = vReportList.get(i);
						if ((oReport == null) || (sFile == null)) {
							continue;
						}
						if (this.reportList.contains(oReport)) {
							MSAccessRG.logger.debug(" -----> Duplicated report: " + oReport + ". DB: " + sFile);
						} else {
							this.reportList.add(oReport);
							this.fileReportDB.put(oReport, sFile);
						}
					}
				}
			}
		}
	}

	public List getReportList(final String databse) {
		try {
			return this.su.listAccessDBReports(databse);
		} catch (final Exception e) {
			MSAccessRG.logger.error(null, e);
			return null;
		}
	}

	@Override
	public List getReportList() {
		return this.reportList;
	}

	@Override
	public List getReportDescription() {
		final List vList = new Vector();
		return vList;
	}

	@Override
	public String getDescription() {
		return MSAccessRG.description;
	}

	@Override
	public String createReport(final String name, final Object params, String fileName) {
		synchronized (this.synchronizedReport) {
			if (this.synchronizedReport.contains(name)) {
				synchronized (this.synchronizedReportsUse) {
					if (this.synchronizedReportsUse.contains(name)) {
						throw new IllegalArgumentException(
								"Reports generation: Requested report has been request for another user. Simultaneous requests are not allowed. Try again later");
					} else {
						this.synchronizedReportsUse.add(name);
					}
				}
			}
		}

		MSAccessRG.logger.debug(this.getClass().toString() + ":  Processing report request: " + name + " , " + params);
		if (!this.reportList.contains(name)) {
			MSAccessRG.logger.debug(this.getClass().toString() + ": Report not found: " + name);
			return ReportGenerator.REPORT_NOT_FOUND;
		}

		final String sDBFile = this.fileReportDB.get(name).toString();
		try {
			if (fileName.indexOf(".") <= 0) {
				// Extension not found
				fileName = fileName + ".snp";
			} else {
				final int index = fileName.lastIndexOf(".");
				if (!".snp".equalsIgnoreCase(fileName.substring(index))) {
					fileName = fileName + ".snp";
				}
			}
			final File f = new File(fileName);
			if (f.exists()) {
				// File already exists:
				MSAccessRG.logger.debug("File exists: " + f.toString());
				// Delete it
				final long t = System.currentTimeMillis();
				while (!f.delete()) {
					try {
						Thread.sleep(1000);
						MSAccessRG.logger.debug("Waiting to remove the file: " + f.toString());
					} catch (final Exception e) {
						MSAccessRG.logger.error(null, e);
					}
					if ((System.currentTimeMillis() - t) > 10000) {
						MSAccessRG.logger
						.debug(this.getClass().toString() + ": End of the waiting time to remove the file.");
						return ReportGenerator.ERROR;
					}
				}
			} else {
				MSAccessRG.logger.debug("File not found: " + f.toString());
			}
			if (params instanceof String) {
				if (this.su.report2Snapshot(sDBFile, name, f.toString(), (String) params)) {
					MSAccessRG.logger.debug(this.getClass().toString() + ": Returning report: " + f.getName());
					return f.getName();
				} else {
					MSAccessRG.logger.debug("Error generating report: " + name);
					return ReportGenerator.ERROR;
				}
			} else if (params instanceof Map) {
				// Search the report properties. It must be in the same
				// directory
				// that the general properties file
				final String path = this.basePath + name + ".properties";
				final URL urlPropInf = this.getClass().getClassLoader().getResource(path);
				if (urlPropInf == null) {
					MSAccessRG.logger.debug("Report properties file not found: " + path);
					return ReportGenerator.ERROR;
				}
				final Properties reportProperties = new Properties();
				reportProperties.load(urlPropInf.openStream());
				final String script = reportProperties.getProperty("script");
				if (script == null) {
					MSAccessRG.logger.debug("Script parameter not found in the report properties file: " + path);
					return ReportGenerator.ERROR;
				}
				final String parameters = reportProperties.getProperty("parameters");
				if (parameters == null) {
					MSAccessRG.logger.debug("Parameter 'parameters' not found in the report properties file: " + path);
					return ReportGenerator.ERROR;
				}
				final List vParameterNames = new Vector();
				final StringTokenizer st = new StringTokenizer(parameters.toString(), ";");
				while (st.hasMoreTokens()) {
					vParameterNames.add(vParameterNames.size(), st.nextElement());
				}
				// Execute the script
				final List vParameters = new Vector();
				vParameters.add(0, sDBFile);
				vParameters.add(1, name);
				vParameters.add(2, f);
				for (int i = 0; i < vParameterNames.size(); i++) {
					final Object oName = vParameterNames.get(i);
					final Object oValue = ((Map) params).get(oName);
					vParameters.add(vParameters.size(), oValue);
				}
				final URL url = this.getClass().getResource("scripts/" + script + ".vbs");
				if (url == null) {
					MSAccessRG.logger
					.debug("Script not found: " + script + ".vbs in com/ontimize/windows/office/scripts/");
					return ReportGenerator.ERROR;
				}
				final File fScript = new File(url.getFile());
				final ExecutionResult res = ScriptUtilities.executeScript(fScript.toString(), vParameters);
				if (res.getResult() == 0) {
					return f.getName();
				} else {
					MSAccessRG.logger.debug("Error generating report " + name + " : " + res.getOuput());
					return ReportGenerator.ERROR;
				}
			} else {
				MSAccessRG.logger.debug("Params must be String or Hashtable");
				return ReportGenerator.ERROR;
			}
		} catch (final Exception e) {
			MSAccessRG.logger.error(null, e);
			return ReportGenerator.ERROR;
		} finally {
			synchronized (this.synchronizedReportsUse) {
				if (this.synchronizedReportsUse.contains(name)) {
					this.synchronizedReportsUse.remove(name);
				}
			}
		}
	}

}
