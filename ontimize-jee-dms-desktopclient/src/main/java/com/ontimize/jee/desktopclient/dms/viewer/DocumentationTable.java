package com.ontimize.jee.desktopclient.dms.viewer;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DropMode;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.InteractionManagerModeEvent;
import com.ontimize.gui.InteractionManagerModeListener;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableSorter;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.exceptions.DmsRuntimeException;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.naming.DMSNaming;
import com.ontimize.jee.common.services.dms.DMSCategory;
import com.ontimize.jee.common.services.dms.IDMSService;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.jee.desktopclient.components.taskmanager.ByteSizeTableCellRenderer;
import com.ontimize.jee.desktopclient.dms.upload.OpenUploadableChooserActionListener;
import com.ontimize.jee.desktopclient.spring.BeansFactory;


/**
 * The Class DocumentationTable.
 */
public class DocumentationTable extends Table implements InteractionManagerModeListener {

	private static final Logger logger = LoggerFactory.getLogger(DocumentationTable.class);
	protected static final String AVOID_PARENT_KEYS_NULL = "avoidparentkeysnull";

	private final DocumentationTree categoryTree = new DocumentationTree();
	protected DocumentationTableDetailFormOpener opener = new DocumentationTableDetailFormOpener(
			new Hashtable<String, Object>());
	private Serializable currentIdDocument = null;
	private Map<? extends Serializable, ? extends Serializable> currentFilter = null;
	private boolean deleting = false;
	private boolean ignoreEvents = false;
	private boolean ignoreCheckRefreshThread = false;
	private boolean categoryPanel = true;
	protected String form_id_dms_doc_field;

