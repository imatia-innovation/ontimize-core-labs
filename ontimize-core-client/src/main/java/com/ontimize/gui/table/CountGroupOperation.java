package com.ontimize.gui.table;

import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;

public class CountGroupOperation implements GroupOperation {

    public static String headerText = "COUNT";

    @Override
    public Number getOperationValue(List list, List rowIndexes, Map requiredColsValues) {
        int i = 0;
        if (list != null) {
            i = list.size();
        }
        return Integer.valueOf(i);
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
        return TableSorter.SUM;
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
        return CountGroupOperation.headerText;
    }

    @Override
    public List<String> getRequiredColumns() {
        return null;
    }

}
