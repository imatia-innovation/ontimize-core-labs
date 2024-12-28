package com.ontimize.util.rule;

import java.util.List;
import java.util.Vector;

import com.ontimize.util.rule.RuleParser.Attributes;

public class Action implements IAction {

	protected String id;

	protected List params;

	protected ICondition condition;

	public Action() {
		this.params = new Vector();
	}

	public Action(final String id, final List params) {
		this.id = id;
		this.params = params;
	}

	/**
	 * @param params the params to set
	 */
	@Override
	public void setParams(final List params) {
		this.params = params;
	}

	/**
	 * @return the params
	 */
	@Override
	public List getParams() {
		return this.params;
	}

	/**
	 * @param id the id to set
	 */
	@Override
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setCondition(final ICondition condition) {
		this.condition = condition;
	}

	@Override
	public ICondition getCondition() {
		return this.condition;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(RuleParser.openTagWithAttribute(Attributes.ACTION, Attributes.ID, this.id));
		sb.append(RuleParser.openTag(Attributes.PARAMS));
		for (int i = 0; i < this.params.size(); i++) {
			// ActionParams items
			sb.append(this.params.get(i).toString());
		}
		sb.append(RuleParser.closeTag(Attributes.PARAMS));
		sb.append(RuleParser.closeTag(Attributes.ACTION));
		return sb.toString();
	}

}
