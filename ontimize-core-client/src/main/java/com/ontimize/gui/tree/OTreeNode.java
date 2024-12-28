package com.ontimize.gui.tree;

import java.lang.reflect.Constructor;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DynamicFormManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;

/**
 * Main class that represents a tree node. It implements <code>Comparable</code> to allow order the
 * nodes. These ones could be organizational or data nodes. Organizational nodes creates the tree
 * structure and data nodes are correspondent with database records. <br>
 * <br>
 * XML parameters in constructor OTreeNode(Map)
 *
 * @since 5.2000 Default implementation
 * @since 5.2060EN Pageable tree.
 * @author Imatia Innovation SL
 */
public class OTreeNode extends DefaultMutableTreeNode implements Comparable, Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(OTreeNode.class);

	public static final String THERE_ARENT_RESULTS_KEY = "tree.there_are_no_results";

	public static final String THERE_ARENT_RESULTS_KEY_es_ES = "tree.there_are_no_results";

	public static final String ENTITY = "entity";

	public static final String KEYS = "keys";

	public static final String KEYS_VALUES = "keysvalues";

	public static final String ATTR = "attr";

	public static final String TEXT = "text";

	public static final String FORM = "form";

	public static final String ORG = "org";

	public static final String PARENT_KEYS = "parentkeys";

	public static final String ICON_ORG = "iconorg";

	public static final String ICON_NO_ORG = "iconnoorg";

	public static final String PAGEABLE_ICON = "pageableicon";

	public static final String ID = "id";

	public static final String SEPARATOR = "separator";

	public static final String SORTATTR = "sortattr";

	/**
	 * Parameter to order nodes (sort="desc" / sort="asc"). By default, "asc"
	 *
	 * @Since 5.2077EN-0.3
	 */
	public static final String SORT = "sort";

	public static final String DESC = "desc";

	public static final String FIX_ATTR = "fixattr";

	public static final String HIDE_ATTR = "hideattr";

	public static final String CAN_DELETE = "candelete";

	public static final String DYNAMIC_FORM = "dinamicform";

	public static final String DYNAMICFORM = "dynamicform";

	public static final String DYNAMIC = "dynamic";

	public static final String CLASSIFY_NODE = "classifynode";

	public static final String QUERY_ROWS = "queryrows";

	public static boolean defaultPageableEnabled = false;

	protected String shownForm = null;

	protected String entityName = null;

	protected String shownAttribute = null;

	protected Object text = null;

	protected boolean organization = true;

	protected List keyList = new Vector(2);

	protected String keysString = null;

	protected Map keysValues = new Hashtable(2);

	protected Map associatedDataField = new Hashtable(2);

	/**
	 * List that contains associatedFields(parentKeys) ordered.
	 */
	protected List associatedOrderedDataField = new Vector(1);

	protected String associateDataFieldString = null;

	protected String stringFixAttr = null;

	protected String stringHideAttr = null;

	protected List hiddenAttributes = new Vector(0, 1);

	protected boolean canDelete = false;

	protected String cachedText = "";

	protected boolean classifyNode = false;

	protected boolean dynamic = false;

	protected Map attributeEquivalences = null;

	protected Map queryResult = null;

	protected String separator = " ";

	protected boolean ascending = true;

	protected List shownAttributeList = new Vector(1);

	protected String attrString = null;

	protected boolean orderToAdd = true;

	protected ImageIcon iconorg = null;

	protected ImageIcon iconnoorg = null;

	protected ImageIcon pageableIcon = null;

	protected String iconorgStr = null;

	protected String iconnoorgStr = null;

	protected boolean emptyNode = false;

	protected boolean remark = false;

	protected boolean pageableEnabled = OTreeNode.defaultPageableEnabled;

	protected String id = null;

	protected String orderByAttribute = null;

	protected List fixAtributtes = new Vector(0);

	protected String dynamicFormClass = null;

	protected DynamicFormManager dynamicFormManager = null;

	protected ResourceBundle resourceArchive = null;

	protected boolean leaf = false;

	protected boolean overrideLeaf = false;

	// Comparator locale sensitive
	protected static Collator comparator = Collator.getInstance();

	protected static Locale componentLocale = Locale.getDefault();

	protected List renderTime = null;

	protected int rowsNumberToQuery = -1;

	protected static SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

	protected static SimpleDateFormat dfH = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();

	protected int totalCount = 0;

	protected Map parameters = null;

	protected Comparator oTreeNodeComparator = new Comparator<TreeNode>() {
		@Override
		public int compare(final TreeNode arg0, final TreeNode arg1) {
			if (arg0 instanceof OTreeNode) {
				return ((OTreeNode) arg0).compareTo(arg1);
			}
			return -1;
		}
	};

	static {
		OTreeNode.df.applyPattern("dd/MM/yyyy");
		OTreeNode.dfH.applyPattern("hh:mm dd/MM/yyyy");
	}

	/**
	 * This method uses the <code>Hashtable</code> and creates the tree node.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td><i>attr1;...;attrn</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Indicates the field attribute. This fields will be displayed in node text.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Entity where tree data are obtained.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>keys</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Primary keys of entity.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>text</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Text to show in organizational nodes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>form</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Form to show when node is clicked.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>parentkeys</td>
	 *        <td><i>parentk1;associparentk1;...;parentkn;associparentkn</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Parent key and associated key for node. If parent key name and current-node key are
	 *        the same name, this field will be filled with pairs of fields with the same name.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>iconorg</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Icon for organizational nodes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>iconnoorg</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Icon for non-organizational nodes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>id</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Identifier for an organizational node and its children placed in the inmediatly
	 *        level.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>separator</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Separator character for attributes.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sortattr</td>
	 *        <td><i></td>
	 *        <td>The first attribute indicated in attr</td>
	 *        <td>no</td>
	 *        <td>Attribute to sort the tree.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>fixattr</td>
	 *        <td><i>fixattr1;fixattr2;...;fixattrn</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>List of attributes that will be fixed by tree node in associated form.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>hideattr</td>
	 *        <td><i>hideattr1;hideattr2;...;hideattrn</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Attributes that will be queried, but not showed in node.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>candelete</td>
	 *        <td><i>true/false</td>
	 *        <td>false</td>
	 *        <td>no</td>
	 *        <td>Indicates if a node can be deleted.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>dynamicform</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Name of class to show dynamic forms.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>rendertime</td>
	 *        <td><i>render1;render2;...;rendern</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Attribute to be renderized like a hour field.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>classifynode</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td></td>
	 *        </tr>
	 *        <tr>
	 *        <td>queryrows</td>
	 *        <td><i>positive values</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>For making pageable tree. Number of records that will be downloaded for each page.
	 *        (since 5.2060EN)</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sort</td>
	 *        <td><i>asc/desc</td>
	 *        <td>asc</td>
	 *        <td>no</td>
	 *        <td>Allow to sort current node ascending/descending. Before this version, ascending sort
	 *        was set for all nodes(since 5.2077EN-0.3)</td>
	 *        </tr>
	 *        </table>
	 */
	public OTreeNode(final Map parameters) {
		this.parameters = parameters;
		final Object render = parameters.get("rendertime");
		if (render != null) {
			this.renderTime = ApplicationManager.getTokensAt(render.toString(), ";");
		}

		final Object id = parameters.get(OTreeNode.ID);
		if (id != null) {
			if (id instanceof String) {
				this.id = (String) id;
			} else {
				this.id = id.toString();
			}
		}


		// Check if it is an organizational node
		this.organization = ParseUtils.getBoolean((String) parameters.get(OTreeNode.ORG), true);

		this.canDelete = ParseUtils.getBoolean((String) parameters.get(OTreeNode.CAN_DELETE), false);

		final Object fixattr = parameters.get(OTreeNode.FIX_ATTR);
		if (fixattr != null) {
			this.stringFixAttr = fixattr.toString();
			this.fixAtributtes = ApplicationManager.getTokensAt(this.stringFixAttr, ";");
		}

		this.dynamic = ParseUtils.getBoolean((String) parameters.get(OTreeNode.DYNAMIC), false);

		this.classifyNode = ParseUtils.getBoolean((String) parameters.get(OTreeNode.CLASSIFY_NODE), false);


		final Object separator = parameters.get(OTreeNode.SEPARATOR);
		if (separator != null) {
			if (separator instanceof String) {
				if (((String) separator).length() > 0) {
					this.separator = (String) separator;
				}
			} else {
				if (separator.toString().length() > 0) {
					this.separator = separator.toString();
				}
			}
		}

		final Object ent = parameters.get(OTreeNode.ENTITY);
		if (ent != null) {
			this.entityName = ent.toString();
		}

		this.configureKeys(parameters);
		this.configureParentKeys(parameters);
		this.configureAttrs(parameters);
		this.configureHideAttrs(parameters);
		this.configureText(parameters);

		final Object form = parameters.get(OTreeNode.FORM);
		if (form == null) {
		} else {
			this.shownForm = form.toString();
		}

		final Object oKeysValues = parameters.get(OTreeNode.KEYS_VALUES);
		if (oKeysValues != null) {
			try {
				this.keysValues = (Map) oKeysValues;
			} catch (final Exception e) {
				OTreeNode.logger.error("Error in parameter keysvalues. Fatal Error.", e);
			}
		}

		// Icon parameter
		final Object iconorg = parameters.get(OTreeNode.ICON_ORG);
		if (iconorg != null) {
			final String iconFile = iconorg.toString();
			this.iconorgStr = iconFile;
			this.iconorg = ImageManager.getIcon(iconFile);
		}

		// iconnoorg parameter
		final Object iconnoorg = parameters.get(OTreeNode.ICON_NO_ORG);
		if (iconnoorg == null) {
			this.iconnoorg = this.iconorg;
		} else {
			final String iconFile = iconnoorg.toString();
			this.iconnoorgStr = iconFile;
			this.iconnoorg = ImageManager.getIcon(this.iconnoorgStr);
		}

		// iconnoorg parameter
		final Object pageableicon = parameters.get(OTreeNode.PAGEABLE_ICON);
		if (pageableicon != null) {
			this.pageableIcon = ImageManager.getIcon(pageableicon.toString());
		}

		final Object sortattr = parameters.get("sortattr");
		if (sortattr != null) {
			final String sattr = sortattr.toString();
			if (this.shownAttributeList.contains(sattr)) {
				this.orderByAttribute = sattr;
			}
		}

		/**
		 * Since 5.2077EN-0.3
		 */
		final Object sort = parameters.get(OTreeNode.SORT);
		if ((sort != null) && OTreeNode.DESC.equalsIgnoreCase(sort.toString())) {
			this.ascending = false;
		}

		try {
			this.rowsNumberToQuery = ParseUtils.getInteger((String) parameters.get(OTreeNode.QUERY_ROWS), -1);
			if (this.rowsNumberToQuery != -1) {
				this.pageableEnabled = true;
			}
		} catch (final Exception e) {
			OTreeNode.logger.error(this.getClass().toString() + ": Error in 'queryrows' parameter: " + e.getMessage(),
					e);
		}

		this.configureDynamicForm(parameters);
		this.setUserObject(null);
		this.updateNodeTextCache();
	}

	protected void configureDynamicForm(final Map parameters) {
		Object oDynamicForm = parameters.get(OTreeNode.DYNAMIC_FORM);
		if (oDynamicForm == null) {
			oDynamicForm = parameters.get(OTreeNode.DYNAMICFORM);
		}
		if (oDynamicForm != null) {
			try {
				final String sDFClass = oDynamicForm.toString();
				final Class cDSClass = Class.forName(sDFClass);
				final DynamicFormManager dfManager = (DynamicFormManager) cDSClass.newInstance();
				dfManager.setBaseName(this.shownForm);

				this.dynamicFormClass = sDFClass;
				this.dynamicFormManager = dfManager;
				OTreeNode.logger.debug("Set class " + sDFClass + " as FormDinamic to the node.");
			} catch (final Exception e) {
				OTreeNode.logger.error("Error loading class for dynamicform", e);
				OTreeNode.logger.error(null, e);
			}
		}
	}

	protected void configureText(final Map parameters) {
		final Object text = parameters.get(OTreeNode.TEXT);
		if (text == null) {
			this.text = "";
		} else {
			this.text = text;
		}
	}

	protected void configureHideAttrs(final Map parameters) {
		final Object hideattr = parameters.get(OTreeNode.HIDE_ATTR);
		if (hideattr != null) {
			this.stringHideAttr = hideattr.toString();
			final StringTokenizer st = new StringTokenizer(hideattr.toString(), ";");
			while (st.hasMoreTokens()) {
				this.hiddenAttributes.add(st.nextToken());
			}
		}
	}

	protected void configureAttrs(final Map parameters) {
		final Object attr = parameters.get(OTreeNode.ATTR);
		if (attr != null) {
			this.attrString = attr.toString();
			if (this.attrString.length() == 0) {
			} else {
				final StringTokenizer tokenizer = new StringTokenizer(attr.toString(), ";");
				int i = 0;
				while (tokenizer.hasMoreTokens()) {
					this.shownAttributeList.add(i, tokenizer.nextToken());
					i++;
				}
				this.orderByAttribute = this.shownAttributeList.get(0).toString();
			}
		}
	}

	protected void configureParentKeys(final Map parameters) {
		final Object parentKeys = parameters.get(OTreeNode.PARENT_KEYS);
		if (parentKeys == null) {
			this.associateDataFieldString = "";
		} else {
			// Pairs of strings.
			// Separated by ;
			final String sFieldNames = parentKeys.toString();
			this.associateDataFieldString = sFieldNames;

			final List vFields = ApplicationManager.getTokensAt(sFieldNames, ";");
			if ((vFields.size() % 2) != 0) {
				OTreeNode.logger.debug("Error in node parentkeys. Format = parentKey1;key1;paretkey2;key2");
			}
			for (int i = 0; i < (vFields.size() - 1); i = i + 2) {
				this.associatedDataField.put(vFields.get(i), vFields.get(i + 1));
				this.associatedOrderedDataField.add(vFields.get(i));
			}
		}
	}

	protected void configureKeys(final Map parameters) {
		final Object keys = parameters.get(OTreeNode.KEYS);
		if (keys == null) {
			if (ApplicationManager.DEBUG) {
				OTreeNode.logger
				.debug("INFO: Node entity: " + this.entityName + " : Parameter keys not found. Fatal Error.");
			}
		} else {
			if (keys instanceof String) {
				this.keysString = (String) keys;
			} else {
				this.keysString = keys.toString();
			}
			final StringTokenizer st = new StringTokenizer(this.keysString, ";");
			while (st.hasMoreTokens()) {
				this.keyList.add(this.keyList.size(), st.nextToken());
			}
		}
	}

	public int getRowsNumberToQuery() {
		return this.rowsNumberToQuery;
	}

	@Override
	public int compareTo(final Object object) {
		if (object instanceof OTreeNode) {
			if (this.isPageableEnabled()) {
				return +1;
			}
			final Object oAttrValue = this.getValueForAttribute(this.orderByAttribute);
			final Object oAttrValueObject = ((OTreeNode) object).getValueForAttribute(this.orderByAttribute);
			if (oAttrValue == null) {
				return -1;
			}
			if (oAttrValueObject == null) {
				return +1;
			}
			if ((!(oAttrValue instanceof String)) && (!(oAttrValueObject instanceof String))
					&& (oAttrValue instanceof Comparable) && (oAttrValueObject instanceof Comparable)) {
				if (this.ascending) {
					return ((Comparable) oAttrValue).compareTo(oAttrValueObject);
				} else {
					return -((Comparable) oAttrValue).compareTo(oAttrValueObject);
				}

			} else {
				final String sNodeText = oAttrValue.toString();
				final String sNodeTextObject = oAttrValueObject.toString();

				if (this.ascending) {
					return OTreeNode.comparator.compare(sNodeText, sNodeTextObject);
				} else {
					return -OTreeNode.comparator.compare(sNodeText, sNodeTextObject);
				}
			}
		} else {
			return -1;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	public void setAttributes(final List attributes) {
		this.shownAttributeList = attributes;
	}

	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	public void setForm(final String form) {
		this.shownForm = form;
	}

	public void setSeparator(final String separator) {
		this.separator = separator;
		this.updateNodeTextCache();
	}

	@Override
	public Object clone() {
		final Map parameters = new Hashtable(0);
		Object current = null;
		try {
			final Class nodeClass = this.getClass();
			final Constructor constructor = nodeClass.getConstructor(new Class[] { Hashtable.class });
			current = constructor.newInstance(new Object[] { parameters });
		} catch (final Exception ex) {
			OTreeNode.logger.error(null, ex);
		}
		if (!(current instanceof OTreeNode)) {
			return null;
		}

		final OTreeNode otNode = (OTreeNode) current;
		otNode.setClassifyNode(this.classifyNode);
		otNode.setText(this.getText());
		otNode.setAttributes(this.shownAttributeList);
		otNode.setEntityName(this.getEntityName());
		otNode.setForm(this.getForm());
		otNode.setOrganizational(this.organization);
		otNode.setSeparator(this.getSeparator());
		otNode.id = this.getId();
		otNode.setKeys(this.keyList);
		otNode.setAssociatedDataField(this.associatedDataField);
		otNode.setAssociatedOrderedDataField(this.associatedOrderedDataField);
		otNode.attrString = this.attrString;
		otNode.shownAttribute = this.shownAttribute;
		otNode.associateDataFieldString = this.associateDataFieldString;
		otNode.keysString = this.keysString;
		otNode.stringFixAttr = this.stringFixAttr;
		otNode.stringHideAttr = this.stringHideAttr;
		otNode.iconnoorg = this.iconnoorg;
		otNode.iconorg = this.iconorg;
		otNode.iconnoorgStr = this.iconnoorgStr;
		otNode.iconorgStr = this.iconorgStr;
		otNode.orderByAttribute = this.orderByAttribute;
		otNode.fixAtributtes = this.fixAtributtes;
		otNode.hiddenAttributes = this.hiddenAttributes;
		otNode.canDelete = this.canDelete;
		otNode.dynamicFormManager = this.getDynamicFormManager();
		otNode.dynamicFormClass = this.dynamicFormClass;
		otNode.pageableEnabled = this.isPageableEnabled();
		otNode.rowsNumberToQuery = this.rowsNumberToQuery;
		otNode.parameters = this.parameters;
		otNode.resourceArchive = this.resourceArchive;
		otNode.cachedText = this.cachedText;
		otNode.setDynamic(this.dynamic);
		otNode.setOverrideLeaf(this.overrideLeaf, this.leaf);
		return otNode;

	}

	@Override
	public String toString() {
		if (this.isOrganizational()) {
			String text = null;
			if (this.resourceArchive != null) {
				text = ApplicationManager.getTranslation(this.getText(), this.resourceArchive);
			} else {
				text = ApplicationManager.getTranslation(this.getText());
			}
			return text;

		}
		if (this.getUserObject() == null) {
			return "";
		}
		return this.getUserObject().toString();
	}

	public String getForm() {
		return this.shownForm;
	}

	public void setToString(final String s) {
		this.cachedText = s;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public List getKeys() {
		return this.keyList;
	}

	public List getParentKeys() {
		final List v = new Vector();
		final Enumeration c = Collections.enumeration(this.associatedDataField.keySet());
		while (c.hasMoreElements()) {
			v.add(this.associatedDataField.get(c.nextElement()));
		}
		return v;
	}

	public void setKeysValues(final Object key, final Object value) {
		if (this.keyList.contains(key)) {
			this.keysValues.put(key, value);
			this.updateNodeTextCache();
		}
	}

	/**
	 * Returns a <code>Hashtable</code> where keys are attributes specified in keysValues, and values
	 * are their correspondent values.
	 * @return the <code>Hashtable</code>
	 */
	public Map getKeysValues() {
		final Map hNodeKeys = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(this.keysValues.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			if (this.keyList.contains(oKey)) {
				hNodeKeys.put(oKey, this.keysValues.get(oKey));
			}
		}
		return hNodeKeys;
	}

	/**
	 * Returns node keyvalues.
	 * @return a <code>Hashtable</code> with keyvalues
	 */
	public Map getNodeData() {
		return ObjectTools.clone(this.keysValues);
	}

	public String getSortAttr() {
		return this.orderByAttribute;
	}

	public String[] getAttributes() {
		final String[] attributes = new String[this.shownAttributeList.size()];
		for (int i = 0; i < this.shownAttributeList.size(); i++) {
			attributes[i] = new String(this.shownAttributeList.get(i).toString());
		}
		return attributes;
	}

	public void setAttribute(final String attribute, final Object value) {
		if (this.attrString.indexOf(attribute) >= 0) {
			if (value == null) {
				if (ApplicationManager.DEBUG) {
					OTreeNode.logger
					.debug(this.getClass().toString() + " : NULL Value cann't be established to node attribute");
				}
				return;
			}
			this.keysValues.put(attribute, value);
			this.updateNodeTextCache();
		} else {
			if (ApplicationManager.DEBUG) {
				OTreeNode.logger.debug(this.getClass().toString() + " : This node hasn't " + attribute + " attribute ");
			}
		}
	}

	public boolean isOrganizational() {
		return this.organization;
	}

	/**
	 * Get a copy of the node and all the children
	 */
	public OTreeNode cloneNodeAndChildren() {
		final OTreeNode n = this.cloneThisAndChildren();
		return n;
	}

	protected OTreeNode cloneThisAndChildren() {
		final OTreeNode otNode = (OTreeNode) this.clone();

		for (int i = 0; i < this.getChildCount(); i++) {
			final OTreeNode aux = (OTreeNode) this.getChildAt(i);
			otNode.add(aux.cloneNodeAndChildren());
		}
		return otNode;
	}

	public void setOverrideLeaf(final boolean override, final boolean leaf) {
		this.overrideLeaf = override;
		this.leaf = leaf;
	}

	@Override
	public boolean isLeaf() {
		if (this.overrideLeaf) {
			return this.leaf;
		}
		if (this.isOrganizational()) {
			return false;
		} else {
			if (this.getChildCount() > 0) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * Method to obtain children of a node.
	 * @return a <code>Vector</code> with children
	 */
	public List getChildren() {
		return this.children;
	}

	/**
	 * Overrides the method add to insert a node in a sorted tree.
	 */
	@Override
	public void add(final MutableTreeNode node) {
		// Add the node in the appropriate position if it is a data node.
		// If node is organizational node then sort is not needed
		if ((node instanceof OTreeNode) && (this.orderToAdd)) {
			if (!((OTreeNode) node).isOrganizational()) {
				((OTreeNode) node).setSortOrder(this.ascending);
				super.add(node);
				Collections.sort(this.children, this.oTreeNodeComparator);
			} else {
				super.add(node);
			}
		} else {
			super.add(node);
		}
	}

	public Map getAssociatedDataField() {
		return this.associatedDataField;
	}

	public void setAssociatedDataField(final Map associatedFields) {
		this.associatedDataField = associatedFields;
	}

	public List getAssociatedOrderedDataField() {
		return this.associatedOrderedDataField;
	}

	public void setAssociatedOrderedDataField(final List associatedOrderedDataField) {
		this.associatedOrderedDataField = associatedOrderedDataField;
	}

	public String getParentKeyNameInParentNode(final String parentKey) {
		final Enumeration c = Collections.enumeration(this.associatedDataField.keySet());
		while (c.hasMoreElements()) {
			final Object oParentKey = c.nextElement();
			final Object oKey = this.associatedDataField.get(oParentKey);
			if (oKey.equals(parentKey)) {
				return oParentKey.toString();
			}
		}
		return null;
	}

	public void setKeys(final List keys) {
		this.keyList = keys;
	}

	public Object getValueForAttribute(final Object attribute) {
		final Object oAttribute = this.keysValues.get(attribute);
		if (oAttribute == null) {
			if (ApplicationManager.DEBUG) {
				OTreeNode.logger.debug(this.keysValues.toString());
			}

			if (ApplicationManager.DEBUG) {
				OTreeNode.logger.debug(attribute.toString() + " attribute hasn't been found.");
			}
			return null;
		} else {
			return oAttribute;
		}
	}

	public String getKeysString() {
		return this.keysString;
	}

	public String getStringAssociatedDataField() {
		return this.associateDataFieldString;
	}

	public String getAttr() {
		return this.attrString;
	}

	public void setText(final String textToShow) {
		this.text = textToShow;
		this.updateNodeTextCache();
	}

	/**
	 * Establishes key values and attributes for this node, that is 'attr' and 'keys' specified in xml
	 * definition.
	 * @param keysValues
	 */
	public void setKeysValues(final Map keysValues) {
		this.keysValues = keysValues;
		this.updateNodeTextCache();
	}

	public void setQueryResult(final Map result) {
		this.queryResult = result;
	}

	public Map getQueryResult() {
		return this.queryResult;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resourceArchive = resources;
		this.updateNodeTextCache();
	}

	/**
	 * Allows to disable sorting in nodes.
	 */
	public void disableNodeSorting() {
		this.orderToAdd = false;
	}

	/**
	 * Enables sorting in node.
	 */
	public void enableNodeSorting() {
		this.orderToAdd = true;
	}

	public void setSortAttr(final String sortAttr) {
		if (this.shownAttributeList.contains(sortAttr) && !this.hiddenAttributes.contains(sortAttr)) {
			this.orderByAttribute = sortAttr;
		}
	}

	/**
	 * Sorts child nodes with order: ascending or descending.
	 */
	public void sortNow() {
		this.setSortOrder(this.ascending);
	}

	public boolean getAscending() {
		return this.ascending;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		if (!OTreeNode.componentLocale.equals(l)) {
			OTreeNode.comparator = Collator.getInstance(l);
			OTreeNode.componentLocale = l;
		}
	}

	public void setSortOrder(final boolean ascendingSort) {
		this.ascending = ascendingSort;
		if (this.children == null) {
			return;
		}
		// Update
		if (this.children != null) {
			// If it is an organizational node then sort the children
			if (this.organization) {
				// It has to establish child sorting
				for (int i = 0; i < this.children.size(); i++) {
					final Object oAuxChildNode = this.getChildAt(i);
					if (oAuxChildNode instanceof OTreeNode) {
						final OTreeNode oChildNode = (OTreeNode) oAuxChildNode;
						if (this.orderByAttribute != null) {
							oChildNode.setSortAttr(this.orderByAttribute);
						}
						oChildNode.setSortOrder(ascendingSort);
					}
				}
				Collections.sort(this.children, this.oTreeNodeComparator);
			}
		}
	}

	@Override
	protected void finalize() throws Throwable {
		this.queryResult = null;
		this.text = null;
		super.finalize();
	}

	/**
	 * Returns the icon to use with this node. In other case returns null. It is used by class
	 * {@link TreeCellRenderer}.
	 * @return the image icon
	 */
	public ImageIcon getIcon() {
		if (this.isOrganizational()) {
			return this.iconorg;
		} else {
			return this.iconnoorg;
		}
	}

	public String getIconOrg() {
		return this.iconorgStr;
	}

	public String getIconNoOrg() {
		return this.iconnoorgStr;
	}

	@Override
	public void setUserObject(final Object value) {
		super.setUserObject(value);
	}

	@Override
	public Object getUserObject() {
		return this.cachedText;
	}

	public void setAttributeText(final String attr, final String equivalence) {
		if (this.shownAttributeList.contains(attr) || this.hiddenAttributes.contains(attr)) {
			if (this.attributeEquivalences == null) {
				this.attributeEquivalences = new Hashtable(5);
			}
			this.attributeEquivalences.put(attr, equivalence);
			this.updateNodeTextCache();
		}
	}

	public String getId() {
		return this.id;
	}

	public void setEmptyNode(final boolean emptyNode) {
		this.emptyNode = emptyNode;
	}

	public boolean isEmptyNode() {
		return this.emptyNode;
	}

	public String getSeparator() {
		return this.separator;
	}

	public String getOrderByAttribute() {
		return this.orderByAttribute;
	}

	public String getText() {
		return this.text.toString();
	}

	/**
	 * Indicates when node must show a special icon, for example, when its data have changed and tree
	 * has not been updated yet.
	 * @param remark the condition of remark
	 */
	public void setRemark(final boolean remark) {
		this.remark = remark;
	}

	public boolean getRemark() {
		return this.remark;
	}

	public boolean canDelete() {
		return this.canDelete;
	}

	public List getFixAttributes() {
		return ObjectTools.clone(this.fixAtributtes);
	}

	public String getFixAttr() {
		return this.stringFixAttr;
	}

	public String getHideAttr() {
		return this.stringHideAttr;
	}

	public void setOrganizational(final boolean o) {
		this.organization = o;
	}

	protected void updateNodeTextCache() {
		if (this.isOrganizational() || this.isEmptyNode()) {
			try {
				if (this.resourceArchive != null) {
					this.cachedText = this.resourceArchive.getString(this.text.toString());
				} else {
					this.cachedText = this.text.toString();
				}
			} catch (final Exception e) {
				if (this.text.equals(OTreeNode.THERE_ARENT_RESULTS_KEY)) {
					this.cachedText = OTreeNode.THERE_ARENT_RESULTS_KEY_es_ES;
				} else {
					this.cachedText = this.text.toString();
				}
				if (ApplicationManager.DEBUG) {
					OTreeNode.logger.debug(e.getMessage(), e);
				}
			}
			if (!this.isEmptyNode()) {
				this.cachedText = this.cachedText + "                  ";
			}
			this.setUserObject(this.cachedText);
			return;
		}
		if ((this.shownAttributeList == null) || this.shownAttributeList.isEmpty()) {
			this.cachedText = "";
			this.setUserObject(this.cachedText);
			return;
		}
		final StringBuilder string = new StringBuilder();
		// Return the attributes to show using the separator
		int discarded = 0;
		final int shownAttributes = this.shownAttributeList.size() - this.hiddenAttributes.size();
		int nodiscarded = 0;
		for (int i = 0; i < this.shownAttributeList.size(); i++) {
			final Object at = this.shownAttributeList.get(i);
			if (this.hiddenAttributes.contains(at)) {
				discarded++;
				continue;
			} else {
				nodiscarded++;
			}
			if ((this.attributeEquivalences == null) || !this.attributeEquivalences.containsKey(at)) {
				final Object oAttributeValue = this.keysValues.get(at);
				if (oAttributeValue != null) {
					// If type is Date or Timestamp then use a DateDataField
					if (oAttributeValue instanceof java.util.Date) {
						if ((this.renderTime == null) || !this.renderTime.contains(at)) {
							string.append(OTreeNode.df.format(oAttributeValue) + " ");
						} else {
							string.append(OTreeNode.dfH.format(oAttributeValue) + " ");
						}
					} else {
						string.append(oAttributeValue.toString());
						if (nodiscarded < shownAttributes) {
							string.append(this.separator);
						}
					}
				}
			} else {
				string.append(this.attributeEquivalences.get(at).toString());
				if (nodiscarded < shownAttributes) {
					string.append(this.separator);
				}
			}
		}
		this.cachedText = string.toString();
		this.setUserObject(this.cachedText);
	}

	public String getDynamicFormManagerClass() {
		return this.dynamicFormClass;
	}

	public DynamicFormManager getDynamicFormManager() {
		return this.dynamicFormManager;
	}

	public void setDynamicFormManager(final DynamicFormManager dfm) {
		this.dynamicFormManager = dfm;
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector(1);
		if (this.isOrganizational() || this.isEmptyNode()) {
			v.add(this.text);
		}
		return v;
	}

	public List getShownAttributeList() {
		return this.shownAttributeList;
	}

	public List getVisibleAttributes() {
		final List v = new Vector();
		for (int i = 0; i < this.shownAttributeList.size(); i++) {
			if (!this.hiddenAttributes.contains(this.shownAttributeList.get(i))) {
				v.add(this.shownAttributeList.get(i));
			}
		}
		return v;
	}

	public boolean isClassifyNode() {
		return this.classifyNode;
	}

	public void setClassifyNode(final boolean classify) {
		this.classifyNode = classify;
	}

	public boolean isDynamic() {
		return this.dynamic;
	}

	public void setDynamic(final boolean d) {
		this.dynamic = d;
	}

	protected int count = 0;

	public void setCount(final int c) {
		this.count = c;
	}

	public int getCount() {
		return this.count;
	}

	public boolean isPageableEnabled() {
		return this.pageableEnabled;
	}

	public Map getParameters() {
		return ObjectTools.clone(this.parameters);
	}

	public void setTotalCount(final int totalRecordCount) {
		this.totalCount = totalRecordCount;
	}

	public int getTotalCount() {
		return this.totalCount;
	}

	public ImageIcon getPageableIcon() {
		return this.pageableIcon;
	}

	public void setPageableIcon(final ImageIcon pageableIcon) {
		this.pageableIcon = pageableIcon;
	}

}
