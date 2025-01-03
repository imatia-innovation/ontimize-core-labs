package com.ontimize.jee.desktopclient.builder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.builder.xml.XMLButtonBarBuilder;
import com.ontimize.builder.xml.XMLFormBuilder;
import com.ontimize.builder.xml.XMLMenuBuilder;
import com.ontimize.builder.xml.XMLTreeBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MenuListener;
import com.ontimize.gui.ToolBarListener;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.manager.ITreeFormManager;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.tools.Chronometer;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.util.extend.ExtendedXmlParser;
import com.ontimize.jee.desktopclient.components.treetabbedformmanager.ITreeTabbedFormManager;
import com.ontimize.jee.desktopclient.components.treetabbedformmanager.levelmanager.builder.xml.XMLLevelManagerBuilder;

/**
 * The Class XMLMultiModuleApplicationBuilder. Unifica todos los "clientapplication.xml" en uno
 * sólo y lo analiza
 */
public class XMLMultiModuleApplicationBuilder extends XMLApplicationBuilder {

	// TODO cambiar a un parser SAX

	private static final String ONTIMIZE_MODULE = "OntimizeModule";

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(XMLMultiModuleApplicationBuilder.class);

	private static final String MODULE = "Module";

	/**
	 * Instantiates a new xML multi module application builder.
	 * @param labelsFileURI the labels file uri
	 * @throws Exception the exception
	 */
	public XMLMultiModuleApplicationBuilder(final String labelsFileURI) throws Exception {
		super(labelsFileURI, "");
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.ontimize.builder.xml.XMLApplicationBuilder#buildApplication(java.lang.String)
	 */
	@Override
	public Application buildApplication(final String fileURI) {
		final Pair<InputStream, List<String>> pair = this.composeClientConfigurationFile(fileURI);
		final Application buildApplication = this.buildApplication(pair.getFirst());
		// TODO ver como aplicar el bundle del resto de modulos cuando flexibilicen la carga en ontimize
		return buildApplication;
	}

	/**
	 * Compose client configuration file. Obtiene todos los ficheros xml con nombre <b>fileURI</b> y los
	 * unifica en uno solo.
	 * @param fileURI the file uri
	 * @return the input stream
	 */
	protected Pair<InputStream, List<String>> composeClientConfigurationFile(final String fileURI) {
		final List<String> bundles = new ArrayList<>();
		try {
			final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			final List<Node> extraChildNodes = new ArrayList<>();
			final Enumeration<URL> resources = this.localizeResources(fileURI, classLoader);

			final List<Document> resourceModulesAsDocs = this.toDocs(resources);
			final Document mainDocument = this.getMainDocAndRemove(resourceModulesAsDocs);
			for (final Document doc : resourceModulesAsDocs) {
				if (XMLMultiModuleApplicationBuilder.ONTIMIZE_MODULE.equals(doc.getDocumentElement().getNodeName())) {
					final String resourceAttribute = doc.getDocumentElement().getAttribute("resources");
					if ((resourceAttribute != null) && !resourceAttribute.isEmpty()) {
						bundles.add(resourceAttribute);
					}
					final NodeList nodes = doc.getDocumentElement().getChildNodes();
					for (int i = 0; i < nodes.getLength(); i++) {
						final Node item = nodes.item(i);
						extraChildNodes.add(item);
					}
				}
			}

			for (final Node node : extraChildNodes) {
				mainDocument.getDocumentElement().appendChild(mainDocument.importNode(node, true));
			}
			bundles.add(0, mainDocument.getDocumentElement().getAttribute("resources"));

			final byte[] res = this.documentToBytes(mainDocument);
			XMLMultiModuleApplicationBuilder.logger.debug(new String(res, Charset.defaultCharset()));

			return new Pair<>(new ByteArrayInputStream(res), bundles);
		} catch (final Exception ex) {
			throw new OntimizeJEERuntimeException("Error joining module descriptions", ex);
		}
	}

	private Enumeration<URL> localizeResources(final String fileURI, final ClassLoader classLoader) throws IOException {
		if (fileURI.contains("*")) {
			// Pattern support
			final HashSet<Resource> res = new HashSet<>();//
			final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
			final Resource[] cpResources = resolver.getResources("classpath*:" + fileURI);
			if ((cpResources == null) || (cpResources.length == 0)) {
				throw new OntimizeJEERuntimeException(fileURI + " not found");
			}
			res.addAll(Arrays.asList(cpResources));
			final Vector<URL> urls = new Vector<>(res.size());
			for (final Resource r : cpResources) {
				urls.add(r.getURL());
			}
			return urls.elements();
			// TODO exists a bug throught JavaWebStart and JNLPClassLoader -> not detect properly the APP jars,
			// only inspect System/JRE jars
		} else if (fileURI.contains(",")) {
			// Multiple files
			final Vector<URL> urls = new Vector<>();
			for (final String singleFileURI : fileURI.split(",")) {
				try {
					final Enumeration<URL> resources2 = classLoader.getResources(singleFileURI.trim());
					if (resources2.hasMoreElements()) {
						urls.addAll(Collections.list(resources2));
					}
				} catch (final Exception ex) {
					XMLMultiModuleApplicationBuilder.logger.warn(null, ex);
				}
			}
			return urls.elements();
		} else {
			return classLoader.getResources(fileURI);
		}
	}

	private List<Document> toDocs(final Enumeration<URL> resources)
			throws ParserConfigurationException, SAXException, IOException {
		final List<Document> docs = new ArrayList<>();
		final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		while (resources.hasMoreElements()) {
			final URL element = resources.nextElement();
			final Document doc = dBuilder.parse(element.openStream());
			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();
			docs.add(doc);
			// and now get inner modules references
			final CustomNode rootNode = new CustomNode(
					this.performExtendedClientApplication(doc, "/", null).getDocumentElement());
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				final CustomNode node = rootNode.child(i);
				// Creates the object
				if (node.isTag()) {
					final String tag = node.getNodeInfo();
					if (tag.equals(XMLMultiModuleApplicationBuilder.MODULE)) {
						final String menuFile = node.hashtableAttribute().get("archive");
						if (menuFile == null) {
							XMLMultiModuleApplicationBuilder.logger
							.warn("'archive' parameter is missing in MODULE node");
						} else {
							// get all resources that matches module name
							final Enumeration<URL> resourcesInner = classLoader.getResources(menuFile);
							final List<Document> resourcesAsDocsInner = this.toDocs(resourcesInner);
							docs.addAll(resourcesAsDocsInner);
						}
					}
				}
			}
		}
		return docs;
	}

