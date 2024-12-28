package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.chart.ChartVersionControl;
import com.ontimize.gui.ColorConstants;
import com.ontimize.gui.Freeable;
import com.ontimize.util.templates.ITemplateField;

/**
 * The component that allows to show the information in form of chart. Many types are allowed: bars,
 * circles, etc.
 * <p>
 *
 * @see JFreeChart
 */
public class Chart extends JPanel implements DataComponent, IChartComponent, ITemplateField, Freeable {

	private static final Logger logger = LoggerFactory.getLogger(Chart.class);

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
	protected Object chartPanel = null;

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
	protected int type = Chart.LINE;

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

	protected IChartComponent chartImpl;

	/**
	 * Inits parameters and {@link #setLayout(LayoutManager)}.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */

	public Chart(final Map parameters) {
		this.init(parameters);
		this.setLayout(new BorderLayout());
		this.setBorder(null);
		this.add(this.chartImpl.getChartComponentPanel());
	}

	/**
	 * This method create the component detecting version of JfreeChart library in classpath.
	 * @param parameters <code>Hashtable</code> with parameters
	 */
	public void createChartComponent(final Map parameters) {
		if (ChartVersionControl.isVersion_1_0()) {
			this.chartImpl = new Chart_1_0(parameters);
		} else {
			try {
				final Class rootClass = Class.forName("com.ontimize.gui.field.Chart_0_9");
				final Class[] p = { Hashtable.class };
				final Constructor constructorChart = rootClass.getConstructor(p);
				final Object[] params = { p };
				this.chartImpl = (IChartComponent) constructorChart.newInstance(params);
			} catch (final Exception e) {
				Chart.logger.error(null, e);
			}
		}
	}

	/**
	 * Sets the name or names in Y axis and updates the chart.
	 * <p>
	 * @param yAxis the String List with names
	 */
	@Override
	public void setYAxis(final String[] yAxis) {
		this.chartImpl.setYAxis(yAxis);
	}

	/**
	 * Sets the preferred size for chart.
	 * <p>
	 * @param w the width
	 * @param h the height
	 */
	@Override
	public void setChartPreferredSize(final int w, final int h) {
		this.chartImpl.setChartPreferredSize(w, h);
	}

	/**
	 * Adds to {@link #visibleSeries} new visible values aned updates chart.
	 * <p>
	 * @param s the value to set visible
	 * @param visible Indicates whether other parameter is visible
	 */
	@Override
	public void setSerieVisible(final String s, final boolean visible) {
		this.chartImpl.setSerieVisible(s, visible);
	}

