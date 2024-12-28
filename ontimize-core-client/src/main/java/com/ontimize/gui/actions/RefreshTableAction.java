package com.ontimize.gui.actions;

import java.awt.event.ActionEvent;
import java.text.MessageFormat;
import java.util.List;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.gui.table.RefreshTableListener;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.dto.EntityResult;

public class RefreshTableAction extends AbstractButtonAction {

    public static final String M_INSERT_VALUE_FIELD = "value_must_be_entered_message";

    protected String tableEntity = null;

    protected String messageWithoutData = null;

    protected boolean fitTableCols = false;

    private boolean firstTime = true;

    public RefreshTableAction(String tableEntity) {
        this.tableEntity = tableEntity;
    }

    public RefreshTableAction(String tableEntity, String messageWithoutData) {
        this.tableEntity = tableEntity;
        this.messageWithoutData = messageWithoutData;
    }

    public RefreshTableAction(String tableEntity, String messageWithoutData, boolean adjustTableCols) {
        this.tableEntity = tableEntity;
        this.messageWithoutData = messageWithoutData;
        this.fitTableCols = adjustTableCols;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final Form f = this.getForm(e);
        if (f != null) {
            FormComponent c = f.getDataFieldReference(this.tableEntity);
            if (c instanceof Table) {
                final Table t = (Table) c;
                if (t != null) {
                    if (!t.isEnabled()) {
                        t.setEnabled(true);
                    }
                    List v = t.getParentKeys();
                    boolean someEmpty = false;
                    String emptyAttribute = null;
                    for (int i = 0; i < v.size(); i++) {
                        if (f.isEmpty(v.get(i).toString()) && (f.getDataFieldReference(v.get(i).toString()) != null)
                                && f.getDataFieldReference(v.get(i).toString()).isRequired()) {
                            emptyAttribute = v.get(i).toString();
                            someEmpty = true;
                            break;
                        }
                    }
                    if (someEmpty) {
                        String translation = ApplicationManager.getTranslation(RefreshTableAction.M_INSERT_VALUE_FIELD,
                                f.getResourceBundle());
                        String fieldAttribute = ApplicationManager.getTranslation(emptyAttribute,
                                f.getResourceBundle());
                        Object[] args = { fieldAttribute };
                        String menssage = MessageFormat.format(translation, args);
                        f.message(menssage, Form.WARNING_MESSAGE);
                        return;
                    } else {
                        boolean adjust = this.firstTime || this.fitTableCols;
                        if (this.firstTime) {
                            this.firstTime = false;
                        }
                        t.refresh(adjust);
                        t.addRefreshTableListener(new RefreshTableListener() {

                            @Override
                            public void postIncorrectRefresh(RefreshTableEvent e) {

                            }

                            @Override
                            public void postCorrectRefresh(RefreshTableEvent e) {
                                if (t.isEmpty() && (t.getValue() != null) && (t.getValue() instanceof EntityResult)
                                        && (((EntityResult) t.getValue())
                                            .getCode() != EntityResult.OPERATION_WRONG)) {
                                    if (RefreshTableAction.this.messageWithoutData != null) {
                                        f.message(RefreshTableAction.this.messageWithoutData, Form.INFORMATION_MESSAGE);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        }
    }

}
