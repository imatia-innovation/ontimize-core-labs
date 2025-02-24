package com.ontimize.report.listeners;

import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.report.DefaultReportDialog;
import com.ontimize.util.share.FormAddSharedReference;

public class SharedItemListener implements ActionListener {

    protected String shareKey = "REPORT_SHARE_KEY";

    protected String preferenceKey;

    protected DefaultReportDialog reportDialog;

    protected EntityReferenceLocator locator;

    private static final Logger logger = LoggerFactory.getLogger(SharedItemListener.class);

    public SharedItemListener(String preferenceKey, DefaultReportDialog reportDialog) {
        this.preferenceKey = preferenceKey;
        this.reportDialog = reportDialog;
        this.locator = ApplicationManager.getApplication().getReferenceLocator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o instanceof AbstractButton) {
            String filterName = ((AbstractButton) o).getActionCommand();
            Point p = ((Component) e.getSource()).getLocationOnScreen();
            String user = ((ClientReferenceLocator) this.locator).getUser();
            Object value = DefaultReportDialog.getConfigurationValue(this.preferenceKey, user, filterName);

            try {

                FormAddSharedReference f = new FormAddSharedReference(
                        SwingUtilities.getWindowAncestor(
                                SwingUtilities.getAncestorOfClass(Window.class, (Component) e.getSource())),
                        true, this.locator, value, this.preferenceKey, user, "", filterName, false, p);

                if (f.getButtonOptionResult()) {
                    this.reportDialog.deleteConfiguration(filterName);
                }

                f = null;
            } catch (Exception e1) {
                SharedItemListener.logger.error("ERROR -> {}", e1.getMessage(), e1);
            }
        }
    }

}
