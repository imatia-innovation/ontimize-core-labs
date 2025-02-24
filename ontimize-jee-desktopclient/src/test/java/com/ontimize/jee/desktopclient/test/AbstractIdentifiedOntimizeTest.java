package com.ontimize.jee.desktopclient.test;

public abstract class AbstractIdentifiedOntimizeTest extends AbstractOntimizeTest {

	@Override
	protected String getServiceBaseUrl() {
		return "http://localhost:9090";
	}

	@Override
	protected String getUser() {
		return "a";
	}

	@Override
	protected String getPass() {
		return "a";
	}

}
