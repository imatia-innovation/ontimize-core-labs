/*
 * Class JasperViewer.
 */
package com.ontimize.report.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.ontimize.gui.container.EJDialog;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.swing.JRViewer;

public class JasperViewerDialog extends EJDialog {

	protected String title;

	protected javax.swing.JPanel pnlMain;

	/** Creates new form JasperViewer */
	/**
	 * @param jasperPrint report to display
	 * @param frameTitle Title to be displayed
	 * @throws JRException
	 */
	public JasperViewerDialog(final JDialog parent, final JasperPrint jasperPrint, final String title) throws JRException {
		super(parent, true);
		this.title = title;
		this.initComponents();
		final JRViewer viewer = new JRViewer(jasperPrint);
		this.pnlMain.add(viewer, BorderLayout.CENTER);
	}

	public JasperViewerDialog(final Frame parentComponent, final JasperPrint jasperPrint, final String title) {
		super(parentComponent, true);
		this.title = title;
		this.initComponents();
		final JRViewer viewer = new JRViewer(jasperPrint);
		this.pnlMain.add(viewer, BorderLayout.CENTER);
	}

	protected void initComponents() {
		this.pnlMain = new javax.swing.JPanel();

		this.setTitle(this.title);
		this.addWindowListener(new java.awt.event.WindowAdapter() {

			@Override
			public void windowClosing(final java.awt.event.WindowEvent evt) {
				JasperViewerDialog.this.exitForm();
			}
		});
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(final KeyEvent event) {
				if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
					JasperViewerDialog.this.exitForm();
				}
			}

			@Override
			public void keyTyped(final KeyEvent event) {
				super.keyTyped(event);
			}
		});

		this.pnlMain.setLayout(new java.awt.BorderLayout());

		this.getContentPane().add(this.pnlMain, java.awt.BorderLayout.CENTER);

		this.pack();
		final java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new java.awt.Dimension(750, 550));
		this.setLocation((screenSize.width - 750) / 2, (screenSize.height - 550) / 2);
	}

	protected void exitForm() {
		this.hide();
		this.dispose();
	}

	public static void viewReport(final Component component, final JasperPrint jasperPrint, final String titleDialog) throws JRException {
		Component parentComponent = SwingUtilities.getAncestorOfClass(JDialog.class, component);
		if (parentComponent != null) {
			final JasperViewerDialog jasperViewer = new JasperViewerDialog((JDialog) parentComponent, jasperPrint,
					titleDialog);
			jasperViewer.setVisible(true);
		} else {
			parentComponent = SwingUtilities.getAncestorOfClass(Frame.class, component);
			final JasperViewerDialog jasperViewer = new JasperViewerDialog((Frame) parentComponent, jasperPrint, titleDialog);
			jasperViewer.setVisible(true);
		}
	}

	public static void viewReport(final Component component, final JasperPrint jasperPrint) throws JRException {
		JasperViewerDialog.viewReport(component, jasperPrint, "JasperReport");
	}

	public JPanel getReportPanel() {
		return this.pnlMain;
	}

	public void setReportPanel(final JPanel pnlMain) {
		this.pnlMain = pnlMain;
	}

}
