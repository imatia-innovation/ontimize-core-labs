package com.ontimize.gui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.button.AttachmentFileButton;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.button.DeleteAttachmentButton;
import com.ontimize.gui.button.DownloadAttachmentFileButton;
import com.ontimize.gui.button.QueryButton;
import com.ontimize.gui.container.CollapsiblePanel;
import com.ontimize.gui.container.SplitPane;
import com.ontimize.gui.container.Tab;
import com.ontimize.gui.container.TabPanel;
import com.ontimize.gui.field.AdvancedDataComponent;
import com.ontimize.gui.field.CalculatedCurrencyDataField;
import com.ontimize.gui.field.CalculatedDataField;
import com.ontimize.gui.field.CalculatedTextDataField;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.FormComponent;
import com.ontimize.gui.field.HTMLDataField;
import com.ontimize.gui.field.SliderDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.manager.ITreeFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.tree.OTreeNode;
import com.ontimize.jee.common.db.CancellableQueryEntity;
import com.ontimize.jee.common.db.DynamicMemoryEntity;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.query.QueryExpression;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.ConnectionOptimizer;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.gui.table.TableAttribute;
import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.util.ObjectTools;

/**
 * BasicInteractionManager is the class that manages the {@link Form} instances, extending the
 * default behavior provided by the {@link InteractionManager} class. The BasicInteractionManager
 * manages the events related to the default buttons, that is, the query, insert, delete and update
 * buttons. The keys of these buttons must be the following ones:
 * <ul>
 * <li>query
 * <li>insert
 * <li>update
 * <li>delete
 * </ul>
 */
public class BasicInteractionManager extends InteractionManager {

	static final Logger logger = LoggerFactory.getLogger(BasicInteractionManager.class);

	public static final String S_CORRECT_INSERT = "interactionmanager.insertion_successful";

	public static final String S_INCORRECT_INSERT = "interactionmanager.error_happened_while_trying_to_insert";

	public static final String S_CORRECT_UPDATE = "interactionmanager.update_operation_successful";

	public static final String S_INCORRECT_UPDATE = "interactionmanager.error_ocurred_while_trying_to_update";

	public static final String S_CORRECT_DELETE = "interactionmanager.delete_operation_successful";

	public static final String S_INCORRECT_DELETE = "interactionmanager.error_happened_while_trying_to_delete";

	public static final String M_DELETE_CONFIRM = "interactionmanager.please_confirm_proceed_with_deletion";

	public static final String M_UPDATE_CONFIRM = "interactionmanager.please_confirm_proceed_with_update";

	public static final String M_FILL_ALL_REQUIRED_FIELDS = "interactionmanager.fill_all_required_fields";

	public static String M_MODIFIED_DATA_APPLY_CHANGES = "interactionmanager.apply_changes_to_modified_data";

	/**
	 * When true, shows a message prior performing any deletion.
	 */
	public static boolean CONFIRM_DELETE_DEFAULT_VALUE = false;

	/**
	 * The advanced query mode.
	 */
	public static boolean AVANCED_QUERY_DEFAULT_VALUE = true;

	public static boolean defaultScriptEnabled = false;

	/**
	 * When true, closes the detail form after performing an insertion.
	 */
	public static boolean CLOSE_DETAIL_FORM_AFTER_INSERT_DEFAULT_VALUE = false;

	protected Map keysValuesLastQuery = null;

	protected Map keysValues = null;

	protected QueryExpression expression = null;

	protected Map attributesValues = null;

	protected List attributes = null;

	protected boolean detailForm = false;

	protected boolean afterUpdate = true;

	private static final String M_LICENSE_PERMISSION_ERROR = "M_LICENSE_PERMISSION_ERROR";

	/**
	 * Makes the InteractionManager to show a message prior performing the deletion, asking to the user
	 * to do so.
	 */
	protected boolean showDeleteConfirmMessage = BasicInteractionManager.CONFIRM_DELETE_DEFAULT_VALUE;

	protected boolean searchBetweenResults = false;

	public QueryListener queryListener = new QueryListener();

	public AvancedQueryListener avancedQueryListener = new AvancedQueryListener();

	public InsertListener insertListener = new InsertListener();

	public UpdateListener updateListener = new UpdateListener();

	public DeleteListener deleteListener = new DeleteListener();

	protected Window formAncestor = null;

	protected boolean stayInRecordAfterInsert = false;

	protected boolean closeDetailFormAfterInsert = BasicInteractionManager.CLOSE_DETAIL_FORM_AFTER_INSERT_DEFAULT_VALUE;

	protected boolean addColumnsToTableView = false;

	protected boolean scriptEnabled = BasicInteractionManager.defaultScriptEnabled;

	protected IFormInteractionScriptManager formInteractionScriptManager;

	public static String defaultInteractionScriptManagerClassName = "com.ontimize.scripting.DefaultScriptInteractionManager";

