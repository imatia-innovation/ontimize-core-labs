package com.ontimize.report.engine.dynamicjasper;

import java.awt.Component;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JPopupMenu;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.StyleConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.db.EntityResultUtils.EntityResultTableModel;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.table.CurrencyCellRenderer;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.PercentCellRenderer;
import com.ontimize.gui.table.RealCellRenderer;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.report.store.BasicReportStoreDefinition;
import com.ontimize.report.DefaultReportDialog;
import com.ontimize.report.ReportEngine;
import com.ontimize.report.ReportUtils;
import com.ontimize.report.SelectableItemOrder;
import com.ontimize.report.TableSorter;
import com.ontimize.report.item.PredefinedFunctionItem;
import com.ontimize.report.item.SelectableItem;
import com.ontimize.report.item.SelectableMultipleItem;
import com.ontimize.report.listeners.UpdateReportListener;
import com.ontimize.report.utils.PreviewDialog;
import com.ontimize.report.utils.ReportProcessor;
import com.ontimize.util.CollectionTools;
import com.ontimize.util.swing.image.BooleanImage;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.FontHelper;
import ar.com.fdvs.dj.core.layout.LayoutManager;
import ar.com.fdvs.dj.domain.CustomExpression;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DJValueFormatter;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ExpressionHelper;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.ImageScaleMode;
import ar.com.fdvs.dj.domain.constants.LabelPosition;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJColSpan;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRVirtualizer;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * This class is a default implementation of {@link ReportEngine} to allow using JasperReports in
 * <code>Table</code> reports. It is used DynamicJasper library (version 3.0.13 - LGPL license) :
 * http://dynamicjasper.sourceforge.net/ to build reports dynamically at runtime.
 *
 * Required libraries: {@link #checkLibraries()}
 *
 * @author Imatia Innovation SL
 * @since 5.2057EN-0.9
 */
public class DynamicJasperEngine implements ReportEngine, IGroupByDate, Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(DynamicJasperEngine.class);

	public static final String TRACE = "trace";

	public static final String DEBUG = "debug";

	public static final String INFO = "info";

	public static final String WARN = "warn";

	public static final String ERROR = "error";

	public static final String FATAL = "fatal";

	public static String logLevel = DynamicJasperEngine.ERROR;

	static {
		DynamicJasperEngine.setLogLevel(DynamicJasperEngine.ERROR);
	}

	public static final String GROUP_COUNT_LABEL = "group_count_label_";

	/**
	 * Sets log level for this engine. Available values (static variables of this class):
	 * <ul>
	 * <li>TRACE
	 * <li>DEBUG
	 * <li>INFO
	 * <li>WARN
	 * <li>ERROR
	 * <li>FATAL
	 * </ul>
	 * @param logLevel the <code>String</code> with error level.
	 */
	public static void setLogLevel(final String logLevel) {
		try {
			System.setProperty("org.apache.commons.logging.simplelog.defaultlog", logLevel);
		} catch (final Exception e) {
			DynamicJasperEngine.logger.error(null, e);
		}
	}

	/**
	 * Objects that implements Jasper interface <code>JRDataSource</code> to store report data.
	 */
	protected TableModelDataSource erDataSource;

	public static final int WIDTH_OFFSET = 4;

	public static final int DEFAULT_PADDING_FOR_GROUPS = 7;

	public static final int DEFAULT_WIDTH_ROW_NUMBERS = 20;

	public static final int DEFAULT_COLUMN_WIDTH = 140;

	public static int paddingForGroups = DynamicJasperEngine.DEFAULT_PADDING_FOR_GROUPS;

	public static int widthRowNumbers = DynamicJasperEngine.DEFAULT_WIDTH_ROW_NUMBERS;

	public static int columnWidth = DynamicJasperEngine.DEFAULT_COLUMN_WIDTH;

	public static boolean useTemplatesWithDefaultLogo = true;

	public static String reportLogoPath;

	public static String reportLogoOnlyForFirstPage;

	public String dynamicGroupIdentifier = new String();

	public static final String DEFAULT_JRXML_TEMPLATE_PORTRAIT = "com/ontimize/report/default_portrait_jasper.jrxml";

	public static final String DEFAULT_JRXML_TEMPLATE_LANDSCAPE = "com/ontimize/report/default_landscape_jasper.jrxml";

	public static final String DEFAULT_JRXML_TEMPLATE_PORTRAIT_NO_LOGO = "com/ontimize/report/default_portrait_jasper_no_logo.jrxml";

	public static final String DEFAULT_JRXML_TEMPLATE_LANDSCAPE_NO_LOGO = "com/ontimize/report/default_landscape_jasper_no_logo.jrxml";

	public static final String JANUARY = "report.month1";

	public static final String FEBRUARY = "report.month2";

	public static final String MARCH = "report.month3";

	public static final String APRIL = "report.month4";

	public static final String MAY = "report.month5";

	public static final String JUNE = "report.month6";

	public static final String JULY = "report.month7";

	public static final String AUGUST = "report.month8";

	public static final String SEPTEMBER = "report.month9";

	public static final String OCTOBER = "report.month10";

	public static final String NOVEMBER = "report.month11";

	public static final String DECEMBER = "report.month12";

	public static final String FIRST_QUARTER = "report.quarter1";

	public static final String SECOND_QUARTER = "report.quarter2";

	public static final String THIRD_QUARTER = "report.quarter3";

	public static final String FOURTH_QUARTER = "report.quarter4";

	public static String dateNameSeparator = ": ";

	public static String groupDateSeparator = " - ";

	public static String groupOpenCharacter = "(";

	public static String groupCloseCharacter = ")";

	public static String GROUP_BY_DEFAULT = "";

	/**
	 * Suffix added to virtual column at the end. By default, <i>_virtual_column</i>
	 */
	public static String VIRTUAL_SUFFIX = "_virtual_column";

	public static String VIRTUAL_ORDERING_SUFFIX = "_virtual_ordering";

	/**
	 * Instance for report dialog (this report dialog contains all elements in panel that is showed when
	 * user presses the report button in Table).
	 */
	protected DefaultReportDialog reportDialog;

	/**
	 * Builder for reports, with this instance is built the report.
	 */
	protected CustomDynamicReportBuilder drb;

	protected DynamicReport dr;

	protected TableModel model;

	protected TableSorter sortermodel;

	protected EntityResult erData;

	protected boolean isfocusAdapterAdded = false;

	protected boolean bPreviousEvaluation = false;

	protected String sHeaderGroupingDate = "";

	protected EntityResult resultmodel = new EntityResultMapImpl();

	protected Map parameters = new Hashtable();

	protected Map hColsPositions = new Hashtable();

	protected Map hMultiGroupColumns = new Hashtable();

	protected Map hRenderColumns = new Hashtable();

	protected CustomJasperViewer viewer;

	protected JasperPrint jp;

	/** Group builder **/
	protected GroupBuilder gb1 = new GroupBuilder();

	public List vMultiGroupColumns = new Vector();

	/**
	 * It contains the equivalences between column and its virtualcolumn. For example, when user selects
	 * an item for grouping by a date column, automatically it is added a virtual column to model (with
	 * name original_column + VIRTUAL_SUFFIX required for calculations. In this case, in this
	 * <code>Hashtable</code> is added an entry (original_column , original_column + VIRTUAL_SUFFIX).
	 */
	protected Map hVirtualColumns = new Hashtable();

	// Styles

	protected Style defaultTitleStyle;

	protected Style defaultSubtitleStyle;

	protected Style defaultHeaderStyle;

	protected Style defaultHeaderForGroupStyle;

	protected Style defaultHeaderForMultiGroupStyle;

	protected Style defaultColumnDataStyle;

	protected Style oddRowBackgroundStyle;

	protected Style styleFooterVariable;

	protected Style styleGroupFooterVariable;

	protected Style styleGroupFooterNumberOcurrencesVariable;

	// Virtualizers

	protected JRVirtualizer virtualizer;

	public static boolean useVirtualizerByDefault = false;

	public static boolean isShowedDateInReportFooterByDefault = true;

	public static boolean isShowedPageInReportFooterByDefault = true;

	public static boolean isShowedTitleByDefault = true;

	public static boolean isShowedSubtitleByDefault = true;

	public static int defaultVirtualizerCacheSize = 20;

	protected boolean isUsedVirtualizer = DynamicJasperEngine.useVirtualizerByDefault;

	protected boolean isRegisteredVirtualizer = false;

	// Parameters showed in report footer (current page and current date)

	protected boolean isShowedDateInReportFooter = DynamicJasperEngine.isShowedDateInReportFooterByDefault;

	protected boolean isShowedPageInReportFooter = DynamicJasperEngine.isShowedPageInReportFooterByDefault;

	protected boolean isShowedTitle = DynamicJasperEngine.isShowedTitleByDefault;

	protected boolean isShowedSubtitle = DynamicJasperEngine.isShowedSubtitleByDefault;

	/**
	 * @deprecated in 5.2060EN-0.1. In next versions numeric patterns for reports are retrieved from
	 *             table renderers automatically. This variable is ignored.
	 */
	@Deprecated
	public static String numericPattern = "0.00";

	@Override
	public void close() {

	}

	/**
	 * Sets the dynamic report builder to null.
	 */
	@Override
	public void dispose() {
		this.drb = null;
	}

	/**
	 * Not implemented.
	 */
	@Override
	public BasicReportStoreDefinition generaReportStoreDefinition(final String pageTitle) {
		return null;
	}

	/**
	 * Gets the manager that controls the layout of report.
	 * @return the layout manager
	 */
	protected LayoutManager getLayoutManager() {
		return new CustomClassicLayoutManager(this.hColsPositions, this, this.isShowedRowNumber());
	}

	/**
	 * Creates the model. This model contains the data to show in report. When report has groups this
	 * model is a com.ontimize.report.TableSorter object in other situation is a EntityResultTableModel.
	 *
	 * @see {@link DefaultReportDialog#getOrderedDataModel(boolean)}
	 */
	public void createModel() {
		final long init = System.currentTimeMillis();
		DynamicJasperEngine.logger.trace("Creating model -> ");

		this.model = this.validateModel(this.getDataModel(this.reportDialog.getAscendingOpMenu().isSelected()));
		this.createVirtualColumns();
		this.configureColumnRenders();
		this.erData = ((EntityResultTableModel) this.reportDialog.getModel()).getEntityResult();
		this.sortermodel = this.getOrderedDataModel(this.reportDialog.getAscendingOpMenu().isSelected());
		this.erDataSource = new TableModelDataSource(this.sortermodel);

		final long elapsed = System.currentTimeMillis() - init;
		DynamicJasperEngine.logger.trace("Total time: {} ms.", elapsed);
	}

	public void createVirtualColumns() {
		final List printCols = this.reportDialog.getPrintingColumns();
		if (printCols != null) {
			if (!printCols.isEmpty()) {
				for (int i = 0; i < printCols.size(); i++) {
					if (this.model != null) {
						if (this.isDateColumn(this.getColumnClassForColumn(printCols.get(i).toString()))) {
							this.hVirtualColumns.put(printCols.get(i).toString(),
									this.getVirtualColumn(printCols.get(i).toString()));
						}
					}
				}
			}
		}
	}

	public TableModel getDataModelWithVirtualColumns() {
		if (this.hVirtualColumns.size() > 0) {
			final EntityResult res = this.erData;
			final Enumeration enumkeys = Collections.enumeration(this.hVirtualColumns.keySet());
			while (enumkeys.hasMoreElements()) {
				final Object keyColumn = enumkeys.nextElement();
				Object keyValue = this.hVirtualColumns.get(keyColumn);
				final List vVirtualValues = this.generateVirtualColumnValues(keyColumn.toString(),
						(List) res.get(keyColumn));
				if (vVirtualValues != null) {
					res.put(keyValue, vVirtualValues.get(0));
					keyValue = this.getVirtualColumnForOrdering(keyValue.toString());
					res.put(keyValue, vVirtualValues.get(1));
				}
			}
			return EntityResultUtils.createTableModel(res);
		}
		return this.model;
	}

	private String getCompleteValueForDate(String sOriginalValue) {
		if (sOriginalValue.length() == 1) {
			return sOriginalValue = "0" + sOriginalValue;
		}
		return sOriginalValue;
	}

	public String getGroupingDateTimePattern() {
		if (ApplicationManager.getLocale().toString().equals("es_ES")) {
			return "dd-MM-yyyy HH:mm";
		}
		if (ApplicationManager.getLocale().toString().equals("en_US")) {
			return "MM-dd-yyyy HH:mm";
		}
		if (ApplicationManager.getLocale().toString().equals("en_GB")) {
			return "dd-MM-yyyy HH:mm";
		}
		if (ApplicationManager.getLocale().toString().equals("gl_ES")) {
			return "dd-MM-yyyy HH:mm";
		}
		return "MM-dd-yyyy HH:mm";
	}

	public String getGroupingDatePattern() {
		if (ApplicationManager.getLocale().toString().equals("es_ES")) {
			return "dd/MM/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("en_US")) {
			return "MM/dd/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("en_GB")) {
			return "dd/MM/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("gl_ES")) {
			return "dd/MM/yyyy";
		}
		return "MM/dd/yyyy";
	}

	public AbstractColumn setPatterns(final AbstractColumn column, final Class columnClass) {
		if (this.getPatternForColumn(column.getName()) != null) {
			column.setPattern(this.getPatternForColumn(column.getName()));
		}
		return column;
	}

	// public List generateVirtualColumnValuesForOrdering(String column,
	// List originalColumnValues){
	// List virtualColumnValues = new Vector();
	// Integer operation = (Integer)
	// reportDialog.getSelectedDateGroupingColumns().get(column);
	// if (operation == null){
	// //not selected operation
	// operation = new Integer(0);
	// }
	// Calendar calendar = Calendar.getInstance();
	// for (int i=0;i<originalColumnValues.size();i++){
	// String value=null;
	// if (originalColumnValues.get(i)!= null){
	// calendar.setTime((java.util.Date)originalColumnValues.get(i));
	// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// switch (operation.intValue()) {
	// case 0:
	// //Complete date (day, month and year) with time
	// SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// value = sdfDateTime.format(calendar.getTime());
	// break;
	// case 1:
	// //Date without time
	// SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
	// value = sdfDate.format(calendar.getTime());
	// //value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_DAY_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// getCompleteValueForDate(Integer.toString((calendar.get(Calendar.DAY_OF_MONTH))));
	// break;
	// case 2:
	// //Month
	// value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH)+1));
	// break;
	// case 3:
	// //Month and year
	// value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// Integer.toString(calendar.get(Calendar.YEAR)) + groupDateSeparator +
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH)+1));
	// break;
	// case 4:
	// //Quarter
	// value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// Integer.toString((calendar.get(Calendar.MONTH)+1)/4 + 1);
	// break;
	// case 5:
	// //Quarter and year
	// value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// Integer.toString(calendar.get(Calendar.YEAR)) + groupDateSeparator +
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// Integer.toString((calendar.get(Calendar.MONTH)+1)/4 + 1);
	// break;
	// case 6:
	// //Year
	// value =
	// ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
	// reportDialog.getBundle()) + dateNameSeparator +
	// Integer.toString(calendar.get(Calendar.YEAR));
	// break;
	// default:
	// value = sdf.format(calendar.getTime());
	// }
	// }
	// virtualColumnValues.add(value);
	// }
	// return virtualColumnValues;
	// }

	public List generateVirtualColumnValues(final String column, final List originalColumnValues) {
		final List virtualColumnValues = new Vector();
		final List virtualColumnValuesForOrdering = new Vector();
		final List vGlobal = new Vector();
		Integer operation = (Integer) this.reportDialog.getSelectedDateGroupingColumns().get(column);
		if (operation == null) {
			// not selected operation
			operation = new Integer(0);
		}
		final Calendar calendar = Calendar.getInstance();
		for (int i = 0; i < originalColumnValues.size(); i++) {
			String value = null;
			String valueOrdering = null;
			if (originalColumnValues.get(i) != null) {
				if (!(originalColumnValues.get(i) instanceof Date)) {
					return null;
				}
				calendar.setTime((java.util.Date) originalColumnValues.get(i));
				switch (operation.intValue()) {
				case 0:
					// Complete date (day, month and year) and hour
					final SimpleDateFormat sdfDateTime = new SimpleDateFormat(this.getPatternForColumn(column));
					value = sdfDateTime.format(calendar.getTime());
					final SimpleDateFormat sdfDateTimeOrdering = new SimpleDateFormat("yyyy-MM-dd HH:mm");
					valueOrdering = sdfDateTimeOrdering.format(calendar.getTime());
					break;
				case 1:
					// Date without hour
					final SimpleDateFormat sdfDate = new SimpleDateFormat(this.getGroupingDatePattern());
					value = sdfDate.format(calendar.getTime());
					final SimpleDateFormat sdfDateOrdering = new SimpleDateFormat("yyyy-MM-dd");
					valueOrdering = sdfDateOrdering.format(calendar.getTime());
					break;
				case 2:
					// Month
					value = this.getMonthText(calendar.get(Calendar.MONTH) + 1);
					valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
							this.reportDialog
							.getBundle())
							+ DynamicJasperEngine.dateNameSeparator
							+ this.getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH) + 1));;
							break;
				case 3:
					// Month and year
					value = this.getMonthText(calendar.get(Calendar.MONTH) + 1) + " "
							+ Integer.toString(calendar.get(Calendar.YEAR));
					valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
							+ Integer
							.toString(calendar.get(Calendar.YEAR))
							+ DynamicJasperEngine.groupDateSeparator + ApplicationManager.getTranslation(
									DefaultReportDialog.GROUP_BY_MONTH_KEY, this.reportDialog.getBundle())
							+ DynamicJasperEngine.dateNameSeparator + this
							.getCompleteValueForDate(Integer.toString(calendar.get(Calendar.MONTH) + 1));
					break;
				case 4:
					// Quarter
					value = this.getQuarterText(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
					valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
							this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
							+ Integer.toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);;
							break;
				case 5:
					// Quarter and year
					value = this.getQuarterText(((calendar.get(Calendar.MONTH) + 1) / 4) + 1) + " "
							+ Integer.toString(calendar.get(Calendar.YEAR));
					valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
							+ Integer
							.toString(calendar.get(Calendar.YEAR))
							+ DynamicJasperEngine.groupDateSeparator + ApplicationManager.getTranslation(
									DefaultReportDialog.GROUP_BY_QUARTER_KEY, this.reportDialog.getBundle())
							+ DynamicJasperEngine.dateNameSeparator + Integer
							.toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
					break;
				case 6:
					// Year
					value = Integer.toString(calendar.get(Calendar.YEAR));
					valueOrdering = ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
							+ Integer.toString(calendar.get(Calendar.YEAR));;
							break;
				}
			}
			virtualColumnValues.add(value);
			virtualColumnValuesForOrdering.add(valueOrdering);
		}
		vGlobal.add(virtualColumnValues);
		vGlobal.add(virtualColumnValuesForOrdering);
		return vGlobal;
	}

	/**
	 * Convenience method that modifies report data model for including a virtual column (not showed)
	 * for date columns to apply additional grouping operations (by date, by day, by month...).
	 * @param dataModel the model with data
	 * @return the modified data model
	 */
	private TableModel validateModel(TableModel dataModel) {
		if (this.hVirtualColumns.size() > 0) {
			final EntityResult res = this.erData;
			final Enumeration enumkeys = Collections.enumeration(this.hVirtualColumns.keySet());
			while (enumkeys.hasMoreElements()) {
				final Object keyColumn = enumkeys.nextElement();
				Object keyValue = this.hVirtualColumns.get(keyColumn);
				final List vVirtualValues = this.generateVirtualColumnValues(keyColumn.toString(),
						(List) res.get(keyColumn));
				res.put(keyValue, vVirtualValues.get(0));
				keyValue = this.getVirtualColumnForOrdering(keyValue.toString());
				res.put(keyValue, vVirtualValues.get(1));
			}
			dataModel = EntityResultUtils.createTableModel(res);
		}
		return dataModel;
	}

	/**
	 * Creates the report builder.
	 */
	public void createReportBuilder() {
		final long init = System.currentTimeMillis();
		DynamicJasperEngine.logger.trace("Creating report builder -> ");

		this.drb = null;
		this.drb = new CustomDynamicReportBuilder();
		this.dr = null;
		this.dr = new DynamicReport();

		final long elapsed = System.currentTimeMillis() - init;
		DynamicJasperEngine.logger.trace("Total time: {} ms.", elapsed);
	}

	public DynamicReport getDynamicReport() {
		return this.dr;
	}

	public void setDynamicReport(final DynamicReport dr) {
		this.dr = dr;
	}

	/**
	 * This method builds report. Calls to:
	 * <ul>
	 * <li>clearReportVariables()
	 * <li>createModel()
	 * <li>createReportBuilder()
	 * <li>setReportStyles()
	 * <li>buildReportHeader()
	 * <li>buildReportDetail()
	 * <li>buildReportFooter()
	 * <li>setDefaultParameters()
	 * <li>DynamicJasperHelper.generateJasperPrint(...)
	 * <li>refreshOntimizeViewer()
	 * </ul>
	 *
	 * <b>Notice</b>: title and description (subtitle) parameters not used in this implementation.
	 */
	@Override
	public Object generateReport(final String pageTitle, final String description) throws Exception {
		this.clearReportVariables();
		this.createModel();
		this.createReportBuilder();
		// Set styles, required for build report well-formed
		this.setReportStyles();
		// Build report
		long initialTime = System.currentTimeMillis();
		DynamicJasperEngine.logger.trace("Building report -> ");
		this.buildReportHeader();
		this.buildReportDetail();
		this.buildReportFooter();
		this.drb.setUseFullPageWidth(true);
		this.dr = this.drb.build();
		DynamicJasperEngine.logger.trace("Total time: {} ms", System.currentTimeMillis() - initialTime);
		initialTime = System.currentTimeMillis();
		DynamicJasperEngine.logger.trace("Generating report -> ");
		this.setDefaultParameters();
		this.jp = DynamicJasperHelper.generateJasperPrint(this.dr, this.getLayoutManager(), this.erDataSource,
				this.parameters);
		DynamicJasperEngine.logger.trace("Total time: {} ms", System.currentTimeMillis() - initialTime);
		this.refreshOntimizeViewer(this.jp);
		return this.jp;
	}

	/**
	 * Method called before report is built. It is useful to clear variables and data structures that
	 * are shared in report.
	 */
	public void clearReportVariables() {
		// Map with positions to keep initial positions
		this.hColsPositions.clear();
		this.hMultiGroupColumns.clear();
		this.vMultiGroupColumns.clear();
		this.clearVirtualColumns();
		this.hRenderColumns.clear();
	}

	/**
	 * Sets the report logo for template when it is not used the default template. There are two
	 * possible configurations:
	 * <ul>
	 * <li>The same logo for all pages. Configuring only static variable: reportLogoPath from client
	 * side.
	 * <li>One logo for first page and another for the rest of pages. Configuring static variables
	 * reportLogoOnlyForFirstPage and reportLogoPath.
	 * </ul>
	 */
	public void setReportLogo() {
		if ((DynamicJasperEngine.reportLogoOnlyForFirstPage != null)
				&& DynamicJasperEngine.useTemplatesWithDefaultLogo) {
			this.drb.addFirstPageImageBanner(DynamicJasperEngine.reportLogoOnlyForFirstPage, new Integer(175),
					new Integer(70), ImageBanner.ALIGN_RIGHT);
		}
	}

	/**
	 * Returns the viewer.
	 * @return the viewer
	 */
	@Override
	public Object getBaseTemplate() {
		return this.viewer;
	}

	/**
	 * Gets the text of title field.
	 * @return the title of page
	 */
	@Override
	public String getPageTitle() {
		return ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getTitleField().getText();
	}

	/**
	 * Gets the text of subtitle field.
	 * @return the subtitle text
	 */
	@Override
	public String getReportDescription() {
		return ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getDescriptionField().getText();
	}

	/**
	 * Get column index associated with <code>columnName</code> in a particular table model.
	 * @param columnName The name of column
	 * @param model Data model
	 * @return the index of column or -1 when it is not encountered
	 */
	public int getColumnIndex(final String columnName, final TableModel model) {
		final int iColumnCount = model.getColumnCount();
		for (int i = 0; i < iColumnCount; i++) {
			final String sName = model.getColumnName(i);
			if (sName.equals(columnName)) {
				return i;
			}
		}
		return -1; // Column not found
	}

	/**
	 * Gets the model with data for report. It can be a TableModel or a TableSorter depending on
	 * existing groups (For checking about existence of groups {@link #getRealNumberOfGroups()}).
	 * @param ascending the order for data
	 * @return the data model
	 */
	protected TableModel getDataModel(final boolean ascending) {
		final List groupCols = this.reportDialog.getSelectedGroupColumns();
		if (groupCols != null) {
			if (!groupCols.isEmpty()) {
				final TableSorter sorter = new TableSorter(this.getDataModelWithVirtualColumns());
				for (int i = 0; i < groupCols.size(); i++) {
					final Object oColumn = groupCols.get(i);
					final StringTokenizer token = new StringTokenizer(oColumn.toString(), ",");
					while (token.hasMoreTokens()) {
						final String c = token.nextToken();
						int ind = -1;
						if (this.isDateColumn(this.getColumnClassForColumn(c))) {
							ind = this.getColumnIndex(this.getVirtualColumnForOrdering(this.getVirtualColumn(c)),
									this.getDataModelWithVirtualColumns());
						} else {
							ind = this.getColumnIndex(c, this.getDataModelWithVirtualColumns());
						}
						if (ind >= 0) {
							sorter.sortByColumn(ind, ascending);
						}
					}
				}
				return sorter;
			}
		}
		return this.reportDialog.getModel();
	}

	/**
	 * This method is used to order a table model. The table model is ordered when exists groups in
	 * reports or when user presses ascending-descending order in report dialog (a-z pop-up menu).
	 * @param ascending condition to ascending-descending order.
	 * @return the <code>com.ontimize.report.TableSorter</code> with order
	 */
	public TableSorter getOrderedDataModel(final boolean ascending) {
		final List colsGrupos = this.reportDialog.getSelectedGroupColumns();
		final TableSorter sorter = new TableSorter(this.getDataModelWithVirtualColumns());
		if (colsGrupos != null) {
			if (!colsGrupos.isEmpty()) {
				for (int i = 0; i < colsGrupos.size(); i++) {
					final Object oColumn = colsGrupos.get(i);
					final StringTokenizer token = new StringTokenizer(oColumn.toString(), ",");
					while (token.hasMoreTokens()) {
						final String c = token.nextToken();
						int ind = -1;
						if (this.isDateColumn(this.getColumnClassForColumn(c))) {
							ind = this.getColumnIndex(this.getVirtualColumnForOrdering(this.getVirtualColumn(c)),
									this.getDataModelWithVirtualColumns());
						} else {
							ind = this.getColumnIndex(c, this.getDataModelWithVirtualColumns());
						}
						sorter.sortByColumn(ind, ascending);
					}
				}
			}
		}
		if ((this.reportDialog.getOrderCols() != null) && !this.reportDialog.getOrderCols().isEmpty()) {
			for (int j = 0; j < this.reportDialog.getOrderCols().size(); j++) {
				final Object o = this.reportDialog.getOrderCols().get(j);
				if (o instanceof String) {
					int index = -1;
					if (this.isDateColumn(this.getColumnClassForColumn(o.toString()))) {
						index = this.getColumnIndex(
								this.getVirtualColumnForOrdering(this.getVirtualColumn(o.toString())),
								this.getDataModelWithVirtualColumns());
					} else {
						index = this.getColumnIndex(o.toString(), this.getDataModelWithVirtualColumns());
					}
					sorter.sortByColumn(index, true);
				} else if (o instanceof SelectableItemOrder) {
					final SelectableItemOrder item = (SelectableItemOrder) o;
					int iIndex = -1;
					if (this.isDateColumn(this.getColumnClassForColumn(item.getText()))) {
						iIndex = this.getColumnIndex(
								this.getVirtualColumnForOrdering(this.getVirtualColumn(item.getText())),
								this.getDataModelWithVirtualColumns());
					} else {
						iIndex = this.getColumnIndex(item.getText(), this.getDataModelWithVirtualColumns());
					}
					if (iIndex >= 0) {
						sorter.sortByColumn(iIndex, item.getOrder());
					} else {
						DynamicJasperEngine.logger.warn("COLUMN DOES NOT EXIST IN TABLEMODEL -> " + item.getText());
					}
				}
			}
		}
		return sorter;
	}

	/**
	 * Convenience method.
	 */
	@Override
	public String getTitle() {
		return this.getPageTitle();
	}

	@Override
	public void setDefaultReportDialog(final Object reportDialog) {
		this.reportDialog = (DefaultReportDialog) reportDialog;
		// Dynamic report builder is instanced always because there are not way
		// to reset it.
		this.drb = new CustomDynamicReportBuilder();
		if (this.viewer == null) {
			this.dr = this.drb.build();
			try {
				if (this.model != null) {
					this.jp = DynamicJasperHelper.generateJasperPrint(this.dr, this.getLayoutManager(),
							this.erDataSource, this.parameters);
				}
			} catch (final JRException e) {
				DynamicJasperEngine.logger.error(null, e);
			}
			this.createViewer(this.jp, this.reportDialog);
		}

	}

	@Override
	public void setTitleReport(final String text) {
		if (this.drb != null) {
			this.drb.setTitle(text);
			// refreshOntimizeViewer();
			((CustomJasperViewerToolbar) this.viewer.getToolbar()).title.setText(text);
		}
	}

	@Override
	public void setReportDescription(final String text) {
		if (this.drb != null) {
			((CustomJasperViewerToolbar) this.viewer.getToolbar()).reportDescripcion.setText(text);
		}
	}

	/**
	 * Generates the report again. If
	 * @param
	 */
	@Override
	public void updateReport(final boolean force) {
		try {
			if (this.isSelectedUpdateDynamically()) {
				this.generateReport("", "");
			}
		} catch (final Exception e) {
			DynamicJasperEngine.logger.error(null, e);
		}
	}

	/**
	 * Adds the specified parameters to report. If one parameter
	 * @param parameters
	 */
	public void setParameters(final Map parameters) {
		if (parameters != null) {
			this.parameters.putAll(parameters);
		}
	}

	/**
	 * Returns the parameters that are passed to report.
	 * @return
	 */
	public Map getParameters() {
		return this.parameters;
	}

	/**
	 * Returns a date pattern locale-dependent.
	 * @return the string with date pattern
	 */
	public String getDatePattern() {
		if (ApplicationManager.getLocale().toString().equals("es_ES")) {
			return "dd/MMM/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("en_US")) {
			return "MMM/dd/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("en_GB")) {
			return "dd/MMM/yyyy";
		}
		if (ApplicationManager.getLocale().toString().equals("gl_ES")) {
			return "dd/MMM/yyyy";
		}
		return "MMM/dd/yyyy";
	}

	/**
	 * Sets the parameters that are passed by default to the report:
	 * <ul>
	 * <li><b>JRParameter.REPORT_VIRTUALIZER</b>: This parameter allows to use virtualizers to manage
	 * big reports. It is not sent by default, you must setting "true" with the static variable
	 * useVirtualizerByDefault.
	 * <li><b>currentdate</b>: This parameter includes the current date to print in report footer. It is
	 * sent by default, you can change the static variable isShowedDateInReportFooterByDefault.
	 * <li><b>printcurrentdate</b>: This parameter is a boolean condition to show/hide the currentdate.
	 * <li><b>printNumberPage</b>: This parameter includes the current date to print in report footer.
	 * It is sent by default, you can change this with the static variable
	 * isShowedPageInReportFooterByDefault. The number of page is not sent by program in a parameter
	 * because it is calculated with a variable ($V) from jasperreport library.
	 * <li><b>translationPage</b>: This parameter includes the translation of word "Page" to show
	 * <i>Page</i> X of Y
	 * <li><b>translationOf</b>: This parameter includes the translation of word "of" to show Page X
	 * <i>of</i> Y
	 * <li><b>printTotalNumberOcurrences</b>: This parameter is a boolean condition to show/hide the
	 * phrase "number of ocurrences". It is only send when isSelectedNumberOfOccurrences() returns true
	 * (CheckDataField selected in report dialog).
	 * <li><b>translationNumberOcurrences</b>: This parameter includes the translation of phrase "number
	 * of occurrences".
	 * <li><b>title</b>: This parameter includes the title for report.
	 * <li><b>printitle</b>: This parameter includes the condition to show/hide the title in report.
	 * <li><b>subtitle</b>: This parameter includes the subtitle for report.
	 * <li><b>printsubtitle</b>: This parameter includes the condition to show/hide the subtitle in
	 * report
	 * </ul>
	 *
	 */
	public void setDefaultParameters() {
		if (this.isUsedVirtualizer()) {
			// Pass virtualizer object throw parameter map
			if (this.virtualizer == null) {
				this.virtualizer = new JRFileVirtualizer(DynamicJasperEngine.defaultVirtualizerCacheSize,
						System.getProperty("java.io.tmpdir"));
			}
			if (!this.parameters.containsKey(JRParameter.REPORT_VIRTUALIZER)) {
				this.parameters.put(JRParameter.REPORT_VIRTUALIZER, this.virtualizer);
			}
		}
		if (this.isShowedDateInReportFooter) {
			final SimpleDateFormat sdf = new SimpleDateFormat(this.getDatePattern());
			this.parameters.put(new String("currentdate"), sdf.format(new Date()));
			this.parameters.put(new String("printcurrentdate"), new Boolean(true));
		} else {
			this.parameters.put(new String("currentdate"), new String(""));
			this.parameters.put(new String("printcurrentdate"), new Boolean(false));
		}

		if (this.isShowedPageInReportFooter()) {
			this.parameters.put(new String("printNumberPage"), new Boolean(true));
			this.parameters.put(new String("translationPage"), new String(
					ApplicationManager.getTranslation("DynamicJasperEngine.Page", this.reportDialog.getBundle())));
			this.parameters.put(new String("translationOf"), new String(
					ApplicationManager.getTranslation("DynamicJasperEngine.Of", this.reportDialog.getBundle())));
		} else {
			this.parameters.put(new String("printNumberPage"), new Boolean(false));
			this.parameters.put(new String("translationPage"), new String(""));
			this.parameters.put(new String("translationOf"), new String(""));
		}

		if (this.isShowedTitle()) {
			this.parameters.put(new String("printtitle"), new Boolean(true));
			this.parameters.put(new String("title"),
					((CustomJasperViewerToolbar) this.viewer.getToolbar()).getTitleField().getText());
		} else {
			this.parameters.put(new String("printtitle"), new Boolean(false));
			this.parameters.put(new String("title"), new String(""));
		}

		if (this.isShowedSubtitle()) {
			this.parameters.put(new String("printsubtitle"), new Boolean(true));
			this.parameters.put(new String("subtitle"),
					((CustomJasperViewerToolbar) this.viewer.getToolbar()).getDescriptionField().getText());
		} else {
			this.parameters.put(new String("printsubtitle"), new Boolean(false));
			this.parameters.put(new String("subtitle"), new String(""));
		}

		if (this.isSelectedNumberOfOccurrences()) {
			this.parameters.put(new String("printTotalNumberOcurrences"), new Boolean(true));
			this.parameters.put(new String("translationNumberOcurrences"),
					new String(" Total "
							+ ApplicationManager.getTranslation("NumeroDeOcurrencias", this.reportDialog.getBundle())));
		} else {
			this.parameters.put(new String("printTotalNumberOcurrences"), new Boolean(false));
			this.parameters.put(new String("translationNumberOcurrences"), new String(""));
		}

		if (!DynamicJasperEngine.useTemplatesWithDefaultLogo && (DynamicJasperEngine.reportLogoPath != null)) {
			this.parameters.put(new String("printCustomLogo"), new Boolean(true));
			this.parameters.put(new String("customLogo"), DynamicJasperEngine.reportLogoPath);
		} else {
			this.parameters.put(new String("printCustomLogo"), new Boolean(false));
			this.parameters.put(new String("customLogo"), "");
		}
	}

	/**
	 * Returns true when a virtualizer is used.
	 * @return the condition
	 */
	public boolean isUsedVirtualizer() {
		return this.isUsedVirtualizer;
	}

	/**
	 * Allows to modify the variable that manages the use of virtualizers.
	 * @param isUsedVirtualizer Condition to use virtualizers
	 */
	public void setIsUsedVirtualizer(final boolean isUsedVirtualizer) {
		this.isUsedVirtualizer = isUsedVirtualizer;
	}

	/**
	 * Checks whether date in report is showed.
	 * @return true When data in report is showed
	 */
	public boolean isShowedDateInReportFooter() {
		return this.isShowedDateInReportFooter;
	}

	/**
	 * Allows to modify the variable that shows date in report footer.
	 * @param isShowedDateInReportFooter the variable to show/hide date in report footer
	 */
	public void setShowedDateInReportFooter(final boolean isShowedDateInReportFooter) {
		this.isShowedDateInReportFooter = isShowedDateInReportFooter;
	}

	/**
	 * Checks whether page in report footer is showed.
	 * @return true When page in report footer is showed
	 */
	public boolean isShowedPageInReportFooter() {
		return this.isShowedPageInReportFooter;
	}

	/**
	 * Checks whether page in report footer is showed.
	 * @return true When page in report footer is showed
	 */
	public boolean isShowedTitle() {
		return this.isShowedTitle;
	}

	/**
	 * Checks whether page in report footer is showed.
	 * @return true When page in report footer is showed
	 */
	public boolean isShowedSubtitle() {
		return this.isShowedSubtitle;
	}

	/**
	 * Allows to modify the variable that shows page in report footer.
	 * @param isShowedPageInReportFooter The variable to show/hide page in report footer
	 */
	public void setShowedPageInReportFooter(final boolean isShowedPageInReportFooter) {
		this.isShowedPageInReportFooter = isShowedPageInReportFooter;
	}

	/**
	 * Creates the jasper viewer to show in report dialog. Sets the default zoom ratio and adds focus
	 * adapters for title and subtitle in toolbar.
	 * @param jp the jasperprint object
	 * @param reportDialog the dialog where viewer is added
	 */
	public void createViewer(final JasperPrint jp, final DefaultReportDialog reportDialog) {
		this.viewer = new CustomJasperViewer(jp, reportDialog);
		// viewer.set
		this.viewer.setZoomRatio((float) ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getZoom());
		this.viewer.getStatusBar().removeAll();
		reportDialog.getChartPanel().add(this.viewer);
		reportDialog.getChartPanel().revalidate();
		this.viewer.getViewerController().reload();
		this.viewer.getViewerController().refreshPage();

		if (!((CustomJasperViewerToolbar) this.viewer.getToolbar()).isfocusAdapterAdded()) {
			((CustomJasperViewerToolbar) this.viewer.getToolbar()).setFocusAdapters(reportDialog);
			((CustomJasperViewerToolbar) this.viewer.getToolbar()).setFocusAdapterAdded(true);
		}

		((CustomJasperViewerToolbar) this.viewer.getToolbar()).setDefaultReportDialog(reportDialog);
	}

	/**
	 * Refreshes the viewer. Rebuilds the report, generates the jasper print and send it to viewer.
	 */
	public void refreshOntimizeViewer() {
		this.dr = this.drb.build();
		try {
			this.jp = DynamicJasperHelper.generateJasperPrint(this.dr, this.getLayoutManager(),
					new TableModelDataSource(this.sortermodel), this.parameters);
		} catch (final JRException e) {
			DynamicJasperEngine.logger.error(null, e);
		}
		this.reportDialog.getChartPanel().removeAll();
		this.reportDialog.getChartPanel().add(this.viewer);
		this.viewer.getViewerController().loadReport(this.jp);
		this.viewer.getViewerController().reload();
		this.viewer.getViewerController().refreshPage();
		this.viewer.setZoomRatio((float) ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getZoom());
	}

	/**
	 * Refreshes the viewer for a specific jasper print passed in parameter.
	 */
	public void refreshOntimizeViewer(final JasperPrint jp) {
		this.reportDialog.getChartPanel().removeAll();
		this.reportDialog.getChartPanel().add(this.viewer);
		this.viewer.getViewerController().loadReport(jp);
		this.viewer.getViewerController().refreshPage();
		this.viewer.setZoomRatio((float) ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getZoom());
	}

	/**
	 * Returns the default templates. If exists property: <code>com.ontimize.report.templates</code>
	 * these templates will be used. Otherwise, default templates contain default parameters and default
	 * logo. To use a empty template (without logo), user can set to "false" the static variable
	 * useTemplatesWithDefaultLogo.
	 * @return the <code>List</code> with default templates.
	 */
	@Override
	public List getDefaultTemplates() {
		final List v = new Vector();
		final String property = System.getProperty("com.ontimize.report.templates");
		if ((property != null) && (property.length() > 0)) {
			v.addAll(ApplicationManager.getTokensAt(property, ";"));
			return v;
		}
		if (ReportUtils.getCustomReportTemplates().size() != 0) {
			return ReportUtils.getCustomReportTemplates();
		}
		if (DynamicJasperEngine.useTemplatesWithDefaultLogo) {
			v.add(DynamicJasperEngine.DEFAULT_JRXML_TEMPLATE_PORTRAIT);
			v.add(DynamicJasperEngine.DEFAULT_JRXML_TEMPLATE_LANDSCAPE);
		} else {
			v.add(DynamicJasperEngine.DEFAULT_JRXML_TEMPLATE_PORTRAIT_NO_LOGO);
			v.add(DynamicJasperEngine.DEFAULT_JRXML_TEMPLATE_LANDSCAPE_NO_LOGO);
		}
		return v;
	}

	/**
	 * Sets the template file in report and sets all styles.
	 */
	public void setReportStyles() {
		final Object fontSizeValue = ((CustomJasperViewerToolbar) this.viewer.getToolbar()).getFontSizeCombo()
				.getSelectedItem();
		if (fontSizeValue instanceof Integer) {
			this.setFontSize((Integer) fontSizeValue);
		} else {
			this.setFontSize(9);
		}

		this.drb.setWhenNoDataBlankPage();
		if (!this.existGroups() && this.isSelectedPrintColumnNames()) {
			this.drb.setPrintColumnNames(true);
		} else {
			this.drb.setPrintColumnNames(false);
		}
		this.setColumnHeaderStyle();
		this.setTitleandSubtitleStyle();
		this.setColumnDataStyle();
		this.setColumnHeaderForGroupsStyle();
		this.setColumnHeaderForMultiGroupsStyle();
		this.setFooterVariableStyle();
		this.setGroupFooterVariableStyle();
		this.setGroupFooterNumberOcurrencesVariableStyle();
		this.drb.setDefaultStyles(this.defaultTitleStyle, this.defaultSubtitleStyle, this.defaultHeaderStyle,
				this.defaultColumnDataStyle);
		this.drb.setPrintBackgroundOnOddRows(true);
		this.setRowReportSyles();
	}

	/**
	 * Sets the styles for title and subtitle.
	 */
	public void setTitleandSubtitleStyle() {
		this.defaultSubtitleStyle = new Style();
		this.defaultSubtitleStyle.setFont(this.getDefaultSubtitleFont());
		this.defaultSubtitleStyle.setTextColor(DynamicJasperStyles.defaultSubtitleFontColor);
		this.defaultSubtitleStyle.setBackgroundColor(DynamicJasperStyles.defaultSubtitleBackgroundColor);
		this.defaultSubtitleStyle.setTransparency(DynamicJasperStyles.defaultSubtitleTransparency);

		this.defaultTitleStyle = new Style();
		this.defaultTitleStyle.setFont(this.getDefaultTitleFont());
		this.defaultTitleStyle.setTextColor(DynamicJasperStyles.defaultTitleFontColor);
		this.defaultTitleStyle.setBackgroundColor(DynamicJasperStyles.defaultTitleBackgroundColor);
		this.defaultTitleStyle.setTransparency(DynamicJasperStyles.defaultTitleTransparency);
	}

	/**
	 * Sets the style for columns showed in header of report.
	 */
	public void setColumnHeaderStyle() {
		this.defaultHeaderStyle = new Style();
		this.defaultHeaderStyle.setFont(this.getDefaultHeaderFont());
		this.defaultHeaderStyle.setPaddingBottom(new Integer(DynamicJasperStyles.defaultHeaderPaddingBottom));
		this.defaultHeaderStyle.setPaddingTop(new Integer(DynamicJasperStyles.defaultHeaderPaddingTop));
		this.defaultHeaderStyle.setHorizontalAlign(DynamicJasperStyles.defaultHeaderHorizontalAlignment);
		this.defaultHeaderStyle.setBorderBottom(DynamicJasperStyles.defaultHeaderBorderBottom);
		this.defaultHeaderStyle.setVerticalAlign(DynamicJasperStyles.defaultHeaderVerticalAlignment);
		this.defaultHeaderStyle.setBackgroundColor(DynamicJasperStyles.defaultHeaderBackgroundColor);
		this.defaultHeaderStyle.setTransparency(DynamicJasperStyles.defaultHeaderTransparency);
	}

	/**
	 * Sets the style for report layout: font, padding, alignment.
	 */
	public void setColumnDataStyle() {
		this.defaultColumnDataStyle = new Style();
		this.defaultColumnDataStyle.setFont(this.getDefaultColumnDataFont());
		this.defaultColumnDataStyle.setTextColor(DynamicJasperStyles.defaultColumnDataFontColor);
		this.defaultColumnDataStyle.setPaddingBottom(new Integer(DynamicJasperStyles.defaultColumnDataPaddingBottom));
		this.defaultColumnDataStyle.setPaddingTop(new Integer(DynamicJasperStyles.defaultColumnDataPaddingTop));
		this.defaultColumnDataStyle.setPaddingLeft(new Integer((this.reportDialog.getSelectedGroupItems().size() * 5)
				+ DynamicJasperStyles.defaultColumnDataPaddingLeft));
		this.defaultColumnDataStyle.setPaddingRight(new Integer(DynamicJasperStyles.defaultColumnDataPaddingRight));
		this.defaultColumnDataStyle.setHorizontalAlign(DynamicJasperStyles.defaultColumnDataHorizontalAlignment);
		this.defaultColumnDataStyle.setBorderBottom(DynamicJasperStyles.defaultColumnDataBorderBottom);
		this.defaultColumnDataStyle.setBorderTop(DynamicJasperStyles.defaultColumnDataBorderTop);
		this.defaultColumnDataStyle.setBorderLeft(DynamicJasperStyles.defaultColumnDataBorderLeft);
		this.defaultColumnDataStyle.setBorderRight(DynamicJasperStyles.defaultColumnDataBorderRight);
		this.defaultColumnDataStyle.setVerticalAlign(DynamicJasperStyles.defaultColumnDataVerticalAlignment);
		this.defaultColumnDataStyle.setTransparency(DynamicJasperStyles.defaultColumnDataTransparency);
	}

	/**
	 * Sets the style for report layout: font, padding, alignment.
	 */
	public Style createColumnDataStyle(final String columnName, final Class columnClass) {
		final Style columnDataStyle = new Style();
		columnDataStyle.setFont(this.getDefaultColumnDataFont());
		columnDataStyle.setTextColor(DynamicJasperStyles.defaultColumnDataFontColor);
		columnDataStyle.setPaddingBottom(new Integer(DynamicJasperStyles.defaultColumnDataPaddingBottom));
		columnDataStyle.setPaddingTop(new Integer(DynamicJasperStyles.defaultColumnDataPaddingTop));
		columnDataStyle.setPaddingLeft(new Integer((this.reportDialog.getSelectedGroupItems().size() * 5)
				+ DynamicJasperStyles.defaultColumnDataPaddingLeft));
		columnDataStyle.setPaddingRight(new Integer(DynamicJasperStyles.defaultColumnDataPaddingRight));

		if (this.reportDialog.getConfiguredColumns().contains(columnName)
				&& this.reportDialog.getColumnAlignment().containsKey(columnName)) {
			final Integer alignment = this.reportDialog.getColumnAlignment().get(columnName);
			switch (alignment) {
			case StyleConstants.ALIGN_LEFT:
				columnDataStyle.setHorizontalAlign(HorizontalAlign.LEFT);
				break;
			case StyleConstants.ALIGN_CENTER:
				columnDataStyle.setHorizontalAlign(HorizontalAlign.CENTER);
				break;
			case StyleConstants.ALIGN_RIGHT:
				columnDataStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
				break;
			default:
				columnDataStyle.setHorizontalAlign(DynamicJasperStyles.defaultColumnDataHorizontalAlignment);
				break;
			}
		} else {
			if (columnClass.isAssignableFrom(String.class)) {
				columnDataStyle.setHorizontalAlign(HorizontalAlign.LEFT);
			} else if (this.isDateColumn(columnClass) || this.isImageColumn(columnClass)
					|| this.isBooleanImageColumn(columnClass)) {
				columnDataStyle.setHorizontalAlign(HorizontalAlign.CENTER);
			} else {
				columnDataStyle.setHorizontalAlign(DynamicJasperStyles.defaultColumnDataHorizontalAlignment);
			}
		}

		if (this.isSelectedShowGrid()) {
			columnDataStyle.setBorderBottom(DynamicJasperStyles.defaultColumnGridDataBorder);
			columnDataStyle.setBorderTop(DynamicJasperStyles.defaultColumnGridDataBorder);
			columnDataStyle.setBorderLeft(DynamicJasperStyles.defaultColumnGridDataBorder);
			columnDataStyle.setBorderRight(DynamicJasperStyles.defaultColumnGridDataBorder);
		} else {
			columnDataStyle.setBorderBottom(DynamicJasperStyles.defaultColumnDataBorderBottom);
			columnDataStyle.setBorderTop(DynamicJasperStyles.defaultColumnDataBorderTop);
			columnDataStyle.setBorderLeft(DynamicJasperStyles.defaultColumnDataBorderLeft);
			columnDataStyle.setBorderRight(DynamicJasperStyles.defaultColumnDataBorderRight);
		}
		if (this.isImageColumn(columnClass) || this.isBooleanImageColumn(columnClass)) {
			columnDataStyle.setVerticalAlign(VerticalAlign.MIDDLE);
		} else {
			columnDataStyle.setVerticalAlign(DynamicJasperStyles.defaultColumnDataVerticalAlignment);
		}
		columnDataStyle.setTransparency(DynamicJasperStyles.defaultColumnDataTransparency);
		return columnDataStyle;
	}

	/**
	 * Sets the sytle for elements that are showed in groups header. For example, if we are grouping by
	 * column City and in these group city is Barcelona, these styles will be applied to group header,
	 * in this case to: City Barcelona
	 */
	public void setColumnHeaderForGroupsStyle() {
		this.defaultHeaderForGroupStyle = new Style();
		this.defaultHeaderForGroupStyle.setFont(this.getDefaultHeaderForGroupFont());
		this.defaultHeaderForGroupStyle.setTextColor(DynamicJasperStyles.defaultHeaderForGroupFontColor);
		this.defaultHeaderForGroupStyle
		.setPaddingBottom(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingBottom));
		this.defaultHeaderForGroupStyle.setPaddingTop(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingTop));
		this.defaultHeaderForGroupStyle
		.setPaddingLeft(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingLeft));
		this.defaultHeaderForGroupStyle
		.setPaddingRight(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingRight));
		this.defaultHeaderForGroupStyle
		.setHorizontalAlign(DynamicJasperStyles.defaultHeaderForGroupHorizontalAlignmentValues);
		this.defaultHeaderForGroupStyle.setBorderBottom(DynamicJasperStyles.defaultHeaderForGroupBorderBottom);
		this.defaultHeaderForGroupStyle.setBorderTop(DynamicJasperStyles.defaultHeaderForGroupBorderTop);
		this.defaultHeaderForGroupStyle.setBorderLeft(DynamicJasperStyles.defaultHeaderForGroupBorderLeft);
		this.defaultHeaderForGroupStyle.setBorderRight(DynamicJasperStyles.defaultHeaderForGroupBorderRight);
		this.defaultHeaderForGroupStyle.setVerticalAlign(DynamicJasperStyles.defaultHeaderForGroupVerticalAlignment);
		this.defaultHeaderForGroupStyle.setBackgroundColor(DynamicJasperStyles.defaultHeaderForGroupBackgroundColor);
		this.defaultHeaderForGroupStyle.setTransparency(DynamicJasperStyles.defaultHeaderForGroupTransparency);
	}

	/**
	 * Sets the sytle for elements that are showed in groups header. For example, if we are grouping by
	 * column City and in these group city is Barcelona, these styles will be applied to group header,
	 * in this case to: City Barcelona
	 */
	public void setColumnHeaderForMultiGroupsStyle() {
		this.defaultHeaderForMultiGroupStyle = new Style();
		this.defaultHeaderForMultiGroupStyle.setFont(this.getDefaultHeaderForGroupFont());
		this.defaultHeaderForMultiGroupStyle.setTextColor(DynamicJasperStyles.defaultHeaderForGroupFontColor);
		this.defaultHeaderForMultiGroupStyle
		.setPaddingBottom(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingBottom));
		this.defaultHeaderForMultiGroupStyle
		.setPaddingTop(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingTop));
		this.defaultHeaderForMultiGroupStyle
		.setPaddingLeft(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingLeft));
		this.defaultHeaderForMultiGroupStyle
		.setPaddingRight(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingRight));
		this.defaultHeaderForMultiGroupStyle
		.setHorizontalAlign(DynamicJasperStyles.defaultHeaderForGroupHorizontalAlignmentValues);
		this.defaultHeaderForMultiGroupStyle
		.setBorderBottom(DynamicJasperStyles.defaultHeaderForMultiGroupBorderBottom);
		this.defaultHeaderForMultiGroupStyle.setBorderTop(DynamicJasperStyles.defaultHeaderForMultiGroupBorderTop);
		this.defaultHeaderForMultiGroupStyle.setBorderLeft(DynamicJasperStyles.defaultHeaderForMultiGroupBorderLeft);
		this.defaultHeaderForMultiGroupStyle.setBorderRight(DynamicJasperStyles.defaultHeaderForMultiGroupBorderRight);
		this.defaultHeaderForMultiGroupStyle
		.setVerticalAlign(DynamicJasperStyles.defaultHeaderForGroupVerticalAlignment);
		this.defaultHeaderForMultiGroupStyle
		.setBackgroundColor(DynamicJasperStyles.defaultHeaderForGroupBackgroundColor);
		this.defaultHeaderForMultiGroupStyle.setTransparency(DynamicJasperStyles.defaultHeaderForGroupTransparency);
	}

	/**
	 * Sets the style for footer variables.
	 */
	public void setFooterVariableStyle() {
		this.styleFooterVariable = new Style();
		this.styleFooterVariable.setBackgroundColor(DynamicJasperStyles.defaultFooterVariableBackgroundColor);
		this.styleFooterVariable.setTextColor(DynamicJasperStyles.defaultFooterVariableTextColor);
		this.styleFooterVariable.setHorizontalAlign(DynamicJasperStyles.defaultFooterVariableHorizontalAlignment);
		this.styleFooterVariable.setTransparency(DynamicJasperStyles.defaultFooterVariableTransparency);
		this.styleFooterVariable.setBorderTop(DynamicJasperStyles.defaultFooterVariableBorderTop);
		this.styleFooterVariable.setFont(this.getDefaultFooterVariableFont());
	}

	/**
	 * Sets style for variables in group footer.
	 */
	public void setGroupFooterVariableStyle() {
		this.styleGroupFooterVariable = new Style();
		this.styleGroupFooterVariable.setBackgroundColor(DynamicJasperStyles.defaultGroupFooterVariableBackgroundColor);
		this.styleGroupFooterVariable.setTextColor(DynamicJasperStyles.defaultGroupFooterVariableTextColor);
		this.styleGroupFooterVariable
		.setHorizontalAlign(DynamicJasperStyles.defaultGroupFooterVariableHorizontalAlignment);
		this.styleGroupFooterVariable.setTransparency(DynamicJasperStyles.defaultGroupFooterVariableTransparency);
		this.styleGroupFooterVariable.setFont(this.getDefaultGroupFooterVariableFont());

	}

	/**
	 * Sets style for variables in group footer.
	 */
	public void setGroupFooterNumberOcurrencesVariableStyle() {
		this.styleGroupFooterNumberOcurrencesVariable = new Style();
		this.styleGroupFooterNumberOcurrencesVariable
		.setBackgroundColor(DynamicJasperStyles.defaultGroupFooterNumberOcurrencesVariableBackgroundColor);
		this.styleGroupFooterNumberOcurrencesVariable
		.setTextColor(DynamicJasperStyles.defaultGroupFooterNumberOcurrencesVariableTextColor);
		this.styleGroupFooterNumberOcurrencesVariable
		.setHorizontalAlign(DynamicJasperStyles.defaultGroupFooterNumberOcurrencesVariableHorizontalAlignment);
		this.styleGroupFooterNumberOcurrencesVariable
		.setTransparency(DynamicJasperStyles.defaultGroupFooterNumberOcurrencesVariableTransparency);
		this.styleGroupFooterNumberOcurrencesVariable.setFont(this.getDefaultGroupFooterNumberOcurrencesVariableFont());

	}

	/**
	 * Sets the background color for odd and even rows.
	 */
	public void setRowReportSyles() {
		this.oddRowBackgroundStyle = new Style();
		this.oddRowBackgroundStyle.setBackgroundColor(DynamicJasperStyles.defaultOddColumnBackgroundColor);
		this.drb.setOddRowBackgroundStyle(this.oddRowBackgroundStyle);
	}

	/**
	 * Builds the report footer. Footer by default is built with parameters (
	 * {@link #setDefaultParameters()} passed to report, so this method is empty in this implementation
	 */
	public void buildReportFooter() {
	}

	/**
	 * Builds the report header. Sets the report logo when is not used the default and sets title and
	 * subtitle.
	 */
	public void buildReportHeader() {
		this.setReportLogo();
		this.drb.setHeaderHeight(10);
		if ((this.reportDialog.getSelectedPrintingColumns().size() == 0)
				&& (this.reportDialog.getSelectedGroupColumns().size() == 0) && (this.reportDialog
						.getSelectedFunctionColumns()
						.size() == 0)) {
			// ((CustomJasperViewerToolbar)this.viewer.getToolbar()).getTitleField().setText("");
			// ((CustomJasperViewerToolbar)this.viewer.getToolbar()).getDescriptionField().setText("");
			this.drb.setTitle("");
			this.drb.setSubtitle("");
		}
	}

	/**
	 * Build the report detail: columns and groups.
	 * @throws Exception when an <code>Exception</code> is thrown.
	 */
	public void buildReportDetail() throws Exception {
		this.buildColumns();
		if (this.existGroups()) {
			this.buildGroups();
		}

	}

	protected void configureColumnRenders() {
		for (int i = 0; i < this.reportDialog.getModel().getColumnCount(); i++) {
			if ((this.reportDialog.getTable() != null) && (this.model != null)) {
				// TableCellRenderer renderer =
				// reportDialog.getTable().getJTable().getCellRenderer(0,
				// reportDialog.getTable().getColumnIndex(this.model.getColumnName(i)));
				// since 5.4.0
				TableCellRenderer renderer = this.reportDialog.getTable()
						.getRendererForColumn(this.model.getColumnName(i));
				if (renderer == null) {
					renderer = this.reportDialog.getTable()
							.getJTable()
							.getDefaultRenderer(this.model.getColumnClass(i));
				}
				if (renderer != null) {
					final String columnPattern = this.getColumnPatternFromRenderer(renderer);
					if (columnPattern != null) {
						this.hRenderColumns.put(this.model.getColumnName(i), columnPattern);
					}
				}
			}
		}
	}

	public String getPatternForColumn(String columnName) {
		if (this.isVirtualColumn(columnName)) {
			columnName = this.getColumnFromVirtualColumn(columnName);
		}
		if (this.isDateColumn(this.getColumnClassForColumn(columnName))
				&& (this.hRenderColumns.get(columnName) == null)) {
			return this.getDatePattern();
		}
		return this.hRenderColumns.get(columnName) == null ? null : this.hRenderColumns.get(columnName).toString();
	}

	protected String getColumnPatternFromRenderer(final TableCellRenderer rendererColumn) {
		if (rendererColumn instanceof DateCellRenderer) {
			return this.createDatePattern((DateCellRenderer) rendererColumn);
		}
		if (rendererColumn instanceof CurrencyCellRenderer) {
			return this.createDecimalPattern((CurrencyCellRenderer) rendererColumn) + " "
					+ ((CurrencyCellRenderer) rendererColumn).getCurrencySymbol();
		}
		if (rendererColumn instanceof RealCellRenderer) {
			return this.createDecimalPattern((RealCellRenderer) rendererColumn);
		}

		if (rendererColumn instanceof PercentCellRenderer) {
			return this.createDecimalPattern((PercentCellRenderer) rendererColumn);
		}
		return null;
	}

	public String createDatePattern(final DateCellRenderer rendererColumn) {
		if (rendererColumn.isWithHour()) {
			if (rendererColumn.isHourOnly()) {
				return rendererColumn.getHourPattern();
			} else {
				if (rendererColumn.isHourInFirstPlace()) {
					return rendererColumn.getHourPattern() + " " + rendererColumn.getDatePattern();
				} else {
					return rendererColumn.getDatePattern() + " " + rendererColumn.getHourPattern();
				}
			}
		} else {
			return rendererColumn.getDatePattern();
		}
	}

	public String createDecimalPattern(final PercentCellRenderer rendererColumn) {
		final NumberFormat format = (NumberFormat) rendererColumn.getFormat();
		final StringBuilder patter = new StringBuilder();
		if (format.isGroupingUsed()) {
			patter.append("#,##0");
		}
		if (format.getMaximumFractionDigits() > 0) {
			patter.append(".");
			int fractionDigits = format.getMinimumFractionDigits();
			for (int i = 0; i < fractionDigits; i++) {
				patter.append("0");
			}

			for (; fractionDigits < format.getMaximumFractionDigits(); fractionDigits++) {
				patter.append("#");
			}
		}
		patter.append("%");
		return patter.toString();
	}

	public String createDecimalPattern(final RealCellRenderer rendererColumn) {
		final NumberFormat format = (NumberFormat) rendererColumn.getFormat();
		final StringBuilder patter = new StringBuilder();
		if (format.isGroupingUsed()) {
			patter.append("#,##0");
		}
		if (format.getMaximumFractionDigits() > 0) {
			patter.append(".");
			int fractionDigits = format.getMinimumFractionDigits();
			for (int i = 0; i < fractionDigits; i++) {
				patter.append("0");
			}

			for (; fractionDigits < format.getMaximumFractionDigits(); fractionDigits++) {
				patter.append("#");
			}
		}
		return patter.toString();
		// char c = '0';
		// char[] cDecimal = new char[(format.getMinimumIntegerDigits() > 20) ?
		// 20 : format.getMinimumIntegerDigits()];
		// Arrays.fill(cDecimal, c);
		// int lengthFractionDigits = (format.getMinimumFractionDigits() == 0) ?
		// 2 : format.getMinimumFractionDigits();
		// char[] cFraction = new char[lengthFractionDigits];
		// Arrays.fill(cFraction, c);
		// return String.valueOf(cFraction).length() == 0 ?
		// String.valueOf(cDecimal) : String.valueOf(cDecimal) + "." +
		// String.valueOf(cFraction);
		// return "#,##0.##";
	}

	/**
	 * Fixes in report the selected template.
	 * @throws Exception When template is not selected.
	 */
	public void configureTemplate() throws Exception {
		if (this.reportDialog.getTemplateCombo().getSelectedItem().toString() != null) {
			this.drb.setTemplateFile(this.reportDialog.getTemplateCombo().getSelectedItem().toString(), true, true,
					true, true);
		} else {
			throw new Exception(this.getClass() + " :Template is required.");
		}
	}

	/**
	 * This method gets values to add the row number in report.
	 * @return the expression
	 */
	public CustomExpression getExpressionForRowNumbers() {
		return new CustomExpression() {

			@Override
			public Object evaluate(final Map fields, final Map variables, final Map parameters) {
				return variables.get("REPORT_COUNT");
			}

			@Override
			public String getClassName() {
				return Integer.class.getName();
			}
		};
	}

	/**
	 * It is the method called to build columns in report
	 * @throws Exception when an <code>Exception</code> occurs
	 */
	public void buildColumns() throws Exception {

		// We must iterate for all possible printing columns
		for (int i = 0; i < this.reportDialog.getPrintingColumns().size(); i++) {

			final String columnName = this.reportDialog.getPrintingColumns().get(i).toString();
			final Class columnClass = this.getColumnClassForColumn(columnName);

			if (this.isShowedRowNumber() && (i == 0)) {
				// Create the row that shows number of column
				this.createRowNumberColumn();
			}

			this.configureTemplate();
			// Store the original position for column
			this.keepOriginalColumnPositions(i);

			ColumnBuilder cb = ColumnBuilder.getNew();
			cb = this.configureColumnBuilder(cb, columnName, columnClass);

			// Builds the column from column builder
			AbstractColumn column = cb.build();

			// Configure the column
			column = this.configureColumn(column, columnName, columnClass);

			if (this.reportDialog.getSelectedPrintingColumns().contains(columnName)) {
				// When column is printed we must add a column to report
				this.drb.addColumn(column);
			} else {
				// when column is not printed in report we must register a field
				// for this column because could be create a group with this
				// column
				// or show a function column. Fields are only used internally by
				// jasperreports but not showed in report
				this.drb = this.registerField(columnName, columnClass);
			}
			// For date columns, we must create a virtual column that allows to
			// group by date. This column will be hidden (width = 0) but it is
			// used for calculation
			if (this.isDateColumn(columnClass)) {
				this.drb = this.configureVirtualColumn(this.drb, columnName, columnClass);
			}

			// Adds function for these column columns when not exists groups,
			// one row at the end of the report
			// Builds the footer of report.
			if (this.existFunctions() && this.isNumericClass(columnClass)) {
				// column = setPatterns(column,columnClass);
				this.applyFunctionForColumn(column, null);
			}
		}
	}

	/**
	 * Create and register a hidden virtual column for dates.
	 * @param drb
	 * @param columnName
	 * @param columnClass
	 * @return
	 * @throws Exception
	 */
	public CustomDynamicReportBuilder configureVirtualColumn(final CustomDynamicReportBuilder drb, final String columnName,
			final Class columnClass) throws Exception {
		final ColumnBuilder cbVirtual = ColumnBuilder.getNew();
		cbVirtual.setColumnProperty(this.getVirtualColumn(columnName), String.class.getName());
		cbVirtual.setTitle(ApplicationManager.getTranslation(columnName, this.reportDialog.getBundle()));
		cbVirtual.setStyle(this.defaultColumnDataStyle);
		AbstractColumn columnVirtual = cbVirtual.build();
		columnVirtual.setName(this.getVirtualColumn(columnName));
		columnVirtual.setTitle("");
		columnVirtual.setWidth(new Integer(0));
		columnVirtual = this.setPatterns(columnVirtual, columnClass);
		if (this.reportDialog.getSelectedPrintingColumns().contains(columnName)) {
			drb.addColumn(columnVirtual);
		} else {
			drb.addField(this.getVirtualColumn(columnName), String.class.getName());
		}
		return drb;
	}

	/**
	 * Register a field in report.
	 * @param columnName The column name referred to field
	 * @param columnClass The class of column
	 * @return the dynamic report with field added
	 */
	public CustomDynamicReportBuilder registerField(final String columnName, final Class columnClass) {
		this.drb.addField(columnName, columnClass.getName());
		return this.drb;
	}

	/**
	 * Set properties for column:
	 *
	 * <ul>
	 * <li>Print Blanks when null values
	 * <li>Name of column
	 * <li>Title of column
	 * <li>Default width for column (It is calculated dynamically)
	 * </ul>
	 * @param column The initial column
	 * @param columnName The column name
	 * @return The column with properties fixed
	 */
	public AbstractColumn configureColumn(AbstractColumn column, final String columnName, final Class columnClass) {
		column.setBlankWhenNull(Boolean.TRUE);
		column.setName(columnName);
		column.setTitle(ApplicationManager.getTranslation(columnName, this.reportDialog.getBundle()));

		column.setTruncateSuffix("...");
		column = this.setPatterns(column, columnClass);
		this.configureColumnWidth(column, columnName, columnClass);
		return column;
	}

	public void configureColumnWidth(final AbstractColumn column, final String columnName, final Class columnClass) {
		if (this.reportDialog.getConfiguredColumns().contains(columnName)) {
			final Map<String, Integer> columnFixedWidth = this.reportDialog.getColumnFixedWidth();
			if (columnFixedWidth.containsKey(columnName)) {
				column.setFixedWidth(true);
				column.setWidth(columnFixedWidth.get(columnName));
			} else {
				column.setFixedWidth(false);
				column.setWidth(70);
			}
		} else {
			// only calculate in visible columns.
			if (this.reportDialog.getSelectedPrintingColumns().contains(columnName)) {
				int titleWidth = FontHelper.getWidthFor(this.getDefaultHeaderFont(), column.getTitle());
				titleWidth = titleWidth + DynamicJasperStyles.defaultHeaderPaddingRight
						+ DynamicJasperStyles.defaultHeaderPaddingLeft + DynamicJasperEngine.WIDTH_OFFSET;
				if (this.isDateColumn(columnClass)) {
					column.setFixedWidth(true);
					final String pattern = column.getPattern();
					final int contentWidth = FontHelper.getWidthFor(this.getDefaultColumnDataFont(), pattern);
					final int width = Math.max(titleWidth, contentWidth);
					column.setWidth(width);
				} else if (this.isNumericClass(columnClass)) {
					column.setFixedWidth(true);
					int contentWidth = 0;
					try {
						if (this.erData.containsKey(columnName)) {
							final List data = (List) this.erData.get(columnName);
							final Object value = Collections.max(data);
							if (value instanceof Number) {
								final String pattern = column.getPattern();
								final DecimalFormat format = new DecimalFormat(pattern);
								final String maxText = format.format(value);
								contentWidth = FontHelper.getWidthFor(this.getDefaultColumnDataFont(), maxText)
										+ DynamicJasperEngine.WIDTH_OFFSET;
							}
						}
					} catch (final Exception ex) {
						DynamicJasperEngine.logger.error("Column name: " + columnName, ex);
					}
					final int width = Math.max(titleWidth, contentWidth);
					column.setWidth(width);
				}
			}
		}
	}

	/**
	 * Sets the property for column builder. This column builder is converted to a column with method
	 * .build():
	 *
	 * <ul>
	 * <li>Sets the column class passed in parameter to column builder taking into account:
	 * <ul>
	 * <li>Sets the column type to image and column builder class to "java.awt.Image" for all image
	 * columns.
	 * <li>Sets the column builder class to "java.util.Date" for "java.sql.date" columns because this
	 * type is not supported in jasper.
	 * </ul>
	 * <li>Name of column
	 * <li>Title of column
	 * <li>Default width for column (It is calculated dynamically)
	 * <li>Default style to be applied to this column builder
	 * </ul>
	 * @param cb The original column builder for this column
	 * @param columnName The name of the column
	 * @param columnClass The class of the column
	 * @return The column builder modified
	 */
	public ColumnBuilder configureColumnBuilder(final ColumnBuilder cb, final String columnName, final Class columnClass) {
		if (this.isBooleanImageColumn(columnClass)) {
			cb.setColumnProperty(columnName, "java.awt.Image");
			cb.setColumnType(ColumnBuilder.COLUMN_TYPE_IMAGE);
			cb.setImageScaleMode(ImageScaleMode.NO_RESIZE);
		} else if (this.isImageColumn(columnClass)) {
			cb.setColumnProperty(columnName, "java.awt.Image");
			cb.setColumnType(ColumnBuilder.COLUMN_TYPE_IMAGE);
		} else {
			// Jasperreports engine not supports java.sql.Date (throws a
			// ValidationException)
			if (columnClass.isAssignableFrom(java.sql.Date.class)) {
				cb.setColumnProperty(columnName, "java.util.Date");
			} else {
				cb.setColumnProperty(columnName, columnClass.getName());
			}
		}
		cb.setTitle(ApplicationManager.getTranslation(columnName, this.reportDialog.getBundle()));
		cb.setStyle(this.createColumnDataStyle(columnName, columnClass));

		cb.setWidth(new Integer(70));
		return cb;
	}

	/**
	 * Stores in <code>Hashtable</code> hColsPositions the original order in columns. This order is
	 * required in layout to be printed correctly.
	 * @param i Index of column
	 */
	public void keepOriginalColumnPositions(final int i) {
		if (i < this.reportDialog.getSelectedPrintingColumns().size()) {
			this.hColsPositions.put(this.reportDialog.getSelectedPrintingColumns().get(i).toString(), new Integer(i));
		}
	}

	/**
	 * Creates and adds a column to show the number of rows in report. This column is showed in report
	 * on the left.
	 * @throws ColumnBuilderException
	 */
	public void createRowNumberColumn() throws ColumnBuilderException {
		final AbstractColumn numbers = ColumnBuilder.getNew()
				.setCustomExpression(this.getExpressionForRowNumbers())
				.setStyle(this.defaultColumnDataStyle)
				.setTitle("")
				.setWidth(DynamicJasperEngine.widthRowNumbers)
				.setFixedWidth(true)
				.build();
		final int number = this.erData.calculateRecordNumber();
		final DecimalFormat format = new DecimalFormat("#,##0");
		final String maxText = format.format(number);
		final int contentWidth = FontHelper.getWidthFor(this.getDefaultColumnDataFont(), maxText)
				+ DynamicJasperEngine.WIDTH_OFFSET;
		numbers.setWidth(contentWidth);
		numbers.setFixedWidth(true);
		this.drb.addColumn(numbers);
	}

	/**
	 * This method checks is a specified column is numeric. It is a heavy method because iterates for
	 * all columns in the model.
	 * @param column the column to check
	 * @return condition the condition
	 */
	public boolean existFunctionForColumn(final AbstractColumn column) {
		for (int j = 0; j < this.model.getColumnCount(); j++) {
			if (this.model.getColumnName(j).equals(column.getName())) {
				return this.isNumericClass(this.model.getColumnClass(j));
			}
		}
		return false;
	}

	/**
	 * Returns true when parameter satisfies Number.class.isAssignableFrom() condition.
	 * @param classparameter the class to check
	 * @return condition about Number instance
	 */
	public boolean isNumericClass(final Object classparameter) {
		if ((classparameter instanceof Class) && Number.class.isAssignableFrom((Class) classparameter)) {
			return true;
		}
		return false;
	}

	/**
	 * This method iterates in model and returns th class of column passed in parameter.
	 * @param columnName The name of column
	 * @return The class of column
	 */
	public Class getColumnClassForColumn(final String columnName) {
		for (int j = 0; j < this.model.getColumnCount(); j++) {
			if (this.model.getColumnName(j).equals(columnName)) {
				final Class<?> currentClass = this.model.getColumnClass(j);
				if (currentClass.isAssignableFrom(com.ontimize.gui.table.TableSorter.ValueByGroup.class)) {
					// grouped columns
					return String.class;
				}
				return currentClass;
			}
		}
		return null;
	}

	/**
	 * Checks whether contextual menu in group list is marked to show a multi-group report (header with
	 * selected columns).
	 * @return the condition of selection
	 */
	public boolean isSelectedMultiGrouping() {
		if (this.reportDialog.getMultigroups() != null) {
			return this.reportDialog.getMultigroups().length > 0 ? true : false;
		}
		return false;
	}

	/**
	 * Apply function for a specific column. This method is called twice: inside
	 * buildcolumns(column,null) and in buildgroups(column,group).
	 *
	 * There are two possibilities: <br>
	 * - when function is for a group (group!=null).<br>
	 * - when function is for a column that is not in a group (group==null at the end of report).<br>
	 * @throws Exception
	 */
	public void applyFunctionForColumn(final AbstractColumn column, final GroupBuilder group) throws Exception {
		final Enumeration enumkeys = Collections.enumeration(this.reportDialog.getSelectedFunctionColumns().keySet());
		while (enumkeys.hasMoreElements()) {
			final Object keyColumn = enumkeys.nextElement();
			final Object valueOperation = this.reportDialog.getSelectedFunctionColumns().get(keyColumn);
			if (column.getName().equals(keyColumn)) {
				if (group != null) {
					final Style columnStyle = (Style) this.styleGroupFooterVariable.clone();
					// If style is null the column isn't in selected printing
					// columns.
					if (column.getStyle() != null) {
						columnStyle.setHorizontalTextAlign(column.getStyle().getHorizontalTextAlign());
						columnStyle.setHorizontalImageAlign(column.getStyle().getHorizontalImageAlign());
						group.addFooterVariable(column, this.getOperation((Integer) valueOperation), columnStyle,
								new DJValueFormatter() {

							@Override
							public Object evaluate(final Object value, final Map fields, final Map variables, final Map parameters) {
								return value;
							}

							@Override
							public String getClassName() {
								return Number.class.getName();
							}

						});
					} else {
						group.addFooterVariable(column, this.getOperation((Integer) valueOperation), columnStyle,
								new DJValueFormatter() {

							@Override
							public Object evaluate(final Object value, final Map fields, final Map variables, final Map parameters) {
								return value;
							}

							@Override
							public String getClassName() {
								return Number.class.getName();
							}

						}, new DJGroupLabel(
								ApplicationManager.getTranslation(column.getName(),
										this.reportDialog.getBundle()) + " ("
										+ this.getTextOperation((Integer) valueOperation) + ")",
										columnStyle, LabelPosition.LEFT));
					}
				} else {
					final Style columnStyle = (Style) this.styleFooterVariable.clone();
					columnStyle.setHorizontalTextAlign(column.getStyle().getHorizontalTextAlign());
					columnStyle.setHorizontalImageAlign(column.getStyle().getHorizontalImageAlign());
					if (this.drb.getColumns().contains(column)) {
						this.drb.addGlobalFooterVariable(column, this.getOperation((Integer) valueOperation),
								columnStyle, new DJValueFormatter() {

							@Override
							public Object evaluate(final Object value, final Map fields, final Map variables, final Map parameters) {
								return value;
							}

							@Override
							public String getClassName() {
								return String.class.getName();
							}
						});
					} else {
						column.setFixedWidth(false);
						final DJColSpan colSpan = new DJColSpan();
						colSpan.setColumns(this.drb.getColumns());
						column.setColSpan(colSpan);
						this.drb.addGlobalFooterVariable(column, this.getOperation((Integer) valueOperation),
								columnStyle, new DJValueFormatter() {

							@Override
							public Object evaluate(final Object value, final Map fields, final Map variables, final Map parameters) {
								return "Total " + ApplicationManager.getTranslation(column.getName(),
										DynamicJasperEngine.this.reportDialog.getBundle()) + " ("
										+ DynamicJasperEngine.this
										.getTextOperation((Integer) valueOperation)
										+ ")" + " " + new DecimalFormat(column.getPattern()).format(value);
							}

							@Override
							public String getClassName() {
								return String.class.getName();
							}
						});
					}

					this.drb.setGrandTotalLegend("");
				}
				break;
			}
		}
	}

	/**
	 * This method performs operation to return a string with values for grouping dates. These values
	 * are returned in string and are used to know when group must break.
	 * @param fields All fields in report
	 * @param groupColumn The column to be grouped
	 * @return The String with evaluation date
	 * @throws ParseException
	 */
	public String getEvaluationValueForGroupingDate(final Map fields, final String groupColumn) {
		final Calendar calendar = Calendar.getInstance();
		if (fields.get(groupColumn) instanceof String) {
			return fields.get(groupColumn).toString();
		}

		// TODO next code could be delete (not used - always string)
		calendar.setTime((java.util.Date) fields.get(groupColumn));
		final Integer operation = (Integer) this.reportDialog.getSelectedDateGroupingColumns().get(groupColumn.toString());
		switch (operation.intValue()) {
		case 0:
			// Complete date (day, month and year)
			return calendar.getTime().toString();
		case 1:
			// Day of month
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_DATE_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ Integer
					.toString(calendar.get(Calendar.DAY_OF_MONTH));
		case 2:
			// Month
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ new Integer(calendar.get(Calendar.MONTH) + 1);
		case 3:
			// Month and year
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ Integer
					.toString(calendar.get(Calendar.MONTH) + 1)
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.dateNameSeparator + Integer.toString(calendar.get(Calendar.YEAR));
		case 4:
			// Quarter
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ Integer
					.toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1);
		case 5:
			// Quarter and year
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ Integer
					.toString(((calendar.get(Calendar.MONTH) + 1) / 4) + 1)
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.dateNameSeparator + Integer.toString(calendar.get(Calendar.YEAR));
		case 6:
			// Year
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
					this.reportDialog.getBundle()) + DynamicJasperEngine.dateNameSeparator
					+ Integer
					.toString(calendar.get(Calendar.YEAR));
		default:
			return calendar.getTime().toString();
		}

	}

	/**
	 * This method performs operation to return a string with values for grouping with multiple columns.
	 * These values are returned in string and are used to know when multi group must break.
	 * @param groupColumns The list of group columns
	 * @param fields Fields in report
	 * @param variables Variables in report
	 * @param parameters Report parameters
	 * @return The multi group expression
	 */
	protected Object getMultigroupExpression(final List groupColumns, final Map fields, final Map variables, final Map parameters) {
		String returned = new String();
		for (int i = 0; i < groupColumns.size(); i++) {
			returned = returned
					+ (fields.get(groupColumns.get(i)) == null ? "" : fields.get(groupColumns.get(i)).toString());

		}
		return returned;
	}

	/**
	 * Internal method used to create a multi group.
	 * @param groupIndex Index in group criteria in group columns
	 * @param groupColumn Name of criteria to group by
	 * @param groupClass Class for group criteria
	 * @param multiGroupContainsSubgroups This condition is true when a multigroup is parent of another
	 *        groups
	 * @return the <code>GroupBuilder</code> with groups
	 * @throws Exception A <code>Exception</code> when group cannot be created.
	 */
	protected GroupBuilder buildMultiGroup(final int groupIndex, final List groupColumns, List groupClasses,
			final boolean multiGroupContainsSubgroups) throws Exception {
		// Each group is an instance of a DJGRoup
		this.dynamicGroupIdentifier = DynamicJasperEngine.GROUP_COUNT_LABEL + groupColumns.get(0).toString();
		GroupBuilder gb1 = new GroupBuilder(this.dynamicGroupIdentifier);

		// Checks type of classes for column, (java.sql.Date) not supported and
		// it is replaced by "java.util.Date"
		groupClasses = this.configureMultiGroupColumnClasses(groupClasses);

		ColumnBuilder cb = ColumnBuilder.getNew();
		cb = this.configureMultiGroupColumnBuilder(cb, groupColumns.get(0).toString(), (Class) groupClasses.get(0),
				groupColumns);

		// Creates columns for all group columns included in current group
		this.generateOtherMultiGroupColumns(groupColumns, groupClasses);
		// Build the column to group
		AbstractColumn column = cb.build();

		column = this.configureMultiGroupColumn(column, groupColumns.get(0).toString(), (Class) groupClasses.get(0),
				groupIndex);
		gb1 = this.configureMultiGroupBuilder(gb1, column, multiGroupContainsSubgroups);

		// Creates the label for number of ocurrences in group
		if (this.isSelectedNumberOfOccurrences()) {
			this.configureGroupNumberOfOcurrencesLabel(gb1, column.getName());
		}

		// Adds footer variables to group
		if (this.existFunctions() && this.existGroups()) {
			this.configureGroupFooterVariables(gb1, column);
		}

		return gb1;
	}

	/**
	 * Iterates in group classes and modify java.sql.Date columns by java.util.Date columns
	 * @param groupClasses The group classes for a multi group
	 * @return The groupClasses modified
	 */
	protected List configureMultiGroupColumnClasses(final List groupClasses) {
		for (int i = 0; i < groupClasses.size(); i++) {
			if (((Class) groupClasses.get(i)).isAssignableFrom(java.sql.Date.class)) {
				groupClasses.add(i, java.util.Date.class);
			}
		}
		return groupClasses;
	}

	/**
	 * Parameter groupColumns contains all columns to create the multigroup. First element is the root
	 * of group and rest of columns must be registered to build the header of group. So, it is stored in
	 * <code>Hashtable</code> hMultiGroupColumns with key the first column of multigroup (first in group
	 * list) and value other multigroup columns.
	 * @param groupColumns Multi group columns
	 * @param groupClasses Multi group classes
	 */
	protected void generateOtherMultiGroupColumns(final List groupColumns, final List groupClasses) {
		final List vOtherMultiGroupCols = new Vector();
		vOtherMultiGroupCols.addAll(groupColumns);
		vOtherMultiGroupCols.remove(0);
		final List vOtherMultiGroupClasses = new Vector();
		vOtherMultiGroupClasses.addAll(groupClasses);
		vOtherMultiGroupClasses.remove(0);
		this.hMultiGroupColumns.put(groupColumns.get(0).toString(), vOtherMultiGroupCols);
		for (int i = 0; i < vOtherMultiGroupCols.size(); i++) {
			final ColumnBuilder cb = ColumnBuilder.getNew();
			cb.setColumnProperty(vOtherMultiGroupCols.get(i).toString(),
					((Class) vOtherMultiGroupClasses.get(i)).getName());
			cb.setTitle(ApplicationManager.getTranslation(
					this.getColumnFromVirtualColumn(vOtherMultiGroupCols.get(i).toString()),
					this.reportDialog.getBundle()));
			try {
				final AbstractColumn column = cb.build();
				column.setName(vOtherMultiGroupCols.get(i).toString());
				column.setTitle(ApplicationManager.getTranslation(
						this.getColumnFromVirtualColumn(vOtherMultiGroupCols.get(i).toString()),
						this.reportDialog.getBundle()));
				this.vMultiGroupColumns.add(column);
			} catch (final ColumnBuilderException e) {
				DynamicJasperEngine.logger.error(null, e);
			}

		}
	}

	/**
	 * Gets the List with columns in multigroup from the initial column of multigroup
	 * @param initialMultiColumn The column
	 * @return the rest of columns
	 */
	public List getMultiGroupColumnsForColumn(final String initialMultiColumn) {
		return (List) this.hMultiGroupColumns.get(initialMultiColumn);
	}

	/**
	 * Internal method used to create a group.
	 * @param groupIndex Index in group criteria in group columns
	 * @param groupColumn Name of criteria to group by
	 * @param groupClass Class for group criteria
	 * @return the <code>GroupBuilder</code> with groups
	 * @throws Exception A <code>Exception</code> when group cannot be created.
	 */
	protected GroupBuilder buildGroup(final int groupIndex, final String groupColumn, final Class groupClass) throws Exception {
		// Each group is an instance of a DJGRoup
		this.dynamicGroupIdentifier = this.getGroupIdentifier(groupColumn);
		GroupBuilder gb1 = new GroupBuilder(this.dynamicGroupIdentifier);

		ColumnBuilder cb = ColumnBuilder.getNew();

		cb = this.configureGroupColumnBuilder(cb, groupColumn, groupClass);

		// Build the column to group
		AbstractColumn column = cb.build();

		column = this.configureGroupColumn(column, groupColumn, groupClass, groupIndex);

		gb1 = this.configureGroupBuilder(gb1, column, groupIndex);

		// Adds number of ocurrences for group
		if (this.isSelectedNumberOfOccurrences()) {
			this.configureGroupNumberOfOcurrencesLabel(gb1, groupColumn);
		}

		// Adds footer variables to group
		if (this.existFunctions() && this.existGroups()) {
			this.configureGroupFooterVariables(gb1, column);
		}


		if (this.isGroupStartInNewPage()) {
			gb1.setStartInNewPage(Boolean.TRUE);
		} else if (this.isFirstGroupStartInNewPage() && groupIndex == 0) {
			gb1.setStartInNewPage(Boolean.TRUE);
		}

		return gb1;
	}

	/**
	 * Creates and sets the label to group indicating the number of ocurrences.
	 * @param gb1 The group builder
	 * @param groupColumn The group column
	 */
	public void configureGroupNumberOfOcurrencesLabel(final GroupBuilder gb1, final String groupColumn) {
		final DJGroupLabel glabel = new DJGroupLabel(new CustomExpression() {

			@Override
			public Object evaluate(final Map fields, final Map variables, final Map parameters) {
				return ApplicationManager.getTranslation("NumeroDeOcurrencias",
						DynamicJasperEngine.this.reportDialog.getBundle()) + "         "
						+ ExpressionHelper
						.getGroupCount(DynamicJasperEngine.GROUP_COUNT_LABEL + groupColumn, variables);
			}

			@Override
			public String getClassName() {
				return String.class.getName();
			}

		}, this.styleGroupFooterNumberOcurrencesVariable, LabelPosition.TOP);
		gb1.setFooterLabel(glabel);

	}

	/**
	 * Configures and applies variables in group footer.
	 * @param gb1 The group builder
	 * @param column Column that is the criteria for grouping
	 * @throws Exception when occurs an error configuring group builder
	 */
	public void configureGroupFooterVariables(final GroupBuilder gb1, final AbstractColumn column) throws Exception {
		for (int k = 0; k < this.reportDialog.getPrintingColumns().size(); k++) {
			// if
			// (!this.reportDialog.getSelectedPrintingAndGroupedColumns().contains(this.reportDialog.getPrintingColumns().get(k).toString())){
			// drb.addField(this.reportDialog.getPrintingColumns().get(k).toString(),
			// getColumnClassForColumn(this.reportDialog.getPrintingColumns().get(k).toString().trim()).getName());
			// }
			final String columnName = this.reportDialog.getPrintingColumns().get(k).toString().trim();
			if (this.isNumericClass(this.getColumnClassForColumn(columnName))) {
				final List<AbstractColumn> columns = this.drb.getColumns();
				AbstractColumn printingColumn = null;
				for (final AbstractColumn current : columns) {
					if (columnName.equals(current.getName())) {
						printingColumn = current;
						break;
					}
				}

				final ColumnBuilder cbVariables = ColumnBuilder.getNew();
				if (printingColumn != null) {
					cbVariables.setColumnProperty(printingColumn.getName(),
							this.getColumnClassForColumn(
									this.reportDialog.getPrintingColumns().get(k).toString().trim())
							.getName());
					cbVariables.setWidth(printingColumn.getWidth());
					cbVariables.setTitle(printingColumn.getTitle());
				} else {
					cbVariables.setColumnProperty(this.reportDialog.getPrintingColumns().get(k).toString().trim(),
							this.getColumnClassForColumn(
									this.reportDialog.getPrintingColumns().get(k).toString().trim())
							.getName());
					// defines // the // field // of // the // data // source //
					// that // this // column // will // show, // also // its //
					// type
					cbVariables.setWidth(column.getWidth());
					cbVariables.setTitle(ApplicationManager.getTranslation(
							this.reportDialog.getPrintingColumns().get(k).toString(), this.reportDialog.getBundle()));
				}

				AbstractColumn columnVariables = cbVariables.build();
				columnVariables.setPosX(printingColumn != null ? printingColumn.getPosX() : column.getPosX());
				columnVariables.setName(columnName);
				if (printingColumn != null) {
					columnVariables.setPattern(printingColumn.getPattern());
				} else {
					columnVariables = this.setPatterns(columnVariables, this
							.getColumnClassForColumn(this.reportDialog.getPrintingColumns().get(k).toString().trim()));
				}

				if (printingColumn != null) {
					this.applyFunctionForColumn(printingColumn, gb1);
				} else {
					this.applyFunctionForColumn(columnVariables, gb1);
				}
			}
		}
	}

	/**
	 * Configures group builder for single groups
	 * @param gb1 The initial group builder
	 * @param column Column that is the criteria for grouping
	 */
	public GroupBuilder configureGroupBuilder(final GroupBuilder gb1, final AbstractColumn column, final int groupIndex) {
		gb1.setHeaderHeight(this.calculateRequiredGroupHeaderHeight());
		// Indicates to group builder the column to group by
		gb1.setCriteriaColumn((PropertyColumn) column);
		gb1.setFooterHeight(new Integer(40));
		// multigroup contains a different style where not border bottom line
		// are showed in header (except the more internal)
		// Fix the style for column showed in group header
		// more internal group in multi group must contain a thin bottom border

		// Fix the style for column showed in group header (right padding to
		// avoid offset)
		// gb1.addColumnHeaderStyle(column, this.defaultHeaderForGroupStyle);
		final int offset = groupIndex * DynamicJasperEngine.paddingForGroups;
		final Style style = new Style();
		style.setFont(this.getDefaultHeaderForGroupFont());
		style.setTextColor(DynamicJasperStyles.defaultHeaderForGroupFontColor);
		style.setPaddingBottom(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingBottom));
		style.setPaddingTop(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingTop));
		style.setPaddingLeft(new Integer(DynamicJasperStyles.defaultHeaderForGroupPaddingLeft));
		style.setPaddingRight(new Integer(offset + DynamicJasperStyles.defaultHeaderForGroupPaddingRight));
		style.setHorizontalAlign(DynamicJasperStyles.defaultHeaderForGroupHorizontalAlignmentTitles);
		style.setBorderBottom(DynamicJasperStyles.defaultHeaderForGroupBorderBottom);
		style.setBorderTop(DynamicJasperStyles.defaultHeaderForGroupBorderTop);
		style.setBorderLeft(DynamicJasperStyles.defaultHeaderForGroupBorderLeft);
		style.setBorderRight(DynamicJasperStyles.defaultHeaderForGroupBorderRight);
		style.setVerticalAlign(DynamicJasperStyles.defaultHeaderForGroupVerticalAlignment);
		style.setBackgroundColor(DynamicJasperStyles.defaultHeaderForGroupBackgroundColor);
		style.setTransparency(DynamicJasperStyles.defaultHeaderForGroupTransparency);
		gb1.addColumnHeaderStyle(column, style);
		gb1.setGroupLayout(this.getGroupLayoutForColumn(column)); // tells the
		// group how
		// to be
		// shown,
		// there are
		// many
		// possibilities,
		// see the
		// GroupLayout
		// for more.
		gb1.setAllowFooterSplit(false);
		return gb1;
	}

	/**
	 * Utility method used because fitcontenttoHeight in gb1.setHeaderHeight is ignored and with large
	 * column names and several columns selected, column names were not drawn correctly
	 * @return the group header height
	 */
	private Integer calculateRequiredGroupHeaderHeight() {
		int columnNameSize = 0;
		for (int i = 0; i < this.reportDialog.getSelectedPrintingColumns().size(); i++) {
			if (ApplicationManager
					.getTranslation(this.reportDialog.getSelectedPrintingColumns().get(i).toString(),
							this.reportDialog.getBundle())
					.length() > columnNameSize) {
				columnNameSize = ApplicationManager
						.getTranslation(this.reportDialog.getSelectedPrintingColumns().get(i).toString(),
								this.reportDialog.getBundle())
						.length();
			}
		}
		if ((this.reportDialog.getSelectedPrintingColumns().size() > 4) && (columnNameSize > 12)) {
			return new Integer(25);
		}
		return new Integer(15);
	}

	/**
	 * Configures group builder for multiple groups
	 * @param gb1 The initial group builder
	 * @param column Column that is the criteria for grouping
	 * @param multiGroupContainsSubgroups Checks whether group contains nested subgroups
	 */
	public GroupBuilder configureMultiGroupBuilder(final GroupBuilder gb1, final AbstractColumn column,
			final boolean multiGroupContainsSubgroups) {
		gb1.setHeaderHeight(new Integer(15));
		// Indicates to group builder the column to group by
		gb1.setCriteriaColumn((PropertyColumn) column);
		gb1.setFooterHeight(new Integer(40));
		gb1.addColumnHeaderStyle(column, this.defaultHeaderForMultiGroupStyle);
		gb1.setGroupLayout(this.getGroupLayoutForMultiGroup(column, multiGroupContainsSubgroups));
		gb1.setAllowFooterSplit(false);
		return gb1;
	}

	/**
	 * Set properties for column in multiple group: <br>
	 * NOTE: Implementation is equals to configureGroupColumn but it is duplicated for make easier
	 * future inheritance and modularity.
	 * <ul>
	 * <li>Print Blanks when null values
	 * <li>Name of column
	 * <li>Sets relative position in X axis for creating indented groups
	 * <li>Default width for column (It is calculated dynamically)
	 * </ul>
	 * @param column The column to be configured
	 * @param groupColumn The group column
	 * @param groupClass The group class for column
	 * @param groupIndex The number of group in report (Used for set width and position)
	 * @return The column modified
	 */
	public AbstractColumn configureMultiGroupColumn(AbstractColumn column, final String groupColumn, final Class groupClass,
			final int groupIndex) {
		// Fix the padding to show each group indented with previous
		column.setPosX(new Integer(groupIndex * DynamicJasperEngine.paddingForGroups));
		column.setBlankWhenNull(Boolean.TRUE);
		column.setWidth(new Integer(DynamicJasperEngine.columnWidth
				- (this.reportDialog.getSelectedGroupColumns().size() * DynamicJasperEngine.paddingForGroups)));
		column.setName(groupColumn);
		column = this.setPatterns(column, groupClass);
		return column;
	}

	/**
	 * Set properties for column in single group: <br>
	 * NOTE: Implementation is equals to configureMultiGroupColumn but it is duplicated for make easier
	 * future inheritance and modularity.
	 *
	 * <ul>
	 * <li>Print Blanks when null values
	 * <li>Name of column
	 * <li>Sets relative position in X axis for creating indented groups
	 * <li>Default width for column (It is calculated dynamically)
	 * </ul>
	 * @param column The column to be configured
	 * @param groupColumn The group column
	 * @param groupClass The group class for column
	 * @param groupIndex The number of group in report (Used for set width and position)
	 * @return The column modified
	 */
	public AbstractColumn configureGroupColumn(AbstractColumn column, final String groupColumn, final Class groupClass,
			final int groupIndex) {
		// Fix the padding to show each group indented with previous
		final int offset = groupIndex * DynamicJasperEngine.paddingForGroups;
		column.setPosX(offset);
		column.setBlankWhenNull(Boolean.TRUE);
		// column.setWidth(new Integer(DynamicJasperEngine.columnWidth -
		// (this.reportDialog.getSelectedGroupItems().size() *
		// DynamicJasperEngine.paddingForGroups)));
		column.setWidth(DynamicJasperEngine.columnWidth);
		column.setName(groupColumn);
		column = this.setPatterns(column, groupClass);
		return column;
	}

	/**
	 * Set properties for column builder in multiple group:
	 * <ul>
	 * <li>Set column builder property
	 * <li>Title of column builder
	 * <li>Expression to group by
	 * <li>Style for column builder
	 * <li>Default width for column (It is calculated dynamically)
	 * </ul>
	 * @param cb The initial column builder
	 * @param groupColumn The group column
	 * @param groupClass The group class
	 * @param groupColumns Other group columns that belongs to multiple group
	 * @return The column builder modified
	 */
	public ColumnBuilder configureMultiGroupColumnBuilder(ColumnBuilder cb, final String groupColumn, final Class groupClass,
			final List groupColumns) {
		cb = this.setColumnGroupClass(cb, groupColumn, groupClass);
		// Used to allow group by in dates
		cb.setCustomExpressionToGroupBy(new CustomExpression() {

			@Override
			public Object evaluate(final Map fields, final Map variables, final Map parameters) {
				return DynamicJasperEngine.this.getMultigroupExpression(groupColumns, fields, variables, parameters);
			}

			@Override
			public String getClassName() {
				return String.class.getName();
			}
		});
		cb.setTitle(ApplicationManager.getTranslation(this.getColumnFromVirtualColumn(groupColumn),
				this.reportDialog.getBundle()));
		cb.setWidth(DynamicJasperEngine.columnWidth);
		cb.setStyle(this.defaultHeaderForMultiGroupStyle);
		return cb;
	}

	/**
	 * Set properties for column builder in simple group:
	 * <ul>
	 * <li>Set column builder property
	 * <li>Title of column builder
	 * <li>Style for column builder
	 * <li>Default width for column (It is calculated dynamically)
	 * </ul>
	 * @param cb The initial column builder
	 * @param groupColumn The group column
	 * @param groupClass The group class
	 * @return The column builder modified
	 */
	public ColumnBuilder configureGroupColumnBuilder(ColumnBuilder cb, final String groupColumn, final Class groupClass) {
		// defines the field of the data source that this column will show, also
		// its type (java.sql.Date) not supported
		cb = this.setColumnGroupClass(cb, groupColumn, groupClass);

		// Used to allow group by in dates
		// This column requires special evaluation depending on the type of
		// grouping date selected. Conditions are evaluated in
		// getEvaluationValueForGroupingDate
		if (this.isVirtualColumn(groupColumn)) {
			cb = this.setDateGroupingExpression(cb, groupColumn);
		}

		cb.setTitle(ApplicationManager.getTranslation(this.getColumnFromVirtualColumn(groupColumn), this.reportDialog
				.getBundle()) /*
				 * + " " + ApplicationManager . getTranslation ( getGroupedKey ( getColumnFromVirtualColumn (
				 * groupColumn ) ) , reportDialog . getBundle ( ) )
				 */);

		cb.setWidth(DynamicJasperEngine.columnWidth);
		cb.setStyle(this.defaultHeaderForGroupStyle);
		return cb;
	}

	/**
	 * Gets the group identifier. This identifier must be unique.
	 * @param groupColumn The name of column
	 * @return The generated identifier
	 */
	public String getGroupIdentifier(final String groupColumn) {
		return DynamicJasperEngine.GROUP_COUNT_LABEL + groupColumn;
	}

	/**
	 * Sets the group column class for column group. This method changes "java.sql.Date" classes by
	 * "java.util.Date".
	 * @param cb The column builder
	 * @param groupColumn The group column
	 * @param groupClass The group class
	 * @return The column builder modified
	 */
	public ColumnBuilder setColumnGroupClass(final ColumnBuilder cb, final String groupColumn, final Class groupClass) {
		if (groupClass.isAssignableFrom(java.sql.Date.class)) {
			cb.setColumnProperty(groupColumn, java.util.Date.class.getName());
		} else {
			cb.setColumnProperty(groupColumn, groupClass.getName());
		}
		return cb;

	}

	/**
	 * Fixes the property column class to <code>String</code> and date grouping expression to column
	 * builder.
	 * @param cb The column builder
	 * @param groupColumn Column to group
	 * @return The column builder with expression fixed
	 */
	public ColumnBuilder setDateGroupingExpression(final ColumnBuilder cb, final String groupColumn) {
		cb.setColumnProperty(groupColumn, String.class.getName());
		// cb.setCustomExpression(new CustomExpression(){
		//
		// public Object evaluate(Map fields, Map variables, Map parameters) {
		// return
		// getEvaluationValueForGroupingDate(fields,groupColumn).toString();
		// }
		//
		// public String getClassName() {
		// return String.class.getName();
		// }
		// });
		cb.setCustomExpressionToGroupBy(new CustomExpression() {

			@Override
			public Object evaluate(final Map fields, final Map variables, final Map parameters) {
				return DynamicJasperEngine.this.getEvaluationValueForGroupingDate(fields, groupColumn).toString();
			}

			@Override
			public String getClassName() {
				return String.class.getName();
			}
		});
		return cb;
	}

	/**
	 * Gets the text showed in grouping dates.
	 * @param groupColumn The name of group column
	 * @return The value of grouped key.
	 */
	public String getGroupedKey(final String groupColumn) {
		final Integer operation = (Integer) this.reportDialog.getSelectedDateGroupingColumns().get(groupColumn.toString());
		if (operation == null) {
			return new String();
		}
		switch (operation.intValue()) {
		case 0:
			// Complete date (day, month and year) and hour
			return ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_DATE_TIME_KEY,
					this.reportDialog.getBundle());
		case 1:
			// Day of month
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_DATE_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		case 2:
			// Month
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		case 3:
			// Month and year
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_MONTH_AND_YEAR_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		case 4:
			// Quarter
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		case 5:
			// Quarter and year
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_QUARTER_AND_YEAR_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		case 6:
			// Year
			return DynamicJasperEngine.groupOpenCharacter
					+ ApplicationManager.getTranslation(DefaultReportDialog.GROUP_BY_YEAR_KEY,
							this.reportDialog.getBundle())
					+ DynamicJasperEngine.groupCloseCharacter;
		default:
			return DynamicJasperEngine.groupOpenCharacter + ApplicationManager
					.getTranslation(DynamicJasperEngine.GROUP_BY_DEFAULT, this.reportDialog.getBundle());
		}
	}

	/**
	 * Build groups from selected group columns.
	 * @throws Exception Throws a <code>ColumnBuilderException</code> when occurs an Error building
	 *         columns for groups.
	 */
	public void buildGroups() throws Exception {
		for (int i = 0; i < this.reportDialog.getSelectedGroupItems().size(); i++) {
			// If column is selected for grouping and it is included in report,
			// group will be created
			if (this.reportDialog.getSelectedGroupItems().get(i) instanceof SelectableMultipleItem) {
				final List vGroupColumns = new Vector();
				final List vGroupClasses = new Vector();
				boolean multiGroupContainsSubgroups = false;
				if (i != (this.reportDialog.getSelectedGroupItems().size() - 1)) {
					multiGroupContainsSubgroups = true;
				}
				for (int m = 0; m < ((SelectableMultipleItem) this.reportDialog.getSelectedGroupItems().get(i))
						.getItemList()
						.size(); m++) {

					String currentGroupItem = ((com.ontimize.report.item.SelectableItem) ((SelectableMultipleItem) this.reportDialog
							.getSelectedGroupItems()
							.get(i)).getItemList()
							.get(m)).getText();

					if (this.isDateColumn(this.getColumnClassForColumn(currentGroupItem))) {
						currentGroupItem = this.getVirtualColumn(currentGroupItem);
					}
					vGroupColumns.add(currentGroupItem);
					vGroupClasses.add(this.getColumnClassForColumn(currentGroupItem));
				}
				this.gb1 = this.buildMultiGroup(i, vGroupColumns, vGroupClasses, multiGroupContainsSubgroups);
				final DJGroup g1 = this.gb1.build();
				this.drb.addGroup(g1);
			} else {
				String currentGroupItem = ((SelectableItem) this.reportDialog.getSelectedGroupItems().get(i)).getText();
				final Class currentGroupClass = this.getColumnClassForColumn(currentGroupItem);
				if (!this.isImageColumn(currentGroupClass)) {
					if (this.isDateColumn(currentGroupClass)) {
						currentGroupItem = this.getVirtualColumn(currentGroupItem);
					}
					this.gb1 = this.buildGroup(i, currentGroupItem, currentGroupClass);
					final DJGroup g1 = this.gb1.build();
					this.drb.addGroup(g1);
				}
			}
		}
	}

	/**
	 * Returns the identifier for virtual column addign to <code>currentGroupItem</code> the suffix
	 * indicated by variable: <i>VIRTUAL_SUFFIX</i>
	 * @param currentGroupItem Name of group column
	 * @return the value
	 */
	public String getVirtualColumn(final String currentGroupItem) {
		return currentGroupItem + DynamicJasperEngine.VIRTUAL_SUFFIX;
	}

	/**
	 * Returns the identifier for virtual column used for ordering to <code>virtualColumn</code> the
	 * suffix indicated by variable: <i>IRTUAL_ORDERING_SUFFIX</i>
	 * @param currentGroupItem Name of group column
	 * @return the value
	 */
	public String getVirtualColumnForOrdering(final String virtualColumn) {
		return virtualColumn + DynamicJasperEngine.VIRTUAL_ORDERING_SUFFIX;
	}

	/**
	 * Gets identifier for column from virtual column. This identifier is created adding to column the
	 * suffix variable: VIRTUAL_SUFFIX.
	 * @param virtualColumn The name of virtual column
	 * @return the name of column
	 */
	public String getColumnFromVirtualColumn(final String virtualColumn) {
		if (this.isVirtualColumn(virtualColumn)) {
			return virtualColumn.substring(0, virtualColumn.length() - DynamicJasperEngine.VIRTUAL_SUFFIX.length());
		} else {
			return virtualColumn;
		}
	}

	/**
	 * Checks whether the group indicated by groupindex is the most internal in layout.
	 * @param i The group index
	 * @return The condition about this group
	 */
	public boolean isMoreInternalGroup(final int groupindex) {
		if (groupindex == (this.reportDialog.getSelectedGroupColumns().size() - 1)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the group indicated by groupindex is the most internal in layout.
	 * @param i The group index
	 * @return The condition about this group
	 */
	public boolean isMoreInternalGroup(final DJGroup group) {
		for (int i = 0; i < this.reportDialog.getSelectedGroupItems().size(); i++) {
			// If column is selected for grouping and it is included in report,
			// group will be created
			if (this.reportDialog.getSelectedGroupItems().get(i) instanceof SelectableMultipleItem) {
				for (int m = 0; m < ((SelectableMultipleItem) this.reportDialog.getSelectedGroupItems().get(i))
						.getItemList()
						.size(); m++) {
					final String currentGroupItem = ((com.ontimize.report.item.SelectableItem) ((SelectableMultipleItem) this.reportDialog
							.getSelectedGroupItems()
							.get(i)).getItemList()
							.get(m)).getText();
					if (currentGroupItem.equals(group.getName().substring(18)) && (i == (m - 1))) {
						return true;
					}
				}
			} else {
				if (group.getName()
						.substring(18)
						.equals(((SelectableItem) this.reportDialog.getSelectedGroupItems().get(i)).getText())) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Obtains the layout for a determined group. It is necessary for embedded groups where only the
	 * internal group should show the title of columns (Header).
	 *
	 *
	 * <br>
	 * <br>
	 * <i>Group1 criterion1</i><br>
	 * &nbsp <i>Group 2 criterion2</i><br>
	 * &nbsp&nbsp <b>Header1 Header2 Header 3</b><br>
	 * &nbsp&nbsp value 1.1 &nbsp value1.2 &nbsp value1.3<br>
	 * &nbsp&nbsp value 2.1 &nbsp value2.2 &nbsp value2.3<br>
	 * @param column the column to compute the layout
	 * @return the layout for group
	 *
	 * @see GroupLayout
	 * @see CustomGroupLayout
	 */
	public GroupLayout getGroupLayoutForColumn(final AbstractColumn column) {

		if (!this.isShowedGroupDetails()) {
			return CustomGroupLayout.JUST_HEADERS;
		}
		if (CollectionTools.lastElement(this.reportDialog.getSelectedGroupColumns()).toString().equals(column.getName())
				|| CollectionTools.lastElement(this.reportDialog.getSelectedGroupColumns()).toString()
				.equals(this.getColumnFromVirtualColumn(column.getName()))) {
			return CustomGroupLayout.VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME_PRINT_EACH;
		} else {
			return CustomGroupLayout.VALUE_IN_HEADER_WITHOUT_HEADERS_AND_COLUMN_NAME;
		}
	}

	/**
	 * Get group layout for multigroup. Layout must show column names when group does not contains
	 * subgroups. On the contrary, only the most internal subgroup should contain the column names.
	 * @param column Group column to check the layout
	 * @param multiGroupContainsSubgroups Condition about existence of subgroups for a determined group
	 * @return the group layout
	 */
	public GroupLayout getGroupLayoutForMultiGroup(final AbstractColumn column, final boolean multiGroupContainsSubgroups) {
		if (!this.isShowedGroupDetails()) {
			return CustomGroupLayout.JUST_HEADERS;
		}
		if (!multiGroupContainsSubgroups) {
			return CustomGroupLayout.VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME_PRINT_EACH;
		} else {
			return CustomGroupLayout.VALUE_IN_HEADER_WITHOUT_HEADERS_AND_COLUMN_NAME;
		}
	}

	/**
	 * Determines if check for hiding group details is marked in report menu options.
	 * @return the condition
	 */
	public boolean isShowedGroupDetails() {
		return !this.reportDialog.getHideGroupDetail7CheckMenu().isSelected();
	}

	public boolean isGroupStartInNewPage() {
		return this.reportDialog.getGroupStartInNewPageCheckMenu().isSelected();
	}

	public boolean isFirstGroupStartInNewPage() {
		return this.reportDialog.getFirstGroupStartInNewPageCheckMenu().isSelected();
	}

	/**
	 * Checks whether any column in group list is selected.
	 * @return the condition about group existence
	 */
	public boolean existGroups() {
		if (this.reportDialog.getSelectedGroupColumns().size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the number of groups that user is viewing printed in report (This number matches with
	 * number of items selected in group column list when all group columns are also selected in
	 * printing column list).
	 * @return the number of viewed groups
	 */
	public int getRealNumberOfGroups() {
		int iRealGroups = 0;
		for (int i = 0; i < this.reportDialog.getSelectedGroupColumns().size(); i++) {
			if (this.reportDialog.getSelectedPrintingColumns()
					.contains(this.reportDialog.getSelectedGroupColumns().get(i))) {
				iRealGroups++;
			}
		}
		return iRealGroups;
	}

	/**
	 * For a group column checks whether this column is being viewed (selected in printing column list).
	 * @param column the index of column
	 * @return true when it is visible
	 */
	public boolean isGroupColumnPrinted(final int column) {
		if (this.reportDialog.getSelectedPrintingColumns()
				.contains(this.reportDialog.getSelectedGroupColumns().get(column))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Checks whether any column in function list is selected. It is not considered the item that shows
	 * the number of occurrences.
	 * @return the condition about function existence
	 */
	public boolean existFunctions() {
		if (this.reportDialog.getSelectedFunctionColumns().size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether show grid option is selected.
	 * @return condition about grid visibility
	 */
	public boolean isSelectedShowGrid() {
		return this.reportDialog.getGrid1CheckMenu().isSelected();
	}

	/**
	 * Checks whether row number column item in pop-up menu for options is selected.
	 * @return the condition about function existence
	 */
	public boolean isShowedRowNumber() {
		return this.reportDialog.getRowNumber2CheckMenu().isSelected();
	}

	/**
	 * Checks whether number of occurrences in function panel is selected.
	 * @return condition about visibility of number of occurrences
	 */
	public boolean isSelectedNumberOfOccurrences() {
		return ((PredefinedFunctionItem) this.reportDialog.getFunctionList()
				.getModel()
				.getElementAt(this.reportDialog.getFunctionList().getModel().getSize() - 1)).isSelected();
	}

	/**
	 * Checks whether UpdateDynamically toggle button is pressed.
	 * @return the condition to update dynamically the report
	 */
	public boolean isSelectedUpdateDynamically() {
		return this.reportDialog.getUpdateCheck().isSelected();
	}

	/**
	 * Checks whether column names are printed.
	 * @return the condition to update dynamically the report
	 */
	public boolean isSelectedPrintColumnNames() {
		return this.reportDialog.getIncludeColumnName3CheckMenu().isSelected();
	}

	/**
	 * Checks whether exist virtual columns. Virtual columns are columns added automatically to allow
	 * grouping by month, year, ... in date columns.
	 * @return Condition about existence of virtual columns
	 */
	public boolean existVirtualColumns() {
		if (this.hVirtualColumns.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the column parameter is a virtual column.
	 * @param column The name of column
	 * @return The condition
	 */
	public boolean isVirtualColumn(final String column) {
		if (column == null) {
			return false;
		}
		return this.hVirtualColumns.containsKey(column) ? true : false;
	}

	/**
	 * Checks whether a column is the original column for a virtual column.
	 * @param virtualColumn VirtualColumn
	 * @return
	 */
	public boolean isBaseColumnFromVirtualColumn(final String column) {
		if (column == null) {
			return false;
		}
		return this.hVirtualColumns.containsKey(column) ? true : false;
	}

	/**
	 * Clear table model and virtual columns <code>Hashtable</code>..
	 */
	public void clearVirtualColumns() {
		if (this.hVirtualColumns != null) {
			this.hVirtualColumns.clear();
		}
	}

	/**
	 * Creates the JPopUpMenu for options: include row number, show column names,...
	 */
	@Override
	public void buildOptions() {
		final UpdateReportListener updateReportListener = this.reportDialog.getUpdateReportListener();
		final JPopupMenu optionMenu = new JPopupMenu();
		optionMenu.add(this.reportDialog.getGrid1CheckMenu());
		this.reportDialog.getGrid1CheckMenu().addActionListener(updateReportListener);
		optionMenu.add(this.reportDialog.getRowNumber2CheckMenu());
		this.reportDialog.getRowNumber2CheckMenu().addActionListener(updateReportListener);
		optionMenu.add(this.reportDialog.getIncludeColumnName3CheckMenu());
		this.reportDialog.getIncludeColumnName3CheckMenu().addActionListener(updateReportListener);
		optionMenu.add(this.reportDialog.getHideGroupDetail7CheckMenu());
		this.reportDialog.getHideGroupDetail7CheckMenu().addActionListener(updateReportListener);
		this.reportDialog.getGroupStartInNewPageCheckMenu().addActionListener(updateReportListener);
		this.reportDialog.getFirstGroupStartInNewPageCheckMenu().addActionListener(updateReportListener);

		optionMenu.add(this.reportDialog.getGroupStartInNewPageCheckMenu());
		optionMenu.add(this.reportDialog.getFirstGroupStartInNewPageCheckMenu());

		optionMenu.pack();
		this.reportDialog.setOptionMenu(optionMenu);
	}

	/**
	 * Checks whether column class is <code>sun.awt.image.ToolkitImage</code>. It is used to allow
	 * rendering images in report.
	 * @param imageClass the class for image.
	 * @return the condition for column class
	 */
	public boolean isImageColumn(final Class imageClass) {
		return (imageClass != null) && imageClass.isAssignableFrom(Image.class);
	}


	/**
	 * Checks whether column class is a BooleanImage.
	 * @param booleanImageClass the class for image.
	 * @return the condition for column class
	 */
	public boolean isBooleanImageColumn(final Class booleanImageClass) {
		return (booleanImageClass != null) && booleanImageClass.isAssignableFrom(BooleanImage.class);
	}

	/**
	 * Checks whether column class is a Date.
	 * @param dateClass the class for date.
	 * @return the condition for column class
	 */
	public boolean isDateColumn(final Class dateClass) {
		if (dateClass != null) {
			if (java.util.Date.class.isAssignableFrom(dateClass)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Gets the text for operation applied in column
	 * @param operation The operation
	 * @return The text for operation parameter
	 */
	public String getTextOperation(final Integer operation) {
		if (operation.intValue() == ReportUtils.SUM) {
			return ApplicationManager.getTranslation(DefaultReportDialog.SUM_OP_KEY, this.reportDialog.getBundle());
		}
		if (operation.intValue() == ReportUtils.MAX) {
			return ApplicationManager.getTranslation(DefaultReportDialog.MAXIMUM_OP_KEY, this.reportDialog.getBundle());
		}
		if (operation.intValue() == ReportUtils.MIN) {
			return ApplicationManager.getTranslation(DefaultReportDialog.MINIMUM_OP_KEY, this.reportDialog.getBundle());
		}
		if (operation.intValue() == ReportUtils.AVG) {
			return ApplicationManager.getTranslation(DefaultReportDialog.AVERAGE_OP_KEY, this.reportDialog.getBundle());
		}
		return ApplicationManager.getTranslation(DefaultReportDialog.SUM_OP_KEY, this.reportDialog.getBundle());
	}

	/**
	 * Gets the operation to use in variables of footer (SUM, MAX, MIN, AVG).
	 * @param operation the operation
	 * @return the correspondent <code>DJCalculation</code> object.
	 */
	public DJCalculation getOperation(final Integer operation) {
		if (operation.intValue() == ReportUtils.SUM) {
			return DJCalculation.SUM;
		}
		if (operation.intValue() == ReportUtils.MAX) {
			return DJCalculation.HIGHEST;
		}
		if (operation.intValue() == ReportUtils.MIN) {
			return DJCalculation.LOWEST;
		}
		if (operation.intValue() == ReportUtils.AVG) {
			return DJCalculation.AVERAGE;
		}
		return DJCalculation.COUNT;
	}

	/**
	 * Returns the name of report Engine: "DynamicJasper".
	 */
	@Override
	public String getReportEngineName() {
		return "DynamicJasper 5.0.0";
	}

	/**
	 * Requires:
	 * <ul>
	 * <li>Dynamic jasper 3.09
	 * <li>Jasperreports 3.5.1
	 * <li>commons-beanutils
	 * <li>commons-collections
	 * <li>commons-logging
	 * <li>iText2.0 (only for PDF export)
	 * </ul>
	 */
	@Override
	public boolean checkLibraries() {
		return DynamicJasperEngine.checkJasperLibraries();
	}

	protected static boolean checkJasperLibreries;

	protected static boolean jasperLibreries;

	public static boolean checkJasperLibraries() {
		if (DynamicJasperEngine.checkJasperLibreries) {
			return DynamicJasperEngine.jasperLibreries;
		} else {
			DynamicJasperEngine.checkJasperLibreries = true;
			try {
				Class.forName("ar.com.fdvs.dj.domain.DynamicReport");
			} catch (final ClassNotFoundException e) {
				DynamicJasperEngine.logger.debug(
						"{}: Error: DynamicJasper library missed (required version 3.0.13). Check the classpath",
						DynamicJasperEngine.class.getClass().getName(), e);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("net.sf.jasperreports.engine.JasperPrint");
			} catch (final ClassNotFoundException e1) {
				DynamicJasperEngine.logger.debug(
						"{}: Error: Jasperreports library missed (required version 3.5.1). Check the classpath",
						DynamicJasperEngine.class.getName(), e1);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("org.apache.commons.beanutils.BeanUtils");
			} catch (final ClassNotFoundException e1) {
				DynamicJasperEngine.logger.debug("{}: Error: Commons-beanutils library missed. Check the classpath",
						DynamicJasperEngine.class.getName(), e1);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("org.apache.commons.collections.CollectionUtils");
			} catch (final ClassNotFoundException e1) {
				DynamicJasperEngine.logger.debug("{}: Error: Commons-collections library missed. Check the classpath",
						DynamicJasperEngine.class.getName(), e1);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("org.apache.commons.logging.Log");
			} catch (final ClassNotFoundException e) {
				DynamicJasperEngine.logger.debug("{}: Error: Commons-logging library missed. Check the classpath",
						DynamicJasperEngine.class.getClass().getName(), e);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("org.apache.commons.digester.Digester");
			} catch (final ClassNotFoundException e) {
				DynamicJasperEngine.logger.debug("{}: Error: Commons-digester library missed. Check the classpath",
						DynamicJasperEngine.class.getClass().getName(), e);
				DynamicJasperEngine.jasperLibreries = false;
				return DynamicJasperEngine.jasperLibreries;
			}
			try {
				Class.forName("com.lowagie.text.factories.ElementFactory");
			} catch (final ClassNotFoundException e) {
				DynamicJasperEngine.logger.debug(
						"{}: Warning: iText 2.0 library missed (PDF export will not be supported). Check the classpath",
						DynamicJasperEngine.class.getClass().getName(), e);
				DynamicJasperEngine.jasperLibreries = true;
				return DynamicJasperEngine.jasperLibreries;
			}
			DynamicJasperEngine.logger.debug("{}: All libraries encountered",
					DynamicJasperEngine.class.getClass().getName());
			DynamicJasperEngine.jasperLibreries = true;
			return DynamicJasperEngine.jasperLibreries;
		}
	}

	/**
	 * Creates a table model that replaces complete null columns by empty strings.
	 * @return the table model with data.
	 */
	public static TableModel createTableModel(final Map res, final boolean returnEmptyStrings, final boolean convertBB2Im,
			final boolean convertBooleanToIm) {
		final EntityResult result = new EntityResultMapImpl();
		final Enumeration enumKeys = Collections.enumeration(res.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			Collections.replaceAll((List) res.get(oKey), null, "");
			result.put(oKey, res.get(oKey));

		}
		return new EntityResultTableModel(result, returnEmptyStrings, convertBB2Im, convertBooleanToIm);
	}

	/**
	 * Views jasper dialog from a .jrxml template.
	 * @param template the .jrxml template
	 * @throws Exception When a <code>Jasper Exception</code> occurs
	 */
	public void viewJasperDialog(final String template) throws Exception {
		final JasperPrint jp = JasperFillManager.fillReport(template, this.parameters);
		JasperViewer.viewReport(jp);
	}

	/**
	 * Fills and views jasper report from a compiled report template without jasper parameters.
	 * @param template Complete path to the compiled report
	 * @param title of window where report is showed
	 * @throws Exception When a <code>Jasper Exception</code> occurs
	 */
	public void viewJasperDialog(final String template, final String title) throws Exception {
		final JasperPrint jp = JasperFillManager.fillReport(template, new Hashtable());
		com.ontimize.report.utils.JasperViewer.viewReport(jp, title);
	}

	/**
	 * Fills and views jasper report from a compiled report template without jasper parameters.
	 * @param component Complete path to the compiled report
	 * @param template Complete path to the compiled report
	 * @param title of window where report is showed
	 * @throws Exception When a <code>Jasper Exception</code> occurs
	 */
	public void viewJasperDialog(final Component parent, final String template, final String title) throws Exception {
		final JasperPrint jp = JasperFillManager.fillReport(template, new Hashtable());
		com.ontimize.report.utils.JasperViewer.viewReport(jp, title);
	}

	/**
	 * Views jasper dialog from a URL where is the template.
	 * @param template URL where is the template.
	 * @param data <code>Hashtable</code> or <code>EntityResult</code> with data.
	 * @throws Exception Never throws Exception. When an Exception occurs filling report is catched
	 *         internally
	 */
	public void viewJasperDialog(final URL template, Map data, Map reportParameters) throws Exception {
		InputStream isReport = null;
		try {
			if (data == null) {
				data = new EntityResultMapImpl();
			}
			if (reportParameters == null) {
				reportParameters = new Hashtable();
			}
			isReport = template.openStream();
			final JasperReport jrReport = JasperCompileManager.compileReport(isReport);
			final JasperPrint jp = JasperFillManager.fillReport(jrReport, reportParameters,
					new TableModelDataSource(EntityResultUtils.createTableModel(data)));
			// View report
			com.ontimize.report.utils.JasperViewer.viewReport(jp, "");

		} catch (final Exception e) {
			DynamicJasperEngine.logger.error("{}: {}", this.getClass().getName(), e.getMessage(), e);
		} finally {
			if (isReport != null) {
				isReport.close();
			}
		}
	}

	/**
	 * Views jasper dialog from a URL where is the template.
	 * @param template URL where is the template.
	 * @throws Exception Never throws Exception. When an Exception occurs filling report is catched
	 *         internally
	 */
	public void viewJasperDialog(final URL template, TableModel dataModel, Map reportParameters) throws Exception {
		InputStream isReport = null;
		try {
			if (dataModel == null) {
				dataModel = EntityResultUtils.createTableModel((EntityResult) new EntityResultMapImpl());
			}
			if (reportParameters == null) {
				reportParameters = new Hashtable();
			}
			isReport = template.openStream();
			final JasperReport jrReport = JasperCompileManager.compileReport(isReport);
			final JasperPrint jp = JasperFillManager.fillReport(jrReport, reportParameters,
					new TableModelDataSource(dataModel));
			// View report
			JasperViewer.viewReport(jp);

		} catch (final Exception e) {
			DynamicJasperEngine.logger.error("{}: {}", this.getClass().getName(), e.getMessage(), e);
		} finally {
			if (isReport != null) {
				isReport.close();
			}
		}
	}

	/**
	 * Views jasper dialog from a URL where is the template.
	 * @param template URL where is the template.
	 * @throws Exception Never throws Exception. When an Exception occurs filling report is catched
	 *         internally
	 */
	public void viewJasperDialog(final URL template, TableModel dataModel, final String title, Map reportParameters)
			throws Exception {
		InputStream isReport = null;
		try {
			if (dataModel == null) {
				dataModel = EntityResultUtils.createTableModel((EntityResult) new EntityResultMapImpl());
			}
			if (reportParameters == null) {
				reportParameters = new Hashtable();
			}
			isReport = template.openStream();
			final JasperReport jrReport = JasperCompileManager.compileReport(isReport);
			final JasperPrint jp = JasperFillManager.fillReport(jrReport, reportParameters,
					new TableModelDataSource(dataModel));
			// View report
			com.ontimize.report.utils.JasperViewer.viewReport(jp, title);

		} catch (final Exception e) {
			DynamicJasperEngine.logger.error("{}: {}", this.getClass().getName(), e.getMessage(), e);
		} finally {
			if (isReport != null) {
				isReport.close();
			}
		}
	}

	/**
	 * Views jasper dialog from a URL where is the template.
	 * @param template URL where is the template.
	 * @throws Exception Never throws Exception. When an Exception occurs filling report is catched
	 *         internally
	 */
	public void viewJasperDialog(final Component parent, final URL template, TableModel dataModel, final String title,
			Map reportParameters) throws Exception {
		InputStream isReport = null;
		try {
			if (dataModel == null) {
				dataModel = EntityResultUtils.createTableModel((EntityResult) new EntityResultMapImpl());
			}
			if (reportParameters == null) {
				reportParameters = new Hashtable();
			}
			isReport = template.openStream();
			final JasperReport jrReport = JasperCompileManager.compileReport(isReport);
			final JasperPrint jp = JasperFillManager.fillReport(jrReport, reportParameters,
					new TableModelDataSource(dataModel));
			// View report
			com.ontimize.report.utils.JasperViewerDialog.viewReport(parent, jp, title);

		} catch (final Exception e) {
			DynamicJasperEngine.logger.error("{}: {}", this.getClass().getName(), e.getMessage(), e);
		} finally {
			if (isReport != null) {
				isReport.close();
			}
		}
	}

	public List getMultiGroupColumns() {
		return this.vMultiGroupColumns;
	}

	public Map getVirtualColumns() {
		return this.hVirtualColumns;
	}

	public boolean isColumnGroupInSimpleGroup(String groupColumn) {
		if (this.isVirtualColumn(groupColumn)) {
			groupColumn = this.getColumnFromVirtualColumn(groupColumn);
		}
		if (!this.reportDialog.getSimpleSelectedGroupColumns().contains(groupColumn)) {
			return false;
		}
		for (int i = 0; i < this.reportDialog.getSelectedGroupItems().size(); i++) {
			final Object o = this.reportDialog.getSelectedGroupItems().get(i);
			if (o instanceof com.ontimize.report.item.SelectableItem) {
				if (((com.ontimize.report.item.SelectableItem) o).getText().equals(groupColumn)
						|| this.isVirtualColumn(groupColumn)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Fills and view report from template that contains <b>compiled report</b> without parameters.
	 * @param template The complete url to compiled report
	 * @throws Exception When occurs an <code>Exception</code> filling report
	 */
	public void viewJasperDialog(final URL template) throws Exception {
		final JasperPrint jp = JasperFillManager.fillReport(template.openStream(), new Hashtable());
		com.ontimize.report.utils.JasperViewer.viewReport(jp);
	}

	/**
	 * Fills and view report from template that contains <b>compiled report</b> without parameters.
	 * @param parent Parent component to report
	 * @param template The complete url to compiled report
	 * @throws Exception When occurs an <code>Exception</code> filling report
	 */
	public void viewJasperDialog(final Component parent, final URL template) throws Exception {
		final JasperPrint jp = JasperFillManager.fillReport(template.openStream(), new Hashtable());
		com.ontimize.report.utils.JasperViewerDialog.viewReport(parent, jp);
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final String template, final URL base)
			throws Exception {
		if (template != null) {
			final URL urlTemplate = this.getClass().getResource(template);
			if (urlTemplate != null) {
				this.viewJasperDialog(c, urlTemplate, m, title, new Hashtable());
			} else {
				throw new IllegalArgumentException(
						this.getClass().getName() + ": template: '" + template + "' not found.");
			}
		} else {
			throw new IllegalArgumentException(this.getClass().getName() + ": Parameter template cannot be null.");
		}
		return null;
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base)
			throws Exception {
		if (template != null) {
			this.viewJasperDialog(c, template, m, title, new Hashtable());
		} else {
			throw new IllegalArgumentException(this.getClass().getName() + ": Parameter template cannot be null.");
		}
		return null;
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final String template, final URL base,
			final ReportProcessor rp) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final ReportProcessor rp) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor rp) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog showPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor rp, final PageFormat pf)
					throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final ReportProcessor r) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base)
			throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor r) throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	@Override
	public PreviewDialog getPreviewDialog(final Component c, final String title, final TableModel m, final URL template, final URL base,
			final String[] order, final boolean[] asc, final ReportProcessor r, final PageFormat pf)
					throws Exception {
		return this.showPreviewDialog(c, title, m, template, base);
	}

	public String getMonthText(final int month) {
		switch (month) {
		case 1:
			return ApplicationManager.getTranslation(DynamicJasperEngine.JANUARY, this.reportDialog.getBundle());
		case 2:
			return ApplicationManager.getTranslation(DynamicJasperEngine.FEBRUARY, this.reportDialog.getBundle());
		case 3:
			return ApplicationManager.getTranslation(DynamicJasperEngine.MARCH, this.reportDialog.getBundle());
		case 4:
			return ApplicationManager.getTranslation(DynamicJasperEngine.APRIL, this.reportDialog.getBundle());
		case 5:
			return ApplicationManager.getTranslation(DynamicJasperEngine.MAY, this.reportDialog.getBundle());
		case 6:
			return ApplicationManager.getTranslation(DynamicJasperEngine.JUNE, this.reportDialog.getBundle());
		case 7:
			return ApplicationManager.getTranslation(DynamicJasperEngine.JULY, this.reportDialog.getBundle());
		case 8:
			return ApplicationManager.getTranslation(DynamicJasperEngine.AUGUST, this.reportDialog.getBundle());
		case 9:
			return ApplicationManager.getTranslation(DynamicJasperEngine.SEPTEMBER, this.reportDialog.getBundle());
		case 10:
			return ApplicationManager.getTranslation(DynamicJasperEngine.OCTOBER, this.reportDialog.getBundle());
		case 11:
			return ApplicationManager.getTranslation(DynamicJasperEngine.NOVEMBER, this.reportDialog.getBundle());
		case 12:
			return ApplicationManager.getTranslation(DynamicJasperEngine.DECEMBER, this.reportDialog.getBundle());
		default:
			return "";
		}
	}

	public String getQuarterText(final int quarter) {
		switch (quarter) {
		case 1:
			return ApplicationManager.getTranslation(DynamicJasperEngine.FIRST_QUARTER,
					this.reportDialog.getBundle());
		case 2:
			return ApplicationManager.getTranslation(DynamicJasperEngine.SECOND_QUARTER,
					this.reportDialog.getBundle());
		case 3:
			return ApplicationManager.getTranslation(DynamicJasperEngine.THIRD_QUARTER,
					this.reportDialog.getBundle());
		case 4:
			return ApplicationManager.getTranslation(DynamicJasperEngine.FOURTH_QUARTER,
					this.reportDialog.getBundle());
		default:
			return "";
		}
	}

	@Override
	public String getXMLTemplate() throws IOException {
		return null;
	}

	@Override
	public Map getColumnWidth() {
		return new Hashtable();
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		if (this.viewer != null) {
			this.viewer.setResourceBundle(resourceBundle);
		}
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	protected int fontSize = 15;

	public void setFontSize(final int fontSize) {
		this.fontSize = fontSize;
	}

	public Font configureFont(final Font source, final int increment) {
		final Font finalFont = (Font) source.clone();
		finalFont.setFontSize(this.fontSize + increment);
		return finalFont;
	}

	protected Font getDefaultHeaderFont() {
		final Font source = DynamicJasperStyles.defaultHeaderFont;
		return this.configureFont(source, 1);
	}

	protected Font getDefaultTitleFont() {
		final Font source = DynamicJasperStyles.defaultTitleFont;
		return this.configureFont(source, 3);
	}

	protected Font getDefaultSubtitleFont() {
		final Font source = DynamicJasperStyles.defaultSubtitleFont;
		return this.configureFont(source, 1);
	}

	protected Font getDefaultHeaderForGroupFont() {
		final Font source = DynamicJasperStyles.defaultHeaderForGroupFont;
		return this.configureFont(source, 1);
	}

	protected Font getDefaultColumnDataFont() {
		final Font source = DynamicJasperStyles.defaultColumnDataFont;
		return this.configureFont(source, 0);
	}

	protected Font getDefaultGroupFooterVariableFont() {
		final Font source = DynamicJasperStyles.defaultGroupFooterVariableFont;
		return this.configureFont(source, 0);
	}

	protected Font getDefaultGroupFooterNumberOcurrencesVariableFont() {
		final Font source = DynamicJasperStyles.defaultGroupFooterNumberOcurrencesVariableFont;
		return this.configureFont(source, 0);
	}

	protected Font getDefaultFooterVariableFont() {
		final Font source = DynamicJasperStyles.defaultFooterVariableFont;
		return this.configureFont(source, 1);
	}

}
