package com.ontimize.gui.field;

import javax.swing.JTextField;

import com.ontimize.gui.CreateForms;
import com.ontimize.gui.Form;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.jee.common.dto.EntityResult;

public interface ReferenceDataComponent extends DataComponent, ReferenceComponent, CreateForms, OpenDialog {

    /**
     * Gets the code field.
     * @return a <code>JTextField</code> with the code field.
     */
    public JTextField getCodeField();

    /**
     * The name of the code.
     * @return a <code>String</code> with the code name.
     */
    public String getCodeFieldName();

    /**
     * Checks whether the codefield is visible.
     * @return a boolean
     */
    public boolean isCodeFieldVisible();

    /**
     * Checks whether the code search is visible.
     * @return true if the code search is visible.
     */
    public boolean isCodeSearchVisible();

    /**
     * Gets the code search field name.
     * @return a <code>String</code> with the search field name.
     */
    public String getCodeSearchFieldName();

    /**
     * Gets the parent form.
     * @return a <code>Form</code> where it's the component in.
     */
    public Form getParentForm();

    /**
     * Performs the entity query for the column and the value that are passed as entry parameters.
     * @param column a <code>String</code> with the name of the column.
     * @param value a <code>Object</code> with the value to filter.
     * @return a <code>EntityResult</code> with the result operation.
     */
    public EntityResult queryBy(String column, Object value);

}
