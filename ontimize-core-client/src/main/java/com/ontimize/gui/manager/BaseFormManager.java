package com.ontimize.gui.manager;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.ApplicationBuilder;
import com.ontimize.builder.FormBuilder;
import com.ontimize.builder.xml.XMLApplicationBuilder;
import com.ontimize.builder.xml.XMLFormBuilder;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BackgroundFormManagerBuilderManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.CustomColumnGridBagLayout;
import com.ontimize.gui.DataNavigationEvent;
import com.ontimize.gui.DynamicFormManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerLoader;
import com.ontimize.gui.TableBasicInteractionManager;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.i18n.ExtendedPropertiesBundle;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.security.FMPermission;
import com.ontimize.jee.common.xml.XMLClientProvider;
import com.ontimize.jee.common.xml.XMLFormProvider;
import com.ontimize.jee.common.xml.XMLUtil;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ObjectWrapper;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.rule.DefaultRuleEngineLoader;
import com.ontimize.util.rule.IRules;
import com.ontimize.util.rule.RuleEngine;
import com.ontimize.util.rule.RuleEngineLoader;
import com.ontimize.util.rule.RuleParser;

/**
 * The <code>FormManager</code> is used to create, register and manage a group of form.
 *
 * @author Imatia Innovation
 */

public abstract class BaseFormManager extends JPanel implements IFormManager {

	private static final Logger logger = LoggerFactory.getLogger(BaseFormManager.class);

	/** GUI Text */
	public static String M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE = "formmanager.no_data_found";

	/** GUI Text */
	public static String M_MODIFIED_DATA = "formmanager.unsaved_changes_in_the_form";

	/** Application frame reference. */
	protected Frame applicationFrame = null;

	/**
	 * Panel used to add new buttons at <code>FormManager</code>
	 */
	protected JPanel panelTop = new JPanel(new BorderLayout());

	/**
	 * Container where is stored this component in
	 */
	protected Container parent = null;

	/**
	 * Form list that are loaded and layout
	 */
	protected List<String> loadedList = new Vector();

	/** Locale */
	protected Locale locale = Locale.getDefault();

	/**
	 * A <code>Hashtable</code> with a form list where the key is the form name and the value a
	 * <code>Form</code> class instance.
	 */
	protected Map<String, Form>						formReferenceList										= new Hashtable<String, Form>();

	/** Base URI used to obtain the form file. */
	protected String uRIBase = null;

	/**
	 * Condition that determine whether the 'form' attribute value specified a location relative to the
	 * classpath.
	 */
	protected boolean useClasspath = false;

	/**
	 * Base Classpath used whether the useClasspath condition is setted to true
	 */
	protected String baseCP = null;

	/** Entity reference locator reference */
	protected EntityReferenceLocator locator = null;

	/**
	 * A <code>Hashtable</code> with the interaction manager to be registered. The
	 * <code>Hashtable</code> key is the form name and the <code>Hashtable</code> value is the
	 * <code>InteractionManager</code> class instance.
	 */
	protected Map<String, InteractionManager> interactionManagerList = new Hashtable<String, InteractionManager>();

	/**
	 * A <code>Hashtable</code> with the interaction manager class name assigned to each form. The
	 * <code>Hashtable</code> key is the form name and the <code>Hashtable</code> value is the
	 * <code>InteractionManager</code> class name.
	 */
	protected Map<String, String> formInteractionManagerClassNameList = null;

	/**
	 * A <code>Hashtable</code> with the interaction manager class name assigned to each action handler.
	 * The <code>Hashtable</code> key is the form name and the <code>Hashtable</code> value is the
	 * <code>InteractionManagerAction</code> XML definition location.
	 *
	 * @since Ontimize 5.2059EN
	 */
	protected Map formInteractionManagerActionList = null;

	/**
	 * The <code>Hashtable</code> key is the form name and the <code>Hashtable</code> value is the
	 * <code>RuleEngine</code> XML definition location.
	 *
	 * @since Ontimize 5.2075EN
	 */
	protected Map formRules = new Hashtable();

	/**
	 * <code>JPanel</code> reference where the form is layered out in. The layout of this panel is
	 * stored in the {@link #formLayout} variable
	 */
	// protected JPanel formPanel = null;

	/** The form name that is visible in this <code>FormManager</code> */
	protected String currentForm = "";

	/** ResourceBundle file name */
	protected String resourceFileName;

	/** ResourceBundle reference */
	protected ResourceBundle resourceFile;

	/** FormBuilder reference */
	protected FormBuilder formBuilder;

	protected JPanel auxPanel = null;

	protected List<InteractionManager> interactionManagers = new ArrayList<InteractionManager>();

	protected boolean checkModifiedDataForms = true;

	protected FMPermission visiblePermission = null;

	/**
	 * A <code>String</code> with the identifier of this <code>FormManager</code>
	 */
	protected String id = null;

	/**
	 * @since 5.2075EN
	 */
	protected RuleEngine ruleEngine;

	/** Application reference */
	protected Application application = null;

	private final boolean canceledSelectionChange = false;

	private Form activeForm = null;

	/**
	 * A list formed by the forms where this <code>FormManager</code> has been registered as a
	 * <code>DataNavigationListener</code> in.
	 */
	protected List formRegisteredDataNavigationListener = new Vector(0, 5);

	/** The FormManager will dispatch the DataNavigationEvent if this is true */
	protected boolean processDataChangeEvents = true;

	/** The <code>ApplicationPreferences</code> reference */
	protected ApplicationPreferences aPreferences = null;

	/** Condition used to register the preference listener only one time */
	protected boolean preferenceListenersRegistered = false;

	protected String dynamicFormClass = null;

	protected DynamicFormManager formDynamicManager = null;

	/**
	 * If this condition is true, the <code>FormManager</code> will be loaded.
	 */
	protected boolean loadedForm = false;

	/** The <code>Form</code> name that will be shown at first */
	protected String initialForm = null;

	/**
	 * The complete path for rule file for initial form. E.g. 'com/ontimize/client/modules/rules.orl'
	 */
	protected String rules = null;

	/**
	 * The <code>InteractionManager</code> reference of the form that will be shown at first
	 */
	protected InteractionManager initialInteractionManager = null;

	protected boolean setResourceBundleOnLoad = false;

	/**
	 * If this condition is setted to true,this <code>FormManager</code> will not be loaded when is
	 * instanced, that is, the initial <code>Form</code> class, <code>InteractionManager</code> class,
	 * and <code>Tree</code> class will not be instanced until the <code>FormManager</code> is called
	 * for first time. The condition value is established with the {@link #DELAYED_LOAD} attribute,by
	 * default is false.
	 *
	 * @see {@link #FormManager(Map)}
	 */
	protected boolean delayedLoad = false;

	public static String defaultInteractionManagerLoader = "com.ontimize.gui.DefaultInteractionManagerLoader";

	protected String interactionManagerLoader = BaseFormManager.defaultInteractionManagerLoader;

	protected InteractionManagerLoader iManagerLoader = null;

	protected RuleEngineLoader ruleEngineLoader = null;

	protected Map formModifiableFields = new Hashtable();

	protected boolean showFrame;

	protected String icon;

	/**
	 * Creates a FormManager instance with the parameters establishes in <code>Hastable</code>. This
	 * constructor is called from {@link ApplicationBuilder}
	 * <p>
	 * The parameters
	 * <code>Hashtable<code> contains the attribute values set in the Application XML. <p>
	 *
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=
    BOX> <tr> <td><b>attribute</td> <td><b>values</td> <td><b>default</td> <td><b>required</td> <td><b>meaning</td>
	 * </tr>
	 *
	 * <tr> <td>id</td> <td></td> <td></td> <td>yes</td> <td> Establishes the unique identifier for this <code>FormManager</code>
	 * </td>
	 * </tr>
	 * <tr>
	 * <td>useclasspath</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Establishes if the form files are loaded using a location relative to the classpath.</td>
	 * </tr>
	 * <tr>
	 * <td>locator</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>A <code>EntityReferenceLocator</code> reference.</td>
	 * </tr>
	 * <tr>
	 * <td>application</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>An <code>Application</code> reference.</td>
	 * </tr>
	 * <tr>
	 * <td>formbuilder</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>A <code>FormBuilder</code> reference.</td>
	 * </tr>
	 * <tr>
	 * <td>treebuilder</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>A <code>TreeBuilder</code> reference.</td>
	 * </tr>
	 * <tr>
	 * <td>tree</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the path of tree xml description file.</td>
	 * </tr>
	 * <tr>
	 * <td>treeclass</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the class name that will be instanced for create a <code>Tree</code></td>
	 * </tr>
	 * <tr>
	 * <td>frame</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Established the application <code>Frame</code> reference.</td>
	 * </tr>
	 * <tr>
	 * <td>container</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Established container where this <code>FormManager</code> will be layered out in.</td>
	 * </tr>
	 * <tr>
	 * <td>form</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Established the initial <code>Form</code> that will be loaded at first.</td>
	 * </tr>
	 * <tr>
	 * <td>imanager</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the <code>InteractionManager</code> that will be registered to initial
	 * <code>Form</code></td>
	 * </tr>
	 * <tr>
	 * <td>imloader</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the <code>InteractionManagerLoader</code> that will be registered</td>
	 * </tr>
	 * <tr>
	 * <td>resources</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the <code>ResourceBundle</code> that will be used in this
	 * <code>FormManager</code></td>
	 * </tr>
	 * <tr>
	 * <td>dynamicform</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Established the <code>DynamicFormManager</code> class name that will be used.</td>
	 * </tr>
	 * <tr>
	 * <td>delayedload</td>
	 * <td>yes/no/background</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Established the {@link #delayedLoad} condition.</td>
	 * </tr>
	 * </Table>
	 */
	public BaseFormManager(final Map parameters) throws Exception {
		this.init(parameters);
	}

