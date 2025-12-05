package com.ontimize.util.templates;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.FileUtils;
import com.ontimize.windows.office.WindowsUtils;

/**
 * Create ODF (Open Document Format) templates (with or without data). This class uses JooReports
 * library and its dependencies.
 *
 * More information in: http://jooreports.sourceforge.net
 *
 * @author Imatia Innovation
 * @since 21/06/2007 Fill template
 * @since 30/11/2007 Create empty template
 */
public class ODFTemplateGenerator extends AbstractTemplateGenerator {

	private static final Logger logger = LoggerFactory.getLogger(ODFTemplateGenerator.class);

	public static boolean DEBUG = true;

	private static final String DEFAULT_JARS_PROPERTIES = "com/ontimize/util/templates/odf.properties",
			ODF_EMPTY_TEMPLATE = "com/ontimize/util/templates/template.odt",
			DEFAULT_IMAGE_FORMAT = "png";

	private static boolean librariesChecked = false;

	protected boolean showTableTotals = false;

	protected boolean showTemplate = false;

	protected void log(final String log) {
		if (ODFTemplateGenerator.DEBUG) {
			ODFTemplateGenerator.logger.debug(log);
		}
	}

	/**
	 * Check if JOOReports jars are avaliable.
	 */
	public static boolean checkLibraries() {
		if (ODFTemplateGenerator.librariesChecked) {
			return true;
		}
		final JarVerifier jv = new JarVerifier(ODFTemplateGenerator.DEFAULT_JARS_PROPERTIES);
		ODFTemplateGenerator.librariesChecked = jv.verify();
		return ODFTemplateGenerator.librariesChecked;
	}

	@Override
	public File createTemplate(Map fieldValues, Map valuesTable, Map valuesImages) throws Exception {

		valuesTable = ODFParser.translateTableDotFields(valuesTable);
		fieldValues = ODFParser.translateDotFields(fieldValues);
		valuesImages = ODFParser.translateDotFields(valuesImages);

		this.log("Create template from form ...");
		final long init = System.currentTimeMillis();

		final java.net.URL url = this.getClass().getClassLoader().getResource(ODFTemplateGenerator.ODF_EMPTY_TEMPLATE);
		final InputStream input = url.openStream();

		ODFParser op = new ODFParser(input);
		final File f = op.create(fieldValues, valuesTable, valuesImages, AbstractTemplateGenerator.createLabelsInTemplate);
		op = null;
		input.close();

		final long end = System.currentTimeMillis();
		this.log("\nCreated in " + (end - init) + " ms.\n");
		if (this.showTemplate) {
			WindowsUtils.openFile_Script(f);
		}
		return f;
	}

	@Override
	public File fillDocument(final String resource, Map fieldValues, Map valuesTable, Map valuesImages,
			final Map valuesPivotTable) throws Exception {

		this.log("Fill ODF document ... " + resource);

		valuesTable = ODFParser.translateTableDotFields(valuesTable);
		fieldValues = ODFParser.translateDotFields(fieldValues);
		valuesImages = ODFParser.translateDotFields(valuesImages);

		// Check the library. Verify in the class loader.
		if (!ODFTemplateGenerator.checkLibraries()) {
			return null;
		}

		final java.net.URL url = this.getClass().getClassLoader().getResource(resource);
		final InputStream input = url.openStream();

		// List templateFields = queryTemplateFields(resource);
		// OpenOfficeTemplateFields ooTF = new
		// OpenOfficeTemplateFields(templateFields);
		//
		// fieldValues = ooTF.checkTemplateFieldValues(fieldValues);
		// valuesTable = ooTF.checkTemplateTableValues(valuesTable);

		final File f = this.fillDocument(input, FileUtils.getFileName(resource), fieldValues, valuesTable, valuesImages,
				valuesPivotTable);
		input.close();
		return f;
	}

