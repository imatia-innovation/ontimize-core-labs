package com.ontimize.report.engine.dynamicjasper;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class TableModelDataSource implements JRDataSource {

	private final TableModel model;

	private int index;

	private final int size;

	public TableModelDataSource(final TableModel model) {
		this.model = model;
		this.size = model.getRowCount();
		this.index = -1;
	}

	public int findColumnIndex(final String name) {
		for (int i = 0; i < this.model.getColumnCount(); i++) {
			if (this.model.getColumnName(i).equals(name)) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object getFieldValue(final JRField jrField) throws JRException {
		return this.model.getValueAt(this.index, this.findColumnIndex(jrField.getName()));
	}

	@Override
	public boolean next() throws JRException {
		this.index++;
		return this.index < this.size;
	}

	public static JRField[] getFields(final TableModel model) {
		final List tmp = new Vector();

		for (int i = 0; i < model.getColumnCount(); i++) {

			final Class classClass = model.getColumnClass(i);
			final String className = model.getColumnClass(i).getName();

			final Map m = new Hashtable();
			m.put(CustomField.NAME_KEY, model.getColumnName(i));
			m.put(CustomField.VALUE_CLASS_NAME_KEY, className);
			m.put(CustomField.VALUE_CLASS_KEY, classClass);

			tmp.add(new CustomField(m));
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