	private Document getMainDocAndRemove(final List<Document> resourcesAsDocs) {
		Document mainDoc = null;
		for (final Document doc : resourcesAsDocs) {
			if (!XMLMultiModuleApplicationBuilder.ONTIMIZE_MODULE.equals(doc.getDocumentElement().getNodeName())) {
				if (mainDoc != null) {
					throw new OntimizeJEERuntimeException("There are more than one main application descriptor");
				} else {
					mainDoc = doc;
				}
			}
		}
		if (mainDoc != null) {
			resourcesAsDocs.remove(mainDoc);
		} else {
			throw new OntimizeJEERuntimeException("Main document not found");
		}
		return mainDoc;
	}

	/**
	 * Builds the application.
	 * @param fileURI the file uri
	 * @return the application
	 */
	protected Application buildApplication(final InputStream fileURI) {
		EntityReferenceLocator locator = null;
		MultiModuleMenuListener menuListener = null;
		MultiModuleToolBarListener toolbarListener = null;
		final List<String> toolbarDefinitions = new ArrayList<>();
		final List<String> menubarDefinitions = new ArrayList<>();
		JMenuBar menuBar = null;
		JToolBar toolbar = null;
		// Root node must be an application.
		try {
			final Chronometer chr = new Chronometer();
			chr.start();

			Document documentClientApplication = this.getDocumentModel(fileURI);
			documentClientApplication = this.performExtendedClientApplication(documentClientApplication, "/", null);
			final CustomNode rootNode = new CustomNode(documentClientApplication.getDocumentElement());

			final Object application = this.instance(rootNode);
			if (!(application instanceof Application)) {
				throw new OntimizeJEERuntimeException("Error: Root is not a com.ontimize.gui.Application instance");
			}

			locator = this.buildLocator(rootNode);
			if (locator == null) {
				throw new OntimizeJEERuntimeException("Error: Reference Locator not specified");
			}
			((Application) application).setReferencesLocator(locator);

			// Children of this node must be IFormManager nodes
			for (int i = 0; i < rootNode.getChildrenNumber(); i++) {
				Thread.yield();
				try {
					final CustomNode node = rootNode.child(i);
					// Creates the object
					if (node.isTag()) {
						final String tag = node.getNodeInfo();
						if (tag.equals(XMLApplicationBuilder.MENU)) {
							final String menuFile = node.hashtableAttribute().get("archive");
							if (menuFile == null) {
								XMLMultiModuleApplicationBuilder.logger
								.warn("'archive' parameter is missing in MENU node");
							} else {
								menubarDefinitions.add(menuFile);
							}

						} else if (tag.equals(XMLApplicationBuilder.TOOLBAR)) {
							final String buttonsBarFile = node.hashtableAttribute().get("archive");
							if (buttonsBarFile == null) {
								XMLMultiModuleApplicationBuilder.logger
								.warn("'archive' parameter is missing in TOOLBAR node");
							} else {
								toolbarDefinitions.add(buttonsBarFile);
							}

						} else if (tag.equals(XMLApplicationBuilder.TOOLBARLISTENER)) {
							final ToolBarListener listener = this.buildToolBarListener(node);
							if (toolbarListener == null) {
								toolbarListener = (MultiModuleToolBarListener) listener;
							} else {
								toolbarListener.addListener((IModuleActionToolBarListener) listener);
							}

						} else if (tag.equals(XMLApplicationBuilder.MENULISTENER)) {
							final MenuListener listener = this.buildMenuListener(node);
							if (menuListener == null) {
								menuListener = (MultiModuleMenuListener) listener;
							} else {
								menuListener.addListener((IModuleActionMenuListener) listener);
							}

						} else if (tag.equals(XMLMultiModuleApplicationBuilder.MODULE)) {
							XMLMultiModuleApplicationBuilder.logger
							.trace("skipping Module tag, it was already merged, now it is a meaningless tag");
						} else if (!XMLApplicationBuilder.REFLOCATOR.equals(tag)) {
							this.buildApplicationFormManager(locator, application, node);
						}
					}
				} catch (final Exception e) {
					XMLMultiModuleApplicationBuilder.logger.error(null, e);
				}
			}

			menuBar = this.buildMenuBar(menubarDefinitions);
			((Application) application).setMenu(menuBar);

			toolbar = this.buildToolBar(toolbarDefinitions);
			((Application) application).setToolBar(toolbar);

			if ((menuBar != null) && (menuListener != null)) {
				((Application) application).setMenuListener(menuListener);
				menuListener.addMenuToListenFor(menuBar);
				menuListener.setApplication((Application) application);
				menuListener.setInitialState();
			} else {
				XMLMultiModuleApplicationBuilder.logger.warn("Menu Listener not set. Cause: Menu not specified");
			}
			if ((toolbar != null) && (toolbarListener != null)) {
				((Application) application).setToolBarListener(toolbarListener);
				toolbarListener.addToolBarToListenFor(toolbar);
				toolbarListener.setApplication((Application) application);
				toolbarListener.setInitialState();
			}

			XMLMultiModuleApplicationBuilder.logger.trace("Time elapsed while creating application: {} seconds.",
					chr.stopSeconds());
			return (Application) application;
		} catch (final OntimizeJEERuntimeException ex) {
			throw ex;
		} catch (final Exception e2) {
			throw new OntimizeJEERuntimeException(e2);
		}
	}

