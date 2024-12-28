package com.ontimize.builder.xml;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ontimize.builder.ApplicationBuilder;
import com.ontimize.builder.ButtonBarBuilder;
import com.ontimize.builder.MenuBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MenuListener;
import com.ontimize.gui.ToolBarListener;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.manager.ITreeFormManager;
import com.ontimize.jee.common.builder.CustomNode;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.extend.ExtendedClientApplicationParser;
import com.ontimize.jee.common.util.extend.ExtendedXmlParser;
import com.ontimize.jee.common.util.extend.OrderDocument;
import com.ontimize.jee.common.xml.XMLUtil;
import com.ontimize.module.IModuleActionMenuListener;
import com.ontimize.module.IModuleActionToolBarListener;
import com.ontimize.module.ModuleManager;
import com.ontimize.module.ModuleManager.ModuleType;
import com.ontimize.module.OModule;
import com.ontimize.util.ParseUtils;
import com.ontimize.xml.XMLInterpreter;

/**
 * This class creates an application from a XML file
 *
 * <PRE>
 * &lt;?xml version=&quot;1.0&quot; encoding=&quot;ISO-8859-1&quot; ?&gt;
 * &lt;MainApplication resources=&quot;untitled1.bundleComponentes&quot; locale=&quot;en_US&quot;&gt;</br>
 * &lt;ReferenceLocator class=&quot;com.ontimize.locator.ReferenceLocator&quot; packageorhostname=&quot;untitled1.&quot;/&gt;&lt;br&gt;
 * &lt;Menu archive=&quot;applicationmenu.xml&quot;/&gt;&lt;br&gt;
 * &lt;Toolbar archive=&quot;toolbarbuttons.xml&quot;/&gt;&lt;br&gt;
 * &lt;MenuListener class=&quot;untitled1.MenuListener&quot;/&gt; Must have an empty constructor&lt;br&gt;
 * &lt;ToolbarListener class=&quot;untitled1.ToolbarListener&quot;/&gt; Must have an empty constructor&lt;br&gt;
 * &lt;FormManager id=&quot;manager1&quot; tree=&quot;treename1.xml&quot;
 * form=&quot;formname1.xml&quot;&gt;&lt;br&gt;
 * &lt;InteractionManager form=&quot;formname2.xml&quot; class=&quot;package.IManagerClassName&quot;/&gt;&lt;br&gt;
 * &lt;FManager form=&quot;formname3.xml&quot; fmid=&quot;managerId&quot;/&gt;&lt;br&gt;
 * &lt;/FormManager&gt;&lt;br&gt;
 * lt;/AplicacionGeneral&gt;
 * </PRE>
 *
 * Added: FManager form="" fmid=""
 */

public class XMLApplicationBuilder extends XMLInterpreter implements ApplicationBuilder {

	private static final Logger logger = LoggerFactory.getLogger(XMLApplicationBuilder.class);

	protected static final String FMID = "fmid";

	public static final String FMANAGER = "FManager";

	public static final String INTERACTION_MANAGER = "InteractionManager";

	public static final String INTERACTION_MANAGER_ACTION = "InteractionManagerAction";

	protected static final String ID = "id";

	protected static final String TREE = "tree";

	protected static final String TREE_CLASS = "treeclass";

	protected static final String FORM = "form";

	public static final String RULES = "rules";

	protected static final String ACTION = "action";

	public static final String MODULE = "Module";

	public static final String MENULISTENER = "MenuListener";

	public static final String TOOLBARLISTENER = "ToolbarListener";

	public static final String TOOLBAR = "Toolbar";

	public static final String MENU = "Menu";

	public static final String MENULISTENER_BUILDED = "MenuListenerBuilder";

	public static final String TOOLBARLISTENER_BUILDED = "ToolbarListenerBuilder";

	public static final String TOOLBAR_BUILDED = "ToolbarBuilder";

	public static final String MENU_BUILDED = "MenuBuilder";

	protected static final String REFLOCATOR = "ReferenceLocator";

	protected static final String LOCALREFERENCES = "LocalReferences";

	protected static final String MODULE_DISCOVERY_ATTR = "modulediscovery";

	public static boolean INCLUDE_DEFAULT_LABELS = true;

	// FIXME DMS
	public static final String FILE_TRANSFER_CLIENT = "FILE_TRANSFER_CLIENT";

	private static Object iftc = null;
	static {
		try {
			final Class c = Class.forName("com.ontimize.dms.filetransfer.client.LoadTransferClient");
			XMLApplicationBuilder.iftc = c.newInstance();
		} catch (final Exception ex) {
			XMLApplicationBuilder.logger.debug("DMS hasn't been detected", ex);
		}
	}