	public void init(final Map parameters) throws Exception {
		final Object id = parameters.get(IFormManager.ID);
		if (id != null) {
			final String aux = id.toString();
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				manager.checkPermission(new FMPermission(aux, IFormManager.LOAD, aux, true));
			}
			this.id = aux;
		} else {
			BaseFormManager.logger.debug(IFormManager.ID + " parameter cannot be found");
		}

		this.useClasspath = ParseUtils.getBoolean((String) parameters.get(IFormManager.USE_CLASS_PATH), false);

		final Object oLocator = parameters.get(IFormManager.LOCATOR);
		if ((oLocator != null) && (oLocator instanceof EntityReferenceLocator)) {
			this.setReferenceLocator((EntityReferenceLocator) oLocator);
		}

		final Object app = parameters.get(IFormManager.APPLICATION);
		if ((app != null) && (app instanceof Application)) {
			this.setApplication((Application) app);
		}

		final Object oFormsBuilder = parameters.get(IFormManager.FORM_BUILDER);
		if ((oFormsBuilder != null) && (oFormsBuilder instanceof FormBuilder)) {
			this.formBuilder = (FormBuilder) oFormsBuilder;
		} else {
			throw new IllegalArgumentException(
					"'" + IFormManager.FORM_BUILDER + "' doesn't implement FormBuilder or is NULL");
		}

		final Object oframe = parameters.get(IFormManager.FRAME);
		if (oframe == null) {
			throw new IllegalArgumentException("'" + IFormManager.FRAME + "' isn't a Frame or is NULL");
		}

		this.showFrame = ParseUtils.getBoolean((String) parameters.get(IFormManager.SHOW_FRAME), false);

		this.icon = ParseUtils.getString((String) parameters.get(IFormManager.ICON), null);

		final Object oContainer = parameters.get(IFormManager.CONTAINER);
		if ((oContainer != null) && (oContainer instanceof Container)) {
			this.parent = (Container) oContainer;
		} else {
			throw new IllegalArgumentException(" 'container' isn't a Container or is NULL");
		}

		this.configureForm(parameters);

		this.rules = ParseUtils.getString((String) parameters.get(IFormManager.RULES), this.rules);

		this.configureInteractionManager(parameters);

		// Resources
		this.resourceFileName = ParseUtils.getString((String) parameters.get(IFormManager.RESOURCES),
				this.resourceFileName);
		if (this.resourceFileName == null) {
			BaseFormManager.logger.debug("ResourceBundle is null");
		}

		this.configureDynamicForm(parameters);
		this.configureDetail(parameters);

		this.interactionManagerLoader = ParseUtils.getString((String) parameters.get(IFormManager.IM_LOADER),
				BaseFormManager.defaultInteractionManagerLoader);

		this.configureRuleLoader(parameters);

		// Creates rule engine. It is unique by form manager. since 5.2075EN
		this.ruleEngine = this.ruleEngineLoader.getRuleEngine(this);

