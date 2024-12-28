package com.ontimize.gui.field;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.document.AdvancedRealDocument;
import com.ontimize.gui.field.document.RealDocument;
import com.ontimize.jee.common.gui.field.EntityFunctionAttribute;

/**
 * This class implements a data field that can make a query against a entity different to the form
 * entity <br>
 * The query is like:<br>
 * <b>select fuction(functioncolum) as attr from (select cols from entity.table where
 * parentkeys=parentkeyvalues) as vTemp</b><br>
 * It is useful to get an operation result against related data in other entity, for example:<br>
 * In a form with information about a person and his account number it is possible to use this data
 * field to calculate the current balance using all movements information.
 */
public class EntityFunctionDataField extends TextDataField {

	private static final Logger logger = LoggerFactory.getLogger(EntityFunctionDataField.class);

	protected String fieldType;

	protected String pattern;

	protected SimpleDateFormat sdf;

	protected EntityFunctionAttribute entityAttribute;

	public EntityFunctionDataField(final Map parameters) {
		super(parameters);
		this.configureFieldType(parameters);
	}

	/**
	 * This method gets the <code>Hashtable</code> and sets the field configuration.<br>
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Indicates the name of the entity to use in the query.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>function</td>
	 *        <td>max/min/sum/avg/count(*)<i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Indicates the function to execute in the query.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>functioncolumn</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>no<br>
	 *        (only if the function is about one column<br>
	 *        example max(functioncolumn)</td>
	 *        <td>Indicates the name of the column to apply the function.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>cols</td>
	 *        <td>colum1;colum2;columnN<i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the list of columns to query.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>parentkeys</td>
	 *        <td>parentkey1;parentkey2;parentkeyN<i></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Indicates the list of columns to use as filter in the query.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>type</td>
	 *        <td>text/number/date</td>
	 *        <td>number</td>
	 *        <td>no</td>
	 *        <td>Indicates the field data type (function result).<br>
	 *        If type=number it is possible to use RealDataField parameters to configure the number
	 *        format<br>
	 *        These parameters are: minintegerdigits,maxintegerdigits,mindecimaldigits
	 *        ,maxdecimaldigits,grouping</td>
	 *        </tr>
	 *        <tr>
	 *        <td>pattern</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Pattern to use for dates when type=date.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>parentkeynullvalue</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates if it is necessary to use NullValue in the query filter if some of the
	 *        filter column values are null</td>
	 *        </tr>
	 *        <tr>
	 *        <td>defaultkeyfilter</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Indicates if it is necessary to use the form entity key as a filter column in the
	 *        query</td>
	 *        </tr>
	 *        </table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		String columnName = null;
		String entityName = null;
		Vector filterColumns = null;
		String functionName = null;
		Vector queryColumns = null;

		if (parameters.containsKey("functioncolumn")) {
			columnName = (String) parameters.get("functioncolumn");
		}

		final Object entity = parameters.get("entity");
		if (entity == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				EntityFunctionDataField.logger
				.debug("Parameter 'entity' not found in EntityFunctionDataField. Check parameters.");
			}
		} else {
			entityName = entity.toString();
		}

		final Object filter = parameters.get("parentkeys");
		if (filter != null) {
			filterColumns = new Vector<>(com.ontimize.gui.ApplicationManager.getTokensAt(filter.toString(), ";"));
		}

		final Object cols = parameters.get("cols");
		if (cols != null) {
			queryColumns = new Vector<>(com.ontimize.gui.ApplicationManager.getTokensAt(cols.toString(), ";"));
		}

		final Object function = parameters.get("function");
		if (function == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				EntityFunctionDataField.logger
				.debug("Parameter 'function' not found in EntityFunctionDataField. Check parameters.");
			}
		} else {
			functionName = function.toString();
		}

		this.pattern = (String) parameters.get("pattern");

		this.entityAttribute = new EntityFunctionAttribute((String) parameters.get("attr"), entityName, columnName,
				filterColumns, functionName);

		if ("yes".equals(parameters.get("parentkeynullvalue"))) {
			this.entityAttribute.setUseNullValueToParentkeys(true);
		}

		if ("no".equals(parameters.get("defaultkeyfilter"))) {
			this.entityAttribute.setUseDefaultKeyFilter(false);
		}

		if (queryColumns != null) {
			this.entityAttribute.setQueryColumns(queryColumns);
		}

	}

	/**
	 * @param parameters
	 */
	protected void configureFieldType(final Map parameters) {
		if (parameters.containsKey("type")) {
			this.fieldType = (String) parameters.get("type");
		} else {
			this.fieldType = "number";
		}

		if (this.fieldType.equals("number")) {
			this.configureRealField(parameters);
		}
	}

