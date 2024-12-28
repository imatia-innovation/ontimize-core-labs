package com.ontimize.gui.econ;

import java.io.File;
import java.util.List;

import com.ontimize.gui.econ.FileTransferModel.FileTransferRegister;
import com.ontimize.util.FileUtils;

public class FileTransferGenerator {

    protected FileTransferModel model = null;

    private static String newLine = "\n";

    private boolean saveANSI = false;

    public FileTransferGenerator(FileTransferModel m) {
        this.model = m;
    }

    public String getString() throws Exception {
        StringBuilder sb = new StringBuilder();
        try {
            List registros = this.model.generate();
            for (int i = 0; i < registros.size(); i++) {
                FileTransferRegister registro = (FileTransferRegister) registros.get(i);
                sb.append(registro.getContents());
                sb.append(FileTransferGenerator.newLine);
            }
            return sb.toString();
        } catch (Exception e) {
            throw e;
        }
    }

    public void save(String file) throws Exception {
        String contenido = this.getString();
        if (this.saveANSI) {
            FileUtils.saveANSIFile(new File(file), contenido);
        } else {
            FileUtils.saveOEMFile(new File(file), contenido);
        }
    }

    public void setSaveANSI(boolean s) {
        this.saveANSI = s;
    }

}
