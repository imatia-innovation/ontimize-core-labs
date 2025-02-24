package com.ontimize.util.xls;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.windows.office.ExecutionResult;
import com.ontimize.windows.office.ScriptUtilities;

public class ClipboardXLSExporterUtils implements XLSExporter {

	private static final Logger logger = LoggerFactory.getLogger(ClipboardXLSExporterUtils.class);

	public ClipboardXLSExporterUtils() {
		// empty constructor
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final Map hColumnRenderers, final List columnSort,
			final boolean writeHeader, final boolean xlsx, final boolean openFile)
					throws Exception {
		if (!openFile) {
			ClipboardXLSExporterUtils.logger.debug("ClipboardXLSExporterUtils --> openFile=false is not available");
		}
		final StringSelection sselection = new StringSelection(this.getXLSString(rs, writeHeader, columnSort));
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
		// Generates the excel
		final File fScript = ScriptUtilities
				.createTemporalFileForScript("com/ontimize/windows/office/scripts/excelpaste.vbs");
		final List vParameters = new Vector();
		vParameters.add(output.getPath());
		final ExecutionResult res = ScriptUtilities.executeScript(fScript.getPath(), vParameters, ScriptUtilities.WSCRIPT);
		if (res.getResult() != 0) {
			throw new Exception("M_XLS_FILE_ERROR");
		}
	}

	public String getXLSString(final EntityResult res, final boolean writeHeader, final List columnSort) {
		// Creates a string with all the object data
		// Export to excel: columns separated with tab and rows with enter.

		final StringBuilder header = new StringBuilder();
		final Enumeration keysEnum = res.keys();

		List keyNames = new Vector();
		while (keysEnum.hasMoreElements()) {
			final Object name = keysEnum.nextElement();
			if (writeHeader) {
				header.append(name + "\t");
			}
			keyNames.add(name);
		}

		if (columnSort != null) {
			keyNames = columnSort;
		}

		final StringBuilder sbValues = new StringBuilder("");
		for (int j = 0; j < res.calculateRecordNumber(); j++) {
			sbValues.append("\n");
			final Map record = res.getRecordValues(j);
			for (int i = 0; i < keyNames.size(); i++) {
				final Object oValue = record.get(keyNames.get(i));
				final String sText = oValue != null ? oValue.toString() : "";
				sbValues.append("\"");
				sbValues.append(sText);
				sbValues.append("\"");
				sbValues.append("\t");
			}
		}
		sbValues.append("\n");
		return header + sbValues.toString();
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final Map hColumnRenderers, final List columnSort,
			final List columnStyles, final List columnHeaderStyles, final Workbook wb,
			final boolean xlsx, final boolean writeHeader, final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, hColumnRenderers, columnSort, writeHeader, false, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final Map hColumnRenderers, final List columnSort,
			final boolean writeHeader, final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, hColumnRenderers, columnSort, writeHeader, false, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final Map hColumnRenderers, final List columnSort,
			final List columnStyles, final List columnHeaderStyles, final Workbook wb,
			final boolean writeHeader, final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, hColumnRenderers, columnSort, columnStyles, columnHeaderStyles, wb, false,
				writeHeader, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final List columnSort, final boolean writeHeader,
			final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, new Hashtable(), columnSort, writeHeader, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final List columnSort, final boolean writeHeader,
			final boolean xlsx, final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, new Hashtable(), columnSort, writeHeader, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final List columnSort, final List columnStyles,
			final List columnHeaderStyles, final Workbook wb, final boolean writeHeader,
			final boolean xlsx, final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, new Hashtable(), columnSort, columnStyles, columnHeaderStyles, wb, xlsx,
				writeHeader, openFile);
	}

	@Override
	public void createXLS(final EntityResult rs, final File output, final String sheetName, final List columnSort, final List columnStyles,
			final List columnHeaderStyles, final Workbook wb, final boolean writeHeader,
			final boolean openFile) throws Exception {
		this.createXLS(rs, output, sheetName, new Hashtable(), columnSort, columnStyles, columnHeaderStyles, wb,
				writeHeader, openFile);
	}

}
