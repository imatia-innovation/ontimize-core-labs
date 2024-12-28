package com.ontimize.gui;

import java.util.List;
import java.util.Map;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.table.Table;

public interface IDetailForm extends Internationalization, Freeable {

	public Form getForm();

	public void showDetailForm();

	public void hideDetailForm();

	public Table getTable();

	public void setQueryInsertMode();

	public void setQueryMode();

	public void setInsertMode();

	public void setAttributeToFix(Object attribute, Object value);

	public void resetParentkeys(List parentKeys);

	public void setParentKeyValues(Map parentKeyValues);

	/**
	 * This method sets the keys in the table records.<br>
	 * This keys are used to query the record values
	 * @param tableKeys
	 * @param index
	 */
	public void setKeys(Map tableKeys, int index);

	public void setUpdateMode();

	public String getTableFieldName(Object name);

	public String getFormFieldName(Object name);

	public Map valuesToTable(Map values);

	public Map valuesToForm(Map values);

}
