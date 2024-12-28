package com.ontimize.gui.tree;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class TreeInteractionManager {

	private static final Logger logger = LoggerFactory.getLogger(TreeInteractionManager.class);

	/**
	 * Reference to the tree.
	 */
	Tree tree = null;

	/**
	 * Reference to the locator.
	 */
	EntityReferenceLocator locator = null;

	public TreeInteractionManager(final Tree managedTree, final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	public void createOTreeNode(final OTreeNode parentNode) {
		try {
			final Entity entity = this.locator.getEntityReference(parentNode.getEntityName());
			// Needs an entity reference.
			if (entity == null) {
				return;
			}
			final List vAttributes = new Vector();
			final String[] sAttributes = parentNode.getAttributes();
			for (int i = 0; i < sAttributes.length; i++) {
				vAttributes.add(sAttributes[i]);
			}
			// For each parent field. we look for the value.
			// We looks for organizational nodes, moving across all branches.
			final List vOrganizationNodes = new Vector();
			for (int i = 0; i < parentNode.getChildCount(); i++) {
				// If exists a non-organizational node is due to they have not
				// deleted yet
				if (((OTreeNode) parentNode.getChildAt(i)).isOrganizational()) {
					vOrganizationNodes.add(parentNode.getChildAt(i));
				} else {
					return;
				}
			}

			// For tree nodes, moreover 'attr' we need the 'parentkeys' to
			// identify
			// the node.
			for (int i = 0; i < parentNode.getKeys().size(); i++) {
				vAttributes.add(parentNode.getKeys().get(i));
			}

			// In query, we have to consider the upper data nodes
			final Map hSearchKeysValues = new Hashtable();
			OTreeNode otnParentNode;
			if (!parentNode.isRoot()) {
				otnParentNode = (OTreeNode) parentNode.getParent();
				final Map hAssociatedFields = parentNode.getAssociatedDataField();
				final Map hKeysValues = otnParentNode.getKeysValues();
				// From keys and values, we get the associated field
				final Enumeration enumAssociatedFieldKeys = Collections.enumeration(hAssociatedFields.keySet());
				while (enumAssociatedFieldKeys.hasMoreElements()) {
					final Object oParentField = enumAssociatedFieldKeys.nextElement();
					final Object oChildField = hAssociatedFields.get(oParentField);
					hSearchKeysValues.put(oChildField, otnParentNode.getValueForAttribute(oParentField));
				}
			} else { // If it is the root, we look for all
			}
			try {
				final Map result = EntityResultUtils
						.toMap(entity.query(hSearchKeysValues, vAttributes, this.locator.getSessionId()));
				if (result.isEmpty()) {
					// If it is empty, remove all children.
					parentNode.removeAllChildren();
					final Map parameters = new Hashtable();
					parameters.put(OTreeNode.TEXT, OTreeNode.THERE_ARENT_RESULTS_KEY);
					parameters.put(OTreeNode.ATTR, "");
					parameters.put(OTreeNode.ENTITY, "");
					parameters.put(OTreeNode.ORG, "false");
					if (parentNode.getKeysString() != null) {
						parameters.put(OTreeNode.KEYS, parentNode.getKeysString());
					}
					if (parentNode.getStringAssociatedDataField() != null) {
						parameters.put(OTreeNode.PARENT_KEYS, parentNode.getStringAssociatedDataField());
					}
					final OTreeNode warningNode = new OTreeNode(parameters);
					parentNode.add(warningNode);
					return;
				}

				// Create child nodes of this node. But, these child nodes must
				// be
				// placed
				// like child nodes for all nodes that will be added.

				final List vResults = (List) result.get(parentNode.getAttributes()[0]);

				// Updates node result
				// parentNode.setQueryResult(result)
				// In associated form, update data.

				// Remove all children
				parentNode.removeAllChildren();
				for (int j = 0; j < vResults.size(); j++) {
					final Map parameters = new Hashtable();
					parameters.put(OTreeNode.TEXT, vResults.get(j));
					parameters.put(OTreeNode.ATTR, parentNode.getAttr());
					parameters.put(OTreeNode.ENTITY, vResults.get(j));
					parameters.put(OTreeNode.FORM, parentNode.getForm());
					parameters.put(OTreeNode.ORG, new Boolean(false));
					parameters.put(OTreeNode.SEPARATOR, parentNode.getSeparator());
					if (parentNode.getKeysString() != null) {
						parameters.put(OTreeNode.KEYS, parentNode.getKeysString());
					}
					if (parentNode.getStringAssociatedDataField() != null) {
						parameters.put(OTreeNode.PARENT_KEYS, parentNode.getStringAssociatedDataField());
					}
					final Map newNodeKeyValues = new Hashtable();
					final List vKeys = parentNode.getKeys();
					for (int k = 0; k < vKeys.size(); k++) {
						final List vKeyValues = (List) result.get(vKeys.get(k));
						if (vKeyValues != null) {
							newNodeKeyValues.put(vKeys.get(k), vKeyValues.get(j));
						}
					}
					parameters.put(OTreeNode.KEYS_VALUES, newNodeKeyValues);

					final OTreeNode oDataNode = new OTreeNode(parameters);
					parentNode.add(oDataNode);
					// Adds all organizational nodes
					for (int k = 0; k < vOrganizationNodes.size(); k++) {
						oDataNode.add(((OTreeNode) vOrganizationNodes.get(k)).cloneNodeAndChildren());
					}
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					TreeInteractionManager.logger.debug("Exception in tree interaction manager: " + e.getMessage(), e);
				}
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				TreeInteractionManager.logger.debug("Exception in tree interaction manager: " + e.getMessage(), e);
			}
		}
	}

	public void removeNotOrganizationalNode(final OTreeNode node) {
		// We have to move across the tree for deleting all data nodes.

		// If the node is organizational, it will contain data nodes.
		// If node is a data node, it will contain organizational nodes.

		// Child count must be greater than 1
		if (node.getChildCount() < 1) {
			return;
		}

		// Organizational node. We get the first child and create a List with
		// all children of this node.
		final List vChildNodes = new Vector();
		if (node.isOrganizational()) {
			final OTreeNode childNode = (OTreeNode) node.getChildAt(0);
			// For organizational nodes we do nothing
			if (childNode.isOrganizational()) {
				return;
			}
			// We get the organizational nodes and add them to the vector
			for (int i = 0; i < childNode.getChildCount(); i++) {
				vChildNodes.add(childNode.getChildAt(i));
				// Recursive
				this.removeNotOrganizationalNode((OTreeNode) childNode.getChildAt(i));
			}
			// All children of organizational nodes are deleted.
			node.removeAllChildren();
			// Adds children.
			for (int i = 0; i < vChildNodes.size(); i++) {
				node.add((OTreeNode) vChildNodes.get(i));
			}
		}
		// If this node is not organizational
		else {
			// Call to this method for all organizational nodes
			for (int i = 0; i < node.getChildCount(); i++) {
				if (((OTreeNode) node.getChildAt(i)).isOrganizational()) {
					this.removeNotOrganizationalNode((OTreeNode) node.getChildAt(i));
				}
			}
		}
	}

}
