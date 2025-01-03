package com.ontimize.gui.table;

import java.awt.Adjustable;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.jfree.chart.ChartUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.FormBuilder;
import com.ontimize.cache.CacheManager;
import com.ontimize.cache.CachedComponent;
import com.ontimize.cache.DateFormatCache;
import com.ontimize.chart.ChartInfo;
import com.ontimize.chart.ChartInfoRepository;
import com.ontimize.chart.ChartUtilities_1_0;
import com.ontimize.chart.ChartVersionControl;
import com.ontimize.chart.IChartUtilities;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.Application;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BackgroundFormBuilderManager;
import com.ontimize.gui.BorderManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.CreateForms;
import com.ontimize.gui.DetailForm;
import com.ontimize.gui.DynamicFormManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormExt;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.HasHelpIdComponent;
import com.ontimize.gui.IDetailForm;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.SelectCurrencyValues;
import com.ontimize.gui.TabbedDetailForm;
import com.ontimize.gui.TipScroll;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.CurrencyDataField;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.IFilterElement;
import com.ontimize.gui.field.IntegerDataField;
import com.ontimize.gui.field.ListDataField;
import com.ontimize.gui.field.RealDataField;
import com.ontimize.gui.field.SelectionListDataField.SelectableItem;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.manager.ITabbedFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.preferences.ApplicationPreferencesListener;
import com.ontimize.gui.preferences.BasicApplicationPreferences;
import com.ontimize.gui.preferences.HasPreferenceComponent;
import com.ontimize.gui.preferences.PreferenceEvent;
import com.ontimize.gui.table.SumRowTable.SumRowBorder;
import com.ontimize.gui.table.TableSorter.ColumnSizeEvent;
import com.ontimize.gui.table.TableSorter.DateFilter;
import com.ontimize.gui.table.TableSorter.Filter;
import com.ontimize.gui.table.TableSorter.GroupTableModel.GroupList;
import com.ontimize.gui.table.TableSorter.MultipleFilter;
import com.ontimize.gui.table.TableSorter.SimpleFilter;
import com.ontimize.gui.table.TableSorter.ValueByGroup;
import com.ontimize.gui.table.blocked.BlockedBoundedRangeModel;
import com.ontimize.gui.table.blocked.BlockedTable;
import com.ontimize.gui.table.blocked.BlockedTableModel;
import com.ontimize.help.HelpUtilities;
import com.ontimize.jee.common.db.AdvancedEntity;
import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.db.DynamicMemoryEntity;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.ConnectionOptimizer;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.security.FormPermission;
import com.ontimize.jee.common.security.TableFormPermission;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.jee.common.util.share.IShareRemoteReference;
import com.ontimize.jee.common.util.share.SharedElement;
import com.ontimize.jee.common.util.swing.SwingUtils;
import com.ontimize.report.DefaultReportDialog;
import com.ontimize.security.ClientSecurityManager;
import com.ontimize.util.FileUtils;
import com.ontimize.util.FormatPattern;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.PreferenceUtils;
import com.ontimize.util.math.MathExpressionParserFactory;
import com.ontimize.util.share.FormAddSharedReference;
import com.ontimize.util.share.FormAddUserSharedReference;
import com.ontimize.util.share.FormUpdateSharedReference;
import com.ontimize.util.swing.MenuButton;
import com.ontimize.util.swing.RolloverButton;
import com.ontimize.util.swing.Toast;
import com.ontimize.util.swing.table.PivotTableUtils;
import com.ontimize.util.swing.table.PivotTableUtils.PivotDialog;
import com.ontimize.util.templates.ITemplateField;
import com.ontimize.util.xls.XLSExporter;
import com.ontimize.util.xls.XLSExporterFactory;

/**
 * The <code>Table</code> is used to display regular two-dimensional tables of cells.
 * <p>
 * The <code>Table</code> has many facilities that make it possible to customize its rendering and
 * editing but provides defaults for these features so that simple tables can be set up easily. For
 * example, there are default renderers for the most common data types such as Date, Timestamp,
 * Integer, Double, Float, String, Boolean.
 * <p>
 * The <code>Table</code> can be added to a {@link com.ontimize.gui#Form Form} element by using the
 * default XML label Table. The structure is as follows:
 * <P>
 * &lt;Table {attribute=value} /&gt;
 * <P>
 * The attributes allowed can be found in methods: </br>
 * - {@link #init(Map params)} where params contains the xml attributes.</br>
 * -{@link #configureButtons(Map params)} where all table button icons are configured. </br>
 * - {@link SortTableCellRenderer#init(Map params)} where are additional configurations for
 * table header.</br>
 * - {@link #configureQuickFilter(Map params)} where are additional configurations for table
 * quickfilter search box.</br>
 * -When table is autoinsertable (<b>inserttable='yes'</b>) there are additional parameters in
 * {@link #configureInsertTable(Map params)}
 */

public class Table extends JRootPane
implements DataComponent, CreateForms, OpenDialog, AccessForm, Freeable, SelectCurrencyValues,
ApplicationPreferencesListener, HasPreferenceComponent, HasHelpIdComponent,
ReferenceComponent, InsertTableInsertRowChange, ITemplateField, IFilterElement, ListSelectionListener,
IRefreshable, InteractionManagerModeListener {

	public static final String DELETED_ROW = "DeletedRow";

	private static final Logger logger = LoggerFactory.getLogger(Table.class);

	//
	// Static Constants
	//
	/**
	 * Name of default cell rederer
	 */
	public static final String DEFAULT_CELL_RENDERER = "DEFAULT_CELL_RENDERER";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String INSERT_TABLE = "inserttable";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TRANSLATE_HEADER = "translateheader";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DYNAMIC = "dynamic";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String REQUIRED_COLS = "requiredcols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String REPORT_COLS = "reportcols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DATABASE_REMOVE = "databaseremove";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DATABASE_INSERT = "databaseinsert";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DETAIL_FORMAT = "detailformat";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DETAIL_TITLE_MAX_SIZE = "detailtitlemaxsize";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String INSERT_TITLE_KEY = "inserttitlekey";

	@Override
	public void setLayeredPane(final JLayeredPane layered) {
		super.setLayeredPane(layered);
	}

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DETAIL_DATE_FORMAT = "detaildateformat";

	/**
	 * Complete path to renderer and editor configuration file. It is used in constructor of this class.
	 *
	 * @see TableConfigurationManager
	 */
	public static String rendererEditorConfigurationFile = null;

	/**
	 * Default class to use in the xls export button
	 */
	public static String XLS_EXPORT_CLASS = XLSExporterFactory.POI;

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String QUICK_FILTER = "quickfilter";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String QUICK_FILTER_VISIBLE = "quickfiltervisible";


	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String QUICK_FILTER_ON_FOCUS_SELECT_ALL = "quickfilteronfocusselectall";


	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String PIVOT_BUTTON = "pivotbutton";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CUSTOM_CHARTS = "customcharts";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CONTROLS_VISIBLE = "controlsvisible";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String MEMO_RENDER = "rendermemo";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String UPDATE_ENTITY_EDITABLE_COLUMNS = "updateentityeditablecolumns";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String EDITABLE_COLUMNS = "editablecolumns";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TIME_RENDER = "rendertime";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CURRENCY = "currency";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CALCULED_COLS_REQUIRED_FIELDS = "calculedcolsrequiredfields";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CALCULED_COLS = "calculedcols";

	public static final String MODIFIABLE_CALCULATED_COLUMNS = "modifiablecalculatedcols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String FIX_ATTR = "fixattr";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String OTHER_KEYS = "otherkeys";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String FONT_SIZE = "fontsize";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CONTROLS = "controls";


	/**
	 * Attribute name allowed in the XML
	 */

	public static final String BLOCKED_COLS = "blockedcols";


	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DYNAMIC_FORM = "dynamicform";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String FORM = "form";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String INSERT_FORM = "insertform";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String PARENTKEY = "parentkey";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String KEYS = "keys";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String KEY = "key";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String VISIBLE_COLS = "visiblecols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String QUICK_FILTER_COLS = "quickfiltercols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DEFAULT_VISIBLE_COLS = "defaultvisiblecols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String COLS = "cols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String REFRESH_BUTTON = "refreshbutton";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String ROWS = "rows";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String QUERY_ROWS = "queryrows";

	public static final String QUERY_ROWS_MODIFIABLE = "queryrowsmodifiable";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DELETE_BUTTON = "deletebutton";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String SUM_ROW = "sumrow";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String TITLE = "title";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String MEMORY_ENTITY = "memoryentity";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String ENTITY = "entity";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String NUM_ROWS_COLUMN = "numrowscolumn";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String HORIZONTAL_SCROLL = "scrollh";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String AUTO_ADJUST_HEADER = "autoadjustheader";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DISABLE_INSERT = "disableinsert";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String CONF_VISIBLE_COLS = "confvisiblecols";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String DEFAULT_NEW_WINDOW = "defaultnewwindow";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */
	public static final String OPEN_NEW_WINDOW = "opennewwindow";

	/**
	 * Attribute name allowed in the XML.
	 *
	 * @see #init
	 */

	public static final String ONSETVALUESET = "onsetvalueset";

	public static final String DEFAULT_BUTTONS = "defaultbuttons";

	/**
	 * Key name to store preferences related to the <code>Table</code>
	 */
	public static final String OPERATION_PREFERENCE = "table_operation_preferences";

	public static final String SHOWGRID = "showgrid";

	public static final String FITROWHEIGHT = "fitrowheight";

	public static final String MINROWHEIGHT = "minrowheight";

	public static final String TABLEOPAQUE = "tableopaque";

	public static final String TABLEBGCOLOR = "tablebgcolor";

	/**
	 * Attribute name allowed in the XML
	 *
	 * @see #init
	 */
	public static final String CODS = "cods";

	public static final String CONTINUOUS = "continuous";

	public static final String SINGLE = "single";

	public static final String SELECTIONMODE = "selectionmode";

	/**
	 * Attribute name allowed in the XML
	 *
	 * @see #init
	 */
	public static final String BACKGROUND_FORM_BUILDER = "backgroundformbuilder";

	/**
	 * Attribute name allowed in the XML
	 *
	 * @see #init
	 */
	public static final String INSERT_MODE = "insertmode";

	/**
	 * Attribute name allowed in the XML
	 *
	 * @see #init
	 */
	public static final String SORT_REPORT_COLUMS = "sortreportcolumns";

	/**
	 * Attribute name allowed in the XML
	 *
	 * @see #init
	 */
	public static final String D_PIVOT_TABLE_PREFERENCES = "dpivottablepref";

	public static final String D_PIVOT_TABLE_PREFERENCES_FORM = "dynamicPivotTableForm";

	protected String dynamicPivotTableForm = null;

	public static final String D_PIVOT_TABLE_PREFERENCES_ENTITY = "dynamicPivotTableEntity";

	protected String dynamicPivotTableEntity = null;

	protected CalculatedCellListModel renderListModel = new CalculatedCellListModel();

	private static Map<String, CellRenderer> rendererMap;

	public static void initRendererMap() {
		if (Table.rendererMap == null) {
			Table.rendererMap = new LinkedHashMap<String, CellRenderer>();
			Table.rendererMap.put(Table.DEFAULT_CELL_RENDERER, null);
			Table.rendererMap.put("BOOLEAN_CELL_RENDERER", new BooleanCellRenderer());
			// Table.rendererMap.put("DATE_CELL_RENDERER", new
			// DateCellRenderer());
			Table.rendererMap.put("PERCENT_CELL_RENDERER", new PercentCellRenderer());
			Table.rendererMap.put("CURRENCY_CELL_RENDERER", new CurrencyCellRenderer());
			Table.rendererMap.put("REAL_CELL_RENDERER", new RealCellRenderer());
		}
	}

	/**
	 * An interface that provides access to {@link #TableCellEditor}.
	 *
	 * @author Imatia Innovation
	 */
	public static interface EditorManager {

		/**
		 * Returns the {@link #TableCellEditor} associated to the <code>JTable</code> specified by the
		 * params, in the concrete row and column.
		 * @param table
		 * @param row
		 * @param column
		 * @return
		 */
		public TableCellEditor getCellEditor(JTable table, int row, int column);

	}

	/**
	 * An interface that provides access to {@link #TableCellRenderer}.
	 *
	 * @author Imatia Innovation
	 */
	public static interface RendererManager {

		/**
		 * Returns the {@link #TableCellRenderer} associated to the <code>JTable</code> specified by the
		 * params, in the concrete row and column.
		 * @param table
		 * @param row
		 * @param column
		 * @return
		 */
		public TableCellRenderer getCellRenderer(JTable table, int row, int column);

	}

	/**
	 * The default implementation of the {@link #RendererManager}. Provides references to the table
	 * renderes.
	 *
	 * @author Imatia Innovation
	 */
	public static class DefaultRendererManager implements RendererManager {

		/**
		 * The date cell renderer set to this RendererManager
		 */
		protected DateCellRenderer rDate = new DateCellRenderer();

		/**
		 * The boolean cell renderer set to this RendererManager
		 */
		protected BooleanCellRenderer rBoolean = new BooleanCellRenderer();

		/**
		 * The real cell renderer set to this RendererManager
		 */
		protected RealCellRenderer rReal = new RealCellRenderer();

		/**
		 * The object cell renderer set to this RendererManager
		 */
		protected ObjectCellRenderer rObject = new ObjectCellRenderer();

		/**
		 * The image cell renderer set to this RendererManager
		 */
		protected ImageCellRenderer rImage = new ImageCellRenderer(new Hashtable());

		/**
		 * Returns a cell renderer depending on the cell data type.
		 */
		@Override
		public TableCellRenderer getCellRenderer(final JTable jTable, final int row, final int column) {
			final Object oValue = jTable.getValueAt(row, column);
			if (oValue != null) {
				if (oValue instanceof java.util.Date) {
					return this.rDate;
				} else if (oValue instanceof Number) {
					return this.rReal;
				} else if (oValue instanceof Boolean) {
					return this.rBoolean;
				} else if (oValue instanceof BytesBlock) {
					return this.rImage;
				} else {
					return this.rObject;
				}
			}
			return null;
		}

	}

	/**
	 * Allows the user to modify the visible cols
	 */
	public static boolean DEFAULT_VALUE_SETUP_VISIBLE_COLS = true;

	/**
	 * Allows the user to modify pivot table visibility
	 */
	public static boolean defaultPivotTableVisibility = true;

	/**
	 * Allows the user to show/hide globally the quickfilter warning message on paged tables.
	 *
	 * @since 5.2072EN-0.1
	 */
	public static boolean defaultQuickFilterWarningVisibility = true;

	/**
	 * Allows the user to modify quickfilter functionality
	 */
	public static boolean DEFAULT_QUICK_FILTER = true;

	/**
	 * Allows the user to modify quickfilter visibility
	 */

	public static boolean defaultQuickFilterVisible = true;

	public static boolean defaultQuickFilterOnFocusSelectAll = false;

	public static boolean defaultSortReportColumns = true;

	/**
	 * Forces the column order to like the visible cols order.
	 */
	public static boolean ORDER_COLS_BY_VISIBLE_COLS = false;

	/**
	 * @deprecated
	 */
	@Deprecated
	public static boolean DEFAULT_VALUE_SET_HEIGHT_HEAD = false;

	/**
	 * The table opens the detail forms in new windows, instead using dialogs.
	 */
	public static boolean SHOW_OPEN_IN_NEW_WINDOW_MENU = false;

	/**
	 * Allows the user to save the filter configuration
	 *
	 * @see #confFiltroOrden
	 */
	public static boolean PERMIT_SAVE_FILTER_ORDER_CONFIGURATION = true;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int PIE = ChartVersionControl.PIE;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int PIE_3D = ChartVersionControl.PIE_3D;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int BAR = ChartVersionControl.BAR;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int BAR_3D = ChartVersionControl.BAR_3D;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int STACKED_3D = ChartVersionControl.STACKED_3D;

	/**
	 * Field number for charting indicating the chart type.
	 */
	public static final int LINE = ChartVersionControl.LINE;

	/**
	 * Field number for charting indicating the period type.
	 */
	public static final int MONTH = ChartVersionControl.MONTH;

	/**
	 * Field number for charting indicating the period type.
	 */
	public static final int QUARTER = ChartVersionControl.QUARTER;

	/**
	 * Field number for charting indicating the period type.
	 */
	public static final int YEAR = ChartVersionControl.YEAR;

	/**
	 * Configures the MEMORY trace, to see how much memory is being used by the table. This traces are
	 * focused on the printing process.
	 */
	public static boolean DEBUG_MEMORY = false;

	/**
	 * Allows the charting functionality
	 */
	public static boolean CHART_ENABLED = true;

	protected static boolean CHART_V1 = true;

	/**
	 * The key for the preference to store the columns position
	 */
	public static final String SAVE_WIDTH_POSITION_COLUMNS = "table.save_width_position_columns";

	/**
	 * GUI text
	 */
	public static final String M_MODIFIED_DATA_CLOSE_AND_LOST_CHANGES = "detailform.modified_data_discard_changes_and_close";

	/**
	 * GUI text
	 */
	public static final String HEAD_TIP_COD = "table.left_click_to_sort_right_click_to_filter";

	/**
	 * GUI text
	 */
	public static final String SORT_HEAD_TIP_COD = "table.left_click_to_sort";

	/**
	 * GUI text
	 */
	public static final String FILTER_HEAD_TIP_COD = "table.right_click_to_filter";

	/**
	 * GUI text
	 */
	public static final String BLOCKED_COLUMN_TIP_COD = "table.blocked_column_tooltip";

	/**
	 * GUI text
	 */
	public static final String OF = "OF";

	/**
	 * The minimum row height default value
	 */
	public static int MIN_ROW_HEIGHT = 8;

	/**
	 * GUI text
	 * @deprecated
	 */
	@Deprecated
	public static final String DOWN_10 = "table.download_10+";

	/**
	 * GUI text
	 * @deprecated
	 */
	@Deprecated
	public static final String DOWN_50 = "table.download_50+";

	/**
	 * GUI text
	 * @deprecated
	 */
	@Deprecated
	public static final String DOWN_100 = "table.download_100+";

	/**
	 * GUI text
	 * @deprecated
	 */
	@Deprecated
	public static final String DOWN_ALL = "table.download_all";

	/**
	 * GUI text
	 * @deprecated
	 */
	@Deprecated
	public static final String TOTAL = "table.total";

	/**
	 * GUI text
	 */
	public static final String RECORDS = "table.registers";

	/**
	 * GUI text
	 */
	public static final String IT_IS_SHOWED = "table.showing";

	/**
	 * GUI text
	 */
	public static final String TOTAL_es_ES = "Total: ";

	/**
	 * GUI text
	 */
	public static final String RECORDS_es_ES = "Total: ";

	/**
	 * GUI text
	 */
	public static final String IT_IS_SHOWED_es_ES = "Se muestran ";

	/**
	 * GUI text
	 */
	public static final String TIP_CLIPBOARD_COPY = "table.copy_to_clipboard";

	/**
	 * GUI text
	 */
	public static final String TIP_EXCEL_EXPORT = "table.export_excel_format";

	/**
	 * GUI text
	 */
	public static final String TIP_PRINTING = "table.print";

	/**
	 * GUI text
	 */
	public static final String PRINT_START = "table.print_start";

	/**
	 * GUI text
	 */
	public static final String TIP_HTML_EXPORT = "table.export_html_format";

	/**
	 * GUI text
	 */
	public static final String TIP_INSERT_BUTTON = "table.insert_new_row";

	/**
	 * GUI text
	 */
	public static final String TIP_CHART_MENU = "table.custom_charts";

	/**
	 * GUI text
	 */
	public static final String TIP_VISIBLES_COLS_SETUP = "table.configure_visible_columns";

	/**
	 * GUI text
	 */
	public static final String TIP_SUMROW_SETUP = "table.total_rows_configuration";

	/**
	 * GUI text
	 */
	public static final String TIP_FILTER_ORDEN_CONF = "table.configure_position_order_filter";

	/**
	 * GUI text
	 */
	public static final String TIP_REPORT_PRINTING = "table.report_printing";

	/**
	 * GUI text
	 */
	public static final String TIP_CHART_MENU_es_ES = "Gráficas a la carta";

	/**
	 * GUI text
	 */
	public static final String TIP_CLIPBOARD_COPY_es_ES = "Copiar al portapapeles";

	/**
	 * GUI text
	 */
	public static final String TIP_EXCEL_EXPORT_es_ES = "Exportar a archivo de texto";

	/**
	 * GUI text
	 */
	public static final String TIP_PRINTING_es_ES = "print";

	/**
	 * GUI text
	 */
	public static final String TIP_HTML_EXPORT_es_ES = "Exportar como página HTML";

	/**
	 * GUI text
	 */
	public static final String TIP_INSERT_BUTTON_es_ES = "Insertar un registro";

	/**
	 * GUI text
	 */
	public static final String TIP_VISIBLES_COLS_SETUP_es_ES = "Configurar columnas visibles";

	/**
	 * GUI text
	 */
	public static final String TIP_SAVE_FILTER_ORDER_CONFIGURATION_es_ES = "Guardar configuración actual de filtro y ordenación";

	/**
	 * GUI text
	 */
	public static final String TIP_REPORT_PRINTING_es_ES = "Impresión de informes";

	/**
	 * GUI text
	 */
	public static final String TIP_PIVOT_TABLE_es_ES = "Tabla pivotada";

	/**
	 * GUI text
	 */
	public static final String TIP_PIVOT_TABLE = "table.pivot_table";

	public static final String TIP_GROUP_TABLE_BUTTON = "table.group_table_button";

	public static final String TIP_CALCULATED_COLUMNS = "table.calculated_columns";

	/**
	 * GUI text
	 */
	public static final String M_PRINTING_CANCELED = "table.printing_process_cancelled";

	/**
	 * GUI text
	 */
	public static final String M_ERROR_PRINTING_TABLE = "table.error_printing_table";

	/**
	 * GUI text
	 */
	public static final String M_PRINTING_FINISHED = "table.printing_task_has_finished";

	/**
	 * GUI text
	 */
	public static final String M_WOULD_YOU_LIKE_TO_DELETE_ROWS = "table.do_you_really_want_to_delete_selected_rows";

	/**
	 * GUI text
	 */
	public static final String M_CANT_DELETE_TABLE_IS_GROUPED = "table.cant_delete_table_is_grouped";

	/**
	 * GUI text
	 */
	public static final String M_SELECTION_ONLY_ONE_ROW_TO_OPEN_DETAIL_FORM = "table.please_select_single_row_to_open_detail_form";

	/**
	 * GUI text
	 */
	public static final String M_SELECTION_LESS_THAN_5_ROWS = "table.select_less_than_five_rows";

	/**
	 * GUI text
	 */
	public static final String M_WOULD_YOU_LIKE_TO_DELETE_THIS_CONFIGURATION_OF_FILTER = "table.delete_filter_configuration";

	/**
	 * GUI text
	 */
	public static final String M_WOULD_YOU_LIKE_TO_DELETE_THIS_SHARE_FILTER = "shareRemote.delete_share_filter_configuration";

	/**
	 * GUI text
	 */
	public static final String FILTER_COLUMN_es_ES = "table.filter_by_{0}";

	/**
	 * GUI text
	 */
	public static final String FILTER_BY_VALUE_es_ES = "table.filter_by_value_{0}";

	/**
	 * GUI text
	 */
	public static final String DELETE_FILTER_es_ES = "table.delete_all_filter";

	/**
	 * GUI text
	 */
	public static final String DELETE_FILTER_COLUMN = "table.delete_filter_of_column";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_es_ES = "table.group_by_column";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_DATE_es_ES = "table.group_by_date_column";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_YEAR = "table.group_by_year";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_YEAR_MONTH = "table.group_by_year_and_month";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_YEAR_MONTH_DAY = "table.group_by_year_month_and_day";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_QUARTER_YEAR = "table.group_by_quarter_and_year";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_WEEK_YEAR = "table.group_by_week_and_year";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_QUARTER = "table.group_by_quarter";

	/**
	 * GUI text
	 */
	public static final String GROUP_COLUMN_BY_MONTH = "table.group_by_month";

	/**
	 * GUI text
	 */
	public static final String GROUP_FUNCTION = "table.group_function";

	/**
	 * GUI text
	 */
	public static final String UNGROUP_COLUMN_es_ES = "table.undo_column_grouping";

	/**
	 * GUI text
	 */
	public static final String DELETE_GROUP_es_ES = "table.undo_grouping";

	/**
	 * GUI text
	 */
	public static final String COPY_CELL = "table.copy_cell";

	/**
	 * GUI text
	 */
	public static final String COPY_SELECTION = "table.copy_selection";

	/**
	 * GUI text
	 */
	public static final String PRINTING_SELECTION = "table.print_selection";

	/**
	 * GUI text
	 */
	public static final String REFRESH = "table.refresh";

	/**
	 * GUI text
	 */
	public static final String PAGEABLE = "table.pageable";

	/**
	 * GUI text
	 */
	public static final String SHOW_HIDE_CONTROLS = "table.show_hide_controls";

	/**
	 * Operation column type
	 */
	public static final String SUM_es_ES = "table.sum";

	/**
	 * Operation column type
	 */
	public static final String AVERAGE_es_ES = "table.average";

	/**
	 * Operation column type
	 */
	public static final String MAXIMUM_es_ES = "table.maximum";

	/**
	 * Operation column type
	 */
	public static final String MINIMUM_es_ES = "table.minimum";

	/**
	 * Operation column type
	 */
	public static final String COUNT_es_ES = "table.count";

	/**
	 * GUI text
	 */
	public static final String COPY_CELL_es_ES = "Copiar Celda";

	/**
	 * GUI text
	 */
	public static final String COPY_SELECTION_es_ES = "Copiar Selección";

	/**
	 * GUI text
	 */
	public static final String PRINTING_SELECTION_es_ES = "Imprimir Selección";

	/**
	 * GUI text
	 */
	public static final String REFRESH_es_ES = "Actualizar tabla";

	/**
	 * GUI text
	 */
	public static final String SHOW_HIDE_CONTROLS_es_ES = "Mostrar/Ocultar controles";

	/**
	 * GUI text
	 */
	protected static final String editKey = "table.edit_cell";

	/**
	 * GUI text
	 */
	protected static final String insertKey = "table.new_register";

	/**
	 * GUI text
	 */
	protected static final String detailKey = "table.details";

	/**
	 * GUI text
	 */
	protected static final String resetOrderKey = "table.reset_order";

	/**
	 * GUI text
	 */
	protected static final String openInNewWindowKey = "table.open_in_new_window";

	/**
	 * GUI text
	 */
	protected String totalText = Table.TOTAL_es_ES;

	/**
	 * GUI text
	 */
	protected String recordsText = Table.RECORDS_es_ES;

	/**
	 * GUI text
	 */
	protected String shownText = Table.IT_IS_SHOWED_es_ES;

	/**
	 * Default renderer for the table
	 */
	protected DateCellRenderer rDate = null;

	/**
	 * Default renderer for the table
	 */
	protected RowHeadCellRenderer rRowHead = null;

	/**
	 * Default renderer for the table
	 */
	protected BooleanCellRenderer rBoolean = null;

	/**
	 * Default renderer for the table
	 */
	protected RealCellRenderer rReal = null;

	/**
	 * Default renderer for the table
	 */
	protected ObjectCellRenderer rObject = null;

	/**
	 * Default renderer for the table
	 */
	protected ImageCellRenderer rImagen = null;

	/**
	 * Default renderer for the table
	 */
	protected HeadCellRenderer rHead = null;

	/**
	 * Default renderer for the table
	 */
	protected MemoCellRenderer rMemo = null;

	/**
	 * The table columns that will use memo render
	 */
	protected List memoRenderColumns = new Vector(2, 2);

	/**
	 * Minimum row height, by default is MIN_ROW_HEIGHT
	 */
	protected int minRowHeight = Table.MIN_ROW_HEIGHT;

	protected boolean fitRowHeight = false;

	public static boolean defaultShowGridValue = true;

	protected boolean showGridValue;

	protected boolean dynamicTable;

	protected boolean blockedCols;

	protected boolean translateHeader;

	protected MouseListener listenerHighlightButtons;

	protected boolean borderbuttons;

	protected boolean opaquebuttons;

	protected Map buttonIcons;

	protected boolean dataBaseInsert;

	protected boolean dataBaseRemove;

	/**
	 * List with the required columns to insert
	 */
	protected List vrequiredCols;

	protected List vupdateEditableColumns;

	/**
	 * boolean to know when the insertion is executing
	 */
	protected boolean inserting;

	/**
	 * List with all the listeners to the insert event
	 */
	protected EventListenerList insertTableInsertRowListenerList;

	protected EventListenerList refreshTableListenerList;

	/**
	 * A property change listener that when the property in the event is named <code>width</code> will
	 * set the preferred row height.
	 *
	 * @see #evaluatePreferredRowsHeight()
	 */
	protected PropertyChangeListener columnWidthListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent e) {
			if ("width".equals(e.getPropertyName())) {
				Table.this.evaluatePreferredRowsHeight();
			}
		}
	};

	private static final String SPACE = " ";

	private Form formView = null;

	private boolean tableView = true;

	private final ImageIcon formViewIcon = null;

	private final ImageIcon tableViewIcon = null;

	/**
	 * The parameters passed to the table when it is builded. These parameters are set in the xml.
	 */
	protected Map parameters = null;

	protected BorderLayout layout = null;

	/**
	 * The JTable inside this table
	 */
	protected JTable table = null;

	protected BlockedTable blockedTable;

	protected JTable sumRowTable;

	protected JTable sumRowBlockedTable;

	protected TableWaitPanel waitPanel;

	protected TableInformationPanel informationPanel;

	/**
	 * Contains the columns and keys names
	 */
	protected List attributes = new Vector(5);

	/**
	 * The detail form name
	 */
	protected String formName = null;

	protected String insertFormName = null;

	protected JScrollPane scrollPane = null;

	protected JScrollPane blockedScrollPane = null;

	protected JScrollPane sumRowScrollPane;

	protected JScrollPane sumRowBlockedScrollPane;

	protected JSplitPane mainSplit;

	protected JPanel mainPanel;

	protected JPanel tablePanel;

	protected JPanel blockedTablePanel;

	/**
	 * The table entity
	 */
	protected String entity = null;

	protected String memoryEntity = null;

	protected List primaryKey = new Vector(2);

	protected String keyField = null;

	protected List keyFields = null;

	protected Map codValues = null;

	/**
	 * @deprecated. Now must use {@link #parentkeys}
	 */
	protected String parentKey = null;

	/**
	 * @deprecated. Now must use {@link #parentkeys}
	 */
	protected List otherParentKeys = new Vector(2);

	/**
	 * List with all the fields in the parent form used to filter the table values
	 */
	protected List parentkeys;

	protected Map hParentkeyEquivalences;

	/**
	 * The List with attributes to update when data field value changed. By default, null.
	 */
	protected List onsetvaluesetAttributes = null;

	/**
	 * This object is used to store onsetvalueset attributes and equivalences (for these fields in
	 * entity) when structure of parameter <code>onsetvalueset</code> is:
	 * "fieldonset1:SUM(fieldentitypk1);fieldonset2:CONCAT(fieldentitypk2);...fieldonsetn:MAX(fieldentitypkn)"
	 */
	protected Map hOnSetValueSetEquivalences = new Hashtable();

	/**
	 * This object is used to store onsetvalueset attributes and function when structure of parameter
	 * <code>onsetvalueset</code> is:
	 * "fieldonset1:SUM(fieldentitypk1);fieldonset2:CONCAT(fieldentitypk2);...fieldonsetn:MAX(fieldentitypkn)"
	 */
	protected Map hOnSetValueSetFunction;

	protected Frame parentFrame = null;

	protected FormBuilder formBuilder = null;

	protected DetailForm detailForm = null;

	protected DetailForm insertDetailForm = null;

	protected Form parentForm = null;

	protected boolean visibleFieldKeyColumn = false;

	protected boolean enabled = true;

	protected boolean enabledDetail = true;

	protected boolean showControls = true;

	protected boolean defaultButtons;

	public boolean controlsVisible = true;

	protected boolean quickFilterVisible = false;

	protected boolean quickFilterLocal = true;

	protected boolean sortReportColumn = true;

	public static boolean defaultQuickFilterLocal = false;

	public static final boolean quickFilterClobExclude = true;

	protected boolean allowSetupVisibleColumns = Table.DEFAULT_VALUE_SETUP_VISIBLE_COLS;

	protected ControlPanel controlsPanel = new ControlPanel();

	public static final String BUTTON_LAYOUT = "grouptablekey;pivottablebutton;reportbutton;defaultchartbutton;excelexportbutton;sumrowbutton;calculedcolsbutton;printingbutton;filtersavebutton;visiblecolsbutton;htmlexportbutton;copybutton;grouptablekey;insertbutton;refreshbutton";

	public static final String CLASSIC_BUTTON_LAYOUT = "grouptablekey;grouptablekey;excelexportbutton;copybutton;htmlexportbutton;printingbutton;insertbutton;filtersavebutton;defaultchartbutton;visiblecolsbutton;reportbutton;sumrowbutton;calculedcolsbutton;pivottablebutton";

	public static String defaultControlButtonLayout = Table.BUTTON_LAYOUT;

	protected String controlButtonLayout;

	protected java.nio.charset.Charset charsetIso88591 = java.nio.charset.Charset.forName("ISO-8859-1");

	protected static final String CONTROL_LAYOUT_KEY = "buttonlayout";

	protected boolean debug = false;

	protected PropertyChangeListener buttonChangeListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (ControlPanel.CHANGE_BUTTON_PROPERTY.equalsIgnoreCase(evt.getPropertyName())) {
				Table.this.saveControlPanelConfiguration();
			}
		}
	};

	protected JLabel lInfoFilter = new JLabel();

	protected QuickFieldText quickFilterText = null;

	public static final String FIND_MESSAGE = "table.findMessage";

	public static final String QUICK_FILTER_PAGEABLE_MESSAGE_KEY = "table.quickfilterPageableMessageKey";

	public static final String QUICK_FILTER_PAGEABLE_LOCAL = "quickfilterlocal";

	public static final String QUICK_FILTER_LABEL_KEY = "table.quickFilterLabelKey";


	/**
	 * Button key
	 */
	public static final String BUTTON_CHANGEVIEW = "changeviewbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_COPY = "copybutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_EXCEL_EXPORT = "excelexportbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_HTML_EXPORT = "htmlexportbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_PLUS = "insertbutton";

	/**
	 *
	 * Button key
	 */
	public static final String GROUP_TABLE_KEY = "grouptablekey";

	/**
	 * Button key
	 */
	public static final String BUTTON_PRINTING = "printingbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_DELETE = Table.DELETE_BUTTON;

	/**
	 * Button key
	 */
	public static final String BUTTON_REFRESH = Table.REFRESH_BUTTON;

	/**
	 * Button key
	 */
	public static final String BUTTON_CHART = "chartbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_DEFAULT_CHART = "defaultchartbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_VISIBLE_COLS_SETUP = "visiblecolsbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_SUM_ROW_SETUP = "sumrowbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_REPORT = "reportbutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_SAVE_FILTER_ORDER_SETUP = "filtersavebutton";

	/**
	 * Button key
	 */
	public static final String BUTTON_SAVE_FILTER_PIVOT_DYNAMIC_ORDER_SETUP = "filterDynamicPivotSaveButton";

	/**
	 * Button key
	 */
	public static final String BUTTON_CALCULATED_COL = "calculedcolsbutton";

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonChangeView = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonCopy = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonExcelExport = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonHTMLExport = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonPlus = new TableButton();

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonPlus2 = new TableButton();

	/**
	 * Group Table button. It is placed in the control panel
	 *
	 */
	protected GroupTableButton groupTableButton;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonPrint = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonDelete = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonRefresh = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonChart = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected TableButtonSelection buttonDefaultChart = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonVisibleColsSetup = null;

	/**
	 * Table button. It is placed in the control panel.
	 */
	protected JButton buttonSumRowSetup = null;

	protected TableButtonSelection buttonReports = null;

	protected JPopupMenu menuReportSetup = null;

	protected JButton buttonSaveFilterOrderSetup = null;

	protected JButton buttonCalculatedColumns = null;

	public static boolean createCalculatedButton = true;

	protected List addButtons = new Vector(2, 2);

	protected List addComponents = new Vector(2, 2);

	protected PrintingWindow printingWindow = null;

	protected Locale locale = Locale.getDefault();

	protected int fontSize = 1;

	protected boolean orderEnabled = true;

	protected boolean modified = false;

	protected ResourceBundle resourcesFile = null;

	protected Map<String, String> hAttributesToFixEquivalences;

	protected List<String> attributesToFix;

	protected List currencyColumns = new Vector(0, 1);

	protected List hourRenderColumns = new Vector(0, 1);

	protected List editableColumnsUpdateEntity = new Vector(0, 2);

	protected List editableColumns = new Vector(0, 2);

	protected JMenuItem filter = new JMenuItem("Filtrar");

	protected boolean activatedFilter = true;

	protected boolean setPivotTableButton = true;

	protected boolean allowDelete = false;

	protected boolean setRefreshButton = false;

	protected boolean installQuickFilter = false;

	protected boolean quickfilterOnFocusSelectAll = false;

	protected boolean packTable = false;

	protected ExtendedJPopupMenu menu = null;

	protected ExtendedJPopupMenu chartMenu = null;

	protected List menuChartItems = new Vector(0);

	protected List chartGraphMenuItems = new Vector(0);

	protected IChartUtilities chartUtilities;

	protected boolean defaultChartsEnabled = true;

	protected JMenuItem menuDetail = null;

	protected JMenuItem menuInsert = null;

	protected JMenuItem menuPrintSelection = null;

	protected JMenuItem menuCopyCell = null;

	protected JMenuItem menuCopySelection = null;

	protected JMenuItem menuRefresh = null;

	protected JMenuItem menuResetOrder = null;

	protected JMenuItem menuOpenInNewWindow = null;

	protected JMenuItem menuFilter = null;

	protected JMenuItem menuFilterByValue = null;

	protected JMenuItem menuDeleteColumnFilter = null;

	protected JMenuItem menuDeleteFilter = null;

	protected JMenuItem menuGroup = null;

	protected JMenu menuGroupDate = null;

	protected JMenuItem menuGroupByYear = null;

	protected JMenuItem menuGroupByYearMonth = null;

	protected JMenuItem menuGroupYearMonthDay = null;

	protected JMenuItem menuGroupByQuarterYear = null;

	protected JMenuItem menuGroupByWeekYear = null;

	protected JMenuItem menuGroupByQuarter = null;

	protected JMenuItem menuGroupByMonth = null;

	protected JMenu menuGroupFunction = null;

	protected JMenuItem menuSumFunction = null;

	protected JMenuItem menuAvgFunction = null;

	protected JMenuItem menuMaxFunction = null;

	protected JMenuItem menuMinFunction = null;

	protected JMenuItem menuCountFunction = null;

	protected JMenuItem menuDeleteGroup = null;

	protected JMenuItem menuPageableEnabled = null;

	protected FilterDialog filterWindow = null;

	protected JCheckBoxMenuItem menuShowHideControls = null;

	protected boolean operationInMemory = false;

	protected boolean insertMode = false;

	protected boolean scrollHorizontal = false;

	protected Object defaultFilter = null;

	protected boolean allowOpenInNewWindow = Table.SHOW_OPEN_IN_NEW_WINDOW_MENU;

	protected boolean openInNewWindowByDefault = false;

	protected Map<String, Integer> hColumnSQLTypes = new Hashtable<String, Integer>();

	protected boolean allColumnTypes = false;

	protected boolean dynamicPivotable = false;

	public boolean isDynamicPivotable() {
		return this.dynamicPivotable;
	}

	public void setDynamicPivotable(final boolean dynamicPivotPref) {
		this.dynamicPivotable = dynamicPivotPref;
	}

	/**
	 * Filter operation key
	 */
	public static final int LESS = TableSorter.Filter.LESS;

	/**
	 * Filter operation key
	 */
	public static final int LESS_EQUAL = TableSorter.Filter.LESS_EQUAL;

	/**
	 * Filter operation key
	 */
	public static final int EQUAL = TableSorter.Filter.EQUAL;

	/**
	 * Filter operation key
	 */
	public static final int GREATER_EQUAL = TableSorter.Filter.GREATER_EQUAL;

	/**
	 * Filter operation key
	 */
	public static final int GREATER = TableSorter.Filter.GREATER;

	/**
	 * Filter operation key
	 */
	public static final int RANGE = TableSorter.Filter.RANGE;

	public static final boolean DEFAULT_RENDER_REPORT_VALUES = true;

	public static boolean renderReportValues = Table.DEFAULT_RENDER_REPORT_VALUES;

	protected int rowPress = -1;

	protected int colPress = -1;

	protected MouseEvent eventPress = null;

	protected DetailFormBuilder detailFormBuilder;

	protected InsertDetailFormBuilder insertDetailFormBuilder;

	// public static boolean defaultBackgroundDetailFormBuilder = false;

	public boolean backgroundDetailFormBuilder;

	/**
	 * Defines a listener that in the event restored the sorting applied to the table.
	 */
	protected ActionListener reserOrderListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			Table.this.resetOrder();
		}
	};

	/**
	 * Defines a listener that opens the table detail form in inserting mode, in order to insert new
	 * records to the table.
	 */
	protected ActionListener addRecordListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if ((Table.this.formName != null)
					|| ((Table.this.formName == null) && (Table.this.insertFormName != null))) {
				// Open the detail form
				final Cursor c = Table.this.getCursor();
				try {
					Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Table.this.openInsertDetailForm();
				} catch (final Exception ex) {
					Table.logger.error("RecordListener error", ex);
				} finally {
					Table.this.setCursor(c);
				}
			}
		}
	};

	protected void changeOpacity(final JComponent c, final boolean opaque) {
		c.setOpaque(opaque);
		final Component[] components = c.getComponents();
		for (int i = 0; i < components.length; i++) {
			if (components[i] instanceof JComponent) {
				this.changeOpacity((JComponent) components[i], opaque);
			}
		}
	}

	protected List visibleColumns = new Vector(0, 3);

	protected List<String> quickFilterColumns;

	protected List defaultVisibleColumns;

	protected List originalVisibleColumns = new Vector(0, 3);

	protected List reportCols = new Vector();

	protected int[] columnWidthSet = new int[0];

	protected int preferredSizeInRows = 10;

	protected Map calculedColumns = new Hashtable(0);

	protected List originalCalculatedColumns;

	public static boolean defaultModifiableCalculatedColumns = true;

	protected boolean modifiableCalculatedColumns = true;

	protected List requiredColumnsCalculedColumns = new Vector();

	protected List columnsToOperate = null;

	protected List columnsToSum = null;

	// protected List sumCellRenderers = new Vector(2);
	protected CurrencyCellRenderer currencyRenderer = null;

	protected DateCellRenderer hourRenderer = null;

	protected boolean visibleRowNumberColumn = true;

	protected boolean autoFixHead = Table.DEFAULT_VALUE_SET_HEIGHT_HEAD;

	/**
	 * @deprecated
	 */
	@Deprecated
	protected TableAttribute attributeTable = null;

	protected int rowsNumberToQuery = -1;

	protected boolean queryRowsModifiable;

	protected PageFetcher pageFetcher = null;

	protected String detailFormTitleKey = null;

	// protected JPanel queryPanel = new JPanel(new
	// FlowLayout(FlowLayout.RIGHT));

	protected String dinamicFormClass = null;

	protected DynamicFormManager dynamicFormManager = null;

	protected RefreshThread refreshThread = null;

	protected Table tAux = null;

	protected FormPermission visiblePermission = null;

	protected FormPermission enabledPermission = null;

	protected FormPermission insertPermission = null;

	protected FormPermission queryPermission = null;

	protected ComponentsPermissionsStore tableComponentPermission = new ComponentsPermissionsStore();

	/**
	 * Object to store permission info about column visibility.
	 *
	 * @since 5.2077EN-0.2
	 */
	protected ColumnPermissionStore tableColumnPermission = new ColumnPermissionStore();

	protected boolean tableOpaque = false;

	protected Color tableBackgroundColor;

	public static boolean defaultTableOpaque = false;

	public static Color defaultTableBackgroundColor;

	public static int defaultDetailTitleMaxSize = -1;

	protected int detailTitleMaxSize = -1;

	/**
	 * Check if exists Shared Remote Reference to share preferences
	 */

	protected boolean shareRemoteReferenceFilters = false;

	/**
	 * Class that stores all the permissions for all the table components
	 */
	protected static class ComponentsPermissionsStore {

		String[] type = new String[0];

		String[] key = new String[0];

		TableFormPermission[][] store = new TableFormPermission[0][0];

		/**
		 * Returns the permission form the component key corresponding to the permission determined by type.
		 * @param key the component
		 * @param type the permission
		 * @return the corresponding permission or null if the permission is not set
		 */
		public TableFormPermission get(final String key, final String type) {
			final int t = this.getTypeId(type);
			if (t == -1) {
				return null;
			}
			final int k = this.getIdKey(key);
			if (k == -1) {
				return null;
			}
			return this.store[t][k];
		}

		/**
		 * Returns the index corresponding to the key passed as parameter. In case that the key is not set,
		 * the method will return null.
		 * @param key a component key
		 * @return
		 */
		public int getIdKey(final String key) {
			for (int i = 0; i < this.key.length; i++) {
				if (this.key[i].equals(key)) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Returns the index corresponding to the different permissions set for this table, for example
		 * "visible" or "enabled"
		 * @param type the permission type
		 * @return the index in case the permission exists or null in other case
		 */
		public int getTypeId(final String type) {
			for (int i = 0; i < this.type.length; i++) {
				if (this.type[i].equals(type)) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Adds a new type to the types collection, and returns the index set to the new type.
		 * @param type the type to add
		 * @return the index set
		 */
		public int addType(final String type) {
			final TableFormPermission[][] temp = new TableFormPermission[this.store.length + 1][this.key.length];

			for (int i = 0; i < this.store.length; i++) {
				System.arraycopy(this.store[i], 0, temp[i], 0, this.store[i].length);
			}
			this.store = temp;

			final String[] te = new String[this.type.length + 1];
			System.arraycopy(this.type, 0, te, 0, this.type.length);
			te[this.type.length] = type;

			this.type = te;
			return this.type.length - 1;
		}

		/**
		 * Adds a new component key to the components key collection, and returns the index set.
		 * @param key new component key to add
		 * @return the index set
		 */
		public int addKey(final String key) {
			final TableFormPermission[][] temp = new TableFormPermission[this.store.length][this.key.length + 1];

			for (int i = 0; i < this.store.length; i++) {
				System.arraycopy(this.store[i], 0, temp[i], 0, this.store[i].length);
			}
			this.store = temp;

			final String[] te = new String[this.key.length + 1];
			System.arraycopy(this.key, 0, te, 0, this.key.length);
			te[this.key.length] = key;

			this.key = te;
			return this.key.length - 1;
		}

		/**
		 * Adds a new Permission to the corresponding pair component-permission type.
		 * @param key
		 * @param type
		 * @param tableFormPermission
		 */
		public void addTableFormPermission(final String key, final String type, final TableFormPermission tableFormPermission) {
			try {
				int t = this.getTypeId(type);
				if (t == -1) {
					t = this.addType(type);
				}
				int k = this.getIdKey(key);
				if (k == -1) {
					k = this.addKey(key);
				}
				this.store[t][k] = tableFormPermission;
			} catch (final Exception e) {
				Table.logger.error(null, e);
			}
		}

	}

	/**
	 * Class that stores all the permissions for all the table components
	 */
	protected static class ColumnPermissionStore {

		String[] type = new String[0];

		String[] columnName = new String[0];

		TableFormPermission[][] store = new TableFormPermission[0][0];

		/**
		 * Returns the permission form the component key corresponding to the permission determined by type.
		 * @param key the component
		 * @param type the permission
		 * @return the corresponding permission or null if the permission is not set
		 */
		public TableFormPermission get(final String columnName, final String type) {
			final int t = this.getTypeId(type);
			if (t == -1) {
				return null;
			}
			final int k = this.getColumnNameId(columnName);
			if (k == -1) {
				return null;
			}
			return this.store[t][k];
		}

		/**
		 * Returns the index corresponding to the key passed as parameter. In case that the key is not set,
		 * the method will return null.
		 * @param key a component key
		 * @return
		 */
		public int getColumnNameId(final String columnName) {
			for (int i = 0; i < this.columnName.length; i++) {
				if (this.columnName[i].equals(columnName)) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Returns the index corresponding to the different permissions set for this table, for example
		 * "visible" or "enabled"
		 * @param type the permission type
		 * @return the index in case the permission exists or null in other case
		 */
		public int getTypeId(final String type) {
			for (int i = 0; i < this.type.length; i++) {
				if (this.type[i].equals(type)) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Adds a new type to the types collection, and returns the index set to the new type.
		 * @param type the type to add
		 * @return the index set
		 */
		public int addType(final String type) {
			final TableFormPermission[][] temp = new TableFormPermission[this.store.length + 1][this.columnName.length];

			for (int i = 0; i < this.store.length; i++) {
				System.arraycopy(this.store[i], 0, temp[i], 0, this.store[i].length);
			}
			this.store = temp;

			final String[] te = new String[this.type.length + 1];
			System.arraycopy(this.type, 0, te, 0, this.type.length);
			te[this.type.length] = type;

			this.type = te;
			return this.type.length - 1;
		}

		/**
		 * Adds a new component key to the components key collection, and returns the index set.
		 * @param key new component key to add
		 * @return the index set
		 */
		public int addColumnName(final String columnName) {
			final TableFormPermission[][] temp = new TableFormPermission[this.store.length][this.columnName.length + 1];

			for (int i = 0; i < this.store.length; i++) {
				System.arraycopy(this.store[i], 0, temp[i], 0, this.store[i].length);
			}
			this.store = temp;

			final String[] te = new String[this.columnName.length + 1];
			System.arraycopy(this.columnName, 0, te, 0, this.columnName.length);
			te[this.columnName.length] = columnName;

			this.columnName = te;
			return this.columnName.length - 1;
		}

		/**
		 * Adds a new Permission to the corresponding pair component-permission type.
		 * @param key
		 * @param type
		 * @param tableFormPermission
		 */
		public void addTableFormPermission(final String columnName, final String type, final TableFormPermission tableFormPermission) {
			try {
				int t = this.getTypeId(type);
				if (t == -1) {
					t = this.addType(type);
				}
				int k = this.getColumnNameId(columnName);
				if (k == -1) {
					k = this.addColumnName(columnName);
				}
				this.store[t][k] = tableFormPermission;
			} catch (final Exception e) {
				Table.logger.error("TableFormPermission ", e);
			}
		}

	}

	/**
	 * Determines whether the insertion using the table is enabled or not.
	 */
	protected boolean disableInsert = false;

	protected InfoFilterButtonsModelListener actButtonsModelListener = new InfoFilterButtonsModelListener();

	/**
	 * Listener that manager the control buttons visibility. These controls visibility will be activate
	 * when the table is enable and has records in it.
	 */
	protected class InfoFilterButtonsModelListener implements TableModelListener {

		@Override
		public void tableChanged(final TableModelEvent e) {
			if ((Table.this.table != null) && !Table.this.table.isEnabled()) {
				Table.this.setControlButtonsEnabled(false);
				return;
			}
			final int nRows = ((TableModel) e.getSource()).getRowCount();
			if (nRows > 0) {
				Table.this.setControlButtonsEnabled(true);
			} else {
				Table.this.setControlButtonsEnabled(false);
			}
			Table.this.updateFilterInfo();
		}

	}

	@Override
	public void addRefreshTableListener(final RefreshTableListener l) {
		this.refreshTableListenerList.add(RefreshTableListener.class, l);
	}

	@Override
	public void removeRefreshTableListener(final RefreshTableListener l) {
		this.refreshTableListenerList.remove(RefreshTableListener.class, l);
	}

	@Override
	public void fireRefreshTableEvent(final RefreshTableEvent refreshTableEvent) {
		final Object aobj[] = this.refreshTableListenerList.getListenerList();
		for (int i = aobj.length - 2; i >= 0; i -= 2) {
			if (aobj[i] == RefreshTableListener.class) {
				if (RefreshTableEvent.OK == refreshTableEvent.getType()) {
					((RefreshTableListener) aobj[i + 1]).postCorrectRefresh(refreshTableEvent);
				} else {
					((RefreshTableListener) aobj[i + 1]).postIncorrectRefresh(refreshTableEvent);
				}
			}
		}
	}

	/**
	 * The thread that refreshes the table.
	 */
	public static class RefreshThread extends Thread {

		protected int delay = 0;

		protected Table table = null;

		protected boolean stop = false;

		protected int refreshState = RefreshTableEvent.OK;

		/**
		 * Creates the thread for the corresponding table.
		 * @param queryTable the table to be managed
		 */
		public RefreshThread(final Table queryTable) {
			super("RefreshThread: " + queryTable.getEntityName());
			this.table = queryTable;
		}

		public synchronized void setDelay(final int delay) {
			this.delay = delay;
		}

		public void stopThread() {
			this.stop = true;
			this.table.hideWaitPanel();
		}

		public boolean isStop() {
			return stop;
		}

		public void setStop(final boolean stop) {
			this.stop = stop;
		}

		/**
		 * Executes the table query
		 */
		@Override
		public void run() {
			try {
				if (this.delay > 0) {
					Thread.sleep(this.delay);
				}

				try {

					if (this.stop) {
						this.refreshState = RefreshTableEvent.CANCEL;
						return;
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (!RefreshThread.this.stop) {
								RefreshThread.this.table.showWaitPanel();
								RefreshThread.this.table.repaint();
							}
						}
					});

					final EntityReferenceLocator referenceLocator = this.table.parentForm.getFormManager()
							.getReferenceLocator();
					if (this.stop) {
						this.refreshState = RefreshTableEvent.CANCEL;
						return;
					}
					final Entity ent = referenceLocator.getEntityReference(this.table.getEntityName());
					if (this.stop) {
						this.refreshState = RefreshTableEvent.CANCEL;
						return;
					}
					if (ent == null) {
						Table.logger.info(
								"REFRESH ERROR: Table: The refresh operation can't be executed because the entity can't be found. Entity:{}",
								this.table.getEntityName());
						return;
					}

					Map<Object, Object> kv = new Hashtable();
					synchronized (this.table) {
						kv = this.table.getParentKeyValues();
						if (this.table.getPageFetcher() != null) {
							final Expression exp = this.table.getPageFetcher().getFilterExpression();
							if (exp != null) {
								kv.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.FILTER_KEY, exp);
							}
						}
					}

					if (this.stop) {
						this.refreshState = RefreshTableEvent.CANCEL;
						return;
					}

					EntityResult res = null;

					if ((this.table.pageFetcher != null) && this.table.pageFetcher.isPageableEnabled()) {

						res = ((AdvancedEntity) ent).query(kv, this.table.attributes, referenceLocator.getSessionId(),
								this.table.pageFetcher.getPageSize(),
								this.table.pageFetcher.getOffset(), this.table.getSQLOrderList());
					} else {
						res = ent.query(kv, this.table.attributes, referenceLocator.getSessionId());
					}

					if (this.stop) {
						this.refreshState = RefreshTableEvent.CANCEL;
						return;
					}

					if (res.getCode() == EntityResult.OPERATION_WRONG) {
						Table.logger.error("REFRESH ERROR- EntityResult : {}", res.getMessage());
						this.refreshState = RefreshTableEvent.ERROR;
					} else {
						if (this.stop) {
							this.refreshState = RefreshTableEvent.CANCEL;
							return;
						}
						synchronized (this.table) {
							// Check the net
							final int threshold = ConnectionManager.getCompresionThreshold(res.getBytesNumber(),
									res.getStreamTime());
							if (threshold > 0) {
								final ConnectionOptimizer opt = ConnectionManager.getConnectionOptimizer();
								if ((opt != null) && (this.table.locator instanceof ClientReferenceLocator)) {
									try {
										opt.setDataCompressionThreshold(
												((ClientReferenceLocator) this.table.locator).getUser(),
												this.table.locator.getSessionId(), threshold);
										Table.logger.info("Table: Compression threshold established to {} {} in: {}",
												((ClientReferenceLocator) this.table.locator).getUser(),
												this.table.locator.getSessionId(), threshold);
									} catch (final Exception e) {
										Table.logger.error("Table: Error establishing compression threshold", e);
									}
								}
							}
							final EntityResult res2 = res;
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									RefreshThread.this.table.setInnerValue(res2);
								}
							});
						}
					}

				} catch (final Exception e) {
					Table.logger.error(null, e);
					this.refreshState = RefreshTableEvent.ERROR;
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							RefreshThread.this.table.showInformationPanel(ParseUtils.throwableToString(e, 10));
						}
					});
				} finally {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							if (!RefreshThread.this.stop) {
								RefreshThread.this.table.hideWaitPanel();
							}
							RefreshThread.this.table.fireRefreshTableEvent(
									new RefreshTableEvent(RefreshThread.this.table, RefreshThread.this.refreshState));
						}
					});
				}
			} catch (final Exception e) {
				Table.logger.trace(null, e);
			}

		}

	}

	/**
	 * The tip configured to the table scroll.
	 */
	protected TipScroll tipScroll = null;

	/**
	 * Determines whether the tip scroll is enabled or not.
	 */
	protected boolean tipScrollEnabled = true;

	protected SumRowSetupDialog sumRowSetupDialog = null;

	protected VisibleColsSetupDialog visibleColsSetupDialog = null;

	/**
	 * Opens the popup menu (context menu)
	 *
	 * @see #showPopupMenu
	 */
	protected MouseAdapter popupListener = new MouseAdapter() {

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				// if (e.getModifiers() == InputEvent.META_MASK) {
				e.consume();
				Table.this.eventPress = e;
				final Point point = e.getPoint();
				final Point newPoint = SwingUtilities.convertPoint((Component) e.getSource(), point, Table.this);
				Table.this.showPopupMenu(newPoint.x, newPoint.y);
			}
		}
	};

	/**
	 * The EJTable ListSelectionListener. It is notified each time a change to the selection occurs.
	 */
	protected ListSelectionListener selectionListener = new ListSelectionListener() {

		@Override
		public void valueChanged(final ListSelectionEvent e) {
			if (Table.this.buttonDelete != null) {
				if (Table.this.getSelectedRowsNumber() > 0) {
					Table.this.setTableComponentEnabled(Table.BUTTON_DELETE, true);
				} else {
					Table.this.setTableComponentEnabled(Table.BUTTON_DELETE, false);
				}
			}
			if (Table.this.hasSumRow()) {
				Table.this.repaint();
			}
		}
	};

	public void addListSelectionListener(final ListSelectionListener listSelectionListener) {
		if ((this.table != null) && (this.table.getSelectionModel() != null)) {
			this.table.getSelectionModel().addListSelectionListener(listSelectionListener);
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent listEvent) {
		if (this.table != null) {
			this.table.valueChanged(listEvent);
		}
	}

	/**
	 * Class that determines a filter in order to retrieve only txt files.
	 */
	protected static class TxtFileFilter extends javax.swing.filechooser.FileFilter {

		protected static final String TEXT_FILES = "table.text_file";

		public TxtFileFilter() {
		}

		@Override
		public String getDescription() {
			return ApplicationManager.getTranslation(TxtFileFilter.TEXT_FILES) + " (*.txt)";
		}

		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				final int dotIndex = file.getPath().lastIndexOf(".");
				if (file.getPath().substring(dotIndex + 1).equalsIgnoreCase("txt")) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

	/**
	 * Class that determines a filter in order to retrieve only html/htm files.
	 */
	protected class HTMLFileFilter extends javax.swing.filechooser.FileFilter {

		public HTMLFileFilter() {
		}

		@Override
		public String getDescription() {
			return ApplicationManager.getTranslation("M_HTML_FILE_FILTER_DESCRIPTION");
		}

		@Override
		public boolean accept(final File file) {
			if (file.isDirectory()) {
				return true;
			} else {
				final int dotIndex = file.getPath().lastIndexOf(".");
				if (file.getPath().substring(dotIndex + 1).equalsIgnoreCase("htm")
						|| file.getPath().substring(dotIndex + 1).equalsIgnoreCase("html")) {
					return true;
				} else {
					return false;
				}
			}
		}

	}

	/**
	 * Constructs a <code>Table</code> with the default configuration. This method creates the layout
	 * and calls the methods that configures the table. The table is a component to represent database
	 * information in a grid, with several functionalities such as sorting, filtering, visible column
	 * configuration, data export, etc.
	 * @param params
	 * @throws Exception
	 * @see #init
	 * @see #createTable
	 * @see #setRenderers
	 * @see #setScrollHorizontal
	 * @see #setVisibleColumns
	 * @see #createPopupMenu
	 * @see #installScrollListener
	 * @see #configureQueryPanel
	 * @see #setButtonsTips
	 */
	public Table(final Map params) throws Exception {
		super();
		Table.initRendererMap();
		this.parameters = ObjectTools.clone(params);

		this.setOpaque(false);
		this.mainPanel = new JPanel();
		this.tablePanel = new JPanel(new BorderLayout());
		this.blockedTablePanel = new JPanel(new BorderLayout());

		// since 5.10.0
		this.mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		this.mainSplit.setDividerSize(0);
		this.mainSplit.setContinuousLayout(true);
		this.mainSplit.setDividerLocation(0);

		this.layout = new BorderLayout();
		this.mainPanel.setLayout(this.layout);
		this.mainPanel.setOpaque(false);
		this.mainPanel.add(this.mainSplit);

		this.setContentPane(this.mainPanel);

		this.mainSplit.setLeftComponent(this.blockedTablePanel);
		this.mainSplit.setRightComponent(this.tablePanel);

		if ((this.buttonPlus2 != null) && (this.buttonPlus2 instanceof TableComponent)) {
			((TableComponent) this.buttonPlus2).setKey(Table.BUTTON_PLUS);
		}

		this.init(params);
		this.table = this.createTable();
		this.blockedTable = this.createBlockedTable();
		this.sumRowTable = this.createSumRowTable();
		this.sumRowBlockedTable = this.createSumRowBlockedTable();

		this.waitPanel = this.createWaitPanel();
		this.informationPanel = this.createInformationPanel();
		this.setGlassPane(this.waitPanel);
		this.getGlassPane().setVisible(false);

		this.setEditableColumns();
		this.setRenderers();
		this.setEditors();

		final FontMetrics fontMetrics = this.getFontMetrics(this.getFont());

		this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(),
				this.preferredSizeInRows * fontMetrics.getHeight()));

		// since 5.2080EN
		this.createJScrollPane(params);
		this.sumRowScrollPane = this.createSumRowJScrollPane(params);

		this.createBlockedScrollPane(params);
		this.sumRowBlockedScrollPane = this.createSumRowBlockedScrollPane(params);
		this.fixBlockedVisibility();

		this.setHorizontalScroll(this.scrollHorizontal);

		this.tablePanel.add(this.scrollPane);
		this.tablePanel.add(this.sumRowScrollPane, BorderLayout.SOUTH);

		this.blockedTablePanel.add(this.blockedScrollPane);
		this.blockedTablePanel.add(this.sumRowBlockedScrollPane, BorderLayout.SOUTH);

		// Header tip
		this.table.getTableHeader().setToolTipText(Table.HEAD_TIP_COD);

		this.tablePanel.setBorder(ParseUtils.getBorder((String) params.get("border"),
				BorderManager.getBorder(BorderManager.DEFAULT_TABLE_BORDER_KEY)));

		this.setVisibleColumns();
		this.setRowNumberColumnVisible(this.visibleRowNumberColumn);
		this.installButtonsListener();

		if (this.rowsNumberToQuery >= 0) {
			this.configurePageable(this.rowsNumberToQuery);
		}

		// Configure buttons must be after installButtonsListener because
		// installbuttonslistener creates some buttons
		// Must be after configurePageable too.
		this.configureButtons(this.parameters);
		this.configureQuickFilter(this.parameters);

		this.installDetailFormListener();
		this.createPopupMenu();
		this.installScrollListener();

		// No Editable
		this.setEditable(false);
		this.setButtonTips();

		this.setTableComponentEnabled(Table.BUTTON_CHANGEVIEW, false);
		this.setTableComponentEnabled(Table.BUTTON_COPY, false);
		this.setTableComponentEnabled(Table.BUTTON_EXCEL_EXPORT, false);
		this.setTableComponentEnabled(Table.BUTTON_HTML_EXPORT, false);
		this.setTableComponentEnabled(Table.BUTTON_PRINTING, false);
		this.setTableComponentEnabled(Table.BUTTON_SUM_ROW_SETUP, false);

		if (this.buttonPlus2 != null) {
			this.setTableComponentEnabled(Table.BUTTON_PLUS, this.buttonPlus.isEnabled());
		}

		this.getJTable().setShowGrid(this.showGridValue);

		if (!this.isOpaque()) {
			this.changeOpacity(this, false);
			if (this.tableOpaque) {
				this.scrollPane.setOpaque(true);
				this.table.setOpaque(true);
				this.blockedScrollPane.setOpaque(true);
				this.blockedTable.setOpaque(true);
			}
		}

		if (this.tableBackgroundColor != null) {
			this.scrollPane.setBackground(this.tableBackgroundColor);
			this.blockedScrollPane.setBackground(this.tableBackgroundColor);
		}

		if (params.containsKey(Table.SELECTIONMODE)) {
			if (Table.SINGLE.equals(params.get(Table.SELECTIONMODE))) {
				this.table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			} else if (Table.CONTINUOUS.equals(params.get(Table.SELECTIONMODE))) {
				this.table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			}
		}

		if (this.isInsertingEnabled()) {
			this.registerKeyListeners();
		}

		this.initRendererEditors();

		if ((this.onsetvaluesetAttributes != null) && !this.onsetvaluesetAttributes.isEmpty()) {
			this.getJTable().getModel().addTableModelListener(new TableModelListener() {

				@Override
				public void tableChanged(final TableModelEvent e) {
					if (e.getSource() instanceof ExtendedTableModel) {
						final Map data = Table.this.retrieveOnSetValueData();
						Table.this.updateOnSetValueSetAttributes(data);
					}
				}
			});
		}

		final EntityReferenceLocator loc = ApplicationManager.getApplication().getReferenceLocator();
		try {
			this.shareRemoteReferenceFilters = (IShareRemoteReference) ((UtilReferenceLocator) loc).getRemoteReference(
					IShareRemoteReference.REMOTE_NAME,
					loc.getSessionId()) != null ? true : false;
		} catch (final Exception e) {
			Table.logger.trace(null, e);
			this.shareRemoteReferenceFilters = false;
		}
	}

	protected void initRendererEditors() {
		if (Table.rendererEditorConfigurationFile != null) {
			final TableConfigurationManager tableConfigurationManager = TableConfigurationManager
					.getTableConfigurationManager(Table.rendererEditorConfigurationFile, true);
			if (tableConfigurationManager != null) {
				// Renderer configuration
				final String rendererConfig = (String) this.parameters.get("renderers");
				if (rendererConfig != null) {
					final List allTokens = ApplicationManager.getTokensAt(rendererConfig, ";");
					for (int i = 0; i < allTokens.size(); i++) {
						final String tokenI = (String) allTokens.get(i);
						final List elementI = ApplicationManager.getTokensAt(tokenI, ":");
						if (elementI.size() == 2) {
							final String columnName = (String) elementI.get(0);
							final String elementId = (String) elementI.get(1);
							final TableCellRenderer cellRenderer = tableConfigurationManager.getCellRenderer(elementId);
							if (cellRenderer != null) {
								this.setRendererForColumn(columnName, cellRenderer);
							}
						} else {
							Table.logger.debug(this.getClass().getName() + " : error configuring renderer " + tokenI);
						}

					}
				}

				final String editorConfig = (String) this.parameters.get("editors");
				if (editorConfig != null) {
					final List allTokens = ApplicationManager.getTokensAt(editorConfig, ";");
					for (int i = 0; i < allTokens.size(); i++) {
						final String tokenI = (String) allTokens.get(i);
						final List elementI = ApplicationManager.getTokensAt(tokenI, ":");
						if (elementI.size() == 2) {
							final String columnName = (String) elementI.get(0);
							final String elementId = (String) elementI.get(1);
							final TableCellEditor cellEditor = tableConfigurationManager.getCellEditor(elementId, columnName);
							if (cellEditor != null) {
								this.setColumnEditor(columnName, cellEditor);
							}
						} else {
							Table.logger.debug(this.getClass().getName() + " : error configuring editor " + tokenI);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the table constrains.
	 */
	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		if (parentLayout instanceof GridBagLayout) {
			return new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(5, 5, 5, 5), 0, 0);
		} else {
			return null;
		}
	}

	/**
	 * Method that configures the component. The param <code>Hashtable</code> contains the values set in
	 * the XML in which the <code>Table</code> is placed.</br>
	 * </br>
	 * More parameters in: <br>
	 * -{@link #configureButtons(Map params)} where all table button icons are configured. </br>
	 * -{@link SortTableCellRenderer#init(Map)} where are additional configurations for table
	 * header.<br>
	 * <br>
	 * -{@link #configureQuickFilter(Map)} where it is possible to manage additional graphical
	 * configurations for quickfilter box.
	 * <p>
	 * The attributes allowed are:
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</b></td>
	 * <td><b>values</b></td>
	 * <td><b>default</b></td>
	 * <td><b>required</b></td>
	 * <td><b>meaning</b></td>
	 * </tr>
	 * <tr>
	 * <td>autoadjustheader</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Allows the TableHeader to adjusts its height to the FontText in order to see the the text
	 * when the font size changes.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>calculedcols</td>
	 * <td><I>colname1;exp1;.....;colnameN;expN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>The value is a string with the name of the new column and the math expression that will be
	 * used in each column. With this, the Table will have new columns calculated on the client side
	 * according to the expression. For example <BR>
	 * <i>calculedcols=total;number*units <i><br>
	 * This will create a new column named total which value will be the multiplication of number and
	 * units. This columns must be too in visible cols if you want to show them in the table but you
	 * don't have to add this names in cols</td>
	 * </tr>
	 * <tr>
	 * <td>calculedcolsrequiredfields</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>Required columns in the table for the calculed colums. When these required columns has a null
	 * value, the calculed colum involving it will show a null value as well.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>cods</td>
	 * <td><I>codtable1;coddetail1;codtable2;coddetail2;...;codtablen;coddetailn</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>Establishes a correspondence between a table column and a field in detail form.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>cols</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The names of the columns that this table will query and handle.</td>
	 * </tr>
	 * <tr>
	 * <td>confvisiblecols</td>
	 * <td><I>yes/no</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>Indicates whether user will be able to configure visible columns.</td>
	 * </tr>
	 * <tr>
	 * <td>controls</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Allows the Table to have some control buttons, in the top of it. This buttons, by default,
	 * can export to excel the data in the table, show charts, reports, and so on. If the value is not,
	 * the controlsvisible attribute will not affect.</td>
	 * </tr>
	 * <tr>
	 * <td>controlsvisible</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Shows the table controls. See the <code>controls</code> attribute.</td>
	 * </tr>
	 * <tr>
	 * <td>currency</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>This attribute specifies the columns that will be renderer as a currency, using a renderer to
	 * do that. See {@link com.ontimize.gui.table#CurrencyCellRenderer CurrencyCellRenderer}.</td>
	 * </tr>
	 * <tr>
	 * <td>customcharts</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Activates a Table default functionality tha can represent the Table information using
	 * graphical charts.</td>
	 * </tr>
	 * <tr>
	 * <td>defaultnewwindow</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>If the table has a detail form set, with this then in which the window will be open can be
	 * controled. There are two ways of opening a new window: as a dialog that blocks the applicacion in
	 * backgroung (default), or as a window itself. With this the default behaviour can be change.<br>
	 * Pressing the 'Shift' while opening the window the non-default behaviour will be chosen.<br>
	 * If the attribute <code>opennewwindow</code> is set, <defaultnewwindow> will not be applied.</td>
	 * </tr>
	 * <tr>
	 * <td>deletebutton</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Activates the functionallity of deleting records directly from the <code>Table</code>,
	 * instead using the detail form to do that.</td>
	 * </tr>
	 * <tr>
	 * <td>disableinsert</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Activates the functionallity of inserting records directly from the <code>Table</code> using
	 * the detail form to do that. Is is set to <code>yes</code> no records can be inserted using this
	 * table amd the button to that, a small one in a top-right corner od the <code>Table</code> will be
	 * deactivated.</td>
	 * </tr>
	 * <tr>
	 * <td>dynamicform</td>
	 * <td></td>
	 * <td><code>none</code></td>
	 * <td></td>
	 * <td>name of the class which provides the form dynamc names. The contructor without parameter will
	 * be used to do so. No one set by default.</td>
	 * </tr>
	 * <tr>
	 * <td>dynamic</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Defines a new type of table: a dynamic table where columns are inferred from value setted in
	 * the {@link #setValue(Object)} method, not defined in xml.</td>
	 * </tr>
	 * <tr>
	 * <td>translateheader</td>
	 * <td>yes/no</td>
	 * <td>By default, if table is 'dynamic' fixes translateheader='no', in other case always is
	 * translated.</td>
	 * <td>no</td>
	 * <td>Defines whether columns in table header are translated.</td>
	 * </tr>
	 * <tr>
	 * <td>editablecolumns</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Columns that can be edited directly within the <code>Table</code>. By default, the database
	 * will not be updated. This can be used with a Editor to update the record.</td>
	 * </tr>
	 * <tr>
	 * <td>entity</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Name of the entity related to the <code>Table</code></td>
	 * </tr>
	 * <tr>
	 * <td>fixattr</td>
	 * <td><I>parentFormField1:detailFormField1;...;fieldN</I></td>
	 * <td></td>
	 * <td>1</td>
	 * <td>If the <code>Table</code> has detail form, with this parameter, when the detail form is open,
	 * the <code>Table</code> will pass the field values of the parent form (this is, the form in which
	 * the table is placed) to the detail form, to fit them, not allowing them to be modified.It is
	 * accepted to indicate only the parentFormFieldN when it is equal to detailFormField</td>
	 *
	 * </tr>
	 * <tr>
	 * <td>fontsize</td>
	 * <td>Integer</td>
	 * <td></td>
	 * <td></td>
	 * <td>Sets the printing font size. The values can be from 1 up to 7 (more is too big).</td>
	 * </tr>
	 * <tr>
	 * <td>form</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Name of the form that will be open to show the <code>Table</code> information. By default,
	 * this form will be open by double-clicking a record of the <code>Table</code > or when pressing
	 * the <code>Table</code> insert button if "insertform" is not defined.</td>
	 * </tr>
	 * <tr>
	 * <td>insertform</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Name of the form that will be open to show the <code>Table</code> insert form.If you don't
	 * declare this, the detail form also will be the insert form. The insert form</td>
	 * </tr>
	 * <tr>
	 * <td>inserttable</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Defines the table as a autoinsertable table. The last row in this table allows to insert
	 * records directly into database. See {@link #configureInsertTable(Map)}</td>
	 * </tr>
	 * <tr>
	 * <td>key</td>
	 * <td></td>
	 * <td></td>
	 * <td>when multiple key</td>
	 * <td>Name of the entity primary key, when the key is a single value.</td>
	 * </tr>
	 * <tr>
	 * <td>keys</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Additional key names, when the key is multiple. The {@link #getKeys} methid will return the
	 * key and the keys values.</td>
	 * </tr>
	 * <tr>
	 * <td>memoryentity</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Name of the Entity related to the <code>Table</code>. In this case the entity is a local one,
	 * stored un the client memory, to perform local operations that do not need a database
	 * interaction.</td>
	 * </tr>
	 * <tr>
	 * <td>numrowscolumn</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Shows or hide a column containing the row numumber</td>
	 * </tr>
	 * <tr>
	 * <td>opennewwindow</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Activates the functionality to open the detail form in a new window. See the defaultnewwindow
	 * attribute. This option can be set to the whole application by setting the <code>Table</code>
	 * variable SHOW_OPEN_IN_NEW_WINDOW_MENU.</td>
	 * </tr>
	 * <tr>
	 * <td><s>otherkeys</s> Deprecated in 5.2058EN. Use parentkeys</td>
	 * <td><I>field1;...;fieldN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>Name of the other fields that are parentkeys, that are passed to the detail form when it is
	 * open. These values will be get from the parent form, in which the <code>Table</code> is
	 * placed.</td>
	 * </tr>
	 * <tr>
	 * <td><s>parentkey</s> Deprecated in 5.2058EN. Use parentkeys</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Foreing key column name.</td>
	 * </tr>
	 * <tr>
	 * <td>parentkeys</td>
	 * <td><i>fieldpk1:fieldentitypk1;fieldpk2:fieldentitypk2;...fieldpkn :fieldentitypkn (since version
	 * 5.2058EN)</i></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The field that is parentkey and correspondent associated field in entity. It is accepted to
	 * indicate only the fieldpki when it is equal to fieldentitypki, e.g. :
	 * <i>fieldpk1;fieldpk2:fieldentitypk2 ;...fieldpkn:fieldentitypkn</i></td>
	 * </tr>
	 * <tr>
	 * <td>pivotbutton</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Allows the pivot functionality, to work with the information in the <code>Table</code></td>
	 * </tr>
	 * <tr>
	 * <td>queryrows</td>
	 * <td>Integer</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>A positive value in this parameter converts the table into a pageable. This value indicates
	 * the number of records to query for each page.</td>
	 * </tr>
	 * <tr>
	 * <td>queryrowsmodifiable</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Enable or disable to user the option of changes the page size</td>
	 * </tr>
	 * <tr>
	 * <td>quickfilter</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Allows the quick filter functionality.</td>
	 * </tr>
	 * <tr>
	 * <td>quickfiltervisible</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Shows or hide the quick filter component.</td>
	 * </tr>
	 * <tr>
	 * <td>quickfilterlocal</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Allows the quick filter performs local filter in pageable tables</td>
	 * </tr>
	 * <tr>
	 * <td>quickfiltercols</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>The names of the columns use by quickfilter for querying. By default use the visible
	 * columns</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>refreshbutton</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Add the refreshbutton to the table to allow the user to refresh the <code>Table</code>.</td>
	 * </tr>
	 * <tr>
	 * <td>rendermemo</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td></td>
	 * <td></td>
	 * <td>Selects the columns to add a RederMemo, this is, the columns that stores long texts.</td>
	 * </tr>
	 * <tr>
	 * <td>reportcols</td>
	 * <td><I>colname1;...;colnameN</I></td>
	 * <td>Same columns that <code>visiblecols</code></td>
	 * <td>no</td>
	 * <td>Selects the columns that will be showed in report dialog to print custom reports.</td>
	 * </tr>
	 * <tr>
	 * <td>rendertime</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Determines the columns that will have a time render.</td>
	 * </tr>
	 * <tr>
	 * <td>rows</td>
	 * <td>Integer</td>
	 * <td>10</td>
	 * <td></td>
	 * <td>Default width for the table header</td>
	 * </tr>
	 * <tr>
	 * <td>scrollh</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td></td>
	 * <td>Allows the <code>Table</code> to have a horizontal scroll. This is useful in
	 * <code>Table</code> with several columns to display and there is no room enough for all of
	 * them.</td>
	 * </tr>
	 * <tr>
	 * <td>sumrow</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>Columns to sum at the botton of the <code>Table</code>, in a new row.</td>
	 * </tr>
	 * <tr>
	 * <td>title</td>
	 * <td></td>
	 * <td>the same value than entity attribute</td>
	 * <td></td>
	 * <td>The title that the detail form will have when open.</td>
	 * </tr>
	 * <tr>
	 * <td>updateentityeditablecolumns</td>
	 * <td></td>
	 * <td>the same value than cols attribute</td>
	 * <td></td>
	 * <td>Columns that will update the changes in the database when the user change the values stored
	 * in those.</td>
	 * </tr>
	 * <tr>
	 * <td>visiblecols</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>The columns that will be show in the <code>Table</code> among all the table columns.</td>
	 * </tr>
	 * <tr>
	 * <td>defaultvisiblecols</td>
	 * <td></td>
	 * <td></td>
	 * <td></td>
	 * <td>The default columns that will be show in the <code>Table</code> when user preferences don't
	 * exist.</td>
	 * </tr>
	 * <tr>
	 * <td>opaque</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Parameter to set all the componets in the table opaque or not</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>minrowheight</td>
	 * <td>Integer</td>
	 * <td>Table.MIN_ROW_HEIGHT</td>
	 * <td></td>
	 * <td>Sets the minimun row height. 'fitrowheight' parameter must be setted to 'yes'</td>
	 * </tr>
	 *
	 *
	 * <td>border</td>
	 * <td><i>default/none/raised/lowered or a color defined in {@link ColorConstants}. Moreover, it is
	 * also allowed a border defined in #BorderManager</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The border for Table</td>
	 * </tr>
	 *
	 * <td>vscroll</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td></td>
	 * <td>Vertical scroll for Table component</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>vscrollwidth</td>
	 * <td>Integer</td>
	 * <td>20</td>
	 * <td></td>
	 * <td>Width for vertical scroll</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>showgrid</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Show grid lines in table</td>
	 *
	 * <tr>
	 * <td>renderers</td>
	 * <td>col1:renderer1;col2:renderer2;...;coln:renderern</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Definition of columns and associated renderers. Renderer identifiers are defined in external
	 * file.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>editors</td>
	 * <td>col1:editor1;col2:editor2;...;coln:editorn</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Definition of columns and associated editors. Editor identifiers are defined in external
	 * file.</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>backgroundformbuilder</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Establishes if the detail form is built in a background thread.(Since 5.2060EN-0.5)</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>onsetvalueset</td>
	 * <td><i>fieldonset1:function(tablecolumn1);fieldonset2:function(tablecolumn2);
	 * ...;fieldonsetn:tablecolumnn <br>
	 * Function can have the next values: SUM;MAX;MIN;AVG;CONCAT <br>
	 * (since version 5.2060EN-0.6)</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Field attributes whose value will be set when table data change.</td>
	 * </tr>
	 * <tr>
	 * <td>sortreportcolumns</td>
	 * <td><i>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Sort columns for its translation when create a report</td>
	 * </tr>
	 * <td>inserttitlekey</td>
	 * <td>String</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Configure the insert tab title in a TabbedFormManager</td>
	 * </tr>
	 * <td>detailtitlemaxsize</td>
	 * <td>Integer</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Specifies the max. number of characters in the insert tab title in TabbedFormManager</td>
	 * </tr>
	 * </tr>
	 * <td>blockedcols</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Specifies if the user can block columns when there is a horizontal scroll</td>
	 * </tr>
	 *
	 * </table>
	 */
	@Override
	public void init(final Map parameters) throws Exception {

		this.blockedCols = ParseUtils.getBoolean((String) parameters.get(Table.BLOCKED_COLS), false);
		this.dynamicTable = ParseUtils.getBoolean((String) parameters.get(Table.DYNAMIC), false);
		this.translateHeader = ParseUtils.getBoolean((String) parameters.get(Table.TRANSLATE_HEADER),
				!this.dynamicTable);

		this.configureOpenInNewWindow(parameters);

		this.allowSetupVisibleColumns = ParseUtils.getBoolean((String) parameters.get(Table.CONF_VISIBLE_COLS),
				Table.DEFAULT_VALUE_SETUP_VISIBLE_COLS);
		this.configureDisableInsert(parameters);

		this.autoFixHead = ParseUtils.getBoolean((String) parameters.get(Table.AUTO_ADJUST_HEADER), this.autoFixHead);
		this.scrollHorizontal = ParseUtils.getBoolean((String) parameters.get(Table.HORIZONTAL_SCROLL),
				this.scrollHorizontal);
		this.visibleRowNumberColumn = ParseUtils.getBoolean((String) parameters.get(Table.NUM_ROWS_COLUMN),
				this.visibleRowNumberColumn);

		this.configureEntity(parameters);

		this.memoryEntity = ParseUtils.getString((String) parameters.get(Table.MEMORY_ENTITY), null);
		this.detailFormTitleKey = ParseUtils.getString((String) parameters.get(Table.TITLE), this.entity);

		this.configureSumRow(parameters);

		// Parameter deletebutton
		this.allowDelete = ParseUtils.getBoolean((String) parameters.get(Table.DELETE_BUTTON), false);

		this.configureQueryRows(parameters);

		this.configureRows(parameters);

		// refreshbutton
		this.setRefreshButton = ParseUtils.getBoolean((String) parameters.get(Table.REFRESH_BUTTON), false);

		// Columns = column names separated with ;
		this.configureColumns(parameters);

		this.configureControlVisible(parameters);

		this.setDefaultChartsEnabled(ParseUtils.getBoolean((String) parameters.get(Table.CUSTOM_CHARTS), true));

		this.setPivotTableButton = ParseUtils.getBoolean((String) parameters.get(Table.PIVOT_BUTTON),
				Table.defaultPivotTableVisibility);
		this.installQuickFilter = ParseUtils.getBoolean((String) parameters.get(Table.QUICK_FILTER),
				Table.DEFAULT_QUICK_FILTER);
		this.quickFilterVisible = ParseUtils.getBoolean((String) parameters.get(Table.QUICK_FILTER_VISIBLE),
				Table.defaultQuickFilterVisible);
		this.quickfilterOnFocusSelectAll = ParseUtils.getBoolean(
				(String) parameters.get(Table.QUICK_FILTER_ON_FOCUS_SELECT_ALL),
				Table.defaultQuickFilterOnFocusSelectAll);

		this.sortReportColumn = ParseUtils.getBoolean((String) parameters.get(Table.SORT_REPORT_COLUMS),
				Table.defaultSortReportColumns);

		this.detailTitleMaxSize = ParseUtils.getInteger((String) parameters.get(Table.DETAIL_TITLE_MAX_SIZE),
				Table.defaultDetailTitleMaxSize);

		this.setOpaque(ParseUtils.getBoolean((String) parameters.get(DataField.OPAQUE), false));

		this.tableOpaque = ParseUtils.getBoolean((String) parameters.get(Table.TABLEOPAQUE), Table.defaultTableOpaque);

		this.tableBackgroundColor = ParseUtils.getColor((String) parameters.get(Table.TABLEBGCOLOR),
				(Table.defaultTableBackgroundColor != null ? Table.defaultTableBackgroundColor
						: this.tableBackgroundColor));

		this.configureFitRowHeight(parameters);

		this.configureMinRowHeight(parameters);

		this.showGridValue = ParseUtils.getBoolean((String) parameters.get(Table.SHOWGRID), Table.defaultShowGridValue);

		this.installHelpId();

		// Insert options
		this.inserting = false;
		this.dataBaseInsert = true;
		this.dataBaseRemove = ParseUtils.getBoolean((String) parameters.get(Table.DATABASE_REMOVE), true);
		if (ParseUtils.getBoolean((String) parameters.get(Table.INSERT_TABLE), false)) {
			this.configureInsertTable(parameters);
		}

		this.configureRefreshTable(parameters);

		// It is not possible to set default value (true) in this parameter
		// because can produce a infinite loop when one detail form has
		// tables whose detail forms are the same that initial source form.
		this.backgroundDetailFormBuilder = ParseUtils.getBoolean((String) parameters.get(Table.BACKGROUND_FORM_BUILDER),
				false);

		this.configureOnSetValueSet(parameters);

		this.defaultButtons = ParseUtils.getBoolean((String) parameters.get(Table.DEFAULT_BUTTONS), true);

		this.controlButtonLayout = ParseUtils.getString((String) parameters.get(Table.CONTROL_LAYOUT_KEY),
				Table.defaultControlButtonLayout);

		this.insertMode = ParseUtils.getBoolean((String) parameters.get(Table.INSERT_MODE), false);

		this.dynamicPivotable = ParseUtils.getBoolean((String) parameters.get(Table.D_PIVOT_TABLE_PREFERENCES), false);

		this.dynamicPivotTableForm = ParseUtils.getString((String) parameters.get(Table.D_PIVOT_TABLE_PREFERENCES_FORM),
				null);

		this.dynamicPivotTableEntity = ParseUtils
				.getString((String) parameters.get(Table.D_PIVOT_TABLE_PREFERENCES_ENTITY), null);

		this.configureDetailFormat(parameters);

		this.configureInsertTitleKey(parameters);

		ToolTipManager.sharedInstance().registerComponent(this);
	}

	protected void configureOpenInNewWindow(final Map parameters) {
		this.allowOpenInNewWindow = ParseUtils.getBoolean((String) parameters.get(Table.OPEN_NEW_WINDOW),
				Table.SHOW_OPEN_IN_NEW_WINDOW_MENU);
		if (this.allowOpenInNewWindow) {
			this.openInNewWindowByDefault = ParseUtils.getBoolean((String) parameters.get(Table.DEFAULT_NEW_WINDOW),
					this.openInNewWindowByDefault);
		}
	}

	protected void configureDisableInsert(final Map parameters) {
		this.disableInsert = ParseUtils.getBoolean((String) parameters.get(Table.DISABLE_INSERT), this.disableInsert);
		if (this.dynamicTable) {
			// In a dynamicTable it is not allowed to insert new records
			this.disableInsert = true;
		}

		if (this.disableInsert) {
			this.disableInsert();
		}
	}

	protected void configureControlVisible(final Map parameters) {
		final Object controlsvisible = parameters.get(Table.CONTROLS_VISIBLE);
		if (controlsvisible != null) {
			this.setControlsVisible(ApplicationManager.parseStringValue(controlsvisible.toString(), true));
		}
	}

	protected void configureFitRowHeight(final Map parameters) {
		try {
			this.fitRowHeight = ParseUtils.getBoolean((String) parameters.get(Table.FITROWHEIGHT), this.fitRowHeight);
		} catch (final Exception e) {
			Table.logger.error("Error in 'fitrowheight' parameter", e);
		}
	}

	protected void configureMinRowHeight(final Map parameters) {
		try {
			final String value = (String) parameters.get(Table.MINROWHEIGHT);
			if (value != null) {
				this.fitRowHeight = true;
				this.minRowHeight = ParseUtils.getInteger(value, this.minRowHeight);
			}
		} catch (final Exception e) {
			Table.logger.error("Error in 'minrowheight' parameter", e);
		}
	}

	protected void configureRows(final Map parameters) {
		try {
			this.preferredSizeInRows = ParseUtils.getInteger((String) parameters.get(Table.ROWS),
					this.preferredSizeInRows);
		} catch (final Exception e) {
			Table.logger.error("Error in 'rows' parameter", e);
		}
	}

	protected void configureQueryRows(final Map parameters) {
		try {
			this.rowsNumberToQuery = ParseUtils.getInteger((String) parameters.get(Table.QUERY_ROWS),
					this.rowsNumberToQuery);
		} catch (final Exception e) {
			Table.logger.error("Error in 'queryrows' parameter", e);
		}

		this.queryRowsModifiable = ParseUtils.getBoolean((String) parameters.get(Table.QUERY_ROWS_MODIFIABLE), true);

		if (this.rowsNumberToQuery > 0) {
			this.quickFilterLocal = ParseUtils.getBoolean((String) parameters.get(Table.QUICK_FILTER_PAGEABLE_LOCAL),
					Table.defaultQuickFilterLocal);
		}
	}

	protected void configureOnSetValueSet(final Map parameters) {
		final Object onsetvalueset = parameters.get(Table.ONSETVALUESET);
		if (onsetvalueset != null) {
			this.hOnSetValueSetEquivalences = ApplicationManager.getTokensAt(onsetvalueset.toString(), ";", ":");

			this.onsetvaluesetAttributes = new Vector();

			// We can't use the keys of the Map to get the attribute names
			// because we have to use the same order that is in the xml
			final List valueNamesOrder = ApplicationManager.getTokensAt(onsetvalueset.toString(), ";");
			for (int i = 0; i < valueNamesOrder.size(); i++) {
				final int dotIndex = valueNamesOrder.get(i).toString().indexOf(":");
				if (dotIndex > 0) {
					this.onsetvaluesetAttributes.add(valueNamesOrder.get(i).toString().substring(0, dotIndex));
				} else {
					this.onsetvaluesetAttributes.add(valueNamesOrder.get(i));
				}
			}
		}
	}

	protected void configureEntity(final Map parameters) {
		final Object ent = parameters.get(Table.ENTITY);
		if (ent == null) {
			throw new IllegalArgumentException(this.getClass().getName() + ": entity parameter not found");
		} else {
			this.entity = ent.toString();
		}
	}

	protected void configureSumRow(final Map parameters) {
		final Object sumrow = parameters.get(Table.SUM_ROW);
		if (sumrow != null) {
			this.columnsToSum = ApplicationManager.getTokensAt(sumrow.toString(), ";");
		}
	}

	protected boolean configureVisibleColumns(final Map parameters) {
		final Object visiblecols = parameters.get(Table.VISIBLE_COLS);
		if (visiblecols != null) {
			this.visibleColumns = ApplicationManager.getTokensAt(visiblecols.toString(), ";");
			return true;
		} else { // If visiblecols parameter does not exist then use the
			// same as columns
			for (int i = 0; i < this.attributes.size(); i++) {
				if (!this.visibleColumns.contains(this.attributes.get(i))) {
					this.visibleColumns.add(this.attributes.get(i));
				}
			}
		}
		return false;
	}

	protected void configureKeys(final Map parameters) {
		Object key = parameters.get(Table.KEY);
		final Object keys = parameters.get(Table.KEYS);
		List keyTokens = null;
		if (keys != null) {
			// This code is in case that 'keys' is not nulla and 'key' is
			// null
			keyTokens = ApplicationManager.getTokensAt((String) keys, ";");
			if (key == null) {
				key = keyTokens.remove(0);
			}
		}

		if (key == null) {
			Table.logger.info("{} parameter not found in table component", Table.KEY);
			// Include the key field in the columns
			if (!this.attributes.isEmpty()) {
				this.keyField = (String) this.attributes.get(0);
				this.visibleFieldKeyColumn = true;
			}
		} else {
			this.keyField = key.toString();
			if (!this.attributes.contains(this.keyField)) {
				this.attributes.add(this.keyField);
			}
		}

		if ((keyTokens != null) && (keyTokens.size() > 0)) {
			// Now configure the key fields
			this.keyFields = new Vector();
			for (int k = 0; k < keyTokens.size(); k++) {
				final String keyName = (String) keyTokens.get(k);
				this.keyFields.add(keyName);
				if (!this.attributes.contains(keyName)) {
					this.attributes.add(keyName);
				}
			}
		}

		final Object cods = parameters.get(Table.CODS);
		if (cods != null) {
			try {
				if (cods.toString().indexOf(':') > 0) {
					this.codValues = ApplicationManager.getTokensAt(cods.toString(), ";", ":");
				} else {
					final StringTokenizer st = new StringTokenizer(cods.toString(), ";");
					this.codValues = new Hashtable();

					while (st.hasMoreTokens()) {
						final String tablek = st.nextToken();
						final String formk = st.nextToken();
						this.codValues.put(tablek, formk);
					}
				}
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
				this.codValues = null;
			}
		}
	}

	protected void configureParentKeys(final Map parameters) {
		final String sParentkeys = (String) parameters.get("parentkeys");
		if (sParentkeys != null) {
			this.hParentkeyEquivalences = ApplicationManager.getTokensAt(sParentkeys, ";", ":");
			final Enumeration parentKeysEnum = Collections.enumeration(this.hParentkeyEquivalences.keySet());
			this.parentkeys = new Vector(this.hParentkeyEquivalences.size());
			while (parentKeysEnum.hasMoreElements()) {
				this.parentkeys.add(parentKeysEnum.nextElement());
			}
		} else {
			this.parentKey = ParseUtils.getString((String) parameters.get(Table.PARENTKEY), this.parentKey);
			this.parentkeys = new Vector();
			if (this.parentKey == null) {
				Table.logger.debug("{} parameter hasn't been found in table component", Table.PARENTKEY);
			} else {
				this.parentkeys.add(this.parentKey);
			}

			final Object otherKeys = parameters.get(Table.OTHER_KEYS);
			if (otherKeys != null) {
				final StringTokenizer st = new StringTokenizer(otherKeys.toString(), ";");
				while (st.hasMoreTokens()) {
					final String parentKeyName = st.nextToken();
					this.otherParentKeys.add(parentKeyName);
					if (!this.parentkeys.contains(parentKeyName)) {
						this.parentkeys.add(parentKeyName);
					}
				}
			}
		}
	}

	protected void configureReportColumns(final Map parameters) {
		// Create report columns to show in custom report dialog
		List reportCols = new Vector();
		reportCols = ApplicationManager.getTokensAt((String) parameters.get(Table.REPORT_COLS), ";");
		if (reportCols.isEmpty()) {
			this.reportCols.addAll(this.visibleColumns);
		} else {
			this.reportCols.addAll(reportCols);
		}

	}

	protected void configureDynamicForm(final Map parameters) {
		final Object dynamicForm = parameters.get(Table.DYNAMIC_FORM);
		if (dynamicForm != null) {
			try {
				final String sStringClass = dynamicForm.toString();
				final Class cFormClass = Class.forName(sStringClass);
				final DynamicFormManager dFDynamicForm = (DynamicFormManager) cFormClass.newInstance();
				dFDynamicForm.setBaseName(this.formName);
				this.dinamicFormClass = sStringClass;
				this.dynamicFormManager = dFDynamicForm;
				Table.logger.debug("{} class has been established as DynamicForm for the node.", sStringClass);
			} catch (final Exception e) {
				Table.logger.error("Error loading class for DynamicForm", e);
			}
		}
	}

	protected void configureFixAttr(final Map parameters) {
		final Object fixattr = parameters.get(Table.FIX_ATTR);
		if (fixattr != null) {
			this.hAttributesToFixEquivalences = ApplicationManager.getTokensAt((String) fixattr, ";", ":");
			final Iterator<String> attributesToFixKeys = this.hAttributesToFixEquivalences.keySet().iterator();
			this.attributesToFix = new ArrayList<String>(this.hAttributesToFixEquivalences.size());
			while (attributesToFixKeys.hasNext()) {
				this.attributesToFix.add(attributesToFixKeys.next());
			}
		}
	}

	protected void configureCalculedColumns(final Map parameters, final boolean hasVisibleColumns) {
		final Object oCalculatedColumns = parameters.get(Table.CALCULED_COLS);
		if (oCalculatedColumns != null) {
			// We need the column List and the expressions
			final StringTokenizer st = new StringTokenizer(oCalculatedColumns.toString(), ";");
			boolean bOdd = true;
			String sColumName = null;
			String sExpression = null;
			while (st.hasMoreTokens()) {
				final String token = st.nextToken();
				// Odd tokens are the column names
				if (bOdd) {
					sColumName = token;
					bOdd = false;
				} else {
					// Math expression
					sExpression = token;
					if (this.calculedColumns == null) {
						this.calculedColumns = new Hashtable(3);
					}
					this.calculedColumns.put(sColumName, sExpression);
					bOdd = true;
				}
			}

			if ((this.calculedColumns != null) && !this.calculedColumns.isEmpty()) {
				this.originalCalculatedColumns = new Vector(Arrays.asList(this.calculedColumns.keySet().toArray()));
			}
		}

		this.modifiableCalculatedColumns = ParseUtils.getBoolean(
				(String) parameters.get(Table.MODIFIABLE_CALCULATED_COLUMNS), Table.defaultModifiableCalculatedColumns);

		if (!hasVisibleColumns) {
			final Enumeration enumKeys = Collections.enumeration(this.calculedColumns.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object cc = enumKeys.nextElement();
				if (!this.visibleColumns.contains(cc)) {
					this.visibleColumns.add(cc);
				}
			}
		}

		final Object calculedcolsrequiredfields = parameters.get(Table.CALCULED_COLS_REQUIRED_FIELDS);
		if (calculedcolsrequiredfields != null) {
			this.requiredColumnsCalculedColumns = ApplicationManager.getTokensAt(calculedcolsrequiredfields.toString(),
					";");
		}
	}

	protected void configureCurrencyColumns(final Map parameters) {
		final Object currency = parameters.get(Table.CURRENCY);
		if (currency != null) {
			final StringTokenizer st = new StringTokenizer(currency.toString(), ";");
			while (st.hasMoreTokens()) {
				final String nom = st.nextToken();
				if (this.attributes.contains(nom)
						|| ((this.calculedColumns != null) && this.calculedColumns.containsKey(nom))) {
					this.currencyColumns.add(nom);
				}
			}
		}
	}

	protected void configureTimeColumns(final Map parameters) {
		final Object rendertime = parameters.get(Table.TIME_RENDER);
		if (rendertime != null) {
			final StringTokenizer st = new StringTokenizer(rendertime.toString(), ";");
			while (st.hasMoreTokens()) {
				final String nom = st.nextToken();
				if (this.attributes.contains(nom)
						|| ((this.calculedColumns != null) && this.calculedColumns.containsKey(nom))) {
					this.hourRenderColumns.add(nom);
				}
			}
		}
	}

	protected void configureEditableColumns(final Map parameters) {
		final Object editablecolumns = parameters.get(Table.EDITABLE_COLUMNS);
		if (editablecolumns == null) {
		} else {
			final StringTokenizer st = new StringTokenizer(editablecolumns.toString(), ";");
			while (st.hasMoreTokens()) {
				this.editableColumns.add(st.nextToken());
			}
		}

		final Object updateentityeditablecolumns = parameters.get(Table.UPDATE_ENTITY_EDITABLE_COLUMNS);
		if (updateentityeditablecolumns != null) {
			final StringTokenizer st = new StringTokenizer(updateentityeditablecolumns.toString(), ";");
			while (st.hasMoreTokens()) {
				this.editableColumnsUpdateEntity.add(st.nextToken());
			}
		}
	}

	protected void configureMemoColumns(final Map parameters) {
		final Object rendermemo = parameters.get(Table.MEMO_RENDER);
		if (rendermemo != null) {
			final StringTokenizer st = new StringTokenizer(rendermemo.toString(), ";");
			while (st.hasMoreTokens()) {
				final String nom = st.nextToken();
				if (this.attributes.contains(nom)
						|| ((this.calculedColumns != null) && this.calculedColumns.containsKey(nom))) {
					this.memoRenderColumns.add(nom);
				}
			}
		}
	}

	protected void configureColumns(final Map parameters) {
		final Object cols = parameters.get(Table.COLS);
		if (cols != null) {
			final String sColumnNames = cols.toString();
			this.attributes = ApplicationManager.getTokensAt(sColumnNames, ";");

			final boolean hasVisibleColumns = this.configureVisibleColumns(parameters);

			final Object quickfiltercols = parameters.get(Table.QUICK_FILTER_COLS);
			if (quickfiltercols != null) {
				this.quickFilterColumns = ApplicationManager.getTokensAt(quickfiltercols.toString(), ";");
			}

			this.originalVisibleColumns = ObjectTools.clone(this.visibleColumns);

			final Object oDefaultCols = parameters.get(Table.DEFAULT_VISIBLE_COLS);
			if (oDefaultCols != null) {
				this.defaultVisibleColumns = ApplicationManager.getTokensAt(oDefaultCols.toString(), ";");
			}

			this.configureKeys(parameters);
			this.configureParentKeys(parameters);

			this.configureReportColumns(parameters);
			// Detail form
			this.formName = ParseUtils.getString((String) parameters.get(Table.FORM), this.formName);

			this.insertFormName = ParseUtils.getString((String) parameters.get(Table.INSERT_FORM), this.insertFormName);

			if (this.formName == null) {
				this.disableInsert = true;
				this.buttonPlus.setEnabled(false);
				if (this.buttonPlus2 != null) {
					this.buttonPlus2.setEnabled(false);
					this.buttonPlus2.setVisible(false);
				}
			}

			this.configureDynamicForm(parameters);

			// Parameter controls
			this.showControls = ParseUtils.getBoolean((String) parameters.get(Table.CONTROLS), true);

			// Parameter fontsize
			try {
				this.fontSize = ParseUtils.getInteger((String) parameters.get(Table.FONT_SIZE), this.fontSize);
			} catch (final Exception e) {
				Table.logger.error("Table: Error in parameter: " + Table.FONT_SIZE, e);
				this.fontSize = 1;
			}

			this.configureFixAttr(parameters);

			this.configureCalculedColumns(parameters, hasVisibleColumns);

			this.configureCurrencyColumns(parameters);

			this.configureTimeColumns(parameters);

			this.configureEditableColumns(parameters);

			this.configureMemoColumns(parameters);
		}
	}

	public boolean isSortReportColumn() {
		return this.sortReportColumn;
	}

	public void setSortReportColumn(final boolean sortReportColumn) {
		this.sortReportColumn = sortReportColumn;
	}

	public String getDebugInfo() {
		final StringBuilder buffer = new StringBuilder();
		buffer.append("<html>");
		buffer.append("<B>" + Table.ENTITY + "</B>" + ":  " + this.getEntityName());
		buffer.append("<br>");
		buffer.append("<B>" + Table.COLS + "</B>" + ":  "
				+ ApplicationManager.vectorToStringSeparateBy(this.attributes, ";"));
		buffer.append("<br>");
		buffer.append("<B>" + Table.VISIBLE_COLS + "</B>" + ":  "
				+ ApplicationManager.vectorToStringSeparateBy(this.visibleColumns, ";"));
		buffer.append("<br>");
		buffer.append("<B>" + Table.EDITABLE_COLUMNS + "</B>" + ":  "
				+ ApplicationManager.vectorToStringSeparateBy(this.editableColumns, ";"));
		buffer.append("<br>");
		buffer.append("<B>" + Table.UPDATE_ENTITY_EDITABLE_COLUMNS + "</B>" + ":  "
				+ ApplicationManager.vectorToStringSeparateBy(this.editableColumnsUpdateEntity, ";"));
		buffer.append("<br>");
		buffer.append("<B>" + "parentkeys" + "</B>" + ":  "
				+ ApplicationManager.vectorToStringSeparateBy(this.parentkeys, ";"));
		buffer.append("<br>");
		buffer.append("<B>" + "detailForm" + "</B>" + ":  " + this.formName);
		if (this.insertFormName != null) {
			buffer.append("<br>");
			buffer.append("<B>" + "insertDetailForm" + "</B>" + ":  " + this.insertFormName);
		}
		buffer.append("</html>");
		return buffer.toString();
	}

	@Override
	public String getToolTipText(final MouseEvent e) {
		if (e.isControlDown() && e.isAltDown() && e.isShiftDown()) {
			return this.getDebugInfo();
		} else {
			return super.getToolTipText(e);
		}
	}

	/**
	 * This method configure parameters for insertable table. This method is called only when
	 * 'inserttable' parameter is enabled in .xml definition file.
	 * @param params the <code> Hashtable</code> with parameters
	 *
	 *        <p>
	 *        The attributes allowed are:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</b></td>
	 *        <td><b>values</b></td>
	 *        <td><b>default</b></td>
	 *        <td><b>required</b></td>
	 *        <td><b>meaning</b></td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>requiredcols</td>
	 *        <td><i>requiredcol1;requiredcol2;...;requiredcoln</i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Required cols in table</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>insertablecols</td>
	 *        <td><i>inserttablecol1;inserttablecol2;...;inserttablecoln</i></td>
	 *        <td>All columns (when is insertable='yes')</td>
	 *        <td>no</td>
	 *        <td>Columns of table that are insertables</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>databaseinsert</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether insert operations are executed directly in database.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>databaseremove</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether remove operations are executed directly in database.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>showtotalinserting</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether the row at bottom to show total operation results is showed. Note:
	 *        This parameter is read in {@link TableSorter} constructor</td>
	 *        </tr>
	 *        </Table>
	 */
	protected void configureInsertTable(final Map params) {
		this.insertTableInsertRowListenerList = new EventListenerList();
		this.vrequiredCols = new Vector();
		if (this.parameters.containsKey(Table.REQUIRED_COLS) && !this.parameters.get(Table.REQUIRED_COLS).equals("")) {
			this.vrequiredCols = ApplicationManager.getTokensAt((String) this.parameters.get(Table.REQUIRED_COLS), ";");
		}
		this.vupdateEditableColumns = this.editableColumnsUpdateEntity;
		this.dataBaseInsert = ParseUtils.getBoolean((String) this.parameters.get(Table.DATABASE_INSERT), true);
		// TODO
		// if (!this.dataBaseInsert) {
		// this.editableColumnsUpdateEntity = new Vector(1);
		// }
	}

	protected void configureRefreshTable(final Map params) {
		this.refreshTableListenerList = new EventListenerList();
	}

	/**
	 * Removes all the charts available for this table.
	 *
	 * @see ChartUtilities
	 */
	public void removeAllCharts() {
		if (!Table.CHART_ENABLED) {
			return;
		}

		if (this.chartUtilities == null) {
			return;
		}

		this.chartUtilities.removeAllCharts();
		for (int i = 0; i < this.menuChartItems.size(); i++) {
			if (this.menu != null) {
				this.menu.remove((JMenuItem) this.menuChartItems.get(i));
			}
			this.menuChartItems.remove(i);
			i = i - 1;
		}
		for (int i = 0; i < this.chartGraphMenuItems.size(); i++) {
			if (this.chartMenu != null) {
				this.chartMenu.remove((JMenuItem) this.chartGraphMenuItems.get(i));
			}
			this.chartGraphMenuItems.remove(i);
			i = i - 1;
		}
	}

	/**
	 * Removes the specified chart for this table
	 * @param description the chart to remove description
	 * @see ChartUtilities
	 */
	public void removeChart(final String description) {
		if (!Table.CHART_ENABLED) {
			return;
		}
		if (this.chartUtilities == null) {
			return;
		}
		this.chartUtilities.removeChart(description);
		for (int i = 0; i < this.menuChartItems.size(); i++) {
			if (((JMenuItem) this.menuChartItems.get(i)).getName().equals(description)) {
				if (this.menu != null) {
					this.menu.remove((JMenuItem) this.menuChartItems.get(i));
				}
				this.menuChartItems.remove(i);
				return;
			}
		}

		for (int i = 0; i < this.chartGraphMenuItems.size(); i++) {
			if (((JMenuItem) this.chartGraphMenuItems.get(i)).getName().equals(description)) {
				if (this.chartMenu != null) {
					this.chartMenu.remove((JMenuItem) this.chartGraphMenuItems.get(i));
				}
				this.chartGraphMenuItems.remove(i);
				return;
			}
		}
	}

	/**
	 * Configures the chart.
	 * @param xLabel the text corresponding to the x axis label
	 * @param yLabel the text corresponding to the y axis label
	 * @param xColumn the column displayed in the x axis
	 * @param yColumns the columns displayed in the y axis
	 * @param series
	 * @param descr a chart description
	 * @param type the chart type. Values can be be
	 *        <p>
	 *        <ul>
	 *        <li>ChartUtilities.PIE</li>
	 *        <li>ChartUtilities.PIE_3D</li>
	 *        <li>ChartUtilities.BAR</li>
	 *        <li>ChartUtilities.BAR_3D</li>
	 *        <li>ChartUtilities.STACKED_3D</li>
	 *        <li>ChartUtilities.LINE</li>
	 *        </ul>
	 * @see ChartUtilities#configureChart(String , String , String , String[] , String[] , String , int
	 *      )
	 */
	public void configureChart(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int type) {
		if (!Table.CHART_ENABLED) {
			return;
		}

		this.createChartUtilities();

		this.chartUtilities.configureChart(xLabel, yLabel, xColumn, yColumns, series, descr, type);

		if (this.buttonChart == null) {
			this.installChartButton();
		}
	}

	/**
	 * Configures a chart with dates.
	 * @param xLabel the text corresponding to the x axis label
	 * @param yLabel the text corresponding to the y axis label
	 * @param xColumn the column displayed in the x axis
	 * @param yColumns the columns displayed in the y axis
	 * @param series
	 * @param descr a chart description
	 * @param interval the time interval. Values can be be
	 *        <p>
	 *        <ul>
	 *        <li>ChartUtilities.DAY</li>
	 *        <li>ChartUtilities.MONTH</li>
	 *        <li>ChartUtilities.QUARTER</li>
	 *        <li>ChartUtilities.YEAR</li>
	 *        </ul>
	 * @see ChartUtilities#configureChartXDate
	 */

	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int interval) {
		if (!Table.CHART_ENABLED) {
			return;
		}
		if (this.chartUtilities == null) {
			this.createChartUtilities();
		}

		this.chartUtilities.configureChartXDate(xLabel, yLabel, xColumn, yColumns, series, descr, interval);

		if (this.buttonChart == null) {
			this.installChartButton();
		}
	}

	/**
	 * Configures a chart with dates.
	 * @param xLabel the text corresponding to the x axis label
	 * @param yLabel the text corresponding to the y axis label
	 * @param xColumn the column displayed in the x axis
	 * @param yColumns the columns displayed in the y axis
	 * @param series
	 * @param descr a chart description
	 * @param interval the time interval. Values can be be
	 *        <p>
	 *        <ul>
	 *        <li>ChartUtilities.DAY</li>
	 *        <li>ChartUtilities.MONTH</li>
	 *        <li>ChartUtilities.QUARTER</li>
	 *        <li>ChartUtilities.YEAR</li>
	 *        </ul>
	 * @param fillZeros
	 */
	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int interval, final boolean fillZeros) {
		if (!Table.CHART_ENABLED) {
			return;
		}

		if (this.chartUtilities == null) {
			this.createChartUtilities();
		}

		this.chartUtilities.configureChartXDate(xLabel, yLabel, xColumn, yColumns, series, descr, interval, fillZeros);

		if (this.buttonChart == null) {
			this.installChartButton();
		}
	}

	/**
	 * Shows the chart window. Client permissions are checked.
	 * @param description chart description
	 */
	public void showChart(final String description) {
		if (this.chartUtilities == null) {
			this.createChartUtilities();
		}

		this.chartUtilities.showChart(description);
	}

	/**
	 * Installs the table button that open the Charting dialog.
	 */
	protected void installChartButton() {
		if (!Table.CHART_ENABLED) {
			return;
		}
		this.buttonChart = new TableButton();
		if (this.buttonChart instanceof TableComponent) {
			((TableComponent) this.buttonChart).setKey(Table.BUTTON_CHART);
		}
		final ImageIcon chartIcon = ImageManager.getIcon(ImageManager.TABLE_CHART);
		if (chartIcon != null) {
			this.buttonChart.setIcon(chartIcon);
		} else {
			this.buttonChart.setText("Chart");
		}
		this.setTableComponentEnabled(Table.BUTTON_CHART, false);
		this.buttonChart.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonChart);
		this.buttonChart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.showChartMenu(e);
			}
		});
	}

	/**
	 * Shows the ChartMenu
	 * @param e event that launched the request to open the chart menu
	 */
	protected void showChartMenu(final ActionEvent e) {
		if (this.chartMenu == null) {
			this.chartMenu = new ExtendedJPopupMenu();
			if (!ApplicationManager.useOntimizePlaf) {
				this.chartMenu.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
			}
		}

		if (this.chartUtilities == null) {
			this.createChartUtilities();
		}

		final ChartInfoRepository repository = this.chartUtilities.getChartInfoRepository();

		if (repository != null) {
			final Enumeration descriptions = repository.keys();
			while (descriptions.hasMoreElements()) {
				final String description = descriptions.nextElement().toString();
				final ChartInfo info = repository.getChartInfo(description);
				boolean found = false;
				for (int i = 0; i < this.chartGraphMenuItems.size(); i++) {
					if (((JMenuItem) this.chartGraphMenuItems.get(i)).getName().equals(description)) {
						found = true;
						break;
					}
				}
				if (found) {
					continue;
				}
				final JMenuItem item = new JMenuItem(description);
				ImageIcon chartIcon = null;
				if (info.hasIntervals()) {
					chartIcon = ImageManager.getIcon(ImageManager.LINE_CHART);
				} else {
					switch (info.getType()) {
					case IChartUtilities.LINE:
						chartIcon = ImageManager.getIcon(ImageManager.LINE_CHART);
						break;
					case IChartUtilities.PIE:
						chartIcon = ImageManager.getIcon(ImageManager.PIE_CHART);
						break;
					case IChartUtilities.PIE_3D:
						chartIcon = ImageManager.getIcon(ImageManager.PIE);
						break;
					case IChartUtilities.BAR:
						chartIcon = ImageManager.getIcon(ImageManager.BAR);
						break;
					case IChartUtilities.BAR_3D:
						chartIcon = ImageManager.getIcon(ImageManager.BAR);
						break;

					case IChartUtilities.STACKED_3D:
						chartIcon = ImageManager.getIcon(ImageManager.STACK);
						break;
					default:
						chartIcon = ImageManager.getIcon(ImageManager.BAR);
						break;
					}
				}
				item.setName(description);
				if (chartIcon != null) {
					item.setIcon(chartIcon);
				}
				item.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						Table.this.showChart(description);
					}
				});
				this.chartMenu.add(item);
				this.chartGraphMenuItems.add(item);
			}
		}
		this.chartMenu.show((Component) e.getSource(), 0, ((Component) e.getSource()).getHeight() + 1);
	}

	/**
	 * Creates a EntityResult object with the visible values of the table.<br>
	 * This method uses the table renderers to obtain visible cell values.
	 * @return
	 */
	public EntityResult getValueToExport() {
		return this.getValueToExport(true, true);
	}

	/**
	 * This method get the values of the renderers of all the cells in the table
	 * @param calculatedRow To include or not the calculated row
	 * @param translateHeader To translate or not the header titles. If you use this option with true,
	 *        the translations of the column names must be all diferent
	 * @return
	 */
	public EntityResult getValueToExport(final boolean calculatedRow, final boolean translateHeader) {
		return this.getValueToExport(calculatedRow, translateHeader, false);
	}

	/**
	 * This method get the values of the renderers of all the cells in the table
	 * @param calculatedRow To include or not the calculated row
	 * @param translateHeader To translate or not the header titles.
	 * @param useNoStringKeys When you are translating the header values you need to put an Object
	 *        diferent of a String as key to avoid duplicated keys (to string with the same translation)
	 * @return
	 */
	public EntityResult getValueToExport(final boolean calculatedRow, final boolean translateHeader, final boolean useNoStringKeys) {
		final EntityResult result = new EntityResultMapImpl();
		final List columnNames = new Vector();
		final Map hColumnSQLTypes = new Hashtable();
		final List visibleColumnNames = new Vector();
		boolean bSetSqlTypes = false;
		final Object currentValue = this.getValue();
		if (currentValue instanceof EntityResult) {
			bSetSqlTypes = true;
		}
		final List vCalculedColumns = new Vector(this.calculedColumns.keySet());
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			final Object oText = translateHeader ? tc.getHeaderValue() : tc.getIdentifier().toString();

			final Object text = useNoStringKeys ? new KeyObject(tc.getIdentifier(), oText) : oText;
			if ((tc.getIdentifier() != null) && this.isVisibleColumn(tc.getIdentifier().toString())) {
				if (!ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(tc.getIdentifier().toString())) {
					if (bSetSqlTypes) {
						int sqlType = ((EntityResult) currentValue).getColumnSQLType(tc.getIdentifier().toString());
						// since 5.3.13 -> calculated column types are asigned
						// to float insteadof varchar. So, user can operate with
						// them.
						if (vCalculedColumns.contains(tc.getIdentifier())) {
							sqlType = Types.FLOAT;
						}
						hColumnSQLTypes.put(oText, sqlType);
					}
					visibleColumnNames.add(text);
				}
				result.put(text, new Vector());

			}
			columnNames.add(text);
		}

		int rowsCount = this.table.getRowCount();
		if (this.isInsertingEnabled()) {
			rowsCount = rowsCount - 1;
		}
		for (int j = 0; j < rowsCount; j++) {
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final String text = this.getCellValueAsString(j, i);
				final List v = (List) result.get(columnNames.get(i));
				if (v != null) {
					if (text != null) {
						v.add(text);
					} else {
						v.add("");
					}
					result.put(columnNames.get(i), v);
				}
			}
		}

		if (calculatedRow && (this.sumRowTable.getRowCount() > 0)) {
			final Map sumRowData = new Hashtable();
			for (int i = 0; i < this.sumRowTable.getColumnCount(); i++) {
				final Object columnName = columnNames.get(i);
				if (result.containsKey(columnName)) {
					final Object value = ((SumRowTable) this.sumRowTable).getCellValueAsString(0, i);
					if (value != null) {
						sumRowData.put(columnName, value.toString());
					} else {
						sumRowData.put(columnName, "");
					}
				}
			}
			result.addRecord(sumRowData, result.calculateRecordNumber());
		}

		result.setColumnOrder(visibleColumnNames);
		result.setColumnSQLTypes(hColumnSQLTypes);
		return result;
	}

	/**
	 * Values that will be used to create custom reports
	 * @return
	 */

	public Object getValueToReport() {
		final EntityResult result = new EntityResultMapImpl();
		final List columnNames = new Vector();
		final List visibleColumnNames = new Vector();

		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			final Object text = tc.getIdentifier().toString();

			if ((tc.getIdentifier() != null) && this.reportCols.contains(tc.getIdentifier().toString())) {
				result.put(text, new Vector());
				visibleColumnNames.add(text);
			}
			columnNames.add(text);
		}

		int rowsCount = this.table.getRowCount();
		if (this.isInsertingEnabled()) {
			rowsCount = rowsCount - 1;
		}

		for (int j = 0; j < rowsCount; j++) {
			if (!this.isSumRow(j)) {
				for (int i = 0; i < this.table.getColumnCount(); i++) {
					if (this.reportCols.contains(this.table.getColumnName(i))) {
						Object oText = this.getCellRenderedValue(j, i);
						if (oText instanceof GroupList) {
							oText = ((GroupList) oText).toString();
						} else if (oText instanceof ValueByGroup) {
							oText = ((ValueByGroup) oText).getValue();
						}
						final List v = (List) result.get(this.table.getColumnName(i));
						v.add(oText);
						result.put(columnNames.get(i), v);
					}
				}
			}
		}
		result.setColumnOrder(visibleColumnNames);
		return result;
	}

	public static class KeyObject {

		protected Object value;

		protected Object key;

		public KeyObject(final Object key) {
			this(key, key);
		}

		public KeyObject(final Object key, final Object value) {
			this.value = value;
			this.key = key;
		}

		public Object getKey() {
			return this.key;
		}

		@Override
		public String toString() {
			if (this.value != null) {
				return this.value.toString();
			}
			return "";
		}

	}

	/**
	 * Creates a text to be interpreted by the Excel. The text will contain all the data in the table,
	 * with the columns separated by tabs and EOL at the end of each line. The headers are exported as
	 * well.
	 * @return the contents of the excel file.
	 */
	public String getExcelString() {
		final int rowsCount = this.table.getRowCount();
		final int[] rows = new int[rowsCount];
		for (int i = 0; i < rowsCount; i++) {
			rows[i] = i;
		}
		return this.getExcelString(rows);
	}

	/**
	 *
	 * Creates a text to be interpreted by the Excel. The text will contain all the data in the table,
	 * with the columns separated by tabs and EOL at the end of each line. The headers are exported as
	 * well.
	 * @param selectedRows
	 * @return
	 */
	public String getExcelString(final int[] selectedRows) {
		final long t = System.currentTimeMillis();
		// Create a string with all the table data
		// Excel export: columns separated with tab and rows with enter

		final StringBuilder sbHeader = new StringBuilder();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			if ((tc.getIdentifier() != null) && this.isVisibleColumn(tc.getIdentifier().toString())) {
				sbHeader.append(tc.getHeaderValue() + "\t");
			}
		}
		final StringBuilder sbValues = new StringBuilder("");
		int rowsCount = this.table.getRowCount();
		if (this.isInsertingEnabled()) {
			rowsCount = rowsCount - 1;
		}

		if ((selectedRows != null) && (selectedRows.length > 0)) {
			for (int j = 0; j < selectedRows.length; j++) {
				sbValues.append("\n");
				for (int i = 0; i < this.table.getColumnCount(); i++) {
					String sText = this.getCellValueAsString(selectedRows[j], i);
					if (sText != null) {
						sText = sText.replaceAll("\"", "\"\"");
						sbValues.append("\"");
						sbValues.append(sText);
						sbValues.append("\"");
						sbValues.append("\t");
					}
				}
			}
		}

		sbValues.append("\n");

		final long tf = System.currentTimeMillis();
		Table.logger.trace("Table: Excel String time: {} milliseconds", tf - t);
		return sbHeader + sbValues.toString();
	}

	/**
	 * Creates a HTML representation of the information in the table. The response will contain all the
	 * data in the table, formatted as a HTML table The headers are also exported.
	 * @return the table information formatted in HTML
	 */
	public String getHTMLString() {
		// Create an String with the table data
		// Export to HTML:
		final String tagEnd = "</TABLE></BODY></HTML>";
		final StringBuilder sbHeader = new StringBuilder("<HTML><HEAD></HEAD><BODY><TABLE border='1'><TR>");
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			if ((tc.getIdentifier() != null) && this.isVisibleColumn(tc.getIdentifier().toString())) {
				sbHeader.append("<TH>" + tc.getHeaderValue() + "</TH>");
			}
		}
		int rowsCount = this.table.getRowCount();
		if ((this.getJTable().getModel() instanceof TableSorter)
				&& ((TableSorter) this.getJTable().getModel()).isInsertingRow(rowsCount - 1)) {
			rowsCount = rowsCount - 1;
		}
		for (int j = 0; j < rowsCount; j++) {
			final StringBuilder sbRow = new StringBuilder("<TR>");
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final String sText = this.getCellValueAsString(j, i);
				if (sText != null) {
					sbRow.append("<TD>");
					sbRow.append(sText);
					sbRow.append("</TD>");
				}
			}
			sbRow.append("</TR>\n");
			sbHeader.append(sbRow.toString());
		}
		return sbHeader.toString() + tagEnd;
	}

	/**
	 * Get the value of the specified cell as a String, using the renderer if exist
	 * @param row Row number
	 * @param column Column number
	 * @return
	 */
	public String getCellValueAsString(final int row, final int column) {
		String sText = null;
		final TableColumn tc = this.table.getColumnModel().getColumn(column);
		if ((tc.getIdentifier() != null) && this.isVisibleColumn(tc.getIdentifier().toString())) {
			final Object oValue = this.table.getValueAt(row, column);
			final TableCellRenderer r = this.table.getCellRenderer(row, column);
			final Component c = r.getTableCellRendererComponent(this.table, oValue, false, false, row, column);
			if (r instanceof ImageCellRenderer) {
				if (oValue != null) {
					sText = new String(((BytesBlock) oValue).getBytes(), this.charsetIso88591);
				}
			} else if (r instanceof ComboReferenceCellRenderer) {
				sText = ((ComboReferenceCellRenderer) r).getCodeDescription(oValue);
			} else if (c instanceof JLabel) {
				sText = ((JLabel) c).getText();
			} else if (c instanceof JTextComponent) {
				sText = ((JTextComponent) c).getText();
			} else if (c instanceof JCheckBox) {
				if (((JCheckBox) c).isSelected()) {
					sText = ApplicationManager.getTranslation("Yes");
				} else {
					sText = ApplicationManager.getTranslation("No");
				}
			} else {
				sText = "";
				if (oValue != null) {
					if (oValue instanceof Boolean) {
						if (((Boolean) oValue).booleanValue()) {
							sText = ApplicationManager.getTranslation("Yes");
						} else {
							sText = ApplicationManager.getTranslation("No");
						}
					} else {
						sText = oValue.toString();
					}
				}
			}
		}
		return sText;
	}

	/**
	 *
	 * This method returns the value of rendered cell take into account when a cell contains a combo
	 * reference cell renderer.
	 * @param row The current row
	 * @param column The current column
	 * @return the value rendered
	 */
	public Object getCellRenderedValue(final int row, final int column) {
		Object oValue = null;
		final TableColumn tc = this.table.getColumnModel().getColumn(column);
		if ((tc.getIdentifier() != null) && this.reportCols.contains(tc.getIdentifier().toString())) {
			oValue = this.table.getValueAt(row, column);
			final TableCellRenderer r = this.table.getCellRenderer(row, column);
			// TODO Check the comboreferenceCellRenderer.
			if (r instanceof ComboReferenceCellRenderer) {
				return ((ComboReferenceCellRenderer) r).getCodeDescription(oValue);
			}
		}
		return oValue;
	}

	/**
	 * Returns the name of all the table columns.
	 * @return the name of all the table columns
	 */
	public List getAttributeList() {
		return ObjectTools.clone(this.attributes);
	}

	/**
	 * Deletes all the data stored in the table.
	 */
	@Override
	public void deleteData() {
		this.checkRefreshThread();
		this.hideInformationPanel();
		this.changeToTableView();

		if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
			this.table.getCellEditor().stopCellEditing();
		}

		((TableSorter) this.table.getModel()).setData(null);

		if (this.isInsertingEnabled()) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
		}

		if (this.isOperationInMemory()) {
			try {
				final Entity ent = ApplicationManager.getApplication()
						.getReferenceLocator()
						.getEntityReference(this.getEntityName());
				if (ent instanceof DynamicMemoryEntity) {
					((DynamicMemoryEntity) ent).clear();
				}
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
			}
		}
	}

	/**
	 * Sets the initial column width. The value set is the one stored in {@link #preferredSizeInRows}
	 * @return an array with the column widths
	 * @see #initColumnsWidth
	 */
	public int[] initColumnsWidth() {
		this.checkRefreshThread();
		return this.initColumnsWidth(this.preferredSizeInRows);
	}

	/**
	 * Sets the initial column width to the one passed as parameter.
	 * @param columnsWidth the default column width
	 * @see #setPreferredTableColumnWidths
	 * @return an array with the column widths
	 */

	public int[] initColumnsWidth(final int columnsWidth) {
		final int[] iWidths = this.setPreferredTableColumnWidths(columnsWidth);
		if (this.packTable) {
			this.setPreferredTableWidth();
		}
		if ((this.table != null) && (this.table.getModel() instanceof TableSorter)) {
			((TableSorter) this.table.getModel()).setPreferredHeadSize();
		}
		return iWidths;
	}

	/**
	 * Returns true if the column with the specified name is visible.
	 * @param columnName the column name
	 * @return true if the column with the specified name is visible; false otherwise
	 */
	public boolean isVisibleColumn(final String columnName) {
		if (ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(columnName)) {
			return this.isRowNumberColumnVisible();
		}
		if (this.attributes.contains(columnName) || this.calculedColumns.containsKey(columnName)) {
			if ((this.visibleColumns != null) && !this.visibleColumns.isEmpty()) {
				if (this.visibleColumns.contains(columnName)
						&& this.checkColumnTablePermission(columnName, "visible")) {
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

	/**
	 * Packs the table, in order to reduce the unused space.
	 */
	public void packTable() {
		this.packTable = true;
		final int[] widths = this.initColumnsWidth(this.preferredSizeInRows);

		int total = 0;
		for (int i = 0; i < widths.length; i++) {
			total = total + widths[i];
		}
		final FontMetrics fontMetrics = this.getFontMetrics(this.getFont());

		// Sum the border and the scroll width to the columns width
		total = total + 40;
		Table.logger.debug("Preferred size established: {} , {}", total,
				this.preferredSizeInRows * fontMetrics.getHeight());
		this.setPreferredSize(new Dimension(total, this.preferredSizeInRows * this.table.getRowHeight()));// fontMetrics.getHeight()));

		if (this.table != null) {
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final TableColumn tc = this.table.getColumn(this.table.getColumnName(i));
				tc.setWidth(widths[i]);
			}
		}
	}

	/**
	 * Sets the preferred table width.
	 */
	protected void setPreferredTableWidth() {
		int preferredWidth = 0;
		for (int i = 1; i < this.table.getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(this.table.convertColumnIndexToModel(i));
			preferredWidth += tc.getPreferredWidth();
		}
		this.setPreferredSize(
				new Dimension(Math.min(600, preferredWidth), this.preferredSizeInRows * this.table.getRowHeight()));// fontMetrics.getHeight()));
	}

	protected List getOperationColumns() {
		final List vColumns = this.getOriginalSumRowCols();
		final List vVisibleColumns = this.getOriginallyVisibleColumns();
		TableCellRenderer renderer = null;
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String sKey = this.table.getColumnName(i);
			final Class columnClass = this.table.getColumnClass(i);
			if ((columnClass != null) && Number.class.isAssignableFrom(columnClass)) {
				if (!vColumns.contains(sKey)) {
					if (vVisibleColumns.contains(sKey)) {
						renderer = this.getRendererForColumn(sKey);
						if ((renderer != null) && ((renderer instanceof BooleanCellRenderer)
								|| (renderer instanceof ComboReferenceCellRenderer))) {
							continue;
						}
						vColumns.add(sKey);
					}
				}
			}
		}
		this.columnsToOperate = vColumns;
		return vColumns;
	}

	/**
	 * Sets values for the table. The <code>value</code> must be a Hashtable, an EntityResult or an
	 * AdvancedEntityResult.
	 *
	 * @see #setInnerValue
	 */
	@Override
	public void setValue(final Object value) {

		if (SwingUtilities.isEventDispatchThread()) {
			this.hideInformationPanel();
			this.setInnerValue(value);
		} else {
			final Object oValue = value;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Table.this.hideInformationPanel();
						Table.this.setInnerValue(oValue);
					}
				});
			} catch (final Exception e) {
				Table.logger.trace(null, e);
				this.setInnerValue(oValue);
			}
		}
	}

	/**
	 * Sets values for the table. The <code>value</code> must be a Hashtable, an EntityResult or an
	 * AdvancedEntityResult.
	 * @param value the value to set
	 * @param autoSizeColumns if true, the column size will be adjusted to its new contents
	 */
	public void setValue(final Object value, final boolean autoSizeColumns) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.setInnerValue(value, autoSizeColumns);
		} else {
			final Object oValue = value;
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Table.this.setInnerValue(oValue, autoSizeColumns);
					}
				});
			} catch (final Exception e) {
				Table.logger.trace(null, e);
				this.setInnerValue(oValue, autoSizeColumns);
			}
		}
	}

	protected void retrieveSQLTypes(final EntityResult res) {
		// Then use this information in pageable tables.
		if ((this.pageFetcher != null) && this.pageFetcher.isPageableEnabled()) {
			final Map<String, Integer> sqlTypes = res.getColumnSQLTypes();
			if (sqlTypes != null) {
				final Enumeration<String> keys = Collections.enumeration(sqlTypes.keySet());
				while (keys.hasMoreElements()) {
					final String key = keys.nextElement();
					if (!this.hColumnSQLTypes.containsKey(key)) {
						this.hColumnSQLTypes.put(key, sqlTypes.get(key));
					}
				}
			}
		}
	}

	/**
	 * Sets the data that the table will handle, this is, the columns information. This method also
	 * forces to fit the column size to its contents.
	 *
	 * @see #setInnerValue(Object, boolean)
	 * @param value the data
	 */
	protected void setInnerValue(final Object value) {
		this.setInnerValue(value, true);
	}

	/**
	 * Sets the information the table will manage. The information must be a Map instance, where
	 * keys are the column names, and values must be vectors containing the information corresponding to
	 * each column. All vectors must have the same size. Only the information keys that are configured
	 * as table attributes in the table definition will be processed.
	 * @param value new information to be displayed and managed by the table
	 * @param autoSizeColumns when true, the columns size will be adjusted to the column contents
	 */
	protected void setInnerValue(final Object value, final boolean autoSizeColumns) {

		if (this.dynamicTable) {
			this.setDynamicTableConfiguration(value);
		}

		this.checkRefreshThread();

		final long t = System.currentTimeMillis();
		this.changeToTableView();
		if ((value == null) || (!(value instanceof Map))) {
			this.deleteData();
		} else {
			if (value instanceof EntityResult) {
				this.retrieveSQLTypes((EntityResult) value);
			}

			/*
			 * The AdvancedEntityResult is deprecated
			 */
			if (value instanceof AdvancedEntityResult) {
				if (this.pageFetcher != null) {
					this.pageFetcher.setPageableEnabled(true);
					this.pageFetcher.refreshLabel((AdvancedEntityResult) value);
				}
			} else {
				if (this.pageFetcher != null) {
					this.pageFetcher.setPageableEnabled(false);
				}
			}

			if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
				this.table.getCellEditor().stopCellEditing();
			}

			// Remove values that not belongs to the table attributes
			final Map hData = ObjectTools.clone((Map) value);
			final Enumeration enumKeys = Collections.enumeration(hData.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				if ((!this.attributes.contains(oKey)) && !oKey.equals(this.keyField)) {
					hData.remove(oKey);
				}
			}
			final boolean fitRowHeight = ((EJTable) this.table).isFitRowsHeight();
			try {
				((EJTable) this.table).setFitRowsHeight(false);
				((TableSorter) this.table.getModel()).setData(hData);
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
				throw new RuntimeException(ex.getMessage(), ex);
			} finally {
				((EJTable) this.table).setFitRowsHeight(fitRowHeight);
			}

			final boolean pref = this.evaluateColumnsWidthAndPosition();
			if (!pref) {
				if (autoSizeColumns) {
					this.initColumnsWidth();
				}
			}
			this.setVisibleColumns();
			final long td = System.currentTimeMillis();
			Table.logger.trace("Table: Final: Initiating column width {}", td - t);
			this.evaluatePreferredRowsHeight();
		}

		if (this.dynamicTable) {
			this.checkNumberColumnVisibility(value);
		}

		if (this.isInsertingEnabled()) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
		}

		if (this.isOperationInMemory()) {
			try {
				final Entity ent = ApplicationManager.getApplication()
						.getReferenceLocator()
						.getEntityReference(this.getEntityName());
				if ((ent instanceof DynamicMemoryEntity) && (value instanceof Map)) {
					final EntityResult data = new EntityResultMapImpl(EntityResult.OPERATION_SUCCESSFUL,
							EntityResult.BEST_COMPRESSION);
					final Map hValue = (Map) value;
					final Set entries = hValue.entrySet();
					for (final Object entry : entries) {
						final Map.Entry current = (Map.Entry) entry;
						data.put(current.getKey(), current.getValue());
					}
					((DynamicMemoryEntity) ent).setValue(data);
				}
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
			}
		}
	}

	protected void checkNumberColumnVisibility(final Object value) {
		// to hide the row number columns when the table is empty
		if (value instanceof Map) {
			final Map h = (Map) value;
			if ((h == null) || h.isEmpty()
					|| ((h instanceof EntityResult) && (((EntityResult) h).calculateRecordNumber() == 0))) {
				this.setRowNumberColumnVisible(false);
			} else {
				boolean visible = true;
				if ((ApplicationManager.getApplication().getPreferences() != null) && (this.getParentForm() != null)
						&& (this.getParentForm().getFormManager() != null)) {
					final String preferenceValue = ApplicationManager.getApplication()
							.getPreferences()
							.getPreference(
									((ClientReferenceLocator) this.getParentForm().getFormManager().getReferenceLocator())
									.getUser(),
									BasicApplicationPreferences.SHOW_TABLE_NUM_ROW);
					visible = ParseUtils.getBoolean(preferenceValue, true);
				}
				this.setRowNumberColumnVisible(visible);
			}
		}
	}

	protected void setDynamicTableConfiguration(final Object value) {
		if (value instanceof Map) {
			final Map h = (Map) value;
			if ((h == null) || h.isEmpty()) {
				// this means the Map is empty
				// 1remove the filtering and the sorting and the grouping
				this.resetGroup();
				this.resetFilter();
				this.resetOrder();
				// 2 remove the columns
				final List attributesClone = ObjectTools.clone(this.attributes);
				if (attributesClone.size() > 0) {
					final String[] columns = (String[]) attributesClone.toArray(new String[attributesClone.size()]);
					this.deleteColumn(columns);
				}
				// for (int i = 0; i < attributesClone.size(); i++) {
				// this.deleteColumn(attributesClone.get(i).toString());
				// }

			} else {
				// we have to check whether the new columns are the same to the
				// old
				// ones just replace the values
				final List hashtableKeys = value == null ? new Vector() : new Vector(Arrays.asList(h.keySet().toArray()));
				final List attributesClone = ObjectTools.clone(this.attributes);
				Collections.sort(hashtableKeys);
				Collections.sort(attributesClone);
				if (!hashtableKeys.equals(attributesClone)) {
					// the new values and the columns in the table are different
					// we need to
					// remove the old sorting
					this.resetGroup();
					this.resetFilter();
					this.resetOrder();
					// and delete the table columns

					if (attributesClone.size() > 0) {
						final String[] columns = (String[]) attributesClone.toArray(new String[attributesClone.size()]);
						this.deleteColumn(columns);
					}
					// for (int i = 0; i < attributesClone.size(); i++) {
					// this.deleteColumn(attributesClone.get(i).toString());
					// }

					// and now we can add the new values
					final Map hValue = (Map) value;

					Iterator eKeys = null;
					if (value instanceof EntityResult) {
						final List orderColumns = ((EntityResult) value).getOrderColumns();
						if ((orderColumns == null) || orderColumns.isEmpty()) {
							eKeys = hValue.keySet().iterator();
						} else {
							eKeys = orderColumns.iterator();
						}
					} else {
						eKeys = hValue.keySet().iterator();
					}

					final List<String> columnNames = new ArrayList<String>();
					while (eKeys.hasNext()) {
						final Object key = eKeys.next();
						columnNames.add(key.toString());
						// this.addColumn(key.toString());
					}
					if (columnNames.size() > 0) {
						final String[] columns = columnNames.toArray(new String[columnNames.size()]);
						this.addColumn(columns);
					}
				}
			}
		}
	}

	/**
	 * Returns the table model. In this case returns an instance of {@link TableSorter}.
	 * @return the table model
	 */
	@Override
	public Object getValue() {
		this.checkRefreshThread();
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).getData();
	}

	/**
	 * Returns the information contained in the table. Returns the information in the TableSorter.
	 *
	 * @see TableSorter#getShowedValue
	 * @return
	 */
	public Object getShownValue() {
		this.checkRefreshThread();
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).getShownValue();
	}

	public Object getShownValue(final String cols[]) {
		this.checkRefreshThread();
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).getShownValue(cols);
	}

	/**
	 * Returns the table attribute. The attribute for a table is an object that is an
	 * {@link TableAttribute} instance.
	 */
	@Override
	public Object getAttribute() {
		if (this.dynamicTable) {
			return this.entity;
		} else {
			final TableAttribute tableAttribute = new TableAttribute();
			tableAttribute.setEntityAndAttributes(this.entity, ObjectTools.clone(this.attributes));

			if (this.pageFetcher != null) {
				tableAttribute.setRecordNumberToInitiallyDownload(this.pageFetcher.getPageFetcherRecordNumber());
				this.pageFetcher.setPageSize(this.pageFetcher.getPageFetcherRecordNumber());
			} else {
				tableAttribute.setRecordNumberToInitiallyDownload(this.rowsNumberToQuery);
			}

			tableAttribute.setKeysParentkeysOtherkeys(this.getKeys(), this.getParentKeys());
			if (this.rowsNumberToQuery >= 0) {
				tableAttribute.setOrderBy(this.getSQLOrderList());
			}
			tableAttribute.setParentkeyEquivalences(this.hParentkeyEquivalences);
			return tableAttribute;
		}
	}

	protected List getSQLOrderList() {
		final String[] colOrd = this.getOrderColumns();
		final boolean[] asce = this.getAscendents();
		final List orderBy = new Vector();
		for (int i = 0; i < colOrd.length; i++) {
			orderBy.add(new SQLStatementBuilder.SQLOrder(colOrd[i], asce[i]));
		}
		return orderBy;
	}

	/**
	 * Provides the name of the configured entity for the table.
	 * @return the entity name
	 */
	public String getEntityName() {
		if (this.operationInMemory && (this.memoryEntity != null)) {
			return this.memoryEntity;
		}
		return this.entity;
	}

	@Override
	public String getLabelComponentText() {
		return ApplicationManager.getTranslation(this.entity, this.resourcesFile);
	}

	/**
	 * Sets whether the table is editable. If <code>showHorizontalLines</code> is true it does; if it is
	 * false it doesn't.
	 * @param editable
	 */
	public void setEditable(final boolean editable) {
		if (!editable) {
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				this.table.setDefaultEditor(this.table.getColumnClass(i), null);
			}
			this.table.setDefaultEditor(BigDecimal.class, null);
			this.table.setDefaultEditor(BigInteger.class, null);
			this.table.setDefaultEditor(Long.class, null);
			this.table.setDefaultEditor(Integer.class, null);
			this.table.setDefaultEditor(Integer.class, null);
			this.table.setDefaultEditor(Short.class, null);
			this.table.setDefaultEditor(Double.class, null);
			this.table.setDefaultEditor(Float.class, null);
			this.table.setDefaultEditor(java.util.Date.class, null);
			this.table.setDefaultEditor(String.class, null);
			this.table.setDefaultEditor(Object.class, null);
			this.table.setDefaultEditor(Boolean.class, null);
		} else {
			Table.logger.debug("Table cannot be editable!: Use setColumnEditor");
		}
	}

	/**
	 * Returns all the primary keys. Deprecated, use instead {@link #getAllPrimaryKeys}
	 * @deprecated
	 * @see #getAllPrimaryKeys
	 */
	@Deprecated
	public List getPrimaryKeys() {
		this.checkRefreshThread();
		final Map hAllKeys = this.getAllPrimaryKeys();
		if (hAllKeys.containsKey(this.keyField)) {
			return (List) hAllKeys.get(this.keyField);
		} else {
			return new Vector(0);
		}
	}

	/**
	 * Returns all the values for the columns configured as key and keys for the table. In the returned
	 * Hashtable, the keys will be the columns mentioned previously, and the values, all the values in
	 * the table corresponding to those columns.
	 * @return the table primary keys values
	 */
	public Map getAllPrimaryKeys() {
		this.checkRefreshThread();
		final Map h = new Hashtable();
		// Search the column name in the table header
		final List vKeys = this.getKeys();
		for (int i = 0; i < vKeys.size(); i++) {
			final TableColumn tc = this.table.getColumn(vKeys.get(i));
			if (tc != null) {
				final int columnModelIndex = tc.getModelIndex();
				final TableModel tableModel = this.table.getModel();
				final TableSorter tableSorter = (TableSorter) tableModel;
				int rowCount = tableSorter.getRowCount();
				// if (tableSorter.isSum()) {
				// rowCount = rowCount - 1;
				// }
				if (this.isInsertingEnabled()) {
					rowCount = rowCount - 1;
				}
				final List vKeyValues = new Vector();
				for (int j = 0; j < rowCount; j++) {
					vKeyValues.add(vKeyValues.size(), ((TableSorter) tableModel).getValueAt(j, columnModelIndex));
				}
				h.put(vKeys.get(i), vKeyValues);
			}
		}
		return h;
	}

	/**
	 * Returns all the information in the table for all the attributes, this is, for all the columns in
	 * the table.
	 * @return the table columns information
	 */
	public Map getAttributesAndKeysData() {
		this.checkRefreshThread();
		final Map h = new Hashtable();
		// Search the column in the table header
		final List vKeys = this.getKeys();
		for (int i = 0; i < this.attributes.size(); i++) {
			if (!vKeys.contains(this.attributes.get(i))) {
				vKeys.add(this.attributes.get(i));
			}
		}

		for (int i = 0; i < vKeys.size(); i++) {
			try {
				final TableColumn tableColumn = this.table.getColumn(vKeys.get(i));
				if (tableColumn != null) {
					final int columnModelIndex = tableColumn.getModelIndex();
					final TableModel tableModel = this.table.getModel();
					final TableSorter tableSorter = (TableSorter) tableModel;
					int rowCount = tableSorter.getRowCount();
					// if (tableSorter.isSum()) {
					// rowCount = rowCount - 1;
					// }
					if (tableSorter.isInsertingEnabled()) {
						rowCount = rowCount - 1;
					}
					final List vKeyValues = new Vector();
					for (int j = 0; j < rowCount; j++) {
						vKeyValues.add(vKeyValues.size(), ((TableSorter) tableModel).getValueAt(j, columnModelIndex));
					}
					h.put(vKeys.get(i), vKeyValues);
				}
			} catch (final Exception e) {
				Table.logger.error("getAttributesAndKeysData error", e);
			}
		}
		return h;
	}

	/**
	 * Provides the value of the 'key' parameter, configured in the xml that defines the table.
	 * @return the key attribute value
	 */
	public String getKeyFieldName() {
		return this.keyField;
	}

	/**
	 * Returns all the configured keys for the table, this is, the column names correponding to the
	 * attributes key and keys.
	 * @return the configured keys for the table
	 */
	public List getKeys() {
		if (this.keyFields != null) {
			final List v = ObjectTools.clone(this.keyFields);
			if (!v.contains(this.keyField)) {
				v.add(this.keyField);
			}
			return v;
		} else {
			final List v = new Vector();
			v.add(this.keyField);
			return v;
		}
	}

	/**
	 * Returns the value set in the XML for the 'parentkey' attribute.
	 *
	 * @see #init
	 * @return the value set in the XML for the 'parentkey' attribute
	 * @deprecated
	 */
	@Deprecated
	public String getParentKeyFieldName() {
		return this.parentKey;
	}

	/**
	 * Returns all text keys the table stores, and that must be translated in order to internationalize
	 * the component. Text such as tips, menu keys and user messages are returned.
	 * @return all text keys the table stores
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		for (int i = 0; i < this.attributes.size(); i++) {
			v.add(this.attributes.get(i).toString());
		}
		v.add(Table.COPY_CELL);
		v.add(Table.COPY_SELECTION);
		v.add(Table.PRINTING_SELECTION);
		v.add(Table.REFRESH);
		v.add(Table.SHOW_HIDE_CONTROLS);
		v.add(Table.resetOrderKey);
		v.add(Table.TOTAL);
		v.add(Table.RECORDS);
		v.add(Table.IT_IS_SHOWED);
		v.add(Table.HEAD_TIP_COD);
		v.add(Table.FILTER_HEAD_TIP_COD);
		v.add(Table.SORT_HEAD_TIP_COD);

		v.add(Table.DOWN_10);
		v.add(Table.DOWN_100);
		v.add(Table.DOWN_50);
		v.add(Table.DOWN_ALL);
		v.add(this.detailFormTitleKey);
		v.add(Table.detailKey);
		v.add(Table.editKey);
		v.add(Table.insertKey);
		v.add(Table.TIP_CLIPBOARD_COPY);
		v.add(Table.TIP_EXCEL_EXPORT);
		v.add(Table.TIP_HTML_EXPORT);
		v.add(Table.TIP_PRINTING);
		v.add(Table.TIP_CHART_MENU);
		v.add(Table.TIP_VISIBLES_COLS_SETUP);

		v.add(Table.M_PRINTING_CANCELED);
		v.add(Table.M_ERROR_PRINTING_TABLE);
		v.add(Table.M_PRINTING_FINISHED);
		v.add(Table.M_WOULD_YOU_LIKE_TO_DELETE_ROWS);
		v.add(Table.M_SELECTION_ONLY_ONE_ROW_TO_OPEN_DETAIL_FORM);

		v.add(Table.M_SELECTION_LESS_THAN_5_ROWS);
		v.add(Table.openInNewWindowKey);
		if (this.detailForm != null) {
			v.addAll(this.detailForm.getTextsToTranslate());
		}
		return v;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourcesFile = resourceBundle;
		this.setTip();
		this.setTextsMenu();

		// Attributes contains the key fields
		// Set the header texts excepts in a dynamic table
		if (this.translateHeader) {
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final TableColumn tableColumn = this.table.getColumnModel().getColumn(i);
				final Object id = tableColumn.getIdentifier();
				tableColumn.setHeaderValue(ApplicationManager.getTranslation((String) id, resourceBundle));
			}
		}
		if (!this.prefWidthAndPosApply) {
			this.initColumnsWidth();
		}
		this.setVisibleColumns();
		if (this.detailForm != null) {
			this.detailForm.setResourceBundle(resourceBundle);
		}

		if (this.formatPattern != null) {
			this.formatPattern.setResourceBundle(resourceBundle);
		}

		for (int i = 0; i < this.attributes.size(); i++) {
			final TableColumn col = this.table.getColumn(this.attributes.get(i));
			final TableCellRenderer renderer = col.getCellRenderer();
			if (renderer instanceof Internationalization) {
				((Internationalization) renderer).setResourceBundle(resourceBundle);
			}
			final TableCellEditor editor = col.getCellEditor();
			if (editor instanceof Internationalization) {
				((Internationalization) editor).setResourceBundle(resourceBundle);
			}
		}
		final TableModel model = this.table.getModel();// Propagate bundle
		if ((model != null) && (model instanceof TableSorter)) {
			((TableSorter) model).setResourceBundle(resourceBundle);
		}

		if (this.quickFilterText != null) {
			this.quickFilterText.setResourceBundle(resourceBundle);
		}

		if (this.deleteListener != null) {
			this.deleteListener.setResourceBundle(resourceBundle);
		}

		// Use the bundle to translate the added components that implements
		// Internationalization

		// if (this.addButtons != null) {
		// for (int i = 0; i < this.addButtons.size(); i++) {
		// if (this.addButtons.get(i) instanceof Internationalization) {
		// ((Internationalization)
		// this.addButtons.get(i)).setResourceBundle(resourceBundle);
		// }
		// }
		// }

		if ((this.buttonPlus != null) && (this.buttonPlus instanceof Internationalization)) {
			((Internationalization) this.buttonPlus).setResourceBundle(resourceBundle);
		}

		if ((this.buttonPlus2 != null) && (this.buttonPlus2 instanceof Internationalization)) {
			((Internationalization) this.buttonPlus2).setResourceBundle(resourceBundle);
		}

		if (this.addComponents != null) {
			for (int i = 0; i < this.addComponents.size(); i++) {
				if (this.addComponents.get(i) instanceof Internationalization) {
					((Internationalization) this.addComponents.get(i)).setResourceBundle(resourceBundle);
				}
			}
		}

		this.setScrollTooltipText();

		if (this.pageFetcher != null) {
			this.pageFetcher.setResourceBundle(resourceBundle);
		}

	}

	/**
	 * A table is considered empty when it has no records. If the table has records, the method returns
	 * false.
	 * @return true if the table has no records
	 */
	@Override
	public boolean isEmpty() {
		if ((this.table.getRowCount() <= 0) || ((this.table.getRowCount() == 1) && this.isInsertingEnabled())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns always true. Interface implementation.
	 * @return true.
	 */
	@Override
	public boolean isModifiable() {
		return true;
	}

	/**
	 * Returns always false. Interface implementation.
	 * @return false.
	 */
	@Override
	public boolean isRequired() {
		return false;
	}

	/**
	 * Empty method. Interface implementation.
	 */
	@Override
	public void setRequired(final boolean req) {
	}

	/**
	 * Empty method. Interface implementation.
	 */
	@Override
	public void setModifiable(final boolean modif) {
	}

	/**
	 * Returns the index of the first selected row in the view.
	 * @return
	 */
	public int getSelectedRow() {
		return this.table.getSelectedRow();
	}

	/**
	 * Returns the row index primary keys.
	 * @param rowIndex
	 * @return
	 * @deprecated
	 */
	@Deprecated
	public Object getRowKey(final int rowIndex) {
		this.checkRefreshThread();
		final Object oKey = this.getPrimaryKeys().get(rowIndex);
		return oKey;
	}

	/**
	 * Returns the value of one single key, for the specified row. This is, returns the value of a
	 * single cell in the table, when the cell is a key.
	 * @param rowIndex the index of the row
	 * @param keyName the key name
	 * @return the key value for that row
	 */
	public Object getRowKey(final int rowIndex, final String keyName) {
		this.checkRefreshThread();
		final Map h = this.getAllPrimaryKeys();
		final List v = (List) h.get(keyName);
		if (v != null) {
			return v.get(rowIndex);
		} else {
			return null;
		}
	}

	/**
	 * Returns the value stored in a concrete column and row in the column model.
	 * @param rowIndex the row index in the model
	 * @param modelColumnIndex the column index in the model
	 * @return
	 */
	public Object getRowValue(final int rowIndex, final String modelColumnIndex) {
		this.checkRefreshThread();
		final TableColumn tc = this.table.getColumn(modelColumnIndex);
		if (tc != null) {
			final int columnModelIndex = tc.getModelIndex();
			final TableModel model = this.table.getModel();
			final TableSorter m = (TableSorter) model;

			final int rowCount = m.getRowCount();
			if ((rowIndex < 0) || (rowIndex >= rowCount)) {
				return null;
			}
			return ((TableSorter) model).getValueAt(rowIndex, columnModelIndex);
		} else {
			return null;
		}
	}

	/**
	 * @see #hasSumRow
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public boolean existsSumRow() {
		final TableModel model = this.table.getModel();
		final TableSorter m = (TableSorter) model;
		return m.isSum();
	}

	/**
	 * Returns the values of the keys for the index specified. The result is a {@link #Hashtable}
	 * containing the keys as the Map keys, and the key value in the TableModel as value for each
	 * entry in the Hashtable.
	 * <p>
	 * In case that the asked key is not present in the model, the key will not be in the result
	 * Hashtable.
	 * @param rowIndex the model row index that specifies the row to query
	 * @return a Hastable with the key values. The value will be null if the rowIndex is bigger than the
	 *         total amount of records in the grid.
	 */
	public Map getRowKeys(final int rowIndex) {
		this.checkRefreshThread();
		final Map rowKeys = new Hashtable();
		final List vKeys = this.getKeys();
		final TableSorter tableSorter = this.getTableSorter();
		final int rowCount = tableSorter.getRowCount();
		// if (tableSorter.isSum()) {
		// rowCount = rowCount - 1;
		// }
		// null when there are no results
		if (rowIndex >= rowCount) {
			return null;
		}

		for (int i = 0; i < vKeys.size(); i++) {
			final TableColumn tc = this.table.getColumn(vKeys.get(i));
			final int columnModelIndex = tc.getModelIndex();
			if (tc != null) {
				final Object value = tableSorter.getValueAt(rowIndex, columnModelIndex);
				if (value != null) {
					rowKeys.put(vKeys.get(i), value);
				}

			}
		}
		return rowKeys;
	}

	/**
	 * Deletes the rows specified as parameter.
	 * @param rowIndex an array with the model indexes to delete
	 */
	public void deleteRows(final int[] rowIndex) {
		if ((rowIndex == null) || (rowIndex.length == 0)) {
			return;
		}

		this.checkRefreshThread();
		final TableModel tableModel = this.table.getModel();
		if (SwingUtilities.isEventDispatchThread()) {
			if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
				this.table.getCellEditor().stopCellEditing();
			}
			((TableSorter) tableModel).deleteRows(rowIndex);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						if ((Table.this.table != null) && Table.this.table.isEditing()
								&& (Table.this.table.getCellEditor() != null)) {
							Table.this.table.getCellEditor().stopCellEditing();
						}
						((TableSorter) tableModel).deleteRows(rowIndex);
					}
				});
			} catch (final Exception e) {
				Table.logger.error("deleteRows: ", e);
			}
		}

	}

	/**
	 * Deletes the row specified as parameter
	 * @param rowIndex the index of the row to delete in the Model
	 */
	public void deleteRow(final int rowIndex) {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			// Inserting row can not be deleted
			return;
		}
		this.checkRefreshThread();
		final TableModel tableModel = this.table.getModel();
		if (SwingUtilities.isEventDispatchThread()) {
			if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
				this.table.getCellEditor().stopCellEditing();
			}
			((TableSorter) tableModel).deleteRow(rowIndex);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						if ((Table.this.table != null) && Table.this.table.isEditing()
								&& (Table.this.table.getCellEditor() != null)) {
							Table.this.table.getCellEditor().stopCellEditing();
						}
						((TableSorter) tableModel).deleteRow(rowIndex);
					}
				});
			} catch (final Exception e) {
				Table.logger.error("deleteRow ", e);
			}
		}

	}

	/**
	 * Deletes the row which key is passed as parameter.
	 * @deprecated
	 * @see #deleteRow(Map)
	 */
	@Deprecated
	public void deleteRow(final Object key) {
		this.checkRefreshThread();
		final TableModel tableModel = this.table.getModel();
		final int f = this.getPrimaryKeys().indexOf(key);
		if (f >= 0) {
			if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
				this.table.getCellEditor().stopCellEditing();
			}
			((TableSorter) tableModel).deleteRow(f);
		}
	}

	/**
	 * Deletes the row specified as parameter
	 * @param keysValues stores pairs key-value that defines the record to delete
	 */
	public void deleteRow(final Map keysValues) {
		this.checkRefreshThread();
		for (int i = 0; i < this.table.getRowCount(); i++) {
			boolean keysMatch = true;
			final List v = this.getKeys();
			for (int j = 0; j < v.size(); j++) {
				final int iColIndex = this.table.convertColumnIndexToView(this.table.getColumn(v.get(j)).getModelIndex());
				final Object oTableValue = this.table.getValueAt(i, iColIndex);
				final Object oKeysValues = keysValues.get(v.get(j));
				if (!oKeysValues.equals(oTableValue)) {
					keysMatch = false;
					break;
				}
			}
			if (keysMatch) {
				if ((this.table != null) && this.table.isEditing()
						&& (this.table.getCellEditor() != null)) {
					this.table.getCellEditor().stopCellEditing();
				}
				this.deleteRow(i);
				return;
			}
		}
	}

	/**
	 * Returns the row view index specified by the keys passed as parameter.
	 * @param keysValues the values for the keys that defines the queried row.
	 * @return the view row index of the coincidence, -1 otherwise
	 */
	public int getRowForKeys(final Map keysValues) {
		this.checkRefreshThread();
		// Get the first key
		final List v = this.getKeys();
		if (v.isEmpty()) {
			return -1;
		}
		final Object oKey1 = v.get(0);
		final Object oKeyValue1 = keysValues.get(oKey1);
		if (oKeyValue1 == null) {
			Table.logger.debug("Table: getRowForKeys: keys doesn't contain value for: " + oKey1);
			return -1;
		}
		final int keyCOlumnIndex1 = this.table.convertColumnIndexToView(this.table.getColumn(oKey1).getModelIndex());
		// Now search for matches in table values
		for (int i = 0; i < this.table.getRowCount(); i++) {
			final Object oValue = this.table.getValueAt(i, keyCOlumnIndex1);
			if (oKeyValue1.equals(oValue)) {
				// Check the other keys
				boolean allKeysMatch = true;
				for (int j = 1; j < v.size(); j++) {
					final Object oKeyj = v.get(j);
					final Object oKeyValuej = keysValues.get(oKeyj);
					final int columnIndexj = this.table.convertColumnIndexToView(this.table.getColumn(oKeyj).getModelIndex());
					final Object oTableValuej = this.table.getValueAt(i, columnIndexj);
					if (oKeyValuej == null) {
						if (Table.logger.isDebugEnabled()) {
							this.parentForm.message(SwingUtilities.getWindowAncestor(this),
									" Value for the key " + oKeyj + " to search in rows is null : " + keysValues,
									Form.ERROR_MESSAGE);
						}
						allKeysMatch = false;
						break;
					}
					if (!oKeyValuej.equals(oTableValuej)) {
						allKeysMatch = false;
						break;
					}
				}
				if (allKeysMatch) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * For each Map passed as parameter in the array, the method will search the row in which the
	 * keys have the same value that the ones stored in the Hashtable.
	 * <p>
	 * Each Map must have, for every element, as key a column name (that is, a table's key) and as
	 * value the concrete value corresponding to that key.
	 * <p>
	 * The indexes of all these coincidences will be returned in the response int array.
	 * @param keysValuesToQuery an array of {@link #Hashtable} that stores in each element the
	 *        keys-values combination to search
	 * @return the indexes of the rows that matches the keys in the param
	 */
	public int[] getRowsForKeys(final Map[] keysValuesToQuery) {
		this.checkRefreshThread();

		if ((keysValuesToQuery == null) || (keysValuesToQuery.length == 0)) {
			return null;
		}
		final int[] result = new int[keysValuesToQuery.length];
		// Get the first key in the vector
		final List v = this.getKeys();
		if (v.isEmpty()) {
			return null;
		}
		final int[] keyColumnsViewIndex = new int[v.size()];
		for (int i = 0; i < v.size(); i++) {
			final int keyColumnIndex = this.table.convertColumnIndexToView(this.table.getColumn(v.get(i)).getModelIndex());
			keyColumnsViewIndex[i] = keyColumnIndex;
		}

		final ArrayList[] keyList = new ArrayList[v.size()];
		for (int i = 0; i < v.size(); i++) {
			keyList[i] = new ArrayList();
		}

		for (int i = 0; i < this.table.getRowCount(); i++) {
			for (int j = 0; j < v.size(); j++) {
				final int columnIndex = keyColumnsViewIndex[j];
				final Object tableValueColumnIndex = this.table.getValueAt(i, columnIndex);
				keyList[j].add(i, tableValueColumnIndex);
			}
		}

		for (int i = 0; i < keysValuesToQuery.length; i++) {
			result[i] = -1;
			final Map hKeysI = keysValuesToQuery[i];
			final Object oKey1 = v.get(0);
			final Object oKeyValue1 = hKeysI.get(oKey1);
			final List column0ValueList = keyList[0];
			for (int j = 0; j < column0ValueList.size(); j++) {
				final Object oValue = column0ValueList.get(j);
				if (oKeyValue1.equals(oValue)) {
					// Check the other keys
					boolean allKeysMatch = true;
					for (int k = 1; k < v.size(); k++) {
						final Object oKeyk = v.get(k);
						final Object oKeyValuek = hKeysI.get(oKeyk);
						final List columnkValueList = keyList[k];
						final Object oTableValuek = columnkValueList.get(j);
						if (!oKeyValuek.equals(oTableValuek)) {
							allKeysMatch = false;
							break;
						}
					}
					if (allKeysMatch) {
						result[i] = j;
						break;
					}
				}
			}

		}

		return result;
	}

	/**
	 * Maps the index of the row in the view to the index of the row in the table model. Returns the
	 * index of the corresponding row in the model. If viewColumnIndex is less than zero, returns
	 * viewColumnIndex.
	 * @param viewRowIndex the row index in the view
	 * @return the index of the corresponding row in the model
	 */
	public int convertRowIndexToModel(final int viewRowIndex) {
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).convertRowIndexToModel(viewRowIndex);
	}

	/**
	 * Selects a row in the table
	 * @param viewRowIndex the index of the row in the view
	 */
	public void setSelectedRow(final int viewRowIndex) {
		this.checkRefreshThread();
		if (viewRowIndex < 0) {
			this.table.clearSelection();
			return;
		} else if (viewRowIndex > (this.table.getRowCount() - 1)) {
			return;
		}

		this.table.setRowSelectionInterval(viewRowIndex, viewRowIndex);

		// We have to ensure that the selected row is visible
		final Rectangle cellRectangle = this.table.getCellRect(viewRowIndex, 1, false);
		this.table.scrollRectToVisible(cellRectangle);
	}

	/**
	 * If the rowIndex is <0 the selection is removed and false is returned. In case the index is > than
	 * the getRowCount, only a false is returned. Finally, if the index is right, a true is returned.
	 * @param rowIndex
	 * @return
	 */
	protected boolean checkBoundRow(final int rowIndex) {
		if (rowIndex < 0) {
			this.table.clearSelection();
			return false;
		} else if (rowIndex > (this.table.getRowCount() - 1)) {
			this.table.clearSelection();
			return false;
		}
		return true;
	}

	/**
	 * Selects one or more rows in the table
	 * @param viewRowIndex [] indexes of the rows in the view
	 */

	public void setSelectedRows(final int[] viewRowIndex) {
		this.checkRefreshThread();

		// if there are not values, the method returns:
		if ((viewRowIndex == null) || (viewRowIndex.length == 0)) {
			return;
			// // if viewRowIndex is a single value, the column for this index
			// must
			// be
			// selected
			// if (viewRowIndex.length == 1) {
			// if (checkBoundRow(viewRowIndex[0])) {
			// this.setSelectedRow(viewRowIndex[0]);
			// }
			// return;
			// }
			// if viewRowIndex contains several values, the columns which their
			// index
			// matchs with each value of the List viewRowIndex must be
			// selected.
			// So:
		}

		// 1.- checks that all the values of viewRowIndex are a possible index
		// value:
		for (int i = 0; i < viewRowIndex.length; i++) {
			if (!this.checkBoundRow(viewRowIndex[i])) {
				return;
			}
		}

		// 2.- selects all the rows (<=>the first row is setted and the rest are
		// added of the first one):
		this.table.setRowSelectionInterval(viewRowIndex[0], viewRowIndex[0]);
		for (int i = 1; i < viewRowIndex.length; i++) {
			this.table.addRowSelectionInterval(viewRowIndex[i], viewRowIndex[i]);
		}
		// 3.- We have to ensure that last selected row is visible (at least
		// one)
		final Rectangle cellRect = this.table.getCellRect(viewRowIndex[viewRowIndex.length - 1], 1, false);
		if (cellRect != null) {
			this.table.scrollRectToVisible(cellRect);
		}
	}

	/**
	 * Sets the element locale
	 * @param the new locale to set
	 * @see Locale
	 */
	@Override
	public void setComponentLocale(final Locale locale) {
		this.locale = locale;
		for (int i = 0; i < this.attributes.size(); i++) {
			final TableColumn col = this.table.getColumn(this.attributes.get(i));
			final TableCellRenderer renderer = col.getCellRenderer();
			if (renderer instanceof Internationalization) {
				((Internationalization) renderer).setComponentLocale(locale);
			}

			final TableCellEditor editor = col.getCellEditor();
			if (editor instanceof Internationalization) {
				((Internationalization) editor).setComponentLocale(locale);
			}
		}

		if (this.calculedColumns != null) {
			final Enumeration calculedKeys = Collections.enumeration(this.calculedColumns.keySet());
			while (calculedKeys.hasMoreElements()) {
				final TableColumn col = this.table.getColumn(calculedKeys.nextElement());
				final TableCellRenderer renderer = col.getCellRenderer();
				if (renderer instanceof Internationalization) {
					((Internationalization) renderer).setComponentLocale(locale);
				}

				final TableCellEditor editor = col.getCellEditor();
				if (editor instanceof Internationalization) {
					((Internationalization) editor).setComponentLocale(locale);
				}
			}
		}

		TableCellRenderer renderer = this.table.getDefaultRenderer(Timestamp.class);
		if (renderer instanceof Internationalization) {
			((Internationalization) renderer).setComponentLocale(locale);
		}
		renderer = this.table.getDefaultRenderer(java.util.Date.class);
		if (renderer instanceof Internationalization) {
			((Internationalization) renderer).setComponentLocale(locale);
		}

		renderer = this.table.getDefaultRenderer(java.lang.Number.class);
		if (renderer instanceof Internationalization) {
			((Internationalization) renderer).setComponentLocale(locale);
		}

		TableCellEditor editor = this.table.getDefaultEditor(Timestamp.class);
		if (editor instanceof Internationalization) {
			((Internationalization) editor).setComponentLocale(locale);
		}
		editor = this.table.getDefaultEditor(java.util.Date.class);

		if (editor instanceof Internationalization) {
			((Internationalization) editor).setComponentLocale(locale);
		}

		editor = this.table.getDefaultEditor(java.lang.Number.class);

		if (editor instanceof Internationalization) {
			((Internationalization) editor).setComponentLocale(locale);
		}

		editor = this.table.getDefaultEditor(java.lang.Number.class);
		if (editor instanceof Internationalization) {
			((Internationalization) editor).setComponentLocale(locale);
		}

		if (this.detailForm != null) {
			this.detailForm.setComponentLocale(locale);
		}
		this.setLocale(locale);
		this.setSumRowTableComponentLocale(locale);
		this.table.revalidate();
		this.table.repaint();

	}

	protected void setSumRowTableComponentLocale(final Locale locale) {
		if (this.sumRowTable != null) {

			for (int i = 0; i < this.attributes.size(); i++) {
				final TableColumn col = this.sumRowTable.getColumn(this.attributes.get(i));
				final TableCellRenderer renderer = col.getCellRenderer();
				if (renderer instanceof Internationalization) {
					((Internationalization) renderer).setComponentLocale(locale);
				}

				final TableCellEditor editor = col.getCellEditor();
				if (editor instanceof Internationalization) {
					((Internationalization) editor).setComponentLocale(locale);
				}
			}

			if (this.calculedColumns != null) {
				final Enumeration calculedKeys = Collections.enumeration(this.calculedColumns.keySet());
				while (calculedKeys.hasMoreElements()) {
					final TableColumn col = this.sumRowTable.getColumn(calculedKeys.nextElement());
					final TableCellRenderer renderer = col.getCellRenderer();
					if (renderer instanceof Internationalization) {
						((Internationalization) renderer).setComponentLocale(locale);
					}

					final TableCellEditor editor = col.getCellEditor();
					if (editor instanceof Internationalization) {
						((Internationalization) editor).setComponentLocale(locale);
					}
				}
			}

			TableCellRenderer renderer = this.sumRowTable.getDefaultRenderer(Timestamp.class);
			if (renderer instanceof Internationalization) {
				((Internationalization) renderer).setComponentLocale(locale);
			}
			renderer = this.sumRowTable.getDefaultRenderer(java.util.Date.class);
			if (renderer instanceof Internationalization) {
				((Internationalization) renderer).setComponentLocale(locale);
			}

			TableCellEditor editor = this.sumRowTable.getDefaultEditor(Timestamp.class);
			if (editor instanceof Internationalization) {
				((Internationalization) editor).setComponentLocale(locale);
			}
			editor = this.sumRowTable.getDefaultEditor(java.util.Date.class);
			if (editor instanceof Internationalization) {
				((Internationalization) editor).setComponentLocale(locale);
			}

			this.sumRowTable.revalidate();
			this.sumRowTable.repaint();
		}
	}

	/**
	 * Determines if the object is enabled.
	 * @return true if object is enabled; otherwise, false
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	protected void setQuickFilterEnabled(final boolean enabled) {
		if (this.quickFilterText != null) {
			if (this.quickFilterVisible) {
				this.quickFilterText.setEnabled(enabled);
			} else {
				this.quickFilterText.setVisible(this.controlsVisible && enabled);
			}
		}
	}

	/**
	 * Sets whether or not this component is enabled.
	 * @param enabled true if this component should be enabled, false otherwise
	 */
	@Override
	public void setEnabled(final boolean enabled) {
		final boolean previousState = this.enabled;
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				return;
			}
		}

		this.changeToTableView();
		this.enabled = enabled;
		this.controlsPanel.setVisible(this.controlsVisible && enabled);

		this.setQuickFilterEnabled(enabled);

		for (int i = 0; i < this.addButtons.size(); i++) {
			((AbstractButton) this.addButtons.get(i)).setEnabled(enabled);
		}

		for (int i = 0; i < this.addComponents.size(); i++) {
			((JComponent) this.addComponents.get(i)).setEnabled(enabled);
		}

		if ((this.formName != null) || ((this.formName == null) && (this.insertFormName != null))) {
			final boolean permission = this.checkInsertPermission();
			if (this.disableInsert) {
				this.disableInsert();
			} else {
				if (permission) {
					this.buttonPlus.setEnabled(enabled);
					this.setTableComponentEnabled(Table.BUTTON_PLUS, enabled);
					// this.setTableComponentVisible(Table.BUTTON_PLUS,
					// enabled);
				}
			}
		}

		this.setTableComponentEnabled(Table.BUTTON_REFRESH, enabled);
		this.setTableComponentEnabled(Table.BUTTON_VISIBLE_COLS_SETUP, enabled);
		this.setTableComponentEnabled(Table.BUTTON_SAVE_FILTER_ORDER_SETUP, enabled);
		this.setTableComponentEnabled(Table.BUTTON_PIVOTTABLE, enabled);

		this.enableFiltering(enabled);
		this.enableSort(enabled);
		if (this.table != null) {
			this.table.setEnabled(enabled);
		}

		if (this.sumRowTable != null) {
			this.sumRowTable.setEnabled(enabled);
			this.sumRowScrollPane.setEnabled(enabled);
			if (enabled) {
				this.sumRowScrollPane.setVisible(enabled);
			}
		}

		if (this.sumRowBlockedTable != null) {
			this.sumRowBlockedTable.setEnabled(enabled);
			this.sumRowBlockedScrollPane.setEnabled(enabled);
			if (enabled) {
				this.sumRowBlockedScrollPane.setVisible(enabled);
				this.fixBlockedVisibility();
			}
		}

		this.evaluateButtonsState();
		if ((!this.isFiltered()) && (previousState != enabled)) {
			this.setDefaultFilter();
		}
		if (this.isInsertingEnabled() && (this.table.getModel() instanceof TableSorter)) {
			if ((this.getParentForm() != null) && (this.getParentForm().getInteractionManager() != null)
					&& (this.getParentForm().getInteractionManager().getCurrentMode() == InteractionManager.UPDATE)
					&& this.table.isEnabled()
					&& ((TableSorter) this.table.getModel()).isInsertingRow(0)) {
				this.evaluatePreferredRowsHeight();
			} else {
				((TableSorter) this.getJTable().getModel())
				.fireTableChanged(new TableModelEvent(this.getJTable().getModel()));
			}
		}

		if (this.pageFetcher != null) {
			this.pageFetcher.setEnabled(enabled);
		}

		this.scrollPane.setEnabled(enabled);
		if (this.scrollPane.getVerticalScrollBar().isVisible()) {
			this.scrollPane.getVerticalScrollBar().setEnabled(enabled);
		}
		if (this.scrollHorizontal && this.scrollPane.getHorizontalScrollBar().isVisible()) {
			this.scrollPane.getHorizontalScrollBar().setEnabled(enabled);
		}
	}

	/**
	 * Enables or disables the table button depending on the table state. The table buttons panel will
	 * be enabled only in case the table is enabled and also has records in it. The buttons will be
	 * disabled in other case.
	 */
	protected void evaluateButtonsState() {
		if ((this.table != null) && !this.table.isEnabled()) {
			this.setControlButtonsEnabled(false);
			return;
		}
		if (this.table != null) {
			final int rowsCount = this.table.getRowCount();
			if (rowsCount > 0) {
				this.setControlButtonsEnabled(true);
			} else {
				this.setControlButtonsEnabled(false);
			}
		}
	}

	/**
	 * Returns always false. Interface implementation.
	 * @return false
	 */
	@Override
	public boolean isHidden() {
		return false;
	}

	/**
	 * Sets the frame that will be used as parent of the dialogs.
	 * @param frame the frame that will be used as parent frame for the dialogs.
	 */
	@Override
	public void setParentFrame(final Frame frame) {
		this.parentFrame = frame;
	}

	/**
	 * Sets the {@link FormBuilder} that the table will use to build the detail form
	 * @param builder the {@link FormBuilder} that will be used to create the detail form
	 */
	@Override
	public void setFormBuilder(final FormBuilder builder) {
		this.formBuilder = builder;
	}

	/**
	 * Provides a reference to the JTable element contained in the table.
	 * @return the JTable inside the table
	 */
	public JTable getJTable() {
		return this.table;
	}

	/**
	 * Sets the Form in which the table is placed. This method is call automatically when the table is
	 * created.
	 * @param form the form that contains the table
	 */
	@Override
	public void setParentForm(final Form form) {
		this.parentForm = form;
		this.setEditable(false);

		// Configure the parent form in all renderers and editors
		this.configureComponentsParentForm(this.getAllColumnRenderer(), form);
		this.configureComponentsParentForm(this.getAllColumnEditors(), form);

		if ((this.formName != null) && this.backgroundDetailFormBuilder) {
			this.detailFormBuilder = new DetailFormBuilder(this);
			BackgroundFormBuilderManager.buildDetailForm(this.detailFormBuilder);
		}

		if ((this.insertFormName != null) && !this.insertFormName.equals(this.formName)
				&& this.backgroundDetailFormBuilder) {
			this.insertDetailFormBuilder = new InsertDetailFormBuilder(this);
			BackgroundFormBuilderManager.buildDetailForm(this.insertDetailFormBuilder);
		}
	}

	/**
	 * Returns a reference to the Form in which the table is placed.
	 *
	 * @see #Form
	 * @return a reference to the Form in which the table is placed
	 */
	public Form getParentForm() {
		return this.parentForm;
	}

	/**
	 * Returns the data contained by the asked columns. The response is a {@link #Hashtable} in which
	 * the keys will be the columns asked as a method parameter, and the values are {@link #Vector} of
	 * String containing the printable data for each column.
	 * <p>
	 * If the column asked is not in the table, the response will not contain that column.
	 * <p>
	 * The values in the response will be renderized to convert the value stored in the table to the
	 * String suitable to be printed, according to the locale and data type.
	 * @param askedColumns the column names
	 * @return the values of each column
	 */
	public Map getPrintingData(final List askedColumns) {
		this.checkRefreshThread();
		final Map hData = new Hashtable();
		final RealDataField realDataField = new RealDataField(new Hashtable(0));
		final DateDataField dateDataField = new DateDataField(new Hashtable(0));
		final IntegerDataField integerDataField = new IntegerDataField(new Hashtable(0));
		final CurrencyDataField currencyDataField = new CurrencyDataField(new Hashtable(0));
		realDataField.setComponentLocale(this.locale);
		dateDataField.setComponentLocale(this.locale);
		integerDataField.setComponentLocale(this.locale);
		currencyDataField.setComponentLocale(this.locale);
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String sColumnName = this.table.getColumnName(i);
			if (askedColumns.contains(sColumnName)) {
				if (this.attributes.contains(sColumnName)) {
					final List vColumnData = new Vector();
					final long t = System.currentTimeMillis();
					long tRendersTotal = 0;
					long tGetValueTotal = 0;
					int iterations = 0;
					for (int j = 0; j < this.table.getRowCount(); j++) {
						iterations++;
						if ((this.printingWindow != null)
								&& (iterations >= (this.printingWindow.progressBar.getMaximum() / 20))) {
							if (this.printingWindow != null) {
								this.printingWindow.progressBar
								.setValue(this.printingWindow.progressBar.getValue() + iterations);
							}
							if (this.printingWindow != null) {
								this.printingWindow.progressBar.paintImmediately(0, 0,
										this.printingWindow.progressBar.getWidth(),
										this.printingWindow.progressBar.getHeight());
							}
							iterations = 0;
						}
						// Use the apropriate renderer component
						final long t1 = System.currentTimeMillis();
						final Object oValue = this.table.getValueAt(j, i);
						tGetValueTotal += System.currentTimeMillis() - t1;
						if (oValue == null) {
							vColumnData.add(oValue);
						} else {
							final long t2 = System.currentTimeMillis();
							if (this.currencyColumns.contains(sColumnName)) {
								currencyDataField.setValue(oValue);
								final String sText = new String(((JTextField) currencyDataField.getDataField()).getText());
								vColumnData.add(sText);
								tRendersTotal += System.currentTimeMillis() - t2;
							} else {
								// Segun tipo de dato, el render.
								if ((oValue instanceof java.sql.Date) || (oValue instanceof java.util.Date)) {
									dateDataField.setValue(oValue);
									final String sText = new String(((JTextField) dateDataField.getDataField()).getText());
									vColumnData.add(sText);
									tRendersTotal += System.currentTimeMillis() - t2;
									continue;
								} else if (oValue instanceof Integer) {
									integerDataField.setValue(oValue);
									final String sText = new String(((JTextField) integerDataField.getDataField()).getText());
									vColumnData.add(sText);
									tRendersTotal += System.currentTimeMillis() - t2;
									continue;
								} else if ((oValue instanceof Double) || (oValue instanceof Float)) {
									realDataField.setValue(oValue);
									final String sText = new String(((JTextField) realDataField.getDataField()).getText());
									vColumnData.add(sText);
									tRendersTotal += System.currentTimeMillis() - t2;
									continue;
								} else if (oValue instanceof Boolean) {
									String sText = "";
									if (((Boolean) oValue).booleanValue()) {
										sText = "Yes";
									} else {
										sText = "No";
									}
									vColumnData.add(sText);
									tRendersTotal += System.currentTimeMillis() - t2;
									continue;
								}
								// In a different case text
								vColumnData.add(oValue);
								tRendersTotal += System.currentTimeMillis() - t2;
							}
						}
					}

					Table.logger.trace("Table: Render column time - {}: {}", sColumnName,
							System.currentTimeMillis() - t);
					Table.logger.trace("Table: Accumulated render time: {}", tRendersTotal);
					Table.logger.trace("Table: Accumulated GetValue time : ", tGetValueTotal);

					hData.put(this.table.getColumnName(i), vColumnData);
				}
			}
		}
		return hData;
	}

	/**
	 * Returns a String with all the information displayed in the corresponding row, for each table
	 * attributes on that row. The data contained by each cell is rendered in order to have a legible
	 * result.
	 * @param rowIndex
	 * @return the data contained in the specified row
	 */
	protected String getRowText(final int rowIndex) {
		final Map hData = new Hashtable();
		final RealDataField realDataField = new RealDataField(new Hashtable(0));
		final DateDataField dateDataField = new DateDataField(new Hashtable(0));
		final IntegerDataField integerDataField = new IntegerDataField(new Hashtable(0));
		final CurrencyDataField currencyDataField = new CurrencyDataField(new Hashtable(0));
		realDataField.setComponentLocale(this.locale);
		dateDataField.setComponentLocale(this.locale);
		integerDataField.setComponentLocale(this.locale);
		currencyDataField.setComponentLocale(this.locale);
		for (int i = 0; i < this.attributes.size(); i++) {
			final String sColumnName = this.attributes.get(i).toString();
			final List vColumnData = (List) ((Map) this.getValue()).get(sColumnName);
			final Object oValue = vColumnData.get(rowIndex);
			if (this.currencyColumns.contains(sColumnName)) {
				currencyDataField.setValue(oValue);
				final String sText = new String(((JTextField) currencyDataField.getDataField()).getText());
				hData.put(this.attributes.get(i), sText);
			} else {
				// Renderer to use depends on the data type.
				if ((oValue instanceof java.sql.Date) || (oValue instanceof java.util.Date)) {
					dateDataField.setValue(oValue);
					final String sText = new String(((JTextField) dateDataField.getDataField()).getText());
					hData.put(this.attributes.get(i), sText);
					continue;
				} else if (oValue instanceof Integer) {
					integerDataField.setValue(oValue);
					final String sText = new String(((JTextField) integerDataField.getDataField()).getText());
					vColumnData.add(sText);
					hData.put(this.attributes.get(i), sText);
					continue;
				} else if ((oValue instanceof Double) || (oValue instanceof Float)) {
					realDataField.setValue(oValue);
					final String sText = new String(((JTextField) realDataField.getDataField()).getText());
					vColumnData.add(sText);
					hData.put(this.attributes.get(i), sText);
					continue;
				} else if (oValue instanceof Boolean) {
					String sText = "";
					if (((Boolean) oValue).booleanValue()) {
						sText = "Yes";
					} else {
						sText = "No";
					}
					vColumnData.add(sText);
					hData.put(this.attributes.get(i), sText);
					continue;
				}
				hData.put(this.attributes.get(i), oValue.toString());
			}
		}
		return hData.toString();
	}

	/**
	 * Returns a {@link #Hashtable} with all the data of the visible columns.
	 *
	 * @see #getPrintingData(List)
	 * @return a {@link #Hashtable} with all the data of the visible columns.
	 */
	public Map getPrintingData() {
		this.checkRefreshThread();
		int rejected = 0;
		final List orderColumnNames = new Vector();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			// Get the column
			final TableColumn tableColumn = this.table.getColumnModel().getColumn(i);
			Table.logger.debug("{}", tableColumn.getIdentifier());
			if ((tableColumn.getMaxWidth() > 1) && tableColumn.getResizable()) {
				// this means that is visible
				final String attribute = tableColumn.getIdentifier().toString();
				orderColumnNames.add(i - rejected, attribute);
			} else {
				rejected++;
			}
		}
		return this.getPrintingData(orderColumnNames);
	}

	/**
	 * Liberates the memory occupied by the table component.
	 */
	@Override
	public void free() {
		for (int i = 0; i < this.table.getColumnModel().getColumnCount(); i++) {
			final TableColumn tc = this.table.getColumnModel().getColumn(i);
			for (final PropertyChangeListener propertyChangeListeners : tc.getPropertyChangeListeners()) {
				tc.removePropertyChangeListener(propertyChangeListeners);
			}
		}
		final TableModel previousModel = this.table.getModel();
		if ((previousModel != null) && (previousModel instanceof TableSorter)) {
			try {
				((TableSorter) previousModel).free();
			} catch (final Exception e) {
				Table.logger.error("free ", e);
			}
		}
		if (this.tipScroll != null) {
			this.tipScroll.dispose();
			this.tipScroll = null;
		}
		FreeableUtils.freeObject(this.rHead);
		FreeableUtils.freeObject(this.rDate);
		FreeableUtils.freeObject(this.rRowHead);
		FreeableUtils.freeObject(this.rBoolean);
		FreeableUtils.freeObject(this.rReal);
		FreeableUtils.freeObject(this.rObject);

		FreeableUtils.freeComponent(this.table);
		this.table = null;
		this.parentFrame = null;
		this.resourcesFile = null;
		this.attributes.clear();

		this.attributes = null;

		this.buttonCopy = null;
		this.buttonExcelExport = null;
		this.buttonHTMLExport = null;
		this.buttonPrint = null;
		this.buttonPlus = null;
		this.buttonPlus2 = null;
		this.primaryKey.clear();
		this.primaryKey = null;
		this.formBuilder = null;
		this.detailForm = null;
		this.parentForm = null;
		this.controlsPanel = null;
		this.scrollPane = null;
		this.table = null;
		this.buttonSumRowSetup = null;
		if (this.printingWindow != null) {
			this.printingWindow.dispose();
		}
		this.printingWindow = null;
		Table.logger.debug("Free method");
		if (this.insertDetailForm != null) {
			this.insertDetailForm.free();
			this.insertDetailForm = null;
		}
		this.parentForm = null;
		FreeableUtils.freeComponent(this.controlsPanel);
		this.charsetIso88591 = null;
		this.buttonChangeListener = null;
		FreeableUtils.freeComponent(this.lInfoFilter);
		FreeableUtils.freeComponent(this.quickFilterText);
		FreeableUtils.freeComponent(this.buttonChangeView);
		FreeableUtils.freeComponent(this.buttonCopy);
		FreeableUtils.freeComponent(this.buttonExcelExport);
		FreeableUtils.freeComponent(this.buttonHTMLExport);
		FreeableUtils.freeComponent(this.buttonPlus);
		FreeableUtils.freeComponent(this.buttonPlus2);
		FreeableUtils.freeComponent(this.groupTableButton);
		FreeableUtils.freeComponent(this.buttonPrint);
		FreeableUtils.freeComponent(this.buttonDelete);
		FreeableUtils.freeComponent(this.buttonRefresh);
		FreeableUtils.freeComponent(this.buttonChart);
		FreeableUtils.freeComponent(this.buttonDefaultChart);
		FreeableUtils.freeComponent(this.buttonVisibleColsSetup);
		FreeableUtils.freeComponent(this.buttonSumRowSetup);
		FreeableUtils.freeComponent(this.buttonReports);
		FreeableUtils.freeComponent(this.menuReportSetup);
		FreeableUtils.freeComponent(this.buttonSaveFilterOrderSetup);
		FreeableUtils.freeComponent(this.buttonCalculatedColumns);
		// protected List addButtons = new Vector(2, 2);
		// protected List addComponents = new Vector(2, 2);
		FreeableUtils.freeComponent(this.printingWindow);
		this.locale = null;
		this.resourcesFile = null;
		FreeableUtils.clearMap(this.hAttributesToFixEquivalences);
		FreeableUtils.clearCollection(this.attributesToFix);
		FreeableUtils.clearCollection(this.currencyColumns);
		FreeableUtils.clearCollection(this.hourRenderColumns);
		FreeableUtils.clearCollection(this.editableColumnsUpdateEntity);
		FreeableUtils.clearCollection(this.editableColumns);
		FreeableUtils.freeComponent(this.filter);
		FreeableUtils.freeComponent(this.menu);
		FreeableUtils.freeComponent(this.chartMenu);
		FreeableUtils.clearCollection(this.menuChartItems);
		FreeableUtils.clearCollection(this.chartGraphMenuItems);
		// protected IChartUtilities chartUtilities;
		FreeableUtils.freeComponent(this.menuDetail);
		FreeableUtils.freeComponent(this.menuInsert);
		FreeableUtils.freeComponent(this.menuPrintSelection);
		FreeableUtils.freeComponent(this.menuCopyCell);
		FreeableUtils.freeComponent(this.menuCopySelection);
		FreeableUtils.freeComponent(this.menuRefresh);
		FreeableUtils.freeComponent(this.menuResetOrder);
		FreeableUtils.freeComponent(this.menuOpenInNewWindow);
		FreeableUtils.freeComponent(this.menuFilter);
		FreeableUtils.freeComponent(this.menuFilterByValue);
		FreeableUtils.freeComponent(this.menuDeleteColumnFilter);
		FreeableUtils.freeComponent(this.menuDeleteFilter);
		FreeableUtils.freeComponent(this.menuGroup);
		FreeableUtils.freeComponent(this.menuGroupDate);
		FreeableUtils.freeComponent(this.menuGroupByYear);
		FreeableUtils.freeComponent(this.menuGroupByYearMonth);
		FreeableUtils.freeComponent(this.menuGroupYearMonthDay);
		FreeableUtils.freeComponent(this.menuGroupByQuarterYear);
		FreeableUtils.freeComponent(this.menuGroupByWeekYear);
		FreeableUtils.freeComponent(this.menuGroupByQuarter);
		FreeableUtils.freeComponent(this.menuGroupByMonth);
		FreeableUtils.freeComponent(this.menuGroupFunction);
		FreeableUtils.freeComponent(this.menuSumFunction);
		FreeableUtils.freeComponent(this.menuAvgFunction);
		FreeableUtils.freeComponent(this.menuMaxFunction);
		FreeableUtils.freeComponent(this.menuMinFunction);
		FreeableUtils.freeComponent(this.menuCountFunction);
		FreeableUtils.freeComponent(this.menuDeleteGroup);
		FreeableUtils.freeComponent(this.menuPageableEnabled);
		FreeableUtils.freeComponent(this.filterWindow);
		FreeableUtils.freeComponent(this.menuShowHideControls);
		this.defaultFilter = null;
		FreeableUtils.clearMap(this.hColumnSQLTypes);
		this.eventPress = null;
		this.detailFormBuilder = null;
		this.insertDetailFormBuilder = null;
		this.reserOrderListener = null;

		this.addRecordListener = null;

		FreeableUtils.clearCollection(this.visibleColumns);
		FreeableUtils.clearCollection(this.defaultVisibleColumns);
		FreeableUtils.clearCollection(this.originalVisibleColumns);
		FreeableUtils.clearCollection(this.reportCols);
		FreeableUtils.clearMap(this.calculedColumns);
		FreeableUtils.clearCollection(this.originalCalculatedColumns);
		FreeableUtils.clearCollection(this.requiredColumnsCalculedColumns);
		FreeableUtils.clearCollection(this.columnsToOperate);
		FreeableUtils.clearCollection(this.columnsToSum);
		this.currencyRenderer = null;
		this.hourRenderer = null;
		this.attributeTable = null;
		this.pageFetcher = null;
		this.dynamicFormManager = null;
		this.refreshThread = null;
		if (this.tAux != null) {
			this.tAux.free();
			this.tAux = null;
		}
		this.visiblePermission = null;
		this.enabledPermission = null;
		this.insertPermission = null;
		this.queryPermission = null;
		this.tableComponentPermission = null;
		this.tableColumnPermission = null;
		this.actButtonsModelListener = null;
		FreeableUtils.freeComponent(this.tipScroll);
		FreeableUtils.freeComponent(this.sumRowSetupDialog);
		FreeableUtils.freeComponent(this.visibleColsSetupDialog);
		this.popupListener = null;
		this.selectionListener = null;

	}

	/**
	 * Returns the table SQL type. In this case the type is {@link java.sql.Types.OTHER}
	 */
	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

	/**
	 * Empty method. Default interface implementation. The table cannot be set modified.
	 * @param modif
	 */
	public void setModified(final boolean modif) {
	}

	/**
	 * Returns always false. The table cannot be modified. Interface implementation.
	 * @return false
	 */
	@Override
	public boolean isModified() {
		return false;
	}

	/**
	 * Returns a {@link #Vector} with the column names of all the visible columns. The column order is
	 * the same they have in the ColumnModel
	 * @return a {@link #Vector} containing the visible column names in the same order in which they are
	 *         been displayed
	 */
	public List getOrderColumnName() {
		final List sortColumnNames = new Vector();
		int rejeted = 0;
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			// Get the column
			final TableColumn tableColumn = this.table.getColumnModel().getColumn(i);
			Table.logger.debug("OrderColumnName: {}", tableColumn.getIdentifier());
			if ((tableColumn.getMaxWidth() > 1) && tableColumn.getResizable()) {
				// the column is visible
				final String sAttribute = tableColumn.getIdentifier().toString();
				sortColumnNames.add(i - rejeted, sAttribute);
			} else {
				rejeted++;
			}
		}
		return sortColumnNames;
	}

	/**
	 * Disables the table insert button, '+', even though the table has a detail form.
	 */
	public void disableInsert() {
		if (this.buttonPlus != null) {
			this.buttonPlus.setEnabled(false);
			this.buttonPlus.setVisible(false);
			if (this.buttonPlus2 != null) {
				this.buttonPlus2.setEnabled(false);
				this.buttonPlus2.setVisible(false);
			}
		}
		if (this.menuInsert != null) {
			this.menuInsert.setEnabled(false);
		}
	}

	/**
	 * Enables the insert button, '+'. The user must have permissions to insert records.
	 *
	 * @see #checkInsertPermission
	 */
	public void enableInsert() {
		final boolean permission = this.checkInsertPermission();
		if (this.buttonPlus != null) {
			if (permission) {
				if (!this.disableInsert) {
					this.buttonPlus.setEnabled(true);
					this.buttonPlus.setVisible(true);
				}

				if (this.buttonPlus2 != null) {
					if (!this.disableInsert) {
						this.buttonPlus2.setVisible(true);
						this.buttonPlus2.setEnabled(true);
					}
				}
			} else {
				this.setTableComponentEnabled(Table.BUTTON_PLUS, false);
				this.buttonPlus.setEnabled(false);
				if (this.buttonPlus2 != null) {
					this.buttonPlus2.setEnabled(false);
					this.buttonPlus2.setVisible(false);
				}
			}
		}
		if ((this.menuInsert != null) && permission) {
			this.menuInsert.setEnabled(true);
		}
	}

	/**
	 * Returns the data contained in the specified row. The result is a {@link #Hashtable} with pairs
	 * key-value, where the keys are the column names, containing the row data. If the table is
	 * filtered, returns the data as well.
	 * @param rowIndex the model index for the column
	 * @return a {@link #Hashtable} with the row data
	 */
	public Map getRowData(final int rowIndex) {
		this.checkRefreshThread();
		final TableSorter model = (TableSorter) this.table.getModel();
		return model.getRowData(rowIndex);
	}

	public Map getRowDataForKeys(final Map keysValues) {
		this.checkRefreshThread();
		final TableSorter model = (TableSorter) this.table.getModel();
		return model.getRowDataForKeys(this.getKeys(), keysValues);
	}

	/**
	 * Returns the information corresponding to the row which index is passed as parameter.
	 * @param rowIndex the model row index
	 * @return the row information
	 */
	public Map getGroupedRowData(final int rowIndex) {
		this.checkRefreshThread();
		final TableSorter model = (TableSorter) this.table.getModel();
		return model.getGroupedRowData(rowIndex);
	}

	/**
	 * Returns a Map containing the data associated to the row calculated columns. The Hashtable
	 * keys are the calculated column names. The Map values are the values in the table
	 * corresponding to the row passed as parameter.
	 * @param rowIndex
	 * @return
	 */
	public Map getCalculatedRowData(final int rowIndex) {
		this.checkRefreshThread();
		final TableSorter model = (TableSorter) this.table.getModel();
		return model.getCalculatedRowData(rowIndex);
	}

	/**
	 * Enables or disables the sorting functionality.
	 * @param enable if true sorting is enabled. If false, the functionality will be disabled.
	 */
	public void enableSort(final boolean enable) {
		this.orderEnabled = enable;
		((TableSorter) this.table.getModel()).enableSort(enable);
		this.setTip();
	}

	/**
	 * Repaints the component.
	 */
	public void update() {
		this.update(this.getGraphics());
	}

	/**
	 * Sets the default renders to the class types.
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>class</b></td>
	 * <td><b>render</b></td>
	 * </tr>
	 * <tr>
	 * <td>Timestamp.class, java.sql.Date.class, java.util.Date.class</td>
	 * <td>{@link #DateCellRenderer}</td>
	 * </tr>
	 * <tr>
	 * <td>Boolean.class</td>
	 * <td>{@link #BooleanCellRenderer}</td>
	 * </tr>
	 * <tr>
	 * <td>Float.class, Double.class, Number.class</td>
	 * <td>{@link #RealCellRenderer}</td>
	 * </tr>
	 * <tr>
	 * <td>BytesBlock.class</td>
	 * <td>{@link #ImageCellRenderer}</td>
	 * </tr>
	 * <tr>
	 * <td>Object.class, String.class</td>
	 * <td>{@link #ObjectCellRenderer}</td>
	 * </tr>
	 * There are also renderers for the columns specified as currency, hour and memo.
	 * </Table>
	 *
	 * @see #JTable
	 */
	protected void setRenderers() {
		if (this.rDate == null) {
			this.rHead = new HeadCellRenderer();

			this.rDate = new DateCellRenderer();
			this.rRowHead = new RowHeadCellRenderer(this.table, this.parameters);

			this.rBoolean = new BooleanCellRenderer();
			this.rReal = new RealCellRenderer();
			this.rObject = new ObjectCellRenderer();
			final Map pImagen = new Hashtable(2);
			pImagen.put("keepaspectratio", "yes");
			pImagen.put("allowzoom", "no");
			pImagen.put("height", "50");
			this.rImagen = new ImageCellRenderer(pImagen);

		}

		this.table.setDefaultRenderer(Timestamp.class, this.rDate);
		this.table.setDefaultRenderer(RowHeadCellRenderer.class, this.rRowHead);
		this.table.setDefaultRenderer(java.sql.Date.class, this.rDate);
		this.table.setDefaultRenderer(java.util.Date.class, this.rDate);

		this.table.setDefaultRenderer(Boolean.class, this.rBoolean);
		this.table.setDefaultRenderer(Float.class, this.rReal);
		this.table.setDefaultRenderer(Double.class, this.rReal);
		this.table.setDefaultRenderer(Object.class, this.rObject);

		this.table.setDefaultRenderer(String.class, this.rObject);
		this.table.setDefaultRenderer(Number.class, this.rReal);
		this.table.setDefaultRenderer(BytesBlock.class, this.rImagen);

		for (int i = 0; i < this.currencyColumns.size(); i++) {
			final TableColumn col = this.table.getColumn(this.currencyColumns.get(i));
			if (col != null) {
				if (this.currencyRenderer == null) {
					this.currencyRenderer = new CurrencyCellRenderer();
					this.currencyRenderer.setFont(this.getFont());
				}
				// Use CurrencyCellRenderer with currency columns
				col.setCellRenderer(this.currencyRenderer);
			}
		}

		for (int i = 0; i < this.hourRenderColumns.size(); i++) {
			final TableColumn col = this.table.getColumn(this.hourRenderColumns.get(i));
			if (col != null) {
				if (this.hourRenderer == null) {
					this.hourRenderer = new DateCellRenderer(true);
					this.hourRenderer.setFont(this.getFont());
				}
				col.setCellRenderer(this.hourRenderer);
			}
		}

		for (int i = 0; i < this.memoRenderColumns.size(); i++) {
			final TableColumn col = this.table.getColumn(this.memoRenderColumns.get(i));
			if (col != null) {
				if (this.rMemo == null) {
					this.rMemo = new MemoCellRenderer();
					this.rMemo.setFont(this.getFont());
				}
				col.setCellRenderer(this.rMemo);
				col.addPropertyChangeListener(this.columnWidthListener);
				((EJTable) this.table).setFitRowsHeight(true);
			}
		}

		// SumRowTable
		if (this.sumRowTable != null) {
			this.sumRowTable.setDefaultRenderer(Boolean.class, this.rBoolean);
			this.sumRowTable.setDefaultRenderer(Float.class, this.rReal);
			this.sumRowTable.setDefaultRenderer(Double.class, this.rReal);
			this.sumRowTable.setDefaultRenderer(Object.class, this.rObject);

			this.sumRowTable.setDefaultRenderer(String.class, this.rObject);
			this.sumRowTable.setDefaultRenderer(Number.class, this.rReal);

			for (int i = 0; i < this.currencyColumns.size(); i++) {
				final TableColumn col = this.sumRowTable.getColumn(this.currencyColumns.get(i));
				if (col != null) {
					if (this.currencyRenderer == null) {
						this.currencyRenderer = new CurrencyCellRenderer();
						this.currencyRenderer.setFont(this.getFont());
					}
					// Use CurrencyCellRenderer with currency columns
					col.setCellRenderer(this.currencyRenderer);
				}
			}
		}
	}

	/**
	 * Configure the editors for currency columns and time columns
	 */
	protected void setEditors() {
		if (this.editableColumns != null) {
			for (int i = 0; i < this.editableColumns.size(); i++) {
				final String colName = (String) this.editableColumns.get(i);
				// Set the currency editors
				if ((this.currencyColumns != null) && this.currencyColumns.contains(colName)) {
					final Map h = new Hashtable();
					h.put(CellEditor.COLUMN_PARAMETER, colName);
					final CurrencyCellEditor editor = new CurrencyCellEditor(h);
					this.setColumnEditor(colName, editor);
				} else if ((this.hourRenderColumns != null) && this.hourRenderColumns.contains(colName)) {
					// Set the time editors
					final Map h = new Hashtable();
					h.put(CellEditor.COLUMN_PARAMETER, colName);
					final DateCellEditor editor = new DateCellEditor(h);
					this.setColumnEditor(colName, editor);
				}
			}
		}
	}

	/**
	 * Sets a concrete renderer for the specified table column. See the concrete render type
	 * configuration in order to create the renderer right. For example, if the renderer is a Reference
	 * one, the reference locator must be sat, as well the locale must be set in the internationalized
	 * ones, etc.
	 * @param column the column that will be configured with the renderer
	 * @param renderer the renderer to set
	 * @see #setRendererForColumnExp
	 */
	public void setRendererForColumn(final String column, final TableCellRenderer renderer) {
		this.checkRefreshThread();
		if ((!this.attributes.contains(column)) && (!this.calculedColumns.containsKey(column))
				&& !ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(column)) {
			Table.logger.info("setRendererFormColumn: Column not found : {}", column);
			return;
		}
		TableColumn col = this.table.getColumn(column);
		if (col != null) {

			col.setCellRenderer(renderer);

			if (renderer instanceof CellRenderer) {
				((CellRenderer) renderer).setCellRendererColorManager(this.cellRendererColorManager);
				((CellRenderer) renderer).setCellRendererFontManager(this.cellRendererFontManager);
			}
			if (renderer instanceof ComboReferenceCellRenderer) {
				((ComboReferenceCellRenderer) renderer).setCellRendererColorManager(this.cellRendererColorManager);
				((ComboReferenceCellRenderer) renderer).setCellRendererFontManager(this.cellRendererFontManager);
			}
			if (renderer instanceof MemoCellRenderer) {
				col.addPropertyChangeListener(this.columnWidthListener);
			}
			if (renderer instanceof ReferenceComponent) {
				// EntityReferenceLocator locator =
				// this.parentForm.getFormManager().getReferenceLocator();
				((ReferenceComponent) renderer).setReferenceLocator(this.locator);
			}
			if (renderer instanceof Internationalization) {
				((Internationalization) renderer).setComponentLocale(this.locale);
				((Internationalization) renderer).setResourceBundle(this.resourcesFile);
			}
			if (renderer instanceof AccessForm) {
				((AccessForm) renderer).setParentForm(this.parentForm);
			}
			if (renderer instanceof CachedComponent) {
				if (this.parentForm != null) {
					if (this.locator == null) {
						Table.logger.debug("Cannot set CacheManager to the renderer " + renderer.getClass()
						+ ", because locator is NULL");
					} else {
						CacheManager.getDefaultCacheManager(this.locator)
						.addCachedComponent((CachedComponent) renderer);
						((CachedComponent) renderer).setCacheManager(CacheManager.getDefaultCacheManager(this.locator));
					}
				} else {
					Table.logger.debug("Cannot set CacheManager to the renderer " + renderer.getClass()
					+ ", because parent form is NULL");
				}
			}
		}

		if (this.sumRowTable != null) {
			col = this.sumRowTable.getColumn(column);
			if (col != null) {
				col.setCellRenderer(renderer);
			}
		}

	}

	/**
	 * Returns the CellRenderer set to the column.
	 * @param column
	 * @return the CellRenderer set to the column
	 */
	public TableCellRenderer getRendererForColumn(final String column) {
		final TableColumn col = this.table.getColumn(column);
		if (col != null) {
			return col.getCellRenderer();
		} else {
			return null;
		}
	}

	public TableCellEditor getEditorForColumn(final String column) {
		this.checkRefreshThread();
		final TableColumn col = this.table.getColumn(column);
		if (col != null) {
			return col.getCellEditor();
		} else {
			return null;
		}
	}

	/**
	 * Returns the renderers that are being used by the table. The result is an array of renderers,
	 * containing the renderers in the order: <br>
	 * Boolean, Date, Real, Object, Image, Memo
	 * @return the renderers that is using the table
	 */
	public TableCellRenderer[] getDefaultRenderers() {
		this.checkRefreshThread();
		return new TableCellRenderer[] { this.rBoolean, this.rDate, this.rReal, this.rObject, this.rImagen,
				this.rMemo };
	}

	/**
	 * Sets a concrete renderer for the specified table column.
	 * @param column the column name
	 * @param renderer the renderer to be set
	 */
	public void setRendererForColumnExp(final String column, final TableCellRenderer renderer) {
		this.checkRefreshThread();
		final TableColumn col = this.table.getColumn(column);
		if (col != null) {
			col.setCellRenderer(renderer);
			if (renderer instanceof CellRenderer) {
				((CellRenderer) renderer).setCellRendererColorManager(this.cellRendererColorManager);
				((CellRenderer) renderer).setCellRendererFontManager(this.cellRendererFontManager);
			}
		}
	}

	/**
	 * Shows the currency symbol when {@link CurrencyCellRenderer} are used
	 */
	@Override
	public void showCurrencyValue(final String currencySymbol) {
		// Currency columns
		for (int i = 0; i < this.currencyColumns.size(); i++) {
			final TableColumn col = this.table.getColumn(this.currencyColumns.get(i));
			final TableCellRenderer renderer = col.getCellRenderer();
			if (renderer instanceof CurrencyCellRenderer) {
				((CurrencyCellRenderer) renderer).showCurrencyValue(currencySymbol);
			}
		}
		// Update the table
		final TableModel m = this.table.getModel();
		if ((m != null) && (m instanceof TableSorter)) {
			((TableSorter) m).fireTableChanged(new TableModelEvent(m));
		}
	}

	/**
	 * Enables or disables the filtering functionality.
	 * @param enable if true, the table allows to filter; if false, filtering will be disabled
	 */
	public void enableFiltering(final boolean enable) {
		if (this.activatedFilter != enable) {
			this.activatedFilter = enable;
			((TableSorter) this.table.getModel()).enableFiltering(enable);
			if (this.quickFilterText != null) {
				this.quickFilterText.setEnabled(enable);
				if (!enable) {
					this.quickFilterText.setText("");
				}
			}
			// tip update
			this.setTip();
		}
	}

	public boolean isFilteringEnabled() {
		return this.activatedFilter;
	}

	/**
	 * Configures and sets the table tips.
	 */
	protected void setTip() {
		String tip = null;
		try {
			if (this.orderEnabled && (this.activatedFilter)) {
				tip = ApplicationManager.getTranslation(Table.HEAD_TIP_COD, this.resourcesFile);
			} else if ((this.orderEnabled) && (!this.activatedFilter)) {
				tip = ApplicationManager.getTranslation(Table.SORT_HEAD_TIP_COD, this.resourcesFile);
			} else if ((!this.orderEnabled) && (this.activatedFilter)) {
				tip = ApplicationManager.getTranslation(Table.FILTER_HEAD_TIP_COD, this.resourcesFile);
			} else if ((!this.orderEnabled) && (!this.activatedFilter)) {
				tip = "";
			}

			final StringBuilder builder = new StringBuilder("<HTML>");
			builder.append(tip);
			if (this.isBlockedEnabled()) {
				builder.append("<BR>");
				builder.append("<BR>");
				builder.append(ApplicationManager.getTranslation(Table.BLOCKED_COLUMN_TIP_COD, this.resourcesFile));
			}
			builder.append("</HTML>");
			this.table.getTableHeader().setToolTipText(builder.toString());
			this.blockedTable.getTableHeader().setToolTipText(builder.toString());
		} catch (final Exception e) {
			Table.logger.error("setTip", e);
		}
	}

	/**
	 * Returns the value stored in the field, placed in the table's parent form, that is configured as
	 * the table parent key in the XML.
	 *
	 * @see Form#getDataFieldValue
	 * @see #getParentKeyFieldName
	 * @see #init
	 * @return the parent key value
	 * @deprecated
	 */
	@Deprecated
	public Object getParentKeyValue() {
		if (this.getParentKeyFieldName() != null) {
			return this.parentForm.getDataFieldValue(this.getParentKeyFieldName());
		} else {
			return null;
		}
	}

	/**
	 * Returns the name of the column appearing in the view at column position
	 * <code>viewColumnIndex</code>.
	 * @param viewColumnIndex the column in the view being queried
	 * @return the name of the column at position <code>column</code> in the view where the first column
	 *         is column 0
	 */
	public String getColumnName(final int viewColumnIndex) {
		return this.table.getColumnName(viewColumnIndex);
	}

	/**
	 * Creates and configures the popup menu.
	 *
	 * @see #showPopupMenu
	 */
	protected void createPopupMenu() {
		this.menu = new ExtendedJPopupMenu();
		this.scrollPane.addMouseListener(this.popupListener);
		this.table.addMouseListener(this.popupListener);
		this.blockedTable.addMouseListener(this.popupListener);

		final String sDetailText = ApplicationManager.getTranslation(Table.detailKey, this.resourcesFile);

		this.menuDetail = new JMenuItem(sDetailText);
		final ImageIcon detailIcon = ImageManager.getIcon(ImageManager.DETAIL);
		if (detailIcon != null) {
			this.menuDetail.setIcon(detailIcon);
		}
		this.menu.add(this.menuDetail);
		this.menuDetail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (Table.this.formName != null) {
					final Cursor c = Table.this.getCursor();
					// Open the detail form
					try {
						Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						if (Table.this.table.getSelectedRowCount() < 0) {
							return;
						}
						Table.this.detail(null);
					} catch (final Exception ex) {
						Table.logger.error("menuDetail", ex);
					} finally {
						Table.this.setCursor(c);
					}
				}
			}
		});

		final String sInsertText = ApplicationManager.getTranslation(Table.insertKey, this.resourcesFile);

		this.menuInsert = new JMenuItem(sInsertText);
		final ImageIcon insertIcon = ImageManager.getIcon(ImageManager.INSERT);
		if (insertIcon != null) {
			this.menuInsert.setIcon(insertIcon);
		}
		this.menu.add(this.menuInsert);
		this.menuInsert.addActionListener(this.addRecordListener);

		final String resetOrderText = ApplicationManager.getTranslation(Table.resetOrderKey, this.resourcesFile);

		this.menuResetOrder = new JMenuItem(resetOrderText);
		final ImageIcon resetOrderIcon = ImageManager.getIcon(ImageManager.RESET_ORDER);
		if (resetOrderIcon != null) {
			this.menuResetOrder.setIcon(resetOrderIcon);
		}
		this.menu.add(this.menuResetOrder);
		this.menuResetOrder.addActionListener(this.reserOrderListener);

		final String printSelectionText = ApplicationManager.getTranslation(Table.PRINTING_SELECTION, this.resourcesFile);

		this.menuPrintSelection = new JMenuItem(printSelectionText);
		final ImageIcon printIcon = ImageManager.getIcon(ImageManager.PRINT);
		if (printIcon != null) {
			this.menuPrintSelection.setIcon(printIcon);
		}
		this.menu.addSeparator();

		String sFilterText = ApplicationManager.getTranslation(Table.FILTER_COLUMN_es_ES, this.resourcesFile);
		this.menuFilter = new JMenuItem(sFilterText);
		this.menuFilter.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_ADD));
		this.menuFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertFilter();
			}
		});
		this.menu.add(this.menuFilter);

		final String sFilterByValueText = ApplicationManager.getTranslation(Table.FILTER_BY_VALUE_es_ES, this.resourcesFile);
		this.menuFilterByValue = new JMenuItem(sFilterByValueText);
		this.menuFilterByValue.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_ADD));
		this.menuFilterByValue.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertFilterByValue();
			}
		});
		this.menu.add(this.menuFilterByValue);

		sFilterText = ApplicationManager.getTranslation(Table.DELETE_FILTER_es_ES, this.resourcesFile);
		this.menuDeleteFilter = new JMenuItem(sFilterText);
		this.menuDeleteFilter.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_DELETE));
		this.menuDeleteFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.deleteFilter();
			}

		});

		sFilterText = ApplicationManager.getTranslation(Table.DELETE_FILTER_COLUMN, this.resourcesFile);
		this.menuDeleteColumnFilter = new JMenuItem(sFilterText);
		this.menuDeleteColumnFilter.setIcon(ImageManager.getIcon(ImageManager.FUNNEL_DELETE));
		this.menuDeleteColumnFilter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.deleteColumnFilter();
			}
		});

		this.menu.add(this.menuDeleteColumnFilter);
		this.menu.add(this.menuDeleteFilter);
		this.menu.addSeparator();

		// GROUP
		final String groupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_es_ES, this.resourcesFile);
		this.menuGroup = new JMenuItem(groupText);
		this.menuGroup.setIcon(ImageManager.getIcon(ImageManager.GROUP));

		this.menuGroup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup();
			}
		});

		final String dateGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_DATE_es_ES,
				this.resourcesFile);
		this.menuGroupDate = new JMenu(dateGroupText);
		this.menuGroupDate.setIcon(ImageManager.getIcon(ImageManager.GROUP));
		this.menu.add(this.menuGroupDate);

		final String yearGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_YEAR, this.resourcesFile);
		this.menuGroupByYear = new JMenuItem(yearGroupText);
		this.menuGroupDate.add(this.menuGroupByYear);

		this.menuGroupByYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.YEAR);
			}
		});

		final String monthGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_MONTH, this.resourcesFile);
		this.menuGroupByMonth = new JMenuItem(monthGroupText);
		this.menuGroupDate.add(this.menuGroupByMonth);

		this.menuGroupByMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.MONTH);
			}
		});

		final String monthYearGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_YEAR_MONTH,
				this.resourcesFile);
		this.menuGroupByYearMonth = new JMenuItem(monthYearGroupText);
		this.menuGroupDate.add(this.menuGroupByYearMonth);

		this.menuGroupByYearMonth.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.YEAR_MONTH);
			}
		});

		final String dayMonthYearGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_YEAR_MONTH_DAY,
				this.resourcesFile);
		this.menuGroupYearMonthDay = new JMenuItem(dayMonthYearGroupText);
		this.menuGroupDate.add(this.menuGroupYearMonthDay);

		this.menuGroupYearMonthDay.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.YEAR_MONTH_DAY);
			}
		});
		final String quarterYearGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_QUARTER_YEAR,
				this.resourcesFile);
		this.menuGroupByQuarterYear = new JMenuItem(quarterYearGroupText);
		this.menuGroupDate.add(this.menuGroupByQuarterYear);
		this.menuGroupByQuarterYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.QUARTER_YEAR);
			}
		});

		final String weekYearGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_WEEK_YEAR,
				this.resourcesFile);
		this.menuGroupByWeekYear = new JMenuItem(weekYearGroupText);
		this.menuGroupDate.add(this.menuGroupByWeekYear);
		this.menuGroupByWeekYear.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.WEEK_YEAR);

			}
		});

		final String quarterGroupText = ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_QUARTER,
				this.resourcesFile);
		this.menuGroupByQuarter = new JMenuItem(quarterGroupText);
		this.menuGroupDate.add(this.menuGroupByQuarter);
		this.menuGroupByQuarter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.insertGroup(TableSorter.QUARTER);
			}
		});

		this.menu.add(this.menuGroup);

		this.menuGroupFunction = new JMenu(
				ApplicationManager.getTranslation(Table.GROUP_FUNCTION, this.resourcesFile));
		this.menuGroupFunction.setIcon(ImageManager.getIcon(ImageManager.SUM));
		this.menuSumFunction = new JMenuItem(
				ApplicationManager.getTranslation(Table.SUM_es_ES, this.resourcesFile));
		this.menuAvgFunction = new JMenuItem(
				ApplicationManager.getTranslation(Table.AVERAGE_es_ES, this.resourcesFile));
		this.menuMaxFunction = new JMenuItem(
				ApplicationManager.getTranslation(Table.MAXIMUM_es_ES, this.resourcesFile));
		this.menuMinFunction = new JMenuItem(
				ApplicationManager.getTranslation(Table.MINIMUM_es_ES, this.resourcesFile));
		this.menuCountFunction = new JMenuItem(
				ApplicationManager.getTranslation(Table.COUNT_es_ES, this.resourcesFile));

		this.menuGroupFunction.add(this.menuSumFunction);
		this.menuGroupFunction.add(this.menuAvgFunction);
		this.menuGroupFunction.add(this.menuMaxFunction);
		this.menuGroupFunction.add(this.menuMinFunction);
		this.menuGroupFunction.add(this.menuCountFunction);
		this.menu.add(this.menuGroupFunction);

		this.menuSumFunction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int col = Table.this.table.convertColumnIndexToModel(Table.this.colPress);
				((TableSorter) Table.this.table.getModel()).setGroupedColumnFunction(col, TableSorter.SUM);
			}
		});

		this.menuAvgFunction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int col = Table.this.table.convertColumnIndexToModel(Table.this.colPress);
				((TableSorter) Table.this.table.getModel()).setGroupedColumnFunction(col, TableSorter.AVG);
			}
		});

		this.menuMaxFunction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int col = Table.this.table.convertColumnIndexToModel(Table.this.colPress);
				((TableSorter) Table.this.table.getModel()).setGroupedColumnFunction(col, TableSorter.MAX);
			}
		});
		this.menuMinFunction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int col = Table.this.table.convertColumnIndexToModel(Table.this.colPress);
				((TableSorter) Table.this.table.getModel()).setGroupedColumnFunction(col, TableSorter.MIN);
			}
		});
		this.menuCountFunction.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int col = Table.this.table.convertColumnIndexToModel(Table.this.colPress);
				((TableSorter) Table.this.table.getModel()).setGroupedColumnFunction(col, TableSorter.COUNT);
			}
		});
		final String ungroupText = ApplicationManager.getTranslation(Table.UNGROUP_COLUMN_es_ES, this.resourcesFile);
		this.menuDeleteGroup = new JMenuItem(ungroupText);
		this.menuDeleteGroup.setIcon(ImageManager.getIcon(ImageManager.DELETE_GROUP));

		this.menuDeleteGroup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.deleteGroup();
			}
		});

		this.menu.add(this.menuDeleteGroup);
		this.menu.addSeparator();



		this.menu.add(this.menuPrintSelection);
		this.menuPrintSelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Print only selected data
				Table.this.printSelection();
			}
		});

		String sCopySelectionText = Table.COPY_SELECTION_es_ES;
		try {
			if (this.resourcesFile != null) {
				sCopySelectionText = this.resourcesFile.getString(Table.COPY_SELECTION);
			}
		} catch (final Exception e) {
			Table.logger.error("COPY_SELECTION", e);
		}
		this.menuCopySelection = new JMenuItem(sCopySelectionText);
		final ImageIcon copyIcon = ImageManager.getIcon(ImageManager.COPY);
		if (copyIcon != null) {
			this.menuCopySelection.setIcon(copyIcon);
		}
		this.menu.add(this.menuCopySelection);
		this.menuCopySelection.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.copySelection();
			}
		});

		final String cellCopyText = Table.COPY_CELL_es_ES;
		try {
			if (this.resourcesFile != null) {
				sCopySelectionText = this.resourcesFile.getString(Table.COPY_CELL);
			}
		} catch (final Exception e) {
			Table.logger.error("COPY_CELL", e);
		}
		this.menuCopyCell = new JMenuItem(cellCopyText);

		final ImageIcon copyCellIcon = ImageManager.getIcon(ImageManager.COPY);
		if (copyCellIcon != null) {
			this.menuCopyCell.setIcon(copyCellIcon);
		}
		this.menu.add(this.menuCopyCell);
		this.menuCopyCell.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.copyCell();
			}
		});

		if (this.buttonRefresh != null) {
			String sRefreshText = Table.REFRESH_es_ES;
			try {
				if (this.resourcesFile != null) {
					sRefreshText = this.resourcesFile.getString(Table.REFRESH);
				}
			} catch (final Exception e) {
				Table.logger.error("REFRESH_BUTTON", e);
			}
			this.menuRefresh = new JMenuItem(sRefreshText);
			final ImageIcon refreshIcon = ImageManager.getIcon(ImageManager.REFRESH);
			if (refreshIcon != null) {
				this.menuRefresh.setIcon(refreshIcon);
			}
			this.menu.addSeparator();
			this.menu.add(this.menuRefresh);
			this.menuRefresh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.refreshEDT(true);
				}
			});
		}

		if (this.pageFetcher != null) {
			final String pageableText = ApplicationManager.getTranslation(Table.PAGEABLE);
			this.menuPageableEnabled = new JMenuItem(pageableText);
			this.menuPageableEnabled.setIcon(ImageManager.getIcon(ImageManager.LEFT_ALIGN));
			if (this.menuRefresh == null) {
				this.menu.addSeparator();
			}

			this.menu.add(this.menuPageableEnabled);
			this.menuPageableEnabled.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.pageFetcher.setPageableEnabled(true);
					Table.this.refreshEDT(true);
				}
			});
		}

		if (this.showControls) {
			String sShowHideControlsText = Table.SHOW_HIDE_CONTROLS_es_ES;
			try {
				if (this.resourcesFile != null) {
					sShowHideControlsText = this.resourcesFile.getString(Table.SHOW_HIDE_CONTROLS);
				}
			} catch (final Exception e) {
				Table.logger.error("SHOW_HIDE_CONTROLS", e);
			}
			this.menuShowHideControls = new JCheckBoxMenuItem(sShowHideControlsText, true);
			final ImageIcon showHideControlsIcon = ImageManager.getIcon(ImageManager.SHOW_HIDE_CONTROLS);
			if (showHideControlsIcon != null) {
				this.menuShowHideControls.setIcon(showHideControlsIcon);
			}
			this.menu.addSeparator();
			this.menu.add(this.menuShowHideControls);
			this.menuShowHideControls.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.toggleControls();
				}
			});
		}

		if (this.allowOpenInNewWindow) {
			this.menu.addSeparator();
			final ImageIcon openNewWindowIcon = ImageManager.getIcon(ImageManager.OPEN_NEW_WINDOW);
			this.menuOpenInNewWindow = new JMenuItem(
					ApplicationManager.getTranslation(Table.openInNewWindowKey, this.resourcesFile));
			this.menu.add(this.menuOpenInNewWindow);

			if (openNewWindowIcon != null) {
				this.menuOpenInNewWindow.setIcon(openNewWindowIcon);
			}
			this.menuOpenInNewWindow.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int[] selectedRows = Table.this.getSelectedRows();
					if (selectedRows.length > 5) {
						Table.this.parentForm.message(Table.M_SELECTION_LESS_THAN_5_ROWS, Form.WARNING_MESSAGE);
						return;
					}
					Table.this.openInNewWindow(selectedRows);
				}
			});
		}
	}

	public void addGroupedFunction(final int id, final GroupOperation operation) {
		if (id <= 4) {
			throw new IllegalArgumentException(
					"Lowest grouped function id must be 5 or higher. Previous ones are reserved.");
		}
		this.getTableSorter().addGroupedFunction(id, operation);
		final JMenuItem item = operation.getItem();
		this.menuGroupFunction.add(item);
	}

	public void addTotalRowOperation(final TotalRowOperation operation) {
		this.getTableSorter().addTotalRowOperation(operation);
	}

	public List getTotalRowOperation() {
		return this.getTableSorter().getTotalRowOperation();
	}

	protected TableFrame[] windowCache = new TableFrame[0];

	/**
	 * Opens the detail form for the rows passed as parameter in the array, in a new window each. The
	 * maximum number of rows that can opened at the same time is 5.
	 * @param modelSelectedRows contains the model row indexes to open the detail form
	 */
	public void openInNewWindow(int[] modelSelectedRows) {
		if (this.formName == null) {
			return;
		}
		if (modelSelectedRows.length > 5) {
			Table.logger
			.debug(this.getClass().toString() + "openInNewWindow: will be only shown data corresponding to 5 rows");
			final int[] auxSelectedRows = new int[5];
			System.arraycopy(modelSelectedRows, 0, auxSelectedRows, 0, auxSelectedRows.length);
			modelSelectedRows = auxSelectedRows;
		}
		// If there are no visible windows in cache then use them
		// If the visible windows in cache plus the additional rows number is
		// greater than 5 then hide some of them
		int visibles = 0;
		for (int i = 0; i < this.windowCache.length; i++) {
			if (this.windowCache[i].isVisible()) {
				visibles++;
			}
		}
		if ((visibles + modelSelectedRows.length) > 5) {
			final int hideNumber = (visibles + modelSelectedRows.length) - 5;
			int hidden = 0;
			for (int i = 0; (i < hideNumber) && (hidden < hideNumber); i++) {
				if (this.windowCache[i].isVisible()) {
					this.windowCache[i].setVisible(false);
					hidden++;
				}
			}
		}

		// 9-9-2005 To avoid detect value events in fields in update mode
		int shown = 0;
		for (int i = 0; (i < this.windowCache.length) && (shown < modelSelectedRows.length); i++) {
			final Form form = this.windowCache[i].form;
			if (!this.windowCache[i].isVisible()) {
				if (form instanceof FormExt) {
					form.getInteractionManager().setQueryInsertMode();
					final EntityResult res = new EntityResultMapImpl();
					res.addRecord(this.getRowKeys(modelSelectedRows[shown]));
					form.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
					form.getInteractionManager().setUpdateMode();
				} else {
					final String entStr = form.getEntityName();
					try {
						form.getInteractionManager().setQueryInsertMode();
						final EntityReferenceLocator referenceLocator = this.parentForm.getFormManager()
								.getReferenceLocator();
						final Entity ent = referenceLocator.getEntityReference(entStr);
						final EntityResult res = ent.query(this.getRowKeys(modelSelectedRows[shown]),
								form.getDataFieldAttributeList(), referenceLocator.getSessionId());
						form.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
						form.getInteractionManager().setUpdateMode();
					} catch (final Exception ex) {
						Table.logger.error(null, ex);
						this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
					}
				}
				shown++;
				this.windowCache[i].setVisible(true);
			}
		}

		for (int i = shown; i < modelSelectedRows.length; i++) {
			final Form form = this.parentForm.getFormManager().getFormCopy(this.formName);
			final TableFrame f = new TableFrame(
					ApplicationManager.getTranslation(this.detailFormTitleKey, this.resourcesFile), form);
			// Set the parentkeys
			if (this.parentkeys != null) {
				for (int j = 0; j < this.parentkeys.size(); j++) {
					final Object currentParent = this.parentkeys.get(j);
					final Object parentValue = this.parentForm.getDataFieldValue(currentParent.toString());
					if (parentValue != null) {
						form.setDataFieldValue(currentParent, parentValue);
					}
					form.setModifiable(currentParent.toString(), false);
				}
			}

			if (this.parentForm.getFormManager().getApplication() != null) {
				f.setIconImage(this.parentForm.getFormManager().getApplication().getFrame().getIconImage());
			}
			if (form instanceof FormExt) {
				form.getInteractionManager().setQueryInsertMode();
				final EntityResult res = new EntityResultMapImpl();
				res.addRecord(this.getRowKeys(modelSelectedRows[i]));
				form.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
				form.getInteractionManager().setUpdateMode();
			} else {
				final String entStr = form.getEntityName();
				try {
					form.getInteractionManager().setQueryInsertMode();
					final EntityReferenceLocator buscador = this.parentForm.getFormManager().getReferenceLocator();
					final Entity ent = buscador.getEntityReference(entStr);
					final EntityResult res = ent.query(this.getRowKeys(modelSelectedRows[i]),
							form.getDataFieldAttributeList(), buscador.getSessionId());
					form.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
					form.getInteractionManager().setUpdateMode();
				} catch (final Exception ex) {
					Table.logger.error(null, ex);
					this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
				}
			}
			form.setParentFrame(f);
			f.pack();
			ApplicationManager.center(f);
			f.setVisible(true);
			final TableFrame[] v = new TableFrame[this.windowCache.length + 1];
			System.arraycopy(this.windowCache, 0, v, 0, this.windowCache.length);
			v[this.windowCache.length] = f;
			this.windowCache = v;
		}
	}

	/**
	 * Method that returns true when the table has sum rows.
	 *
	 * @see TableSorter#isSum
	 * @return true when the table has sum rows
	 */
	public boolean hasSumRow() {
		return ((TableSorter) this.table.getModel()).isSum();
	}

	/**
	 * Determines if the file passed as parameter is a sum row.
	 * @param rowIndex
	 * @return true is the row is a sum row
	 * @deprecated in version 5.3.8
	 */
	@Deprecated
	public boolean isSumRow(final int rowIndex) {
		return ((TableSorter) this.table.getModel()).isSumRow(rowIndex);
	}

	protected void showGroupPopupMenu(final int column) {
		// Group
		this.menuGroupFunction.setText(ApplicationManager.getTranslation(Table.GROUP_FUNCTION, this.resourcesFile));
		this.menuSumFunction.setText(ApplicationManager.getTranslation(Table.SUM_es_ES, this.resourcesFile));
		this.menuAvgFunction.setText(ApplicationManager.getTranslation(Table.AVERAGE_es_ES, this.resourcesFile));
		this.menuMaxFunction.setText(ApplicationManager.getTranslation(Table.MAXIMUM_es_ES, this.resourcesFile));
		this.menuMinFunction.setText(ApplicationManager.getTranslation(Table.MINIMUM_es_ES, this.resourcesFile));
		this.menuCountFunction.setText(ApplicationManager.getTranslation(Table.COUNT_es_ES, this.resourcesFile));
		// translate additional grouping operations defined by user
		this.translateGroupedFunctions();
		this.menuGroupFunction.setVisible(false);
		if (column > 0) {
			final Class columnClass = this.table.getModel().getColumnClass(column);
			if ((columnClass == Date.class) || (columnClass == Timestamp.class)) {
				this.menuGroupDate.setVisible(true);
				this.menuGroup.setVisible(false);
				this.menuGroupDate.setText(
						ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_DATE_es_ES, this.resourcesFile));
				this.menuGroupByYear
				.setText(ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_YEAR, this.resourcesFile));
				this.menuGroupByYearMonth.setText(
						ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_YEAR_MONTH, this.resourcesFile));
				this.menuGroupYearMonthDay.setText(ApplicationManager
						.getTranslation(Table.GROUP_COLUMN_BY_YEAR_MONTH_DAY, this.resourcesFile));
				this.menuGroupByQuarterYear.setText(
						ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_QUARTER_YEAR, this.resourcesFile));
				this.menuGroupByWeekYear.setText(
						ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_WEEK_YEAR, this.resourcesFile));

				this.menuGroupByQuarter
				.setText(ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_QUARTER, this.resourcesFile));
				this.menuGroupByMonth
				.setText(ApplicationManager.getTranslation(Table.GROUP_COLUMN_BY_MONTH, this.resourcesFile));
			} else {
				if (columnClass.getSuperclass() == Number.class) {
					this.menuGroupFunction.setVisible(true);
					if (this.isGrouped() && (this.getTableSorter() != null)
							&& !this.getTableSorter().isGrouped(column)) {
						this.menuGroupFunction.setEnabled(true);
					} else {
						this.menuGroupFunction.setEnabled(false);
					}
				} else {
					this.menuGroupFunction.setVisible(false);
				}
				this.menuGroupDate.setVisible(false);
				this.menuGroup.setVisible(true);
				this.menuGroup
				.setText(ApplicationManager.getTranslation(Table.GROUP_COLUMN_es_ES, this.resourcesFile));
			}
		} else {
			this.menuGroupDate.setVisible(false);
			this.menuGroup.setVisible(false);
		}
		this.menuDeleteGroup
		.setText(ApplicationManager.getTranslation(Table.DELETE_GROUP_es_ES, this.resourcesFile));
		if (((TableSorter) this.table.getModel()).isGrouped()) {
			this.menuDeleteGroup.setEnabled(true);
		} else {
			this.menuDeleteGroup.setEnabled(false);
		}

	}

	protected void showChartPopupMenu(final int column) {
		if (Table.CHART_ENABLED) {
			ChartInfoRepository chartInforRepository = null;

			this.createChartUtilities();

			chartInforRepository = this.chartUtilities.getChartInfoRepository();

			if (chartInforRepository != null) {

				final Enumeration enumDescriptions = chartInforRepository.keys();

				while (enumDescriptions.hasMoreElements()) {
					final String description = enumDescriptions.nextElement().toString();
					final ChartInfo info = chartInforRepository.getChartInfo(description);
					boolean bFound = false;
					for (int i = 0; i < this.menuChartItems.size(); i++) {
						if (((JMenuItem) this.menuChartItems.get(i)).getName().equals(description)) {
							bFound = true;
							break;
						}
					}
					if (bFound) {
						continue;
					}
					final JMenuItem item = new JMenuItem(description);
					ImageIcon chartIcon = null;
					if (info.hasIntervals()) {
						chartIcon = ImageManager.getIcon(ImageManager.LINE_CHART);
					} else {
						switch (info.getType()) {
						case IChartUtilities.LINE:
							chartIcon = ImageManager.getIcon(ImageManager.LINE_CHART);
							break;
						case IChartUtilities.PIE:
							chartIcon = ImageManager.getIcon(ImageManager.PIE);
							break;
						case IChartUtilities.PIE_3D:
							chartIcon = ImageManager.getIcon(ImageManager.PIE);
							break;
						case IChartUtilities.BAR:
							chartIcon = ImageManager.getIcon(ImageManager.BAR);
							break;
						case IChartUtilities.BAR_3D:
							chartIcon = ImageManager.getIcon(ImageManager.BAR);
							break;

						case IChartUtilities.STACKED_3D:
							chartIcon = ImageManager.getIcon(ImageManager.STACK);
							break;
						default:
							chartIcon = ImageManager.getIcon(ImageManager.BAR);
							break;
						}
					}
					item.setName(description);
					if (chartIcon != null) {
						item.setIcon(chartIcon);
					}
					item.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							Table.this.showChart(description);
						}
					});
					this.menu.add(item);
					this.menuChartItems.add(item);
				}
			}
			if (this.isEmpty()) {
				for (int i = 0; i < this.menuChartItems.size(); i++) {
					((JMenuItem) this.menuChartItems.get(i)).setEnabled(false);
				}
			} else {
				for (int i = 0; i < this.menuChartItems.size(); i++) {
					((JMenuItem) this.menuChartItems.get(i)).setEnabled(true);
				}
			}
		}
	}

	protected void showFilterPopupMenu(final TableSorter sorter, final int column) {
		final Object[] columns = new Object[] {
				ApplicationManager.getTranslation(this.table.getColumnName(this.colPress), this.resourcesFile) };
		this.menuFilter
		.setText(ApplicationManager.getTranslation(Table.FILTER_COLUMN_es_ES, this.resourcesFile, columns));

		final boolean filterOr = sorter.lastFilterOr();
		Object oValue = this.table.getModel().getValueAt(this.rowPress, column);
		if (this.colPress >= 0) {
			this.menuFilterByValue.setVisible(true);
			this.menuFilter.setVisible(true);

			if (this.getJTable().getCellRenderer(0, this.colPress) instanceof ComboReferenceCellRenderer) {
				oValue = ((ComboReferenceCellRenderer) this.getJTable().getCellRenderer(0, this.colPress))
						.getCodeDescription(oValue);
			}

			final Object[] valuesFilter = new Object[] { oValue };

			// If the value is a very long string then only show the first
			// 40 characters and ... at the end
			if ((oValue != null) && (oValue.toString() != null) && (oValue.toString().length() > 40)) {
				valuesFilter[0] = oValue.toString().substring(0, 40) + "...";
			}

			if (sorter.isGrouped() || filterOr) {
				this.menuFilter.setEnabled(false);
				this.menuFilterByValue.setText(ApplicationManager.getTranslation(Table.FILTER_BY_VALUE_es_ES,
						this.resourcesFile, new Object[] { "" }));
				this.menuFilterByValue.setEnabled(false);
			} else {
				this.menuFilterByValue.setText(ApplicationManager.getTranslation(Table.FILTER_BY_VALUE_es_ES,
						this.resourcesFile, valuesFilter));
				this.menuFilter.setEnabled(true);
				this.menuFilterByValue.setEnabled(true);
			}
		} else {
			this.menuFilterByValue.setVisible(false);
			this.menuFilter.setVisible(false);
		}
		this.menuDeleteFilter.setText(ApplicationManager.getTranslation(Table.DELETE_FILTER_es_ES, this.resourcesFile));
		this.menuDeleteColumnFilter
		.setText(ApplicationManager.getTranslation(Table.DELETE_FILTER_COLUMN, this.resourcesFile));

		this.menuDeleteFilter.setEnabled(false);
		this.menuDeleteColumnFilter.setEnabled(false);

		if (((TableSorter) this.table.getModel()).isFiltered()) {
			this.menuDeleteFilter.setEnabled(true);
			if (!filterOr) {
				this.menuDeleteColumnFilter.setEnabled(true);
			}
		}

		if (this.colPress >= 0) {
			this.menuDeleteColumnFilter.setVisible(true);
		} else {
			this.menuDeleteColumnFilter.setVisible(false);
		}
	}

	protected void showPageFetcherPopupMenu() {
		if ((this.pageFetcher != null) && !this.pageFetcher.isPageableEnabled()) {
			if (this.menuPageableEnabled != null) {
				this.menuPageableEnabled.setVisible(true);
			}
		} else {
			if (this.menuPageableEnabled != null) {
				this.menuPageableEnabled.setVisible(false);
			}
		}
	}

	/**
	 * Shows the popup menu.
	 * @param x
	 * @param y
	 */
	protected void showPopupMenu(final int x, final int y) {
		Table.logger.trace("showPopupMenu...");
		final TableModel m = this.table.getModel();
		if (this.enabled && (m != null) && (m instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) m;

			if (this.formName != null) {
				if ((this.table.getSelectedRow() >= 0) && (!sorter.isSumRow(this.table.getSelectedRow()))
						&& this.isFrame()) {
					this.menuDetail.setEnabled(true);
					if (this.menuOpenInNewWindow != null) {
						this.menuOpenInNewWindow.setEnabled(true);
					}
				} else {
					this.menuDetail.setEnabled(false);
					if (this.menuOpenInNewWindow != null) {
						this.menuOpenInNewWindow.setEnabled(false);
					}
				}
				this.menuInsert.setEnabled(this.buttonPlus.isEnabled());
			} else {
				this.menuDetail.setEnabled(false);
				if (this.menuOpenInNewWindow != null) {
					this.menuOpenInNewWindow.setEnabled(false);
				}
				this.menuInsert.setEnabled(false);
			}

			final int selectedRow = this.table.getSelectedRowCount();
			if (selectedRow > 0) {
				this.menuCopySelection.setEnabled(true);
				this.menuPrintSelection.setEnabled(true);
			} else {
				this.menuCopySelection.setEnabled(false);
				this.menuPrintSelection.setEnabled(false);
			}

			if ((this.buttonPrint != null) && !this.buttonPrint.isVisible()) {
				this.menuPrintSelection.setVisible(false);
			}

			if ((this.buttonPrint != null) && !this.buttonPrint.isEnabled()) {
				this.menuPrintSelection.setEnabled(false);
			}

			if ((this.buttonCopy != null) && !this.buttonCopy.isVisible()) {
				this.menuCopyCell.setVisible(false);
				this.menuCopySelection.setVisible(false);
			}

			if ((this.buttonCopy != null) && !this.buttonCopy.isEnabled()) {
				this.menuCopyCell.setEnabled(false);
				this.menuCopySelection.setEnabled(false);
			}

			final Object columnValue = this.getColumnValue(x, y);
			this.menuCopyCell.setEnabled(columnValue != null);

			final int column = this.table.convertColumnIndexToModel(this.colPress);
			final Object col = this.table.getModel().getColumnName(column);
			if (col == null) {
				return;
			}
			if (col.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
				return;
			}

			if (sorter.isSorted()) {
				this.menuResetOrder.setEnabled(true);
			} else {
				this.menuResetOrder.setEnabled(false);
			}

			this.showFilterPopupMenu(sorter, column);

			this.showGroupPopupMenu(column);

			this.showPageFetcherPopupMenu();

			this.showChartPopupMenu(column);

			this.menu.show(this, x, y);
		}
	}

	protected Object getColumnValue(final int x, final int y) {
		final Point point = new Point(x, y);

		final Rectangle scrollPaneBounds = this.scrollPane.getBounds();

		final Point scrollPanePoint = SwingUtilities.convertPoint(this, point, this.scrollPane);

		final Point currentPoint = SwingUtilities.convertPoint(this, point, this.table);

		JTable usedTable = this.table;
		if (scrollPaneBounds.contains(scrollPanePoint)) {
			this.colPress = this.table.columnAtPoint(currentPoint);
		} else {
			final Point blockedPoint = SwingUtilities.convertPoint(this, point, this.blockedTable);
			this.colPress = this.blockedTable.columnAtPoint(blockedPoint);
			usedTable = this.blockedTable;
		}

		this.rowPress = this.table.rowAtPoint(currentPoint);

		// If cell is not empty then enable the menu
		return usedTable.getValueAt(this.rowPress, this.colPress);
	}

	protected void translateGroupedFunctions() {
		final Map operations = this.getTableSorter().getOperations();
		final List listOperations = new ArrayList(operations.keySet());
		for (int i = 0; i < listOperations.size(); i++) {
			final GroupOperation operation = (GroupOperation) operations.get(listOperations.get(i));
			// operations with id=0,1,2,3,4 are reserved for AVG, COUNT, MIN...
			if (operation.getOperationId() > 4) {
				final JMenuItem item = operation.getItem();
				if (item != null) {
					item.setText(ApplicationManager.getTranslation(operation.getOperationText(), this.resourcesFile));
				}
			}
		}

	}

	protected void createChartUtilities() {
		if (this.chartUtilities == null) {
			if (Table.CHART_V1) {
				this.chartUtilities = new ChartUtilities_1_0(this);
			}
			if (!Table.CHART_V1) {
				try {
					final Class clazz = Class.forName("com.ontimize.chart.ChartUtilities");
					final Constructor constructor = clazz.getConstructor(new Class[] { Table.class });
					this.chartUtilities = (IChartUtilities) constructor.newInstance(new Object[] { this });
				} catch (final Exception e1) {
					Table.logger.trace(null, e1);
				}
			}

		}
	}

	/**
	 * Creates the detail form for this table. The detail form is used to insert new records to the
	 * table and to diplay de information contained buy the table in a more detailed way using a form
	 * instead a row.
	 */
	protected void createDetailForm() {
		final String sFormName = Table.this.formName;

		final List vKeys = this.getKeys();

		Form formCopy = null;
		if (this.detailFormBuilder != null) {
			formCopy = this.detailFormBuilder.getForm();
		} else {
			formCopy = this.parentForm.getFormManager().getFormCopy(sFormName);
		}

		if (formCopy != null) {
			if (this.dynamicFormManager != null) {
				formCopy.setDynamicFormManager(this.dynamicFormManager);
				this.dynamicFormManager.setFormManager(this.parentForm.getFormManager());
			}

			final Map filterKeys = this.getParentKeyValues();

			final Map hPrimaryKeys = new Hashtable();
			final Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof Frame) {
				this.detailForm = new DetailForm((Frame) w, this.detailFormTitleKey, true, formCopy, hPrimaryKeys,
						vKeys, Table.this, filterKeys, this.codValues);
			} else if (w instanceof Dialog) {
				this.detailForm = new DetailForm((Dialog) w, this.detailFormTitleKey, true, formCopy, hPrimaryKeys,
						vKeys, Table.this, filterKeys, this.codValues);
			} else {
				this.detailForm = new DetailForm(this.detailFormTitleKey, true, formCopy, hPrimaryKeys, vKeys,
						Table.this, filterKeys, this.codValues);
			}
			this.detailForm.setSizePositionPreference(this.getDetailFormSizePreferenceKey());
			this.detailForm.setResourceBundle(this.resourcesFile);
		}
	}

	/**
	 * Creates the detail form for this table. The detail form is used to insert new records to the
	 * table and to diplay de information contained buy the table in a more detailed way using a form
	 * instead a row.
	 */
	protected void createInsertDetailForm() {
		final String sFormName = Table.this.insertFormName;

		final List vKeys = this.getKeys();

		Form formCopy = null;
		if (this.insertDetailFormBuilder != null) {
			formCopy = this.insertDetailFormBuilder.getForm();
		} else {
			formCopy = this.parentForm.getFormManager().getFormCopy(sFormName);
		}

		if (formCopy != null) {
			if (this.dynamicFormManager != null) {
				formCopy.setDynamicFormManager(this.dynamicFormManager);
				this.dynamicFormManager.setFormManager(this.parentForm.getFormManager());
			}

			final Map filterKeys = this.getParentKeyValues();

			final Map hPrimaryKeys = new Hashtable();
			final Window w = SwingUtilities.getWindowAncestor(this);
			if (w instanceof Frame) {
				this.insertDetailForm = new DetailForm((Frame) w, this.detailFormTitleKey, true, formCopy, hPrimaryKeys,
						vKeys, Table.this, filterKeys, this.codValues);
			} else if (w instanceof Dialog) {
				this.insertDetailForm = new DetailForm((Dialog) w, this.detailFormTitleKey, true, formCopy,
						hPrimaryKeys, vKeys, Table.this, filterKeys, this.codValues);
			} else {
				this.insertDetailForm = new DetailForm(this.detailFormTitleKey, true, formCopy, hPrimaryKeys, vKeys,
						Table.this, filterKeys, this.codValues);
			}
			this.insertDetailForm.setSizePositionPreference(this.getDetailFormSizePreferenceKey());
			this.insertDetailForm.setResourceBundle(this.resourcesFile);
		}
	}

	/**
	 * Creates the detail form for this table. The detail form is used to insert new records to the
	 * table (if "insertform" not exists) and to diplay de information contained buy the table in a more
	 * detailed way using a form instead a row.
	 */
	protected IDetailForm createTabbedDetailForm() {
		final String sFormName = Table.this.formName;
		return this.createTabbedDetailForm(sFormName);
	}

	/**
	 * Creates the detail form for this table. The detail form is used to insert new records to the
	 * table.
	 */
	protected IDetailForm createInsertTabbedDetailForm() {
		final String sFormName = Table.this.insertFormName != null ? Table.this.insertFormName : Table.this.formName;
		return this.createTabbedDetailForm(sFormName);

	}

	/**
	 * Creates the detail form for this table.
	 */
	protected IDetailForm createTabbedDetailForm(final String sFormName) {
		final List vKeys = this.getKeys();

		final Form formCopy = this.parentForm.getFormManager().getFormCopy(sFormName);

		if (formCopy != null) {
			final Map filterKeys = this.getParentKeyValues();

			final Map hPrimaryKeys = new Hashtable();
			final IDetailForm detailForm = new TabbedDetailForm(formCopy, hPrimaryKeys, vKeys, Table.this, filterKeys,
					this.codValues);
			detailForm.setResourceBundle(this.resourcesFile);
			return detailForm;
		}
		return null;
	}

	/**
	 * Returns the key of the preference that stores the size of the DetailForm
	 * @return
	 */
	public String getDetailFormSizePreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.DETAIL_DIALOG_SIZE_POSITION + "_" + f.getArchiveName() + "_" + this.entity
						: BasicApplicationPreferences.DETAIL_DIALOG_SIZE_POSITION + "_" + this.entity;
	}

	/**
	 * Deletes from the entity the specified row.
	 * @param rowIndex the row index
	 * @return the result of the execution of the delete instruction
	 * @throws Exception
	 * @see Entity#delete(Hashtable, int)
	 */
	protected EntityResult deleteEntityRow(final int rowIndex) throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
			return new EntityResultMapImpl();
		} else if (this.dataBaseRemove) {
			final EntityReferenceLocator referenceLocator = this.parentForm.getFormManager().getReferenceLocator();
			final Entity ent = referenceLocator.getEntityReference(this.getEntityName());
			final Map kv = this.getParentKeyValues();
			final List vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				kv.put(vKeys.get(i), this.getRowKey(rowIndex, vKeys.get(i).toString()));
			}
			final EntityResult eR = ent.delete(kv, referenceLocator.getSessionId());
			if ((eR.getCode() == EntityResult.OPERATION_SUCCESSFUL)
					&& (this.parentForm.getFormManager() instanceof ITabbedFormManager)) {
				final ITabbedFormManager tabbedFormManager = (ITabbedFormManager) this.parentForm.getFormManager();
				if (tabbedFormManager.getMainForm().equals(this.parentForm)) {

					final int index = tabbedFormManager.indexOfKeys(kv);
					if (index > 0) {
						tabbedFormManager.removeTab(index);
					}
				}
			}
			return eR;
		}
		return new EntityResultMapImpl();
	}

	/**
	 * Returns the selected rows indexes, sorted from higher to lower index
	 * @return the selected rows indexes or an empty array if no row is selected
	 */
	public int[] getSelectedRows() {
		final int[] aux = this.table.getSelectedRows();
		Arrays.sort(aux); // ascendent sort
		final int[] aux2 = new int[aux.length];
		for (int i = 0; i < aux.length; i++) {
			aux2[aux.length - i - 1] = aux[i];
		}
		return aux2;
	}

	/**
	 * Counts the number of rows selected in the table.
	 * @return the total amount of selected rows
	 */
	public int getSelectedRowsNumber() {
		return this.table.getSelectedRowCount();
	}

	/**
	 * Changes the table representation to the table one. That is, instead using a form to display the
	 * table information, the normal grid format will be used.
	 */
	protected void changeToTableView() {
		if (!this.tableView) {
			this.tableView = true;
			this.scrollPane.getViewport().remove(this.table);
			this.scrollPane.getViewport().add(this.formView);
			this.scrollPane.doLayout();
		}
	}

	/**
	 * Changes the table by the detail form.
	 */
	public void changeView() {
		if (this.formName != null) {
			if (this.formView == null) {
				if (this.detailForm == null) {
					this.createDetailForm();
				}
				this.formView = this.detailForm.getForm();
			}
			if (!this.tableView) {
				this.scrollPane.getViewport().add(this.table);
				this.scrollPane.getViewport().remove(this.formView);
				this.detailForm.getContentPane().add(this.formView);
				this.scrollPane.doLayout();
				// this.doLayout();
			} else {
				this.scrollPane.getViewport().remove(this.table);
				this.scrollPane.getViewport().add(this.formView);
				this.detailForm.getContentPane().remove(this.formView);
				this.scrollPane.doLayout();
				// this.doLayout();
			}
			this.tableView = !this.tableView;
		} else {
			Table.logger.debug("View cannot be changed to Form View because no form was especified");
		}
	}

	/**
	 * Adds a new row with data
	 *
	 * @see TableSorter#addRow
	 * @param rowData a {@link #Hashtable} containing the data to add
	 */
	public void addRow(final Map rowData) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
			this.table.getCellEditor().stopCellEditing();
		}
		if (this.sumRowTable != null && ts.getRowCount() == 0) {
			this.tablePanel.revalidate();
		}
		ts.addRow(rowData);
		this.table.repaint();
	}

	/**
	 * Adds a new row with data in a concrete position in the table
	 *
	 * @see TableSorter#addRow(int, Hashtable)
	 * @param rowData a {@link #Hashtable} containing the data to add
	 * @param row the position in the model in which the row will be added
	 */
	public void addRow(final int row, final Map rowData) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
			this.table.getCellEditor().stopCellEditing();
		}
		if (this.sumRowTable != null && ts.getRowCount() == 0) {
			this.tablePanel.revalidate();
		}
		ts.addRow(row, rowData);
		this.table.repaint();
	}

	/**
	 * Adds several rows to the table.
	 *
	 * @param rows
	 *            and array with the position of the rows in which the new rows will be inserted
	 * @param rowsData
	 *            a {@link #Vector} containing {@link #Hashtable}. Each position of the List is a Map containing
	 *            information for a row
	 * @deprecated
	 * @see #addRows(List)
	 * @see TableSorter#addRows(int[], List)
	 */
	@Deprecated
	public void addRows(final int[] rows, final List rowsData) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
			this.table.getCellEditor().stopCellEditing();
		}
		ts.addRows(rows, rowsData);
		this.table.repaint();
	}

	/**
	 * Adds several rows to the table.
	 * @param rowsData a {@link #Vector} containing {@link #Hashtable}. Each position of the List is a
	 *        Map containing information for a row
	 * @see TableSorter#addRows(List)
	 */
	public void addRows(final List rowsData) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		if ((this.table != null) && this.table.isEditing() && (this.table.getCellEditor() != null)) {
			this.table.getCellEditor().stopCellEditing();
		}
		ts.addRows(rowsData);
		this.table.repaint();
	}

	/**
	 * Refreshes the rows passed as parameter
	 * @param viewRowIndexes the row indexes
	 */
	public void refreshRows(final int[] viewRowIndexes) {
		try {
			final String indexKeyString = "__INDEX__";

			this.checkRefreshThread();
			final EntityReferenceLocator locator = this.parentForm.getFormManager().getReferenceLocator();
			final Entity ent = this.locator.getEntityReference(this.getEntityName());
			final Map kv = this.getParentKeyValues();
			final List vKeys = this.getKeys();
			BasicExpression bexp = null;
			final EntityResult mapIndexKey = new EntityResultMapImpl();

			for (int j = 0; j < viewRowIndexes.length; j++) {
				final Map indexKeysHash = new Hashtable();
				final int objectRowIndex = viewRowIndexes[j];
				indexKeysHash.put(indexKeyString, objectRowIndex);
				BasicExpression bexpIndex = null;
				for (int i = 0; i < vKeys.size(); i++) {
					final Object oKey = vKeys.get(i);
					final Object rowKeyValue = this.getRowKey(objectRowIndex, oKey.toString());
					indexKeysHash.put(oKey, rowKeyValue);
					if (bexpIndex != null) {
						final BasicExpression bexpAux = new BasicExpression(new BasicField((String) oKey),
								BasicOperator.EQUAL_OP, rowKeyValue);
						bexpIndex = new BasicExpression(bexpIndex, BasicOperator.AND_OP, bexpAux);
					} else {
						bexpIndex = new BasicExpression(new BasicField((String) oKey), BasicOperator.EQUAL_OP,
								rowKeyValue);
					}
				}

				if (bexp != null) {
					bexp = new BasicExpression(bexp, BasicOperator.OR_OP, bexpIndex);
				} else {
					bexp = bexpIndex;
				}

				mapIndexKey.addRecord(indexKeysHash);
			}

			kv.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp);

			final long t = System.currentTimeMillis();
			final EntityResult res = ent.query(kv, this.attributes, this.locator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
				Table.logger.debug("{}", res.getMessage());
			} else if (res.isEmpty()) {
				this.deleteRows(viewRowIndexes);
			} else {
				final long t2 = System.currentTimeMillis();

				final List<Integer> deleteIndex = new ArrayList<Integer>();
				for (int i = 0; i < mapIndexKey.calculateRecordNumber(); i++) {
					final Map<String, Integer> actualIndexRecord = mapIndexKey.getRecordValues(i);
					final int actualIndexRow = actualIndexRecord.remove(indexKeyString);
					final Map rowData = res.getRecordValues(res.getRecordIndex(actualIndexRecord));
					if (rowData != null) {
						this.updateRowData(rowData, actualIndexRecord);
					} else {
						deleteIndex.add(actualIndexRow);
					}
				}

				if (!deleteIndex.isEmpty()) {
					for (final int i : deleteIndex) {
						this.deleteRow(i);
					}
				}

				final long t3 = System.currentTimeMillis();
				Table.logger.trace("Table: Query time: {}  ,  deleteRow-addRow time: {}", t2 - t, t3 - t2);
			}

		} catch (final Exception e) {
			Table.logger.error("refreshRow:", e);
		}
	}

	/**
	 * Refreshes the row passed as parameter.
	 * @param viewRowIndex the index to refresh
	 */
	public void refreshRow(final int viewRowIndex) {
		this.refreshRow(viewRowIndex, null);
	}

	/**
	 * Refreshes the row passed as parameter.
	 * @param viewRowIndex the index to refresh
	 * @param oldkv
	 */
	public void refreshRow(final int viewRowIndex, final Map oldkv) {
		try {
			this.checkRefreshThread();
			final Entity ent = this.locator.getEntityReference(this.getEntityName());
			final Map kv = this.getParentKeyValues();
			// Put the row keys
			final List vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				final Object oKey = vKeys.get(i);
				if ((oldkv != null) && oldkv.containsKey(oKey)) {
					kv.put(oKey, oldkv.get(oKey));
				} else {
					kv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
			}
			final long t = System.currentTimeMillis();
			final EntityResult res = ent.query(kv, this.attributes, this.locator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
				Table.logger.debug("{}", res.getMessage());
			} else if (res.isEmpty()) {
				this.deleteRow(viewRowIndex);
			} else {
				final long t2 = System.currentTimeMillis();
				// Update row data
				final Map hRowData = res.getRecordValues(0);
				final Map newkv = new Hashtable();
				for (int i = 0; i < vKeys.size(); i++) {
					final Object oKey = vKeys.get(i);
					newkv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
				this.updateRowData(hRowData, newkv);

				final long t3 = System.currentTimeMillis();
				Table.logger.trace("Table: Query time: {}  ,  deleteRow-addRow time: {}", t2 - t, t3 - t2);
			}
		} catch (final Exception e) {
			Table.logger.error("refreshRow:", e);
		}
	}

	/**
	 * Removes the sorting applied to the table.
	 */
	public void resetOrder() {
		((TableSorter) this.table.getModel()).resetOrder();
	}

	/**
	 * Updates the table content querying the entity specified in the XML, and filtering the results by
	 * the values set in the form to the fields corresponding to the attributes parentkey and otherkeys.
	 *
	 *
	 * @since 5.3.8
	 *
	 *        Refresh operation in Tables is made in separated thread. Sometimes, users refresh manually
	 *        table and execute code after this (coloring rows, computing data...). This listener allows
	 *        to execute after this operation finishes (success/error).E.g.: <br>
	 *
	 *        table.addRefreshTableListener(new RefreshTableListener() {
	 *
	 * @Override public void postIncorrectRefresh(RefreshTableEvent e) { managedForm.message("Incorrect
	 *           refresh", MessageDialog.ERROR_MESSAGE); }
	 *
	 * @Override public void postCorrectRefresh(RefreshTableEvent e) { managedForm.message("Succesfully
	 *           refresh", MessageDialog.INFORMATION_MESSAGE); }
	 *
	 *           });
	 *
	 */
	public void refresh() {
		if (SwingUtilities.isEventDispatchThread()) {
			this.hideInformationPanel();
			this.refreshInEDT(true);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Table.this.hideInformationPanel();
						Table.this.refreshInEDT(true);
					}
				});
			} catch (final Exception e) {
				Table.logger.trace(null, e);
			}
		}
	}

	public void refreshEDT() {
		this.refreshEDT(true);
	}

	/**
	 * Updates the table content querying the entity specified in the XML, and filtering the results by
	 * the values set in the form to the fields corresponding to the attributes parentkey and otherkeys.
	 * @param autoSizeColumns if true, resizes the column width
	 */
	public void refresh(final boolean autoSizeColumns) {

		if (SwingUtilities.isEventDispatchThread()) {
			this.hideInformationPanel();
			this.refreshEDT(autoSizeColumns);
		} else {
			try {
				SwingUtilities.invokeAndWait(new Runnable() {

					@Override
					public void run() {
						Table.this.hideInformationPanel();
						Table.this.refreshEDT(autoSizeColumns);
					}
				});
			} catch (final Exception e) {
				Table.logger.trace(null, e);
			}
		}
	}

	/**
	 * Updates the table content querying the entity specified in the XML, and filtering the results by
	 * the values set in the form to the fields corresponding to the attributes parentkey and otherkeys.
	 * @param autoSizeColumns
	 */

	public void refreshEDT(final boolean autoSizeColumns) {
		this.checkRefreshThread();
		this.refreshThread = null;
		this.refreshInThread(0);
	}

	/**
	 * Updates the table content querying the entity specified in the XML, and filtering the results by
	 * the values set in the form to the fields corresponding to the attributes parentkey and otherkeys.
	 * @param autoSizeColumns
	 * @deprecated
	 */

	@Deprecated
	public void refreshInEDT(final boolean autoSizeColumns) {
		try {
			this.checkRefreshThread();
			final EntityReferenceLocator locator = this.parentForm.getFormManager().getReferenceLocator();
			final Entity ent = locator.getEntityReference(this.getEntityName());
			Map kv = new Hashtable();
			kv = this.getParentKeyValues();
			if (ent == null) {
				if (Table.logger.isDebugEnabled()) {
					MessageDialog.showErrorMessage(this.parentFrame,
							"DEBUG: Table: The refresh operation can't be executed because the entity can't be found. Entity: "
									+ this.entity);
				}
				return;
			}

			EntityResult res = null;
			if ((this.pageFetcher != null) && this.pageFetcher.isPageableEnabled()) {
				res = ((AdvancedEntity) ent).query(kv, this.attributes, locator.getSessionId(),
						this.pageFetcher.getPageSize(), this.pageFetcher.getOffset(),
						this.getSQLOrderList());
			} else {
				res = ent.query(kv, this.attributes, locator.getSessionId());
			}

			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
				Table.logger.debug("{}", res.getMessage());
				this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.ERROR));
			} else {
				// Check the net
				final int threshold = ConnectionManager.getCompresionThreshold(res.getBytesNumber(), res.getStreamTime());
				if (threshold > 0) {
					final ConnectionOptimizer opt = ConnectionManager.getConnectionOptimizer();
					if ((opt != null) && (locator instanceof ClientReferenceLocator)) {
						try {
							opt.setDataCompressionThreshold(((ClientReferenceLocator) locator).getUser(),
									locator.getSessionId(), threshold);
							Table.logger.debug("Table: Compression threshold established to "
									+ ((ClientReferenceLocator) locator).getUser() + " " + locator.getSessionId()
									+ " en : " + threshold);
						} catch (final Exception e) {
							Table.logger.error("Table: Error establishing compression threshold", e);
						}
					}
				}
				this.setValue(res, autoSizeColumns);
				// Update the data in parentkform cache
				if (this.parentForm != null) {
					final Map av = new Hashtable();
					av.put(this.getAttribute(), res);
					this.parentForm.updateDataListDataCurrentRecord(av, false);
				}
				this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.OK));
			}
		} catch (final Exception e) {
			Table.logger.error("Refresh row: ", e);
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.ERROR));
		}
	}

	/**
	 * Refreshes the data in the table periodically. The time between table updates is passed as
	 * parameter.
	 * @param delay the length of time to sleep in milliseconds
	 */
	public void refreshInThread(final int delay) {

		try {
			if ((this.refreshThread != null) && this.refreshThread.isAlive()) {
				Table.logger.debug("Table: A thread is already refreshing");
			} else {
				this.refreshThread = new RefreshThread(this);
				this.refreshThread.setDelay(delay);
				this.refreshThread.start();
			}
		} catch (final Exception e) {
			Table.logger.trace(null, e);
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		if (this.table != null) {
			this.evaluatePreferredRowsHeight();
			if (this.table.getTableHeader() != null) {
				((JComponent) this.table.getTableHeader().getDefaultRenderer()).updateUI();
			}
		}
	}

	/**
	 * Sets if the table must have even rows of one color and odd rows of other
	 * @param lineRemark true if odd and even lines must be remarked, false otherwise
	 */
	public void setLineRemark(final boolean lineRemark) {
		final TableColumnModel columnModel = this.table.getColumnModel();
		for (int i = 0; i < columnModel.getColumnCount(); i++) {
			final TableColumn tableColumn = columnModel.getColumn(i);
			final TableCellRenderer cellR = tableColumn.getCellRenderer();
			if ((cellR != null) && (cellR instanceof CellRenderer)) {
				((CellRenderer) cellR).setLineRemark(lineRemark);
			} else if ((cellR != null) && (cellR instanceof ComboReferenceCellRenderer)) {
				((ComboReferenceCellRenderer) cellR).setLineRemark(lineRemark);
			}

		}
		try {
			((DateCellRenderer) this.table.getDefaultRenderer(Timestamp.class)).setLineRemark(lineRemark);
			((DateCellRenderer) this.table.getDefaultRenderer(java.sql.Date.class)).setLineRemark(lineRemark);
			((DateCellRenderer) this.table.getDefaultRenderer(java.util.Date.class)).setLineRemark(lineRemark);
			((BooleanCellRenderer) this.table.getDefaultRenderer(Boolean.class)).setLineRemark(lineRemark);
			((RealCellRenderer) this.table.getDefaultRenderer(Float.class)).setLineRemark(lineRemark);
			((RealCellRenderer) this.table.getDefaultRenderer(Double.class)).setLineRemark(lineRemark);
			((ObjectCellRenderer) this.table.getDefaultRenderer(Object.class)).setLineRemark(lineRemark);
			((ObjectCellRenderer) this.table.getDefaultRenderer(String.class)).setLineRemark(lineRemark);
			((RealCellRenderer) this.table.getDefaultRenderer(Number.class)).setLineRemark(lineRemark);
			this.repaint();
		} catch (final Exception e) {
			Table.logger.error(null, e);
		}
	}

	/**
	 * Determines if the table column passed as param is filtered.
	 * @param viewColumnIndex
	 * @return true if the table has filters; otherwise, false
	 */
	public boolean isFiltered(final int viewColumnIndex) {
		return ((TableSorter) this.table.getModel()).isFiltered(this.table.convertColumnIndexToModel(viewColumnIndex));
	}

	/**
	 * Determines if the table has filters.
	 * @return true if the table has filters; otherwise, false
	 */
	public boolean isFiltered() {
		return ((TableSorter) this.table.getModel()).isFiltered();
	}

	/**
	 * Determines if the table is grouped.
	 * @return true if the table is grouped; otherwise, false
	 */
	public boolean isGrouped() {
		return ((TableSorter) this.table.getModel()).isGrouped();
	}

	/**
	 * Sets the font for this component, for all the elements that must have a configured fort, for
	 * example renderers.
	 * @param newFont the desired <code>Font</code> for this component
	 * @see java.awt.Component#getFont
	 */
	@Override
	public void setFont(final Font newFont) {
		super.setFont(newFont);
		if (this.table != null) {
			this.getJTable().setFont(newFont);
			// this.table.getTableHeader().setFont(newFont);
			if (this.currencyRenderer != null) {
				this.currencyRenderer.setFont(newFont);
			}
			if (this.hourRenderer != null) {
				this.hourRenderer.setFont(newFont);
			}
			final TableCellRenderer renderer = this.table.getTableHeader().getDefaultRenderer();
			if (renderer instanceof ObjectCellRenderer) {
				((ObjectCellRenderer) renderer).setFont(newFont);
			} else if (renderer instanceof HeadCellRenderer) {
				((HeadCellRenderer) renderer).setFont(newFont);
			} else if (renderer instanceof JLabel) {
				((JLabel) renderer).setFont(newFont);
			}
			final TableColumnModel mc = this.table.getColumnModel();
			for (int i = 0; i < mc.getColumnCount(); i++) {
				final TableColumn tc = mc.getColumn(i);
				final TableCellRenderer cellR = tc.getCellRenderer();
				if ((cellR != null) && (cellR instanceof CellRenderer)) {
					((CellRenderer) cellR).setFont(newFont);
				} else if ((cellR != null) && (cellR instanceof ComboReferenceCellRenderer)) {
					((ComboReferenceCellRenderer) cellR).setFont(newFont);
				}
			}
			try {

				((JComponent) this.table.getDefaultRenderer(Timestamp.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(java.sql.Date.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(java.util.Date.class)).setFont(newFont);

				((JComponent) this.table.getDefaultRenderer(Boolean.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(Float.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(Double.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(Number.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(Object.class)).setFont(newFont);
				((JComponent) this.table.getDefaultRenderer(String.class)).setFont(newFont);

			} catch (final Exception e) {
				Table.logger.error(null, e);
			}

			this.evaluatePreferredRowsHeight();
		}
	}

	/**
	 * @deprecated
	 */

	@Deprecated
	protected int calculatePreferredRowHeights() {
		final long t = System.currentTimeMillis();
		int preferredHeight = this.getMinRowHeight();

		for (int i = 1; i < this.table.getColumnCount(); i++) {
			TableCellRenderer cellRenderer = this.table.getDefaultRenderer(this.table.getColumnClass(i));
			final TableCellRenderer cellRenderer2 = this.table.getColumnModel().getColumn(i).getCellRenderer();
			if (cellRenderer2 != null) {
				cellRenderer = cellRenderer2;
			}
			if (cellRenderer == null) {
				continue;
			}
			if (cellRenderer instanceof ObjectCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((ObjectCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof RealCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((RealCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof ComboReferenceCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((ComboReferenceCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof CurrencyCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((CurrencyCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof DateCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((DateCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof BooleanCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((BooleanCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof ImageCellRenderer) {
				preferredHeight = Math.max(preferredHeight,
						((ImageCellRenderer) cellRenderer).getPreferredSize().height);
			} else if (cellRenderer instanceof CellRenderer) {
				preferredHeight = Math.max(preferredHeight, ((CellRenderer) cellRenderer).getPreferredSize().height);
			} else {
				final Component c = cellRenderer.getTableCellRendererComponent(this.table, "0", false, false, 1, i);
				if (c != null) {
					preferredHeight = Math.max(preferredHeight, c.getPreferredSize().height);
				}
			}
		}

		Table.logger.debug("calculePreferredRowsHeight time: {}", System.currentTimeMillis() - t);
		return preferredHeight;
	}

	protected MouseAdapter listMouseListener = null;

	/**
	 * Creates a Sorter for TableModels
	 * @param model the model
	 * @return
	 */
	protected TableSorter createTableSorter(final ExtendedTableModel model) {
		return new TableSorter(model, this.getOriginalSumRowCols(), this.parameters);
	}

	protected ExtendedTableModel createExtendedTableModel() {
		return new ExtendedTableModel(new Hashtable(0), this.attributes, this.calculedColumns, false,
				this.requiredColumnsCalculedColumns);
	}

	protected JTable createSumRowBlockedTable() {
		final SumRowTable sumRowBlockedTable = new SumRowTable(this.blockedTable);
		sumRowBlockedTable.setModel(new SumRowTableModel((OTableModel) this.blockedTable.getModel()));

		sumRowBlockedTable.getTableHeader()
		.setPreferredSize(new Dimension(this.blockedTable.getTableHeader().getPreferredSize().width, 0));
		return sumRowBlockedTable;
	}

	protected JTable createSumRowTable() {
		final SumRowTable sumRowTable = new SumRowTable(this.table) {
			@Override
			public void setVisible(final boolean aFlag) {
				super.setVisible(aFlag);
			}
		};
		final TableSorter sorter = (TableSorter) this.table.getModel();
		sumRowTable.setModel(new SumRowTableModel(sorter));

		sumRowTable.getTableHeader()
		.setPreferredSize(new Dimension(this.table.getTableHeader().getPreferredSize().width, 0));
		return sumRowTable;
	}

	/**
	 * Creates the <code>JScrollpane</code> that contains the Table. This method should assign field
	 * class: <code>scrollpane</code>.
	 *
	 */
	protected JScrollPane createSumRowJScrollPane(final Map params) {
		final JScrollPane sumRowScrollPane = new JScrollPane(this.sumRowTable) {

			@Override
			public void setBorder(final Border border) {
				// TODO Fix bug ontimize plaf 1.0.7
				if (border instanceof EmptyBorder) {
					return;
				}
				super.setBorder(border);
			}


			@Override
			public void setVisible(final boolean aFlag) {
				super.setVisible(aFlag);
			}
		};

		sumRowScrollPane.setVerticalScrollBar(new JScrollBar() {

			@Override
			protected void paintComponent(final Graphics g) {
			}

			@Override
			public void paint(final Graphics g) {
			}
		});

		// To show the vertical scroll only if needed
		if (!ParseUtils.getBoolean((String) params.get("vscroll"), true)) {
			sumRowScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			sumRowScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}
		sumRowScrollPane.setOpaque(false);

		// scrollPane.getVerticalScrollBar().setPreferredSize(
		// new Dimension(ParseUtils.getInteger((String) params
		// .get("vscrollwidth"), scrollPane.getVerticalScrollBar()
		// .getPreferredSize().width), scrollPane
		// .getVerticalScrollBar().getPreferredSize().height));

		if (ApplicationManager.useOntimizePlaf) {
			final Color gridColor = this.table.getGridColor();
			final LineBorder border = new LineBorder(gridColor, 1);
			sumRowScrollPane.setBorder(new SumRowBorder(border, new Insets(-1, 0, 0, 0)));
		} else {
			final Border currentBorder = sumRowScrollPane.getBorder();
			if (currentBorder != null) {
				sumRowScrollPane.setBorder(new SumRowBorder(currentBorder, new Insets(0, -1, -1, -1)));
			}
		}

		this.scrollPane.getHorizontalScrollBar().setModel(sumRowScrollPane.getHorizontalScrollBar().getModel());
		return sumRowScrollPane;
	}

	public JTable getBlockedTable() {
		return this.blockedTable;
	}

	protected BlockedTable createBlockedTable() {
		final TableSorter sorter = (TableSorter) this.table.getModel();
		// blockTable.setModel(new BlockedTableModel(sorter));

		final BlockedTable blockTable = new BlockedTable(new BlockedTableModel(sorter), (EJTable) this.table);
		// blockTable.setAutoCreateColumnsFromModel(false);

		this.addTableHeaderRenderer(blockTable);

		blockTable.setSelectionModel(this.table.getSelectionModel());
		this.addBlockedTableHeaderMouseListener(blockTable);

		return blockTable;
	}

	/**
	 * Creates the <code>JScrollpane</code> that contains the BlockedTable. This method should assign
	 * field class: <code>scrollpane</code>.
	 *
	 * @since 5.2080EN
	 */
	protected void createBlockedScrollPane(final Map params) {
		this.blockedScrollPane = new JScrollPane(this.blockedTable) {
			@Override
			protected JViewport createViewport() {
				return new JViewport() {
					@Override
					public Dimension getSize() {
						final Dimension d = super.getSize();
						if ((d != null) && (d.height <= 0) && (d.width <= 0)) {
							try {
								final Dimension origin = Table.this.scrollPane.getViewport().getSize();
								d.height = origin.height;
							} catch (final Exception ex) {
								logger.trace("Error getting size of viewport", ex);
							}
						}
						return d;
					}

					@Override
					public Dimension getViewSize() {
						final Dimension d = super.getViewSize();
						try {
							if ((d != null) && (d.width <= 0)) {
								final Dimension dP = Table.this.scrollPane.getViewport().getViewSize();
								d.height = dP.height;
							}
						} catch (final Exception ex) {
							logger.trace("Error getting size of viewport", ex);
						}
						return d;
					}

					@Override
					public Dimension getExtentSize() {
						final Dimension d = super.getExtentSize();
						final Dimension root = Table.this.scrollPane.getViewport().getExtentSize();
						if (d.height!=root.height){
							d.height = root.height;
						}
						return d;
					}
				};
			}
		};

		this.blockedScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		this.blockedScrollPane.getVerticalScrollBar().setModel(this.scrollPane.getVerticalScrollBar().getModel());
		this.blockedScrollPane.setOpaque(false);

		if (ApplicationManager.useOntimizePlaf) {
			this.blockedScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		}
	}

	public static class WrapperBoundedRangeModel implements BoundedRangeModel {

		private final BoundedRangeModel source;

		public WrapperBoundedRangeModel(final BoundedRangeModel source) {
			this.source = source;
		}

		@Override
		public int getMinimum() {
			return this.source.getMinimum();
		}

		@Override
		public void setMinimum(final int newMinimum) {
			// not implements
		}

		@Override
		public int getMaximum() {
			return this.source.getMaximum();
		}

		@Override
		public void setMaximum(final int newMaximum) {
			// no implementar
		}

		@Override
		public int getValue() {
			return this.source.getValue();
		}

		@Override
		public void setValue(final int newValue) {
			// no implementar
			System.out.println("SetValue: " + newValue);
		}

		@Override
		public void setValueIsAdjusting(final boolean b) {
			// no implementar
		}

		@Override
		public boolean getValueIsAdjusting() {
			return this.source.getValueIsAdjusting();
		}

		@Override
		public int getExtent() {
			return this.source.getExtent();
		}

		@Override
		public void setExtent(final int newExtent) {
			// no implementar
		}

		@Override
		public void setRangeProperties(final int value, final int extent, final int min, final int max, final boolean adjusting) {
			// no implementar
		}

		@Override
		public void addChangeListener(final ChangeListener x) {
			this.source.addChangeListener(x);
		}

		@Override
		public void removeChangeListener(final ChangeListener x) {
			this.source.removeChangeListener(x);
		}

	}

	/**
	 * Creates the <code>JScrollpane</code> that contains the Table. This method should assign field
	 * class: <code>scrollpane</code>.
	 *
	 */
	protected JScrollPane createSumRowBlockedScrollPane(final Map params) {
		final JScrollPane sumRowBlockedScrollPane = new JScrollPane(this.sumRowBlockedTable) {

			@Override
			public void setBorder(final Border border) {
				// TODO Fix bug ontimize plaf 1.0.7
				if (border instanceof EmptyBorder) {
					return;
				}
				super.setBorder(border);
			}
		};

		sumRowBlockedScrollPane.setHorizontalScrollBar(new JScrollBar(Adjustable.HORIZONTAL) {
			@Override
			protected void paintComponent(final Graphics g) {
			}

			@Override
			public void paint(final Graphics g) {
			}
		});

		sumRowBlockedScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		sumRowBlockedScrollPane.setOpaque(false);

		if (ApplicationManager.useOntimizePlaf) {
			final Color gridColor = this.table.getGridColor();
			final LineBorder border = new LineBorder(gridColor, 1);
			sumRowBlockedScrollPane.setBorder(new SumRowBorder(border, new Insets(-1, 0, 0, 0)));
		} else {
			final Border currentBorder = sumRowBlockedScrollPane.getBorder();
			if (currentBorder != null) {
				sumRowBlockedScrollPane.setBorder(new SumRowBorder(currentBorder, new Insets(0, -1, -1, -1)));
			}
		}

		final BlockedBoundedRangeModel rangeModel = new BlockedBoundedRangeModel(
				sumRowBlockedScrollPane.getHorizontalScrollBar().getModel());
		this.blockedScrollPane.getHorizontalScrollBar().setModel(rangeModel);

		return sumRowBlockedScrollPane;
	}

	/**
	 * Creates the JTable contained in the <code>Table</code>
	 * @return the <code>JTable</code> contained by the <code>Table</code>
	 */
	protected JTable createTable() {
		final ExtendedTableModel model = this.createExtendedTableModel();
		final TableSorter sorter = this.createTableSorter(model);
		sorter.setFitHeadSize(this.autoFixHead);
		sorter.setSizeColumnListener(new TableSorter.ColumnSizeListener() {

			@Override
			public void columnToFitSize(final ColumnSizeEvent e) {
				final int col = e.col;
				if (col >= 0) {
					Table.this.fitColumnSize(Table.this.getJTable().convertColumnIndexToView(col));
				}
			}
		});

		final EJTable eJTable = this.createEJTable(sorter, this.visibleColumns);
		this.enabled = eJTable.isEnabled();
		if (this.fitRowHeight) {
			eJTable.setFitRowsHeight(this.fitRowHeight);
		}
		sorter.enableSort(this.orderEnabled);
		sorter.enableFiltering(this.activatedFilter);

		// Table header renderer
		this.addTableHeaderRenderer(eJTable);

		// Table header listener
		this.addTableHeaderMouseListener(eJTable);

		sorter.setResourceBundle(this.resourcesFile);
		sorter.setData(new Hashtable());
		sorter.setSourceTable(eJTable);

		eJTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		eJTable.getSelectionModel().addListSelectionListener(this.selectionListener);
		eJTable.getModel().addTableModelListener(this.actButtonsModelListener);
		// Now the column with the rows number
		try {
			final TableColumn rowNumberColumn = eJTable.getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
			rowNumberColumn.setMinWidth(0);
			rowNumberColumn.setPreferredWidth(0);
			rowNumberColumn.setWidth(0);
			rowNumberColumn.setMaxWidth(0);
			rowNumberColumn.setResizable(false);
		} catch (final Exception e) {
			Table.logger.error("Table: Row number column", e);
		}
		// Set the column identifiers
		for (int i = 0; i < eJTable.getColumnCount(); i++) {
			final TableColumn column = eJTable.getColumnModel().getColumn(i);
			column.setIdentifier(((TableSorter) eJTable.getModel()).getColumnIdentifier(i));
		}
		// Set the header texts
		for (int i = 0; i < eJTable.getColumnCount(); i++) {
			final TableColumn column = eJTable.getColumnModel().getColumn(i);
			final Object id = column.getIdentifier();
			column.setHeaderValue(ApplicationManager.getTranslation((String) id, this.resourcesFile));
		}

		// Set the columns order using visible columns
		if (Table.ORDER_COLS_BY_VISIBLE_COLS) {
			try {
				for (int i = 0; i < this.originalVisibleColumns.size(); i++) {
					final String s = (String) this.originalVisibleColumns.get(i);
					try {
						final TableColumn tc = eJTable.getColumn(s);
						eJTable.moveColumn(eJTable.convertColumnIndexToView(tc.getModelIndex()), i + 1);
					} catch (final Exception e) {
						Table.logger.error("Column " + s + " not found.", e);
					}

				}
				final TableColumn tc2 = eJTable.getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
				if (tc2 != null) {
					eJTable.moveColumn(eJTable.convertColumnIndexToView(tc2.getModelIndex()), 0);
				}
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
			}

		}

		return eJTable;
	}

	/**
	 * Creates the <code>JScrollpane</code> that contains the Table. This method should assign field
	 * class: <code>scrollpane</code>.
	 *
	 * @since 5.2080EN
	 */
	protected void createJScrollPane(final Map params) {
		this.scrollPane = new JScrollPane(this.table) {

			@Override
			public void setBackground(final Color bg) {
				super.setBackground(bg);
			}
		};

		// To show the vertical scroll only if needed
		if (!ParseUtils.getBoolean((String) params.get("vscroll"), true)) {
			this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		} else {
			this.scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		}

		this.scrollPane.setOpaque(false);
		this.scrollPane.setCorner(ScrollPaneConstants.UPPER_RIGHT_CORNER, this.buttonPlus);
		this.scrollPane.getVerticalScrollBar()
		.setPreferredSize(new Dimension(
				ParseUtils.getInteger((String) params.get("vscrollwidth"),
						this.scrollPane.getVerticalScrollBar().getPreferredSize().width),
				this.scrollPane.getVerticalScrollBar().getPreferredSize().height));

		if (ApplicationManager.useOntimizePlaf) {
			this.scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		}
	}

	protected EJTable createEJTable(final TableSorter sorter, final List visibleColumns) {
		return new EJTable(this, sorter, this.visibleColumns);
	}

	protected TableWaitPanel createWaitPanel() {
		return new TableWaitPanel();
	}

	protected TableInformationPanel createInformationPanel() {
		int offset = 0;
		try {
			offset += this.getJTable().getTableHeader().getPreferredSize().getHeight();
		} catch (final Exception e) {
			Table.logger.trace(null, e);
		}
		try {
			offset += this.controlsPanel.getPreferredSize().getHeight();
		} catch (final Exception e) {
			Table.logger.trace(null, e);
		}

		return new TableInformationPanel(offset);
	}

	public void showInformationPanel(final String msg) {
		synchronized (this) {
			try {
				this.informationPanel.setMessage(ApplicationManager.getTranslation(msg, this.getResourceBundle()));
			} catch (final Exception ex) {
				Table.logger.trace(null, ex);
				this.informationPanel.setMessage(msg);
			}
			this.getGlassPane().setVisible(false);
			this.setGlassPane(this.informationPanel);
			this.getGlassPane().setVisible(true);
			this.repaint();
		}
	}

	public void hideInformationPanel() {
		synchronized (this) {
			if (this.getGlassPane() == this.informationPanel) {
				this.getGlassPane().setVisible(false);
				this.repaint();
			}
		}
	}

	public void hideWaitPanel() {
		synchronized (this) {
			if (this.getGlassPane() == this.waitPanel) {
				this.getGlassPane().setVisible(false);
				this.repaint();
			}
		}
	}

	public void showWaitPanel() {
		synchronized (this) {
			this.setGlassPane(this.waitPanel);
			this.getGlassPane().setVisible(true);
			this.repaint();
		}
	}

	/**
	 * Creates a new TableCellRenderer to use in the table header renderer, by default creates a
	 * SortTableCellRenderer
	 * @param table
	 * @return
	 */
	protected TableCellRenderer createTableHeaderRenderer(final JTable table) {
		Object rend = null;

		if (this.parameters != null) {

			try {
				final String renderClassName = (String) this.parameters.get("tableheaderrenderer");
				if (renderClassName != null) {
					final Class renderClass = Class.forName(renderClassName);
					Constructor constructor = null;
					try {
						constructor = renderClass.getConstructor(new Class[] { JTable.class, Map.class });
						rend = constructor.newInstance(new Object[] { table, this.parameters });
					} catch (final Exception e) {
						Table.logger.trace(null, e);
						try {
							constructor = renderClass.getConstructor(new Class[] { JTable.class });
							rend = constructor.newInstance(new Object[] { table });
						} catch (final Exception e1) {
							Table.logger.trace(null, e1);
						}
					}
				}
			} catch (final Exception e) {
				Table.logger.trace(null, e);
			}
			if ((rend != null) && (rend instanceof TableCellRenderer)) {
				return (TableCellRenderer) rend;
			} else {
				rend = new SortTableCellRenderer(table, this.parameters);
			}
		} else {
			rend = new SortTableCellRenderer(table);
		}
		// ((SortTableCellRenderer)
		// rend).setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
		return (TableCellRenderer) rend;
	}

	/**
	 * Calls the {@link #createTableHeaderRenderer} method and configures the table header rendering
	 * @param table
	 */
	protected void addTableHeaderRenderer(final JTable table) {
		final TableCellRenderer rend = this.createTableHeaderRenderer(table);
		table.getTableHeader().setDefaultRenderer(rend);
		final TableColumnModel tcModel = table.getColumnModel();
		for (int i = 0; i < tcModel.getColumnCount(); i++) {
			final TableColumn tc = tcModel.getColumn(i);
			tc.setHeaderRenderer(rend);
		}
		table.getTableHeader().repaint();
		table.setColumnSelectionAllowed(false);

	}

	/**
	 * Adds a default mouse listener to the table header. Controls all the click dependent behaviour of
	 * the table header.
	 * @param table the table which header will have the listener
	 */
	public void addTableHeaderMouseListener(final JTable table) {
		// Column listener
		this.listMouseListener = new SortTableCellRenderer.ListMouseListener();
		final JTableHeader th = table.getTableHeader();
		th.addMouseListener(this.listMouseListener);
	}

	public void addBlockedTableHeaderMouseListener(final JTable table) {
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

	protected void setScrollTooltipText() {
		if ((this.scrollPane != null) && (this.scrollPane.getVerticalScrollBar() != null)) {
			if (this.isEmpty()) {
				this.scrollPane.getVerticalScrollBar().setToolTipText(null);
				return;

			}
			if (this.tipScrollEnabled) {
				if (this.scrollPane.getVerticalScrollBar().getVisibleAmount() < this.scrollPane.getVerticalScrollBar()
						.getMaximum()) {
					// Show a window
					final Point p = this.scrollPane.getViewport().getViewPosition();
					int topRow = 1;
					int totalHeight = 0;
					for (int i = 0; i < (this.getRealRecordsNumber() - 1); i++) {
						totalHeight += this.table.getRowHeight(i);
						if (totalHeight > p.y) {
							topRow = i + 1;
							break;
						}
					}
					this.scrollPane.getVerticalScrollBar().setToolTipText(this.getScrollText(topRow));
				}
			}
		}
	}

	/**
	 * Installs a {@link #MouseMotionListener} and a {@link #MouseListener} to the vertical scroll, in
	 * order to control the tip.
	 */
	protected void installScrollListener() {
		final JScrollBar scrollBar = this.scrollPane.getVerticalScrollBar();
		scrollBar.addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(final AdjustmentEvent e) {
				Table.this.setScrollTooltipText();
			}
		});

		scrollBar.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(final MouseEvent e) {
				if (Table.this.tipScrollEnabled) {
					if (scrollBar.getVisibleAmount() < scrollBar.getMaximum()) {
						// Show a small window.
						final Point p = Table.this.scrollPane.getViewport().getViewPosition();
						int topRow = 1;
						int totalHeight = 0;
						for (int i = 0; i < (Table.this.getRealRecordsNumber() - 1); i++) {
							totalHeight += Table.this.table.getRowHeight(i);
							if (totalHeight > p.y) {
								topRow = i + 1;
								break;
							}
						}
						scrollBar.setToolTipText(Table.this.getScrollText(topRow));
						Table.this.hideTipScroll();
						Table.this.showTipScroll(scrollBar, 0, 0, topRow);
					}
				}
			}
		});
		scrollBar.addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(final MouseEvent e) {
				if (Table.this.tipScrollEnabled) {
					if (scrollBar.getVisibleAmount() < scrollBar.getMaximum()) {
						// Show a small window
						final Point p = Table.this.scrollPane.getViewport().getViewPosition();
						int topRow = 1;
						int totalHeight = 0;
						for (int i = 0; i < (Table.this.getRealRecordsNumber() - 1); i++) {
							totalHeight += Table.this.table.getRowHeight(i);
							if (totalHeight > p.y) {
								topRow = i + 1;
								break;
							}
						}

						scrollBar.setToolTipText(Table.this.getScrollText(topRow));
						Table.this.hideTipScroll();
						Table.this.showTipScroll(scrollBar, 0, 0, topRow);
					}
				}
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				Table.this.hideTipScroll();
			}

		});

	}

	/**
	 * Shows the tip corresponding to the vertical scroll.
	 * @param scrollBar
	 * @param x
	 * @param y
	 * @param viewRowIndex
	 */
	protected void showTipScroll(final JScrollBar scrollBar, final int x, final int y, final int viewRowIndex) {
		if (this.tipScroll == null) {
			this.tipScroll = new TipScroll(SwingUtilities.getWindowAncestor(scrollBar));
		}
		final StringBuilder s = new StringBuilder(Table.SPACE);
		s.append(viewRowIndex);
		s.append(Table.SPACE);
		s.append(ApplicationManager.getTranslation(Table.OF, this.resourcesFile));
		s.append(Table.SPACE);
		s.append(this.getCurrentRowCount());
		s.append(Table.SPACE);
		s.append(Table.SPACE);
		s.append("(");
		s.append(Table.TOTAL_es_ES);
		s.append(this.getRealRecordsNumber());
		s.append(")");
		s.append(Table.SPACE);
		this.tipScroll.show(scrollBar, x, y, s.toString());
	}

	/**
	 * Creates the text to be displayed in the scroll.
	 * @param viewRowIndex
	 * @return
	 */
	protected String getScrollText(final int viewRowIndex) {
		final StringBuilder s = new StringBuilder(Table.SPACE);
		s.append(viewRowIndex);
		s.append(Table.SPACE);
		s.append(ApplicationManager.getTranslation(Table.OF, this.resourcesFile));
		s.append(Table.SPACE);
		s.append(this.getCurrentRowCount());
		s.append(Table.SPACE);
		s.append(Table.SPACE);
		s.append("(");
		s.append(Table.TOTAL_es_ES);
		s.append(this.getRealRecordsNumber());
		s.append(")");
		s.append(Table.SPACE);
		return s.toString();
	}

	/**
	 * Hides the tip corresponding to the vertical scroll.
	 */
	protected void hideTipScroll() {
		if (this.tipScroll != null) {
			this.tipScroll.setVisible(false);
		}
	}

	/**
	 * Configures the columns to show in the <code>Table</code> init process.
	 */
	protected void setVisibleColumns() {

		if ((this.visibleColumns != null) && !this.visibleColumns.isEmpty()) {
			// From 0 because the first column contains the row number
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final String col = this.table.getColumnName(i);
				if (!(this.visibleColumns.contains(col)) || !this.checkColumnTablePermission(col, "visible")) {
					this.hideColumn(col);
				} else {
					this.showColumn(col);
				}
			}
			this.table.sizeColumnsToFit(-1);
		}
		this.setRowNumberColumnVisible(this.visibleRowNumberColumn);
	}

	protected void showColumn(final String col) {
		final int colIndex = this.table.getColumnModel().getColumnIndex(col);
		final int index = this.blockedTable.getBlockedColumnIndex();
		TableColumn tc = null;
		if ((index > 0) && (colIndex <= index)) {
			tc = this.blockedTable.getColumnModel().getColumn(colIndex);
		} else {
			tc = this.table.getColumnModel().getColumn(colIndex);
		}
		tc.setMinWidth(0);
		tc.setMaxWidth(Integer.MAX_VALUE);
		tc.setResizable(true);
	}

	protected void hideColumn(final String col) {
		final int colIndex = this.table.getColumnModel().getColumnIndex(col);

		final int index = this.blockedTable.getBlockedColumnIndex();
		TableColumn tc = null;
		if ((index > 0) && (colIndex <= index)) {
			tc = this.blockedTable.getColumnModel().getColumn(colIndex);
		} else {
			tc = this.table.getColumnModel().getColumn(colIndex);
		}
		tc.setMinWidth(0);
		tc.setPreferredWidth(0);
		tc.setWidth(0);
		tc.setMaxWidth(0);
		tc.setResizable(false);
	}

	/**
	 * Determines whether the column has its width fixed or not.
	 * @param column
	 * @return true in case the width is fixed, false otherwise
	 */
	protected boolean isColumnWidthFixed(final String column) {
		final TableColumn tc = this.table.getColumn(column);
		if (tc == null) {
			return false;
		}
		final int modelIndex = tc.getModelIndex();
		final int viewIndex = this.table.convertColumnIndexToView(modelIndex);
		if (this.columnWidthSet.length <= viewIndex) {
			return false;
		} else {
			if (this.columnWidthSet[viewIndex] != 0) {
				return true;
			} else {
				return false;
			}

		}
	}

	/**
	 * Returns a reference an object that implements the {@link TableComponent} interface, stored in the
	 * table {link #controlsPanel}, specified by the key. For example, elements such a
	 * {@link TableButton} and {@link TableButtonSelection} implements this interface.
	 * @param key the key of the TableComponent
	 * @return the TableComponent with the specified key or null if there is not matching
	 */
	public TableComponent getTableComponentReference(final Object key) {
		for (int i = 0; i < this.controlsPanel.getComponentCount(); i++) {
			final Component c = this.controlsPanel.getComponent(i);
			if (c instanceof TableComponent) {
				final TableComponent tableComponent = (TableComponent) c;
				final Object o = tableComponent.getKey();
				if ((o != null) && o.equals(key)) {
					return tableComponent;
				}
			}
			if (c instanceof GroupTableButton) {
				final Component[] components = ((GroupTableButton) c).getInnerComponents();
				for (final Component currentC : components) {
					if (currentC instanceof TableComponent) {
						final TableComponent tableComponent = (TableComponent) currentC;
						final Object o = tableComponent.getKey();
						if ((o != null) && o.equals(key)) {
							return tableComponent;
						}
					}
				}
			}
		}
		return null;
	}

	public List getTableComponentReferences() {
		final List result = new ArrayList();
		for (int i = 0; i < this.controlsPanel.getComponentCount(); i++) {
			final Component c = this.controlsPanel.getComponent(i);
			if (c instanceof TableComponent) {
				result.add(c);
			}

			if (c instanceof GroupTableButton) {
				final Component[] components = ((GroupTableButton) c).getInnerComponents();
				for (final Component currentC : components) {
					if (currentC instanceof TableComponent) {
						result.add(currentC);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Sets whether or not the specified component of the table should be enabled or not. Permissions
	 * are checked in order to verify that the user that tries to enable the component has the rights to
	 * do so.
	 * @param key the name of the component
	 * @param enabled true if the component should be enabled, false otherwise
	 * @see #checkComponentTablePermission
	 */
	public void setTableComponentEnabled(final String key, final boolean enabled) {
		final TableComponent c = this.getTableComponentReference(key);
		if ((c != null) && (c instanceof Component)) {
			final boolean permission = this.checkComponentTablePermission(key, "enabled");
			if (permission) {
				((Component) c).setEnabled(enabled);
			} else {
				((Component) c).setEnabled(permission);
			}
		}
	}

	/**
	 * Sets whether or not the table component is visible.
	 * @param key the component name
	 * @param visible true if this component should be enabled, false otherwise
	 */
	public void setTableComponentVisible(final String key, final boolean visible) {
		final TableComponent c = this.getTableComponentReference(key);
		if ((c != null) && (c instanceof Component)) {
			final boolean permission = this.checkComponentTablePermission(key, "visible");
			if (permission) {
				((Component) c).setVisible(visible);
			} else {
				((Component) c).setVisible(permission);
			}
		}
	}

	private static boolean checkFormatDate(final String checkString) {
		try {
			final StringTokenizer tokens = new StringTokenizer(checkString, "/");
			String date = null;
			String month = null;
			String year = null;
			while (tokens.hasMoreElements()) {
				if (date == null) {
					date = tokens.nextToken().trim();
					if (date.length() > 2) {
						return false;
					}
					Integer.parseInt(date);
				} else if (month == null) {
					month = tokens.nextToken().trim();
					if (month.length() > 2) {
						return false;
					}
					Integer.parseInt(month);
				} else if (year == null) {
					year = tokens.nextToken().trim();
					if (year.length() > 4) {
						return false;
					}
					Integer.parseInt(year);
				} else {
					return false;
				}
			}
			return true;
		} catch (final Exception exc) {
			Table.logger.trace(null, exc);
			return false;
		}
	}

	/**
	 * This method configure appearance of quickfilter box.
	 * @param params the <code> Hashtable</code> with parameters
	 *
	 *        <p>
	 *        The attributes allowed are:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</b></td>
	 *        <td><b>values</b></td>
	 *        <td><b>default</b></td>
	 *        <td><b>required</b></td>
	 *        <td><b>meaning</b></td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>quickfilterfont</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Font for quickfilter text</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>quickfilterfontcolor</td>
	 *        <td></td>
	 *        <td>black</td>
	 *        <td>no</td>
	 *        <td>Color for quickfilter font</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>quickfilterborder</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Specific border for quickfilter</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>quickfilteremptyfont</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Font for message showed when user does not type any value to search.</td>
	 *        </tr>
	 *
	 *        </Table>
	 */
	protected void configureQuickFilter(final Map params) {
		if (this.quickFilterText != null) {
			this.quickFilterText
			.setFont(ParseUtils.getFont((String) params.get("quickfilterfont"), this.quickFilterText.getFont()));
			this.quickFilterText.setForeground(ParseUtils.getColor((String) params.get("quickfilterfontcolor"),
					this.quickFilterText.getForeground()));
			this.quickFilterText.setBorder(
					ParseUtils.getBorder((String) params.get("quickfilterborder"), this.quickFilterText.getBorder()));
			this.quickFilterText.setEmptyFont(ParseUtils.getFont((String) params.get("quickfilteremptyfont"), null));
		}
	}

	/**
	 * This method configure appearance of table buttons: border, opacity, ... Finally, this method also
	 * calls to {@link #changeButtonIcon(JButton, String, boolean, boolean, MouseListener)} for each
	 * table button.
	 * @param params the <code> Hashtable</code> with parameters
	 *
	 *        <p>
	 *        The attributes allowed are:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME= BOX>
	 *        <tr>
	 *        <td><b>attribute</b></td>
	 *        <td><b>values</b></td>
	 *        <td><b>default</b></td>
	 *        <td><b>required</b></td>
	 *        <td><b>meaning</b></td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>borderbuttons</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Defines whether table buttons have border or not.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaquebuttons</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Defines whether table buttons are opaque or not.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>highlightbuttons</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Defines whether buttons are highlighted when mouse is entered over them.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>changeviewicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>copyicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>excelicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>htmlicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>inserticon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>printicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>deleteicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>refreshicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>charticon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *
	 *        <tr>
	 *        <td>visiblecolsicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>sumrowicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>savefiltericon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>calculatedcolsicon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>defaultcharticon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>reporticon</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Path to this Table icon</td>
	 *        </tr>
	 *
	 *        </table>
	 */
	protected void configureButtons(final Map params) {
		this.borderbuttons = ParseUtils.getBoolean((String) params.get("borderbuttons"), true);
		this.opaquebuttons = ParseUtils.getBoolean((String) params.get("opaquebuttons"), true);
		if (ParseUtils.getBoolean((String) params.get("highlightbuttons"), false)) {
			this.listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButtonIcon(this.groupTableButton, (String) params.get("grouptableicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);

		this.changeButtonIcon(this.buttonChangeView, (String) params.get("changeviewicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonCopy, (String) params.get("copyicon"), this.borderbuttons, this.opaquebuttons,
				this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonExcelExport, (String) params.get("excelicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonHTMLExport, (String) params.get("htmlicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonPlus2, (String) params.get("inserticon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonPrint, (String) params.get("printicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonDelete, (String) params.get("deleteicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonRefresh, (String) params.get("refreshicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonChart, (String) params.get("charticon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonVisibleColsSetup, (String) params.get("visiblecolsicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonSumRowSetup, (String) params.get("sumrowicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonSaveFilterOrderSetup, (String) params.get("savefiltericon"),
				this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonCalculatedColumns, (String) params.get("calculatedcolsicon"),
				this.borderbuttons, this.opaquebuttons, this.listenerHighlightButtons);
		this.changeButtonIcon(this.buttonPivotTable, (String) params.get("pivottableicon"), this.borderbuttons,
				this.opaquebuttons, this.listenerHighlightButtons);

		if (this.buttonDefaultChart != null) {
			final ImageIcon ii = ParseUtils.getImageIcon((String) params.get("defaultcharticon"), null);
			if (ii != null) {
				this.buttonDefaultChart.setIcon(ii);
				this.buttonDefaultChart.getPreferredSize().height = ii.getIconHeight() + 2;
				this.buttonDefaultChart.getPreferredSize().width = ii.getIconWidth() + 18;
			}
			this.changeButtonIcon(this.buttonDefaultChart.getButton(), (String) params.get("defaultcharticon"),
					this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.changeButtonIcon(this.buttonDefaultChart.getMenuButton(), null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);

		}
		if (this.buttonReports != null) {
			final ImageIcon ii = ParseUtils.getImageIcon((String) params.get("reporticon"), null);
			if (ii != null) {
				this.buttonReports.setIcon(ii);
				this.buttonReports.getPreferredSize().height = ii.getIconHeight() + 2;
				this.buttonReports.getPreferredSize().width = ii.getIconWidth() + 18;
			}
			this.changeButtonIcon(this.buttonReports.getButton(), (String) params.get("reporticon"), this.borderbuttons,
					this.opaquebuttons, this.listenerHighlightButtons);
			this.changeButtonIcon(this.buttonReports.getMenuButton(), null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
		}

		if (this.pageFetcher != null) {
			this.changeButtonIcon(this.pageFetcher.downloadAllButton, null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.changeButtonIcon(this.pageFetcher.firstPageButton, null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.changeButtonIcon(this.pageFetcher.lastPageButton, null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.changeButtonIcon(this.pageFetcher.nextPageButton, null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
			this.changeButtonIcon(this.pageFetcher.previousPageButton, null, this.borderbuttons, this.opaquebuttons,
					this.listenerHighlightButtons);
		}
	}

	protected void changeButtonIcon(final JButton button, final String iconpath, final boolean borderButtons, final boolean opaqueButtons,
			final MouseListener listenerHighlightButtons) {
		if (button != null) {
			final ImageIcon icon = ParseUtils.getImageIcon(iconpath, (ImageIcon) button.getIcon());
			if ((iconpath != null) && (button instanceof TableComponent)) {
				if (this.buttonIcons == null) {
					this.buttonIcons = new Hashtable();
				}
				this.buttonIcons.put(((TableComponent) button).getKey(), icon);
			}
			button.setIcon(icon);
			if (button.getIcon() != null) {
				button.setPreferredSize(
						new Dimension(button.getIcon().getIconWidth() + 6, button.getIcon().getIconHeight() + 6));
			}

			button.setFocusPainted(false);
			if (!borderButtons) {
				button.setBorder(BorderFactory.createEmptyBorder());
			}
			if (!opaqueButtons) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (listenerHighlightButtons != null) {
				button.addMouseListener(listenerHighlightButtons);
			}
			final Icon pressedIcon = ParseUtils.getPressedImageIcon("yes", iconpath, null);
			if (pressedIcon != null) {
				button.setPressedIcon(pressedIcon);
			}
			final Icon disabledIcon = ParseUtils.getDisabledImageIcon("yes", iconpath, null);
			if (disabledIcon != null) {
				button.setDisabledIcon(disabledIcon);
			}
		}
	}

	protected void installQuickFilter(final JPanel pTopControls) {
		if (this.installQuickFilter) {
			pTopControls.add(this.lInfoFilter, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			this.quickFilterText = this.createQuickFilter();
			this.quickFilterText.setSelectAll(this.quickfilterOnFocusSelectAll);

			pTopControls.add(this.quickFilterText, new GridBagConstraints(2, 0, 1, 1, 0, 0,
					GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 6, 0), 0, 0));
		} else {
			pTopControls.add(this.lInfoFilter, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		}
	}

	protected void installDeleteListener() {
		if ((this.allowDelete) && (this.buttonDelete != null)) {
			// Delete icon
			this.buttonDelete.setMargin(new Insets(0, 0, 0, 0));
			final ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.TABLE_REMOVE);
			if (deleteIcon != null) {
				this.buttonDelete.setIcon(deleteIcon);
			} else {
				this.buttonDelete.setText("Delete");
			}
			this.setTableComponentEnabled(Table.BUTTON_DELETE, false);
			this.buttonDelete.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent actionEvent) {
					Table.this.checkRefreshThread();
					if (Table.this.isGrouped()) {
						Table.this.parentForm.message(Table.M_CANT_DELETE_TABLE_IS_GROUPED, Form.WARNING_MESSAGE);
						return;
					}

					final int[] selectedRows = Table.this.getSelectedRows();
					// Ascendent sort
					if (selectedRows.length > 0) {
						final int option = Table.this.parentForm.message(Table.M_WOULD_YOU_LIKE_TO_DELETE_ROWS,
								Form.QUESTION_MESSAGE);
						if (option == Form.YES) {
							if (selectedRows.length == 1) {
								for (int i = 0; i < selectedRows.length; i++) {
									final int iSelectedRow = selectedRows[i];
									try {
										final EntityResult res = Table.this.deleteEntityRow(iSelectedRow);
										if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
											Table.this.deleteRow(iSelectedRow);
										} else {
											Table.this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
										}
									} catch (final Exception ex) {
										Table.logger.error(null, ex);
										Table.this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
									}
								}
							} else {
								final ExtendedOperationThread op = new ExtendedOperationThread("DeleteOperation") {

									@Override
									public void run() {
										this.hasStarted = true;
										this.progressDivisions = selectedRows.length;
										Thread.yield();
										final String sCancelText = ApplicationManager.getTranslation("CanceledOperation",
												Table.this.resourcesFile);
										Thread.yield();

										String deleteText = Table.DELETED_ROW;
										Thread.yield();
										deleteText = ApplicationManager.getTranslation(Table.DELETED_ROW,
												Table.this.resourcesFile);
										Thread.yield();
										final String sErrorDeletingText = ApplicationManager
												.getTranslation("ErrorDeletingRow", Table.this.resourcesFile);
										Thread.yield();
										final List vRemovedRowsIndex = new Vector();
										final long t = System.currentTimeMillis();
										for (int i = 0; i < selectedRows.length; i++) {
											final int iSelectedRow = selectedRows[i];
											try {
												if (this.isCancelled()) {
													this.status = sCancelText;
													Thread.sleep(1000);
													break;
												}
												Thread.yield();
												final EntityResult res = Table.this.deleteEntityRow(iSelectedRow);
												if (res.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
													vRemovedRowsIndex.add(new Integer(iSelectedRow));
													this.status = deleteText + " " + iSelectedRow;
												} else {
													this.status = sErrorDeletingText + " " + iSelectedRow + " "
															+ res.getMessage();

												}
											} catch (final Exception ex) {
												Table.logger.trace(null, ex);
												this.status = sErrorDeletingText + " " + iSelectedRow + " "
														+ ex.getMessage();
											}
											this.currentPosition++;
											final long t2 = System.currentTimeMillis();
											this.estimatedTimeLeft = ((int) (t2 - t) / (i + 1))
													* (selectedRows.length - (i + 1));
										}
										// Update the table
										final int[] index = new int[vRemovedRowsIndex.size()];
										for (int i = 0; i < vRemovedRowsIndex.size(); i++) {
											index[i] = ((Integer) vRemovedRowsIndex.get(i)).intValue();
										}
										Table.this.deleteRows(index);
									}
								};
								final Window w = SwingUtilities.getWindowAncestor(Table.this);
								if (w instanceof Frame) {
									ApplicationManager.proccessOperation((Frame) w, op, 0);
								} else {
									ApplicationManager.proccessOperation((Dialog) w, op, 0);
								}
							}
						}
					}
				}
			});
		}
	}

	protected void installRefreshListener() {
		if ((this.setRefreshButton) && (this.buttonRefresh != null)) {
			// Refresh icon
			this.buttonRefresh.setMargin(new Insets(0, 0, 0, 0));
			final ImageIcon refreshIcon = ImageManager.getIcon(ImageManager.TABLE_REFRESH);
			if (refreshIcon != null) {
				this.buttonRefresh.setIcon(refreshIcon);
			} else {
				this.buttonRefresh.setText("Refresh");
			}
			this.setTableComponentEnabled(Table.BUTTON_REFRESH, true);
			this.buttonRefresh.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					try {
						if (Table.this.parentForm != null) {
							Table.this.parentForm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						}
						Table.this.refreshEDT(true);
					} catch (final Exception ex) {
						Table.logger.trace(null, ex);
					} finally {
						if (Table.this.parentForm != null) {
							Table.this.parentForm.setCursor(Cursor.getDefaultCursor());
						}
					}
				}
			});
		}
	}

	protected void installHTMLExportListener() {
		// HTML export button icon
		final ImageIcon netIcon = ImageManager.getIcon(ImageManager.TABLE_HTML);
		if (netIcon != null) {
			this.buttonHTMLExport.setMargin(new Insets(0, 0, 0, 0));
			// buttonHTMLExport.setIcon(new
			// ImageIcon(netIcon.getImage().getScaledInstance(16, 16,
			// java.awt.Image.SCALE_DEFAULT)));
			this.buttonHTMLExport.setIcon(netIcon);
		} else {
			this.buttonHTMLExport.setText("HTML");
		}
		// Button listener
		this.buttonHTMLExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				// Create an HTML document, but before show the option
				// to set
				// a title
				final JFileChooser fileChooser = new JFileChooser();
				fileChooser.setFileFilter(new HTMLFileFilter());
				final int option = fileChooser.showSaveDialog(Table.this.parentFrame);
				if (option == JFileChooser.APPROVE_OPTION) {
					try {
						File selectedFile = fileChooser.getSelectedFile();
						final String selectedFileString = selectedFile.getPath();
						if (!selectedFileString.substring(selectedFileString.length() - 4, selectedFileString.length())
								.equalsIgnoreCase(".htm")) {
							selectedFile = new File(selectedFileString + ".htm");
						}
						final FileWriter fw = new FileWriter(selectedFile);
						final String htmlStringValue = Table.this.getHTMLString();
						fw.write(htmlStringValue, 0, htmlStringValue.length());
						fw.flush();
						fw.close();
					} catch (final Exception e) {
						Table.logger.error("Exception trying to save the file. ", e);
						if (e instanceof SecurityException) {
							Table.this.parentForm.message("table.operation_cannot_be_performed", Form.WARNING_MESSAGE,
									"table.security_error_message");
						}
					}
				}
			}
		});
	}

	protected void installCopyListener() {
		// Copy button icon
		final ImageIcon copyIcon = ImageManager.getIcon(ImageManager.TABLE_COPY);
		if (copyIcon != null) {
			this.buttonCopy.setMargin(new Insets(0, 0, 0, 0));
			// buttonCopy.setIcon(new
			// ImageIcon(copyIcon.getImage().getScaledInstance(16, 16,
			// java.awt.Image.SCALE_DEFAULT)));
			this.buttonCopy.setIcon(copyIcon);
		} else {
			this.buttonCopy.setText("Copy");
		}

		// Buttons listeners
		this.buttonCopy.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				final Cursor c = Table.this.getCursor();
				try {
					Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					final StringSelection sSelection = new StringSelection(Table.this.getExcelString());
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sSelection, sSelection);
				} catch (final Exception e) {
					if (e instanceof SecurityException) {
						Table.logger.error("Exception setting clipboard contents", e);
						java.security.AccessController.doPrivileged(new java.security.PrivilegedAction() {

							@Override
							public Object run() {
								final JTextField textDataField = new JTextField();
								textDataField.setText(Table.this.getExcelString());
								textDataField.selectAll();
								textDataField.copy();
								Table.logger.debug("Copied to clipboard");
								return null;
							}
						});
						Table.this.parentForm.message("table.operation_cannot_be_performed", Form.WARNING_MESSAGE,
								"table.security_error_message");
					}
				} finally {
					Table.this.setCursor(c);
				}
			}
		});
	}

	protected void installExcelExportListener() {
		final ImageIcon excelIcon = ImageManager.getIcon(ImageManager.TABLE_EXCEL);
		if (excelIcon != null) {
			this.buttonExcelExport.setMargin(new Insets(0, 0, 0, 0));
			// buttonExcelExport.setIcon(new
			// ImageIcon(excelIcon.getImage().getScaledInstance(16, 16,
			// java.awt.Image.SCALE_DEFAULT)));
			this.buttonExcelExport.setIcon(excelIcon);
		} else {
			this.buttonExcelExport.setText("Export to Excel");
		}

		this.buttonExcelExport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				final JFileChooser selFile = new JFileChooser();

				final String[] exts = { "xls" };
				final String[] extsXLSX = { "xlsx" };
				final javax.swing.filechooser.FileFilter ffText = new TxtFileFilter();
				final javax.swing.filechooser.FileFilter ffExcel = FileUtils.getExtensionFileFilter("Excel 97-2003 (*.xls)",
						exts);
				final javax.swing.filechooser.FileFilter ffXLSXExcel = FileUtils
						.getExtensionFileFilter("Excel 2007-2010 (*.xlsx)", extsXLSX);

				selFile.addChoosableFileFilter(ffText);
				selFile.addChoosableFileFilter(ffExcel);
				selFile.setFileFilter(ffExcel);
				if (XLSExporterFactory.isAvailableXLSX()) {

					selFile.addChoosableFileFilter(ffXLSXExcel);
					selFile.setFileFilter(ffXLSXExcel);
				}

				// In java7, all-files filter always appears at first,
				// we must force to set xls extension
				// since 5.2078EN-0.4

				final int iChoice = selFile.showSaveDialog(Table.this.parentFrame);
				if (iChoice == JFileChooser.APPROVE_OPTION) {
					try {
						boolean bXLSX = false;
						File selectedFile = selFile.getSelectedFile();
						String selectedFileString = selectedFile.getPath();
						final javax.swing.filechooser.FileFilter ff = selFile.getFileFilter();
						if ((ff == ffExcel) || selectedFileString.endsWith(".xlsx") || (ff == ffXLSXExcel)) {
							if (selectedFileString.endsWith(".xlsx")) {
								if (XLSExporterFactory.isAvailableXLSX()) {
									bXLSX = true;
								} else {
									MessageDialog.showMessage(ApplicationManager.getApplication().getFrame(),
											"table.xlsx_extension_not_supported",
											XLSExporterFactory.getErrorMessage(), JOptionPane.WARNING_MESSAGE,
											Table.this.resourcesFile);
									selectedFileString = selectedFileString.substring(0,
											selectedFileString.length() - 5);
								}
							}

							if (!selectedFileString.endsWith(".xls") && !selectedFileString.endsWith(".xlsx")
									&& (ff == ffXLSXExcel)) {
								bXLSX = true;
								selectedFile = new File(selectedFileString + ".xlsx");
							} else if (!selectedFileString.endsWith(".xls") && !selectedFileString.endsWith(".xlsx")) {
								selectedFile = new File(selectedFileString + ".xls");
							}
							// If selection is xls then save a temporal
							// file and
							// convert
							final File finalFile = selectedFile;
							final boolean xlsx = bXLSX;
							try {
								Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
								final Window w = SwingUtilities.getWindowAncestor(Table.this);
								final OperationThread op = new OperationThread() {

									@Override
									public void run() {
										this.hasStarted = true;
										this.status = "";
										try {
											if (Table.this.resourcesFile != null) {
												this.status = Table.this.resourcesFile
														.getString("table.generating_xls_file");
											} else {
												this.status = "Generating XLS file";
											}
										} catch (final Exception e) {
											Table.logger.error("Generting XLS file", e);
											this.status = "Generting XLS file";
										}
										try {
											// Create the excel file
											final EntityResult res = Table.this.getValueToExport(true, true, true);
											final XLSExporter exporter = XLSExporterFactory
													.instanceXLSExporter(Table.XLS_EXPORT_CLASS);
											final List orderColumns = res.getOrderColumns();
											final Map renderers = Table.this.getAllColumnRenderer();
											for (final Object current : orderColumns) {
												if (current instanceof Table.KeyObject) {
													final KeyObject currentKO = (KeyObject) current;
													if (renderers.containsKey(currentKO.getKey())) {
														renderers.put(currentKO.toString(),
																renderers.get(currentKO.getKey()));
													}
												}
											}
											exporter.createXLS(res, finalFile, null, renderers, res.getOrderColumns(),
													true, xlsx, true);
										} catch (final Exception e) {
											Table.logger.error(null, e);
											MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
													"table.error_generating_xls_file");
										} finally {
											this.hasFinished = true;
										}
									}
								};

								if (w instanceof Dialog) {
									ApplicationManager.proccessOperation((Dialog) w, op, 500);
								} else {
									ApplicationManager.proccessOperation((Frame) w, op, 500);
								}

							} catch (final Exception e) {
								Table.logger.error(null, e);
								MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
										"table.error_generating_xls_file");
							} finally {
								Table.this.setCursor(Cursor.getDefaultCursor());
							}
						} else {
							// If selection is text file then save it
							if (!selectedFileString
									.substring(selectedFileString.length() - 4, selectedFileString.length())
									.equalsIgnoreCase(".txt")) {
								selectedFile = new File(selectedFileString + ".txt");
							}
							final FileWriter fw = new FileWriter(selectedFile);
							final String cadenaExcel = Table.this.getExcelString();
							fw.write(cadenaExcel, 0, cadenaExcel.length());
							fw.flush();
							fw.close();
						}
					} catch (final Exception e) {
						Table.logger.error("Exception trying to save the file ", e);
						if (e instanceof SecurityException) {
							Table.this.parentForm.message("table.operation_cannot_be_performed", Form.WARNING_MESSAGE,
									"table.security_error_message");
						} else {
							Table.this.parentForm.message("M_XLS_EXPORT_ERROR", Form.WARNING_MESSAGE, e);
						}
					}
				}
			}
		});

	}

	protected void installGroupTableButton() {
		if (this.groupTableButton == null) {
			this.groupTableButton = new GroupTableButton();
			this.groupTableButton.addPropertyChangeListener(ControlPanel.CHANGE_BUTTON_PROPERTY,
					this.buttonChangeListener);
			this.groupTableButton.setKey(Table.GROUP_TABLE_KEY);
			this.groupTableButton.setMargin(new Insets(0, 0, 0, 0));
			final ImageIcon groupIcon = ImageManager.getIcon(ImageManager.TABLE_GROUP);
			if (groupIcon != null) {
				this.groupTableButton.setIcon(groupIcon);
			}
		}
	}

	protected void installPrintButton() {
		if (this.buttonPrint == null) {
			this.buttonPrint = new TableButton();
			if (this.buttonPrint instanceof TableComponent) {
				((TableComponent) this.buttonPrint).setKey(Table.BUTTON_PRINTING);
			}
		}
	}

	protected void installCopyButton() {
		if (this.buttonCopy == null) {
			this.buttonCopy = new TableButton();
			if (this.buttonCopy instanceof TableComponent) {
				((TableComponent) this.buttonCopy).setKey(Table.BUTTON_COPY);
			}
		}
	}

	/**
	 * Installs the control panel buttons, and the listeners associated to them, when necessary.
	 */
	protected void installButtonsListener() {

		if (this.showControls) {
			if (this.defaultButtons) {
				this.controlsPanel.addPropertyChangeListener(ControlPanel.CHANGE_BUTTON_PROPERTY,
						this.buttonChangeListener);

				this.installGroupTableButton();

				this.installPrintButton();

				this.installCopyButton();

				if (this.buttonExcelExport == null) {
					this.buttonExcelExport = new TableButton();
					if (this.buttonExcelExport instanceof TableComponent) {
						((TableComponent) this.buttonExcelExport).setKey(Table.BUTTON_EXCEL_EXPORT);
					}
				}

				if (this.buttonHTMLExport == null) {
					this.buttonHTMLExport = new TableButton();
					if (this.buttonHTMLExport instanceof TableComponent) {
						((TableComponent) this.buttonHTMLExport).setKey(Table.BUTTON_HTML_EXPORT);
					}
				}

				if (this.buttonPlus2 == null) {
					this.buttonPlus2 = new TableButton();
					if (this.buttonPlus2 instanceof TableComponent) {
						((TableComponent) this.buttonPlus2).setKey(Table.BUTTON_PLUS);
					}
					this.buttonPlus2.setActionCommand("Table+");
				}
				this.buttonPrint.setMargin(new Insets(0, 0, 0, 0));
				final ImageIcon printIcon = ImageManager.getIcon(ImageManager.TABLE_PRINT);
				if (printIcon == null) {
					this.buttonPrint.setText("print");
				} else {
					this.buttonPrint.setIcon(printIcon);
				}

				this.buttonPrint.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent evento) {
						Table.this.print();
					}
				});

				// Controls panel
				this.controlsPanel.add(this.groupTableButton);
				this.controlsPanel.add(this.buttonCopy);
				this.controlsPanel.add(this.buttonExcelExport);
				this.controlsPanel.add(this.buttonHTMLExport);
				this.controlsPanel.add(this.buttonPrint);

				if (!this.dynamicTable) {
					this.controlsPanel.add(this.buttonPlus2);
				}

				if ((!this.dynamicTable || (this.dynamicPivotable)) && (this.buttonSaveFilterOrderSetup == null)
						&& Table.PERMIT_SAVE_FILTER_ORDER_CONFIGURATION) {

					this.buttonSaveFilterOrderSetup = new TableButtonPopupble();
				}

				if (this.buttonSaveFilterOrderSetup != null) {
					if (this.buttonSaveFilterOrderSetup instanceof TableComponent) {
						((TableComponent) this.buttonSaveFilterOrderSetup).setKey(Table.BUTTON_SAVE_FILTER_ORDER_SETUP);
					}
					// Save button icon
					final ImageIcon saveTableFilterIcon = ImageManager.getIcon(ImageManager.TABLE_SAVE_TABLE_FILTER);
					if (saveTableFilterIcon != null) {
						this.buttonSaveFilterOrderSetup.setMargin(new Insets(0, 0, 0, 0));
						// buttonSaveFilterOrderSetup.setIcon(new
						// ImageIcon(saveTableFilterIcon.getImage().getScaledInstance(16,
						// 16, java.awt.Image.SCALE_DEFAULT)));
						this.buttonSaveFilterOrderSetup.setIcon(saveTableFilterIcon);
					} else {
						this.buttonSaveFilterOrderSetup.setText("...");
					}
					this.controlsPanel.add(this.buttonSaveFilterOrderSetup);

					this.buttonSaveFilterOrderSetup.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							// Save order and filter configuration
							Table.this.configureFilterOrder(e);
						}
					});
				}

				if (this.allowDelete) {
					this.buttonDelete = new TableButton();
					this.buttonDelete.setToolTipText(Table.DELETE_BUTTON);
					if (this.buttonDelete instanceof TableComponent) {
						((TableComponent) this.buttonDelete).setKey(Table.BUTTON_DELETE);
					}
					this.controlsPanel.add(this.buttonDelete);
				}
				if (this.setRefreshButton) {
					this.buttonRefresh = new TableButton();
					this.buttonRefresh.setToolTipText(Table.REFRESH_BUTTON);
					if (this.buttonRefresh instanceof TableComponent) {
						((TableComponent) this.buttonRefresh).setKey(Table.BUTTON_REFRESH);
					}
					this.controlsPanel.add(this.buttonRefresh);
				}

				this.installDefaultChartsButton();
				this.installConfVisibleColsButtons();
				this.installReportButton();
				this.installConfSumRowButton();
				this.installCalculedColsButton();

				if (this.setPivotTableButton) {
					this.installPivotTableButton();
				}

				final ImageIcon insertIcon = ImageManager.getIcon(ImageManager.TABLE_INSERT);
				if (insertIcon != null) {
					this.buttonPlus2.setMargin(new Insets(0, 0, 0, 0));
					this.buttonPlus2.setIcon(insertIcon);
				} else {
					this.buttonPlus2.setText("+");
				}

				this.installCopyListener();

				this.installExcelExportListener();

				this.installHTMLExportListener();

				this.installRefreshListener();

				this.installDeleteListener();
			}

			final JPanel pTopControls = new JPanel(new GridBagLayout());
			pTopControls.add(this.controlsPanel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

			this.installQuickFilter(pTopControls);

			this.lInfoFilter.setFont(this.lInfoFilter.getFont().deriveFont(Font.BOLD));
			this.mainPanel.add(pTopControls, BorderLayout.NORTH);

			// 10 lines
			// FontMetrics fontMetrics = this.getFontMetrics(this.getFont());
			this.setPreferredSize(new Dimension((int) this.getPreferredSize().getWidth(),
					this.preferredSizeInRows * this.table.getRowHeight()));// fontMetrics.getHeight()));

		}
	}

	/**
	 * If there is a detail form set, installs the listeners that manager the interaction between the
	 * <code>Table</code> and the detail form. Two listeners will be installed, one for insertion
	 * related to the table insert button, placed at the table top right corner, and another listening
	 * double clicks events over the data records. one for the
	 *
	 * @see #detail
	 * @see #addRecordListener
	 */
	protected void installDetailFormListener() {
		if (this.formName != null) {
			if (this.buttonPlus == null) {
				this.buttonPlus = new JButton();
			}
			this.buttonPlus.setMargin(new Insets(0, 0, 0, 0));
			this.buttonPlus.setText("+");
			this.buttonPlus.setBorder(new EmptyBorder(0, 0, 0, 0));
			final Font f = this.buttonPlus.getFont();
			this.buttonPlus.setFont(f.deriveFont(f.getSize2D() + (float) 2.0));
			this.buttonPlus.setFont(this.buttonPlus.getFont().deriveFont(Font.BOLD));
			this.buttonPlus.setActionCommand("Table+");
			this.buttonPlus.setForeground(Color.red);

			this.buttonPlus.addActionListener(this.addRecordListener);

			if (this.buttonPlus2 != null) {
				this.buttonPlus2.addActionListener(this.addRecordListener);
			}

			final MouseAdapter detailFormMouseAdapter = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent event) {
					final boolean query = Table.this.checkQueryPermission();
					if ((Table.this.enabled) && (Table.this.formName != null) && Table.this.enabledDetail && query
							&& !Table.this.isGrouped()) {
						Table.this.table.setCursor(ApplicationManager.getDetailsCursor());
						Table.this.blockedTable.setCursor(ApplicationManager.getDetailsCursor());
					}
				}

				@Override
				public void mouseExited(final MouseEvent event) {
					if ((Table.this.enabled) && (Table.this.formName != null)) {
						Table.this.table.setCursor(Cursor.getDefaultCursor());
						Table.this.blockedTable.setCursor(Cursor.getDefaultCursor());
					}
				}

				@Override
				public void mouseClicked(final MouseEvent event) {

					if ((Table.this.enabled) && Table.this.enabledDetail) {
						if ((event.getClickCount() == 2) && (event.getModifiers() != InputEvent.META_MASK)) {
							Table.this.detail(event);
						}
					}
				}
			};
			this.blockedTable.addMouseListener(detailFormMouseAdapter);
			this.table.addMouseListener(detailFormMouseAdapter);
		}
	}

	/**
	 * Determines whether the window in which the table is placed is a Frame or not
	 * @return true in case that the table is in a Frame, false otherwise
	 */
	protected boolean isFrame() {
		final Window w = SwingUtilities.getWindowAncestor(Table.this);
		if (w instanceof Frame) {
			return true;
		}
		return false;
	}

	protected boolean isTabbedFormManagerMainForm() {
		return (this.parentForm.getFormManager() instanceof ITabbedFormManager)
				&& this.parentForm.equals(((ITabbedFormManager) this.parentForm.getFormManager()).getMainForm());
	}

	/**
	 * Opens the detail form for the rows selected in the view
	 * @param event
	 */
	protected void detail(final InputEvent event) {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(this.table.getSelectedRow())) {
			// Inserting row does not open a detail form
			return;
		}

		if ((event instanceof MouseEvent) && event.isControlDown() && (((MouseEvent) event).getClickCount() == 2)
				&& this.isTabbedFormManagerMainForm()) {
			final Cursor tableCursor = this.table.getCursor();
			final Cursor c = this.getCursor();
			try {
				this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				final int selectedIndex = this.table.rowAtPoint(((MouseEvent) event).getPoint());
				Table.this.openDetailForm(selectedIndex);
				((ITabbedFormManager) this.parentForm.getFormManager()).showTab(0);
			} catch (final Exception e) {
				Table.logger.error(null, e);
			} finally {
				this.setCursor(c);
				this.table.setCursor(tableCursor);
			}
			return;
		}

		boolean bOpenNewWindow = this.openInNewWindowByDefault;

		if ((event != null) && event.isShiftDown()) {
			bOpenNewWindow = !bOpenNewWindow;
		}
		if (bOpenNewWindow && this.allowOpenInNewWindow && this.isFrame()) {
			final Cursor tableCursor = this.table.getCursor();
			final Cursor c = this.getCursor();
			try {
				if (this.table.getSelectedRowCount() != 1) {
					return;
				}
				this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				final TableSorter tableSorter = (TableSorter) this.table.getModel();
				for (int i = 0; i < tableSorter.getColumnCount(); i++) {
					if (tableSorter.isSumCell(Table.this.getSelectedRow(), i)) {
						return;
					}
				}
				Table.this.openInNewWindow(Table.this.getSelectedRows());
			} catch (final Exception e) {
				Table.logger.error(null, e);
			} finally {
				this.setCursor(c);
				this.table.setCursor(tableCursor);
			}
		} else {
			if (this.formName != null) {
				if (this.table.getSelectedRowCount() != 1) {
					if ((this.table.getSelectedRow() > 0) && this.isTabbedFormManagerMainForm()) {
						final Cursor tableCursor = this.table.getCursor();
						final Cursor c = this.getCursor();
						try {
							this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							final int[] selectedIndex = this.table.getSelectedRows();
							for (final int index : selectedIndex) {
								try {
									Table.this.openDetailForm(index);
								} catch (final Exception ex) {
									Table.logger.error(null, ex);
								}
							}

							if ((event != null) && event.isControlDown()) {
								((ITabbedFormManager) this.parentForm.getFormManager()).showTab(0);
							}
						} catch (final Exception e) {
							Table.logger.error(null, e);
						} finally {
							this.setCursor(c);
							this.table.setCursor(tableCursor);
						}

					} else {
						this.parentForm.message(Table.M_SELECTION_ONLY_ONE_ROW_TO_OPEN_DETAIL_FORM,
								Form.WARNING_MESSAGE);
					}
					return;

				}
				final Cursor tableCursor = this.table.getCursor();
				final Cursor c = this.getCursor();
				try {
					if (this.table.getSelectedRowCount() != 1) {
						return;
					}
					this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

					final TableSorter ts = (TableSorter) this.table.getModel();
					for (int i = 0; i < ts.getColumnCount(); i++) {
						if (ts.isSumCell(Table.this.getSelectedRow(), i)) {
							return;
						}
					}
					Table.this.openDetailForm(Table.this.getSelectedRow());
					if ((event != null) && event.isControlDown()) {
						((ITabbedFormManager) this.parentForm.getFormManager()).showTab(0);
					}
				} catch (final Exception e) {
					Table.logger.error(null, e);
				} finally {
					this.setCursor(c);
					this.table.setCursor(tableCursor);
				}
			} else {
				if (this.parentForm != null) {
					if (this.table.getListeners(MouseListener.class).length <= 1) {
						Table.this.parentForm.message("table.table_has_no_associated_detail_form",
								Form.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}

	/**
	 * Checks the refresh thread to update the GUI while the table update is being performed.
	 */
	protected void checkRefreshThread() {
		if ((this.refreshThread != null) && this.refreshThread.isAlive()) {
			this.refreshThread.interrupt();
			this.refreshThread.stopThread();
			try {
				this.refreshThread.join(200);
			} catch (final Exception e) {
				Table.logger.trace(null, e);
			}
			this.refreshThread = null;
		}
	}

	/**
	 * Opens the table {@link DetailForm} with the data related to the row passed as parameter. The form
	 * will be open in the UPDATE mode.
	 * <p>
	 * In case that the row is a sum row, the form will not be open. If the table is grouped or the user
	 * has not permission to see the form, it will not be open neither.
	 * <p>
	 * @param rowIndex the row which data will be shown in the form
	 */
	public void openDetailForm(final int rowIndex) {
		if (this.debug) {
			this.detailForm = null;
		}
		final boolean bPemission = this.checkQueryPermission();
		if ((this.formName != null) && bPemission && !this.isGrouped()) {
			this.checkRefreshThread();

			// If row is a sum row then the detail form is ot open
			final TableSorter ts = this.getTableSorter();
			for (int i = 0; i < ts.getColumnCount(); i++) {
				if (ts.isSumCell(rowIndex, i)) {
					return;
				}
			}

			if ((this.parentForm.getFormManager() instanceof ITabbedFormManager)
					&& this.parentForm.equals(((ITabbedFormManager) this.parentForm.getFormManager()).getMainForm())) {
				final IDetailForm detailForm = this.createTabbedDetailForm();
				detailForm.setQueryInsertMode();
				if (this.attributesToFix != null) {
					for (int i = 0; i < this.attributesToFix.size(); i++) {
						detailForm.setAttributeToFix(this.hAttributesToFixEquivalences.get(this.attributesToFix.get(i)),
								this.parentForm.getDataFieldValue(this.attributesToFix.get(i)));
					}
				}
				final Map hFilterKeys = this.getParentKeyValues();
				detailForm.resetParentkeys(this.getParentKeys(true));

				detailForm.setParentKeyValues(hFilterKeys);
				detailForm.setKeys(this.getAttributesAndKeysData(), rowIndex);
				detailForm.setUpdateMode();
				detailForm.showDetailForm();
			} else {
				if (this.detailForm == null) {
					this.createDetailForm();
				}
				if (this.operationInMemory) {
					this.detailForm.setEntityName(this.memoryEntity);
				} else {
					this.detailForm.resetEntityName();
				}
				// 9-9-2005 To avoid value events in the fields in update mode

				this.setAttributesToFix();

				this.detailForm.setQueryInsertMode();

				final Map hFilterKeys = this.getParentKeyValues();
				this.detailForm.resetParentkeys(this.getParentKeys(true));

				this.detailForm.setParentKeyValues(hFilterKeys);
				this.detailForm.setKeys(this.getAttributesAndKeysData(), rowIndex);
				this.detailForm.setUpdateMode();
				this.detailForm.showDetailForm();
			}
		} else {
			Table.logger.debug(
					"Form is NULL. Or 'form' tag was not especified, or the user has not permission to open the form to query, or the table is grouped.");
		}
	}

	/**
	 * Opens the table {@link DetailForm} from in Insert mode. The user must have permission to do that
	 * and the form must be specified.
	 */
	public void openInsertDetailForm() {
		final boolean permission = this.checkInsertPermission();
		if (((this.formName != null) || (this.insertFormName != null)) && permission && !this.isGrouped()) {
			this.checkRefreshThread();

			if ((this.parentForm.getFormManager() instanceof ITabbedFormManager)
					&& this.parentForm.equals(((ITabbedFormManager) this.parentForm.getFormManager()).getMainForm())) {
				final IDetailForm detailForm = this.createInsertTabbedDetailForm();
				detailForm.setKeys(new Hashtable(0), 0);

				if (this.attributesToFix != null) {
					for (int i = 0; i < this.attributesToFix.size(); i++) {
						detailForm.setAttributeToFix(this.hAttributesToFixEquivalences.get(this.attributesToFix.get(i)),
								this.parentForm.getDataFieldValue(this.attributesToFix.get(i)));
					}
				}

				final Map hFilterKeys = this.getParentKeyValues();
				detailForm.resetParentkeys(this.getParentKeys(true));
				detailForm.setParentKeyValues(hFilterKeys);
				detailForm.setInsertMode();
				detailForm.showDetailForm();
			} else {
				if (this.insertFormName != null) {
					if (this.insertDetailForm == null) {
						this.createInsertDetailForm();
					}
				} else {
					if (this.detailForm == null) {
						this.createDetailForm();
					}
				}

				DetailForm detailform;
				if (this.insertDetailForm != null) {
					detailform = this.insertDetailForm;
				} else {
					detailform = this.detailForm;
				}

				if (this.operationInMemory) {
					detailform.setEntityName(this.memoryEntity);
				} else {
					detailform.resetEntityName();
				}

				detailform.setKeys(new Hashtable(0), 0);

				this.setAttributesToFix();
				detailform.resetParentkeys(this.getParentKeys(true));
				// Create a Map with all filter keys
				final Map hFilterKeys = this.getParentKeyValues();

				detailform.setParentKeyValues(hFilterKeys);
				detailform.setInsertMode();
				detailform.showDetailForm();
			}
		} else {
			Table.logger.error("Form is NULL. No 'form' tag was specified.");
		}
	}

	/**
	 * Sets a column editor to the specified column. configures the editor if necessary.
	 * @param col the column name
	 * @param editor the editor to set
	 */
	public void setColumnEditor(final String col, final TableCellEditor editor) {
		this.checkRefreshThread();
		final TableColumn tc = this.table.getColumn(col);
		tc.setCellEditor(editor);

		if (this.locator != null) {
			if (editor instanceof ReferenceComponent) {
				((ReferenceComponent) editor).setReferenceLocator(this.locator);
			}

			if (editor instanceof CachedComponent) {
				if (this.parentForm != null) {
					CacheManager.getDefaultCacheManager(this.locator).addCachedComponent((CachedComponent) editor);
					((CachedComponent) editor).setCacheManager(CacheManager.getDefaultCacheManager(this.locator));
				} else {
					Table.logger.info("Cannot set CacheManager to the editor {}, because for is NULL",
							editor.getClass());
				}
			}
		}
		if ((this.resourcesFile != null) && (editor instanceof Internationalization)) {
			((Internationalization) editor).setComponentLocale(this.locale);
			((Internationalization) editor).setResourceBundle(this.resourcesFile);
		}
		if ((editor instanceof AccessForm) && (this.parentForm != null)) {
			((AccessForm) editor).setParentForm(this.parentForm);
		}
	}

	/**
	 * Allows the specified column to be editable from the <code>Table</code>. The column will not be
	 * updated.
	 * @param col the column name
	 */
	public void setEditableColumn(final String col) {
		this.setEditableColumn(col, false);
	}

	/**
	 * Allows the specified column to be editable from the <code>Table</code>. To update the chages
	 * produced to this columns can be configuired.
	 * @param col the column name
	 * @param updateEntity if true, the column will be updated
	 */
	public void setEditableColumn(final String col, final boolean updateEntity) {
		this.checkRefreshThread();
		if (!this.checkColumnTablePermission(col, "enabled")) {
			return;
		}
		final TableSorter ts = (TableSorter) this.table.getModel();
		if (ts == null) {
			return;
		}
		ts.setEditableColumn(col);

		if (updateEntity) {
			if (!this.editableColumnsUpdateEntity.contains(col)) {
				this.editableColumnsUpdateEntity.add(col);
			}
		} else {
			this.editableColumnsUpdateEntity.remove(col);
		}
	}

	/**
	 * Activates or deactivates the editable mode for the specified column, in order to be the column
	 * editable or not from the <code>Table</code>. Updating the entity can be configured.
	 * @param col the column name
	 * @param updateEntity if true, the column will be updated
	 * @param editable if true, sets the column editable, if false, will avoid the column to be editable
	 */
	public void setEditableColumn(final String col, final boolean updateEntity, final boolean editable) {
		if (editable) {
			this.setEditableColumn(col, updateEntity);
		} else {
			this.checkRefreshThread();
			final TableSorter ts = (TableSorter) this.table.getModel();
			if (ts == null) {
				return;
			}
			ts.setEditableColumn(col, false);
			this.editableColumnsUpdateEntity.remove(col);
		}
		this.repaint();
	}

	/**
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public String getOrderColumn() {
		final int sortColumnModelIndex = ((TableSorter) this.table.getModel()).getFirstSortedColumn();
		if (sortColumnModelIndex < 0) {
			return null;
		}
		final int sortColumnIndex = this.table.convertColumnIndexToView(sortColumnModelIndex);

		return this.table.getColumnName(sortColumnIndex);
	}

	/**
	 * Returns the sorting column names. This is, returns the column names of the columns that are being
	 * used to sort the information in the table. In this case the some columns could not be visible,
	 * although they are used to sort the table.
	 * @return a String[] containing the sorting column names
	 */
	public String[] getOrderColumns() {
		final int[] inds = ((TableSorter) this.table.getModel()).getSortingColumns();
		if (inds.length == 0) {
			return new String[0];
		}
		final String[] res = new String[inds.length];
		for (int i = 0; i < inds.length; i++) {
			final int sortColumnIndex = this.table.convertColumnIndexToView(inds[i]);
			res[i] = this.table.getColumnName(sortColumnIndex);
		}
		return res;
	}

	/**
	 * Returns an array with the sorting type of all the sorting columns. The array size will be the
	 * same of the number of sorting columns present in the table. The value of each position will be
	 * true, if the sorting is ascendant, or false, is the column is ordered in a descendant way. The
	 * index in the array corresponds to the application sorting order, this is, the index 0 will
	 * correspond with the first sorting applied, etc.
	 * @return
	 */
	public boolean[] getAscendents() {
		return ((TableSorter) this.table.getModel()).getAscendent();
	}

	/**
	 * Returns a List with the names of the columns that have filters applied in the table view.
	 * @return the names of the filtered columns
	 */
	public List getFilterColumn() {
		return ((TableSorter) this.table.getModel()).getFilteredColumns();
	}

	/**
	 * Returns the sorting mode corresponding to the first sorted column
	 * @return true if the first sorted column is ascending. If no ordenation applied or in case that
	 *         there is a sorting applied and this is descendant, the return will be false.
	 */
	public boolean getAscending() {
		return ((TableSorter) this.table.getModel()).isAscending();
	}

	/**
	 * Sorts the table based on the column values. Previous sorting is removed.
	 * @param column the column that will be used for sorting
	 * @param ascendant true if the sorting will start with the smallest values, false otherwise
	 */
	public void sortBy(final String column, final boolean ascendant) {
		if (column == null) {
			return;
		}
		final TableColumn tc = this.table.getColumn(column);
		if (tc != null) {
			final int modelIndex = tc.getModelIndex();
			((TableSorter) this.table.getModel()).resetOrder();
			((TableSorter) this.table.getModel()).sortByColumn(modelIndex, ascendant);
		}
	}

	/**
	 * Sorts the table based on the column values. Previous sorting is mantained.
	 * @param column the column that will be used for sorting
	 * @param ascendant true if the sorting will start with the smallest values, false otherwise
	 */
	public void sortByWithoutReset(final String column, final boolean ascendant) {
		if (column == null) {
			return;
		}
		final TableColumn tc = this.table.getColumn(column);
		if (tc != null) {
			final int modelIndex = tc.getModelIndex();
			((TableSorter) this.table.getModel()).sortByColumn(modelIndex, ascendant);
		}
	}

	/**
	 * Sets the preferred width for all columns.
	 * @return the width set for each column, in pixels, order by the column order in the table
	 */
	protected int[] setPreferredTableColumnWidths() {
		return this.setPreferredTableColumnWidths(this.table.getRowCount());
	}

	/**
	 * Returns the preferred width for the column passed as param.
	 * @param modelColumnIndex the index of the column
	 * @param maxRowNumber the max number of rows to check
	 * @return
	 */
	protected int getPreferredColumnWidth(final int modelColumnIndex, final int maxRowNumber) {
		int width = 0;
		final String sName = this.table.getColumnName(modelColumnIndex);

		final TableColumn tableColumn = this.table.getColumn(sName);
		if (sName.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
			width = tableColumn.getPreferredWidth();
			return width;
		}
		if (!this.isVisibleColumn(sName)) {
			tableColumn.setMinWidth(0);
			tableColumn.setMaxWidth(0);
			tableColumn.setWidth(0);
			width = 0;
			return width;
		} else {
			tableColumn.setMinWidth(10);
			tableColumn.setMaxWidth(10000);
		}
		// If there is no data then initialize the columns with using the header
		try {
			// JRE 1.2 does not contain the function
			// TableCellRenderer.getDefaultRenderer()

			final JTableHeader header = this.table.getTableHeader();
			final TableCellRenderer headerRenderer = header.getDefaultRenderer();
			final Object oHeaderValue = tableColumn.getHeaderValue();
			final Component hederRendererComponent = headerRenderer.getTableCellRendererComponent(this.table, oHeaderValue,
					false, false, 0, 1);
			int headerPreferredWidth = hederRendererComponent.getPreferredSize().width;
			if (hederRendererComponent instanceof JLabel) {
				final FontMetrics metrics = ((JLabel) hederRendererComponent)
						.getFontMetrics(hederRendererComponent.getFont());
				if (oHeaderValue != null) {
					headerPreferredWidth = metrics.stringWidth(oHeaderValue.toString());
				}
			} else if (hederRendererComponent instanceof JTextComponent) {
				final FontMetrics fontMetrics = ((JTextComponent) hederRendererComponent)
						.getFontMetrics(hederRendererComponent.getFont());
				if (oHeaderValue != null) {
					headerPreferredWidth = fontMetrics.stringWidth(oHeaderValue.toString() + 6);
				}
			} else {
				headerPreferredWidth = hederRendererComponent.getPreferredSize().width;
			}
			width = headerPreferredWidth + 4;
		} catch (final Exception e) {
			Table.logger.error("Exception initiating table column width. JRE 1.3 or above is required: ", e);
			width = tableColumn.getPreferredWidth();
		}

		TableCellRenderer renderer = this.table.getDefaultRenderer(this.table.getColumnClass(modelColumnIndex));
		final TableCellRenderer cellRenderer2 = this.table.getColumnModel().getColumn(modelColumnIndex).getCellRenderer();
		if (cellRenderer2 != null) {
			renderer = cellRenderer2;
		}

		for (int j = 0; j < Math.min(this.table.getRowCount(), maxRowNumber); j++) {
			final Object oValue = this.table.getValueAt(j, modelColumnIndex);
			final Component rendererComponent = renderer.getTableCellRendererComponent(this.table, oValue, false, false, 0,
					0);
			int preferredWidth = rendererComponent.getPreferredSize().width;
			if (rendererComponent instanceof JComponent) {
				preferredWidth = preferredWidth - ((JComponent) rendererComponent).getInsets().left
						- ((JComponent) rendererComponent).getInsets().right;
			}
			if (rendererComponent instanceof JTextField) {
				final FontMetrics fontMetrics = ((JTextField) rendererComponent)
						.getFontMetrics(rendererComponent.getFont());
				preferredWidth = fontMetrics.stringWidth(((JTextField) rendererComponent).getText()) + 4;
			} else if (rendererComponent instanceof JLabel) {
				final FontMetrics fontMetrics = ((JLabel) rendererComponent)
						.getFontMetrics(rendererComponent.getFont());
				try {
					String text = ((JLabel) rendererComponent).getText();
					if (text == null) {
						text = "";
					}
					preferredWidth = fontMetrics.stringWidth(text) + 4;
				} catch (final Exception eM) {
					Table.logger.trace(null, eM);
				}
			}
			width = Math.max(preferredWidth + 5, width);

		}
		return width;
	}

	/**
	 * Sets the preferred width for the columns in the table, up to the maximum number of rows.
	 * @param maxRows max number of rows to check
	 * @return the width set for each column, in pixels, order by the column order in the table
	 */
	protected int[] setPreferredTableColumnWidths(final int maxRows) {
		// Calculate the minimun table width to ensure that values are visible
		this.setRowNumberColumnVisible(this.visibleRowNumberColumn);
		final int tableWidth = this.table.getParent().getWidth();
		int columnsWidth = 0;
		int nColsMaxTableWidth = 0;
		final int[] widths = new int[this.table.getColumnCount()];
		final int[] colsWidthMaxTableWidth = new int[this.table.getColumnCount()];
		try {
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final String sName = this.table.getColumnName(i);
				final TableColumn tableColumn = this.table.getColumn(sName);
				widths[i] = this.getPreferredColumnWidth(i, maxRows);
				if (widths[i] > tableWidth) {
					colsWidthMaxTableWidth[i] = widths[i];
					nColsMaxTableWidth++;
				} else {
					columnsWidth += widths[i];
				}

				tableColumn.setWidth(widths[i]);
				tableColumn.setPreferredWidth(widths[i]);
			}
			final int available = tableWidth - columnsWidth;
			if ((available > 0) && (nColsMaxTableWidth > 0)) {
				final int single = available / nColsMaxTableWidth;
				for (int i = 0; i < widths.length; i++) {
					if (colsWidthMaxTableWidth[i] > 0) {
						final String sName = this.table.getColumnName(i);
						final TableColumn tableColumn = this.table.getColumn(sName);
						tableColumn.setWidth(single);
						tableColumn.setPreferredWidth(single);
						widths[i] = single;
					}
				}
			}
		} catch (final OutOfMemoryError errorMem) {
			Table.logger.error("Memory Error", errorMem);
			for (int i = 0; i < this.table.getColumnCount(); i++) {
				final String sName = this.table.getColumnName(i);
				final TableColumn tableColumn = this.table.getColumn(sName);
				widths[i] = tableColumn.getPreferredWidth();
			}
			throw errorMem;
		}
		return widths;
	}

	public boolean isRowNumberColumnVisible() {
		return this.visibleRowNumberColumn;
	}

	/**
	 * Sets whether or not the row number column is visible.
	 * @param visible true if the row number column should be visible, false otherwise
	 */
	public void setRowNumberColumnVisible(final boolean visible) {

		if (this.dynamicTable && visible && (this.getJTable().getRowCount() == 0)) {
			// In a dynamic table only show the column with numbers if there are
			// some data
			this.setRowNumberColumnVisible(false);
			return;
		}

		this.visibleRowNumberColumn = visible;
		if (visible) {
			final TableColumn rowNumbersColumn = this.blockedTable.getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
			final TableCellRenderer renderer = this.blockedTable.getDefaultRenderer(Integer.class);
			final Component rendererComponnt = renderer.getTableCellRendererComponent(this.table,
					new Integer(this.blockedTable.getRowCount()), false, false, 0, 0);
			int preferredWidth = rendererComponnt.getPreferredSize().width;
			if (rendererComponnt instanceof JTextField) {
				final FontMetrics fontMetrics = ((JTextField) rendererComponnt)
						.getFontMetrics(rendererComponnt.getFont());
				preferredWidth = fontMetrics.stringWidth(((JTextField) rendererComponnt).getText()) + 4;
			}
			if (rendererComponnt instanceof JLabel) {
				final FontMetrics fontMetrics = ((JLabel) rendererComponnt)
						.getFontMetrics(rendererComponnt.getFont());
				try {
					String text = ((JLabel) rendererComponnt).getText();
					if (text == null) {
						text = "";
					}
					preferredWidth = fontMetrics.stringWidth(text) + 4;
					preferredWidth = preferredWidth < 15 ? 15 : preferredWidth;
				} catch (final Exception eM) {
					Table.logger.trace(null, eM);
				}
			}
			rowNumbersColumn.setMaxWidth(preferredWidth + 8);
			rowNumbersColumn.setWidth(preferredWidth + 6);
			rowNumbersColumn.setPreferredWidth(preferredWidth + 6);
			rowNumbersColumn.setMinWidth(0);
			// rowNumbersColumn.setResizable(true);
		} else {
			final TableColumn rowNumbersColumn = this.blockedTable.getColumn(ExtendedTableModel.ROW_NUMBERS_COLUMN);
			rowNumbersColumn.setMaxWidth(0);
			rowNumbersColumn.setMinWidth(0);
			rowNumbersColumn.setWidth(0);
			rowNumbersColumn.setPreferredWidth(0);
			rowNumbersColumn.setResizable(false);
		}
		this.mainSplit.setDividerLocation(this.blockedTable.getColumnModel().getTotalColumnWidth());
		this.fixBlockedVisibility();
	}

	/**
	 * Sets the current column width to the preferred column width for the columns past as parameter
	 * @param visibleColumns a List containing the column names
	 * @return
	 */
	protected int[] setPreferredTableColumnWidths(final List visibleColumns) {
		return this.setPreferredTableColumnWidths(visibleColumns, null);
	}

	/**
	 * Sets the current column width to the preferred column width for the columns past as parameter
	 * @param visibleColumns a List containing the column names
	 * @param progressBar a progress bar to show the process
	 * @return
	 */
	protected int[] setPreferredTableColumnWidths(final List visibleColumns, final JProgressBar progressBar) {
		// Calculate the minimun table width to ensure that values are visible

		Table.logger.trace("Table: setting columns preferred width: Memory used: {} kbytes",
				(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0);
		final long t = System.currentTimeMillis();

		this.setRowNumberColumnVisible(this.visibleRowNumberColumn);
		final int[] widths = new int[this.table.getColumnCount()];
		try {
			final int columnNumber = this.table.getColumnCount();
			if (progressBar != null) {
				progressBar.setMaximum(this.table.getRowCount() * visibleColumns.size());
			}
			int lastPainted = 0;
			int aux = 0;
			final int paintIncrement = Math.max(1, (int) ((this.table.getRowCount() * columnNumber) / 10.0));
			final JTableHeader header = this.table.getTableHeader();
			TableCellRenderer rendererCabecera = null;
			try {
				rendererCabecera = header.getDefaultRenderer();
			} catch (final Exception e) {
				Table.logger.error(null, e);
			}

			for (int i = 0; i < columnNumber; i++) {
				final String sName = this.table.getColumnName(i);
				final TableColumn tableColumn = this.table.getColumn(sName);
				if (sName.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
					widths[i] = tableColumn.getPreferredWidth();
					continue;
				}
				if ((!this.isVisibleColumn(sName)) || ((visibleColumns != null) && !visibleColumns.contains(sName))) {
					tableColumn.setMinWidth(0);
					tableColumn.setMaxWidth(0);
					tableColumn.setWidth(0);
					widths[i] = 0;
					continue;
				} else {
					tableColumn.setMinWidth(10);
					tableColumn.setMaxWidth(10000);
				}
				// Without data use the header to initialize the columns width
				try {
					final Object oHeaderValue = tableColumn.getHeaderValue();
					final Component rendererHeaderComponent = rendererCabecera.getTableCellRendererComponent(this.table,
							oHeaderValue, false, false, 0, 1);
					int headerPreferredWidth = 0;
					if (rendererHeaderComponent instanceof JLabel) {
						final FontMetrics fontMetrics = ((JLabel) rendererHeaderComponent)
								.getFontMetrics(rendererHeaderComponent.getFont());
						if (oHeaderValue != null) {
							headerPreferredWidth = fontMetrics.stringWidth(oHeaderValue.toString()) + 4;
						}
					} else if (rendererHeaderComponent instanceof JTextComponent) {
						final FontMetrics fontMetrics = ((JTextComponent) rendererHeaderComponent)
								.getFontMetrics(rendererHeaderComponent.getFont());
						if (oHeaderValue != null) {
							headerPreferredWidth = fontMetrics.stringWidth(oHeaderValue.toString() + 6);
						}
					} else {
						headerPreferredWidth = rendererHeaderComponent.getPreferredSize().width;
					}
					widths[i] = headerPreferredWidth;
				} catch (final Exception e) {
					Table.logger.error("Exception initiating table column width. JRE 1.3 or above is required", e);
					widths[i] = tableColumn.getPreferredWidth();
				}

				TableCellRenderer renderer = this.table.getDefaultRenderer(this.table.getColumnClass(i));
				TableCellRenderer cellRenderer2 = this.table.getColumnModel().getColumn(i).getCellRenderer();
				if (cellRenderer2 != null) {
					renderer = cellRenderer2;
				}
				cellRenderer2 = null;
				FontMetrics fontMetrics = null;
				int preferredWidth = 0;
				Component componenteRender = null;
				Object oValue = null;
				final long tIniCol = System.currentTimeMillis();
				for (int j = 0; j < this.table.getRowCount(); j++) {
					oValue = this.table.getValueAt(j, i);
					componenteRender = renderer.getTableCellRendererComponent(null, oValue, false, false, 0, 0);
					if (componenteRender instanceof JTextField) {
						if (fontMetrics == null) {
							fontMetrics = ((JTextField) componenteRender)
									.getFontMetrics(componenteRender.getFont());
						}
						preferredWidth = fontMetrics.stringWidth(((JTextField) componenteRender).getText()) + 4;
					} else if (componenteRender instanceof JLabel) {
						if (fontMetrics == null) {
							fontMetrics = ((JLabel) componenteRender)
									.getFontMetrics(componenteRender.getFont());
						}
						try {
							String text = ((JLabel) componenteRender).getText();
							if (text == null) {
								text = "";
							}
							preferredWidth = fontMetrics.stringWidth(text) + 4;
						} catch (final Exception eM) {
							Table.logger.trace(null, eM);
						}
					} else if (componenteRender instanceof JComponent) {
						preferredWidth = componenteRender.getPreferredSize().width;
						preferredWidth = preferredWidth - ((JComponent) componenteRender).getInsets().left
								- ((JComponent) componenteRender).getInsets().right;
					} else {
						preferredWidth = componenteRender.getPreferredSize().width;
					}

					widths[i] = Math.max(preferredWidth + 5, widths[i]);

					if (progressBar != null) {

						aux++;
						if ((aux - lastPainted) > paintIncrement) {
							progressBar.setValue(aux);
							if (SwingUtilities.isEventDispatchThread()) {
								progressBar.paintImmediately(0, 0, progressBar.getWidth(), progressBar.getHeight());
							} else {
								SwingUtilities.invokeLater(new Runnable() {

									@Override
									public void run() {
										progressBar.paintImmediately(0, 0, progressBar.getWidth(),
												progressBar.getHeight());
									}
								});
							}
							lastPainted = aux;

						}
					}

				}

				Table.logger.trace("Table:  columns preferred width set time: {} for {} rows: {}",
						tableColumn.getHeaderValue(), this.table.getRowCount(),
						System.currentTimeMillis() - tIniCol);

				Table.logger.debug("Table: set columns preferred width: {}. Memory use: {} kbytes",
						tableColumn.getHeaderValue(),
						(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0);
				// System.gc();
				tableColumn.setWidth(widths[i]);
				tableColumn.setPreferredWidth(widths[i]);

			}
		} catch (final OutOfMemoryError errorMem) {
			Table.logger.error("Memory error", errorMem);
			throw errorMem;
		}

		Table.logger.trace("Table: columns preferred width set time: {} rows : {}", this.table.getRowCount(),
				System.currentTimeMillis() - t);

		if ((this.table != null) && (this.table.getModel() instanceof TableSorter)) {
			((TableSorter) this.table.getModel()).setPreferredHeadSize();
		}
		return widths;
	}

	/**
	 * Starts the table printing process. No dialogs are shown.
	 * @param title
	 */
	public void printSilent(final String title) {
		this.checkRefreshThread();
		PrintingSetupWindow vPrintConfiguration = null;
		try {
			final Window w = SwingUtilities.getWindowAncestor(Table.this);
			if (w instanceof Dialog) {
				if (vPrintConfiguration == null) {
					vPrintConfiguration = new PrintingSetupWindow((Dialog) w, Table.this);
				}
				vPrintConfiguration.setResourceBundle(this.resourcesFile);
				vPrintConfiguration.printDefault(title);
			} else {
				if (vPrintConfiguration == null) {
					vPrintConfiguration = new PrintingSetupWindow(Table.this.parentFrame, Table.this);
				}
				vPrintConfiguration.setResourceBundle(this.resourcesFile);
				vPrintConfiguration.printDefault(title);
			}
		} catch (final Exception e) {
			Table.logger.error(null, e);
			this.parentForm.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
		} catch (final OutOfMemoryError error) {
			Table.logger.error("Memory Error", error);
			vPrintConfiguration.setVisible(false);
			vPrintConfiguration.dispose();
			vPrintConfiguration = null;
		}
	}

	/**
	 * Starts the print table dialog.
	 */
	public void print() {
		this.checkRefreshThread();
		PrintingSetupWindow vPrintConfiguration = null;
		try {
			final Window w = SwingUtilities.getWindowAncestor(Table.this);
			if (w instanceof Dialog) {
				if (vPrintConfiguration == null) {
					vPrintConfiguration = new PrintingSetupWindow((Dialog) w, Table.this);
				}
				vPrintConfiguration.setResourceBundle(this.resourcesFile);
				vPrintConfiguration.setVisible(true);
			} else {
				if (vPrintConfiguration == null) {
					vPrintConfiguration = new PrintingSetupWindow(Table.this.parentFrame, Table.this);
				}
				vPrintConfiguration.setResourceBundle(this.resourcesFile);
				vPrintConfiguration.setVisible(true);
			}
		} catch (final Exception e) {
			Table.logger.error(null, e);
			this.parentForm.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
		} catch (final OutOfMemoryError error) {
			Table.logger.error("Memory Error", error);
			vPrintConfiguration.setVisible(false);
			vPrintConfiguration.dispose();
			vPrintConfiguration = null;
		}
	}

	/**
	 * Copies the selected data in the table GUI to the system clipboard.
	 */
	protected void copySelection() {
		try {
			Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			if (this.table.getSelectedRowCount() == 0) {
				return;
			}
			final int[] selectedRows = this.table.getSelectedRows();
			final StringSelection sselection = new StringSelection(this.getExcelString(selectedRows));
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
		} catch (final Exception e) {
			Table.logger.debug("Exception establishing contents to Clipboard.", e);
			this.parentForm.message("table.operation_cannot_be_performed", Form.WARNING_MESSAGE, e);
		} finally {
			Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Returns the grouping for the column over which was pressed the mouse to select the deletion.
	 */
	protected void deleteGroup() {
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			sorter.resetGroup();
			this.enableInsert();
		}
	}

	/**
	 * Groups the selected column by the defined type. Types are defined in the TableSorter class, and
	 * can be at least:
	 * <ul>
	 * <li>YEAR</li>
	 * <li>YEAR_MONTH</li>
	 * <li>YEAR_MONTH_DAY</li>
	 * <li>QUARTER_YEAR</li>
	 * <li>QUARTER</li>
	 * <li>MONTH</li>
	 * </ul>
	 * @param type the grouping type; see {@link TableSorter}
	 */
	protected void insertGroup(final int type) {
		if (this.table.isEditing()) {
			this.table.editingStopped(new ChangeEvent(this.table));
		}
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			final int column = this.table.convertColumnIndexToModel(this.colPress);
			sorter.group(column, type);
			this.disableInsert();
		}
	}

	/**
	 * Groups the selected column in the table by year.
	 */
	protected void insertGroup() {
		if (this.table.isEditing()) {
			this.table.editingStopped(new ChangeEvent(this.table));
		}
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			final int column = this.table.convertColumnIndexToModel(this.colPress);
			sorter.group(column);
			this.disableInsert();
			if (this.buttonDelete != null) {
				this.setTableComponentEnabled(Table.BUTTON_DELETE, false);
			}
		}
	}

	/**
	 * Popup menu method that gets the value in the cell over which the menu was displayed, gets that
	 * value, and filters the table applying that value as filter value. The column that will be
	 * filtered in the corresponding the cell.
	 */
	protected void insertFilterByValue() {
		if ((this.rowPress < 0) || (this.colPress < 0)) {
			return;
		}
		if (this.table.isEditing()) {
			this.table.editingStopped(new ChangeEvent(this.table));
		}

		final int row = this.rowPress;
		final int col = Table.this.table.convertColumnIndexToModel(this.colPress);
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			Object value = sorter.getValueAt(row, col);
			Class columnClass = sorter.getColumnClass(col);

			if (this.getJTable().getCellRenderer(0, this.colPress) instanceof ComboReferenceCellRenderer) {
				value = ((ComboReferenceCellRenderer) this.getJTable().getCellRenderer(0, this.colPress))
						.getCodeDescription(value);
				columnClass = String.class;
			}

			if (value == null) {
				final SimpleFilter simple = new SimpleFilter(value);
				sorter.applyFilter(col, simple);
			} else {
				if (Number.class.isAssignableFrom(columnClass)) {
					final Filter filter = new Filter(Filter.EQUAL, new Object[] { value });
					sorter.applyFilter(col, filter);
				} else if (java.util.Date.class.isAssignableFrom(columnClass)) {
					final Filter filter = new Filter(Filter.EQUAL, new Object[] { value });
					sorter.applyFilter(col, filter);
				} else {
					final SimpleFilter simple = new SimpleFilter(value);
					sorter.applyFilter(col, simple);
				}
			}
		}
	}

	/**
	 * Shows the FilterDialog.
	 *
	 * @see FilterDialog
	 */
	protected void insertFilter() {
		if (this.filterWindow == null) {
			final Window w = SwingUtilities.getWindowAncestor(this.table);
			if (w instanceof Dialog) {
				this.filterWindow = new FilterDialog((Dialog) w, this);
			} else {
				this.filterWindow = new FilterDialog((Frame) w, this);
			}
		}

		this.filterWindow.setResourceBundle(this.resourcesFile);
		this.filterWindow.setComponentLocale(this.locale);

		if (!(this.table.getTableHeader().getDefaultRenderer() instanceof SortTableCellRenderer)) {
			final SortTableCellRenderer rend = new SortTableCellRenderer(this.table);
			// rend.setMaxLinesNumber(SortTableCellRenderer.MAX_VALUE_HEAD_RENDERER_LINES);
			this.table.getTableHeader().setDefaultRenderer(rend);
			Table.logger.info("--");
		} else {
		}
		this.table.getTableHeader().repaint();
		Table.logger.debug("Table: Showing filter window");
		this.filterWindow.show(this.eventPress);
	}

	/**
	 * Deletes all the filters that are being applied to the table.
	 */
	protected void deleteFilter() {
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			// int column = table.convertColumnIndexToModel(this.colPress);
			if (sorter.lastFilterOr()) {
				this.quickFilterText.setText("");
			} else {
				sorter.resetFilter();
			}
		}
	}

	/**
	 * Popup menu method. Deletes all the filters that are being applied to the column over which the
	 * right click menu was invoked.
	 */
	protected void deleteColumnFilter() {
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final TableSorter sorter = (TableSorter) model;
			// TODO the following line can be removed
			final int column = this.table.convertColumnIndexToModel(this.colPress);
			sorter.resetFilter(column);
		}
	}

	/**
	 * Opens the printing dialog to print the information contained in the selected rows.
	 */
	protected void printSelection() {
		try {
			final Map hSelectedData = Table.this.getSelectedRowData();
			if (this.tAux == null) {
				this.tAux = new Table(Table.this.getParameters());
				this.tAux.setParentForm(this.parentForm);
			}
			this.tAux.setResourceBundle(this.getResourceBundle());
			this.tAux.setValue(hSelectedData);
			this.checkRefreshThread();
			PrintingSetupWindow vPrintConfigurationSelection = null;
			try {
				final Window w = SwingUtilities.getWindowAncestor(Table.this);
				if (w instanceof Dialog) {
					if (vPrintConfigurationSelection == null) {
						vPrintConfigurationSelection = new PrintingSetupWindow((Dialog) w, Table.this.tAux);
					}
					vPrintConfigurationSelection.setResourceBundle(this.getResourceBundle());
					vPrintConfigurationSelection.setVisible(true);

				} else if (w instanceof Frame) {
					if (vPrintConfigurationSelection == null) {
						vPrintConfigurationSelection = new PrintingSetupWindow((Frame) w, Table.this.tAux);
					}
					vPrintConfigurationSelection.setResourceBundle(this.getResourceBundle());
					vPrintConfigurationSelection.setVisible(true);

				} else {
					if (vPrintConfigurationSelection == null) {
						vPrintConfigurationSelection = new PrintingSetupWindow(Table.this.parentFrame, Table.this.tAux);
					}
					vPrintConfigurationSelection.setResourceBundle(this.getResourceBundle());
					vPrintConfigurationSelection.setVisible(true);
				}
			} catch (final Exception e) {
				Table.logger.error(null, e);
				this.parentForm.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
			}
		} catch (final Exception e) {
			Table.logger.error(null, e);
			this.parentForm.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
		}

		this.tAux.deleteData();
	}

	/**
	 * Copies to the clipboard the information contained by the cell over which the mouse right click
	 * opened the popup menu.
	 */
	protected void copyCell() {
		final Object datosSel = this.getCellValueAsString(this.rowPress, this.colPress);
		// Object datosSel = this.table.getValueAt(rowPress, colPress);
		try {
			Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final StringSelection sselection = new StringSelection(datosSel.toString());
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sselection, sselection);
		} catch (final Exception e) {
			Table.logger.debug("Exception setting Clipboard content.", e);
			this.parentForm.message("table.operation_cannot_be_performed", Form.WARNING_MESSAGE, e);
		} finally {
			Table.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Sets the height that will have the rendered images into the table.
	 * @param pixels the height in pixels for the image renderers
	 * @see ImageCellRenderer
	 */
	public void setImageRendererHeight(final int pixels) {
		final Map pImage = new Hashtable();
		pImage.put("height", Integer.toString(pixels));
		this.table.setDefaultRenderer(BytesBlock.class, new ImageCellRenderer(pImage));
		this.table.repaint();
	}

	/**
	 * Configures the pageable table.
	 * @param pageSize
	 */
	protected void configurePageable(final int pageSize) {
		this.pageFetcher = new PageFetcher(this, pageSize);
	}

	/**
	 * Returns the total amount of records shown in the table. In case that the table is filtered or
	 * grouped, returns the total amount of records after performing those operations.
	 * <p>
	 * In any case the sum column will not be considered.
	 * @return the total number of records shown in the table
	 */
	public int getCurrentRowCount() {
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).getCurrentRowCount();
	}

	/**
	 * Returns the total amount of records in the table. Sum row is not included. In case that the table
	 * is filtered or grouped, the result will not be modified.
	 * @return the total amount of records in the table
	 */
	public int getRealRecordsNumber() {
		final TableModel model = this.table.getModel();
		return ((TableSorter) model).getRealRecordNumber();
	}

	/**
	 * Returns a {@link #Hashtable} in which the keys are the field names configured as table parent
	 * keys (using the names in the table entity if they are different that the form fields), and the
	 * values are the values those fields have in the table's parent form.
	 * @return all the parent keys values
	 * @see #getParentKeyValues(boolean)
	 */
	@Override
	public Map getParentKeyValues() {
		return this.getParentKeyValues(true);
	}

	@Override
	public List getParentKeyList() {
		if (this.parentkeys != null) {
			final List temp = new Vector();
			temp.addAll(this.parentkeys);
			return temp;
		}
		return null;
	}

	@Override
	public boolean hasParentKeys() {
		if ((this.parentkeys == null) || this.parentkeys.isEmpty()) {
			return false;
		}
		return true;

	}

	/**
	 * Returns a {@link #Hashtable} in which the keys are the field names configured as table parent
	 * keys, and the values are the values those fields have in the table's parent form.
	 * @param applyEquivalences If this parameter is true then the names used as keys in the result are
	 *        the names of the parent keys in the table entity. If false, then the names are the names
	 *        of the fields in the parent form
	 * @return
	 */
	public Map getParentKeyValues(final boolean applyEquivalences) {

		final Map kv = new Hashtable();
		if (this.parentkeys != null) {
			for (int i = 0; i < this.parentkeys.size(); i++) {
				final Object v = this.parentForm.getDataFieldValue(this.parentkeys.get(i).toString());
				if (v != null) {
					Object pkName = this.parentkeys.get(i);
					if (applyEquivalences) {
						pkName = this.getParentkeyEquivalentValue(pkName);
					}
					kv.put(pkName, v);
				} else {
					Table.logger.debug(
							"Table: Parentkey {} is null. It won't be included in the query.Check the xml file in which the table is defined to ensure that the field has a value",
							this.parentkeys.get(i));
					if (Table.logger.isTraceEnabled()) {
						MessageDialog.showErrorMessage(this.parentFrame, "DEBUG: Table: Parentkey "
								+ this.parentkeys.get(i) + " is null. It won't be included in the query. "
								+ "Check the xml file in which the table is defined to ensure that the field has a value");
					}
				}
			}
		}
		return kv;
	}

	/**
	 * Sets whether the control buttons pannel will be enabled or not.
	 * @param enabled true if the table header should enable
	 */
	protected void setControlButtonsEnabled(final boolean enabled) {
		if (this.showControls) {
			this.setTableComponentEnabled(Table.BUTTON_COPY, enabled);
			this.setTableComponentEnabled(Table.BUTTON_EXCEL_EXPORT, enabled);
			this.setTableComponentEnabled(Table.BUTTON_HTML_EXPORT, enabled);
			this.setTableComponentEnabled(Table.BUTTON_PRINTING, enabled);
			this.setTableComponentEnabled(Table.BUTTON_PIVOTTABLE, enabled);
			this.setTableComponentEnabled(Table.BUTTON_CHART, enabled);

			this.setTableComponentEnabled(Table.BUTTON_REPORT, enabled);

			this.setTableComponentEnabled(Table.BUTTON_DEFAULT_CHART, enabled && this.defaultChartsEnabled);
			if (!this.defaultChartsEnabled) {
				this.setTableComponentVisible(Table.BUTTON_DEFAULT_CHART, false);
			}

			if (this.dynamicTable) {
				this.setTableComponentEnabled(Table.BUTTON_VISIBLE_COLS_SETUP, enabled);
			}

			final boolean enableOperationButtons = this.getOperationColumns().size() > 0;
			this.setTableComponentEnabled(Table.BUTTON_CALCULATED_COL, enableOperationButtons && enabled);
			this.setTableComponentEnabled(Table.BUTTON_SUM_ROW_SETUP, enableOperationButtons && enabled);
		}
	}

	/**
	 * Configures the buttons tip.
	 */
	protected void setButtonTips() {
		if (this.buttonVisibleColsSetup != null) {
			this.buttonVisibleColsSetup.setToolTipText(Table.TIP_VISIBLES_COLS_SETUP);
		}

		if (this.buttonSumRowSetup != null) {
			this.buttonSumRowSetup.setToolTipText(Table.TIP_SUMROW_SETUP);
		}

		if (this.buttonSaveFilterOrderSetup != null) {
			this.buttonSaveFilterOrderSetup.setToolTipText(Table.TIP_FILTER_ORDEN_CONF);
		}

		if (this.buttonPrint != null) {
			this.buttonPrint.setToolTipText(Table.TIP_PRINTING);
			if (this.buttonPrint instanceof TableButton) {
				((TableButton) this.buttonPrint).setDefaultToolTipText(Table.TIP_PRINTING_es_ES);
			}
		}

		if (this.buttonDefaultChart != null) {
			this.buttonDefaultChart.setToolTipText(Table.TIP_CHART_MENU);
			this.buttonDefaultChart.setDefaultToolTipText(Table.TIP_CHART_MENU_es_ES);
		}

		if (this.buttonCopy != null) {
			this.buttonCopy.setToolTipText(Table.TIP_CLIPBOARD_COPY);
			if (this.buttonCopy instanceof TableButton) {
				((TableButton) this.buttonCopy).setDefaultToolTipText(Table.TIP_CLIPBOARD_COPY_es_ES);
			}
		}

		if (this.buttonExcelExport != null) {
			this.buttonExcelExport.setToolTipText(Table.TIP_EXCEL_EXPORT);
			if (this.buttonExcelExport instanceof TableButton) {
				((TableButton) this.buttonExcelExport).setDefaultToolTipText(Table.TIP_EXCEL_EXPORT_es_ES);
			}
		}

		if (this.buttonHTMLExport != null) {
			this.buttonHTMLExport.setToolTipText(Table.TIP_HTML_EXPORT);
			if (this.buttonHTMLExport instanceof TableButton) {
				((TableButton) this.buttonHTMLExport).setDefaultToolTipText(Table.TIP_HTML_EXPORT_es_ES);
			}
		}

		if (this.buttonPlus2 != null) {
			this.buttonPlus2.setToolTipText(Table.TIP_INSERT_BUTTON);
			if (this.buttonPlus2 instanceof TableButton) {
				((TableButton) this.buttonPlus2).setDefaultToolTipText(Table.TIP_INSERT_BUTTON_es_ES);
			}
		}

		if (this.buttonPlus != null) {
			this.buttonPlus.setToolTipText(Table.TIP_INSERT_BUTTON);
			if (this.buttonPlus instanceof TableButton) {
				((TableButton) this.buttonPlus).setDefaultToolTipText(Table.TIP_INSERT_BUTTON_es_ES);
			}
		}

		if (this.buttonReports != null) {
			this.buttonReports.setToolTipText(Table.TIP_REPORT_PRINTING);
			this.buttonReports.setDefaultToolTipText(Table.TIP_REPORT_PRINTING_es_ES);
		}

		if (this.buttonPivotTable != null) {
			this.buttonPivotTable.setToolTipText(Table.TIP_PIVOT_TABLE);
			this.buttonPivotTable.setDefaultToolTipText(Table.TIP_PIVOT_TABLE_es_ES);
		}

		if (this.buttonCalculatedColumns != null) {
			this.buttonCalculatedColumns.setToolTipText(Table.TIP_CALCULATED_COLUMNS);
			if (this.buttonCalculatedColumns instanceof TableButton) {
				((TableButton) this.buttonCalculatedColumns).setDefaultToolTipText(Table.TIP_CALCULATED_COLUMNS);
			}
		}

		if (this.groupTableButton != null) {
			this.groupTableButton.setToolTipText(Table.TIP_GROUP_TABLE_BUTTON);
			this.groupTableButton.setDefaultToolTipText(Table.TIP_GROUP_TABLE_BUTTON);
		}

		// adding the buttons
		for (int i = 0; i < this.addButtons.size(); i++) {
			final Object o = this.addButtons.get(i);
			if (o instanceof Internationalization) {
				((Internationalization) o).setResourceBundle(this.resourcesFile);
			}
		}

		for (int i = 0; i < this.addComponents.size(); i++) {
			final Object o = this.addComponents.get(i);
			if (o instanceof Internationalization) {
				((Internationalization) o).setResourceBundle(this.resourcesFile);
			}
		}

	}

	/**
	 * Returns the sum of all the values for the specified column.
	 * @param col the column to sum
	 * @return the sum of the values of the record for the column
	 */
	public Number getSumColumn(final Object col) {
		this.checkRefreshThread();
		if ((this.table == null) || (this.table.getModel() == null)) {
			return new Double(0.0);
		}
		return ((TableSorter) this.table.getModel()).getColumnSum(col);
	}

	/**
	 * Returns the DetailForm configured for this table. The detail form is the form in which the
	 * records can be shown in detail by double clicking the rows in the grid. It is used as well to
	 * perform insert operations from the tables, by clicking the insert button in the top right corner
	 * of the table.
	 * @return the detail form, or null if no form has been specified
	 */
	public DetailForm getDetailForm() {
		if (this.formName != null) {
			if (this.detailForm == null) {
				this.createDetailForm();
				this.detailForm.setKeys(new Hashtable(0), 0);

				this.detailForm.resetParentkeys(this.parentkeys);
				final Map hOtherKeys = new Hashtable();
				for (int i = 0; i < this.parentkeys.size(); i++) {
					final Object vParentKey = Table.this.parentForm
							.getDataFieldValueFromFormCache(this.parentkeys.get(i).toString());
					if (Table.logger.isDebugEnabled() && (vParentKey == null)) {
						MessageDialog.showErrorMessage(this.parentFrame, "DEBUG: Table: parentkey "
								+ this.parentkeys.get(i)
								+ " is NULL. It won't be included in the query. Check the xml that contains the table configuration and ensure that the parentkey has value there.");
					}
					if (vParentKey != null) {
						hOtherKeys.put(this.parentkeys.get(i), vParentKey);
					}
				}
				this.detailForm.setParentKeyValues(hOtherKeys);
			}
			return this.detailForm;
		} else {
			Table.logger.debug("This table does not have detail form");
			return null;
		}
	}

	/**
	 * Sets the preferred size to the specified column.
	 * @param column the column to set the size
	 */
	public void fitColumnSize(final int column) {
		final String sName = this.table.getColumnName(column);
		TableColumn tableColumn = null;
		if (column <= this.getBlockedColumnIndex()) {
			tableColumn = this.blockedTable.getColumn(sName);
		} else {
			tableColumn = this.table.getColumn(sName);
		}

		this.table.getColumn(sName);
		if (tableColumn == null) {
			return;
		}

		final int iPreferredWidth = this.getPreferredColumnWidth(column, this.table.getRowCount());

		if (sName.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
			tableColumn.setWidth(iPreferredWidth);
			tableColumn.setPreferredWidth(iPreferredWidth);
		} else {
			tableColumn.setMaxWidth(this.table.getWidth());
			tableColumn.setMinWidth(0);
			tableColumn.setWidth(iPreferredWidth);
			tableColumn.setPreferredWidth(iPreferredWidth + 5);
		}
	}

	/**
	 * Sets all the table texts in the table popup menu.
	 */
	protected void setTextsMenu() {
		// TODO this code must be extracted to reduce the number of lines used
		// because it is a repetion of the same code
		String sText = ApplicationManager.getTranslation(Table.detailKey, this.resourcesFile);
		this.menuDetail.setText(sText);

		sText = ApplicationManager.getTranslation(Table.insertKey, this.resourcesFile);
		this.menuInsert.setText(sText);

		sText = ApplicationManager.getTranslation(Table.resetOrderKey, this.resourcesFile);
		this.menuResetOrder.setText(sText);

		sText = Table.PRINTING_SELECTION_es_ES;
		try {
			if (this.resourcesFile != null) {
				sText = this.resourcesFile.getString(Table.PRINTING_SELECTION);
			}
		} catch (final Exception e) {
			Table.logger.error("PRINTING_SELECTION Error", e);
		}
		this.menuPrintSelection.setText(sText);

		sText = Table.COPY_CELL_es_ES;
		try {
			if (this.resourcesFile != null) {
				sText = this.resourcesFile.getString(Table.COPY_CELL);
			}
		} catch (final Exception e) {
			Table.logger.error("COPY_CELL Error", e);
		}

		this.menuCopyCell.setText(sText);

		if (this.menuOpenInNewWindow != null) {
			this.menuOpenInNewWindow
			.setText(ApplicationManager.getTranslation(Table.openInNewWindowKey, this.resourcesFile));
		}

		sText = Table.COPY_SELECTION_es_ES;
		try {
			if (this.resourcesFile != null) {
				sText = this.resourcesFile.getString(Table.COPY_SELECTION);
			}
		} catch (final Exception e) {
			Table.logger.error("COPY_SELECTION", e);
		}

		this.menuCopySelection.setText(sText);

		if (this.menuRefresh != null) {
			this.menuRefresh.setText(ApplicationManager.getTranslation(Table.REFRESH, this.resourcesFile));
		}

		if (this.menuPageableEnabled != null) {
			this.menuPageableEnabled.setText(ApplicationManager.getTranslation(Table.PAGEABLE, this.resourcesFile));
		}

		if (this.menuOpenInNewWindow != null) {
			this.menuOpenInNewWindow
			.setText(ApplicationManager.getTranslation(Table.openInNewWindowKey, this.resourcesFile));
		}

		if (this.menuShowHideControls != null) {
			this.menuShowHideControls
			.setText(ApplicationManager.getTranslation(Table.SHOW_HIDE_CONTROLS, this.resourcesFile));
		}

	}

	/**
	 * Returns the information contained in the selected rows in a {@link #Hashtable}. The
	 * {@link #Hashtable} keys are the table attributes, and the values are {@link #Vector} with the row
	 * values.
	 * @return the information contained by the selected rows, and null when there is no selection
	 */
	public Map getSelectedRowData() {
		if (this.table.getSelectedRowCount() == 0) {
			return null;
		}
		// Return a new hastable with the data
		final int[] selectedRows = this.table.getSelectedRows();
		final List attributes = this.getAttributeList();
		final Map hData = new Hashtable();
		for (int i = 0; i < selectedRows.length; i++) {
			final int row = selectedRows[i];
			final Map hRowData = this.getRowData(row);
			if (hRowData == null) {
				continue;
			}
			for (int j = 0; j < attributes.size(); j++) {
				final Object oKey = attributes.get(j);
				final Object oValue = hRowData.get(oKey);
				final List v = (List) hData.get(oKey);
				if (v == null) {
					final List vAux = new Vector();
					vAux.add(0, oValue);
					hData.put(oKey, vAux);
				} else {
					v.add(i, oValue);
				}
			}
		}
		return hData;
	}

	/**
	 * Checks the component visibility. Depending on the client permissions, the table can be shown or
	 * not. This method checks that permission to hide the table when necessary.
	 * @return true in case the table can not be visible, false if it can
	 */
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
				// Check to show
				if (this.visiblePermission != null) {
					manager.checkPermission(this.visiblePermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("Visible permission:", e);
				} else {
					Table.logger.trace("Visible permission:", e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks if the table must be disabled. Depending on the client permissions, the table can be
	 * enabled or not. This method checks that permission to disable or enable the table when necessary.
	 * @return true in case the table can not be enabled, false if it can
	 */
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
				if (this.enabledPermission != null) {
					manager.checkPermission(this.enabledPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("Enabled Permission:", e);
				} else {
					Table.logger.trace("Enabled Permission:", e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks if the table can perform insertions. Depending on the client permissions, the table can
	 * have the insertions restricted. This method checks that permission to disable or enable the
	 * inserting from the table feature.
	 * @return true in case the table can insert, false if it cannot
	 */
	protected boolean checkInsertPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.insertPermission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					this.insertPermission = new FormPermission(this.parentForm.getArchiveName(), "insert", this.entity,
							true);
				}
			}
			try {
				if (this.insertPermission != null) {
					manager.checkPermission(this.insertPermission);
				}

				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("Insert permission:", e);
				} else {
					Table.logger.trace("Insert permission:", e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks if the table can perform queries. Depending on the client permissions, the table can have
	 * the queries restricted. This method checks that permission to disable or enable querying.
	 * @return true in case the table can query, false if it cannot
	 */
	protected boolean checkQueryPermission() {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			if (this.queryPermission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					this.queryPermission = new FormPermission(this.parentForm.getArchiveName(), "query", this.entity,
							true);
				}
			}
			try {
				if (this.queryPermission != null) {
					manager.checkPermission(this.queryPermission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("Query permission:", e);
				} else {
					Table.logger.trace("Query permission:", e);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	/**
	 * Checks whether the component with this <code>key</code> has been restricted by the permission
	 * <code>type</code> . If this method returns true the table component will not be restricted by
	 * this permission <code>type</code>. <br>
	 * For example, if the a call to this method with the type "visible" returns true, then the
	 * component is visible.
	 * @param key the key of the table component to be restricted
	 * @param type the type of permission to be checked
	 * @return false the component are restricted
	 */
	protected boolean checkComponentTablePermission(final Object key, final String type) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			TableFormPermission permission = this.tableComponentPermission.get((String) key, type);
			if (permission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					permission = new TableFormPermission(this.parentForm.getArchiveName(), (String) key, this.entity,
							true, null, type);
					this.tableComponentPermission.addTableFormPermission((String) key, type, permission);
				}
			}
			try {
				if (permission != null) {
					manager.checkPermission(permission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("TableComponentPermission:", e);
				} else {
					Table.logger.trace("TableComponentPermission:", e);
				}
				return false;
			}

		} else {
			return true;
		}
	}

	/**
	 * Checks whether the column with this <code>columnName</code> has been restricted by the permission
	 * <code>type</code> . If this method returns true the table column will not be restricted by this
	 * permission <code>type</code>. <br>
	 * For example, if the a call to this method with the type "visible" returns true, then column is
	 * able to be visible according to permission (another program condition could be hide it).
	 * @param key the key of the table component to be restricted
	 * @param type the type of permission to be checked
	 * @return false the component are restricted
	 *
	 *
	 * @since 5.2077EN-0.2
	 */
	protected boolean checkColumnTablePermission(final Object key, final String type) {
		final ClientSecurityManager manager = ApplicationManager.getClientSecurityManager();
		if (manager != null) {
			TableFormPermission permission = this.tableColumnPermission.get((String) key, type);
			if (permission == null) {
				if ((this.entity != null) && (this.parentForm != null)) {
					permission = new TableFormPermission(this.parentForm.getArchiveName(), "column", this.entity, true,
							null, type, key.toString());
					this.tableColumnPermission.addTableFormPermission((String) key, type, permission);
				}
			}
			try {
				if (permission != null) {
					manager.checkPermission(permission);
				}
				this.restricted = false;
				return true;
			} catch (final Exception e) {
				this.restricted = true;
				if (e instanceof NullPointerException) {
					Table.logger.error("ColumnTablePermission:", e);
				} else {
					Table.logger.trace("ColumnTablePermission:", e);
				}
				return false;
			}

		} else {
			return true;
		}
	}

	/**
	 * Sets the table visible. Client permissions are checked.
	 *
	 * @see checkVisiblePermission
	 */
	@Override
	public void setVisible(final boolean vis) {
		if (vis) {
			final boolean permission = this.checkVisiblePermission();
			if (!permission) {
				return;
			}
		}
		super.setVisible(vis);
		this.controlsPanel.setVisible(this.controlsVisible);
	}

	/**
	 * Some table functionalities and behaviours can be controled using client security, so the same
	 * table can be different depending on the user profile.
	 * <p>
	 * This method sets the client permission for the table, according to the client permissions XML
	 * profile. This is, checks whether the table can be visible or not, as well as whether the table is
	 * enabled or not.
	 * <p>
	 * The method checks some other permissions such as the query permission and the insert permission,
	 * which can allow insertions from the table.
	 *
	 * @see #initTableComponentPermissions
	 * @see #initTableColumnPermissions()
	 */
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

		this.checkQueryPermission();

		this.initTableComponentPermissions();
		this.initTableColumnPermissions();

		final boolean pInsert = this.checkInsertPermission();
		if (!pInsert) {
			this.setTableComponentEnabled(Table.BUTTON_PLUS, false);
			this.setTableComponentVisible(Table.BUTTON_PLUS, false);
			if (this.buttonPlus != null) {
				this.buttonPlus.setEnabled(false);
				this.buttonPlus.setVisible(false);
			}
		}
	}

	/**
	 * Applies client permissions to the components in the controlsPanel.
	 * <p>
	 * The table functionalities, accessible through the controlsPanel buttons, can be controlled using
	 * client permission. This implies that those buttons can be disabled or hidden using the client
	 * permission XML.
	 *
	 * @see #checkComponentTablePermission
	 */
	public void initTableComponentPermissions() {
		if (this.controlsPanel != null) {
			for (int i = 0; i < this.controlsPanel.getComponentCount(); i++) {
				final Component c = this.controlsPanel.getComponent(i);
				if (c instanceof TableComponent) {
					final TableComponent cT = (TableComponent) c;
					final Object o = cT.getKey();
					if (o != null) {
						final boolean v = this.checkComponentTablePermission(o, "visible");
						if (!v) {
							c.setVisible(false);
						} else {
							c.setVisible(true);
						}

						final boolean e = this.checkComponentTablePermission(o, "enabled");
						if (!e) {
							c.setEnabled(false);
						}
					}
				}
			}
		}
	}

	/**
	 * Applies client permissions to the table columns.
	 * <p>
	 * The table columns can be controlled using client permission. This implies those columns can be
	 * hidden using the client permission XML. e.g. for hiding a column named BALANCE: <br>
	 * <b> <code> &lt;column attr="BALANCE" restricted="yes" type="visible"/&gt</code> </b>
	 *
	 * @see #checkComponentTablePermission
	 *
	 * @since 5.2077EN-0.2
	 */
	public void initTableColumnPermissions() {
		if (this.getJTable().getColumnModel() != null) {
			final TableColumnModel columnModel = this.getJTable().getColumnModel();
			for (int i = 0; i < columnModel.getColumnCount(); i++) {
				final Object o = columnModel.getColumn(i).getIdentifier();
				if (o != null) {
					boolean v = this.checkColumnTablePermission(o, "visible");
					if (!v) {
						columnModel.getColumn(i).setWidth(0);
						columnModel.getColumn(i).setMaxWidth(0);
						columnModel.getColumn(i).setMinWidth(0);
					}

					v = this.checkColumnTablePermission(o, "enabled");
					if (!v) {
						this.setEditableColumn((String) o, false, false);
					}
				}
			}
		}
	}

	/**
	 * Sets whether a tip informing about the record number when pressing the table scroll must be shown
	 * or not.
	 * @param enable true if the tip should be enabled, false otherwise
	 */
	public void setScrollTipEnabled(final boolean enable) {
		this.tipScrollEnabled = enable;
	}

	/**
	 * Toggles the control panel visibility.
	 */
	public void toggleControls() {
		if (this.showControls) {
			this.controlsVisible = !this.controlsPanel.isVisible();
			this.controlsPanel.setVisible(this.controlsVisible);
			if (this.quickFilterText != null) {
				this.quickFilterText.setVisible(this.controlsVisible || this.quickFilterVisible);
			}
			if (this.menuShowHideControls != null) {
				this.menuShowHideControls.setSelected(this.controlsVisible);
			}
			this.saveVisibleControlsConfiguration();
		}
	}

	/**
	 * Sets whether or not the table controls are visible.
	 * @param visible true if the table controls should be visible, false otherwise
	 */
	public void setControlsVisible(final boolean visible) {
		this.setControlsVisible(visible, false);
	}

	/**
	 * Sets whether or not the table controls are visible.
	 * @param vis true if the table controls should be visible, false otherwise
	 * @param savePreferences if true, the visibility set will be stored as application preference
	 */
	public void setControlsVisible(final boolean vis, final boolean savePreferences) {
		if (this.showControls) {
			this.controlsVisible = vis;
			this.controlsPanel.setVisible(vis);
			if (this.quickFilterText != null) {
				this.quickFilterText.setVisible(this.controlsVisible || this.quickFilterVisible);
			}
			if (this.menuShowHideControls != null) {
				this.menuShowHideControls.setSelected(vis);
			}
			if (savePreferences) {
				this.saveVisibleControlsConfiguration();
			}
		} else if (this.menuShowHideControls != null) {
			this.menuShowHideControls.setSelected(false);
		}
	}

	/**
	 * Is call when a preference changes, because the table is registered as a preference change
	 * listener.
	 * <p>
	 * @param prefEvent the event with the preference changes
	 */
	@Override
	public void preferenceChanged(final PreferenceEvent prefEvent) {
		final String pref = prefEvent.getPreference();
		if (pref.equals(BasicApplicationPreferences.SHOW_TABLE_CONTROLS)) {
			final String sValue = prefEvent.getValue();
			if (sValue != null) {
				final boolean controlesVisibles = ApplicationManager.parseStringValue(sValue);
				this.setControlsVisible(controlesVisibles);
			}
		} else if (pref.equals(BasicApplicationPreferences.TABLE_EVEN_ROWS_COLOR)) {
			final String sValue = prefEvent.getValue();
			if (sValue != null) {
				try {
					final Color c = ColorConstants.parseColor(sValue);
					if (CellRenderer.getEvenRowBackgroundColor() != c) {
						CellRenderer.setEvenRowBackgroundColor(c);
					}
					this.repaint();
				} catch (final Exception ex) {
					Table.logger.error(null, ex);
				}
			}
		} else if (pref.equals(BasicApplicationPreferences.SHOW_TABLE_NUM_ROW)) {
			final String sValue = prefEvent.getValue();
			if (sValue != null) {
				final boolean rowNumber = ApplicationManager.parseStringValue(sValue);
				this.setRowNumberColumnVisible(rowNumber);
			}
		}
	}

	protected String userPrefs = null;

	/**
	 * Returns the user name.
	 * @return the user name
	 */
	protected String getUser() {
		if (this.locator instanceof ClientReferenceLocator) {
			return ((ClientReferenceLocator) this.locator).getUser();
		} else {
			return this.userPrefs;
		}
	}

	protected void initCalculedColPreferences(final ApplicationPreferences aPrefs, final String user) {
		final String calcColsPref = aPrefs.getPreference(user, this.getCalculatedColumnsConfPreferenceKey());
		if (calcColsPref != null) {
			// configure the current calculated columns

			// Parse the calculated columns stored in preferences
			final List calculatedColsUserConf = ApplicationManager.getTokensAt(calcColsPref, ";");
			final List calcColNames = new Vector(calculatedColsUserConf.size());
			final List expressions = new Vector(calculatedColsUserConf.size());
			final List renderKey = new Vector(calculatedColsUserConf.size());
			for (int i = 0; i < calculatedColsUserConf.size(); i++) {
				final List tokensAt = ApplicationManager.getTokensAt((String) calculatedColsUserConf.get(i), ":");
				if (tokensAt.size() >= 2) {
					calcColNames.add(tokensAt.get(0));
					expressions.add(tokensAt.get(1));
				}

				if (tokensAt.size() == 3) {
					renderKey.add(tokensAt.get(2));
				} else {
					renderKey.add(Table.DEFAULT_CELL_RENDERER);
				}
			}

			this.configureCalculatedCols(calcColNames, expressions, renderKey, false);
		}
	}

	protected void initVisibleColumnsPreferences(final ApplicationPreferences aPrefs, final String user) {
		final String tvc = aPrefs.getPreference(user, this.getVisibleColumnsPreferenceKey());
		if (tvc != null) {
			final List cols = ApplicationManager.getTokensAt(tvc, ";");
			this.setVisibleColumns(cols);
		} else if ((this.defaultVisibleColumns != null) && (this.defaultVisibleColumns.size() >= 0)) {
			this.setVisibleColumns(this.defaultVisibleColumns);
		}
	}

	protected void initPanelControlPreferences(final ApplicationPreferences aPrefs, final String user) {
		final String tpc = aPrefs.getPreference(user, this.getControlPanelPreferenceKey());
		if (tpc != null) {
			this.controlsPanel.setButtonPosition(tpc);
		} else {
			this.controlsPanel.setButtonPosition(this.controlButtonLayout);
		}

	}

	/**
	 * Sets the table preferences.
	 * <p>
	 * Tables can remember their state between application execution. For instance things like visible
	 * columns, column size and order, and, in general, all the parameters that can be modified and
	 * changed but the user can be reset by calling this method.
	 */
	@Override
	public void initPreferences(final ApplicationPreferences aPrefs, final String user) {
		boolean paginable = false;
		try {
			if ((this.getPageFetcher() != null) && this.getPageFetcher().isPageableEnabled()) {
				paginable = true;
				this.getPageFetcher().setPageableEnabled(false);
			}
			if (aPrefs == null) {
				return;
			}
			this.userPrefs = user;
			final String sc = aPrefs.getPreference(user, BasicApplicationPreferences.SHOW_TABLE_CONTROLS);
			if (sc != null) {
				final boolean prefControlsVisible = ApplicationManager.parseStringValue(sc);
				if (this.controlsVisible) {
					this.setControlsVisible(prefControlsVisible);
				}
			}
			final String snr = aPrefs.getPreference(user, BasicApplicationPreferences.SHOW_TABLE_NUM_ROW);
			if (snr != null) {
				final boolean bRowNumberColumnVisible = ApplicationManager.parseStringValue(snr);
				this.setRowNumberColumnVisible(bRowNumberColumnVisible);
				this.initColumnsWidth();
			}

			this.initCalculedColPreferences(aPrefs, user);

			this.initVisibleColumnsPreferences(aPrefs, user);

			this.initPanelControlPreferences(aPrefs, user);

			this.initFilterOrderPreferences(aPrefs, user);

			try {
				// Background
				final String pref = aPrefs.getPreference(user, BasicApplicationPreferences.TABLE_EVEN_ROWS_COLOR);
				if (pref != null) {
					final Color c = ColorConstants.parseColor(pref);
					if (CellRenderer.getEvenRowBackgroundColor() != c) {
						CellRenderer.setEvenRowBackgroundColor(c);
						this.repaint();
					}
				}
			} catch (final Exception ex) {
				Table.logger.error("Table event rows color:", ex);
			}

			try {
				final String pref = aPrefs.getPreference(user, this.getVisibleControlsPreferenceKey());
				if (pref != null) {
					final boolean vis = ApplicationManager.parseStringValue(pref);
					this.setControlsVisible(vis);
				}
			} catch (final Exception ex) {
				Table.logger.error("VisibleControlsPreference:", ex);
			}
			this.applyOperations();
		} finally {
			if (this.getPageFetcher() != null) {
				this.getPageFetcher().setPageableEnabled(paginable);
			}
		}
	}

	protected void initFilterOrderPreferences(final ApplicationPreferences aPrefs, final String user) {
		// Apply filter and order configuration
		final String sf = aPrefs.getPreference(user, this.getFilterOrderConfPreferenceKey(null));
		if (sf != null) {
			// Disable the filter
			final List cols = ApplicationManager.getTokensAt(sf, ";");
			if (cols.size() >= 2) {
				final String col = (String) cols.get(0);
				final String asc = (String) cols.get(1);
				try {
					if (!"null".equals(col)) {
						if (col.indexOf(":") >= 0) {
							final List otherCols = ApplicationManager.getTokensAt(col, ":");
							final List ascends = ApplicationManager.getTokensAt(asc, ":");
							if (otherCols.size() != ascends.size()) {
								Table.logger.info("Error in preference: {} -> {} has a different size from {}", sf,
										otherCols, ascends);
							} else {
								this.resetOrder();
								for (int i = 0; i < otherCols.size(); i++) {
									final String c = (String) otherCols.get(i);
									final String a = (String) ascends.get(i);
									final boolean ascb = ApplicationManager.parseStringValue(a, false);
									this.sortByWithoutReset(c, ascb);
								}
							}
						} else {
							final boolean ascb = ApplicationManager.parseStringValue(asc, false);
							this.sortBy(col, ascb);
						}
					} else {
						this.resetOrder();
					}
					if (cols.size() >= 3) {

						String f = (String) cols.get(2);
						int index = 3;

						if (!f.startsWith("BASE64")) {
							// Load position and column width
							if (!"null".equals(f)) {
								this.applyColumnPositonAndPreferences(f);
								this.prefWidthAndPosApply = true;
							}
							f = (String) cols.get(3);
							index = 4;
						}
						if (cols.size() > index) {
							for (int i = index; i < cols.size(); i++) {
								f = f + ";" + cols.get(i);
							}
						}
						byte[] bytes = null;
						if (f.startsWith("BASE64")) {
							f = f.substring("BASE64".length());
						}

						bytes = com.ontimize.jee.common.util.Base64Utils.decode(f.toCharArray());
						final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
						final ObjectInputStream in = new ObjectInputStream(bIn);
						final Object o = in.readObject();
						if (o instanceof Map) {
							// To avoid serialization problems
							final Map hNews = new Hashtable();
							final Map g = (Map) o;
							final Enumeration enumKeys = Collections.enumeration(g.keySet());
							while (enumKeys.hasMoreElements()) {
								final Object oKey = enumKeys.nextElement();
								final Object oValue = g.get(oKey);
								if (oValue instanceof TableSorter.Filter) {
									final TableSorter.Filter v = (TableSorter.Filter) oValue;
									final Object[] oValues = v.values;
									for (int i = 0; i < oValues.length; i++) {
										if (oValues[i] instanceof FilterDate) {
											oValues[i] = new java.util.Date(((FilterDate) oValues[i]).longValue());
										}
									}
								}
								hNews.put(oKey, oValue);
							}
							this.defaultFilter = hNews;
							this.applyFilter(hNews);
						}
					}
				} catch (final Exception e) {
					Table.logger.error("Error reading preference " + this.getFilterOrderConfPreferenceKey(null), e);
					aPrefs.setPreference(user, this.getFilterOrderConfPreferenceKey(null), null);
				}
			}
		}
	}

	protected void configureCalculatedCols(final List calcColNames, final List expressions, final List rendersKey,
			final boolean savePreferences) {
		// First of all check the existing ones to update the expression or
		// delete the column
		final List vCurrentCalcCols = this.getCalculatedColumns();
		final List vOriginalCaclCols = this.getOriginalCalculatedColumns();
		for (int i = 0; i < vCurrentCalcCols.size(); i++) {
			final String currentColName = (String) vCurrentCalcCols.get(i);
			final int index = calcColNames.indexOf(currentColName);
			if (index >= 0) {
				if (this.modifiableCalculatedColumns || (vOriginalCaclCols == null)
						|| !vOriginalCaclCols.contains(currentColName)) {
					final String col = (String) calcColNames.get(index);
					final String exp = (String) expressions.get(index);
					final String renderKey = (String) rendersKey.get(index);
					this.getTableSorter().setCalculatedColumnExpression(col, exp);
					final Map allRender = this.getAllColumnRenderer();
					final Map allEditor = this.getAllColumnEditors();
					if (!Table.DEFAULT_CELL_RENDERER.equalsIgnoreCase(renderKey)) {
						allRender.put(col, Table.getRendererMap().get(renderKey));
					} else {
						allRender.remove(col);
						this.getJTable().getColumn(col).setCellRenderer(null);
					}
					final StringBuilder builder = new StringBuilder();
					builder.append(exp);
					builder.append(":");
					builder.append(renderKey);
					this.calculedColumns.put(col, builder.toString());
					this.configureRenderEditor(allRender, allEditor);
				}
				calcColNames.remove(index);
				expressions.remove(index);
				rendersKey.remove(index);
			} else {
				if (this.modifiableCalculatedColumns || (vOriginalCaclCols == null)
						|| !vOriginalCaclCols.contains(currentColName)) {
					this.deleteCalculatedColumn((String) vCurrentCalcCols.get(i));
				}
			}
		}

		// Now check the new calculated columns
		for (int i = 0; i < calcColNames.size(); i++) {
			this.addCalculatedColumn((String) calcColNames.get(i), (String) expressions.get(i),
					(String) rendersKey.get(i));
		}

		if (savePreferences) {
			// Save the preferences
			final Application ap = this.parentForm.getFormManager().getApplication();
			if (ap.getPreferences() != null) {
				ap.getPreferences()
				.setPreference(this.getUser(), this.getCalculatedColumnsConfPreferenceKey(),
						this.getCalculatedColsPreferenceStringValue());
				ap.getPreferences().savePreferences();
			}
		}
		this.repaint();
	}

	protected String getCalculatedColsPreferenceStringValue() {
		if (this.calculedColumns != null) {
			final Enumeration calcCols = Collections.enumeration(this.calculedColumns.keySet());
			final List value = new Vector(this.calculedColumns.size());
			while (calcCols.hasMoreElements()) {
				final String col = (String) calcCols.nextElement();
				final String expression = (String) this.calculedColumns.get(col);
				value.add(col + ":" + expression);
			}
			return ApplicationManager.vectorToStringSeparateBy(value, ";");
		}
		return null;
	}

	/**
	 * Establishes as visible columns the columns passed as parameter and shows the columns.
	 * @param visibleColumns a List containing the column names to show.
	 */
	public void setVisibleColumns(final List visibleColumns) {
		this.setVisibleColumns(visibleColumns, true);
	}

	/**
	 * Establishes as visible columns the columns passed as parameter and shows the columns.
	 * @param visibleColumns a List containing the column names to show.
	 * @param autoSizeColumns if true, the column size will be adjusted to its new contents
	 */
	public void setVisibleColumns(final List visibleColumns, final boolean autoSizeColumns) {
		if (visibleColumns == null) {
			throw new IllegalArgumentException("visiblecols can not be NULL");
		}
		this.visibleColumns = visibleColumns;

		boolean bHideColumns = false;
		for (int i = 0; i < this.originalVisibleColumns.size(); i++) {
			if (!this.visibleColumns.contains(this.originalVisibleColumns.get(i))) {
				bHideColumns = true;
				break;
			}
		}
		if (bHideColumns && (this.buttonVisibleColsSetup != null)) {
			this.buttonVisibleColsSetup.setIcon(this.getButtonVisibleColsSetupIcon(true));
		} else if (this.buttonVisibleColsSetup != null) {
			this.buttonVisibleColsSetup.setIcon(this.getButtonVisibleColsSetupIcon(false));
		}
		this.setVisibleColumns();
		if (autoSizeColumns) {
			this.initColumnsWidth();
		}
	}

	protected Icon getButtonVisibleColsSetupIcon(final boolean renderer) {
		if ((this.buttonIcons != null)
				&& this.buttonIcons.containsKey(((TableComponent) this.buttonVisibleColsSetup).getKey())) {
			return (Icon) this.buttonIcons.get(((TableComponent) this.buttonVisibleColsSetup).getKey());
		} else {
			if (renderer) {
				return ImageManager.getIcon(ImageManager.TABLE_CONF_VISIBLE_COLS_RED);
			} else {
				return ImageManager.getIcon(ImageManager.TABLE_CONF_VISIBLE_COLS);
			}
		}
	}

	/**
	 * Returns the client preferences key to store the visible columns.
	 * @return the client preferences key to store the visible columns
	 */
	protected String getVisibleColumnsPreferenceKey() {
		final Form f = this.parentForm;
		return f != null ? BasicApplicationPreferences.TABLE_VISIBLE_COLS + "_" + f.getArchiveName() + "_" + this.entity
				: BasicApplicationPreferences.TABLE_VISIBLE_COLS + "_" + this.entity;
	}

	/**
	 * Returns the client preferences key to store the button position.
	 * @return the client preferences key to store the button position.
	 */
	protected String getControlPanelPreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.TABLE_CONTROL_PANEL + "_" + f.getArchiveName() + "_" + this.entity
						: BasicApplicationPreferences.TABLE_VISIBLE_COLS + "_" + this.entity;
	}

	/**
	 * Returns the client preferences key to store the visibility of the controls.
	 * @return the client preferences key to store the visibility of the controls
	 */
	protected String getVisibleControlsPreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.SHOW_TABLE_CONTROLS + "_" + f.getArchiveName() + "_" + this.entity
						: BasicApplicationPreferences.SHOW_TABLE_CONTROLS + "_" + this.entity;
	}

	/**
	 * Returns the client preferences key to store the filters for a determined column.
	 * @param columnName the column name
	 * @return the client preferences key to store the filters for a determined column.
	 */
	protected String getFilterOrderConfPreferenceKey(final String columnName) {
		if (!this.dynamicPivotable) {
			if ((columnName == null) || (columnName.length() == 0)) {
				final Form f = this.parentForm;
				return f != null
						? BasicApplicationPreferences.TABLE_CONF_SORT_FILTER + "_" + f.getArchiveName() + "_"
						+ this.entity
						: BasicApplicationPreferences.TABLE_CONF_SORT_FILTER + "_" + this.entity;
			} else {
				final Form f = this.parentForm;
				return f != null
						? BasicApplicationPreferences.TABLE_CONF_SORT_FILTER + "_" + f.getArchiveName() + "_"
						+ this.entity + "_" + columnName
						: BasicApplicationPreferences.TABLE_CONF_SORT_FILTER + "_" + this.entity + "_" + columnName;
			}
		} else {
			if ((columnName == null) || (columnName.length() == 0)) {
				return BasicApplicationPreferences.TABLE_CONF_SORT_FILTER_DYNAMIC_PIVOT_TABLE + "_"
						+ this.dynamicPivotTableForm + "_" + this.dynamicPivotTableEntity;
			} else {
				return BasicApplicationPreferences.TABLE_CONF_SORT_FILTER_DYNAMIC_PIVOT_TABLE + "_"
						+ this.dynamicPivotTableForm + "_" + this.dynamicPivotTableEntity + columnName;
			}
		}
	}

	protected Object getOriginalForm(final Object table) {

		return null;
	}

	/**
	 * Returns the client preferences key to store the configuration of the sorting columns
	 * @return the client preferences key to store the configuration of the sorting columns
	 */
	protected String getFilterOrderConfigurationPreferenceKey() {
		if (!this.dynamicPivotable) {
			final Form f = this.parentForm;
			return f != null
					? BasicApplicationPreferences.TABLE_CONF_SORT_FILTER_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
					+ this.entity
					: BasicApplicationPreferences.TABLE_CONF_SORT_FILTER_CONFIGURATIONS + "_" + this.entity;
		} else {
			return BasicApplicationPreferences.TABLE_CONF_SORT_FILTER_CONFIGURATIONS_DYNAMIC_PIVOT_TABLE + "_"
					+ this.dynamicPivotTableForm + "_" + this.dynamicPivotTableEntity;
		}
	}

	/**
	 * Returns the client preferences key to store the calculated columns configuration.
	 * @return the client preferences key to store the calculated columns configuration
	 */
	protected String getCalculatedColumnsConfPreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.TABLE_CALCULATED_COLUMNS_CONFIGURATION + "_" + f.getArchiveName() + "_"
				+ this.entity
				: BasicApplicationPreferences.TABLE_CALCULATED_COLUMNS_CONFIGURATION + "_" + this.entity;
	}

	/**
	 * Returns the current visible columns.
	 * @return the current visible columns
	 */
	public List getVisibleColumns() {
		return ObjectTools.clone(this.visibleColumns);
	}

	public List<String> getQuickFilterColumns() {
		if (this.quickFilterColumns != null) {
			return this.quickFilterColumns;
		}
		return this.getVisibleColumns();
	}

	/**
	 * Returns the columns used to build reports.
	 * @return the columns used to build reports.
	 */
	public List getReportColumns() {
		return ObjectTools.clone(this.reportCols);
	}

	public void setReportColumns(final List columns) {
		if (this.reportCols == null) {
			this.reportCols = new Vector();
		}
		this.reportCols.clear();
		this.reportCols.addAll(columns);
	}

	/**
	 * Returns the visible columns specified in the XML table definition.
	 * @return a {@link #Vector} with the visible columns specified in the XML
	 */
	public List getOriginallyVisibleColumns() {
		return ObjectTools.clone(this.originalVisibleColumns);
	}

	/**
	 * Returns the sum row columns specified in the XML table definition.
	 * @return a {@link #Vector} with the sum row columns specified in the XML
	 */
	public List getOriginalSumRowCols() {
		if (this.columnsToSum == null) {
			return new Vector();
		}
		return ObjectTools.clone(this.columnsToSum);
	}

	/**
	 * Returns a {@link #Vector} with the column names that have been set as visible columns in the XML,
	 * and that are visible in the table as well.
	 * @return he column names set as visible columns in the XML, and that are visible in the table as
	 *         well
	 */
	public List getRealColumns() {
		// TODO maybe is a good idea to chage this method's name
		final List cols = new Vector();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String name = this.table.getColumnName(i);
			if (this.originalVisibleColumns.contains(name)) {
				cols.add(this.table.getColumnName(i));
			}
		}
		return cols;
	}

	/**
	 * Returns the current visible columns managed by the table.
	 * @return the current visible columns managed by the table
	 */
	public List getCurrentColumns() {
		final List cols = new Vector();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String name = this.table.getColumnName(i);
			if (this.visibleColumns.contains(name)) {
				cols.add(this.table.getColumnName(i));
			}
		}
		return cols;
	}

	public List getCalculatedColumns() {
		if (this.calculedColumns != null) {
			return new Vector(Arrays.asList(this.calculedColumns.keySet().toArray()));
		}
		return null;
	}

	public List getOriginalCalculatedColumns() {
		return this.originalCalculatedColumns;
	}

	public boolean isModifiableCalculatedColumns() {
		return this.modifiableCalculatedColumns;
	}

	static {
		Table.checkChartEnabled();
	}

	/**
	 * Check whether or not the charting functionality is enabled for this table.
	 * @return true if charting is allowed, false otherwise
	 */
	public static boolean isChartEnabled() {
		return Table.CHART_ENABLED;
	}

	/**
	 * Checks if the charting classes are set into the classpath
	 */
	protected static void checkChartEnabled() {
		Table.logger.debug("Looking for charting classes...");
		final long t = System.currentTimeMillis();
		try {
			Class.forName("com.jrefinery.data.DefaultXYDataset");
		} catch (final Exception e) {
			Table.logger.info("0.9.3 Charting classes not found");
			Table.logger.debug(null, e);
			Table.CHART_ENABLED = false;
		}
		Table.logger.trace("Check time: {}", System.currentTimeMillis() - t);
		Table.CHART_ENABLED = ChartVersionControl.isChartEnabled();
		Table.CHART_V1 = ChartVersionControl.isVersion_1_0();
	}

	/**
	 * Adds a button to the controls panel
	 * @param button the button to add to the panel
	 */
	public void addButtonToControls(final AbstractButton button) {
		this.addButtonToControls(button, false);
	}

	public void addButtonToControls(final AbstractButton button, final boolean useDefaultConfiguration) {
		if (useDefaultConfiguration) {
			this.configureControlButton(button);
		}
		if ((button != null) && !this.addButtons.contains(button)) {
			button.setMargin(new Insets(0, 0, 0, 0));
			this.controlsPanel.add(button);
			this.addButtons.add(button);
		}

		if ((button instanceof Internationalization) && (this.parentForm != null)) {
			this.parentForm.addComponentsToInternationalizeList(button, true);
		}
		// since 5.3.13 check permissions after added
		if (button instanceof TableComponent) {
			final TableComponent cT = (TableComponent) button;
			final Object o = cT.getKey();
			if (o != null) {
				final boolean v = this.checkComponentTablePermission(o, "visible");
				if (!v) {
					button.setVisible(false);
				} else {
					button.setVisible(true);
				}

				final boolean e = this.checkComponentTablePermission(o, "enabled");
				if (!e) {
					button.setEnabled(false);
				}
			}
		} else {
			Table.logger.warn("{} button in table control does not implement TableComponent", button);
		}
	}

	protected void configureControlButton(final AbstractButton button) {
		if (button != null) {
			if (!this.borderbuttons) {
				button.setBorder(BorderFactory.createEmptyBorder());
			}
			if (!this.opaquebuttons) {
				button.setOpaque(false);
				button.setContentAreaFilled(false);
			}
			if (this.listenerHighlightButtons != null) {
				button.addMouseListener(this.listenerHighlightButtons);
			}
		}
	}

	protected QuickFieldText createQuickFilter() {
		// since 5.2071EN-0.2
		final JLabel label;
		final JPopupMenu pageablePopUpMessage;
		if (this.quickFilterLocal) {
			label = new JLabel(Table.QUICK_FILTER_PAGEABLE_MESSAGE_KEY, ImageManager.getIcon(ImageManager.VIEW_DETAILS),
					SwingConstants.RIGHT);
			pageablePopUpMessage = new JPopupMenu(label.getText()) {

				@Override
				public void show(final Component c, int x, int y) {
					final Dimension dScreen = Toolkit.getDefaultToolkit().getScreenSize();
					final int height = this.getHeight() == 0 ? this.getPreferredSize().height : this.getHeight();
					final int width = this.getWidth() == 0 ? this.getPreferredSize().width : this.getWidth();
					// Avoid that the menu disappears of the window
					try {
						final Point p = c.getLocationOnScreen();
						x = Math.max(x - width, -p.x);
						if ((p.y + y + height) > dScreen.height) {
							y = Math.max(y - height, -p.y);
						}
					} catch (final Exception e) {
						Table.logger.error(null, e);
					}
					super.show(c, x, y - height);
				}
			};
			pageablePopUpMessage.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if ((e.getX() > ((label.getBounds().x + label.getBounds().width) - label.getIcon().getIconWidth()))
							&& (e.getX() < (label.getBounds().x + label.getBounds().width))) {
						if ((e.getY() > label.getBounds().y)
								&& (e.getY() < (label.getBounds().y + label.getBounds().height))) {
							Table.this.pageFetcher.downloadAll();
						}
					}
				}
			});
			label.setHorizontalTextPosition(SwingConstants.LEFT);
			pageablePopUpMessage.add(label);
		} else {
			label = null;
			pageablePopUpMessage = null;
		}

		final QuickFieldText quickFilterText = new QuickFieldText();

		quickFilterText.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				quickFilterText.repaint();
				quickFilterText.getParent().repaint();
			}

			@Override
			public void focusLost(final FocusEvent e) {
				quickFilterText.repaint();
				quickFilterText.getParent().repaint();
			}
		});

		quickFilterText.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(final DocumentEvent event) {
			}

			@Override
			public void insertUpdate(final DocumentEvent event) {
				this.update(event);
			}

			@Override
			public void removeUpdate(final DocumentEvent event) {
				this.update(event);
			}

			protected void update(final DocumentEvent e) {
				try {
					if ((Table.this.pageFetcher != null) && Table.this.pageFetcher.isPageableEnabled()
							&& (Table.this.pageFetcher.totalSize > Table.this.pageFetcher.pageSize)
							&& Table.defaultQuickFilterWarningVisibility) {
						if (Table.this.quickFilterLocal) {
							label.setText(ApplicationManager.getTranslation(Table.QUICK_FILTER_PAGEABLE_MESSAGE_KEY,
									Table.this.getResourceBundle()));
							pageablePopUpMessage.show(Table.this,
									quickFilterText.getBounds().x + quickFilterText.getBounds().width,
									quickFilterText.getBounds().y);
						}
						quickFilterText.requestFocus();
					}
					quickFilterText.executeFilter(e.getDocument().getText(0, e.getDocument().getLength()), Table.this);
				} catch (final BadLocationException ex) {
					Table.logger.error(null, ex);
				}
			}
		});

		quickFilterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				if (!Table.this.quickFilterLocal){
					if (Table.this.waitPanel!=null && Table.this.waitPanel.isVisible()){
						e.consume();
						return;
					}
				}
				super.keyTyped(e);
			}
		});


		return quickFilterText;
	}

	/**
	 * Removes a button from the controls panel
	 * @param button the button to remove
	 */
	public void removeButtonFromControls(final AbstractButton button) {
		if (button != null) {
			this.controlsPanel.remove(button);
			this.addButtons.remove(button);
		}
	}

	/**
	 * Adds a {@link #JComponent} to the controls panel
	 * @param component the component to be added
	 */
	public void addComponentToControls(final JComponent component) {
		if ((component != null) && !this.addComponents.contains(component)) {
			this.controlsPanel.add(component);
			this.addComponents.add(component);
			if ((component instanceof Internationalization) && (this.parentForm != null)) {
				this.parentForm.addComponentsToInternationalizeList(component, true);
			}
		}

	}

	/**
	 * Removes a {@link #JComponent} from the controls panel.
	 * @param component the component to remove
	 */
	public void removeComponentFromControls(final JComponent component) {
		if (component != null) {
			this.controlsPanel.remove(component);
			this.addComponents.remove(component);

			if ((component instanceof Internationalization) && (this.parentForm != null)) {
				this.parentForm.removeComponentsToInternationalizeList(component);
			}
		}
	}

	/**
	 * Updates row values in the table. The {@link #Hashtable} with the parameters contains both the
	 * keys that select the row and the values to be updated.
	 * @param rowData
	 * @see com.ontimize.gui.table.TableSorter#updateRowData
	 */
	public void updateRowData(final Map rowData) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		ts.updateRowData(rowData, ObjectTools.clone(this.getKeys()));
		this.table.repaint();
	}

	public void updateRowData(final Map rowData, final Map newkv) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		ts.updateRowData(rowData, newkv);
		this.table.repaint();
	}

	public void updateRowData(final Map rowData, final List columns, final Map newkv) {
		this.checkRefreshThread();
		final TableSorter ts = (TableSorter) this.table.getModel();
		ts.updateRowData(rowData, columns, newkv);
		this.table.repaint();
	}

	@Override
	public String getHelpIdString() {
		String sClassName = this.getClass().getName();
		sClassName = sClassName.substring(sClassName.lastIndexOf(".") + 1);
		return sClassName + "HelpId";
	}

	/**
	 * Sets the help id for this class in the {@link HelpUtilities}
	 *
	 * @see #getHelpIdString
	 */
	@Override
	public void installHelpId() {
		try {
			final String helpId = this.getHelpIdString();
			HelpUtilities.setHelpIdString(this, helpId);
		} catch (final Exception e) {
			Table.logger.error(null, e);
			return;
		}
	}

	protected boolean restricted = false;

	/**
	 * Determines if the table is restricted, this is, has client permission restrictions.
	 * @return true if the table is restricted; otherwise, false
	 */
	@Override
	public boolean isRestricted() {
		return this.restricted;
	}

	/**
	 * Configures the table to perform the operations in memory. In that case, the entity that will be
	 * use to perform the operations will be the one defined in the table configuration
	 * @param inMemory true if this component should perform operations in memory. false otherwise
	 * @see Table#init
	 */
	public void setMemoryOperations(final boolean inMemory) {
		this.operationInMemory = inMemory;

	}

	/**
	 * Adds a {@link #TableRowHeader} to the table. The {@link #TableRowHeader} will be actually set in
	 * the {@link #scrollPane}.
	 * @param tableRowHeader the TableRowHeader
	 */
	public void addTableRowHeader(final TableRowHeader tableRowHeader) {
		this.scrollPane.setRowHeaderView(tableRowHeader);
	}

	/**
	 * Applies a filter to the specified column. When the filter depends on a range, the array size must
	 * be at least 2. If values is null, a {@link SimpleFilter} will be set. In case that values has a
	 * String or a Boolean, a {@link SimpleFilter} will be applied, and in other case a {@link Filter}
	 * will be set.
	 * @param columnName the column in which the filter will be applied
	 * @param values the values to make the filter
	 * @param condition the condition to filter
	 */
	public void applyFilter(final String columnName, final Object[] values, final int condition) {
		if (this.table.getModel() instanceof TableSorter) {
			final TableSorter ts = (TableSorter) this.table.getModel();
			if (values.length == 0) {
				return;
			} else if (values[0] == null) {
				final TableSorter.SimpleFilter f = new TableSorter.SimpleFilter(null);
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), f);
			} else if ((values[0] instanceof String) || (values[0] instanceof Boolean)) {

				final TableSorter.SimpleFilter f = new TableSorter.SimpleFilter(values[0]);
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), f);
			} else {
				final TableSorter.Filter f = new TableSorter.Filter(condition, values);
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), f);
			}
		}
	}

	/**
	 * Applies a filter to the specified column.
	 * @param columnName
	 * @param filter must be an instance of the classes {@link MultipleFilter}, {@link Filter} ,
	 *        {@link SimpleFilter} or {@link DateFilter}
	 */
	public void applyFilter(final String columnName, final Object filter) {
		if (this.table.getModel() instanceof TableSorter) {
			final TableSorter ts = (TableSorter) this.table.getModel();
			if (filter == null) {
				return;
			}
			if (filter instanceof TableSorter.SimpleFilter) {
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), filter);
			} else if (filter instanceof TableSorter.Filter) {
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), filter);
			} else if (filter instanceof TableSorter.MultipleFilter) {
				ts.applyFilter(this.table.getColumn(columnName).getModelIndex(), filter);
			}
		}
	}

	/**
	 * Applies filters to the current table.
	 *
	 * @see TableSorter#applyFilter(Map)
	 * @see Filter
	 * @param filters is a Map containing the filters. The key is the columns name and the value
	 *        must be an instance of the classes {@link MultipleFilter}, {@link Filter} ,
	 *        {@link SimpleFilter} , {@link DateFilter}
	 */
	public void applyFilter(final Map filters) {

		if (this.table.getModel() instanceof TableSorter) {
			final TableSorter ts = (TableSorter) this.table.getModel();
			ts.applyFilter(filters);
		}
	}

	/**
	 * Removes all the filters set to the table.
	 */
	public void resetFilter() {
		if (this.table.getModel() instanceof TableSorter) {
			final TableSorter ts = (TableSorter) this.table.getModel();
			ts.resetFilter();
		}
	}

	/**
	 * Removes the filter set to the specified column.
	 * @param column the column name
	 */
	public void resetFilter(final String column) {
		if (this.table.getModel() instanceof TableSorter) {
			final TableSorter ts = (TableSorter) this.table.getModel();
			ts.resetFilter(column);
		}
	}

	/**
	 * Adds a column to the table.
	 * @param column the column name
	 */
	public void addColumn(final String column) {
		this.addColumn(column, true);
	}

	public void addColumn(final String[] columns) {
		if ((columns != null) && (columns.length > 0)) {
			for (int i = 0; i < columns.length; i++) {
				if (i == (columns.length - 1)) {
					this.addColumn(columns[i], true);
				} else {
					this.addColumn(columns[i], false);
				}
			}
		}
	}

	protected void addColumn(final String column, final boolean fireEvent) {
		if (!(this.attributes.contains(column)) && !this.calculedColumns.containsKey(column)) {
			if (this.table.getModel() instanceof TableSorter) {

				Table.logger.debug("Table: Adding column: {} previous column number = {} previous attributes: {}",
						column, this.table.getColumnCount(), this.attributes);

				// This is needed to conserve all the renderes and editor after
				// adding the column
				final Map allRender = this.getAllColumnRenderer();
				final Map allEditor = this.getAllColumnEditors();

				final TableSorter ts = (TableSorter) this.table.getModel();
				this.attributes.add(column);
				this.originalVisibleColumns.add(column);
				this.visibleColumns.add(column);
				this.reportCols.add(column);
				ts.addColumn(column, fireEvent);
				Table.logger.debug("Table: Column added: {} current column number= {}", column,
						this.table.getColumnCount());

				this.configureRenderEditor(allRender, allEditor);
				if (fireEvent) {
					this.setResourceBundle(this.resourcesFile);
				}
			}
		} else {
			Table.logger.warn("Table: The specified column already exists: {}", column);
		}
	}

	/**
	 * Adds a calculated column to the table.
	 * @param column the column name
	 * @param expression the column expression, for example SomeColumnName*5
	 */
	public void addCalculatedColumn(final String columnName, final String expression, final String renderKey) {

		final TableSorter sorter = this.getTableSorter();
		if ((!this.attributes.contains(columnName)) && !this.calculedColumns.containsKey(columnName)) {

			// This is needed to conserve all the renderes and editor after
			// adding the column
			final Map allRender = this.getAllColumnRenderer();
			final Map allEditor = this.getAllColumnEditors();

			if (sorter != null) {
				Table.logger.debug("Table: Adding calculated column: {} previous column number = {}", columnName,
						this.table.getColumnCount());
				this.originalVisibleColumns.add(columnName);
				this.visibleColumns.add(columnName);
				this.calculedColumns.put(columnName, expression);
				this.reportCols.add(columnName);

				sorter.addCalculatedColumn(columnName, expression);
				if ((renderKey != null) && !Table.DEFAULT_CELL_RENDERER.equalsIgnoreCase(renderKey)) {
					allRender.put(columnName, Table.getRendererMap().get(renderKey));
				}

				Table.logger.debug("Table: Column added: {} current column number= {}", columnName,
						this.table.getColumnCount());
				this.configureRenderEditor(allRender, allEditor);
				this.setResourceBundle(this.resourcesFile);

			}
		} else {
			Table.logger.debug("Table: The specified column already exists: {}", columnName);
		}
	}

	/**
	 * Creates a Map with all the columns that have a renderer.<br>
	 * Map key is the column name and value is the column renderer
	 * @return
	 */
	protected Map getAllColumnRenderer() {
		final int count = this.table.getColumnCount();
		final Map renderers = new Hashtable();
		for (int i = 0; i < count; i++) {
			final String cName = this.getColumnName(i);
			final TableCellRenderer rendererForColumn = this.getRendererForColumn(cName);
			if (rendererForColumn != null) {
				renderers.put(cName, rendererForColumn);
			}
		}
		return renderers;
	}

	/**
	 * Creates a Map with all the columns that have an editor.<br>
	 * Map key is the column name and value is the column editor
	 * @return
	 */
	protected Map getAllColumnEditors() {
		final int count = this.table.getColumnCount();
		final Map editors = new Hashtable();
		for (int i = 0; i < count; i++) {
			final String cName = this.getColumnName(i);
			final TableCellEditor editorForColumn = this.getEditorForColumn(cName);
			if (editorForColumn != null) {
				editors.put(cName, editorForColumn);
			}
		}
		return editors;
	}

	/**
	 * Configure the renderer and editor to the columns
	 * @param renderer Key is the column name and value is the renderer to set
	 * @param editor Key is the column name and value is the editor to set
	 */
	protected void configureRenderEditor(final Map renderer, final Map editor) {
		final int count = this.table.getColumnCount();
		for (int i = 0; i < count; i++) {
			final String cName = this.getColumnName(i);
			if (renderer != null) {
				final Object object = renderer.get(cName);
				if (object instanceof TableCellRenderer) {
					this.setRendererForColumn(cName, (TableCellRenderer) object);
				}
			}
			if (editor != null) {
				final Object object = editor.get(cName);
				if (object instanceof TableCellEditor) {
					this.setColumnEditor(cName, (TableCellEditor) object);
				}
			}
		}
	}

	/**
	 * Deletes the specified column from the table.
	 * @param column the column name
	 */
	public void deleteColumn(final String column) {
		this.deleteColumn(column, true);
	}

	public void deleteColumn(final String[] columns) {
		if ((columns != null) && (columns.length > 0)) {
			for (int i = 0; i < columns.length; i++) {
				if (i == (columns.length - 1)) {
					this.deleteColumn(columns[i], true);
				} else {
					this.deleteColumn(columns[i], false);
				}
			}
		}
	}

	protected void deleteColumn(final String column, final boolean fireEvent) {
		if (this.attributes.contains(column)) {
			if (this.table.getModel() instanceof TableSorter) {
				Table.logger.debug("Table: Deleting column: {} previous column number = {} previous attributes: {}",
						column, this.table.getColumnCount(), this.attributes);
				// This is needed to conserve all the renderes and editor after
				// deleting the column
				final Map allRenderers = new Hashtable();
				final Map allEditors = new Hashtable();
				if (fireEvent) {
					// Retrieve renderer and editor of columns before event
					for (final Object cName : this.attributes) {
						final TableCellRenderer rendererForColumn = this.getRendererForColumn(cName.toString());
						if (rendererForColumn != null) {
							allRenderers.put(cName, rendererForColumn);
						}

						final TableCellEditor editorForColumn = this.getEditorForColumn(cName.toString());
						if (editorForColumn != null) {
							allEditors.put(cName, editorForColumn);
						}
					}
				}

				// Map allRender = this.getAllColumnRenderer();
				// Map allEditor = this.getAllColumnEditors();
				// allRender.remove(column);
				// allEditor.remove(column);

				final TableSorter ts = (TableSorter) this.table.getModel();
				this.attributes.remove(column);
				this.visibleColumns.remove(column);
				this.originalVisibleColumns.remove(column);
				this.reportCols.remove(column);
				try {
					this.table.getColumn(column).removePropertyChangeListener(this.columnWidthListener);
				} catch (final Exception ex) {
					Table.logger.trace(null, ex);
				}
				ts.deleteColumn(column, fireEvent);
				Table.logger.debug("Table: Deleted column: {} current column number = {}", column,
						this.table.getColumnCount());
				if (fireEvent) {
					this.configureRenderEditor(allRenderers, allEditors);
					this.setResourceBundle(this.resourcesFile);
				}
			}
		}
	}

	/**
	 * Deletes the specified calculated column from the table.
	 * @param column the column name
	 */
	public void deleteCalculatedColumn(final String column) {
		if ((this.calculedColumns != null) && this.calculedColumns.containsKey(column)) {
			final TableSorter sorter = this.getTableSorter();
			if (sorter != null) {
				Table.logger.debug("Table: Deleting calculated column: {} previous column number = {}", column,
						this.table.getColumnCount());

				// This is needed to conserve all the renderes and editor after
				// deleting the column
				final Map allRender = this.getAllColumnRenderer();
				final Map allEditor = this.getAllColumnEditors();
				allRender.remove(column);
				allEditor.remove(column);

				this.calculedColumns.remove(column);
				this.visibleColumns.remove(column);
				this.originalVisibleColumns.remove(column);
				this.reportCols.remove(column);

				try {
					this.table.getColumn(column).removePropertyChangeListener(this.columnWidthListener);
				} catch (final Exception ex) {
					Table.logger.trace(null, ex);
				}

				sorter.deleteCalculatedColumn(column);
				Table.logger.debug("Table: Deleted column: {} current column number= {}", column,
						this.table.getColumnCount());
				this.configureRenderEditor(allRender, allEditor);
				this.setResourceBundle(this.resourcesFile);
			}
		}
	}

	/**
	 * Sets the fixed attributes to the detail form. Thas is, make the detail form to have, in the
	 * fields configured as fixattr, the same values that are placed in the form in which the talbe is
	 * placed.
	 */
	protected void setAttributesToFix() {
		if ((this.detailForm != null) && (this.attributesToFix != null)) {
			for (int i = 0; i < this.attributesToFix.size(); i++) {
				this.detailForm.setAttributeToFix(this.hAttributesToFixEquivalences.get(this.attributesToFix.get(i)),
						this.parentForm.getDataFieldValue(this.attributesToFix.get(i)));
			}
		}
	}

	public Integer getBlockedColumnIndex() {
		if (this.blockedTable == null) {
			return -1;
		}
		return this.blockedTable.getBlockedColumnIndex();
	}

	public void setBlockedColumnIndex(int column) {
		final BlockedTableModel model = (BlockedTableModel) this.blockedTable.getModel();
		this.mainSplit.setDividerSize(4);
		final int index = this.blockedTable.getBlockedColumnIndex();

		if (column > index) {
			final TableColumnModel tcModel = this.table.getColumnModel();
			int size = 0;
			for (int i = index + 1; i <= column; i++) {
				size = size + tcModel.getColumn(i).getWidth();
			}
			this.mainSplit.setDividerLocation(this.mainSplit.getDividerLocation() + size);
		} else if (column < index) {
			final TableColumnModel tcModel = this.blockedTable.getColumnModel();
			int size = 0;
			for (int i = 0; i <= column; i++) {
				size = size + tcModel.getColumn(i).getWidth();
			}
			this.mainSplit.setDividerLocation(size);
		} else {
			// Remove blockedColumn;
			this.mainSplit.setDividerSize(0);
			this.mainSplit.setDividerLocation(this.blockedTable.getColumnModel().getColumn(0).getWidth());
			column = 0;
		}

		this.fixBlockedVisibility();

		this.blockedTable.setBlockedColumnIndex(column);
		// this.doLayout();
	}

	protected void fixBlockedVisibility() {
		this.blockedScrollPane.setVisible(this.mainSplit.getDividerLocation() > 0);
		this.sumRowBlockedScrollPane.setVisible(this.mainSplit.getDividerLocation() > 0);
	}

	public boolean isBlockedEnabled() {
		if (this.blockedCols) {
			return this.scrollHorizontal;
		}
		return false;
	}

	/**
	 * Sets an horizontal scroll to the <code>Table</code>. This allows the <code>Table</code> to
	 * contain a big number of columns and display them.
	 * @param scrollH true to enable the scroll, false to disable it
	 */
	protected void setHorizontalScroll(final boolean scrollH) {
		this.scrollHorizontal = scrollH;

		if (this.scrollHorizontal) {
			if (this.scrollPane != null) {
				this.table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				this.sumRowTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

				this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.scrollPane.revalidate();
				this.sumRowScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				this.sumRowScrollPane.revalidate();

				this.blockedScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.blockedScrollPane.revalidate();

				this.sumRowBlockedScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
				this.sumRowBlockedScrollPane.revalidate();
			}
		} else {
			if (this.scrollPane != null) {
				this.table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
				this.sumRowTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);

				this.scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.sumRowScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.blockedScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.sumRowBlockedScrollPane
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				this.scrollPane.revalidate();
				this.sumRowScrollPane.revalidate();
				this.blockedScrollPane.revalidate();
				this.sumRowBlockedScrollPane.revalidate();
			}
		}
	}

	/**
	 * Returns a List containing the parent key names, this is, the names set in the XML to the
	 * attribute 'parentkeys'.
	 *
	 * @see #init
	 * @return the parent key names
	 */
	public List getParentKeys() {
		return this.getParentKeys(false);
	}

	public List getParentKeys(final boolean applyEquivalences) {
		final List v = new Vector();
		if (this.parentkeys != null) {
			for (int i = 0; i < this.parentkeys.size(); i++) {
				Object pkName = this.parentkeys.get(i);
				if (applyEquivalences) {
					pkName = this.getParentkeyEquivalentValue(pkName);
				}
				v.add(pkName);
			}
		}
		return v;
	}

	/**
	 * Get the name of the table column associated with the parent key field name
	 * @param parentkey
	 * @return
	 */
	public Object getParentkeyEquivalentValue(final Object parentkey) {
		if ((this.hParentkeyEquivalences != null) && this.hParentkeyEquivalences.containsKey(parentkey)) {
			return this.hParentkeyEquivalences.get(parentkey);
		}
		return parentkey;
	}

	/**
	 * Updates the value determined by the specified column and row in the Entity. If other data related
	 * to the row must be updates, can be passed as param.
	 * @param rowIndex the row indes
	 * @param viewColumnIndex the column index in the model
	 * @return the result of the update
	 * @throws Exception
	 * @deprecated Must be used {@link #updateTable(Hashtable, int, TableCellEditor, Hashtable, Object)}
	 *             Must be used {@link #updateTable(Hashtable, int, TableCellEditor, Hashtable, Object)}
	 */
	@Deprecated
	protected EntityResult updateTable(final int rowIndex, final int viewColumnIndex) throws Exception {
		return this.updateTable(rowIndex, viewColumnIndex, null);
	}

	/**
	 * Updates the value determined by the specified column and row in the Entity. If other data related
	 * to the row must be updates, can be passed as param.
	 * @param rowIndex the row index
	 * @param viewColumnIndex the column index in the model
	 * @param otherData other data to be updated in that entity
	 * @return the result of the update
	 * @throws Exception
	 * @deprecated Must be used {@link #updateTable(Hashtable, int, TableCellEditor, Hashtable, Object)}
	 */
	@Deprecated
	protected EntityResult updateTable(final int rowIndex, final int viewColumnIndex, final Map otherData) throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			return new EntityResultMapImpl();
		}
		return this.updateTable(rowIndex, viewColumnIndex, otherData, null);
	}

	/**
	 * Updates the value determined by the specified column and row in the Entity. If other data related
	 * to the row must be updates, can be passed as param.
	 * @param rowIndex the row index
	 * @param viewColumnIndex the column index in the model
	 * @param otherData other data to be updated in that entity
	 * @param previousData
	 * @return the result of the update
	 * @throws Exception
	 * @deprecated Must be used {@link #updateTable(Hashtable, int, TableCellEditor, Hashtable, Object)}
	 */

	@Deprecated
	protected EntityResult updateTable(final int rowIndex, final int viewColumnIndex, final Map otherData, final Object previousData)
			throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			return new EntityResultMapImpl();
		}

		// TODO because the modelRowIndex is nonsense (is the same for the model
		// and for the view) replace all rowIndex for rowIndex
		if ((this.entity != null) && (this.entity.length() != 0)) {
			final Map av = new Hashtable();
			final TableModel m = this.table.getModel();
			final Object col = m.getColumnName(this.table.convertColumnIndexToModel(viewColumnIndex));
			final Object oValue = m.getValueAt(rowIndex, this.table.convertColumnIndexToModel(viewColumnIndex));
			if (oValue != null) {
				av.put(col, oValue);
			} else {
				final TableCellEditor cellEditor = this.table.getCellEditor(rowIndex, viewColumnIndex);
				if ((cellEditor != null) && (cellEditor instanceof com.ontimize.gui.table.CellEditor)) {
					final com.ontimize.gui.table.CellEditor cE = (com.ontimize.gui.table.CellEditor) cellEditor;
					av.put(col, new NullValue(cE.getSQLDataType()));
				}
			}
			if (otherData != null) {
				av.putAll(otherData);
			}

			// To include calculted values in the update operation
			final Map calculatedRowData = this.getCalculatedRowData(rowIndex);
			if (calculatedRowData != null) {
				av.putAll(calculatedRowData);
			}

			final Map kv = new Hashtable();
			// Keys and parentkeys
			final List vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				final Object atr = vKeys.get(i);
				if (atr.equals(col)) {
					final Object oKeyValue = previousData;
					if (oKeyValue != null) {
						kv.put(atr, oKeyValue);
					}
				} else {
					// Object oKeyValue = table.getValueAt(rowIndex,
					// getColumnIndex((String) atr));
					final Object oKeyValue = m.getValueAt(rowIndex, this.table.getColumn(atr).getModelIndex());
					if (oKeyValue != null) {
						kv.put(atr, oKeyValue);
					}
				}
			}
			final List vParentkeys = this.getParentKeys();
			for (int i = 0; i < vParentkeys.size(); i++) {
				final Object atr = vParentkeys.get(i);
				final Object oParentkeyValue = this.parentForm.getDataFieldValueFromFormCache(atr.toString());
				if (oParentkeyValue != null) {
					kv.put(atr, oParentkeyValue);
				}
			}
			final Entity ent = this.locator.getEntityReference(this.getEntityName());
			return ent.update(av, kv, this.locator.getSessionId());
		} else {
			return new EntityResultMapImpl();
		}
	}

	/**
	 * Updates the value determined by the specified column and row in the Entity. If other data related
	 * to the row must be updates, can be passed as param.
	 * @param keysValues the values of the keys
	 * @param viewColumnIndex the column index in the model
	 * @param tableCellEditor the editor
	 * @param otherData other data to be updated in that entity
	 * @param previousData
	 * @return the result of the update
	 * @throws Exception
	 */

	protected EntityResult updateTable(final Map keysValues, final int viewColumnIndex, final TableCellEditor tableCellEditor,
			final Map otherData, final Object previousData) throws Exception {
		if ((this.entity != null) && (this.entity.length() != 0)) {
			final Map av = new Hashtable();
			final TableSorter model = (TableSorter) this.table.getModel();
			final Object col = model.getColumnName(this.table.convertColumnIndexToModel(viewColumnIndex));
			final Object newData = tableCellEditor.getCellEditorValue();
			if (newData != null) {
				av.put(col, newData);
			} else {
				if ((tableCellEditor != null) && (tableCellEditor instanceof com.ontimize.gui.table.CellEditor)) {
					final com.ontimize.gui.table.CellEditor cE = (com.ontimize.gui.table.CellEditor) tableCellEditor;
					av.put(col, new NullValue(cE.getSQLDataType()));
				}
			}

			if (otherData != null) {
				av.putAll(otherData);
			}

			// To include calculted values in the update operation
			final Map rowData = this.getRowDataForKeys(keysValues);

			final List calculatedColumns = model.getCalculatedColumnsName();
			for (int i = 0; i < calculatedColumns.size(); i++) {
				final Object column = calculatedColumns.get(i);
				if (rowData.containsKey(column)) {
					av.put(column, rowData.get(column));
				}
			}

			final Map kv = ObjectTools.clone(keysValues);

			// Keys and parentkeys
			final List vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				final Object atr = vKeys.get(i);
				if (atr.equals(col)) {
					final Object oKeyValue = previousData;
					if (oKeyValue != null) {
						kv.put(atr, oKeyValue);
					}
				}
			}
			// Parentkeys with equivalences
			final List vParentkeys = this.getParentKeys();
			for (int i = 0; i < vParentkeys.size(); i++) {
				final Object atr = vParentkeys.get(i);
				final Object oParentkeyValue = this.parentForm.getDataFieldValueFromFormCache(atr.toString());
				if (oParentkeyValue != null) {
					// since 5.2074EN-0.4
					// when equivalences, we must get equivalence value for
					// parentkey insteadof atr
					kv.put(this.getParentkeyEquivalentValue(atr), oParentkeyValue);
				}
			}
			final Entity ent = this.locator.getEntityReference(this.getEntityName());
			return ent.update(av, kv, this.locator.getSessionId());
		} else {
			return new EntityResultMapImpl();
		}
	}

	/**
	 * The reference locator set for this table.
	 */
	protected EntityReferenceLocator locator = null;

	/**
	 * Sets a reference to the EntityReferenceLocator
	 * @param referenceLocator the {@link EntityReferenceLocator}
	 */
	@Override
	public void setReferenceLocator(final EntityReferenceLocator referenceLocator) {
		this.locator = referenceLocator;

		// Configure the locator in all renderers and editors
		this.configureComponentsLocator(this.getAllColumnRenderer(), this.locator);
		this.configureComponentsLocator(this.getAllColumnEditors(), this.locator);

	}

	protected void configureComponentsLocator(final Map components, final EntityReferenceLocator locator) {
		if ((components != null) && (locator != null)) {
			final Iterator iterator = components.values().iterator();
			while (iterator.hasNext()) {
				final Object element = iterator.next();
				if (element instanceof ReferenceComponent) {
					((ReferenceComponent) element).setReferenceLocator(locator);
				}

				if ((this.parentForm != null) && (element instanceof CachedComponent)) {
					CacheManager.getDefaultCacheManager(locator).addCachedComponent((CachedComponent) element);
					((CachedComponent) element).setCacheManager(CacheManager.getDefaultCacheManager(locator));
				}
			}
		}
	}

	protected void configureComponentsParentForm(final Map components, final Form parentForm) {
		if ((components != null) && (parentForm != null)) {
			final Iterator iterator = components.values().iterator();
			while (iterator.hasNext()) {
				final Object element = iterator.next();
				if (element instanceof AccessForm) {
					((AccessForm) element).setParentForm(parentForm);
				}

				if ((this.locator != null) && (element instanceof CachedComponent)) {
					CacheManager.getDefaultCacheManager(this.locator).addCachedComponent((CachedComponent) element);
					((CachedComponent) element).setCacheManager(CacheManager.getDefaultCacheManager(this.locator));
				}
			}
		}
	}

	/**
	 * Returns the detail form name.
	 * @return the detail form name
	 */
	public String getFormName() {
		return this.formName;
	}

	/**
	 * Returns the insert detail form name.
	 * @return the insert detail form name
	 */
	public String getInsertFormName() {
		return this.insertFormName;
	}

	/**
	 * Determines if the table is grouped.
	 * @return true if the table is grouped; otherwise, false
	 */
	public boolean isGroup() {
		return ((TableSorter) this.table.getModel()).isGrouped();
	}

	/**
	 * Removes the grouping applied to the table.
	 */
	public void resetGroup() {
		((TableSorter) this.table.getModel()).resetGroup();
		if (this.buttonDelete != null) {
			this.setTableComponentEnabled(Table.BUTTON_DELETE, true);
		}
	}

	/**
	 * Sets whether the default charting functionality is enabled or not. If <code>enabled</code> is
	 * true it is; if it is false it is not.
	 * @param enabled true if default charting is enabled
	 */
	public void setDefaultChartsEnabled(final boolean enabled) {
		this.defaultChartsEnabled = enabled;
		if (!Table.CHART_ENABLED) {
			return;
		}
		if (this.buttonDefaultChart != null) {
			if ((!this.isEmpty()) && (enabled)) {
				this.buttonDefaultChart.setEnabled(enabled);
			}
			this.buttonDefaultChart.setVisible(enabled);
		}
	}

	/**
	 * Creates, adds to the control panel, and creates the action listener related to the button that
	 * manages the configuration of the sum rows.
	 */
	protected void installConfSumRowButton() {
		this.buttonSumRowSetup = new TableButton();
		if (this.buttonSumRowSetup instanceof TableComponent) {
			((TableComponent) this.buttonSumRowSetup).setKey(Table.BUTTON_SUM_ROW_SETUP);
		}

		final ImageIcon calcIcon = ImageManager.getIcon(ImageManager.TABLE_SUMROWSETUP);
		if (calcIcon != null) {
			this.buttonSumRowSetup.setIcon(calcIcon);
		} else {
			this.buttonSumRowSetup.setText("SumConf");
		}
		this.buttonSumRowSetup.setEnabled(true);
		this.buttonSumRowSetup.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonSumRowSetup);
		this.buttonSumRowSetup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				Table.this.showConfSumRowDialog();
			}
		});
	}

	protected void showConfSumRowDialog() {
		// If there is some cell in editing mode then finish the edition
		this.table.removeEditor();

		final Window w = SwingUtilities.getWindowAncestor(Table.this);
		if (this.sumRowSetupDialog == null) {
			if (w instanceof Frame) {
				this.sumRowSetupDialog = new SumRowSetupDialog((Frame) w, Table.this);
			} else if (w instanceof Dialog) {
				this.sumRowSetupDialog = new SumRowSetupDialog((Dialog) w, Table.this);
			} else {
				this.sumRowSetupDialog = new SumRowSetupDialog((Frame) null, Table.this);
			}
		}
		this.sumRowSetupDialog.setColumn();
		this.sumRowSetupDialog.setResourceBundle(this.resourcesFile);
		this.sumRowSetupDialog.pack();
		ApplicationManager.center(this.sumRowSetupDialog);
		this.sumRowSetupDialog.setVisible(true);
	}

	/**
	 * @since 5.2079EN-0.1
	 * @return sumrowsetup dialog instance
	 */
	public SumRowSetupDialog getSumRowSetupDialog() {
		return this.sumRowSetupDialog;
	}

	/**
	 * Creates, adds to the control panel, and creates the action listener related to the button that
	 * manages the configuration of the visible columns.
	 */
	protected void installConfVisibleColsButtons() {
		this.buttonVisibleColsSetup = new TableButton();
		if (this.buttonVisibleColsSetup instanceof TableComponent) {
			((TableComponent) this.buttonVisibleColsSetup).setKey(Table.BUTTON_VISIBLE_COLS_SETUP);
		}
		final Icon confColsIcon = this.getButtonVisibleColsSetupIcon(false);
		if (confColsIcon != null) {
			this.buttonVisibleColsSetup.setIcon(confColsIcon);
		} else {
			this.buttonVisibleColsSetup.setText("ColsConf");
		}
		this.buttonVisibleColsSetup.setEnabled(false);
		this.buttonVisibleColsSetup.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonVisibleColsSetup);
		this.buttonVisibleColsSetup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Window w = SwingUtilities.getWindowAncestor(Table.this);
				if (Table.this.visibleColsSetupDialog == null) {
					if (w instanceof Frame) {
						Table.this.visibleColsSetupDialog = new VisibleColsSetupDialog((Frame) w, Table.this);
					} else if (w instanceof Dialog) {
						Table.this.visibleColsSetupDialog = new VisibleColsSetupDialog((Dialog) w, Table.this);
					} else {
						Table.this.visibleColsSetupDialog = new VisibleColsSetupDialog((Frame) null, Table.this);
					}
				}
				Table.this.visibleColsSetupDialog.setColumn();
				Table.this.visibleColsSetupDialog.setResourceBundle(Table.this.resourcesFile);
				Table.this.visibleColsSetupDialog.pack();
				ApplicationManager.center(Table.this.visibleColsSetupDialog);
				Table.this.visibleColsSetupDialog.checkSelectPosition(-1);
				Table.this.visibleColsSetupDialog.setVisible(true);
			}
		});
		this.setVisibleColsConfigurationAllowed(this.allowSetupVisibleColumns);
	}

	/**
	 * Creates, adds to the control panel, and creates the action listener related to the button that
	 * open the charting window.
	 */
	protected void installDefaultChartsButton() {
		if (!Table.CHART_ENABLED) {
			return;
		}
		this.buttonDefaultChart = new ChartButton(this);
		this.buttonDefaultChart.setKey(Table.BUTTON_DEFAULT_CHART);
		final ImageIcon chartsIcon = ImageManager.getIcon(ImageManager.TABLE_DEFAULT_CHARTS);
		if (chartsIcon != null) {
			this.buttonDefaultChart.setIcon(chartsIcon);
		} else {
			this.buttonDefaultChart.setText("ChartsConf");
		}

		if (this.dynamicTable) {
			// In a dynamic table it is not possible to save default chart
			// configuration, so hide the option
			this.buttonDefaultChart.getPreferredSize().width = this.buttonDefaultChart.getPreferredSize().width
					- this.buttonDefaultChart.getMenuButton().getPreferredSize().width;
			this.buttonDefaultChart.getMenuButton().setVisible(false);

			((ChartButton) this.buttonDefaultChart).setLoadButtonVisible(false);
			((ChartButton) this.buttonDefaultChart).setSaveButtonVisible(false);
		}

		this.buttonDefaultChart.setEnabled(false);
		this.buttonDefaultChart.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonDefaultChart);

	}

	/**
	 * Retuns the resources file used by the table.
	 * @return
	 */
	public ResourceBundle getResourceBundle() {
		return this.resourcesFile;
	}

	/**
	 * Sets whether or not the <code>DetailForm</code> of this component is enabled.
	 * @param enabled true if this component should be enabled, false otherwise
	 */
	public void setEnabledDetail(final boolean enabled) {
		this.enabledDetail = enabled;
	}

	/**
	 * If the table has a detail form, the method returns true. False, in other case.
	 * @return true if the table has detail form
	 */
	public boolean hasForm() {
		return this.formName != null;
	}

	/**
	 * Determines if the detail form of the table is enable.
	 * @return true if the detail form is enable, false otherwise
	 */
	public boolean getEnabledDetail() {
		return this.enabledDetail;
	}

	/**
	 * Determines if the table is sorted.
	 * @return true if the table is sorted; otherwise, false
	 */
	public boolean isSorted() {
		return ((TableSorter) this.table.getModel()).isSorted();
	}

	/**
	 * Returns the view index of the first column that is sorted.
	 *
	 * @see TableSorter#getFirstSortedColumn
	 * @return the view index of the first column that is sorted, null when the table is not sorted
	 */
	public int getViewOrderColumnIndex() {
		if (!this.isSorted()) {
			return -1;
		}
		return this.table.convertColumnIndexToView(((TableSorter) this.table.getModel()).getFirstSortedColumn());
	}

	/**
	 * Determines whether the first sorting applied to this table is ascending or not.
	 * @return true if the first column sorted has an ascending sorting; false otherwise
	 */
	protected boolean isAscending() {
		return ((TableSorter) this.table.getModel()).isAscending();
	}

	protected List editionListeners = new Vector(0, 3);

	/**
	 * Notifies the edition listeners that the edition has been stopped.
	 * @param value the new value
	 * @param previousValue the old value
	 * @param rowIndex the row that has been changed
	 * @param viewColumnIndex the column that has been changed
	 */
	protected void fireEditingStopped(final Object value, final Object previousValue, final int rowIndex, final int viewColumnIndex) {
		if ((rowIndex < 0) || (viewColumnIndex < 0)) {
			return;
		}
		final Object id = this.table.getColumnModel().getColumn(viewColumnIndex).getIdentifier();
		final TableEditionEvent e = new TableEditionEvent(this, value, rowIndex, viewColumnIndex, id.toString(),
				previousValue);
		for (int i = 0; i < this.editionListeners.size(); i++) {
			((TableEditorListener) this.editionListeners.get(i)).editingStopped(e);
		}
	}

	/**
	 * Notifies the edition listeners that the edition will stop.
	 * @param value the new value
	 * @param previousValue the old value
	 * @param rowIndex the row that has been changed
	 * @param viewColumnIndex the column that has been changed
	 * @throws com.ontimize.gui.table.EditingVetoException
	 */
	protected void fireEditingWillStop(final Object value, final Object previousValue, final int rowIndex, final int viewColumnIndex)
			throws com.ontimize.gui.table.EditingVetoException {
		if ((rowIndex < 0) || (viewColumnIndex < 0)) {
			return;
		}
		final Object id = this.table.getColumnModel().getColumn(viewColumnIndex).getIdentifier();
		final TableEditionEvent e = new TableEditionEvent(this, value, rowIndex, viewColumnIndex, id.toString(),
				previousValue);

		for (int i = 0; i < this.editionListeners.size(); i++) {
			((TableEditorListener) this.editionListeners.get(i)).editingWillStop(e);
		}
	}

	/**
	 * Notifies the edition listeners that the edition has been cancel.
	 * @param rowIndex the row that has been changed
	 * @param viewColumnIndex the column that has been changed
	 */
	protected void fireEditingCancelled(final int rowIndex, final int viewColumnIndex) {
		if ((rowIndex < 0) || (viewColumnIndex < 0)) {
			return;
		}
		final Object id = this.table.getColumnModel().getColumn(viewColumnIndex).getIdentifier();
		final TableEditionEvent e = new TableEditionEvent(this, null, rowIndex, viewColumnIndex, id.toString());

		for (int i = 0; i < this.editionListeners.size(); i++) {
			((TableEditorListener) this.editionListeners.get(i)).editingCanceled(e);
		}
	}

	/**
	 * Adds a {@link #TableEditorListener} to the table.
	 * @param listener
	 */
	public void addTableEditorListener(final TableEditorListener listener) {
		if (listener != null) {
			this.editionListeners.add(listener);
		}
	}

	/**
	 * Removes a {@link #TableEditorListener} from the table.
	 * @param listener the listener to remove
	 */
	public void removeTableEditorListener(final TableEditorListener listener) {
		if (listener != null) {
			this.editionListeners.remove(listener);
		}
	}

	/**
	 * Sets all the editable columns determined by the <code>Table</code> definition.
	 *
	 * @see #setEditableColumn(String , boolean )
	 */
	private void setEditableColumns() {
		for (int i = 0; i < this.editableColumns.size(); i++) {
			final boolean bUpdateEntity = this.editableColumnsUpdateEntity.contains(this.editableColumns.get(i));
			this.setEditableColumn((String) this.editableColumns.get(i), bUpdateEntity);
		}
	}

	public List getEditableColumns() {
		return this.editableColumns;
	}

	/**
	 * Sets whether the table header will adjusts its size automatically.
	 * @param autoFixHead true if the table header should auto adjust
	 */
	public void setAutoFixHead(final boolean autoFixHead) {
		this.autoFixHead = autoFixHead;
		if ((this.table != null) && (this.table.getModel() instanceof TableSorter)) {
			((TableSorter) this.table.getModel()).setFitHeadSize(this.autoFixHead);
		}
	}

	/**
	 * Sets whether or not the visible columns can be configured by the user.
	 * @param allowConfiguration true if the visible columns configuration should be enabled, false
	 *        otherwise
	 */
	public void setVisibleColsConfigurationAllowed(final boolean allowConfiguration) {
		this.allowSetupVisibleColumns = allowConfiguration;
		if (this.buttonVisibleColsSetup != null) {
			this.buttonVisibleColsSetup.setVisible(allowConfiguration);
		}
		if (this.buttonVisibleColsSetup != null) {
			this.buttonVisibleColsSetup.setEnabled(allowConfiguration);
		}
	}

	protected CellRenderer.CellRendererColorManager cellRendererColorManager = null;

	/**
	 * Sets the color manager for all the {@link CellRenderer} contained by the table.
	 * @param colorManager the new color manager
	 */
	public void setCellRendererColorManager(final CellRenderer.CellRendererColorManager colorManager) {
		this.cellRendererColorManager = colorManager;
		final List cols = this.getAttributeList();
		for (int i = 0; i < cols.size(); i++) {
			final TableColumn tc = this.getJTable().getColumn(cols.get(i));
			if (tc != null) {
				final TableCellRenderer rend = tc.getCellRenderer();
				if (rend instanceof CellRenderer) {
					((CellRenderer) rend).setCellRendererColorManager(colorManager);
				} else if (rend instanceof ComboReferenceCellRenderer) {
					((ComboReferenceCellRenderer) rend).setCellRendererColorManager(colorManager);
				}
			}
		}
		final TableCellRenderer[] rends = this.getDefaultRenderers();
		for (int i = 0; i < rends.length; i++) {
			if (rends[i] instanceof CellRenderer) {
				((CellRenderer) rends[i]).setCellRendererColorManager(colorManager);
			}
		}
	}

	protected CellRenderer.CellRendererFontManager cellRendererFontManager = null;

	/**
	 * Sets the font manager for all the {@link CellRenderer} contained by the table.
	 * @param fontManager
	 */
	public void setCellRendererFontManager(final CellRenderer.CellRendererFontManager fontManager) {
		this.cellRendererFontManager = fontManager;
		final List cols = this.getAttributeList();
		for (int i = 0; i < cols.size(); i++) {
			final TableColumn tc = this.getJTable().getColumn(cols.get(i));
			if (tc != null) {
				final TableCellRenderer rend = tc.getCellRenderer();
				if (rend instanceof CellRenderer) {
					((CellRenderer) rend).setCellRendererFontManager(fontManager);
				}
			}
		}
		final TableCellRenderer[] rends = this.getDefaultRenderers();
		for (int i = 0; i < rends.length; i++) {
			if (rends[i] instanceof CellRenderer) {
				((CellRenderer) rends[i]).setCellRendererFontManager(fontManager);
			}
		}
	}

	/**
	 * Deletes the specified sorting and the filtering preference.
	 * @param confName the name given to the configuration when saved
	 */
	protected void deteleFilterOrderConfiguration(final String confName) {

		final String sPreferenceKey = this.getFilterOrderConfPreferenceKey(confName);
		// If name is not null then save the preference
		final String sKey = this.getFilterOrderConfigurationPreferenceKey();
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		if ((prefs != null) && (confName != null)) {
			prefs.setPreference(this.getUser(), sPreferenceKey, null);
			final String pref = prefs.getPreference(this.getUser(), sKey);
			if (pref != null) {
				final List tokens = ApplicationManager.getTokensAt(pref, ";");
				if (tokens.contains(confName)) {
					tokens.remove(confName);
					final String sNew = ApplicationManager.vectorToStringSeparateBySemicolon(tokens);
					ap.getPreferences().setPreference(this.getUser(), sKey, sNew);
					ap.getPreferences().savePreferences();
				}
			}
		}
	}

	/**
	 * Saves the table filter configuration.
	 * @param confName the name given to the configuration
	 */
	protected void saveOrderFilterConfiguration(final String confName) {
		this.saveFilterOrderConfiguration(confName, false);
	}

	/**
	 * Saves the table filter configuration with a name, and optionally the column position and widths.
	 * @param confName the name given to the configuration;
	 * @param savePositionAndWidth if true, column position and width will be saved
	 */
	protected void saveFilterOrderConfiguration(final String confName, final boolean savePositionAndWidth) {
		if (this.dynamicTable && !this.dynamicPivotable) {
			// Dynamic tables have not preferences of order and filters
			return;
		}

		final TableSorter ts = (TableSorter) this.table.getModel();
		final String[] colOrd = this.getOrderColumns();
		final boolean[] asce = this.getAscendents();
		Map hFilter = ts.getFilters();
		// since 5.2076EN-0.2 - quickfilter is not saved in filter configuration
		if ((this.quickFilterText != null) && !"".equals(this.quickFilterText.getText())) {
			hFilter = new Hashtable();
		}
		final Map hFilter2 = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(hFilter.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oValue = hFilter.get(oKey);
			if (oValue instanceof TableSorter.Filter) {
				final TableSorter.Filter v = (TableSorter.Filter) oValue;
				final Object[] oValues = v.values;
				for (int i = 0; i < oValues.length; i++) {
					if (oValues[i] instanceof java.util.Date) {
						oValues[i] = new FilterDate(((java.util.Date) oValues[i]).getTime());
					}
				}
			}
			hFilter2.put(oKey, oValue);
		}
		if (confName == null) {
			this.defaultFilter = hFilter;
		}

		String sValue = null;
		final String sPreferenceKey = this.getFilterOrderConfPreferenceKey(confName);
		String col = null;
		String sAscent = null;
		if ((colOrd != null) && (colOrd.length > 0)) {
			col = ApplicationManager.vectorToStringSeparateBy(new Vector(Arrays.asList(colOrd)), ":");
			final List aux = new Vector();
			for (int i = 0; i < asce.length; i++) {
				if (asce[i]) {
					aux.add(Boolean.TRUE);
				} else {
					aux.add(Boolean.FALSE);
				}
			}
			sAscent = ApplicationManager.vectorToStringSeparateBy(aux, ":");
		}
		sValue = col + ";" + sAscent;

		if (savePositionAndWidth) {
			sValue = sValue + ";" + this.createColumnPositionAndWidthPreference();
		} else {
			sValue = sValue + ";" + null;
		}

		if (hFilter != null) {
			try {
				final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				final ObjectOutputStream out = new ObjectOutputStream(bOut);
				out.writeObject(hFilter2);
				out.flush();
				final String s = "BASE64"
						+ new String(com.ontimize.jee.common.util.Base64Utils.encode(bOut.toByteArray()));
				out.close();
				sValue = sValue + ";" + s;
				final Application ap = ApplicationManager.getApplication();
				if (ap.getPreferences() != null) {
					ap.getPreferences().setPreference(this.getUser(), sPreferenceKey, sValue);
					ap.getPreferences().savePreferences();
				}
			} catch (final Exception e) {
				Table.logger.error(null, e);
			}
		} else {
			if (this.parentForm != null) {
				final Application ap = ApplicationManager.getApplication();
				if (ap.getPreferences() != null) {
					ap.getPreferences().setPreference(this.getUser(), sPreferenceKey, sValue);
					ap.getPreferences().savePreferences();
				}
			}
		}

		// If name is not null then save the preference
		final String sKey = this.getFilterOrderConfigurationPreferenceKey();
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		if ((prefs != null) && (confName != null)) {
			final String pref = prefs.getPreference(this.getUser(), sKey);
			if (pref != null) {
				final List tokens = ApplicationManager.getTokensAt(pref, ";");
				if (!tokens.contains(confName)) {
					tokens.add(confName);
					final String sNew = ApplicationManager.vectorToStringSeparateBySemicolon(tokens);
					ap.getPreferences().setPreference(this.getUser(), sKey, sNew);
					ap.getPreferences().savePreferences();
				}
			} else {
				final String sNew = confName;
				ap.getPreferences().setPreference(this.getUser(), sKey, sNew);
				ap.getPreferences().savePreferences();
			}
		}
	}

	/**
	 * Get the value of the table preference, optionally with the position and with of the columns
	 * @param savePositionAndWidth if true, column position and width will added to value
	 */
	protected String getValueFilterOrderConfiguration(final boolean savePositionAndWidth) {
		String sValue = null;
		if (this.dynamicTable && !this.dynamicPivotable) {
			// Dynamic tables have not preferences of order and filters
			return sValue;
		}

		final TableSorter ts = (TableSorter) this.table.getModel();
		final String[] colOrd = this.getOrderColumns();
		final boolean[] asce = this.getAscendents();
		Map hFilter = ts.getFilters();
		// since 5.2076EN-0.2 - quickfilter is not saved in filter configuration
		if ((this.quickFilterText != null) && !"".equals(this.quickFilterText.getText())) {
			hFilter = new Hashtable();
		}
		final Map hFilter2 = new Hashtable();
		final Enumeration enumKeys = Collections.enumeration(hFilter.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final Object oValue = hFilter.get(oKey);
			if (oValue instanceof TableSorter.Filter) {
				final TableSorter.Filter v = (TableSorter.Filter) oValue;
				final Object[] oValues = v.values;
				for (int i = 0; i < oValues.length; i++) {
					if (oValues[i] instanceof java.util.Date) {
						oValues[i] = new FilterDate(((java.util.Date) oValues[i]).getTime());
					}
				}
			}
			hFilter2.put(oKey, oValue);
		}

		String col = null;
		String sAscent = null;
		if ((colOrd != null) && (colOrd.length > 0)) {
			col = ApplicationManager.vectorToStringSeparateBy(new Vector(Arrays.asList(colOrd)), ":");
			final List aux = new Vector();
			for (int i = 0; i < asce.length; i++) {
				if (asce[i]) {
					aux.add(Boolean.TRUE);
				} else {
					aux.add(Boolean.FALSE);
				}
			}
			sAscent = ApplicationManager.vectorToStringSeparateBy(aux, ":");
		}
		sValue = col + ";" + sAscent;

		if (savePositionAndWidth) {
			sValue = sValue + ";" + this.createColumnPositionAndWidthPreference();
		} else {
			sValue = sValue + ";" + null;
		}

		if (hFilter != null) {

			try {
				final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				ObjectOutputStream out;
				out = new ObjectOutputStream(bOut);
				out.writeObject(hFilter2);
				out.flush();
				final String s = "BASE64"
						+ new String(com.ontimize.jee.common.util.Base64Utils.encode(bOut.toByteArray()));
				out.close();
				sValue = sValue + ";" + s;
				return sValue;
			} catch (final IOException e) {
				Table.logger.error("Error retrieving filter data. ", e);
				return sValue;
			}

		} else {
			return sValue;
		}
	}

	protected ExtendedJPopupMenu menuOrderFilterSetup = null;

	protected ActionListener sortFilterOrderItemListener = null;

	protected ActionListener orderFilterSetupDeleteItemsListener = null;

	protected ActionListener addShareFilterSetupItemsListener = null;

	protected ActionListener obtainShareElementMessageItemListener = null;

	protected ActionListener stopSharingElementItemListener = null;

	protected ActionListener removeTargetSharedElementItemListener = null;

	protected ActionListener editSharingElementItemListenes = null;

	protected ActionListener addTargetToSharedElementItemListener = null;

	protected ActionListener loadFilterSharedElementItemListener = null;

	protected JMenuItem menuDefaultOrderSetup = new JMenuItem("table.load_default_configuration");

	protected JMenuItem menuSaveDefaultOrderConfiguration = new JMenuItem("table.save_as_default");

	protected JMenuItem menuSave = new JMenuItem("save");

	protected static String loadDefaultConfigurationKey = "table.load_default_configuration";

	protected static String saveAsDefaultConfigurationKey = "table.save_as_default";

	/**
	 * Class that contains a TableButton and a JButton into a JPanel..
	 */
	protected static String saveKey = "save";

	class AuxPanel extends JPanel implements MenuElement {

		JButton button = null;

		TableButton tableButton1 = null;

		TableButton tableButton2 = null;

		TableButton tableButton3 = null;

		TableButton tableButtonI = null;

		public AuxPanel(final TableButton tableButton1, final JButton jButton) {
			this.tableButton1 = tableButton1;
			this.button = jButton;
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);
			this.add(tableButton1, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(jButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		public AuxPanel(final TableButton tableButton1, final TableButton tableButton2, final JButton jButton) {
			this.tableButton1 = tableButton1;
			this.tableButton2 = tableButton2;
			this.button = jButton;
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);
			this.add(jButton, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton1, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton2, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		public AuxPanel(final TableButton tableButton1, final TableButton tableButton2, final TableButton tableButtonI, final JButton jButton) {
			this.tableButton1 = tableButton1;
			this.tableButton2 = tableButton2;
			this.tableButtonI = tableButtonI;
			this.button = jButton;
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);
			this.add(tableButtonI, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(jButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton1, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton2, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		public AuxPanel(final TableButton tableButton1, final TableButton tableButton2, final TableButton tableButton3,
				final TableButton tableButtonI, final JButton jButton) {
			this.tableButton1 = tableButton1;
			this.tableButton2 = tableButton2;
			this.tableButton3 = tableButton3;
			this.tableButtonI = tableButtonI;
			this.button = jButton;
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);
			this.add(tableButtonI, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(jButton, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton1, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton2, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(tableButton3, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		/**
		 * Empty
		 */
		@Override
		public void menuSelectionChanged(final boolean isIncluded) {
			// m.menuSelectionChanged(isIncluded);
		}

		/**
		 * Empty
		 */
		@Override
		public void processMouseEvent(final MouseEvent e, final MenuElement[] path, final MenuSelectionManager manager) {
			// m.processMouseEvent(e,path,manager);
		}

		/**
		 * Empty
		 */
		@Override
		public void processKeyEvent(final KeyEvent e, final MenuElement[] path, final MenuSelectionManager manager) {
			// m.processKeyEvent(e,path,manager);
		}

		/**
		 * Returns the first MenuElement
		 */
		@Override
		public MenuElement[] getSubElements() {
			return new MenuElement[0];
		}

		/**
		 * Returns this
		 */
		@Override
		public Component getComponent() {
			return this;
		}

	}

	/**
	 * Method call by the listener of the TableButton that manages the saving of the filter
	 * configuration. Creates the menu corresponding to the stored configurations and displays it.
	 * @param e the event
	 */
	protected void configureFilterOrder(final ActionEvent e) {
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		if (prefs == null) {
			return;
		}
		// Configuration window
		if (this.menuOrderFilterSetup == null) {
			this.menuOrderFilterSetup = new ExtendedJPopupMenu();
			if (this.buttonSaveFilterOrderSetup != null) {
				((TableButtonPopupble) this.buttonSaveFilterOrderSetup).setMenu(this.menuOrderFilterSetup);
			}
			this.menuSave.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					// Ask
					final Object s = MessageDialog.showInputMessage(SwingUtilities.getWindowAncestor(Table.this),
							"table.enter_configuration_name", Table.this.resourcesFile);
					if (s != null) {
						final String str = s.toString();
						final int i = JOptionPane.showConfirmDialog((Component) e.getSource(),
								ApplicationManager.getTranslation(Table.SAVE_WIDTH_POSITION_COLUMNS,
										Table.this.resourcesFile));
						if (i == JOptionPane.YES_OPTION) {
							Table.this.saveFilterOrderConfiguration(str, true);
						} else if (i == JOptionPane.NO_OPTION) {
							Table.this.saveFilterOrderConfiguration(str, false);
						} else {
							return;
						}
					}
				}
			});

			this.menuDefaultOrderSetup.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.loadFilterOrderConfiguration(null);
				}
			});
			this.menuSaveDefaultOrderConfiguration.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int i = JOptionPane.showConfirmDialog((Component) e.getSource(),
							ApplicationManager.getTranslation(Table.SAVE_WIDTH_POSITION_COLUMNS,
									Table.this.resourcesFile));
					if (i == JOptionPane.YES_OPTION) {
						Table.this.saveFilterOrderConfiguration(null, true);
					} else if (i == JOptionPane.NO_OPTION) {
						Table.this.saveFilterOrderConfiguration(null, false);
					} else {
						return;
					}
				}
			});
			this.sortFilterOrderItemListener = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (e.getSource() instanceof JButton) {
						Table.this.menuOrderFilterSetup.setVisible(false);
						Table.this.loadFilterOrderConfiguration(((JButton) e.getSource()).getActionCommand());
					}
				}
			};

			this.orderFilterSetupDeleteItemsListener = new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (e.getSource() instanceof JButton) {
						final Window w = SwingUtils.getWindowAncestor((Component) e.getSource());
						// SwingUtilities.getWindowAncestor(((Component)
						// e.getSource())).setVisible(false);
						if (MessageDialog.showQuestionMessage(w, ApplicationManager
								.getTranslation(Table.M_WOULD_YOU_LIKE_TO_DELETE_THIS_CONFIGURATION_OF_FILTER))) {
							Table.this.deteleFilterOrderConfiguration(((JButton) e.getSource()).getActionCommand());
							Table.this.menuOrderFilterSetup.setVisible(false);
						}
					}
				}
			};

			if (this.shareRemoteReferenceFilters) {
				this.addShareFilterSetupItemsListener = new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (e.getSource() instanceof JButton) {
							try {
								final Point p = ((Component) e.getSource()).getLocationOnScreen();
								// SwingUtilities.getWindowAncestor(((Component)
								// e.getSource())).setVisible(false);
								final String filterName = ((TableButton) e.getSource()).getActionCommand();
								final Window w = SwingUtils.getWindowAncestor((Component) e.getSource());
								FormAddSharedReference f = new FormAddSharedReference(w, true,
										ApplicationManager.getApplication().getReferenceLocator(),
										Table.this.getFilterOrderConfigurationValue(filterName),
										Table.this.getFilterOrderConfPreferenceKey(null),
										((ClientReferenceLocator) ApplicationManager.getApplication()
												.getReferenceLocator()).getUser(),
										"", filterName, false, p);
								if (f.getButtonOptionResult()) {
									Table.this.deteleFilterOrderConfiguration(filterName);
								}
								f = null;

							} catch (final Exception e1) {
								Table.logger.error(null, e1);
							}
						}
					}
				};
			}

			if (!ApplicationManager.useOntimizePlaf) {
				this.menuOrderFilterSetup.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
			}
		}

		if (prefs.getPreference(this.getUser(), this.getFilterOrderConfPreferenceKey(null)) != null) {
			this.menuDefaultOrderSetup.setEnabled(true);
		} else {
			this.menuDefaultOrderSetup.setEnabled(false);
		}

		this.menuSave.setText(ApplicationManager.getTranslation(Table.saveKey, this.resourcesFile));
		this.menuSaveDefaultOrderConfiguration
		.setText(ApplicationManager.getTranslation(Table.saveAsDefaultConfigurationKey, this.resourcesFile));
		this.menuDefaultOrderSetup
		.setText(ApplicationManager.getTranslation(Table.loadDefaultConfigurationKey, this.resourcesFile));

		final Component[] c = this.menuOrderFilterSetup.getComponents();
		for (int i = 0; (c != null) && (i < c.length); i++) {
			if (c[i] instanceof JPanel) {
				final Component[] c2 = ((JPanel) c[i]).getComponents();
				for (int j = 0; j < c2.length; j++) {
					if (c2[j] instanceof RolloverButton) {
						((RolloverButton) c2[j]).removeActionListener(this.sortFilterOrderItemListener);
					} else if (c2[j] instanceof JButton) {
						((JButton) c2[j]).removeActionListener(this.orderFilterSetupDeleteItemsListener);
					}
				}
				((JPanel) c[i]).removeAll();
			}
		}

		this.menuOrderFilterSetup.removeAll();
		// By default

		this.menuOrderFilterSetup.add(this.menuDefaultOrderSetup);

		// Now all the others
		final String sKey = this.getFilterOrderConfigurationPreferenceKey();
		final String pref = prefs.getPreference(this.getUser(), sKey);
		final List tokens = ApplicationManager.getTokensAt(pref, ";");
		if (this.shareRemoteReferenceFilters) {
			this.showShareFilterTableList(tokens);
		} else {
			for (int i = 0; i < tokens.size(); i++) {
				if (i == 0) {
					this.menuOrderFilterSetup.addSeparator();
				}
				final String token = (String) tokens.get(i);
				final JButton menu = new RolloverButton(token) {

					@Override
					public Dimension getPreferredSize() {
						final Dimension d = super.getPreferredSize();
						d.height = 22;
						return d;
					}

					@Override
					public boolean isFocusTraversable() {
						return false;
					}

					@Override
					public boolean isRequestFocusEnabled() {
						return false;
					}
				};
				menu.setActionCommand(token);
				menu.addActionListener(this.sortFilterOrderItemListener);
				menu.setForeground(Color.blue.darker());
				menu.setIcon(ImageManager.getIcon(ImageManager.EMPTY_16));

				final TableButton b = new TableButton();
				b.setIcon(ImageManager.getIcon(ImageManager.RECYCLER));
				b.setToolTipText(
						ApplicationManager.getTranslation("table.delete_this_configuration", this.resourcesFile));
				b.setActionCommand(token);

				b.addActionListener(this.orderFilterSetupDeleteItemsListener);
				final JPanel p = new AuxPanel(b, menu);

				this.menuOrderFilterSetup.add(p);

			}

			if (tokens.size() > 0) {
				this.menuOrderFilterSetup.addSeparator();
			}
		}

		this.menuOrderFilterSetup.add(this.menuSaveDefaultOrderConfiguration);
		this.menuOrderFilterSetup.add(this.menuSave);

		this.menuOrderFilterSetup.show((Component) e.getSource(), 0, ((Component) e.getSource()).getHeight() + 1);
	}

	protected void configurePivotDynamicFilterOrder(final ActionEvent e) {

	}

	protected void showShareFilterTableList(final List tokens) {

		this.obtainShareElementMessageItemListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final int shareId = Integer.parseInt(e.getActionCommand());
					final int sessionID = Table.this.locator.getSessionId();
					final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
							.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
					final String message = remoteReference.getSharedElementMessage(shareId, sessionID);
					if (!message.isEmpty()) {

						Toast.showMessage(Table.this, message, null, 1500);

					} else {
						JOptionPane.showMessageDialog(Table.this,
								ApplicationManager.getTranslation("shareRemote.message_empty"),
								ApplicationManager.getTranslation("shareRemote.message_dialog"),
								JOptionPane.INFORMATION_MESSAGE);
					}
				} catch (final Exception ex) {
					Table.logger.error(ApplicationManager.getTranslation("shareRemote.not_retrive_message"), ex);
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
							"shareRemote.not_retrive_message");
				}
			}
		};

		this.stopSharingElementItemListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				final Window w = SwingUtils.getWindowAncestor((Component) e.getSource());

				if (MessageDialog.showQuestionMessage(w,
						ApplicationManager.getTranslation(Table.M_WOULD_YOU_LIKE_TO_DELETE_THIS_SHARE_FILTER))) {
					try {
						final int shareId = Integer.parseInt(e.getActionCommand());
						final int sessionID = Table.this.locator.getSessionId();
						final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
								.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
						final SharedElement sharedItem = remoteReference.getSharedItem(shareId, sessionID);
						final Application ap = ApplicationManager.getApplication();
						final ApplicationPreferences prefs = ap.getPreferences();

						if (prefs != null) {

							final String confName = sharedItem.getName();

							prefs.setPreference(Table.this.getUser(),
									Table.this.getFilterOrderConfPreferenceKey(sharedItem.getName()),
									sharedItem.getContentShare());

							final String sKey = Table.this.getFilterOrderConfigurationPreferenceKey();
							final String pref = prefs.getPreference(Table.this.getUser(), sKey);

							if (pref != null) {
								final List tokens = ApplicationManager.getTokensAt(pref, ";");
								if (!tokens.contains(confName)) {
									tokens.add(confName);
									final String sNew = ApplicationManager.vectorToStringSeparateBySemicolon(tokens);
									prefs.setPreference(Table.this.getUser(), sKey, sNew);
								}
							} else {
								final String sNew = confName;
								prefs.setPreference(Table.this.getUser(), sKey, sNew);
							}
							prefs.savePreferences();
						}
						remoteReference.deleteSharedItem(shareId, sessionID);
					} catch (final Exception ex) {
						Table.logger.error(ApplicationManager.getTranslation("shareRemote.not_retrive_message"), ex);
						MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
								"shareRemote.error_deleting_shared_element");
					}
				}
			}
		};

		this.editSharingElementItemListenes = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				try {
					final Point p = ((Component) e.getSource()).getLocationOnScreen();
					// SwingUtilities.getWindowAncestor(((Component)
					// e.getSource())).setVisible(false);
					final int shareId = Integer.parseInt(e.getActionCommand());
					final int sessionID = Table.this.locator.getSessionId();
					final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
							.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
					final SharedElement sharedItem = remoteReference.getSharedItem(shareId, sessionID);
					String filterContent = null;
					final int i = JOptionPane.showConfirmDialog(Table.this, ApplicationManager
							.getTranslation(Table.SAVE_WIDTH_POSITION_COLUMNS, Table.this.resourcesFile));
					if (i == JOptionPane.YES_OPTION) {
						filterContent = Table.this.getValueFilterOrderConfiguration(true);
					} else if (i == JOptionPane.NO_OPTION) {
						filterContent = Table.this.getValueFilterOrderConfiguration(false);
					} else {
						return;
					}

					final FormUpdateSharedReference f = new FormUpdateSharedReference(
							SwingUtilities.getWindowAncestor(Table.this), true, Table.this.locator, p, sharedItem);
					if (f.getUpdateStatus()) {
						final String nameUpdate = f.getName();
						final String contentShareUpdate = filterContent;
						final String messageUpdate = (String) f.getMessage();

						remoteReference.updateSharedItem(shareId, contentShareUpdate, messageUpdate, nameUpdate,
								sessionID);
					}
				} catch (final Exception ex) {
					Table.logger.error(ApplicationManager.getTranslation("shareRemote.not_retrive_message"), ex);
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
							"shareRemote.not_retrive_message");
				}

			}
		};

		this.addTargetToSharedElementItemListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				try {
					final int shareId = Integer.parseInt(e.getActionCommand());
					final int sessionID = Table.this.locator.getSessionId();
					final Point p = ((Component) e.getSource()).getLocationOnScreen();
					// SwingUtilities.getWindowAncestor(((Component)
					// e.getSource())).setVisible(false);
					final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
							.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
					final ListDataField listDataField = Table.this.createAndConfigureTargetUser();
					final List<String> oldTargetList = remoteReference.getTargetSharedItemsList(shareId, sessionID);
					listDataField.setValue(new Vector<String>(oldTargetList));
					final Window w = SwingUtils.getWindowAncestor((Component) e.getSource());
					final FormAddUserSharedReference f = new FormAddUserSharedReference(w, true, Table.this.locator,
							listDataField);
					f.setLocation(p);
					f.setVisible(true);

					if (f.getUpdateStatus()) {
						final List<String> targetList = new ArrayList<String>();
						if (listDataField.getValue() != null) {
							for (final Object oActual : (List) listDataField.getValue()) {
								targetList.add(oActual.toString());
							}
						}
						remoteReference.editTargetSharedElement(shareId, targetList, sessionID);
					}

				} catch (final Exception ex) {
					Table.logger.error(ApplicationManager.getTranslation("shareRemote.error_adding_target_user"), ex);
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
							"shareRemote.error_adding_target_user");
				}

			}
		};

		this.loadFilterSharedElementItemListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final int shareId = Integer.parseInt(e.getActionCommand());
					final int sessionID = Table.this.locator.getSessionId();
					final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
							.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
					final SharedElement sharedItem = remoteReference.getSharedItem(shareId, sessionID);
					Table.this.loadShareFilterOrderConfiguration(sharedItem);
				} catch (final Exception ex) {
					Table.logger.error(ApplicationManager.getTranslation("shareRemote.error_loading_shared_filter"),
							ex);
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
							"shareRemote.error_loading_shared_filter");
				}

			}

		};

		this.removeTargetSharedElementItemListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					final int shareId = Integer.parseInt(e.getActionCommand());
					final int sessionID = Table.this.locator.getSessionId();
					final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) Table.this.locator)
							.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
					final EntityResult erToret = remoteReference.deleteTargetSharedItem(shareId, sessionID);
					if (erToret.getCode() == EntityResult.OPERATION_WRONG) {
						Table.logger.error("{}", ApplicationManager.getTranslation("shareRemote.not_delete_target"),
								erToret.getMessage());
						MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
								"shareRemote.not_delete_target");
					}
				} catch (final Exception ex) {
					Table.logger.error(ApplicationManager.getTranslation("shareRemote.not_delete_target"), ex);
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor(Table.this),
							"shareRemote.not_delete_target");
				}
			}
		};

		try {
			final EntityReferenceLocator loc = ApplicationManager.getApplication().getReferenceLocator();
			final IShareRemoteReference shareReference = (IShareRemoteReference) ((UtilReferenceLocator) loc)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, loc.getSessionId());

			for (int i = 0; i < tokens.size(); i++) {

				if (i == 0) {
					this.menuOrderFilterSetup.addSeparator();
				}

				final String token = (String) tokens.get(i);
				this.showNonSharedElement(token);

				if ((i + 1) == tokens.size()) {
					this.menuOrderFilterSetup.addSeparator();
				}

			}

			// LIST SHARED BY ME
			this.showSharedElement();
			// LIST SHARED WITH ME
			this.showSharedElementUserTarget();

		} catch (final Exception e) {
			if (Table.logger.isDebugEnabled()) {
				Table.logger.debug(null, e);
			} else {
				Table.logger.info("Preference sharing system not available");
			}
		}
	}

	protected void showSharedElement() {
		try {

			final String shareKey = this.getFilterOrderConfPreferenceKey(null);
			final String username = ((ClientReferenceLocator) Table.this.locator).getUser();
			final int sessionID = Table.this.locator.getSessionId();

			final IShareRemoteReference remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) this.locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);
			final List<HashMap<String, Object>> result = remoteReference.getSourceSharedElementMenuList(username, shareKey,
					sessionID);

			// if (!result.isEmpty()){
			// this.menuOrderFilterSetup.addSeparator();
			// }

			for (final Map<String, Object> actualPreference : result) {

				final int shareKeyId = (Integer) actualPreference.get(IShareRemoteReference.SHARE_KEY_STRING);
				final String filterName = (String) actualPreference.get(IShareRemoteReference.SHARE_NAME_STRING);

				final JButton menu = new RolloverButton(filterName) {

					@Override
					public Dimension getPreferredSize() {
						final Dimension d = super.getPreferredSize();
						d.height = 22;
						return d;
					}

					@Override
					public boolean isFocusTraversable() {
						return false;
					}

					@Override
					public boolean isRequestFocusEnabled() {
						return false;
					}
				};

				menu.setActionCommand(Integer.toString(shareKeyId));
				menu.addActionListener(this.loadFilterSharedElementItemListener);
				menu.setForeground(Color.blue.darker());
				menu.setIcon(ImageManager.getIcon(ImageManager.EMPTY_16));

				final TableButton b1 = new TableButton();
				b1.setIcon(ImageManager.getIcon(ImageManager.INFO_16));
				b1.setToolTipText(ApplicationManager.getTranslation("shareRemote.obtain_message", this.resourcesFile));
				b1.setActionCommand(Integer.toString(shareKeyId));
				b1.addActionListener(this.obtainShareElementMessageItemListener);

				final TableButton b2 = new TableButton();
				b2.setIcon(ImageManager.getIcon(ImageManager.EDIT));
				b2.setToolTipText(
						ApplicationManager.getTranslation("shareRemote.edit_share_element", this.resourcesFile));
				b2.setActionCommand(Integer.toString(shareKeyId));
				b2.addActionListener(this.editSharingElementItemListenes);

				final TableButton b3 = new TableButton();
				b3.setIcon(ImageManager.getIcon(ImageManager.DATA_SHARED_DELETE));
				b3.setToolTipText(
						ApplicationManager.getTranslation("shareRemote.delete_share_element", this.resourcesFile));
				b3.setActionCommand(Integer.toString(shareKeyId));
				b3.addActionListener(this.stopSharingElementItemListener);

				final TableButton bI = new TableButton();
				bI.setIcon(ImageManager.getIcon(ImageManager.USERS_EDIT));
				bI.setToolTipText(
						ApplicationManager.getTranslation("shareRemote.add_target_element", this.resourcesFile));
				bI.setActionCommand(Integer.toString(shareKeyId));
				bI.addActionListener(this.addTargetToSharedElementItemListener);

				final JPanel p = new AuxPanel(b1, b2, b3, bI, menu);
				this.menuOrderFilterSetup.add(p);

			}

			if (!result.isEmpty()) {
				this.menuOrderFilterSetup.addSeparator();
			}

		} catch (final Exception e) {
			Table.logger.error("shareRemote.cannot_obtain_shared_element", e);
		}

	}

	protected void showSharedElementUserTarget() {

		try {
			if (this.locator == null) {
				this.locator = ApplicationManager.getApplication().getReferenceLocator();
			}
			final String shareKey = this.getFilterOrderConfPreferenceKey(null);
			final String username = ((ClientReferenceLocator) Table.this.locator).getUser();
			final int sessionID = Table.this.locator.getSessionId();

			IShareRemoteReference remoteReference;
			remoteReference = (IShareRemoteReference) ((UtilReferenceLocator) this.locator)
					.getRemoteReference(IShareRemoteReference.REMOTE_NAME, sessionID);

			final List<HashMap<String, Object>> result = remoteReference.getTargetSharedElementMenuList(username, shareKey,
					sessionID);

			for (final Map<String, Object> actualPreference : result) {

				final String filterName = (String) actualPreference.get(IShareRemoteReference.SHARE_NAME_STRING);

				final JButton menu = new RolloverButton(filterName) {

					@Override
					public Dimension getPreferredSize() {
						final Dimension d = super.getPreferredSize();
						d.height = 22;
						return d;
					}

					@Override
					public boolean isFocusTraversable() {
						return false;
					}

					@Override
					public boolean isRequestFocusEnabled() {
						return false;
					}
				};

				final int shareKeyId = (Integer) actualPreference.get(IShareRemoteReference.SHARE_KEY_STRING);
				final int shareTargetKey = (Integer) actualPreference.get(IShareRemoteReference.SHARE_TARGET_KEY_STRING);

				menu.setActionCommand(Integer.toString(shareKeyId));
				menu.addActionListener(this.loadFilterSharedElementItemListener);
				menu.setForeground(Color.blue.darker());
				menu.setIcon(ImageManager.getIcon(ImageManager.EMPTY_16));

				final TableButton b1 = new TableButton();
				b1.setIcon(ImageManager.getIcon(ImageManager.INFO_16));
				b1.setToolTipText(ApplicationManager.getTranslation("shareRemote.obtain_message", this.resourcesFile));
				b1.setActionCommand(Integer.toString(shareKeyId));
				b1.addActionListener(this.obtainShareElementMessageItemListener);

				final TableButton b2 = new TableButton();
				b2.setIcon(ImageManager.getIcon(ImageManager.DATA_SHARED_DELETE));
				b2.setToolTipText(ApplicationManager.getTranslation("shareRemote.delete_target_share_element",
						this.resourcesFile));
				b2.setActionCommand(Integer.toString(shareTargetKey));
				b2.addActionListener(this.removeTargetSharedElementItemListener);

				final JPanel p = new AuxPanel(b1, b2, menu);
				this.menuOrderFilterSetup.add(p);

			}

			if (!result.isEmpty()) {
				this.menuOrderFilterSetup.addSeparator();
			}

		} catch (final Exception e) {
			Table.logger.error("Error obtaining elements shared with the user.", e);
		}

	}

	protected ListDataField createAndConfigureTargetUser() throws Exception {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IShareRemoteReference.SHARE_USER_TARGET_STRING);
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation(IShareRemoteReference.SHARE_USER_TARGET_STRING));
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.DIM, "text");
		h.put(DataField.EXPAND, "yes");
		h.put("rows", "5");
		final ListDataField listDataField = new ListDataField(h);
		return listDataField;
	}

	protected void showNonSharedElement(final String token) {
		final JButton menu = new RolloverButton(token) {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.height = 22;
				return d;
			}

			@Override
			public boolean isFocusTraversable() {
				return false;
			}

			@Override
			public boolean isRequestFocusEnabled() {
				return false;
			}
		};
		menu.setActionCommand(token);
		menu.addActionListener(this.sortFilterOrderItemListener);
		menu.setForeground(Color.blue.darker());
		menu.setIcon(ImageManager.getIcon(ImageManager.EMPTY_16));

		final TableButton b1 = new TableButton();
		b1.setIcon(ImageManager.getIcon(ImageManager.DATA_SHARE_ACTION));
		b1.setToolTipText(ApplicationManager.getTranslation("shareRemote.share_filter", this.resourcesFile));
		b1.setActionCommand(token);
		b1.addActionListener(this.addShareFilterSetupItemsListener);

		final TableButton b2 = new TableButton();
		b2.setIcon(ImageManager.getIcon(ImageManager.RECYCLER));
		b2.setToolTipText(ApplicationManager.getTranslation("table.delete_this_configuration", this.resourcesFile));
		b2.setActionCommand(token);
		b2.addActionListener(this.orderFilterSetupDeleteItemsListener);

		final JPanel p = new AuxPanel(b1, b2, menu);
		this.menuOrderFilterSetup.add(p);

	}

	/**
	 * Loads the configuration which name is passed as parameter. If the filter configuration cannot be
	 * found, loads the default one, if exists.
	 * @param filterName
	 */
	public void loadFilterOrderConfiguration(final String filterName) {
		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();

		if (prefs == null) {
			return;
		}
		final String sf = prefs.getPreference(this.getUser(), this.getFilterOrderConfPreferenceKey(filterName));
		if (sf != null) {
			final List cols = ApplicationManager.getTokensAt(sf, ";");
			if (cols.size() >= 2) {
				final String col = (String) cols.get(0);
				final String asc = (String) cols.get(1);
				try {
					if (!"null".equals(col)) {
						// Search if there are multiple columns

						if (col.indexOf(":") >= 0) {
							final List otherCols = ApplicationManager.getTokensAt(col, ":");
							final List ascends = ApplicationManager.getTokensAt(asc, ":");
							if (otherCols.size() != ascends.size()) {
								Table.logger.info("Preference Error {} -> {}  different size from {}", sf, otherCols,
										ascends);
							} else {
								this.resetOrder();
								for (int i = 0; i < otherCols.size(); i++) {
									final String c = (String) otherCols.get(i);
									final String a = (String) ascends.get(i);
									final boolean ascb = ApplicationManager.parseStringValue(a, false);
									this.sortByWithoutReset(c, ascb);
								}
							}
						} else {
							final boolean ascb = ApplicationManager.parseStringValue(asc, false);
							this.sortBy(col, ascb);
						}
					} else {
						this.resetOrder();
					}
					if (cols.size() >= 3) {
						((TableSorter) this.table.getModel()).resetGroup();
						// Check if it is groupping or filter
						String f = (String) cols.get(2);
						int index = 3;
						if (!f.startsWith("BASE64")) {
							// Load position and columns width
							if (!"null".equals(f)) {
								this.applyColumnPositonAndPreferences(f);
								if (filterName == null) {
									this.prefWidthAndPosApply = true;
								}
							}
							f = (String) cols.get(3);
							index = 4;
						}
						if (cols.size() > index) {
							for (int i = index; i < cols.size(); i++) {
								f = f + ";" + cols.get(i);
							}
						}
						byte[] bytes = null;
						if (f.startsWith("BASE64")) {
							f = f.substring("BASE64".length());
						}

						bytes = com.ontimize.jee.common.util.Base64Utils.decode(f.toCharArray());
						final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
						final ObjectInputStream in = new ObjectInputStream(bIn);
						final Object o = in.readObject();
						if (o instanceof Map) {
							// To avoid serialization problems
							final Map hNews = new Hashtable();
							final Map g = (Map) o;
							final Enumeration enumKeys = Collections.enumeration(g.keySet());
							while (enumKeys.hasMoreElements()) {
								final Object oKey = enumKeys.nextElement();
								final Object oValue = g.get(oKey);
								if (oValue instanceof TableSorter.Filter) {
									final TableSorter.Filter v = (TableSorter.Filter) oValue;
									final Object[] oValues = v.values;
									for (int i = 0; i < oValues.length; i++) {
										if (oValues[i] instanceof FilterDate) {
											oValues[i] = new java.util.Date(((FilterDate) oValues[i]).longValue());
										}
									}
								}
								hNews.put(oKey, oValue);
							}
							this.applyFilter(hNews);
						}
					}
				} catch (final Exception e) {
					Table.logger.error(null, e);
					prefs.setPreference(this.getUser(), this.getFilterOrderConfPreferenceKey(filterName), null);
				}
			}
		}
	}

	public void loadShareFilterOrderConfiguration(final SharedElement shareElement) {

		final String sf = shareElement.getContentShare();
		final String filterName = shareElement.getName();

		final List cols = ApplicationManager.getTokensAt(sf, ";");
		if (cols.size() >= 2) {
			final String col = (String) cols.get(0);
			final String asc = (String) cols.get(1);
			try {
				if (!"null".equals(col)) {
					// Search if there are multiple columns

					if (col.indexOf(":") >= 0) {
						final List otherCols = ApplicationManager.getTokensAt(col, ":");
						final List ascends = ApplicationManager.getTokensAt(asc, ":");
						if (otherCols.size() != ascends.size()) {
							Table.logger.info("Preference Error {} -> {}  different size from {}", sf, otherCols,
									ascends);
						} else {
							this.resetOrder();
							for (int i = 0; i < otherCols.size(); i++) {
								final String c = (String) otherCols.get(i);
								final String a = (String) ascends.get(i);
								final boolean ascb = ApplicationManager.parseStringValue(a, false);
								this.sortByWithoutReset(c, ascb);
							}
						}
					} else {
						final boolean ascb = ApplicationManager.parseStringValue(asc, false);
						this.sortBy(col, ascb);
					}
				} else {
					this.resetOrder();
				}
				if (cols.size() >= 3) {
					((TableSorter) this.table.getModel()).resetGroup();
					// Check if it is groupping or filter
					String f = (String) cols.get(2);
					int index = 3;
					if (!f.startsWith("BASE64")) {
						// Load position and columns width
						if (!"null".equals(f)) {
							this.applyColumnPositonAndPreferences(f);
							if (filterName == null) {
								this.prefWidthAndPosApply = true;
							}
						}
						f = (String) cols.get(3);
						index = 4;
					}
					if (cols.size() > index) {
						for (int i = index; i < cols.size(); i++) {
							f = f + ";" + cols.get(i);
						}
					}
					byte[] bytes = null;
					if (f.startsWith("BASE64")) {
						f = f.substring("BASE64".length());
					}

					bytes = com.ontimize.jee.common.util.Base64Utils.decode(f.toCharArray());
					final ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);
					final ObjectInputStream in = new ObjectInputStream(bIn);
					final Object o = in.readObject();
					if (o instanceof Map) {
						// To avoid serialization problems
						final Map hNews = new Hashtable();
						final Map g = (Map) o;
						final Enumeration enumKeys = Collections.enumeration(g.keySet());
						while (enumKeys.hasMoreElements()) {
							final Object oKey = enumKeys.nextElement();
							final Object oValue = g.get(oKey);
							if (oValue instanceof TableSorter.Filter) {
								final TableSorter.Filter v = (TableSorter.Filter) oValue;
								final Object[] oValues = v.values;
								for (int i = 0; i < oValues.length; i++) {
									if (oValues[i] instanceof FilterDate) {
										oValues[i] = new java.util.Date(((FilterDate) oValues[i]).longValue());
									}
								}
							}
							hNews.put(oKey, oValue);
						}
						this.applyFilter(hNews);
					}
				}
			} catch (final Exception e) {
				Table.logger.error(null, e);
				// prefs.setPreference(this.getUser(),
				// this.getFilterOrderConfPreferenceKey(filterName), null);
			}
		}

	}

	public String getFilterOrderConfigurationValue(final String filterName) {

		String toRet = null;

		final Application ap = ApplicationManager.getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();

		if (prefs == null) {
			return toRet;
		}

		final String sf = prefs.getPreference(this.getUser(), this.getFilterOrderConfPreferenceKey(filterName));
		if (sf != null) {
			toRet = sf;
		}

		return toRet;
	}

	protected EditorManager cellEditorManager = null;

	/**
	 * Sets an {@link EditorManager} to the present <code>Table</code>.
	 * @param manager
	 */
	public void setEditorManager(final EditorManager manager) {
		this.cellEditorManager = manager;
	}

	/**
	 * Returns the {@link EditorManager} configured in the present table.
	 * @return
	 */
	public EditorManager getEditorManager() {
		return this.cellEditorManager;
	}

	protected RendererManager cellRendererManager = null;

	/**
	 * Sets the RendererManager, which provides the renderes depending on the object type that must be
	 * renderer.
	 * @param manager
	 * @see RendererManager
	 */
	public void setRendererManager(final RendererManager manager) {
		this.cellRendererManager = manager;
	}

	/**
	 * Returns the {@link RendererManager} configured for this table.
	 * @return
	 */
	public RendererManager getRendererManager() {
		return this.cellRendererManager;
	}

	/**
	 * Establishes the initial parameters for the {@link TableCellEditor} components
	 * <p>
	 * The method must be called to prepare all editors implementing <lu>
	 * <li>{@link ReferenceComponent}</li>
	 * <li>{@link Internationalization}</li>
	 * <li>{@link AccessForm}</li>
	 * <li>{@link CachedComponent}</li> </lu>
	 * @param editor
	 */
	public void prepareEditor(final TableCellEditor editor) {
		if (editor instanceof ReferenceComponent) {
			final EntityReferenceLocator locator = this.parentForm.getFormManager().getReferenceLocator();
			((ReferenceComponent) editor).setReferenceLocator(locator);
		}
		if (editor instanceof Internationalization) {
			((Internationalization) editor).setComponentLocale(this.locale);
			((Internationalization) editor).setResourceBundle(this.resourcesFile);
		}
		if (editor instanceof AccessForm) {
			((AccessForm) editor).setParentForm(this.parentForm);
		}
		if (editor instanceof CachedComponent) {
			if (this.parentForm != null) {
				final EntityReferenceLocator locator = this.parentForm.getFormManager().getReferenceLocator();
				if (locator == null) {
					Table.logger.warn("Cannot set CacheManager to the editor {},  because locator is NULL",
							editor.getClass());
				} else {
					CacheManager.getDefaultCacheManager(locator).addCachedComponent((CachedComponent) editor);
					((CachedComponent) editor).setCacheManager(CacheManager.getDefaultCacheManager(locator));
				}
			} else {
				Table.logger.warn("Cannot set CacheManager to the editor {}, because parent form is NULL",
						editor.getClass());
			}
		}
	}

	/**
	 * Establishes the initial parameters for the {@link TableCellRenderer} components
	 * <p>
	 * The method must be called to prepare all renderes implementing <lu>
	 * <li>{@link CellRenderer}</li>
	 * <li>{@link ReferenceComponent}</li>
	 * <li>{@link Internationalization}</li>
	 * <li>{@link AccessForm}</li>
	 * <li>{@link CachedComponent}</li> </lu>
	 * @param renderer
	 */
	public void prepareRenderer(final TableCellRenderer renderer) {
		if (renderer instanceof CellRenderer) {
			((CellRenderer) renderer).setCellRendererColorManager(this.cellRendererColorManager);
			((CellRenderer) renderer).setCellRendererFontManager(this.cellRendererFontManager);
		}

		if (renderer instanceof ReferenceComponent) {
			final EntityReferenceLocator buscador = this.parentForm.getFormManager().getReferenceLocator();
			((ReferenceComponent) renderer).setReferenceLocator(buscador);
		}
		if (renderer instanceof Internationalization) {
			((Internationalization) renderer).setComponentLocale(this.locale);
			((Internationalization) renderer).setResourceBundle(this.resourcesFile);
		}
		if (renderer instanceof AccessForm) {
			((AccessForm) renderer).setParentForm(this.parentForm);
		}
		if (renderer instanceof CachedComponent) {
			if (this.parentForm != null) {
				final EntityReferenceLocator buscador = this.parentForm.getFormManager().getReferenceLocator();
				if (buscador == null) {
					Table.logger.debug("Cannot set CacheManager to the editor " + renderer.getClass()
					+ ", because locator is NULL");
				} else {
					CacheManager.getDefaultCacheManager(buscador).addCachedComponent((CachedComponent) renderer);
					((CachedComponent) renderer).setCacheManager(CacheManager.getDefaultCacheManager(buscador));
				}
			} else {
				Table.logger.debug("Cannot set CacheManager to the editor " + renderer.getClass()
				+ ", because parent form is NULL");
			}
		}

	}

	/**
	 * Returns true if the table detail form is created.
	 * @return true if the table detail form is created
	 */
	public boolean isDetailFormCreated() {
		return this.detailForm != null;
	}

	/**
	 * Sets the default filter to the table. The default filter can be configured in the preferences.
	 */
	protected void setDefaultFilter() {
		if (this.defaultFilter instanceof Map) {
			this.applyFilter((Map) this.defaultFilter);
		}
	}

	/**
	 * Installs the button that manages the calculated columns configuration in the table.
	 */
	protected void installCalculedColsButton() {
		if (Table.createCalculatedButton && (MathExpressionParserFactory.getInstance() != null)) {
			this.buttonCalculatedColumns = new TableButton();
			if (this.buttonCalculatedColumns instanceof TableComponent) {
				((TableComponent) this.buttonCalculatedColumns).setKey(Table.BUTTON_CALCULATED_COL);
			}

			final Icon icon = ImageManager.getIcon(ImageManager.TABLE_CALCULATEDCOLS);
			if (icon != null) {
				this.buttonCalculatedColumns.setIcon(icon);
			} else {
				this.buttonCalculatedColumns.setText("CalculatedCols");
			}
			this.buttonCalculatedColumns.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.showCalculatorWindow(e.getSource());
				}
			});
			this.addComponentToControls(this.buttonCalculatedColumns);
		}
	}

	/**
	 * Shows the calculator window
	 * @param source
	 */
	protected void showCalculatorWindow(final Object source) {
		// ColumnCalculatorWindow.showCalculatorWindow((Component) source,
		// this);
		CalculatedColumnDialog.showCalculatorWindow((Component) source, this);
	}

	/**
	 * Installs the Report Button
	 */
	protected void installReportButton() {
		if (!com.ontimize.report.ReportManager.isReportsEnabled()) {
			return;
		}
		this.buttonReports = new TableButtonSelection();
		this.buttonReports.setKey(Table.BUTTON_REPORT);
		final ImageIcon pageIcon = ImageManager.getIcon(ImageManager.TABLE_REPORTS);
		if (pageIcon != null) {
			this.buttonReports.setIcon(pageIcon);
		} else {
			this.buttonReports.setText("Reports");
		}
		this.buttonReports.setEnabled(false);
		this.buttonReports.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonReports);
		this.buttonReports.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Cursor oldCursor = Table.this.buttonReports.getCursor();
				try {
					Table.this.buttonReports.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Table.this.showCustomReportsWindow();
				} finally {
					Table.this.buttonReports.setCursor(oldCursor);
				}
			}
		});

		if (this.dynamicTable) {
			this.buttonReports.getPreferredSize().width = this.buttonReports.getPreferredSize().width
					- this.buttonReports.getMenuButton().getPreferredSize().width;
			this.buttonReports.getMenuButton().setVisible(false);
		} else {
			this.buttonReports.addActionMenuListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					Table.this.createReportSetupMenu();

					Table.this.menuReportSetup.show(Table.this.buttonReports, 0,
							Table.this.buttonReports.getHeight());
				}

			});
		}
	}

	/**
	 * Returns the configured reports for this table stored in the application preferences.
	 * @return
	 */
	protected java.util.List getConfigurationReport() {
		final List arrayList = new ArrayList();
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getCustomReportPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						final int index = token.indexOf(":");
						if (index > 0) {
							final String sName = token.substring(0, index);
							arrayList.add(sName);
						}
					}
				}
			}
		} catch (final Exception e) {
			Table.logger.error(null, e);
		}
		return arrayList;
	}

	/**
	 * Class that implement an action listener that shows the custom report window with the selected
	 * report. The selected report corresponds to the comand of the action.
	 */
	protected class ListenerItem implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof AbstractButton) {
				final String command = ((AbstractButton) o).getActionCommand();
				Table.this.showCustomReportsWindow(command);
			}
			Table.this.menuReportSetup.setVisible(false);
		}

	}

	/**
	 * Class that implement an action listener that deletes the selected report. The selected report
	 * corresponds to the command of the action.
	 */

	protected class DeleteItemListener implements ActionListener {

		protected String DELETE_KEY = "REPORT_DELETE_KEY";

		protected ResourceBundle bundle = null;

		public DeleteItemListener(final ResourceBundle resource) {
			this.bundle = resource;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			final Object o = e.getSource();
			if (o instanceof AbstractButton) {
				final int i = JOptionPane.showConfirmDialog((Component) o,
						ApplicationManager.getTranslation(this.DELETE_KEY, this.bundle), "", JOptionPane.YES_NO_OPTION);
				if (i == JOptionPane.OK_OPTION) {
					final String command = ((AbstractButton) o).getActionCommand();
					Table.this.deleteConfigurationReport(command);
				}
			}
			Table.this.menuReportSetup.setVisible(false);
		}

		public void setResourceBundle(final ResourceBundle bundle) {
			this.bundle = bundle;
		}

	}

	protected ListenerItem listener = null;

	private void deleteConfigurationReport(final String conf) {
		try {
			final Application ap = this.parentForm.getFormManager().getApplication();
			final String preferenceKey = this.getCustomReportPreferenceKey();
			final ApplicationPreferences prefs = ap.getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(this.getUser(), preferenceKey);
				String pout = "";
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						final int index = token.indexOf(":");
						if (index > 0) {
							final String sName = token.substring(0, index);
							if (!sName.equalsIgnoreCase(conf)) {
								pout += pout.length() == 0 ? token : ";" + token;
							}
						}
					}
					prefs.setPreference(this.getUser(), preferenceKey, pout);
					prefs.savePreferences();
				}
			}
		} catch (final Exception e) {
			Table.logger.error(null, e);
		}
	}

	protected DeleteItemListener deleteListener = null;

	/**
	 * Creates the report setup menu, and sets it to the menuReportSetup variable.
	 */
	protected void createReportSetupMenu() {
		if (this.menuReportSetup == null) {
			this.menuReportSetup = new JPopupMenu();
			this.buttonReports.setMenu(this.menuReportSetup);
			this.listener = new ListenerItem();
			this.deleteListener = new DeleteItemListener(this.resourcesFile);
		}

		final java.util.List list = this.getConfigurationReport();
		final int originalSize = list.size();

		for (int i = this.menuReportSetup.getComponentCount() - 1; i >= 0; i--) {
			final Object o = this.menuReportSetup.getComponent(i);
			if (o instanceof JPanel) {
				final JPanel jPanel = (JPanel) o;
				final JButton item = (JButton) jPanel.getComponent(0);
				final String sKey = item.getActionCommand();
				if (!list.contains(sKey)) {
					this.menuReportSetup.remove(i);
				} else {
					list.remove(sKey);
				}
			} else {
				this.menuReportSetup.remove(i);
			}
		}

		if (originalSize != 0) {
			for (int i = 0; i < list.size(); i++) {
				final JPanel panel = new JPanel(new GridBagLayout());
				final JButton item = new MenuButton((String) list.get(i));
				item.addActionListener(this.listener);
				panel.add(item, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
				final ImageIcon icon = ImageManager.getIcon(ImageManager.RECYCLER);
				item.setMargin(new Insets(0, 0, 0, 0));
				final JButton delete = new MenuButton(icon);
				delete.setActionCommand((String) list.get(i));
				delete.addActionListener(this.deleteListener);
				delete.setMargin(new Insets(0, 0, 0, 0));
				panel.add(delete,
						new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 0, 0, GridBagConstraints.EAST,
								GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
				this.menuReportSetup.add(panel);
			}
		} else {
			final JLabel label = new JLabel(
					ApplicationManager.getTranslation("table.no_stored_report_templates", this.resourcesFile));
			this.menuReportSetup.add(label);
		}
	}

	protected static class FilterDate implements Serializable {

		public static final long serialVersionUID = 1509416594978732908L;

		long l = -1;

		static transient DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);

		public FilterDate(final long l) {
			this.l = l;
		}

		public long longValue() {
			return this.l;
		}

		@Override
		public String toString() {
			return FilterDate.dateFormat.format(new java.util.Date(this.l));
		}

	}

	/**
	 * Creates the preference according to the current table configuration.
	 * @return the String that defines this preference
	 */
	protected String createColumnPositionAndWidthPreference() {
		final List cols = this.getVisibleColumns();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String n = this.table.getColumnName(i);
			if (cols.contains(n)) {
				final TableColumn tc = this.table.getColumn(n);
				sb.append(n);
				sb.append("=" + tc.getWidth() + ":" + i);
				tc.setPreferredWidth(tc.getWidth());
				sb.append("|");
			}
		}
		return sb.toString();
	}

	/**
	 * Saves the columns color and width as table preference.
	 */
	public void saveColumnsPositionAndWith() {
		final Application ap = this.parentForm.getFormManager().getApplication();
		if (ap.getPreferences() != null) {
			ap.getPreferences()
			.setPreference(this.getUser(), this.getColumnsPosAndOrderPreferenceKey(),
					this.getColumnsPositionAndWith());
			ap.getPreferences().savePreferences();
		}
	}

	protected String getColumnsPositionAndWith() {
		final List cols = this.getVisibleColumns();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.table.getColumnCount(); i++) {
			final String n = this.table.getColumnName(i);
			if (cols.contains(n)) {
				final TableColumn tc = this.table.getColumn(n);
				sb.append(n);
				sb.append("=" + tc.getWidth() + ":" + i);
				tc.setPreferredWidth(tc.getWidth());
				sb.append(";");
			}
		}
		return sb.toString();
	}

	/**
	 * Saves the operations passed as parameter in the preferences. The parameter keys must be column
	 * names and the values, the operations described in the <code>Table</code>.
	 * @param operations contains the operations to store
	 */
	public void saveOperations(final Map operations) {
		final TableModel model = this.table.getModel();
		if ((model != null) && (model instanceof TableSorter)) {
			final Enumeration enu = Collections.enumeration(operations.keySet());
			final StringBuilder sb = new StringBuilder();
			while (enu.hasMoreElements()) {
				final String nameColumn = (String) enu.nextElement();
				sb.append(nameColumn);
				final String sValue = (String) operations.get(nameColumn);
				sb.append("=" + sValue + ";");
			}
			final Application ap = this.parentForm.getFormManager().getApplication();
			if (ap.getPreferences() != null) {
				ap.getPreferences().setPreference(this.getUser(), this.getOperationPreferenceKey(), sb.toString());
				ap.getPreferences().savePreferences();
			}
		}
	}

	/**
	 * Applies the operations stored in the preferences file to the table.
	 *
	 * @see TableSorter#setOperationColumns
	 */
	public void applyOperations() {
		Map hOperations = null;
		final Application ap = this.parentForm.getFormManager().getApplication();
		final ApplicationPreferences prefs = ap.getPreferences();
		final String sf = prefs.getPreference(this.getUser(), this.getOperationPreferenceKey());
		if (sf != null) {
			hOperations = new Hashtable();
			final StringTokenizer st = new StringTokenizer(sf, ";");
			while (st.hasMoreTokens()) {
				final String t = st.nextToken();
				final int iIg = t.indexOf('=');
				if (iIg < 0) {
					continue;
				}
				final String col = t.substring(0, iIg);
				final String ope = t.substring(iIg + 1);
				hOperations.put(col, ope);
			}
			final TableModel model = this.table.getModel();
			if ((model != null) && (model instanceof TableSorter)) {
				((TableSorter) model).setOperationColumns(hOperations);
			}
		}
	}

	/**
	 * Saves the current visible controls configuration in the preferences.
	 */
	public void saveVisibleControlsConfiguration() {
		final Application ap = this.parentForm.getFormManager().getApplication();
		if (ap.getPreferences() != null) {
			ap.getPreferences()
			.setPreference(this.getUser(), this.getVisibleControlsPreferenceKey(),
					Boolean.toString(this.controlsPanel.isVisible()));
			ap.getPreferences().savePreferences();
		}
	}

	public void saveControlPanelConfiguration() {
		final Application ap = this.parentForm.getFormManager().getApplication();
		if (ap.getPreferences() != null) {
			ap.getPreferences()
			.setPreference(this.getUser(), this.getControlPanelPreferenceKey(),
					this.controlsPanel.getButtonPosition());
			ap.getPreferences().savePreferences();
		}

	}

	protected boolean prefWidthAndPosApply = false;

	/**
	 * Ensures that the {@link #setWidthAndPositionColumns()} method only is called once.
	 * @return true if all was ok, false otherwise
	 */
	protected boolean evaluateColumnsWidthAndPosition() {
		if (this.prefWidthAndPosApply) {
			return true;
		}
		this.prefWidthAndPosApply = this.setWidthAndPositionColumns();
		return this.prefWidthAndPosApply;
	}

	/**
	 * Sets the column width and position to the values stored in the preferences.
	 * @return true if all was ok, false otherwise
	 */
	protected boolean setWidthAndPositionColumns() {
		if (this.table == null) {
			return false;
		}
		if (this.parentForm == null) {
			return false;
		}
		if (this.parentForm.getFormManager() == null) {
			return false;
		}
		final Application ap = this.parentForm.getFormManager().getApplication();
		if (ap == null) {
			return false;
		}
		if (ap.getPreferences() != null) {
			final String p = ap.getPreferences().getPreference(this.getUser(), this.getColumnsPosAndOrderPreferenceKey());
			if (p == null) {
				return true;
			}
			final StringTokenizer st = new StringTokenizer(p, ";");
			while (st.hasMoreTokens()) {
				final String t = st.nextToken();
				final int iIg = t.indexOf('=');
				if (iIg < 0) {
					continue;
				}
				final int iDP = t.indexOf(':');
				if (iDP < 0) {
					continue;
				}
				final String col = t.substring(0, iIg);
				final String sWidth = t.substring(iIg + 1, iDP);
				final String pos = t.substring(iDP + 1);
				if (!this.isVisibleColumn(col)) {
					continue;
				}
				try {
					final TableColumn tc = this.table.getColumn(col);
					if (tc != null) {
						tc.setPreferredWidth(Integer.parseInt(sWidth));
						tc.setWidth(Integer.parseInt(sWidth));
						this.table.moveColumn(this.table.convertColumnIndexToView(tc.getModelIndex()),
								Integer.parseInt(pos));
					}
				} catch (final Exception e) {
					Table.logger.error(null, e);
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Applies the position and column preferences past as parameter.
	 * @param preference the preference value to set
	 */
	protected void applyColumnPositonAndPreferences(final String preference) {
		final List visibleCols = new Vector();
		if (preference == null) {
			return;
		}
		final StringTokenizer st = new StringTokenizer(preference, "|");
		while (st.hasMoreTokens()) {
			final String t = st.nextToken();
			final int iIg = t.indexOf('=');
			if (iIg < 0) {
				continue;
			}
			final int iDP = t.indexOf(':');
			if (iDP < 0) {
				continue;
			}
			final String col = t.substring(0, iIg);
			final String sWidth = t.substring(iIg + 1, iDP);
			final String pos = t.substring(iDP + 1);
			visibleCols.add(col);
			try {
				final TableColumn tc = this.table.getColumn(col);
				if (tc != null) {
					tc.setMaxWidth(Integer.MAX_VALUE);
					tc.setPreferredWidth(Integer.parseInt(sWidth));
					tc.setWidth(Integer.parseInt(sWidth));
					this.table.moveColumn(this.table.convertColumnIndexToView(tc.getModelIndex()),
							Integer.parseInt(pos));
				}
			} catch (final Exception e) {
				Table.logger.error(null, e);
			}
		}
		this.setVisibleColumns(visibleCols, false);
	}

	/**
	 * Returns the key that will be used to identify the preference that will save the operations
	 * applied to the different columns.
	 * @return the preference key
	 */
	protected String getOperationPreferenceKey() {
		final Form f = this.parentForm;
		return f != null ? Table.OPERATION_PREFERENCE + "_" + f.getArchiveName() + "_" + this.entity
				: Table.OPERATION_PREFERENCE + "_" + this.entity;
	}

	/**
	 * Returns the key that will be used to identify the preference that will save the columns position
	 * and order.
	 * @return the preference key
	 */
	protected String getColumnsPosAndOrderPreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.TABLE_COLS_POSITION_SIZE + "_" + f.getArchiveName() + "_" + this.entity
						: BasicApplicationPreferences.TABLE_COLS_POSITION_SIZE + "_" + this.entity;
	}

	/**
	 * Provides a reference to the table ScrollPane
	 * @return a refrence to the table scroll pane
	 */
	public JScrollPane getJScrollPane() {
		return this.scrollPane;
	}

	protected com.ontimize.report.ReportUtils ru = null;

	/**
	 * Returns the key that will be used to identify the preference that will save the custom reports
	 * associated to this table.
	 * @return the preference key
	 */
	protected String getCustomReportPreferenceKey() {
		if (!this.dynamicPivotable) {
			final Form f = this.parentForm;
			return f != null
					? BasicApplicationPreferences.TABLE_CONF_REPORT_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
					+ this.entity
					: BasicApplicationPreferences.TABLE_CONF_REPORT_CONFIGURATIONS + "_" + this.entity;
		} else {
			return BasicApplicationPreferences.TABLE_CONF_REPORT_CONFIGURATIONS_DYNAMIC_PIVOT_TABLE + "_"
					+ this.dynamicPivotTableForm + "_" + this.dynamicPivotTableEntity;
		}
	}

	/**
	 * Shows the custom reports windows, when the table is not empty.
	 */
	public void showCustomReportsWindow() {
		if (this.isEmpty()) {
			return;
		}
		// TODO change the values to show using the renderers if exist
		Map hData = new Hashtable();
		if (Table.renderReportValues) {
			hData = (Map) this.getValueToReport();
		} else {
			hData = (Map) this.getShownValue();
		}
		// Map hData = getValueToExport(false, false);
		Application ap = null;
		if (this.parentForm != null) {
			ap = this.parentForm.getFormManager().getApplication();
		} else {
			ap = ApplicationManager.getApplication();
		}
		if (this.ru == null) {
			this.ru = new com.ontimize.report.ReportUtils(EntityResultUtils.createTableModel(hData, this.reportCols),
					null, this.getResourceBundle(), null, this.entity,
					this.getUser(), this.getCustomReportPreferenceKey(), ap != null ? ap.getPreferences() : null);
		} else {
			this.ru.setModel(EntityResultUtils.createTableModel(hData, this.reportCols));
		}
		this.ru.setResourceBundle(this.getResourceBundle());
		final DefaultReportDialog reportDialog = this.ru.createDefaultDialog(this, this.lInfoFilter.getText());
		if (this.dynamicTable && !this.dynamicPivotable) {
			if (reportDialog.getLoadButton() != null) {
				reportDialog.getLoadButton().setVisible(false);
			}
			if (reportDialog.getSaveButton() != null) {
				reportDialog.getSaveButton().setVisible(false);
			}
		}
		this.ru.showDefaultReportDialog(reportDialog, null);
	}

	/**
	 * Shows the custom reports windows, when the table is not empty.
	 * @param configuration the report description
	 */
	public void showCustomReportsWindow(final String configuration) {
		if (this.isEmpty()) {
			return;
		}

		Map hData = new Hashtable();
		if (Table.renderReportValues) {
			hData = (Map) this.getValueToReport();
		} else {
			hData = (Map) this.getShownValue();
		}
		final List vVisible = this.getOriginallyVisibleColumns();
		final Application ap = this.parentForm.getFormManager().getApplication();
		if (this.ru == null) {
			this.ru = new com.ontimize.report.ReportUtils(EntityResultUtils.createTableModel(hData, vVisible), null,
					this.getResourceBundle(), null, this.entity, this.getUser(),
					this.getCustomReportPreferenceKey(), ap.getPreferences());
		} else {
			this.ru.setModel(EntityResultUtils.createTableModel(hData, vVisible));
		}
		this.ru.setResourceBundle(this.getResourceBundle());
		this.ru.showDefaultReportDialog(this, this.lInfoFilter.getText(), configuration);

	}

	/**
	 * Returns a String with the information of all the filters that are being applied to the table.
	 * This String will be stored in the preferences in order to recover the filter configuration later.
	 * @return an empty String when no filters set
	 */
	protected String getFilterInfo() {
		final Map hFilters = ((TableSorter) this.getJTable().getModel()).getFilters();
		final boolean lastFilterOr = ((TableSorter) this.getJTable().getModel()).lastFilterOr();
		if (hFilters.isEmpty() || lastFilterOr) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		final Enumeration enumKeys = Collections.enumeration(hFilters.keySet());
		int i = 0;
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			sb.append(ApplicationManager.getTranslation((String) oKey, this.resourcesFile));
			sb.append(" '" + hFilters.get(oKey) + "'");
			if (i < (hFilters.size() - 1)) {
				sb.append(", ");
			}
			i++;
		}
		return sb.toString();
	}

	/**
	 * Updates the text in the table that shows the current filter status,which shows the columns that
	 * are being filtered and the values that are being applied to those columns.
	 */
	protected void updateFilterInfo() {
		final String s = this.getFilterInfo();
		if ((s != null) && (s.length() > 0)) {
			this.lInfoFilter.setText(ApplicationManager.getTranslation("table.filter", this.resourcesFile) + ": " + s);
		} else {
			this.lInfoFilter.setText("");
		}
		this.lInfoFilter.setToolTipText(this.lInfoFilter.getText());
	}

	/**
	 * Returns the view index of the column specified.
	 * @param columnName
	 * @return
	 */
	public int getColumnIndex(final String columnName) {
		try {
			return ((EJTable) this.table).getColumnIndex(columnName);
		} catch (final Exception ex) {
			Table.logger.trace(null, ex);
			return -1;
		}
	}

	/**
	 * Adjusts the height of all rows to the contents inside them.
	 */
	protected void evaluatePreferredRowsHeight() {
		if ((this.table != null) && (this.table instanceof EJTable)) {
			if (((EJTable) this.table).isFitRowsHeight()) {
				((EJTable) this.table).fitRowHeight();
			}

			final int rowCount = this.table.getRowCount();
			for(int i=0;i<rowCount;i++){
				final int rowHeight = this.table.getRowHeight(i);
				this.blockedTable.setRowHeight(i, rowHeight);
			}
			// this.blockedTable.setRowHeight(17);
		}

	}

	public static final String BUTTON_PIVOTTABLE = "pivottablebutton";

	protected TableButton buttonPivotTable = null;

	protected JDialog dPivot = null;

	/**
	 * Installs the button that provides pivot functionallity in the control panel.
	 */
	protected void installPivotTableButton() {
		this.buttonPivotTable = new TableButton();
		this.buttonPivotTable.setKey(Table.BUTTON_PIVOTTABLE);

		final ImageIcon pivotIcon = ImageManager.getIcon(ImageManager.TABLE_PIVOT);
		if (pivotIcon != null) {
			this.buttonPivotTable.setIcon(pivotIcon);
		} else {
			this.buttonPivotTable.setText("Pivot");
		}
		this.buttonPivotTable.setEnabled(true);
		this.buttonPivotTable.setMargin(new Insets(0, 0, 0, 0));
		this.controlsPanel.add(this.buttonPivotTable);
		this.buttonPivotTable.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final String columPosAndWith = Table.this.getColumnsPositionAndWith();
				final Map<?, ?> rederersMap = new HashMap(Table.this.getMapRenderersForColumns());
				if ((Table.this.dPivot == null) || Table.this.dynamicTable) {
					final Window w = SwingUtilities.getWindowAncestor(Table.this);
					Table.this.dPivot = PivotTableUtils.createPivotDialog(w,
							EntityResultUtils.createTableModel((Map) Table.this.getShownValue(),
									Table.this.getOriginallyVisibleColumns()),
							Table.this.resourcesFile,
							Table.this.getDetailWindowParameters(), columPosAndWith);
					ApplicationManager.center(Table.this.dPivot);
					Table.this.setPivotTablePreferences(Table.this.dPivot);
				}
				((PivotTableUtils.PivotDialog) Table.this.dPivot)
				.setModel(EntityResultUtils.createTableModel((Map) Table.this.getShownValue(),
						Table.this.getOriginallyVisibleColumns()), true);
				((PivotTableUtils.PivotDialog) Table.this.dPivot).setOriginalColPosAndWith(columPosAndWith);
				((PivotTableUtils.PivotDialog) Table.this.dPivot).setRenderersForColumns(rederersMap);
				Table.this.dPivot.setVisible(true);
			}
		});

	}

	protected Map<?, ?> getMapRenderersForColumns() {
		final Map<String, Object> toRet = new HashMap<String, Object>();
		for (final Object s : Table.this.getTableSorter().getColumnNames()) {
			final String columnName = (String) s;
			final Object cellRenderer = Table.this.getRendererForColumn((String) s);
			if (cellRenderer != null) {
				toRet.put(columnName, cellRenderer);
			}
		}
		return toRet;

	}

	protected Map getDetailWindowParameters() {
		final Map param = new Hashtable();
		param.put("entity", "entity");
		param.put("dynamic", "yes");
		param.put("translateheader", "yes");
		// if (this.dynamicPivotable) {
		if (this.getParentForm() != null) {
			param.put(Table.D_PIVOT_TABLE_PREFERENCES_FORM, this.getParentForm().getArchiveName());
		} else {
			if (this.dynamicPivotTableForm != null) {
				param.put(Table.D_PIVOT_TABLE_PREFERENCES_FORM, this.dynamicPivotTableForm);
			}
		}

		if (this.dynamicPivotTableEntity != null) {
			param.put(Table.D_PIVOT_TABLE_PREFERENCES_ENTITY, this.dynamicPivotTableEntity);
		} else {
			param.put(Table.D_PIVOT_TABLE_PREFERENCES_ENTITY, this.getEntityName());
		}

		// }
		if (this.parameters.containsKey("headerheight")) {
			param.put("headerheight", this.parameters.get("headerheight"));
		}
		if (this.parameters.containsKey("headerfont")) {
			param.put("headerfont", this.parameters.get("headerfont"));
		}
		if (this.parameters.containsKey("headerfg")) {
			param.put("headerfg", this.parameters.get("headerfg"));
		}
		if (this.parameters.containsKey("headerbg")) {
			param.put("headerbg", this.parameters.get("headerbg"));
		}
		if (this.parameters.containsKey("fontshadowcolor")) {
			param.put("fontshadowcolor", this.parameters.get("fontshadowcolor"));
		}
		if (this.parameters.containsKey("headerbgimage")) {
			param.put("headerbgimage", this.parameters.get("headerbgimage"));
		}
		if (this.parameters.containsKey("headerborder")) {
			param.put("headerborder", this.parameters.get("headerborder"));
		}
		if (this.parameters.containsKey("headerlastcolumnborder")) {
			param.put("headerlastcolumnborder", this.parameters.get("headerlastcolumnborder"));
		}
		if (this.parameters.containsKey("headerfirstcolumnborder")) {
			param.put("headerfirstcolumnborder", this.parameters.get("headerfirstcolumnborder"));
		}
		if (this.parameters.containsKey("border")) {
			param.put("border", this.parameters.get("border"));
		}
		if (this.parameters.containsKey("percentage")) {
			param.put("percentage", this.parameters.get("percentage"));
		}

		return param;
	}

	/**
	 * Adds the fuctionallity of manage preferences to the pivot table dialog.
	 * @param pivotDialog the dialog that contains the pivot table
	 */
	protected void setPivotTablePreferences(final JDialog pivotDialog) {
		if (!this.dynamicTable) {
			if ((pivotDialog != null) && (pivotDialog instanceof PivotDialog)) {
				final PivotDialog pD = (PivotDialog) pivotDialog;

				final JButton preferencesButton = new JButton();

				final ImageIcon saveTableFilterIcon = ImageManager.getIcon(ImageManager.SAVE_TABLE_FILTER);
				if (saveTableFilterIcon != null) {
					preferencesButton.setMargin(new Insets(2, 2, 2, 2));
					preferencesButton.setIcon(new ImageIcon(
							saveTableFilterIcon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_DEFAULT)));
				} else {
					preferencesButton.setText("P");
				}
				pD.addButton(preferencesButton);
				preferencesButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						Table.this.menuPivotTablePreferences(e);
					}
				});
			}
		}
	}

	/**
	 * Returns the key that will be used to identify the preference that will save the pivot table
	 * configuration.
	 * @param configurationName the name of the concrete configuration
	 * @return the preference key for the specified configuration
	 */
	protected String getPivotTablePreferenceKey(final String configurationName) {
		if ((configurationName == null) || (configurationName.length() == 0)) {
			final Form f = this.parentForm;
			return f != null
					? BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
					+ this.entity
					: BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + this.entity;
		} else {
			final Form f = this.parentForm;
			return f != null
					? BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
					+ this.entity + "_" + configurationName
					: BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + this.entity + "_"
					+ configurationName;
		}
	}

	/**
	 * Returns the key that will be used to identify the preference that will save the pivot table
	 * configurations.
	 * @return the preference key for the default configuration
	 */
	protected String getPivotTablePreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + f.getArchiveName() + "_"
				+ this.entity
				: BasicApplicationPreferences.TABLE_CONF_PIVOT_TABLE_CONFIGURATIONS + "_" + this.entity;
	}

	protected ExtendedJPopupMenu menuPivotTableSetup = null;

	protected JMenuItem menuSavePivot = new JMenuItem("save");

	/**
	 * Method called by the table pivot button listener that shows the pivot table dialog.
	 * @param e
	 */
	protected void menuPivotTablePreferences(final ActionEvent e) {
		final String prefer = PreferenceUtils.loadPreference(this.getPivotTablePreferenceKey());
		if (this.menuPivotTableSetup == null) {
			this.menuPivotTableSetup = new ExtendedJPopupMenu();
			this.menuSavePivot.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final Object s = MessageDialog.showInputMessage(SwingUtilities.getWindowAncestor(Table.this),
							"table.enter_configuration_name", Table.this.resourcesFile);
					if (s != null) {
						final String str = s.toString();
						Table.this.savePivotTableConfiguration(str);
						return;
					}
				}

			});
			if (!ApplicationManager.useOntimizePlaf) {
				this.menuPivotTableSetup.setBorder(new SoftBevelBorder(BevelBorder.RAISED));
			}
		} else {
			final Component[] c = this.menuPivotTableSetup.getComponents();
			for (int i = 0; (c != null) && (i < c.length); i++) {
				if (c[i] instanceof PreferenceItem) {
					((PreferenceItem) c[i]).removeAllListeners();
				}
			}
			this.menuPivotTableSetup.removeAll();
		}

		if (this.menuSavePivot != null) {
			this.menuSavePivot.setText(ApplicationManager.getTranslation("save", this.getResourceBundle()));
		}

		if ((prefer != null) && (prefer.length() > 0)) {
			final StringTokenizer tokens = new StringTokenizer(prefer, ";");
			while (tokens.hasMoreTokens()) {
				final PreferenceItem item = new PreferenceItem(tokens.nextToken());
				this.menuPivotTableSetup.add(item);
			}
			this.menuPivotTableSetup.addSeparator();
		}
		this.menuPivotTableSetup.add(this.menuSavePivot);
		this.menuPivotTableSetup.show((Component) e.getSource(), 0, ((Component) e.getSource()).getHeight());
	}

	protected ActionListener prefPivotTableItemListener = null;

	protected ActionListener deletePrefPivotTableItemListener = null;

	/**
	 * The listener that loads a concrete pivot table configuration. Called by the pivot table setup
	 * window.
	 */
	protected class ListenerItemPrefPivotTable implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			Table.this.loadPivotTableConfiguration(e.getActionCommand());
			Table.this.menuPivotTableSetup.setVisible(false);
		}

	}

	/**
	 * The listener that deletes a concrete pivot table configuration. Called by the pivo table setup
	 * window.
	 */
	protected class ListenerItemDeletePrefPivotTable implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			Table.this.deletePivotTableConfiguration(e.getActionCommand());
			Table.this.menuPivotTableSetup.setVisible(false);
		}

	}

	/**
	 * The class that has all the pivot table management GUI
	 */
	protected class PreferenceItem extends JPanel {

		private String token = null;

		private JButton menu = null;

		private TableButton button = null;

		public PreferenceItem(final String token) {
			this.token = token;
			this.menu = new RolloverButton(token) {

				@Override
				public Dimension getPreferredSize() {
					final Dimension d = super.getPreferredSize();
					d.height = 22;
					return d;
				}

				@Override
				public boolean isFocusTraversable() {
					return false;
				}

				@Override
				public boolean isRequestFocusEnabled() {
					return false;
				}
			};
			this.menu.setActionCommand(token);
			if (Table.this.prefPivotTableItemListener == null) {
				Table.this.prefPivotTableItemListener = new ListenerItemPrefPivotTable();
			}
			this.menu.addActionListener(Table.this.prefPivotTableItemListener);
			this.menu.setForeground(Color.blue.darker());
			this.menu.setIcon(ImageManager.getIcon(ImageManager.EMPTY_16));

			this.button = new TableButton();
			this.button.setIcon(ImageManager.getIcon(ImageManager.RECYCLER));
			this.button.setToolTipText(
					ApplicationManager.getTranslation("table.delete_this_configuration", Table.this.resourcesFile));
			this.button.setActionCommand(token);
			if (Table.this.deletePrefPivotTableItemListener == null) {
				Table.this.deletePrefPivotTableItemListener = new ListenerItemDeletePrefPivotTable();
			}
			this.button.addActionListener(Table.this.deletePrefPivotTableItemListener);
			this.setLayout(new GridBagLayout());
			this.setOpaque(false);
			this.add(this.button, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.menu, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.setBorder(new EmptyBorder(0, 0, 0, 0));
		}

		public void removeAllListeners() {
			this.menu.removeActionListener(Table.this.deletePrefPivotTableItemListener);
			this.button.removeActionListener(Table.this.deletePrefPivotTableItemListener);
		}

	}

	/**
	 * Load the concrete pivot table configuration.
	 * @param confName the configuration name
	 */
	protected void loadPivotTableConfiguration(final String confName) {
		if ((this.dPivot != null) && (this.dPivot instanceof PivotDialog)) {
			final String pref = PreferenceUtils.loadPreference(this.getPivotTablePreferenceKey(confName));
			if ((pref != null) && (pref.length() > 0)) {
				final Map selection = new Hashtable();
				final StringTokenizer tokens = new StringTokenizer(pref, "|");
				while (tokens.hasMoreTokens()) {
					final String token = tokens.nextToken();
					final int ind = token.lastIndexOf("~");
					if (ind > 0) {
						final String key = token.substring(1, ind);
						if (PivotTableUtils.PIVOTTABLE_ROWFIELD.equals(key)
								|| PivotTableUtils.PIVOTTABLE_COLUMNFIELD.equals(key)
								|| PivotTableUtils.PIVOTTABLE_DATAFIELD.equals(key)) {
							final String value = token.substring(ind + 1);
							if (PivotTableUtils.PIVOTTABLE_ROWFIELD.equals(key) && (value != null)
									&& (value.length() > 0)) {
								final StringTokenizer pairsFixed = new StringTokenizer(value, ":");
								final List list = new ArrayList<Pair<String, Integer>>();
								while (pairsFixed.hasMoreTokens()) {
									final String tokenFixedColumns = pairsFixed.nextToken();
									final String[] pairComp = tokenFixedColumns.split("=");
									if (pairComp.length > 1) {
										list.add(new Pair<String, Integer>(pairComp[0], Integer.parseInt(pairComp[1])));
									} else {
										list.add(pairComp[0]);
									}
								}
								selection.put(key, list);

							} else if ((value != null) && (value.length() > 0)) {
								final List list = new ArrayList(ApplicationManager.getTokensAt(value, ":"));
								selection.put(key, list);
							}
						} else if (PivotTableUtils.PIVOTTABLE_OPERATION.equals(key)
								|| PivotTableUtils.PIVOTTABLE_DATEGROUPOPTIONS.equals(key)
								|| PivotTableUtils.PIVOTTABLE_FORMAT_OPTIONS.equals(key)) {
							final String value = token.substring(ind + 1);
							if ((value != null) && (value.length() > 0)) {
								selection.put(key, value);
							}
						}
					}
				}
				((PivotDialog) this.dPivot).setSelectedColumn(selection);
			}
		}
	}

	/**
	 * Deletes the pivot table configuration passed as parameter.
	 * @param confName the configuration name
	 */
	protected void deletePivotTableConfiguration(final String confName) {
		PreferenceUtils.savePreference(this.getPivotTablePreferenceKey(confName), null);
		final String pref = PreferenceUtils.loadPreference(this.getPivotTablePreferenceKey());
		if ((pref != null) && (pref.length() > 0)) {
			final StringTokenizer tokens = new StringTokenizer(pref, ";");
			final StringBuilder buffer = new StringBuilder();
			while (tokens.hasMoreTokens()) {
				final String tok = tokens.nextToken();
				if (!confName.equals(tok)) {
					buffer.append(tok);
					if (tokens.hasMoreTokens()) {
						buffer.append(";");
					}
				}
			}
			PreferenceUtils.savePreference(this.getPivotTablePreferenceKey(), buffer.toString());
		}
	}

	/**
	 * Saves the current pivot table configuration.
	 * @param confName the configuration name
	 */
	protected void savePivotTableConfiguration(final String confName) {
		if ((this.dPivot != null) && (this.dPivot instanceof PivotDialog)) {
			final Map h = ((PivotDialog) this.dPivot).getSelectedColumn();
			final StringBuilder buffer = new StringBuilder();
			if (h.containsKey(PivotTableUtils.PIVOTTABLE_ROWFIELD)) {
				final List<Pair> fixedColumnWidth = ((PivotDialog) this.dPivot).getFixedColumnWidth();
				buffer.append("~" + PivotTableUtils.PIVOTTABLE_ROWFIELD + "~");
				for (int i = 0; i < fixedColumnWidth.size(); i++) {
					final Pair<String, Integer> p = fixedColumnWidth.get(i);
					buffer.append(p.getFirst());
					buffer.append("=");
					buffer.append(p.getSecond());
					if (i != (fixedColumnWidth.size() - 1)) {
						buffer.append(":");
					}
				}
				buffer.append("|");
			}

			if (h.containsKey(PivotTableUtils.PIVOTTABLE_COLUMNFIELD)) {
				final Object o = h.get(PivotTableUtils.PIVOTTABLE_COLUMNFIELD);
				if (o instanceof List) {
					final List list = (List) o;
					buffer.append("~" + PivotTableUtils.PIVOTTABLE_COLUMNFIELD + "~");
					for (int i = 0; i < list.size(); i++) {
						buffer.append(list.get(i));
						if (i != (list.size() - 1)) {
							buffer.append(":");
						}
					}
					buffer.append("|");
				}
			}

			if (h.containsKey(PivotTableUtils.PIVOTTABLE_DATAFIELD)) {
				final Object o = h.get(PivotTableUtils.PIVOTTABLE_DATAFIELD);
				if (o instanceof List) {
					final List list = (List) o;
					buffer.append("~" + PivotTableUtils.PIVOTTABLE_DATAFIELD + "~");
					for (int i = 0; i < list.size(); i++) {
						buffer.append(list.get(i));
						if (i != (list.size() - 1)) {
							buffer.append(":");
						}
					}
					buffer.append("|");
				}
			}

			if (h.containsKey(PivotTableUtils.PIVOTTABLE_OPERATION)) {
				final Object o = h.get(PivotTableUtils.PIVOTTABLE_OPERATION);
				if (o instanceof String) {
					final String list = (String) o;
					buffer.append("~" + PivotTableUtils.PIVOTTABLE_OPERATION + "~");
					buffer.append(list);
					buffer.append("|");
				}
			}

			if (h.containsKey(PivotTableUtils.PIVOTTABLE_DATEGROUPOPTIONS)) {
				final Object o = h.get(PivotTableUtils.PIVOTTABLE_DATEGROUPOPTIONS);
				if (o instanceof String) {
					final String list = (String) o;
					buffer.append("~" + PivotTableUtils.PIVOTTABLE_DATEGROUPOPTIONS + "~");
					buffer.append(list);
					buffer.append("|");
				}
			}

			if (h.containsKey(PivotTableUtils.PIVOTTABLE_FORMAT_OPTIONS)) {
				final Object o = h.get(PivotTableUtils.PIVOTTABLE_FORMAT_OPTIONS);
				if (o instanceof String) {
					final String list = (String) o;
					buffer.append("~" + PivotTableUtils.PIVOTTABLE_FORMAT_OPTIONS + "~");
					buffer.append(list);
					buffer.append("|");
				}
			}

			// Check if exists this name
			String pref = PreferenceUtils.loadPreference(this.getPivotTablePreferenceKey());
			boolean insert = true;
			if (pref != null) {
				final StringTokenizer tokens = new StringTokenizer(pref, ";");
				while (tokens.hasMoreElements()) {
					final String token = tokens.nextToken().trim();
					if (confName.equals(token)) {
						// Ask the user if replace the preference
						final boolean replace = MessageDialog.showQuestionMessage(Table.this,
								"pivottable.preference_name_exists", this.resourcesFile);
						if (!replace) {
							return;
						} else {
							insert = false;
							break;
						}
					}
				}

				if (insert) {
					final StringBuilder buff = new StringBuilder(pref);
					buff.append(";");
					buff.append(confName);
					pref = buff.toString();
				}
			} else {
				pref = confName;
			}

			PreferenceUtils.savePreference(this.getPivotTablePreferenceKey(confName), buffer.toString());
			PreferenceUtils.savePreference(this.getPivotTablePreferenceKey(), pref);
		}
	}

	public static class QuickFieldText extends JTextField {

		/**
		 * The name of class. Used by L&F to put UI properties.
		 *
		 * @since 5.2062EN
		 */
		public static final String QUICKFILTER_NAME = "Table.QuickFilter";

		public static boolean paintFindText = true;

		protected String emptyText;

		protected Font emptyFont;

		protected ResourceBundle bundle;

		public static int timeDelay = 500;

		protected boolean selectAll = false;

		public QuickFieldText() {
			super(10);
			this.emptyText = Table.FIND_MESSAGE;
			super.setOpaque(ApplicationManager.useOntimizePlaf ? false : true);
			this.setName(QuickFieldText.QUICKFILTER_NAME);
			this.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(final FocusEvent e) {
					if (QuickFieldText.this.selectAll) {
						QuickFieldText.this.selectAll();
					}
				}
			});
		}

		@Override
		public String getName() {
			return QuickFieldText.QUICKFILTER_NAME;
		}

		@Override
		public Dimension getMinimumSize() {
			return this.getPreferredSize();
		}

		public void setEmptyText(final String text) {
			this.emptyText = text;
		}

		public String getEmptyText() {
			return this.emptyText;
		}

		@Override
		public void setOpaque(final boolean isOpaque) {
			if (isOpaque) {
				super.setOpaque(isOpaque);
			}
		}

		public boolean isSelectAll() {
			return this.selectAll;
		}

		public void setSelectAll(final boolean selectAll) {
			this.selectAll = selectAll;
		}

		public void setResourceBundle(final ResourceBundle resource) {
			this.bundle = resource;
			if (this.bundle != null) {
				this.createFormatter(DateDataField.getSameCountryLocale(this.bundle.getLocale()));
			}
		}

		protected void createFormatter(final Locale locale) {
			if (!DateFormatCache.containsDateFormat(locale)) {
				this.createNewFormatter(locale);
			} else {
				QuickFieldText.format = DateFormatCache.getDateFormat(locale);
			}
		}

		protected void createNewFormatter(final Locale l) {
			if (l != null) {
				final SimpleDateFormat dateFormat = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM, l);
				dateFormat.setLenient(false);
				final GregorianCalendar calendar = new GregorianCalendar(l);
				calendar.setLenient(false);
				dateFormat.setCalendar(calendar);
				// Set the patter using the locale
				final DateFormatSymbols symbols = new DateFormatSymbols(l);
				symbols.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
				dateFormat.setDateFormatSymbols(symbols);

				QuickFieldText.format = dateFormat;
				// Initialize the date
				// this.buildPattern();
				// dateFormat.applyPattern(datePattern);
				DateFormatCache.addDateFormat(l, dateFormat);
			}
		}

		protected void clearFind(final Graphics g) {
			if (this.hasFocus()) {
				final Rectangle alloc = this.getBounds();
				if ((alloc.width > 0) && (alloc.height > 0)) {
					alloc.x = alloc.y = 0;
					final Insets insets = this.getInsets();
					alloc.x += insets.left;
					alloc.y += insets.top;
					alloc.width -= insets.left + insets.right;
					alloc.height -= insets.top + insets.bottom;
					g.clearRect(alloc.x, alloc.y, alloc.width, alloc.height);
				}
			}
		}

		protected void paintFind(final Graphics g) {
			final Rectangle alloc = this.getBounds();
			if ((alloc.width > 0) && (alloc.height > 0)) {
				alloc.x = alloc.y = 0;
				final Insets insets = this.getInsets();
				alloc.x += insets.left;
				alloc.y += insets.top;
				alloc.width -= insets.left + insets.right;
				alloc.height -= insets.top + insets.bottom;
				if (!this.hasFocus()) {
					g.setColor(Color.black);
					final Font f = this.emptyFont == null
							? this.getFont().deriveFont(Font.PLAIN, this.getFont().getSize() - 2) : this.emptyFont;
					g.setFont(f);
					g.drawString(ApplicationManager.getTranslation(this.emptyText, this.bundle), alloc.x, alloc.height);
				}
			}
		}

		@Override
		protected void paintComponent(final Graphics g) {
			this.clearFind(g);
			super.paintComponent(g);
			if ((this.getDocument().getLength() == 0) && (!this.hasFocus()) && QuickFieldText.paintFindText) {
				this.paintFind(g);
			}
		}

		// @Override
		// public void paint(Graphics g) {
		// this.clearFind(g);
		// super.paint(g);
		// if ((this.getDocument().getLength() == 0) && (this.hasFocus() ==
		// false) && QuickFieldText.paintFindText) {
		// this.paintFind(g);
		// }
		// }

		public void setEmptyFont(final Font emptyFont) {
			this.emptyFont = emptyFont;
		}

		public static Format format = new SimpleDateFormat("dd/MM/yyyy");

		protected QuickFilterActionListener target = new QuickFilterActionListener();

		protected Timer timer = null;

		public void executeFilter(final String text, final Table table) {
			if (this.timer == null) {
				this.timer = new Timer(0, this.target);
				this.timer.setInitialDelay(QuickFieldText.timeDelay);
			}

			if (this.timer.isRunning()) {
				this.timer.stop();
				if (this.target.table != null) {
					if (!this.target.table.equals(table)) {
						this.target.applyFilter();
					}
				}
			}

			this.target.setTableFilter(table, text);
			this.timer.start();
		}

		public Timer getTimer() {
			return this.timer;
		}

		public void setTimer(final Timer timer) {
			this.timer = timer;
		}

		public List getTextsToTranslate() {
			return null;
		}

		public void setComponentLocale(final Locale l) {

		}

	}

	protected static class QuickFilterActionListener implements ActionListener {

		protected Table table = null;

		protected String text = null;

		public void setTableFilter(final Table newTable, final String newText) {
			this.table = newTable;
			this.text = newText;
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			this.table.getQuickFilter().getTimer().stop();
			if ((this.table.getPageFetcher() != null) && this.table.getPageFetcher().isPageableEnabled()
					&& !this.table.isQuickFilterLocal()) {
				try {
					this.table.getQuickFilter().setEnabled(false);
					this.executeQuery();
				} finally {
					this.table.getQuickFilter().setEnabled(true);
				}
			} else {
				this.applyFilter();
			}
		}

		protected String createResultText() {
			final StringBuilder queryText = new StringBuilder();

			if ((this.text != null) && (this.text.length() > 0)) {
				if (!this.text.startsWith("*")) {
					queryText.append("*");
				}
				queryText.append(this.text);
				if (!this.text.endsWith("*")) {
					queryText.append("*");
				}
			}

			return queryText.toString().replace('*', '%');
		}

		protected BasicExpression createExpression(final boolean isNumber) {
			final String resultText = this.createResultText();

			BasicExpression filterExpression = null;
			final List<String> cols = this.table.getQuickFilterColumns();
			final List calculedColumns = this.table.getCalculatedColumns();

			final Map<String, Integer> columnSQLTypes = this.table.getColumnSQLTypes();

			for (final String currentColumn : cols) {
				if ((calculedColumns != null) && calculedColumns.contains(currentColumn)) {
					continue;
				}
				if ((columnSQLTypes != null) && columnSQLTypes.containsKey(currentColumn)) {
					final Integer sqlType = columnSQLTypes.get(currentColumn);
					switch (sqlType) {
					case java.sql.Types.VARCHAR:
					case java.sql.Types.LONGVARCHAR:
					case java.sql.Types.LONGNVARCHAR:
					case java.sql.Types.NCHAR:
					case java.sql.Types.NVARCHAR:
						break;
					case java.sql.Types.NCLOB:
					case java.sql.Types.CLOB:
						if (Table.quickFilterClobExclude) {
							continue;
						}
						break;
					case java.sql.Types.TINYINT:
					case java.sql.Types.SMALLINT:
					case java.sql.Types.INTEGER:
					case java.sql.Types.BIGINT:
					case java.sql.Types.FLOAT:
					case java.sql.Types.REAL:
					case java.sql.Types.DOUBLE:
					case java.sql.Types.NUMERIC:
					case java.sql.Types.DECIMAL:
						if (!isNumber) {
							continue;
						}
						break;
					default:
						continue;
					}
				}

				final BasicField bF = new BasicField(currentColumn);
				final BasicExpression expression = new BasicExpression(bF, BasicOperator.LIKE_OP, resultText);
				if (filterExpression == null) {
					filterExpression = expression;
				} else {
					filterExpression = new BasicExpression(filterExpression, BasicOperator.OR_OP, expression);
				}
			}
			return filterExpression;
		}

		protected boolean isNumber(final String text) {
			try {
				final Number object = NumberFormat.getInstance().parse(text);
				return true;
			} catch (final ParseException e) {
				Table.logger.trace("Check in quickfilter-> It's not number: " + text, e);
			}
			return false;
		}

		public void executeQuery() {
			final Cursor c = this.table.getCursor();
			final Cursor cQuickfilter = this.table.getQuickFilter().getCursor();
			try {
				this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.table.getQuickFilter().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				boolean dateFilter = false;
				boolean excludeDate = false;

				PageFetcher pageFetcher = this.table.getPageFetcher();
				pageFetcher.setOffset(0);
				pageFetcher.changePageSize(false);

				if ((this.text == null) || (this.text.length() == 0)) {
					pageFetcher.setFilterExpression(null);
					pageFetcher.refreshCurrentPageInThread();
					return;
				}

				final boolean isNumber = this.isNumber(this.text);

				excludeDate = !Table.checkFormatDate(this.text);

				if (!excludeDate) {
					try {
						QuickFieldText.format.parseObject(this.text);
						dateFilter = true;
					} catch (final ParseException e1) {
						Table.logger.trace(null, e1);
					}
				}

				final BasicExpression filterExpression = this.createExpression(isNumber);
				pageFetcher.setFilterExpression(filterExpression);
				pageFetcher = this.table.getPageFetcher();
				pageFetcher.refreshCurrentPageInThread();
			} catch (final Exception e1) {
				Table.logger.error(null, e1);
			} finally {
				this.table.setCursor(c);
				this.table.getQuickFilter().setCursor(cQuickfilter);
			}
		}

		public void applyFilter() {
			final Cursor c = this.table.getCursor();
			final Cursor cQuickfilter = this.table.getQuickFilter().getCursor();
			try {
				this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				this.table.getQuickFilter().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				boolean dateFilter = false;
				boolean excludeDate = false;

				final TableSorter sorter = (TableSorter) this.table.getJTable().getModel();
				if ((this.text == null) || (this.text.length() == 0)) {
					sorter.resetFilter();
					return;
				}

				excludeDate = !Table.checkFormatDate(this.text);

				if (!excludeDate) {
					try {
						QuickFieldText.format.parseObject(this.text);
						dateFilter = true;
					} catch (final ParseException e) {
						Table.logger.trace(null, e);
					}
				}

				if ((this.text != null) && (this.text.length() > 0)) {
					if (!this.text.startsWith("*")) {
						this.text = "*" + this.text;
					}
					if (!this.text.endsWith("*")) {
						this.text = this.text + "*";
					}
				}

				final List cols = this.table.getVisibleColumns();
				final Map filters = new Hashtable();
				final SimpleFilter filter = new SimpleFilter(this.text);

				for (int i = 0; i < cols.size(); i++) {
					final String currentColumn = (String) cols.get(i);
					for (int j = 0; j < sorter.getColumnCount(); j++) {
						if (currentColumn.equals(sorter.getColumnName(j))) {
							final Class columnClass = sorter.getColumnClass(j);
							if (dateFilter) {
								if ((columnClass != null) && Date.class.isAssignableFrom(columnClass)) {
									filters.put(currentColumn, filter);
								}
							} else if (excludeDate) {
								if ((columnClass != null) && !Date.class.isAssignableFrom(columnClass)) {
									filters.put(currentColumn, filter);
								}
							} else {
								filters.put(currentColumn, filter);
							}
							continue;
						}
					}
				}
				sorter.applyFilter(filters, true);

			} catch (final Exception e) {
				Table.logger.error(null, e);
			} finally {
				this.table.setCursor(c);
				this.table.getQuickFilter().setCursor(cQuickfilter);
			}
		}

	}

	public void setQuickFilterValue(final String text) {
		if (this.quickFilterText != null) {
			this.quickFilterText.setText(text);
		}
	}

	public QuickFieldText getQuickFilter() {
		return this.quickFilterText;
	}

	/**
	 * Get the configuration parameters. If this is a dynamic table this method updates the parameters
	 * with the new column configuration. When the value changes, the columns configuration changes too,
	 * and so, there is a difference between the configuration in the xml (that says that there are no
	 * columns) and the information that is being displayed in the GUI (which has the value in the
	 * hashtable). Some Table tools, such and the PrintTable button, creates a new Table from the
	 * parameters configuration, and then adds the values in the previous Table to the new one,
	 * provoking a configuration mismatch.
	 * @return
	 */
	protected Map getParameters() {
		if (this.dynamicTable) {
			final Map newParameters = ObjectTools.clone(this.parameters);
			final String cols = ApplicationManager.vectorToStringSeparateBySemicolon(this.attributes);
			newParameters.put(Table.COLS, cols);
			newParameters.put(Table.VISIBLE_COLS, cols);
			return newParameters;
		}
		return this.parameters;
	}

	public int getMinRowHeight() {
		return this.minRowHeight;
	}

	public void setMinRowHeight(final int height) {
		this.minRowHeight = height;
	}

	public int getPrintingFontSize() {
		return this.fontSize;
	}

	/**
	 * Get the required columns to insert. This method must be used only if inserting row exist
	 * @return
	 */
	public List getRequieredCols() {
		return this.vrequiredCols;
	}

	/**
	 * @param insert
	 * @deprecated Use setInsertInDatabase
	 */
	@Deprecated
	public void setInsertInDataBase(final boolean insert) {
		this.setInsertInDatabase(insert);
	}

	public void setInsertInDatabase(final boolean insert) {
		this.dataBaseInsert = insert;
		if (insert) {
			this.editableColumnsUpdateEntity = this.vupdateEditableColumns;
		} else {
			this.editableColumnsUpdateEntity = new Vector();
		}
	}

	/**
	 * @deprecated Use setRemoveInDatabase
	 */
	@Deprecated
	public void setRemoveInDataBase(final boolean insert) {
		this.setRemoveInDatabase(insert);
	}

	public void setRemoveInDatabase(final boolean remove) {
		this.dataBaseRemove = remove;
		if (remove) {
			this.editableColumnsUpdateEntity = this.vupdateEditableColumns;
		} else {
			this.editableColumnsUpdateEntity = new Vector();
		}
	}

	/**
	 * Enabled/disabled updating the changes in the database when the user change the values stored in
	 * those.
	 * @param update
	 */
	public void setUpdateInDatabase(final boolean update) {
		if (update) {
			this.editableColumnsUpdateEntity = this.vupdateEditableColumns;
		} else {
			this.editableColumnsUpdateEntity = new Vector();
		}
	}

	public boolean isDataBaseInsert() {
		return this.dataBaseInsert;
	}

	public boolean isInsertingEnabled() {
		if (this.getJTable().getModel() instanceof TableSorter) {
			return ((TableSorter) this.getJTable().getModel()).isInsertingEnabled();
		}
		return false;
	}

	@Override
	public void addInsertTableInsertRowListener(final InsertTableInsertRowListener l) {
		this.insertTableInsertRowListenerList.add(InsertTableInsertRowListener.class, l);
	}

	@Override
	public void removeInsertTableInsertRowListener(final InsertTableInsertRowListener l) {
		this.insertTableInsertRowListenerList.remove(InsertTableInsertRowListener.class, l);
	}

	@Override
	public void fireInsertTableInsertRowChange(final InsertTableInsertRowEvent insertTableInsertRowEvent) {
		final Object aobj[] = this.insertTableInsertRowListenerList.getListenerList();
		for (int i = aobj.length - 2; i >= 0; i -= 2) {
			if (aobj[i] == InsertTableInsertRowListener.class) {
				((InsertTableInsertRowListener) aobj[i + 1]).insertTableInsertRowChange(insertTableInsertRowEvent);
			}
		}
	}

	public TableSorter getTableSorter() {
		if (this.getJTable().getModel() instanceof TableSorter) {
			return (TableSorter) this.getJTable().getModel();
		}
		return null;
	}

	public int getColPress() {
		return this.colPress;
	}

	public void checkInsertingRowValue() throws Exception {
		final List vreq = this.getRequieredCols();
		for (int i = 0; i < vreq.size(); i++) {
			final Object col = vreq.get(i);
			final Object value = this.getInsertingData().get(col);
			if ((value == null) || value.equals("")) {
				throw new Exception("table.insertingrequiredfieldserror");
			}
		}
	}

	protected Map getInsertingData() {
		if (this.isInsertingEnabled()) {
			final TableSorter model = this.getTableSorter();
			final Map data = model.getInsertingData();

			if ((this.getCalculatedColumns() != null) && (this.getCalculatedColumns().size() > 0)) {
				final int rowIndex = model.getRowCount() - 1;
				if (model.isInsertingRow(rowIndex)) {
					final Map calculatedRowData = model.getCalculatedRowData(rowIndex);
					if (calculatedRowData != null) {
						data.putAll(calculatedRowData);
					}
				}
			}
			return data;
		}
		return null;
	}

	protected void executeInsertRow() {
		if (((TableSorter) this.getJTable().getModel()).isInsertingRow(this.getJTable().getSelectedRow())) {
			try {
				// If there is not an active insertion
				if (!this.inserting) {
					this.checkInsertingRowValue();
					try {
						final Map insertingRowData = this.getInsertingData();
						if (this.isDataBaseInsert()) {
							final Entity ent = ApplicationManager.getApplication()
									.getReferenceLocator()
									.getEntityReference(this.getEntityName());
							final EntityResult rs = ent.insert(insertingRowData,
									ApplicationManager.getApplication().getReferenceLocator().getSessionId());
							if (rs.getCode() == EntityResult.OPERATION_WRONG) {
								throw new Exception(rs.getMessage());
							}
							if (rs.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
								this.getParentForm().message(rs.getMessage(), Form.INFORMATION_MESSAGE);
							}
							final Enumeration en = rs.keys();
							while (en.hasMoreElements()) {
								final Object key = en.nextElement();
								insertingRowData.put(key, rs.get(key));
							}
						}
						this.addRow(insertingRowData);
						this.enableInsert();
						this.fireInsertTableInsertRowChange(new InsertTableInsertRowEvent(this, insertingRowData));
						this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
					} finally {
						this.inserting = false;
					}
				}
				// Insert the row
				// getJTable().changeSelection(getJTable().getRowCount() - 1, 0,
				// false, false);
			} catch (final Exception ex) {
				Table.logger.error(null, ex);
				this.getParentForm().message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
			}
		}
	}

	protected void registerKeyListeners() {
		this.getJTable().addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(final KeyEvent e) {
				super.keyTyped(e);
				if (Table.this.inserting) {
					return;
				}
				if ((e.getKeyCode() == KeyEvent.VK_ESCAPE) || (e.getKeyChar() == KeyEvent.VK_ESCAPE)) {
					if (Table.this.getJTable().getSelectedRow() == (Table.this.getJTable().getRowCount() - 1)) {
						final TableSorter sorter = Table.this.getTableSorter();
						sorter.clearInsertingRow(Table.this.getParentKeyValues());
						sorter.fireTableChanged(new TableModelEvent(sorter, sorter.getRowCount() - 1));
					}
				}
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				super.keyPressed(e);
				if (Table.this.inserting) {
					return;
				}
				// if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				// executeInsertRow();
				// getJTable().changeSelection(getJTable().getRowCount() - 1, 0,
				// false, false);
				// }
			}
		});
	}

	public boolean isTranslateHeader() {
		return this.translateHeader;
	}

	public PageFetcher getPageFetcher() {
		return this.pageFetcher;
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_TABLE;
	}

	@Override
	public Object getTemplateDataValue() {
		return this.getValueToExport(false, false);
	}

	protected Map retrieveOnSetValueData() {
		if (this.hOnSetValueSetFunction == null) {
			this.hOnSetValueSetFunction = this.parseFunction(this.hOnSetValueSetEquivalences);
		}
		final Map data = new Hashtable();
		for (int i = 0; i < this.onsetvaluesetAttributes.size(); i++) {
			final String at = (String) this.onsetvaluesetAttributes.get(i);
			final String columnName = (String) this.hOnSetValueSetEquivalences.get(at);
			final String function = (String) this.hOnSetValueSetFunction.get(at);
			final Object value = ((TableSorter) this.getJTable().getModel()).getColumnOperation(columnName, function);
			if (value != null) {
				data.put(columnName, value);
			}
		}
		return data;
	}

	protected void updateOnSetValueSetAttributes(final Map data) {
		if ((this.parentForm != null) && (data != null)) {
			for (int i = 0; i < this.onsetvaluesetAttributes.size(); i++) {
				final Object at = this.onsetvaluesetAttributes.get(i);
				final Object oValue = data.get(this.hOnSetValueSetEquivalences.get(at));
				this.parentForm.setDataFieldValue(at, oValue);
				Table.logger.debug("Setting field value: {} -> {}", at, oValue);
			}
		}
	}

	protected Map parseFunction(final Map equivalences) {
		final Map functionEquivalences = new Hashtable();
		final Enumeration enumeration = Collections.enumeration(equivalences.keySet());
		while (enumeration.hasMoreElements()) {
			final Object attr = enumeration.nextElement();
			final String valueToParse = (String) equivalences.get(attr);
			String columnIdentifier;
			String function = ExtendedTableModel.SUM_OPERATION;

			if (valueToParse.indexOf("(") >= 0) {
				function = valueToParse.substring(0, valueToParse.indexOf("(")).trim();
				columnIdentifier = valueToParse.substring(valueToParse.indexOf("(") + 1, valueToParse.lastIndexOf(")"))
						.trim();
			} else {
				columnIdentifier = valueToParse;
				int columnIndex = -1;
				for (int i = 0; i < this.getTableSorter().getColumnCount(); i++) {
					if (columnIdentifier.equals(this.getTableSorter().getColumnName(i))) {
						columnIndex = i;
						break;
					}
				}

				if (columnIndex < 0) {
					function = ExtendedTableModel.SUM_OPERATION;
				} else {
					final Class columnClass = this.getTableSorter().getColumnClass(columnIndex);
					if (Number.class.isAssignableFrom(columnClass)) {
						function = ExtendedTableModel.SUM_OPERATION;
					} else if (String.class.isAssignableFrom(columnClass)) {
						function = ExtendedTableModel.CONCAT_OPERATION;
					}
				}
			}
			equivalences.put(attr, columnIdentifier);
			functionEquivalences.put(attr, function);
		}
		return functionEquivalences;
	}

	@Override
	public Font getFont() {
		final Font font = super.getFont();
		if (font == null) {
			return this.getContentPane().getFont();
		}
		return font;
	}

	/**
	 * Pattern to format the field contents. Null if the <code>format</code> parameter is missing. Also
	 * wrappers the content of the <code>dateformat</code> parameter.
	 *
	 * @since Ontimize 5.4.3
	 */
	protected FormatPattern formatPattern = null;

	public FormatPattern getDetailFormatPattern() {
		return this.formatPattern;
	}

	protected void configureDetailFormat(final Map parameters) {
		final String oFormat = ParseUtils.getString((String) parameters.get(Table.DETAIL_FORMAT), null);
		if (oFormat != null) {
			this.formatPattern = new FormatPattern(oFormat);

			final String oDateFormat = ParseUtils.getString((String) parameters.get(Table.DETAIL_DATE_FORMAT), null);
			if (oDateFormat != null) {
				this.formatPattern.setDateFormat(oDateFormat);
			}
		}
	}

	protected void configureInsertTitleKey(final Map parameters) {
		this.insertTitleKey = ParseUtils.getString((String) parameters.get(Table.INSERT_TITLE_KEY), "insert");
	}

	protected String insertTitleKey;

	public String getInsertTitleKey() {
		return this.insertTitleKey;
	}

	public Map<String, Integer> getColumnSQLTypes() {
		return this.hColumnSQLTypes;
	}

	public boolean isQuickFilterLocal() {
		return this.quickFilterLocal;
	}

	public boolean isQueryRowsModifiable() {
		return this.queryRowsModifiable;
	}

	public void setQueryRowsModifiable(final boolean queryRowsModifiable) {
		this.queryRowsModifiable = queryRowsModifiable;
	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (this.insertMode && (InteractionManager.INSERT == e.getInteractionManagerMode())) {
			this.setEnabled(true);
			this.operationInMemory = true;
			if (this.memoryEntity == null) {
				final StringBuilder buffer = new StringBuilder("$LocalProxy$");
				buffer.append(UUID.randomUUID());
				buffer.append("$");
				buffer.append(this.entity);
				this.memoryEntity = buffer.toString();
			}
		} else {
			this.operationInMemory = false;

		}
	}

	public boolean isOperationInMemory() {
		return this.operationInMemory;
	}

	public int getDetailTitleMaxSize() {
		return this.detailTitleMaxSize;
	}

	public void setDetailTitleMaxSize(final int detailTitleMaxSize) {
		this.detailTitleMaxSize = detailTitleMaxSize;
	}

	public boolean isFitRowHeight() {
		return fitRowHeight;
	}

	public void setFitRowHeight(final boolean fitRowHeight) {
		this.fitRowHeight = fitRowHeight;
	}

	public static class CalculatedCellListModel extends DefaultListModel {

		public CalculatedCellListModel() {
			super();

			for (final String name : Table.getRendererMap().keySet()) {
				super.addElement(new com.ontimize.gui.table.Table.SelectableRenderCell(name));

			}
		}

	}

	public ListModel getCalculatedColumnsRender() {
		return this.renderListModel;
	}

	public static Map<String, CellRenderer> getRendererMap() {
		initRendererMap();
		return Table.rendererMap;
	}

	public static class SelectableRenderCell extends SelectableItem {

		public SelectableRenderCell(final Object value) {
			super(value);
		}

		@Override
		public String toString() {
			return ApplicationManager.getTranslation((String) this.getValue());
		}

	}

}