	public DocumentationTable(final Map<String, Object> params) throws Exception {
		super(params);
		this.getJTable().setFillsViewportHeight(true);
		this.setRendererForColumn(DMSNaming.DOCUMENT_FILE_VERSION_FILE_SIZE, new ByteSizeTableCellRenderer());
		this.categoryPanel = ParseUtilsExtended.getBoolean((String) params.get("categorypanel"), true);

		this.categoryTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(final TreeSelectionEvent event) {
				if (!DocumentationTable.this.isIgnoreEvents()) {
					DocumentationTable.this.refreshIfNeededInThread();
				}
			}
		});
		final JScrollPane scrollPane = new JScrollPane(this.categoryTree);
		this.mainPanel.add(scrollPane, BorderLayout.WEST);
		this.getJTable().setDragEnabled(true);
		this.getJTable().setDropMode(DropMode.INSERT_ROWS);
		this.getJTable().setTransferHandler(new DocumentationTableTransferHandler(this));
		scrollPane.setVisible(this.categoryPanel);
	}

	@Override
	protected void installDetailFormListener() {
		super.installDetailFormListener();
		try {
			this.buttonPlus2.removeActionListener(this.addRecordListener);
			this.buttonPlus.removeActionListener(this.addRecordListener);
			this.addRecordListener = new OpenUploadableChooserActionListener(this);
			this.buttonPlus.addActionListener(this.addRecordListener);
			this.buttonPlus2.addActionListener(this.addRecordListener);
		} catch (final Exception e) {
			DocumentationTable.logger.error(null, e);
		}
	}

	@Override
	public void init(final Map params) throws Exception {
		if (!params.containsKey("detailformopener")) {
			params.put("detailformopener", DocumentationTableDetailFormOpener.class.getName());
			params.put("form", "dummy");
		}
		this.form_id_dms_doc_field = ParseUtilsExtended.getString((String) params.get("form_id_dms_doc_field"),
				DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		if (!params.containsKey("parentkeys")) {
			params.put("parentkeys", this.form_id_dms_doc_field + ":" + DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
		}
		// Hidden insert button '+'
		if (!params.containsKey(Table.DISABLE_INSERT)) {
			params.put(Table.DISABLE_INSERT, "yes");
		}
		super.init(params);
		if (!this.parentkeys.contains(this.form_id_dms_doc_field)) {
			this.hParentkeyEquivalences.put(this.form_id_dms_doc_field, DMSNaming.DOCUMENT_ID_DMS_DOCUMENT);
			this.parentkeys.add(this.form_id_dms_doc_field);
		}
		if (this.keyFields == null) {
			this.keyFields = new Vector<>(1);
		}
		this.keyFields.clear();
		this.keyFields.add(DMSNaming.DOCUMENT_FILE_VERSION_ID_DMS_DOCUMENT_FILE_VERSION);
		this.keyFields.add(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
	}

	@Override
	public void refreshInThread(final int delay) {
		if (this.currentFilter != null) {
			DocumentationTable.this.currentFilter.clear();
		}
		this.currentIdDocument = null;
		this.refreshIfNeededInThread();
	}

	@Override
	public void openInNewWindow(final int[] modelSelectedRows) {
		// do nothing
	}

	@Override
	public void refreshInEDT(final boolean autoSizeColumns) {
		try {
			this.checkRefreshThread();
			this.requeryDocuments();
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.OK));
		} catch (final Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, DocumentationTable.logger);
			this.fireRefreshTableEvent(new RefreshTableEvent(this, RefreshTableEvent.ERROR));
		}
	}

	/**
	 * Refreshes the rows passed as parameter
	 *
	 * @param viewRowIndexes the row indexes
	 */
	@Override
	public void refreshRows(final int[] viewRowIndexes) {
		try {
			this.checkRefreshThread();
			Arrays.sort(viewRowIndexes);
			final List<Object> vRowsValues = new Vector<>();
			for (int k = 0; k < viewRowIndexes.length; k++) {
				final int viewRow = viewRowIndexes[k];
				final Map<Object, Object> kv = this.getParentKeyValues();
				// Put the row keys
				final List<?> vKeys = this.getKeys();
				for (int i = 0; i < vKeys.size(); i++) {
					final Object oKey = vKeys.get(i);
					kv.put(oKey, this.getRowKey(viewRow, oKey.toString()));
				}
				final EntityResult res = this.doQueryDocuments(kv, this.attributes);
				final Map<?, ?> hRowData = res.getRecordValues(0);
				vRowsValues.add(vRowsValues.size(), hRowData);
			}
			// Update rows data
			this.deleteRows(viewRowIndexes);

			this.addRows(viewRowIndexes, vRowsValues);
		} catch (final Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, DocumentationTable.logger);
		}
	}

	/**
	 * Refreshes the row passed as parameter.
	 *
	 * @param viewRowIndex the index to refresh
	 * @param oldkv
	 */
	@Override
	public void refreshRow(final int viewRowIndex, final Map oldkv) {
		try {
			this.checkRefreshThread();
			final Map<Object, Object> kv = this.getParentKeyValues();
			// Put the row keys
			final List<?> vKeys = this.getKeys();
			for (int i = 0; i < vKeys.size(); i++) {
				final Object oKey = vKeys.get(i);
				if ((oldkv != null) && oldkv.containsKey(oKey)) {
					kv.put(oKey, oldkv.get(oKey));
				} else {
					kv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
			}
			final long t = System.currentTimeMillis();
			final EntityResult res = this.doQueryDocuments(kv, this.attributes);
			if (res.isEmpty()) {
				this.deleteRow(viewRowIndex);
			} else {
				final long t2 = System.currentTimeMillis();
				// Update row data
				final Map<?, ?> hRowData = res.getRecordValues(0);
				final Map<Object, Object> newkv = new Hashtable<>();
				for (int i = 0; i < vKeys.size(); i++) {
					final Object oKey = vKeys.get(i);
					newkv.put(oKey, this.getRowKey(viewRowIndex, oKey.toString()));
				}
				this.updateRowData(hRowData, newkv);

				final long t3 = System.currentTimeMillis();
				DocumentationTable.logger.trace("Table: Query time: {}  ,  deleteRow-addRow time: {}", t2 - t, t3 - t2);
			}
		} catch (final Exception error) {
			MessageManager.getMessageManager().showExceptionMessage(error, DocumentationTable.logger);
		}
	}

	protected EntityResult doQueryDocuments(final Map<?, ?> filter, final List<?> attrs) throws DmsException {
		if (!filter.containsKey(DMSNaming.DOCUMENT_ID_DMS_DOCUMENT)) {
			return new EntityResultMapImpl();
		}
		return BeansFactory.getBean(IDMSService.class).fileQuery(filter, attrs);
	}

	protected void requeryDocuments() throws DmsException {
		final Serializable idDms = (Serializable) this.parentForm.getDataFieldValue(this.form_id_dms_doc_field);
		if (idDms == null) {
			this.deleteData();
			this.currentIdDocument = null;
			return;
		}
		// Consider to refresh tree ----------------------------------------
		if (!ObjectTools.safeIsEquals(idDms, this.currentIdDocument)) {
			this.currentIdDocument = idDms;
			final boolean oldIgnoreEvents = this.isIgnoreEvents();
			try {
				this.setIgnoreEvents(true);
				this.categoryTree.refreshModel(this.currentIdDocument);
			} finally {
				this.setIgnoreEvents(oldIgnoreEvents);
			}
		}

		// Refresh table --------------------------------------------------
		final Map<?, ?> kv = this.getParentKeyValues();
		if (ObjectTools.safeIsEquals(this.currentFilter, kv)) {
			return;
		}
		this.currentFilter = (Map<Serializable, Serializable>) kv;
		final EntityResult er = BeansFactory.getBean(IDMSService.class).fileQuery(kv, this.getAttributeList());
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				DocumentationTable.this.setValue(er, false);
			}
		});
	}

	@Override
	public Map getParentKeyValues() {
		final Map<Object, Object> kv = super.getParentKeyValues();
		final Object idCategory = this.getCurrentIdCategoryToFilter();
		if (idCategory != null) {
			kv.put(DMSNaming.CATEGORY_ID_CATEGORY, idCategory);
		}
		return kv;
	}

	protected Serializable getCurrentIdCategoryToFilter() {
		final TreePath selectionPath = this.categoryTree.getSelectionPath();
		if (selectionPath == null) {
			return null;
		}
		final Object ob = selectionPath.getLastPathComponent();
		if (ob instanceof DMSCategory) {
			final DMSCategory category = (DMSCategory) ob;
			if (category.getIdCategory() == null) {
				// categoría raíz
				return new SearchValue(SearchValue.NULL, null);
			} else {
				return category.getIdCategory();
			}
		} else {
			return null;
		}
	}

	@Override
	public void setParentForm(final Form form) {
		final FormComponent idDmsField = form.getElementReference(this.form_id_dms_doc_field);
		if (idDmsField == null) {
			throw new DmsRuntimeException("Field ID_DMS_DOC is mandatory in form");
		}
		((DataField) idDmsField).addValueChangeListener(new ValueChangeListener() {

			@Override
			public void valueChanged(final ValueEvent e) {
				if (e.getNewValue() == null) {
					DocumentationTable.this.deleteData();
				} else if (!ObjectTools.safeIsEquals(e.getOldValue(), e.getNewValue())) {
					DocumentationTable.this.refreshInThread(0);
				}
			}
		});
		super.setParentForm(form);
		form.getDataComponentList().remove(this.getAttribute());
	}

	@Override
	public void deleteData() {
		if (this.deleting) {
			return;
		}
		try {
			this.deleting = true;
			super.deleteData();
			this.categoryTree.deleteData();
		} finally {
			this.deleting = false;
		}
	}

	/**
	 * Deletes from the entity the specified row.
	 *
	 * @param rowIndex
	 *            the row index
	 * @return the result of the execution of the delete instruction
	 * @throws Exception
	 * @see Entity#delete(Map, int)
	 */
	@Override
	public EntityResult deleteEntityRow(final int rowIndex) throws Exception {
		if (this.isInsertingEnabled() && this.getTableSorter().isInsertingRow(rowIndex)) {
			this.getTableSorter().clearInsertingRow(this.getParentKeyValues());
		} else if (this.dataBaseRemove) {
			final IDMSService service = BeansFactory.getBean(IDMSService.class);
			final Serializable fileId = (Serializable) this.getRowKey(rowIndex, DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
			service.fileDelete(fileId);
		}
		return new EntityResultMapImpl();
	}

	public Serializable getCurrentIdDocument() {
		return this.currentIdDocument;
	}

	public void refreshIfNeededInThread() {
		try {
			this.silentDeleteData();

			// Minimum required filter
			final Object idDms = this.parentForm.getDataFieldValue(this.form_id_dms_doc_field);
			if (idDms == null) {
				return;
			}

			if ((this.refreshThread != null) && this.refreshThread.isAlive()) {
				DocumentationTable.logger
				.warn("A thread is already refreshing. Ensure to invoke to checkRefreshThread() to cancel it.");
			}
			this.hideInformationPanel();
			this.refreshThread = new DocumentationTableRefreshThread(this);
			this.refreshThread.setDelay(0);
			this.refreshThread.start();
		} catch (final Exception error) {
			DocumentationTable.logger.error(null, error);
		}
	}

	private void silentDeleteData() {
		try {
			this.ignoreCheckRefreshThread = true;
			super.deleteData();
		} finally {
			this.ignoreCheckRefreshThread = false;
		}
	}

	@Override
	public void checkRefreshThread() {
		if (!this.ignoreCheckRefreshThread) {
			super.checkRefreshThread();
		}
	}

	public DocumentationTree getCategoryTree() {
		return this.categoryTree;
	}

	public Serializable getCurrentIdCategory() {
		Serializable idCategory = this.getCurrentIdCategoryToFilter();
		if ((idCategory instanceof SearchValue) || (idCategory instanceof NullValue)) {
			idCategory = null;
		}
		return idCategory;
	}

	@Override
	public void interactionManagerModeChanged(final InteractionManagerModeEvent e) {
		if (e.getInteractionManagerMode() == InteractionManager.INSERT) {
			this.setEnabled(false);
		} else if (e.getInteractionManagerMode() == InteractionManager.UPDATE) {
			this.setEnabled(true);
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		boolean upEnabled = enabled;
		if (this.getParentForm().getInteractionManager().getCurrentMode() == InteractionManager.INSERT) {
			upEnabled = false;
		}
		super.setEnabled(upEnabled);
	}

	public boolean isIgnoreEvents() {
		return this.ignoreEvents;
	}

	/**
	 * Disable events on tree selection, because we are just setting root values.
	 *
	 * @param ignoreEvents
	 */
	public void setIgnoreEvents(final boolean ignoreEvents) {
		this.ignoreEvents = ignoreEvents;
	}

	@Override
	public EntityResult updateTable(final Map keysValues, final int viewColumnIndex,
			final TableCellEditor tableCellEditor,
			final Map otherData, final Object previousData) throws Exception {
		final Map<String, Object> av = new Hashtable<>();
		final TableSorter model = (TableSorter) this.table.getModel();
		final String col = model.getColumnName(this.table.convertColumnIndexToModel(viewColumnIndex));
		final Object newData = tableCellEditor == null ? null : tableCellEditor.getCellEditorValue();
		if (newData != null) {
			av.put(col, newData);
		} else {
			if ((tableCellEditor != null) && (tableCellEditor instanceof com.ontimize.gui.table.CellEditor)) {
				final com.ontimize.gui.table.CellEditor cE = (com.ontimize.gui.table.CellEditor) tableCellEditor;
				av.put(col, new NullValue(cE.getSQLDataType()));
			}
		}

		if (otherData != null) {
			av.putAll(otherData);
		}

		// To include calculted values in the update operation
		final Map rowData = this.getRowDataForKeys(keysValues);

		final List<String> calculatedColumns = model.getCalculatedColumnsName();
		for (int i = 0; i < calculatedColumns.size(); i++) {
			final String column = calculatedColumns.get(i);
			if (rowData.containsKey(column)) {
				av.put(column, rowData.get(column));
			}
		}

		final Map kv = com.ontimize.util.ObjectTools.clone(keysValues);

		// Keys and parentkeys
		final List vKeys = this.getKeys();
		for (int i = 0; i < vKeys.size(); i++) {
			final Object atr = vKeys.get(i);
			if (atr.equals(col)) {
				final Object oKeyValue = previousData;
				if (oKeyValue != null) {
					kv.put(atr, oKeyValue);
				}
			}
		}
		// Parentkeys with equivalences
		final List vParentkeys = this.getParentKeys();
		for (int i = 0; i < vParentkeys.size(); i++) {
			final Object atr = vParentkeys.get(i);
			final Object oParentkeyValue = this.parentForm.getDataFieldValueFromFormCache(atr.toString());
			if (oParentkeyValue != null) {
				// since 5.2074EN-0.4
				// when equivalences, we must get equivalence value for
				// parentkey insteadof atr
				kv.put(this.getParentkeyEquivalentValue(atr), oParentkeyValue);
			}
		}

		final IDMSService service = BeansFactory.getBean(IDMSService.class);
		final Serializable fileId = (Serializable) kv.get(DMSNaming.DOCUMENT_FILE_ID_DMS_DOCUMENT_FILE);
		service.fileUpdate(fileId, av, null);
		return new EntityResultMapImpl();
	}

	@Override
	public void openDetailForm(final int rowIndex) {
		opener.openDetailForm(this, rowIndex);
	}
}
