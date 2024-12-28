package com.ontimize.util.xls;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.ontimize.jee.common.dto.EntityResult;

public interface XLSExporter {

	/**
	 * Creates a *.xls file with the {@link EntityResult} content in the {@link File} indicated. If the
	 * parameter {@code openFile} is {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param columnSort {@link List} List with the ordered columns
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, List columnSort, boolean writeHeader,
			boolean openFile) throws Exception;

	/**
	 * Creates a *.xls or *.xslx file with the {@link EntityResult} content in the {@link File}
	 * indicated depending on whether the {@code xslx} parameter is {@code true} or not. If the
	 * parameter {@code openFile} is {@code true}, opens the file at the end of the export.
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param columnSort {@link List} List with the ordered columns
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param xlsx {@link Boolean} Boolean indicating whether the export is in *.xsl or *.xslx
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, List columnSort, boolean writeHeader,
			boolean xlsx, boolean openFile) throws Exception;

	/**
	 * Creates a *.xls file with the {@link EntityResult} content in the {@link File} indicated. This
	 * contains a specific {@link Workbook} and the name of the sheet. If the parameter {@code openFile}
	 * is {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param columnSort {@link List} List with the ordered columns
	 * @param columnStyles {@link List} List with the styles of the column
	 * @param columnHeaderStyles {@link List} List with the styles of the column headers
	 * @param wb {@link Workbook} Workbook to which the spreadsheet belongs
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param xlsx {@link Boolean} Boolean indicating whether the export is in *.xsl or *.xslx
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, List columnSort, List columnStyles,
			List columnHeaderStyles, Workbook wb, boolean writeHeader,
			boolean xlsx, boolean openFile) throws Exception;

	/**
	 * Creates a *.xls file with the {@link EntityResult} content in the {@link File}. This contains a
	 * specific {@link Workbook} and the name of the sheet. If the parameter {@code openFile} is
	 * {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param columnSort {@link List} List with the ordered columns
	 * @param columnStyles {@link List} List with the styles of the column
	 * @param columnHeaderStyles {@link List} List with the styles of the column headers
	 * @param wb {@link Workbook} Workbook to which the spreadsheet belongs
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, List columnSort, List columnStyles,
			List columnHeaderStyles, Workbook wb, boolean writeHeader,
			boolean openFile) throws Exception;

	// Next 4 methods added in 5.2062EN in order to format currency columns
	// according to renderers

	/**
	 * Creates a *.xls file with the {@link EntityResult} content in the {@link File} indicated. If the
	 * parameter {@code openFile} is {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param hColumnRenderers {@link Hashtable} Map with the column renderers
	 * @param columnSort {@link List} List with the ordered columns
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, Map hColumnRenderers, List columnSort,
			boolean writeHeader, boolean openFile) throws Exception;

	public void createXLS(EntityResult rs, File output, String sheetName, Map hColumnRenderers, List columnSort,
			boolean writeHeader, boolean xlsx, boolean openFile)
					throws Exception;

	/**
	 * Creates a *.xls or *.xslx file with the {@link EntityResult} content in the {@link File}
	 * indicated depending on whether the {@code xslx} parameter is {@code true} or not. If the
	 * parameter {@code openFile} is {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param hColumnRenderers {@link Hashtable} Map with the column renderers
	 * @param columnSort {@link List} List with the ordered columns
	 * @param columnStyles {@link List} List with the styles of the column
	 * @param columnHeaderStyles {@link List} List with the styles of the column headers
	 * @param wb {@link Workbook} Workbook to which the spreadsheet belongs
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param xlsx {@link Boolean} Boolean indicating whether the export is in *.xsl or *.xslx
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 *
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, Map hColumnRenderers, List columnSort,
			List columnStyles, List columnHeaderStyles, Workbook wb,
			boolean writeHeader, boolean xlsx, boolean openFile) throws Exception;

	/**
	 * Creates a *.xls file with the {@link EntityResult} content in the {@link File} indicated. This
	 * contains a specific {@link Workbook} and the name of the sheet. If the parameter {@code openFile}
	 * is {@code true}, opens the file at the end of the export
	 * @param rs {@link EntityResult} EntityResult with the data to export
	 * @param output {@link File} The file where the documente will be saved
	 * @param sheetName {@link String} The name of the sheet
	 * @param hColumnRenderers {@link Hashtable} Map with the column renderers
	 * @param columnSort {@link List} List with the ordered columns
	 * @param columnStyles {@link List} List with the styles of the column
	 * @param columnHeaderStyles {@link List} List with the styles of the column headers
	 * @param wb {@link Workbook} Workbook to which the spreadsheet belongs
	 * @param writeHeader {@link Boolean} Boolean indicating whether to write column headers
	 * @param openFile {@link Boolean} Boolean indicating whether to open the file at the end of the
	 *        export or not.
	 * @throws Exception
	 */
	public void createXLS(EntityResult rs, File output, String sheetName, Map hColumnRenderers, List columnSort,
			List columnStyles, List columnHeaderStyles, Workbook wb,
			boolean writeHeader, boolean openFile) throws Exception;

}
