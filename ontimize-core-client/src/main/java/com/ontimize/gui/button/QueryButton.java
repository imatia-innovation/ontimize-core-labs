package com.ontimize.gui.button;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataComponent;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ObjectTools;

/**
 * <p>
 * This button allows to query an entity and fill some form data fields with the query result. The
 * form values in the query moment are used as filter values. <BR>
 * This class uses Button parameters and Table parameters. the 'table_key' parameter is used as key
 * for the table because there is a conflict with the 'key' parameter'.<BR>
 * </p>
 * <p>
 * Company: IMATIA
 * </p>
 */

public class QueryButton extends Button implements ReferenceComponent {

	private static final Logger logger = LoggerFactory.getLogger(QueryButton.class);

	private static String MISSING_DATA_MESSAGE_KEY = "value_must_be_entered_message";

	private static final int MIN_WIDTH_TABLE_WINDOW = 300;

	protected String entity = null;

	protected List formAttributes = new Vector(5);

	protected List queryAttributes = new Vector(5);

	protected List requiredQueryAttributes = new Vector(5);

	protected List listenedAttributeList = new Vector(5);

	protected EntityReferenceLocator locator = null;

	protected Table table = null;

	protected JDialog tableWindow = null;

	private static String okKey = "application.accept";

	protected JButton oKButton = null;

	protected final String ASTERISK = "*";

	protected boolean addAsterisk = false;

	protected boolean someFieldChangeByUser = false;