	@Override
	public void setInsertMode() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isInsertModeScript()) {
			final boolean scriptResult = this.formInteractionScriptManager.setInsertMode();
			if (scriptResult) {
				return;
			}
		}

		super.setInsertMode();
		if (this.managedForm.getDetailComponent() != null) {
			this.detailForm = true;
		}
		this.searchBetweenResults = false;
		this.managedForm.setAdvancedQueryModeAll(false);
		for (int i = 0; i < this.managedForm.componentList.size(); i++) {
			final Object comp = this.managedForm.componentList.get(i);
			if (comp instanceof QueryButton) {
				((FormComponent) comp).setEnabled(true);
			}
		}
	}

	public IFormInteractionScriptManager getFormInteractionScriptManager() {
		return this.formInteractionScriptManager;
	}

	/**
	 * This method determines whether the form will remain in a record after being inserted, to check
	 * the new record information, or it will be remain in insert mode to make another insertion.
	 * @param stayInRecordAfterInsert if true, the form will show a record after its insertion
	 */
	public void setStayInRecordAfterInsert(final boolean stayInRecordAfterInsert) {
		this.stayInRecordAfterInsert = stayInRecordAfterInsert;
	}

	@Override
	public void setQueryInsertMode() {
		if ((this.formInteractionScriptManager != null)
				&& !this.formInteractionScriptManager.isQueryInsertModeScript()) {
			final boolean scriptResult = this.formInteractionScriptManager.setQueryInsertMode();
			if (scriptResult) {
				return;
			}
		}
		super.setQueryInsertMode();
		if (this.managedForm.getDetailComponent() != null) {
			this.detailForm = true;
		}
		this.searchBetweenResults = false;
		this.managedForm.setAdvancedQueryModeAll(false);
		for (int i = 0; i < this.managedForm.componentList.size(); i++) {
			final Object comp = this.managedForm.componentList.get(i);
			if (comp instanceof QueryButton) {
				((FormComponent) comp).setEnabled(false);
			}
		}
	}

	@Override
	public void setQueryMode() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isQueryModeScript()) {
			final boolean scriptResult = this.formInteractionScriptManager.setQueryMode();
			if (scriptResult) {
				return;
			}
		}
		super.setQueryMode();

		// Put the fields in advanced query mode
		if (this.managedForm.getDetailComponent() != null) {
			this.detailForm = true;
		}
		for (int i = 0; i < this.managedForm.componentList.size(); i++) {
			final Object comp = this.managedForm.componentList.get(i);
			if (comp instanceof CheckDataField) {
				((CheckDataField) comp).setAdvancedQueryMode(true);
			} else if (comp instanceof SliderDataField) {
				((SliderDataField) comp).setAdvancedQueryMode(true);
			} else if (comp instanceof QueryButton) {
				((FormComponent) comp).setEnabled(true);
			} else if (comp instanceof HTMLDataField) {
				((AdvancedDataComponent) comp).setAdvancedQueryMode(true);
			} else if (comp instanceof AdvancedDataComponent) {
				((AdvancedDataComponent) comp)
				.setAdvancedQueryMode(BasicInteractionManager.AVANCED_QUERY_DEFAULT_VALUE);
			}
		}
	}

	@Override
	public void setUpdateMode() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isUpdateModeScript()) {
			final boolean scriptResult = this.formInteractionScriptManager.setUpdateMode();
			if (scriptResult) {
				return;
			}
		}

		super.setUpdateMode();
		this.searchBetweenResults = false;
		if (this.managedForm.getDetailComponent() != null) {
			this.detailForm = true;
		}
		this.managedForm.setAdvancedQueryModeAll(false);
		for (int i = 0; i < this.managedForm.componentList.size(); i++) {
			final Object comp = this.managedForm.componentList.get(i);
			if (comp instanceof QueryButton) {
				((FormComponent) comp).setEnabled(true);
			} else if (comp instanceof AttachmentFileButton) {
				((FormComponent) comp).setEnabled(true);
			} else if (comp instanceof DownloadAttachmentFileButton) {
				((FormComponent) comp).setEnabled(true);
			} else if (comp instanceof DeleteAttachmentButton) {
				((FormComponent) comp).setEnabled(true);
			}
		}

	}

	/**
	 * Query button default listener. If the event occurs when the form is in the query mode, checkQuery
	 * is called. If this method returns true, this implies than the query can go on, and a thread is
	 * launched to perform this operation. This thread can be canceled if the operation takes too long.
	 * <p>
	 * The filter of the query are the values of the non empty fields in the form. This values are
	 * established in checkQuery, both the values of the filter(WHERE) and the database fields to query.
	 * With the result of this query the form fields are filled in.
	 * <p>
	 */
	public class QueryListener implements ActionListener {

		public QueryListener() {
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (InteractionManager.NEWMODE) {
				if (BasicInteractionManager.this.currentMode == InteractionManager.UPDATE) {
					BasicInteractionManager.this.deleteFieldsListener.actionPerformed(event);
				}
				if (BasicInteractionManager.this.currentMode == InteractionManager.INSERT) {
					BasicInteractionManager.this.deleteFieldsListener.actionPerformed(event);
				}
			}
			this.action(event, false);
		}

		/**
		 * The method that performs the query. It receives an event, coming from the query button, and the
		 * mode in which the query is being performed.
		 * @param event
		 * @param advancedQuery if true, the advanced query mode will be enabled, allowing to pass complex
		 *        expressions to the server to perform the operation
		 */
		protected void action(final ActionEvent event, final boolean advancedQuery) {
			if ((BasicInteractionManager.this.currentMode == InteractionManager.QUERY)
					|| (advancedQuery
							&& (BasicInteractionManager.this.currentMode == InteractionManager.QUERYINSERT))) {
				if (advancedQuery || BasicInteractionManager.this.checkQuery()) {
					try {
						if (advancedQuery) {
							BasicInteractionManager.this.expression = com.ontimize.db.query.QueryBuilder
									.showQueryBuilder((Component) event.getSource(),
											BasicInteractionManager.this.managedForm.getEntityName(),
											ApplicationManager.getApplication().getResourceBundle(),
											BasicInteractionManager.this.managedForm.getFormManager().getReferenceLocator(),
											(QueryExpression) null, true, true);
						} else {
							BasicInteractionManager.this.expression = null;
						}

						BasicInteractionManager.this.managedForm
						.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						final Entity entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(BasicInteractionManager.this.managedForm.getEntityName());

						final OperationThread op = new OperationThread() {

							private String idOperation = null;

							/**
							 * Cancels the query operation. This could be done when the query takes too much time to be
							 * performed.
							 */
							@Override
							public void cancel() {
								super.cancel();
								if (this.idOperation != null) {
									if (entity instanceof CancellableQueryEntity) {
										try {
											((CancellableQueryEntity) entity).cancelOperation(this.idOperation);
										} catch (final Exception e) {
											BasicInteractionManager.logger.error("Error canceling Entity Operation", e);
										}
									}
								}
							}

							/**
							 * Executes the query.
							 */
							@Override
							public void run() {
								this.hasStarted = true;
								try {
									if (BasicInteractionManager.this.managedForm.getResourceBundle() != null) {
										this.status = BasicInteractionManager.this.managedForm.getResourceBundle()
												.getString("performing_query");
									} else {
										this.status = ApplicationManager
												.getTranslation("interactionmanager.performing_query");
									}
								} catch (final Exception e) {
									BasicInteractionManager.logger.trace(null, e);
									this.status = ApplicationManager
											.getTranslation("interactionmanager.performing_query");
								}
								try {

									if ((BasicInteractionManager.this.expression != null)
											&& (BasicInteractionManager.this.expression.getExpression() != null)) {
										BasicInteractionManager.this.keysValues.put(
												ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
												BasicInteractionManager.this.expression.getExpression());
									}

									if (entity instanceof CancellableQueryEntity) {

										this.idOperation = ((CancellableQueryEntity) entity)
												.getOperationUniqueIdentifier();

										if ((BasicInteractionManager.this.expression != null)
												&& (BasicInteractionManager.this.expression.getExpression() != null)) {
											BasicInteractionManager.this.keysValues.put(
													ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
													BasicInteractionManager.this.expression.getExpression());
										}

										this.res = ((CancellableQueryEntity) entity).query(
												BasicInteractionManager.this.keysValues,
												BasicInteractionManager.this.attributes,
												BasicInteractionManager.this.formManager.getReferenceLocator()
												.getSessionId(),
												this.idOperation);
									} else {
										this.res = entity.query(BasicInteractionManager.this.keysValues,
												BasicInteractionManager.this.attributes,
												BasicInteractionManager.this.formManager.getReferenceLocator()
												.getSessionId());
									}

									if (this.isCancelled()) {
										return;
									}
									BasicInteractionManager.this.keysValuesLastQuery = BasicInteractionManager.this.keysValues;
									// Test the net speed
									final int iCompressionThreshold = ConnectionManager.getCompresionThreshold(
											((EntityResult) this.res).getBytesNumber(),
											((EntityResult) this.res).getStreamTime());
									if (iCompressionThreshold > 0) {
										final ConnectionOptimizer opt = ConnectionManager.getConnectionOptimizer();
										if ((opt != null) && (BasicInteractionManager.this.formManager
												.getReferenceLocator() instanceof ClientReferenceLocator)) {
											try {
												opt.setDataCompressionThreshold(
														((ClientReferenceLocator) BasicInteractionManager.this.formManager
																.getReferenceLocator()).getUser(),
														BasicInteractionManager.this.formManager.getReferenceLocator()
														.getSessionId(),
														iCompressionThreshold);
												BasicInteractionManager.logger.debug("Compression threshold set to "
														+ ((ClientReferenceLocator) BasicInteractionManager.this.formManager
																.getReferenceLocator()).getUser()
														+ " "
														+ BasicInteractionManager.this.formManager.getReferenceLocator()
														.getSessionId()
														+ " in : " + iCompressionThreshold);
											} catch (final Exception e) {
												BasicInteractionManager.logger
												.error(" Error setting compression threshold", e);
											}
										}
									}
								} catch (final NoSuchObjectException ex) {
									BasicInteractionManager.logger.trace(null, ex);
									this.res = new EntityResultMapImpl();
									((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
									((EntityResult) this.res).setMessage(ex.getMessage());
									if (BasicInteractionManager.this.formManager
											.getReferenceLocator() instanceof UtilReferenceLocator) {
										try {
											((UtilReferenceLocator) BasicInteractionManager.this.formManager
													.getReferenceLocator()).removeEntity(
															BasicInteractionManager.this.managedForm.getEntityName(),
															BasicInteractionManager.this.formManager.getReferenceLocator()
															.getSessionId());
										} catch (final Exception e) {
											BasicInteractionManager.logger.error(null, e);
										}
									}
									return;
								} catch (final Exception e) {
									if (this.isCancelled()) {
										return;
									}
									BasicInteractionManager.logger.error(null, e);
									this.res = new EntityResultMapImpl();
									((EntityResult) this.res).setCode(EntityResult.OPERATION_WRONG);
									((EntityResult) this.res).setMessage(e.getMessage());
								}
								this.hasFinished = true;
							}
						};

						final Window w = SwingUtilities.getWindowAncestor(BasicInteractionManager.this.managedForm);
						if (w instanceof Frame) {
							ApplicationManager.proccessOperation((Frame) w, op, 2000);
						} else {
							ApplicationManager.proccessOperation((Dialog) w, op, 2000);
						}
						final EntityResult result = (EntityResult) op.getResult();
						if (result.getCode() == EntityResult.OPERATION_WRONG) {
							BasicInteractionManager.this.managedForm.message(result.getMessage(), Form.ERROR_MESSAGE,
									null, result.getMessageParameter());
						} else {
							if (!InteractionManager.NEWMODE) {
								BasicInteractionManager.this.managedForm.setAdvancedQueryModeAll(false);
								BasicInteractionManager.this.managedForm
								.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(result));
							}
							if (result.isEmpty()) {
								if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
									BasicInteractionManager.this.managedForm.message(result.getMessage(),
											Form.INFORMATION_MESSAGE, null, result.getMessageParameter());
								} else {
									BasicInteractionManager.this.managedForm.message(
											"interactionmanager.no_result_found", Form.INFORMATION_MESSAGE, null,
											result.getMessageParameter());
									if (!InteractionManager.NEWMODE) {
										BasicInteractionManager.this.setQueryInsertMode();
									}
								}
							} else {
								if (InteractionManager.NEWMODE) {
									BasicInteractionManager.this.managedForm.setAdvancedQueryModeAll(false);
									BasicInteractionManager.this.managedForm
									.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(result));
								}
								if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
									BasicInteractionManager.this.managedForm.message(result.getMessage(),
											Form.INFORMATION_MESSAGE, null, result.getMessageParameter());
								}
								BasicInteractionManager.this.setUpdateMode();
								if (result.calculateRecordNumber() > 1) {
									if ((BasicInteractionManager.this.managedForm.additionalTableViewColumns != null)
											&& (BasicInteractionManager.this.managedForm.additionalTableViewColumns
													.size() > 0)
											&& BasicInteractionManager.this.managedForm.multipleData) {
										BasicInteractionManager.this.managedForm.showTableView();
									}
								}
							}
						}
					} catch (final Exception e) {
						BasicInteractionManager.logger.error(null, e);
						BasicInteractionManager.this.managedForm.message(e.getMessage(), Form.ERROR_MESSAGE, e);
						BasicInteractionManager.this.setQueryInsertMode();
					} finally {
						BasicInteractionManager.this.managedForm.setCursor(Cursor.getDefaultCursor());
					}
				}
			} else {
				BasicInteractionManager.this.setQueryMode();
			}
		}

	}

	/**
	 * Class that implements the listener to the standard feature AdvancedQuery. This feature allows to
	 * insert complex query conditions when searching.
	 */
	public class AvancedQueryListener extends QueryListener {

		public AvancedQueryListener() {
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			this.action(event, true);
		}

	}

	/**
	 * Default insertion listener. Is associated to the insert button. Updates the tree or the table
	 * related to the managed {@link Form} if them exist. After an insertion, it can be configured
	 * whether the form has to remain in the register that has just been inserted or if it has to set
	 * the insert mode state, in order to be prepared to insert a new register.
	 * <p>
	 * Once the insertion has been done, if the form has a related the this will be updated, and if the
	 * form is raised from a table, the table will be updated.
	 */
	public class InsertListener implements ActionListener {

		protected EntityResult lastResult = null;

		public InsertListener() {
		}

		/**
		 * Returns the last insert operation result.
		 * @return the last insert operation result
		 */
		public EntityResult getLastResult() {
			return this.lastResult;
		}

		/**
		 * Method called when an insert operation finalizes with error.
		 * @param result
		 * @param entity
		 * @throws Exception
		 */
		protected void postIncorrectInsert(final EntityResult result, final Entity entity) throws Exception {
			this.lastResult = result;
		}

		/**
		 * Method called after performing an insertion successfully. Updates the tree, and the form when
		 * necessary.
		 * @param result operation result
		 * @param entity the entity used to perform the operation
		 * @throws Exception
		 */
		protected void postCorrectInsert(final EntityResult result, Entity entity) throws Exception {
			this.lastResult = result;
			if (result.getCode() != EntityResult.OPERATION_WRONG) {
				BasicInteractionManager.this.managedForm.fireDataRecordChange(
						new DataRecordEvent(BasicInteractionManager.this.managedForm, DataRecordEvent.INSERT, null,
								BasicInteractionManager.this.attributesValues, result));
			}
			if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				BasicInteractionManager.this.managedForm.setStatusBarText(result.getMessage(), 3000);
			} else {
				BasicInteractionManager.this.managedForm.setStatusBarText(BasicInteractionManager.S_CORRECT_INSERT,
						3000);
			}
			// Check if attributesValues has AttributeTable then clear
			// DynamicMemoryEntity
			this.clearDynamicEntities();

			// Check if managed form is a detail form
			if (BasicInteractionManager.this.managedForm.getDetailComponent() == null) {
				this.postCorrectInsertOnForm(result);
			} else {
				BasicInteractionManager.this.managedForm.deleteDataFields();
				if (BasicInteractionManager.this.afterUpdate) {
					// After the insert update the table, but only the inserted
					// record
					// Make a query, if no results found the the row is not
					// added
					final IDetailForm detailForm = BasicInteractionManager.this.managedForm.getDetailComponent();
					if (detailForm != null) {
						final com.ontimize.gui.table.Table sourceTable = detailForm.getTable();
						// Check the table entity. Usually is the same as the
						// detail form but not always
						entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(sourceTable.getEntityName());

						// Get the table keys to know the inserted record keys.
						// If not all keys are found then refresh

						final Pair<Object, Object> pair = this.getAutonumericalValues(result);

						final Object autonumerical = pair.getFirst();
						final Object autonumericalKey = pair.getSecond();

						final Map queryFilter = new Hashtable();
						final List tableKeys = sourceTable.getKeys();
						boolean allKeysFound = true;
						for (int i = 0; i < tableKeys.size(); i++) {
							Object oParentValue = null;
							if (tableKeys.get(i).equals(detailForm.getTableFieldName(autonumericalKey))) {
								oParentValue = autonumerical;
							} else {
								oParentValue = BasicInteractionManager.this.attributesValues
										.get(detailForm.getFormFieldName(tableKeys.get(i)));
							}
							if (oParentValue == null) {
								// Check for ReferenceFieldAttribute with
								// attribute tableKeys.get(i)
								allKeysFound = false;
								final Enumeration keys = Collections
										.enumeration(BasicInteractionManager.this.attributesValues.keySet());
								while (keys.hasMoreElements()) {
									final Object key = keys.nextElement();
									if (key instanceof ReferenceFieldAttribute) {
										if (((ReferenceFieldAttribute) key).getAttr()
												.equals(detailForm.getFormFieldName(tableKeys.get(i)))) {
											oParentValue = BasicInteractionManager.this.attributesValues.get(key);
											allKeysFound = true;
											break;
										}
									}
								}
								if (!allKeysFound) {
									break;
								}
							}
							queryFilter.put(tableKeys.get(i), oParentValue);
						}
						// Put the parentkeys
						final Map parentKeysValues = sourceTable.getParentKeyValues();
						final Enumeration keys = Collections.enumeration(parentKeysValues.keySet());
						while (keys.hasMoreElements()) {
							final Object key = keys.nextElement();
							queryFilter.put(key, parentKeysValues.get(key));
						}
						try {
							if (allKeysFound) {
								final List vAttributes = sourceTable.getAttributeList();
								final List at = BasicInteractionManager.this.getQueryAttributes();
								for (int i = 0; i < at.size(); i++) {
									if (!vAttributes.contains(detailForm.getTableFieldName(at.get(i)))) {
										vAttributes.add(at.get(i));
									}
								}
								final EntityResult res = entity.query(queryFilter, vAttributes,
										BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());
								if (res.getCode() == EntityResult.OPERATION_WRONG) {
									BasicInteractionManager.logger.debug(res.getMessage());
									sourceTable.refresh();
									final int row = sourceTable.getRowForKeys(BasicInteractionManager.this.attributesValues);
									sourceTable.setSelectedRow(row);
								} else {
									if (res.isEmpty()) {
										sourceTable.refresh();
										BasicInteractionManager.logger
										.info("InsertListener: Table was completely updated");
									} else {
										// insert a new row
										final Map hNewRow = new Hashtable();
										final Enumeration enumKeys = res.keys();
										while (enumKeys.hasMoreElements()) {
											final Object oKey = enumKeys.nextElement();
											final Object oValue = ((List) res.get(oKey)).get(0);
											if (oValue != null) {
												hNewRow.put(oKey, oValue);
											}
										}
										sourceTable.addRow(hNewRow);
										final Map av2 = ObjectTools
												.clone(BasicInteractionManager.this.attributesValues);
										if ((autonumerical != null) && (autonumericalKey != null)) {
											av2.put(autonumericalKey, autonumerical);
										}
										final int row = sourceTable.getRowForKeys(detailForm.valuesToTable(av2));
										sourceTable.setSelectedRow(row);
										if (BasicInteractionManager.this.stayInRecordAfterInsert) {
											BasicInteractionManager.this.setQueryInsertMode();
											detailForm.setKeys(com.ontimize.jee.common.util.EntityResultUtils.toMap(res), 0);
											// BasicInteractionManager.this.managedForm.updateDataFields(detailForm.valuesToForm(res));
											BasicInteractionManager.this.setUpdateMode();
											if (detailForm instanceof TabbedDetailForm) {
												((TabbedDetailForm) detailForm).updateTitle();
											}
											return;
										}
									}
								}
							} else {
								// Not all keys found then refresh the table
								BasicInteractionManager.logger.info("InsertListener: Table was completely updated");
								sourceTable.refresh();
							}
						} catch (final Exception e) {
							BasicInteractionManager.logger.error(null, e);
							// If an error happens update all table
							sourceTable.refresh();
							final int row = sourceTable.getRowForKeys(BasicInteractionManager.this.attributesValues);
							sourceTable.setSelectedRow(row);

						}
					}
				}
				BasicInteractionManager.this.setInsertMode();
				if (BasicInteractionManager.this.closeDetailFormAfterInsert) {
					BasicInteractionManager.this.managedForm.getDetailComponent().hideDetailForm();
				}
			}

		}

		/**
		 * Method to reduce the complexity of {@link #postCorrectInsert(EntityResult, Entity)}
		 * @param result
		 */
		protected void postCorrectInsertOnForm(final EntityResult result) {
			// If it is not a detail form then a tree can exist
			// If tree does not exist then is a normal form without tree
			if (BasicInteractionManager.this.afterUpdate) {
				final List keys = BasicInteractionManager.this.managedForm.getKeys();
				final List parentkeys = BasicInteractionManager.this.managedForm.getParentKeys();

				Map nodeKeysValues = null;
				boolean hasKeys = false;
				if (!result.isEmpty()) {
					final Enumeration enumKeys = result.keys();
					while (enumKeys.hasMoreElements()) {
						if (keys.contains(enumKeys.nextElement())) {
							hasKeys = true;
							break;
						}
					}
				}
				if (hasKeys) {
					nodeKeysValues = new Hashtable();
					for (int i = 0; i < keys.size(); i++) {
						final Object key = keys.get(i);
						if (result.containsKey(key)) {
							nodeKeysValues.put(key, result.get(key));
						}
						if (BasicInteractionManager.this.attributesValues.containsKey(key)) {
							nodeKeysValues.put(key, BasicInteractionManager.this.attributesValues.get(key));
						}

					}
					for (int i = 0; i < parentkeys.size(); i++) {
						final Object parentkey = parentkeys.get(i);
						if (BasicInteractionManager.this.attributesValues.containsKey(parentkey)) {
							nodeKeysValues.put(parentkey, BasicInteractionManager.this.attributesValues.get(parentkey));
						}
					}
				} else {
					nodeKeysValues = BasicInteractionManager.this.attributesValues;
				}

				// Get the form keys and parentkeys.
				// If this is an insertion autonumerical can exist
				// If autonumerical exist we use it to update the tree

				if ((BasicInteractionManager.this.formManager instanceof ITreeFormManager)
						&& (((ITreeFormManager) BasicInteractionManager.this.formManager).getTree() != null)) {
					final ITreeFormManager treeFormManager = (ITreeFormManager) BasicInteractionManager.this.formManager;
					if (!BasicInteractionManager.this.stayInRecordAfterInsert) {
						treeFormManager.insertedNode(BasicInteractionManager.this.managedForm.getAssociatedTreePath(),
								nodeKeysValues);
						BasicInteractionManager.this.managedForm.updateDataFields(null);
						BasicInteractionManager.this.setInsertMode();
					} else {
						// To stay in the record after the insertion then we
						// need
						// the keys to update the record in the form
						treeFormManager.insertedNode(BasicInteractionManager.this.managedForm.getAssociatedTreePath(),
								nodeKeysValues, true);
						final EntityResult res = BasicInteractionManager.this.query(nodeKeysValues,
								BasicInteractionManager.this.getQueryAttributes());
						if ((res != null) && (res.getCode() != EntityResult.OPERATION_WRONG) && !res.isEmpty()
								&& (res.calculateRecordNumber() == 1)) {
							BasicInteractionManager.this.setQueryInsertMode();
							BasicInteractionManager.this.managedForm.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
							BasicInteractionManager.this.setUpdateMode();
						} else {
							BasicInteractionManager.logger
							.info("Impossible to stay in the insert record, because the query using the values of the keys did not return right results");
							if (res.getCode() == EntityResult.OPERATION_WRONG) {
								BasicInteractionManager.logger.debug(res.getMessage());
							}
							final int recordCount = res.calculateRecordNumber();
							if (recordCount != 1) {
								BasicInteractionManager.logger.info("Inserted record query returned: {} results",
										recordCount);
							}
							treeFormManager.insertedNode(
									BasicInteractionManager.this.managedForm.getAssociatedTreePath(), nodeKeysValues);
							BasicInteractionManager.this.managedForm.updateDataFields(null);
							BasicInteractionManager.this.setInsertMode();
						}
					}
				} else {
					if (!BasicInteractionManager.this.stayInRecordAfterInsert) {
						BasicInteractionManager.this.managedForm.updateDataFields(new Hashtable());
						BasicInteractionManager.this.setInsertMode();
					} else {
						// To stay in the record after the insertion then we
						// need
						// the keys to update the record in the form
						final EntityResult res = BasicInteractionManager.this.query(nodeKeysValues,
								BasicInteractionManager.this.getQueryAttributes());
						if ((res != null) && (res.getCode() != EntityResult.OPERATION_WRONG) && !res.isEmpty()
								&& (res.calculateRecordNumber() == 1)) {
							BasicInteractionManager.this.setQueryInsertMode();
							BasicInteractionManager.this.managedForm.updateDataFields(com.ontimize.jee.common.util.EntityResultUtils.toMap(res));
							BasicInteractionManager.this.setUpdateMode();
						} else {
							// Impossible to stay in the insert record,
							// because the
							// query using the values of the keys did not
							// return
							// right results
							BasicInteractionManager.logger.debug(this.getClass().toString()
									+ " : Impossible to stay in the insert record, because the query using the values of the keys did not return right results");
							if (res.getCode() == EntityResult.OPERATION_WRONG) {
								BasicInteractionManager.logger.debug(res.getMessage());
							}
							final int recordCount = res.calculateRecordNumber();
							if (recordCount != 1) {
								BasicInteractionManager.logger
								.debug("Inserted record query returned: " + recordCount + " results");
							}
							BasicInteractionManager.this.managedForm.updateDataFields(null);
							BasicInteractionManager.this.setInsertMode();
						}
					}
				}
			} else {
				if (BasicInteractionManager.this.formManager instanceof ITreeFormManager) {
					((ITreeFormManager) BasicInteractionManager.this.formManager).dataInserted();
				}
				BasicInteractionManager.this.managedForm.deleteDataFields();
				BasicInteractionManager.this.setInsertMode();
			}
		}


		/**
		 * Method to reduce the complexity of {@link #postCorrectInsert(EntityResult, Entity)}
		 * @param result
		 */
		protected Pair<Object, Object> getAutonumericalValues(final EntityResult result) {

			final Pair<Object, Object> pair = new Pair<Object, Object>();

			Object autonumerical = null;
			Object autonumericalKey = null;

			final List formKeys = BasicInteractionManager.this.managedForm.getKeys();
			if (!result.isEmpty()) {
				for (final Object formKey : formKeys) {
					if (result.containsKey(formKey)) {
						autonumericalKey = formKey;
						autonumerical = result.get(autonumericalKey);
						break;
					}
				}
				if (autonumericalKey == null) {
					final Enumeration enumKeys = result.keys();
					autonumericalKey = enumKeys.nextElement();
					autonumerical = result.get(autonumericalKey);
				}
			}

			pair.setFirst(autonumerical);
			pair.setSecond(autonumericalKey);

			return pair;
		}

		protected void clearDynamicEntities() {
			final Enumeration attributes = Collections
					.enumeration(BasicInteractionManager.this.attributesValues.keySet());
			while (attributes.hasMoreElements()) {
				final Object current = attributes.nextElement();
				if (current instanceof TableAttribute) {
					try {
						final FormComponent component = BasicInteractionManager.this.managedForm
								.getElementReference(current.toString());
						if (component instanceof Table) {
							final String entityName = ((Table) component).getEntityName();
							final Entity dynamicEntity = BasicInteractionManager.this.managedForm.getFormManager()
									.getReferenceLocator()
									.getEntityReference(entityName);
							if (dynamicEntity instanceof DynamicMemoryEntity) {
								((DynamicMemoryEntity) dynamicEntity).clear();
							}
						}
					} catch (final Exception ex) {
						BasicInteractionManager.logger.error(null, ex);
					}
				}
			}
		}

		/**
		 * Performs the Insert operation. Checks the {@link BasicInteractionManager#checkInsert()} method
		 * prior performing the operation.
		 */
		@Override
		public void actionPerformed(final ActionEvent event) {
			if (InteractionManager.NEWMODE) {
				if (BasicInteractionManager.this.currentMode == InteractionManager.QUERY) {
					BasicInteractionManager.this.deleteFieldsListener.actionPerformed(event);
				}

				if (BasicInteractionManager.this.currentMode == InteractionManager.UPDATE) {
					BasicInteractionManager.this.deleteFieldsListener.actionPerformed(event);
				}
			}
			if (BasicInteractionManager.this.currentMode == InteractionManager.INSERT) {
				if (BasicInteractionManager.this.checkInsert()) {
					try {
						BasicInteractionManager.this.managedForm
						.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						final Entity entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(BasicInteractionManager.this.managedForm.getEntityName());
						final EntityResult result = entity.insert(BasicInteractionManager.this.attributesValues,
								BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());
						if (result.getCode() == EntityResult.OPERATION_WRONG) {
							BasicInteractionManager.this.managedForm.message(result.getMessage(), Form.ERROR_MESSAGE,
									null, result.getMessageParameter());
							BasicInteractionManager.this.managedForm
							.setStatusBarText(BasicInteractionManager.S_INCORRECT_INSERT, 3000);
							this.postIncorrectInsert(result, entity);
						} else {
							this.postCorrectInsert(result, entity);
						}
					} catch (final NoSuchObjectException ex) {
						BasicInteractionManager.logger.trace(null, ex);
						this.lastResult = new EntityResultMapImpl();
						this.lastResult.setCode(EntityResult.OPERATION_WRONG);
						this.lastResult.setMessage(ex.getMessage());
						if (BasicInteractionManager.this.formManager
								.getReferenceLocator() instanceof UtilReferenceLocator) {
							try {
								((UtilReferenceLocator) BasicInteractionManager.this.formManager.getReferenceLocator())
								.removeEntity(
										BasicInteractionManager.this.managedForm.getEntityName(),
										BasicInteractionManager.this.formManager.getReferenceLocator()
										.getSessionId());
							} catch (final Exception e) {
								BasicInteractionManager.logger.error(null, e);
							}
						}
						BasicInteractionManager.this.managedForm.message(this.lastResult.getMessage(),
								Form.ERROR_MESSAGE, null, this.lastResult.getMessageParameter());
						return;
					} catch (final Exception e) {
						BasicInteractionManager.logger.error(null, e);
						BasicInteractionManager.this.managedForm.message("operation_error_message", Form.ERROR_MESSAGE,
								e.getMessage());
						BasicInteractionManager.this.setQueryInsertMode();
					} finally {
						BasicInteractionManager.this.managedForm.setCursor(Cursor.getDefaultCursor());
					}
				} else {
					this.lastResult = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
				}
			} else {
				BasicInteractionManager.this.setInsertMode();
			}
		}

	}

	/**
	 * Update button default listener. Gets the modified fields in the form, and sends a undo request to
	 * the server. This class holds the previous operation result.
	 *
	 * @author Imatia Innovation
	 */
	public class UpdateListener implements ActionListener {

		protected EntityResult lastResult = null;

		public UpdateListener() {
		}

		/**
		 * Method called when the update was wrong.
		 * @param result
		 * @param entity
		 * @throws Exception
		 */
		protected void postIncorrectUpdate(final EntityResult result, final Entity entity) throws Exception {
			this.lastResult = result;
		}

		/**
		 * Returns the result of the last update.
		 * @return
		 */
		public EntityResult getLastResult() {
			return this.lastResult;
		}

		/**
		 * Method called when the updating process finishes successfully. Updates the tables and the tree
		 * when necessary.
		 * @param result
		 * @param entity
		 * @throws Exception
		 */
		protected void postCorrectUpdate(final EntityResult result, Entity entity) throws Exception {
			this.lastResult = result;
			if (result.getCode() != EntityResult.OPERATION_WRONG) {
				BasicInteractionManager.this.managedForm.fireDataRecordChange(
						new DataRecordEvent(BasicInteractionManager.this.managedForm, DataRecordEvent.UPDATE,
								BasicInteractionManager.this.keysValues, BasicInteractionManager.this.attributesValues,
								result));
			}

			BasicInteractionManager.this.managedForm
			.updateDataListDataCurrentRecord(BasicInteractionManager.this.attributesValues);
			BasicInteractionManager.this.modifiedFieldAttributes.clear();

			BasicInteractionManager.this.managedForm.disableButton(InteractionManager.UPDATE_KEY);

			if (InteractionManager.NEWMODE) {
				// Update the query and insert button after the correct update
				BasicInteractionManager.this.managedForm.enableButton(InteractionManager.INSERT_KEY);
				BasicInteractionManager.this.managedForm.enableButton(InteractionManager.QUERY_KEY);
			}

			if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				BasicInteractionManager.this.managedForm.setStatusBarText(result.getMessage(), 3000);
			} else {
				BasicInteractionManager.this.managedForm.setStatusBarText(BasicInteractionManager.S_CORRECT_UPDATE,
						3000);
			}
			if (BasicInteractionManager.this.managedForm.getDetailComponent() == null) {
				if (BasicInteractionManager.this.afterUpdate) {
					// To update the node
					final List keys = BasicInteractionManager.this.managedForm.getKeys();
					final List parentkeys = BasicInteractionManager.this.managedForm.getParentKeys();
					Map nodeKeysValues = null;
					if (!keys.isEmpty()) {
						nodeKeysValues = BasicInteractionManager.this.attributesValues;
						for (int i = 0; i < keys.size(); i++) {
							final Object key = keys.get(i);
							final Object vKey = BasicInteractionManager.this.managedForm.getDataFieldValue(key.toString());
							if (vKey != null) {
								nodeKeysValues.put(key, vKey);
								BasicInteractionManager.logger
								.debug("Key set in key-values pair: keys : {} with value: ", key, vKey);
							} else {
								BasicInteractionManager.logger.debug("Key: {} not found in form values", key);
							}
						}
						for (int i = 0; i < parentkeys.size(); i++) {
							final Object parentkey = parentkeys.get(i);
							final Object vParentkey = BasicInteractionManager.this.managedForm
									.getDataFieldValue(parentkey.toString());
							if (vParentkey != null) {
								nodeKeysValues.put(parentkey, vParentkey);
								BasicInteractionManager.logger.debug(
										"Parentkey set in key-values pair: parentkey: {}  with value: {}", parentkey,
										vParentkey);
							} else {
								BasicInteractionManager.logger.debug("Parentkey: {} not found in form values",
										parentkey);
							}
						}
					} else {
						nodeKeysValues = BasicInteractionManager.this.attributesValues;
					}

					if (BasicInteractionManager.this.formManager instanceof ITreeFormManager) {
						((ITreeFormManager) BasicInteractionManager.this.formManager).updatedNode(
								BasicInteractionManager.this.managedForm.getAssociatedTreePath(), nodeKeysValues,
								BasicInteractionManager.this.keysValues);
					}
				} else {
					if (BasicInteractionManager.this.formManager instanceof ITreeFormManager) {
						((ITreeFormManager) BasicInteractionManager.this.formManager).dataInserted();
					}
				}
			} else {
				if (BasicInteractionManager.this.afterUpdate) {
					// After update then refresh the table but only the modified
					// row.

					final IDetailForm detailForm = BasicInteractionManager.this.managedForm.getDetailComponent();
					if (detailForm != null) {
						final com.ontimize.gui.table.Table sourceTable = detailForm.getTable();
						entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(sourceTable.getEntityName());

						// Get the table keys to know the update record keys
						try {
							final Map kvQuery = new Hashtable();
							final Map oldTableKV = new Hashtable();
							final Map newTableKV = new Hashtable();

							BasicInteractionManager.this.keysValues.size();

							final List vTableKeys = sourceTable.getKeys();

							for (int i = 0; i < vTableKeys.size(); i++) {
								// Put the form keys
								final String formFieldName = detailForm.getFormFieldName(vTableKeys.get(i));
								if (BasicInteractionManager.this.attributesValues.containsKey(formFieldName)) {
									kvQuery.put(vTableKeys.get(i),
											BasicInteractionManager.this.attributesValues.get(formFieldName));
									newTableKV.put(vTableKeys.get(i),
											BasicInteractionManager.this.attributesValues.get(formFieldName));
								} else {
									kvQuery.put(vTableKeys.get(i),
											BasicInteractionManager.this.keysValues.get(formFieldName));
									newTableKV.put(vTableKeys.get(i),
											BasicInteractionManager.this.keysValues.get(formFieldName));
								}

								if (BasicInteractionManager.this.keysValues.containsKey(formFieldName)) {
									oldTableKV.put(vTableKeys.get(i),
											BasicInteractionManager.this.keysValues.get(formFieldName));
								} else {
									BasicInteractionManager.logger
									.warn("The DetailForm keys are different to Table keys");
								}
							}

							// Put the parentkeys to use in the entity query
							final Map parentkeysValues = sourceTable.getParentKeyValues();
							final Enumeration keys = Collections.enumeration(parentkeysValues.keySet());
							while (keys.hasMoreElements()) {
								final Object key = keys.nextElement();
								kvQuery.put(key, parentkeysValues.get(key));
							}

							final EntityResult res = entity.query(kvQuery, sourceTable.getAttributeList(),
									BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());

							if (res.isEmpty() || (res.getCode() == EntityResult.OPERATION_WRONG)) {
								BasicInteractionManager.logger.debug("Up to data data not found {}", res.getMessage());
								sourceTable.refresh();
								final int row = sourceTable.getRowForKeys(kvQuery);
								if (row < 0) {
									sourceTable.setSelectedRow(row);
									// TODO Check if it's necessary
									// detailForm.valuesToForm. I think it's
									// necessary only when it's established the
									// cod in Table.
									// detailForm.setKeys(detailForm.valuesToForm(sourceTable.getAllPrimaryKeys()),
									// row);
									detailForm.getForm().disableNavigationButtons();
								} else {
									sourceTable.setSelectedRow(0);
									detailForm.setKeys(detailForm.valuesToForm(sourceTable.getAllPrimaryKeys()), 0);
								}
							} else {

								// update the row
								final Map hNewRow = new Hashtable();
								final Enumeration enumKeys = res.keys();
								while (enumKeys.hasMoreElements()) {
									final Object oKey = enumKeys.nextElement();
									if (((List) res.get(oKey)).size() < 1) {
										// An error happens because there isn't
										// any key value.
										// Print a WARNING with the name of the
										// problematic key
										BasicInteractionManager.logger.warn(
												"The query result isn't empty but, for the key: {} the returned List size is 0",
												oKey);
									}
									final Object oValue = ((List) res.get(oKey)).get(0);
									if (oValue != null) {
										hNewRow.put(oKey, oValue);
									}
								}

								final int indexCheck = sourceTable.getRowForKeys(oldTableKV);
								if (indexCheck < 0) {
									sourceTable.addRow(hNewRow);
								}

								sourceTable.updateRowData(hNewRow, oldTableKV);
								final int row = sourceTable.getRowForKeys(newTableKV);
								sourceTable.setSelectedRow(row);
								// TODO Check if it's necessary
								// detailForm.valuesToForm.I think it's
								// necessary only when it's established the cod
								// in Table.
								if (row >= 0) {
									detailForm.setKeys(detailForm.valuesToForm(sourceTable.getAllPrimaryKeys()), row);
								}
							}
						} catch (final Exception e) {
							BasicInteractionManager.logger.error(null, e);
							// If an error happens then update the table
							sourceTable.refresh();
							final int row = sourceTable
									.getRowForKeys(detailForm.valuesToTable(BasicInteractionManager.this.keysValues));
							sourceTable.setSelectedRow(row);
							detailForm.setKeys(sourceTable.getAllPrimaryKeys(), row);
						}
					}
				}
			}
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (BasicInteractionManager.this.currentMode == InteractionManager.UPDATE) {
				if (BasicInteractionManager.this.checkUpdate()) {
					try {
						BasicInteractionManager.this.managedForm
						.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						final Entity entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(BasicInteractionManager.this.managedForm.getEntityName());
						final EntityResult result = entity.update(BasicInteractionManager.this.attributesValues,
								BasicInteractionManager.this.keysValues,
								BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());
						BasicInteractionManager.this.checkModifiedDataChangeEvent = false;
						if (result.getCode() == EntityResult.OPERATION_WRONG) {
							BasicInteractionManager.this.managedForm.message(result.getMessage(), Form.ERROR_MESSAGE,
									null, result.getMessageParameter());
							BasicInteractionManager.this.managedForm
							.setStatusBarText(BasicInteractionManager.S_INCORRECT_UPDATE, 3000);
							this.postIncorrectUpdate(result, entity);
						} else {
							this.postCorrectUpdate(result, entity);
						}
					} catch (final NoSuchObjectException ex) {
						BasicInteractionManager.logger.trace(null, ex);
						this.lastResult = new EntityResultMapImpl();
						this.lastResult.setCode(EntityResult.OPERATION_WRONG);
						this.lastResult.setMessage(ex.getMessage());
						if (BasicInteractionManager.this.formManager
								.getReferenceLocator() instanceof UtilReferenceLocator) {
							try {
								((UtilReferenceLocator) BasicInteractionManager.this.formManager.getReferenceLocator())
								.removeEntity(
										BasicInteractionManager.this.managedForm.getEntityName(),
										BasicInteractionManager.this.formManager.getReferenceLocator()
										.getSessionId());
							} catch (final Exception e) {
								BasicInteractionManager.logger.error(null, e);
							}
						}
						BasicInteractionManager.this.managedForm.message(this.lastResult.getMessage(),
								Form.ERROR_MESSAGE, null, this.lastResult.getMessageParameter());
						return;
					} catch (final Exception e) {
						BasicInteractionManager.logger.error(null, e);
						BasicInteractionManager.this.managedForm
						.message(
								ApplicationManager.getTranslation(e.getMessage(),
										BasicInteractionManager.this.managedForm.getResourceBundle()),
								Form.ERROR_MESSAGE, e);
						BasicInteractionManager.this.setQueryInsertMode();
					} finally {
						BasicInteractionManager.this.managedForm.setCursor(Cursor.getDefaultCursor());
						BasicInteractionManager.this.checkModifiedDataChangeEvent = true;
					}
				} else {
					this.lastResult = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
				}
			} else {
				BasicInteractionManager.this.setUpdateMode();
			}
		}

	}

	/**
	 * This class implements the delete button default behavior. This means that when a elimination is
	 * successful a message can be shown and the tree, if present, will be update. In case that the
	 * elimination fails, a message advising of this situation can be shown. In order to do this, the
	 * EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE determines whether the message will be shown or
	 * not.
	 * <p>
	 * This class stores last delete operation result.
	 */
	public class DeleteListener implements ActionListener {

		protected EntityResult lastResult = null;

		public DeleteListener() {
		}

		/**
		 * Method called after performing the deletion operation, when that operation has been executed with
		 * errors. The entity reference is not stored.
		 * @param result the operation result
		 * @param entity the entity called to perform the operation
		 * @throws Exception
		 */
		protected void postIncorrectDelete(final EntityResult result, final Entity entity) throws Exception {
			this.lastResult = result;
		}

		/**
		 * Method called after performing the deletion operation, when that operation has been executed
		 * successfully. Shows information messages, and updates the tree and the tables when necessary.
		 * @param result the operation result
		 * @param entity the entity called to perform the operation
		 * @throws Exception
		 */
		protected void postCorrectDelete(final EntityResult result, Entity entity) throws Exception {
			this.lastResult = result;

			if (result.getCode() != EntityResult.OPERATION_WRONG) {
				BasicInteractionManager.this.managedForm.fireDataRecordChange(
						new DataRecordEvent(BasicInteractionManager.this.managedForm, DataRecordEvent.DELETE,
								BasicInteractionManager.this.keysValues, null, result));
			}

			if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL_SHOW_MESSAGE) {
				BasicInteractionManager.this.managedForm.setStatusBarText(result.getMessage(), 3000);
			} else {
				BasicInteractionManager.this.managedForm.setStatusBarText(BasicInteractionManager.S_CORRECT_DELETE,
						3000);
			}
			if (BasicInteractionManager.this.managedForm.getDetailComponent() == null) {
				if (BasicInteractionManager.this.afterUpdate) {
					// If the form has more than one result then delete the
					// current one
					// Delete the associated node too
					if (BasicInteractionManager.this.managedForm.getFormCacheSize() > 1) {
						// If there is not cache update the form and the tree
						final int currentIndex = BasicInteractionManager.this.managedForm.getCurrentIndex();
						if (BasicInteractionManager.this.formManager instanceof ITreeFormManager) {
							((ITreeFormManager) BasicInteractionManager.this.formManager).deletedNode(
									BasicInteractionManager.this.managedForm.getAssociatedTreePath(),
									BasicInteractionManager.this.keysValues, false);
						}
						// After the deleteNode delete from cache to process the
						// DataNavigationEvent
						BasicInteractionManager.this.managedForm.deleteRecordFromFormCache(currentIndex);
					} else {

						if ((BasicInteractionManager.this.formManager instanceof ITreeFormManager)
								&& (((ITreeFormManager) BasicInteractionManager.this.formManager).getTree() != null)) {
							final ITreeFormManager treeFormManager = (ITreeFormManager) BasicInteractionManager.this.formManager;
							final OTreeNode selectedNode = (OTreeNode) treeFormManager.getTree()
									.getLastSelectedPathComponent();
							TreePath organizational = BasicInteractionManager.this.managedForm.getAssociatedTreePath();
							if (!selectedNode.isOrganizational()) {
								organizational = BasicInteractionManager.this.managedForm.getAssociatedTreePath()
										.getParentPath();
							}

							treeFormManager.deletedNode(
									BasicInteractionManager.this.managedForm.getAssociatedTreePath(),
									BasicInteractionManager.this.keysValues, true);

							if (treeFormManager.getTree().isCollapsed(organizational)) {
								BasicInteractionManager.this.managedForm.updateDataFields(new Hashtable());
								BasicInteractionManager.this.setQueryInsertMode();
							}
						} else {
							// There was only one record so now no records
							// exist. Then put in queryInsert mode
							BasicInteractionManager.this.managedForm.updateDataFields(new Hashtable());
							BasicInteractionManager.this.setQueryInsertMode();
						}

					}
				} else {
					if (BasicInteractionManager.this.formManager instanceof ITreeFormManager) {
						((ITreeFormManager) BasicInteractionManager.this.formManager).dataInserted();
					}
				}
			} else {
				if (BasicInteractionManager.this.afterUpdate) {
					// After delete the node update the table. Remove the row
					// but
					// previously query the record keys to ensure the remove
					// operation
					final IDetailForm detailForm = BasicInteractionManager.this.managedForm.getDetailComponent();
					if (detailForm != null) {
						final Table sourceTable = detailForm.getTable();
						entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(sourceTable.getEntityName());

						final int row = sourceTable
								.getRowForKeys(detailForm.valuesToTable(BasicInteractionManager.this.keysValues));
						try {
							final Map kvQuery = new Hashtable();

							final List tableKeys = sourceTable.getKeys();
							for (int i = 0; i < tableKeys.size(); i++) {
								final Object tableKey = tableKeys.get(i);
								kvQuery.put(tableKey, BasicInteractionManager.this.keysValues
										.get(detailForm.getFormFieldName(tableKey)));
							}
							// Insert the parentkeys
							final Map parentKeysValues = sourceTable.getParentKeyValues();
							final Enumeration keys = Collections.enumeration(parentKeysValues.keySet());
							while (keys.hasMoreElements()) {
								final Object key = keys.nextElement();
								kvQuery.put(key, parentKeysValues.get(key));
							}
							final EntityResult res = entity.query(kvQuery, sourceTable.getAttributeList(),
									BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());
							if (res.getCode() == EntityResult.OPERATION_WRONG) {
								sourceTable.refresh();
								// Select the next record
								final int rowsNumber = sourceTable.getJTable().getRowCount();
								final int selectedRow = Math.min(row, rowsNumber - 1);
								sourceTable.setSelectedRow(selectedRow);
								detailForm.setKeys(sourceTable.getAllPrimaryKeys(), selectedRow);
								if (detailForm instanceof TabbedDetailForm) {
									detailForm.hideDetailForm();
								} else {
									if (rowsNumber == 0) {
										detailForm.setQueryInsertMode();
									}
								}
							} else {
								if (res.isEmpty()) {
									// If result is empty row is deleted.
									sourceTable
									.deleteRow(detailForm.valuesToTable(BasicInteractionManager.this.keysValues));
									// Select the next record
									final int rowsCount = sourceTable.getJTable().getRowCount();
									final int selectedRow = Math.min(row, rowsCount - 1);
									sourceTable.setSelectedRow(selectedRow);
									detailForm.setKeys(sourceTable.getAllPrimaryKeys(), selectedRow);
									if (detailForm instanceof TabbedDetailForm) {
										detailForm.hideDetailForm();
									} else {
										if (rowsCount == 0) {
											detailForm.setQueryInsertMode();
										}
									}
								} else {
									BasicInteractionManager.logger.warn(
											"The delete method has returned a result with no errors, but the checking query returned results: {}",
											res);
									// If res is not empty then the record has
									// not been removed
									// Refresh all the table
									sourceTable.refresh();
									final int rowsCount = sourceTable.getJTable().getRowCount();
									final int selectedRow = Math.min(row, rowsCount - 1);
									sourceTable.setSelectedRow(selectedRow);
									detailForm.setKeys(sourceTable.getAllPrimaryKeys(), selectedRow);
									if (detailForm instanceof TabbedDetailForm) {
										detailForm.hideDetailForm();
									} else {
										if (rowsCount == 0) {
											detailForm.setQueryInsertMode();
										}
									}
								}
							}
						} catch (final Exception e) {
							BasicInteractionManager.logger.error(null, e);
							// If an error happens then update the table
							sourceTable.refresh();
							// Select the next record
							final int rowsCount = sourceTable.getJTable().getRowCount();
							final int selectedRow = Math.min(row, rowsCount - 1);
							sourceTable.setSelectedRow(selectedRow);
							detailForm.setKeys(sourceTable.getAllPrimaryKeys(), selectedRow);
							if (detailForm instanceof TabbedDetailForm) {
								detailForm.hideDetailForm();
							} else {
								if (rowsCount == 0) {
									detailForm.setQueryInsertMode();
								}
							}
						}
					}
				}
			}

		}

		/**
		 * Returns the last delete operation result.
		 * @return the last delete operation result
		 */
		public EntityResult getLastResult() {
			return this.lastResult;
		}

		@Override
		public void actionPerformed(final ActionEvent event) {
			if (BasicInteractionManager.this.currentMode == InteractionManager.UPDATE) {
				if (BasicInteractionManager.this.checkDelete()) {
					try {
						BasicInteractionManager.this.managedForm
						.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						final Entity entity = BasicInteractionManager.this.formManager.getReferenceLocator()
								.getEntityReference(BasicInteractionManager.this.managedForm.getEntityName());
						final EntityResult result = entity.delete(BasicInteractionManager.this.keysValues,
								BasicInteractionManager.this.formManager.getReferenceLocator().getSessionId());
						BasicInteractionManager.this.checkModifiedDataChangeEvent = false;
						if (result.getCode() == EntityResult.OPERATION_WRONG) {
							BasicInteractionManager.this.managedForm.message(result.getMessage(), Form.ERROR_MESSAGE,
									null, result.getMessageParameter());
							BasicInteractionManager.this.managedForm
							.setStatusBarText(BasicInteractionManager.S_INCORRECT_DELETE, 3000);
							this.postIncorrectDelete(result, entity);
						} else {
							this.postCorrectDelete(result, entity);
						}
					} catch (final NoSuchObjectException ex) {
						BasicInteractionManager.logger.trace(null, ex);
						this.lastResult = new EntityResultMapImpl();
						this.lastResult.setCode(EntityResult.OPERATION_WRONG);
						this.lastResult.setMessage(ex.getMessage());
						if (BasicInteractionManager.this.formManager
								.getReferenceLocator() instanceof UtilReferenceLocator) {
							try {
								((UtilReferenceLocator) BasicInteractionManager.this.formManager.getReferenceLocator())
								.removeEntity(
										BasicInteractionManager.this.managedForm.getEntityName(),
										BasicInteractionManager.this.formManager.getReferenceLocator()
										.getSessionId());
							} catch (final Exception e) {
								BasicInteractionManager.logger.error(null, e);
							}
						}
						BasicInteractionManager.this.managedForm.message(this.lastResult.getMessage(),
								Form.ERROR_MESSAGE, null, this.lastResult.getMessageParameter());
						return;
					} catch (final Exception e) {
						BasicInteractionManager.logger.error(null, e);
						BasicInteractionManager.this.managedForm
						.message(
								ApplicationManager.getTranslation(e.getMessage(),
										BasicInteractionManager.this.managedForm.getResourceBundle()),
								Form.ERROR_MESSAGE, e);
						BasicInteractionManager.this.setQueryInsertMode();
					} finally {
						BasicInteractionManager.this.managedForm.setCursor(Cursor.getDefaultCursor());
						BasicInteractionManager.this.checkModifiedDataChangeEvent = true;
					}
				} else {
					this.lastResult = new EntityResultMapImpl(EntityResult.OPERATION_WRONG, EntityResult.NODATA_RESULT);
				}
			} else {
				BasicInteractionManager.this.setUpdateMode();
			}
		}

	}

	/**
	 * Constructs a BasicInterationManager. By default sets no detailForm, what means that the
	 * InteractionManager is no related to a From arising from a Table, and also refreshing of the
	 * related trees and tables is active.
	 */
	public BasicInteractionManager() {
		super();
		this.afterUpdate = true;
		this.detailForm = false;
	}

	/**
	 * Constructs a BasicInterationManager. By default sets no detailForm, what means that the
	 * InteractionManager is no related to a From arising from a Table. Can be specified whether the
	 * InteractionManager has to refresh the tree nodes and the tables or just letting the to be
	 * refreshed on their own.
	 * @param update false if to refresh the trees and the tables from the BasicInteractionManager is
	 *        not desired
	 */
	public BasicInteractionManager(final boolean update) {
		super();
		this.afterUpdate = update;
		this.detailForm = false;
	}

	/**
	 * Constructs a BasicInteractionManager.
	 * @param update false if to refresh the trees and the tables from the BasicInteractionManager is
	 *        not desired
	 * @param detailForm when true indicates that the form comes from a {@link Table}
	 */
	protected BasicInteractionManager(final boolean update, final boolean detailForm) {
		super();
		this.afterUpdate = update;
		this.detailForm = detailForm;
	}

	/**
	 * Performs the logical checks prior to execute the query action.
	 * @return - true if the query can be executed
	 */
	public boolean checkQuery() {

		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isCheckQueryScript()) {
			final Boolean scriptResult = this.formInteractionScriptManager.checkQuery();
			if (scriptResult != null) {
				return scriptResult.booleanValue();
			}
		}

		this.keysValues = this.managedForm.getDataFieldValues(false);
		if (this.managedForm instanceof FormExt) {
			this.attributes = this.getQueryAttributesValues();
			// Then add the query column to the table view
			final List vQueryColumn = new Vector();
			final Enumeration enumKeys = Collections.enumeration(this.keysValues.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				if (!this.attributes.contains(oKey)
						&& !(this.managedForm.getDataFieldReference(oKey.toString()) instanceof CalculatedDataField)
						&& !(this.managedForm
								.getDataFieldReference(oKey.toString()) instanceof CalculatedCurrencyDataField)
						&& !(this.managedForm
								.getDataFieldReference(oKey.toString()) instanceof CalculatedTextDataField)) {
					this.attributes.add(oKey);
					vQueryColumn.add(oKey);
				}
			}
			if (this.addColumnsToTableView) {
				this.managedForm.addColumnsToTableView(vQueryColumn);
			}
		} else {
			this.attributes = this.managedForm.getDataFieldAttributeList();
		}
		return true;
	}

	/**
	 * Returns the attributes that must be queried.
	 * @return a List with the query attributes
	 */
	protected List getQueryAttributes() {
		List at = new Vector();
		if (this.managedForm instanceof FormExt) {
			at = ((FormExt) this.managedForm).getAttributesToQuery();
			if (this.keysValues != null) {
				final List queryColumns = new Vector();
				final Enumeration enumKeys = Collections.enumeration(this.keysValues.keySet());
				while (enumKeys.hasMoreElements()) {
					final Object oKey = enumKeys.nextElement();
					if (!at.contains(oKey)) {
						at.add(oKey);
						queryColumns.add(oKey);
					}
				}
				if (this.addColumnsToTableView) {
					this.managedForm.addColumnsToTableView(queryColumns);
				}
			}
		} else {
			at = this.managedForm.getDataFieldAttributeList();
		}

		return at;
	}

	/**
	 * Sets the right values to the {@link #keysValues} List and also to the {@link #attributes} one,
	 * getting those values from the managed form.
	 * @return true
	 */
	public boolean checkSearchInResults() {
		this.keysValues = this.managedForm.getDataFieldValues(false);
		if (this.managedForm instanceof FormExt) {
			this.attributes = ((FormExt) this.managedForm).getAttributesToQuery();
		} else {
			this.attributes = this.managedForm.getDataFieldAttributeList();
		}
		return true;
	}

	/**
	 * Performs basic checks in order to process the insert order. This can be used to insert client
	 * logic to prevent non desired insertions, by overwriting this method.
	 * @return false if the insertion can't be done, true if it can
	 */
	public boolean checkInsert() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isCheckInsertScript()) {
			final Boolean scriptResult = this.formInteractionScriptManager.checkInsert();
			if (scriptResult != null) {
				return scriptResult.booleanValue();
			}
		}
		final List emptyRequiredDataField = this.managedForm.getEmptyRequiredDataField();
		if ((emptyRequiredDataField != null) && (emptyRequiredDataField.size() > 0)) {
			// managedForm.message(M_FILL_ALL_REQUIRED_FIELDS,
			// Form.WARNING_MESSAGE);
			this.managedForm.message(this.getEmptyRequiredFieldsMessage(emptyRequiredDataField), Form.WARNING_MESSAGE);
			this.requestFocusForEmptyRequiredComponent(emptyRequiredDataField);
			return false;
		} else {
			this.attributesValues = this.getInsertAttributesValues();
			return true;
		}
	}

	/**
	 * Request the focus for the first element in the list and ensures that this component is visible in
	 * the form
	 * @param emptyRequiredDataField List of all the empty required field names (String attr)
	 */
	protected void requestFocusForEmptyRequiredComponent(final List emptyRequiredDataField) {
		final Component component = (Component) this.managedForm
				.getDataFieldReference(emptyRequiredDataField.get(0).toString());
		component.requestFocus();
		this.ensureComponentVisible(component);

		final JScrollPane scrollPane = this.managedForm.getScrollPane();
		if ((scrollPane != null) && (component.getParent() != null)) {
			final Rectangle convertRectangle = SwingUtilities.convertRectangle(component.getParent(), component.getBounds(),
					scrollPane);
			scrollPane.getViewport().scrollRectToVisible(convertRectangle);
		}
	}

	/**
	 * Ensure that the component is visible in the form, if the component is in a Tab or
	 * CollapsiblePanel or SplitPane
	 * @param component
	 */
	protected void ensureComponentVisible(final Component component) {
		try {
			final Tab tab = (Tab) SwingUtilities.getAncestorOfClass(Tab.class, component);
			if (tab != null) {
				if (!tab.isVisible()) {
					final TabPanel tabPanel = (TabPanel) SwingUtilities.getAncestorOfClass(TabPanel.class, tab);
					if (tabPanel != null) {
						for (int i = 0; i < tabPanel.getTabCount(); i++) {
							if (tabPanel.getComponentAt(i).equals(tab)) {
								tabPanel.setSelectedIndex(i);
								break;
							}
						}
						tabPanel.setTabVisible(tab.getTitleKey());
					}
				}
				this.ensureComponentVisible(tab);
			}

			final CollapsiblePanel collapsible = (CollapsiblePanel) SwingUtilities.getAncestorOfClass(CollapsiblePanel.class,
					component);
			if (collapsible != null) {
				if (!collapsible.isDeploy()) {
					// This is needed because if the component has not be
					// painted yet then it can hide itself later
					if (collapsible.isFirstTime()) {
						collapsible.setFirstShow(true);
					} else {
						collapsible.doActionDeploy(false);
					}
				}
				this.ensureComponentVisible(collapsible);
			}

			final List ancestorsToSplit = this.getAncestorsTo(component, SplitPane.class);
			if (ancestorsToSplit != null) {
				final SplitPane split = (SplitPane) ancestorsToSplit.get(ancestorsToSplit.size() - 1);
				final int orientation = split.getOrientation();
				if (orientation == JSplitPane.HORIZONTAL_SPLIT) {
					if (ancestorsToSplit.contains(split.getLeftComponent())) {
						final int width = ((JComponent) split.getLeftComponent()).getVisibleRect().width;
						if (((JComponent) split.getLeftComponent()).getPreferredSize().width > width) {
							split.setDividerLocation(1.0);
						}
					} else {
						final int width = ((JComponent) split.getRightComponent()).getVisibleRect().width;
						if (((JComponent) split.getRightComponent()).getPreferredSize().width > width) {
							split.setDividerLocation(0.0);
						}

					}
				} else {
					if (ancestorsToSplit.contains(split.getBottomComponent())) {
						final int height = ((JComponent) split.getBottomComponent()).getVisibleRect().height;
						if (((JComponent) split.getBottomComponent()).getPreferredSize().height > height) {
							split.setDividerLocation(0.0);
						}
					} else {
						final int height = ((JComponent) split.getTopComponent()).getVisibleRect().height;
						if (((JComponent) split.getTopComponent()).getPreferredSize().height > height) {
							split.setDividerLocation(1.0);
						}

					}
				}
				this.ensureComponentVisible(split);
			}

		} catch (final Exception e) {
			BasicInteractionManager.logger.error(null, e);
		}

	}

	protected List getAncestorsTo(final Component component, final Class parentClass) {
		final List ancestors = new ArrayList();
		Container parent = component.getParent();
		while ((parent != null) && !parentClass.isInstance(parent)) {
			ancestors.add(parent);
			parent = parent.getParent();
		}

		if ((parent != null) && parentClass.isInstance(parent)) {
			ancestors.add(parent);
			return ancestors;
		}
		return null;

	}

	/**
	 * Checks whether an update can be done or no. This method checks that all the required fields have
	 * values. In case the update can be done, the method sets the right values to the
	 * {@link #attributes} and {@link #keysValues} variables from the managed form. This method must be
	 * overwritten in order to set a check that can avoid to perform the update.
	 * @return true if the update can be done, false if not
	 */
	public boolean checkUpdate() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isCheckUpdateScript()) {
			final Boolean scriptResult = this.formInteractionScriptManager.checkUpdate();
			if (scriptResult != null) {
				return scriptResult.booleanValue();
			}
		}

		final List emptyRequiredDataField = this.managedForm.getEmptyRequiredDataField();
		if ((emptyRequiredDataField != null) && (emptyRequiredDataField.size() > 0)) {
			// managedForm.message(M_FILL_ALL_REQUIRED_FIELDS,
			// Form.WARNING_MESSAGE);
			this.managedForm.message(this.getEmptyRequiredFieldsMessage(emptyRequiredDataField), Form.WARNING_MESSAGE);
			this.requestFocusForEmptyRequiredComponent(emptyRequiredDataField);
			return false;
		} else {
			this.attributesValues = this.getAttributesValuesUpdateForm();

			this.keysValues = this.getFormKeyValues();
			return true;
		}
	}

	/**
	 * Create the message to show when you are trying to insert or update a record with empty required
	 * fields. This method create a message using the name of the fields
	 * @param emptyFields
	 * @return
	 */
	protected String getEmptyRequiredFieldsMessage(final List emptyFields) {
		final StringBuilder translation = new StringBuilder(ApplicationManager
				.getTranslation(BasicInteractionManager.M_FILL_ALL_REQUIRED_FIELDS, this.managedForm.getResourceBundle()));
		boolean isHTML = false;

		if (translation.toString().toLowerCase().endsWith("</html>")) {
			isHTML = true;
			translation.delete(translation.length() - 7, translation.length());
		}

		translation.append(": ");

		String firstText = emptyFields.get(0).toString();
		FormComponent elementReference = this.managedForm.getElementReference(firstText);
		if ((elementReference instanceof DataField) && (((DataField) elementReference).getLabelText() != null)) {
			firstText = ((DataField) elementReference).getLabelText();
		}

		translation.append(ApplicationManager.getTranslation(firstText, this.managedForm.getResourceBundle()));
		for (int i = 1; i < emptyFields.size(); i++) {
			translation.append(", ");
			String emptyFieldAttr = emptyFields.get(i).toString();
			elementReference = this.managedForm.getElementReference(emptyFieldAttr);
			if ((elementReference instanceof DataField) && (((DataField) elementReference).getLabelText() != null)) {
				emptyFieldAttr = ((DataField) elementReference).getLabelText();
			}
			translation.append(ApplicationManager.getTranslation(emptyFieldAttr, this.managedForm.getResourceBundle()));
		}
		if (isHTML) {
			translation.append("</html>");
		}
		return translation.toString();

	}

	/**
	 * Performs basic checks in order to process the delete order. This can be used to insert client
	 * logic to prevent non desired deletions, by overwriting this method.
	 * <p>
	 * This standard implementations returns always true, but can be changed calling the method
	 * setShowDeleteConfirmMessage.
	 * @return false if the deletion can't be done, true if it can
	 */
	public boolean checkDelete() {
		if ((this.formInteractionScriptManager != null) && !this.formInteractionScriptManager.isCheckDeleteScript()) {
			final Boolean scriptResult = this.formInteractionScriptManager.checkDelete();
			if (scriptResult != null) {
				return scriptResult.booleanValue();
			}
		}
		if (this.showDeleteConfirmMessage) {
			final boolean resp = this.managedForm.question(BasicInteractionManager.M_DELETE_CONFIRM);
			if (!resp) {
				return false;
			}
		}

		this.keysValues = this.getFormKeyValues();
		return true;
	}

	@Override
	public InteractionManager cloneInteractionManager() {
		try {
			return super.cloneInteractionManager();
		} catch (final Exception e) {
			BasicInteractionManager.logger.trace(null, e);
			return new BasicInteractionManager(this.afterUpdate);
		}
	}

	@Override
	public boolean dataWillChange(final DataNavigationEvent e) {
		final boolean b = super.dataWillChange(e);
		if (!b) {
			return b;
		}
		if (this.checkModifiedDataChangeEvent) {
			BasicInteractionManager.logger.debug("Modified Field Attributes: {}", this.modifiedFieldAttributes);
			BasicInteractionManager.logger.debug("The record shown in the form is going to change {}", e);

			if ((!this.modifiedFieldAttributes.isEmpty())
					&& this.managedForm.question(BasicInteractionManager.M_MODIFIED_DATA_APPLY_CHANGES)) {
				this.updateListener
				.actionPerformed(new ActionEvent(this.managedForm.getButton(InteractionManager.UPDATE_KEY),
						ActionEvent.ACTION_PERFORMED, InteractionManager.UPDATE_KEY));
				if ((this.updateListener.getLastResult() != null)
						&& (this.updateListener.getLastResult().getCode() == EntityResult.OPERATION_WRONG)) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	/**
	 * Registers the basic listener for the basic operations that can be performed by an interaction
	 * manager.
	 */
	@Override
	public void registerInteractionManager(final Form form, final IFormManager formManager) {
		super.registerInteractionManager(form, formManager);
		if (form.getDetailComponent() != null) {
			this.detailForm = true;
		}
		// Now listeners:
		final Button queryButton = this.managedForm.getButton(InteractionManager.QUERY_KEY);
		if (queryButton != null) {
			queryButton.addActionListener(this.queryListener);
		}

		final Button advancedQueryButton = this.managedForm.getButton(InteractionManager.ADVANCED_QUERY_KEY);
		if (advancedQueryButton != null) {
			advancedQueryButton.addActionListener(this.avancedQueryListener);
		}
		final Button insertButton = this.managedForm.getButton(InteractionManager.INSERT_KEY);
		if (insertButton != null) {
			insertButton.addActionListener(this.insertListener);
		}

		final Button updateButton = this.managedForm.getButton(InteractionManager.UPDATE_KEY);
		if (updateButton != null) {
			updateButton.addActionListener(this.updateListener);
		}
		final Button deleteButton = this.managedForm.getButton(InteractionManager.DELETE_KEY);
		if (deleteButton != null) {
			deleteButton.addActionListener(this.deleteListener);
		}

		if (this.scriptEnabled) {
			this.createScriptManager(form);
			if (this.formInteractionScriptManager == null) {
				this.scriptEnabled = false;
			} else {
				this.formInteractionScriptManager.registerListeners(form);
			}
		}
	};

	public boolean isScriptEnabled() {
		return this.scriptEnabled;
	}

	protected void createScriptManager(final Form form) {
		// Create the script manager using reflection.
		try {
			final Class scriptClass = Class.forName(BasicInteractionManager.defaultInteractionScriptManagerClassName);
			final Constructor constructor = scriptClass
					.getConstructor(new Class[] { Form.class, BasicInteractionManager.class });
			if (constructor != null) {
				this.formInteractionScriptManager = (IFormInteractionScriptManager) constructor
						.newInstance(new Object[] { form, this });
			}
		} catch (final Exception e) {
			BasicInteractionManager.logger.error(null, e);
		}
	}

	/**
	 * Removes the listener associated to the Delete button. The listener is added in the
	 * {@link #registerInteractionManager(Form, IFormManager)} method.
	 */
	public void removeDeleteListener() {
		if (this.managedForm != null) {
			final Button deleteButton = this.managedForm.getButton(InteractionManager.DELETE_KEY);
			if (deleteButton != null) {
				deleteButton.removeActionListener(this.deleteListener);
			}
		}
	}

	/**
	 * Removes the listener associated to the Query button. The listener is added in the
	 * {@link #registerInteractionManager(Form, IFormManager)} method.
	 */
	public void removeQueryListener() {
		if (this.managedForm != null) {
			final Button queryButton = this.managedForm.getButton(InteractionManager.QUERY_KEY);
			if (queryButton != null) {
				queryButton.removeActionListener(this.queryListener);
			}
		}
	}

	/**
	 * Removes the listener associated to the Insert button. The listener is added in the
	 * {@link #registerInteractionManager(Form, IFormManager)} method.
	 */
	public void removeInsertListener() {
		if (this.managedForm != null) {
			final Button insertButton = this.managedForm.getButton(InteractionManager.INSERT_KEY);
			if (insertButton != null) {
				insertButton.removeActionListener(this.insertListener);
			}
		}
	}

	/**
	 * Removes the listener associated to the Update button. The listener is added in the
	 * {@link #registerInteractionManager(Form, IFormManager)} method.
	 */
	public void removeUpdateListener() {
		if (this.managedForm != null) {
			final Button updateButton = this.managedForm.getButton(InteractionManager.UPDATE_KEY);
			if (updateButton != null) {
				updateButton.removeActionListener(this.updateListener);
			}
		}
	}

	@Override
	public void free() {
		this.removeUpdateListener();
		this.removeQueryListener();
		this.removeDeleteListener();
		this.removeInsertListener();
		if (this.attributes != null) {
			this.attributes.clear();
		}
		if (this.attributesValues != null) {
			this.attributesValues.clear();
		}
		this.attributes = null;
		this.attributesValues = null;
		this.printThread = null;
		super.free();
		BasicInteractionManager.logger.debug("Remove references.-> free");
	}

	@Override
	public void print() throws Exception {

		if ((this.printThread != null) && this.printThread.isAlive()) {
			this.managedForm.message("interactionmanager.wait_until_finished_printing", Form.WARNING_MESSAGE,
					(String) null);
			return;
		} else {
			// If there are multiple results then show a window to print a table
			// with the data
			if (this.managedForm instanceof FormExt) {
				final int recordCount = this.managedForm.getFormCacheSize();
				if (recordCount > 1) {

					// TODO Check this
					// When the form is open from a table (for example the
					// detail
					// form from a ReferenceExtDataField)
					// keysValuesLastQuery is empty.
					// In this situation it is not possible to offer the
					// possibility
					// to print all values in a table
					// because this action needs to execute a query with the
					// last
					// filter used and this filter is unknown
					boolean printAll = false;
					if (this.keysValuesLastQuery != null) {
						// Option to print all
						printAll = this.managedForm
								.question("interactionmanager.would_you_like_print_registers_in_table");
					}
					if (printAll) {
						((FormExt) this.managedForm).printMultipleRecords(this.keysValuesLastQuery);
					} else {
						super.print();
					}
				} else {
					super.print();
				}
			} else {
				super.print();
			}
		}
	}

	/**
	 * Configures the confirm message before performing operations.
	 * @param show if true, a message will appear before performing operations
	 */
	public void setShowDeleteConfirmMessage(final boolean show) {
		this.showDeleteConfirmMessage = show;
	}

	/**
	 * Performs a query to the entity configured in the managed form.
	 * @param keysValues the keysValues that filter the query
	 * @param attributes the attributes to query
	 * @return the result of the query
	 */
	protected EntityResult query(final Map keysValues, final List attributes) {
		try {
			this.managedForm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final Entity entity = this.formManager.getReferenceLocator()
					.getEntityReference(this.managedForm.getEntityName());
			return entity.query(keysValues, attributes, this.formManager.getReferenceLocator().getSessionId());
		} catch (final Exception e) {
			BasicInteractionManager.logger.trace(null, e);
			this.managedForm.message("interactionmanager.error_in_query", Form.ERROR_MESSAGE, e);
			return null;
		} finally {
			this.managedForm.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Returns the form keys and its values, to identify the record.
	 * @return
	 */
	public Map getFormKeyValues() {
		if (this.getCurrentMode() != InteractionManager.UPDATE) {
			return null;
		}
		final List keys = this.managedForm.getKeys();
		final List parentkeys = this.managedForm.getParentKeys();
		if (!keys.isEmpty()) {
			final Map kv = new Hashtable();
			for (int i = 0; i < keys.size(); i++) {
				final Object key = keys.get(i);
				final Object oValue = this.managedForm.getDataFieldValueFromFormCache(key.toString());
				if (oValue != null) {
					kv.put(key, oValue);
				}
			}
			for (int i = 0; i < parentkeys.size(); i++) {
				final Object parentkey = parentkeys.get(i);
				final Object oValue = this.managedForm.getDataFieldValueFromFormCache(parentkey.toString());
				if (oValue != null) {
					kv.put(parentkey, oValue);
				}
			}
			return kv;
		} else {
			return this.managedForm.getDataFieldValues(false);
		}
	}

	/**
	 * Returns all the attributes that will be queried.
	 */

	protected List getQueryAttributesValues() {
		if (this.managedForm instanceof FormExt) {
			return ((FormExt) this.managedForm).getAttributesToQuery();
		} else {
			return this.managedForm.getDataFieldAttributeList();
		}
	}

	/**
	 * Returns all the attributes that will be inserted and the values that will be inserted in that
	 * attributes.
	 * @return
	 */
	protected Map getInsertAttributesValues() {
		final Map attributesToInsert = this.managedForm.getDataFieldValues(false);

		final Map dataComponentList = this.managedForm.getDataComponentList();
		final Enumeration enumKeys = Collections.enumeration(dataComponentList.keySet());
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			if (dataComponentList.get(oKey) instanceof Table) {
				final Table table = (Table) dataComponentList.get(oKey);
				if (table.isOperationInMemory()) {
					final String entityName = table.getEntityName();
					try {
						final Entity localEntity = this.managedForm.getFormManager()
								.getReferenceLocator()
								.getEntityReference(entityName);
						final EntityResult eR = localEntity.query(new Hashtable(), new Vector(), -1);
						attributesToInsert.put(table.getAttribute(), eR);
					} catch (final Exception e) {
						BasicInteractionManager.logger.error(null, e);
					}
				}
			}
		}
		return attributesToInsert;
	}

	/**
	 * Returns the attributes and the values that changed in the form to perform the update.
	 * @return the attributes that changed and the new values
	 */
	protected Map getAttributesValuesUpdateForm() {
		if (this.getCurrentMode() != InteractionManager.UPDATE) {
			return null;
		}
		Map av = new Hashtable();
		if (this.updateMethod == InteractionManager.UPDATE_CHANGED) {
			BasicInteractionManager.logger.debug("Update: Modified Fields: {}", this.modifiedFieldAttributes);

			for (int i = 0; i < this.modifiedFieldAttributes.size(); i++) {
				final Object atr = this.modifiedFieldAttributes.get(i);
				final Object oValue = this.managedForm.getDataFieldValue(atr.toString());
				if (oValue == null) {
					av.put(atr, new NullValue(this.managedForm.getDataFieldReference(atr.toString()).getSQLDataType()));
				} else {
					av.put(atr, oValue);
				}
			}
		} else {
			av = this.managedForm.getDataFieldValues(false, true);
		}
		return av;
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		if (this.managedForm.getDetailComponent() != null) {
			this.detailForm = true;
		}
	}

	protected void setDetailForm(final boolean f) {
		this.detailForm = f;
	}

	public Map getAttributesValues() {
		return this.attributesValues;
	}

}
