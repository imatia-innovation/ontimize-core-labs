package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.MultipleValue;
import com.ontimize.util.ParseUtils;

/**
 * This class provides an implementation of an AbstractMultipleReferenceDataField.
 * <p>
 *
 * @author Imatia Innovation
 */
public class MultipleReferenceDataField extends AbstractMultipleReferenceDataField {

	private static final Logger logger = LoggerFactory.getLogger(MultipleReferenceDataField.class);

	/**
	 * An instance for a search button.
	 */
	protected JButton searchButton = new FieldButton();

	/**
	 * An instance for a delete button.
	 */
	protected JButton deleteButton = new FieldButton();

	/**
	 * An instance for a table Window. By default, null.
	 */
	protected TableWindow tableWindow = null;

	/**
	 * A reference for the ok button. By default, null.
	 */
	protected JButton okButton = null;

	protected boolean showButtons = true;

	/**
	 * The reference for refreshing the cache button. By default, null.
	 */
	protected JButton refreshCacheButton = null;

	/**
	 * The reference for the table.
	 */
	protected Table t;

	/**
	 * The window title.
	 */
	protected String windowTitle;

	/**
	 * A key for search operation.
	 */
	protected static String auxCodeLabelKey = "search";

	private static final int TABLE_WINDOW_MINIMUM_WIDTH = 300;

	/**
	 * The class constructor. Initializes parameters, installs inner listeners and creates additional
	 * components.
	 * <p>
	 * @param parameters the <code>Hashtable</code> with parameters
	 */
	public MultipleReferenceDataField(final Map parameters) throws Exception {
		this.createComponent();
		this.init(parameters);
		this.createCodeComponents();
		this.installInnerListener();
		this.createAdditionalComponents();

	}

	/**
	 * Initializes parameters.
	 * <p>
	 * @param parameters
	 *
	 *        The <code>Hashtable</code> with additional parameters:
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
	 *        <td>buttons</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td>no</td>
	 *        <td>Show/Hide search and delete buttons</td>
	 *        </tr>
	 *        <tr>
	 *        <td>title</td>
	 *        <td></td>
	 *        <td>entity</td>
	 *        <td>no</td>
	 *        <td>The title for window.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>opaquebuttons</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Opacity condition for buttons showed with this field</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>numrowscolumn</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Shows or hide a column containing the row numumber in the result table</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>controlsvisible</td>
	 *        <td>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Shows or hide the table controls in the result table</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>autoadjustheader</td>
	 *        <td>yes/no</td>
	 *        <td>yes</td>
	 *        <td></td>
	 *        <td>Allows the TableHeader to adjusts its height to the FontText in order to see the the
	 *        text when the font size changes.</td>
	 *        </tr>
	 *
	 *        <tr>
	 *        <td>rows</td>
	 *        <td>Integer</td>
	 *        <td>15</td>
	 *        <td></td>
	 *        <td>Default width for the table header</td>
	 *        </tr>
	 *
	 *        </TABLE>
	 */

	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		final Object title = parameters.get("title");
		if (title != null) {
			this.windowTitle = title.toString();
		} else {
			this.windowTitle = this.entity;
		}

		// Create the table
		parameters.put("key", this.keys.get(0));
		if (this.keys.size() > 1) {
			final StringBuilder buffer = new StringBuilder();
			for (int t = 1; t < this.keys.size(); t++) {
				buffer.append(this.keys.get(t));
				if ((t + 1) != this.keys.size()) {
					buffer.append(";");
				}
			}
			parameters.put("keys", buffer.toString());
		}
		parameters.put("numrowscolumn", "no");
		parameters.put("controlsvisible", "no");
		if (!parameters.containsKey("autoadjustheader")) {
			parameters.put("autoadjustheader", "no");
		}
		if (!parameters.containsKey("rows")) {
			parameters.put("rows", "15");
		}

		parameters.remove("cods");

		if (parameters.containsKey("opaquebuttons")
				&& !ApplicationManager.parseStringValue(parameters.get("opaquebuttons").toString())) {
			if (this.deleteButton != null) {
				this.deleteButton.setOpaque(false);
			}
			if (this.searchButton != null) {
				this.searchButton.setOpaque(false);
			}
		}

