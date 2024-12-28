package com.ontimize.chart;

import java.util.Hashtable;

import org.jfree.chart.JFreeChart;

public class ChartRepository_1_0 extends Hashtable {

	public void addChart(final JFreeChart chart, final String description) {
		this.put(description, chart);
	}

	public JFreeChart getChart(final String description) {
		return (JFreeChart) this.get(description);
	}

	public void removeChart(final String descr) {
		this.remove(descr);
	}

	public void removeAllCharts() {
		this.clear();
	}

};
