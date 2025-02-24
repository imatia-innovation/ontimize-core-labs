package com.ontimize.report.engine.dynamicjasper;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApToolBarSeparator;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.report.DefaultReportDialog;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.swing.JRViewerController;
import net.sf.jasperreports.swing.JRViewerToolbar;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.save.JRCsvSaveContributor;
import net.sf.jasperreports.view.save.JRDocxSaveContributor;
import net.sf.jasperreports.view.save.JREmbeddedImagesXmlSaveContributor;
import net.sf.jasperreports.view.save.JRHtmlSaveContributor;
import net.sf.jasperreports.view.save.JRMultipleSheetsXlsSaveContributor;
import net.sf.jasperreports.view.save.JROdtSaveContributor;
import net.sf.jasperreports.view.save.JRPdfSaveContributor;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;
import net.sf.jasperreports.view.save.JRRtfSaveContributor;
import net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor;
import net.sf.jasperreports.view.save.JRXmlSaveContributor;

public class CustomJasperViewerToolbar extends JRViewerToolbar implements Internationalization {

	private static final Logger logger = LoggerFactory.getLogger(CustomJasperViewerToolbar.class);

	public static final String TXT_GO_TO_PAGE_TOOLTIP_KEY = "jasperviewer.toolbar.txt_go_to_page.tooltip";

	public static final String BTN_SAVE_TOOLTIP_KEY = "jasperviewer.toolbar.btn_save.tooltip";

	public static final String BTN_ZOOM_IN_TOOLTIP_KEY = "jasperviewer.toolbar.btn_zoom_in.tooltip";

	public static final String BTN_ZOOM_OUT_TOOLTIP_KEY = "jasperviewer.toolbar.btn_zoom_out.tooltip";

	public static final String BTN_SEE_ALL_TOOLTIP_KEY = "jasperviewer.toolbar.btn_see_all.tooltip";

	public static final String BTN_PAPER_FIT_WIDTH_TOOLTIP_KEY = "jasperviewer.toolbar.paper_fit_width.tooltip";

	public static final String BTN_PAPER_CURRENT_SIZE_TOOLTIP_KEY = "jasperviewer.toolbar.paper_current_size.tooltip";

	public static final String BTN_END_TOOLTIP_KEY = "jasperviewer.toolbar.btn_end.tooltip";

	public static final String BTN_NEXT_TOOLTIP_KEY = "jasperviewer.toolbar.btn_next.tooltip";

	public static final String BTN_PREV_TOOLTIP_KEY = "jasperviewer.toolbar.btn_prev.tooltip";

	public static final String BTN_START_TOOLTIP_KEY = "jasperviewer.toolbar.btn_start.tooltip";

	public static final String BTN_REFRESH_TOOLTIP_KEY = "jasperviewer.toolbar.btn_refresh.tooltip";

	public static final String BTN_PRINT_TOOLTIP_KEY = "jasperviewer.toolbar.btn_print.tooltip";

	public static final String SAVE_CONTRIBUTOR_CSV = "csv";

	public static final String SAVE_CONTRIBUTOR_DOCX = "docx";

	public static final String SAVE_CONTRIBUTOR_HTML = "html";

	public static final String SAVE_CONTRIBUTOR_JASPER = "jasper";

	public static final String SAVE_CONTRIBUTOR_ODT = "odt";

	public static final String SAVE_CONTRIBUTOR_PDF = "pdf";

	public static final String SAVE_CONTRIBUTOR_RTF = "rtf";

	public static final String SAVE_CONTRIBUTOR_XLS_SINGLE_SHEET = "xls";

	public static final String SAVE_CONTRIBUTOR_XLS_MULTIPLE_SHEET = "xls-multiple-sheet";

	public static final String SAVE_CONTRIBUTOR_XML = "xml";

	public static final String SAVE_CONTRIBUTOR_XML_EMBEDDED_IMAGES = "xml-embedded-images";

	public static double defaultZoomRatio = 0.85;

