package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.table.TableColumn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.printing.PageFooter;
import com.ontimize.printing.PageHeader;
import com.ontimize.printing.ReportFrame;
import com.ontimize.printing.TextAttributes;

/**
 * Class that determines the window that manages the printing of the table columns.
 */
public class PrintingWindow extends EJDialog implements Freeable {

	private static final Logger logger = LoggerFactory.getLogger(PrintingWindow.class);

	protected JLabel labelTitle = new JLabel(ApplicationManager.getTranslation("M_PRINTING_WINDOW_TITLE"));

	protected JButton buttonPageSetup = new JButton(
			ApplicationManager.getTranslation("M_PRINTING_WINDOW_CONFIGURE_PAGE"));

	protected JButton buttonPrint = new JButton(ApplicationManager.getTranslation("M_PRINTING_WINDOW_PRINT_BUTTON"));

	protected JTextField titleTextField = new JTextField(30);

	protected JPanel panelNorth = new JPanel(new FlowLayout(FlowLayout.LEFT));

	protected JPanel panelCenter = new JPanel(new BorderLayout());

	protected JPanel panelCheckBox = new JPanel(new GridLayout(0, 1));

	protected List listCheckBox = new Vector();

	protected List listCheckBoxPrinting = new Vector();

	protected JProgressBar progressBar = new JProgressBar();