		this.showButtons = ParseUtils.getBoolean((String) parameters.get("buttons"), true);
		if (!this.showButtons) {
			if (this.deleteButton != null) {
				this.deleteButton.setVisible(false);
			}
			if (this.searchButton != null) {
				this.searchButton.setVisible(false);
			}
		}

		try {
			final long tIni = System.currentTimeMillis();
			this.t = new Table(parameters);
			// t.enableFiltering(false);
			this.t.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.t.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						if (MultipleReferenceDataField.this.t.getJTable().getSelectedRowCount() > 0) {
							if (MultipleReferenceDataField.this.okButton != null) {
								MultipleReferenceDataField.this.okButton.setEnabled(true);
							}
						} else {
							if (MultipleReferenceDataField.this.okButton != null) {
								MultipleReferenceDataField.this.okButton.setEnabled(false);
							}
						}
					}
				}
			});

			if (!this.t.hasForm()) {
				this.t.getJTable().addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(final MouseEvent e) {
						if (e.getClickCount() == 2) {
							e.consume();
							if (MultipleReferenceDataField.this.t.getSelectedRow() >= 0) {
								MultipleReferenceDataField.this.okButton.doClick(10);
							}
						}
					}
				});
				this.t.getJTable().addKeyListener(new KeyAdapter() {

					@Override
					public void keyTyped(final KeyEvent e) {

					}

					@Override
					public void keyPressed(final KeyEvent e) {
					}

					@Override
					public void keyReleased(final KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							if ((MultipleReferenceDataField.this.okButton != null)
									&& MultipleReferenceDataField.this.okButton.isEnabled()) {
								MultipleReferenceDataField.this.okButton.doClick(10);
							}
						}
					}
				});
			}
			if (ApplicationManager.DEBUG_TIMES) {
				MultipleReferenceDataField.logger
				.debug("Time to create the table: " + (System.currentTimeMillis() - tIni));
			}
		} catch (final Exception e) {
			MultipleReferenceDataField.logger.error("Error creating table.", e);
		}

	}

	@Override
	protected void createComponent() {
		this.dataField = new TextFieldDataField.EJTextField() {

			@Override
			public boolean isFocusTraversable() {
				return false;
			}

			@Override
			public void setDocument(final Document doc) {
				try {
					final Document docAnt = this.getDocument();
					if ((MultipleReferenceDataField.this.innerListener != null) && (docAnt != null)) {
						docAnt.removeDocumentListener((DocumentListener) MultipleReferenceDataField.this.innerListener);
					}
				} catch (final Exception e) {
					MultipleReferenceDataField.logger.trace(null, e);
				}
				super.setDocument(doc);
				try {
					MultipleReferenceDataField.this.installInnerListener();
				} catch (final Exception e) {
					MultipleReferenceDataField.logger.trace(null, e);
				}
			}
		};

		((JTextField) this.dataField).setEditable(false);
		((JTextField) this.dataField).setDisabledTextColor(Color.darkGray);
		this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
		this.dataField.setForeground(Color.darkGray);

		if (this.dataField instanceof JTextField) {
			((JTextField) this.dataField).setColumns(this.fieldSize);
		}
		this.dataField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(final MouseEvent e) {
				if (MultipleReferenceDataField.this.isEnabled() && (MultipleReferenceDataField.this.t != null)
						&& (MultipleReferenceDataField.this.t.getFormName() != null)) {
					MultipleReferenceDataField.this.dataField.setCursor(ApplicationManager.getDetailsCursor());
				}
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				MultipleReferenceDataField.this.dataField.setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (MultipleReferenceDataField.this.isEnabled() && (e.getClickCount() == 2)) {
					e.consume();
					if (MultipleReferenceDataField.this.isEmpty()
							|| (MultipleReferenceDataField.this.t.getFormName() == null)) {
						MultipleReferenceDataField.this.queryListener.actionPerformed(null);
					} else {
						// If the table has a detail form then open it with
						// current
						// data
						if (MultipleReferenceDataField.this.t != null) {
							if (MultipleReferenceDataField.this.t.getFormName() != null) {
								try {
									MultipleReferenceDataField.this.populateTable();
									if ((MultipleReferenceDataField.this.value != null)
											&& (MultipleReferenceDataField.this.value instanceof MultipleValue)) {
										final Map h = new Hashtable();
										final Enumeration enu = ((MultipleValue) MultipleReferenceDataField.this.value)
												.keys();
										while (enu.hasMoreElements()) {
											final Object c = enu.nextElement();
											h.put(c, ((MultipleValue) MultipleReferenceDataField.this.value).get(c));
										}
										final int iRow = MultipleReferenceDataField.this.t.getRowForKeys(h);
										if (iRow >= 0) {
											MultipleReferenceDataField.this.t.openDetailForm(iRow);
										} else {
											MultipleReferenceDataField.this.parentForm.message(
													"MultipleRecordDataField.SpecifiedRecordNotFound",
													Form.ERROR_MESSAGE);
										}
									}
								} catch (final Exception ex) {
									MultipleReferenceDataField.logger.error(null, ex);
									MultipleReferenceDataField.this.parentForm.message(ex.getMessage(),
											Form.ERROR_MESSAGE, ex);
								}
							}
						}
					}

				}
			}
		});
	}

	/**
	 * Creates a document inner listener.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class DocumentInnerListener implements InnerListener, DocumentListener {

		/**
		 * The inner listener enabled condition. By default, true.
		 */
		protected boolean innerListenerEnabled = true;

		/**
		 * The inner value. By default, null.
		 */
		protected Object innerValue = null;

		@Override
		public void setInnerListenerEnabled(final boolean act) {
			this.innerListenerEnabled = act;
		}

		@Override
		public Object getInnerValue() {
			return this.innerValue;
		}

		@Override
		public void setInnerValue(final Object o) {
			this.innerValue = o;
		}

		/**
		 * Gets the field value.
		 * <p>
		 *
		 * @see AbstractMultipleReferenceDataField#getValue()
		 * @return the value
		 */
		protected Object getFieldValue() {
			return MultipleReferenceDataField.this.getValue();
		}

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getFieldValue();
				MultipleReferenceDataField.this.fireValueChanged(oNewValue, this.getInnerValue(),
						ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (this.innerListenerEnabled) {
				final Object oNewValue = this.getFieldValue();
				MultipleReferenceDataField.this.fireValueChanged(oNewValue, this.getInnerValue(),
						ValueEvent.USER_CHANGE);
				this.setInnerValue(oNewValue);
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
		}

	}

	/**
	 * Installs an inner listener for field.
	 */
	protected void installInnerListener() {
		if (this.dataField != null) {
			final Document d = ((JTextField) this.dataField).getDocument();
			if (d != null) {
				if (this.innerListener == null) {
					this.innerListener = new DocumentInnerListener();
				}
				d.addDocumentListener((DocumentListener) this.innerListener);
			}
		}
	}

	/**
	 * Creates additional components like magnifying glass button, delete button and search button.
	 */
	protected void createAdditionalComponents() {
		final ImageIcon buttonIcon = ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS);
		if (buttonIcon == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MultipleReferenceDataField.logger.debug("magnifyingglass.png icon not found");
			}
		} else {
			this.searchButton.setIcon(buttonIcon);
		}
		final ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.DELETE);
		if (deleteIcon != null) {
			this.deleteButton.setIcon(deleteIcon);
		} else {
			this.deleteButton.setText("..");
		}
		this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
		this.searchButton.setMargin(new Insets(0, 0, 0, 0));
		this.searchButton.setToolTipText(ApplicationManager.getTranslation("datafield.select", this.resources));
		this.deleteButton.setToolTipText(ApplicationManager.getTranslation("LimpiarCampo", this.resources));
		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent evento) {
				// Delete data field
				MultipleReferenceDataField.this.deleteUserData();
			}
		});
		// Process button click
		this.searchButton.addActionListener(this.queryListener);

		super.add(this.searchButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		super.add(this.deleteButton,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0, GridBagConstraints.CENTER,
						GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}
	}

	/**
	 * Fills the table.
	 * <p>
	 * @throws Exception when an Exception occurs
	 */
	protected void populateTable() throws Exception {
		Map hData = this.dataCache;
		if (this.cacheTime != 0) {
			if (!this.dataCacheInitialized) {
				try {
					this.initCache();
					hData = this.dataCache;
				} catch (final Exception e) {
					MultipleReferenceDataField.logger.trace(null, e);
					this.dataCache = null;
					this.deleteData();
					return;
				}
			}
		} else {
			final Map hKeysValues = new Hashtable();
			if (this.parentCods != null) {
				for (int i = 0; i < this.parentCods.size(); i++) {
					final Object oParentKey = this.parentkeys.get(i);
					final Object oParentKeyValue = this.parentForm.getDataFieldValue(oParentKey.toString());
					if (ApplicationManager.DEBUG) {
						MultipleReferenceDataField.logger
						.debug("Filtering by parent key: " + oParentKey + " with value: " + oParentKeyValue);
					}
					if (oParentKeyValue != null) {
						hKeysValues.put(this.parentCods.get(i), oParentKeyValue);
					}
				}
			}
			final EntityResult result = this.locator.getEntityReference(this.entity)
					.query(hKeysValues, this.t.getAttributeList(), this.locator.getSessionId());
			if (result.getCode() == EntityResult.OPERATION_WRONG) {
				if (this.parentForm != null) {
					MultipleReferenceDataField.this.parentForm.message(result.getMessage(), Form.ERROR_MESSAGE);
				}
				return;
			} else {
				ConnectionManager.checkEntityResult(result, this.locator);
			}
			hData = EntityResultUtils.toMap(result);
		}
		// Now show the window and set the table value
		this.t.setValue(hData);
	}

	/**
	 * This class implements a table window in a dialog.
	 * <p>
	 *
	 * @author Imatia Innovation.
	 */
	protected class TableWindow extends EJDialog {

		/**
		 * An auxiliary text field for codes. By default, null.
		 */
		protected JTextField auxCodeField = null;

		/**
		 * An auxiliary code label. By default, null.
		 */
		protected JLabel auxCodeLabel = null;

		/**
		 * A search button reference. By default, null.
		 */
		protected JButton searchButton = null;

		/**
		 * An instance of a panel with <code>FlowLayout</code> alignment fixed to LEFT.
		 */
		protected JPanel codeFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		/**
		 * Class constructor for table window in a frame.
		 * <p>
		 * @param f the frame
		 */
		public TableWindow(final Frame f) {
			super(f, true);
			this.init();
		}

		/**
		 * Class constructor for table window in a dialog.
		 * <p>
		 * @param d the dialog
		 */
		public TableWindow(final Dialog d) {
			super(d, true);
			this.init();
		}

		/**
		 * Initializes the table window, buttons and listeners for components.
		 */
		protected void init() {
			this.setSizePositionPreference(MultipleReferenceDataField.this.getSearchDialogSizePreferenceKey());
			try {
				if ((MultipleReferenceDataField.this.windowTitle != null)
						&& (MultipleReferenceDataField.this.resources != null)) {
					this.setTitle(MultipleReferenceDataField.this.resources
							.getString(MultipleReferenceDataField.this.windowTitle));
				} else if (MultipleReferenceDataField.this.windowTitle != null) {
					this.setTitle(MultipleReferenceDataField.this.windowTitle);
				}
			} catch (final Exception e) {
				if (MultipleReferenceDataField.this.windowTitle != null) {
					this.setTitle(MultipleReferenceDataField.this.windowTitle);
				}
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					MultipleReferenceDataField.logger.debug(null, e);
				} else {
					MultipleReferenceDataField.logger.trace(null, e);
				}
			}

			final JPanel southPanel = new JPanel();
			MultipleReferenceDataField.this.okButton = new FieldButton("datafield.select");
			MultipleReferenceDataField.this.okButton.setEnabled(false);
			final ImageIcon okIcon = ImageManager.getIcon(ImageManager.OK);
			if (okIcon != null) {
				MultipleReferenceDataField.this.okButton.setIcon(okIcon);
			}

			southPanel.add(MultipleReferenceDataField.this.okButton);
			if (MultipleReferenceDataField.this.cacheTime > 0) {
				MultipleReferenceDataField.this.refreshCacheButton = new FieldButton("application.update");

				final ImageIcon refreshIcon = ImageManager.getIcon(ImageManager.REFRESH);
				if (refreshIcon != null) {
					MultipleReferenceDataField.this.refreshCacheButton.setIcon(refreshIcon);
				}
				southPanel.add(MultipleReferenceDataField.this.refreshCacheButton);
			}

			if (this.codeFieldPanel != null) {
				this.getContentPane().add(this.codeFieldPanel, BorderLayout.NORTH);
			}

			if (MultipleReferenceDataField.this.okButton != null) {
				MultipleReferenceDataField.this.okButton.setText(ApplicationManager.getTranslation("datafield.select",
						MultipleReferenceDataField.this.resources));
			}
			if (MultipleReferenceDataField.this.refreshCacheButton != null) {
				MultipleReferenceDataField.this.refreshCacheButton.setText(ApplicationManager
						.getTranslation("application.update", MultipleReferenceDataField.this.resources));
			}

			this.getContentPane().add(southPanel, BorderLayout.SOUTH);
			this.getContentPane().add(MultipleReferenceDataField.this.t);
			if (MultipleReferenceDataField.this.refreshCacheButton != null) {
				MultipleReferenceDataField.this.refreshCacheButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						MultipleReferenceDataField.this.invalidateCache();
						MultipleReferenceDataField.this.t.setValue(MultipleReferenceDataField.this.dataCache);
						final Object oValue = MultipleReferenceDataField.this.getValue();
						if ((oValue != null) && (oValue instanceof Map)) {
							final int row = MultipleReferenceDataField.this.t.getRowForKeys((Map) oValue);
							if (row >= 0) {
								MultipleReferenceDataField.this.t.setSelectedRow(row);
							}
						}
					}
				});
			}

			MultipleReferenceDataField.this.okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (MultipleReferenceDataField.this.t.getSelectedRow() >= 0) {
						if (MultipleReferenceDataField.this.cacheTime > 0) {
							final Map hFieldValue = MultipleReferenceDataField.this.t
									.getRowData(MultipleReferenceDataField.this.t.getSelectedRow());
							final Object cod = new MultipleValue(hFieldValue);
							MultipleReferenceDataField.this.setCode(cod, ValueEvent.USER_CHANGE);
							MultipleReferenceDataField.this.tableWindow.setVisible(false);
						} else {// There is not cache
							final Map hFieldValue = MultipleReferenceDataField.this.t
									.getRowData(MultipleReferenceDataField.this.t.getSelectedRow());

							MultipleReferenceDataField.this.valueEventDisabled = true;
							final Object oPreviousSavedValue = MultipleReferenceDataField.this.getValue();
							try {
								final Object v = new MultipleValue(hFieldValue);
								MultipleReferenceDataField.this.setValue(v);
							} catch (final Exception ex) {
								MultipleReferenceDataField.logger.trace(null, ex);
							}
							MultipleReferenceDataField.this.valueSave = oPreviousSavedValue;
							MultipleReferenceDataField.this.valueEventDisabled = false;
							MultipleReferenceDataField.this.fireValueChanged(MultipleReferenceDataField.this.getValue(),
									MultipleReferenceDataField.this.valueSave,
									ValueEvent.USER_CHANGE);
							MultipleReferenceDataField.this.tableWindow.setVisible(false);
						}
					}
				}
			});
			MultipleReferenceDataField.this.t.packTable();
			final Object oValue = MultipleReferenceDataField.this.getValue();
			MultipleReferenceDataField.this.t.setSelectedRow(-1);

			this.pack();
			if (this.getWidth() < MultipleReferenceDataField.TABLE_WINDOW_MINIMUM_WIDTH) {
				this.setSize(MultipleReferenceDataField.TABLE_WINDOW_MINIMUM_WIDTH, this.getHeight());
			} else if ((this.getWidth() + 40) > Toolkit.getDefaultToolkit().getScreenSize().width) {
				this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 40, this.getHeight());
			}
			if ((oValue != null) && (oValue instanceof Map)) {
				final int row = MultipleReferenceDataField.this.t.getRowForKeys((Map) oValue);
				if (row >= 0) {
					MultipleReferenceDataField.this.t.setSelectedRow(row);
				}
			}
		}

	}

	/**
	 * An instance of query listener.
	 */
	protected ActionListener queryListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent event) {
			final Cursor cursor = MultipleReferenceDataField.this.getCursor();
			// Window o =
			// SwingUtilities.getWindowAncestor(MultipleReferenceDataField.this);
			try {
				// w.show(true);
				MultipleReferenceDataField.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				MultipleReferenceDataField.this.populateTable();
				if (MultipleReferenceDataField.this.tableWindow == null) {
					final Window win = SwingUtilities.getWindowAncestor(MultipleReferenceDataField.this);
					if (win instanceof Frame) {
						MultipleReferenceDataField.this.tableWindow = new TableWindow((Frame) win);
					} else if (win instanceof Dialog) {
						MultipleReferenceDataField.this.tableWindow = new TableWindow((Dialog) win);
					} else {
						MultipleReferenceDataField.this.tableWindow = new TableWindow((Frame) win);
					}

					// w.hide();

					if ((MultipleReferenceDataField.this.windowTitle != null)
							&& (MultipleReferenceDataField.this.resources != null)) {
						MultipleReferenceDataField.this.tableWindow
						.setTitle(ApplicationManager.getTranslation(MultipleReferenceDataField.this.windowTitle,
								MultipleReferenceDataField.this.resources));
					} else if (MultipleReferenceDataField.this.windowTitle != null) {
						MultipleReferenceDataField.this.tableWindow
						.setTitle(MultipleReferenceDataField.this.windowTitle);
					}
					if (MultipleReferenceDataField.this.tableWindow.auxCodeLabel != null) {
						MultipleReferenceDataField.this.tableWindow.auxCodeLabel
						.setText(ApplicationManager.getTranslation(MultipleReferenceDataField.auxCodeLabelKey,
								MultipleReferenceDataField.this.resources));
					}

					MultipleReferenceDataField.this.tableWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					ApplicationManager.center(MultipleReferenceDataField.this.tableWindow);
					MultipleReferenceDataField.this.tableWindow.setVisible(true);
				} else {
					if (MultipleReferenceDataField.this.okButton != null) {
						MultipleReferenceDataField.this.okButton.setText(ApplicationManager
								.getTranslation("datafield.select", MultipleReferenceDataField.this.resources));
					}
					if (MultipleReferenceDataField.this.refreshCacheButton != null) {
						MultipleReferenceDataField.this.refreshCacheButton
						.setText(ApplicationManager.getTranslation("application.update",
								MultipleReferenceDataField.this.resources));
					}
					MultipleReferenceDataField.this.t.setSelectedRow(-1);
					final Object oValue = MultipleReferenceDataField.this.getValue();
					if ((oValue != null) && (oValue instanceof Map)) {
						final int row = MultipleReferenceDataField.this.t.getRowForKeys((Map) oValue);
						if (row >= 0) {
							MultipleReferenceDataField.this.t.setSelectedRow(row);
						}
					}

					if ((MultipleReferenceDataField.this.windowTitle != null)
							&& (MultipleReferenceDataField.this.resources != null)) {
						MultipleReferenceDataField.this.tableWindow
						.setTitle(ApplicationManager.getTranslation(MultipleReferenceDataField.this.windowTitle,
								MultipleReferenceDataField.this.resources));
					} else if (MultipleReferenceDataField.this.windowTitle != null) {
						MultipleReferenceDataField.this.tableWindow
						.setTitle(MultipleReferenceDataField.this.windowTitle);
					}
					if (MultipleReferenceDataField.this.tableWindow.auxCodeLabel != null) {
						MultipleReferenceDataField.this.tableWindow.auxCodeLabel
						.setText(ApplicationManager.getTranslation(MultipleReferenceDataField.auxCodeLabelKey,
								MultipleReferenceDataField.this.resources));
					}
					// w.hide();
					MultipleReferenceDataField.this.tableWindow.setVisible(true);
				}
			} catch (final Exception e) {
				MultipleReferenceDataField.this.parentForm.message("interactionmanager.error_in_query",
						Form.ERROR_MESSAGE, e);
				MultipleReferenceDataField.logger.error("Error in query. Results can not be shown", e);
			} finally {
				MultipleReferenceDataField.this.setCursor(cursor);
			}
		}
	};

	/**
	 * Gets the size preference key for search dialog.
	 * <p>
	 * @return the size preferences
	 */
	public String getSearchDialogSizePreferenceKey() {
		final Form f = this.parentForm;
		return f != null ? ReferenceExtDataField.SEARCH_DIALOG_SIZE_POSITION + "_" + f
				.getArchiveName() + "_" + this.attribute
				: ReferenceExtDataField.SEARCH_DIALOG_SIZE_POSITION + "_" + this.attribute;
	}

	@Override
	public void setFormatValue(final Object value) {
		if (value == null) {
			((JTextField) this.dataField).setText("");
		} else if (value instanceof MultipleValue) {
			final String t = this.getDescription(value);
			((JTextField) this.dataField).setText(t);
		}
	}

	@Override
	public void setParentForm(final Form parentForm) {
		this.parentForm = parentForm;
		this.t.setParentForm(this.parentForm);
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);
		if (this.t != null) {
			this.t.setResourceBundle(resources);
		}
		try {
			if (this.tableWindow != null) {
				if ((this.windowTitle != null) && (resources != null)) {
					this.tableWindow.setTitle(resources.getString(this.windowTitle));
				} else if (this.windowTitle != null) {
					this.tableWindow.setTitle(this.windowTitle);
				}
				if (this.tableWindow.auxCodeLabel != null) {
					this.tableWindow.auxCodeLabel.setText(
							ApplicationManager.getTranslation(MultipleReferenceDataField.auxCodeLabelKey, resources));
				}
			}
		} catch (final Exception e) {
			if (this.windowTitle != null) {
				this.tableWindow.setTitle(this.windowTitle);
			}
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				MultipleReferenceDataField.logger.debug(null, e);
			} else {
				MultipleReferenceDataField.logger.trace(null, e);
			}
		}

		this.searchButton.setToolTipText(ApplicationManager.getTranslation("datafield.select", resources));
		this.deleteButton.setToolTipText(ApplicationManager.getTranslation("datafield.reset_field", resources));
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (enabled) {
			final boolean permission = this.checkEnabledPermission();
			if (!permission) {
				this.setEnabled(false);
				return;
			}
		}
		super.setEnabled(enabled);
		this.dataField.setEnabled(enabled);
		this.searchButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);

		/**
		 * Enable or disable the field to data input
		 */
		if ((this.visibleCods != null) && (this.visibleCods.size() > 0)) {
			for (int j = 0; j < this.visibleCods.size(); j++) {
				final Object c = this.visibleCods.get(j);
				final Component comp = this.jVisibleCods.get(c);
				comp.setEnabled(enabled);
				if (!enabled) {
					comp.setBackground(DataComponent.VERY_LIGHT_GRAY);
					comp.setForeground(this.fontColor);
				} else {
					if (this.required) {
						comp.setBackground(DataField.requiredFieldBackgroundColor);
					} else {
						comp.setBackground(this.backgroundColor);
					}
					comp.setForeground(this.fontColor);
				}
			}

		}
		this.searchButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);

		this.enabled = enabled;
		if (!enabled) {
			this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
		} else {
			if (this.required) {
				this.dataField.setBackground(DataField.requiredFieldBackgroundColor);
			} else {
				this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
			}
		}
	}

	public JButton getSearchButton() {
		return this.searchButton;
	}

	public JButton getDeleteButton() {
		return this.deleteButton;
	}

}
