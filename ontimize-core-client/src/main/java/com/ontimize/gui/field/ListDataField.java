package com.ontimize.gui.field;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a list. This field is similar to a table but only with a column.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ListDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(ListDataField.class);

	/**
	 * The number of rows. By default, 5.
	 */
	protected int rowsNumber = 5;

	protected int scrollPolicy = JScrollPane.VERTICAL_SCROLLBAR_ALWAYS;

	protected JScrollPane scrollPane;

	public static Border defaultNoFocusBorder = new EmptyBorder(1, 1, 1, 1);

	/**
	 * The class constructor. Calls to <code>super()</code>, creates the <code>JList</code> and inits
	 * parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public ListDataField(final Map parameters) {
		super();
		this.dataField = new JList();
		((JList) this.dataField).setCellRenderer(new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected,
					final boolean cellHasFocus) {
				this.setComponentOrientation(list.getComponentOrientation());
				if (isSelected) {
					this.setBackground(list.getSelectionBackground());
					this.setForeground(list.getSelectionForeground());
				} else {
					this.setBackground(list.getBackground());
					this.setForeground(list.getForeground());
				}

				if (value instanceof Icon) {
					this.setIcon((Icon) value);
					this.setText("");
				} else {
					this.setIcon(null);
					this.setText(value == null ? "" : value.toString());
				}
				this.setFont(list.getFont());
				if (!this.isEnabled()) {
					this.setForeground(Color.darkGray);
				}
				this.setBorder(cellHasFocus ? UIManager.getBorder("List.focusCellHighlightBorder")
						: ListDataField.defaultNoFocusBorder);
				return this;
			}
		});

		this.init(parameters);

		final Container parent = this.dataField.getParent();
		final LayoutManager layout = parent.getLayout();

		final GridBagConstraints constraints = ((GridBagLayout) layout).getConstraints(this.dataField);
		parent.remove(this.dataField);
		this.scrollPane = new JScrollPane(this.dataField);
		constraints.fill = this.redimensJTextField;
		constraints.weighty = (this.redimensJTextField == GridBagConstraints.VERTICAL)
				|| (this.redimensJTextField == GridBagConstraints.BOTH) ? 1.0 : 0;
		parent.add(this.scrollPane, constraints);

		this.scrollPane.setVerticalScrollBarPolicy(this.scrollPolicy);

		this.scrollPane.setBorder(this.dataField.getBorder());
		this.dataField.setBorder(null);

	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
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
	 *        <td>5</td>
	 *        <td>no</td>
	 *        <td>The number of showed rows.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>expand</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates whether field will be expanded vertically to use all space in parent
	 *        container.</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {

		super.init(parameters);

		final Object rows = parameters.get("rows");
		if (rows != null) {
			try {
				this.rowsNumber = Integer.parseInt(rows.toString());
			} catch (final Exception e) {
				ListDataField.logger.error(this.getClass().toString() + ". Error in parameter 'rows' " + e.getMessage(),
						e);
			}
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

		final String scrollbarPolicy = (String) parameters.get("vscroll");
		if (scrollbarPolicy != null) {
			if (scrollbarPolicy.equalsIgnoreCase("always")) {
				this.scrollPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
			} else if (scrollbarPolicy.equalsIgnoreCase("never")) {
				this.scrollPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER;
			} else if (scrollbarPolicy.equalsIgnoreCase("needed")) {
				this.scrollPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
			}
		}

	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		final Object c = super.getConstraints(parentLayout);
		if (((this.redimensJTextField == GridBagConstraints.VERTICAL)
				|| (this.redimensJTextField == GridBagConstraints.BOTH)) && (c instanceof GridBagConstraints)) {
			((GridBagConstraints) c).weighty = 0.01;
			final int fill = ((GridBagConstraints) c).fill;
			switch (fill) {
			case GridBagConstraints.HORIZONTAL:
				((GridBagConstraints) c).fill = GridBagConstraints.BOTH;
				break;
			case GridBagConstraints.NONE:
				((GridBagConstraints) c).fill = GridBagConstraints.VERTICAL;
				break;
			}
		}
		return c;
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		final List v = new Vector();
		final ListModel m = ((JList) this.dataField).getModel();
		for (int i = 0; i < m.getSize(); i++) {
			v.add(i, m.getElementAt(i));
		}
		return v;
	}

	/**
	 * Gets the value from a list.
	 * <p>
	 * @param index the index in the list
	 * @return the value
	 */
	public Object getValue(final int index) {
		if (this.isEmpty()) {
			return null;
		}
		final ListModel m = ((JList) this.dataField).getModel();
		if (index < m.getSize()) {
			return m.getElementAt(index);
		}
		return null;
	}

	public Object getSelectedObject() {
		if (this.isEmpty()) {
			return null;
		}
		final int index = ((JList) this.dataField).getSelectedIndex();
		if (index >= 0) {
			return this.getValue(index);
		}
		return null;
	}

	@Override
	public void setValue(final Object value) {
		if (value instanceof Vector) {
			((JList) this.dataField).setListData((Vector) value);
		} else if (value instanceof List) {
			((JList) this.dataField).setListData(new Vector((List) value));
		} else if (value instanceof Object[]) {
			((JList) this.dataField).setListData((Object[]) value);
		} else {
			this.deleteData();
		}
	}

	@Override
	public void deleteData() {
		((JList) this.dataField).setListData(new Vector());
	}

	@Override
	public boolean isEmpty() {
		if (((JList) this.dataField).getModel().getSize() == 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getSQLDataType() {
		return Types.OTHER;
	}

	/**
	 * Adds the list selection listener.
	 * <p>
	 * @param l the list selection listener
	 */
	public void addListSelectionListener(final ListSelectionListener l) {
		((JList) this.dataField).addListSelectionListener(l);
	}

	/**
	 * Removes the list selection listener.
	 * <p>
	 * @param l the list selection listener
	 */
	public void removeListSelectionListener(final ListSelectionListener l) {
		((JList) this.dataField).removeListSelectionListener(l);
	}

	public void setCellRenderer(final ListCellRenderer listCellRenderer) {
		((JList) this.dataField).setCellRenderer(listCellRenderer);
	}

	public void setSelectionMode(final int selectionMode) {
		((JList) this.dataField).setSelectionMode(selectionMode);

	}

	public void setSelectedIndex(final int index) {
		((JList) this.dataField).setSelectedIndex(index);

	}

	public ListCellRenderer getCellRenderer() {
		return ((JList) this.dataField).getCellRenderer();
	}

}