	protected static ExtendedClientApplicationParser clientApplicationParser = new ExtendedClientApplicationParser();

	protected Map equivalentLabelsList = new Hashtable();

	protected String uRILabelsFile = null;

	protected String packageA = "com.ontimize.gui.";

	protected XMLMenuBuilder menuBuilder = null;

	protected XMLButtonBarBuilder toolbarBuilder = null;

	protected ModuleManager moduleManager;

	public XMLApplicationBuilder(final String labelsFileURI, final String packageName) throws Exception {
		this(labelsFileURI);
		this.packageA = packageName;

	}

	public XMLApplicationBuilder(final String labelsFileURI) throws Exception {
		if (XMLApplicationBuilder.INCLUDE_DEFAULT_LABELS) {
			this.equivalentLabelsList = this.getDefaultLabelList();
		}

		this.uRILabelsFile = labelsFileURI;
		try {
			this.processLabelFile(this.uRILabelsFile, this.equivalentLabelsList, new ArrayList());
		} catch (final Exception e) {
			XMLApplicationBuilder.logger.error("Process label file:", e);
		}

		try {
			this.moduleManager = new ModuleManager(ModuleType.CLIENT);
		} catch (final Exception e) {
			XMLApplicationBuilder.logger.error("Processing Ontimize Modules", e);
		}
	}

