package com.ontimize.jee.webclient.export.support.exporter;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.ontimize.jee.webclient.export.CellStyleContext;
import com.ontimize.jee.webclient.export.ExportColumn;
import com.ontimize.jee.webclient.export.ExportColumnStyle;
import com.ontimize.jee.webclient.export.Exporter;
import com.ontimize.jee.webclient.export.HeadExportColumn;
import com.ontimize.jee.webclient.export.exception.ExportException;
import com.ontimize.jee.webclient.export.providers.ExportColumnProvider;
import com.ontimize.jee.webclient.export.providers.ExportDataProvider;
import com.ontimize.jee.webclient.export.providers.ExportStyleProvider;
import com.ontimize.jee.webclient.export.style.PdfCellStyle;
import com.ontimize.jee.webclient.export.style.PdfDataFormat;
import com.ontimize.jee.webclient.export.style.support.DefaultPdfDataFormat;
import com.ontimize.jee.webclient.export.style.support.DefaultPdfPCellStyle;
import com.ontimize.jee.webclient.export.style.util.PdfCellStyleUtils;
import com.ontimize.jee.webclient.export.support.DefaultExportColumnStyle;
import com.ontimize.jee.webclient.export.support.DefaultHeadExportColumn;
import com.ontimize.jee.webclient.export.util.ExportOptions;

/**
 * Exportador base, que exporta a partir de un contexto y unos providers. Migrado a OpenPDF (PdfPTable/PdfPCell).
 */
public abstract class BasePdfExporter<T extends Document> implements Exporter<T> {

	private final DefaultExportColumnStyle	exportColumnStyle	= new DefaultExportColumnStyle();

	protected final PdfDataFormat pdfDataFormat;

	protected final File pdfFile;

	public BasePdfExporter(final File pdfFile) {
		this.pdfFile = pdfFile;
		this.pdfDataFormat = new DefaultPdfDataFormat();
	}

	public File getPdfFile() {
		return this.pdfFile;
	}

	public DefaultExportColumnStyle getExportColumnStyle() {
		return this.exportColumnStyle;
	}

