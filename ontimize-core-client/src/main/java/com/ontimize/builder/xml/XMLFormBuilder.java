package com.ontimize.builder.xml;

import java.awt.Container;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.ontimize.builder.FormBuilder;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.util.extend.ExtendedFormXmlParser;
import com.ontimize.module.ModuleManager;
import com.ontimize.util.ParseUtils;
import com.ontimize.xml.XMLInterpreter;

/**
 * Implementation of {@link FormBuilder} from a XML File
 */
public class XMLFormBuilder extends XMLInterpreter implements FormBuilder {

	static final Logger logger = LoggerFactory.getLogger(XMLFormBuilder.class);

	public static boolean INCLUDE_DEFAULT_LABELS = true;

	protected String baseClasspath = null;

	protected String defaultPackage = "com.ontimize.gui.";

	protected Map equivalenceLabelList = new Hashtable();

	protected String labelFileURI = null;

	protected ModuleManager moduleManager;

	protected static ExtendedFormXmlParser formParser = new ExtendedFormXmlParser();

	/**
	 * @param labelFileURI URI to the labels file. Example 'http://.../xml/labels.xml'<br>
	 *        This class uses reflection to load the objects (Class.forName())
	 * @throws Exception
	 */
	public XMLFormBuilder(final String labelFileURI) throws Exception {
		if (XMLFormBuilder.INCLUDE_DEFAULT_LABELS) {
			this.equivalenceLabelList = this.getDefaultLabelList();
		}
		// labelFileURI is the labels equivalence file
		this.labelFileURI = labelFileURI;
		this.processLabelFile(this.labelFileURI, this.equivalenceLabelList, new ArrayList());
	}

	/**
	 * @param labelFileURI
	 * @param guiClassesPackage
	 * @throws Exception
	 */
	public XMLFormBuilder(final String labelFileURI, final String guiClassesPackage) throws Exception {
		this(labelFileURI);
		if (this.defaultPackage != null) {
			this.defaultPackage = guiClassesPackage;
		}
	}

	public XMLFormBuilder(final Map equivalenceLabelList) throws Exception {
		this.equivalenceLabelList = new HashMap(equivalenceLabelList);
	}

	public XMLFormBuilder(final Map equivalenceLabelList, final String gUIClassPackage) throws Exception {
		this(equivalenceLabelList);
		if (this.defaultPackage != null) {
			this.defaultPackage = gUIClassPackage;
		}
	}

	protected Document performExtendedForm(Document doc, final String fileURI) {
		try {

			doc = XMLFormBuilder.formParser.getExtendedDocumentForm(doc, fileURI, this.getBaseClasspath());

		} catch (final Exception ex) {
			XMLFormBuilder.logger.error("Extending form", ex);
			// If an error happens executing the parser, original dom is reload.
			return this.getDocumentModel(fileURI);
		}
		return doc;
	}

	@Override
	public Form buildForm(final Container parentContainer, final String fileURI) {
		// Move throw the tree and crate the UI
		// From root node and while children number is greater than zero
		XMLFormBuilder.logger.debug("Creating form: {}", fileURI);
		try {
			final long initTime = System.currentTimeMillis();
			parentContainer.setVisible(false);
			Document current = this.getDocumentModel(fileURI);
			current = this.performExtendedForm(current, fileURI);
			final CustomNode auxiliar = new CustomNode(current.getDocumentElement());
			this.processChildren(auxiliar, parentContainer);
			parentContainer.setVisible(true);
			parentContainer.setSize(parentContainer.getSize());
			parentContainer.repaint();
			parentContainer.validate();
			final long endTime = System.currentTimeMillis();
			final double tiempoTranscurrido = (endTime - initTime) / 1000.0;
			XMLFormBuilder.logger.trace("Total time building UI: {}  seconds.",
					new Double(tiempoTranscurrido).toString());
			return (Form) parentContainer.getComponent(0);
		} catch (final Exception e) {
			XMLFormBuilder.logger.error("Building form: {}", fileURI, e);
			return null;
		}
	}

	@Override
	public Form buildForm(final Container parentContainer, final InputStream input) {
		try {
			final long initTime = System.currentTimeMillis();
			parentContainer.setVisible(false);
			final CustomNode auxiliar = new CustomNode(this.getDocumentModel(input).getDocumentElement());
			this.processChildren(auxiliar, parentContainer);
			parentContainer.setVisible(true);
			parentContainer.setSize(parentContainer.getSize());
			parentContainer.repaint();
			parentContainer.validate();
			final long endTime = System.currentTimeMillis();
			final double totalTime = (endTime - initTime) / 1000.0;
			XMLFormBuilder.logger.trace("Total time building UI from InputStream: {} seconds.",
					new Double(totalTime).toString());
			return (Form) parentContainer.getComponent(0);
		} catch (final Exception e) {
			XMLFormBuilder.logger.error("Building form from InputStream ", e);
			return null;
		}
	}

	public Form buildForm(final Container parentContainer, final StringBuffer fileContent) throws Exception {
		try {
			final long initTime = System.currentTimeMillis();
			parentContainer.setVisible(false);
			final CustomNode auxiliar = new CustomNode(this.getDocumentModel(fileContent).getDocumentElement());
			this.processChildren(auxiliar, parentContainer);
			parentContainer.setVisible(true);
			parentContainer.setSize(parentContainer.getSize());
			parentContainer.repaint();
			parentContainer.validate();
			final long endTime = System.currentTimeMillis();
			final double totalTime = (endTime - initTime) / 1000.0;
			XMLFormBuilder.logger.trace("Total time building UI from StringBuilder: {} seconds.",
					new Double(totalTime).toString());
			return (Form) parentContainer.getComponent(0);
		} catch (final Exception e) {
			XMLFormBuilder.logger.error("Building form from StringBuilder ", e);
			throw e;
		}
	}

	protected void processChildren(final CustomNode node, final Container parentContainer) {
		final Container container = this.interpreterTag(node, parentContainer, this.defaultPackage,
				this.equivalenceLabelList);
		for (int i = 0; i < node.getChildrenNumber(); i++) {
			final CustomNode cnAuxNode = node.child(i);
			if (cnAuxNode.isTag()) {
				if (this.moduleManager != null) {
					final Map<String, String> attributes = cnAuxNode.hashtableAttribute();
					if (attributes.containsKey(ModuleManager.MODULE_ATTR)) {
						final String modules = ParseUtils.getString(attributes.get(ModuleManager.MODULE_ATTR), "");
						final String attr = ParseUtils.getString(attributes.get("attr"), cnAuxNode.getNodeInfo());
						final StringTokenizer tokens = new StringTokenizer(modules, ";");
						boolean hasModules = true;
						while (tokens.hasMoreTokens()) {
							final String moduleName = tokens.nextToken();
							if (!this.moduleManager.hasModule(moduleName)) {
								hasModules = false;
								XMLFormBuilder.logger.info("{} attr has not been created by {} module dependencies",
										attr, moduleName);
								break;
							}
						}

						if (!hasModules) {
							continue;
						}
					}
				}
				this.processChildren(cnAuxNode, container);
			}
		}
	}

	public String getLabelFileURI() {
		return this.labelFileURI;
	}

	public String getBaseClasspath() {
		return this.baseClasspath;
	}

	public void setBaseClasspath(final String baseClasspath) {
		this.baseClasspath = baseClasspath;
	}

	public Map getCurrentLabelList() {
		return this.equivalenceLabelList;
	}

	public ModuleManager getModuleManager() {
		return this.moduleManager;
	}

	public void setModuleManager(final ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}

}
