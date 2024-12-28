package com.ontimize.builder.xml;

import java.awt.Component;
import java.awt.Container;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ontimize.builder.ButtonBarBuilder;
import com.ontimize.gui.ApToolBarModule;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.util.extend.ExtendedMenuXmlParser;
import com.ontimize.jee.common.util.extend.ExtendedXmlParser;
import com.ontimize.xml.DefaultXMLParametersManager;
import com.ontimize.xml.XMLInterpreter;

public class XMLButtonBarBuilder extends XMLInterpreter implements ButtonBarBuilder {

	private static final Logger logger = LoggerFactory.getLogger(XMLButtonBarBuilder.class);

	protected String baseClasspath = null;

	public static boolean INCLUDE_DEFAULT_LABELS = true;

	protected String defaultPackage = "com.ontimize.gui.";

	protected Map equivalenceLabelList = new Hashtable();

	protected JToolBar toolbar = null;

	protected static ExtendedMenuXmlParser toolbarParser = new ExtendedMenuXmlParser();

	/**
	 * Class used to create the application toolbar.
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 *        The classes indicate in the XML file are in package com.ontimize.gui and this class uses
	 *        reflection (Class.forName()) to load them.
	 * @throws Exception
	 */
	public XMLButtonBarBuilder(final String uriLabelsFile) throws Exception {
		if (XMLButtonBarBuilder.INCLUDE_DEFAULT_LABELS) {
			this.equivalenceLabelList = this.getDefaultLabelList();
		}
		try {
			this.processLabelFile(uriLabelsFile, this.equivalenceLabelList, new ArrayList());
		} catch (final Exception e) {
			XMLButtonBarBuilder.logger.error("Processing label file", e);
		}
	}

