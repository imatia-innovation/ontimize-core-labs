package com.ontimize.util.templates;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.FileUtils;

public abstract class AbstractTemplateGenerator implements TemplateGenerator {

	public static boolean DEBUG = false;

	/**
	 * Variable used to show labels in the template or not, if createLabelsInTemplate is false only the
	 * bookmarks are added to the template, without any label
	 */
	protected static boolean createLabelsInTemplate = true;

	protected DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();

	protected NumberFormat numberFormat = new DecimalFormat();

	@Override
	public File fillDocument(final String resource, final Map fieldValues, final Map valuesTable, final Map valuesImages)
			throws Exception {
		return this.fillDocument(resource, fieldValues, valuesTable, valuesImages, null);
	}

	@Override
	public File fillDocument(final InputStream input, final String nameFile, final Map fieldValues, final Map valuesTable,
			final Map valuesImages) throws Exception {
		return this.fillDocument(input, nameFile, fieldValues, valuesTable, valuesImages, null);
	}

	@Override
	public File fillDocument(final String resource, final Map fieldValues, final Map valuesTable, final Map valuesImages,
			final Map valuesPivotTable) throws Exception {
		final URL url = this.getClass().getClassLoader().getResource(resource);
		final InputStream input = url.openStream();
		return this.fillDocument(input, FileUtils.getFileName(resource), fieldValues, valuesTable, valuesImages,
				valuesPivotTable);
	}

	@Override
	public void setCreateLabelsInTemplate(final boolean createLabels) {
		AbstractTemplateGenerator.createLabelsInTemplate = createLabels;
	}

	/**
	 * Creates a String with format: key1 + delimiter + value1 + delimiter + key2 + delimiter + value2 +
	 * delimiter + ... + keyN + delimiter + valueN Replace all "\n" for "\r" to delete the carriage
	 * return
	 * @param dataField
	 * @param df
	 * @return
	 * @see #exportFieldData(Hashtable, String, DateFormat)
	 */
	public static String transformFieldData(final Map dataField, final DateFormat df) {
		return AbstractTemplateGenerator.transformFieldData(dataField, df, null);
	}

	/**
	 * Creates a String with format: key1 + delimiter + value1 + delimiter + key2 + delimiter + value2 +
	 * delimiter + ... + keyN + delimiter + valueN Replace all "\n" for "\r" to delete the carriage
	 * return
	 * @param dataField
	 * @param df
	 * @param numberFormat
	 * @return
	 * @see #exportFieldData(Hashtable, String, DateFormat,NumberFormat)
	 */
	public static String transformFieldData(final Map dataField, final DateFormat df, final NumberFormat numberFormat) {
		String stringResult = AbstractTemplateGenerator.exportFieldData(dataField, "$#", df, numberFormat);
		stringResult = stringResult.replace('\n', '\r');
		return stringResult;
	}

	/**
	 * Creates the File 'fielddata.dat' in the specified directory
	 * @param directory Directory
	 * @param data File content
	 * @return
	 * @throws Exception
	 */
	public static File createFileFieldData(final File directory, final String data) throws Exception {
		final File actual = new File(directory.getPath(), "fielddata.dat");
		FileUtils.saveFile(actual, data);
		return actual;
	}

	/**
	 * Creates the data files with table values.<br>
	 * Creates one file for each table and one index file (tableIndex.txt)
	 * @param directory Directory where the files will be created in
	 * @param valuesTable The object contains the table values to insert in the template. Each key must
	 *        be the table entity name and value must be an EntityResult
	 * @return The index file with name tableIndex.txt
	 * @throws Exception
	 */
	public static File createTableDataFile(final File directory, final Map valuesTable) throws Exception {
		return AbstractTemplateGenerator.createTableDataFile(directory, valuesTable, "tableIndex.txt");
	}

