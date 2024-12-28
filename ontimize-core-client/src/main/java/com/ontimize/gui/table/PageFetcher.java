package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.field.document.IntegerDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.AdvancedEntity;
import com.ontimize.jee.common.db.AdvancedEntityResult;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.ConnectionOptimizer;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

/**
 * Class that defines additional buttons for <code>Table</code> when it is pageable. These buttons
 * are placed at the bottom of table and allows move forward/back on table pages, go to the first
 * and last page and download all records.
 *
 * @see Table#QUERY_ROWS
 * @author Imatia Innovation
 */
public class PageFetcher extends JPanel implements TableModelListener {

	private static final Logger logger = LoggerFactory.getLogger(PageFetcher.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.3.7
	 */
	public static final String PAGE_FETCHER_LABEL_NAME = "PageFetcher.Label";

	protected static final String PAGINABLE_LABEL_KEY = "rowfetcher.paginable_label";

	protected static final String PAGINABLE_LABEL_SIZE_KEY = "rowfetcher.page_size_label";

	protected static final String FIRST_BUTTON_TOOLTIP_KEY = "rowfetcher.first_page";

	protected static final String PREVIOUS_BUTTON_TOOLTIP_KEY = "rowfetcher.previous_page";

	protected static final String NEXT_BUTTON_TOOLTIP_KEY = "rowfetcher.next_page";

	protected static final String LAST_BUTTON_TOOLTIP_KEY = "rowfetcher.last_page";

	protected static final String DOWNLOAD_ALL_BUTTON_TOOLTIP_KEY = "rowfetcher.download_all_data";

	protected static final String CHANGE_PAGE_SIZE_TOOLTIP_KEY = "rowfetcher.change_page_size";

	protected Expression filterExpression;

	protected Table table = null;

	protected boolean pageableEnabled = true;

	protected JButton firstPageButton = null;

	protected JButton previousPageButton = null;

	protected JButton nextPageButton = null;

	protected JButton lastPageButton = null;

	protected JButton downloadAllButton = null;

	protected JLabel pageSizeLabel = null;

	protected JTextField pageSizeField = null;

	protected ResourceBundle bundle = null;

	protected JLabel messageLabel = null;

	protected ExtendedJPopupMenu pageMenu = null;

	protected PageMenuItemListener pageMenuListener;

	protected int pageSize = -1;

	protected int offset = 0;

	protected int totalSize = 0;

	// /**
	// * @since 5.3.11
	// */
	// protected RefreshThread refreshThread = null;

	public int getTotalSize() {
		return this.totalSize;
	}

	public PageFetcher(final Table table, final int pageSize) {
		this.table = table;
		this.pageSize = pageSize;
		this.setLayout(new GridBagLayout());
		this.init();
		this.table.getContentPane().add(this, BorderLayout.SOUTH);

		this.table.getJTable().getModel().addTableModelListener(this);
	}

	@Override
	public String getName() {
		return "TableButtonFooterPanel";
	}

	public boolean isPageableEnabled() {
		return this.pageableEnabled;
	}

	public void setPageableEnabled(final boolean pageableEnabled) {
		this.pageableEnabled = pageableEnabled;
		this.downloadAllButton.setEnabled(pageableEnabled);
		if (!pageableEnabled) {
			this.disablePageButtons();
		}
		((TableSorter) this.table.getJTable().getModel()).setLocalSorter(!pageableEnabled);
		// setVisible(pageableEnabled);
	}

	protected void disablePageButtons() {
		this.nextPageButton.setEnabled(false);
		this.previousPageButton.setEnabled(false);
		this.firstPageButton.setEnabled(false);
		this.lastPageButton.setEnabled(false);
		this.messageLabel.setText("");
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getOffset() {
		return this.offset;
	}

	public void setOffset(final int offset) {
		this.offset = offset;
	}

	protected void setResourceBundle(final ResourceBundle bundle) {
		this.bundle = bundle;

		this.messageLabel.setText(ApplicationManager.getTranslation(PageFetcher.PAGINABLE_LABEL_KEY, this.bundle,
				new String[] { "" + this.offset, "" + (this.offset + this.pageSize), "" + this.totalSize }));

		this.firstPageButton
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.FIRST_BUTTON_TOOLTIP_KEY, bundle));

		this.previousPageButton
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.PREVIOUS_BUTTON_TOOLTIP_KEY, bundle));

		this.nextPageButton
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.NEXT_BUTTON_TOOLTIP_KEY, bundle));

		this.lastPageButton
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.LAST_BUTTON_TOOLTIP_KEY, bundle));

		this.downloadAllButton
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.DOWNLOAD_ALL_BUTTON_TOOLTIP_KEY, bundle));

		this.pageSizeField
		.setToolTipText(ApplicationManager.getTranslation(PageFetcher.CHANGE_PAGE_SIZE_TOOLTIP_KEY, bundle));

		this.pageSizeLabel.setText(ApplicationManager.getTranslation(PageFetcher.PAGINABLE_LABEL_SIZE_KEY, bundle));
	}

	protected void refreshLabel(final AdvancedEntityResult aER) {
		this.totalSize = aER.getTotalRecordCount();
		if (this.totalSize <= this.pageSize) {
			// setPageableEnabled(false);
			this.downloadAllButton.setEnabled(false);
			this.disablePageButtons();
			((TableSorter) this.table.getJTable().getModel()).setLocalSorter(this.table.isQuickFilterLocal());
		} else {

			this.offset = aER.getStartRecordIndex();

			if (this.offset > this.totalSize) {
				this.offset = this.totalSize;
			}

			final int firstindex = this.totalSize > 0 ? this.offset + 1 : this.offset;
			final int lastoffset = this.offset + aER.getCurrentRecordCount();

			this.messageLabel.setText(
					ApplicationManager.getTranslation(PageFetcher.PAGINABLE_LABEL_KEY, this.bundle,
							new String[] { "" + firstindex, "" + lastoffset, "" + this.totalSize }));

			if (this.offset == 0) {
				this.firstPageButton.setEnabled(false);
				this.previousPageButton.setEnabled(false);
			} else {
				this.firstPageButton.setEnabled(true);
				this.previousPageButton.setEnabled(true);
			}
			if ((this.totalSize - this.pageSize) <= this.offset) {
				this.nextPageButton.setEnabled(false);
				this.lastPageButton.setEnabled(false);
			} else {
				this.nextPageButton.setEnabled(true);
				this.lastPageButton.setEnabled(true);
			}
		}
	}

	protected class PageMenuItem extends JMenuItem {

		protected int offset;

		public PageMenuItem(final int offset) {
			this.offset = offset;
		}

		public int getOffset() {
			return this.offset;
		}

	}

	protected class PageMenuItemListener implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (e.getSource() instanceof PageMenuItem) {
				final int index = ((PageMenuItem) e.getSource()).getOffset();
				PageFetcher.this.queryRecordsInThread(index, PageFetcher.this.getPageSize(), 0);
			}
			PageFetcher.this.pageMenu.setVisible(false);
		}

	}

	protected void calculatePages() {
		if (this.pageMenuListener != null) {
			final Component[] menus = this.pageMenu.getComponents();
			for (final Component item : menus) {
				if (item instanceof PageMenuItem) {
					((PageMenuItem) item).removeActionListener(this.pageMenuListener);
				}
			}
			this.pageMenu.removeAll();
		}

		if (this.totalSize <= this.pageSize) {
			return;
		}

		int limit = 0;
		int forwardSize = 10 - ((this.totalSize - this.offset) / this.pageSize);
		if (forwardSize < 0) {
			forwardSize = 0;
		}

		int initValue = this.offset;
		for (int i = 0; i < (10 + forwardSize); i++) {
			if (initValue <= 0) {
				break;
			} else {
				initValue -= this.pageSize;
			}
		}

		if (initValue < 0) {
			initValue = 0;
		}
		for (int i = initValue; i < this.totalSize; i += this.pageSize) {
			if (i == this.offset) {
				continue;
			}

			final PageMenuItem item = new PageMenuItem(i);
			final int firstElement = i + 1;
			int lastElement = i + this.pageSize;
			if (lastElement > this.totalSize) {
				lastElement = this.totalSize;
			}
			item.setText(
					ApplicationManager.getTranslation(PageFetcher.PAGINABLE_LABEL_KEY, this.bundle,
							new String[] { "" + firstElement, "" + lastElement, "" + this.totalSize }));
			if (this.pageMenuListener == null) {
				this.pageMenuListener = new PageMenuItemListener();
			}
			item.addActionListener(this.pageMenuListener);
			this.pageMenu.add(item);
			limit++;
			if (limit > 20) {
				break;
			}
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		super.setEnabled(enabled);
		if (!enabled) {
			this.nextPageButton.setEnabled(enabled);
			this.previousPageButton.setEnabled(enabled);
			this.firstPageButton.setEnabled(enabled);
			this.lastPageButton.setEnabled(enabled);
			this.downloadAllButton.setEnabled(enabled);
		}

		if (this.table.isQueryRowsModifiable()) {
			this.pageSizeField.setEnabled(enabled);
		} else {
			this.pageSizeField.setEnabled(false);
		}
	}

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		super.setEnabled(visible);
	}

	protected void changePageSize() {
		this.changePageSize(true);
	}

	protected void changePageSize(final boolean query) {
		final String value = this.pageSizeField.getText();
		if ((value == null) || (value.length() == 0)) {
			this.pageSizeField.setText("" + this.pageSize);
			return;
		}

		try {
			final int newPageSize = Integer.parseInt(value);
			if (newPageSize != this.pageSize) {
				this.pageSize = newPageSize;
				if (this.pageSize > this.totalSize) {
					this.offset = 0;
					setPageableEnabled(false);
				} else {
					setPageableEnabled(true);
				}
				if (query) {
					this.refreshCurrentPageInThread();
				}
			}
		} catch (final Exception ex) {
			PageFetcher.logger.trace(null, ex);
		}

	}

	protected void init() {
		this.pageSizeLabel = new JLabel(PageFetcher.PAGINABLE_LABEL_SIZE_KEY) {

			@Override
			public String getName() {
				return PageFetcher.PAGE_FETCHER_LABEL_NAME;
			}
		};

		this.pageSizeField = new JTextField(4);
		this.pageSizeField.setHorizontalAlignment(SwingConstants.CENTER);
		this.pageSizeField.setDocument(new IntegerDocument());
		this.pageSizeField.setText("" + this.pageSize);

		this.pageSizeField.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				PageFetcher.this.changePageSize();
			}
		});

		this.pageSizeField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(final KeyEvent e) {
				super.keyTyped(e);
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					PageFetcher.this.changePageSize();
				}
			}

		});

		this.firstPageButton = new PageFetcherButton(ImageManager.getIcon(ImageManager.START_2));
		this.firstPageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PageFetcher.this.getFirstPage();
			}
		});

		this.previousPageButton = new PageFetcherButton(ImageManager.getIcon(ImageManager.PREVIOUS_2));
		this.previousPageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PageFetcher.this.getPreviousPage();
			}
		});

		this.nextPageButton = new PageFetcherButton(ImageManager.getIcon(ImageManager.NEXT_2));
		this.nextPageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PageFetcher.this.getNextPage();
			}
		});

		this.lastPageButton = new PageFetcherButton(ImageManager.getIcon(ImageManager.END_2));
		this.lastPageButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PageFetcher.this.getLastPage();
			}
		});

		this.downloadAllButton = new PageFetcherButton(ImageManager.getIcon(ImageManager.VIEW_DETAILS));
		this.downloadAllButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				PageFetcher.this.downloadAll();
			}
		});

		this.messageLabel = new JLabel("") {

			@Override
			public String getName() {
				return PageFetcher.PAGE_FETCHER_LABEL_NAME;
			}
		};

		this.messageLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				super.mouseClicked(e);
				PageFetcher.this.calculatePages();
				PageFetcher.this.pageMenu.show((Component) e.getSource(), 0, 0);
			}
		});

		this.pageMenu = new ExtendedJPopupMenu();

		this.add(this.pageSizeLabel,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.pageSizeField,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.WEST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.firstPageButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.previousPageButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.messageLabel,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.nextPageButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 2), 0, 0));
		this.add(this.lastPageButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
		this.add(this.downloadAllButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(0, 2, 0, 0), 0, 0));
	}

	// since 5.2071EN-0.2
	public void downloadAll() {
		try {
			this.table.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.offset = 0;
			this.setPageableEnabled(false);
			this.table.refreshEDT(true);
		} finally {
			this.table.setCursor(Cursor.getDefaultCursor());
		}
	}

	public void getNextPage() {
		this.offset = this.offset + this.pageSize;
		this.queryRecordsInThread(this.offset, this.pageSize, 0);
		// queryRecords(offset, pageSize);
	}

	public void getPreviousPage() {
		this.offset = this.offset - this.pageSize;
		if (this.offset < 0) {
			this.offset = 0;
		}
		this.queryRecordsInThread(this.offset, this.pageSize, 0);
		// queryRecords(offset, pageSize);
	}

	public void getFirstPage() {
		this.offset = 0;
		this.queryRecordsInThread(this.offset, this.pageSize, 0);
		// queryRecords(offset, pageSize);
	}

	public void getLastPage() {
		if ((this.totalSize % this.pageSize) == 0) {
			this.offset = this.totalSize - this.pageSize;
		} else {
			this.offset = this.totalSize - (this.totalSize % this.pageSize);
		}
		this.queryRecordsInThread(this.offset, this.pageSize, 0);
		// queryRecords(offset, pageSize);
	}

	/**
	 * @deprecated
	 * @see #refreshCurrentPageInThread
	 */
	@Deprecated
	public void refreshCurrentPage() {
		this.queryRecords(this.offset, this.pageSize);
	}

	public void refreshCurrentPageInThread() {
		this.table.refreshInThread(0);
	}

	protected void queryRecordsInThread(final int offset, final int recordNumber, final int delay) {
		try {
			this.offset = offset;
			this.pageSize = recordNumber;
			this.table.refreshInThread(0);

		} catch (final Exception e) {
			PageFetcher.logger.trace(null, e);
		}
	}

	/**
	 * @deprecated
	 * @see #queryRecordsInThread
	 */
	@Deprecated
	protected void queryRecords(final int offset, final int recordNumber) {

		try {
			this.table.getParentForm().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final EntityReferenceLocator locator = this.table.getParentForm().getFormManager().getReferenceLocator();
			final Entity ent = locator.getEntityReference(this.table.getEntityName());

			final Map kv = this.table.getParentKeyValues();

			final AdvancedEntityResult res = ((AdvancedEntity) ent).query(kv, this.table.getAttributeList(),
					locator.getSessionId(), recordNumber, offset, this.table.getSQLOrderList());

			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.table.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
				PageFetcher.logger.debug("Operation wrong message : {}", res.getMessage());
			} else {
				// Test net speed
				final int threshold = ConnectionManager.getCompresionThreshold(res.getBytesNumber(), res.getStreamTime());
				if (threshold > 0) {
					final ConnectionOptimizer opt = ConnectionManager.getConnectionOptimizer();
					if ((opt != null) && (locator instanceof ClientReferenceLocator)) {
						try {
							opt.setDataCompressionThreshold(((ClientReferenceLocator) locator).getUser(),
									locator.getSessionId(), threshold);
							PageFetcher.logger.info("Compression threshold set to {} {} in: {}",
									((ClientReferenceLocator) locator).getUser(), locator.getSessionId(), threshold);
						} catch (final Exception e) {
							PageFetcher.logger.error("Table: Error setting compression threshold", e);
						}
					}
				}

				// If returned records count and requested records count is not
				// the
				// same then notify using the console
				final int nReturnedRecords = res.calculateRecordNumber();
				if (nReturnedRecords != recordNumber) {
					PageFetcher.logger.debug(
							"Table: The records amount returned by the advanced entity does not match the requested amount: Requested: {}. Returned: {}",
							recordNumber, nReturnedRecords);
				}
				this.table.setValue(res);
				this.totalSize = res.getTotalRecordCount();
			}
		} catch (final Exception e) {
			PageFetcher.logger.error(null, e);
		} finally {
			this.table.getParentForm().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	public void tableChanged(final TableModelEvent e) {
		// This method only must refresh the current page if the event is a sort
		// event but not an edition event or any other
		if ((e.getColumn() == TableModelEvent.ALL_COLUMNS) && (e.getSource() instanceof TableSorter)) {
			if (this.isPageableEnabled() && this.table.isEnabled()) {
				final int row = e.getFirstRow();
				if ((row >= 0) && ((TableSorter) e.getSource()).isInsertingRow(row)) {
					// If the event is an event in the inserting row then no
					// page
					// must be query
					return;
				}
				this.refreshCurrentPageInThread();
			}
		}
	}

	public class PageFetcherButton extends JButton {

		public PageFetcherButton() {
			super();
		}

		public PageFetcherButton(final Action a) {
			super(a);
		}

		public PageFetcherButton(final Icon icon) {
			super(icon);
		}

		public PageFetcherButton(final String text, final Icon icon) {
			super(text, icon);
		}

		public PageFetcherButton(final String text) {
			super(text);
		}

		@Override
		public String getName() {
			return TableButton.TABLEBUTTON_NAME;
		}

		@Override
		public boolean isDefaultCapable() {
			if (TableButton.defaultCapable != null) {
				return TableButton.defaultCapable.booleanValue();
			}
			return false;
		}

		@Override
		public void setContentAreaFilled(final boolean b) {
			if (TableButton.defaultContentAreaFilled != null) {
				super.setContentAreaFilled(TableButton.defaultContentAreaFilled.booleanValue());
				return;
			}
			super.setContentAreaFilled(b);
		}

		@Override
		public void setFocusPainted(final boolean b) {
			if (TableButton.defaultPaintFocus != null) {
				super.setFocusPainted(TableButton.defaultPaintFocus.booleanValue());
				return;
			}
			super.setFocusPainted(b);
		}

	}

	public Expression getFilterExpression() {
		return this.filterExpression;
	}

	public void setFilterExpression(final Expression filterExpression) {
		this.filterExpression = filterExpression;
	}

	public Integer getPageFetcherRecordNumber() {
		return Integer.parseInt(this.pageSizeField.getText());
	}

	public void setPageSize(final Integer pageSize) {
		this.pageSize = pageSize;
	}

}
