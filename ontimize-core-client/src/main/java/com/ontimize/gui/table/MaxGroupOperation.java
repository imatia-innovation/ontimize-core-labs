package com.ontimize.gui.table;

import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

public class MaxGroupOperation implements GroupOperation {

    public static String headerText = "MAX";

    @Override
    public Number getOperationValue(List list, List rowIndexes, Map requiredColsValues) {
        if ((list == null) || list.isEmpty()) {
            return null;
        }
        double d = Double.MIN_VALUE;
        for (int i = 0; i < list.size(); i++) {
            Object v = list.get(i);
            if ((v != null) && (v instanceof Number)) {
                if (((Number) v).doubleValue() > d) {
                    d = ((Number) v).doubleValue();
                }
            }
        }
        if (Double.compare(d, Double.MIN_VALUE) == 0) {
            return null;
        }
        return Double.valueOf(d);
    }

    /**
     * Not implemented
     */
    @Override
    public JMenuItem getItem() {
        return null;
    }

    /**
     * Not used
     */
    @Override
    public int getOperationId() {
        return TableSorter.MAX;
    }

    /**
     * Not used
     */
    @Override
    public String getOperationText() {
        return null;
    }

    @Override
    public String getHeaderText() {
        return MaxGroupOperation.headerText;
    }

    @Override
    public List<String> getRequiredColumns() {
        return null;
    }

}