	protected double zoom = CustomJasperViewerToolbar.defaultZoomRatio;

	protected JTextField title = new JTextField(65);

	protected JTextField reportDescripcion = new JTextField(65);

	protected LayoutManager toolbarLayout = new BoxLayout(this, BoxLayout.LINE_AXIS);

	/** Panel where title and description are placed in */
	protected JPanel textPanel = new JPanel();

	protected FocusAdapter focusAdapterTitle;

	protected FocusAdapter focusAdapterDescription;

	protected boolean focusAdapterAdded = false;

	protected JComboBox fontSizeCombo = new javax.swing.JComboBox();

	protected int fontSizes[] = { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

	protected int defaultfontSizesIndex = 6;

	protected DefaultReportDialog defaultReportDialog;

	public boolean isfocusAdapterAdded() {
		return this.focusAdapterAdded;
	}

	public void setFocusAdapterAdded(final boolean focusAdapterAdded) {
		this.focusAdapterAdded = focusAdapterAdded;
	}

	public CustomJasperViewerToolbar(final JRViewerController viewerContext, final DefaultReportDialog reportDialog) {
		super(viewerContext);
		// initcomponents is also called in super, but is private and we need
		// change it
		this.initComponents();
		this.initSaveContributors();
		this.changeToolbarIcons();
		this.createFontSizeCombo();
		this.createTitleandDescription(reportDialog);
	}

	@Override
	public void init() {
		super.init();
		this.fontSizeCombo.setSelectedIndex(this.defaultfontSizesIndex);
	}

	protected void initComponents() {
		this.remove(this.btnSave);
		this.btnSave = null;
		this.btnSave = new Form.FormButton();

		this.remove(this.btnPrint);
		this.btnPrint = null;
		this.btnPrint = new Form.FormButton();

		this.remove(this.btnReload);
		this.btnReload = null;
		this.btnReload = new Form.FormButton();

		this.remove(this.pnlSep01);
		this.pnlSep01 = null;
		this.pnlSep01 = new javax.swing.JPanel();

		this.remove(this.btnFirst);
		this.btnFirst = null;
		this.btnFirst = new Form.FormButton();

		this.remove(this.btnPrevious);
		this.btnPrevious = null;
		this.btnPrevious = new Form.FormButton();

		this.remove(this.btnNext);
		this.btnNext = null;
		this.btnNext = new Form.FormButton();

		this.remove(this.btnLast);
		this.btnLast = null;
		this.btnLast = new Form.FormButton();

		this.remove(this.txtGoTo);
		this.txtGoTo = null;
		this.txtGoTo = new javax.swing.JTextField();

		this.remove(this.pnlSep02);
		this.pnlSep02 = null;
		this.pnlSep02 = new javax.swing.JPanel();

		this.remove(this.btnActualSize);
		this.btnActualSize = null;
		this.btnActualSize = new javax.swing.JToggleButton();

		this.remove(this.btnFitPage);
		this.btnFitPage = null;
		this.btnFitPage = new javax.swing.JToggleButton();

		this.remove(this.btnFitWidth);
		this.btnFitWidth = null;
		this.btnFitWidth = new javax.swing.JToggleButton();

		this.remove(this.pnlSep03);
		this.pnlSep03 = null;
		this.pnlSep03 = new javax.swing.JPanel();

		this.remove(this.btnZoomIn);
		this.btnZoomIn = null;
		this.btnZoomIn = new Form.FormButton();

		this.remove(this.btnZoomOut);
		this.btnZoomOut = null;
		this.btnZoomOut = new Form.FormButton();

		this.remove(this.cmbZoom);
		this.cmbZoom = null;
		this.cmbZoom = new javax.swing.JComboBox();

		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (int i = 0; i < this.zooms.length; i++) {
			model.addElement("" + this.zooms[i] + "%");
		}
		this.cmbZoom.setModel(model);

		this.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 2));

