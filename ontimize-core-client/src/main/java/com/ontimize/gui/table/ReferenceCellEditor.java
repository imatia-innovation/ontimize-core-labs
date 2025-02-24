package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.ParseUtils;

public class ReferenceCellEditor extends StringCellEditor
implements ReferenceComponent, AccessForm, ISetReferenceValues {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceCellEditor.class);

	private EditorComp editorAux = null;

	private String entityName = null;

	private String queryField = null;

	protected final String ASTERISK = "*";

	protected boolean addAsterisk = false;

	private static String keyOK = "application.accept";

	protected JButton buttonOK = null;

	protected EntityReferenceLocator locator = null;

	protected Form parentForm = null;

	private static String NO_DATA_KEY_MESSAGE = "value_must_be_entered_message";

	private static final int MIN_WIDTH_TABLE_WINDOW = 300;

	protected Table table = null;

	protected JDialog tableWindow = null;

	protected ResourceBundle resources = null;

	protected List columns = new Vector(2, 2);

	protected Map valuesKeys = null;

	protected List colsSet = null;

	protected Map colsSetTypes;

	protected boolean codeNumber = false;

	/**
	 * The code number class. By default, it is referred to integer code.
	 */
	protected int codeNumberClass = ParseTools.INTEGER_;

	protected String columnToSet = null;

	/**
	 * Configuration parameters.
	 * <p>
	 * @param parameters the Map with parameters
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
	 *        <td>addasterisk</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates when it is needed to add asterisk characters at the beginning and at the end
	 *        to include in the query.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>search</td>
	 *        <td></td>
	 *        <td>the editing column</td>
	 *        <td>no</td>
	 *        <td>Column to search by.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>set</td>
	 *        <td></td>
	 *        <td>table keys</td>
	 *        <td>no</td>
	 *        <td>Columns to set in the edited table.</td>
	 *        </tr>
	 *        </Table>
	 */
	public ReferenceCellEditor(final Map parameters) {
		super(parameters);
		final Object entity = parameters.get("entity");
		if (entity == null) {
			throw new IllegalArgumentException("Parameter 'entity' is required");
		}
		this.entityName = entity.toString();

		final Object useasterisk = parameters.get("addasterisk");
		if (useasterisk != null) {
			if (useasterisk.equals("yes")) {
				this.addAsterisk = true;
			}
		}

		final Object search = parameters.get("search");
		if (search == null) {
			this.queryField = this.column;
		} else {
			this.queryField = search.toString();
		}
		this.editorAux = new EditorComp(this.field.getDataField());
		final Object keys = parameters.get("keys");
		if (keys == null) {
			throw new IllegalArgumentException("Parameter 'keys' is required");
		}

		final boolean integerValue = ParseUtils.getBoolean((String) parameters.get("codinteger"), false);
		final Object codnumberclass = parameters.get("codnumberclass");
		if (codnumberclass != null) {
			this.codeNumber = true;
			this.codeNumberClass = ParseUtils.getTypeForName(codnumberclass.toString(), ParseTools.INTEGER_);
		} else if (integerValue) {
			this.codeNumber = true;
			this.codeNumberClass = ParseTools.INTEGER_;
		}

		// We allow 'set' and 'onsetvalueset' parameter to configure the other
		// columns to set
		final Object set = parameters.containsKey("set") ? parameters.get("set") : parameters.get("onsetvalueset");
		if (set != null) {
			this.colsSet = ApplicationManager.getTokensAt(set.toString(), ";");
			final Object setTypes = parameters.get("onsetsqltypes");
			if (setTypes != null) {
				this.colsSetTypes = ApplicationManager.getTokensAt((String) setTypes, ";", ":");
			}
		}

		this.columnToSet = ParseUtils.getString((String) parameters.get("columnToSet"), this.column);

		parameters.put("numrowscolumn", "no");
		if (!parameters.containsKey("autoadjustheader")) {
			parameters.put("autoadjustheader", "no");
		}
		if (!parameters.containsKey("rows")) {
			parameters.put("rows", "15");
		}
		try {
			final long tIni = System.currentTimeMillis();
			this.table = new Table(parameters);
			this.table.setControlsVisible(false);
			this.table.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.table.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						if (ReferenceCellEditor.this.table.getJTable().getSelectedRowCount() > 0) {
							if (ReferenceCellEditor.this.buttonOK != null) {
								ReferenceCellEditor.this.buttonOK.setEnabled(true);
							}
						} else {
							if (ReferenceCellEditor.this.buttonOK != null) {
								ReferenceCellEditor.this.buttonOK.setEnabled(false);
							}
						}
					}
				}
			});
			if (!this.table.hasForm()) {
				this.table.getJTable().addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(final MouseEvent e) {
						if (e.getClickCount() == 2) {
							e.consume();
							if (ReferenceCellEditor.this.table.getSelectedRow() >= 0) {
								ReferenceCellEditor.this.buttonOK.doClick(10);
							}
						}
					}
				});

				this.table.getJTable().addKeyListener(new KeyAdapter() {

					@Override
					public void keyPressed(final KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							ReferenceCellEditor.this.buttonOK.doClick();
						}
					}
				});

			}
			this.table.packTable();
			ReferenceCellEditor.logger.debug("ReferenceDataField: Table Creation Time: {}",
					System.currentTimeMillis() - tIni);
		} catch (final Exception e) {
			ReferenceCellEditor.logger.error(this.getClass().toString() + ": Table cannot be created." + e.getMessage(),
					e);
		}
	}

	protected Object getParentKeyValue(final String parentkey) {
		return this.parentForm.getDataFieldValue(parentkey);
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {
		if (this.editorAux == null) {
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}
		if (value != null) {
			ReferenceCellEditor.logger.debug("getTableCellEditorComponent: {}", value.toString());
		} else {
			ReferenceCellEditor.logger.debug("getTableCellEditorComponent");
		}
		if (table != null) {
			this.currentEditor = this.field;
			this.field.deleteData();
			this.field.setValue(value);
			this.editor = this.editorAux;
			this.field.setBorder(new LineBorder(Color.red));
			this.valuesKeys = null;
			return this.editor;
		} else {
			this.currentEditor = null;
			this.valuesKeys = null;
			return null;
		}
	}

	public List getKeys() {
		return this.table.getKeys();
	}

	public List getColumnsToSet() {
		return this.colsSet != null ? this.colsSet : this.table.getKeys();
	}

	@Override
	public boolean stopCellEditing() {

		if (this.field.isEmpty()) {
			this.valuesKeys = null;
			return super.stopCellEditing();
		}
		if (this.valuesKeys == null) {
			this.processingAction(true);
			if (this.valuesKeys != null) {
				return true;
			} else {
				return false;
			}
		}
		return super.stopCellEditing();
	}

	@Override
	public void cancelCellEditing() {
		super.cancelCellEditing();

	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	@Override
	public void setParentForm(final Form parentForm) {
		if (this.field != null) {
			this.field.setParentForm(parentForm);
		}
		this.parentForm = parentForm;
	}

	protected void showTableWindow() {
		if (this.tableWindow == null) {
			this.createTableWindow();
		}
		this.tableWindow.pack();
		if (this.tableWindow.getWidth() < ReferenceCellEditor.MIN_WIDTH_TABLE_WINDOW) {
			this.tableWindow.setSize(ReferenceCellEditor.MIN_WIDTH_TABLE_WINDOW, this.tableWindow.getHeight());
		}
		this.tableWindow.setVisible(true);
	}

	protected void createTableWindow() {
		final Window w = SwingUtilities.getWindowAncestor(this.parentForm);
		if ((w instanceof Frame) || (w == null)) {
			this.tableWindow = new EJDialog((Frame) w,
					ApplicationManager.getTranslation(this.entityName, this.resources), true) {

				@Override
				protected void setInitialFocus() {
					if (ReferenceCellEditor.this.table.getJTable().getRowCount() > 0) {
						ReferenceCellEditor.this.table.setSelectedRow(0);
					}
					ReferenceCellEditor.this.table.getJTable().requestFocus();
				}
			};
			((EJDialog) this.tableWindow).setAutoPackOnOpen(false);
		} else {
			this.tableWindow = new EJDialog((Dialog) w,
					ApplicationManager.getTranslation(this.entityName, this.resources), true) {

				@Override
				protected void setInitialFocus() {
					if (ReferenceCellEditor.this.table.getJTable().getRowCount() > 0) {
						ReferenceCellEditor.this.table.setSelectedRow(0);
					}
					ReferenceCellEditor.this.table.getJTable().requestFocus();
				}
			};
			((EJDialog) this.tableWindow).setAutoPackOnOpen(false);
		}
		final JPanel jButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.tableWindow.getContentPane().add(jButtonsPanel, BorderLayout.SOUTH);
		this.tableWindow.getContentPane().add(this.table);
		this.buttonOK = new JButton(ApplicationManager.getTranslation(ReferenceCellEditor.keyOK, this.resources));
		this.buttonOK.setIcon(ImageManager.getIcon(ImageManager.OK));

		jButtonsPanel.add(this.buttonOK);
		this.tableWindow.pack();
		ApplicationManager.center(this.tableWindow);

		this.buttonOK.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected row
				final int selectedRow = ReferenceCellEditor.this.table.getSelectedRow();
				if (selectedRow >= 0) {
					ReferenceCellEditor.this.valuesKeys = ReferenceCellEditor.this.table.getRowKeys(selectedRow);
					final Map hData = ReferenceCellEditor.this.table.getRowData(selectedRow);
					final List columnsToSet = ReferenceCellEditor.this.getColumnsToSet();
					for (int i = 0; i < columnsToSet.size(); i++) {
						if (hData.containsKey(columnsToSet.get(i))) {
							ReferenceCellEditor.this.valuesKeys.put(columnsToSet.get(i),
									hData.get(columnsToSet.get(i)));
						}
					}
					ReferenceCellEditor.this.field.setValue(hData.get(ReferenceCellEditor.this.columnToSet));
					ReferenceCellEditor.this.tableWindow.setVisible(false);
					ReferenceCellEditor.this.stopCellEditing();
				}
			}
		});
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		this.resources = resources;
		if (this.table != null) {
			this.table.setResourceBundle(resources);
		}
	}

	public Map getValuesKeys() {
		return this.valuesKeys;
	}

	@Override
	public Map getSetData(final boolean useNullValues) {
		final Map setData = new Hashtable(this.valuesKeys);
		if ((this.colsSet != null) && useNullValues) {
			// If the data does not contains some of the columns to set then
			// update the data with NullValue objects
			for (int i = 0; i < this.colsSet.size(); i++) {
				if (!setData.containsKey(this.colsSet.get(i))) {
					if ((this.colsSetTypes != null) && this.colsSetTypes.containsKey(this.colsSet.get(i))) {
						final String colType = (String) this.colsSetTypes.get(this.colsSet.get(i));
						setData.put(this.colsSet.get(i), new NullValue(ParseUtils.getSQLType(colType)));
					} else {
						setData.put(this.colsSet.get(i), new NullValue(Types.VARCHAR));
					}
				}
			}
		}
		return setData;
	}

	@Override
	public List getSetColumns() {
		return this.getColumnsToSet();
	}

	protected void processingAction(final boolean oneResultIsNotShown) {
		// Evaluate if filter is necessary
		Object oValue = this.field.getValue();
		if (oValue == null) {
			final String sText = this.queryField;
			final Object[] args = { ApplicationManager.getTranslation(sText, ReferenceCellEditor.this.resources) };
			final String translation = ApplicationManager.getTranslation(ReferenceCellEditor.NO_DATA_KEY_MESSAGE,
					ReferenceCellEditor.this.resources, args);
			this.parentForm.message(SwingUtilities.getWindowAncestor(this.parentForm), translation,
					Form.WARNING_MESSAGE);
			this.field.requestFocus();
			return;
		}

		if (this.tableWindow == null) {
			this.createTableWindow();
		}

		// Execute the query
		try {
			final Entity entity = this.locator.getEntityReference(this.entityName);
			final Map filter = new Hashtable();

			// If value is asterisk or null then no filter is needed
			if ((oValue != null) && !oValue.toString().equals(this.ASTERISK)) {
				oValue = this.getTypedInnerValue(oValue);
				if ((oValue instanceof String) && this.addAsterisk && (((String) oValue).indexOf(this.ASTERISK) < 0)) {
					oValue = this.ASTERISK + oValue + this.ASTERISK;
				}
				filter.put(this.queryField, oValue);
			}

			if (this.table.getParentKeys() != null) {
				for (int i = 0; i < this.table.getParentKeys().size(); i++) {
					final Object oParentKey = this.table.getParentKeys().get(i);
					final Object oParentKeyValue = this.getParentKeyValue(oParentKey.toString());
					ReferenceCellEditor.logger.debug("Filtering by parent key: {} with value: {}", oParentKey,
							oParentKeyValue);
					if (oParentKeyValue != null) {
						filter.put(oParentKey, oParentKeyValue);
					}
				}
			}

			final EntityResult res = entity.query(filter, this.table.getAttributeList(), this.locator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.parentForm.message(SwingUtilities.getWindowAncestor(ReferenceCellEditor.this), res.getMessage(),
						Form.ERROR_MESSAGE);
			} else {
				if (oneResultIsNotShown && (res.calculateRecordNumber() == 1)) {
					final Map hData = res.getRecordValues(0);
					this.valuesKeys = new Hashtable();
					List columnsToSet = this.table.getKeys();
					for (int i = 0; i < columnsToSet.size(); i++) {
						if (hData.containsKey(columnsToSet.get(i))) {
							this.valuesKeys.put(columnsToSet.get(i), hData.get(columnsToSet.get(i)));
						}
					}
					columnsToSet = this.getColumnsToSet();
					for (int i = 0; i < columnsToSet.size(); i++) {
						if (hData.containsKey(columnsToSet.get(i))) {
							this.valuesKeys.put(columnsToSet.get(i), hData.get(columnsToSet.get(i)));
						}
					}
					this.field.setValue(hData.get(this.columnToSet));
					this.tableWindow.setVisible(false);
					this.stopCellEditing();
				} else {
					this.table.setValue(res);
					this.showTableWindow();
				}
			}
		} catch (final Exception ex) {
			ReferenceCellEditor.logger.debug("{}", ex.getMessage(), ex);
			this.parentForm.message(SwingUtilities.getWindowAncestor(ReferenceCellEditor.this), ex.getMessage(),
					Form.ERROR_MESSAGE);
		}
	}

	@Override
	public int getSQLDataType() {
		if (this.codeNumber) {
			return Types.NUMERIC;
		} else {
			return Types.VARCHAR;
		}
	}

	/**
	 * Obtains the typed value from parameter.
	 * <p>
	 * @param s the object to obtain the type
	 * @return the typed inner value
	 */
	protected Object getTypedInnerValue(final Object s) {
		if (this.codeNumber) {
			return ParseUtils.getValueForClassType(s, this.codeNumberClass);
		} else {
			return s;
		}
	}

	private class EditorComp extends JPanel {

		private JComponent dataComponent = null;

		private JButton detailButton = null;

		public EditorComp(final JComponent c) {
			this.setLayout(new BorderLayout(0, 0));
			this.dataComponent = c;
			this.setOpaque(false);
			this.detailButton = new DataField.FieldButton(ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS)) {

				@Override
				public boolean isFocusTraversable() {
					return false;
				}
			};

			this.add(this.detailButton, BorderLayout.EAST);
			this.detailButton.setRequestFocusEnabled(false);
			this.detailButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					ReferenceCellEditor.this.processingAction(false);
				}
			});
			this.add(ReferenceCellEditor.this.field.getDataField());
			ReferenceCellEditor.this.field.getDataField().setBorder(null);
		}

		@Override
		public void requestFocus() {
			super.requestFocus();
			this.dataComponent.requestFocus();
		}

	};

}
