package com.ontimize.util.templates;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.bind.JAXBElement;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.TraversalUtil;
import org.docx4j.XmlUtils;
import org.docx4j.dml.wordprocessingDrawing.Inline;
import org.docx4j.finders.ClassFinder;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.WordprocessingML.BinaryPartAbstractImage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.openpackaging.parts.relationships.RelationshipsPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.vml.CTShape;
import org.docx4j.vml.CTTextbox;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.CTTxbxContent;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.Drawing;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.Pict;
import org.docx4j.wml.R;
import org.docx4j.wml.STBorder;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.TblBorders;
import org.docx4j.wml.TblPr;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.util.FileUtils;
import com.ontimize.windows.office.WindowsUtils;

public class DocxTemplateGenerator extends AbstractTemplateGenerator {

	private static final Logger logger = LoggerFactory.getLogger(DocxTemplateGenerator.class);

	private static final String HIDE = "HIDE";

	public static final String OPEN_PLACEHOLDER = "${";

	public static final String CLOSE_PLACEHOLDER = "}";

	protected boolean showTemplate = false;

	private static org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();

	/**
	 * Populate the templates with the information given in the {@link Hashtable}s.
	 * @param input : An {@link InputStream} from the original template, which will remain. unaltered
	 * @param nameFile : The name of the new *.docx document, with extension included.
	 * @param fieldValues : A {@link Hashtable} with the data used in the fields of the form. As key,
	 *        the attribute to replace and as value, their replace.
	 * @param valuesTable : A {@link Hashtable} with the data used in the tables of the form. As key,
	 *        the entity name of the table, as value, the {@link EntityResult} will all table data.
	 * @param valuesImages : A {@link Hashtable} with the images in the form. As key, the attribute to
	 *        replace and as value, their image replace (like a {@link BytesBlock} or a
	 *        {@link BufferedImage})
	 * @param valuesPivotTable : A {@link Hashtable} of pivots table (NOT USED)
	 * @throws Exception
	 */
	@Override
	public File fillDocument(final InputStream input, final String nameFile, Map fieldValues, final Map valuesTable,
			Map valuesImages, final Map valuesPivotTable)
					throws Exception {
		fieldValues = this.translateDotFields(fieldValues);
		fieldValues = this.translateToPatternFields(fieldValues);
		valuesImages = this.translateDotFields(valuesImages);
		valuesImages = this.translateToPatternFields(valuesImages);
		final File templateFilled = new File(System.getProperty("java.io.tmpdir"), nameFile);
		templateFilled.deleteOnExit();
		FileUtils.copyFile(input, templateFilled);

		this.findAndReplaceDocument(templateFilled, fieldValues, valuesTable, valuesImages, valuesPivotTable);

		if (this.showTemplate) {
			WindowsUtils.openFile_Script(templateFilled);
		}

		return templateFilled;
	}

	/**
	 * Find and replace the placeholders of the template with the data given in the {@link Hashtable}
	 * @param templateFilled : The {@link File} of template to fill.
	 * @param fieldValues : A {@link Hashtable} with the data used in the fields of the form. As key,
	 *        the placeholder to replace and as value, their replace.
	 * @param valuesTable : A {@link Hashtable} with the data used in the tables of the form. As key,
	 *        the entity name of the table, as value, the {@link EntityResult} will all table data.
	 * @param valuesImages : A {@link Hashtable} with the images in the form. As key, the placeholder to
	 *        replace and as value, their image replace (like a {@link BytesBlock} or a
	 *        {@link BufferedImage})
	 * @param valuesPivotTable : A {@link Hashtable} of pivots table (NOT USED)
	 * @throws Exception
	 */
	protected void findAndReplaceDocument(final File templateFilled, final Map fieldValues, final Map valuesTable,
			final Map valuesImages, final Map valuesPivotTable) throws Exception {
		final WordprocessingMLPackage mlp = WordprocessingMLPackage.load(templateFilled);
		this.findAndReplaceMainTables(mlp, valuesTable);
		this.findAndReplaceMainFields(mlp, fieldValues);
		this.findAndReplaceMainImages(mlp, valuesImages);
		this.findAndReplaceTextBox(mlp, valuesTable, valuesImages, fieldValues);
		mlp.save(templateFilled);
	}

