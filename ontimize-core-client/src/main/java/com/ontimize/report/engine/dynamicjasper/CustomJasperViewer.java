package com.ontimize.report.engine.dynamicjasper;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.List;

import javax.swing.JPanel;

import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.report.DefaultReportDialog;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;
import net.sf.jasperreports.swing.JRViewerController;
import net.sf.jasperreports.swing.JRViewerToolbar;

public class CustomJasperViewer extends JRViewer implements Internationalization {

    protected DefaultReportDialog reportDialog;

    public CustomJasperViewer(JasperPrint jasperPrint, DefaultReportDialog reportDialog) {
        super(jasperPrint);
        this.setReportDialog(reportDialog);
    }

    protected void setReportDialog(DefaultReportDialog reportDialog) {
        this.reportDialog = reportDialog;
    }

    public DefaultReportDialog getReportDialog() {
        return this.reportDialog;
    }

    public JRViewerController getViewerController() {
        return this.viewerContext;
    }

    public JPanel getToolbar() {
        return this.tlbToolBar;
    }

    @Override
    protected JRViewerToolbar createToolbar() {
        return this.tlbToolBar = new CustomJasperViewerToolbar(this.viewerContext, this.reportDialog);
    }

    public JPanel getStatusBar() {
        return this.pnlStatus;
    }

    public void refresh(JasperPrint jasperPrint) {
        this.viewerContext.loadReport(jasperPrint);
    }

    @Override
    public void setComponentLocale(Locale l) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setResourceBundle(ResourceBundle resourceBundle) {
        if ((this.tlbToolBar != null) && (this.tlbToolBar instanceof Internationalization)) {
            ((Internationalization) this.tlbToolBar).setResourceBundle(resourceBundle);
        }
    }

    @Override
    public List getTextsToTranslate() {
        return null;
    }

}
