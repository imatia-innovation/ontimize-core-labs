package com.ontimize.chart;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.AbstractRenderer;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Month;
import org.jfree.data.time.Quarter;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.FontChooserPanel;
import org.jfree.ui.FontDisplayField;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.TextAnchor;
import org.jfree.util.TableOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.RolloverButton;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.table.ChartButton;
import com.ontimize.gui.table.ExtendedTableModel;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.util.CollectionTools;

/**
 * Convenience class to manage the 1.0 version of JFreeChart.
 *
 * @author Imatia Innovation
 */
public class ChartUtilities_1_0 implements IChartUtilities {

	private static final Logger logger = LoggerFactory.getLogger(ChartUtilities_1_0.class);

	public static boolean DEBUG = false;

	public static final String ROW_NUMBERS_KEY = "chartutilities.row_count";

	public static final int PIE = 0;

	public static final int PIE_3D = 1;

	public static final int BAR = 2;

	public static final int BAR_3D = 3;

	public static final int STACKED_3D = 4;

	public static final int LINE = 5;

	public static final int DAY = ChartInfo.DAY;

	public static final int MONTH = ChartInfo.MONTH;

	public static final int QUARTER = ChartInfo.QUARTER;

	public static final int YEAR = ChartInfo.YEAR;

	public static final int SUM = 0;

	public static final int MAX = 1;

	public static final int MIN = 2;

	public static final int AVG = 3;

	/*
	 * Set foreground color
	 */
	public static Color axisTextPaint = Color.BLACK;

	protected class ChartDialog extends JDialog {

		protected ChartPanel chartPanel = null;

		public ChartDialog(final Dialog owner, final String title, final JFreeChart chart) {
			super(owner, title, false);
			this.chartPanel = new ChartPanel(chart);
			this.setContentPane(this.chartPanel);
		}

		public ChartPanel getChartPanel() {
			return this.chartPanel;
		}

	}

	protected Table t = null;

	protected Object charts = null;

	protected Object chartInfo = null;

	public ChartUtilities_1_0(final Table t) {
		this.t = t;
	}

	@Override
	public ChartInfoRepository getChartInfoRepository() {
		if (this.chartInfo == null) {
			return null;
		}
		return (ChartInfoRepository) this.chartInfo;
	}

	@Override
	public void showChart(final String description) {
		if (!Table.isChartEnabled()) {
			return;
		}
		final Window w = SwingUtilities.getWindowAncestor(this.t);
		if (w instanceof Frame) {
			final ChartFrame f = new ChartFrame(description, this.getChart(description));
			f.setIconImage(((Frame) w).getIconImage());
			f.getChartPanel().setMouseZoomable(true);
			f.pack();
			f.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			ApplicationManager.center(f);
			f.setVisible(true);
		} else {
			final ChartDialog d = new ChartDialog((Dialog) w, description, this.getChart(description));
			d.getChartPanel().setMouseZoomable(true);

			d.pack();
			d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			ApplicationManager.center(d);
			d.setVisible(true);
		}

	}

	protected XYDataset getXYDataset(final String colX, final String colY, final String series, final int timeInterval) {
		return this.getXYDataset(colX, colY, series, timeInterval, true, ChartUtilities_1_0.SUM, (CategoryData) null);
	}

	protected XYDataset getXYDataset(final String colX, final String[] colY, final String[] series, final int timeInterval, final boolean fillCeros) {
		return this.getXYDataset(colX, colY, series, timeInterval, fillCeros, null);
	}

	protected XYDataset getXYDataset(final String colX, final String[] colY, final String[] series, final int timeInterval, final boolean fillZeros,
			final int[] operations) {
		TimeSeriesCollection set = null;
		for (int i = 0; i < series.length; i++) {
			int operation = ChartUtilities_1_0.SUM;
			if ((operations != null) && (operations.length > i)) {
				operation = operations[i];
			}
			final TimeSeriesCollection serie = (TimeSeriesCollection) this.getXYDataset(colX, colY[i], series[i],
					timeInterval, fillZeros, operation, (CategoryData) null);
			if (set == null) {
				set = serie;
			} else if (serie != null) {
				set.addSeries(serie.getSeries(0));
			}
		}
		return set;
	}

	protected XYDataset getXYDataset(final String colX, final String colY[], final String series[], final int timeInterval, final boolean fillCeros,
			final int operations[], final String columnSeriesValuesGen[]) {
		TimeSeriesCollection set = null;
		for (int i = 0; i < series.length; i++) {
			int iOperation = ChartUtilities_1_0.SUM;
			if ((operations != null) && (operations.length > i)) {
				iOperation = operations[i];
			}
			final TimeSeriesCollection serie = (TimeSeriesCollection) this.getXYDataset(colX, colY[i], series[i],
					timeInterval, fillCeros, iOperation,
					this.getCategoryData(colX, colY[i], iOperation,
							columnSeriesValuesGen == null ? null : columnSeriesValuesGen[i], ChartInfo.NONE));
			if (set == null) {
				set = serie;
				continue;
			}
			if (serie == null) {
				continue;
			}
			for (int j = 0; j < serie.getSeriesCount(); j++) {
				set.addSeries(serie.getSeries(j));
			}

		}

		return set;
	}

	/**
	 * @param colX
	 * @param colY
	 * @param series
	 * @param timeInterval
	 * @param fillZeros
	 * @param operation Accepts values SUM, MAX and MIN. AVG value not available
	 * @return
	 */
	protected XYDataset getXYDataset(final String colX, final String colY, String series, final int timeInterval, final boolean fillZeros,
			final int operation, final CategoryData categoryData) {

		if (!Table.isChartEnabled()) {
			return null;
		}

		final long t = System.currentTimeMillis();
		final List xDefData = categoryData.getXData();
		final List[] yDefDataList = categoryData.getYData();
		final Object[] serieValues = categoryData.getSerieValue();

		if (yDefDataList != null) {
			TimeSeriesCollection set = null;
			for (int k = 0; k < yDefDataList.length; k++) {
				final List yDefData = yDefDataList[k];
				final Object serieValue = serieValues == null ? null : serieValues[k];
				if (categoryData.getSerie() != null) {
					if (serieValue != null) {
						series = series + "("
								+ ApplicationManager.getTranslation(categoryData.getSerie(), this.t.getResourceBundle())
								+ "=" + serieValue + ")";
					} else {
						series = series + "("
								+ ApplicationManager.getTranslation(categoryData.getSerie(), this.t.getResourceBundle())
								+ "= )";
					}
				}
				if (xDefData.isEmpty() || yDefData.isEmpty()) {
					continue;
				}
				final Map<RegularTimePeriod, List<Number>> dataPeriod = this.getPeriodValues(xDefData, yDefData,
						timeInterval);

				TimeSeries tSeries = null;
				switch (timeInterval) {
				case ChartInfo.DAY:
					// Creates the dataset depending of the interval
					tSeries = new TimeSeries(series, Day.class);
					break;
				case ChartInfo.MONTH:
					tSeries = new TimeSeries(series, Month.class);
					break;
				case ChartInfo.QUARTER:
					tSeries = new TimeSeries(series, Quarter.class);
					break;
				case ChartInfo.YEAR:
					tSeries = new TimeSeries(series, Year.class);
					break;
				default:
					tSeries = new TimeSeries(series, Month.class);
					break;
				}

				for (final Entry<RegularTimePeriod, List<Number>> entry : dataPeriod.entrySet()) {
					final RegularTimePeriod period = entry.getKey();
					final List<Number> yValuesList = entry.getValue();
					double newValue = 0.0D;
					for (int i = 0; i < yValuesList.size(); i++) {
						double toAdd = 0.0;
						if (yValuesList.get(i) != null) {
							toAdd = yValuesList.get(i).doubleValue();
						}
						switch (operation) {
						case SUM:
							newValue = newValue + toAdd;
							break;
						case AVG:
							newValue = newValue + (toAdd / yValuesList.size());
							break;
						case MAX:
							if (i == 0) {
								newValue = Integer.MIN_VALUE;
							}
							newValue = Math.max(newValue, toAdd);
							break;
						case MIN:
							if (i == 0) {
								newValue = Integer.MAX_VALUE;
							}
							newValue = Math.min(newValue, toAdd);
							break;
						default:
							newValue = newValue + toAdd;
							break;
						}
						if (tSeries.getIndex(period) >= 0) {
							final TimeSeriesDataItem current = tSeries.getDataItem(tSeries.getIndex(period));
							current.setValue(new Double(newValue));
						} else {
							tSeries.add(period, new Double(newValue));
						}
					}
				}

				RegularTimePeriod start = null;
				RegularTimePeriod end = null;
				RegularTimePeriod current = null;
				set = new TimeSeriesCollection(tSeries);
				start = tSeries.getTimePeriod(0);
				end = tSeries.getTimePeriod(tSeries.getItemCount() - 1);
				current = start;

				while (current.compareTo(end) < 0) {
					final RegularTimePeriod next = current.next();
					if (tSeries.getDataItem(next) == null) {
						// If not found, add it
						if (fillZeros) {
							tSeries.add(next, new Double(0.0));
						}
						current = next;
					} else {
						// If it is found get the next
						current = next;
					}
				}
			}

			ChartUtilities_1_0.logger.debug("getXYDataset, time {}", System.currentTimeMillis() - t);
			return set;
		}

		ChartUtilities_1_0.logger.debug("Request chart: return null");
		return null;
	}

	protected CategoryDataset getCategoryDataset(final String colX, final String[] colsY, final String[] seriesNames, final int timeGrouping) {
		return this.getCategoryDataset(colX, colsY, seriesNames, null, timeGrouping);
	}

	protected CategoryDataset getCategoryDataset(final String colX, final String[] colsY, final String[] seriesNames, final int[] operations,
			final int timeGrouping) {
		return this.getCategoryDataset(colX, colsY, seriesNames, operations, null, timeGrouping);
	}

	protected CategoryDataset getCategoryDataset(final String colX, final String colsY[], final String seriesNames[], final int operations[],
			final String columnSeriesValuesGen[], final int timeGrouping) {
		if (!Table.isChartEnabled()) {
			return null;
		}
		// Data vectors
		final long t = System.currentTimeMillis();

		List xData = null;
		Object[] seriesValues = null;
		final List seriesNamesList = new ArrayList();

		final List yDataList = new ArrayList();
		for (int i = 0; i < colsY.length; i++) {
			int iOperation = ChartUtilities_1_0.SUM;
			if ((operations != null) && (operations.length > i)) {
				iOperation = operations[i];
			}
			final CategoryData categoryData = this.getCategoryData(colX, colsY[i], iOperation,
					columnSeriesValuesGen == null ? null : columnSeriesValuesGen[i], timeGrouping);
			xData = categoryData.getXData();
			seriesValues = categoryData.getSerieValue();
			final java.util.List lLists[] = categoryData.getYData();
			for (int j = 0; j < lLists.length; j++) {
				yDataList.add(lLists[j]);
				if (seriesValues != null) {
					seriesNamesList.add(seriesNames[i] + " - " + seriesValues[j]);
				} else {
					seriesNamesList.add(seriesNames[i]);
				}
			}

		}

		List datosYDef = (java.util.List) yDataList.get(0);

		final double[][] d = new double[yDataList.size()][datosYDef.size()];
		for (int i = 0; i < datosYDef.size(); i++) {
			for (int j = 0; j < yDataList.size(); j++) {
				datosYDef = (java.util.List) yDataList.get(j);
				final Number current = (Number) datosYDef.get(i);
				if (current != null) {
					d[j][i] = current.doubleValue();
				}
			}
		}

		final Comparable[] columnKey = new Comparable[xData.size()];
		for (int i = 0; i < columnKey.length; i++) {
			if ((xData.get(i) instanceof Comparable) || (xData.get(i) == null)) {
				columnKey[i] = (Comparable) xData.get(i);
			} else {
				columnKey[i] = xData.get(i).toString();
			}
		}

		final Comparable[] rowKey = new Comparable[seriesNamesList.size()];
		for (int i = 0; i < rowKey.length; i++) {
			if ((seriesNamesList.get(i) instanceof Comparable) || (seriesNamesList.get(i) == null)) {
				rowKey[i] = (Comparable) seriesNamesList.get(i);
			} else {
				rowKey[i] = seriesNamesList.get(i).toString();
			}
		}

		final CategoryDataset set = DatasetUtilities.createCategoryDataset(rowKey, columnKey, d);

		ChartUtilities_1_0.logger.debug("getCategoryDataset, time: {}", System.currentTimeMillis() - t);
		return set;
	}

