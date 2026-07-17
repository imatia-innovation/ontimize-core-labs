package com.ontimize.jee.webclient.export.support.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;
import com.ontimize.jee.webclient.export.exception.ExportException;

/**
 * Exportador a documentos PDF (OpenPDF).
 */
public class DefaultPdfExporter extends BasePdfExporter<Document> {

	public DefaultPdfExporter(final File pdfFile) {
		super(pdfFile);
	}

	@Override
	protected Document buildDocument(final boolean landscape) throws ExportException {
		try {
			// PageSize.A4 es un Rectangle; rotate() devuelve la orientación horizontal
			final Document document = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4);
			PdfWriter.getInstance(document, new FileOutputStream(this.getPdfFile()));
			document.open();
			return document;
		} catch (final FileNotFoundException e) {
			throw new ExportException(e);
		}
	}

}