	/**
	 * @param directory Directory where the files will be created in
	 * @param valuesTable The object contains the table values to insert in the template. Each key must
	 *        be the table entity name and value must be an EntityResult
	 * @param indexFileName Name of the returned file with the table index
	 * @return
	 * @throws Exception
	 */
	public static File createTableDataFile(final File directory, final Map valuesTable, final String indexFileName)
			throws Exception {
		final StringBuilder info = new StringBuilder();
		if (valuesTable != null) {
			final Enumeration enu = Collections.enumeration(valuesTable.keySet());
			while (enu.hasMoreElements()) {
				final Object entityKey = enu.nextElement();
				final Object entityValue = valuesTable.get(entityKey);
				if ((entityKey instanceof String) && (((String) entityKey).length() > 0)
						&& (entityValue instanceof Map)) {
					final String dataTable = AbstractTemplateGenerator.exportTableData((Map) entityValue);
					final File actual = new File(directory.getPath(), (String) entityKey);
					FileUtils.saveFile(actual, dataTable);
					info.append(actual.getPath()).append("$");
					info.append((String) entityKey).append("\n");
				}
			}

		}

		// If any table exists create the index file
		// Index file format is: table configuration file path + "$" + table
		// entity name + "\n"
		if (info.length() > 0) {
			final File actual = new File(directory.getPath(), indexFileName);
			FileUtils.saveFile(actual, info.toString());
			return actual;
		} else {
			return null;
		}
	}

	/**
	 * Creates table definition files<br>
	 * Creates one file for each table and one index file (tableIndex.txt)
	 * @param directory Directory where the files will be created in
	 * @param valuesTable The object contains the table data to create the template. Each key must be
	 *        the table entity name and value must be an Map with column names and labels
	 * @param df
	 * @return The table index File
	 * @throws Exception
	 */
	public static File createTableDataDefinition(final File directory, final Map valuesTable, final DateFormat df)
			throws Exception {
		return AbstractTemplateGenerator.createTableDataDefinition(directory, valuesTable, df, null);
	}

	public static File createTableDataDefinition(final File directory, final Map valuesTable, final DateFormat df,
			final NumberFormat numberFormat) throws Exception {
		final StringBuilder info = new StringBuilder();
		if (valuesTable != null) {
			final Enumeration enu = Collections.enumeration(valuesTable.keySet());
			while (enu.hasMoreElements()) {
				final Object entityKey = enu.nextElement();
				final Object entityValue = valuesTable.get(entityKey);
				if ((entityKey instanceof String) && (entityValue instanceof Map)) {
					final String tableColumns = AbstractTemplateGenerator.exportFieldData((Map) entityValue, "$#", df,
							numberFormat);
					info.append(entityKey).append("$#");
					info.append(tableColumns).append("$#").append("\n");
				}
			}
		}

		if (info.length() > 0) {
			final File actual = new File(directory.getPath(), "tableIndex.txt");
			FileUtils.saveFile(actual, info.toString());
			return actual;
		} else {
			return null;
		}
	}

