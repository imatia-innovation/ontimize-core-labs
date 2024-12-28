package com.ontimize.util.templates;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

public interface TemplateGenerator {

	/**
	 * Fills the specified template
	 * @param resource : Resource where the template is located.
	 * @param valuesDescriptions Values for the data fields
	 * @param valuesTable Table values
	 * @param valuesImages Image values
	 * @return
	 * @throws Exception
	 */

	public File fillDocument(String resource, Map valuesDescriptions, Map valuesTable,
			Map valuesImages) throws Exception;

	public File fillDocument(String resource, Map valuesDescriptions, Map valuesTable,
			Map valuesImages, Map valuesPivotTable) throws Exception;

	/**
	 * Fills the template
	 * @param input : Template to fill.
	 * @param nameFile : Name used to save the filled template
	 * @param fieldValues : This object contains the data field values to insert in the template. Keys
	 *        must be the field names and values must have the data field values to insert.
	 * @param valuesTable : The object contains the table values to insert in the template. Each key
	 *        must be the table entity name and value must be an EntityResult
	 * @param valuesImages : The object contains the image values to insert. Key must be the image field
	 *        name and value must be the image data. The value could be an image object (Image), a
	 *        BytesBlock or a File.
	 * @return
	 * @throws Exception
	 */
	public File fillDocument(InputStream input, String nameFile, Map fieldValues, Map valuesTable,
			Map valuesImages) throws Exception;

	public File fillDocument(InputStream input, String nameFile, Map fieldValues, Map valuesTable,
			Map valuesImages, Map valuesPivotTable) throws Exception;

	// public void fillDocument(String archive, Form form, List field, Vector
	// table,Vector images);

	/**
	 * Sets the date format to use in the dates of the document
	 * @param df Format
	 */
	public void setDateFormat(DateFormat df);

	/**
	 * Sets the number format to use in the numeric values of the document
	 * @param nf
	 */
	public void setNumberFormat(NumberFormat nf);

	/**
	 * Sets the value of the variable show. When this variable takes the value "true", the template is
	 * shown to the user. Elsewhere, the template will not be shown
	 * @param show
	 */
	public void setShowTemplate(boolean show);

	public void setCreateLabelsInTemplate(boolean createLabels);

	/**
	 * Creates an empty template.
	 * @param fieldValues : This object contains the data fields attributes and labels to show in the
	 *        template
	 * @param valuesTable : The object contains the table information to insert in the template. This
	 *        map must have the table entity name as key and the value must be other Map with the
	 *        columns attributes and names to show (column name - column label)
	 * @param valuesImages : The object contains information about the image fields which owns the form.
	 *        This map contains the name of the image field (value) and its attribute (key)
	 * @throws Exception
	 */
	public File createTemplate(Map fieldValues, Map valuesTable, Map valuesImages) throws Exception;

	/**
	 * Creates a list with all data fields included in the template
	 * @param template Template to query
	 * @return List with all data fields included in the template
	 * @throws Exception
	 */
	public List queryTemplateFields(String template) throws Exception;

	/**
	 * Creates a list with all data fields included in the template
	 * @param template Template to query
	 * @return List with all data fields included in the template
	 * @throws Exception
	 */
	public List queryTemplateFields(File template) throws Exception;

}