	/**
	 * Returns the chart panel.
	 * <p>
	 * @return the chartPanel to draw
	 */
	@Override
	public Object getChartPanel() {
		return this.chartImpl.getChartPanel();
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
		// Object entity = parameters.get("entity");
		// if (entity != null) {
		// this.entityName = entity.toString();
		// }
		//
		// Object atrib = parameters.get("attr");
		// if (atrib == null) {
		// if (entityName == null) {
		// throw new IllegalArgumentException(this.getClass().toString() +
		// ": 'attr' parameter is required");
		// }
		// this.attribute = entityName;
		// } else {
		// attribute = atrib.toString();
		// }
		//
		// Object xaxis = parameters.get("xaxis");
		// if (xaxis == null) {
		// throw new IllegalArgumentException(this.getClass().toString() +
		// ": 'xaxis' parameter is required");
		// } else {
		// xAxesName = xaxis.toString();
		// }
		// Object yaxis = parameters.get("yaxis");
		// if (yaxis == null) {
		// throw new IllegalArgumentException(this.getClass().toString() +
		// ": 'yaxis' is required");
		// } else {
		// yAxesName = ApplicationManager.getTokensAt(yaxis.toString(), ";");
		// }
		//
		// Object xlabel = parameters.get("xlabel");
		// if (xlabel != null) {
		// labelX = xlabel.toString();
		// }
		// Object ylabel = parameters.get("ylabel");
		// if (ylabel != null) {
		// labelY = ylabel.toString();
		// }
		//
		// Object oTitle = parameters.get("title");
		// if (oTitle != null) {
		// this.title = oTitle.toString();
		// }
		//
		// Object type = parameters.get("type");
		// if (type != null) {
		// if (type.equals("line")) {
		// this.type = Chart.LINE;
		// } else if (type.equals("bar")) {
		// this.type = BAR;
		// } else if (type.equals("bar3d")) {
		// this.type = BAR_3D;
		// } else if (type.equals("stacked3d")) {
		// this.type = STACKED_3D;
		// } else if (type.equals("pie")) {
		// this.type = PIE;
		// } else if (type.equals("pie3d")) {
		// this.type = PIE_3D;
		// } else {
		// }
		// }
		//
		// Object width = parameters.get("width");
		// if (width != null) {
		// try {
		// this.prefW = Integer.parseInt(width.toString());
		// } catch (Exception ex) {
		// }
		// }
		//
		// Object height = parameters.get("height");
		// if (height != null) {
		// try {
		// this.prefH = Integer.parseInt(height.toString());
		// } catch (Exception ex) {
		// }
		// }
		//
		// Object paint = parameters.get("bgpaint");
		// if (paint != null) {
		// try {
		// this.backgroundPaint =
		// ColorConstants.paintNameToPaint(paint.toString());
		// } catch (Exception e) {
		// logger.error(null,e);
		// }
		// }
		//
		// Object plotPaint = parameters.get("bgplotpaint");
		// if (plotPaint != null) {
		// try {
		// Paint paintNameToPaint = null;
		// try{
		// paintNameToPaint =
		// ColorConstants.paintNameToPaint(plotPaint.toString());
		// }catch (Exception e) {
		// if (DEBUG){
		// logger.error(null,e);
		// }
		// }
		// if (paintNameToPaint != null) {
		// this.backgroundPlotPaint = paintNameToPaint;
		// } else {
		// Color colorNameToColor =
		// ColorConstants.colorNameToColor(plotPaint.toString());
		// this.backgroundPlotPaint = colorNameToColor;
		// }
		// } catch (Exception e) {
		// if (DEBUG) {
		// logger.error(null,e);
		// }
		// }
		// }
		//
		// Object opaque = parameters.get("opaque");
		// if ((opaque!=null) &&
		// (!ApplicationManager.parseStringValue(opaque.toString()))){
		// this.setOpaque(false);
		// if (this.chartComponentPanel!=null){
		// this.chartComponentPanel.setOpaque(false);
		// }
		// }
		this.createChartComponent(parameters);

	}

