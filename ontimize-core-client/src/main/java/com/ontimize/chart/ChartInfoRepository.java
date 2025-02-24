package com.ontimize.chart;

import java.util.Hashtable;

public class ChartInfoRepository extends Hashtable {

	public void addChartInfo(final ChartInfo chart, final String description) {
		this.put(description, chart);
	}

	public ChartInfo getChartInfo(final String description) {
		return (ChartInfo) this.get(description);
	}

	public void removeChart(final String descr) {
		this.remove(descr);
	}

	public void removeAllCharts() {
		this.clear();
	}

}
