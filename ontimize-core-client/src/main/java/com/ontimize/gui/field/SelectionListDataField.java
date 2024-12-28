package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ValueEvent;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.list.SortedListModel;
import com.ontimize.util.swing.list.SortedListModel.SortOrder;

/**
 * This class implements a list with the possibility to select the rows. It is possible to get the
 * selected data in rows with method <code>getValue<code>. This method returns a List with data of
 * selected rows or null when there are not selected rows.
 * <p>
 *
 * @author Imatia Innovation
 */
public class SelectionListDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(SelectionListDataField.class);

	/**
	 * This class manages the selection for an item.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class SelectableItem implements Comparable<SelectableItem> {

		private Object value = null;

		private boolean selected = false;

		/**
		 * Class constructor.
		 * <p>
		 * @param value the object
		 */
		public SelectableItem(final Object value) {
			this.value = value;
		}

		/**
		 * Gets the value.
		 * <p>
		 * @return the value
		 */
		public Object getValue() {
			return this.value;
		}

		/**
		 * Sets selected an item.
		 * <p>
		 * @param selected the selected condition
		 */
		public void setSelected(final boolean selected) {
			this.selected = selected;
		}

		/**
		 * Checks wheter is selected.
		 * <p>
		 * @return the selected condition
		 */
		public boolean isSelected() {
			return this.selected;
		}

		@Override
		public String toString() {
			if (this.value == null) {
				return "";
			}
			return this.value.toString();
		}

		@Override
		public int compareTo(final SelectableItem item) {
			if ((this.value == null) && (item.getValue() == null)) {
				return 0;
			}

			if (this.value == null) {
				return 1;
			}

			if (item.getValue() == null) {
				return -1;
			}
			return this.toString().compareTo(item.toString());
		}

		@Override
		public boolean equals(final Object obj) {
			return super.equals(obj);
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

	}

	/**
	 * This class manages the description for a selectable item.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	public static class DescriptionSelectableItem extends SelectableItem {

		/**
		 * A reference to a description. By default, null.
		 */
		protected Object description = null;

		/**
		 * The class constructor.
		 * <p>
		 * @param value the object to fix description
		 * @param description the description to set
		 */
		public DescriptionSelectableItem(final Object value, final Object description) {
			super(value);
			this.description = description;
		}

		/**
		 * Gets description.
		 * <p>
		 * @return the description
		 */
		public Object getDescription() {
			return this.description;
		}

		/**
		 * Sets the description for object.
		 * <p>
		 * @param description the description to set
		 */
		public void setDescription(final Object description) {
			this.description = description;
		}

		@Override
		public String toString() {
			if (this.getDescription() == null) {
				if (this.getValue() == null) {
					return "";
				}
				return this.getValue().toString();
			}
			return this.description.toString();
		}

	}

	private static class SelectableItemsListCellRenderer extends JCheckBox implements ListCellRenderer {

		public SelectableItemsListCellRenderer() {
			this.setBorderPaintedFlat(true);
		}

		@Override
		public String getName() {
			return "SelectableItem";
		}

		@Override
		public Component getListCellRendererComponent(final JList l, final Object v, final int r, final boolean sel, final boolean foc) {

			Color selectedBackground = UIManager.getColor("List[Selected].textBackground");
			Color selectedForeground = UIManager.getColor("List[Selected].textForeground");
			final Color disabledForeground = UIManager.getColor("\"SelectableItem\"[Disabled].textForeground");

			if (selectedBackground == null) {
				selectedBackground = UIManager.getColor("List.selectionBackground");
			}
			if (selectedForeground == null) {
				selectedForeground = UIManager.getColor("List.selectionForeground");
			}

			Color notSelectedBackground = UIManager.getColor("\"SelectableItem\".background");
			Color notSelectedForeground = UIManager.getColor("\"SelectableItem\".foreground");

			if (notSelectedBackground == null) {
				notSelectedBackground = UIManager.getColor("List.background");
			}
			if (notSelectedForeground == null) {
				notSelectedForeground = UIManager.getColor("List.foreground");
			}

			this.setOpaque(true);

			if (sel) {
				if (!l.isEnabled()) {
					this.setForeground(disabledForeground);
					this.setBackground(notSelectedBackground);
				} else {
					this.setForeground(selectedForeground);
					this.setBackground(selectedBackground);
				}

			} else {
				this.setForeground(notSelectedForeground);
				this.setBackground(notSelectedBackground);
			}
			if (v instanceof SelectableItem) {
				this.setText(((SelectableItem) v).toString());
				final boolean bSelected = ((SelectableItem) v).isSelected();
				this.setSelected(bSelected);
			}
			return this;
		}

	}

	private class SelectionListener extends MouseAdapter {

		@Override
		public void mouseReleased(final MouseEvent e) {
			if (SelectionListDataField.this.dataField.isEnabled()) {
				final JList jList = (JList) e.getSource();
				final int index = jList.locationToIndex(e.getPoint());
				if (index >= 0) {
					final Object oPreviousValue = SelectionListDataField.this.getValue();
					final SelectableItem it = (SelectableItem) jList.getModel().getElementAt(index);
					it.setSelected(!it.isSelected());
					jList.repaint(jList.getCellBounds(index, index));
					SelectionListDataField.this.fireValueChanged(SelectionListDataField.this.getValue(), oPreviousValue,
							ValueEvent.PROGRAMMATIC_CHANGE);
				}
			}
		}

	}

	private class SimpleSelectionListener extends MouseAdapter {

		@Override
		public void mouseReleased(final MouseEvent e) {
			final JList jList = (JList) e.getSource();
			final int index = jList.locationToIndex(e.getPoint());
			if (index >= 0) {
				final Object oPreviousValue = SelectionListDataField.this.getValue();
				for (int i = 0; i < jList.getModel().getSize(); i++) {
					final SelectableItem it = (SelectableItem) jList.getModel().getElementAt(i);
					if (it.isSelected() && (i != index)) {
						it.setSelected(false);
					}
				}
				final SelectableItem it = (SelectableItem) jList.getModel().getElementAt(index);
				it.setSelected(!it.isSelected());
				jList.repaint();
				SelectionListDataField.this.fireValueChanged(SelectionListDataField.this.getValue(), oPreviousValue,
						ValueEvent.PROGRAMMATIC_CHANGE);
			}
		}

	}

	private static class EJList extends JList {

		private int columnsWidth = -1;

		public EJList() {
			super(new SortedListModel());
		}

		public void setColumnWidth(final int a) {
			this.columnsWidth = a;
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension d = super.getPreferredSize();
			if (this.columnsWidth > 0) {
				d.width = this.columnsWidth * this.getFontMetrics(this.getFont()).charWidth('M');
			}
			return d;
		}

		@Override
		public Dimension getPreferredScrollableViewportSize() {
			final Dimension d = super.getPreferredScrollableViewportSize();
			if (this.columnsWidth > 0) {
				d.width = this.columnsWidth * this.getFontMetrics(this.getFont()).charWidth('M');
			}
			return d;
		}

	}

	/**
	 * The class constructor. Calls to <code>super()</code>, creates the list and inits parameters.
	 * <p>
	 * @param parameters the <CODE>Hashtable</CODE> with parameters
	 */
	public SelectionListDataField(final Map parameters) {
		super();
		this.dataField = new EJList();
		this.init(parameters);
	}

	@Override
	public Object getValue() {
		final ListModel model = ((JList) this.dataField).getModel();
		if (model.getSize() == 0) {
			return null;
		} else {
			final List v = new Vector();
			for (int i = 0; i < model.getSize(); i++) {
				final Object oElement = model.getElementAt(i);
				if (oElement instanceof SelectableItem) {
					if (((SelectableItem) oElement).isSelected()) {
						v.add(oElement);
					}
				}
			}
			if (v.isEmpty()) {
				return null;
			} else {
				return v;
			}
		}
	}

	@Override
	public void setValue(final Object v) {
		if (v == null) {
			this.deleteData();
			return;
		} else {
			if (v instanceof List) {
				final Object oPreviousValue = this.getValue();
				final List v2 = (List) v;
				final ListModel model = ((JList) this.dataField).getModel();
				for (int i = 0; i < model.getSize(); i++) {
					final SelectableItem item = (SelectableItem) model.getElementAt(i);
					if (v2.contains(item.getValue())) {
						item.setSelected(true);
					} else {
						item.setSelected(false);
					}
				}

				this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			}
		}
	}

	@Override
	public void deleteData() {
		final Object oPreviousValue = this.getValue();
		final ListModel model = ((JList) this.dataField).getModel();
		for (int i = 0; i < model.getSize(); i++) {
			if (model.getElementAt(i) instanceof SelectableItem) {
				((SelectableItem) model.getElementAt(i)).setSelected(false);
			}
		}
		this.dataField.repaint();
		this.fireValueChanged(this.getValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	/**
	 * Sets the data list. At starting, none is selected.
	 * <p>
	 * @param items the <CODE>Vector</CODE> with items
	 */
	public void setItems(final Vector items) {
		final DefaultListModel model = (DefaultListModel) ((JList) this.dataField).getModel();
		model.clear();
		if (items == null) {
			return;
		}
		for (int i = 0; i < items.size(); i++) {
			model.add(i, new SelectableItem(items.get(i)));
		}
	}

	/**
	 * Sets items from a <code>Hashtable</code>.
	 * <p>
	 * @param items the <code>Hashtable</code> with items
	 */
	public void setItems(final Map items) {
		final DefaultListModel model = (DefaultListModel) ((JList) this.dataField).getModel();
		model.clear();
		if (items == null) {
			return;
		}
		final Enumeration enu = Collections.enumeration(items.keySet());
		while (enu.hasMoreElements()) {
			final Object k = enu.nextElement();
			final Object d = items.get(k);
			model.addElement(new DescriptionSelectableItem(k, d));
		}
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <CODE>Hashtable</CODE> with parameters.
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>rows</td>
	 *        <td></td>
	 *        <td>8</td>
	 *        <td>no</td>
	 *        <td>The number of rows showed.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>selection</td>
	 *        <td><i>simple/multiple</td>
	 *        <td>multiple</td>
	 *        <td>no</td>
	 *        <td>The type of selection.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>sort</td>
	 *        <td><i>unordered/ascending/descending</td>
	 *        <td>unordered</td>
	 *        <td>no</td>
	 *        <td>The type of sort.</td>
	 *        </tr>
	 *        </TABLE>
	 */

	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final Object rows = parameters.get("rows");
		if (rows != null) {
			try {
				final int iRowNumber = Integer.parseInt(rows.toString());
				((JList) this.dataField).setVisibleRowCount(iRowNumber);
			} catch (final Exception e) {
				SelectionListDataField.logger.error("{}: Error in parameter 'rows' -> {}", this.getClass().toString(),
						e.getMessage(), e);
			}
		} else {
			((JList) this.dataField).setVisibleRowCount(8);
		}

		final Object expand = parameters.get("expand");
		if ("yes".equalsIgnoreCase((String) expand)) {
			switch (this.redimensJTextField) {
			case GridBagConstraints.NONE:
				this.redimensJTextField = GridBagConstraints.VERTICAL;
				break;
			case GridBagConstraints.HORIZONTAL:
				this.redimensJTextField = GridBagConstraints.BOTH;
				break;
			}
		}

		// ((JList) this.dataField).setModel(new DefaultListModel());
		final GridBagConstraints constraints = ((GridBagLayout) this.getLayout()).getConstraints(this.dataField);

		this.remove(this.dataField);
		final JScrollPane scroll = new JScrollPane(this.dataField);
		this.add(scroll,
				new GridBagConstraints(constraints.gridx, constraints.gridy, 1, 1, this.weightDataFieldH, 1,
						GridBagConstraints.EAST, this.redimensJTextField,
						new Insets(2, 2, 2, 2), 0, 0));
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		final Object selection = parameters.get("selection");
		if ("simple".equals(selection)) {
			this.dataField.addMouseListener(new SimpleSelectionListener());
		} else {
			this.dataField.addMouseListener(new SelectionListener());
		}

		((JList) this.dataField).setCellRenderer(new SelectableItemsListCellRenderer());
		if (parameters.get(DataField.SIZE) != null) {
			((EJList) this.dataField).setColumnWidth(this.fieldSize);
		}

		final String sort = ParseUtils.getString((String) parameters.get("sort"), "unordered");
		final JList list = (JList) this.dataField;
		if (list.getModel() instanceof SortedListModel) {
			final SortedListModel model = (SortedListModel) list.getModel();
			if (SortedListModel.SortOrder.ASCENDING.name().equalsIgnoreCase(sort)) {
				model.setSortOrder(SortOrder.ASCENDING);
			} else if (SortedListModel.SortOrder.DESCENDING.name().equalsIgnoreCase(sort)) {
				model.setSortOrder(SortOrder.DESCENDING);
			}
		}

	}

	@Override
	public boolean isEmpty() {
		final DefaultListModel model = (DefaultListModel) ((JList) this.dataField).getModel();
		for (int i = 0; i < model.size(); i++) {
			if (((SelectableItem) model.get(i)).isSelected()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

}