	protected PieDataset getPieDataset(final String colX, final String colY, final int timeGrouping) {
		return this.getPieDataset(colX, colY, ChartUtilities_1_0.SUM, timeGrouping);
	}

	protected PieDataset getPieDataset(final String colX, final String colY, final int operation, final int timeGrouping) {
		if (!Table.isChartEnabled()) {
			return null;
		}
		final PieDataset set = DatasetUtilities.createPieDatasetForRow(
				this.getCategoryDataset(colX, new String[] { colY }, new String[] { colY }, timeGrouping), 0);
		return set;
	}

	public JFreeChart getChart(final String description) {
		if (!Table.isChartEnabled()) {
			return null;
		}

		final ChartInfo info = ((ChartInfoRepository) this.chartInfo).getChartInfo(description);

		if (info.hasIntervals()) {
			final XYDataset xySet = this.getXYDataset(info.colX, info.colsY, info.series, info.intervalType, info.fillZeros(),
					info.getOperations(), info.getColumnSeriesValuesGen());
			final JFreeChart chart = ChartFactory.createTimeSeriesChart(description, info.xLabel, info.yLabel, xySet, true,
					true, true);
			((ChartRepository_1_0) this.charts).addChart(chart, description);
		} else {
			final int type = info.type;
			switch (type) {
			case PIE:
				CategoryDataset set = this.getCategoryDataset(info.colX, info.colsY, info.series,
						info.getOperations(), info.getColumnSeriesValuesGen(), info.intervalType);
				// PieDataset pieData =
				// DatasetUtilities.createPieDatasetForRow(set, 0);
				JFreeChart chart = ChartFactory.createMultiplePieChart(description, set, TableOrder.BY_ROW, true,
						true, true);

				final StandardPieSectionLabelGenerator labelGenerator = new StandardPieSectionLabelGenerator(
						"{0} = {1} ({2})");
				Plot plot = chart.getPlot();

				if (plot instanceof MultiplePiePlot) {
					plot = ((MultiplePiePlot) plot).getPieChart().getPlot();
				}
				if (plot instanceof PiePlot) {
					((PiePlot) plot).setLabelGenerator(labelGenerator);
				}
				((ChartRepository_1_0) this.charts).addChart(chart, description);

				break;
			case PIE_3D:
				set = this.getCategoryDataset(info.colX, info.colsY, info.series, info.getOperations(),
						info.getColumnSeriesValuesGen(), info.intervalType);
				// PieDataset set2 =
				// DatasetUtilities.createPieDatasetForRow(set, 0);
				chart = ChartFactory.createMultiplePieChart3D(description, set, TableOrder.BY_ROW, true, true,
						true);
				((ChartRepository_1_0) this.charts).addChart(chart, description);
				break;
			case BAR:
				set = this.getCategoryDataset(info.colX, info.colsY, info.series, info.getOperations(),
						info.getColumnSeriesValuesGen(), info.intervalType);
				chart = ChartFactory.createBarChart(description, info.xLabel, info.yLabel, set,
						PlotOrientation.VERTICAL, true, true, true);
				((BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer())
				.setBarPainter(new StandardBarPainter());
				((BarRenderer) ((CategoryPlot) chart.getPlot()).getRenderer()).setShadowVisible(false);
				((ChartRepository_1_0) this.charts).addChart(chart, description);
				break;
			case BAR_3D:
				set = this.getCategoryDataset(info.colX, info.colsY, info.series, info.getOperations(),
						info.getColumnSeriesValuesGen(), info.intervalType);
				chart = ChartFactory.createBarChart3D(description, info.xLabel, info.yLabel, set,
						PlotOrientation.VERTICAL, true, true, true);
				if (chart.getPlot() instanceof CategoryPlot) {
					final CategoryItemRenderer cRenderer = ((CategoryPlot) chart.getPlot()).getRenderer();
					final ItemLabelPosition position1 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
							TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 4);
					cRenderer.setBasePositiveItemLabelPosition(position1);
					final ItemLabelPosition position2 = new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,
							TextAnchor.CENTER_LEFT, TextAnchor.CENTER_LEFT, -Math.PI / 4);
					cRenderer.setBaseNegativeItemLabelPosition(position2);
				}
				((ChartRepository_1_0) this.charts).addChart(chart, description);
				break;

			case STACKED_3D:
				set = this.getCategoryDataset(info.colX, info.colsY, info.series, info.getOperations(),
						info.getColumnSeriesValuesGen(), info.intervalType);
				chart = ChartFactory.createStackedBarChart3D(description, info.xLabel, info.yLabel, set,
						PlotOrientation.VERTICAL, true, true, true);
				if (chart.getPlot() instanceof CategoryPlot) {
					final CategoryItemRenderer cRenderer = ((CategoryPlot) chart.getPlot()).getRenderer();
					cRenderer.setSeriesPaint(0, new Color(254, 125, 252));
					cRenderer.setSeriesPaint(1, new Color(16, 200, 233));
					cRenderer.setSeriesPaint(2, new Color(255, 255, 85));
					cRenderer.setSeriesPaint(3, new Color(59, 237, 50));
				}
				((ChartRepository_1_0) this.charts).addChart(chart, description);
				break;
			case LINE:
				set = this.getCategoryDataset(info.colX, info.colsY, info.series, info.getOperations(),
						info.getColumnSeriesValuesGen(), info.intervalType);
				chart = ChartFactory.createLineChart(description, info.xLabel, info.yLabel, set,
						PlotOrientation.VERTICAL, true, true, true);
				if (((CategoryPlot) chart.getPlot()).getRenderer() instanceof LineAndShapeRenderer) {
					((LineAndShapeRenderer) ((CategoryPlot) chart.getPlot()).getRenderer()).setShapesVisible(false);
				}

				((ChartRepository_1_0) this.charts).addChart(chart, description);
				break;
			default:
				ChartUtilities_1_0.logger.warn("ERROR: CHART TYPE IS INVALUD IN TABLE: CONFIGURECHART");
				break;
			}
		}
		final JFreeChart chart = ((ChartRepository_1_0) this.charts).getChart(description);

