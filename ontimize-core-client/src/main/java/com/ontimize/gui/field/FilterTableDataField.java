package com.ontimize.gui.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.Operator;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.tools.Pair;

@SuppressWarnings(value = { "serial", "rawtypes", "unchecked" })
/**
 * This field allow to filter a table by a series of DataField without any configuration on the
 * Interaction Manager, only in the *.xml form file.
 *
 * @author Imatia Innovation S.L.
 *
 */
public class FilterTableDataField extends ObjectDataField implements ValueChangeListener {

	/**
	 * Logger for FilterTableDataField class.
	 */
	private static final Logger logger = LoggerFactory.getLogger(FilterTableDataField.class);

	/**
	 * Constant XML attribute.
	 */
	public static final String TABLE_REFRESH = "tablerefresh";

	/**
	 * Constant XML attribute.
	 */
	public static final String FILTERS = "filters";

	/**
	 * Constant XML attribute.
	 */
	public static final String NOT_NULL_COLUMN = "notnullcolumn";

	/**
	 * Constant name for parsing filters.
	 */
	public static final String BEXP_PREFIX = "bexp";

	/**
	 * Constant for open parentheses symbol.
	 */
	public static final String OPEN_PARANTHESES = "(";

	/**
	 * Constant for close parentheses symbol.
	 */
	public static final String CLOSE_PARANTHESES = ")";

	/**
	 * Constant for open brackets symbol.
	 */
	public static final String OPEN_BRACKETS = "[";

	/**
	 * Constant for close brackets symbol.
	 */
	public static final String CLOSE_BRACKETS = "]";

	/**
	 * Constant to indicate mixed mode symbol.
	 */
	public static final String COLON_OPEN_BRACKET = ":[";

	/**
	 * Constant for semicolon symbol.
	 */
	public static final String SEMICOLON = ";";

	/**
	 * Constant for colon symbol.
	 */
	public static final String COLON = ":";

	/**
	 * Constant for white space
	 */
	public static final String WHITE_SPACE = " ";

	/**
	 * Constant for percentage symbol.
	 */
	public static final String PERCENTAGE = "%";

	/**
	 * Constant for dollar symbol.
	 */
	public static final String DOLLAR = "$";

	/**
	 * Constant for single at symbol.
	 */
	public static final String SINGLE_AT = "@";

	/**
	 * Constant for double at symbol.
	 */
	public static final String DOUBLE_AT = "@@";

	/**
	 * Constant to indicate simple mode.
	 */
	public static final int SIMPLE_MODE = 1;

	/**
	 * Constant to indicate complex mode.
	 */
	public static final int COMPLEX_MODE = 2;

	/**
	 * Contant to indicate mixed mode.
	 */
	public static final int MIXED_MODE = 3;

	/**
	 * Variable that stores the table entity to retrieve and update the table.
	 */
	protected String tableAttr = null;

	/**
	 * Variable that stores the filter specified in the component.
	 */
	protected String filterString = null;

	/**
	 * Variable that indicates a not null table column, necessary for a complex mode filter.
	 */
	protected String notNullColumn = null;

	/**
	 * Variable used to store the filter while it is being parsed.
	 */
	protected String filterVarParsed = null;

	/**
	 * Variable used to store a map of the filters and the columns they are referenced to.
	 */
	protected LinkedHashMap filterList = null;

	/**
	 * Variable used to store a map of the filters and the binding operation between them.
	 */
	protected HashMap filterJoinList = null;

	/**
	 * Variable used to store a map of complex filters.
	 */
	protected HashMap filterComplexData = null;

	/**
	 * Variable used to store a map of mixed filters.
	 */
	protected LinkedHashMap filterMixedData = null;

	/**
	 * Variable usada para almacenar el estado del componente.
	 */
	protected int mode = 0;

	/**
	 * Variable used to store the BasicExpression
	 */
	protected BasicExpression basicExpressionFilter = null;

	/**
	 * Contructor of the component
	 * @param parameters Map with the parameters to initialize the components
	 */
	public FilterTableDataField(final Map parameters) {
		super(parameters);
		this.init(parameters);
	}

