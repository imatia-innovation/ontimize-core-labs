package com.ontimize.util.pdf;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Image;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Extension of <code>PDFFiller</code>, that allows to specify input fields that will remain in
 * output PDF.
 *
 * @author Imatia Innovation
 * @see {@link http://www.lowagie.com/iText/}
 */
public abstract class ExtPDFFiller extends PdfFiller {

	private static final Logger logger = LoggerFactory.getLogger(ExtPDFFiller.class);

	/**
	 * Fills PDF fields that parameters included in <code>params</code>. If some of these ones are
	 * included in <code>imagesFields</code> will be considered images and will be filled differently.
	 * @param pdfInputStream input document stream
	 * @param pdfOutputStream output document stream
	 * @param params fields and values to fill
	 * @param imagesFields fields that are images
	 * @param flatFields fields to delete or null when all fields must be filled
	 * @throws Exception when an <code>Exception</code> occurs
	 */
	public static void fillTextImagesFields(final java.io.InputStream pdfInputStream, final java.io.OutputStream pdfOutputStream,
			final Map params, final List imagesFields, final String[] flatFields)
					throws Exception {
		final Map htextfields = new Hashtable();
		final Map himagefields = new Hashtable();
		Enumeration enumKeys;
		try {
			enumKeys = Collections.enumeration(params.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();

				if (imagesFields.contains(oKey)) {
					himagefields.put(oKey, params.get(oKey));
				} else {
					htextfields.put(oKey, params.get(oKey));
				}
			}
			final ByteArrayOutputStream inter = new ByteArrayOutputStream();
			ExtPDFFiller.fillFields(pdfInputStream, inter, htextfields, null);
			ExtPDFFiller.fillImageFields(new ByteArrayInputStream(inter.toByteArray()), pdfOutputStream, himagefields,
					flatFields);
			inter.close();

		} catch (final IOException ex) {
			ExtPDFFiller.logger.error(null, ex);
			throw ex;
		}
	}

	/**
	 * Fills PDF fields that parameters included in <code>params</code>.
	 * @param pdfInputStream input document stream
	 * @param pdfOutputStream output document stream
	 * @param params fields and values to fill
	 * @param flatFields fields to delete or null when all fields must be filled
	 * @throws Exception when an <code>Exception</code> occurs
	 */
	public static void fillFields(final InputStream pdfInputStream, final OutputStream pdfOutputStream, final Map params,
			final String[] flatFields) throws Exception {
		try {
			final ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			final BufferedInputStream bInput = new BufferedInputStream(pdfInputStream);
			int a = 0;
			while ((a = bInput.read()) != -1) {
				baOut.write(a);
			}
			final byte[] buffer = baOut.toByteArray();

			final PdfReader reader = new PdfReader(buffer);
			final PdfStamper stamp = new PdfStamper(reader, pdfOutputStream);

			final AcroFields form = stamp.getAcroFields();
			final Enumeration enumkeys = Collections.enumeration(params.keySet());
			while (enumkeys.hasMoreElements()) {
				final String fieldelement = enumkeys.nextElement().toString();
				final Object fieldvalue = params.get(fieldelement);
				if (fieldvalue != null) {
					form.setField(fieldelement, fieldvalue.toString());
				}
			}
			ExtPDFFiller.setFlattening(stamp, flatFields);
			stamp.close();
		} catch (final IOException ex) {
			ExtPDFFiller.logger.error(null, ex);
			throw ex;
		} catch (final Exception e) {
			ExtPDFFiller.logger.error(null, e);
		}
	}

	private static void setFlattening(final PdfStamper stamper, final String[] fields) {
		if ((fields == null) || (fields.length == 0)) {
			stamper.setFormFlattening(false);
		} else {
			stamper.setFormFlattening(true);
			for (int i = 0; i < fields.length; i++) {
				stamper.partialFormFlattening(fields[i]);
			}
		}

	}

	/**
	 * Fills PDF image fields.
	 * @param pdfInputStream input document stream
	 * @param pdfOutputStream output document stream
	 * @param params fields and values to fill
	 * @param flatFields fields to delete or null when all fields must be filled
	 * @throws Exception when an <code>Exception</code> occurs
	 */
	public static void fillImageFields(final InputStream pdfInputStream, final OutputStream pdfOutputStream, final Map params,
			final String[] flatFields) throws Exception {
		try {
			final ByteArrayOutputStream baOut = new ByteArrayOutputStream();
			final BufferedInputStream bInput = new BufferedInputStream(pdfInputStream);
			int a = 0;
			while ((a = bInput.read()) != -1) {
				baOut.write(a);
			}
			final byte[] buffer = baOut.toByteArray();

			final PdfReader reader = new PdfReader(buffer);
			reader.getPageSize(1);

			final PdfStamper writer = new PdfStamper(reader, pdfOutputStream);

			PdfContentByte cb = null;

			final ByteArrayInputStream pdfBytesArrayInputStream = new ByteArrayInputStream(buffer);
			final List fieldProps = PdfFiller.getFieldProps(pdfBytesArrayInputStream);
			for (int pageNumber = 1; pageNumber <= reader.getNumberOfPages(); pageNumber++) {
				cb = writer.getOverContent(pageNumber);
				final Enumeration enumKeys = Collections.enumeration(fieldProps);
				while (enumKeys.hasMoreElements()) {
					final Object keyelement = enumKeys.nextElement();
					final FieldProp keyvalue = (FieldProp) keyelement;
					if (keyvalue.page == pageNumber) {
						if (params.containsKey(keyvalue.name)) {
							final Object value = params.get(keyvalue.name);
							Image img = null;
							if (value instanceof InputStream) {
							}
							if (value instanceof byte[]) {
								img = Image.getInstance((byte[]) value);
							}
							if (value instanceof java.awt.Image) {
								img = Image.getInstance((java.awt.Image) value, null);
							}
							if (img != null) {
								if ((img.width() > keyvalue.width()) || (img.height() > keyvalue.height())) {
									img.scaleToFit(keyvalue.width(), keyvalue.height());
								}
								img.setAbsolutePosition(keyvalue.x1 + ((keyvalue.width() - img.width()) / 2),
										keyvalue.y1 + ((keyvalue.height() - img.height()) / 2));
								cb.addImage(img);
							}
						}
					}
				}
			}
			final PRAcroForm form = reader.getAcroForm();
			ExtPDFFiller.setFlattening(writer, flatFields);
			writer.close();
		} catch (final Exception ex) {
			ExtPDFFiller.logger.error(null, ex);
			throw ex;
		}
	}

}
