package com.ontimize.util.templates;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfReader;
import com.ontimize.util.FileUtils;
import com.ontimize.util.pdf.PdfFiller;

public class PdfTemplateGenerator extends AbstractTemplateGenerator implements TemplateGenerator {

	protected boolean showTemplate = true;

	/**
	 * Template creation in PDF format is not supported
	 */
	@Override
	public File createTemplate(final Map fieldValues, final Map valuesTable, final Map valuesImages) {
		throw new RuntimeException("It isn't supported");
	}

	@Override
	public File fillDocument(final InputStream input, final String nameFile, final Map fieldValues, final Map valuesTable,
			final Map valuesImages, final Map valuesPivotTable)
					throws Exception {
		final File directory = FileUtils.createTempDirectory();
		final File template = new File(directory.getAbsolutePath(), FileUtils.getFileName(nameFile));
		final List imageField = new Vector();

		if ((valuesImages != null) && !valuesImages.isEmpty()) {
			final Enumeration enu = Collections.enumeration(valuesImages.keySet());
			while (enu.hasMoreElements()) {
				final Object key = enu.nextElement();
				final Object value = valuesImages.get(key);
				fieldValues.put(key, value);
				imageField.add(key);
			}
		}

		PdfFiller.fillTextImageFields(input, new FileOutputStream(template), fieldValues, imageField, true);
		if (this.showTemplate) {
			com.ontimize.windows.office.WindowsUtils.openFile_Script(template);
		}
		return template;
	}

	@Override
	public void setShowTemplate(final boolean show) {
		this.showTemplate = show;
	}

	@Override
	public List queryTemplateFields(final String template) throws Exception {
		final File templateFile = new File(template);
		if (templateFile.exists()) {
			return this.queryTemplateFields(templateFile);
		} else {
			throw new Exception("File " + template + " not found.");
		}

	}

	@Override
	public List queryTemplateFields(final File template) throws Exception {
		final FileInputStream pdfInputStream = new FileInputStream(template);

		final ByteArrayOutputStream baOut = new ByteArrayOutputStream();
		final BufferedInputStream bInput = new BufferedInputStream(pdfInputStream);
		for (int a = 0; (a = bInput.read()) != -1;) {
			baOut.write(a);
		}
		final byte buffer[] = baOut.toByteArray();
		final PdfReader reader = new PdfReader(buffer);
		final AcroFields form = reader.getAcroFields();
		final HashMap fields = form.getFields();
		final Iterator names = fields.keySet().iterator();
		final List result = new Vector();
		while (names.hasNext()) {
			result.add(names.next());
		}
		return result;
	}

}