		this.configureDelayedLoad(parameters);

	}

	protected void configureForm(final Map params) {
		final Object form = params.get(IFormManager.FORM);
		if ((form != null) && (form instanceof String)) {
			this.initialForm = form.toString();
			final int last = this.initialForm.lastIndexOf("/");
			if (last >= 0) {
				this.baseCP = this.initialForm.substring(0, last + 1);
			} else {
				this.baseCP = "";
			}
		} else {
			throw new IllegalArgumentException("'" + IFormManager.FORM + " isn't a String or is NULL");
		}
	}

	protected void configureInteractionManager(final Map parameters) {
		final Object gInt = parameters.get(IFormManager.INTERACTION_MANAGER);
		Object oInteractionManager = null;
		if (gInt != null) {
			try {
				final Class claseGI = Class.forName(gInt.toString());
				oInteractionManager = claseGI.newInstance();
				if (oInteractionManager instanceof InteractionManager) {
					this.initialInteractionManager = (InteractionManager) oInteractionManager;

					final Object oAction = parameters.get(IFormManager.ACTION);
					if ((oAction != null) && (oAction instanceof String)) {
						this.initialInteractionManager.loadActionHandler(oAction.toString());
					}
				} else {
					BaseFormManager.logger
					.debug("'" + IFormManager.INTERACTION_MANAGER + " class doesn't extends InteractionManager");
				}
			} catch (final Exception e) {
				BaseFormManager.logger
				.error("Error loading the class specified in '" + IFormManager.INTERACTION_MANAGER + "'", e);
			}
		} else {
			BaseFormManager.logger.debug("No InteractionManager received for the initial Form. ");
			oInteractionManager = null;
		}
	}

	protected void configureDynamicForm(final Map parameters) {
		final Object df = parameters.get(IFormManager.DYNAMIC_FORM);
		if (df != null) {
			try {
				final String dfClassName = df.toString();
				final Class dfClass = Class.forName(dfClassName);
				final DynamicFormManager fd = (DynamicFormManager) dfClass.newInstance();
				this.dynamicFormClass = dfClassName;
				this.formDynamicManager = fd;

				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger
					.debug(dfClassName + " class has been established as dynamic form for this node");
				}
			} catch (final Exception e) {
				BaseFormManager.logger.error("Error loading formdynamic class", e);
			}
		}
	}

	protected void configureDelayedLoad(final Map parameters) {
		final Object delayedload = parameters.get(IFormManager.DELAYED_LOAD);
		if ("yes".equalsIgnoreCase((String) delayedload) || "true".equalsIgnoreCase((String) delayedload)) {
			this.delayedLoad = true;
		} else if ("background".equalsIgnoreCase((String) delayedload)) {
			this.delayedLoad = true;
			BackgroundFormManagerBuilderManager.buildFormManager(this);
		} else {
			this.delayedLoad = false;
			this.load();
		}
	}

	protected void configureRuleLoader(final Map parameters) {
		// creating ruleloader. since 5.2075EN
		final Object ruleloader = parameters.get(IFormManager.RULE_LOADER);
		if ((ruleloader != null) && (ruleloader.toString().length() > 0)) {
			try {
				this.ruleEngineLoader = (RuleEngineLoader) Class.forName(ruleloader.toString()).newInstance();
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.error(null, e);
				} else {
					BaseFormManager.logger.trace(null, e);
				}
			}
		} else {
			this.ruleEngineLoader = new DefaultRuleEngineLoader();
		}
	}

	protected void configureDetail(final Map parameters) {
		final Object detail = parameters.get(IFormManager.DETAIL);
		if ((detail != null) && (detail instanceof Map)) {
			final Map currentDetail = (Map) detail;
			if (currentDetail.containsKey(XMLApplicationBuilder.INTERACTION_MANAGER)) {
				Map current = (Map) currentDetail.get(XMLApplicationBuilder.INTERACTION_MANAGER);
				if (!current.isEmpty()) {
					if (this.formInteractionManagerClassNameList == null) {
						this.formInteractionManagerClassNameList = new Hashtable();
					}
					this.formInteractionManagerClassNameList.putAll(current);
				}

				current = (Map) currentDetail.get(XMLApplicationBuilder.FMANAGER);
				if (!current.isEmpty()) {
					if (this.formManagerForms == null) {
						this.formManagerForms = new Hashtable();
					}
					this.formManagerForms.putAll(current);
				}

				// Load the actions.
				final Object oImAction = currentDetail.get(XMLApplicationBuilder.INTERACTION_MANAGER_ACTION);
				if ((oImAction != null) && (oImAction instanceof Map)) {
					final Map imAction = (Map) oImAction;

					if (this.formInteractionManagerActionList == null) {
						this.formInteractionManagerActionList = new Hashtable();
					}
					this.formInteractionManagerActionList.putAll(imAction);
				}

				// Load the rules
				final Object oRules = currentDetail.get(XMLApplicationBuilder.RULES);
				if ((oRules != null) && (oRules instanceof Map)) {
					final Map mRules = (Map) oRules;

					if (this.formRules == null) {
						this.formRules = new Hashtable();
					}
					this.formRules.putAll(mRules);
				}
			}
		}
	}

	/**
	 * Loads the <code>FormManager</code>.The initial <code>Form</code>, initial
	 * <code>InteractionManager</code> and <code>Tree</code> are instanced and are layered out in
	 * container to be shown. All this operations are performed in <code>EventDispatchThread</code>.
	 */

	@Override
	public void load() {
		if (SwingUtilities.isEventDispatchThread()) {
			this.loadInEDTh();
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						BaseFormManager.this.loadInEDTh();
					}
				});
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Loads the <code>FormManager</code>. This method must be called from
	 * <code>EventDispatchThread</code>.
	 *
	 * @see #load
	 */
	@Override
	public synchronized void loadInEDTh() {
		try {
			if (!this.isLoaded()) {
				if (this.formInteractionManagerClassNameList != null) {
					final Enumeration enumKeys = Collections
							.enumeration(this.formInteractionManagerClassNameList.keySet());
					while (enumKeys.hasMoreElements()) {
						final String form = (String) enumKeys.nextElement();
						final String imClassName = this.formInteractionManagerClassNameList.get(form);
						this.applyInteractionManager(imClassName, form);
					}
				}

				this.loadedForm = true;
				if (this.setResourceBundleOnLoad) {
					this.setResourceBundle_internal();
				}
				this.createLayout(this.applicationFrame, this.parent, this.formBuilder, this.initialForm,
						this.initialInteractionManager, this.resourceFile, false);
			} else {
				BaseFormManager.logger.debug("FormManager has already done the initial load");
			}
		} catch (final Exception ex) {
			BaseFormManager.logger.error(null, ex);
			throw new RuntimeException(ex.getMessage(), ex);
		}

	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param northPanel true if new components can be added in <code>FormManager</code> using the
	 *        {@link #addTopComponent} method.
	 */

	public BaseFormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final ResourceBundle resource, final boolean northPanel) {
		this.loadedForm = true;
		this.createLayout(frame, container, builder, formURI, null, resource, northPanel);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param interactionManager <code>InteractionManager</code> reference.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param northPanel true if new components can be added in <code>FormManager</code> using the
	 *        {@link #addTopComponent} method.
	 */

	public BaseFormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final InteractionManager interactionManager, final ResourceBundle resource,
			final boolean northPanel) {
		this.createLayout(frame, container, builder, formURI, interactionManager, resource, northPanel);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param resource ResourceBundle where is the language resources in.
	 */
	public BaseFormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final ResourceBundle resource) {
		this.createLayout(frame, container, builder, formURI, null, resource, false);
	}

	protected abstract JComponent createCenterPanel();

	protected void createLayout(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final InteractionManager interactionManager, final ResourceBundle resource,
			final boolean northPanel) {
		if (ApplicationManager.getApplication() != null) {
			this.locale = ApplicationManager.getApplication().getLocale();
		}
		try {
			if (this.resourceFileName != null) {
				this.resourceFile = ExtendedPropertiesBundle.getExtendedBundle(this.resourceFileName, this.locale);
			}
		} catch (final Exception e) {
			BaseFormManager.logger.error("Error loading resource file.", e);
		}

		this.formBuilder = builder;
		this.setLayout(new BorderLayout());
		// Control panel
		if (northPanel) {
			this.add(this.panelTop, BorderLayout.NORTH);
		}
		this.add(this.createCenterPanel(), BorderLayout.CENTER);
		container.setLayout(new BorderLayout());
		try {
			if (container instanceof JFrame) {
				((JFrame) container).getContentPane().setLayout(new BorderLayout());
				((JFrame) container).getContentPane().add(this);
			} else {
				if (container instanceof JDialog) {
					((JDialog) container).getContentPane().setLayout(new BorderLayout());
					((JDialog) container).getContentPane().add(this);
				} else {
					container.add(this);
				}
			}
		} catch (final Exception e) {
			BaseFormManager.logger.error("FormManager cannot be added to container: ", e);
			return;
		}
		this.parent = container;
		this.applicationFrame = frame;
		this.resourceFile = resource;
		// Looks for the last '\' separator in path
		int index = -1;
		for (int i = 0; i < formURI.length(); i++) {
			if ((formURI.charAt(i) == '/') || (formURI.charAt(i) == '\\')) {
				index = i;
			}
		}
		// XML files will be placed in this location.
		this.uRIBase = formURI.substring(0, index + 1);
		final String sFormName = formURI.substring(index + 1, formURI.length());
		if (interactionManager != null) {
			this.interactionManagerList.put(sFormName, interactionManager);
		}
		if (this.rules != null) {
			if (this.formRules == null) {
				this.formRules = new Hashtable();
			}
			final String sRules = this.rules.replaceAll(this.uRIBase, "");
			this.formRules.put(sFormName, sRules);
		}
		if (this.formDynamicManager != null) {
			this.formDynamicManager.setFormManager(this);
			this.formDynamicManager.setBaseName(sFormName);
		}
		this.showFormInEDTh(sFormName);
	}

	/**
	 * Loads the form list from <code>Vector</code> instance that are passed as entry parameter. For
	 * each form from <code>Vector</code>, this method will call the {@link IFormManager#loadFormInEDTh}
	 * method.
	 * @param formList List of Form to be loaded
	 */

	public void loadForms(final List<String> formList) {
		if (!this.isLoaded()) {
			this.load();
		}
		// At first, checks if form has already loaded

		if (formList == null) {
			return;
		}
		for (int i = 0; i < formList.size(); i++) {
			if (!this.loadedList.contains(formList.get(i))) {
				// It has not been loaded yet
				this.loadForm(formList.get(i).toString());
			}
		}
	}

	protected void configureRules(final String formFileName) {
		String sformrules = null;
		if (this.locator instanceof XMLClientProvider) {
			final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formFileName;
			try {
				sformrules = ((XMLClientProvider) this.locator).getXMLRules(xmlProviderFormName,
						this.locator.getSessionId());
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("Error loading form from server ", e);
				} else {
					BaseFormManager.logger.trace("Error loading form from server ", e);
				}
			}
		}
		if (sformrules == null) {
			// check whether rules are defined in client files ->
			// rules='xxx.orl'
			if (this.formRules.containsKey(formFileName)) {
				// Loads the rules of specified file
				final String ruleFileName = this.formRules.get(formFileName).toString();
				String sFileURI = this.uRIBase + ruleFileName;
				// If you use classpath, then we need to obtain the url
				if (this.useClasspath) {
					final URL url = this.getClass().getClassLoader().getResource(this.baseCP + ruleFileName);
					if (url == null) {
						throw new RuntimeException(this.getId() + " -> Rules not found: " + sFileURI);
					}
					sFileURI = url.toString();
				}
				sformrules = XMLUtil.dom2String(sFileURI);
			}
		}
		if (sformrules != null) {
			final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formFileName;
			final IRules rules = RuleParser.parseRules(sformrules);
			this.ruleEngine.setRules(formFileName, rules);
		}
	}

	@Override
	public boolean showForm(final String formName) {
		final List res = new Vector();
		if (SwingUtilities.isEventDispatchThread()) {
			return this.showFormInEDTh(formName);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						final boolean bIn = BaseFormManager.this.showFormInEDTh(formName);
						if (bIn) {
							res.add(Boolean.TRUE);
						}
					}
				});
				return res.isEmpty() ? false : true;
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	protected void loadForm(final String fileName) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.loadFormInEDTh(fileName);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						BaseFormManager.this.loadFormInEDTh(fileName);
					}
				});
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	@Override
	public abstract void addFormToContainer(JPanel panelForm, String formName);

	/**
	 * Loads the Form which file name is passed as entry parameter. If the Form has just been loaded
	 * before this method does nothing. <br>
	 * First, this method checks whether a form reference exists remotely using the
	 * {@link XMLFormProvider}. If this remote reference doesn't exist
	 * @param formFileName
	 */
	protected void loadFormInEDTh(final String formFileName) {
		final long initTime = System.currentTimeMillis();

		if (this.formManagerForms.containsKey(formFileName)) {
			final String formManagerIdentifier = (String) this.formManagerForms.get(formFileName);
			final IFormManager fmFormManager = this.application.getFormManager(formManagerIdentifier);
			if (fmFormManager == null) {
				BaseFormManager.logger.warn("FormManager cannot be found {}" + formManagerIdentifier);
				return;
			}
			final JPanel jpPanelForm = new JPanel();
			jpPanelForm.setLayout(new CustomColumnGridBagLayout(this.getContainer()));
			final Form f = fmFormManager.getFormCopy(formFileName, jpPanelForm);

			this.loadedList.add(formFileName);
			this.formReferenceList.put(formFileName, (Form) jpPanelForm.getComponent(0));
			this.addFormToContainer(jpPanelForm, formFileName);
			// It is loaded
			return;
		}

		// Checks the loaded form list
		if (!this.loadedList.contains(formFileName)) {
			String formxml = null;
			if (this.locator instanceof XMLFormProvider) {
				final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formFileName;
				try {
					formxml = ((XMLFormProvider) this.locator).getXMLForm(xmlProviderFormName,
							this.locator.getSessionId());
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						BaseFormManager.logger.debug("Error loading form from server ", e);
					} else {
						BaseFormManager.logger.trace("Error loading form from server ", e);
					}
				}
			}
			String sFileURI = this.uRIBase + formFileName;
			// When classpath is used, we need to get the url
			if (this.useClasspath && (formxml == null)) {
				final URL url = this.getClass().getClassLoader().getResource(this.baseCP + formFileName);
				if (url == null) {
					throw new RuntimeException(this.getId() + " -> Form not found: " + sFileURI);
				}
				sFileURI = url.toString();
			}

			// We load the form to specified file.
			final JPanel jpPanelForm = new JPanel();
			jpPanelForm.setLayout(new CustomColumnGridBagLayout(jpPanelForm));

			if (this.formBuilder instanceof XMLFormBuilder) {
				((XMLFormBuilder) this.formBuilder).setBaseClasspath(this.baseCP);
			}

			if ((formxml != null) && (this.formBuilder instanceof XMLFormBuilder)) {
				try {
					((XMLFormBuilder) this.formBuilder).buildForm(jpPanelForm, new StringBuffer(formxml));
				} catch (final Exception ex) {
					BaseFormManager.logger.error(null, ex);
				}
			} else {
				this.formBuilder.buildForm(jpPanelForm, sFileURI);
			}

			this.configureRules(formFileName);

			// The child of a form panel should be a form.
			final Form theForm = (Form) jpPanelForm.getComponent(0);

			this.formReferenceList.put(formFileName, theForm);
			this.addFormToContainer(jpPanelForm, formFileName);
			this.loadedList.add(formFileName);

			try {
				theForm.setFormManager(this);
				if (this.formDynamicManager != null) {
					theForm.addDataNavigationListener(this);
					theForm.setDynamicFormManager(this.formDynamicManager);
					this.formRegisteredDataNavigationListener.add(formFileName);
				}
				theForm.setParentFrame(this.applicationFrame);
				theForm.setFormBuilder(this.formBuilder);
				theForm.setFileName(formFileName);
				theForm.createLists();
				theForm.setComponentLocale(this.locale);
				theForm.setResourceBundle(this.resourceFile);


				// Checks the interaction manager list
				if (this.interactionManagerList.containsKey(formFileName)) {
					// It is assigned
					theForm.setInteractionManager(this.interactionManagerList.get(formFileName));
					this.interactionManagerList.get(formFileName)
					.registerInteractionManager(this.getFormReference(formFileName), this);
					this.interactionManagerList.get(formFileName).setInitialState();

					// It is added to another list to make easier free
					// resources.
					this.interactionManagers.add(this.interactionManagerList.get(formFileName));
				} else {
					// if the interaction manager is not in the list, we
					// establish
					// it
					// By default, the interaction manager is established
					InteractionManager defaultInteractionManager = null;
					if (this.interactionManagerLoader != null) {
						if (this.iManagerLoader == null) {
							try {
								final Class interactionManagerLoaderClass = Class.forName(this.interactionManagerLoader);
								this.iManagerLoader = (InteractionManagerLoader) interactionManagerLoaderClass
										.newInstance();
							} catch (final Exception e) {
								BaseFormManager.logger.error(null, e);
							}
						}
						if (this.iManagerLoader != null) {
							try {
								defaultInteractionManager = this.iManagerLoader.getInteractionManager(formFileName);
							} catch (final Exception e) {
								BaseFormManager.logger.trace(null, e);
							}
						}

					}
					if (defaultInteractionManager == null) {
						defaultInteractionManager = new BasicInteractionManager(true);
					}

					theForm.setInteractionManager(defaultInteractionManager);
					defaultInteractionManager.registerInteractionManager((Form) jpPanelForm.getComponent(0), this);

					// It is added to another list to make easier free
					// resources.
					this.interactionManagers.add(defaultInteractionManager);
					defaultInteractionManager.setInitialState();
				}
				if (this.aPreferences != null) {
					theForm.registerApplicationPreferencesListener();
					String user = null;
					if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
						user = ((ClientReferenceLocator) this.locator).getUser();
					}
					theForm.initPreferences(this.aPreferences, user);
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("FormManager: Error initiating form.", e);
				} else {
					BaseFormManager.logger.trace("FormManager: Error initiating form. ", e);
				}
			}
		} else {
			// It has already loaded
		}
		final long finalTime = System.currentTimeMillis();
		if (com.ontimize.gui.ApplicationManager.DEBUG_TIMES) {
			BaseFormManager.logger
			.debug("FormManager: Form load time: " + formFileName + " -> " + (finalTime - initTime));
		}
	}

	/**
	 * Gets the form reference which name is passed as entry parameter. This method call the
	 * {@link #load} if is necessary and the loadIfNecessary parameter is true.
	 * @param formName a Form name.
	 * @param loadIfNecessary true to be loaded the <code>FormManager</code>
	 */
	@Override
	public Form getFormReference(final String formName, final boolean loadIfNecessary) {
		if (!this.isLoaded() && loadIfNecessary) {
			this.load();
		}
		final Object oFormReference = this.formReferenceList.get(formName);
		if (oFormReference == null) {
			if (loadIfNecessary) {
				this.loadForm(formName);
				return this.formReferenceList.get(formName);
			} else {
				return null;
			}
		} else {
			return (Form) oFormReference;
		}
	}

	/**
	 * Gets a <code>Form</code> reference which name is passed as entry parameter.
	 * @return a <code>Form</code> reference; null if the Form hasn't been loaded.
	 */

	@Override
	public Form getFormReference(final String form) {
		final Object oFormReference = this.formReferenceList.get(form);
		if (oFormReference == null) {
			return null;
		} else {
			return (Form) oFormReference;
		}
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter.
	 * @param formName a <code>String</code> with the Form name.
	 */

	@Override
	public Form getFormCopy(final String formName) {
		return this.getFormCopy(formName, (Container) null);
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. The
	 * <code>Form</code> reference that will be returned doesn't have registered any
	 * <code>InteractionManager</code>
	 * @param formName a <code>String</code> with the <code>Form</code> name.
	 * @return a <code>Form</code> reference
	 */

	public Form getFormCopyWithoutInteractionManager(final String formName) {
		return this.getFormCopyWithoutInteractionManager(formName, null);
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. This
	 * <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned doesn't have registered any <code>InteractionManager</code>. This
	 * method call the {@link #getFormCopyWithoutInteractionManagerInEDTh} method from
	 * EventDispatchThread.
	 * @param formName a <code>String</code> with the <code>Form</code> name.
	 * @param container a <code>Container</code> where the <code>Form</code> will be displayed in.
	 * @return a <code>Form</code> reference.
	 */

	public Form getFormCopyWithoutInteractionManager(final String formName, final Container container) {
		if (SwingUtilities.isEventDispatchThread()) {
			return this.getFormCopyWithoutInteractionManagerInEDTh(formName, container);
		} else {
			try {
				final ObjectWrapper<Form> wrapper = new ObjectWrapper<Form>();
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						wrapper.setValue(
								BaseFormManager.this.getFormCopyWithoutInteractionManagerInEDTh(formName, container));
					}
				});
				return wrapper.getValue();
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. This
	 * <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned doesn't have registered any <code>InteractionManager</code>.
	 * @param formName a <code>String</code> with the <code>Form</code> name.
	 * @param container a <code>Container</code> where the <code>Form</code> will be displayed in.
	 * @return a <code>Form</code> reference.
	 */

	public Form getFormCopyWithoutInteractionManagerInEDTh(final String formName, final Container container) {
		if (!this.isLoaded()) {
			this.load();
		}

		String formxml = null;
		if (this.locator instanceof XMLFormProvider) {
			final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formName;
			try {
				formxml = ((XMLFormProvider) this.locator).getXMLForm(xmlProviderFormName, this.locator.getSessionId());
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("Error loading form from server ", e);
				} else {
					BaseFormManager.logger.trace("Error loading form from server ", e);
				}
			}
		}
		// Loads form from file
		String sFileURI = this.uRIBase + formName;
		// If classpath parameter is used, we have to obtain the url
		if (this.useClasspath && (formxml == null)) {
			final URL url = this.getClass().getClassLoader().getResource(this.baseCP + formName);
			if (url == null) {
				throw new RuntimeException(this.getId() + "-> Form not found: " + sFileURI);
			}
			sFileURI = url.toString();
		}
		Container formPanel = new Container();
		if (container != null) {
			formPanel = container;
		}
		formPanel.setLayout(new CustomColumnGridBagLayout(formPanel));

		if (this.formBuilder instanceof XMLFormBuilder) {
			((XMLFormBuilder) this.formBuilder).setBaseClasspath(this.baseCP);
		}

		if ((formxml != null) && (this.formBuilder instanceof XMLFormBuilder)) {
			try {
				((XMLFormBuilder) this.formBuilder).buildForm(formPanel, new StringBuffer(formxml));
			} catch (final Exception ex) {
				BaseFormManager.logger.error(null, ex);
			}
		} else {
			this.formBuilder.buildForm(formPanel, sFileURI);
		}

		this.configureRules(formName);

		// The child of a form panel should be a form
		final Form theForm = (Form) formPanel.getComponent(0);
		// Loads the class of associated entity and creates the object
		try {
			theForm.setFormManager(this);
			if (this.formDynamicManager != null) {
				theForm.addDataNavigationListener(this);
				theForm.setDynamicFormManager(this.formDynamicManager);
				this.formRegisteredDataNavigationListener.add(formName);
			}
			theForm.setParentFrame(this.applicationFrame);
			theForm.setResourceBundle(this.resourceFile);
			theForm.setFileName(formName);
			theForm.createLists();
			theForm.setResourceBundle(this.resourceFile);
			theForm.setComponentLocale(this.locale);

			if (this.aPreferences != null) {
				theForm.registerApplicationPreferencesListener();
				String user = null;
				if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}
				theForm.initPreferences(this.aPreferences, user);
			}
		} catch (final Exception e) {
			BaseFormManager.logger.debug("Error in Form: {}", formName, e);
		}

		return theForm;
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. This
	 * <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned have registered the <code>InteractionManager</code> that has been
	 * defined in application xml description. This method calls the {@link #getFormCopyInEDTh} method
	 * from EventDispatchThread.
	 * @param formName a <code>String</code> with the <code>Form</code> name.
	 * @param container a <code>Container</code> where the <code>Form</code> will be displayed in.
	 * @return a <code>Form</code> reference.
	 */

	@Override
	public Form getFormCopy(final String formName, final Container container) {
		if (SwingUtilities.isEventDispatchThread()) {
			return this.getFormCopyInEDTh(formName, container);
		} else {
			try {
				final ObjectWrapper<Form> wrapper = new ObjectWrapper<Form>();
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						wrapper.setValue(BaseFormManager.this.getFormCopyInEDTh(formName, container));
					}
				});
				return wrapper.getValue();
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Gets a <code>Form</code> instance copy which form resource name is passed as entry parameter.
	 * This <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned have registered the <code>InteractionManager</code> that has been
	 * passed as entry parameter. This method calls the {@link #getFormCopyInEDTh} method from
	 * EventDispatchThread.
	 * @param formResourceName a <code>String</code> with the <code>Form</code> name.
	 * @param imQualifiedName a <code>String</code> with the interaction manager qualified class name.
	 * @return a <code>Form</code> reference.
	 */

	@Override
	public Form getFormCopy(final String formResourceName, final String imQualifiedName) {
		if (SwingUtilities.isEventDispatchThread()) {
			return this.getFormCopyInEDTh(formResourceName, imQualifiedName);
		} else {
			try {
				final ObjectWrapper<Form> wrapper = new ObjectWrapper<Form>();
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						wrapper.setValue(BaseFormManager.this.getFormCopyInEDTh(formResourceName, imQualifiedName));
					}
				});
				return wrapper.getValue();
			} catch (final Exception ex) {
				throw new RuntimeException(ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Gets the Form URL which name is passed as entry parameter. The base classpath is retrieved from
	 * {@link #baseCP} variable.
	 * @param formName a <code>String<code> with the <code>Form</code> file name.
	 * @return a <code>URL</code> reference
	 */
	@Override
	public URL getURL(final String formName) {
		final URL url = this.getClass().getClassLoader().getResource(this.baseCP + formName);
		return url;
	}

	/**
	 * Gets a <code>Form</code> instance copy which form resource name is passed as entry parameter.
	 * This <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned have registered the <code>InteractionManager</code> that has been
	 * passed as entry parameter. This method calls the {@link #getFormCopyInEDTh} method from
	 * EventDispatchThread.
	 * @param formResourceName a <code>String</code> with the <code>Form</code> name.
	 * @param imQualifiedName a <code>String</code> with the interaction manager qualified class name.
	 * @return a <code>Form</code> reference.
	 */

	protected Form getFormCopyInEDTh(final String formResourceName, final String imQualifiedName) {
		if (!this.isLoaded()) {
			this.load();
		}

		String formxml = null;
		if (this.locator instanceof XMLFormProvider) {
			final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formResourceName;
			try {
				formxml = ((XMLFormProvider) this.locator).getXMLForm(xmlProviderFormName, this.locator.getSessionId());
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("Error loading server form ", e);
				} else {
					BaseFormManager.logger.trace("Error loading server form ", e);
				}
			}
		}
		InputStream input = null;
		if (formxml == null) {
			try {
				input = this.getClass().getClassLoader().getResourceAsStream(formResourceName);
			} catch (final Exception e) {
				throw new RuntimeException(this.getId() + " -> Form not found: " + formResourceName, e);
			}
		}

		final Container formPanel = new Container();

		formPanel.setLayout(new CustomColumnGridBagLayout(formPanel));

		if (this.formBuilder instanceof XMLFormBuilder) {
			((XMLFormBuilder) this.formBuilder).setBaseClasspath(this.baseCP);
		}

		if ((formxml != null) && (this.formBuilder instanceof XMLFormBuilder)) {
			try {
				((XMLFormBuilder) this.formBuilder).buildForm(formPanel, new StringBuffer(formxml));
			} catch (final Exception ex) {
				BaseFormManager.logger.error(null, ex);
			}
		} else {
			this.formBuilder.buildForm(formPanel, input);
		}
		this.configureRules(formResourceName);

		// The child of a form panel should be a form
		if (formPanel.getComponent(0) instanceof Form) {
			// Loads the associated entity
			try {
				((Form) formPanel.getComponent(0)).setFormManager(this);
				((Form) formPanel.getComponent(0)).setParentFrame(this.applicationFrame);
				((Form) formPanel.getComponent(0)).setFormBuilder(this.formBuilder);
				((Form) formPanel.getComponent(0)).setFileName(formResourceName);
				((Form) formPanel.getComponent(0)).createLists();
				((Form) formPanel.getComponent(0)).setResourceBundle(this.resourceFile);
				((Form) formPanel.getComponent(0)).setComponentLocale(this.locale);

				if (this.aPreferences != null) {
					((Form) formPanel.getComponent(0)).registerApplicationPreferencesListener();
					String user = null;
					if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
						user = ((ClientReferenceLocator) this.locator).getUser();
					}
					((Form) formPanel.getComponent(0)).initPreferences(this.aPreferences, user);
				}

				// If it is not in the list, establishes the default interaction
				// manager
				// By default, establishes the interaction manager of detail
				// form
				InteractionManager defaultInteractionManager = null;
				if (defaultInteractionManager == null) {
					try {
						final Class currentInteractionManagerClass = Class.forName(imQualifiedName);
						defaultInteractionManager = (InteractionManager) currentInteractionManagerClass.newInstance();
					} catch (final Exception ex) {
						BaseFormManager.logger.error(null, ex);
					}
				}

				if (defaultInteractionManager == null) {
					if (this.interactionManagerLoader != null) {
						if (this.iManagerLoader == null) {
							try {
								final Class interactionManagerLoaderClass = Class.forName(this.interactionManagerLoader);
								this.iManagerLoader = (InteractionManagerLoader) interactionManagerLoaderClass
										.newInstance();
								defaultInteractionManager = this.iManagerLoader.getInteractionManager(formResourceName);
							} catch (final Exception e) {
								BaseFormManager.logger.error(null, e);
							}
						}
						if (this.iManagerLoader != null) {
							try {
								defaultInteractionManager = this.iManagerLoader.getInteractionManager(formResourceName);

							} catch (final Exception e) {
								BaseFormManager.logger.trace(null, e);
							}
						}

					}
				}

				((Form) formPanel.getComponent(0)).setInteractionManager(defaultInteractionManager);
				defaultInteractionManager.registerInteractionManager((Form) formPanel.getComponent(0), this);
				// Changes form to update mode
				defaultInteractionManager.setInitialState();
				// Adds to list to free
				this.interactionManagers.add(defaultInteractionManager);

			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("Form error:", e);
				} else {
					BaseFormManager.logger.trace("Form error:", e);
				}
			}
		}

		final Form form = (Form) formPanel.getComponent(0);
		return form;
	}

	@Override
	public Form getFormCopyInEDTh(final String formName) {
		return this.getFormCopyInEDTh(formName, (Container) null);
	}

	/**
	 * Gets a <code>Form</code> instance copy which form name is passed as entry parameter. This
	 * <code>Form</code> copy is layered out into the container parameter. The <code>Form</code>
	 * reference that will be returned have registered the <code>InteractionManager</code> that has been
	 * defined in application xml description.
	 * @param formName a <code>String</code> with the <code>Form</code> name.
	 * @param container a <code>Container</code> where the <code>Form</code> will be displayed in.
	 * @return a <code>Form</code> reference.
	 */

	protected Form getFormCopyInEDTh(final String formName, final Container container) {
		if (!this.isLoaded()) {
			this.load();
		}

		if (this.formManagerForms.containsKey(formName)) {
			final String formManagerIdentifier = (String) this.formManagerForms.get(formName);
			final IFormManager formManager = this.application.getFormManager(formManagerIdentifier);
			if (formManager == null) {
				BaseFormManager.logger.debug("FormManager cannot be found " + formManagerIdentifier);
				return null;
			}
			final Form f = ((BaseFormManager) formManager).getFormCopyInEDTh(formName, container);
			return f;
		}

		String formxml = null;
		if (this.locator instanceof XMLFormProvider) {
			final String xmlProviderFormName = (this.getId() != null ? this.getId() : "") + "/" + formName;
			try {
				formxml = ((XMLFormProvider) this.locator).getXMLForm(xmlProviderFormName, this.locator.getSessionId());
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("Error loading server form ", e);
				} else {
					BaseFormManager.logger.trace("Error loading server form ", e);
				}
			}
		}
		// Loads the form of specified file
		String sFileURI = this.uRIBase + formName;
		// If you use classpath, then we need to obtain the url
		if (this.useClasspath && (formxml == null)) {
			final URL url = this.getClass().getClassLoader().getResource(this.baseCP + formName);
			if (url == null) {
				throw new RuntimeException(this.getId() + " -> Form not found: " + sFileURI);
			}
			sFileURI = url.toString();
		}
		Container formPanel = new Container();
		if (container != null) {
			formPanel = container;
		}
		formPanel.setLayout(new CustomColumnGridBagLayout(formPanel));

		if (this.formBuilder instanceof XMLFormBuilder) {
			((XMLFormBuilder) this.formBuilder).setBaseClasspath(this.baseCP);
		}

		if ((formxml != null) && (this.formBuilder instanceof XMLFormBuilder)) {
			try {
				((XMLFormBuilder) this.formBuilder).buildForm(formPanel, new StringBuffer(formxml));
			} catch (final Exception ex) {
				BaseFormManager.logger.error(null, ex);
			}
		} else {
			this.formBuilder.buildForm(formPanel, sFileURI);
		}
		this.configureRules(formName);

		// The child of a form panel should be a form
		final Form theForm = (Form) formPanel.getComponent(0);
		// Loads the associated entity
		try {
			theForm.setFormManager(this);
			if (this.formDynamicManager != null) {
				theForm.addDataNavigationListener(this);
				theForm.setDynamicFormManager(this.formDynamicManager);
				this.formRegisteredDataNavigationListener.add(formName);
			}
			theForm.setParentFrame(this.applicationFrame);
			theForm.setFormBuilder(this.formBuilder);
			theForm.setFileName(formName);
			theForm.createLists();
			theForm.setResourceBundle(this.resourceFile);
			theForm.setComponentLocale(this.locale);

			if (this.aPreferences != null) {
				theForm.registerApplicationPreferencesListener();
				String user = null;
				if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}
				theForm.initPreferences(this.aPreferences, user);
			}

			// Checks the manager list
			if (this.interactionManagerList.containsKey(formName)) {
				// Assigned it
				final InteractionManager interactionManager = this.interactionManagerList.get(formName)
						.cloneInteractionManager();
				if (interactionManager != null) {
					theForm.setInteractionManager(interactionManager);
					interactionManager.registerInteractionManager(theForm, this);

					interactionManager.setInitialState();
				} else {
					BaseFormManager.logger.debug(
							"The interaction manager of the form {} does not implement properly the clone() method.",
							formName);
				}
			} else {
				// If it is not in the list, establishes the default
				// interaction
				// manager
				// By default, establishes the interaction manager of detail
				// form
				InteractionManager defaultInteractionManager = new TableBasicInteractionManager(true);
				if (this.interactionManagerLoader != null) {
					if (this.iManagerLoader == null) {
						try {
							final Class interactionManagerLoaderClass = Class.forName(this.interactionManagerLoader);
							this.iManagerLoader = (InteractionManagerLoader) interactionManagerLoaderClass
									.newInstance();
							defaultInteractionManager = this.iManagerLoader.getInteractionManager(formName);
						} catch (final Exception e) {
							BaseFormManager.logger.error(null, e);
						}
					}
					if (this.iManagerLoader != null) {
						try {
							defaultInteractionManager = this.iManagerLoader.getInteractionManager(formName);
						} catch (final Exception e) {
							BaseFormManager.logger.trace(null, e);
						}
					}

				}

				if (defaultInteractionManager == null) {
					defaultInteractionManager = new TableBasicInteractionManager(true);
				}

				theForm.setInteractionManager(defaultInteractionManager);
				defaultInteractionManager.registerInteractionManager(theForm, this);
				// Changes form to update mode
				defaultInteractionManager.setInitialState();
				// Adds to list to free
				this.interactionManagers.add(defaultInteractionManager);
			}
		} catch (final Exception e) {
			BaseFormManager.logger.error("Form error: ", e);
		}
		return theForm;
	}

	/**
	 * Gets the <code>EntityReferenceLocator</code> reference that is stored inside.
	 * @return a <code>EntityReferenceLocator</code>
	 */

	@Override
	public EntityReferenceLocator getReferenceLocator() {
		return this.locator;
	}

	/**
	 * Sets the <code>EntityReferenceLocator</code> reference used in this <code>FormManager</code>
	 * @param locator
	 */

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
		// For each form, it updates their reference components.
		final Enumeration enumKeys = Collections.enumeration(this.formReferenceList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oForm = enumKeys.nextElement();
			final Form fForm = this.formReferenceList.get(oForm);
			fForm.updateReferencesLocator();
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE);
		v.add(BaseFormManager.M_MODIFIED_DATA);
		for (int i = 0; i < this.loadedList.size(); i++) {
			v.addAll(this.formReferenceList.get(this.loadedList.get(i)).getTextsToTranslate());
		}
		return v;
	}

	/**
	 * Stores in the file which name is passed as entry parameters all texts in the
	 * <code>FormManager</code> that must be translated.
	 * @param file the destiny file
	 * @param append boolean if <code>true</code>, then the translate text will be will be written to
	 *        the end of the file rather than the beginning.
	 * @param keys List with the keys that has just been added.
	 * @param importProp
	 */

	public void saveTextsToTranslate(final String file, final boolean append, final List keys, final Properties importProp) {
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			fw = new FileWriter(file, append);
			bw = new BufferedWriter(fw);
			bw.write("#####################     FormManager  '" + this.id + "' ######################");
			bw.newLine();
			if (!keys.contains(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE)) {
				bw.write(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE + "=");
				if ((importProp != null) && importProp
						.containsKey(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE)) {
					bw.write(importProp
							.getProperty(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE));
				}
				bw.newLine();
				keys.add(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE);
			}
			if (!keys.contains(BaseFormManager.M_MODIFIED_DATA)) {
				bw.write(BaseFormManager.M_MODIFIED_DATA + "=");
				if ((importProp != null) && importProp.containsKey(BaseFormManager.M_MODIFIED_DATA)) {
					bw.write(importProp.getProperty(BaseFormManager.M_MODIFIED_DATA));
				}
				bw.newLine();
				keys.add(BaseFormManager.M_MODIFIED_DATA);
			}
			for (int i = 0; i < this.loadedList.size(); i++) {
				final Form form = this.formReferenceList.get(this.loadedList.get(i));
				bw.write("###################    Form " + form.getArchiveName() + "  #################");
				bw.newLine();
				final List vForm = form.getTextsToTranslate();
				try {
					Collections.sort(vForm);
				} catch (final Exception e) {
					BaseFormManager.logger.error(null, e);
				}
				for (int j = 0; j < vForm.size(); j++) {
					if (!keys.contains(vForm.get(j))) {
						keys.add(vForm.get(j));
						bw.write(vForm.get(j) + "=");
						if ((importProp != null) && importProp.containsKey(vForm.get(j))) {
							bw.write(importProp.getProperty(vForm.get(j).toString()));
						}
						bw.newLine();
					}
				}
			}

			bw.flush();
			bw.close();
			fw.close();
		} catch (final Exception e) {
			BaseFormManager.logger.error(null, e);
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (fw != null) {
					fw.close();
				}
			} catch (final Exception e) {
				BaseFormManager.logger.trace(null, e);
			}
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		// If custom resource file is defined, it will be used and passed
		// parameter will be obviated
		if (this.resourceFileName == null) {
			this.resourceFile = resources;
			// For all forms, we establish resource file.
			for (int i = 0; i < this.loadedList.size(); i++) {
				this.formReferenceList.get(this.loadedList.get(i)).setResourceBundle(resources);
			}
		} else {
			if (resources != null) {
				if (this.delayedLoad && !this.isLoaded()) {
					this.setResourceBundleOnLoad = true;
					return;
				}
				try {
					final Locale lAux = this.locale;
					this.resourceFile = ExtendedPropertiesBundle.getExtendedBundle(this.resourceFileName, lAux);
					// For all forms, we establish resource file.
					for (int i = 0; i < this.loadedList.size(); i++) {
						this.formReferenceList.get(this.loadedList.get(i)).setResourceBundle(this.resourceFile);
					}
				} catch (final Exception e) {
					BaseFormManager.logger.debug(null, e);
				}
			}

		}
	}

	protected void setResourceBundle_internal() {
		if (this.resourceFileName == null) {
			return;
		}
		try {
			final Locale lAux = this.locale;

			this.resourceFile = ExtendedPropertiesBundle.getExtendedBundle(this.resourceFileName, lAux);
			// For all forms, we establish resource file.
			for (int i = 0; i < this.loadedList.size(); i++) {
				this.formReferenceList.get(this.loadedList.get(i)).setResourceBundle(this.resourceFile);
			}
		} catch (final Exception e) {
			BaseFormManager.logger.debug(null, e);
		}
	}

	@Override
	public void showCurrencyValue(final String currencySymbol) {
		// For all forms, we establish resource file.
		for (int i = 0; i < this.loadedList.size(); i++) {
			this.formReferenceList.get(this.loadedList.get(i)).showCurrencyValue(currencySymbol);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.locale = l;
		this.setLocale(l);
		// For all forms, we establish resource file.
		for (int i = 0; i < this.loadedList.size(); i++) {
			this.formReferenceList.get(this.loadedList.get(i)).setComponentLocale(l);
		}
	}

	/**
	 * Registers the <code>InteractionManager</code> which is passed as entry parameter to the Form
	 * which name is passed as entry parameter as well. If the Form has just been loaded, the
	 * <code>InteractionManager</code> is registered else the Form name and the
	 * <code>InteractionManager</code> instance are stored in {@link #interactionManagerList} variable
	 * to be registered when the <code>Form</code> is loaded.
	 * @param interactionManager <code>InteractionManager</code> reference where the <code>Form</code>
	 *        is registered in.
	 * @param formName a <code>String</code> with the <code>Form</code> file name.
	 */
	@Override
	public void setInteractionManager(final InteractionManager interactionManager, final String formName) {
		final Form formReference = this.getFormReference(formName);
		if (formReference != null) {
			// It is necessary to remove the previous interaction manager
			// associated.
			final InteractionManager previousIneractionManager = formReference.getInteractionManager();
			if (previousIneractionManager != null) {
				try {
					previousIneractionManager.free();
				} catch (final Exception e) {
					BaseFormManager.logger.trace(null, e);
				}
			}
			// Adds the manager list (assigned) to free the resoruces when
			// needed
			this.interactionManagers.add(interactionManager);
			formReference.setInteractionManager(interactionManager);
			interactionManager.registerInteractionManager(formReference, this);

			interactionManager.setInitialState();
		} else {
			// Adds to the interaction manager list. So, it will be easy free
			// it.
			this.interactionManagerList.put(formName, interactionManager);
		}
	}

	@Override
	public InteractionManager getInteractionManager(final String formName) {
		if (this.interactionManagerList.containsKey(formName)) {
			return this.interactionManagerList.get(formName);
		}
		return null;
	}

	/**
	 * Sets the <code>InteractionManager</code> for a
	 * <code>Form<code> which file name is passed as entry parameter. The <code>InteractionManager</code>
	 * instance is loaded by reflection and the class constructor mustn't have any parameter.
	 * @param interactionManagerClass String with the <code>InteractionManager</code> Class name.
	 * @param formName String with the <code>Form</code> file name.
	 */

	@Override
	public void setInteractionManager(final String interactionManagerClass, final String formName) {
		if (this.getDelayedLoad() && !this.isLoaded()) {
			if (this.formInteractionManagerClassNameList == null) {
				this.formInteractionManagerClassNameList = new Hashtable();
			}
			this.formInteractionManagerClassNameList.put(formName, interactionManagerClass);
		} else {
			this.applyInteractionManager(interactionManagerClass, formName);
		}
	}

	/**
	 * Creates the <code>InteractionManager</code> class instance and call the
	 * {@link #setInteractionManager(InteractionManager, String)}
	 * @param interactionManagerClass a <code>String</code> with the <code>InteractionManager</code>
	 *        class name.
	 * @param formName a <code>String</code> with the <code>Form</code> file name.
	 */
	protected void applyInteractionManager(final String interactionManagerClass, final String formName) {
		try {
			final Class claseGI = Class.forName(interactionManagerClass);
			final Object gInt = claseGI.newInstance();
			if (gInt instanceof InteractionManager) {
				final InteractionManager im = (InteractionManager) gInt;

				if ((formName != null) && (formName.length() > 0) && (this.formInteractionManagerActionList != null)) {
					final Object oActionList = this.formInteractionManagerActionList.get(formName);
					if ((oActionList != null) && (oActionList instanceof String)) {
						im.loadActionHandler(oActionList.toString());
					}
				}

				this.setInteractionManager(im, formName);
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BaseFormManager.logger
					.debug(claseGI + " InteractionManager has been created successfully for " + formName + " Form");
				}
			} else {
				BaseFormManager.logger
				.debug(interactionManagerClass + "' class doesn't extend <code>InteractionManager</code> class");
			}
		} catch (final Exception e) {
			BaseFormManager.logger.error("Error creating <code>InteractionManager</code>: ", e);
		}
	}

	/**
	 * Gets the <code>Form</code> name that is currently visible.
	 */

	public String getCurrentForm() {
		return this.currentForm;
	}

	@Override
	public void free() {
		// It is necessary to free interaction managers and forms and remove the
		// form manager of parent container.
		// So, Resources of FormManager can be set free
		this.parent.remove(this);
		this.applicationFrame = null;
		try {
			this.formModifiableFields.clear();
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				BaseFormManager.logger.debug("Exception while trying to free the tree.", e);
			} else {
				BaseFormManager.logger.trace("Exception while trying to free the tree.", e);
			}
		}
		// Frees interaction managers
		for (int i = 0; i < this.interactionManagers.size(); i++) {
			try {
				this.interactionManagers.get(i).free();
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug(
							"Exception while trying to free " + this.interactionManagers.get(i).getClass().toString(),
							e);
				} else {
					BaseFormManager.logger.trace(
							"Exception while trying to free " + this.interactionManagers.get(i).getClass().toString(),
							e);
				}
			}
		}
		this.interactionManagers.clear();
		if (this.interactionManagerList != null) {
			final Enumeration enumKeys = Collections.enumeration(this.interactionManagerList.keySet());
			while (enumKeys.hasMoreElements()) {
				Object oKey = null;
				try {
					oKey = enumKeys.nextElement();
					final InteractionManager interactionManager = this.interactionManagerList.get(oKey);
					if (interactionManager != null) {
						interactionManager.free();
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						BaseFormManager.logger.debug("Exception while trying to free "
								+ this.interactionManagerList.get(oKey).getClass().toString(), e);
					} else {
						BaseFormManager.logger.trace("Exception while trying to free "
								+ this.interactionManagerList.get(oKey).getClass().toString(), e);
					}
				}
			}
		}
		if (this.formReferenceList != null) {
			final Enumeration enumFormKeys = Collections.enumeration(this.formReferenceList.keySet());
			while (enumFormKeys.hasMoreElements()) {
				Object oKey = null;
				try {
					oKey = enumFormKeys.nextElement();
					final Form f = this.formReferenceList.get(oKey);
					if (f != null) {
						f.free();
					}
				} catch (final Exception e) {
					BaseFormManager.logger.debug("Exception while trying to free {} ",
							this.interactionManagerList.get(oKey).getClass().toString(), e);
				}
			}
		}

		// Frees resources
		this.interactionManagers = null;
		this.resourceFile = null;
		this.locator = null;
		this.formBuilder = null;
		this.currentForm = null;
		this.loadedList = null;
		this.interactionManagerList = null;
		this.formReferenceList = null;
		this.applicationFrame = null;
		this.parent = null;
		this.auxPanel = null;
		this.panelTop = null;
	}

	/**
	 * Gets the <code>ResourceBundle</code> reference stored in this <code>FormManager</code>
	 * @return a <code>ResourceBundle</code> reference
	 */
	@Override
	public ResourceBundle getResourceBundle() {
		return this.resourceFile;
	}

	@Override
	public String getResourceFileName() {
		return this.resourceFileName;
	}

	@Override
	public Font showAvaliableFonts(final boolean supportingEuro) {
		final Form f = this.getFormReference(this.getCurrentForm());
		if (f != null) {
			return f.showAvaliableFonts(supportingEuro);
		} else {
			BaseFormManager.logger.debug("There is no current form");
			return null;
		}
	}

	/**
	 * Checks if the euro symbol can be displayed by the <code>Font</code> that is passed as entry
	 * parameter.
	 * @param font a <code>Font</code> that must display the euro symbol.
	 * @return true if euro symbol can be displayed.
	 */

	public static boolean supportsEuroSymbol(final Font font) {
		if (font.canDisplay('€')) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void useFont(final Font f) {
		// Establishes font type
		for (int i = 0; i < this.loadedList.size(); i++) {
			this.formReferenceList.get(this.loadedList.get(i)).useFont(f);
		}
	}

	/**
	 * Gets the container where the <code>FormManager</code> is layered out in.
	 * @return a <code>Container</code> reference.
	 */
	@Override
	public Container getContainer() {
		return this.parent;
	}

	/**
	 * Adds the component that is passed as entry parameter to {@link IFormManager#panelTop}
	 * @param component
	 */
	public void addTopComponent(final Component component) {
		if (!this.isLoaded()) {
			this.load();
		}
		this.panelTop.add(component);
		try {
			this.getLayout().layoutContainer(this);
		} catch (final Exception e) {
			BaseFormManager.logger.trace(null, e);
		}
	}

	/**
	 * Sets the unique identifier for this <code>FormManager</code>
	 * @param id a <code>String</code> with the unique identifier.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the unique identifier for this <code>FormManager</code>
	 * @return a <code>String</code> with the unique identifier.
	 */
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void initPermissions() {
		// Nothing
	}

	/**
	 * Sets the <code>Application</code> reference.
	 * @param application
	 */
	@Override
	public void setApplication(final Application application) {
		this.application = application;
	}

	/**
	 * Gets the <code>Application</code> reference.
	 * @return a <code>Application</code> reference
	 */
	@Override
	public Application getApplication() {
		return this.application;
	}

	/**
	 * Checks if this <code>FormManager</code> is restricted with visible permissions
	 * @param id
	 * @return false
	 */
	protected boolean checkVisiblePermission(final String id) {
		// Checking visible permissions
		try {
			final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
			if (manager != null) {
				if (this.visiblePermission == null) {
					this.visiblePermission = new FMPermission(this.id, "visible", "", true);
				}
				this.visiblePermission.setAttribute(id);
				manager.checkPermission(this.visiblePermission);
				return true;
			}
			return true;
		} catch (final Exception e) {
			BaseFormManager.logger.error(null, e);
			return false;
		}
	}

	/**
	 * Gets the attribute value into the <code>OTreeNode</code> that is passed as entry parameter. This
	 * method search this attribute into the <code>OTreeNode</code> and its parents.
	 * @param attribute the attribute to be found.
	 * @param node <code>OTreeNode</code> where the search of the attribute value will start in.
	 * @return a attribute value.
	 */

	protected Object getAttributeValue(final Object attribute, OTreeNode node) {
		Object oAttributeValue = null;
		while (!node.isRoot()) {
			if (!node.isOrganizational()) {
				boolean bContainsAttribute = false;
				for (int i = 0; i < node.getAttributes().length; i++) {
					if (node.getAttributes()[i].equals(attribute)) {
						bContainsAttribute = true;
						break;
					}
				}
				if (bContainsAttribute) {
					oAttributeValue = node.getValueForAttribute(attribute);
					break;
				}
			}
			node = (OTreeNode) node.getParent();
		}
		return oAttributeValue;
	}

	/**
	 * Enables/disables the check of modified form data
	 * @param check true the check of modifed form data is enabled.
	 */
	public void setCheckModifiedFormData(final boolean check) {
		this.checkModifiedDataForms = check;
	}

	@Override
	public String getLabelFileURI() {
		if (this.formBuilder instanceof XMLFormBuilder) {
			return ((XMLFormBuilder) this.formBuilder).getLabelFileURI();
		} else {
			return null;
		}
	}

	@Override
	public void setActiveForm(final Form f) {
		this.activeForm = f;
	}

	/**
	 * Returns the active <code>Form</code> reference. This method takes into account both the forms
	 * displayed in container and the forms layered out in <code>DetailForms</code>. Returns the
	 * <code>Form</code> reference established in method {@link #setActiveForm(Form)}. If the
	 * <code>Form</code> reference hasn't been established then this method returns the
	 * <code>Form</code> reference that is visible into the FormManager container.
	 * @return
	 */

	@Override
	public Form getActiveForm() {
		if (!this.isLoaded()) {
			this.load();
		}
		if (this.activeForm == null) {
			return this.getFormReference(this.currentForm);
		} else {
			return this.activeForm;
		}
	}

	@Override
	public boolean dataWillChange(final DataNavigationEvent e) {
		return true;
	}

	@Override
	public void dataChanged(final DataNavigationEvent e) {
		if (!this.processDataChangeEvents) {
			return;
		}
		final Form f = e.getForm();
		if (f == null) {
			return;
		}
		// Checks if other forms must be showed
		final DynamicFormManager dfm = f.getDynamicFormManager();
		if (dfm != null) {
			// Form to show.
			final String fShowForm = dfm.getForm(e.getData());
			if (fShowForm != null) {
				if (ApplicationManager.DEBUG) {
					BaseFormManager.logger.debug("DataChanged: Form to show: " + fShowForm);
				}
				if (!this.interactionManagerList.containsKey(fShowForm)) {
					this.setInteractionManager(dfm.getFormInteractionManagerClass(fShowForm), fShowForm);
				}
				// Now, we have to show the new form. This one must share the
				// data
				// list
				this.showForm(fShowForm);
				final Form fNewForm = this.getFormReference(fShowForm);
				if (fNewForm.getAssociatedTreePath() == null) {
					fNewForm.setLinkedTreePath(f.getAssociatedTreePath());
					fNewForm.setAssociatedNode(f.getAssociatedNode());
				}
				if (!this.formRegisteredDataNavigationListener.contains(fShowForm)) {
					// Registers the listener
					if (fNewForm != null) {
						fNewForm.addDataNavigationListener(this);
						fNewForm.setDynamicFormManager(dfm);
						this.formRegisteredDataNavigationListener.add(fShowForm);
					}
				}
				if ((fNewForm != null) && (fNewForm != f)) {
					if (ApplicationManager.DEBUG) {
						BaseFormManager.logger.debug("Setting the form data list to show...");
					}
					// We must configure the non-modifiable fields and values
					// for
					// fields that they are not
					// included in data list.
					final Map hFieldsValues = f.getDataFieldValues(false);
					fNewForm.setDataFieldValues(hFieldsValues);
					final List v = f.getDataFieldAttributeList();
					for (int i = 0; i < v.size(); i++) {
						final DataComponent c = f.getDataFieldReference(v.get(i).toString());
						if (c != null) {
							if (!c.isModifiable()) {
								fNewForm.setModifiable(v.get(i).toString(), false);
							}
						}
					}

					try {
						// Data list.
						this.processDataChangeEvents = false;
						if (fNewForm.getInteractionManager() != null) {
							fNewForm.getInteractionManager().setDataChangedEventProcessing(false);
						}
						fNewForm.updateDataFields(f.getDataList());

						// Selects the record
						fNewForm.updateDataFields(e.getIndex());
						if (ApplicationManager.DEBUG) {
							BaseFormManager.logger.debug("The form data list setted. Selected index: " + e.getIndex());
						}
					} catch (final Exception ex) {
						BaseFormManager.logger.error(null, ex);
					} finally {
						this.processDataChangeEvents = true;
						if (fNewForm.getInteractionManager() != null) {
							fNewForm.getInteractionManager().setDataChangedEventProcessing(true);
						}
					}
				} else {
					// If form is the same, we do nothing
				}
			}
		} else {
			BaseFormManager.logger.debug("DynamicFormManager is null for the form: " + f.getArchiveName());
		}
	}

	@Override
	public void setApplicationPreferences(final ApplicationPreferences ap) {
		this.aPreferences = ap;
		final Enumeration enumKeys = Collections.enumeration(this.formReferenceList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Form f = this.formReferenceList.get(oKey);
			if (f != null) {
				f.registerApplicationPreferencesListener();
				String user = null;
				if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}
				f.initPreferences(ap, user);
			}
		}
	}

	protected void updateInteractionDynamicFormMode(final Form form) {
		// Now, state of interaction manager
		final Form fNewForm = this.getFormReference(this.getCurrentForm());
		if (fNewForm != form) {
			final InteractionManager formInteractionManager = fNewForm.getInteractionManager();
			if (formInteractionManager != null) {
				final int currentMode = form.getInteractionManager().currentMode;
				switch (currentMode) {
				case InteractionManager.INSERT:
					formInteractionManager.setInsertMode();
					break;
				case InteractionManager.QUERY:
					formInteractionManager.setQueryMode();
					break;
				case InteractionManager.QUERYINSERT:
					formInteractionManager.setQueryInsertMode();
					break;
				case InteractionManager.UPDATE:
					formInteractionManager.setUpdateMode();
					break;
				default:
					formInteractionManager.setUpdateMode();
					break;
				}
			}
		}
	}

	/**
	 * Reload the <code>Form</code> that is passed as entry parameter.
	 * @param form <code>Form</code> reference to be reloaded.
	 */
	@Override
	public void reload(final Form form) {
		if (form == null) {
			return;
		}
		try {
			if (this.formReferenceList.containsValue(form)) {
				final String sFileName = form.getArchiveName();
				this.loadedList.remove(sFileName);
				if (form.getInteractionManager() != null) {
					form.getInteractionManager().free();
					form.free();
				}
				this.formReferenceList.remove(sFileName);
				final Container c = form.getParent();
				if (c != null) {
					Object constraints = null;
					final LayoutManager l = c.getLayout();
					if (l instanceof CardLayout) {
						constraints = form.getArchiveName();
					}
					try {
						final Form fNewForm = this.getFormCopy(sFileName);
						c.remove(form);
						c.add(fNewForm, constraints);
						c.validate();
						c.repaint();
					} catch (final Exception e) {
						BaseFormManager.logger.trace(null, e);
						form.message("Error reloading the form.  Maybe the xml is incorrect", Form.ERROR_MESSAGE, e);
					}

				}
			}
		} catch (final Exception e) {
			BaseFormManager.logger.error(null, e);
		}
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Gets the delayed load condition
	 * @return true if the FormManager will delay its loaded.
	 */
	public boolean getDelayedLoad() {
		return this.delayedLoad;
	}

	protected Map formManagerForms = new Hashtable(2);

	/**
	 * Stores in the {@link #formManagerForms} variable the <code>FormManager</code> unique identifier
	 * and the <code>Form</code> name that are passed as entry parameter.
	 * @param formName <code>String</code> with the form name.
	 * @param formManagerId <code>String</code> with the unique identifier where is stored the
	 *        <code>Form</code> in.
	 */
	public void setFormManagerToForm(final String formName, final String formManagerId) {
		// Adds
		BaseFormManager.logger.debug("Registering FormManager {} for the form {}", formManagerId, formName);
		if (formManagerId != null) {
			this.formManagerForms.put(formName, formManagerId);
		} else {
			this.formManagerForms.remove(formName);
		}
	}

	@Override
	public synchronized boolean isLoaded() {
		return this.loadedForm;
	}

	protected void setUpdateMode(final Form form) {
		try {
			form.getInteractionManager().setDefaultFocusEnabled(false);
			form.getInteractionManager().setUpdateMode();
		} catch (final Exception ex) {
			BaseFormManager.logger.error("Error in class " + this.getClass().getName(), ex);
		} finally {
			form.getInteractionManager().setDefaultFocusEnabled(true);
		}
	}

	protected void setQueryInsertMode(final Form form) {
		try {
			form.getInteractionManager().setDefaultFocusEnabled(false);
			form.getInteractionManager().setQueryInsertMode();
		} catch (final Exception ex) {
			BaseFormManager.logger.trace(null, ex);
		} finally {
			form.getInteractionManager().setDefaultFocusEnabled(true);
		}
	}

	@Override
	public FormBuilder getFormBuilder() {
		return this.formBuilder;
	}

	@Override
	public RuleEngine getRuleEngine() {
		return this.ruleEngine;
	}

	public void setRuleEngine(final RuleEngine ruleEngine) {
		this.ruleEngine = ruleEngine;
	}

	@Override
	public boolean showFrame() {
		return this.showFrame;
	}

	@Override
	public String getIcon() {
		return this.icon;
	};

}
