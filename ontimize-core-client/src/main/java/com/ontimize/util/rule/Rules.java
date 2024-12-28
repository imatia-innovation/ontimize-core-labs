package com.ontimize.util.rule;

import java.util.List;
import java.util.Vector;

import com.ontimize.util.rule.RuleParser.Attributes;

/**
 * This class contains the complete presentation logic for a particular form. It is composed by a
 * list of events.
 *
 * <code> <br> <br> &lt;rules&gt; <br> &nbsp;&nbsp;&ltevent&gt <br> &nbsp;&nbsp;&lt/event&gt <br> &nbsp; ... <br> &nbsp;&nbsp;&lt;event&gt; <br> &nbsp;&nbsp;&lt;/event&gt; <br>
 * &lt;/rules&gt; <br> </code>
 *
 * @author Imatia Innovation
 * @since 5.2075EN
 */
public class Rules implements IRules {

	protected List events;

	public Rules() {
		this.events = new Vector();
	}

	public Rules(final List events) {
		this.setEvents(events);
	}

	/**
	 * @param events the events to set
	 */
	public void setEvents(final List events) {
		this.events = events;
	}

	/**
	 * @return the events
	 */
	@Override
	public List getEvents() {
		return this.events;
	}

	@Override
	public void addEvent(final IEvent event) {
		this.events.add(event);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(Attributes.XML_HEADER);
		sb.append(RuleParser.openTag(Attributes.RULES));
		for (int i = 0; i < this.events.size(); i++) {
			sb.append(this.events.get(i).toString());
		}
		sb.append(RuleParser.closeTag(Attributes.RULES));
		return sb.toString();
	}

}
