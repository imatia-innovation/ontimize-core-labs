package com.ontimize.builder.xml;

import java.awt.Component;
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

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.MenuElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ontimize.builder.MenuBuilder;
import com.ontimize.gui.ApplicationMenuBar;
import com.ontimize.gui.CheckMenuItem;
import com.ontimize.gui.RadioMenuItem;
import com.ontimize.gui.container.RadioItemGroup;
import com.ontimize.gui.field.IdentifiedElement;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.util.extend.ExtendedMenuXmlParser;
import com.ontimize.jee.common.util.extend.ExtendedXmlParser;
import com.ontimize.xml.XMLInterpreter;

public class XMLMenuBuilder extends XMLInterpreter implements MenuBuilder {

	static final Logger logger = LoggerFactory.getLogger(XMLMenuBuilder.class);

	public static boolean INCLUDE_DEFAULT_LABELS = true;

	protected String defaultPackage = "com.ontimize.gui.";

	protected Map equivalenceLabelList = new Hashtable();

	protected JMenuBar menuBar = null;

	protected static ExtendedMenuXmlParser menuParser = new ExtendedMenuXmlParser();

	protected String baseClasspath = null;

	/**
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 * @throws Exception
	 */
	public XMLMenuBuilder(final String uriLabelsFile) throws Exception {
		if (XMLMenuBuilder.INCLUDE_DEFAULT_LABELS) {
			this.equivalenceLabelList = this.getDefaultLabelList();
		}

		try {
			this.processLabelFile(uriLabelsFile, this.equivalenceLabelList, new ArrayList());
		} catch (final Exception e) {
			XMLMenuBuilder.logger.error("Process Label file", e);
		}
	}

	/**
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 * @param guiClassesPackage Package where all classes specified in the xml file are stored
	 * @throws Exception
	 */
	public XMLMenuBuilder(final String uriLabelsFile, final String guiClassesPackage) throws Exception {
		this(uriLabelsFile);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	public XMLMenuBuilder(final Map labelEquivalences) throws Exception {
		this.equivalenceLabelList = new HashMap(labelEquivalences);
	}

	public XMLMenuBuilder(final Map labelEquivalences, final String guiClassesPackage) throws Exception {
		this(labelEquivalences);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	protected Document performExtendedMenu(Document doc, final String fileURI) {
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
				// TODO Auto-generated catch block
				XMLMenuBuilder.logger.error(null, e);
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
				doc = XMLMenuBuilder.menuParser.parseExtendedXml(doc, extendedDocument);
				XMLMenuBuilder.logger.debug("{} Menu extend, Load order -> {}", entry.getKey(), entry.getValue());
			}
		} catch (final Exception ex) {
			XMLMenuBuilder.logger.error("Extending menu", ex);
			// If an error happens executing the parser, the original dom is
			// reloaded
			return this.getDocumentModel(fileURI);
		}
		return doc;
	}