	/**
	 * Find and replace the main tables in template which have reference an entity.
	 * @param mlp : The {@link WordprocessingMLPackage} obtained from load the template file.
	 * @param valuesTable : A {@link Hashtable} with the data used in the tables of the form. As key,
	 *        the entity name of the table, as value, the {@link EntityResult} with all table data.
	 */
	protected void findAndReplaceMainTables(final WordprocessingMLPackage mlp, final Map valuesTable) {

		if (!valuesTable.isEmpty()) {
			final Enumeration tableEntitiesKey = Collections.enumeration(valuesTable.keySet());
			while (tableEntitiesKey.hasMoreElements()) {
				final Object oActualTableEntity = tableEntitiesKey.nextElement();

				// Search for template tables
				this.findAndReplaceTables(mlp.getMainDocumentPart(), valuesTable, oActualTableEntity);

				final RelationshipsPart rp = mlp.getMainDocumentPart().getRelationshipsPart();
				for (final Relationship r : rp.getRelationships().getRelationship()) {
					if (r.getType().equals(Namespaces.HEADER) || r.getType().equals(Namespaces.FOOTER)) {
						final Part part = rp.getPart(r);
						this.findAndReplaceTables(part, valuesTable, oActualTableEntity);
					}
				}
			}
		}
	}

