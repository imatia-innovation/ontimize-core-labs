package com.ontimize.util.rule;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ontimize.jee.common.xml.XMLUtil;

public class RuleParser {

	private static final Logger logger = LoggerFactory.getLogger(RuleParser.class);

	public static IRules parseRules(final String rules) {
		try {

			final Document doc = XMLUtil.getDOMDocument(rules);

			final NodeList elementsByTagName = doc.getElementsByTagName(Attributes.RULES);
			if ((elementsByTagName != null) && (elementsByTagName.getLength() == 1)) {
				final Node currentXMLNode = elementsByTagName.item(0);
				return RuleParser.parseRulesXMLNode(currentXMLNode);
			}

		} catch (final Exception e) {
			RuleParser.logger.error(null, e);
		}
		return null;
	}

	protected static IRules parseRulesXMLNode(final Node rulesNode) {
		final IRules rules = new Rules();
		final List events = new Vector();
		final NodeList rulesChildNodes = rulesNode.getChildNodes();
		// Children should be a list of:
		// <event>
		// </event>
		for (int i = 0; i < rulesChildNodes.getLength(); i++) {
			final Node currentChildNode = rulesChildNodes.item(i);
			final String nodeName = currentChildNode.getNodeName();
			if (Attributes.EVENT.equalsIgnoreCase(nodeName)) {
				// Parse each <event> tag
				final IEvent event = RuleParser.parseEventXMLNode(currentChildNode);
				rules.addEvent(event);
			}
		}
		return rules;
	}

	protected static IEvent parseEventXMLNode(final Node eventNode) {
		final IEvent event = new Event();
		final Map eventAttributes = XMLUtil.parseAttributes(eventNode.getAttributes());
		event.setAttributes(eventAttributes);
		event.setType(eventAttributes.get(Attributes.TYPE).toString());
		final NodeList eventChildNodes = eventNode.getChildNodes();
		// Event contains a list of rules:
		// <rule>
		// </rule>
		for (int i = 0; i < eventChildNodes.getLength(); i++) {
			final Node currentChildNode = eventChildNodes.item(i);
			final String nodeName = currentChildNode.getNodeName();
			if (Attributes.RULE.equalsIgnoreCase(nodeName)) {
				// Parse each <rule> tag
				final IRule rule = RuleParser.parseRuleEventXMLNode(currentChildNode);
				event.addRule(rule);
			}
		}
		return event;
	}

	protected static IRule parseRuleEventXMLNode(final Node ruleEventNode) {
		final IRule rule = new Rule();
		final NodeList eventChildNodes = ruleEventNode.getChildNodes();
		// Rule contains a condition and a list of actions:
		// <condition>
		// </condition>
		// <action>
		// </action>
		// List lCurrentConditions = new Vector();
		ICondition condition = new Condition();
		for (int i = 0; i < eventChildNodes.getLength(); i++) {
			final Node currentChildNode = eventChildNodes.item(i);
			final String nodeName = currentChildNode.getNodeName();
			if (Attributes.CONDITION.equalsIgnoreCase(nodeName)) {
				// <condition> tag found
				condition = RuleParser.parseRuleConditionXMLNode(currentChildNode);
				rule.setCondition(condition);
				// lCurrentConditions.add(condition);
			} else if (Attributes.ACTION.equalsIgnoreCase(nodeName)) {
				final IAction action = RuleParser.parseRuleActionXMLNode(currentChildNode);
				action.setCondition(condition);
				rule.addAction(action);
			}
		}
		return rule;
	}

	protected static ICondition parseRuleConditionXMLNode(final Node ruleConditionNode) {
		final ICondition condition = new Condition();
		condition.setExpression(XMLUtil.getTextValue(ruleConditionNode));
		return condition;
	}

	protected static IAction parseRuleActionXMLNode(final Node ruleActionNode) {
		final IAction action = new Action();
		final Map eventAttributes = XMLUtil.parseAttributes(ruleActionNode.getAttributes());
		final Object id = eventAttributes.get(Attributes.ID);
		if (id != null) {
			action.setId(id.toString());
		}
		// Object type = eventAttributes.get(Attributes.TYPE);
		// if (type != null) {
		// action.setType(type.toString());
		// }
		final NodeList eventChildNodes = ruleActionNode.getChildNodes();
		// Action contains one tag with params:
		// <params>
		// </params>
		for (int i = 0; i < eventChildNodes.getLength(); i++) {
			final Node currentChildNode = eventChildNodes.item(i);
			final String nodeName = currentChildNode.getNodeName();
			if (Attributes.PARAMS.equalsIgnoreCase(nodeName)) {
				// create Map with parameters
				final List params = RuleParser.parseRuleActionParameters(currentChildNode);
				action.setParams(params);
			}
		}
		return action;
	}