		this.btnSave.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/save.GIF")));
		this.btnSave.setToolTipText(this.viewerContext.getBundleString("save"));
		this.btnSave.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnSave.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnSave.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnSave.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnSave.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnSaveActionPerformed(evt);
			}
		});
		this.add(this.btnSave);

		this.btnPrint.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/print.GIF")));
		this.btnPrint.setToolTipText(this.viewerContext.getBundleString("print"));
		this.btnPrint.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnPrint.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnPrint.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnPrint.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnPrint.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnPrintActionPerformed(evt);
			}
		});
		this.add(this.btnPrint);

		this.btnReload.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/reload.GIF")));
		this.btnReload.setToolTipText(this.viewerContext.getBundleString("reload"));
		this.btnReload.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnReload.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnReload.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnReload.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnReload.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnReloadActionPerformed(evt);
			}
		});
		this.add(this.btnReload);

		this.pnlSep01.setMaximumSize(new java.awt.Dimension(10, 10));
		this.add(this.pnlSep01);

		this.btnFirst.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/first.GIF")));
		this.btnFirst.setToolTipText(this.viewerContext.getBundleString("first.page"));
		this.btnFirst.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFirst.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFirst.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFirst.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFirst.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnFirstActionPerformed(evt);
			}
		});
		this.add(this.btnFirst);

		this.btnPrevious.setIcon(new javax.swing.ImageIcon(
				this.getClass().getResource("/net/sf/jasperreports/view/images/previous.GIF")));
		this.btnPrevious.setToolTipText(this.viewerContext.getBundleString("previous.page"));
		this.btnPrevious.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnPrevious.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnPrevious.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnPreviousActionPerformed(evt);
			}
		});
		this.add(this.btnPrevious);

		this.btnNext.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/next.GIF")));
		this.btnNext.setToolTipText(this.viewerContext.getBundleString("next.page"));
		this.btnNext.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnNext.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnNext.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnNext.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnNext.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnNextActionPerformed(evt);
			}
		});
		this.add(this.btnNext);

		this.btnLast.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/last.GIF")));
		this.btnLast.setToolTipText(this.viewerContext.getBundleString("last.page"));
		this.btnLast.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnLast.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnLast.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnLast.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnLast.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnLastActionPerformed(evt);
			}
		});
		this.add(this.btnLast);

		this.txtGoTo.setToolTipText(this.viewerContext.getBundleString("go.to.page"));
		this.txtGoTo.setMaximumSize(new java.awt.Dimension(60, 23));
		this.txtGoTo.setMinimumSize(new java.awt.Dimension(60, 23));
		this.txtGoTo.setPreferredSize(new java.awt.Dimension(60, 23));
		this.txtGoTo.setHorizontalAlignment(SwingConstants.CENTER);
		this.txtGoTo.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.txtGoToActionPerformed(evt);
			}
		});
		this.add(this.txtGoTo);

		this.pnlSep02.setMaximumSize(new java.awt.Dimension(10, 10));
		this.add(this.pnlSep02);

		this.btnActualSize.setIcon(new javax.swing.ImageIcon(
				this.getClass().getResource("/net/sf/jasperreports/view/images/actualsize.GIF")));
		this.btnActualSize.setToolTipText(this.viewerContext.getBundleString("actual.size"));
		this.btnActualSize.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnActualSize.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnActualSize.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnActualSizeActionPerformed(evt);
			}
		});
		this.add(this.btnActualSize);

		this.btnFitPage.setIcon(new javax.swing.ImageIcon(
				this.getClass().getResource("/net/sf/jasperreports/view/images/fitpage.GIF")));
		this.btnFitPage.setToolTipText(this.viewerContext.getBundleString("fit.page"));
		this.btnFitPage.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFitPage.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFitPage.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnFitPageActionPerformed(evt);
			}
		});
		this.add(this.btnFitPage);

		this.btnFitWidth.setIcon(new javax.swing.ImageIcon(
				this.getClass().getResource("/net/sf/jasperreports/view/images/fitwidth.GIF")));
		this.btnFitWidth.setToolTipText(this.viewerContext.getBundleString("fit.width"));
		this.btnFitWidth.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnFitWidth.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnFitWidth.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnFitWidthActionPerformed(evt);
			}
		});
		this.add(this.btnFitWidth);

		this.pnlSep03.setMaximumSize(new java.awt.Dimension(10, 10));
		this.add(this.pnlSep03);

		this.btnZoomIn.setIcon(
				new javax.swing.ImageIcon(this.getClass().getResource("/net/sf/jasperreports/view/images/zoomin.GIF")));
		this.btnZoomIn.setToolTipText(this.viewerContext.getBundleString("zoom.in"));
		this.btnZoomIn.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnZoomIn.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnZoomIn.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnZoomInActionPerformed(evt);
			}
		});
		this.add(this.btnZoomIn);

		this.btnZoomOut.setIcon(new javax.swing.ImageIcon(
				this.getClass().getResource("/net/sf/jasperreports/view/images/zoomout.GIF")));
		this.btnZoomOut.setToolTipText(this.viewerContext.getBundleString("zoom.out"));
		this.btnZoomOut.setMargin(new java.awt.Insets(2, 2, 2, 2));
		this.btnZoomOut.setMaximumSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.setMinimumSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.setPreferredSize(new java.awt.Dimension(23, 23));
		this.btnZoomOut.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.btnZoomOutActionPerformed(evt);
			}
		});
		this.add(this.btnZoomOut);

		this.cmbZoom.setEditable(true);
		this.cmbZoom.setToolTipText(this.viewerContext.getBundleString("zoom.ratio"));
		this.cmbZoom.setMaximumSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.setMinimumSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.setPreferredSize(new java.awt.Dimension(80, 23));
		this.cmbZoom.addActionListener(new java.awt.event.ActionListener() {

			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				CustomJasperViewerToolbar.this.cmbZoomActionPerformed(evt);
			}
		});
		this.cmbZoom.addItemListener(new java.awt.event.ItemListener() {

			@Override
			public void itemStateChanged(final java.awt.event.ItemEvent evt) {
				CustomJasperViewerToolbar.this.cmbZoomItemStateChanged(evt);
			}
		});
		this.add(this.cmbZoom);
	}

	@Override
	protected void initSaveContributors() {
		final List values = new Vector();
		final String property = System.getProperty("com.ontimize.report.saveContributors");
		if ((property != null) && (property.length() > 0)) {
			values.addAll(ApplicationManager.getTokensAt(property, ";"));
			if (values.size() > 0) {
				final JRSaveContributor[] customSaveContributors = new JRSaveContributor[values.size()];
				for (int i = 0; i < values.size(); ++i) {
					final String value = ((String) values.get(i)).toLowerCase();
					if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_CSV)) {
						customSaveContributors[i] = new JRCsvSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_DOCX)) {
						customSaveContributors[i] = new JRDocxSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_HTML)) {
						customSaveContributors[i] = new JRHtmlSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_JASPER)) {
						customSaveContributors[i] = new JRPrintSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_ODT)) {
						customSaveContributors[i] = new JROdtSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_PDF)) {
						customSaveContributors[i] = new JRPdfSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_RTF)) {
						customSaveContributors[i] = new JRRtfSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_SINGLE_SHEET)) {
						customSaveContributors[i] = new JRSingleSheetXlsSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XLS_MULTIPLE_SHEET)) {
						customSaveContributors[i] = new JRMultipleSheetsXlsSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XML)) {
						customSaveContributors[i] = new JRXmlSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					} else if (value.equals(CustomJasperViewerToolbar.SAVE_CONTRIBUTOR_XML_EMBEDDED_IMAGES)) {
						customSaveContributors[i] = new JREmbeddedImagesXmlSaveContributor(this.getLocale(),
								this.viewerContext.getResourceBundle());
					}
				}
				this.setSaveContributors(customSaveContributors);
			}
		}
		// If there were not custom contributors, set defaults
		if (this.saveContributors.size() == 0) {
			super.initSaveContributors();
		}
	}

	public JComboBox getFontSizeCombo() {
		return this.fontSizeCombo;
	}

	public JTextField getTitleField() {
		return this.title;
	}

	public JTextField getDescriptionField() {
		return this.reportDescripcion;
	}

	public JPanel getTitleAndDescriptionPanel() {
		return this.textPanel;
	}

	@Override
	public void setLayout(final LayoutManager arg0) {
		super.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
	}

	public void changeToolbarIcons() {
		this.btnActualSize.setIcon(ImageManager.getIcon(ImageManager.PAPER_FIRST_PLANE));
		this.btnFirst.setIcon(ImageManager.getIcon(ImageManager.START_2));
		this.btnFitPage.setIcon(ImageManager.getIcon(ImageManager.PAPER_SEE_ALL));
		this.btnFitWidth.setIcon(ImageManager.getIcon(ImageManager.PAPER_WIDTH));
		this.btnLast.setIcon(ImageManager.getIcon(ImageManager.END_2));
		this.btnNext.setIcon(ImageManager.getIcon(ImageManager.NEXT_2));
		this.btnPrevious.setIcon(ImageManager.getIcon(ImageManager.PREVIOUS_2));
		this.btnPrint.setIcon(ImageManager.getIcon(ImageManager.PRINT));
		this.btnReload.setIcon(ImageManager.getIcon(ImageManager.REFRESH));
		this.btnSave.setIcon(ImageManager.getIcon(ImageManager.SAVE));
		this.btnZoomIn.setIcon(ImageManager.getIcon(ImageManager.ZOOM_IN));
		this.btnZoomOut.setIcon(ImageManager.getIcon(ImageManager.ZOOM_OUT));
	}

	public double getZoom() {
		return this.zoom;
	}

	public void setZoom(final double zoom) {
		this.zoom = zoom;
	}

	public void createTitleandDescription(final DefaultReportDialog reportDialog) {
		final Font f = this.title.getFont().deriveFont(11F);
		this.title.setFont(f);
		this.reportDescripcion.setFont(f);
		this.textPanel.setLayout(new GridLayout(0, 1));
		this.textPanel.add(this.title);
		this.textPanel.add(this.reportDescripcion);
		this.add(new ApToolBarSeparator(new Hashtable()));
		this.add(this.textPanel);
	}

	public void createFontSizeCombo() {
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		for (int i = 0; i < this.fontSizes.length; i++) {
			model.addElement(this.fontSizes[i]);
		}
		this.fontSizeCombo.setModel(model);
		this.fontSizeCombo.setMaximumSize(new java.awt.Dimension(60, 23));
		this.fontSizeCombo.setMinimumSize(new java.awt.Dimension(60, 23));
		this.fontSizeCombo.setPreferredSize(new java.awt.Dimension(60, 23));
		this.fontSizeCombo.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				if (evt.getSource() instanceof JComboBox) {
					final JComboBox comboBox = (JComboBox) evt.getSource();
					final Object newValue = comboBox.getSelectedItem();
					if (CustomJasperViewerToolbar.this.defaultReportDialog != null) {
						CustomJasperViewerToolbar.this.defaultReportDialog.updateReport();
					}

				}
				// cmbZoomActionPerformed(evt);
			}
		});
		this.add(this.fontSizeCombo);
	}

	public void setFocusAdapters(final DefaultReportDialog reportDialog) {
		this.focusAdapterTitle = new FocusAdapter() {

			String oldTitle = "";

			@Override
			public void focusGained(final FocusEvent e) {
				if (!e.isTemporary()) {
					final String t = CustomJasperViewerToolbar.this.title.getText();
					this.oldTitle = t != null ? t : "";
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if (!e.isTemporary()) {
					final String t = CustomJasperViewerToolbar.this.title.getText();
					if (!this.oldTitle.equals(t)) {
						reportDialog.updateReport();
					}
				}
			}
		};
		this.focusAdapterDescription = new FocusAdapter() {

			String oldDescrip = "";

			@Override
			public void focusGained(final FocusEvent e) {
				if (!e.isTemporary()) {
					final String t = CustomJasperViewerToolbar.this.reportDescripcion.getText();
					this.oldDescrip = t != null ? t : "";
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {
				if (!e.isTemporary()) {
					final String t = CustomJasperViewerToolbar.this.reportDescripcion.getText();
					if (!this.oldDescrip.equals(t)) {
						reportDialog.updateReport();
					}
				}
			}
		};
		this.title.addFocusListener(this.focusAdapterTitle);
		this.reportDescripcion.addFocusListener(this.focusAdapterDescription);
	}

	void btnSaveActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnSaveActionPerformed
		// Add your handling code here:

		final JFileChooser fileChooser = new JFileChooser();
		fileChooser.setLocale(this.getLocale());
		fileChooser.updateUI();
		for (int i = 0; i < this.saveContributors.size(); i++) {
			fileChooser.addChoosableFileFilter(this.saveContributors.get(i));
		}

		if (this.saveContributors.contains(this.lastSaveContributor)) {
			fileChooser.setFileFilter(this.lastSaveContributor);
		} else if (this.saveContributors.size() > 0) {
			fileChooser.setFileFilter(this.saveContributors.get(0));
		}

		if (this.lastFolder != null) {
			fileChooser.setCurrentDirectory(this.lastFolder);
		}

		final int retValue = fileChooser.showSaveDialog(this);
		if (retValue == JFileChooser.APPROVE_OPTION) {
			final FileFilter fileFilter = fileChooser.getFileFilter();
			final File file = fileChooser.getSelectedFile();

			this.lastFolder = file.getParentFile();

			JRSaveContributor contributor = null;

			if (fileFilter instanceof JRSaveContributor) {
				contributor = (JRSaveContributor) fileFilter;
			} else {
				int i = 0;
				while ((contributor == null) && (i < this.saveContributors.size())) {
					contributor = this.saveContributors.get(i++);
					if (!contributor.accept(file)) {
						contributor = null;
					}
				}

				if (contributor == null) {
					contributor = new JRPrintSaveContributor(this.getLocale(), this.viewerContext.getResourceBundle());
				}
			}

			this.lastSaveContributor = contributor;

			try {
				contributor.save(this.viewerContext.getJasperPrint(), file);
			} catch (final JRException e) {
				CustomJasperViewerToolbar.logger.trace(null, e);
				JOptionPane.showMessageDialog(this, this.viewerContext.getBundleString("error.saving"));
			}
		}
	}// GEN-LAST:event_btnSaveActionPerformed

	void btnPrintActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnPrintActionPerformed
	{// GEN-HEADEREND:event_btnPrintActionPerformed
		// Add your handling code here:

		final Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					CustomJasperViewerToolbar.this.btnPrint.setEnabled(false);
					JasperPrintManager.printReport(CustomJasperViewerToolbar.this.viewerContext.getJasperPrint(), true);
				} catch (final Exception ex) {
					CustomJasperViewerToolbar.logger.trace(null, ex);
					JOptionPane.showMessageDialog(CustomJasperViewerToolbar.this,
							CustomJasperViewerToolbar.this.viewerContext.getBundleString("error.printing"));
				} finally {
					CustomJasperViewerToolbar.this.btnPrint.setEnabled(true);
				}
			}
		});

		thread.start();

	}// GEN-LAST:event_btnPrintActionPerformed

	void btnReloadActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnReloadActionPerformed
	{// GEN-HEADEREND:event_btnReloadActionPerformed
		// Add your handling code here:
		this.viewerContext.reload();
	}// GEN-LAST:event_btnReloadActionPerformed

	void btnFirstActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnFirstActionPerformed
	{// GEN-HEADEREND:event_btnFirstActionPerformed
		// Add your handling code here:
		this.viewerContext.setPageIndex(0);
		this.viewerContext.refreshPage();
	}// GEN-LAST:event_btnFirstActionPerformed

	void btnPreviousActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnPreviousActionPerformed
	{// GEN-HEADEREND:event_btnPreviousActionPerformed
		// Add your handling code here:
		this.viewerContext.setPageIndex(this.viewerContext.getPageIndex() - 1);
		this.viewerContext.refreshPage();
	}// GEN-LAST:event_btnPreviousActionPerformed

	void btnNextActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnNextActionPerformed
	{// GEN-HEADEREND:event_btnNextActionPerformed
		// Add your handling code here:
		this.viewerContext.setPageIndex(this.viewerContext.getPageIndex() + 1);
		this.viewerContext.refreshPage();
	}// GEN-LAST:event_btnNextActionPerformed

	void btnLastActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnLastActionPerformed
	{// GEN-HEADEREND:event_btnLastActionPerformed
		// Add your handling code here:
		this.viewerContext.setPageIndex(this.viewerContext.getPageCount() - 1);
		this.viewerContext.refreshPage();
	}// GEN-LAST:event_btnLastActionPerformed

	void txtGoToActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_txtGoToActionPerformed
		try {
			final int pageNumber = Integer.parseInt(this.txtGoTo.getText());
			if ((pageNumber != (this.viewerContext.getPageIndex() + 1)) && (pageNumber > 0)
					&& (pageNumber <= this.viewerContext.getPageCount())) {
				this.viewerContext.setPageIndex(pageNumber - 1);
				this.viewerContext.refreshPage();
			}
		} catch (final NumberFormatException e) {
		}
	}// GEN-LAST:event_txtGoToActionPerformed

	void btnActualSizeActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnActualSizeActionPerformed
		// Add your handling code here:
		if (this.btnActualSize.isSelected()) {
			this.btnFitPage.setSelected(false);
			this.btnFitWidth.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.viewerContext.setZoomRatio(1);
			this.btnActualSize.setSelected(true);
		}
	}// GEN-LAST:event_btnActualSizeActionPerformed

	void btnFitPageActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFitPageActionPerformed
		// Add your handling code here:
		if (this.btnFitPage.isSelected()) {
			this.btnActualSize.setSelected(false);
			this.btnFitWidth.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.viewerContext.fitPage();
			this.btnFitPage.setSelected(true);
		}
	}// GEN-LAST:event_btnFitPageActionPerformed

	void btnFitWidthActionPerformed(final java.awt.event.ActionEvent evt) {// GEN-FIRST:event_btnFitWidthActionPerformed
		// Add your handling code here:
		if (this.btnFitWidth.isSelected()) {
			this.btnActualSize.setSelected(false);
			this.btnFitPage.setSelected(false);
			this.cmbZoom.setSelectedIndex(-1);
			this.viewerContext.fitWidth();
			this.btnFitWidth.setSelected(true);
		}
	}// GEN-LAST:event_btnFitWidthActionPerformed

	void btnZoomInActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnZoomInActionPerformed
	{// GEN-HEADEREND:event_btnZoomInActionPerformed
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);

		final int newZoomInt = (int) (100 * this.getZoomRatio());
		final int index = Arrays.binarySearch(this.zooms, newZoomInt);
		if (index < 0) {
			this.viewerContext.setZoomRatio(this.zooms[-index - 1] / 100f);
		} else if (index < (this.cmbZoom.getModel().getSize() - 1)) {
			this.viewerContext.setZoomRatio(this.zooms[index + 1] / 100f);
		}
	}// GEN-LAST:event_btnZoomInActionPerformed

	@Override
	protected float getZoomRatio() {
		float newZoom = this.viewerContext.getZoom();

		try {
			newZoom = this.zoomDecimalFormat.parse(String.valueOf(this.cmbZoom.getEditor().getItem())).floatValue()
					/ 100f;
		} catch (final ParseException e) {
		}

		return newZoom;
	}

	void btnZoomOutActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_btnZoomOutActionPerformed
	{// GEN-HEADEREND:event_btnZoomOutActionPerformed
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);

		final int newZoomInt = (int) (100 * this.getZoomRatio());
		final int index = Arrays.binarySearch(this.zooms, newZoomInt);
		if (index > 0) {
			this.viewerContext.setZoomRatio(this.zooms[index - 1] / 100f);
		} else if (index < -1) {
			this.viewerContext.setZoomRatio(this.zooms[-index - 2] / 100f);
		}
	}// GEN-LAST:event_btnZoomOutActionPerformed

	void cmbZoomActionPerformed(final java.awt.event.ActionEvent evt)// GEN-FIRST:event_cmbZoomActionPerformed
	{// GEN-HEADEREND:event_cmbZoomActionPerformed
		// Add your handling code here:
		float newZoom = this.getZoomRatio();

		if (newZoom < this.MIN_ZOOM) {
			newZoom = this.MIN_ZOOM;
		}

		if (newZoom > this.MAX_ZOOM) {
			newZoom = this.MAX_ZOOM;
		}

		this.viewerContext.setZoomRatio(newZoom);
	}// GEN-LAST:event_cmbZoomActionPerformed

	void cmbZoomItemStateChanged(final java.awt.event.ItemEvent evt) {// GEN-FIRST:event_cmbZoomItemStateChanged
		// Add your handling code here:
		this.btnActualSize.setSelected(false);
		this.btnFitPage.setSelected(false);
		this.btnFitWidth.setSelected(false);
	}// GEN-LAST:event_cmbZoomItemStateChanged

	@Override
	public void setComponentLocale(final Locale l) {

	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		if (resourceBundle != null) {
			if (this.btnSave != null) {
				this.btnSave
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_SAVE_TOOLTIP_KEY));
			}
			if (this.txtGoTo != null) {
				this.txtGoTo.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.TXT_GO_TO_PAGE_TOOLTIP_KEY));
			}

			if (this.btnActualSize != null) {
				this.btnActualSize.setToolTipText(ApplicationManager
						.getTranslation(CustomJasperViewerToolbar.BTN_PAPER_CURRENT_SIZE_TOOLTIP_KEY));
			}

			if (this.btnFirst != null) {
				this.btnFirst
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_START_TOOLTIP_KEY));
			}

			if (this.btnFitPage != null) {
				this.btnFitPage.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_SEE_ALL_TOOLTIP_KEY));
			}

			if (this.btnFitWidth != null) {
				this.btnFitWidth.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_PAPER_FIT_WIDTH_TOOLTIP_KEY));
			}

			if (this.btnLast != null) {
				this.btnLast
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_END_TOOLTIP_KEY));
			}

			if (this.btnNext != null) {
				this.btnNext
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_NEXT_TOOLTIP_KEY));
			}

			if (this.btnPrevious != null) {
				this.btnPrevious
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_PREV_TOOLTIP_KEY));
			}

			if (this.btnPrint != null) {
				this.btnPrint
				.setToolTipText(ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_PRINT_TOOLTIP_KEY));
			}

			if (this.btnReload != null) {
				this.btnReload.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_REFRESH_TOOLTIP_KEY));
			}

			if (this.btnZoomIn != null) {
				this.btnZoomIn.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_ZOOM_IN_TOOLTIP_KEY));
			}

			if (this.btnZoomOut != null) {
				this.btnZoomOut.setToolTipText(
						ApplicationManager.getTranslation(CustomJasperViewerToolbar.BTN_ZOOM_OUT_TOOLTIP_KEY));
			}
		}
	}

	@Override
	public List getTextsToTranslate() {
		return null;
	}

	public void setDefaultReportDialog(final DefaultReportDialog reportDialog) {
		this.defaultReportDialog = reportDialog;
	}

}