	protected JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));

	protected PageFormat pageFormat = PrinterJob.getPrinterJob().defaultPage();

	protected List columnNamesOrder = new Vector();

	protected ReportFrame inf = null;

	protected Table ontimizeTable;

	public PrintingWindow(final Frame parentFrame, final Table oTable) {
		super(parentFrame, true);
		this.ontimizeTable = oTable;
		this.getContentPane().add(this.panelNorth, BorderLayout.NORTH);
		this.getContentPane().add(this.panelCenter);
		this.progressBar.setValue(0);
		this.progressBar
		.setMaximum(this.ontimizeTable.getJTable().getColumnCount() * this.ontimizeTable.getJTable().getRowCount());
		this.progressBar.setPreferredSize(new Dimension(200, this.progressBar.getPreferredSize().height));
		this.getContentPane().add(this.panelSouth, BorderLayout.SOUTH);
		this.panelNorth.add(this.labelTitle);
		this.panelNorth.add(this.titleTextField);
		this.panelCenter.add(this.progressBar, BorderLayout.SOUTH);
		this.panelCenter.add(this.panelCheckBox);

		// It is possible to select columns to print and order to show them in
		// the table view (swing).
		// Column attributes are used as table header.

		int rejected = 0;
		for (int i = 0; i < this.ontimizeTable.getJTable().getColumnCount(); i++) {
			// Get the column
			final TableColumn tableColumn = this.ontimizeTable.getJTable().getColumnModel().getColumn(i);
			if (ApplicationManager.DEBUG) {
				PrintingWindow.logger.debug("{}", tableColumn.getIdentifier());
			}
			if ((tableColumn.getMaxWidth() > 1) && tableColumn.getResizable()) {
				final String sAttribute = tableColumn.getIdentifier().toString();
				this.columnNamesOrder.add(i - rejected, sAttribute);
			} else {
				rejected++;
			}
		}

		final JLabel infoSum = new JLabel(
				ApplicationManager.getTranslation("M_PRINTING_WINDOW_PRINT_SUM_VALUES_FOR_COLUMNS"));
		infoSum.setFont(infoSum.getFont().deriveFont(Font.BOLD));
		this.panelCheckBox.add(infoSum);
		// Numeric data type columns
		for (int i = 0; i < this.ontimizeTable.getJTable().getColumnCount(); i++) {
			final String sColumnName = this.ontimizeTable.getJTable().getColumnName(i);
			final TableColumn tableColumn = this.ontimizeTable.getJTable().getColumnModel().getColumn(i);
			if (tableColumn.getResizable()) {
				if (this.ontimizeTable.getAttributeList().contains(sColumnName)) {
					try {
						final Object oValue = this.ontimizeTable.getJTable().getValueAt(0, i);
						if (oValue instanceof Number) {
							final JCheckBox check = new JCheckBox(sColumnName);
							check.setName(sColumnName);
							this.listCheckBox.add(check);
							this.panelCheckBox.add(check);
							continue;
						}
					} catch (final Exception e) {
						PrintingWindow.logger.trace(null, e);
					}
				}
			}
		}

		final JLabel infoCol = new JLabel(ApplicationManager.getTranslation("M_PRINTING_WINDOW_PRINT_COLUMNS_TO_PRINT"));
		infoCol.setFont(infoCol.getFont().deriveFont(Font.BOLD));
		this.panelCheckBox.add(infoCol);
		for (int i = 0; i < this.columnNamesOrder.size(); i++) {
			final String sColumnName = this.columnNamesOrder.get(i).toString();
			try {
				final JCheckBox check = new JCheckBox(sColumnName);
				check.setName(sColumnName);
				check.setSelected(true);
				this.listCheckBoxPrinting.add(check);
				this.panelCheckBox.add(check);
				continue;
			} catch (final Exception e) {
				PrintingWindow.logger.trace(null, e);
			}
		}
		this.panelSouth.add(this.buttonPageSetup);
		this.panelSouth.add(this.buttonPrint);
		this.pack();
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
		this.buttonPageSetup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				PrintingWindow.this.pageFormat = PrinterJob.getPrinterJob().pageDialog(PrintingWindow.this.pageFormat);
			}
		});
		this.buttonPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				final List vSortColumns = new Vector();
				int rejected = 0;
				for (int i = 0; i < PrintingWindow.this.listCheckBoxPrinting.size(); i++) {
					final JCheckBox check = (JCheckBox) PrintingWindow.this.listCheckBoxPrinting.get(i);
					if (check.isSelected()) {
						vSortColumns.add(i - rejected, check.getName());
					} else {
						rejected++;
					}
				}
				PrintingWindow.this.inf = new ReportFrame(PrintingWindow.this.pageFormat, true,
						new PageHeader(PrintingWindow.this.titleTextField.getText(),
								new TextAttributes(TextAttributes.ARIAL, TextAttributes.REGULAR, TextAttributes.BOLD)),
						new PageFooter(""));
				final com.ontimize.printing.TableReportElement htmlTable = new com.ontimize.printing.TableReportElement(
						PrintingWindow.this.ontimizeTable.getKeyFieldName(), 100, 100,
						false, PrintingWindow.this.ontimizeTable.getPrintingData(vSortColumns), vSortColumns,
						PrintingWindow.this.ontimizeTable.getPrintingFontSize());
				int checkedNumber = 0;
				for (int i = 0; i < PrintingWindow.this.listCheckBox.size(); i++) {
					if (((JCheckBox) PrintingWindow.this.listCheckBox.get(i)).isSelected()) {
						checkedNumber++;
					}
				}
				if (checkedNumber != 0) {
					int index = 0;
					final String[] sumColumns = new String[checkedNumber];
					for (int i = 0; i < PrintingWindow.this.listCheckBox.size(); i++) {
						if (((JCheckBox) PrintingWindow.this.listCheckBox.get(i)).isSelected()) {
							sumColumns[index] = ((JCheckBox) PrintingWindow.this.listCheckBox.get(i)).getName();
							index++;
						}
					}
					htmlTable.sumColumns(sumColumns);
				}
				if (PrintingWindow.this.progressBar != null) {
					PrintingWindow.this.progressBar.setValue(
							PrintingWindow.this.progressBar.getValue() + ((PrintingWindow.this.progressBar.getMaximum()
									- PrintingWindow.this.progressBar.getValue()) / 2));
				}
				if (PrintingWindow.this.progressBar != null) {
					PrintingWindow.this.progressBar.paintImmediately(0, 0, PrintingWindow.this.progressBar.getWidth(),
							PrintingWindow.this.progressBar.getHeight());
				}
				try {
					PrintingWindow.this.inf.addReportElement(htmlTable);
				} catch (final Exception e) {
					PrintingWindow.logger.error(null, e);
				} catch (final OutOfMemoryError e) {
					PrintingWindow.logger.trace(null, e);
					PrintingWindow.this.ontimizeTable.getParentForm()
					.message("table.memory_error", Form.ERROR_MESSAGE, e);
					try {
						PrintingWindow.this.inf.free();
						PrintingWindow.this.free();
						PrintingWindow.this.dispose();
					} catch (final Exception ex) {
						PrintingWindow.logger.error(null, ex);
					}
					return;
				}
				if (PrintingWindow.this.progressBar != null) {
					PrintingWindow.this.progressBar.setValue(PrintingWindow.this.progressBar.getMaximum() - 1);
				}
				if (PrintingWindow.this.progressBar != null) {
					PrintingWindow.this.progressBar.paintImmediately(0, 0, PrintingWindow.this.progressBar.getWidth(),
							PrintingWindow.this.progressBar.getHeight());
				}
				PrintingWindow.this.inf.finishReport();
				PrintingWindow.this.setVisible(false);
				PrintingWindow.this.inf.setVisible(true);
				try {
					PrintingWindow.this.free();
				} catch (final Exception e) {
					PrintingWindow.logger.trace(null, e);
				}
				PrintingWindow.this.dispose();
			}
		});
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			try {
				this.free();
			} catch (final Exception e2) {
				if (ApplicationManager.DEBUG) {
					PrintingWindow.logger.debug("Error while trying to free memory: ", e2);
				} else {
					PrintingWindow.logger.trace("Error while trying to free memory: ", e2);
				}
			}
		}
	}


	@Override
	public void free() {
		super.free();
		this.inf = null;
		this.listCheckBox.clear();
		this.listCheckBoxPrinting.clear();
		this.pageFormat = null;
		this.listCheckBox.clear();
		this.panelNorth.removeAll();
		this.panelSouth.removeAll();
		this.panelCenter.removeAll();
		this.labelTitle = null;
		this.titleTextField = null;
		this.panelNorth = null;
		this.panelCenter = null;
		this.panelCheckBox = null;
		this.listCheckBox = null;
		this.progressBar = null;
		this.panelSouth = null;
		this.buttonPageSetup = null;
		this.buttonPrint = null;
		this.pageFormat = null;
		if (ApplicationManager.DEBUG) {
			System.out.println(this.getClass().toString() + " : free");
		}
	}

}
