package com.ontimize.gui;

import java.util.EventObject;
import java.util.List;

public class RowTransferEvent extends EventObject {

    private List transferredRowKey = null;

    public RowTransferEvent(Object source, List transferredRows) {
        super(source);
        this.transferredRowKey = transferredRows;
    }

    public List getTransferredRowsKeys() {
        return this.transferredRowKey;
    }

}
