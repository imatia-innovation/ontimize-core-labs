package com.ontimize.jee.server.spring.namespace;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class OntimizeDMSServerSpringHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        this.registerBeanDefinitionParser("ontimize-dms-configuration",
                new OntimizeDMSConfigurationBeanDefinitionParser());
    }

}
