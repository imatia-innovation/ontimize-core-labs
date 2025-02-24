package com.ontimize.util.rule;

import java.util.List;
import java.util.Vector;

import com.ontimize.util.rule.RuleParser.Attributes;

public class Rule implements IRule {

	protected ICondition condition;

	protected List actions;

	public Rule() {
		this.condition = new Condition();
		this.actions = new Vector();
	}

	public Rule(final ICondition condition, final List actions) {
		this.setCondition(condition);
		this.setActions(actions);
	}

	/**
	 * @param conditions the conditions to set
	 */
	@Override
	public void setCondition(final ICondition condition) {
		this.condition = condition;
	}

	/**
	 * @return the conditions
	 */
	@Override
	public ICondition getCondition() {
		return this.condition;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(final List actions) {
		this.actions = actions;
	}

	/**
	 * @return the actions
	 */
	@Override
	public List getActions() {
		return this.actions;
	}

	@Override
	public void addAction(final IAction action) {
		this.actions.add(action);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(RuleParser.openTag(Attributes.RULE));
		sb.append(this.condition.toString());
		for (int i = 0; i < this.actions.size(); i++) {
			sb.append(this.actions.get(i).toString());
		}
		sb.append(RuleParser.closeTag(Attributes.RULE));
		return sb.toString();
	}

}
