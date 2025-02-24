package com.ontimize.util.templates;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class OpenOfficeTemplateFields {

	protected List singleFields = new Vector();

	protected Map tableFields = new Hashtable();

	public OpenOfficeTemplateFields(final List fields) {
		if (fields != null) {
			for (int i = 0; i < fields.size(); i++) {
				if (((String) fields.get(i)).indexOf(".") < 0) {
					this.singleFields.add(fields.get(i));
				} else {

					final String entityName = ((String) fields.get(i)).substring(0, ((String) fields.get(i)).indexOf("."));
					final String columnName = ((String) fields.get(i)).substring(((String) fields.get(i)).indexOf(".") + 1);
					if (this.tableFields.containsKey(entityName)) {
						final List columns = (List) this.tableFields.get(entityName);
						columns.add(columnName);
						this.tableFields.put(entityName, columns);
					} else {
						final List columns = new Vector();
						columns.add(columnName);
						this.tableFields.put(entityName, columns);
					}
				}
			}
		}
	}

	/**
	 * @return the singleFields
	 */
	public List getSingleFields() {
		return this.singleFields;
	}

	public List getTableNames() {
		final List result = new Vector();
		final Iterator entities = this.tableFields.keySet().iterator();
		while (entities.hasNext()) {
			result.add(entities.next());
		}
		return result;
	}

	public List getTableFields(final String tableName) {
		return (List) this.tableFields.get(tableName);
	}

	public Map checkTemplateFieldValues(final Map fieldValues) {
		final Map result = new Hashtable();
		if (fieldValues != null) {
			result.putAll(fieldValues);
		}
		for (int i = 0; i < this.singleFields.size(); i++) {
			if (!result.containsKey(this.singleFields.get(i))) {
				result.put(this.singleFields.get(i), " - ");
			}
		}

		return result;
	}

	public Map checkTemplateTableValues(final Map tableValues) {
		final Map result = new Hashtable();
		if (tableValues != null) {
			result.putAll(tableValues);
		}
		final Iterator entities = this.tableFields.keySet().iterator();
		while (entities.hasNext()) {
			final String entity = (String) entities.next();
			if (!result.containsKey(entity)) {
				final EntityResult resEnt = new EntityResultMapImpl();
				final List entityColumns = (List) this.tableFields.get(entity);
				final Map currentReg = new Hashtable();
				for (int i = 0; i < entityColumns.size(); i++) {
					currentReg.put(entityColumns.get(i), "  ");
				}
				resEnt.addRecord(currentReg);
				result.put(entity, resEnt);
			}
		}
		return result;
	}

}
