package com.ontimize.util.multilanguage;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.ComboReferenceCellRenderer;
import com.ontimize.gui.table.EditingVetoException;
import com.ontimize.gui.table.MemoCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableEditionEvent;
import com.ontimize.gui.table.TableEditorListener;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.util.multilanguage.MultilanguageEntity;
import com.ontimize.util.ObjectTools;

/**
 * This class shows a table with the translations of a column of an entity in the different
 * languages contained in the multi-language system. Allows you to update translations in any
 * language
 */
public class FormMultilanguageTable extends EJDialog implements Internationalization {

	/**
	 * Class that implements methods to save changes made in the table by the user
	 *
	 */
	protected class MultilanguageTableEditor implements TableEditorListener {

		@Override
		public void editingCanceled(final TableEditionEvent e) {
			// It is not necessary to implement this method.
		}

		@Override
		public void editingStopped(final TableEditionEvent e) {
			// It is not necessary to implement this method.
		}

		@Override
		public void editingWillStop(final TableEditionEvent e) throws EditingVetoException {
			final Map rowValues = FormMultilanguageTable.this.multilanguageTranslateTable.getRowData(e.getRow());
			if (this.checkEventCellValuesDifferences(e) && (rowValues != null)) {
				Object value = e.getValue();
				if (value == null) {
					value = "";
				}
				rowValues.put(e.getColumnId(), value);
				final Map keys = new Hashtable();
				keys.put(FormMultilanguageTable.this.getAttributeLocale(),
						rowValues.get(FormMultilanguageTable.this.getAttributeLocale()));
				final int recordIndex = FormMultilanguageTable.this.tableChanges.getRecordIndex(keys);
				if (recordIndex >= 0) {
					FormMultilanguageTable.this.tableChanges.deleteRecord(recordIndex);
				}
				FormMultilanguageTable.this.tableChanges.addRecord(rowValues);
			}
		}

		private boolean checkEventCellValuesDifferences(final TableEditionEvent e) {
			final Object oValue = e.getOldValue();
			final Object nValue = e.getValue();

			if ((oValue == null) && (nValue == null)) {
				return false;
			} else if (oValue != null) {
				return !oValue.equals(nValue);
			}
			return true;

		}

	}