	protected void configureRealField(final Map parameters) {
		((JTextField) this.dataField).setDocument(new AdvancedRealDocument());

		final Object minintegerdigits = parameters.get("minintegerdigits");
		if (minintegerdigits != null) {
			try {
				final int minimum = Integer.parseInt(minintegerdigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMinimumIntegerDigits(minimum);
			} catch (final Exception e) {
				EntityFunctionDataField.logger
				.error(this.getClass().toString() + " Error in parameter 'minintegerdigits' " + e.getMessage(), e);
			}
		}

		final Object maxintegerdigits = parameters.get("maxintegerdigits");
		if (maxintegerdigits != null) {
			try {
				final int maximum = Integer.parseInt(maxintegerdigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMaximumIntegerDigits(maximum);
			} catch (final Exception e) {
				EntityFunctionDataField.logger
				.error(this.getClass().toString() + " Error in parameter 'maxintegerdigits' " + e.getMessage(), e);
			}
		}

		final Object mindecimaldigits = parameters.get("mindecimaldigits");
		if (mindecimaldigits != null) {
			try {
				final int minimum = Integer.parseInt(mindecimaldigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMinimumFractionDigits(minimum);
			} catch (final Exception e) {
				EntityFunctionDataField.logger
				.error(this.getClass().toString() + "Error in parameter 'mindecimaldigits' " + e.getMessage(), e);
			}
		}

		final Object maxdecimaldigits = parameters.get("maxdecimaldigits");
		if (maxdecimaldigits != null) {
			try {
				final int maximum = Integer.parseInt(maxdecimaldigits.toString());
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setMaximumFractionDigits(maximum);
			} catch (final Exception e) {
				EntityFunctionDataField.logger
				.error(this.getClass().toString() + "Error in parameter 'maxdecimaldigits' " + e.getMessage(), e);
			}
		}

		final Object grouping = parameters.get("grouping");
		if (grouping != null) {
			if (grouping.toString().equals("no")) {
				final RealDocument doc = (RealDocument) ((JTextField) this.dataField).getDocument();
				doc.setGrouping(false);
			}
		}

		if (this.textAlignment == -1) {
			((JTextField) this.dataField).setHorizontalAlignment(SwingConstants.RIGHT);
		}
		if (this.dataField instanceof EJTextField) {
			((EJTextField) this.dataField).setReplaceDecimalSeparator(true);
		}
	}

	@Override
	public void setValue(Object value) {
		if (value != null) {
			if (this.fieldType.equalsIgnoreCase("number") && (value instanceof Number)) {
				final RealDocument document = (RealDocument) ((JTextField) this.dataField).getDocument();
				try {
					document.remove(0, document.getLength());
					// Format with the document formatter
					value = document.getFormat().format(value);
					document.insertString(0, (String) value, null);
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						EntityFunctionDataField.logger.debug(this.getClass().toString() + ": " + e.getMessage(), e);
					}
				}
			} else if ((value instanceof Date) && this.fieldType.equalsIgnoreCase("date") && (this.pattern != null)) {
				if (this.sdf == null) {
					this.sdf = new SimpleDateFormat(this.pattern);
				}

				value = this.sdf.format((Date) value);
			}
		}
		super.setValue(value);
	}

	@Override
	public Object getAttribute() {
		return this.entityAttribute;
	}

}
