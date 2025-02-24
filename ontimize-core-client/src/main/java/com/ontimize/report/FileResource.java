package com.ontimize.report;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.ontimize.jee.common.report.ReportResource;

public class FileResource implements ReportResource {

    protected byte[] bytes = null;

    protected String name = null;

    public FileResource(File f) throws IOException {
        if (f.isDirectory()) {
            throw new IllegalArgumentException("FileResource can´t be directory");
        }
        this.name = f.getName();
        FileInputStream fIn = new FileInputStream(f);
        BufferedInputStream bIn = new BufferedInputStream(fIn);
        int a = -1;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        while ((a = bIn.read()) != -1) {
            bOut.write(a);
        }
        this.bytes = bOut.toByteArray();
    }

    public FileResource(String nameResource, InputStream input) throws IOException {
        this.name = nameResource;
        BufferedInputStream bIn = new BufferedInputStream(input);
        int a = -1;
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        while ((a = bIn.read()) != -1) {
            bOut.write(a);
        }
        this.bytes = bOut.toByteArray();
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
