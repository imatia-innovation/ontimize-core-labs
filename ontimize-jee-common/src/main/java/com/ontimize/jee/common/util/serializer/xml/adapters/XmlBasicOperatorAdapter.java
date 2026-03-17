package com.ontimize.jee.common.util.serializer.xml.adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.apache.commons.text.StringEscapeUtils;

import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;

public class XmlBasicOperatorAdapter extends XmlAdapter<String, BasicOperator> {

    @Override
    public BasicOperator unmarshal(final String v) throws Exception {
		return new BasicOperator(StringEscapeUtils.unescapeHtml4(v));
    }

    @Override
    public String marshal(final BasicOperator v) throws Exception {
        return v.toString();
    }

}