	/**
	 * @param parameters This class uses Button parameters and Table parameters. the 'table_key'
	 *        parameter is used as key for the table because there is a conflict with the 'key'
	 *        parameter'. Additional parameters:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>entity</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Associated entity.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>source</td>
	 *        <td>attr1;...;attrN</td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Data field attributes separated by ';'. This fields will be filled with the query
	 *        result</td>
	 *        </tr>
	 *        <tr>
	 *        <td>search</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Field attributes used in the query to show the table.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>searchrequired</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>no</td>
	 *        <td>Field attributes (separated by ';'), which are specified in 'search' and are necessary
	 *        to make the query of data that will be shown in the table.It allows to force to intoduce
	 *        search criterion</td>
	 *        </tr>
	 *        <tr>
	 *        <td>addasterisk</td>
	 *        <td>yes,no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Specifies if it automatically includes an * at the end of the string value when it
	 *        searches for the query attributes</td>
	 *        </tr>
	 *        <tr>
	 *        <td>listen</td>
	 *        <td></td>
	 *        <td>The same as 'search'</td>
	 *        <td>no</td>
	 *        <td>Field attributes (subset as 'source') to listen for the button. If some of this fields
	 *        changes the record select window will be shown</td>
	 *        </tr>
	 *        <tr>
	 *        <td>table_key</td>
	 *        <td></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>Name of the table primary key</td>
	 *        </tr>
	 *
	 *
	 *        </table>
	 *        <br>
	 *        </tr>
	 *        <![if supportMisalignedColumns]>
	 *        </table>
	 */
	public QueryButton(final Map parameters) {
		super(parameters);
		this.setMargin(new Insets(0, 0, 0, 0));
		final Object entity = parameters.get("entity");
		if (entity == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'entity' parameter is mandatory");
		}
		this.entity = entity.toString();
		final Object source = parameters.get("source");
		if (source == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'source' parameter is mandatory");
		}
		StringTokenizer st = new StringTokenizer(source.toString(), ";");
		while (st.hasMoreTokens()) {
			this.formAttributes.add(st.nextToken());
		}
		final Object search = parameters.get("search");
		if (search == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'search' parameter is mandatory");
		}
		st = new StringTokenizer(search.toString(), ";");
		while (st.hasMoreTokens()) {
			this.queryAttributes.add(st.nextToken());
		}

		final Object useasterisk = parameters.get("addasterisk");
		if (useasterisk != null) {
			if (useasterisk.equals("yes")) {
				this.addAsterisk = true;
			}
		}

		// Filter.
		final Object searchrequired = parameters.get("searchrequired");
		if (searchrequired != null) {
			st = new StringTokenizer(searchrequired.toString(), ";");
			while (st.hasMoreTokens()) {
				final String at = st.nextToken();
				if (this.queryAttributes.contains(at)) {
					this.requiredQueryAttributes.add(at);
				}
			}
		}

		// Listen
		final Object listen = parameters.get("listen");
		if (listen != null) {
			st = new StringTokenizer(listen.toString(), ";");
			while (st.hasMoreTokens()) {
				final String at = st.nextToken();
				if (this.formAttributes.contains(at)) {
					this.listenedAttributeList.add(at);
				}
			}
		} else {
			this.listenedAttributeList = ObjectTools.clone(this.queryAttributes);
		}

		final Object tablekey = parameters.get("table_key");
		if (tablekey == null) {
			throw new IllegalArgumentException(this.getClass().toString() + " 'table_key' parameter is mandatory");
		}
		parameters.put("key", tablekey);
		parameters.put("numrowscolumn", "no");
		if (!parameters.containsKey("autoadjustheader")) {
			parameters.put("autoadjustheader", "no");
		}
		if (!parameters.containsKey("rows")) {
			parameters.put("rows", "15");
		}
		try {
			final long tIni = System.currentTimeMillis();
			this.table = new Table(parameters);
			this.table.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.table.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						if (QueryButton.this.table.getJTable().getSelectedRowCount() > 0) {
							if (QueryButton.this.oKButton != null) {
								QueryButton.this.oKButton.setEnabled(true);
							}
						} else {
							if (QueryButton.this.oKButton != null) {
								QueryButton.this.oKButton.setEnabled(false);
							}
						}
					}
				}
			});
			if (!this.table.hasForm()) {
				this.table.getJTable().addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(final MouseEvent e) {
						if (e.getClickCount() == 2) {
							e.consume();
							if (QueryButton.this.table.getSelectedRow() >= 0) {
								QueryButton.this.oKButton.doClick(10);
							}
						}
					}
				});
			}
			this.table.packTable();
			if (ApplicationManager.DEBUG_TIMES) {
				QueryButton.logger.debug("QueryButton: Table creation time: " + (System.currentTimeMillis() - tIni));
			}
		} catch (final Exception e) {
			QueryButton.logger.error("Table has not been created.", e);
		}
		super.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				QueryButton.this.processingAction();
			}
		});
	}

	protected void processingAction() {
		// Evaluates if filter is necessary.
		for (int i = 0; i < this.requiredQueryAttributes.size(); i++) {
			final String atr = this.requiredQueryAttributes.get(i).toString();

			if (this.parentForm.isEmpty(atr)) {
				final DataComponent cData = this.parentForm.getDataFieldReference(atr);
				String sText = atr;
				if ((cData != null) && (cData instanceof DataField)) {
					if (((DataField) cData).getLabelText() != null) {
						sText = ((DataField) cData).getLabelText();
					}
				}
				final Object[] args = { ApplicationManager.getTranslation(sText, QueryButton.this.resourcesFileName) };
				final String sTranslation = ApplicationManager.getTranslation(QueryButton.MISSING_DATA_MESSAGE_KEY,
						QueryButton.this.resourcesFileName, args);
				this.parentForm.message(SwingUtilities.getWindowAncestor(QueryButton.this), sTranslation,
						Form.WARNING_MESSAGE);
				this.deleteDataFields();
				this.requestFocus();
				return;
			}
		}
		if (this.tableWindow == null) {
			this.createTableWindow();
		}
		// Now executes the query
		try {
			final Entity ent = this.locator.getEntityReference(this.entity);
			final Map kv = new Hashtable();
			for (int i = 0; i < this.queryAttributes.size(); i++) {
				Object v = this.parentForm.getDataFieldValue(this.queryAttributes.get(i).toString());
				if (v != null) {
					if ((v instanceof String) && (this.addAsterisk) && (((String) v).indexOf(this.ASTERISK) < 0)) {
						v = v + this.ASTERISK;
					}
					kv.put(this.queryAttributes.get(i), v);
				}
			}

			final EntityResult res = ent.query(kv, this.table.getAttributeList(), this.locator.getSessionId());
			if (res.getCode() == EntityResult.OPERATION_WRONG) {
				this.parentForm.message(SwingUtilities.getWindowAncestor(QueryButton.this), res.getMessage(),
						Form.ERROR_MESSAGE);
			} else {
				ConnectionManager.checkEntityResult(res, this.locator);
				// Now look in the form the values of the keys. If these values
				// exist then select the row
				final Map hKeys = new Hashtable();
				final List vKeys = this.table.getKeys();
				boolean all = true;
				for (int i = 0; i < vKeys.size(); i++) {
					final Object v = this.parentForm.getDataFieldValue(vKeys.get(i).toString());
					if (v == null) {
						all = false;
						break;
					} else {
						hKeys.put(vKeys.get(i), v);
					}
				}
				this.table.setValue(res);
				if (all) {
					this.table.setSelectedRow(this.table.getRowForKeys(hKeys));
				}
				this.showTableWindow();
			}
		} catch (final Exception ex) {
			if (ApplicationManager.DEBUG) {
				QueryButton.logger.error(null, ex);
			} else {
				QueryButton.logger.trace(null, ex);
			}
			this.parentForm.message(SwingUtilities.getWindowAncestor(QueryButton.this), ex.getMessage(),
					Form.ERROR_MESSAGE);
		}
	}

	@Override
	public void setParentForm(final Form f) {
		super.setParentForm(f);

		final List vListenedAttributes = this.listenedAttributeList;
		for (int i = 0; i < vListenedAttributes.size(); i++) {
			final Object at = vListenedAttributes.get(i);
			final DataComponent c = this.parentForm.getDataFieldReference(at.toString());
			if ((c != null) && (c instanceof DataField)) {
				final DataField dfField = (DataField) c;
				dfField.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChanged(final ValueEvent e) {
						// ONLY USER
						if (e.getType() != ValueEvent.USER_CHANGE) {
							return;
						}
						if ((e.getNewValue() == null) && (e.getOldValue() == null)) {
							// The value has not changed
						} else if (e.getNewValue() == null) {
							QueryButton.this.someFieldChangeByUser = true;
						} else if (e.getOldValue() == null) {
							QueryButton.this.someFieldChangeByUser = true;
						} else if (!e.getNewValue().equals(e.getOldValue())) {
							QueryButton.this.someFieldChangeByUser = true;
						}
					}
				});
				dfField.addFocusListener(new FocusAdapter() {

					@Override
					public void focusLost(final FocusEvent e) {
						if (ApplicationManager.DEBUG) {
							QueryButton.logger.debug(
									this.getClass() + " focusLost en campo " + at + " temporary: " + e.isTemporary());
						}
						if (!QueryButton.this.someFieldChangeByUser && (e.isTemporary())) {
							QueryButton.this.processingAction();
						}
					}
				});
			}
		}

	}

	protected void showTableWindow() {
		if (this.tableWindow == null) {
			this.createTableWindow();
		}
		this.tableWindow.pack();
		if (this.tableWindow.getWidth() < QueryButton.MIN_WIDTH_TABLE_WINDOW) {
			this.tableWindow.setSize(QueryButton.MIN_WIDTH_TABLE_WINDOW, this.tableWindow.getHeight());
		}
		this.tableWindow.setVisible(true);
	}

	protected void createTableWindow() {
		final Window w = SwingUtilities.getWindowAncestor(this);
		if ((w instanceof Frame) || (w == null)) {
			this.tableWindow = new EJDialog((Frame) w,
					ApplicationManager.getTranslation(this.entity, this.resourcesFileName), true) {

				@Override
				public void processWindowEvent(final WindowEvent e) {
					super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						try {
							QueryButton.this.someFieldChangeByUser = false;
							// Set the value for the selected index
							final Map hKeysValues = new Hashtable();
							final List vTableKeys = QueryButton.this.table.getKeys();
							for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
								if (vTableKeys.contains(QueryButton.this.formAttributes
										.get(i))
										&& (QueryButton.this.parentForm.getDataFieldValueFromFormCache(
												QueryButton.this.formAttributes.get(i).toString()) != null)) {
									hKeysValues.put(QueryButton.this.formAttributes.get(i),
											QueryButton.this.parentForm.getDataFieldValueFromFormCache(
													QueryButton.this.formAttributes.get(i).toString()));
								}
							}
							// If hKeysValues is empty then not data found
							if (hKeysValues.isEmpty()) {
								for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
									QueryButton.this.parentForm
									.deleteDataField(QueryButton.this.formAttributes.get(i).toString());
								}
								return;
							}
							final EntityResult res = QueryButton.this.query(hKeysValues);
							if (!res.isEmpty()) {
								final Map hData = res.getRecordValues(0);
								QueryButton.this.parentForm.setDataFieldValues(hData);
							} else {
								for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
									QueryButton.this.parentForm
									.deleteDataField(QueryButton.this.formAttributes.get(i).toString());
								}
								return;
							}
						} catch (final Exception ex) {
							QueryButton.logger.error(null, ex);
						}
					}
				}
			};
		} else {
			this.tableWindow = new EJDialog((Dialog) w,
					ApplicationManager.getTranslation(this.entity, this.resourcesFileName), true) {

				@Override
				public void processWindowEvent(final WindowEvent e) {
					super.processWindowEvent(e);
					if (e.getID() == WindowEvent.WINDOW_CLOSING) {
						try {
							QueryButton.this.someFieldChangeByUser = false;
							// Set the value for the current selection
							final Map hKVs = new Hashtable();
							final List vTableKeys = QueryButton.this.table.getKeys();
							for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
								if (vTableKeys.contains(QueryButton.this.formAttributes
										.get(i))
										&& (QueryButton.this.parentForm.getDataFieldValueFromFormCache(
												QueryButton.this.formAttributes.get(i).toString()) != null)) {
									hKVs.put(QueryButton.this.formAttributes.get(i),
											QueryButton.this.parentForm.getDataFieldValueFromFormCache(
													QueryButton.this.formAttributes.get(i).toString()));
								}
							}
							// If hKVs is empty then not data found
							if (hKVs.isEmpty() || (vTableKeys.size() != hKVs.size())) {
								for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
									QueryButton.this.parentForm
									.deleteDataField(QueryButton.this.formAttributes.get(i).toString());
								}
								return;
							}
							final EntityResult res = QueryButton.this.query(hKVs);
							if (!res.isEmpty()) {
								final Map hData = res.getRecordValues(0);
								QueryButton.this.parentForm.setDataFieldValues(hData);
							} else {
								for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
									QueryButton.this.parentForm
									.deleteDataField(QueryButton.this.formAttributes.get(i).toString());
								}
								return;
							}
						} catch (final Exception ex) {
							QueryButton.logger.error(null, ex);
						}
					}
				}
			};
		}
		final JPanel jbButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		this.tableWindow.getContentPane().add(jbButtonsPanel, BorderLayout.SOUTH);
		this.tableWindow.getContentPane().add(this.table);
		this.oKButton = new JButton(ApplicationManager.getTranslation(QueryButton.okKey, this.resourcesFileName));
		this.oKButton.setIcon(ImageManager.getIcon(ImageManager.OK));
		jbButtonsPanel.add(this.oKButton);
		this.tableWindow.pack();
		ApplicationManager.center(this.tableWindow);
		this.oKButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				// Selected row
				final int iSelectedRow = QueryButton.this.table.getSelectedRow();
				if (iSelectedRow >= 0) {
					// Get all data related with this button and clear their
					// values
					QueryButton.this.deleteDataFields();
					for (int i = 0; i < QueryButton.this.formAttributes.size(); i++) {
						QueryButton.this.parentForm.deleteDataField(QueryButton.this.formAttributes.get(i).toString());
					}
					// Now query in function of the table
					final Map hKv = QueryButton.this.table.getRowData(iSelectedRow);
					// Set the values
					for (final Object oKey : hKv.keySet()) {
						if (QueryButton.this.formAttributes.contains(oKey)) {
							QueryButton.this.parentForm.setDataFieldValue(oKey, hKv.get(oKey));
						}
					}
					QueryButton.this.someFieldChangeByUser = false;
					QueryButton.this.tableWindow.setVisible(false);
				}
			}
		});
	}

	private void deleteDataFields() {
		for (int i = 0; i < this.formAttributes.size(); i++) {
			this.parentForm.deleteDataField(this.formAttributes.get(i).toString());
		}
	}

	public void addActionListener(final ActionEvent e) {

	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	@Override
	public void setResourceBundle(final ResourceBundle res) {
		super.setResourceBundle(res);
		try {
			if (this.table != null) {
				this.table.setResourceBundle(res);
			}
			this.oKButton.setText(ApplicationManager.getTranslation(QueryButton.okKey, res));
		} catch (final Exception e) {
			QueryButton.logger.trace(null, e);
		}
	}

	protected EntityResult query(final Map kv) throws Exception {
		final Entity ent = this.locator.getEntityReference(this.entity);
		final EntityResult res = ent.query(kv, this.table.getAttributeList(), this.locator.getSessionId());
		if (EntityResult.OPERATION_WRONG != res.getCode()) {
			ConnectionManager.checkEntityResult(res, this.locator);
		}
		return res;
	}

}
