package com.ontimize.gui.container;

import java.util.List;
import java.util.Map;

import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.IdentifiedElement;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:
 * </p>
 */
public interface DataComponentGroup extends FormComponent, IdentifiedElement {

	/**
	 * Gets a data set with field values.<br>
	 * @return The key is the attribute of the data field and the value is the data field value
	 */
	public Map getGroupValue();

	public void setGroupValue(Map value);

	public String getLabel();

	public List getAttributes();

	public void setAllEnabled(boolean en);

	public void setAllModificable(boolean modif);

}
