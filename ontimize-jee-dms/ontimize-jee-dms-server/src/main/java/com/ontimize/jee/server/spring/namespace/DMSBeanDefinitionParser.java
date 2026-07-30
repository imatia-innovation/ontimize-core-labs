package com.ontimize.jee.server.spring.namespace;

import java.util.List;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.ontimize.jee.common.spring.parser.DefinitionParserUtil;
import com.ontimize.jee.server.services.dms.DMSConfiguration;
import com.ontimize.jee.server.services.dms.OntimizeDMSEngine;

/**
 * The Class DMSBeanDefinitionParser.
 */
public class DMSBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    /** The Constant SCOPE. */
    private static final String SCOPE = "scope";

    /*
     * (non-Javadoc)
     *
     * @see
     * org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser#getBeanClass(org.w3c.dom
     * .Element)
     */
    @Override
    protected Class<?> getBeanClass(Element element) {
        return DMSConfiguration.class;
    }

    /**
     * Called when the dms tag is to be parsed.
     * @param element The tag element
     * @param ctx The context in which the parsing is occuring
     * @param builder The bean definitions build to use
     */
    @Override
    protected void doParse(Element element, ParserContext ctx, BeanDefinitionBuilder builder) {
        Element child = DomUtils.getChildElements(element).get(0);
        Object engine;
        if ("ontimize-dms-engine".equals(child.getLocalName())) {
            final ParserContext nestedCtx = new ParserContext(ctx.getReaderContext(), ctx.getDelegate(),
                    builder.getBeanDefinition());
            engine = new OntimizeDMSParser().parse(child, nestedCtx);
        } else {
            // construimos el bean que nos venga que deberia ser un IRemoteApplicationPreferencesEngine
            engine = DefinitionParserUtil.parseNode(child, ctx, builder.getBeanDefinition(),
                    element.getAttribute(DMSBeanDefinitionParser.SCOPE), false);
        }
        builder.addPropertyValue("engine", engine);
        builder.setLazyInit(true);
    }

    /**
     * The Class FileRemotePreferencesParser.
     */
    public static class OntimizeDMSParser extends AbstractSingleBeanDefinitionParser {

        /**
         * The bean that is created for this tag element.
         * @param element The tag element
         * @return A FileListFactoryBean
         */
        @Override
        protected Class<?> getBeanClass(final Element element) {
            return OntimizeDMSEngine.class;
        }

        /**
         * Called when the fileList tag is to be parsed.
         * @param element The tag element
         * @param ctx The context in which the parsing is occuring
         * @param builder The bean definitions build to use
         */
        @Override
        protected void doParse(final Element element, final ParserContext ctx, final BeanDefinitionBuilder builder) {
            Element item = DomUtils.getChildElementByTagName(element, "document-base-path");
            List<Element> childElements = DomUtils.getChildElements(item);
            if (childElements.isEmpty()) {
                builder.addPropertyValue("documentsBasePath", item.getNodeValue());
            } else {
                Object parseNode = DefinitionParserUtil.parseNode(childElements.get(0), ctx,
                        builder.getBeanDefinition(), element.getAttribute("scope"));
                builder.addPropertyValue("documentsBasePathResolver", parseNode);
                if (parseNode instanceof GenericBeanDefinition) {
                    if (!((AbstractBeanDefinition) parseNode).getPropertyValues()
                        .contains("useMyselfInSpringContext")) {
                        ((AbstractBeanDefinition) parseNode).getPropertyValues()
                            .add("useMyselfInSpringContext", Boolean.TRUE);
                    }
                }
            }
            builder.setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
            builder.setLazyInit(true);
        }

    }

}
