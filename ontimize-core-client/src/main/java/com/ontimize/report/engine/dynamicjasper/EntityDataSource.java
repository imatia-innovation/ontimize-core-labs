package com.ontimize.report.engine.dynamicjasper;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.fill.JRFillField;
import net.sf.jasperreports.engine.fill.JRFillParameter;

/**
 * <p>
 * Wrappers a Ontimize {@link Entity} into a Jasper data source to allow allow fill reports without
 * data transformation.
 * <p>
 * This data source can rewinds the internal index and return to first element.
 *
 * @see JRDataSource
 * @see JRRewindableDataSource
 * @see JasperFillManager#fillReport(JasperReport, String, Map, JRDataSource)
 * @author Imatia Innovation S.L.
 * @since 07/11/2008
 */
public class EntityDataSource implements JRRewindableDataSource {

	protected int sessionId;

	protected Entity entity;

	protected String[] keys = null;

	protected Map keysValues = null;

	protected EntityResult result = null;

	private int index = -1;

	private int size = -1;

	public EntityDataSource(final int sessionId, final Entity entity) {
		this.sessionId = sessionId;
		this.entity = entity;
	}

	@Override
	public Object getFieldValue(final JRField field) throws JRException {
		if ((this.result == null) || (field == null)) {
			return null;
		}
		final String name = field.getName();
		if (name == null) {
			return null;
		}
		final Object obj = this.result.get(name);
		if ((obj == null) || !(obj instanceof List)) {
			return null;
		}
		final List v = (List) obj;
		return (this.index >= 0) && (this.index < this.size) ? v.get(this.index) : null;
	}

	@Override
	public boolean next() throws JRException {
		if (this.index == -1) {
			this.result = this.doQuery();
			this.size = this.result.calculateRecordNumber();
		}
		this.index++;
		final boolean next = this.index < this.size;
		return next;
	}

	@Override
	public void moveFirst() {
		if (this.keys == null) {
			return;
		}
		this.index = -1;
	}

	/**
	 * <p>
	 * Set entity keys list.
	 * @param keys Entity keys list
	 */
	public void setKeys(final String[] keys) {
		this.keys = keys;
	}

	/**
	 * Set the filter keys values.
	 * <li>If keys attribute is null, the query is filter by all keys.
	 * <li>If keys attribute is not null, only given keys are choosed.
	 * @param kv Keys values
	 */
	public void setKeysValues(final Map kv) {
		this.keysValues = new Hashtable();
		if (kv == null) {
			return;
		}

		// Convert.
		final Object[] l = this.keys != null ? this.keys : kv.keySet().toArray();
		for (int i = 0, size = l.length; i < size; i++) {
			final Object selected = l[i];
			final Object o = kv.get(selected);
			final Object value = this.getValue(o); // Convert.
			if (value != null) {
				this.keysValues.put(selected, value);
			}
		}
	}

	private Object getValue(final Object o) {
		if (o instanceof JRFillField) {
			final JRFillField f = (JRFillField) o;
			final Object v = f.getValue();
			return v;
		} else if (o instanceof JRFillParameter) {
			final JRFillParameter p = (JRFillParameter) o;
			final Object v = p.getValue();
			return v;
		} else {
			return o;
		}
	}

	protected EntityResult doQuery() throws JRException {
		Map keysCopy = this.keysValues;
		if (keysCopy == null) {
			keysCopy = new Hashtable();
		}
		final List attr = new Vector();

		EntityResult er = null;
		try {
			er = this.entity.query(keysCopy, attr, this.sessionId);
			if (er.getCode() == EntityResult.OPERATION_WRONG) {
				throw new Exception(er.getMessage());
			}
		} catch (final Exception e) {
			throw new JRException(e);
		}
		return er;
	}

}
