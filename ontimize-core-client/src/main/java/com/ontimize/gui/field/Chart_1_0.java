package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.UnitType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.util.CollectionTools;
import com.ontimize.util.ObjectTools;

/**
 * The component that allows to show the information in form of chart. Many types are allowed: bars,
 * circles, etc.
 * <p>
 *
 * @see JFreeChart
 */
public class Chart_1_0 extends JPanel implements DataComponent, IChartComponent, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Chart_1_0.class);

	// Possible types of charts.
	/**
	 * A key that allows to count the occurrences of a Xaxis value.
	 */
	public static final String COUNT_COLUMN = "$Count$";

	/**
	 * Constant that sets the PIE chart identifier. By definition, PIE = 0;
	 */
	public static final int PIE = 0;

	/**
	 * Constant that sets the PIE_3D chart identifier. By definition, PIE_3D = 1;
	 */
	public static final int PIE_3D = 1;

	/**
	 * Constant that sets the BAR chart identifier. By definition, BAR = 2;
	 */
	public static final int BAR = 2;

	/**
	 * Constant that sets the BAR_3D chart identifier. By definition, BAR_3D = 3;
	 */
	public static final int BAR_3D = 3;

	/**
	 * Constant that sets the STACKED_3D chart identifier. By definition, STACKED_3D = 4;
	 */
	public static final int STACKED_3D = 4;

	/**
	 * Constant that sets the LINE chart identifier. By definition, LINE = 5;
	 */
	public static final int LINE = 5;

	// Possible Operations

	/**
	 * Constant that sets the SUM operation identifier. By definition, SUM = 0;
	 */
	public static final int SUM = 0;

	/**
	 * Constant that sets the MAX operation identifier. By definition, MAX = 1;
	 */
	public static final int MAX = 1;

	/**
	 * Constant that sets the MIN operation identifier. By definition, MIN = 2;
	 */
	public static final int MIN = 2;

	/**
	 * Constant that sets the AVG operation identifier. By definition, AVG = 3;
	 */
	public static final int AVG = 3;

	/**
	 * An instance of a north panel.
	 */
	protected JPanel northPanel = new JPanel();

	/**
	 * An instance of a chart component panel.
	 */
	protected JPanel chartComponentPanel = new JPanel(new BorderLayout());

	/**
	 * An instance of a scroll pane.
	 */
	protected JScrollPane scroll = new JScrollPane();

	/**
	 * A reference for a chart panel. By default null.
	 */
	protected ChartPanel chartPanel = null;

	/**
	 * A reference to attribute. By default, null.
	 */
	protected Object attribute = null;

	/**
	 * A reference to a x Axis name. By default, null.
	 */
	protected String xAxesName = null;

	/**
	 * A reference to a y Axis name. By default, null.
	 */
	protected List yAxesName = null;

	/**
	 * A reference to a visible series. By default, null.
	 */
	protected List visibleSeries = null;

	/**
	 * The reference to type. By default, LYNE type.
	 */
	protected int type = Chart_1_0.LINE;

	/**
	 * A reference to value. By default, null.
	 */
	protected Map value = null;

	/**
	 * A reference to X label. By default, "".
	 */
	protected String labelX = "";

	/**
	 * A reference to Y label. By default, "".
	 */
	protected String labelY = "";

	/**
	 * A reference to a entity name. By default, null.
	 */
	protected String entityName = null;

	/**
	 * The preferred weight. By default, -1.
	 */
	protected int prefW = -1;

	/**
	 * The preferred height. By default, -1.
	 */
	protected int prefH = -1;

	protected ResourceBundle bundle = null;

	protected String title;

	protected Paint backgroundPaint;

	protected Paint backgroundPlotPaint;

	protected String[] translateSeries;

	/**
	 * Inits parameters and {@link #setLayout(LayoutManager)}.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */

	public Chart_1_0(final Map parameters) {
		this.init(parameters);
		this.setLayout(new BorderLayout());
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.chartComponentPanel);
	}

	/**
	 * Sets the name or names in Y axis and updates the chart.
	 * <p>
	 * @param yAxis the String List with names
	 */
	@Override
	public void setYAxis(final String[] yAxis) {
		this.yAxesName.clear();
		for (int i = 0; i < yAxis.length; i++) {
			this.yAxesName.add(yAxis[i]);
		}
		this.updateChart();
	}

	/**
	 * Sets the preferred size for chart.
	 * <p>
	 * @param w the width
	 * @param h the height
	 */
	@Override
	public void setChartPreferredSize(final int w, final int h) {
		this.prefH = h;
		this.prefW = w;
		if (this.chartPanel != null) {
			this.chartPanel.setPreferredSize(new Dimension(w, h));
			this.revalidate();
		}
	}

	/**
	 * Adds to {@link #visibleSeries} new visible values aned updates chart.
	 * <p>
	 * @param s the value to set visible
	 * @param visible Indicates whether other parameter is visible
	 */
	@Override
	public void setSerieVisible(final String s, final boolean visible) {
		if (this.visibleSeries == null) {
			this.visibleSeries = ObjectTools.clone(this.yAxesName);
		}
		if (visible) {
			if (!this.visibleSeries.contains(s)) {
				this.visibleSeries.add(s);
				this.updateChart();
			}
		} else {
			if (this.visibleSeries.contains(s)) {
				this.visibleSeries.remove(s);
				this.updateChart();
			}
		}
	}

	/**
	 * This method gets the <code>Hashtable</code>, processes one by one its parameters and creates the
	 * chart.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
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
	 *        <td>yes</td>
	 *        <td>The attribute.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The entity name. Mandatory when data are obtained from entity.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>xaxis</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>X axis name.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>yaxis</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Y axis name.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>type</td>
	 *        <td><i>line/bar/bar3d/pie/pie3d/stacked3d</td>
	 *        <td>line</td>
	 *        <td>no</td>
	 *        <td>The chart type</td>
	 *        </tr>
	 *        <tr>
	 *        <td>xlabel</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The X axis label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>ylabel</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>The Y axis label.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>width</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Preferred width size in pixels for chart component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>height</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Preferred height size in pixels for chart component.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Title to set.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgpaint</td>
	 *        <td>A paint registered in the {@link ColorConstants} class</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Paint value to use in the panel when the Chart is contained in</td>
	 *        </tr>
	 *        <tr>
	 *        <td>bgplotpaint</td>
	 *        <td>A paint registered in the {@link ColorConstants} class</td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Paint to use in the chart plot background</td>
	 *        </tr>
	 *        <tr>
	 *        <td>opaque</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Specifies if the chart panel must be opaque or not</td>
	 *        </tr>
	 *        </TABLE>
	 */

	@Override
	public void init(final Map parameters) {

		final Object entity = parameters.get("entity");
		if (entity != null) {
			this.entityName = entity.toString();
		}

		final Object oAttr = parameters.get("attr");
		if (oAttr == null) {
			if (this.entityName == null) {
				throw new IllegalArgumentException(this.getClass().toString() + ": 'attr' parameter is required");
			}
			this.attribute = this.entityName;
		} else {
			this.attribute = oAttr.toString();
		}

		final Object xaxis = parameters.get("xaxis");
		if (xaxis == null) {
			throw new IllegalArgumentException(this.getClass().toString() + ": 'xaxis' parameter is required");
		} else {
			this.xAxesName = xaxis.toString();
		}
		final Object yaxis = parameters.get("yaxis");
		if (yaxis == null) {
			throw new IllegalArgumentException(this.getClass().toString() + ": 'yaxis' parameter is required");
		} else {
			this.yAxesName = ApplicationManager.getTokensAt(yaxis.toString(), ";");
		}

		final Object xlabel = parameters.get("xlabel");
		if (xlabel != null) {
			this.labelX = xlabel.toString();
		}
		final Object ylabel = parameters.get("ylabel");
		if (ylabel != null) {
			this.labelY = ylabel.toString();
		}

		final Object oTitle = parameters.get("title");
		if (oTitle != null) {
			this.title = oTitle.toString();
		}

		final Object type = parameters.get("type");
		if (type != null) {
			if (type.equals("line")) {
				this.type = Chart_1_0.LINE;
			} else if (type.equals("bar")) {
				this.type = Chart_1_0.BAR;
			} else if (type.equals("bar3d")) {
				this.type = Chart_1_0.BAR_3D;
			} else if (type.equals("stacked3d")) {
				this.type = Chart_1_0.STACKED_3D;
			} else if (type.equals("pie")) {
				this.type = Chart_1_0.PIE;
			} else if (type.equals("pie3d")) {
				this.type = Chart_1_0.PIE_3D;
			} else {
				Chart_1_0.logger.debug(this.getClass().toString() + " Unknown chart type " + type);
			}
		}

		final Object width = parameters.get("width");
		if (width != null) {
			try {
				this.prefW = Integer.parseInt(width.toString());
			} catch (final Exception ex) {
				Chart_1_0.logger
				.error(this.getClass().toString() + " Error in 'width' parameter. It must be an Integer", ex);
			}
		}

		final Object height = parameters.get("height");
		if (height != null) {
			try {
				this.prefH = Integer.parseInt(height.toString());
			} catch (final Exception ex) {
				Chart_1_0.logger
				.error(this.getClass().toString() + " Error in 'height' parameter. It must be an Integer", ex);
			}
		}

		final Object paint = parameters.get("bgpaint");
		if (paint != null) {
			try {
				this.backgroundPaint = ColorConstants.paintNameToPaint(paint.toString());
			} catch (final Exception e) {
				Chart_1_0.logger.error(null, e);
			}
		}

		final Object plotPaint = parameters.get("bgplotpaint");
		if (plotPaint != null) {
			try {
				Paint paintNameToPaint = null;
				try {
					paintNameToPaint = ColorConstants.paintNameToPaint(plotPaint.toString());
				} catch (final Exception e) {
					Chart_1_0.logger.error(e.getMessage(), e);
				}
				if (paintNameToPaint != null) {
					this.backgroundPlotPaint = paintNameToPaint;
				} else {
					final Color colorNameToColor = ColorConstants.colorNameToColor(plotPaint.toString());
					this.backgroundPlotPaint = colorNameToColor;
				}
			} catch (final Exception e) {
				Chart_1_0.logger.error(e.getMessage(), e);
			}
		}

		final Object opaque = parameters.get("opaque");
		if ((opaque != null) && !ApplicationManager.parseStringValue(opaque.toString())) {
			this.setOpaque(false);
			if (this.chartComponentPanel != null) {
				this.chartComponentPanel.setOpaque(false);
			}
		}
	}

	/**
	 * Returns the constraints to chart to set in the correct position in parent Container.
	 * <p>
	 * @param parentLayout the parent Layout
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
	 * Gets the parameter value.
	 * <p>
	 * @return the value the Map with List for Xaxis and other for each Yaxis serie
	 */
	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		} else {
			return this.value;
		}
	}

	/**
	 * Changes the type of chart and repaints the chart.
	 * <p>
	 * @param type type of chart
	 */
	@Override
	public void setType(final int type) {
		final int previousType = this.type;
		this.type = type;
		if (previousType != this.type) {
			this.updateChart();
		}
	}

	@Override
	public void setValue(final Object value) {
		if (value instanceof Map) {
			this.value = (Map) value;
			this.updateChart();
		} else {
			this.deleteData();
		}
	}

	/**
	 * Repaints the chart.
	 */
	@Override
	public void updateChart() {
		if (this.isEmpty()) {
			this.chartComponentPanel.removeAll();
			this.chartComponentPanel.revalidate();
			this.chartComponentPanel.repaint();
		} else {
			this.chartComponentPanel.removeAll();
			final JFreeChart chart = (JFreeChart) this.createChart();
			if (this.chartPanel == null) {
				this.chartPanel = new ChartPanel(chart);
				final Dimension d = this.chartPanel.getPreferredSize();
				if (this.prefW != -1) {
					d.width = this.prefW;
				}
				if (this.prefH != -1) {
					d.height = this.prefH;
				}
				this.chartPanel.setPreferredSize(d);
				this.chartPanel.setMouseZoomable(true);
				// chartPanel.setHorizontalZoom(true);
				// chartPanel.setVerticalZoom(true);
			} else {
				this.chartPanel.setChart(chart);
			}

			this.scroll.removeAll();
			this.scroll = new JScrollPane(this.chartPanel);
			this.scroll.setBorder(new EmptyBorder(0, 0, 0, 0));

			if (this.bundle != null) {
				this.setResourceBundle(this.bundle);
			}

			this.chartComponentPanel.add(this.scroll);
			this.chartComponentPanel.revalidate();
			this.chartComponentPanel.repaint();
		}
	}

	/**
	 * Delete data and calls {@link #updateChart()} to repaint.
	 */
	@Override
	public void deleteData() {
		this.value = null;
		this.updateChart();
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
	public int getSQLDataType() {
		return Types.JAVA_OBJECT;
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	@Override
	public void setModifiable(final boolean modifiable) {
	}

	@Override
	public boolean isModifiable() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return (this.value == null) || this.value.isEmpty();
	}

	@Override
	public String getLabelComponentText() {
		return "";
	}

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.bundle = resources;

		if ((this.chartPanel != null) && (this.chartPanel.getChart() != null)) {
			if (this.chartPanel.getChart().getLegend() != null) {
				final Plot currentPlot = this.chartPanel.getChart().getPlot();
				if (currentPlot instanceof CategoryPlot) {
					final CategoryPlot categoryPlot = (CategoryPlot) currentPlot;
					if ((categoryPlot.getDataset() != null)
							&& (categoryPlot.getDataset() instanceof DefaultCategoryDataset)) {
						if (this.visibleSeries == null) {
							this.visibleSeries = this.yAxesName;
						}
						this.translateSeries = new String[this.visibleSeries.size()];
						for (int i = 0; i < this.visibleSeries.size(); i++) {
							this.translateSeries[i] = ApplicationManager
									.getTranslation((String) this.visibleSeries.get(i), resources);
						}
						// ((DefaultCategoryDataset)
						// ((DefaultCategoryDataset)categoryPlot.getDataset()).getColumnKeys()setSeriesNames(translate);
					}

					// Range Axis is Y Axis
					if (this.labelX != null) {
						// Domain Axis is X Axix
						categoryPlot.getDomainAxis()
						.setLabel(ApplicationManager.getTranslation(this.labelX, resources));
					}
					if (this.labelY != null) {
						categoryPlot.getRangeAxis().setLabel(ApplicationManager.getTranslation(this.labelY, resources));
					}
				} else if (currentPlot instanceof XYPlot) {
					final XYPlot xyPlot = (XYPlot) currentPlot;
					if ((xyPlot.getDataset() != null) && (xyPlot.getDataset() instanceof DefaultCategoryDataset)) {
						if (this.visibleSeries == null) {
							this.visibleSeries = this.yAxesName;
						}

						this.translateSeries = new String[this.visibleSeries.size()];

						for (int i = 0; i < this.visibleSeries.size(); i++) {
							this.translateSeries[i] = ApplicationManager
									.getTranslation((String) this.visibleSeries.get(i), resources);
						}
					}

					if (this.labelX != null) {
						xyPlot.getDomainAxis().setLabel(ApplicationManager.getTranslation(this.labelX, resources));
					}

					if (this.labelY != null) {
						xyPlot.getRangeAxis().setLabel(ApplicationManager.getTranslation(this.labelY, resources));
					}

				}
			}
			if (this.title != null) {
				this.chartPanel.getChart().setTitle(new TextTitle(ApplicationManager.getTranslation(this.title)));
			}

			final Component[] popupComponents = this.chartPanel.getPopupMenu().getComponents();
			this.getPopupMenuComponentsTranslate(popupComponents);
		}
	}

	public void getPopupMenuComponentsTranslate(final Component[] component) {
		for (int i = 0; i < component.length; i++) {

			if (component[i] instanceof JMenu) {
				final JMenu popupMenu = (JMenu) component[i];
				final Component[] popupMenuComponents = popupMenu.getPopupMenu().getComponents();
				final JMenuItem popupMenuItemNameParent = (JMenuItem) popupMenuComponents[0];
				popupMenu.setText(ApplicationManager
						.getTranslation(this.MenuLabelName(popupMenuItemNameParent.getActionCommand()), this.bundle));
				this.getPopupMenuComponentsTranslate(popupMenuComponents);
				continue;
			}

			if (component[i] instanceof JMenuItem) {
				final JMenuItem popupMenuItem = (JMenuItem) component[i];
				popupMenuItem.setText(ApplicationManager.getTranslation(popupMenuItem.getActionCommand(), this.bundle));
			}
		}
	}

	public String MenuLabelName(final String string) {
		final char c = string.charAt(5);
		switch (c) {
		case 'I':
			return "Zoom_In";
		case 'O':
			return "Zoom_Out";
		case 'R':
			return "Auto_Range";
		default:
			return string;
		}
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

	/**
	 * Returns an #TableAttribute wheter 'entity' parameter is not null. In other case returns attr.
	 * <p>
	 * @return a #TableAttribute with information about entity data
	 * @see #TableAttribute
	 */
	@Override
	public Object getAttribute() {
		if (this.entityName != null) {
			final TableAttribute tableAttribute = new TableAttribute();
			final List vCols = ObjectTools.clone(this.yAxesName);
			vCols.add(this.xAxesName);
			if (vCols.contains(Chart_1_0.COUNT_COLUMN)) {
				vCols.remove(Chart_1_0.COUNT_COLUMN);
			}
			tableAttribute.setEntityAndAttributes(this.entityName, vCols);
			// tableAttribute.setKeysParentkeysOtherkeys(new Vector(), new
			// Vector);
			return tableAttribute;
		} else {
			return this.attribute;
		}
	}

	@Override
	public void initPermissions() {
	}

	@Override
	public boolean isRestricted() {
		return false;
	}

	/**
	 * Creates the chart in function of parameters: type, labelX, labelY, xAxesName, yAxesName, ...
	 * <p>
	 * @return a JFreeChart with the chart created
	 */
	@Override
	public Object createChart() {
		if (this.visibleSeries == null) {
			this.visibleSeries = ObjectTools.clone(this.yAxesName);
		}

		final String[] colsY = new String[this.visibleSeries.size()];
		final String description = this.title != null ? ApplicationManager.getTranslation(this.title) : "";
		JFreeChart chart = null;
		for (int i = 0; i < this.visibleSeries.size(); i++) {
			colsY[i] = (String) this.visibleSeries.get(i);
		}
		switch (this.type) {
		case PIE:
			CategoryDataset set = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
			chart = ChartFactory.createPieChart(description, DatasetUtilities.createPieDatasetForRow(set, 0), true,
					true, true);
			((PiePlot) chart.getPlot()).setCircular(false);
			// ((PiePlot) chart.getPlot()).setRadiusPercent(0.8);
			((PiePlot) chart.getPlot()).setExplodePercent(0, 1);
			// ((PiePlot)
			// chart.getPlot()).setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
			break;
		case PIE_3D:
			// PieDataset set2 = this.getPieDataset(info.colX,info.colsY[0]);
			set = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
			final PieDataset set2 = DatasetUtilities.createPieDatasetForRow(set, 0);
			chart = ChartFactory.createPieChart3D(description, set2, true, true, true);
			((PiePlot) chart.getPlot()).setCircular(false);
			// ((PiePlot) chart.getPlot()).setRadiusPercent(0.8);
			((PiePlot) chart.getPlot()).setExplodePercent(0, 1);
			// ((PiePlot)
			// chart.getPlot()).setSectionLabelType(PiePlot.NAME_AND_PERCENT_LABELS);
			break;
		case BAR:
			set = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
			chart = ChartFactory.createBarChart(description, this.labelX, this.labelY, set,
					PlotOrientation.VERTICAL, true, true, true);
			chart.setBackgroundPaint(new GradientPaint(0.0F, 0.0F, Color.white, 1000.0F, 0.0F, Color.white));
			break;
		case BAR_3D:
			set = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
			chart = ChartFactory.createBarChart3D(description, this.labelX, this.labelY, set,
					PlotOrientation.VERTICAL, true, true, true);
			break;

		case STACKED_3D:
			set = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
			chart = ChartFactory.createStackedBarChart3D(description, this.labelX, this.labelY, set,
					PlotOrientation.VERTICAL, true, true, true);
			// chart = ChartFactory.createStackedVerticalBarChart3D(description,
			// labelX, labelY, set, true);
			break;
		case LINE:
			if (this.checkXYDataset(this.xAxesName, colsY)) {
				final XYDataset set6 = (XYDataset) this.getXYDataset(this.xAxesName, colsY, colsY);
				chart = ChartFactory.createXYLineChart(description, this.labelX, this.labelY, set6,
						PlotOrientation.VERTICAL, true, true, true);
			} else {
				final CategoryDataset set6 = (CategoryDataset) this.getCategoryDataset(this.xAxesName, colsY, colsY);
				chart = ChartFactory.createLineChart(description, this.labelX, this.labelY, set6,
						PlotOrientation.VERTICAL, true, true, true);

				if (chart.getCategoryPlot() != null) {
					class CustomRend extends LineAndShapeRenderer {

						public CustomRend() {
							// this.shapeScale = 0.6d * this.shapeScale;
						}

					}
					((CategoryPlot) chart.getPlot()).setRenderer(new CustomRend());
				}
			}
			break;
		default:
			Chart_1_0.logger.debug(this.getClass().toString() + " ERROR : CHART TYPE IS INVALID ");
			break;
		}

		if (this.getBackgroundPaint() != null) {
			chart.setBackgroundPaint(this.getBackgroundPaint());
		}

		if (this.getBackgroundPlotPaint() != null) {
			chart.getPlot().setBackgroundPaint(this.getBackgroundPlotPaint());
		} else {
			chart.getPlot().setBackgroundPaint(DataComponent.VERY_LIGHT_YELLOW);
		}

		if (chart.getPlot() instanceof CategoryPlot) {
			((CategoryPlot) chart.getPlot()).getRangeAxis()
			.setLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 15d, 2d, 15d, 15d));

		} else if (chart.getPlot() instanceof XYPlot) {
			((XYPlot) chart.getPlot()).getRangeAxis()
			.setLabelInsets(new RectangleInsets(UnitType.ABSOLUTE, 15d, 2d, 15d, 15d));
		}
		return chart;
	}

	/**
	 * Returns the CategoryDataset.
	 * <p>
	 *
	 * @see Chart_1_0#getCategoryDataset(String, String[], String[], int[])
	 */
	@Override
	public Object getCategoryDataset(final String colX, final String[] colsY, final String[] seriesNames) {
		return this.getCategoryDataset(colX, colsY, seriesNames, null);
	}

	/**
	 * Gets the categoy dataset for chart.
	 * <p>
	 * @param colX the X col
	 * @param colsY the Y cols
	 * @param seriesNames the names of series
	 * @param operations the operations for applying to the columns. By default (operations = null), SUM
	 *        is the operation
	 * @return
	 */
	@Override
	public Object getCategoryDataset(final String colX, final String[] colsY, final String[] seriesNames, final int[] operations) {
		if (!Table.isChartEnabled()) {
			return null;
		}
		// Data vectors
		final long t = System.currentTimeMillis();
		Chart_1_0.logger.debug("Request chart -> colX: {}, colY: {}, seriesName: {}", colX, colsY, seriesNames);
		java.util.List xDataList = null;
		final java.util.List[] yDataLists = new java.util.List[colsY.length];
		for (int i = 0; i < colsY.length; i++) {
			int operation = Chart_1_0.SUM;
			if ((operations != null) && (operations.length > i)) {
				operation = operations[i];
			}
			final CategData categoryData = (CategData) this.getCategoryDataset(colX, colsY[i], operation);
			xDataList = categoryData.getXData();
			yDataLists[i] = categoryData.getYData();
		}
		java.util.List yDataList = yDataLists[0];
		final double[][] d = new double[colsY.length][yDataList.size()];
		for (int i = 0; i < yDataList.size(); i++) {
			for (int j = 0; j < colsY.length; j++) {
				yDataList = yDataLists[j];
				d[j][i] = ((Number) yDataList.get(i)).doubleValue();
			}
		}

		final Comparable[] cK = new Comparable[xDataList.size()];
		for (int i = 0; i < cK.length; i++) {
			if ((xDataList.get(i) instanceof Comparable) || (xDataList.get(i) == null)) {
				cK[i] = (Comparable) xDataList.get(i);
			} else {
				cK[i] = xDataList.get(i).toString();
			}
		}

		final CategoryDataset set = DatasetUtilities.createCategoryDataset(seriesNames, cK, d);
		// DefaultCategoryDataset set = new DefaultCategoryDataset(d);
		// set.setCategories(datosX.toArray());
		// set.setSeriesNames(seriesNames);
		Chart_1_0.logger.trace("getCategoryDataset, Time: {} ms", System.currentTimeMillis() - t);
		return set;
	}

	/**
	 * Calls to {@link #getPieDataset(String, String, int)} with operation = SUM.
	 * <p>
	 * @param colX the x column
	 * @param colY the y column
	 * @return the Dataset for pie chart
	 */
	@Override
	public Object getPieDataset(final String colX, final String colY) {
		return this.getPieDataset(colX, colY, Chart_1_0.SUM);
	}

	/**
	 * Gets the Dataset for pie charts.
	 * <p>
	 * @param colX the X col
	 * @param colY the Y col
	 * @param operation One of the possible operations: SUM, AVG, MAX or MIN
	 * @return the Dataset for pie chart
	 */
	@Override
	public Object getPieDataset(final String colX, final String colY, final int operation) {
		if (!Table.isChartEnabled()) {
			return null;
		}
		Chart_1_0.logger.debug("Request piedataset chart -> colX: {}, colY: {}", colX, colY);
		List vectorXData = (List) this.value.get(colX);

		final List vectorXDataAux = new Vector();
		for (int i = 0; i < vectorXData.size(); i++) {
			Object oData = vectorXData.get(i);
			if (oData == null) {
				oData = "";
			}
			vectorXDataAux.add(i, oData);
		}
		vectorXData = vectorXDataAux;

		final List vectorYData = (List) this.value.get(colY);

		if ((vectorXData != null) && (vectorYData != null)) {
			// Now, we are checking the necessity to group data.
			final List vectorXDefData = new Vector();
			final List vectorYDefData = new Vector();

			for (int i = 0; i < vectorXData.size(); i++) {
				boolean ready = false;

				ready = vectorXDefData.contains(vectorXData.get(i));

				if (!ready) {
					vectorXDefData.add(vectorXDefData.size(), vectorXData.get(i));
					// Now it is adding to Y
					if ((vectorYData != null) && (vectorYData.get(i) != null)) {
						vectorYDefData.add(vectorYDefData.size(), vectorYData.get(i));
					} else {
						if (Chart_1_0.COUNT_COLUMN.equals(colY)) {
							vectorYDefData.add(vectorYDefData.size(), new Double(1));
						} else {
							vectorYDefData.add(vectorYDefData.size(), new Double(0.0));
						}
					}
				} else {
					int index = -1;
					index = vectorXDefData.indexOf(vectorXData.get(i));

					final Number previousYValue = (Number) vectorYDefData.get(index);

					if (Chart_1_0.COUNT_COLUMN.equals(colY)) {
						final double newValue = previousYValue.doubleValue() + 1;
						final Double dNewValue = new Double(newValue);
						vectorYDefData.remove(index);
						vectorYDefData.add(index, dNewValue);
					} else {
						double toSumm = 0.0;
						if (vectorYData.get(i) != null) {
							toSumm = ((Number) vectorYData.get(i)).doubleValue();
						}
						double newValue = previousYValue.doubleValue();
						switch (operation) {
						case SUM:
							newValue = newValue + toSumm;
							break;
						case AVG:
							newValue = newValue + (toSumm / vectorYData.size());
							break;
						case MAX:
							newValue = Math.max(newValue, toSumm);
							break;
						case MIN:
							newValue = Math.min(newValue, toSumm);
							break;
						default:
							newValue = newValue + toSumm;
							break;
						}
						final Double dNewValue = new Double(newValue);
						vectorYDefData.remove(index);
						vectorYDefData.add(index, dNewValue);
					}
				}
			}

			final PieDataset set = DatasetUtilities.createPieDatasetForRow(
					(CategoryDataset) this.getCategoryDataset(colX, new String[] { colY }, new String[] { colY }), 0);
			// DefaultPieDataset set = new DefaultPieDataset(datosYDef);
			return set;
		}
		Chart_1_0.logger.debug("Requested chart null");
		return null;
	}

	/**
	 * Gets the category dataset.
	 * <p>
	 * @param colX the X col
	 * @param colY the Y col
	 * @param operation the operation
	 * @return
	 */
	@Override
	public Object getCategoryDataset(final String colX, final String colY, final int operation) {
		// Search the different values for x axis and put them in a Vector
		final List xAxisDifferentValues = new ArrayList();
		final List yAxisValues = new ArrayList();
		final Map hShownValue = this.value;
		final List vXData = (List) hShownValue.get(colX);
		final List vYData = (List) hShownValue.get(colY);
		if (vXData == null) {
			Chart_1_0.logger.debug(this.getClass().toString() + " : column hasn't been found: " + colX);
			return new CategData(new Vector(), new Vector());
		}
		if ((vYData == null) && !Chart_1_0.COUNT_COLUMN.equals(colY)) {
			Chart_1_0.logger.debug(this.getClass().toString() + " : column hasn't been found: " + colY);
			return new CategData(new Vector(), new Vector());
		}
		for (int i = 0; i < vXData.size(); i++) {
			final Object oData = vXData.get(i);
			boolean ready = false;
			ready = xAxisDifferentValues.contains(oData);
			if (!ready) {
				xAxisDifferentValues.add(oData);
			}
		}
		// For each x axis value calculate the y value
		for (int i = 0; i < xAxisDifferentValues.size(); i++) {
			final Object oXValue = xAxisDifferentValues.get(i);
			double resY = 0.0;
			boolean someValueNotNull = false;
			if (Chart_1_0.COUNT_COLUMN.equals(colY)) {
				someValueNotNull = true;
				// Count the number of occurrences of oXValue
				for (int j = 0; j < vXData.size(); j++) {
					final Object dX = vXData.get(j);
					if ((dX == null) || (oXValue == null)) {
						if ((dX == null) && (oXValue == null)) {
							resY = resY + 1;
						}
					} else {
						if (oXValue.equals(dX)) {
							resY = resY + 1;
						}
					}
				}
			} else {
				final List yValues = new ArrayList();
				int indexInXData = vXData.indexOf(oXValue);
				while (indexInXData >= 0) {
					yValues.add(vYData.get(indexInXData));
					indexInXData = CollectionTools.indexOf(vXData, oXValue, indexInXData + 1);
				}
				// Now calculate the result with the y axis values
				for (int j = 0; j < yValues.size(); j++) {
					final Object oYValue = yValues.get(j);
					if (oYValue == null) {
						continue;
					}
					someValueNotNull = true;
					switch (operation) {
					case SUM:
						resY = resY + ((Number) oYValue).doubleValue();
						break;
					case AVG:
						resY = resY + (((Number) oYValue).doubleValue() / yValues.size());
						break;
					case MAX:
						if (j == 0) {
							resY = Integer.MIN_VALUE;
						}
						resY = Math.max(resY, ((Number) oYValue).doubleValue());
						break;
					case MIN:
						if (j == 0) {
							resY = Integer.MAX_VALUE;
						}
						resY = Math.min(resY, ((Number) oYValue).doubleValue());
						break;
					}
				}
			}
			if (someValueNotNull) {
				yAxisValues.add(i, new Double(resY));
			} else {
				yAxisValues.add(i, null);
			}
			if (oXValue == null) {
				xAxisDifferentValues.remove(i);
				xAxisDifferentValues.add(i, "");
			}
		}
		return new CategData(xAxisDifferentValues, yAxisValues);
	}

	private class CategData {

		private java.util.List Xdata = null;

		private java.util.List Ydata = null;

		public CategData(final java.util.List xData, final java.util.List yData) {
			this.Xdata = xData;
			this.Ydata = yData;
		}

		public java.util.List getXData() {
			return this.Xdata;
		}

		public java.util.List getYData() {
			return this.Ydata;
		}

	}

	/**
	 * Checks the possible inconsistency for pairs x-y dataset values.
	 * <p>
	 * @param colX
	 * @param colY
	 * @return true when data are valid
	 */
	@Override
	public boolean checkXYDataset(final String colX, final String[] colY) {
		for (int i = 0; i < colY.length; i++) {
			final CategData dat = (CategData) this.getCategoryDataset(colX, colY[i], Chart_1_0.SUM);
			final java.util.List x = dat.getXData();
			for (int k = 0; k < x.size(); k++) {
				if ((x.get(k) != null) && (!(x.get(k) instanceof Number))) {
					return false;
				}
			}
			final java.util.List y = dat.getYData();
			for (int k = 0; k < y.size(); k++) {
				if ((y.get(k) != null) && (!(y.get(k) instanceof Number))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Matches the x-y pairs and adds to resultant XYDataSet.
	 * <p>
	 * @param colX the X col
	 * @param colY the Y cols
	 * @param series the name of series
	 * @return the matched xy Dataset
	 */
	@Override
	public Object getXYDataset(final String colX, final String[] colY, final String[] series) {
		final XYSeriesCollection dataCollection = new XYSeriesCollection();
		for (int i = 0; i < series.length; i++) {
			final XYSeries xySeries = new XYSeries(series[i]);
			final CategData dat = (CategData) this.getCategoryDataset(colX, colY[i], Chart_1_0.SUM);
			final java.util.List x = dat.getXData();
			final java.util.List y = dat.getYData();
			for (int k = 0; k < x.size(); k++) {
				final double px = ((Number) x.get(k)).doubleValue();
				if (y.get(k) != null) {
					final double py = ((Number) y.get(k)).doubleValue();
					xySeries.add(px, py);
				}
			}
			dataCollection.addSeries(xySeries);
		}
		return dataCollection;
	}

	@Override
	public Object getChartPanel() {
		return this.chartPanel;
	}

	@Override
	public Paint getBackgroundPaint() {
		return this.backgroundPaint;
	}

	@Override
	public void setBackgroundPaint(final Paint backgroundPaint) {
		this.backgroundPaint = backgroundPaint;
	}

	@Override
	public Paint getBackgroundPlotPaint() {
		return this.backgroundPlotPaint;
	}

	@Override
	public void setBackgroundPlotPaint(final Paint backgroundPlotPaint) {
		this.backgroundPlotPaint = backgroundPlotPaint;
	}

	public void add(final JPanel chartComponentPanel) {
		super.add(chartComponentPanel);
	}

	public void setLayout(final BorderLayout borderLayout) {
		super.setLayout(borderLayout);
	}

	@Override
	public JPanel getChartComponentPanel() {
		return this.chartComponentPanel;
	}

	@Override
	public JScrollPane getScrollPane() {
		return this.scroll;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
