package com.ontimize.report.engine.dynamicjasper;

import java.awt.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.util.remote.BytesBlock;
import com.ontimize.report.TableSorter;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRRewindableDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;

/**
 * <p>
 * Wrappers a Ontimize {@link EntityResult} into a Jasper data source to allow fill reports without
 * data transformation.
 *
 * @see JRDataSource
 * @see JasperFillManager#fillReport(JasperReport, String, Map, JRDataSource)
 * @see EntityDataSource
 * @see ReportServer#fill(int, Hashtable, Hashtable, EntityResult)
 * @author Imatia Innovation S.L.
 * @since 05/11/2008
 */
public class EntityResultDataSource implements JRDataSource, JRRewindableDataSource {

	private static final Logger logger = LoggerFactory.getLogger(EntityResultDataSource.class);

	protected EntityResult result;

	private int index;

	private final int size;

	public EntityResultDataSource(final EntityResult result) {
		this.result = result;

		this.size = result.calculateRecordNumber();
		this.index = -1;
	}

	public EntityResultDataSource(final TableModel originalmodel, final TableSorter sorter) {
		this.result = this.createEntityResultFromTableSorter(originalmodel, sorter);
		this.size = this.result.calculateRecordNumber();
		this.index = -1;
	}

	public EntityResult createEntityResultFromTableSorter(final TableModel model, final TableSorter sorter) {
		return sorter.getOrderedEntityResult(model);
	}

	@Override
	public Object getFieldValue(final JRField field) throws JRException {
		final Object obj = this.result.get(field.getName());
		if ((obj == null) || !(obj instanceof List)) {
			return null;
		}

		final List v = (List) obj;

		final Class fieldClass = field.getValueClass();
		Object value = (this.index >= 0) && (this.index < this.size) ? v.get(this.index) : null;

		if (java.awt.Image.class.equals(fieldClass) && (value instanceof BytesBlock)) {
			final Image im = new ImageIcon(((BytesBlock) value).getBytes()).getImage();
			v.set(this.index, im);
			value = im;
		}
		return value;
	}

	@Override
	public boolean next() throws JRException {
		this.index++;
		return this.index < this.size;
	}

	public void reset() {
		this.index = -1;
	}

	@Override
	public void moveFirst() throws JRException {
		this.index = -1;
	}

	public EntityResult getEntityResult() {
		return this.result;
	}

	public JRField[] getFields() {
		return EntityResultDataSource.getFields(this.result);
	}

	public static JRField[] getFields(final EntityResult result) {
		final Enumeration keys = result.keys();
		final List tmp = new Vector();

		try {
			while (keys.hasMoreElements()) {
				final Object o = keys.nextElement();
				if ((o == null) || !(o instanceof String)) {
					continue;
				}

				final String name = (String) o;
				final int type = result.getColumnSQLType(name);
				final Class classClass = TypeMappingsUtils.getClass(type);
				final String className = TypeMappingsUtils.getClassName(type);

				final Map m = new Hashtable();
				m.put(CustomField.NAME_KEY, name);
				m.put(CustomField.VALUE_CLASS_NAME_KEY, className);
				m.put(CustomField.VALUE_CLASS_KEY, classClass);

				tmp.add(new CustomField(m));
			}

		} catch (final Exception ex) {
			EntityResultDataSource.logger.error(ex.getMessage(), ex);
		}

		// To array
		final int s = tmp.size();
		final CustomField[] a = new CustomField[s];
		for (int i = 0; i < s; i++) {
			final Object o = tmp.get(i);
			if ((o == null) || !(o instanceof CustomField)) {
				continue;
			}
			a[i] = (CustomField) o;
		}
		return a;
	}

}