		try {
			chart.setBackgroundPaint(this.t.getParentForm().getBackground());
		} catch (final Exception e) {
			ChartUtilities_1_0.logger.trace(null, e);
			chart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.white, 1000.0F, 0.0F, Color.white));
		}

		chart.getPlot().setBackgroundPaint(DataComponent.VERY_LIGHT_GRAY);
		if (chart.getPlot() instanceof CategoryPlot) {
			((CategoryPlot) chart.getPlot()).getRangeAxis().setLabelInsets(new RectangleInsets(15, 2, 15, 15));
			((CategoryPlot) chart.getPlot()).setRangeGridlinesVisible(true);
		} else if (chart.getPlot() instanceof XYPlot) {
			((XYPlot) chart.getPlot()).getRangeAxis().setLabelInsets(new RectangleInsets(15, 2, 15, 15));
		}

		return chart;
	}

	@Override
	public void removeAllCharts() {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			return;
		}
		if (this.chartInfo == null) {
			return;
		}

		((ChartInfoRepository) this.chartInfo).removeAllCharts();
		((ChartRepository_1_0) this.charts).removeAllCharts();
	}

	@Override
	public void removeChart(final String descr) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			return;
		}
		if (this.chartInfo == null) {
			return;
		}

		((ChartInfoRepository) this.chartInfo).removeChart(descr);
		((ChartRepository_1_0) this.charts).removeChart(descr);
	}

	@Override
	public void configureChart(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int type) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, type);
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);

	}

	public void configureChart(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int type, final int[] operations) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, type, operations);
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);
	}

	public void configureChart(final String xLabel, final String yLabel, final String xColumn, final String yColumns[], final String series[],
			final String descr, final int type, final int operations[],
			final String columnSeriesValuesGen[]) {
		this.configureChart(xLabel, yLabel, xColumn, yColumns, series, descr, type, operations, columnSeriesValuesGen,
				ChartInfo.NONE);
	}

	public void configureChart(final String xLabel, final String yLabel, final String xColumn, final String yColumns[], final String series[],
			final String descr, final int type, final int operations[],
			final String columnSeriesValuesGen[], final int intervalType) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, type, operations,
				columnSeriesValuesGen);
		info.intervalType = intervalType;
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);
	}

	@Override
	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int interval) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, 0, interval);
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);

	}

	@Override
	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int interval, final boolean fillZeros) {
		this.configureChartXDate(xLabel, yLabel, xColumn, yColumns, series, descr, interval, fillZeros, null);
	}

	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String[] yColumns, final String[] series,
			final String descr, final int interval, final boolean fillZeros,
			final int[] operations) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, 0, interval, fillZeros, operations);
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);
	}

	public void configureChartXDate(final String xLabel, final String yLabel, final String xColumn, final String yColumns[], final String series[],
			final String descr, final int interval, final boolean fillZeros,
			final int operations[], final String columnSeriesValuesGen[]) {
		if (!Table.isChartEnabled()) {
			return;
		}
		if (this.charts == null) {
			this.charts = new ChartRepository_1_0();
		}

		final ChartInfo info = new ChartInfo(xLabel, yLabel, xColumn, yColumns, series, 0, interval, fillZeros, operations,
				columnSeriesValuesGen);
		if (this.chartInfo == null) {
			this.chartInfo = new ChartInfoRepository();
		}
		((ChartInfoRepository) this.chartInfo).addChartInfo(info, descr);
	}

	/**
	 * Show a modal dialog to configure custom charts with all data in a table.<br>
	 * It is possible to select the X axis, Y axis and chart type (PIE, LINE, BAR)
	 */
	@Override
	public void showDefaultChartDialog() {
		if (!Table.isChartEnabled()) {
			return;
		}

		final Locale oldLocale = Locale.getDefault();
		Locale.setDefault(ApplicationManager.getLocale());
		if (this.defaultDialog == null) {
			this.createDefaultDialog();
		} else {
			this.defaultDialog.updateColumnClasses();
		}
		this.defaultDialog.setResourceBundle(this.t.getResourceBundle());
		this.defaultDialog.updateChart();
		this.defaultDialog.pack();
		this.defaultDialog.setVisible(true);
		Locale.setDefault(oldLocale);
	}

	/**
	 * Show a modal dialog to configure custom chart.
	 * @param configuration Default chart configuration to show
	 */
	@Override
	public void showDefaultChartDialog(final String configuration) {
		if (!Table.CHART_ENABLED) {
			return;
		}
		if (this.defaultDialog == null) {
			this.createDefaultDialog();
		} else {
			this.defaultDialog.updateColumnClasses();
		}
		this.defaultDialog.setResourceBundle(this.t.getResourceBundle());
		this.defaultDialog.updateChart();
		this.defaultDialog.pack();
		this.loadChartConfiguration(configuration);
		this.defaultDialog.setVisible(true);
	}

	protected boolean loadButtonVisible = true;

	protected boolean saveButtonVisible = true;

	private DefaultChartDialog defaultDialog = null;

	private static class DefaultChartDialog extends EJDialog implements Internationalization {

		private static String SELECTION_OPERATION_TOOLTIP = "chartutilities.selection_operation";

		private static String titleKey = "chartutilities.custom_charts";

		private static String barChartKey = "chartutilities.bars";

		private static String pieChartKey = "chartutilities.pie";

		private static String barChar3DKey = "chartutilities.3d_bars";

		private static String stockedBar3DKey = "chartutilities.stacked_3d_bars";

		private static String lineChartKey = "chartutilities.line";

		private static String pieChart3DKey = "chartutilities.3d_pie";

		private static String timeSerialChartKey = "chartutilities.time_series";

		private static String daysKey = "days";

		private static String monthsKey = "months";

		private static String quarterKey = "chartutilities.quarters";

		private static String yearKey = "chartutilities.years";

		private static String showValuesKey = "chartutilities.show_values";

		private static String valueFontKey = "chartutilities.value_font";

		private static String selectFontKey = "chartutilities.select";

		private static String showPercentKey = "chartutilities.show_percent";

		private static String breakdownKey = "chartutilities.breakdown";

		private static String noColumnSelectedKey = "chartutilities.no_column_has_been_selected_for_the_y_axis";

		private static String axisXKey = "chartutilities.x_axis";

		private static String axisYKey = "chartutilities.y_axis";

		private static String charTypeKey = "chartutilities.chart_type";

		private static String periodTypeKey = "chartutilities.perid_type";

		private static String chartTitleKey = "chartutilities.chart_title";

		private static String averageOpKey = "average";

		private static String sumOpKey = "sum";

		private static String saveKey = "chartutilities.save";

		private static String loadKey = "chartutilities.load";

		private static String maxOpKey = "maximum";

		private static String minOpKey = "minimum";

		private static String breakdownOpKey = "breakdown";

		private static String withoutBreakDownOpKey = "withoutbreakdown";

		protected ResourceBundle resource = null;

		private final JComboBox comboX = new JComboBox();

		private final JList yList = new JList();

		private final JPanel configurationPanel = new JPanel(new GridBagLayout());

		private ChartButton loadButton = null;

		private final RolloverButton saveButton = new RolloverButton();

		private final ButtonGroup group = new ButtonGroup();

		private final JRadioButton barChart = new JRadioButton("chartutilities.bars");

		private final JRadioButton pieChart = new JRadioButton("GraficoCircular");

		private final JRadioButton bar3DChart = new JRadioButton("chartutilities.3d_bars");

		private final JRadioButton stockedBar3DChart = new JRadioButton("chartutilities.stacked_3d_bars");

		private final JRadioButton lineChart = new JRadioButton("chartutilities.line");

		private final JRadioButton pie3DChart = new JRadioButton("GraficoCircular3D");

		private final JRadioButton timeSeriesChart = new JRadioButton("chartutilities.time_series");

		private final JRadioButton dayPeriod = new JRadioButton("days");

		private final JRadioButton monthPeriod = new JRadioButton("months");

		private final JRadioButton quarterPeriod = new JRadioButton("chartutilities.quarters");

		private final JRadioButton yearPeriod = new JRadioButton("chartutilities.years");

		private final ButtonGroup groupPeriodTypeButtons = new ButtonGroup();

		private final JPopupMenu operationTypePopup = new JPopupMenu();

		private final ButtonGroup groupOperationTypeButtons = new ButtonGroup();

		private final JRadioButtonMenuItem sumOperationMenu = new JRadioButtonMenuItem(DefaultChartDialog.sumOpKey);

		private final JRadioButtonMenuItem averageOperationMenu = new JRadioButtonMenuItem(
				DefaultChartDialog.averageOpKey);

		private final JRadioButtonMenuItem maxOperationMenu = new JRadioButtonMenuItem(DefaultChartDialog.maxOpKey);

		private final JRadioButtonMenuItem minimumOperationMenu = new JRadioButtonMenuItem(DefaultChartDialog.minOpKey);

		private final JMenu breakdownMenu = new JMenu(DefaultChartDialog.breakdownKey);

		private final JRadioButtonMenuItem withoutBreakdownMenu = new JRadioButtonMenuItem(
				DefaultChartDialog.withoutBreakDownOpKey);

		private SelectableItem currentItem = null;

		private final JLabel labelNoChart = new JLabel("chartutilities.no_column_has_been_selected_for_the_y_axis",
				JLabel.CENTER);

		private Table t = null;

		private JScrollPane scroll = new JScrollPane();

		private final JTextField chartTitle = new JTextField();

		private final JPanel chartTitlePanel = new JPanel(new BorderLayout());

		private final List titleList = new ArrayList();

		private ChartUtilities_1_0 chartUtilities = null;

		private ChartPanel panelChart = null;

		protected CategoryItemLabelGenerator itemValuePercentLabelGenerator = new StandardCategoryItemLabelGenerator(
				"{2} ({3})", NumberFormat.getInstance());

		protected CategoryItemLabelGenerator itemPercentLabelGenerator = new StandardCategoryItemLabelGenerator("{3}",
				NumberFormat.getInstance());

		protected CategoryItemLabelGenerator itemValueLabelGenerator = new StandardCategoryItemLabelGenerator("{2}",
				NumberFormat.getInstance());

		protected StandardPieSectionLabelGenerator pieLabelGenerator = new StandardPieSectionLabelGenerator();

		protected StandardPieSectionLabelGenerator pieValueLabelGenerator = new StandardPieSectionLabelGenerator(
				"{0} = {1}");

		protected StandardPieSectionLabelGenerator piePercentLabelGenerator = new StandardPieSectionLabelGenerator(
				"{0} ({2})");

		protected StandardPieSectionLabelGenerator pieValuePercentLabelGenerator = new StandardPieSectionLabelGenerator(
				"{0} = {1} ({2})");

		private final JCheckBox paintLabelValues = new JCheckBox(DefaultChartDialog.showValuesKey);

		protected final JCheckBox paintPercentValues = new JCheckBox(DefaultChartDialog.showPercentKey);

		protected JLabel fontLabel;

		/** The font used to draw the title. */
		protected Font valueFont;

		/** A field for displaying a description of the title font. */
		protected JTextField fontfield;

		/** The button to use to select a new title font. */
		protected JButton selectFontButton;

		private JPanel chartTypePanel = null;

		private JPanel periodTypePanel = null;

		private JPanel pX = null;

		private JPanel pY = null;

		private final SerieSelectionListener serieSelectionListener = new SerieSelectionListener();

		protected String configurationName;

		private class SerieSelectionListener extends MouseAdapter {

			@Override
			public void mouseReleased(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					return;
				}

				DefaultChartDialog.this.yList.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					final int index = DefaultChartDialog.this.yList.locationToIndex(e.getPoint());
					final SelectableItem it = (SelectableItem) DefaultChartDialog.this.yList.getModel().getElementAt(index);
					final boolean willBeSelected = !it.isSelected();
					// Look for other selected items
					if (willBeSelected && DefaultChartDialog.this.pie3DChart.isSelected()) {
						final ListModel model = DefaultChartDialog.this.yList.getModel();
						for (int i = 0; i < model.getSize(); i++) {
							if (((SelectableItem) model.getElementAt(i)).isSelected()) {
								// No more items can be selected
								return;
							}
						}
					}

					it.setSelected(willBeSelected);
					// If more than one item is selected, items with unique
					// series
					// are not allowed
					int selected = 0;
					final ListModel model = DefaultChartDialog.this.yList.getModel();
					for (int i = 0; i < model.getSize(); i++) {
						if (((SelectableItem) model.getElementAt(i)).isSelected()) {
							selected++;
						}
					}
					if (selected > 1) {
						DefaultChartDialog.this.pie3DChart.setEnabled(false);
					} else {
						DefaultChartDialog.this.pie3DChart.setEnabled(true);
					}
					final Rectangle rect = DefaultChartDialog.this.yList.getCellBounds(index, index);
					DefaultChartDialog.this.yList.repaint(rect);
					DefaultChartDialog.this.updateChart();
				} catch (final Exception ex) {
					ChartUtilities_1_0.logger.trace(null, ex);
				} finally {
					DefaultChartDialog.this.yList.setCursor(Cursor.getDefaultCursor());
				}
			}

		};

		private final JPanel chartPanel = new JPanel(new BorderLayout()) {

			@Override
			public Dimension getPreferredSize() {
				final Dimension d = super.getPreferredSize();
				d.width = Math.max(650, d.width);
				d.height = Math.max(300, d.height);
				return d;
			}
		};

		private final ActionListener changeXListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final Object colXTrans = DefaultChartDialog.this.comboX.getSelectedItem();
				Object colX = null;
				if (colXTrans != null) {
					colX = ((TranslatedItem) colXTrans).getText();
				}
				if (colX == null) {
					return;
				}
				final int modelIndex = DefaultChartDialog.this.t.getJTable().getColumn(colX).getModelIndex();
				final Class columnClass = DefaultChartDialog.this.t.getJTable()
						.getColumnClass(DefaultChartDialog.this.t.getJTable().convertColumnIndexToView(modelIndex));
				if ((columnClass == Date.class) || (columnClass == java.sql.Date.class)
						|| (columnClass == java.sql.Timestamp.class)) {
					// Activate temporal series
					DefaultChartDialog.this.timeSeriesChart.setEnabled(true);
					DefaultChartDialog.this.timeSeriesChart.setSelected(true);
					DefaultChartDialog.this.dayPeriod.setEnabled(true);
					DefaultChartDialog.this.monthPeriod.setEnabled(true);
					DefaultChartDialog.this.quarterPeriod.setEnabled(true);
					DefaultChartDialog.this.yearPeriod.setEnabled(true);

				} else {
					if (DefaultChartDialog.this.timeSeriesChart.isSelected()) {
						DefaultChartDialog.this.barChart.setSelected(true);
					}
					DefaultChartDialog.this.timeSeriesChart.setEnabled(false);
					DefaultChartDialog.this.monthPeriod.setEnabled(false);
					DefaultChartDialog.this.dayPeriod.setEnabled(false);
					DefaultChartDialog.this.quarterPeriod.setEnabled(false);
					DefaultChartDialog.this.yearPeriod.setEnabled(false);
				}
				DefaultChartDialog.this.updateChart();
			}
		};

		private final ActionListener act = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// @modif. now we do not need timeserieschart to allow time
				// grouping...
				DefaultChartDialog.this.updateChart();
			}
		};

		private class ListenerPopup extends MouseAdapter {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if ((e.getClickCount() == 1) && SwingUtilities.isRightMouseButton(e)) {
					// Look for the selected element
					final int x = e.getX();
					final int y = e.getY();
					final int index = DefaultChartDialog.this.yList.locationToIndex(e.getPoint());
					if (index >= 0) {
						DefaultChartDialog.this.currentItem = (SelectableItem) DefaultChartDialog.this.yList.getModel()
								.getElementAt(index);

						if (DefaultChartDialog.this.currentItem == null) {
							return;
						}

						if (ChartUtilities_1_0.ROW_NUMBERS_KEY.equals(DefaultChartDialog.this.currentItem.getText())) {
							return;
						}

						final int op = DefaultChartDialog.this.currentItem.getOperation();
						final String breakdownColumn = DefaultChartDialog.this.currentItem.getColumnSerie();
						if (op == ChartUtilities_1_0.SUM) {
							DefaultChartDialog.this.sumOperationMenu.setSelected(true);
						} else if (op == ChartUtilities_1_0.AVG) {
							DefaultChartDialog.this.averageOperationMenu.setSelected(true);
						} else if (op == ChartUtilities_1_0.MAX) {
							DefaultChartDialog.this.maxOperationMenu.setSelected(true);
						} else if (op == ChartUtilities_1_0.MIN) {
							DefaultChartDialog.this.minimumOperationMenu.setSelected(true);
						}

						DefaultChartDialog.this.withoutBreakdownMenu.setSelected(true);
						int i = 0;
						do {
							if (i >= DefaultChartDialog.this.breakdownMenu.getItemCount()) {
								break;
							}
							if ((DefaultChartDialog.this.breakdownMenu.getItem(i) != null) && (breakdownColumn != null)
									&& breakdownColumn
									.equals(DefaultChartDialog.this.breakdownMenu.getItem(i).getActionCommand())) {
								DefaultChartDialog.this.breakdownMenu.getItem(i).setSelected(true);
								break;
							}
							i++;
						} while (true);

						DefaultChartDialog.this.operationTypePopup
						.setLabel(DefaultChartDialog.this.currentItem.toString());
						DefaultChartDialog.this.operationTypePopup.show(DefaultChartDialog.this.yList, x, y);
					}
				}
			}

		}

		private final ListenerPopup operationTypeListener = new ListenerPopup();

		private final ActionListener listenerTipoOp = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (e.getSource() == DefaultChartDialog.this.sumOperationMenu) {
					DefaultChartDialog.this.currentItem.setOperation(ChartUtilities_1_0.SUM);
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				} else if (e.getSource() == DefaultChartDialog.this.maxOperationMenu) {
					DefaultChartDialog.this.currentItem.setOperation(ChartUtilities_1_0.MAX);
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				} else if (e.getSource() == DefaultChartDialog.this.minimumOperationMenu) {
					DefaultChartDialog.this.currentItem.setOperation(ChartUtilities_1_0.MIN);
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				} else if (e.getSource() == DefaultChartDialog.this.averageOperationMenu) {
					DefaultChartDialog.this.currentItem.setOperation(ChartUtilities_1_0.AVG);
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				} else {
					// Nothing
				}

			}
		};

		private class TranslatedItem implements Internationalization {

			protected String text = "";

			protected String translatedText = null;

			protected ResourceBundle res = null;

			public TranslatedItem(final String text, final ResourceBundle res) {
				this.text = text;
				this.translatedText = text;
				this.setResourceBundle(res);
			}

			@Override
			public void setResourceBundle(final ResourceBundle res) {
				this.res = res;
				if (res != null) {
					try {
						this.translatedText = res.getString(this.text);
					} catch (final Exception e) {
						ChartUtilities_1_0.logger.error(null, e);
						this.translatedText = this.text;
					}
				}
			}

			@Override
			public void setComponentLocale(final Locale l) {
			}

			@Override
			public List getTextsToTranslate() {
				return null;
			}

			@Override
			public String toString() {
				return this.translatedText;
			}

			public String getText() {
				return this.text;
			}

			@Override
			public int hashCode() {
				return this.text.hashCode();
			}

			@Override
			public boolean equals(final Object o) {
				if (this == o) {
					return true;
				} else if (o instanceof SelectableItem) {
					if (this.text.equals(((SelectableItem) o).getText())) {
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}

		}

		private class SelectableItem extends TranslatedItem implements Internationalization {

			protected boolean selected = false;

			protected int operation = ChartUtilities_1_0.SUM;

			protected String opText = DefaultChartDialog.sumOpKey;

			protected String column = null;

			public SelectableItem(final String text, final ResourceBundle res) {
				super(text, res);
				this.setOperationText();
			}

			public boolean isSelected() {
				return this.selected;
			}

			@Override
			public void setResourceBundle(final ResourceBundle res) {
				super.setResourceBundle(res);
				this.setOperationText();
			}

			public void setSelected(final boolean sel) {
				this.selected = sel;
			}

			public void setOperation(final int op) {
				this.operation = op;
				this.setOperationText();
			}

			protected void setOperationText() {
				if (this.res != null) {
					try {
						if (this.operation == ChartUtilities_1_0.SUM) {
							this.opText = DefaultChartDialog.sumOpKey;
						} else if (this.operation == ChartUtilities_1_0.MAX) {
							this.opText = DefaultChartDialog.maxOpKey;
						} else if (this.operation == ChartUtilities_1_0.MIN) {
							this.opText = DefaultChartDialog.minOpKey;
						} else if (this.operation == ChartUtilities_1_0.AVG) {
							this.opText = DefaultChartDialog.averageOpKey;
						}
						this.opText = this.res.getString(this.opText);

					} catch (final Exception e) {
						ChartUtilities_1_0.logger.debug(null, e);
					}
				}
			}

			public int getOperation() {
				return this.operation;
			}

			public String getColumnSerie() {
				return this.column;
			}

			public void setColumnSerie(final String c) {
				this.column = c;
			}

			@Override
			public String toString() {
				if (!this.isSelected()) {
					return super.translatedText;
				}
				if (this.column == null) {
					return super.translatedText + " - " + this.opText;
				} else {
					return super.translatedText + " - " + this.opText + " ("
							+ ApplicationManager.getTranslation(this.column, super.res) + ")";
				}
			}

			public String getSerieName() {
				if (!this.isSelected()) {
					return super.translatedText;
				} else {
					return super.translatedText + " - " + this.opText;
				}
			}

		};

		private class SelectableItemsListCellRenderer extends JCheckBox implements ListCellRenderer {

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
					this.setForeground(selectedForeground);
					this.setBackground(selectedBackground);
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

		private final JButton closeButton = new JButton("close");

		public DefaultChartDialog(final Frame f, final Table t) {
			super(f, true);
			this.t = t;
			this.chartUtilities = new ChartUtilities_1_0(t);
			this.init();
		}

		public DefaultChartDialog(final Dialog d, final Table t) {
			super(d, true);
			this.t = t;
			this.chartUtilities = new ChartUtilities_1_0(t);
			this.init();
		}

		private void setXColumns(final Vector v) {
			this.comboX.setModel(new DefaultComboBoxModel(v));
			final Object colXTrans = this.comboX.getSelectedItem();
			Object colX = null;
			if (colXTrans != null) {
				colX = ((TranslatedItem) colXTrans).getText();
			}
			if (colX == null) {
				return;
			}
			final int iModelIndex = this.t.getJTable().getColumn(colX).getModelIndex();
			final Class columnClass = this.t.getJTable()
					.getColumnClass(this.t.getJTable().convertColumnIndexToView(iModelIndex));
			if ((columnClass == Date.class) || (columnClass == java.sql.Date.class)
					|| (columnClass == java.sql.Timestamp.class)) {
				// Enable temporal series too
				this.timeSeriesChart.setEnabled(true);
				this.timeSeriesChart.setSelected(true);
				this.monthPeriod.setEnabled(true);
				this.dayPeriod.setEnabled(true);
				this.quarterPeriod.setEnabled(true);
				this.yearPeriod.setEnabled(true);
			} else {
				if (this.timeSeriesChart.isSelected()) {
					this.barChart.setSelected(true);
				}
				this.timeSeriesChart.setEnabled(false);
				this.monthPeriod.setEnabled(false);
				this.dayPeriod.setEnabled(false);
				this.quarterPeriod.setEnabled(false);
				this.yearPeriod.setEnabled(false);
			}

			for (int i = 0; i < this.breakdownMenu.getItemCount(); i++) {
				if (this.breakdownMenu.getItem(i) != null) {
					if (this.breakdownMenu.getItem(i) != this.withoutBreakdownMenu) {
						this.breakdownMenu.getItem(i).removeActionListener(this.breakdownListener);
					}
				}
			}

			this.breakdownMenu.removeAll();
			this.breakdownMenu.add(this.withoutBreakdownMenu);
			this.breakdownMenu.addSeparator();
			if (v != null) {
				final ButtonGroup bg = new ButtonGroup();
				for (int i = 0; i < v.size(); i++) {
					final JRadioButtonMenuItem m = new JRadioButtonMenuItem(
							ApplicationManager.getTranslation(v.get(i).toString(), this.resource));
					m.setActionCommand(((TranslatedItem) v.get(i)).getText());
					this.breakdownMenu.add(m);
					bg.add(m);
					bg.add(this.withoutBreakdownMenu);
					m.addActionListener(this.breakdownListener);
				}
			}
		}

		private void setYColumns(final List v) {
			final DefaultListModel m = new DefaultListModel();
			for (int i = 0; i < v.size(); i++) {
				m.add(i, v.get(i));
			}
			final SelectableItem itemNF = new SelectableItem(ChartUtilities_1_0.ROW_NUMBERS_KEY, this.t.getResourceBundle());
			if (!v.contains(itemNF)) {
				m.add(m.size(), itemNF);
			}
			this.yList.setModel(m);
		}

		public void updateColumnClasses() {
			final List yAxisColumns = new Vector();
			final Vector xAxisColumn = new Vector();
			final TableModel model = this.t.getJTable().getModel();
			// In X axis all data type is accepted but Y axis only admit numeric
			// data
			for (int i = 0; i < model.getColumnCount(); i++) {
				final String columnName = model.getColumnName(i);
				if (columnName.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
					continue;
				}
				if (!this.t.isVisibleColumn(columnName)) {
					continue;
				}
				final Class classObject = model.getColumnClass(i);
				if (classObject.getSuperclass() == Number.class) {
					yAxisColumns.add(model.getColumnName(i));
				}
				xAxisColumn.add(new TranslatedItem(model.getColumnName(i), this.t.getResourceBundle()));
			}
			this.setXColumns(xAxisColumn);

			// Now configure the list
			final List items = new Vector();
			for (int i = 0; i < yAxisColumns.size(); i++) {
				items.add(new SelectableItem(yAxisColumns.get(i).toString(), this.t.getResourceBundle()));
			}
			this.setYColumns(items);
		}

		private void init() {
			this.addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosing(final WindowEvent e) {
					super.windowClosing(e);
					DefaultChartDialog.this.deleteDataFields();
				}
			});
			final List yAxisColumns = new Vector();
			final Vector xAxisColumns = new Vector();
			final TableModel model = this.t.getJTable().getModel();
			// In X axis all data type is accepted but Y axis only admit numeric
			// data
			for (int i = 0; i < model.getColumnCount(); i++) {
				final String columnName = model.getColumnName(i);
				if (columnName.equals(ExtendedTableModel.ROW_NUMBERS_COLUMN)) {
					continue;
				}
				if (!this.t.isVisibleColumn(columnName)) {
					continue;
				}
				final Class classObject = model.getColumnClass(i);
				if (classObject.getSuperclass() == Number.class) {
					yAxisColumns.add(model.getColumnName(i));
				}
				xAxisColumns.add(new TranslatedItem(model.getColumnName(i), this.t.getResourceBundle()));
			}
			this.setXColumns(xAxisColumns);

			// Now configure the list
			final List items = new Vector();
			for (int i = 0; i < yAxisColumns.size(); i++) {
				items.add(new SelectableItem(yAxisColumns.get(i).toString(), this.t.getResourceBundle()));
			}
			this.setYColumns(items);

			this.yList.setCellRenderer(new SelectableItemsListCellRenderer());

			// now the listener
			this.yList.addMouseListener(this.serieSelectionListener);

			this.yList.addMouseListener(this.operationTypeListener);

			// Now create the interface
			final JPanel controlPanel = new JPanel(new GridBagLayout());

			this.loadButton = new ExtendChartButton(this.t);
			this.loadButton.setIcon(ImageManager.getIcon(ImageManager.OPEN_FILE));
			this.loadButton.setMargin(new Insets(2, 2, 2, 2));
			this.loadButton.getButton().setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));

			this.saveButton.setIcon(ImageManager.getIcon(ImageManager.SAVE_FILE));
			this.saveButton.setMargin(new Insets(0, 0, 0, 0));
			this.saveButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					DefaultChartDialog.this.saveButtonAction(e);
				}
			});

			controlPanel.add(this.loadButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));
			controlPanel.add(this.saveButton, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
					GridBagConstraints.NONE, new Insets(0, 2, 2, 2), 0, 0));

			this.configurationPanel.add(controlPanel,
					new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(8, 5, 2, 2), 0, 0));

			this.pX = new JPanel(new BorderLayout());
			this.pX.setBorder(new TitledBorder("chartutilities.x_axis"));
			this.pX.add(this.comboX);
			this.configurationPanel.add(this.pX, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

			this.pY = new JPanel(new BorderLayout());
			this.pY.setBorder(new TitledBorder("chartutilities.y_axis"));
			this.yList.setVisibleRowCount(5);
			this.pY.add(new JScrollPane(this.yList));
			this.configurationPanel.add(this.pY, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

			this.chartTypePanel = new JPanel(new GridLayout(0, 1));
			this.chartTypePanel.setBorder(new TitledBorder("chartutilities.chart_type"));
			this.chartTypePanel.add(this.lineChart);
			this.chartTypePanel.add(this.barChart);
			this.chartTypePanel.add(this.pieChart);
			this.chartTypePanel.add(this.bar3DChart);
			this.chartTypePanel.add(this.stockedBar3DChart);
			this.chartTypePanel.add(this.pie3DChart);
			this.chartTypePanel.add(this.timeSeriesChart);

			this.periodTypePanel = new JPanel(new GridLayout(0, 1));
			this.periodTypePanel.setBorder(new TitledBorder("chartutilities.perid_type"));
			this.periodTypePanel.add(this.dayPeriod);
			this.periodTypePanel.add(this.monthPeriod);
			this.periodTypePanel.add(this.quarterPeriod);
			this.periodTypePanel.add(this.yearPeriod);

			this.operationTypePopup.add(this.sumOperationMenu);
			this.operationTypePopup.add(this.averageOperationMenu);
			this.operationTypePopup.add(this.maxOperationMenu);
			this.operationTypePopup.add(this.minimumOperationMenu);
			this.operationTypePopup.add(this.breakdownMenu);

			this.groupOperationTypeButtons.add(this.sumOperationMenu);
			this.groupOperationTypeButtons.add(this.averageOperationMenu);
			this.groupOperationTypeButtons.add(this.maxOperationMenu);
			this.groupOperationTypeButtons.add(this.minimumOperationMenu);
			this.sumOperationMenu.setSelected(true);

			this.maxOperationMenu.addActionListener(this.listenerTipoOp);
			this.averageOperationMenu.addActionListener(this.listenerTipoOp);
			this.sumOperationMenu.addActionListener(this.listenerTipoOp);
			this.minimumOperationMenu.addActionListener(this.listenerTipoOp);

			this.chartTitlePanel.setBorder(new TitledBorder("chartutilities.chart_title"));
			this.chartTitlePanel.add(this.chartTitle);
			this.chartTitle.getDocument().addDocumentListener(new DocumentListener() {

				private void updateTitle() {
					if ((DefaultChartDialog.this.panelChart != null)
							&& (DefaultChartDialog.this.panelChart.getChart() != null)) {
						final TextTitle title = DefaultChartDialog.this.panelChart.getChart().getTitle();
						Font font = null;
						if (title != null) {
							font = title.getFont();
						}
						DefaultChartDialog.this.titleList.clear();
						final TextTitle tit = new TextTitle(DefaultChartDialog.this.chartTitle.getText());
						if (font != null) {
							tit.setFont(font);
						}
						try {
							tit.setPaint(Color.GREEN);
						} catch (final Exception e) {
							ChartUtilities_1_0.logger.error(null, e);
						}
						DefaultChartDialog.this.panelChart.getChart().setTitle(tit);
						DefaultChartDialog.this.panelChart.repaint();
					}
				}

				@Override
				public void insertUpdate(final DocumentEvent e) {
					this.updateTitle();
				}

				@Override
				public void removeUpdate(final DocumentEvent e) {
					this.updateTitle();
				}

				@Override
				public void changedUpdate(final DocumentEvent e) {
					this.updateTitle();
				}

			});
			this.group.add(this.lineChart);
			this.group.add(this.barChart);
			this.barChart.setSelected(true);
			this.group.add(this.pieChart);
			this.group.add(this.bar3DChart);
			this.group.add(this.pie3DChart);
			this.group.add(this.stockedBar3DChart);
			this.group.add(this.timeSeriesChart);

			this.groupPeriodTypeButtons.add(this.dayPeriod);
			this.groupPeriodTypeButtons.add(this.monthPeriod);
			this.groupPeriodTypeButtons.add(this.quarterPeriod);
			this.groupPeriodTypeButtons.add(this.yearPeriod);

			this.monthPeriod.setSelected(true);

			this.configurationPanel.add(this.chartTypePanel,
					new GridBagConstraints(0, 3, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(2, 2, 2, 2), 0, 0));
			this.configurationPanel.add(this.periodTypePanel,
					new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(2, 15, 2, 2), 0, 0));

			this.configurationPanel.add(this.chartTitlePanel,
					new GridBagConstraints(0, 5, 1, 1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(2, 2, 2, 2), 0, 0));

			this.paintLabelValues.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final JFreeChart chart = DefaultChartDialog.this.panelChart.getChart();
					if (chart == null) {
						return;
					}
					final Plot p = chart.getPlot();
					if (p instanceof CategoryPlot) {
						if (((CategoryPlot) p).getRenderer() instanceof AbstractRenderer) {
							final AbstractRenderer render = (AbstractRenderer) ((CategoryPlot) p).getRenderer();
							if (render instanceof AbstractCategoryItemRenderer) {
								((AbstractCategoryItemRenderer) render)
								.setBaseItemLabelGenerator(DefaultChartDialog.this.getCategoryItemLabelGenerator());
							}
							render.setBaseItemLabelsVisible(DefaultChartDialog.this.paintLabelValues.isSelected()
									|| DefaultChartDialog.this.paintPercentValues.isSelected());
							DefaultChartDialog.this.panelChart.repaint();
						}
					} else if ((p instanceof PiePlot) || (p instanceof MultiplePiePlot)) {
						Plot plot = p;
						if (plot instanceof MultiplePiePlot) {
							plot = ((MultiplePiePlot) plot).getPieChart().getPlot();
						}
						((PiePlot) plot).setLabelGenerator(DefaultChartDialog.this.getPieSectionLabelGenerator());
						DefaultChartDialog.this.panelChart.repaint();
					}

				}
			});

			this.paintPercentValues.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final JFreeChart chart = DefaultChartDialog.this.panelChart.getChart();
					if (chart == null) {
						return;
					}
					final Plot p = chart.getPlot();
					if (p instanceof CategoryPlot) {
						if (((CategoryPlot) p).getRenderer() instanceof AbstractRenderer) {
							final AbstractRenderer render = (AbstractRenderer) ((CategoryPlot) p).getRenderer();
							if (render instanceof AbstractCategoryItemRenderer) {
								((AbstractCategoryItemRenderer) render)
								.setBaseItemLabelGenerator(DefaultChartDialog.this.getCategoryItemLabelGenerator());
							}
							render.setBaseItemLabelsVisible(DefaultChartDialog.this.paintLabelValues.isSelected()
									|| DefaultChartDialog.this.paintPercentValues.isSelected());
							DefaultChartDialog.this.panelChart.repaint();
							// render.setSeriesItemLabelsVisible(0,
							// DefaultChartDialog.this.paintLabelValues.isSelected());
						}
					} else if ((p instanceof PiePlot) || (p instanceof MultiplePiePlot)) {
						Plot plot = p;
						if (plot instanceof MultiplePiePlot) {
							plot = ((MultiplePiePlot) plot).getPieChart().getPlot();
						}
						((PiePlot) plot).setLabelGenerator(DefaultChartDialog.this.getPieSectionLabelGenerator());
						DefaultChartDialog.this.panelChart.repaint();
					}
				}
			});

			final StandardChartTheme theme = (StandardChartTheme) ChartFactory.getChartTheme();
			this.valueFont = theme.getRegularFont();

			this.configurationPanel.add(this.paintLabelValues,
					new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 2, 2, 2), 0, 0));

			this.configurationPanel.add(this.paintPercentValues,
					new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 2, 2, 2), 0, 0));

			this.fontLabel = new JLabel(DefaultChartDialog.valueFontKey);
			this.configurationPanel.add(this.fontLabel,
					new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
							new Insets(5, 2, 2, 2), 0, 0));

			this.fontfield = new FontDisplayField(this.valueFont);
			final JPanel fontPanel = new JPanel(new GridBagLayout());

			fontPanel.add(this.fontfield, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 2), 0, 0));
			this.selectFontButton = new JButton(DefaultChartDialog.selectFontKey);
			fontPanel.add(this.selectFontButton, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.EAST,
					GridBagConstraints.CENTER, new Insets(5, 2, 2, 2), 0, 0));
			this.configurationPanel.add(fontPanel, new GridBagConstraints(0, 9, 1, 1, 1, 0, GridBagConstraints.EAST,
					GridBagConstraints.HORIZONTAL, new Insets(5, 2, 2, 2), 0, 0));
			this.selectFontButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final FontChooserPanel panel = new FontChooserPanel(DefaultChartDialog.this.valueFont);
					final int result = JOptionPane.showConfirmDialog((JComponent) e.getSource(), panel, "Font_Selection",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {
						DefaultChartDialog.this.valueFont = panel.getSelectedFont();
						DefaultChartDialog.this.fontfield.setText(DefaultChartDialog.this.valueFont.getFontName() + " "
								+ DefaultChartDialog.this.valueFont.getSize());
						DefaultChartDialog.this.configureFont();
					}
				}
			});

			this.configurationPanel.add(this.closeButton,
					new GridBagConstraints(0, 10, 1, 1, 1, 1, GridBagConstraints.SOUTH, GridBagConstraints.NONE,
							new Insets(10, 2, 2, 2), 0, 0));

			// this.configurationPanel.add(new JPanel(), new
			// GridBagConstraints(0, 9, 1, 1, 0, 1, GridBagConstraints.EAST,
			// GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

			final JScrollPane scrollConf = new JScrollPane(this.configurationPanel);
			scrollConf.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			final JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setOneTouchExpandable(true);
			splitPane.setDividerSize(6);
			splitPane.setDividerLocation(0.25);

			this.chartPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));

			splitPane.add(scrollConf, JSplitPane.LEFT);
			// left controsl in a scroll pane
			splitPane.add(this.chartPanel, JSplitPane.RIGHT);
			this.getContentPane().add(splitPane);

			this.comboX.addActionListener(this.changeXListener);
			this.barChart.addActionListener(this.act);
			this.pieChart.addActionListener(this.act);
			this.bar3DChart.addActionListener(this.act);
			this.pie3DChart.addActionListener(this.act);
			this.lineChart.addActionListener(this.act);
			this.stockedBar3DChart.addActionListener(this.act);
			this.timeSeriesChart.addActionListener(this.act);

			this.dayPeriod.addActionListener(this.act);
			this.monthPeriod.addActionListener(this.act);
			this.quarterPeriod.addActionListener(this.act);
			this.yearPeriod.addActionListener(this.act);

			this.closeButton.setIcon(ImageManager.getIcon(ImageManager.CANCEL));

			this.closeButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					DefaultChartDialog.this.deleteDataFields();
					DefaultChartDialog.this.setVisible(false);
				}
			});

			this.withoutBreakdownMenu.addActionListener(this.breakdownListener);
			// Now update the chart
			this.updateChart();
		}

		protected CategoryItemLabelGenerator getCategoryItemLabelGenerator() {
			if (this.paintLabelValues.isSelected() && this.paintPercentValues.isSelected()) {
				return this.itemValuePercentLabelGenerator;
			}

			if (this.paintLabelValues.isSelected()) {
				return this.itemValueLabelGenerator;
			}

			if (this.paintPercentValues.isSelected()) {
				return this.itemPercentLabelGenerator;
			}
			return null;
		}

		protected PieSectionLabelGenerator getPieSectionLabelGenerator() {
			if (this.paintLabelValues.isSelected() && this.paintPercentValues.isSelected()) {
				return this.pieValuePercentLabelGenerator;
			}

			if (this.paintLabelValues.isSelected()) {
				return this.pieValueLabelGenerator;
			}

			if (this.paintPercentValues.isSelected()) {
				return this.piePercentLabelGenerator;
			}

			return this.pieLabelGenerator;
		}

		public void deleteDataFields() {
			if (this.chartTitle != null) {
				this.chartTitle.setText("");
			}
		}

		public void setLoadButtonVisible(final boolean visible) {
			this.loadButton.setVisible(visible);
		}

		public void setSaveButtonVisible(final boolean visible) {
			this.saveButton.setVisible(visible);
		}

		protected class ExtendChartButton extends ChartButton {

			private SelectStoredChartDialog storedChartConfigDialog;

			public ExtendChartButton(final Table table) {
				super(table);
			}

			protected void showDefaultChartDialog() {
				// Show a dialog to select the chart configuration
				this.showConfigChartDialog();
			}

			protected void showConfigChartDialog() {
				final Window w = SwingUtilities.getWindowAncestor(this);

				if (w instanceof Frame) {
					this.storedChartConfigDialog = new SelectStoredChartDialog((Frame) w);
				} else {
					this.storedChartConfigDialog = new SelectStoredChartDialog((Dialog) w);
				}
				final String conf = this.storedChartConfigDialog.show(this.getChartConfiguration(),
						DefaultChartDialog.this.t.getResourceBundle());
				if (conf != null) {
					DefaultChartDialog.this.loadChartConfiguration(conf);
				}
			}

			@Override
			protected void createChartConfigMenu() {
				if (this.listener == null) {
					this.listener = new ActionListener() {

						@Override
						public void actionPerformed(final ActionEvent e) {
							if (e.getSource() instanceof AbstractButton) {
								DefaultChartDialog.this
								.loadChartConfiguration(((AbstractButton) e.getSource()).getActionCommand());
							}
							ExtendChartButton.this.chartConfigMenu.setVisible(false);

						}
					};
				}
				super.createChartConfigMenu();
			}

		}

		public boolean loadChartConfiguration(final String conf) {
			final String preferenceKey = this.loadButton.getCustomChartPreferenceKey();
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			if ((preferenceKey != null) && (prefs != null)) {
				final String p = prefs.getPreference(
						((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser(),
						preferenceKey);
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						final int index = token.indexOf(":");
						if (index > 0) {
							final String configName = token.substring(0, index);
							if (configName.equalsIgnoreCase(conf)) {
								final String confData = token.substring(index + 1);
								this.configurationName = conf;
								this.configureChart(confData);
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		protected void configureChart(final String confData) {
			final StringTokenizer token = new StringTokenizer(confData, "|");
			while (token.hasMoreElements()) {
				final String currentToken = token.nextToken();

				if (currentToken.indexOf("~xaxis~") != -1) {
					final int pos = currentToken.indexOf("~xaxis~") + "~xaxis~".length();
					final String xField = currentToken.substring(pos);
					for (int i = 0; i < this.comboX.getItemCount(); i++) {
						final Object itemAt = this.comboX.getItemAt(i);
						if (itemAt instanceof TranslatedItem) {
							if (((TranslatedItem) itemAt).getText().equalsIgnoreCase(xField)) {
								this.comboX.setSelectedIndex(i);
								break;
							}
						} else if (itemAt.equals(xField)) {
							this.comboX.setSelectedIndex(i);
							break;
						}
					}
				}
				if (currentToken.indexOf("~yaxis~") != -1) {
					final int pos = currentToken.indexOf("~yaxis~") + "~yaxis~".length();
					final String yItem = currentToken.substring(pos);
					// yItem must be item name//true or false//operation type
					final StringTokenizer configYElements = new StringTokenizer(yItem, ":");
					while (configYElements.hasMoreTokens()) {
						final String yElement = configYElements.nextToken();
						final StringTokenizer configItemYTokens = new StringTokenizer(yElement, "//");
						if (configItemYTokens.countTokens() == 3) {
							final String itemName = configItemYTokens.nextToken();
							final String selected = configItemYTokens.nextToken();
							final String operation = configItemYTokens.nextToken();
							for (int i = 0; i < this.yList.getModel().getSize(); i++) {
								final SelectableItem current = (SelectableItem) this.yList.getModel().getElementAt(i);
								if (current.getText().equals(itemName)) {
									if (selected.equals("true")) {
										current.setSelected(true);
									}
									current.setOperation(Integer.parseInt(operation));
									break;
								}
							}
						}
					}
				}
				if (currentToken.indexOf("~charttype~") != -1) {
					final int pos = currentToken.indexOf("~charttype~") + "~charttype~".length();
					final String chartType = currentToken.substring(pos);

					final Enumeration chartTypeEnum = this.group.getElements();
					while (chartTypeEnum.hasMoreElements()) {
						final Object element = chartTypeEnum.nextElement();
						if ((element instanceof JRadioButton) && this.compareActionCommandAndPreference(
								((JRadioButton) element).getActionCommand(), chartType)) {
							((JRadioButton) element).setSelected(true);
							break;
						}
					}
				}

				if (currentToken.indexOf("~timeconf~") != -1) {
					final int pos = currentToken.indexOf("~timeconf~") + "~timeconf~".length();
					final String timeType = currentToken.substring(pos);

					final Enumeration timeConfigEnum = this.groupPeriodTypeButtons.getElements();
					while (timeConfigEnum.hasMoreElements()) {
						final Object element = timeConfigEnum.nextElement();
						if ((element instanceof JRadioButton) && this
								.compareActionCommandAndPreference(((JRadioButton) element).getActionCommand(), timeType)) {
							((JRadioButton) element).setSelected(true);
							break;
						}
					}
				}

				if (currentToken.indexOf("~charttitle~") != -1) {
					final int pos = currentToken.indexOf("~charttitle~") + "~charttitle~".length();
					final String chartTitle = currentToken.substring(pos);
					this.chartTitle.setText(chartTitle);
				}
				if (currentToken.indexOf("~paintvalues~") != -1) {
					this.paintLabelValues.setSelected(true);
				}

				if (currentToken.indexOf("~paintpercent~") != -1) {
					this.paintPercentValues.setSelected(true);
				}
			}
			this.updateChart();
		}

		public boolean compareActionCommandAndPreference(final String actionCommnad, final String preference) {
			if (actionCommnad.equals(preference)) {
				return true;
			} else {
				return ApplicationManager.getTranslation(actionCommnad).equals(preference);
			}

		}

		protected void saveButtonAction(final ActionEvent e) {
			try {
				final Object s = MessageDialog.showInputMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
						"chartutilities.m_set_configuration_name",
						this.t.getResourceBundle(), this.configurationName);
				if (s != null) {
					String str = s.toString();
					str = str.replace(':', '_');
					final int o = this.saveChartConfiguration(str);
					if (o != JOptionPane.OK_OPTION) {
						return;
					}
					this.configurationName = str;
					final Window window = SwingUtilities.getWindowAncestor((Component) e.getSource());
					if (window instanceof Frame) {
						final Frame f = (Frame) window;
						MessageDialog.showMessage(f, "chartutilities.m_save_successfully",
								JOptionPane.INFORMATION_MESSAGE, this.t.getResourceBundle());
					} else if (window instanceof Dialog) {
						final Dialog d = (Dialog) window;
						MessageDialog.showMessage(d, "chartutilities.m_save_successfully",
								JOptionPane.INFORMATION_MESSAGE, this.t.getResourceBundle());
					}
				}
			} catch (final Exception ex) {
				ChartUtilities_1_0.logger.trace(null, ex);
				MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
						ex.getMessage());
			}
		}

		protected int saveChartConfiguration(final String conf) {
			final StringBuilder preferenceConfiguration = new StringBuilder();
			boolean found = false;
			final String preferenceKey = this.loadButton.getCustomChartPreferenceKey();
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			if (prefs != null) {
				final String p = prefs.getPreference(
						((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser(),
						preferenceKey);
				if (p != null) {
					final StringTokenizer st = new StringTokenizer(p, ";");
					while (st.hasMoreTokens()) {
						final String token = st.nextToken();
						final int index = token.indexOf(":");
						if (index > 0) {
							final String name = token.substring(0, index);
							if (name.equalsIgnoreCase(conf)) {
								final String newConfig = this.getCurrentChartConfiguration();
								final int o = JOptionPane.showConfirmDialog(ApplicationManager.getApplication().getFrame(),
										ApplicationManager.getTranslation("chartutilities.overwritechart?",
												this.t.getResourceBundle()),
										"", JOptionPane.YES_NO_OPTION);
								found = true;
								if (o != JOptionPane.OK_OPTION) {
									return o;
								}
								preferenceConfiguration.append(name + ":" + newConfig);
							} else {
								preferenceConfiguration.append(token);
							}
							preferenceConfiguration.append(";");
						}
					}
				}
				if (!found) {
					preferenceConfiguration.append(";" + conf + ":" + this.getCurrentChartConfiguration());
				}
				prefs.setPreference(
						((ClientReferenceLocator) ApplicationManager.getApplication().getReferenceLocator()).getUser(),
						preferenceKey, preferenceConfiguration.toString());
				prefs.savePreferences();
				return JOptionPane.OK_OPTION;
			}
			return JOptionPane.NO_OPTION;
		}

		protected void add(final StringBuilder sb, final String s) {
			if (s != null) {
				sb.append(s);
				sb.append("|");
			}
		}

		protected String getCurrentChartConfiguration() {
			// Put all keys separated by ~ before each preference

			// X-Axis
			// Y-Axis
			// Chart type
			// Period type
			// Chart title
			// Show values

			final StringBuilder sb = new StringBuilder();
			// X-axis
			final String p = this.comboX.getSelectedItem() != null
					? ((TranslatedItem) this.comboX.getSelectedItem()).getText() : null;
			this.add(sb, "~xaxis~" + p);

			// Y - Axis
			final StringBuilder yColumns = new StringBuilder();
			if (this.yList.getModel().getSize() > 0) {
				yColumns.append("~yaxis~");
				for (int i = 0; i < this.yList.getModel().getSize(); i++) {
					final SelectableItem current = (SelectableItem) this.yList.getModel().getElementAt(i);
					yColumns.append(current.getText());
					yColumns.append("//" + (current.isSelected() ? "true//" : "false//") + current.getOperation());
					if (i < (this.yList.getModel().getSize() - 1)) {
						yColumns.append(":");
					}
				}
				this.add(sb, yColumns.toString());
			}

			// Chart type
			final Enumeration chartTypeEnum = this.group.getElements();
			while (chartTypeEnum.hasMoreElements()) {
				final Object element = chartTypeEnum.nextElement();
				if ((element instanceof JRadioButton) && ((JRadioButton) element).isSelected()) {
					this.add(sb, "~charttype~" + ((JRadioButton) element).getActionCommand());
					break;
				}
			}
			// Period type
			final Enumeration periodTypeEnum = this.groupPeriodTypeButtons.getElements();
			while (periodTypeEnum.hasMoreElements()) {
				final Object element = periodTypeEnum.nextElement();
				if ((element instanceof JRadioButton) && ((JRadioButton) element).isSelected()) {
					this.add(sb, "~timeconf~" + ((JRadioButton) element).getActionCommand());
					break;
				}
			}

			final String title = this.chartTitle.getText();
			this.add(sb, "~charttitle~" + title);

			if (this.paintLabelValues.isSelected()) {
				this.add(sb, "~paintvalues~true");
			}

			if (this.paintPercentValues.isSelected()) {
				this.add(sb, "~paintpercent~true");
			}

			return sb.toString();
		}

		private List getSelectedYAxisColumns() {
			final List v = new Vector();
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					v.add(item.getText());
				}
			}
			return v;
		}

		private int[] getSelectedYAxisColumnOperations() {
			int select = 0;
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					select++;
				}
			}
			final int[] operations = new int[select];
			int index = 0;
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					operations[index] = item.getOperation();
					index++;
				}
			}
			return operations;
		}

		private String[] getColumnSeriesValuesGen() {
			int selec = 0;
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					selec++;
				}
			}

			final String columns[] = new String[selec];
			int iIndex = 0;
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					columns[iIndex] = item.getColumnSerie();
					iIndex++;
				}
			}
			return columns;
		}

		private List getSelectedYAxisColumnTranslatedTexts() {
			final List v = new Vector();
			for (int i = 0; i < this.yList.getModel().getSize(); i++) {
				final SelectableItem item = (SelectableItem) this.yList.getModel().getElementAt(i);
				if (item.isSelected()) {
					v.add(item.toString());
				}
			}
			return v;
		}

		private void updateChart() {

			final long t = System.currentTimeMillis();
			try {
				DefaultChartDialog.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				DefaultChartDialog.this.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Update the chart with the current configuration

				final Object colXTrans = this.comboX.getSelectedItem();
				Object colX = null;
				Object colXTrad = null;
				if (colXTrans != null) {
					colX = ((TranslatedItem) colXTrans).getText();
					colXTrad = ((TranslatedItem) colXTrans).toString();
				}
				final List csY = this.getSelectedYAxisColumns();
				final int[] operations = this.getSelectedYAxisColumnOperations();
				final String columnSeriesValuesGen[] = this.getColumnSeriesValuesGen();

				final List csYTrad = this.getSelectedYAxisColumnTranslatedTexts();
				if (csY.isEmpty()) {
					this.chartPanel.removeAll();
					this.chartPanel.add(this.labelNoChart);

					this.chartPanel.invalidate();
					this.chartPanel.validate();

					this.chartPanel.repaint();
					return;
				}

				if (colX == null) {
					return;
				}

				// type

				final Pair<Integer, Boolean> pair = this.setChartType();
				final int type = pair.getFirst();
				final boolean temporalSeries = pair.getSecond();
				final int intervalType = this.setIntervalType();

				final StringBuilder descrColsY = new StringBuilder();
				final String[] colsY = new String[csYTrad.size()];
				final String[] colsYTrad = new String[csYTrad.size()];
				for (int i = 0; i < csYTrad.size(); i++) {
					descrColsY.append(csYTrad.get(i));
					if (i < (csYTrad.size() - 1)) {
						descrColsY.append(" - ");
					}
					colsY[i] = (String) csY.get(i);
					colsYTrad[i] = (String) csYTrad.get(i);
				}

				// Create the chart with a string to identifier it:
				final String descr = "" + colXTrad + " - " + descrColsY + " " + type;

				if (!temporalSeries) {
					this.chartUtilities.configureChart(colXTrad.toString(), descrColsY.toString(), colX.toString(),
							colsY, colsYTrad, descr, type, operations,
							columnSeriesValuesGen, intervalType);
				} else {
					this.chartUtilities.configureChartXDate(colXTrad.toString(), descrColsY.toString(), colX.toString(),
							colsY, colsYTrad, descr, type, true, operations,
							columnSeriesValuesGen);
				}

				final JFreeChart chart = this.chartUtilities.getChart(descr);

				final Plot p = chart.getPlot();

				try {
					ChartUtilities_1_0.axisTextPaint = this.bar3DChart.getForeground();
				} catch (final Exception e) {
					ChartUtilities_1_0.logger.trace(null, e);
				}
				try {
					if (chart.getTitle() != null) {
						chart.getTitle().setPaint(ChartUtilities_1_0.axisTextPaint);
					}
				} catch (final Exception e) {
					ChartUtilities_1_0.logger.trace(null, e);
				}

				if (chart.getPlot() instanceof CategoryPlot) {
					((CategoryPlot) chart.getPlot()).getRangeAxis().setLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((CategoryPlot) chart.getPlot()).getRangeAxis().setAxisLinePaint(ChartUtilities_1_0.axisTextPaint);
					((CategoryPlot) chart.getPlot()).getRangeAxis().setTickLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((CategoryPlot) chart.getPlot()).getDomainAxis().setLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((CategoryPlot) chart.getPlot()).getDomainAxis().setAxisLinePaint(ChartUtilities_1_0.axisTextPaint);
					((CategoryPlot) chart.getPlot()).getDomainAxis()
					.setTickLabelPaint(ChartUtilities_1_0.axisTextPaint);
				} else if (chart.getPlot() instanceof XYPlot) {
					((XYPlot) chart.getPlot()).getRangeAxis().setLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((XYPlot) chart.getPlot()).getRangeAxis().setAxisLinePaint(ChartUtilities_1_0.axisTextPaint);
					((XYPlot) chart.getPlot()).getRangeAxis().setTickLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((XYPlot) chart.getPlot()).getDomainAxis().setLabelPaint(ChartUtilities_1_0.axisTextPaint);
					((XYPlot) chart.getPlot()).getDomainAxis().setAxisLinePaint(ChartUtilities_1_0.axisTextPaint);
					((XYPlot) chart.getPlot()).getDomainAxis().setTickLabelPaint(ChartUtilities_1_0.axisTextPaint);
				}

				if (p instanceof CategoryPlot) {
					final CategoryAxis domainAxis = ((CategoryPlot) p).getDomainAxis();
					domainAxis.setCategoryLabelPositions(
							CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4.0));

					this.paintLabelValues.setEnabled(true);
					this.paintPercentValues.setEnabled(true);

					// ((BarRenderer) ((CategoryPlot)
					// p).getRenderer()).setPositiveItemLabelPositionFallback(new
					// ItemLabelPosition());

					if (((CategoryPlot) p).getRenderer() instanceof AbstractRenderer) {
						final AbstractRenderer render = (AbstractRenderer) ((CategoryPlot) p).getRenderer();
						if (render instanceof AbstractCategoryItemRenderer) {
							((AbstractCategoryItemRenderer) render)
							.setBaseItemLabelGenerator(this.getCategoryItemLabelGenerator());
						}
						((AbstractRenderer) ((CategoryPlot) p).getRenderer()).setBaseItemLabelsVisible(
								this.paintLabelValues.isSelected() || this.paintPercentValues.isSelected());
					}
				} else if ((p instanceof PiePlot) || (p instanceof MultiplePiePlot)) {
					this.paintLabelValues.setEnabled(true);
					this.paintPercentValues.setEnabled(true);

					Plot plot = p;
					if (plot instanceof MultiplePiePlot) {
						plot = ((MultiplePiePlot) plot).getPieChart().getPlot();
					}
					((PiePlot) plot).setLabelGenerator(this.getPieSectionLabelGenerator());

				} else {
					this.paintLabelValues.setEnabled(false);
					this.paintPercentValues.setEnabled(false);
				}

				this.selectFontButton.setEnabled(true);
				this.chartUtilities.removeAllCharts();

				this.chartPanel.removeAll();
				if (this.panelChart == null) {
					this.panelChart = new ChartPanel(chart);
					this.panelChart.setMouseZoomable(true);
				} else {
					this.panelChart.setChart(chart);
				}
				this.scroll.removeAll();
				this.scroll = new JScrollPane(this.panelChart);
				this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

				// TITLE
				if ((this.chartTitle.getText() != null) && (this.chartTitle.getText().length() > 0)) {
					final TextTitle title = this.panelChart.getChart().getTitle();
					Font titleFont = null;
					if (title != null) {
						titleFont = title.getFont();
					}
					this.titleList.clear();
					final TextTitle tit = new TextTitle(this.chartTitle.getText());
					if (titleFont != null) {
						tit.setFont(titleFont);
					}
					this.panelChart.getChart().setTitle(tit);
					this.panelChart.repaint();
				}
				this.configureFont();
				this.chartPanel.add(this.scroll);

				this.panelChart.invalidate();
				this.panelChart.validate();

				this.scroll.invalidate();
				this.scroll.validate();
				this.chartPanel.validate();
				this.panelChart.repaint();
				this.getContentPane().doLayout();
			} catch (final OutOfMemoryError e) {
				ChartUtilities_1_0.logger.error(null, e);
				final Window owner = DefaultChartDialog.this.getOwner();
				DefaultChartDialog.this.dispose();
				MessageDialog.showErrorMessage(owner, "Memory not enough. Charts can not be showed");
			} catch (final Exception e) {
				ChartUtilities_1_0.logger.error(null, e);
			} finally {
				DefaultChartDialog.this.setCursor(Cursor.getDefaultCursor());
				DefaultChartDialog.this.getContentPane().setCursor(Cursor.getDefaultCursor());
			}

			ChartUtilities_1_0.logger.debug("UpdateChart, time: {}", System.currentTimeMillis() - t);

		}

		/**
		 * Method to reduce the complexity of {@link #updateChart()}
		 * @return
		 */
		protected Pair<Integer, Boolean> setChartType() {

			int type = ChartUtilities_1_0.BAR;
			boolean temporalSeries = false;

			final Pair<Integer, Boolean> pair = new Pair<Integer, Boolean>();

			if (this.barChart.isSelected()) {
				type = ChartUtilities_1_0.BAR;
			} else if (this.pieChart.isSelected()) {
				type = ChartUtilities_1_0.PIE;
			} else if (this.pie3DChart.isSelected()) {
				type = ChartUtilities_1_0.PIE_3D;
			} else if (this.bar3DChart.isSelected()) {
				type = ChartUtilities_1_0.BAR_3D;
			} else if (this.lineChart.isSelected()) {
				type = ChartUtilities_1_0.LINE;
			} else if (this.stockedBar3DChart.isSelected()) {
				type = ChartUtilities_1_0.STACKED_3D;
			} else if (this.timeSeriesChart.isSelected()) {
				if (this.dayPeriod.isSelected()) {
					type = ChartUtilities_1_0.DAY;
				} else if (this.monthPeriod.isSelected()) {
					type = ChartUtilities_1_0.MONTH;
				} else if (this.quarterPeriod.isSelected()) {
					type = ChartUtilities_1_0.QUARTER;
				} else if (this.yearPeriod.isSelected()) {
					type = ChartUtilities_1_0.YEAR;
				} else {
					type = ChartUtilities_1_0.MONTH;
				}
				temporalSeries = true;
			}

			pair.setFirst(type);
			pair.setSecond(temporalSeries);

			return pair;
		}

		protected int setIntervalType() {
			if (this.dayPeriod.isSelected() && this.dayPeriod.isEnabled()) {
				return ChartUtilities_1_0.DAY;
			} else if (this.monthPeriod.isSelected() && this.monthPeriod.isEnabled()) {
				return ChartUtilities_1_0.MONTH;
			} else if (this.quarterPeriod.isSelected() && this.quarterPeriod.isEnabled()) {
				return ChartUtilities_1_0.QUARTER;
			} else if (this.yearPeriod.isSelected() && this.yearPeriod.isEnabled()) {
				return ChartUtilities_1_0.YEAR;
			} else {
				return ChartInfo.NONE;
			}
		}

		protected void configureFont() {
			final ChartPanel cPanel = this.panelChart;
			final JFreeChart chart = cPanel.getChart();
			if (chart != null) {
				Plot plot = chart.getPlot();
				AbstractRenderer renderer = null;
				if (plot instanceof CategoryPlot) {
					if (((CategoryPlot) plot).getRenderer() instanceof AbstractRenderer) {
						renderer = (AbstractRenderer) ((CategoryPlot) plot).getRenderer();

					}
				} else if (plot instanceof XYPlot) {
					if (((XYPlot) plot).getRenderer() instanceof AbstractRenderer) {
						renderer = (AbstractRenderer) ((XYPlot) plot).getRenderer();
					}
				} else if ((plot instanceof PiePlot) || (plot instanceof MultiplePiePlot)) {
					if (plot instanceof MultiplePiePlot) {
						plot = ((MultiplePiePlot) plot).getPieChart().getPlot();
					}
					((PiePlot) plot).setLabelFont(this.valueFont);
				}
				if (renderer != null) {
					renderer.setBaseItemLabelFont(this.valueFont);
				}
				cPanel.repaint();
			}
		}

		@Override
		public List getTextsToTranslate() {
			return null;
		}

		@Override
		public void setComponentLocale(final Locale l) {
		}

		@Override
		public void setResourceBundle(final ResourceBundle res) {
			this.resource = res;

			if (res != null) {
				this.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.titleKey, res));
				this.closeButton.setText(ApplicationManager.getTranslation("close", res));
				this.barChart.setText(ApplicationManager.getTranslation(DefaultChartDialog.barChartKey, res));
				this.barChart.setActionCommand(DefaultChartDialog.barChartKey);
				this.bar3DChart.setText(ApplicationManager.getTranslation(DefaultChartDialog.barChar3DKey, res));
				this.bar3DChart.setActionCommand(DefaultChartDialog.barChar3DKey);
				this.stockedBar3DChart
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.stockedBar3DKey, res));
				this.stockedBar3DChart.setActionCommand(DefaultChartDialog.stockedBar3DKey);
				this.lineChart.setText(ApplicationManager.getTranslation(DefaultChartDialog.lineChartKey, res));
				this.lineChart.setActionCommand(DefaultChartDialog.lineChartKey);
				this.pieChart.setText(ApplicationManager.getTranslation(DefaultChartDialog.pieChartKey, res));
				this.pieChart.setActionCommand(DefaultChartDialog.pieChartKey);
				this.pie3DChart.setText(ApplicationManager.getTranslation(DefaultChartDialog.pieChart3DKey, res));
				this.pie3DChart.setActionCommand(DefaultChartDialog.pieChart3DKey);
				this.timeSeriesChart
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.timeSerialChartKey, res));
				this.timeSeriesChart.setActionCommand(DefaultChartDialog.timeSerialChartKey);

				this.dayPeriod.setText(ApplicationManager.getTranslation(DefaultChartDialog.daysKey, res));
				this.dayPeriod.setActionCommand(DefaultChartDialog.daysKey);
				this.monthPeriod.setText(ApplicationManager.getTranslation(DefaultChartDialog.monthsKey, res));
				this.monthPeriod.setActionCommand(DefaultChartDialog.monthsKey);
				this.quarterPeriod.setText(ApplicationManager.getTranslation(DefaultChartDialog.quarterKey, res));
				this.quarterPeriod.setActionCommand(DefaultChartDialog.quarterKey);
				this.yearPeriod.setText(ApplicationManager.getTranslation(DefaultChartDialog.yearKey, res));
				this.yearPeriod.setActionCommand(DefaultChartDialog.yearKey);

				this.maxOperationMenu.setText(ApplicationManager.getTranslation(DefaultChartDialog.maxOpKey, res));
				this.minimumOperationMenu.setText(ApplicationManager.getTranslation(DefaultChartDialog.minOpKey, res));
				this.averageOperationMenu
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.averageOpKey, res));
				this.sumOperationMenu.setText(ApplicationManager.getTranslation(DefaultChartDialog.sumOpKey, res));
				this.saveButton.setToolTipText(ApplicationManager.getTranslation(DefaultChartDialog.saveKey, res));
				this.loadButton.setToolTipText(ApplicationManager.getTranslation(DefaultChartDialog.loadKey, res));

				this.paintLabelValues.setText(ApplicationManager.getTranslation(DefaultChartDialog.showValuesKey, res));
				this.paintPercentValues
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.showPercentKey, res));

				this.fontLabel.setText(ApplicationManager.getTranslation(DefaultChartDialog.valueFontKey, res));
				this.selectFontButton.setText(ApplicationManager.getTranslation(DefaultChartDialog.selectFontKey, res));

				if ((this.chartTitlePanel.getBorder() != null)
						&& (this.chartTitlePanel.getBorder() instanceof TitledBorder)) {
					((TitledBorder) this.chartTitlePanel.getBorder())
					.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.chartTitleKey, res));
				}

				if ((this.chartTypePanel.getBorder() != null)
						&& (this.chartTypePanel.getBorder() instanceof TitledBorder)) {
					((TitledBorder) this.chartTypePanel.getBorder())
					.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.charTypeKey, res));
				}
				if ((this.periodTypePanel.getBorder() != null)
						&& (this.periodTypePanel.getBorder() instanceof TitledBorder)) {
					((TitledBorder) this.periodTypePanel.getBorder())
					.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.periodTypeKey, res));
				}

				if ((this.pX.getBorder() != null) && (this.pX.getBorder() instanceof TitledBorder)) {
					((TitledBorder) this.pX.getBorder())
					.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.axisXKey, res));
				}

				if ((this.pY.getBorder() != null) && (this.pY.getBorder() instanceof TitledBorder)) {
					((TitledBorder) this.pY.getBorder())
					.setTitle(ApplicationManager.getTranslation(DefaultChartDialog.axisYKey, res));
				}

				this.yList.setToolTipText(
						ApplicationManager.getTranslation(DefaultChartDialog.SELECTION_OPERATION_TOOLTIP, res));

				this.labelNoChart
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.noColumnSelectedKey, res));

				this.breakdownMenu.setText(ApplicationManager.getTranslation(DefaultChartDialog.breakdownKey, res));
				this.withoutBreakdownMenu
				.setText(ApplicationManager.getTranslation(DefaultChartDialog.withoutBreakDownOpKey, res));
			}
		}

		private final ActionListener breakdownListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (e.getSource() == DefaultChartDialog.this.withoutBreakdownMenu) {
					DefaultChartDialog.this.currentItem.setColumnSerie(null);
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				} else {
					DefaultChartDialog.this.currentItem.setColumnSerie(e.getActionCommand());
					DefaultChartDialog.this.yList.repaint();
					DefaultChartDialog.this.updateChart();
				}
			}

		};

	}

	public void createConfigurationDialog() {
		this.createDefaultDialog();
	}

	private JDialog createDefaultDialog() {
		if (this.defaultDialog == null) {

			final Window w = SwingUtilities.getWindowAncestor(this.t);
			if (w instanceof Frame) {
				this.defaultDialog = new DefaultChartDialog((Frame) w, this.t);
			} else {
				this.defaultDialog = new DefaultChartDialog((Dialog) w, this.t);
			}

			this.defaultDialog.setLoadButtonVisible(this.loadButtonVisible);
			this.defaultDialog.setSaveButtonVisible(this.saveButtonVisible);

			this.defaultDialog.pack();
			ApplicationManager.center(this.defaultDialog);
		}
		return this.defaultDialog;

	}

	private class CategoryData {

		private List xData = null;

		private List[] yData = null;

		private final Object[] serieValues;

		private final String serie;

		public CategoryData(final List xData, final List[] yData, final Object[] serieValues, final String serie) {
			this.xData = xData;
			this.yData = yData;
			this.serieValues = serieValues;
			this.serie = serie;
		}

		public List getXData() {
			return this.xData;
		}

		public List[] getYData() {
			return this.yData;
		}

		public String getSerie() {
			return this.serie;
		}

		public Object[] getSerieValue() {
			return this.serieValues;
		}

	}

	// @ modif

	protected List truncateDatesToDay(final List dates) {
		final Calendar calendar = Calendar.getInstance();
		final DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT, ApplicationManager.getLocale());
		final String localPattern = ((SimpleDateFormat) formatter).toLocalizedPattern();
		final SimpleDateFormat df = new SimpleDateFormat(localPattern);
		return this.set(dates, calendar, df);
	}

	protected List truncateDatesToMonth(final List dates) {
		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy", ApplicationManager.getLocale());
		return this.set(dates, calendar, df, Calendar.DAY_OF_MONTH);
	}

	protected List truncateDatesToYear(final List dates) {
		final Calendar calendar = Calendar.getInstance();
		final SimpleDateFormat df = new SimpleDateFormat("yyyy");
		return this.set(dates, calendar, df, Calendar.DAY_OF_MONTH, Calendar.MONTH);
	}

	protected List truncateDatesToQuarter(final List dates) {
		final Calendar calendar = Calendar.getInstance();

		final List result = new Vector();
		for (int i = 0; i < dates.size(); i++) {
			Object date = dates.get(i);
			String dateStr = null;
			if (date instanceof Date) {
				calendar.setTime((Date) date);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR, 0);
				calendar.set(Calendar.DAY_OF_MONTH, 1);
				if (calendar.get(Calendar.MONTH) < 3) {
					calendar.set(Calendar.MONTH, 0);
					dateStr = "Q1 " + calendar.get(Calendar.YEAR);
				} else if (calendar.get(Calendar.MONTH) < 6) {
					calendar.set(Calendar.MONTH, 3);
					dateStr = "Q2 " + calendar.get(Calendar.YEAR);
				} else if (calendar.get(Calendar.MONTH) < 9) {
					calendar.set(Calendar.MONTH, 6);
					dateStr = "Q3 " + calendar.get(Calendar.YEAR);
				} else {
					calendar.set(Calendar.MONTH, 9);
					dateStr = "Q4 " + calendar.get(Calendar.YEAR);
				}
				date = calendar.getTime();
			}
			result.add(dateStr);
		}
		return result;
	}

	protected List set(final List dates, final Calendar calendar, final SimpleDateFormat df, final int... fields) {
		final List result = new Vector();
		for (int i = 0; i < dates.size(); i++) {
			Object date = dates.get(i);
			if (date instanceof Date) {
				calendar.setTime((Date) date);
				calendar.set(Calendar.MILLISECOND, 0);
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				for (int j = 0; (fields != null) && (j < fields.length); j++) {
					if (fields[j] == Calendar.DAY_OF_MONTH) {
						calendar.set(fields[j], 1);
					} else {
						calendar.set(fields[j], 0);
					}
				}
				date = calendar.getTime();
				result.add(df.format(date));
			} else {
				result.add(date);
			}
		}
		return result;
	}

	// @end_modif

	protected CategoryData getCategoryData(final String colX, final String colY, final int operation, final String columnSeriesValuesGen,
			final int timeGrouping) {

		// Look form x axis distinct values and put then into a vector
		final List xDistintcValues = new ArrayList();
		ArrayList[] yCorrespondentValues = null;

		final Map shownValue = (Map) this.t.getShownValue(new String[] { colX, colY, columnSeriesValuesGen });

		final List serieDistinctValues = new ArrayList();

		List xData = (List) shownValue.get(colX);

		// @modif
		// if xdata are dates, then allow grouping by day, month, quarter, year
		switch (timeGrouping) {
		case ChartInfo.DAY:
			xData = this.truncateDatesToDay(xData);
			shownValue.put(colX, xData);
			break;
		case ChartInfo.MONTH:
			xData = this.truncateDatesToMonth(xData);
			shownValue.put(colX, xData);
			break;
		case ChartInfo.QUARTER:
			xData = this.truncateDatesToQuarter(xData);
			shownValue.put(colX, xData);
			break;
		case ChartInfo.YEAR:
			xData = this.truncateDatesToYear(xData);
			shownValue.put(colX, xData);
			break;
		}

		// @end_modif

		final List yData = (List) shownValue.get(colY);

		for (int i = 0; i < xData.size(); i++) {
			final Object oData = xData.get(i);
			boolean ready = false;
			ready = xDistintcValues.contains(oData);
			if (!ready) {
				xDistintcValues.add(oData);
			}
		}

		ArrayList aListDistinctValuesColSeriesGen = null;
		if ((columnSeriesValuesGen != null) && !columnSeriesValuesGen.equals(colY)
				&& !columnSeriesValuesGen.equals(colX)) {
			final List vDataColSeriesGen = (List) shownValue.get(columnSeriesValuesGen);
			if (vDataColSeriesGen == null) {
				throw new IllegalArgumentException("no data for column " + columnSeriesValuesGen);
			}
			aListDistinctValuesColSeriesGen = new ArrayList();

			for (int i = 0; i < vDataColSeriesGen.size(); i++) {
				final Object oData = vDataColSeriesGen.get(i);
				boolean ready = false;
				ready = aListDistinctValuesColSeriesGen.contains(oData);
				if (!ready) {
					aListDistinctValuesColSeriesGen.add(oData);
				}
			}

			for (int i = 0; i < aListDistinctValuesColSeriesGen.size(); i++) {
				final Object oValue = aListDistinctValuesColSeriesGen.get(i);
				final Map hValueData = new Hashtable();
				final List auxDataX = new Vector();
				final List auxDataY = new Vector();
				final List auxDataS = new Vector();
				for (int j = 0; j < vDataColSeriesGen.size(); j++) {
					final Object valuej = vDataColSeriesGen.get(j);
					if ((oValue == null) && (valuej == null)) {
						auxDataX.add(xData.get(j));
						auxDataY.add(yData.get(j));
						auxDataS.add(vDataColSeriesGen.get(j));
						continue;
					}
					if ((oValue != null) && (valuej != null) && oValue.equals(valuej)) {
						auxDataX.add(xData.get(j));
						auxDataY.add(yData.get(j));
						auxDataS.add(vDataColSeriesGen.get(j));
					}
				}

				hValueData.put(colX, auxDataX);
				hValueData.put(colY, auxDataY);
				hValueData.put(columnSeriesValuesGen, auxDataS);
				serieDistinctValues.add(hValueData);
			}
		} else {
			serieDistinctValues.add(shownValue);
		}

		yCorrespondentValues = new ArrayList[serieDistinctValues.size()];
		for (int i = 0; i < yCorrespondentValues.length; i++) {
			yCorrespondentValues[i] = new ArrayList();
		}

		for (int j = 0; j < serieDistinctValues.size(); j++) {
			for (int i = 0; i < xDistintcValues.size(); i++) {
				final Object dataX = xDistintcValues.get(i);
				final Object valueY = this.getYValue((List) ((Map) serieDistinctValues.get(j)).get(colX),
						(List) ((Map) serieDistinctValues.get(j)).get(colY), dataX,
						colY, operation);
				yCorrespondentValues[j].add(i, valueY);
				if (dataX == null) {
					xDistintcValues.remove(i);
					xDistintcValues.add(i, "");
				}
			}
		}

		return new CategoryData(xDistintcValues, yCorrespondentValues,
				aListDistinctValuesColSeriesGen == null ? null : aListDistinctValuesColSeriesGen.toArray(),
						columnSeriesValuesGen);
	}

	// Now for each distinct value in X axis calculate the Y axis equivalent
	// value
	protected Object getYValue(final List xDataVector, final List yDataVector, final Object xData, final String yColumnName,
			final int operation) {

		double resY = 0.0;
		if (ChartUtilities_1_0.ROW_NUMBERS_KEY.equals(yColumnName)) {
			for (int j = 0; j < xDataVector.size(); j++) {
				final Object dX = xDataVector.get(j);
				if ((dX == null) || (xData == null)) {
					if ((dX == null) && (xData == null)) {
						resY++;
					}

				} else {
					if (xData.equals(dX)) {
						resY++;
					}
				}
			}
		} else {
			final List yValues = new ArrayList();
			int xDataIndex = xDataVector.indexOf(xData);
			if (xDataIndex < 0) {
				return null;
			}
			for (; xDataIndex >= 0; xDataIndex = CollectionTools.indexOf(xDataVector, xData, xDataIndex + 1)) {
				yValues.add(yDataVector.get(xDataIndex));
			}

			for (int j = 0; j < yValues.size(); j++) {
				final Object yValue = yValues.get(j);
				if (yValue != null) {
					switch (operation) {
					default:
						break;
					case SUM:
						resY += ((Number) yValue).doubleValue();
						break;

					case AVG:
						resY += ((Number) yValue).doubleValue() / yValues.size();
						break;

					case MAX:
						if (j == 0) {
							resY = -2147483648D;
						}
						resY = Math.max(resY, ((Number) yValue).doubleValue());
						break;

					case MIN:
						if (j == 0) {
							resY = 2147483647D;
						}
						resY = Math.min(resY, ((Number) yValue).doubleValue());
						break;
					}
				}
			}
		}
		final Object dato = new Double(resY);
		return dato;
	}

	protected Map getPeriodValues(final List xData, final List yData, final int timeInterval) {
		final Map<TimePeriod, List> res = new Hashtable();

		for (int i = 0; i < xData.size(); i++) {
			final Object oDate = xData.get(i);
			if (oDate == null) {
				continue;
			}

			if (oDate.equals("") || !(oDate instanceof java.util.Date)) {
				continue;
			}
			TimePeriod period = null;
			switch (timeInterval) {
			case DAY:
				period = new Day((java.util.Date) oDate);
				break;
			case MONTH:
				period = new Month((java.util.Date) oDate);
				break;
			case QUARTER:
				period = new Quarter((java.util.Date) oDate);
				break;
			case YEAR:
				period = new Year((java.util.Date) oDate);
				break;
			default:
				period = new Month((java.util.Date) oDate);
				break;
			}
			TimePeriod availablePeriod = null;
			final Iterator<TimePeriod> enumKeys = res.keySet().iterator();
			while (enumKeys.hasNext()) {
				final TimePeriod p = enumKeys.next();
				if (p.compareTo(period) == 0) {
					availablePeriod = p;
					break;
				}
			}

			if (availablePeriod != null) {
				final List dy = res.get(availablePeriod);
				dy.add(yData.get(i));
			} else {
				final List dy = new ArrayList();
				dy.add(0, yData.get(i));
				res.put(period, dy);
			}
		}
		return res;

	}

	public void loadChartConfiguration(final String conf) {
		this.defaultDialog.loadChartConfiguration(conf);
	}

	@Override
	public void setLoadButtonVisible(final boolean visible) {
		this.loadButtonVisible = visible;
		if (this.defaultDialog != null) {
			this.defaultDialog.setLoadButtonVisible(visible);
		}
	}

	@Override
	public void setSaveButtonVisible(final boolean visible) {
		this.saveButtonVisible = visible;
		if (this.defaultDialog != null) {
			this.defaultDialog.setSaveButtonVisible(visible);
		}
	}

}
