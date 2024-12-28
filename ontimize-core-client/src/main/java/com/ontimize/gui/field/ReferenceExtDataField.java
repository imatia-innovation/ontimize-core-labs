package com.ontimize.gui.field;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.builder.FormBuilder;
import com.ontimize.cache.CacheManager;
import com.ontimize.cache.CacheManager.DataCacheId;
import com.ontimize.cache.CachedComponent;
import com.ontimize.db.EntityResultUtils;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.CreateForms;
import com.ontimize.gui.DataRecordEvent;
import com.ontimize.gui.DataRecordListener;
import com.ontimize.gui.Form;
import com.ontimize.gui.Freeable;
import com.ontimize.gui.FreeableUtils;
import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.document.DateDocument;
import com.ontimize.gui.field.document.MaskDocument;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.Table.QuickFieldText;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.gui.ConnectionManager;
import com.ontimize.jee.common.gui.SearchValue;
import com.ontimize.jee.common.gui.field.ReferenceFieldAttribute;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.tools.Pair;
import com.ontimize.jee.common.util.ParseTools;
import com.ontimize.util.FormatPattern;
import com.ontimize.util.ObjectTools;
import com.ontimize.util.ParseUtils;
import com.ontimize.util.swing.RotatedLabel;
import com.ontimize.util.templates.ITemplateField;
import com.ontimize.xml.DefaultXMLParametersManager;

/**
 * The main class to extend the <code>ReferenceDataField</CODE> functionalities.
 *
 * @see ReferenceDataField
 * @author Imatia Innovation
 */