	/**
	 * Initialize the FilterTableDataField component with the parameters in *.xml form
	 * @param parameters the <code>Hashtable</code> with parameters
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</b></td>
	 *        <td><b>values</b></td>
	 *        <td><b>default</b></td>
	 *        <td><b>required</b></td>
	 *        <td><b>meaning</b></td>
	 *        </tr>
	 *        <tr>
	 *        <td>attr</td>
	 *        <td><i>String</i></td>
	 *        <td>EXPRESSION_KEY_UNIQUE_IDENTIFIER</td>
	 *        <td>no</td>
	 *        <td>The default attr value is the String in</br>
	 *        {@link SQLStatementBuilder.ExtendedSQLConditionValuesProcessor. EXPRESSION_KEY} , but can
	 *        be modified.</br>
	 *        This name will be the parent key of the table to filter</td>
	 *        </tr>
	 *        <tr>
	 *        <td>visible</td>
	 *        <td><i>no</i></td>
	 *        <td><i>no</i></td>
	 *        <td>no</td>
	 *        <td>This component always be hidden</td>
	 *        </tr>
	 *        <tr>
	 *        <td>filters</td>
	 *        <td>String</td>
	 *        <td>-</td>
	 *        <td>yes</td>
	 *        <td><strong>Simple mode:</strong> </br>
	 *        Specifies the filters for the fields in the table following this rule: </br>
	 *        <i>filter1:tablefield1;filter2:tablefield2...</i> </br>
	 *        Each pair separated by ';' and each part of a pair separed by ':'. If a <i>pair</i> has
	 *        the same name, you can use only one name:</br>
	 *        <i>filter1:tablefield1;samename2;filter3:tablefield3...</i> </br>
	 *        <strong>Complex mode:</strong> </br>
	 *        Create a complex expression:</br>
	 *        <i>(A:a AND (B:B OR ((C:c AND D:D) OR (E:e OR F:f))))</i></br>
	 *        This mode only allow pairs of values, if the pair has the same name, must be writed twice
	 *        separated by ':'. In this mode, all values of the filter must be filled to perform a valid
	 *        search</br>
	 *        <strong>Mixed mode:</strong> </br>
	 *        Create a mixed expression</br>
	 *        <i>filter1:tablefield1;‌filter2:[(@tablefield2 >= @@)];filter3:[(@tablefield3
	 *        &lt; @@)][OR];filter4;filter5:tablefield5;...</i></br>
	 *        This mode allow to define a simple relation between the filter and table column. Like in
	 *        previous modes, multiple filter separated by semicolon ';':</br>
	 *        <i>filter1</i> -> Filter data field and table column has the same name</br>
	 *        <i>filter1:tablecolumn1 </i>-> Filter data field binded with a specific column</br>
	 *        <i>filter1:[(@tablecolumn1 > @@)] </i>-> Create a complex expression. Mark the table
	 *        column with with @ symbol. Indicate the field value with double @ symbol.</br>
	 *        <i>filter1:[((@tablecolumn1 > @@) OR (@tablecolumn2 = @@))]</i> -> The complex expression
	 *        can use dor affet to multiple columns. </br>
	 *        <i>filter1:[(@tablecolumn1 > @@)][OR]</i> -> All filter are linked by AND operator. If is
	 *        need to link with other operation must be specified betweet second pair of brakets</br>
	 *        </br>
	 *        The complex expression in this mode will be always in pairs (columns and fields)</td>
	 *        </tr>
	 *        <tr>
	 *        <td>tablerefresh</td>
	 *        <td>String</td>
	 *        <td>-</td>
	 *        <td>no</td>
	 *        <td>The name of the table which performs a refresh when a filter value changes</td>
	 *        </tr>
	 *        <td>notnullcolumn</td>
	 *        <td>String</td>
	 *        <td>-</td>
	 *        <td>no</td>
	 *        <td><i>Required if use the complex filter mode.</i> The name of the column which has not
	 *        null values. e.g.: the key column of a Table. If you use the complex filter mode and no
	 *        fill this attribute, the component won't be created</td>
	 *        </tr>
	 *        </TABLE>
	 */
	@Override
	public void init(final Map parameters) {
		parameters.put("visible", "no");

		final Object oAttr = parameters.get(DataField.ATTR);
		if (oAttr != null) {
			parameters.put(DataField.ATTR, oAttr.toString());
		} else {
			parameters.put(DataField.ATTR, SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY);
		}

		super.init(parameters);

		final Object oTable = parameters.get(FilterTableDataField.TABLE_REFRESH);
		if (oTable != null) {
			this.tableAttr = oTable.toString();
		}

		final Object oNotNull = parameters.get(FilterTableDataField.NOT_NULL_COLUMN);
		if (oNotNull != null) {
			this.notNullColumn = oNotNull.toString();
		}

		final Object oFilter = parameters.get(FilterTableDataField.FILTERS);
		if (oFilter != null) {
			this.filterString = oFilter.toString();
			this.filterList = new LinkedHashMap();
			this.setFilterMode();
			this.parseFilterString();
		} else {
			FilterTableDataField.logger.error("ERROR -> \"filters\" parameter is required to create this class: {}",
					this.getClass().getName());
			throw new IllegalArgumentException();
		}
	}