	@Override
	public Application buildApplication(String fileURI) {
		String uRIBase = null;

		String fileRelCP = null;
		String baseCP = null;
		if (fileURI.startsWith("/")) {
			// Classpath
			fileRelCP = fileURI.substring(1);
			final int last = fileRelCP.lastIndexOf("/");
			if (last >= 0) {
				baseCP = fileRelCP.substring(0, last + 1);
			} else {
				baseCP = "";
			}

			// Now the URI
			final URL url = this.getClass().getClassLoader().getResource(fileRelCP);
			fileURI = url.toString();
		} else {
			// Searches the last path separator of fileURI
			int index = 0;
			for (int j = 0; j < fileURI.length(); j++) {
				if ((fileURI.charAt(j) == '/') || (fileURI.charAt(j) == '\\')) {
					index = j;
				}
			}
			// .xml files must be in this location
			uRIBase = fileURI.substring(0, index + 1);
		}

		boolean locatorBuilded = false;
		EntityReferenceLocator locator = null;

		boolean menuBuilded = false;
		boolean toolbarBuilded = false;
		boolean menuListenerBuilded = false;
		boolean toolbarListenerBuilded = false;

		MenuListener menuListener = null;
		ToolBarListener toolbarListener = null;
		JMenuBar menuBar = null;
		JToolBar toolbar = null;

		// Root node must be an application. All nodes are loaded with
		// <pre>Class.forName();</pre>
		try {
			final long initTime = System.currentTimeMillis();
			Document documentClientApplication = this.getDocumentModel(fileURI);
			documentClientApplication = this.performExtendedClientApplication(documentClientApplication, fileRelCP,
					baseCP);
			final CustomNode auxiliar = new CustomNode(documentClientApplication.getDocumentElement());
			// In this moment auxiliar variable is the root node.
			final Object application = this.instance(auxiliar);
			if (application instanceof Application) {
				final Map<String, String> applicationAttrs = auxiliar.hashtableAttribute();
				if (ParseUtils.getBoolean(applicationAttrs.get(XMLApplicationBuilder.MODULE_DISCOVERY_ATTR), true)) {
					this.moduleManager.retrieveOntimizeModules();
				}
				// First of all must set the reference locator.

				final Map<String, Object> mapLocator = this.findAndSetReferenceLocator(auxiliar, locatorBuilded, application);

				if (mapLocator.containsKey(XMLApplicationBuilder.REFLOCATOR)) {
					locator = (EntityReferenceLocator) mapLocator.get(XMLApplicationBuilder.REFLOCATOR);
					locatorBuilded = true;
				}

				if (!locatorBuilded) {
					XMLApplicationBuilder.logger.error("Reference Locator not specified");
					throw new Exception("Error: Reference Locator not specified");
				}

				// Children of this node must be FormManager nodes

				final Map<String, Object> mapFromXML = this.createToolbarAndMenuFromXml(auxiliar, baseCP, uRIBase,
						application, locator);

				// Adding MODULES
				try {
					final List<OModule> modules = this.moduleManager.getModules();
					this.addingModulesBuildApplication(modules, application, locator, mapFromXML);
				} catch (final Exception ex) {
					XMLApplicationBuilder.logger.error("Building FormManager modules", ex);
				}

				menuBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.MENU_BUILDED);
				toolbarBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.TOOLBAR_BUILDED);
				menuListenerBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.MENULISTENER_BUILDED);
				toolbarListenerBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.TOOLBAR_BUILDED);

				menuListener = (MenuListener) mapFromXML.get(XMLApplicationBuilder.MENULISTENER);
				toolbarListener = (ToolBarListener) mapFromXML.get(XMLApplicationBuilder.TOOLBARLISTENER);
				menuBar = (JMenuBar) mapFromXML.get(XMLApplicationBuilder.MENU);
				toolbar = (JToolBar) mapFromXML.get(XMLApplicationBuilder.TOOLBAR);

				final long endTime = System.currentTimeMillis();
				final double totalTime = (endTime - initTime) / 1000.0;
				XMLApplicationBuilder.logger.trace("Time elapsed while creating application: {} seconds.",
						new Double(totalTime).toString());

				if ((menuBuilded) && (menuListenerBuilded)) {
					((Application) application).setMenuListener(menuListener);
					menuListener.addMenuToListenFor(menuBar);
					menuListener.setApplication((Application) application);
					menuListener.setInitialState();
					if (menuBar instanceof Internationalization) {
						((Internationalization) menuBar)
						.setResourceBundle(((Application) application).getResourceBundle());
					}
				} else {
					XMLApplicationBuilder.logger.warn("Menu Listener not set. Cause: Menu not specified");
				}
				if ((toolbarBuilded) && (toolbarListenerBuilded)) {
					((Application) application).setToolBarListener(toolbarListener);
					toolbarListener.addToolBarToListenFor(toolbar);
					toolbarListener.setApplication((Application) application);
					toolbarListener.setInitialState();
					if (toolbar instanceof Internationalization) {
						((Internationalization) toolbar)
						.setResourceBundle(((Application) application).getResourceBundle());
					}
				}
				return (Application) application;
			} else {
				XMLApplicationBuilder.logger.error("Root is not a com.ontimize.gui.Application instance");
				return null;
			}
		} catch (final Exception e2) {
			XMLApplicationBuilder.logger.error(e2.getMessage(), e2);
			return null;
		}
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param auxiliar
	 * @param locatorBuilded
	 * @param application
	 * @return
	 */
	protected Map<String, Object> findAndSetReferenceLocator(final CustomNode auxiliar, final boolean locatorBuilded,
			final Object application) {
		// First of all must set the reference locator.
		final Map<String, Object> toret = new HashMap();

		for (int i = 0; i < auxiliar.getChildrenNumber(); i++) {
			Thread.yield();
			final CustomNode node = auxiliar.child(i);
			// Creates the object
			if (node.isTag()) {
				final String tag = node.getNodeInfo();
				if (tag.equals(XMLApplicationBuilder.REFLOCATOR) && (!locatorBuilded)) {
					final Map param = node.hashtableAttribute();
					final Object locatorClassName = param.get("class");
					if (locatorClassName == null) {
						XMLApplicationBuilder.logger.info("ReferenceLocator class not specified");
					} else {
						try {
							final Class locatorClass = Class.forName(locatorClassName.toString());
							final Class[] p = { Hashtable.class };
							final Constructor constructorHash = locatorClass.getConstructor(p);
							final Object[] params = { param };
							final Object lc = constructorHash.newInstance(params);
							if (lc instanceof EntityReferenceLocator) {
								final EntityReferenceLocator locator = (EntityReferenceLocator) lc;
								((Application) application).setReferencesLocator(locator);
								toret.put(XMLApplicationBuilder.REFLOCATOR, locator);
							}
							break;
						} catch (final NoSuchMethodException err) {
							XMLApplicationBuilder.logger.error(
									"Instantiation requires Constructor that accepts a Map as input parameter.{}",
									node.toString(), err);
						} catch (final SecurityException e) {
							XMLApplicationBuilder.logger.error("Object cannot be instantiated in {}", node.toString(),
									e);
						} catch (final Exception e) {
							XMLApplicationBuilder.logger.error("Error creating EntityReferenceLocator {}:",
									locatorClassName, e);
						}
					}
				}
			}
		}

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param node
	 * @param baseCP
	 * @param uRIBase
	 * @param application
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> findAndSetMenuBar(final CustomNode node, final String baseCP, final String uRIBase, final Object application)
			throws Exception {
		final Map<String, Object> toret = new HashMap();

		JMenuBar menuBar = null;
		final String menuFile = node.hashtableAttribute().get("archive");
		if (menuFile != null) {
			this.menuBuilder = new XMLMenuBuilder(this.equivalentLabelsList, this.packageA);

			if (baseCP != null) {
				this.menuBuilder.setBaseClasspath(baseCP);
				final URL url = this.getClass().getClassLoader().getResource(baseCP + menuFile);
				if (url == null) {
					XMLApplicationBuilder.logger.info("Menu file not found: {} {}", baseCP, menuFile);
				} else {
					menuBar = this.menuBuilder.buildMenu(url.toString());
				}
			} else {
				menuBar = this.menuBuilder.buildMenu(uRIBase + menuFile);
			}
			((Application) application).setMenu(menuBar);
			if (menuBar != null) {
				toret.put(XMLApplicationBuilder.MENU, menuBar);
			} else {
				XMLApplicationBuilder.logger.error("Creating menu");
			}
		} else {
			XMLApplicationBuilder.logger.warn("'archive' parameter is missing in MENU node");
		}

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param node
	 * @param baseCP
	 * @param uRIBase
	 * @param application
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> findAndSetToolBar(final CustomNode node, final String baseCP, final String uRIBase, final Object application)
			throws Exception {

		if (!MainApplication.checkApplicationPermission("ToolbarPermission")) {
			return null;
		}

		final Map<String, Object> toret = new HashMap();
		JToolBar toolbar = null;
		final String buttonsBarFile = node.hashtableAttribute().get("archive");
		if (buttonsBarFile != null) {
			this.toolbarBuilder = new XMLButtonBarBuilder(this.equivalentLabelsList, this.packageA);
			if (baseCP != null) {
				this.toolbarBuilder.setBaseClasspath(baseCP);
				final URL url = this.getClass().getClassLoader().getResource(baseCP + buttonsBarFile);
				if (url == null) {
					XMLApplicationBuilder.logger.warn("Toolbar file not found: {}", baseCP, buttonsBarFile);
				} else {
					toolbar = this.toolbarBuilder.buildButtonBar(url.toString());
				}
			} else {
				toolbar = this.toolbarBuilder.buildButtonBar(uRIBase + buttonsBarFile);
			}
			((Application) application).setToolBar(toolbar);
			toret.put(XMLApplicationBuilder.TOOLBAR, toolbar);
		} else {
			XMLApplicationBuilder.logger.warn("'archive' parameter is missing in TOOLBAR node");
		}

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param node
	 * @return
	 */
	protected Map<String, Object> findAndSetToolBarListener(final CustomNode node) {

		final Map<String, Object> toret = new HashMap();

		if (!MainApplication.checkApplicationPermission("ToolbarPermission")) {
			return null;
		}
		final Map param = node.hashtableAttribute();
		final Object classTListener = param.get("class");
		if (classTListener == null) {
			XMLApplicationBuilder.logger.warn("Toolbar Listener not specified");
		} else {
			try {
				final Class classM = Class.forName(classTListener.toString());
				final Object ml = classM.newInstance();
				if (ml instanceof ToolBarListener) {
					toret.put(XMLApplicationBuilder.TOOLBARLISTENER, ml);
				} else {
					XMLApplicationBuilder.logger.warn(
							"The specified ToolBar Listener is not an instance of ToolBarListener in {}",
							node.toString());
				}
			} catch (final SecurityException e) {
				XMLApplicationBuilder.logger.error(" Object cannot be instantiated in {}", node.toString(), e);
			} catch (final Exception e) {
				XMLApplicationBuilder.logger.error("Error creating ToolBar Listener {}", classTListener, e);
			}
		}

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param node
	 * @return
	 */
	protected Map<String, Object> findAndSetMenuBarListener(final CustomNode node) {
		final Map<String, Object> toret = new HashMap();

		final Map param = node.hashtableAttribute();
		final Object classMListener = param.get("class");
		if (classMListener == null) {
			XMLApplicationBuilder.logger.info("MenuListener class not specified");
		} else {
			try {
				final Class classM = Class.forName(classMListener.toString());
				final Object ml = classM.newInstance();
				if (ml instanceof MenuListener) {
					toret.put(XMLApplicationBuilder.MENULISTENER, ml);
				} else {
					XMLApplicationBuilder.logger
					.warn("The specified Menu Listener is not an instance of MenuListener in {}", node.toString());
				}
			} catch (final SecurityException e) {
				XMLApplicationBuilder.logger.error("Object cannot be instantiated in {}", node.toString(), e);
			} catch (final Exception e) {
				XMLApplicationBuilder.logger.error("Creating Menu Listener {}", classMListener, e);
			}
		}

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param node
	 */
	protected void findAndSetFileTransferClient(final CustomNode node) {
		if (XMLApplicationBuilder.iftc != null) {
			final org.w3c.dom.Node current = node.getXMLDocumentNode();
			try {
				Method m = XMLApplicationBuilder.iftc.getClass()
						.getMethod("parseXML", new Class[] { org.w3c.dom.Node.class });
				final Object Map = m.invoke(XMLApplicationBuilder.iftc, new Object[] { current });

				m = XMLApplicationBuilder.iftc.getClass()
						.getMethod("setConfiguration", new Class[] { Hashtable.class });
				m.invoke(XMLApplicationBuilder.iftc, new Object[] { Map });

				m = XMLApplicationBuilder.iftc.getClass().getMethod("start", null);
				m.invoke(XMLApplicationBuilder.iftc, null);

			} catch (final Exception ex) {
				XMLApplicationBuilder.logger.error("Creating DMS", ex);
			}
		}
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param auxiliar
	 * @param baseCP
	 * @param uRIBase
	 * @param application
	 * @param locator
	 * @return
	 */
	protected Map<String, Object> createToolbarAndMenuFromXml(final CustomNode auxiliar, final String baseCP, final String uRIBase,
			final Object application, final EntityReferenceLocator locator) {

		final Map<String, Object> toret = new HashMap();

		boolean menuBuilded = false;
		boolean toolbarBuilded = false;
		boolean menuListenerBuilded = false;
		boolean toolbarListenerBuilded = false;

		MenuListener menuListener = null;
		ToolBarListener toolbarListener = null;
		JMenuBar menuBar = null;
		JToolBar toolbar = null;

		boolean fileTransferClient = false;

		for (int i = 0; i < auxiliar.getChildrenNumber(); i++) {
			Thread.yield();
			try {
				final CustomNode node = auxiliar.child(i);
				// Creates the object
				if (!node.isTag()) {
					continue;
				}

				final String tag = node.getNodeInfo();
				if (tag.equals(XMLApplicationBuilder.MENU) && (!menuBuilded)) {
					final Map<String, Object> mapMenu = this.findAndSetMenuBar(node, baseCP, uRIBase, application);
					if (mapMenu.containsKey(XMLApplicationBuilder.MENU)) {
						menuBar = (JMenuBar) mapMenu.get(XMLApplicationBuilder.MENU);
						menuBuilded = true;
					}
				} else if (tag.equals(XMLApplicationBuilder.TOOLBAR) && (!toolbarBuilded)) {
					final Map<String, Object> mapToolbar = this.findAndSetToolBar(node, baseCP, uRIBase, application);
					if (mapToolbar != null) {
						toolbarBuilded = true;
						toolbar = (JToolBar) mapToolbar.get(XMLApplicationBuilder.TOOLBAR);
					}
				} else if (tag.equals(XMLApplicationBuilder.TOOLBARLISTENER) && (!toolbarListenerBuilded)) {

					final Map<String, Object> mapToolBarListener = this.findAndSetToolBarListener(node);
					if (mapToolBarListener != null) {
						toolbarListenerBuilded = true;
						toolbarListener = (ToolBarListener) mapToolBarListener
								.get(XMLApplicationBuilder.TOOLBARLISTENER);
					}
				} else if (tag.equals(XMLApplicationBuilder.MENULISTENER) && (!menuListenerBuilded)) {
					menuListenerBuilded = true;
					final Map<String, Object> mapMenuBarListener = this.findAndSetMenuBarListener(node);
					menuListener = (MenuListener) mapMenuBarListener.get(XMLApplicationBuilder.MENULISTENER);

				} else if (tag.equalsIgnoreCase(XMLApplicationBuilder.FILE_TRANSFER_CLIENT) && (!fileTransferClient)) {
					fileTransferClient = true;
					this.findAndSetFileTransferClient(node);
				} else if (XMLApplicationBuilder.MODULE.equals(tag)) {
					final String moduleFile = node.hashtableAttribute().get("archive");
					this.moduleManager.processModule(moduleFile);
				} else if (!XMLApplicationBuilder.REFLOCATOR.equals(tag)) {
					this.buildFormManager(node, (Application) application, locator, baseCP);
				}

			} catch (final Exception e) {
				XMLApplicationBuilder.logger.error("Building application", e);
			}
		}

		toret.put(XMLApplicationBuilder.MENU_BUILDED, menuBuilded);
		toret.put(XMLApplicationBuilder.TOOLBAR_BUILDED, toolbarBuilded);
		toret.put(XMLApplicationBuilder.MENULISTENER_BUILDED, menuListenerBuilded);
		toret.put(XMLApplicationBuilder.TOOLBARLISTENER_BUILDED, toolbarListenerBuilded);

		toret.put(XMLApplicationBuilder.MENU, menuBar);
		toret.put(XMLApplicationBuilder.TOOLBAR, toolbar);
		toret.put(XMLApplicationBuilder.MENULISTENER, menuListener);
		toret.put(XMLApplicationBuilder.TOOLBARLISTENER, toolbarListener);

		return toret;
	}

	/**
	 * Method to reduce de complexity of {@link #buildApplication(String)}
	 * @param modules
	 * @param application
	 * @param locator
	 * @param mapFromXML
	 * @return
	 * @throws Exception
	 */
	protected Map<String, Object> addingModulesBuildApplication(final List<OModule> modules, final Object application,
			final EntityReferenceLocator locator, final Map<String, Object> mapFromXML)
					throws Exception {

		final boolean toolbarBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.TOOLBAR_BUILDED);
		final boolean toolbarListenerBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.TOOLBARLISTENER_BUILDED);
		final boolean menuBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.MENU_BUILDED);
		final boolean menuListenerBuilded = (Boolean) mapFromXML.get(XMLApplicationBuilder.MENULISTENER_BUILDED);

		// MenuListener menuListener = (MenuListener)
		// mapFromXML.get(XMLApplicationBuilder.MENULISTENER);

		for (final OModule currentModule : modules) {
			final List<CustomNode> formManagerDefinition = currentModule.getFormManagers();
			for (final CustomNode node : formManagerDefinition) {
				final String clientModulePackage = currentModule.getClientBaseClasspath();
				this.buildFormManager(node, (Application) application, locator, clientModulePackage);
			}

			if ((locator instanceof ClientReferenceLocator) && (currentModule.getLocalEntities() != null)) {
				((ClientReferenceLocator) locator).addModuleMemoryEntity(currentModule.getLocalEntityPackage(),
						currentModule.getLocalEntities());
			}

			if (toolbarBuilded && (currentModule.getToolbar() != null)) {
				this.toolbarBuilder.processModules(currentModule.getId(), currentModule.getToolbar(),
						((Application) application).getToolBar());
			}

			if (toolbarListenerBuilded && (currentModule.getToolbarListener() != null)) {
				try {
					final String classTListener = currentModule.getToolbarListener();
					if (classTListener == null) {
						XMLApplicationBuilder.logger.warn("Toolbar Listener not specified");
					} else {
						final Class classM = Class.forName(classTListener);
						final Object ml = classM.newInstance();
						if (ml instanceof IModuleActionToolBarListener) {
							((ToolBarListener) mapFromXML.get(XMLApplicationBuilder.TOOLBARLISTENER))
							.addListener((IModuleActionToolBarListener) ml);
							((IModuleActionToolBarListener) ml).setApplication((Application) application);
						} else {
							XMLApplicationBuilder.logger.warn(
									"The specified ToolBar Listener is not an instance of IModuleActionToolBarListener in {}",
									classTListener);
						}
					}
				} catch (final Exception ex) {
					XMLApplicationBuilder.logger.error("TOOLBARLISTENER cannot be instantiated", ex);
				}
			}

			if (menuBuilded && (currentModule.getMenu() != null)) {
				this.menuBuilder.processModules(currentModule.getMenu(), ((Application) application).getMenu());
			}

			if (menuListenerBuilded && (currentModule.getMenuListener() != null)) {
				try {
					final String classMListener = currentModule.getMenuListener();
					if (classMListener == null) {
						XMLApplicationBuilder.logger.warn("Menu listener not specified");
					} else {
						final Class classM = Class.forName(classMListener);
						final Object ml = classM.newInstance();
						if (ml instanceof IModuleActionMenuListener) {
							((MenuListener) mapFromXML.get(XMLApplicationBuilder.MENULISTENER))
							.addListener((IModuleActionMenuListener) ml);
							((IModuleActionMenuListener) ml).setApplication((Application) application);
						} else {
							XMLApplicationBuilder.logger.warn(
									"The specified ToolBar Listener is not an instance of IModuleActionToolBarListener in {}",
									classMListener);
						}
					}
				} catch (final Exception ex) {
					XMLApplicationBuilder.logger.error("MENULISTENER cannot be instantiated", ex);
				}
			}
		}

		return mapFromXML;
	}

	protected Map analyzeChildren(final CustomNode node) {
		final HashMap map = new HashMap();
		final HashMap interactionManagerMap = new HashMap();
		final HashMap interactionManagerRuleMap = new HashMap();
		final HashMap interactionManagerActionMap = new HashMap();
		final HashMap formManagerMap = new HashMap();

		for (int k = 0; k < node.getChildrenNumber(); k++) {
			final CustomNode child = node.child(k);
			if (child.isTag()) {
				final String childTag = child.getNodeInfo();
				if (childTag.equals(XMLApplicationBuilder.INTERACTION_MANAGER)) {
					final Map childParams = child.hashtableAttribute();
					final Object f = childParams.get(XMLApplicationBuilder.FORM);
					final Object className = childParams.get("class");
					if ((f != null) && (className != null)) {
						interactionManagerMap.put(f.toString(), className.toString());
					}
					final Object oAction = childParams.get(XMLApplicationBuilder.ACTION);
					if ((f != null) && (oAction != null)) {
						interactionManagerActionMap.put(f.toString(), oAction.toString());
					}
					final Object oRules = childParams.get(XMLApplicationBuilder.RULES);
					if ((f != null) && (oRules != null)) {
						interactionManagerRuleMap.put(f.toString(), oRules.toString());
					}
				} else if (childTag.equals(XMLApplicationBuilder.FMANAGER)) {
					final Map childParams = child.hashtableAttribute();
					final Object f = childParams.get(XMLApplicationBuilder.FORM);
					final Object idfm = childParams.get(XMLApplicationBuilder.FMID);
					if (f != null) {
						formManagerMap.put(f.toString(), idfm);
					}
				}
			}
		}

		map.put(XMLApplicationBuilder.INTERACTION_MANAGER, interactionManagerMap);
		map.put(XMLApplicationBuilder.INTERACTION_MANAGER_ACTION, interactionManagerActionMap);
		map.put(XMLApplicationBuilder.RULES, interactionManagerRuleMap);
		map.put(XMLApplicationBuilder.FMANAGER, formManagerMap);
		return map;
	}

	/**
	 * Creates a new instance of the class indicated by the node parameter and return it
	 * @param node
	 * @return
	 */
	protected Object instance(final CustomNode node) {
		try {
			if (!node.isTag()) {
				return null;
			}
			final long t = System.currentTimeMillis();
			Object equivalence = this.equivalentLabelsList.get(node.getNodeInfo());
			if (equivalence == null) {
				equivalence = node.getNodeInfo();
			}
			final Class rootClass = Class.forName(this.packageA + equivalence.toString());
			final Class[] p = { Hashtable.class };
			final Constructor constructorHash = rootClass.getConstructor(p);
			final Map h = node.hashtableAttribute();
			final Object[] parameters = { h };
			final Object o = constructorHash.newInstance(parameters);
			XMLApplicationBuilder.logger.trace("Object creation time {}: {}", equivalence,
					System.currentTimeMillis() - t);
			return o;
		} catch (final NoSuchMethodException err) {
			XMLApplicationBuilder.logger.error(
					"Instantiation requires Constructor that accepts a Map as input parameter. {}",
					node.toString(), err);
			return null;
		} catch (final SecurityException e) {
			XMLApplicationBuilder.logger.error(" Object cannot be instantiated in {}", node.toString(), e);
			return null;
		} catch (final Exception e) {
			XMLApplicationBuilder.logger.error("Error instantiating: {}", node.getNodeInfo(), e);
			return null;
		}
	}

	/**
	 * Creates a new instance of the class indicated by the node parameter and returns it
	 * @param node
	 * @param param
	 * @return
	 */
	protected Object instance(final CustomNode node, final Map param) {
		if ((node == null) || (!node.isTag())) {
			return null;
		}
		return this.instance(node.getNodeInfo(), param);
	}

	public Object instance(final String nameInstance, final Map param) {
		try {
			final long t = System.currentTimeMillis();
			Object equivalence = this.equivalentLabelsList.get(nameInstance);
			if (equivalence == null) {
				equivalence = nameInstance;
			}
			final Class rootClass = Class.forName(this.packageA + equivalence.toString());
			final Class[] p = { Hashtable.class };
			final Constructor constructorHash = rootClass.getConstructor(p);
			final Object[] parameters = { param };
			final Object o = constructorHash.newInstance(parameters);
			XMLApplicationBuilder.logger.trace("Object creation time  {} : {}", equivalence,
					System.currentTimeMillis() - t);
			return o;
		} catch (final NoSuchMethodException err) {
			XMLApplicationBuilder.logger.error(
					"A Constructor method with a Map as input parameter has not been found. {}", nameInstance,
					err);
			return null;
		} catch (final SecurityException e) {
			XMLApplicationBuilder.logger.error("Object cannot be instantiated in {}", nameInstance, e);
			return null;
		} catch (final Exception e) {
			XMLApplicationBuilder.logger.error("Error instantiating: ", nameInstance, e);
			return null;
		}
	}

	protected Document performExtendedClientApplication(Document doc, final String fileURI, final String baseCP) {
		final Enumeration<URL> input = ExtendedXmlParser.getExtendedFile(fileURI, baseCP);

		if ((input == null) || (!input.hasMoreElements())) {
			return doc;
		}

		final List<OrderDocument> extendedDocumentList = new ArrayList<OrderDocument>();
		Document extendedDocument = null;

		while (input.hasMoreElements()) {
			try {
				extendedDocument = XMLUtil.getExtendedDocument(input.nextElement().openStream());
				final Node s = extendedDocument.getChildNodes().item(0).getAttributes().getNamedItem(ExtendedXmlParser.ORDER);
				final int index = s != null ? Integer.parseInt(s.getNodeValue()) : -1;
				extendedDocumentList.add(new OrderDocument(index, extendedDocument));
			} catch (final Exception e) {
				XMLApplicationBuilder.logger.error("{}", e);
			}
		}

		Collections.sort(extendedDocumentList);

		for (final OrderDocument oDocument : extendedDocumentList) {
			try {
				doc = XMLApplicationBuilder.clientApplicationParser.parseExtendedXml(doc, oDocument.getDocument());
				XMLApplicationBuilder.logger.debug("ClientApplication extends, Load order -> {}", oDocument.getIndex());
			} catch (final Exception e) {
				XMLApplicationBuilder.logger.error("Extending ClientApplication", e);
			}
		}

		return doc;
	}

	public IFormManager createFormManager(final String className, final Map parameters) {

		try {
			final Application application = ApplicationManager.getApplication();
			parameters.put(IFormManager.FORM_BUILDER, new XMLFormBuilder(this.equivalentLabelsList, this.packageA));
			parameters.put(ITreeFormManager.TREE_BUILDER, new XMLTreeBuilder(this.equivalentLabelsList, this.packageA));
			parameters.put(IFormManager.FRAME, application.getFrame());
			final JPanel panel = new JPanel();
			parameters.put(IFormManager.CONTAINER, panel);

			// Modification: 27-04-2006. Load from the classpath
			parameters.put(IFormManager.USE_CLASS_PATH, "yes");

			parameters.put(IFormManager.LOCATOR, application.getReferenceLocator());
			parameters.put(IFormManager.APPLICATION, application);
			return (IFormManager) this.instance(className, parameters);

		} catch (final Exception e) {
			XMLApplicationBuilder.logger.error("Creating FormManager {} ", className, e);
		}
		return null;
	}

	public MenuBuilder getMenuBuilder() {
		return this.menuBuilder;
	}

	public ButtonBarBuilder getToolbarBuilder() {
		return this.toolbarBuilder;
	}

	protected static XMLApplicationBuilder builder = null;

	public static void setXMLApplicationBuilder(final XMLApplicationBuilder builder) {
		XMLApplicationBuilder.builder = builder;
	}

	public static XMLApplicationBuilder getXMLApplicationBuilder() {
		return XMLApplicationBuilder.builder;
	}

	protected void buildFormManager(final CustomNode node, final Application application, final EntityReferenceLocator locator,
			final String baseCP) throws Exception {
		final Map<String, String> xmlParams = node.hashtableAttribute();
		final Map<String, Object> param = new Hashtable<String, Object>(xmlParams);

		final XMLFormBuilder xmlFormBuilder = new XMLFormBuilder(this.equivalentLabelsList, this.packageA);
		xmlFormBuilder.setModuleManager(this.moduleManager);

		// Now additional values
		param.put(IFormManager.FORM_BUILDER, xmlFormBuilder);
		param.put(ITreeFormManager.TREE_BUILDER, new XMLTreeBuilder(this.equivalentLabelsList, this.packageA));
		param.put(IFormManager.FRAME, application.getFrame());
		final JPanel panel = new JPanel();
		param.put(IFormManager.CONTAINER, panel);

		// Modification: 27-04-2006. Load from the
		// classpath
		param.put(IFormManager.USE_CLASS_PATH, "yes");

		final Object form = param.get(XMLApplicationBuilder.FORM);
		if (form != null) {
			param.put(XMLApplicationBuilder.FORM, baseCP + form);
		}

		final Object rules = param.get(XMLApplicationBuilder.RULES);
		if (rules != null) {
			param.put(XMLApplicationBuilder.RULES, baseCP + rules);
		}

		final Object tree = param.get(XMLApplicationBuilder.TREE);
		if (tree != null) {
			param.put(XMLApplicationBuilder.TREE, baseCP + tree);
		}

		final Object treeclass = param.get(XMLApplicationBuilder.TREE_CLASS);
		if (treeclass != null) {
			param.put(XMLApplicationBuilder.TREE_CLASS, treeclass);
		}

		param.put(IFormManager.LOCATOR, locator);
		param.put(IFormManager.APPLICATION, application);

		final Map detail = this.analyzeChildren(node);
		param.put(IFormManager.DETAIL, detail);

		final Object mForms = this.instance(node, param);
		if ((mForms != null) && (mForms instanceof IFormManager)) {
			final Object id = param.get(XMLApplicationBuilder.ID);
			if (id == null) {
				XMLApplicationBuilder.logger.warn("'id' attribute is missing in IFormManager tag {}",
						((IFormManager) mForms).getId());
			} else {
				application.registerFormManager((String) id, (IFormManager) mForms);
			}
		}
	}

}
