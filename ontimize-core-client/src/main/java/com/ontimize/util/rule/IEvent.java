package com.ontimize.util.rule;

import java.util.List;
import java.util.Map;

public interface IEvent {

	public String getType();

	public void setType(String type);

	public List getRules();

	public Map getAttributes();

	public void setAttributes(Map attributes);

	public void addRule(IRule rule);

	@Override
	public String toString();

}
