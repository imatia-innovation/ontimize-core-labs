package com.ontimize.gui.field;

import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ValueEvent;

/**
 * This class implements an extended data field.
 * <p>
 *
 * @author Imatia Innovation
 */
public class ObjectDataField extends DataField {

	private static final Logger logger = LoggerFactory.getLogger(ObjectDataField.class);

	/**
	 * The class constructor. Calls to super(), creates a {@link JComponent} and initializes parameters.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public ObjectDataField(final Map parameters) {
		super();
		this.dataField = new JComponent() {
		};
		this.init(parameters);
	}

	@Override
	public Object getValue() {
		return this.valueSave;
	}

	@Override
	public boolean isModified() {
		final Object oValue = this.getValue();
		if ((oValue == null) && (this.valueSave == null)) {
			return false;
		}
		if ((oValue == null) && (this.valueSave != null)) {
			if (ApplicationManager.DEBUG) {
				ObjectDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if ((oValue != null) && (this.valueSave == null)) {
			if (ApplicationManager.DEBUG) {
				ObjectDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		}
		if (!oValue.equals(this.valueSave)) {
			if (ApplicationManager.DEBUG) {
				ObjectDataField.logger.debug("Component: " + this.attribute + " Modified: Previous value = "
						+ this.valueSave + " New value = " + oValue);
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setValue(final Object value) {
		final Object oldValue = this.valueSave;
		this.valueSave = value;
		this.fireValueChanged(this.valueSave, oldValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	@Override
	public void deleteData() {
		// since 5.3.16
		this.setValue(null);
	}

	@Override
	public boolean isEmpty() {
		return this.valueSave == null;
	}

	@Override
	public int getSQLDataType() {
		return java.sql.Types.OTHER;
	}

}
