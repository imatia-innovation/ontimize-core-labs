package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.BooleanCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.ExtImageCellRenderer;
import com.ontimize.gui.table.ExtendedTableModel;
import com.ontimize.gui.table.HeadCellRenderer;
import com.ontimize.gui.table.ObjectCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.ontimize.gui.table.RowHeadCellRenderer;
import com.ontimize.gui.table.SortTableCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.report.ReportUtils;
import com.ontimize.security.ClientSecurityManager;

/**
 * This class implements a table viewer for an Entity result.
 * <p>
 *
 * @author Imatia Innovation
 */
public class EntityResultViewer extends JPanel implements DataComponent, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(EntityResultViewer.class);

	/**
	 * An instance of the table.
	 */
	protected JTable table = new JTable() {

		@Override
		public void setModel(final TableModel m) {
			final TableModel mAct = this.getModel();
			if (mAct != null) {
				if (mAct instanceof TableSorter) {
					try {
						((TableSorter) mAct).free();
					} catch (final Exception ex) {
						EntityResultViewer.logger.trace(null, ex);
					}
				}
			}
			super.setModel(m);
		}
	};

	/**
	 * The reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * The visible permission reference. By default, null.
	 */
	protected FormPermission visiblePermission = null;

	/**
	 * The enabled permission reference. By default, null.
	 */
	protected FormPermission enabledPermission = null;

	/**
	 * The reference to parent form. By default, null.
	 */
	protected Form parentForm = null;

	/**
	 * A reference to date renderer. By default, null.
	 */
	protected DateCellRenderer dateRenderer = null;

	/**
	 * The reference to a row header renderer. By default, null.
	 */
	protected RowHeadCellRenderer rowHeaderRenderer = null;

	/**
	 * A reference to a boolean cell renderer. By default, null.
	 */
	protected BooleanCellRenderer booleanRenderer = null;

	/**
	 * A reference to a real cell renderer. By default, null.
	 */
	protected RealCellRenderer realRenderer = null;

	/**
	 * A reference to a object renderer. By default, null.
	 */
	protected ObjectCellRenderer objectRenderer = null;

	/**
	 * A reference to an image renderer. By default, null.
	 */
	protected ExtImageCellRenderer imageRenderer = null;

	/**
	 * A reference to a header renderer. By default, null.
	 */
	protected HeadCellRenderer headerRenderer = null;

	/**
	 * An instance of pop-up menu.
	 */
	protected ExtendedJPopupMenu popup = new ExtendedJPopupMenu();

	/**
	 * The key to copy to clipboard the tip.
	 */
	protected String copyClipboardKey = Table.TIP_CLIPBOARD_COPY;

	/**
	 * A instance of the copy menu item.
	 */
	protected JMenuItem copyMenu = new JMenuItem(this.copyClipboardKey);

	/**
	 * An instance of a control panel.
	 */
	protected JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

	/**
	 * A reference to report button. By default, null.
	 */
	protected JButton reportButton = null;

	/**
	 * A reference to resources file. By default, null.
	 */
	protected ResourceBundle resources = null;

	/**
	 * The class constructor. Calls to <code>super()</code>, initializes parameters, sets renderer and
	 * installs pop-up and report button.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public EntityResultViewer(final Map parameters) {
		super();
		this.setLayout(new BorderLayout());
		this.init(parameters);
		final JScrollPane scroll = new JScrollPane(this.table);
		this.add(scroll);
		this.add(this.controlPanel, BorderLayout.NORTH);
		this.setRenderers();
		this.installPopup();
		this.installReportButton();
	}

	/**
	 * Installs the report button.
	 */
	protected void installReportButton() {
		if (!com.ontimize.report.ReportManager.isReportsEnabled()) {
			return;
		}
		this.reportButton = new TableButton();
		final ImageIcon icon = ImageManager.getIcon(ImageManager.PAGE);
		if (icon != null) {
			this.reportButton.setIcon(icon);
		} else {
			this.reportButton.setText("Reports");
		}
		this.reportButton.setToolTipText(ApplicationManager.getTranslation("report.custom_reports", this.resources));
		this.reportButton.setEnabled(false);
		this.reportButton.setMargin(new Insets(0, 0, 0, 0));
		this.controlPanel.add(this.reportButton);
		this.reportButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!EntityResultViewer.this.isEmpty()) {
					EntityResultViewer.this.showCustomReportsWindow();
				}
			}
		});
	}

	/**
	 * Installs the copy to clipboard menu.
	 */
	protected void installPopup() {
		this.popup.add(this.copyMenu);
		this.copyMenu.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EntityResultViewer.this.copyClipboard();
			}
		});
		this.table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getModifiers() == InputEvent.META_MASK) && (e.getClickCount() == 1)) {
					EntityResultViewer.this.popup.show(EntityResultViewer.this.table, e.getX(), e.getY());
				}
			}
		});
	}

	/**
	 * Implements the copy-to-clipboard operation.
	 */
	protected void copyClipboard() {
		try {
			EntityResultViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final StringSelection sselection = new StringSelection(this.getExcelString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
		} catch (final Exception e) {
			EntityResultViewer.logger.error(null, e);
		} finally {
			EntityResultViewer.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Gets the string with all table components (Columns separated by \t and rows by \n).
	 * <p>
	 * @return the string data
	 */
	public String getExcelString() {
		final long t = System.currentTimeMillis();
		// Creates the string with all the data in the table
		// Columns separated by tab and rows with enter
		// Get the table values.
		if (ApplicationManager.DEBUG_TIMES) {
			EntityResultViewer.logger.debug("EntityResultViewer: TLocale = " + (System.currentTimeMillis() - t));
		}

		final StringBuilder sbHeader = new StringBuilder();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			if (tc.getIdentifier() != null) {
				sbHeader.append(tc.getHeaderValue() + "\t");
			}
		}
		final StringBuilder sbValues = new StringBuilder("");

		for (int j = 0; j < this.table.getRowCount(); j++) {
			sbValues.append("\n");
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final TableColumn tc = this.table.getColumnModel().getColumn(i);
				if (tc.getIdentifier() != null) {
					final Object oValue = this.table.getValueAt(j, i);
					final TableCellRenderer r = this.table.getCellRenderer(j, i);
					final Component c = r.getTableCellRendererComponent(this.table, oValue, false, false, j, i);
					if (c instanceof JLabel) {
						final String sText = ((JLabel) c).getText();
						sbValues.append(sText);
						sbValues.append("\t");
						continue;
					} else if (c instanceof JTextComponent) {
						final String sText = ((JTextComponent) c).getText();
						sbValues.append(sText);
						sbValues.append("\t");
						continue;
					} else {
						String sText = "";
						if (oValue != null) {
							if (oValue instanceof Boolean) {
								if (((Boolean) oValue).booleanValue()) {
									sText = "Yes";
								} else {
									sText = "No";
								}
							} else {
								sText = oValue.toString();
							}
						}
						sbValues.append(sText);
						sbValues.append("\t");
						continue;
					}
				}
			}
		}
		sbValues.append("\n");

		final long tf = System.currentTimeMillis();
		if (ApplicationManager.DEBUG_TIMES) {
			EntityResultViewer.logger.debug("Table: Time to create excel string: " + (tf - t) + " miliseconds");
		}
		return sbHeader + sbValues.toString();
	}

	/**
	 * Creates instances of all defined renderer and sets them to the table.
	 */
	protected void setRenderers() {
		if (this.dateRenderer == null) {
			this.headerRenderer = new HeadCellRenderer();

			this.dateRenderer = new DateCellRenderer();
			this.rowHeaderRenderer = new RowHeadCellRenderer(this.table);

			this.booleanRenderer = new BooleanCellRenderer();
			this.realRenderer = new RealCellRenderer();
			this.objectRenderer = new ObjectCellRenderer();

			this.imageRenderer = new ExtImageCellRenderer(100, 100, true);

		}
		this.table.setDefaultRenderer(Timestamp.class, this.dateRenderer);
		this.table.setDefaultRenderer(RowHeadCellRenderer.class, this.rowHeaderRenderer);
		this.table.setDefaultRenderer(java.sql.Date.class, this.dateRenderer);
		this.table.setDefaultRenderer(java.util.Date.class, this.dateRenderer);

		this.table.setDefaultRenderer(Boolean.class, this.booleanRenderer);
		this.table.setDefaultRenderer(Float.class, this.realRenderer);
		this.table.setDefaultRenderer(Double.class, this.realRenderer);
		this.table.setDefaultRenderer(Object.class, this.objectRenderer);
		this.table.setDefaultRenderer(String.class, this.objectRenderer);
		this.table.setDefaultRenderer(Number.class, this.realRenderer);
		this.table.setDefaultRenderer(BytesBlock.class, this.imageRenderer);

	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters.
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
	 *        <td>attr</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The attribute for component.</td>
	 *        </tr>
	 *        </Table>
	 */
	@Override
	public void init(final Map parameters) {
		this.attribute = parameters.get("attr");
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void initPermissions() {
		if (ApplicationManager.getClientSecurityManager() != null) {
			ClientSecurityManager.registerSecuredElement(this);
		}
		final boolean pVisible = this.checkVisiblePermission();
		if (!pVisible) {
			this.setVisible(false);
		}

		final boolean pEnabled = this.checkEnabledPermission();
		if (!pEnabled) {
			this.setEnabled(false);
		}

	}

	/**
	 * Checks the visible permission condition.
	 * <p>
	 * @return the visible permission condition
	 */
	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					EntityResultViewer.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					EntityResultViewer.logger.debug(null, e);
				} else {
					EntityResultViewer.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks the enabled permission.
	 * <p>
	 * @return the enabled permission
	 */
	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.attribute != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.attribute.toString(), true);
				}
			}
			try {
				// Check to show
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					EntityResultViewer.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					EntityResultViewer.logger.debug(null, e);
				} else {
					EntityResultViewer.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Gets the table model for the entity result.
	 * <p>
	 * @param res the entity result
	 * @return the table model
	 */
	protected TableModel getTableModel(final EntityResult res) {
		final List vColumns = new Vector();

		final Enumeration enumKeys = res.keys();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			vColumns.add(oKey);
		}
		if (vColumns.isEmpty()) {
			vColumns.add("No data");
		}
		final TableSorter sorter = new TableSorter(
				new ExtendedTableModel(EntityResultUtils.toMap(res), vColumns, vColumns, new Hashtable(), false,
						new Vector()));
		sorter.enableSort(true);
		sorter.enableFiltering(true);
		this.addTableHeaderMouseListener(this.table);
		sorter.setSourceTable(this.table);
		return sorter;
	}

	protected MouseAdapter listMouseListener = null;

	/**
	 * Adds a default mouse listener to the table header and configures the table header rendering.
	 * Controls all the click dependent behaviour of the table header.
	 * @param table the table which header will have the listener
	 */
	public void addTableHeaderMouseListener(final JTable table) {
		final SortTableCellRenderer rend = new SortTableCellRenderer(table);
		// rend.setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
		table.getTableHeader().setDefaultRenderer(rend);
		final TableColumnModel tcModel = table.getColumnModel();
		for (int i = 0; i < tcModel.getColumnCount(); i++) {
			final TableColumn tc = tcModel.getColumn(i);
			tc.setHeaderRenderer(rend);
		}
		table.getTableHeader().repaint();
		table.setColumnSelectionAllowed(false);

		// Column listener
		this.listMouseListener = new SortTableCellRenderer.ListMouseListener();
		final JTableHeader th = table.getTableHeader();
		th.addMouseListener(this.listMouseListener);
	}

	/**
	 * Compares the point with the header of the column specified by the columnIndex. If the point
	 * corresponds to the header, the method returns the related TableColumn.
	 * @param header the table header
	 * @param point a point coming from a mouse event
	 * @param columnIndex the columnIndex to compare return the TableColumn corresponding to the
	 *        columnIndex in case the mouse event was performed into the column header; null otherwise
	 */
	protected TableColumn getResizingColumn(final JTableHeader header, final Point point, final int columnIndex) {
		if (columnIndex == -1) {
			return null;
		}
		final Rectangle r = header.getHeaderRect(columnIndex);
		r.grow(-3, 0);
		if (r.contains(point)) {
			return null;
		}
		final int midPoint = r.x + (r.width / 2);
		int columnIndexLocal;
		if (header.getComponentOrientation().isLeftToRight()) {
			columnIndexLocal = point.x < midPoint ? columnIndex - 1 : columnIndex;
		} else {
			columnIndexLocal = point.x < midPoint ? columnIndex : columnIndex - 1;
		}
		if (columnIndexLocal == -1) {
			return null;
		}
		return header.getColumnModel().getColumn(columnIndexLocal);
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.resources = resources;
		if (this.reportButton != null) {
			this.reportButton
			.setToolTipText(ApplicationManager.getTranslation("report.custom_reports", this.resources));
		}
		if (this.copyMenu != null) {
			this.copyMenu.setText(ApplicationManager.getTranslation(this.copyClipboardKey, this.resources));
		}
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.setLocale(l);
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public Object getValue() {
		return ((TableSorter) this.table.getModel()).getData();
	}

	@Override
	public void setValue(final Object value) {
		if (this.table.getRowHeight() > 20) {
			this.table.setRowHeight(20);
		}
		if (value instanceof EntityResult) {
			final EntityResult res = (EntityResult) value;
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				EntityResultViewer.logger.debug("Error in EntityResult. Message: " + res.getMessage());
				this.deleteData();
			} else if (res.isEmpty()) {
				EntityResultViewer.logger.debug("The EntityResult is empty. There are not results");
				this.deleteData();
			} else {
				final TableModel model = this.getTableModel(res);
				this.table.setModel(model);
				// Set the column identificators
				final JTable t = this.table;
				for (int i = 0; i < t.getColumnCount(); i++) {
					final TableColumn column = t.getColumnModel().getColumn(i);
					column.setIdentifier(((TableSorter) t.getModel()).getColumnIdentifier(i));
					if (column.getIdentifier().equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
						column.setMinWidth(20);
						column.setWidth(30);
						column.setMaxWidth(50);
					}
				}
			}
		} else {
			this.deleteData();
			EntityResultViewer.logger.debug("EntityResult is empty. There are not results");
		}
	}

	@Override
	public void deleteData() {
		this.table.setModel(this.getTableModel(new EntityResultMapImpl()));
		if (this.table.getRowHeight() > 20) {
			this.table.setRowHeight(20);
		}
	}

	@Override
	public boolean isEmpty() {
		if (this.table.getRowCount() <= 0) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public boolean isModified() {
		return false;
	}

	@Override
	public boolean isRequired() {
		return false;
	}

	@Override
	public void setRequired(final boolean required) {
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

	/**
	 * Empty method.
	 * @param modifiable the modified condition
	 */
	public void setModified(final boolean modifiable) {
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
	}

	@Override
	public void setEnabled(final boolean e) {
		super.setEnabled(e);
		if (this.reportButton != null) {
			this.reportButton.setEnabled(e);
		}
	}

	private com.ontimize.report.ReportUtils ru = null;

	/**
	 * Shows the report window.
	 * <p>
	 *
	 * @see ReportUtils#showDefaultReportDialog(Component)
	 */
	public void showCustomReportsWindow() {
		if (this.isEmpty()) {
			return;
		}
		final List v = null;
		final Map hData = (Map) this.getValue();

		final List visibles = new Vector();
		final Enumeration enumKeys = Collections.enumeration(hData.keySet());
		while (enumKeys.hasMoreElements()) {
			visibles.add(enumKeys.nextElement());
		}

		if (this.ru == null) {
			this.ru = new com.ontimize.report.ReportUtils(EntityResultUtils.createTableModel(hData, visibles), null,
					this.resources, v, "");
		} else {
			this.ru.setModel(EntityResultUtils.createTableModel(hData, visibles));
		}
		this.ru.setResourceBundle(this.resources);
		this.ru.showDefaultReportDialog(this);
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
