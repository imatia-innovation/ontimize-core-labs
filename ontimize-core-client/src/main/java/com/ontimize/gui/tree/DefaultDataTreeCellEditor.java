package com.ontimize.gui.tree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.RealDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public class DefaultDataTreeCellEditor implements TreeCellEditor, OpenDialog {

	private static final Logger logger = LoggerFactory.getLogger(DefaultDataTreeCellEditor.class);

	public static boolean DEBUG_RENDERER = false;

	protected DateDataField dateField = new DateDataField(new Hashtable());

	protected IntegerDataField integerField = new IntegerDataField(new Hashtable());

	protected RealDataField doubleField = new RealDataField(new Hashtable());

	protected TextDataField textField = new TextDataField(new Hashtable());

	protected DataComponent currentEditor = null;

	protected int clickCount = 1;

	protected List listeners = new Vector();

	protected Object value = null;

	protected OTreeNode editingNode = null;

	protected JTree tree = null;

	protected TreePath editingNodePath = null;

	protected EntityReferenceLocator locator = null;

	protected Frame parentFrame = null;

	protected ResourceBundle resources = null;

	public DefaultDataTreeCellEditor(final EntityReferenceLocator locator) throws IllegalArgumentException {
		super();
		if (locator == null) {
			throw new IllegalArgumentException("Locator parameter can not be null");
		}
		this.locator = locator;
		final KeyAdapter kl = new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					DefaultDataTreeCellEditor.this.stopCellEditing();
				}
			}
		};
		this.integerField.getDataField().addKeyListener(kl);
		this.dateField.getDataField().addKeyListener(kl);
		this.doubleField.getDataField().addKeyListener(kl);
		this.textField.getDataField().addKeyListener(kl);
		final FocusAdapter fl = new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				DefaultDataTreeCellEditor.this.cancelCellEditing();
			}
		};
		this.integerField.getDataField().addFocusListener(fl);
		this.dateField.getDataField().addFocusListener(fl);
		this.doubleField.getDataField().addFocusListener(fl);
		this.textField.getDataField().addFocusListener(fl);
	}

	@Override
	public Object getCellEditorValue() {
		if (DefaultDataTreeCellEditor.DEBUG_RENDERER) {
			DefaultDataTreeCellEditor.logger.debug(this.getClass().getName() + " : get cell editor value");
		}
		return this.value;
	}

	@Override
	public boolean isCellEditable(final EventObject anEvent) {
		if (DefaultDataTreeCellEditor.DEBUG_RENDERER) {
			DefaultDataTreeCellEditor.logger.debug(this.getClass().getName() + " : is Cell Editable");
		}
		if (anEvent instanceof MouseEvent) {
			if (((MouseEvent) anEvent).isAltDown()) {
				final Object oSource = anEvent.getSource();
				if (oSource instanceof JTree) {
					final Object oNode = ((JTree) oSource)
							.getClosestPathForLocation(((MouseEvent) anEvent).getX(), ((MouseEvent) anEvent).getY())
							.getLastPathComponent();
					if (oNode instanceof OTreeNode) {
						// ((MouseEvent)anEvent).consume();
						if (((OTreeNode) oNode).isOrganizational()) {
							return false;
						} else {
							if (((OTreeNode) oNode).isEmptyNode()) {
								return false;
							} else {
								// If it has more than one attribute then it is
								// not editable
								final String[] attributes = ((OTreeNode) oNode).getAttributes();
								if ((attributes != null) && (attributes.length == 1)) {
									if (((MouseEvent) anEvent).getClickCount() >= this.clickCount) {
										return true;
									} else {
										return false;
									}
								} else {
									return false;
								}
							}
						}
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public boolean shouldSelectCell(final EventObject e) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		if (this.currentEditor.isEmpty()) {
			this.fireEditingStopped();
			return false;
		} else {
			if (this.editingNode != null) {
				this.value = this.currentEditor.getValue();
				this.fireEditingStopped();
				// Use entity to update
				try {
					final String sNodeAttribute = this.editingNode.getAttributes()[0];
					final Entity ent = this.locator.getEntityReference(this.editingNode.getEntityName());
					final Map filter = this.editingNode.getKeysValues();
					final Map hUpateAttributes = new Hashtable();
					hUpateAttributes.put(sNodeAttribute, this.value);
					final EntityResult res = ent.update(hUpateAttributes, filter, this.locator.getSessionId());
					if (res.getCode() == EntityResult.OPERATION_WRONG) {
						DefaultDataTreeCellEditor.logger.debug("Error updating data: " + res.getMessage());
						MessageDialog.showMessage(this.parentFrame, res.getMessage(), null,
								JOptionPane.ERROR_MESSAGE, this.resources);
						return false;
					} else {
						this.editingNode.setAttribute(sNodeAttribute, this.value);
						// Update form values
						if (this.tree instanceof Tree) {
							if (this.tree.isCollapsed(this.editingNodePath)) {
								((Tree) this.tree).updatePath(this.editingNodePath);
							} else {
								this.tree.collapsePath(this.editingNodePath);
							}
						}
						return true;
					}
				} catch (final Exception e) {
					MessageDialog.showMessage(this.parentFrame, "Editor.ErrorUpdatingData", e.getMessage(),
							JOptionPane.ERROR_MESSAGE, this.resources);
					DefaultDataTreeCellEditor.logger
					.error(this.getClass().toString() + ": " + "Error updating data: " + e.getMessage(), e);
					return false;
				}
			} else {
				this.fireEditingStopped();
				return false;
			}
		}
	}

	@Override
	public void cancelCellEditing() {
		this.fireEditingCanceled();
	}

	@Override
	public void addCellEditorListener(final CellEditorListener l) {
		this.listeners.add(l);
	}

	@Override
	public void removeCellEditorListener(final CellEditorListener l) {
		this.listeners.remove(l);
	}

	protected void fireEditingStopped() {
		if (DefaultDataTreeCellEditor.DEBUG_RENDERER) {
			DefaultDataTreeCellEditor.logger.debug("Editing Stopped event");
		}
		for (int i = 0; i < this.listeners.size(); i++) {
			((CellEditorListener) this.listeners.get(i)).editingStopped(new ChangeEvent(this));
		}
	}

	protected void fireEditingCanceled() {
		if (DefaultDataTreeCellEditor.DEBUG_RENDERER) {
			DefaultDataTreeCellEditor.logger.debug("Editinng canceled event");
		}
		for (int i = 0; i < this.listeners.size(); i++) {
			((CellEditorListener) this.listeners.get(i)).editingCanceled(new ChangeEvent(this));
		}
	}

	@Override
	public Component getTreeCellEditorComponent(final JTree tree, final Object value, final boolean selected, final boolean expanded,
			final boolean leaf, final int row) {
		if (DefaultDataTreeCellEditor.DEBUG_RENDERER) {
			DefaultDataTreeCellEditor.logger.debug("Get Tree Cell Editor Component for node: " + value.toString());
		}
		final Object oNode = value;
		if (oNode instanceof OTreeNode) {
			if (!((OTreeNode) oNode).isOrganizational()) {
				// Save the node.
				this.editingNode = (OTreeNode) oNode;
				this.tree = tree;
				this.editingNodePath = tree.getPathForRow(row);
				this.dateField.deleteData();
				this.integerField.deleteData();
				this.doubleField.deleteData();
				this.textField.deleteData();
				final String[] attributes = this.editingNode.getAttributes();
				if ((attributes == null) || (attributes.length != 1)) {
					return null;
				}
				final String sAttribute = attributes[0];
				final Object oEditingValue = this.editingNode.getNodeData().get(sAttribute);
				if (oEditingValue == null) {
					return null;
				}
				this.value = oEditingValue;
				if (oEditingValue instanceof Date) {
					this.dateField.setValue(oEditingValue);
					final JComponent component = this.dateField.getDataField();
					component.setBorder(new LineBorder(Color.black));
					this.currentEditor = this.dateField;
					return component;
				} else if ((oEditingValue instanceof Integer) || (oEditingValue instanceof Short)) {
					this.integerField.setValue(oEditingValue);
					final JComponent component = this.integerField.getDataField();
					component.setBorder(new LineBorder(Color.black));
					this.currentEditor = this.integerField;
					return component;
				} else if ((oEditingValue instanceof Double) || (oEditingValue instanceof Float)) {
					this.doubleField.setValue(oEditingValue);
					final JComponent component = this.doubleField.getDataField();
					component.setBorder(new LineBorder(Color.black));
					this.currentEditor = this.doubleField;
					return component;
				} else {
					this.textField.setValue(oEditingValue);
					final JComponent c = this.textField.getDataField();
					c.setBorder(new LineBorder(Color.black));
					this.currentEditor = this.textField;
					return c;
				}
			} else {
				this.currentEditor = null;
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public void setParentFrame(final Frame parentFrame) {
		this.parentFrame = parentFrame;
	}

}