	protected static List parseRuleActionParameters(final Node ruleActionParamNode) {
		final List params = new Vector();
		final NodeList eventChildNodes = ruleActionParamNode.getChildNodes();
		// action parameters are a list of:
		// <param-name>
		// </param-value>
		// ...
		// <param-name>
		// </param-value>
		Object currentParamName = null;
		Object currentParamValue = null;
		for (int i = 0; i < eventChildNodes.getLength(); i++) {
			final Node currentChildNode = eventChildNodes.item(i);
			final String nodeName = currentChildNode.getNodeName();
			if (Attributes.PARAM_NAME.equalsIgnoreCase(nodeName)) {
				currentParamName = XMLUtil.getTextValue(currentChildNode);
			}
			if (Attributes.PARAM_VALUE.equalsIgnoreCase(nodeName)) {
				currentParamValue = XMLUtil.getTextValue(currentChildNode);
				final ActionParam actionParams = new ActionParam();
				if (currentParamName != null) {
					actionParams.setParamName(currentParamName.toString());
				}
				if (currentParamValue != null) {
					actionParams.setParamValue(currentParamValue.toString());
				}
				params.add(actionParams);
			}
		}
		return params;
	}

	public static String generateXMLAttribute(final String key, final String value) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.SPACE);
		sb.append(key);
		sb.append(Attributes.EQUALS);
		sb.append(Attributes.DOUBLE_QUOTES);
		sb.append(value);
		sb.append(Attributes.DOUBLE_QUOTES);
		return sb.toString();
	}

	public static String generateCDATA(final String value) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.CDATA);
		sb.append(value);
		sb.append(Attributes.CDATA_CLOSE);
		return sb.toString();
	}

	public static String generateXMLAttributes(final Map values) {
		final StringBuilder sb = new StringBuilder();
		final Enumeration enumKeys = Collections.enumeration(values.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object key = enumKeys.nextElement();
			final Object value = values.get(key);
			sb.append(Attributes.SPACE);
			sb.append(key.toString());
			sb.append(Attributes.EQUALS);
			sb.append(Attributes.DOUBLE_QUOTES);
			sb.append(value.toString());
			sb.append(Attributes.DOUBLE_QUOTES);
		}
		return sb.toString();
	}

	public static String openTag(final String tag) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.LT);
		sb.append(tag);
		sb.append(Attributes.GT);
		return sb.toString();
	}

	public static String openTagWithAttributes(final String tag, final Map attributes) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.LT);
		sb.append(tag);
		sb.append(RuleParser.generateXMLAttributes(attributes));
		sb.append(Attributes.GT);
		return sb.toString();
	}

	public static String openTagWithAttribute(final String tag, final String key, final String value) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.LT);
		sb.append(tag);
		sb.append(RuleParser.generateXMLAttribute(key, value));
		sb.append(Attributes.GT);
		return sb.toString();
	}

	public static String closeTag(final String tag) {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.LT_CLOSE);
		sb.append(tag);
		sb.append(Attributes.GT);
		return sb.toString();
	}

	public static class Attributes {

		public static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"no\"?>";

		public static final String LT = "<";

		public static final String LT_CLOSE = "</";

		public static final String EQUALS = "=";

		public static final String DOUBLE_QUOTES = "\"";

		public static final String SPACE = " ";

		public static final String GT = ">";

		public static final String CDATA = "<![CDATA[";

		public static final String CDATA_CLOSE = "]]>";

		// XML tag for rules
		public static final String RULES = "rules";

		public static final String EVENT = "event";

		public static final String RULE = "rule";

		public static final String ACTION = "action";

		public static final String CONDITION = "condition";

		public static final String PARAMS = "params";

		public static final String PARAM_NAME = "param-name";

		public static final String PARAM_VALUE = "param-value";

		// Event types
		public static final String VALUE_TYPE_EVENT = "value";

		public static final String FORM_TYPE_EVENT = "form";

		public static final String ACTION_TYPE_EVENT = "action";

		public static final String SELECTION_TYPE_EVENT = "selection";

		// Id
		public static final String ID = "id";

		public static final String TYPE = "type";

		public static final String FIELD = "field";

		public static final String MODE = "mode";

		// values
		public static final String USER = "user";

		public static final String PROGRAMMATIC = "programmatic";

		// actions
		public static final String SHOW_MESSAGE_ACTION = "ShowMessageAction";

		public static final String SET_VALUE_ACTION = "SetValueAction";

		public static final String SET_ENABLED_ACTION = "SetEnabledAction";

		public static final String SET_REQUIRED_ACTION = "SetRequiredAction";

		public static final String CHANGE_MODE_ACTION = "ChangeModeAction";

		public static final String OPEN_DETAIL_FORM_ACTION = "OpenDetailFormAction";

	}

}
