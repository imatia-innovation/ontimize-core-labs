package com.ontimize.jee.server.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ontimize.jee.server.dms.model.OFile;

@XmlRootElement
public class FileListParameter implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement
    protected List<OFile> fileList;

    public List<OFile> getFileList() {
        return this.fileList;
    }

    public void setFileList(List<OFile> filterList) {
        this.fileList = filterList;
    }

}
