package com.ontimize.builder.xml;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ontimize.builder.TreeBuilder;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.xml.XMLInterpreter;

/**
 * Implementation of a {@link TreeBuilder} from a XML file
 */
public class XMLTreeBuilder extends XMLInterpreter implements com.ontimize.builder.TreeBuilder {

	static final Logger logger = LoggerFactory.getLogger(XMLTreeBuilder.class);

	public static boolean INCLUDE_DEFAULT_LABELS = true;

	protected String defaultPackage = "com.ontimize.gui.";

	protected Map equivalenceLabelList = new Hashtable();

	/**
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 * @throws Exception
	 */
	public XMLTreeBuilder(final String uriLabelsFile) throws Exception {
		if (XMLTreeBuilder.INCLUDE_DEFAULT_LABELS) {
			this.equivalenceLabelList = this.getDefaultLabelList();
		}

		try {
			this.processLabelFile(uriLabelsFile, this.equivalenceLabelList, new ArrayList());
		} catch (final Exception e) {
			XMLTreeBuilder.logger.error("Processing label file", e);
		}
	}

	/**
	 * @param uriLabelsFile URI to the labels file. Example 'http://.../xml/labels.xml'.<br>
	 * @param guiClassesPackage Default package where the gui classes are stored
	 * @throws Exception
	 */
	public XMLTreeBuilder(final String uriLabelsFile, final String guiClassesPackage) throws Exception {
		this(uriLabelsFile);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	public XMLTreeBuilder(final Map labelEquivalences) throws Exception {
		this.equivalenceLabelList = new HashMap(labelEquivalences);
	}

	public XMLTreeBuilder(final Map labelEquivalences, final String guiClassesPackage) throws Exception {
		this(labelEquivalences);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	protected TreeModel buildTree(final CustomNode aux) {
		final String tag = aux.getNodeInfo();
		String className = (String) this.equivalenceLabelList.get(tag);
		if (className == null) {
			XMLTreeBuilder.logger.debug("Label not found in equivalence list: {}", tag);
			// Trying with the tag
			className = tag;
		}
		// Convert the tag to the correct format (package + class name)
		className = this.defaultPackage + className;
		// Get the attribute list
		final NamedNodeMap attributeList = aux.attributeList();
		final Map attributeTable = new Hashtable();
		DefaultMutableTreeNode guiRootNode = null;
		for (int i = 0; i < attributeList.getLength(); i++) {
			final Node auxNode = attributeList.item(i);
			attributeTable.put(auxNode.getNodeName(), auxNode.getNodeValue());
		}
		try {
			final Class classObject = Class.forName(className);
			try {
				final Constructor[] constructors = classObject.getConstructors();
				final Object[] parameters = { attributeTable };
				guiRootNode = (DefaultMutableTreeNode) constructors[0].newInstance(parameters);
				this.processChildren(aux, guiRootNode);
			} catch (final Exception e2) {
				XMLTreeBuilder.logger.error("Error creating object. ", e2);
			}
		} catch (final Exception e) {
			XMLTreeBuilder.logger.error("Error loading class", e);
		}
		return new DefaultTreeModel(guiRootNode);
	}

	@Override
	public TreeModel buildTree(final String fileURI) {
		// This function allows to create a object tree for tree building
		final CustomNode aux = new CustomNode(this.getDocumentModel(fileURI).getDocumentElement());
		return this.buildTree(aux);
	}

	@Override
	public TreeModel buildTree(final StringBuffer content) {
		CustomNode aux = null;
		try {
			aux = new CustomNode(this.getDocumentModel(content).getDocumentElement());
		} catch (final Exception e) {
			XMLTreeBuilder.logger.error(null, e);
		}
		return this.buildTree(aux);
	}

	protected void processChildren(final CustomNode node, final DefaultMutableTreeNode guiNode) {
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			if (node.child(i).isTag()) {
				String className = (String) this.equivalenceLabelList.get(node.child(i).getNodeInfo());

				if (className == null) {
					final String tag = node.child(i).getNodeInfo();
					XMLTreeBuilder.logger.debug("Label not found in equivalence list: {}", tag);
					// Trying with the tag
					className = tag;
				}

				// Convert the tag to the correct format (package + class name)
				className = this.defaultPackage + className;
				// Get the attribute list
				DefaultMutableTreeNode guiChildNode = null;
				final NamedNodeMap attributeList = node.child(i).attributeList();
				final Map attributeTable = new Hashtable();
				for (int j = 0; j < attributeList.getLength(); j++) {
					final Node auxNode = attributeList.item(j);
					attributeTable.put(auxNode.getNodeName(), auxNode.getNodeValue());
				}
				try {
					final Class classObject = Class.forName(className);
					try {
						final Constructor[] constructors = classObject.getConstructors();
						final Object[] parameters = { attributeTable };
						guiChildNode = (DefaultMutableTreeNode) constructors[0].newInstance(parameters);
						guiNode.add(guiChildNode);
					} catch (final Exception e2) {
						XMLTreeBuilder.logger.error("Error creating object", e2);
					}
				} catch (final Exception e) {
					XMLTreeBuilder.logger.error("Error loading class ", e);
				}
				this.processChildren(node.child(i), guiChildNode);
			}
		}
	}

}