	@Override
	public File fillDocument(final InputStream input, final String nameFile, Map fieldValues, Map valuesTable,
			Map valuesImages, final Map valuesPivotTable)
					throws Exception {

		valuesTable = ODFParser.translateTableDotFields(valuesTable);
		fieldValues = ODFParser.translateDotFields(fieldValues);
		valuesImages = ODFParser.translateDotFields(valuesImages);

		this.log("Fill ODF document ... " + nameFile);
		final long init = System.currentTimeMillis();

		// Check the library. Verify in the class loader.
		if (!ODFTemplateGenerator.checkLibraries()) {
			return null;
		}

		// Copy.
		final String tmp = System.getProperty("java.io.tmpdir");
		final File directory = (tmp != null) && (tmp.length() != 0) ? new File(tmp) : FileUtils.createTempDirectory(); // Create
		// it.
		directory.deleteOnExit();
		final File template = new File(directory, FileUtils.getFileName(nameFile));

		final ODFFreeMarkerParser ofmp = new ODFFreeMarkerParser(input);

		final List templateFields = ofmp.queryTemplateFields();
		final OpenOfficeTemplateFields ooTF = new OpenOfficeTemplateFields(templateFields);

		fieldValues = ooTF.checkTemplateFieldValues(fieldValues);

		final File templateOut = ofmp.parse(fieldValues, valuesTable, valuesImages, valuesPivotTable);
		templateOut.deleteOnExit();

		Map allTables = new Hashtable();
		if (valuesTable != null) {
			allTables.putAll(valuesTable);
		}
		if (valuesPivotTable != null) {
			allTables.putAll(valuesPivotTable);
		}
		allTables = ooTF.checkTemplateTableValues(allTables);

		if (templateOut == null) {
			throw new Exception("TEMPLATE_NOT_FOUND" + ofmp);
		}
		// Fields.
		final Map h = this.createDateHashtableParsed(fieldValues);

		// Tables.
		if ((allTables != null) && !allTables.isEmpty()) {

			// For each table
			final Enumeration e = Collections.enumeration(allTables.keySet());
			final Collection v = allTables.values();
			final Iterator i = v.iterator();

			while (e.hasMoreElements()) {

				Object o = e.nextElement();
				if ((o == null) || !(o instanceof String)) {
					i.next();
					continue;
				}

				final String table = (String) o;
				o = i.next();
				if ((o == null) || !(o instanceof Map)) {
					continue;
				}

				final Map t = (Map) o;
				final Map p = this.createDateHashtableParsed(t); // p is a Copy
				if (this.showTableTotals) {
					this.generateTotalsHashtableRow(p);
				}
				final List l = this.createTableList(p);
				if (l != null) {
					h.put(table, l);
				}
			}
		}

		// Images.
		final net.sf.jooreports.templates.images.ByteArrayImageProvider imageProvider = new net.sf.jooreports.templates.images.ByteArrayImageProvider();
		if ((valuesImages != null) && !valuesImages.isEmpty()) {

			final Enumeration e = Collections.enumeration(valuesImages.keySet());
			final Collection v = valuesImages.values();
			final Iterator i = v.iterator();

			while (e.hasMoreElements()) {

				Object o = e.nextElement();
				if ((o == null) || !(o instanceof String)) {
					i.next();
					continue;
				}
				final String image = (String) o;
				o = i.next();
				if (o == null) {
					continue;
				}

				// ByteArrayOutputStream, File, Image, Ontimize BytesBlock
				final byte[] data = this.createImageBytes(o);
				if (data != null) {
					imageProvider.setImage(image, data);
				}
			}
		}

		this.log("Sending data to JOOReports using template " + templateOut.getAbsolutePath());

		net.sf.jooreports.templates.DocumentTemplate dt = null;
		if (templateOut.isDirectory()) {
			dt = new net.sf.jooreports.templates.UnzippedDocumentTemplate(templateOut);
		} else {
			dt = new net.sf.jooreports.templates.ZippedDocumentTemplate(templateOut);
		}

		dt.createDocument(h, new FileOutputStream(template), imageProvider);

		if (this.showTemplate) {
			// Open file
			WindowsUtils.openFile_Script(template);
		}

		final long end = System.currentTimeMillis();
		this.log("Filled in " + (end - init) + " ms.");
		return template;
	}

	/**
	 * If true, the table shows a new row with the column totals. If the column isn't a numeric value,
	 * shows the row count. The default is false.
	 */
	public void setShowTableTotals(final boolean showTableTotals) {
		this.showTableTotals = showTableTotals;
	}

	@Override
	public void setShowTemplate(final boolean show) {
		this.showTemplate = show;
	}