	/**
	 * Find and replace the tables in template which have reference an entity.
	 * @param element : The element witch has a table content
	 * @param valuesTable : A {@link Hashtable} with the data used in the tables of the form. As key,
	 *        the entity name of the table, as value, the {@link EntityResult} will all table data.
	 * @param oActualTableEntity : Name of the actual table
	 * @param sActualTableEntity : Name of the actual table as placeholder
	 */
	protected void findAndReplaceTables(final Object element, final Map valuesTable, final Object oActualTableEntity) {

		final String sActualTableEntity = this.entityAsPlaceholder(oActualTableEntity.toString());

		final List<Tbl> tables = this.getElements(element, Tbl.class);
		for (final Tbl entityTables : tables) {
			// Search for table rows
			final List<Tr> rows = this.getElements(entityTables, Tr.class);
			for (final Tr row : rows) {
				final List<Tc> cols = this.getElements(row, Tc.class);
				for (final Tc col : cols) {
					// Search for row cells
					final List<Text> texts = this.getElements(col, Text.class);
					for (final Text oText : texts) {
						// Search inside cells for entity
						// placeholder
						final int indexB = oText.getValue().indexOf(sActualTableEntity);
						if (indexB > -1) {
							final int indexRowEntityPlaceholder = rows.indexOf(row);
							entityTables.getContent().remove(row);
							final Object actualTableValues = valuesTable.get(oActualTableEntity);
							// Populate table
							if (actualTableValues instanceof EntityResult) {
								final EntityResult actualValues = (EntityResult) actualTableValues;
								this.populateTbl(entityTables, actualValues, indexRowEntityPlaceholder);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Populate the table which references a {@link Table} in the Ontimize form.
	 * @param entityTables : The {@link Tbl} WITHOUT the row that indicates its entity.
	 * @param actualValues : The {@link EntityResult} of table data.
	 * @param indexRowEntityPlaceholder : The index of row entity placeholder BEFORE its removal
	 * @param mlp : The {@link WordprocessingMLPackage} obtained from load the template file.
	 */
	protected void populateTbl(final Tbl entityTables, final EntityResult actualValues,
			final int indexRowEntityPlaceholder) {
		final Vector<String> placeholders = new Vector<String>();
		// Vector<String> cellTexts = new Vector<String>();
		final List persistentRows = new ArrayList();
		// Search for table rows
		final List<Tr> rows = this.getElements(entityTables, Tr.class);
		for (final Tr row : rows) {
			if (rows.indexOf(row) >= indexRowEntityPlaceholder) {
				persistentRows.add(row);
				entityTables.getContent().remove(row);
			}
		}

		// Populate the table
		final int records = actualValues.calculateRecordNumber();
		final Map<Object, Object> mapActualValues = com.ontimize.jee.common.util.EntityResultUtils.toMap(actualValues);
		this.translateToPatternFields(mapActualValues);
		final EntityResult actualValuesTrans = new EntityResultMapImpl(new HashMap(mapActualValues));
		actualValuesTrans.setColumnOrder(actualValues.getOrderColumns());
		for (int i = 0; i < records; i++) {
			final Map actualRecord = actualValuesTrans.getRecordValues(i);
			for (final Object persistentRow : persistentRows) {
				final Tr recordRow = (Tr) XmlUtils.deepCopy(persistentRow);
				final List<Tc> cells = this.getElements(recordRow, Tc.class);
				for (final Tc cell : cells) {
					final List<Text> cellTexts = this.getElements(cell, Text.class);
					for (final Text oText : cellTexts) {
						final int indexB = oText.getValue().indexOf(DocxTemplateGenerator.OPEN_PLACEHOLDER);
						if (indexB > -1) {
							final String placeholder = oText.getValue()
									.substring(indexB,
											oText.getValue().indexOf(DocxTemplateGenerator.CLOSE_PLACEHOLDER) + 1);
							try {
								oText.setValue(oText.getValue()
										.replace(placeholder, actualRecord.get(placeholder).toString()));
							} catch (final Exception e) {
								DocxTemplateGenerator.logger.debug("Error obtaining the value for the placeholder: {}",
										placeholder, e);
							}
						}
					}
				}
				entityTables.getContent().add(recordRow);
			}
		}
	}

	/**
	 * Return the entity name of a table as placeholder
	 * @param entity : The entity name {@link String}
	 * @return The entity name placeholder.
	 */
	protected String entityAsPlaceholder(final String entity) {
		final StringBuilder toRet = new StringBuilder();
		toRet.append(DocxTemplateGenerator.OPEN_PLACEHOLDER);
		toRet.append(entity.toUpperCase());
		toRet.append(DocxTemplateGenerator.CLOSE_PLACEHOLDER);
		return toRet.toString();
	}

	/**
	 * Find and replace the placeholder of main images in template.
	 * @param mlp : The {@link WordprocessingMLPackage} obtained from load the template file.
	 * @param valuesTable : A {@link Hashtable} with the images in the form. As key, the placeholder to
	 *        replace and as value, their image replace (like a {@link BytesBlock} or a
	 *        {@link BufferedImage})
	 * @throws Exception
	 */
	protected void findAndReplaceMainImages(final WordprocessingMLPackage mlp, final Map valuesImages) throws Exception {

		if (!valuesImages.isEmpty()) {

			this.findAndReplaceImages(mlp.getMainDocumentPart(), valuesImages, mlp);

			final String[] keyIndex = (String[]) valuesImages.keySet().toArray(new String[0]);
			final List<List> replaceImages = new ArrayList<>();

			final RelationshipsPart rp = mlp.getMainDocumentPart().getRelationshipsPart();
			for (final Relationship r : rp.getRelationships().getRelationship()) {
				if (r.getType().equals(Namespaces.HEADER) || r.getType().equals(Namespaces.FOOTER)) {
					final Part part = rp.getPart(r);
					final List<Tbl> partTables = this.getElements(part, Tbl.class);
					for (final Tbl imgTables : partTables) {
						// Search for table rows
						final List<Tr> rows = this.getElements(imgTables, Tr.class);
						for (final Tr row : rows) {
							// Search for row cells
							final List<Tc> cols = this.getElements(row, Tc.class);
							for (final Tc col : cols) {
								final List<Text> texts = this.getElements(col, Text.class);
								for (final Text oText : texts) {
									// Search inside cells for image
									// placeholder
									final int indexB = StringUtils.indexOfAny(oText.getValue(), keyIndex);
									if (indexB > -1) {
										final String tString = oText.getValue()
												.substring(indexB,
														oText.getValue().indexOf(DocxTemplateGenerator.CLOSE_PLACEHOLDER)
														+ 1);
										final Object oValue = valuesImages.get(tString);

										final List params = new ArrayList();
										params.add(col);
										params.add(oValue);
										params.add(tString);
										params.add(part);
										replaceImages.add(params);

									}
								}

							}
						}
					}
				}

			}

			for (final List l : replaceImages) {
				final Tc col = (Tc) l.get(0);
				final Object oValue = l.get(1);
				final String tString = l.get(2).toString();
				final Part part = (Part) l.get(3);
				byte[] imageBytes = null;

				if (oValue instanceof BytesBlock) {
					imageBytes = ((BytesBlock) oValue).getBytes();
				} else if (oValue instanceof BufferedImage) {
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write((RenderedImage) oValue, "png", baos);
					baos.flush();
					imageBytes = baos.toByteArray();
				}

				final List<R> rs = this.getElements(col, R.class);
				for (final R o : rs) {
					final R run = o;
					if (imageBytes != null) {
						final R newRun = this.createImageRun(mlp, imageBytes, tString, part,
								col.getTcPr().getTcW().getW().longValue());
						final P p = (P) run.getParent();
						p.getContent().remove(run);
						p.getContent().add(newRun);

					}
				}
			}
		}
	}

	protected void findAndReplaceImages(final Object element, final Map valuesImages, final WordprocessingMLPackage mlp)
			throws Exception {

		final String[] keyIndex = (String[]) valuesImages.keySet().toArray(new String[0]);

		// Search for template tables
		final List<Tbl> tables = this.getElements(element, Tbl.class);
		for (final Tbl imgTables : tables) {
			// Search for table rows
			final List<Tr> rows = this.getElements(imgTables, Tr.class);
			for (final Tr row : rows) {
				// Search for row cells
				final List<Tc> cols = this.getElements(row, Tc.class);
				for (final Tc col : cols) {
					final List<Text> texts = this.getElements(col, Text.class);
					for (final Text oText : texts) {
						// Search inside cells
						// for image placeholder
						final int indexB = StringUtils.indexOfAny(oText.getValue(), keyIndex);
						if (indexB > -1) {
							final String tString = oText.getValue()
									.substring(indexB,
											oText.getValue().indexOf(DocxTemplateGenerator.CLOSE_PLACEHOLDER) + 1);
							final Object oValue = valuesImages.get(tString);

							if (oValue instanceof BytesBlock) {
								final P paragraphWithImage = this.paragraphContentImage(
										this.inlineImage(((BytesBlock) oValue).getBytes(), mlp, tString));
								col.getContent().remove(0);
								col.getContent().add(paragraphWithImage);
							}

							if (oValue instanceof BufferedImage) {
								final ByteArrayOutputStream baos = new ByteArrayOutputStream();
								ImageIO.write((RenderedImage) oValue, "png", baos);
								baos.flush();
								final P paragraphWithImage = this
										.paragraphContentImage(this.inlineImage(baos.toByteArray(), mlp, tString));
								col.getContent().remove(0);
								col.getContent().add(paragraphWithImage);

							}
						}
					}
				}
			}
		}

	}

	/**
	 * Create a {@link P} with the {@link Inline} image received by parameter.
	 * @param createInlineImage : An {@link Inline} image to be sorrounded with a {@link P}
	 * @return A {@link P} paragraph with the image
	 */
	protected P paragraphContentImage(final Inline createInlineImage) {
		final ObjectFactory factory = new ObjectFactory();
		final P p = factory.createP();
		final R r = factory.createR();
		p.getContent().add(r);
		final Drawing drawing = factory.createDrawing();
		r.getContent().add(drawing);
		drawing.getAnchorOrInline().add(createInlineImage);
		return p;
	}

	public R createImageRun(final WordprocessingMLPackage wordMLPackage, final byte[] imageBytes, final String tString, final Part part,
			final long imageWidth) throws Exception {
		final BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(wordMLPackage.getPackage(), part,
				imageBytes);
		final Inline inline = imagePart.createImageInline(null, tString, 1, 2, imageWidth, false);
		final org.docx4j.wml.ObjectFactory factory = Context.getWmlObjectFactory();
		final org.docx4j.wml.R run = factory.createR();
		final org.docx4j.wml.Drawing drawing = factory.createDrawing();
		run.getContent().add(drawing);
		drawing.getAnchorOrInline().add(inline);
		return run;
	}

	/**
	 * Creates an {@link Inline} image.
	 * @param ba : A bytes array from the image
	 * @param mlp : A {@link WordprocessingMLPackage} from load a template {@link File}
	 * @param filenameHint : A {@link String} with the image hint, for example, the original name
	 * @return An {@link Inline} object built with the bytes array of an image
	 * @throws Exception
	 */
	protected Inline inlineImage(final byte[] ba, final WordprocessingMLPackage mlp, final String filenameHint) throws Exception {

		final BinaryPartAbstractImage imagePart = BinaryPartAbstractImage.createImagePart(mlp, ba);

		final int docPrId = 1;
		final int cNvPrId = 2;

		return imagePart.createImageInline(filenameHint, "Image", docPrId, cNvPrId, false);
	}

	/**
	 * Get a {@link List} of elements of the class specified by classFinder, inside the obj.
	 * @param obj : The source object.
	 * @param classFinder : The {@link Class} of the object to search.
	 * @return A {@link List} with the objects of type classFinder inside obj
	 */
	public <T> List<T> getElements(Object obj, final Class<T> classFinder) {
		final List<T> result = new ArrayList<T>();
		if (obj instanceof JAXBElement) {
			obj = ((JAXBElement<?>) obj).getValue();
		}

		if (obj.getClass().equals(classFinder)) {
			result.add((T) obj);
		} else if (obj instanceof ContentAccessor) {
			final List<?> children = ((ContentAccessor) obj).getContent();
			for (final Object child : children) {
				result.addAll(this.getElements(child, classFinder));
			}
		}

		return result;
	}

	/**
	 * Find and replace the placeholder of main fields in template.
	 * @param doc : The element with has fields placeholders.
	 * @param fieldValues : A {@link Hashtable} with the images in the form. As key, the placeholder to
	 *        replace and as value, their replace.
	 */
	public void findAndReplaceMainFields(final WordprocessingMLPackage doc, final Map fieldValues) {
		if (!fieldValues.isEmpty()) {

			this.findAndReplaceFields(doc.getMainDocumentPart(), fieldValues);

			final RelationshipsPart rp = doc.getMainDocumentPart().getRelationshipsPart();
			for (final Relationship r : rp.getRelationships().getRelationship()) {
				if (r.getType().equals(Namespaces.HEADER) || r.getType().equals(Namespaces.FOOTER)) {
					final Part part = rp.getPart(r);
					this.findAndReplaceFields(part, fieldValues);
				}
			}
		}
	}

	/**
	 * Find and replace the placeholder of fields in template.
	 * @param doc : The {@link WordprocessingMLPackage} obtained from load the template file.
	 * @param fieldValues : A {@link Hashtable} with the images in the form. As key, the placeholder to
	 *        replace and as value, their replace.
	 */
	protected void findAndReplaceFields(final Object element, final Map fieldValues) {
		final String[] keyIndex = (String[]) fieldValues.keySet().toArray(new String[0]);

		final List<Text> texts = this.getElements(element, Text.class);
		for (final Text text : texts) {
			String tString;
			while (StringUtils.indexOfAny(text.getValue(), keyIndex) > -1) {
				final int indexB = StringUtils.indexOfAny(text.getValue(), keyIndex);
				if (indexB > -1) {
					tString = text.getValue()
							.substring(indexB, text.getValue().indexOf(DocxTemplateGenerator.CLOSE_PLACEHOLDER) + 1);
					try {
						final String sValue = fieldValues.get(tString).toString();
						text.setValue(text.getValue().replace(tString, sValue));
					} catch (final Exception e) {
						DocxTemplateGenerator.logger.debug("Error obtaining the value for the placeholder: {}", tString,
								e);
					}

				}
			}
		}
	}

	/**
	 * Find and replace the placeholder of an field in template in a textBox.
	 * @param doc : The {@link WordprocessingMLPackage} obtained from load the template file.
	 * @param fieldValues : A {@link Hashtable} with the fieldValues in the form. As key, the
	 *        placeholder to replace and as value, their replace.
	 * @param fieldValues2
	 * @param valuesImages
	 * @throws Exception
	 */
	protected void findAndReplaceTextBox(final WordprocessingMLPackage doc, final Map valuesTable, final Map valuesImages,
			final Map fieldValues) throws Exception {

		/* Check if field values is empty */
		if (!fieldValues.isEmpty()) {
			final String[] keyIndex = (String[]) fieldValues.keySet().toArray(new String[0]);

			final List<Pict> pict = this.getElements(doc.getMainDocumentPart(), Pict.class);
			for (final Pict p : pict) {
				final List<Object> pictContent = p.getAnyAndAny();
				for (final Object o : pictContent) {
					final List<CTShape> filterCTShape = this.getElements(o, CTShape.class);
					if (!filterCTShape.isEmpty()) {
						for (final CTShape ctShape : filterCTShape) {
							final List<JAXBElement<?>> ctShapeContent = ctShape.getEGShapeElements();
							for (final Object oJaxbActual : ctShapeContent) {
								final List<CTTextbox> ctTextBox = this.getElements(oJaxbActual,
										org.docx4j.vml.CTTextbox.class);
								for (final CTTextbox cttextbox : ctTextBox) {
									final CTTxbxContent textboxContent = cttextbox.getTxbxContent();
									if (!fieldValues.isEmpty()) {
										this.findAndReplaceFields(textboxContent, fieldValues);
									}

									if (!valuesTable.isEmpty()) {
										final Enumeration tableEntitiesKey = Collections.enumeration(valuesTable.keySet());
										while (tableEntitiesKey.hasMoreElements()) {
											final Object oActualTableEntity = tableEntitiesKey.nextElement();
											this.findAndReplaceTables(textboxContent, valuesTable, oActualTableEntity);
										}
									}

									if (!valuesImages.isEmpty()) {
										this.findAndReplaceImages(textboxContent, valuesImages, doc);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Set if the template will be open after its creation
	 * @param show Boolean that indicates if the template will be open after its creation or not.
	 */
	@Override
	public void setShowTemplate(final boolean show) {
		this.showTemplate = show;

	}

	/**
	 * The {@link DocxTemplateGenerator} adds the possibility to put the fields in the template in a
	 * specified order.<br>
	 * The parameter <code>fieldValues</code> can be a {@link EntityResult} object, and use the
	 * {@link EntityResult#setColumnOrder(List)} method to specified the order of the fields in the
	 * template.<br>
	 * @return The template file.
	 */
	@Override
	public File createTemplate(Map fieldValues, final Map valuesTable, Map valuesImages) throws Exception {
		try {
			fieldValues = this.translateDotFields(fieldValues);
			valuesImages = this.translateDotFields(valuesImages);

			if (valuesImages != null) {
				if (fieldValues instanceof EntityResult) {
					final List orderColumns = ((EntityResult) fieldValues).getOrderColumns();
					if ((orderColumns != null) && (orderColumns.size() > 0)) {
						final Enumeration imageKeys = Collections.enumeration(valuesImages.keySet());
						while (imageKeys.hasMoreElements()) {
							final Object key = imageKeys.nextElement();
							orderColumns.add(key);
							fieldValues.put(key, valuesImages.get(key));
						}
						((EntityResult) fieldValues).setColumnOrder(orderColumns);
					}
				} else {
					fieldValues.putAll(valuesImages);
				}
			}
		} catch (final Exception e) {
			DocxTemplateGenerator.logger.error(null, e);
		}

		final String userDirectory = System.getProperty("java.io.tmpdir");
		final File template = new File(userDirectory,
				FileUtils.getFileName("~template_" + System.currentTimeMillis() + ".docx"));
		this.generateDocxTemplate(template, fieldValues, valuesTable, valuesImages);
		if (this.showTemplate) {
			WindowsUtils.openFile_Script(template);
		}
		template.deleteOnExit();
		return template;

	}

	@Override
	public List queryTemplateFields(final File template) throws Exception {

		final List toRet = new ArrayList<String>();
		final WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(template);
		final MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();

		final ClassFinder finder = new ClassFinder(P.class);
		final TraversalUtil tUtil = new TraversalUtil(mdp.getContent(), finder);

		final Pattern p = Pattern.compile("\\$\\{([^}]++)\\}");

		for (final Object obj : finder.results) {
			final Matcher m = p.matcher(obj.toString());
			while (m.find()) {
				toRet.add(m.group());
			}
		}

		return toRet;
	}

	@Override
	public List queryTemplateFields(final String template) throws Exception {

		final File fileTemplate = new File(template);
		return this.queryTemplateFields(fileTemplate);
	}

	/**
	 * Change dot character "." on keys with "ç" character
	 * @param values : Original Hashtable
	 * @return A Map with the key field translated
	 */
	protected Map translateDotFields(final Map values) {
		try {
			final Map translations = new Hashtable();
			if (values == null) {
				return translations;
			}
			final Iterator valuesit = values.keySet().iterator();
			while (valuesit.hasNext()) {
				final Object o = valuesit.next();
				if ((o != null) && (o instanceof String)) {
					final String str = o.toString();
					if (str.indexOf(".") > -1) {
						final String stralt = str.replaceAll("\\.", "ç");
						translations.put(str, stralt);
					}
				}
			}
			final Iterator transit = translations.keySet().iterator();
			while (transit.hasNext()) {
				final String val = transit.next().toString();
				values.put(translations.get(val), values.remove(val));
			}
			if (values instanceof EntityResult) {
				((EntityResult) values).setColumnOrder(new Vector(translations.values()));
			}
		} catch (final Exception e) {
			DocxTemplateGenerator.logger.error(null, e);
		}
		return values;
	}

	/**
	 * Change the form attributes keys for placeholder keys pattern
	 * @param values
	 * @return A Map with the key field translated
	 */
	public Map translateToPatternFields(final Map values) {
		try {
			final Map<String, String> translations = new Hashtable<>();
			if (values == null) {
				return translations;
			}
			final Iterator valuesit = values.keySet().iterator();
			while (valuesit.hasNext()) {
				final Object o = valuesit.next();

				if ((o != null) && (o instanceof String)) {
					final String oStr = (String) o;
					if (!((oStr.indexOf("${") > -1) && (oStr.lastIndexOf("}") > -1))) {

						final StringBuilder buffer = new StringBuilder();
						buffer.append(DocxTemplateGenerator.OPEN_PLACEHOLDER + oStr.toUpperCase()
						+ DocxTemplateGenerator.CLOSE_PLACEHOLDER);
						translations.put(oStr, buffer.toString());
					}
				}
			}
			final Iterator<String> transit = translations.keySet().iterator();
			while (transit.hasNext()) {
				final String val = transit.next();
				values.put(translations.get(val), values.remove(val));
			}
			if (values instanceof EntityResult) {
				((EntityResult) values).setColumnOrder(new Vector(translations.values()));
			}
		} catch (final Exception e) {
			DocxTemplateGenerator.logger.error(null, e);
		}
		return values;
	}

	/**
	 * Create a *.docx document template
	 * @param template The file template
	 * @param fieldValues This object contains the data fields attributes and labels to show in the
	 *        template
	 * @param valuesTable The object contains the table information to insert in the template. This map
	 *        must have the table entity name as key and the value must be other Map with the
	 *        columns attributes and names to show (column name - column label)
	 * @param valuesImages The object contains information about the image fields which owns the form.
	 *        This map contains the name of the image field (value) and its attribute (key)
	 * @throws Exception
	 */
	protected void generateDocxTemplate(final File template, final Map fieldValues, final Map valuesTable,
			final Map valuesImages) throws Exception {

		final WordprocessingMLPackage docxDocument = WordprocessingMLPackage.createPackage();
		final MainDocumentPart mainDocument = docxDocument.getMainDocumentPart();

		final Enumeration imageKeys = Collections.enumeration(valuesImages.keySet());
		while (imageKeys.hasMoreElements()) {
			final Object actualKey = imageKeys.nextElement();
			fieldValues.remove(actualKey);
		}

		final List<String> paragraphs = this.createFieldString(fieldValues);
		for (final String s : paragraphs) {
			mainDocument.addParagraphOfText(s);
		}

		final List<Tbl> images = this.createImagesField(valuesImages, mainDocument);
		for (final Tbl i : images) {
			mainDocument.addParagraphOfText("");
			mainDocument.addObject(i);

		}

		final Vector<Tbl> tables = this.createTablesObject(valuesTable, mainDocument);
		for (final Tbl t : tables) {
			mainDocument.addParagraphOfText("");
			this.addBorders(t);
			mainDocument.addObject(t);
		}

		mainDocument.addParagraphOfText("");

		docxDocument.save(template);
		template.deleteOnExit();

	}

	/**
	 * Return a {@link Vector} of {@link Tbl} to store the image placeholder
	 * @param valuesImages : A {@link Hashtable} with the images in the form. As key, the placeholder to
	 *        replace and as value, their image replace (like a {@link BytesBlock} or a
	 *        {@link BufferedImage})
	 * @param mainDocument : A {@link MainDocumentPart} of the template file.
	 * @return a {@link Vector} of {@link Tbl} which contents the placeholder of the image.
	 */
	protected Vector<Tbl> createImagesField(final Map valuesImages, final MainDocumentPart mainDocument) {
		final Vector<Tbl> toRet = new Vector<Tbl>();

		final Enumeration keys = Collections.enumeration(valuesImages.keySet());

		while (keys.hasMoreElements()) {
			final Tbl table = DocxTemplateGenerator.factory.createTbl();
			final Tr tableRowContent = DocxTemplateGenerator.factory.createTr();

			final Object actualHeaderKey = keys.nextElement();
			final Object actualFieldValue = valuesImages.get(actualHeaderKey);

			this.addTableCell(tableRowContent,
					DocxTemplateGenerator.OPEN_PLACEHOLDER + actualHeaderKey.toString().toUpperCase()
					+ DocxTemplateGenerator.CLOSE_PLACEHOLDER,
					mainDocument);

			table.getContent().add(tableRowContent);

			toRet.add(table);
		}
		return toRet;
	}

	/**
	 * Build a List of single{@link String} with all of the fields data in the Hahstable
	 * <code>fieldHashtable</code>. This map stored the label of the field as <code>value</code> and the
	 * attribute of the field as <code>key</code>
	 * @param fieldHashtable : The field value data
	 * @return A List of {@link String} with all of the field data stored in the
	 *         <code>fieldHashtable</code>
	 */
	protected List<String> createFieldString(final Map fieldHashtable) {
		final List<String> toRet = new Vector<String>();

		final Enumeration keys = Collections.enumeration(fieldHashtable.keySet());
		while (keys.hasMoreElements()) {
			final Object objKey = keys.nextElement();
			final Object objValue = fieldHashtable.get(objKey);

			final StringBuilder buffer = new StringBuilder();
			buffer.append(objValue.toString());
			buffer.append(": ");
			buffer.append(DocxTemplateGenerator.OPEN_PLACEHOLDER + objKey.toString().toUpperCase()
					+ DocxTemplateGenerator.CLOSE_PLACEHOLDER);

			toRet.add(buffer.toString());
		}
		return toRet;
	}

	/**
	 * Create a single table with two rows. A header row, that has the name of the columns, and a body
	 * row, with the attribute of columns
	 * @param tableObjectKey
	 * @param valuesSingleTable : A Map which has the name of the columns ad its respective
	 *        attributes.
	 * @param mainDocument : The {@link MainDocumentPart} of the template, where the table will be in.
	 * @return A single table with the built with the <code>valuesSingleTable</code> data
	 */
	protected Tbl createSingleTable(final String tableObjectKey, final Map valuesSingleTable, final MainDocumentPart mainDocument) {

		final Tbl table = DocxTemplateGenerator.factory.createTbl();
		final Tr tableRowEntity = DocxTemplateGenerator.factory.createTr();
		final Tr tableRowHeader = DocxTemplateGenerator.factory.createTr();
		final Tr tableRowContent = DocxTemplateGenerator.factory.createTr();

		final Enumeration keys = Collections.enumeration(valuesSingleTable.keySet());
		boolean entity = true;
		while (keys.hasMoreElements()) {
			final Object actualHeaderKey = keys.nextElement();
			final Object actualFieldValue = valuesSingleTable.get(actualHeaderKey);

			if (entity) {
				this.addTableCell(tableRowEntity, DocxTemplateGenerator.OPEN_PLACEHOLDER + tableObjectKey.toUpperCase()
				+ DocxTemplateGenerator.CLOSE_PLACEHOLDER, mainDocument);
			} else {
				this.addTableCell(tableRowEntity, "", mainDocument);
			}
			this.addTableCell(tableRowHeader, actualFieldValue.toString(), mainDocument);
			this.addTableCell(tableRowContent,
					DocxTemplateGenerator.OPEN_PLACEHOLDER + actualHeaderKey.toString().toUpperCase()
					+ DocxTemplateGenerator.CLOSE_PLACEHOLDER,
					mainDocument);
			entity = false;
		}

		table.getContent().add(tableRowHeader);
		table.getContent().add(tableRowEntity);
		table.getContent().add(tableRowContent);

		return table;
	}

	/**
	 * Create a List of single {@link Tbl} table, which the data stored in <code>valuesTable</code>.
	 * See {@link #createSingleTable(Hashtable, MainDocumentPart)}
	 * @param valuesTable : The object contains the table information to insert in the template. This
	 *        map must have the table entity name as key and the value must be other Map with the
	 *        columns attributes and names to show (column name - column label)
	 * @param mainDocument : The {@link MainDocumentPart} of the template, where the table will be in.
	 * @return A List of {@link Tbl} tables.
	 */
	protected Vector<Tbl> createTablesObject(final Map valuesTable, final MainDocumentPart mainDocument) {
		final Vector<Tbl> vectorTables = new Vector<Tbl>();
		final Enumeration keys = Collections.enumeration(valuesTable.keySet());
		while (keys.hasMoreElements()) {
			final Object tableObjectKey = keys.nextElement();
			final Map valuesTableObjectKey = (Map) valuesTable.get(tableObjectKey);
			final Tbl tableForActualTableKey = this.createSingleTable(tableObjectKey.toString(), valuesTableObjectKey,
					mainDocument);
			vectorTables.add(tableForActualTableKey);
		}
		return vectorTables;
	}

	/**
	 * Add content to a cell, and then add it to the row
	 * @param tableRow The table row where the cell will be added
	 * @param content The content of the cell
	 * @param mainDocument : The {@link MainDocumentPart} of the template, where the cell will be in.
	 */
	protected void addTableCell(final Tr tableRow, final String content, final MainDocumentPart mainDocument) {
		final Tc tableCell = DocxTemplateGenerator.factory.createTc();
		tableCell.getContent().add(mainDocument.createParagraphOfText(content));
		tableRow.getContent().add(tableCell);
	}

	/**
	 * Add single borders to the table.
	 * @param table : The source table
	 */
	protected void addBorders(final Tbl table) {
		table.setTblPr(new TblPr());
		final CTBorder border = new CTBorder();
		border.setColor("auto");
		border.setSz(new BigInteger("4"));
		border.setSpace(new BigInteger("0"));
		border.setVal(STBorder.SINGLE);

		final TblBorders borders = new TblBorders();
		borders.setBottom(border);
		borders.setLeft(border);
		borders.setRight(border);
		borders.setTop(border);
		borders.setInsideH(border);
		borders.setInsideV(border);
		table.getTblPr().setTblBorders(borders);
	}

}