	@Override
	public void appendMenu(final JMenuBar menuBar, final String xmlDefinition) {
		try {
			final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xmlDefinition.getBytes());
			final Document current = this.getDocumentModel(byteArrayInputStream);
			final CustomNode rootNode = new CustomNode(current.getDocumentElement());
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				this.processChildren(rootNode.child(i), menuBar, true, false);
			}
		} catch (final Exception ex) {
			XMLMenuBuilder.logger.error("Append menu", ex);
		}
	}

	@Override
	public JMenuBar buildMenu(final String fileURI) {
		// Creates a menu. This menu is specified in the xml file
		final long initTime = System.currentTimeMillis();
		Document current = this.getDocumentModel(fileURI);
		current = this.performExtendedMenu(current, fileURI);
		final CustomNode rootNode = new CustomNode(current.getDocumentElement());
		MenuElement menu = null;
		try {
			if (rootNode.isTag()) {
				final String tag = rootNode.getNodeInfo();
				String className = (String) this.equivalenceLabelList.get(tag);
				if (className == null) {
					XMLMenuBuilder.logger.debug("Label not found in equivalence list: {}", tag);
					// Trying with the tag
					className = tag;
				}
				// Convert the tag to the correct format (package + class name)
				className = this.defaultPackage + className;
				// Get the attribute list
				final NamedNodeMap attributeList = rootNode.attributeList();
				final Map attributeTable = new Hashtable();
				for (int i = 0; i < attributeList.getLength(); i++) {
					final Node node = attributeList.item(i);
					attributeTable.put(node.getNodeName(), node.getNodeValue());
				}
				try {
					final Class classObject = Class.forName(className);
					try {
						final Constructor[] constructors = classObject.getConstructors();
						final Object[] parameters = { attributeTable };
						menu = (MenuElement) constructors[0].newInstance(parameters);
					} catch (final Exception e2) {
						XMLMenuBuilder.logger.error("Error creatin object.", e2);
					}
				} catch (final Exception e) {
					XMLMenuBuilder.logger.error("Error loading class {}", className, e);
				}
			}
			this.menuBar = (JMenuBar) menu;
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				this.processChildren(rootNode.child(i), menu);
			}
			final long endTime = System.currentTimeMillis();
			final double totalTime = (endTime - initTime) / 1000.0;
			XMLMenuBuilder.logger.trace("Total time building Menu: {} seconds", new Double(totalTime).toString());
		} catch (final Exception e) {
			XMLMenuBuilder.logger.error("Error building Menu", e);
		}
		return (JMenuBar) menu;
	}

	public JMenuBar buildMenu(final StringBuffer fileContent) throws Exception {
		// fileContent must be a xml file
		MenuElement menu = null;
		try {
			final long initTime = System.currentTimeMillis();

			final CustomNode rootNode = new CustomNode(this.getDocumentModel(fileContent).getDocumentElement());

			if (rootNode.isTag()) {
				final String tag = rootNode.getNodeInfo();
				String className = (String) this.equivalenceLabelList.get(tag);
				if (className == null) {
					XMLMenuBuilder.logger.debug("Label not found in equivalence list: {}" + tag);
					// Trying with the tag
					className = tag;
				}
				// Convert the tag to the correct format (package + class name)
				className = this.defaultPackage + className;
				// Get the attribute list
				final NamedNodeMap attributeList = rootNode.attributeList();
				final Map attributeTable = new Hashtable();
				for (int i = 0; i < attributeList.getLength(); i++) {
					final Node node = attributeList.item(i);
					attributeTable.put(node.getNodeName(), node.getNodeValue());
				}
				try {
					final Class classObject = Class.forName(className);
					try {
						final Constructor[] constructors = classObject.getConstructors();
						final Object[] parameters = { attributeTable };
						menu = (MenuElement) constructors[0].newInstance(parameters);
					} catch (final Exception e2) {
						XMLMenuBuilder.logger.error("Error creating object.", e2);
					}
				} catch (final Exception e) {
					XMLMenuBuilder.logger.error("Error loading class: {}", className, e);
				}
			}
			this.menuBar = (JMenuBar) menu;
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				this.processChildren(rootNode.child(i), menu);
			}
			final long endTime = System.currentTimeMillis();
			final double totalTime = (endTime - initTime) / 1000.0;
			XMLMenuBuilder.logger.trace("Total time building Menu: {} seconds.", new Double(totalTime).toString());
		} catch (final Exception e) {
			XMLMenuBuilder.logger.error("Error building Menu {}", e);
			throw e;
		}
		return (JMenuBar) menu;
	}

	protected void processChildren(final CustomNode node, final Object parent) {
		this.processChildren(node, parent, false, false);
	}

	protected void processChildren(final CustomNode node, final Object parent, final boolean dynamic, final boolean module) {
		final Object menuElement = this.translateTag(node, parent, this.defaultPackage, this.equivalenceLabelList, dynamic,
				module);
		if (menuElement instanceof RadioItemGroup) {
			if (this.menuBar instanceof ApplicationMenuBar) {
				((ApplicationMenuBar) this.menuBar).add((RadioItemGroup) menuElement);
			}
			for (int i = 0; i < node.getChildrenNumber(); i++) {
				final Object childElement = this.translateTag(node.child(i), parent, this.defaultPackage,
						this.equivalenceLabelList, dynamic, module);
				// If the element is a RadioButton element it is added
				if (childElement != null) {
					if (childElement instanceof RadioMenuItem) {
						((RadioItemGroup) menuElement).add((AbstractButton) childElement);
					} else if (childElement instanceof CheckMenuItem) {
						((RadioItemGroup) menuElement).add((AbstractButton) childElement);
					}
				}
			}
		} else {
			for (int i = 0; i < node.getChildrenNumber(); i++) {
				final CustomNode auxNode = node.child(i);
				this.processChildren(auxNode, menuElement, dynamic, module);
			}
		}
	}

	public void processModules(final CustomNode node, final Object parent) {
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			final CustomNode childNode = node.child(i);
			this.processChildren(childNode, parent, false, true);
		}
	}

	protected Object translateTag(final CustomNode childNode, final Object parent, final String packageName, final Map equivalenceList) {
		return this.translateTag(childNode, parent, packageName, equivalenceList, false, false);
	}

	protected Object findAttr(final Object parent, final String attr) {
		if (attr == null) {
			return null;
		}

		if (parent instanceof JComponent) {
			final JComponent cParent = (JComponent) parent;
			final Component[] components = cParent.getComponents();
			for (final Component current : components) {
				if (current instanceof IdentifiedElement) {
					final IdentifiedElement identify = (IdentifiedElement) current;
					if (attr.equals(identify.getAttribute())) {
						return current;
					}
				}
			}
		}
		return null;
	}

	protected Object translateTag(final CustomNode childNode, final Object parent, final String packageName, final Map equivalenceList,
			final boolean dynamic, final boolean module) {
		Object childMenuElement = null;
		if (childNode.isTag()) {
			final String tag = childNode.getNodeInfo();
			if (module) {
				final Map<String, String> params = childNode.hashtableAttribute();
				final String attr = params.get("attr");
				final Object current = this.findAttr(parent, attr);
				if (current != null) {
					return current;
				}
			}

			String className = (String) this.equivalenceLabelList.get(tag);
			if (className == null) {
				XMLMenuBuilder.logger.debug("Label not found in equivalence list:{}", tag);
				// Trying with the tag
				className = tag;
			}
			// Convert the tag to the correct format (package + class name)
			className = packageName + className;
			// Get the attribute list
			final NamedNodeMap attributeList = childNode.attributeList();
			final Map attributeTable = new Hashtable();
			for (int i = 0; i < attributeList.getLength(); i++) {
				final Node node = attributeList.item(i);
				attributeTable.put(node.getNodeName(), node.getNodeValue());
			}
			attributeTable.put("dynamic", Boolean.toString(dynamic));

			try {
				final Class classObject = Class.forName(className);
				try {
					final Constructor[] constructors = classObject.getConstructors();
					final Object[] parameters = { attributeTable };
					final Object element = constructors[0].newInstance(parameters);
					if (element instanceof MenuElement) {
						childMenuElement = element;
					} else {
						if (element instanceof RadioItemGroup) {
							childMenuElement = element;
						} else {
							childMenuElement = element;
						}
					}
					if (parent instanceof JMenuBar) {
						final JMenuBar pa = (JMenuBar) parent;
						if (childMenuElement instanceof JMenuItem) {
							pa.add((JMenuItem) childMenuElement);
						} else {
							// If element is a ButtonGroup, it must not be
							// added.
							// The children of RadioItemGroup must be added to
							// the
							// ButtonGroup parent
							if (childMenuElement instanceof RadioItemGroup) {
							} else {
								pa.add((Component) childMenuElement);
							}
						}
					} else {
						if (parent instanceof JMenu) {
							final JMenu pa = (JMenu) parent;
							if (childMenuElement instanceof JMenuItem) {
								pa.add((JMenuItem) childMenuElement);
							} else {
								if (childMenuElement instanceof RadioItemGroup) {

								} else {
									pa.add((Component) childMenuElement);
								}
							}
						}
					}
				} catch (final Exception e2) {
					XMLMenuBuilder.logger.error("Error creating object", e2);
				}
			} catch (final Exception e) {
				XMLMenuBuilder.logger.error("Error loading class {}", className, e);
			}
		}
		return childMenuElement;
	}

	public String getBaseClasspath() {
		return this.baseClasspath;
	}

	public void setBaseClasspath(final String baseClasspath) {
		this.baseClasspath = baseClasspath;
	}

}
