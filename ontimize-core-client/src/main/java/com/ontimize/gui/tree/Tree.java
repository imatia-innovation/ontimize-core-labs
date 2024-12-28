package com.ontimize.gui.tree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.NoSuchObjectException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.text.Document;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.TreeBuilder;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BufferedMessageDialog;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.HasHelpIdComponent;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.SecureElement;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.ITreeFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.ApplicationPreferencesListener;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.preferences.PreferenceEvent;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.db.AdvancedEntity;
import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.db.CancellableQueryEntity;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.util.CountDBFunctionName;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.TreePermission;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.ObjectTools;

/**
 * Class that implements a tree where nodes are objects of class. This class divides nodes in two
 * categories: organizational and data nodes.
 * <ul>
 * <li><b>organizational node:</b> It is similar to a folder in a system directory. It could contain
 * any type of node (organizational or data).
 * <li><b>data node:</b> It corresponds with a database record specified by the organizational node.
 * It is similar to a directory structure in operative system.
 * </ul>
 * For example, it is possible that a organizational node contains clients. This node will show a
 * empty form with the client fields disabled. When an user expands that node, a group of client
 * nodes are showed. These clients are data nodes. Organizational nodes could contain another
 * organizational nodes inside, for client accounts for example.
 * <p>
 *
 * @since 5.2000 Default implementation
 * @since 5.2060EN Implemented pageable tree. See OTreeNode parameters
 * @author Imatia Innovation SL
 */
