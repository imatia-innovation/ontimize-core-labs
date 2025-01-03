package com.ontimize.xml;

import java.awt.Container;
import java.awt.LayoutManager;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.xml.DocumentTreeModel;

/**
 * Abstract class which method are used by <code>TreeBuilder</code> and <code>FormBuilder</code> to
 * handle the xml descriptions
 */
public abstract class XMLInterpreter {

	private static final Logger logger = LoggerFactory.getLogger(XMLInterpreter.class);

	public static boolean SILENT = false;

	public static final String LABELS_FILE = "com/ontimize/gui/labels.xml";

	protected static final String IMPORT_TAG = "import";

	public Document getDocumentModel(final InputStream input) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// First of all the labels file
		final long tiempoInicial = System.currentTimeMillis();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(input);
			final long finalTime = System.currentTimeMillis();
			final double passTime = (finalTime - tiempoInicial) / 1000.0;
			XMLInterpreter.logger.trace("Time parsing xml {} seconds.", new Double(passTime).toString());
			return document;
		} catch (final Exception e) {
			XMLInterpreter.logger.error("{}", e.getMessage(), e);
			return null;
		}
	}

	public Document getDocumentModel(final String fileURI) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// First of all the labels file
		final long initialTime = System.currentTimeMillis();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(fileURI);
			final long finalTime = System.currentTimeMillis();
			final double passTime = (finalTime - initialTime) / 1000.0;
			XMLInterpreter.logger.trace("Time parsing xml {} seconds.", new Double(passTime).toString());
			return document;
		} catch (final Exception e) {
			XMLInterpreter.logger.error("{}", e.getMessage(), e);
			return null;
		}
	}

	public Document getExtendedDocument(final String fileURI) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(fileURI);
			return document;
		} catch (final Exception e) {
			XMLInterpreter.logger.error("{}", e.getMessage(), e);
			return null;
		}
	}

	public Document getExtendedDocument(final InputStream input) {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(input);
			return document;
		} catch (final Exception e) {
			XMLInterpreter.logger.error("{}", e.getMessage(), e);
			return null;
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (final IOException e) {
					XMLInterpreter.logger.error(null, e);
				}
			}
		}
	}

	public Document getDocumentModel(final StringBuffer fileContent) throws Exception {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// First of all the labels file
		final long initialTime = System.currentTimeMillis();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(new StringBufferInputStream(fileContent.toString()));
			final long finalTime = System.currentTimeMillis();
			final double passTime = (finalTime - initialTime) / 1000.0;
			XMLInterpreter.logger.trace("Time parsing xml {} seconds.", new Double(passTime).toString());
			return document;
		} catch (final Exception e) {
			XMLInterpreter.logger.error("{}", e.getMessage(), e);
			throw e;
		}
	}

	public DocumentTreeModel getDocumentTreeModel(final Document document) {
		return new DocumentTreeModel(document.getDocumentElement());
	}

	public Node getRoot(final Document document) {
		return document.getDocumentElement();
	}

	protected void processLabelFile(final String fileURI, final Map equivalences, final List processedFiled) throws Exception {
		if (processedFiled.contains(fileURI)) {
			return;
		}

		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(fileURI);
			final NodeList importList = document.getElementsByTagName(XMLInterpreter.IMPORT_TAG);
			for (int i = 0; i < importList.getLength(); i++) {
				final Node currentImport = importList.item(i);
				if (currentImport.hasAttributes()) {
					final NamedNodeMap map = currentImport.getAttributes();
					final Node srcNode = map.getNamedItem("src");
					if (srcNode != null) {
						final String filePath = srcNode.getNodeValue().trim();
						final URL url = this.getClass().getClassLoader().getResource(filePath);
						if (url != null) {
							this.processLabelFile(url.toString(), equivalences, processedFiled);
						}
					}
				}
			}

			final DocumentTreeModel model = this.getDocumentTreeModel(document);
			final CustomNode rootNode = (CustomNode) model.getRoot();
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				if (rootNode.child(i).isTag()) {
					final String sLabel = rootNode.child(i).getNodeInfo();
					if (XMLInterpreter.IMPORT_TAG.equals(sLabel)) {
						continue;
					}
					for (int j = 0; j < rootNode.child(i).getChildrenNumber(); j++) {
						if (rootNode.child(i).child(j).isTag()) {
							final String equivalence = rootNode.child(i).child(j).getNodeInfo();
							equivalences.put(sLabel, equivalence);
							break;
						}
					}
				}
			}
			processedFiled.add(fileURI);
		} catch (final Exception e) {
			throw e;
		}

	}

	public Map getDefaultLabelList() throws Exception {
		final Map equivalenceList = new Hashtable();
		final URL labelsURL = this.getClass().getClassLoader().getResource(XMLInterpreter.LABELS_FILE);
		if (labelsURL == null) {
			return equivalenceList;
		}
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			final DocumentBuilder builder = factory.newDocumentBuilder();
			final Document document = builder.parse(labelsURL.openStream());
			final DocumentTreeModel model = this.getDocumentTreeModel(document);
			final CustomNode rootNode = (CustomNode) model.getRoot();
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				if (rootNode.child(i).isTag()) {
					final String sLabel = rootNode.child(i).getNodeInfo();
					for (int j = 0; j < rootNode.child(i).getChildrenNumber(); j++) {
						if (rootNode.child(i).child(j).isTag()) {
							final String equivalence = rootNode.child(i).child(j).getNodeInfo();
							equivalenceList.put(sLabel, equivalence);
							break;
						}
					}
				}
			}
			return equivalenceList;
		} catch (final Exception e) {
			throw e;
		}
	}

	protected Container interpreterTag(final CustomNode childNode, final Container containerParent, final String packageName,
			final Map labelsEquivalenceList) {
		Container childContainer = null;
		if (childNode.isTag()) {
			final long t = System.currentTimeMillis();
			final String tag = childNode.getNodeInfo();
			final LayoutManager parentLayout = containerParent.getLayout();
			String className = (String) labelsEquivalenceList.get(tag);
			if (className == null) {
				XMLInterpreter.logger.debug("Tag not found: {}", tag);
				// Try with the tag
				className = tag;
			}
			// Converts the tag to the appropriate format using the package name
			className = packageName + className;
			// Gets the attribute list
			final NamedNodeMap attributeList = childNode.attributeList();
			final Map attributeTable = new Hashtable();

			try {
				// Default parameters
				final DefaultXMLParametersManager.ParameterValue[] params = DefaultXMLParametersManager
						.getStartsWith(className);
				if (params != null) {
					for (int i = 0; i < params.length; i++) {
						attributeTable.put(params[i].getParameter(), params[i].getValue());
					}
				}
				for (int i = 0; i < attributeList.getLength(); i++) {
					final Node node = attributeList.item(i);
					attributeTable.put(node.getNodeName(), node.getNodeValue());
				}
				final Class classObject = Class.forName(className);
				try {
					childContainer = (Container) this.createComponent(tag, classObject, attributeTable);
					childContainer.setVisible(true);
					containerParent.add(childContainer, ((FormComponent) childContainer).getConstraints(parentLayout));
					XMLInterpreter.logger.trace("Time to create the object {} : {}", className,
							System.currentTimeMillis() - t);
				} catch (final Exception e2) {
					XMLInterpreter.logger.error("Error creating object {}", className, e2);
				}
			} catch (final Exception e) {
				XMLInterpreter.logger.error("Error loading class: {}", className, e);
				childContainer = null;
			}
		}
		return childContainer;
	}

	protected Object createComponent(final String tagName, final Class componentClass, final Map attributes) throws Exception {
		final Object[] parameters = { attributes };
		final Class[] p = { Map.class };
		final Constructor constructorHash = componentClass.getConstructor(p);
		return constructorHash.newInstance(parameters);
	}

}
