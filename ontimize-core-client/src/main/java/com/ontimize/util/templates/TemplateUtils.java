package com.ontimize.util.templates;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.gui.field.ImageDataField;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.gui.field.MultipleReferenceDataFieldAttribute;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.gui.table.TableAttribute;

public class TemplateUtils {

	private static final Logger logger = LoggerFactory.getLogger(TemplateUtils.class);

	public static final int TEMPLATE_TYPE_ODT = 0;

	public static final int TEMPLATE_TYPE_DOC = 1;

	public static final int TEMPLATE_TYPE_PDF = 2;

	public static final int TEMPLATE_TYPE_DOCX = 3;

	private static final String M_TEMPLATE_GENERATE_ERROR = "form.error_to_generate_template";

	protected static TemplateGenerator wordTemplateGenerator = null;

	protected static TemplateGenerator ooTemplateGenerator = null;

	protected static TemplateGenerator pdfTemplateGenerator = null;

	protected static TemplateGenerator docxTemplateGenerator = null;

	protected static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	static {

		try {
			TemplateUtils.wordTemplateGenerator = TemplateGeneratorFactory
					.templateGeneratorInstance(TemplateGeneratorFactory.WORD);
			TemplateUtils.wordTemplateGenerator.setDateFormat(TemplateUtils.sdf);
		} catch (final Error ex) {
			TemplateUtils.logger.trace(null, ex);
		}

		try {
			TemplateUtils.ooTemplateGenerator = TemplateGeneratorFactory
					.templateGeneratorInstance(TemplateGeneratorFactory.OPEN_OFFICE);
			TemplateUtils.ooTemplateGenerator.setDateFormat(TemplateUtils.sdf);
		} catch (final Error ex) {
			TemplateUtils.logger.trace(null, ex);
		}

		try {
			TemplateUtils.pdfTemplateGenerator = TemplateGeneratorFactory
					.templateGeneratorInstance(TemplateGeneratorFactory.PDF);
			TemplateUtils.pdfTemplateGenerator.setDateFormat(TemplateUtils.sdf);
		} catch (final Error ex) {
			TemplateUtils.logger.trace(null, ex);
		}

		try {
			TemplateUtils.docxTemplateGenerator = TemplateGeneratorFactory
					.templateGeneratorInstance(TemplateGeneratorFactory.DOCX);
			TemplateUtils.docxTemplateGenerator.setDateFormat(TemplateUtils.sdf);
		} catch (final Error ex) {
			TemplateUtils.logger.trace(null, ex);
		}

	}

	/**
	 * Create a template of specified type with all fields, tables and images of the form
	 * @param form
	 * @param templateType (TEMPLATE_TYPE_DOC,TEMPLATE_TYPE_PDF, TEMPLATE_TYPE_ODT, TEMPLATE_TYPE_DOCX)
	 * @return
	 */
	public static File createTemplate(final Form form, final int templateType) {
		final InteractionManager im = form.getInteractionManager();
		File template = null;
		// File folderODT = null;
		try {
			TemplateUtils.getTemplateGenerator(templateType).setShowTemplate(true);
			template = TemplateUtils.getTemplateGenerator(templateType)
					.createTemplate(im.getTemplateFields(form), im.getTemplateTables(form), im.getTemplateImages(form));
			template.deleteOnExit();
			return template;
		} catch (final Exception e) {
			TemplateUtils.logger.error(null, e);
			form.message(TemplateUtils.M_TEMPLATE_GENERATE_ERROR, Form.ERROR_MESSAGE, e);
		}
		return null;
	}

	public static final void deleteFolderContent(final File folder) throws Exception {

		try {
			if (!folder.isDirectory()) {
				throw new IllegalArgumentException("Folder (" + folder.getPath() + ") must be a directory.");
			}

			if (!folder.exists()) {
				throw new IllegalArgumentException("Folder directory (" + folder.getPath() + ") doesn't exist.");
			}

			final File[] filesAndDirs = folder.listFiles();
			final List<File> filesDirs = Arrays.asList(filesAndDirs);
			for (final File file : filesDirs) {
				if (file.isDirectory()) {
					// recursive call!
					TemplateUtils.deleteFolderContent(file);
					file.deleteOnExit();
				} else {
					file.deleteOnExit();
				}
			}
		} catch (final Exception e) {
			TemplateUtils.logger.error(null, e);
		}
	}

