package com.ontimize.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.container.TabPanel;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.ImageDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.db.Entity;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ObjectTools;

public class FormExt extends Form {

	private static final Logger logger = LoggerFactory.getLogger(FormExt.class);

	/** GUI Text */
	public static final String TITLE_PRINTING_SETUP_WINDOW_KEY = "form.print_form_data";

	/** GUI Text */
	public static final String INFO_PRINTING_SETUP_WINDOW_KEY = "form.select_form_data_to_print";

	/** Conditions for dispatch the navigation events */
	protected boolean fireNavigationEvents = true;

	protected class PrintingSetupWindow extends JDialog {

		private final JPanel checkBoxPanel = new JPanel(new GridLayout(0, 2));

		private final List checkBoxList = new Vector();

		private final JLabel labelInfo = new JLabel();

		private final JButton acceptButton = new JButton(ApplicationManager.getTranslation("application.accept"));

		private final JButton cancelButton = new JButton(ApplicationManager.getTranslation("application.cancel"));

		private Thread printingThread = null;

		private Map kvQuery = null;

		public PrintingSetupWindow(final Map queryFilter) {
			super(FormExt.this.parentFrame, true);
			this.kvQuery = queryFilter;
			String windowTitle = "Print form data";
			try {
				if (FormExt.this.resourcesFile != null) {
					windowTitle = FormExt.this.resourcesFile.getString(FormExt.TITLE_PRINTING_SETUP_WINDOW_KEY);
				}
			} catch (final Exception e) {
				FormExt.logger.trace(null, e);
			}
			this.setTitle(windowTitle);
			String info = "Select data to print:";
			try {
				if (FormExt.this.resourcesFile != null) {
					info = FormExt.this.resourcesFile.getString(FormExt.INFO_PRINTING_SETUP_WINDOW_KEY);
				}
			} catch (final Exception e) {
				FormExt.logger.trace(null, e);
			}
			this.labelInfo.setText(info);
			this.getContentPane().add(this.labelInfo, BorderLayout.NORTH);
			final JPanel buttonsPanel = new JPanel();
			buttonsPanel.add(this.acceptButton);
			buttonsPanel.add(this.cancelButton);
			this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
			this.createCheckBoxPanel();
			this.getContentPane().add(new JScrollPane(this.checkBoxPanel));
			this.createButtonListeners();
			this.pack();
			ApplicationManager.center(this);
		}

		private void createCheckBoxPanel() {
			// Search the fields attributes
			final List vFieldsAttributes = new Vector();
			for (int i = 0; i < FormExt.this.getDataFieldAttributeList().size(); i++) {
				final Object attribute = FormExt.this.getDataFieldAttributeList().get(i);
				if (attribute instanceof String) {
					if ((attribute != null) && (FormExt.this.getDataFieldReference(
							attribute.toString()) instanceof DataField)
							&& !((FormExt.this
									.getDataFieldReference(attribute.toString()) instanceof ImageDataField))) {
						if (!((DataField) FormExt.this.getDataFieldReference(attribute.toString())).isHidden()
								&& FormExt.this.checkAccessPermission(attribute.toString())) {
							vFieldsAttributes.add(attribute);
						}
					}
				}
			}

			for (int i = 0; i < vFieldsAttributes.size(); i++) {
				try {
					String translatedAttribute = vFieldsAttributes.get(i).toString();
					try {
						if (FormExt.this.resourcesFile != null) {
							translatedAttribute = FormExt.this.resourcesFile.getString(translatedAttribute);
						}
					} catch (final Exception e) {
						FormExt.logger.trace(null, e);
					}
					final JCheckBox check = new JCheckBox(translatedAttribute);
					check.setName(vFieldsAttributes.get(i).toString());
					check.setSelected(true);
					this.checkBoxList.add(check);
					this.checkBoxPanel.add(check);
				} catch (final Exception e) {
					FormExt.logger.trace(null, e);
				}
			}
		}

