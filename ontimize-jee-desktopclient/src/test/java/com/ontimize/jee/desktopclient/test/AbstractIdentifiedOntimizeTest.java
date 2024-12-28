package com.ontimize.jee.desktopclient.test;

public abstract class AbstractIdentifiedOntimizeTest extends AbstractOntimizeTest {

    @Override
    protected String getServiceBaseUrl() {
        return "http://127.0.0.1:9999/rexunta/services/hessian";
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