	private void buildApplicationFormManager(final EntityReferenceLocator locator, final Object application, final CustomNode node)
			throws Exception {
		final Object ob = this.buildObject(node, locator, application);
		if ((ob != null) && (ob instanceof IFormManager)) {
			final Object id = ((IFormManager) ob).getId();
			if (id == null) {
				XMLMultiModuleApplicationBuilder.logger
				.warn("'id' attribute is missing in FormManager tag " + ((IFormManager) ob).getId());
			} else {
				((Application) application).registerFormManager((String) id, (IFormManager) ob);
			}
		}
	}

	@Override
	protected Document performExtendedClientApplication(final Document doc, final String fileURI, final String baseCP) {
		// FIX ontimize bug
		final Enumeration<URL> input = ExtendedXmlParser.getExtendedFile(fileURI, baseCP);
		if (input == null) {
			return doc;
		}
		// end fix ontimize bug
		return super.performExtendedClientApplication(doc, fileURI, baseCP);
	}

	/**
	 * Builds the object.
	 * @param node the node
	 * @param baseCP the base cp
	 * @param locator the locator
	 * @param application the application
	 * @return the object
	 * @throws Exception the exception
	 */
	private Object buildObject(final CustomNode node, final EntityReferenceLocator locator, final Object application) throws Exception {
		final Map<Object, Object> param = new Hashtable<>(node.hashtableAttribute());
		// Now additional values
		param.put(IFormManager.FORM_BUILDER, new XMLFormBuilder(this.equivalentLabelsList, this.packageA));
		param.put(ITreeFormManager.TREE_BUILDER, new XMLTreeBuilder(this.equivalentLabelsList, this.packageA));
		param.put(ITreeTabbedFormManager.TREE_TABLE_BUILDER,
				new XMLLevelManagerBuilder(this.equivalentLabelsList, this.packageA));
		param.put(IFormManager.FRAME, ((Application) application).getFrame());
		final JPanel panel = new JPanel();
		param.put(IFormManager.CONTAINER, panel);

		// Modification: 27-04-2006. Load from the classpath
		param.put(IFormManager.USE_CLASS_PATH, "yes");

		final Object form = param.get(XMLApplicationBuilder.FORM);
		if (form != null) {
			param.put(XMLApplicationBuilder.FORM, form);
		}

		final Object rules = param.get(XMLApplicationBuilder.RULES);
		if (rules != null) {
			param.put(XMLApplicationBuilder.RULES, rules);
		}

		final Object tree = param.get(XMLApplicationBuilder.TREE);
		if (tree != null) {
			param.put(XMLApplicationBuilder.TREE, tree);
		}

		final Object treeclass = param.get(XMLApplicationBuilder.TREE_CLASS);
		if (treeclass != null) {
			param.put(XMLApplicationBuilder.TREE_CLASS, treeclass);
		}

		param.put(IFormManager.LOCATOR, locator);
		param.put(IFormManager.APPLICATION, application);

		final Map<?, ?> detail = this.analyzeChildren(node);
		param.put(IFormManager.DETAIL, detail);

		// TODO fix in ontimize core
		return this.instance(node, param);
	}