	/**
	 * Creates the data files with image values.<br>
	 * Creates one file for each image and one index file (imageIndex.txt)
	 * @param directory Directory where the files will be created in
	 * @param valuesImages The object contains the image values to insert. Key must be the image field
	 *        name and value must be the image data. The value could be an image object (Image), a
	 *        BytesBlock or a File .
	 * @return
	 * @throws Exception
	 */
	public static File createImageDataFile(final File directory, final Map valuesImages) throws Exception {
		final StringBuilder info = new StringBuilder();
		if (valuesImages != null) {
			final Enumeration enu = Collections.enumeration(valuesImages.keySet());
			while (enu.hasMoreElements()) {
				final Object entityKey = enu.nextElement();
				final Object entityValue = valuesImages.get(entityKey);
				if (entityKey instanceof String) {
					int width = 0;
					int height = 0;
					File fImage = null;
					if (entityValue instanceof Image) {
						fImage = new File(directory, (String) entityKey);
						final Image im = (Image) entityValue;
						width = im.getWidth(null);
						height = im.getHeight(null);
						AbstractTemplateGenerator.savePngImageFile(im, fImage);
					} else if (entityValue instanceof BytesBlock) {
						fImage = new File(directory, (String) entityKey);
						BytesBlock bb = (BytesBlock) entityValue;
						final InputStream is = new ByteArrayInputStream(bb.getBytes());
						ImageIcon ic = new ImageIcon(bb.getBytes());
						width = ic.getIconWidth();
						height = ic.getIconHeight();
						FileUtils.saveFile(fImage, bb.getBytes());
						is.close();
						ic = null;
						bb = null;
					} else if (entityValue instanceof File) {
						fImage = new File(directory, (String) entityKey);
						final ByteArrayOutputStream out = new ByteArrayOutputStream();
						final InputStream is = new FileInputStream((File) entityValue);
						final byte[] readB = new byte[1];
						while (is.read(readB) > 0) {
							out.write(readB);
						}
						final ImageIcon ic = new ImageIcon(out.toByteArray());
						width = ic.getIconWidth();
						height = ic.getIconHeight();
						FileUtils.saveFile(fImage, out.toByteArray());
					}

					if (fImage != null) {
						info.append(fImage.getPath()).append("$");
						info.append((String) entityKey).append("$");

						final String sWidth = NumberFormat.getInstance()
								.format(AbstractTemplateGenerator.getScreenPoints(width));
						final String sHeight = NumberFormat.getInstance()
								.format(AbstractTemplateGenerator.getScreenPoints(height));
						info.append(sWidth).append("$");
						info.append(sHeight).append("$").append("\n");
					}
				}
			}
		}

		// If there are images then creates the image index file
		if (info.length() > 0) {
			final File actual = new File(directory.getPath(), "imageIndex.txt");
			FileUtils.saveFile(actual, info.toString());
			return actual;
		} else {
			return null;
		}

	}

	/**
	 * Calculates the screen point for a pixel value
	 * @param pixelsValue number of pixels
	 * @return
	 */
	protected static double getScreenPoints(final int pixelsValue) {
		final int screenResolution = Toolkit.getDefaultToolkit().getScreenResolution();
		final double result = (pixelsValue * 72.0) / screenResolution;
		return result;
	}

	/**
	 * Creates a String with format: key1 + delimiter + value1 + delimiter + key2 + delimiter + value2 +
	 * delimiter + ... + keyN + delimiter + valueN
	 * @param data Map with all data
	 * @param delimiter Delimiter to separate the fields
	 * @param df Format to use in date data fields
	 * @return
	 */
	public static String exportFieldData(final Map data, final String delimiter, final DateFormat df) {
		return AbstractTemplateGenerator.exportFieldData(data, delimiter, df, null);
	}

