package com.ontimize.printing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.io.FileWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.HTMLDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.images.ImageManager;

public class ReportFrame extends JFrame implements com.ontimize.gui.Freeable {

	private static final Logger logger = LoggerFactory.getLogger(ReportFrame.class);

	private static boolean DEBUG = false;

	protected List pages = new Vector();

	protected JScrollPane scrollPanel = new JScrollPane();

	protected JPanel pagesPanel = new JPanel();

	protected JPanel controlPanel = new JPanel(new FlowLayout());

	protected JButton previousButton = new JButton("<<");

	protected JButton nextButton = new JButton(">>");

	protected JButton saveButton = new JButton("save");

	protected JButton printingButton = new JButton();

	protected CardLayout pagesLayout = new CardLayout();

	protected PageFormat pageFormat = null;

	protected Page currentPage = null;

	protected boolean multipageReport = false;

	protected PageHeader header = null;

	protected PageFooter footer = null;

	public ReportFrame(final PageFormat pageFormat, final boolean multipage, final PageHeader pageHeader, final PageFooter pageFooter) {
		this(null, pageFormat, multipage, pageHeader, pageFooter);
	}

	public ReportFrame(final URL templateURL, final PageFormat formatoPag, final boolean multipage, final PageHeader pageHeader,
			final PageFooter pageFooter) {
		this.multipageReport = multipage;
		this.pageFormat = formatoPag;
		this.header = pageHeader;
		this.footer = pageFooter;
		// GUI
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.controlPanel, BorderLayout.NORTH);
		this.getContentPane().add(this.scrollPanel);
		// Controls panel
		this.controlPanel.add(this.previousButton);
		this.controlPanel.add(this.nextButton);
		this.controlPanel.add(this.saveButton);
		final ImageIcon printIcon = ImageManager.getIcon(ImageManager.PRINT_HELP_UI);
		if (printIcon == null) {
			this.printingButton.setText("print");
		} else {
			this.printingButton.setIcon(printIcon);
		}
		this.controlPanel.add(this.printingButton);
		this.pagesPanel.setLayout(this.pagesLayout);
		this.scrollPanel.getViewport().add(this.pagesPanel);
		this.scrollPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		// Buttons events
		this.printingButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				ReportFrame.this.print();
			}
		});
		this.previousButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				ReportFrame.this.pagesLayout.previous(ReportFrame.this.pagesPanel);
			}
		});
		this.nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				ReportFrame.this.pagesLayout.next(ReportFrame.this.pagesPanel);
			}
		});
		this.saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				// Save the current page
				final JFileChooser fileChooser = new JFileChooser();
				final int iOption = fileChooser.showSaveDialog(ReportFrame.this);
				if (iOption == JFileChooser.APPROVE_OPTION) {
					try {
						final Object[] oPageList = new Object[ReportFrame.this.pages.size()];
						for (int i = 0; i < ReportFrame.this.pages.size(); i++) {
							oPageList[i] = new Integer(i + 1);
						}
						final Integer iSelectedPage = (Integer) JOptionPane.showInputDialog(ReportFrame.this, "Select page",
								"Report", JOptionPane.QUESTION_MESSAGE, null, oPageList,
								oPageList[0]);

						final FileWriter fWriter = new FileWriter(fileChooser.getSelectedFile());
						final HTMLDocument htmlDocument = (HTMLDocument) ((Page) ReportFrame.this.pages
								.get(iSelectedPage.intValue() - 1)).getDocument();
						((Page) ReportFrame.this.pages.get(iSelectedPage.intValue() - 1)).getHTMLEditor()
						.write(fWriter, htmlDocument, 0, htmlDocument.getLength());
						fWriter.flush();
						fWriter.close();
					} catch (final Exception e) {
						ReportFrame.logger.error("Error saving page", e);
					}
				}
			}
		});
		this.addPage(templateURL);
		this.scrollPanel.setPreferredSize(
				new Dimension(this.scrollPanel.getPreferredSize().width, (int) this.pageFormat.getImageableHeight()));
		this.pack();
		// Center
		final Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((d.width - this.getSize().width) / 2, (d.height - this.getSize().height) / 2);
	}

	/**
	 * Adds an element to the report
	 * @param reportElement
	 */
	public void addReportElement(final ReportElement reportElement) {
		try {
			reportElement.insert(this, true);
			try {
				if (reportElement instanceof TableReportElement) {
					// TEMPORAL: View attempt
					final Element tableElement = this.currentPage.getElementById(((TableReportElement) reportElement).getId())
							.getElement(0);
					if (ReportFrame.DEBUG) {
						final View vView = this.currentPage.getHTMLEditor().getViewFactory().create(tableElement);
						final AttributeSet attributes = vView.getAttributes();
						final Enumeration enumAttributeNames = attributes.getAttributeNames();
						ReportFrame.logger.debug("Table attributes: ");
						while (enumAttributeNames.hasMoreElements()) {
							final Object oName = enumAttributeNames.nextElement();
							ReportFrame.logger.debug(oName.toString());
							ReportFrame.logger.debug(attributes.getAttribute(oName).toString());
						}
					}
				}
			} catch (final Exception e2) {
				ReportFrame.logger.error("Error checking attributes. ", e2);
			}
		} catch (final Exception e) {
			ReportFrame.logger.error("Inserting in report: ", e);
		}
	}

	/**
	 * Adds a new element to the report using a template.
	 * @param reportElement
	 * @param identifier Attribute of the place in the template to insert the new element
	 */
	public void addReportElement(final ReportElement reportElement, final String identifier) {
		try {
			reportElement.insert(this, identifier, true);
			try {
				if (reportElement instanceof TableReportElement) {
					final Element tableElement = this.currentPage.getElementById(((TableReportElement) reportElement).getId())
							.getElement(0);
					if (ReportFrame.DEBUG) {
						final View view = this.currentPage.getHTMLEditor().getViewFactory().create(tableElement);
						final AttributeSet attributes = view.getAttributes();
						final Enumeration enumAttributeNames = attributes.getAttributeNames();
						ReportFrame.logger.debug("Table attributes: ");
						while (enumAttributeNames.hasMoreElements()) {
							final Object oName = enumAttributeNames.nextElement();
							ReportFrame.logger.debug(oName.toString());
							ReportFrame.logger.debug(attributes.getAttribute(oName).toString());
						}
					}
				}
			} catch (final Exception e2) {
				ReportFrame.logger.error("Error checking attributes. ", e2);
			}
		} catch (final Exception e) {
			ReportFrame.logger.error("Inserting in the report: ", e);
		}
	}

	public void insertImage(final String identifier, final String name) {
		// Insert pair key - value
		// Gets the html document element with the identifier
		final Element element = this.currentPage.getElementById(identifier);
		if (element != null) {
			try {
				final String sHTMLString = "<IMG src='" + name + "'></IMG>";
				this.currentPage.getHTMLDocument().insertBeforeEnd(element, sHTMLString);
			} catch (final Exception e) {
				ReportFrame.logger.trace(null, e);
			}
		}
	}

	public void insertImage(final String identifier, final String name, final int width, final int height) {
		final Element element = this.currentPage.getElementById(identifier);
		if (element != null) {
			try {
				String sHTMLString = null;
				if (width == 0) {
					sHTMLString = "<IMG src='" + name + "' width='auto' height='" + Integer.toString(height)
					+ "'></IMG>";
				} else {
					sHTMLString = "<IMG src='" + name + "' width='" + Integer.toString(width) + "' height='"
							+ Integer.toString(height) + "'></IMG>";
				}
				this.currentPage.getHTMLDocument().insertBeforeEnd(element, sHTMLString);
			} catch (final Exception e) {
				ReportFrame.logger.trace(null, e);
			}
		}
	}

	protected void addPage(final URL templateURL) {
		// Set the page number
		try {
			if (this.footer != null) {
				this.footer.setPageNumber(this.pages.size() + 1);
			}
			// If template exists:
			if (templateURL != null) {
				this.currentPage = new Page(this.pageFormat, templateURL, this.header, this.footer);
			} else {
				this.currentPage = new Page(this.pageFormat, this.header, this.footer);
			}
			this.pages.add(this.pages.size(), this.currentPage);
			this.pagesPanel.add(this.currentPage, Integer.toString(this.pages.size()));
			this.pagesLayout.show(this.pagesPanel, Integer.toString(this.pages.size()));
			final int width = this.currentPage.getWidth();
			final int height = this.currentPage.getHeight();
			this.currentPage.paintImmediately(0, 0, width, height);
		} catch (final Exception e) {
			ReportFrame.logger.error(null, e);
		}
	}

	public Page getCurrentPage() {
		return this.currentPage;
	}

	/**
	 * Method used to indicates that all the elements are ready and it is possible to format the last
	 * page
	 */
	public void finishReport() {
		// Get the last page
		final Page pLastPage = (Page) this.pages.get(this.pages.size() - 1);
		// Inserts to the end of the page to move the footer to the bottom
		try {
			int iPreviousOffset = pLastPage.getPageFooter().getStartOffset();
			while (!pLastPage.isFull()) {
				iPreviousOffset = pLastPage.getPageFooter().getStartOffset();
				pLastPage.getHTMLDocument().insertBeforeStart(pLastPage.getPageFooter(), "<BR>");
			}
		} catch (final Exception e) {
			ReportFrame.logger.error("Finishing report: ", e);
		}
	}

	public void print() {
		final Thread printingThread = new Thread() {

			@Override
			public void run() {
				final PrinterJob pj = PrinterJob.getPrinterJob();
				// Sets a pageable Book to print
				final Book book = new Book() {

					@Override
					public Printable getPrintable(final int index) {
						return (Printable) ReportFrame.this.pages.get(index);
					}
				};
				for (int i = 0; i < ReportFrame.this.pages.size(); i++) {
					book.append((Printable) ReportFrame.this.pages.get(i), ReportFrame.this.pageFormat);
				}
				pj.setPageable(book);
				try {
					final boolean bPrint = pj.printDialog();
					if (bPrint) {
						pj.print();
						JOptionPane.showMessageDialog(ReportFrame.this, "Printing job finished");
					} else {
						JOptionPane.showMessageDialog(ReportFrame.this, "Printing job canelled by user");
					}
				} catch (final Exception e) {
					ReportFrame.logger.trace(null, e);
					JOptionPane.showMessageDialog(ReportFrame.this, e.getMessage());
				}
			}
		};
		printingThread.setPriority(Thread.MIN_PRIORITY);
		printingThread.start();
	}

	@Override
	protected void processWindowEvent(final WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			try {
				this.free();
			} catch (final Exception e2) {
				if (ReportFrame.DEBUG) {
					ReportFrame.logger.error("Error in the free() method ", e2);
				} else {
					ReportFrame.logger.trace("Error in the free() method ", e2);
				}
			}
		}
	}

	@Override
	public void free() {
		for (int i = 0; i < this.pages.size(); i++) {
			this.pagesLayout.removeLayoutComponent((Component) this.pages.get(i));
		}
		this.pagesLayout = null;
		this.previousButton = null;
		this.saveButton = null;
		this.printingButton = null;
		this.nextButton = null;
		this.header = null;
		this.pageFormat = null;
		this.controlPanel = null;
		this.pagesPanel.removeAll();
		this.pagesPanel = null;
		this.currentPage = null;
		this.pages.clear();
		this.pages = null;
		if (ReportFrame.DEBUG) {
			ReportFrame.logger.debug(this.getClass().toString() + " : free");
		}
	}

}
