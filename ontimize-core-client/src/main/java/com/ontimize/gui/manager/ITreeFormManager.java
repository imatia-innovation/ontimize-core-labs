package com.ontimize.gui.manager;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.TreePath;

import com.ontimize.gui.FormManager;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.gui.tree.Tree;

public interface ITreeFormManager
extends IFormManager, TreeSelectionListener, TreeWillExpandListener, TreeExpansionListener {

	/** XML Attribute */
	public static final String TREE_CLASS = "treeclass";

	/** XML Attribute */
	public static final String TREE_BUILDER = "treebuilder";

	/** XML Attribute */
	public static final String TREE = "tree";

	public Tree getTree();

	public void setProcessingTreeSelectionEvent(boolean process);

	public void updateParentkeys(OTreeNode node);

	/**
	 * This method call the {@link FormManager#insertedNode(TreePath, Hashtable)} and the new node will
	 * be selected if the condition childSelect is true.
	 * @param path
	 * @param keysValues
	 * @param childSelect
	 */
	public void insertedNode(TreePath path, Map keysValues, boolean childSelect);

	/**
	 * Inserts a new node into the <code>Tree</code> associated to this <code>FormManager</code>
	 * @param path
	 * @param keysValues
	 */
	public void insertedNode(TreePath path, Map keysValues);

	/**
	 * This method does that the parent node of current selected node will be selected if the current
	 * selected node is a data node.
	 */
	public void dataInserted();

	/**
	 * Updates the node of the <code>Tree</code> associate to this <code>FormManager</code>.
	 * @param path <code>TreePath</code> to be updated.
	 * @param attributesValues <code>Hashtable</code> with the values to be updated.
	 * @param keysValues <code>Hashtable</code> with the keys of the record which will be updated.
	 */
	public void updatedNode(TreePath path, Map attributesValues, Map keysValues);

	/**
	 * Updates the node of the <code>Tree</code> associate to this <code>FormManager</code>.
	 * @param path <code>TreePath</code> to be updated.
	 * @param attributesValues <code>Hashtable</code> with the values to be updated.
	 * @param keysValues <code>Hashtable</code> with the keys of the record which will be updated.
	 * @param select true if the updated node will be selected.
	 */
	public void updatedNode(TreePath path, Map attributesValues, Map keysValues, boolean select);

	/**
	 * This method calls the deletedNode method of the <code>Tree</code> associated to this
	 * <code>FormManager</code>
	 * @param path <code>TreePath</code> to be deleted.
	 * @param keysValues a <code>Hashtable</code> with the keys of the record to be deleted.
	 * @param select true the next node will be selected
	 */
	public void deletedNode(TreePath path, Map keysValues, boolean select);

}
