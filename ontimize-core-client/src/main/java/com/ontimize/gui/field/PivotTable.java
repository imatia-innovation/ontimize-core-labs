package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Constructor;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.table.PivotDetailTableUtils;
import com.ontimize.util.swing.table.PivotTableUtils;
import com.ontimize.util.swing.table.PivotTableUtils.ITotalDetailTableModel;

public class PivotTable extends IdentifiedAbstractFormComponent implements DataComponent {

	private static final Logger logger = LoggerFactory.getLogger(PivotTable.class);

	public static final String ENTITY_PARAMETER = "entity";

	public static final String ROW_PARAMETER = "rows";

	public static final String COLUMN_PARAMETER = "column";

	public static final String VALUE_PARAMETER = "value";

	public static final String KEYS_PARAMETER = "keys";

	public static final String PARENTKEYS_PARAMETER = "parentkeys";

	public static final String OPERATION_PAREMETER = "operation";

	public static final String AVG_OPERATION_VALUE = "avg";

	public static final String SUM_OPERATION_VALUE = "sum";

	public static final String MAX_OPERATION_VALUE = "max";

	public static final String MIN_OPERATION_VALUE = "min";

	public static final String COUNT_OPERATION_VALUE = "count";

	public static final String DATE_GROUP_PARAMETER = "date_group";

	public static final String DMY_DATE_GROUP_VALUE = "dmy";

	public static final String MY_DATE_GROUP_VALUE = "my";

	public static final String QY_DATE_GROUP_VALUE = "qy";

	public static final String M_DATE_GROUP_VALUE = "m";

	public static final String Q_DATE_GROUP_VALUE = "q";

	public static final String Y_DATE_GROUP_VALUE = "y";

	public static final String COMPARATOR = "comparator";

	public static final String PERCENTAGE_PARAMETER = "percentage";

	public static int DEFAULT_OPERATION = PivotTableUtils.AVG;

	public static int DEFAULT_DATE_GROUP = PivotTableUtils.DMY;

	protected JTable table = new JTable();

	protected String entity;

	protected List rows;

	protected String column;

	protected String value;

	protected List keys;

	protected List parentkeys;

	protected ResourceBundle bundle;

	protected Object cacheValue = null;

	protected int queryRowNumber = -1;

	protected int operation = PivotTable.DEFAULT_OPERATION;

	protected int date_group = PivotTable.DEFAULT_DATE_GROUP;

	protected Map comparators;

	protected boolean percentage;

	protected Map parameters;

	public PivotTable(final Map h) throws Exception {
		this.init(h);
		this.jInit();
	}