	/**
	 * Get the TemplateGenerator of the specified type
	 * @param templateType
	 * @return
	 */
	public static TemplateGenerator getTemplateGenerator(final int templateType) {
		if (templateType == TemplateUtils.TEMPLATE_TYPE_PDF) {
			return TemplateUtils.pdfTemplateGenerator;
		} else if (templateType == TemplateUtils.TEMPLATE_TYPE_DOC) {
			return TemplateUtils.wordTemplateGenerator;
		} else if (templateType == TemplateUtils.TEMPLATE_TYPE_ODT) {
			return TemplateUtils.ooTemplateGenerator;
		} else if (templateType == TemplateUtils.TEMPLATE_TYPE_DOCX) {
			return TemplateUtils.docxTemplateGenerator;
		}
		return null;
	}

	/**
	 * Get a Map with the names of form's fields and their translations <br>
	 * key - Field name <br>
	 * value - key translation
	 * @param form
	 * @return
	 */
	public static Map getTemplateFields(final Form form) {
		final List fields = form.getDataComponents();
		final List attrs = new ArrayList();
		for (int i = 0; i < fields.size(); i++) {
			if ((fields.get(i) instanceof DataField) && !(fields.get(i) instanceof ImageDataField)
					&& ((DataField) fields.get(i)).isVisible()) {

				if ((fields.get(i) instanceof ITemplateField)
						&& (((ITemplateField) fields.get(i)).getTemplateDataType() != ITemplateField.DATA_TYPE_FIELD)) {
					continue;
				}

				final Object attribute = ((DataField) fields.get(i)).getAttribute();
				if (attribute instanceof String) {
					attrs.add(attribute);
				} else if (attribute instanceof ReferenceFieldAttribute) {
					attrs.add(((ReferenceFieldAttribute) attribute).getAttr());
				} else if (attribute instanceof MultipleReferenceDataFieldAttribute) {
					attrs.add(((MultipleReferenceDataFieldAttribute) attribute).getAttr());
				}
			}
		}
		return TemplateUtils.getTemplateHashtableTranslation(attrs, form.getResourceBundle());
	}

	/**
	 * Get the list of tables to insert in the template <br>
	 * key - Table entity <br>
	 * value - Map with the names of the visible cols and their translations<br>
	 * @param form
	 * @return
	 */
	public static Map getTemplateTables(final Form form) {
		final Map result = new Hashtable();
		final List fields = form.getComponentList();
		for (int i = 0; i < fields.size(); i++) {
			if ((fields.get(i) instanceof Table) && ((Table) fields.get(i)).isVisible()) {
				String entityName = ((Table) fields.get(i)).getEntityName();
				if ((entityName == null) || (entityName.length() == 0)) {
					continue;
				}

				if (entityName.indexOf(".") > 0) {
					entityName = entityName.substring(entityName.lastIndexOf(".") + 1);
				}
				result.put(entityName, TemplateUtils.getTemplateHashtableTranslation(
						((Table) fields.get(i)).getVisibleColumns(), form.getResourceBundle()));
			} else if ((fields.get(i) instanceof ITemplateField)
					&& (((ITemplateField) fields.get(i)).getTemplateDataType() == ITemplateField.DATA_TYPE_TABLE)) {
				final Object attribute = ((IdentifiedElement) fields.get(i)).getAttribute();
				if (attribute instanceof String) {
					final String translate = ApplicationManager.getTranslation((String) attribute, form.getResourceBundle());
					result.put(attribute, translate);
				}
			}
		}
		return result;
	}

