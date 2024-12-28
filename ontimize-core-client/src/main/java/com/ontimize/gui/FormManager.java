package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.ApplicationBuilder;
import com.ontimize.builder.FormBuilder;
import com.ontimize.builder.TreeBuilder;
import com.ontimize.builder.xml.XMLFormBuilder;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.manager.BaseFormManager;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.manager.ITreeFormManager;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.gui.tree.PageFetchTreeNode;
import com.ontimize.gui.tree.Tree;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ObjectTools;

/**
 * The <code>FormManager</code> is used to create, register and manage a group of form.
 *
 * @author Imatia Innovation
 */

public class FormManager extends BaseFormManager implements ITreeFormManager {

	private static final Logger logger = LoggerFactory.getLogger(FormManager.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.0.5
	 */
	public static final String TREESCROLLPANE = "TreeScrollPane";

	/**
	 * <code>SplitPane</code> reference used when a <code>Tree</code> is defined. If the
	 * <code>FormManager</code> hasn't defined a tree, this reference is null.
	 */
	protected JSplitPane splitPane;

	/**
	 * <code>JScrollPane</code> reference. Only if the tree is defined, this reference will have a
	 * value.
	 */
	protected JScrollPane treeScrollPane;

	/**
	 * The <code>Tree</code> reference that will be displayed in this <code>FormManager</code>
	 */
	protected Tree managedTree;

	/** TreeBuilder reference. */
	protected TreeBuilder treeBuilder;

	private JPanel auxPanel;

	private boolean processTreeSelectionEvent = true;

	private TreePath currentPath = null;

	private boolean checkModified = true;

	private boolean checkModifiedDataForms = true;

	private boolean canceledSelectionChange = false;

	private Form activeForm = null;

	/*
	 * /** A list formed by the forms where this <code>FormManager</code> has been registered as a
	 * <code>DataNavigationListener</code> in.
	 */
	// protected List formRegisteredDataNavigationListener = new Vector(0, 5);

	/** The FormManager will dispatch the DataNavigationEvent if this is true */
	// protected boolean processDataChangeEvents = true;

	/** The <code>ApplicationPreferences</code> reference */
	// protected ApplicationPreferences aPreferences = null;

	/** Condition used to register the preference listener only one time */
	// protected boolean preferenceListenersRegistered = false;

	// protected String dynamicFormClass = null;

	// protected DynamicFormManager formDynamicManager = null;

	/**
	 * If this condition is true, the <code>FormManager</code> will be loaded.
	 */
	protected boolean loadedTreeAndForm;

	/** The file name of the tree xml description. */
	protected String treeFileName;

	/** The default <code>Tree</code> class name */
	public static String defaultTreeClass = "com.ontimize.gui.tree.Tree";

	/**
	 * The <code>Tree</code> class name established for this <code>FormManager</code>. By default, if
	 * the {@link #TREE_CLASS} attribute isn't established, has the {@link #defaultTreeClass} value
	 */
	protected String treeClass;

	protected JPanel cardPanel;

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
	public FormManager(final Map parameters) throws Exception {
		super(parameters);
	}

	@Override
	public void init(final Map parameters) throws Exception {
		final String useclasspath = (String) parameters.get(IFormManager.USE_CLASS_PATH);
		if ("yes".equalsIgnoreCase(useclasspath)) {
			this.useClasspath = true;
		}

		final Object oTreeBuilder = parameters.get(ITreeFormManager.TREE_BUILDER);
		if ((oTreeBuilder != null) && (oTreeBuilder instanceof TreeBuilder)) {
			this.treeBuilder = (TreeBuilder) oTreeBuilder;
		} else {
			throw new IllegalArgumentException(
					"'" + ITreeFormManager.TREE_BUILDER + " doesn't implement TreeBuilder or is NULL");
		}

		final Object tree = parameters.get(ITreeFormManager.TREE);
		if ((tree != null) && (tree instanceof String)) {
			this.treeFileName = tree.toString();
			if (this.useClasspath) {
				final URL url = this.getClass().getClassLoader().getResource(this.treeFileName);
				if (url == null) {
					FormManager.logger.warn("{} -> Tree was not found: {}" + this.getId(), this.treeFileName);
				} else {
					this.treeFileName = url.toString();
				}
			}
		} else {
			FormManager.logger.debug("{}  parameter was not found: Tree cannot be established", ITreeFormManager.TREE);
		}

		final Object treeClass = parameters.get(ITreeFormManager.TREE_CLASS);
		if ((treeClass != null) && (treeClass instanceof String)) {
			this.treeClass = treeClass.toString();
		} else {
			this.treeClass = FormManager.defaultTreeClass;
			FormManager.logger.debug("{}  parameter was not found: Tree will be created using  {}",
					ITreeFormManager.TREE_CLASS, this.treeClass.getClass().getName());
		}
		super.init(parameters);
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
					final Enumeration enumKeys = this.formInteractionManagerClassNameList.keys();
					while (enumKeys.hasMoreElements()) {
						final String form = (String) enumKeys.nextElement();
						final String imClassName = this.formInteractionManagerClassNameList.get(form);
						this.applyInteractionManager(imClassName, form);
					}
				}
				if (this.setResourceBundleOnLoad) {
					this.setResourceBundle_internal();
				}

				this.loadedTreeAndForm = true;
				try {

					if (this.treeFileName != null) {
						try {
							final Class currentTreeClass = Class.forName(this.treeClass);
							final Constructor currentConstructor = currentTreeClass.getConstructor(
									new Class[] { String.class, TreeBuilder.class, EntityReferenceLocator.class });
							this.managedTree = (Tree) currentConstructor
									.newInstance(new Object[] { this.treeFileName, this.treeBuilder, this.locator });
						} catch (final Exception ex) {
							FormManager.logger.error(null, ex);
						}
					}

					this.registerApplicationPreferencesListener();
				} catch (final Exception e) {
					FormManager.logger.error(this.getClass().toString() + ": Error building tree" + e.getMessage(), e);
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						FormManager.logger.error(null, e);
					} else {
						FormManager.logger.trace(null, e);
					}
				}
				if (this.managedTree == null) {
					this.createLayout(this.applicationFrame, this.parent, this.formBuilder, this.initialForm,
							this.initialInteractionManager, this.resourceFile, false);
				} else {
					this.createLayoutWithTree(this.applicationFrame, this.parent, this.formBuilder, this.initialForm,
							this.initialInteractionManager, this.resourceFile,
							this.managedTree, false);
				}
			} else if (ApplicationManager.DEBUG) {
				FormManager.logger.debug("FormManager has already done the initial load");
			}

		} catch (final Exception ex) {
			FormManager.logger.error(null, ex);
			throw new RuntimeException(ex.getMessage());
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

	public FormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI, final ResourceBundle resource,
			final boolean northPanel) {
		super(frame, container, builder, formURI, resource, northPanel);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param tree <code>Tree</code> reference.
	 * @param northPanel true if new components can be added in <code>FormManager</code> using the
	 *        {@link #addTopComponent} method.
	 */

	// public FormManager(Frame frame, Container container, FormBuilder builder,
	// String formURI, ResourceBundle resource, Tree tree, boolean northPanel) {
	// this.loadedForm = true;
	// this.createLayoutWithTree(frame, container, builder, formURI, null,
	// resource, tree, northPanel);
	// }

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

	public FormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final InteractionManager interactionManager, final ResourceBundle resource, final boolean northPanel) {
		super(frame, container, builder, formURI, interactionManager, resource, northPanel);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param interactionManager <code>InteractionManager</code> reference.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param tree <code>Tree</code> reference.
	 * @param northPanel true if new components can be added in <code>FormManager</code> using the
	 *        {@link #addTopComponent} method.
	 */

	// public FormManager(Frame frame, Container container, FormBuilder builder,
	// String formURI, InteractionManager interactionManager, ResourceBundle
	// resource, Tree tree,
	// boolean northPanel) {
	// this.loadedTreeAndForm = true;
	// this.initWithTree(frame, container, builder, formURI, interactionManager,
	// resource, tree, northPanel);
	// }

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param resource ResourceBundle where is the language resources in.
	 */
	public FormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI, final ResourceBundle resource) {
		super(frame, container, builder, formURI, null, resource, false);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param interactionManager <code>InteractionManager</code> reference.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param tree <code>Tree</code> reference.
	 */

	// public FormManager(Frame frame, Container container, FormBuilder builder,
	// String formURI, ResourceBundle resource, Tree tree) {
	// this.loadedTreeAndForm = true;
	// this.initWithTree(frame, container, builder, formURI, null, resource,
	// tree, false);
	// }

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param interactionManager <code>InteractionManager</code> reference.
	 * @param resource ResourceBundle where is the language resources in.
	 */
	public FormManager(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final InteractionManager interactionManager, final ResourceBundle resource) {
		super(frame, container, builder, formURI, interactionManager, resource, false);
	}

	/**
	 * Builds a <code>FormManager</code>
	 * @param frame the Application Frame
	 * @param container <code>Container</code> where is layered out the <code>FormManager</code>in.
	 * @param builder <code>FormBuilder</code> used to create the <code>Form</code> instances.
	 * @param formURI URI where is stored the xml description in.
	 * @param interactionManager <code>InteractionManager</code> reference.
	 * @param resource ResourceBundle where is the language resources in.
	 * @param tree <code>Tree</code> reference.
	 */
	// public FormManager(Frame frame, Container container, FormBuilder builder,
	// String formURI, InteractionManager interactionManager, ResourceBundle
	// resource, Tree tree) {
	// this.loadedForm = true;
	// this.createLayoutWithTree(frame, container, builder, formURI,
	// interactionManager, resource, tree, false);
	// }

	@Override
	protected JComponent createCenterPanel() {
		this.cardPanel = new JPanel(new CardLayout());

		if (this.managedTree == null) {
			return this.cardPanel;
		} else {
			this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {

				@Override
				public void updateUI() {
					super.updateUI();
					this.setDividerSize(12);
				}
			};
			this.splitPane.setOneTouchExpandable(true);
			this.splitPane.setDividerSize(12);
			this.treeScrollPane = new JScrollPane() {

				@Override
				public String getName() {
					return FormManager.TREESCROLLPANE;
				}
			};
			this.splitPane.add(this.treeScrollPane, JSplitPane.LEFT);
			this.splitPane.setDividerLocation(0.25);
			this.treeScrollPane.getViewport().add(this.managedTree);
			return this.splitPane;
		}
	}

	private void createLayoutWithTree(final Frame frame, final Container container, final FormBuilder builder, final String formURI,
			final InteractionManager interactionManager, final ResourceBundle resources,
			final Tree tree, final boolean controls) {

		this.managedTree = tree;
		this.managedTree.addTreeWillExpandListener(this);
		this.managedTree.addTreeExpansionListener(this);
		this.managedTree.addTreeSelectionListener(this);
		this.managedTree.setResourceBundle(resources);
		this.managedTree.setFormManager(this);
		this.managedTree.setParentFrame(this.applicationFrame);

		if ((resources != null) && (resources.getLocale() != null)) {
			this.managedTree.setComponentLocale(resources.getLocale());
		} else {
			this.managedTree.setComponentLocale(this.locale);
		}

		this.createLayout(frame, container, builder, formURI, interactionManager, resources, controls);
		try {
			// Control panel
			if (controls) {
				this.auxPanel = new JPanel(new BorderLayout());
				this.auxPanel.add(this.panelTop, BorderLayout.NORTH);
				this.auxPanel.add(this.cardPanel, BorderLayout.CENTER);
				this.splitPane.add(this.auxPanel, JSplitPane.RIGHT);
			} else {
				this.splitPane.add(this.cardPanel, JSplitPane.RIGHT);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger.debug("Error adding form manager to the container. ", e);
			} else {
				FormManager.logger.trace(null, e);
			}
			return;
		}

		this.revalidate();

		if (Tree.PREFERRED_WIDTH == -1) {
			this.treeScrollPane.setPreferredSize(
					new Dimension(tree.getPreferredSize().width + 60, this.treeScrollPane.getPreferredSize().height));
		} else {
			this.treeScrollPane
			.setPreferredSize(new Dimension(Tree.PREFERRED_WIDTH, this.treeScrollPane.getPreferredSize().height));
		}
	}

	/**
	 * Flips to the next <code>Form</code> that is stored in the <code>FormManager</code>
	 */
	public void next() {
		((CardLayout) this.cardPanel.getLayout()).next(this.parent);
	}

	/**
	 * Flips to the first <code>Form</code> that is stored in the <code>FormManager</code>
	 */
	public void first() {
		((CardLayout) this.cardPanel.getLayout()).first(this.parent);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		// And for tree when exists
		if (this.managedTree != null) {
			v.addAll(this.managedTree.getTextsToTranslate());
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		// If custom resource file is defined, it will be used and passed
		// parameter will be obviated
		if (this.resourceFileName == null) {
			// For all forms, we establish resource file.
			// And for tree
			if (this.managedTree != null) {
				this.managedTree.setResourceBundle(resources);
			}
		} else {
			if (resources != null) {
				if ((this.delayedLoad) && !this.isLoaded()) {
					this.setResourceBundleOnLoad = true;
					return;
				}
				try {
					// And for tree when it exists
					if (this.managedTree != null) {
						this.managedTree.setResourceBundle(this.resourceFile);
					}
				} catch (final Exception e) {
					FormManager.logger.trace(null, e);
				}
			}

		}
	}

	@Override
	protected void setResourceBundle_internal() {
		super.setResourceBundle_internal();
		if (this.resourceFileName == null) {
			return;
		}
		try {
			// And for tree when it exists
			if (this.managedTree != null) {
				this.managedTree.setResourceBundle(this.resourceFile);
			}
		} catch (final Exception e) {
			FormManager.logger.trace(null, e);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		super.setComponentLocale(l);
		// And for tree when it exists
		if (this.managedTree != null) {
			this.managedTree.setComponentLocale(l);
		}
	}

	/**
	 * @param node
	 */
	@Override
	public void updateParentkeys(final OTreeNode node) {
		if ((node != null) && node.isOrganizational()) {
			final Form fForm = this.getFormReference(node.getForm());
			// Moreover, we delete the form data
			fForm.deleteDataFields(true);
			// Moves until the root
			OTreeNode auxNode = node;
			final ArrayList settedList = new ArrayList();
			while ((auxNode != null) && !auxNode.isRoot()) {
				final Map hAssociatedFields = auxNode.getAssociatedDataField();
				final Enumeration enumKeys = Collections.enumeration(hAssociatedFields.keySet());
				final OTreeNode oParentNode = (OTreeNode) auxNode.getParent();
				while (enumKeys.hasMoreElements() /*  */) {
					final Object oKey = enumKeys.nextElement();
					final Object pValue = oParentNode.getValueForAttribute(oKey);
					final Object oAssociatedField = hAssociatedFields.get(oKey);
					if (!settedList.contains(oAssociatedField)) {
						fForm.setModifiable(oAssociatedField.toString(), false);
						fForm.setDataFieldValue(oAssociatedField.toString(), pValue);
						settedList.add(oAssociatedField);
					}
				}
				if (oParentNode.isOrganizational()) {
					auxNode = oParentNode;
				} else {
					auxNode = (OTreeNode) oParentNode.getParent();
				}
			}
		}
	}

	protected void selectOrganizationalNode(OTreeNode oTreeNode, final Form fForm) {
		// Organizational node
		// Moreover, we delete form data
		fForm.deleteDataFields(true);

		final List vFixAttributes = this.setFormDataFieldData(oTreeNode, fForm);

		final List vNoModifiables = ObjectTools.clone(vFixAttributes);

		// We have to show the parent associated value

		// Moving backward until the root node.
		final List vCustomerFields = new Vector();
		while ((oTreeNode != null) && !oTreeNode.isRoot()) {
			final Map hAssociatedFields = oTreeNode.getAssociatedDataField();
			final List vAssociatedOrderedFields = oTreeNode.getAssociatedOrderedDataField();
			final OTreeNode oParentNode = (OTreeNode) oTreeNode.getParent();
			for (int i = 0; i < vAssociatedOrderedFields.size(); i++) {
				final Object oKey = vAssociatedOrderedFields.get(i);
				if (vCustomerFields.contains(oKey)) {
					continue;
				} else {
					vCustomerFields.add(oKey);
				}
				final Object oValue = oParentNode.getValueForAttribute(oKey);
				final Object oAssociatedField = hAssociatedFields.get(oKey);
				fForm.setModifiable(oAssociatedField.toString(), false);
				fForm.setDataFieldValue(oAssociatedField.toString(), oValue);
				vNoModifiables.add(oAssociatedField);

			}
			if (oParentNode.isOrganizational()) {
				oTreeNode = oParentNode;
			} else {
				oTreeNode = (OTreeNode) oParentNode.getParent();
			}
		}

		this.formModifiableFields.put(fForm, vNoModifiables);

		fForm.setLinkedTreePath(this.currentPath);
		fForm.setAssociatedNode(oTreeNode);
		// If it is an organizational node, we disable fields
		// and
		// enables query and insert button.
		if (fForm.getInteractionManager() != null) {
			this.setQueryInsertMode(fForm);
			this.updateInteractionDynamicFormMode(fForm);
		}
	}

	protected void selectDataNode(OTreeNode oTreeNode, final Form fForm, final Cursor cursor, final long initTime) {

		final List vFixAttributes = this.setFormDataFieldData(oTreeNode, fForm);

		final List vNoModifiables = ObjectTools.clone(vFixAttributes);

		// When tree shows the form, it must look for if fields
		// whose
		// attribute is equals of the parent node are
		// present. In this case, it must disable it because
		// attribute
		// is fixed by tree hierarchy. In opposite,

		if (!oTreeNode.isRoot()) {
			this.setDataFromParent(oTreeNode, fForm, vNoModifiables);
		}

		final long t3 = System.currentTimeMillis();
		FormManager.logger.trace("Tree node '" + oTreeNode.toString() + "' Non-: " + (t3 - initTime));

		if (!oTreeNode.isClassifyNode()) {
			// For executing query, upper data nodes must be
			// considered.
			// When node is not organizational, we have to get
			// parent
			// of parent of this one.
			final Map hSearchKeysValues = new Hashtable();
			OTreeNode oParentNode;
			if (oTreeNode.isRoot()) {
				// Moreover, we obtain current node keys and
				// query and
				// fill data in form.
				final Map hKeysValues = oTreeNode.getKeysValues();
				try {
					final Entity entity = this.getReferenceLocator().getEntityReference(oTreeNode.getEntityName());
					// When user does click in node, if node is
					// organizational, form will update.
					// Possibly,
					// this form contains tables. This table
					// queries
					// other entities, then, attribute vector
					// must be
					// passed
					// to entity.
					final List vFieldsAttributes = this.getFormAttributes(fForm);

					final long t4 = System.currentTimeMillis();
					final EntityResult results = entity.query(hKeysValues, vFieldsAttributes, this.locator.getSessionId());

					final long t5 = System.currentTimeMillis();
					FormManager.logger.trace("Tree Node '" + oTreeNode.toString() + "' Query time only : " + (t5 - t4));
					if (results != null) {
						if (results.getCode() == EntityResult.OPERATION_WRONG) {
							this.getFormReference(this.getCurrentForm()).message(results.getMessage(), Form.MESSAGE);
						}
						if (results.isEmpty()) {
							if (results.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
								this.getFormReference(this.getCurrentForm())
								.message(results.getMessage(), Form.WARNING_MESSAGE);
							} else {
								this.getFormReference(this.getCurrentForm())
								.message(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE,
										Form.WARNING_MESSAGE);
							}

							if (fForm.getInteractionManager() != null) {
								this.applyResultInteractionManager(oTreeNode, fForm, results);
								this.setQueryInsertMode(fForm);
								this.updateInteractionDynamicFormMode(fForm);
								return;
							}
						}
					}
					final long t6 = System.currentTimeMillis();

					this.applyResultInteractionManager(oTreeNode, fForm, results);

					final long t7 = System.currentTimeMillis();
					FormManager.logger
					.trace("Tree node '" + oTreeNode.toString() + "' Update Form time (only) : " + (t7 - t6));
				} catch (final Exception e) {
					fForm.message("Error performing the query. " + e.getMessage(), Form.MESSAGE);
					FormManager.logger.debug(null, e);
				} finally {
					this.setCursor(cursor);
				}
				return;
			}

			if (!((OTreeNode) (oTreeNode).getParent()).isRoot()) {
				oParentNode = (OTreeNode) ((OTreeNode) oTreeNode.getParent()).getParent();
				// Get the associated field from the keys and
				// values
				final Map AssociatedFields = oTreeNode.getAssociatedDataField();
				final Enumeration enumAssociatedFieldKeys = Collections.enumeration(AssociatedFields.keySet());
				while (enumAssociatedFieldKeys.hasMoreElements()) {
					final Object oParentField = enumAssociatedFieldKeys.nextElement();
					final Object oChildField = AssociatedFields.get(oParentField);
					hSearchKeysValues.put(oChildField, oParentNode.getValueForAttribute(oParentField));
				}
			}
			// Gets the current key and does the query and fills
			// with
			// form data
			final Map hKeysValues = oTreeNode.getKeysValues();
			final Enumeration enumNodeKeys = Collections.enumeration(hKeysValues.keySet());
			while (enumNodeKeys.hasMoreElements()) {
				final Object oKey = enumNodeKeys.nextElement();
				final Object oKeyValue = hKeysValues.get(oKey);
				hSearchKeysValues.put(oKey, oKeyValue);
			}
			try {
				final Entity entity = this.getReferenceLocator().getEntityReference(oTreeNode.getEntityName());
				// Query is executed to the table
				final long t4 = System.currentTimeMillis();
				final List vFieldsAttributes = this.getFormAttributes(fForm);

				final EntityResult results = entity.query(hSearchKeysValues, vFieldsAttributes, this.locator.getSessionId());
				final long t5 = System.currentTimeMillis();
				FormManager.logger.trace("Tree node '" + oTreeNode.toString() + "' Query time (only) : " + (t5 - t4));
				if (results != null) {
					if (results.getCode() == EntityResult.OPERATION_WRONG) {
						this.getFormReference(this.getCurrentForm()).message(results.getMessage(), Form.MESSAGE);
					} else if (results.isEmpty()) {
						if (results.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
							this.getFormReference(this.getCurrentForm()).message(results.getMessage(), Form.MESSAGE);
						} else {
							this.getFormReference(this.getCurrentForm())
							.message(BaseFormManager.M_NOT_DATA_IT_IS_POSSIBLE_THAT_RECORD_HAS_BEEN_DELETE,
									Form.MESSAGE);
						}
					}
				}
				final long t6 = System.currentTimeMillis();
				fForm.setAssociatedNode(oTreeNode);
				fForm.setLinkedTreePath(this.currentPath);
				try {
					this.setQueryInsertMode(fForm);

					if (fForm.getInteractionManager() != null) {
						fForm.getInteractionManager().setDataChangedEventProcessing(false);
					}
					fForm.updateDataFields(EntityResultUtils.toMap(results));
				} catch (final Exception e) {
					FormManager.logger.trace(null, e);
				} finally {
					if (fForm.getInteractionManager() != null) {
						fForm.getInteractionManager().setDataChangedEventProcessing(true);
					}
				}
				final long t7 = System.currentTimeMillis();
				FormManager.logger
				.trace("Tree node '" + oTreeNode.toString() + "' Update Form time (only) : " + (t7 - t6));
				// Form in update mode
				if (fForm.getInteractionManager() != null) {
					this.setUpdateMode(fForm);
					this.updateInteractionDynamicFormMode(fForm);
				}
			} catch (final Exception e) {
				fForm.message("Error performing the query. " + e.getMessage(), Form.MESSAGE);
				FormManager.logger.debug(null, e);
			}
		} else {
			// It is a classify node. We do not execute the
			// query.
			// Fixes QueryInsertMode.
			TreePath tpNewPath = this.currentPath;
			if (oTreeNode.getChildCount() == 1) {
				oTreeNode = (OTreeNode) oTreeNode.getChildAt(0);
				tpNewPath = this.currentPath.pathByAddingChild(oTreeNode);
			}
			fForm.setAssociatedNode(oTreeNode);
			fForm.setLinkedTreePath(tpNewPath);
			if (fForm.getInteractionManager() != null) {
				this.setQueryInsertMode(fForm);
				this.updateInteractionDynamicFormMode(fForm);
			}
			// Now we put the node values in form like parentkey
			// fields.
			final String[] attributes = oTreeNode.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				fForm.setModifiable(attributes[i], false);
				fForm.setDataFieldValue(attributes[i], oTreeNode.getValueForAttribute(attributes[i]));
				vNoModifiables.add(attributes[i]);
			}
		}
		this.formModifiableFields.put(fForm, vNoModifiables);

	}

	protected void applyResultInteractionManager(final OTreeNode oTreeNode, final Form fForm, final EntityResult results) {
		fForm.setAssociatedNode(oTreeNode);
		fForm.setLinkedTreePath(this.currentPath);
		try {
			if (fForm.getInteractionManager() != null) {
				fForm.getInteractionManager().setDataChangedEventProcessing(false);
			}
			fForm.updateDataFields(EntityResultUtils.toMap(results));
		} catch (final Exception e) {
			FormManager.logger.trace(null, e);
		} finally {
			if (fForm.getInteractionManager() != null) {
				fForm.getInteractionManager().setDataChangedEventProcessing(true);
			}
		}

	}

	protected List setFormDataFieldData(final OTreeNode oTreeNode, final Form fForm) {
		// For attributes fixed in tree structure, we establish
		// field
		// values indicated by node.
		final List vFixAttributes = oTreeNode.getFixAttributes();
		// These attributes are the necessary to find in parent
		// nodes
		// and fix their values in form.
		for (int i = 0; i < vFixAttributes.size(); i++) {
			final Object oAttribute = vFixAttributes.get(i);
			final Object oValue = this.getAttributeValue(oAttribute, oTreeNode);
			fForm.disableDataField(oAttribute.toString());
			fForm.setModifiable(oAttribute.toString(), false);
			fForm.setDataFieldValue(oAttribute.toString(), oValue);
		}
		return vFixAttributes;
	}

	protected void setDataFromParent(final OTreeNode oTreeNode, final Form fForm, final List vNoModifiables) {

		OTreeNode parentNode = (OTreeNode) oTreeNode.getParent();
		final Set updatedDataField = new HashSet();
		while ((parentNode != null) && !parentNode.isRoot()) {
			final Map hAssociatedFields = parentNode.getAssociatedDataField();
			final Enumeration enumKeys = Collections.enumeration(hAssociatedFields.keySet());
			final OTreeNode oParentNode = (OTreeNode) parentNode.getParent();
			while (enumKeys.hasMoreElements() /*  */) {
				final Object oKey = enumKeys.nextElement();
				if (!updatedDataField.contains(oKey)) {
					final Object oValue = oParentNode.getValueForAttribute(oKey);
					final Object oAssociatedField = hAssociatedFields.get(oKey);
					fForm.setModifiable(oAssociatedField.toString(), false);
					fForm.setDataFieldValue(oAssociatedField.toString(), oValue);
					vNoModifiables.add(oAssociatedField.toString());
					updatedDataField.add(oKey);
				}
			}
			if (oParentNode.isOrganizational()) {
				parentNode = oParentNode;
			} else {
				parentNode = (OTreeNode) oParentNode.getParent();
			}
		}
	}

	protected List getFormAttributes(final Form fForm) {
		if (fForm instanceof FormExt) {
			return ((FormExt) fForm).getAttributesToQuery();
		} else {
			return fForm.getDataFieldAttributeList();
		}
	}

	@Override
	public void valueChanged(final TreeSelectionEvent event) {
		if (!this.processTreeSelectionEvent) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Selection Event processing disabled: Tree selection event will not be processed");
			}
			return;

		}
		// Cursor cursor = getCursor();
		// Cursor cursor = Cursor.getDefaultCursor();
		final Cursor cursor = null;
		final long initTime = System.currentTimeMillis();
		final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.setCursor(waitCursor);
		final TreePath path = event.getPath();
		final TreePath previousPath = event.getOldLeadSelectionPath();

		if (((previousPath != null) && previousPath.equals(path)) || (path == null)) {
			// If path of event is equals to the current path, then event is not
			// processed for
			// avoiding internal loops.
			this.setCursor(cursor);
			FormManager.logger
			.debug("Tree selection event will not be processed because the selected path haven't changed");
			return;
		}

		if (this.canceledSelectionChange) {
			FormManager.logger.debug("Selection changed canceled:  Tree selection event will not be processed");
			return;
		}

		long t2 = 0;
		final long t3 = 0;
		final long t4 = 0;
		final long t5 = 0;
		final long t6 = 0;
		final long t7 = 0;
		final Object node = path.getLastPathComponent();
		if (node instanceof OTreeNode) {
			final String sFormShowName = ((OTreeNode) node).getForm();

			DynamicFormManager fd = null;
			if (this.formDynamicManager == null) {
				fd = ((OTreeNode) node).getDynamicFormManager();
			} else {
				fd = this.formDynamicManager;
			}
			if (fd != null) {
				FormManager.logger.debug("The Form has set a dynamic form manager...");
				fd.setFormManager(this);
				// If it has formDynamicManager, we register listener before its
				// name changes.
				if (sFormShowName != null) {
					if (!this.formRegisteredDataNavigationListener.contains(sFormShowName)) {
						// Registers the listeners
						this.showForm(sFormShowName);
						final Form f = this.getFormReference(sFormShowName);
						if (f != null) {
							FormManager.logger.debug(
									"Dynamic Form Listener DataNavigation was register sucessfully..." + sFormShowName);
							f.addDataNavigationListener(this);
							f.setDynamicFormManager(fd);
							this.formRegisteredDataNavigationListener.add(sFormShowName);
						} else {
							FormManager.logger.debug("No DataNavigation listener registered because the Form is null");
						}
					}
				}

			}
			FormManager.logger.debug("The form to show is: " + sFormShowName);
			if (sFormShowName != null) {
				if (!this.showForm(sFormShowName)) {
					this.canceledSelectionChange = true;
					FormManager.logger.debug("Cancelling selection change...");
					this.managedTree.setSelectionPath(event.getOldLeadSelectionPath());
					this.canceledSelectionChange = false;
					this.setCursor(cursor);
					return;
				} else {
					this.currentPath = path;
					final Form fForm = this.getFormReference(sFormShowName);

					if (this.formModifiableFields.containsKey(fForm)) {
						final List v = (List) this.formModifiableFields.get(fForm);
						for (int i = 0; i < v.size(); i++) {
							fForm.setModifiable((String) v.get(i), true);
						}
					}
					fForm.updateDataFields(new Hashtable());
					t2 = System.currentTimeMillis();
					FormManager.logger
					.trace("Tree node '" + node.toString() + "' Form fields clear time: " + (t2 - initTime));

					if (!((OTreeNode) node).isOrganizational()) {
						this.selectDataNode((OTreeNode) node, fForm, cursor, initTime);
					} else {
						this.selectOrganizationalNode((OTreeNode) node, fForm);
					}
				}
			}
		}

		final long finalTime = System.currentTimeMillis();
		FormManager.logger.trace("Time since Form data update started: '" + node.toString() + "': " + (finalTime - t7));
		FormManager.logger.trace("Tree node selection time'" + node.toString() + "': " + (finalTime - initTime));
		FormManager.logger.debug("Tree selection event process finished...");
		this.setCursor(cursor);
	}

	@Override
	public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException {
		// Cursor cursor = getCursor();
		final Cursor cursor = null;
		final long initTime = System.currentTimeMillis();
		final Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
		this.setCursor(waitCursor);
		final TreePath path = event.getPath();
		final Object oNode = path.getLastPathComponent();
		((OTreeNode) oNode).setRemark(false);
		if (((OTreeNode) oNode).isLeaf()) {
			this.setCursor(cursor);
			return;
		}

		if (((OTreeNode) oNode).isOrganizational()) {
			final long t = System.currentTimeMillis();
			Map result = null;
			OTreeNode updateParentNode = null;
			if (oNode instanceof PageFetchTreeNode) {
				updateParentNode = (OTreeNode) ((OTreeNode) oNode).getParent();
			}
			result = this.managedTree.createDataNode((OTreeNode) oNode, this.locator);
			final long t2 = System.currentTimeMillis();
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger.debug("Data node creation time= " + ((t2 - t) / 1000.0) + " seconds");
			}
			if (result != null) {
				if (result instanceof EntityResult) {
					if (((EntityResult) result).getCode() == EntityResult.OPERATION_WRONG) {
						if (this.getCurrentForm() != null) {
							final Form fAct = this.getFormReference(this.getCurrentForm());
							if (fAct != null) {
								fAct.message(((EntityResult) result).getMessage(), Form.ERROR_MESSAGE);
							}
						} else {
							MessageDialog.showErrorMessage(null, ((EntityResult) result).getMessage());
						}
						this.setCursor(cursor);
						// Not expand
						throw new ExpandVetoException(event);
					} else if (((EntityResult) result).getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
						if (this.getCurrentForm() != null) {
							final Form fAct = this.getFormReference(this.getCurrentForm());
							if (fAct != null) {
								fAct.setStatusBarText(((EntityResult) result).getMessage(), 5000);
							}
						}
					}
				}
			} else {
				this.managedTree.updateChildrenNodes((OTreeNode) oNode, this.locator);
			}
			// Updates form data and tree
			final long t3 = System.currentTimeMillis();

			if ((result != null) && (result instanceof EntityResult)) {
				if (((EntityResult) result).getCode() != EntityResult.OPERATION_WRONG) {
					((OTreeNode) oNode).setCount(((EntityResult) result).calculateRecordNumber());
				}
			}

			if (updateParentNode == null) {
				((DefaultTreeModel) this.managedTree.getModel()).nodeStructureChanged((OTreeNode) oNode);
			} else {
				((DefaultTreeModel) this.managedTree.getModel()).nodeStructureChanged(updateParentNode);
			}

			final long t4 = System.currentTimeMillis();
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger.debug("Tree model update time = " + ((t4 - t3) / 1000.0) + " seconds");
			}
			this.managedTree.setSelectionPath(path);
			final long t5 = System.currentTimeMillis();
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Node selection time after tree model update = " + ((t5 - t4) / 1000.0) + " seconds");
			}

		} else {
			// Child nodes are organizational
			this.managedTree.updateChildrenNodes((OTreeNode) oNode, this.locator);
			((DefaultTreeModel) this.managedTree.getModel()).nodeStructureChanged((OTreeNode) oNode);
		}

		final long endTime = System.currentTimeMillis();
		if (com.ontimize.gui.ApplicationManager.DEBUG) {
			FormManager.logger.debug("Tree node expand time '" + oNode.toString() + "': "
					+ Double.toString((endTime - initTime) / 1000.0) + " seconds.");
		}
		this.setCursor(cursor);
	}

	@Override
	public void treeExpanded(final TreeExpansionEvent event) {
		if (this.managedTree.getFitSpaceToTree()) {
			this.fitSpaceToTree();
		}
	}

	/**
	 * Fits the space to the tree.
	 */
	protected void fitSpaceToTree() {
		if (SwingUtilities.isEventDispatchThread()) {
			this.managedTree.revalidate();
			this.splitPane.setDividerLocation(this.managedTree.getPreferredSize().width + 30);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						FormManager.this.managedTree.revalidate();
						FormManager.this.splitPane
						.setDividerLocation(FormManager.this.managedTree.getPreferredSize().width + 30);
					}
				});
			} catch (final Exception e) {
				FormManager.logger.trace(null, e);
			}
		}
	}

	/**
	 * Gets the JSplitPane reference. If the form manager doesn't have tree this method return null.
	 * @return
	 */
	public JSplitPane getJSPlitPane() {
		return this.splitPane;
	}

	@Override
	public void treeCollapsed(final TreeExpansionEvent event) {
		if (this.managedTree.getFitSpaceToTree()) {
			this.fitSpaceToTree();
		}
	}

	@Override
	public void treeWillCollapse(final TreeExpansionEvent event) {
		final TreePath path = event.getPath();
		final Object oNode = path.getLastPathComponent();
		this.managedTree.deleteNotOrganizationalNode((OTreeNode) oNode);

		((DefaultTreeModel) this.managedTree.getModel()).nodeStructureChanged((OTreeNode) oNode);
		this.managedTree.setSelectionPath(path);
	}

	/**
	 * Gets the <code>Form</code> name that is currently visible.
	 */

	@Override
	public String getCurrentForm() {
		return this.currentForm;
	}

	/**
	 * This method does that the parent node of current selected node will be selected if the current
	 * selected node is a data node.
	 */
	@Override
	public void dataInserted() {
		try {
			// If dataInserted is called by program, modified fields are not
			// checked
			this.checkModified = false;
			if (this.managedTree != null) {
				TreePath path = this.managedTree.getSelectionPath();
				// If node is organizational. We update children, collapsing and
				// expanding.
				if (path == null) {
					return;
				}
				final Object oNode = path.getLastPathComponent();
				if (oNode instanceof OTreeNode) {
					if (!((OTreeNode) oNode).isOrganizational()) {
						path = path.getParentPath();
						this.managedTree.setSelectionPath(path);
					}
				}
			}
		} catch (final Exception e) {
			FormManager.logger.trace(null, e);
		} finally {
			this.checkModified = true;
		}
	}

	/**
	 * This method updates the tree node associated to the active <code>Form</code>.
	 * @deprecated
	 */
	@Deprecated
	public void dataInserted(final Map keysValues) {
		try {
			this.checkModified = false;
			if (this.managedTree != null) {
				TreePath path = this.managedTree.getSelectionPath();
				if (path == null) {
					return;
				}
				Object oNode = path.getLastPathComponent();
				if (oNode instanceof OTreeNode) {
					if (!((OTreeNode) oNode).isOrganizational()) {
						path = path.getParentPath();
						oNode = path.getLastPathComponent();
					}
					try {
						this.managedTree.collapsePath(path);
						this.managedTree.expandPath(path);
						this.managedTree.setSelectionPath(path);
						if (!keysValues.isEmpty()) {
							try {
								final List vChildren = ((OTreeNode) oNode).getChildren();
								for (int i = 0; i < vChildren.size(); i++) {
									boolean bValid = true;
									final Map hKeysValues = ((OTreeNode) ((OTreeNode) oNode).getChildAt(i))
											.getKeysValues();
									final Enumeration enumInsertKeys = Collections.enumeration(keysValues.keySet());
									while (enumInsertKeys.hasMoreElements()) {
										final Object oKey = enumInsertKeys.nextElement();
										if (!hKeysValues.containsKey(oKey)) {
											bValid = false;
											break;
										} else {
											final Object oValue = keysValues.get(oKey);
											final Object oNodeValue = hKeysValues.get(oKey);
											if (!oValue.equals(oNodeValue)) {
												bValid = false;
												break;
											}
										}
									}
									if (bValid) {
										final TreePath tpNewPath = path.pathByAddingChild(vChildren.get(i));
										final int iRowToSelect = this.managedTree.getRowForPath(tpNewPath);
										this.managedTree.setSelectionRow(iRowToSelect);
										break;
									}
								}
							} catch (final Exception e2) {
								if (com.ontimize.gui.ApplicationManager.DEBUG) {
									FormManager.logger.debug("Exception while searching the inserted node: ", e2);
								} else {
									FormManager.logger.trace(null, e2);
								}
							}
						}
					} catch (final Exception e) {
						FormManager.logger.trace(null, e);
					}
				}
			}
		} catch (final Exception e) {
			FormManager.logger.trace(null, e);
		} finally {
			this.checkModified = true;
		}
	}

	/**
	 * Gets the <code>Tree</code> reference that is associated to this <code>FormManager</code>
	 * @return <code>Tree</code> reference.
	 */
	@Override
	public Tree getTree() {
		if (!this.isLoaded()) {
			this.load();
		}
		return this.managedTree;
	}

	@Override
	public void free() {
		super.free();
		try {
			if (this.managedTree != null) {
				this.remove(this.splitPane);
				this.splitPane.remove(this.treeScrollPane);
				this.treeScrollPane.remove(this.managedTree);
				this.treeScrollPane.getViewport().remove(this.managedTree);
				this.splitPane = null;
				this.treeScrollPane = null;
				this.managedTree.removeTreeSelectionListener(this);
				this.managedTree.removeTreeWillExpandListener(this);
				this.managedTree.free();
			}
		} catch (final Exception e) {
			FormManager.logger.debug("Exception while trying to free the tree:", e);
		}

		this.managedTree = null;

		// Frees resources
		this.interactionManagers = null;
		this.managedTree = null;
		this.resourceFile = null;
		this.locator = null;
		this.formBuilder = null;
		this.currentForm = null;
		this.cardPanel = null;
		this.loadedList = null;
		this.interactionManagerList = null;
		this.formReferenceList = null;
		this.applicationFrame = null;
		this.parent = null;
		this.auxPanel = null;
		this.panelTop = null;
	}

	/**
	 * Inserts a new node into the <code>Tree</code> associated to this <code>FormManager</code>
	 * @param path
	 * @param keysValues
	 */

	@Override
	public void insertedNode(final TreePath path, final Map keysValues) {
		try {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Tree selection event processing disabled when inserting a new node (FormManager)");
			}
			this.processTreeSelectionEvent = false;
			if (this.managedTree != null) {
				this.managedTree.insertedNode(path, keysValues, this.locator, false);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger.debug("Inserted node error", e);
			} else {
				FormManager.logger.trace(null, e);
			}
		} finally {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Tree selection event processing enabled after inserting a new node (FormManager)");
			}
			this.processTreeSelectionEvent = true;
		}
	}

	/**
	 * This method call the {@link FormManager#insertedNode(TreePath, Hashtable)} and the new node will
	 * be selected if the condition childSelect is true.
	 * @param path
	 * @param keysValues
	 * @param childSelect
	 */
	@Override
	public void insertedNode(final TreePath path, final Map keysValues, final boolean childSelect) {
		try {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Tree selection event processing enabled prior inserting a new node (FormManager)");
			}
			this.processTreeSelectionEvent = false;
			if (this.managedTree != null) {
				this.managedTree.insertedNode(path, keysValues, this.locator, childSelect);
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger.debug("Inserted node error", e);
			} else {
				FormManager.logger.trace(null, e);
			}
		} finally {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				FormManager.logger
				.debug("Tree selection event processing enabled after inserting a new node (FormManager)");
			}
			this.processTreeSelectionEvent = true;
		}
	}

	/**
	 * This method calls the deletedNode method of the <code>Tree</code> associated to this
	 * <code>FormManager</code>
	 * @param path <code>TreePath</code> to be deleted.
	 * @param keysValues a <code>Hashtable</code> with the keys of the record to be deleted.
	 * @param select true the next node will be selected
	 */
	@Override
	public void deletedNode(final TreePath path, final Map keysValues, final boolean select) {
		if (this.managedTree != null) {
			this.checkModified = false;
			try {
				this.managedTree.deletedNode(path, keysValues, select);
			} catch (final Exception e) {
				FormManager.logger.trace(null, e);
			} finally {
				this.checkModified = true;
			}
		}
	}

	/**
	 * Updates the node of the <code>Tree</code> associate to this <code>FormManager</code>.
	 * @param path <code>TreePath</code> to be updated.
	 * @param attributesValues <code>Hashtable</code> with the values to be updated.
	 * @param keysValues <code>Hashtable</code> with the keys of the record which will be updated.
	 */
	@Override
	public void updatedNode(final TreePath path, final Map attributesValues, final Map keysValues) {
		this.checkModified = false;
		if (this.managedTree != null) {
			this.processTreeSelectionEvent = false;
			try {
				this.managedTree.updatedNode(path, attributesValues, keysValues, this.locator);
			} catch (final Exception e) {
				FormManager.logger.trace(null, e);
			}
			this.processTreeSelectionEvent = true;
		}
		this.checkModified = true;
	}

	/**
	 * Updates the node of the <code>Tree</code> associate to this <code>FormManager</code>.
	 * @param path <code>TreePath</code> to be updated.
	 * @param attributesValues <code>Hashtable</code> with the values to be updated.
	 * @param keysValues <code>Hashtable</code> with the keys of the record which will be updated.
	 * @param select true if the updated node will be selected.
	 */

	@Override
	public void updatedNode(final TreePath path, final Map attributesValues, final Map keysValues, final boolean select) {
		this.checkModified = false;
		if (this.managedTree != null) {
			// Selection events are processed. It can be necessary to update the
			// form data when selection
			// of node is possible (select=true).
			if (select) {
				this.processTreeSelectionEvent = false;
			}
			try {
				this.managedTree.updatedNode(path, attributesValues, keysValues, this.locator);
			} catch (final Exception e) {
				FormManager.logger.trace(null, e);
			}
			if (select) {
				this.processTreeSelectionEvent = true;
			}
		}
		this.checkModified = true;
	}

	@Override
	public void initPermissions() {
		// Nothing
	}

	/**
	 * Gets the attribute value into the <code>OTreeNode</code> that is passed as entry parameter. This
	 * method search this attribute into the <code>OTreeNode</code> and its parents.
	 * @param attribute the attribute to be found.
	 * @param node <code>OTreeNode</code> where the search of the attribute value will start in.
	 * @return a attribute value.
	 */

	@Override
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
	 * Enables/disables the dispatcher the tree selection event.
	 * @param process true the dispatcher is enabled
	 */

	@Override
	public synchronized void setProcessingTreeSelectionEvent(final boolean process) {
		this.processTreeSelectionEvent = process;
		if (com.ontimize.gui.ApplicationManager.DEBUG) {
			FormManager.logger.debug("***********************************  Processing tree selection event set to: "
					+ this.processTreeSelectionEvent);
		}
	}

	/**
	 * Enables/disables the check of modified form data
	 * @param check true the check of modifed form data is enabled.
	 */
	@Override
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
					FormManager.logger.debug("DataChanged: Form to show: " + fShowForm);
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
						FormManager.logger.debug("Setting the form data list to show...");
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
						fNewForm.updateDataFields(f.totalDataList);

						// Selects the record
						fNewForm.updateDataFields(e.getIndex());
						if (ApplicationManager.DEBUG) {
							FormManager.logger.debug("The form data list setted. Selected index: " + e.getIndex());
						}
					} catch (final Exception ex) {
						FormManager.logger.error(null, ex);
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
			FormManager.logger.debug("DynamicFormManager is null for the form: " + f.getArchiveName());
		}
	}

	@Override
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

	public void setCheckModified(final boolean enabled) {
		this.checkModified = enabled;
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
						FormManager.logger.error(null, e);
						form.message("Error reloading the form.  Maybe the xml is incorrect", Form.ERROR_MESSAGE, e);
					}

				}
			}
		} catch (final Exception e) {
			FormManager.logger.error(null, e);
		}
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	protected void registerApplicationPreferencesListener() {
		if (!this.preferenceListenersRegistered) {
			if ((this.aPreferences != null) && (this.managedTree != null)) {
				this.aPreferences.addApplicationPreferencesListener(this.managedTree);
				String user = null;
				if ((this.locator != null) && (this.locator instanceof ClientReferenceLocator)) {
					user = ((ClientReferenceLocator) this.locator).getUser();
				}
				this.managedTree.initPreferences(this.aPreferences, user);
				this.preferenceListenersRegistered = true;
			}
		}
	}

	@Override
	public synchronized boolean isLoaded() {

		return this.loadedTreeAndForm;
	}

	public TreeBuilder getTreeBuilder() {
		return this.treeBuilder;
	}

	/**
	 * Shows the form which file name is passed as entry parameter If this form isn't loaded, this
	 * method will load the form. Before the form will be shown, this method checks if the current form
	 * has modified data. This method call the {@link #showFormInEDTh} from EventDispatchThread.
	 * @param formName a <code>String</code> with the file name.
	 * @return true if the form is showed.
	 */

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
						final boolean bIn = FormManager.this.showFormInEDTh(formName);
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

	@Override
	public void addFormToContainer(final JPanel panelForm, final String formFileName) {
		if (this.parent.getComponentCount() == 0) {
			this.cardPanel.add(panelForm, formFileName, 0);
		} else {
			this.cardPanel.add(panelForm, formFileName, this.parent.getComponentCount() - 1);
		}
	}

	/**
	 * Shows the form which file name is passed as entry parameter
	 * @param form
	 * @see #showForm
	 * @return true if the form is showed.
	 */
	@Override
	public boolean showFormInEDTh(final String form) {
		if (!this.isLoaded()) {
			this.load();
		}
		if (this.checkVisiblePermission(form)) {
			if (!this.loadedList.contains(form)) {
				this.loadFormInEDTh(form);
			}
			// When data changes, an notice is produced
			if ((this.currentForm != null) && (this.getFormReference(this.currentForm) != null)) {
				if ((this.checkModified) && (this.checkModifiedDataForms)) {
					if (this.getFormReference(this.currentForm).checkModifiedData()) {
						if (this.getFormReference(this.currentForm)
								.message(BaseFormManager.M_MODIFIED_DATA, Form.QUESTION_MESSAGE) == Form.YES) {
							return false;
						}
					}
				}
			}
			((CardLayout) this.cardPanel.getLayout()).show(this.cardPanel, form);
			this.currentForm = form;
			return true;
		} else {
			return false;
		}
	}

}