	/**
	 * Creates a String with format: key1 + delimiter + value1 + delimiter + key2 + delimiter + value2 +
	 * delimiter + ... + keyN + delimiter + valueN
	 * @param data Map with all data
	 * @param delimiter Delimiter to separate the fields
	 * @param df Format to use in date data fields
	 * @param numberFormat Format to use in the numeric fields
	 * @return
	 */
	public static String exportFieldData(final Map data, final String delimiter, final DateFormat df, final NumberFormat numberFormat) {

		// format : key1 + delimiter + value1 + delimiter + key2 + delimiter +
		// value2 + delimiter + ... + keyN + delimiter + valueN
		if (data == null) {
			throw new IllegalArgumentException("exportFieldData: data is null");
		}

		final StringBuilder res = new StringBuilder();

		final Object[] keys = data.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			final Object key = keys[i];
			res.append(key);
			res.append(delimiter);
			if (data.get(key) instanceof List) {
				if (((List) data.get(key)).get(0) != null) {
					final Object currentData = ((List) data.get(key)).get(0);
					if ((df != null) && (currentData instanceof Date)) {
						res.append(df.format(currentData));
					} else {
						res.append(currentData.toString());
					}

				} else {
					res.append(" ");
				}
			} else {
				if ((df != null) && (data.get(key) instanceof Date)) {
					res.append(df.format(data.get(key)));
				} else if (data.get(key) instanceof Number) {
					res.append(AbstractTemplateGenerator.parseNumber((Number) data.get(key), numberFormat));
				} else {
					res.append(data.get(key).toString());
				}

			}
			if (i < (keys.length - 1)) {
				res.append(delimiter);
			}
		}
		return res.toString();
	}

	protected static String parseNumber(final Number number, final NumberFormat numberFormat) {
		if (numberFormat != null) {
			return numberFormat.format(number.doubleValue());
		} else {
			return number.toString();
		}
	}

	/**
	 * Creates a String with all table data. The String format is: <br>
	 * \n colName1 $ colData1.1 # colData1.2 # ...# colData1.N $ colName2 $ colData2.1 # colData2.2 #
	 * ...# colData2.N
	 * @param data Table data
	 * @return
	 */
	public static String exportTableData(final Map data) {
		// format :
		// colName1 $ colData1.1 # colData1.2 # ...# colData1.N $
		// colName2 $ colData2.1 # colData2.2 # ...# colData2.N $

		if (data == null) {
			throw new IllegalArgumentException("ExportTableData: data is null");
		}

		final StringBuilder res = new StringBuilder();
		if (!data.isEmpty() && (AbstractTemplateGenerator.calculateRecordCount(data) > 0)) {
			final Object[] keys = data.keySet().toArray();
			for (int i = 0; i < keys.length; i++) {
				final Object key = keys[i];
				res.append(key);
				res.append("$");
				final List vData = (List) data.get(key);
				for (int j = 0; j < (vData.size() - 1); j++) {
					if (vData.get(j) != null) {
						res.append(vData.get(j).toString());
						res.append("#");
					} else {
						res.append(" ");
						res.append("#");
					}
				}
				if ((vData.size() > 0) && (vData.get(vData.size() - 1) != null)) {
					res.append(vData.get(vData.size() - 1).toString());
				} else {
					res.append(" ");
				}
				res.append("$");
			}
		}
		return res.toString();
	}

	/**
	 * Calculates the number of data rows.
	 * @param data
	 * @return The size of the first List found in the Hashtable
	 */
	public static int calculateRecordCount(final Map data) {
		int r = 0;
		final Enumeration keys = Collections.enumeration(data.keySet());
		while (keys.hasMoreElements()) {
			final Object oKey = keys.nextElement();
			final Object v = data.get(oKey);
			if ((v != null) && (v instanceof List)) {
				r = ((List) v).size();
				break;
			}
		}
		return r;
	}

	/**
	 * Save the image in the specified File as a png Image.
	 * @param im Image to save
	 * @param fImage File
	 * @throws IOException
	 */
	public static void savePngImageFile(final Image im, final File fImage) throws IOException {
		if (im instanceof RenderedImage) {
			ImageIO.write((RenderedImage) im, "png", fImage);
		} else {
			final BufferedImage bim = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
			final Graphics g = bim.getGraphics();
			g.drawImage(im, 0, 0, null);
			bim.flush();
			ImageIO.write(bim, "png", fImage);
		}
	}

	@Override
	public void setDateFormat(final DateFormat df) {
		this.dateFormat = df;
	}

	@Override
	public void setNumberFormat(final NumberFormat nf) {
		this.numberFormat = nf;
	}

	/**
	 * Returns true if some of the values in the input parameter is another hashtable
	 * @param data
	 * @return
	 */
	public static boolean containsHashtableValue(final Map data) {
		final Object[] array = data.keySet().toArray();
		for (int i = 0; i < array.length; i++) {
			if (data.get(array[i]) instanceof Map) {
				return true;
			}
		}
		return false;
	}

	public static Object[] getKeysOrder(final Map data) {
		final List keysOrder = new ArrayList();

		if (data instanceof EntityResult) {
			// Get the order of the field in the data to generate the template
			final List orderColumns = ((EntityResult) data).getOrderColumns();
			if (orderColumns != null) {
				keysOrder.addAll(orderColumns);
			}
		}
		// Put the elements not added yet in any order
		final Object[] dataKeys = data.keySet().toArray();
		for (int i = 0; i < dataKeys.length; i++) {
			if (!keysOrder.contains(dataKeys[i])) {
				keysOrder.add(dataKeys[i]);
			}
		}
		return keysOrder.toArray();
	}

}
