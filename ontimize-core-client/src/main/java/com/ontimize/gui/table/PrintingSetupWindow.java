package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterAbortException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.swing.list.I18nListCellRenderer;

/**
 * This class defines the setup window used to print out the table information.
 */
public class PrintingSetupWindow extends EJDialog implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(PrintingSetupWindow.class);

	protected static final String PAGE_LENGTH_TOO_SHORT = "table.page_length_too_short";

	private final JCheckBox paintGrid = new JCheckBox(ApplicationManager.getTranslation("M_CHECK_BOX_PAINT_GRID"),
			true);

	private final JCheckBox remakLines = new JCheckBox(ApplicationManager.getTranslation("M_CHECK_REMARK_LINES"),
			false);

	private final JCheckBox paintRowNumbers = new JCheckBox(ApplicationManager.getTranslation("M_PAINT_ROW_NUMBERS"),
			false);

	private Table ta = null;

	private PrintableTable printableTable = null;

	private PageFormat pf = null;

	private final JPanel buttonsPanel = new JPanel();

	private final JButton buttonPrint = new JButton();

	private final JButton buttonOrderColumns = new JButton();

	private final double scale = 1.0;

	private int totalPagesNumber = 0;

	private JPanel panelInfo = new JPanel();

	private final JButton buttonPageSetup = new JButton();

	private final JLabel informationPages = new JLabel();

	private final JPanel optionPanel = new JPanel(new GridLayout(0, 1));

	private List checkBoxListToPrint = new Vector();

	private final JTextField titleText = new JTextField();

	private final JLabel informationTitle = new JLabel();

	private final JLabel informationSort = new JLabel();

	private final JLabel stateLabel = new JLabel();

	private List printingVector = new Vector();

	private final JComboBox comboSort = new JComboBox();

	private List columnOrderId = new Vector();

	private final JButton buttonPreview = new JButton();

	private final JButton buttonColumnsToPrint = new JButton();

	private JDialog preview = null;

	private Page page = null;

	private JPanel pagePanel = null;

	private JScrollPane scroll = null;

	private double currentZoom = 1.0;

	private JDialog orderColumnVector = null;

	private JDialog printingColumnVector = null;

	private JList columnList = null;

	private JButton buttonUp = null;

	private JButton buttonDown = null;

	private final int MIN_PAGE_HEIGHT = 50;

	private final JButton buttonAcceptColToPrint = new JButton("application.accept");

	private final JButton buttonCancelColToPrint = new JButton("application.cancel");

	private List auxPrintingVector = null;

	private JLabel lInfoOrdenCols = null;

	private JLabel informationColumn = null;

	private final String keyPaintGrid = "table.paint_grid";

	private final String remarkLinesKey = "report.remark_line";

	private final String keyIncludeRowNumber = "table.include_row_number";

	private final String titleKey = "table.printing_setup";

	private final String keyAccept = "application.accept";

	private final String keyCancel = "application.cancel";

	private final String columnsOrderKey = "table.column_order";

	private final String keyInfoColumnsOrder = "table.column_order_info";

	// First column in the list corresponds with the first column in the page

	private final String pagesTitleKey = "PagesTitle";

	private final String sortByKey = "sort_by";

	private final String keyPrintingColumns = "table.columns_to_print";

	private final String keyTableFit = "table.the_table_fits_in_the_page";

	private final String keyTableNotFit = "table.the_table_does_not_fit_in_the_page";

	private final String previewKey = "preview";

	private final String keyPageSetup = "table.page_setup";

	private final String keyPrint = "print";

	private final String keyPrinting = "Printing...";

	private final String documentPageNumberKey = "table.the_document_to_print_has_?_pages";

	private final String keyFitTableSize = "table.adjusting_table_size";

	public PrintingSetupWindow(final Dialog d, final Table t) throws Exception {
		super(d, "table.printing_setup", true);
		this.init(t);
	}

	public void printDefault(final String title) {
		final Thread tImpresion = new Thread() {

			@Override
			public void run() {
				try {

					this.setPriority(Thread.MIN_PRIORITY);
					PrintingSetupWindow.this.printableTable.setPageTitles(title);

					PrintingSetupWindow.this.setVisible(false);

					PrintingSetupWindow.this.printableTable.print(title);
				} catch (final PrinterAbortException ex) {
					PrintingSetupWindow.logger.error(null, ex);
					PrintingSetupWindow.this.ta.getParentForm()
					.message(Table.M_PRINTING_CANCELED, Form.INFORMATION_MESSAGE, ex);
					PrintingSetupWindow.this.buttonPrint.setEnabled(true);
				} catch (final Exception e) {
					PrintingSetupWindow.logger.error(null, e);
					PrintingSetupWindow.this.ta.getParentForm()
					.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
					PrintingSetupWindow.this.buttonPrint.setEnabled(true);
				} finally {
				}
			}
		};
		tImpresion.start();
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if ((this.printableTable != null) && !this.printableTable.isPrinting()) {
				if (this.printableTable.printingProgressWindow != null) {
					this.printableTable.printingProgressWindow.setVisible(false);
				}
			}
			super.processWindowEvent(e);
		} else {
			super.processWindowEvent(e);
		}
	}

	private void changeColumnOrder() {
		if (this.orderColumnVector == null) {
			this.orderColumnVector = new JDialog(this, ApplicationManager.getTranslation("table.column_order"), true);
			this.orderColumnVector.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			this.lInfoOrdenCols = new JLabel(
					ApplicationManager.getTranslation(this.keyInfoColumnsOrder, this.ta.resourcesFile));
			this.lInfoOrdenCols.setFont(this.lInfoOrdenCols.getFont().deriveFont(Font.BOLD));
			this.orderColumnVector.getContentPane().add(this.lInfoOrdenCols, BorderLayout.NORTH);
			this.columnList = new JList();
			this.columnList.setCellRenderer(new I18nListCellRenderer(this.ta.resourcesFile));
			this.orderColumnVector.getRootPane().setBorder(new EtchedBorder(EtchedBorder.RAISED));
			final JPanel jButtonsPanel = new JPanel(new GridLayout(0, 1));
			this.buttonUp = new JButton();
			this.buttonDown = new JButton();
			jButtonsPanel.add(this.buttonUp);
			jButtonsPanel.add(this.buttonDown);
			final JPanel panelAux = new JPanel();
			panelAux.add(jButtonsPanel);
			this.orderColumnVector.getContentPane().add(panelAux, BorderLayout.EAST);
			this.orderColumnVector.getContentPane().add(new JScrollPane(this.columnList));

			final ImageIcon upIcon = ImageManager.getIcon(ImageManager.UP);
			if (upIcon != null) {
				this.buttonUp.setIcon(upIcon);
			} else {
				this.buttonUp.setText("Up");
			}

			final ImageIcon downIcon = ImageManager.getIcon(ImageManager.DOWN);
			if (downIcon != null) {
				this.buttonDown.setIcon(downIcon);
			} else {
				this.buttonDown.setText("Down");
			}

			this.buttonDown.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int index = PrintingSetupWindow.this.columnList.getSelectedIndex();
					final DefaultListModel model = (DefaultListModel) PrintingSetupWindow.this.columnList.getModel();
					if ((index >= 0) && (index < (model.getSize() - 1))) {
						final Object act = model.get(index);
						final Object sig = model.get(index + 1);
						model.setElementAt(sig, index);
						model.setElementAt(act, index + 1);
						PrintingSetupWindow.this.columnList.setSelectedIndex(index + 1);
						PrintingSetupWindow.this.printableTable.moveColumnRight(act);
					}
				}
			});

			this.buttonUp.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					final int index = PrintingSetupWindow.this.columnList.getSelectedIndex();
					final DefaultListModel model = (DefaultListModel) PrintingSetupWindow.this.columnList.getModel();
					if (index > 0) {
						final Object act = model.get(index);
						model.remove(index);
						model.insertElementAt(act, index - 1);
						PrintingSetupWindow.this.columnList.setSelectedIndex(index - 1);
						PrintingSetupWindow.this.printableTable.moveColumnLeft(act);
					}
				}
			});
		}

		final DefaultListModel model = new DefaultListModel();
		final List colsOrder = this.printableTable.getColumnsToPrintingByOrder();
		for (int i = 0; i < colsOrder.size(); i++) {
			model.insertElementAt(colsOrder.get(i), i);
		}
		this.columnList.setModel(model);
		this.orderColumnVector.pack();
		ApplicationManager.center(this.orderColumnVector);
		this.orderColumnVector.setVisible(true);
	}

	private void init(final Table t) throws Exception {

		if (ApplicationManager.DEBUG) {
			PrintingSetupWindow.logger.debug("Starting PrintingSetupWindow");
		}
		if (Table.DEBUG_MEMORY) {
			PrintingSetupWindow.logger
			.debug("PrintingSetupWindow.init(): Memory used: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0)
					+ " kbytes");
		}
		final long t1 = System.currentTimeMillis();
		this.ta = new Table(t.getParameters());
		this.ta.setParentFrame(t.parentFrame);
		this.ta.setParentForm(t.getParentForm());
		this.ta.setResourceBundle(t.resourcesFile);
		this.ta.setValue(t.getShownValue());
		this.ta.sortBy(t.getOrderColumn(), t.getAscending());
		final List visibleCols = t.getVisibleColumns();

		for (int i = 0; i < visibleCols.size(); i++) {
			final String columnName = visibleCols.get(i).toString();
			if (ExtendedTableModel.ROW_NUMBERS_COLUMN.equals(columnName)) {
				continue;
			}
			final TableCellRenderer renderer = t.getRendererForColumn(columnName);
			if (renderer != null) {
				Object clon = null;
				try {
					final Method method = renderer.getClass().getMethod("clone", null);
					clon = method.invoke(renderer, null);
				} catch (final Exception e) {
					PrintingSetupWindow.logger.trace(null, e);
				}
				if ((clon != null) && (clon instanceof TableCellRenderer)) {
					this.ta.setRendererForColumnExp(columnName, (TableCellRenderer) clon);
				}
			}
		}
		this.ta.setFont(this.ta.getFont().deriveFont((float) 10));

		if (ApplicationManager.DEBUG_TIMES) {
			PrintingSetupWindow.logger
			.debug("PrintingSetupWindow: configuration table time" + (System.currentTimeMillis() - t1));
		}

		if (Table.DEBUG_MEMORY) {
			PrintingSetupWindow.logger.debug("init: create table: Memory used: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0) + " kbytes");
		}

		this.printableTable = new PrintableTable(this.ta, true);

		if (Table.DEBUG_MEMORY) {
			PrintingSetupWindow.logger
			.debug("init: created printabletable: Memory used: "
					+ ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024.0)
					+ " kbytes");
		}

		this.pf = this.printableTable.getPageFormat();
		// Create the graphic interface
		this.buttonsPanel.add(this.buttonPrint);
		this.buttonsPanel.add(this.buttonPreview);
		this.buttonsPanel.add(this.buttonPageSetup);
		this.buttonsPanel.add(this.buttonOrderColumns);
		this.buttonsPanel.add(this.buttonColumnsToPrint);
		this.installButtonListeners();
		this.panelInfo = new JPanel(new BorderLayout());
		this.optionPanel.add(this.paintGrid);
		this.remakLines.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.printableTable.setRemarkLine(PrintingSetupWindow.this.remakLines.isSelected());
			}
		});
		this.paintRowNumbers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.printableTable
				.setPaintRowNumber(PrintingSetupWindow.this.paintRowNumbers.isSelected());
			}
		});
		this.optionPanel.add(this.remakLines);
		this.optionPanel.add(this.paintRowNumbers);
		this.informationTitle.setText("Page title: ");
		this.optionPanel.add(this.informationTitle);
		this.optionPanel.add(this.titleText);
		this.informationSort.setText("Order by:");
		this.optionPanel.add(this.informationSort);
		this.optionPanel.add(this.comboSort);
		this.comboSort.setSelectedIndex(-1);
		this.titleText.setColumns(20);
		this.titleText.setDocument(new com.ontimize.gui.field.document.LimitedTextDocument(50));

		this.printingColumnVector = new EJDialog(this, "Printing columnms", true);

		final JPanel panelCheckBox = new JPanel(new GridLayout(0, 1));
		this.informationColumn = new JLabel("Printing columns: ");
		this.informationColumn.setFont(this.informationColumn.getFont().deriveFont(Font.BOLD));
		panelCheckBox.add(this.informationColumn);
		final JScrollPane scrollColumns = new JScrollPane(panelCheckBox);
		final JPanel pBot = new JPanel();
		pBot.add(this.buttonAcceptColToPrint);
		pBot.add(this.buttonCancelColToPrint);
		this.printingColumnVector.getContentPane().add(pBot, BorderLayout.SOUTH);
		this.printingColumnVector.getContentPane().add(scrollColumns);

		for (int i = 0; i < t.visibleColumns.size(); i++) {
			final String column = t.visibleColumns.get(i).toString();
			String columnName = t.visibleColumns.get(i).toString();
			try {
				if (this.ta.resourcesFile != null) {
					columnName = this.ta.resourcesFile.getString(columnName);
				}
			} catch (final Exception e) {
				PrintingSetupWindow.logger.trace(null, e);
			}
			try {
				this.comboSort.addItem(columnName);
				this.columnOrderId.add(column);
				final JCheckBox check = new JCheckBox(columnName);
				check.setName(column);
				this.printingVector.add(column);

				check.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						if (e.getSource() instanceof JCheckBox) {
							final boolean bSelected = ((JCheckBox) e.getSource()).isSelected();
							if (bSelected) {
								if (!PrintingSetupWindow.this.auxPrintingVector
										.contains(((JCheckBox) e.getSource()).getName())) {
									PrintingSetupWindow.this.auxPrintingVector
									.add(((JCheckBox) e.getSource()).getName());
								}
							} else {
								PrintingSetupWindow.this.auxPrintingVector
								.remove(((JCheckBox) e.getSource()).getName());
							}
						}
					}
				});
				check.setSelected(true);
				this.checkBoxListToPrint.add(check);
				panelCheckBox.add(check);
				continue;
			} catch (final Exception e) {
				PrintingSetupWindow.logger.trace(null, e);
			}
		}

		this.printingColumnVector.pack();
		ApplicationManager.center(this.printingColumnVector);
		this.printableTable.setPrintingColumns(this.printingVector, false);
		this.panelInfo.add(this.informationPages, BorderLayout.NORTH);
		this.informationPages.setFont(this.informationPages.getFont().deriveFont(Font.BOLD));
		this.panelInfo.add(this.optionPanel, BorderLayout.CENTER);
		final ImageIcon pageIcon = ImageManager.getIcon(ImageManager.PAGE);
		if (pageIcon != null) {
			this.buttonPageSetup.setIcon(pageIcon);
		} else {
			this.buttonPageSetup.setText("Configure page");
		}

		final ImageIcon printIcon = ImageManager.getIcon(ImageManager.PRINT);
		if (printIcon != null) {
			this.buttonPrint.setIcon(printIcon);
		} else {
			this.buttonPrint.setText("print");
		}

		final ImageIcon previewIcon = ImageManager.getIcon(ImageManager.PREVIEW);
		if (previewIcon != null) {
			this.buttonPreview.setIcon(previewIcon);
		} else {
			this.buttonPreview.setText("Preview");
		}

		final ImageIcon columnsOrderIcon = ImageManager.getIcon(ImageManager.COLUMNS_ORDER);
		if (columnsOrderIcon != null) {
			this.buttonOrderColumns.setIcon(columnsOrderIcon);
		} else {
			this.buttonOrderColumns.setText("Columns sort");
		}

		final ImageIcon selectPrintColumnsIcon = ImageManager.getIcon(ImageManager.SELECT_PRINT_COLUMNS);
		if (selectPrintColumnsIcon != null) {
			this.buttonColumnsToPrint.setIcon(selectPrintColumnsIcon);
		} else {
			this.buttonColumnsToPrint.setText("Columns to print");
		}

		this.getContentPane().add(this.buttonsPanel, BorderLayout.NORTH);
		this.getContentPane().add(this.stateLabel, BorderLayout.SOUTH);
		this.getContentPane().add(new JScrollPane(this.panelInfo), BorderLayout.CENTER);
		final long t2 = System.currentTimeMillis();
		if (ApplicationManager.DEBUG_TIMES) {
			PrintingSetupWindow.logger.debug("Table: PrintingSetupWindow contruction time: " + (t2 - t1));
		}
		this.fitTableToPage();
		this.pack();
		ApplicationManager.center(this);
	}

	public PrintingSetupWindow(final Frame f, final Table t) throws Exception {
		super(f, "Print configuration", true);
		this.init(t);
	}

	private void fitTableToPage() {
		// ta.setFont(ta.getFont().deriveFont((float)10));
		if (this.printableTable.printingProgressWindow != null) {
			this.printableTable.printingProgressWindow.setVisible(true);
			this.printableTable.printingProgressWindow.toFront();
			this.printableTable.printingProgressWindow
			.setStateText(ApplicationManager.getTranslation(this.keyFitTableSize, this.ta.resourcesFile));
			// Adjusting font size
		}
		if (this.pf.getImageableHeight() < this.MIN_PAGE_HEIGHT) {
			this.stateLabel.setText(ApplicationManager.getTranslation(PrintingSetupWindow.PAGE_LENGTH_TOO_SHORT));
			this.stateLabel.setForeground(Color.red);
			return;
		}
		try {
			// If font size is yet 6 and it is not small enough, finish.

			this.printableTable.setFontSize(10);

			this.totalPagesNumber = this.printableTable.getPagesNumber();
			if (!this.printableTable.fitInPage()) {
				final int totalWidth = this.printableTable.getTotalWidth();
				final int availableWidth = this.printableTable.getAvailableWidth();
				if (totalWidth > (availableWidth * (10 / 8))) {
					this.printableTable.setFontSize(6);
					this.totalPagesNumber = this.printableTable.getPagesNumber();
					if (!this.printableTable.fitInPage()) {
						this.stateLabel
						.setText(ApplicationManager.getTranslation(this.keyTableNotFit, this.ta.resourcesFile));
						// There are no space in the page for the table

						this.stateLabel.setForeground(Color.red);
					} else {
						this.stateLabel
						.setText(ApplicationManager.getTranslation(this.keyTableFit, this.ta.resourcesFile));
						// Table fits in the page
						this.stateLabel.setForeground(Color.black);
					}
				} else {
					this.printableTable.setFontSize(8);
					this.totalPagesNumber = this.printableTable.getPagesNumber();
					if (!this.printableTable.fitInPage()) {
						this.printableTable.setFontSize(6);
						this.totalPagesNumber = this.printableTable.getPagesNumber();
						if (!this.printableTable.fitInPage()) {
							this.stateLabel
							.setText(ApplicationManager.getTranslation(this.keyTableNotFit, this.ta.resourcesFile));
							// Table does not fit in the page
							this.stateLabel.setForeground(Color.red);
						} else {
							this.stateLabel
							.setText(ApplicationManager.getTranslation(this.keyTableFit, this.ta.resourcesFile));
							// The table fits in the page
							this.stateLabel.setForeground(Color.black);
						}
					} else {
						this.stateLabel
						.setText(ApplicationManager.getTranslation(this.keyTableFit, this.ta.resourcesFile));
						// The table fits int the page
						this.stateLabel.setForeground(Color.black);
					}
				}
			} else {
				this.stateLabel.setText(ApplicationManager.getTranslation(this.keyTableFit, this.ta.resourcesFile));
				// The table fits in the page
				this.stateLabel.setForeground(Color.black);
			}

			this.informationPages
			.setText(ApplicationManager.getTranslation(this.documentPageNumberKey, this.ta.resourcesFile,
					new Object[] { new Integer(this.totalPagesNumber) }));

			if (this.printableTable.printingProgressWindow != null) {
				this.printableTable.printingProgressWindow.toBack();
			}

			this.printableTable.setTablePageWidth();
		} catch (final OutOfMemoryError error) {
			PrintingSetupWindow.logger.error("Memory Error: Trying to free", error);
			this.dispose();
			MessageDialog.showMessage(this, "table.memory_error", Form.ERROR_MESSAGE, null);
			this.setVisible(false);
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		if (this.printableTable != null) {
			try {
				this.printableTable.dispose();
			} catch (final Exception ex) {
				PrintingSetupWindow.logger.trace(null, ex);
			}
		}
		this.printableTable = null;
		this.ta = null;
		this.pf = null;
		if (this.checkBoxListToPrint != null) {
			this.checkBoxListToPrint.clear();
		}
		this.checkBoxListToPrint = null;
		if (this.printingVector != null) {
			this.printingVector.clear();
		}
		this.printingVector = null;
		if (this.columnOrderId != null) {
			this.columnOrderId.clear();
		}
		this.columnOrderId = null;
		this.columnList = null;
	}

	private void setPageZoom(final double zoom) {
		this.page.dispose();
		final BufferedImage pageImage = new BufferedImage(
				(int) ((int) ((this.pf.getImageableX() * 2) + this.pf.getImageableWidth()) * this.currentZoom),
				(int) ((int) ((this.pf.getImageableY() * 2) + this.pf.getImageableHeight()) * this.currentZoom),
				BufferedImage.TYPE_3BYTE_BGR);
		this.page.setImage(pageImage);
		final Graphics g = pageImage.getGraphics();
		this.printableTable.setPageTitles(this.titleText.getText());
		this.printableTable.setPaintGrid(this.paintGrid.isSelected());
		g.setColor(Color.white);
		g.fillRect(0, 0, pageImage.getWidth(), pageImage.getHeight());
		g.setColor(Color.black);
		this.printableTable.printInPage(g, this.pf, 0, false, this.currentZoom);
		this.scroll.doLayout();
		this.preview.validate();
	}

	private void preview() {
		this.currentZoom = 1.0;
		if (this.preview == null) {
			this.preview = new EJDialog(this, ApplicationManager.getTranslation(this.previewKey, this.ta.resourcesFile),
					true);
			this.preview.setCursor(ApplicationManager.getZoomCursor());
			this.page = new Page(null);
			this.page.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent mEvent) {
					if (mEvent.getClickCount() == 1) {
						if (mEvent.getModifiers() == InputEvent.BUTTON1_MASK) {
							if (PrintingSetupWindow.this.currentZoom < 2) {
								if (PrintingSetupWindow.this.page != null) {
									PrintingSetupWindow.this.currentZoom = PrintingSetupWindow.this.currentZoom * 2;
									PrintingSetupWindow.this.setPageZoom(PrintingSetupWindow.this.currentZoom);
								}
							}
						} else {
							if (PrintingSetupWindow.this.currentZoom > 0.5) {
								if (PrintingSetupWindow.this.page != null) {
									PrintingSetupWindow.this.currentZoom = PrintingSetupWindow.this.currentZoom / 2;
									PrintingSetupWindow.this.setPageZoom(PrintingSetupWindow.this.currentZoom);
								}
							}
						}
					}
				}
			});
			this.pagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			this.pagePanel.add(this.page);
			this.scroll = new JScrollPane(this.pagePanel);
			this.scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
			this.preview.getContentPane().add(this.scroll);
			this.preview.pack();

		}

		this.printableTable.setPageTitles(this.titleText.getText());
		final int ind = this.comboSort.getSelectedIndex();
		Object oSort = null;

		if (ind >= 0) {
			oSort = this.columnOrderId.get(ind);
		}
		if (oSort != null) {
			this.printableTable.table.sortBy(oSort.toString(), true);
		}

		this.setPageZoom(this.currentZoom);
		this.preview.pack();
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		final Dimension dV = new Dimension();
		if (this.preview.getWidth() >= (d.width - 50)) {
			dV.width = d.width - 50;
		} else {
			dV.width = this.preview.getWidth();
		}
		if (this.preview.getHeight() >= (d.height - 100)) {
			dV.height = d.height - 100;
		} else {
			dV.height = this.preview.getHeight();
		}
		this.preview.setSize(dV);
		ApplicationManager.center(this.preview);
		this.preview.setVisible(true);
	}

	private void installButtonListeners() {

		this.buttonPageSetup
		.setToolTipText(ApplicationManager.getTranslation(this.keyPageSetup, this.ta.resourcesFile));
		this.buttonPrint.setToolTipText(ApplicationManager.getTranslation(this.keyPrint, this.ta.resourcesFile));
		this.buttonPreview.setToolTipText(ApplicationManager.getTranslation(this.previewKey, this.ta.resourcesFile));
		this.buttonOrderColumns
		.setToolTipText(ApplicationManager.getTranslation(this.columnsOrderKey, this.ta.resourcesFile));
		this.buttonColumnsToPrint
		.setToolTipText(ApplicationManager.getTranslation(this.keyPrintingColumns, this.ta.resourcesFile));

		this.buttonPageSetup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final int ind = PrintingSetupWindow.this.comboSort.getSelectedIndex();
				Object ord = null;

				if (ind >= 0) {
					ord = PrintingSetupWindow.this.columnOrderId.get(ind);
				}
				if (ord != null) {
					PrintingSetupWindow.this.printableTable.table.sortBy(ord.toString(), true);
				}
				// Checks the columns
				PrintingSetupWindow.this.printableTable.setPaintGrid(PrintingSetupWindow.this.paintGrid.isSelected());
				PrintingSetupWindow.this.printableTable.setPageTitles(PrintingSetupWindow.this.titleText.getText());
				PrintingSetupWindow.this.totalPagesNumber = PrintingSetupWindow.this.printableTable.configurePage();
				PrintingSetupWindow.this.pf = PrintingSetupWindow.this.printableTable.getPageFormat();
				PrintingSetupWindow.this.fitTableToPage();
				// Clear the image
				if (PrintingSetupWindow.this.page != null) {
					PrintingSetupWindow.this.page.dispose();
					final BufferedImage pageImage = new BufferedImage(
							(int) ((PrintingSetupWindow.this.pf.getImageableX() * 2)
									+ PrintingSetupWindow.this.pf.getImageableWidth()),
							(int) ((PrintingSetupWindow.this.pf.getImageableY() * 2)
									+ PrintingSetupWindow.this.pf.getImageableHeight()),
							BufferedImage.TYPE_3BYTE_BGR);
					PrintingSetupWindow.this.page.setImage(pageImage);
					PrintingSetupWindow.this.scroll.doLayout();
				}
			}
		});
		this.buttonPreview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.preview();
			}
		});

		this.buttonOrderColumns.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.changeColumnOrder();
			}
		});

		this.buttonAcceptColToPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.printingVector = ObjectTools.clone(PrintingSetupWindow.this.auxPrintingVector);
				PrintingSetupWindow.this.printableTable.setPrintingColumns(PrintingSetupWindow.this.printingVector);

				PrintingSetupWindow.this.fitTableToPage();
				PrintingSetupWindow.this.printingColumnVector.setVisible(false);
			}
		});

		this.buttonCancelColToPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.auxPrintingVector = ObjectTools.clone(PrintingSetupWindow.this.printingVector);
				PrintingSetupWindow.this.printingColumnVector.setVisible(false);
			}
		});

		this.buttonColumnsToPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.auxPrintingVector = ObjectTools.clone(PrintingSetupWindow.this.printingVector);
				for (int i = 0; i < PrintingSetupWindow.this.checkBoxListToPrint.size(); i++) {
					if (PrintingSetupWindow.this.checkBoxListToPrint.get(i) instanceof JCheckBox) {
						final JCheckBox cb = (JCheckBox) PrintingSetupWindow.this.checkBoxListToPrint.get(i);
						if (PrintingSetupWindow.this.auxPrintingVector.contains(cb.getName())) {
							cb.setSelected(true);
						} else {
							cb.setSelected(false);
						}
					}
				}
				PrintingSetupWindow.this.printingColumnVector.setVisible(true);
			}
		});

		this.buttonPrint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				PrintingSetupWindow.this.buttonPrint.setEnabled(false);
				PrintingSetupWindow.this.buttonPageSetup.setEnabled(false);
				final Thread tImpresion = new Thread() {

					@Override
					public void run() {
						try {
							final int ind = PrintingSetupWindow.this.comboSort.getSelectedIndex();
							Object ord = null;
							this.setPriority(Thread.MIN_PRIORITY);
							if (ind >= 0) {
								ord = PrintingSetupWindow.this.columnOrderId.get(ind);
							}
							if (ord != null) {
								PrintingSetupWindow.this.printableTable.table.sortBy(ord.toString(), true);
							}
							// Checks the columns
							PrintingSetupWindow.this.printableTable
							.setPaintGrid(PrintingSetupWindow.this.paintGrid.isSelected());
							PrintingSetupWindow.this.printableTable
							.setPageTitles(PrintingSetupWindow.this.titleText.getText());
							if (PrintingSetupWindow.this.printingVector.isEmpty()) {
								PrintingSetupWindow.this.ta.getParentForm()
								.message("table.no_column_selected", Form.ERROR_MESSAGE);
								return;
							}

							PrintingSetupWindow.this.buttonPrint.setEnabled(true);
							PrintingSetupWindow.this.buttonPageSetup.setEnabled(true);
							PrintingSetupWindow.this.setVisible(false);

							if (PrintingSetupWindow.this.printableTable.printingProgressWindow != null) {
								PrintingSetupWindow.this.printableTable.printingProgressWindow.setVisible(true);
								PrintingSetupWindow.this.printableTable.printingProgressWindow.toFront();
								PrintingSetupWindow.this.printableTable.printingProgressWindow
								.setStateText(
										ApplicationManager.getTranslation(PrintingSetupWindow.this.keyPrinting,
												PrintingSetupWindow.this.ta.resourcesFile));
							}
							PrintingSetupWindow.this.printableTable.print();
						} catch (final PrinterAbortException ex) {
							PrintingSetupWindow.logger.error(null, ex);
							PrintingSetupWindow.this.ta.getParentForm()
							.message(Table.M_PRINTING_CANCELED, Form.INFORMATION_MESSAGE, ex);
							PrintingSetupWindow.this.buttonPrint.setEnabled(true);
						} catch (final Exception e) {
							PrintingSetupWindow.logger.error(null, e);
							PrintingSetupWindow.this.ta.getParentForm()
							.message(Table.M_ERROR_PRINTING_TABLE, Form.ERROR_MESSAGE, e);
							PrintingSetupWindow.this.buttonPrint.setEnabled(true);
						} finally {
						}
					}
				};
				tImpresion.start();
			}
		});
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		this.setTitle(ApplicationManager.getTranslation(this.titleKey, this.ta.resourcesFile));

		this.paintGrid.setText(ApplicationManager.getTranslation(this.keyPaintGrid, this.ta.resourcesFile));
		this.paintRowNumbers
		.setText(ApplicationManager.getTranslation(this.keyIncludeRowNumber, this.ta.resourcesFile));
		this.paintGrid.setText(ApplicationManager.getTranslation(this.keyPaintGrid, this.ta.resourcesFile));
		this.remakLines.setText(ApplicationManager.getTranslation(this.remarkLinesKey, this.ta.resourcesFile));

		this.buttonAcceptColToPrint.setText(ApplicationManager.getTranslation(this.keyAccept, this.ta.resourcesFile));
		this.buttonCancelColToPrint.setText(ApplicationManager.getTranslation(this.keyCancel, this.ta.resourcesFile));

		if (this.orderColumnVector != null) {
			this.orderColumnVector
			.setTitle(ApplicationManager.getTranslation(this.columnsOrderKey, this.ta.resourcesFile));
		}
		if (this.lInfoOrdenCols != null) {
			this.lInfoOrdenCols
			.setText(ApplicationManager.getTranslation(this.keyInfoColumnsOrder, this.ta.resourcesFile));
		}

		this.informationTitle.setText(ApplicationManager.getTranslation(this.titleKey, this.ta.resourcesFile));
		this.informationSort.setText(ApplicationManager.getTranslation(this.sortByKey, this.ta.resourcesFile));

		this.informationColumn
		.setText(ApplicationManager.getTranslation(this.keyPrintingColumns, this.ta.resourcesFile));

		if (this.printingColumnVector != null) {
			this.printingColumnVector
			.setTitle(ApplicationManager.getTranslation(this.keyPrintingColumns, this.ta.resourcesFile));
		}

		if (this.preview != null) {
			this.preview.setTitle(ApplicationManager.getTranslation(this.previewKey, this.ta.resourcesFile));
		}
		this.buttonPageSetup
		.setToolTipText(ApplicationManager.getTranslation(this.keyPageSetup, this.ta.resourcesFile));
		this.buttonPrint.setToolTipText(ApplicationManager.getTranslation(this.keyPrint, this.ta.resourcesFile));
		this.buttonPreview.setToolTipText(ApplicationManager.getTranslation(this.previewKey, this.ta.resourcesFile));
		this.buttonOrderColumns
		.setToolTipText(ApplicationManager.getTranslation(this.columnsOrderKey, this.ta.resourcesFile));
		this.buttonColumnsToPrint
		.setToolTipText(ApplicationManager.getTranslation(this.keyPrintingColumns, this.ta.resourcesFile));
	}

	@Override
	public void setComponentLocale(final Locale l) {
	}

	@Override
	public List getTextsToTranslate() {
		final List v = new Vector();
		return v;
	}

}
