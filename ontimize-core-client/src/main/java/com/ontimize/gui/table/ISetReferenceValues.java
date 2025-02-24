package com.ontimize.gui.table;

import java.util.List;
import java.util.Map;

import com.ontimize.jee.common.db.NullValue;

/**
 * This interface must be implemented for all editors that use the 'onsetvalueset' parameter, at
 * least if they want to send the update data to the database in the insert and update
 * operations<br>
 */
public interface ISetReferenceValues {

	/**
	 * Returns an object with the values configured in the 'onsetvalueset' parameter. This method will
	 * be call when table execute an update or insert operations against the database.
	 * @param useNullValues If this parameter is true then the method must return a {@link NullValue}
	 *        object if the column to set is null. If this parameter is false then the column that is
	 *        null is not included in the result
	 * @return
	 */
	public Map getSetData(boolean useNullValues);

	/**
	 * Gets the list of columns to update in the table
	 * @return
	 */
	public List getSetColumns();

}
