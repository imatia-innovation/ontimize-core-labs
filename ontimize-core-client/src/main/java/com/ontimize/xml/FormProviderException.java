package com.ontimize.xml;

import com.ontimize.jee.common.dto.EntityResult;

public class FormProviderException extends Exception {

    public FormProviderException(String column, EntityResult er) {
        super("Form Entity Error. Column not found " + column + ". Results " + er);
    }

}