	public JTable getJTable() {
		return this.table;
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters the Map with parameters
	 *
	 *        <p>
	 *
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>entity</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of the entity to execute the query. This is the component attribute too.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>rows</td>
	 *        <td>colum1;column2;...;columnN</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of the Y axis columns to groyp by</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>column</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of the x axis column. This is the pivot column.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>value</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td></td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>keys</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Entity keys</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>parentkeys</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Foreign key column names</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>operation</td>
	 *        <td>avg/sum/max/min/count</td>
	 *        <td>avg</td>
	 *        <td>no</td>
	 *        <td>Operation to execute with the data in the column specified in the 'value'
	 *        parameter</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>date_group</td>
	 *        <td>dmy,my,qy,m,q,y</td>
	 *        <td>dmy</td>
	 *        <td>no</td>
	 *        <td>Groupping type to apply in date columns (d-day,m-month,y-year,q-quarter)</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>comparator</td>
	 *        <td>column1;comparatorNameClass;column2;comparatorNameClass</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Class to assign to the different columns or rows to set the appropriate order</td>
	 *        </tr>
	 *
	 *        </Table>
	 *
	 */
	@Override
	public void init(final Map parameters) {
		this.parameters = parameters;
		Object param = parameters.get(PivotTable.ENTITY_PARAMETER);
		if (param != null) {
			this.entity = param.toString();
		} else {
			throw new IllegalArgumentException(PivotTable.ENTITY_PARAMETER + " parameter is required");
		}

		param = parameters.get(PivotTable.ROW_PARAMETER);
		if (param != null) {
			final String current = param.toString();
			this.rows = ApplicationManager.getTokensAt(current, ";");
		} else {
			throw new IllegalArgumentException(PivotTable.ROW_PARAMETER + " parameter is required");
		}

		param = parameters.get(PivotTable.COLUMN_PARAMETER);
		if (param != null) {
			this.column = param.toString();
		} else {
			throw new IllegalArgumentException(PivotTable.COLUMN_PARAMETER + " parameter is required");
		}

		param = parameters.get(PivotTable.VALUE_PARAMETER);
		if (param != null) {
			this.value = param.toString();
		} else {
			throw new IllegalArgumentException(PivotTable.VALUE_PARAMETER + " parameter is required");
		}

		param = parameters.get(PivotTable.KEYS_PARAMETER);
		if (param != null) {
			final String current = param.toString();
			this.keys = ApplicationManager.getTokensAt(current, ";");
		} else {
			throw new IllegalArgumentException(PivotTable.KEYS_PARAMETER + " parameter is required");
		}

		param = parameters.get(PivotTable.PARENTKEYS_PARAMETER);
		if (param != null) {
			final String current = param.toString();
			this.parentkeys = ApplicationManager.getTokensAt(current, ";");
		}

		param = parameters.get(PivotTable.OPERATION_PAREMETER);
		if (param != null) {
			if (PivotTable.SUM_OPERATION_VALUE.equals(param)) {
				this.operation = PivotTableUtils.SUM;
			} else if (PivotTable.AVG_OPERATION_VALUE.equals(param)) {
				this.operation = PivotTableUtils.AVG;
			} else if (PivotTable.MAX_OPERATION_VALUE.equals(param)) {
				this.operation = PivotTableUtils.MAX;
			} else if (PivotTable.MIN_OPERATION_VALUE.equals(param)) {
				this.operation = PivotTableUtils.MIN;
			} else if (PivotTable.COUNT_OPERATION_VALUE.equals(param)) {
				this.operation = PivotTableUtils.COUNT;
			}
		}

		param = parameters.get(PivotTable.DATE_GROUP_PARAMETER);
		if (param != null) {
			if (PivotTable.DMY_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.DMY;
			} else if (PivotTable.MY_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.MY;
			} else if (PivotTable.QY_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.QY;
			} else if (PivotTable.M_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.M;
			} else if (PivotTable.Q_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.Q;
			} else if (PivotTable.Y_DATE_GROUP_VALUE.equals(param)) {
				this.date_group = PivotTableUtils.Y;
			}
		}

		// Comparator to the columns or rows
		param = parameters.get(PivotTable.COMPARATOR);
		if (param != null) {
			this.comparators = new Hashtable();
			final List v = ApplicationManager.getTokensAt(param.toString(), ";");
			for (int i = 0; (i + 1) < v.size(); i = i + 2) {
				final String columnName = (String) v.get(i);
				final String className = (String) v.get(i + 1);
				if (!this.rows.contains(columnName) && !this.column.equals(columnName)) {
					PivotTable.logger.debug("Column name " + columnName + "must be a column");
					continue;
				}
				try {
					final Class comparatorClass = Class.forName(className);
					final Constructor constr = comparatorClass.getConstructor(null);
					final Object instance = constr.newInstance(null);
					if (!(instance instanceof Comparator)) {
						PivotTable.logger.debug(className + " instance doesn't implement Comparator");
						continue;
					}
					this.comparators.put(columnName, instance);
				} catch (final Exception exc) {
					PivotTable.logger.error(className + " doesn't exist", exc);
				}
			}
		}

		param = parameters.get(PivotTable.PERCENTAGE_PARAMETER);
		if (param != null) {
			final String current = param.toString();
			this.percentage = ParseUtils.getBoolean(current, false);
		} else {
			this.percentage = false;
		}
	}

	protected void jInit() {
		final JScrollPane scroll = new JScrollPane(this.table);
		this.setLayout(new BorderLayout());
		this.add(scroll, BorderLayout.CENTER);
		scroll.setBorder(
				ParseUtils.getBorder(this.parameters != null ? (String) this.parameters.get("border") : null,
						BorderManager.getBorder(BorderManager.DEFAULT_TABLE_BORDER_KEY)));
		this.table.setDefaultRenderer(Object.class, new PivotTableUtils.NumberRenderer());
		this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.table.getTableHeader().setReorderingAllowed(false);
		this.table.setRowMargin(0);
		this.table.getTableHeader()
		.setDefaultRenderer(new PivotTableUtils.HeaderRenderer(this.bundle,
				this.parameters != null ? this.parameters : new Hashtable()));
		this.installDetailMouseListener();
	}

	protected EJDialog dDetailPivot = null;

	protected void installDetailMouseListener() {
		this.table.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				super.mouseClicked(e);
				if (e.getClickCount() == 2) {
					final Object obj = e.getSource();
					if (obj instanceof JTable) {
						int col = ((JTable) obj).getSelectedColumn();
						int row = ((JTable) obj).getSelectedRow();

						final TableModel model = ((JTable) obj).getModel();
						if (model instanceof ITotalDetailTableModel) {
							if (row == (PivotTable.this.table.getRowCount() - 1)) {
								row = -1;
							}
							if (col == (PivotTable.this.table.getColumnCount() - 1)) {
								col = -1;
							}
							final Cursor cursor = PivotTable.this.table.getCursor();
							try {
								PivotTable.this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								final TableModel mod = ((ITotalDetailTableModel) model).getDetailTableModel(row, col);
								final Map information = ((ITotalDetailTableModel) model).getDetailTableInformation(row,
										col);
								PivotTable.this.showDetailPivotTableWindow(mod, information);
							} finally {
								PivotTable.this.table.setCursor(cursor);
							}
						}
					}
				}
			}
		});
	}

	protected void showDetailPivotTableWindow(final TableModel model, final Map information) {
		if (this.dDetailPivot == null) {
			final Window w = SwingUtilities.getWindowAncestor(PivotTable.this);
			this.dDetailPivot = PivotDetailTableUtils.createPivotDetailDialog(w, model,
					this.getDetailWindowParameters(), this.bundle);
		}
		((PivotDetailTableUtils.PivotDetailDialog) this.dDetailPivot).setModel(model, information);
		((PivotDetailTableUtils.PivotDetailDialog) this.dDetailPivot).setResourceBundle(this.bundle);
		this.dDetailPivot.setVisible(true);
	}

	protected Map getDetailWindowParameters() {
		final Map param = new Hashtable();
		param.putAll(this.parameters);
		param.put("entity", this.entity);
		param.put("dynamic", "yes");
		param.put("translateheader", "yes");
		return param;
	}

	protected List getAttributes() {
		final List attributes = ObjectTools.clone(this.rows);
		attributes.add(this.column);
		attributes.add(this.value);
		return attributes;
	}

	protected List getKeys() {
		return this.keys;
	}

	protected List getParentkeys() {
		return this.parentkeys;
	}

	@Override
	public Object getAttribute() {
		final TableAttribute tableAttribute = new TableAttribute();
		tableAttribute.setEntityAndAttributes(this.entity, this.getAttributes());
		tableAttribute.setRecordNumberToInitiallyDownload(this.queryRowNumber);
		tableAttribute.setKeysParentkeysOtherkeys(this.getKeys(), this.getParentkeys());
		return tableAttribute;
	}

	@Override
	public void deleteData() {
		this.table.setModel(new DefaultTableModel());
	}

	@Override
	public String getLabelComponentText() {
		return null;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public boolean isModifiable() {
		return false;
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
	public void setModifiable(final boolean modifiable) {

	}

	@Override
	public void setRequired(final boolean required) {

	}

	@Override
	public Object getValue() {
		return this.cacheValue;
	}

	@Override
	public void setValue(final Object value) {
		this.cacheValue = value;
		if ((value == null) || (!(value instanceof Map)) || ((Map) value).isEmpty()) {
			this.deleteData();
			return;
		}

		if (value instanceof EntityResult) {
			final TableModel model = EntityResultUtils.createTableModel((EntityResult) value);
			TableModel pModel = null;
			if ((this.comparators == null) || this.comparators.isEmpty()) {
				pModel = PivotTableUtils.PivotPanel.create(model,
						(String[]) this.rows.toArray(new String[this.rows.size()]), this.column, this.value,
						this.operation,
						this.date_group);
			} else {
				pModel = PivotTableUtils.PivotPanel.create(model,
						(String[]) this.rows.toArray(new String[this.rows.size()]), this.column, this.value,
						this.operation,
						this.date_group, this.comparators);
			}
			this.table.setModel(pModel);

			if (this.table.getColumnCount() > 0) {
				for (int i = 0; i < this.rows.size(); i++) {
					this.table.getColumnModel().getColumn(i).setCellRenderer(new PivotTableUtils.RowHeaderRenderer());

				}
			}

			if (pModel instanceof PivotTableUtils.TotalTableModel) {
				final PivotTableUtils.TotalNumberRenderer rend = new PivotTableUtils.TotalNumberRenderer(Color.lightGray);
				rend.setPercentage(this.percentage);
				this.table.getColumnModel().getColumn(this.table.getColumnCount() - 1).setCellRenderer(rend);
			}

			for (int i = 0; i < this.table.getColumnModel().getColumnCount(); i++) {
				this.table.getColumnModel().getColumn(i).sizeWidthToFit();
			}
		}
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		this.bundle = res;
		final Object obj = this.table.getTableHeader().getDefaultRenderer();
		if (obj instanceof Internationalization) {
			((Internationalization) obj).setResourceBundle(res);
		}
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	@Override
	public void setFont(final Font f) {
		super.setFont(f);
		if (this.table != null) {
			this.getJTable().setFont(f);
			this.table.getTableHeader().setFont(f);
		}
	}

	public boolean isPercentage() {
		return this.percentage;
	}

	public void setPercentage(final boolean percentage) {
		this.percentage = percentage;
	}

}