	/**
	 * Variable that represents the entity name of the table. This entity works as the attribute oif the
	 * table, and doesn't exist any entity with this name.
	 */
	public static final String ATTR_ENTITY_MULTILANGUAGE_DYNAMIC_TABLE = "multilanguageDynamicTable";

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FormMultilanguageTable.class);

	/**
	 * Accept button
	 */
	protected Button acceptButton;

	/**
	 * Cancel button
	 */
	protected Button cancelButton;

	/**
	 * This listener is used to record changes in the multi-language table when cells are edited, so
	 * that changes can be sent.
	 */
	protected transient TableEditorListener addMultilanguageTableListener = new MultilanguageTableEditor();

	/**
	 * String that store the column name of the entity to translate
	 */
	protected String attribute = "";

	/**
	 * String that represent the column of the locale id
	 */
	protected String attributeLocale;

	/**
	 * Boolean indicating whether the dialog was created correctly.
	 */
	protected boolean created = false;

	/**
	 * Stores the value of the multi-language entity
	 */
	protected String entity = "";

	/**
	 * Stores the registry keys of the database from which you want to modify the translations.
	 */
	protected transient Map<String, Object> formKeys = null;

	/**
	 * Stores the locator
	 */
	protected transient EntityReferenceLocator locator = null;

	/**
	 * Refers to the table showing the translations.
	 */
	protected Table multilanguageTranslateTable = null;

	/**
	 * Stores the ResourceBundle
	 */
	protected transient ResourceBundle resourceBundle = null;

	/**
	 * {@link EntityResult} that stores modified translations before sending them to the server
	 */
	protected EntityResult tableChanges;

	/**
	 * {@link Boolean} indicating whether the table rows should be high enough to store translations of
	 * memo fields
	 */
	protected boolean expandRows;

	/**
	 * Returns the variable that indicates that the table rows will be expanded.
	 * @return <code>true</code> if the rows are expanded, <code>false</code> otherwise.
	 */
	public boolean isExpandRows() {
		return this.expandRows;
	}

	/**
	 * Sets the variable that indicates that the table rows will be expanded
	 * @param expandRows <code>true</code>
	 */
	public void setExpandRows(final boolean expandRows) {
		this.expandRows = expandRows;
	}

	/**
	 * Construct the dialog that will show the translation table. This table is maintained by calling
	 * the {@link #createAndConfigurePanelComponents()} method. If it is built correctly, the boolean
	 * {@link #created} is set to <code>true</code>. Otherwise, it launches an exception
	 * @param owner Window parent of this table. Uses to make this dialog as a modal form
	 * @param modal Indicates whether the dialog should be opened in modal mode
	 * @param locator The local {@link EntityReferenceLocator}
	 * @param entity The name of the multi-language entity
	 * @param attribute The name of the multi-language column of the entity
	 * @param formKeys The keys that identify the registry
	 * @param expandRows The {@link Boolean} that indicates whether the height of the editable columns
	 *        is increased.
	 */
	public FormMultilanguageTable(final Window owner, final boolean modal, final EntityReferenceLocator locator, final String entity,
			final String attribute, final Map<String, Object> formKeys, final boolean expandRows) {
		super(owner, ApplicationManager.getTranslation("M_ADD_MULTILANGUAGE_TRANSLATION_TITLE"), modal);
		try {
			this.locator = locator;
			this.entity = entity;
			this.attribute = attribute;
			this.formKeys = formKeys;
			this.expandRows = expandRows;
			this.setResourceBundle(ApplicationManager.getApplicationBundle());
			this.createAndConfigurePanelComponents();
			this.pack();
			this.created = true;
		} catch (final Exception e) {
			MessageDialog.showErrorMessage(owner,
					ApplicationManager.getTranslation("M_NOT_CREATE_MULTILANGUAGE_TRANSLATION"),
					(this.resourceBundle != null) ? this.resourceBundle : ApplicationManager.getApplicationBundle());
			FormMultilanguageTable.logger.error("M_NOT_CREATE_MULTILANGUAGE_TRANSLATION", e);
		}
	}

	/**
	 * Return the name of the multi-language column in the entity
	 * @return A {@link String} with the column name
	 */
	public String getAttribute() {
		return this.attribute;
	}

	/**
	 * Returns the name of the column of the local entity that identifies the corresponding language
	 * @return A {@link String} with the column name
	 */
	public String getAttributeLocale() {
		return this.attributeLocale;
	}

	/**
	 * Returns the name of the multi-language entity
	 * @return A {@link String} with the entity name
	 */
	public String getEntity() {
		return this.entity;
	}

	/**
	 * Returns the keys of the record to which the multi-language field belongs
	 * @return A {@link Map}
	 */
	public Map<String, Object> getFormKeys() {
		return this.formKeys;
	}

	/**
	 * Parse the keys of the form, and adds them to a {@link StringBuilder} to return a single
	 * {@link String} with all of them, separated by a semicolon (;)
	 * @return A {@link String} with only the name of the keys, separated by a semicolon (;)
	 */
	public String getFormKeysName() {
		final Set<String> keySet = this.getFormKeys().keySet();
		final StringBuilder builder = new StringBuilder();
		final Iterator<String> itr = keySet.iterator();
		while (itr.hasNext()) {
			builder.append(itr.next());
			if (itr.hasNext()) {
				builder.append(";");
			}
		}
		return builder.toString();
	}

	/**
	 * Parse the keys of the form, and adds them to a {@link StringBuilder} to return a single
	 * {@link String} with all of them, separated by a semicolon (;)
	 * @return A {@link String} with only the name of the keys, separated by a semicolon (;)
	 */
	public String getLocaleKeysName() {

		final Set<String> keySet = this.getFormKeys().keySet();
		final StringBuilder builder = new StringBuilder();
		final Iterator<String> itr = keySet.iterator();
		while (itr.hasNext()) {
			builder.append(itr.next());
			if (itr.hasNext()) {
				builder.append(";");
			}
		}
		return builder.toString();
	}

	/**
	 * Returns the local {@link EntityReferenceLocator}
	 * @return the local {@link EntityReferenceLocator}
	 */
	public EntityReferenceLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the table that contains the field translations.
	 * @return The table with the field translations
	 */
	public Table getMultilanguageTranslateTable() {
		return this.multilanguageTranslateTable;
	}

	@Override
	public List getTextsToTranslate() {
		return new Vector();
	}

	/**
	 * Allows to know if the dialog containing the translation table has been created correctly or not.
	 * @return <code>true</code> if the dialog has been built correctly, <code>false</code> otherwise
	 */
	public boolean isCreated() {
		return this.created;
	}

	/**
	 * Sets the value of the multi-language entity column
	 * @param attribute A {@link String} with the name of the column
	 */
	public void setAttribute(final String attribute) {
		this.attribute = attribute;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// This method is not implemented because it is not necessary to set the
		// locale.
	}

	/**
	 * Sets the value of the multi-language entity
	 * @param entity A {@link String} with the name of the entity
	 */
	public void setEntity(final String entity) {
		this.entity = entity;
	}

	/**
	 * Sets the keys of the registry
	 * @param formKeys A {@link Map} with the keys of the registry
	 */
	public void setFormKeys(final Map<String, Object> formKeys) {
		this.formKeys = formKeys;
	}

	/**
	 * Sets the value of the local {@link EntityReferenceLocator}
	 * @param locator The local {@link EntityReferenceLocator}
	 */
	public void setLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;

	}

	/**
	 * Returns <code>false</code> if cannot update the data in the translation table, <code>true</code>
	 * otherwise
	 * @return <code>true</code>if the translations was madre correct.
	 */
	protected boolean acceptButtonAction() {
		if ((this.multilanguageTranslateTable.getJTable() != null)
				&& this.multilanguageTranslateTable.getJTable().isEditing()
				&& (this.multilanguageTranslateTable.getJTable().getCellEditor() != null)) {
			this.multilanguageTranslateTable.getJTable().getCellEditor().stopCellEditing();
		}
		if (this.tableChanges.calculateRecordNumber() > 0) {
			try {
				final int sessionId = this.locator.getSessionId();
				final MultilanguageEntity multiLanguageService = (MultilanguageEntity) this.locator
						.getEntityReference(this.getEntity());

				multiLanguageService.upgradeMultilanguageTranslationTable(this.tableChanges, this.getFormKeys(),
						this.getAttribute(), this.getAttributeLocale(), sessionId);
				return true;
			} catch (final Exception e) {
				FormMultilanguageTable.logger.error("Cannot update the translated data", e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Create and configure the content panel. This panel shows the translation table and the accept and
	 * cancel buttons.
	 * @throws Exception Launch an {@link Exception}
	 */
	protected void createAndConfigurePanelComponents() throws Exception {
		this.tableChanges = new EntityResultMapImpl();
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		final Map tableParams = new Hashtable();
		tableParams.put("entity", FormMultilanguageTable.ATTR_ENTITY_MULTILANGUAGE_DYNAMIC_TABLE);
		tableParams.put("dynamic", "yes");
		tableParams.put("controls", "no");
		tableParams.put("controlsvisible", "no");
		tableParams.put("quickfilter", "no");
		tableParams.put("quickfiltervisible", "no");
		tableParams.put("translateheader", "yes");
		tableParams.put("cols", this.getAttribute());
		tableParams.put("editablecolumns", this.getAttribute());
		if (this.isExpandRows()) {
			tableParams.put("rendermemo", this.getAttribute());
		}
		this.multilanguageTranslateTable = new Table(tableParams);
		// this.multilanguageTranslateTable.getTableSorter().
		this.multilanguageTranslateTable.setResourceBundle(this.resourceBundle);
		this.multilanguageTranslateTable.setPreferredSize(new Dimension(530, 175));

		final GridBagConstraints gbcTable = new GridBagConstraints();
		gbcTable.gridwidth = 3;
		gbcTable.insets = new Insets(0, 0, 5, 0);
		gbcTable.fill = GridBagConstraints.BOTH;
		gbcTable.gridx = 0;
		gbcTable.gridy = 0;
		this.getContentPane().add(this.multilanguageTranslateTable, gbcTable);

		final Map buttonParams = new Hashtable();
		buttonParams.put("text", ApplicationManager.getTranslation("application.accept"));
		buttonParams.put("key", "btnAccept");
		buttonParams.put("icon", ImageManager.OK);
		this.acceptButton = new Button(buttonParams);
		final GridBagConstraints gbcBtnAccept = new GridBagConstraints();
		gbcBtnAccept.gridx = 1;
		gbcBtnAccept.gridy = 1;
		gbcBtnAccept.insets = new Insets(0, 0, 8, 0);
		this.getContentPane().add(this.acceptButton, gbcBtnAccept);

		final Map buttonParamsCancel = new Hashtable();
		buttonParamsCancel.put("text", ApplicationManager.getTranslation("application.cancel"));
		buttonParamsCancel.put("key", "btnCancel");
		buttonParamsCancel.put("icon", ImageManager.CANCEL);
		this.cancelButton = new Button(buttonParamsCancel);
		final GridBagConstraints gbcBtnCancel = new GridBagConstraints();
		gbcBtnCancel.gridx = 2;
		gbcBtnCancel.gridy = 1;
		gbcBtnCancel.insets = new Insets(0, 0, 8, 8);
		this.getContentPane().add(this.cancelButton, gbcBtnCancel);

		this.populateTable();
		final List v = ObjectTools.clone(this.multilanguageTranslateTable.getAttributeList());
		v.remove(this.getAttribute());
		v.remove(MultilanguageEntity.CHECK_FOR_UPDATE_STRING);

		this.attributeLocale = (String) v.get(0);

		this.multilanguageTranslateTable.addTableEditorListener(this.addMultilanguageTableListener);

		this.acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				final boolean btnSuccess = FormMultilanguageTable.this.acceptButtonAction();
				if (btnSuccess) {
					FormMultilanguageTable.this.dispose();
				} else {
					MessageDialog.showErrorMessage(FormMultilanguageTable.this, "M_NOT_UPDATED_TRANSLATIONS",
							FormMultilanguageTable.this.resourceBundle);
				}
			}
		});

		this.cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormMultilanguageTable.this.dispose();
			}
		});

		if (this.isExpandRows()) {
			final Map params = new Hashtable();
			params.put("rows", "1");
			params.put("column", this.getAttribute());
			this.multilanguageTranslateTable.setRendererForColumn(this.getAttribute(), new MemoCellRenderer());
			this.multilanguageTranslateTable.setMinRowHeight(this.multilanguageTranslateTable.getMinRowHeight() + 25);
			this.multilanguageTranslateTable.setColumnEditor(this.getAttribute(),
					new MemoMultilanguageCellEditor(params));
		}

		final Map<String, Object> cellRendererParameters = new Hashtable<String, Object>();
		cellRendererParameters.put("attr", "localeRender");
		cellRendererParameters.put("entity", "ELocale");
		cellRendererParameters.put("cod", this.attributeLocale);
		cellRendererParameters.put("cols", "LANGUAGE;COUNTRY");
		cellRendererParameters.put("visiblecols", "LANGUAGE;COUNTRY");
		cellRendererParameters.put("format", "{0}_{1};LANGUAGE;COUNTRY");
		cellRendererParameters.put("translate", "yes");
		final ComboReferenceCellRenderer cellRenderer = new ComboReferenceCellRenderer(cellRendererParameters);
		this.multilanguageTranslateTable.setRowNumberColumnVisible(false);
		this.multilanguageTranslateTable.setReferenceLocator(this.locator);
		this.multilanguageTranslateTable.setRendererForColumn(this.attributeLocale, cellRenderer);
	}

	/**
	 * This method consults the entity to obtain the translations in the different languages and
	 * establish them as the table value
	 */
	protected void populateTable() {
		if (this.getMultilanguageTranslateTable() != null) {
			try {
				// this.multilanguageTranslateTable.
				final int sessionId = this.locator.getSessionId();
				final MultilanguageEntity multiLanguageService = (MultilanguageEntity) this.locator
						.getEntityReference(this.getEntity());
				final EntityResult populateMultilanguageTranslationTable = multiLanguageService
						.populateMultilanguageTranslationTable(this.getEntity(), this.getAttribute(),
								this.getFormKeys(), sessionId);
				final List<String> orderList = this.sortMultilanguageOrder(populateMultilanguageTranslationTable);
				populateMultilanguageTranslationTable.setColumnOrder(orderList);
				this.multilanguageTranslateTable.setValue(populateMultilanguageTranslationTable);
				final List visibleColumns = new Vector(this.multilanguageTranslateTable.getVisibleColumns());
				visibleColumns.remove(MultilanguageEntity.CHECK_FOR_UPDATE_STRING);
				this.multilanguageTranslateTable.setVisibleColumns(visibleColumns);

			} catch (final Exception e) {
				FormMultilanguageTable.logger.error("Cannot populate the table with the multilanguage data.", e);
			}
		}

	}

	/**
	 * Creates the order columns for multi-language table, establishing the name of the attribute to
	 * modify at the end of the order columns
	 * @param populateMultilanguageTranslationTable The {@link EntityResult} with the data ready to set
	 */
	protected List<String> sortMultilanguageOrder(final EntityResult populateMultilanguageTranslationTable) {
		final List<String> orderList = new ArrayList();
		for (final Object o : populateMultilanguageTranslationTable.keySet()) {
			if (!((String) o).equalsIgnoreCase(this.getAttribute())) {
				orderList.add((String) o);
			}
		}
		orderList.add(this.getAttribute());
		return orderList;
	}

}
