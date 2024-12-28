package com.ontimize.report;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class FunctionEngine {

	private static final Logger logger = LoggerFactory.getLogger(FunctionEngine.class);

	Document doc = null;

	List groupNames = null;

	Map functionsColumns = null;

	TableModel table = null;

	List booleanColumn = null;

	List colsFunc = null;

	List opCodes = null;

	int groupNumber;

	public FunctionEngine(final TableModel tableModel, final List groupNames, final Map cols, final List booleanColumn, final Document d) {
		this.groupNames = groupNames;
		this.functionsColumns = cols;
		this.booleanColumn = booleanColumn;
		this.doc = d;
		this.table = tableModel;
		this.init();
	}

	private void init() {
		final Enumeration enu = Collections.enumeration(this.functionsColumns.keySet());
		this.colsFunc = new Vector();
		this.opCodes = new Vector();
		while (enu.hasMoreElements()) {
			final Object tmp = enu.nextElement();
			this.colsFunc.add(tmp);
			this.opCodes.add(this.functionsColumns.get(tmp));
		}
	}

	/**
	 * Returns a functions Element.
	 * @return the element returned
	 */
	public Element getFunctions() {
		FunctionEngine.logger.debug("JFreeReport -- INFO: Generating FUNCTIONS");

		final Element functions = this.doc.createElement("functions");
		if (this.booleanColumn != null) {
			for (int i = 0; i < this.booleanColumn.size(); i++) {
				Element function = this.doc.createElement("function");
				function.setAttribute("name", this.booleanColumn.get(i).toString() + "BooleanFunction");
				function.setAttribute("class", "com.ontimize.report.ElementBooleanSwitchFunction");
				Element properties = this.doc.createElement("properties");
				Element property = this.doc.createElement("property");
				property.setAttribute("name", "element");
				property.appendChild(this.doc.createTextNode("CHECK_" + this.booleanColumn.get(i).toString()));
				properties.appendChild(property);
				Element property2 = this.doc.createElement("property");
				property2.setAttribute("name", "trigger");
				property2.appendChild(this.doc.createTextNode(this.booleanColumn.get(i).toString()));
				properties.appendChild(property2);
				function.appendChild(properties);
				functions.appendChild(function);

				// For boolean groupings
				function = this.doc.createElement("function");
				function.setAttribute("name", this.booleanColumn.get(i).toString() + "BooleanFunctionGroup");
				function.setAttribute("class", "com.ontimize.report.ElementBooleanSwitchFunction");
				properties = this.doc.createElement("properties");
				property = this.doc.createElement("property");
				property.setAttribute("name", "element");
				property.appendChild(this.doc.createTextNode("CHECK_" + this.booleanColumn.get(i) + "Group"));
				properties.appendChild(property);
				property2 = this.doc.createElement("property");
				property2.setAttribute("name", "trigger");
				property2.appendChild(this.doc.createTextNode(this.booleanColumn.get(i) + "Group"));
				properties.appendChild(property2);
				final Element property3 = this.doc.createElement("property");
				property3.setAttribute("name", "group");
				property3.appendChild(this.doc.createTextNode(this.booleanColumn.get(i).toString()));
				properties.appendChild(property3);
				function.appendChild(properties);
				functions.appendChild(function);

			}
		}

		if (this.colsFunc != null) {
			if (!this.colsFunc.isEmpty()) {
				for (int i = 0; i < this.colsFunc.size(); i++) {
					Element function = this.doc.createElement("function");
					final String sCurrentElement = (String) this.colsFunc.get(i);
					function.setAttribute("name", this.colsFunc.get(i).toString() + "FunctionParaGrupo");

					// Now we select the type of function
					final int operationCode = ((Integer) this.opCodes.get(i)).intValue();
					if ("ReportDesigner.NumeroOcurrencias".equalsIgnoreCase(sCurrentElement)) {
						function.setAttribute("class", "org.jfree.report.function.TotalItemCountFunction");
					} else {
						if (operationCode == ReportUtils.SUM) {
							FunctionEngine.logger
							.debug("JFreeReport -- INFO: Generating ItemSumFunction for the external group");
							function.setAttribute("class", "org.jfree.report.function.ItemSumFunction");
						}
						if (operationCode == ReportUtils.MIN) {
							FunctionEngine.logger
							.debug("JFreeReport -- INFO: Generating ItemMinFunction for the external group");
							function.setAttribute("class", "com.ontimize.report.ItemMinFunction");
						}
						if (operationCode == ReportUtils.MAX) {
							FunctionEngine.logger
							.debug("JFreeReport -- INFO: Generating ItemMaxFunction for the external group");
							function.setAttribute("class", "org.jfree.report.function.ItemMaxFunction");
						}
						if (operationCode == ReportUtils.AVG) {
							FunctionEngine.logger
							.debug("JFreeReport -- INFO: Generating ItemAvgFunction for the external group");
							function.setAttribute("class", "org.jfree.report.function.ItemAvgFunction");
						}
					}

					Element properties = this.doc.createElement("properties");
					Element property = this.doc.createElement("property");

					if (!"ReportDesigner.NumeroOcurrencias".equalsIgnoreCase(sCurrentElement)) {
						property.setAttribute("name", "field");
						property.appendChild(this.doc.createTextNode(this.colsFunc.get(i).toString()));
						properties.appendChild(property);
					}
					function.appendChild(properties);
					functions.appendChild(function);

					for (int j = 0; j < this.groupNames.size(); j++) {
						function = this.doc.createElement("function");
						function.setAttribute("name", this.colsFunc.get(i).toString() + "FunctionParaGrupo"
								+ this.groupNames.get(j));

						if ("ReportDesigner.NumeroOcurrencias".equalsIgnoreCase(sCurrentElement)) {
							function.setAttribute("class", "org.jfree.report.function.TotalItemCountFunction");
						} else {

							if (operationCode == ReportUtils.SUM) {
								FunctionEngine.logger.debug(
										"JFreeReport -- INFO: Generating TotalGroupSumFunction for the group {}",
										this.groupNames.get(j));
								function.setAttribute("class", "org.jfree.report.function.TotalGroupSumFunction");
							}
							if (operationCode == ReportUtils.MIN) {
								FunctionEngine.logger.debug(
										"JFreeReport -- INFO: Generating ItemMinFunction for the group {}",
										this.groupNames.get(j));
								function.setAttribute("class", "com.ontimize.report.ItemMinFunction");
							}
							if (operationCode == ReportUtils.MAX) {
								FunctionEngine.logger.debug(
										"JFreeReport -- INFO: Generating ItemMaxFunction for the group {}",
										this.groupNames.get(j));
								function.setAttribute("class", "org.jfree.report.function.ItemMaxFunction");
							}
							if (operationCode == ReportUtils.AVG) {
								FunctionEngine.logger.debug(
										"JFreeReport -- INFO: Generating ItemAvgFunction for the group {}",
										this.groupNames.get(j));
								function.setAttribute("class", "org.jfree.report.function.ItemAvgFunction");
							}
						}

						properties = this.doc.createElement("properties");
						if (!"ReportDesigner.NumeroOcurrencias".equalsIgnoreCase(sCurrentElement)) {
							property = this.doc.createElement("property");
							property.setAttribute("name", "field");
							property.appendChild(this.doc.createTextNode(this.colsFunc.get(i).toString()));
							properties.appendChild(property);
						}

						property = this.doc.createElement("property");
						property.setAttribute("name", "group");
						property.appendChild(this.doc.createTextNode(this.groupNames.get(j).toString()));
						properties.appendChild(property);
						function.appendChild(properties);
						functions.appendChild(function);
					}
				}
			}
		}
		return functions;
	}

}