	@Override
	public void appendButtonBar(final JToolBar toolbar, final String xmlDefinition) {
		try {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlDefinition.getBytes());
			final Document current = this.getDocumentModel(byteArrayInputStream);
			final CustomNode rootNode = new CustomNode(current.getDocumentElement());
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				this.processChildren(rootNode.child(i), toolbar);
			}
		} catch (final Exception ex) {
			XMLButtonBarBuilder.logger.error("Append button", ex);
		}
	}

	/**
	 * Class used to create the application toolbar.
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 * @param guiClassesPackage Package where the gui classes are. This class uses reflection to create
	 *        all objects (Class.forName()).
	 * @throws Exception
	 */
	public XMLButtonBarBuilder(final String uriLabelsFile, final String guiClassesPackage) throws Exception {
		this(uriLabelsFile);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	public XMLButtonBarBuilder(final Map equivalenceLabels) throws Exception {
		this.equivalenceLabelList = new HashMap(equivalenceLabels);
	}

	public XMLButtonBarBuilder(final Map equivalenceLabels, final String guiClassesPackage) throws Exception {
		this(equivalenceLabels);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	protected Document performExtendedToolbar(Document doc, final String fileURI) {
		final Enumeration<URL> input = ExtendedXmlParser.getExtendedFile(fileURI, this.getBaseClasspath());
		final Map extendsForm = new HashMap();
		if (input == null) {
			return doc;
		}

		Document extendedDocument = null;

		while (input.hasMoreElements()) {
			try {
				extendedDocument = this.getExtendedDocument(input.nextElement().openStream());
				final Node s = extendedDocument.getChildNodes().item(0).getAttributes().getNamedItem("order");
				final int index = s != null ? Integer.parseInt(s.getNodeValue()) : -1;
				extendsForm.put(extendedDocument, index);
			} catch (final IOException e) {
				XMLButtonBarBuilder.logger.error(null, e);
			}
		}

		final Set<Entry<Object, Integer>> set = extendsForm.entrySet();
		final List<Entry<Object, Integer>> list = new ArrayList<Entry<Object, Integer>>(set);
		Collections.sort(list, new Comparator<Map.Entry<Object, Integer>>() {

			@Override
			public int compare(final Entry<Object, Integer> o1, final Entry<Object, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		if (extendedDocument == null) {
			return doc;
		}

		try {

			for (final Map.Entry<Object, Integer> entry : list) {
				extendedDocument = (Document) entry.getKey();
				doc = XMLButtonBarBuilder.toolbarParser.parseExtendedXml(doc, extendedDocument);
				XMLButtonBarBuilder.logger.debug("{} ButtonBar extend, Load order -> {}", entry.getKey(),
						entry.getValue());
			}
		} catch (final Exception ex) {
			XMLButtonBarBuilder.logger.error("Extending Toolbar", ex);
			// If an error happens executing the parser, original dom is reload
			return this.getDocumentModel(fileURI);
		}
		return doc;
	}

	@Override
	public JToolBar buildButtonBar(final String uriFile) {
		// Creates the menu specified in the XML file.
		// Moves throw the tree and creates the IU
		// from the root node, and while children number is greater than zero
		final long initTime = System.currentTimeMillis();
		Document current = this.getDocumentModel(uriFile);
		current = this.performExtendedToolbar(current, uriFile);
		final CustomNode rootNode = new CustomNode(current.getDocumentElement());
		JToolBar jToolbar = null;
		try {
			if (rootNode.isTag()) {
				final String tag = rootNode.getNodeInfo();
				String className = (String) this.equivalenceLabelList.get(tag);
				if (className == null) {
					XMLButtonBarBuilder.logger.debug("Label not found in equivalence list: {}", tag);
					// Try with tag
					className = tag;
				}
				// Converts tag to the correct form (defaultPackage + class
				// name)
				className = this.defaultPackage + className;
				// Gets attribute list
				final NamedNodeMap attributeList = rootNode.attributeList();
				final Map attributeTable = DefaultXMLParametersManager.getParameters(className);
				for (int i = 0; i < attributeList.getLength(); i++) {
					final Node nNode = attributeList.item(i);
					attributeTable.put(nNode.getNodeName(), nNode.getNodeValue());
				}
				try {
					final Class classObject = Class.forName(className);
					try {
						final Constructor[] constructors = classObject.getConstructors();
						final Object[] parameters = { attributeTable };
						jToolbar = (JToolBar) constructors[0].newInstance(parameters);
					} catch (final Exception e2) {
						XMLButtonBarBuilder.logger.error("Error creating object", e2);
					}
				} catch (final Exception e) {
					XMLButtonBarBuilder.logger.error("Error loading class", e);
				}
			}
			this.toolbar = jToolbar;

			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				final CustomNode childNode = rootNode.child(i);
				this.processChildren(childNode, jToolbar);
			}
			final long endTime = System.currentTimeMillis();
			final double totalTime = (endTime - initTime) / 1000.0;
			XMLButtonBarBuilder.logger.trace("Total building time Buttons Bar: {} seconds.",
					new Double(totalTime).toString());
		} catch (final Exception e) {
			XMLButtonBarBuilder.logger.error("Error building Buttons Bar", e);
		}
		return jToolbar;
	}

	protected void processChildren(final CustomNode node, final Container parent) {
		this.processChildren(node, parent, null);
	}

	protected void processChildren(final CustomNode node, final Container parent, final String moduleName) {
		final Container container = this.interpreterTag(node, parent, this.defaultPackage, this.equivalenceLabelList);
		if ((moduleName != null) && (parent instanceof JToolBar)) {
			// This is a module.Find AppToolBarModule
			final Component[] comps = parent.getComponents();
			Component currentPlace = null;
			for (final Component current : comps) {
				if (current instanceof ApToolBarModule) {
					final String id = current.getName();
					if (moduleName.equalsIgnoreCase(id)) {
						currentPlace = current;
						break;
					} else if (currentPlace == null) {
						currentPlace = current;
					}
				}
			}

			if (currentPlace != null) {

			}
		}
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			final CustomNode auxNode = node.child(i);
			this.processChildren(auxNode, container, moduleName);
		}
	}

	public void processModules(final String moduleId, final CustomNode node, final Container jToolbar) {
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			final CustomNode childNode = node.child(i);
			this.processChildren(childNode, jToolbar, moduleId);
		}
	}

	public String getBaseClasspath() {
		return this.baseClasspath;
	}

	public void setBaseClasspath(final String baseClasspath) {
		this.baseClasspath = baseClasspath;
	}

}
