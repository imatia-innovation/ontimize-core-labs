package com.ontimize.util.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.util.rule.RuleParser.Attributes;

public class RuleUtils {

	/**
	 * List of events matching <code>type</code>. If type is "" or null returns all events for these
	 * rules.
	 */
	public static List findEventsByType(final IRules rules, final String type) {
		final List lMatchEvents = new Vector();
		if ((rules == null) || (rules.getEvents().size() == 0)) {
			return lMatchEvents;
		}
		final List lAllEvents = rules.getEvents();
		if ((type == null) || (type.length() == 0)) {
			return lAllEvents;
		}
		for (int i = 0; i < lAllEvents.size(); i++) {
			final IEvent currentEvent = (IEvent) lAllEvents.get(i);
			if (type.equalsIgnoreCase(currentEvent.getType())) {
				lMatchEvents.add(currentEvent);
			}
		}
		return lMatchEvents;
	}

	/**
	 * List of events matching <code>type</code> and <code>field</code>. If type is "" or null returns
	 * all events for these rules.
	 */
	public static List findEventsByField(final IRules rules, final String field) {
		final List lMatchEvents = new Vector();
		if ((rules == null) || (rules.getEvents().size() == 0)) {
			return lMatchEvents;
		}
		final List lAllEvents = rules.getEvents();
		if ((field == null) || (field.length() == 0)) {
			return lAllEvents;
		}
		for (int i = 0; i < lAllEvents.size(); i++) {
			final IEvent currentEvent = (IEvent) lAllEvents.get(i);
			final Object oField = currentEvent.getAttributes().get(Attributes.FIELD);
			if ((oField != null) && field.equalsIgnoreCase(oField.toString())) {
				lMatchEvents.add(currentEvent);
			}
		}
		return lMatchEvents;
	}

	/**
	 * List of events matching <code>type</code> and <code>field</code>. If type is "" or null returns
	 * all events for these rules.
	 */
	public static List findEventsByFieldAndType(final IRules rules, final String field, final String type) {
		final List lMatchEvents = new Vector();
		if ((rules == null) || (rules.getEvents().size() == 0)) {
			return lMatchEvents;
		}
		final List lAllEvents = rules.getEvents();
		if ((type == null) || (type.length() == 0)) {
			return lAllEvents;
		}
		for (int i = 0; i < lAllEvents.size(); i++) {
			final IEvent currentEvent = (IEvent) lAllEvents.get(i);
			final Object oField = currentEvent.getAttributes().get(Attributes.FIELD);
			if (type.equalsIgnoreCase(currentEvent.getType()) && (oField != null)
					&& field.equalsIgnoreCase(oField.toString())) {
				lMatchEvents.add(currentEvent);
			}
		}
		return lMatchEvents;
	}

	/**
	 * List of actions matching <code>actionType</code> in Event. If actionType is "" or null returns
	 * all actions for this event. This method does not take account conditions.
	 */
	public static List findActionsByEvent(final IEvent event, final String actionId) {
		final List lMatchActions = new Vector();
		if ((event == null) || (event.getRules().size() == 0)) {
			return lMatchActions;
		}
		final List lEventRules = event.getRules();
		for (int i = 0; i < lEventRules.size(); i++) {
			final IRule currentRule = (IRule) lEventRules.get(i);
			final List lActions = currentRule.getActions();
			if ((actionId == null) || (actionId.length() == 0)) {
				lMatchActions.addAll(lActions);
			} else {
				for (int j = 0; j < lActions.size(); j++) {
					final IAction currentAction = (IAction) lActions.get(j);
					if (actionId.equalsIgnoreCase(currentAction.getId())) {
						lMatchActions.add(currentAction);
					}
				}
			}
		}
		return lMatchActions;
	}