	/**
	 * Builds the menu listener.
	 * @param node the node
	 * @return the menu listener
	 */
	private MenuListener buildMenuListener(final CustomNode node) {
		final Map<?, ?> param = node.hashtableAttribute();
		final Object classMListener = param.get("class");
		MenuListener menuListener = null;
		if (classMListener == null) {
			XMLMultiModuleApplicationBuilder.logger.warn("MenuListener class not specified");
		} else {
			try {
				final Class<?> classM = Thread.currentThread().getContextClassLoader().loadClass(classMListener.toString());
				final Object ml = classM.newInstance();
				if (ml instanceof MenuListener) {
					menuListener = (MenuListener) ml;
				} else {
					XMLMultiModuleApplicationBuilder.logger
					.error("The specified Menu Listener is not an instance of MenuListener in {}", node);

				}
			} catch (final Exception e) {
				XMLMultiModuleApplicationBuilder.logger.error("Error creating Menu Listener " + classMListener, e);
			}
		}
		return menuListener;
	}

	/**
	 * Builds the tool bar listener.
	 * @param node the node
	 * @return the tool bar listener
	 */
	private ToolBarListener buildToolBarListener(final CustomNode node) {
		ToolBarListener toolbarListener = null;
		if (MainApplication.checkApplicationPermission("ToolbarPermission")) {
			final Map<?, ?> param = node.hashtableAttribute();
			final Object classTListener = param.get("class");
			if (classTListener == null) {
				XMLMultiModuleApplicationBuilder.logger.warn("Toolbar Listener not specified");
			} else {
				try {
					final Class<?> classM = Thread.currentThread()
							.getContextClassLoader()
							.loadClass(classTListener.toString());
					final Object ml = classM.newInstance();
					if (ml instanceof ToolBarListener) {
						toolbarListener = (ToolBarListener) ml;
					} else {
						XMLMultiModuleApplicationBuilder.logger
						.error("The specified ToolBar Listener is not an instance of ToolBarListener in {}", node);
					}
				} catch (final Exception e) {
					XMLMultiModuleApplicationBuilder.logger.error("Error creating  ToolBar Listener {}", classTListener,
							e);
				}
			}
		}
		return toolbarListener;
	}