public class ReferenceExtDataField extends TextFieldDataField implements OpenDialog, Internationalization, Freeable,
CreateForms, CachedComponent, IFilterElement, ReferenceDataComponent, ITemplateField {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceExtDataField.class);

	/**
	 * The name of class. Used by L&F to put UI properties.
	 *
	 * @since 5.2074EN
	 */
	public static final String REFERENCEEXT_NAME = "TextField.ReferenceExt";

	public static final String REFERENCEEXT_CODE_NAME = "TextField.ReferenceExtCode";

	/**
	 * The search_dialog_size_position property.
	 */
	public static final String SEARCH_DIALOG_SIZE_POSITION = "search_dialog_size_position";

	protected static final String RECORD_NOT_FOUND_ERROR_MESSAGE = "referencedatafield.record_not_found_with_specified_keys_message";

	/**
	 * The results_dialog_size_position property.
	 */
	public static final String RESULTS_DIALOG_SIZE_POSITION = "results_dialog_size_position";

	public static final String PARENTKEY_LISTENER = "parentkeylistener";

	public static final String DISABLE_ON_PARENTKEY_NULL = "disableonparentkeynull";

	public static final String CLEAR_QUICK_FILTER = "clearquickfilter";

	/**
	 * Key used for xml parameter 'parentkeylistenerevent'
	 *
	 * @since Ontimize 5.2068-06EN
	 */
	public static final String PARENTKEY_LISTENER_EVENT = "parentkeylistenerevent";

	public static final String PARENTKEY_LISTENER_EVENT_ALL = "all";

	public static final String PARENTKEY_LISTENER_EVENT_USER = "user";

	public static final String PARENTKEY_LISTENER_EVENT_PROGRAMMATIC = "programmatic";

	/**
	 * Key used for xml parameter 'ignorenullonsetvalueset'
	 *
	 * @since Ontimize 5.2068-06EN
	 */
	public static final String IGNORE_NULL_ONSETVALUESET = "ignorenullonsetvalueset";

	public static boolean defaultIgnoreNullOnSetValueSet = false;

	/**
	 * Format pattern string key to convert the data field content value.
	 *
	 * @see ReferenceExtDataField#DATEFORMAT
	 * @since Ontimize 5.2059EN
	 */
	public static final String FORMAT = "format";

	/**
	 * Key used for xml parameter 'noresultclearcode'
	 *
	 * @since Ontimize 5.20568EN
	 */
	public static final String NO_RESULT_CLEAR_CODE = "noresultclearcode";

	/**
	 * Date format pattern string key to convert the data field dates values.
	 *
	 * @see ReferenceExtDataField#FORMAT
	 * @since Ontimize 5.2059EN
	 */
	public static final String DATEFORMAT = "dateformat";

	/**
	 * @deprecated use {@link ParseTools#BIG_DECIMAL}
	 */
	@Deprecated
	public static final String BIG_DECIMAL = ParseTools.BIG_DECIMAL;

	/**
	 * @deprecated use {@link ParseTools#BIG_INTEGER}
	 */
	@Deprecated
	public static final String BIG_INTEGER = ParseTools.BIG_INTEGER;

	/**
	 * @deprecated use {@link ParseTools#DOUBLE}
	 */
	@Deprecated
	public static final String DOUBLE = ParseTools.DOUBLE;

	/**
	 * @deprecated use {@link ParseTools#LONG}
	 */
	@Deprecated
	public static final String LONG = ParseTools.LONG;

	/**
	 * @deprecated use {@link ParseTools#SHORT}
	 */
	@Deprecated
	public static final String SHORT = ParseTools.SHORT;

	/**
	 * @deprecated use {@link ParseTools#INTEGER}
	 */
	@Deprecated
	public static final String INTEGER = ParseTools.INTEGER;

	/**
	 * @deprecated use {@link ParseTools#BIG_DECIMAL_}
	 */
	@Deprecated
	public static final int BIG_DECIMAL_ = ParseTools.BIG_DECIMAL_;

	/**
	 * @deprecated use {@link ParseTools#BIG_INTEGER_}
	 */
	@Deprecated
	public static final int BIG_INTEGER_ = ParseTools.BIG_INTEGER_;

	/**
	 * @deprecated use {@link ParseTools#DOUBLE_}
	 */
	@Deprecated
	public static final int DOUBLE_ = ParseTools.DOUBLE_;

	/**
	 * @deprecated use {@link ParseTools#LONG_}
	 */
	@Deprecated
	public static final int LONG_ = ParseTools.LONG_;

	/**
	 * @deprecated use {@link ParseTools#INTEGER_}
	 */
	@Deprecated
	public static final int INTEGER_ = ParseTools.INTEGER_;

	/**
	 * @deprecated use {@link ParseTools#SHORT_}
	 */
	@Deprecated
	public static final int SHORT_ = ParseTools.SHORT_;

	private static final int MIN_WIDTH_TABLE_WINDOW = 300;

	public static boolean defaultParentkeyListener = false;

	public static boolean defaultDisableOnParentkeyNull = true;

	public static String defaultParentkeyListenerEvent = ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_ALL;

	/**
	 * The search key.
	 */
	protected static String auxCodeLabelKey = "search";

	/**
	 * The result selection key.
	 */
	protected static String ResultSelectionInfoKey = "datafield.select_a_result";

	/**
	 * True when DataRecordListener is registered in the detail form
	 */
	protected boolean dataRecordListenerReady = false;

	/**
	 * Indicates whether this field has registered a listener for each parentkey
	 * fields to reset it when one of the values of these parentkeys change (when
	 * parentkey field that changes is null it is not reset).
	 *
	 * @since 5.2057EN-1.4
	 */
	protected boolean parentkeyListener = ReferenceExtDataField.defaultParentkeyListener;

	/**
	 * Indicates whether this field has disabled when parentkey field is null.
	 *
	 * @since 5.2057EN-1.4
	 */
	protected boolean disableonparentkeynull = ReferenceExtDataField.defaultDisableOnParentkeyNull;

	/**
	 * Indicates the type of event that is taken into consideration on parentkey
	 * listener changes.
	 *
	 * @since 5.2068EN-0.6
	 */
	protected String parentkeyListenerEvent = ReferenceExtDataField.defaultParentkeyListenerEvent;

	/**
	 * The List with attributes to update when data field value changed. By
	 * default, null.
	 */
	protected List onsetvaluesetAttributes = null;

	/**
	 * Indicates if fields contained into 'onsetvalueset' have to be deleted on null
	 * value of the field.
	 *
	 * @since 5.2068EN-0.6
	 */
	protected boolean ignorenullonsetvalueset = ReferenceExtDataField.defaultIgnoreNullOnSetValueSet;

	/**
	 * The description value condition. By default, false.
	 */
	protected boolean descriptionValue = false;

	/**
	 * The integer value condition. By default, false.
	 */
	protected boolean integerValue = false;

	/**
	 * The code. By default, null.
	 */
	protected String code = null;

	/**
	 * The locator. By default, null.
	 */
	protected EntityReferenceLocator locator = null;

	/**
	 * This object is used to store parentkeys and equivalences (for these fields in
	 * entity) when structure of parameter <code>parentkeys</code> is:
	 * "fieldpk1:fieldentitypk1;fieldpk2:fieldentitypk2;...fieldpkn:fieldentitypkn"
	 */
	protected Map hParentkeyEquivalences = new Hashtable();

	/**
	 * This object is used to store onsetvalueset attributes and equivalences (for
	 * these fields in entity) when structure of parameter
	 * <code>onsetvalueset</code> is:
	 * "fieldonset1:fieldentitypk1;fieldonset2:fieldentitypk2;...fieldonsetn:fieldentitypkn"
	 */
	protected Map hOnSetValueSetEquivalences = new Hashtable();

	/**
	 * This object provides a EJTextField with 4 columns.
	 */
	protected JTextField codeField = new EJTextField(4) {

		/**
		 * Sets the text and checks whether codeField is a mask.
		 * <p>
		 *
		 * @param text the text to set.
		 */
		@Override
		public void setText(final String text) {
			final Document d = this.getDocument();
			if (d instanceof MaskDocument) {
				try {
					((MaskDocument) d).setValue(text, true);
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
					super.setText(text);
				}
			} else {
				super.setText(text);
			}
		}

		@Override
		public String getName() {
			return ReferenceExtDataField.REFERENCEEXT_CODE_NAME;
		};


	};

	/**
	 * The Field button for query.
	 */
	protected JButton queryButton = new FieldButton();

	/**
	 * The Field button for delete.
	 */
	protected JButton deleteButton = new FieldButton();

	/**
	 * The parent Frame. By default, null.
	 */
	protected Frame parentFrame = null;

	/**
	 * The parentkeys. By default, null.
	 */
	protected String parentKeys = null;

	/**
	 * The entity name. By default, null.
	 */
	protected String entityName = null;

	/**
	 * The condition of code field visibility. By default, true.
	 */
	protected boolean visibleCodeField = true;

	/**
	 * The condition of code visibility of the code in the detail window. By
	 * default, false.
	 */
	protected boolean visibleCodeSearch = false;

	/**
	 * The condition of field description visibility. By default, true.
	 */
	protected boolean descriptionFieldVisible = true;

	/**
	 * The UserSelection property.
	 */
	public static final String propUserSelection = "UserSelection";

	/**
	 * The iMask key. By default, null.
	 */
	protected String iMask = null;

	/**
	 * The window title key. By default, null.
	 */
	protected String windowTitle = null;

	/**
	 * The others parent keys in a 1x3 Vector.
	 */
	protected List othersParentKey = new Vector(1, 3);

	/**
	 * The parent keys vector.
	 */
	protected List parentkeyList = null;

	/**
	 * The description columns vector. By default, null.
	 */
	protected List descriptionColumns = null;

	protected List<String> queryColumns = null;

	public static boolean defaultDescriptionQuery = true;

	/**
	 * The condition of query description. By default, false.
	 */
	protected boolean descriptionQuery = ReferenceExtDataField.defaultDescriptionQuery;

	protected DocumentListener queryDocumentListener;

	/**
	 * The cache time. By default, {@value Integer#MAX_VALUE}.
	 */
	protected int cacheTime = Integer.MAX_VALUE;

	/**
	 * The last cache time. By default, 0. With this default value, it is checked
	 * the first time that window is opened.
	 */
	protected long lastCacheTime = 0;

	/**
	 * A <code>Hashtable</code> used as a data cache.
	 */
	protected Map dataCache = new Hashtable();

	protected boolean parentkeyCache = CacheManager.defaultParentKeyCache;

	/**
	 * A separator. By default, " ".
	 */
	protected String separator = " ";

	protected boolean clearDetailTableQuickFilterWhenShow = false;

	/**
	 * The condition to exist a user cache manager. By default, true.
	 */
	protected boolean useCacheManager = true;

	/**
	 * The cache manager. By default, null.
	 */
	protected CacheManager cacheManager = null;

	/**
	 * The condition to initialize cache on setvalue method. By default, false.
	 */
	protected boolean initCacheOnSetValue = false;

	/**
	 * The condition to check whether dataCache are initialized. By default, false.
	 */
	protected boolean dataCacheInitialized = false;

	/**
	 * The enable or disable value events, By default, false.
	 */
	protected boolean disabledValueEvents = false;

	/**
	 * The auxiliary attributes. By default, null.
	 */
	protected String attrAux = null;

	/**
	 * The condition about existence of code number. By default, false.
	 */
	protected boolean codeNumber = false;

	/**
	 * The condition about code will be cleared when not matches with existing
	 * value. By default, code will be cleared.
	 *
	 * @since 5.2068EN
	 */
	protected boolean noresultclearcode = true;

	/**
	 * The code number class. By default, it is referred to integer code.
	 */
	protected int codeNumberClass = ParseTools.INTEGER_;

	/**
	 * The code query field key. By default, null.
	 */
	protected String codeQueryField = null;

	/**
	 * The code value. By default, null.
	 */
	protected Object codeValue = null;

	/**
	 * An auxiliary date document. By default, null.
	 */
	protected DateDocument auxDateDoc = null;

	/**
	 * Checks the existence of text in codeField.
	 * <p>
	 *
	 * @return true when codeField is empty
	 */
	@Override
	public boolean isEmpty() {
		if (this.codeField.getText() == null) {
			this.empty = true;
		} else {
			if (this.codeField.getText().length() == 0) {
				this.empty = true;
			} else {
				this.empty = false;
			}
		}
		return this.empty;
	}

	/**
	 * The main class to manage a multiple result window in a Reference Ext
	 * datafield.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected static class MultipleResultWindow {

		/**
		 * The reference to multiple result table. By default, null.
		 */
		protected Table multipleResultTable = null;

		/**
		 * An auxiliary code field. By default, null.
		 */
		protected JTextField auxCodeField = null;

		/**
		 * An auxiliary code label. By default, null.
		 */
		protected JLabel auxCodeLabel = null;

		/**
		 * The reference to query button. By default, null.
		 */
		protected JButton queryButton = null;

		/**
		 * A JPanel reference to code field panel. A default FlowLayout with left
		 * alignment is created.
		 */
		protected JPanel codeFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		/**
		 * A reference for a dialog.
		 */
		protected EJDialog dSelec = null;

		/**
		 * A reference with selection info.
		 */
		protected JLabel lInfo = new JLabel(ReferenceExtDataField.ResultSelectionInfoKey);

		/**
		 * The cod selection value. By default, null.
		 */
		protected Object codSelectionValue = null;

		/**
		 * The ok button.
		 */
		protected JButton okButton = new JButton("application.accept");

		/**
		 * The cancel button.
		 */
		protected JButton cancelBt = new JButton("application.cancel");

		/**
		 * The reference to an entity result. By default, null.
		 */
		protected EntityResult currentEntityResult = null;

		public MultipleResultWindow(final Table t, final ReferenceDataComponent referenceDataComponent,
				final ResourceBundle resources) {
			this.multipleResultTable = t;
			if ((referenceDataComponent.getCodeField() != null) && referenceDataComponent.isCodeFieldVisible()
					&& (referenceDataComponent.getCodeSearchFieldName() != null)
					&& referenceDataComponent.isCodeSearchVisible()) {

				this.auxCodeLabel = new JLabel(ReferenceExtDataField.auxCodeLabelKey);
				this.auxCodeField = new JTextField(referenceDataComponent.getCodeField().getColumns());
				this.queryButton = new FieldButton();
				this.codeFieldPanel.add(this.auxCodeLabel);
				this.codeFieldPanel.add(this.auxCodeField);
				this.codeFieldPanel.add(this.queryButton);
				this.codeFieldPanel.setBorder(BorderFactory.createEtchedBorder());
				this.queryButton.setIcon(ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS));
				this.queryButton.setToolTipText(ApplicationManager.getTranslation("Query", resources));
				this.queryButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						Object v = MultipleResultWindow.this.auxCodeField.getText();
						if ((v == null) || v.equals("")) {
							v = new SearchValue(SearchValue.NULL, null);
						}
						final EntityResult res = referenceDataComponent
								.queryBy(referenceDataComponent.getCodeSearchFieldName(), v);
						if (res.getCode() == EntityResult.OPERATION_WRONG) {
							referenceDataComponent.getParentForm().message(MultipleResultWindow.this.dSelec,
									res.getMessage(), Form.ERROR_MESSAGE);
						} else {
							MultipleResultWindow.this.multipleResultTable.setValue(res);
						}
					}
				});
				this.auxCodeField.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(final KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							if ((MultipleResultWindow.this.queryButton != null)
									&& MultipleResultWindow.this.queryButton.isEnabled()) {
								MultipleResultWindow.this.queryButton.doClick(10);
							}
						}
					}
				});
			}
			// t.setControlsVisible(false);
			this.multipleResultTable.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.multipleResultTable.getJTable().getSelectionModel()
			.addListSelectionListener(new ListSelectionListener() {

				@Override
				public void valueChanged(final ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						if (MultipleResultWindow.this.multipleResultTable.getJTable()
								.getSelectedRowCount() > 0) {
							if (MultipleResultWindow.this.okButton != null) {
								MultipleResultWindow.this.okButton.setEnabled(true);
							}
						} else {
							if (MultipleResultWindow.this.okButton != null) {
								MultipleResultWindow.this.okButton.setEnabled(false);
							}
						}
					}
				}
			});
			this.multipleResultTable.getJTable().addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 2) {
						e.consume();
						if (MultipleResultWindow.this.multipleResultTable.getSelectedRow() >= 0) {
							MultipleResultWindow.this.okButton.doClick(10);
						}
					}
				}
			});

			this.multipleResultTable.getJTable().addKeyListener(new KeyAdapter() {

				@Override
				public void keyTyped(final KeyEvent e) {
				}

				@Override
				public void keyPressed(final KeyEvent e) {
				}

				@Override
				public void keyReleased(final KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						if ((MultipleResultWindow.this.okButton != null)
								&& MultipleResultWindow.this.okButton.isEnabled()) {
							MultipleResultWindow.this.okButton.doClick(10);
						}
					}
				}
			});
		}

		public String getResultDialogSizePreferenceKey(final ReferenceDataComponent datafield) {
			final Form f = datafield.getParentForm();
			return f != null
					? ReferenceExtDataField.RESULTS_DIALOG_SIZE_POSITION + "_" + f.getArchiveName() + "_"
					+ datafield.getAttribute()
					: ReferenceExtDataField.RESULTS_DIALOG_SIZE_POSITION + "_" + datafield.getAttribute();
		}

		/**
		 * Shows the window with result selection.
		 * <p>
		 *
		 * @param res the result to show
		 * @return the codSelectionValue
		 */
		protected Object showResultSelectionWindow(final EntityResult res,
				final ReferenceDataComponent referenceDataField, final ResourceBundle resources) {
			if (this.dSelec == null) {
				final Window w = SwingUtilities.getWindowAncestor(referenceDataField.getCodeField());
				if (w instanceof Dialog) {
					this.dSelec = new EJDialog((Dialog) w, "datafield.multiple_result", true);
				} else {
					this.dSelec = new EJDialog((Frame) w, "datafield.multiple_result", true);
				}

				this.dSelec.setSizePositionPreference(this.getResultDialogSizePreferenceKey(referenceDataField));
				final JPanel jpButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
				jpButtonsPanel.add(this.okButton);
				jpButtonsPanel.add(this.cancelBt);
				this.dSelec.getContentPane().add(jpButtonsPanel, BorderLayout.SOUTH);
				final JPanel jpNorthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
				jpNorthPanel.add(this.lInfo);
				this.dSelec.getContentPane().add(jpNorthPanel, BorderLayout.NORTH);
				if (this.codeFieldPanel != null) {
					jpNorthPanel.add(this.codeFieldPanel, BorderLayout.NORTH);
				}

				this.lInfo.setBorder(new EmptyBorder(5, 3, 4, 15));
				this.lInfo.setFont(this.lInfo.getFont().deriveFont(Font.BOLD));
				this.dSelec.getContentPane().add(this.multipleResultTable);
				this.okButton.setIcon(ImageManager.getIcon(ImageManager.OK));
				this.cancelBt.setIcon(ImageManager.getIcon(ImageManager.CANCEL));
				this.okButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						final int selec = MultipleResultWindow.this.multipleResultTable.getJTable().getSelectedRow();
						if (selec >= 0) {
							MultipleResultWindow.this.codSelectionValue = MultipleResultWindow.this.multipleResultTable
									.getRowData(selec).get(referenceDataField.getCodeFieldName());
							MultipleResultWindow.this.dSelec.setVisible(false);
						}
					}
				});
				this.cancelBt.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						MultipleResultWindow.this.codSelectionValue = null;
						MultipleResultWindow.this.dSelec.setVisible(false);
					}
				});
				this.dSelec.pack();
				this.multipleResultTable.packTable();
				ApplicationManager.center(this.dSelec);
			}
			this.currentEntityResult = res;
			this.multipleResultTable.setValue(res);
			if (this.auxCodeField != null) {
				this.auxCodeField.setText(referenceDataField.getCodeField().getText());
			}
			final Object oValue = referenceDataField.getValue();
			if (oValue != null) {
				final Map hKeysValues = new Hashtable();
				hKeysValues.put(referenceDataField.getCodeFieldName(), oValue);
				final int row = this.multipleResultTable.getRowForKeys(hKeysValues);
				if (row >= 0) {
					this.multipleResultTable.setSelectedRow(row);
				}
			}
			this.dSelec.setTitle(ApplicationManager.getTranslation("datafield.multiple_result", resources));
			this.codSelectionValue = null;
			this.lInfo.setText(
					ApplicationManager.getTranslation(ReferenceExtDataField.ResultSelectionInfoKey, resources));
			this.okButton.setText(ApplicationManager.getTranslation("application.accept", resources));
			this.cancelBt.setText(ApplicationManager.getTranslation("application.cancel", resources));
			this.multipleResultTable.setResourceBundle(resources);
			this.dSelec.setVisible(true);
			return this.codSelectionValue;
		}

	}

	/**
	 * The reference to multiple result window. By default, null.
	 */
	protected MultipleResultWindow multipleResultWindow = null;

	/**
	 * The main class to shows a table in a window.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class TableWindow extends EJDialog {

		/**
		 * The auxiliary code field. By default, null.
		 */
		protected JTextField auxCodeField = null;

		/**
		 * The auxiliary code label. By default, null.
		 */
		protected JLabel auxCodeLabel = null;

		/**
		 * The query button. By default, null.
		 */
		protected JButton queryBt = null;

		/**
		 * A JPanel reference to code field panel. A default FlowLayout wiht left
		 * alignment is created.
		 */
		protected JPanel codeFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		public TableWindow(final Frame f) {
			super(f, true);
			this.init();
		}

		public TableWindow(final Dialog d) {
			super(d, true);
			this.init();
		}

		/**
		 * Sets the size and position preferences and initializes parameters.
		 */
		protected void init() {
			this.setSizePositionPreference(ReferenceExtDataField.this.getSearchDialogSizePreferenceKey());
			try {
				if ((ReferenceExtDataField.this.windowTitle != null)
						&& (ReferenceExtDataField.this.resources != null)) {
					this.setTitle(
							ReferenceExtDataField.this.resources.getString(ReferenceExtDataField.this.windowTitle));
				} else if (ReferenceExtDataField.this.windowTitle != null) {
					this.setTitle(ReferenceExtDataField.this.windowTitle);
				}
			} catch (final Exception e) {
				if (ReferenceExtDataField.this.windowTitle != null) {
					this.setTitle(ReferenceExtDataField.this.windowTitle);
				}
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(null, e);
				} else {
					ReferenceExtDataField.logger.trace(null, e);
				}
			}

			if ((ReferenceExtDataField.this.codeField != null) && ReferenceExtDataField.this.visibleCodeField
					&& (ReferenceExtDataField.this.codeQueryField != null)
					&& ReferenceExtDataField.this.visibleCodeSearch) {

				this.auxCodeLabel = new JLabel(ReferenceExtDataField.auxCodeLabelKey);
				this.auxCodeField = new JTextField(ReferenceExtDataField.this.codeField.getColumns());
				this.queryBt = new FieldButton();
				this.codeFieldPanel.add(this.auxCodeLabel);
				this.codeFieldPanel.add(this.auxCodeField);
				this.codeFieldPanel.add(this.queryBt);
				this.codeFieldPanel.setBorder(BorderFactory.createEtchedBorder());
				this.queryBt.setIcon(ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS));
				this.queryBt.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						Object v = TableWindow.this.auxCodeField.getText();
						if ((v == null) || v.equals("")) {
							v = new SearchValue(SearchValue.NULL, null);
						}
						final EntityResult res = ReferenceExtDataField.this.queryBy(ReferenceExtDataField.this.codeQueryField,
								v);
						if (res.getCode() == EntityResult.OPERATION_WRONG) {
							ReferenceExtDataField.this.parentForm.message(TableWindow.this, res.getMessage(),
									Form.ERROR_MESSAGE);
						} else {
							ReferenceExtDataField.this.t.setValue(res);
						}
					}
				});
				this.auxCodeField.addKeyListener(new KeyAdapter() {

					@Override
					public void keyReleased(final KeyEvent e) {
						if (e.getKeyCode() == KeyEvent.VK_ENTER) {
							if ((TableWindow.this.queryBt != null) && TableWindow.this.queryBt.isEnabled()) {
								TableWindow.this.queryBt.doClick(10);
							}
						}
					}
				});
			}

			final JPanel jpSouthPanel = new JPanel();
			ReferenceExtDataField.this.okButton = new FieldButton("datafield.select") {

				@Override
				public String getName() {
					return "Button";
				};
			};
			ReferenceExtDataField.this.okButton.setEnabled(false);
			final ImageIcon okIcon = ImageManager.getIcon(ImageManager.OK);
			if (okIcon != null) {
				ReferenceExtDataField.this.okButton.setIcon(okIcon);
			}
			jpSouthPanel.add(ReferenceExtDataField.this.okButton);
			if (ReferenceExtDataField.this.cacheTime > 0) {
				ReferenceExtDataField.this.refreshCacheButton = new FieldButton("update") {

					@Override
					public String getName() {
						return "Button";
					};
				};

				final ImageIcon refreshIcon = ImageManager.getIcon(ImageManager.REFRESH);
				if (refreshIcon != null) {
					ReferenceExtDataField.this.refreshCacheButton.setIcon(refreshIcon);
				}
				jpSouthPanel.add(ReferenceExtDataField.this.refreshCacheButton);
			}

			if (this.codeFieldPanel != null) {
				this.getContentPane().add(this.codeFieldPanel, BorderLayout.NORTH);
			}

			if (ReferenceExtDataField.this.okButton != null) {
				ReferenceExtDataField.this.okButton.setText(
						ApplicationManager.getTranslation("datafield.select", ReferenceExtDataField.this.resources));
			}
			if (ReferenceExtDataField.this.refreshCacheButton != null) {
				ReferenceExtDataField.this.refreshCacheButton
				.setText(ApplicationManager.getTranslation("update", ReferenceExtDataField.this.resources));
			}

			this.getContentPane().add(jpSouthPanel, BorderLayout.SOUTH);
			this.getContentPane().add(ReferenceExtDataField.this.t);
			if (ReferenceExtDataField.this.refreshCacheButton != null) {
				ReferenceExtDataField.this.refreshCacheButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						ReferenceExtDataField.this.invalidateCache();
						ReferenceExtDataField.this.t.setValue(ReferenceExtDataField.this.dataCache);
						final Object oValue = ReferenceExtDataField.this.getValue();
						if (oValue != null) {
							final Map hKeysValues = new Hashtable();
							hKeysValues.put(ReferenceExtDataField.this.code, oValue);
							final int row = ReferenceExtDataField.this.t.getRowForKeys(hKeysValues);
							if (row >= 0) {
								ReferenceExtDataField.this.t.setSelectedRow(row);
							}
						}
					}
				});
			}

			ReferenceExtDataField.this.okButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					if (ReferenceExtDataField.this.t.getSelectedRow() >= 0) {
						if (ReferenceExtDataField.this.cacheTime > 0) {
							final Map hFieldValue = ReferenceExtDataField.this.t
									.getRowData(ReferenceExtDataField.this.t.getSelectedRow());
							final Object cod = hFieldValue.get(ReferenceExtDataField.this.code);
							ReferenceExtDataField.this.setCode(cod, ValueEvent.USER_CHANGE);
							ReferenceExtDataField.this.tableWindow.setVisible(false);
						} else {// There is not cache
							final Map hFieldValue = ReferenceExtDataField.this.t
									.getRowData(ReferenceExtDataField.this.t.getSelectedRow());
							// Data format must be an EntityResult
							ReferenceExtDataField.this.disabledValueEvents = true;
							final Object oPreviusSavedValue = ReferenceExtDataField.this.getValue();
							try {
								final Map hValuesList = new Hashtable();
								final Enumeration enumKeys = Collections.enumeration(hFieldValue.keySet());
								while (enumKeys.hasMoreElements()) {
									final Object oKey = enumKeys.nextElement();
									final List v = new Vector();
									v.add(hFieldValue.get(oKey));
									hValuesList.put(oKey, v);
								}
								final List vAttributes = ReferenceExtDataField.this.getAttributes();
								for (int i = 0; i < vAttributes.size(); i++) {
									if (!hValuesList.containsKey(vAttributes.get(i))) {
										final List v = new Vector();
										v.add(null);
										hValuesList.put(vAttributes.get(i), v);
									}
								}
								ReferenceExtDataField.this.setValue(hValuesList);
							} catch (final Exception ex) {
								ReferenceExtDataField.logger.trace(null, ex);
							}
							ReferenceExtDataField.this.valueSave = oPreviusSavedValue;
							ReferenceExtDataField.this.disabledValueEvents = false;
							ReferenceExtDataField.this.fireValueChanged(ReferenceExtDataField.this.getValue(),
									ReferenceExtDataField.this.valueSave, ValueEvent.USER_CHANGE);
							ReferenceExtDataField.this.tableWindow.setVisible(false);
						}
					}
				}
			});
			ReferenceExtDataField.this.t.packTable();
			final Object oValue = ReferenceExtDataField.this.getValue();
			ReferenceExtDataField.this.t.setSelectedRow(-1);

			this.pack();
			if (this.getWidth() < ReferenceExtDataField.MIN_WIDTH_TABLE_WINDOW) {
				this.setSize(ReferenceExtDataField.MIN_WIDTH_TABLE_WINDOW, this.getHeight());
			} else if ((this.getWidth() + 40) > Toolkit.getDefaultToolkit().getScreenSize().width) {
				this.setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 40, this.getHeight());
			}
			if (oValue != null) {
				final Map kv = new Hashtable();
				kv.put(ReferenceExtDataField.this.code, oValue);
				final int row = ReferenceExtDataField.this.t.getRowForKeys(kv);
				if (row >= 0) {
					ReferenceExtDataField.this.t.setSelectedRow(row);
				}
			}
		}

	}

	/**
	 * A reference to table Window. By default, null.
	 */
	protected TableWindow tableWindow = null;

	/**
	 * The ok button. By default, null.
	 */
	protected JButton okButton = null;

	/**
	 * The reference for a refresh cache button. By default, null.
	 */
	protected JButton refreshCacheButton = null;

	/**
	 * The reference for the table. By default, null.
	 */
	protected Table t = null;

	/**
	 * A listener for query operation.
	 */
	protected ActionListener queryListener = new ActionListener() {

		@Override
		public void actionPerformed(final ActionEvent event) {
			// Executes a query
			final Cursor cursor = ReferenceExtDataField.this.getCursor();
			try {
				ReferenceExtDataField.this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				ReferenceExtDataField.this.populateTable();

				if (ReferenceExtDataField.this.tableWindow == null) {
					final Window win = SwingUtilities.getWindowAncestor(ReferenceExtDataField.this);
					if (win instanceof Frame) {
						ReferenceExtDataField.this.tableWindow = new TableWindow((Frame) win);
					} else if (win instanceof Dialog) {
						ReferenceExtDataField.this.tableWindow = new TableWindow((Dialog) win);
					} else {
						ReferenceExtDataField.this.tableWindow = new TableWindow((Frame) win);
					}
					// Maybe we have to update the cache and data on the
					// 'onsetvalueset' fields
					ReferenceExtDataField.this.createDataRecordListener();

					if ((ReferenceExtDataField.this.windowTitle != null)
							&& (ReferenceExtDataField.this.resources != null)) {
						ReferenceExtDataField.this.tableWindow.setTitle(ApplicationManager.getTranslation(
								ReferenceExtDataField.this.windowTitle, ReferenceExtDataField.this.resources));
					} else if (ReferenceExtDataField.this.windowTitle != null) {
						ReferenceExtDataField.this.tableWindow.setTitle(ReferenceExtDataField.this.windowTitle);
					}
					if (ReferenceExtDataField.this.tableWindow.auxCodeLabel != null) {
						ReferenceExtDataField.this.tableWindow.auxCodeLabel.setText(ApplicationManager.getTranslation(
								ReferenceExtDataField.auxCodeLabelKey, ReferenceExtDataField.this.resources));
					}

					ReferenceExtDataField.this.tableWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
					ApplicationManager.center(ReferenceExtDataField.this.tableWindow);
					ReferenceExtDataField.this.tableWindow.setVisible(true);
				} else {
					if (ReferenceExtDataField.this.okButton != null) {
						ReferenceExtDataField.this.okButton.setText(ApplicationManager
								.getTranslation("datafield.select", ReferenceExtDataField.this.resources));
					}
					if (ReferenceExtDataField.this.refreshCacheButton != null) {
						ReferenceExtDataField.this.refreshCacheButton.setText(
								ApplicationManager.getTranslation("update", ReferenceExtDataField.this.resources));
					}
					ReferenceExtDataField.this.t.setSelectedRow(-1);
					final Object oValue = ReferenceExtDataField.this.getValue();
					if (oValue != null) {
						final Map hKeysValues = new Hashtable();
						hKeysValues.put(ReferenceExtDataField.this.code, oValue);
						final int row = ReferenceExtDataField.this.t.getRowForKeys(hKeysValues);
						if (row >= 0) {
							ReferenceExtDataField.this.t.setSelectedRow(row);
						}
					}

					if ((ReferenceExtDataField.this.windowTitle != null)
							&& (ReferenceExtDataField.this.resources != null)) {
						ReferenceExtDataField.this.tableWindow.setTitle(ApplicationManager.getTranslation(
								ReferenceExtDataField.this.windowTitle, ReferenceExtDataField.this.resources));
					} else if (ReferenceExtDataField.this.windowTitle != null) {
						ReferenceExtDataField.this.tableWindow.setTitle(ReferenceExtDataField.this.windowTitle);
					}
					if (ReferenceExtDataField.this.tableWindow.auxCodeLabel != null) {
						ReferenceExtDataField.this.tableWindow.auxCodeLabel.setText(ApplicationManager.getTranslation(
								ReferenceExtDataField.auxCodeLabelKey, ReferenceExtDataField.this.resources));
					}

					ReferenceExtDataField.this.tableWindow.setVisible(true);
				}
			} catch (final Exception e) {
				ReferenceExtDataField.logger.error("Error in query. Results can not be shown", e);
				ReferenceExtDataField.this.parentForm.message("interactionmanager.error_in_query", Form.ERROR_MESSAGE,
						e);
			} finally {
				ReferenceExtDataField.this.setCursor(cursor);
			}
		}
	};

	protected class OpenDetailMouseListener extends MouseAdapter {

		@Override
		public void mouseEntered(final MouseEvent e) {
			if (ReferenceExtDataField.this.isEnabled() && (ReferenceExtDataField.this.t != null)
					&& (ReferenceExtDataField.this.t.getFormName() != null)) {
				ReferenceExtDataField.this.dataField.setCursor(ApplicationManager.getDetailsCursor());
			}
		}

		@Override
		public void mouseExited(final MouseEvent e) {
			ReferenceExtDataField.this.dataField.setCursor(Cursor.getDefaultCursor());
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (ReferenceExtDataField.this.isEnabled() && (e.getClickCount() == 2)) {
				e.consume();
				if (ReferenceExtDataField.this.isEmpty() || (ReferenceExtDataField.this.t.getFormName() == null)) {
					ReferenceExtDataField.this.queryListener.actionPerformed(null);
				} else {
					if (ReferenceExtDataField.this.t != null) {
						if (ReferenceExtDataField.this.t.getFormName() != null) {
							boolean enabledFiltering = true;
							try {
								enabledFiltering = ReferenceExtDataField.this.t.isFilteringEnabled();
								ReferenceExtDataField.this.populateTable();
								ReferenceExtDataField.this.t.enableFiltering(false);
								final Map hKeysValues = new Hashtable();
								hKeysValues.put(ReferenceExtDataField.this.code, ReferenceExtDataField.this.getValue());
								final int row = ReferenceExtDataField.this.t.getRowForKeys(hKeysValues);
								if (row >= 0) {
									ReferenceExtDataField.this.createDataRecordListener();
									ReferenceExtDataField.this.t.openDetailForm(row);
								} else {
									ReferenceExtDataField.this.parentForm.message(
											ReferenceExtDataField.RECORD_NOT_FOUND_ERROR_MESSAGE, Form.ERROR_MESSAGE);
								}
							} catch (final Exception ex) {
								ReferenceExtDataField.logger.trace(null, ex);
								ReferenceExtDataField.this.parentForm.message(ex.getMessage(), Form.ERROR_MESSAGE, ex);
							} finally {
								ReferenceExtDataField.this.t.enableFiltering(enabledFiltering);
							}
						}
					}
				}
			}
		}

	}

	/**
	 * The code field listener to manage the codeChange value in function of events.
	 * <p>
	 *
	 * @author Imatia Innovation
	 */
	protected class CodeFieldListener extends FocusAdapter implements DocumentListener, KeyListener {

		protected boolean codeChange = false;

		protected boolean enabled = true;

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (!this.enabled) {
				return;
			}
			this.codeChange = true;
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (!this.enabled) {
				return;
			}
			this.codeChange = true;
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			if (!this.enabled) {
				return;
			}
			this.codeChange = true;
		}

		@Override
		public void focusLost(final FocusEvent event) {
			if (!event.isTemporary()) {
				if (this.codeChange) {
					this.codeChange = false;
					this.processFocus();
				}
			}
		}

		@Override
		public void keyReleased(final KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				this.codeChange = false;
				e.consume();
				this.processFocus();
			}
		}

		@Override
		public void keyPressed(final KeyEvent e) {
		}

		@Override
		public void keyTyped(final KeyEvent e) {
		}

		/**
		 * Processes focus in function of code field value is deleted or is empty.
		 */
		protected void processFocus() {
			if (ReferenceExtDataField.this.codeField.getText().equals("")
					&& (ReferenceExtDataField.this.codeQueryField == null)) {
				// Code field is empty. There are 2 possibilities, previous
				// value
				// was empty too or not
				final Object currentValue = ReferenceExtDataField.this.getValue();
				if (!ReferenceExtDataField.this.isInnerValueEqual(currentValue)) {
					try {
						ReferenceExtDataField.this.setInnerListenerEnabled(false);
						final Object oldValue = ReferenceExtDataField.this.getInnerValue();
						((JTextField) ReferenceExtDataField.this.dataField).setText("");
						ReferenceExtDataField.this.setInnerValue(currentValue);
						ReferenceExtDataField.this.fireValueChanged(currentValue, oldValue, ValueEvent.USER_CHANGE);
					} finally {
						ReferenceExtDataField.this.setInnerListenerEnabled(true);
					}
				}
			} else if (ReferenceExtDataField.this.codeQueryField != null) {
				Object oCodeValue = null;
				oCodeValue = ReferenceExtDataField.this.getCodeFieldValue();
				if ((oCodeValue == null) || oCodeValue.equals("")) {
					oCodeValue = new SearchValue(SearchValue.NULL, null);
				}
				final EntityResult res = ReferenceExtDataField.this.queryBy(ReferenceExtDataField.this.codeQueryField,
						oCodeValue);
				if (res.getCode() == EntityResult.OPERATION_WRONG) {
					ReferenceExtDataField.this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
				} else {
					if (res.isEmpty()) {
						if (ReferenceExtDataField.this.noresultclearcode) {
							ReferenceExtDataField.this.deleteUserData();
						} else {
							ReferenceExtDataField.this.deleteUserData(false);
						}
					} else if (res.calculateRecordNumber() == 1) {
						final Map hData = res.getRecordValues(0);
						final Object oResultCodeValue = hData.get(ReferenceExtDataField.this.code);
						if (oResultCodeValue == null) {
							ReferenceExtDataField.logger
							.debug("Query result has not data for the specified code value " + hData);
							if (ReferenceExtDataField.this.noresultclearcode) {
								ReferenceExtDataField.this.deleteData();
							} else {
								ReferenceExtDataField.this.deleteData(false);
							}
						} else {
							ReferenceExtDataField.this.setCode(oResultCodeValue, ValueEvent.USER_CHANGE);
						}
					} else {
						// More than one record
						if (ReferenceExtDataField.this.isClearDetailTableQuickFilterWhenShow()) {
							ReferenceExtDataField.this.clearQuickFilter();
						}
						final Object o = ReferenceExtDataField.this.multipleResultWindow.showResultSelectionWindow(res,
								ReferenceExtDataField.this, ReferenceExtDataField.this.resources);
						if (o != null) {
							ReferenceExtDataField.this.setCode(o, ValueEvent.USER_CHANGE);
						} else {
							ReferenceExtDataField.this.setCode(ReferenceExtDataField.this.getValue(),
									ValueEvent.USER_CHANGE);
						}
					}
				}
			} else {
				Object codeValue = null;
				codeValue = ReferenceExtDataField.this.getCodeFieldValue();

				ReferenceExtDataField.this.setCode(codeValue, ValueEvent.USER_CHANGE);
			}
			this.codeChange = false;
		}

		public void setEnabled(final boolean en) {
			this.enabled = en;
		}

	}

	/**
	 * A reference to code field listener.
	 */
	protected CodeFieldListener codeFieldListener = new CodeFieldListener();

	/**
	 * Pattern to format the field contents. Null if the <code>format</code>
	 * parameter is missing. Also wrappers the content of the <code>dateformat
	 * <code> parameter.
	 *
	 * @since Ontimize 5.2059EN
	 */
	protected FormatPattern formatPattern = null;

	public ReferenceExtDataField(final Map parameters) throws Exception {
		this.init(parameters);
		// Checks parentkeys
		if ((!this.parentkeyCache) && (this.getParentKeys() != null) && !this.getParentKeys().isEmpty()) {
			this.useCacheManager = false;
			this.cacheTime = 0;
		}

		// If cache exists then the attribute is cod. In other case it is a
		// ReferenceFieldAttribute
		if (this.cacheTime == 0) {
			if (this.attribute != null) {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(" Attribute is not null: " + this.attribute);
				}
				this.attrAux = this.attribute.toString();
			} else {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(" Attribute is null, then use the code : " + this.code);
				}
				this.attrAux = this.code;
			}
			this.attribute = new ReferenceFieldAttribute(this.attrAux, this.entityName, this.code,
					this.getAttributes());
			this.getLabelComponent().setText(this.attrAux);
		} else {
			if (this.attribute == null) {
				this.attrAux = this.code;
				this.attribute = this.code;
				this.getLabelComponent().setText(this.code);
			} else {
				this.attrAux = this.attribute.toString();
				this.getLabelComponent().setText(this.attrAux);
			}
		}
	}

	protected void updateOnSetValueSetAttributes(final Map data) {
		if ((this.parentForm != null) && (data != null)) {
			for (int i = 0; i < this.onsetvaluesetAttributes.size(); i++) {
				final Object at = this.onsetvaluesetAttributes.get(i);
				final Object oValue = data.get(this.hOnSetValueSetEquivalences.get(at));
				this.parentForm.setDataFieldValue(at, oValue);
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(" Setting field value: " + at + " -> " + oValue);
				}
			}
		}
	}

	protected void createDataRecordListener() {
		if (!this.dataRecordListenerReady) {
			if (this.t.hasForm()) {
				this.t.getDetailForm().getForm().addDataRecordListener(new DataRecordListener() {

					@Override
					public void dataRecordChanged(final DataRecordEvent e) {
						if (e.getType() == DataRecordEvent.UPDATE) {
							// When the detail form values are updated then it
							// is
							// necessary update the cache too
							if ((ReferenceExtDataField.this.onsetvaluesetAttributes != null)
									&& (ReferenceExtDataField.this.onsetvaluesetAttributes.size() > 0)) {
								final Map updateAttributes = e.getAttributesValues();
								final Map updateKeys = e.getKeysValues();
								ReferenceExtDataField.this.updateDataCache(updateAttributes, updateKeys);
								if (ReferenceExtDataField.this.existFieldsToUpdate(updateAttributes, updateKeys)) {
									final Map data = ReferenceExtDataField.this
											.getCodeValues(ReferenceExtDataField.this.getValue());
									ReferenceExtDataField.this.updateOnSetValueSetAttributes(data);
								}
							}
						}
					}
				});
			}
			this.dataRecordListenerReady = true;
		}
	}

	protected boolean existFieldsToUpdate(final Map changes, final Map keys) {
		// Field in 'onsetvalueset' attribute only must be updated if the update
		// record is the selected one
		// One of the modified fields must be one of the 'onsetvalueset' fields
		Object attr = this.getAttribute();
		if (attr instanceof ReferenceFieldAttribute) {
			attr = ((ReferenceFieldAttribute) this.getAttribute()).getAttr();
		}
		if (keys.containsKey(attr)) {
			final Object currentValue = this.getValue();
			if (keys.get(attr).equals(currentValue)) {
				final Enumeration eKeys = Collections.enumeration(changes.keySet());
				while (eKeys.hasMoreElements()) {
					Object key = eKeys.nextElement();
					if (key instanceof ReferenceFieldAttribute) {
						key = ((ReferenceFieldAttribute) key).getAttr();
					}
					if (this.onsetvaluesetAttributes.contains(key)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	protected void updateDataCache(final Map updateAttributes, final Map updateKeys) {
		if ((this.dataCache != null) && !this.dataCache.isEmpty() && (this.dataCache instanceof EntityResult)) {
			final int index = ((EntityResult) this.dataCache).getRecordIndex(updateKeys);
			if (index >= 0) {
				final Enumeration eKeys = Collections.enumeration(updateAttributes.keySet());
				while (eKeys.hasMoreElements()) {
					final Object key = eKeys.nextElement();
					Object values = this.dataCache.get(key);
					if ((values == null) && (key instanceof ReferenceFieldAttribute)) {
						values = this.dataCache.get(((ReferenceFieldAttribute) key).getAttr());
					}
					if ((values != null) && (values instanceof List)) {
						((List) values).set(index, updateAttributes.get(key));
					}
				}
			}
		}
	}

	/**
	 * Initializes parameters.
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 * <tr>
	 * <td>attr</td>
	 * <td></td>
	 * <td>cod</td>
	 * <td>no</td>
	 * <td>The field attribute.</td>
	 * </tr>
	 * <tr>
	 * <td>entity</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Associated entity.</td>
	 * </tr>
	 * <tr>
	 * <td>cod</td>
	 * <td></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The code name associated to field.</td>
	 * </tr>
	 * <tr>
	 * <td>codinteger</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>When code value is an <code>Integer</code></td>
	 * </tr>
	 * <tr>
	 * <td>codnumber</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Specifies if a number object is used.</td>
	 * </tr>
	 * <tr>
	 * <td>codnumberclass</td>
	 * <td><i>BigDecimal/BigInteger/Double/Long/Short/Integer</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Specifies the type number object used. It is only available when
	 * codnumber is setted.</td>
	 * </tr>
	 * <tr>
	 * <td>csize</td>
	 * <td></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The number of characters for code field.</td>
	 * </tr>
	 * <tr>
	 * <td>codvisible</td>
	 * <td><i>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Condition about visibility of cod.</td>
	 * </tr>
	 * <tr>
	 * <td>codsearchvisible</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Condition about visibility of cod search in detail window.</td>
	 * </tr>
	 * <tr>
	 * <td>dateformat</td>
	 * <td><i>A <a href=
	 * "http://java.sun.com/docs/books/tutorial/i18n/format/simpleDateFormat.html"
	 * >Java date pattern<a></i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Java Date pattern to use in <code>format</code> parameter.</td>
	 * </tr>
	 * <tr>
	 * <td>format</td>
	 * <td>message;column_1;...;column_N</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The field shows the translation with the given message and columns.
	 * Example:<br>
	 * -User has a reference field that queries an entity with three fields
	 * ID;INITIALDATE;ENDDATE. ID is the code of field.<br>
	 * -User wants to format description of this field to show: Period starts:
	 * <i>INITIALDATE</i> and ends: <i>ENDDATE</i><br>
	 * -He only needs to store in bundle a key, e.g., refExtKey= Period starts: {0}
	 * and ends: {1} and the 'translation' parameter to 'yes' value<br>
	 * -He must specify in parameter format="refExtKey;INITIALDATE;ENDDATE"</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>translation</td>
	 * <td>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>The format message is translated by bundle</td>
	 * </tr>
	 *
	 * <tr>
	 * <td>mask</td>
	 * <td><i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Specifies an input mask for applying to the code field.</td>
	 * </tr>
	 * <tr>
	 * <td>onsetvalueset</td>
	 * <td><i>fieldonset1:fieldentitypk1;fieldonset2:fieldentitypk2;... fieldonsetn
	 * :fieldentitypkn (since version 5.2057EN-1.4)</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Field attributes whose value will be set when data on field change.</td>
	 * </tr>
	 * <tr>
	 * <td>descriptioncols</td>
	 * <td><i>dcol1;dcol2;...;dcoln</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Visible table columns in text field. By default, it is the parameter
	 * value of visiblecols in table.</td>
	 * </tr>
	 * <tr>
	 * <td>querycols</td>
	 * <td><i>dcol1;dcol2;...;dcoln</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Columns that are added to description for query</td>
	 * </tr>
	 * <tr>
	 * <td>parentkeylistener</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Register a listener for each parentkey field to delete data of this field
	 * when one of the parentkey values changes (when new value of the parentkey
	 * field is null, field value is not reset, excepts if 'disableonparentkeynull'
	 * is true). If you use this parameter then you need to use the setvalueorder
	 * for the Form @see {@link Form#init(Map)}</td>
	 * </tr>
	 * <tr>
	 * <td>disableonparentkeynull</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>Disable field when parentkey is null.</td>
	 * </tr>
	 * <tr>
	 * <td>noresultclearcode</td>
	 * <td><i>yes/no</i> (since version 5.2068EN)</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Indicates whether code field should be reset when query did not return
	 * any match for current code value.</td>
	 * </tr>
	 * </Table>
	 * <p>
	 * Valid Table parameters:
	 * <p>
	 * <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 * <tr>
	 * <td><b>attribute</td>
	 * <td><b>values</td>
	 * <td><b>default</td>
	 * <td><b>required</td>
	 * <td><b>meaning</td>
	 * </tr>
	 * <tr>
	 * <td>key</td>
	 * <td></td>
	 * <td><i>cod</td>
	 * <td>no</td>
	 * <td>The Table key.</td>
	 * </tr>
	 * <tr>
	 * <td>parentkeys</td>
	 * <td><i>fieldpk1:fieldentitypk1;fieldpk2:fieldentitypk2;...fieldpkn
	 * :fieldentitypkn (since version 5.2057EN-1.4)</i></td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>The field that is parentkey and correspondent associated field in entity.
	 * It is accepted to indicate only the fieldpki when it is equal to
	 * fieldentitypki, e.g. : <i>fieldpk1;fieldpk2:fieldentitypk2
	 * ;...fieldpkn:fieldentitypkn</i></td>
	 * </tr>
	 * <tr>
	 * <td>cols</td>
	 * <td><i>cols1;cols2;...;colsn</td>
	 * <td></td>
	 * <td>yes</td>
	 * <td>Columns associated to the code. It forms the description field.</td>
	 * </tr>
	 * <tr>
	 * <td>cachetime</td>
	 * <td></td>
	 * <td>10 minutes</td>
	 * <td>no</td>
	 * <td>The time data remains in cache.</td>
	 * </tr>
	 * <tr>
	 * <td>separator</td>
	 * <td></td>
	 * <td>" "</td>
	 * <td>no</td>
	 * <td>The separator character for columns.</td>
	 * </tr>
	 * <tr>
	 * <td>usecachemanager</td>
	 * <td>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>With "no" parameter, field will have its own cache.</td>
	 * </tr>
	 * <tr>
	 * <td>visiblecols</td>
	 * <td><i>vcols1,vcols2,...,vcolsn</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The visible cols from cols. With empty parameter all columns are
	 * visible.</td>
	 * </tr>
	 * <tr>
	 * <td>form</td>
	 * <td><i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>The form that is opened in detail. On init, update mode will be its
	 * state.</td>
	 * </tr>
	 * <tr>
	 * <td>initcacheonsetvalue</td>
	 * <td><i>yes/no</td>
	 * <td></td>
	 * <td>no</td>
	 * <td>Inits cache when <code>setValue()</code> is called.</td>
	 * </tr>
	 * <tr>
	 * <td>codsearch</td>
	 * <td><i></td>
	 * <td></td>
	 * <td>no</td>
	 * <td>It specifies the field name for searching when user introduces a search
	 * value.</td>
	 * </tr>
	 * <tr>
	 * <td>controls</td>
	 * <td><i>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>Allows the Table to have some control buttons, in the top of it. This
	 * buttons, by default, can export to excel the data in the table, show charts,
	 * reports, and so on. If the value is not, the controlsvisible attribute will
	 * not affect.</td>
	 * </tr>
	 * <tr>
	 * <td>descvisible</td>
	 * <td><i>yes/no</td>
	 * <td>yes</td>
	 * <td>no</td>
	 * <td>The visibility description.</td>
	 * </tr>
	 * <tr>
	 * <td>parentkeylistenerevent</td>
	 * <td><i>user/programmatic</td>
	 * <td>both</td>
	 * <td>no</td>
	 * <td>The type of event that is taken into consideration on parentkey listener
	 * changes. E.g. if 'user' value is set, just the events generated by user
	 * changes are taken into consideration.</td>
	 * </tr>
	 * <tr>
	 * <td>ignorenullonsetvalueset</td>
	 * <td><i>yes/no</td>
	 * <td>no</td>
	 * <td>no</td>
	 * <td>It specifies whether the fields included into 'onsetvalueset' are deleted
	 * when null value is set in this field. If value is true the fields contained
	 * into 'onsetvalueset' are not deleted on null value.</td>
	 * </tr>
	 * </Table>
	 */
	@Override
	public void init(final Map parameters) {
		super.init(parameters);

		this.updateVisibleColsWithColsContent(parameters);

		this.descriptionQuery = ParseUtils.getBoolean((String) parameters.get("descriptionquery"),
				ReferenceExtDataField.defaultDescriptionQuery);

		// Parameter : parentkey
		String sParentKeyEquivalences = this.setParentKeysParameter(parameters);

		sParentKeyEquivalences = this.setOtherkeysParameter(parameters, sParentKeyEquivalences);
		this.hParentkeyEquivalences = ApplicationManager.getTokensAt(sParentKeyEquivalences, ";", ":");

		this.setSeparatorParameter(parameters);

		this.setUserCacheManagerParameter(parameters);

		this.setCodNumberParameter(parameters);

		this.setParentKeyListenerParameter(parameters);

		this.setParentKeyListenerEventParameter(parameters);

		this.setDisableOnParentKeyNull(parameters);

		if (!this.parentkeyListener) {
			this.disableonparentkeynull = false;
		}

		this.setCodNumberClassParameter(parameters);

		this.setInitCacheOnsSetValueParameter(parameters);

		this.setNoResultClearCodeParameter(parameters);

		((JTextField) this.dataField).setEditable(this.descriptionQuery);
		this.checkAndSetUseOfPlaf();

		this.installMouseListener();

		// Parameter: entity
		this.setEntityParameter(parameters);
		this.setTitleParameter(parameters);
		this.setCodeIntegerParameter(parameters);
		this.setCodeParameter(parameters);
		this.setCacheTimeParameter(parameters);

		this.setMaskParameter(parameters);

		this.setCSizeParameter(parameters);

		this.setCodVisibleParameter(parameters);

		this.setCodSearchVisibleParameter(parameters);

		this.setDescVisibleParameter(parameters);

		this.setCodSearchParameter(parameters);

		// Table creation
		Object key = parameters.get("key");
		this.setKeyToTable(parameters, key);
		parameters.put("numrowscolumn", "no");
		key = parameters.get("quickfiltervisible");
		this.setQuickFilterVisibleToTable(parameters, key);

		this.configParameterForTable(parameters);

		this.setCodQueryFieldTable(parameters);

		if (!parameters.containsKey("rows")) {
			parameters.put("rows", "15");
		}
		try {
			final long tIni = System.currentTimeMillis();
			this.createTable(parameters);

			this.configureOnSetValueSet(parameters);

			final Object ignorenullonsetvalueset = parameters.get(ReferenceExtDataField.IGNORE_NULL_ONSETVALUESET);
			this.ignorenullonsetvalueset = ParseUtils.getBoolean((String) ignorenullonsetvalueset,
					ReferenceExtDataField.defaultIgnoreNullOnSetValueSet);

			ReferenceExtDataField.logger.trace(
					this.getClass().getName() + ": Time to create the table: " + (System.currentTimeMillis() - tIni));
		} catch (final Exception e) {
			ReferenceExtDataField.logger.error("Error creating table.", e);
		}
		this.configureCols(parameters);

		this.configureQueryField(parameters);

		this.configureQueryCols(parameters);

		this.configureButtons(parameters);

		final boolean translation = ParseUtils.getBoolean((String) parameters.get(ComboDataField.TRANSLATION), false);

		this.configureOpaque(parameters);
		this.setParentKeyCache(parameters);

		this.configureFormat(parameters, translation);
	}

	@Override
	public void setRequired(final boolean required) {
		super.setRequired(required);
	}

	protected void configureButtons(final Map parameters) {
		// Code field and button for showing results are added.
		super.panel
		.add(this.codeField,
				new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1, GridBagConstraints.EAST,
						GridBagConstraints.NONE, new Insets(DataField.DEFAULT_TOP_MARGIN,
								DataField.DEFAULT_FIELD_LEFT_MARGIN, DataField.DEFAULT_BOTTOM_MARGIN, 0),
						0, 0));

		this.setSearchIconForQueryButton();
		this.setDeleteIconFornDeleteButton();
		this.deleteButton.setMargin(new Insets(0, 0, 0, 0));
		this.queryButton.setMargin(new Insets(0, 0, 0, 0));
		this.queryButton.setToolTipText(ApplicationManager.getTranslation("datafield.select", this.resources));
		this.deleteButton.setToolTipText(ApplicationManager.getTranslation("LimpiarCampo", this.resources));
		this.deleteButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent event) {
				// Data field are deleted.
				ReferenceExtDataField.this.deleteUserData();
			}
		});

		super.add(this.queryButton, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		super.add(this.deleteButton, new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0, 0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		if (this.labelPosition != SwingConstants.LEFT) {
			this.validateComponentPositions();
		}
		// Button click is processed
		this.queryButton.addActionListener(this.queryListener);

		// Added listener for code field. This listener fills automatically the
		// description (((JTextField)dataField)) field when
		// code field loses focus.
		this.codeField.addFocusListener(this.codeFieldListener);

		// Listener for changes in code
		this.codeField.getDocument().addDocumentListener(this.codeFieldListener);
		// Listener for VK_ENTER key
		this.codeField.addKeyListener(this.codeFieldListener);

		this.setBorderParameterForTable(parameters);

		final boolean borderbuttons = ParseUtils.getBoolean((String) parameters.get("borderbuttons"), true);
		final boolean opaquebuttons = ParseUtils.getBoolean((String) parameters.get("opaquebuttons"), true);
		final boolean highlightButtons = ParseUtils.getBoolean((String) parameters.get("highlightbuttons"), false);
		MouseListener listenerHighlightButtons = null;
		if (highlightButtons) {
			listenerHighlightButtons = new MouseAdapter() {

				@Override
				public void mouseEntered(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(true);
					((AbstractButton) e.getSource()).setContentAreaFilled(true);
				}

				@Override
				public void mouseExited(final MouseEvent e) {
					((AbstractButton) e.getSource()).setOpaque(false);
					((AbstractButton) e.getSource()).setContentAreaFilled(false);
				}
			};
		}

		this.changeButton(this.deleteButton, borderbuttons, opaquebuttons, listenerHighlightButtons);
		this.changeButton(this.queryButton, borderbuttons, opaquebuttons, listenerHighlightButtons);

		if (this.codeField != null) {
			this.codeField
			.setFont(ParseUtils.getFont((String) parameters.get(DataField.FONT), this.dataField.getFont()));
		}

		this.clearDetailTableQuickFilterWhenShow = ParseUtils.getBoolean(
				(String) parameters.get(ReferenceExtDataField.CLEAR_QUICK_FILTER),
				this.clearDetailTableQuickFilterWhenShow);
	}

	protected void configureOpaque(final Map parameters) {
		if (parameters.containsKey("opaque")
				&& !ApplicationManager.parseStringValue(parameters.get("opaque").toString())) {
			if (this.codeField != null) {
				this.codeField.setOpaque(false);
			}
		}
	}

	protected void configureFormat(final Map parameters, final boolean translation) {
		final Object oFormat = parameters.get(ReferenceExtDataField.FORMAT);
		if ((oFormat != null) && (oFormat instanceof String)) {
			this.formatPattern = new FormatPattern(oFormat.toString(), translation);

			final Object oDateFormat = parameters.get(ReferenceExtDataField.DATEFORMAT);
			if ((oDateFormat != null) && (oDateFormat instanceof String)) {
				final String dateFormat = oDateFormat.toString();
				this.formatPattern.setDateFormat(dateFormat);
			}
		}
	}

	protected void configureQueryCols(final Map parameters) {
		final Object querycols = parameters.get("querycols");
		if (querycols != null) {
			this.queryColumns = ApplicationManager.getTokensAt((String) querycols, ";");
		}
	}

	protected void configureQueryField(final Map parameters) {
		if (this.codeQueryField != null) {
			try {
				if (parameters.containsKey("form")) {
					parameters.remove("form");
				}
				final Object cols = parameters.get(Table.COLS);
				if (cols != null) {
					final String sColumnNames = cols.toString();
					final List vCols = ApplicationManager.getTokensAt(sColumnNames, ";");
					if (vCols.indexOf(this.codeQueryField) < 0) {
						vCols.add(this.codeQueryField);
					}
					for (int i = 0; i < this.descriptionColumns.size(); i++) {
						if (vCols.indexOf(this.descriptionColumns.get(i)) < 0) {
							vCols.add(this.descriptionColumns.get(i));
						}
					}
					parameters.put("cols", ApplicationManager.vectorToStringSeparateBy(vCols, ";"));
				}
				final Table tMultipleResults = new Table(parameters);
				this.createMultipleResultsWindow(tMultipleResults);
			} catch (final Exception e) {
				ReferenceExtDataField.logger.error("Error creating multiple results table.", e);
			}
		}
	}

	protected void configureCols(final Map parameters) {
		final Object visiblecols = parameters.get("visiblecols");
		final Object descriptioncols = parameters.get("descriptioncols");
		if (descriptioncols != null) {
			this.descriptionColumns = ApplicationManager.getTokensAt(descriptioncols.toString(), ";");
		} else if (visiblecols != null) {
			this.descriptionColumns = ApplicationManager.getTokensAt(visiblecols.toString(), ";");
		}
	}

	protected void configureOnSetValueSet(final Map parameters) {
		final Object onsetvalueset = parameters.get("onsetvalueset");
		if (onsetvalueset != null) {
			this.hOnSetValueSetEquivalences = ApplicationManager.getTokensAt(onsetvalueset.toString(), ";", ":");
			this.onsetvaluesetAttributes = new Vector();

			// We can't use the keys of the Map to get the attribute
			// names
			// because we have to use the same order that is in the xml
			final List valueNamesOrder = ApplicationManager.getTokensAt(onsetvalueset.toString(), ";");
			for (int i = 0; i < valueNamesOrder.size(); i++) {
				final int dotIndex = valueNamesOrder.get(i).toString().indexOf(":");
				if (dotIndex > 0) {
					this.onsetvaluesetAttributes.add(valueNamesOrder.get(i).toString().substring(0, dotIndex));
				} else {
					this.onsetvaluesetAttributes.add(valueNamesOrder.get(i));
				}
			}
			if (!this.onsetvaluesetAttributes.isEmpty()) {
				this.addValueChangeListener(new ValueChangeListener() {

					@Override
					public void valueChanged(final ValueEvent e) {
						if (ReferenceExtDataField.this.isEmpty()) {
							if ((ReferenceExtDataField.this.parentForm != null)
									&& (!ReferenceExtDataField.this.ignorenullonsetvalueset)) {
								for (int i = 0; i < ReferenceExtDataField.this.onsetvaluesetAttributes.size(); i++) {
									ReferenceExtDataField.this.parentForm.deleteDataField(
											(String) ReferenceExtDataField.this.onsetvaluesetAttributes.get(i));
									if (ApplicationManager.DEBUG) {
										ReferenceExtDataField.logger
										.debug(this.getClass().toString() + " Deleting field value: "
												+ ReferenceExtDataField.this.onsetvaluesetAttributes.get(i));
									}
								}
							}
						} else {
							final Map h = ReferenceExtDataField.this
									.getCodeValues(ReferenceExtDataField.this.getValue());
							ReferenceExtDataField.this.updateOnSetValueSetAttributes(h);
						}
					}
				});
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setParentKeyCache(final Map parameters) {
		final Object parentkeycache = parameters.get("parentkeycache");
		if ((parentkeycache != null) && parentkeycache.equals("yes")) {
			if (this.hasParentKeys()) {
				this.parentkeyCache = true;
				this.useCacheManager = true;
			} else {
				ReferenceExtDataField.logger.debug(
						"WARNING: PARAMETER 'parentkeycache' COULD NOT BE ESTABLISHED WHEN A PARENTKEY IS NOT DEFINED!!!!");
			}
		} else {
			if ((parentkeycache == null) && this.parentkeyCache) {
				this.useCacheManager = true;
				if (this.cacheTime == 0) {
					ReferenceExtDataField.logger.debug(
							"WARNING: PARAMETER 'parentkeycache' COULD NOT BE ESTABLISHED WHEN 'cacheTime' IS NOT DEFINED!!!!!");
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setBorderParameterForTable(final Map parameters) {
		Object border = parameters.get(DataField.BORDER);
		if (border == null) {
			border = DataField.DEFAULT_BORDER;
		}
		if (border != null) {
			final Border b = this.getBorder(border.toString());
			this.codeField.setBorder(b);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 */
	protected void setSearchIconForQueryButton() {
		final ImageIcon searchIcon = ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS);
		if (searchIcon == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("magnifyingglass.png icon not found");
			}
		} else {
			this.queryButton.setIcon(searchIcon);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 */
	protected void setDeleteIconFornDeleteButton() {
		final ImageIcon deleteIcon = ImageManager.getIcon(ImageManager.DELETE);
		if (deleteIcon != null) {
			this.deleteButton.setIcon(deleteIcon);
		} else {
			this.deleteButton.setText("..");
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void configParameterForTable(final Map parameters) {
		if (!parameters.containsKey(Table.CONTROLS_VISIBLE)) {
			parameters.put(Table.CONTROLS_VISIBLE, "no");
		}
		if (!parameters.containsKey("autoadjustheader")) {
			parameters.put("autoadjustheader", "no");
		}
		if (!parameters.containsKey("quickfilteronfocusselectall")) {
			parameters.put("quickfilteronfocusselectall", "true");
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 * @param key
	 */
	protected void setQuickFilterVisibleToTable(final Map parameters, final Object key) {
		if (key == null) {
			parameters.put("quickfiltervisible", "yes");
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 * @param key
	 */
	protected void setKeyToTable(final Map parameters, final Object key) {
		if (key == null) {
			parameters.put("key", this.code);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodQueryFieldTable(final Map parameters) {
		if (this.codeQueryField != null) {
			final Object cols = parameters.get(Table.COLS);
			if (cols != null) {
				final String sColumnNames = cols.toString();
				final List vCols = ApplicationManager.getTokensAt(sColumnNames, ";");
				if (vCols.indexOf(this.codeQueryField) < 0) {
					vCols.add(this.codeQueryField);
				}
				this.configureCols(parameters);
				if (this.descriptionColumns != null) {
					for (int i = 0; i < this.descriptionColumns.size(); i++) {
						if (vCols.indexOf(this.descriptionColumns.get(i)) < 0) {
							vCols.add(this.descriptionColumns.get(i));
						}
					}
				}
				parameters.put("cols", ApplicationManager.vectorToStringSeparateBy(vCols, ";"));
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodSearchParameter(final Map parameters) {
		// Parameter : codsearch
		final Object codsearch = parameters.get("codsearch");
		if (codsearch == null) {
		} else {
			this.codeQueryField = codsearch.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setDescVisibleParameter(final Map parameters) {
		// Parameter : descvisible
		final Object descvisible = parameters.get("descvisible");
		if (descvisible != null) {
			this.descriptionFieldVisible = ApplicationManager.parseStringValue(descvisible.toString());
			this.dataField.setVisible(this.descriptionFieldVisible);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodSearchVisibleParameter(final Map parameters) {
		final Object codSearchVisible = parameters.get("codsearchvisible");
		if (codSearchVisible != null) {
			this.visibleCodeSearch = ApplicationManager.parseStringValue(codSearchVisible.toString());
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodVisibleParameter(final Map parameters) {
		// Parameter :codvisible
		final Object codVisible = parameters.get("codvisible");
		if (codVisible != null) {
			try {
				if (codVisible.equals("yes")) {
					this.visibleCodeField = true;
				} else {
					this.visibleCodeField = new Boolean(codVisible.toString()).booleanValue();
					this.codeField.setVisible(this.visibleCodeField);
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(": Error in parameter 'codvisible': ", e);
				} else {
					ReferenceExtDataField.logger.trace(null, e);
				}
			}
		} else {
			this.visibleCodeField = true;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCSizeParameter(final Map parameters) {
		// Parameter: csize
		final Object csize = parameters.get("csize");
		if (csize != null) {
			try {
				this.codeField.setColumns(new Integer(csize.toString()).intValue());
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug("Error in parameter 'csize': ", e);
				} else {
					ReferenceExtDataField.logger.trace(null, e);
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setMaskParameter(final Map parameters) {
		// Parameter : mask
		final Object mask = parameters.get("mask");
		if (mask != null) {
			this.iMask = mask.toString();
			final MaskDocument doc = new MaskDocument(this.iMask);
			this.codeField.setDocument(doc);
			this.codeField.setToolTipText(this.iMask);
		} else {
			this.iMask = null;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCacheTimeParameter(final Map parameters) {
		// Parameter : cachetime
		final Object cache = parameters.get("cachetime");
		if (cache != null) {
			try {
				this.cacheTime = Integer.parseInt(cache.toString());
			} catch (final Exception e) {
				ReferenceExtDataField.logger.error("Error in parameter 'cachetime'", e);
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodeParameter(final Map parameters) {
		// Parameter cod : Name of database column that contains the code
		final Object cod = parameters.get("cod");
		if (cod == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Parameter 'cod' is required. Check parameters.");
			}
		} else {
			this.code = cod.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodeIntegerParameter(final Map parameters) {
		// Parameter: codInteger
		Object codInteger = parameters.get("codInteger");
		if (codInteger != null) {
			try {
				if (codInteger.equals("yes")) {
					this.integerValue = true;
				} else {
					this.integerValue = new Boolean(codInteger.toString()).booleanValue();
				}
			} catch (final Exception e) {
				if (com.ontimize.gui.ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug("Error in parameter 'codInteger'. " + this.attribute, e);
				} else {
					ReferenceExtDataField.logger.trace(null, e);
				}
			}
		} else {
			codInteger = parameters.get("codinteger");
			if (codInteger != null) {
				try {
					if (codInteger.equals("yes")) {
						this.integerValue = true;
					} else {
						this.integerValue = new Boolean(codInteger.toString()).booleanValue();
					}
				} catch (final Exception e) {
					if (com.ontimize.gui.ApplicationManager.DEBUG) {
						ReferenceExtDataField.logger.debug("Error in parameter 'codInteger'. " + this.attribute, e);
					} else {
						ReferenceExtDataField.logger.trace(null, e);
					}
				}
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setTitleParameter(final Map parameters) {
		final Object title = parameters.get("title");
		if (title != null) {
			this.windowTitle = title.toString();
		} else {
			this.windowTitle = this.entityName;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setEntityParameter(final Map parameters) {
		final Object entity = parameters.get("entity");
		if (entity == null) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Parameter 'entity' is required. Check parameters.");
			}
		} else {
			this.entityName = entity.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 */
	protected void checkAndSetUseOfPlaf() {
		if (ApplicationManager.useOntimizePlaf) {
			this.dataField.setBackground(DataComponent.VERY_LIGHT_GRAY);
			this.dataField.setForeground(Color.darkGray);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setNoResultClearCodeParameter(final Map parameters) {
		final Object noresultclearcode = parameters.get(ReferenceExtDataField.NO_RESULT_CLEAR_CODE);
		if (noresultclearcode != null) {
			this.noresultclearcode = ParseUtils.getBoolean(noresultclearcode.toString(), true);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setInitCacheOnsSetValueParameter(final Map parameters) {
		final Object initcacheonsetvalue = parameters.get("initcacheonsetvalue");
		if (initcacheonsetvalue != null) {
			if (initcacheonsetvalue.equals("yes")) {
				this.initCacheOnSetValue = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodNumberClassParameter(final Map parameters) {
		final Object codnumberclass = parameters.get("codnumberclass");
		if (codnumberclass != null) {
			this.codeNumberClass = ParseUtils.getTypeForName(codnumberclass.toString(), ParseTools.INTEGER_);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setDisableOnParentKeyNull(final Map parameters) {
		final Object disableonparentkeynull = parameters.get(ReferenceExtDataField.DISABLE_ON_PARENTKEY_NULL);
		if (disableonparentkeynull != null) {
			this.disableonparentkeynull = ParseUtils.getBoolean(disableonparentkeynull.toString(),
					ReferenceExtDataField.defaultDisableOnParentkeyNull);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setParentKeyListenerEventParameter(final Map parameters) {
		final Object parentkeylistenerevent = parameters.get(ReferenceExtDataField.PARENTKEY_LISTENER_EVENT);
		if (parentkeylistenerevent != null) {
			if (parentkeylistenerevent.equals(ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_USER)) {
				this.parentkeyListenerEvent = ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_USER;
			} else if (parentkeylistenerevent.equals(ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_PROGRAMMATIC)) {
				this.parentkeyListenerEvent = ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_PROGRAMMATIC;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setParentKeyListenerParameter(final Map parameters) {
		final Object parentkeylistener = parameters.get(ReferenceExtDataField.PARENTKEY_LISTENER);
		if (parentkeylistener != null) {
			this.parentkeyListener = ParseUtils.getBoolean(parentkeylistener.toString(),
					ReferenceExtDataField.defaultParentkeyListener);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setCodNumberParameter(final Map parameters) {
		final Object codnumber = parameters.get("codnumber");
		if (codnumber != null) {
			if (codnumber.equals("yes")) {
				this.codeNumber = true;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setUserCacheManagerParameter(final Map parameters) {
		final Object usecachemanager = parameters.get("usecachemanager");
		if (usecachemanager != null) {
			this.useCacheManager = ParseUtils.getBoolean(usecachemanager.toString(), true);
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void setSeparatorParameter(final Map parameters) {
		final Object separator = parameters.get("separator");
		if (separator != null) {
			this.separator = separator.toString();
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 * @param sParentKeyEquivalences
	 * @return
	 */
	protected String setOtherkeysParameter(final Map parameters, String sParentKeyEquivalences) {
		Object otherKeys = parameters.get("otherkeys");
		if (otherKeys == null) {
			otherKeys = parameters.get("parentkeys");
		}

		if (otherKeys != null) {
			final StringTokenizer st = new StringTokenizer(otherKeys.toString(), ";");
			while (st.hasMoreTokens()) {
				this.othersParentKey.add(ApplicationManager.getTokensAt(st.nextToken(), ":").get(0));
			}
			if (this.parentKeys != null) {
				sParentKeyEquivalences = sParentKeyEquivalences + ";" + otherKeys.toString();
			} else {
				sParentKeyEquivalences = otherKeys.toString();
			}
		}
		return sParentKeyEquivalences;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 * @return
	 */
	protected String setParentKeysParameter(final Map parameters) {
		String sParentKeyEquivalences = new String();
		final Object parentkey = parameters.get("parentkey");
		if (parentkey != null) {
			this.parentKeys = parentkey.toString();
			sParentKeyEquivalences = this.parentKeys;
		} else {
			this.parentKeys = null;
		}
		return sParentKeyEquivalences;
	}

	/**
	 * Method used to reduce the complexity of {@link #init(Map)}
	 *
	 * @param parameters
	 */
	protected void updateVisibleColsWithColsContent(final Map parameters) {
		if (!parameters.containsKey("visiblecols") && parameters.containsKey("cols")) {
			parameters.put("visiblecols", parameters.get("cols"));
		}
	}

	/**
	 * Encapsulates the process of building result <code>Table</code>.
	 *
	 * @param parameters Parameters used to build table
	 * @throws Exception When table constructor throws Exception
	 * @since 5.3.8
	 */
	protected void createTable(final Map parameters) throws Exception {
		final Map tableParameters = ObjectTools.clone(parameters);
		tableParameters.putAll(DefaultXMLParametersManager.getParameters(Table.class.getName()));
		// 5.2067EN - Parameter onsetvalueset cannot be shared by field and
		// table, it is configured only for field
		tableParameters.remove(Table.ONSETVALUESET);
		this.t = new Table(tableParameters);
		this.t.setMinRowHeight(Form.defaultTableViewMinRowHeight);
		// t.enableFiltering(false);
		this.t.getJTable().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.t.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(final ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					if (ReferenceExtDataField.this.t.getJTable().getSelectedRowCount() > 0) {
						if (ReferenceExtDataField.this.okButton != null) {
							ReferenceExtDataField.this.okButton.setEnabled(true);
						}
					} else {
						if (ReferenceExtDataField.this.okButton != null) {
							ReferenceExtDataField.this.okButton.setEnabled(false);
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
						if (ReferenceExtDataField.this.t.getSelectedRow() >= 0) {
							ReferenceExtDataField.this.okButton.doClick(10);
						}
					}
				}
			});
		}
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
					if ((ReferenceExtDataField.this.okButton != null)
							&& ReferenceExtDataField.this.okButton.isEnabled()) {
						ReferenceExtDataField.this.okButton.doClick(10);
					}
				}
			}
		});
	}

	/**
	 * Gets the code field value.
	 * <p>
	 *
	 * @return the object with code field value
	 */
	protected Object getCodeFieldValue() {
		return this.getTypedInnerValue(this.codeField.getText());
	}

	/**
	 * Obtains the typed value from parameter.
	 * <p>
	 *
	 * @param s the object to obtain the type
	 * @return the typed inner value
	 */
	protected Object getTypedInnerValue(final Object s) {
		Object oCodeValue = null;
		if ((s == null) || (s.toString().trim().length() == 0)) {
			return null;
		}
		if (this.codeNumber) {
			return ParseUtils.getValueForClassType(s, this.codeNumberClass);
		} else {
			if (this.integerValue) {
				Object oIntValue = null;
				try {
					oIntValue = new Integer(s.toString());
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
					return s;
				}
				oCodeValue = oIntValue;
			} else {
				oCodeValue = s;
			}
		}
		return oCodeValue;
	}

	/**
	 * Sets the code.
	 * <p>
	 *
	 * @see #setCode(Object, int)
	 * @param codeValue
	 */
	public void setCode(final Object codeValue) {
		this.setCode(codeValue, ValueEvent.PROGRAMMATIC_CHANGE);
	}

	protected MouseListener createOpenDetailMouseListener() {
		return new OpenDetailMouseListener();
	}

	protected void installMouseListener() {
		this.dataField.addMouseListener(this.createOpenDetailMouseListener());
	}

	public Map replaceParentkeyByEquivalence(final Map hParentkeyEquivalences) {
		if ((hParentkeyEquivalences == null) || hParentkeyEquivalences.isEmpty()) {
			return new Hashtable();
		}
		final Map hParentkeyValues = this.getParentKeyValues();
		if (hParentkeyValues != null) {
			final Map hReplacedParentkeyValues = new Hashtable();
			hReplacedParentkeyValues.putAll(hParentkeyValues);
			final Set values = hParentkeyValues.keySet();
			final Iterator itr = values.iterator();
			while (itr.hasNext()) {
				final Object key = itr.next();
				final Object value = hReplacedParentkeyValues.remove(key);
				hReplacedParentkeyValues.put(hParentkeyEquivalences.get(key), value);
			}
			return hReplacedParentkeyValues;
		}
		return new Hashtable();
	}

	// public Map replaceOnSetValueSetByEquivalence(Hashtable
	// hOnSetValueSetEquivalences){
	// Map hParentkeyValues = new Hashtable();
	// hParentkeyValues = getParentKeyValues();
	// if ((hOnSetValueSetEquivalences == null) ||
	// (hOnSetValueSetEquivalences.isEmpty())){
	// return hParentkeyValues;
	// }
	//
	// Map hReplacedOnSetValuesValues = new Hashtable();
	// hReplacedOnSetValuesValues.putAll(hParentkeyValues);
	// Set values = hParentkeyValues.keySet();
	// Iterator itr = values.iterator();
	// while (itr.hasNext()) {
	// Object key = itr.next();
	// Object value = hReplacedOnSetValuesValues.remove(key);
	// hReplacedOnSetValuesValues.put(hOnSetValueSetEquivalences.get(key),value);
	// }
	// return hReplacedOnSetValuesValues;
	// }

	/**
	 * Establishes the query
	 * <p>
	 *
	 * @param column the col name
	 * @param value  the value to add to keyvalues hashtable, with the form
	 *               (col,value)
	 * @return the result of query
	 */
	@Override
	public EntityResult queryBy(final String column, final Object value) {

		final Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		final long t = System.currentTimeMillis();
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final Map keysValues = this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences());
			keysValues.put(column, value);
			final EntityResult result = this.locator.getEntityReference(this.entityName).query(keysValues,
					this.getAttributes(), this.locator.getSessionId());
			if (ApplicationManager.DEBUG_TIMES) {
				ReferenceExtDataField.logger
				.debug("ReferenceExtDataField: Total query time: " + (System.currentTimeMillis() - t));
			}
			if (result.getCode() == EntityResult.OPERATION_WRONG) {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(result.getMessage());
				}
				return new EntityResultMapImpl();
			} else {
				ConnectionManager.checkEntityResult(result, this.locator);
			}
			return result;
		} catch (final Exception e) {
			ReferenceExtDataField.logger.error(null, e);
			if (ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Error in query. Results can not be shown", e);
			} else {
				ReferenceExtDataField.logger.error(null, e);
			}
			return new EntityResultMapImpl();
		} finally {
			this.setCursor(cursor);
		}
	}

	/**
	 * Queries by code
	 * <p>
	 *
	 * @param code the value to add to hashtable, with the form (code,codigo)
	 * @return the result of query
	 */
	protected EntityResult queryByCode(final Object code) {

		final Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		final long t = System.currentTimeMillis();
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final Map keysValues = this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences());
			if (code != null) {
				keysValues.put(this.code, code);
			}
			final EntityResult result = this.locator.getEntityReference(this.entityName).query(keysValues,
					this.getAttributes(), this.locator.getSessionId());
			if (ApplicationManager.DEBUG_TIMES) {
				ReferenceExtDataField.logger.debug(
						this.getClass().getName() + ": Time in queryByCode(): " + (System.currentTimeMillis() - t));
			}
			if (result.getCode() == EntityResult.OPERATION_WRONG) {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(result.getMessage());
				}
				return new EntityResultMapImpl();
			} else {
				ConnectionManager.checkEntityResult(result, this.locator);
			}
			return result;
		} catch (final Exception e) {
			if (ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Error in query. Results can not be shown", e);
			} else {
				ReferenceExtDataField.logger.error(null, e);
			}
			return new EntityResultMapImpl();
		} finally {
			this.setCursor(cursor);
		}
	}

	/**
	 * Sets the code in function of event value.
	 * <p>
	 *
	 * @param codeValue the value to set
	 * @see #setValue(Object, boolean)
	 * @param valueEventType the type of event
	 */
	protected void setCode(final Object codeValue, final int valueEventType) {
		// Query:
		try {
			if (this.cacheTime > 0) {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(": setCode(): Code Value: " + codeValue + " with cache");
				}
				boolean sameValue = false;
				final Object oPreviousValue = this.getInnerValue();
				if ((oPreviousValue == null) && (codeValue == null)) {
					sameValue = true;
				} else if ((oPreviousValue != null) && (codeValue != null) && codeValue.equals(oPreviousValue)) {
					sameValue = true;
				}

				this.disabledValueEvents = true;
				try {
					this.setValue(codeValue, true);
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}
				this.disabledValueEvents = false;
				this.setInnerValue(this.getValue());
				if (!sameValue) {
					this.fireValueChanged(this.getValue(), oPreviousValue, valueEventType);
				}
			} else {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(": setCode(): Code Value: " + codeValue + " without cache");
				}

				boolean sameValue = false;
				final Object oPreviousValue = this.getInnerValue();
				if ((oPreviousValue == null) && (codeValue == null)) {
					sameValue = true;
				} else if ((oPreviousValue != null) && (codeValue != null) && codeValue.equals(oPreviousValue)) {
					sameValue = true;
				}

				this.disabledValueEvents = true;
				try {
					this.setValue(codeValue, true);
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}
				this.disabledValueEvents = false;
				this.setInnerValue(this.getValue());
				if (!sameValue) {
					this.fireValueChanged(this.getValue(), oPreviousValue, valueEventType);
				}
			}
		} catch (final Exception e) {
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Error querying code", e);
			} else {
				ReferenceExtDataField.logger.trace(null, e);
			}
		}
	}

	@Override
	public Object getValue() {
		if (this.isEmpty()) {
			return null;
		}
		if (this.descriptionValue) {
			return ((JTextField) this.dataField).getText();
		} else {
			if (this.codeQueryField == null) {
				final String sValue = this.codeField.getText();
				if ("".equals(sValue)) {
					return null;
				}
				return this.getCodeFieldValue();
			} else if ("".equals(((JTextField) this.dataField).getText()) && (this.codeValue == null)) {
				return null;
			} else {
				return this.codeValue;
			}
		}
	}

	@Override
	public void setValue(final Object value) {
		this.setValue(value, false);
	}

	/**
	 * Establishes the field value. An object that will be an hashtable, whose keys
	 * are the code field name and description columns. Values may be vectors, in
	 * this case the first element is selected.
	 */
	public void setValue(final Object value, final boolean inner) {
		if ((value == null) || (value instanceof NullValue)) {
			this.deleteData();
			return;
		}
		this.setInnerListenerEnabled(false);

		final Object oPreviousValue = this.getValue();
		try {
			ReferenceExtDataField.logger.debug(": " + this.getAttribute() + ": setValue() : value: " + value
					+ " : cachetime" + this.cacheTime + " , usecachemanager: " + this.useCacheManager
					+ ", initcacheonsetvalue: " + this.initCacheOnSetValue);

			if (this.cacheTime > 0) {
				this.setValueIfCacheTimeMoreThanZero(value, inner, oPreviousValue);
				return;
			} else {
				// Obtain the code
				if (value instanceof Map) {
					this.setRecordValue(value, inner, oPreviousValue);
				} else {
					// When it is not a <code>Hashtable</code> is the code.
					// Therefore, query is executed.
					final Object oValue = this.getTypedInnerValue(value);
					this.dataCache = EntityResultUtils.toMap(this.queryByCode(oValue));
					if (this.dataCache.isEmpty()) {
						this.deleteUserData();
						return;
					}
					final List vCodes = (List) this.dataCache.get(this.code);
					if (vCodes == null) {
						ReferenceExtDataField.logger.debug(this.getClass().toString() + " : " + this.getAttribute()
						+ " Data for code " + this.code + " have not been encountered: " + this.dataCache);
					}
					final Object cod = vCodes.get(0);
					if ((this.codeQueryField == null) && (cod != null)) {
						this.codeField.setText(cod.toString());
					} else {
						this.codeValue = null;
						final List v = (List) this.dataCache.get(this.codeQueryField);
						this.codeValue = cod;
						if (v != null) {
							final Object v2 = v.get(0);
							if (v2 != null) {
								this.codeField.setText(v2.toString());
							} else {
								this.codeField.setText("");
							}
						} else {
							this.codeField.setText("");
						}

					}

					if ((vCodes == null) || (cod == null)) {
						this.deleteData();
						return;
					}
					if (!cod.equals(oValue)) {
						ReferenceExtDataField.logger.debug(this.getClass().toString() + ": setValue() : value: "
								+ oValue + " : There is no cache. It has been queried by code and returned value = "
								+ cod + " does not fulfil the equals() condition with indicated value : Passed value: "
								+ oValue.getClass() + " , class of returned value: " + cod.getClass());
						ReferenceExtDataField.logger
						.debug("USES CODNUMBER AND CODNUMBERCLASS, but does not use codInteger");
						this.deleteData();
						return;
					}
					final String cadenaDescripcion = this.getCodeDescription(cod, this.dataCache);
					((JTextField) this.dataField).setText(cadenaDescripcion);

					this.setInnerValue(this.getValue());
					if (!inner) {
						this.valueSave = this.getInnerValue();
					}
					this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
					this.setInnerListenerEnabled(true);

					return;
				}
			}
		} catch (final Exception e) {
			ReferenceExtDataField.logger.error(null, e);
			this.deleteData();
		}
	}

	protected void setRecordValue(final Object value, final boolean inner, final Object oPreviousValue) {
		this.dataCache = (Map) value;
		Object cod = ((Map) value).get(this.code);
		if (cod != null) {
			if (cod instanceof List) {
				cod = ((List) cod).get(0);
				if ((this.codeQueryField == null) && (cod != null)) {
					this.codeField.setText(cod.toString());
				} else {
					this.codeValue = null;
					final List v = (List) this.dataCache.get(this.codeQueryField);
					final int index = ((List) ((Map) value).get(this.code)).indexOf(cod);
					if (index >= 0) {
						this.codeValue = cod;
						final Object v2 = v.get(index);
						if (v2 != null) {
							this.codeField.setText(v2.toString());
						} else {
							this.codeField.setText("");
						}
					}
				}
				if (cod == null) {
					this.deleteData();
					return;
				}
			} else {
				if ((this.codeQueryField == null) && (cod != null)) {
					this.codeField.setText(cod.toString());
				} else {
					this.codeValue = null;
					final List v = (List) this.dataCache.get(this.codeQueryField);
					final int index = ((List) ((Map) value).get(this.code)).indexOf(cod);
					if (index >= 0) {
						this.codeValue = cod;
						final Object v2 = v.get(index);
						if (v2 != null) {
							this.codeField.setText(v2.toString());
						} else {
							this.codeField.setText("");
						}
					}
				}
			}
			final String sDescriptionString = this.getCodeDescription(cod, (Map) value);
			((JTextField) this.dataField).setText(sDescriptionString);

			this.setInnerValue(this.getValue());
			if (!inner) {
				this.valueSave = this.getInnerValue();
			}
			this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
			this.setInnerListenerEnabled(true);
		} else {
			this.deleteData();
			return;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #setValue(Object, boolean)}
	 *
	 * @param value
	 * @param inner
	 * @param oPreviousValue
	 */
	protected void setValueIfCacheTimeMoreThanZero(final Object value, final boolean inner, final Object oPreviousValue) {
		final Object oValue = this.getTypedInnerValue(value);
		final long t = System.currentTimeMillis();
		final long timeFromLastQuery = t - this.getLastCacheTime();
		if ((timeFromLastQuery > this.cacheTime) && this.dataCacheInitialized) {
			try {
				this.fireValueEvents = false;
				this.invalidateCache();
				this.setInnerListenerEnabled(false);
			} catch (final Exception e) {
				ReferenceExtDataField.logger.trace(null, e);
			} finally {
				this.fireValueEvents = true;
			}
		}
		if ((!this.dataCacheInitialized) && (this.initCacheOnSetValue)) {
			try {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(this.getClass().toString() + ": " + this.getAttribute()
					+ ": setValue() : value: " + oValue + " : Initializing in cache");
				}
				this.initCache();
			} catch (final Exception e) {
				ReferenceExtDataField.logger.trace(null, e);
				this.dataCache = null;
				this.deleteData();
				return;
			}
		} else {
			this.useCacheManager();
		}
		this.setInnerListenerEnabled(false);
		final List vCodes = (List) this.dataCache.get(this.code);

		if ((vCodes == null) || vCodes.isEmpty() || !vCodes.contains(oValue)) {
			this.setValueIfNotExistCacheData(inner, oPreviousValue, oValue);
			return;
		} else {
			this.setValueIfExistCacheData(inner, oPreviousValue, oValue, vCodes);
			return;
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #setValue(Object, boolean)}
	 *
	 * @param inner
	 * @param oPreviousValue
	 * @param oValue
	 */
	protected void setValueIfNotExistCacheData(final boolean inner, final Object oPreviousValue, final Object oValue) {
		List vCodes;
		if (ApplicationManager.DEBUG) {
			ReferenceExtDataField.logger.debug(this.getClass().toString() + ": " + this.getAttribute()
			+ ": setValue() : value: " + oValue + " : Code is not stored in cache ");
		}

		// Query
		final EntityResult res = this.queryByCode(oValue);
		if (res.isEmpty() || (res.getCode() == EntityResult.OPERATION_WRONG)) {
			if (ApplicationManager.DEBUG && (res.getCode() == EntityResult.OPERATION_WRONG)) {
				ReferenceExtDataField.logger
				.debug(": setValue() : value: " + oValue + " : Error in query result: " + res.getMessage());
			}
			if (this.noresultclearcode) {
				this.deleteData();
			} else {
				this.deleteData(false);
			}
			return;
		} else {
			if ((this.dataCache.isEmpty()) || !this.dataCache.containsKey(this.code)) {
				this.dataCache = EntityResultUtils.toMap(res);
			}
			// Put in the cache
			vCodes = (List) this.dataCache.get(this.code);
			if (vCodes.contains(oValue)) {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(this.getClass().toString() + ": " + this.getAttribute()
					+ ": setValue() : value: " + oValue + " : Code is not stored in cache");
				}

				if (this.codeQueryField == null) {
					this.codeField.setText(oValue.toString());
				} else {
					this.codeValue = null;
					final List v = (List) this.dataCache.get(this.codeQueryField);
					final int index = vCodes.indexOf(oValue);
					if (index >= 0) {
						final Object v2 = v.get(index);
						if (v2 != null) {
							this.codeField.setText(v2.toString());
						} else {
							this.codeField.setText("");
						}
						this.codeValue = oValue;
					}
				}

				final String sDescriptionString = this.getCodeDescription(oValue, this.dataCache);
				((JTextField) this.dataField).setText(sDescriptionString);
				this.setInnerValue(this.getValue());
				if (!inner) {
					this.valueSave = this.getInnerValue();
				}
				this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				this.setInnerListenerEnabled(true);
				return;
			} else {
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(this.getClass().toString() + ": " + this.getAttribute()
					+ ": setValue() : value: " + oValue + " : Code is not stored in cache");
				}

				if (!((List) res.get(this.code)).contains(oValue)) {
					if (ApplicationManager.DEBUG) {
						ReferenceExtDataField.logger.debug(": " + this.getAttribute() + ": setValue() : value: "
								+ oValue + " : In codes of query result is not stored the code ");
					}
					this.deleteData();
					return;
				}

				// Put in the cache
				final Enumeration enumCacheKeys = Collections.enumeration(this.dataCache.keySet());
				while (enumCacheKeys.hasMoreElements()) {
					final Object oCacheKey = enumCacheKeys.nextElement();
					final List vCacheValues = (List) this.dataCache.get(oCacheKey);
					final List vResultValues = (List) res.get(oCacheKey);
					if ((vResultValues != null) && !vResultValues.isEmpty()) {
						vCacheValues.add(vCacheValues.size(), vResultValues.get(0));
					} else {
						vCacheValues.add(vCacheValues.size(), null);
					}
				}
				if (this.codeQueryField == null) {
					this.codeField.setText(oValue.toString());
				} else {
					this.codeValue = null;
					final List v = (List) this.dataCache.get(this.codeQueryField);
					final int index = vCodes.indexOf(oValue);
					if (index >= 0) {
						this.codeValue = oValue;
						final Object v2 = v.get(index);
						if (v2 != null) {
							this.codeField.setText(v2.toString());
						} else {
							this.codeField.setText("");
						}
					}
				}
				final String sDescriptionString = this.getCodeDescription(oValue, this.dataCache);
				((JTextField) this.dataField).setText(sDescriptionString);

				this.setInnerValue(this.getValue());
				if (!inner) {
					this.valueSave = this.getInnerValue();
				}
				this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
				this.setInnerListenerEnabled(true);
				return;
			}
		}
	}

	/**
	 * Method used to reduce the complexity of {@link #setValue(Object, boolean)}
	 *
	 * @param inner
	 * @param oPreviousValue
	 * @param oValue
	 * @param vCodes
	 */
	protected void setValueIfExistCacheData(final boolean inner, final Object oPreviousValue, final Object oValue, final List vCodes) {
		if (ApplicationManager.DEBUG) {
			ReferenceExtDataField.logger
			.debug(": setValue() : value: " + oValue + " : In codes of query result is stored the code");
		}
		if (this.codeQueryField == null) {
			this.codeField.setText(oValue.toString());
		} else {
			this.codeValue = null;
			final List v = (List) this.dataCache.get(this.codeQueryField);
			final int index = vCodes.indexOf(oValue);
			if (index >= 0) {
				this.codeValue = oValue;
				final Object v2 = v.get(index);
				if (v2 != null) {
					this.codeField.setText(v2.toString());
				} else {
					this.codeField.setText("");
				}
			}
		}
		final String sDescriptionString = this.getCodeDescription(oValue, this.dataCache);
		((JTextField) this.dataField).setText(sDescriptionString);

		this.setInnerValue(this.getValue());
		if (!inner) {
			this.valueSave = this.getInnerValue();
		}
		this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
		return;
	}

	protected void useCacheManager() {
		if (this.useCacheManager && (this.cacheManager != null)) {
			if (this.parentkeyCache && this.hasParentKeys()) {
				try {
					this.setInnerListenerEnabled(false);
					this.fireValueEvents = false;
					final EntityResult res = this.cacheManager.getDataCache(this.entityName, this.getAttributes(),
							this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences()));
					this.dataCache = EntityResultUtils.toMap(res);
				} catch (final Exception ex) {
					ReferenceExtDataField.logger.error("{}", ex.getMessage(), ex);
				} finally {
					this.fireValueEvents = true;
					this.setInnerListenerEnabled(true);
				}
			} else if (this.dataCacheInitialized == false) {
				final Pair<DataCacheId, EntityResult> pair = this.cacheManager.retrieveDataCache(this.entityName,
						this.getAttributes(), null, this.cacheTime);
				if (pair != null) {
					try {
						this.setInnerListenerEnabled(false);
						this.fireValueEvents = false;
						final EntityResult res = pair.getSecond();
						this.dataCache = EntityResultUtils.toMap(res);
						this.dataCacheInitialized = true;
						this.lastCacheTime = pair.getFirst().getTime();
					} catch (final Exception ex) {
						ReferenceExtDataField.logger.error("{}", ex.getMessage(), ex);
					} finally {
						this.fireValueEvents = true;
						this.setInnerListenerEnabled(true);
					}
				} else {
					// Retrieve cache for next time
					final Thread th = new Thread("Initialize cache manager for : " + this.entityName) {
						@Override
						public void run() {
							super.run();
							final EntityResult er = ReferenceExtDataField.this.cacheManager.getDataCache(
									ReferenceExtDataField.this.entityName, ReferenceExtDataField.this.getAttributes(),
									null);
						}
					};
					th.start();
				}
			}
		}
	}

	protected String getQueryDescription(final Object code, final Map value, final String description) {
		if (this.queryColumns == null) {
			return description;
		}

		final StringBuilder buffer = new StringBuilder(description);
		final List vCodes = (List) value.get(this.code);
		final int index = vCodes.indexOf(code);

		for (final String column : this.queryColumns) {
			final Object dataList = value.get(column);
			if ((dataList instanceof List) && (((List) dataList).size() > index)) {
				buffer.append(this.separator);
				buffer.append(this.toString(((List) dataList).get(index)));
			}
		}

		return buffer.toString();
	}

	/**
	 * Obtains the code description.
	 * <p>
	 *
	 * @param code  the object to search in Map parameter
	 * @param value the Map with all codes
	 * @return the code description
	 */
	protected String getCodeDescription(final Object code, final Map value) {
		// For all columns, gets and shows the value
		String description = new String();
		final List vCodes = (List) value.get(this.code);
		final int index = vCodes.indexOf(code);

		if ((this.formatPattern != null) && !this.formatPattern.isEmpty()) {
			description = this.formatPattern.parse(index, value);
		} else {
			final StringBuilder sDescriptionString = new StringBuilder();
			List vColumns = null;
			if (this.descriptionColumns != null) {
				vColumns = this.descriptionColumns;
			} else {
				vColumns = this.t.getVisibleColumns();
			}

			for (int i = 0; i < vColumns.size(); i++) {
				final Object oColumn = vColumns.get(i);
				final Object vColumn = value.get(oColumn);
				if (vColumn != null) {
					if (vColumn instanceof List) {
						if (index < 0) {
							return "NOT FOUND";
						}
						Object v = ((List) vColumn).get(index);
						if (v == null) {
							v = "";
						}
						sDescriptionString.append(this.toString(v));
						if (i < (vColumns.size() - 1)) {
							sDescriptionString.append(this.separator);
						}
					} else {
						sDescriptionString.append(this.toString(vColumn));
						if (i < (vColumns.size() - 1)) {
							sDescriptionString.append(this.separator);
						}
					}
				}
			}
			description = sDescriptionString.toString();
		}
		return description;
	}

	/**
	 * Checks whether parameter is a string or a date.
	 * <p>
	 *
	 * @param v the object to convert
	 * @return the string conversion
	 */
	protected String toString(final Object v) {
		if (v == null) {
			return "";
		} else if (v instanceof String) {
			return (String) v;
		} else if (v instanceof Date) {
			if (this.auxDateDoc == null) {
				this.auxDateDoc = new DateDocument();
			}
			this.auxDateDoc.setComponentLocale(this.locale);
			this.auxDateDoc.setValue((Date) v);
			try {
				return this.auxDateDoc.getText(0, this.auxDateDoc.getLength());
			} catch (final BadLocationException ex) {
				ReferenceExtDataField.logger.trace(null, ex);
				return v.toString();
			}
		} else {
			return v.toString();
		}
	}

	/**
	 * Returns the SQL type of varchar or integer in function of
	 * {@link #integerValue}.
	 * <p>
	 *
	 * @return the sql data type
	 */
	@Override
	public int getSQLDataType() {
		if (this.codeNumber) {
			return ParseUtils.getSQLType(this.codeNumberClass, ParseTools.INTEGER_);
		}
		if (!this.integerValue) {
			return Types.VARCHAR;
		} else {
			return Types.INTEGER;
		}
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		this.locator = locator;
	}

	public void registerParentkeyValueChangeListeners() {
		if (this.isParentkeyListener()) {
			if (this.getParentKeyList() != null) {
				for (int i = 0; i < this.getParentKeyList().size(); i++) {
					final DataField field = (DataField) this.parentForm
							.getDataFieldReference(this.getParentKeyList().get(i).toString());
					if (field != null) {

						field.addValueChangeListener(new ValueChangeListener() {

							@Override
							public void valueChanged(final ValueEvent e) {
								// check event type
								if (ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_ALL
										.equals(ReferenceExtDataField.this.parentkeyListenerEvent)) {
									this.doAction(e);
								} else if ((e.getType() == ValueEvent.PROGRAMMATIC_CHANGE)
										&& ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_PROGRAMMATIC
										.equals(ReferenceExtDataField.this.parentkeyListenerEvent)) {
									this.doAction(e);
								} else if ((e.getType() == ValueEvent.USER_CHANGE)
										&& ReferenceExtDataField.PARENTKEY_LISTENER_EVENT_USER
										.equals(ReferenceExtDataField.this.parentkeyListenerEvent)) {
									this.doAction(e);
								}
							}

							protected void doAction(final ValueEvent e) {
								if ((e.getOldValue() == null)
										|| ((e.getOldValue() != null) && !(e.getOldValue().equals(e.getNewValue())))) {
									if (e.getNewValue() != null) {
										ReferenceExtDataField.this.deleteData();
										ReferenceExtDataField.this.setEnabled(true);
									} else {
										if ((e.getOldValue() != null)
												&& ReferenceExtDataField.this.disableonparentkeynull) {
											ReferenceExtDataField.this.setEnabled(false);
											ReferenceExtDataField.this.deleteData();
										}
									}
								}
							}
						});
					}
				}
			}
		}
	}

	public boolean isParentkeyListener() {
		return this.parentkeyListener;
	}

	public void setParentkeyListener(final boolean parentkeyListener) {
		this.parentkeyListener = parentkeyListener;
	}

	@Override
	public void setParentFrame(final Frame frame) {
		this.parentFrame = frame;
		if (this.t != null) {
			this.t.setParentFrame(this.parentFrame);
		}
		if ((this.multipleResultWindow != null) && (this.multipleResultWindow.multipleResultTable != null)) {
			this.multipleResultWindow.multipleResultTable.setParentFrame(this.parentFrame);
		}
	}

	@Override
	public void free() {
		this.parentFrame = null;
		if (this.tableWindow != null) {
			this.tableWindow.dispose();
		}
		FreeableUtils.freeComponent(this.tableWindow);
		FreeableUtils.freeComponent(this.t);
		this.t = null;
		this.tableWindow = null;
	}

	@Override
	public void setParentForm(final Form f) {
		this.parentForm = f;
		this.t.setParentForm(this.parentForm);
		if (this.multipleResultWindow != null) {
			this.multipleResultWindow.multipleResultTable.setParentForm(f);
		}
		this.registerParentkeyValueChangeListeners();
	}

	@Override
	public void setFormBuilder(final FormBuilder builder) {
		this.t.setFormBuilder(builder);
		if (this.multipleResultWindow != null) {
			this.multipleResultWindow.multipleResultTable.setFormBuilder(builder);
		}
	}

	@Override
	public void setEnabled(final boolean en) {

		boolean enabled = en;

		if (enabled) {
			final boolean permision = this.checkEnabledPermission();
			if (!permision) {
				this.setEnabled(false);
				return;
			}
		} else {
			this.dataField.setEnabled(enabled);
		}

		if ((this.parentkeyList != null) && (this.parentkeyList.size() > 0) && this.disableonparentkeynull) {
			for (int i = 0; i < this.parentkeyList.size(); i++) {
				final Object dataFieldValue = this.parentForm.getDataFieldValue(this.parentkeyList.get(i).toString());
				if (dataFieldValue == null) {
					enabled = false;
					break;
				}
			}
		}
		if (this.descriptionQuery) {
			this.dataField.setEnabled(enabled);
		}
		this.codeField.setEnabled(enabled);
		this.queryButton.setEnabled(enabled);
		this.deleteButton.setEnabled(enabled);

		this.enabled = enabled;
		this.updateBackgroundColor();
	}

	/**
	 * Updates the background color.
	 */
	@Override
	protected void updateBackgroundColor() {
		super.updateBackgroundColor();

		if (this.requiredBorder != null) {
			if (!this.enabled) {
				this.codeField.setForeground(this.fontColor);
				this.codeField.setBackground(this.disabledbgcolor);
			} else {
				this.codeField.setBackground(this.backgroundColor);
				this.codeField.setForeground(this.fontColor);
			}
		} else if (DataField.ASTERISK_REQUIRED_STYLE) {
			if (this.enabled) {
				this.codeField.setForeground(this.fontColor);
				this.codeField.setBackground(this.backgroundColor);
			} else {
				this.codeField.setForeground(this.fontColor);
				this.codeField.setBackground(this.disabledbgcolor);
			}
		} else {
			if (!this.enabled) {
				this.codeField.setForeground(this.fontColor);
				this.codeField.setBackground(this.disabledbgcolor);
			} else {
				if (this.required) {
					this.codeField.setBackground(DataField.requiredFieldBackgroundColor);
					this.codeField.setForeground(DataField.requiredFieldForegroundColor != null
							? DataField.requiredFieldForegroundColor : this.fontColor);
				} else {
					this.codeField.setBackground(this.backgroundColor);
					this.codeField.setForeground(this.fontColor);
				}
			}
		}
	}

	@Override
	public void deleteData() {
		this.deleteData(true);
	}

	public void deleteData(final boolean clearCode) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getValue();
		((JTextField) this.dataField).setText("");
		if (clearCode) {
			this.codeField.setText("");
		}
		this.valueSave = this.getValue();
		this.setInnerValue(this.valueSave);
		this.fireValueChanged(this.valueSave, oPreviousValue, ValueEvent.PROGRAMMATIC_CHANGE);
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Deletes the user data in code field.
	 */
	protected void deleteUserData(final boolean clearcode) {
		this.deleteUserData(clearcode, true);
	}

	protected void deleteUserData(final boolean clearcode, final boolean cleardescription) {
		this.setInnerListenerEnabled(false);
		final Object oPreviousValue = this.getInnerValue();
		if (cleardescription) {
			((JTextField) this.dataField).setText("");
		}
		if (clearcode) {
			this.codeField.setText("");
		}
		if (this.t != null) {
			this.t.getJTable().clearSelection();
		}
		if ((this.multipleResultWindow != null) && (this.multipleResultWindow.multipleResultTable != null)) {
			this.multipleResultWindow.multipleResultTable.getJTable().clearSelection();
		}
		this.setInnerValue(this.getValue());
		this.fireValueChanged(this.getInnerValue(), oPreviousValue, ValueEvent.USER_CHANGE);
		this.codeValue = null;
		this.setInnerListenerEnabled(true);
	}

	/**
	 * Deletes the user data in code field.
	 */
	protected void deleteUserData() {
		this.deleteUserData(true);
	}

	@Override
	public boolean isModified() {
		return super.isModified();
	}

	@Override
	public void setResourceBundle(final ResourceBundle resources) {
		super.setResourceBundle(resources);

		// Update field content if has a custom formatter.
		if (this.formatPattern != null) {
			this.formatPattern.setResourceBundle(resources);

			if (!this.formatPattern.isEmpty()) {
				final Object value = this.getValue();
				if (value != null) {
					final String s = this.getCodeDescription(value, this.dataCache);
					((JTextField) this.dataField).setText(s);
				}
			}
		}

		if (this.t != null) {
			this.t.setResourceBundle(resources);
		}
		if ((this.multipleResultWindow != null) && (this.multipleResultWindow.multipleResultTable != null)) {
			this.multipleResultWindow.multipleResultTable.setResourceBundle(resources);
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
							ApplicationManager.getTranslation(ReferenceExtDataField.auxCodeLabelKey, resources));
				}
			}
		} catch (final Exception e) {
			if (this.windowTitle != null) {
				this.tableWindow.setTitle(this.windowTitle);
			}
			if (com.ontimize.gui.ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug(null, e);
			} else {
				ReferenceExtDataField.logger.trace(null, e);
			}
		}

		this.queryButton.setToolTipText(ApplicationManager.getTranslation("datafield.select", resources));
		this.deleteButton.setToolTipText(ApplicationManager.getTranslation("datafield.reset_field", resources));
	}

	@Override
	public Object getAttribute() {
		return this.attribute;
	}

	/**
	 * Gets the code field name
	 * <p>
	 *
	 * @see #code
	 * @return the code
	 */
	@Override
	public String getCodeFieldName() {
		return this.code;
	}

	@Override
	public void requestFocus() {
		if ((this.codeField != null) && this.codeField.isVisible()) {
			this.codeField.requestFocus();
		} else {
			if (this.queryButton != null) {
				this.queryButton.requestFocus();
			}
		}
	}

	/**
	 * Gets the text to translate
	 * <p>
	 *
	 * @return the List with elements to translate
	 */
	@Override
	public List getTextsToTranslate() {
		final List v = super.getTextsToTranslate();
		if (this.attribute instanceof ReferenceFieldAttribute) {
			v.add(((ReferenceFieldAttribute) this.attribute).getAttr());
			v.add(((ReferenceFieldAttribute) this.attribute).getCod());
		}
		if (this.t != null) {
			v.addAll(this.t.getTextsToTranslate());
		}
		return v;
	}

	/**
	 * Returns the combo values associated to code. Returns key-values pairs.
	 * <p>
	 *
	 * @param code the object to find
	 * @return the Map with key-values
	 */
	public Map getCodeValues(final Object code) {
		final Map h = new Hashtable();
		if ((this.cacheTime > 0) && this.isDataCacheInitialized() && this.useCacheManager && (this.cacheManager != null)
				&& (this.parentkeyCache || this.cacheManager.existsCache(this.entityName, this.getAttributes(),
						this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences())))) {
			this.dataCache = EntityResultUtils
					.toMap(this.cacheManager.getDataCache(this.entityName, this.getAttributes(),
							this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences())));
		}

		if (this.dataCache != null) {
			final List vCodes = (List) this.dataCache.get(this.code);
			if ((vCodes == null) || vCodes.isEmpty()) {
				return h;
			}
			final int index = vCodes.indexOf(code);
			if (index < 0) {
				return h;
			}
			final Enumeration enumKeys = Collections.enumeration(dataCache.keySet());
			while (enumKeys.hasMoreElements()) {
				final Object oKey = enumKeys.nextElement();
				final List vValues = (List) this.dataCache.get(oKey);
				final Object oValue = vValues.get(index);
				if (oValue != null) {
					h.put(oKey, oValue);
				}
			}
		}
		return h;
	}

	/**
	 * Gets the data cache.
	 * <p>
	 *
	 * @return the data cache
	 */
	public Map getDataCache() {
		return this.dataCache;
	}

	/**
	 * Inits the cache. If there are not parentkeys, cachemanager is used.
	 */
	public void initCache() {
		// When parent keys are not present, we use cachemanager.
		if (this.parentkeyCache || (this.getParentKeys() == null) || this.getParentKeys().isEmpty()) {
			if ((this.cacheTime != 0) && this.dataCacheInitialized) {
				return;
			}
			if ((this.cacheManager != null) && this.useCacheManager) {
				try {
					final long t = System.currentTimeMillis();
					this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					final EntityResult res = this.cacheManager.getDataCache(this.entityName, this.getAttributes(),
							this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences()));
					if (res.getCode() == EntityResult.OPERATION_WRONG) {
						if (this.parentForm != null) {
							this.parentForm.message(res.getMessage(), Form.ERROR_MESSAGE);
						}
						return;
					}
					this.dataCache = EntityResultUtils.toMap(res);
					this.dataCacheInitialized = true;
					this.lastCacheTime = System.currentTimeMillis();
					if (ApplicationManager.DEBUG_TIMES) {
						ReferenceExtDataField.logger
						.debug(this.getClass().getName() + ": init cache time: " + (this.lastCacheTime - t));
					}
					return;
				} catch (final Exception e) {
					if (ApplicationManager.DEBUG) {
						ReferenceExtDataField.logger.debug("CacheManager can not be used: ", e);
					} else {
						ReferenceExtDataField.logger.trace(null, e);
					}
				} finally {
					this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		}

		final Cursor cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		final long t = System.currentTimeMillis();
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			final Map hKeysValues = this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences());
			final EntityResult result = this.locator.getEntityReference(this.entityName).query(hKeysValues,
					this.getAttributes(), this.locator.getSessionId());
			if (ApplicationManager.DEBUG_TIMES) {
				ReferenceExtDataField.logger
				.debug(this.getClass().getName() + ": init cache time: " + (this.lastCacheTime - t));
			}
			if (result.getCode() == EntityResult.OPERATION_WRONG) {
				if (this.parentForm != null) {
					this.parentForm.message(result.getMessage(), Form.ERROR_MESSAGE);
				}
				return;
			} else {
				ConnectionManager.checkEntityResult(result, this.locator);
			}
			// Now, shows the window
			this.dataCache = EntityResultUtils.toMap(result);
			this.dataCacheInitialized = true;
			this.lastCacheTime = t;
			if (ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug(": Initialized data cache.");
				int size = -1;
				ByteArrayOutputStream bOut = null;
				ObjectOutputStream out = null;
				try {
					bOut = new ByteArrayOutputStream();
					out = new ObjectOutputStream(bOut);
					out.writeObject(result);
					out.flush();
					size = bOut.size();

				} catch (final Exception e) {
					ReferenceExtDataField.logger.error(null, e);
				} finally {
					if (bOut != null) {
						bOut.reset();
						bOut.close();
					}
					if (out != null) {
						out.close();
					}
				}
				ReferenceExtDataField.logger.debug(": Cache size is " + size + " bytes");
			}
		} catch (final Exception e) {
			this.parentForm.message("interactionmanager.error_in_query", Form.ERROR_MESSAGE, e);
			ReferenceExtDataField.logger.error("" + e.getMessage(), e);
			if (ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.debug("Error in query. Results cannot be showed." + e.getMessage(), e);
			} else {
				ReferenceExtDataField.logger.error(null, e);
			}
		} finally {
			this.setCursor(cursor);
		}
	}

	/**
	 * Gets the last cache time
	 * <p>
	 *
	 * @see #lastCacheTime
	 * @return the last cache time
	 */
	protected long getLastCacheTime() {
		if (this.useCacheManager && (this.cacheManager != null)
				&& (this.parentkeyCache || this.cacheManager.existsCache(this.entityName, this.getAttributes(),
						this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences())))) {
			return this.cacheManager.getLastCacheTime(this.entityName,
					this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences()));
		} else {
			return this.lastCacheTime;
		}
	}

	/**
	 * Invalidate cache setting to 0 the lastCacheTime.
	 */
	public void invalidateCache() {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			this.lastCacheTime = 0;
			final Object oValue = this.getValue();
			this.dataCacheInitialized = false;
			this.dataCache = new Hashtable();
			if ((this.cacheManager != null) && this.useCacheManager) {
				this.cacheManager.invalidateCache(this.entityName,
						this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences()));
			}
			this.initCache();
			this.setValue(oValue);
		} catch (final Exception e) {
			ReferenceExtDataField.logger.error("Error updating cache", e);
			if (ApplicationManager.DEBUG) {
				ReferenceExtDataField.logger.error(null, e);
			} else {
				ReferenceExtDataField.logger.trace(null, e);
			}
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}

	public boolean isDataCacheInitialized() {
		return this.dataCacheInitialized;
	}

	/**
	 * Returns the parentkeys.
	 * <p>
	 *
	 * @return the List with parentkeys
	 */
	public List getParentKeys() {
		if (this.parentkeyList == null) {
			this.parentkeyList = new Vector();
			if (this.parentKeys != null) {
				this.parentkeyList.add(this.parentKeys);
			}
			for (int i = 0; i < this.othersParentKey.size(); i++) {
				final Object oKey = this.othersParentKey.get(i);
				if (!this.parentkeyList.contains(oKey)) {
					this.parentkeyList.add(oKey);
				}
			}
		}
		return this.parentkeyList;
	}

	/**
	 * Returns a Map with key-value corresponding with result to apply two
	 * 'tokenizer' actions over parentkeys parameter. For example, <br>
	 * <br>
	 * <code>string="formfieldpk1:equivalententityfieldpk1;formfieldpk2:equivalententityfieldpk2;...;formfieldpkn:equivalententityfieldpkn"</code>
	 * <br>
	 * <br>
	 * returns <code>Hashtable</code>: <br>
	 * <br>
	 * { formfieldpk1 equivalententityfieldpk1} <br>
	 * { formfieldpk2 equivalententityfieldpk2} <br>
	 * { ... ... } <br>
	 * { formfieldpkn equivalententityfieldpkn} <br>
	 *
	 * @param parentkeys the string with values
	 * @return <code>Hashtable</code> with key-value
	 */
	public Map getParentkeyEquivalences() {
		return this.hParentkeyEquivalences;
	}

	@Override
	public boolean hasParentKeys() {
		if ((this.getParentKeys() != null) && (this.getParentKeys().size() > 0)) {
			return true;
		}
		return false;
	}

	@Override
	public Map getParentKeyValues() {
		if ((this.getParentKeys() != null) && (this.getParentKeys().size() > 0)) {
			final Map keysValues = new Hashtable();
			for (int i = 0; i < this.getParentKeys().size(); i++) {
				final Object currentParentKey = this.getParentKeys().get(i);
				final Object currentParentKeyValue = this.parentForm.getDataFieldValue(currentParentKey.toString());
				if (ApplicationManager.DEBUG) {
					ReferenceExtDataField.logger.debug(
							": Filtering by " + currentParentKey + " parentkey with value: " + currentParentKeyValue);
				}
				if (currentParentKeyValue != null) {
					keysValues.put(currentParentKey, currentParentKeyValue);
				}
			}
			return keysValues;
		}
		return null;
	}

	/**
	 * Sets the CacheManager.
	 */
	@Override
	public void setCacheManager(final CacheManager cm) {
		this.cacheManager = cm;
	}

	/**
	 * Gets Attributes from table.
	 * <p>
	 *
	 * @return the List with attributes
	 */
	@Override
	public List getAttributes() {
		return this.t.getAttributeList();
	}

	/**
	 * The entity name.
	 * <p>
	 *
	 * @return the {@link #entityName}
	 */
	@Override
	public String getEntity() {
		return this.entityName;
	}

	@Override
	protected void fireValueChanged(final Object newValue, final Object oldValue, final int type) {
		if (!this.disabledValueEvents) {
			super.fireValueChanged(newValue, oldValue, type);
		}
	}

	@Override
	protected void installFocusListener() {
		if (this.codeField != null && !ApplicationManager.useOntimizePlaf) {
			this.codeField.addFocusListener(this.fieldlistenerFocus);
		}
	}

	/**
	 * Applies a filter to a specified column.
	 * <p>
	 *
	 * @param column    the name of column
	 * @param values    the object array to apply filter
	 * @param condition the condition to filter
	 */
	public void setFilter(final String column, final Object[] values, final int condition) {
		if (this.t != null) {
			this.t.applyFilter(column, values, condition);
		}
	}

	/**
	 * Removes all the filters set to a table.
	 */
	public void resetFilter() {
		if (this.t != null) {
			this.t.resetFilter();
		}
	}

	public void clearQuickFilter() {
		if ((this.t != null) && (this.t.getQuickFilter() != null)) {
			this.t.setQuickFilterValue(null);
		}
		if ((this.multipleResultWindow != null) && (this.multipleResultWindow.multipleResultTable != null)
				&& (this.multipleResultWindow.multipleResultTable.getQuickFilter() != null)) {
			this.multipleResultWindow.multipleResultTable.setQuickFilterValue(null);
		}
	}

	/**
	 * Fills the table.
	 * <p>
	 *
	 * @throws Exception when exception occurs
	 */
	protected void populateTable() throws Exception {
		if (this.clearDetailTableQuickFilterWhenShow) {
			this.clearQuickFilter();
		}
		Map hData = this.dataCache;
		if (this.cacheTime != 0) {
			final long t = System.currentTimeMillis();
			final long timeSinceLastQuery = t - this.getLastCacheTime();
			if ((!this.dataCacheInitialized) || (timeSinceLastQuery > this.cacheTime)) {
				try {
					this.invalidateCache();
					hData = this.dataCache;
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
					this.dataCache = null;
					this.deleteData();
					return;
				}
			} else {
				// Checks whether cache is valid. In this case, we use cache
				// manager.
				if (this.parentkeyCache) {
					final EntityResult res = this.cacheManager.getDataCache(this.entityName, this.getAttributes(),
							this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences()));
					this.dataCache = EntityResultUtils.toMap(res);
					hData = this.dataCache;
				}
			}
		} else {
			final Map hKeysValues = this.replaceParentkeyByEquivalence(this.getParentkeyEquivalences());
			final EntityResult result = this.locator.getEntityReference(this.entityName).query(hKeysValues,
					this.t.getAttributeList(), this.locator.getSessionId());
			if (result.getCode() == EntityResult.OPERATION_WRONG) {
				if (this.parentForm != null) {
					ReferenceExtDataField.this.parentForm.message(result.getMessage(), Form.ERROR_MESSAGE);
				}
				return;
			} else {
				ConnectionManager.checkEntityResult(result, this.locator);
			}
			hData = EntityResultUtils.toMap(result);
		}
		// Now it shows the window and establishes the table value.
		this.t.setValue(hData);
	}

	/**
	 * Creates a data field that focus is not supported.
	 *
	 * @see #isFocusTraversable()
	 */
	@Override
	protected void createDataField() {
		this.dataField = new EJTextField() {

			{
				if (ReferenceExtDataField.this.descriptionQuery) {
					ReferenceExtDataField.this.queryDocumentListener = new QueryDocumentListener();
					this.getDocument().addDocumentListener(ReferenceExtDataField.this.queryDocumentListener);
				}
			}

			@Override
			public boolean isFocusTraversable() {
				return ReferenceExtDataField.this.descriptionQuery;
			}

			@Override
			public boolean isFocusable() {
				return super.isFocusable();
			}

			@Override
			public void paste() {
				if (!this.isEditable() && ReferenceExtDataField.this.codeField.isVisible()) {
					ReferenceExtDataField.this.codeField.paste();
				} else {
					super.paste();
				}
			}

			@Override
			public void setDocument(final Document doc) {
				final Document previousDocument = this.getDocument();
				try {
					if ((ReferenceExtDataField.this.innerListener != null) && (previousDocument != null)) {
						previousDocument.removeDocumentListener(ReferenceExtDataField.this.innerListener);
					}
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}
				try {
					if ((ReferenceExtDataField.this.queryDocumentListener != null) && (previousDocument != null)) {
						previousDocument.removeDocumentListener(ReferenceExtDataField.this.queryDocumentListener);
					}
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}
				super.setDocument(doc);

				try {
					ReferenceExtDataField.this.installInnerListener();
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}

				try {
					if ((ReferenceExtDataField.this.queryDocumentListener != null) && (doc != null)) {
						doc.addDocumentListener(ReferenceExtDataField.this.queryDocumentListener);
					}
				} catch (final Exception e) {
					ReferenceExtDataField.logger.trace(null, e);
				}
			}

			@Override
			public String getName() {
				return ReferenceExtDataField.REFERENCEEXT_NAME;
			};
		};

		this.installKeyboardActions();

		this.addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(final FocusEvent e) {
				super.focusLost(e);
				if (ReferenceExtDataField.this.queryPopup != null) {
					if (ReferenceExtDataField.this.queryPopup.isVisible()) {
						ReferenceExtDataField.this.queryPopup.setVisible(false);
					}
				}
			}
		});

		if (!ApplicationManager.useOntimizePlaf) {
			((JTextField) this.dataField).setDisabledTextColor(Color.darkGray);
			this.codeField.setDisabledTextColor(Color.darkGray);
		}
	}

	/**
	 * Inits the user and application preferences.
	 */
	@Override
	public void initPreferences(final ApplicationPreferences prefs, final String user) {
		super.initPreferences(prefs, user);
		if (this.t != null) {
			// t.setControlsVisible(false);
			this.t.initPreferences(prefs, user);
		}
		// since 5.2079EN-0.6
		// parameters like default visible cols are not loaded in multiple
		// result window.
		if ((this.multipleResultWindow != null) && (this.multipleResultWindow.multipleResultTable != null)) {
			this.multipleResultWindow.multipleResultTable.initPreferences(prefs, user);
		}
	}

	/**
	 * Creates the multiple result window.
	 * <p>
	 *
	 * @param t the table
	 */

	protected void createMultipleResultsWindow(final Table t) {
		if (this.multipleResultWindow == null) {
			this.multipleResultWindow = new MultipleResultWindow(t, this, this.resources);
		}
	}

	@Override
	protected void setInnerListenerEnabled(final boolean enabled) {
		super.setInnerListenerEnabled(enabled);
		this.codeFieldListener.setEnabled(enabled);
		if (this.queryDocumentListener != null) {
			((QueryDocumentListener) this.queryDocumentListener).setEnabled(enabled);
		}
	}

	/**
	 * Gets the reference to the table.
	 */
	public Table getTable() {
		return this.t;
	}

	/**
	 * Gets the reference to table with multiple results.
	 */
	public Table getMultipleResultTable() {
		return this.multipleResultWindow != null ? this.multipleResultWindow.multipleResultTable : null;
	}

	/**
	 * Returns the codeField.
	 * <p>
	 *
	 * @return a Jcomponent with code field.
	 */
	@Override
	public JTextField getCodeField() {
		return this.codeField;
	}

	/**
	 * Returns a string with preference key about dialog size.
	 *
	 * @deprecated
	 */
	@Deprecated
	public String getResultDialogSizePreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? ReferenceExtDataField.RESULTS_DIALOG_SIZE_POSITION + "_" + f.getArchiveName() + "_" + this.attribute
						: ReferenceExtDataField.RESULTS_DIALOG_SIZE_POSITION + "_" + this.attribute;
	}

	/**
	 * Returns a string with preference key about dialog search.
	 */
	public String getSearchDialogSizePreferenceKey() {
		final Form f = this.parentForm;
		return f != null
				? ReferenceExtDataField.SEARCH_DIALOG_SIZE_POSITION + "_" + f.getArchiveName() + "_" + this.attribute
						: ReferenceExtDataField.SEARCH_DIALOG_SIZE_POSITION + "_" + this.attribute;
	}

	/**
	 * Installs a pop-up menu listener.
	 * <p>
	 *
	 * @see MouseListener
	 */
	@Override
	protected void installPopupMenuListener() {
		final MouseListener ml = new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					ReferenceExtDataField.this.showPopupMenu((Component) e.getSource(), e.getX(), e.getY());
				}
			}
		};
		this.dataField.addMouseListener(ml);
		this.codeField.addMouseListener(ml);
	}

	/**
	 * Adds a help listener
	 */
	@Override
	protected void installPreferenceHelpListener() {
		if (this.dataField != null) {
			final MouseListener ml = new InfoMouseListener(this);

			this.dataField.addMouseListener(ml);
			this.codeField.addMouseListener(ml);
		}
	}

	/**
	 * Returns the condition expressed in parameter 'clearquickfilter'. When it is
	 * true, quickfilter field will be showed empty when result table is showed.
	 * When it is false, the last filter typed is maintained in quickfilter.
	 *
	 * @return condition about clear quickfilter
	 */
	public boolean isClearDetailTableQuickFilterWhenShow() {
		return this.clearDetailTableQuickFilterWhenShow;
	}

	@Override
	public List getParentKeyList() {
		return this.getParentKeys();
	}

	public JButton getDeleteButton() {
		return this.deleteButton;
	}

	public JButton getQueryButton() {
		return this.queryButton;
	}

	@Override
	public String getCodeSearchFieldName() {
		return this.codeQueryField;
	}

	@Override
	public boolean isCodeFieldVisible() {
		return this.visibleCodeField;
	}

	@Override
	public boolean isCodeSearchVisible() {
		return this.visibleCodeSearch;
	}

	@Override
	public int getTemplateDataType() {
		return ITemplateField.DATA_TYPE_FIELD;
	}

	@Override
	public Object getTemplateDataValue() {
		final Object value = this.getValue();
		if (value != null) {
			return this.getCodeDescription(value, this.dataCache);
		}
		return value;
	}

	protected class QueryDocumentListener implements DocumentListener {

		protected ExecuteQuery target = new ExecuteQuery();

		protected Timer timer;

		protected boolean enabled = true;

		@Override
		public void insertUpdate(final DocumentEvent e) {
			if (ReferenceExtDataField.this.dataField.hasFocus()) {
				this.executeQuery();
			}
		}

		@Override
		public void removeUpdate(final DocumentEvent e) {
			if (ReferenceExtDataField.this.dataField.hasFocus()) {
				this.executeQuery();
			}
		}

		@Override
		public void changedUpdate(final DocumentEvent e) {
			if (ReferenceExtDataField.this.dataField.hasFocus()) {
				this.executeQuery();
			}
		}

		protected void executeQuery() {
			if (this.enabled) {
				if (!ReferenceExtDataField.this.isEmpty()) {
					ReferenceExtDataField.this.deleteUserData(true, false);
				}

				if (this.timer == null) {
					this.timer = new Timer(0, this.target);
					this.timer.setInitialDelay(QuickFieldText.timeDelay);
				}

				if (this.timer.isRunning()) {
					this.timer.stop();
				}
				this.timer.start();
			}
		}

		public void setEnabled(final boolean enabled) {
			this.enabled = enabled;
		}

	}

	protected QueryPopup queryPopup;

	protected class ExecuteQuery implements ActionListener {

		@Override
		public void actionPerformed(final ActionEvent e) {
			((Timer) e.getSource()).stop();
			if (ReferenceExtDataField.this.queryPopup == null) {
				ReferenceExtDataField.this.queryPopup = new QueryPopup();
			}
			ReferenceExtDataField.this.queryPopup.executeQuery(
					((JTextField) ReferenceExtDataField.this.dataField).getText(), ReferenceExtDataField.this);
			// QueryPopup.showQuery((JComponent)ReferenceExtDataField.this.getDataField());
		}

	}

	protected void installKeyboardActions() {
		final ActionMap map = this.dataField.getActionMap();
		if (map != null) {
			map.put("selectPrevious", new UpAction());
			map.put("selectNext", new DownAction());
			map.put("selection", new SelectAction());
		}

		final InputMap input = this.dataField.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "selectNext");
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "selectPrevious");
		input.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "selection");
	}

	protected class SelectAction extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JTextField field = (JTextField) e.getSource();
			if (field.isEditable() && (ReferenceExtDataField.this.queryPopup != null)) {
				if (ReferenceExtDataField.this.queryPopup.isVisible()) {
					ReferenceExtDataField.this.queryPopup.selection();
				}
			}
		}

	}

	protected class DownAction extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JTextField field = (JTextField) e.getSource();
			if (field.isEditable() && (ReferenceExtDataField.this.queryPopup != null)) {
				if (ReferenceExtDataField.this.queryPopup.isVisible()) {
					ReferenceExtDataField.this.queryPopup.selectNextPossibleValue();
				}
			}
		}

	}

	protected class UpAction extends AbstractAction {

		@Override
		public void actionPerformed(final ActionEvent e) {
			final JTextField field = (JTextField) e.getSource();
			if (field.isEditable() && (ReferenceExtDataField.this.queryPopup != null)) {
				if (ReferenceExtDataField.this.queryPopup.isVisible()) {
					ReferenceExtDataField.this.queryPopup.selectPreviousPossibleValue();
				}
			}
		}

	}

	public static class QueryPopup extends JPopupMenu {

		/**
		 * This protected field is implementation specific. Do not access directly or
		 * override. Use the accessor methods instead.
		 *
		 * @see #getList
		 * @see #createList
		 */
		protected JList list;

		protected JPanel waitPanel;

		/**
		 * This protected field is implementation specific. Do not access directly or
		 * override. Use the create method instead
		 *
		 * @see #createScroller
		 */
		protected JScrollPane scroller;

		private static Border LIST_BORDER = new LineBorder(Color.BLACK, 1);

		protected QueryThread queryThread;

		public QueryPopup() {
			super();
			this.setName("ComboPopup.popup");
			this.list = this.createList();
			this.list.setName("ComboBox.list");
			// configureList();
			this.scroller = this.createScroller();
			this.scroller.setName("ComboBox.scrollPane");

			this.waitPanel = this.createWaitPanel();

			this.configureScroller();
			this.configurePopup();
			// installComboBoxListeners();

		}

		public ReferenceExtDataField getReferenceExtDataField(final Component invoker) {
			return (ReferenceExtDataField) SwingUtilities.getAncestorOfClass(ReferenceExtDataField.class, invoker);
		}

		@Override
		public void setInvoker(final Component invoker) {
			super.setInvoker(invoker);
			final Dimension d = this.waitPanel.getPreferredSize();
			d.width = invoker.getWidth();
			this.waitPanel.setPreferredSize(d);
		}

		@Override
		public Dimension getPreferredSize() {
			final Dimension dimension = super.getPreferredSize();
			if (dimension.width < this.getInvoker().getWidth()) {
				dimension.width = this.getInvoker().getWidth();
			}
			return dimension;
		}

		/**
		 * Implementation of ComboPopup.getList().
		 */
		public JList getList() {
			return this.list;
		}

		/**
		 * Creates the JList used in the popup to display the items in the combo box
		 * model. This method is called when the UI class is created.
		 *
		 * @return a <code>JList</code> used to display the combo box items
		 */
		protected JList createList() {
			final JList list = new JList(new QueryModel());
			list.addMouseListener(new MouseAdapter() {

				@Override
				public void mousePressed(final MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						final int index = list.locationToIndex(e.getPoint());
						if (index >= 0) {
							QueryPopup.this.selection(index);
						}
					}
				}
			});
			list.setVisibleRowCount(10);
			return list;
		}

		public void selectNextPossibleValue() {
			final JList list = this.getList();
			int selectedIndex = list.getSelectedIndex();
			if (selectedIndex < 0) {
				this.getList().setSelectedIndex(0);
				this.getList().ensureIndexIsVisible(0);
				return;
			}
			final int total = list.getModel().getSize();
			selectedIndex = selectedIndex + 1;
			if (total > selectedIndex) {
				this.getList().setSelectedIndex(selectedIndex);
				this.getList().ensureIndexIsVisible(selectedIndex);
			}
		}

		public void selectPreviousPossibleValue() {
			final JList list = this.getList();
			int selectedIndex = list.getSelectedIndex();
			if (selectedIndex < 0) {
				return;
			}

			selectedIndex = selectedIndex - 1;
			if (selectedIndex >= 0) {
				this.getList().setSelectedIndex(selectedIndex);
				this.getList().ensureIndexIsVisible(selectedIndex);
			}
		}

		public void selection() {
			final JList list = this.getList();
			final int selectedIndex = list.getSelectedIndex();
			this.selection(selectedIndex);
		}

		public void selection(final int index) {
			if (index >= 0) {
				final ResultItem item = (ResultItem) this.list.getModel().getElementAt(index);
				if (item != null) {
					final ReferenceExtDataField datafield = this.getReferenceExtDataField(this.getInvoker());
					try {
						((QueryDocumentListener) datafield.queryDocumentListener).setEnabled(false);
						datafield.setCode(item.getCode(), ValueEvent.USER_CHANGE);
						QueryPopup.this.setVisible(false);
					} finally {
						((QueryDocumentListener) datafield.queryDocumentListener).setEnabled(true);
					}
				}
			}
		}

		protected JPanel createWaitPanel() {
			final JPanel panel = new JPanel(new GridBagLayout());
			panel.add(new RotatedLabel(ImageManager.getIcon("loading.png")), new GridBagConstraints(0, 0, 1, 1, 0, 1,
					GridBagConstraints.CENTER, GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
			return panel;
		}

		/**
		 * Creates the scroll pane which houses the scrollable list.
		 */
		protected JScrollPane createScroller() {
			final JScrollPane sp = new JScrollPane(this.list, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			// sp.setHorizontalScrollBar(null);
			return sp;
		}

		/**
		 * Configures the scrollable portion which holds the list within the combo box
		 * popup. This method is called when the UI class is created.
		 */
		protected void configureScroller() {
			this.scroller.setFocusable(false);
			this.scroller.getVerticalScrollBar().setFocusable(false);
			this.scroller.setBorder(null);
		}

		/**
		 * Configures the popup portion of the combo box. This method is called when the
		 * UI class is created.
		 */
		protected void configurePopup() {
			this.setLayout(new GridBagLayout());
			this.setBorderPainted(true);

			if (ApplicationManager.useOntimizePlaf) {
				this.setBorder(new LineBorder(new Color(0x8ca0ad), 2));
			}

			this.setOpaque(false);
			this.add(this.waitPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.CENTER,
					GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			this.add(this.scroller, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			// setDoubleBuffered( true );
			this.setFocusable(false);
		}

		public void executeQuery(final String filterText, final ReferenceExtDataField invoker) {
			if ((filterText == null) || ((filterText.length() < 3) && !filterText.equalsIgnoreCase("*"))) {
				this.setVisible(false);
				if (this.queryThread != null) {
					this.queryThread.setFinish(true);
				}
				return;
			}

			this.waitPanel.setVisible(true);
			this.scroller.setVisible(false);
			QueryPopup.this.pack();

			if (this.queryThread != null) {
				this.queryThread.setFinish(true);
			}

			this.show(invoker.getDataField(), 0, invoker.getDataField().getHeight());
			this.queryThread = new QueryThread(invoker, filterText);
			this.queryThread.start();
		}

		protected class QueryThread extends Thread {

			protected ReferenceExtDataField dataField;

			protected String filter;

			protected boolean finish = false;

			public QueryThread(final ReferenceExtDataField field, final String filter) {
				super("ReferenceExt.QueryThread");
				this.dataField = field;
				this.filter = filter;
			}

			public boolean isFinish() {
				return this.finish;
			}

			public void setFinish(final boolean finish) {
				this.finish = finish;
			}

			@Override
			public void run() {
				Map dataCache = null;
				if (this.dataField.cacheTime > 0) {
					if (!this.dataField.isDataCacheInitialized()) {
						this.dataField.initCache();
					}
					dataCache = this.dataField.getDataCache();
				} else {
					dataCache = EntityResultUtils.toMap(this.dataField.queryByCode(null));
				}

				if (this.finish) {
					return;
				}

				final List result = this.processFilter(this.filter, dataCache);

				if (this.finish) {
					return;
				}
				QueryPopup.this.pack();
				((QueryModel) QueryPopup.this.list.getModel()).setData(result);
				QueryPopup.this.list.getSelectionModel().clearSelection();

				if (result.size() == 0) {
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							QueryPopup.this.waitPanel.setVisible(true);
							QueryPopup.this.scroller.setVisible(false);
							QueryPopup.this.pack();
							QueryPopup.this.setVisible(false);
						}
					});
					return;
				}
				if (result.size() > 10) {
					QueryPopup.this.list.setVisibleRowCount(10);
				} else {
					QueryPopup.this.list.setVisibleRowCount(result.size());
				}

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						QueryPopup.this.pack();
						QueryPopup.this.waitPanel.setVisible(false);
						QueryPopup.this.scroller.setVisible(true);
						QueryPopup.this.pack();
					}
				});
			}

			protected List processFilter(String filter, final Map dataCache) {
				final List result = new ArrayList();
				if ((dataCache == null) || dataCache.isEmpty()) {
					return result;
				}
				if ((filter != null) && (filter.length() > 0)) {
					if (!filter.startsWith("*")) {
						filter = "*" + filter;
					}
					if (!filter.endsWith("*")) {
						filter = filter + "*";
					}
				}

				filter = filter.replaceAll("[aáAÁ]", "[aáAÁ]");
				filter = filter.replaceAll("[eéEÉ]", "[eéEÉ]");
				filter = filter.replaceAll("[iíIÍ]", "[iíIÍ]");
				filter = filter.replaceAll("[oóOÓ]", "[oóOÓ]");
				filter = filter.replaceAll("[uúUÚ]", "[uúUÚ]");
				filter = filter.replaceAll("[ñÑ]", "[ñÑ]");

				filter = filter.replaceAll("\\*", ".*");
				filter = filter.replaceAll("\\+", "\\\\+");
				filter = filter.replaceAll("\\?", "\\\\?");
				filter = filter.replaceAll("\\(", "\\\\(");
				filter = filter.replaceAll("\\)", "\\\\)");
				filter = "(?i)" + filter;

				final Pattern pattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

				final String codeField = this.dataField.getCodeFieldName();
				final List codes = (List) dataCache.get(codeField);

				final Iterator valueCodes = codes.iterator();
				while (valueCodes.hasNext()) {
					final Object value = valueCodes.next();
					final String sDescription = this.dataField.getCodeDescription(value, dataCache);
					final String queryDescription = this.dataField.getQueryDescription(value, dataCache, sDescription);
					final Matcher matcher = pattern.matcher(queryDescription);
					if (matcher.matches()) {
						result.add(new ResultItem(sDescription, value));
					}
				}

				return result;
			}

		}

	}

	public static class ResultItem {

		protected String description;

		protected Object code;

		protected Map data;

		public ResultItem(final String description, final Object code) {
			this.code = code;
			this.description = description;
		}

		@Override
		public String toString() {
			return this.description;
		}

		public Object getCode() {
			return this.code;
		}

	}

	public static class QueryModel extends AbstractListModel {

		protected List data = new ArrayList();

		@Override
		public int getSize() {
			return this.data.size();
		}

		@Override
		public Object getElementAt(final int index) {
			return this.data.get(index);
		}

		public void setData(final List data) {
			this.data = data;
			this.fireContentsChanged(this, 0, this.data.size());
		}

	}

}