	/**
	 * Obtenemos primero los posibles estilos que pueden venir con las definiciones
	 * de columnas. La forma de asignar estilo es mediante el StyleProvider. Estos
	 * estilos complementan a los que tuvieran las columnas. En el StyleProvider hay
	 * tres estilos: - Cabecera: estilo por defecto para las cabeceras. - Columna:
	 * estilo de todas las celdas de una columna. - Celda: estilo para celdas
	 * concretas. DefaultStyleProvider proporciona por defecto unos estilos para
	 * todas las cabeceras y ademÃ¡s unos por tipo para todas las columnas. Se pueden
	 * sobreescribir
	 */
	@Override
	public T export(final ExportColumnProvider exportColumnProvider, final ExportDataProvider dataProvider,
			final ExportStyleProvider styleProvider, ExportOptions exportOptions, final boolean landscape) throws ExportException {

		if (exportOptions == null) {
			exportOptions = new ExportOptions();
		}
		try (final FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);) {
			final T document = this.buildDocument(landscape);

			// Nota: la creación real del PdfWriter/PdfDocument se hace en buildDocument()
			// y el documento ya debe estar abierto cuando se llama a export. Aquí se trabaja
			// sobre la tabla que se añade al documento.
			final PdfPTable pdfTable = this.createPdfTable(exportColumnProvider);
			// add table to document (document should accept it)
			document.add(pdfTable);

			final Map<String, PdfCellStyle> headerCellStylesById = this
					.addHeaderStyles(exportColumnProvider.getHeaderColumns(), styleProvider);
			this.addHeader(pdfTable, exportColumnProvider.getHeaderColumns(), headerCellStylesById, styleProvider,
					exportOptions);
			final Map<String, PdfCellStyle> bodyCellStyles = this.addBodyStyles(exportColumnProvider.getBodyColumns(),
					dataProvider, styleProvider);
			this.addBody(pdfTable, exportColumnProvider.getBodyColumns(), dataProvider, bodyCellStyles, styleProvider,
					exportOptions);

			// El documento se cierra en buildDocument o por el llamador.
			document.close();
			return document;
		} catch (final IOException e) {
			throw new ExportException("Impossible to create export document", e);
		}
	}

	public PdfPTable createPdfTable(final ExportColumnProvider exportColumnProvider) {
		int numCols = Math.max(exportColumnProvider.getHeaderColumns().size(),
				exportColumnProvider.getBodyColumns().size());
		if (numCols <= 0) {
			numCols = 1;
		}
		final PdfPTable table = new PdfPTable(numCols);
		table.setWidthPercentage(100f);
		table.getDefaultCell().setBorder(Rectangle.NO_BORDER);
		return table;
	}

	public void addHeader(final PdfPTable pdfTable, final List<HeadExportColumn> userHeaderColumns,
			final Map<String, PdfCellStyle> headerCellStylesById, final ExportStyleProvider styleProvider,
			final ExportOptions exportOptions) {
		if (userHeaderColumns != null) {
			this.createHeader(pdfTable, userHeaderColumns, headerCellStylesById, styleProvider, exportOptions);
			// Indica que las primeras filas añadidas son cabecera (1 fila por defecto)
			pdfTable.setHeaderRows(1);
		}
	}

	public void addCell(final PdfPTable pdfTable, final ExportColumn exportColumn, final int rowIndex, final int colUserIndex,
			final Object value, final ExportStyleProvider styleProvider,
			final Map<String, PdfCellStyle> bodyCellStyles) {
		final PdfPCell cell = this.createCell(exportColumn, rowIndex, colUserIndex, value, styleProvider, bodyCellStyles);
		if (cell != null) {
			pdfTable.addCell(cell);
		}
	}

	protected void addBody(final PdfPTable pdfTable, final List<ExportColumn> userColumns,
			final ExportDataProvider dataProvider,
			final Map<String, PdfCellStyle> bodyCellStyles, final ExportStyleProvider styleProvider,
			final ExportOptions exportOptions) {

		// Para cada fila...
		IntStream.range(0, dataProvider.getNumberOfRows()).forEach(rowIndex -> {
			userColumns.stream().forEach(column -> {
				final Object value = dataProvider.getCellValue(rowIndex, column.getId());
				this.addCell(pdfTable, column, rowIndex, userColumns.indexOf(column), value, styleProvider,
						bodyCellStyles);
			});
		});
	}

	protected PdfPCell createCell(final ExportColumn exportColumn, final int row, final int colUserIndex,
			final Object cellValue, final ExportStyleProvider styleProvider,
			final Map<String, PdfCellStyle> bodyCellStyles) {

		final PdfPCell cell = new PdfPCell();
		final PdfCellStyle cellStyle = bodyCellStyles.get(exportColumn.getId());
		this.setCellStyle(cell, exportColumn, row, colUserIndex, cellValue, styleProvider, cellStyle);
		this.assignCellValue(cell, cellValue, cellStyle);
		return cell;
	}

	protected void setCellStyle(final PdfPCell cell, final ExportColumn<ExportColumnStyle> exportColumn, final int row,
			final int colUserIndex, final Object cellValue,
			final ExportStyleProvider<PdfCellStyle, PdfDataFormat> styleProvider, final PdfCellStyle cellStyle) {

		cell.setBorder(Rectangle.NO_BORDER);
		cell.setPaddingLeft(8.0f);
		cell.setPaddingRight(8.0f);

		/*
		 * Primero asignamos el estilo general de esa columna
		 */
		PdfCellStyle cellStyle1 = cellStyle;
		if (cellStyle1 == null) {
			cellStyle1 = new DefaultPdfPCellStyle();
		}
		updateCellStyle(cell, cellStyle1);

		/*
		 * Luego, si existe un estilo para la celda en el styleProvider, se asigna
		 */
		final PdfCellStyle userStyle = styleProvider.getCellStyle(
				new CellStyleContext<>(row, colUserIndex, exportColumn.getId(), cellValue, cellStyle1, () -> {
					final PdfCellStyle ret = new DefaultPdfPCellStyle();
					ret.cloneStyleFrom(cellStyle);
					return ret;
				}, () -> new DefaultPdfDataFormat()));
		if (userStyle != null) {
			updateCellStyle(cell, userStyle);
		}
	}

	protected void createHeader(final PdfPTable pdfTable, final List<HeadExportColumn> userHeaderColumns,
			final Map<String, PdfCellStyle> headerCellStylesById, final ExportStyleProvider styleProvider,
			final ExportOptions exportOptions) {

		final AtomicInteger columnIndex = new AtomicInteger(0);
		userHeaderColumns.forEach(exportColumn -> {
			final int width = this.createHeaderCell(pdfTable, exportColumn, headerCellStylesById, styleProvider,
					exportOptions);
			columnIndex.addAndGet(width);
		});
	}

	protected int createHeaderCell(final PdfPTable pdfTable, final HeadExportColumn column,
			final Map<String, PdfCellStyle> headerCellStylesById, final ExportStyleProvider styleProvider,
			final ExportOptions exportOptions) {

		final PdfPCell headerCell = new PdfPCell();

		headerCell.addElement(
				new Paragraph(column.getTitle(), FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));

		// Default style
		headerCell.setPaddingLeft(8.0f);
		headerCell.setPaddingRight(8.0f);
		headerCell.setBorder(Rectangle.NO_BORDER);

		// set bottom border
		headerCell.setBorderWidthBottom(1f);
		headerCell.setBorderColorBottom(new Color(204, 204, 204));
		headerCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		if (styleProvider != null) {
			// FIXME estilos de la cabecera (si se requiere)
			// this.applyStyleToHeaderCell(sheet, column, rowIndex, columnIndex,
			// headerCellStylesById, styleProvider,
			// cell);
		}

		final int numFields = 1;
		pdfTable.addCell(headerCell);
		return numFields;
	}

	protected void updateCellStyle(final PdfPCell cell, final PdfCellStyle pdfCellStyle) {

		if (pdfCellStyle == null) {
			return;
		}

		if (pdfCellStyle.getBackgroundColor() != null) {
			cell.setBackgroundColor(pdfCellStyle.getBackgroundColor());
		}

		if (pdfCellStyle.getHorizontalAlignment() != null) {
			cell.setHorizontalAlignment(pdfCellStyle.getHorizontalAlignment());
		}

		if (pdfCellStyle.getVerticalAlignment() != null) {
			cell.setVerticalAlignment(pdfCellStyle.getVerticalAlignment());
		}

	}

	protected abstract T buildDocument(boolean lanscape) throws ExportException;

	protected Map<String, PdfCellStyle> addHeaderStyles(final List<HeadExportColumn> columns,
			final ExportStyleProvider styleProvider) {

		final Map<String, PdfCellStyle> headerCellStylesById = new HashMap<>();
		for (final HeadExportColumn column : columns) {
			this.addHeaderStyles(column, headerCellStylesById, styleProvider);
		}
		return headerCellStylesById;
	}

	protected Map<String, PdfCellStyle> addBodyStyles(final List<ExportColumn> bodyColumns,
			final ExportDataProvider dataProvider, final ExportStyleProvider styleProvider) {

		final Map<String, PdfCellStyle> bodyCellStyles = new HashMap<>();

		bodyColumns.stream().forEach(column -> {
			final Object cellValue = dataProvider.getCellValue(0, column.getId());
			final Class<?> objectClass = cellValue != null ? cellValue.getClass() : Object.class;

			final ExportColumnStyle exportColumnStyle = new DefaultExportColumnStyle();
			/*
			 * Orden de preferencia de los estilos: Primero se asigna el estilo de la celda por defecto para el tipo de
			 * dato que contiene. Luego el estilo para esa columna según el styleProvider
			 */
			exportColumnStyle.set(styleProvider.getColumnStyleByType(objectClass))
					.set(styleProvider.getColumnStyle(column.getId()));
			final PdfCellStyle cellStyle = this.createCellStyle(exportColumnStyle, objectClass);
			bodyCellStyles.put(column.getId(), cellStyle);
		});
		return bodyCellStyles;
	}

	protected void applyStyleToHeaderCell(final HeadExportColumn column, final int rowIndex, final int columnIndex,
			final Map<String, PdfCellStyle> headerCellStylesById, final ExportStyleProvider styleProvider,
			final PdfPCell cell) {

		final PdfCellStyle cellStyle = headerCellStylesById.get(column.getId());
		if (cellStyle == null) {
			return;
		}
		updateCellStyle(cell, cellStyle);

		final PdfCellStyle userStyle = (PdfCellStyle) styleProvider.getHeaderCellStyle(
				new CellStyleContext<>(rowIndex, columnIndex, column.getId(), null, cellStyle, () -> {
					final PdfCellStyle ret = new DefaultPdfPCellStyle();
					ret.cloneStyleFrom(cellStyle);
					return ret;
				}, () -> new DefaultPdfDataFormat()));
		if (userStyle != null) {
			updateCellStyle(cell, userStyle);
		}
	}

	private void assignCellValue(final PdfPCell cell, final Object value, final PdfCellStyle columnStyle) {
		if (value == null) {
			return;
		}

		String aux = String.valueOf(value);
		if (columnStyle != null && columnStyle.getDataFormatter() != null) {
			aux = columnStyle.getDataFormatter().format(value);
		}
		cell.addElement(new Paragraph(aux));
	}

	protected void addHeaderStyles(final HeadExportColumn column, final Map<String, PdfCellStyle> headerCellStylesById,
			final ExportStyleProvider styleProvider) {

		DefaultHeadExportColumn<ExportColumnStyle> defaultHeadExportColumn = null;
		ExportColumnStyle styleFromAnnotations = null;
		/*
		 * Primero extraemos el estilo de las anotaciones de las columnas, si hay alguna
		 */
		if (DefaultHeadExportColumn.class.isAssignableFrom(column.getClass())) {
			defaultHeadExportColumn = (DefaultHeadExportColumn<ExportColumnStyle>) column;
			styleFromAnnotations = defaultHeadExportColumn.getStyle();
		} else if (ExportColumn.class.isAssignableFrom(column.getClass())) {
			final ExportColumn exportColumn = (ExportColumn) column;
			styleFromAnnotations = (ExportColumnStyle) exportColumn.getStyle();
		}

		/*
		 * Si no hay ningún estilo de anotaciones tomamos el estilo de la columna Si hay alguno le asignamos los valores
		 * del estilo de la columna del styleProvider
		 */
		ExportColumnStyle finalStyle = styleFromAnnotations;
		if (styleProvider != null) {
			if (finalStyle == null) {
				finalStyle = styleProvider.getColumnStyle(column.getId());
			} else {
				finalStyle.set(styleProvider.getColumnStyle(column.getId()));
			}
		}

		/*
		 * Guardamos el estilo en un mapa por el id de la columna
		 */
		if (finalStyle != null) {
			headerCellStylesById.put(column.getId(), this.createCellStyle(finalStyle, String.class));
		}
		/*
		 * Si la columna tiene columnas anidadas hacemos lo mismo con cada una
		 */
		if (column.getHeadExportColumnCount() > 0) {
			for (int n = 0; n < column.getHeadExportColumnCount(); n++) {
				this.addHeaderStyles(column.getHeadExportColumn(n), headerCellStylesById, styleProvider);
			}
		}
	}

	protected PdfCellStyle createCellStyle(final ExportColumnStyle columnStyle, final Class<?> columnClass) {
		final PdfCellStyle pdfCellStyle = PdfCellStyleUtils.createPdfCellStyle(columnStyle);
		if (columnStyle != null && !StringUtils.isEmpty(columnStyle.getDataFormatString())) {
			final Format format = getPdfDataFormat().getFormat(columnStyle.getDataFormatString(), columnClass);
			if (format != null) {
				pdfCellStyle.setDataFormatter(format);
			}
		}
		return pdfCellStyle;
	}

	protected PdfDataFormat getPdfDataFormat() {
		return this.pdfDataFormat;
	}

}
