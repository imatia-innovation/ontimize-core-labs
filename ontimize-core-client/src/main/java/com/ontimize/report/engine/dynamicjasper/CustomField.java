package com.ontimize.report.engine.dynamicjasper;

import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JRPropertiesHolder;
import net.sf.jasperreports.engine.JRPropertiesMap;
import net.sf.jasperreports.engine.JRPropertyExpression;

public class CustomField implements JRField {

	public static final String NAME_KEY = "NAME", DESCRIPTION_KEY = "DESCRIPTION", VALUE_CLASS_KEY = "VALUE_CLASS",
			VALUE_CLASS_NAME_KEY = "VALUE_CLASS_KEY",
			PROPERTIES_MAP_KEY = "PROPERTIES_MAP";

	private static final String MSG_NAME_NULL = "Parameter name must be exits";

	private static final String MSG_DESCRIPTION_DEFAULT = "CustomField -> Default description.";

	private static final String MSG_CLASS_DEFAULT = "CustomField -> Default class.";

	private static final String MSG_CLASS_NAME_DEFAULT = "CustomField -> Default class name.";

	private static final String MSG_PROPERTIES_DEFAULT = "CustomField -> Default properties map.";

	protected String name;

	protected String description;

	protected Class valueClass;

	protected String valueClassName;

	protected JRPropertiesMap propertiesMap;

	public CustomField(final java.util.Map m) throws IllegalArgumentException {
		Object o = m.get(CustomField.NAME_KEY);
		if ((o == null) || !(o instanceof String)) {
			throw new IllegalArgumentException(CustomField.MSG_NAME_NULL);
		}
		this.name = (String) o;

		o = m.get(CustomField.DESCRIPTION_KEY);
		if ((o != null) && (o instanceof String)) {
			this.description = (String) o;
		} else {
			this.description = new String();

			ReportProperty.log(CustomField.MSG_DESCRIPTION_DEFAULT);
		}

		o = m.get(CustomField.VALUE_CLASS_KEY);
		if ((o != null) && (o instanceof Class)) {
			this.valueClass = (Class) o;
		} else {
			this.valueClass = Object.class;

			ReportProperty.log(CustomField.MSG_CLASS_DEFAULT);
		}

		o = m.get(CustomField.VALUE_CLASS_NAME_KEY);
		if ((o != null) && (o instanceof String)) {
			this.valueClassName = (String) o;
		} else {
			this.valueClassName = new String();

			ReportProperty.log(CustomField.MSG_CLASS_NAME_DEFAULT);
		}

		o = m.get(CustomField.PROPERTIES_MAP_KEY);
		if ((o != null) && (o instanceof JRPropertiesMap)) {
			this.propertiesMap = (JRPropertiesMap) o;
		} else {
			this.propertiesMap = new JRPropertiesMap();

			ReportProperty.log(CustomField.MSG_PROPERTIES_DEFAULT);
		}
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Class getValueClass() {
		return this.valueClass;
	}

	@Override
	public String getValueClassName() {
		return this.valueClassName;
	}

	@Override
	public JRPropertiesHolder getParentProperties() {
		return null;
	}

	@Override
	public boolean hasProperties() {
		return false;
	}

	@Override
	public JRPropertiesMap getPropertiesMap() {
		return this.propertiesMap;
	}

	@Override
	public void setDescription(final String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append(", ");
		sb.append(this.description);
		sb.append(", ");
		sb.append(this.valueClassName);
		return sb.toString();
	}

	@Override
	public Object clone() {
		return this;
	}

	@Override
	public JRPropertyExpression[] getPropertyExpressions() {
		return new JRPropertyExpression[] {};
	}

}