	/**
	 * Returns the constraints to chart to set in the correct position in parent Container.
	 * <p>
	 * @param parentLayout the parent Layout
	 */
	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		return this.chartImpl.getConstraints(parentLayout);
	}

	/**
	 * Gets the parameter value.
	 * <p>
	 * @return the value the Map with List for Xaxis and other for each Yaxis serie
	 */
	@Override
	public Object getValue() {
		if (this.chartImpl.isEmpty()) {
			return null;
		} else {
			return this.chartImpl.getValue();
		}
	}

	/**
	 * Changes the type of chart and repaints the chart.
	 * <p>
	 * @param type type of chart
	 */
	@Override
	public void setType(final int type) {
		this.chartImpl.setType(type);
	}

	@Override
	public void setValue(final Object value) {
		this.chartImpl.setValue(value);
	}

	/**
	 * Repaints the chart.
	 */
	@Override
	public void updateChart() {
		this.chartImpl.updateChart();
	}

	/**
	 * Delete data and calls {@link #updateChart()} to repaint.
	 */
	@Override
	public void deleteData() {
		this.chartImpl.deleteData();
		this.chartImpl.updateChart();
	}

	@Override
	public boolean isModified() {
		return this.chartImpl.isModified();
	}

	@Override
	public boolean isRequired() {
		return this.chartImpl.isRequired();
	}

	@Override
	public void setRequired(final boolean required) {
		this.chartImpl.setRequired(required);
	}

	@Override
	public int getSQLDataType() {
		return this.chartImpl.getSQLDataType();
	}

	@Override
	public boolean isHidden() {
		return this.chartImpl.isHidden();
	}

	@Override
	public void setModifiable(final boolean modifiable) {
		this.chartImpl.setModifiable(modifiable);
	}

	@Override
	public boolean isModifiable() {
		return this.chartImpl.isModifiable();
	}

	@Override
	public boolean isEmpty() {
		return this.chartImpl.isEmpty();
	}

	@Override
	public String getLabelComponentText() {
		return this.chartImpl.getLabelComponentText();
	}

	@Override
	public void setComponentLocale(final Locale l) {
		this.chartImpl.setComponentLocale(l);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		this.chartImpl.setResourceBundle(resources);
	}

	@Override
	public List getTextsToTranslate() {
		return this.chartImpl.getTextsToTranslate();
	}

	/**
	 * Returns an #TableAttribute whether 'entity' parameter is not null. In other case returns attr.
	 * <p>
	 * @return a #TableAttribute with information about entity data
	 * @see #TableAttribute
	 */
	@Override
	public Object getAttribute() {
		return this.chartImpl.getAttribute();
	}

	@Override
	public void initPermissions() {
		this.chartImpl.initPermissions();
	}

	@Override
	public boolean isRestricted() {
		return this.chartImpl.isRestricted();
	}

	/**
	 * Creates the chart in function of parameters: type, labelX, labelY, xAxesName, yAxesName, ...
	 * <p>
	 * @return a JFreeChart with the chart created
	 */
	@Override
	public Object createChart() {
		return this.chartImpl.createChart();
	}

	/**
	 * Returns the CategoryDataset.
	 * <p>
	 *
	 * @see Chart#getCategoryDataset(String, String[], String[], int[])
	 */
	@Override
	public Object getCategoryDataset(final String colX, final String[] colsY, final String[] seriesNames) {
		return this.chartImpl.getCategoryDataset(colX, colsY, seriesNames);
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
		return this.chartImpl.getCategoryDataset(colX, colsY, seriesNames, operations);
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
		return this.chartImpl.getPieDataset(colX, colY);
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
		return this.chartImpl.getPieDataset(colX, colY, operation);
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
		return this.chartImpl.getCategoryDataset(colX, colY, operation);
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
		return this.chartImpl.checkXYDataset(colX, colY);
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
		return this.chartImpl.getXYDataset(colX, colY, series);
	}

	@Override
	public Paint getBackgroundPaint() {
		return this.chartImpl.getBackgroundPaint();
	}

	@Override
	public void setBackgroundPaint(final Paint backgroundPaint) {
		this.chartImpl.setBackgroundPaint(backgroundPaint);
	}

	@Override
	public Paint getBackgroundPlotPaint() {
		return this.chartImpl.getBackgroundPlotPaint();
	}

	@Override
	public void setBackgroundPlotPaint(final Paint backgroundPlotPaint) {
		this.chartImpl.setBackgroundPlotPaint(backgroundPlotPaint);
	}

	@Override
	public JPanel getChartComponentPanel() {
		return this.chartImpl.getChartComponentPanel();
	}

	@Override
	public JScrollPane getScrollPane() {
		return this.chartImpl.getScrollPane();
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_IMAGE;
	}

	@Override
	public Object getTemplateDataValue() {
		final Object chartPanel = this.chartImpl.getChartPanel();
		if (chartPanel instanceof Printable) {
			final BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			final Printable printable = (Printable) chartPanel;
			final PageFormat pf = new PageFormat();
			final Paper p = new Paper();
			p.setImageableArea(0, 0, this.getWidth(), this.getHeight());
			pf.setPaper(p);

			try {
				printable.print(bi.getGraphics(), pf, 0);
			} catch (final PrinterException e) {
				Chart.logger.error(null, e);
				return null;
			}
			return bi;
		}
		return null;
	}

	@Override
	public void free() {
		// TODO Auto-generated method stub

	}

}