	/**
	 * This method check the filter string stored in the component and parses it to establish a specific
	 * mode. If a mode cannot be checked, throws an {@link llegalArgumentException}
	 */
	protected void setFilterMode() throws IllegalArgumentException {
		final String filterString = this.getFilterString();

		if (this.checkMixedMode(filterString)) {
			this.setMode(FilterTableDataField.MIXED_MODE);
		} else if (this.checkComplexMode(filterString)) {
			this.setMode(FilterTableDataField.COMPLEX_MODE);
		} else if (this.checkSimpleMode(filterString)) {
			this.setMode(FilterTableDataField.SIMPLE_MODE);
		}
		if (this.getMode() == 0) {
			FilterTableDataField.logger.error("ERROR: -> The filter pattern \"{}\" is not recognized, please check it.",
					this.getFilterString(), this.getClass().getName());
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Check if the filter passed as parameter meets the requirements to be a simple mode filter. The
	 * requirements must be:</br>
	 *
	 * <ul>
	 * <li>Not contains parentheses ("( )")</li>
	 * <li>Not contains brackets ("[ ]")</li>
	 * <li>May content semicolon (";")</li>
	 * <li>May content colon (":")</li>
	 * </ul>
	 * @param filterExpression String with the filter to check
	 * @return <code>true</code> if the filter is a simple mode filter, <code>false</code> otherwise.
	 */
	protected boolean checkSimpleMode(final String filterExpression) {
		if (!filterExpression.contains(FilterTableDataField.OPEN_PARANTHESES)
				&& (!filterExpression.contains(FilterTableDataField.OPEN_BRACKETS))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the filter passed as parameter meets the requirements to be a complex mode filter. The
	 * requirements must be:</br>
	 *
	 * <ul>
	 * <li>Not contains semicolon (";")</li>
	 * <li>Not contains brackets ("[ ]")</li>
	 * <li>Contains parentheses ("( )")</li>
	 * </ul>
	 * @param filterExpression String with the filter to check
	 * @return <code>true</code> if the filter is a complex mode filter, <code>false</code> otherwise.
	 */
	protected boolean checkComplexMode(final String filterExpression) {
		if (!filterExpression.contains(FilterTableDataField.SEMICOLON)
				&& !filterExpression.contains(FilterTableDataField.COLON_OPEN_BRACKET) && filterExpression
				.contains(FilterTableDataField.OPEN_PARANTHESES)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the filter passed as parameter meets the requirements to be a mixed mode filter. The
	 * requirements must be: </br>
	 *
	 * <ul>
	 * <li>Contains a colon and a open braket (":[")</li>
	 * </ul>
	 * @param filterExpression String with the filter to check
	 * @return <code>true</code> if the filter is a mixed mode filter, <code>false</code> otherwise.
	 */
	protected boolean checkMixedMode(final String filterExpression) {
		if (filterExpression.contains(FilterTableDataField.COLON_OPEN_BRACKET)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check the filter mode and parse the filter string, according to its mode.
	 */
	protected void parseFilterString() {
		if (this.getMode() == FilterTableDataField.SIMPLE_MODE) {
			this.parseFilterStringSimple();
		} else if (this.getMode() == FilterTableDataField.COMPLEX_MODE) {
			this.parseFilterStringComplex();
		} else if (this.getMode() == FilterTableDataField.MIXED_MODE) {
			this.parseFilterStringMixed();
		}
	}

	/**
	 * Analyzes the filter string, with the simple mode syntax, analyzing each element (separated by
	 * semicolon), and stores the relationship between the filter and the table column.
	 */
	protected void parseFilterStringSimple() {
		for (final String pairFields : this.getFilterString().split(FilterTableDataField.SEMICOLON)) {
			if (pairFields.contains(FilterTableDataField.COLON)) {
				final String[] fields = pairFields.split(FilterTableDataField.COLON);
				this.filterList.put(fields[0], fields[1]);
			} else {
				this.filterList.put(pairFields, pairFields);
			}
		}

	}

	/**
	 * Analyze the filter string, with the complex mode syntax. It first checks if the component
	 * contains the name of a column that does not contain null elements of the table. If it doesn't
	 * have this parameter, launch a {@link IllegalArrgumentException}. Then, it extracts the
	 * information about the filters and the columns to which they refer (separated in pairs by the
	 * colon symbol). It modifies the filter string to be analyzed, which now contains placeholders to
	 * evaluate the complex expression.
	 *
	 * @see parseStrignFilter
	 */
	protected void parseFilterStringComplex() {

		if (this.getNotNullColumn() == null) {
			FilterTableDataField.logger.error(
					"ERROR -> \"notnullcolumn\" parameter is required to create this class in complex mode: {}",
					this.getClass().getName());
			throw new IllegalArgumentException();
		}

		String extractingString = this.getFilterString();
		do {
			extractingString = this.extractFiltersFromFilterString(extractingString);
		} while (extractingString.contains(FilterTableDataField.COLON));

		this.filterString = this.parseStrignFilter(this.getFilterString());
	}

	/**
	 * Returns a text string, substituting the filter - field element pairs
	 * (<i>filter1:tablecolumn1</i>) of the filter string to be analyzed, by the name of the field
	 * between percentage symbols (<i>%tablecolumn1%</i>), as long as the filter string has been
	 * previously parsed, storing the information about the filters and the table fields.
	 * @param sFilters The filter text to parse
	 * @return The text string parsed
	 */
	protected String parseStrignFilter(String sFilters) {
		final Set entrySet = this.getFilterMap().entrySet();
		final Iterator itr = entrySet.iterator();
		while (itr.hasNext()) {
			final Entry e = (Entry) itr.next();
			final StringBuilder builder = new StringBuilder();
			builder.append(e.getKey());
			builder.append(FilterTableDataField.COLON);
			builder.append(e.getValue());

			final StringBuilder builderValue = new StringBuilder();
			builderValue.append(FilterTableDataField.PERCENTAGE);
			builderValue.append(e.getValue());
			builderValue.append(FilterTableDataField.PERCENTAGE);

			sFilters = sFilters.replace(builder.toString(), builderValue.toString());

		}
		return sFilters;
	}

	/**
	 * From the filter string sent by parameters, it is checked if it exists as a colon symbol. If it
	 * does not exist, the string sent by parameters is returned. If it exists, it is delimited which
	 * part is the filter (left) or field of the table (right). The pair of elements are stored and
	 * removed from the filter chain. The string is then returned without the analyzed element
	 * @param filterString Filter string to extract the pair elements filter and table field
	 * @return The filter chain without the pair elements
	 */
	protected String extractFiltersFromFilterString(final String filterString) {
		String toRet = filterString;
		final int indexColon = toRet.indexOf(FilterTableDataField.COLON);
		if (indexColon > -1) {
			String auxString = filterString.substring(indexColon, filterString.length());
			final int endFilter = this.endIndex(auxString, FilterTableDataField.WHITE_SPACE,
					FilterTableDataField.CLOSE_PARANTHESES);
			auxString = filterString.substring(0, endFilter + indexColon);
			final int startFilter = this.startIndex(auxString, FilterTableDataField.WHITE_SPACE,
					FilterTableDataField.OPEN_PARANTHESES);
			final String filterPair = toRet.substring(startFilter, endFilter + indexColon).trim();
			final String[] filterRelation = filterPair.split(FilterTableDataField.COLON);
			this.getFilterMap().put(filterRelation[0].toString(), filterRelation[1].toString());
			toRet = toRet.replace(auxString, "");
		} else {
			return "";
		}

		return toRet;
	}

	/**
	 * Returns the index of the first element found, a blank space or a closed parenthesis, in the
	 * string sent as parameter
	 * @param auxString Filter string to find the symbol
	 * @param whiteSpace Symbol of a white space
	 * @param closedParentheses Symbol of a closed parenthesis
	 * @return The index where the first blank space or the first closed parenthesis appears
	 */
	protected int endIndex(final String auxString, final String whiteSpace, final String closedParentheses) {
		int endWhiteSpaceIndex = 0 > auxString.indexOf(whiteSpace) ? 0 : auxString.indexOf(whiteSpace);
		int endParenthesesIndex = 0 > auxString.indexOf(closedParentheses) ? 0 : auxString.indexOf(closedParentheses);

		if (endWhiteSpaceIndex == 0) {
			endWhiteSpaceIndex = endParenthesesIndex;
		} else if (endParenthesesIndex == 0) {
			endParenthesesIndex = endWhiteSpaceIndex;
		}

		return endWhiteSpaceIndex < endParenthesesIndex ? endWhiteSpaceIndex : endParenthesesIndex;
	}

	/**
	 * Returns the index of the last element found, a blank space or a open parenthesis, in the string
	 * sent as parameter
	 * @param auxString Filter string to find the symbol
	 * @param whiteSpace Symbol of a white space
	 * @param closedParentheses Symbol of a open parenthesis
	 * @return The index where the last blank space or the last open parenthesis appears
	 */
	protected int startIndex(final String auxString, final String whiteSpace, final String openParentheses) {
		final int startWhiteSpaceIndex = 0 > auxString.lastIndexOf(whiteSpace) ? 0 : auxString.lastIndexOf(whiteSpace) + 1;
		final int startParenthesesIndex = 0 > auxString.lastIndexOf(openParentheses) ? 0
				: auxString.lastIndexOf(openParentheses) + 1;

		return startWhiteSpaceIndex > startParenthesesIndex ? startWhiteSpaceIndex : startParenthesesIndex;
	}

	/**
	 * Recovers the filter saved by the component, and separates it in each semicolon symbol. For each
	 * substring, it analyzes by treating it as a subchain of a mixed filter
	 */
	protected void parseFilterStringMixed() {
		for (final String mixedFilter : this.getFilterString().split(FilterTableDataField.SEMICOLON)) {
			this.parsedMixedFilter(mixedFilter);
		}

	}

	/**
	 * Analyzes the substring belonging to a substring filter, to store the relationship between the
	 * filter and the table column or the complex expression to which it is associated.
	 * @param mixedFilter Substring of a mixed filter
	 */
	protected void parsedMixedFilter(final String mixedFilter) {

		final int firstIndex = mixedFilter.indexOf(FilterTableDataField.OPEN_BRACKETS);
		final int lastIndex = mixedFilter.lastIndexOf(FilterTableDataField.OPEN_BRACKETS);
		final int lastCloseIndex = mixedFilter.lastIndexOf(FilterTableDataField.CLOSE_BRACKETS);
		final boolean colon = mixedFilter.contains(FilterTableDataField.COLON);

		if ((firstIndex > -1) && (lastIndex > -1)) {
			final String value = mixedFilter.substring(firstIndex, lastCloseIndex + 1);
			final String[] field = mixedFilter.trim().split(FilterTableDataField.COLON);
			this.filterList.put(field[0], value);
		} else if (colon) {
			final String[] fields = mixedFilter.trim().split(FilterTableDataField.COLON);
			this.filterList.put(fields[0], fields[1]);
		} else if (!colon) {
			this.filterList.put(mixedFilter.trim(), mixedFilter.trim());
		}
	}

	/**
	 * Returns the entity name of the table associated to this filter
	 * @return the entity string
	 */
	public String getTableAttr() {
		return this.tableAttr;
	}

	/**
	 * Returns the mode of the component, which matches the filter type.
	 *
	 * <ul>
	 * <li>Simple mode: 1 (<i>FilterTableDataField.SIMPLE_MODE</i>)</li>
	 * <li>Complex mode: 2 (<i>FilterTableDataField.COMPLEX_MODE</i>)</li>
	 * <li>Mixed mode: 3 (<i>FilterTableDataField.MIXED_MODE</i>)</li>
	 * </ul>
	 * @return An int which represents the mode of the component
	 */
	public int getMode() {
		return this.mode;
	}

	/**
	 * Set the mode of the component
	 *
	 * <ul>
	 * <li>Simple mode: 1 (<i>FilterTableDataField.SIMPLE_MODE</i>)</li>
	 * <li>Complex mode: 2 (<i>FilterTableDataField.COMPLEX_MODE</i>)</li>
	 * <li>Mixed mode: 3 (<i>FilterTableDataField.MIXED_MODE</i>)</li>
	 * </ul>
	 * @param mode An int which represent the mode of the component
	 */
	protected void setMode(final int mode) {
		this.mode = mode;
	}

	/**
	 * Returns the string of the filter parsed.
	 * @return The string filter parsed
	 */
	public String getFilterString() {
		return this.filterString;
	}

	/**
	 * Returns the string of the filter parsed, used as temporal variable.
	 * @return The string filter parsed, used as temporal variable.
	 */
	public String getFilterVarParsed() {
		return this.filterVarParsed;
	}

	/**
	 * Return a LinkedHashMap with the binding between the filter and the columns in the table after
	 * parsed the filter string
	 * @return A LinkedHashMap with the binding between the filter and the columns in the table
	 */
	public LinkedHashMap getFilterMap() {
		return this.filterList;
	}

	/**
	 * Returns the LinkedHashMap containing the link between the filter and the corresponding expression
	 * in mixed mode
	 * @return A LinkedHashMap containing the link between the filter and the corresponding expression
	 *         in mixed mode
	 */
	public LinkedHashMap getFilterMixedData() {
		return this.filterMixedData;
	}

	/**
	 * Returns the name of the not null column of the table associated with the filter
	 * @return An String which represents the name of the not null column of the table associated with
	 *         the filter
	 */
	public String getNotNullColumn() {
		return this.notNullColumn;
	}

	/**
	 * Returns the LinkedHashMap containing the link between the filter and the corresponding expression
	 * in complex mode
	 * @return A LinkedHashMap containing the link between the filter and the corresponding expression
	 *         in complex mode
	 */
	public HashMap getFilterDataComplex() {
		return this.filterComplexData;
	}

	/**
	 * Returns the map containing the filters binding to the other expressions
	 * @return A map containing the filters binding to the other expressions
	 */
	public HashMap getFilterJoinList() {
		return this.filterJoinList;
	}

	/**
	 * Sets the parent form of the component
	 * @param f Form to set as parent
	 */
	@Override
	public void setParentForm(final Form f) {
		super.setParentForm(f);
		this.addChangeListeners(this.getFilterMap());
	}

	/**
	 * Returns the formulatio component which attribute matches the one passed as parameter
	 * @param attr Attribute name of the DataField filter
	 * @return a FormComponent
	 */
	public FormComponent getFormComponent(final String attr) {
		try {
			return this.getParentForm().getElementReference(attr);
		} catch (final Exception e) {
			FilterTableDataField.logger.trace(null, e);
			return null;
		}
	}

	/**
	 * Adds a listener to filter fields to update the expression when its value is changed
	 * @param fieldList The map containing the name of the filters and the names of the associated
	 *        columns. Fields are the keys to this map
	 */
	protected void addChangeListeners(final LinkedHashMap fieldList) {
		final Set s = fieldList.keySet();
		final Iterator itr = s.iterator();
		while (itr.hasNext()) {
			final String actualKey = itr.next().toString();
			final DataField filterDataField = (DataField) this.getFormComponent(actualKey.toString());
			if (filterDataField != null) {
				filterDataField.addValueChangeListener(this);
			} else {
				itr.remove();
				FilterTableDataField.logger.error("Cannot retrieve a DataField which attr is : '{}'. Check 'attr' name",
						actualKey.toString());
			}
		}
	}

	/**
	 * When a filter emit a value change event, update the basic expression , and if the component has
	 * the entity name of a table, performs a refresh of the table
	 * @param e Event of value change
	 */
	@Override
	public void valueChanged(final ValueEvent e) {
		this.setFilterExpression();
		if (this.getTableAttr() != null) {
			final Table tableFilter = (Table) this.getFormComponent(this.getTableAttr());
			if (tableFilter != null) {
				tableFilter.refreshEDT();
			}
		}
	}

	/**
	 * The value of the component that this component returns is the basic expression created by the
	 * filters
	 */
	@Override
	public Object getValue() {
		return this.basicExpressionFilter;
	}

	/**
	 * Allows to update the regular expression based on the type of filter.
	 */
	public void setFilterExpression() {
		if (this.getMode() == FilterTableDataField.SIMPLE_MODE) {
			this.setFilterExpressionSimple();
		} else if (this.getMode() == FilterTableDataField.COMPLEX_MODE) {
			this.setFilterExpressionComplex();
		} else if (this.getMode() == FilterTableDataField.MIXED_MODE) {
			this.setFilterExpressionMixed();
		}
	}

	protected void setFilterExpressionSimple() {
		final List<BasicExpression> basicExpList = new ArrayList<BasicExpression>();
		for (final Object oActualKey : this.getFilterMap().keySet()) {
			final String field = (String) oActualKey;
			final String column = this.getFilterMap().get(field).toString();
			final DataField dF = (DataField) this.getFormComponent(field);
			if (dF != null) {
				final Object value = dF.getValue();
				final boolean addCheck = this.addToQueryCheckField(dF);
				if ((value != null) && addCheck) {
					basicExpList.add(this.createBasicExpression(column, value, dF));
				}

			} else {
				continue;
			}
		}

		BasicExpression fullBasicExpression = null;
		if (!basicExpList.isEmpty()) {
			fullBasicExpression = this.getFullBasicExpressionSimple(basicExpList);
		}

		this.basicExpressionFilter = fullBasicExpression;

	}

	/**
	 * Field to check if is a CheckDataField and it is included in the search
	 * @param df Field to check
	 * @return If is not a {@link CheckDataField} returns <code>true</code>, otherwise <code>true</code>
	 *         or <code>false</code> depending if is included in the search or not
	 */
	protected boolean addToQueryCheckField(final DataField df) {
		if (df instanceof CheckDataField) {
			final CheckDataField ck = (CheckDataField) df;
			return ck.isIncluded();
		}
		return true;
	}

	/**
	 * Build a BasicExpression using the BasicExpression of each of the filters
	 * @param basicExpressionList List of BasicExpression elements for simple mode
	 * @return A BasicExpression built together with the other BasicExpression of the other filters
	 */
	protected BasicExpression getFullBasicExpressionSimple(final List basicExpressionList) {
		BasicExpression fullExpression = null;

		// If there is just one value to filter, it have to return only one
		// basicExpression
		if (basicExpressionList.size() == 1) {
			return (BasicExpression) basicExpressionList.get(0);
		}

		// If there are more than one value, it is necessary to build an unique
		// BasicExpression (They are join with "AND" by default)

		// BasicExpression of the first element.
		fullExpression = (BasicExpression) basicExpressionList.get(0);
		for (int i = 1; i < basicExpressionList.size(); i++) {
			final BasicExpression be = (BasicExpression) basicExpressionList.get(i);
			if (fullExpression != null) {
				fullExpression = new BasicExpression(fullExpression, BasicOperator.AND_OP, be);
			}
		}
		return fullExpression;
	}

	/**
	 * Build and set up a BasicExpression using a complex filter. If any filter has no value, it is used
	 * as a basic expression with the column not null. If all filters are not null, the evaluation of
	 * the deeper atomic expressions is evaluated in {@link getClosestOperation}.
	 */
	protected void setFilterExpressionComplex() {
		try {
			this.filterComplexData = new HashMap();
			this.filterVarParsed = this.getFilterString();
			Pair operationFinish = null;
			final boolean finish = false;
			BasicExpression bexp = null;
			int i = 1;
			do {
				operationFinish = this.getClosestOperation(i, this.getFilterVarParsed());
				i++;
				bexp = (BasicExpression) operationFinish.getSecond();
				if (bexp != null) {
					this.basicExpressionFilter = bexp;
				}

			} while (!((Boolean) operationFinish.getFirst()));

		} catch (final FilterNotFilledException e) {
			FilterTableDataField.logger.debug("filter is not filled with data.", e);
			this.basicExpressionFilter = new BasicExpression(new BasicField(this.getNotNullColumn()),
					BasicOperator.NOT_NULL_OP, null);
		} catch (final Exception e) {
			this.basicExpressionFilter = new BasicExpression(new BasicField(this.getNotNullColumn()),
					BasicOperator.NOT_NULL_OP, null);
			FilterTableDataField.logger.debug("Error ocurred parsing.", e);
		}

	}

	/**
	 * Get the first deeper expression and retrieves a Pair composed by a boolean and a BasicExpression.
	 * If the boolean is <code>false</code>, a BasicExpression is retrieved in the second field (and the
	 * filter is not fully parsed). If the boolean is true, the second field is null, and the filter is
	 * fully parsed.
	 *
	 * If the filter string has not a close parentheses, returns a pair with a boolean as
	 * <code>true</code>. If has an close parentheses, find the respective open parentheses. Try to
	 * delete the first and last percentage symbol (if exist). Splits the operation with a percentage
	 * symbol. If the operation is divided in three parts, calls to {@link createClosestExpression} to
	 * create the BasicExpression of this operation.
	 * @param i An integer to passed to {@link createClosestExpression}
	 * @param filterToParser An filter string to parse
	 * @return A Pair consisting on a boolean and a BasicExpression
	 * @throws Exception
	 */
	protected Pair<Boolean, BasicExpression> getClosestOperation(final int i, final String filterToParser) throws Exception {
		final int closeIndex = filterToParser.indexOf(FilterTableDataField.CLOSE_PARANTHESES);
		if (closeIndex > -1) {
			final String auxString = this.getFilterVarParsed().substring(0, closeIndex);
			final int openIndex = auxString.lastIndexOf(FilterTableDataField.OPEN_PARANTHESES) + 1;
			final String operation = this.getFilterVarParsed().substring(openIndex, closeIndex);
			final StringBuilder builderOp = new StringBuilder(operation);
			final int firstPercentage = builderOp.indexOf(FilterTableDataField.PERCENTAGE);
			if (firstPercentage >= 0) {
				builderOp.deleteCharAt(firstPercentage);
			}
			final int lastPercentage = builderOp.lastIndexOf(FilterTableDataField.PERCENTAGE);
			if (lastPercentage >= 0) {
				builderOp.deleteCharAt(lastPercentage);
			}
			final String[] filterRelation = builderOp.toString().split(FilterTableDataField.PERCENTAGE);
			if (filterRelation.length > 3) {
				final int prevIndex = filterToParser.indexOf(filterRelation[2]) + filterRelation[2].length();
				final String filterStringAux = filterToParser.substring(prevIndex);
				final int indexToPivot = filterStringAux.indexOf(filterRelation[3]) + prevIndex;
				final BasicExpression bexp = this.createClosestExpression(filterRelation[0],
						new BasicOperator(filterRelation[1]), filterRelation[2], openIndex, indexToPivot, i,
						filterToParser);
				return new Pair<Boolean, BasicExpression>(false, bexp);
			} else if (filterRelation.length == 3) {
				final BasicExpression bexp = this.createClosestExpression(filterRelation[0],
						new BasicOperator(filterRelation[1]), filterRelation[2], openIndex - 1, closeIndex + 1, i,
						filterToParser);
				return new Pair<Boolean, BasicExpression>(false, bexp);
			} else if (filterRelation.length == 1) {
				final BasicExpression bexp = this.createClosestExpression(filterRelation[0], openIndex - 1, closeIndex + 1, i,
						filterToParser);
				return new Pair<Boolean, BasicExpression>(false, bexp);
			}
		}
		return new Pair<Boolean, BasicExpression>(true, null);
	}

	/**
	 *
	 * Creates a BasicExpression with the parameters that receives. First, checks if
	 * <code>firstField</code> and <code>secondField</code> is a BasicExpression or a DataField. Then,
	 * retrieves de values of this elements. If is null, throws a {@link FilterNotFilledException}.
	 * Creates a BasicExpression for first and second field, retrieving the value for the field. Creates
	 * a BasicExpression with both values and joined by the operation passed as parameter. Stores this
	 * value in a map that has as key a String "bexp" and concatenate to <code>int i</code> surrounded
	 * by percentage symbols (for parsed in {@link getClosestOperation} method). Then the filter string
	 * replaces itself the operation for the key in the map and return the BasicExpression
	 * @param firstField name of the first filter field
	 * @param operator operator between first and second filter
	 * @param secondField name of second filter field
	 * @param openIndex index of open parentheses
	 * @param closeIndex index of closed parentheses
	 * @param i integer to create a key to save an BasicExpression for every iteration
	 * @param filterToParser filter string to replace de filter and change for expression and set for
	 *        parsed in the next iteration
	 * @return BasicExpression create with the params passed as fields and operator
	 * @throws Exception
	 */
	protected BasicExpression createClosestExpression(final String firstField, final Operator operator, final String secondField,
			final int openIndex, final int closeIndex, final int i, final String filterToParser)
					throws Exception {
		final Object field1 = this.checkExpressionValueOrDataField(firstField);
		final Object field2 = this.checkExpressionValueOrDataField(secondField);
		Object value1 = field1 instanceof BasicExpression ? value1 = field1 : ((DataField) field1).getValue();
		Object value2 = field2 instanceof BasicExpression ? value2 = field2 : ((DataField) field2).getValue();
		if ((value1 == null) || (value2 == null)) {
			throw new FilterNotFilledException("M_FILTER_VALUES_NOT_FILLED");
		}
		final StringBuilder builder = new StringBuilder();
		builder.append(FilterTableDataField.PERCENTAGE);
		builder.append(FilterTableDataField.BEXP_PREFIX);
		builder.append(i);
		builder.append(FilterTableDataField.PERCENTAGE);

		final BasicExpression bexp1 = this.createBasicExpression(firstField, value1,
				(DataField) this
				.getFormComponent((String) FilterTableDataField.getKeyByValue(this.getFilterMap(), firstField)));
		final BasicExpression bexp2 = this.createBasicExpression(secondField, value2,
				(DataField) this
				.getFormComponent((String) FilterTableDataField.getKeyByValue(this.getFilterMap(), secondField)));

		final BasicExpression bexpFinal = new BasicExpression(bexp1, operator, bexp2);

		this.filterComplexData.put(builder.toString(), bexpFinal);

		final String actualFilterExp = filterToParser.substring(openIndex, closeIndex);
		this.filterVarParsed = filterToParser.replace(actualFilterExp, builder.toString());
		return bexpFinal;

	}

	/**
	 *
	 * Creates a BasicExpression with the parameters that receives. First, checks if
	 * <code>firstField</code> is a BasicExpression or a DataField. Then, retrieves de value of this
	 * element. If is null, throws a {@link FilterNotFilledException}. Creates a BasicExpression for
	 * first field, retrieving the value for the field. Creates a BasicExpression with the value. Stores
	 * this value in a map that has as key a String "bexp" and concatenate to <code>int i</code>
	 * surrounded by percentage symbols (for parsed in {@link getClosestOperation} method). Then the
	 * filter string replaces itself the operation for the key in the map and return the BasicExpression
	 * @param firstField name of the first filter field
	 * @param openIndex index of open parentheses
	 * @param closeIndex index of closed parentheses
	 * @param i integer to create a key to save an BasicExpression for every iteration
	 * @param filterToParser filter string to replace de filter and change for expression and set for
	 *        parsed in the next iteration
	 * @return BasicExpression create with the params passed as fields and operator
	 * @throws Exception
	 */
	protected BasicExpression createClosestExpression(final String firstField, final int openIndex, final int closeIndex, final int i,
			final String filterToParser) throws Exception {
		final Object field1 = this.checkExpressionValueOrDataField(firstField);
		Object value1 = field1 instanceof BasicExpression ? value1 = field1 : ((DataField) field1).getValue();
		if (value1 == null) {
			throw new FilterNotFilledException("M_FILTER_VALUES_NOT_FILLED");
		}
		final StringBuilder builder = new StringBuilder();
		builder.append(FilterTableDataField.PERCENTAGE);
		builder.append(FilterTableDataField.BEXP_PREFIX);
		builder.append(i);
		builder.append(FilterTableDataField.PERCENTAGE);

		final BasicExpression bexp1 = this.createBasicExpression(firstField, value1,
				(DataField) this
				.getFormComponent((String) FilterTableDataField.getKeyByValue(this.getFilterMap(), firstField)));

		final BasicExpression bexpFinal = bexp1;

		this.filterComplexData.put(builder.toString(), bexpFinal);

		final String actualFilterExp = filterToParser.substring(openIndex, closeIndex);
		this.filterVarParsed = filterToParser.replace(actualFilterExp, builder.toString());
		return bexpFinal;

	}

	/**
	 * Checks if the string passed as parameter is a BasicExpression created in previous iterations and
	 * stored in a map or the parsed name that is binding to a DataField
	 * @param name Key of the basic expression or Datafield
	 * @return A BasicExpression or a DataField
	 */
	protected Object checkExpressionValueOrDataField(final String name) {
		final StringBuilder builder = new StringBuilder();
		builder.append(FilterTableDataField.PERCENTAGE);
		builder.append(name);
		builder.append(FilterTableDataField.PERCENTAGE);

		if (this.getFilterDataComplex().containsKey(builder.toString())) {
			return this.getFilterDataComplex().remove(builder.toString());
		} else {
			return this.getFormComponent((String) FilterTableDataField.getKeyByValue(this.getFilterMap(), name));
		}

	}

	/**
	 * Set the BasicExpression from a mixed filter. Search in the map which contains the bindindgs
	 * between filters and expressions of table columns. Obtain the attribute of the filter a check if
	 * exists in the form and if it has value. If it has value, parses the expression, accordinly if it
	 * has simple or complex. Adds the filter expression to a map, and check its join operation if its
	 * type is complex. Then, joins all individual BasicExpression in an unique BasicExpression and
	 * establishes it as final BasicExpression
	 */
	protected void setFilterExpressionMixed() {
		final LinkedHashMap<BasicExpression, BasicOperator> basicExpMap = new LinkedHashMap<BasicExpression, BasicOperator>();
		this.filterJoinList = new HashMap();
		this.filterMixedData = new LinkedHashMap();
		for (final Object oActualKey : this.getFilterMap().keySet()) {
			final String field = (String) oActualKey;
			final DataField dF = (DataField) this.getFormComponent(field);
			final boolean addCheck = this.addToQueryCheckField(dF);
			if ((dF.getValue() != null) && addCheck) {
				String expString = (String) this.getFilterMap().get(oActualKey);
				Pair operationFinish = null;
				BasicExpression bexp = null;
				int i = 1;
				expString = this.checkOperationJoin(field, expString);

				if (this.checkSimpleMode(expString)) {
					final DataField df = (DataField) this.getFormComponent(field);
					bexp = this.createBasicExpression(expString, df.getValue(), df);
				}

				if (bexp == null) {
					this.filterVarParsed = expString;
					do {
						operationFinish = this.getMixedClosestOperation(i, field, this.getFilterVarParsed());
						i++;
						if (operationFinish.getSecond() != null) {
							bexp = (BasicExpression) operationFinish.getSecond();
						}
					} while (!((Boolean) operationFinish.getFirst()));
				}

				if (this.getFilterJoinList().containsKey(field)) {
					basicExpMap.put(bexp, (BasicOperator) this.getFilterJoinList().get(field));
				} else {
					basicExpMap.put(bexp, (BasicOperator) BasicOperator.AND_OP);
				}

			} else {
				continue;
			}

		}

		BasicExpression fullBasicExpression = null;
		if (!basicExpMap.isEmpty()) {
			fullBasicExpression = this.getFullBasicExpressionMixed(basicExpMap);
		}

		this.basicExpressionFilter = fullBasicExpression;

	}

	/**
	 * Creates a unique BasicExpression formed by all BasicExpression cotained in the map passed as
	 * parameter, with its union operations between BasicExpressions
	 * @param basicExpressionMap A map which contains a BasicExpression as key and its join operation as
	 *        value with the previous one.
	 * @return A BasicExpression formed by all BasicExpression in the map passed as parameter.
	 */
	protected BasicExpression getFullBasicExpressionMixed(
			final LinkedHashMap<BasicExpression, BasicOperator> basicExpressionMap) {
		BasicExpression fullExpression = null;

		if (!basicExpressionMap.isEmpty()) {
			final Iterator itr = basicExpressionMap.entrySet().iterator();
			final Map.Entry entry = (Entry) itr.next();
			if (basicExpressionMap.size() == 1) {
				return (BasicExpression) entry.getKey();
			}

			fullExpression = (BasicExpression) entry.getKey();

			while (itr.hasNext()) {
				final Map.Entry actualEntry = (Entry) itr.next();
				fullExpression = new BasicExpression(fullExpression, (BasicOperator) actualEntry.getValue(),
						actualEntry.getKey());
			}
		}

		return fullExpression;
	}

	/**
	 * Evaluate the deeper operation in the mixed filter. If this method returns a new pair of a boolean
	 * (as true) and a null, implies than the expression for the filter is fully parsed. If returns a
	 * pair with a boolean (as false) and a BasicExpression, implies than the expression for the filter
	 * is partially parsed, and the BasicExpression is a portion of that. If the value has not a close
	 * parentheses, implies than the expression is fully parsed. If has close parentheses, recover the
	 * text in the enclosed parentheses. Tries to delete the first and las percentage symbol, and split
	 * the operation by the percentage symbol, creating the BasicExpression of the enclosed text and
	 * returning a pair of boolean (as false) and the BasicExpression created.
	 * @param i An integer that use to be pass as parameter a numeric for every iteration while the
	 *        expression its parsed.
	 * @param key The name of the field's attribute used to obtain its value in the mixed expressions.
	 * @param value The mixed filter for its field
	 * @return A pair of elements (boolean and BasicExpression) for every iteration
	 */
	protected Pair<Boolean, BasicExpression> getMixedClosestOperation(final int i, final String key, final String value) {

		final int closeIndex = value.indexOf(FilterTableDataField.CLOSE_PARANTHESES);
		if (closeIndex > -1) {
			final String auxString = value.substring(0, closeIndex);
			final int openIndex = auxString.lastIndexOf(FilterTableDataField.OPEN_PARANTHESES) + 1;
			String operation = value.substring(openIndex, closeIndex);
			final StringBuilder builderOp = new StringBuilder(operation);
			operation = value.substring(openIndex - 1, closeIndex + 1);
			final int firstPercentage = builderOp.indexOf(FilterTableDataField.PERCENTAGE);
			if (firstPercentage >= 0) {
				builderOp.deleteCharAt(firstPercentage);
			}
			final int lastPercentage = builderOp.lastIndexOf(FilterTableDataField.PERCENTAGE);
			if (lastPercentage >= 0) {
				builderOp.deleteCharAt(lastPercentage);
			}
			final String[] filterRelation = builderOp.toString().split(FilterTableDataField.PERCENTAGE);
			final BasicExpression bexp = this.createClosestExpression(filterRelation, key, operation, value, i);
			return new Pair<Boolean, BasicExpression>(false, bexp);
		}
		return new Pair(true, null);
	}

	/**
	 * A BasicExpression created with the parts of the operation. Check if the String array contais 1 or
	 * 3 elements. If contains 1, implies that not contains a BasicExpression created preiously. Parses
	 * the expression and creates a BasicExpression, saving it in a map with an unique name surrounded
	 * by percentage symbols. If contains 3 elements, creates the BasicExpression with the stored
	 * BasicExpression
	 * @param filterRelation The components of the filter operation
	 * @param key The name of the filter's attribute
	 * @param operation The operation to parse
	 * @param value The filter string to parsed in multiple iterations
	 * @param i The integer to create an unique name in multiple iterations
	 * @return A BasicExpression created with the parts of the operation
	 */
	protected BasicExpression createClosestExpression(final String[] filterRelation, final String key, final String operation,
			final String value, final int i) {
		BasicExpression toRet = null;

		final StringBuilder builderBexp = new StringBuilder();
		builderBexp.append(FilterTableDataField.BEXP_PREFIX);
		builderBexp.append(i);

		final StringBuilder builder = new StringBuilder();
		builder.append(FilterTableDataField.PERCENTAGE);
		builder.append(builderBexp.toString());
		builder.append(FilterTableDataField.PERCENTAGE);

		if (filterRelation.length == 3) {
			toRet = new BasicExpression(this.getFilterMixedData().remove(filterRelation[0]),
					new BasicOperator(filterRelation[1]),
					this.getFilterMixedData().remove(filterRelation[2]));
			this.filterVarParsed = value.replace(operation, builder.toString());
			this.filterMixedData.put(builderBexp.toString(), toRet);

		} else if (filterRelation.length == 1) {
			String columnTable = null;
			String filterString = filterRelation[0];
			Object filterValue = ((DataField) this.getFormComponent(key)).getValue();
			if ((filterValue instanceof SearchValue) && (((SearchValue) filterValue).getCondition() == 2)) {
				filterValue = ((SearchValue) filterValue).getValue();
			}
			filterString = filterString.replaceAll(FilterTableDataField.DOUBLE_AT, "");
			final int indexAt = filterString.indexOf(FilterTableDataField.SINGLE_AT);

			final String filterFromAt = filterString.substring(indexAt);
			int indexWhite = filterFromAt.indexOf(FilterTableDataField.WHITE_SPACE);
			if (indexWhite < 0) {
				indexWhite = filterString.length();
			}

			final String column = filterString.substring(indexAt, indexWhite);
			columnTable = column.substring(1);
			filterString = filterString.replaceAll(column, "");
			final BasicOperator op = new BasicOperator(filterString);
			toRet = new BasicExpression(new BasicField(columnTable), op, filterValue);

			this.filterVarParsed = value.replace(operation, builder.toString());

			this.filterMixedData.put(builderBexp.toString(), toRet);

		}
		return toRet;
	}

	/**
	 * Parses the join operator between filters an return its String representation
	 * @param key Filter's field's attribute
	 * @param value The expression to parse with join operation
	 * @return The string of the operation
	 */
	protected String checkOperationJoin(final String key, final String value) {
		final int lastCloseBracket = value.lastIndexOf(FilterTableDataField.CLOSE_BRACKETS);
		final int firstCloseBracket = value.indexOf(FilterTableDataField.CLOSE_BRACKETS);
		if (lastCloseBracket == firstCloseBracket) {
			if (lastCloseBracket < 0) {
				return value;
			} else {
				final int firstOpenBracket = value.indexOf(FilterTableDataField.OPEN_BRACKETS) + 1;
				return value.substring(firstOpenBracket, firstCloseBracket);
			}
		} else {
			final int lastOpenBracket = value.lastIndexOf(FilterTableDataField.OPEN_BRACKETS);
			final String operation = value.substring(lastOpenBracket + 1, lastCloseBracket);
			final StringBuilder builder = new StringBuilder();
			builder.append(" ");
			builder.append(operation);
			builder.append(" ");
			this.saveOperationJoin(key, operation.toString());
			final int firstOpenBracket = value.indexOf(FilterTableDataField.OPEN_BRACKETS) + 1;
			return value.substring(firstOpenBracket, firstCloseBracket);
		}

	}

	/**
	 * Saves the join operator between filters in a map
	 * @param field Filter field attribute
	 * @param operation String of the join operation
	 */
	protected void saveOperationJoin(final String field, final String operation) {
		this.filterJoinList.put(field, new BasicOperator(operation));
	}

	/**
	 * Creates a BasicExpression between the column and the value provided. If the value is a
	 * BasicExpression, returns it. If the field is null, return a BasicExpression with the column
	 * equals to the value. If the field is an instance TextFieldDataField and the value is a instance
	 * of String, change wildcars with sql wildcards, like % or _.
	 * @param column Name of the table's column
	 * @param value Value to add as BasicExpression value
	 * @param field Filters DataField to check its type
	 * @return A BasicExpression created with the data provided
	 */
	protected BasicExpression createBasicExpression(final String column, final Object value, DataField field) {

		if (value instanceof BasicExpression) {
			return (BasicExpression) value;
		}

		if (field == null) {
			field = (DataField) this.getFormComponent("");
		}

		if (value instanceof SearchValue) {
			return new BasicExpression(new BasicField(column), (SearchValue) value, false);
		} else if ((value instanceof String) && (field instanceof TextFieldDataField)) {
			final String newValue = (String) this.obtainParsedValue(value, field);
			return new BasicExpression(new BasicField(column), BasicOperator.LIKE_OP, newValue);
		} else {
			return new BasicExpression(new BasicField(column), BasicOperator.EQUAL_OP, value);
		}
	}

	/***
	 * Parses the value to change its wildcards characters for sql wildcard characters if the value is a
	 * String and the DataField is an instance of TextFieldDataField
	 * @param value Value to check
	 * @param field Filters DataField to check its type
	 * @return The value parsed. If the value not meets the requirements, return de original value
	 *         passed as parameters
	 */
	protected Object obtainParsedValue(final Object value, final DataField field) {
		final Object toRet = value;
		if ((value instanceof String) && (field instanceof TextFieldDataField)) {
			String newValue = value.toString();
			newValue = newValue.replace(SQLStatementBuilder.ASTERISK_CHAR, SQLStatementBuilder.PERCENT);
			newValue = newValue.replace(SQLStatementBuilder.INTERROG, SQLStatementBuilder.LOW_LINE);
			return newValue;
		}
		return toRet;
	}

	/**
	 * Returns the key associated to the value passed as parameter in the specified map
	 * @param map Map to retrieve the key associated to a value
	 * @param value Value to retrieve its key
	 * @return key binded to the value passed as parameter if the value is founded , or
	 *         <code>null</code> otherwise
	 */
	public static <T, E> T getKeyByValue(final Map<T, E> map, final E value) {
		for (final Entry<T, E> entry : map.entrySet()) {

			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	/**
	 * An Exception that implies a FilterTableDataField has not all of its filters with values
	 *
	 * @author Imatia Innovation
	 *
	 */
	public class FilterNotFilledException extends Exception {

		public FilterNotFilledException(final String message) {
			super(message);
		}

	}

}
