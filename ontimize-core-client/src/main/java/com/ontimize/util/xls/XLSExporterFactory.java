package com.ontimize.util.xls;

import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;

/**
 * Class that manages objects that export to excel in Table.
 *
 * User must set the next variable: Table.XLS_EXPORT_CLASS to choose the engine to export to Excel:
 *
 * @see Table#XLS_EXPORT_CLASS
 * @author Imatia Innovation SL
 */
public abstract class XLSExporterFactory {

	private static final Logger logger = LoggerFactory.getLogger(XLSExporterFactory.class);


	public static final String	POI						= "poi";

	public static final String CLIPBOARD = "clipboard";

	public static String defaultExporter = XLSExporterFactory.CLIPBOARD;

	protected static Map xlsExporterInstances = new Hashtable();

	protected static String errorMessage;

	public static XLSExporter instanceXLSExporter(final String type) {
		if (XLSExporterFactory.xlsExporterInstances.containsKey(type)) {
			return (XLSExporter) XLSExporterFactory.xlsExporterInstances.get(type);
		} else if (type.equals(XLSExporterFactory.POI) && XLSExporterFactory.isPOILibraryAvailable()) {
			if (!XLSExporterFactory.xlsExporterInstances.containsKey(type)) {
				XLSExporterFactory.xlsExporterInstances.put(type, new Poi5XLSExporterUtils());
			}
			return (XLSExporter) XLSExporterFactory.xlsExporterInstances.get(type);
		} else if (type.equals(XLSExporterFactory.CLIPBOARD)) {
			if (!XLSExporterFactory.xlsExporterInstances.containsKey(type)) {
				XLSExporterFactory.xlsExporterInstances.put(type, new ClipboardXLSExporterUtils());
			}
			return (XLSExporter) XLSExporterFactory.xlsExporterInstances.get(type);
		} else {
			XLSExporterFactory.logger.debug("Type " + type + " is not available. Return default type --> "
					+ XLSExporterFactory.defaultExporter);
			return XLSExporterFactory.instanceXLSExporter(XLSExporterFactory.defaultExporter);
		}
	}

	public static void registerXLSExporter(final String type, final XLSExporter exporterObject) {
		XLSExporterFactory.xlsExporterInstances.put(type, exporterObject);
	}

	/**
	 * Method that checks whether poi 2.0 is available.
	 * @return <code>true</code> when poi library is available.
	 */
	public static boolean isPOILibraryAvailable() {
		try {
			Class.forName("org.apache.poi.hssf.usermodel.HSSFSheet");
			return true;
		} catch (final Exception e) {
			XLSExporterFactory.logger.trace(null, e);
			return false;
		}
	}

	public static Object createXSSFWorkbook() {
		Class classObject = null;
		try {
			classObject = Poi5XLSExporterUtils.class.getClassLoader()
					.loadClass("org.apache.poi.xssf.usermodel.XSSFWorkbook");
		} catch (final Exception e) {
			XLSExporterFactory.logger.error(null, e);
		}
		return classObject;
	}

	/**
	 * Check required libraries to allow .xlsx export.
	 * @return
	 */
	public static boolean isAvailableXLSX() {
		try {
			// Poi 3.5 or higher
			Class.forName("org.apache.poi.xssf.usermodel.XSSFWorkbook");
			// poi-ooxml 3.5 or higher (version of this library should match
			// with version of Poi library)
			Class.forName("org.apache.poi.ooxml.POIXMLDocument");
			// xmlbeans-2.3.0
			Class.forName("org.apache.xmlbeans.Filer");
			// dom4j-1.6.1
			Class.forName("org.dom4j.XPath");
			// ooxml-schemas-1.0
			XLSExporterFactory.isSTCFAvailable();
			return true;
		} catch (final Exception e) {
			XLSExporterFactory.logger.debug(null, e);
			XLSExporterFactory.errorMessage = e.getMessage();
			return false;
		}
	}

	public static boolean isSTCFAvailable() throws Exception {
		try {
			// ooxml-schemas-1.3
			Class.forName("com.microsoft.schemas.office.excel.STCF");
			return true;
		} catch (final Exception e2) {
			XLSExporterFactory.logger.debug(null, e2);
			XLSExporterFactory.errorMessage = e2.getMessage();
			throw e2;
		}
	}

	public static String getErrorMessage() {
		return XLSExporterFactory.errorMessage;
	}

}