		private void createButtonListeners() {
			this.acceptButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					PrintingSetupWindow.this.setVisible(false);
					// Print
					PrintingSetupWindow.this.acceptButton.setEnabled(false);
					PrintingSetupWindow.this.cancelButton.setEnabled(false);
					final List queryAttributes = new Vector();
					for (int i = 0; i < PrintingSetupWindow.this.checkBoxList.size(); i++) {
						final JCheckBox cb = (JCheckBox) PrintingSetupWindow.this.checkBoxList.get(i);
						if (cb.isSelected()) {
							queryAttributes.add(cb.getName());
						}
					}
					// Keys
					for (int i = 0; i < FormExt.this.keys.size(); i++) {
						if (!queryAttributes.contains(FormExt.this.keys.get(i))) {
							FormExt.logger.debug("Added column for query: {}", FormExt.this.keys.get(i));
							queryAttributes.add(FormExt.this.keys.get(i));
						}
					}
					// First query and then print
					PrintingSetupWindow.this.printingThread = new Thread() {

						@Override
						public void run() {
							final TopWindow w = new TopWindow(FormExt.this.parentFrame, "performing_query",
									FormExt.this.resourcesFile, ImageManager.getIcon(ImageManager.SEARCHING),
									null);
							try {
								w.show();
								final EntityReferenceLocator locator = FormExt.this.formManager.getReferenceLocator();
								final Entity ent = locator.getEntityReference(FormExt.this.entityName);
								final EntityResult res = ent.query(PrintingSetupWindow.this.kvQuery, queryAttributes,
										locator.getSessionId());
								if (res.getCode() == EntityResult.OPERATION_WRONG) {
									FormExt.this.message(PrintingSetupWindow.this, "form.error_ocurred_while_printing",
											Form.ERROR_MESSAGE, res.getMessage());
									return;
								} else {
									w.hide();
									w.dispose();
									// Creates the table
									final Map p = new Hashtable();
									final StringBuilder sbColumns = new StringBuilder();
									for (int i = 0; i < queryAttributes.size(); i++) {
										final Object oAttribute = queryAttributes.get(i);
										if (oAttribute instanceof String) {
											sbColumns.append((String) oAttribute);
											if (i < (queryAttributes.size() - 1)) {
												sbColumns.append(";");
											}
										}
									}
									p.put("cols", sbColumns.toString());
									p.put("entity", FormExt.this.entityName);
									if (!FormExt.this.keys.isEmpty()) {
										p.put("key", FormExt.this.keys.get(0));
										if (FormExt.this.keys.size() > 1) {
											final StringBuilder cadenaKeys = new StringBuilder();
											for (int i = 1; i < FormExt.this.keys.size(); i++) {
												cadenaKeys.append(FormExt.this.keys.get(i));
												if (i < (FormExt.this.keys.size() - 1)) {
													cadenaKeys.append(";");
												}
											}
											p.put("keys", cadenaKeys);
										}
									}
									final Table t = new Table(p);
									t.setParentForm(FormExt.this);
									t.setParentFrame(FormExt.this.parentFrame);
									t.setResourceBundle(FormExt.this.resourcesFile);
									t.setValue(res);
									t.print();
								}
							} catch (final Exception e) {
								FormExt.logger.error(null, e);
								PrintingSetupWindow.this.setVisible(false);
								FormExt.this.message(PrintingSetupWindow.this, "form.error_ocurred_while_printing",
										Form.ERROR_MESSAGE);
								return;
							} finally {
								w.hide();
								w.dispose();
								PrintingSetupWindow.this.dispose();
							}
						}
					};
					PrintingSetupWindow.this.printingThread.start();
				}
			});
			this.cancelButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					PrintingSetupWindow.this.dispose();
				}
			});
		}

	};

	/** A list with the record indexes which has been queried */
	protected List queryRecordIndex = null;

	/**
	 * Creates a Form instance with the parameters establishes in <code>Hastable</code>
	 * @param parameters
	 * @throws Exception
	 */
	public FormExt(final Map parameters) throws Exception {
		super(parameters);
		if ((this.printButton != null) && this.buttons) {
			this.printButton.setVisible(true);
		}
		if ((this.refreshButton != null) && this.buttons) {
			this.refreshButton.setVisible(true);
		}
		if ((this.keys == null) || this.keys.isEmpty()) {
			throw new IllegalArgumentException(
					this.getClass().toString() + " : Parameter 'keys' is required in FormExt.");
		}
		this.previousButton.removeActionListener(this.previousButtonListener);
		this.nextButton.removeActionListener(this.nextButtonListener);
		this.startButton.removeActionListener(this.startButtonListener);
		this.endButton.removeActionListener(this.endButtonListener);
		// Install the new listeners
		this.installButtonListeners();
	}

	/**
	 * Installs the navigation listeners
	 */
	protected void installButtonListeners() {
		/*
		 * The FormExt only stores the record keys and the current record data. The data list contains the
		 * keys and the current record values
		 */
		this.previousButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int newIndex = FormExt.this.currentIndex;
					if (FormExt.this.currentIndex > 0) {
						newIndex--;
					}
					FormExt.this.endButton.setEnabled(true);
					FormExt.this.nextButton.setEnabled(true);

					FormExt.this.updateDataFieldNavegationButton(newIndex);
				} catch (final Exception ex) {
					FormExt.logger.error(null, ex);
				} finally {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		this.nextButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int newIndex = FormExt.this.currentIndex;
					if (FormExt.this.currentIndex < (FormExt.this.vectorSize - 1)) {
						newIndex++;
					}

					FormExt.this.updateDataFieldNavegationButton(newIndex);
				} catch (final Exception ex) {
					FormExt.logger.error(null, ex);
				} finally {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		this.startButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					final int iNewIndex = 0;
					FormExt.this.updateDataFieldNavegationButton(iNewIndex);
				} catch (final Exception ex) {
					FormExt.logger.error(null, ex);
				} finally {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		this.endButtonListener = new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				try {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					int newIndex = FormExt.this.vectorSize - 1;
					newIndex = Math.max(newIndex, 0);
					FormExt.this.updateDataFieldNavegationButton(newIndex);
				} catch (final Exception ex) {
					FormExt.logger.error(null, ex);
				} finally {
					FormExt.this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		};
		this.previousButton.addActionListener(this.previousButtonListener);
		this.nextButton.addActionListener(this.nextButtonListener);
		this.startButton.addActionListener(this.startButtonListener);
		this.endButton.addActionListener(this.endButtonListener);
	}

	protected void freeFormCache() {
	}

	/**
	 * Updates the navigation button state depending on the current record index and the total record
	 * count.
	 */

	protected void updateNavigationButtonState() {
		if (this.vectorSize <= 1) {
			this.previousButton.setEnabled(false);
			this.startButton.setEnabled(false);
			this.endButton.setEnabled(false);
			this.nextButton.setEnabled(false);
			return;
		} else {
			this.previousButton.setEnabled(true);
			this.startButton.setEnabled(true);
			this.endButton.setEnabled(true);
			this.nextButton.setEnabled(true);
		}
		if (this.currentIndex == 0) {
			this.previousButton.setEnabled(false);
			this.startButton.setEnabled(false);
			this.endButton.setEnabled(true);
			this.nextButton.setEnabled(true);
		} else if (this.currentIndex >= (this.vectorSize - 1)) {
			this.nextButton.setEnabled(false);
			this.endButton.setEnabled(false);
			this.startButton.setEnabled(true);
			this.previousButton.setEnabled(true);
		}
	}

	@Override
	public void updateDataFields(final Map data) {
		this.updateDataFields(data, 0);
	}

	@Override
	protected void updateDataFields_Internal(final int index) {
		try {
			// If the record does not exist yet then query.
			if (this.queryRecordIndex.contains(new Integer(index)) && !this.existNoQueriedDataField()) {
				super.updateDataFields_Internal(index);
				this.updateNavigationButtonState();
			} else {
				final EntityResult result = this.query(index);

				if (result.getCode() == EntityResult.OPERATION_WRONG) {
					if (this.totalDataList instanceof EntityResult) {
						((EntityResult) this.totalDataList).setCode(EntityResult.OPERATION_WRONG);
						((EntityResult) this.totalDataList).setMessage(result.getMessage());
					}
					this.message(result.getMessage(), Form.ERROR_MESSAGE);
					return;
				}

				if (this.totalDataList instanceof EntityResult) {
					((EntityResult) this.totalDataList).setCode(result.getCode());
				}
				// remove previous values
				if (totalDataList != null) {
					for (final Object ob : new ArrayList(totalDataList.keySet())) {
						if (!getAttributesToQuery().contains(ob)) {
							totalDataList.remove(ob);
						}
					}
				}
				// Put the values in the data list in the appropriate index
				final Enumeration keys = result.keys();
				while (keys.hasMoreElements()) {
					final Object key = keys.nextElement();
					final Object oValue = result.get(key);
					if (oValue instanceof List) {
						// Search for the List in the data list
						List vValues = (List) FormExt.this.totalDataList.get(key);
						if ((vValues == null) || vValues.isEmpty()) {
							vValues = new Vector(this.vectorSize);
							for (int i = 0; i < this.vectorSize; i++) {
								vValues.add(i, null);
							}
							this.totalDataList.put(key, vValues);
						}
						vValues.remove(index);
						if (((List) oValue).size() > 0) {
							vValues.add(index, ((List) oValue).get(0));
						} else {
							vValues.add(index, null);
						}
					} else {
						// Search for the value in the data list
						List vValues = (List) this.totalDataList.get(key);
						if (vValues == null) {
							// Initializes the vector
							vValues = new Vector(this.vectorSize);
							for (int i = 0; i < this.vectorSize; i++) {
								vValues.add(i, null);
							}
							this.totalDataList.put(key, vValues);
						}
						// Put the current data record values in the appropriate
						// index
						vValues.remove(index);
						vValues.add(index, oValue);
					}
				}
				super.updateDataFields_Internal(index);
				this.updateNavigationButtonState();
			}
		} catch (final Exception e) {
			FormExt.logger.error(null, e);
		}
	}

	/**
	 * Gets the attribute list that be used to perform the query. This list is obtained from column list
	 * established in the 'column' xml parameter and the columns established by the user in table view.
	 * @return a <Vector> with the attribute list.
	 */
	public List getAttributesToQuery() {
		final List v = ObjectTools.clone(this.keys);
		if ((this.tableViewColumns != null) && (this.tableViewColumns.size() > 0)) {
			for (int i = 0; i < this.tableViewColumns.size(); i++) {
				if (!v.contains(this.tableViewColumns.get(i))) {
					v.add(this.tableViewColumns.get(i));
				}
			}
		}

		if ((this.additionalTableViewColumns != null) && (this.additionalTableViewColumns.size() > 0)) {
			for (int i = 0; i < this.additionalTableViewColumns.size(); i++) {
				if (!v.contains(this.additionalTableViewColumns.get(i))) {
					v.add(this.additionalTableViewColumns.get(i));
				}
			}
		}

		return v;
	}

	/**
	 * Checks whether exist data fields that haven't been queried
	 * @return true exist not queried data fields; false otherwise.
	 */
	protected boolean existNoQueriedDataField() {
		for (int i = 0; i < this.componentList.size(); i++) {
			if (this.componentList.get(i) instanceof TabPanel) {
				final List v = ((TabPanel) this.componentList.get(i)).initNotQueriedDataFieldAttributes();
				if (v.size() > 0) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void refreshCurrentDataRecord() {
		try {
			if (this.getInteractionManager().currentMode == InteractionManager.UPDATE) {
				this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				if ((this.totalDataList != null) && !this.totalDataList.isEmpty()) {
					if (this.queryRecordIndex != null) {
						this.queryRecordIndex.remove(new Integer(this.currentIndex));
						this.updateDataFields(this.currentIndex);
					}
				}
			}
		} catch (final Exception e) {
			FormExt.logger.error("Error refreshing current record values", e);
		} finally {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

	}

	/**
	 * Shows a window where the column list to print can be selected in.
	 * @param keysValues a <code>Hashtable</code> with the record keys to be printed.
	 */
	public void printMultipleRecords(final Map keysValues) {
		// Show a window to select the columns to query:
		if (this.printButton != null) {
			this.printButton.setEnabled(false);
		}
		if (this.printTemplateButton != null) {
			this.printTemplateButton.setEnabled(false);
		}

		final PrintingSetupWindow vConfigImpresion = new PrintingSetupWindow(keysValues);
		vConfigImpresion.setVisible(true);

		if (this.printButton != null) {
			this.printButton.setEnabled(true);
		}
		if (this.printTemplateButton != null) {
			this.printTemplateButton.setEnabled(true);
		}
	}

	@Override
	public void updateDataFields(Map data, final int currentIndex) {
		this.queryRecordIndex = new Vector();
		if (data == null) {
			data = new Hashtable();
		}
		if (!(data instanceof EntityResult)) {
			data = new EntityResultMapImpl(new HashMap(data));
		}
		if (!data.isEmpty()) {
			// Checks if the keys exist.
			this.vectorSize = 0;
			for (int i = 0; i < this.keys.size(); i++) {
				final Object oKey = this.keys.get(i);
				final Object v = data.get(oKey);
				if (!(v instanceof List)) {
					FormExt.logger.warn(
							"The Map used by method updateDataFields() does not contain a List for the key: {}.",
							oKey);
					this.vectorSize = 1;
					continue;
				}
				final List vKeys = (List) v;
				// if (vKeys == null) {
				// logger.warn("The Map used by method updateDataFields() does not contain the key: {}.",
				// oKey);
				// vectorSize = 0;
				// break;
				// } else {
				// if (vectorSize == 0 && vKeys.size() > 0) {
				// vectorSize = vKeys.size();
				// }
				// vectorSize = Math.min(vectorSize, vKeys.size());
				// }
				if ((this.vectorSize == 0) && (vKeys.size() > 0)) {
					this.vectorSize = vKeys.size();
				}
				this.vectorSize = Math.min(this.vectorSize, vKeys.size());
			}
			if ((this.vectorSize > currentIndex) && (currentIndex >= 0)) {
				super.updateDataFields(data, currentIndex);
			} else {
				if (currentIndex >= this.vectorSize) {
					FormExt.logger.warn("TThe index to update by updateDataFields() method is {} but data size is: {}.",
							currentIndex, this.vectorSize);
				}
				if (currentIndex >= 0) {
					super.updateDataFields(data, 0);
				}
			}
		} else {
			super.updateDataFields(data);
		}
	}

	@Override
	public void deleteRecordFromFormCache(final int index) {
		super.deleteRecordFromFormCache(index);
		// Update the index in the cache because when a record is removed all
		// index are modified

		// Record in the position index+1 is now in position index. All indexes
		// > index must change one position

		if ((this.queryRecordIndex != null) && (index >= 0)) {
			for (int i = 0; i < this.queryRecordIndex.size(); i++) {
				final Object ind = this.queryRecordIndex.get(i);
				if ((ind != null) && (((Number) ind).intValue() > index)) {
					this.queryRecordIndex.set(i, new Integer(((Number) ind).intValue() - 1));
				}
			}
		}
	}

	@Override
	public void free() {
		super.free();
	}

}