	/**
	 * Builds the tool bar.
	 * @param toolbarDefinitions the toolbar definitions
	 * @param baseCP the base cp
	 * @param uRIBase the u ri base
	 * @return the j tool bar
	 * @throws Exception the exception
	 */
	private JToolBar buildToolBar(final List<String> toolbarDefinitions) throws Exception {
		JToolBar toolbar = null;
		if (MainApplication.checkApplicationPermission("ToolbarPermission")) {
			final StringBuffer sb = this.composeMenuBarOrToolBarFile(toolbarDefinitions);
			this.toolbarBuilder = new XMLButtonBarBuilder(this.equivalentLabelsList, this.packageA);
			this.toolbarBuilder.setBaseClasspath("");
			final File f = File.createTempFile("buttonbar", ".xml");
			f.deleteOnExit();
			try (FileOutputStream fos = new FileOutputStream(f)) {
				fos.write(sb.toString().getBytes());
				toolbar = this.toolbarBuilder.buildButtonBar(f.toURI().toString());
				if (toolbar == null) {
					XMLMultiModuleApplicationBuilder.logger.error("Error creating toolbar");
				}
			}
		}
		return toolbar;
	}

	/**
	 * Builds the menu bar.
	 * @param menubarDefinitions the menubar definitions
	 * @param baseCP the base cp
	 * @return the j menu bar
	 * @throws Exception the exception
	 */
	private JMenuBar buildMenuBar(final List<String> menubarDefinitions) throws Exception {
		JMenuBar menuBar = null;
		final StringBuffer sb = this.composeMenuBarOrToolBarFile(menubarDefinitions);
		this.menuBuilder = new XMLMenuBuilder(this.equivalentLabelsList, this.packageA);
		this.menuBuilder.setBaseClasspath("");
		menuBar = this.menuBuilder.buildMenu(sb);
		if (menuBar == null) {
			XMLMultiModuleApplicationBuilder.logger.error("Error creating menu");
		}
		return menuBar;
	}

