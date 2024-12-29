package com.ontimize.xml;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.printing.PrintingElement;
import com.ontimize.printing.TemplateBuilder;
import com.ontimize.printing.TemplateElement;

public class XMLTemplateBuilder extends XMLInterpreter implements TemplateBuilder {

	private static final Logger logger = LoggerFactory.getLogger(XMLTemplateBuilder.class);

	public static boolean INCLUDE_DEFAULT_TEMPLATE = false;

	protected Map equivalencesLabelList = new Hashtable();

	protected String labelFileURI = null;

	protected String myPackage = "com.ontimize.printing.";

	protected String uRIBase = null;

	private XMLTemplateBuilder() {
	}

	public void setLabelFile(final String labelFileURI) {
		this.labelFileURI = labelFileURI;
		try {
			this.processLabelFile(this.labelFileURI, this.equivalencesLabelList, new ArrayList());
		} catch (final Exception e) {
			XMLTemplateBuilder.logger.error("{}", e.getMessage(), e);
		}
	}

	public XMLTemplateBuilder(final String labelFileURI, final String packageName) throws Exception {
		this(labelFileURI);
		this.myPackage = packageName;
	}

	public XMLTemplateBuilder(final String labelFileURI) throws Exception {
		if (XMLTemplateBuilder.INCLUDE_DEFAULT_TEMPLATE) {
			this.equivalencesLabelList = this.getDefaultLabelList();
		}
		this.setLabelFile(labelFileURI);
	}

	public TemplateElement buildTemplate(final StringBuffer fileContent) {
		try {
			final CustomNode rootNode = new CustomNode(this.getDocumentModel(fileContent).getDocumentElement());
			final PrintingElement el = this.interpreterTag(rootNode, this.myPackage, this.equivalencesLabelList);
			if (el instanceof TemplateElement) {
				this.performChildren(rootNode, (TemplateElement) el);
				return (TemplateElement) el;
			} else {
				return null;
			}
		} catch (final Exception e) {
			XMLTemplateBuilder.logger.error(null, e);
			return null;
		}
	}

	@Override
	public TemplateElement buildTemplate(final String fileURI) {
		final CustomNode rootNode = new CustomNode(this.getDocumentModel(fileURI).getDocumentElement());
		final PrintingElement el = this.interpreterTag(rootNode, this.myPackage, this.equivalencesLabelList);
		if (el instanceof TemplateElement) {
			this.performChildren(rootNode, (TemplateElement) el);
			return (TemplateElement) el;
		} else {
			return null;
		}
	}

	protected void performChildren(final CustomNode node, final TemplateElement p) {
		final PrintingElement e = this.interpreterTag(node, this.myPackage, this.equivalencesLabelList);
		p.addPrintingElement(e);
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			final CustomNode auxNode = node.child(i);
			this.performChildren(auxNode, p);
		}
	}

	protected PrintingElement interpreterTag(final CustomNode childNode, final String packageName, final Map equivalenceLabelList) {
		if (childNode.isTag()) {
			final String tag = childNode.getNodeInfo();
			String className = (String) equivalenceLabelList.get(tag);
			if (className == null) {
				XMLTemplateBuilder.logger.debug("Tag not found: {}", tag);
				// Try with the tag
				className = tag;
			}
			// Converts the tag to the appropriate format for a class name using
			// the package name
			className = packageName + className;
			// Get the attribute list
			final NamedNodeMap attributeList = childNode.attributeList();
			final Map<String, String> hAttributesTable = new Hashtable<String, String>();
			for (int i = 0; i < attributeList.getLength(); i++) {
				final Node node = attributeList.item(i);
				hAttributesTable.put(node.getNodeName(), node.getNodeValue());
			}
			try {
				final Class classObject = Class.forName(className);
				try {
					final Constructor[] constructors = classObject.getConstructors();
					final Object[] parameters = { hAttributesTable };
					final PrintingElement e = (PrintingElement) constructors[0].newInstance(parameters);
					return e;
				} catch (final Exception e2) {
					XMLTemplateBuilder.logger.error("Error creating object. {}", className, e2);
					return null;
				}
			} catch (final Exception e) {
				XMLTemplateBuilder.logger.error("Error loading class: {}", className, e);
				return null;
			}
		}
		return null;
	}

	/**
	 * Creates and return a template associated with the specified locale.
	 * @param labelURI
	 * @param file Must be the name of the file without the locale suffix but with the extension, the
	 *        same as the resource bundles
	 * @param locale
	 * @return
	 */
	public static TemplateElement buildTemplate(final String labelURI, final String file, final Locale locale) {
		// Search the extension
		int lastDotIndex = file.lastIndexOf(".");
		if (lastDotIndex < 0) {
			XMLTemplateBuilder.logger.info("Dot not found in the name of the file.");
			lastDotIndex = file.length();
		}
		XMLTemplateBuilder.logger.debug("Loading template for the locale : {}", locale.toString());

		String sLocaleSuffix = "_" + locale.getLanguage() + "_" + locale.getCountry();
		final String variant = locale.getVariant();
		if ((variant != null) && !variant.equals("")) {
			sLocaleSuffix = sLocaleSuffix + "_ " + variant;
		}
		// File name:
		final String sFileName = file.substring(0, lastDotIndex);
		final String extension = file.substring(lastDotIndex, file.length());
		final String sCompleteName = sFileName + sLocaleSuffix + extension;
		final XMLTemplateBuilder constructor = new XMLTemplateBuilder();
		constructor.setLabelFile(labelURI);
		URL url = constructor.getClass().getClassLoader().getResource(sCompleteName);
		if (url == null) {
			XMLTemplateBuilder.logger.warn("Not found : {}", sCompleteName);
			// Try without the locale
			url = constructor.getClass().getClassLoader().getResource(file);
			if (url == null) {
				XMLTemplateBuilder.logger.warn("Neither found : {}", file);
			} else {
				return constructor.buildTemplate(url.toString());
			}
		} else {
			return constructor.buildTemplate(url.toString());
		}
		return null;
	}

}