	/**
	 * Get a Map with all attributes of ImageField in the form and their translations
	 * @param form
	 * @return
	 */
	public static Map getTemplateImages(final Form form) {
		final Map result = new Hashtable();
		// Changed in 5.2060EN-0.6 to get all component list in form (instead of
		// data components only)
		final List fields = form.getComponentList();
		for (int i = 0; i < fields.size(); i++) {
			if ((fields.get(i) instanceof ImageDataField) && ((DataField) fields.get(i)).isVisible()) {
				final Object attribute = ((ImageDataField) fields.get(i)).getAttribute();
				if (attribute instanceof String) {
					final String translate = ApplicationManager.getTranslation((String) attribute, form.getResourceBundle());
					result.put(attribute, translate);
				}
			}
			if ((fields.get(i) instanceof ITemplateField)
					&& (((ITemplateField) fields.get(i)).getTemplateDataType() == ITemplateField.DATA_TYPE_IMAGE)) {
				final Object attribute = ((IdentifiedElement) fields.get(i)).getAttribute();
				if (attribute instanceof String) {
					final String translate = ApplicationManager.getTranslation((String) attribute, form.getResourceBundle());
					result.put(attribute, translate);
				}
				if (attribute instanceof TableAttribute) {
					final String translate = ApplicationManager.getTranslation(((TableAttribute) attribute).getEntity(),
							form.getResourceBundle());
					result.put(((TableAttribute) attribute).getEntity(), translate);
				}
			}
		}
		return result;
	}

	protected static Map getTemplateHashtableTranslation(final List attrs, final ResourceBundle bundle) {
		if (attrs != null) {
			final Map translation = new Hashtable();
			for (int i = 0; i < attrs.size(); i++) {
				final String trans = ApplicationManager.getTranslation((String) attrs.get(i), bundle);
				translation.put(attrs.get(i), trans);
			}
			return translation;
		}
		return null;
	}

	/**
	 * Fill the specified template with current values in the form
	 * @param template
	 * @param form
	 */
	public static void fillTemplate(final File template, final Form form) {
		final InteractionManager im = form.getInteractionManager();
		if (template != null) {
			int templateType = -1;
			if (template.getName().endsWith("doc")) {
				templateType = TemplateUtils.TEMPLATE_TYPE_DOC;
			} else if (template.getName().endsWith("docx")) {
				templateType = TemplateUtils.TEMPLATE_TYPE_DOCX;
			} else if (template.getName().endsWith("odt")) {
				templateType = TemplateUtils.TEMPLATE_TYPE_ODT;
			} else if (template.getName().endsWith("pdf")) {
				templateType = TemplateUtils.TEMPLATE_TYPE_PDF;
			}
			try {
				final FileInputStream inputStream = new FileInputStream(template);
				TemplateUtils.getTemplateGenerator(templateType).setShowTemplate(true);
				final String suffix = template.getName().substring(template.getName().lastIndexOf("."));
				TemplateUtils.getTemplateGenerator(templateType)
				.fillDocument(inputStream, "template_" + System.currentTimeMillis() + suffix,
						im.getFieldValues(form),
						im.getTableValues(form), im.getImageValues(form, false));

			} catch (final Exception e) {
				TemplateUtils.logger.error(null, e);
				form.message("templates.fill_template_error", Form.ERROR_MESSAGE, e);
			}
		} else {
			form.message("templates.template_not_found", Form.WARNING_MESSAGE);
		}
	}

	/**
	 * Get a Map with the values of all fields in the form
	 * @param form
	 * @return
	 */
	public static Map getFieldsValues(final Form form) {
		final List components = form.getComponentList();
		final Map result = new Hashtable();
		for (int i = 0; i < components.size(); i++) {

			if ((components.get(i) instanceof ITemplateField)
					&& (((ITemplateField) components.get(i)).getTemplateDataType() == ITemplateField.DATA_TYPE_FIELD)) {
				final Object attribute = ((IdentifiedElement) components.get(i)).getAttribute();
				String attr = null;

				if (attribute instanceof String) {
					attr = (String) attribute;
				} else if (attribute instanceof ReferenceFieldAttribute) {
					attr = ((ReferenceFieldAttribute) attribute).getAttr();
				} else if (attribute instanceof MultipleReferenceDataFieldAttribute) {
					attr = ((MultipleReferenceDataFieldAttribute) attribute).getAttr();
				} else if (attribute != null) {
					attr = attribute.toString();
				}

				if (attr != null) {
					final Object templateDataValue = ((ITemplateField) components.get(i)).getTemplateDataValue();
					if (templateDataValue != null) {
						result.put(attr, templateDataValue);
					} else {
						result.put(attr, " - ");
					}
				}
				continue;
			}

			if ((components.get(i) instanceof DataField) && !(components.get(i) instanceof ImageDataField)) {
				final Object attribute = ((DataField) components.get(i)).getAttribute();
				String attr = null;

				if (attribute instanceof String) {
					attr = (String) attribute;
				} else if (attribute instanceof ReferenceFieldAttribute) {
					attr = ((ReferenceFieldAttribute) attribute).getAttr();
				} else if (attribute instanceof MultipleReferenceDataFieldAttribute) {
					attr = ((MultipleReferenceDataFieldAttribute) attribute).getAttr();
				} else if (attribute != null) {
					attr = attribute.toString();
				}

				if (attr != null) {
					if (((DataField) components.get(i)).getValue() != null) {
						result.put(attr, ((DataField) components.get(i)).getValue());
					} else {
						result.put(attr, " - ");
					}
				}
			}
		}
		return result;
	}

