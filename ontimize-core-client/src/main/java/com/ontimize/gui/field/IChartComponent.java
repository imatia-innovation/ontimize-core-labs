package com.ontimize.gui.field;

import java.awt.LayoutManager;
import java.awt.Paint;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Interface to implement by all chart components independent of version of JFreeChart that it is
 * being used. At this moment, <code>Ontimize</code> provides two different implementations for
 * component Chart:
 * <ul>
 * <li>Chart_0_9: with JFreeChart 0.9.3 library
 * <li>Chart_1_0: with JFreeChart 1.0 library and newer
 * </ul>
 *
 * @author Imatia Innovation
 */
public interface IChartComponent {

    /**
     * Defines method that returns where chart panel is drawed
     * @return the chart panel
     */
    public Object getChartPanel();

    /**
     * Creates and returns the chart created.
     * @return the chart created
     */
    public Object createChart();

    /**
     * Method called to update data of chart.
     */
    public void updateChart();

    /**
     * Returns the CategoryDataset.
     */
    public Object getCategoryDataset(String colX, String[] colsY, String[] seriesNames);

    /**
     * Returns the CategoryDataset.
     */
    public Object getCategoryDataset(String colX, String[] colsY, String[] seriesNames, int[] operations);

    /**
     * Returns the CategoryDataset.
     */
    public Object getCategoryDataset(String colX, String colY, int operation);

    /**
     * Returns the PieDataset.
     */
    public Object getPieDataset(String colX, String colY);

    /**
     * Returns the PieDataset.
     */
    public Object getPieDataset(String colX, String colY, int operation);

    /**
     * Returns the XYDataset.
     */
    public Object getXYDataset(String colX, String[] colY, String[] series);

    /**
     * Checks data for each column of horizontal axis.
     * @param colX the X col
     * @param colY Array with names of Y cols
     * @return true when data are correct
     */
    public boolean checkXYDataset(String colX, String[] colY);

    /**
     * Gets the paint of background of charts.
     * @return the background paint
     */
    public Paint getBackgroundPaint();

    /**
     * Sets the paint of background of charts.
     * @param backgroundPaint the background paint to set
     */
    public void setBackgroundPaint(Paint backgroundPaint);

    /**
     * Gets the paint of background in plot of charts.
     * @return the background paint for plots
     */
    public Paint getBackgroundPlotPaint();

    /**
     * Sets the paint of background in plot of charts.
     * @param backgroundPaint the background paint to set
     */
    public void setBackgroundPlotPaint(Paint backgroundPlotPaint);

    /**
     * Sets the preferred size for chart.
     * @param w width
     * @param h height
     */
    public void setChartPreferredSize(int w, int h);

    /**
     * Sets the serie visible
     * @param s the serie to set visible
     * @param visible condition of visibility
     */
    public void setSerieVisible(String s, boolean visible);

    /**
     * Set YAxis.
     * @param yAxis Array with YAxis
     */
    public void setYAxis(String[] yAxis);

    /**
     * Gets value.
     * @return the value
     */
    public Object getValue();

    /**
     * Gets the component with chart panel is placed.
     * @return the chart component
     */
    public JPanel getChartComponentPanel();

    /**
     * Gets the scroll pane where is placed.
     * @return the chart component
     */
    public JScrollPane getScrollPane();

    /**
     * Sets value.
     * @param value the value to set
     */
    public void setValue(Object value);

    /**
     * Gets the label component text.
     * @return the label component text.
     */
    public String getLabelComponentText();

    /**
     * Sets the type of chart.
     * @param type the type of chart
     */
    public void setType(int type);

    public void setResourceBundle(ResourceBundle resources);

    public Object getAttribute();

    public Object getConstraints(LayoutManager parentLayout);

    public boolean isModified();

    public boolean isRequired();

    public boolean isEmpty();

    public void deleteData();

    public void setRequired(boolean required);

    public int getSQLDataType();

    public boolean isHidden();

    public void setModifiable(boolean modifiable);

    public boolean isModifiable();

    public boolean isRestricted();

    public void initPermissions();

    public List getTextsToTranslate();

    public void setComponentLocale(Locale l);

}
