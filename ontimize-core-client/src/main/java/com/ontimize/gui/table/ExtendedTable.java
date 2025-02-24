package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.security.ClientSecurityManager;

/**
 * @deprecated
 */
@Deprecated
public class ExtendedTable extends JPanel
implements DataComponent, ReferenceComponent, OpenDialog, AccessForm, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ExtendedTable.class);

	public static final String COLUMN_ = "M_COLUMNA_";

	public static final String REQUIRED = "_REQUERIDA";

	public static final String INSERTION_NO_CONFIRM = "M_INSERCION_NO_CONFIRMADA";

	public static final String INSERTION_PERFORM = "M_INSERCION_REALIZADA";

	public static final String MODIFICATION_CONFIRM = "M_CONFIRMAR_MODIFICACIONES";

	public static final String M_CONFIRM_UPDATE = "M_CONFIRMA_ACTUALIZAR?";

	public static final String M_CONFIRM_INSERT = "M_CONFIRMA_INSERTAR?";

	protected String entity = null;

	protected String attribute = null;

	protected List columns = new Vector();

	protected List keys = new Vector();

	protected List parentKeys = new Vector();

	protected List visibleColumns = new Vector();

	protected List editableColumns = new Vector();

	protected List requiredColumns = new Vector();

	protected List currencyColumns = new Vector();

	protected Map calculatedColumns = new Hashtable();

	protected MJTable table = null;

	protected ResourceBundle resources = null;

	protected EntityReferenceLocator locator = null;

	protected Frame parentFrame = null;

	protected JLabel status = new JLabel();

	protected String formName = null;

	protected int rowPreferredSize = 10;

	protected boolean editable = true;

	protected FormPermission visiblePermission = null;

	protected FormPermission enabledPermission = null;

	protected MouseAdapter openDetailFormListener = new MouseAdapter() {

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2) {
				ExtendedTable.this.showDetailForm();
			}
		}
	};

	protected int[] settedColumnWidth = new int[0];

	protected Form parentForm = null;

	protected Map editors = new Hashtable();

	protected JPanel buttonPanel = new JPanel(new GridBagLayout());

	protected JCheckBox confirmChangeCheck = new JCheckBox(ExtendedTable.MODIFICATION_CONFIRM);

	protected class MJTable extends JTable {

		protected boolean inserting = false;

		protected int editRow = -1;

		protected int editColumn = -1;

		protected boolean insertedScroll = false;

		protected JScrollPane scroll = null;

		public MJTable(final TableModel m) {
			super(m);
		}

		@Override
		public boolean editCellAt(final int row, final int column, final EventObject o) {
			final boolean edited = super.editCellAt(row, column, o);
			if (edited) {
				// Install the editor and set the component size.
				// If component is a JTextArea then set an scroll
				final Component comp = this.getEditorComponent();
				if (comp instanceof JTextArea) {
					final Dimension d = comp.getPreferredSize();
					final int rowHeight = this.getRowHeight(row);
					if (d.height > rowHeight) {
						this.setRowHeight(row, d.height);
					}
					this.scroll = new JScrollPane(comp);
					this.scroll.setBounds(this.getCellRect(row, column, false));
					this.remove(comp);
					this.add(this.scroll);
					((JComponent) comp).setNextFocusableComponent(this);
					this.scroll.setNextFocusableComponent(this);
					comp.validate();
					this.scroll.validate();
					this.insertedScroll = true;
				}
				final TableCellEditor c = this.getCellEditor();
				if (c instanceof JComponent) {
					((JComponent) c).requestFocus();
				}
				// this.repaint(this.getCellRect(row,column,true));
				if (row == (this.getRowCount() - 1)) {
					this.inserting = true;
					ExtendedTable.this.setStateText(ExtendedTable.INSERTION_NO_CONFIRM, 0, Color.red);
				} else {
					this.inserting = false;
				}
			}
			return edited;
		}

		public void performInsertion() {
			final boolean ok = ExtendedTable.this.confirmRequiredInsertCell(this.editRow);
			if (ok) {
				// If entity is not null then insert in the entity
				try {
					final EntityResult res = ExtendedTable.this.insertEntity(this.editRow);
					if (res.getCode() == EntityResult.OPERATION_WRONG) {
						ExtendedTable.this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
						ExtendedTable.logger.debug(res.getMessage());
					} else {
						// If no error happend then add a new row
						this.inserting = false;
						ExtendedTable.this.refresh();
						ExtendedTable.this.setStateText(ExtendedTable.INSERTION_PERFORM, 1000);
						// Start the next row edition
						this.setRowSelectionInterval(this.getRowCount() - 1, this.getRowCount() - 1);
						this.setCellFocus(this.getRowCount() - 1, 1);
					}
				} catch (final Exception ex) {
					ExtendedTable.logger.trace(null, ex);
					ExtendedTable.this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
				}
			}
		}

		public void setCellFocus(final int row, final int column) {
			this.selectionModel.setAnchorSelectionIndex(row);
			this.columnModel.getSelectionModel().setAnchorSelectionIndex(column);
		}

		@Override
		protected boolean processKeyBinding(final KeyStroke ks, final KeyEvent e, final int condition, final boolean pressed) {
			final int editedRow = this.editingRow;
			final int editedColumn = this.editingColumn;
			if ((this.inserting) && (condition == JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT) && this.hasFocus()) {
				if ((e != null) && (e.getID() == KeyEvent.KEY_PRESSED)) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						// Sort the insertion
						this.performInsertion();
						return true;
					}
					if ((e.getKeyCode() == KeyEvent.VK_TAB) && !e.isShiftDown()) {
						// Next cell edition
						for (int i = editedColumn + 1; i < this.getColumnCount(); i++) {
							if (ExtendedTable.this.editableColumns.contains(this.getColumnName(i))) {
								this.selectionModel.setAnchorSelectionIndex(editedRow);
								this.columnModel.getSelectionModel().setAnchorSelectionIndex(i);
								this.editCellAt(editedRow, i, new ActionEvent(this, 0, "EditCell"));
								return true;
							}
						}
					} else if ((e.getKeyCode() == KeyEvent.VK_TAB) && e.isShiftDown()) {
						// Next cell edition
						for (int i = editedColumn - 1; i > 0; i--) {
							if (ExtendedTable.this.editableColumns.contains(this.getColumnName(i))) {
								this.selectionModel.setAnchorSelectionIndex(editedRow);
								this.columnModel.getSelectionModel().setAnchorSelectionIndex(i);
								this.editCellAt(editedRow, i, new ActionEvent(this, 0, "EditCell"));
								return true;
							}
						}
					}
				}
			}
			final boolean procesado = super.processKeyBinding(ks, e, condition, pressed);
			return procesado;
		}

		@Override
		public TableCellEditor getCellEditor(final int row, final int col) {
			final int column = this.convertColumnIndexToModel(col);
			final TableModel m = this.getModel();
			final String sColumnName = m.getColumnName(column);
			if (ExtendedTable.this.editableColumns.contains(sColumnName)) {
				if (!ExtendedTable.this.editable) {
					return null;
				}
				return (TableCellEditor) ExtendedTable.this.editors.get(sColumnName);
			} else {
				return null;
			}
		}

		@Override
		public void editingStopped(final ChangeEvent e) {
			this.editRow = this.editingRow;
			this.editColumn = this.editingColumn;
			// If the editing column is the last editable column and is an
			// insertion then validate the row to insert
			// If is an insertion but the column is not the last editable column
			// then continue the edition
			// If it is not an insertion an entity is not null then update
			if (!this.inserting) {
				if (ExtendedTable.this.confirmChanges()) {
					if (!ExtendedTable.this.parentForm.question(ExtendedTable.M_CONFIRM_UPDATE)) {
						if (this.getCellEditor() != null) {
							this.getCellEditor().cancelCellEditing();
						}
						return;
					}
				}
				super.editingStopped(e);
				try {
					final EntityResult res = ExtendedTable.this.updateCell(this.editRow, this.editColumn);
					if (res.getCode() == EntityResult.OPERATION_WRONG) {
						ExtendedTable.this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
					}
				} catch (final Exception ex) {
					ExtendedTable.logger.trace(null, ex);
					ExtendedTable.this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
				}
				ExtendedTable.this.refresh();
				this.setColumnSelectionInterval(this.editColumn, this.editColumn);
			} else {
				super.editingStopped(e);
				// Update
				boolean lastEditableColumn = true;
				// If the editor lost focus and edition stopped then checks if
				// the
				// selected column is before
				for (int i = this.editColumn + 1; i < this.getColumnCount(); i++) {
					if (ExtendedTable.this.editableColumns.contains(this.getColumnName(i))) {
						lastEditableColumn = false;
						break;
					}
				}
				if (lastEditableColumn) {
					if (ExtendedTable.this.confirmChanges()) {
						if (!ExtendedTable.this.parentForm.question(ExtendedTable.M_CONFIRM_INSERT)) {
							return;
						}
					}
					this.performInsertion();
				}
			}
		}

		@Override
		public void editingCanceled(final ChangeEvent e) {
			super.editingCanceled(e);
		}

		@Override
		public Component prepareEditor(final TableCellEditor editor, final int row, final int column) {
			final Component c = super.prepareEditor(editor, row, column);
			this.editRow = row;
			this.editColumn = column;
			return c;
		}

		@Override
		public void removeEditor() {
			if (this.insertedScroll) {
				this.insertedScroll = false;
				// Remove the scroll.
				this.remove(this.scroll);
				// resize the row
				final int iEditedRow = this.getEditingRow();
				if (iEditedRow >= 0) {
					this.setRowHeight(iEditedRow, 16);
				}
			}
			super.removeEditor();
		}

	};

	public ExtendedTable(final Map parameters) throws Exception {
		this.setLayout(new BorderLayout());
		this.add(this.status, BorderLayout.SOUTH);
		this.confirmChangeCheck.setSelected(true);
		this.buttonPanel.add(this.confirmChangeCheck,
				new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
						new Insets(3, 2, 2, 2), 0, 0));
		this.add(this.buttonPanel, BorderLayout.NORTH);
		this.init(parameters);
		// Validate the input parameters
		if ((this.entity == null) && (this.attribute == null)) {
			throw new Exception(this.getClass().toString() + ". Debe especificar 'entity' o 'attr'");
		}
		if (this.columns.isEmpty()) {
			throw new Exception(this.getClass().toString() + ". Debe especificar 'cols'");
		}
		if (this.keys.isEmpty()) {
			throw new Exception(this.getClass().toString() + ". Debe especificar 'keys'");
		}

		// Columns in the model include the keys.
		for (int i = 0; i < this.keys.size(); i++) {
			if (!this.columns.contains(this.keys.get(i))) {
				this.columns.add(this.keys.get(i));
			}
		}
		// If editableColumns is empty then table is not editable
		if (this.editableColumns.isEmpty()) {
			ExtendedTable.logger.debug("ExtendedTable: " + this.entity + " no editable");
			this.editable = false;
		} else {
			ExtendedTable.logger
			.debug("ExtendedTable: " + this.entity + " . Editable columns: " + this.editableColumns);
		}
		final ExtendedTableModel m = new ExtendedTableModel(new Hashtable(), this.columns, this.columns,
				this.calculatedColumns, this.editable);
		this.table = new MJTable(m);
		this.add(new JScrollPane(this.table));
		this.table.getTableHeader().setReorderingAllowed(false);
		this.setEditableColumns();
		this.setRenderers();
		this.setVisibleColumns();
		final FontMetrics fontMetrics = this.table.getFontMetrics(this.table.getFont());
		this.table.setPreferredScrollableViewportSize(
				new Dimension(this.table.getPreferredScrollableViewportSize().width, 10 * fontMetrics.getHeight()));
		this.packTable();
		if (!this.editable) {
			this.status.setVisible(false);
		}
	}

	@Override
	public void init(final Map parameters) {
		// Entity parameter;

		final Object entity = parameters.get("entity");
		if (entity != null) {
			this.entity = entity.toString();
		}

		if (entity == null) {
			final Object attr = parameters.get("attr");
			this.attribute = attr.toString();
		}

		final Object editab = parameters.get("editable");
		if (editab != null) {
			if (editab.toString().equalsIgnoreCase("no")) {
				this.editable = false;
			}
		}

		final Object form = parameters.get("form");
		if (form == null) {
		} else {
			this.formName = form.toString();
			this.installDetailFormListener();
		}

		final Object cols = parameters.get("cols");
		if (cols != null) {
			final StringTokenizer st = new StringTokenizer(cols.toString(), ";");
			while (st.hasMoreTokens()) {
				this.columns.add(st.nextToken());
			}
		} else {
			ExtendedTable.logger.debug(this.getClass().toString() + ". Parameter 'cols' not found");
		}
		final Object keys = parameters.get("keys");
		if (keys != null) {
			final StringTokenizer st = new StringTokenizer(keys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.keys.add(st.nextToken());
			}
		} else {
			ExtendedTable.logger.debug(this.getClass().toString() + ". Parameter 'keys' not found");
		}
		final Object parentkeys = parameters.get("parentkeys");
		if (parentkeys != null) {
			final StringTokenizer st = new StringTokenizer(parentkeys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.parentKeys.add(st.nextToken());
			}
		} else {
			ExtendedTable.logger.debug(this.getClass().toString() + ". Parameter 'parentkeys' not found");
		}
		final Object visiblecols = parameters.get("visiblecols");
		if (visiblecols != null) {
			final StringTokenizer st = new StringTokenizer(visiblecols.toString(), ";");
			while (st.hasMoreTokens()) {
				this.visibleColumns.add(st.nextToken());
			}
		}

		final Object requiredcols = parameters.get("requiredcols");
		if (requiredcols != null) {
			final StringTokenizer st = new StringTokenizer(requiredcols.toString(), ";");
			while (st.hasMoreTokens()) {
				this.requiredColumns.add(st.nextToken());
			}
		}

		final Object editablecols = parameters.get("editablecols");
		if (editablecols != null) {
			final StringTokenizer st = new StringTokenizer(editablecols.toString(), ";");
			while (st.hasMoreTokens()) {
				this.editableColumns.add(st.nextToken());
			}
		}

		final Object calculedcolumns = parameters.get("calculedcols");
		if (calculedcolumns != null) {
			// Tenemos que construir los vectores de columnas y las expresiones,
			final StringTokenizer st = new StringTokenizer(calculedcolumns.toString(), ";");
			boolean impar = true;
			String sColumnName = null;
			String expresion = null;
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				// Los tokens impares son los nombres de columnas
				if (impar) {
					// Column name
					sColumnName = token;
					impar = false;
				} else {
					expresion = token;
					if (this.calculatedColumns == null) {
						this.calculatedColumns = new Hashtable();
					}
					this.calculatedColumns.put(sColumnName, expresion);
					impar = true;
				}
			}
		}

		final Object currency = parameters.get("currency");
		if (currency == null) {
		} else {
			final StringTokenizer st = new StringTokenizer(currency.toString(), ";");
			while (st.hasMoreTokens()) {
				final String nom = st.nextToken();
				if (this.columns.contains(nom) || this.calculatedColumns.containsKey(nom)) {
					this.currencyColumns.add(nom);
				}
			}
		}

		final Object rows = parameters.get("rows");
		if (rows != null) {
			try {
				this.rowPreferredSize = Integer.parseInt(rows.toString());
			} catch (final Exception e) {
				ExtendedTable.logger.trace(null, e);
				ExtendedTable.logger
				.error(this.getClass().toString() + ": Error in parameter 'rows': " + e.getMessage(), e);
			}
		}
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
	public boolean isEmpty() {
		final ExtendedTableModel m = (ExtendedTableModel) this.table.getModel();
		if ((m.getData() == null) || m.getData().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public void setModifiable(final boolean modif) {
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public void deleteData() {
		((ExtendedTableModel) this.table.getModel()).setData(new Hashtable());
		this.initColumnWidth();
	}

	@Override
	public void setValue(final Object value) {
		if (value instanceof Map) {
			((ExtendedTableModel) this.table.getModel()).setData((Map) value);
			this.initColumnWidth();
		} else {
			this.deleteData();
		}
	}

	@Override
	public Object getValue() {
		final TableModel model = this.table.getModel();
		return ((ExtendedTableModel) model).getData();
	}

	@Override
	public String getLabelComponentText() {
		try {
			if (this.resources != null) {
				return this.resources.getString(this.entity);
			} else {
				return this.entity;
			}
		} catch (final Exception e) {
			ExtendedTable.logger.trace(null, e);
			return this.entity;
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		for (int i = 0; i < this.columns.size(); i++) {
			v.add(this.columns.get(i).toString());
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.resources = res;
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public Object getAttribute() {
		if (this.entity == null) {
			return this.attribute;
		} else {
			final Map h = new Hashtable();
			h.put(this.entity, this.columns);
			return h;
		}
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
		this.setReferencesLocatorEditors(locator);
	}

	public void setReferencesLocatorEditors(final EntityReferenceLocator locator) {
		final Enumeration enumKeys = Collections.enumeration(this.editors.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object editor = this.editors.get(enumKeys.nextElement());
			if (editor instanceof ReferenceComponent) {
				((ReferenceComponent) editor).setReferenceLocator(locator);
			}
		}
	}

	@Override
	public void setParentFrame(final Frame f) {
		this.parentFrame = f;
		this.setParentFrameEditors(f);
	}

	public void setParentFrameEditors(final Frame f) {
		final Enumeration enumKeys = Collections.enumeration(this.editors.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object editor = this.editors.get(enumKeys.nextElement());
			if (editor instanceof OpenDialog) {
				((OpenDialog) editor).setParentFrame(f);
			}
		}
	}

	protected void setEditableColumns() {
		if (this.editable) {
			final ExtendedTableModel model = (ExtendedTableModel) this.table.getModel();
			for (int i = 0; i < this.editableColumns.size(); i++) {
				model.setEditableColumn(this.editableColumns.get(i));
			}
		}
	}

	public int[] initColumnWidth() {
		// For the row number column
		final TableColumn rowNumbersColumns = this.table.getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
		rowNumbersColumns.setMaxWidth(15);
		rowNumbersColumns.setWidth(15);
		rowNumbersColumns.setPreferredWidth(15);
		rowNumbersColumns.setMinWidth(15);
		rowNumbersColumns.setResizable(false);
		try {// Solo para jre 1.3
			final TableCellRenderer rendererRowNumbers = this.table
					.getDefaultRenderer(this.table.getColumnClass(rowNumbersColumns.getModelIndex()));
			final Component rendererRowNumbersComponent = rendererRowNumbers.getTableCellRendererComponent(this.table,
					new Integer(this.table.getRowCount()), false, false, 0, 0);
			// Set the column width
			final Dimension d = rendererRowNumbersComponent.getPreferredSize();
			rowNumbersColumns.setMaxWidth(d.width + 5);
			rowNumbersColumns.setWidth(d.width + 5);
			rowNumbersColumns.setPreferredWidth(d.width + 5);
			rowNumbersColumns.setMinWidth(d.width + 5);
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ExtendedTable.logger.error(null, e);
			} else {
				ExtendedTable.logger.trace(null, e);
			}
		}
		// Con los datos de la tabla, tratamos de que quepan las columnas
		final Object oTableValues = this.getValue();
		if (oTableValues == null) {
			final int[] iWidths = new int[this.table.getColumnCount() - 1];
			for (int i = 1; i < this.table.getColumnCount(); i++) {
				final String sName = this.table.getColumnName(i);
				final TableColumn columna = this.table.getColumn(sName);
				if (!this.isColumnVisible(sName)) {
					columna.setMinWidth(0);
					columna.setMaxWidth(1);
					columna.setWidth(0);
					iWidths[i - 1] = 0;
					continue;
				}
				// Si no hay datos, entonces, inicializamos el ancho de las
				// columnas
				// según las cabeceras.
				try { // Debido a que el JRE 1.2 no tiene la funcion:
					// TableCellRenderer.getDefaultRenderer()
					// Componente de la cabecera de las columnas
					final JTableHeader cabecera = this.table.getTableHeader();
					final TableCellRenderer rendererCabecera = cabecera.getDefaultRenderer();
					final Object oHeaderValue = columna.getHeaderValue();
					final Component headerRenderComponent = rendererCabecera.getTableCellRendererComponent(null, oHeaderValue,
							false, false, 0, 0);
					final int anchoPreferidoCabecera = headerRenderComponent.getPreferredSize().width;
					iWidths[i - 1] = anchoPreferidoCabecera;
				} catch (final Exception e) {
					ExtendedTable.logger
					.error("Excepcion Inicializando ancho columnas Tabla. Se requiere JRE 1.3 o mayor", e);
					iWidths[i - 1] = columna.getPreferredWidth();
				}
			}
			return iWidths;
		}
		final Map hData = (Map) oTableValues;
		final Enumeration enumKeys = Collections.enumeration(hData.keySet());
		// Tambien hay que tener en cuenta los títulos de las columnas
		// (cabecera)
		// Variable para contener los anchos
		final int[] anchos = new int[hData.size()];
		int j = 0;
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			// Si la columna es visible
			if (!this.isColumnVisible(oKey.toString())) {
				continue;
			}
			final List vValues = (List) hData.get(oKey);
			final TableColumn columna = this.table.getColumn(oKey);
			if (this.isColumnWidthFixed(oKey.toString())) {
				continue;
			}
			final TableCellRenderer renderer = this.table
					.getDefaultRenderer(this.table.getColumnClass(columna.getModelIndex()));
			int anchoPreferido = 0;
			for (int i = 0; i < Math.min(10, vValues.size()); i++) {
				final Object oValue = vValues.get(i);
				if (renderer == null) {
					continue;
				}
				final Component componenteRender = renderer.getTableCellRendererComponent(null, oValue, false, false, 0, 0);
				final int anchoAux = componenteRender.getPreferredSize().width;
				anchoPreferido = Math.max(anchoPreferido, anchoAux);
			}
			try { // Debido a que el JRE 1.2 no tiene la funcion:
				// TableCellRenderer.getDefaultRenderer()
				// Componente de la cabecera de las columnas
				final JTableHeader cabecera = this.table.getTableHeader();
				final TableCellRenderer rendererCabecera = cabecera.getDefaultRenderer();
				// TableCellRenderer rendererCabecera =
				// columna.getHeaderRenderer();
				final Object oHeaderValue = columna.getHeaderValue();
				final Component componenteRenderCabecera = rendererCabecera.getTableCellRendererComponent(null, oHeaderValue,
						false, false, 0, 0);
				final int anchoPreferidoCabecera = componenteRenderCabecera.getPreferredSize().width;
				anchoPreferido = Math.max(anchoPreferido, anchoPreferidoCabecera);
			} catch (final Exception e) {
				ExtendedTable.logger.error("Excepcion Inicializando ancho columnas Tabla. Se requiere JRE 1.3 o mayor",
						e);
			}
			// Establecemos el ancho de la columna
			// columna.setMaxWidth(this.tabla.getWidth());
			columna.setWidth(anchoPreferido);
			columna.setPreferredWidth(anchoPreferido + 5);
			anchos[j] = anchoPreferido;
			j++;
		}
		return anchos;
	}

	protected boolean isColumnVisible(final String col) {
		if (this.columns.contains(col) || this.calculatedColumns.containsKey(col)) {
			if ((this.visibleColumns != null) && !this.visibleColumns.isEmpty()) {
				if (this.visibleColumns.contains(col)) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void packTable() {
		final int[] anchos = this.initColumnWidth();
		int total = 0;
		for (int i = 0; i < anchos.length; i++) {
			total = total + anchos[i];
		}
		this.setPreferredSize(new Dimension(total, (int) this.table.getPreferredScrollableViewportSize().getHeight()));
	}

	protected void setVisibleColumns() {
		if ((this.visibleColumns != null) && !this.visibleColumns.isEmpty()) {
			// Desde 1 porque la primera columna es de números de fila
			for (int i = 1; i < this.table.getColumnCount(); i++) {
				final String col = this.table.getColumnName(i);
				if (!this.visibleColumns.contains(col)) {
					final TableColumn tc = this.table.getColumn(col);
					tc.setMinWidth(0);
					tc.setWidth(0);
					tc.setMaxWidth(0);
				}
			}
		}
	}

	protected void setRenderers() {
		this.table.setDefaultRenderer(Timestamp.class, new DateCellRenderer());
		this.table.setDefaultRenderer(RowHeadCellRenderer.class, new RowHeadCellRenderer(this.table));
		this.table.setDefaultRenderer(java.sql.Date.class, new DateCellRenderer());
		this.table.setDefaultRenderer(java.util.Date.class, new DateCellRenderer());
		this.table.setDefaultRenderer(Integer.class, new RealCellRenderer());
		this.table.setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
		this.table.setDefaultRenderer(Float.class, new RealCellRenderer());
		this.table.setDefaultRenderer(Double.class, new RealCellRenderer());
		this.table.setDefaultRenderer(Object.class, new ObjectCellRenderer());
		this.table.setDefaultRenderer(String.class, new ObjectCellRenderer());
		this.table.setDefaultRenderer(Number.class, new RealCellRenderer());
		for (int i = 0; i < this.currencyColumns.size(); i++) {
			final TableColumn col = this.table.getColumn(this.currencyColumns.get(i));
			if (col != null) {
				// Para las columnas de moneda ponemos un CellRendererMoneda
				col.setCellRenderer(new CurrencyCellRenderer());
			}
		}
	}

	public void setEditable(final boolean editable) {
		this.editable = true;
	}

	public void setColumnEditor(final String col, final TableCellEditor editor) {
		if (this.editable) {
			final TableColumn tc = this.table.getColumn(col);
			tc.setCellEditor(editor);
			this.editors.put(col, editor);
			this.setParentFrameEditors(this.parentFrame);
			this.setReferencesLocatorEditors(this.locator);
			this.setParentFormEditors(this.parentForm);
		} else {
			ExtendedTable.logger.debug(this.getClass().toString() + ": No editable");
			final TableColumn tc = this.table.getColumn(col);
			tc.setCellEditor(null);
		}
	}

	protected boolean confirmRequiredInsertCell(final int fila) {
		// Comprobamos que tengan valores
		for (int i = 0; i < this.requiredColumns.size(); i++) {
			final Object col = this.requiredColumns.get(i);
			final TableColumn tc = this.table.getColumn(col);
			final int colModel = tc.getModelIndex();
			final TableModel m = this.table.getModel();
			final Object oValue = m.getValueAt(fila, colModel);
			if (oValue == null) {
				this.parentForm.message(ExtendedTable.COLUMN_ + col.toString() + ExtendedTable.REQUIRED,
						Form.WARNING_MESSAGE);
				this.table.setCellFocus(fila, this.table.convertColumnIndexToView(colModel));
				this.table.editCellAt(fila, this.table.convertColumnIndexToView(colModel),
						new ActionEvent(this, 0, "EditCell"));
				return false;
			}
		}
		return true;
	}

	/*
	 * Insert in the entity
	 */
	protected EntityResult insertEntity(final int row) throws Exception {
		if (this.entity != null) {
			// Attributes to insert are all the columns
			final Map av = new Hashtable();
			final TableModel m = this.table.getModel();
			for (int i = 1; i < m.getColumnCount(); i++) {
				final Object col = m.getColumnName(i);
				final Object oValue = m.getValueAt(row, i);
				if (oValue != null) {
					av.put(col, oValue);
				}
			}
			final Entity ent = this.locator.getEntityReference(this.entity);
			return ent.insert(av, this.locator.getSessionId());
		} else {
			return new EntityResultMapImpl();
		}
	}

	protected EntityResult updateCell(final int fila, final int columna) throws Exception {
		if (this.entity != null) {
			// Update data
			final Map av = new Hashtable();
			final TableModel m = this.table.getModel();
			final Object col = m.getColumnName(this.table.convertColumnIndexToModel(columna));
			final Object oValue = m.getValueAt(fila, this.table.convertColumnIndexToModel(columna));
			if (oValue != null) {
				av.put(col, oValue);
			}
			final Map hkv = new Hashtable();
			// Keys and parent keys
			for (int i = 0; i < this.keys.size(); i++) {
				final Object oAttr = this.keys.get(i);
				final Object oKeyValue = m.getValueAt(fila, this.table.getColumn(oAttr).getModelIndex());
				if (oKeyValue != null) {
					hkv.put(oAttr, oKeyValue);
				}
			}
			for (int i = 0; i < this.parentKeys.size(); i++) {
				final Object oAttr = this.parentKeys.get(i);
				final Object parentKeyValue = this.parentForm.getDataFieldValueFromFormCache(oAttr.toString());
				if (parentKeyValue != null) {
					hkv.put(oAttr, parentKeyValue);
				}
			}
			final Entity ent = this.locator.getEntityReference(this.entity);
			return ent.update(av, hkv, this.locator.getSessionId());
		} else {
			return new EntityResultMapImpl();
		}
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
		this.setParentFormEditors(f);
	}

	protected void setParentFormEditors(final Form f) {
		final Enumeration enumKeys = Collections.enumeration(this.editors.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object editor = this.editors.get(enumKeys.nextElement());
			if (editor instanceof AccessForm) {
				((AccessForm) editor).setParentForm(f);
			}
		}
	}

	protected boolean confirmChanges() {
		return this.confirmChangeCheck.isSelected();
	}

	public void refresh() {
		this.status.setText("");
		try {
			final EntityReferenceLocator referenceLocator = this.parentForm.getFormManager().getReferenceLocator();
			final Entity ent = referenceLocator.getEntityReference(this.entity);
			final Map kv = new Hashtable();
			for (int i = 0; i < this.parentKeys.size(); i++) {
				final Object v = this.parentForm.getDataFieldValueFromFormCache(this.parentKeys.get(i).toString());
				if (v != null) {
					kv.put(this.parentKeys.get(i), v);
				}
			}
			final EntityResult res = ent.query(kv, this.columns, referenceLocator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				ExtendedTable.logger.debug(res.getMessage());
			} else {
				this.setValue(res);
			}
		} catch (final Exception e) {
			ExtendedTable.logger.error(null, e);
		}
	}

	public void setStateText(final String texto, final int time) {
		this.setStateText(texto, time, Color.black);
	}

	public void setStateText(final String texto, final int time, final Color c) {
		this.status.setForeground(c);
		try {
			if (this.resources != null) {
				this.status.setText(this.resources.getString(texto));
			}
			this.status.setText(texto);
		} catch (final Exception e) {
			ExtendedTable.logger.trace(null, e);
			this.status.setText(texto);
		}
		if (time != 0) {
			final Thread t = new Thread() {

				@Override
				public void run() {
					try {
						Thread.sleep(time);
					} catch (final Exception e) {
						ExtendedTable.logger.trace(null, e);
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							ExtendedTable.this.status.setText("");
						}
					});
				}
			};
			t.start();
		}
	}

	/**
	 * Este metodo esta sobrecargado por conveniencia. Es una forma facil de establecer los editores de
	 * la tabla mediante XML, ya que el constructor de formularios utiliza este metodo.
	 */
	@Override
	public void add(final Component c, final Object constraints) {
		if ((c instanceof TableCellEditor) && (constraints instanceof String)) {
			this.setColumnEditor((String) constraints, (TableCellEditor) c);
		} else {
			super.add(c, constraints);
		}
	}

	/**
	 * Establece el ancho de una columna. Para anularlo el valor de pixels debe ser 0. En este caso, la
	 * tabla calcula el ancho adecuado de cada columna.
	 */
	public void columnWidthSet(final String columna, final int pixels) {
		final TableColumn tc = this.table.getColumn(columna);
		final int modelIndex = tc.getModelIndex();
		final int viewIndex = this.table.convertColumnIndexToView(modelIndex);
		if (this.settedColumnWidth.length == 0) {
			this.settedColumnWidth = new int[this.table.getColumnCount()];
			for (int i = 0; i < this.settedColumnWidth.length; i++) {
				this.settedColumnWidth[i] = 0;
			}
		}
		this.settedColumnWidth[viewIndex] = pixels;
		if (pixels != 0) {
			tc.setPreferredWidth(pixels);
			tc.setWidth(pixels);
			tc.setResizable(false);
		} else {
			tc.setResizable(true);
			this.initColumnWidth();
		}
	}

	protected boolean isColumnWidthFixed(final String columna) {
		final TableColumn tc = this.table.getColumn(columna);
		if (tc == null) {
			return false;
		}
		final int iModelIndex = tc.getModelIndex();
		final int iViewIndex = this.table.convertColumnIndexToView(iModelIndex);
		if (this.settedColumnWidth.length <= iViewIndex) {
			return false;
		} else {
			if (this.settedColumnWidth[iViewIndex] != 0) {
				return true;
			} else {
				return false;
			}
		}
	}

	protected void installDetailFormListener() {
		this.table.addMouseListener(this.openDetailFormListener);
	}

	protected void removeDetailFormListener() {
		this.table.removeMouseListener(this.openDetailFormListener);
	}

	public JTable getJTable() {
		return this.table;
	}

	public void addMouseListenerToTable(final MouseListener m) {
		this.table.addMouseListener(m);
	}

	public void addListSelectionListenerToTable(final ListSelectionListener l) {
		this.table.getSelectionModel().addListSelectionListener(l);
	}

	public void removeMouseListenerFromTable(final MouseListener m) {
		this.table.removeMouseListener(m);
	}

	public void removeListSelectionListenerFromTable(final ListSelectionListener l) {
		this.table.getSelectionModel().removeListSelectionListener(l);
	}

	public void showDetailForm() {
	}

	protected boolean checkVisiblePermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.visiblePermission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					this.visiblePermission = new FormPermission(this.parentForm.getArchiveName(), "visible",
							this.entity, true);
				}
			}
			try {
				// Checkeamos para mostrar
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ExtendedTable.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					ExtendedTable.logger.debug(null, e);
				} else {
					ExtendedTable.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	protected boolean checkEnabledPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.enabledPermission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					this.enabledPermission = new FormPermission(this.parentForm.getArchiveName(), "enabled",
							this.entity, true);
				}
			}
			try {
				// Checkeamos para mostrar
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					ExtendedTable.logger.error(null, e);
				} else if (ApplicationManager.DEBUG_SECURITY) {
					ExtendedTable.logger.debug(null, e);
				} else {
					ExtendedTable.logger.trace(null, e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	public void setEnabled(final boolean activ) {
		if (activ) {
			final boolean permiso = this.checkEnabledPermission();
			if (!permiso) {
				return;
			}
		}
		super.setEnabled(activ);
	}

	@Override
	public void setVisible(final boolean vis) {
		if (vis) {
			final boolean permiso = this.checkVisiblePermission();
			if (!permiso) {
				return;
			}
		}
		super.setVisible(vis);
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

	protected boolean restricted = false;

	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