	/**
	 * Get a Map with the values of all tables in the form <br>
	 * key - table entity name <br>
	 * value - EntityResult with the table value
	 * @param form
	 * @return
	 */
	public static Map getTablesValues(final Form form) {
		final List components = form.getComponentList();
		final Map tables = new Hashtable();
		for (int i = 0; i < components.size(); i++) {
			if ((components.get(i) instanceof ITemplateField)
					&& (((ITemplateField) components.get(i)).getTemplateDataType() == ITemplateField.DATA_TYPE_TABLE)) {
				final Object attribute = ((IdentifiedElement) components.get(i)).getAttribute();
				String attr = null;
				if (attribute instanceof String) {
					attr = (String) attribute;
					if (attr.indexOf(".") > 0) {
						attr = attr.substring(attr.lastIndexOf(".") + 1);
					}

				} else if (attribute instanceof TableAttribute) {
					attr = ((TableAttribute) attribute).getEntity();
					if ((attr != null) && (attr.indexOf(".") > 0)) {
						attr = attr.substring(attr.lastIndexOf(".") + 1);
					}
				} else {
					attr = attribute.toString();
				}
				final Object dataValue = ((ITemplateField) components.get(i)).getTemplateDataValue();
				if (attr != null) {
					if (dataValue != null) {
						tables.put(attr, dataValue);
					} else {
						tables.put(attr, " - ");
					}
				}
				continue;
			}

			if (components.get(i) instanceof Table) {
				String entityName = ((Table) components.get(i)).getEntityName();
				if ((entityName == null) || (entityName.length() == 0)) {
					continue;
				}
				if (entityName.indexOf(".") > 0) {
					entityName = entityName.substring(entityName.lastIndexOf(".") + 1);
				}
				tables.put(entityName, ((Table) components.get(i)).getValue());
			}
		}
		return tables;
	}

	/**
	 * Get a Map with the values of all Image fields in the form
	 * @param form
	 * @param insertEmptyImages
	 * @return
	 */
	public static Map getImagesValues(final Form form, final boolean insertEmptyImages) {
		final List components = form.getComponentList();
		final Map result = new Hashtable();
		for (int i = 0; i < components.size(); i++) {
			if ((components.get(i) instanceof ITemplateField)
					&& (((ITemplateField) components.get(i)).getTemplateDataType() == ITemplateField.DATA_TYPE_IMAGE)) {

				final Object attribute = ((IdentifiedElement) components.get(i)).getAttribute();
				String attr = null;
				if (attribute instanceof String) {
					attr = (String) attribute;
				}
				if (attribute instanceof TableAttribute) {
					attr = ((TableAttribute) attribute).getEntity();
				}
				Object value = null;
				value = ((ITemplateField) components.get(i)).getTemplateDataValue();
				if ((value == null) && insertEmptyImages) {
					final BufferedImage bImage = new BufferedImage(3, 1, BufferedImage.TYPE_3BYTE_BGR);
					bImage.setRGB(0, 0, 16777215);
					result.put(attr, bImage);
				} else if (value != null) {
					result.put(attr, value);
				}
				continue;
			}
		}
		return result;

	}

}