public class Tree extends JTree implements Internationalization, Freeable, OpenDialog, SecureElement,
HasHelpIdComponent, HasPreferenceComponent, ApplicationPreferencesListener {

	private static final Logger logger = LoggerFactory.getLogger(Tree.class);

	protected static boolean DEBUG = false;

	public static int PREFERRED_WIDTH = -1;

	public static boolean enabledRowCount = false;

	public static final String ADJUST_TREE_SPACE = BasicApplicationPreferences.ADJUST_TREE_SPACE;

	class MTreeModelListener extends TreeModelHandler {

		/**
		 * Event is captured. Before calling to parent class, TreeWillCollapse event is triggered, in all
		 * child nodes of source event node. So, when internally tree status is updated, FormManager will
		 * receive the notification.
		 */
		@Override
		public void treeStructureChanged(final TreeModelEvent e) {
			/*
			 * Parent class method marks like expanded the child nodes of this node. It exists a problem because
			 * expand/collapse change is not notified to the listeners, so these ones will not able to detect a
			 * collapse event. For avoiding this circumstance, child nodes will never be expanded when methods
			 * of TreeModelHandler (JTree class) are executed.
			 */
			final TreePath path = e.getTreePath();
			final Object oParent = path.getLastPathComponent();
			if (oParent instanceof TreeNode) {
				final int childCount = ((TreeNode) oParent).getChildCount();
				for (int i = 0; i < childCount; i++) {
					final Object oChild = ((TreeNode) oParent).getChildAt(i);
					final TreePath childPath = path.pathByAddingChild(oChild);
					if (Tree.this.isExpanded(childPath)) {
						Tree.this.collapsePath(path.pathByAddingChild(oChild));
					}
				}
			}
			super.treeStructureChanged(e);
		}

	}

	class QueryPopup extends JPopupMenu implements Internationalization, Freeable {

		class SelectionThread extends Thread {

			int row = -1;

			public SelectionThread(final int row) {
				this.row = row;
			}

			@Override
			public void run() {
				try {
					try {
						Thread.sleep(Tree.this.queryDelayTime);
					} catch (final InterruptedException ie) {
					}

					if (this.row != -1) {
						// Update the path;
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								Tree.this.scrollRowToVisible(SelectionThread.this.row);
								Tree.this.setSelectionRow(SelectionThread.this.row);
								logger.debug("Query popup: select row : {}", SelectionThread.this.row);
								SelectionThread.this.row = -1;
							}
						});
					}
				} catch (final Exception e) {
					Tree.logger.error(null, e);
				}
			}

		};

		SelectionThread t = null;

		class QueryDocumentListener implements DocumentListener {

			public QueryDocumentListener() {
			}

			protected int getCharactersMatch(final String source, final String desired) {
				if ((source == null) || (desired == null)) {
					return -1;
				} else {
					final char[] sc = source.toCharArray();
					final char[] dc = desired.toCharArray();
					int coinc = 0;
					for (int i = 0; (i < sc.length) && (i < dc.length); i++) {
						if ((sc[i] == 'á') || (sc[i] == 'Á')) {
							sc[i] = 'a';
						}
						if ((sc[i] == 'é') || (sc[i] == 'É')) {
							sc[i] = 'é';
						}
						if ((sc[i] == 'í') || (sc[i] == 'Í')) {
							sc[i] = 'í';
						}
						if ((sc[i] == 'ó') || (sc[i] == 'Ó')) {
							sc[i] = 'ó';
						}
						if ((sc[i] == 'ú') || (sc[i] == 'Ú')) {
							sc[i] = 'ú';
						}
						if ((dc[i] == 'á') || (dc[i] == 'Á')) {
							dc[i] = 'a';
						}
						if ((dc[i] == 'é') || (dc[i] == 'É')) {
							dc[i] = 'é';
						}
						if ((dc[i] == 'í') || (dc[i] == 'Í')) {
							dc[i] = 'í';
						}
						if ((dc[i] == 'ó') || (dc[i] == 'Ó')) {
							dc[i] = 'ó';
						}
						if ((dc[i] == 'ú') || (dc[i] == 'Ú')) {
							dc[i] = 'ú';
						}

						if ((sc[i] != dc[i]) && (Character.toLowerCase(sc[i]) != Character.toLowerCase(dc[i]))) {
							break;
						}
						coinc++;
					}
					return coinc;
				}
			}

			protected void update() {
				// Checks letter by letter the first node that matches.
				OTreeNode otNode = (OTreeNode) Tree.this.getSelectionPath().getLastPathComponent();
				// Text of the selected node
				final String selectedText = otNode.toString();
				// For organizational nodes, it selects in children.
				// For data nodes, it selects in that level.
				boolean organizationalSelectedNode = true;
				if (otNode.isOrganizational() == false) {
					otNode = (OTreeNode) otNode.getParent();
					organizationalSelectedNode = false;
				}
				int nodeIndex = 0;
				try {
					int maxMatches = -1;
					final String searchText = QueryPopup.this.textField.getDocument()
							.getText(0, QueryPopup.this.textField.getDocument().getLength());
					for (int i = 0; i < otNode.getChildCount(); i++) {
						final String sNodeText = ((OTreeNode) otNode.getChildAt(i)).toString();
						final int match = this.getCharactersMatch(sNodeText, searchText);
						if (match > maxMatches) {
							maxMatches = match;
							nodeIndex = i;
						}
					}
				} catch (final Exception e) {

					if (ApplicationManager.DEBUG) {
						Tree.logger.error("PopupTree: ", e);
					} else {
						Tree.logger.debug("PopupTree: ", e);
					}
					return;
				}

				// Always will be organizational
				if (otNode.isOrganizational() == true) {
					if ((nodeIndex < 0) || (nodeIndex > (otNode.getChildCount() - 1))) {
						QueryPopup.this.pack();
						QueryPopup.this.revalidate();
						QueryPopup.this.paintImmediately(0, 0, QueryPopup.this.getWidth(), QueryPopup.this.getHeight());
						return;
					}
					final Object childNode = otNode.getChildAt(nodeIndex);
					// If the node text to select is the same that we is
					// actually selected, we do not select.
					if (selectedText.equals(childNode.toString()) == false) {
						// If the selected node is not organizational, we must
						// to select
						// the path of the parent.
						TreePath p = Tree.this.getSelectionPath();
						if (organizationalSelectedNode == false) {
							p = p.getParentPath();
						}
						final int selectRow = Tree.this.getRowForPath(p.pathByAddingChild(childNode));
						try {
							if ((QueryPopup.this.t != null) && QueryPopup.this.t.isAlive()) {
								QueryPopup.this.t.interrupt();
							}
							QueryPopup.this.t.join(10);
						} catch (final Exception e) {
							Tree.logger.debug(null, e);
						}
						QueryPopup.this.t = new SelectionThread(selectRow);
						QueryPopup.this.t.start();
					}
				} else {
					final Object childNode = otNode.getChildAt(nodeIndex);
					if (ApplicationManager.DEBUG) {
						System.out.println(childNode.toString());
					}
					// Selects the node.
					// Gets the child nodes.
					final TreePath p = Tree.this.getSelectionPath();
					final TreePath parentPath = p.getParentPath();
					final int selectRow = Tree.this.getRowForPath(parentPath.pathByAddingChild(childNode));
					try {
						if ((QueryPopup.this.t != null) && QueryPopup.this.t.isAlive()) {
							QueryPopup.this.t.interrupt();
						}
						QueryPopup.this.t.join(50);
					} catch (final Exception e) {
						Tree.logger.debug(null, e);
					}
					QueryPopup.this.t = new SelectionThread(selectRow);
					QueryPopup.this.t.start();
				}
				QueryPopup.this.pack();
				QueryPopup.this.revalidate();
				QueryPopup.this.paintImmediately(0, 0, QueryPopup.this.getWidth(), QueryPopup.this.getHeight());
			}

			@Override
			public void changedUpdate(final DocumentEvent event) {
			}

			@Override
			public void insertUpdate(final DocumentEvent event) {
				this.update();
			}

			@Override
			public void removeUpdate(final DocumentEvent event) {
				this.update();
			}

		};

		QueryDocumentListener listener = new QueryDocumentListener();

		JPanel panel = new JPanel(new BorderLayout());

		JTextField textField = new JTextField();

		JTextField queryField = new JTextField(Tree.queryTextKey + " : ");

		OTreeNode node = null;

		public QueryPopup(final ResourceBundle res) {
			this.add(this.panel);
			this.setBorder(BorderFactory.createLineBorder(Color.black));
			this.textField.setBorder(null);
			this.queryField.setBorder(null);
			this.panel.setBorder(null);

			this.panel.add(this.queryField, BorderLayout.WEST);
			this.panel.add(this.textField, BorderLayout.CENTER);
			this.queryField.setBackground(new Color(204, 204, 255));
			this.queryField.setEditable(false);
			this.textField.setBackground(new Color(204, 204, 255));
			this.textField.setEditable(false);
			this.pack();
			this.textField.getDocument().addDocumentListener(this.listener);
			this.setResourceBundle(res);
		}

		@Override
		public void setVisible(final boolean visible) {
			if (visible == true) {
				this.textField.setText("");
				this.pack();
			}
			super.setVisible(visible);
		}

		@Override
		public List getTextsToTranslate() {
			final List v = new Vector();
			v.add(Tree.queryTextKey);
			return v;
		}

		@Override
		public void setResourceBundle(final ResourceBundle resources) {
			try {
				if (resources != null) {
					this.queryField.setText(resources.getString(Tree.queryTextKey) + ": ");
				}
			} catch (final Exception e) {
				if (ApplicationManager.DEBUG) {
					Tree.logger.error(this.getClass().toString() + " : ", e);
				} else {
					Tree.logger.debug(null, e);
				}
			}
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

		@Override
		public void free() {
			this.textField.getDocument().removeDocumentListener(this.listener);
			this.listener = null;
			this.node = null;
			this.queryField = null;
			this.textField = null;
			this.panel = null;
			this.removeAll();
			if (ApplicationManager.DEBUG) {
				System.out.println(this.getClass().toString() + " : free");
			}
		}

	}

	protected class TreeMouseListener extends MouseAdapter {

		public TreeMouseListener() {
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				Tree.this.showPopupMenu(e);
			}
		}

	}

	protected class MenuListener implements ActionListener {

		public MenuListener() {
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (event.getSource() instanceof JMenuItem) {
				if (((JMenuItem) event.getSource()).getActionCommand().equals(Tree.updateTextKey)) {

					final TreePath path = Tree.this.getSelectionPath();
					final OTreeNode n = (OTreeNode) path.getLastPathComponent();
					if (n.isOrganizational()) {
						Tree.this.collapsePath(path);
						Tree.this.expandPath(path);
					} else {
						((FormManager) Tree.this.formManager).setCheckModified(false);
						try {
							final TreePath parentTreePath = path.getParentPath();
							Tree.this.setSelectionPath(parentTreePath);
							Tree.this.setSelectionPath(path);
						} catch (final Exception e) {
							Tree.logger.debug(null, e);
						}
						((FormManager) Tree.this.formManager).setCheckModified(true);
					}
				} else if (((JMenuItem) event.getSource()).getActionCommand().equals(Tree.deleteNodeTextKey)) {
					final int selectedRowsCount = Tree.this.getSelectionCount();
					if (selectedRowsCount > 0) {
						final int option = MessageDialog.showMessage(Tree.this.parentFrame,
								"tree.do_you_really_want_deleted_the_selected_nodes", JOptionPane.QUESTION_MESSAGE,
								Tree.this.resource);
						if (option == JOptionPane.YES_OPTION) {
							Tree.this.deleteNodes(Tree.this.getSelectionRows());
						}

					}
				} else if (((JMenuItem) event.getSource()).getActionCommand().equals(Tree.selectedQueryKey)) {
					final TreePath selec = Tree.this.getSelectionPath();
					if (selec != null) {
						Tree.this.scrollPathToVisible(selec);
					}
				} else if (((JMenuItem) event.getSource()).getActionCommand().equals(Tree.ascendingOrderKey)) {
					final long t = System.currentTimeMillis();
					final Object selectedObject = Tree.this.getLastSelectedPathComponent();
					if (selectedObject == null) {
						return;
					}
					if (selectedObject instanceof OTreeNode) {
						final OTreeNode selectedNode = (OTreeNode) selectedObject;
						selectedNode.setSortOrder(true);
					}
					final TreeModel treeModel = Tree.this.getModel();
					if (treeModel instanceof DefaultTreeModel) {
						((DefaultTreeModel) treeModel).nodeStructureChanged((TreeNode) selectedObject);
						final int[] index = new int[((TreeNode) selectedObject).getChildCount()];
						for (int p = 0; p < index.length; p++) {
							index[p] = p;
						}
						((DefaultTreeModel) treeModel).nodesChanged(((TreeNode) selectedObject), index);
					}
					final long tf = System.currentTimeMillis();
					if (ApplicationManager.DEBUG_TIMES) {
						System.out.println("Sorting time : " + ((tf - t) / 1000.0) + " seconds");
					}
				} else if (((JMenuItem) event.getSource()).getActionCommand().equals(Tree.descendingOrderKey)) {
					final long t = System.currentTimeMillis();
					final Object selectedObject = Tree.this.getLastSelectedPathComponent();
					if (selectedObject == null) {
						return;
					}
					if (selectedObject instanceof OTreeNode) {
						final OTreeNode selectedNode = (OTreeNode) selectedObject;
						selectedNode.setSortOrder(false);
					}
					final TreeModel treeModel = Tree.this.getModel();
					if (treeModel instanceof DefaultTreeModel) {
						((DefaultTreeModel) treeModel).nodeStructureChanged((TreeNode) selectedObject);
						final int[] index = new int[((TreeNode) selectedObject).getChildCount()];
						for (int p = 0; p < index.length; p++) {
							index[p] = p;
						}
						((DefaultTreeModel) treeModel).nodesChanged(((TreeNode) selectedObject), index);
					}
					final long tf = System.currentTimeMillis();
					if (ApplicationManager.DEBUG_TIMES) {
						System.out.println("Sorting time : " + ((tf - t) / 1000.0) + " seconds");
					}
				} else if (((JMenuItem) event.getSource()).getActionCommand()
						.equalsIgnoreCase(Tree.fitSpaceToTreeKey)) {
					final boolean selec = ((JCheckBoxMenuItem) event.getSource()).isSelected();
					Tree.this.fitSpaceToTree = selec;
					ApplicationManager.getApplication()
					.getPreferences()
					.setPreference(((ClientReferenceLocator) Tree.this.locator).getUser(), Tree.ADJUST_TREE_SPACE,
							"" + selec);
				} else if (((JMenuItem) event.getSource()).getActionCommand().indexOf(Tree.orderByTextKey) >= 0) {
					final long t = System.currentTimeMillis();
					final String ac = ((JMenuItem) event.getSource()).getActionCommand();
					if (ac.length() <= Tree.orderByTextKey.length()) {
						return;
					}
					final String at = ac.substring(Tree.orderByTextKey.length());
					final Object selectedObject = Tree.this.getLastSelectedPathComponent();
					if (selectedObject == null) {
						return;
					}
					if (selectedObject instanceof OTreeNode) {
						final OTreeNode selectedNode = (OTreeNode) selectedObject;
						if (selectedNode.getShownAttributeList().contains(at) == false) {
							return;
						}
						selectedNode.setSortAttr(at);
						selectedNode.setSortOrder(true);
					}
					final TreeModel treeModel = Tree.this.getModel();
					if (treeModel instanceof DefaultTreeModel) {
						((DefaultTreeModel) treeModel).nodeStructureChanged((TreeNode) selectedObject);
					}
					final long tf = System.currentTimeMillis();
					if (ApplicationManager.DEBUG_TIMES) {
						System.out.println("Sorting time : " + ((JMenuItem) event.getSource()).getActionCommand() + " "
								+ ((tf - t) / 1000.0) + " seconds");
					}
				}

			}
		}

	};

	public static String TITLE_DELETE_NODE_RESULT_ERROR = "tree.error_deleting_node";

	public static String TITLE_DELETE_NODE_RESULT_SUCCESSFULLY = "tree.node_successfully_deleted";

	public static String TITLE_DELETE_NODE_RESULT_ERROR_es_ES = "Resultados: Ocurrieron errores en el proceso de eliminar nodos. ";

	public static String TITLE_DELETE_NODE_RESULT_SUCCESSFULLY_es_ES = "Resultados: Se eliminaron correctamente los nodos seleccionados. ";

	public static String M_DELETED_NODE = "tree.deleting_node";

	public static String M_DELETED_NODE_es_ES = "Eliminando nodo...";

	public static String M_UPDATED_NODE = "tree.updating_node";

	public static String M_UPDATED_NODE_es_ES = "Actualizando...";

	public static String M_ERROR_DELETED_NODE = "tree.error_deleting_node";

	public static String M_DELETED_NODE_SUCCESSFULLY = "tree.node_successfully_deleted";

	public static String M_DELETED_NODE_SUCCESSFULLY_es_ES = "El nodo se eliminó correctamente. ";

	public static String M_ERROR_DELETED_NODE_es_ES = "No se pudo eliminar el nodo. ";

	protected static String queryTextKey = "tree.queryText";

	protected static String deleteNodeTextKey = "tree.delete_node";

	protected static String updateTextKey = "tree.update_node_data";

	protected static String ascendingOrderKey = "tree.ascending_sort";

	protected static String orderByTextKey = "sort_by";

	protected static String descendingOrderKey = "tree.descending_sort";

	protected static String editNodeKey = "tree.edit_node";

	protected static String selectedQueryKey = "tree.searchSelection";

	protected static String fitSpaceToTreeKey = "tree.adjust_tree_width";

	protected QueryPopup popup = null;

	protected ImageIcon newIcon = null;

	protected ResourceBundle resource = null;

	protected ReferenceTreeComponent compReference = null;

	protected EntityReferenceLocator locator = null;

	protected ITreeFormManager formManager = null;

	private boolean processTreeWillExpandEvents = true;

	private boolean processTreeWillCollapseEvents = true;

	private final boolean processValueChangeEvents = true;

	/**
	 * List with tree models. These models must contain nodes whose class is OTreeNode.
	 */
	protected Map modelList = new Hashtable();

	/**
	 * URI for files that contains the tree specification. All files must reside in this path, because
	 * setModel() have to load them.
	 */
	String uRIBaseFile = null;

	/**
	 * Name of current tree model.
	 */
	protected String currentTree = null;

	protected ExtendedJPopupMenu popupMenu = new ExtendedJPopupMenu();

	protected JMenuItem updateItem = new JMenuItem(Tree.updateTextKey);

	protected JMenu orderBy = new JMenu(Tree.orderByTextKey);

	protected JMenuItem queryItem = new JMenuItem(Tree.selectedQueryKey);

	protected JMenuItem deleteNodeItem = new JMenuItem(Tree.deleteNodeTextKey);

	protected JMenuItem ascOrderItem = new JMenuItem(Tree.ascendingOrderKey);

	protected JMenuItem descOrderItem = new JMenuItem(Tree.descendingOrderKey);

	protected JMenuItem editNodeItem = new JMenuItem(Tree.editNodeKey);

	protected JCheckBoxMenuItem fitSpaceToTreeMenuItem = new JCheckBoxMenuItem(Tree.fitSpaceToTreeKey);

	/**
	 * Reference to the tree builder that creates the tree model.
	 */
	protected TreeBuilder treeBuilder = null;

	protected TreeMouseListener treeMouseListener = new TreeMouseListener();

	protected MenuListener menuListener = new MenuListener();

	protected Frame parentFrame = null;

	private TreePermission nodeVisiblePermission = null;

	protected boolean fitSpaceToTree = false;

	protected int queryDelayTime = 750;

	/**
	 * Class constructor. Moreover to call to <code>super()</code>, registers the tooltip manager,
	 * creates the contextual pop-up menu options for right mouse button and fixes the tree model.
	 * @param treeURI the path to the file with tree definition
	 * @param constructor reference to the tree constructor that must process tree definition file and
	 *        return the tree model
	 */
	public Tree(final String treeURI, final TreeBuilder constructor) {
		super();
		ToolTipManager.sharedInstance().registerComponent(this);
		this.updateItem.setActionCommand(Tree.updateTextKey);
		this.queryItem.setActionCommand(Tree.selectedQueryKey);
		this.deleteNodeItem.setActionCommand(Tree.deleteNodeTextKey);
		this.ascOrderItem.setActionCommand(Tree.ascendingOrderKey);
		this.descOrderItem.setActionCommand(Tree.descendingOrderKey);
		this.fitSpaceToTreeMenuItem.setActionCommand(Tree.fitSpaceToTreeKey);
		this.setCellRenderer(new BasicTreeCellRenderer());
		this.treeBuilder = constructor;
		this.popupMenu.add(this.updateItem);
		this.popupMenu.add(this.queryItem);
		this.popupMenu.add(new JSeparator());
		this.popupMenu.add(this.editNodeItem);
		this.popupMenu.add(new JSeparator());
		this.popupMenu.add(this.deleteNodeItem);
		this.popupMenu.add(new JSeparator());
		this.popupMenu.add(this.ascOrderItem);
		this.popupMenu.add(this.descOrderItem);
		this.popupMenu.addSeparator();
		this.popupMenu.add(this.fitSpaceToTreeMenuItem);
		this.popupMenu.addSeparator();
		this.popupMenu.add(this.orderBy);
		this.editNodeItem.setEnabled(false);
		this.queryItem.addActionListener(this.menuListener);
		this.deleteNodeItem.addActionListener(this.menuListener);
		this.updateItem.addActionListener(this.menuListener);
		this.ascOrderItem.addActionListener(this.menuListener);
		this.descOrderItem.addActionListener(this.menuListener);
		this.fitSpaceToTreeMenuItem.addActionListener(this.menuListener);
		this.add(this.popupMenu);
		this.addMouseListener(this.treeMouseListener);

		int index = 0;
		for (int i = 0; i < treeURI.length(); i++) {
			if ((treeURI.charAt(i) == '/') || (treeURI.charAt(i) == '\\')) {
				index = i;
			}
		}

		// XML files will be in this path
		this.uRIBaseFile = treeURI.substring(0, index + 1);
		final String sTreeName = treeURI.substring(index + 1, treeURI.length());
		this.setCurrentModel(sTreeName);
		this.currentTree = sTreeName;
		this.setSelectionRow(0);
		/*
		 * It exists a problem with original tree approach due to JTree listener model. It occurs when
		 * nodeStructureChanged() (Class DefaultTreeModel) is called. This method notifies to the model
		 * listeners that process the update operation. This listener changes the collapse-expand state for
		 * any nodes. But this change is realized in a HashMap of JTree class and it is not notified to our
		 * listeners, for example FormManager. So, it has been modified the class TreeModelListener .
		 */
		this.getModel().removeTreeModelListener(this.treeModelListener);
		this.treeModelListener = new MTreeModelListener();
		this.getModel().addTreeModelListener(this.treeModelListener);

		this.newIcon = ImageManager.getIcon(ImageManager.NEW);

		this.installHelpId();
	}

	/**
	 * Class constructor.
	 * @param treeURI the path to the file with tree definition
	 * @param constructor reference to the tree constructor that must process tree definition file and
	 *        return the tree model
	 * @param locator a reference to the locator
	 */
	public Tree(final String treeURI, final TreeBuilder constructor, final EntityReferenceLocator locator) {
		this(treeURI, constructor);
		this.locator = locator;
		this.setEditable(false);
	}

	@Override
	public void setEditable(final boolean editable) {
		if (editable == true) {
			if (this.locator != null) {
				// To do the tree editable.
				super.setEditable(true);
				if ((this.getCellEditor() == null)
						|| ((this.getCellEditor() instanceof DefaultDataTreeCellEditor) == false)) {
					this.setCellEditor(new DefaultDataTreeCellEditor(this.locator));
				}
				this.setInvokesStopCellEditing(true);
			}
		} else {
			super.setEditable(false);
		}
	}

	/**
	 * Loads (when it is not loaded) and sets the model for tree.
	 * @param tree the name of model
	 */
	public void setCurrentModel(final String tree) {
		final TreeModel model = (TreeModel) this.modelList.get(tree);
		if (model == null) {
			// Load
			final List vTrees = new Vector();
			vTrees.add(tree);
			this.loadTrees(vTrees);
			this.setModel((TreeModel) this.modelList.get(tree));
			this.currentTree = tree;
		} else {
			this.setModel((TreeModel) this.modelList.get(tree));
			this.currentTree = tree;
		}
		this.collapsePath(this.getPathForRow(0));
		this.initPermissions();
	}

	/**
	 * Loads the tree models whose names are passed in List parameter.
	 * @param trees
	 */
	public void loadTrees(final List trees) {
		for (int i = 0; i < trees.size(); i++) {
			final String sTree = (String) trees.get(i);
			try {
				final TreeModel model = this.treeBuilder.buildTree(this.uRIBaseFile + sTree);
				if (model != null) {
					this.modelList.put(sTree, model);
				} else {
					Tree.logger.debug("Error loading tree: " + sTree);
				}
			} catch (final Exception e) {
				Tree.logger.error("Error in the tree constructor. " + sTree + " . ", e);
			}
		}
	}

	protected void showPopupMenu(final MouseEvent event) {
		// Gets x and y position
		final int x = event.getX();
		final int y = event.getY();
		final int row = this.getRowForLocation(x, y);
		if (row >= 0) {
			if (!this.isRowSelected(row)) {
				this.setSelectionRow(row);
			}
		}
		if (this.getSelectionCount() > 0) {

			final int[] selectedRows = this.getSelectionRows();
			this.deleteNodeItem.setEnabled(true);
			this.orderBy.removeAll();
			for (int i = 0; i < selectedRows.length; i++) {
				final int f = selectedRows[i];
				final Object oNode = this.getPathForRow(f).getLastPathComponent();
				if ((oNode instanceof OTreeNode)
						&& ((!((OTreeNode) oNode).canDelete()) || ((OTreeNode) oNode).isOrganizational())) {
					this.deleteNodeItem.setEnabled(false);
				}
			}
			final Object node = this.getSelectionPath().getLastPathComponent();
			if (node instanceof OTreeNode) {
				if (this.isExpanded(this.getSelectionPath())) {
					this.ascOrderItem.setEnabled(true);
					this.descOrderItem.setEnabled(true);
					if (((OTreeNode) node).isOrganizational()) {
						this.orderBy.setEnabled(true);
						// Creates the menu items
						final List visibleAttributes = ((OTreeNode) node).getVisibleAttributes();
						for (int i = 0; i < visibleAttributes.size(); i++) {
							String sText = visibleAttributes.get(i).toString();
							final String ac = sText;
							try {
								if (this.resource != null) {
									sText = this.resource.getString(sText);
								}
							} catch (final Exception e) {
								if (ApplicationManager.DEBUG) {
									Tree.logger.debug(null, e);
								} else {
									Tree.logger.trace(null, e);
								}
							}
							final JRadioButtonMenuItem item = new JRadioButtonMenuItem(sText);
							item.setActionCommand(Tree.orderByTextKey + ac);
							if (ac.equals(((OTreeNode) node).getSortAttr())) {
								item.setSelected(true);
							}
							item.addActionListener(this.menuListener);
							this.orderBy.add(item);
						}
					} else {
						this.orderBy.setEnabled(false);
					}
				} else {
					this.ascOrderItem.setEnabled(false);
					this.descOrderItem.setEnabled(false);
					this.orderBy.setEnabled(false);
				}
				if (((OTreeNode) node).isOrganizational()) {
					this.editNodeItem.setEnabled(false);
				} else {
					this.editNodeItem.setEnabled(this.isEditable());
				}
			} else {
				this.editNodeItem.setEnabled(false);
			}

			this.popupMenu.show(this, x, y);
		}
	}

	protected class DataNodeOperationThread extends OperationThread {

		String sOperationId = null;

		OTreeNode parentNode;

		EntityReferenceLocator locator;

		public DataNodeOperationThread(final OTreeNode parentNode, final EntityReferenceLocator locator) {
			this.parentNode = parentNode;
			this.locator = locator;
		}

		@Override
		public void cancel() {
			Tree.logger.debug("createDataNode: Cancelling OperationThread.");
			super.cancel();
			if (this.sOperationId != null) {
				try {
					Tree.logger.debug("Calling to cancel OperationThread for entity.");
					final Entity entity = this.locator.getEntityReference(this.parentNode.getEntityName());
					if (entity instanceof CancellableQueryEntity) {
						((CancellableQueryEntity) entity).cancelOperation(this.sOperationId);
					}
				} catch (final Exception e) {
					Tree.logger.debug("ERROR calling to cancel OperationThread for entity.", e);
				}
			}
		}

		protected Entity getEntityReference() throws Exception {
			if ((this.parentNode.getEntityName() == null) || this.parentNode.getEntityName().equals("")) {
				Tree.logger.debug(" Node entity is NULL.");
				if (!this.cancelled) {
					this.res = null;
				}
				this.hasFinished = true;
				return null;
			}

			final Entity entity = this.locator.getEntityReference(this.parentNode.getEntityName());
			if (entity == null) {
				Tree.logger.debug("Node entity is null.");
				if (!this.cancelled) {
					this.res = null;
				}
				throw new Exception("Entity reference is NULL : " + this.parentNode.getEntityName());
			}
			return entity;
		}

		protected void createDataNodes(final EntityResult result, final List resultVector, final List organizationNodes, final List keys,
				final String[] attributes) {
			boolean bShowErrorMessage = true;
			for (int j = 0; j < resultVector.size(); j++) {

				OTreeNode oDataNode = null;
				if (this.parentNode instanceof PageFetchTreeNode) {
					oDataNode = (OTreeNode) ((OTreeNode) this.parentNode.getParent()).clone();
				} else {
					oDataNode = (OTreeNode) this.parentNode.clone();
				}
				oDataNode.setOrganizational(false);

				boolean bNodeErrors = false;

				final Map newNodeKeysValues = new Hashtable();
				// Key results
				for (int k = 0; k < keys.size(); k++) {
					final List keyValues = (List) result.get(keys.get(k));
					if (keyValues != null) {
						if (keyValues.get(j) == null) {
							bNodeErrors = true;
							if (bShowErrorMessage) {
								MessageDialog.showErrorMessage((Frame) null,
										"Error in tree data. There is a null value for the key: " + keys.get(k)
										+ ". Some results have been removed.");
								bShowErrorMessage = MessageDialog.showQuestionMessage(null,
										"Do you want to show the error messages for each node?");
							}
							break;
						}
						newNodeKeysValues.put(keys.get(k), keyValues.get(j));
					}
				}

				if (bNodeErrors) {
					continue;
				}
				// Attributes result
				for (int k = 0; k < attributes.length; k++) {
					final List keysValues = (List) result.get(attributes[k]);
					if (keysValues != null) {
						if (keysValues.get(j) != null) {
							newNodeKeysValues.put(attributes[k], keysValues.get(j));
						} else {
							newNodeKeysValues.put(attributes[k], "");
						}
					}
				}
				oDataNode.setKeysValues(newNodeKeysValues);
				if (Tree.this.compReference != null) {
					final Object oCodeValue = oDataNode.getValueForAttribute(Tree.this.compReference.getAttribute());
					if (oCodeValue != null) {
						final String descr = Tree.this.compReference.getDescriptionForCode(oCodeValue);
						oDataNode.setAttributeText(Tree.this.compReference.getAttribute(), descr);
					}
				}
				if (this.parentNode instanceof PageFetchTreeNode) {
					((OTreeNode) this.parentNode.getParent()).add(oDataNode);
					if (Tree.DEBUG) {
						Tree.logger.debug("Tree (pageable tree): Adding node: " + oDataNode + " to -> "
								+ this.parentNode.getParent() + " total Child count: "
								+ ((OTreeNode) this.parentNode.getParent()).getChildCount());
					}

				} else {
					this.parentNode.add(oDataNode);
				}
				oDataNode.disableNodeSorting();

				// If the node is dynamic, and not contains the node
				// yet, a
				// clone is added.

				if (oDataNode.isDynamic()) {
					boolean bFound = false;
					for (int i = 0; i < oDataNode.getChildCount(); i++) {
						final OTreeNode nAux = (OTreeNode) oDataNode.getChildAt(i);
						if (nAux.equals(oDataNode)) {
							bFound = true;
							break;
						}
					}
					if (!bFound) {
						final OTreeNode clon = (OTreeNode) this.parentNode.clone();
						oDataNode.add(clon);
					}
				}

				// Add organizational nodes
				for (int k = 0; k < organizationNodes.size(); k++) {
					oDataNode.add(((OTreeNode) organizationNodes.get(k)).cloneNodeAndChildren());
				}

				oDataNode.enableNodeSorting();

			}
		}

		protected void executeQuery(final Entity entity, final List vectorAttributes, final Map keysValuesSearch, final int records,
				final List organizationNodes, final List keys, final String[] attributes,
				final long t) throws Exception {

			int offset = 0;
			if (this.parentNode instanceof PageFetchTreeNode) {
				offset = this.parentNode.getParent().getChildCount() - 1;
				// records = ((OTreeNode)
				// parentNode.getParent()).getRowsNumberToQuery();
			}
			// Query time
			EntityResult result = null;
			if (entity instanceof CancellableQueryEntity) {
				this.sOperationId = ((CancellableQueryEntity) entity).getOperationUniqueIdentifier();
				// result = ((CancellableQueryEntity)
				// entity).query(keysValuesSearch, vectorAttributes,
				// locator.getSessionId(), sOperationId);
				if (this.parentNode.isPageableEnabled()) {
					result = ((AdvancedEntity) entity).query(keysValuesSearch, vectorAttributes,
							this.locator.getSessionId(), records, offset,
							Tree.this.getSQLOrderList(this.parentNode));
				} else {
					result = entity.query(keysValuesSearch, vectorAttributes, this.locator.getSessionId());
				}
			} else {
				// result = entity.query(keysValuesSearch,
				// vectorAttributes, locator.getSessionId());
				if (this.parentNode.isPageableEnabled()) {
					result = ((AdvancedEntity) entity).query(keysValuesSearch, vectorAttributes,
							this.locator.getSessionId(), records, offset,
							Tree.this.getSQLOrderList(this.parentNode));
				} else {
					result = entity.query(keysValuesSearch, vectorAttributes, this.locator.getSessionId());
				}
			}
			final long t3 = System.currentTimeMillis();
			Tree.logger.trace("Time to end query = " + ((t3 - t) / 1000.0) + " seconds");

			if (result != null) {
				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					if (!this.cancelled) {
						this.res = result;
					}
					this.hasFinished = true;
					return;
				}
				if (!this.cancelled) {
					// Test the net speed
					ConnectionManager.checkEntityResult(result, this.locator);
				}
			}

			if (result.isEmpty()) {
				this.processEmptyEntityResult(result, organizationNodes);
				return;
			}

			// We create all child nodes for current node.
			// But this node has organizational child nodes. So,
			// these organizational nodes must be placed like child
			// nodes
			// for all the next nodes that will be added. Attribute
			// from Map is
			// obtained.
			String firstAttribute = null;
			List resultVector = new Vector();
			if (keys.size() > 0) {
				resultVector = (List) result.get(keys.get(0));
			}

			if (resultVector == null) {
				if (attributes.length > 0) {
					firstAttribute = attributes[0];
					resultVector = (List) result.get(firstAttribute);

				} else {
					Tree.logger.debug("TREE: Warning --> There is a node without attributes");
					this.res = result;
					this.hasFinished = true;
					return;
				}
			}

			if (this.cancelled) {
				this.res = new EntityResultMapImpl(EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE, EntityResult.NODATA_RESULT,
						"entity.operation_cancelled");
				this.hasFinished = true;
				return;
			}

			// Updates the node value with method:
			// parentNode.setQueryResult(result)
			// In associated form, data are also updated with
			// method.

			// Deletes all child nodes
			if (!(this.parentNode instanceof PageFetchTreeNode)) {
				this.parentNode.removeAllChildren();
				this.parentNode.disableNodeSorting();
				// Now, we create all nodes to add.
				if (resultVector == null) {
					final EntityResult auxResult = new EntityResultMapImpl();
					auxResult.setCode(EntityResult.OPERATION_WRONG);
					auxResult.setMessage("Error in tree configuration. Node key not found in the results: "
							+ keys.get(0) + " Is in the data : " + result.keySet());
					this.res = auxResult;
					this.hasFinished = true;
					return;
				}
			}

			this.createDataNodes(result, resultVector, organizationNodes, keys, attributes);

			// Enable sorting
			if (this.parentNode instanceof PageFetchTreeNode) {
				this.enableSortingPageFetchTreeNode(this.parentNode, organizationNodes, result);
			} else {
				this.parentNode.enableNodeSorting();
				this.parentNode.sortNow();

				if (result instanceof AdvancedEntityResult) {
					this.parentNode.setTotalCount(((AdvancedEntityResult) result).getTotalRecordCount());
					// si no están todos añadimos un nodo 'aux'
					final OTreeNode nodepru = (OTreeNode) this.parentNode.clone();
					if (this.parentNode.getChildCount() < this.parentNode.getTotalCount()) {
						final Map params = new Hashtable();
						params.putAll(this.parentNode.getParameters());

						final PageFetchTreeNode node = new PageFetchTreeNode(params);
						// Add organizational nodes
						for (int k = 0; k < organizationNodes.size(); k++) {
							node.add(((OTreeNode) organizationNodes.get(k)).cloneNodeAndChildren());
						}
						this.parentNode.add(node);
					}
				}
			}

			// Final time
			final long t4 = System.currentTimeMillis();
			Tree.logger.trace("Time to create all nodes = " + ((t4 - t) / 1000.0) + " seconds");
			this.res = result;
		}

		protected void enableSortingPageFetchTreeNode(final OTreeNode parentNode, final List organizationNodes,
				final EntityResult result) {
			((OTreeNode) parentNode.getParent()).enableNodeSorting();
			((OTreeNode) parentNode.getParent()).sortNow();
			if (result instanceof AdvancedEntityResult) {
				((OTreeNode) parentNode.getParent())
				.setTotalCount(((AdvancedEntityResult) result).getTotalRecordCount());
				// si no están todos añadimos un nodo 'aux'
				final OTreeNode nodepru = (OTreeNode) parentNode.clone();
				if (((OTreeNode) parentNode.getParent()).getChildCount() <= ((OTreeNode) parentNode.getParent())
						.getTotalCount()) {
					if (!((((OTreeNode) parentNode.getParent())
							.getChildCount() == (((OTreeNode) parentNode.getParent()).getTotalCount() - 1))
							&& (parentNode.getRowsNumberToQuery() == 1))) {
						final Map params = new Hashtable();
						params.putAll(((OTreeNode) parentNode.getParent()).getParameters());

						final PageFetchTreeNode node = new PageFetchTreeNode(params);
						// Add organizational nodes
						for (int k = 0; k < organizationNodes.size(); k++) {
							node.add(((OTreeNode) organizationNodes.get(k)).cloneNodeAndChildren());
						}
						((OTreeNode) parentNode.getParent()).add(node);
					}
				}
			}
			((OTreeNode) parentNode.getParent()).remove(parentNode);
		}

		@Override
		public void run() {
			this.hasStarted = true;
			try {
				if (Tree.this.resource != null) {
					this.status = Tree.this.resource.getString("performing_query");
				} else {
					this.status = "Querying...";
				}
			} catch (final Exception e) {
				Tree.logger.trace(null, e);
				this.status = "Querying...";
			}
			try {
				// Necessary reference to the entity

				final Entity entity = this.getEntityReference();
				if (entity == null) {
					return;
				}
				final List vectorAttributes = new Vector();

				// For tree nodes, we need attr value and primary keys to
				// identify the record.
				for (int i = 0; i < this.parentNode.getKeys().size(); i++) {
					final Object atribu = this.parentNode.getKeys().get(i);
					vectorAttributes.add(atribu);
				}
				final String[] attributes = this.parentNode.getAttributes();
				final List keys = this.parentNode.getKeys();
				for (int i = 0; i < attributes.length; i++) {
					if (!vectorAttributes.contains(attributes[i])) {
						vectorAttributes.add(attributes[i]);
					}
				}

				// For each parent field associated with a child node, it is
				// looked for
				// organizational nodes. It is necessary to travel across
				// all
				// tree branches and leafs.
				// A SubTree with organizational nodes is created.

				final List organizationNodes = new Vector();
				final long t = System.currentTimeMillis();

				for (int i = 0; i < this.parentNode.getChildCount(); i++) {
					// If exists a non-organizational node, it has not
					// deleted yet.
					if (((OTreeNode) this.parentNode.getChildAt(i)).isOrganizational()) {
						final OTreeNode childNode = (OTreeNode) this.parentNode.getChildAt(i);
						final OTreeNode cloneChildNode = childNode.cloneNodeAndChildren();
						organizationNodes.add(cloneChildNode);
					}
				}
				// }
				final long t2 = System.currentTimeMillis();
				Tree.logger.trace("Time invested in child node clonation = " + ((t2 - t) / 1000.0) + " seconds");

				// For creating the query, it is necessary to obtain the
				// upper data note. If this node is
				// organizational, we need to get the parent of this one (if
				// this process obtains the root node
				// must be discarded)
				final Map keysValuesSearch = Tree.this.buildQueryFilter(this.parentNode);

				// int page = parentNode.getRowsNumberToQuery();
				// int childcount = parentNode.getTotalCount();
				// String nameparent = parentNode.getText();
				// boolean enabledpag = parentNode.isPageableEnabled();
				final int records = this.parentNode.getRowsNumberToQuery();

				try {
					this.executeQuery(entity, vectorAttributes, keysValuesSearch, records, organizationNodes, keys,
							attributes, t);
				} catch (final NoSuchObjectException e) {
					Tree.logger.debug("Exception in tree: ", e);
					this.res = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
							e.getMessage());
					if (this.locator instanceof UtilReferenceLocator) {
						((UtilReferenceLocator) this.locator).removeEntity(this.parentNode.getEntityName(),
								this.locator.getSessionId());
					}
					return;
				} catch (final Exception e) {
					this.res = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
							e.getMessage());
					Tree.logger.error(null, e);
					return;
				}
			} catch (final Exception e) {
				this.res = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT, e.getMessage());
				Tree.logger.error(null, e);
				return;
			}
			this.hasFinished = true;
			return;
		}

		protected void processEmptyEntityResult(final EntityResult result, final List organizationNodes) {

			if (this.parentNode instanceof PageFetchTreeNode) {
				((OTreeNode) this.parentNode.getParent()).remove(this.parentNode);
			} else {
				// If it is empty, they are deleted all child
				// nodes.
				this.parentNode.removeAllChildren();
				final Map parameters = new Hashtable();
				parameters.put(OTreeNode.TEXT, OTreeNode.THERE_ARENT_RESULTS_KEY);
				parameters.put(OTreeNode.ATTR, "Aviso");
				parameters.put(OTreeNode.ENTITY, "");
				parameters.put(OTreeNode.ORG, "false");
				parameters.put(OTreeNode.SEPARATOR, " ");
				parameters.put(OTreeNode.CAN_DELETE, "no");
				final OTreeNode warningNode = new OTreeNode(parameters) {

					@Override
					public boolean isLeaf() {
						return true;
					}
				};
				warningNode.setDynamicFormManager(this.parentNode.getDynamicFormManager());
				warningNode.setKeys(this.parentNode.getKeys());
				warningNode.setAssociatedDataField(this.parentNode.getAssociatedDataField());
				final Map warningKeysValues = new Hashtable();
				warningKeysValues.put("Warning", OTreeNode.THERE_ARENT_RESULTS_KEY);

				warningNode.setKeysValues(warningKeysValues);
				warningNode.setEmptyNode(true);
				warningNode.setResourceBundle(Tree.this.resource);
				this.parentNode.add(warningNode);
				// Add organizational nodes
				for (int k = 0; k < organizationNodes.size(); k++) {
					warningNode.add(((OTreeNode) organizationNodes.get(k)).cloneNodeAndChildren());
				}
			}
			this.res = result;
			this.hasFinished = true;
		}

	}

	/**
	 * This function creates the child nodes of parent node. For organizational nodes,child nodes are
	 * created with data obtained of node entity. Model for tree only contains organizational nodes, so
	 * data nodes are created dynamically. For each organizational node, this method creates the child
	 * nodes.
	 * @param parentNode the parent node for current node
	 * @param locator a reference to the locator
	 * @return the <code>Hashtable</code> with data.
	 */
	public Map createDataNode(final OTreeNode parentNode, final EntityReferenceLocator locator) {
		if (locator == null) {
			Tree.logger.debug("ReferenceLocator is NULL");
			return null;
		}
		if (parentNode.getEntityName() == null) {
			return null;
		}

		final DataNodeOperationThread op = new DataNodeOperationThread(parentNode, locator);
		final OperationThread opThread = ApplicationManager.proccessOperation(this.parentFrame, op, 3000);
		return (Map) opThread.getResult();
		// op.run();
		// return new Hashtable();
	}

	/**
	 * This function deletes the child data nodes in an organizational node, for all child nodes.
	 * Organizational nodes are ignored.
	 * @param oTreeNode the node
	 */
	public void deleteNotOrganizationalNode(final OTreeNode oTreeNode) {
		// It is necessary to look around leafs and branches for deleting data
		// nodes.

		// If node is organizational, it has data node.
		// If node is a data node, it has organizational child nodes.

		// If node has not child nodes, exits the mehtod.
		if (oTreeNode.getChildCount() < 1) {
			return;
		}

		// Organizational node. It takes the first child, and a List with
		// child
		// nodes is created.
		final List childNodes = new Vector();
		if (oTreeNode.isOrganizational()) {
			final OTreeNode oChildNode = (OTreeNode) oTreeNode.getChildAt(0);
			// If node is organizational, returns.
			if (oChildNode.isOrganizational()) {
				return;
			}
			// For this node, all child organizational nodes will be added to
			// this
			// vector
			for (int i = 0; i < oChildNode.getChildCount(); i++) {
				childNodes.add(oChildNode.getChildAt(i));
				// Recursive. It is organizational.
				this.deleteNotOrganizationalNode((OTreeNode) oChildNode.getChildAt(i));
			}
			// Now, all children are deleted
			oTreeNode.removeAllChildren();
			// Adds the child nodes
			for (int i = 0; i < childNodes.size(); i++) {
				oTreeNode.add((OTreeNode) childNodes.get(i));
			}
		}
		// When it is not organizational
		else {
			// Call to this method for all organizational children
			for (int i = 0; i < oTreeNode.getChildCount(); i++) {
				if (((OTreeNode) oTreeNode.getChildAt(i)).isOrganizational()) {
					this.deleteNotOrganizationalNode((OTreeNode) oTreeNode.getChildAt(i));
				}
			}
		}

		// When process finishes, if the node is organizational, a new node
		// will be added to show the "+" in tree

		if ((oTreeNode.isOrganizational()) && (oTreeNode.getChildCount() == 0)) {
			final Map parameters = new Hashtable();
			parameters.put(OTreeNode.TEXT, OTreeNode.THERE_ARENT_RESULTS_KEY);
			parameters.put(OTreeNode.ATTR, "");
			parameters.put(OTreeNode.ENTITY, "");
			parameters.put(OTreeNode.FORM, "");
			parameters.put(OTreeNode.ORG, "false");
			parameters.put(OTreeNode.SEPARATOR, " ");
			parameters.put(OTreeNode.CAN_DELETE, "no");
			final OTreeNode auxiliaryNode = new OTreeNode(parameters);
			auxiliaryNode.setEmptyNode(true);
			auxiliaryNode.setDynamicFormManager(oTreeNode.getDynamicFormManager());
			auxiliaryNode.setResourceBundle(this.resource);
			oTreeNode.add(auxiliaryNode);
		}
	}

	protected QueryPopup getQueryPopup() {
		if (this.popup == null) {
			this.popup = new QueryPopup(this.resource);
		}
		return this.popup;
	}

	protected boolean removeStringInPopup(final KeyEvent event) {
		if ((event.getKeyCode() == 8) || (event.getKeyCode() == KeyEvent.VK_BACK_SPACE)) {
			this.popup = this.getQueryPopup();

			// It is the VK_DELETE key. It is deleted the final character if
			// it
			// is visible.
			if (this.popup.isVisible()) {
				final Document document = this.popup.textField.getDocument();
				try {
					document.remove(document.getLength() - 1, 1);
					this.popup.pack();
				} catch (final Exception e) {
					Tree.logger.trace(null, e);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean insertSpaceStringInPopup(final KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_SPACE) {
			if (this.popup == null) {
				this.popup = new QueryPopup(this.resource);
			}
			if (this.popup.isVisible()) {
				final Character character = new Character(event.getKeyChar());
				final Document document = this.popup.textField.getDocument();
				try {
					document.insertString(document.getLength(), character.toString(), null);
					this.popup.pack();

				} catch (final Exception e) {
				}
				return true;
			}
		}
		return false;
	}

	protected boolean insertStringInPopup(final KeyEvent event) {
		final Character character = new Character(event.getKeyChar());
		Tree.logger.debug(Integer.toString(event.getKeyCode()));
		Tree.logger.debug("" + character.charValue());

		// If pop-up is visible, typed key is added. In opposite, we
		// show
		// the pop-up.
		if ((Character.isLetterOrDigit(event.getKeyChar())) || (event.getKeyCode() == KeyEvent.VK_SPACE)) {
			if (this.popup.isVisible()) {
				final Document document = this.popup.textField.getDocument();
				try {
					document.insertString(document.getLength(), character.toString(), null);
					this.popup.pack();
				} catch (final Exception e) {
					Tree.logger.trace(null, e);
				}
				return true;
			} else {
				// Pop-up has to be placed in (0,0) position, but
				// when
				// scroll exists this position is not valid.
				if (this.getParent() instanceof JViewport) {
					final Point p = ((JViewport) this.getParent()).getViewPosition();
					Tree.logger.debug("Tree parent is a JViewport. View position is: " + p.toString());
					this.popup.show(this, p.x, p.y);
					this.requestFocus();
				} else {
					Tree.logger.debug("Tree parent is not a JViewport. Is: " + this.getParent());
					this.popup.show(this, 0, 0);
					this.requestFocus();
				}
				final Document document = this.popup.textField.getDocument();
				try {
					document.insertString(document.getLength(), character.toString(), null);
					this.popup.pack();
				} catch (final Exception e) {
					Tree.logger.trace(null, e);
				}
				return true;
			}
		}
		return false;
	}

	protected boolean processAltUpDownLeftKeyEvent(final KeyEvent event, final TreePath selectedPath) {
		if ((event.getKeyCode() == KeyEvent.VK_DOWN) && event.isAltDown()) {
			// Next organizational visible node with the same text.
			final int selectedRow = this.getRowForPath(selectedPath);
			if (selectedRow >= (this.getRowCount() - 2)) {
				return true;
			}
			final OTreeNode selectedNode = (OTreeNode) selectedPath.getLastPathComponent();
			for (int i = selectedRow + 1; i < this.getRowCount(); i++) {
				final OTreeNode n = (OTreeNode) this.getPathForRow(i).getLastPathComponent();
				if ((n.isOrganizational() == selectedNode.isOrganizational())
						&& n.getText().equals(selectedNode.getText())) {
					this.setSelectionRow(i);
					this.scrollRowToVisible(i);
					return true;
				}
			}
		}

		if ((event.getKeyCode() == KeyEvent.VK_UP) && event.isAltDown()) {
			// Next organizational visible node with the same text.
			final int selectedRow = this.getRowForPath(selectedPath);
			if (selectedRow < 1) {
				return true;
			}
			final OTreeNode selectedNode = (OTreeNode) selectedPath.getLastPathComponent();
			for (int i = selectedRow - 1; i > 0; i--) {
				final OTreeNode n = (OTreeNode) this.getPathForRow(i).getLastPathComponent();
				if ((n.isOrganizational() == selectedNode.isOrganizational())
						&& n.getText().equals(selectedNode.getText())) {
					this.setSelectionRow(i);
					this.scrollRowToVisible(i);
					return true;
				}
			}
		}
		// If key pressed is VK_LEFT, it moves to parent
		// organizational node.
		if ((event.getKeyCode() == KeyEvent.VK_LEFT) && event.isAltDown()) {
			// Next organizational visible node with the same text.
			OTreeNode n = (OTreeNode) selectedPath.getLastPathComponent();
			TreePath p = selectedPath;
			while (!n.isRoot()) {
				p = p.getParentPath();
				n = (OTreeNode) p.getLastPathComponent();
				if (n.isOrganizational()) {
					this.setSelectionPath(p);
					this.scrollPathToVisible(p);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Overrides the parent method to process custom key events.
	 * @param event the key event
	 */
	@Override
	protected void processKeyEvent(final KeyEvent event) {
		// If it is a organizational node, pressing VK_ALT+VK_UP or VK_DOWN
		// cursor is fixed to the next organizational node in the same level.
		// For non-organizational nodes, it is passed to the next
		// non-organizational node
		// with the same text.

		final TreePath selectedPath = this.getSelectionPath();
		if (selectedPath != null) {
			final OTreeNode selectedNode = (OTreeNode) selectedPath.getLastPathComponent();
			if ((this.getSelectionCount() > 0) && !selectedNode.isRoot()) {
				if (event.getID() == KeyEvent.KEY_PRESSED) {
					if (this.processAltUpDownLeftKeyEvent(event, selectedPath)) {
						return;
					}
				}
			}
		}

		// Pop-up for search is showed when any node is selected. If selected
		// node
		// is organizational,it must
		// be expanded.
		if (((this.getSelectionCount() > 0)
				&& ((OTreeNode) this.getSelectionPath().getLastPathComponent()).isOrganizational()
				&& this.isExpanded(this.getSelectionPath()))
				|| ((this.getSelectionCount() > 0)
						&& !((OTreeNode) this.getSelectionPath().getLastPathComponent()).isOrganizational())) {
			// Pop-up window supports letters, digits, VK_DELETE key.
			if (event.getID() == KeyEvent.KEY_TYPED) {
				this.popup = this.getQueryPopup();

				if (Character.isLetterOrDigit(event.getKeyChar()) && this.insertStringInPopup(event)) {
					return;
				}
			}
		}
		if (event.getID() == KeyEvent.KEY_PRESSED) {
			if (this.removeStringInPopup(event) || this.insertSpaceStringInPopup(event)) {
				return;
			}
		}
		// Processing parent events.
		super.processKeyEvent(event);
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		v.add(Tree.updateTextKey);
		v.add(Tree.ascendingOrderKey);
		v.add(Tree.descendingOrderKey);
		v.add(Tree.editNodeKey);
		v.add(Tree.deleteNodeTextKey);
		v.add(Tree.fitSpaceToTreeKey);
		v.add(Tree.selectedQueryKey);

		v.add(Tree.orderByTextKey);

		if (this.popup != null) {
			v.addAll(this.popup.getTextsToTranslate());
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resource = resources;
		try {
			if (resources != null) {
				this.updateItem.setText(resources.getString(Tree.updateTextKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}

		try {
			if (resources != null) {
				this.deleteNodeItem.setText(resources.getString(Tree.deleteNodeTextKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}

		try {
			if (resources != null) {
				this.queryItem.setText(resources.getString(Tree.selectedQueryKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}
		try {
			if (resources != null) {
				this.ascOrderItem.setText(resources.getString(Tree.ascendingOrderKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}
		try {
			if (resources != null) {
				this.descOrderItem.setText(resources.getString(Tree.descendingOrderKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}
		try {
			if (resources != null) {
				this.editNodeItem.setText(resources.getString(Tree.editNodeKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}

		try {
			if (resources != null) {
				this.orderBy.setText(resources.getString(Tree.orderByTextKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}

		try {
			if (resources != null) {
				this.fitSpaceToTreeMenuItem.setText(resources.getString(Tree.fitSpaceToTreeKey));
			}
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				Tree.logger.debug(null, e);
			} else {
				Tree.logger.trace(null, e);
			}
		}

		if (this.popup != null) {
			this.popup.setResourceBundle(resources);
		}
		this.setNodesResourceBundle((TreeNode) this.getModel().getRoot(), resources);
		this.repaint(this.getBounds());
	}

	private void setNodesResourceBundle(final TreeNode node, final ResourceBundle resources) {
		if (node instanceof Internationalization) {
			((Internationalization) node).setResourceBundle(resources);
		}
		// For each child
		for (int i = 0; i < node.getChildCount(); i++) {
			final TreeNode childNode = node.getChildAt(i);
			this.setNodesResourceBundle(childNode, resources);
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setNodesLocale((TreeNode) this.getModel().getRoot(), l);
		this.repaint(this.getBounds());
	}

	private void setNodesLocale(final TreeNode node, final Locale l) {
		if (node instanceof Internationalization) {
			((Internationalization) node).setComponentLocale(l);
		}
		// For each child
		for (int i = 0; i < node.getChildCount(); i++) {
			final TreeNode childNode = node.getChildAt(i);
			this.setNodesLocale(childNode, l);
		}
	}

	@Override
	public void fireTreeWillExpand(final TreePath path) throws ExpandVetoException {
		if (this.processTreeWillExpandEvents) {
			super.fireTreeWillExpand(path);
		}
	}

	@Override
	public void fireTreeWillCollapse(final TreePath path) throws ExpandVetoException {
		if (this.processTreeWillCollapseEvents) {
			super.fireTreeWillCollapse(path);
		}
	}

	@Override
	public void fireValueChanged(final TreeSelectionEvent e) {
		if (this.processValueChangeEvents) {
			super.fireValueChanged(e);
		}
	}

	@Override
	public void updateUI() {
		final boolean collapseRoot = this.isCollapsed(0);
		this.processTreeWillExpandEvents = false;
		this.processTreeWillCollapseEvents = false;
		final TreeCellRenderer renderer = this.getCellRenderer();
		this.putClientProperty("JTree.lineStyle", "Angled");
		try {
			super.updateUI();
		} catch (final Exception e) {
			Tree.logger.error(null, e);
		}
		// This method overrides the established renderer. So, the previous
		// renderer must be recovered to set.
		if (renderer instanceof JComponent) {
			((JComponent) renderer).updateUI();
		}
		this.setCellRenderer(renderer);

		// Collapse
		if (collapseRoot) {
			this.collapseRow(0);
			this.collapseRow(1);
			this.collapsePath(this.getPathForRow(0));
		}
		this.setRowHeight(-1);
		this.processTreeWillExpandEvents = true;
		this.processTreeWillCollapseEvents = true;
	}

	@Override
	public void free() {
		if (this.popup != null) {
			this.popup.free();
		}
		this.updateItem.removeActionListener(this.menuListener);
		this.ascOrderItem.removeActionListener(this.menuListener);
		this.descOrderItem.removeActionListener(this.menuListener);
		this.menuListener = null;
		this.popupMenu.removeAll();
		this.remove(this.popupMenu);
		this.removeMouseListener(this.treeMouseListener);
		this.updateItem.removeActionListener(this.menuListener);
		this.ascOrderItem.removeActionListener(this.menuListener);
		this.descOrderItem.removeActionListener(this.menuListener);
		this.treeMouseListener = null;
		this.updateItem = null;
		this.currentTree = null;
		Tree.ascendingOrderKey = null;
		Tree.descendingOrderKey = null;
		Tree.updateTextKey = null;
		Tree.queryTextKey = null;
		this.treeBuilder = null;
		this.modelList.clear();
		this.modelList = null;
		this.listenerList = null;
		this.listenerList = null;
		this.popupMenu = null;
		this.ascOrderItem = null;
		this.descOrderItem = null;
		this.popup = null;
		this.setModel(null);
		final Container parentContainer = this.getParent();
		if (parentContainer != null) {
			parentContainer.remove(this);
		}
		if (ApplicationManager.DEBUG) {
			Tree.logger.debug("Free tree");

		}
	}

	public void updatePath(final TreePath path) {
		if (path == null) {
			return;
		}
		final Object node = path.getLastPathComponent();
		if (((OTreeNode) node).isOrganizational()) {
			this.collapsePath(path);
			this.expandPath(path);
		}
		this.fireValueChanged(new TreeSelectionEvent(this, path, true, null, path));
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
		final Object cellEditor = this.getCellEditor();
		if ((cellEditor != null) && (cellEditor instanceof OpenDialog)) {
			((OpenDialog) cellEditor).setParentFrame(parentFrame);
		}
	}

	/**
	 * Insertion notification. This path will be the associated path to the form where insertion has
	 * been produced. Collapsed nodes has not effect about tree aspect. If node is expanded, a new node
	 * is created with a icon that indicates new record. When it is not possible, organizational node is
	 * changed indicating that tree is not updated. Moreover, font color in node will be green.
	 * @param path the path
	 * @param keysValues the keysValues of inserted record
	 * @param locator the locator
	 * @param selectedChild the condition to allow selection children
	 */
	public void insertedNode(TreePath path, final Map keysValues, final EntityReferenceLocator locator,
			final boolean selectedChild) {
		// Look around the tree to obtain a organizational node. Gets the
		// keyValues correspondent
		// with organizational node.

		try {
			if (path == null) {
				return;
			}
			OTreeNode node = (OTreeNode) path.getLastPathComponent();
			if ((!node.isOrganizational()) && node.isRoot()) {
				return;
			}
			if (!node.isOrganizational()) {
				node = (OTreeNode) node.getParent();
				path = path.getParentPath();
			}

			// Sum one to the count
			node.setCount(node.getCount() + 1);

			// Now, when it is collapsed, we do nothing. Simply, we mark the
			// tree
			// state to notify that new data exist.
			if (this.isCollapsed(path)) {
				node.setRemark(true);
				if (this.getPathBounds(path) != null) {
					this.repaint(this.getPathBounds(path));
				} else {
					this.repaint();
				}
				return;
			}

			// If it is not collapsed,
			// We take one of the child nodes, because it is possible
			// that organizational nodes exist under this one
			final OTreeNode childNode = (OTreeNode) node.getChildAt(0);
			final List keys = node.getKeys();
			final Map kv = new Hashtable();
			for (int i = 0; i < keys.size(); i++) {
				// When any key is not encountered, it is indicated in parent
				// node
				if (!keysValues.containsKey(keys.get(i))) {
					node.setRemark(true);
					this.repaint(this.getPathBounds(path));
					return;
				}
				kv.put(keys.get(i), keysValues.get(keys.get(i)));
			}
			final long t = System.currentTimeMillis();
			final Entity ent = locator.getEntityReference(node.getEntityName());
			final List vectorAttributes = new Vector();
			for (int i = 0; i < keys.size(); i++) {
				vectorAttributes.add(keys.get(i));
			}
			final String[] attributes = node.getAttributes();
			for (int i = 0; i < attributes.length; i++) {
				if (!vectorAttributes.contains(attributes[i])) {
					vectorAttributes.add(attributes[i]);
				}
			}
			final EntityResult res = ent.query(kv, vectorAttributes, locator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				Tree.logger.debug("Inserted node: Error in query: " + res.getMessage());
				return;
			} else {
				if (!res.isEmpty()) {

					final OTreeNode oDataNode = (OTreeNode) node.clone();
					oDataNode.setOrganizational(false);

					final Map newNodeKeysValues = new Hashtable();
					for (int k = 0; k < keys.size(); k++) {
						final List vKeyValues = (List) res.get(keys.get(k));
						if (vKeyValues != null) {
							newNodeKeysValues.put(keys.get(k), vKeyValues.get(0));
						}
					}

					// Search the attributes
					for (int k = 0; k < attributes.length; k++) {
						final List vKeyValues = (List) res.get(attributes[k]);
						if (vKeyValues != null) {
							if (vKeyValues.get(0) != null) {
								newNodeKeysValues.put(attributes[k], vKeyValues.get(0));
							} else {
								newNodeKeysValues.put(attributes[k], "");
							}
						}
					}
					oDataNode.setKeysValues(newNodeKeysValues);

					oDataNode.setRemark(true);
					// The rest of children, not marked
					for (int i = 0; i < node.getChildCount(); i++) {
						final OTreeNode n = (OTreeNode) node.getChildAt(i);
						n.setRemark(false);
					}

					// Child nodes to these data node.
					for (int i = 0; i < childNode.getChildCount(); i++) {
						final OTreeNode n = (OTreeNode) childNode.getChildAt(i);
						oDataNode.add(n.cloneNodeAndChildren());
					}

					// When exists a node that is temporal, it is deleted
					if (node.getChildCount() == 1) {
						if (((OTreeNode) node.getChildAt(0)).isEmptyNode()) {
							node.remove((OTreeNode) node.getChildAt(0));
						}
					}
					node.add(oDataNode);

					((DefaultTreeModel) this.getModel()).nodeStructureChanged(node);
					final int[] index = new int[node.getChildCount()];
					for (int p = 0; p < index.length; p++) {
						index[p] = p;
					}
					((DefaultTreeModel) this.getModel()).nodesChanged(node, index);

					final long tf = System.currentTimeMillis();
					if (ApplicationManager.DEBUG_TIMES) {
						Tree.logger.debug(this.getClass().getName() + ": Time to insert node: " + (tf - t));
					}

					final TreePath childPath = path.pathByAddingChild(oDataNode);
					this.scrollPathToVisible(childPath);
					if (selectedChild) {
						this.setSelectionPath(childPath);
					} else {
						this.setSelectionPath(path);
					}
				} else {
					node.setRemark(true);
				}
			}
		} catch (final Exception e) {
			Tree.logger.error(null, e);
		}
	}

	/**
	 * Notification for deleded node. It is deleted the indicated node and selected the next node if
	 * condition selected is true.
	 * @param p the path
	 * @param keysValues the key values that determine the node
	 * @param selected the condition for allowing select a node after deleting
	 */
	public void deletedNode(final TreePath p, final Map keysValues, final boolean selected) {
		if (p == null) {
			return;
		}
		// If node is organizational:
		// -When tree is collapsed, it does nothing.
		// -When it is expanded, it will be collapsed. If keys are empty, node
		// is
		// deleted.
		// If node is not organizational:
		// -node is deleted.
		Object node = p.getLastPathComponent();
		TreePath path = p;
		if ((node != null) && (node instanceof OTreeNode)) {
			if (!((OTreeNode) node).isOrganizational()) {
				node = ((OTreeNode) node).getParent();
				path = p.getParentPath();
			}
			if (((OTreeNode) node).isOrganizational()) {
				if (!this.isCollapsed(path)) {
					for (int i = 0; i < ((OTreeNode) node).getChildCount(); i++) {
						boolean bKeysMatch = true;
						final OTreeNode childNode = (OTreeNode) ((OTreeNode) node).getChildAt(i);
						final Map kvNode = childNode.getKeysValues();
						final Enumeration enumKeys = Collections.enumeration(kvNode.keySet());
						while (enumKeys.hasMoreElements()) {
							final Object oKey = enumKeys.nextElement();
							if (keysValues.containsKey(oKey)) {
								// Checks if exists matches
								final Object oNodeValue = kvNode.get(oKey);
								final Object oSentValue = keysValues.get(oKey);
								if (!oNodeValue.equals(oSentValue)) {
									bKeysMatch = false;
									break;
								}
							} else {
								bKeysMatch = false;
								break;
							}
						}
						// If keys match, this is a node to delete.
						if (bKeysMatch) {
							// If only a node leaves, tree will be collapsed and
							// expanded
							// for not deleting the organizational children that
							// could
							// contain.

							((OTreeNode) node).setCount(((OTreeNode) node).getCount() - 1);
							if (((OTreeNode) node).getChildCount() == 1) {
								this.collapsePath(path);
								this.expandPath(path);
								if (selected) {
									this.setSelectionPath(path);
								}
								return;
							} else {
								((OTreeNode) node).remove(i);
								((DefaultTreeModel) this.getModel()).nodeStructureChanged((OTreeNode) node);
								final int[] index = new int[((OTreeNode) node).getChildCount()];
								for (int s = 0; s < index.length; s++) {
									index[s] = s;
								}
								((DefaultTreeModel) this.getModel()).nodesChanged((OTreeNode) node, index);
								final int childToSelect = Math.min(((OTreeNode) node).getChildCount() - 1, i);
								if (selected) {
									this.setSelectionPath(
											path.pathByAddingChild(((OTreeNode) node).getChildAt(childToSelect)));
								}
								return;
							}
						}
					}
					this.collapsePath(path);
					this.expandPath(path);
					return;
				} else {
					// When it is collapsed, selection node event to delete
					// fields.
					((OTreeNode) node).setCount(((OTreeNode) node).getCount() - 1);
					if (selected) {
						this.setSelectionPath(path);
					}
				}
			}
		}
	}

	/**
	 * Method called when a node is updated. When data changes, node is updated and tree is put in
	 * order. If node dissappears, organizational parent node is selected.
	 * @param treePath the path
	 * @param attributesValues the keys and values before updating node process.
	 * @param filter keyvalues to obtain the node to update
	 * @param locator the reference to locator
	 */
	public void updatedNode(final TreePath treePath, final Map attributesValues, final Map filter,
			final EntityReferenceLocator locator) {
		if ((treePath == null) || (attributesValues == null) || (filter == null) || attributesValues.isEmpty()
				|| filter.isEmpty()) {
			return;
		}

		final Object oNode = treePath.getLastPathComponent();
		if ((oNode != null) && (oNode instanceof OTreeNode)) {
			if (((OTreeNode) oNode).isOrganizational()) {
				this.updatedNodeAux(treePath, attributesValues, filter, locator);
			} else {
				// Gets the parent path
				this.updatedNodeAux(treePath.getParentPath(), attributesValues, filter, locator);
			}
		}
	}

	private void updateNodeData(final OTreeNode node, final Map newData, final TreePath treePath) {
		// Gets the results and establishes the new values for node.
		final Map hNodeData = node.getNodeData();
		final List keyNames = node.getKeys();
		for (int k = 0; k < keyNames.size(); k++) {
			final Object oNodeValue = hNodeData.get(keyNames.get(k));
			final Object oNewValue = newData.get(keyNames.get(k));
			if (oNewValue == null) {
				return;
			}
			if (oNodeValue == null) {
				if (ApplicationManager.DEBUG) {
					Tree.logger.debug(this.getClass().toString() + ": Key " + keyNames.get(k) + " is NULL in node "
							+ node.toString());
				}
				continue;
			}
			if (!oNodeValue.equals(oNewValue)) {
				// Change
				node.setKeysValues(keyNames.get(k), oNewValue);
			}
		}
		final String[] attributeNames = node.getAttributes();
		for (int k = 0; k < attributeNames.length; k++) {
			final Object oNodeValue = hNodeData.get(attributeNames[k]);
			final Object oNewValue = newData.get(attributeNames[k]);
			if (oNewValue == null) {
				continue;
			}
			if (!oNodeValue.equals(oNewValue)) {
				// Change
				node.setAttribute(attributeNames[k], oNewValue);
			}
		}
		((DefaultTreeModel) this.getModel()).nodeChanged(node);
		this.repaint(this.getPathBounds(treePath));
	}

	protected void updatedNodeAux(final TreePath treePath, final Map attributesValues, final Map keysValues,
			final EntityReferenceLocator locator) {
		final Object node = treePath.getLastPathComponent();
		if ((node != null) && (node instanceof OTreeNode)) {
			final Object oParent = ((OTreeNode) node).getParent();
			// When node is not expanded does nothing.
			if (!this.isExpanded(treePath)) {
				return;
			}
			for (int i = 0; i < ((OTreeNode) node).getChildCount(); i++) {
				boolean bKeysMatch = true;
				final OTreeNode child = (OTreeNode) ((OTreeNode) node).getChildAt(i);
				final Map kvNode = child.getKeysValues();
				final Enumeration enumKeys = Collections.enumeration(kvNode.keySet());
				while (enumKeys.hasMoreElements()) {
					final Object oKey = enumKeys.nextElement();
					if (keysValues.containsKey(oKey)) {
						// Checks if exist matches.
						final Object oNodeValue = kvNode.get(oKey);
						final Object oSentValue = keysValues.get(oKey);
						if (!oNodeValue.equals(oSentValue)) {
							bKeysMatch = false;
							break;
						}
					} else {
						bKeysMatch = false;
						break;
					}
				}
				// If keys matches, this is the node to update.
				if (bKeysMatch) {
					// If node is the root node, parent is null, we maintain
					// selected
					// this node and update the data node.
					if (oParent == null) {
						final TreePath childPath = treePath.pathByAddingChild(child);
						this.updateNodeData(child, attributesValues, childPath);
						if (!this.getSelectionPath().equals(childPath)) {
							this.setSelectionPath(childPath);
						} else {
							this.fireValueChanged(new TreeSelectionEvent(this, childPath, true, null, childPath));
						}
						return;
					}
					// We establish the new values for node. We take the values
					// from
					// attributes values, that contains the new values. If a key
					// has changed, we must delete because node will not stay in
					// this
					// level.

					boolean bChangeParentKeys = false;
					final Map parentNodeKeysValues = ((OTreeNode) oParent).getKeysValues();
					final List vParentKeys = child.getParentKeys();
					for (int j = 0; j < vParentKeys.size(); j++) {
						final Object parentKeyName = vParentKeys.get(j);
						// We have to use the equivalence, because in parent
						// node is
						// not necessary that name of keys matches.
						final String parentKeyNameInParentNode = child.getParentKeyNameInParentNode(parentKeyName.toString());
						final Object oNodeValue = parentNodeKeysValues.get(parentKeyNameInParentNode);
						final Object oAttributeValues = attributesValues.get(parentKeyName);
						if ((oNodeValue != null) && (oAttributeValues != null)) {
							if (!oNodeValue.equals(oAttributeValues)) {
								if (ApplicationManager.DEBUG) {
									Tree.logger.debug(this.getClass().toString() + " : Detected change in parentkey: "
											+ parentKeyName + " : Value in node: " + oNodeValue
											+ "    , Atributes value: " + oAttributeValues);
								}
								bChangeParentKeys = true;
								break;
							}
						} else {
							if (parentKeyNameInParentNode != null) {
								Tree.logger.debug(
										"TREE: Parentkey " + parentKeyNameInParentNode + " is null in the parent node");
							}
						}
					}
					if (bChangeParentKeys) {
						((OTreeNode) node).remove(i);
						((DefaultTreeModel) this.getModel()).nodeStructureChanged((OTreeNode) node);
						final int[] index = new int[((OTreeNode) node).getChildCount()];
						for (int s = 0; s < index.length; s++) {
							index[s] = s;
						}
						((DefaultTreeModel) this.getModel()).nodesChanged((OTreeNode) node, index);
						final TreePath childPath = treePath.pathByAddingChild(child);
						if ((this.getSelectionPath() != null) && !this.getSelectionPath().equals(childPath)) {
							this.setSelectionPath(childPath);
						} else {
							this.fireValueChanged(new TreeSelectionEvent(this, childPath, true, null, childPath));
						}
						return;
					}
					// If they are not changed, data node are updated.
					final TreePath pChild = treePath.pathByAddingChild(child);
					this.updateNodeData(child, attributesValues, pChild);
					if (!this.getSelectionPath().equals(pChild)) {
						this.setSelectionPath(pChild);
					} else {
						this.fireValueChanged(new TreeSelectionEvent(this, pChild, true, null, pChild));
					}
					return;
				}
			}
		}
		// Selects the organizational node.
		if (!this.getSelectionPath().equals(treePath)) {
			this.setSelectionPath(treePath);
		} else {
			this.fireValueChanged(new TreeSelectionEvent(this, treePath, true, null, treePath));
		}
	}

	public String getArchiveName() {
		return this.currentTree;
	}

	@Override
	public void initPermissions() {
		// Hides node without permissions
		final TreeModel m = this.getModel();
		if (m == null) {
			return;
		}
		final Object oRoot = m.getRoot();
		if ((oRoot != null) && (oRoot instanceof OTreeNode)) {
			this.initPermissions((OTreeNode) oRoot);
		}
	}

	private void initPermissions(final OTreeNode n) {
		final String id = n.getId();
		if (n.isOrganizational() && (id != null)) {
			try {
				final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
				if (manager != null) {
					if (this.nodeVisiblePermission == null) {
						this.nodeVisiblePermission = new TreePermission(this.currentTree, "visible", id, true);
					}
					this.nodeVisiblePermission.setAttribute(id);
					manager.checkPermission(this.nodeVisiblePermission);
				}
			} catch (final Exception e) {
				Tree.logger.debug(null, e);
				// Removes the node and returns for avoiding children checks.
				final TreeNode parentNode = n.getParent();
				if (parentNode != null) {
					if (parentNode instanceof OTreeNode) {
						((OTreeNode) parentNode).remove(n);
					}
				} else {
					// It is the root. Then, tree model is null, it must be
					// fixed.
					this.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Without data")));
				}
				return;
			}
		}
		final List vChildren = n.getChildren();
		if (vChildren != null) {
			for (int i = 0; i < vChildren.size(); i++) {
				final Object childNode = vChildren.get(i);
				if (childNode instanceof OTreeNode) {
					this.initPermissions((OTreeNode) childNode);
				}
			}
		}
	}

	protected void deleteNodes(final int[] rows) {
		if ((this.locator == null) || (rows.length == 0)) {
			return;
		}
		// / Uses OperationThread for allowing stops.
		final BufferedMessageDialog bMessages = BufferedMessageDialog.createBufferedMessageDialog(this.parentFrame,
				null);
		final OperationThread opThread = new OperationThread() {

			@Override
			public void run() {
				this.hasStarted = true;
				String sErrorMessage = Tree.M_ERROR_DELETED_NODE_es_ES;
				String sOKMessage = Tree.M_DELETED_NODE_SUCCESSFULLY_es_ES;
				String sOKMessageTitle = Tree.TITLE_DELETE_NODE_RESULT_SUCCESSFULLY_es_ES;
				String sErrorMessageTitle = Tree.TITLE_DELETE_NODE_RESULT_ERROR_es_ES;
				String sDeletedText = Tree.M_DELETED_NODE_es_ES;
				String sUpdatedText = Tree.M_UPDATED_NODE_es_ES;
				try {
					if (Tree.this.resource != null) {
						sUpdatedText = Tree.this.resource.getString(Tree.M_UPDATED_NODE);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				try {
					if (Tree.this.resource != null) {
						sDeletedText = Tree.this.resource.getString(Tree.M_DELETED_NODE);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				try {
					if (Tree.this.resource != null) {
						sErrorMessage = Tree.this.resource.getString(Tree.M_ERROR_DELETED_NODE);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				try {
					if (Tree.this.resource != null) {
						sOKMessage = Tree.this.resource.getString(Tree.M_DELETED_NODE_SUCCESSFULLY);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				try {
					if (Tree.this.resource != null) {
						sOKMessageTitle = Tree.this.resource.getString(Tree.TITLE_DELETE_NODE_RESULT_SUCCESSFULLY);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				try {
					if (Tree.this.resource != null) {
						sErrorMessageTitle = Tree.this.resource.getString(Tree.TITLE_DELETE_NODE_RESULT_ERROR);
					}
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						Tree.logger.debug(null, e);
					} else {
						Tree.logger.trace(null, e);
					}
				}
				boolean bErrors = false;
				try {
					// Nodes are used 1 by 1 and deleted.
					// Gets the data nodes and the paths to update.
					final List vUpdatePaths = new Vector();
					for (int i = 0; i < rows.length; i++) {
						if (this.isCancelled()) {
							bMessages.addErrorMessage("entity.operation_cancelled");
							this.hasFinished = true;
							return;
						}
						final TreePath path = Tree.this.getPathForRow(rows[i]);
						final Object node = path.getLastPathComponent();
						if ((node instanceof OTreeNode) && (!((OTreeNode) node).isOrganizational())
								&& ((OTreeNode) node).canDelete()) {
							final TreePath updateTreePath = path.getParentPath();
							if (!vUpdatePaths.contains(updateTreePath)) {
								vUpdatePaths.add(updateTreePath);
							}
							final Map hNodeKeys = ((OTreeNode) node).getKeysValues();
							final String entity = ((OTreeNode) node).getEntityName();
							try {
								this.status = sDeletedText + ((OTreeNode) node).toString();
								final Entity ent = Tree.this.locator.getEntityReference(entity);
								final EntityResult result = ent.delete(hNodeKeys, Tree.this.locator.getSessionId());
								if (result.getCode() == EntityResult.OPERATION_WRONG) {
									bMessages.addErrorMessage(sErrorMessage + " Node: " + ((OTreeNode) node).toString()
											+ " " + result.getMessage());
									bErrors = true;
								} else {
									bMessages.addMessage(sOKMessage + " Node: " + ((OTreeNode) node).toString());
								}
							} catch (final Exception e) {
								Tree.logger.trace(null, e);
								bMessages.addErrorMessage(sErrorMessage + " Node: " + ((OTreeNode) node).toString());
								bErrors = true;
							}
						}
					}
					for (int i = 0; i < vUpdatePaths.size(); i++) {
						if (this.isCancelled()) {
							bMessages.addErrorMessage("entity.operation_cancelled");
							this.hasFinished = true;
							return;
						}
						try {
							final TreePath p = (TreePath) vUpdatePaths.get(i);
							if (p != null) {
								final Object node = p.getLastPathComponent();
								if (node instanceof OTreeNode) {
									this.status = sUpdatedText + ((OTreeNode) node).toString();
								}
								Tree.this.collapsePath(p);
								Tree.this.expandPath(p);
							}
						} catch (final Exception e) {
							if (com.ontimize.gui.ApplicationManager.DEBUG) {
								Tree.logger.error(null, e);
							}
						}
					}
				} catch (final Exception ex) {
					bErrors = true;
					if (ApplicationManager.DEBUG) {
						Tree.logger.error(null, ex);
					} else {
						Tree.logger.trace(null, ex);
					}
				} finally {
					this.hasFinished = true;
					if (bErrors) {
						bMessages.setTitle(sErrorMessageTitle);
					} else {
						bMessages.setTitle(sOKMessageTitle);
					}
				}
			}
		};
		ApplicationManager.proccessOperation(opThread, 0);
		// Shows
		bMessages.pack();
		ApplicationManager.center(bMessages);
		bMessages.setVisible(true);
	}

	public void setFormManager(final ITreeFormManager formManager) {
		this.formManager = formManager;
	}

	public void setReferenceComponent(final ReferenceTreeComponent component) {
		this.compReference = component;
	}

	@Override
	public String getHelpIdString() {
		String sClassName = this.getClass().getName();
		sClassName = sClassName.substring(sClassName.lastIndexOf(".") + 1);
		return sClassName + "HelpId";
	}

	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			Tree.logger.error(null, e);
			return;
		}
	}

	public boolean getFitSpaceToTree() {
		return this.fitSpaceToTree;
	}

	@Override
	public void scrollRectToVisible(final Rectangle r) {
		super.scrollRectToVisible(r);
		this.repaint();
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	public void setFitSpaceToTree(final boolean adjust) {
		this.fitSpaceToTree = adjust;
		this.fitSpaceToTreeMenuItem.setSelected(adjust);
	}

	@Override
	public void initPreferences(final ApplicationPreferences prefs, final String user) {
		if (prefs != null) {
			final String pref = prefs.getPreference(user, Tree.ADJUST_TREE_SPACE);
			final boolean adjust = ApplicationManager.parseStringValue(pref, false);
			this.setFitSpaceToTree(adjust);
		}
	}

	@Override
	public void preferenceChanged(final PreferenceEvent e) {
		final String pref = e.getPreference();
		if ((pref != null) && pref.equals(Tree.ADJUST_TREE_SPACE)) {
			final String value = e.getValue();
			this.setFitSpaceToTree(ApplicationManager.parseStringValue(value, false));
		}
	}

	public final synchronized boolean setSelectedPath(final Map hKeysIds) {
		// Tree is looked around from root to query the keys-values. We looks
		// for
		// the organizational node.
		final Object oRoot = this.getModel().getRoot();
		this.searchedNodePath = null;
		this.queryNode(new TreePath(oRoot), ObjectTools.clone(hKeysIds), false);
		if (this.searchedNodePath != null) {
			this.setSelectionPath(this.searchedNodePath);
			this.scrollPathToVisible(this.searchedNodePath);
			Tree.logger.debug("node found: " + this.searchedNodePath.getLastPathComponent());
			return true;
		} else {
			return false;
		}
	}

	public final synchronized boolean setSelectedPath(final Map hKeysIds, final boolean forzarCompleto) {
		// Tree is looked around from root to query the keys-values. We looks
		// for
		// the organizational node.
		final Object oRoot = this.getModel().getRoot();
		this.searchedNodePath = null;
		this.queryNode(new TreePath(oRoot), ObjectTools.clone(hKeysIds), forzarCompleto);
		if (this.searchedNodePath != null) {
			this.setSelectionPath(this.searchedNodePath);
			this.scrollPathToVisible(this.searchedNodePath);
			Tree.logger.debug("node found: " + this.searchedNodePath.getLastPathComponent());
			return true;
		} else {
			return false;
		}
	}

	private TreePath searchedNodePath = null;

	protected synchronized void queryNode(final TreePath treePath, final Map keys, final boolean forceComplete) {
		if (this.searchedNodePath != null) {
			return;
		}
		final OTreeNode n = (OTreeNode) treePath.getLastPathComponent();
		if (n.isOrganizational() && forceComplete && ((n.getId() == null) || !keys.containsKey(n.getId()))) {
			return;
		}
		this.expandPath(treePath);
		final int childCount = n.getChildCount();
		boolean discardLevel = false;
		for (int i = 0; i < childCount; i++) {
			if (this.searchedNodePath != null) {
				return;
			}
			final OTreeNode childNode = (OTreeNode) n.getChildAt(i);
			if (n.getId() != null) {
				final Object v = keys.get(n.getId());
				if ((v != null) && !childNode.isOrganizational()) {
					discardLevel = true;
					if (v instanceof Map) {
						final Map kv = childNode.getKeysValues();
						boolean bMatch = true;
						final Enumeration cs = Collections.enumeration(kv.keySet());
						boolean hasElements = false;
						while (cs.hasMoreElements()) {
							hasElements = true;
							final Object oKey = cs.nextElement();
							final Object v2 = kv.get(oKey);
							final Map hC = (Map) v;
							if (hC.containsKey(oKey)) {
								if (v2.equals(hC.get(oKey))) {
									// nothing
								} else {
									bMatch = false;
									break;
								}
							} else {
								bMatch = false;
								break;
							}
						}
						if (bMatch && hasElements) {
							discardLevel = false;
							if (keys.size() == 1) {
								this.searchedNodePath = treePath.pathByAddingChild(childNode);
								return;
							} else {
								keys.remove(n.getId());
								this.queryNode(treePath.pathByAddingChild(childNode), keys, forceComplete);
								return;
							}
						}
					}
				}
			}

		}
		if (!discardLevel) {
			for (int i = 0; i < childCount; i++) {
				if (this.searchedNodePath != null) {
					return;
				}
				final OTreeNode childNode = (OTreeNode) n.getChildAt(i);
				this.queryNode(treePath.pathByAddingChild(childNode), keys, forceComplete);
			}
		}
	}

	public void setQueryDelay(final int t) {
		this.queryDelayTime = t;
	}

	public int getQueryDelay() {
		return this.queryDelayTime;
	}

	public void updateChildrenNodes(final OTreeNode parent, final EntityReferenceLocator locator) {
		if (!Tree.enabledRowCount) {
			return;
		}
		if (locator == null) {
			Tree.logger.debug("ReferencesLocator is null");
			return;
		}
		final OperationThread op = new OperationThread() {

			String OperationId = null;

			@Override
			public void cancel() {
				Tree.logger.debug("OperationThread cancelling");
				super.cancel();
			}

			@Override
			public void run() {
				this.hasStarted = true;
				this.setStatusText();
				try {
					for (int i = 0; i < parent.getChildCount(); i++) {
						if (this.cancelled) {
							break;
						}
						final OTreeNode orgChildNode = (OTreeNode) parent.getChildAt(i);
						if ((orgChildNode.getEntityName() == null) || orgChildNode.getEntityName().equals("")) {
							Tree.logger.debug("Node Entity is NULL.");
							if (!this.cancelled) {
								this.res = null;
							}
							orgChildNode.setCount(orgChildNode.getChildCount());
							continue;
						}
						final Entity entity = locator.getEntityReference(orgChildNode.getEntityName());

						if (entity == null) {
							Tree.logger.debug("Node Entity is NULL.");
							if (!this.cancelled) {
								this.res = null;
							}
							continue;
						}

						final Map hSearchKeysValues = new Hashtable();
						this.setKeysValues(parent, orgChildNode, hSearchKeysValues);
						final List attributesVector = new Vector();
						attributesVector.add(new CountDBFunctionName());
						try {
							// Query time
							EntityResult result = null;

							result = entity.query(hSearchKeysValues, attributesVector, locator.getSessionId());

							if (result instanceof AdvancedEntityResult) {
								orgChildNode.setTotalCount(((AdvancedEntityResult) result).getTotalRecordCount());
							}

							if (result != null) {
								if (result.getCode() == EntityResult.OPERATION_WRONG) {
									if (!this.cancelled) {
										this.res = result;
									}
									Tree.logger.debug(this.getClass().toString() + "-> " + result.getMessage());
									continue;
								}

							}
							if (!result.isEmpty()) {
								Object count = result.getRecordValues(0).get("COUNT");
								if (count == null) {
									count = result.getRecordValues(0).get("count");
								}
								if (count != null) {
									orgChildNode.setCount(((Number) count).intValue());
								}
							} else {
								orgChildNode.setCount(0);
								Tree.logger.debug(this.getClass().toString() + "-> Node results is empty:  "
										+ parent.getEntityName());
							}

							this.res = result;
						} catch (final Exception e) {
							Tree.logger.error("Tree exception: ", e);
							this.res = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
									e.getMessage());
							return;
						}
					}

				} catch (final Exception e) {
					this.res = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT,
							e.getMessage());
					Tree.logger.debug(null, e);
					return;
				}
				this.hasFinished = true;
				return;
			}

			protected void setStatusText() {
				try {
					if (Tree.this.resource != null) {
						this.status = Tree.this.resource.getString("M_CONSULTANDO");
					} else {
						this.status = "Querying...";
					}
				} catch (final Exception e) {
					Tree.logger.trace(null, e);
					this.status = "Querying...";
				}
			}

			protected void setKeysValues(final OTreeNode parent, final OTreeNode orgChildNode, final Map hSearchKeysValues) {
				if (!parent.isRoot()) {
					final Map hAssociatedFields = orgChildNode.getAssociatedDataField();
					// From keys and values, gets the associated field
					// key.
					final Enumeration enumAssociatedFieldKeys = Collections.enumeration(hAssociatedFields.keySet());
					while (enumAssociatedFieldKeys.hasMoreElements()) {
						final Object oParentField = enumAssociatedFieldKeys.nextElement();
						final Object oChildField = hAssociatedFields.get(oParentField);
						hSearchKeysValues.put(oChildField, parent.getValueForAttribute(oParentField));
					}
				}
			}
		};
		final OperationThread opThread = ApplicationManager.proccessOperation(this.parentFrame, op, 3000);
		return;
	}

	protected List getSQLOrderList(final OTreeNode node) {

		final List orderBy = new Vector();
		orderBy.add(new SQLStatementBuilder.SQLOrder(node.getSortAttr(), node.getAscending()));

		return orderBy;
	}

	protected Map buildQueryFilter(final OTreeNode parentNode) {
		final Map keysValuesSearch = new Hashtable();
		OTreeNode parentParentNode;
		if (!parentNode.isRoot()) {
			if (parentNode instanceof PageFetchTreeNode) {
				parentParentNode = (OTreeNode) parentNode.getParent().getParent();
			} else {
				parentParentNode = (OTreeNode) parentNode.getParent();
			}
			final Map associatedFields = parentNode.getAssociatedDataField();
			// Map hKeysValues = parentParentNode.getKeysValues();
			// For keys and values of parent node, it is getted the
			// associated field.
			final Enumeration enumAssociatedKeyFields = Collections.enumeration(associatedFields.keySet());
			while (enumAssociatedKeyFields.hasMoreElements()) {
				final Object oParentField = enumAssociatedKeyFields.nextElement();
				final Object oChildField = associatedFields.get(oParentField);
				keysValuesSearch.put(oChildField, parentParentNode.getValueForAttribute(oParentField));
			}
		} else { // If it is the root then search all
		}
		return keysValuesSearch;
	}

}