	/**
	 * List of actions matching event type and event field <code>actionType</code> in Event. If
	 * actioType is "" or null returns all actions matching this eventType and eventField. This method
	 * does not take account conditions.
	 */
	public static List findActionsByTypeAndField(final IRules rules, final String eventType, final Map filterEvents,
			final String eventField, final String actionType) {
		final List lMatchActions = new Vector();
		final List lEvents = rules.getEvents();
		if ((lEvents == null) || (lEvents.size() == 0)) {
			return lMatchActions;
		}
		for (int i = 0; i < lEvents.size(); i++) {
			final IEvent event = (IEvent) lEvents.get(i);
			if (RuleUtils.matchAttributes(event.getAttributes(), filterEvents)) {
				final Object currentEventField = event.getAttributes().get(Attributes.FIELD);
				if (eventType.equalsIgnoreCase(event.getType()) && (eventField != null)
						&& eventField.equalsIgnoreCase(currentEventField.toString())) {
					final List lEventRules = event.getRules();
					for (int j = 0; j < lEventRules.size(); j++) {
						final IRule rule = (IRule) lEventRules.get(j);
						final List lActions = rule.getActions();
						if ((actionType == null) || (actionType.length() == 0)) {
							lMatchActions.addAll(lActions);
						} else {
							for (int k = 0; k < lActions.size(); k++) {
								final IAction action = (IAction) lActions.get(k);
								if (actionType.equalsIgnoreCase(action.getId())) {
									lMatchActions.add(action);
								}
							}
						}
					}
				}
			}
		}
		return lMatchActions;
	}

	public static EntityResult paramActionsToEntityResult(final List paramActions) {
		final EntityResult res = new EntityResultMapImpl(
				Arrays.asList(new String[] { Attributes.PARAM_NAME, Attributes.PARAM_VALUE }));
		for (int i = 0; i < paramActions.size(); i++) {
			final IActionParam param = (IActionParam) paramActions.get(i);
			final Map hParam = new Hashtable();
			hParam.put(Attributes.PARAM_VALUE, param.getParamValue());
			hParam.put(Attributes.PARAM_NAME, param.getParamName());
			res.addRecord(hParam);
		}
		return res;
	}

	public static List entityResultToParamActions(final EntityResult erActions) {
		final List paramActions = new Vector();
		for (int i = 0; i < erActions.calculateRecordNumber(); i++) {
			final Map hParam = erActions.getRecordValues(i);
			final IActionParam param = new ActionParam();
			final Object paramName = hParam.get(Attributes.PARAM_NAME);
			if (paramName != null) {
				param.setParamName(paramName.toString());
			}
			final Object paramValue = hParam.get(Attributes.PARAM_VALUE);
			if (paramValue != null) {
				param.setParamValue(paramValue.toString());
			}
			paramActions.add(param);
		}
		return paramActions;
	}

	public static boolean matchAttributes(final Map attributeList, final Map attrsToMatch) {
		if ((attrsToMatch == null) || attrsToMatch.isEmpty()) {
			return true;
		}
		if (attrsToMatch != null) {
			final Enumeration enumKeys = Collections.enumeration(attrsToMatch.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object key = enumKeys.nextElement();
				if (attributeList.containsKey(key)) {
					final Object oColumn = attrsToMatch.get(key);
					if (!oColumn.equals(attributeList.get(key))) {
						return false;
					}
				} else {
					return false;
				}

			}
		}
		return true;
	}

	// /**
	// * List of actions matching <code>actionType</code>. If actionType is ""
	// or null
	// * returns all actions for this event. This method does not take account
	// * conditions.
	// */
	// public static List findConditionsByEvent(IEvent event, String actionId) {
	// List lMatchActions = new Vector();
	// if (event == null || event.getRules().size() == 0) {
	// return lMatchActions;
	// }
	// List lEventRules = event.getRules();
	// for (int i=0; i < lEventRules.size(); i++) {
	// IRule currentRule = (IRule) lEventRules.get(i);
	// List lActions = currentRule.getActions();
	// if (actionId == null || actionId.length() == 0) {
	// lMatchActions.addAll(lActions);
	// }
	// else {
	// for (int j=0; j<lActions.size(); j++) {
	// IAction currentAction = (IAction) lActions.get(j);
	// if (actionId.equalsIgnoreCase(currentAction.getId())) {
	// lMatchActions.add(currentAction);
	// }
	// }
	// }
	// }
	// return lMatchActions;
	// }

}