	/**
	 * Create a copy of the parameter Map to parse the containing dates with the current date
	 * format parser.
	 * @param h Map to parse.
	 * @return A copy of the hastable to parse.
	 */
	private Map createDateHashtableParsed(final Map h) {
		if (h == null) {
			return new Hashtable();
		}

		final Map p = new Hashtable(h.size());

		final Set s = h.entrySet();
		final Iterator i = s.iterator();
		while (i.hasNext()) {
			final Object o = i.next();
			if ((o == null) || !(o instanceof Map.Entry)) {
				continue;
			}
			final Map.Entry e = (Map.Entry) o;
			final Object k = e.getKey();
			Object v = e.getValue();

			if ((v instanceof java.util.Date) || (v instanceof java.sql.Timestamp)) {
				v = this.dateFormat.format(v);
			} else if (v instanceof List) { // EntityResult
				final List l = (List) v;

				final int size = l.size();
				for (int j = 0; j < size; j++) {
					final Object tmp = l.get(j);
					if ((tmp instanceof java.util.Date) || (tmp instanceof java.sql.Timestamp)) {
						l.set(j, this.dateFormat.format((Date) tmp));
					}
				}
			}
			p.put(k, v);
		}
		return p;
	}

	private void generateTotalsHashtableRow(final Map h) {
		if (h == null) {
			return;
		}

		final Set s = h.entrySet();
		final Iterator i = s.iterator();
		while (i.hasNext()) {
			final Object o = i.next();
			if ((o == null) || !(o instanceof Map.Entry)) {
				continue;
			}
			// Map.Entry e = (Map.Entry) o;
			// Object k = e.getKey();
			// Object v = e.getValue();

			// TODO v must be a vector, and values must be integer objects, to
			// get
			// the sum of them, if there is other data type then only count the
			// attemps

		}
	}

	private List<Map<String, Object>> createTableList(final Map<String, List<?>> h) {
		if ((h == null) || h.isEmpty()) {
			return null;
		}

		// Tomamos la primera columna para obtener el número de filas
		final List<?> firstColumn = h.values().iterator().next();
		if ((firstColumn == null) || !(firstColumn instanceof List)) {
			return new ArrayList<>();
		}

		final int size = firstColumn.size();
		final List<Map<String, Object>> result = new ArrayList<>(size);

		// Recorremos filas
		for (int i = 0; i < size; i++) {
			final Map<String, Object> row = new HashMap<>();

			// Recorremos columnas
			for (final Map.Entry<String, List<?>> entry : h.entrySet()) {
				final String key = entry.getKey();
				final List<?> column = entry.getValue();

				if ((column != null) && (i < column.size())) {
					final Object value = column.get(i);
					row.put(key, value != null ? value : "");
				}
			}

			result.add(row);
		}

		return result;
	}

	private byte[] createImageBytes(final Object o) throws Exception {
		byte[] data = null;

		if (o == null) {
			return data;
		}

		if (o instanceof ByteArrayOutputStream) {
			data = ((ByteArrayOutputStream) o).toByteArray();
		} else if (o instanceof File) {
			final File f = (File) o;
			if (f.exists() && f.isFile()) {
				final ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(ImageIO.read(f), ODFTemplateGenerator.DEFAULT_IMAGE_FORMAT, baos);
				baos.flush();

				data = baos.toByteArray();
				baos.close();
			}
		} else if (o instanceof Image) {
			final Image img = (Image) o;

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (img instanceof RenderedImage) {
				ImageIO.write((RenderedImage) img, ODFTemplateGenerator.DEFAULT_IMAGE_FORMAT, baos);
			} else {
				final BufferedImage bim = new BufferedImage(img.getWidth(null), img.getHeight(null),
						BufferedImage.TYPE_INT_RGB);
				final Graphics g = bim.getGraphics();
				g.drawImage(img, 0, 0, null);
				bim.flush();
				ImageIO.write(bim, ODFTemplateGenerator.DEFAULT_IMAGE_FORMAT, baos);
			}
			baos.flush();

			data = baos.toByteArray();
			baos.close();
		} else if (o instanceof BytesBlock) {
			data = ((BytesBlock) o).getBytes();
		}
		return data;
	}

	@Override
	public List queryTemplateFields(final String template) throws Exception {
		return this.queryTemplateFields(new File(template));
	}

	@Override
	public List queryTemplateFields(final File template) throws Exception {
		final ODFParser op = new ODFParser(template);
		return op.queryTemplateFields();
	}

}