	/**
	 * Compose menu bar or tool bar file.
	 * @param definitions the definitions
	 * @return the string buffer
	 */
	private StringBuffer composeMenuBarOrToolBarFile(final List<String> definitions) {
		if (definitions.isEmpty()) {
			return null;
		}
		try {
			final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			// list to save nodes temporarily before ordering them
			final List<IndexableNode> tempNodes = new ArrayList<>();
			final Map<Integer, Integer> idxToPosition = new HashMap<>();
			Document mainDocument = null;
			for (int i = 0; i < definitions.size(); i++) {
				final Document currentDocument = dBuilder
						.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(definitions.get(i)));
				if (mainDocument == null) {
					mainDocument = currentDocument;
				}
				currentDocument.getDocumentElement().normalize();

				final Element documentElement = currentDocument.getDocumentElement();
				if (documentElement != null) {
					final NodeList childNodes = documentElement.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); j++) {
						final Node testNode = childNodes.item(j);
						final Node currentNode = this.findNode(tempNodes, testNode, "attr");
						if (currentNode == null) {
							// get index of this node
							final String idx = this.getAttributeValue(testNode, "index");
							int index = 0;
							if ((idx != null) && (idx.trim().length() != 0)) {
								try {
									index = Integer.parseInt(idx);
								} catch (final NumberFormatException e) {
									index = 0;
									// not a number
								}
							}
							// get last position for same index and increment it
							Integer pos = idxToPosition.get(index);
							if (pos == null) {
								pos = 0;
								idxToPosition.put(index, pos);
							} else {
								pos++;
								idxToPosition.put(index, pos);
							}
							final IndexableNode iNode = new IndexableNode(index, pos, testNode);
							tempNodes.add(iNode);
						} else if (mainDocument != currentDocument) {
							final NodeList childItems = testNode.getChildNodes();
							for (int k = 0; k < childItems.getLength(); k++) {
								currentNode.appendChild(mainDocument.importNode(childItems.item(k), true));
							}
						}
					}
				}
			}

			// borramos los nodos del documento principal
			final Element root = mainDocument.getDocumentElement();
			if (root != null) {
				XMLMultiModuleApplicationBuilder.removeChilds(root);
			}

			// ordenamos todos los nodos por el index y se los anhadimos al doc principal
			if (root != null) {
				Collections.sort(tempNodes);
				for (final IndexableNode iNode : tempNodes) {
					root.appendChild(mainDocument.importNode(iNode.getNode(), true));
				}
			}

			final byte[] res = this.documentToBytes(mainDocument);

			XMLMultiModuleApplicationBuilder.logger.debug(new String(res, Charset.defaultCharset()));

			return new StringBuffer(new String(res, Charset.forName("UTF-8")));
		} catch (final Exception ex) {
			throw new OntimizeJEERuntimeException("Error joining module descriptions", ex);
		}
	}

	public static void removeChilds(final Node node) {
		while (node.hasChildNodes()) {
			node.removeChild(node.getFirstChild());
		}
	}

	/**
	 * Find node.
	 * @param mainNode the main node
	 * @param testNode the test node
	 * @param idTag the id tag
	 * @return the node
	 */
	private Node findNode(final Node mainNode, final Node testNode, final String idTag) {
		final String idTest = this.getAttributeValue(testNode, idTag);
		final String nodeNameTest = testNode.getNodeName();

		final String idMain = this.getAttributeValue(mainNode, idTag);
		final String nodeNameMain = mainNode.getNodeName();

		if ((nodeNameMain != null) && nodeNameMain.equals(nodeNameTest) && (idMain != null) && idMain.equals(idTest)) {
			return mainNode;
		}
		final NodeList childNodes = mainNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			final Node node = this.findNode(childNodes.item(i), testNode, idTag);
			if (node != null) {
				return node;
			}
		}
		return null;
	}

	private Node findNode(final List<IndexableNode> mainNodes, final Node testNode, final String idTag) {
		for (final IndexableNode mainNode : mainNodes) {
			final Node result = this.findNode(mainNode.getNode(), testNode, idTag);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Document to bytes.
	 * @param doc the doc
	 * @return the byte[]
	 * @throws TransformerException the transformer exception
	 */
	private byte[] documentToBytes(final Document doc) throws TransformerException {
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		final DOMSource source = new DOMSource(doc);
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final StreamResult result = new StreamResult(baos);
		transformer.transform(source, result);
		return baos.toByteArray();
	}

	/**
	 * Gets the attribute value.
	 * @param testNode the test node
	 * @param attributeName the attribute name
	 * @return the attribute value
	 */
	private String getAttributeValue(final Node testNode, final String attributeName) {
		final NamedNodeMap attributes = testNode.getAttributes();
		if (attributes != null) {
			final Node namedItem = attributes.getNamedItem(attributeName);
			if (namedItem != null) {
				return namedItem.getNodeValue();
			}
		}
		return null;
	}

	/**
	 * Builds the locator.
	 * @param auxiliar the auxiliar
	 * @return the entity reference locator
	 */
	private EntityReferenceLocator buildLocator(final CustomNode auxiliar) {
		EntityReferenceLocator locator = null;
		// First of all must set the reference locator.
		for (int i = 0; i < auxiliar.getChildrenNumber(); i++) {
			Thread.yield();
			final CustomNode node = auxiliar.child(i);
			// Creates the object
			if (node.isTag()) {
				final String tag = node.getNodeInfo();
				if (tag.equals(XMLApplicationBuilder.REFLOCATOR)) {
					final Map<?, ?> param = node.hashtableAttribute();
					final Object locatorClassName = param.get("class");
					if (locatorClassName == null) {
						XMLMultiModuleApplicationBuilder.logger.error("ReferenceLocator class not specified");
					} else {
						try {
							final Class<?> locatorClass = Thread.currentThread()
									.getContextClassLoader()
									.loadClass(locatorClassName.toString());
							final Class<?>[] p = { Map.class };
							final Constructor<?> constructorHash = locatorClass.getConstructor(p);
							final Object[] params = { param };
							final Object lc = constructorHash.newInstance(params);
							if (lc instanceof EntityReferenceLocator) {
								locator = (EntityReferenceLocator) lc;
							}
							break;
						} catch (final Exception e) {
							XMLMultiModuleApplicationBuilder.logger
							.error("Error creating EntityReferenceLocator " + locatorClassName, e);
						}
					}
				}
			}
		}
		return locator;
	}

	private class IndexableNode implements Comparable<IndexableNode> {

		private final int index;

		private final int position;

		private final Node node;

		private IndexableNode(final int index, final int position, final Node node) {
			this.index = index;
			this.position = position;
			this.node = node;
		}

		@Override
		public int compareTo(final IndexableNode o) {
			if (o != null) {
				final int result = Integer.valueOf(this.index).compareTo(o.index);
				if (result == 0) {
					return Integer.valueOf(this.position).compareTo(o.position);
				}
				return result;
			}
			return 0;
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		private Node getNode() {
			return this.node;
		}

	}

}
