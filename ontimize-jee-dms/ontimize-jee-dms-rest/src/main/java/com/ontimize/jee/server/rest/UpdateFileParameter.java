package com.ontimize.jee.server.rest;

import java.io.Serializable;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;

import com.ontimize.jee.server.dms.model.OFile;

public class UpdateFileParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    protected OFile file;

    @XmlElement
    protected Map<Object, Object> params;

    public OFile getFile() {
        return file;
    }

    public void setFile(OFile file) {
        this.file = file;
    }

    public Map<Object, Object> getParams() {
        return params;
    }

    public void setParams(Map<Object, Object> params) {
        this.params = params;
    }

}
